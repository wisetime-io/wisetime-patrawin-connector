/*
 * Copyright (c) 2019 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector.patrawin.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

/**
 * @author galya.bogdanova@m.practiceinsight.io
 */
public final class MsSqlTimeDbFormatter implements TimeDbFormatter {

  // MSSQL's DATETIME are rounded to increments of .000, .003 or .007 seconds
  // https://docs.microsoft.com/en-us/sql/t-sql/data-types/datetime-transact-sql?view=sql-server-2017
  private static final int ROUNDING_FACTOR_MICRO_SECONDS = 3;

  private static final ZoneOffset INSTANT_DEFAULT_TIME_ZONE = ZoneOffset.UTC;

  private static final DateTimeFormatter MSSQL_DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
      .appendPattern("yyyy-MM-dd HH:mm:ss")
      .appendFraction(ChronoField.MICRO_OF_SECOND, 0, ROUNDING_FACTOR_MICRO_SECONDS, true)
      .toFormatter()
      .withZone(INSTANT_DEFAULT_TIME_ZONE);

  @Override
  public String format(Instant instant) {
    return MSSQL_DATE_TIME_FORMATTER.format(instant);
  }

  @Override
  public Instant parse(String msqlDateTime) {
    return LocalDateTime.parse(msqlDateTime, MSSQL_DATE_TIME_FORMATTER).toInstant(INSTANT_DEFAULT_TIME_ZONE);
  }

  @Override
  public Instant convert(LocalDateTime localDateTime) {
    return localDateTime.toInstant(INSTANT_DEFAULT_TIME_ZONE);
  }

}
