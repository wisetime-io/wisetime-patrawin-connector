/*
 * Copyright (c) 2019 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector.patrawin.persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.testcontainers.shaded.com.google.common.collect.ImmutableList;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import io.wisetime.connector.datastore.ConnectorStore;
import io.wisetime.connector.patrawin.model.ImmutableCase;
import io.wisetime.connector.patrawin.model.ImmutableClient;

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
    when(connectorStore.getString("printLast-synced-case-creation-time")).thenReturn(Optional.empty());
    assertThat(syncStore.getLastSyncedCaseCreationTime())
        .as("Should use the min date supported my MS SQL datetime when no cases have been synced yet")
        .isEqualTo(LocalDateTime.of(1753, 1, 1, 0, 0));
  }

  @Test
  void getLastSyncedCaseCreationTime_some() {
    final LocalDateTime now = LocalDateTime.now();
    when(connectorStore.getString("printLast-synced-case-creation-time")).thenReturn(Optional.of(now.toString()));
    assertThat(syncStore.getLastSyncedCaseCreationTime())
        .as("We should get back the correct LocalDateTime value")
        .isEqualTo(now);
  }

  @Test
  void getLastSyncedCaseNumbers_none() {
    when(connectorStore.getString("printLast-synced-case-numbers-csv")).thenReturn(Optional.empty());
    assertThat(syncStore.getLastSyncedCaseNumbers())
        .as("No cases have been synced yet")
        .hasSize(0);
  }

  @Test
  void getLastSyncedCaseNumber_some() {
    when(connectorStore.getString("printLast-synced-case-numbers-csv")).thenReturn(Optional.of("1@@2@@3"));
    assertThat(syncStore.getLastSyncedCaseNumbers())
        .as("Some cases have been synced")
        .containsExactly("1", "2", "3");
  }

  @Test
  void setLastSyncedCases() {
    final LocalDateTime later = LocalDateTime.now();
    final LocalDateTime earlier = later.minusSeconds(60);

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
            .build(),
        ImmutableCase.builder()
            .caseNumber("3")
            .description("")
            .creationTime(later)
            .build()
    ));

    ArgumentCaptor<String> keysCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> valuesCaptor = ArgumentCaptor.forClass(String.class);

    verify(connectorStore, times(2)).putString(keysCaptor.capture(), valuesCaptor.capture());

    List<String> keys = keysCaptor.getAllValues();
    List<String> values = valuesCaptor.getAllValues();

    assertThat(keys.get(0)).isEqualTo("printLast-synced-case-creation-time");
    assertThat(values.get(0)).isEqualTo(later.toString());
    assertThat(keys.get(1)).isEqualTo("printLast-synced-case-numbers-csv");
    assertThat(values.get(1)).isEqualTo("2@@3");
  }

  // Clients:

  @Test
  void getLastSyncedClientCreationTime_none() {
    when(connectorStore.getString("printLast-synced-client-creation-time")).thenReturn(Optional.empty());
    assertThat(syncStore.getLastSyncedClientCreationTime())
        .as("Should use the min date supported my MS SQL datetime when no clients have been synced yet")
        .isEqualTo(LocalDateTime.of(1753, 1, 1, 0, 0));
  }

  @Test
  void getLastSyncedClientCreationTime_some() {
    final LocalDateTime now = LocalDateTime.now();
    when(connectorStore.getString("printLast-synced-case-creation-time")).thenReturn(Optional.of(now.toString()));
    assertThat(syncStore.getLastSyncedCaseCreationTime())
        .as("We should get back the correct LocalDateTime value")
        .isEqualTo(now);
  }

  @Test
  void getLastSyncedClientNumbers_none() {
    when(connectorStore.getString("printLast-synced-client-numbers-csv")).thenReturn(Optional.empty());
    assertThat(syncStore.getLastSyncedClientNumbers())
        .as("No clients have been synced yet")
        .hasSize(0);
  }

  @Test
  void getLastSyncedClientNumbers_some() {
    when(connectorStore.getString("printLast-synced-client-numbers-csv")).thenReturn(Optional.of("1@@2@@3"));
    assertThat(syncStore.getLastSyncedClientNumbers())
        .as("Some clients have been synced")
        .containsExactly("1", "2", "3");
  }

  @Test
  void setLastSyncedClients() {
    final LocalDateTime later = LocalDateTime.now();
    final LocalDateTime earlier = later.minusSeconds(60);

    syncStore.setLastSyncedClients(ImmutableList.of(
        ImmutableClient.builder()
            .clientNumber("1")
            .alias("")
            .creationTime(earlier)
            .build(),
        ImmutableClient.builder()
            .clientNumber("3")
            .alias("")
            .creationTime(earlier)
            .build(),
        ImmutableClient.builder()
            .clientNumber("2")
            .alias("")
            .creationTime(later)
            .build()
    ));

    ArgumentCaptor<String> keysCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> valuesCaptor = ArgumentCaptor.forClass(String.class);

    verify(connectorStore, times(2)).putString(keysCaptor.capture(), valuesCaptor.capture());

    List<String> keys = keysCaptor.getAllValues();
    List<String> values = valuesCaptor.getAllValues();

    assertThat(keys.get(0)).isEqualTo("printLast-synced-client-creation-time");
    assertThat(values.get(0)).isEqualTo(later.toString());
    assertThat(keys.get(1)).isEqualTo("printLast-synced-client-numbers-csv");
    assertThat(values.get(1)).isEqualTo("2");
  }
}
