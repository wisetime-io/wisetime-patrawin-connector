/*
 * Copyright (c) 2019 Practice Insight Pty Ltd. All rights reserved.
 */

package io.wisetime.connector.patrawin.persistence;

import static io.wisetime.connector.patrawin.persistence.PatrawinDao.MIN_SQL_DATE_TIME;

import com.github.javafaker.Faker;
import com.google.common.base.Strings;
import io.wisetime.connector.patrawin.model.Case;
import io.wisetime.connector.patrawin.model.Client;
import io.wisetime.connector.patrawin.persistence.PatrawinDaoTest.PendingTime;
import io.wisetime.connector.patrawin.util.TimeDbFormatter;
import io.wisetime.generated.connect.User;
import java.time.LocalDateTime;
import org.codejargon.fluentjdbc.api.FluentJdbc;
import org.codejargon.fluentjdbc.api.mapper.Mappers;

/**
 * @author alvin.llobrera, thomas.haines
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
        .update("INSERT INTO BEHORIG_50 (Username, Email, Namn, Officeid, Isactive, Isattorney) "
        + "VALUES (?, ?, ?, 1, 1, 1)")
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
        .update("INSERT INTO FAKTURATEXTNR_15 (Fakturatextnr, AmountIncludedInStatistics, "
        + "HoursIncludedInStatistics, Inaktiv) VALUES (?, 1, 1, ?)")
        .params(activityCode, active ? 0 : 1)
        .run()
        .affectedRows() == 1;
  }

  boolean createActivityTypeLabel(int activityCode) {
    fluentJdbc.query()
        .update(
            "INSERT INTO FAKTURATEXT_16 (Fakturatextnr, Sprakkod, Skuggfakturatext, Rowid, Fakturatext )"
        + " VALUES (?, '1', '1', DEFAULT,?)")
        .params(activityCode, activityCode)
        .run();
    fluentJdbc.query()
        .update(
            "INSERT INTO FORETAGINFO_56 (Skarmsprak,Companyid,Patent_electronic,"
                + "Trademark_electronic,Design_electronic,"
                + "Domain_electronic,Div_patent_electronic,"
                + "Div_trademark_electronic,Div_design_electronic,"
                + "Div_various_electronic)"
        + " VALUES ('1','1','1','1','1','1','1','1','1','1')")
        .run();
    return createActivityCode(activityCode);
  }

  Case createCase(Case patrawinCase) {
    fluentJdbc.query()
        .update("INSERT INTO ARENDE_1 (Arendenr, Slagord, Skapatdat, Rowguid, Officeid, Electronic_file, "
        + "Excludedfromiprcontrol, Outsourced) VALUES (?, ?, ?, NEWID(), 1, 1, 0, 0)")
        .params(
            patrawinCase.getNumber(),
            patrawinCase.getDescription(),
            // set null in table for really old dates
            patrawinCase.getCreationTime().compareTo(LocalDateTime.now().withYear(1975)) < 0
                ? null : timeDbFormatter.format(patrawinCase.getCreationTime()))
        .run();

    // MSSQL's DATETIME are rounded to increments of .000, .003 or .007 seconds
    // https://docs.microsoft.com/en-us/sql/t-sql/data-types/datetime-transact-sql?view=sql-server-2017
    // Let's query the created case so we can have reference to the actual created date
    return fluentJdbc.query()
        .select("SELECT Arendenr, Slagord, COALESCE(Skapatdat, :minDate) FROM ARENDE_1 WHERE Arendenr = :case")
        .namedParam("case", patrawinCase.getNumber())
        .namedParam("minDate", timeDbFormatter.format(MIN_SQL_DATE_TIME))
        .singleResult(rs -> Case.builder()
            .description(rs.getString(2))
            .number(rs.getString(1))
            .creationTime(timeDbFormatter.parseDateTime(rs.getString(3)))
            .build()
        );
  }

  Client createClient(Client client) {
    return createClient(client, FAKER.letterify("?"));
  }

  Client createClient(Client client, String creditCode) {
    fluentJdbc.query()
        .update("INSERT INTO KUND_24 "
        + "(Kundnr, Kortnamnkund, Skapatdat, Valutakod, Landkod, Sprakkod, Rowguid, Einvoicetype, Xmlinvoicetypeid, "

        + "Einvoiceaccent, Enableipforecaster, Automatfakturajn, Usebasicoutsourcingsurcharge, IsAgentInFile, Kreditjn) "

        + "VALUES (?, ?, ?, 'N', 'N', 'N', NEWID(), 0, 0, 0, 0, 'N', 0, 0, ?)")
        .params(
            client.getNumber(),
            client.getAlias(),
            // set null in table for really old dates
            client.getCreationTime().compareTo(LocalDateTime.now().withYear(1975)) < 0
                ? null : timeDbFormatter.format(client.getCreationTime()),
            creditCode)
        .run();

    // MSSQL's DATETIME are rounded to increments of .000, .003 or .007 seconds
    // https://docs.microsoft.com/en-us/sql/t-sql/data-types/datetime-transact-sql?view=sql-server-2017
    // Let's query the created case so we can have reference to the actual created date
    return fluentJdbc.query()
        .select("SELECT Kundnr, Kortnamnkund, COALESCE(Skapatdat, :minDate) FROM KUND_24 WHERE Kundnr = :client")
        .namedParam("client", client.getNumber())
        .namedParam("minDate", timeDbFormatter.format(MIN_SQL_DATE_TIME))
        .singleResult(rs ->
            Client.builder()
                .alias(rs.getString(2))
                .number(rs.getString(1))
                .creationTime(timeDbFormatter.parseDateTime(rs.getString(3)))
                .build()
        );
  }

  void createCreditCode(String creditCode, boolean blocked) {
    final int creditCodeType = blocked ? 2 : 0; // 2 means blocked

    // Set credit code of client as blocked
    fluentJdbc.query()
        .update("INSERT INTO CREDIT_LEVEL_334 (Creditcode, Type) VALUES (?, ?)")
        .params(creditCode, creditCodeType)
        .run();
  }

  Case createCaseWithClient(Case patrawinCase, Client client) {
    final String creditCode = FAKER.letterify("?");
    createCreditCode(creditCode, false);

    Case createdCase = createCase(patrawinCase);
    createClient(client, creditCode);
    linkCaseToClient(patrawinCase, client);
    return createdCase;
  }

  void linkCaseToClient(Case patrawinCase, Client client) {
    fluentJdbc.query()
        .update("INSERT INTO KUND_ARENDE_25 (Arendenr, Kundnr, Part, Kundtyp) VALUES (?, ?, 1, 2)")
        .params(patrawinCase.getNumber(), client.getNumber())
        .run();
  }

  PatrawinDaoTest.PendingTime getCreatedPendingTime(String clientNumber) {
    return fluentJdbc.query()
        .select("SELECT User_Id, Arendenr, Kundnr, StartTimeUtc, Minutes, Fakturatextnr, Text FROM PENDING_TIME_335 "
        + "WHERE Kundnr = ?")
        .params(clientNumber)
        .singleResult(rs -> {
          PendingTime pendingTime = new PendingTime();
          return pendingTime.setUserId(rs.getLong("User_Id"))
              .setCaseNum(Strings.nullToEmpty(rs.getString("Arendenr")))
              .setClientNum(rs.getString("Kundnr"))
              .setStartTimeUtc(rs.getString("StartTimeUtc"))
              .setMinutes(rs.getInt("Minutes"))
              .setServiceNum(rs.getInt("Fakturatextnr"))
              .setNarrative(rs.getString("Text"));
        });
  }
}
