====================================
Welcome to Jannovar's documentation!
====================================

Jannovar is a Java-based program and library for the functional annotation of VCF files.
The documentation is split into four parts (accessible through the navigation on the left).

Installation & Getting Started
    Instructions for the Installation of the program and some examples to get you started.

Jannovar Usage
    An overview of how Jannovar works and documentation and examples for the Jannovar sub commands.
    Also contains a description of the molecular impact prediction and other annotations from Jannovar.

Tips & Tricks
    Further documentation on Jannovar, information on troubleshooting etc.

Project Information
    More information on the project, including the changelog, list of contributing authors, and contribution instructions.


-------------
Quick Example
-------------

.. code-block:: console

    $ java -jar jannovar-\ |version|\ .jar \
        download -d hg19/refseq
    [...]
    $ java -jar jannovar-\ |version|\ .jar \
        annotate-vcf -d data/hg19_refseq.ser -i IN.vcf.gz -o OUT.vcf.gz


--------
Features
--------

- annotation of VCF files for functional impact, supporting different transcript databases (RefSeq, ENSEMBL, UCSC)
- annotation with information from dbSNP, ExAC, UK10K, ...
- ... and more

--------
Feedback
--------

The best place to leave feedback, ask questions, and report bugs is the `Jannovar Issue Tracker <https://github.com/charite/jannovar/issues>`_.


.. toctree::
    :caption: Installation & Getting Started
    :name: getting-started
    :maxdepth: 2
    :hidden:

    quickstart
    install


.. toctree::
    :caption: Jannovar Usage
    :name: jannovar-usage
    :maxdepth: 2
    :hidden:

    download
    annotate_vcf
    annotate_pos
    annotate_csv
    var_effects
    ped_filters


.. toctree::
    :caption: Tips & Tricks
    :name: tips-tricks
    :maxdepth: 2
    :hidden:

    memory
    proxy
    datasource


.. toctree::
    :caption: Project Info
    :name: project-info
    :maxdepth: 2
    :titlesonly:
    :hidden:

    contributing
    authors
    history
    license
