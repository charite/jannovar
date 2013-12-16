/**
 * 
 */
package jannovar.annotation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import jannovar.common.Constants;
import jannovar.common.VariantType;
import jannovar.exception.AnnotationException;
import jannovar.exception.JannovarException;
import jannovar.io.SerializationManager;
import jannovar.reference.Chromosome;
import jannovar.reference.TranscriptModel;

/**
 * @author mjaeger
 * 
 */
public class DuplicationAnnotationTest implements Constants {

	private static HashMap<Byte, Chromosome> chromosomeMap = null;

	@BeforeClass
	public static void setUp() throws IOException, JannovarException {
		ArrayList<TranscriptModel> kgList = null;
		java.net.URL url = SynonymousAnnotationTest.class.getResource(UCSCserializationTestFileName);
		String path = url.getPath();
		SerializationManager manager = new SerializationManager();
		kgList = manager.deserializeKnownGeneList(path);
		chromosomeMap = Chromosome.constructChromosomeMapWithIntervalTree(kgList);
	}

	@AfterClass
	public static void releaseResources() {
		chromosomeMap = null;
		System.gc();
	}

	/**
	 * <P>
	 * This is the test for the in-frame duplication of a single triplicate /
	 * one amino acids '+' strand
	 * </P>
	 */
	@Test
	public void testDuplicationVar1() throws AnnotationException {
		byte chr = 1;
		int pos = 248637423;
		String ref = "-";
		String alt = "TTC";
		Chromosome c = chromosomeMap.get(chr);
		if (c == null) {
			Assert.fail("Could not identify chromosome \"" + chr + "\"");
		} else {
			AnnotationList ann = c.getAnnotationList(pos, ref, alt);
			VariantType varType = ann.getVariantType();
			Assert.assertEquals(VariantType.NON_FS_DUPLICATION, varType);
			String annot = ann.getVariantAnnotation();
			Assert.assertEquals("OR2T3(uc001iel.1:exon1:c.769_771dupTTC:p.F257dup)", annot);
		}
	}

	/**
	 * <P>
	 * This is the test for the in-frame duplication of six nuc.acids / two
	 * amino acids '+' strand
	 * </P>
	 */
	@Test
	public void testDuplicationVar2() throws AnnotationException {
		byte chr = 1;
		int pos = 248637423;
		String ref = "-";
		String alt = "CTCTTC";
		Chromosome c = chromosomeMap.get(chr);
		if (c == null) {
			Assert.fail("Could not identify chromosome \"" + chr + "\"");
		} else {
			AnnotationList ann = c.getAnnotationList(pos, ref, alt);
			VariantType varType = ann.getVariantType();
			Assert.assertEquals(VariantType.NON_FS_DUPLICATION, varType);
			String annot = ann.getVariantAnnotation();
			Assert.assertEquals("OR2T3(uc001iel.1:exon1:c.766_771dupCTCTTC:p.L256_F257dup)", annot);
		}
	}

	/**
	 * <P>
	 * This is the test for the in-frame duplication of 12 nuc.acids / tree
	 * amino acids '+' strand
	 * </P>
	 */
	@Test
	public void testDuplicationVar3() throws AnnotationException {
		byte chr = 1;
		int pos = 248637423;
		String ref = "-";
		String alt = "CTGCTGCTCTTC";
		Chromosome c = chromosomeMap.get(chr);
		if (c == null) {
			Assert.fail("Could not identify chromosome \"" + chr + "\"");
		} else {
			AnnotationList ann = c.getAnnotationList(pos, ref, alt);
			VariantType varType = ann.getVariantType();
			Assert.assertEquals(VariantType.NON_FS_DUPLICATION, varType);
			String annot = ann.getVariantAnnotation();
			Assert.assertEquals("OR2T3(uc001iel.1:exon1:c.760_771dupCTGCTGCTCTTC:p.L254_F257dup)", annot);
		}
	}

