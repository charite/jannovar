package de.charite.compbio.jannovar.filter.facade;

/**
 * Configuration for {@link PedigreeFilterAnnotator}.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class PedigreeFilterOptions {

	public static PedigreeFilterOptions buildDefaultOptions() {
		return new PedigreeFilterOptions(1, true, true);
	}

	/**
	 * Maximal allelic depth of alternative allele to tolerate in parent before flagging with
	 * ParentAd2.
	 */
	private final int deNovoMaxParentAd2;

	/**
	 * Whether or not to apply {@code OneParentGtFiltered} and {@code BothParentGtFiltered} genotype
	 * filter.
	 */
	private final boolean applyParentGtFilteredFilters;

	/**
	 * Whether or not to apply the {@code OneParentGtFiltered} to the global {@code FILTER} column.
	 */
	private final boolean oneParentGtFilteredFiltersAffected;

	public PedigreeFilterOptions(int deNovoMaxParentAd2, boolean applyParentGtFilteredFilters,
								 boolean oneParentGtFilteredFiltersAffected) {
		this.deNovoMaxParentAd2 = deNovoMaxParentAd2;
		this.applyParentGtFilteredFilters = applyParentGtFilteredFilters;
		this.oneParentGtFilteredFiltersAffected = oneParentGtFilteredFiltersAffected;
	}

	@Override
	public String toString() {
		return "PedigreeFilterOptions [deNovoMaxParentAd2=" + deNovoMaxParentAd2
			+ ", applyParentGtFilteredFilters=" + applyParentGtFilteredFilters
			+ ", oneParentGtFilteredFiltersAffected=" + oneParentGtFilteredFiltersAffected
			+ "]";
	}

	public int getDeNovoMaxParentAd2() {
		return deNovoMaxParentAd2;
	}

	public boolean isApplyParentGtFilteredFilters() {
		return applyParentGtFilteredFilters;
	}

	public boolean isOneParentGtFilteredFiltersAffected() {
		return oneParentGtFilteredFiltersAffected;
	}

}
