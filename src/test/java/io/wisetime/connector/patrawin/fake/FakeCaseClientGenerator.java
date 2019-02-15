/*
 * Copyright (c) 2019 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector.patrawin.fake;

import com.github.javafaker.Faker;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import io.wisetime.connector.patrawin.model.Case;
import io.wisetime.connector.patrawin.model.Client;
import io.wisetime.connector.patrawin.model.ImmutableCase;
import io.wisetime.connector.patrawin.model.ImmutableClient;

/**
 * @author shane.xie@practiceinsigt.io
 */
public class FakeCaseClientGenerator {

  private static final Faker FAKER = new Faker();

  public Case randomCase() {
    return randomCase(LocalDateTime.now());
  }

  public Case randomCase(LocalDateTime createdTime) {
    return ImmutableCase.builder()
        .caseNumber(FAKER.bothify("??####", true))
        .description(FAKER.lorem().characters(12, 30))
        .creationTime(createdTime)
        .build();
  }

  public List<Case> randomCases(int count) {
    return randomEntities(this::randomCase, count, count);
  }

  public Client randomClient() {
    return randomClient(LocalDateTime.now());
  }

  public Client randomClient(LocalDateTime createdTime) {
    return ImmutableClient.builder()
        .clientNumber(FAKER.bothify("??###", true))
        .alias(FAKER.company().name())
        .creationTime(createdTime)
        .build();
  }

  public List<Client> randomClients(int count) {
    return randomEntities(this::randomClient, count, count);
  }

  private <T> List<T> randomEntities(final Supplier<T> supplier, final int min, final int max) {
    return IntStream
        .range(0, FAKER.random().nextInt(min, max))
        .mapToObj(i -> supplier.get())
        .collect(Collectors.toList());
  }
}
