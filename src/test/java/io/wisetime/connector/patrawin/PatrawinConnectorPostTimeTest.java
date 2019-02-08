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

import io.wisetime.connector.api_client.ApiClient;
import io.wisetime.connector.api_client.PostResult;
import io.wisetime.connector.config.ConnectorConfigKey;
import io.wisetime.connector.config.RuntimeConfig;
import io.wisetime.connector.datastore.ConnectorStore;
import io.wisetime.connector.integrate.ConnectorModule;
import io.wisetime.connector.patrawin.fake.FakeEntities;
import io.wisetime.connector.patrawin.fake.RandomDataGenerator;
import io.wisetime.connector.patrawin.model.Worklog;
import io.wisetime.connector.patrawin.persistence.PatrawinDao;
import io.wisetime.connector.patrawin.util.MsSqlTimeDbFormatter;
import io.wisetime.connector.patrawin.util.TimeDbFormatter;
import io.wisetime.connector.template.TemplateFormatter;
import io.wisetime.generated.connect.TimeGroup;
import spark.Request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
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
public class PatrawinConnectorPostTimeTest {

  private static PatrawinDao patrawinDao = mock(PatrawinDao.class);
  private static ApiClient apiClient = mock(ApiClient.class);
  private static TemplateFormatter narrativeFormatter = mock(TemplateFormatter.class);

  private static PatrawinConnector connector;
  private static FakeEntities fakeEntities = new FakeEntities();
  private static RandomDataGenerator randomDataGenerator = new RandomDataGenerator();

  @BeforeAll
  static void setUp() {
    Injector injector = Guice.createInjector(binder -> {
      binder.bind(PatrawinDao.class).toProvider(() -> patrawinDao);
      binder.bind(TimeDbFormatter.class).toInstance(new MsSqlTimeDbFormatter());
    });
    connector = injector.getInstance(PatrawinConnector.class);

    // Ensure PatrawinConnector#init will not fail
    doReturn(true).when(patrawinDao).hasExpectedSchema();

    connector.init(new ConnectorModule(apiClient, mock(ConnectorStore.class)));
  }

  @BeforeEach
  void setUpTest() {
    RuntimeConfig.clearProperty(ConnectorConfigKey.CALLER_KEY);

    reset(narrativeFormatter);
    reset(patrawinDao);

    when(patrawinDao.doesUserExist(anyString()))
        .thenReturn(true);

    // Ensure that code in the transaction lambda gets exercised
    doAnswer(invocation -> {
      invocation.<Runnable>getArgument(0).run();
      return null;
    }).when(patrawinDao).asTransaction(any(Runnable.class));
  }

  @Test
  void postTime_with_invalid_caller_key_should_fail() {
    RuntimeConfig.setProperty(ConnectorConfigKey.CALLER_KEY, "caller-key");

    final TimeGroup groupWithNoTags = fakeEntities
        .randomTimeGroup()
        .callerKey("wrong-key")
        .tags(ImmutableList.of());

    assertThat(connector.postTime(mock(Request.class), groupWithNoTags))
        .isEqualTo(PostResult.PERMANENT_FAILURE);

    verify(patrawinDao, never()).createWorklog(any(Worklog.class));
  }

  @Test
  void postTime_without_tags_should_succeed() {
    final TimeGroup groupWithNoTags = fakeEntities.randomTimeGroup().tags(ImmutableList.of());

    assertThat(connector.postTime(mock(Request.class), groupWithNoTags))
        .isEqualTo(PostResult.SUCCESS);

    verify(patrawinDao, never()).createWorklog(any(Worklog.class));
  }

  @Test
  void postTime_without_time_rows_should_fail() {
    final TimeGroup groupWithNoTimeRows = fakeEntities.randomTimeGroup().timeRows(ImmutableList.of());

    assertThat(connector.postTime(mock(Request.class), groupWithNoTimeRows))
        .isEqualTo(PostResult.PERMANENT_FAILURE);

    verify(patrawinDao, never()).createWorklog(any(Worklog.class));
  }

  @Test
  void postTime_nonexistent_author_should_fail() {
    final TimeGroup timeGroup = fakeEntities.randomTimeGroup();

    when(patrawinDao.doesUserExist(anyString()))
        .thenReturn(false);

    assertThat(connector.postTime(mock(Request.class), timeGroup))
        .isEqualTo(PostResult.PERMANENT_FAILURE);

    verify(patrawinDao, times(1)).doesUserExist(anyString());
    verify(patrawinDao, never()).createWorklog(any(Worklog.class));
  }

  @Test
  void postTime_email_used_when_no_externalId() {
    final TimeGroup timeGroup = fakeEntities.randomTimeGroup().user(
        fakeEntities.randomUser().externalId(null).email("email@domain.com")
    );

    ArgumentCaptor<String> userIdentityCaptor = ArgumentCaptor.forClass(String.class);
    when(patrawinDao.doesUserExist(userIdentityCaptor.capture()))
        .thenReturn(false);

    connector.postTime(mock(Request.class), timeGroup);

    assertThat(userIdentityCaptor.getValue())
        .isEqualTo("email@domain.com");
  }

