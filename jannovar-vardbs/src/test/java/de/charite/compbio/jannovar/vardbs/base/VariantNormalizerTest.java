package de.charite.compbio.jannovar.vardbs.base;

import com.google.common.io.Files;
import de.charite.compbio.jannovar.utils.ResourceUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;

public class VariantNormalizerTest {

	static String fastaPath;
	static VariantNormalizer normalizer;

	@BeforeAll
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

		Assertions.assertEquals("braf", descOut.getChrom());
		Assertions.assertEquals(19, descOut.getPos());
		Assertions.assertEquals("G", descOut.getRef());
		Assertions.assertEquals("C", descOut.getAlt());
	}

	@Test
	public void testOnNormalInsertion() {
		// In VCF, indels are always given including the base left of it, this base will be stripped
		VariantDescription descIn = new VariantDescription("braf", 19, "G", "GCT");
		VariantDescription descOut = normalizer.normalizeVariant(descIn);

		Assertions.assertEquals("braf", descOut.getChrom());
		Assertions.assertEquals(19, descOut.getPos());
		Assertions.assertEquals("", descOut.getRef());
		Assertions.assertEquals("CT", descOut.getAlt());
	}

	@Test
	public void testShiftDeletionLeft() {
		// In VCF, indels are always given including the base left of it, this base will be stripped.
		VariantDescription descIn = new VariantDescription("braf", 180, "TGT", "T");
		VariantDescription descOut = normalizer.normalizeVariant(descIn);

		Assertions.assertEquals("braf", descOut.getChrom());
		Assertions.assertEquals(175, descOut.getPos());
		Assertions.assertEquals("TG", descOut.getRef());
		Assertions.assertEquals("", descOut.getAlt());
	}

	@Test
	public void testShiftInsertionLeft() {
		// In VCF, indels are always given including the base left of it, this base will be stripped.
		VariantDescription descIn = new VariantDescription("braf", 180, "T", "TGT");
		VariantDescription descOut = normalizer.normalizeVariant(descIn);

		Assertions.assertEquals("braf", descOut.getChrom());
		Assertions.assertEquals(175, descOut.getPos());
		Assertions.assertEquals("", descOut.getRef());
		Assertions.assertEquals("TG", descOut.getAlt());
	}

}
