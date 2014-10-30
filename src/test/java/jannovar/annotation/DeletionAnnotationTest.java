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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/* serialization */

/**
 * This class is intended to perform unuit testing on variants that are intergenic.
 */
public class DeletionAnnotationTest implements Constants {

	private VariantAnnotator annotator = null;

	@Before
	public void setUp() throws IOException, JannovarException {
		ArrayList<TranscriptModel> kgList = null;
		java.net.URL url = SynonymousAnnotationTest.class.getResource(UCSCserializationTestFileName);
		String path = url.getPath();
		SerializationManager manager = new SerializationManager();
		kgList = manager.deserializeKnownGeneList(path);
		annotator = new VariantAnnotator(Chromosome.constructChromosomeMapWithIntervalTree(kgList));
	}

	/**
	 * <P>
	 * annovar: MST1P9:uc010ock.2:exon2:c.117_121del:p.39_41del The protein annotations are not accurate.
	 * chr1:17087544GCTGT>- The deletion goes from 17,087,544-17,087,548, on the rc it is ACAGC The gene is on the minus
	 * strand the deletion affects 3 codons. GTG-CTG-TAG Or reverse-complement CTA-CAG-CAC: the deletion A-CAG-C the
	 * deletion thus does not change the first amino acid, (CT*****A->CTA=Leu) Leu-Gln-His (i.e., LQH) The mutation
	 * changes TELQHLL -> TELPAT, i.e., the Q at position 40 is changed to a P followed by frameshift.
	 * NM_001271733.1:c.117_121del Mutalzyer: NM_001271733.1(MST1L_v001):c.119_123del
	 * NM_001271733.1(MST1L_i001):p.(Gln40Profs*18) Thus, we want p.Q40Pfs
	 * </P>
	 */
	@Test
	public void testFSDeletionVar2() throws AnnotationException {
		byte chr = 1;
		int pos = 17087544;
		String ref = "GCTGT";
		String alt = "-";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.FS_DELETION, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("MST1L(uc010ock.3:exon2:c.119_123del:p.Q40fs)", annot);
	}

	/**
	 * <P>
	 * annovar: OR14A16:uc001idm.1:exon1:c.486_488del:p.162_163del, chr1:247978544GAG>-
	 * </P>
	 */
	@Test
	public void testFSDeletionVar4() throws AnnotationException {
		byte chr = 1;
		int pos = 247978544;
		String ref = "GAG";
		String alt = "-";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.NON_FS_DELETION, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("OR14A16(uc001idm.1:exon1:c.488_490del:p.S163del)", annot);
	}

	// /**
	// *<P>
	// * annovar:
	// SRGAP2:uc009xbt.3:exon7:c.816delT:p.I272fs,SRGAP2:uc001hdy.3:exon7:c.1047delT:p.I349fs,SRGAP2:uc010prv.1:exon6:c.819delT:p.I273fs,SRGAP2:uc010pru.2:exon7:c.1044delT:p.I348fs,SRGAP2:uc001hdx.3:exon7:c.1047delT:p.I349fs,SRGAP2:uc010prt.1:exon7:c.816delT:p.I272fs,
	// * chr1:206579885T>-
	// *</P>
	// --- Works in principle, some of the UCSC transcripts have divergent nucleotides.
	// @Test public void testFSDeletionVar3() throws AnnotationException {
	// byte chr = 1;
	// int pos = 206579885;
	// String ref = "T";
	// String alt = "-";
	// Chromosome c = chromosomeMap.get(chr);
	// if (c==null) {
	// Assert.fail("Could not identify chromosome \"" + chr + "\"");
	// } else {
	// AnnotationList ann =c.getAnnotationList(pos,ref,alt);
	// byte varType = ann.getVarType();
	// Assert.assertEquals(FS_DELETION,varType);
	// String annot = ann.getVariantAnnotation();
	// Assert.assertEquals("SRGAP2(uc010prt.1:exon7:c.816delT:p.I272fs,uc009xbt.3:exon7:c.816delT:p.I272fs,uc010prv.1:exon6:c.819delT:p.I273fs,uc010pru.2:exon7:c.1044delT:p.I348fs,uc001hdy.3:exon7:c.1047delT:p.I349fs,uc001hdx.3:exon7:c.1047delT:p.I349fs)",annot);
	// }
	// }
	// */