  @Test
  void postTime_different_timerow_modifiers_should_fail() {
    final TimeGroup timeGroup = fakeEntities.randomTimeGroup().timeRows(ImmutableList.of(
        fakeEntities.randomTimeRow().modifier("1"),
        fakeEntities.randomTimeRow().modifier(null)));

    assertThat(connector.postTime(mock(Request.class), timeGroup))
        .isEqualTo(PostResult.PERMANENT_FAILURE);

    verify(patrawinDao, never()).createWorklog(any(Worklog.class));
  }

  @Test
  void postTime_no_timerow_modifiers_no_default_should_fail() {
    RuntimeConfig.setProperty(ConnectorLauncher.PatrawinConnectorConfigKey.DEFAULT_MODIFIER, null);

    final TimeGroup timeGroup = fakeEntities.randomTimeGroup().timeRows(ImmutableList.of(
        fakeEntities.randomTimeRow().modifier(null),
        fakeEntities.randomTimeRow().modifier(null)));

    assertThat(connector.postTime(mock(Request.class), timeGroup))
        .isEqualTo(PostResult.PERMANENT_FAILURE);

    verify(patrawinDao, never()).createWorklog(any(Worklog.class));
  }

  @Test
  void postTime_default_modifier_used_when_no_timegroup_modifier() {
    RuntimeConfig.setProperty(ConnectorLauncher.PatrawinConnectorConfigKey.DEFAULT_MODIFIER, "5");

    final TimeGroup timeGroup = fakeEntities.randomTimeGroup().timeRows(ImmutableList.of(
        fakeEntities.randomTimeRow().modifier(null)));

    ArgumentCaptor<Integer> modifierCaptor = ArgumentCaptor.forClass(Integer.class);
    when(patrawinDao.doesActivityCodeExist(modifierCaptor.capture()))
        .thenReturn(false);

    connector.postTime(mock(Request.class), timeGroup);

    assertThat(modifierCaptor.getValue())
        .isEqualTo(5);
  }

  @Test
  void postTime_modifier_invalid_integer_should_fail() {
    final TimeGroup timeGroup = fakeEntities.randomTimeGroup().timeRows(ImmutableList.of(
        fakeEntities.randomTimeRow().modifier("Modifier")));

    assertThat(connector.postTime(mock(Request.class), timeGroup))
        .isEqualTo(PostResult.PERMANENT_FAILURE);

    verify(patrawinDao, never()).doesActivityCodeExist(anyInt());
    verify(patrawinDao, never()).createWorklog(any(Worklog.class));
  }

  @Test
  void postTime_nonexistent_activity_code_should_fail() {
    final TimeGroup timeGroup = fakeEntities.randomTimeGroup();

    when(patrawinDao.doesActivityCodeExist(anyInt()))
        .thenReturn(false);

    assertThat(connector.postTime(mock(Request.class), timeGroup))
        .isEqualTo(PostResult.PERMANENT_FAILURE);

    verify(patrawinDao, times(1)).doesActivityCodeExist(anyInt());
    verify(patrawinDao, never()).createWorklog(any(Worklog.class));
  }

