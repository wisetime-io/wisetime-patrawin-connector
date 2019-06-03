/*
 * Copyright (c) 2019 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector.patrawin;

import com.google.common.collect.ImmutableList;
import com.google.inject.Guice;
import com.google.inject.Injector;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;

import io.wisetime.connector.ConnectorModule;
import io.wisetime.connector.api_client.ApiClient;
import io.wisetime.connector.api_client.PostResult;
import io.wisetime.connector.api_client.PostResult.PostResultStatus;
import io.wisetime.connector.config.ConnectorConfigKey;
import io.wisetime.connector.config.RuntimeConfig;
import io.wisetime.connector.datastore.ConnectorStore;
import io.wisetime.connector.patrawin.fake.FakeTimeGroupGenerator;
import io.wisetime.connector.patrawin.model.Worklog;
import io.wisetime.connector.patrawin.persistence.PatrawinDao;
import io.wisetime.connector.patrawin.util.MsSqlTimeDbFormatter;
import io.wisetime.connector.patrawin.util.TimeDbFormatter;
import io.wisetime.generated.connect.Tag;
import io.wisetime.generated.connect.TimeGroup;
import io.wisetime.generated.connect.TimeRow;
import io.wisetime.generated.connect.User;
import spark.Request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author shane.xie@practiceinsight.io
 * @author galya.bogdanova@m.practiceinsight.io
 */
class PatrawinConnectorPostTimeTest {

  private static final String DEFAULT_ACTIVITY_CODE = "123456";
  private static final String NON_NUMERIC_ACTIVITY_CODE = "onetwothree";

  private static PatrawinDao patrawinDaoMock = mock(PatrawinDao.class);
  private static ApiClient apiClientMock = mock(ApiClient.class);

  private static PatrawinConnector connector;
  private static FakeTimeGroupGenerator fakeGenerator = new FakeTimeGroupGenerator();

  @BeforeAll
  static void setUp() {
    Injector injector = Guice.createInjector(binder -> {
      binder.bind(PatrawinDao.class).toProvider(() -> patrawinDaoMock);
      binder.bind(TimeDbFormatter.class).toInstance(new MsSqlTimeDbFormatter());
    });
    connector = injector.getInstance(PatrawinConnector.class);

    // Ensure PatrawinConnector#init will not fail
    doReturn(true).when(patrawinDaoMock).hasExpectedSchema();

    connector.init(new ConnectorModule(apiClientMock, mock(ConnectorStore.class)));
  }

  @BeforeEach
  void setUpTest() {
    RuntimeConfig.clearProperty(ConnectorConfigKey.CALLER_KEY);

    reset(patrawinDaoMock);

    when(patrawinDaoMock.doesUserExist(anyString()))
        .thenReturn(true);
    when(patrawinDaoMock.doesActivityCodeExist(anyInt()))
        .thenReturn(true);
    when(patrawinDaoMock.doesCaseExist(anyString()))
        .thenReturn(true);

    // Ensure that code in the transaction lambda gets exercised
    doAnswer(invocation -> {
      invocation.<Runnable>getArgument(0).run();
      return null;
    }).when(patrawinDaoMock).asTransaction(any(Runnable.class));
  }

  @Test
  void postTime_with_invalid_caller_key_should_fail() {
    RuntimeConfig.setProperty(ConnectorConfigKey.CALLER_KEY, "caller-key");

    final TimeGroup groupWithNoTags = fakeGenerator
        .randomTimeGroup()
        .callerKey("wrong-key")
        .tags(ImmutableList.of());

    assertThat(connector.postTime(mock(Request.class), groupWithNoTags).getStatus())
        .isEqualTo(PostResultStatus.PERMANENT_FAILURE);

    verify(patrawinDaoMock, never()).createWorklog(any(Worklog.class));
  }

  @Test
  void postTime_without_tags_should_succeed() {
    final TimeGroup groupWithNoTags = fakeGenerator.randomTimeGroup().tags(ImmutableList.of());

    assertThat(connector.postTime(mock(Request.class), groupWithNoTags).getStatus())
        .isEqualTo(PostResultStatus.SUCCESS);

    verify(patrawinDaoMock, never()).createWorklog(any(Worklog.class));
  }

