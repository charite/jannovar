package de.charite.compbio.jannovar.impl.parse.gtfgff;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.util.zip.GZIPInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: interpret only curated flag

/**
 * A class for parsing a stream of GFFRecord objects from a GTF or GFF file.
 * 
 * This class is state-ful and not thread safe.
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class GFFParser {

	private static final Logger LOGGER = LoggerFactory.getLogger(GFFParser.class);

	/** Enum type for describing the GFF version */
	public enum GFFVersion {
		/** Value for GTF ("GFF2") */
		GTF,
		/** Value for GFF3 */
		GFF3
	}

	/** Data is read line by line from this reader */
	private final BufferedReader reader;

	/** The version of the input stream, as determined when creating the parser */
	private final GFFVersion gffVersion;

	/** A Buffer with the current line, null in the beginning */
	private String lastLine = null;

	/** Object for parsing GFF records */
	private final FeatureRecordParser recordParser;

	/**
	 * Initialize with a file, gzip compression is automatically recognized.
	 * 
	 * @param file
	 *            The file to read from
	 * @throws IOException
	 *             on I/O problems
	 */
	public GFFParser(File file) throws IOException {
		this(new FileInputStream(file));
	}

	/**
	 * Reads next record from the GFF file and return it, <code>null</code> when the file is at its end.
	 * 
	 * @return GFFRecord or <code>null</code>
	 * @throws IOException
	 *             on problems with reading the GFF files
	 */
	public FeatureRecord next() throws IOException {
		if (lastLine == null)
			return null;
		FeatureRecord result = recordParser.parseLine(lastLine);
		do {
			lastLine = reader.readLine();
		} while (lastLine != null && lastLine.startsWith("#"));
		return result;
	}

	/**
	 * Initialize from a {@link InputStream}, gzip compression is automatically recognized.
	 * 
	 * @param stream
	 *            {@link InputStream} to read from
	 * @throws IOException
	 *             on I/O problems
	 */
	public GFFParser(InputStream stream) throws IOException {
		this.reader = new BufferedReader(new InputStreamReader(openStream(stream)));
		this.gffVersion = initializeStream();
		if (gffVersion == GFFVersion.GTF)
			recordParser = new GTFRecordParser();
		else
			recordParser = new GFFRecordParser();
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

	/**
	 * Skip over header of {@link #reader} and read version if any.
	 * 
	 * @return GFFVersion as determined from the stream (GFF3 is required to have a header)
	 * @throws IOException
	 *             on problems with reading the file
	 */
	private GFFVersion initializeStream() throws IOException {
		GFFVersion result = GFFVersion.GTF;
		assert lastLine == null;

		while ((lastLine = this.reader.readLine()) != null) {
			if (!lastLine.startsWith("#")) {
				break;
			} else if (lastLine.startsWith("##gff-version")) {
				String[] tokens = lastLine.split(" ");
				if (tokens[1].equals("3"))
					result = GFFVersion.GFF3;
			}
		}

		LOGGER.info("Determined GTF/GFF file version to be {}", new Object[] { result });

		return result;
	}

	/** @return GFF version detected from the stream */
	public GFFVersion getGFFVersion() {
		return gffVersion;
	}

}
