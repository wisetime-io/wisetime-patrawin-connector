/*
 * Copyright (c) 2019 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector.patrawin.model;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * @author yehor.lashkul
 */
@Data
@SuperBuilder
public class BaseModel {

  String number;

  LocalDateTime creationTime;

}
