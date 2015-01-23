package de.charite.compbio.jannovar.pedigree;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.Immutable;
import de.charite.compbio.jannovar.reference.GenomeInterval;

/**
 * Wrapper for a immutable lists of {@link Genotype} calls for one {@link TranscriptInfo}, one list of calls for each
 * individual.
 *
 * This name list is used for ensuring that the same order and number of individuals is used in the genotype file as in
 * the pedigree file.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
@Immutable
public final class GenotypeList {

	/** the name of the gene for this genotype call list */
	public final String geneName;

	// TODO(holtgrem): Remove genomeRegion?
	/**
	 * The approximate genomic interval of this gene, e.g. the region of one transcript. This information is only used
	 * for getting the chromosome of the gene.
	 */
	public final GenomeInterval genomeRegion;

	/** the list of individual names */
	public final ImmutableList<String> names;

	/** the lists of genotype calls, each contains one entry for each individual */
	public final ImmutableList<ImmutableList<Genotype>> calls;

	public GenotypeList(String geneID, GenomeInterval genomeRegion, ImmutableList<String> names,
			ImmutableList<ImmutableList<Genotype>> calls) {
		this.geneName = geneID;
		this.genomeRegion = genomeRegion;
		this.names = names;
		this.calls = calls;
	}

	/**
	 * Check whether the {@link #names} of this GenotypeList are the same as the names of the members of
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
		if (pedigree.members.size() != names.size())
			return false;

		int i = 0;
		for (Person person : pedigree.members)
			if (!person.name.equals(names.get(i++)))
				return false;
		return true;
	}

}
