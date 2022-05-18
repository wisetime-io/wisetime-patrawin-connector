/*
 * Copyright (c) 2019 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector.patrawin.alert;

import static io.wisetime.connector.patrawin.ConnectorLauncher.PatrawinConnectorConfigKey.PATRAWIN_DB_PASSWORD;
import static io.wisetime.connector.patrawin.ConnectorLauncher.PatrawinConnectorConfigKey.PATRAWIN_DB_USER;
import static io.wisetime.connector.patrawin.ConnectorLauncher.PatrawinConnectorConfigKey.PATRAWIN_JDBC_URL;

import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.zaxxer.hikari.HikariDataSource;
import io.wisetime.connector.config.RuntimeConfig;
import io.wisetime.connector.patrawin.ConnectorLauncher;
import io.wisetime.connector.patrawin.persistence.PatrawinDao;
import io.wisetime.connector.patrawin.util.TimeDbFormatter;
import io.wisetime.test_docker.ContainerRuntimeSpec;
import io.wisetime.test_docker.DockerLauncher;
import io.wisetime.test_docker.containers.SqlServer;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import javax.inject.Inject;
import lombok.Builder;
import org.codejargon.fluentjdbc.api.FluentJdbc;
import org.codejargon.fluentjdbc.api.FluentJdbcBuilder;
import org.codejargon.fluentjdbc.api.query.Query;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.api.configuration.FluentConfiguration;

/**
 * Standalone test runner for AlertEmailScheduler To run from your own environment provide the following parameters
 * <p>
 * ALERT_EMAIL_ENABLED=true ALERT_EMAIL_INTERVAL_HH_MM=00:02  Email interval is 2 minutes
 * ALERT_RECIPIENT_EMAIL_ADDRESSES=an email address the alarm will be sent to ALERT_SENDER_EMAIL_ADDRESS=an email
 * address the alarm will be sent from ALERT_EMAIL_SENDER_USERNAME= sender user name. For some servers can be the same
 * as sender email address, ie for google ALERT_MAIL_SMTP_HOST=alert email smtp host. As an example for gmail provide
 * smtp.gmail.com ALERT_MAIL_SMTP_PORT=alert email smtp port. As an example for gmail TLS it is 587
 * ALERT_EMAIL_SENDER_PASSWORD=sender password ALERT_STARTTLS_ENABLE=true set for smtp servers with TLS
 * HEALTH_CHECK_INTERVAL=5 5 minutes interval for health check
 * <p>
 * The expected behaviour.
 * <p>
 * The parameters above are going to start scheduler with email interval 2 minutes and a health check interval of 5
 * minutes. The standalone runner is creating a non-imported record in the pending time table when it is started. The
 * check is running every two minutes but alarm will not be sent at least for 5 minutes because it should wait for 5
 * minutes interval In 5 +2 minutes after start we should expect the first email to be sent to recipient The emails will
 * be sent every 2 minutes until we stop scheduler or alternatively we can update record in the pending  time table by
 * setting the imported field to some date value.
 *
 * @author dmitry.zatselyapin@wisetime.com
 */
public class StandaloneEmailScheduleRunner {

  private JdbcDatabaseContainer sqlServerContainer;
  private PatrawinDao patrawinDao;
  private AlertEmailScheduler scheduler;
  private FluentJdbc fluentJdbc;
  private TimeDbFormatter timeDbFormatter;

