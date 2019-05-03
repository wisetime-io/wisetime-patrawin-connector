# WiseTime Patrawin Connector

## Status

The WiseTime Patrawin Connector is currently under development, and is not ready for production use. This notice will be removed once the connector is deemed production-ready.

## About

The WiseTime Patrawin Connector connects [WiseTime](https://wisetime.io) to [Patrawin](https://www.cpaglobal.com/ipone-patrawin), and will automatically:

* Create a new WiseTime tag whenever a new case is created in Patrawin
* Create a new WiseTime tag whenever a new client is created in Patrawin
* Record a new time registration in Patrawin whenever a user posts time to WiseTime

In order to use the WiseTime Patrawin Connector, you will need a [WiseTime Connect](https://wisetime.io/docs/connect/) API key. The WiseTime Patrawin Connector runs as a Docker container and is easy to set up and operate.

## Configuration

Configuration is done through environment variables. The following configuration options are required.

| Environment Variable               | Description                                                                |
| ---------------------------------- | -------------------------------------------------------------------------- |
| API_KEY                            | Your WiseTime Connect API Key                                              |
| PATRAWIN_JDBC_URL                  | The JDBC URL for your Patrawin database                                    |
| PATRAWIN_DB_USER                   | Username to use to connect to the Patrawin database                        |
| PATRAWIN_DB_PASSWORD               | Password to use to connect to the Patrawin database                        |
| TAG_MODIFIER_ACTIVITY_CODE_MAPPING | Activity code mapping with modifier name as key and activity code as value |
| DEFAULT_MODIFIER                   | The default modifier to use when it is not explicitly in posted time.      |

The following configuration options are optional.

| Environment Variable  | Description                                                                                                                                                                                                                                    |
| --------------------- | -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| CALLER_KEY            | The caller key that WiseTime should provide with post time webhook calls. The connector does not authenticate Webhook calls if not set.                                                                                                        |
| TAG_UPSERT_PATH       | The WiseTime tag folder path to use for Patrawin tags. Defaults to `/Patrawin/` (trailing slash is required). Use `/` for root folder.                                                                                                         |
| TAG_UPSERT_BATCH_SIZE | Number of tags to upsert at a time. A large batch size mitigates API call latency. Defaults to 500.                                                                                                                                            |
| DATA_DIR              | If set, the connector will use the directory as the location for storing data to keep track of the Patrawin cases and clients that it has synced. By default, WiseTime Connector will create a temporary dir under `/tmp` as its data storage. |
| CONNECTOR_MODE        | If unset, this defaults to `LONG_POLL`: use long polling to fetch posted time. Optional parameters are `WEBHOOK` to start up a server to listen for posted time. `TAG_ONLY` use the connector only to upsert new tags                          |
| WEBHOOK_PORT          | If set, the connector will listen to this port e.g. 8090. Defaults to 8080.                                                                                                                                                                    |
| LOG_LEVEL             | Define log level. Available values are: `TRACE`, `DEBUG`, `INFO`, `WARN`, `ERROR` and `OFF`. Default is `INFO`.                                                                                                                                |

The connector needs to be able to read from the `ARENDE_1`, `KUND_24`, `BEHORIG_50` and `FAKTURATEXTNR_15` tables, and call the post_time stored procedure in the Patrawin database.

## Running the WiseTime Patrawin Connector

The easiest way to run the Patrawin Connector is using Docker. For example:

```text
docker run -d \
    --restart=unless-stopped \
    -v volume_name:/usr/local/wisetime-connector/data \
    -e DATA_DIR=/usr/local/wisetime-connector/data \
    -e API_KEY=yourwisetimeapikey \
    -e PATRAWIN_JDBC_URL="jdbc:sqlserver://HOST:PORT;databaseName=DATABASE_NAME;ssl=request;useCursors=true" \
    -e PATRAWIN_DB_USER=dbuser \
    -e PATRAWIN_DB_PASSWORD=dbpass \
    -e TAG_MODIFIER_ACTIVITY_CODE_MAPPING=modifier_name_1:numeric_activity_code_1,modifier_name_2:numeric_activity_code_2 \
    -e DEFAULT_MODIFIER=modifier_name_1 \
    wisetime/patrawin-connector
```

If you are using `CONNECTOR_MODE=WEBHOOK`: Note that you need to define port forwarding in the docker run command (and similarly any docker-compose.yaml definition). If you set the webhook port other than default (8080) you must also add the WEBHOOK_PORT environment variable to match the docker ports definition.

The Patrawin connector runs self-checks to determine whether it is healthy. If health check fails, the connector will shutdown. This gives us a chance to automatically re-initialise the application through the Docker restart policy.

## Logging

#### Common

Patrawin Connector uses [logback](https://logback.qos.ch) as logging framework. Default log level is `INFO`, you can change it by setting `LOG_LEVEL` configuration.

To setup own appenders or add another customization you can add `logback-extra.xml` on classpath. For more information see [File inclusion](https://logback.qos.ch/manual/configuration.html#fileInclusion).

#### Logging to AWS CloudWatch

If configured, the Patrawin Connector can send application logs to [AWS CloudWatch](https://aws.amazon.com/cloudwatch/). In order to do so, you must supply the following configuration through the following environment variables.

| Environment Variable  | Description                                          |
| --------------------- | ---------------------------------------------------- |
| AWS_ACCESS_KEY_ID     | AWS access key for account with access to CloudWatch |
| AWS_SECRET_ACCESS_KEY | Secret for the AWS access key                        |
| AWS_REGION            | AWS region to log to                                 |

## Building

To build a Docker image of the WiseTime Patrawin Connector, run:

```text
make docker
```
