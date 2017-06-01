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
 * Test base for generic TSV annotation, using the head of ReMM v0.3.1
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class GenericTSVAnnotationDriverWithRemmBaseTest {

	protected String genericTsvPath;
	protected String fastaPath;
	protected GenericTSVAnnotationOptions options;

	// File to annotate
	protected VCFFileReader vcfReader;

	@Before
	public void setUp() throws Exception {
		// Setup dbSNP VCF file
		File tmpDir = Files.createTempDir();
		genericTsvPath = tmpDir + "/ReMM.v0.3.1.head.tsv.gz";
		ResourceUtils.copyResourceToFile("/ReMM.v0.3.1.head.tsv.gz", new File(genericTsvPath));
		String tbiPath = tmpDir + "/ReMM.v0.3.1.head.tsv.gz.tbi";
		ResourceUtils.copyResourceToFile("/ReMM.v0.3.1.head.tsv.gz.tbi", new File(tbiPath));

		// Setup reference FASTA file
		fastaPath = tmpDir + "/chr1.fasta";
		ResourceUtils.copyResourceToFile("/chr1.fasta", new File(fastaPath));
		String faiPath = tmpDir + "/chr1.fasta.fai";
		ResourceUtils.copyResourceToFile("/chr1.fasta.fai", new File(faiPath));

		// Construct options
		Map<String, GenericTSVValueColumnDescription> descriptions = new HashMap<>();
		descriptions.put("REMM_SCORE", new GenericTSVValueColumnDescription(3, VCFHeaderLineType.Float, "REMM_SCORE",
				"ReMM Score", GenericTSVAccumulationStrategy.CHOOSE_MAX));
		this.options = new GenericTSVAnnotationOptions(true, false, "", MultipleMatchBehaviour.BEST_ONLY,
				new File(genericTsvPath), GenericTSVAnnotationTarget.VARIANT, true, 1, 2, 2, 0, 0, true,
				ImmutableList.of("REMM_SCORE"), descriptions);
		options.setReportOverlapping(true);
		options.setReportOverlappingAsMatching(true);

		// Write out file to use in the test
		String vcfHeader = "##fileformat=VCFv4.0\n"
				+ "#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\tFORMAT\tindividual\n";
		String testVCFPath = tmpDir + "/test_var_in_dbnsfp_tsv.vcf";
		PrintWriter writer = new PrintWriter(testVCFPath);
		writer.write(vcfHeader);
		writer.write("1\t10026\t.\tT\tA,C,G\t.\t.\t.\tGT\t0/1\n");
		writer.write("1\t10032\t.\tTA\tT\t.\t.\t.\tGT\t0/1\n"); // 1033 has higher score
		writer.close();

		vcfReader = new VCFFileReader(new File(testVCFPath), false);
	}

}