  public static void main(String[] args) {
    StandaloneEmailScheduleRunner runner = new StandaloneEmailScheduleRunner();
    runner.setupDatabase();
    runner.setupTestData();
    runner.init();
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      System.out.println("Shutting down scheduler");
      runner.scheduler.shutdown();
    }));
  }

  void init() {
    scheduler.init();
  }

  void setupTestData() {
    Preconditions.checkState(
        // We don't want to accidentally truncate production tables
        RuntimeConfig.getString(PATRAWIN_JDBC_URL).orElse("").equals(sqlServerContainer.getJdbcUrl())
    );
    Query query = fluentJdbc.query();
    query.update("TRUNCATE TABLE ARENDE_1").run();
    query.update("TRUNCATE TABLE KUND_24").run();
    query.update("TRUNCATE TABLE BEHORIG_50").run();
    query.update("TRUNCATE TABLE CREDIT_LEVEL_334").run();
    query.update("TRUNCATE TABLE PENDING_TIME_335").run();
    query.update("TRUNCATE TABLE CREDIT_LEVEL_334").run();
    // Creating record with imported date equal null to start receiving emails
    PendingTime pendingTime = PendingTime.builder()
        .startTimeUtc(LocalDateTime.now().minus(1, ChronoUnit.DAYS))
        .arendenr("1")
        .fakturatextnr("1")
        .kundnr("1")
        .minutes("1")
        .text("1")
        .user_Id("1")
        .build();
    createUnimportedPendingTime(pendingTime);

  }

  private void setupDatabase() {
    sqlServerContainer = getContainer();
    RuntimeConfig.setProperty(PATRAWIN_JDBC_URL, sqlServerContainer.getJdbcUrl());
    RuntimeConfig.setProperty(PATRAWIN_DB_USER, sqlServerContainer.getUsername());
    RuntimeConfig.setProperty(PATRAWIN_DB_PASSWORD, sqlServerContainer.getPassword());

    final Injector injector = Guice.createInjector(
        new ConnectorLauncher.PatrawinDbModule(), new FlywayPatrawinTestDbModule()
    );

    patrawinDao = injector.getInstance(PatrawinDao.class);
    scheduler = injector.getInstance(AlertEmailScheduler.class);
    fluentJdbc = new FluentJdbcBuilder().connectionProvider(injector.getInstance(HikariDataSource.class)).build();
    timeDbFormatter = injector.getInstance(TimeDbFormatter.class);
    // Apply DB schema to test db
    injector.getInstance(Flyway.class).migrate();
  }

  private void createUnimportedPendingTime(PendingTime pendingTime) {

    fluentJdbc.query()
        .update("INSERT INTO PENDING_TIME_335 (User_Id, Arendenr, Kundnr, StartTimeUtc, Minutes, Fakturatextnr, Text )"
            + " VALUES (?, ?, ?, ?, ? , ?, ?)")
        .params(pendingTime.user_Id, pendingTime.arendenr, pendingTime.kundnr, pendingTime.startTimeUtc,
            pendingTime.minutes, pendingTime.fakturatextnr, pendingTime.text)
        .run();
  }

  @Builder
  private static class PendingTime {

    String user_Id;
    String arendenr;
    String kundnr;
    LocalDateTime startTimeUtc;
    String minutes;
    String fakturatextnr;
    String text;
  }

  /**
   * Initializes database schema for unit tests
   */
  public static class FlywayPatrawinTestDbModule extends AbstractModule {

    @Override
    protected void configure() {
      bind(Flyway.class).toProvider(
          FlywayPatrawinTestDbModule.FlywayPatriciaProvider.class);
    }

    private static class FlywayPatriciaProvider implements Provider<Flyway> {

      @Inject
      private Provider<HikariDataSource> dataSourceProvider;

      @Override
      public Flyway get() {
        FluentConfiguration configure = Flyway.configure()
            .dataSource(dataSourceProvider.get())
            .baselineVersion(MigrationVersion.fromVersion("0"))
            .locations("sql/")
            .baselineOnMigrate(true);
        return new Flyway(configure);
      }
    }
  }

  private static JdbcDatabaseContainer getContainer() {
    ContainerRuntimeSpec container = DockerLauncher.instance().createContainer(new SqlServer());
    return new JdbcDatabaseContainer(container);
  }

  private static class JdbcDatabaseContainer {

    private final String jdbcUrl;

    JdbcDatabaseContainer(ContainerRuntimeSpec containerSpec) {
      jdbcUrl = String.format("jdbc:sqlserver://%s:%d",
          containerSpec.getContainerIpAddress(),
          containerSpec.getRequiredMappedPort(1433));
    }

    String getJdbcUrl() {
      return jdbcUrl;
    }

    String getUsername() {
      return "SA";
    }

    String getPassword() {
      return "A_Str0ng_Required_Password";
    }
  }
}