	/**
	 * <P>
	 * annovar: ZNF852:uc011azx.2:exon4:c.1472_1473del:p.491_491del, Annovar is inaccurate here. chr3:44540796TC>- wt:
	 * GGAGAAAAACCTTATGAATGTATTGAGT mt: GGAAAAACCTTATGAATGTATTGAGT wt: GEKNLMNVLS mt: GEKPYECIE Thus, p.N494Pfs
	 * </P>
	 */
	@Test
	public void testFSDeletionVar6() throws AnnotationException {
		byte chr = 3;
		int pos = 44540796;
		String ref = "TC";
		String alt = "-";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.FS_DELETION, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("ZNF852(uc011azx.2:exon4:c.1476_1477del:p.N494fs)", annot);
	}

	/**
	 * <P>
	 * annovar: OR5H6:uc003dsi.1:exon1:c.369_377del:p.123_126del, chr3:97983497TGTAACCAC>-
	 * </P>
	 */
	@Test
	public void testFSDeletionVar8() throws AnnotationException {
		byte chr = 3;
		int pos = 97983497;
		String ref = "TGTAACCAC";
		String alt = "-";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.NON_FS_DELETION, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("OR5H6(uc003dsi.1:exon1:c.369_377del:p.V124_T126del)", annot);
	}

	/**
	 * <P>
	 * annovar: OR5K2:uc011bgx.2:exon1:c.275_285del:p.92_95del (wrong) chr3:98216799TTTCCCTCTAT>- Mutalzyer:
	 * NM_001004737.1(OR5K2_v001):c.275_285del NM_001004737.1(OR5K2_i001):p.(Ile92Argfs*26) Thus p.I92Rfs
	 * </P>
	 */
	@Test
	public void testFSDeletionVar9() throws AnnotationException {
		byte chr = 3;
		int pos = 98216799;
		String ref = "TTTCCCTCTAT";
		String alt = "-";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.FS_DELETION, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("OR5K2(uc011bgx.2:exon1:c.275_285del:p.I92fs)", annot);
	}

	/**
	 * <P>
	 * annovar: PCDHA7:uc003lhq.2:exon1:c.1503_1507del:p.501_503del,PCDHA7:uc011dac.2:exon1:c.1503_1507del:p.501_503del,
	 * chr5:140215471GCGCG>- Thus = NM_031852.1:c.1503_1507del Mutalyzer: NM_031852.1(PCDHA7_v001):c.1503_1507del
	 * NM_031852.1(PCDHA7_i001):p.(Glu501Aspfs*96) Thus p.E501Dfs
	 * </P>
	 */
	@Test
	public void testFSDeletionVar10() throws AnnotationException {
		byte chr = 5;
		int pos = 140215471;
		String ref = "GCGCG";
		String alt = "-";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.FS_DELETION, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals(
				"PCDHA7(uc003lhq.2:exon1:c.1503_1507del:p.E501fs,uc011dac.2:exon1:c.1503_1507del:p.E501fs)", annot);
	}

	/**
	 * <P>
	 * annovar: PCDHB18:uc003ljc.1:exon1:c.1219_1221del:p.407_407del, chr5:140615504GTC>-
	 * </P>
	 */
	@Test
	public void testFSDeletionVar11() throws AnnotationException {
		byte chr = 5;
		int pos = 140615504;
		String ref = "GTC";
		String alt = "-";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.NON_FS_DELETION, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("PCDHB18(uc003ljc.1:exon1:c.1219_1221del:p.V407del)", annot);
	}

	/**
	 * <P>
	 * annovar: OR2B2:uc011dkw.2:exon1:c.985delA:p.T329fs, chr6:27879113T>-
	 * </P>
	 */
	@Test
	public void testFSDeletionVar12() throws AnnotationException {
		byte chr = 6;
		int pos = 27879113;
		String ref = "T";
		String alt = "-";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.FS_DELETION, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("OR2B2(uc011dkw.2:exon1:c.985del:p.T329fs)", annot);
	}