  /*
  @Test
  void postTime_db_transaction_error() {
    final TimeGroup timeGroup = fakeEntities.randomTimeGroup();

    when(patrawinDao.findUsername(anyString()))
        .thenReturn(Optional.of(timeGroup.getUser().getExternalId()));

    final Tag tag = fakeEntities.randomTag("/Patrawin/");
    final Issue issue = randomDataGenerator.randomIssue(tag.getName());

    when(patrawinDao.findIssueByTagName(anyString())).thenReturn(Optional.of(issue));
    when(templateFormatter.format(any(TimeGroup.class))).thenReturn("Work log body");
    doThrow(new RuntimeException("Test exception")).when(patrawinDao).createWorklog(any(Worklog.class));

    final PostResult result = connector.postTime(fakeRequest(), fakeEntities.randomTimeGroup());

    assertThat(result)
        .isEqualTo(PostResult.TRANSIENT_FAILURE)
        .as("Database transaction error while posting time should result in transient failure");

    assertThat(result.getError().get())
        .isInstanceOf(RuntimeException.class)
        .as("Post result should contain the cause of the error");
  }

  @Test
  void postTime_with_valid_group_should_succeed() {
    final Tag tag1 = fakeEntities.randomTag("/Patrawin/");
    final Tag tag2 = fakeEntities.randomTag("/Patrawin/");
    final Tag tag3 = fakeEntities.randomTag("/Patrawin/");

    final TimeRow timeRow1 = fakeEntities.randomTimeRow().activityHour(2018110110);
    final TimeRow timeRow2 = fakeEntities.randomTimeRow().activityHour(2018110109);

    final User user = fakeEntities.randomUser().experienceWeightingPercent(50);

    final TimeGroup timeGroup = fakeEntities.randomTimeGroup()
        .tags(ImmutableList.of(tag1, tag2, tag3))
        .timeRows(ImmutableList.of(timeRow1, timeRow2))
        .user(user)
        .durationSplitStrategy(TimeGroup.DurationSplitStrategyEnum.DIVIDE_BETWEEN_TAGS)
        .totalDurationSecs(1500);

    when(patrawinDao.findUsername(anyString()))
        .thenReturn(Optional.of(timeGroup.getUser().getExternalId()));

    final Issue issue1 = randomDataGenerator.randomIssue(tag1.getName());
    final Issue issue2 = randomDataGenerator.randomIssue(tag1.getName());

    when(patrawinDao.findIssueByTagName(anyString()))
        .thenReturn(Optional.of(issue1))
        .thenReturn(Optional.of(issue2))
        // Last tag has no matching Patrawin issue
        .thenReturn(Optional.empty());

    when(templateFormatter.format(any(TimeGroup.class)))
        .thenReturn("Work log body");

    assertThat(connector.postTime(fakeRequest(), timeGroup))
        .as("Valid time group should be posted successfully")
        .isEqualTo(PostResult.SUCCESS);

    // Verify worklog creation
    ArgumentCaptor<Worklog> worklogCaptor = ArgumentCaptor.forClass(Worklog.class);
    verify(patrawinDao, times(2)).createWorklog(worklogCaptor.capture());
    List<Worklog> createdWorklogs = worklogCaptor.getAllValues();

    assertThat(createdWorklogs.get(0).getIssueId())
        .isEqualTo(issue1.getId())
        .as("The worklog should be assigned to the right issue");

    assertThat(createdWorklogs.get(1).getIssueId())
        .isEqualTo(issue2.getId())
        .as("The worklog should be assigned to the right issue");

    assertThat(createdWorklogs.get(0).getAuthor())
        .isEqualTo(timeGroup.getUser().getExternalId())
        .as("The author should be set to the posted time user's external ID");

    assertThat(createdWorklogs.get(0).getBody())
        .isNotEmpty();

    assertThat(createdWorklogs.get(0).getCreated())
        .isEqualTo(LocalDateTime.of(2018, 11, 1, 9, 0))
        .as("The worklog should be created with the earliest time row start time");

    assertThat(createdWorklogs.get(0).getTimeWorked())
        .isEqualTo(250)
        .as("The time worked should take into account the user's experience rating and" +
            "be split equally between the two tags");

    ArgumentCaptor<Long> idUpdateIssueCaptor = ArgumentCaptor.forClass(Long.class);
    ArgumentCaptor<Long> timeSpentUpdateIssueCaptor = ArgumentCaptor.forClass(Long.class);
    verify(patrawinDao, times(2))
        .updateIssueTimeSpent(idUpdateIssueCaptor.capture(), timeSpentUpdateIssueCaptor.capture());

    List<Long> updatedIssueIds = idUpdateIssueCaptor.getAllValues();
    assertThat(updatedIssueIds)
        .containsExactly(issue1.getId(), issue2.getId())
        .as("Time spent of both matching issues should be updated");

    List<Long> updatedIssueTimes = timeSpentUpdateIssueCaptor.getAllValues();
    assertThat(updatedIssueTimes)
        .containsExactly(issue1.getTimeSpent() + 250, issue2.getTimeSpent() + 250)
        .as("Time spent of both matching issues should be updated with new duration. The duration should be " +
            "split among the three tags even if one of them was not found.");
  }

  @Test
  void postTime_should_only_handle_configured_project_keys() {
    final Tag tagWt = fakeEntities.randomTag("/Patrawin/").name("WT-2");
    final Tag tagOther = fakeEntities.randomTag("/Patrawin/").name("OTHER-1");
    final TimeGroup timeGroup = fakeEntities
        .randomTimeGroup()
        .tags(ImmutableList.of(tagWt, tagOther));

    when(patrawinDao.findUsername(anyString()))
        .thenReturn(Optional.of(timeGroup.getUser().getExternalId()));

    connector.postTime(fakeRequest(), timeGroup);

    ArgumentCaptor<String> tagNameCaptor = ArgumentCaptor.forClass(String.class);
    verify(patrawinDao, times(1)).findIssueByTagName(tagNameCaptor.capture());

    assertThat(tagNameCaptor.getValue())
        .isEqualTo("WT-2")
        .as("Only configured project keys should be handled when posting time");
  }

  @Test
  void postTime_should_handle_tags_not_matching_project_keys_filter() {
    final Tag tagOther = fakeEntities.randomTag("/Patrawin/").name("OTHER-1");
    final TimeGroup timeGroup = fakeEntities
        .randomTimeGroup()
        .tags(ImmutableList.of(tagOther));

    when(patrawinDao.findUsername(anyString()))
        .thenReturn(Optional.of(timeGroup.getUser().getExternalId()));

    assertThat(connector.postTime(fakeRequest(), timeGroup))
        .as("There is nothing to post to Patrawin")
        .isEqualTo(PostResult.SUCCESS);

    verifyPatrawinNotUpdated();
  }*/
}
