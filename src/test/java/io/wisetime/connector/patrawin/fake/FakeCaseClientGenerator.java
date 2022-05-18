/*
 * Copyright (c) 2019 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector.patrawin.fake;

import com.github.javafaker.Faker;
import io.wisetime.connector.patrawin.model.Case;
import io.wisetime.connector.patrawin.model.Client;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author shane.xie@practiceinsigt.io
 */
public class FakeCaseClientGenerator {

  private static final Faker FAKER = new Faker();

  public Case randomCase() {
    return randomCase(LocalDateTime.now()).build();
  }

  public Case.CaseBuilder<?, ?> randomCase(LocalDateTime createdTime) {
    return Case.builder()
        .description(FAKER.lorem().characters(12, 30))
        .creationTime(createdTime)
        .number(FAKER.bothify("??####", true));
  }

  public List<Case> randomCases(int count) {
    return randomEntities(this::randomCase, count, count);
  }

  public Client randomClient() {
    return randomClient(LocalDateTime.now()).build();
  }

  public Client.ClientBuilder<?, ?> randomClient(LocalDateTime createdTime) {
    return Client.builder()
        .alias(FAKER.company().name())
        .number(FAKER.bothify("??###", true))
        .creationTime(createdTime);
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
