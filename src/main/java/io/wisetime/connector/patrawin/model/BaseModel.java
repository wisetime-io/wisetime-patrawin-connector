package io.wisetime.connector.patrawin.model;

import io.wisetime.generated.connect.UpsertTagRequest;
import java.time.LocalDateTime;

/**
 * @author yehor.lashkul
 */
public interface BaseModel {

  String getNumber();

  LocalDateTime getCreationTime();

  UpsertTagRequest toUpsertTagRequest(final String path);

}
