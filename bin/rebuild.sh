#!/bin/bash
# We assume the tag version has ben checkedout already
bin/vh set pom && mvn clean package -Ddeploy=release -Duser.timezone=UTC -DskipTests -Dmaven.javadoc.skip -Dgpg.skip $@
