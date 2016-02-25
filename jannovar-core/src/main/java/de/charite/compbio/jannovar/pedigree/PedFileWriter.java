package de.charite.compbio.jannovar.pedigree;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

// TODO(holtgrem): Test me!

/**
 * Allows writing of {@link PedFileContents} to a {@link OutputStream} or {@link File}.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public final class PedFileWriter {

	/** the file to write to */
	private final File file;

	public PedFileWriter(File file) {
		this.file = file;
	}

	/**
	 * Write out the {@link PedFileContents} object to the output.
	 *
	 * @param contents
	 *            the PED file contents to write out
	 * @throws IOException
	 *             on failures during writing
	 */
	public void write(PedFileContents contents) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		BufferedOutputStream stream = new BufferedOutputStream(fos);
		write(contents, stream);
		stream.close();
		fos.close();
	}

	/**
	 * Static method for writing a {@link PedFileContents} file to a {@link OutputStream}.
	 *
	 * @param contents
	 *            {@link PedFileContents} to write
	 * @param stream
	 *            destination stream
	 * @throws IOException
	 *             on failures during writing
	 */
	public static void write(PedFileContents contents, OutputStream stream) throws IOException {
		PrintWriter out = new PrintWriter(stream);

		// write header
		out.append("#PEDIGREE\tNAME\tFATHER\tMOTHER\tSEX\tDISEASE");
		for (String header : contents.getExtraColumnHeaders()) {
			out.append('\t');
			out.append(header);
		}
		out.append('\n');

		// write payload
		for (PedPerson individual : contents.getIndividuals())
			writeIndividual(individual, out);
		out.close();
	}

	private static void writeIndividual(PedPerson individual, PrintWriter out) throws IOException {
		out.append(individual.getPedigree());
		out.append('\t');
		out.append(individual.getName());
		out.append('\t');
		out.append(individual.getFather());
		out.append('\t');
		out.append(individual.getMother());
		out.append('\t');
		out.append("" + individual.getSex().toInt());
		out.append('\t');
		out.append("" + individual.getDisease().toInt());

		for (String field : individual.getExtraFields()) {
			out.append('\t');
			out.append(field);
		}
		out.append('\n');
	}
}
