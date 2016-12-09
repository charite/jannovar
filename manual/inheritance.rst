.. _inheritance:

======================
Inheritance Annotation
======================

The command ``jannovar annotate-vcf`` can also be used for annotating variants with compatible modes of inheritance.
For this, you have to specify a `pedigree file <http://pngu.mgh.harvard.edu/~purcell/plink/data.shtml#ped>`_.

In short, these are TSV files with 6 columns.
Each line describes one individual.

1. Pedigree/family name (only individuals of the first occuring family name are interpreted).
2. Name of the individual.
3. Name of the father, ``0`` for "not in pedigree" (for founders)
4. Name of the mother, ``0`` for "not in pedigree" (for founders)
5. Sex of the individual: ``1`` for male, ``2`` for female, ``0`` for other/unknown
6. Disease status: ``1`` for unaffected, ``2`` for affected, 0 for unknown

-------------------
Used Pedigree Files
-------------------

The file ``ar.ped`` contains the following pedigree which matches autosomal recessive inheritance.
Note that the index could also have a de novo mutation (which is flagged as autosomal dominant by Jannovar).

.. code-block:: text

    FAM	index	father	mother	1	2
    FAM	father	0	0	1	1
    FAM	mother	0	0	2	1

The file ``ad.ped`` contains the following pedigree which matches autosomal dominant inheritance.

.. code-block:: text

    FAM	index	father	mother	1	2
    FAM	father	0	0	1	2
    FAM	mother	0	0	2	1

-------------
Used VCF File
-------------

The flie ``small.vcf`` contains the following variant file.

.. code-block:: text

    ##fileformat=VCFv4.1
    ##contig=<ID=1,length=249250621>
    ##INFO=<ID=INHERITANCE,Number=.,Type=String,Description="Mode of Inheritance">
    #CHROM	POS	ID	REF	ALT	QUAL	FILTER	INFO	FORMAT	index	father	mother
    1	866511	.	C	CCCCT	.	.	.	GT	1/1	0/1	0/1
    1	879317	.	C	T	.	.	.	GT	0/1	0/1	0/0
    1	879318	.	G	T	.	.	.	GT	0/1	0/0	0/0
    1	879482	.	G	C	.	.	.	GT	0/1	0/1	0/0

Note that all variants lie within the same gene, as shown in the following, annotated version of small.vcf.

.. code-block:: text

    ##fileformat=VCFv4.2
    ##INFO=<ID=ANN,Number=1,Type=String,Description="Functional annotations:'Allele|Annotation|Annotation_Impact|Gene_Name|Gene_ID|Feature_Type|Feature_ID|Transcript_BioType|Rank|HGVS.c|HGVS.p|cDNA.pos / cDNA.length|CDS.pos / CDS.length|AA.pos / AA.length|Distance|ERRORS / WARNINGS / INFO'">
    ##INFO=<ID=INHERITANCE,Number=.,Type=String,Description="Mode of Inheritance">
    ##contig=<ID=1,length=249250621>
    ##jannovarCommand=annotate-vcf -d data/hg19_refseq.ser -i small.vcf -o small.jv.vcf
    ##jannovarVersion=0.17
    #CHROM	POS	ID	REF	ALT	QUAL	FILTER	INFO	FORMAT	index	father	mother
    1	866511	.	C	CCCCT	.	.	ANN=CCCCT|coding_transcript_intron_variant|LOW|SAMD11|148398|transcript|NM_152486.2|Coding|4/13|c.305+42_305+43insCCCT|p.(%3D)|386/18841|306/2046|102/682||	GT	1/1	0/10/1
    1	879317	.	C	T	.	.	ANN=T|missense_variant|MODERATE|SAMD11|148398|transcript|XM_005244727.1|Coding|9/9|c.799C>T|p.(Arg267Cys)|1155/19962|799/1188|267/396||	GT	0/1	0/1	0/0
    1	879318	.	G	T	.	.	ANN=T|missense_variant|MODERATE|SAMD11|148398|transcript|NM_152486.2|Coding|14/14|c.1831G>T|p.(Val611Leu)|1911/18841|1831/2046|611/682||	GT	0/1	0/0	0/0
    1	879482	.	G	C	.	.	ANN=C|missense_variant|MODERATE|SAMD11|148398|transcript|XM_005244727.1|Coding|9/9|c.964G>C|p.(Asp322His)|1320/19962|964/1188|322/396||	GT	0/1	0/0	0/1

Also note that for annotating for compatibility with inheritance, **all** variants assigned to a gene will be used.
This includes deep intronic as well as upstream/downstream (up to 5kbp) variants.
Thus, it is a good idea to first filter out low-quality and non-coding variants before annotating compatible modes of inheritance.
It remains for future work to include a quality/variant type filter for the pedigree-based filtration.

