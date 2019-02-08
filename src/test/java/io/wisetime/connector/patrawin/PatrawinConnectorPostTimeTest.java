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

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.wisetime.connector.api_client.ApiClient;
import io.wisetime.connector.api_client.PostResult;
import io.wisetime.connector.config.ConnectorConfigKey;
import io.wisetime.connector.config.RuntimeConfig;
import io.wisetime.connector.datastore.ConnectorStore;
import io.wisetime.connector.integrate.ConnectorModule;
import io.wisetime.connector.patrawin.fake.FakeEntities;
import io.wisetime.connector.patrawin.model.Worklog;
import io.wisetime.connector.patrawin.persistence.PatrawinDao;
import io.wisetime.connector.patrawin.util.MsSqlTimeDbFormatter;
import io.wisetime.connector.patrawin.util.TimeDbFormatter;
import io.wisetime.generated.connect.Tag;
import io.wisetime.generated.connect.TimeGroup;
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
public class PatrawinConnectorPostTimeTest {

  private static PatrawinDao patrawinDao = mock(PatrawinDao.class);
  private static ApiClient apiClient = mock(ApiClient.class);
  // private static TemplateFormatter narrativeFormatter = mock(TemplateFormatter.class);

  private static PatrawinConnector connector;
  private static FakeEntities fakeEntities = new FakeEntities();

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

    // reset(narrativeFormatter);
    reset(patrawinDao);

    when(patrawinDao.doesUserExist(anyString()))
        .thenReturn(true);
    when(patrawinDao.doesActivityCodeExist(anyInt()))
        .thenReturn(true);
    when(patrawinDao.doesCaseExist(anyString()))
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

  @Test
  void postTime_db_transaction_error() {
    final TimeGroup timeGroup = fakeEntities.randomTimeGroup();

    RuntimeException createWorklogException = new RuntimeException("Test exception");
    doThrow(createWorklogException)
        .when(patrawinDao)
        .createWorklog(any(Worklog.class));

    when(patrawinDao.doesCaseExist(anyString()))
        .thenReturn(true);

    final PostResult result = connector.postTime(mock(Request.class), timeGroup);

    assertThat(result)
        .isEqualTo(PostResult.TRANSIENT_FAILURE);
    assertThat(result.getError().isPresent())
        .isTrue();
    assertThat(result.getError().get())
        .isEqualTo(createWorklogException);
  }

  @Test
  void postTime_with_valid_group_should_succeed() {
    final TimeGroup timeGroup = fakeEntities.randomTimeGroup();

    assertThat(connector.postTime(mock(Request.class), timeGroup))
        .isEqualTo(PostResult.SUCCESS);
  }

  /**
   * Should first check for case and then for client.
   */
  @Test
  void postTime_create_worklog_for_each_valid_tag() {
    final Tag existentCaseTag = fakeEntities.randomTag();
    final Tag nonexistentCaseOrClientTag = fakeEntities.randomTag();
    final Tag existentClientTag = fakeEntities.randomTag();

    final TimeGroup timeGroup = fakeEntities.randomTimeGroup().tags(
        Arrays.asList(existentCaseTag, nonexistentCaseOrClientTag, existentClientTag));

    when(patrawinDao.doesCaseExist(existentCaseTag.getName()))
        .thenReturn(true);
    when(patrawinDao.doesCaseExist(nonexistentCaseOrClientTag.getName()))
        .thenReturn(false);
    when(patrawinDao.doesCaseExist(existentClientTag.getName()))
        .thenReturn(false);

    when(patrawinDao.doesClientExist(nonexistentCaseOrClientTag.getName()))
        .thenReturn(false);
    when(patrawinDao.doesClientExist(existentClientTag.getName()))
        .thenReturn(true);

    assertThat(connector.postTime(mock(Request.class), timeGroup))
        .isEqualTo(PostResult.SUCCESS);

    verify(patrawinDao, times(3)).doesCaseExist(anyString());
    verify(patrawinDao, times(2)).doesClientExist(anyString());

    ArgumentCaptor<Worklog> worklogCaptor = ArgumentCaptor.forClass(Worklog.class);
    verify(patrawinDao, times(2)).createWorklog(worklogCaptor.capture());

    List<Worklog> createdWorklogs = worklogCaptor.getAllValues();

    assertThat(createdWorklogs)
        .hasSize(2);
    assertThat(createdWorklogs.get(0).getCaseOrClientId())
        .isEqualTo(existentCaseTag.getName());
    assertThat(createdWorklogs.get(1).getCaseOrClientId())
        .isEqualTo(existentClientTag.getName());
  }

