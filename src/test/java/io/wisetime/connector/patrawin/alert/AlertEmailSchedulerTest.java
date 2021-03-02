/*
 * Copyright (c) 2019 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector.patrawin.alert;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import com.github.javafaker.Faker;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.wisetime.connector.config.RuntimeConfig;
import io.wisetime.connector.patrawin.ConnectorLauncher.PatrawinConnectorConfigKey;
import io.wisetime.connector.patrawin.persistence.PatrawinDao;
import io.wisetime.connector.patrawin.util.MsSqlTimeDbFormatter;
import io.wisetime.connector.patrawin.util.TimeDbFormatter;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import org.junit.jupiter.api.Test;

public class AlertEmailSchedulerTest {

  private static PatrawinDao patrawinDaoMock = mock(PatrawinDao.class);

  private static final Faker FAKER = new Faker();

  private static AlertEmailScheduler alertEmailScheduler;

  @Test
  public void scheduleUnprocessedCheck() throws InterruptedException {
    String recipientEmail = FAKER.internet().emailAddress();
    String senderEmail = FAKER.internet().emailAddress();
    final AtomicInteger sendCallCount = new AtomicInteger(0);
    AlertEmailService alertEmailService = new AlertEmailService() {
      @Override
      public void send(Message message) throws MessagingException {
        assertThat(message.getFrom()).isEqualTo(InternetAddress.parse(senderEmail));
        assertThat(message.getRecipients(RecipientType.TO)).isEqualTo(InternetAddress.parse(recipientEmail));
        sendCallCount.incrementAndGet();
      }
    };

    Injector injector = Guice.createInjector(binder -> {
      binder.bind(PatrawinDao.class).toProvider(() -> patrawinDaoMock);
      binder.bind(TimeDbFormatter.class).toInstance(new MsSqlTimeDbFormatter());
      binder.bind(AlertEmailService.class).toProvider(() -> alertEmailService);
    });

    alertEmailScheduler = injector.getInstance(AlertEmailScheduler.class);

    RuntimeConfig.setProperty(PatrawinConnectorConfigKey.ALERT_EMAIL_ENABLED, "true");
    RuntimeConfig.setProperty(PatrawinConnectorConfigKey.ALERT_EMAIL_INTERVAL_HH_MM, "00:60");
    RuntimeConfig.setProperty(PatrawinConnectorConfigKey.ALERT_RECIPIENT_EMAIL_ADDRESSES, recipientEmail);
    RuntimeConfig.setProperty(PatrawinConnectorConfigKey.ALERT_SENDER_EMAIL_ADDRESS, senderEmail);
    RuntimeConfig.setProperty(PatrawinConnectorConfigKey.ALERT_MAIL_SMTP_HOST, FAKER.internet().domainName());
    RuntimeConfig.setProperty(PatrawinConnectorConfigKey.ALERT_MAIL_SMTP_PORT, "404");
    RuntimeConfig.setProperty(PatrawinConnectorConfigKey.ALERT_EMAIL_SENDER_USERNAME, FAKER.internet().emailAddress());
    RuntimeConfig.setProperty(PatrawinConnectorConfigKey.ALERT_EMAIL_SENDER_PASSWORD, FAKER.internet().password());
    RuntimeConfig.setProperty(PatrawinConnectorConfigKey.ALERT_MAIL_SMTP_SOCKET_FACTORY_PORT, "405");
    // ALERT_STARTTLS_ENABLE defaults to false

    alertEmailService.checkServiceConfiguration();

    doReturn(true).when(patrawinDaoMock).hasExpectedSchema();
    doReturn(true).when(patrawinDaoMock).canQueryDb();

    final AtomicBoolean getEarliestCalled = new AtomicBoolean(false);

    doAnswer(invocation -> !getEarliestCalled.get()).when(patrawinDaoMock).hasUnprocessedTime(anyInt());
    doAnswer(invocation -> {
          getEarliestCalled.set(true);
          return Optional.of(LocalDateTime.now().minusMinutes(70));
        }
    ).when(patrawinDaoMock).getEarliestUnprocessedTime(anyInt());

    alertEmailScheduler.runScheduledTask();
    alertEmailScheduler.runScheduledTask();
    alertEmailScheduler.runScheduledTask();

    assertThat(getEarliestCalled.get())
        .as("we expect test establishes send is called")
        .isTrue();

    assertThat(sendCallCount.get())
        .as("we expect test establishes send is called once")
        .isEqualTo(1);

  }

}