  @Test
  void postTime_without_time_rows_should_fail() {
    final TimeGroup groupWithNoTimeRows = fakeGenerator.randomTimeGroup().timeRows(ImmutableList.of());

    assertThat(connector.postTime(mock(Request.class), groupWithNoTimeRows).getStatus())
        .isEqualTo(PostResultStatus.PERMANENT_FAILURE);

    verify(patrawinDaoMock, never()).createWorklog(any(Worklog.class));
  }

  @Test
  void postTime_nonexistent_author_should_fail() {
    final TimeGroup timeGroup = fakeGenerator.randomTimeGroup();

    when(patrawinDaoMock.doesUserExist(anyString()))
        .thenReturn(false);

    assertThat(connector.postTime(mock(Request.class), timeGroup).getStatus())
        .isEqualTo(PostResultStatus.PERMANENT_FAILURE);

    verify(patrawinDaoMock, times(1)).doesUserExist(anyString());
    verify(patrawinDaoMock, never()).createWorklog(any(Worklog.class));
  }

  @Test
  void postTime_email_used_when_no_externalId() {
    final TimeGroup timeGroup = fakeGenerator.randomTimeGroup().user(
        fakeGenerator.randomUser().externalId(null).email("email@domain.com")
    );

    ArgumentCaptor<String> userIdentityCaptor = ArgumentCaptor.forClass(String.class);
    when(patrawinDaoMock.doesUserExist(userIdentityCaptor.capture()))
        .thenReturn(false);

    connector.postTime(mock(Request.class), timeGroup);

    assertThat(userIdentityCaptor.getValue())
        .isEqualTo("email@domain.com");
  }

  @Test
  void postTime_different_timerow_modifiers_should_fail() {
    final TimeGroup timeGroup = fakeGenerator.randomTimeGroup().timeRows(ImmutableList.of(
        fakeGenerator.randomTimeRow().modifier("1"),
        fakeGenerator.randomTimeRow().modifier(null)));

    PostResult result = connector.postTime(mock(Request.class), timeGroup);
    assertThat(result.getStatus()).isEqualTo(PostResultStatus.PERMANENT_FAILURE);
    assertThat(result.getMessage()).contains("Time group has an invalid activity code");

    verify(patrawinDaoMock, never()).createWorklog(any(Worklog.class));
  }

  @Test
  void postTime_modifier_invalid_integer_should_fail() {
    final TimeGroup timeGroup = fakeGenerator.randomTimeGroup().timeRows(ImmutableList.of(
        fakeGenerator.randomTimeRow().activityTypeCode(NON_NUMERIC_ACTIVITY_CODE)));

    PostResult result = connector.postTime(mock(Request.class), timeGroup);
    assertThat(result.getStatus()).isEqualTo(PostResultStatus.PERMANENT_FAILURE);
    assertThat(result.getMessage()).contains("Time group has an invalid activity code");

    verify(patrawinDaoMock, never()).doesActivityCodeExist(anyInt());
    verify(patrawinDaoMock, never()).createWorklog(any(Worklog.class));
  }

  @Test
  void postTime_nonexistent_activity_code_should_fail() {
    final TimeGroup timeGroup = fakeGenerator.randomTimeGroup(DEFAULT_ACTIVITY_CODE);

    when(patrawinDaoMock.doesActivityCodeExist(Integer.parseInt(DEFAULT_ACTIVITY_CODE)))
        .thenReturn(false);

    assertThat(connector.postTime(mock(Request.class), timeGroup).getStatus())
        .isEqualTo(PostResultStatus.PERMANENT_FAILURE);

    verify(patrawinDaoMock, times(1)).doesActivityCodeExist(anyInt());
    verify(patrawinDaoMock, never()).createWorklog(any(Worklog.class));
  }

