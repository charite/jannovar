package de.charite.compbio.jannovar.vardbs.generic_tsv;

import htsjdk.samtools.util.CloseableIterator;
import htsjdk.variant.variantcontext.VariantContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

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

		Assertions.assertEquals(6, vcs.size());
		Assertions.assertEquals(
			"[VC null @ 1:91 Q. of type=SNP alleles=[A*, C] attr={AAREF=M, HG19POS=69091, RS_DBSNP147=., SIFT_SCORE=0.13} GT=[] filters=",
			vcs.get(0).toString());
		Assertions.assertEquals(
			"[VC null @ 1:91 Q. of type=SNP alleles=[A*, G] attr={AAREF=M, HG19POS=69091, RS_DBSNP147=., SIFT_SCORE=1.0} GT=[] filters=",
			vcs.get(1).toString());
		Assertions.assertEquals(
			"[VC null @ 1:91 Q. of type=SNP alleles=[A*, T] attr={AAREF=M, HG19POS=69091, RS_DBSNP147=., SIFT_SCORE=0.13} GT=[] filters=",
			vcs.get(2).toString());
		Assertions.assertEquals(
			"[VC null @ 1:92 Q. of type=SNP alleles=[T*, A] attr={AAREF=M, HG19POS=69092, RS_DBSNP147=., SIFT_SCORE=0.0} GT=[] filters=",
			vcs.get(3).toString());
		Assertions.assertEquals(
			"[VC null @ 1:92 Q. of type=SNP alleles=[T*, C] attr={AAREF=M, HG19POS=69092, RS_DBSNP147=., SIFT_SCORE=0.001} GT=[] filters=",
			vcs.get(4).toString());
		Assertions.assertEquals(
			"[VC null @ 1:92 Q. of type=SNP alleles=[T*, G] attr={AAREF=M, HG19POS=69092, RS_DBSNP147=., SIFT_SCORE=0.0} GT=[] filters=",
			vcs.get(5).toString());
	}

}