	/**
	 * <P>
	 * This is the test for the in-frame duplication of a single triplicate /
	 * one amino acids '-' strand
	 * </P>
	 */

	@Test
	public void testDuplicationVar4() throws AnnotationException {
		byte chr = 3;
		int pos = 184429172;
		String ref = "-";
		String alt = "GTT";
		Chromosome c = chromosomeMap.get(chr);
		if (c == null) {
			Assert.fail("Could not identify chromosome \"" + chr + "\"");
		} else {
			AnnotationList ann = c.getAnnotationList(pos, ref, alt);
			VariantType varType = ann.getVariantType();
			Assert.assertEquals(VariantType.NON_FS_DUPLICATION, varType);
			String annot = ann.getVariantAnnotation();
			Assert.assertEquals("MAGEF1(uc003fpa.3:exon1:c.439_441dupAAC:p.N147dup)", annot);
		}
	}

	/**
	 * <P>
	 * This is the test for the in-frame duplication of 6 nuc.acids / two amino
	 * acids '-' strand
	 * </P>
	 */
	@Test
	public void testDuplicationVar5() throws AnnotationException {
		byte chr = 3;
		int pos = 184429172;
		String ref = "-";
		String alt = "TTTGTT";
		Chromosome c = chromosomeMap.get(chr);
		if (c == null) {
			Assert.fail("Could not identify chromosome \"" + chr + "\"");
		} else {
			AnnotationList ann = c.getAnnotationList(pos, ref, alt);
			VariantType varType = ann.getVariantType();
			Assert.assertEquals(VariantType.NON_FS_DUPLICATION, varType);
			String annot = ann.getVariantAnnotation();
			Assert.assertEquals("MAGEF1(uc003fpa.3:exon1:c.439_444dupAACAAA:p.N147_K148dup)", annot);
		}
	}

	/**
	 * <P>
	 * This is the test for the in-frame duplication of 12 nuc.acids / three
	 * amino acids '-' strand
	 * </P>
	 */
	@Test
	public void testDuplicationVar6() throws AnnotationException {
		byte chr = 3;
		int pos = 184429172;
		String ref = "-";
		String alt = "TTTTAGTTTGTT";
		Chromosome c = chromosomeMap.get(chr);
		if (c == null) {
			Assert.fail("Could not identify chromosome \"" + chr + "\"");
		} else {
			AnnotationList ann = c.getAnnotationList(pos, ref, alt);
			VariantType varType = ann.getVariantType();
			Assert.assertEquals(VariantType.NON_FS_DUPLICATION, varType);
			String annot = ann.getVariantAnnotation();
			Assert.assertEquals("MAGEF1(uc003fpa.3:exon1:c.439_450dupAACAAACTAAAA:p.N147_K150dup)", annot);
		}
	}

	/**
	 * <P>
	 * This is the test for the offset (+1) duplication of a single triplicate /
	 * one amino acids shifting the Stop-codon
	 * '+' strand
	 * </P>
	 */
	@Test
	public void testDuplicationVar7() throws AnnotationException {
		byte chr = 1;
		int pos = 248637606;
		String ref = "-";
		String alt = "GAAAAG";
		Chromosome c = chromosomeMap.get(chr);
		if (c == null) {
			Assert.fail("Could not identify chromosome \"" + chr + "\"");
		} else {
			AnnotationList ann = c.getAnnotationList(pos, ref, alt);
			VariantType varType = ann.getVariantType();
			Assert.assertEquals(VariantType.NON_FS_DUPLICATION, varType);
			String annot = ann.getVariantAnnotation();
			Assert.assertEquals("OR2T3(uc001iel.1:exon1:c.949_954dupGAAAAG:p.E317_K318dup)", annot);
		}
	}
	
