package de.charite.compbio.jannovar.cmd.annotate_vcf;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.writer.Options;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.variantcontext.writer.VariantContextWriterBuilder;
import htsjdk.variant.vcf.VCFFileReader;
import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFHeaderLineType;
import htsjdk.variant.vcf.VCFInfoHeaderLine;

import java.io.File;
import java.util.ArrayList;

import com.google.common.collect.ImmutableMap;

import de.charite.compbio.jannovar.JannovarOptions;
import de.charite.compbio.jannovar.annotation.AllAnnotationListTextGenerator;
import de.charite.compbio.jannovar.annotation.AnnotationException;
import de.charite.compbio.jannovar.annotation.AnnotationList;
import de.charite.compbio.jannovar.annotation.AnnotationListTextGenerator;
import de.charite.compbio.jannovar.annotation.BestAnnotationListTextGenerator;
import de.charite.compbio.jannovar.annotation.VariantAnnotator;
import de.charite.compbio.jannovar.impl.util.PathUtil;
import de.charite.compbio.jannovar.io.Chromosome;
import de.charite.compbio.jannovar.io.ReferenceDictionary;
import de.charite.compbio.jannovar.reference.GenomeChange;
import de.charite.compbio.jannovar.reference.GenomePosition;
import de.charite.compbio.jannovar.reference.PositionType;

/**
 * Annotate variant in {@link VariantContext} and write out through HTSJDK (i.e. in VCF/BCF format).
 */
public class AnnotatedVCFWriter extends AnnotatedVariantWriter {

	/** {@link ReferenceDictionary} object to use for information about the genome. */
	private final ReferenceDictionary refDict;

	/** path to VCF file to process */
	private final String vcfPath;

	/** configuration to use */
	private final JannovarOptions options;

	/** the VariantAnnotator to use. */
	private final VariantAnnotator annotator;

	/** writer for annotated VariantContext objects */
	VariantContextWriter out = null;

	public AnnotatedVCFWriter(ReferenceDictionary refDict, VCFFileReader reader,
			ImmutableMap<Integer, Chromosome> chromosomeMap, String vcfPath, JannovarOptions options) {
		this.refDict = refDict;
		this.annotator = new VariantAnnotator(refDict, chromosomeMap);
		this.vcfPath = vcfPath;
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
		// Be more lenient in missing header fields.
		builder.setOption(Options.ALLOW_MISSING_FIELDS_IN_HEADER);
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
		File f = new File(vcfPath);
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

		// Get shortcuts to ref, alt, and position. Note that this is "uncorrected" data, common prefixes etc. are
		// stripped when constructing the GenomeChange.
		ArrayList<AnnotationList> annoLists = new ArrayList<AnnotationList>();
		final int altCount = vc.getAlternateAlleles().size();
		for (int alleleID = 0; alleleID < altCount; ++alleleID) {
			final String ref = vc.getReference().getBaseString();
			final String alt = vc.getAlternateAllele(alleleID).getBaseString();
			final int pos = vc.getStart();
			// Construct GenomeChange from this and strip common prefixes.
			final GenomeChange change = new GenomeChange(new GenomePosition(refDict, '+', chr, pos,
					PositionType.ONE_BASED), ref, alt);

			// Collect annotation lists for all variants.
			// TODO(holtgrem): better checking of structural variants?
			if (!(alt.contains("[") || alt.contains("]") || alt.equals("."))) { // is not break-end
				AnnotationList annoList = null;
				try {
					annoList = annotator.buildAnnotationList(change);
				} catch (Exception e) {
					// swallow
				}
				if (annoList == null) {
					System.err.println(String.format("[ERROR]: Problem generating annotation for variant %s", change));
					continue; // ignore variant
				}
				annoLists.add(annoList);
			}

			// TODO(holtgrem): Find better solution for collecting annotations from more than one variant.
			StringBuilder effectText = new StringBuilder();
			StringBuilder hgvsText = new StringBuilder();
			if (this.options.showAll) {
				for (AnnotationList annoList : annoLists) {
					AnnotationListTextGenerator gen = new AllAnnotationListTextGenerator(annoList, alleleID, altCount);

					if (effectText.length() > 0)
						effectText.append(",");
					effectText.append(gen.buildEffectText());

					if (hgvsText.length() > 0)
						hgvsText.append(",");
					hgvsText.append(gen.buildHGVSText());
				}
			} else {
				AnnotationList bestList = null; // by pathogenicity
				for (AnnotationList annoList : annoLists) {
					if (annoList.entries.isEmpty())
						continue;
					if (bestList == null)
						bestList = annoList;
					else if (annoList.entries.get(0).getMostPathogenicVarType().priorityLevel() < bestList.entries
							.get(0).getMostPathogenicVarType().priorityLevel())
						bestList = annoList;
				}
				if (bestList != null) {
					AnnotationListTextGenerator gen = new BestAnnotationListTextGenerator(bestList, alleleID, altCount);
					effectText.append(gen.buildEffectText());
					hgvsText.append(gen.buildHGVSText());
				}
			}

			// add the annotations to the INFO field (third arg allows overwriting)
			if (effectText != null)
				vc.getCommonInfo().putAttribute("EFFECT", effectText.toString(), true);
			if (hgvsText.length() > 0)
				vc.getCommonInfo().putAttribute("HGVS", hgvsText.toString(), true);
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
