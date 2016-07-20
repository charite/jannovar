package de.charite.compbio.jannovar.pedigree;

import java.util.List;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.Immutable;
import de.charite.compbio.jannovar.reference.TranscriptModel;

/**
 * Wrapper for a immutable lists of {@link Genotype} calls for one {@link TranscriptModel}, one list of calls for
 * each individual.
 *
 * This name list is used for ensuring that the same order and number of individuals is used in the genotype file as in
 * the pedigree file.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 * 
 * @deprecated Use {@link VariantContextList} instead
 */
@Immutable
@Deprecated
public final class GenotypeList {

	/** the name of the gene for this genotype call list */
	private final String geneName;

	/** the list of individual names */
	private final ImmutableList<String> names;

	/** whether or not the variants are on the X chromsome */
	private final boolean isXChromosomal;

	/** the lists of genotype calls, each contains one entry for each individual */
	private final ImmutableList<ImmutableList<Genotype>> calls;

	/**
	 * Construct and initialize object.
	 *
	 * @param geneID
	 *            name of gene that this genotype list is for
	 * @param names
	 *            individual names, gives sorting of individuals in the call lists
	 * @param isXChromosomal
	 *            <code>true</code> if the gene list is on the X chromosome, only affects X-linked compatibility checks
	 *            (setting this to <code>true</code> can lead to too false positives and but neither <code>true</code>
	 *            nor <code>false</code> can lead to false negatives, given the filter's properties)
	 * @param calls
	 *            the genotype calls for this list
	 */
	public GenotypeList(String geneID, List<String> names, boolean isXChromosomal,
			ImmutableList<ImmutableList<Genotype>> calls) {
		this.geneName = geneID;
		this.names = ImmutableList.copyOf(names);
		this.isXChromosomal = isXChromosomal;
		this.calls = calls;
	}

	/** the name of the gene for this genotype call list */
	public String getGeneName() {
		return geneName;
	}

	/** the list of individual names */
	public ImmutableList<String> getNames() {
		return names;
	}

	/** whether or not the variants are on the X chromsome */
	public boolean isXChromosomal() {
		return isXChromosomal;
	};

	/** the lists of genotype calls, each contains one entry for each individual */
	public ImmutableList<ImmutableList<Genotype>> getCalls() {
		return calls;
	}

	/**
	 * Check whether the {@link #names} of this GenotypeCalls are the same as the names of the members of
	 * <code>pedigree</code>.
	 *
	 * For this, the order of the names has to be the same as the number of the names. This check is important for the
	 * {@link PedigreeDiseaseCompatibilityDecorator}, where the names in the pedigree must be the same as the names in
	 * the genotype list.
	 *
	 * @return <code>true</code> if the list of {@link #names} is the same as the names of the members of
	 *         <code>pedigree</code>
	 */
	public boolean namesEqual(Pedigree pedigree) {
		return (pedigree.getNames().equals(names));
	}

	@Override
	public String toString() {
		return "GenotypeCalls(" + calls + ")";
	}

}
