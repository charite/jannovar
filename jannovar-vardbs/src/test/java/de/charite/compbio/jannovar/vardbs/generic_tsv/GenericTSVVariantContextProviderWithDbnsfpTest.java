package de.charite.compbio.jannovar.vardbs.generic_tsv;

import htsjdk.samtools.util.CloseableIterator;
import htsjdk.variant.variantcontext.VariantContext;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

public class GenericTSVVariantContextProviderWithDbnsfpTest extends GenericTSVAnnotationDriverWithDbnsfpBaseTest {

	@Test
	public void test() {
		GenericTSVVariantContextProvider provider = new GenericTSVVariantContextProvider(options);

		List<VariantContext> vcs = new ArrayList<>();
		try (CloseableIterator<VariantContext> it = provider.query("1", 90, 92)) {
			while (it.hasNext()) {
				vcs.add(it.next());
			}
		}

		Assert.assertEquals(6, vcs.size());
		Assert.assertEquals(
				"[VC null @ 1:91 Q. of type=SNP alleles=[A*, C] attr={AAREF=M, HG19POS=69091, RS_DBSNP147=., SIFT_SCORE=0.13} GT=[]",
				vcs.get(0).toString());
		Assert.assertEquals(
				"[VC null @ 1:91 Q. of type=SNP alleles=[A*, G] attr={AAREF=M, HG19POS=69091, RS_DBSNP147=., SIFT_SCORE=1.0} GT=[]",
				vcs.get(1).toString());
		Assert.assertEquals(
				"[VC null @ 1:91 Q. of type=SNP alleles=[A*, T] attr={AAREF=M, HG19POS=69091, RS_DBSNP147=., SIFT_SCORE=0.13} GT=[]",
				vcs.get(2).toString());
		Assert.assertEquals(
				"[VC null @ 1:92 Q. of type=SNP alleles=[T*, A] attr={AAREF=M, HG19POS=69092, RS_DBSNP147=., SIFT_SCORE=0.0} GT=[]",
				vcs.get(3).toString());
		Assert.assertEquals(
				"[VC null @ 1:92 Q. of type=SNP alleles=[T*, C] attr={AAREF=M, HG19POS=69092, RS_DBSNP147=., SIFT_SCORE=0.001} GT=[]",
				vcs.get(4).toString());
		Assert.assertEquals(
				"[VC null @ 1:92 Q. of type=SNP alleles=[T*, G] attr={AAREF=M, HG19POS=69092, RS_DBSNP147=., SIFT_SCORE=0.0} GT=[]",
				vcs.get(5).toString());
	}

}
