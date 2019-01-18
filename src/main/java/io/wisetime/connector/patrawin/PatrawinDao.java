/*
 * Copyright (c) 2019 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector.patrawin;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;

import org.codejargon.fluentjdbc.api.FluentJdbc;
import org.codejargon.fluentjdbc.api.FluentJdbcBuilder;
import org.codejargon.fluentjdbc.api.mapper.Mappers;
import org.codejargon.fluentjdbc.api.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
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
    // TODO
    return ImmutableList.of();
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
    // TODO
    return ImmutableList.of();
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
