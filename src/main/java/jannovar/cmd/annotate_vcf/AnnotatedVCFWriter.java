package jannovar.cmd.annotate_vcf;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.writer.Options;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.variantcontext.writer.VariantContextWriterBuilder;
import htsjdk.variant.vcf.VCFFileReader;
import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFHeaderLineType;
import htsjdk.variant.vcf.VCFInfoHeaderLine;
import jannovar.JannovarOptions;
import jannovar.annotation.AnnotationException;
import jannovar.annotation.AnnotationList;
import jannovar.annotation.VariantAnnotator;
import jannovar.io.ReferenceDictionary;
import jannovar.reference.Chromosome;
import jannovar.reference.GenomeChange;
import jannovar.reference.GenomePosition;
import jannovar.reference.PositionType;
import jannovar.util.PathUtil;

import java.io.File;
import java.util.HashMap;

/**
 * Although public, this class is not meant to be part of the public Jannovar intervace. It can be changed or removed at
 * any point.
 */
public class AnnotatedVCFWriter extends AnnotatedVariantWriter {

	/** {@link ReferenceDictionary} object to use for information about the genome. */
	private final ReferenceDictionary refDict;

	/** configuration to use */
	private final JannovarOptions options;

	/** the VariantAnnotator to use. */
	private final VariantAnnotator annotator;

	/** writer for annotated VariantContext objects */
	VariantContextWriter out = null;

	public AnnotatedVCFWriter(ReferenceDictionary refDict, VCFFileReader reader,
			HashMap<Integer, Chromosome> chromosomeMap, JannovarOptions options) {
		this.refDict = refDict;
		this.annotator = new VariantAnnotator(refDict, chromosomeMap);
		this.options = options;
		openVariantContextWriter(reader);
	}

	/**
	 * Initialize this.out and write out header already.
	 *
	 * We need <tt>reader</tt> for the sequence dictionary and the VCF header.
	 *
	 * @param reader
	 *            the reader to use for the construction
	 */
	private void openVariantContextWriter(VCFFileReader reader) {
		// construct factory object for VariantContextWriter
		VariantContextWriterBuilder builder = new VariantContextWriterBuilder();
		builder.setReferenceDictionary(reader.getFileHeader().getSequenceDictionary());
		builder.setOutputFile(new File(getOutFileName()));
		// Disable on-the-fly generation of Tribble index if the input file does not have a sequence dictionary.
		if (reader.getFileHeader().getSequenceDictionary() == null)
			builder.unsetOption(Options.INDEX_ON_THE_FLY);

		// construct VariantContextWriter and write out header
		out = builder.build();
		out.writeHeader(extendHeaderFields(reader.getFileHeader()));
	}

	/** @return extended VCFHeader */
	private VCFHeader extendHeaderFields(VCFHeader header) {
		// add INFO line for EFFECT field
		VCFInfoHeaderLine effectLine = new VCFInfoHeaderLine("EFFECT", 1, VCFHeaderLineType.String,
				VCFStrings.INFO_EFFECT);
		header.addMetaDataLine(effectLine);
		// add INFO line for HGVS field
		VCFInfoHeaderLine hgvsLine = new VCFInfoHeaderLine("HGVS", 1, VCFHeaderLineType.String, VCFStrings.INFO_HGVS);
		header.addMetaDataLine(hgvsLine);
		return header;
	}

	/** @return output file name, depending on this.options */
	@Override
	public String getOutFileName() {
		File f = new File(this.options.vcfFilePath);
		String outname = f.getName();
		if (options.outVCFFolder != null)
			outname = PathUtil.join(options.outVCFFolder, outname);
		int i = outname.lastIndexOf("vcf");
		if (i < 0)
			i = outname.lastIndexOf("VCF");
		if (i < 0)
			return outname + ".jv.vcf";
		else
			return outname.substring(0, i) + "jv.vcf";
	}

	@Override
	public void put(VariantContext vc) throws AnnotationException {
		// Catch the case that vc.getChr() is not in ChromosomeMap.identifier2chromosom. This is the case
		// for the "random" contigs etc. In this case, we simply write the record out unmodified.
		Integer boxedInt = refDict.contigID.get(vc.getChr());
		if (boxedInt == null) {
			out.add(vc);
			return;
		}
		int chr = boxedInt.intValue();

		// FIXME(mjaeger): We should care about more than just the first alternative allele.

		// Get shortcuts to ref, alt, and position. Note that this is "uncorrected" data, common prefixes etc. are
		// stripped when constructing the GenomeChange.
		final String ref = vc.getReference().getBaseString();
		final String alt = vc.getAlternateAllele(0).getBaseString();
		final int pos = vc.getStart();
		// Construct GenomeChange from this and strip common prefixes.
		final GenomeChange change = new GenomeChange(
				new GenomePosition(refDict, '+', chr, pos, PositionType.ONE_BASED), ref, alt);

		// TODO(holtgrem): better checking of structural variants?
		if (!(alt.contains("[") || alt.contains("]") || alt.equals("."))) { // is not break-end
			AnnotationList anno = annotator.buildAnnotationList(change);
			if (anno == null) {
				String e = String.format("No annotations found for variant %s", vc.toString());
				throw new AnnotationException(e);
			}
			String annotation;
			String effect;
			if (anno.isStructural()) {
				annotation = anno.getCombinedAnnotationForStructuralVariant();
				effect = anno.getVariantType().toString();
			} else {
				if (this.options.showAll) {
					annotation = anno.getAllTranscriptAnnotations();
					effect = anno.getAllTranscriptVariantEffects();
				} else {
					annotation = anno.getSingleTranscriptAnnotation();
					effect = anno.getVariantType().toString();
				}
			}

			// add the annotations to the INFO field (third arg allows overwriting)
			vc.getCommonInfo().putAttribute("EFFECT", effect, true);
			vc.getCommonInfo().putAttribute("HGVS", annotation, true);
		}

		// Write out variantContext to out.
		out.add(vc);
	}

	/** Close VariantContextWriter in out. */
	@Override
	public void close() {
		out.close();
	}
}
