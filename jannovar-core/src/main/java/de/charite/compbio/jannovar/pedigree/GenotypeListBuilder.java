package de.charite.compbio.jannovar.pedigree;

import java.util.List;

import com.google.common.collect.ImmutableList;

/**
 * Builder for {@link GenotypeCalls}.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 * 
 *  @deprecated use {@link VariantContextList.Builder} instead.  
 */
@Deprecated
public class GenotypeListBuilder {
	private final String geneID;
	private boolean isXChromosomal;
	private final ImmutableList<String> names;
	private final ImmutableList.Builder<ImmutableList<Genotype>> callBuilder = new ImmutableList.Builder<ImmutableList<Genotype>>();

	public GenotypeListBuilder(String geneID, List<String> names) {
		this(geneID, names, true);
	}

	public GenotypeListBuilder(String geneID, List<String> names, boolean isXChromosomal) {
		this.geneID = geneID;
		this.names = ImmutableList.copyOf(names);
		this.isXChromosomal = isXChromosomal;
	}

	/**
	 * Set "is X chromosomal" flag of next build GenotypeCalls.
	 *
	 * @param isXChromosomal
	 *            <code>true</code> if the next built genotype list is X chromosomal
	 * @return <code>this</code> for chaining
	 */
	public GenotypeListBuilder setIsXChromosomal(boolean isXChromosomal) {
		this.isXChromosomal = isXChromosomal;
		return this;
	}

	/**
	 * Adds new list of genotypes, one for each individual.
	 *
	 * @param lst
	 *            list of {@link Genotype} objects, one for each individual
	 * @return <code>this</code> for chaining
	 */
	public GenotypeListBuilder addGenotypes(ImmutableList<Genotype> lst) {
		callBuilder.add(lst);
		return this;
	}

	/**
	 * @return new {@link GenotypeCalls}
	 */
	public GenotypeList build() {
		return new GenotypeList(geneID, names, isXChromosomal, callBuilder.build());
	}
}
