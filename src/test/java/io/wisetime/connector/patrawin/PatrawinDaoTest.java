/*
 * Copyright (c) 2019 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector.patrawin;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provider;

import org.codejargon.fluentjdbc.api.FluentJdbc;
import org.codejargon.fluentjdbc.api.FluentJdbcBuilder;
import org.codejargon.fluentjdbc.api.query.Query;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MSSQLServerContainer;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.List;

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

  private static FakeEntities fakeEntities = new FakeEntities();
  private static MSSQLServerContainer sqlServerContainer = new MSSQLServerContainer();
  private static DateTimeFormatter dbDateTimeUtcFormatter = new DateTimeFormatterBuilder()
      .appendPattern("yyyy-MM-dd HH:mm:ss")
      .appendFraction(ChronoField.MICRO_OF_SECOND, 0, 3, true)
      .toFormatter()
      .withZone(ZoneOffset.UTC);

  private static PatrawinDao patrawinDao;
  private static FluentJdbc fluentJdbc;

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
    Query query = fluentJdbc.query();
    query.update("DELETE FROM ARENDE_1").run();
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

  @Test
  void findCasesOrderedByCreationTime() {
    final ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
    final Case createdNow1 = createCase(ImmutableCase.copyOf(fakeEntities.randomCase(now)).withCaseNumber("B1234"));
    final Case createdNow2 = createCase(ImmutableCase.copyOf(fakeEntities.randomCase(now)).withCaseNumber("A1234"));
    final Case createdYesterday = createCase(fakeEntities.randomCase(now.minusDays(1)));
    final Case createdLastWeek = createCase(fakeEntities.randomCase(now.minusWeeks(1)));
    final Case createdLast2Weeks = createCase(fakeEntities.randomCase(now.minusWeeks(2)));

    // initial query
    final List<Case> initialClients = patrawinDao.findCasesOrderedByCreationTime(
        now.minusWeeks(1).toInstant(),
        Lists.newArrayList(),
        3
    );

    assertThat(initialClients)
        .as("cases created last 2 weeks is < the creation time param")
        .doesNotContain(createdLast2Weeks);
    assertThat(initialClients)
        .as("correct cases should be retrieved in right order")
        .containsExactly(createdLastWeek, createdYesterday, createdNow2);

    // succeeding query
    final List<Case> nextCases = patrawinDao.findCasesOrderedByCreationTime(
        now.toInstant(),
        Lists.newArrayList(createdNow2.getCaseNumber()),
        3
    );

    assertThat(nextCases)
        .as("should only contain the matching cases excluding those case numbers specified")
        .containsExactly(createdNow1);
  }

  @Test
  void findClientsOrderedByCreationTime() {
    final ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
    final Client createdNow1 = createClient(ImmutableClient.copyOf(fakeEntities.randomClient(now)).withClientId("123"));
    final Client createdNow2 = createClient(ImmutableClient.copyOf(fakeEntities.randomClient(now)).withClientId("122"));
    final Client createdYesterday = createClient(fakeEntities.randomClient(now.minusDays(1)));
    final Client createdLastWeek = createClient(fakeEntities.randomClient(now.minusWeeks(1)));
    final Client createdLast2Weeks = createClient(fakeEntities.randomClient(now.minusWeeks(2)));

    // initial query
    final List<Client> initialClients = patrawinDao.findClientsOrderedByCreationTime(
        now.minusWeeks(1).toInstant(),
        Lists.newArrayList(),
        3
    );

    assertThat(initialClients)
        .as("clients created last 2 weeks is < the creation time param")
        .doesNotContain(createdLast2Weeks);
    assertThat(initialClients)
        .as("correct clients should be retrieved in right order")
        .containsExactly(createdLastWeek, createdYesterday, createdNow2);

    // succeeding query
    final List<Client> nextClients = patrawinDao.findClientsOrderedByCreationTime(
        now.toInstant(),
        Lists.newArrayList(createdNow2.getClientId()),
        3
    );

    assertThat(nextClients)
        .as("should only contain the matching clients excluding those client IDs specified")
        .containsExactly(createdNow1);
  }

  private Case createCase(Case patrawinCase) {
    fluentJdbc.query()
        .update("INSERT INTO ARENDE_1 (Arendenr, Slagord, Skapatdat, Rowguid, Officeid, Electronic_file, " +
            "Excludedfromiprcontrol, Outsourced) VALUES (?, ?, ?, NEWID(), 1, 1, 0, 0)")
        .params(
            patrawinCase.getCaseNumber(),
            patrawinCase.getDescription(),
            dbDateTimeUtcFormatter.format(patrawinCase.getCreationTime()))
        .run();

    // MSSQL's DATETIME are rounded to increments of .000, .003 or .007 seconds
    // https://docs.microsoft.com/en-us/sql/t-sql/data-types/datetime-transact-sql?view=sql-server-2017
    // Let's query the created case so we can have reference to the actual created date
    return fluentJdbc.query()
        .select("SELECT Arendenr, Slagord, Skapatdat FROM ARENDE_1 WHERE Arendenr = ?")
        .params(patrawinCase.getCaseNumber())
        .singleResult(rs -> ImmutableCase.builder()
            .caseNumber(rs.getString(1))
            .description(rs.getString(2))
            .creationTime(Instant.from(dbDateTimeUtcFormatter.parse(rs.getString(3))))
            .build());
  }

  private Client createClient(Client client) {
    fluentJdbc.query()
        .update("INSERT INTO KUND_24 " +
            "(Kundnr, Kortnamnkund, Skapatdat, Valutakod, Landkod, Sprakkod, Rowguid, Einvoicetype, Xmlinvoicetypeid, " +
            "Einvoiceaccent, Enableipforecaster, Automatfakturajn, Usebasicoutsourcingsurcharge, IsAgentInFile) " +
            "VALUES (?, ?, ?, 'N', 'N', 'N', NEWID(), 0, 0, 0, 0, 'N', 0, 0)")
        .params(
            client.getClientId(),
            client.getAlias(),
            dbDateTimeUtcFormatter.format(client.getCreationTime()))
        .run();

    // MSSQL's DATETIME are rounded to increments of .000, .003 or .007 seconds
    // https://docs.microsoft.com/en-us/sql/t-sql/data-types/datetime-transact-sql?view=sql-server-2017
    // Let's query the created case so we can have reference to the actual created date
    return fluentJdbc.query()
        .select("SELECT Kundnr, Kortnamnkund, Skapatdat FROM KUND_24 WHERE Kundnr = ?")
        .params(client.getClientId())
        .singleResult(rs -> ImmutableClient.builder()
            .clientId(rs.getString(1))
            .alias(rs.getString(2))
            .creationTime(Instant.from(dbDateTimeUtcFormatter.parse(rs.getString(3))))
            .build());
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