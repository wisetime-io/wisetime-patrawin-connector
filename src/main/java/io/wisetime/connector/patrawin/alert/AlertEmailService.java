/*
 * Copyright (c) 2020 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector.patrawin.alert;

import com.google.common.base.Splitter;
import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import io.wisetime.connector.config.RuntimeConfig;
import io.wisetime.connector.patrawin.ConnectorLauncher.PatrawinConnectorConfigKey;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Builder;

public class AlertEmailService {

  @Builder
  public static class EmailMessage {

    @NotNull
    String username;
    @NotNull
    String password;
    @NotNull
    String subject;
    @NotNull
    String from;
    @NotEmpty
    InternetAddress[] recipients;
    @NotNull
    String text;
    @NotEmpty
    Properties props;
    boolean debug;
  }

  public void sendPatrawinAlertMessage(LocalDateTime datetime) {
    sendPlainHtmlMessage(createEmailMessage(createTemplateMessage(datetime)));
  }

  public void sendPlainHtmlMessage(EmailMessage emailMessage) {
    try {
      Session session = Session.getInstance(emailMessage.props,
          new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
              return new PasswordAuthentication(emailMessage.username, emailMessage.password);
            }
          });
      session.setDebug(emailMessage.debug);

      Message message = new MimeMessage(session);
      message.setFrom(new InternetAddress(emailMessage.from));
      message.setRecipients(Message.RecipientType.TO, emailMessage.recipients);
      message.setSubject(emailMessage.subject);
      message.setContent(emailMessage.text, "text/html");
      send(message);
    } catch (MessagingException e) {
      throw new RuntimeException("Cannot send alert email", e);
    }
  }

  public void send(Message message) throws MessagingException {
    Transport.send(message);
  }

  public String createTemplateMessage(LocalDateTime date) {
    try {
      Configuration configuration = new Configuration(Configuration.VERSION_2_3_28);
      configuration.setDefaultEncoding("UTF-8");
      configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
      configuration.setLogTemplateExceptions(false);
      configuration.setWrapUncheckedExceptions(true);
      configuration.setTemplateLoader(new ClassTemplateLoader(this.getClass().getClassLoader(), ""));

      StringWriter stringWriter = new StringWriter();
      configuration.clearTemplateCache();
      Template template = configuration.getTemplate("connection-alert.ftl");
      Map<String, Object> map = new HashMap<>();
      map.put("datetime", getFormattedDate(date));
      template.process(map, stringWriter);
      return stringWriter.toString().trim();
    } catch (Throwable e) {
      throw new RuntimeException("Failed to create an email body", e);
    }
  }

  /**
   * Format LocalDateTime to a string with a template 'yyyy/MM/dd HH:mm:ss Z' The zone is taken from the config
   * of the connector.
   *
   * @param date The {@link LocalDateTime}
   * @return String
   */
  private String getFormattedDate(LocalDateTime date) {
    return new StringBuilder()
        .append(date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")))
        .append(" ")
        .append(getZone()).toString();
  }

  public EmailMessage createEmailMessage(String text) {
    String senderAddress = getAlertSenderEmailAddresses();
    String recipientAddress = getAlertRecipientEmailAddresses();
    String password = getAlertEmailSenderPassword();
    String username = getAlertSenderUsername();
    try {
      return EmailMessage.builder()
          .from(senderAddress)
          .recipients(InternetAddress.parse(recipientAddress))
          .subject(getSubject())
          .text(text)
          .password(password)
          .username(username)
          .props(getProperties())
          .debug(true)
          .build();
    } catch (AddressException e) {
      throw new RuntimeException("Failed to build alert email message", e);
    }
  }

  public String getZone() {
    return RuntimeConfig.getString(PatrawinConnectorConfigKey.TIMEZONE).orElse("UTC");
  }

  public boolean isEnabled() {
    return RuntimeConfig.getBoolean(PatrawinConnectorConfigKey.ALERT_EMAIL_ENABLED).orElse(false);
  }

  void checkServiceConfiguration() {
    getAlertEmailSenderPassword();
    getAlertRecipientEmailAddresses();
    getAlertSenderEmailAddresses();
    getAlertSenderUsername();
    getAlertSmtpPort();
    getAlertSmtpHost();
  }

  /**
   * Alert email interval in minutes. The interval will be defaulted to 300 (5 hours) if not provided
   */
  public long getAlertEmailInterval() {
    String hhmm = RuntimeConfig.getString(PatrawinConnectorConfigKey.ALERT_EMAIL_INTERVAL_HH_MM).orElse("05:00");
    List<String> time = Splitter.on(":").omitEmptyStrings().trimResults().splitToList(hhmm);
    if (time.size() == 2) {
      return Long.parseLong(time.get(0)) * 60 + Long.parseLong(time.get(1));
    }
    return defaultEmailTimeout();
  }

  /**
   * Default email timeout is set to 300 minutes (5 hours)
   *
   * @return long
   */
  private long defaultEmailTimeout() {
    return 300;
  }

  String getSubject() {
    return "Your Wisetime-to-Patrawin connection is unhealthy";
  }

  /**
   * Setting smtp properties.
   * <p>
   * Assuming that smtp is always authenticated, ie mail.smtp.auth=true
   * <p>
   * The client supports connection via TLS or SSL.
   * <p>Minor changes
   * TLS requires mail.smtp.starttls.enable=true. If mail.smtp.starttls.enable is not set or false then we assume that
   * access is via SSL.
   * <p>
   * SSL requires mail.smtp.socketFactory.port and mail.smtp.socketFactory.class. The mail.smtp.socketFactory.port and
   * mail.smtp.port settings are usually equal but can differ. If mail.smtp.socketFactory.port is not set it will be
   * defaulted to mail.smtp.port value. The default for mail.smtp.socketFactory.class is javax.net.ssl.SSLSocketFactory
   *
   * @return Properties
   */
  private Properties getProperties() {
    Properties prop = new Properties();
    prop.put("mail.smtp.host", getAlertSmtpHost());
    prop.put("mail.smtp.port", getAlertSmtpPort());
    prop.put("mail.smtp.auth", "true");

    Optional<String> starttls = RuntimeConfig.getString(PatrawinConnectorConfigKey.ALERT_STARTTLS_ENABLE);

    starttls.ifPresent(value -> prop.put("mail.smtp.starttls.enable", starttls.get()));
    // If not TLS then setting up SSL
    if (!starttls.isPresent() || starttls.get().equalsIgnoreCase("false")) {
      prop.put("mail.smtp.socketFactory.port",
          RuntimeConfig.getString(PatrawinConnectorConfigKey.ALERT_MAIL_SMTP_SOCKET_FACTORY_PORT)
              .orElse(getAlertSmtpPort()));
      prop.put("mail.smtp.socketFactory.class",
          RuntimeConfig.getString(PatrawinConnectorConfigKey.ALERT_MAIL_SMTP_SOCKET_FACTORY_CLASS)
              .orElse("javax.net.ssl.SSLSocketFactory"));
    }
    return prop;
  }

  private String getAlertEmailSenderPassword() {
    return getRuntimeConfigKey(PatrawinConnectorConfigKey.ALERT_EMAIL_SENDER_PASSWORD,
        "Missing required ALERT_EMAIL_SENDER_PASSWORD configuration");
  }

  private String getAlertRecipientEmailAddresses() {
    return getRuntimeConfigKey(PatrawinConnectorConfigKey.ALERT_RECIPIENT_EMAIL_ADDRESSES,
        "Missing required ALERT_RECIPIENT_EMAIL_ADDRESSES configuration");
  }

  private String getAlertSenderEmailAddresses() {
    return getRuntimeConfigKey(PatrawinConnectorConfigKey.ALERT_SENDER_EMAIL_ADDRESS,
        "Missing required ALERT_SENDER_EMAIL_ADDRESS configuration");
  }

  private String getAlertSenderUsername() {
    return getRuntimeConfigKey(PatrawinConnectorConfigKey.ALERT_EMAIL_SENDER_USERNAME,
        "Missing required ALERT_EMAIL_SENDER_USERNAME configuration");
  }

  private String getAlertSmtpPort() {
    return getRuntimeConfigKey(PatrawinConnectorConfigKey.ALERT_MAIL_SMTP_PORT,
        "Missing required ALERT_MAIL_SMTP_PORT configuration");
  }

  private String getAlertSmtpHost() {
    return getRuntimeConfigKey(PatrawinConnectorConfigKey.ALERT_MAIL_SMTP_HOST,
        "Missing required ALERT_MAIL_SMTP_HOST configuration");
  }

  private String getRuntimeConfigKey(PatrawinConnectorConfigKey configKey, String errorMessage) {
    return RuntimeConfig.getString(configKey)
        .orElseThrow(() -> new RuntimeException(errorMessage));
  }
}
