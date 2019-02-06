/*
 * Copyright (c) 2019 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector.patrawin;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import io.wisetime.connector.ServerRunner;
import io.wisetime.connector.config.RuntimeConfig;
import io.wisetime.connector.config.RuntimeConfigKey;
import io.wisetime.connector.patrawin.util.MsSqlTimeDbFormatter;
import io.wisetime.connector.patrawin.util.TimeDbFormatter;

/**
 * Connector application entry point.
 *
 * @author shane.xie@practiceinsight.io
 */
public class ConnectorLauncher {

  public static void main(final String... args) throws Exception {
    ServerRunner.createServerBuilder()
        .withWiseTimeConnector(Guice.createInjector(new PatrawinDbModule()).getInstance(PatrawinConnector.class))
        .build()
        .startServer();
  }

  /**
   * Configuration keys for the WiseTime Jira Connector.
   *
   * @author shane.xie@practiceinsight.io
   */
  public enum PatrawinConnectorConfigKey implements RuntimeConfigKey {

    PATRAWIN_JDBC_URL("PATRAWIN_JDBC_URL"),
    PATRAWIN_DB_USER("PATRAWIN_DB_USER"),
    PATRAWIN_DB_PASSWORD("PATRAWIN_DB_PASSWORD"),
    TAG_UPSERT_PATH("TAG_UPSERT_PATH"),
    TAG_UPSERT_BATCH_SIZE("TAG_UPSERT_BATCH_SIZE");

    private final String configKey;

    PatrawinConnectorConfigKey(final String configKey) {
      this.configKey = configKey;
    }

    @Override
    public String getConfigKey() {
      return configKey;
    }
  }

  /**
   * Bind the Patrawin database connection via DI.
   */
  public static class PatrawinDbModule extends AbstractModule {
    private static final Logger log = LoggerFactory.getLogger(PatrawinDbModule.class);

    @Override
    protected void configure() {
      final HikariConfig hikariConfig = new HikariConfig();

      hikariConfig.setJdbcUrl(
          RuntimeConfig.getString(PatrawinConnectorConfigKey.PATRAWIN_JDBC_URL)
              .orElseThrow(() -> new RuntimeException("Missing required PATRAWIN_JDBC_URL configuration"))
      );

      hikariConfig.setUsername(
          RuntimeConfig.getString(PatrawinConnectorConfigKey.PATRAWIN_DB_USER)
              .orElseThrow(() -> new RuntimeException("Missing required PATRAWIN_DB_USER configuration"))
      );

      hikariConfig.setPassword(
          RuntimeConfig.getString(PatrawinConnectorConfigKey.PATRAWIN_DB_PASSWORD)
              .orElseThrow(() -> new RuntimeException("Missing required PATRAWIN_JDBC_PASSWORD configuration"))
      );
      hikariConfig.setConnectionTimeout(TimeUnit.MINUTES.toMillis(1));
      hikariConfig.setMaximumPoolSize(10);

      log.info("Connecting to Patrawin database at URL: {}, Username: {}", hikariConfig.getJdbcUrl(),
          hikariConfig.getUsername());

      bind(TimeDbFormatter.class).toInstance(new MsSqlTimeDbFormatter());
      bind(DataSource.class).toInstance(new HikariDataSource(hikariConfig));
    }
  }
}
