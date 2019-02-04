/*
 * Copyright (c) 2019 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector.patrawin;

import com.google.inject.Guice;

import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.com.google.common.collect.ImmutableList;

import io.wisetime.connector.datastore.ConnectorStore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * @author shane.xie@practiceinsight.io
 */
public class PatrawinConnectorUtilsTest {

  @Test
  void createSyncStore_createsNewInstance() {
    PatrawinDao patrawinDao = mock(PatrawinDao.class);
    PatrawinConnector connector =
        Guice.createInjector(binder -> binder.bind(PatrawinDao.class).toProvider(() -> patrawinDao))
            .getInstance(PatrawinConnector.class);
    SyncStore syncStore1 = connector.createSyncStore(mock(ConnectorStore.class));
    SyncStore syncStore2 = connector.createSyncStore(mock(ConnectorStore.class));
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
