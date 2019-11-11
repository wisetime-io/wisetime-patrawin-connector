/*
 * Copyright (c) 2019 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector.patrawin.model;

import com.google.common.collect.ImmutableList;

import org.immutables.value.Value;

import io.wisetime.generated.connect.UpsertTagRequest;

/**
 * Models a Patrawin case.
 *
 * @author shane.xie@practiceinsight.io
 */
@Value.Immutable
public interface Case extends BaseModel {
  String getDescription();

  default UpsertTagRequest toUpsertTagRequest(final String path) {
    return new UpsertTagRequest()
        .name(getNumber())
        .description(getDescription())
        .path(path)
        .additionalKeywords(ImmutableList.of(getNumber()));
  }
}
