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
import org.codejargon.fluentjdbc.api.query.Query;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;
import org.immutables.value.Value;
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

  private static PatrawinDaoTestUtils patrawinDaoTestUtils;
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
    patrawinDaoTestUtils = new PatrawinDaoTestUtils(fluentJdbc, timeDbFormatter);

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
    patrawinDaoTestUtils.createUser(user);
    assertThat(patrawinDao.doesUserExist(user.getEmail()))
        .as("User is in DB")
        .isTrue();
  }

  @Test
  void doesCaseExist() {
    final Case aCase = fakeCaseClientGenerator.randomCase();
    assertThat(patrawinDaoTestUtils.createCase(aCase))
        .isNotNull();
    assertThat(patrawinDao.doesCaseExist(aCase.getCaseNumber()))
        .isTrue();
  }

  @Test
  void doesClientExist() {
    final Client client = fakeCaseClientGenerator.randomClient();
    assertThat(patrawinDaoTestUtils.createClient(client))
        .isNotNull();
    assertThat(patrawinDao.doesClientExist(client.clientNumber()))
        .isTrue();
  }

  @Test
  void doesActivityCodeExist() {
    final int activityCode = 1;
    assertThat(patrawinDaoTestUtils.createActivityCode(activityCode))
        .isTrue();
    assertThat(patrawinDao.doesActivityCodeExist(activityCode))
        .isTrue();
  }

  @Test
  void createWorklog_client() {
    final String creditCode = faker.letterify("?");
    patrawinDaoTestUtils.createCreditCode(creditCode, false);

    final Client client = fakeCaseClientGenerator.randomClient();
    assertThat(patrawinDaoTestUtils.createClient(client, creditCode))
        .isNotNull();

    final User user = fakeTimeGroupGenerator.randomUser();
    final long userId = patrawinDaoTestUtils.createUser(user);

    final int activityCode = faker.number().numberBetween(1, 5);
    assertThat(patrawinDaoTestUtils.createActivityCode(activityCode))
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

    final PendingTime pendingTime = patrawinDaoTestUtils.getCreatedPendingTime(client.clientNumber());
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
    patrawinDaoTestUtils.createCaseWithClient(aCase, client);

    final User user = fakeTimeGroupGenerator.randomUser();
    final long userId = patrawinDaoTestUtils.createUser(user);

    final int activityCode = faker.number().numberBetween(1, 5);
    assertThat(patrawinDaoTestUtils.createActivityCode(activityCode))
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

    final PendingTime pendingTime = patrawinDaoTestUtils.getCreatedPendingTime(client.clientNumber());
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
    final Case createdNow1 = patrawinDaoTestUtils
        .createCase(ImmutableCase.copyOf(fakeCaseClientGenerator.randomCase(now)).withCaseNumber("B1234"));
    final Case createdNow2 = patrawinDaoTestUtils
        .createCase(ImmutableCase.copyOf(fakeCaseClientGenerator.randomCase(now)).withCaseNumber("A1234"));
    final Case createdYesterday = patrawinDaoTestUtils
        .createCase(fakeCaseClientGenerator.randomCase(now.minus(1, ChronoUnit.DAYS)));
    final Case createdLastWeek = patrawinDaoTestUtils
        .createCase(fakeCaseClientGenerator.randomCase(now.minus(7, ChronoUnit.DAYS)));
    final Case createdLast2Weeks = patrawinDaoTestUtils
        .createCase(fakeCaseClientGenerator.randomCase(now.minus(14, ChronoUnit.DAYS)));

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
    final Client createdNow1 = patrawinDaoTestUtils
        .createClient(ImmutableClient.copyOf(fakeCaseClientGenerator.randomClient(now)).withClientNumber("123"));
    final Client createdNow2 = patrawinDaoTestUtils
        .createClient(ImmutableClient.copyOf(fakeCaseClientGenerator.randomClient(now)).withClientNumber("122"));
    final Client createdYesterday = patrawinDaoTestUtils
        .createClient(fakeCaseClientGenerator.randomClient(now.minus(1, ChronoUnit.DAYS)));
    final Client createdLastWeek = patrawinDaoTestUtils
        .createClient(fakeCaseClientGenerator.randomClient(now.minus(7, ChronoUnit.DAYS)));
    final Client createdLast2Weeks = patrawinDaoTestUtils
        .createClient(fakeCaseClientGenerator.randomClient(now.minus(14, ChronoUnit.DAYS)));

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

  @Value.Immutable
  public interface PendingTime {
    long getUserId();

    String getCaseNum();

    String getClientNum();

    String getStartTimeUtc();

    int getMinutes();

    int getServiceNum();

    String getNarrative();
  }
}
