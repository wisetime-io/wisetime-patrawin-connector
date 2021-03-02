/*
 * Copyright (c) 2019 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector.patrawin.alert;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.javafaker.Faker;
import io.wisetime.connector.config.RuntimeConfig;
import io.wisetime.connector.patrawin.ConnectorLauncher.PatrawinConnectorConfigKey;
import java.time.LocalDateTime;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import org.junit.jupiter.api.Test;

public class AlertEmailServiceTest {

  private static final Faker FAKER = new Faker();

  private int times = 0;

  @Test
  public void createTemplateMessage() {
    AlertEmailService alertEmailService = new AlertEmailService();
    assertThat(alertEmailService.createTemplateMessage(LocalDateTime.now()))
        .startsWith("<html>\n<h1>Dear admin,\n");
  }

  @Test
  public void send() throws MessagingException {
    Properties prop = new Properties();
    prop.put("mail.smtp.host", FAKER.internet().domainName());
    prop.put("mail.smtp.port", "587");
    prop.put("mail.smtp.auth", "true");
    prop.put("mail.smtp.starttls.enable", "true");
    String subject = FAKER.lorem().sentence();
    String sender = FAKER.internet().emailAddress();
    String recipient = FAKER.internet().emailAddress();
    times = 0;

    AlertEmailService alertEmailService = new AlertEmailService() {
      public void send(Message message) throws MessagingException {
        //count how many times send method was called
        times++;
        assertThat(message.getSubject()).isEqualTo(subject);
        assertThat(message.getFrom()).isEqualTo(InternetAddress.parse(sender));
        assertThat(message.getRecipients(RecipientType.TO)).isEqualTo(InternetAddress.parse(recipient));
      }
    };

    alertEmailService.sendPlainHtmlMessage(AlertEmailService.EmailMessage.builder()
        .from(sender)
        .recipients(InternetAddress.parse(recipient))
        .subject(subject)
        .text(FAKER.lorem().paragraph())
        .password(FAKER.internet().password())
        .username(FAKER.internet().emailAddress())
        .props(prop)
        .debug(true)
        .build()
    );
    assertThat(times).isEqualTo(1);
  }

  @Test
  public void createEmailMessage() throws AddressException {

    String recipientEmail = FAKER.internet().emailAddress();
    String senderEmail = FAKER.internet().emailAddress();
    String smtpHost = FAKER.internet().domainName();
    String smtpPort = "404";
    String socketFactoryPort = "405";
    String startTlsEnabled = "false";
    String senderUsername = FAKER.internet().emailAddress();
    String password = FAKER.internet().password();
    String interval = "00:01";

    RuntimeConfig.setProperty(PatrawinConnectorConfigKey.ALERT_EMAIL_INTERVAL_HH_MM, interval);
    RuntimeConfig.setProperty(PatrawinConnectorConfigKey.ALERT_RECIPIENT_EMAIL_ADDRESSES, recipientEmail);
    RuntimeConfig.setProperty(PatrawinConnectorConfigKey.ALERT_SENDER_EMAIL_ADDRESS, senderEmail);
    RuntimeConfig.setProperty(PatrawinConnectorConfigKey.ALERT_MAIL_SMTP_HOST, smtpHost);
    RuntimeConfig.setProperty(PatrawinConnectorConfigKey.ALERT_MAIL_SMTP_PORT, smtpPort);
    RuntimeConfig.setProperty(PatrawinConnectorConfigKey.ALERT_EMAIL_SENDER_USERNAME, senderUsername);
    RuntimeConfig.setProperty(PatrawinConnectorConfigKey.ALERT_EMAIL_SENDER_PASSWORD, password);
    RuntimeConfig.setProperty(PatrawinConnectorConfigKey.ALERT_MAIL_SMTP_SOCKET_FACTORY_PORT, socketFactoryPort);
    RuntimeConfig.setProperty(PatrawinConnectorConfigKey.ALERT_STARTTLS_ENABLE, startTlsEnabled);

    AlertEmailService alertEmailService = new AlertEmailService();
    AlertEmailService.EmailMessage emailMessage = alertEmailService.createEmailMessage("text");

    assertThat(alertEmailService.getAlertEmailInterval()).isEqualTo(1L);
    assertThat(emailMessage.password).isEqualTo(password);
    assertThat(emailMessage.from).isEqualTo(senderEmail);
    assertThat(emailMessage.recipients).isEqualTo(InternetAddress.parse(recipientEmail));
    assertThat(emailMessage.username).isEqualTo(senderUsername);
    assertThat(emailMessage.subject).isEqualTo(alertEmailService.getSubject());
    assertThat(emailMessage.props.get("mail.smtp.host")).isEqualTo(smtpHost);
    assertThat(emailMessage.props.get("mail.smtp.port")).isEqualTo(smtpPort);
    assertThat(emailMessage.props.get("mail.smtp.auth")).isEqualTo("true");
    assertThat(emailMessage.props.get("mail.smtp.starttls.enable")).isEqualTo(startTlsEnabled);
    assertThat(emailMessage.props.get("mail.smtp.socketFactory.class")).isEqualTo("javax.net.ssl.SSLSocketFactory");
    assertThat(emailMessage.props.get("mail.smtp.socketFactory.port")).isEqualTo(socketFactoryPort);

  }
}
