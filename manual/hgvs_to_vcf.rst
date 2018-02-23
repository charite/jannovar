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
    ##contig=<ID=2,length=243199373>
    ##contig=<ID=3,length=198022430>
    ##contig=<ID=4,length=191154276>
    ##contig=<ID=5,length=180915260>
    ##contig=<ID=6,length=171115067>
    ##contig=<ID=7,length=159138663>
    ##contig=<ID=8,length=146364022>
    ##contig=<ID=9,length=141213431>
    ##contig=<ID=10,length=135534747>
    ##contig=<ID=11,length=135006516>
    ##contig=<ID=12,length=133851895>
    ##contig=<ID=13,length=115169878>
    ##contig=<ID=14,length=107349540>
    ##contig=<ID=15,length=102531392>
    ##contig=<ID=16,length=90354753>
    ##contig=<ID=17,length=81195210>
    ##contig=<ID=18,length=78077248>
    ##contig=<ID=19,length=59128983>
    ##contig=<ID=20,length=63025520>
    ##contig=<ID=21,length=48129895>
    ##contig=<ID=22,length=51304566>
    ##contig=<ID=X,length=155270560>
    ##contig=<ID=Y,length=59373566>
    ##contig=<ID=MT,length=16569>
    ##contig=<ID=GL000207.1,length=4262>
    ##contig=<ID=GL000226.1,length=15008>
    ##contig=<ID=GL000229.1,length=19913>
    ##contig=<ID=GL000231.1,length=27386>
    ##contig=<ID=GL000210.1,length=27682>
    ##contig=<ID=GL000239.1,length=33824>
    ##contig=<ID=GL000235.1,length=34474>
    ##contig=<ID=GL000201.1,length=36148>
    ##contig=<ID=GL000247.1,length=36422>
    ##contig=<ID=GL000245.1,length=36651>
    ##contig=<ID=GL000197.1,length=37175>
    ##contig=<ID=GL000203.1,length=37498>
    ##contig=<ID=GL000246.1,length=38154>
    ##contig=<ID=GL000249.1,length=38502>
    ##contig=<ID=GL000196.1,length=38914>
    ##contig=<ID=GL000248.1,length=39786>
    ##contig=<ID=GL000244.1,length=39929>
    ##contig=<ID=GL000238.1,length=39939>
    ##contig=<ID=GL000202.1,length=40103>
    ##contig=<ID=GL000234.1,length=40531>
    ##contig=<ID=GL000232.1,length=40652>
    ##contig=<ID=GL000206.1,length=41001>
    ##contig=<ID=GL000240.1,length=41933>
    ##contig=<ID=GL000236.1,length=41934>
    ##contig=<ID=GL000241.1,length=42152>
    ##contig=<ID=GL000243.1,length=43341>
    ##contig=<ID=GL000242.1,length=43523>
    ##contig=<ID=GL000230.1,length=43691>
    ##contig=<ID=GL000237.1,length=45867>
    ##contig=<ID=GL000233.1,length=45941>
    ##contig=<ID=GL000204.1,length=81310>
    ##contig=<ID=GL000198.1,length=90085>
    ##contig=<ID=GL000208.1,length=92689>
    ##contig=<ID=GL000191.1,length=106433>
    ##contig=<ID=GL000227.1,length=128374>
    ##contig=<ID=GL000228.1,length=129120>
    ##contig=<ID=GL000214.1,length=137718>
    ##contig=<ID=GL000221.1,length=155397>
    ##contig=<ID=GL000209.1,length=159169>
    ##contig=<ID=GL000218.1,length=161147>
    ##contig=<ID=GL000220.1,length=161802>
    ##contig=<ID=GL000213.1,length=164239>
    ##contig=<ID=GL000211.1,length=166566>
    ##contig=<ID=GL000199.1,length=169874>
    ##contig=<ID=GL000217.1,length=172149>
    ##contig=<ID=GL000216.1,length=172294>
    ##contig=<ID=GL000215.1,length=172545>
    ##contig=<ID=GL000205.1,length=174588>
    ##contig=<ID=GL000219.1,length=179198>
    ##contig=<ID=GL000224.1,length=179693>
    ##contig=<ID=GL000223.1,length=180455>
    ##contig=<ID=GL000195.1,length=182896>
    ##contig=<ID=GL000212.1,length=186858>
    ##contig=<ID=GL000222.1,length=186861>
    ##contig=<ID=GL000200.1,length=187035>
    ##contig=<ID=GL000193.1,length=189789>
    ##contig=<ID=GL000194.1,length=191469>
    ##contig=<ID=GL000225.1,length=211173>
    ##contig=<ID=GL000192.1,length=547496>
    ##contig=<ID=NC_007605,length=171823>
    ##contig=<ID=hs37d5,length=35477943>
    #CHROM	POS	ID	REF	ALT	QUAL	FILTER	INFO
    1	197112812	.	GCTC	G	.	.	.
    1	866511	.	C	CCCCT	.	.	.
    1	879317	.	C	T	.	.	.
    1	879482	.	G	C	.	.	.
