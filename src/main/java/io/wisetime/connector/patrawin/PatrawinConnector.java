/*
 * Copyright (c) 2019 Practice Insight Pty Ltd. All Rights Reserved.
 */

package io.wisetime.connector.patrawin;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.wisetime.connector.api_client.ApiClient;
import io.wisetime.connector.api_client.PostResult;
import io.wisetime.connector.datastore.ConnectorStore;
import io.wisetime.connector.integrate.ConnectorModule;
import io.wisetime.connector.integrate.WiseTimeConnector;
import io.wisetime.connector.template.TemplateFormatter;
import io.wisetime.generated.connect.TimeGroup;
import spark.Request;

/**
 * WiseTime Connector implementation for Patrawin
 *
 * @author shane.xie@practiceinsight.io
 */
public class PatrawinConnector implements WiseTimeConnector {

  private static final Logger log = LoggerFactory.getLogger(PatrawinConnector.class);

  private ApiClient apiClient;
  private ConnectorStore connectorStore;
  private TemplateFormatter templateFormatter;

  @Inject
  private PatrawinDao patrawinDao;

  @Override
  public void init(ConnectorModule connectorModule) {
    Preconditions.checkArgument(patrawinDao.hasExpectedSchema(),
        "Patrawin database schema is unsupported by this connector");

    this.apiClient = connectorModule.getApiClient();
    this.connectorStore = connectorModule.getConnectorStore();
    this.templateFormatter = connectorModule.getTemplateFormatter();
  }

  @Override
  public void performTagUpdate() {
    // TODO
  }

  @Override
  public PostResult postTime(Request request, TimeGroup userPostedTime) {
    // TODO
    return null;
  }

  @Override
  public boolean isConnectorHealthy() {
    return patrawinDao.canQueryDb();
  }
}
