package de.charite.compbio.jannovar.htsjdk;

import de.charite.compbio.jannovar.data.JannovarData;
import de.charite.compbio.jannovar.data.JannovarDataSerializer;
import de.charite.compbio.jannovar.data.SerializationException;
import de.charite.compbio.jannovar.reference.GenomeInterval;
import de.charite.compbio.jannovar.reference.GenomePosition;
import de.charite.compbio.jannovar.reference.Strand;
import de.charite.compbio.jannovar.utils.ResourceUtils;
import htsjdk.samtools.reference.IndexedFastaSequenceFile;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;


import java.io.File;
import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GenomeRegionSequenceExtractorTest {

	/**
	 * path to FASTA file with sequence
	 */
	static String fastaPath;
	/**
	 * path to Jannovar database file
	 */
	static String dbPath;
	/**
	 * indexed FASTA file reader
	 */
	IndexedFastaSequenceFile indexedFile;
	/**
	 * Jannovar database
	 */
	JannovarData jannovarData;

	@TempDir
	static File tmpDir;

	@BeforeAll
	public static void setUpClass() {
		// copy out files to temporary directory
		fastaPath = tmpDir + "/ref.fa";
		ResourceUtils.copyResourceToFile("/ex_fbn1/ref.fa", new File(fastaPath));
		String faiPath = tmpDir + "/ref.fa.fai";
		ResourceUtils.copyResourceToFile("/ex_fbn1/ref.fa.fai", new File(faiPath));
		String dictPath = tmpDir + "/ref.dict";
		ResourceUtils.copyResourceToFile("/ex_fbn1/ref.dict", new File(dictPath));
		dbPath = tmpDir + "/mini_fbn1.ser";
		ResourceUtils.copyResourceToFile("/ex_fbn1/mini_fbn1.ser", new File(dbPath));
	}

	@BeforeEach
	public void setUp() throws FileNotFoundException, SerializationException {
		this.indexedFile = new IndexedFastaSequenceFile(new File(fastaPath));
		this.jannovarData = new JannovarDataSerializer(dbPath).load();
	}

	@Test
	public void testLoadGenomeInterval() {
		GenomeRegionSequenceExtractor extractor = new GenomeRegionSequenceExtractor(jannovarData, this.indexedFile);
		GenomeInterval region = new GenomeInterval(new GenomePosition(jannovarData.getRefDict(), Strand.FWD, 1, 99), 51);
		String seq = extractor.load(region);
		assertEquals("CTTTAGGCCTGGGAATCAGGAGTGCTATGACAATTTCCTCCAAAGTGGAGA", seq);
	}

}
