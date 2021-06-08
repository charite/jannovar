#!/bin/bash

JANNOVAR_VERSION="0.36-SNAPSHOT"
JANNOVAR_CMD="java -jar jannovar-cli/target/jannovar-cli-${JANNOVAR_CMD}.jar"

# Download Jannovar v0.34 raw data ZIP from Zenodo.
if [[ ! -e data/raw/tmp/jannovar-data-0.34.zip ]]; then
    mkdir -p data/raw/tmp
    cd data/raw/tmp
    curl https://zenodo.org/record/4906580/files/jannovar-data-0.34.zip?download=1 \
    > jannovar-data-0.34.zip || \
    rm -f jannovar-data-0.34.zip
    cd ../../..
fi

# Extract the ZIP archive.
if [[ ! -e data/raw/jannovar-data-0.34 ]]; then
    mkdir -p data/raw/jannovar-data-0.34 && \
    cd data/raw/jannovar-data-0.34 && \
    unzip ../tmp/jannovar-data-0.34.zip || \
    rm -rf ../jannovar-data-0.34
    cd ../../..
fi

