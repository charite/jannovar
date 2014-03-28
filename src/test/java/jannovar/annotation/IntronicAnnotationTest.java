package jannovar.annotation;

import jannovar.common.Constants;
import jannovar.common.VariantType;
import jannovar.exception.AnnotationException;
import jannovar.exception.JannovarException;
import jannovar.io.SerializationManager;
import jannovar.reference.Chromosome;
import jannovar.reference.TranscriptModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/* serialization */

/**
 * This class is intended to perform unuit testing on variants that are intergenic.
 */
public class IntronicAnnotationTest implements Constants {

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
	 * An intron of PLEKHN1 gtgagtaagg atcctgcctc ctg [a] ggtgagtgcc tgttgcctcc cacaggctga cacatctctg ccttccctac cag
	 * Result hand-checked OK.
	 */

	@Test
	public void testIntronicVar4() throws AnnotationException {
		byte chr = 1;
		int pos = 909768;
		String ref = "A";
		String alt = "G";
		Chromosome c = chromosomeMap.get(chr);
		if (c == null) {
			Assert.fail("Could not identify chromosome \"" + chr + "\"");
		} else {
			AnnotationList ann = c.getAnnotationList(pos, ref, alt);
			VariantType varType = ann.getVariantType();
			Assert.assertEquals(VariantType.INTRONIC, varType);
			String annot = ann.getVariantAnnotation();
			// Assert.assertEquals("PLEKHN1(uc001acf.3:dist to exon14=24:dist to exon15=54)", annot);
			Assert.assertEquals("PLEKHN1(uc001acf.3:intron14:c.1597+24A>G)", annot);
		}
	}

	// /**
	// * An intron C12orf54 <br>
	// * 5'UTR <br>
	// * closer to left exon<br>
	// * '+'-strand
	// */
	//
	// @Test
	// public void testIntronicVar41() throws AnnotationException {
	// byte chr = 12;
	// int pos = 46637200;
	// String ref = "A";
	// String alt = "G";
	// Chromosome c = chromosomeMap.get(chr);
	// if (c == null) {
	// Assert.fail("Could not identify chromosome \"" + chr + "\"");
	// } else {
	// AnnotationList ann = c.getAnnotationList(pos, ref, alt);
	// VariantType varType = ann.getVariantType();
	// Assert.assertEquals(VariantType.INTRONIC, varType);
	// String annot = ann.getVariantAnnotation();
	// // Assert.assertEquals("PLEKHN1(uc001acf.3:dist to exon14=24:dist to exon15=54)", annot);
	// Assert.assertEquals("SLC38A1(uc001rpd.3:exon3:c.-208-103A>G)", annot);
	// }
	// }

	/**
	 * An intron of C12orf54<br>
	 * 5'UTR <br>
	 * closer to left exon<br>
	 * '+'-strand
	 */

	@Test
	public void testIntronicVar42() throws AnnotationException {
		byte chr = 12;
		int pos = 48876500;
		String ref = "C";
		String alt = "T";
		Chromosome c = chromosomeMap.get(chr);
		if (c == null) {
			Assert.fail("Could not identify chromosome \"" + chr + "\"");
		} else {
			AnnotationList ann = c.getAnnotationList(pos, ref, alt);
			VariantType varType = ann.getVariantType();
			Assert.assertEquals(VariantType.INTRONIC, varType);
			String annot = ann.getVariantAnnotation();
			// Assert.assertEquals("PLEKHN1(uc001acf.3:dist to exon14=24:dist to exon15=54)", annot);
			Assert.assertEquals("C12orf54(uc001rrr.3:intron1:c.-59+141C>T)", annot);
		}
	}

	/**
	 * An intron of C12orf54<br>
	 * 5'UTR <br>
	 * closer to right exon<br>
	 * '+'-strand
	 */

	@Test
	public void testIntronicVar43() throws AnnotationException {
		byte chr = 12;
		int pos = 48877000;
		String ref = "C";
		String alt = "T";
		Chromosome c = chromosomeMap.get(chr);
		if (c == null) {
			Assert.fail("Could not identify chromosome \"" + chr + "\"");
		} else {
			AnnotationList ann = c.getAnnotationList(pos, ref, alt);
			VariantType varType = ann.getVariantType();
			Assert.assertEquals(VariantType.INTRONIC, varType);
			String annot = ann.getVariantAnnotation();
			// Assert.assertEquals("PLEKHN1(uc001acf.3:dist to exon14=24:dist to exon15=54)", annot);
			Assert.assertEquals("C12orf54(uc001rrr.3:intron1:c.-58-23C>T)", annot);
		}
	}

