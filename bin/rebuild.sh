#!/bin/bash
bin/vh set pom && mvn clean package -Ddeploy=release -DskipTests -Dmaven.javadoc.skip -Dgpg.skip $@
