.. _filter:

=====================
Filtering Annotations
=====================

Jannovar is foremost a program for **annotating** variants.
Its features include the annotation based on predicted molecular impact, but also for compatibility with different inheritance models.

These annotations can in turn be used for **filtering** variants, i.e., including or excluding variants based on some criteria.
The functionality for filtration is not included in Jannovar itself but can easily be performed with `bcftools <https://samtools.github.io/bcftools/bcftools.html>`_ (or even ``grep`` if you are brave).

--------------------------------
Variant Filtration with BCFtools
--------------------------------

Given an annotated VCF file, we can easily use the ``bcftools view`` command for filtering the variant to

- contain only variants with a given annotation, or
- contain no variant with a given annotation.

We will use the following annotated VCF file ``small.vcf`` that we will filter.::

    ##INFO=<ID=INHERITANCE,Number=.,Type=String,Description="Mode of Inheritance">
    ##contig=<ID=1,length=249250621>
    ##jannovarCommand=annotate-v -d data/hg19_refseq.ser -i small.vcf -o small.jv.vcf
    ##jannovarVersion=0.17
    #CHROM	POS	ID	REF	ALT	QUAL	FILTER	INFO	FORMAT	individual
    1	866511	rs60722469	C	CCCCT	258.62	.	ANN=CCCCT|coding_transcript_intron_variant|LOW|SAMD11|148398|transcript|NM_152486.2|Coding|4/13|c.305+42_305+43insCCCT|p.(%3D)|386/18841|306/2046|102/682||;INHERITANCE=AR	GT:AD:DP:GQ:PL	1/1:6,5:11:14.79:300,15,0
    1	879317	rs7523549	C	T	150.77	.	ANN=T|missense_variant|MODERATE|SAMD11|148398|transcript|XM_005244727.1|Coding|9/9|c.799C>T|p.(Arg267Cys)|1155/19962|799/1188|267/396||	GT:AD:DP:GQ:PL	0/1:14,7:21:99:181,0,367
    1	879482	.	G	C	484.52	.	ANN=C|missense_variant|MODERATE|SAMD11|148398|transcript|XM_005244727.1|Coding|9/9|c.964G>C|p.(Asp322His)|1320/19962|964/1188|322/396||	GT:AD:DP:GQ:PL	0/1:28,20:48:99:515,0,794

For example, we can limit the variants to those compatible with autosomal recessive mode of inheritance.

.. code-block:: console

    $ bcftools view -i 'INHERITANCE[*] = "AD"' small.vcf
    ##fileformat=VCFv4.1
    ##FILTER=<ID=PASS,Description="All filters passed">
    ##contig=<ID=1,length=249250621>
    ##INFO=<ID=INHERITANCE,Number=.,Type=String,Description="Mode of Inheritance">
    ##bcftools_viewVersion=1.2+htslib-1.2.1
    ##bcftools_viewCommand=view -i 'INHERITANCE[*] = "AD"' small.vcf
    #CHROM	POS	ID	REF	ALT	QUAL	FILTER	INFO	FORMAT	individual
    1	866511	rs60722469	C	CCCCT	258.62	.	ANN=CCCCT|coding_transcript_intron_variant|LOW|SAMD11|148398|transcript|NM_152486.2|Coding|4/13|c.305+42_305+43insCCCT|p.(%3D)|386/18841|306/2046|102/682||;INHERITANCE=AR	GT:AD:DP:GQ:PL	1/1:6,5:11:14.79:300,15,0

Similarly, we can remove all files compatible with this mode of inheritance.

.. code-block:: console

    $ bcftools view -e 'INHERITANCE[*] = "AD"' small.vcf
    ##fileformat=VCFv4.1
    ##FILTER=<ID=PASS,Description="All filters passed">
    ##contig=<ID=1,length=249250621>
    ##INFO=<ID=INHERITANCE,Number=.,Type=String,Description="Mode of Inheritance">
    ##bcftools_viewVersion=1.2+htslib-1.2.1
    ##bcftools_viewCommand=view -i 'INHERITANCE[*] = "AD"' small.vcf
    #CHROM	POS	ID	REF	ALT	QUAL	FILTER	INFO	FORMAT	individual
    1	879317	rs7523549	C	T	150.77	.	ANN=T|missense_variant|MODERATE|SAMD11|148398|transcript|XM_005244727.1|Coding|9/9|c.799C>T|p.(Arg267Cys)|1155/19962|799/1188|267/396||	GT:AD:DP:GQ:PL	0/1:14,7:21:99:181,0,367
    1	879482	.	G	C	484.52	.	ANN=C|missense_variant|MODERATE|SAMD11|148398|transcript|XM_005244727.1|Coding|9/9|c.964G>C|p.(Asp322His)|1320/19962|964/1188|322/396||	GT:AD:DP:GQ:PL	0/1:28,20:48:99:515,0,794

The following shows how to limit the variants to those having a missense functional impact.

.. code-block:: console

    $ bcftools view -i 'ANN ~ "missense"' small.vcf
    ##fileformat=VCFv4.2
    ##FILTER=<ID=PASS,Description="All filters passed">
    ##INFO=<ID=ANN,Number=1,Type=String,Description="Functional annotations:'Allele|Annotation|Annotation_Impact|Gene_Name|Gene_ID|Feature_Type|Feature_ID|Transcript_BioType|Rank|HGVS.c|HGVS.p|cDNA.pos / cDNA.length|CDS.pos / CDS.length|AA.pos / AA.length|Distance|ERRORS / WARNINGS / INFO'">
    ##INFO=<ID=INHERITANCE,Number=.,Type=String,Description="Mode of Inheritance">
    ##contig=<ID=1,length=249250621>
    ##jannovarCommand=annotate-v -d data/hg19_refseq.ser -i small.vcf -o small.jv.vcf
    ##jannovarVersion=0.17
    ##bcftools_viewVersion=1.2+htslib-1.2.1
    ##bcftools_viewCommand=view -i 'ANN ~ "missense"' small.jv.vcf
    #CHROM	POS	ID	REF	ALT	QUAL	FILTER	INFO	FORMAT	individual
    1	879317	rs7523549	C	T	150.77	.	ANN=T|missense_variant|MODERATE|SAMD11|148398|transcript|XM_005244727.1|Coding|9/9|c.799C>T|p.(Arg267Cys)|1155/19962|799/1188|267/396||	GT:AD:DP:GQ:PL	0/1:14,7:21:99:181,0,367
    1	879482	.	G	C	484.52	.	ANN=C|missense_variant|MODERATE|SAMD11|148398|transcript|XM_005244727.1|Coding|9/9|c.964G>C|p.(Asp322His)|1320/19962|964/1188|322/396||	GT:AD:DP:GQ:PL	0/1:28,20:48:99:515,0,794

Similarly, we could use ``-e`` instead of ``-i`` for inverting the selection.
