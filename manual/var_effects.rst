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
HIGH             SO:0001893  transcript_ablation
HIGH             SO:0001572  exon_loss_variant
HIGH             SO:0001909  frameshift_elongation
HIGH             SO:0001910  frameshift_truncation
HIGH             SO:0001589  frameshift_variant
HIGH             SO:0001908  internal_feature_elongation
HIGH             SO:0001906  feature_truncation
HIGH             SO:0002007  mnv
HIGH             SO:1000005  complex_substitution
HIGH             SO:0001587  stop_gained
HIGH             SO:0001578  stop_lost
HIGH             SO:0002012  start_lost
HIGH             SO:0001574  splice_acceptor_variant
HIGH             SO:0001575  splice_donor_variant
HIGH             SO:0002008  rare_amino_acid_variant
MODERATE         SO:0001583  missense_variant
MODERATE         SO:0001821  inframe_insertion
MODERATE         SO:0001824  disruptive_inframe_insertion
MODERATE         SO:0001822  inframe_deletion
MODERATE         SO:0001826  disruptive_inframe_insertion
MODERATE         SO:0002013  5_prime_utr_truncation
MODERATE         SO:0002015  3_prime_utr_truncation
MODERATE         SO:0001630  splice_region_variant
LOW              SO:0001567  stop_retained_variant
LOW              SO:0001582  initiator_codon_variant
LOW              SO:0001819  synonymous_variant
LOW              SO:0001623  5_prime_utr_variant
LOW              SO:0001624  3_prime_utr_variant
LOW              SO:0001969  coding_transcript_intron_variant
LOW              SO:0001792  non_coding_transcript_exon_variant
LOW              SO:0001970  non_coding_transcript_intron_variant
MODIFIER         SO:0001631  upstream_gene_variant
MODIFIER         SO:0001632  downstream_gene_variant
MODIFIER         SO:0001628  intergenic_variant
===============  ==========  ===================================

Classic Jannovar Effects
------------------------

The original Jannovar used the following terms together with priority levels.

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

===================================  =============================
SO Term                              Classic Term
===================================  =============================
transcript_ablation                  -
exon_loss_variant                    -
frameshift_elongation                FS_INSERTION/FS_DELETION/FS_SUBSTITUTION
frameshift_truncation                FS_INSERTION/FS_DELETION/FS_SUBSTITUTION
frameshift_variant                   FS_INSERTION/FS_DELETION/FS_SUBSTITUTION
internal_feature_elongation          NON_FS_INSERTION
feature_truncation                   NON_FS
mnv
complex_substitution
stop_gained
stop_lost
start_lost
splice_acceptor_variant
splice_donor_variant
rare_amino_acid_variant
missense_variant
inframe_insertion
disruptive_inframe_insertion
inframe_deletion
disruptive_inframe_insertion
5_prime_utr_truncation
3_prime_utr_truncation
splice_region_variant
stop_retained_variant
initiator_codon_variant
synonymous_variant
5_prime_utr_variant
3_prime_utr_variant
coding_transcript_intron_variant
non_coding_transcript_exon_variant
non_coding_transcript_intron_variant
upstream_gene_variant
downstream_gene_variant
intergenic_variant
non_coding_transcript_variant
