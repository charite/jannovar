# Hand-curated data set for LTBP4

- default_sources.ini was hand-curated
- chr_accessions* and chromInfo.txt.gz are copied from actual download
- GFF was created by
    ```
    zgrep '^#\|LTBP4\|NP_001036010.1\|NM_003573.2\|NM_001042544.1\|NM_001042545.1\|gene=LTBP4' data/hg19/refseq/ref_GRCh37.p13_top_level.gff3.gz | bgzip -c > /vol/local/projects/jannovar/jannovar-core/src/test/resources/build/hg19/refseq_ltbp4/ref_GRCh37.p13_top_level.gff3.gz
    ```
- RNA FASTA was created by
    ```
    zcat data/hg19/refseq/rna.fa.gz  > data/hg19/refseq/rna.fa
    samtools faidx data/hg19/refseq/rna.fa
    for x in 'gi|110347411|ref|NM_003573.2|' 'gi|110347430|ref|NM_001042544.1|' 'gi|110347436|ref|NM_001042545.1|'; do samtools faidx data/hg19/refseq/rna.fa $x; done | gzip -c > /vol/local/projects/jannovar/jannovar-core/src/test/resources/build/hg19/refseq_ltbp4/rna.fa.gz
    ```
