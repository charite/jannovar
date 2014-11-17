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
public class UTR3AnnotationTest implements Constants {

	private VariantAnnotator annotator = null;

	@Before
	public void setUp() throws IOException, JannovarException {
		ArrayList<TranscriptModel> kgList = null;
		java.net.URL url = UTR3AnnotationTest.class.getResource(UCSCserializationTestFileName);
		String path = url.getPath();
		SerializationManager manager = new SerializationManager();
		kgList = manager.deserializeKnownGeneList(path);
		annotator = new VariantAnnotator(Chromosome.constructChromosomeMapWithIntervalTree(kgList));
	}

	/**
	 * <P>
	 * annovar: THAP3 chr1:6693165->TA
	 * </P>
	 */
	@Test
	public void testUTR3Var7() throws AnnotationException {
		byte chr = 1;
		int pos = 6693165;
		String ref = "-";
		String alt = "TA";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.UTR3, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("THAP3(uc001aod.3:c.*28_*29insTA,uc001aoc.3:c.*28_*29insTA)", annot);
	}

	/**
	 * <P>
	 * annovar: UNQ5810 chr16:19315621C>G
	 * </P>
	 */
	@Test
	public void testNcRnaExonicVar495() throws AnnotationException {
		byte chr = 16;
		int pos = 19315621;
		String ref = "C";
		String alt = "G";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.UTR3, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("CLEC19A(uc002dga.5:c.*81C>G)", annot);
	}

	/**
	 * <P>
	 * annovar: ANGPTL3 chr1:63070540TAATGTGGT>-
	 * </P>
	 */
	@Test
	public void testUTR3Var44() throws AnnotationException {
		byte chr = 1;
		int pos = 63070540;
		String ref = "TAATGTGGT";
		String alt = "-";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.UTR3, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("ANGPTL3(uc001das.2:c.*52_*60del)", annot);
	}

	/**
	 * <P>
	 * annovar: GBP7 chr1:89597755T>C minus strand
	 * </P>
	 */
	@Test
	public void testUTR3Var54() throws AnnotationException {
		byte chr = 1;
		int pos = 89597755;
		String ref = "T";
		String alt = "C";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.UTR3, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("GBP7(uc001dna.2:c.*77A>G)", annot);
	}

	/**
	 * <P>
	 * annovar: FCGR2B chr1:161643333A>G
	 * </P>
	 */
	@Test
	public void testUTR3Var95() throws AnnotationException {
		byte chr = 1;
		int pos = 161643333;
		String ref = "A";
		String alt = "G";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.UTR3, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("FCGR2B(uc009wum.2:c.*21A>G)", annot);
	}

	/**
	 * <P>
	 * annovar: LRRC52 chr1:165533075C>T
	 * </P>
	 */
	@Test
	public void testUTR3Var96() throws AnnotationException {
		byte chr = 1;
		int pos = 165533075;
		String ref = "C";
		String alt = "T";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.UTR3, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("LRRC52(uc001gde.2:c.*14C>T)", annot);
	}

	/**
	 * <P>
	 * annovar: XCL1 chr1:168550535A>G
	 * </P>
	 */
	@Test
	public void testUTR3Var99() throws AnnotationException {
		byte chr = 1;
		int pos = 168550535;
		String ref = "A";
		String alt = "G";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.UTR3, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("XCL1(uc001gfo.2:c.*77A>G)", annot);
	}

	/**
	 * <P>
	 * annovar: RGS21 chr1:192335274->CTAA expected:<...S21(uc001gsh.3:c.*20[->]CTAA)> but
	 * was:<...S21(uc001gsh.3:c.*20[_21ins]CTAA)>
	 *
	 * </P>
	 */
	@Test
	public void testUTR3Var112() throws AnnotationException {
		byte chr = 1;
		int pos = 192335274;
		String ref = "-";
		String alt = "CTAA";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.UTR3, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("RGS21(uc001gsh.3:c.*20_*21insCTAA)", annot);
	}

