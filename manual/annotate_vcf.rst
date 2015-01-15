.. _annotate_vcf:

Annotating VCF Files
====================

The main purpose of Jannovar is the annotation of all variants in a VCF file.
That is, for each annotation, predict the results for all transcripts that can be afflicted by the change.
Depending on the configuration, the one effect that is most pathogenic, or all, are written out.

This is done using the ``annotate`` command.
You pass the path to an annotation database and one or more paths to VCF files that are to be annotated.
For each file, the resulting annotated file is to the current directory, the file name is derived by replacing the file name suffix ``.vcf`` to ``.jv.vcf``.

For example, for annotating the ``small.vcf`` file in the ``examples`` directory:

.. code-block:: console

    # java -jar jannovar-cli-|version|.jar annotate data/hg19_ucsc.ser examples/small.vcf
    [...]
    # ls
    [...]
    small.jv.vcf

The first three variant lines of ``small.jv.vcf`` will look as follows.:w

.. code-block:: text

      1	866511	rs60722469	C	CCCCT	258.62	PASS	EFFECT=INTRONIC;HGVS=SAMD11:NM_152486.2:c.305+42_305+43insCCCT	GT:AD:DP:GQ:PL	1/1:6,5:11:14.79:300,15,0
      1	879317	rs7523549	C	T	150.77	PASS	EFFECT=MISSENSE;HGVS=SAMD11:XM_005244727.1:exon9:c.799C>T:p.Arg267Cys	GT:AD:DP:GQ:PL	0/1:14,7:21:99:181,0,367
      1	879482	.	G	C	484.52	PASS	EFFECT=MISSENSE;HGVS=SAMD11:XM_005244727.1:exon9:c.964G>C:p.Asp322His	GT:AD:DP:GQ:PL	0/1:28,20:48:99:515,0,794

The Show-All Option
-------------------

By default, Jannovar will only write out one most pathogenic variant as predicted.
You can use the ``--show-all``/``-a`` option to write out all functional annotations:

.. code-block:: console

    # java -jar jannovar-cli-|version|.jar annotate --show-all data/hg19_refseq.ser examples/small.vcf

For example, the first line of ``small.jv.vcf`` will look as follows and contain multiple effects and HGVS annotations.

.. code-block:: text

    1	866511	rs60722469	C	CCCCT	258.62	PASS	EFFECT=INTRONIC,INTRONIC,INTRONIC,INTRONIC,INTRONIC,INTRONIC,ncRNA_INTRONIC,ncRNA_INTRONIC;HGVS=SAMD11:NM_152486.2:c.305+42_305+43insCCCT,SAMD11:XM_005244723.1:c.305+42_305+43insCCCT,SAMD11:XM_005244724.1:c.305+42_305+43insCCCT,SAMD11:XM_005244725.1:c.305+42_305+43insCCCT,SAMD11:XM_005244726.1:c.305+42_305+43insCCCT,SAMD11:XM_005244727.1:c.305+42_305+43insCCCT,SAMD11:XR_241028.1:n.661+42_661+43insCCCT,SAMD11:XR_241029.1:n.661+42_661+43insCCCT	GT:AD:DP:GQ:PL	1/1:6,5:11:14.79:300,15,0


.. TODO: describe Jannovar format
