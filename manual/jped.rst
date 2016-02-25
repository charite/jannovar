.. _jannovar_filter:

JPed - Filter for Compatible Variants
=====================================

The Jannovar package ``de.charite.compbio.jannovar.filter`` contains functionality to load PED files and check lists of genotype calls for compatibility with a given pedigree and a selected mode of inheritance.
This functionality is exposed in the ``jped-cli`` program.
You can get the command line help for ``jped-cli`` as follows:

::

    # java -jar jped-cli-0.16.jar --help

A basic call looks as follows:

::

    # java -jar jped-cli-0.16.jar -m MODE(S) -p IN.ped -i IN.vcf -o OUT.vcf

This call of ``jped-cli`` will first read in the pedigree from ``IN.ped``.
Then, it will read the file ``IN.vcf`` and filter the variants therein for compatibility with the given ``MODE(S)`` of inheritance and the pedigree.
Multiple `MODE(S)`` can be used. This can be usefull if you like to have autosomal dominant and x recessive in one file.
The resulting VCF file will be written to ``OUT.vcf``.

Available Modes of Inheritance
------------------------------

You can select one of the following modes of inheritance:

``AUTOSOMAL_DOMINANT``
  Require compatibility with autosomal dominant mode of inheritance.
  This can also be used to filter for *de novo* mutations.

``AUTOSOMAL_RECESSIVE``
  Require compatibility with autosomal recessive mode of inheritance.

``X_RECESSIVE``
  Require compatibility with X-recessive mode of inheritance.

``X_DOMINANT``
  Require compatibility with X-dominant mode of inheritance.

Gene-Wise Processing
--------------------

By default, ``jped-cli`` checks each record individually for compatibility.
Of course, this does not account for composite recessive autosomal mode of inheritance.
Here, all variants for a given gene have to be analyzed.

To enable gene-wise processing, you have to pass the ``--gene-wise`` flag and pass in a path to a Jannovar database (a ``.ser`` file, as previously downloaded with ``jannovar download``).
In this case, ``jped-cli`` will check all variants for compatibility with the selected mode of inheritance and will write out all variants in genes with possible compatibility.

.. note::

    When doing gene-wise processing, all variants are written out for a gene for which a compatible mutation was found.
    This sometimes causes confusion for users.
