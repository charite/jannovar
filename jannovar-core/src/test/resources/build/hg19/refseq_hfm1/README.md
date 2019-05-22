# Hand-curated data set for HFM1

- default_sources.ini was hand-curated
- chr_accessions* and chromInfo.txt.gz are copied from actual download
- GFF was created by
    ```
    zgrep '^#\|HFM1\|XP_005270609.1\|NP_001017975.3\|XM_005270552.1\|NM_001017975.3\|NM_006304.1\|XP_005270609.1\|NP_001017975.3\|XM_005270552.1\|NM_001017975.3\|gene=HFM1' data/hg19/refseq/ref_GRCh37.p13_top_level.gff3.gz | bgzip -c > /vol/local/projects/jannovar/jannovar-core/src/test/resources/build/hg19/refseq_hfm1/ref_GRCh37.p13_top_level.gff3.gz
    ```
- RNA FASTA was created by
    ```
    zcat data/hg19/refseq/rna.fa.gz  > data/hg19/refseq/rna.fa
    samtools faidx data/hg19/refseq/rna.fa
    for x in 'gi|130484566|ref|NM_001017975.3|' 'gi|530361974|ref|XM_005270552.1|' 'gi|5453639|ref|NM_006304.1|'; do samtools faidx data/hg19/refseq/rna.fa $x; done | gzip -c > /vol/local/projects/jannovar/jannovar-core/src/test/resources/build/hg19/refseq_hfm1/rna.fa.gz
    ```
