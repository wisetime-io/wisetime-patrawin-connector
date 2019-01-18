/*
 * Copyright (c) 2019 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector.patrawin;

import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.com.google.common.collect.ImmutableList;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author shane.xie@practiceinsight.io
 */
public class PatrawinConnectorUtilsTest {

  @Test
  void printLast_none() {
    assertThat(PatrawinConnector.printLast(ImmutableList.of()))
        .isEqualTo("None yet");
  }

  @Test
  void printLast_some() {
    assertThat(PatrawinConnector.printLast(ImmutableList.of("1", "2")))
        .isEqualTo("2");
  }
}
