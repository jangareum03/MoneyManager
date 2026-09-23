#!/bin/bash

set -a
source "$(dirname "$0")/.env"
set +a

export DB_HOST="$WINDOWS_HOST"

./gradlew bootRun