	/**
	 * An intron of C12orf54<br>
	 * 3'UTR <br>
	 * closer to left exon<br>
	 * '+'-strand
	 */

	@Test
	public void testIntronicVar44() throws AnnotationException {
		byte chr = 12;
		int pos = 48888800;
		String ref = "C";
		String alt = "T";
		Chromosome c = chromosomeMap.get(chr);
		if (c == null) {
			Assert.fail("Could not identify chromosome \"" + chr + "\"");
		} else {
			AnnotationList ann = c.getAnnotationList(pos, ref, alt);
			VariantType varType = ann.getVariantType();
			Assert.assertEquals(VariantType.INTRONIC, varType);
			String annot = ann.getVariantAnnotation();
			// Assert.assertEquals("PLEKHN1(uc001acf.3:dist to exon14=24:dist to exon15=54)", annot);
			Assert.assertEquals("C12orf54(uc001rrr.3:intron8:c.*41+38C>T)", annot);
		}
	}

	/**
	 * An intron of C12orf54<br>
	 * 3'UTR <br>
	 * closer to right exon<br>
	 * '+'-strand
	 */

	@Test
	public void testIntronicVar45() throws AnnotationException {
		byte chr = 12;
		int pos = 48889800;
		String ref = "T";
		String alt = "C";
		Chromosome c = chromosomeMap.get(chr);
		if (c == null) {
			Assert.fail("Could not identify chromosome \"" + chr + "\"");
		} else {
			AnnotationList ann = c.getAnnotationList(pos, ref, alt);
			VariantType varType = ann.getVariantType();
			Assert.assertEquals(VariantType.INTRONIC, varType);
			String annot = ann.getVariantAnnotation();
			// Assert.assertEquals("PLEKHN1(uc001acf.3:dist to exon14=24:dist to exon15=54)", annot);
			Assert.assertEquals("C12orf54(uc001rrr.3:intron8:c.*42-164T>C)", annot);
		}
	}

	/**
	 * An intron of C12orf54<br>
	 * CDS <br>
	 * closer to left exon<br>
	 * '+'-strand
	 */

	@Test
	public void testIntronicVar46() throws AnnotationException {
		byte chr = 12;
		int pos = 48880600;
		String ref = "T";
		String alt = "C";
		Chromosome c = chromosomeMap.get(chr);
		if (c == null) {
			Assert.fail("Could not identify chromosome \"" + chr + "\"");
		} else {
			AnnotationList ann = c.getAnnotationList(pos, ref, alt);
			VariantType varType = ann.getVariantType();
			Assert.assertEquals(VariantType.INTRONIC, varType);
			String annot = ann.getVariantAnnotation();
			// Assert.assertEquals("PLEKHN1(uc001acf.3:dist to exon14=24:dist to exon15=54)", annot);
			Assert.assertEquals("C12orf54(uc001rrr.3:intron4:c.135+91T>C)", annot);
		}
	}

	/**
	 * An intron of C12orf54<br>
	 * CDS <br>
	 * closer to right exon<br>
	 * '+'-strand
	 */

	@Test
	public void testIntronicVar47() throws AnnotationException {
		byte chr = 12;
		int pos = 48882700;
		String ref = "C";
		String alt = "T";
		Chromosome c = chromosomeMap.get(chr);
		if (c == null) {
			Assert.fail("Could not identify chromosome \"" + chr + "\"");
		} else {
			AnnotationList ann = c.getAnnotationList(pos, ref, alt);
			VariantType varType = ann.getVariantType();
			Assert.assertEquals(VariantType.INTRONIC, varType);
			String annot = ann.getVariantAnnotation();
			// Assert.assertEquals("PLEKHN1(uc001acf.3:dist to exon14=24:dist to exon15=54)", annot);
			Assert.assertEquals("C12orf54(uc001rrr.3:intron4:c.136-7C>T)", annot);
		}
	}

