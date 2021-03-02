/*
 * Copyright (c) 2019 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector.patrawin.persistence;

import com.google.common.base.Preconditions;
import io.wisetime.connector.datastore.ConnectorStore;
import io.wisetime.connector.patrawin.model.BaseModel;
import io.wisetime.connector.patrawin.model.Case;
import io.wisetime.connector.patrawin.model.Client;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
  private static final String LAST_SYNCED_CASE_NUMBER_KEY = "printLast-synced-case-number";
  private static final String LAST_SYNCED_CLIENT_CREATION_TIME_KEY = "printLast-synced-client-creation-time";
  private static final String LAST_SYNCED_CLIENT_NUMBER_KEY = "printLast-synced-client-number";

  private static final String LAST_REFRESHED_CASE_CREATION_TIME_KEY = "printLast-refreshed-case-creation-time";
  private static final String LAST_REFRESHED_CASE_NUMBER_KEY = "printLast-refreshed-case-number";
  private static final String LAST_REFRESHED_CLIENT_CREATION_TIME_KEY = "printLast-refreshed-client-creation-time";
  private static final String LAST_REFRESHED_CLIENT_NUMBER_KEY = "printLast-refreshed-client-number";
  public static final String ACTIVITY_TYPE_LABELS_HASH_KEY = "activity-type-labels-hash-key";
  public static final String LAST_ACTIVITY_TYPE_LABELS_SYNC_KEY = "last-activity-type-labels-sync-key";

  private ConnectorStore connectorStore;

  public SyncStore(final ConnectorStore connectorStore) {
    this.connectorStore = connectorStore;
  }

  public Optional<Long> getLastActivityTypeLabelsSyncKey() {
    return connectorStore.getLong(LAST_ACTIVITY_TYPE_LABELS_SYNC_KEY);
  }

  public Optional<String> getLastActivityTypeLabelsHashKey() {
    return connectorStore.getString(ACTIVITY_TYPE_LABELS_HASH_KEY);
  }

  public void setLastActivityTypeLabelsSyncKey(Long key) {
    connectorStore.putLong(LAST_ACTIVITY_TYPE_LABELS_SYNC_KEY, key);
  }

  public void setLastActivityTypeLabelsHashKey(String hashKey) {
    connectorStore.putString(ACTIVITY_TYPE_LABELS_HASH_KEY, hashKey);
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
   * @return last synced case number
   */
  public Optional<String> getLastSyncedCaseNumber() {
    return getLastModelNumbers(LAST_SYNCED_CASE_NUMBER_KEY);
  }

  /**
   * @return last refreshed case number
   */
  public Optional<String> getLastRefreshedCaseNumbers() {
    return getLastModelNumbers(LAST_REFRESHED_CASE_NUMBER_KEY);
  }

  /**
   * @return comma separated client ids of the printLast batch of clients that were synced
   */
  public Optional<String> getLastSyncedClientNumber() {
    return getLastModelNumbers(LAST_SYNCED_CLIENT_NUMBER_KEY);
  }

  /**
   * @return comma separated client ids of the printLast batch of clients that were refreshed
   */
  public Optional<String> getLastRefreshedClientNumber() {
    return getLastModelNumbers(LAST_REFRESHED_CLIENT_NUMBER_KEY);
  }

  private Optional<String> getLastModelNumbers(String numbersKey) {
    return connectorStore.getString(numbersKey)
        .filter(StringUtils::isNotEmpty);
  }

  /**
   * Remember the printLast synced case references
   *
   * @param lastSyncedCases List of cases, with most recently created printLast. Can't be empty.
   */
  public void setLastSyncedCases(final List<Case> lastSyncedCases) {
    setLastModels(lastSyncedCases, LAST_SYNCED_CASE_CREATION_TIME_KEY, LAST_SYNCED_CASE_NUMBER_KEY);
  }

  /**
   * Remember the printLast refreshed case references
   *
   * @param lastRefreshedCases List of cases, with most recently created printLast. Can't be empty.
   */
  public void setLastRefreshedCases(final List<Case> lastRefreshedCases) {
    setLastModels(lastRefreshedCases, LAST_REFRESHED_CASE_CREATION_TIME_KEY, LAST_REFRESHED_CASE_NUMBER_KEY);
  }

  /**
   * Remember the printLast synced client references
   *
   * @param lastSyncedClients List of clients, with the most recently created printLast. Can't be empty.
   */
  public void setLastSyncedClients(final List<Client> lastSyncedClients) {
    setLastModels(lastSyncedClients, LAST_SYNCED_CLIENT_CREATION_TIME_KEY, LAST_SYNCED_CLIENT_NUMBER_KEY);
  }

  /**
   * Remember the printLast refreshed client references
   *
   * @param lastRefreshedClients List of clients, with the most recently created printLast. Can't be empty.
   */
  public void setLastRefreshedClients(final List<Client> lastRefreshedClients) {
    setLastModels(lastRefreshedClients, LAST_REFRESHED_CLIENT_CREATION_TIME_KEY, LAST_REFRESHED_CLIENT_NUMBER_KEY);
  }

  private void setLastModels(final List<? extends BaseModel> models, String creationTimeKey, String numberKey) {
    Preconditions.checkArgument(CollectionUtils.isNotEmpty(models), "Models list shouldn't be empty");
    LocalDateTime lastCreationTime = models.get(models.size() - 1).getCreationTime();
    connectorStore.putString(creationTimeKey, lastCreationTime.toString());
    String lastId = models.get(models.size() - 1).getNumber();
    connectorStore.putString(numberKey, lastId);
  }

  /**
   * Clear previously remembered printLast refreshed case references
   */
  public void clearLastRefreshedCases() {
    clearLastModels(LAST_REFRESHED_CASE_CREATION_TIME_KEY, LAST_REFRESHED_CASE_NUMBER_KEY);
  }

  /**
   * Clear previously remembered printLast refreshed client references
   */
  public void clearLastRefreshedClients() {
    clearLastModels(LAST_REFRESHED_CLIENT_CREATION_TIME_KEY, LAST_REFRESHED_CLIENT_NUMBER_KEY);
  }

  private void clearLastModels(String creationTimeKey, String numberKey) {
    connectorStore.putString(creationTimeKey, StringUtils.EMPTY);
    connectorStore.putString(numberKey, StringUtils.EMPTY);
  }
}