	/**
	 * <P>
	 * annovar: KCNK17:uc003ooo.3:exon2:c.318_320del:p.106_107del,KCNK17:uc003oop.3:exon2:c.318_320del:p.106_107del,
	 * chr6:39278701AAG>-
	 * </P>
	 */
	@Test
	public void testFSDeletionVar13() throws AnnotationException {
		byte chr = 6;
		int pos = 39278701;
		String ref = "AAG";
		String alt = "-";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.NON_FS_DELETION, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("KCNK17(uc003ooo.3:exon2:c.323_325del:p.S108del,uc003oop.3:exon2:c.324_326del:p.F109del)",
				annot);
	}

	/**
	 * <P>
	 * annovar:
	 * KIAA2026:uc010mht.3:exon4:c.1539_1541del:p.513_514del,KIAA2026:uc003zjq.4:exon8:c.4014_4016del:p.1338_1339del,
	 * chr9:5921980GTT>-
	 * </P>
	 */
	@Test
	public void testFSDeletionVar14() throws AnnotationException {
		byte chr = 9;
		int pos = 5921980;
		String ref = "GTT";
		String alt = "-";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.NON_FS_DELETION, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals(
				"KIAA2026(uc010mht.3:exon4:c.1542_1544del:p.T517del,uc003zjq.4:exon8:c.4017_4019del:p.T1342del)", annot);
	}

	/**
	 * <P>
	 * annovar: AGAP6:uc001jix.4:exon8:c.791_792del:p.264_264del, chr10:51768676AA>- NM_001077665.2:c.791_792del
	 * Mutalyzer: NM_001077665.2(AGAP6_v001):c.791_792del NM_001077665.2(AGAP6_i001):p.(Lys264Argfs*10) Thus p.K264Rfs
	 * </P>
	 */
	@Test
	public void testFSDeletionVar16() throws AnnotationException {
		byte chr = 10;
		int pos = 51768676;
		String ref = "AA";
		String alt = "-";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.FS_DELETION, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("AGAP6(uc001jix.4:exon8:c.791_792del:p.K264fs)", annot);
	}

	/**
	 * <P>
	 * annovar: AGAP6:uc001jix.4:exon8:c.890_892del:p.297_298del, chr10:51768775TGA>-
	 * </P>
	 */
	@Test
	public void testFSDeletionVar17() throws AnnotationException {
		byte chr = 10;
		int pos = 51768775;
		String ref = "TGA";
		String alt = "-";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.NON_FS_DELETION, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("AGAP6(uc001jix.4:exon8:c.890_892del:p.L297_K298delinsQ)", annot);
	}

	/**
	 * <P>
	 * annovar: OR5M1:uc001nja.1:exon1:c.423_426del:p.141_142del, chr11:56380553GACA>- Thus NM_001004740:c.423_426del
	 * Mutalzyer NM_001004740(OR5M1_v001):c.429_432del NM_001004740(OR5M1_i001):p.(Cys143Trpfs*19) Thus p.C143Wfs
	 * </P>
	 */
	@Test
	public void testFSDeletionVar18() throws AnnotationException {
		byte chr = 11;
		int pos = 56380553;
		String ref = "GACA";
		String alt = "-";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.FS_DELETION, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("OR5M1(uc001nja.1:exon1:c.429_432del:p.C143fs)", annot);
	}

	/**
	 * <P>
	 * annovar: FAM90A1:uc001quh.2:exon5:c.376delC:p.P126fs,FAM90A1:uc001qui.2:exon6:c.376delC:p.P126fs,
	 * chr12:8376101G>-
	 * </P>
	 */
	@Test
	public void testFSDeletionVar19() throws AnnotationException {
		byte chr = 12;
		int pos = 8376101;
		String ref = "G";
		String alt = "-";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.FS_DELETION, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("FAM90A1(uc001qui.2:exon6:c.377del:p.P126fs,uc001quh.2:exon5:c.377del:p.P126fs)", annot);
	}

