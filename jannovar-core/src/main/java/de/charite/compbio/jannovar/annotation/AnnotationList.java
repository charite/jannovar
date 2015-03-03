package de.charite.compbio.jannovar.annotation;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedMultiset;

import de.charite.compbio.jannovar.Immutable;
import de.charite.compbio.jannovar.reference.GenomeChange;

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

	/** the {@link GenomeChange} that this <code>AnnotationList</code> contains entries for. */
	private final GenomeChange change;

	/** the list of the annotations */
	private final ImmutableList<Annotation> entries;

	/**
	 * @param change
	 *            to use for the empty list
	 * @return empty <code>AnnotationList</code> with the given {@link GenomeChange}
	 */
	public static AnnotationList buildEmptyList(GenomeChange change) {
		return new AnnotationList(change, ImmutableList.<Annotation> of());
	}

	/**
	 * Construct ImmutableAnnotationList from a {@link Collection} of {@link Annotation} objects.
	 *
	 * @param change
	 *            {@link GenomeChange} that this anotation list annotates
	 * @param entries
	 *            {@link Collection} of {@link Annotation} objects
	 */
	public AnnotationList(GenomeChange change, Collection<Annotation> entries) {
		this.change = change;
		this.entries = ImmutableList.copyOf(ImmutableSortedMultiset.copyOf(entries));
	}

	/**
	 * @return {@link GenomeChange} that this <code>AnnotationList</code> contains entries for.
	 */
	public GenomeChange getChange() {
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
		if (anno.effects.isEmpty())
			return VariantEffect.SEQUENCE_VARIANT;
		else
			return anno.effects.first();
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

	@Override
	public int size() {
		return entries.size();
	}

	@Override
	public boolean isEmpty() {
		return entries.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return entries.contains(o);
	}

	@Override
	public Iterator<Annotation> iterator() {
		return entries.iterator();
	}

	@Override
	public Object[] toArray() {
		return entries.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return entries.toArray(a);
	}

	@Deprecated
	@Override
	public boolean add(Annotation e) {
		return entries.add(e);
	}

	@Deprecated
	@Override
	public boolean remove(Object o) {
		return entries.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return entries.containsAll(c);
	}

	@Deprecated
	@Override
	public boolean addAll(Collection<? extends Annotation> c) {
		return entries.addAll(c);
	}

	@Deprecated
	@Override
	public boolean addAll(int index, Collection<? extends Annotation> c) {
		return entries.addAll(index, c);
	}

	@Deprecated
	@Override
	public boolean removeAll(Collection<?> c) {
		return entries.removeAll(c);
	}

	@Deprecated
	@Override
	public boolean retainAll(Collection<?> c) {
		return entries.retainAll(c);
	}

	@Deprecated
	@Override
	public void clear() {
		entries.clear();
	}

	@Override
	public Annotation get(int index) {
		return entries.get(index);
	}

	@Deprecated
	@Override
	public Annotation set(int index, Annotation element) {
		return entries.set(index, element);
	}

	@Deprecated
	@Override
	public void add(int index, Annotation element) {
		entries.add(index, element);
	}

	@Deprecated
	@Override
	public Annotation remove(int index) {
		return entries.remove(index);
	}

	@Override
	public int indexOf(Object o) {
		return entries.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return entries.lastIndexOf(o);
	}

	@Override
	public ListIterator<Annotation> listIterator() {
		return entries.listIterator();
	}

	@Override
	public ListIterator<Annotation> listIterator(int index) {
		return entries.listIterator(index);
	}

	@Override
	public AnnotationList subList(int fromIndex, int toIndex) {
		return new AnnotationList(change, entries.subList(fromIndex, toIndex));
	}

}
