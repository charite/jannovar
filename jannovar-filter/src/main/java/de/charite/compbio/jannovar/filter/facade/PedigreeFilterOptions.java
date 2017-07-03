package de.charite.compbio.jannovar.filter.facade;

/**
 * Configuration for {@link PedigreeFilterAnnotator}.
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class PedigreeFilterOptions {


	public static PedigreeFilterOptions buildDefaultOptions() {
		return new PedigreeFilterOptions(1, true);
	}

	/**
	 * Maximal allelic depth of alternative allele to tolerate in parent before
	 * flagging with ParentAd2.
	 */
	private final int deNovoMaxParentAd2;

	/**
	 * Whether or not to apply {@code OneParentGtFiltered} and {@code BothParentGtFiltered} genotype filter. 
	 */
	private final boolean applyParentGtFilteredFilters;


	public PedigreeFilterOptions(int deNovoMaxParentAd2, boolean applyParentGtFilteredFilters) {
		this.deNovoMaxParentAd2 = deNovoMaxParentAd2;
		this.applyParentGtFilteredFilters = applyParentGtFilteredFilters;
	}

	@Override
	public String toString() {
		return "PedigreeFilterOptions [deNovoMaxParentAd2=" + deNovoMaxParentAd2
				+ ", applyParentGtFilteredFilters=" + applyParentGtFilteredFilters + "]";
	}

	public int getDeNovoMaxParentAd2() {
		return deNovoMaxParentAd2;
	}

	public boolean isApplyParentGtFilteredFilters() {
		return applyParentGtFilteredFilters;
	}

}
