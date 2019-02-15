/*
 * Copyright (c) 2019 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector.patrawin.model;

import com.google.common.collect.ImmutableList;

import org.immutables.value.Value;

import java.time.LocalDateTime;

import io.wisetime.generated.connect.UpsertTagRequest;

/**
 * Models a Patrawin case.
 *
 * @author shane.xie@practiceinsight.io
 */
@Value.Immutable
public interface Case {
  String getCaseNumber();

  String getDescription();

  LocalDateTime getCreationTime();

  default UpsertTagRequest toUpsertTagRequest(final String path) {
    return new UpsertTagRequest()
        .name(getCaseNumber())
        .description(getDescription())
        .path(path)
        .additionalKeywords(ImmutableList.of(getCaseNumber()));
  }
}
