/*
 * Copyright (c) 2019 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector.patrawin;

import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provider;

import com.github.javafaker.Faker;

import org.codejargon.fluentjdbc.api.FluentJdbc;
import org.codejargon.fluentjdbc.api.FluentJdbcBuilder;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MSSQLServerContainer;

import javax.inject.Inject;
import javax.sql.DataSource;

import io.wisetime.connector.config.RuntimeConfig;

import static io.wisetime.connector.patrawin.ConnectorLauncher.PatrawinConnectorConfigKey.PATRAWIN_DB_PASSWORD;
import static io.wisetime.connector.patrawin.ConnectorLauncher.PatrawinConnectorConfigKey.PATRAWIN_DB_USER;
import static io.wisetime.connector.patrawin.ConnectorLauncher.PatrawinConnectorConfigKey.PATRAWIN_JDBC_URL;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author shane.xie@practiceinsight.io
 */
class PatrawinDaoTest {

  private static PatrawinDao patrawinDao;
  private static Faker faker = new Faker();
  private static FluentJdbc fluentJdbc;
  private static MSSQLServerContainer sqlServerContainer = new MSSQLServerContainer();

  @BeforeAll
  static void setUp() {
    sqlServerContainer.start();
    RuntimeConfig.setProperty(PATRAWIN_JDBC_URL, sqlServerContainer.getJdbcUrl());
    RuntimeConfig.setProperty(PATRAWIN_DB_USER, sqlServerContainer.getUsername());
    RuntimeConfig.setProperty(PATRAWIN_DB_PASSWORD, sqlServerContainer.getPassword());

    final Injector injector = Guice.createInjector(
        new ConnectorLauncher.PatrawinDbModule(), new FlywayPatrawinTestDbModule()
    );

    patrawinDao = injector.getInstance(PatrawinDao.class);
    fluentJdbc = new FluentJdbcBuilder().connectionProvider(injector.getInstance(DataSource.class)).build();

    // Apply Inprotech and FileSite DB schema to test db
    injector.getInstance(Flyway.class).migrate();
  }

  @BeforeEach
  void setupTests() {
    Preconditions.checkState(
        // We don't want to accidentally truncate production tables
        RuntimeConfig.getString(PATRAWIN_JDBC_URL).orElse("").equals(sqlServerContainer.getJdbcUrl())
    );
//    Query query = fluentJdbc.query();
//    query.update("DELETE FROM CASES").run();
  }

  @AfterAll
  static void tearDown() {
    sqlServerContainer.stop();
  }

  @Test
  void canQueryDb() {
    assertThat(patrawinDao.canQueryDb())
        .as("should return true if connected to a database")
        .isTrue();
  }

  /**
   * Initializes database schema for unit tests
   */
  public static class FlywayPatrawinTestDbModule extends AbstractModule {

    @Override
    protected void configure() {
      bind(Flyway.class).toProvider(FlywayPatriciaProvider.class);
    }

    private static class FlywayPatriciaProvider implements Provider<Flyway> {

      @Inject
      private Provider<DataSource> dataSourceProvider;

      @Override
      public Flyway get() {
        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSourceProvider.get());
        flyway.setBaselineVersion(MigrationVersion.fromVersion("0"));
        flyway.setBaselineOnMigrate(true);
        flyway.setLocations("sql/");
        return flyway;
      }
    }
  }
}