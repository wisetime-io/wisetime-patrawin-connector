/*
 * Copyright (c) 2019 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector.patrawin.persistence;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provider;

import com.github.javafaker.Faker;

import org.codejargon.fluentjdbc.api.FluentJdbc;
import org.codejargon.fluentjdbc.api.FluentJdbcBuilder;
import org.codejargon.fluentjdbc.api.mapper.Mappers;
import org.codejargon.fluentjdbc.api.query.Query;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MSSQLServerContainer;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;

import javax.inject.Inject;
import javax.sql.DataSource;

import io.wisetime.connector.config.RuntimeConfig;
import io.wisetime.connector.patrawin.ConnectorLauncher;
import io.wisetime.connector.patrawin.fake.FakeCaseClientGenerator;
import io.wisetime.connector.patrawin.fake.FakeTimeGroupGenerator;
import io.wisetime.connector.patrawin.model.Case;
import io.wisetime.connector.patrawin.model.Client;
import io.wisetime.connector.patrawin.model.ImmutableCase;
import io.wisetime.connector.patrawin.model.ImmutableClient;
import io.wisetime.connector.patrawin.model.ImmutableWorklog;
import io.wisetime.connector.patrawin.model.Worklog;
import io.wisetime.connector.patrawin.util.TimeDbFormatter;
import io.wisetime.generated.connect.User;

import static io.wisetime.connector.patrawin.ConnectorLauncher.PatrawinConnectorConfigKey.PATRAWIN_DB_PASSWORD;
import static io.wisetime.connector.patrawin.ConnectorLauncher.PatrawinConnectorConfigKey.PATRAWIN_DB_USER;
import static io.wisetime.connector.patrawin.ConnectorLauncher.PatrawinConnectorConfigKey.PATRAWIN_JDBC_URL;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author shane.xie@practiceinsight.io
 * @author galya.bogdanova@m.practiceinsight.io
 */
class PatrawinDaoTest {

  private static JdbcDatabaseContainer sqlServerContainer = new MSSQLServerContainer();
  private static FakeCaseClientGenerator fakeCaseClientGenerator = new FakeCaseClientGenerator();
  private static FakeTimeGroupGenerator fakeTimeGroupGenerator = new FakeTimeGroupGenerator();
  private static Faker faker = new Faker();

  private static PatrawinDao patrawinDao;
  private static FluentJdbc fluentJdbc;
  private static TimeDbFormatter timeDbFormatter;

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
    timeDbFormatter = injector.getInstance(TimeDbFormatter.class);

