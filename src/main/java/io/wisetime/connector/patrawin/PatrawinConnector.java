/*
 * Copyright (c) 2019 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector.patrawin;

import static io.wisetime.connector.utils.ActivityTimeCalculator.startTime;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import io.wisetime.connector.ConnectorModule;
import io.wisetime.connector.WiseTimeConnector;
import io.wisetime.connector.api_client.ApiClient;
import io.wisetime.connector.api_client.PostResult;
import io.wisetime.connector.config.ConnectorConfigKey;
import io.wisetime.connector.config.RuntimeConfig;
import io.wisetime.connector.datastore.ConnectorStore;
import io.wisetime.connector.patrawin.ConnectorLauncher.PatrawinConnectorConfigKey;
import io.wisetime.connector.patrawin.model.BaseModel;
import io.wisetime.connector.patrawin.model.Case;
import io.wisetime.connector.patrawin.model.Client;
import io.wisetime.connector.patrawin.model.ImmutableWorklog;
import io.wisetime.connector.patrawin.persistence.PatrawinDao;
import io.wisetime.connector.patrawin.persistence.SyncStore;
import io.wisetime.connector.template.TemplateFormatter;
import io.wisetime.connector.template.TemplateFormatterConfig;
import io.wisetime.connector.utils.DurationCalculator;
import io.wisetime.connector.utils.DurationSource;
import io.wisetime.generated.connect.Tag;
import io.wisetime.generated.connect.TimeGroup;
import io.wisetime.generated.connect.TimeRow;
import io.wisetime.generated.connect.UpsertTagRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;

/**
 * WiseTime Connector implementation for Patrawin
 *
 * @author shane.xie@practiceinsight.io
 * @author galya.bogdanova@m.practiceinsight.io
 */
public class PatrawinConnector implements WiseTimeConnector {

  private static final Logger log = LoggerFactory.getLogger(PatrawinConnector.class);

  private Supplier<Integer> tagSyncIntervalMinutes;
  private ApiClient apiClient;
  private SyncStore syncStore;
  private TemplateFormatter narrativeFormatter;

  @Inject
  private PatrawinDao patrawinDao;

  @Override
  public void init(ConnectorModule connectorModule) {
    Preconditions.checkArgument(patrawinDao.hasExpectedSchema(),
        "Patrawin database schema is unsupported by this connector");

    tagSyncIntervalMinutes = connectorModule::getTagSyncIntervalMinutes;
    apiClient = connectorModule.getApiClient();
    syncStore = createSyncStore(connectorModule.getConnectorStore());
    // default to no summary
    if (RuntimeConfig.getBoolean(PatrawinConnectorConfigKey.ADD_SUMMARY_TO_NARRATIVE).orElse(false)) {
      narrativeFormatter = new TemplateFormatter(
          TemplateFormatterConfig.builder()
              .withTemplatePath("classpath:timegroup-narrative-template.ftl")
              .withWindowsClr(true)
              .build()
      );
    } else {
      narrativeFormatter = new TemplateFormatter(
          TemplateFormatterConfig.builder()
              .withTemplatePath("classpath:timegroup-narrative-template_no-summary.ftl")
              .withWindowsClr(true)
              .build()
      );
    }
  }

  @Override
  public String getConnectorType() {
    return "wisetime-patrawin-connector";
  }

  @VisibleForTesting
  SyncStore createSyncStore(ConnectorStore connectorStore) {
    return new SyncStore(connectorStore);
  }

  @Override
  public boolean isConnectorHealthy() {
    return patrawinDao.canQueryDb();
  }

  /**
   * Called by the WiseTime Connector library on a regular schedule.
   *
   * 1. Finds all Patrawin cases and clients that haven't been synced and creates matching tags for them in WiseTime.
   * Blocks until all cases and clients have been synced.
   *
   * 2. Sends a batch of already synced cases and clients to WiseTime to maintain freshness of existing tags. Mitigates
   * effect of renamed or missed tags. Only one refresh batch per performTagUpdate() call.
   */
  @Override
  public void performTagUpdate() {
    while (syncNewCases()) {
      // Drain all unsynced cases
    }
    while (syncNewClients()) {
      // Drain all unsynced clients
    }

    refreshCases(casesTagRefreshBatchSize());
    refreshClients(clientsTagRefreshBatchSize());
  }