  @Test
  void postTime_worklog_has_valid_author_id() {
    final String userExternalId = "ExternalId";
    final TimeGroup timeGroup = fakeEntities.randomTimeGroup()
        .user(fakeEntities.randomUser().externalId(userExternalId))
        .tags(ImmutableList.of(fakeEntities.randomTag()));

    assertThat(connector.postTime(mock(Request.class), timeGroup))
        .isEqualTo(PostResult.SUCCESS);

    ArgumentCaptor<Worklog> worklogCaptor = ArgumentCaptor.forClass(Worklog.class);
    verify(patrawinDao, times(1)).createWorklog(worklogCaptor.capture());

    assertThat(worklogCaptor.getValue().getUsernameOrEmail())
        .isEqualTo(userExternalId);
  }

  @Test
  void postTime_worklog_narrative_contains_valid_data() {
    /*final TimeGroup timeGroup = fakeEntities.randomTimeGroup();

    assertThat(connector.postTime(mock(Request.class), timeGroup))
        .isEqualTo(PostResult.SUCCESS);*/
  }

  @Test
  void postTime_worklog_has_valid_start_time() {
    final TimeGroup timeGroup = fakeEntities.randomTimeGroup()
        .timeRows(ImmutableList.of(
            fakeEntities.randomTimeRow().modifier("1").activityHour(2018110115),
            fakeEntities.randomTimeRow().modifier("1").activityHour(2018110114)))
        .tags(Collections.singletonList(fakeEntities.randomTag()));

    assertThat(connector.postTime(mock(Request.class), timeGroup))
        .isEqualTo(PostResult.SUCCESS);

    ArgumentCaptor<Worklog> worklogCaptor = ArgumentCaptor.forClass(Worklog.class);
    verify(patrawinDao, times(1)).createWorklog(worklogCaptor.capture());

    final Instant expectedActivityStartTime = Instant.ofEpochSecond(1541080800);
    assertThat(worklogCaptor.getValue().getStartTime())
        .isEqualTo(expectedActivityStartTime);
  }

  /**
   * Total duration could be modified by the user.
   */
  @Test
  void postTime_worklog_has_valid_duration() {
    final TimeGroup timeGroup = fakeEntities.randomTimeGroup()
        .tags(Collections.singletonList(fakeEntities.randomTag()));

    assertThat(connector.postTime(mock(Request.class), timeGroup))
        .isEqualTo(PostResult.SUCCESS);

    ArgumentCaptor<Worklog> worklogCaptor = ArgumentCaptor.forClass(Worklog.class);
    verify(patrawinDao, times(1)).createWorklog(worklogCaptor.capture());

    assertThat(worklogCaptor.getValue().getDurationSeconds())
        .isEqualTo(timeGroup.getTotalDurationSecs());
  }

  /**
   * Total duration could be modified by the user.
   */
  @Test
  void postTime_worklog_has_valid_chargeable_time() {
    final TimeGroup timeGroup = fakeEntities.randomTimeGroup()
        .totalDurationSecs(1000)
        .user(fakeEntities.randomUser().experienceWeightingPercent(40))
        .tags(Collections.singletonList(fakeEntities.randomTag())); // getPerTagDuration ?? strategy

    assertThat(connector.postTime(mock(Request.class), timeGroup))
        .isEqualTo(PostResult.SUCCESS);

    ArgumentCaptor<Worklog> worklogCaptor = ArgumentCaptor.forClass(Worklog.class);
    verify(patrawinDao, times(1)).createWorklog(worklogCaptor.capture());

    final int expectedChargeableTimeSeconds = 400;
    assertThat(worklogCaptor.getValue().getChargeableTimeSeconds())
        .isEqualTo(expectedChargeableTimeSeconds);
  }
}
