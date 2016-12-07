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

The command ``--exac-vcf <EXAC_VCF>`` will add allele frequencies from the ExAC database to the variants. Please download the actual ExAC database as VCF (tested with 0.3.1) from their `website <http://exac.broadinstitute.org/>`_. In addition you will need the actual reference file because we do a local normalization of the variants; command ``--ref-fasta <REF_FASTA>``. With ``--exac-prefix <EXAC_PREFIX>`` you can define the info token used for the annotation. The default is ``EXAC_``.

.. note::

	TODO: Add descriptions how about finding the max frequency and finishing the example

Example:

.. parsed-literal::

	$ java -jar jannovar-cli-\ |version|\ .jar annotate-vcf \\
	-d data/hg19_refseq.ser -i examples/small.vcf -o examples/small.jv.vcf \\
	--exac-vcf --ref-fasta


dnSBP
----------

The command ``--dbsnp-vcf <DBSNP_VCF>`` will add rsIDs from dbSNP to the variants in the info column and, if the genotype is identical, the ID column will be annotated with te rsID. Please download the actual dbSNP database as VCF for your genome release from the NCBI website. In addition you will need the actual reference file because we do a local normalization of the variants; command ``--ref-fasta <REF_FASTA>``. With ``--dbsnp-prefix <DBSNP_PREFIX>`` you can define the info token used for the annotation of the rsID. The default is ``DBSNP_``.

Example:

.. parsed-literal::

	$ java -jar jannovar-cli-\ |version|\ .jar annotate-vcf \\
	-d data/hg19_refseq.ser -i examples/small.vcf -o examples/small.jv.vcf \\
	--dbsnp-vcf --ref-fasta




UK10K
----------

The command ``--uk10k-vcf <UK10K_VCF>`` will add allele frequencies from the UK10K project to the variants. Please download the actual UK10K samples from their website (you have to register). In addition you will need the actual reference file because we do a local normalization of the variants; command ``--ref-fasta <REF_FASTA>``. With ``--uk10k-prefix <UK10K_PREFIX>`` you can define the info token used for the annotation. The default is ``UK10K_``.

.. note::

	TODO: Add descriptions how about finding the max frequency and finishing the example

Example:

.. parsed-literal::

	$ java -jar jannovar-cli-\ |version|\ .jar annotate-vcf \\
	-d data/hg19_refseq.ser -i examples/small.vcf -o examples/small.jv.vcf \\
	--uk10k-vcf --ref-fasta
