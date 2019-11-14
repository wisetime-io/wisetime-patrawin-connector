/*
 * Copyright (c) 2019 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector.patrawin;

import static io.wisetime.connector.patrawin.ConnectorLauncher.PatrawinConnectorConfigKey;
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

import com.google.common.collect.ImmutableList;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.wisetime.connector.ConnectorModule;
import io.wisetime.connector.api_client.ApiClient;
import io.wisetime.connector.config.RuntimeConfig;
import io.wisetime.connector.datastore.ConnectorStore;
import io.wisetime.connector.patrawin.fake.FakeCaseClientGenerator;
import io.wisetime.connector.patrawin.model.Case;
import io.wisetime.connector.patrawin.model.Client;
import io.wisetime.connector.patrawin.persistence.PatrawinDao;
import io.wisetime.connector.patrawin.persistence.SyncStore;
import io.wisetime.connector.patrawin.util.MsSqlTimeDbFormatter;
import io.wisetime.connector.patrawin.util.TimeDbFormatter;
import io.wisetime.generated.connect.UpsertTagRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

/**
 * @author shane.xie@practiceinsight.io
 * @author galya.bogdanova@m.practiceinsight.io
 */
class PatrawinConnectorRefreshTagsTest {

  private static final String TAG_UPSERT_PATH = "/test/path/";

  private static FakeCaseClientGenerator fakeGenerator = new FakeCaseClientGenerator();
  private static ConnectorModule connectorModule;
  private static PatrawinConnector connector;

  private static PatrawinDao patrawinDao = mock(PatrawinDao.class);
  private static ApiClient apiClient = mock(ApiClient.class);
  private static SyncStore syncStore = mock(SyncStore.class);

  @BeforeAll
  static void setUp() {
    RuntimeConfig.setProperty(PatrawinConnectorConfigKey.TAG_UPSERT_PATH, TAG_UPSERT_PATH);

    Injector injector = Guice.createInjector(binder -> {
      binder.bind(PatrawinDao.class).toProvider(() -> patrawinDao);
      binder.bind(TimeDbFormatter.class).toInstance(new MsSqlTimeDbFormatter());
    });
    connector = spy(injector.getInstance(PatrawinConnector.class));

    // Ensure PatrawinConnector#init will not fail
    doReturn(true)
        .when(patrawinDao)
        .hasExpectedSchema();

    doReturn(syncStore)
        .when(connector)
        .createSyncStore(any(ConnectorStore.class));

    connectorModule = new ConnectorModule(apiClient, mock(ConnectorStore.class));
    connector.init(connectorModule);
  }

  @BeforeEach
  void setUpTest() {
    reset(patrawinDao);
    reset(apiClient);
    reset(syncStore);
    reset(connector);
  }

  @Test
  void performTagUpdate_data_for_two_loops() {
    when(patrawinDao.findCasesOrderedByCreationTime(any(), anyList(), anyInt()))
        .thenReturn(ImmutableList.of()) // empty list for syncing new
        .thenReturn(fakeGenerator.randomCases(3));
    when(patrawinDao.findClientsOrderedByCreationTime(any(), anyList(), anyInt()))
        .thenReturn(ImmutableList.of()) // empty list for syncing new
        .thenReturn(fakeGenerator.randomClients(3));

    connector.performTagUpdate();

    verify(connector, times(1)).refreshCases(anyInt());
    verify(connector, times(1)).refreshClients(anyInt());
  }

  @Test
  void refreshCases_none_found() throws IOException {
    when(patrawinDao.findCasesOrderedByCreationTime(any(), anyList(), anyInt()))
        .thenReturn(ImmutableList.of());

    connector.refreshCases(10);

    verify(apiClient, never()).tagUpsertBatch(anyList());
    verify(syncStore, never()).setLastRefreshedCases(anyList());
  }

  @Test
  void refreshCases_error_during_io() throws IOException {
    when(patrawinDao.findCasesOrderedByCreationTime(any(), anyList(), anyInt()))
        .thenReturn(ImmutableList.of(fakeGenerator.randomCase()));

    IOException apiException = new IOException("Expected Exception");
    doThrow(apiException)
        .when(apiClient)
        .tagUpsertBatch(anyList());

    assertThatThrownBy(() -> connector.refreshCases(10))
        .isInstanceOf(RuntimeException.class)
        .hasCause(apiException);

    verify(apiClient, times(1)).tagUpsertBatch(anyList());
    verify(syncStore, never()).setLastRefreshedCases(anyList());
  }

