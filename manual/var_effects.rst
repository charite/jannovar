.. _var_effects:

Variant Effects
===============

This section describes the variant effect names that Jannovar uses for annotating variants.
These descriptions are `Sequence Ontology <http://www.sequenceontology.org/>`_ (SO) terms and meant to be compatible with the `Variant annotations in VCF format <http://snpeff.sourceforge.net/VCFannotationformat_v1.0.pdf>`_ standard.

Effect Names
------------

The following table gives a list of the used SO terms, the putative impact, and the SO ID.
The section `Classic Jannovar Effects`_ lists the effect annotations used in the previous Jannovar versions and the corresponding SO-based effect name.
The putative impact is one of HIGH, MODERATE, LOW, and MODIFIER.
The impact class MODIFIER is both used for terms with hard-to-predict effects and markers (e.g. non_coding_transcript_variant).

===============  ==========  ===================================
Putative Impact  SO ID       SO Term
===============  ==========  ===================================
HIGH             SO:1000182  chromosome_number_variation
HIGH             SO:0001624  transcript_ablation
HIGH             SO:0001572  exon_loss_variant
HIGH             SO:0001909  frameshift_elongation
HIGH             SO:0001910  frameshift_truncation
HIGH             SO:0001589  frameshift_variant
HIGH             SO:0001908  internal_feature_elongation
HIGH             SO:0001906  feature_truncation
HIGH             SO:0001583  mnv
HIGH             SO:1000005  complex_substitution
HIGH             SO:0002012  stop_gained
HIGH             SO:0002012  stop_lost
HIGH             SO:0002012  start_lost
HIGH             SO:0001619  splice_acceptor_variant
HIGH             SO:0001575  splice_donor_variant
HIGH             SO:0001619  rare_amino_acid_variant
MODERATE         SO:0001583  missense_variant
MODERATE         SO:0001821  inframe_insertion
MODERATE         SO:0001824  disruptive_inframe_insertion
MODERATE         SO:0001822  inframe_deletion
MODERATE         SO:0001826  disruptive_inframe_deletion
MODERATE         SO:0002013  5_prime_utr_truncation
MODERATE         SO:0001819  3_prime_utr_truncation
MODERATE         SO:0001630  splice_region_variant
LOW              SO:0001567  stop_retained_variant
LOW              SO:0001582  initiator_codon_variant
LOW              SO:0001819  synonymous_variant
LOW              SO:0001969  coding_transcript_intron_variant
LOW              SO:0001583  non_coding_transcript_exon_variant
LOW              SO:0001970  non_coding_transcript_intron_variant
LOW              SO:0001983  5_prime_UTR_premature_start_codon_gain_variant
LOW              SO:0001623  5_prime_utr_variant
LOW              SO:0001624  3_prime_utr_variant
MODIFIER         SO:1000039  direct_tandem_duplication
MODIFIER         <custom>    <custom>
MODIFIER         SO:0001624  upstream_gene_variant
MODIFIER         SO:0001632  downstream_gene_variant
MODIFIER         SO:0001628  intergenic_variant
MODIFIER         SO:0001819  tf_binding_site_variant
MODIFIER         SO:0001619  regulatory_region_variant
MODIFIER         SO:0002018  conserved_intron_variant
MODIFIER         SO:0001908  intragenic_variant
MODIFIER         SO:0002017  conserved_intergenic_variant
MODIFIER         SO:0001537  structural_variant
MODIFIER         SO:0001580  coding_sequence_variant
MODIFIER         SO:0001908  intron_variant
MODIFIER         SO:0001791  exon_variant
MODIFIER         SO:0001568  splicing_variant
MODIFIER         SO:0001908  miRNA
MODIFIER         SO:0001564  gene_variant
MODIFIER         SO:0001968  coding_transcript_variant
MODIFIER         SO:0001619  non_coding_transcript_variant
MODIFIER         SO:0001624  transcript_variant
MODIFIER         SO:0000605  intergenic_region
MODIFIER         SO:0000340  chromosome
MODIFIER         SO:0001060  sequence_variant
===============  ==========  ===================================

Classic Jannovar Effects
------------------------

The original Jannovar used the following terms together with priority levels.