	/**
	 * An intron of TAF1A<br>
	 * 5'UTR <br>
	 * closer to trailing exon<br>
	 * '-'-strand
	 */

	@Test
	public void testIntronicVar50() throws AnnotationException {
		byte chr = 1;
		int pos = 222762000;
		String ref = "A";
		String alt = "G";
		Chromosome c = chromosomeMap.get(chr);
		if (c == null) {
			Assert.fail("Could not identify chromosome \"" + chr + "\"");
		} else {
			AnnotationList ann = c.getAnnotationList(pos, ref, alt);
			VariantType varType = ann.getVariantType();
			Assert.assertEquals(VariantType.INTRONIC, varType);
			String annot = ann.getVariantAnnotation();
			// Assert.assertEquals("PLEKHN1(uc001acf.3:dist to exon14=24:dist to exon15=54)", annot);
			Assert.assertEquals("TAF1A(uc001hni.2:intron1:c.-175-93T>C)", annot);
		}
	}

	/**
	 * An intron of TAF1A<br>
	 * 5'UTR <br>
	 * closer to leading exon<br>
	 * '-'-strand
	 */

	@Test
	public void testIntronicVar51() throws AnnotationException {
		byte chr = 1;
		int pos = 222763000;
		String ref = "T";
		String alt = "C";
		Chromosome c = chromosomeMap.get(chr);
		if (c == null) {
			Assert.fail("Could not identify chromosome \"" + chr + "\"");
		} else {
			AnnotationList ann = c.getAnnotationList(pos, ref, alt);
			VariantType varType = ann.getVariantType();
			Assert.assertEquals(VariantType.INTRONIC, varType);
			String annot = ann.getVariantAnnotation();
			// Assert.assertEquals("PLEKHN1(uc001acf.3:dist to exon14=24:dist to exon15=54)", annot);
			Assert.assertEquals("TAF1A(uc001hni.2:intron1:c.-176+69A>G)", annot);
		}
	}

	/**
	 * An intron of TAF1A<br>
	 * 3'UTR <br>
	 * closer to trailing exon<br>
	 * '-'-strand
	 */

	@Test
	public void testIntronicVar52() throws AnnotationException {
		byte chr = 1;
		int pos = 222731700;
		String ref = "A";
		String alt = "G";
		Chromosome c = chromosomeMap.get(chr);
		if (c == null) {
			Assert.fail("Could not identify chromosome \"" + chr + "\"");
		} else {
			AnnotationList ann = c.getAnnotationList(pos, ref, alt);
			VariantType varType = ann.getVariantType();
			Assert.assertEquals(VariantType.INTRONIC, varType);
			String annot = ann.getVariantAnnotation();
			// Assert.assertEquals("PLEKHN1(uc001acf.3:dist to exon14=24:dist to exon15=54)", annot);
			Assert.assertEquals("TAF1A(uc009xdy.1:intron4:c.*39-90T>C)", annot);
		}
	}

	/**
	 * An intron of TAF1A<br>
	 * 3'UTR <br>
	 * closer to leading exon<br>
	 * '-'-strand
	 */

	@Test
	public void testIntronicVar53() throws AnnotationException {
		byte chr = 1;
		int pos = 222731900;
		String ref = "T";
		String alt = "C";
		Chromosome c = chromosomeMap.get(chr);
		if (c == null) {
			Assert.fail("Could not identify chromosome \"" + chr + "\"");
		} else {
			AnnotationList ann = c.getAnnotationList(pos, ref, alt);
			VariantType varType = ann.getVariantType();
			Assert.assertEquals(VariantType.INTRONIC, varType);
			String annot = ann.getVariantAnnotation();
			// Assert.assertEquals("PLEKHN1(uc001acf.3:dist to exon14=24:dist to exon15=54)", annot);
			Assert.assertEquals("TAF1A(uc009xdy.1:intron4:c.*38+65A>G)", annot);
		}
	}

	/**
	 * An intron of TAF1A<br>
	 * CDS <br>
	 * closer to trailing exon<br>
	 * '-'-strand
	 */