  @Test
  void refreshCases_some_found() throws IOException {
    Case case1 = fakeGenerator.randomCase();
    Case case2 = fakeGenerator.randomCase();

    LocalDateTime lastRefreshedCaseCreationTime = LocalDateTime.now().withYear(1753);
    doReturn(Optional.of(lastRefreshedCaseCreationTime))
        .when(syncStore)
        .getLastRefreshedCaseCreationTime();

    List<String> lastRefreshedCaseNumbers = Collections.emptyList();
    doReturn(lastRefreshedCaseNumbers)
        .when(syncStore)
        .getLastRefreshedCaseNumbers();

    ArgumentCaptor<Optional<LocalDateTime>> lastRefreshedCaseCreationTimeCaptor = ArgumentCaptor.forClass(Optional.class);
    ArgumentCaptor<List> lastRefreshedCaseNumbersCaptor = ArgumentCaptor.forClass(List.class);
    when(patrawinDao.findCasesOrderedByCreationTime(lastRefreshedCaseCreationTimeCaptor.capture(),
        lastRefreshedCaseNumbersCaptor.capture(), anyInt()))
        .thenReturn(ImmutableList.of(case1, case2));

    connector.refreshCases(10);

    assertThat(lastRefreshedCaseCreationTimeCaptor.getValue())
        .contains(lastRefreshedCaseCreationTime);

    assertThat(lastRefreshedCaseNumbersCaptor.getValue())
        .isEqualTo(lastRefreshedCaseNumbers);

    ArgumentCaptor<List<UpsertTagRequest>> upsertRequestsCaptor = ArgumentCaptor.forClass(List.class);
    verify(apiClient, times(1)).tagUpsertBatch(upsertRequestsCaptor.capture());

    assertThat(upsertRequestsCaptor.getValue())
        .containsExactly(
            new UpsertTagRequest()
                .name(case1.getNumber())
                .description(case1.getDescription())
                .additionalKeywords(ImmutableList.of(case1.getNumber()))
                .path(TAG_UPSERT_PATH),
            new UpsertTagRequest()
                .name(case2.getNumber())
                .description(case2.getDescription())
                .additionalKeywords(ImmutableList.of(case2.getNumber()))
                .path(TAG_UPSERT_PATH));

    ArgumentCaptor<List<Case>> storeCasesCaptor = ArgumentCaptor.forClass(List.class);
    verify(syncStore, times(1)).setLastRefreshedCases(storeCasesCaptor.capture());

    assertThat(storeCasesCaptor.getValue())
        .containsExactly(case1, case2);
  }

  @Test
  void syncClients_none_found() throws IOException {
    when(patrawinDao.findClientsOrderedByCreationTime(any(), anyList(), anyInt()))
        .thenReturn(ImmutableList.of());

    connector.refreshClients(10);

    verify(apiClient, never()).tagUpsertBatch(anyList());
    verify(syncStore, never()).setLastRefreshedClients(anyList());
  }

  @Test
  void syncClients_error_during_io() throws IOException {
    when(patrawinDao.findClientsOrderedByCreationTime(any(), anyList(), anyInt()))
        .thenReturn(ImmutableList.of(fakeGenerator.randomClient()));

    IOException apiException = new IOException("Expected Exception");
    doThrow(apiException)
        .when(apiClient)
        .tagUpsertBatch(anyList());

    assertThatThrownBy(() -> connector.refreshClients(10))
        .isInstanceOf(RuntimeException.class)
        .hasCause(apiException);

    verify(apiClient, times(1)).tagUpsertBatch(anyList());
    verify(syncStore, never()).setLastRefreshedClients(anyList());
  }

