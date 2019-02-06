/*
 * Copyright (c) 2019 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector.patrawin.util;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.format.DateTimeParseException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author galya.bogdanova@m.practiceinsight.io
 */
public class MsSqlTimeDbFormatterTest {

  private MsSqlTimeDbFormatter msSqlDateTimeUtils = new MsSqlTimeDbFormatter();

  @Test
  void format_correct_instant() {
    Instant correctInstant = Instant.ofEpochSecond(1549373172); // GMT: Tuesday, February 5, 2019 1:26:12 PM
    String msSqlFormat = msSqlDateTimeUtils.format(correctInstant);
    assertThat(msSqlFormat)
        .isEqualTo("2019-02-05 13:26:12");
  }

  @Test
  void format_null_instant_throws_npe() {
    assertThrows(NullPointerException.class, () -> msSqlDateTimeUtils.format(null));
  }

  @Test
  void parse_correct_string() {
    Instant instant = msSqlDateTimeUtils.parse("2019-02-05 13:26:12");
    assertThat(instant)
        .isEqualTo(Instant.ofEpochSecond(1549373172));
  }

  @Test
  void parse_invalid_string_throws_exception() {
    assertThrows(DateTimeParseException.class, () -> msSqlDateTimeUtils.parse("2019-2-05 13:26:12"));
  }

}
