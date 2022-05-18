/*
 * Copyright (c) 2019 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector.patrawin.persistence;

import static io.wisetime.connector.patrawin.ConnectorLauncher.PatrawinConnectorConfigKey.PATRAWIN_DB_PASSWORD;
import static io.wisetime.connector.patrawin.ConnectorLauncher.PatrawinConnectorConfigKey.PATRAWIN_DB_USER;
import static io.wisetime.connector.patrawin.ConnectorLauncher.PatrawinConnectorConfigKey.PATRAWIN_JDBC_URL;
import static io.wisetime.connector.patrawin.persistence.PatrawinDao.MIN_SQL_DATE_TIME;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.javafaker.Faker;
import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.zaxxer.hikari.HikariDataSource;
import io.wisetime.connector.config.RuntimeConfig;
import io.wisetime.connector.patrawin.ConnectorLauncher;
import io.wisetime.connector.patrawin.fake.FakeCaseClientGenerator;
import io.wisetime.connector.patrawin.fake.FakeTimeGroupGenerator;
import io.wisetime.connector.patrawin.model.Case;
import io.wisetime.connector.patrawin.model.Client;
import io.wisetime.connector.patrawin.model.Worklog;
import io.wisetime.connector.patrawin.persistence.PatrawinDao.ActivityTypeLabel;
import io.wisetime.connector.patrawin.util.TimeDbFormatter;
import io.wisetime.generated.connect.User;
import io.wisetime.test_docker.ContainerRuntimeSpec;
import io.wisetime.test_docker.DockerLauncher;
import io.wisetime.test_docker.containers.SqlServer;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.inject.Inject;
import lombok.experimental.Accessors;
import org.assertj.core.data.TemporalUnitLessThanOffset;
import org.codejargon.fluentjdbc.api.FluentJdbc;
import org.codejargon.fluentjdbc.api.FluentJdbcBuilder;
import org.codejargon.fluentjdbc.api.query.Query;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author shane.xie@practiceinsight.io
 * @author galya.bogdanova@m.practiceinsight.io
 */
class PatrawinDaoTest {

  private static final SqlServer SQL_SERVER_CONTAINER_DEFINITION = new SqlServer();
  private static JdbcDatabaseContainer sqlServerContainer;
  private static FakeCaseClientGenerator fakeCaseClientGenerator = new FakeCaseClientGenerator();
  private static FakeTimeGroupGenerator fakeTimeGroupGenerator = new FakeTimeGroupGenerator();
  private static Faker faker = new Faker();

  private static PatrawinDaoTestUtils patrawinDaoTestUtils;
  private static PatrawinDao patrawinDao;
  private static FluentJdbc fluentJdbc;
  private static TimeDbFormatter timeDbFormatter;


