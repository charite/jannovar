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
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public final class PedFileWriter {

	/** the file to write to */
	final public File file;

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
		for (String header : contents.extraColumnHeaders) {
			out.append('\t');
			out.append(header);
		}
		out.append('\n');

		// write payload
		for (PedPerson individual : contents.individuals)
			writeIndividual(individual, out);
		out.close();
	}

	private static void writeIndividual(PedPerson individual, PrintWriter out) throws IOException {
		out.append(individual.pedigree);
		out.append('\t');
		out.append(individual.name);
		out.append('\t');
		out.append(individual.father);
		out.append('\t');
		out.append(individual.mother);
		out.append('\t');
		out.append("" + individual.sex.toInt());
		out.append('\t');
		out.append("" + individual.disease.toInt());

		for (String field : individual.extraFields) {
			out.append('\t');
			out.append(field);
		}
		out.append('\n');
	}
}
