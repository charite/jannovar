.. _annotate_vcf:

Annotating VCF Files
====================

The main purpose of Jannovar is the annotation of all variants in a VCF file.
That is, for each annotation, predict the results for all transcripts that can be afflicted by the change.
Depending on the configuration, the one effect that is most pathogenic, or all, are written out.

This is done using the ``annotate`` command.
You pass the path to an annotation database and one or more paths to VCF files that are to be annotated.
For each file, the resulting annotated file is to the current directory, the file name is derived by replacing the file name suffix ``.vcf`` to ``.jv.vcf``.

For example, for annotating the ``pfeiffer.vcf`` file in the ``examples`` directory:

.. code-block:: console

    # java -jar jannovar-cli/target/jannovar-cli-0.10.jar annotate data/hg19_ucsc.ser examples/pfeiffer.vcf
    [...]
    # ls
    [...]
    pfeiffer.jv.vcf

.. note:: TODO: describe Jannovar format
.. note:: TODO: describe show-all option
