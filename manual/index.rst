====================================
Welcome to Jannovar's documentation!
====================================

Jannovar is a Java-based program and library for the functional annotation of VCF files.
The documentation is split into five parts (accessible through the navigation on the left).

Installation & Getting Started
    Instructions for the Installation of the program and some examples to get you started.

Jannovar Usage
    An overview of how Jannovar works and documentation and examples for the Jannovar sub commands.
    The original and prime feature of Jannovar predicting molecular impact of variants given a transcriptome model.
    Further features such as the conversion between HGVS and VCF are also described.

Further Annotation
    This part documents the annotation with different databases and compatible modes of inheritance.
    Also, it quickly describes how to (hard-)filter your VCF files given annotations using ``bcftools``.

Tips & Tricks
    Further documentation on Jannovar, information on troubleshooting etc.

Project Information
    More information on the project, including the changelog, list of contributing authors, and contribution instructions.


-------------
Quick Example
-------------

   .. parsed-literal::

    $ java -jar jannovar-\ |version|\ .jar \\
        download -d hg19/refseq
    [...]
    $ java -jar jannovar-\ |version|\ .jar \\
        annotate-vcf -d data/hg19_refseq.ser -i IN.vcf.gz -o OUT.vcf.gz


--------
Features
--------

- annotation of VCF files for functional impact, supporting different transcript databases (RefSeq, ENSEMBL, UCSC)
- annotation with information from dbSNP, ExAC, UK10K, ...
- use `Sequence Ontology <http://charite.github.io/jannovar/api/0.17/de/charite/compbio/jannovar/annotation/VariantEffect.html>`_ for variant effect annotation
- API Javadoc for the library is here: |api_url|.
- ... and more

--------
Feedback
--------

The best place to leave feedback, ask questions, and report bugs is the `Jannovar Issue Tracker <https://github.com/charite/jannovar/issues>`_.

-----------------
API Documentation
-----------------

The friendly people at `javadoc.io <https://www.javadoc.io>`_ host our API documentation:

jannovar-core
    |api_url|

jannovar-hgvs
    |api_url_hgvs|

jannovar-htsjdk
    |api_url_htsjdk|

jannovar-vardbs
    |api_url_vardbs|


.. toctree::
    :caption: Installation & Getting Started
    :name: getting-started
    :maxdepth: 1
    :hidden:

    quickstart
    install


.. toctree::
    :caption: Jannovar Usage
    :name: jannovar-usage
    :maxdepth: 1
    :hidden:

    download
    annotate_vcf
    annotate_pos
    annotate_csv
    hgvs_to_vcf
    ped_filters
    jannovar_lib

.. toctree::
    :caption: Further annotation
    :name: jannovar-annotation
    :maxdepth: 1
    :hidden:

    annotate_vcf_databases
    inheritance
    filter

.. toctree::
    :caption: Tips & Tricks
    :name: tips-tricks
    :maxdepth: 1
    :hidden:

    memory
    proxy
    datasource
    faq


.. toctree::
    :caption: Project Info
    :name: project-info
    :maxdepth: 1
    :hidden:

    contributing
    authors
    history
    license
