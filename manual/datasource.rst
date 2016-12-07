.. _custom_datasource:

Custom Datasources
==================

Jannovar ships with a number of predefined data sources (e.g., UCSC, Ensembl, and RefSeq for human releases hg18 to hg38, and mouse mm9 and mm10).
However, it is quite easy to define your own data source by writing a datasource INI file.
This section describes how to define your own data source.

.. note::

    If you think that your new data source would be useful for others, please send them to us either using our `issue tracker <https://github.com/charite/jannovar/issues>`_ or by sending an email to Peter N Robinson <peter.robinson@jax.org>.

Datasource INI Files
--------------------

The data sources are defined in INI files.
For example, consider the following definition of human release ``hg19`` from UCSC:

.. code-block:: ini

    [hg19/ucsc]
    type=ucsc
    alias=MT,M,chrM
    chromInfo=http://hgdownload.soe.ucsc.edu/goldenPath/hg19/database/chromInfo.txt.gz
    chrToAccessions=ftp://ftp.ncbi.nlm.nih.gov/genomes/H_sapiens/ARCHIVE/ANNOTATION_RELEASE.105/Assembled_chromosomes/chr_accessions_GRCh37.p13
    chrToAccessions.format=chr_accessions
    knownCanonical=http://hgdownload.soe.ucsc.edu/goldenPath/hg19/database/knownCanonical.txt.gz
    knownGene=http://hgdownload.soe.ucsc.edu/goldenPath/hg19/database/knownGene.txt.gz
    knownGeneMrna=http://hgdownload.soe.ucsc.edu/goldenPath/hg19/database/knownGeneMrna.txt.gz
    kgXref=http://hgdownload.soe.ucsc.edu/goldenPath/hg19/database/kgXref.txt.gz
    knownToLocusLink=http://hgdownload.soe.ucsc.edu/goldenPath/hg19/database/knownToLocusLink.txt.gz

The section name ``hg19/ucsc`` defines the data source name.
When saving the above file contents as ``my_ucsc.ini``, you can pass it to the Jannovar ``download`` command ``--data-source-list``/``-s``.

.. parsed-literal::
    java -Xms2G -Xmx2G -jar jannovar-cli-\ |version|\ .jar download -s my_ucsc.ini -d hg19/ucsc

Your INI file can either add new definitions or override the built-in ones.
In fact, the definition from above is part of the INI file that is contained in the Jannovar JAR file and used by default.

The ``type`` setting of the data source section defines the type of the data source.
Currently, Jannovar supports the types ``ensembl``, ``refseq``, and ``ucsc``.
The sections below explain the general settings and the data source types further.

.. _chrom_aliasing:

Chromosome Aliasing
-------------------

The ``alias`` setting defines an aliasing of the contigs and chromosomes.
It can be used regardless of the used data source type.

The names of the contigs from the different data sources usually differ between UCSC and RefSeq (and Ensembl which uses the same names as RefSeq).
Usually, the UCSC names can be derived from the RefSeq names by prepending ``"chr"``.
However, this is not true for the important case of the mitochondrial chromosome.

The ``alias`` line from above defines an alias between the chromosome names *MT*, *M*, and *chrM*.
The first entry (*MT*) is implicitely added if it is not in the *chromInfo* file (see :ref:`chrom_mappings`).
This is the case for older RefSeq releases.

.. _chrom_mappings:

Name Mapping and Lengths
------------------------

The ``chromInfo`` setting defines the URL to the ``chromInfo.txt.gz`` file from UCSC.
Usually, this URL is ``http://hgdownload.soe.ucsc.edu/goldenPath/${RELEASE}/database/chromInfo.txt.gz``.
This file contains the contig lengths for each chromosome with the UCSC name of the chromosome/contig (e.g., ``chr19``).

The ``chrToAccessions`` setting defines the URL to the RefSeq file that contains the mapping from the RefSeq names to the RefSeq and GenBank contig sequence accessions.
It is assumed that the UCSC contig names are derived from the RefSeq contig names by prepending ``"chr"``, also see :ref:`chrom_aliasing`.
This information is required as it is equally common to use the RefSeq names, UCSC names, or Genbank or RefSeq contig sequence accessions.

The two settings ``chromInfo`` and ``chrToAccessions`` have to be provided for all data source types.

The ``chroToAccessions`` file can have different formats, specified as ``chrToAccessions.format``.
The "modern" one is ``chr_accessions`` where the file is a TSV file with five columns, e.g.:

.. code-block:: text

    #Chromosome	RefSeq Accession.version	RefSeq gi	GenBank Accession.version	GenBank gi
    1	NC_000001.10	224589800	CM000663.1	224384768
    2	NC_000002.11	224589811	CM000664.1	224384767
    3	NC_000003.11	224589815	CM000665.1	224384766
    [...]

The first column gives the RefSeq name, the second the RefSeq sequence accession number, and the fourth one the GenBank accession number.

The ``chr_NC_gi`` file format has four columns and contains the mapping for the HuRef but also alternative assemblies, e.g.:

.. code-block:: text

    #Chr	Accession.ver	gi	Assembly
    1	AC_000044.1	89161184	Celera
    2	AC_000045.1	89161198	Celera
    [...]
    1	AC_000133.1	157704448	HuRef
    2	AC_000134.1	157724517	HuRef

In this case, you have to specify a value that the last column should match to.
The hg18 release uses the ``chr_NC_gi`` format, for example.
Here, we filter the lines to those having ``"HuRef"`` in the last column:

.. code-block:: ini
    :emphasize-lines: 5-7

    [hg18/refseq]
    type=refseq
    alias=MT,M,chrM
    chromInfo=http://hgdownload.soe.ucsc.edu/goldenPath/hg18/database/chromInfo.txt.gz
    chrToAccessions=ftp://ftp.ncbi.nlm.nih.gov/genomes/H_sapiens/ARCHIVE/BUILD.36.3/Assembled_chromosomes/chr_NC_gi
    chrToAccessions.format=chr_NC_gi
    chrToAccessions.matchLast=HuRef
    gff=ftp://ftp.ncbi.nlm.nih.gov/genomes/H_sapiens/ARCHIVE/BUILD.36.3/GFF/ref_NCBI36_top_level.gff3.gz
    rna=ftp://ftp.ncbi.nlm.nih.gov/genomes/H_sapiens/ARCHIVE/BUILD.36.3/RNA/rna.fa.gz

Ensembl Data Sources
--------------------

When selecting the ``ensembl`` data source type then you have to pass the transcript definition GTF URL to ``gtf`` and the cDNA FASTA file to ``cdna``.
Below is an example for the Ensemble data source for human release hg19.

.. code-block:: ini
    :emphasize-lines: 7-8

    [hg19/ensembl]
    type=ensembl
    alias=MT,M,chrM
    chromInfo=http://hgdownload.soe.ucsc.edu/goldenPath/hg19/database/chromInfo.txt.gz
    chrToAccessions=ftp://ftp.ncbi.nlm.nih.gov/genomes/H_sapiens/ARCHIVE/ANNOTATION_RELEASE.105/Assembled_chromosomes/chr_accessions_GRCh37.p13
    chrToAccessions.format=chr_accessions
    gtf=ftp://ftp.ensembl.org/pub/release-74/gtf/homo_sapiens/Homo_sapiens.GRCh37.74.gtf.gz
    cdna=ftp://ftp.ensembl.org/pub/release-74/fasta/homo_sapiens/cdna/Homo_sapiens.GRCh37.74.cdna.all.fa.gz

RefSeq Data Sources
-------------------

When selecting the ``ensembl`` data source type then you have to pass the transcript definition GFF URL to ``gff`` and the RNA FASTA file to ``rna``.
Below is an example for the RefSeqe data source for human release hg19.

.. code-block:: ini
    :emphasize-lines: 7-8

    [hg19/refseq]
    type=refseq
    alias=MT,M,chrM
    chromInfo=http://hgdownload.soe.ucsc.edu/goldenPath/hg19/database/chromInfo.txt.gz
    chrToAccessions=ftp://ftp.ncbi.nlm.nih.gov/genomes/H_sapiens/ARCHIVE/ANNOTATION_RELEASE.105/Assembled_chromosomes/chr_accessions_GRCh37.p13
    chrToAccessions.format=chr_accessions
    gff=ftp://ftp.ncbi.nlm.nih.gov/genomes/H_sapiens/ARCHIVE/ANNOTATION_RELEASE.105/GFF/ref_GRCh37.p13_top_level.gff3.gz
    rna=ftp://ftp.ncbi.nlm.nih.gov/genomes/H_sapiens/ARCHIVE/ANNOTATION_RELEASE.105/RNA/rna.fa.gz

For RefSeq, you can also limit building the database to those transcripts that are curated (e.g., that do not have a name starting with ``"XM_"`` or ``"XR_"``.
You can do this by setting ``onlyCurated`` to ``true``:

.. code-block:: ini
    :emphasize-lines: 4

    [hg19/refseq_curated]
    type=refseq
    alias=MT,M,chrM
    onlyCurated=true
    chromInfo=http://hgdownload.soe.ucsc.edu/goldenPath/hg19/database/chromInfo.txt.gz
    chrToAccessions=ftp://ftp.ncbi.nlm.nih.gov/genomes/H_sapiens/ARCHIVE/ANNOTATION_RELEASE.105/Assembled_chromosomes/chr_accessions_GRCh37.p13
    chrToAccessions.format=chr_accessions
    gff=ftp://ftp.ncbi.nlm.nih.gov/genomes/H_sapiens/ARCHIVE/ANNOTATION_RELEASE.105/GFF/ref_GRCh37.p13_top_level.gff3.gz
    rna=ftp://ftp.ncbi.nlm.nih.gov/genomes/H_sapiens/ARCHIVE/ANNOTATION_RELEASE.105/RNA/rna.fa.gz

UCSC Data Sources
-----------------

For UCSC data sources, you have specify the settings ``knownCanonical``, ``knownGene``, ``knownGeneMrna``, ``kgXref``, and ``knownToLocusLink``.
These can usually be derived from the example below by exchanging ``hg19`` by the release id (e.g., ``mm10`` for mouse release 10).

.. code-block:: ini
    :emphasize-lines: 7-10

    [hg19/ucsc]
    type=ucsc
    alias=MT,M,chrM
    chromInfo=http://hgdownload.soe.ucsc.edu/goldenPath/hg19/database/chromInfo.txt.gz
    chrToAccessions=ftp://ftp.ncbi.nlm.nih.gov/genomes/H_sapiens/ARCHIVE/ANNOTATION_RELEASE.105/Assembled_chromosomes/chr_accessions_GRCh37.p13
    chrToAccessions.format=chr_accessions
    knownCanonical=http://hgdownload.soe.ucsc.edu/goldenPath/hg19/database/knownCanonical.txt.gz
    knownGene=http://hgdownload.soe.ucsc.edu/goldenPath/hg19/database/knownGene.txt.gz
    knownGeneMrna=http://hgdownload.soe.ucsc.edu/goldenPath/hg19/database/knownGeneMrna.txt.gz
    kgXref=http://hgdownload.soe.ucsc.edu/goldenPath/hg19/database/kgXref.txt.gz
    knownToLocusLink=http://hgdownload.soe.ucsc.edu/goldenPath/hg19/database/knownToLocusLink.txt.gz