    // Apply DB schema to test db
    injector.getInstance(Flyway.class).migrate();
  }

  @BeforeEach
  void setupTests() {
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
  void doesUserExist() {
    final User user = fakeTimeGroupGenerator.randomUser();

    assertThat(patrawinDao.doesUserExist(user.getEmail()))
        .as("User not in DB")
        .isFalse();

    // Add user in DB
    createUser(user);
    assertThat(patrawinDao.doesUserExist(user.getEmail()))
        .as("User is in DB")
        .isTrue();
  }

  @Test
  void doesCaseExist() {
    final Case aCase = fakeCaseClientGenerator.randomCase();
    assertThat(createCase(aCase))
        .isNotNull();
    assertThat(patrawinDao.doesCaseExist(aCase.getCaseNumber()))
        .isTrue();
  }

  @Test
  void doesClientExist() {
    final Client client = fakeCaseClientGenerator.randomClient();
    assertThat(createClient(client))
        .isNotNull();
    assertThat(patrawinDao.doesClientExist(client.clientNumber()))
        .isTrue();
  }

  @Test
  void doesActivityCodeExist() {
    final int activityCode = 1;
    assertThat(createActivityCode(activityCode))
        .isTrue();
    assertThat(patrawinDao.doesActivityCodeExist(activityCode))
        .isTrue();
  }

  @Test
  void createWorklog_client() {
    final String creditCode = faker.letterify("?");
    createCreditCode(creditCode, false);

    final Client client = fakeCaseClientGenerator.randomClient();
    assertThat(createClient(client, creditCode))
        .isNotNull();

    final User user = fakeTimeGroupGenerator.randomUser();
    final long userId = createUser(user);

    final int activityCode = faker.number().numberBetween(1, 5);
    assertThat(createActivityCode(activityCode))
        .isTrue();

    final Worklog worklog = ImmutableWorklog.builder()
        .caseOrClientNumber(client.clientNumber())
        .usernameOrEmail(user.getExternalId())
        .activityCode(activityCode)
        .narrative(faker.shakespeare().asYouLikeItQuote())
        .startTime(OffsetDateTime.now(ZoneOffset.UTC))
        .durationSeconds(2 * 60 * 60)
        .chargeableTimeSeconds(2 * 60 * 60)
        .build();

    patrawinDao.createWorklog(worklog);

    final PendingTime pendingTime = getCreatedPendingTime(client.clientNumber());
    assertThat(pendingTime.getUserId())
        .as("the user id of the provided username or email")
        .isEqualTo(userId);
    assertThat(pendingTime.getClientNum())
        .as("should use client number")
        .isEqualTo(worklog.getCaseOrClientNumber());
    assertThat(pendingTime.getCaseNum())
        .as("blank because no client number is provided")
        .isBlank();
    assertThat(timeDbFormatter.parseOffsetDateTime(pendingTime.getStartTimeUtc() + " Z"))
        .as("should used correct start time in UTC")
        .isEqualTo(worklog.getStartTime());
    assertThat(pendingTime.getMinutes())
        .as("should correctly convert duration in seconds to minutes")
        .isEqualTo(worklog.getDurationSeconds() / 60);
    assertThat(pendingTime.getServiceNum())
        .as("should be equal to the activity code")
        .isEqualTo(worklog.getActivityCode());
    assertThat(pendingTime.getNarrative())
        .as("should used correct narrative")
        .isEqualTo(worklog.getNarrative());
  }

  @Test
  void createWorklog_case() {
    final Case aCase = fakeCaseClientGenerator.randomCase();
    final Client client = fakeCaseClientGenerator.randomClient();
    createCaseWithClient(aCase, client);

    final User user = fakeTimeGroupGenerator.randomUser();
    final long userId = createUser(user);

    final int activityCode = faker.number().numberBetween(1, 5);
    assertThat(createActivityCode(activityCode))
        .isTrue();

    final Worklog worklog = ImmutableWorklog.builder()
        .caseOrClientNumber(aCase.getCaseNumber())
        .usernameOrEmail(user.getExternalId())
        .activityCode(activityCode)
        .narrative(faker.shakespeare().asYouLikeItQuote())
        .startTime(OffsetDateTime.now(ZoneOffset.UTC))
        .durationSeconds(2 * 60 * 60)
        .chargeableTimeSeconds(2 * 60 * 60)
        .build();

    patrawinDao.createWorklog(worklog);

    final PendingTime pendingTime = getCreatedPendingTime(client.clientNumber());
    assertThat(pendingTime.getUserId())
        .as("the user id of the provided username or email")
        .isEqualTo(userId);
    assertThat(pendingTime.getCaseNum())
        .as("should use specified case number")
        .isEqualTo(worklog.getCaseOrClientNumber());
    assertThat(pendingTime.getClientNum())
        .as("should use client number associated to the case")
        .isEqualTo(client.clientNumber());
    assertThat(timeDbFormatter.parseOffsetDateTime(pendingTime.getStartTimeUtc() + " Z"))
        .as("should used correct start time in UTC")
        .isEqualTo(worklog.getStartTime());
    assertThat(pendingTime.getMinutes())
        .as("should correctly convert duration in seconds to minutes")
        .isEqualTo(worklog.getDurationSeconds() / 60);
    assertThat(pendingTime.getServiceNum())
        .as("should be equal to the activity code")
        .isEqualTo(worklog.getActivityCode());
    assertThat(pendingTime.getNarrative())
        .as("should used correct narrative")
        .isEqualTo(worklog.getNarrative());
  }

  @Test
  void findCasesOrderedByCreationTime() {
    final LocalDateTime now = LocalDateTime.now();
    final Case createdNow1 = createCase(ImmutableCase.copyOf(fakeCaseClientGenerator.randomCase(now))
        .withCaseNumber("B1234"));
    final Case createdNow2 = createCase(ImmutableCase.copyOf(fakeCaseClientGenerator.randomCase(now))
        .withCaseNumber("A1234"));
    final Case createdYesterday = createCase(fakeCaseClientGenerator.randomCase(now.minus(1, ChronoUnit.DAYS)));
    final Case createdLastWeek = createCase(fakeCaseClientGenerator.randomCase(now.minus(7, ChronoUnit.DAYS)));
    final Case createdLast2Weeks = createCase(fakeCaseClientGenerator.randomCase(now.minus(14, ChronoUnit.DAYS)));

    // initial query
    final List<Case> initialClients = patrawinDao.findCasesOrderedByCreationTime(
        now.minus(7, ChronoUnit.DAYS), Lists.newArrayList(), 3
    );

    assertThat(initialClients)
        .as("cases created last 2 weeks is < the creation time param")
        .doesNotContain(createdLast2Weeks);
    assertThat(initialClients)
        .as("correct cases should be retrieved in right order")
        .containsExactly(createdLastWeek, createdYesterday, createdNow2);

    // succeeding query
    final List<Case> nextCases = patrawinDao.findCasesOrderedByCreationTime(
        now, Lists.newArrayList(createdNow2.getCaseNumber()), 3
    );

    assertThat(nextCases)
        .as("should only contain the matching cases excluding those case numbers specified")
        .containsExactly(createdNow1);
  }

  @Test
  void findClientsOrderedByCreationTime() {
    final LocalDateTime now = LocalDateTime.now();
    final Client createdNow1 = createClient(ImmutableClient.copyOf(fakeCaseClientGenerator.randomClient(now))
        .withClientNumber("123"));
    final Client createdNow2 = createClient(ImmutableClient.copyOf(fakeCaseClientGenerator.randomClient(now))
        .withClientNumber("122"));
    final Client createdYesterday = createClient(fakeCaseClientGenerator.randomClient(now.minus(1, ChronoUnit.DAYS)));
    final Client createdLastWeek = createClient(fakeCaseClientGenerator.randomClient(now.minus(7, ChronoUnit.DAYS)));
    final Client createdLast2Weeks = createClient(fakeCaseClientGenerator.randomClient(now.minus(14, ChronoUnit.DAYS)));

    // initial query
    final List<Client> initialClients = patrawinDao.findClientsOrderedByCreationTime(
        now.minus(7, ChronoUnit.DAYS), Lists.newArrayList(), 3
    );

    assertThat(initialClients)
        .as("clients created last 2 weeks is < the creation time param")
        .doesNotContain(createdLast2Weeks);
    assertThat(initialClients)
        .as("correct clients should be retrieved in right order")
        .containsExactly(createdLastWeek, createdYesterday, createdNow2);

    // succeeding query
    final List<Client> nextClients = patrawinDao.findClientsOrderedByCreationTime(
        now, Lists.newArrayList(createdNow2.clientNumber()), 3
    );

    assertThat(nextClients)
        .as("should only contain the matching clients excluding those client IDs specified")
        .containsExactly(createdNow1);
  }

  @Test
  void hasExpectedSchema() {
    assertThat(patrawinDao.hasExpectedSchema())
        .as("Flyway should freshly applied the expected Patrawin DB schema")
        .isTrue();
  }

  private long createUser(User patrawinUser) {
    return fluentJdbc.query()
        .update("INSERT INTO BEHORIG_50 (Username, Email, Namn, Officeid, Isactive, Isattorney) " +
            "VALUES (?, ?, ?, 1, 1, 1)")
        .params(
            patrawinUser.getExternalId(),
            patrawinUser.getEmail(),
            patrawinUser.getName())
        .runFetchGenKeys(Mappers.singleLong())
        .generatedKeys()
        .get(0);
  }

  private boolean createActivityCode(int activityCode) {
    return createActivityCode(activityCode, true);
  }

  private boolean createActivityCode(int activityCode, boolean active) {
    return fluentJdbc.query()
        .update("INSERT INTO FAKTURATEXTNR_15 (Fakturatextnr, AmountIncludedInStatistics, " +
            "HoursIncludedInStatistics, Inaktiv) VALUES (?, 1, 1, ?)")
        .params(activityCode, active ? 0 : 1)
        .run()
        .affectedRows() == 1;
  }

  private Case createCase(Case patrawinCase) {
    fluentJdbc.query()
        .update("INSERT INTO ARENDE_1 (Arendenr, Slagord, Skapatdat, Rowguid, Officeid, Electronic_file, " +
            "Excludedfromiprcontrol, Outsourced) VALUES (?, ?, ?, NEWID(), 1, 1, 0, 0)")
        .params(
            patrawinCase.getCaseNumber(),
            patrawinCase.getDescription(),
            timeDbFormatter.format(patrawinCase.getCreationTime()))
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
            .creationTime(timeDbFormatter.parseDateTime(rs.getString(3)))
            .build());
  }

  private Client createClient(Client client) {
    return createClient(client, faker.letterify("?"));
  }

  private Client createClient(Client client, String creditCode) {
    fluentJdbc.query()
        .update("INSERT INTO KUND_24 " +
            "(Kundnr, Kortnamnkund, Skapatdat, Valutakod, Landkod, Sprakkod, Rowguid, Einvoicetype, Xmlinvoicetypeid, " +
            "Einvoiceaccent, Enableipforecaster, Automatfakturajn, Usebasicoutsourcingsurcharge, IsAgentInFile, Kreditjn) " +
            "VALUES (?, ?, ?, 'N', 'N', 'N', NEWID(), 0, 0, 0, 0, 'N', 0, 0, ?)")
        .params(
            client.clientNumber(),
            client.getAlias(),
            timeDbFormatter.format(client.getCreationTime()),
            creditCode)
        .run();

    // MSSQL's DATETIME are rounded to increments of .000, .003 or .007 seconds
    // https://docs.microsoft.com/en-us/sql/t-sql/data-types/datetime-transact-sql?view=sql-server-2017
    // Let's query the created case so we can have reference to the actual created date
    return fluentJdbc.query()
        .select("SELECT Kundnr, Kortnamnkund, Skapatdat FROM KUND_24 WHERE Kundnr = ?")
        .params(client.clientNumber())
        .singleResult(rs -> ImmutableClient.builder()
            .clientNumber(rs.getString(1))
            .alias(rs.getString(2))
            .creationTime(timeDbFormatter.parseDateTime(rs.getString(3)))
            .build());
  }

  private void createCreditCode(String creditCode, boolean blocked) {
    final int creditCodeType = blocked ? 2 : 0; // 2 means blocked

    // Set credit code of client as blocked
    fluentJdbc.query()
        .update("INSERT INTO CREDIT_LEVEL_334 (Creditcode, Type) VALUES (?, ?)")
        .params(creditCode, creditCodeType)
        .run();
  }

  private void createCaseWithClient(Case patrawinCase, Client client) {
    final String creditCode = faker.letterify("?");
    createCreditCode(creditCode, false);

    createCase(patrawinCase);
    createClient(client, creditCode);

    fluentJdbc.query()
        .update("INSERT INTO KUND_ARENDE_25 (Arendenr, Kundnr, Part, Kundtyp) VALUES (?, ?, 1, 2)")
        .params(patrawinCase.getCaseNumber(), client.clientNumber())
        .run();
  }

  private PendingTime getCreatedPendingTime(String clientNumber) {
    return fluentJdbc.query()
        .select("SELECT User_Id, Arendenr, Kundnr, StartTimeUtc, Minutes, Fakturatextnr, Text FROM PENDING_TIME_335 " +
            "WHERE Kundnr = ?")
        .params(clientNumber)
        .singleResult(rs ->
            new PendingTime()
                .setUserId(rs.getLong("User_Id"))
                .setCaseNum(rs.getString("Arendenr"))
                .setClientNum(rs.getString("Kundnr"))
                .setStartTimeUtc(rs.getString("StartTimeUtc"))
                .setMinutes(rs.getInt("Minutes"))
                .setServiceNum(rs.getInt("Fakturatextnr"))
                .setNarrative(rs.getString("Text"))
        );
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

  public class PendingTime {
    long userId;
    String caseNum;
    String clientNum;
    String startTimeUtc;
    int minutes;
    int serviceNum;
    String narrative;

    long getUserId() {
      return userId;
    }

    PendingTime setUserId(long userId) {
      this.userId = userId;
      return this;
    }

    String getCaseNum() {
      return caseNum;
    }

    PendingTime setCaseNum(String caseNum) {
      this.caseNum = caseNum;
      return this;
    }

    String getClientNum() {
      return clientNum;
    }

    PendingTime setClientNum(String clientNum) {
      this.clientNum = clientNum;
      return this;
    }

    String getStartTimeUtc() {
      return startTimeUtc;
    }

    PendingTime setStartTimeUtc(String startTimeUtc) {
      this.startTimeUtc = startTimeUtc;
      return this;
    }

    int getMinutes() {
      return minutes;
    }

    PendingTime setMinutes(int minutes) {
      this.minutes = minutes;
      return this;
    }

    int getServiceNum() {
      return serviceNum;
    }

    PendingTime setServiceNum(int serviceNum) {
      this.serviceNum = serviceNum;
      return this;
    }

    String getNarrative() {
      return narrative;
    }

    PendingTime setNarrative(String narrative) {
      this.narrative = narrative;
      return this;
    }
  }
}
