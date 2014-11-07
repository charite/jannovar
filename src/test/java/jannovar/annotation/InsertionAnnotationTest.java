package jannovar.annotation;

import jannovar.annotation.AnnotationList;
import jannovar.annotation.VariantAnnotator;
import jannovar.common.Constants;
import jannovar.common.VariantType;
import jannovar.exception.AnnotationException;
import jannovar.exception.JannovarException;
import jannovar.exome.Variant;
import jannovar.genotype.GenotypeFactoryA;
import jannovar.genotype.MultipleGenotypeFactory;
import jannovar.io.SerializationManager;
import jannovar.io.VCFLine;
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
public class InsertionAnnotationTest implements Constants {

	private VariantAnnotator annotator = null;

	/** This is needed for the VCF line initialization, but is not used for anything else. */
	private static GenotypeFactoryA genofactory = null;

	@Before
	public void setUp() throws IOException, JannovarException {
		ArrayList<TranscriptModel> kgList = null;
		java.net.URL url = SynonymousAnnotationTest.class.getResource(UCSCserializationTestFileName);
		String path = url.getPath();
		SerializationManager manager = new SerializationManager();
		kgList = manager.deserializeKnownGeneList(path);
		annotator = new VariantAnnotator(Chromosome.constructChromosomeMapWithIntervalTree(kgList));
		genofactory = new MultipleGenotypeFactory();
		VCFLine.setGenotypeFactory(genofactory);
	}

