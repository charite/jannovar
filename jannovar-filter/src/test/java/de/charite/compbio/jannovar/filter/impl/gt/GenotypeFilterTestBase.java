package de.charite.compbio.jannovar.filter.impl.gt;

import java.io.File;
import java.io.PrintWriter;

import com.google.common.io.Files;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;

public class GenotypeFilterTestBase {

	/**
	 * Write out VCF file with one line and additional header, read in line again and return record
	 * 
	 * @param vcfHeaderLines
	 *            Additional VCF headers to write
	 * @param vcfLine
	 *            VCF line to write
	 * @return
	 * @throws Exception
	 *             in case of any problems
	 */
	protected VariantContext writeAndReadVcfLine(String vcfLine, String vcfHeaderLines) throws Exception {
		// Setup dbSNP VCF file
		File tmpDir = Files.createTempDir();

		// Write out file to use in the test
		String testVCFPath = tmpDir + "/test_file.vcf";
		PrintWriter writer = new PrintWriter(testVCFPath);
		writer.write("##fileformat=VCFv4.0\n");
		writer.write(vcfHeaderLines);
		writer.write("#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\tFORMAT\tindividual\n");
		writer.write(vcfLine);
		writer.close();

		try (VCFFileReader vcfReader = new VCFFileReader(new File(testVCFPath), false)) {
			return vcfReader.iterator().next();
		}
	}

