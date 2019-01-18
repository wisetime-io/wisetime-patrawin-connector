/*
 * Copyright (c) 2019 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector.patrawin;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import io.wisetime.connector.datastore.ConnectorStore;

/**
 * Persist and read references for last synced case and client records.
 *
 * @author shane.xie@practiceinsight.io
 */
public class SyncStore {

  private static final String LAST_SYNCED_CASE_CREATION_TIME_KEY = "last-synced-case-creation-time";
  private static final String LAST_SYNCED_CASE_NUMBERS_KEY = "last-synced-case-numbers-csv";
  private static final String LAST_SYNCED_CLIENT_CREATION_TIME_KEY = "last-synced-client-creation-time";
  private static final String LAST_SYNCED_CLIENT_IDS_KEY = "last-synced-client-ids-csv";

  private ConnectorStore connectorStore;

  public SyncStore(final ConnectorStore connectorStore) {
    this.connectorStore = connectorStore;
  }

  /**
   * @return the creation time of the last case that was synced
   */
  Instant getLastSyncedCaseCreationTime() {
    return connectorStore
        .getLong(LAST_SYNCED_CASE_CREATION_TIME_KEY)
        .map(Instant::ofEpochMilli)
        .orElse(Instant.EPOCH);
  }

  /**
   * @return comma separated case numbers of the last batch of cases that were synced
   */
  Optional<String> getLastSyncedCaseNumbersCsv() {
    return connectorStore.getString(LAST_SYNCED_CASE_NUMBERS_KEY);
  }

  /**
   * @return the creation time of the last client that was synced
   */
  Instant getLastSyncedClientCreationTime() {
    return connectorStore
        .getLong(LAST_SYNCED_CLIENT_CREATION_TIME_KEY)
        .map(Instant::ofEpochMilli)
        .orElse(Instant.EPOCH);
  }

  /**
   * @return comma separated client ids of the last batch of clients that were synced
   */
  Optional<String> getLastSyncedClientIdsCsv() {
    return connectorStore.getString(LAST_SYNCED_CLIENT_IDS_KEY);
  }

  /**
   * Remember the last synced case references
   * @param lastSyncedCases List of cases, with most recently created last
   */
  void setLastSyncedCases(final List<Case> lastSyncedCases) {
    connectorStore.putLong(LAST_SYNCED_CASE_CREATION_TIME_KEY,
        lastSyncedCases.get(lastSyncedCases.size() - 1).getCreationTime().toEpochMilli());
    connectorStore.putString(LAST_SYNCED_CASE_NUMBERS_KEY,
        lastSyncedCases.stream().map(Case::getCaseNumber).collect(Collectors.joining(",")));
  }

  /**
   * Remember the last synced client references
   * @param lastSyncedClients List of clients, with the most recently created last
   */
  void setLastSyncedClients(final List<Client> lastSyncedClients) {
    connectorStore.putLong(LAST_SYNCED_CLIENT_CREATION_TIME_KEY,
        lastSyncedClients.get(lastSyncedClients.size() - 1).getCreationTime().toEpochMilli());
    connectorStore.putString(LAST_SYNCED_CLIENT_IDS_KEY,
        lastSyncedClients.stream().map(Client::getClientId).collect(Collectors.joining(",")));
  }
}
