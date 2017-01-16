.. _annotate_vcf_databases:

Annotation Of Other Databases
==============================

In addition to the transcript variant effect annotation Jannovar can annotate some additional databases like ExAC or dbSNP. For these extra annotation the ``annotate-vcf`` command is used with some additional options.


Exact vs position wise annotation
--------------------------------------

Different programs that annotate allele frequency etc. use different strategies to annotate variants. Mostly only position wise is annotated and the exact genotype is discarded. If annotation is used for filtering this might be problematic because a rare genotype is filtered out because of a common genotype at the same position. Therefor Jannovar has enables both: position and genotype wise annotation.

If the annotation matches the position of the variant an additional string ``OVL_`` is added to the token in the INFO column. If the genotype matches this identifier is missing.

ExAC
----------

The command ``--exac-vcf <EXAC_VCF>`` will add allele frequencies from the ExAC database to the variants. Please download the current veriosn of the ExAC database as VCF (tested with 0.3.1) from their `website <http://exac.broadinstitute.org/>`_. In addition you will need the actual reference file because we do a local normalization of the variants; command ``--ref-fasta <REF_FASTA>`` (a FASTA index file should be present in the same directory; if needed, one can be created with samtools using hte ``faidx`` command). With ``--exac-prefix <EXAC_PREFIX>`` you can define the info token used for the annotation. The default is ``EXAC_``.

.. note::

	TODO: Add descriptions how about finding the max frequency and finishing the example

Example:

.. parsed-literal::

	$ java -jar jannovar-cli-\ |version|\ .jar annotate-vcf \\
	-d data/hg19_refseq.ser -i examples/small.vcf -o examples/small.jv.vcf \\
	--exac-vcf ExAC.r0.3.1.sites.vep.vcf.gz --ref-fasta hg19.fa

For example, the second line (first coding variant) of ``small.jv.vcf`` will look as follows and contain the ExAC annotations.

.. code-block:: text

	1	879317	rs7523549	C	T	150.77	.	ANN=T|missense_variant|MODERATE|SAMD11|148398|transcript|XM_005244727.1|Coding|9/9|c.799C>T|p.(Arg267Cys)|1155/19962|799/1188|267/396||;EXAC_AC_AFR=1922;EXAC_AC_ALL=5591;EXAC_AC_AMR=684;EXAC_AC_EAS=556;EXAC_AC_FIN=215;EXAC_AC_NFE=1933;EXAC_AC_OTH=39;EXAC_AC_SAS=242;EXAC_AF_AFR=0.209;EXAC_AF_ALL=0.048;EXAC_AF_AMR=0.060;EXAC_AF_EAS=0.065;EXAC_AF_FIN=0.033;EXAC_AF_NFE=0.031;EXAC_AF_OTH=0.045;EXAC_AF_SAS=0.015;EXAC_AN_AFR=9190;EXAC_AN_ALL=116406;EXAC_AN_AMR=11472;EXAC_AN_EAS=8498;EXAC_AN_FIN=6536;EXAC_AN_NFE=63374;EXAC_AN_OTH=870;EXAC_AN_SAS=16466;EXAC_BEST_AC=1922;EXAC_BEST_AF=0.209;EXAC_OVL_AC_AFR=1922;EXAC_OVL_AC_ALL=5591;EXAC_OVL_AC_AMR=684;EXAC_OVL_AC_EAS=556;EXAC_OVL_AC_FIN=215;EXAC_OVL_AC_NFE=1933;EXAC_OVL_AC_OTH=39;EXAC_OVL_AC_SAS=242;EXAC_OVL_AF_AFR=0.209;EXAC_OVL_AF_ALL=0.048;EXAC_OVL_AF_AMR=0.060;EXAC_OVL_AF_EAS=0.065;EXAC_OVL_AF_FIN=0.033;EXAC_OVL_AF_NFE=0.031;EXAC_OVL_AF_OTH=0.045;EXAC_OVL_AF_SAS=0.015;EXAC_OVL_AN_AFR=9190;EXAC_OVL_AN_ALL=116406;EXAC_OVL_AN_AMR=11472;EXAC_OVL_AN_EAS=8498;EXAC_OVL_AN_FIN=6536;EXAC_OVL_AN_NFE=63374;EXAC_OVL_AN_OTH=870;EXAC_OVL_AN_SAS=16466;EXAC_OVL_BEST_AC=1922;EXAC_OVL_BEST_AF=0.209	GT:AD:DP:GQ:PL	0/1:14,7:21:99:181,0,367


