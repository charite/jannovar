package de.charite.compbio.jannovar.vardbs.exac;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.io.Files;

import de.charite.compbio.jannovar.utils.ResourceUtils;
import de.charite.compbio.jannovar.vardbs.base.DBAnnotationOptions;
import de.charite.compbio.jannovar.vardbs.base.JannovarVarDBException;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;
import htsjdk.variant.vcf.VCFHeader;

public class ExacAnnotationDriverDefaultOptionsTest {

	// Path to dbSNP VCF file
	String dbExacVCFPath;
	// Path to reference FASTA file
	String fastaPath;
	// VCF reader for file to be used in the test
	VCFFileReader vcfReader;
	// Configuration to use in the tests
	DBAnnotationOptions options;

	@Before
	public void setUpClass() throws Exception {
		options = DBAnnotationOptions.createDefaults();

		// Setup dbSNP VCF file
		File tmpDir = Files.createTempDir();
		dbExacVCFPath = tmpDir + "/exac.vcf.gz";
		ResourceUtils.copyResourceToFile("/ExAC.r0.3.sites.vep.head.vcf.gz", new File(dbExacVCFPath));
		String tbiPath = tmpDir + "/exac.vcf.gz.tbi";
		ResourceUtils.copyResourceToFile("/ExAC.r0.3.sites.vep.head.vcf.gz.tbi", new File(tbiPath));

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
		writer.write("1\t13482\t.\tG\tA,C,T\t.\t.\t.\tGT\t0/1\n");
		writer.close();

		vcfReader = new VCFFileReader(new File(testVCFPath), false);
	}

	@Test
	public void testAnnotateExtendHeaderWithDefaultPrefix() throws JannovarVarDBException {
		ExacAnnotationDriver driver = new ExacAnnotationDriver(dbExacVCFPath, fastaPath, options);

		VCFHeader header = vcfReader.getFileHeader();

		// Check header before extension
		Assert.assertEquals(0, header.getFilterLines().size());
		Assert.assertEquals(0, header.getInfoHeaderLines().size());
		Assert.assertEquals(0, header.getFormatHeaderLines().size());
		Assert.assertEquals(0, header.getIDHeaderLines().size());
		Assert.assertEquals(0, header.getOtherHeaderLines().size());

		driver.constructVCFHeaderExtender().addHeaders(header);

		// Check header after extension
		Assert.assertEquals(0, header.getFilterLines().size());
		Assert.assertEquals(26, header.getInfoHeaderLines().size());
		Assert.assertEquals(0, header.getFormatHeaderLines().size());
		Assert.assertEquals(26, header.getIDHeaderLines().size());
		Assert.assertEquals(0, header.getOtherHeaderLines().size());

		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AN_AFR"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AC_AFR"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AF_AFR"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AN_AMR"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AC_AMR"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AF_AMR"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AN_EAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AC_EAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AF_EAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AN_FIN"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AC_FIN"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AF_FIN"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AN_NFE"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AC_NFE"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AF_NFE"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AN_OTH"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AC_OTH"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AF_OTH"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AN_SAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AC_ALL"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AF_ALL"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AN_ALL"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_BEST_AC"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_BEST_AF"));
	}

	@Test
	public void testAnnotateVariantContext() throws JannovarVarDBException {
		DBAnnotationOptions options = DBAnnotationOptions.createDefaults();
		ExacAnnotationDriver driver = new ExacAnnotationDriver(dbExacVCFPath, fastaPath, options);
		VariantContext vc = vcfReader.iterator().next();

		Assert.assertEquals(0, vc.getAttributes().size());
		Assert.assertEquals(".", vc.getID());

		VariantContext annotated = driver.annotateVariantContext(vc);

		Assert.assertEquals(".", annotated.getID());

		Assert.assertEquals(26, annotated.getAttributes().size());
		ArrayList<String> keys = Lists.newArrayList(annotated.getAttributes().keySet());
		Collections.sort(keys);
		Assert.assertEquals("[AC_AFR, AC_ALL, AC_AMR, AC_EAS, AC_FIN, AC_NFE, AC_OTH, AC_SAS, AF_AFR, AF_ALL, "
				+ "AF_AMR, AF_EAS, AF_FIN, AF_NFE, AF_OTH, AF_SAS, AN_AFR, AN_ALL, AN_AMR, "
				+ "AN_EAS, AN_FIN, AN_NFE, AN_OTH, AN_SAS, BEST_AC, BEST_AF]", keys.toString());

		Assert.assertEquals("[0, 0, 2, 0]", annotated.getAttributeAsString("AC_AFR", null));
		Assert.assertEquals("[0, 0, 2, 0]", annotated.getAttributeAsString("AC_ALL", null));
		Assert.assertEquals("[0, 0, 0, 0]", annotated.getAttributeAsString("AC_AMR", null));
		Assert.assertEquals("[0, 0, 0, 0]", annotated.getAttributeAsString("AC_EAS", null));
		Assert.assertEquals("[0, 0, 0, 0]", annotated.getAttributeAsString("AC_FIN", null));
		Assert.assertEquals("[0, 0, 0, 0]", annotated.getAttributeAsString("AC_NFE", null));
		Assert.assertEquals("[0, 0, 0, 0]", annotated.getAttributeAsString("AC_OTH", null));
		Assert.assertEquals("[0, 0, 0, 0]", annotated.getAttributeAsString("AC_SAS", null));

		Assert.assertEquals("[0.0, 0.0, 0.003194888178913738, 0.0]", annotated.getAttributeAsString("AF_AFR", null));
		Assert.assertEquals("[0.0, 0.0, 1.7041581458759374E-4, 0.0]", annotated.getAttributeAsString("AF_ALL", null));
		Assert.assertEquals("[0.0, 0.0, 0.0, 0.0]", annotated.getAttributeAsString("AF_AMR", null));
		Assert.assertEquals("[0.0, 0.0, 0.0, 0.0]", annotated.getAttributeAsString("AF_EAS", null));
		Assert.assertEquals("[0.0, 0.0, 0.0, 0.0]", annotated.getAttributeAsString("AF_FIN", null));
		Assert.assertEquals("[0.0, 0.0, 0.0, 0.0]", annotated.getAttributeAsString("AF_NFE", null));
		Assert.assertEquals("[0.0, 0.0, 0.0, 0.0]", annotated.getAttributeAsString("AF_OTH", null));
		Assert.assertEquals("[0.0, 0.0, 0.0, 0.0]", annotated.getAttributeAsString("AF_SAS", null));

		Assert.assertEquals("626", annotated.getAttributeAsString("AN_AFR", null));
		Assert.assertEquals("11736", annotated.getAttributeAsString("AN_ALL", null));
		Assert.assertEquals("148", annotated.getAttributeAsString("AN_AMR", null));
		Assert.assertEquals("218", annotated.getAttributeAsString("AN_EAS", null));
		Assert.assertEquals("24", annotated.getAttributeAsString("AN_FIN", null));
		Assert.assertEquals("3078", annotated.getAttributeAsString("AN_NFE", null));
		Assert.assertEquals("122", annotated.getAttributeAsString("AN_OTH", null));
		Assert.assertEquals("7520", annotated.getAttributeAsString("AN_SAS", null));

		Assert.assertEquals("[0, 2]", annotated.getAttributeAsString("BEST_AC", null));
		Assert.assertEquals("[0.0, 0.003194888178913738]", annotated.getAttributeAsString("BEST_AF", null));
	}

}
