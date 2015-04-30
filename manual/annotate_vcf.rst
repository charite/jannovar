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

    # java -jar jannovar-cli-0.14.jar annotate data/hg19_ucsc.ser examples/small.vcf
    [...]
    # ls examples/small.jv.vcf
    small.jv.vcf

The first three variant lines of ``examples/small.jv.vcf`` will look as follows.

.. code-block:: text

    1   866511  rs60722469      C       CCCCT   258.62  PASS    ANN=CCCCT|5_prime_utr_variant|LOW|SAMD11|ENTREZ148398|transcript|uc031pjn.1|Coding|2/4|c.-129+795_-129+796insCCCT|p.%3D|275/18232|1/558|1/186|| GT:AD:DP:GQ:PL  1/1:6,5:11:14.79:300,15,0
    1   879317  rs7523549       C       T       150.77  PASS    ANN=T|missense_variant|MODERATE|SAMD11|ENTREZ148398|transcript|uc031pjn.1|Coding|5/5|c.343C>T|p.Arg115Cys|745/18232|343/558|115/186||   GT:AD:DP:GQ:PL  0/1:14,7:21:99:181,0,367
    1   879482  .       G       C       484.52  PASS    ANN=C|missense_variant|MODERATE|SAMD11|ENTREZ148398|transcript|uc031pjn.1|Coding|5/5|c.508G>C|p.Asp170His|910/18232|508/558|170/186||   GT:AD:DP:GQ:PL  0/1:28,20:48:99:515,0,794

Disabling 3' Shifting
---------------------

The `HGVS Nomenclature for the description fo sequence variants <http://www.hgvs.org/mutnomen/>`_  requires that variants are to be shifted towards the 3' end of transcripts in case of ambiguities.
This is in partial conflict with the VCF standard which requires all variant calls to be shifted towards the 3' end of the genome.
In the case that Jannovar shifted the variants towards the 3' end of the transcript, it will generate a ``INFO_REALIGN_3_PRIME`` information in the message field of the annotation (``ANN`` field).

To comply with the VCF annotation standard, Jannovar also implements the ``--no-3-prime-shifting`` option.
Using this switch suppresses this shifting and the variant will be kept as given in the VCF file.
Here is an example of using this command line option:

.. code-block:: console

    # java -jar jannovar-cli-0.14.jar annotate --no-3-prime-shifting \
        data/hg19_refseq.ser examples/small.vcf

The Show-All Option
-------------------

By default, Jannovar will only write out one most pathogenic variant as predicted.
You can use the ``--show-all``/``-a`` option to write out all functional annotations:

.. code-block:: console

    # java -jar jannovar-cli-0.14.jar annotate --show-all \
        data/hg19_refseq.ser examples/small.vcf

For example, the first line of ``small.jv.vcf`` will look as follows and contain multiple effects and HGVS annotations.

