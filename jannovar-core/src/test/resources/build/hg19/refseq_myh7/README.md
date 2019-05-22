# Hand-curated data set for MYH7

- default_sources.ini was hand-curated
- chr_accessions* and chromInfo.txt.gz are copied from actual download
- GFF was created by
    ```
    zgrep -w '^#\|MYH7\|gene=MYH7\|XP_005267754.1\|XM_005267697.1\|XR_245686.1\|NP_000248.2\|XM_005267696.1\|NM_000257.2' data/hg19/refseq/ref_GRCh37.p13_top_level.gff3.gz | bgzip -c > /vol/local/projects/jannovar/jannovar-core/src/test/resources/build/hg19/refseq_myh7/ref_GRCh37.p13_top_level.gff3.gz
    ```
- RNA FASTA was created by
    ```
    zcat data/hg19/refseq/rna.fa.gz  > data/hg19/refseq/rna.fa
    samtools faidx data/hg19/refseq/rna.fa
    for x in 'gi|115496168|ref|NM_000257.2|' 'gi|530403846|ref|XM_005267696.1|' 'gi|530403848|ref|XR_245686.1|' 'gi|530403849|ref|XM_005267697.1|'; do samtools faidx data/hg19/refseq/rna.fa $x; done | gzip -c > /vol/local/projects/jannovar/jannovar-core/src/test/resources/build/hg19/refseq_myh7/rna.fa.gz
    ```
