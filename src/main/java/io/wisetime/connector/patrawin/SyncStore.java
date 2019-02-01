/*
 * Copyright (c) 2019 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector.patrawin;

import com.google.common.collect.ImmutableList;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import io.wisetime.connector.datastore.ConnectorStore;

/**
 * Persist and read references for printLast synced case and client records.
 *
 * @author shane.xie@practiceinsight.io
 */
public class SyncStore {

  private static final String LAST_SYNCED_CASE_CREATION_TIME_KEY = "printLast-synced-case-creation-time";
  private static final String LAST_SYNCED_CASE_NUMBERS_KEY = "printLast-synced-case-numbers-csv";
  private static final String LAST_SYNCED_CLIENT_CREATION_TIME_KEY = "printLast-synced-client-creation-time";
  private static final String LAST_SYNCED_CLIENT_IDS_KEY = "printLast-synced-client-ids-csv";
  private static final String DELIMITER = "@@";

  private ConnectorStore connectorStore;

  public SyncStore(final ConnectorStore connectorStore) {
    this.connectorStore = connectorStore;
  }

  /**
   * @return the creation time of the printLast case that was synced
   */
  LocalDateTime getLastSyncedCaseCreationTime() {
    return connectorStore
        .getString(LAST_SYNCED_CASE_CREATION_TIME_KEY)
        .map(LocalDateTime::parse)
        .orElse(LocalDateTime.MIN);
  }

  /**
   * @return comma separated case numbers of the printLast batch of cases that were synced
   */
  List<String> getLastSyncedCaseNumbers() {
    return connectorStore.getString(LAST_SYNCED_CASE_NUMBERS_KEY)
        .map(n -> n.split(DELIMITER))
        .map(Arrays::asList)
        .orElse(ImmutableList.of());
  }

  /**
   * @return the creation time of the printLast client that was synced
   */
  LocalDateTime getLastSyncedClientCreationTime() {
    return connectorStore
        .getString(LAST_SYNCED_CLIENT_CREATION_TIME_KEY)
        .map(LocalDateTime::parse)
        .orElse(LocalDateTime.MIN);
  }

  /**
   * @return comma separated client ids of the printLast batch of clients that were synced
   */
  List<String> getLastSyncedClientIds() {
    return connectorStore.getString(LAST_SYNCED_CLIENT_IDS_KEY)
        .map(i -> i.split(DELIMITER))
        .map(Arrays::asList)
        .orElse(ImmutableList.of());
  }

  /**
   * Remember the printLast synced case references
   * @param lastSyncedCases List of cases, with most recently created printLast
   */
  void setLastSyncedCases(final List<Case> lastSyncedCases) {
    connectorStore.putString(LAST_SYNCED_CASE_CREATION_TIME_KEY,
        lastSyncedCases.get(lastSyncedCases.size() - 1).getCreationTime().toString());
    connectorStore.putString(LAST_SYNCED_CASE_NUMBERS_KEY,
        lastSyncedCases.stream().map(Case::getCaseNumber).collect(Collectors.joining(DELIMITER)));
  }

  /**
   * Remember the printLast synced client references
   * @param lastSyncedClients List of clients, with the most recently created printLast
   */
  void setLastSyncedClients(final List<Client> lastSyncedClients) {
    connectorStore.putString(LAST_SYNCED_CLIENT_CREATION_TIME_KEY,
        lastSyncedClients.get(lastSyncedClients.size() - 1).getCreationTime().toString());
    connectorStore.putString(LAST_SYNCED_CLIENT_IDS_KEY,
        lastSyncedClients.stream().map(Client::getClientId).collect(Collectors.joining(DELIMITER)));
  }
}
