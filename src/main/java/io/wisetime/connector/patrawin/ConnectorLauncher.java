/*
 * Copyright (c) 2019 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector.patrawin;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.wisetime.connector.ConnectorController;
import io.wisetime.connector.config.RuntimeConfig;
import io.wisetime.connector.config.RuntimeConfigKey;
import io.wisetime.connector.patrawin.util.MsSqlTimeDbFormatter;
import io.wisetime.connector.patrawin.util.TimeDbFormatter;
import java.util.concurrent.TimeUnit;

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
   * <pre>
   *  Configuration keys for the WiseTime Patrawin Connector.
   *  Refer to README.md file for documentation of each of the below config keys.
   * </pre>
   * @author shane.xie, dmitry.zatselyapin
   */
  public enum PatrawinConnectorConfigKey implements RuntimeConfigKey {

    PATRAWIN_JDBC_URL("PATRAWIN_JDBC_URL"),
    PATRAWIN_DB_USER("PATRAWIN_DB_USER"),
    PATRAWIN_DB_PASSWORD("PATRAWIN_DB_PASSWORD"),
    TAG_UPSERT_PATH("TAG_UPSERT_PATH"),
    ADD_SUMMARY_TO_NARRATIVE("ADD_SUMMARY_TO_NARRATIVE"),
    TIMEZONE("TIMEZONE"),
    HEALTH_CHECK_INTERVAL("HEALTH_CHECK_INTERVAL"),
    TAG_UPSERT_BATCH_SIZE("TAG_UPSERT_BATCH_SIZE"),
    // Set ALERT_EMAIL_ENABLED to true if alert emails are required
    ALERT_EMAIL_ENABLED("ALERT_EMAIL_ENABLED"),
    // Alert email interval specify number of hours and minutes
    // to wait before the next alert email is sent. Provides as HH:MM
    // ie 01:10 is 1 hour and 10 minutes.
    ALERT_EMAIL_INTERVAL_HH_MM("ALERT_EMAIL_INTERVAL_HH_MM"),
    ALERT_RECIPIENT_EMAIL_ADDRESSES("ALERT_RECIPIENT_EMAIL_ADDRESSES"),
    ALERT_SENDER_EMAIL_ADDRESS("ALERT_SENDER_EMAIL_ADDRESS"),
    ALERT_MAIL_SMTP_HOST("ALERT_MAIL_SMTP_HOST"),
    ALERT_MAIL_SMTP_PORT("ALERT_MAIL_SMTP_PORT"),
    ALERT_EMAIL_SENDER_PASSWORD("ALERT_EMAIL_SENDER_PASSWORD"),
    ALERT_EMAIL_SENDER_USERNAME("ALERT_EMAIL_SENDER_USERNAME"),
    //Set ALERT_STARTTLS_ENABLE to true for email alerts smtp host that supports TLS
    ALERT_STARTTLS_ENABLE("ALERT_STARTTLS_ENABLE"),
    //Port for access via ssl will be defaulted to ALERT_MAIL_SMTP_PORT if not set
    ALERT_MAIL_SMTP_SOCKET_FACTORY_PORT("ALERT_MAIL_SMTP_SOCKET_FACTORY_PORT"),
    // Socket factory class is required for SSL. It will be defaulted to javax.net.ssl.SSLSocketFactory if not set
    ALERT_MAIL_SMTP_SOCKET_FACTORY_CLASS("ALERT_MAIL_SMTP_SOCKET_FACTORY_CLASS"),
    // Activity sync batch size
    ACTIVITY_TYPE_BATCH_SIZE("ACTIVITY_TYPE_BATCH_SIZE");

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

      bind(TimeDbFormatter.class).toInstance(new MsSqlTimeDbFormatter());
      bind(HikariDataSource.class).toInstance(new HikariDataSource(hikariConfig));
    }

  }
}
