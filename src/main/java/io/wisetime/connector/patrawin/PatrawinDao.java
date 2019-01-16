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
import org.immutables.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

  boolean hasExpectedSchema() {
    // TODO
    return true;
  }

  boolean canQueryDb() {
    try {
      query().select("SELECT 1 FROM ARENDE_1").firstResult(Mappers.singleInteger());
      return true;
    } catch (Exception ex) {
      return false;
    }
  }

  List<Case> findCasesOrderedById(final long startIdExclusive, final int maxResults) {
    // TODO
    return ImmutableList.of();
  }

  List<Case> findClientsOrderedById(final long startIdExclusive, final int maxResults) {
    // TODO
    return ImmutableList.of();
  }

  private Query query() {
    return fluentJdbc.query();
  }

  /**
   * Models a Patrawin case.
   */
  @Value.Immutable
  public interface Case {
    // TODO
  }

  /**
   * Models a Patrawin client.
   */
  @Value.Immutable
  public interface Client {
    // TODO
  }
}
