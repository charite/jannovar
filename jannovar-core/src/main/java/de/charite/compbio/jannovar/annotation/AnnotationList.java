package de.charite.compbio.jannovar.annotation;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedMultiset;

import de.charite.compbio.jannovar.Immutable;
import de.charite.compbio.jannovar.reference.GenomeVariant;
import de.charite.compbio.jannovar.reference.Strand;

/**
 * A list of priority-sorted {@link Annotation} objects.
 *
 * @see AllAnnotationListTextGenerator
 * @see BestAnnotationListTextGenerator
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
@Immutable
public final class AnnotationList implements List<Annotation> {

	/** the {@link GenomeVariant} that this <code>AnnotationList</code> contains entries for. */
	private final GenomeVariant change;

	/** the list of the annotations */
	private final ImmutableList<Annotation> entries;

	/**
	 * @param change
	 *            to use for the empty list
	 * @return empty <code>AnnotationList</code> with the given {@link GenomeVariant}
	 */
	public static AnnotationList buildEmptyList(GenomeVariant change) {
		return new AnnotationList(change, ImmutableList.<Annotation> of());
	}

	/**
	 * Construct ImmutableAnnotationList from a {@link Collection} of {@link Annotation} objects.
	 *
	 * Note that <code>variant</code> is converted to the forward strand using {@link GenomeVariant#withStrand}.
	 *
	 * @param change
	 *            {@link GenomeVariant} that this anotation list annotates
	 * @param entries
	 *            {@link Collection} of {@link Annotation} objects
	 */
	public AnnotationList(GenomeVariant variant, Collection<Annotation> entries) {
		this.change = variant.withStrand(Strand.FWD);
		this.entries = ImmutableList.copyOf(ImmutableSortedMultiset.copyOf(entries));
	}

	/**
	 * Return the {@link GenomeVariant} that this AnnotationList is annotated with.
	 *
	 * Note that the {@link GenomeVariant} is converted to be on the forward strand on construction of AnnotationList
	 * objects.
	 *
	 * @return {@link GenomeVariant} that this <code>AnnotationList</code> contains entries for.
	 */
	public GenomeVariant getChange() {
		return change;
	}

	/**
	 * @return {@link Annotation} with highest predicted impact, or <code>null</code> if there is none.
	 */
	public Annotation getHighestImpactAnnotation() {
		if (entries.isEmpty())
			return null;
		else
			return entries.get(0);
	}

	/**
	 * Convenience method.
	 *
	 * @return {@link VariantEffect} with the highest impact of all in {@link #entries} or
	 *         {@link VariantEffect.SEQUENCE_VARIANT} if {@link #entries} is empty or has no annotated effects.
	 */
	public VariantEffect getHighestImpactEffect() {
		final Annotation anno = getHighestImpactAnnotation();
		if (anno.getEffects().isEmpty())
			return VariantEffect.SEQUENCE_VARIANT;
		else
			return anno.getEffects().first();
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
		AnnotationList other = (AnnotationList) obj;
		if (entries == null) {
			if (other.entries != null)
				return false;
		} else if (!entries.equals(other.entries))
			return false;
		return true;
	}

	public int size() {
		return entries.size();
	}

	public boolean isEmpty() {
		return entries.isEmpty();
	}

	public boolean contains(Object o) {
		return entries.contains(o);
	}

	public Iterator<Annotation> iterator() {
		return entries.iterator();
	}

	public Object[] toArray() {
		return entries.toArray();
	}

	public <T> T[] toArray(T[] a) {
		return entries.toArray(a);
	}

	@Deprecated
	public boolean add(Annotation e) {
		return entries.add(e);
	}

	@Deprecated
	public boolean remove(Object o) {
		return entries.remove(o);
	}

	public boolean containsAll(Collection<?> c) {
		return entries.containsAll(c);
	}

	@Deprecated
	public boolean addAll(Collection<? extends Annotation> c) {
		return entries.addAll(c);
	}

	@Deprecated
	public boolean addAll(int index, Collection<? extends Annotation> c) {
		return entries.addAll(index, c);
	}

	@Deprecated
	public boolean removeAll(Collection<?> c) {
		return entries.removeAll(c);
	}

	@Deprecated
	public boolean retainAll(Collection<?> c) {
		return entries.retainAll(c);
	}

	@Deprecated
	public void clear() {
		entries.clear();
	}

	public Annotation get(int index) {
		return entries.get(index);
	}

	@Deprecated
	public Annotation set(int index, Annotation element) {
		return entries.set(index, element);
	}

	@Deprecated
	public void add(int index, Annotation element) {
		entries.add(index, element);
	}

	@Deprecated
	public Annotation remove(int index) {
		return entries.remove(index);
	}

	public int indexOf(Object o) {
		return entries.indexOf(o);
	}

	public int lastIndexOf(Object o) {
		return entries.lastIndexOf(o);
	}

	public ListIterator<Annotation> listIterator() {
		return entries.listIterator();
	}

	public ListIterator<Annotation> listIterator(int index) {
		return entries.listIterator(index);
	}

	public AnnotationList subList(int fromIndex, int toIndex) {
		return new AnnotationList(change, entries.subList(fromIndex, toIndex));
	}

}
