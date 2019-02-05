/*
 * Copyright (c) 2019 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector.patrawin;

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

  private final FluentJdbc fluentJdbc;
  private final MsSqlDateTimeUtils msSqlDateTimeUtils;

  @Inject
  PatrawinDao(DataSource dataSource, MsSqlDateTimeUtils msSqlDateTimeUtils) {
    this.fluentJdbc = new FluentJdbcBuilder().connectionProvider(dataSource).build();
    this.msSqlDateTimeUtils = msSqlDateTimeUtils;
  }

  void asTransaction(final Runnable runnable) {
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
  List<Case> findCasesOrderedByCreationTime(final Instant createdOnOrAfter, final List<String> excludedCaseNumbers,
                                            final int maxResults) {
    final StringBuilder query = new StringBuilder(
        "SELECT TOP (:maxResults) Arendenr AS CaseNum, Slagord AS Description, Skapatdat AS CreatedDate " +
            "FROM ARENDE_1 WHERE Skapatdat >= :createdOnOrAfter"
    );
    if (!excludedCaseNumbers.isEmpty()) {
      query.append(" AND Arendenr NOT IN (:excludedCaseNumbers)");
    }
    query.append(" ORDER BY CreatedDate, CaseNum");

    SelectQuery selectQuery = query().select(query.toString())
        .namedParam("maxResults", maxResults)
        .namedParam("createdOnOrAfter", msSqlDateTimeUtils.format(createdOnOrAfter));
    if (!excludedCaseNumbers.isEmpty()) {
      selectQuery = selectQuery.namedParam("excludedCaseNumbers", excludedCaseNumbers);
    }
    return selectQuery.listResult(rs -> ImmutableCase.builder()
        .caseNumber(rs.getString(1))
        .description(rs.getString(2))
        .creationTime(msSqlDateTimeUtils.parse(rs.getString(3)))
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
  List<Client> findClientsOrderedByCreationTime(final Instant createdOnOrAfter, final List<String> excludedClientIds,
                                                final int maxResults) {
    final StringBuilder query = new StringBuilder(
        "SELECT TOP (:maxResults) Kundnr AS ClientId, Kortnamnkund AS Alias, Skapatdat AS CreatedDate " +
            "FROM KUND_24 WHERE Skapatdat >= :createdOnOrAfter"
    );
    if (!excludedClientIds.isEmpty()) {
      query.append(" AND Kundnr NOT IN (:excludedClientIds)");
    }
    query.append(" ORDER BY CreatedDate, ClientId");

    final SelectQuery selectQuery = query().select(query.toString())
        .namedParam("maxResults", maxResults)
        .namedParam("createdOnOrAfter", msSqlDateTimeUtils.format(createdOnOrAfter));
    if (!excludedClientIds.isEmpty()) {
      selectQuery.namedParam("excludedClientIds", excludedClientIds);
    }
    return selectQuery.listResult(rs -> ImmutableClient.builder()
        .clientId(rs.getString(1))
        .alias(rs.getString(2))
        .creationTime(msSqlDateTimeUtils.parse(rs.getString(3)))
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
    log.info("Checking if Patrawin DB has correct schema...");

    final Map<String, Set<String>> requiredTablesAndColumnsMap = new HashMap<>();
    requiredTablesAndColumnsMap.put(
        "arende_1",
        ImmutableSet.of("arendenr", "slagord", "skapatdat")
    );
    requiredTablesAndColumnsMap.put(
        "kund_24",
        ImmutableSet.of("kundnr", "kortnamnkund", "skapatdat")
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
