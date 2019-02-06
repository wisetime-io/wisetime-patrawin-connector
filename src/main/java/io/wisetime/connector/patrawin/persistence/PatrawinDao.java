/*
 * Copyright (c) 2019 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector.patrawin.persistence;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.codejargon.fluentjdbc.api.FluentJdbc;
import org.codejargon.fluentjdbc.api.FluentJdbcBuilder;
import org.codejargon.fluentjdbc.api.mapper.Mappers;
import org.codejargon.fluentjdbc.api.query.Query;
import org.codejargon.fluentjdbc.api.query.SelectQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import io.wisetime.connector.patrawin.model.Case;
import io.wisetime.connector.patrawin.model.Client;
import io.wisetime.connector.patrawin.model.ImmutableCase;
import io.wisetime.connector.patrawin.model.ImmutableClient;
import io.wisetime.connector.patrawin.model.ImmutableWorklog;
import io.wisetime.connector.patrawin.util.TimeDbFormatter;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

/**
 * Simple, unsophisticated access to the Patrawin database.
 *
 * @author shane.xie@practiceinsight.io
 */
public class PatrawinDao {

  private static final Logger log = LoggerFactory.getLogger(PatrawinDao.class);

  private static final String TABLE_NAME_CASE = "ARENDE_1";
  private static final String TABLE_NAME_CLIENT = "KUND_24";
  private static final String TABLE_NAME_USER = "BEHORIG_50";

  private final FluentJdbc fluentJdbc;
  private final TimeDbFormatter timeDbFormatter;

  @Inject
  public PatrawinDao(DataSource dataSource, TimeDbFormatter timeDbFormatter) {
    this.fluentJdbc = new FluentJdbcBuilder().connectionProvider(dataSource).build();
    this.timeDbFormatter = timeDbFormatter;
  }

  public void asTransaction(final Runnable runnable) {
    query().transaction().inNoResult(runnable);
  }

  /**
   * Find cases
   *
   * @param createdOnOrAfter    find cases created on or after this time
   * @param excludedCaseNumbers list of case numbers to exclude from results
   * @param maxResults          maximum number of cases to return
   * @return list of cases ordered by creation time ascending
   */
  public List<Case> findCasesOrderedByCreationTime(final Instant createdOnOrAfter, final List<String> excludedCaseNumbers,
                                                   final int maxResults) {
    final StringBuilder query = new StringBuilder(
        "SELECT TOP (:maxResults) Arendenr AS CaseNum, Slagord AS Description, Skapatdat AS CreatedDate FROM "
    );
    query.append(TABLE_NAME_CASE);
    query.append(" WHERE CreatedDate >= :createdOnOrAfter");
    if (!excludedCaseNumbers.isEmpty()) {
      query.append(" AND CaseNum NOT IN (:excludedCaseNumbers)");
    }
    query.append(" ORDER BY CreatedDate, CaseNum");

    final SelectQuery selectQuery = query().select(query.toString())
        .namedParam("maxResults", maxResults)
        .namedParam("createdOnOrAfter", timeDbFormatter.format(createdOnOrAfter));
    if (!excludedCaseNumbers.isEmpty()) {
      selectQuery.namedParam("excludedCaseNumbers", excludedCaseNumbers);
    }
    return selectQuery.listResult(rs -> ImmutableCase.builder()
        .id(rs.getString(1))
        .description(rs.getString(2))
        .creationTime(timeDbFormatter.parse(rs.getString(3)))
        .build()
    );
  }

  /**
   * Find clients
   *
   * @param createdOnOrAfter  find clients created on or after this time
   * @param excludedClientIds list of client IDs to exclude from results
   * @param maxResults        maximum number of clients to return
   * @return list of clients ordered by creation time ascending
   */
  public List<Client> findClientsOrderedByCreationTime(final Instant createdOnOrAfter, final List<String> excludedClientIds,
                                                       final int maxResults) {
    final StringBuilder query = new StringBuilder(
        "SELECT TOP (:maxResults) Kundnr AS ClientId, Kortnamnkund AS Alias, Skapatdat AS CreatedDate FROM "
    );
    query.append(TABLE_NAME_CLIENT);
    query.append(" WHERE CreatedDate >= :createdOnOrAfter");
    if (!excludedClientIds.isEmpty()) {
      query.append(" AND ClientId NOT IN (:excludedClientIds)");
    }
    query.append(" ORDER BY CreatedDate, ClientId");

    final SelectQuery selectQuery = query().select(query.toString())
        .namedParam("maxResults", maxResults)
        .namedParam("createdOnOrAfter", timeDbFormatter.format(createdOnOrAfter));
    if (!excludedClientIds.isEmpty()) {
      selectQuery.namedParam("excludedClientIds", excludedClientIds);
    }
    return selectQuery.listResult(rs -> ImmutableClient.builder()
        .id(rs.getString(1))
        .alias(rs.getString(2))
        .creationTime(timeDbFormatter.parse(rs.getString(3)))
        .build()
    );
  }

  public boolean doesUserExist(String usernameOrEmail) {
    return query().select("SELECT TOP 1 Username, Email FROM " + TABLE_NAME_USER +
        " WHERE Username = :usernameOrEmail OR Email = :usernameOrEmail")
        .namedParam("usernameOrEmail", usernameOrEmail)
        .firstResult(rs -> rs)
        .isPresent();
  }

