package de.charite.compbio.jannovar.mendel.filter;

import java.util.HashSet;

import htsjdk.variant.variantcontext.VariantContext;

/**
 * Check that the VCF file is sorted by coordinate
 *
 * Since VCF files do not have to provide a reference name dictionary in their header, validating the sort order of the
 * chromosomes is tricky. Instead, we check that there is no run of VCF records for a chromosome, something on a
 * different chromsome, and then a previous one again. Within one chromosome, we check by change begin position.
 *
 * In case of problems, {@link #put} throws a {@link VariantContextFilterException}.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public class CoordinateSortingChecker implements VariantContextProcessor {

	/** Next pipeline step */
	VariantContextProcessor sink;

	/** set of seen chromosome names */
	final private HashSet<String> seenChromosomes = new HashSet<String>();

	/** previously seen variant context */
	private VariantContext prevVC = null;

	/** Initialize the checker */
	public CoordinateSortingChecker(VariantContextProcessor sink) {
		this.sink = sink;
	}

	public void put(VariantContext vc) throws VariantContextFilterException {
		// Perform sortedness check and throw exception otherwise
		if (prevVC != null)
			if (!vc.getContig().equals(prevVC.getContig())) { // change in chromosomes
				if (seenChromosomes.contains(vc.getContig()))
					throw new VariantContextFilterException("Unsorted VCF file, seen " + vc.getContig() + " twice!");
			} else {
				if (vc.getStart() < prevVC.getStart())
					throw new VariantContextFilterException("Unsorted VCF file, seen " + vc.getStart() + " < " + prevVC.getStart());
			}

		// Pass through to next filter
		sink.put(vc);

		// Update prevVC reference
		prevVC = vc;
	}

	public void close() {
		sink.close();
	}

}
