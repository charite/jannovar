package de.charite.compbio.jannovar;

import htsjdk.variant.variantcontext.writer.Options;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.variantcontext.writer.VariantContextWriterBuilder;
import htsjdk.variant.vcf.VCFFileReader;

import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.ParseException;

import de.charite.compbio.jannovar.io.JannovarData;
import de.charite.compbio.jannovar.io.JannovarDataSerializer;
import de.charite.compbio.jannovar.pedigree.PedFileContents;
import de.charite.compbio.jannovar.pedigree.PedFileReader;
import de.charite.compbio.jannovar.pedigree.Pedigree;

// TODO(holtgrew): Add support for DE NOVO

public class JannovarFilterApp {

	/** Configuration */
	private JannovarFilterOptions options;

	/** Jannovar DB */
	JannovarData jannovarDB;

	JannovarFilterApp(JannovarFilterOptions options) {
		this.options = options;
	}

	void run() throws JannovarException, HelpRequestedException {
		options.print(System.err);

		final long startTime = System.nanoTime();
		VCFFileReader reader = new VCFFileReader(new File(options.inputPath), false);
		VariantContextWriter writer = getWriter(reader);
		deserializeJannovarDB(); // only need this when inheritance filter is active

		PedFileContents pedContents;
		try {
			pedContents = new PedFileReader(new File(options.pedPath)).read();
		} catch (IOException e) {
			throw new JannovarException("Could not parse Pedigree from " + options.pedPath);
		}
		Pedigree pedigree = new Pedigree(pedContents, pedContents.individuals.get(0).pedigree);
		new FilteredWriter(pedigree, options.modeOfInheritance, jannovarDB, reader, writer).run(options);
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
		final long startTime = System.nanoTime();
		this.jannovarDB = new JannovarDataSerializer(options.jannovarDB).load();
		final long endTime = System.nanoTime();
	}

	public static void main(String[] args) {
		try {
			JannovarFilterOptions options = new JannovarFilterCommandLineParser().parse(args);
			new JannovarFilterApp(options).run();
		} catch (ParseException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (HelpRequestedException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (JannovarException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

}
