package de.charite.compbio.jannovar.hgvs.bridge;

import htsjdk.samtools.reference.IndexedFastaSequenceFile;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.io.Files;

import de.charite.compbio.jannovar.data.JannovarData;
import de.charite.compbio.jannovar.data.JannovarDataSerializer;
import de.charite.compbio.jannovar.data.SerializationException;
import de.charite.compbio.jannovar.hgvs.HGVSVariant;
import de.charite.compbio.jannovar.hgvs.nts.variant.SingleAlleleNucleotideVariant;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParser;
import de.charite.compbio.jannovar.reference.GenomeVariant;
import de.charite.compbio.jannovar.utils.ResourceUtils;

/**
 * Tests using FBN1 transcript that is on the reverse strand.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class NucleotideSubstitutionToGenomeVariantForwardStrandTest {

	/** path to Jannovar database file */
	static String dbPath;
	/** path to Jannovar database file */
	static String fastaPath;
	/** Jannovar database */
	JannovarData jannovarData;
	/** Translation of NucleotideChange to GenomeVariant */
	NucleotideChangeToGenomeVariantTranslator translator;

	@BeforeClass
	public static void setUpClass() throws Exception {
		// copy out files to temporary directory
		File tmpDir = Files.createTempDir();
		dbPath = tmpDir + "/mini_ctns.ser";
		ResourceUtils.copyResourceToFile("/ex_ctns/mini_ctns.ser", new File(dbPath));
		fastaPath = tmpDir + "/ref.fa";
		ResourceUtils.copyResourceToFile("/ex_ctns/ref.fa", new File(fastaPath));
		String faiPath = tmpDir + "/ref.fa.fai";
		ResourceUtils.copyResourceToFile("/ex_ctns/ref.fa.fai", new File(faiPath));
	}

	@Before
	public void setUp() throws FileNotFoundException, SerializationException {
		jannovarData = new JannovarDataSerializer(dbPath).load();

		IndexedFastaSequenceFile fastaFile = new IndexedFastaSequenceFile(new File(fastaPath));
		translator = new NucleotideChangeToGenomeVariantTranslator(jannovarData, fastaFile);
	}

	@Test
	public void testPositionInCDS() throws CannotTranslateHGVSVariant {
		String hgvsStr = "NM_004937.2(CTNS):c.400A>T";
		HGVSVariant hgvsVar = new HGVSParser().parseHGVSString(hgvsStr);
		Assert.assertEquals(hgvsStr, hgvsVar.toHGVSString());

		SingleAlleleNucleotideVariant saVar = (SingleAlleleNucleotideVariant) hgvsVar;
		GenomeVariant gVar = translator.translateNucleotideVariantToGenomeVariant(saVar);
		Assert.assertEquals("ref:g.58585A>T", gVar.toString());
	}

	@Test
	public void testPositionWithPositiveOffsetInCDS() throws CannotTranslateHGVSVariant {
		String hgvsStr = "NM_004937.2(CTNS):c.400+1T>A";
		HGVSVariant hgvsVar = new HGVSParser().parseHGVSString(hgvsStr);
		Assert.assertEquals(hgvsStr, hgvsVar.toHGVSString());

		SingleAlleleNucleotideVariant saVar = (SingleAlleleNucleotideVariant) hgvsVar;
		GenomeVariant gVar = translator.translateNucleotideVariantToGenomeVariant(saVar);
		Assert.assertEquals("ref:g.58586T>A", gVar.toString());
	}

	@Test
	public void testPositionWithNegativeOffsetInCDS() throws CannotTranslateHGVSVariant {
		String hgvsStr = "NM_004937.2(CTNS):c.400-1C>A";
		HGVSVariant hgvsVar = new HGVSParser().parseHGVSString(hgvsStr);
		Assert.assertEquals(hgvsStr, hgvsVar.toHGVSString());

		SingleAlleleNucleotideVariant saVar = (SingleAlleleNucleotideVariant) hgvsVar;
		GenomeVariant gVar = translator.translateNucleotideVariantToGenomeVariant(saVar);
		Assert.assertEquals("ref:g.58584C>A", gVar.toString());
	}

	@Test
	public void testPositionInUTR5() throws CannotTranslateHGVSVariant {
		String hgvsStr = "NM_004937.2(CTNS):c.-1T>A";
		HGVSVariant hgvsVar = new HGVSParser().parseHGVSString(hgvsStr);
		Assert.assertEquals(hgvsStr, hgvsVar.toHGVSString());

		SingleAlleleNucleotideVariant saVar = (SingleAlleleNucleotideVariant) hgvsVar;
		GenomeVariant gVar = translator.translateNucleotideVariantToGenomeVariant(saVar);
		Assert.assertEquals("ref:g.43500T>A", gVar.toString());
	}

	@Test
	public void testPositionWithPositiveOffsetInUTR5() throws CannotTranslateHGVSVariant {
		String hgvsStr = "NM_004937.2(CTNS):c.-1+1A>C";
		HGVSVariant hgvsVar = new HGVSParser().parseHGVSString(hgvsStr);
		Assert.assertEquals(hgvsStr, hgvsVar.toHGVSString());

		SingleAlleleNucleotideVariant saVar = (SingleAlleleNucleotideVariant) hgvsVar;
		GenomeVariant gVar = translator.translateNucleotideVariantToGenomeVariant(saVar);
		Assert.assertEquals("ref:g.43501A>C", gVar.toString());
	}

	@Test
	public void testPositionWithNegativeOffsetInUTR5() throws CannotTranslateHGVSVariant {
		String hgvsStr = "NM_004937.2(CTNS):c.-1-1C>A";
		HGVSVariant hgvsVar = new HGVSParser().parseHGVSString(hgvsStr);
		Assert.assertEquals(hgvsStr, hgvsVar.toHGVSString());

		SingleAlleleNucleotideVariant saVar = (SingleAlleleNucleotideVariant) hgvsVar;
		GenomeVariant gVar = translator.translateNucleotideVariantToGenomeVariant(saVar);
		Assert.assertEquals("ref:g.43499C>A", gVar.toString());
	}

	@Test
	public void testPositionInUTR3() throws CannotTranslateHGVSVariant {
		String hgvsStr = "NM_004937.2(CTNS):c.*1A>C";
		HGVSVariant hgvsVar = new HGVSParser().parseHGVSString(hgvsStr);
		Assert.assertEquals(hgvsStr, hgvsVar.toHGVSString());

		SingleAlleleNucleotideVariant saVar = (SingleAlleleNucleotideVariant) hgvsVar;
		GenomeVariant gVar = translator.translateNucleotideVariantToGenomeVariant(saVar);
		Assert.assertEquals("ref:g.63664A>C", gVar.toString());
	}

	@Test
	public void testPositionWithPositiveOffsetInUTR3() throws CannotTranslateHGVSVariant {
		String hgvsStr = "NM_004937.2(CTNS):c.*1+1C>A";
		HGVSVariant hgvsVar = new HGVSParser().parseHGVSString(hgvsStr);
		Assert.assertEquals(hgvsStr, hgvsVar.toHGVSString());

		SingleAlleleNucleotideVariant saVar = (SingleAlleleNucleotideVariant) hgvsVar;
		GenomeVariant gVar = translator.translateNucleotideVariantToGenomeVariant(saVar);
		Assert.assertEquals("ref:g.63665C>A", gVar.toString());
	}

	@Test
	public void testPositionWithNegativeOffsetInUTR3() throws CannotTranslateHGVSVariant {
		String hgvsStr = "NM_004937.2(CTNS):c.*1-1T>A";
		HGVSVariant hgvsVar = new HGVSParser().parseHGVSString(hgvsStr);
		Assert.assertEquals(hgvsStr, hgvsVar.toHGVSString());

		SingleAlleleNucleotideVariant saVar = (SingleAlleleNucleotideVariant) hgvsVar;
		GenomeVariant gVar = translator.translateNucleotideVariantToGenomeVariant(saVar);
		Assert.assertEquals("ref:g.63663T>A", gVar.toString());
	}

}
