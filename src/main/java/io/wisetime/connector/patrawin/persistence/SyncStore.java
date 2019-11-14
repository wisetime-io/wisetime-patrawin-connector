/*
 * Copyright (c) 2019 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector.patrawin.persistence;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import io.wisetime.connector.datastore.ConnectorStore;
import io.wisetime.connector.patrawin.model.BaseModel;
import io.wisetime.connector.patrawin.model.Case;
import io.wisetime.connector.patrawin.model.Client;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Persist and read references for printLast synced/refreshed case and client records.
 *
 * @author shane.xie@practiceinsight.io
 * @author galya.bogdanova@m.practiceinsight.io
 */
public class SyncStore {

  private static final String LAST_SYNCED_CASE_CREATION_TIME_KEY = "printLast-synced-case-creation-time";
  private static final String LAST_SYNCED_CASE_NUMBERS_KEY = "printLast-synced-case-numbers-csv";
  private static final String LAST_SYNCED_CLIENT_CREATION_TIME_KEY = "printLast-synced-client-creation-time";
  private static final String LAST_SYNCED_CLIENT_NUMBERS_KEY = "printLast-synced-client-numbers-csv";

  private static final String LAST_REFRESHED_CASE_CREATION_TIME_KEY = "printLast-refreshed-case-creation-time";
  private static final String LAST_REFRESHED_CASE_NUMBERS_KEY = "printLast-refreshed-case-numbers-csv";
  private static final String LAST_REFRESHED_CLIENT_CREATION_TIME_KEY = "printLast-refreshed-client-creation-time";
  private static final String LAST_REFRESHED_CLIENT_NUMBERS_KEY = "printLast-refreshed-client-numbers-csv";

  private static final String DELIMITER = "@@";

  private ConnectorStore connectorStore;

  public SyncStore(final ConnectorStore connectorStore) {
    this.connectorStore = connectorStore;
  }

  /**
   * @return the creation time of the printLast case that was synced
   */
  public Optional<LocalDateTime> getLastSyncedCaseCreationTime() {
    return getLastCreationTime(LAST_SYNCED_CASE_CREATION_TIME_KEY);
  }

  /**
   * @return the creation time of the printLast case that was refreshed
   */
  public Optional<LocalDateTime> getLastRefreshedCaseCreationTime() {
    return getLastCreationTime(LAST_REFRESHED_CASE_CREATION_TIME_KEY);
  }

  /**
   * @return the creation time of the printLast client that was synced
   */
  public Optional<LocalDateTime> getLastSyncedClientCreationTime() {
    return getLastCreationTime(LAST_SYNCED_CLIENT_CREATION_TIME_KEY);
  }

  /**
   * @return the creation time of the printLast client that was refreshed
   */
  public Optional<LocalDateTime> getLastRefreshedClientCreationTime() {
    return getLastCreationTime(LAST_REFRESHED_CLIENT_CREATION_TIME_KEY);
  }

  private Optional<LocalDateTime> getLastCreationTime(String creationTimeKey) {
    return connectorStore
        .getString(creationTimeKey)
        .filter(StringUtils::isNotEmpty)
        .map(LocalDateTime::parse);
  }

  /**
   * @return comma separated case numbers of the printLast batch of cases that were synced
   */
  public List<String> getLastSyncedCaseNumbers() {
    return getLastModelNumbers(LAST_SYNCED_CASE_NUMBERS_KEY);
  }

  /**
   * @return comma separated case numbers of the printLast batch of cases that were refreshed
   */
  public List<String> getLastRefreshedCaseNumbers() {
    return getLastModelNumbers(LAST_REFRESHED_CASE_NUMBERS_KEY);
  }

  /**
   * @return comma separated client ids of the printLast batch of clients that were synced
   */
  public List<String> getLastSyncedClientNumbers() {
    return getLastModelNumbers(LAST_SYNCED_CLIENT_NUMBERS_KEY);
  }

  /**
   * @return comma separated client ids of the printLast batch of clients that were refreshed
   */
  public List<String> getLastRefreshedClientNumbers() {
    return getLastModelNumbers(LAST_REFRESHED_CLIENT_NUMBERS_KEY);
  }

  private List<String> getLastModelNumbers(String numbersKey) {
    return connectorStore.getString(numbersKey)
        .filter(StringUtils::isNotEmpty)
        .map(i -> i.split(DELIMITER))
        .map(Arrays::asList)
        .orElse(ImmutableList.of());
  }

  /**
   * Remember the printLast synced case references
   *
   * @param lastSyncedCases List of cases, with most recently created printLast. Can't be empty.
   */
  public void setLastSyncedCases(final List<Case> lastSyncedCases) {
    setLastModels(lastSyncedCases, LAST_SYNCED_CASE_CREATION_TIME_KEY, LAST_SYNCED_CASE_NUMBERS_KEY);
  }

  /**
   * Remember the printLast refreshed case references
   *
   * @param lastRefreshedCases List of cases, with most recently created printLast. Can't be empty.
   */
  public void setLastRefreshedCases(final List<Case> lastRefreshedCases) {
    setLastModels(lastRefreshedCases, LAST_REFRESHED_CASE_CREATION_TIME_KEY, LAST_REFRESHED_CASE_NUMBERS_KEY);
  }

  /**
   * Remember the printLast synced client references
   *
   * @param lastSyncedClients List of clients, with the most recently created printLast. Can't be empty.
   */
  public void setLastSyncedClients(final List<Client> lastSyncedClients) {
    setLastModels(lastSyncedClients, LAST_SYNCED_CLIENT_CREATION_TIME_KEY, LAST_SYNCED_CLIENT_NUMBERS_KEY);
  }

  /**
   * Remember the printLast refreshed client references
   *
   * @param lastRefreshedClients List of clients, with the most recently created printLast. Can't be empty.
   */
  public void setLastRefreshedClients(final List<Client> lastRefreshedClients) {
    setLastModels(lastRefreshedClients, LAST_REFRESHED_CLIENT_CREATION_TIME_KEY, LAST_REFRESHED_CLIENT_NUMBERS_KEY);
  }

  private void setLastModels(final List<? extends BaseModel> models, String creationTimeKey, String numberKey) {
    Preconditions.checkArgument(CollectionUtils.isNotEmpty(models), "Models list shouldn't be empty");
    LocalDateTime lastCreationTime = models.get(models.size() - 1).getCreationTime();
    connectorStore.putString(creationTimeKey, lastCreationTime.toString());
    connectorStore.putString(numberKey, models.stream()
        .filter(lastSyncedClient -> lastSyncedClient.getCreationTime().equals(lastCreationTime))
        .map(BaseModel::getNumber)
        .collect(Collectors.joining(DELIMITER)));
  }

  /**
   * Clear previously remembered printLast refreshed case references
   */
  public void clearLastRefreshedCases() {
    clearLastModels(LAST_REFRESHED_CASE_CREATION_TIME_KEY, LAST_REFRESHED_CASE_NUMBERS_KEY);
  }

  /**
   * Clear previously remembered printLast refreshed client references
   */
  public void clearLastRefreshedClients() {
    clearLastModels(LAST_REFRESHED_CLIENT_CREATION_TIME_KEY, LAST_REFRESHED_CLIENT_NUMBERS_KEY);
  }

  private void clearLastModels(String creationTimeKey, String numberKey) {
    connectorStore.putString(creationTimeKey, StringUtils.EMPTY);
    connectorStore.putString(numberKey, StringUtils.EMPTY);
  }
}
