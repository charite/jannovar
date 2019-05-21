# chr1_oma1_to_jun.ser - RefSeq snippet from chr1, including genes OMA1 TACSTD2
# MYSM1 JUN. hg19 coordinates.

java -jar jannovar-cli/target/jannovar-cli-0.31-SNAPSHOT.jar \
    download \
    -d hg19/refseq_curated \
    -o jannovar-cli/src/test/resources/chr1_oma1_to_jun.ser \
    --gene-ids OMA1 TACSTD2 MYSM1 JUN
cp jannovar-cli/src/test/resources/chr1_oma1_to_jun.ser jannovar-core/src/test/resources/chr1_oma1_to_jun.ser

# chr15_whammp3.ser - ENSEMBL snippet of gene WHAMPP3

java -jar jannovar-cli/target/jannovar-cli-0.31-SNAPSHOT.jar \
     download \
     -d hg19/ensembl \
     -o jannovar-core/src/test/resources/chr15_whammp3.ser \
     --gene-ids WHAMMP3

# hg19_small.ser - just the beginning of chr1 from refseq

java -jar jannovar-cli/target/jannovar-cli-0.31-SNAPSHOT.jar \
    download \
    -d hg19/refseq \
    -o jannovar-cli/src/test/resources/hg19_small.ser \
    --gene-ids DDX11L1 FAM138A

# chr19_ltbp4.ser - RefSeq transcripts of LTBP4 with fixes from UCSC

java -jar jannovar-cli/target/jannovar-cli-0.31-SNAPSHOT.jar \
    download \
    -d hg19/refseq_curated \
    -o jannovar-core/src/test/resources/chr19_ltbp4.ser \
    --gene-ids LTBP4