----------------------
Annotating AR Variants
----------------------

The following shows the annotation result with the AR pedigree.
The molecular impact annotation ``ANN`` is suppressed for brevity.

.. code-block:: console
    :emphasize-lines: 12-15

    $ java -jar jannovar-cli.jar annotate-vcf \
        -d data/hg19_refseq.ser -i small.vcf \
        -o small.ar.vcf --pedigree-file ar.ped
    $ cat small.ar.vcf
    ##fileformat=VCFv4.2
    ##INFO=<ID=ANN,Number=1,Type=String,Description="Functional annotations:'Allele|Annotation|Annotation_Impact|Gene_Name|Gene_ID|Feature_Type|Feature_ID|Transcript_BioType|Rank|HGVS.c|HGVS.p|cDNA.pos / cDNA.length|CDS.pos / CDS.length|AA.pos / AA.length|Distance|ERRORS / WARNINGS / INFO'">
    ##INFO=<ID=INHERITANCE,Number=.,Type=String,Description="Mode of Inheritance">
    ##contig=<ID=1,length=249250621>
    ##jannovarCommand=annotate-vcf -d data/hg19_refseq.ser -i small.vcf -o small.ar.vcf --pedigree-file ar.ped
    ##jannovarVersion=0.17
    #CHROM	POS	ID	REF	ALT	QUAL	FILTER	INFO	FORMAT	index	father	mother
    1	866511	.	C	CCCCT	.	.	ANN=[...];INHERITANCE=AR	GT	1/1	0/1	0/1
    1	879317	.	C	T	.	.	ANN=[...];INHERITANCE=AR	GT	0/1	0/1	0/0
    1	879318	.	G	T	.	.	ANN=[...];INHERITANCE=AD	GT	0/1	0/0	0/0
    1	879482	.	G	C	.	.	ANN=[...];INHERITANCE=AR	GT	0/1	0/0	0/1

The variant at

- ``chr1:866511`` is annotated as compatible with autosomal recessive as it is a "classic" autosomal recessive variant.
- ``chr1:879317`` is annotated as compatible with autosomal recessive as together with the variant at ``chr1:879482``, it matches the composite autosomal recessive mode of inheritance.
- ``chr1:879318`` is annotated as compatible with autosomal dominant as it could be a de novo variant that is autosomal dominant.

----------------------
Annotating AD Variants
----------------------

.. code-block:: console
    :emphasize-lines: 12,14

    $ $ java -jar jannovar-cli.jar annotate-vcf \
        -d data/hg19_refseq.ser -i small.vcf \
        -o small.ad.vcf --pedigree-file ad.ped
    $ cat small.ad.vcf
    ##fileformat=VCFv4.2
    ##INFO=<ID=ANN,Number=1,Type=String,Description="Functional annotations:'Allele|Annotation|Annotation_Impact|Gene_Name|Gene_ID|Feature_Type|Feature_ID|Transcript_BioType|Rank|HGVS.c|HGVS.p|cDNA.pos / cDNA.length|CDS.pos / CDS.length|AA.pos / AA.length|Distance|ERRORS / WARNINGS / INFO'">
    ##INFO=<ID=INHERITANCE,Number=.,Type=String,Description="Mode of Inheritance">
    ##contig=<ID=1,length=249250621>
    ##jannovarCommand=annotate-vcf -d data/hg19_refseq.ser -i small.vcf -o small.ad.vcf --pedigree-file ad.ped
    ##jannovarVersion=0.17
    #CHROM	POS	ID	REF	ALT	QUAL	FILTER	INFO	FORMAT	index	father	mother
    1	866511	.	C	CCCCT	.	.	ANN=[...]	GT	1/1	0/1	0/1
    1	879317	.	C	T	.	.	ANN=[...];INHERITANCE=AD	GT	0/1	0/1	0/0
    1	879318	.	G	T	.	.	ANN=[...]	GT	0/1	0/0	0/0
    1	879482	.	G	C	.	.	ANN=[...];INHERITANCE=AD	GT	0/1	0/1	0/0

The variants at ``chr1:879317`` and ``chr1:879482`` match the autosomal dominant mode of inheritance from the father.
The remaining variants do not match this mode of inheritance.


-------------------------------
No-calls and Mixed genotypes
-------------------------------

We implemented the filter that we might loose specificity but not some sensitibvity.Therfore a genotype call of ``./1`` or ``1/.`` can be ``HET`` or ``HOM_ALT``. ``0/.`` or ``./0`` are ``HET`` or ``HOM_REF``. A no-call of ``./.`` is ``NO_CALL`` and will be used only as a wildcard in multi-vcfs but at least one called correct genotype must be observed. For more information see :ref:`ped_filters`.

