package jannovar.impl.intervals;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.google.common.collect.ImmutableList;

/**
 * Sorted array of {@link Interval} objects representing an immutable interval tree.
 *
 * The query results are sorted lexicographically by <code>(begin, end)</code>.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public final class IntervalArray<T extends Comparable<T>> implements Serializable {

	// TODO(holtgrew): store lists for left and right as well?
	/**
	 * Type for storing the query result.
	 */
	public class QueryResult {
		/** the values that overlapped with the given point or interval */
		public final ImmutableList<T> values;
		/** the value to the left of the given point */
		public final T left;
		/** the value to the right of the given point */
		public final T right;

		QueryResult(ImmutableList<T> values, T left, T right) {
			this.values = values;
			this.left = left;
			this.right = right;
		}
	}

	/**
	 * Builder for {@link QueryResult}.
	 */
	private class QueryResultBuilder {
		/** the values that overlapped with the given point or interval */
		public ImmutableList.Builder<T> values = new ImmutableList.Builder<T>();
		/** the value to the left of the given point */
		public T left = null;
		/** the value to the right of the given point */
		public T right = null;

		public QueryResult build() {
			return new QueryResult(values.build(), left, right);
		}
	}

	/** sorted list of {@link Interval} objects */
	public final ImmutableList<Interval<T>> intervals;

	public IntervalArray(Collection<T> elements, IntervalEndExtractor<T> extractor) {
		this.intervals = buildIntervals(elements, extractor);
	}

	/**
	 * Query the encoded interval tree for all values with intervals overlapping with a given <code>point</code>.
	 *
	 * @param point
	 *            zero-based point for the query
	 * @return the elements from the intervals overlapping with the point <code>point</code>
	 */
	public QueryResult findOverlappingWithPoint(int point) {
		QueryResultBuilder result = new QueryResultBuilder();
		findOverlappingWithPoint(0, intervals.size(), intervals.size() / 2, point, result);
		return result.build();
	}

	/**
	 * Implementation of in-order traversal of the encoded tree with pruning using {@link Interval#maxEnd}.
	 *
	 * @param begin
	 *            begin index of subtree to search through
	 * @param end
	 *            end index of subtree to search through
	 * @param center
	 *            root index of subtree to search through
	 * @param point
	 *            point to use for querying
	 * @param result
	 *            {@link QueryResultBuilder} to add values to
	 */
	private void findOverlappingWithPoint(int begin, int end, int center, int point, QueryResultBuilder result) {
		if (begin >= end) // handle base case of empty interval
			return;

		final Interval<T> node = intervals.get(center); // shortcut to current node

		if (node.allLeftOf(point)) // point is right of the rightmost point of any interval in this node
			return;

		if (begin < center) // recurse left
			findOverlappingWithPoint(begin, center, (center - begin) / 2, point, result);

		if (node.contains(point)) // check this node
			result.values.add(node.value);

		if (node.isRightOf(point)) // point is left of the start of the interval, can't to the right
			return;

		if (center + 1 < end) // recurse right
			findOverlappingWithPoint(center + 1, end, (center + 1) + (end - (center + 1)) / 2, point, result);
	}

	/**
	 * Query the encoded interval tree for all values with intervals overlapping with a given <code>interval</code>.
	 *
	 * @param begin
	 *            zero-based begin position of the query interval
	 * @param end
	 *            zero-based end position of the query interval
	 * @return the elements from the intervals overlapping with the interval <code>[begin, end)</code>
	 */
	public QueryResult findOverlappingWithInterval(int begin, int end) {
		QueryResultBuilder result = new QueryResultBuilder();
		findOverlappingWithInterval(0, intervals.size(), intervals.size() / 2, begin, end, result);
		return result.build();
	}

	/**
	 * Implementation of in-order traversal of the encoded tree with pruning using {@link Interval#maxEnd}.
	 *
	 * @param begin
	 *            begin index of subtree to search through
	 * @param end
	 *            end index of subtree to search through
	 * @param center
	 *            root index of subtree to search through
	 * @param iBegin
	 *            interval begin to use for querying
	 * @param iEnd
	 *            interval end to use for querying
	 * @param result
	 *            {@link QueryResultBuilder} to add values to
	 */
	private void findOverlappingWithInterval(int begin, int end, int center, int iBegin, int iEnd,
			QueryResultBuilder result) {
		if (begin >= end) // handle base case of empty interval
			return;

		final Interval<T> node = intervals.get(center); // shortcut to current node

		if (node.allLeftOf(iBegin)) // iBegin is right of the rightmost point of any interval in this node
			return;

		if (begin < center) // recurse left
			findOverlappingWithInterval(begin, center, begin + (center - begin) / 2, iBegin, iEnd, result);

		if (node.overlapsWith(iBegin, iEnd)) // check this node
			result.values.add(node.value);

		if (node.isRightOf(iEnd - 1)) // last interval entry is left of the start of the interval, can't to the right
			return;

		if (center + 1 < end) // recurse right
			findOverlappingWithInterval(center + 1, end, (center + 1) + (end - (center + 1)) / 2, iBegin, iEnd, result);
	}

	// TODO(holtgrew): Put construction into nested class?
	private ImmutableList<Interval<T>> buildIntervals(Collection<T> elements, IntervalEndExtractor<T> extractor) {
		// obtain list of elements sorted by begin positions
		ArrayList<MutableInterval<T>> lst = new ArrayList<MutableInterval<T>>();
		for (T element : elements)
			lst.add(new MutableInterval<T>(extractor.getBegin(element), extractor.getEnd(element), element, extractor
					.getEnd(element)));
		Collections.sort(lst);

		// compute the maxEnd members of the lst entries
		computeMaxEndProperties(lst, 0, lst.size());

		// convert the mutable intervals into immutable ones
		ImmutableList.Builder<Interval<T>> builder = new ImmutableList.Builder<Interval<T>>();
		for (MutableInterval<T> i : lst)
			builder.add(new Interval<T>(i));
		return builder.build();
	}

	// TODO(holtgrew): change from recursive to using a stack, might use less stack memory and be faster?
	private int computeMaxEndProperties(ArrayList<MutableInterval<T>> lst, int beginIdx, int endIdx) {
		if (beginIdx == endIdx)
			return -1;

		int centerIdx = (endIdx + beginIdx) / 2;
		MutableInterval<T> mi = lst.get(centerIdx);

		if (beginIdx + 1 == endIdx)
			return mi.maxEnd;

		mi.maxEnd = Math.max(
				mi.maxEnd,
				Math.max(computeMaxEndProperties(lst, beginIdx, centerIdx),
						computeMaxEndProperties(lst, centerIdx + 1, endIdx)));
		return mi.maxEnd;
	}

	/** version number to use when serializing */
	private static final long serialVersionUID = 1L;

}
