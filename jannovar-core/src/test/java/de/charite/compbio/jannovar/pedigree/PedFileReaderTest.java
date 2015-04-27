package de.charite.compbio.jannovar.pedigree;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.pedigree.Disease;
import de.charite.compbio.jannovar.pedigree.PedFileContents;
import de.charite.compbio.jannovar.pedigree.PedFileReader;
import de.charite.compbio.jannovar.pedigree.PedParseException;
import de.charite.compbio.jannovar.pedigree.PedPerson;
import de.charite.compbio.jannovar.pedigree.Sex;

public class PedFileReaderTest {

	File tmpFileWithHeader;
	File tmpFileWithoutHeader;
	PedFileReader reader;

	@Before
	public void setUp() throws IOException {
		this.tmpFileWithHeader = File.createTempFile("with_header", "ped");
		writePedFileWithHeader(tmpFileWithHeader);
		this.tmpFileWithoutHeader = File.createTempFile("without_header", "ped");
		writePedFileWithoutHeader(tmpFileWithoutHeader);
	}

	private void writePedFileWithHeader(File file) {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(file));
			bw.write("#FAM\tNAME\tFATHER\tMOTHER\tSEX\tDISEASE\n");
			bw.write("fam\tfather\t0\t0\t1\t0\n");
			bw.write("fam\tmother\t0\t0\t2\t0\n");
			bw.write("fam\tson\tfather\tmother\t1\t0\n");
			bw.write("fam\tdaughter\tfather\tmother\t2\t0\n");
			bw.close();
		} catch (IOException e) {
			if (bw != null)
				try {
					bw.close();
				} catch (IOException e1) {
					// swallow, nothing we can do about it
				}
		}
	}

	private void writePedFileWithoutHeader(File file) {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(file));
			bw.write("fam\tfather\t0\t0\t1\t0\n");
			bw.write("fam\tmother\t0\t0\t2\t0\n");
			bw.write("fam\tson\tfather\tmother\t1\t0\n");
			bw.write("fam\tdaughter\tfather\tmother\t2\t0\n");
			bw.close();
		} catch (IOException e) {
			if (bw != null)
				try {
					bw.close();
				} catch (IOException e1) {
					// swallow, nothing we can do about it
				}
		}
	}

	@Test
	public void testParseWithHeader() throws PedParseException, IOException {
		PedFileReader reader = new PedFileReader(this.tmpFileWithHeader);
		PedFileContents pedFileContents = reader.read();

		Assert.assertEquals(pedFileContents.getExtraColumnHeaders().size(), 0);

		ImmutableList.Builder<PedPerson> individuals = new ImmutableList.Builder<PedPerson>();
		individuals.add(new PedPerson("fam", "father", "0", "0", Sex.MALE, Disease.UNKNOWN));
		individuals.add(new PedPerson("fam", "mother", "0", "0", Sex.FEMALE, Disease.UNKNOWN));
		individuals.add(new PedPerson("fam", "son", "father", "mother", Sex.MALE, Disease.UNKNOWN));
		individuals.add(new PedPerson("fam", "daughter", "father", "mother", Sex.FEMALE, Disease.UNKNOWN));
		Assert.assertEquals(pedFileContents.getIndividuals(), individuals.build());
	}

	@Test
	public void testParseWithoutHeader() throws PedParseException, IOException {
		PedFileReader reader = new PedFileReader(this.tmpFileWithoutHeader);
		PedFileContents pedFileContents = reader.read();

		Assert.assertEquals(pedFileContents.getExtraColumnHeaders().size(), 0);

		ImmutableList.Builder<PedPerson> individuals = new ImmutableList.Builder<PedPerson>();
		individuals.add(new PedPerson("fam", "father", "0", "0", Sex.MALE, Disease.UNKNOWN));
		individuals.add(new PedPerson("fam", "mother", "0", "0", Sex.FEMALE, Disease.UNKNOWN));
		individuals.add(new PedPerson("fam", "son", "father", "mother", Sex.MALE, Disease.UNKNOWN));
		individuals.add(new PedPerson("fam", "daughter", "father", "mother", Sex.FEMALE, Disease.UNKNOWN));
		Assert.assertEquals(pedFileContents.getIndividuals(), individuals.build());
	}

}
