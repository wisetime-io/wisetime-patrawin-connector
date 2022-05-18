/*
 * Copyright (c) 2019 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector.patrawin.model;

import java.time.OffsetDateTime;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author galya.bogdanova, thomas.haines
 */
@Data
@Accessors(chain = true)
public class Worklog {
  String caseOrClientNumber;

  String usernameOrEmail;

  int activityCode;

  /*
   * Example narrative:
   *
   * Replying email regarding purchasing agreement and finalising same
   * 13:12 [5 mins] – Outlook – [P1033] Re: Latest numbers
   * 13:17 [11 mins] – Chrome – About ACME Corp
   * 13:22 [2 mins] – Finder – P1033
   * 13:25 [33 mins] – Word – ACME Corp Purchase Agreement [P1033]
   * 15:01 [7 mins] – Outlook – [P1033] Finalised ACME Corp Agreement
   *
   * @return A formatted narrative for the time being recorded. Uses Windows line breaks.
   */
  String narrative;

  OffsetDateTime startTime;

  int durationSeconds;

  int chargeableTimeSeconds;
}
