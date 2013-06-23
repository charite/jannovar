#!/bin/sh

## This script will download the UCSC data and use Jannovar to create ucsc.ser

URL=http://hgdownload.soe.ucsc.edu/goldenPath/hg19/database
EXTERNAL_DATA=src/main/resources

KG=knownGene.txt
MRNA=knownGeneMrna.txt
XR=kgXref.txt
LOCUS=knownToLocusLink.txt


if [ ! -e ${EXTERNAL_DATA}/${KG} ]; then
    cd $(EXTERNAL_DATA) ; wget --retry-connrefused -N -i ${URL}/${KG}.gz ; gunzip ${KG}.gz
fi

if [ ! -e ${EXTERNAL_DATA}/${MRNA} ]; then
    cd $(EXTERNAL_DATA) ; wget --retry-connrefused -N -i ${URL}/${MRNA}.gz ; gunzip ${MRNA}.gz
fi

if [ ! -e ${EXTERNAL_DATA}/${XR} ]; then
    cd $(EXTERNAL_DATA) ; wget --retry-connrefused -N -i ${URL}/${XR}.gz ; gunzip ${XR}.gz
fi

if [ ! -e ${EXTERNAL_DATA}/${LOCUS} ]; then
    cd $(EXTERNAL_DATA) ; wget --retry-connrefused -N -i ${URL}/${LOCUS}.gz ; gunzip ${LOCUS}.gz
fi



## This is the default name of the output file
SERIAL="ucsc.ser"

##set -x  uncomment for VERBOSE

JAVA="/usr/bin/java"
MV="/bin/mv"

JANNOVAR="target/jannovar-0.0.7-SNAPSHOT.jar"


if [ ! -e ucsc.ser ]; then
    ${JAVA}  -Xms1G -Xmx2G -jar ${JANNOVAR} -U ${EXTERNAL_DATA}/${KG} \
	-M ${EXTERNAL_DATA}/${MRNA} -X ${EXTERNAL_DATA}/${XR} -L ${EXTERNAL_DATA}/${LOCUS} -S ucsc.ser
    else
    echo "ucsc.ser already exists. Delete it and rerun script to create new file"
fi



