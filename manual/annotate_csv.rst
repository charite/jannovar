.. _annotate_csv:

Annotating CSV Files
====================

Sometimes, it is useful to annotate a whole CSV file where you have the chromosome, position, reference allele, and alternative allele in different columns.
You can do this using the ``annotate-csv`` command of Jannovar.

You have to pass a path to a annotation database file and one or more chromosomal change specifiers.
Jannovar will then return the effect, the HGVS annotation at the end of the line in the given CSV format and prints it out to the standard output.

Just imagine we have tab separated file with a header named input.tsv

.. code-block:: text

	contig	position	reference	alt
	chr1	12345	C	A
	chr1	12346	C	A

Now we run jannovar with this command an will get this output:

.. parsed-literal::
    # java -jar jannovar-cli-\ |version|\ .jar annotate-csv -d data/hg19_refseq.ser --input input.tsv -c 1 -p 2 -r 3 -a 4 --header --type TDF
    [...]
    contig  position	reference	alt	HGVS	FunctionalClass
    chr1	12345	C	A	DDX11L1:NR_046018.2:n.354+118C>A:	NON_CODING_TRANSCRIPT_INTRON_VARIANT
    chr1	12346	C	A	DDX11L1:NR_046018.2:n.354+119C>A:	NON_CODING_TRANSCRIPT_INTRON_VARIANT

The format for the chromsomal change is as follows:

.. code-block:: console

    {CHROMOSOME}	{POSITION}	{REF}	{ALT}

CHROMOSOME
  name of the chromosome or contig
POSITION
  position of the first change base on the chromosome; in the case of insertions the first base after the insertion; the first base on the chromosome has position ``1``
REF
  the reference bases
ALT
  the alternative bases


Right now it is only possible to use the column number and not the header column. This might be extended in the future. Possible CSV file types are:

Default
	Standard comma separated format, as for RFC4180 but allowing empty lines.
TDF
	Tab-delimited format.
RFC4180
	Comma separated format as defined by `RFC4180 <http://tools.ietf.org/html/rfc4180>`_.
Excel
	Excel file format (using a comma as the value delimiter). Note that the actual value delimiter used by Excel is locale dependent, it might be necessary to customize this format to accommodate to your regional settings.
MySQL
	Default MySQL format. This is a tab-delimited format with a LF character as the line separator. Values are not quoted and special characters are escaped with ``\``. The default `NULL` string is ``\\N``.


