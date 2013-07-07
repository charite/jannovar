#!/bin/sh

## This script will download the UCSC data and use Jannovar to create ucsc.ser

##set -x  uncomment for VERBOSE

JAVA="/usr/bin/java"

JANNOVAR="target/jannovar-0.0.9-SNAPSHOT.jar"

${JAVA} -jar ${JANNOVAR} -download-ucsc




