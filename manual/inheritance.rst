.. _inheritance:

======================
Inheritance Annotation
======================

.. note:

    Caution: this is work in progress

The command ``jannovar annotate-vcf`` can also be used for annotating variants with compatible modes of inheritance.

http://pngu.mgh.harvard.edu/~purcell/plink/data.shtml#ped

``ar.ped``

.. code-block:: text

    FAM	index	father	mother	1	2
    FAM	father	0	0	1	1
    FAM	mother	0	0	2	1

``ad.ped``

.. code-block:: text

    FAM	index	father	mother	1	2
    FAM	father	0	0	1	2
    FAM	mother	0	0	2	1

``small.vcf``

.. code-block:: text

    ##fileformat=VCFv4.1
    ##contig=<ID=1,length=249250621>
    ##INFO=<ID=INHERITANCE,Number=.,Type=String,Description="Mode of Inheritance">
    #CHROM	POS	ID	REF	ALT	QUAL	FILTER	INFO	FORMAT	index	father	mother
    1	866511	.	C	CCCCT	.	.	.	GT	1/1	0/0	0/1
    1	879317	.	C	T	.	.	.	GT	0/1	0/1	0/0
    1	879318	.	G	T	.	.	.	GT	0/1	0/0	0/0
    1	879482	.	G	C	.	.	.	GT	0/1	0/1	0/0

.. code-block:: console

    $ java -jar ../jannovar-cli/target/jannovar-cli-0.18-SNAPSHOT.jar annotate-vcf -d data/hg19_refseq.ser -i small.vcf -o small.ar.vcf --pedigree-file ar.ped
    $ cat small.ar.vcf
    ##fileformat=VCFv4.2
    ##INFO=<ID=ANN,Number=1,Type=String,Description="Functional annotations:'Allele|Annotation|Annotation_Impact|Gene_Name|Gene_ID|Feature_Type|Feature_ID|Transcript_BioType|Rank|HGVS.c|HGVS.p|cDNA.pos / cDNA.length|CDS.pos / CDS.length|AA.pos / AA.length|Distance|ERRORS / WARNINGS / INFO'">
    ##INFO=<ID=INHERITANCE,Number=.,Type=String,Description="Mode of Inheritance">
    ##contig=<ID=1,length=249250621>
    ##jannovarCommand=annotate-vcf -d data/hg19_refseq.ser -i small.vcf -o small.ar.vcf --pedigree-file ar.ped
    ##jannovarVersion=0.17
    #CHROM	POS	ID	REF	ALT	QUAL	FILTER	INFO	FORMAT	index	father	mother
    1	866511	.	C	CCCCT	.	.	ANN=CCCCT|coding_transcript_intron_variant|LOW|SAMD11|148398|transcript|NM_152486.2|Coding|4/13|c.305+42_305+43insCCCT|p.(%3D)|386/18841|306/2046|102/682||	GT	1/1	0/0	0/1
    1	879317	.	C	T	.	.	ANN=T|missense_variant|MODERATE|SAMD11|148398|transcript|XM_005244727.1|Coding|9/9|c.799C>T|p.(Arg267Cys)|1155/19962|799/1188|267/396||	GT	0/1	0/1	0/0
    1	879318	.	G	T	.	.	ANN=T|missense_variant|MODERATE|SAMD11|148398|transcript|NM_152486.2|Coding|14/14|c.1831G>T|p.(Val611Leu)|1911/18841|1831/2046|611/682||;INHERITANCE=AD	GT	0/1	0/0	0/0
    1	879482	.	G	C	.	.	ANN=C|missense_variant|MODERATE|SAMD11|148398|transcript|XM_005244727.1|Coding|9/9|c.964G>C|p.(Asp322His)|1320/19962|964/1188|322/396||	GT	0/1	0/1	0/0

.. code-block:: console

    $ $ java -jar ../jannovar-cli/target/jannovar-cli-0.18-SNAPSHOT.jar annotate-vcf -d data/hg19_refseq.ser -i small.vcf -o small.ad.vcf --pedigree-file ad.ped
    $ cat small.ad.vcf
    ##fileformat=VCFv4.2
    ##INFO=<ID=ANN,Number=1,Type=String,Description="Functional annotations:'Allele|Annotation|Annotation_Impact|Gene_Name|Gene_ID|Feature_Type|Feature_ID|Transcript_BioType|Rank|HGVS.c|HGVS.p|cDNA.pos / cDNA.length|CDS.pos / CDS.length|AA.pos / AA.length|Distance|ERRORS / WARNINGS / INFO'">
    ##INFO=<ID=INHERITANCE,Number=.,Type=String,Description="Mode of Inheritance">
    ##contig=<ID=1,length=249250621>
    ##jannovarCommand=annotate-vcf -d data/hg19_refseq.ser -i small.vcf -o small.ad.vcf --pedigree-file ad.ped
    ##jannovarVersion=0.17
    #CHROM	POS	ID	REF	ALT	QUAL	FILTER	INFO	FORMAT	index	father	mother
    1	866511	.	C	CCCCT	.	.	ANN=CCCCT|coding_transcript_intron_variant|LOW|SAMD11|148398|transcript|NM_152486.2|Coding|4/13|c.305+42_305+43insCCCT|p.(%3D)|386/18841|306/2046|102/682||	GT	1/1	0/0	0/1
    1	879317	.	C	T	.	.	ANN=T|missense_variant|MODERATE|SAMD11|148398|transcript|XM_005244727.1|Coding|9/9|c.799C>T|p.(Arg267Cys)|1155/19962|799/1188|267/396||;INHERITANCE=AD	GT	0/1	0/1	0/0
    1	879318	.	G	T	.	.	ANN=T|missense_variant|MODERATE|SAMD11|148398|transcript|NM_152486.2|Coding|14/14|c.1831G>T|p.(Val611Leu)|1911/18841|1831/2046|611/682||	GT	0/1	0/0	0/0
    1	879482	.	G	C	.	.	ANN=C|missense_variant|MODERATE|SAMD11|148398|transcript|XM_005244727.1|Coding|9/9|c.964G>C|p.(Asp322His)|1320/19962|964/1188|322/396||;INHERITANCE=AD	GT	0/1	0/1	0/0
