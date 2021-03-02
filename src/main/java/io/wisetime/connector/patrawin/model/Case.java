/*
 * Copyright (c) 2019 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector.patrawin.model;

import com.google.common.collect.ImmutableList;
import io.wisetime.generated.connect.UpsertTagRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * Models a Patrawin case.
 *
 * @author shane.xie@practiceinsight.io
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class Case extends BaseModel implements TagRequestConvert {
  String description;

  public UpsertTagRequest toUpsertTagRequest(final String path) {
    return new UpsertTagRequest()
        .name(getNumber())
        .description(description)
        .path(path)
        .additionalKeywords(ImmutableList.of(getNumber()));
  }
}
