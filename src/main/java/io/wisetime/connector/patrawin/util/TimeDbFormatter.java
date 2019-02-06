/*
 * Copyright (c) 2019 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector.patrawin.util;

import java.time.Instant;
import java.time.LocalDateTime;

/**
 * @author galya.bogdanova@m.practiceinsight.io
 */
public interface TimeDbFormatter {

  String format(Instant instant);

  Instant parse(String dateTime);

  Instant convert(LocalDateTime localDateTime);
}