	@Test
	public void testIntronicVar54() throws AnnotationException {
		byte chr = 1;
		int pos = 222736700;
		String ref = "A";
		String alt = "G";
		Chromosome c = chromosomeMap.get(chr);
		if (c == null) {
			Assert.fail("Could not identify chromosome \"" + chr + "\"");
		} else {
			AnnotationList ann = c.getAnnotationList(pos, ref, alt);
			VariantType varType = ann.getVariantType();
			Assert.assertEquals(VariantType.INTRONIC, varType);
			String annot = ann.getVariantAnnotation();
			// Assert.assertEquals("PLEKHN1(uc001acf.3:dist to exon14=24:dist to exon15=54)", annot);
			Assert.assertEquals("TAF1A(uc001hni.2:intron7:c.620-62T>C)", annot);
		}
	}

	/**
	 * An intron of TAF1A<br>
	 * CDS <br>
	 * closer to leading exon<br>
	 * '-'-strand
	 */

	@Test
	public void testIntronicVar55() throws AnnotationException {
		byte chr = 1;
		int pos = 222737200;
		String ref = "A";
		String alt = "G";
		Chromosome c = chromosomeMap.get(chr);
		if (c == null) {
			Assert.fail("Could not identify chromosome \"" + chr + "\"");
		} else {
			AnnotationList ann = c.getAnnotationList(pos, ref, alt);
			VariantType varType = ann.getVariantType();
			Assert.assertEquals(VariantType.INTRONIC, varType);
			String annot = ann.getVariantAnnotation();
			// Assert.assertEquals("PLEKHN1(uc001acf.3:dist to exon14=24:dist to exon15=54)", annot);
			Assert.assertEquals("TAF1A(uc001hni.2:intron7:c.619+201T>C)", annot);
		}
	}

	/**
	 * An intron of BC052952<br>
	 * non-coding <br>
	 * closer to leading exon<br>
	 * '+'-strand
	 */

	@Test
	public void testIntronicVar60() throws AnnotationException {
		byte chr = 20;
		int pos = 25944000;
		String ref = "C";
		String alt = "A";
		Chromosome c = chromosomeMap.get(chr);
		if (c == null) {
			Assert.fail("Could not identify chromosome \"" + chr + "\"");
		} else {
			AnnotationList ann = c.getAnnotationList(pos, ref, alt);
			VariantType varType = ann.getVariantType();
			Assert.assertEquals(VariantType.ncRNA_INTRONIC, varType);
			String annot = ann.getVariantAnnotation();
			// Assert.assertEquals("PLEKHN1(uc001acf.3:dist to exon14=24:dist to exon15=54)", annot);
			Assert.assertEquals("BC052952(uc002wvf.3:intron3:n.313+168C>A)", annot);
		}
	}

	/**
	 * An intron of BC052952<br>
	 * non-coding <br>
	 * closer to trailing exon<br>
	 * '+'-strand
	 */

	@Test
	public void testIntronicVar61() throws AnnotationException {
		byte chr = 20;
		int pos = 25945000;
		String ref = "C";
		String alt = "A";
		Chromosome c = chromosomeMap.get(chr);
		if (c == null) {
			Assert.fail("Could not identify chromosome \"" + chr + "\"");
		} else {
			AnnotationList ann = c.getAnnotationList(pos, ref, alt);
			VariantType varType = ann.getVariantType();
			Assert.assertEquals(VariantType.ncRNA_INTRONIC, varType);
			String annot = ann.getVariantAnnotation();
			// Assert.assertEquals("PLEKHN1(uc001acf.3:dist to exon14=24:dist to exon15=54)", annot);
			Assert.assertEquals("BC052952(uc002wvf.3:intron3:n.314-639C>A)", annot);
		}
	}

	/**
	 * An intron of BC052952<br>
	 * non-coding <br>
	 * closer to leading exon<br>
	 * '-'-strand
	 */

	@Test
	public void testIntronicVar62() throws AnnotationException {
		byte chr = 17;
		int pos = 36361000;
		String ref = "A";
		String alt = "T";
		Chromosome c = chromosomeMap.get(chr);
		if (c == null) {
			Assert.fail("Could not identify chromosome \"" + chr + "\"");
		} else {
			AnnotationList ann = c.getAnnotationList(pos, ref, alt);
			VariantType varType = ann.getVariantType();
			Assert.assertEquals(VariantType.ncRNA_INTRONIC, varType);
			String annot = ann.getVariantAnnotation();
			// Assert.assertEquals("PLEKHN1(uc001acf.3:dist to exon14=24:dist to exon15=54)", annot);
			Assert.assertEquals("LOC440434(uc010wdn.1:intron4:n.424+697T>A)", annot);
		}
	}