dnSBP
----------

The command ``--dbsnp-vcf <DBSNP_VCF>`` will add rsIDs from dbSNP to the variants in the info column and, if the genotype is identical, the ID column will be annotated with te rsID. Please download the latest version of the dbSNP database in VCF format for your genome release from the NCBI website. In addition you will need the actual reference file because we do a local normalization of the variants; command ``--ref-fasta <REF_FASTA>``. With ``--dbsnp-prefix <DBSNP_PREFIX>`` you can define the info token used for the annotation of the rsID. The default is ``DBSNP_``.

Example:

.. parsed-literal::

	$ java -jar jannovar-cli-\ |version|\ .jar annotate-vcf \\
	-d data/hg19_refseq.ser -i examples/small.vcf -o examples/small.jv.vcf \\
	--dbsnp-vcf 00-All.vcf.gz --ref-fasta hg19.fa

For example, the first line of ``small.jv.vcf`` will look as follows and contain the dnSBNP annotation `rs375757231` as exact match.

.. code-block:: text

	1	866511	rs60722469;rs375757231	C	CCCCT	258.62	.	ANN=CCCCT|coding_transcript_intron_variant|LOW|SAMD11|148398|transcript|NM_152486.2|Coding|4/13|c.305+42_305+43insCCCT|p.(%3D)|386/18841|306/2046|102/682||;DBSNP_CAF=0.149;DBSNP_COMMON=1;DBSNP_G5=1;DBSNP_IDS=rs375757231;DBSNP_OVL_CAF=0.149;DBSNP_OVL_COMMON=1;DBSNP_OVL_G5=1;DBSNP_OVL_IDS=rs375757231	GT:AD:DP:GQ:PL	1/1:6,5:11:14.79:300,15,0




UK10K
----------

The command ``--uk10k-vcf <UK10K_VCF>`` will add allele frequencies from the UK10K project to the variants. Please download the actual UK10K samples from their website (you have to register). In addition you will need the actual reference file because we do a local normalization of the variants; command ``--ref-fasta <REF_FASTA>``. With ``--uk10k-prefix <UK10K_PREFIX>`` you can define the info token used for the annotation. The default is ``UK10K_``.

.. note::

	TODO: Add descriptions how about finding the max frequency and finishing the example

Example:

.. parsed-literal::

	$ java -jar jannovar-cli-\ |version|\ .jar annotate-vcf \\
	-d data/hg19_refseq.ser -i examples/small.vcf -o examples/small.jv.vcf \\
	--uk10k-vcf UK10K_COHORT.20160215.sites.vcf.gz --ref-fasta hg19.fa


For example, the first line of ``small.jv.vcf`` will look as follows and contain the UK10K annotations.

.. code-block:: text

	1	866511	rs60722469	C	CCCCT	258.62	.	ANN=CCCCT|coding_transcript_intron_variant|LOW|SAMD11|148398|transcript|NM_152486.2|Coding|4/13|c.305+42_305+43insCCCT|p.(%3D)|386/18841|306/2046|102/682||;UK10K_AC=5708;UK10K_AF=0.755;UK10K_AN=7562;UK10K_OVL_AC=5708;UK10K_OVL_AF=0.755;UK10K_OVL_AN=7562	GT:AD:DP:GQ:PL	1/1:6,5:11:14.79:300,15,0
