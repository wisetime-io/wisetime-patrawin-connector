/*
 * Copyright (c) 2019 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector.patrawin;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import io.wisetime.connector.api_client.ApiClient;
import io.wisetime.connector.api_client.PostResult;
import io.wisetime.connector.config.RuntimeConfig;
import io.wisetime.connector.datastore.ConnectorStore;
import io.wisetime.connector.integrate.ConnectorModule;
import io.wisetime.connector.integrate.WiseTimeConnector;
import io.wisetime.generated.connect.TimeGroup;
import io.wisetime.generated.connect.UpsertTagRequest;
import spark.Request;

/**
 * WiseTime Connector implementation for Patrawin
 *
 * @author shane.xie@practiceinsight.io
 */
public class PatrawinConnector implements WiseTimeConnector {

  private static final Logger log = LoggerFactory.getLogger(PatrawinConnector.class);
  private ApiClient apiClient;
  private SyncStore syncStore;

  @Inject
  private PatrawinDao patrawinDao;

  @Override
  public void init(ConnectorModule connectorModule) {
    Preconditions.checkArgument(patrawinDao.hasExpectedSchema(),
        "Patrawin database schema is unsupported by this connector");

    this.apiClient = connectorModule.getApiClient();
    this.syncStore = createSyncStore(connectorModule.getConnectorStore());
  }

  @VisibleForTesting
  SyncStore createSyncStore(ConnectorStore connectorStore) {
    return new SyncStore(connectorStore);
  }

  @Override
  public void performTagUpdate() {
    while (syncCases()) {
      // Drain all unsynced cases
    }
    while (syncClients()) {
      // Drain all unscyned clients
    }
  }

  @Override
  public PostResult postTime(Request request, TimeGroup userPostedTime) {
    // TODO
    return null;
  }

  @Override
  public boolean isConnectorHealthy() {
    return patrawinDao.canQueryDb();
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

  private int tagUpsertBatchSize() {
    return RuntimeConfig
        .getInt(ConnectorLauncher.PatrawinConnectorConfigKey.TAG_UPSERT_BATCH_SIZE)
        // A large batch mitigates query round trip latency
        .orElse(200);
  }

  private String tagUpsertPath() {
    return RuntimeConfig
        .getString(ConnectorLauncher.PatrawinConnectorConfigKey.TAG_UPSERT_PATH)
        .orElse("/Patrawin/");
  }

  @VisibleForTesting
  static <T> String printLast(List<T> items) {
    if (items.size() == 0) {
      return "None yet";
    }
    return items.get(items.size() - 1).toString();
  }
}