	/**
	 * <P>
	 * annovar: RGS21 chr1:192335275->TAAT
	 * </P>
	 */
	@Test
	public void testUTR3Var116() throws AnnotationException {
		byte chr = 1;
		int pos = 192335275;
		String ref = "-";
		String alt = "TAAT";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.UTR3, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("RGS21(uc001gsh.3:c.*21_*22insTAAT)", annot);
	}

	/**
	 * <P>
	 * annovar: LMOD1 chr1:201865763A>G Minus strand
	 * </P>
	 */
	@Test
	public void testUTR3Var119() throws AnnotationException {
		byte chr = 1;
		int pos = 201865763;
		String ref = "A";
		String alt = "G";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.UTR3, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("LMOD1(uc021phl.1:c.*1643T>C,uc010ppu.2:c.*1643T>C,uc021phm.1:c.*1737T>C)", annot);
	}

	/**
	 * <P>
	 * annovar: KLHL23,PHOSPHO2-KLHL23 chr2:170606300->A Jannovar shows an insertion mutation in one of the two genes
	 * here.
	 * </P>
	 */
	@Test
	public void testUTR3Var182() throws AnnotationException {
		byte chr = 2;
		int pos = 170606300;
		String ref = "-";
		String alt = "A";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.UTR3, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("PHOSPHO2-KLHL23(uc002ufh.2:c.*58_*59insA,uc002ufi.2:c.*58_*59insA)", annot);
	}

	/**
	 * <P>
	 * annovar: GPBAR1 chr2:219128506C>T
	 * </P>
	 */
	@Test
	public void testUTR3Var205() throws AnnotationException {
		byte chr = 2;
		int pos = 219128506;
		String ref = "C";
		String alt = "T";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.UTR3, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("GPBAR1(uc010zjy.1:c.*66C>T,uc010zjw.1:c.*66C>T,uc010zjx.1:c.*66C>T)", annot);
	}

	/**
	 * <P>
	 * annovar: GLT8D1 chr3:52728804C>T
	 * </P>
	 */
	@Test
	public void testUTR3Var248() throws AnnotationException {
		byte chr = 3;
		int pos = 52728804;
		String ref = "C";
		String alt = "T";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.UTR3, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals(
				"GLT8D1(uc003dfn.3:c.*57G>A,uc003dfl.3:c.*57G>A,uc003dfm.3:c.*57G>A,uc003dfi.4:c.*57G>A,uc003dfk.3:c.*57G>A)",
				annot);
	}

	/**
	 * <P>
	 * annovar: TPRG1 chr3:189038648T>C
	 * </P>
	 */
	@Test
	public void testUTR3Var298() throws AnnotationException {
		byte chr = 3;
		int pos = 189038648;
		String ref = "T";
		String alt = "C";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.UTR3, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("TPRG1(uc003frw.2:c.*39T>C,uc003frv.2:c.*39T>C)", annot);
	}

	/**
	 * <P>
	 * annovar: AK308309 chr4:119435320CAAGAA>- expected:<AK308309(uc010[1]my.1:c.*99_104delCAA...> but
	 * was:<AK308309(uc010[i]my.1:c.*99_104delCAA...>
	 *
	 * </P>
	 */
	@Test
	public void testUTR3Var338() throws AnnotationException {
		byte chr = 4;
		int pos = 119435320;
		String ref = "CAAGAA";
		String alt = "-";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.UTR3, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("AK308309(uc010imy.1:c.*99_*104del)", annot);
	}

	/**
	 * <P>
	 * annovar: FRG1 chr4:190884289->GACA
	 * </P>
	 */
	@Test
	public void testUTR3Var352() throws AnnotationException {
		byte chr = 4;
		int pos = 190884289;
		String ref = "-";
		String alt = "GACA";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.UTR3, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("FRG1(uc003izs.3:c.*5_*6insGACA)", annot);
	}

	/**
	 * <P>
	 * annovar: SLC6A3 chr5:1393761->GG Minus strand!
	 * </P>
	 */
	@Test
	public void testUTR3Var360() throws AnnotationException {
		byte chr = 5;
		int pos = 1393761;
		String ref = "-";
		String alt = "GG";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.UTR3, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("SLC6A3(uc003jck.3:c.*1089_*1090insCC)", annot);
	}