========  =================== ===========================
Priority  Classic Term        Description
========  =================== ===========================
1         FS_DELETION         frameshift truncation
1         FS_DUPLICATION      frameshift duplication
1         FS_INSERTION        frameshift elongation
1         FS_SUBSTITUTION     frameshift substitution
1         MISSENSE            missense
1         NON_FS_DELETION     inframe deletion
1         NON_FS_DUPLICATION  inframe duplication
1         NON_FS_INSERTION    inframe insertion
1         NON_FS_SUBSTITUTION inframe substitution
1         SPLICING            splicing
1         START_LOSS          startloss
1         STOPGAIN            stopgain
1         STOPLOSS            stoploss
1         SV_DELETION         1k+ deletion
1         SV_INSERTION        1k+ insertion
1         SV_INVERSION        1k+ inversion
1         SV_SUBSTITUTION     1k+ substitution
2         ncRNA_EXONIC        ncRNA exonic
2         ncRNA_SPLICING      ncRNA splicing
3         UTR3                UTR3
4         UTR5                UTR5
5         SYNONYMOUS          synonymous
6         INTRONIC            intronic
7         ncRNA_INTRONIC      ncRNA intronic
8         DOWNSTREAM          downstream
8         UPSTREAM            upstream
9         INTERGENIC          intergenic
10        ERROR               error
========  =================== ===========================


The following table gives a mapping between classic Jannovar terms to SO-based terms.
In some cases, two SO attributes are combined to achieve the same annotation.

========  ===================
Priority  Classic Term
========  ===================
1         MISSENSE
1         FS_DELETION
1         FS_INSERTION
1         NON_FS_DELETION
1         NON_FS_INSERTION
1         SPLICING
1         STOPGAIN
1         STOPLOSS
1         FS_DUPLICATION
1         NON_FS_DUPLICATION
1         FS_SUBSTITUTION
1         NON_FS_SUBSTITUTION
1         STARTLOSS
2         ncRNA_EXONIC
2         ncRNA_SPLICING
3         UTR3
4         UTR5
5         SYNONYMOUS
6         INTRONIC
7         ncRNA_INTRONIC
8         UPSTREAM
8         DOWNSTREAM
9         INTERGENIC
10        ERROR
========  ===================

=============================================  =============================
SO Term                                        Classic Term
=============================================  =============================
chromosome_number_variation                     -
transcript_ablation                             TRANSCRIPT_ABLATION
exon_loss_variant                               -
frameshift_elongation                           FS_INSERTION
frameshift_truncation                           FS_DELETION
frameshift_variant                              -
internal_feature_elongation                     NON_FS_INSERTION
feature_truncation                              NON_FS_DELETION
mnv                                             NON_FS_SUBSTITUTION
complex_substitution                            -
stop_gained                                     STOPGAIN
stop_lost                                       STOPLOSS
start_lost                                      STARTLOSS
splice_acceptor_variant                         SPLICING
splice_donor_variant                            SPLICING
rare_amino_acid_variant                         MISSENSE
missense_variant                                MISSENSE
inframe_insertion                               NON_FS_INSERTION
disruptive_inframe_insertion                    NON_FS_INSERTION
inframe_deletion                                NON_FS_DELETION
disruptive_inframe_deletion                     NON_FS_DELETION
5_prime_utr_truncation                          UTR5
3_prime_utr_truncation                          UTR3
splice_region_variant                           SPLICING
stop_retained_variant                           SYNONYMOUS
initiator_codon_variant                         STARTLOSS
synonymous_variant                              SYNONYMOUS
coding_transcript_intron_variant                INTRONIC
non_coding_transcript_exon_variant              ncRNA_EXONIC
non_coding_transcript_intron_variant            ncRNA_INTRONIC
5_prime_UTR_premature_start_codon_gain_variant  UTR5
5_prime_utr_variant                             UTR5
3_prime_utr_variant                             UTR3
direct_tandem_duplication                       NON_FS_DELETION
<custom>                                        -
upstream_gene_variant                           UPSTREAM
downstream_gene_variant                         DOWNSTREAM
intergenic_variant                              INTERGENIC
tf_binding_site_variant                         -
regulatory_region_variant                       -
conserved_intron_variant                        INTRONIC
intragenic_variant                              INTRAGENIC
conserved_intergenic_variant                    INTERGENIC
structural_variant                              -
coding_sequence_variant                         -
intron_variant                                  INTRONIC
exon_variant                                    -
splicing_variant                                SPLICING
miRNA                                           -
gene_variant                                    -
coding_transcript_variant                       -
non_coding_transcript_variant                   -
transcript_variant                              -
intergenic_region                               -
chromosome                                      -
sequence_variant                                UNKNOWN
=============================================  =============================
