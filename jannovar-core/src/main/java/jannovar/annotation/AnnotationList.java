package jannovar.annotation;

import jannovar.Immutable;

import java.util.Collection;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedMultiset;

/**
 * A list of priority-sorted {@link Annotation} objects.
 *
 * @see AllAnnotationListTextGenerator
 * @see BestAnnotationListTextGenerator
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
@Immutable
public final class AnnotationList {

	/** the list of the annotations */
	public final ImmutableList<Annotation> entries;

	/**
	 * Construct ImmutableAnnotationList from a {@link Collection} of {@link Annotation} objects.
	 *
	 * @param entries
	 *            {@link Collection} of {@link Annotation} objects
	 */
	public AnnotationList(Collection<Annotation> entries) {
		this.entries = ImmutableList.copyOf(ImmutableSortedMultiset.copyOf(entries));
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

}
