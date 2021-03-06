/*
 * Copyright (c) 2019 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector.patrawin.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.wisetime.connector.datastore.ConnectorStore;
import io.wisetime.connector.patrawin.model.Case;
import io.wisetime.connector.patrawin.model.Client;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.testcontainers.shaded.com.google.common.collect.ImmutableList;
import org.testcontainers.shaded.org.apache.commons.lang.StringUtils;

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
        .as("Should return empty if no case has been synced yet")
        .isEmpty();
  }

  @Test
  void getLastSyncedCaseCreationTime_some() {
    final LocalDateTime now = LocalDateTime.now();
    when(connectorStore.getString("printLast-synced-case-creation-time")).thenReturn(Optional.of(now.toString()));
    assertThat(syncStore.getLastSyncedCaseCreationTime())
        .as("We should get back the correct LocalDateTime value")
        .contains(now);
  }

  @Test
  void getLastRefreshedCaseCreationTime_none() {
    when(connectorStore.getString("printLast-refreshed-case-creation-time")).thenReturn(Optional.empty());
    assertThat(syncStore.getLastRefreshedCaseCreationTime())
        .as("Should return empty if no case has been refreshed yet")
        .isEmpty();
  }

  @Test
  void getLastRefreshedCaseCreationTime_empty() {
    when(connectorStore.getString("printLast-refreshed-case-creation-time")).thenReturn(Optional.of(StringUtils.EMPTY));
    assertThat(syncStore.getLastRefreshedCaseCreationTime())
        .as("Should return empty if empty value has been saved")
        .isEmpty();
  }

  @Test
  void getLastRefreshedCaseCreationTime_some() {
    final LocalDateTime now = LocalDateTime.now();
    when(connectorStore.getString("printLast-refreshed-case-creation-time")).thenReturn(Optional.of(now.toString()));
    assertThat(syncStore.getLastRefreshedCaseCreationTime())
        .as("We should get back the correct LocalDateTime value")
        .contains(now);
  }

  @Test
  void getLastSyncedCaseNumbers_none() {
    when(connectorStore.getString("printLast-synced-case-number")).thenReturn(Optional.empty());
    assertThat(syncStore.getLastSyncedCaseNumber())
        .as("No cases have been synced yet")
        .isEmpty();
  }

  @Test
  void getLastSyncedCaseNumber_some() {
    when(connectorStore.getString("printLast-synced-case-number")).thenReturn(Optional.of("1"));
    assertThat(syncStore.getLastSyncedCaseNumber())
        .as("Some cases have been synced")
        .contains("1");
  }

  @Test
  void getLastRefreshedCaseNumbers_none() {
    when(connectorStore.getString("printLast-refreshed-case-number")).thenReturn(Optional.empty());
    assertThat(syncStore.getLastRefreshedCaseNumbers())
        .as("No cases have been refreshed yet")
        .isEmpty();
  }

  @Test
  void getLastRefreshedCaseNumbers_empty() {
    when(connectorStore.getString("printLast-refreshed-case-number")).thenReturn(Optional.of(StringUtils.EMPTY));
    assertThat(syncStore.getLastRefreshedCaseNumbers())
        .as("Should return empty if empty value has been saved")
        .isEmpty();
  }

  @Test
  void getLastRefreshedCaseNumber_some() {
    when(connectorStore.getString("printLast-refreshed-case-number")).thenReturn(Optional.of("2"));
    assertThat(syncStore.getLastRefreshedCaseNumbers())
        .as("Some cases have been refreshed")
        .contains("2");
  }

  @Test
  void setLastSyncedCases() {
    final LocalDateTime later = LocalDateTime.now();
    final LocalDateTime earlier = later.minusSeconds(60);

    syncStore.setLastSyncedCases(ImmutableList.of(
        Case.builder()
            .description("")
            .number("1")
            .creationTime(earlier)
            .build(),
        Case.builder()
            .number("2")
            .description("")
            .creationTime(later)
            .build(),
        Case.builder()
            .number("3")
            .description("")
            .creationTime(later)
            .build()));

    ArgumentCaptor<String> keysCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> valuesCaptor = ArgumentCaptor.forClass(String.class);

    verify(connectorStore, times(2)).putString(keysCaptor.capture(), valuesCaptor.capture());

    List<String> keys = keysCaptor.getAllValues();
    List<String> values = valuesCaptor.getAllValues();

    assertThat(keys.get(0)).isEqualTo("printLast-synced-case-creation-time");
    assertThat(values.get(0)).isEqualTo(later.toString());
    assertThat(keys.get(1)).isEqualTo("printLast-synced-case-number");
    assertThat(values.get(1)).isEqualTo("3");
  }

  @Test
  void setLastRefreshedCases() {
    final LocalDateTime later = LocalDateTime.now();
    final LocalDateTime earlier = later.minusSeconds(60);

    syncStore.setLastRefreshedCases(ImmutableList.of(
        Case.builder()
            .number("1")
            .description("")
            .creationTime(earlier)
            .build(),
        Case.builder()
            .number("2")
            .description("")
            .creationTime(later)
            .build(),
        Case.builder()
            .number("3")
            .description("")
            .creationTime(later)
            .build()
    ));

    ArgumentCaptor<String> keysCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> valuesCaptor = ArgumentCaptor.forClass(String.class);

    verify(connectorStore, times(2)).putString(keysCaptor.capture(), valuesCaptor.capture());

    List<String> keys = keysCaptor.getAllValues();
    List<String> values = valuesCaptor.getAllValues();

    assertThat(keys.get(0)).isEqualTo("printLast-refreshed-case-creation-time");
    assertThat(values.get(0)).isEqualTo(later.toString());
    assertThat(keys.get(1)).isEqualTo("printLast-refreshed-case-number");
    assertThat(values.get(1)).isEqualTo("3");
  }

  @Test
  void clearLastRefreshedCases() {
    syncStore.clearLastRefreshedCases();
    verify(connectorStore, times(1)).putString("printLast-refreshed-case-creation-time", "");
    verify(connectorStore, times(1)).putString("printLast-refreshed-case-number", "");
  }

  // Clients:

  @Test
  void getLastSyncedClientCreationTime_none() {
    when(connectorStore.getString("printLast-synced-client-creation-time")).thenReturn(Optional.empty());
    assertThat(syncStore.getLastSyncedClientCreationTime())
        .as("Should return empty if no client has been synced yet")
        .isEmpty();
  }

  @Test
  void getLastSyncedClientCreationTime_some() {
    final LocalDateTime now = LocalDateTime.now();
    when(connectorStore.getString("printLast-synced-case-creation-time")).thenReturn(Optional.of(now.toString()));
    assertThat(syncStore.getLastSyncedCaseCreationTime())
        .as("We should get back the correct LocalDateTime value")
        .contains(now);
  }

  @Test
  void getLastRefreshedClientCreationTime_none() {
    when(connectorStore.getString("printLast-refreshed-client-creation-time")).thenReturn(Optional.empty());
    assertThat(syncStore.getLastRefreshedClientCreationTime())
        .as("Should return empty if no client has been refreshed yet")
        .isEmpty();
  }

  @Test
  void getLastRefreshedClientCreationTime_empty() {
    when(connectorStore.getString("printLast-refreshed-client-creation-time"))
        .thenReturn(Optional.of(StringUtils.EMPTY));
    assertThat(syncStore.getLastRefreshedClientCreationTime())
        .as("Should return empty if empty value has been saved")
        .isEmpty();
  }

  @Test
  void getLastRefreshedClientCreationTime_some() {
    final LocalDateTime now = LocalDateTime.now();
    when(connectorStore.getString("printLast-refreshed-case-creation-time")).thenReturn(Optional.of(now.toString()));
    assertThat(syncStore.getLastRefreshedCaseCreationTime())
        .as("We should get back the correct LocalDateTime value")
        .contains(now);
  }

  @Test
  void getLastSyncedClientNumbers_none() {
    when(connectorStore.getString("printLast-synced-client-number")).thenReturn(Optional.empty());
    assertThat(syncStore.getLastSyncedClientNumber())
        .as("No clients have been synced yet")
        .isEmpty();
  }

  @Test
  void getLastSyncedClientNumbers_some() {
    when(connectorStore.getString("printLast-synced-client-number")).thenReturn(Optional.of("3"));
    assertThat(syncStore.getLastSyncedClientNumber())
        .as("Some clients have been synced")
        .contains("3");
  }

  @Test
  void getLastRefreshedClientNumbers_none() {
    when(connectorStore.getString("printLast-refreshed-client-number")).thenReturn(Optional.empty());
    assertThat(syncStore.getLastRefreshedClientNumber())
        .as("No clients have been synced yet")
        .isEmpty();
  }

  @Test
  void getLastRefreshedClientNumbers_empty() {
    when(connectorStore.getString("printLast-refreshed-client-number")).thenReturn(Optional.of(StringUtils.EMPTY));
    assertThat(syncStore.getLastRefreshedClientNumber())
        .as("Should return empty if empty value has been saved")
        .isEmpty();
  }

  @Test
  void getLastRefreshedClientNumbers_some() {
    when(connectorStore.getString("printLast-refreshed-client-number")).thenReturn(Optional.of("2"));
    assertThat(syncStore.getLastRefreshedClientNumber())
        .as("Some clients have been synced")
        .contains("2");
  }

  @Test
  void setLastSyncedClients() {
    final LocalDateTime later = LocalDateTime.now();
    final LocalDateTime earlier = later.minusSeconds(60);

    syncStore.setLastSyncedClients(ImmutableList.of(
        Client.builder()
            .number("1")
            .alias("")
            .creationTime(earlier)
            .build(),
        Client.builder()
            .number("3")
            .alias("")
            .creationTime(earlier)
            .build(),
        Client.builder()
            .number("2")
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
    assertThat(keys.get(1)).isEqualTo("printLast-synced-client-number");
    assertThat(values.get(1)).isEqualTo("2");
  }

  @Test
  void setLastRefreshedClients() {
    final LocalDateTime later = LocalDateTime.now();
    final LocalDateTime earlier = later.minusSeconds(60);

    syncStore.setLastRefreshedClients(ImmutableList.of(
        Client.builder()
            .number("1")
            .alias("")
            .creationTime(earlier)
            .build(),
        Client.builder()
            .number("3")
            .alias("")
            .creationTime(earlier)
            .build(),
        Client.builder()
            .number("2")
            .alias("")
            .creationTime(later)
            .build()
    ));

    ArgumentCaptor<String> keysCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> valuesCaptor = ArgumentCaptor.forClass(String.class);

    verify(connectorStore, times(2)).putString(keysCaptor.capture(), valuesCaptor.capture());

    List<String> keys = keysCaptor.getAllValues();
    List<String> values = valuesCaptor.getAllValues();

    assertThat(keys.get(0)).isEqualTo("printLast-refreshed-client-creation-time");
    assertThat(values.get(0)).isEqualTo(later.toString());
    assertThat(keys.get(1)).isEqualTo("printLast-refreshed-client-number");
    assertThat(values.get(1)).isEqualTo("2");
  }

  @Test
  void clearLastRefreshedClients() {
    syncStore.clearLastRefreshedClients();
    verify(connectorStore, times(1)).putString("printLast-refreshed-client-creation-time", "");
    verify(connectorStore, times(1)).putString("printLast-refreshed-client-number", "");
  }
}