	/**
	 * <P>
	 * annovar:
	 * FAM178B:uc002sxl.4:exon13:c.1579_1580insCGAT:p.L527fs,FAM178B:uc002sxk.4:exon7:c.628_629insCGAT:p.L210fs,
	 * chr2:97568428->ATCG
	 * </P>
	 */
	@Test
	public void testInsertionVar11() throws JannovarException {
		// byte chr = 2;
		// int pos = 97568428;
		String s = "2	97568428	.	G	GATCG	100	PASS	QD=11.71;	GT:GQ	0/1:99	0/0:99	0/1:99	0/0:99	0/1:99";
		VCFLine line = new VCFLine(s);
		Variant v = line.toVariant();
		int pos = v.get_position();
		String ref = v.get_ref();
		String alt = v.get_alt();
		byte chr = (byte) v.get_chromosome();
		// String ref = "-";
		// String alt = "ATCG";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.FS_INSERTION, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals(
				"FAM178B(uc002sxk.4:exon7:c.628_629insGATC:p.L210fs,uc002sxl.4:exon13:c.1579_1580insGATC:p.L527fs)",
				annot);
	}

	/**
	 * <P>
	 * annovar: RANBP2:uc002tem.4:exon16:c.2265_2266insCC:p.D755fs, chr2:109371423->CC
	 * </P>
	 */
	@Test
	public void testInsertionVar12() throws AnnotationException {
		byte chr = 2;
		int pos = 109371423;
		String ref = "-";
		String alt = "CC";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.FS_INSERTION, varType);
		String annot = ann.getVariantAnnotation();
		// Assert.assertEquals("RANBP2(uc002tem.4:exon16:c.2265_2266insCC:p.D756fs)",annot);
		Assert.assertEquals("RANBP2(uc002tem.4:exon16:c.2265_2266insCC:p.Y756fs)", annot);
	}

	/**
	 * <P>
	 * annovar: FBXL21 chr5:135272375->A
	 * </P>
	 * mutalzyer: NM_012159(FBXL21_v001):c.93_94insA NM_012159(FBXL21_i001):p.(Gln32Thrfs*39) Note that the Asparagine
	 * on position 32 is the aminoacid affected by the insertion. The coded amino acid is unchanged, but the following
	 * aminoacids are. This is why mutalyzer has this output. This needs to be improved in the future for Jannovar but
	 * is not incorrect for now, if suboptimal.
	 */
	@Test
	public void testfsInsertionVar152() throws AnnotationException {
		byte chr = 5;
		int pos = 135272376;
		String ref = "-";
		String alt = "A";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();

		String annot = ann.getVariantAnnotation();
		Assert.assertEquals(VariantType.FS_INSERTION, varType);
		Assert.assertEquals("FBXL21(uc031sld.1:exon5:c.93_94insA:p.Q32fs)", annot);
	}

	/**
	 * <P>
	 * annovar: RANBP2:uc002tem.4:exon20:c.6318_6319insAGCG:p.M2106fs, chr2:109383313->AGCG
	 * </P>
	 */
	@Test
	public void testInsertionVar13() throws AnnotationException {
		byte chr = 2;
		int pos = 109383313;
		String ref = "-";
		String alt = "AGCG";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.FS_INSERTION, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("RANBP2(uc002tem.4:exon20:c.6318_6319insAGCG:p.W2107fs)", annot);
	}

	/**
	 * <P>
	 * annovar: RANBP2:uc002tem.4:exon20:c.6882_6883insCAT:p.D2294delinsDH, chr2:109383877->CAT
	 * </P>
	 */
	@Test
	public void testInsertionVar14() throws AnnotationException {
		byte chr = 2;
		int pos = 109383877;
		String ref = "-";
		String alt = "CAT";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.NON_FS_INSERTION, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("RANBP2(uc002tem.4:exon20:c.6882_6883insCAT:p.D2294_E2295insH)", annot);
	}

	/**
	 * <P>
	 * annovar: TTN:uc002umz.1:exon112:c.21594_21595insACTT:p.K7198fs, chr2:179519685->AAGT
	 * </P>
	 */
	@Test
	public void testInsertionVar17() throws AnnotationException {
		byte chr = 2;
		int pos = 179519685;
		String ref = "-";
		String alt = "AAGT";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.FS_INSERTION, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals(
				"TTN(uc002umz.1:exon112:c.21594_21595insCTTA:p.V7199fs,uc031rqc.1:exon190:c.38076_38077insCTTA:p.V12693fs)",
				annot);
	}

	/**
	 * <P>
	 * annovar: CPS1:uc010fur.3:exon2:c.15_16insTTC:p.I5delinsIF, chr2:211421454->TTC
	 * </P>
	 */
	@Test
	public void testInsertionVar18() throws AnnotationException {
		byte chr = 2;
		int pos = 211421454;
		String ref = "-";
		String alt = "TTC";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.NON_FS_INSERTION, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("CPS1(uc010fur.3:exon2:c.15_16insTTC:p.I5_K6insF)", annot);
	}

	/**
	 * <P>
	 * annovar: MUC4:uc021xjp.1:exon2:c.8108_8109insTG:p.T2703fs, chr3:195510343->CA
	 * </P>
	 */
	@Test
	public void testInsertionVar26() throws AnnotationException {
		byte chr = 3;
		int pos = 195510343;
		String ref = "-";
		String alt = "CA";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.FS_INSERTION, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("MUC4(uc021xjp.1:exon2:c.8107_8108insTG:p.T2703fs)", annot);
	}

	/**
	 * <P>
	 * annovar: MUC4:uc021xjp.1:exon2:c.6858_6859insCAG:p.T2286delinsTS, chr3:195511593->CTG
	 * </P>
	 */
	@Test
	public void testInsertionVar27() throws AnnotationException {
		byte chr = 3;
		int pos = 195511593;
		String ref = "-";
		String alt = "CTG";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.NON_FS_INSERTION, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("MUC4(uc021xjp.1:exon2:c.6859_6860insGCA:p.T2286_T2287insS)", annot);
	}

	/**
	 * <P>
	 * annovar: FRG1:uc003izs.3:exon7:c.608_609insGACT:p.K203fs, chr4:190881973->GACT
	 * </P>
	 */
	@Test
	public void testInsertionVar30() throws AnnotationException {
		byte chr = 4;
		int pos = 190881973;
		String ref = "-";
		String alt = "GACT";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.FS_INSERTION, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("FRG1(uc003izs.3:exon7:c.608_609insGACT:p.Q204fs)", annot);
	}

	/**
	 * <P>
	 * annovar: PRDM9:uc003jgo.3:exon11:c.1147_1148insTGA:p.P383delinsLT, chr5:23526344->TGA
	 * </P>
	 */
	@Test
	public void testInsertionVar31() throws AnnotationException {
		byte chr = 5;
		int pos = 23526344;
		String ref = "-";
		String alt = "TGA";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.NON_FS_INSERTION, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("PRDM9(uc003jgo.3:exon11:c.1147_1148insTGA:p.P383delinsLT)", annot);
	}

	/**
	 * <P>
	 * annovar: SCAMP1:uc003kfl.3:exon8:c.730_731insT:p.C244fs, chr5:77745856->T
	 * </P>
	 * -- According to mutalyzer, p.(Asn244Ilefs*52), thus should be p.N244fs (this is what jannovar says, annovar finds
	 * a "C")
	 */
	@Test
	public void testInsertionVar32() throws AnnotationException {
		byte chr = 5;
		int pos = 77745856;
		String ref = "-";
		String alt = "T";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.FS_INSERTION, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("SCAMP1(uc003kfl.3:exon8:c.730_731insT:p.N244fs)", annot);
	}

	/**
	 * <P>
	 * annovar: PCDHB10:uc003lix.3:exon1:c.1806_1807insATGC:p.L602fs, chr5:140573931->ATGC
	 * </P>
	 */
	@Test
	public void testInsertionVar34() throws AnnotationException {
		byte chr = 5;
		int pos = 140573931;
		String ref = "-";
		String alt = "ATGC";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.FS_INSERTION, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("PCDHB10(uc003lix.3:exon1:c.1806_1807insATGC:p.S603fs)", annot);
	}

	/**
	 * <P>
	 * annovar: AK098012:uc003nrp.1:exon2:c.254_255insCAAA:p.P85fs, chr6:30782220->TTTG
	 * </P>
	 */
	@Test
	public void testInsertionVar37() throws AnnotationException {
		byte chr = 6;
		int pos = 30782220;
		String ref = "-";
		String alt = "TTTG";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.FS_INSERTION, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("AK098012(uc003nrp.1:exon2:c.255_256insAACA:p.V86fs)", annot);
	}

	/**
	 * <P>
	 * annovar: PRICKLE4:uc011duf.1:exon8:c.863_864insTCT:p.L288delinsLL, chr6:41754575->TCT
	 * </P>
	 */
	@Test
	public void testInsertionVar40() throws AnnotationException {
		byte chr = 6;
		int pos = 41754575;
		String ref = "-";
		String alt = "TCT";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.NON_FS_INSERTION, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("PRICKLE4(uc011duf.1:exon8:c.863_864insTCT:p.L288dup)", annot);
	}

	/**
	 * <P>
	 * annovar: AEBP1:uc003tkb.3:exon1:c.118_119insAAAA:p.G40fs, chr7:44144382->AAAA
	 * </P>
	 */
	@Test
	public void testInsertionVar43() throws AnnotationException {
		byte chr = 7;
		int pos = 44144382;
		String ref = "-";
		String alt = "AAAA";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.FS_INSERTION, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("AEBP1(uc003tkb.4:exon1:c.118_119insAAAA:p.G40fs)", annot);
	}

	/**
	 * <P>
	 * annovar: MUC12:uc003uxo.3:exon2:c.3442_3443insGTA:p.T1148delinsST, chr7:100637286->GTA
	 * </P>
	 */
	@Test
	public void testInsertionVar44() throws AnnotationException {
		byte chr = 7;
		int pos = 100637286;
		String ref = "-";
		String alt = "GTA";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.NON_FS_INSERTION, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("MUC12(uc003uxo.3:exon2:c.3442_3443insGTA:p.S1147dup)", annot);
	}

	/**
	 * <P>
	 * annovar: OLFM1:uc010naq.2:exon2:c.328_329insAA:p.G110fs, chr9:137968919->AA
	 * </P>
	 */
	@Test
	public void testInsertionVar48() throws AnnotationException {
		byte chr = 9;
		int pos = 137968919;
		String ref = "-";
		String alt = "AA";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.FS_INSERTION, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("OLFM1(uc010naq.2:exon2:c.328_329insAA:p.G110fs)", annot);
	}

	/**
	 * <P>
	 *
	 * g19:chr17:g.37830925_37830926insG. This is reported as NM_033419.3:c.440_441insC in Annovar. Correct nomenclature
	 * in HGVS should be: NM_033419.3:c.441dup, NM_033419.3:p.(Asn148Glnfs*15).
	 *
	 * Position 37,830,924 in a "G" Position 37,830,925 is an "A". Position 37,830,926 in a "G" PGAP3 is on the minus
	 * strand. This insertion leads to a duplication of the "G" at 37,830,926
	 *
	 * However, I think there is also a position bug. Correct cDNA code should be:NM_033419.3:c.439dup,
	 * p.(Leu147Profs*16)
	 *
	 * Jannovar gets: PGAP3(uc002hsk.3:exon3:c.288dupC:p.N97fs,uc002hsj.3:exon4:c.441dupC:p.N148fs)
	 *
	 * uc002hsj.3=NM_033419
	 *
	 *
	 * Note:
	 *
	 * Mutalyzer says: M_033419.3(PGAP3_v001):c.441dup NM_033419.3(PGAP3_i001):p.(Asn148Glnfs*15) and
	 * NM_033419(PGAP3_v001):c.288dup NM_033419(PGAP3_i001):p.(Ser97Leufs*66)
	 *
	 * Note that there is a discrepancy between what UCSC says and what the sequences of the several RefSeqs are.
	 * According to the UCSC browser, I think that Jannovar is producing the correct results, i.e., p.Asn97fs
	 *
	 *
	 *
	 * expected: <...exon3:c.288dupC:p.S9[7fs,uc002hsj.3:exon4:c.441dupC:p.N148]fs)> but was:
	 * <...exon3:c.288dupC:p.S9[6fs,uc002hsj.3:exon4:c.441dupC:p.S147]fs)>
	 *
	 * Note NM_033419 = uc002hsk.3 Mutalzyer: NM_033419(PGAP3_v001):c.288dup NM_033419(PGAP3_i001):p.(Ser97Leufs*66)
	 * Note: This is OK, but the insertion does not change the original aa, instead the frameshift starts one codon
	 * downstream, which is why we should have 148 instead of 147
	 * </P>
	 *
	 * @Test public void testInsertionVar49() throws JannovarException { String s =
	 *       "17	37830924	.	G	GG	100	PASS	QD=11.71;	GT:GQ	0/1:99	0/0:99	0/1:99	0/0:99	0/1:99"; VCFLine line = new
	 *       VCFLine(s); Variant v = line.toVariant(); int pos = v.get_position(); String ref = v.get_ref(); String alt
	 *       = v.get_alt(); byte chr = (byte) v.get_chromosome(); Chromosome c = chromosomeMap.get(chr); if (c==null) {
	 *       Assert.fail("Could not identify chromosome \"" + chr + "\""); } else { AnnotationList ann
	 *       =c.getAnnotationList(pos,ref,alt); VariantType varType = ann.getVariantType(); Assert.assertEquals(17,chr);
	 *       Assert.assertEquals("-",ref); Assert.assertEquals("G",alt); Assert.assertEquals(37830924,pos); String annot
	 *       = ann.getVariantAnnotation(); System.out.println(annot);
	 *       Assert.assertEquals(VariantType.FS_DUPLICATION,varType);
	 *       Assert.assertEquals("PGAP3(uc002hsk.3:exon3:c.288dupC:p.S97fs,uc002hsj.3:exon4:c.441dupC:p.N148fs)",annot);
	 *       } }
	 */

	/**
	 * <P>
	 *
	 * This duplication variation should lead to the loss of the translation initiation site
	 * </P>
	 * uc003izs.3 is NM_004477 mutalyzer: NM_004477(FRG1_v001):c.1_2insC NM_004477(FRG1_i001):p.? Note that mutalyzer
	 * says it does not know what happens to the protein but this can be classified as startloss since the start codon
	 * is destroyed.
	 */
	@Test
	public void testInsertionVar50() throws JannovarException {
		// int pos = 190862165;
		String s = "4	190862165	.	A	AC	100	PASS	QD=11.71;	GT:GQ	0/1:99	0/0:99	0/1:99	0/0:99	0/1:99";
		VCFLine line = new VCFLine(s);
		Variant v = line.toVariant();
		int pos = v.get_position();
		String ref = v.get_ref();
		String alt = v.get_alt();
		byte chr = (byte) v.get_chromosome();

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals(4, chr);
		Assert.assertEquals("-", ref);
		Assert.assertEquals("C", alt);
		Assert.assertEquals(VariantType.START_LOSS, varType);
		Assert.assertEquals("FRG1(uc003izs.3:exon1:c.1_2insC:p.M1?)", annot);
	}

	/**
	 * <P>
	 *
	 * This duplication variation should lead to the loss of the translation initiation site
	 * </P>
	 */
	@Test
	public void testInsertionVar51() throws AnnotationException {
		byte chr = 4;
		int pos = 190862166;
		String ref = "-";
		String alt = "A";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals(VariantType.START_LOSS, varType);
		Assert.assertEquals("FRG1(uc003izs.3:exon1:c.2_3insA:p.M1?)", annot);
	}
}