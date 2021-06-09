[![Build Status](https://github.com/charite/jannovar/actions/workflows/ci.yml/badge.svg?branch=master)](https://github.com/charite/jannovar/actions/workflows/ci.yml)
[![Documentation](https://readthedocs.org/projects/jannovar/badge/?version=master)](http://jannovar.readthedocs.org/)
[![API Docs](https://img.shields.io/badge/api-v0.36-blue.svg?style=flat)](http://javadoc.io/doc/de.charite.compbio/jannovar-core/0.36)
[![Install with Bioconda](https://img.shields.io/badge/install%20with-bioconda-brightgreen.svg)](https://bioconda.github.io/recipes/jannovar-cli/README.html)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/2b12f94e30404667997f8ae264a97bd6)](https://www.codacy.com/app/visze/jannovar?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=charite/jannovar&amp;utm_campaign=Badge_Grade)

# Jannovar

Functional variant file annotation in Java. Jannovar provides a program for
the annotation of VCF files and also exposes its functionality through a
library API.

Also see the
[Quickstart](http://jannovar.readthedocs.org/en/master/quickstart.html) section
in the Jannovar manual.

## In Brief

- **Language/Platform:** Java >=8
- **License:** BSD 3-Clause
- **Version:** see Github side bar for current release
- **Availability:**
    - Java command line tool `jannovar-cli`
    - Java libraries exposing most of `jannovar-cli`'s functionality.

## Databases

As of Jannovar version v0.36, we provide pre-built databases via Zenodo.

[![DOI](https://zenodo.org/badge/DOI/10.5281/zenodo.4916051.svg)](https://doi.org/10.5281/zenodo.4916051)

You can obtain pre-built databases Zenodo as shown from the following table.
In the case that you need is missing, please [start a Github discussion](https://github.com/charite/jannovar/discussions).

| Organism | Database | DB release | Reference | File | MD5 Sum |
| -------- | -------- | ---------- | --------- | ---- | ------- |
| H.&nbsp;sapiens | ENSEMBL | 87 | hg19 | [ensembl_87_hg19.ser](https://zenodo.org/api/files/470066bc-a797-48e3-80f6-8bf25920a15d/ensembl_87_hg19.ser) | ecaffeaa26531a002e75953c6b309c53 |
| H.&nbsp;sapiens | ENSEMBL | 91 | hg38 | [ensembl_91_hg38.ser](https://zenodo.org/api/files/470066bc-a797-48e3-80f6-8bf25920a15d/ensembl_91_hg38.ser) | 6218669555a52057ee88132edfed0ae2 |
| H.&nbsp;sapiens | RefSeq | 105 | hg19 | [refseq_105_hg19.ser](https://zenodo.org/api/files/470066bc-a797-48e3-80f6-8bf25920a15d/refseq_105_hg19.ser) | b2087f8f3d41d20ad52fb9660853642e |
| H.&nbsp;sapiens | RefSeq\* | 105 | hg19 | [refseq_curated_105_hg19.ser](https://zenodo.org/api/files/470066bc-a797-48e3-80f6-8bf25920a15d/refseq_curated_105_hg19.ser) | a92fea7b8e37d46c75936783ae326d71 |
| H.&nbsp;sapiens | RefSeq\* | 105 | rn6 | [refseq_curated_105_rn6.ser](https://zenodo.org/api/files/470066bc-a797-48e3-80f6-8bf25920a15d/refseq_curated_105_rn6.ser) | b028ae0e6768c0505b7a4d2fe89cd462 |
| H.&nbsp;sapiens | RefSeq | 109 | hg38 | [refseq_109_hg38.ser](https://zenodo.org/api/files/470066bc-a797-48e3-80f6-8bf25920a15d/refseq_109_hg38.ser) | 6b1205bb534adb5ff9e0e569e6fabc5d |
| H.&nbsp;sapiens | RefSeq\* | 109 | hg38 | [refseq_curated_109_hg38.ser](https://zenodo.org/api/files/470066bc-a797-48e3-80f6-8bf25920a15d/refseq_curated_109_hg38.ser) | c2747c4c1b42a75930603d6deda105cf |
| M.&nbsp;musculus | RefSeq | 106 | mm9 | [refseq_106_mm9.ser](https://zenodo.org/api/files/470066bc-a797-48e3-80f6-8bf25920a15d/refseq_106_mm9.ser) | 1f7e2bf9860d06fab85225987fef3550 |
| M.&nbsp;musculus | RefSeq\* | 106 | mm9 | [refseq_curated_106_mm9.ser](https://zenodo.org/api/files/470066bc-a797-48e3-80f6-8bf25920a15d/refseq_curated_106_mm9.ser) | 059bd7103dbf4014bebd2f900af7b36b |
| M.&nbsp;musculus | RefSeq | 37.2 | mm9 | [refseq_37.2_mm9.ser](https://zenodo.org/api/files/470066bc-a797-48e3-80f6-8bf25920a15d/refseq_37.2_mm9.ser) | 1f7e2bf9860d06fab85225987fef3550 |
| M.&nbsp;musculus | RefSeq\* | 37.2 | mm9 | [refseq_curated_37.2_mm9.ser](https://zenodo.org/api/files/470066bc-a797-48e3-80f6-8bf25920a15d/refseq_curated_37.2_mm9.ser) | 059bd7103dbf4014bebd2f900af7b36b |
| R.&nbsp;norvegicus | RefSeq | 105 | rn6 | [refseq_105_rn6.ser](https://zenodo.org/api/files/470066bc-a797-48e3-80f6-8bf25920a15d/refseq_105_rn6.ser) | 4a9c3416ee9159c0c71f613a3d168869 |

Note: `RefSeq*` = RefSeq with curated / `NM_` transcript only and excluding `XM_` transcripts that are based on gene predictions.

Note that files are compatible with both the NCBI and the UCSC genomes.
E.g., the files for hg19 are compatible with the UCSC hg19 FASTA file and the GRCh37 files (e.g., hs37/hs37d5).

### Database Compatibility

Jannovar database `.ser` files are compatible within a given version range with respect to the Jannovar version.
The following table lists the compatibility.

| First Version | Last Version | Notes |
| ------------- | ------------ | ----- |
| 0.33          | 0.36         | first version with compatibility description |

## Developer Guidelines

### Style

- Java code should follow IntelliJ default formatting and the `Ctrl+Alt+l` formatter.
  Eclipse users please use [Eclipse Code Formatter](https://plugins.jetbrains.com/plugin/6546-eclipse-code-formatter).
  Enable the "wrap at right margin" option for JavaDoc.
- For all other text, use `.editorconfig`.

## Building Transcript Databases

For building Jannovar transcript database files (with `.ser` extension), you will need files from various sources.
These include the actual transcript databases from RefSeq, ENSEMBL, UCSC etc.
But you will also helper files for mapping between gene names and symbols from HGNC and information regarding contig sequence identifiers from NCBI.
It turned out that the upstream locations are unstable so we resolved in uploading the files to Zenodo as this offers stable identifiers.
At the same time, this create challenges in versioning as, e.g., UCSC regularly publishes updates without giving out versions.

### Downloading Raw Data Files

The script `./utils/download-raw.sh` contains scripts to download raw data files from the original "upstream" locations.
The files will go into `./data` (ignored via `.gitignore`).
The top level file directory is `./data/raw/bwa.3430-N1-DNA1-WGS1.bam.7z/` which contains the raw data files for building the database of name `${database}`, in variant `${_variant}` (e.g., `refseq_curated`) that for a given release and genome build.
Everything below this will follow specific requirements of the given data base.
The `download-raw.sh` script may also directly download data from Zenodo where applicable.

The script `./utils/gen-zenodo-raw.sh` will prepare the previously downloaded raw data for upload to Zenodo.
The files will go to `./data/zenodo-raw`.
Zenodo does not support folders so we fall back to introducing `--` as flat file separators.
Note well that uploading files twice to Zenodo just takes space on their storage systems and we don't have any mechanism in place to remove duplicates.

### Building Databases

The script `./utils/build-dbs.sh` will generate the databases for the current Jannovar version.
The files will go into `./data/jannovar-data-${jannovar_version}`.
These files can also go to Zenodo.
For now, we will curate links to the files in the `README.md` file for each version.
Note that not each Jannovar version will require rebuilding the databases.
The currently needed latest version is given in `JannovarDataSerializer.minVersion`.
Upload to Zenodo and curation of databases is currently manual work.
