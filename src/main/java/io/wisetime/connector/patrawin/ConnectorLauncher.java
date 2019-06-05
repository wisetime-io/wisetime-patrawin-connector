/*
 * Copyright (c) 2019 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector.patrawin;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.wisetime.connector.ConnectorController;
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
    ConnectorController connectorController = buildConnectorController();
    connectorController.start();
  }

  public static ConnectorController buildConnectorController() {
    return ConnectorController.newBuilder()
        .withWiseTimeConnector(Guice.createInjector(new PatrawinDbModule()).getInstance(PatrawinConnector.class))
        .build();
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
    TAG_MODIFIER_ACTIVITY_CODE_MAPPING("TAG_MODIFIER_ACTIVITY_CODE_MAPPING"),
    DEFAULT_MODIFIER("DEFAULT_MODIFIER"),
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

      log.info("Connecting to Patrawin database {} with user {}",
          formatForLogging(hikariConfig.getJdbcUrl()), hikariConfig.getUsername());

      bind(TimeDbFormatter.class).toInstance(new MsSqlTimeDbFormatter());
      bind(HikariDataSource.class).toInstance(new HikariDataSource(hikariConfig));
    }

    /**
     * return Return database connection info, excluding sensitive information (e.g. password).
     */
    @VisibleForTesting
    String formatForLogging(String jdbcUrl) {
      String host = "UNKNOWN";
      String port = "UNKNOWN";
      String databaseName = "UNKNOWN";
      if (StringUtils.startsWithIgnoreCase(jdbcUrl, "jdbc:sqlserver:")) {
        Pattern jdbcUrlPattern = Pattern.compile(".*//(\\S+?)(?::(\\d+))?(;.*)?");
        Matcher matcher = jdbcUrlPattern.matcher(jdbcUrl);
        if (matcher.matches()) {
          host = matcher.group(1);
          port = StringUtils.defaultIfEmpty(matcher.group(2), "DEFAULT");
        }
      } else {
        Pattern jdbcUrlPattern = Pattern.compile(".*//(\\S+:\\S+@)?(\\S+?)(?::(\\d+))?/(\\S+?)(\\?.*)?");
        Matcher matcher = jdbcUrlPattern.matcher(jdbcUrl);
        if (matcher.matches()) {
          host = matcher.group(2);
          port = StringUtils.defaultIfEmpty(matcher.group(3), "DEFAULT");
          databaseName = matcher.group(4);
        }
      }
      return String.format("host: %s, port: %s, database name: %s", host, port, databaseName);
    }
  }
}