  @Test
  void syncClients_some_found() throws IOException {
    Client client1 = fakeGenerator.randomClient();
    Client client2 = fakeGenerator.randomClient();

    LocalDateTime lastRefreshedClientCreationTime = LocalDateTime.now().withYear(1753);
    doReturn(Optional.of(lastRefreshedClientCreationTime))
        .when(syncStore)
        .getLastRefreshedClientCreationTime();

    List<String> lastRefreshedClientNumbers = Collections.emptyList();
    doReturn(lastRefreshedClientNumbers)
        .when(syncStore)
        .getLastRefreshedClientNumbers();

    ArgumentCaptor<Optional<LocalDateTime>> lastRefreshedClientsCreationTimeCaptor = ArgumentCaptor.forClass(Optional.class);
    ArgumentCaptor<List> lastRefreshedClientsNumbersCaptor = ArgumentCaptor.forClass(List.class);
    when(patrawinDao.findClientsOrderedByCreationTime(lastRefreshedClientsCreationTimeCaptor.capture(),
        lastRefreshedClientsNumbersCaptor.capture(), anyInt()))
        .thenReturn(ImmutableList.of(client1, client2));

    connector.refreshClients(10);

    assertThat(lastRefreshedClientsCreationTimeCaptor.getValue())
        .contains(lastRefreshedClientCreationTime);

    assertThat(lastRefreshedClientsNumbersCaptor.getValue())
        .isEqualTo(lastRefreshedClientNumbers);

    ArgumentCaptor<List<UpsertTagRequest>> upsertRequests = ArgumentCaptor.forClass(List.class);
    verify(apiClient, times(1)).tagUpsertBatch(upsertRequests.capture());

    assertThat(upsertRequests.getValue())
        .containsExactly(
            new UpsertTagRequest()
                .name(client1.getNumber())
                .description(client1.getAlias())
                .additionalKeywords(ImmutableList.of(client1.getNumber()))
                .path(TAG_UPSERT_PATH),
            new UpsertTagRequest()
                .name(client2.getNumber())
                .description(client2.getAlias())
                .additionalKeywords(ImmutableList.of(client2.getNumber()))
                .path(TAG_UPSERT_PATH));

    ArgumentCaptor<List<Client>> storeClientsCaptor = ArgumentCaptor.forClass(List.class);
    verify(syncStore, times(1)).setLastRefreshedClients(storeClientsCaptor.capture());

    assertThat(storeClientsCaptor.getValue())
        .containsExactly(client1, client2);
  }

  @Test
  void casesTagRefreshBatchSize_enforce_min() {
    RuntimeConfig.setProperty(PatrawinConnectorConfigKey.TAG_UPSERT_BATCH_SIZE, "100");
    when(patrawinDao.casesCount()).thenReturn(20L);
    assertThat(connector.casesTagRefreshBatchSize())
        .as("Calculated batch size was less than the minimum refresh batch size")
        .isEqualTo(10);
  }

  @Test
  void casesTagRefreshBatchSize_enforce_max() {
    RuntimeConfig.setProperty(PatrawinConnectorConfigKey.TAG_UPSERT_BATCH_SIZE, "20");
    when(patrawinDao.casesCount()).thenReturn(Long.MAX_VALUE);
    assertThat(connector.casesTagRefreshBatchSize())
        .as("Calculated batch size was more than the maximum refresh batch size")
        .isEqualTo(20);
  }

  @Test
  void casesTagRefreshBatchSize_calculated() {
    RuntimeConfig.setProperty(PatrawinConnectorConfigKey.TAG_UPSERT_BATCH_SIZE, "1000");
    final int fourteenDaysInMinutes = 20_160;
    when(patrawinDao.casesCount()).thenReturn(400_000L);
    assertThat(connector.casesTagRefreshBatchSize())
        .as("Calculated batch size was greater than the minimum and less than the maximum")
        .isEqualTo(400_000 / (fourteenDaysInMinutes / connectorModule.getTagSyncIntervalMinutes()));
  }

  @Test
  void clientsTagRefreshBatchSize_enforce_min() {
    RuntimeConfig.setProperty(PatrawinConnectorConfigKey.TAG_UPSERT_BATCH_SIZE, "100");
    when(patrawinDao.clientsCount()).thenReturn(20L);
    assertThat(connector.clientsTagRefreshBatchSize())
        .as("Calculated batch size was less than the minimum refresh batch size")
        .isEqualTo(10);
  }

  @Test
  void clientsTagRefreshBatchSize_enforce_max() {
    RuntimeConfig.setProperty(PatrawinConnectorConfigKey.TAG_UPSERT_BATCH_SIZE, "20");
    when(patrawinDao.clientsCount()).thenReturn(Long.MAX_VALUE);
    assertThat(connector.clientsTagRefreshBatchSize())
        .as("Calculated batch size was more than the maximum refresh batch size")
        .isEqualTo(20);
  }

  @Test
  void clientsTagRefreshBatchSize_calculated() {
    RuntimeConfig.setProperty(PatrawinConnectorConfigKey.TAG_UPSERT_BATCH_SIZE, "1000");
    final int fourteenDaysInMinutes = 20_160;
    when(patrawinDao.clientsCount()).thenReturn(400_000L);
    assertThat(connector.clientsTagRefreshBatchSize())
        .as("Calculated batch size was greater than the minimum and less than the maximum")
        .isEqualTo(400_000 / (fourteenDaysInMinutes / connectorModule.getTagSyncIntervalMinutes()));
  }
}
