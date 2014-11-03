package jannovar.annotation;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Helper for creating temporary VCF files, reading VariantContext objects from them and then closing and deleting these
 * files again.
 *
 * Protocol:
 *
 * <pre>
 * VariantContextGenerator generator = new VariantContextGenerator();
 * String s = &quot;1	248637422	.	C	CTTC	100	PASS	QD=11.71;	GT:GQ	0/1:99	0/0:99	0/1:99	0/0:99	0/1:99&quot;;
 * try {
 * 	VariantContext context = generator.generateForLine(s);
 * } finally {
 * 	context.close();
 * }
 * </pre>
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class VariantContextGenerator {

	/** Temporary file. */
	private File file = null;
	/** VCFFileReader to use for reading. */
	private VCFFileReader parser;

	/**
	 * Create corrected variant data corrector for one VCF line.
	 *
	 * @param s
	 *            VCF line to parse
	 * @return corrected variant data
	 */
	public VariantDataCorrector correctedVarDataForLine(String s) throws IOException {
		VariantContext vc = generateForLine(s);

		VariantDataCorrector corr = new VariantDataCorrector(vc.getReference().getBaseString(), vc
				.getAlternateAllele(0).getBaseString(), vc.getStart());
		return corr;
	}

	/**
	 * Generate a VariantContext from VCF file.
	 *
	 * The VCF file is created by concatenating static header to line.
	 *
	 * @param line
	 *            one VCF line to parse
	 * @return VariantContext for first line
	 * @throws IOException
	 *             in the case that something went wrong
	 */
	public VariantContext generateForLine(String line) throws IOException {
		if (file != null)
			close();

		file = temporaryVCFFile(line);
		parser = new VCFFileReader(file, /* requireIndex= */false);
		VariantContext result = null;
		for (VariantContext ctx : parser) {
			result = ctx;
			break;
		}
		if (result == null)
			throw new IOException("Could not read VCFFile");
		return result;
	}

	/** Close temporary file again */
	public void close() {
		parser.close();
		file.delete();
	}

	/**
	 * Write out VCF file with the given line to a temporary file.
	 *
	 * The file will be marked as delete on exit.
	 *
	 * @return temporary file with this content
	 * @throws IOException
	 */
	private File temporaryVCFFile(String line) throws IOException {
		file = File.createTempFile("test", "vcf");
		file.deleteOnExit();

		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file));
			writer.write(getFileContents(line));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (writer != null)
				writer.close();
		}
		return file;
	}

	/** @return file contents with header prepended to line */
	private String getFileContents(String line) {
		return getHeader() + line + "\n";
	}

	/** @return VCF header */
	private String getHeader() {
		return "##fileformat=VCFv4.1\n"
				+ "##FILTER=<ID=LowQual,Description=\"Low quality\">\n"
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
				+ "##INFO=<ID=STR,Number=0,Type=Flag,Description=\"Variant is a short tandem repeat\">\n"
				+ "##contig=<ID=chr1,length=249250621,assembly=hg19>\n"
				+ "##contig=<ID=chr2,length=243199373,assembly=hg19>\n"
				+ "##contig=<ID=chr3,length=198022430,assembly=hg19>\n"
				+ "##contig=<ID=chr4,length=191154276,assembly=hg19>\n"
				+ "##contig=<ID=chr5,length=180915260,assembly=hg19>\n"
				+ "##contig=<ID=chr6,length=171115067,assembly=hg19>\n"
				+ "##contig=<ID=chr7,length=159138663,assembly=hg19>\n"
				+ "##contig=<ID=chr8,length=146364022,assembly=hg19>\n"
				+ "##contig=<ID=chr9,length=141213431,assembly=hg19>\n"
				+ "##contig=<ID=chr10,length=135534747,assembly=hg19>\n"
				+ "##contig=<ID=chr11,length=135006516,assembly=hg19>\n"
				+ "##contig=<ID=chr12,length=133851895,assembly=hg19>\n"
				+ "##contig=<ID=chr13,length=115169878,assembly=hg19>\n"
				+ "##contig=<ID=chr14,length=107349540,assembly=hg19>\n"
				+ "##contig=<ID=chr15,length=102531392,assembly=hg19>\n"
				+ "##contig=<ID=chr16,length=90354753,assembly=hg19>\n"
				+ "##contig=<ID=chr17,length=81195210,assembly=hg19>\n"
				+ "##contig=<ID=chr18,length=78077248,assembly=hg19>\n"
				+ "##contig=<ID=chr19,length=59128983,assembly=hg19>\n"
				+ "##contig=<ID=chr20,length=63025520,assembly=hg19>\n"
				+ "##contig=<ID=chr21,length=48129895,assembly=hg19>\n"
				+ "##contig=<ID=chr22,length=51304566,assembly=hg19>\n"
				+ "##contig=<ID=chrX,length=155270560,assembly=hg19>\n"
				+ "##contig=<ID=chrY,length=59373566,assembly=hg19>\n"
				+ "##contig=<ID=chrM,length=16571,assembly=hg19>\n"
				+ "##reference=file:///home/parkhomc/hg19/hg19_chrs_sorted/hg19.fa\n"
				+ "#CHROM	POS	ID	REF	ALT	QUAL	FILTER	INFO	FORMAT	OT37	OT39	OT41	OT43	OT47	OT51\n";
	}
}
