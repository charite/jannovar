package de.charite.compbio.jannovar.vardbs.uk10k;

import java.io.File;
import java.io.PrintWriter;

import org.junit.Before;

import com.google.common.io.Files;

import de.charite.compbio.jannovar.utils.ResourceUtils;
import de.charite.compbio.jannovar.vardbs.base.DBAnnotationOptions;
import htsjdk.variant.vcf.VCFFileReader;

public class UK10KAnnotationDriverBaseTest {

	protected String dbUK10KVCFPath;
	protected String fastaPath;
	protected VCFFileReader vcfReader;
	protected DBAnnotationOptions options;

	@Before
	public void setUpClass() throws Exception {
		options = DBAnnotationOptions.createDefaults();
	
		// Setup dbSNP VCF file
		File tmpDir = Files.createTempDir();
		dbUK10KVCFPath = tmpDir + "/uk10k.vcf.gz";
		ResourceUtils.copyResourceToFile("/UK10K_COHORT.20160215.sites.head.vcf.gz", new File(dbUK10KVCFPath));
		String tbiPath = tmpDir + "/uk10k.vcf.gz.tbi";
		ResourceUtils.copyResourceToFile("/UK10K_COHORT.20160215.sites.head.vcf.gz.tbi", new File(tbiPath));
	
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
		writer.write("1\t714118\t.\tC\tA,G,T\t.\t.\t.\tGT\t0/1\n");
		writer.close();
	
		vcfReader = new VCFFileReader(new File(testVCFPath), false);
	}

}