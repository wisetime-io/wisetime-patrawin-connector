/*
 * Copyright (c) 2019 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector.patrawin.model;

import io.wisetime.generated.connect.UpsertTagRequest;

public interface TagRequestConvert {
  UpsertTagRequest toUpsertTagRequest(final String path);
}
