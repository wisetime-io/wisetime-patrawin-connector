/*
 * Copyright (c) 2020 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector.patrawin;

import com.google.inject.Singleton;
import io.wisetime.connector.patrawin.persistence.PatrawinDao.ActivityTypeLabel;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;


@Singleton
public class HashFunction {

  public String hashStrings(List<String> strings) {
    return hash(strings, Function.identity());
  }

  public String hashActivities(List<ActivityTypeLabel> activities) {
    return hash(activities, activityTypeLabel -> activityTypeLabel.getId()
        + StringUtils.trimToEmpty(activityTypeLabel.getLabel()));
  }

  private <T> String hash(List<T> list, Function<T, String> toString) {
    final String listString = list.stream().map(toString).collect(Collectors.joining());
    return DigestUtils.md5Hex(listString);
  }
}