  @Test
  void postTime_db_transaction_error() {
    final TimeGroup timeGroup = fakeGenerator.randomTimeGroup(DEFAULT_ACTIVITY_CODE);

    RuntimeException createWorklogException = new RuntimeException("Test exception");
    doThrow(createWorklogException)
        .when(patrawinDaoMock)
        .createWorklog(any(Worklog.class));

    when(patrawinDaoMock.doesCaseExist(anyString()))
        .thenReturn(true);

    final PostResult result = connector.postTime(mock(Request.class), timeGroup);

    assertThat(result.getStatus())
        .isEqualTo(PostResultStatus.TRANSIENT_FAILURE);
    assertThat(result.getError().isPresent())
        .isTrue();
    assertThat(result.getError().get())
        .isEqualTo(createWorklogException);
  }

  @Test
  void postTime_with_valid_group_should_succeed() {
    final TimeGroup timeGroup = fakeGenerator.randomTimeGroup(DEFAULT_ACTIVITY_CODE);

    assertThat(connector.postTime(mock(Request.class), timeGroup).getStatus())
        .isEqualTo(PostResultStatus.SUCCESS);
  }

  /**
   * Should first check for case and then for client.
   */
  @Test
  void postTime_create_worklog_for_each_valid_tag() {
    final Tag existentCaseTag = fakeGenerator.randomTag();
    final Tag nonexistentCaseOrClientTag = fakeGenerator.randomTag();
    final Tag existentClientTag = fakeGenerator.randomTag();

    final TimeGroup timeGroup = fakeGenerator.randomTimeGroup(DEFAULT_ACTIVITY_CODE)
        .tags(Arrays.asList(existentCaseTag, nonexistentCaseOrClientTag, existentClientTag));

    when(patrawinDaoMock.doesCaseExist(existentCaseTag.getName()))
        .thenReturn(true);
    when(patrawinDaoMock.doesCaseExist(nonexistentCaseOrClientTag.getName()))
        .thenReturn(false);
    when(patrawinDaoMock.doesCaseExist(existentClientTag.getName()))
        .thenReturn(false);

    when(patrawinDaoMock.doesClientExist(nonexistentCaseOrClientTag.getName()))
        .thenReturn(false);
    when(patrawinDaoMock.doesClientExist(existentClientTag.getName()))
        .thenReturn(true);

    assertThat(connector.postTime(mock(Request.class), timeGroup).getStatus())
        .isEqualTo(PostResultStatus.SUCCESS);

    verify(patrawinDaoMock, times(3)).doesCaseExist(anyString());
    verify(patrawinDaoMock, times(2)).doesClientExist(anyString());

    ArgumentCaptor<Worklog> worklogCaptor = ArgumentCaptor.forClass(Worklog.class);
    verify(patrawinDaoMock, times(2)).createWorklog(worklogCaptor.capture());

    List<Worklog> createdWorklogs = worklogCaptor.getAllValues();

    assertThat(createdWorklogs)
        .hasSize(2);
    assertThat(createdWorklogs.get(0).getCaseOrClientNumber())
        .isEqualTo(existentCaseTag.getName());
    assertThat(createdWorklogs.get(1).getCaseOrClientNumber())
        .isEqualTo(existentClientTag.getName());
  }

  @Test
  void postTime_worklog_has_valid_author_id() {
    final String userExternalId = "ExternalId";
    final TimeGroup timeGroup = fakeGenerator.randomTimeGroup(DEFAULT_ACTIVITY_CODE)
        .user(fakeGenerator.randomUser().externalId(userExternalId))
        .tags(ImmutableList.of(fakeGenerator.randomTag()));

    assertThat(connector.postTime(mock(Request.class), timeGroup).getStatus())
        .isEqualTo(PostResultStatus.SUCCESS);

    ArgumentCaptor<Worklog> worklogCaptor = ArgumentCaptor.forClass(Worklog.class);
    verify(patrawinDaoMock, times(1)).createWorklog(worklogCaptor.capture());

    assertThat(worklogCaptor.getValue().getUsernameOrEmail())
        .isEqualTo(userExternalId);
  }

