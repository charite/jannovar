package de.charite.compbio.jannovar.hgvs.bridge;

import com.google.common.io.Files;
import de.charite.compbio.jannovar.annotation.InvalidGenomeVariant;
import de.charite.compbio.jannovar.data.JannovarData;
import de.charite.compbio.jannovar.data.JannovarDataSerializer;
import de.charite.compbio.jannovar.data.SerializationException;
import de.charite.compbio.jannovar.hgvs.HGVSVariant;
import de.charite.compbio.jannovar.hgvs.nts.variant.SingleAlleleNucleotideVariant;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParser;
import de.charite.compbio.jannovar.reference.GenomeVariant;
import de.charite.compbio.jannovar.utils.ResourceUtils;
import htsjdk.samtools.reference.IndexedFastaSequenceFile;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Tests using FBN1 transcript that is on the reverse strand.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class NucleotideInversionToGenomeVariantReverseStrandTest {

	/**
	 * path to Jannovar database file
	 */
	static String dbPath;
	/**
	 * path to Jannovar database file
	 */
	static String fastaPath;
	/**
	 * Jannovar database
	 */
	JannovarData jannovarData;
	/**
	 * Translation of NucleotideChange to GenomeVariant
	 */
	NucleotideChangeToGenomeVariantTranslator translator;

	@BeforeAll
	public static void setUpClass() throws Exception {
		// copy out files to temporary directory
		File tmpDir = Files.createTempDir();
		dbPath = tmpDir + "/mini_fbn1.ser";
		ResourceUtils.copyResourceToFile("/ex_fbn1/mini_fbn1.ser", new File(dbPath));
		fastaPath = tmpDir + "/ref.fa";
		ResourceUtils.copyResourceToFile("/ex_fbn1/ref.fa", new File(fastaPath));
		String faiPath = tmpDir + "/ref.fa.fai";
		ResourceUtils.copyResourceToFile("/ex_fbn1/ref.fa.fai", new File(faiPath));
		String dictPath = tmpDir + "/ref.dict";
		ResourceUtils.copyResourceToFile("/ex_fbn1/ref.dict", new File(dictPath));
	}

	@BeforeEach
	public void setUp() throws FileNotFoundException, SerializationException {
		jannovarData = new JannovarDataSerializer(dbPath).load();

		IndexedFastaSequenceFile fastaFile = new IndexedFastaSequenceFile(new File(fastaPath));
		translator = new NucleotideChangeToGenomeVariantTranslator(jannovarData, fastaFile);
	}

	@Test
	public void testRangeInCDS() throws CannotTranslateHGVSVariant, InvalidGenomeVariant {
		String hgvsStr = "NM_000138.4(FBN1):c.7338_7341invGAGT";
		HGVSVariant hgvsVar = new HGVSParser().parseHGVSString(hgvsStr);
		Assertions.assertEquals(hgvsStr, hgvsVar.toHGVSString());

		SingleAlleleNucleotideVariant saVar = (SingleAlleleNucleotideVariant) hgvsVar;
		GenomeVariant gVar = translator.translateNucleotideVariantToGenomeVariant(saVar);
		Assertions.assertEquals("ref:g.217678_217681delACTCinsGAGT", gVar.toString());
	}

}
