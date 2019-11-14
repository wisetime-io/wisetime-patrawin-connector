/*
 * Copyright (c) 2019 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector.patrawin.model;

import com.google.common.collect.ImmutableList;

import org.immutables.value.Value;

import java.time.LocalDateTime;

import io.wisetime.generated.connect.UpsertTagRequest;

/**
 * Models a Patrawin client.
 *
 * @author shane.xie@practiceinsight.io
 */
@Value.Immutable
public interface Client extends BaseModel {
  String getAlias();

  default UpsertTagRequest toUpsertTagRequest(final String path) {
    return new UpsertTagRequest()
        .name(getNumber())
        .description(getAlias())
        .path(path)
        .additionalKeywords(ImmutableList.of(getNumber()));
  }
}
