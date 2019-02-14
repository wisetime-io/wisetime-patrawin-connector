/*
 * Copyright (c) 2019 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector.patrawin.util;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

/**
 * @author galya.bogdanova@m.practiceinsight.io
 */
public interface TimeDbFormatter {

  String format(LocalDateTime dateTime);

  String format(OffsetDateTime offsetDateTime);

  LocalDateTime parseDateTime(String msqlDateTime);
}
