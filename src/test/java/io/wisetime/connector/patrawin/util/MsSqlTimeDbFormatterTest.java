/*
 * Copyright (c) 2019 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector.patrawin.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author galya.bogdanova@m.practiceinsight.io
 */
public class MsSqlTimeDbFormatterTest {

  private MsSqlTimeDbFormatter msSqlDateTimeUtils = new MsSqlTimeDbFormatter();

  @Test
  void format_correct_localDateTime() {
    LocalDateTime correctLocalDateTime = LocalDateTime.of(2019, 2, 5, 13, 6, 12);
    String msSqlFormat = msSqlDateTimeUtils.format(correctLocalDateTime);
    assertThat(msSqlFormat)
        .isEqualTo("2019-02-05 13:06:12");
  }

  @Test
  void format_correct_offsetDateTime_positiveOffset() {
    OffsetDateTime correctOffsetDateTime = OffsetDateTime.of(2019, 2, 5, 13, 6, 12, 0, ZoneOffset.ofHours(2));
    String msSqlFormat = msSqlDateTimeUtils.format(correctOffsetDateTime);
    assertThat(msSqlFormat)
        .isEqualTo("2019-02-05 13:06:12 +02:00");
  }

  @Test
  void format_correct_offsetDateTime_negativeOffset() {
    OffsetDateTime correctOffsetDateTime = OffsetDateTime.of(2019, 2, 5, 13, 6, 12, 0, ZoneOffset.ofHours(-2));
    String msSqlFormat = msSqlDateTimeUtils.format(correctOffsetDateTime);
    assertThat(msSqlFormat)
        .isEqualTo("2019-02-05 13:06:12 -02:00");
  }

  @Test
  void format_correct_offsetDateTime_noOffset() {
    OffsetDateTime correctOffsetDateTime = OffsetDateTime.of(2019, 2, 5, 13, 6, 12, 0, ZoneOffset.ofHours(0));
    String msSqlFormat = msSqlDateTimeUtils.format(correctOffsetDateTime);
    assertThat(msSqlFormat)
        .isEqualTo("2019-02-05 13:06:12 Z");
  }

  @Test
  void format_null_localDateTime_throws_npe() {
    assertThrows(NullPointerException.class, () -> msSqlDateTimeUtils.format((LocalDateTime) null));
  }

  @Test
  void format_null_offsetDateTime_throws_npe() {
    assertThrows(NullPointerException.class, () -> msSqlDateTimeUtils.format((OffsetDateTime) null));
  }

  @Test
  void parse_correct_string() {
    LocalDateTime localDateTime = msSqlDateTimeUtils.parseDateTime("2019-02-05 13:26:12");
    assertThat(localDateTime)
        .isEqualTo(LocalDateTime.of(2019, 2, 5, 13, 26, 12));
  }

  @Test
  void parse_invalid_string_throws_exception() {
    assertThrows(DateTimeParseException.class, () -> msSqlDateTimeUtils.parseDateTime("2019-2-05 13:26:12"));
  }

}
