/*
 * Copyright (c) 2019 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector.patrawin;

import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.com.google.common.collect.ImmutableList;

import java.util.Set;

import io.wisetime.connector.datastore.ConnectorStore;
import io.wisetime.connector.patrawin.fake.FakeTimeGroupGenerator;
import io.wisetime.connector.patrawin.persistence.SyncStore;
import io.wisetime.generated.connect.TimeGroup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * @author shane.xie@practiceinsight.io
 * @author galya.bogdanova@m.practiceinsight.io
 */
class PatrawinConnectorUtilsTest {

  @Test
  void createSyncStore_creates_new_instance() {
    final PatrawinConnector connector = new PatrawinConnector();
    final ConnectorStore connectorStore = mock(ConnectorStore.class);
    final SyncStore syncStore1 = connector.createSyncStore(connectorStore);
    final SyncStore syncStore2 = connector.createSyncStore(connectorStore);
    assertThat(syncStore1).isNotSameAs(syncStore2);
  }

  @Test
  void getTimeGroupModifiers_returns_all_unique_timerow_modifiers() {
    final PatrawinConnector connector = new PatrawinConnector();
    final FakeTimeGroupGenerator fakeEntities = new FakeTimeGroupGenerator();
    final String activityType1 = "123";
    final String activityType2 = null;
    final String activityType3 = "Modifier";
    final String activityType4 = "123";
    final TimeGroup timeGroup = fakeEntities.randomTimeGroup().timeRows(ImmutableList.of(
        fakeEntities.randomTimeRow(activityType1),
        fakeEntities.randomTimeRow(activityType2),
        fakeEntities.randomTimeRow(activityType3),
        fakeEntities.randomTimeRow(activityType4)
    ));
    final Set<String> modifiers = connector.getTimeGroupActivityCodes(timeGroup);
    assertThat(modifiers)
        .as("should not contain any duplicates")
        .hasSize(3);
    assertThat(modifiers)
        .as("should contain all distinct values")
        .contains(activityType1, activityType2, activityType3);
  }

  @Test
  void printLast_none() {
    assertThat(PatrawinConnector.printLast(ImmutableList.of()))
        .isEqualTo("None yet");
  }

  @Test
  void printLast_some() {
    assertThat(PatrawinConnector.printLast(ImmutableList.of("1", "2")))
        .isEqualTo("2");
  }
}
