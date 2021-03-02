/*
 * Copyright (c) 2019 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector.patrawin;

import static io.wisetime.connector.patrawin.persistence.SyncStore.ACTIVITY_TYPE_LABELS_HASH_KEY;
import static io.wisetime.connector.patrawin.persistence.SyncStore.LAST_ACTIVITY_TYPE_LABELS_SYNC_KEY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.github.javafaker.Faker;
import com.google.inject.Guice;
import io.wisetime.connector.ConnectorModule;
import io.wisetime.connector.api_client.ApiClient;
import io.wisetime.connector.config.RuntimeConfig;
import io.wisetime.connector.datastore.ConnectorStore;
import io.wisetime.connector.patrawin.ConnectorLauncher.PatrawinConnectorConfigKey;
import io.wisetime.connector.patrawin.persistence.PatrawinDao;
import io.wisetime.connector.patrawin.persistence.PatrawinDao.ActivityTypeLabel;
import io.wisetime.generated.connect.ActivityType;
import io.wisetime.generated.connect.SyncActivityTypesRequest;
import io.wisetime.generated.connect.SyncSession;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mockito;

public class ActivityTypeUpdateTest {

  private final Faker faker = Faker.instance();

  private static HashFunction hashFunctionSpy = spy(HashFunction.class);
  private static PatrawinDao patrawinDaoMock = mock(PatrawinDao.class);
  private static ApiClient apiClientMock = mock(ApiClient.class);
  private static ConnectorStore connectorStoreMock = mock(ConnectorStore.class);

  private static PatrawinConnector connector;

  @BeforeAll
  static void setUp() {
    connector = Guice.createInjector(
        binder -> binder.bind(PatrawinDao.class).toProvider(() -> patrawinDaoMock),
        binder -> binder.bind(HashFunction.class).toInstance(hashFunctionSpy)
    )
        .getInstance(PatrawinConnector.class);
    when(patrawinDaoMock.hasExpectedSchema()).thenReturn(true);
    connector.init(new ConnectorModule(apiClientMock, connectorStoreMock));
  }

  @BeforeEach
  void setUpTest() {
    reset(patrawinDaoMock);
    reset(apiClientMock);
    reset(connectorStoreMock);
  }

  @Test
  void performActivityTypeUpdate_noActivityTypeLabels() throws Exception {
    final String syncSessionId = faker.numerify("syncSession-###");
    when(patrawinDaoMock.findActivityTypeLabels(anyInt(), anyInt()))
        .thenReturn(List.of());
    when(apiClientMock.activityTypesStartSyncSession())
        .thenReturn(new SyncSession().syncSessionId(syncSessionId));

    connector.performActivityTypeUpdate();

    // check that session was started and completed
    // as we should sync even with empty activity type labels to handle deletions
    verify(apiClientMock, times(1)).activityTypesStartSyncSession();
    verify(apiClientMock, times(1)).activityTypesCompleteSyncSession(new SyncSession()
        .syncSessionId(syncSessionId));
    // check that no activity type was sent
    verify(apiClientMock, never()).syncActivityTypes(any());
  }

  @Test
  void performActivityTypeUpdate_sameHash_syncedLessThanDayAgo() {
    // current and previous hashes are the same
    final String hash = faker.numerify("hash-###");
    when(hashFunctionSpy.hashStrings(anyList()))
        .thenReturn(hash);
    when(connectorStoreMock.getString(ACTIVITY_TYPE_LABELS_HASH_KEY))
        .thenReturn(Optional.of(hash));

    // last synced recently
    when(connectorStoreMock.getLong(LAST_ACTIVITY_TYPE_LABELS_SYNC_KEY))
        .thenReturn(Optional.of(System.currentTimeMillis()));

    connector.performActivityTypeUpdate();

    // check that there were no api calls
    verifyZeroInteractions(apiClientMock);
    // last sync timestamp should not be saved as no sync happened
    verify(connectorStoreMock, never()).putLong(eq(LAST_ACTIVITY_TYPE_LABELS_SYNC_KEY), anyLong());
    // new hash should not be saved as it wasn't changed
    verify(connectorStoreMock, never()).putString(eq(ACTIVITY_TYPE_LABELS_HASH_KEY), anyString());
  }

  @Test
  void performActivityTypeUpdate_sameHash_syncedMoreThanDayAgo() throws Exception {
    // current and previous hashes are the same
    final String hash = faker.numerify("hash-###");
    when(hashFunctionSpy.hashStrings(anyList()))
        .thenReturn(hash);
    when(connectorStoreMock.getString(ACTIVITY_TYPE_LABELS_HASH_KEY))
        .thenReturn(Optional.of(hash));

    // last synced more than day ago
    when(connectorStoreMock.getLong(LAST_ACTIVITY_TYPE_LABELS_SYNC_KEY))
        .thenReturn(Optional.of(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1)));

    when(apiClientMock.activityTypesStartSyncSession())
        .thenReturn(new SyncSession().syncSessionId(faker.numerify("syncSession-###")));
    when(patrawinDaoMock.findActivityTypeLabels(anyInt(), anyInt()))
        .thenReturn(List.of(randomActivityTypeLabel()));

    connector.performActivityTypeUpdate();

    // check that sync session was processed
    verify(apiClientMock, times(1)).activityTypesStartSyncSession();
    verify(apiClientMock, times(1)).activityTypesCompleteSyncSession(any());
    verify(apiClientMock, times(1)).syncActivityTypes(any());
  }

  private ActivityTypeLabel randomActivityTypeLabel() {
    return
        new ActivityTypeLabel()
            .setLabel(faker.chuckNorris().fact())
            .setId(faker.idNumber().valid());
  }

  @Test
  void performActivityTypeUpdate_hashDiffers() throws Exception {
    // current and previous hashes are different
    when(hashFunctionSpy.hashStrings(anyList()))
        .thenReturn(faker.numerify("hash-#####"));
    when(connectorStoreMock.getString(ACTIVITY_TYPE_LABELS_HASH_KEY))
        .thenReturn(Optional.of(faker.numerify("hash-#####")));

    // last synced recently
    when(connectorStoreMock.getLong(LAST_ACTIVITY_TYPE_LABELS_SYNC_KEY))
        .thenReturn(Optional.of(System.currentTimeMillis()));

    when(apiClientMock.activityTypesStartSyncSession())
        .thenReturn(new SyncSession().syncSessionId(faker.numerify("syncSession-###")));
    when(patrawinDaoMock.findActivityTypeLabels(anyInt(), anyInt()))
        .thenReturn(List.of(randomActivityTypeLabel()));

    connector.performActivityTypeUpdate();

    // check that sync session was processed
    verify(apiClientMock, times(1)).activityTypesStartSyncSession();
    verify(apiClientMock, times(1)).activityTypesCompleteSyncSession(any());
    verify(apiClientMock, times(1)).syncActivityTypes(any());
  }

  @Test
  void performActivityTypeUpdate() throws Exception {
    // clear property to use default (500)
    RuntimeConfig.clearProperty(PatrawinConnectorConfigKey.ACTIVITY_TYPE_BATCH_SIZE);

    final String newHash = faker.numerify("hash-###");
    when(hashFunctionSpy.hashStrings(anyList()))
        .thenReturn(newHash);

    final String syncSessionId = faker.numerify("syncSession-###");
    when(apiClientMock.activityTypesStartSyncSession())
        .thenReturn(new SyncSession().syncSessionId(syncSessionId));

    final ActivityTypeLabel activityTypeLabel1 = randomActivityTypeLabel();
    final ActivityTypeLabel activityTypeLabel2 = randomActivityTypeLabel();
    when(patrawinDaoMock.findActivityTypeLabels(0, 500))
        .thenReturn(List.of(activityTypeLabel1, activityTypeLabel2));

    connector.performActivityTypeUpdate();

    final InOrder inOrder = Mockito.inOrder(apiClientMock);
    // check that session was started
    inOrder.verify(apiClientMock, times(1)).activityTypesStartSyncSession();
    // check that proper request has been sent with sync session id
    final SyncActivityTypesRequest expectedRequest = new SyncActivityTypesRequest()
        .syncSessionId(syncSessionId)
        .activityTypes(List.of(
            new ActivityType()
                .code(String.valueOf(activityTypeLabel1.getId()))
                .label(activityTypeLabel1.getLabel())
                .description(""),
            new ActivityType()
                .code(String.valueOf(activityTypeLabel2.getId()))
                .label(activityTypeLabel2.getLabel())
                .description("")));
    inOrder.verify(apiClientMock, times(1)).syncActivityTypes(expectedRequest);
    // check that session was completed at the end
    inOrder.verify(apiClientMock, times(1)).activityTypesCompleteSyncSession(new SyncSession()
        .syncSessionId(syncSessionId));

    // last sync timestamp should be saved
    final ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
    verify(connectorStoreMock, times(1)).putLong(eq(LAST_ACTIVITY_TYPE_LABELS_SYNC_KEY), captor.capture());
    assertThat(captor.getValue())
        .as("new last sync timestamp should be saved")
        .isCloseTo(System.currentTimeMillis(), within(TimeUnit.SECONDS.toMillis(1)));
    // new hash should be saved
    verify(connectorStoreMock, times(1)).putString(ACTIVITY_TYPE_LABELS_HASH_KEY, newHash);
  }

  @Test
  void performActivityTypeUpdate_multipleBatches() throws Exception {
    // set batch size
    RuntimeConfig.setProperty(PatrawinConnectorConfigKey.ACTIVITY_TYPE_BATCH_SIZE, String.valueOf(1));

    final String syncSessionId = faker.numerify("syncSession-###");
    when(apiClientMock.activityTypesStartSyncSession())
        .thenReturn(new SyncSession().syncSessionId(syncSessionId));

    final List<ActivityTypeLabel> activityTypeLabelBatch1 = List.of(randomActivityTypeLabel());
    final List<ActivityTypeLabel> activityTypeLabelBatch2 = List.of(randomActivityTypeLabel());
    when(patrawinDaoMock.findActivityTypeLabels(0, 1))
        .thenReturn(activityTypeLabelBatch1);
    when(patrawinDaoMock.findActivityTypeLabels(1, 1))
        .thenReturn(activityTypeLabelBatch2);

    connector.performActivityTypeUpdate();

    final InOrder inOrder = Mockito.inOrder(apiClientMock);
    // check that session was started
    inOrder.verify(apiClientMock, times(1)).activityTypesStartSyncSession();
    // check that 2 batches has been sent
    final ArgumentCaptor<SyncActivityTypesRequest> captor = ArgumentCaptor.forClass(SyncActivityTypesRequest.class);
    inOrder.verify(apiClientMock, times(2)).syncActivityTypes(captor.capture());
    assertThat(captor.getAllValues().get(0).getActivityTypes())
        .hasSize(activityTypeLabelBatch1.size());
    assertThat(captor.getAllValues().get(1).getActivityTypes())
        .hasSize(activityTypeLabelBatch2.size());
    // check that session was completed at the end
    inOrder.verify(apiClientMock, times(1)).activityTypesCompleteSyncSession(new SyncSession()
        .syncSessionId(syncSessionId));
  }

  @Test
  void performActivityTypeUpdate_emptyDescription() throws Exception {
    when(patrawinDaoMock.findActivityTypeLabels(anyInt(), anyInt()))
        .thenReturn(List.of(
            new ActivityTypeLabel().setId("c-1").setLabel(null),
            new ActivityTypeLabel().setId("c-2").setLabel("   "),
            new ActivityTypeLabel().setId("c-3").setLabel("d-3")
        ));
    when(apiClientMock.activityTypesStartSyncSession())
        .thenReturn(new SyncSession().syncSessionId(faker.numerify("syncSession-###")));

    connector.performActivityTypeUpdate();

    final ArgumentCaptor<SyncActivityTypesRequest> captor = ArgumentCaptor.forClass(SyncActivityTypesRequest.class);
    verify(apiClientMock, times(1)).syncActivityTypes(captor.capture());

    assertThat(captor.getValue().getActivityTypes())
        .as("activity types should be mapped properly given empty descriptions")
        .containsExactly(
            // activity type label id is set as label
            new ActivityType().code("c-1").label("c-1").description(""),
            // activity type label id is set as label
            new ActivityType().code("c-2").label("c-2").description(""),
            // original label is set as label
            new ActivityType().code("c-3").label("d-3").description(""));
  }
}
