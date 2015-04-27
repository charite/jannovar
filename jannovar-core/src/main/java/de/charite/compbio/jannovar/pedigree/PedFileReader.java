package de.charite.compbio.jannovar.pedigree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;

// TODO(holtgrem): Test me!

/**
 * Allows reading of {@link PedFileContents} from a {@link InputStream} or {@link File}.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 * @author Max Schubach <max.schubach@charite.de>
 */
public final class PedFileReader {

	/** the file to read from */
	private final File file;

	/**
	 * Initialize object with the given file.
	 */
	public PedFileReader(File file) {
		this.file = file;
	}

	/**
	 * Read in the pedigree file in {@link #file}.
	 *
	 * @throws IOException
	 *             in the case of problems with reading from {@link #file}
	 * @throws PedParseException
	 *             in the case of problems with parsing the data from {@link #file}
	 */
	public PedFileContents read() throws IOException, PedParseException {
		return read(new FileInputStream(file));
	}

	/**
	 * Static method for parsing a PED file into a {@link PedFileContents} object.
	 *
	 * @param stream
	 *            input stream to read from
	 * @return resulting {@link PedFileContents} representing the contents of the file
	 * @throws IOException
	 *             in the case of problems with reading from <code>stream</code>
	 * @throws PedParseException
	 *             in the case of problems with parsing the data from <code>stream</code>
	 */
	public static PedFileContents read(InputStream stream) throws IOException, PedParseException {
		BufferedReader in = new BufferedReader(new InputStreamReader(stream, "UTF-8"));

		// Parse header.
		ImmutableList<String> extraHeaders = ImmutableList.of(); // default to empty
		String line = in.readLine();
		if (line != null && line.startsWith("#")) {
			extraHeaders = parseHeader(line);
			line = in.readLine();
		}

		// Parse individuals.
		ImmutableList.Builder<PedPerson> individualBuilder = new ImmutableList.Builder<PedPerson>();
		while (line != null) {
			line = line.trim(); // trim leading and trailing whitespace
			if (line.length() != 0) // ignore empty lines
				individualBuilder.add(readIndividual(line));

			line = in.readLine(); // read next
		}

		return new PedFileContents(extraHeaders, individualBuilder.build());
	}

	/**
	 * Parse header and return extra header fields, <code>line</code> must start with <code>'#'</code>.
	 */
	private static ImmutableList<String> parseHeader(String line) {
		ImmutableList.Builder<String> extraHeaderBuilder = new ImmutableList.Builder<String>();
		Iterator<String> it = Splitter.on('\t').split(line.trim().substring(1)).iterator();
		for (int i = 0; it.hasNext(); ++i)
			if (i < 6)
				it.next();
			else
				extraHeaderBuilder.add(it.next());
		return extraHeaderBuilder.build();
	}

	/**
	 * Parse individual from the given line.
	 *
	 * @throws PedParseException
	 *             on problems with the parsing
	 */
	private static PedPerson readIndividual(String line) throws PedParseException {
		try {
			Iterator<String> it = Splitter.on('\t').split(line.trim()).iterator();

			/*
			 * public PedPerson(String pedigree, String name, String father, String mother, Sex sex, Disease disease,
			 * Collection<String> extraFields) {
			 */

			// parse out core fields
			String pedigree = it.next();
			String name = it.next();
			String father = it.next();
			String mother = it.next();
			String sex = it.next();
			String disease = it.next();

			// parse out extra fields
			ImmutableList.Builder<String> extraFields = new ImmutableList.Builder<String>();
			while (it.hasNext())
				extraFields.add(it.next());

			return new PedPerson(pedigree, name, father, mother, Sex.toSex(sex), Disease.toDisease(disease),
					extraFields.build());
		} catch (NoSuchElementException e) {
			throw new PedParseException("Insufficient number of fields in line: \"" + line + "\"");
		}
	}

}