  @BeforeAll
  static void setUp() {
    sqlServerContainer = getContainer();
    RuntimeConfig.setProperty(PATRAWIN_JDBC_URL, sqlServerContainer.getJdbcUrl());
    RuntimeConfig.setProperty(PATRAWIN_DB_USER, SQL_SERVER_CONTAINER_DEFINITION.getUsername());
    RuntimeConfig.setProperty(PATRAWIN_DB_PASSWORD, SQL_SERVER_CONTAINER_DEFINITION.getPassword());

    final Injector injector = Guice.createInjector(
        new ConnectorLauncher.PatrawinDbModule(), new FlywayPatrawinTestDbModule()
    );

    patrawinDao = injector.getInstance(PatrawinDao.class);
    fluentJdbc = new FluentJdbcBuilder().connectionProvider(injector.getInstance(HikariDataSource.class)).build();
    // Apply DB schema to test db
    injector.getInstance(Flyway.class).migrate();

    timeDbFormatter = injector.getInstance(TimeDbFormatter.class);
    patrawinDaoTestUtils = new PatrawinDaoTestUtils(fluentJdbc, timeDbFormatter);

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

  @Test
  void canQueryDb() {
    assertThat(patrawinDao.canQueryDb())
        .as("should return true if connected to a database")
        .isTrue();
  }

  @Test
  void casesCount() {
    assertThat(patrawinDao.casesCount())
        .as("No cases have been saved yet")
        .isEqualTo(0);

    fakeCaseClientGenerator.randomCases(3)
        .forEach(patrawinDaoTestUtils::createCase);

    assertThat(patrawinDao.casesCount())
        .as("Should return the number of all saved cases")
        .isEqualTo(3);
  }

  @Test
  void clientsCount() {
    assertThat(patrawinDao.clientsCount())
        .as("No clients have been saved yet")
        .isEqualTo(0);

    fakeCaseClientGenerator.randomClients(5)
        .forEach(patrawinDaoTestUtils::createClient);

    assertThat(patrawinDao.clientsCount())
        .as("Should return the number of all saved clients")
        .isEqualTo(5);
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
    assertThat(patrawinDao.doesCaseExist(aCase.getNumber()))
        .isTrue();
  }

  @Test
  void doesClientExist() {
    final Client client = fakeCaseClientGenerator.randomClient();
    assertThat(patrawinDaoTestUtils.createClient(client))
        .isNotNull();
    assertThat(patrawinDao.doesClientExist(client.getNumber()))
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

    final Worklog worklog = new Worklog()
        .setCaseOrClientNumber(client.getNumber())
        .setUsernameOrEmail(user.getExternalId())
        .setActivityCode(activityCode)
        .setNarrative(faker.shakespeare().asYouLikeItQuote())
        .setStartTime(OffsetDateTime.now(ZoneOffset.UTC))
        .setDurationSeconds(2 * 60 * 60)
        .setChargeableTimeSeconds(2 * 60 * 60);

    patrawinDao.createWorklog(worklog);

    final PendingTime pendingTime = patrawinDaoTestUtils.getCreatedPendingTime(client.getNumber());
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
        .isCloseTo(worklog.getStartTime(), new TemporalUnitLessThanOffset(1, ChronoUnit.SECONDS));
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

    final Worklog worklog = new Worklog()
        .setCaseOrClientNumber(aCase.getNumber())
        .setUsernameOrEmail(user.getExternalId())
        .setActivityCode(activityCode)
        .setNarrative(faker.shakespeare().asYouLikeItQuote())
        .setStartTime(OffsetDateTime.now(ZoneOffset.UTC))
        .setDurationSeconds(2 * 60 * 60)
        .setChargeableTimeSeconds(2 * 60 * 60);

    patrawinDao.createWorklog(worklog);

    final PendingTime pendingTime = patrawinDaoTestUtils.getCreatedPendingTime(client.getNumber());
    assertThat(pendingTime.getUserId())
        .as("the user id of the provided username or email")
        .isEqualTo(userId);
    assertThat(pendingTime.getCaseNum())
        .as("should use specified case number")
        .isEqualTo(worklog.getCaseOrClientNumber());
    assertThat(pendingTime.getClientNum())
        .as("should use client number associated to the case")
        .isEqualTo(client.getNumber());
    assertThat(timeDbFormatter.parseOffsetDateTime(pendingTime.getStartTimeUtc() + " Z"))
        .as("should used correct start time in UTC")
        .isCloseTo(worklog.getStartTime(), new TemporalUnitLessThanOffset(1, ChronoUnit.SECONDS));
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
    final String creditCode = faker.letterify("?");
    patrawinDaoTestUtils.createCreditCode(creditCode, false);
    final Client client = fakeCaseClientGenerator.randomClient();
    patrawinDaoTestUtils.createClient(client, creditCode);

    final LocalDateTime now = LocalDateTime.now();
    final Case createdNow1 = patrawinDaoTestUtils
        .createCase(fakeCaseClientGenerator.randomCase(now).number("B1234").build());
    patrawinDaoTestUtils.linkCaseToClient(createdNow1, client);

    final Case createdNow2 = patrawinDaoTestUtils
        .createCase(fakeCaseClientGenerator.randomCase(now).number("A1234").build());
    patrawinDaoTestUtils.linkCaseToClient(createdNow2, client);

    final Case createdYesterday = patrawinDaoTestUtils
        .createCase(fakeCaseClientGenerator.randomCase(now.minus(1, ChronoUnit.DAYS)).build());
    patrawinDaoTestUtils.linkCaseToClient(createdYesterday, client);

    final Case createdLastWeek = patrawinDaoTestUtils
        .createCase(fakeCaseClientGenerator.randomCase(now.minus(7, ChronoUnit.DAYS)).build());
    patrawinDaoTestUtils.linkCaseToClient(createdLastWeek, client);

    final Case createdLongTimeAgo = patrawinDaoTestUtils
        .createCase(fakeCaseClientGenerator.randomCase(MIN_SQL_DATE_TIME).build());
    patrawinDaoTestUtils.linkCaseToClient(createdLongTimeAgo, client);

    // initial query
    final List<Case> initialClients = patrawinDao.findCasesOrderedByCreationTime(
        Optional.of(now.minus(7, ChronoUnit.DAYS)), Optional.empty(), 3
    );

    assertThat(initialClients)
        .as("cases created last 2 weeks is < the creation time param")
        .doesNotContain(createdLongTimeAgo);
    assertThat(initialClients)
        .as("correct cases should be retrieved in right order")
        .containsExactly(createdLastWeek, createdYesterday, createdNow2);

    // succeeding query
    final List<Case> nextCases = patrawinDao.findCasesOrderedByCreationTime(
        Optional.of(now), Optional.of(createdNow2.getNumber()), 3
    );

    assertThat(nextCases)
        .as("should only contain the matching cases excluding those case numbers specified")
        .containsExactly(createdNow1);
  }

  @Test
  void findCasesOrderedByCreationTime_start_at_min_date() {
    final String creditCode = faker.letterify("?");
    patrawinDaoTestUtils.createCreditCode(creditCode, false);
    final Client client = fakeCaseClientGenerator.randomClient();
    patrawinDaoTestUtils.createClient(client, creditCode);

    final Case existingCase = patrawinDaoTestUtils
        .createCase(fakeCaseClientGenerator.randomCase(MIN_SQL_DATE_TIME).build());

    patrawinDaoTestUtils.linkCaseToClient(existingCase, client);

    final List<Case> initialClients = patrawinDao.findCasesOrderedByCreationTime(
        Optional.empty(), Optional.empty(), 3
    );

    assertThat(initialClients)
        .as("When no creation time provided, it should retrieve cases from the minimum date supported")
        .containsExactly(existingCase);
  }

  @Test
  void findCasesOrderedByCreationTimeForNonBlockedClients_not_finding_blocked() {
    final String creditCode = faker.letterify("?");
    final Client client = fakeCaseClientGenerator.randomClient();
    patrawinDaoTestUtils.createCreditCode(creditCode, true);
    patrawinDaoTestUtils.createClient(client, creditCode);

    final LocalDateTime now = LocalDateTime.now();
    Case patrawinCase = patrawinDaoTestUtils.createCase(fakeCaseClientGenerator.randomCase(now).number("B1234").build());
    patrawinDaoTestUtils.linkCaseToClient(patrawinCase, client);
    // initial query
    final List<Case> cases = patrawinDao.findCasesOrderedByCreationTime(
        Optional.of(now.minus(7, ChronoUnit.DAYS)), Optional.empty(), 100
    );

    assertThat(cases).isEmpty();
  }

  @Test
  void findClientsOrderedByCreationTime() {
    final String creditCode = faker.letterify("?");
    patrawinDaoTestUtils.createCreditCode(creditCode, false);
    final LocalDateTime now = LocalDateTime.now();
    final Client createdNow1 = patrawinDaoTestUtils
        .createClient(fakeCaseClientGenerator.randomClient(now).number("123").build(), creditCode);
    final Client createdNow2 = patrawinDaoTestUtils
        .createClient(fakeCaseClientGenerator.randomClient(now).number("122").build(), creditCode);
    final Client createdYesterday = patrawinDaoTestUtils
        .createClient(fakeCaseClientGenerator.randomClient(now.minus(1, ChronoUnit.DAYS)).build(), creditCode);
    final Client createdLastWeek = patrawinDaoTestUtils
        .createClient(fakeCaseClientGenerator.randomClient(now.minus(7, ChronoUnit.DAYS)).build(), creditCode);
    final Client createdLongTimeAgo = patrawinDaoTestUtils
        .createClient(fakeCaseClientGenerator.randomClient(MIN_SQL_DATE_TIME).build(), creditCode);

    // initial query
    final List<Client> initialClients = patrawinDao.findClientsOrderedByCreationTime(
        Optional.of(now.minus(7, ChronoUnit.DAYS)), Optional.empty(), 3
    );

    assertThat(initialClients)
        .as("clients with null as created date < the creation time param")
        .doesNotContain(createdLongTimeAgo);
    assertThat(initialClients)
        .as("correct clients should be retrieved in right order")
        .containsExactly(createdLastWeek, createdYesterday, createdNow2);

    // succeeding query
    final List<Client> nextClients = patrawinDao.findClientsOrderedByCreationTime(
        Optional.of(now), Optional.of(createdNow2.getNumber()), 3
    );

    assertThat(nextClients)
        .as("should only contain the matching clients excluding those client IDs specified")
        .containsExactly(createdNow1);
  }

  @Test
  void findClientsOrderedByCreationTime_start_at_min_date() {
    final String creditCode = faker.letterify("?");
    patrawinDaoTestUtils.createCreditCode(creditCode, false);
    final Client existingClient = patrawinDaoTestUtils
        .createClient(fakeCaseClientGenerator.randomClient(MIN_SQL_DATE_TIME).build(), creditCode);

    final List<Client> initialClients = patrawinDao.findClientsOrderedByCreationTime(
        Optional.empty(), Optional.empty(), 3
    );

    assertThat(initialClients)
        .as("When no creation time provided, it should retrieve clients from the minimum date supported")
        .containsExactly(existingClient);
  }

  @Test
  void findClientsOrderedByCreationTime_blocked_not_found() {
    final String creditCode = faker.letterify("?");
    patrawinDaoTestUtils.createCreditCode(creditCode, true);

    final LocalDateTime now = LocalDateTime.now();
    patrawinDaoTestUtils
        .createClient(fakeCaseClientGenerator.randomClient(now).build(), creditCode);

    final List<Client> initialClients = patrawinDao.findClientsOrderedByCreationTime(
        Optional.of(now.minus(7, ChronoUnit.DAYS)), Optional.empty(), 100
    );

    assertThat(initialClients)
        .as("Blocked clients should not be found")
        .isEmpty();
  }

  @Test
  void findCasesOfBlockedClientsOrderedByCreationTime() {
    final String creditCode = faker.letterify("?");
    patrawinDaoTestUtils.createCreditCode(creditCode, true);
    final Client client = fakeCaseClientGenerator.randomClient();
    patrawinDaoTestUtils.createClient(client, creditCode);

    final LocalDateTime now = LocalDateTime.now();
    final Case createdNow1 = patrawinDaoTestUtils
        .createCase(fakeCaseClientGenerator.randomCase(now).number("B1234").build());
    patrawinDaoTestUtils.linkCaseToClient(createdNow1, client);

    final Case createdNow2 = patrawinDaoTestUtils
        .createCase(fakeCaseClientGenerator.randomCase(now).number("A1234").build());
    patrawinDaoTestUtils.linkCaseToClient(createdNow2, client);

    final Case createdYesterday = patrawinDaoTestUtils
        .createCase(fakeCaseClientGenerator.randomCase(now.minus(1, ChronoUnit.DAYS)).build());
    patrawinDaoTestUtils.linkCaseToClient(createdYesterday, client);

    final Case createdLastWeek = patrawinDaoTestUtils
        .createCase(fakeCaseClientGenerator.randomCase(now.minus(7, ChronoUnit.DAYS)).build());
    patrawinDaoTestUtils.linkCaseToClient(createdLastWeek, client);

    final Case createdLongTimeAgo = patrawinDaoTestUtils
        .createCase(fakeCaseClientGenerator.randomCase(MIN_SQL_DATE_TIME).build());
    patrawinDaoTestUtils.linkCaseToClient(createdLongTimeAgo, client);

    // initial query
    final List<Case> initialClients = patrawinDao.findCasesOfBlockedClientsOrderedByCreationTime(
        Optional.of(now.minus(7, ChronoUnit.DAYS)), Optional.empty(), 3
    );

    assertThat(initialClients)
        .as("cases created last 2 weeks is < the creation time param")
        .doesNotContain(createdLongTimeAgo);
    assertThat(initialClients)
        .as("correct cases should be retrieved in right order")
        .containsExactly(createdLastWeek, createdYesterday, createdNow2);

    // succeeding query
    final List<Case> nextCases = patrawinDao.findCasesOfBlockedClientsOrderedByCreationTime(
        Optional.of(now), Optional.of(createdNow2.getNumber()), 3
    );

    assertThat(nextCases)
        .as("should only contain the matching cases excluding those case numbers specified")
        .containsExactly(createdNow1);
  }

  @Test
  void findCasesOfBlockedClientsOrderedByCreationTime_start_at_min_date() {
    final String creditCode = faker.letterify("?");
    patrawinDaoTestUtils.createCreditCode(creditCode, true);
    final Client client = fakeCaseClientGenerator.randomClient();
    patrawinDaoTestUtils.createClient(client, creditCode);

    final Case existingCase = patrawinDaoTestUtils
        .createCase(fakeCaseClientGenerator.randomCase(MIN_SQL_DATE_TIME).build());

    patrawinDaoTestUtils.linkCaseToClient(existingCase, client);

    final List<Case> initialClients = patrawinDao.findCasesOfBlockedClientsOrderedByCreationTime(
        Optional.empty(), Optional.empty(), 3
    );

    assertThat(initialClients)
        .as("When no creation time provided, it should retrieve cases from the minimum date supported")
        .containsExactly(existingCase);
  }

  @Test
  void findCasesOfBlockedClientsOrderedByCreationTimeForNonBlockedClients_not_finding_non_blocked() {
    final String creditCode = faker.letterify("?");
    final Client client = fakeCaseClientGenerator.randomClient();
    patrawinDaoTestUtils.createCreditCode(creditCode, false);
    patrawinDaoTestUtils.createClient(client, creditCode);

    final LocalDateTime now = LocalDateTime.now();
    Case patrawinCase = patrawinDaoTestUtils.createCase(fakeCaseClientGenerator.randomCase(now).number("B1234").build());
    patrawinDaoTestUtils.linkCaseToClient(patrawinCase, client);
    // initial query
    final List<Case> cases = patrawinDao.findCasesOfBlockedClientsOrderedByCreationTime(
        Optional.of(now.minus(7, ChronoUnit.DAYS)), Optional.empty(), 100
    );

    assertThat(cases).isEmpty();
  }

  @Test
  void findBlockedClientsOrderedByCreationTime() {
    final String creditCode = faker.letterify("?");
    patrawinDaoTestUtils.createCreditCode(creditCode, true);
    final LocalDateTime now = LocalDateTime.now();
    final Client createdNow1 = patrawinDaoTestUtils
        .createClient(fakeCaseClientGenerator.randomClient(now).number("123").build(), creditCode);
    final Client createdNow2 = patrawinDaoTestUtils
        .createClient(fakeCaseClientGenerator.randomClient(now).number("122").build(), creditCode);
    final Client createdYesterday = patrawinDaoTestUtils
        .createClient(fakeCaseClientGenerator.randomClient(now.minus(1, ChronoUnit.DAYS)).build(), creditCode);
    final Client createdLastWeek = patrawinDaoTestUtils
        .createClient(fakeCaseClientGenerator.randomClient(now.minus(7, ChronoUnit.DAYS)).build(), creditCode);
    final Client createdLongTimeAgo = patrawinDaoTestUtils
        .createClient(fakeCaseClientGenerator.randomClient(MIN_SQL_DATE_TIME).build(), creditCode);

    // initial query
    final List<Client> initialClients = patrawinDao.findBlockedClientsOrderedByCreationTime(
        Optional.of(now.minus(7, ChronoUnit.DAYS)), Optional.empty(), 3
    );

    assertThat(initialClients)
        .as("clients with null as created date < the creation time param")
        .doesNotContain(createdLongTimeAgo);
    assertThat(initialClients)
        .as("correct clients should be retrieved in right order")
        .containsExactly(createdLastWeek, createdYesterday, createdNow2);

    // succeeding query
    final List<Client> nextClients = patrawinDao.findBlockedClientsOrderedByCreationTime(
        Optional.of(now), Optional.of(createdNow2.getNumber()), 3
    );

    assertThat(nextClients)
        .as("should only contain the matching clients excluding those client IDs specified")
        .containsExactly(createdNow1);
  }

  @Test
  void findBlockedClientsOrderedByCreationTime_start_at_min_date() {
    final String creditCode = faker.letterify("?");
    patrawinDaoTestUtils.createCreditCode(creditCode, true);
    final Client existingClient = patrawinDaoTestUtils
        .createClient(fakeCaseClientGenerator.randomClient(MIN_SQL_DATE_TIME).build(), creditCode);

    final List<Client> initialClients = patrawinDao.findBlockedClientsOrderedByCreationTime(
        Optional.empty(), Optional.empty(), 3
    );

    assertThat(initialClients)
        .as("When no creation time provided, it should retrieve clients from the minimum date supported")
        .containsExactly(existingClient);
  }

  @Test
  void findBlockedClientsOrderedByCreationTime_non_blocked_not_found() {
    final String creditCode = faker.letterify("?");
    patrawinDaoTestUtils.createCreditCode(creditCode, false);

    final LocalDateTime now = LocalDateTime.now();
    patrawinDaoTestUtils
        .createClient(fakeCaseClientGenerator.randomClient(now).build(), creditCode);

    final List<Client> initialClients = patrawinDao.findBlockedClientsOrderedByCreationTime(
        Optional.of(now.minus(7, ChronoUnit.DAYS)), Optional.empty(), 100
    );

    assertThat(initialClients)
        .as("Blocked clients should not be found")
        .isEmpty();
  }

  @Test
  void hasExpectedSchema() {
    assertThat(patrawinDao.hasExpectedSchema())
        .as("Flyway should freshly applied the expected Patrawin DB schema")
        .isTrue();
  }

  @Test
  void getEarliestUnprocessedTime() {

    assertThat(patrawinDao.getEarliestUnprocessedTime(5))
        .as("Unprocessed cases do not exit")
        .isEmpty();

    final Case aCase = fakeCaseClientGenerator.randomCase();
    final Client client = fakeCaseClientGenerator.randomClient();
    patrawinDaoTestUtils.createCaseWithClient(aCase, client);

    final User user = fakeTimeGroupGenerator.randomUser();
    final long userId = patrawinDaoTestUtils.createUser(user);

    final int activityCode = faker.number().numberBetween(1, 5);
    assertThat(patrawinDaoTestUtils.createActivityCode(activityCode))
        .isTrue();
    OffsetDateTime dateTime = OffsetDateTime.now(ZoneOffset.UTC);
    final Worklog worklog = new Worklog()
        .setCaseOrClientNumber(aCase.getNumber())
        .setUsernameOrEmail(user.getExternalId())
        .setActivityCode(activityCode)
        .setNarrative(faker.shakespeare().asYouLikeItQuote())
        .setStartTime(dateTime)
        .setDurationSeconds(2 * 60 * 60)
        .setChargeableTimeSeconds(2 * 60 * 60);

    patrawinDao.createWorklog(worklog);
    assertThat(patrawinDao.getEarliestUnprocessedTime(-5).get().toEpochSecond(ZoneOffset.UTC))
        .as("Unprocessed cases exit")
        .isEqualTo(dateTime.toEpochSecond());
  }

  @Test
  void hasUnprocessedTime() {

    assertThat(patrawinDao.hasUnprocessedTime(5))
        .as("Unprocessed cases do not exit")
        .isFalse();

    final Case aCase = fakeCaseClientGenerator.randomCase();
    final Client client = fakeCaseClientGenerator.randomClient();
    patrawinDaoTestUtils.createCaseWithClient(aCase, client);

    final User user = fakeTimeGroupGenerator.randomUser();
    patrawinDaoTestUtils.createUser(user);

    final int activityCode = faker.number().numberBetween(1, 5);
    assertThat(patrawinDaoTestUtils.createActivityCode(activityCode))
        .isTrue();

    final Worklog worklog = new Worklog()
        .setCaseOrClientNumber(aCase.getNumber())
        .setUsernameOrEmail(user.getExternalId())
        .setActivityCode(activityCode)
        .setNarrative(faker.shakespeare().asYouLikeItQuote())
        .setStartTime(OffsetDateTime.now(ZoneOffset.UTC))
        .setDurationSeconds(2 * 60 * 60)
        .setChargeableTimeSeconds(2 * 60 * 60);

    patrawinDao.createWorklog(worklog);
    assertThat(patrawinDao.hasUnprocessedTime(-5))
        .as("Unprocessed cases exit")
        .isTrue();
  }

  @Test
  void findActivityTypeLabels() {
    IntStream.range(11, 31).forEach(i -> patrawinDaoTestUtils.createActivityTypeLabel(i));
    assertThat(patrawinDao.findActivityTypeLabels(0, 100))
        .as("all activity type labels should be returned")
        .hasSize(20);
    assertThat(patrawinDao.findActivityTypeLabels(0, 10))
        .as("10 activity type labels should be returned")
        .hasSize(10);
    assertThat(patrawinDao
        .findActivityTypeLabels(0, 10)
        .stream()
        .map(ActivityTypeLabel::getId)
        .collect(Collectors.toList()))
        .containsExactlyElementsOf(IntStream.range(11, 21)
            .mapToObj(String::valueOf)
            .collect(Collectors.toList()));
    assertThat(patrawinDao.findActivityTypeLabels(10, 10))
        .as("10 activity type labels should be returned")
        .hasSize(10);
    assertThat(patrawinDao
        .findActivityTypeLabels(10, 10)
        .stream()
        .map(ActivityTypeLabel::getId)
        .collect(Collectors.toList()))
        .containsExactlyElementsOf(IntStream.range(21, 31)
            .mapToObj(String::valueOf)
            .collect(Collectors.toList()));
    assertThat(patrawinDao.findActivityTypeLabels(20, 10))
        .as("0 activity type labels should be returned")
        .hasSize(0);
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
    ContainerRuntimeSpec container = DockerLauncher.instance().createContainer(SQL_SERVER_CONTAINER_DEFINITION);
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
      return SQL_SERVER_CONTAINER_DEFINITION.getUsername();
    }

    String getPassword() {
      return SQL_SERVER_CONTAINER_DEFINITION.getPassword();
    }
  }

  @lombok.Data
  @Accessors(chain = true)
  public static class PendingTime {

    long userId;

    String caseNum;

    String clientNum;

    String startTimeUtc;

    int minutes;

    int serviceNum;

    String narrative;
  }
}