	/**
	 * <P>
	 * This is the test for the offset (+2) duplication of a single triplicate /
	 * one amino acids shifting the Stop-codon
	 * '+' strand
	 * </P>
	 */
	@Test
	public void testDuplicationVar8() throws AnnotationException {
		byte chr = 1;
		int pos = 248637607;
		String ref = "-";
		String alt = "AAAAGT";
		Chromosome c = chromosomeMap.get(chr);
		if (c == null) {
			Assert.fail("Could not identify chromosome \"" + chr + "\"");
		} else {
			AnnotationList ann = c.getAnnotationList(pos, ref, alt);
			VariantType varType = ann.getVariantType();
			Assert.assertEquals(VariantType.NON_FS_DUPLICATION, varType);
			String annot = ann.getVariantAnnotation();
			Assert.assertEquals("OR2T3(uc001iel.1:exon1:c.950_955dupAAAAGT:p.*319delins*)", annot);
		}
	}
	
	/**
	 * <P>
	 * This is the test for the in-frame duplication of a single triplicate /
	 * one amino acids
	 * '+' strand
	 * </P>
	 */	
	@Test public void testDuplicationVar9test() throws AnnotationException  {
	    byte chr = 9;
	    int pos = 137968919;
	    String ref = "-";
	    String alt = "AGA";
	    Chromosome c = chromosomeMap.get(chr); 
	    if (c==null) {
		Assert.fail("Could not identify chromosome \"" + chr + "\"");
	    } else {
		AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.NON_FS_DUPLICATION,varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("OLFM1(uc010naq.2:exon2:c.325_327dupAGA:p.R109dup)",annot);
	    }
	}

	/**
	 * <P>
	 * This is the test for the offset (+1) duplication of a single triplicate /
	 * one amino acids
	 * '+' strand
	 * </P>
	 */	
	@Test public void testDuplicationVar9() throws AnnotationException  {
	    byte chr = 9;
	    int pos = 137968908;
	    String ref = "-";
	    String alt = "TGG";
	    Chromosome c = chromosomeMap.get(chr); 
	    if (c==null) {
		Assert.fail("Could not identify chromosome \"" + chr + "\"");
	    } else {
		AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.NON_FS_DUPLICATION,varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("OLFM1(uc010naq.2:exon2:c.314_316dupTGG:p.A106delinsVA)",annot);
	    }
	}

	
	/**
	 * <P>
	 * This is the test for the offset (+2) duplication of a single triplicate /
	 * one amino acids
	 * '+' strand
	 * </P>
	 */	
	@Test public void testDuplicationVar10() throws AnnotationException  {
	    byte chr = 9;
	    int pos = 137968909;
	    String ref = "-";
	    String alt = "GGC";
	    Chromosome c = chromosomeMap.get(chr); 
	    if (c==null) {
		Assert.fail("Could not identify chromosome \"" + chr + "\"");
	    } else {
		AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.NON_FS_DUPLICATION,varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("OLFM1(uc010naq.2:exon2:c.315_317dupGGC:p.A106delinsAA)",annot);
	    }
	}


//	/**
//	 * <P>
//	 * This is the test for the in-frame duplication of a single triplicate /
//	 * one amino acids  shifting the Stop-codon
//	 * '+' strand
//	 * This TEST should fail since the length of the mRNA provided by UCSC and the cumulative length of the exons 
//	 * do not fit (6245 vs. 6242) and therefore a wring nuc.acid sequence is use for duplication comparision. 
//	 * </P>
//	 */
//	@Test
//	public void testDuplicationVar11() throws AnnotationException {
//		System.out.println("-----------------");
//		byte chr = 5;
//		int pos = 77771489;
//		String ref = "-";
//		String alt = "TTT";
//		Chromosome c = chromosomeMap.get(chr); 
//		if (c==null) {
//		    Assert.fail("Could not identify chromosome \"" + chr + "\"");
//		} else {
//		    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
//		    VariantType varType = ann.getVariantType();
//		    Assert.assertEquals(VariantType.NON_FS_DUPLICATION,varType);
//		    String annot = ann.getVariantAnnotation();
//		    Assert.assertEquals("SCAMP1(uc003kfn.3:exon3:c.227_229dupTTT:p.*77delinsF*,uc003kfl.3:exon10:c.1010_1012dupTTT:p.*338delinsF*)",annot);
//		}
//	}

//	/**
//	 * <P>
//	 * This is the test for the in-frame duplication of a single triplicate /
//	 * one amino acids  shifting the Stop-codon
//	 * '+' strand
//	 * This TEST should fail since the length of the mRNA provided by UCSC and the cumulative length of the exons 
//	 * do not fit (6245 vs. 6242) and therefore a wrong nuc.acid sequence is use for duplication comparison. <br>
//	 * The difference in the index is due to the wrong assignment of the {@link VariantType} to InsertionVariation
//	 * </P>
//	 */
//	@Test
//	public void testDuplicationVar12() throws AnnotationException {
//		System.out.println("-----------------");
//		byte chr = 5;
//		int pos = 77771490;
//		String ref = "-";
//		String alt = "TTA";
//		Chromosome c = chromosomeMap.get(chr); 
//		if (c==null) {
//		    Assert.fail("Could not identify chromosome \"" + chr + "\"");
//		} else {
//		    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
//		    VariantType varType = ann.getVariantType();
//		    Assert.assertEquals(VariantType.NON_FS_DUPLICATION,varType);
//		    String annot = ann.getVariantAnnotation();
//		    Assert.assertEquals("SCAMP1(uc003kfn.3:exon3:c.228_230dupTTA:p.*77delinsY*,uc003kfl.3:exon10:c.1011_1013dupTTA:p.*338delinsY*)",annot);
//		}
//	}

	
	/**
	 * <P>
	 * This is the test for the in-frame duplication of a single triplicate /
	 * one amino acids shifting the Stop-codon
	 * '-' strand
	 * </P>
	 */

