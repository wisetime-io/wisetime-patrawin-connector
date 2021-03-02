/*
 * Copyright (c) 2019 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector.patrawin;

import static io.wisetime.connector.utils.ActivityTimeCalculator.startTime;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import io.wisetime.connector.ConnectorModule;
import io.wisetime.connector.WiseTimeConnector;
import io.wisetime.connector.api_client.ApiClient;
import io.wisetime.connector.api_client.PostResult;
import io.wisetime.connector.config.RuntimeConfig;
import io.wisetime.connector.datastore.ConnectorStore;
import io.wisetime.connector.patrawin.ConnectorLauncher.PatrawinConnectorConfigKey;
import io.wisetime.connector.patrawin.alert.AlertEmailScheduler;
import io.wisetime.connector.patrawin.model.Case;
import io.wisetime.connector.patrawin.model.Client;
import io.wisetime.connector.patrawin.model.TagRequestConvert;
import io.wisetime.connector.patrawin.model.Worklog;
import io.wisetime.connector.patrawin.persistence.PatrawinDao;
import io.wisetime.connector.patrawin.persistence.PatrawinDao.ActivityTypeLabel;
import io.wisetime.connector.patrawin.persistence.SyncStore;
import io.wisetime.connector.template.TemplateFormatter;
import io.wisetime.connector.template.TemplateFormatterConfig;
import io.wisetime.connector.utils.DurationCalculator;
import io.wisetime.connector.utils.DurationSource;
import io.wisetime.generated.connect.ActivityType;
import io.wisetime.generated.connect.DeleteTagRequest;
import io.wisetime.generated.connect.SyncActivityTypesRequest;
import io.wisetime.generated.connect.SyncSession;
import io.wisetime.generated.connect.Tag;
import io.wisetime.generated.connect.TimeGroup;
import io.wisetime.generated.connect.TimeRow;
import io.wisetime.generated.connect.UpsertTagRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
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
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  private Supplier<Integer> tagSyncIntervalMinutes;
  private ApiClient apiClient;
  private SyncStore syncStore;
  private TemplateFormatter narrativeFormatter;

  @Inject
  private PatrawinDao patrawinDao;

  @Inject
  private AlertEmailScheduler alertEmailScheduler;

  @Inject
  private HashFunction hashFunction;

  @Override
  public void init(ConnectorModule connectorModule) {
    Preconditions.checkArgument(patrawinDao.hasExpectedSchema(),
        "Patrawin database schema is unsupported by this connector");

    tagSyncIntervalMinutes = connectorModule::getTagSlowLoopIntervalMinutes;
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
    // Schedule unprocessed posted times check and email alert
    alertEmailScheduler.init();
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
   * <p>
   * Finds all Patrawin cases and clients that haven't been synced and creates matching tags for them in WiseTime.
   * Blocks until all cases and clients have been synced.
   */
  @Override
  public void performTagUpdate() {
    while (syncNewCases()) {
      // Drain all unsynced cases
    }
    while (syncNewClients()) {
      // Drain all unsynced clients
    }
  }

  /**
   * Sends a batch of already synced cases and clients to WiseTime to maintain freshness of existing tags. Mitigates
   * effect of renamed or missed tags.
   */
  @Override
  public void performTagUpdateSlowLoop() {
    refreshCases(casesTagRefreshBatchSize());
    refreshClients(clientsTagRefreshBatchSize());
  }

  @Override
  public void performActivityTypeUpdate() {
    syncActivityTypes();
  }

  @VisibleForTesting
  void syncActivityTypes() {
    final List<String> hashes = new ArrayList<>();
    final int activitiesCount =
        iterateAllActivityTypes(activities -> hashes.add(hashFunction.hashActivities(activities)));
    final String currentHash = hashFunction.hashStrings(hashes);

    final String prevSyncedHash = syncStore.getLastActivityTypeLabelsHashKey().orElse(StringUtils.EMPTY);
    final long lastSync = syncStore.getLastActivityTypeLabelsSyncKey().orElse(0L);
    final boolean syncedMoreThanDayAgo = System.currentTimeMillis() - lastSync > TimeUnit.DAYS.toMillis(1);

    log.info("Sync Activity types: {} Activity type found with hash: '{}'. Previously synced at {} with hash: '{}'",
        activitiesCount, currentHash, lastSync, prevSyncedHash);

    if (!currentHash.equals(prevSyncedHash) || syncedMoreThanDayAgo) {
      final String syncSessionId = startSyncSession();
      iterateAllActivityTypes(activities -> sendActivityTypesToSync(mapToActivityTypes(activities), syncSessionId));
      completeSyncSession(syncSessionId);
      syncStore.setLastActivityTypeLabelsHashKey(currentHash);
      syncStore.setLastActivityTypeLabelsSyncKey(System.currentTimeMillis());
      log.info("Activity Type Labels synced successfully finished with session '{}'", syncSessionId);
    } else {
      log.info("No need to sync activity types");
    }
  }

  private void sendActivityTypesToSync(List<ActivityType> activityTypes, String sessionId) {
    final SyncActivityTypesRequest request = new SyncActivityTypesRequest()
        .activityTypes(activityTypes)
        .syncSessionId(sessionId);
    try {
      apiClient.syncActivityTypes(request);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private String startSyncSession() {
    try {
      return apiClient.activityTypesStartSyncSession().getSyncSessionId();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void completeSyncSession(String syncSessionId) {
    try {
      apiClient.activityTypesCompleteSyncSession(new SyncSession().syncSessionId(syncSessionId));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private int iterateAllActivityTypes(Consumer<List<ActivityTypeLabel>> consumer) {
    final int batchSize = RuntimeConfig.getInt(PatrawinConnectorConfigKey.ACTIVITY_TYPE_BATCH_SIZE).orElse(500);
    int offset = 0;
    int counter = 0;
    while (true) {
      final List<ActivityTypeLabel> activities = patrawinDao.findActivityTypeLabels(offset, batchSize);
      if (activities.size() > 0) {
        consumer.accept(activities);
        counter += activities.size();
      }
      if (activities.size() < batchSize) {
        return counter;
      }
      offset += batchSize;
    }
  }

  private List<ActivityType> mapToActivityTypes(List<ActivityTypeLabel> activityTypeLabels) {
    return activityTypeLabels.stream()
        .map(this::mapToActivityType)
        .collect(Collectors.toList());
  }

  private ActivityType mapToActivityType(ActivityTypeLabel activityTypeLabel) {
    String label = activityTypeLabel.getLabel();
    if (StringUtils.isBlank(label)) {
      log.warn("Activity type label with code {} doesn't have a description. Fallback to using code instead.",
          activityTypeLabel.getId());
      label = activityTypeLabel.getId();
    }
    return new ActivityType()
        .code(activityTypeLabel.getId())
        .label(label)
        .description("");
  }

  @VisibleForTesting
  boolean syncNewCases() {
    final Optional<LocalDateTime> lastPreviouslySyncedCaseCreationTime = syncStore.getLastSyncedCaseCreationTime();
    final Optional<String> lastPreviouslySyncedCaseNumber = syncStore.getLastSyncedCaseNumber();
    final List<Case> cases = patrawinDao.findCasesOrderedByCreationTime(
        lastPreviouslySyncedCaseCreationTime,
        lastPreviouslySyncedCaseNumber,
        tagUpsertBatchSize());

    if (cases.isEmpty()) {
      log.info("No new case tags found. Last case number previously synced: {}",
          lastPreviouslySyncedCaseNumber.orElse("None"));
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
    final Optional<String> lastPreviouslySyncedClientNumber = syncStore.getLastSyncedClientNumber();
    final List<Client> clients = patrawinDao.findClientsOrderedByCreationTime(
        lastPreviouslySyncedClientCreationTime,
        lastPreviouslySyncedClientNumber,
        tagUpsertBatchSize());

    if (clients.isEmpty()) {
      log.info("No new client tags found. Last client ID previously synced: {}",
          lastPreviouslySyncedClientNumber.orElse("None"));
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
    final Optional<String> lastPreviouslyRefreshedCaseNumber = syncStore.getLastRefreshedCaseNumbers();
    final List<Case> cases = patrawinDao.findCasesOrderedByCreationTime(
        lastPreviouslyRefreshedCaseCreationTime,
        lastPreviouslyRefreshedCaseNumber,
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
    final Optional<String> lastPreviouslyRefreshedClientNumber = syncStore.getLastRefreshedClientNumber();
    final List<Client> clients = patrawinDao.findClientsOrderedByCreationTime(
        lastPreviouslyRefreshedClientCreationTime,
        lastPreviouslyRefreshedClientNumber,
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

  private void upsertWiseTimeTags(final List<? extends TagRequestConvert> models) {
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

  /**
   * Determines if the tag is an existing case or client in Patrawin. Note that even a case or client is existing, the
   * stored procedure `pw_PostTime` for posting time has additional checks if posting time to this case or client can
   * proceed.
   */
  private boolean hasCaseOrClientNumber(Tag tag) {
    final String id = tag.getName();
    return patrawinDao.doesCaseExist(id) || patrawinDao.doesClientExist(id);
  }

  @Override
  public PostResult postTime(Request request, TimeGroup timeGroup) {
    log.info("Posted time received: {}", timeGroup.getGroupId());

    List<Tag> relevantTags = getRelevantTags(timeGroup);
    timeGroup.setTags(relevantTags);

    if (timeGroup.getTags().isEmpty()) {
      return PostResult.SUCCESS().withMessage("Time group has no tags. There is nothing to post to Patrawin.");
    }

    final Optional<LocalDateTime> activityStartTime = startTime(timeGroup);
    if (!activityStartTime.isPresent()) {
      return PostResult.PERMANENT_FAILURE().withMessage("Cannot post time group with no time rows");
    }

    final String authorUsernameOrEmail = StringUtils.isEmpty(timeGroup.getUser().getExternalId())
        ? timeGroup.getUser().getEmail() :
        timeGroup.getUser().getExternalId();
    // The Patrawin post time stored procedure also performs this validation
    if (!patrawinDao.doesUserExist(authorUsernameOrEmail)) {
      return PostResult.PERMANENT_FAILURE().withMessage("User does not exist in Patrawin");
    }

    final Optional<Integer> activityCode = getTimeGroupActivityCode(timeGroup);
    if (!activityCode.isPresent()) {
      return PostResult.PERMANENT_FAILURE().withMessage("Time group has an invalid activity code");
    }

    final String narrative = narrativeFormatter.format(convertToZone(timeGroup, getTimeZoneId()));
    final int workedTimeSeconds = Math.round(DurationCalculator.of(timeGroup)
        .disregardExperienceWeighting()
        .useDurationFrom(DurationSource.SUM_TIME_ROWS)
        .calculate());
    final int chargeableTimeSeconds = Math.round(DurationCalculator.of(timeGroup)
        .useExperienceWeighting()
        .calculate());

    final Function<String, String> createWorklog = caseOrClientNumber -> {
      final Worklog worklog = new Worklog()
          .setCaseOrClientNumber(caseOrClientNumber)
          .setUsernameOrEmail(authorUsernameOrEmail)
          .setActivityCode(activityCode.get())
          .setNarrative(narrative)
          .setStartTime(OffsetDateTime.of(activityStartTime.get(), ZoneOffset.UTC))
          .setDurationSeconds(workedTimeSeconds)
          .setChargeableTimeSeconds(chargeableTimeSeconds);

      patrawinDao.createWorklog(worklog);
      return caseOrClientNumber;
    };

    try {
      List<Tag> tagsMissingInPatrawin = new ArrayList<>();
      List<String> tagToPostTo = timeGroup.getTags()
          .stream()
          .filter(this::createdByConnector)
          .map(tag -> {
            if (hasCaseOrClientNumber(tag)) {
              return Optional.of(tag.getName());
            }
            tagsMissingInPatrawin.add(tag);
            return Optional.<String>empty();
          })
          .filter(Optional::isPresent)
          .map(Optional::get)
          .collect(Collectors.toList());

      handleMissingTags(tagsMissingInPatrawin);
      patrawinDao.asTransaction(() -> tagToPostTo.stream().map(createWorklog)
          .forEach(caseOrClientNumber ->
              log.info("Posted time {} to Patrawin case / client {}", timeGroup.getGroupId(), caseOrClientNumber)));
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

  private List<Tag> getRelevantTags(TimeGroup timeGroup) {
    return timeGroup.getTags().stream()
        .filter(tag -> {
          if (!createdByConnector(tag)) {
            log.warn(
                "The Patrawin connector is not configured to handle this tag: {}. No time will be posted for this tag.",
                tag.getName());
            return false;
          }
          return true;
        })
        .collect(Collectors.toList());
  }

  private void handleMissingTags(List<Tag> tagsMissingInPatrawin) {
    if (!tagsMissingInPatrawin.isEmpty()) {
      log.warn("Couldn't find all cases in Patrawin");
      tagsMissingInPatrawin.forEach(tag -> {
        try {
          // the tag will be deleted, but the user still needs to manually repost and existing time rows need
          // to be fixed
          apiClient.tagDelete(new DeleteTagRequest().name(tag.getName()));
        } catch (IOException e) {
          log.error("Error deleting tag: {}", tag.toString(), e);
          // connect-api-server down: Throw general exception to retry
          throw new RuntimeException(e);
        }
      });
      throw new CaseNotFoundException("Patrawin case was not found for next tags: "
          + tagsMissingInPatrawin.stream()
          .map(Tag::getName)
          .collect(Collectors.joining(", ")));
    }
  }

  /**
   * Determines if the tag is an existing case or client in Patrawin. Note that even a case or client is existing, the
   * stored procedure `pw_PostTime` for posting time has additional checks if posting time to this case or client can
   * proceed.
   */
  private final Function<Tag, Optional<String>> findCaseOrClientNumber = tag -> {
    final String id = tag.getName();
    if (patrawinDao.doesCaseExist(id) || patrawinDao.doesClientExist(id)) {
      return Optional.of(id);
    }
    return Optional.empty();
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
    alertEmailScheduler.shutdown();
    patrawinDao.shutdown();
  }

  private boolean createdByConnector(Tag tag) {
    return tag.getPath().equals(tagUpsertPath())
        || tag.getPath().equals(StringUtils.strip(tagUpsertPath(), "/"));
  }

  private ZoneId getTimeZoneId() {
    return ZoneId.of(RuntimeConfig.getString(PatrawinConnectorConfigKey.TIMEZONE).orElse("UTC"));
  }

  @VisibleForTesting
  TimeGroup convertToZone(TimeGroup timeGroupUtc, ZoneId zoneId) {
    try {
      final String timeGroupUtcJson = OBJECT_MAPPER.writeValueAsString(timeGroupUtc);
      final TimeGroup timeGroupCopy = OBJECT_MAPPER.readValue(timeGroupUtcJson, TimeGroup.class);
      timeGroupCopy.getTimeRows()
          .forEach(tr -> convertToZone(tr, zoneId));
      return timeGroupCopy;
    } catch (IOException ex) {
      throw new RuntimeException("Failed to convert TimeGroup to zone " + zoneId, ex);
    }
  }

  private void convertToZone(TimeRow timeRowUtc, ZoneId zoneId) {
    final Pair<Integer, Integer> activityTimePair = convertToZone(
        timeRowUtc.getActivityHour(), timeRowUtc.getFirstObservedInHour(), zoneId
    );

    timeRowUtc
        .activityHour(activityTimePair.getLeft())
        .firstObservedInHour(activityTimePair.getRight())
        .setSubmittedDate(convertToZone(timeRowUtc.getSubmittedDate(), zoneId));
  }

  /**
   * Returns a Pair of "activity hour" (left value) and "first observed in hour" (right value) converted in the
   * specified zone ID.
   */
  private Pair<Integer, Integer> convertToZone(int activityHourUtc, int firstObservedInHourUtc, ZoneId toZoneId) {
    final DateTimeFormatter activityTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
    final String activityTimeUTC =
        activityHourUtc + StringUtils.leftPad(String.valueOf(firstObservedInHourUtc), 2, "0");
    final String activityTimeConverted = ZonedDateTime
        .of(LocalDateTime.parse(activityTimeUTC, activityTimeFormatter), ZoneOffset.UTC)
        .withZoneSameInstant(toZoneId)
        .format(activityTimeFormatter);

    return Pair.of(
        Integer.parseInt(activityTimeConverted.substring(0, 10)), // activityHour in 'yyyyMMddHH' format
        Integer.parseInt(activityTimeConverted.substring(10))     // firstObservedInHour in 'mm' format
    );
  }

  private Long convertToZone(long submittedDateUtc, ZoneId toZoneId) {
    DateTimeFormatter submittedDateFormatter = new DateTimeFormatterBuilder()
        .appendPattern("yyyyMMddHHmmss")
        .appendValue(ChronoField.MILLI_OF_SECOND, 3)
        .toFormatter();
    final String submittedDateConverted = ZonedDateTime
        .of(LocalDateTime.parse(String.valueOf(submittedDateUtc), submittedDateFormatter), ZoneOffset.UTC)
        .withZoneSameInstant(toZoneId)
        .format(submittedDateFormatter);

    return Long.parseLong(submittedDateConverted);
  }

  private static class CaseNotFoundException extends RuntimeException {

    CaseNotFoundException(String message) {
      super(message);
    }
  }
}