	/**
	 * <P>
	 * annovar: ATP10B chr5:160039687T>C
	 * </P>
	 */
	@Test
	public void testUTR3Var390() throws AnnotationException {
		byte chr = 5;
		int pos = 160039687;
		String ref = "T";
		String alt = "C";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.UTR3, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("ATP10B(uc003lyn.3:c.*52A>G)", annot);
	}

	/**
	 * <P>
	 * annovar: MICB chr6:31477771GA>-
	 * </P>
	 */
	@Test
	public void testUTR3Var430() throws AnnotationException {
		byte chr = 6;
		int pos = 31477771;
		String ref = "GA";
		String alt = "-";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.UTR3, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals(
				"MICB(uc031snm.1:c.*85_*86del,uc011dnm.2:c.*85_*86del,uc003nto.4:c.*85_*86del,uc003ntn.4:c.*85_*86del)",
				annot);
	}

	/**
	 * <P>
	 * annovar: PRPH2 chr6:42666020G>A
	 * </P>
	 */
	@Test
	public void testUTR3Var448() throws AnnotationException {
		byte chr = 6;
		int pos = 42666020;
		String ref = "G";
		String alt = "A";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.UTR3, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("PRPH2(uc003osk.3:c.*13C>T)", annot);
	}

	/**
	 * <P>
	 * annovar: ULBP3 chr6:150385730T>G minus strand
	 * </P>
	 */
	@Test
	public void testUTR3Var472() throws AnnotationException {
		byte chr = 6;
		int pos = 150385730;
		String ref = "T";
		String alt = "G";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.UTR3, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("ULBP3(uc011eej.1:c.*13A>C,uc003qns.3:c.*13A>C)", annot);
	}

	/**
	 * <P>
	 * annovar: CDCA7L chr7:21941866TCTT>-
	 * expected:<...74delTCTT,uc003svf.4[c.*71_74delTCTT,uc010kuk.3c.*71_74delTCTT,uc010kul.3]c.*71_74delTCTT)> but was:
	 * <...74delTCTT,uc003svf.4[:c.*71_74delTCTT,uc010kuk.3:c.*71_74delTCTT,uc010kul.3:]c.*71_74delTCTT)>
	 *
	 * </P>
	 */
	@Test
	public void testUTR3Var498() throws AnnotationException {
		byte chr = 7;
		int pos = 21941866;
		String ref = "TCTT";
		String alt = "-";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.UTR3, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals(
				"CDCA7L(uc003svf.4:c.*75_*78del,uc010kul.3:c.*75_*78del,uc010kuk.3:c.*75_*78del,uc003sve.4:c.*75_*78del)",
				annot);
		// Assert.assertEquals("CDCA7L(uc003svf.4:c.*71_*74del,uc010kul.3:c.*71_*74del,uc010kuk.3:c.*71_*74del,uc003sve.4:c.*71_*74del)",
		// annot);
	}

	/**
	 * <P>
	 * annovar: WBSCR22 chr7:73118196T>C
	 * </P>
	 */
	@Test
	public void testUTR3Var511() throws AnnotationException {
		byte chr = 7;
		int pos = 73118196;
		String ref = "T";
		String alt = "C";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.UTR3, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("WBSCR22(uc003tyw.1:c.*337T>C)", annot);
	}

	/**
	 * <P>
	 * annovar: HGF chr7:81372156A>G minus strand
	 * </P>
	 */
	@Test
	public void testUTR3Var513() throws AnnotationException {
		byte chr = 7;
		int pos = 81372156;
		String ref = "A";
		String alt = "G";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.UTR3, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("HGF(uc003uhn.1:c.*102T>C,uc003uho.1:c.*102T>C)", annot);
	}

	/**
	 * <P>
	 * annovar: FAM75D3 chr9:84563495->CTAC
	 * </P>
	 */
	@Test
	public void testUTR3Var608() throws AnnotationException {
		byte chr = 9;
		int pos = 84563495;
		String ref = "-";
		String alt = "CTAC";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.UTR3, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("SPATA31D3(uc010mpt.2:c.*573_*574insCTAC)", annot);
	}

