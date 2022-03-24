#!/usr/bin/env bash

set -e

BUILD_DIR="${0%/*}"

function _version {
    local version=$(mvn -f $BUILD_DIR/../ help:evaluate -Dexpression=project.version -q -DforceStdout) 
    echo "$version"
}

function _release {
    local snapshotVersion="$1"
    local buildNumber="$2"
    if ((buildNumber == 0)); then
        echo "Bad buildNumber"
        exit 1;
    fi;
    local finalVersion=${snapshotVersion/"-SNAPSHOT"/}
    local parts=( ${finalVersion//./ } )
    local minor=${parts[1]}
    local revision=${parts[2]}
    if ((minor == 0)); then
        echo "Bad minor version. Minor version cannot be zero. version: $snapshotVersion"
        exit 1;
    fi
    ((parts[1]--))
    ((parts[2]=buildNumber))
    IFS="."
    echo "${parts[*]}"
}

case $1 in
    current) _version;;
    release) shift && _release $@;;
    *) echo "Failure with $1" && exit 1;;
esac


