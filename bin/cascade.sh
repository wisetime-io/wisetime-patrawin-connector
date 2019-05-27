#!/usr/bin/env bash
set -o errexit ; set -o errtrace ; set -o pipefail

curl --silent --show-error --fail -u ${BAMBOO_USER}:${BAMBOO_PASS} \
  -X POST -d "ARTIFACT1&ExecuteAllStages" \
  https://bamboo.dev.wisetime.com/rest/api/latest/queue/CAPI-WCPWA > result.xml

echo "Result: "
cat result.xml

# build for raspberry pi
curl --silent --show-error --fail -u ${BAMBOO_USER}:${BAMBOO_PASS} \
  -X POST -d "ARTIFACT1&ExecuteAllStages" \
  https://bamboo.dev.wisetime.com/rest/api/latest/queue/CAPI-WPAA > result-arm64.xml

echo "Result (arm64): "
cat result-arm64.xml