	/**
	 * <P>
	 * annovar: ROR2 chr9:94456643G>C minus strand
	 * </P>
	 */
	@Test
	public void testUTR3Var611() throws AnnotationException {
		byte chr = 9;
		int pos = 94456643;
		String ref = "G";
		String alt = "C";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.UTR3, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("ROR2(uc004ari.1:c.*1C>G)", annot);
	}

	/**
	 * <P>
	 * annovar: DDX31 chr9:135470176C>G minus strand
	 * </P>
	 */
	@Test
	public void testUTR3Var643() throws AnnotationException {
		byte chr = 9;
		int pos = 135470176;
		String ref = "C";
		String alt = "G";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.UTR3, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("DDX31(uc010mzu.1:c.*77G>C,uc004cbq.1:c.*77G>C)", annot);
	}

	/**
	 * <P>
	 * annovar: WDFY4 chr10:50190799C>T
	 * </P>
	 */
	@Test
	public void testUTR3Var681() throws AnnotationException {
		byte chr = 10;
		int pos = 50190799;
		String ref = "C";
		String alt = "T";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.UTR3, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("WDFY4(uc001jha.4:c.*179C>T)", annot);
	}

	/**
	 * <P>
	 * annovar: AGAP11 chr10:88769678->TGC
	 * </P>
	 */
	@Test
	public void testUTR3Var697() throws AnnotationException {
		byte chr = 10;
		int pos = 88769678;
		String ref = "-";
		String alt = "TGC";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.UTR3, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("AGAP11(uc001kee.2:c.*16_*17insTGC,uc031pwm.1:c.*16_*17insTGC)", annot);
	}

	/**
	 * <P>
	 * annovar: LOC440040 chr11:49831603G>-
	 * </P>
	 */
	@Test
	public void testUTR3Var755() throws AnnotationException {
		byte chr = 11;
		int pos = 49831603;
		String ref = "G";
		String alt = "-";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.UTR3, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("LOC440040(uc009ymb.3:c.*317del,uc010rhy.2:c.*317del)", annot);
	}

	/**
	 * <P>
	 * annovar: SLC22A20 chr11:64993197->TAAG
	 * </P>
	 */
	@Test
	public void testUTR3Var766() throws AnnotationException {
		byte chr = 11;
		int pos = 64993197;
		String ref = "-";
		String alt = "TAAG";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.UTR3, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("SLC22A20(uc021qlg.1:c.*30_*31insTAAG)", annot);
	}

	/**
	 * <P>
	 * annovar: SLC22A20 chr11:64993200->GCAA
	 * </P>
	 */
	@Test
	public void testUTR3Var767() throws AnnotationException {
		byte chr = 11;
		int pos = 64993200;
		String ref = "-";
		String alt = "GCAA";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.UTR3, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("SLC22A20(uc021qlg.1:c.*33_*34insGCAA)", annot);
	}

	/**
	 * <P>
	 * annovar: KDELC2 chr11:108345515->A minus strand
	 * </P>
	 */
	@Test
	public void testUTR3Var787() throws AnnotationException {
		byte chr = 11;
		int pos = 108345515;
		String ref = "-";
		String alt = "A";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.UTR3, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("KDELC2(uc001pki.2:c.*39_*40insT,uc001pkj.2:c.*39_*40insT)", annot);
	}

	/**
	 * <P>
	 * annovar: DPPA3 chr12:7869698->CCCG
	 * </P>
	 */
	@Test
	public void testUTR3Var815() throws AnnotationException {
		byte chr = 12;
		int pos = 7869698;
		String ref = "-";
		String alt = "CCCG";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.UTR3, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("DPPA3(uc001qtf.3:c.*25_*26insCCCG)", annot);
	}

