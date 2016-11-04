package de.charite.compbio.jannovar.cmd.annotate_vcf;

import java.io.Closeable;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import de.charite.compbio.jannovar.Jannovar;
import de.charite.compbio.jannovar.data.Chromosome;
import de.charite.compbio.jannovar.data.ReferenceDictionary;
import de.charite.compbio.jannovar.htsjdk.InvalidCoordinatesException;
import de.charite.compbio.jannovar.htsjdk.VariantContextAnnotator;
import de.charite.compbio.jannovar.htsjdk.VariantContextWriterConstructionHelper;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFHeaderLine;

/**
 * Annotate variant in {@link VariantContext} and write out through HTSJDK (i.e. in VCF/BCF format).
 */
public class AnnotatedVCFWriter implements Closeable {

	/** {@link ReferenceDictionary} object to use for information about the genome. */
	@SuppressWarnings("unused")
	private final ReferenceDictionary refDict;

	/** VCF header to use */
	private VCFHeader vcfHeader;

	/** configuration to use */
	private final JannovarAnnotateVCFOptions options;

	/** the {@link VariantContextAnnotator} to use. */
	private final VariantContextAnnotator annotator;

	/** writer for annotated VariantContext objects */
	private final VariantContextWriter out;

	/** command line arguments to Jannovar */
	@SuppressWarnings("unused")
	private final ImmutableList<String> args;

	public AnnotatedVCFWriter(ReferenceDictionary refDict, VCFHeader vcfHeader,
			ImmutableMap<Integer, Chromosome> chromosomeMap, String vcfPath, JannovarAnnotateVCFOptions options,
			ImmutableList<String> args) {
		this.refDict = refDict;
		this.vcfHeader = vcfHeader;
		this.annotator = new VariantContextAnnotator(refDict, chromosomeMap, new VariantContextAnnotator.Options(
				!options.isShowAll(), options.isEscapeAnnField(), options.isNt3PrimeShifting()));
		this.options = options;
		this.args = args;

		ImmutableSet<VCFHeaderLine> additionalLines = ImmutableSet.of(
				new VCFHeaderLine("jannovarVersion", Jannovar.JANNOVAR_VERSION),
				new VCFHeaderLine("jannovarCommand", Joiner.on(' ').join(args)));
		this.out = VariantContextWriterConstructionHelper.openVariantContextWriter(vcfHeader,
				options.getPathOutputVCF(), additionalLines);
	}

	/**
	 * @return {@link VCFHeader} that is used
	 */
	public VCFHeader getVCFHeader() {
		return vcfHeader;
	}

	public void put(VariantContext vc) {
		try {
			vc = annotator.applyAnnotations(vc, annotator.buildAnnotations(vc));
		} catch (InvalidCoordinatesException e) {
			annotator.putErrorAnnotation(vc, ImmutableSet.of(e.getAnnotationMessage()));
		}
		vc.getCommonInfo().removeAttribute(""); // remove leading/trailing comma
		out.add(vc);
	}

	/** Close VariantContextWriter in out. */
	@Override
	public void close() {
		out.close();
	}

}
