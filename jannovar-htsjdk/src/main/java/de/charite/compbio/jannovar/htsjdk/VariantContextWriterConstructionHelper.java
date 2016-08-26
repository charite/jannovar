package de.charite.compbio.jannovar.htsjdk;

import java.io.File;
import java.io.OutputStream;
import java.util.Collection;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.annotation.Annotation;
import htsjdk.variant.variantcontext.writer.Options;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.variantcontext.writer.VariantContextWriterBuilder;
import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFHeaderLine;
import htsjdk.variant.vcf.VCFHeaderLineType;
import htsjdk.variant.vcf.VCFInfoHeaderLine;

/**
 * Helper for creating a {@link VariantContextWriter} from a {@link OutputStream}.
 *
 * Part of the Jannovar-HTSJDK bridge.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public final class VariantContextWriterConstructionHelper {

	/**
	 * Return a new {@link VariantContextWriter} that uses the header from <code>reader</code> but has the header
	 * extended header through {@link #extendHeaderFields}.
	 *
	 * @param header
	 *            the VCF header to use for the construction
	 * @param outStream
	 *            {@link OutputStream} to write to
	 * @param additionalHeaderLines
	 *            additional {@link VCFHeaderLine}s to add
	 * @return A correct writer for variantContexts
	 */
	public static VariantContextWriter openVariantContextWriter(VCFHeader header, OutputStream outStream,
			Collection<VCFHeaderLine> additionalHeaderLines) {
		VariantContextWriterBuilder builder = makeBuilder(header);
		builder.unsetOption(Options.INDEX_ON_THE_FLY);
		builder.setOutputStream(outStream);

		// construct VariantContextWriter and write out header
		VariantContextWriter out = builder.build();
		final VCFHeader updatedHeader = extendHeaderFields(new VCFHeader(header));
		for (VCFHeaderLine headerLine : additionalHeaderLines)
			updatedHeader.addMetaDataLine(headerLine);
		out.writeHeader(updatedHeader);
		return out;
	}

	/**
	 * Return a new {@link VariantContextWriter} that uses the header from <code>reader</code> but has the header
	 * extended header through {@link #extendHeaderFields}.
	 *
	 * @param header
	 *            the VCF header to use for the construction
	 * @param fileName
	 *            path to output file
	 * @param additionalHeaderLines
	 *            additional {@link VCFHeaderLine}s to add
	 * @return A correct writer for variantContexts
	 */
	public static VariantContextWriter openVariantContextWriter(VCFHeader header, String fileName,
			Collection<VCFHeaderLine> additionalHeaderLines) {
		return openVariantContextWriter(header, fileName, additionalHeaderLines, false);
	}

	/**
	 * Return a new {@link VariantContextWriter} that uses the header from <code>reader</code> but has the header
	 * extended header through {@link #extendHeaderFields}.
	 *
	 * @param header
	 *            the VCF header to use for the construction
	 * @param fileName
	 *            path to output file
	 * @param additionalHeaderLines
	 *            additional {@link VCFHeaderLine}s to add
	 * @param generateIndex
	 *            whether or not to generate an index
	 * @return A correct writer for variantContexts
	 */
	public static VariantContextWriter openVariantContextWriter(VCFHeader header, String fileName,
			Collection<VCFHeaderLine> additionalHeaderLines, boolean generateIndex) {
		VariantContextWriterBuilder builder = makeBuilder(header);
		builder.setOutputFile(new File(fileName));
		if (!generateIndex)
			builder.unsetOption(Options.INDEX_ON_THE_FLY);

		// construct VariantContextWriter and write out header
		VariantContextWriter out = builder.build();
		final VCFHeader updatedHeader = extendHeaderFields(new VCFHeader(header));
		for (VCFHeaderLine headerLine : additionalHeaderLines)
			updatedHeader.addMetaDataLine(headerLine);
		out.writeHeader(updatedHeader);
		return out;
	}

	/**
	 * Common parts of {@link VariantContextWriterBuilder} creation for the openVariantContextWriter functions.
	 */
	private static VariantContextWriterBuilder makeBuilder(VCFHeader header) {
		// construct factory object for VariantContextWriter
		VariantContextWriterBuilder builder = new VariantContextWriterBuilder();
		builder.setReferenceDictionary(header.getSequenceDictionary());
		// Be more lenient in missing header fields.
		builder.setOption(Options.ALLOW_MISSING_FIELDS_IN_HEADER);
		// Disable on-the-fly generation of Tribble index if the input file does not have a sequence dictionary.
		if (header.getSequenceDictionary() == null)
			builder.unsetOption(Options.INDEX_ON_THE_FLY);
		return builder;
	}

	/**
	 * Forward to {@link #openVariantContextWriter(VCFHeader, String, InfoFields, Collection)}.
	 ** 
	 * @param header
	 *            the VCF header to use for the construction
	 * @param fileName
	 *            path to output file
	 * @return A correct writer for variantContexts
	 */
	public static VariantContextWriter openVariantContextWriter(VCFHeader header, String fileName) {
		return openVariantContextWriter(header, fileName, ImmutableList.<VCFHeaderLine> of());
	}

	/**
	 * Extend a {@link VCFHeader} with the given <code>fields</code>.
	 *
	 * @param header
	 *            the {@link VCFHeader} to extend
	 * @return extended VCFHeader
	 */
	public static VCFHeader extendHeaderFields(VCFHeader header) {
		// add INFO line for standardized ANN field
		VCFInfoHeaderLine annLine = new VCFInfoHeaderLine("ANN", 1, VCFHeaderLineType.String,
				Annotation.VCF_ANN_DESCRIPTION_STRING);
		header.addMetaDataLine(annLine);
		return header;
	}

}