	/**
	 * <P>
	 * annovar: STRAP chr12:16055927->T :<...nsT,uc001rdc.4:c.*15[->]T,uc010shw.2:c.*15_1...> but was:
	 * <...nsT,uc001rdc.4:c.*15[_16ins]T,uc010shw.2:c.*15_1...>
	 *
	 * </P>
	 */
	@Test
	public void testUTR3Var824() throws AnnotationException {
		byte chr = 12;
		int pos = 16055927;
		String ref = "-";
		String alt = "T";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.UTR3, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("STRAP(uc001rdd.4:c.*15_*16insT,uc001rdc.4:c.*15_*16insT,uc010shw.2:c.*15_*16insT)", annot);
	}

	/**
	 * <P>
	 * annovar: WIBG chr12:56295548TAAG>- <...1sif.1:c.*105_108del[CTTA,uc001sie.1:c.*105_108delCTTA])> but was:
	 * <...1sif.1:c.*105_108del[TAAG,uc001sie.1:c.*105_108delTAAG])>
	 *
	 *
	 * </P>
	 */
	@Test
	public void testUTR3Var843() throws AnnotationException {
		byte chr = 12;
		int pos = 56295548;
		String ref = "TAAG";
		String alt = "-";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.UTR3, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("WIBG(uc001sie.1:c.*105_*108del,uc001sif.1:c.*108_*111del)", annot);
	}

	/**
	 * <P>
	 * annovar: AHNAK2 chr14:105404384T>C minus strand
	 * </P>
	 * <AHNAK2(uc0[21seo.1:c.*16A>G,uc021sen.1:c.*16A>G,uc001ypx.2:c.*16A>G,uc010axc].1:c.*16A>G)> but was:
	 * <AHNAK2(uc0[10axc.1:c.*16A>G,uc001ypx.2:c.*16A>G,uc021seo.1:c.*16A>G,uc021sen].1:c.*16A>G)>
	 */
	@Test
	public void testUTR3Var950() throws AnnotationException {
		byte chr = 14;
		int pos = 105404384;
		String ref = "T";
		String alt = "C";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.UTR3, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("AHNAK2(uc021seo.1:c.*16A>G,uc001ypx.2:c.*16A>G,uc010axc.1:c.*16A>G,uc021sen.1:c.*16A>G)",
				annot);
	}

	/**
	 * <P>
	 * annovar: SNX11 chr17:46198876G>C
	 * </P>
	 */
	@Test
	public void testUTR3Var1099() throws AnnotationException {
		byte chr = 17;
		int pos = 46198876;
		String ref = "G";
		String alt = "C";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.UTR3, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals(
				"SNX11(uc010wlg.1:c.*6G>C,uc010wlj.1:c.*6G>C,uc010wlh.1:c.*6G>C,uc002ing.1:c.*6G>C,uc010wli.1:c.*6G>C,uc002inf.1:c.*6G>C,uc002inh.1:c.*6G>C)",
				annot);
	}

	/**
	 * <P>
	 * annovar: ATP5G1 chr17:46973146G>-
	 * </P>
	 */
	@Test
	public void testUTR3Var1100() throws AnnotationException {
		byte chr = 17;
		int pos = 46973146;
		String ref = "G";
		String alt = "-";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.UTR3, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("ATP5G1(uc002iog.3:c.*15del,uc002ioh.3:c.*15del)", annot);
	}

	/**
	 * <P>
	 * annovar: EXOC7 chr17:74077497G>C minus strand
	 * </P>
	 */
	@Test
	public void testUTR3Var1118() throws AnnotationException {
		byte chr = 17;
		int pos = 74077497;
		String ref = "G";
		String alt = "C";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.UTR3, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals(
				"EXOC7(uc002jqq.3:c.*2232C>G,uc010wsw.2:c.*2232C>G,uc002jqr.3:c.*2232C>G,uc010wsx.2:c.*2232C>G,uc002jqs.3:c.*2232C>G,uc010wsv.2:c.*2232C>G,uc010dgv.2:c.*2232C>G,uc002jqp.2:c.*2261C>G)",
				annot);
	}

	/**
	 * <P>
	 * annovar: SLC14A2 chr18:43262532C>A
	 * </P>
	 */
	@Test
	public void testUTR3Var1131() throws AnnotationException {
		byte chr = 18;
		int pos = 43262532;
		String ref = "C";
		String alt = "A";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.UTR3, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("SLC14A2(uc002lbe.3:c.*48C>A,uc010dnj.3:c.*48C>A)", annot);
	}