	/**
	 * An intron of BC052952<br>
	 * non-coding <br>
	 * closer to trailing exon<br>
	 * '-'-strand
	 */

	@Test
	public void testIntronicVar63() throws AnnotationException {
		byte chr = 17;
		int pos = 36359600;
		String ref = "G";
		String alt = "T";
		Chromosome c = chromosomeMap.get(chr);
		if (c == null) {
			Assert.fail("Could not identify chromosome \"" + chr + "\"");
		} else {
			AnnotationList ann = c.getAnnotationList(pos, ref, alt);
			VariantType varType = ann.getVariantType();
			Assert.assertEquals(VariantType.ncRNA_INTRONIC, varType);
			String annot = ann.getVariantAnnotation();
			// Assert.assertEquals("PLEKHN1(uc001acf.3:dist to exon14=24:dist to exon15=54)", annot);
			Assert.assertEquals("LOC440434(uc010wdn.1:intron4:n.425-558C>A)", annot);
		}
	}

	/**
	 * <P>
	 * annovar: MORN1 chr1:2286947A>G
	 * </P>
	 * TODO check distance
	 * 
	 * @Test public void testIntronicVar42() throws AnnotationException { byte chr = 1; int pos = 2286947; String ref =
	 *       "A"; String alt = "G"; Chromosome c = chromosomeMap.get(chr); if (c==null) {
	 *       Assert.fail("Could not identify chromosome \"" + chr + "\""); } else { AnnotationList ann =
	 *       c.getAnnotationList(pos,ref,alt); VariantType varType = ann.getVariantType();
	 *       Assert.assertEquals(VariantType.INTRONIC,varType); String annot = ann.getVariantAnnotation();
	 *       Assert.assertEquals("MORN1(uc009vld.3)",annot); } }
	 */

	/**
	 * <P>
	 * annovar: CHD5 chr1:6204222C>G distance of 7 hand-checked
	 * </P>
	 */
	@Test
	public void testIntronicVar70() throws AnnotationException {
		byte chr = 1;
		int pos = 6204222;
		String ref = "C";
		String alt = "G";
		Chromosome c = chromosomeMap.get(chr);
		if (c == null) {
			Assert.fail("Could not identify chromosome \"" + chr + "\"");
		} else {
			AnnotationList ann = c.getAnnotationList(pos, ref, alt);
			VariantType varType = ann.getVariantType();
			Assert.assertEquals(VariantType.INTRONIC, varType);
			String annot = ann.getVariantAnnotation();
			Assert.assertEquals("CHD5(uc001amb.2:intron11:c.1803-7G>C)", annot);
		}
	}

	/**
	 * <P>
	 * annovar: TNFRSF1B chr1:12248965A>G
	 * </P>
	 * TODO test distance
	 * 
	 * @Test public void testIntronicVar150() throws AnnotationException { byte chr = 1; int pos = 12248965; String ref
	 *       = "A"; String alt = "G"; Chromosome c = chromosomeMap.get(chr); if (c==null) {
	 *       Assert.fail("Could not identify chromosome \"" + chr + "\""); } else { AnnotationList ann =
	 *       c.getAnnotationList(pos,ref,alt); VariantType varType = ann.getVariantType();
	 *       Assert.assertEquals(VariantType.INTRONIC,varType); String annot = ann.getVariantAnnotation();
	 *       Assert.assertEquals("TNFRSF1B(uc001atu.3)",annot); } }
	 */

	/**
	 * <P>
	 * annovar: NBPF14,NBPF9,PDE4DIP chr1:144915412T>C
	 * </P>
	 * -- Wierd part of genome with lots of transcripts. All intronic variants.
	 * 
	 * @Test public void testIntronicVar967() throws AnnotationException { byte chr = 1; int pos = 144915412; String ref
	 *       = "T"; String alt = "C"; Chromosome c = chromosomeMap.get(chr); if (c==null) {
	 *       Assert.fail("Could not identify chromosome \"" + chr + "\""); } else { AnnotationList ann =
	 *       c.getAnnotationList(pos,ref,alt); VariantType varType = ann.getVarType();
	 *       Assert.assertEquals(VariantType.INTRONIC,varType); String annot = ann.getVariantAnnotation();
	 *       Assert.assertEquals("NBPF14,NBPF9,PDE4DIP",annot); } }
	 */

