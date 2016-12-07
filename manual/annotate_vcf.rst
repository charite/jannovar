.. _annotate_vcf:

Annotating VCF Files
====================

The main purpose of Jannovar is the annotation of all variants in a VCF file.
That is, for each annotation, predict the results for all transcripts that can be afflicted by the change.
Depending on the configuration, the one effect that is most pathogenic, or all, are written out.

This is done using the ``annotate-vcf`` command.
You pass the path to an annotation database and one VCF file that should be annotated.
The resulting annotated file is written to the file specified by ``-o`` or ``--output-vcf``.

For example, for annotating the ``small.vcf`` file (see `small.vcf <https://github.com/charite/jannovar/blob/master/examples/small.vcf>`_) in the ``examples`` directory:

.. parsed-literal::
    # java -jar jannovar-cli-\ |version|\ .jar annotate-vcf \\
    -d data/hg19_refseq.ser -i examples/small.vcf -o examples/small.jv.vcf
    [...]
    # ls examples/small.jv.vcf
    small.jv.vcf

The first three variant lines of ``examples/small.jv.vcf`` will look as follows.

.. code-block:: text

	1	866511	rs60722469	C	CCCCT	258.62	.	ANN=CCCCT|coding_transcript_intron_variant|LOW|SAMD11|148398|transcript|NM_152486.2|Coding|4/13|c.305+42_305+43insCCCT|p.(%3D)|386/18841|306/2046|102/682||	GT:AD:DP:GQ:PL	1/1:6,5:11:14.79:300,15,0
	1	879317	rs7523549	C	T	150.77	.	ANN=T|missense_variant|MODERATE|SAMD11|148398|transcript|XM_005244727.1|Coding|9/9|c.799C>T|p.(Arg267Cys)|1155/19962|799/1188|267/396||	GT:AD:DP:GQ:PL	0/1:14,7:21:99:181,0,367
	1	879482	.	G	C	484.52	.	ANN=C|missense_variant|MODERATE|SAMD11|148398|transcript|XM_005244727.1|Coding|9/9|c.964G>C|p.(Asp322His)|1320/19962|964/1188|322/396||	GT:AD:DP:GQ:PL	0/1:28,20:48:99:515,0,794

Disabling 3' Shifting
---------------------

The `HGVS Nomenclature for the description fo sequence variants <http://varnomen.hgvs.org/>`_  requires that variants are to be shifted towards the 3' end of transcripts in case of ambiguities.
This is in partial conflict with the VCF standard which requires all variant calls to be shifted towards the 3' end of the genome.
In the case that Jannovar shifted the variants towards the 3' end of the transcript, it will generate a ``INFO_REALIGN_3_PRIME`` information in the message field of the annotation (``ANN`` field).

To comply with the VCF annotation standard, Jannovar also implements the ``--no-3-prime-shifting`` option.
Using this switch suppresses this shifting and the variant will be kept as given in the VCF file.
Here is an example of using this command line option:

.. parsed-literal::
    # java -jar jannovar-cli-\ |version|\ .jar annotate --no-3-prime-shifting \\
    -d data/hg19_refseq.ser -i examples/small.vcf -o examples/small.jv.vcf

The Show-All Option
-------------------

By default, Jannovar will only write out one most pathogenic variant as predicted.
You can use the ``--show-all`` option to write out all functional annotations:

.. parsed-literal::
    # java -jar jannovar-cli-\ |version|\ .jar annotate-vcf --show-all \\
    -d data/hg19_refseq.ser -i examples/small.vcf -o examples/small.jv.vcf

For example, the first line of ``small.jv.vcf`` will look as follows and contain multiple effects and HGVS annotations.

.. code-block:: text

	1	866511	rs60722469	C	CCCCT	258.62	.	ANN=CCCCT|coding_transcript_intron_variant|LOW|SAMD11|148398|transcript|NM_152486.2|Coding|4/13|c.305+42_305+43insCCCT|p.(%3D)|386/18841|306/2046|102/682||,CCCCT|coding_transcript_intron_variant|LOW|SAMD11|148398|transcript|XM_005244723.1|Coding|4/12|c.305+42_305+43insCCCT|p.(%3D)|662/19962|306/2145|102/715||,CCCCT|coding_transcript_intron_variant|LOW|SAMD11|148398|transcript|XM_005244724.1|Coding|4/13|c.305+42_305+43insCCCT|p.(%3D)|662/19962|306/2001|102/667||,CCCCT|coding_transcript_intron_variant|LOW|SAMD11|148398|transcript|XM_005244725.1|Coding|4/13|c.305+42_305+43insCCCT|p.(%3D)|662/19962|306/1998|102/666||,CCCCT|coding_transcript_intron_variant|LOW|SAMD11|148398|transcript|XM_005244726.1|Coding|4/11|c.305+42_305+43insCCCT|p.(%3D)|662/19962|306/1719|102/573||,CCCCT|coding_transcript_intron_variant|LOW|SAMD11|148398|transcript|XM_005244727.1|Coding|4/8|c.305+42_305+43insCCCT|p.(%3D)|662/19962|306/1188|102/396||,CCCCT|non_coding_transcript_intron_variant|LOW|SAMD11|148398|transcript|XR_241028.1|Noncoding|4/12|n.661+42_661+43insCCCT||662/19541||||,CCCCT|non_coding_transcript_intron_variant|LOW|SAMD11|148398|transcript|XR_241029.1|Noncoding|4/12|n.661+42_661+43insCCCT||662/19541||||	GT:AD:DP:GQ:PL	1/1:6,5:11:14.79:300,15,0
