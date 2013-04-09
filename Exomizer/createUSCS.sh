#!/bin/bash
#
# This simple script downloads the necessary files from hg19
# and invokes the annotator to create the uscs.ser file
# which is required in various places.
#
# (c) 2013 Sebastian Bauer <mail@sebastianbauer.info>
#
# Released as public domain
#


# Download
wget -N http://hgdownload.soe.ucsc.edu/goldenPath/hg19/database/knownGene.txt.gz
wget -N http://hgdownload.soe.ucsc.edu/goldenPath/hg19/database/kgXref.txt.gz
wget -N http://hgdownload.soe.ucsc.edu/goldenPath/hg19/database/knownGeneMrna.txt.gz
wget -N http://hgdownload.soe.ucsc.edu/goldenPath/hg19/database/knownToLocusLink.txt.gz

# Uncompress (we keep the sources to avoid re-downloading the files again)
zcat knownGene.txt.gz >knownGene.txt
zcat kgXref.txt.gz >kgXref.txt
zcat knownGeneMrna.txt.gz >knownGeneMrna.txt
zcat knownToLocusLink.txt.gz >knownToLocusLink.txt

ant annotator

java -Xmx2G -jar Annotator.jar -U knownGene.txt -M knownGeneMrna.txt -X kgXref.txt -L knownToLocusLink.txt -S ucsc.ser
