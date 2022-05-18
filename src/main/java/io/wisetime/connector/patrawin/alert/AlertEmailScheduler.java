/*
 * Copyright (c) 2020 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector.patrawin.alert;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;
import io.wisetime.connector.config.RuntimeConfig;
import io.wisetime.connector.patrawin.ConnectorLauncher.PatrawinConnectorConfigKey;
import io.wisetime.connector.patrawin.persistence.PatrawinDao;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AlertEmailScheduler {

  @Inject
  private AlertEmailService alertEmailService;

  @Inject
  private PatrawinDao patrawinDao;

  /**
   * Atomic  Reference to the last time of the alert email
   */
  private final AtomicReference<LocalDateTime> lastAlertDateTime = new AtomicReference<>();

  // Only one thread is required for executor service
  private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

  /**
   * Initialise and run task
   */
  public void init() {

    if (alertEmailService.isEnabled()) {
      try {
        // check if all required scheduler configuration parameters are provided
        alertEmailService.checkServiceConfiguration();

        // Scheduling the task to be run with fixed delay equal to TASK_INTERVAL with initial delay equal 0
        executorService.scheduleWithFixedDelay(this::runScheduledTask,
            0,
            getTaskInterval(),
            TimeUnit.MINUTES);

      } catch (RuntimeException e) {
        log.error("The alert email service could not be started.", e);
        throw new RuntimeException("The alert email service could not be started.", e);
      }
    }
  }

  @VisibleForTesting
  boolean isShutdown() {
    return executorService.isShutdown();
  }

  @VisibleForTesting
  boolean isTerminated() {
    return executorService.isTerminated();
  }

  /**
   * Shutdown executor service
   */
  public void shutdown() {
    executorService.shutdown();
  }

  @VisibleForTesting
  void runScheduledTask() {
    try {
      Optional<LocalDateTime> localDateTime = patrawinDao.getEarliestUnprocessedTime(getHealthCheckInterval());
      localDateTime.ifPresent(dateTime -> {
        if (shouldScheduleAlertEmail()) {
          lastAlertDateTime.set(LocalDateTime.now());
          alertEmailService.sendPatrawinAlertMessage(dateTime);
        } else {
          log.info("There is unprocessed time in database. Alert email skipped as last sent at {} ",
              lastAlertDateTime.get());
        }
      });
    } catch (Exception e) {
      log.error("An exception was thrown during the check of unprocessed time. Alert email was not sent.", e);
    }
  }

  private boolean shouldScheduleAlertEmail() {
    return lastAlertDateTime.get() == null
        || lastAlertDateTime.get().plusMinutes(alertEmailService.getAlertEmailInterval()).isBefore(LocalDateTime.now());
  }

  /**
   * The check for unprocessed time is run with 2 minutes interval.
   *
   * @return long Task interval in minutes.
   */
  private long getTaskInterval() {
    return 2;
  }

  /**
   * <pre>
   * The health check interval defines time interval allowed for unprocessed posted time.
   * If unprocessed time stays longer then health check interval the situation is considered unhealthy.
   * </pre>
   */
  private int getHealthCheckInterval() {
    return RuntimeConfig
        .getInt(PatrawinConnectorConfigKey.HEALTH_CHECK_INTERVAL)
        .orElse(5);
  }
}
