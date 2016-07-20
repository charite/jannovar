package de.charite.compbio.jannovar.impl.parse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import com.google.common.base.Joiner;

import de.charite.compbio.jannovar.impl.parse.gtfgff.GFFParser.GFFVersion;

/**
 * Generic FASTA parser that allow record-wise loading of FASTA files
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class FASTAParser {

	/** Data is read line by line from this reader */
	private final BufferedReader reader;

	/** A Buffer with the current line, null in the beginning */
	private String lastLine = null;

	/** Lines collect for this record so far */
	private final List<String> recordBuffer = new ArrayList<>();

	/**
	 * Initialize with a file, gzip compression is automatically recognized.
	 * 
	 * @param file
	 *            The file to read from
	 * @throws IOException
	 *             on I/O problems
	 */
	public FASTAParser(File file) throws IOException {
		this(new FileInputStream(file));
	}

	/**
	 * Initialize from a {@link InputStream}, gzip compression is automatically recognized.
	 * 
	 * @param stream
	 *            {@link InputStream} to read from
	 * @throws IOException
	 *             on I/O problems
	 */
	public FASTAParser(InputStream stream) throws IOException {
		this.reader = new BufferedReader(new InputStreamReader(openStream(stream)));
		this.lastLine = reader.readLine(); // read first line
		if (this.lastLine != null)
			this.lastLine = this.lastLine.trim();
	}

	/**
	 * Reads next record from the GFF file and return it, <code>null</code> when the file is at its end.
	 * 
	 * @return GFFRecord or <code>null</code>
	 * @throws IOException
	 *             on problems with reading the GFF files
	 */
	public FASTARecord next() throws IOException {
		if (lastLine == null)
			return null;

		assert lastLine.startsWith(">");

		while (true) {
			// add current line to buffer
			if (lastLine != null && !lastLine.isEmpty())
				recordBuffer.add(lastLine);

			// read next line, skipping empty lines
			lastLine = reader.readLine();

			if (lastLine == null || lastLine.startsWith(">"))
				break;
		}

		return buildRecord();
	}

	/** Build record from {@link #recordBuffer} */
	private FASTARecord buildRecord() {
		if (recordBuffer.isEmpty())
			return null;

		final String firstLine = recordBuffer.get(0);
		String[] tokens = firstLine.substring(1).split("\\s", 2);
		String id = tokens[0];
		String comment = (tokens.length > 1) ? tokens[1] : "";
		String sequence = Joiner.on("").join(recordBuffer.subList(1, recordBuffer.size()));

		recordBuffer.clear();
		return new FASTARecord(id, comment, sequence);
	}

	/**
	 * Open the {@link InputStream} as a {@link BufferedReader}
	 * 
	 * @return {@link InputStream}, wrapping a gzip reading stream if <code>stream</code> is gzip compressed
	 * @throws IOException
	 *             on I/O problems
	 */
	private InputStream openStream(InputStream stream) throws IOException {
		PushbackInputStream pb = new PushbackInputStream(stream, 2);
		byte[] signature = new byte[2];
		pb.read(signature);
		pb.unread(signature);
		if (signature[0] == (byte) 0x1f && signature[1] == (byte) 0x8b)
			return new GZIPInputStream(pb);
		else
			return pb;
	}

}