  @VisibleForTesting
  boolean syncNewCases() {
    final Optional<LocalDateTime> lastPreviouslySyncedCaseCreationTime = syncStore.getLastSyncedCaseCreationTime();
    final List<String> lastPreviouslySyncedCaseNumbers = syncStore.getLastSyncedCaseNumbers();
    final List<Case> cases = patrawinDao.findCasesOrderedByCreationTime(
        lastPreviouslySyncedCaseCreationTime,
        lastPreviouslySyncedCaseNumbers,
        tagUpsertBatchSize());

    if (cases.isEmpty()) {
      log.info("No new case tags found. Last case number previously synced: {}",
          printLast(lastPreviouslySyncedCaseNumbers));
      return false;
    }

    log.info("Detected {} new {}: {}",
        cases.size(),
        cases.size() > 1 ? "cases" : "case",
        cases.stream().map(Case::getNumber).collect(Collectors.joining(", ")));

    upsertWiseTimeTags(cases);

    syncStore.setLastSyncedCases(cases);
    log.info("Last synced case: {}", printLast(cases));
    return true;
  }

  @VisibleForTesting
  boolean syncNewClients() {
    final Optional<LocalDateTime> lastPreviouslySyncedClientCreationTime = syncStore
        .getLastSyncedClientCreationTime();
    final List<String> lastPreviouslySyncedClientNumbers = syncStore.getLastSyncedClientNumbers();
    final List<Client> clients = patrawinDao.findClientsOrderedByCreationTime(
        lastPreviouslySyncedClientCreationTime,
        lastPreviouslySyncedClientNumbers,
        tagUpsertBatchSize());

    if (clients.isEmpty()) {
      log.info("No new client tags found. Last client ID previously synced: {}",
          printLast(lastPreviouslySyncedClientNumbers));
      return false;
    }

    log.info("Detected {} new {}: {}",
        clients.size(),
        clients.size() > 1 ? "clients" : "client",
        clients.stream().map(Client::getNumber).collect(Collectors.joining(", ")));

    upsertWiseTimeTags(clients);

    syncStore.setLastSyncedClients(clients);
    log.info("Last synced client: {}", printLast(clients));
    return true;
  }

  @VisibleForTesting
  void refreshCases(final int batchSize) {
    final Optional<LocalDateTime> lastPreviouslyRefreshedCaseCreationTime = syncStore
        .getLastRefreshedCaseCreationTime();
    final List<String> lastPreviouslyRefreshedCaseNumbers = syncStore.getLastRefreshedCaseNumbers();
    final List<Case> cases = patrawinDao.findCasesOrderedByCreationTime(
        lastPreviouslyRefreshedCaseCreationTime,
        lastPreviouslyRefreshedCaseNumbers,
        batchSize);

    if (cases.isEmpty()) {
      // Start over the next time we are called
      syncStore.clearLastRefreshedCases();
      return;
    }

    log.info("Refreshing {} {}: {}",
        cases.size(),
        cases.size() > 1 ? "cases" : "case",
        cases.stream().map(Case::getNumber).collect(Collectors.joining(", ")));

    upsertWiseTimeTags(cases);

    syncStore.setLastRefreshedCases(cases);
    log.info("Last refreshed case: {}", printLast(cases));
  }

  @VisibleForTesting
  void refreshClients(final int batchSize) {
    final Optional<LocalDateTime> lastPreviouslyRefreshedClientCreationTime = syncStore
        .getLastRefreshedClientCreationTime();
    final List<String> lastPreviouslyRefreshedClientNumbers = syncStore.getLastRefreshedClientNumbers();
    final List<Client> clients = patrawinDao.findClientsOrderedByCreationTime(
        lastPreviouslyRefreshedClientCreationTime,
        lastPreviouslyRefreshedClientNumbers,
        batchSize);

    if (clients.isEmpty()) {
      // Start over the next time we are called
      syncStore.clearLastRefreshedClients();
      return;
    }

    log.info("Refreshing {} {}: {}",
        clients.size(),
        clients.size() > 1 ? "clients" : "client",
        clients.stream().map(Client::getNumber).collect(Collectors.joining(", ")));

    upsertWiseTimeTags(clients);

    syncStore.setLastRefreshedClients(clients);
    log.info("Last refreshed client: {}", printLast(clients));
  }