	/**
	 * <P>
	 * annovar: SKA1 chr18:47918639A>C
	 * </P>
	 */
	@Test
	public void testUTR3Var1133() throws AnnotationException {
		byte chr = 18;
		int pos = 47918639;
		String ref = "A";
		String alt = "C";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.UTR3, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("SKA1(uc002let.3:c.*22A>C,uc002leu.3:c.*22A>C,uc010xdl.2:c.*22A>C)", annot);
	}

	/**
	 * <P>
	 * annovar: FZR1 chr19:3534842G>A
	 * </P>
	 */
	@Test
	public void testUTR3Var1146() throws AnnotationException {
		byte chr = 19;
		int pos = 3534842;
		String ref = "G";
		String alt = "A";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.UTR3, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("FZR1(uc010dtk.2:c.*8G>A,uc002lxv.2:c.*8G>A,uc002lxt.2:c.*8G>A)", annot);
	}

	/**
	 * <P>
	 * annovar: C19orf40 chr19:33467620T>C
	 * </P>
	 */
	@Test
	public void testUTR3Var1174() throws AnnotationException {
		byte chr = 19;
		int pos = 33467620;
		String ref = "T";
		String alt = "C";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.UTR3, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("C19orf40(uc002nud.4:c.*32T>C)", annot);
	}

	/**
	 * <P>
	 * annovar: LGI4 chr19:35616086C>T minus strand!
	 * </P>
	 */
	@Test
	public void testUTR3Var1178() throws AnnotationException {
		byte chr = 19;
		int pos = 35616086;
		String ref = "C";
		String alt = "T";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.UTR3, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("LGI4(uc002nxx.2:c.*11G>A,uc002nxy.1:c.*11G>A,uc002nxz.1:c.*986G>A)", annot);
	}

	/**
	 * <P>
	 * annovar: C19orf55 chr19:36259494C>A
	 * </P>
	 */
	@Test
	public void testUTR3Var1183() throws AnnotationException {
		byte chr = 19;
		int pos = 36259494;
		String ref = "C";
		String alt = "A";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.UTR3, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("C19orf55(uc021usz.1:c.*47C>A)", annot);
	}

	/**
	 * <P>
	 * annovar: MIA chr19:41283365C>G
	 * </P>
	 */
	@Test
	public void testUTR3Var1192() throws AnnotationException {
		byte chr = 19;
		int pos = 41283365;
		String ref = "C";
		String alt = "G";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.UTR3, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("MIA(uc021uuu.1:c.*40C>G,uc002opb.4:c.*40C>G)", annot);
	}

	/**
	 * <P>
	 * annovar: PLA2G4C chr19:48551546A>T minus strand
	 * </P>
	 */
	@Test
	public void testUTR3Var1199() throws AnnotationException {
		byte chr = 19;
		int pos = 48551546;
		String ref = "A";
		String alt = "T";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.UTR3, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals(
				"PLA2G4C(uc010xzd.2:c.*54T>A,uc002phx.3:c.*54T>A,uc010elr.3:c.*139T>A,uc002phw.3:c.*54T>A)", annot);
	}

	/**
	 * <P>
	 * annovar: ZNF160 chr19:53576589C>G minus strand
	 * </P>
	 */
	@Test
	public void testUTR3Var1209() throws AnnotationException {
		byte chr = 19;
		int pos = 53576589;
		String ref = "C";
		String alt = "G";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.UTR3, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("ZNF160(uc002qas.4:c.*1G>C)", annot);
	}

	/**
	 * <P>
	 * annovar: TRAPPC10 chr21:45523416C>T
	 * </P>
	 */
	@Test
	public void testUTR3Var1269() throws AnnotationException {
		byte chr = 21;
		int pos = 45523416;
		String ref = "C";
		String alt = "T";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.UTR3, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("TRAPPC10(uc011afa.2:c.*4C>T,uc010gpo.3:c.*4C>T,uc002zea.3:c.*4C>T)", annot);
	}

