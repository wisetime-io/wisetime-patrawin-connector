/*
 * Copyright (c) 2019 Practice Insight Pty Ltd. All rights reserved.
 */

package io.wisetime.connector.patrawin;

import com.google.inject.Guice;
import com.google.inject.Injector;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.wisetime.connector.ConnectorModule;
import io.wisetime.connector.api_client.ApiClient;
import io.wisetime.connector.config.RuntimeConfig;
import io.wisetime.connector.datastore.ConnectorStore;
import io.wisetime.connector.patrawin.persistence.PatrawinDao;
import io.wisetime.connector.patrawin.util.MsSqlTimeDbFormatter;
import io.wisetime.connector.patrawin.util.TimeDbFormatter;

import static io.wisetime.connector.patrawin.ConnectorLauncher.PatrawinConnectorConfigKey;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;

/**
 * @author alvin.llobrera@practiceinsight.io
 */
class PatrawinConnectorInitTest {

  private static PatrawinDao patrawinDaoMock = mock(PatrawinDao.class);
  private static ApiClient apiClientMock = mock(ApiClient.class);
  private static ConnectorStore connectorStoreMock = mock(ConnectorStore.class);

  private static PatrawinConnector connector;

  @BeforeAll
  static void setUp() {
    Injector injector = Guice.createInjector(binder -> {
      binder.bind(PatrawinDao.class).toProvider(() -> patrawinDaoMock);
      binder.bind(TimeDbFormatter.class).toInstance(new MsSqlTimeDbFormatter());
    });
    connector = injector.getInstance(PatrawinConnector.class);

    // Ensure PatrawinConnector#init will not fail
    doReturn(true).when(patrawinDaoMock).hasExpectedSchema();
  }

  @BeforeEach
  void setUpTest() {
    reset(apiClientMock);
    reset(connectorStoreMock);

    RuntimeConfig.clearProperty(PatrawinConnectorConfigKey.DEFAULT_MODIFIER);
    RuntimeConfig.clearProperty(PatrawinConnectorConfigKey.TAG_MODIFIER_ACTIVITY_CODE_MAPPING);
  }

  @Test
  void init_should_initialized_when_required_params_exist() {
    RuntimeConfig.setProperty(PatrawinConnectorConfigKey.DEFAULT_MODIFIER, "default modifier");
    RuntimeConfig.setProperty(PatrawinConnectorConfigKey.TAG_MODIFIER_ACTIVITY_CODE_MAPPING, "default modifier:1");

    connector.init(new ConnectorModule(apiClientMock, connectorStoreMock));
  }

  @Test
  void init_should_require_default_modifier() {
    RuntimeConfig.clearProperty(PatrawinConnectorConfigKey.DEFAULT_MODIFIER);
    assertThatThrownBy(() -> connector.init(new ConnectorModule(apiClientMock, connectorStoreMock)))
        .as("Default modifier should be set to initialize connector")
        .withFailMessage("Required configuration param DEFAULT_MODIFIER is not set")
        .isExactlyInstanceOf(IllegalStateException.class);
  }

  @Test
  void init_should_require_modifier_activity_code_mapping() {
    RuntimeConfig.setProperty(PatrawinConnectorConfigKey.DEFAULT_MODIFIER, "modifier");
    RuntimeConfig.clearProperty(ConnectorLauncher.PatrawinConnectorConfigKey.TAG_MODIFIER_ACTIVITY_CODE_MAPPING);
    assertThatThrownBy(() -> connector.init(new ConnectorModule(apiClientMock, connectorStoreMock)))
        .as("activity code - modifier mapping should be set to initialize connector")
        .withFailMessage("Required configuration param TAG_MODIFIER_ACTIVITY_CODE_MAPPING is not set")
        .isExactlyInstanceOf(IllegalStateException.class);
  }

  @Test
  void init_valid_format_for_modifier_activity_code_mapping() {
    RuntimeConfig.setProperty(PatrawinConnectorConfigKey.DEFAULT_MODIFIER, "modifier");
    RuntimeConfig.setProperty(PatrawinConnectorConfigKey.TAG_MODIFIER_ACTIVITY_CODE_MAPPING, "hello, how are you?");
    assertThatThrownBy(() -> connector.init(new ConnectorModule(apiClientMock, connectorStoreMock)))
        .as("activity code - modifier mapping should be in csv following 'key:value' format ")
        .hasMessageContaining("Invalid Patrawin modifier to activity code mapping.")
        .isExactlyInstanceOf(IllegalStateException.class);
  }

  @Test
  void init_modifier_activity_code_mapping_should_contain_default_modifier() {
    RuntimeConfig.setProperty(PatrawinConnectorConfigKey.DEFAULT_MODIFIER, "default modifier");
    RuntimeConfig.setProperty(PatrawinConnectorConfigKey.TAG_MODIFIER_ACTIVITY_CODE_MAPPING, "modifier:activitycode");
    assertThatThrownBy(() -> connector.init(new ConnectorModule(apiClientMock, connectorStoreMock)))
        .hasMessageContaining("Patrawin modifiers mapping should include activity code for default modifier")
        .isExactlyInstanceOf(IllegalArgumentException.class);
  }
}
