/*
 * Copyright (c) 2019 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector.patrawin.util;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

/**
 * @author galya.bogdanova@m.practiceinsight.io
 */
public final class MsSqlTimeDbFormatter implements TimeDbFormatter {

  // Note: converted Java date time to SQL date time might have slight difference in nanos because
  // MSSQL's DATETIME are rounded to increments of .000, .003 or .007 seconds
  // https://docs.microsoft.com/en-us/sql/t-sql/data-types/datetime-transact-sql?view=sql-server-2017

  // DateTime formatter builder for pattern `"yyyy-MM-dd HH:mm:ss.SSS`
  private static final DateTimeFormatter MSSQL_DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
      .appendPattern("yyyy-MM-dd HH:mm:ss")
      .appendFraction(ChronoField.MICRO_OF_SECOND, 0, 3, true)
      .toFormatter();

  // DateTime formatter builder for pattern `"yyyy-MM-dd HH:mm:ss.nnnnnnn XXX`
  // See https://docs.microsoft.com/en-us/sql/t-sql/data-types/datetimeoffset-transact-sql?view=sql-server-2017
  private static final DateTimeFormatter MSSQL_DATE_TIME_OFFSET_FORMATTER = new DateTimeFormatterBuilder()
      .appendPattern("yyyy-MM-dd HH:mm:ss")
      .appendFraction(ChronoField.NANO_OF_SECOND, 0, 7, true)
      .appendPattern(" XXX") // zone offset for [+|-]hh:mm
      .toFormatter();

  @Override
  public String format(LocalDateTime dateTime) {
    return MSSQL_DATE_TIME_FORMATTER.format(dateTime);
  }

  @Override
  public String format(OffsetDateTime offsetDateTime) {
    return MSSQL_DATE_TIME_OFFSET_FORMATTER.format(offsetDateTime);
  }

  @Override
  public LocalDateTime parseDateTime(String msqlDateTime) {
    return LocalDateTime.parse(msqlDateTime, MSSQL_DATE_TIME_FORMATTER);
  }

  @Override
  public OffsetDateTime parseOffsetDateTime(String offsetDateTime) {
    return OffsetDateTime.parse(offsetDateTime, MSSQL_DATE_TIME_OFFSET_FORMATTER);
  }

}
