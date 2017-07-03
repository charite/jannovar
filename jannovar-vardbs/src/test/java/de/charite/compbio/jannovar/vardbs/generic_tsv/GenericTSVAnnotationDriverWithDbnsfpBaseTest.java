package de.charite.compbio.jannovar.vardbs.generic_tsv;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import de.charite.compbio.jannovar.utils.ResourceUtils;
import de.charite.compbio.jannovar.vardbs.base.DBAnnotationOptions.MultipleMatchBehaviour;
import htsjdk.variant.vcf.VCFFileReader;
import htsjdk.variant.vcf.VCFHeaderLineType;
import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;

/**
 * Test base for generic TSV annotation, using part of dbNSFP (shifted 69k bases towards the left).
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class GenericTSVAnnotationDriverWithDbnsfpBaseTest {

	protected String genericTsvPath;
	protected String fastaPath;
	protected GenericTSVAnnotationOptions options;

	// File to annotate
	protected VCFFileReader vcfReader;

	@Before
	public void setUp() throws Exception {
		// Setup dbSNP VCF file
		File tmpDir = Files.createTempDir();
		genericTsvPath = tmpDir + "/dbNSFP.tsv.gz";
		ResourceUtils.copyResourceToFile("/dbNSFP3.4a_variant.fake.tsv.gz",
				new File(genericTsvPath));
		String tbiPath = tmpDir + "/dbNSFP.tsv.gz.tbi";
		ResourceUtils.copyResourceToFile("/dbNSFP3.4a_variant.fake.tsv.gz.tbi", new File(tbiPath));

		// Setup reference FASTA file
		fastaPath = tmpDir + "/chr1.fasta";
		ResourceUtils.copyResourceToFile("/chr1.fasta", new File(fastaPath));
		String faiPath = tmpDir + "/chr1.fasta.fai";
		ResourceUtils.copyResourceToFile("/chr1.fasta.fai", new File(faiPath));

		// Construct options
		Map<String, GenericTSVValueColumnDescription> descriptions = new HashMap<>();
		descriptions.put("AAREF",
				new GenericTSVValueColumnDescription(5, VCFHeaderLineType.Character, "AAREF",
						"Reference amino acid", GenericTSVAccumulationStrategy.CHOOSE_FIRST));
		descriptions.put("RS_DBSNP147",
				new GenericTSVValueColumnDescription(7, VCFHeaderLineType.String, "RS_DBSNP147",
						"ID in dbSNP v147", GenericTSVAccumulationStrategy.CHOOSE_FIRST));
		descriptions.put("HG19POS",
				new GenericTSVValueColumnDescription(9, VCFHeaderLineType.Integer, "HG19POS",
						"Position in hg19", GenericTSVAccumulationStrategy.CHOOSE_FIRST));
		descriptions.put("SIFT_SCORE",
				new GenericTSVValueColumnDescription(24, VCFHeaderLineType.Float, "SIFT_SCORE",
						"Sift Score", GenericTSVAccumulationStrategy.CHOOSE_MAX));
		this.options = new GenericTSVAnnotationOptions(true, false, "DBNSFP_",
				MultipleMatchBehaviour.BEST_ONLY, new File(genericTsvPath),
				GenericTSVAnnotationTarget.VARIANT, true, 1, 2, 2, 3, 4, true,
				ImmutableList.of("AAREF", "RS_DBSNP147", "HG19POS", "SIFT_SCORE"), descriptions);
		options.setReportOverlapping(true);
		options.setReportOverlappingAsMatching(false);

		// Write out file to use in the test
		String vcfHeader = "##fileformat=VCFv4.0\n"
				+ "#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\tFORMAT\tindividual\n";
		String testVCFPath = tmpDir + "/test_var_in_dbnsfp_tsv.vcf";
		PrintWriter writer = new PrintWriter(testVCFPath);
		writer.write(vcfHeader);
		writer.write("1\t119\t.\tT\tA,C,G\t.\t.\t.\tGT\t0/1\n");
		writer.close();

		vcfReader = new VCFFileReader(new File(testVCFPath), false);
	}

}