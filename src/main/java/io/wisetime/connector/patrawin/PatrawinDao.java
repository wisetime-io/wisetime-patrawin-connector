/*
 * Copyright (c) 2019 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector.patrawin;

import com.google.inject.Inject;

import org.codejargon.fluentjdbc.api.FluentJdbc;
import org.codejargon.fluentjdbc.api.FluentJdbcBuilder;
import org.codejargon.fluentjdbc.api.mapper.Mappers;
import org.codejargon.fluentjdbc.api.query.Query;
import org.codejargon.fluentjdbc.api.query.SelectQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.List;

import javax.sql.DataSource;

/**
 * Simple, unsophisticated access to the Patrawin database.
 *
 * @author shane.xie@practiceinsight.io
 */
public class PatrawinDao {

  private final Logger log = LoggerFactory.getLogger(PatrawinDao.class);
  private final FluentJdbc fluentJdbc;

  private static DateTimeFormatter dbDateTimeUtcFormatter = new DateTimeFormatterBuilder()
      .appendPattern("yyyy-MM-dd HH:mm:ss")
      .appendFraction(ChronoField.MICRO_OF_SECOND, 0, 3, true)
      .toFormatter()
      .withZone(ZoneOffset.UTC); // TODO: Decide what timezone should be use

  @Inject
  PatrawinDao(DataSource dataSource) {
    fluentJdbc = new FluentJdbcBuilder().connectionProvider(dataSource).build();
  }

  void asTransaction(final Runnable runnable) {
    query().transaction().inNoResult(runnable);
  }

  /**
   * Find cases
   *
   * @param createdOnOrAfter find cases created on or after this time
   * @param excludedCaseNumbers list of case numbers to exclude from results
   * @param maxResults maximum number of cases to return
   * @return list of cases ordered by creation time ascending
   */
  List<Case> findCasesOrderedByCreationTime(final Instant createdOnOrAfter, final List<String> excludedCaseNumbers,
                                            final int maxResults) {
    final StringBuilder query = new StringBuilder(
        "SELECT TOP (:maxResults) Arendenr AS CaseNum, Slagord AS Description, Skapatdat AS CreatedDate " +
        "FROM ARENDE_1 WHERE Skapatdat >= :createdOnOrAfter"
    );
    if (!excludedCaseNumbers.isEmpty()) {
      query.append(" AND Arendenr NOT IN (:excludedCaseNumbers)");
    }
    query.append(" ORDER BY Skapatdat, Arendenr");

    SelectQuery selectQuery = query().select(query.toString())
        .namedParam("maxResults", maxResults)
        .namedParam("createdOnOrAfter", dbDateTimeUtcFormatter.format(createdOnOrAfter));
    if (!excludedCaseNumbers.isEmpty()) {
      selectQuery = selectQuery.namedParam("excludedCaseNumbers", excludedCaseNumbers);
    }
    return selectQuery.listResult(rs -> ImmutableCase.builder()
            .caseNumber(rs.getString(1))
            .description(rs.getString(2))
            .creationTime(Instant.from(dbDateTimeUtcFormatter.parse(rs.getString(3))))
            .build()
    );
  }

  /**
   * Find clients
   *
   * @param createdOnOrAfter find clients created on or after this time
   * @param excludedClientIds list of client IDs to exclude from results
   * @param maxResults maximum number of clients to return
   * @return list of clients ordered by creation time ascending
   */
  List<Client> findClientsOrderedByCreationTime(final Instant createdOnOrAfter, final List<String> excludedClientIds,
                                              final int maxResults) {
    final StringBuilder query = new StringBuilder(
        "SELECT TOP (:maxResults) Kundnr AS ClientId, Kortnamnkund AS Alias, Skapatdat AS CreatedDate " +
            "FROM KUND_24 WHERE Skapatdat >= :createdOnOrAfter"
    );
    if (!excludedClientIds.isEmpty()) {
      query.append(" AND Kundnr NOT IN (:excludedClientIds)");
    }
    query.append(" ORDER BY Skapatdat, Kundnr");

    SelectQuery selectQuery = query().select(query.toString())
        .namedParam("maxResults", maxResults)
        .namedParam("createdOnOrAfter", dbDateTimeUtcFormatter.format(createdOnOrAfter));
    if (!excludedClientIds.isEmpty()) {
      selectQuery = selectQuery.namedParam("excludedClientIds", excludedClientIds);
    }
    return selectQuery.listResult(rs -> ImmutableClient.builder()
        .clientId(rs.getString(1))
        .alias(rs.getString(2))
        .creationTime(Instant.from(dbDateTimeUtcFormatter.parse(rs.getString(3))))
        .build()
    );
  }

  boolean canQueryDb() {
    try {
      query().select("SELECT 1 FROM ARENDE_1").firstResult(Mappers.singleInteger());
      return true;
    } catch (Exception ex) {
      return false;
    }
  }

  boolean hasExpectedSchema() {
    // TODO
    return true;
  }

  private Query query() {
    return fluentJdbc.query();
  }
}
