.. _hgvs_to_vcf:

Getting chromosomal positions of HGVS description
====================

Because Jannovar uses `HGVS <http://varnomen.hgvs.org/>`_ to describe the variant change in atranscript it is also possible to use HGVS strings and get the genomic position. In Jannovar there is a command line interface to decode a list of HGVS notations into a VCF file.

This is done using the ``hgvs-to-vcf`` command.
You pass the path to an annotation database that you use in you HGVS code and a file where each line is exactly one HGVS notation. The resulting file is a fully siupported VCF file. 

For example, for converting the ``small_hgvs.lst`` file (see `small_hgvs.lst <https://github.com/charite/jannovar/blob/master/examples/small_hgvs.lst>`_) in the ``examples`` directory:

.. parsed-literal::
    # java -jar jannovar-cli-\ |version|\ .jar annotate-vcf -d data/hg19_refseq.ser -i examples/small.vcf -o examples/small.jv.vcf
    [...]
    # ls examples/small.jv.vcf
    small.jv.vcf

The first three variant lines of ``examples/small.jv.vcf`` will look as follows.

.. code-block:: text

	1	866511	rs60722469	C	CCCCT	258.62	.	ANN=CCCCT|coding_transcript_intron_variant|LOW|SAMD11|148398|transcript|NM_152486.2|Coding|4/13|c.305+42_305+43insCCCT|p.(%3D)|386/18841|306/2046|102/682||	GT:AD:DP:GQ:PL	1/1:6,5:11:14.79:300,15,0
	1	879317	rs7523549	C	T	150.77	.	ANN=T|missense_variant|MODERATE|SAMD11|148398|transcript|XM_005244727.1|Coding|9/9|c.799C>T|p.(Arg267Cys)|1155/19962|799/1188|267/396||	GT:AD:DP:GQ:PL	0/1:14,7:21:99:181,0,367
	1	879482	.	G	C	484.52	.	ANN=C|missense_variant|MODERATE|SAMD11|148398|transcript|XM_005244727.1|Coding|9/9|c.964G>C|p.(Asp322His)|1320/19962|964/1188|322/396||	GT:AD:DP:GQ:PL	0/1:28,20:48:99:515,0,794