  public boolean doesCaseExist(String caseNumber) {
    return query().select("SELECT TOP 1 Arendenr AS CaseNum FROM " + TABLE_NAME_CASE +
        " WHERE CaseNum = :caseNumber")
        .namedParam("caseNumber", caseNumber)
        .firstResult(rs -> rs)
        .isPresent();
  }

  public boolean doesClientExist(String clientId) {
    return query().select("SELECT TOP 1 Kundnr AS ClientId FROM " + TABLE_NAME_CLIENT +
        " WHERE ClientId = :clientId")
        .namedParam("clientId", clientId)
        .firstResult(rs -> rs)
        .isPresent();
  }

  /**
   * The parameters for post_time are as follows:
   *
   * @pnUserIdentityId int = null, -- not being set being used by the agent
   * @pnStaffMemberId int,
   * @pdtEntryDate datetime, -- in yyyy-MM-dd format
   * @pnCaseId int = null,
   * @psActivityCode nvarchar(6),
   * @pdtTimePeriod datetime, -- in 1899-01-01 HH:mm:ss:SSS format where the HH:mm:ss:SSS is the period between @pdtStartTime
   * and @pdtEndTime
   * @pdtStartTime datetime = null, -- in yyyy-MM-dd HH:mm:ss:SSS format
   * @pdtEndTime datetime = null, -- in yyyy-MM-dd HH:mm:ss:SSS format
   * @psNarrative nvarchar(max) = null
   */
  public void createWorklog(ImmutableWorklog worklog) {
    /*query().update("EXEC post_time " +
        "@pnStaffMemberId = :pnStaffMemberId, " +
        "@pdtEntryDate    = :pdtEntryDate, " +
        "@pnCaseId        = :pnCaseId, " +
        "@psActivityCode  = :psActivityCode, " +
        "@pdtTimePeriod   = :pdtTimePeriod, " +
        "@pdtStartTime    = :pdtStartTime, " +
        "@pdtEndTime      = :pdtEndTime, " +
        "@psNarrative     = :psNarrative ")
        .namedParam("pnStaffMemberId", worklog.getStaffKey())
        .namedParam("pdtEntryDate",
            ZonedDateTime.of(worklog.getEntryDate(), ZoneOffset.UTC)
                .withZoneSameInstant(getTimeZone())
                .format(DATE_FORMAT))
        .namedParam("pnCaseId", worklog.getCaseId())
        .namedParam("psActivityCode", worklog.getActivityCode())
        .namedParam("pdtTimePeriod", worklog.getTimePeriod().format(DATE_TIME_FORMAT))
        .namedParam("pdtStartTime",
            ZonedDateTime.of(worklog.getStartTime(), ZoneOffset.UTC)
                .withZoneSameInstant(getTimeZone())
                .format(DATE_TIME_FORMAT))
        .namedParam("pdtEndTime",
            ZonedDateTime.of(worklog.getEndTime(), ZoneOffset.UTC)
                .withZoneSameInstant(getTimeZone())
                .format(DATE_TIME_FORMAT))
        .namedParam("psNarrative", worklog.getNarrative())
        .run();*/
  }

  public boolean canQueryDb() {
    try {
      query().select("SELECT 1 FROM " + TABLE_NAME_CASE).firstResult(Mappers.singleString());
      return true;
    } catch (Exception ex) {
      return false;
    }
  }

  public boolean hasExpectedSchema() {
    log.info("Checking if Patrawin DB has correct schema...");

    final Map<String, Set<String>> requiredTablesAndColumnsMap = new HashMap<>();
    requiredTablesAndColumnsMap.put(
        TABLE_NAME_CASE,
        ImmutableSet.of("arendenr", "slagord", "skapatdat")
    );
    requiredTablesAndColumnsMap.put(
        TABLE_NAME_CLIENT,
        ImmutableSet.of("kundnr", "kortnamnkund", "skapatdat")
    );
    requiredTablesAndColumnsMap.put(
        TABLE_NAME_USER,
        ImmutableSet.of("Username", "Email")
    );

    final Map<String, List<String>> actualTablesAndColumnsMap = query().databaseInspection()
        .selectFromMetaData(meta -> meta.getColumns(null, null, null, null))
        .listResult(rs -> ImmutablePair.of(rs.getString("TABLE_NAME"), rs.getString("COLUMN_NAME")))
        .stream()
        .filter(pair -> requiredTablesAndColumnsMap.containsKey(pair.getKey().toLowerCase()))
        // transform to lower case to ensure we are comparing the same case
        .collect(groupingBy(pair -> pair.getKey().toLowerCase(), mapping(pair -> pair.getValue().toLowerCase(), toList())));

    return requiredTablesAndColumnsMap.entrySet().stream()
        .allMatch(entry -> actualTablesAndColumnsMap.containsKey(entry.getKey()) &&
            actualTablesAndColumnsMap.get(entry.getKey()).containsAll(entry.getValue())
        );
  }

  private Query query() {
    return fluentJdbc.query();
  }

}
