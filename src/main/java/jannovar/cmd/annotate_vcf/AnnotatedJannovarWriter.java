package jannovar.cmd.annotate_vcf;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.VariantContext;
import jannovar.JannovarOptions;
import jannovar.annotation.Annotation;
import jannovar.annotation.AnnotationList;
import jannovar.annotation.VariantAnnotator;
import jannovar.annotation.VariantDataCorrector;
import jannovar.common.ChromosomeMap;
import jannovar.exception.AnnotationException;
import jannovar.reference.Chromosome;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Although public, this class is not meant to be part of the public Jannovar intervace. It can be changed or removed at
 * any point.
 */
public class AnnotatedJannovarWriter extends AnnotatedVariantWriter {

	/** configuration to use */
	private JannovarOptions options;

	/** the VariantAnnotator to use. */
	private VariantAnnotator annotator;

	/** BufferedWriter to use for writing */
	BufferedWriter out = null;

	/** current line */
	int currentLine = 0;

	public AnnotatedJannovarWriter(HashMap<Byte, Chromosome> chromosomeMap, JannovarOptions options) throws IOException {
		this.annotator = new VariantAnnotator(chromosomeMap);
		this.options = options;
		this.openBufferedWriter();
	}

	@Override
	public String getOutFileName() {
		// build file name for output file
		File f = new File(this.options.vcfFilePath);
		return f.getName() + ".jannovar";
	}

	/**
	 * Open the output file stream.
	 *
	 * @throws IOException
	 *             when opening the output file failed.
	 */
	private void openBufferedWriter() throws IOException {
		// try to open file
		try {
			FileWriter fstream = new FileWriter(getOutFileName());
			out = new BufferedWriter(fstream);
		} catch (IOException e) {
			close(); // swallows any exception thrown by this.out.close
			throw e; // rethrow e
		}
	}

	/** Close writer, free resources */
	@Override
	public void close() {
		try {
			if (out != null)
				out.close();
		} catch (IOException e) {
			// swallow, nothing we can do about it
		}
	}

	/**
	 * Write out record for VariantContext.
	 *
	 * @throws AnnotationException
	 *             when a problem with annotation occurs
	 * @throws IOException
	 *             when problem with I/O occurs
	 */
	@Override
	public void put(VariantContext vc) throws AnnotationException, IOException {
		currentLine++;

		String chrStr = vc.getChr();
		// Catch the case that variantContext.getChr() is not in ChromosomeMap.identifier2chromosom. This is the case
		// for the "random" contigs etc. In this case, we simply ignore the record.
		Byte boxedChr = ChromosomeMap.identifier2chromosom.get(vc.getChr());
		if (boxedChr == null)
			return;
		byte chr = boxedChr.byteValue();

		// FIXME(mjaeger): We should care about more than just the first alternative allele.
		// translate from VCF ref/alt/pos to internal Jannovar representation
		VariantDataCorrector corr = new VariantDataCorrector(vc.getReference().getBaseString(), vc
				.getAlternateAllele(0).getBaseString(), vc.getStart());
		String ref = corr.ref;
		String alt = corr.alt;
		int pos = corr.position;

		String gtype = stringForGenotype(vc, 0);
		float qual = (float) vc.getPhredScaledQual();
		AnnotationList anno = annotator.getAnnotationList(chr, pos, ref, alt);
		if (anno == null) {
			String e = String.format("No annotations found for variant %s", vc.toString());
			throw new AnnotationException(e);
		}

		ArrayList<Annotation> lst = anno.getAnnotationList();
		for (Annotation a : lst) {
			String effect = a.getVariantTypeAsString();
			String annt = a.getVariantAnnotation();
			String sym = a.getGeneSymbol();
			String s = String.format("%d\t%s\t%s\t%s\t%s\t%d\t%s\t%s\t%s\t%.1f\n", currentLine, effect, sym, annt,
					chrStr, pos, ref, alt, gtype, qual);
			out.write(s);
		}
	}

	/**
	 * Return genotype string as in VCF for the i-th individual at the position in variantContext.
	 *
	 * @param variantContext
	 *            The VariantContext to query.
	 * @param i
	 *            Index of individual.
	 * @return String with the genotype call string, e.g. "0/1" or "1|1".
	 */
	private String stringForGenotype(VariantContext variantContext, int i) {
		Genotype gt = variantContext.getGenotype(i);
		StringBuilder builder = new StringBuilder();
		for (Allele allele : gt.getAlleles()) {
			if (builder.length() > 0)
				builder.append(gt.isPhased() ? '|' : '/');
			builder.append(variantContext.getAlleleIndex(allele));
		}
		return builder.toString();
	}
}
