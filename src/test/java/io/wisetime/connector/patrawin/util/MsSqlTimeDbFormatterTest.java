/*
 * Copyright (c) 2019 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector.patrawin.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
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
    LocalDateTime correctDateTime = LocalDateTime.of(2019, 2, 5, 13, 26, 12);
    String msSqlFormat = msSqlDateTimeUtils.format(correctDateTime);
    assertThat(msSqlFormat)
        .isEqualTo("2019-02-05 13:26:12");
  }

  @Test
  void format_null_instant_throws_npe() {
    assertThrows(NullPointerException.class, () -> msSqlDateTimeUtils.format(null));
  }

  @Test
  void parse_correct_string() {
    LocalDateTime localDateTime = msSqlDateTimeUtils.parse("2019-02-05 13:26:12");
    assertThat(localDateTime)
        .isEqualTo(LocalDateTime.of(2019, 2, 5, 13, 26, 12));
  }

  @Test
  void parse_invalid_string_throws_exception() {
    assertThrows(DateTimeParseException.class, () -> msSqlDateTimeUtils.parse("2019-2-05 13:26:12"));
  }

}