	@Test
	public void testDuplicationVar13() throws AnnotationException {
		byte chr = 3;
		int pos = 184428690;
		String ref = "-";
		String alt = "CAC";
		Chromosome c = chromosomeMap.get(chr);
		if (c == null) {
			Assert.fail("Could not identify chromosome \"" + chr + "\"");
		} else {
			AnnotationList ann = c.getAnnotationList(pos, ref, alt);
			VariantType varType = ann.getVariantType();
			Assert.assertEquals(VariantType.NON_FS_DUPLICATION, varType);
			String annot = ann.getVariantAnnotation();
			Assert.assertEquals("MAGEF1(uc003fpa.3:exon1:c.921_923dupGTG:p.*308delinsW*)", annot);
		}
	}

	
	/**
	 * <P>
	 * This is the test for the in-frame duplication of a single triplicate /
	 * one amino acids shifting the Stop-codon
	 * '-' strand
	 * </P>
	 */

	@Test
	public void testDuplicationVar14() throws AnnotationException {
		byte chr = 3;
		int pos = 184428691;
		String ref = "-";
		String alt = "ACC";
		Chromosome c = chromosomeMap.get(chr);
		if (c == null) {
			Assert.fail("Could not identify chromosome \"" + chr + "\"");
		} else {
			AnnotationList ann = c.getAnnotationList(pos, ref, alt);
			VariantType varType = ann.getVariantType();
			Assert.assertEquals(VariantType.NON_FS_DUPLICATION, varType);
			String annot = ann.getVariantAnnotation();
			Assert.assertEquals("MAGEF1(uc003fpa.3:exon1:c.920_922dupGGT:p.*308delinsW*)", annot);
		}
	}
	
	
	/**
	 * <P>
	 * This is the test for the offset (+2) duplication of a single triplicate /
	 * one amino acids shifting the Stop-codon
	 * '-' strand
	 * </P>
	 */

