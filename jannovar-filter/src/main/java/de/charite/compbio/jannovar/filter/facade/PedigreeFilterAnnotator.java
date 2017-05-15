package de.charite.compbio.jannovar.filter.facade;

import de.charite.compbio.jannovar.pedigree.Pedigree;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;

/**
 * Further pedigree-based filters (beyond mode of inheritance).
 *
 * <p>
 * Useful for obtaining reliable de novo variant calls.
 * </p>
 *
 * <p>
 * Note that this filter has to be applied <b>after</b> {@link ThresholdFilterAnnotator} because the
 * de novo filtration settings would otherwise conflict with the "all affected individuals filtered"
 * variant filter.
 * </p>
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class PedigreeFilterAnnotator {

	/** Filter configuration */
	private final PedigreeFilterOptions options;

	/** Mapping from individual to {@link Pedigree} */
	private final Pedigree pedigree;

	public PedigreeFilterAnnotator(PedigreeFilterOptions options, Pedigree pedigree) {
		super();
		this.options = options;
		this.pedigree = pedigree;
	}

	/**
	 * Annotate VariantContext with the pedigree-based filters
	 *
	 * @param vc
	 *            the {@link VariantContext} to annotate
	 * @return copy of <code>vc</code> with applied annotations
	 */
	public VariantContext annotateVariantContext(VariantContext vc) {
		VariantContextBuilder builder = new VariantContextBuilder(vc);
		return builder.make();
	}

}
