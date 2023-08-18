#!/bin/bash

_profiles="$1"
if [ -z "$_profiles" ]; then
  _profiles="checkerframework errorprone eclipse"
fi
for profile in $_profiles; do
./mvnw clean verify -pl api/jstachio,compiler/apt  -P${profile} -Dmaven.javadoc.skip -DskipTests -Dmaven.source.skip=true 
done

# Checker or the maven compiler leaves these files around
# I'm not sure why
find . -name "javac.*.args" | xargs rm