  @Test
  void postTime_narrative_duration_divide_between_tags() {
    final User user = fakeGenerator.randomUser().experienceWeightingPercent(50);

    final TimeRow earliestTimeRow = fakeGenerator.randomTimeRow()
        .activityTypeCode(DEFAULT_ACTIVITY_CODE).activityHour(2018110106).firstObservedInHour(57).durationSecs(66);
    final TimeRow latestTimeRow = fakeGenerator.randomTimeRow()
        .activityTypeCode(DEFAULT_ACTIVITY_CODE).activityHour(2018110110).firstObservedInHour(2).durationSecs(2400);

    List<Tag> tags = ImmutableList.of(fakeGenerator.randomTag(), fakeGenerator.randomTag());

    setPrerequisitesForSuccessfulPostTime(user, tags);

    final TimeGroup timeGroup = fakeGenerator.randomTimeGroup()
        .durationSplitStrategy(TimeGroup.DurationSplitStrategyEnum.DIVIDE_BETWEEN_TAGS)
        .narrativeType(TimeGroup.NarrativeTypeEnum.AND_TIME_ROW_ACTIVITY_DESCRIPTIONS)
        .timeRows(ImmutableList.of(earliestTimeRow, latestTimeRow))
        .tags(tags)
        .user(user)
        .totalDurationSecs(3000);

    assertThat(connector.postTime(mock(Request.class), timeGroup).getStatus())
        .isEqualTo(PostResultStatus.SUCCESS);

    ArgumentCaptor<Worklog> worklogCaptor = ArgumentCaptor.forClass(Worklog.class);
    verify(patrawinDaoMock, times(2)).createWorklog(worklogCaptor.capture());
    List<Worklog> worklogs = worklogCaptor.getAllValues();

    assertThat(worklogs.get(0).getNarrative())
        .as("time rows should be grouped by segment hour in ascending order")
        .startsWith(timeGroup.getDescription())
        .contains(
            "\r\n06:00 - 06:59\n" +
                "- 1m 6s - " + earliestTimeRow.getActivity() + " - " + earliestTimeRow.getDescription() + "\n" +
                "\r\n10:00 - 10:59\n" +
                "- 40m - " + latestTimeRow.getActivity() + " - " + latestTimeRow.getDescription()
        )
        .contains("\r\nTotal Worked Time: 41m 6s\n" +
            "Total Chargeable Time: 50m")
        .endsWith("\r\nThe above times have been split across 2 items and are thus greater than " +
            "the chargeable time in this item");

    assertThat(worklogs.get(0).getNarrative())
        .isEqualTo(worklogs.get(1).getNarrative());
  }

  @Test
  void postTime_narrative_whole_duration_each_tag() {
    final User user = fakeGenerator.randomUser().experienceWeightingPercent(50);

    final TimeRow earliestTimeRow = fakeGenerator.randomTimeRow()
        .activityTypeCode(DEFAULT_ACTIVITY_CODE).activityHour(2018110106).firstObservedInHour(57).durationSecs(66);
    final TimeRow latestTimeRow = fakeGenerator.randomTimeRow()
        .activityTypeCode(DEFAULT_ACTIVITY_CODE).activityHour(2018110110).firstObservedInHour(2).durationSecs(2400);

    List<Tag> tags = ImmutableList.of(fakeGenerator.randomTag(), fakeGenerator.randomTag());

    setPrerequisitesForSuccessfulPostTime(user, tags);

    final TimeGroup timeGroup = fakeGenerator.randomTimeGroup()
        .durationSplitStrategy(TimeGroup.DurationSplitStrategyEnum.WHOLE_DURATION_TO_EACH_TAG)
        .narrativeType(TimeGroup.NarrativeTypeEnum.AND_TIME_ROW_ACTIVITY_DESCRIPTIONS)
        .timeRows(ImmutableList.of(earliestTimeRow, latestTimeRow))
        .tags(tags)
        .user(user)
        .totalDurationSecs(3000);

    assertThat(connector.postTime(mock(Request.class), timeGroup).getStatus())
        .isEqualTo(PostResultStatus.SUCCESS);

    ArgumentCaptor<Worklog> worklogCaptor = ArgumentCaptor.forClass(Worklog.class);
    verify(patrawinDaoMock, times(2)).createWorklog(worklogCaptor.capture());
    List<Worklog> worklogs = worklogCaptor.getAllValues();

    assertThat(worklogs.get(0).getNarrative())
        .as("time rows should be grouped by segment hour in ascending order")
        .startsWith(timeGroup.getDescription())
        .contains(
            "\r\n06:00 - 06:59\n" +
                "- 1m 6s - " + earliestTimeRow.getActivity() + " - " + earliestTimeRow.getDescription() + "\n" +
                "\r\n10:00 - 10:59\n" +
                "- 40m - " + latestTimeRow.getActivity() + " - " + latestTimeRow.getDescription()
        )
        .endsWith("\r\nTotal Worked Time: 41m 6s\n" +
            "Total Chargeable Time: 50m");

    assertThat(worklogs.get(0).getNarrative())
        .isEqualTo(worklogs.get(1).getNarrative());
  }

