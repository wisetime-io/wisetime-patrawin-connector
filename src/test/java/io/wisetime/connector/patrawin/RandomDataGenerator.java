/*
 * Copyright (c) 2019 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector.patrawin;

import com.github.javafaker.Faker;

import java.time.Instant;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author shane.xie@practiceinsigt.io
 */
public class RandomDataGenerator {

  private static final Faker FAKER = new Faker();

  Case randomCase() {
    return randomCase(Instant.now());
  }

  public Case randomCase(Instant createdTime) {
    return ImmutableCase.builder()
        .caseNumber(FAKER.bothify("??####", true))
        .description(FAKER.lorem().characters(12, 30))
        .creationTime(createdTime)
        .build();
  }

  List<Case> randomCases(int count) {
    return randomEntities(this::randomCase, count, count);
  }

  Client randomClient() {
    return randomClient(Instant.now());
  }

  public Client randomClient(Instant createdTime) {
    return ImmutableClient.builder()
        .clientId(FAKER.bothify("??###", true))
        .alias(FAKER.company().name())
        .creationTime(createdTime)
        .build();
  }

  List<Client> randomClients(int count) {
    return randomEntities(this::randomClient, count, count);
  }

  private <T> List<T> randomEntities(final Supplier<T> supplier, final int min, final int max) {
    return IntStream
        .range(0, FAKER.random().nextInt(min, max))
        .mapToObj(i -> supplier.get())
        .collect(Collectors.toList());
  }
}