	/**
	 * <P>
	 * annovar: SETD8:uc001uew.3:exon5:c.542_543del:p.181_181del, chr12:123880924TT>- NM_020382:c.542_543del Mutalyzer:
	 * NM_020382(SETD8_v001):c.542_543del NM_020382(SETD8_i001):p.(Leu181Hisfs*20) THus p.L181Hfs
	 * </P>
	 */
	@Test
	public void testFSDeletionVar21() throws AnnotationException {
		byte chr = 12;
		int pos = 123880924;
		String ref = "TT";
		String alt = "-";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.FS_DELETION, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("SETD8(uc001uew.3:exon5:c.542_543del:p.L181fs)", annot);
	}

	/**
	 * <P>
	 * annovar: FAM194B:uc001vam.1:exon2:c.398_415del:p.133_139del,FAM194B:uc001val.2:exon3:c.398_415del:p.133_139del,
	 * chr13:46170726ACTCTTCCTCCTCCAGAT>-
	 * expected:<FAM194B(uc001va[m.1:exon2:c.398_415del:p.133_139del,uc001val.2:exon3]:c.398_415del:p.133_...> but
	 * was:<FAM194B(uc001va[l.2:exon3:c.398_415del:p.133_139del,uc001vam.1:exon2]:c.398_415del:p.133_...> (order
	 * changed) expected:<FAM194B(uc001va[l.2:exon3:c.398_415del:p.133_139del,uc001vam.1:exon2]:c.398_415del:p.133_...>
	 * but was: <FAM194B(uc001va[m.1:exon2:c.398_415del:p.133_139del,uc001val.2:exon3]:c.398_415del:p.133_...>
	 * </P>
	 */
	@Test
	public void testFSDeletionVar22() throws AnnotationException {
		byte chr = 13;
		int pos = 46170726;
		String ref = "ACTCTTCCTCCTCCAGAT";
		String alt = "-";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.NON_FS_DELETION, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals(
				"FAM194B(uc001val.2:exon3:c.404_421del:p.E135_L140del,uc001vam.1:exon2:c.404_421del:p.E135_L140del)",
				annot);
	}

	/**
	 * <P>
	 * annovar: CCDC33:uc002axo.3:exon2:c.100_102del:p.34_34del, chr15:74536404AAG>-
	 * </P>
	 */
	@Test
	public void testFSDeletionVar23() throws AnnotationException {
		byte chr = 15;
		int pos = 74536404;
		String ref = "AAG";
		String alt = "-";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.NON_FS_DELETION, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("CCDC33(uc002axo.4:exon2:c.100_102del:p.K34del)", annot);
	}

	/**
	 * <P>
	 * annovar: CCDC33:uc002axo.3:exon2:c.100_102del:p.34_34del, chr15:74536404AAG>-
	 * </P>
	 */
	@Test
	public void testFSDeletionVar24() throws AnnotationException {
		byte chr = 15;
		int pos = 74536404;
		String ref = "AAG";
		String alt = "-";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.NON_FS_DELETION, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("CCDC33(uc002axo.4:exon2:c.100_102del:p.K34del)", annot);
	}

	/**
	 * <P>
	 * annovar: LOC645752:uc010bky.2:exon14:c.832_834del:p.278_278del, chr15:78208899CTC>-
	 * </P>
	 */
	@Test
	public void testFSDeletionVar25() throws AnnotationException {
		byte chr = 15;
		int pos = 78208899;
		String ref = "CTC";
		String alt = "-";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.NON_FS_DELETION, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("LOC645752(uc010bky.2:exon14:c.842_844del:p.E281del)", annot);
	}

	/**
	 * <P>
	 * annovar: SENP3:uc002ghm.3:exon8:c.1307delA:p.K436fs, chr17:7470288A>-
	 * </P>
	 */
	@Test
	public void testFSDeletionVar26() throws AnnotationException {
		byte chr = 17;
		int pos = 7470288;
		String ref = "A";
		String alt = "-";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.FS_DELETION, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("SENP3(uc002ghm.3:exon8:c.1308del:p.K436fs)", annot);
	}

