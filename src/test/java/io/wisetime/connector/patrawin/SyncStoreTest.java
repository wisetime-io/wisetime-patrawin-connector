/*
 * Copyright (c) 2019 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector.patrawin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.testcontainers.shaded.com.google.common.collect.ImmutableList;

import java.time.Instant;
import java.util.Optional;

import io.wisetime.connector.datastore.ConnectorStore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author shane.xie@practiceinsight.io
 */
class SyncStoreTest {

  private static ConnectorStore connectorStore = mock(ConnectorStore.class);
  private static SyncStore syncStore = new SyncStore(connectorStore);

  @BeforeEach
  void setUpTest() {
    reset(connectorStore);
  }

  // Cases:

  @Test
  void getLastSyncedCaseCreationTime_none() {
    when(connectorStore.getLong("last-synced-case-creation-time")).thenReturn(Optional.empty());
    assertThat(syncStore.getLastSyncedCaseCreationTime())
        .isEqualTo(Instant.EPOCH)
        .as("No cases have been synced yet");
  }

  @Test
  void getLastSyncedCaseCreationTime_some() {
    final Instant now = Instant.now();
    when(connectorStore.getLong("last-synced-case-creation-time")).thenReturn(Optional.of(now.toEpochMilli()));
    assertThat(syncStore.getLastSyncedCaseCreationTime())
        .isEqualTo(now)
        .as("We should get back the correct Instant value");
  }

  @Test
  void getLastSyncedCaseNumbersCsv_none() {
    when(connectorStore.getString("last-synced-case-numbers-csv")).thenReturn(Optional.empty());
    assertThat(syncStore.getLastSyncedCaseNumbersCsv())
        .isEmpty()
        .as("No cases have been synced yet");
  }

  @Test
  void getLastSyncedCaseNumbersCsv_some() {
    when(connectorStore.getString("last-synced-case-numbers-csv")).thenReturn(Optional.of("1,2,3"));
    assertThat(syncStore.getLastSyncedCaseNumbersCsv())
        .isEqualTo(Optional.of("1,2,3"))
        .as("Some cases have been synced");
  }

  @Test
  void setLastSyncedCases() {
    final Instant later = Instant.now();
    final Instant earlier = later.minusSeconds(60);

    syncStore.setLastSyncedCases(ImmutableList.of(
        ImmutableCase.builder()
            .caseNumber("1")
            .description("")
            .creationTime(earlier)
            .build(),
        ImmutableCase.builder()
            .caseNumber("2")
            .description("")
            .creationTime(later)
            .build()
    ));

    ArgumentCaptor<String> caseNumbersKeyCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> caseNumbersCsvCaptor = ArgumentCaptor.forClass(String.class);
    verify(connectorStore, times(1)).putString(
        caseNumbersKeyCaptor.capture(), caseNumbersCsvCaptor.capture());
    assertThat(caseNumbersKeyCaptor.getValue()).isEqualTo("last-synced-case-numbers-csv");
    assertThat(caseNumbersCsvCaptor.getValue()).isEqualTo("1,2");

    ArgumentCaptor<String> caseCreationKeyCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<Long> creationTimeCaptor = ArgumentCaptor.forClass(Long.class);
    verify(connectorStore, times(1)).putLong(
        caseCreationKeyCaptor.capture(), creationTimeCaptor.capture());
    assertThat(caseCreationKeyCaptor.getValue()).isEqualTo("last-synced-case-creation-time");
    assertThat(creationTimeCaptor.getValue()).isEqualTo(later.toEpochMilli());
  }

  // Clients:

  @Test
  void getLastSyncedClientCreationTime_none() {
    when(connectorStore.getLong("last-synced-client-creation-time")).thenReturn(Optional.empty());
    assertThat(syncStore.getLastSyncedClientCreationTime())
        .isEqualTo(Instant.EPOCH)
        .as("No clients have been synced yet");
  }

  @Test
  void getLastSyncedClientCreationTime_some() {
    final Instant now = Instant.now();
    when(connectorStore.getLong("last-synced-case-creation-time")).thenReturn(Optional.of(now.toEpochMilli()));
    assertThat(syncStore.getLastSyncedCaseCreationTime())
        .isEqualTo(now)
        .as("We should get back the correct Instant value");
  }

  @Test
  void getLastSyncedClientIdsCsv_none() {
    when(connectorStore.getString("last-synced-client-ids-csv")).thenReturn(Optional.empty());
    assertThat(syncStore.getLastSyncedClientIdsCsv())
        .isEmpty()
        .as("No clients have been synced yet");
  }

  @Test
  void getLastSyncedClientIdsCsv_some() {
    when(connectorStore.getString("last-synced-client-ids-csv")).thenReturn(Optional.of("1,2,3"));
    assertThat(syncStore.getLastSyncedClientIdsCsv())
        .isEqualTo(Optional.of("1,2,3"))
        .as("Some clients have been synced");
  }

  @Test
  void setLastSyncedClients() {
    final Instant later = Instant.now();
    final Instant earlier = later.minusSeconds(60);

    syncStore.setLastSyncedClients(ImmutableList.of(
        ImmutableClient.builder()
            .clientId("1")
            .alias("")
            .creationTime(earlier)
            .build(),
        ImmutableClient.builder()
            .clientId("2")
            .alias("")
            .creationTime(later)
            .build()
    ));

    ArgumentCaptor<String> clientIdsKeyCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> clientIdsCsvCaptor = ArgumentCaptor.forClass(String.class);
    verify(connectorStore, times(1)).putString(
        clientIdsKeyCaptor.capture(), clientIdsCsvCaptor.capture());
    assertThat(clientIdsKeyCaptor.getValue()).isEqualTo("last-synced-client-ids-csv");
    assertThat(clientIdsCsvCaptor.getValue()).isEqualTo("1,2");

    ArgumentCaptor<String> clientCreationKeyCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<Long> creationTimeCaptor = ArgumentCaptor.forClass(Long.class);
    verify(connectorStore, times(1)).putLong(
        clientCreationKeyCaptor.capture(), creationTimeCaptor.capture());
    assertThat(clientCreationKeyCaptor.getValue()).isEqualTo("last-synced-client-creation-time");
    assertThat(creationTimeCaptor.getValue()).isEqualTo(later.toEpochMilli());
  }
}