	@Test
	public void testDuplicationVar15() throws AnnotationException {
		byte chr = 3;
		int pos = 184428696;
		String ref = "-";
		String alt = "AGG";
		Chromosome c = chromosomeMap.get(chr);
		if (c == null) {
			Assert.fail("Could not identify chromosome \"" + chr + "\"");
		} else {
			AnnotationList ann = c.getAnnotationList(pos, ref, alt);
			VariantType varType = ann.getVariantType();
			Assert.assertEquals(VariantType.NON_FS_DUPLICATION, varType);
			String annot = ann.getVariantAnnotation();
			Assert.assertEquals("MAGEF1(uc003fpa.3:exon1:c.915_917dupCCT:p.L306delinsLL)", annot);
		}
	}

		/**
	 * <P>
	 * This is the test for the offset (+2) duplication of a double triplicate /
	 * two amino acids shifting the Stop-codon
	 * '-' strand
	 * </P>
	 */

	@Test
	public void testDuplicationVar16() throws AnnotationException {
		byte chr = 3;
		int pos = 184428696;
		String ref = "-";
		String alt = "CAGAGG";
		Chromosome c = chromosomeMap.get(chr);
		if (c == null) {
			Assert.fail("Could not identify chromosome \"" + chr + "\"");
		} else {
			AnnotationList ann = c.getAnnotationList(pos, ref, alt);
			VariantType varType = ann.getVariantType();
			Assert.assertEquals(VariantType.NON_FS_DUPLICATION, varType);
			String annot = ann.getVariantAnnotation();
			Assert.assertEquals("MAGEF1(uc003fpa.3:exon1:c.915_920dupCCTCTG:p.W307delinsCLW)", annot);
		}
	}

	/**
	 * <P>
	 * This is the test for the offset (+1) duplication of a single triplicate /
	 * one amino acids shifting the Stop-codon
	 * '-' strand
	 * </P>
	 */

	@Test
	public void testDuplicationVar17() throws AnnotationException {
		byte chr = 3;
		int pos = 184428697;
		String ref = "-";
		String alt = "GGT";
		Chromosome c = chromosomeMap.get(chr);
		if (c == null) {
			Assert.fail("Could not identify chromosome \"" + chr + "\"");
		} else {
			AnnotationList ann = c.getAnnotationList(pos, ref, alt);
			VariantType varType = ann.getVariantType();
			Assert.assertEquals(VariantType.NON_FS_DUPLICATION, varType);
			String annot = ann.getVariantAnnotation();
			Assert.assertEquals("MAGEF1(uc003fpa.3:exon1:c.914_916dupACC:p.L306delinsHL)", annot);
		}
	}

		/**
	 * <P>
	 * This is the test for the offset (+1) duplication of a double triplicate /
	 * two amino acids shifting the Stop-codon
	 * '-' strand
	 * </P>
	 */

	@Test
	public void testDuplicationVar18() throws AnnotationException {
		byte chr = 3;
		int pos = 184428697;
		String ref = "-";
		String alt = "AGAGGT";
		Chromosome c = chromosomeMap.get(chr);
		if (c == null) {
			Assert.fail("Could not identify chromosome \"" + chr + "\"");
		} else {
			AnnotationList ann = c.getAnnotationList(pos, ref, alt);
			VariantType varType = ann.getVariantType();
			Assert.assertEquals(VariantType.NON_FS_DUPLICATION, varType);
			String annot = ann.getVariantAnnotation();
			Assert.assertEquals("MAGEF1(uc003fpa.3:exon1:c.914_919dupACCTCT:p.W307delinsYLW)", annot);
		}
	}
	

