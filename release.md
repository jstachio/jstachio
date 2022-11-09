This project does not use the maven release plugin for a variety of reasons:

  * Creates an environment that can be very different from the normal snapshot deploy process by forking
  * Needlessly edits the pom file twice and checkins it in which **requires every pom file in the project to be changed**.
  * Because it constantly changes the SNAPSHOT pom on each release downstream projects have to remember to update their SNAPSHOT version even for patch releases.

Unlike the maven release plugin we do not constantly change the pom for each patch version.
The release version is stored in a properties file called `version.properties`.
The pom only should change (commited change) if the minor or major version changes. 

For example say the pom is `0.7.0-SNAPSHOT` and our last release is `0.6.0`.

If we now want to make a patch version of `0.6.1` the pom file will not need to be updated (except by the release script for deployment).
However if we are actually wanting to finally release `0.7.0` then the pom must be updated to something like `0.8.0-SNAPSHOT`.

The `vh` script will mostly make sure you do not violate this.

Here is the process for **release**:

1. Edit `version.properties` to the desired release version. 
1. If this is not a patch release you will need to update the pom to a later snapshot
   
   1. `bin/vh set pom VERSION-SNAPSHOT` # where VERSION is the new minor/major version

1. Checkin the file `version.properties` (and pom file if minor or major version change). It will serve as the commit for tagging reproducible builds.
1. run `bin/vh release` which will tag and temporarily update the pom for release
1. Run the commands it tells you to run


If you just want to deploy a snapshot to centrals snapshot repositories run:

`mvn clean deploy -Pcentral`
