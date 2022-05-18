/*
 * Copyright (c) 2019 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector.patrawin.persistence;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.zaxxer.hikari.HikariDataSource;
import io.wisetime.connector.config.RuntimeConfig;
import io.wisetime.connector.patrawin.ConnectorLauncher.PatrawinConnectorConfigKey;
import io.wisetime.connector.patrawin.model.Case;
import io.wisetime.connector.patrawin.model.Client;
import io.wisetime.connector.patrawin.model.Worklog;
import io.wisetime.connector.patrawin.util.TimeDbFormatter;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import lombok.Data;
import lombok.experimental.Accessors;
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

/**
 * Simple, unsophisticated access to the Patrawin database.
 *
 * @author shane.xie@practiceinsight.io
 * @author galya.bogdanova@m.practiceinsight.io
 * @author pascal
 */
public class PatrawinDao {

  private static final Logger log = LoggerFactory.getLogger(PatrawinDao.class);

  // https://docs.microsoft.com/en-us/sql/t-sql/data-types/datetime-transact-sql?view=sql-server-2017
  // some client dbs are set up using smalldatetime:
  // https://docs.microsoft.com/de-de/sql/t-sql/data-types/smalldatetime-transact-sql?view=sql-server-ver15
  @VisibleForTesting
  static final LocalDateTime MIN_SQL_DATE_TIME = LocalDateTime.of(1900, 1, 1, 0, 0);

  private static final String TABLE_NAME_CASE = "ARENDE_1";
  private static final String TABLE_NAME_CLIENT = "KUND_24";
  private static final String TABLE_NAME_CASE_CLIENT_JOIN = "KUND_ARENDE_25";
  private static final String TABLE_NAME_CREDIT_LEVEL = "CREDIT_LEVEL_334";
  private static final String TABLE_NAME_USER = "BEHORIG_50";
  private static final String TABLE_NAME_ACTIVITY_CODE = "FAKTURATEXTNR_15";
  private static final String TABLE_NAME_PENDING_TIME = "PENDING_TIME_335";

  private static final String PROCEDURE_NAME_POST_TIME = "pw_PostTime";

  private final FluentJdbc fluentJdbc;
  private final TimeDbFormatter timeDbFormatter;
  private final HikariDataSource dataSource;

  @Inject
  public PatrawinDao(HikariDataSource dataSource, TimeDbFormatter timeDbFormatter) {
    this.dataSource = dataSource;
    this.fluentJdbc = new FluentJdbcBuilder().connectionProvider(dataSource).build();
    this.timeDbFormatter = timeDbFormatter;
  }

  public void asTransaction(final Runnable runnable) {
    query().transaction().inNoResult(runnable);
  }

  /**
   * Find cases
   *
   * @param createdOnOrAfter     find cases created on or after this time
   * @param lastSyncedCaseNumber last synec case number
   * @param maxResults           maximum number of cases to return
   * @return list of cases ordered by creation time ascending
   */
  public List<Case> findCasesOrderedByCreationTime(final Optional<LocalDateTime> createdOnOrAfter,
      final Optional<String> lastSyncedCaseNumber,
      final int maxResults) {
    final StringBuilder query = new StringBuilder(
        "SELECT DISTINCT a.Arendenr AS CaseNum, a.Slagord AS Description,"
            + " COALESCE(a.Skapatdat, :minDate) AS CreatedDate FROM "
    );
    query.append(TABLE_NAME_CASE + " a ");
    query.append(" JOIN " + TABLE_NAME_CASE_CLIENT_JOIN + " ka ");
    query.append(" ON a.Arendenr = ka.Arendenr ");
    query.append(" JOIN " + TABLE_NAME_CLIENT + " k ");
    query.append(" ON ka.Kundnr = k.Kundnr ");
    query.append(" JOIN " + TABLE_NAME_CREDIT_LEVEL + " c ON k.Kreditjn = c.Creditcode");
    query.append(" WHERE ");

    query.append(" c.Type != 2 AND ");
    if (lastSyncedCaseNumber.isPresent()) {
      // Only consider last synced id for same date. if date is null consider it as very old
      query.append(" ((COALESCE(a.Skapatdat, :minDate) = :createdOnOrAfter AND a.Arendenr > :lastSyncedCaseNumber)");
      query.append(" OR COALESCE(a.Skapatdat, :minDate) > :createdOnOrAfter)");
    } else {
      query.append(" COALESCE(a.Skapatdat, :minDate) >= :createdOnOrAfter");
    }
    query.append(" ORDER BY CreatedDate, CaseNum");

    final SelectQuery selectQuery = query().select(query.toString())
        .namedParam("maxResults", maxResults)
        .namedParam("createdOnOrAfter", timeDbFormatter.format(createdOnOrAfter.orElse(MIN_SQL_DATE_TIME)))
        .namedParam("minDate", timeDbFormatter.format(MIN_SQL_DATE_TIME))
        .maxRows((long) maxResults);
    lastSyncedCaseNumber.ifPresent(s -> selectQuery.namedParam("lastSyncedCaseNumber", s));
    return selectQuery.listResult(rs ->
        Case.builder()
            .description(StringUtils.trimToEmpty(rs.getString(2)))
            .number(rs.getString(1))
            .creationTime(timeDbFormatter.parseDateTime(rs.getString(3))).build()
    );
  }