.. code-block:: text

    1   866511  rs60722469      C       CCCCT   258.62  PASS    ANN=CCCCT|5_prime_utr_variant|LOW|SAMD11|ENTREZ148398|transcript|uc031pjn.1|Coding|2/4|c.-129+795_-129+796insCCCT|p.%3D|275/18232|1/558|1/186||,CCCCT|5_prime_utr_variant|LOW|SAMD11|ENTREZ148398|transcript|uc031pjq.1|Coding|3/11|c.-126+42_-126+43insCCCT|p.%3D|326/18660|1/1443|1/481||,CCCCT|5_prime_utr_variant|LOW|SAMD11|ENTREZ148398|transcript|uc031pjr.1|Coding|3/10|c.-377+42_-377+43insCCCT|p.%3D|326/18660|1/1029|1/343||,CCCCT|5_prime_utr_variant|LOW|SAMD11|ENTREZ148398|transcript|uc031pjv.1|Coding|3/13|c.-336+42_-336+43insCCCT|p.%3D|326/18660|1/1491|1/497||,CCCCT|5_prime_utr_variant|LOW|SAMD11|ENTREZ148398|transcript|uc031pjy.1|Coding|2/12|c.-339+795_-339+796insCCCT|p.%3D|275/18660|1/1443|1/481||,CCCCT|5_prime_utr_variant|LOW|SAMD11|ENTREZ148398|transcript|uc031pka.1|Coding|3/9|c.-126+42_-126+43insCCCT|p.%3D|326/18660|1/1164|1/388||,CCCCT|5_prime_utr_variant|LOW|SAMD11|ENTREZ148398|transcript|uc031pkb.1|Coding|1/8|c.-58-4641_-58-4640insCCCT|p.%3D|93/18660|1/1356|1/452||,CCCCT|5_prime_utr_variant|LOW|SAMD11|ENTREZ148398|transcript|uc031pke.1|Coding|3/11|c.-129+42_-129+43insCCCT|p.%3D|326/18660|1/1491|1/497||,CCCCT|coding_transcript_intron_variant|LOW|SAMD11|ENTREZ148398|transcript|uc001abv.1|Coding|4/4|c.305+42_305+43insCCCT|p.%3D|366/10747|306/429|102/143||,CCCCT|coding_transcript_intron_variant|LOW|SAMD11|ENTREZ148398|transcript|uc001abw.1|Coding|4/13|c.305+42_305+43insCCCT|p.%3D|386/18841|306/2046|102/682||,CCCCT|coding_transcript_intron_variant|LOW|SAMD11|ENTREZ148398|transcript|uc001abx.2|Coding|3/12|c.305+42_305+43insCCCT|p.%3D|326/18660|306/1998|102/666||,CCCCT|coding_transcript_intron_variant|LOW|SAMD11|ENTREZ148398|transcript|uc031pjl.1|Coding|3/11|c.305+42_305+43insCCCT|p.%3D|326/18232|306/2100|102/700||,CCCCT|coding_transcript_intron_variant|LOW|SAMD11|ENTREZ148398|transcript|uc031pjm.1|Coding|3/12|c.305+42_305+43insCCCT|p.%3D|326/18232|306/2064|102/688||,CCCCT|coding_transcript_intron_variant|LOW|SAMD11|ENTREZ148398|transcript|uc031pjp.1|Coding|3/10|c.305+42_305+43insCCCT|p.%3D|326/18660|306/1719|102/573||,CCCCT|coding_transcript_intron_variant|LOW|SAMD11|ENTREZ148398|transcript|uc031pjs.1|Coding|3/11|c.305+42_305+43insCCCT|p.%3D|326/18660|306/2046|102/682||,CCCCT|coding_transcript_intron_variant|LOW|SAMD11|ENTREZ148398|transcript|uc031pjt.1|Coding|3/11|c.305+42_305+43insCCCT|p.%3D|326/18660|306/1860|102/620||,CCCCT|coding_transcript_intron_variant|LOW|SAMD11|ENTREZ148398|transcript|uc031pju.1|Coding|3/12|c.305+42_305+43insCCCT|p.%3D|326/18660|306/2049|102/683||,CCCCT|coding_transcript_intron_variant|LOW|SAMD11|ENTREZ148398|transcript|uc031pjx.1|Coding|3/12|c.305+42_305+43insCCCT|p.%3D|326/18660|306/2001|102/667||,CCCCT|coding_transcript_intron_variant|LOW|SAMD11|ENTREZ148398|transcript|uc031pkc.1|Coding|3/12|c.305+42_305+43insCCCT|p.%3D|326/18660|306/1968|102/656||,CCCCT|coding_transcript_intron_variant|LOW|SAMD11|ENTREZ148398|transcript|uc031pkg.1|Coding|3/10|c.305+42_305+43insCCCT|p.%3D|326/18660|306/1722|102/574||,CCCCT|coding_transcript_intron_variant|LOW|SAMD11|ENTREZ148398|transcript|uc031pkh.1|Coding|2/9|c.254+795_254+796insCCCT|p.%3D|275/18660|255/1671|85/557||,CCCCT|coding_transcript_intron_variant|LOW|SAMD11|ENTREZ148398|transcript|uc031pki.1|Coding|3/7|c.305+42_305+43insCCCT|p.%3D|326/18660|306/1188|102/396||,CCCCT|coding_transcript_intron_variant|LOW|SAMD11|ENTREZ148398|transcript|uc031pkj.1|Coding|3/7|c.305+42_305+43insCCCT|p.%3D|326/18660|306/1191|102/397||,CCCCT|coding_transcript_intron_variant|LOW|SAMD11|ENTREZ148398|transcript|uc031pkm.1|Coding|3/11|c.305+42_305+43insCCCT|p.%3D|326/18660|306/1806|102/602||,CCCCT|non_coding_transcript_intron_variant|LOW|SAMD11|ENTREZ148398|transcript|uc031pjo.1|Noncoding|3/12|n.325+42_325+43insCCCT||326/18660||||,CCCCT|non_coding_transcript_intron_variant|LOW|SAMD11|ENTREZ148398|transcript|uc031pjw.1|Noncoding|3/11|n.325+42_325+43insCCCT||326/18660||||,CCCCT|non_coding_transcript_intron_variant|LOW|SAMD11|ENTREZ148398|transcript|uc031pjz.1|Noncoding|1/9|n.93-4641_93-4640insCCCT||93/18660||||,CCCCT|non_coding_transcript_intron_variant|LOW|SAMD11|ENTREZ148398|transcript|uc031pkd.1|Noncoding|3/12|n.325+42_325+43insCCCT||326/18660||||,CCCCT|non_coding_transcript_intron_variant|LOW|SAMD11|ENTREZ148398|transcript|uc031pkf.1|Noncoding|3/12|n.325+42_325+43insCCCT||326/18660||||,CCCCT|non_coding_transcript_intron_variant|LOW|SAMD11|ENTREZ148398|transcript|uc031pkk.1|Noncoding|3/11|n.325+42_325+43insCCCT||326/18660||||,CCCCT|non_coding_transcript_intron_variant|LOW|SAMD11|ENTREZ148398|transcript|uc031pkl.1|Noncoding|3/11|n.325+42_325+43insCCCT||326/18660||||,CCCCT|non_coding_transcript_intron_variant|LOW|SAMD11|ENTREZ148398|transcript|uc031pkn.1|Noncoding|3/12|n.325+42_325+43insCCCT||326/18660||||        GT:AD:DP:GQ:PL  1/1:6,5:11:14.79:300,15,0
    

.. TODO: describe Jannovar format