	/**
	 * <P>
	 * annovar: ATAD5:uc002hfs.1:exon2:c.861_866del:p.287_289del,ATAD5:uc002hft.1:exon1:c.552_557del:p.184_186del,
	 * chr17:29161960GTCAAT>-
	 * </P>
	 */
	@Test
	public void testFSDeletionVar28() throws AnnotationException {
		byte chr = 17;
		int pos = 29161960;
		String ref = "GTCAAT";
		String alt = "-";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.NON_FS_DELETION, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals(
				"ATAD5(uc002hft.1:exon1:c.552_557del:p.M184_S185del,uc002hfs.1:exon2:c.864_869del:p.M288_S289del)",
				annot);
	}

	/**
	 * <P>
	 * annovar: DCAF7:uc002jbc.3:exon6:c.560delG:p.G187fs, chr17:61660895G>-
	 * </P>
	 */
	@Test
	public void testFSDeletionVar29() throws AnnotationException {
		byte chr = 17;
		int pos = 61660895;
		String ref = "G";
		String alt = "-";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.FS_DELETION, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("DCAF7(uc002jbc.4:exon6:c.561del:p.G187fs)", annot);
	}

	/**
	 * <P>
	 * annovar: DEFB126:uc002wcx.3:exon2:c.317_318del:p.106_106del, chr20:126314CC>- Mutalzyer:
	 * NM_030931(DEFB126_v001):c.317_318del NM_030931(DEFB126_i001):p.(Pro106Argfs*27) p.P106Rfs
	 */
	@Test
	public void testFSDeletionVar32() throws AnnotationException {
		byte chr = 20;
		int pos = 126314;
		String ref = "CC";
		String alt = "-";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.FS_DELETION, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("DEFB126(uc002wcx.3:exon2:c.317_318del:p.P106fs)", annot);
	}

	/**
	 * <P>
	 * annovar: PLAC4:uc002yyz.3:exon1:c.70_88del:p.24_30del, chr21:42551468GTGTCAGGGTGAGTGAGGG>- minus strand
	 * Mutalyzer: NM_182832(PLAC4_v001):c.72_90del NM_182832(PLAC4_i001):p.(Ser25Hisfs*78) Thus p.S25Hfs
	 * </P>
	 */
	@Test
	public void testFSDeletionVar33() throws AnnotationException {
		byte chr = 21;
		int pos = 42551468;
		String ref = "GTGTCAGGGTGAGTGAGGG";
		String alt = "-";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.FS_DELETION, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("PLAC4(uc002yyz.3:exon1:c.72_90del:p.S25fs)", annot);
	}

	/**
	 * <P>
	 * annovar: ZNF135:uc010yhq.2:exon5:c
	 * .1992_1997del:p.664_666del,ZNF135:uc002qre.3:exon5:c.1956_1961del:p.652_654del,ZNF135:uc002qrg.3:exon4:c.2028_2033del:p.676_678del,ZNF135:uc002qrd.2:exon5:c.1152_1157del:p.384_386del,ZNF135:uc010yhr.2:exon3:c.1419_1424del:p.473_475del,ZNF135:uc002qrf.3:exon5:c.1830_1835del:p.
	 * 6 1 0 _ 6 1 2 d e l , chr19:58579808CCAGAG>-
	 * </P>
	 */
	@Test
	public void testFSDeletionVar31() throws AnnotationException {
		byte chr = 19;
		int pos = 58579808;
		String ref = "CCAGAG";
		String alt = "-";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.NON_FS_DELETION, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals(
				"ZNF135(uc002qrd.2:exon5:c.1152_1157del:p.H384_R386delinsQ,uc010yhr.2:exon3:c.1419_1424del:p.H473_R475delinsQ,uc002qrf.3:exon5:c.1830_1835del:p.H610_R612delinsQ,uc002qre.3:exon5:c.1956_1961del:p.H652_R654delinsQ,uc010yhq.2:exon5:c.1992_1997del:p.H664_R666delinsQ,uc002qrg.3:exon4:c.2028_2033del:p.H676_R678delinsQ)",
				annot);
	}

}