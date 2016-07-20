package de.charite.compbio.jannovar.vardbs.base;

import java.io.File;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.io.Files;

import de.charite.compbio.jannovar.utils.ResourceUtils;

public class VariantNormalizerTest {

	static String fastaPath;
	static VariantNormalizer normalizer;

	@BeforeClass
	public static void setUpClass() throws Exception {
		File tmpDir = Files.createTempDir();
		fastaPath = tmpDir + "/braf.fasta";
		ResourceUtils.copyResourceToFile("/braf.fasta", new File(fastaPath));
		ResourceUtils.copyResourceToFile("/braf.fasta.fai", new File(fastaPath + ".fai"));

		normalizer = new VariantNormalizer(fastaPath);
	}

	@Test
	public void testSNV() {
		VariantDescription descIn = new VariantDescription("braf", 19, "G", "C");
		VariantDescription descOut = normalizer.normalizeVariant(descIn);

		Assert.assertEquals("braf", descOut.getChrom());
		Assert.assertEquals(19, descOut.getPos());
		Assert.assertEquals("G", descOut.getRef());
		Assert.assertEquals("C", descOut.getAlt());
	}

	@Test
	public void testOnNormalInsertion() {
		// In VCF, indels are always given including the base left of it, this base will be stripped
		VariantDescription descIn = new VariantDescription("braf", 19, "G", "GCT");
		VariantDescription descOut = normalizer.normalizeVariant(descIn);

		Assert.assertEquals("braf", descOut.getChrom());
		Assert.assertEquals(19, descOut.getPos());
		Assert.assertEquals("", descOut.getRef());
		Assert.assertEquals("CT", descOut.getAlt());
	}

	@Test
	public void testShiftDeletionLeft() {
		// In VCF, indels are always given including the base left of it, this base will be stripped.
		VariantDescription descIn = new VariantDescription("braf", 180, "TGT", "T");
		VariantDescription descOut = normalizer.normalizeVariant(descIn);

		Assert.assertEquals("braf", descOut.getChrom());
		Assert.assertEquals(175, descOut.getPos());
		Assert.assertEquals("TG", descOut.getRef());
		Assert.assertEquals("", descOut.getAlt());
	}

	@Test
	public void testShiftInsertionLeft() {
		// In VCF, indels are always given including the base left of it, this base will be stripped.
		VariantDescription descIn = new VariantDescription("braf", 180, "T", "TGT");
		VariantDescription descOut = normalizer.normalizeVariant(descIn);

		Assert.assertEquals("braf", descOut.getChrom());
		Assert.assertEquals(175, descOut.getPos());
		Assert.assertEquals("", descOut.getRef());
		Assert.assertEquals("TG", descOut.getAlt());
	}

}
