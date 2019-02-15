/*
 * Copyright (c) 2019 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector.patrawin;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.wisetime.connector.api_client.ApiClient;
import io.wisetime.connector.api_client.PostResult;
import io.wisetime.connector.config.ConnectorConfigKey;
import io.wisetime.connector.config.RuntimeConfig;
import io.wisetime.connector.datastore.ConnectorStore;
import io.wisetime.connector.integrate.ConnectorModule;
import io.wisetime.connector.integrate.WiseTimeConnector;
import io.wisetime.connector.patrawin.ConnectorLauncher.PatrawinConnectorConfigKey;
import io.wisetime.connector.patrawin.model.Case;
import io.wisetime.connector.patrawin.model.Client;
import io.wisetime.connector.patrawin.model.ImmutableWorklog;
import io.wisetime.connector.patrawin.persistence.PatrawinDao;
import io.wisetime.connector.patrawin.persistence.SyncStore;
import io.wisetime.connector.patrawin.util.TimeDbFormatter;
import io.wisetime.connector.template.TemplateFormatter;
import io.wisetime.connector.template.TemplateFormatterConfig;
import io.wisetime.connector.utils.DurationCalculator;
import io.wisetime.connector.utils.DurationSource;
import io.wisetime.generated.connect.Tag;
import io.wisetime.generated.connect.TimeGroup;
import io.wisetime.generated.connect.TimeRow;
import io.wisetime.generated.connect.UpsertTagRequest;
import spark.Request;

import static io.wisetime.connector.utils.ActivityTimeCalculator.startTime;

/**
 * WiseTime Connector implementation for Patrawin
 *
 * @author shane.xie@practiceinsight.io
 * @author galya.bogdanova@m.practiceinsight.io
 */
public class PatrawinConnector implements WiseTimeConnector {

  private static final Logger log = LoggerFactory.getLogger(PatrawinConnector.class);
  private ApiClient apiClient;
  private SyncStore syncStore;
  private TemplateFormatter narrativeFormatter;
  private String defaultModifier;

  @Inject
  private PatrawinDao patrawinDao;
  @Inject
  private TimeDbFormatter timeDbFormatter;

  @Override
  public void init(ConnectorModule connectorModule) {
    Preconditions.checkArgument(patrawinDao.hasExpectedSchema(),
        "Patrawin database schema is unsupported by this connector");
    this.defaultModifier = RuntimeConfig.getString(PatrawinConnectorConfigKey.DEFAULT_MODIFIER)
        .orElseThrow(() -> new IllegalStateException("Required configuration param DEFAULT_MODIFIER is not set."));

    this.apiClient = connectorModule.getApiClient();
    this.syncStore = createSyncStore(connectorModule.getConnectorStore());
    this.narrativeFormatter = new TemplateFormatter(
        TemplateFormatterConfig.builder()
            .withTemplatePath("classpath:timegroup-narrative-template.ftl")
            .build()
    );
  }

  @VisibleForTesting
  SyncStore createSyncStore(ConnectorStore connectorStore) {
    return new SyncStore(connectorStore);
  }

  @Override
  public boolean isConnectorHealthy() {
    return patrawinDao.canQueryDb();
  }

  @Override
  public void performTagUpdate() {
    while (syncCases()) {
      // Drain all unsynced cases
    }
    while (syncClients()) {
      // Drain all unsynced clients
    }
  }

  @VisibleForTesting
  boolean syncCases() {
    final LocalDateTime lastPreviouslySyncedCaseCreationTime = syncStore.getLastSyncedCaseCreationTime();
    final List<String> lastPreviouslySyncedCaseNumbers = syncStore.getLastSyncedCaseNumbers();
    final List<Case> cases = patrawinDao.findCasesOrderedByCreationTime(
        lastPreviouslySyncedCaseCreationTime,
        lastPreviouslySyncedCaseNumbers,
        tagUpsertBatchSize());

    if (cases.isEmpty()) {
      log.info("No new case tags found. Last case number previously synced: {}", printLast(lastPreviouslySyncedCaseNumbers));
      return false;
    } else {
      try {
        log.info("Detected {} new {}: {}",
            cases.size(),
            cases.size() > 1 ? "cases" : "case",
            cases.stream().map(Case::getCaseNumber).collect(Collectors.joining(", ")));

        final List<UpsertTagRequest> upsertRequests = cases
            .stream()
            .map(i -> i.toUpsertTagRequest(tagUpsertPath()))
            .collect(Collectors.toList());

        apiClient.tagUpsertBatch(upsertRequests);

        syncStore.setLastSyncedCases(cases);
        log.info("Last synced case: {}", printLast(cases));
        return true;

      } catch (IOException e) {
        // The batch will be retried since we didn't update the last synced cases
        // Let scheduler know that this batch has failed
        throw new RuntimeException(e);
      }
    }
  }

  @VisibleForTesting
  boolean syncClients() {
    final LocalDateTime lastPreviouslySyncedClientCreationTime = syncStore.getLastSyncedClientCreationTime();
    final List<String> lastPreviouslySyncedClientIds = syncStore.getLastSyncedClientIds();
    final List<Client> clients = patrawinDao.findClientsOrderedByCreationTime(
        lastPreviouslySyncedClientCreationTime,
        lastPreviouslySyncedClientIds,
        tagUpsertBatchSize());

    if (clients.isEmpty()) {
      log.info("No new client tags found. Last client ID previously synced: {}", printLast(lastPreviouslySyncedClientIds));
      return false;
    } else {
      try {
        log.info("Detected {} new {}: {}",
            clients.size(),
            clients.size() > 1 ? "clients" : "client",
            clients.stream().map(Client::getClientId).collect(Collectors.joining(", ")));

        final List<UpsertTagRequest> upsertRequests = clients
            .stream()
            .map(i -> i.toUpsertTagRequest(tagUpsertPath()))
            .collect(Collectors.toList());

        apiClient.tagUpsertBatch(upsertRequests);

        syncStore.setLastSyncedClients(clients);
        log.info("Last synced client: {}", printLast(clients));
        return true;

      } catch (IOException e) {
        // The batch will be retried since we didn't update the last synced clients
        // Let scheduler know that this batch has failed
        throw new RuntimeException(e);
      }
    }
  }

