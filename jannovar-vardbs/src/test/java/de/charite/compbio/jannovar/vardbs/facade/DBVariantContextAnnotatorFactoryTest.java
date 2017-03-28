package de.charite.compbio.jannovar.vardbs.facade;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintWriter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.io.Files;

import de.charite.compbio.jannovar.utils.ResourceUtils;
import de.charite.compbio.jannovar.vardbs.base.DBAnnotationOptions;
import de.charite.compbio.jannovar.vardbs.base.JannovarVarDBException;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.writer.Options;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.variantcontext.writer.VariantContextWriterBuilder;
import htsjdk.variant.vcf.VCFFileReader;
import htsjdk.variant.vcf.VCFHeader;

/**
 * End-to-end smoke test for annotation with dbSNP
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class DBVariantContextAnnotatorFactoryTest {

	String pathDBVCF;
	String pathRefFASTA;
	DBAnnotationOptions options;
	VCFFileReader vcfReader;
	ByteArrayOutputStream outStream;

	@Before
	public void setUp() throws Exception {
		File tmpDir = Files.createTempDir();
		pathDBVCF = tmpDir + "/dbsnp.vcf.gz";
		ResourceUtils.copyResourceToFile("/dbSNP147.head.vcf.gz", new File(pathDBVCF));
		String pathDBTBI = tmpDir + "/dbsnp.vcf.gz.tbi";
		ResourceUtils.copyResourceToFile("/dbSNP147.head.vcf.gz.tbi", new File(pathDBTBI));

		pathRefFASTA = tmpDir + "/chr1.fasta";
		ResourceUtils.copyResourceToFile("/chr1.fasta", new File(pathRefFASTA));
		String pathRefFAI = tmpDir + "/chr1.fasta.fai";
		ResourceUtils.copyResourceToFile("/chr1.fasta.fai", new File(pathRefFAI));

		options = DBAnnotationOptions.createDefaults();

		// Header of VCF file
		String vcfHeader = "##fileformat=VCFv4.0\n"
				+ "#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\tFORMAT\tindividual\n";

		// Write out file to use in the test
		String testVCFPath = tmpDir + "/test_var_in_dbsnp.vcf";
		PrintWriter writer = new PrintWriter(testVCFPath);
		writer.write(vcfHeader);
		writer.write("1\t11022\t.\tG\tA\t.\t.\t.\tGT\t0/1\n");
		writer.close();

		vcfReader = new VCFFileReader(new File(testVCFPath), false);

		outStream = new ByteArrayOutputStream();
	}

	@Test
	public void test() throws JannovarVarDBException {
		DBVariantContextAnnotator annotator = new DBVariantContextAnnotatorFactory().constructDBSNP(pathDBVCF,
				pathRefFASTA, options);

		VariantContextWriter writer = new VariantContextWriterBuilder().setOutputStream(outStream)
				.unsetOption(Options.INDEX_ON_THE_FLY).build();
		writer.writeHeader(annotator.extendHeader(new VCFHeader(vcfReader.getFileHeader())));
		for (VariantContext vc : vcfReader)
			writer.add(annotator.annotateVariantContext(vc));

		Assert.assertEquals(
				"##fileformat=VCFv4.2\n"
						+ "##INFO=<ID=CAF,Number=R,Type=Float,Description=\"Allele frequencies from dbSNP. "
						+ "Original description: An ordered, comma delimited list of allele frequencies based "
						+ "on 1000Genomes, starting with the reference allele followed by alternate alleles as "
						+ "ordered in the ALT column. Where a 1000Genomes alternate allele is not in the dbSNPs "
						+ "alternate allele set, the allele is added to the ALT column.  The minor allele is the "
						+ "second largest value in the list, and was previuosly reported in VCF as the GMAF.  "
						+ "This is the GMAF reported on the RefSNP and EntrezSNP pages and VariationReporter\">\n"
						+ "##INFO=<ID=COMMON,Number=A,Type=Integer,Description=\"Flagged as common in dbSNP. "
						+ "Original description: RS is a common SNP.  A common SNP is one that has at least one "
						+ "1000Genomes population with a minor allele of frequency >= 1% and for which 2 or more "
						+ "founders contribute to that minor allele frequency.\">\n"
						+ "##INFO=<ID=G5,Number=A,Type=Integer,Description=\"Allele frequency >5% in all populations "
						+ "from dbSNP (yes: 1, no: 0). Original description: >5% minor allele frequency in "
						+ "1+ populations\">\n"
						+ "##INFO=<ID=G5A,Number=A,Type=Integer,Description=\"Allele frequency >5% in all populations "
						+ "from dbSNP (yes: 1, no: 0). Original description: >5% minor allele frequency in each and "
						+ "all populations\">\n"
						+ "##INFO=<ID=IDS,Number=A,Type=String,Description=\"dbSNP cluster identifiers with matching "
						+ "alternative positions and alleles, for each alternative alleles, separated by '|'\">\n"
						+ "##INFO=<ID=OVL_CAF,Number=R,Type=Float,Description=\"Allele frequencies from dbSNP (requiring "
						+ "no genotype match, only position overlap). Original description: An ordered, comma delimited "
						+ "list of allele frequencies based on 1000Genomes, starting with the reference allele followed "
						+ "by alternate alleles as ordered in the ALT column. Where a 1000Genomes alternate allele is not "
						+ "in the dbSNPs alternate allele set, the allele is added to the ALT column.  The minor allele is "
						+ "the second largest value in the list, and was previuosly reported in VCF as the GMAF.  This is "
						+ "the GMAF reported on the RefSNP and EntrezSNP pages and VariationReporter\">\n"
						+ "##INFO=<ID=OVL_COMMON,Number=A,Type=Integer,Description=\"Flagged as common in dbSNP (requiring "
						+ "no genotype match, only position overlap). Original description: RS is a common SNP.  A common "
						+ "SNP is one that has at least one 1000Genomes population with a minor allele of frequency >= 1% "
						+ "and for which 2 or more founders contribute to that minor allele frequency.\">\n"
						+ "##INFO=<ID=OVL_G5,Number=A,Type=Integer,Description=\"Allele frequency >5% in all populations "
						+ "from dbSNP (yes: 1, no: 0) (requiring no genotype match, only position overlap). Original "
						+ "description: >5% minor allele frequency in 1+ populations\">\n"
						+ "##INFO=<ID=OVL_G5A,Number=A,Type=Integer,Description=\"Allele frequency >5% in all populations "
						+ "from dbSNP (yes: 1, no: 0) (requiring no genotype match, only position overlap). Original "
						+ "description: >5% minor allele frequency in each and all populations\">\n"
						+ "##INFO=<ID=OVL_IDS,Number=A,Type=String,Description=\"dbSNP cluster identifiers with overlapping "
						+ "alternative positions, not necessarily matching alleles, for each alternative allele, "
						+ "separated '|'\">\n"
						+ "##INFO=<ID=OVL_SAO,Number=A,Type=String,Description=\"Variant allele origin (UNSPECIFIED, GERMLINE, SOMATIC, BOTH)\">\n"
						+ "##INFO=<ID=SAO,Number=A,Type=String,Description=\"Variant allele origin (UNSPECIFIED, GERMLINE, SOMATIC, BOTH)\">\n"
						+ "#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\tFORMAT\tindividual\n"
						+ "1\t11022\trs28775022\tG\tA\t.\t.\tIDS=rs28775022;OVL_IDS=rs28775022\tGT\t0/1\n",
				outStream.toString());
	}

}
