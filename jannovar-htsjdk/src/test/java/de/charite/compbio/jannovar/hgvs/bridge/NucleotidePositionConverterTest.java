package de.charite.compbio.jannovar.hgvs.bridge;

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
import de.charite.compbio.jannovar.hgvs.SequenceType;
import de.charite.compbio.jannovar.hgvs.nts.NucleotidePointLocation;
import de.charite.compbio.jannovar.hgvs.nts.NucleotideRange;
import de.charite.compbio.jannovar.reference.GenomeInterval;
import de.charite.compbio.jannovar.reference.GenomePosition;
import de.charite.compbio.jannovar.reference.TranscriptModel;
import de.charite.compbio.jannovar.utils.ResourceUtils;

public class NucleotidePositionConverterTest {

	/** path to Jannovar database file */
	static String dbPath;
	/** Jannovar database */
	JannovarData jannovarData;
	/** TranscriptModel */
	TranscriptModel tm;
	/** Position conversion */
	NucleotideLocationConverter converter;

	@BeforeClass
	public static void setUpClass() throws Exception {
		// copy out files to temporary directory
		File tmpDir = Files.createTempDir();
		dbPath = tmpDir + "/mini_ctns.ser";
		ResourceUtils.copyResourceToFile("/ex_ctns/mini_ctns.ser", new File(dbPath));
	}

	@Before
	public void setUp() throws FileNotFoundException, SerializationException {
		jannovarData = new JannovarDataSerializer(dbPath).load();
		tm = (TranscriptModel) jannovarData.getTmByGeneSymbol().get("CTNS").toArray()[0];

		converter = new NucleotideLocationConverter();
	}

	@Test
	public void testConvertCDSPosition() throws CannotTranslateHGVSVariant {
		NucleotidePointLocation pos = NucleotidePointLocation.build(10);
		GenomePosition gPos = converter.translateNucleotidePointLocation(tm, pos, SequenceType.CODING_DNA);
		Assert.assertEquals("ref:g.43511", gPos.toString());
	}

	@Test
	public void testConvertUTR5Position() throws CannotTranslateHGVSVariant {
		NucleotidePointLocation pos = NucleotidePointLocation.build(-10);
		GenomePosition gPos = converter.translateNucleotidePointLocation(tm, pos, SequenceType.CODING_DNA);
		Assert.assertEquals("ref:g.43491", gPos.toString());
	}

	@Test
	public void testConvertUTR3Position() throws CannotTranslateHGVSVariant {
		NucleotidePointLocation pos = NucleotidePointLocation.buildDownstreamOfCDS(10);
		GenomePosition gPos = converter.translateNucleotidePointLocation(tm, pos, SequenceType.CODING_DNA);
		Assert.assertEquals("ref:g.63671", gPos.toString());
	}

	@Test
	public void testConvertCDSInterval() throws CannotTranslateHGVSVariant {
		NucleotideRange range = new NucleotideRange(NucleotidePointLocation.build(10),
				NucleotidePointLocation.build(20));
		GenomeInterval gItv = converter.translateNucleotideRange(tm, range, SequenceType.CODING_DNA);
		Assert.assertEquals("ref:g.43511_43521", gItv.toString());
	}

	@Test
	public void testConvertUTR5Interval() throws CannotTranslateHGVSVariant {
		NucleotideRange range = new NucleotideRange(NucleotidePointLocation.build(-20),
				NucleotidePointLocation.build(-10));
		GenomeInterval gItv = converter.translateNucleotideRange(tm, range, SequenceType.CODING_DNA);
		Assert.assertEquals("ref:g.40610_43491", gItv.toString());
	}

	@Test
	public void testConvertUTR3Interval() throws CannotTranslateHGVSVariant {
		NucleotideRange range = new NucleotideRange(NucleotidePointLocation.buildDownstreamOfCDS(10),
				NucleotidePointLocation.buildDownstreamOfCDS(20));
		GenomeInterval gItv = converter.translateNucleotideRange(tm, range, SequenceType.CODING_DNA);
		Assert.assertEquals("ref:g.63671_63681", gItv.toString());
	}

}