	/**
	 * <P>
	 * annovar: LOC100505666 -- actually in intron of ADAM15 (many transcripts) and also of LOC100505666, which is -- a
	 * noncoding RNA intron chr1:155028522G>A
	 * </P>
	 * TODO check distance
	 * 
	 * @Test public void testIntronicVar1095() throws AnnotationException { byte chr = 1; int pos = 155028522; String
	 *       ref = "G"; String alt = "A"; Chromosome c = chromosomeMap.get(chr); if (c==null) {
	 *       Assert.fail("Could not identify chromosome \"" + chr + "\""); } else { AnnotationList ann =
	 *       c.getAnnotationList(pos,ref,alt); VariantType varType = ann.getVariantType();
	 *       Assert.assertEquals(VariantType.INTRONIC,varType); String annot = ann.getVariantAnnotation();
	 *       Assert.assertEquals("LOC100505666(uc021pan.2),ADAM15(uc001fgq.1)" ,annot); } }
	 */

	/**
	 * <P>
	 * annovar: PPFIBP1 chr12:27832582G>A
	 * </P>
	 * TODO check distance
	 * 
	 * @Test public void testIntronicVar11478() throws AnnotationException { byte chr = 12; int pos = 27832582; String
	 *       ref = "G"; String alt = "A"; Chromosome c = chromosomeMap.get(chr); if (c==null) {
	 *       Assert.fail("Could not identify chromosome \"" + chr + "\""); } else { AnnotationList ann =
	 *       c.getAnnotationList(pos,ref,alt); VariantType varType = ann.getVariantType();
	 *       Assert.assertEquals(VariantType.INTRONIC,varType); String annot = ann.getVariantAnnotation();
	 *       Assert.assertEquals("PPFIBP1(uc001rib.2)",annot); } }
	 */

	/**
	 * <P>
	 * annovar: TMTC1 chr12:29920791->CATA
	 * </P>
	 * TODO check distance
	 * 
	 * @Test public void testIntronicVar11489() throws AnnotationException { byte chr = 12; int pos = 29920791; String
	 *       ref = "-"; String alt = "CATA"; Chromosome c = chromosomeMap.get(chr); if (c==null) {
	 *       Assert.fail("Could not identify chromosome \"" + chr + "\""); } else { AnnotationList ann =
	 *       c.getAnnotationList(pos,ref,alt); VariantType varType = ann.getVariantType();
	 *       Assert.assertEquals(VariantType.INTRONIC,varType); String annot = ann.getVariantAnnotation();
	 *       Assert.assertEquals("TMTC1(uc001rjb.3)",annot); } }
	 */

	/**
	 * <P>
	 * annovar: BICD1 chr12:32459070T>G
	 * </P>
	 * TODO check distance
	 * 
	 * @Test public void testIntronicVar11500() throws AnnotationException { byte chr = 12; int pos = 32459070; String
	 *       ref = "T"; String alt = "G"; Chromosome c = chromosomeMap.get(chr); if (c==null) {
	 *       Assert.fail("Could not identify chromosome \"" + chr + "\""); } else { AnnotationList ann =
	 *       c.getAnnotationList(pos,ref,alt); VariantType varType = ann.getVariantType();
	 *       Assert.assertEquals(VariantType.INTRONIC,varType); String annot = ann.getVariantAnnotation();
	 *       Assert.assertEquals("BICD1(uc001rkv.3)",annot); } }
	 */

