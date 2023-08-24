#!/bin/bash

set -e

_profiles="$1"
if [ -z "$_profiles" ]; then
  _profiles="checkerframework errorprone eclipse"
fi

_ignored_profiles="-enforce-maven-version,-format-apply,-deploy-local,-javadoc-jar"

for profile in $_profiles; do
echo ""
echo "--------------------- Running $profile -----------------------"
echo ""
./mvnw $MAVEN_CLI_OPTS clean verify -pl api/jstachio,compiler/apt  -P${profile},show-profiles,${_ignored_profiles} -Dmaven.javadoc.skip -DskipTests -Dmaven.source.skip=true 
done

# Checker or the maven compiler leaves these files around
# I'm not sure why
find . -name "javac.*.args" | xargs rm -f
