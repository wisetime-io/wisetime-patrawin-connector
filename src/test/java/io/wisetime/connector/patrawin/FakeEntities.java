/*
 * Copyright (c) 2019 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector.patrawin;

import com.github.javafaker.Faker;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Generator of entities with random field values. Typically used to mock real data.
 *
 * @author alvin.llobrera@practiceinsight.io
 */
public class FakeEntities {

  private static final Faker FAKER = new Faker();

  public Case randomCase(ZonedDateTime createdTime) {
    return ImmutableCase.builder()
        .caseNumber(FAKER.bothify("?####"))
        .description(FAKER.superhero().descriptor())
        .creationTime(createdTime.toInstant())
        .build();
  }

  public Client randomClient(ZonedDateTime createdTime) {
    return ImmutableClient.builder()
        .clientId(FAKER.numerify("1###"))
        .alias(FAKER.superhero().descriptor())
        .creationTime(createdTime.toInstant())
        .build();
  }

  private <T> List<T> randomEntities(final Supplier<T> supplier, final int min, final int max) {
    return IntStream
        .range(0, FAKER.random().nextInt(min, max))
        .mapToObj(i -> supplier.get())
        .collect(Collectors.toList());
  }

  private static <T extends Enum<?>> T randomEnum(final Class<T> clazz) {
    final int index = FAKER.random().nextInt(clazz.getEnumConstants().length);
    return clazz.getEnumConstants()[index];
  }
}