	/**
	 * <P>
	 * annovar: NELL2 chr12:44926334T>A
	 * </P>
	 * TODO check distance
	 * 
	 * @Test public void testIntronicVar11543() throws AnnotationException { byte chr = 12; int pos = 44926334; String
	 *       ref = "T"; String alt = "A"; Chromosome c = chromosomeMap.get(chr); if (c==null) {
	 *       Assert.fail("Could not identify chromosome \"" + chr + "\""); } else { AnnotationList ann =
	 *       c.getAnnotationList(pos,ref,alt); VariantType varType = ann.getVariantType();
	 *       Assert.assertEquals(VariantType.INTRONIC,varType); String annot = ann.getVariantAnnotation();
	 *       Assert.assertEquals("NELL2(uc009zkd.2)",annot); } }
	 */

	/**
	 * <P>
	 * annovar: SLC38A1 chr12:46601056C>T negative strand. gtaaaaagaacatgcttttctttacataacttgaaactaattctggtggatgagcggcca
	 * catgaattttaacataattcaagcagttatcatcctcattgctaaaatggcacagggaaa
	 * gtaaagcagagacagcagtcacttatttaaagccacaaatcctgctagagtagctgaagt
	 * tgcctttgtgtcttactgacagttggtctaagaacgggctgataactttttatgtagctt
	 * gcaacataagccttc[g]tatcttctttctaaaaatgttctcattttccttcag Distance to left exon 256, distance to right exon 37
	 * </P>
	 */
	// @Test
	// public void testIntronicVar11550() throws AnnotationException {
	// byte chr = 12;
	// int pos = 46601056;
	// String ref = "C";
	// String alt = "T";
	// Chromosome c = chromosomeMap.get(chr);
	// if (c == null) {
	// Assert.fail("Could not identify chromosome \"" + chr + "\"");
	// } else {
	// AnnotationList ann = c.getAnnotationList(pos, ref, alt);
	// VariantType varType = ann.getVariantType();
	// Assert.assertEquals(VariantType.INTRONIC, varType);
	// String annot = ann.getVariantAnnotation();
	// Assert.assertEquals("SLC38A1(uc001rpd.3:dist to exon8=256:dist to exon9=37)", annot);
	// }
	// }

	/**
	 * <P>
	 * annovar: PRKAG1 chr12:49398862G>A "-" strand gtatgtagagaattggggttataaaaggataaagggatgg[c]gggtttctgggaaacactt
	 * ttccatggtggtattttgtgacccatcccttttcccttcag 41 nt to left, 61 to right exon
	 * </P>
	 */
	// @Test
	// public void testIntronicVar11581() throws AnnotationException {
	// byte chr = 12;
	// int pos = 49398862;
	// String ref = "G";
	// String alt = "A";
	// Chromosome c = chromosomeMap.get(chr);
	// if (c == null) {
	// Assert.fail("Could not identify chromosome \"" + chr + "\"");
	// } else {
	// AnnotationList ann = c.getAnnotationList(pos, ref, alt);
	// VariantType varType = ann.getVariantType();
	// Assert.assertEquals(VariantType.INTRONIC, varType);
	// String annot = ann.getVariantAnnotation();
	// Assert.assertEquals("PRKAG1(uc001rsy.3:dist to exon6=41:dist to exon7=61)", annot);
	// }
	// }

	/**
	 * <P>
	 * annovar: TMBIM6 chr12:50151977G>A "+" strand gt....................tttctataaaaaaaaaaaaa
	 * acaacgcagtcaggtatacattttattgaaaacaattacagcggaggggagagagattta
	 * attctttagtccacgtttgttaagagcatgagtatgcctatatttcgggatcagcttttg
	 * gatgaggatcctattcaagaattgatc[g]taatactgtgttctgggttttctgttttctag 33 to right exon, 2439 from other (hand-checked)
	 * </P>
	 */
	// @Test
	// public void testIntronicVar11598() throws AnnotationException {
	// byte chr = 12;
	// int pos = 50151977;
	// String ref = "G";
	// String alt = "A";
	// Chromosome c = chromosomeMap.get(chr);
	// if (c == null) {
	// Assert.fail("Could not identify chromosome \"" + chr + "\"");
	// } else {
	// AnnotationList ann = c.getAnnotationList(pos, ref, alt);
	// VariantType varType = ann.getVariantType();
	// Assert.assertEquals(VariantType.INTRONIC, varType);
	// String annot = ann.getVariantAnnotation();
	// Assert.assertEquals("TMBIM6(uc001rux.2:dist to exon4=2439:dist to exon5=33)", annot);
	// }
	// }

}