	/**
	 * <P>
	 * This is the test for the offset (+2) frame shift duplication of a two NA on the '+' strand
	 * </P>
	 */
	@Test
	public void testDuplicationVar19() throws AnnotationException {
		byte chr = 1;
		int pos = 248637422;
		String ref = "-";
		String alt = "TT";
		Chromosome c = chromosomeMap.get(chr);
		if (c == null) {
			Assert.fail("Could not identify chromosome \"" + chr + "\"");
		} else {
			AnnotationList ann = c.getAnnotationList(pos, ref, alt);
			VariantType varType = ann.getVariantType();
			Assert.assertEquals(VariantType.FS_DUPLICATION, varType);
			String annot = ann.getVariantAnnotation();
			Assert.assertEquals("OR2T3(uc001iel.1:exon1:c.769_770dupTT:p.F257fs)", annot);
		}
	}

	/**
	 * <P>
	 * This is the test for the offset (+1) frame shift duplication of a two NA on the '+' strand
	 * </P>
	 */
	@Test
	public void testDuplicationVar20() throws AnnotationException {
		byte chr = 1;
		int pos = 248637421;
		String ref = "-";
		String alt = "CT";
		Chromosome c = chromosomeMap.get(chr);
		if (c == null) {
			Assert.fail("Could not identify chromosome \"" + chr + "\"");
		} else {
			AnnotationList ann = c.getAnnotationList(pos, ref, alt);
			VariantType varType = ann.getVariantType();
			Assert.assertEquals(VariantType.FS_DUPLICATION, varType);
			String annot = ann.getVariantAnnotation();
			Assert.assertEquals("OR2T3(uc001iel.1:exon1:c.768_769dupCT:p.F257fs)", annot);
		}
	}

	/**
	 * <P>
	 * This is the test for the offset (+0) frame shift duplication of a two NA on the '+' strand
	 * </P>
	 */
	@Test
	public void testDuplicationVar21() throws AnnotationException {
		byte chr = 1;
		int pos = 248637420;
		String ref = "-";
		String alt = "TC";
		Chromosome c = chromosomeMap.get(chr);
		if (c == null) {
			Assert.fail("Could not identify chromosome \"" + chr + "\"");
		} else {
			AnnotationList ann = c.getAnnotationList(pos, ref, alt);
			VariantType varType = ann.getVariantType();
			Assert.assertEquals(VariantType.FS_DUPLICATION, varType);
			String annot = ann.getVariantAnnotation();
			Assert.assertEquals("OR2T3(uc001iel.1:exon1:c.767_768dupTC:p.F257fs)", annot);
		}
	}

	/**
	 * <P>
	 * This is the test for the offset (+0) frame shift duplication of 7 nuc.acids / on the '+' strand
	 * </P>
	 */
	@Test
	public void testDuplicationVar22() throws AnnotationException {
		byte chr = 1;
		int pos = 248637420;
		String ref = "-";
		String alt = "GCTGCTC";
		Chromosome c = chromosomeMap.get(chr);
		if (c == null) {
			Assert.fail("Could not identify chromosome \"" + chr + "\"");
		} else {
			AnnotationList ann = c.getAnnotationList(pos, ref, alt);
			VariantType varType = ann.getVariantType();
			Assert.assertEquals(VariantType.FS_DUPLICATION, varType);
			String annot = ann.getVariantAnnotation();
			Assert.assertEquals("OR2T3(uc001iel.1:exon1:c.762_768dupGCTGCTC:p.F257fs)", annot);
		}
	}

	/**
	 * <P>
	 * This is the test for the offset (+1) frame shift duplication of 7 nuc.acids  on the '+' strand
	 * </P>
	 */
	@Test
	public void testDuplicationVar23() throws AnnotationException {
		byte chr = 1;
		int pos = 248637421;
		String ref = "-";
		String alt = "CTGCTCT";
		Chromosome c = chromosomeMap.get(chr);
		if (c == null) {
			Assert.fail("Could not identify chromosome \"" + chr + "\"");
		} else {
			AnnotationList ann = c.getAnnotationList(pos, ref, alt);
			VariantType varType = ann.getVariantType();
			Assert.assertEquals(VariantType.FS_DUPLICATION, varType);
			String annot = ann.getVariantAnnotation();
			Assert.assertEquals("OR2T3(uc001iel.1:exon1:c.763_769dupCTGCTCT:p.F257fs)", annot);
		}
	}

