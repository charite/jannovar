package de.charite.compbio.jannovar.vardbs.cosmic;

import java.io.File;
import java.io.PrintWriter;

import org.junit.Before;

import com.google.common.io.Files;

import de.charite.compbio.jannovar.utils.ResourceUtils;
import de.charite.compbio.jannovar.vardbs.base.DBAnnotationOptions;
import htsjdk.variant.vcf.VCFFileReader;

public class CosmicAnnotationDriverBaseTest {

	protected String cosmicVCFPath;
	protected String fastaPath;
	protected VCFFileReader vcfReader;
	protected DBAnnotationOptions options;

	@Before
	public void setUpClass() throws Exception {
		options = DBAnnotationOptions.createDefaults();
	
		// Setup dbSNP VCF file
		File tmpDir = Files.createTempDir();
		cosmicVCFPath = tmpDir + "/cosmic.vcf.gz";
		ResourceUtils.copyResourceToFile("/COSMIC.v72.fake.vcf.gz", new File(cosmicVCFPath));
		String tbiPath = tmpDir + "/cosmic.vcf.gz.tbi";
		ResourceUtils.copyResourceToFile("/COSMIC.v72.fake.vcf.gz.tbi", new File(tbiPath));
	
		// Setup reference FASTA file
		fastaPath = tmpDir + "/chr1.fasta";
		ResourceUtils.copyResourceToFile("/chr1.fasta", new File(fastaPath));
		String faiPath = tmpDir + "/chr1.fasta.fai";
		ResourceUtils.copyResourceToFile("/chr1.fasta.fai", new File(faiPath));
	
		// Header of VCF file
		String vcfHeader = "##fileformat=VCFv4.0\n"
				+ "#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\tFORMAT\tindividual\n";
	
		// Write out file to use in the test
		String testVCFPath = tmpDir + "/test_var_in_exac.vcf";
		PrintWriter writer = new PrintWriter(testVCFPath);
		writer.write(vcfHeader);
		writer.write("1\t714118\t.\tA\tC,G,T\t.\t.\t.\tGT\t0/1\n");
		writer.close();
	
		vcfReader = new VCFFileReader(new File(testVCFPath), false);
	}

}