  public List<Case> findCasesOfBlockedClientsOrderedByCreationTime(final Optional<LocalDateTime> createdOnOrAfter,
      final Optional<String> lastSyncedCaseNumber,
      final int maxResults) {
    final StringBuilder query = new StringBuilder(
        "SELECT DISTINCT a.Arendenr AS CaseNum, a.Slagord AS Description,"
            + " COALESCE(a.Skapatdat, :minDate) AS CreatedDate FROM "
    );
    query.append(TABLE_NAME_CASE + " a ");
    query.append(" JOIN " + TABLE_NAME_CASE_CLIENT_JOIN + " ka ");
    query.append(" ON a.Arendenr = ka.Arendenr ");
    query.append(" JOIN " + TABLE_NAME_CLIENT + " k ");
    query.append(" ON ka.Kundnr = k.Kundnr ");
    query.append(" JOIN " + TABLE_NAME_CREDIT_LEVEL + " c ON k.Kreditjn = c.Creditcode");
    query.append(" WHERE ");

    query.append(" c.Type = 2 AND ");
    if (lastSyncedCaseNumber.isPresent()) {
      // Only consider last synced id for same date. if date is null consider it as very old
      query.append(" ((COALESCE(a.Skapatdat, :minDate) = :createdOnOrAfter AND a.Arendenr > :lastSyncedCaseNumber)");
      query.append(" OR COALESCE(a.Skapatdat, :minDate) > :createdOnOrAfter)");
    } else {
      query.append(" COALESCE(a.Skapatdat, :minDate) >= :createdOnOrAfter");
    }
    query.append(" ORDER BY CreatedDate, CaseNum");

    final SelectQuery selectQuery = query().select(query.toString())
        .namedParam("maxResults", maxResults)
        .namedParam("createdOnOrAfter", timeDbFormatter.format(createdOnOrAfter.orElse(MIN_SQL_DATE_TIME)))
        .namedParam("minDate", timeDbFormatter.format(MIN_SQL_DATE_TIME))
        .maxRows((long) maxResults);
    lastSyncedCaseNumber.ifPresent(s -> selectQuery.namedParam("lastSyncedCaseNumber", s));
    return selectQuery.listResult(rs ->
        Case.builder()
            .description(StringUtils.trimToEmpty(rs.getString(2)))
            .number(rs.getString(1))
            .creationTime(timeDbFormatter.parseDateTime(rs.getString(3))).build()
    );
  }

  public long casesCount() {
    String query = "SELECT COUNT(*) FROM " + TABLE_NAME_CASE;
    return query().select(query)
        .firstResult(Mappers.singleLong())
        .orElse(0L);
  }

