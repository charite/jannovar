package jannovar.annotation;

import jannovar.common.Constants;
import jannovar.common.VariantType;
import jannovar.exception.AnnotationException;
import jannovar.exception.JannovarException;
import jannovar.exception.VCFParseException;
import jannovar.io.SerializationManager;
import jannovar.reference.Chromosome;
import jannovar.reference.TranscriptModel;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for generating duplication annotations.
 *
 * The corrected positions are directly generated from VCF such that it is easy to cross-validate with other tools such
 * as Annovar.
 *
 * @author Marten Jäger
 * @author Manuel Holtgrewe
 */
public class DuplicationAnnotationTest extends AnnotationTest implements Constants {

	/** Used for annotation creation. */
	private VariantAnnotator annotator = null;

	@Override
	@Before
	public void setUp() throws JannovarException {
		super.setUp();

		// fill this.annotator
		ArrayList<TranscriptModel> kgList = null;
		java.net.URL url = SynonymousAnnotationTest.class.getResource(UCSCserializationTestFileName);
		String path = url.getPath();
		SerializationManager manager = new SerializationManager();
		kgList = manager.deserializeKnownGeneList(path);
		annotator = new VariantAnnotator(Chromosome.constructChromosomeMapWithIntervalTree(kgList));
	}

	@Override
	@After
	public void tearDown() throws JannovarException {
		super.tearDown();
	}

