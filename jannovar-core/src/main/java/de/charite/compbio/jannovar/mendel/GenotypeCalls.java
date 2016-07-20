package de.charite.compbio.jannovar.mendel;

import java.util.Iterator;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedMap;

import de.charite.compbio.jannovar.Immutable;

/**
 * A list of genotypes (at an implicitely assumed site) in multiple individuals
 * 
 * This list contains the core information for the filtration of variants by mendelian inheritance.
 * 
 * This list is not called <code>GenotypeList</code> as "list" indicates more of a "vertical" arrangement (multiple
 * sites) of genotypes instead of a "horizontal" one (one site, multiple samples).
 * 
 * Note: of course, the class is only immutable as long as <code>payload</code> is immutable!
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
@Immutable
public final class GenotypeCalls implements Iterable<Entry<String, Genotype>> {

	/** Type of the chromosome that the variant lies on */
	private final ChromosomeType chromType;
	/** Mapping from sample name to {@link Genotype} */
	private final ImmutableSortedMap<String, Genotype> sampleToGenotype;
	/** List of sample names */
	private final ImmutableList<String> sampleNames;
	/** A payload object for later easier reidentification */
	private final Object payload;

	/**
	 * Initialize {@link GenotypeCalls} with mapping from sample to genotype
	 * 
	 * @param chromType
	 *            type of the chromosome of this genotype call site
	 * @param sampleToGenotype
	 *            {@link Iterable} with mapping from sample name to {@link Genotype}
	 */
	public GenotypeCalls(ChromosomeType chromType, Iterable<? extends Entry<String, Genotype>> sampleToGenotype) {
		this(chromType, sampleToGenotype, null);
	}

	/**
	 * Initialize {@link GenotypeCalls} with mapping from sample to genotype and an additional "payload" object
	 * 
	 * @param chromType
	 *            type of the chromosome of this genotype call site
	 * @param sampleToGenotype
	 *            {@link Iterable} with mapping from sample name to {@link Genotype}
	 * @param payload
	 *            An arbitrary payload object. This could be something to later match the constructed
	 *            <code>GenotypeCalls</code> back to an object in your application (e.g., the HTSJDK
	 *            <code>VariantContext</code> that was used for constructing the {@link GenotypeCalls}).
	 */
	public GenotypeCalls(ChromosomeType chromType, Iterable<? extends Entry<String, Genotype>> sampleToGenotype,
			Object payload) {
		this.chromType = chromType;
		this.sampleToGenotype = ImmutableSortedMap.copyOf(sampleToGenotype);
		this.sampleNames = ImmutableList.copyOf(this.sampleToGenotype.keySet());
		this.payload = payload;
	}

	/** @return number of samples in genotype list */
	public int getNSamples() {
		return sampleNames.size();
	}

	/**
	 * @param sample
	 *            name of the sample to return {@link Genotype} for
	 * @return {@link Genotype} for the given sample
	 */
	public Genotype getGenotypeForSample(String sample) {
		return sampleToGenotype.get(sample);
	}

	/**
	 * @param sampleNo
	 *            0-based sample number to return {@link Genotype} for
	 * @return {@link Genotype} by sample number
	 */
	public Genotype getGenotypeBySampleNo(int sampleNo) {
		return sampleToGenotype.get(sampleNames.get(sampleNo));
	}

	/** @return type of the chromosome */
	public ChromosomeType getChromType() {
		return chromType;
	}

	/** @return Sample to genotype map */
	public ImmutableSortedMap<String, Genotype> getSampleToGenotype() {
		return sampleToGenotype;
	}

	/** @return Sample names */
	public ImmutableList<String> getSampleNames() {
		return sampleNames;
	}

	/** @return Payload object */
	public Object getPayload() {
		return payload;
	}

	@Override
	public String toString() {
		return "GenotypeCalls [chromType=" + chromType + ", sampleToGenotype=" + sampleToGenotype + ", sampleNames="
				+ sampleNames + ", payload=" + payload + "]";
	}

	@Override
	public Iterator<Entry<String, Genotype>> iterator() {
		return sampleToGenotype.entrySet().iterator();
	}

	@Override
	public int hashCode() {
		// Yes, we really need object identity here
		return System.identityHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		// Yes, we really need object identity here
		return (this.hashCode() == obj.hashCode());
	}

}
