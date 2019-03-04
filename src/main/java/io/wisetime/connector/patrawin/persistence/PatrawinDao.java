/*
 * Copyright (c) 2019 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector.patrawin.persistence;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.codejargon.fluentjdbc.api.FluentJdbc;
import org.codejargon.fluentjdbc.api.FluentJdbcBuilder;
import org.codejargon.fluentjdbc.api.mapper.Mappers;
import org.codejargon.fluentjdbc.api.query.Query;
import org.codejargon.fluentjdbc.api.query.SelectQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import io.wisetime.connector.patrawin.model.Case;
import io.wisetime.connector.patrawin.model.Client;
import io.wisetime.connector.patrawin.model.ImmutableCase;
import io.wisetime.connector.patrawin.model.ImmutableClient;
import io.wisetime.connector.patrawin.model.Worklog;
import io.wisetime.connector.patrawin.util.TimeDbFormatter;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

/**
 * Simple, unsophisticated access to the Patrawin database.
 *
 * @author shane.xie@practiceinsight.io
 * @author galya.bogdanova@m.practiceinsight.io
 */
public class PatrawinDao {

  private static final Logger log = LoggerFactory.getLogger(PatrawinDao.class);

  private static final String TABLE_NAME_CASE = "ARENDE_1";
  private static final String TABLE_NAME_CLIENT = "KUND_24";
  private static final String TABLE_NAME_USER = "BEHORIG_50";
  private static final String TABLE_NAME_ACTIVITY_CODE = "FAKTURATEXTNR_15";

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
  public List<Case> findCasesOrderedByCreationTime(final LocalDateTime createdOnOrAfter,
                                                   final List<String> excludedCaseNumbers,
                                                   final int maxResults) {
    final StringBuilder query = new StringBuilder(
        "SELECT Arendenr AS CaseNum, Slagord AS Description, Skapatdat AS CreatedDate FROM "
    );
    query.append(TABLE_NAME_CASE);
    query.append(" WHERE Skapatdat >= :createdOnOrAfter");
    if (!excludedCaseNumbers.isEmpty()) {
      query.append(" AND Arendenr NOT IN (:excludedCaseNumbers)");
    }
    query.append(" ORDER BY CreatedDate, CaseNum");

    final SelectQuery selectQuery = query().select(query.toString())
        .namedParam("maxResults", maxResults)
        .namedParam("createdOnOrAfter", timeDbFormatter.format(createdOnOrAfter))
        .maxRows((long) maxResults);
    if (!excludedCaseNumbers.isEmpty()) {
      selectQuery.namedParam("excludedCaseNumbers", excludedCaseNumbers);
    }
    return selectQuery.listResult(rs -> ImmutableCase.builder()
        .caseNumber(rs.getString(1))
        .description(StringUtils.trimToEmpty(rs.getString(2)))
        .creationTime(timeDbFormatter.parseDateTime(rs.getString(3)))
        .build()
    );
  }

  /**
   * Find clients
   *
   * @param createdOnOrAfter      find clients created on or after this time
   * @param excludedClientNumbers list of client IDs to exclude from results
   * @param maxResults            maximum number of clients to return
   * @return list of clients ordered by creation time ascending
   */
  public List<Client> findClientsOrderedByCreationTime(final LocalDateTime createdOnOrAfter,
                                                       final List<String> excludedClientNumbers,
                                                       final int maxResults) {
    final StringBuilder query = new StringBuilder(
        "SELECT Kundnr AS ClientNumber, Kortnamnkund AS Alias, Skapatdat AS CreatedDate FROM "
    );
    query.append(TABLE_NAME_CLIENT);
    query.append(" WHERE Skapatdat >= :createdOnOrAfter");
    if (!excludedClientNumbers.isEmpty()) {
      query.append(" AND Kundnr NOT IN (:excludedClientNumbers)");
    }
    query.append(" ORDER BY CreatedDate, ClientNumber");

    final SelectQuery selectQuery = query().select(query.toString())
        .namedParam("maxResults", maxResults)
        .namedParam("createdOnOrAfter", timeDbFormatter.format(createdOnOrAfter))
        .maxRows((long) maxResults);
    if (!excludedClientNumbers.isEmpty()) {
      selectQuery.namedParam("excludedClientNumbers", excludedClientNumbers);
    }
    return selectQuery.listResult(rs -> ImmutableClient.builder()
        .clientNumber(rs.getString(1))
        .alias(StringUtils.trimToEmpty(rs.getString(2)))
        .creationTime(timeDbFormatter.parseDateTime(rs.getString(3)))
        .build()
    );
  }

  public boolean doesUserExist(String usernameOrEmail) {
    return query().select("SELECT Username, Email FROM " + TABLE_NAME_USER +
        " WHERE Username = :usernameOrEmail OR Email = :usernameOrEmail")
        .namedParam("usernameOrEmail", usernameOrEmail)
        .maxRows(1L)
        .firstResult(rs -> rs)
        .isPresent();
  }

