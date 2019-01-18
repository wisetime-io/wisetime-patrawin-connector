/*
 * Copyright (c) 2019 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector.patrawin;

import org.immutables.value.Value;

import java.time.Instant;

/**
 * Models a Patrawin case.
 *
 * @author shane.xie@practiceinsight.io
 */
@Value.Immutable
public interface Case {
  String getCaseNumber();
  String getDescription();
  Instant getCreationTime();
}
