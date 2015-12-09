package de.charite.compbio.jannovar.htsjdk;

import htsjdk.variant.variantcontext.writer.Options;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.variantcontext.writer.VariantContextWriterBuilder;
import htsjdk.variant.vcf.VCFFileReader;
import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFHeaderLine;
import htsjdk.variant.vcf.VCFHeaderLineType;
import htsjdk.variant.vcf.VCFInfoHeaderLine;

import java.io.File;
import java.io.OutputStream;
import java.util.Collection;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.annotation.Annotation;

/**
 * Helper for creating a {@link VariantContextWriter} from a {@link VariantContextReader}.
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
	 * @param fields
	 *            selection of header fields to write out
	 * @param additionalHeaderLines
	 *            additional {@link VCFHeaderLine}s to add
	 */
	public static VariantContextWriter openVariantContextWriter(VCFHeader header, OutputStream outStream,
			InfoFields fields, Collection<VCFHeaderLine> additionalHeaderLines) {
		VariantContextWriterBuilder builder = makeBuilder(header);
		builder.unsetOption(Options.INDEX_ON_THE_FLY);
		builder.setOutputStream(outStream);

		// construct VariantContextWriter and write out header
		VariantContextWriter out = builder.build();
		final VCFHeader updatedHeader = extendHeaderFields(new VCFHeader(header), fields);
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
	 * @param fields
	 *            selection of header fields to write out
	 * @param additionalHeaderLines
	 *            additional {@link VCFHeaderLine}s to add
	 */
	public static VariantContextWriter openVariantContextWriter(VCFHeader header, String fileName, InfoFields fields,
			Collection<VCFHeaderLine> additionalHeaderLines) {
		return openVariantContextWriter(header, fileName, fields, additionalHeaderLines, false);
	}

	/**
	 * Return a new {@link VariantContextWriter} that uses the header from <code>reader</code> but has the header
	 * extended header through {@link #extendHeaderFields}.
	 *
	 * @param header
	 *            the VCF header to use for the construction
	 * @param fileName
	 *            path to output file
	 * @param fields
	 *            selection of header fields to write out
	 * @param additionalHeaderLines
	 *            additional {@link VCFHeaderLine}s to add
	 * @param generateIndex
	 *            whether or not to generate an index
	 */
	public static VariantContextWriter openVariantContextWriter(VCFHeader header, String fileName, InfoFields fields,
			Collection<VCFHeaderLine> additionalHeaderLines, boolean generateIndex) {
		VariantContextWriterBuilder builder = makeBuilder(header);
		builder.setOutputFile(new File(fileName));
		if (!generateIndex)
			builder.unsetOption(Options.INDEX_ON_THE_FLY);

		// construct VariantContextWriter and write out header
		VariantContextWriter out = builder.build();
		final VCFHeader updatedHeader = extendHeaderFields(new VCFHeader(header), fields);
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
	 */
	public static VariantContextWriter openVariantContextWriter(VCFHeader header, String fileName, InfoFields fields) {
		return openVariantContextWriter(header, fileName, fields, ImmutableList.<VCFHeaderLine> of());
	}

	/**
	 * Extend a {@link VCFHeader} with the given <code>fields</code>.
	 *
	 * @param header
	 *            the {@link VCFHeader} to extend
	 * @param fields
	 *            the {@link InfoFields} to get the field selection from
	 * @return extended VCFHeader
	 */
	public static VCFHeader extendHeaderFields(VCFHeader header, InfoFields fields) {
		if (fields == InfoFields.EFFECT_HGVS || fields == InfoFields.BOTH) {
			// add INFO line for EFFECT field
			VCFInfoHeaderLine effectLine = new VCFInfoHeaderLine("EFFECT", 1, VCFHeaderLineType.String,
					Annotation.INFO_EFFECT);
			header.addMetaDataLine(effectLine);
			// add INFO line for HGVS field
			VCFInfoHeaderLine hgvsLine = new VCFInfoHeaderLine("HGVS", 1, VCFHeaderLineType.String,
					Annotation.INFO_HGVS);
			header.addMetaDataLine(hgvsLine);
		}
		if (fields == InfoFields.VCF_ANN || fields == InfoFields.BOTH) {
			// add INFO line for standardized ANN field
			VCFInfoHeaderLine annLine = new VCFInfoHeaderLine("ANN", 1, VCFHeaderLineType.String,
					Annotation.VCF_ANN_DESCRIPTION_STRING);
			header.addMetaDataLine(annLine);
		}
		return header;
	}

}
