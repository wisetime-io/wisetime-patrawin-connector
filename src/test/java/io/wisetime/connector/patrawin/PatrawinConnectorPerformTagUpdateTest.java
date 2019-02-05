/*
 * Copyright (c) 2019 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector.patrawin;

import com.google.common.collect.ImmutableList;
import com.google.inject.Guice;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import io.wisetime.connector.api_client.ApiClient;
import io.wisetime.connector.config.RuntimeConfig;
import io.wisetime.connector.datastore.ConnectorStore;
import io.wisetime.connector.integrate.ConnectorModule;
import io.wisetime.generated.connect.UpsertTagRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author shane.xie@practiceinsight.io
 * @author galya.bogdanova@m.practiceinsight.io
 */
public class PatrawinConnectorPerformTagUpdateTest {

  private static final String TAG_UPSERT_PATH = "/test/path/";

  private static RandomDataGenerator randomDataGenerator = new RandomDataGenerator();
  private static PatrawinConnector connector;

  private static PatrawinDao patrawinDao = mock(PatrawinDao.class);
  private static ApiClient apiClient = mock(ApiClient.class);
  private static SyncStore syncStore = mock(SyncStore.class);

  @BeforeAll
  static void setUp() {
    RuntimeConfig.setProperty(ConnectorLauncher.PatrawinConnectorConfigKey.TAG_UPSERT_PATH, TAG_UPSERT_PATH);

    connector = spy(
        Guice.createInjector(binder -> binder.bind(PatrawinDao.class).toProvider(() -> patrawinDao))
            .getInstance(PatrawinConnector.class)
    );

    // Ensure PatrawinConnector#init will not fail
    doReturn(true)
        .when(patrawinDao)
        .hasExpectedSchema();

    doReturn(syncStore)
        .when(connector)
        .createSyncStore(any(ConnectorStore.class));

    connector.init(new ConnectorModule(apiClient, mock(ConnectorStore.class)));
  }

  @BeforeEach
  void setUpTest() {
    reset(patrawinDao);
    reset(apiClient);
    reset(syncStore);
  }

  @Test
  void performTagUpdate_data_for_two_loops() {
    when(patrawinDao.findCasesOrderedByCreationTime(any(), anyList(), anyInt()))
        .thenReturn(randomDataGenerator.randomCases(3))
        .thenReturn(ImmutableList.of());
    when(patrawinDao.findClientsOrderedByCreationTime(any(), anyList(), anyInt()))
        .thenReturn(randomDataGenerator.randomClients(3))
        .thenReturn(ImmutableList.of());

    connector.performTagUpdate();

    verify(connector, times(2)).syncCases();
    verify(connector, times(2)).syncClients();
  }

  @Test
  void syncCases_none_found() throws IOException {
    when(patrawinDao.findCasesOrderedByCreationTime(any(), anyList(), anyInt()))
        .thenReturn(ImmutableList.of());

    connector.syncCases();

    verify(apiClient, never()).tagUpsertBatch(anyList());
    verify(syncStore, never()).setLastSyncedCases(anyList());
  }

  @Test
  void syncCases_error_during_io() throws IOException {
    when(patrawinDao.findCasesOrderedByCreationTime(any(), anyList(), anyInt()))
        .thenReturn(ImmutableList.of(randomDataGenerator.randomCase()));

    IOException apiException = new IOException("Expected Exception");
    doThrow(apiException)
        .when(apiClient)
        .tagUpsertBatch(anyList());

    assertThatThrownBy(() -> connector.syncCases())
        .isInstanceOf(RuntimeException.class)
        .hasCause(apiException);

    verify(apiClient, times(1)).tagUpsertBatch(anyList());
    verify(syncStore, never()).setLastSyncedCases(anyList());
  }