  public boolean doesCaseExist(String caseNumber) {
    return query().select("SELECT Arendenr AS CaseNum FROM " + TABLE_NAME_CASE +
        " WHERE Arendenr = :caseNumber")
        .namedParam("caseNumber", caseNumber)
        .maxRows(1L)
        .firstResult(rs -> rs)
        .isPresent();
  }

  public boolean doesClientExist(String clientNumber) {
    return query().select("SELECT Kundnr AS ClientNumber FROM " + TABLE_NAME_CLIENT +
        " WHERE Kundnr = :clientNumber")
        .namedParam("clientNumber", clientNumber)
        .maxRows(1L)
        .firstResult(rs -> rs)
        .isPresent();
  }

  public boolean doesActivityCodeExist(int activityCode) {
    return query().select("SELECT Fakturatextnr AS ActivityCode FROM " + TABLE_NAME_ACTIVITY_CODE +
        " WHERE Fakturatextnr = :activityCode")
        .namedParam("activityCode", activityCode)
        .maxRows(1L)
        .firstResult(rs -> rs)
        .isPresent();
  }

  /**
   * The parameters for pw_PostTime are as follows:
   *
   * @case_or_client_id nvarchar
   * @username_or_email nvarchar
   * @activity_code int
   * @narrative nvarchar(max)
   * @narrative_internal_note nvarchar
   * @start_time datetimeoffset
   * @total_time_secs bigint
   * @chargeable_time_secs bigint
   *
   * Return codes: SUCCESS, CASE_OR_CLIENT_ID_NOT_FOUND, USER_NOT_FOUND, ACTIVITY_CODE_NOT_FOUND
   */
  public void createWorklog(Worklog worklog) {
    final int recordIdOrFailureStatus = query().plainConnection(con -> {
      try (CallableStatement cs = con.prepareCall("{ ? = call pw_PostTime(?, ?, ?, ?, ?, ?, ?, ?) }")) {
        cs.registerOutParameter(1, Types.INTEGER);
        cs.setString(2, worklog.getCaseOrClientNumber());
        cs.setString(3, worklog.getUsernameOrEmail());
        cs.setInt(4, worklog.getActivityCode());
        cs.setString(5, worklog.getNarrative());
        cs.setString(6, "");
        cs.setString(7, timeDbFormatter.format(worklog.getStartTime()));
        cs.setInt(8, worklog.getDurationSeconds());
        cs.setInt(9, worklog.getChargeableTimeSeconds());
        cs.execute();
        return cs.getInt(1);
      } catch (SQLException ex) {
        throw new RuntimeException(ex);
      }
    });

    // this will throw IllegalStateException if posting time is not successful.
    verifyPostingTimeIsSuccessful(recordIdOrFailureStatus);
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
        ImmutableSet.of("Arendenr", "Slagord", "Skapatdat")
    );
    requiredTablesAndColumnsMap.put(
        TABLE_NAME_CLIENT,
        ImmutableSet.of("Kundnr", "Kortnamnkund", "Skapatdat")
    );
    requiredTablesAndColumnsMap.put(
        TABLE_NAME_USER,
        ImmutableSet.of("Username", "Email")
    );
    requiredTablesAndColumnsMap.put(
        TABLE_NAME_ACTIVITY_CODE,
        ImmutableSet.of("Fakturatextnr")
    );

    final Map<String, List<String>> actualTablesAndColumnsMap = query().databaseInspection()
        .selectFromMetaData(meta -> meta.getColumns(null, null, null, null))
        .listResult(rs -> ImmutablePair.of(rs.getString("TABLE_NAME"), rs.getString("COLUMN_NAME")))
        .stream()
        .filter(pair -> requiredTablesAndColumnsMap.containsKey(pair.getKey()))
        .collect(groupingBy(Pair::getKey, mapping(Pair::getValue, toList())));

    return requiredTablesAndColumnsMap.entrySet().stream()
        .allMatch(entry -> actualTablesAndColumnsMap.containsKey(entry.getKey()) &&
            actualTablesAndColumnsMap.get(entry.getKey()).containsAll(entry.getValue())
        );
  }

  private void verifyPostingTimeIsSuccessful(int recordIdOrFailureStatus) {
    if (recordIdOrFailureStatus > 0) {
      // A record ID is generated, which means posting time is successful
      return;
    }

    switch (recordIdOrFailureStatus) {
      case -1:
        throw new IllegalStateException("Case or client not found");
      case -2:
        throw new IllegalStateException("Client is blocked");
      case -3:
        throw new IllegalStateException("User not found or inactive");
      case -4:
        throw new IllegalStateException("Activity code not found or inactive");
      default:
        throw new RuntimeException("Unknown status code returned when calling `pw_PostTime`");
    }
  }

  private Query query() {
    return fluentJdbc.query();
  }
}
