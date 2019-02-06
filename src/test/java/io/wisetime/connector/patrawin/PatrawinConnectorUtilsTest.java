/*
 * Copyright (c) 2019 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector.patrawin;

import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.com.google.common.collect.ImmutableList;

import io.wisetime.connector.datastore.ConnectorStore;
import io.wisetime.connector.patrawin.persistence.SyncStore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * @author shane.xie@practiceinsight.io
 */
public class PatrawinConnectorUtilsTest {

  @Test
  void createSyncStore_creates_new_instance() {
    PatrawinConnector connector = new PatrawinConnector();
    ConnectorStore connectorStore = mock(ConnectorStore.class);
    SyncStore syncStore1 = connector.createSyncStore(connectorStore);
    SyncStore syncStore2 = connector.createSyncStore(connectorStore);
    assertThat(syncStore1).isNotSameAs(syncStore2);
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