	public static final String BCFTOOLS_HEADER = "##ALT=<ID=X,Description=\"Represents allele(s) other than observed.\">\n"
			+ "##INFO=<ID=INDEL,Number=0,Type=Flag,Description=\"Indicates that the variant is an INDEL.\">\n"
			+ "##INFO=<ID=IDV,Number=1,Type=Integer,Description=\"Maximum number of reads supporting an indel\">\n"
			+ "##INFO=<ID=IMF,Number=1,Type=Float,Description=\"Maximum fraction of reads supporting an indel\">\n"
			+ "##INFO=<ID=DP,Number=1,Type=Integer,Description=\"Raw read depth\">\n"
			+ "##INFO=<ID=VDB,Number=1,Type=Float,Description=\"Variant Distance Bias for filtering splice-site artefacts in RNA-seq data (bigger is better)\">\n"
			+ "##INFO=<ID=RPB,Number=1,Type=Float,Description=\"Mann-Whitney U test of Read Position Bias (bigger is better)\">\n"
			+ "##INFO=<ID=MQB,Number=1,Type=Float,Description=\"Mann-Whitney U test of Mapping Quality Bias (bigger is better)\">\n"
			+ "##INFO=<ID=BQB,Number=1,Type=Float,Description=\"Mann-Whitney U test of Base Quality Bias (bigger is better)\">\n"
			+ "##INFO=<ID=MQSB,Number=1,Type=Float,Description=\"Mann-Whitney U test of Mapping Quality vs Strand Bias (bigger is better)\">\n"
			+ "##INFO=<ID=SGB,Number=1,Type=Float,Description=\"Segregation based metric.\">\n"
			+ "##INFO=<ID=MQ0F,Number=1,Type=Float,Description=\"Fraction of MQ0 reads (smaller is better)\">\n"
			+ "##FORMAT=<ID=PL,Number=G,Type=Integer,Description=\"List of Phred-scaled genotype likelihoods\">\n"
			+ "##FORMAT=<ID=DP,Number=1,Type=Integer,Description=\"Number of high-quality bases\">\n"
			+ "##FORMAT=<ID=DV,Number=1,Type=Integer,Description=\"Number of high-quality non-reference bases\">\n"
			+ "##FORMAT=<ID=DPR,Number=R,Type=Integer,Description=\"Number of high-quality bases observed for each allele\">\n"
			+ "##INFO=<ID=DPR,Number=R,Type=Integer,Description=\"Number of high-quality bases observed for each allele\">\n"
			+ "##FORMAT=<ID=DP4,Number=4,Type=Integer,Description=\"Number of high-quality ref-fwd, ref-reverse, alt-fwd and alt-reverse bases\">\n"
			+ "##FORMAT=<ID=SP,Number=1,Type=Integer,Description=\"Phred-scaled strand bias P-value\">\n"
			+ "##FORMAT=<ID=GT,Number=1,Type=String,Description=\"Genotype\">\n"
			+ "##FORMAT=<ID=GQ,Number=1,Type=Integer,Description=\"Phred-scaled Genotype Quality\">\n"
			+ "##FORMAT=<ID=GP,Number=G,Type=Float,Description=\"Phred-scaled genotype posterior probabilities\">\n"
			+ "##INFO=<ID=ICB,Number=1,Type=Float,Description=\"Inbreeding Coefficient Binomial test (bigger is better)\">\n"
			+ "##INFO=<ID=HOB,Number=1,Type=Float,Description=\"Bias in the number of HOMs number (smaller is better)\">\n"
			+ "##INFO=<ID=AC,Number=A,Type=Integer,Description=\"Allele count in genotypes for each ALT allele, in the same order as listed\">\n"
			+ "##INFO=<ID=AN,Number=1,Type=Integer,Description=\"Total number of alleles in called genotypes\">\n"
			+ "##INFO=<ID=DP4,Number=4,Type=Integer,Description=\"Number of high-quality ref-forward , ref-reverse, alt-forward and alt-reverse bases\">\n"
			+ "##INFO=<ID=MQ,Number=1,Type=Integer,Description=\"Average mapping quality\">\n";
	public static final String FREEBAYES_HEADER = "##INFO=<ID=NS,Number=1,Type=Integer,Description=\"Number of samples with data\">\n"
			+ "##INFO=<ID=DP,Number=1,Type=Integer,Description=\"Total read depth at the locus\">\n"
			+ "##INFO=<ID=DPB,Number=1,Type=Float,Description=\"Total read depth per bp at the locus; bases in reads overlapping / bases in haplotype\">\n"
			+ "##INFO=<ID=AC,Number=A,Type=Integer,Description=\"Total number of alternate alleles in called genotypes\">\n"
			+ "##INFO=<ID=AN,Number=1,Type=Integer,Description=\"Total number of alleles in called genotypes\">\n"
			+ "##INFO=<ID=AF,Number=A,Type=Float,Description=\"Estimated allele frequency in the range (0,1]\">\n"
			+ "##INFO=<ID=RO,Number=1,Type=Integer,Description=\"Reference allele observation count, with partial observations recorded fractionally\">\n"
			+ "##INFO=<ID=AO,Number=A,Type=Integer,Description=\"Alternate allele observations, with partial observations recorded fractionally\">\n"
			+ "##INFO=<ID=PRO,Number=1,Type=Float,Description=\"Reference allele observation count, with partial observations recorded fractionally\">\n"
			+ "##INFO=<ID=PAO,Number=A,Type=Float,Description=\"Alternate allele observations, with partial observations recorded fractionally\">\n"
			+ "##INFO=<ID=QR,Number=1,Type=Integer,Description=\"Reference allele quality sum in phred\">\n"
			+ "##INFO=<ID=QA,Number=A,Type=Integer,Description=\"Alternate allele quality sum in phred\">\n"
			+ "##INFO=<ID=PQR,Number=1,Type=Float,Description=\"Reference allele quality sum in phred for partial observations\">\n"
			+ "##INFO=<ID=PQA,Number=A,Type=Float,Description=\"Alternate allele quality sum in phred for partial observations\">\n"
			+ "##INFO=<ID=SRF,Number=1,Type=Integer,Description=\"Number of reference observations on the forward strand\">\n"
			+ "##INFO=<ID=SRR,Number=1,Type=Integer,Description=\"Number of reference observations on the reverse strand\">\n"
			+ "##INFO=<ID=SAF,Number=A,Type=Integer,Description=\"Number of alternate observations on the forward strand\">\n"
			+ "##INFO=<ID=SAR,Number=A,Type=Integer,Description=\"Number of alternate observations on the reverse strand\">\n"
			+ "##INFO=<ID=SRP,Number=1,Type=Float,Description=\"Strand balance probability for the reference allele: Phred-scaled upper-bounds estimate of the probability of observing the deviation between SRF and SRR given E(SRF/SRR) ~ 0.5, derived using Hoeffding's inequality\">\n"
			+ "##INFO=<ID=SAP,Number=A,Type=Float,Description=\"Strand balance probability for the alternate allele: Phred-scaled upper-bounds estimate of the probability of observing the deviation between SAF and SAR given E(SAF/SAR) ~ 0.5, derived using Hoeffding's inequality\">\n"
			+ "##INFO=<ID=AB,Number=A,Type=Float,Description=\"Allele balance at heterozygous sites: a number between 0 and 1 representing the ratio of reads showing the reference allele to all reads, considering only reads from individuals called as heterozygous\">\n"
			+ "##INFO=<ID=ABP,Number=A,Type=Float,Description=\"Allele balance probability at heterozygous sites: Phred-scaled upper-bounds estimate of the probability of observing the deviation between ABR and ABA given E(ABR/ABA) ~ 0.5, derived using Hoeffding's inequality\">\n"
			+ "##INFO=<ID=RUN,Number=A,Type=Integer,Description=\"Run length: the number of consecutive repeats of the alternate allele in the reference genome\">\n"
			+ "##INFO=<ID=RPP,Number=A,Type=Float,Description=\"Read Placement Probability: Phred-scaled upper-bounds estimate of the probability of observing the deviation between RPL and RPR given E(RPL/RPR) ~ 0.5, derived using Hoeffding's inequality\">\n"
			+ "##INFO=<ID=RPPR,Number=1,Type=Float,Description=\"Read Placement Probability for reference observations: Phred-scaled upper-bounds estimate of the probability of observing the deviation between RPL and RPR given E(RPL/RPR) ~ 0.5, derived using Hoeffding's inequality\">\n"
			+ "##INFO=<ID=RPL,Number=A,Type=Float,Description=\"Reads Placed Left: number of reads supporting the alternate balanced to the left (5') of the alternate allele\">\n"
			+ "##INFO=<ID=RPR,Number=A,Type=Float,Description=\"Reads Placed Right: number of reads supporting the alternate balanced to the right (3') of the alternate allele\">\n"
			+ "##INFO=<ID=EPP,Number=A,Type=Float,Description=\"End Placement Probability: Phred-scaled upper-bounds estimate of the probability of observing the deviation between EL and ER given E(EL/ER) ~ 0.5, derived using Hoeffding's inequality\">\n"
			+ "##INFO=<ID=EPPR,Number=1,Type=Float,Description=\"End Placement Probability for reference observations: Phred-scaled upper-bounds estimate of the probability of observing the deviation between EL and ER given E(EL/ER) ~ 0.5, derived using Hoeffding's inequality\">\n"
			+ "##INFO=<ID=DPRA,Number=A,Type=Float,Description=\"Alternate allele depth ratio.  Ratio between depth in samples with each called alternate allele and those without.\">\n"
			+ "##INFO=<ID=ODDS,Number=1,Type=Float,Description=\"The log odds ratio of the best genotype combination to the second-best.\">\n"
			+ "##INFO=<ID=GTI,Number=1,Type=Integer,Description=\"Number of genotyping iterations required to reach convergence or bailout.\">\n"
			+ "##INFO=<ID=TYPE,Number=A,Type=String,Description=\"The type of allele, either snp, mnp, ins, del, or complex.\">\n"
			+ "##INFO=<ID=CIGAR,Number=A,Type=String,Description=\"The extended CIGAR representation of each alternate allele, with the exception that '=' is replaced by 'M' to ease VCF parsing.  Note that INDEL alleles do not have the first matched base (which is provided by default, per the spec) referred to by the CIGAR.\">\n"
			+ "##INFO=<ID=NUMALT,Number=1,Type=Integer,Description=\"Number of unique non-reference alleles in called genotypes at this position.\">\n"
			+ "##INFO=<ID=MEANALT,Number=A,Type=Float,Description=\"Mean number of unique non-reference allele observations per sample with the corresponding alternate alleles.\">\n"
			+ "##INFO=<ID=LEN,Number=A,Type=Integer,Description=\"allele length\">\n"
			+ "##INFO=<ID=MQM,Number=A,Type=Float,Description=\"Mean mapping quality of observed alternate alleles\">\n"
			+ "##INFO=<ID=MQMR,Number=1,Type=Float,Description=\"Mean mapping quality of observed reference alleles\">\n"
			+ "##INFO=<ID=PAIRED,Number=A,Type=Float,Description=\"Proportion of observed alternate alleles which are supported by properly paired read fragments\">\n"
			+ "##INFO=<ID=PAIREDR,Number=1,Type=Float,Description=\"Proportion of observed reference alleles which are supported by properly paired read fragments\">\n"
			+ "##INFO=<ID=MIN,Number=1,Type=Integer,Description=\"Minimum depth in gVCF output block.\">\n"
			+ "##INFO=<ID=END,Number=1,Type=Integer,Description=\"Last position (inclusive) in gVCF output record.\">\n"
			+ "##INFO=<ID=technology.ILLUMINA,Number=A,Type=Float,Description=\"Fraction of observations supporting the alternate observed in reads from ILLUMINA\">\n"
			+ "##FORMAT=<ID=GT,Number=1,Type=String,Description=\"Genotype\">\n"
			+ "##FORMAT=<ID=GQ,Number=1,Type=Float,Description=\"Genotype Quality, the Phred-scaled marginal (or unconditional) probability of the called genotype\">\n"
			+ "##FORMAT=<ID=GL,Number=G,Type=Float,Description=\"Genotype Likelihood, log10-scaled likelihoods of the data given the called genotype for each possible genotype generated from the reference and alternate alleles given the sample ploidy\">\n"
			+ "##FORMAT=<ID=DP,Number=1,Type=Integer,Description=\"Read Depth\">\n"
			+ "##FORMAT=<ID=RO,Number=1,Type=Integer,Description=\"Reference allele observation count\">\n"
			+ "##FORMAT=<ID=QR,Number=1,Type=Integer,Description=\"Sum of quality of the reference observations\">\n"
			+ "##FORMAT=<ID=AO,Number=A,Type=Integer,Description=\"Alternate allele observation count\">\n"
			+ "##FORMAT=<ID=QA,Number=A,Type=Integer,Description=\"Sum of quality of the alternate observations\">\n"
			+ "##FORMAT=<ID=MIN,Number=1,Type=Integer,Description=\"Minimum depth in gVCF output block.\">\n"
			+ "##INFO=<ID=OLD_VARIANT,Number=.,Type=String,Description=\"Original chr:pos:ref:alt encoding\">\n";
	public static final String GATK_HEADER = "##FILTER=<ID=LowQual,Description=\"Low quality\">\n"
			+ "##FORMAT=<ID=AD,Number=.,Type=Integer,Description=\"Allelic depths for the ref and alt alleles in the order listed\">\n"
			+ "##FORMAT=<ID=DP,Number=1,Type=Integer,Description=\"Approximate read depth (reads with MQ=255 or with bad mates are filtered)\">\n"
			+ "##FORMAT=<ID=GQ,Number=1,Type=Integer,Description=\"Genotype Quality\">\n"
			+ "##FORMAT=<ID=GT,Number=1,Type=String,Description=\"Genotype\">\n"
			+ "##FORMAT=<ID=PL,Number=G,Type=Integer,Description=\"Normalized, Phred-scaled likelihoods for genotypes as defined in the VCF specification\">\n"
			+ "##INFO=<ID=AC,Number=A,Type=Integer,Description=\"Allele count in genotypes, for each ALT allele, in the same order as listed\">\n"
			+ "##INFO=<ID=AF,Number=A,Type=Float,Description=\"Allele Frequency, for each ALT allele, in the same order as listed\">\n"
			+ "##INFO=<ID=AN,Number=1,Type=Integer,Description=\"Total number of alleles in called genotypes\">\n"
			+ "##INFO=<ID=BaseQRankSum,Number=1,Type=Float,Description=\"Z-score from Wilcoxon rank sum test of Alt Vs. Ref base qualities\">\n"
			+ "##INFO=<ID=DP,Number=1,Type=Integer,Description=\"Approximate read depth; some reads may have been filtered\">\n"
			+ "##INFO=<ID=DS,Number=0,Type=Flag,Description=\"Were any of the samples downsampled?\">\n"
			+ "##INFO=<ID=Dels,Number=1,Type=Float,Description=\"Fraction of Reads Containing Spanning Deletions\">\n"
			+ "##INFO=<ID=FS,Number=1,Type=Float,Description=\"Phred-scaled p-value using Fisher's exact test to detect strand bias\">\n"
			+ "##INFO=<ID=GC,Number=1,Type=Float,Description=\"GC content around the variant (see docs for window size details)\">\n"
			+ "##INFO=<ID=HRun,Number=1,Type=Integer,Description=\"Largest Contiguous Homopolymer Run of Variant Allele In Either Direction\">\n"
			+ "##INFO=<ID=HaplotypeScore,Number=1,Type=Float,Description=\"Consistency of the site with at most two segregating haplotypes\">\n"
			+ "##INFO=<ID=InbreedingCoeff,Number=1,Type=Float,Description=\"Inbreeding coefficient as estimated from the genotype likelihoods per-sample when compared against the Hardy-Weinberg expectation\">\n"
			+ "##INFO=<ID=MLEAC,Number=A,Type=Integer,Description=\"Maximum likelihood expectation (MLE) for the allele counts (not necessarily the same as the AC), for each ALT allele, in the same order as listed\">\n"
			+ "##INFO=<ID=MLEAF,Number=A,Type=Float,Description=\"Maximum likelihood expectation (MLE) for the allele frequency (not necessarily the same as the AF), for each ALT allele, in the same order as listed\">\n"
			+ "##INFO=<ID=MQ,Number=1,Type=Float,Description=\"RMS Mapping Quality\">\n"
			+ "##INFO=<ID=MQ0,Number=1,Type=Integer,Description=\"Total Mapping Quality Zero Reads\">\n"
			+ "##INFO=<ID=MQRankSum,Number=1,Type=Float,Description=\"Z-score From Wilcoxon rank sum test of Alt vs. Ref read mapping qualities\">\n"
			+ "##INFO=<ID=QD,Number=1,Type=Float,Description=\"Variant Confidence/Quality by Depth\">\n"
			+ "##INFO=<ID=RPA,Number=.,Type=Integer,Description=\"Number of times tandem repeat unit is repeated, for each allele (including reference)\">\n"
			+ "##INFO=<ID=RU,Number=1,Type=String,Description=\"Tandem repeat unit (bases)\">\n"
			+ "##INFO=<ID=ReadPosRankSum,Number=1,Type=Float,Description=\"Z-score from Wilcoxon rank sum test of Alt vs. Ref read position bias\">\n"
			+ "##INFO=<ID=SOR,Number=1,Type=Float,Description=\"Symmetric Odds Ratio of 2x2 contingency table to detect strand bias\">\n"
			+ "##INFO=<ID=STR,Number=0,Type=Flag,Description=\"Variant is a short tandem repeat\">\n";
	public static final String PLATYPUS_HEADER = "##INFO=<ID=FR,Number=.,Type=Float,Description=\"Estimated population frequency of variant\">\n"
			+ "##INFO=<ID=MMLQ,Number=1,Type=Float,Description=\"Median minimum base quality for bases around variant\">\n"
			+ "##INFO=<ID=TCR,Number=1,Type=Integer,Description=\"Total reverse strand coverage at this locus\">\n"
			+ "##INFO=<ID=HP,Number=1,Type=Integer,Description=\"Homopolymer run length around variant locus\">\n"
			+ "##INFO=<ID=WE,Number=1,Type=Integer,Description=\"End position of calling window\">\n"
			+ "##INFO=<ID=Source,Number=.,Type=String,Description=\"Was this variant suggested by Playtypus, Assembler, or from a VCF?\">\n"
			+ "##INFO=<ID=FS,Number=.,Type=Float,Description=\"Fisher's exact test for strand bias (Phred scale)\">\n"
			+ "##INFO=<ID=WS,Number=1,Type=Integer,Description=\"Starting position of calling window\">\n"
			+ "##INFO=<ID=PP,Number=.,Type=Float,Description=\"Posterior probability (phred scaled) that this variant segregates\">\n"
			+ "##INFO=<ID=TR,Number=.,Type=Integer,Description=\"Total number of reads containing this variant\">\n"
			+ "##INFO=<ID=NF,Number=.,Type=Integer,Description=\"Total number of forward reads containing this variant\">\n"
			+ "##INFO=<ID=TCF,Number=1,Type=Integer,Description=\"Total forward strand coverage at this locus\">\n"
			+ "##INFO=<ID=NR,Number=.,Type=Integer,Description=\"Total number of reverse reads containing this variant\">\n"
			+ "##INFO=<ID=TC,Number=1,Type=Integer,Description=\"Total coverage at this locus\">\n"
			+ "##INFO=<ID=END,Number=.,Type=Integer,Description=\"End position of reference call block\">\n"
			+ "##INFO=<ID=MGOF,Number=.,Type=Integer,Description=\"Worst goodness-of-fit value reported across all samples\">\n"
			+ "##INFO=<ID=SbPval,Number=.,Type=Float,Description=\"Binomial P-value for strand bias test\">\n"
			+ "##INFO=<ID=START,Number=.,Type=Integer,Description=\"Start position of reference call block\">\n"
			+ "##INFO=<ID=ReadPosRankSum,Number=.,Type=Float,Description=\"Mann-Whitney Rank sum test for difference between in positions of variants in reads from ref and alt\">\n"
			+ "##INFO=<ID=MQ,Number=.,Type=Float,Description=\"Root mean square of mapping qualities of reads at the variant position\">\n"
			+ "##INFO=<ID=QD,Number=1,Type=Float,Description=\"Variant-quality/read-depth for this variant\">\n"
			+ "##INFO=<ID=SC,Number=1,Type=String,Description=\"Genomic sequence 10 bases either side of variant position\">\n"
			+ "##INFO=<ID=BRF,Number=1,Type=Float,Description=\"Fraction of reads around this variant that failed filters\">\n"
			+ "##INFO=<ID=HapScore,Number=.,Type=Integer,Description=\"Haplotype score measuring the number of haplotypes the variant is segregating into in a window\">\n"
			+ "##INFO=<ID=Size,Number=.,Type=Integer,Description=\"Size of reference call block\">\n"
			+ "##FORMAT=<ID=GT,Number=1,Type=String,Description=\"Unphased genotypes\">\n"
			+ "##FORMAT=<ID=GQ,Number=1,Type=Integer,Description=\"Genotype quality as phred score\">\n"
			+ "##FORMAT=<ID=GOF,Number=.,Type=Float,Description=\"Goodness of fit value\">\n"
			+ "##FORMAT=<ID=NR,Number=.,Type=Integer,Description=\"Number of reads covering variant location in this sample\">\n"
			+ "##FORMAT=<ID=GL,Number=.,Type=Float,Description=\"Genotype log10-likelihoods for AA,AB and BB genotypes, where A = ref and B = variant. Only applicable for bi-allelic sites\">\n"
			+ "##FORMAT=<ID=NV,Number=.,Type=Integer,Description=\"Number of reads containing variant in this sample\">\n"
			+ "##FILTER=<ID=GOF,Description=\"Variant fails goodness-of-fit test.\">\n"
			+ "##FILTER=<ID=badReads,Description=\"Variant supported only by reads with low quality bases close to variant position, and not present on both strands.\">\n"
			+ "##FILTER=<ID=alleleBias,Description=\"Variant frequency is lower than expected for het\">\n"
			+ "##FILTER=<ID=hp10,Description=\"Flanking sequence contains homopolymer of length 10 or greater\">\n"
			+ "##FILTER=<ID=Q20,Description=\"Variant quality is below 20.\">\n"
			+ "##FILTER=<ID=HapScore,Description=\"Too many haplotypes are supported by the data in this region.\">\n"
			+ "##FILTER=<ID=MQ,Description=\"Root-mean-square mapping quality across calling region is low.\">\n"
			+ "##FILTER=<ID=strandBias,Description=\"Variant fails strand-bias filter\">\n"
			+ "##FILTER=<ID=SC,Description=\"Variants fail sequence-context filter. Surrounding sequence is low-complexity\">\n"
			+ "##FILTER=<ID=QualDepth,Description=\"Variant quality/Read depth ratio is low.\">\n"
			+ "##FILTER=<ID=REFCALL,Description=\"This line represents a homozygous reference call\">\n"
			+ "##FILTER=<ID=QD,Description=\"Variants fail quality/depth filter.\">\n";

}