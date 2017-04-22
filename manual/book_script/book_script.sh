#!/bin/bash

# Examples from the exome book for Jannovar 0.20

# --------------------
# Chapter 15: Jannovar
# --------------------

# begin-chapter-15-jannovar-clone
git clone https://github.com/charite/jannovar
git checkout tags/v0.20
# end-chapter-15-jannovar-clone

# begin-chapter-15-jannovar-mvn-package
cd jannovar
mvn package
# end-chapter-15-jannovar-mvn-package

# begin-chapter-15-jannovar-ln-s
ln -s jannovar-cli/target/jannovar-cli-0.20.jar \
    jannovar.jar
# end-chapter-15-jannovar-ln-s

cd ..

# ------------------------------
# Chapter 16: Variant Calling QC
# ------------------------------

# ------------------------------
# Chapter 17: Variant Annotation
# ------------------------------

# -----------------------------
# Chapter 19: Pedigree Analysis
# -----------------------------

# -----------------------------
# Chapter 21: Variant Frequency
# -----------------------------