  @Test
  void postTime_narrative_narrative_only() {
    final User user = fakeGenerator.randomUser().experienceWeightingPercent(50);

    final TimeRow earliestTimeRow = fakeGenerator.randomTimeRow()
        .activityTypeCode(DEFAULT_ACTIVITY_CODE).activityHour(2018110106).firstObservedInHour(57).durationSecs(66);
    final TimeRow latestTimeRow = fakeGenerator.randomTimeRow()
        .activityTypeCode(DEFAULT_ACTIVITY_CODE).activityHour(2018110110).firstObservedInHour(2).durationSecs(2400);

    List<Tag> tags = ImmutableList.of(fakeGenerator.randomTag(), fakeGenerator.randomTag());

    setPrerequisitesForSuccessfulPostTime(user, tags);

    final TimeGroup timeGroup = fakeGenerator.randomTimeGroup()
        .durationSplitStrategy(TimeGroup.DurationSplitStrategyEnum.WHOLE_DURATION_TO_EACH_TAG)
        .narrativeType(TimeGroup.NarrativeTypeEnum.ONLY)
        .timeRows(ImmutableList.of(earliestTimeRow, latestTimeRow))
        .tags(tags)
        .user(user)
        .totalDurationSecs(3000);

    assertThat(connector.postTime(mock(Request.class), timeGroup).getStatus())
        .isEqualTo(PostResultStatus.SUCCESS);

    ArgumentCaptor<Worklog> worklogCaptor = ArgumentCaptor.forClass(Worklog.class);
    verify(patrawinDaoMock, times(2)).createWorklog(worklogCaptor.capture());
    List<Worklog> worklogs = worklogCaptor.getAllValues();

    assertThat(worklogs.get(0).getNarrative())
        .as("time rows should be grouped by segment hour in ascending order")
        .startsWith(timeGroup.getDescription())
        .doesNotContain(earliestTimeRow.getActivity(), earliestTimeRow.getDescription())
        .doesNotContain(latestTimeRow.getActivity(), latestTimeRow.getDescription())
        .endsWith("\r\nTotal Worked Time: 41m 6s\n" +
            "Total Chargeable Time: 50m");

    assertThat(worklogs.get(0).getNarrative())
        .isEqualTo(worklogs.get(1).getNarrative());
  }

  @Test
  void postTime_worklog_has_valid_start_time() {
    final TimeGroup timeGroup = fakeGenerator.randomTimeGroup()
        .timeRows(ImmutableList.of(
            fakeGenerator.randomTimeRow()
                .activityTypeCode(DEFAULT_ACTIVITY_CODE).activityHour(2018110115).firstObservedInHour(0),
            fakeGenerator.randomTimeRow()
                .activityTypeCode(DEFAULT_ACTIVITY_CODE).activityHour(2018110114).firstObservedInHour(0)))
        .tags(ImmutableList.of(fakeGenerator.randomTag()));

    assertThat(connector.postTime(mock(Request.class), timeGroup).getStatus())
        .isEqualTo(PostResultStatus.SUCCESS);

    ArgumentCaptor<Worklog> worklogCaptor = ArgumentCaptor.forClass(Worklog.class);
    verify(patrawinDaoMock, times(1)).createWorklog(worklogCaptor.capture());

    final OffsetDateTime expectedActivityStartTime = OffsetDateTime.of(2018, 11, 1, 14, 0, 0, 0, ZoneOffset.ofHours(0));
    assertThat(worklogCaptor.getValue().getStartTime())
        .isEqualTo(expectedActivityStartTime);
  }

