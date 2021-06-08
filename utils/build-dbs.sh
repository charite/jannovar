#!/bin/bash

set -x
set -euo pipefail

JANNOVAR_DATA_VERSION=0.36
JANNOVAR_VERSION=0.36-SNAPSHOT
JANNOVAR_JAR=$(readlink -f jannovar-cli/target/jannovar-cli-${JANNOVAR_VERSION}.jar)

#${database}${_variant}_${release}_${genome}

mkdir -p data/jannovar-data-${JANNOVAR_DATA_VERSION}


# Helper for building .ser files.
#
# Usage: build-db database $variant $version $reference
build-db()
{
    db=$1
    variant=$2
    test -z "${variant}" || variant=_${variant}
    version=$3
    reference=$4

    if [[ ! -e data/jannovar-data-${JANNOVAR_DATA_VERSION}/${db}${variant}_${version}_${reference}.ser ]]; then
        mkdir -p data/tmp/jannovar-data-${JANNOVAR_DATA_VERSION}/${db}${variant}_${version}_${reference}/data/${reference}/${db}${variant}
        cp \
            data/raw/jannovar-data-0.34/${reference}/${db}${variant}/* \
            data/tmp/jannovar-data-${JANNOVAR_DATA_VERSION}/${db}${variant}_${version}_${reference}/data/${reference}/${db}${variant}
        pushd data/tmp/jannovar-data-${JANNOVAR_DATA_VERSION}/${db}${variant}_${version}_${reference}

        if [[ ! -e data/${reference}_${db}${variant}.ser ]]; then
            java -jar ${JANNOVAR_JAR} download -d ${reference}/${db}${variant} \
            &> data/${reference}_${db}${variant}.ser.log
        fi
        cp \
            data/${reference}_${db}${variant}.ser \
            ../../../jannovar-data-${JANNOVAR_DATA_VERSION}/${db}${variant}_${version}_${reference}.ser
        cp \
            data/${reference}_${db}${variant}.ser.log \
            ../../../jannovar-data-${JANNOVAR_DATA_VERSION}/${db}${variant}_${version}_${reference}.ser.log

        popd
    fi
}

# Build RefSeq database files.
build-db refseq "" 105 hg19
build-db refseq "curated" 105 hg19
build-db refseq "" 109 hg38
build-db refseq "curated" 109 hg38
build-db refseq "" 37.2 mm9
build-db refseq "curated" 37.2 mm9
build-db refseq "" 106 mm9
build-db refseq "curated" 106 mm9
build-db refseq "" 105 rn6
build-db refseq "curated" 105 rn6
# Build ENSEMBL databases.
build-db ensembl "" 87 hg19
build-db ensembl "" 91 hg38

# Compute MD5 checksums
pushd data/jannovar-data-${JANNOVAR_DATA_VERSION}

for x in *.ser; do
    if [[ -e $x.md5 ]]; then
        md5sum --check $x.md5
    else
        md5sum $x >$x.md5
    fi
done

popd
