.. _annotate_pos:

Annotating Positions
====================

Sometimes, it is useful to annotate a single position only, for example for quick checks or for debugging purposes.
You can do this using the ``annotate-pos`` command of Jannovar.

You have to pass a path to a annotation database file and one or more chromosomal change specifiers.
Jannovar will then return the effect and the HGVS annotation for each chromosomal change.

.. parsed-literal::
    # java -jar jannovar-cli-\ |version|\ .jar annotate-pos \\
    -d data/hg19_refseq.ser -c 'chr1:12345C>A' -c 'chr1:12346C>A'
    [...]
    #change     effect  hgvs_annotation
    chr1:12345C>A   NON_CODING_TRANSCRIPT_INTRON_VARIANT    DDX11L1:NR_046018.2:n.354+118C>A:
    chr1:12346C>A   NON_CODING_TRANSCRIPT_INTRON_VARIANT    DDX11L1:NR_046018.2:n.354+119C>A:


The format for the chromsomal change is as follows:

.. code-block:: console

    {CHROMOSOME}:{POSITION}{REF}>{ALT}

CHROMOSOME
  name of the chromosome or contig
POSITION
  position of the first change base on the chromosome; in the case of insertions the first base after the insertion; the first base on the chromosome has position ``1``
REF
  the reference bases
ALT
  the alternative bases
