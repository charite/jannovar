package de.charite.compbio.jannovar.filter.facade;

/**
 * Configuration for {@link PedigreeFilterAnnotator}.
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class PedigreeFilterOptions {

	/**
	 * Maximal allelic depth of alternative allele to tolerate in parent before
	 * flagging with ParentAd2.
	 */
	private final int deNovoMaxParentAd2;

	public PedigreeFilterOptions(int deNovoMaxParentAd2) {
		this.deNovoMaxParentAd2 = deNovoMaxParentAd2;
	}

	public int getDeNovoMaxParentAd2() {
		return deNovoMaxParentAd2;
	}

	@Override
	public String toString() {
		return "PedigreeFilterOptions [deNovoMaxParentAd2=" + deNovoMaxParentAd2 + "]";
	}

	public static PedigreeFilterOptions buildDefaultOptions() {
		return new PedigreeFilterOptions(1);
	}

}
