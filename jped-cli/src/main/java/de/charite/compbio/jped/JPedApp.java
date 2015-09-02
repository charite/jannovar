package de.charite.compbio.jped;

import htsjdk.variant.variantcontext.writer.Options;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.variantcontext.writer.VariantContextWriterBuilder;
import htsjdk.variant.vcf.VCFFileReader;

import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;

import de.charite.compbio.jannovar.JannovarException;
import de.charite.compbio.jannovar.data.JannovarData;
import de.charite.compbio.jannovar.data.JannovarDataSerializer;
import de.charite.compbio.jannovar.pedigree.PedFileContents;
import de.charite.compbio.jannovar.pedigree.PedFileReader;
import de.charite.compbio.jannovar.pedigree.Pedigree;
import de.charite.compbio.jannovar.pedigree.Person;

// TODO(holtgrew): Add support for DE NOVO

public class JPedApp {

	/** Configuration */
	private JPedOptions options;

	/** Jannovar DB */
	JannovarData jannovarDB;

	JPedApp(JPedOptions options) {
		this.options = options;
	}

	void run() throws JannovarException, HelpRequestedException {
		options.print(System.err);
		setLogLevel();

		final long startTime = System.nanoTime();
		VCFFileReader reader = new VCFFileReader(new File(options.inputPath), false);
		VariantContextWriter writer = getWriter(reader);
		deserializeJannovarDB(); // only need this when inheritance filter is active

		PedFileContents pedContents;
		try {
			pedContents = new PedFileReader(new File(options.pedPath)).read();
		} catch (IOException e) {
			throw new JannovarException("Could not parse Pedigree from " + options.pedPath, e);
		}
		Pedigree pedigree = new Pedigree(pedContents, pedContents.getIndividuals().get(0).getPedigree());
		System.err.println("Family used from PED file: " + pedigree.getName());
		for (Person p : pedigree.getMembers())
			System.err.println("    " + p.getName());
		new FilteredWriter(pedigree, options.modeOfInheritances, jannovarDB, reader, writer).run(options);
		writer.close();
		final long endTime = System.nanoTime();
		System.err.println(String.format("Filtering and writing took %.2f sec.",
				(endTime - startTime) / 1000.0 / 1000.0 / 1000.0));
	}

	/**
	 * @return {@link VariantContextWriter} given an input {@link VCFFileReader}.
	 */
	VariantContextWriter getWriter(VCFFileReader reader) {
		// construct factory object for VariantContextWriter
		VariantContextWriterBuilder builder = new VariantContextWriterBuilder();
		builder.setReferenceDictionary(reader.getFileHeader().getSequenceDictionary());
		builder.setOutputFile(new File(options.outputPath));
		// Be more lenient in missing header fields.
		builder.setOption(Options.ALLOW_MISSING_FIELDS_IN_HEADER);
		// Disable on-the-fly generation of Tribble index if the input file does not have a sequence dictionary.
		if (reader.getFileHeader().getSequenceDictionary() == null)
			builder.unsetOption(Options.INDEX_ON_THE_FLY);

		// construct VariantContextWriter and write out header
		VariantContextWriter out = builder.build();
		out.writeHeader(reader.getFileHeader());
		return out;
	}

	protected void deserializeJannovarDB() throws JannovarException, HelpRequestedException {
		this.jannovarDB = new JannovarDataSerializer(options.jannovarDB).load();
	}

	public static void main(String[] args) {
		JPedCommandLineParser parser = new JPedCommandLineParser();
		try {
			JPedOptions options = parser.parse(args);
			new JPedApp(options).run();
		} catch (ParseException e) {
			System.err.println("ERROR: " + e.getMessage());
			parser.printHelp();
			System.exit(1);
		} catch (HelpRequestedException e) {
			System.err.println("ERROR: " + e.getMessage());
			parser.printHelp();
			System.exit(1);
		} catch (JannovarException e) {
			System.err.println("ERROR: " + e.getMessage());
			parser.printHelp();
			System.exit(1);
		}
	}

	/**
	 * Set log level, depending on this.verbosity.
	 */
	private void setLogLevel() {
		LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
		Configuration conf = ctx.getConfiguration();

		if (options.verbosity <= 1)
			conf.getLoggerConfig(LogManager.ROOT_LOGGER_NAME).setLevel(Level.INFO);
		else if (options.verbosity <= 2)
			conf.getLoggerConfig(LogManager.ROOT_LOGGER_NAME).setLevel(Level.DEBUG);
		else
			conf.getLoggerConfig(LogManager.ROOT_LOGGER_NAME).setLevel(Level.TRACE);

		ctx.updateLoggers(conf);
	}

}