	/**
	 * This is the test for the in-frame duplication of a single triplicate / one amino acids '+' strand
	 *
	 * Mutalyzer: NM_001005495(OR2T3_v001):c.769_771dup NM_001005495(OR2T3_i001):p.(Phe257dup)
	 */
	@Test
	public void testDuplicationVar1() throws JannovarException, IOException {
		String s = "1	248637422	.	C	CTTC	100	PASS	QD=11.71;	GT:GQ	0/1:99	0/0:99	0/1:99	0/0:99	0/1:99";
		VariantDataCorrector corr = variantContextGenerator.correctedVarDataForLine(s);

		byte chr = 1;
		Assert.assertEquals(248637423, corr.position);
		Assert.assertEquals("-", corr.ref);
		Assert.assertEquals("TTC", corr.alt);

		AnnotationList ann = annotator.getAnnotationList(chr, corr.position, corr.ref, corr.alt);

		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.NON_FS_DUPLICATION, varType);

		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("OR2T3(uc001iel.1:exon1:c.769_771dup:p.F257dup)", annot);
	}

	/**
	 * annovar: FRG1:uc003izs.3:exon6:c.439_440insA:p.M147fs, chr4:190878559->A FRG1 is on the "+" strand
	 *
	 * Jannovar says: FRG1(uc003izs.3:exon6:c.438dupA:p.M147fs) expected <...c003izs.3:exon6:c.43[9]dupA:p.M147fs)> but
	 * was: <...c003izs.3:exon6:c.43[8]dupA:p.M147fs)> is uc003izs.3 NM_004477.2
	 *
	 * Mutalyzer says NM_004477.2(FRG1_v001):c.439dup NM_004477.2(FRG1_i001):p.(Met147Asnfs*8)
	 *
	 * Raw variant 1: duplication from 630 to 630 GAACCAGTCTTTCAAAATGGGAAAA - TGGCTTTGTTGGCCTCAAATAGCTG
	 * GAACCAGTCTTTCAAAATGGGAAAA A TGGCTTTGTTGGCCTCAAATAGCTG
	 *
	 * Thus, 439 and not 438 is the correct number for the duplicated nucleotide. Jannovar lists refvarstart as 630.
	 * This is the last "A" of a polyA tract in the gene (see genbank L76159.1). Jannovar lists refcdsstart as 192. This
	 * is the position of the start of the start codon in FRG1 (L76159.1). <...c003izs.3:exon6:c.43[9]dupA:p.M147fs)>
	 * but was: <...c003izs.3:exon6:c.43[8]dupA:p.M147fs)>
	 */
	@Test
	public void testInsertionVar29y() throws AnnotationException, VCFParseException, IOException {
		String s = "4	190878559	.	A	AA	100	PASS	QD=11.71;	GT:GQ	0/1:99	0/0:99	0/1:99	0/0:99	0/1:99";
		VariantDataCorrector corr = variantContextGenerator.correctedVarDataForLine(s);

		byte chr = 4;
		Assert.assertEquals(190878560, corr.position);
		Assert.assertEquals("-", corr.ref);
		Assert.assertEquals("A", corr.alt);

		AnnotationList ann = annotator.getAnnotationList(chr, corr.position, corr.ref, corr.alt);

		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.FS_DUPLICATION, varType);

		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("FRG1(uc003izs.3:exon6:c.439dup:p.M147fs)", annot);
	}

	/**
	 * This is the test for the in-frame duplication of a single triplicate / one amino acids '+' strand
	 */
	@Test
	public void testDuplicationVar9test() throws AnnotationException, VCFParseException, IOException {
		String s = "9	137968918	.	A	AAGA	100	PASS	QD=11.71;	GT:GQ	0/1:99	0/0:99	0/1:99	0/0:99	0/1:99";
		VariantDataCorrector corr = variantContextGenerator.correctedVarDataForLine(s);

		byte chr = 9;
		Assert.assertEquals(137968919, corr.position);
		Assert.assertEquals("-", corr.ref);
		Assert.assertEquals("AGA", corr.alt);

		AnnotationList ann = annotator.getAnnotationList(chr, corr.position, corr.ref, corr.alt);

		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.NON_FS_DUPLICATION, varType);

		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("OLFM1(uc010naq.2:exon2:c.325_327dup:p.R109dup)", annot);
	}

	@Test
	public void testDuplicationVar10() throws JannovarException, IOException {
		String s = "1	248637607	.	A	AA	50	PASS	NS=3;DP=9;AA=G	GT:GQ:DP	0/1:35:4";
		VariantDataCorrector corr = variantContextGenerator.correctedVarDataForLine(s);

		byte chr = 1;
		Assert.assertEquals(248637608, corr.position);
		Assert.assertEquals("-", corr.ref);
		Assert.assertEquals("A", corr.alt);

		AnnotationList ann = annotator.getAnnotationList(chr, corr.position, corr.ref, corr.alt);

		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.FS_DUPLICATION, varType);

		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("OR2T3(uc001iel.1:exon1:c.956dup:p.(=))", annot);
	}

	/**
	 * This is the test for the in-frame duplication of six nuc.acids / two amino acids '+' strand
	 *
	 * Mutalyzer: NM_001005495.1(OR2T3_v001):c.766_771dup NM_001005495.1(OR2T3_i001):p.(Leu256_Phe257dup)
	 *
	 *
	 * <...6_771dupCTCTTC:p.L25[6_F257]dup)> but was: <...6_771dupCTCTTC:p.L25[4_F256]dup)>
	 */
	@Test
	public void testDuplicationVar2() throws JannovarException, IOException {
		String s = "1	248637422	.	C	CCTCTTC	100	PASS	QD=11.71;	GT:GQ	0/1:99	0/0:99	0/1:99	0/0:99	0/1:99";
		VariantDataCorrector corr = variantContextGenerator.correctedVarDataForLine(s);

		byte chr = 1;
		Assert.assertEquals(248637423, corr.position);
		Assert.assertEquals("-", corr.ref);
		Assert.assertEquals("CTCTTC", corr.alt);

		AnnotationList ann = annotator.getAnnotationList(chr, corr.position, corr.ref, corr.alt);

		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.NON_FS_DUPLICATION, varType);

		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("OR2T3(uc001iel.1:exon1:c.766_771dup:p.L256_F257dup)", annot);
	}

	/**
	 * This is the test for the in-frame duplication of 12 nuc.acids / tree amino acids '+' strand
	 */
	@Test
	public void testDuplicationVar3() throws JannovarException, IOException {
		String s = "1	248637422	.	C	CCTGCTGCTCTTC	100	PASS	QD=11.71;	GT:GQ	0/1:99	0/0:99	0/1:99	0/0:99	0/1:99";
		VariantDataCorrector corr = variantContextGenerator.correctedVarDataForLine(s);

		byte chr = 1;
		Assert.assertEquals(248637423, corr.position);
		Assert.assertEquals("-", corr.ref);
		Assert.assertEquals("CTGCTGCTCTTC", corr.alt);

		AnnotationList ann = annotator.getAnnotationList(chr, corr.position, corr.ref, corr.alt);

		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.NON_FS_DUPLICATION, varType);

		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("OR2T3(uc001iel.1:exon1:c.760_771dup:p.L254_F257dup)", annot);
	}

	/**
	 * This is the test for the in-frame duplication of a single triplicate / one amino acids '-' strand
	 *
	 * Mutalyzer: NM_022149.4(MAGEF1_v001):c.424_426dup NM_022149.4(MAGEF1_i001):p.(Thr142dup)
	 */
	@Test
	public void testDuplicationVar4() throws JannovarException, IOException {
		String s = "3	184429186	.	A	AAGT	100	PASS	QD=11.71;	GT:GQ	0/1:99	0/0:99	0/1:99	0/0:99	0/1:99";
		VariantDataCorrector corr = variantContextGenerator.correctedVarDataForLine(s);

		byte chr = 3;
		Assert.assertEquals(184429187, corr.position);
		Assert.assertEquals("-", corr.ref);
		Assert.assertEquals("AGT", corr.alt);

		AnnotationList ann = annotator.getAnnotationList(chr, corr.position, corr.ref, corr.alt);

		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.NON_FS_DUPLICATION, varType);

		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("MAGEF1(uc003fpa.3:exon1:c.424_426dup:p.T142dup)", annot);
	}

	/**
	 *
	 * This is the test for the in-frame duplication of 6 nuc.acids / two amino acids '-' strand
	 *
	 * mutalzyer: NM_022149.4(MAGEF1_v001):c.439_444dup NM_022149.4(MAGEF1_i001):p.(Asn147_Lys148dup)
	 */
	@Test
	public void testDuplicationVar5() throws JannovarException, IOException {
		String s = "3	184429171	.	T	TTTTGTT	100	PASS	QD=11.71;	GT:GQ	0/1:99	0/0:99	0/1:99	0/0:99	0/1:99";
		VariantDataCorrector corr = variantContextGenerator.correctedVarDataForLine(s);

		byte chr = 3;
		Assert.assertEquals(184429172, corr.position);
		Assert.assertEquals("-", corr.ref);
		Assert.assertEquals("TTTGTT", corr.alt);

		AnnotationList ann = annotator.getAnnotationList(chr, corr.position, corr.ref, corr.alt);

		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.NON_FS_DUPLICATION, varType);

		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("MAGEF1(uc003fpa.3:exon1:c.439_444dup:p.N147_K148dup)", annot);
	}

	/**
	 * This is the test for the in-frame duplication of 12 nuc.acids / three amino acids '-' strand
	 */
	@Test
	public void testDuplicationVar6() throws JannovarException, IOException {
		String s = "3	184429171	.	T	TTTTTAGTTTGTT	100	PASS	QD=11.71;	GT:GQ	0/1:99	0/0:99	0/1:99	0/0:99	0/1:99";
		VariantDataCorrector corr = variantContextGenerator.correctedVarDataForLine(s);

		byte chr = 3;
		Assert.assertEquals(184429172, corr.position);
		Assert.assertEquals("-", corr.ref);
		Assert.assertEquals("TTTTAGTTTGTT", corr.alt);

		AnnotationList ann = annotator.getAnnotationList(chr, corr.position, corr.ref, corr.alt);

		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.NON_FS_DUPLICATION, varType);

		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("MAGEF1(uc003fpa.3:exon1:c.439_450dup:p.N147_K150dup)", annot);
	}

	/**
	 *
	 * This is the test for the offset (+1) duplication of a single triplicate / one amino acids shifting the Stop-codon
	 * '+' strand
	 *
	 * mutalyzer: NM_001005495.1(OR2T3_v001):c.949_954dup NM_001005495.1(OR2T3_i001):p.(*319Gluext*2) I think mutalyzer
	 * is wrong here, the stop is right after the duplication.
	 */
	@Test
	public void testDuplicationVar7() throws JannovarException, IOException {
		String s = "1	248637605	.	G	GGAAAAG	100	PASS	QD=11.71;	GT:GQ	0/1:99	0/0:99	0/1:99	0/0:99	0/1:99";
		VariantDataCorrector corr = variantContextGenerator.correctedVarDataForLine(s);

		byte chr = 3;
		Assert.assertEquals(248637606, corr.position);
		Assert.assertEquals("-", corr.ref);
		Assert.assertEquals("GAAAAG", corr.alt);

		AnnotationList ann = annotator.getAnnotationList(chr, corr.position, corr.ref, corr.alt);

		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.NON_FS_DUPLICATION, varType);

		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("OR2T3(uc001iel.1:exon1:c.949_954dup:p.*319ext*2)", annot);
	}

	/**
	 * annovar: MAGEF1:uc003fpa.3:exon1:c.456_457insGGA:p.L152delinsLE, chr3:184429154->TCC
	 *
	 * uc003fpa.3:exon1:c.456_458dupGGA:p.L152delinsLG
	 *
	 * Refseq: NM_022149
	 *
	 * Mutalyzer: Note that the position gets shifted downstream to bcome the most 3' position.
	 *
	 * NM_022149.4(MAGEF1_v001):c.474_476dup NM_022149.4(MAGEF1_i001):p.(Glu158dup)
	 */
	@Test
	public void testInsertionVar25() throws JannovarException, IOException {
		String s = "3	184429154	.	C	CTCC	100	PASS	QD=11.71;	GT:GQ	0/1:99	0/0:99	0/1:99	0/0:99	0/1:99";
		VariantDataCorrector corr = variantContextGenerator.correctedVarDataForLine(s);

		byte chr = 3;
		Assert.assertEquals(184429155, corr.position);
		Assert.assertEquals("-", corr.ref);
		Assert.assertEquals("TCC", corr.alt);

		AnnotationList ann = annotator.getAnnotationList(chr, corr.position, corr.ref, corr.alt);

		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.NON_FS_DUPLICATION, varType);

		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("MAGEF1(uc003fpa.3:exon1:c.474_476dup:p.E158dup)", annot);

		// VCFLine line = new VCFLine(s);
		//
		// Variant v = line.toVariant();
		// int pos = v.get_position();
		// String ref = v.get_ref();
		// String alt = v.get_alt();
		// byte chr = (byte) v.get_chromosome();
		//
		// AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		// VariantType varType = ann.getVariantType();
		// Assert.assertEquals(184429154, pos);
		// Assert.assertEquals("-", ref);
		// Assert.assertEquals("TCC", alt);
		// String annot = ann.getVariantAnnotation();
		// Assert.assertEquals(VariantType.NON_FS_DUPLICATION, varType);
		// Assert.assertEquals("MAGEF1(uc003fpa.3:exon1:c.474_476dup:p.E158dup)", annot);
	}

}