	/**
	 * <P>
	 * This is the test for the offset (+2) frame shift duplication of 7 nuc.acids on the '+' strand
	 * </P>
	 */
	@Test
	public void testDuplicationVar24() throws AnnotationException {
		byte chr = 1;
		int pos = 248637422;
		String ref = "-";
		String alt = "TGCTCTT";
		Chromosome c = chromosomeMap.get(chr);
		if (c == null) {
			Assert.fail("Could not identify chromosome \"" + chr + "\"");
		} else {
			AnnotationList ann = c.getAnnotationList(pos, ref, alt);
			VariantType varType = ann.getVariantType();
			Assert.assertEquals(VariantType.FS_DUPLICATION, varType);
			String annot = ann.getVariantAnnotation();
			Assert.assertEquals("OR2T3(uc001iel.1:exon1:c.764_770dupTGCTCTT:p.F257fs)", annot);
		}
	}
	
	
	/**
	 * <P>
	 * This is the test for the offset (+2) frame shift duplication of 2 nuc.acids / three 
	 * amino acids '-' strand
	 * </P>
	 */
	@Test
	public void testDuplicationVar25() throws AnnotationException {
		byte chr = 3;
		int pos = 184429173;
		String ref = "-";
		String alt = "TG";
		Chromosome c = chromosomeMap.get(chr);
		if (c == null) {
			Assert.fail("Could not identify chromosome \"" + chr + "\"");
		} else {
			AnnotationList ann = c.getAnnotationList(pos, ref, alt);
			VariantType varType = ann.getVariantType();
			Assert.assertEquals(VariantType.FS_DUPLICATION, varType);
			String annot = ann.getVariantAnnotation();
			Assert.assertEquals("MAGEF1(uc003fpa.3:exon1:c.438_439dupCA:p.N147fs)", annot);
		}
	}

	/**
	 * <P>
	 * This is the test for the offset (+1) frame shift duplication of 2 nuc.acids / three 
	 * amino acids '-' strand
	 * </P>
	 */
	@Test
	public void testDuplicationVar26() throws AnnotationException {
		byte chr = 3;
		int pos = 184429174;
		String ref = "-";
		String alt = "GA";
		Chromosome c = chromosomeMap.get(chr);
		if (c == null) {
			Assert.fail("Could not identify chromosome \"" + chr + "\"");
		} else {
			AnnotationList ann = c.getAnnotationList(pos, ref, alt);
			VariantType varType = ann.getVariantType();
			Assert.assertEquals(VariantType.FS_DUPLICATION, varType);
			String annot = ann.getVariantAnnotation();
			Assert.assertEquals("MAGEF1(uc003fpa.3:exon1:c.437_438dupTC:p.N147fs)", annot);
		}
	}

	
	
	/**
	 * <P>
	 * This is the test for the offset (+2) frame shift duplication of 12 nuc.acids / three
	 * amino acids '-' strand
	 * </P>
	 */
	@Test
	public void testDuplicationVar27() throws AnnotationException {
		byte chr = 3;
		int pos = 184429173;
		String ref = "-";
		String alt = "TTTTAGTTTGTTG";
		Chromosome c = chromosomeMap.get(chr);
		if (c == null) {
			Assert.fail("Could not identify chromosome \"" + chr + "\"");
		} else {
			AnnotationList ann = c.getAnnotationList(pos, ref, alt);
			VariantType varType = ann.getVariantType();
			Assert.assertEquals(VariantType.FS_DUPLICATION, varType);
			String annot = ann.getVariantAnnotation();
			Assert.assertEquals("MAGEF1(uc003fpa.3:exon1:c.438_450dupCAACAAACTAAAA:p.P151fs)", annot);
		}
	}


}