  private void upsertWiseTimeTags(final List<? extends BaseModel> models) {
    try {
      final List<UpsertTagRequest> upsertRequests = models
          .stream()
          .map(i -> i.toUpsertTagRequest(tagUpsertPath()))
          .collect(Collectors.toList());
      apiClient.tagUpsertBatch(upsertRequests);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public PostResult postTime(Request request, TimeGroup timeGroup) {
    log.info("Posted time received: {}", timeGroup.getGroupId());

    final Optional<String> callerKey = RuntimeConfig.getString(ConnectorConfigKey.CALLER_KEY);
    if (callerKey.isPresent() && !callerKey.get().equals(timeGroup.getCallerKey())) {
      return PostResult.PERMANENT_FAILURE().withMessage("Invalid caller key in post time webhook call");
    }

    if (timeGroup.getTags().isEmpty()) {
      return PostResult.SUCCESS().withMessage("Time group has no tags. There is nothing to post to Patrawin.");
    }

    final Optional<LocalDateTime> activityStartTime = startTime(timeGroup);
    if (!activityStartTime.isPresent()) {
      return PostResult.PERMANENT_FAILURE().withMessage("Cannot post time group with no time rows");
    }

    final String authorUsernameOrEmail = StringUtils.isEmpty(timeGroup.getUser().getExternalId()) ?
        timeGroup.getUser().getEmail() :
        timeGroup.getUser().getExternalId();
    // The Patrawin post time stored procedure also performs this validation
    if (!patrawinDao.doesUserExist(authorUsernameOrEmail)) {
      return PostResult.PERMANENT_FAILURE().withMessage("User does not exist in Patrawin");
    }

    final Optional<Integer> activityCode = getTimeGroupActivityCode(timeGroup);
    if (!activityCode.isPresent()) {
      return PostResult.PERMANENT_FAILURE().withMessage("Time group has an invalid activity code");
    }

    final String narrative = narrativeFormatter.format(timeGroup);
    final int workedTimeSeconds = Math.round(DurationCalculator.of(timeGroup)
        .disregardExperienceWeighting()
        .useDurationFrom(DurationSource.SUM_TIME_ROWS)
        .calculate()
        .getPerTagDuration());
    final int chargeableTimeSeconds = Math.round(DurationCalculator.of(timeGroup)
        .useExperienceWeighting()
        .calculate()
        .getPerTagDuration());

    final Function<String, String> createWorklog = caseOrClientNumber -> {
      final ImmutableWorklog worklog = ImmutableWorklog
          .builder()
          .caseOrClientNumber(caseOrClientNumber)
          .usernameOrEmail(authorUsernameOrEmail)
          .activityCode(activityCode.get())
          .narrative(narrative)
          .startTime(OffsetDateTime.of(activityStartTime.get(), ZoneOffset.UTC))
          .durationSeconds(workedTimeSeconds)
          .chargeableTimeSeconds(chargeableTimeSeconds)
          .build();

      patrawinDao.createWorklog(worklog);
      return caseOrClientNumber;
    };

    try {
      patrawinDao.asTransaction(() ->
          timeGroup.getTags()
              .stream()
              .map(findCaseOrClientNumber)
              .filter(Optional::isPresent)
              .map(Optional::get)
              .map(createWorklog)
              .forEach(caseOrClientNumber ->
                  log.info("Posted time {} to Patrawin case / client {}",
                      timeGroup.getGroupId(), caseOrClientNumber)
              )
      );
    } catch (CaseNotFoundException e) {
      log.warn("Can't post time to the Patrawin database: " + e.getMessage());
      return PostResult.PERMANENT_FAILURE()
          .withError(e)
          .withMessage(e.getMessage());
    } catch (IllegalStateException ex) {
      // Thrown if Patrawin has rejected the posted time
      return PostResult.PERMANENT_FAILURE()
          .withError(ex)
          .withMessage(ex.getMessage());
    } catch (RuntimeException e) {
      return PostResult.TRANSIENT_FAILURE()
          .withError(e)
          .withMessage("There was an error posting time to the Patrawin database");
    }
    return PostResult.SUCCESS();
  }

  /**
   * Determines if the tag is an existing case or client in Patrawin. Note that even a case or client is existing, the
   * stored procedure `pw_PostTime` for posting time has additional checks if posting time to this case or client can
   * proceed.
   */
  private final Function<Tag, Optional<String>> findCaseOrClientNumber = tag -> {
    if (!createdByConnector(tag)) {
      log.warn("The Patrawin connector is not configured to handle this tag: {}. No time will be posted for this tag.",
          tag.getName());
      return Optional.empty();
    }
    final String id = tag.getName();
    if (patrawinDao.doesCaseExist(id) || patrawinDao.doesClientExist(id)) {
      return Optional.of(id);
    }
    throw new CaseNotFoundException("Can't find Patrawin case for tag " + tag.getName());
  };

  @VisibleForTesting
  Set<String> getTimeGroupActivityCodes(final TimeGroup timeGroup) {
    return timeGroup.getTimeRows().stream()
        .map(TimeRow::getActivityTypeCode)
        .collect(Collectors.toSet());
  }

  private int tagUpsertBatchSize() {
    return RuntimeConfig
        .getInt(PatrawinConnectorConfigKey.TAG_UPSERT_BATCH_SIZE)
        // A large batch mitigates query round trip latency
        .orElse(200);
  }

  @VisibleForTesting
  int casesTagRefreshBatchSize() {
    return tagRefreshBatchSize(patrawinDao.casesCount());
  }

  @VisibleForTesting
  int clientsTagRefreshBatchSize() {
    return tagRefreshBatchSize(patrawinDao.clientsCount());
  }

  private int tagRefreshBatchSize(long tagCount) {
    final long batchFullFortnightlyRefresh = tagCount / (TimeUnit.DAYS.toMinutes(14) / tagSyncIntervalMinutes.get());

    if (batchFullFortnightlyRefresh > tagUpsertBatchSize()) {
      return tagUpsertBatchSize();
    }
    final int minimumBatchSize = 10;
    if (batchFullFortnightlyRefresh < minimumBatchSize) {
      return minimumBatchSize;
    }
    return (int) batchFullFortnightlyRefresh;
  }

  private String tagUpsertPath() {
    return RuntimeConfig
        .getString(PatrawinConnectorConfigKey.TAG_UPSERT_PATH)
        .orElse("/Patrawin/");
  }

  @VisibleForTesting
  static <T> String printLast(final List<T> items) {
    if (items.size() == 0) {
      return "None yet";
    }
    return items.get(items.size() - 1).toString();
  }

  private Optional<Integer> getTimeGroupActivityCode(final TimeGroup timeGroup) {
    final Set<String> activityCodes = getTimeGroupActivityCodes(timeGroup);
    if (activityCodes.size() > 1) {
      log.error("All time logs within time group should have same activity type, but got: {}", activityCodes);
      return Optional.empty();
    }
    if (activityCodes.isEmpty()) {
      log.warn("Activity type is not set for time group {}", timeGroup.getGroupId());
      return Optional.empty();
    }

    final String activityType = activityCodes.iterator().next();

    final int activityCode;
    try {
      activityCode = Integer.parseInt(activityType);
    } catch (NumberFormatException e) {
      log.error("Time group {} has an invalid format of the activity code {}", timeGroup.getGroupId(), activityType);
      return Optional.empty();
    }

    // The Patrawin post time stored procedure also performs this validation
    if (patrawinDao.doesActivityCodeExist(activityCode)) {
      return Optional.of(activityCode);
    }

    return Optional.empty();
  }

  @Override
  public void shutdown() {
    patrawinDao.shutdown();
  }

  private boolean createdByConnector(Tag tag) {
    return tag.getPath().equals(tagUpsertPath() + tag.getName()) ||
        tag.getPath().equals(StringUtils.strip(tagUpsertPath(), "/"));
  }

  private static class CaseNotFoundException extends RuntimeException {
    CaseNotFoundException(String message) {
      super(message);
    }
  }
}