  /**
   * Total duration could be modified by the user.
   */
  @Test
  void postTime_worklog_has_valid_duration() {
    final TimeGroup timeGroup = fakeGenerator.randomTimeGroup()
        .user(fakeGenerator.randomUser().experienceWeightingPercent(40))
        .timeRows(ImmutableList.of(
            fakeGenerator.randomTimeRow().activityTypeCode(DEFAULT_ACTIVITY_CODE).durationSecs(60),
            fakeGenerator.randomTimeRow().activityTypeCode(DEFAULT_ACTIVITY_CODE).durationSecs(8 * 60)))
        .totalDurationSecs(20 * 60)
        .durationSplitStrategy(TimeGroup.DurationSplitStrategyEnum.DIVIDE_BETWEEN_TAGS)
        .tags(ImmutableList.of(fakeGenerator.randomTag(), fakeGenerator.randomTag()));

    assertThat(connector.postTime(mock(Request.class), timeGroup).getStatus())
        .isEqualTo(PostResultStatus.SUCCESS);

    ArgumentCaptor<Worklog> worklogCaptor = ArgumentCaptor.forClass(Worklog.class);
    verify(patrawinDaoMock, times(2)).createWorklog(worklogCaptor.capture());

    List<Worklog> allValues = worklogCaptor.getAllValues();
    assertThat(allValues.get(0).getDurationSeconds())
        .isEqualTo(540 / 2);
    assertThat(allValues.get(1).getDurationSeconds())
        .isEqualTo(540 / 2);
  }

  /**
   * Total duration could be modified by the user.
   */
  @Test
  void postTime_worklog_has_valid_chargeable_time() {
    final TimeGroup timeGroup = fakeGenerator.randomTimeGroup(DEFAULT_ACTIVITY_CODE)
        .totalDurationSecs(1000)
        .user(fakeGenerator.randomUser().experienceWeightingPercent(40))
        .tags(ImmutableList.of(fakeGenerator.randomTag())); // getPerTagDuration ?? strategy

    assertThat(connector.postTime(mock(Request.class), timeGroup).getStatus())
        .isEqualTo(PostResultStatus.SUCCESS);

    ArgumentCaptor<Worklog> worklogCaptor = ArgumentCaptor.forClass(Worklog.class);
    verify(patrawinDaoMock, times(1)).createWorklog(worklogCaptor.capture());

    final int expectedChargeableTimeSeconds = 400;
    assertThat(worklogCaptor.getValue().getChargeableTimeSeconds())
        .isEqualTo(expectedChargeableTimeSeconds);
  }

  @Test
  void postTime_post_time_not_successful() {
    final TimeGroup timeGroup = fakeGenerator.randomTimeGroup(DEFAULT_ACTIVITY_CODE)
        .totalDurationSecs(1000)
        .user(fakeGenerator.randomUser().experienceWeightingPercent(40))
        .tags(ImmutableList.of(fakeGenerator.randomTag()));
    doThrow(new IllegalStateException("Detailed error message why posting time failed"))
        .when(patrawinDaoMock).createWorklog(any());

    final PostResult result = connector.postTime(mock(Request.class), timeGroup);

    assertThat(result.getStatus())
        .as("should return permanent failure if Patrawin rejected the posted time")
        .isEqualTo(PostResultStatus.PERMANENT_FAILURE);
    assertThat(result.getMessage())
        .as("reason for failure should be the msg of the IllegalStateException")
        .contains("Detailed error message why posting time failed");
  }

  private void setPrerequisitesForSuccessfulPostTime(User user, List<Tag> tags) {
    when(patrawinDaoMock.doesUserExist(user.getExternalId()))
        .thenReturn(true);

    tags.forEach(tag -> when(patrawinDaoMock.doesCaseExist(tag.getName()))
        .thenReturn(true));
  }
}
