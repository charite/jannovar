package de.charite.compbio.jannovar.annotation;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMultiset;
import de.charite.compbio.jannovar.Immutable;
import de.charite.compbio.jannovar.reference.SVDescription;
import de.charite.compbio.jannovar.reference.SVGenomeVariant;
import de.charite.compbio.jannovar.reference.Strand;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * A list of priority-sorted {@link SVAnnotation} objects.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
@Immutable
public final class SVAnnotations implements SVDescription {

	/**
	 * the {@link SVGenomeVariant} that this <code>AnnotationList</code> contains entries for.
	 */
	private final SVGenomeVariant change;

	/**
	 * the list of the annotations
	 */
	private final ImmutableList<SVAnnotation> entries;

	/**
	 * @param change to use for the empty list
	 * @return empty <code>AnnotationList</code> with the given {@link SVGenomeVariant}
	 */
	public static SVAnnotations buildEmptyList(SVGenomeVariant change) {
		return new SVAnnotations(change, ImmutableList.of());
	}

	/**
	 * Construct ImmutableAnnotationList from a {@link Collection} of {@link SVAnnotation} objects.
	 * <p>
	 * Note that <code>variant</code> is converted to the forward strand using {@link SVGenomeVariant#withStrand}.
	 *
	 * @param variant {@link SVGenomeVariant} that this anotation list annotates
	 * @param entries {@link Collection} of {@link SVAnnotation} objects
	 */
	public SVAnnotations(SVGenomeVariant variant, Collection<SVAnnotation> entries) {
		this.change = variant.withStrand(Strand.FWD);
		this.entries = ImmutableList.copyOf(ImmutableSortedMultiset.copyOf(entries));
	}

	/**
	 * Return the {@link SVGenomeVariant} that this AnnotationList is annotated with.
	 * <p>
	 * Note that the {@link SVGenomeVariant} is converted to be on the forward strand on construction of AnnotationList
	 * objects.
	 *
	 * @return {@link SVGenomeVariant} that this <code>AnnotationList</code> contains entries for.
	 */
	public SVGenomeVariant getGenomeVariant() {
		return change;
	}

	/**
	 * @return the list of annotations
	 */
	public ImmutableList<SVAnnotation> getAnnotations() {
		return entries;
	}

	/**
	 * @return <code>true</code> if the result of {@link #getAnnotations} is empty
	 */
	public boolean hasAnnotation() {
		return !entries.isEmpty();
	}

	/**
	 * Return the highest impact annotation for each gene.
	 *
	 * @return {@link ImmutableMap} from gene ID to highest-impact {@link SVAnnotation} of all transcripts, empty list
	 * if there is none.
	 */
	public ImmutableMap<String, SVAnnotation> getHighestImpactAnnotation() {
		if (!hasAnnotation()) {
			return ImmutableMap.of();
		} else {
			final Map<String, SVAnnotation> map = new HashMap<>();
			for (SVAnnotation svAnno : entries) {
				final String geneID = svAnno.getTranscript().getGeneID();
				final SVAnnotation other = map.get(geneID);
				if (other == null) {
					map.put(geneID, svAnno);
				} else if (svAnno.getMostPathogenicVariantEffect().compareTo(
					other.getMostPathogenicVariantEffect()) > 0) {
					map.replace(geneID, other, svAnno);
				}
			}
			return ImmutableMap.copyOf(map);
		}
	}

	/**
	 * Convenience method.
	 *
	 * @return {@link VariantEffect} with the highest impact of all in entries or {@link VariantEffect#SEQUENCE_VARIANT}
	 * if entries are empty or contain no annotated effects.
	 */
	public VariantEffect getHighestImpactEffect() {
		final ImmutableMap<String, SVAnnotation> anno = getHighestImpactAnnotation();
		if (anno == null || anno.isEmpty() || anno.entrySet().iterator().next().getValue().getEffects().isEmpty()) {
			return VariantEffect.SEQUENCE_VARIANT;
		} else {
			VariantEffect result = null;
			for (SVAnnotation svAnno: anno.values()) {
				final VariantEffect candidate = svAnno.getMostPathogenicVariantEffect();
				if (candidate.compareTo(result) < 0) {
					result = candidate;
				}
			}
			return result;
		}
	}

	@Override
	public String toString() {
		return "AnnotationList(change=" + change + ", entries=[" + entries + "])";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((entries == null) ? 0 : entries.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SVAnnotations other = (SVAnnotations) obj;
		if (entries == null) {
			if (other.entries != null)
				return false;
		} else if (!entries.equals(other.entries))
			return false;
		return true;
	}

	@Override
	public String getChrName() {
		return change.getChrName();
	}

	@Override
	public int getChr() {
		return change.getChr();
	}

	@Override
	public int getPos() {
		return change.getPos();
	}

	@Override
	public String getChr2Name() {
		return change.getChr2Name();
	}

	@Override
	public int getChr2() {
		return change.getChr2();
	}

	@Override
	public int getPos2() {
		return change.getPos2();
	}

	@Override
	public Type getType() {
		return change.getType();
	}

}
