/*
 * Copyright (c) 2019 Practice Insight Pty Ltd. All rights reserved.
 */

package io.wisetime.connector.patrawin.persistence;

import com.google.common.base.Strings;

import com.github.javafaker.Faker;

import org.codejargon.fluentjdbc.api.FluentJdbc;
import org.codejargon.fluentjdbc.api.mapper.Mappers;

import io.wisetime.connector.patrawin.model.Case;
import io.wisetime.connector.patrawin.model.Client;
import io.wisetime.connector.patrawin.model.ImmutableCase;
import io.wisetime.connector.patrawin.model.ImmutableClient;
import io.wisetime.connector.patrawin.util.TimeDbFormatter;
import io.wisetime.generated.connect.User;

/**
 * @author alvin.llobrera@practiceinsight.io
 */
public class PatrawinDaoTestUtils {

  private static final Faker FAKER = new Faker();

  private TimeDbFormatter timeDbFormatter;
  private FluentJdbc fluentJdbc;

  public PatrawinDaoTestUtils(FluentJdbc fluentJdbc, TimeDbFormatter timeDbFormatter) {
    this.fluentJdbc = fluentJdbc;
    this.timeDbFormatter = timeDbFormatter;
  }

  long createUser(User patrawinUser) {
    return fluentJdbc.query()
        .update("INSERT INTO BEHORIG_50 (Username, Email, Namn, Officeid, Isactive, Isattorney) " +
            "VALUES (?, ?, ?, 1, 1, 1)")
        .params(
            patrawinUser.getExternalId(),
            patrawinUser.getEmail(),
            patrawinUser.getName())
        .runFetchGenKeys(Mappers.singleLong())
        .generatedKeys()
        .get(0);
  }

  boolean createActivityCode(int activityCode) {
    return createActivityCode(activityCode, true);
  }

  boolean createActivityCode(int activityCode, boolean active) {
    return fluentJdbc.query()
        .update("INSERT INTO FAKTURATEXTNR_15 (Fakturatextnr, AmountIncludedInStatistics, " +
            "HoursIncludedInStatistics, Inaktiv) VALUES (?, 1, 1, ?)")
        .params(activityCode, active ? 0 : 1)
        .run()
        .affectedRows() == 1;
  }

  Case createCase(Case patrawinCase) {
    fluentJdbc.query()
        .update("INSERT INTO ARENDE_1 (Arendenr, Slagord, Skapatdat, Rowguid, Officeid, Electronic_file, " +
            "Excludedfromiprcontrol, Outsourced) VALUES (?, ?, ?, NEWID(), 1, 1, 0, 0)")
        .params(
            patrawinCase.getNumber(),
            patrawinCase.getDescription(),
            timeDbFormatter.format(patrawinCase.getCreationTime()))
        .run();

    // MSSQL's DATETIME are rounded to increments of .000, .003 or .007 seconds
    // https://docs.microsoft.com/en-us/sql/t-sql/data-types/datetime-transact-sql?view=sql-server-2017
    // Let's query the created case so we can have reference to the actual created date
    return fluentJdbc.query()
        .select("SELECT Arendenr, Slagord, Skapatdat FROM ARENDE_1 WHERE Arendenr = ?")
        .params(patrawinCase.getNumber())
        .singleResult(rs -> ImmutableCase.builder()
            .number(rs.getString(1))
            .description(rs.getString(2))
            .creationTime(timeDbFormatter.parseDateTime(rs.getString(3)))
            .build());
  }

  Client createClient(Client client) {
    return createClient(client, FAKER.letterify("?"));
  }

  Client createClient(Client client, String creditCode) {
    fluentJdbc.query()
        .update("INSERT INTO KUND_24 " +
            "(Kundnr, Kortnamnkund, Skapatdat, Valutakod, Landkod, Sprakkod, Rowguid, Einvoicetype, Xmlinvoicetypeid, " +
            "Einvoiceaccent, Enableipforecaster, Automatfakturajn, Usebasicoutsourcingsurcharge, IsAgentInFile, Kreditjn) " +
            "VALUES (?, ?, ?, 'N', 'N', 'N', NEWID(), 0, 0, 0, 0, 'N', 0, 0, ?)")
        .params(
            client.getNumber(),
            client.getAlias(),
            timeDbFormatter.format(client.getCreationTime()),
            creditCode)
        .run();

    // MSSQL's DATETIME are rounded to increments of .000, .003 or .007 seconds
    // https://docs.microsoft.com/en-us/sql/t-sql/data-types/datetime-transact-sql?view=sql-server-2017
    // Let's query the created case so we can have reference to the actual created date
    return fluentJdbc.query()
        .select("SELECT Kundnr, Kortnamnkund, Skapatdat FROM KUND_24 WHERE Kundnr = ?")
        .params(client.getNumber())
        .singleResult(rs -> ImmutableClient.builder()
            .number(rs.getString(1))
            .alias(rs.getString(2))
            .creationTime(timeDbFormatter.parseDateTime(rs.getString(3)))
            .build());
  }

  void createCreditCode(String creditCode, boolean blocked) {
    final int creditCodeType = blocked ? 2 : 0; // 2 means blocked

    // Set credit code of client as blocked
    fluentJdbc.query()
        .update("INSERT INTO CREDIT_LEVEL_334 (Creditcode, Type) VALUES (?, ?)")
        .params(creditCode, creditCodeType)
        .run();
  }

  void createCaseWithClient(Case patrawinCase, Client client) {
    final String creditCode = FAKER.letterify("?");
    createCreditCode(creditCode, false);

    createCase(patrawinCase);
    createClient(client, creditCode);

    fluentJdbc.query()
        .update("INSERT INTO KUND_ARENDE_25 (Arendenr, Kundnr, Part, Kundtyp) VALUES (?, ?, 1, 2)")
        .params(patrawinCase.getNumber(), client.getNumber())
        .run();
  }

  PatrawinDaoTest.PendingTime getCreatedPendingTime(String clientNumber) {
    return fluentJdbc.query()
        .select("SELECT User_Id, Arendenr, Kundnr, StartTimeUtc, Minutes, Fakturatextnr, Text FROM PENDING_TIME_335 " +
            "WHERE Kundnr = ?")
        .params(clientNumber)
        .singleResult(rs ->
            ImmutablePendingTime.builder()
                .userId(rs.getLong("User_Id"))
                .caseNum(Strings.nullToEmpty(rs.getString("Arendenr")))
                .clientNum(rs.getString("Kundnr"))
                .startTimeUtc(rs.getString("StartTimeUtc"))
                .minutes(rs.getInt("Minutes"))
                .serviceNum(rs.getInt("Fakturatextnr"))
                .narrative(rs.getString("Text"))
                .build()
        );
  }
}
