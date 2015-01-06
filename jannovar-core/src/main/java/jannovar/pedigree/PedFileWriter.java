package jannovar.pedigree;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

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
		write(contents, new BufferedOutputStream(new FileOutputStream(file)));
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
		DataOutputStream out = new DataOutputStream(stream);

		// write header
		out.writeBytes("#PEDIGREE\tNAME\tFATHER\tMOTHER\tSEX\tDISEASE");
		for (String header : contents.extraColumnHeaders) {
			out.writeChar('\t');
			out.writeBytes(header);
		}

		// write payload
		for (PedPerson individual : contents.individuals)
			writeIndividual(individual, out);
	}

	private static void writeIndividual(PedPerson individual, DataOutputStream out) throws IOException {
		out.writeBytes(individual.pedigree);
		out.writeChar('\t');
		out.writeBytes(individual.name);
		out.writeChar('\t');
		out.writeBytes(individual.father);
		out.writeChar('\t');
		out.writeBytes(individual.mother);
		out.writeChar('\t');
		out.writeBytes("" + individual.sex.toInt());
		out.writeChar('\t');
		out.writeBytes("" + individual.disease.toInt());

		for (String field : individual.extraFields) {
			out.writeChar('\t');
			out.writeBytes(field);
		}
	}
}
