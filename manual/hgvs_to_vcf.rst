.. _hgvs_to_vcf:

Convert HGVS to VCF
====================

Because Jannovar uses `HGVS <http://varnomen.hgvs.org/>`_ to describe the variant change in a transcript it is also possible to use HGVS strings and get the genomic position. In Jannovar there is a command line interface to decode a list of HGVS notations into a VCF file.

This is done using the ``hgvs-to-vcf`` command.
You pass the path to an annotation database that you use in you HGVS code and a file where each line is exactly one HGVS notation. In addition the indexed reference fasta file with a dictionary is needed. The resulting file is a fully supported VCF file. 

For example, for converting the ``small_hgvs.lst`` file (see `small_hgvs.lst <https://github.com/charite/jannovar/blob/master/examples/small_hgvs.lst>`_) in the ``examples`` directory:

.. parsed-literal::
    # java -jar jannovar-cli-\ |version|\ .jar hgvs-to-vcf -d data/hg19_refseq.ser -i examples/small_hgvs.lst -o examples/small_hgvs.vcf -r hg19.fa
    [...]
    # ls examples/small_hgvs.vcf
    small_hgvs.vcf

The file ``examples/small_hgvs.vcf`` will look as follows.

.. code-block:: text

	##fileformat=VCFv4.2
	##ALT=<ID=ERROR,Description="Error in conversion">
	##FILTER=<ID=PARSE_ERROR,Description="Problem in parsing original HGVS variant string, written out as variant at 1:g.1N>N">
	##INFO=<ID=ERROR_MESSAGE,Number=1,Type=String,Description="Error message">
	##INFO=<ID=ORIG_VAR,Number=1,Type=String,Description="Original HGVS variant string from input file to hgvs-to-vcf">
	##contig=<ID=1,length=249250621>
	#CHROM	POS	ID	REF	ALT	QUAL	FILTER	INFO
	1	866512	.	CC	CCCCCT	.	.	.
	1	879317	.	C	T	.	.	.
