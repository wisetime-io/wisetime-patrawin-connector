/*
 * Copyright (c) 2019 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector.patrawin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.wisetime.connector.ConnectorModule;
import io.wisetime.connector.api_client.ApiClient;
import io.wisetime.connector.datastore.ConnectorStore;
import io.wisetime.connector.patrawin.fake.FakeTimeGroupGenerator;
import io.wisetime.connector.patrawin.persistence.PatrawinDao;
import io.wisetime.connector.patrawin.util.MsSqlTimeDbFormatter;
import io.wisetime.connector.patrawin.util.TimeDbFormatter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class PatrawinConnectorHealthCheckTest {

  private static PatrawinDao patrawinDaoMock = mock(PatrawinDao.class);
  private static ApiClient apiClientMock = mock(ApiClient.class);
  private static PatrawinConnector connector;
  private static FakeTimeGroupGenerator fakeGenerator = new FakeTimeGroupGenerator();


  @BeforeAll
  static void setUp() {
    Injector injector = Guice.createInjector(binder -> {
      binder.bind(PatrawinDao.class).toProvider(() -> patrawinDaoMock);
      binder.bind(TimeDbFormatter.class).toInstance(new MsSqlTimeDbFormatter());
    });

    connector = injector.getInstance(PatrawinConnector.class);

    doReturn(true).when(patrawinDaoMock).hasExpectedSchema();
    connector.init(new ConnectorModule(apiClientMock, mock(ConnectorStore.class), 5));
  }

  @Test
  void isConnectorHealthyTrue() {
    doReturn(true).when(patrawinDaoMock).canQueryDb();
    assertThat(connector.isConnectorHealthy())
        .as("The connector should be healthy")
        .isTrue();
  }

  @Test
  void isConnectorHealthyFalse() {
    doReturn(false).when(patrawinDaoMock).canQueryDb();
    doReturn(false).when(patrawinDaoMock).hasUnprocessedTime(anyInt());
    assertThat(connector.isConnectorHealthy())
        .as("The connector should not be healthy")
        .isFalse();
  }
}
