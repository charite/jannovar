## Test run of Jannovar
## TO create the serialized UCSC file, you need to download data files from the UCSC
## server, http://hgdownload.soe.ucsc.edu/goldenPath/hg19/database/
## There are four files to be downloaded (KnownGene.txt, knownGeneMrna.txt, kgXref.txt
## and knownToLocusLink.txt). Put them all in some directory (probably you then need 
## to change the following path):


EXTERNAL_DATA?=src/main/resources

## The following four variables now give the complete path of these four files
KG="${EXTERNAL_DATA}/knownGene.txt"
MRNA="${EXTERNAL_DATA}/knownGeneMrna.txt" 
XR="${EXTERNAL_DATA}/kgXref.txt"
LOCUS="${EXTERNAL_DATA}/knownToLocusLink.txt"

## This is the default name of the output file
SERIAL="ucsc.ser"

##set -x  uncomment for VERBOSE

JAVA="/usr/bin/java"
MV="/bin/mv"

JANNOVAR="target/jannovar-0.0.6-SNAPSHOT.jar"


all: ucsc-ser


.PHONY: ucsc-ser
ucsc-ser: $(KG) $(MRNA) $(XR) $(LOCUS) $(SERIAL)
	${JAVA}  -Xms1G -Xmx2G -jar ${JANNOVAR} -U $(KG) -M $(MRNA) -X $(XR) -L $(LOCUS) -S $(SERIAL)


#.PHONY: load-external
#load-external:
# Employ wget to retrieve external data from NCBI/OMIM, Orphanet, HPO-annotation. 
# Files are listed in external_ncbi.txt. We also ignore lines starting with #

${KG}: ${KG}
	cd $(EXTERNAL_DATA) ; wget --retry-connrefused -N -i ${KG}.gz ; gunzip knownGene.txt.gz

$(MRNA):
	cd $(EXTERNAL_DATA) ; wget --retry-connrefused -N -i ${MRNA}.gz ; gunzip *gz

$(XR):
	cd $(EXTERNAL_DATA) ; wget --retry-connrefused -N -i ${XR}.gz ; gunzip *gz

$(LOCUS):
	cd $(EXTERNAL_DATA) ; wget --retry-connrefused -N -i ${LOCUS}.gz ; gunzip *gz


.PHONY: clean

clean:
	@-rm ucsc.ser




