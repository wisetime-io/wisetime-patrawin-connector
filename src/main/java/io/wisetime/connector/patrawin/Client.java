/*
 * Copyright (c) 2019 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector.patrawin;

import com.google.common.collect.ImmutableList;

import org.immutables.value.Value;

import java.time.Instant;

import io.wisetime.generated.connect.UpsertTagRequest;

/**
 * Models a Patrawin client.
 *
 * @author shane.xie@practiceinsight.io
 */
@Value.Immutable
public interface Client {
  String getClientId();
  String getAlias();
  Instant getCreationTime();

  default UpsertTagRequest toUpsertTagRequest(final String path) {
    return new UpsertTagRequest()
        .name(getClientId())
        .description(getAlias())
        .path(path)
        .additionalKeywords(ImmutableList.of(getClientId()));
  }
}