  @Test
  void syncCases_some_found() throws IOException {
    Case case1 = randomDataGenerator.randomCase();
    Case case2 = randomDataGenerator.randomCase();

    Instant lastSyncedCaseCreationTime = Instant.EPOCH;
    doReturn(lastSyncedCaseCreationTime)
        .when(syncStore)
        .getLastSyncedCaseCreationTime();

    List<String> lastSyncedCaseNumbers = Collections.emptyList();
    doReturn(lastSyncedCaseNumbers)
        .when(syncStore)
        .getLastSyncedCaseNumbers();

    ArgumentCaptor<Instant> lastSyncedCaseCreationTimeCaptor = ArgumentCaptor.forClass(Instant.class);
    ArgumentCaptor<List> lastSyncedCaseNumbersCaptor = ArgumentCaptor.forClass(List.class);
    when(patrawinDao.findCasesOrderedByCreationTime(lastSyncedCaseCreationTimeCaptor.capture(),
        lastSyncedCaseNumbersCaptor.capture(), anyInt()))
        .thenReturn(ImmutableList.of(case1, case2));

    connector.syncCases();

    assertThat(lastSyncedCaseCreationTimeCaptor.getValue())
        .isEqualTo(lastSyncedCaseCreationTime);

    assertThat(lastSyncedCaseNumbersCaptor.getValue())
        .isEqualTo(lastSyncedCaseNumbers);

    ArgumentCaptor<List<UpsertTagRequest>> upsertRequestsCaptor = ArgumentCaptor.forClass(List.class);
    verify(apiClient, times(1)).tagUpsertBatch(upsertRequestsCaptor.capture());

    assertThat(upsertRequestsCaptor.getValue())
        .containsExactly(
            new UpsertTagRequest()
                .name(case1.getCaseNumber())
                .description(case1.getDescription())
                .additionalKeywords(ImmutableList.of(case1.getCaseNumber()))
                .path(TAG_UPSERT_PATH),
            new UpsertTagRequest()
                .name(case2.getCaseNumber())
                .description(case2.getDescription())
                .additionalKeywords(ImmutableList.of(case2.getCaseNumber()))
                .path(TAG_UPSERT_PATH)
        );

    ArgumentCaptor<List<Case>> storeCasesCaptor = ArgumentCaptor.forClass(List.class);
    verify(syncStore, times(1)).setLastSyncedCases(storeCasesCaptor.capture());

    assertThat(storeCasesCaptor.getValue())
        .containsExactly(case1, case2);
  }

  @Test
  void syncClients_none_found() throws IOException {
    when(patrawinDao.findClientsOrderedByCreationTime(any(), anyList(), anyInt()))
        .thenReturn(ImmutableList.of());

    connector.syncClients();

    verify(apiClient, never()).tagUpsertBatch(anyList());
    verify(syncStore, never()).setLastSyncedClients(anyList());
  }

  @Test
  void syncClients_error_during_io() throws IOException {
    when(patrawinDao.findClientsOrderedByCreationTime(any(), anyList(), anyInt()))
        .thenReturn(ImmutableList.of(randomDataGenerator.randomClient()));

    IOException apiException = new IOException("Expected Exception");
    doThrow(apiException)
        .when(apiClient)
        .tagUpsertBatch(anyList());

    assertThatThrownBy(() -> connector.syncClients())
        .isInstanceOf(RuntimeException.class)
        .hasCause(apiException);

    verify(apiClient, times(1)).tagUpsertBatch(anyList());
    verify(syncStore, never()).setLastSyncedClients(anyList());
  }

  @Test
  void syncClients_some_found() throws IOException {
    Client client1 = randomDataGenerator.randomClient();
    Client client2 = randomDataGenerator.randomClient();

    Instant lastSyncedClientCreationTime = Instant.EPOCH;
    doReturn(lastSyncedClientCreationTime)
        .when(syncStore)
        .getLastSyncedClientCreationTime();

    List<String> lastSyncedClientNumbers = Collections.emptyList();
    doReturn(lastSyncedClientNumbers)
        .when(syncStore)
        .getLastSyncedClientIds();

    ArgumentCaptor<Instant> lastSyncedClientsCreationTimeCaptor = ArgumentCaptor.forClass(Instant.class);
    ArgumentCaptor<List> lastSyncedClientsNumbersCaptor = ArgumentCaptor.forClass(List.class);
    when(patrawinDao.findClientsOrderedByCreationTime(lastSyncedClientsCreationTimeCaptor.capture(),
        lastSyncedClientsNumbersCaptor.capture(), anyInt()))
        .thenReturn(ImmutableList.of(client1, client2));

    connector.syncClients();

    assertThat(lastSyncedClientsCreationTimeCaptor.getValue())
        .isEqualTo(lastSyncedClientCreationTime);

    assertThat(lastSyncedClientsNumbersCaptor.getValue())
        .isEqualTo(lastSyncedClientNumbers);

    ArgumentCaptor<List<UpsertTagRequest>> upsertRequests = ArgumentCaptor.forClass(List.class);
    verify(apiClient, times(1)).tagUpsertBatch(upsertRequests.capture());

    assertThat(upsertRequests.getValue())
        .containsExactly(
            new UpsertTagRequest()
                .name(client1.getClientId())
                .description(client1.getAlias())
                .additionalKeywords(ImmutableList.of(client1.getClientId()))
                .path(TAG_UPSERT_PATH),
            new UpsertTagRequest()
                .name(client2.getClientId())
                .description(client2.getAlias())
                .additionalKeywords(ImmutableList.of(client2.getClientId()))
                .path(TAG_UPSERT_PATH)
        );

    ArgumentCaptor<List<Client>> storeClientsCaptor = ArgumentCaptor.forClass(List.class);
    verify(syncStore, times(1)).setLastSyncedClients(storeClientsCaptor.capture());

    assertThat(storeClientsCaptor.getValue())
        .containsExactly(client1, client2);
  }
}