  /**
   * Find clients
   *
   * @param createdOnOrAfter       find clients created on or after this time
   * @param lastSyncedClientNumber the last synced client number
   * @param maxResults             maximum number of clients to return
   * @return list of clients ordered by creation time ascending
   */
  public List<Client> findClientsOrderedByCreationTime(final Optional<LocalDateTime> createdOnOrAfter,
      final Optional<String> lastSyncedClientNumber,
      final int maxResults) {
    final StringBuilder query = new StringBuilder(
        "SELECT DISTINCT Kundnr AS ClientNumber, Kortnamnkund AS Alias, COALESCE(Skapatdat, :minDate) AS CreatedDate FROM "
    );
    query.append(TABLE_NAME_CLIENT + " k ");
    query.append(" JOIN " + TABLE_NAME_CREDIT_LEVEL + " c ON k.Kreditjn = c.Creditcode");
    query.append(" WHERE");
    query.append(" c.Type != 2 AND ");
    if (lastSyncedClientNumber.isPresent()) {
      // Only consider last synced id for same date. if date is null consider it as very old
      query.append(" (COALESCE(Skapatdat, :minDate) = :createdOnOrAfter AND Kundnr > :lastSyncedClientNumber)");
      query.append(" OR COALESCE(Skapatdat, :minDate) > :createdOnOrAfter");
    } else {
      query.append(" COALESCE(Skapatdat, :minDate) >= :createdOnOrAfter");
    }
    query.append(" ORDER BY CreatedDate, ClientNumber");

    final SelectQuery selectQuery = query().select(query.toString())
        .namedParam("maxResults", maxResults)
        .namedParam("createdOnOrAfter", timeDbFormatter.format(createdOnOrAfter.orElse(MIN_SQL_DATE_TIME)))
        .namedParam("minDate", timeDbFormatter.format(MIN_SQL_DATE_TIME))
        .maxRows((long) maxResults);
    lastSyncedClientNumber.ifPresent(s -> selectQuery.namedParam("lastSyncedClientNumber", s));
    return selectQuery.listResult(rs -> {
      Client clientRes = Client.builder()
          .alias(StringUtils.trimToEmpty(rs.getString(2)))
          .number(rs.getString(1))
          .creationTime(timeDbFormatter.parseDateTime(rs.getString(3)))
          .build();
      return clientRes;
    });
  }

  public List<Client> findBlockedClientsOrderedByCreationTime(final Optional<LocalDateTime> createdOnOrAfter,
      final Optional<String> lastSyncedClientNumber,
      final int maxResults) {
    final StringBuilder query = new StringBuilder(
        "SELECT DISTINCT Kundnr AS ClientNumber, Kortnamnkund AS Alias, COALESCE(Skapatdat, :minDate) AS CreatedDate FROM "
    );
    query.append(TABLE_NAME_CLIENT + " k ");
    query.append(" JOIN " + TABLE_NAME_CREDIT_LEVEL + " c ON k.Kreditjn = c.Creditcode");
    query.append(" WHERE");
    query.append(" c.Type = 2 AND ");
    if (lastSyncedClientNumber.isPresent()) {
      // Only consider last synced id for same date. if date is null consider it as very old
      query.append(" (COALESCE(Skapatdat, :minDate) = :createdOnOrAfter AND Kundnr > :lastSyncedClientNumber)");
      query.append(" OR COALESCE(Skapatdat, :minDate) > :createdOnOrAfter");
    } else {
      query.append(" COALESCE(Skapatdat, :minDate) >= :createdOnOrAfter");
    }
    query.append(" ORDER BY CreatedDate, ClientNumber");

    final SelectQuery selectQuery = query().select(query.toString())
        .namedParam("maxResults", maxResults)
        .namedParam("createdOnOrAfter", timeDbFormatter.format(createdOnOrAfter.orElse(MIN_SQL_DATE_TIME)))
        .namedParam("minDate", timeDbFormatter.format(MIN_SQL_DATE_TIME))
        .maxRows((long) maxResults);
    lastSyncedClientNumber.ifPresent(s -> selectQuery.namedParam("lastSyncedClientNumber", s));
    return selectQuery.listResult(rs -> {
      Client clientRes = Client.builder()
          .alias(StringUtils.trimToEmpty(rs.getString(2)))
          .number(rs.getString(1))
          .creationTime(timeDbFormatter.parseDateTime(rs.getString(3)))
          .build();
      return clientRes;
    });
  }

  public long clientsCount() {
    String query = "SELECT COUNT(*) FROM " + TABLE_NAME_CLIENT;
    return query().select(query)
        .firstResult(Mappers.singleLong())
        .orElse(0L);
  }

