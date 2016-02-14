#!/bin/bash
# We need to translate the environmental variables provided by the docker
# linking into usable variables.  The prefix "DB" (in $DB_ENV_DB_USER for
# example) is determined by the links argument in the gradle task
# createJavaAppContainer.  The rest of the environmental variable was defined
# by the base image we used for the postgresql database defined in the gradle
# task createPostgresDockerfile
export DATABASE_USER="$DB_ENV_DB_USER"
export DATABASE_PASSWORD="$DB_ENV_DB_PASS"
export JDBC_DATABASE_URL="jdbc:postgresql://$DB_PORT_5432_TCP_ADDR:$DB_PORT_5432_TCP_PORT/$DB_ENV_DB_NAME"

# Starting the dropwizard app
echo "PWD IS: $(pwd)"
echo "FILES ARE: $(ls)"
java -jar tododrop-0.1-all.jar server tododrop.yml
