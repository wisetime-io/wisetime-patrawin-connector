/*
 * Copyright (c) 2019 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector.patrawin;

import com.google.common.collect.ImmutableList;
import com.google.inject.Guice;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import io.wisetime.connector.api_client.ApiClient;
import io.wisetime.connector.config.ConnectorConfigKey;
import io.wisetime.connector.config.RuntimeConfig;
import io.wisetime.connector.datastore.ConnectorStore;
import io.wisetime.connector.integrate.ConnectorModule;
import io.wisetime.connector.patrawin.ConnectorLauncher.PatrawinConnectorConfigKey;
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

  private static final int TAG_UPSERT_BATCH_SIZE = 100;
  private static final String TAG_UPSERT_PATH = "/test/path/";

  private static RandomDataGenerator randomDataGenerator = new RandomDataGenerator();
  private static PatrawinConnector connector;

  private static PatrawinDao patrawinDao = mock(PatrawinDao.class);
  private static ApiClient apiClient = mock(ApiClient.class);
  private static SyncStore syncStore = mock(SyncStore.class);

  @BeforeAll
  static void setUp() {
    RuntimeConfig.setProperty(PatrawinConnectorConfigKey.TAG_UPSERT_BATCH_SIZE, String.valueOf(TAG_UPSERT_BATCH_SIZE));
    RuntimeConfig.setProperty(PatrawinConnectorConfigKey.TAG_UPSERT_PATH, TAG_UPSERT_PATH);
    RuntimeConfig.clearProperty(ConnectorConfigKey.CALLER_KEY);

    assertThat(RuntimeConfig.getString(ConnectorConfigKey.CALLER_KEY))
        .as("CALLER_KEY empty value expected")
        .isNotPresent();

    assertThat(RuntimeConfig.getInt(PatrawinConnectorConfigKey.TAG_UPSERT_BATCH_SIZE))
        .as("TAG_UPSERT_BATCH_SIZE should be set to " + TAG_UPSERT_BATCH_SIZE)
        .contains(TAG_UPSERT_BATCH_SIZE);

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

  @AfterAll
  static void tearDown() {
    RuntimeConfig.clearProperty(PatrawinConnectorConfigKey.TAG_UPSERT_BATCH_SIZE);
    RuntimeConfig.clearProperty(PatrawinConnectorConfigKey.TAG_UPSERT_PATH);

    assertThat(RuntimeConfig.getInt(PatrawinConnectorConfigKey.TAG_UPSERT_BATCH_SIZE))
        .as("TAG_UPSERT_BATCH_SIZE empty result expected")
        .isNotPresent();
    assertThat(RuntimeConfig.getString(PatrawinConnectorConfigKey.TAG_UPSERT_PATH))
        .isNotPresent();
  }

  @BeforeEach
  void setUpTest() {
    reset(patrawinDao);
    reset(apiClient);
    reset(syncStore);
  }

  @Test
  void syncCases_none_found() throws IOException {
    when(patrawinDao.findCasesOrderedByCreationTime(any(), anyList(), anyInt()))
        .thenReturn(ImmutableList.of());

    connector.performTagUpdate();

    verify(apiClient, never()).tagUpsertBatch(anyList());
    verify(syncStore, never()).setLastSyncedCases(anyList());
  }

  @Test
  void syncCases_error_during_io() throws IOException {
    when(patrawinDao.findCasesOrderedByCreationTime(any(), anyList(), anyInt()))
        .thenReturn(ImmutableList.of(randomDataGenerator.randomCase()));

    doThrow(new IOException())
        .when(apiClient)
        .tagUpsertBatch(anyList());

    assertThatThrownBy(() -> connector.performTagUpdate()).isInstanceOf(RuntimeException.class);

    verify(apiClient, times(1)).tagUpsertBatch(anyList());
    verify(syncStore, never()).setLastSyncedCases(anyList());
  }

  @Test
  void syncCases_some_found() throws IOException {
    Case case1 = randomDataGenerator.randomCase();
    Case case2 = randomDataGenerator.randomCase();

    doReturn(Instant.EPOCH)
        .when(syncStore)
        .getLastSyncedCaseCreationTime();
    doReturn(Collections.emptyList())
        .when(syncStore)
        .getLastSyncedCaseNumbers();

    ArgumentCaptor<Integer> batchSize = ArgumentCaptor.forClass(Integer.class);
    when(patrawinDao.findCasesOrderedByCreationTime(any(), anyList(), batchSize.capture()))
        .thenReturn(ImmutableList.of(case1, case2))
        .thenReturn(ImmutableList.of());

    connector.performTagUpdate();

    ArgumentCaptor<List<UpsertTagRequest>> upsertRequests = ArgumentCaptor.forClass(List.class);
    verify(apiClient, times(1)).tagUpsertBatch(upsertRequests.capture());

    assertThat(upsertRequests.getValue())
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
        )
        .as("We should create tags for both new cases found, with the configured tag upsert path");

    ArgumentCaptor<List<Case>> storeCasesCaptor = ArgumentCaptor.forClass(List.class);
    verify(syncStore, times(1)).setLastSyncedCases(storeCasesCaptor.capture());

    assertThat(batchSize.getValue())
        .isEqualTo(TAG_UPSERT_BATCH_SIZE)
        .as("The configured batch size should be used");
    assertThat(storeCasesCaptor.getValue())
        .containsExactly(case1, case2)
        .as("Upserted cases are set as last synced cases");
  }

  @Test
  void syncClients_none_found() throws IOException {
    when(patrawinDao.findClientsOrderedByCreationTime(any(), anyList(), anyInt()))
        .thenReturn(ImmutableList.of());

    connector.performTagUpdate();

    verify(apiClient, never()).tagUpsertBatch(anyList());
    verify(syncStore, never()).setLastSyncedClients(anyList());
  }

  @Test
  void syncClients_error_during_io() throws IOException {
    when(patrawinDao.findClientsOrderedByCreationTime(any(), anyList(), anyInt()))
        .thenReturn(ImmutableList.of(randomDataGenerator.randomClient()));

    doThrow(new IOException())
        .when(apiClient)
        .tagUpsertBatch(anyList());

    assertThatThrownBy(() -> connector.performTagUpdate()).isInstanceOf(RuntimeException.class);

    verify(apiClient, times(1)).tagUpsertBatch(anyList());
    verify(syncStore, never()).setLastSyncedClients(anyList());
  }

  @Test
  void syncClients_some_found() throws IOException {
    Client client1 = randomDataGenerator.randomClient();
    Client client2 = randomDataGenerator.randomClient();

    doReturn(Instant.EPOCH)
        .when(syncStore)
        .getLastSyncedClientCreationTime();
    doReturn(Collections.emptyList())
        .when(syncStore)
        .getLastSyncedClientIds();

    ArgumentCaptor<Integer> batchSize = ArgumentCaptor.forClass(Integer.class);
    when(patrawinDao.findClientsOrderedByCreationTime(any(), anyList(), batchSize.capture()))
        .thenReturn(ImmutableList.of(client1, client2))
        .thenReturn(ImmutableList.of());

    connector.performTagUpdate();

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
        )
        .as("We should create tags for both new clients found, with the configured tag upsert path");

    ArgumentCaptor<List<Client>> storeClientsCaptor = ArgumentCaptor.forClass(List.class);
    verify(syncStore, times(1)).setLastSyncedClients(storeClientsCaptor.capture());

    assertThat(batchSize.getValue())
        .isEqualTo(TAG_UPSERT_BATCH_SIZE)
        .as("The configured batch size should be used");
    assertThat(storeClientsCaptor.getValue())
        .containsExactly(client1, client2)
        .as("Upserted clients are set as last synced cases");
  }
}