	/**
	 * <P>
	 * annovar: C21orf33 chr21:45565473C>T
	 * </P>
	 */
	@Test
	public void testUTR3Var1270() throws AnnotationException {
		byte chr = 21;
		int pos = 45565473;
		String ref = "C";
		String alt = "T";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.UTR3, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("C21orf33(uc002zec.4:c.*642C>T,uc002zed.4:c.*642C>T)", annot);
	}

	/**
	 * <P>
	 * annovar: LSS chr21:47608580G>A minus strand
	 * </P>
	 */
	@Test
	public void testUTR3Var1276() throws AnnotationException {
		byte chr = 21;
		int pos = 47608580;
		String ref = "G";
		String alt = "A";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.UTR3, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("LSS(uc002zij.3:c.*194C>T)", annot);
	}

	/**
	 * <P>
	 * annovar: C22orf13 chr22:24936970A>G minus strand
	 * </P>
	 */
	@Test
	public void testUTR3Var1284() throws AnnotationException {
		byte chr = 22;
		int pos = 24936970;
		String ref = "A";
		String alt = "G";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.UTR3, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("GUCD1(uc003aah.2:c.*2004T>C,uc003aal.2:c.*2004T>C)", annot);
	}

	/**
	 * <P>
	 * annovar: PVALB chr22:37196871G>A minus strand
	 * </P>
	 */
	@Test
	public void testUTR3Var1290() throws AnnotationException {
		byte chr = 22;
		int pos = 37196871;
		String ref = "G";
		String alt = "A";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.UTR3, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("PVALB(uc010gwz.3:c.*63C>T,uc003apx.3:c.*63C>T)", annot);
	}

	/**
	 * <P>
	 * annovar: NCAPH2 chr22:50961854T>C
	 * </P>
	 */
	@Test
	public void testUTR3Var1312() throws AnnotationException {
		byte chr = 22;
		int pos = 50961854;
		String ref = "T";
		String alt = "C";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.UTR3, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("NCAPH2(uc003blx.4:c.*50T>C,uc003blr.4:c.*50T>C,uc003blv.3:c.*166T>C)", annot);
	}

	/**
	 * <P>
	 * annovar: FAM47B chrX_CHROMOSOME:34962909A>C
	 * </P>
	 */
	@Test
	public void testUTR3Var1317() throws AnnotationException {
		byte chr = X_CHROMOSOME;
		int pos = 34962909;
		String ref = "A";
		String alt = "C";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.UTR3, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("FAM47B(uc004ddi.2:c.*23A>C)", annot);
	}

	/**
	 * <P>
	 * annovar: SAGE1 chrX_CHROMOSOME:134994622T>G
	 * </P>
	 */
	@Test
	public void testUTR3Var1332() throws AnnotationException {
		byte chr = X_CHROMOSOME;
		int pos = 134994622;
		String ref = "T";
		String alt = "G";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.UTR3, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("SAGE1(uc010nry.1:c.*481T>G)", annot);
	}

	/**
	 * <P>
	 * annovar: SAGE1 chrX_CHROMOSOME:134994633G>C
	 * </P>
	 */
	@Test
	public void testUTR3Var1337() throws AnnotationException {
		byte chr = X_CHROMOSOME;
		int pos = 134994633;
		String ref = "G";
		String alt = "C";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.UTR3, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("SAGE1(uc010nry.1:c.*492G>C)", annot);
	}

	/**
	 * <P>
	 * annovar: CD24 chrY_CHROMOSOME:21154323G>A minus strand
	 * </P>
	 */
	@Test
	public void testUTR3Var1346() throws AnnotationException {
		byte chr = Y_CHROMOSOME;
		int pos = 21154323;
		String ref = "G";
		String alt = "A";

		AnnotationList ann = annotator.getAnnotationList(chr, pos, ref, alt);
		VariantType varType = ann.getVariantType();
		Assert.assertEquals(VariantType.UTR3, varType);
		String annot = ann.getVariantAnnotation();
		Assert.assertEquals("CD24(uc004ftz.1:c.*30C>T)", annot);
	}

}