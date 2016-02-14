#!/bin/bash

# We need to translate the environmental variables provided by the docker
# linking into usable variables.  The prefix DB_ENV used below is determined
# by the links argument in the gradle task createJavaAppContainer.  For example:
#
# links = ["original_container_name:alias"]
#
# Would give us environmental variable names like ALIAS_ENV_USER. The part of
# the environmental variable after ALIAS_ENV was defined by the base image we used
# for the postgresql database.  The base image is defined in the gradle task
# buildPostgresDockerfile.
export DATABASE_USER="$DB_ENV_DB_USER"
export DATABASE_PASSWORD="$DB_ENV_DB_PASS"
export JDBC_DATABASE_URL="jdbc:postgresql://$DB_PORT_5432_TCP_ADDR:$DB_PORT_5432_TCP_PORT/$DB_ENV_DB_NAME"

# Start the dropwizard app
java -jar tododrop-0.1-all.jar server tododrop.yml
