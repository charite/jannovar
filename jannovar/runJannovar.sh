#!/bin/sh


## Test run of Jannovar
## TO create the serialized UCSC file, you need to download data files from the UCSC
## server, http://hgdownload.soe.ucsc.edu/goldenPath/hg19/database/
## There are four files to be downloaded (KnownGene.txt, knownGeneMrna.txt, kgXref.txt
## and knownToLocusLink.txt). Put them all in some directory (probably you then need 
## to change the following path):
DIR="/home/peter/data/ucsc"
#DIR="/Users/hecht/Desktop/data/ucsc"


## The following four variables now give the complete path of these four files
KG="${DIR}/knownGene.txt"
MRNA="${DIR}/knownGeneMrna.txt" 
XR="${DIR}/kgXref.txt"
LOCUS="${DIR}/knownToLocusLink.txt"

## This is the default name of the output file
SERIAL="ucsc.ser"

##set -x  uncomment for VERBOSE

JAVA="/usr/bin/java"
MV="/bin/mv"

JANNOVAR="target/jannovar-0.0.6-SNAPSHOT.jar"


echo ${JAVA}  -Xms1G -Xmx2G -jar ${JANNOVAR} -U $KG -M $MRNA -X $XR -L $LOCUS -S $SERIAL
${JAVA}  -Xms1G -Xmx2G -jar ${JANNOVAR} -U $KG -M $MRNA -X $XR -L $LOCUS -S $SERIAL