  @Override
  public PostResult postTime(Request request, TimeGroup timeGroup) {
    log.info("Posted time received for {}: {}", timeGroup.getUser().getExternalId(), timeGroup.toString());

    final Optional<String> callerKey = RuntimeConfig.getString(ConnectorConfigKey.CALLER_KEY);
    if (callerKey.isPresent() && !callerKey.get().equals(timeGroup.getCallerKey())) {
      return PostResult.PERMANENT_FAILURE.withMessage("Invalid caller key in post time webhook call");
    }

    if (timeGroup.getTags().isEmpty()) {
      return PostResult.SUCCESS.withMessage("Time group has no tags. There is nothing to post to Patrawin.");
    }

    final Optional<LocalDateTime> activityStartTime = startTime(timeGroup);
    if (!activityStartTime.isPresent()) {
      return PostResult.PERMANENT_FAILURE.withMessage("Cannot post time group with no time rows");
    }

    final String authorUsernameOrEmail = StringUtils.isEmpty(timeGroup.getUser().getExternalId()) ?
        timeGroup.getUser().getEmail() :
        timeGroup.getUser().getExternalId();
    if (!patrawinDao.doesUserExist(authorUsernameOrEmail)) {
      return PostResult.PERMANENT_FAILURE.withMessage("User does not exist in Patrawin");
    }

    final Set<String> timeGroupModifiers = getTimeGroupModifiers(timeGroup);
    if (timeGroupModifiers.size() > 1) {
      return PostResult.PERMANENT_FAILURE.withMessage("Time group contains different activity codes " + timeGroupModifiers);
    }

    final String timeGroupModifier = timeGroupModifiers.iterator().next();
    final String modifier = StringUtils.isEmpty(timeGroupModifier) ? defaultModifier : timeGroupModifier;

    int activityCode;
    try {
      activityCode = Integer.parseInt(modifier);
    } catch (NumberFormatException e) {
      return PostResult.PERMANENT_FAILURE.withMessage("Time group has an invalid format of the activity code " + modifier);
    }

    if (!patrawinDao.doesActivityCodeExist(activityCode)) {
      return PostResult.PERMANENT_FAILURE.withMessage("Time group has an invalid activity code " + modifier);
    }

    final String narrative = narrativeFormatter.format(timeGroup);
    final int workedTimeSeconds = Math.round(DurationCalculator.of(timeGroup)
        .disregardExperienceRating()
        .useDurationFrom(DurationSource.SUM_TIME_ROWS)
        .calculate()
        .getPerTagDuration());
    final int chargeableTimeSeconds = Math.round(DurationCalculator.of(timeGroup)
        .useExperienceRating()
        .calculate()
        .getPerTagDuration());

    final OffsetDateTime activityStartTimeOffset = activityStartTime.get().atOffset(ZoneOffset.ofHours(0));

    final Function<String, String> createWorklog = caseOrClientId -> {
      final ImmutableWorklog worklog = ImmutableWorklog
          .builder()
          .caseOrClientId(caseOrClientId)
          .usernameOrEmail(authorUsernameOrEmail)
          .activityCode(activityCode)
          .narrative(narrative)
          .startTime(activityStartTimeOffset)
          .durationSeconds(workedTimeSeconds)
          .chargeableTimeSeconds(chargeableTimeSeconds)
          .build();

      patrawinDao.createWorklog(worklog);
      return caseOrClientId;
    };

    try {
      patrawinDao.asTransaction(() ->
          timeGroup.getTags()
              .stream()
              .map(findCaseOrClientId)
              .filter(Optional::isPresent)
              .map(Optional::get)
              .map(createWorklog)
              .forEach(caseOrClientId ->
                  log.info("Posted time to Patrawin case / client {} on behalf of {}", caseOrClientId, authorUsernameOrEmail)
              )
      );
    } catch (RuntimeException e) {
      log.error(e.getMessage(), e);
      return PostResult.TRANSIENT_FAILURE
          .withError(e)
          .withMessage("There was an error posting time to the Patrawin database");
    }
    return PostResult.SUCCESS;
  }

  private final Function<Tag, Optional<String>> findCaseOrClientId = tag -> {
    final String id = tag.getName();
    if (patrawinDao.doesCaseExist(id) || patrawinDao.doesClientExist(id)) {
      return Optional.of(id);
    }
    log.warn("Can't find Patrawin case or client for tag {}. No time will be posted for this tag.", tag.getName());
    return Optional.empty();
  };

  @VisibleForTesting
  Set<String> getTimeGroupModifiers(final TimeGroup timeGroup) {
    return timeGroup.getTimeRows().stream()
        .map(TimeRow::getModifier)
        .collect(Collectors.toSet());
  }

  private int tagUpsertBatchSize() {
    return RuntimeConfig
        .getInt(PatrawinConnectorConfigKey.TAG_UPSERT_BATCH_SIZE)
        // A large batch mitigates query round trip latency
        .orElse(200);
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
}
