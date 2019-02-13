/*
 * Copyright (c) 2019 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector.patrawin.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;

/**
 * @author galya.bogdanova@m.practiceinsight.io
 */
public final class MsSqlTimeDbFormatter implements TimeDbFormatter {

  // MSSQL's DATETIME are rounded to increments of .000, .003 or .007 seconds
  // https://docs.microsoft.com/en-us/sql/t-sql/data-types/datetime-transact-sql?view=sql-server-2017
  private static final int ROUNDING_FACTOR_MICRO_SECONDS = 3;

  private static final DateTimeFormatter MSSQL_DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
      .appendPattern("yyyy-MM-dd HH:mm:ss")
      .appendFraction(ChronoField.MICRO_OF_SECOND, 0, ROUNDING_FACTOR_MICRO_SECONDS, true)
      .toFormatter();

  @Override
  public String format(TemporalAccessor dateTime) {
    return MSSQL_DATE_TIME_FORMATTER.format(dateTime);
  }

  @Override
  public LocalDateTime parse(String msqlDateTime) {
    return LocalDateTime.parse(msqlDateTime, MSSQL_DATE_TIME_FORMATTER);
  }

}
