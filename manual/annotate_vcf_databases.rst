.. _annotate_vcf_databases:

Annotation of other databases
====================

In addition to the transcript variant effect annotation Jannovar can annotate some additional databases like ExAC or dbSNP. For these extra annotation the ``annotate-vcf`` command is used with some additional options.


Exact vs position wise annotation
----------

Different programs that annotate allele frequency etc. use different strategies to annotate variants. Mostly only position wise is annotated and the exact genotype is discarded. If annotation is used for filtering this might be problematic because a rare genotype is filtered out because of a common genotype at the same position. Therefor Jannovar has enables both: position and genotype wise annotation.

If the annotation matches the position of the variant an additional string ``OVL_`` is added to the token in the INFO column. If the genotype matches this identifier is missing.

ExAC
----------



dnSBP
----------



UK10G
----------
