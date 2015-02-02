package de.charite.compbio.jannovar.pedigree;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.reference.GenomeInterval;

// TODO(holtgrew): Renomve genomeRegion member?

/**
 * Builder for {@link GenotypeList}.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class GenotypeListBuilder {
	private final String geneID;
	private final GenomeInterval genomeRegion;
	private final ImmutableList<String> names;
	private final ImmutableList.Builder<ImmutableList<Genotype>> callBuilder = new ImmutableList.Builder<ImmutableList<Genotype>>();

	public GenotypeListBuilder(String geneID, GenomeInterval genomeRegion, ImmutableList<String> names) {
		this.geneID = geneID;
		this.genomeRegion = genomeRegion;
		this.names = names;
	}

	/**
	 * Adds new list of genotypes, one for each individual.
	 *
	 * @param lst
	 *            list of {@link Genotype} objects, one for each individual
	 */
	public void addGenotypes(ImmutableList<Genotype> lst) {
		callBuilder.add(lst);
	}

	/**
	 * @return new {@link GenotypeList}
	 */
	public GenotypeList build() {
		return new GenotypeList(geneID, genomeRegion, names, callBuilder.build());
	}
}