  public boolean doesUserExist(String usernameOrEmail) {
    return query().select("SELECT Username, Email FROM " + TABLE_NAME_USER
        + " WHERE Username = :usernameOrEmail OR Email = :usernameOrEmail")
        .namedParam("usernameOrEmail", usernameOrEmail)
        .maxRows(1L)
        .firstResult(rs -> rs)
        .isPresent();
  }

  public boolean doesCaseExist(String caseNumber) {
    return query().select("SELECT Arendenr AS CaseNum FROM " + TABLE_NAME_CASE
        + " WHERE Arendenr = :caseNumber")
        .namedParam("caseNumber", caseNumber)
        .maxRows(1L)
        .firstResult(rs -> rs)
        .isPresent();
  }

  public boolean doesClientExist(String clientNumber) {
    return query().select("SELECT Kundnr AS ClientNumber FROM " + TABLE_NAME_CLIENT
        + " WHERE Kundnr = :clientNumber")
        .namedParam("clientNumber", clientNumber)
        .maxRows(1L)
        .firstResult(rs -> rs)
        .isPresent();
  }

  public boolean doesActivityCodeExist(int activityCode) {
    return query().select("SELECT Fakturatextnr AS ActivityCode FROM " + TABLE_NAME_ACTIVITY_CODE
        + " WHERE Fakturatextnr = :activityCode")
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
   * <p>
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
      query().select("SELECT TOP 1 1 FROM " + TABLE_NAME_CASE).firstResult(Mappers.singleString());
      return true;
    } catch (Exception ex) {
      return false;
    }
  }

  public boolean hasUnprocessedTime(int healthCheckInterval) {
    LocalDateTime createdBefore = LocalDateTime.now(getTimeZoneId()).minusMinutes(healthCheckInterval);
    String query = "SELECT TOP 1 1 FROM " + TABLE_NAME_PENDING_TIME
        + " WHERE created <= :createdBefore AND imported is null";
    return query().select(query)
        .namedParam("createdBefore", timeDbFormatter.format(createdBefore))
        .firstResult(Mappers.singleLong()).orElse(0L) > 0;
  }

  public Optional<LocalDateTime> getEarliestUnprocessedTime(int healthCheckInterval) {
    LocalDateTime createdBefore = LocalDateTime.now(getTimeZoneId()).minusMinutes(healthCheckInterval);
    String query = "SELECT TOP 1 created FROM " + TABLE_NAME_PENDING_TIME
        + " WHERE created <= :createdBefore AND imported is null order by created asc ";
    return query().select(query)
        .namedParam("createdBefore", timeDbFormatter.format(createdBefore))
        .firstResult((rs) -> rs.getTimestamp(1).toLocalDateTime());
  }

  public boolean hasExpectedSchema() {
    log.info("Checking if Patrawin DB has correct schema...");
    return hasExpectedTables() && hasExpectedProcedures();
  }

  private boolean hasExpectedTables() {
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
    requiredTablesAndColumnsMap.put(
        TABLE_NAME_PENDING_TIME,
        ImmutableSet.of("User_Id", "Arendenr", "Kundnr", "StartTimeUtc", "Minutes", "Fakturatextnr", "Text")
    );

    final Map<String, List<String>> actualTablesAndColumnsMap = query().databaseInspection()
        .selectFromMetaData(meta -> meta.getColumns(null, null, null, null))
        .listResult(rs -> ImmutablePair.of(rs.getString("TABLE_NAME"), rs.getString("COLUMN_NAME")))
        .stream()
        .filter(pair -> requiredTablesAndColumnsMap.containsKey(pair.getKey()))
        .collect(groupingBy(Pair::getKey, mapping(Pair::getValue, toList())));

    return requiredTablesAndColumnsMap.entrySet().stream()
        .allMatch(entry -> actualTablesAndColumnsMap.containsKey(entry.getKey())
        && actualTablesAndColumnsMap.get(entry.getKey()).containsAll(entry.getValue())
        );
  }

  private boolean hasExpectedProcedures() {
    final Map<String, Set<String>> requiredProceduresAndParametersMap = new HashMap<>();
    requiredProceduresAndParametersMap.put(
        PROCEDURE_NAME_POST_TIME,
        ImmutableSet.of("@case_or_client_id",
            "@username_or_email",
            "@activity_code",
            "@narrative",
            "@narrative_internal_note",
            "@start_time",
            "@total_time_secs",
            "@chargeable_time_secs")
    );

    Map<String, List<String>> actualProceduresAndParametersMap =
        query().select("SELECT * FROM INFORMATION_SCHEMA.PARAMETERS")
            .listResult(rs -> ImmutablePair.of(rs.getString("SPECIFIC_NAME"), rs.getString("PARAMETER_NAME")))
            .stream()
            .filter(pair -> requiredProceduresAndParametersMap.containsKey(pair.getKey()))
            .collect(groupingBy(Pair::getKey, mapping(Pair::getValue, toList())));

    return requiredProceduresAndParametersMap.entrySet().stream()
        .allMatch(entry -> actualProceduresAndParametersMap.containsKey(entry.getKey())
        && actualProceduresAndParametersMap.get(entry.getKey()).containsAll(entry.getValue())
        );
  }

  private void verifyPostingTimeIsSuccessful(int recordIdOrFailureStatus) {
    if (recordIdOrFailureStatus > 0) {
      // A record ID is generated, which means posting time is successful
      return;
    }

    switch (recordIdOrFailureStatus) {
      case -1:
        throw new IllegalStateException("Case or client not found in Patrawin.");
      case -2:
        throw new IllegalStateException(
            "Client is blocked in Patrawin database, please contact your system administrator");
      case -3:
        throw new IllegalStateException(
            "Patrawin user not found or inactive, please contact your system administrator");
      case -4:
        throw new IllegalStateException("Activity code not found or inactive");
      default:
        throw new RuntimeException("Unknown status code returned when calling `pw_PostTime`");
    }
  }

  private Query query() {
    return fluentJdbc.query();
  }

  public void shutdown() {
    dataSource.close();
  }

  private ZoneId getTimeZoneId() {
    return ZoneId.of(RuntimeConfig.getString(PatrawinConnectorConfigKey.TIMEZONE).orElse("UTC"));
  }

  public List<ActivityTypeLabel> findActivityTypeLabels(int offset, int limit) {
    return query()
        .select("SELECT F15.Fakturatextnr as ID, F16.Fakturatext as LABEL"
            + " FROM FAKTURATEXTNR_15 F15 "
            + " INNER JOIN FAKTURATEXT_16 F16 "
            + " ON F15.Fakturatextnr = F16.Fakturatextnr "
            + "AND F16.Sprakkod = (SELECT TOP 1 Skarmsprak FROM FORETAGINFO_56)"
            + " WHERE F15.Inaktiv = 0 "
            + " AND F15.Fakturatextnr > 9 "
            + " AND F15.Fakturatextnr != 9998 "
            + " AND F15.Fakturatextnr NOT IN "
            + "  (SELECT CAST(Varde as int) "
            + "   FROM PARAMETER_149 "
            + "   WHERE Sektion = 'INVOICES RENEWAL INVOICING' "
            + "   AND LEN(ISNULL(Varde, '')) > 0 "
            + "   AND Varde NOT LIKE '%[^0-9]%') "
            + " AND F15.Fakturatextnr NOT IN "
            + " (SELECT CAST(Varde as int) "
            + "  FROM PARAMETER_149 "
            + "  WHERE Sektion = 'INVOICES ADVANCE INVOICING' "
            + "  AND Parameter IN ('ADVANCE','ADVANCE DEDUCTION SERVICE','ADVANCE WITH VAT',"
            + "     'ADVANCE WITH VAT DEDUCTION SERVICE') "
            + "  AND LEN(ISNULL(Varde, '')) > 0 AND Varde NOT LIKE '%[^0-9]%'"
            + " ) "
            + " ORDER BY ID"
            + " OFFSET :offset ROWS"
            + " FETCH NEXT :limit ROWS ONLY")
        .namedParam("offset", offset)
        .namedParam("limit", limit)
        .listResult(this::buildActivityTypeLabelFromResultSet);
  }

  private ActivityTypeLabel buildActivityTypeLabelFromResultSet(ResultSet rs) throws SQLException {
    return new ActivityTypeLabel()
        .setId(rs.getString("ID"))
        .setLabel(rs.getString("LABEL"));
  }

  @Data
  @Accessors(chain = true)
  public static class ActivityTypeLabel {

    private String id;
    @Nullable
    private String label;
  }
}
