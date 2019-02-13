/*
 * Copyright (c) 2019 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector.patrawin.util;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAccessor;

/**
 * @author galya.bogdanova@m.practiceinsight.io
 */
public interface TimeDbFormatter {

  String format(TemporalAccessor dateTime);

  LocalDateTime parse(String msqlDateTime);
}
