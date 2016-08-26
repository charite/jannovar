package de.charite.compbio.jannovar.cmd.annotate_vcf;

import java.io.File;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import de.charite.compbio.jannovar.JannovarOptions;
import de.charite.compbio.jannovar.data.Chromosome;
import de.charite.compbio.jannovar.data.ReferenceDictionary;
import de.charite.compbio.jannovar.htsjdk.InvalidCoordinatesException;
import de.charite.compbio.jannovar.htsjdk.VariantContextAnnotator;
import de.charite.compbio.jannovar.htsjdk.VariantContextWriterConstructionHelper;
import de.charite.compbio.jannovar.impl.util.PathUtil;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFHeaderLine;

/**
 * Annotate variant in {@link VariantContext} and write out through HTSJDK (i.e. in VCF/BCF format).
 */
public class AnnotatedVCFWriter extends AnnotatedVariantWriter {

	/** {@link ReferenceDictionary} object to use for information about the genome. */
	@SuppressWarnings("unused")
	private final ReferenceDictionary refDict;

	/** VCF header to use */
	private VCFHeader vcfHeader;

	/** path to VCF file to process */
	private final String vcfPath;

	/** configuration to use */
	private final JannovarOptions options;

	/** the {@link VariantContextAnnotator} to use. */
	private final VariantContextAnnotator annotator;

	/** writer for annotated VariantContext objects */
	private final VariantContextWriter out;

	/** command line arguments to Jannovar */
	@SuppressWarnings("unused")
	private final ImmutableList<String> args;

	public AnnotatedVCFWriter(ReferenceDictionary refDict, VCFHeader vcfHeader,
			ImmutableMap<Integer, Chromosome> chromosomeMap, String vcfPath, JannovarOptions options,
			ImmutableList<String> args) {
		this.refDict = refDict;
		this.vcfHeader = vcfHeader;
		this.annotator = new VariantContextAnnotator(refDict, chromosomeMap, new VariantContextAnnotator.Options(
				!options.showAll, options.escapeAnnField, options.nt3PrimeShifting));
		this.vcfPath = vcfPath;
		this.options = options;
		this.args = args;

		ImmutableSet<VCFHeaderLine> additionalLines = ImmutableSet.of(
				new VCFHeaderLine("jannovarVersion", JannovarOptions.JANNOVAR_VERSION),
				new VCFHeaderLine("jannovarCommand", Joiner.on(' ').join(args)));
		this.out = VariantContextWriterConstructionHelper.openVariantContextWriter(vcfHeader, getOutFileName(),
				additionalLines);
	}

	/**
	 * @return {@link VCFHeader} that is used
	 */
	public VCFHeader getVCFHeader() {
		return vcfHeader;
	}

	/**
	 * Create and return output file name.
	 *
	 * The output file name is the same as the input, with the extension ".EXT" replaced by ".jv.EXT" where EXT is one
	 * of "vcf.gz", "vcf", and "bcf". If the extension is different from these values, ".jv.vcf.gz" is appended to the
	 * input file name.
	 *
	 * When <code>options.outVCFFolder</code> is set then the file is written to this folder.
	 *
	 * @return output file name, depending on this.options
	 */
	@Override
	public String getOutFileName() {
		File f = new File(vcfPath);
		String outname = f.getName();
		if (options.outVCFFolder != null)
			outname = PathUtil.join(options.outVCFFolder, outname);
		else if (f.getParent() != null)
			outname = PathUtil.join(f.getParent(), outname);

		String suffix = ".vcf.gz";
		for (String x : new String[] { ".vcf.gz", ".vcf", ".bcf" })
			if (outname.endsWith(x))
				suffix = x;

		int i = outname.toLowerCase().lastIndexOf(suffix);
		if (i < 0)
			return outname + options.outputInfix + ".vcf.gz";
		else
			return outname.substring(0, i) + options.outputInfix + suffix;
	}

	@Override
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
