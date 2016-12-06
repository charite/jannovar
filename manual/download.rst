.. _download:

Downloading Transcript Databases
================================

The first step after installing Jannovar is to obtain a **transcript database**.
This database stores information about the transcripts, such as the location of a transcript and its exons, its CDS start and end position, and the transcript sequence.
There are three major sources of annotation databases for the main model organisms: (1) the UCSC genome browser, (2) the Ensembl project, and (3) the RefSeq database at NCBI.
Each database is linked to a certain release of a reference genome.

Displaying Available Database
-----------------------------

.. note:: You can use your own datasources by editing the ini file. See datasource_ for more information.

Jannovar has built-in support for the human and mouse genomes in releases ``hg18``, ``hg19``, ``hg38``, ``mm9``, and ``mm10``.
For each release, the database can originate from the sources ``ucsc``, ``ensembl``, and ``refseq``.
Further, the database can be limited to the curated transcripts only when using RefSeq: ``refseq_curated``.

The genome release names and the source names are joint into database descriptors such as ``hg19/ucsc`` and ``hg38/refseq``.
You can view the built-in database names using the ``db-list`` Jannovar command:

.. parsed-literal::

    $ java -jar jannovar-cli-\ |version|\ .jar db-list
    [...]
        hg18/refseq_curated
        hg19/ucsc
    [...]

Database Download
-----------------

A database can be downloaded using the ``download`` command.
You can pass a list of database source names to this command.
For each, Jannovar will download the database files over the network to the directory ``data/${source}``
This directory is created if necessary.
When a to be downloaded file already exists, Jannovar will not attempt to overwrite this file.

.. note::

    If you have problems with downloading files (e.g., because of proxy settings) and later on building the database fails then you should delete the directory ``data/${source}`` and retry downloading the file.

Finally, Jannovar will build a file with the extension ``.ser`` in the directory ``data``, e.g. ``data/hg19_ucsc.ser``.

.. note::

   If you are behind a proxy then you have to pass the appropriate argument to Jannovar download.
   For most users, adding ``--proxy http://proxy.example.com:8080/`` should suffice.
   Advanced proxy settings and details are explained in the section :ref:`proxy_settings`

Let us now download the RefSeq and UCSC annotations for human release *hg19*:

.. parsed-literal::

    $ java -jar jannovar-cli-\ |version|\ .jar download -d hg19/refseq -d hg19/ucsc


