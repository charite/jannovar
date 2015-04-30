.. _quickstart:

Quickstart
==========

This short How-To guides you from downloading the Jannovar program to annotating a VCF file in 5 steps.

#. Download the current stable release from our `GitHub project <https://github.com/charite/jannovar>`_ by clicking `here <https://github.com/charite/jannovar/releases/download/v0.14/jannovar-0.14.zip>`_.
#. Extract the ZIP archive.

   * you should find file called ``jannovar-cli-0.14.jar`` in the ZIP
   * you should also find a file ``small.vcf`` file in the folder ``examples``

#. Download the `RefSeq <http://www.ncbi.nlm.nih.gov/refseq/>`_ transcript database for the release *hg19/GRCh37*.

   .. note::

      If you are behind a proxy then you have to pass its path to the ``--proxy`` option, e.g., ``--proxy http://proxy.example.com:8080``.
      See the section :ref:`proxy_settings` for more information.

   .. code-block:: console

      # java -jar jannovar-cli-0.14.jar download hg19/refseq

   This will create the file ``data/hg19_refseq.ser`` which is a self-contained transcript database and can be used for functional annotation.
#. Annotate the file ``small.vcf`` from the ``examples`` directory.

   .. code-block:: console

      # java -jar jannovar-cli-0.14.jar annotate data/hg19_refseq.ser examples/small.vcf

   Jannovar will now load the transcript database from ``data/hg19_refseq.ser`` and then read ``examples/small.vcf`` file.
   Each contained variant in this file will be annotated with an ``EFFECT`` and an ``HGVS`` field in the ``VCF`` info column.
   The ``EFFECT`` field contains an effect, e.g., ``SYNONYMOUS`` and the ``HGVS`` field contains a HGVS representation of the variant.
   The result will be written out to ``small.jv.vcf``.

   The following excerpt shows the first three variants of the ``small.vcf`` file with their effect and HGVS annotation.

   .. code-block:: text

      1	866511	rs60722469	C	CCCCT	258.62	PASS	EFFECT=INTRONIC;HGVS=SAMD11:NM_152486.2:c.305+42_305+43insCCCT	GT:AD:DP:GQ:PL	1/1:6,5:11:14.79:300,15,0
      1	879317	rs7523549	C	T	150.77	PASS	EFFECT=MISSENSE;HGVS=SAMD11:XM_005244727.1:exon9:c.799C>T:p.Arg267Cys	GT:AD:DP:GQ:PL	0/1:14,7:21:99:181,0,367
      1	879482	.	G	C	484.52	PASS	EFFECT=MISSENSE;HGVS=SAMD11:XM_005244727.1:exon9:c.964G>C:p.Asp322His	GT:AD:DP:GQ:PL	0/1:28,20:48:99:515,0,794

Next Steps
----------

Of course, you can follow the other manual chapters and get more extensive information on Jannovar.
In addition, here are some external links that can help you in your understanding:

Current VCF Specification
  can be found in the **hts-specs** project on GitHub `here <https://github.com/samtools/hts-specs>`_.
HGVS Mutation Nomenclature.
  is mainainted by the `Human Genome Variation Society <http://www.hgvs.org/>`_ and the nomenclature can be found `here <http://www.hgvs.org/mutnomen/>`_.
