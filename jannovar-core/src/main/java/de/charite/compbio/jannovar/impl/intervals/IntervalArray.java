package de.charite.compbio.jannovar.impl.intervals;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import com.google.common.collect.ImmutableList;

/**
 * Sorted array of {@link Interval} objects representing an immutable interval
 * tree.
 *
 * The query results are sorted lexicographically by <code>(begin, end)</code>.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public final class IntervalArray<T> implements Serializable {

	/** version number to use when serializing */
	private static final long serialVersionUID = 1L;

	// TODO(holtgrew): store lists for left and right as well?
	/**
	 * Type for storing the query result.
	 */
	public class QueryResult {
		/** the values that overlapped with the given point or interval */
		private final ImmutableList<T> entries;
		/** the value to the left of the given point */
		private final T left;
		/** the value to the right of the given point */
		private final T right;

		QueryResult(ImmutableList<T> values, T left, T right) {
			this.entries = values;
			this.left = left;
			this.right = right;
		}

		public ImmutableList<T> getEntries() {
			return entries;
		}

		public T getLeft() {
			return left;
		}

		public T getRight() {
			return right;
		}
	}

	/**
	 * Builder for {@link QueryResult}.
	 */
	private class QueryResultBuilder {
		/** the values that overlapped with the given point or interval */
		private ImmutableList.Builder<T> values = new ImmutableList.Builder<T>();
		/** the value to the left of the given point */
		private T left = null;
		/** the value to the right of the given point */
		private T right = null;

		public QueryResult build() {
			return new QueryResult(values.build(), left, right);
		}
	}

	/** list of {@link Interval} objects, sorted by begin position */
	private final ImmutableList<Interval<T>> intervals;

	/** list of {@link Interval} objects, sorted by end position */
	private final ImmutableList<Interval<T>> intervalsEnd;

	/**
	 * Construct object with the given values.
	 */
	public IntervalArray(Collection<T> elements, IntervalEndExtractor<T> extractor) {
		IntervalListBuilder.TwoIntervalList pair = new IntervalListBuilder(elements, extractor).build();
		this.intervals = pair.intervals;
		this.intervalsEnd = pair.intervalsEnd;
	}

	/** @return {@link Interval}s, sorted by begin position */
	public ImmutableList<Interval<T>> getIntervals() {
		return intervals;
	}

	/** @return {@link Interval}s, sorted by end position */
	public ImmutableList<Interval<T>> getIntervalsEnd() {
		return intervalsEnd;
	}

	/** @return the number of elements in the tree */
	public int size() {
		return intervals.size();
	}

	/**
	 * Query the encoded interval tree for all values with intervals overlapping
	 * with a given <code>point</code>.
	 *
	 * @param point
	 *            zero-based point for the query
	 * @return the elements from the intervals overlapping with the point
	 *         <code>point</code>
	 */
	public QueryResult findOverlappingWithPoint(int point) {
		QueryResultBuilder resultBuilder = new QueryResultBuilder();
		findOverlappingWithPoint(0, intervals.size(), intervals.size() / 2, point, resultBuilder);

		// if overlapping interval was found then return this set
		QueryResult result = resultBuilder.build();
		if (result.entries.size() > 0)
			return result;

		// otherwise, find left and right neighbour
		resultBuilder.left = findLeftNeighbor(point);
		resultBuilder.right = findRightNeighbor(point);
		return resultBuilder.build();
	}

	/**
	 * @return right neighbor of the given point if any, or <code>null</code>
	 */
	private T findRightNeighbor(int point) {
		final Interval<T> query = new Interval<T>(point, point, null, point);
		int idx = Collections.binarySearch(intervals, query, new Comparator<Interval<T>>() {
			public int compare(Interval<T> o1, Interval<T> o2) {
				return (o1.getBegin() - o2.getBegin());
			}
		});

		if (idx >= 0)
			throw new RuntimeException("Found element although in right neighbor search!");
		idx = -(idx + 1); // convert to insertion point

		if (idx == intervals.size())
			return null;
		else
			return intervals.get(idx).getValue();
	}

	/**
	 * @return left neighbor of the given point if any, or <code>null</code>
	 */
	private T findLeftNeighbor(int point) {
		final Interval<T> query = new Interval<T>(point, point, null, point);
		int idx = Collections.binarySearch(intervalsEnd, query, new Comparator<Interval<T>>() {
			public int compare(Interval<T> o1, Interval<T> o2) {
				return (o1.getEnd() - o2.getEnd());
			}
		});

		if (idx >= 0)
			idx += 1;
		else
			idx = -(idx + 1); // convert to insertion point

		if (idx == 0)
			return null;
		else
			return intervalsEnd.get(idx - 1).getValue();
	}

	/**
	 * Implementation of in-order traversal of the encoded tree with pruning
	 * using {@link Interval#maxEnd}.
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
			findOverlappingWithPoint(begin, center, begin + (center - begin) / 2, point, result);

		if (node.contains(point)) // check this node
			result.values.add(node.getValue());

		if (node.isRightOf(point)) // point is left of the start of the interval, can't to the right
			return;

		if (center + 1 < end) // recurse right
			findOverlappingWithPoint(center + 1, end, (center + 1) + (end - (center + 1)) / 2, point, result);
	}

	/**
	 * Query the encoded interval tree for all values with intervals overlapping
	 * with a given <code>interval</code>.
	 *
	 * @param begin
	 *            zero-based begin position of the query interval
	 * @param end
	 *            zero-based end position of the query interval
	 * @return the elements from the intervals overlapping with the interval
	 *         <code>[begin, end)</code>
	 */
	public QueryResult findOverlappingWithInterval(int begin, int end) {
		QueryResultBuilder resultBuilder = new QueryResultBuilder();
		findOverlappingWithInterval(0, intervals.size(), intervals.size() / 2, begin, end, resultBuilder);

		// if overlapping interval was found then return this set
		QueryResult result = resultBuilder.build();
		if (result.entries.size() > 0)
			return result;

		// otherwise, find left and right neighbour, can use begin for all queries, have no overlap
		resultBuilder.left = findLeftNeighbor(begin);
		resultBuilder.right = findRightNeighbor(begin);
		return resultBuilder.build();
	}

	/**
	 * Implementation of in-order traversal of the encoded tree with pruning
	 * using {@link Interval#maxEnd}.
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
			result.values.add(node.getValue());

		if (node.isRightOf(iEnd - 1)) // last interval entry is left of the start of the interval, can't to the right
			return;

		if (center + 1 < end) // recurse right
			findOverlappingWithInterval(center + 1, end, (center + 1) + (end - (center + 1)) / 2, iBegin, iEnd, result);
	}

	/**
	 * Helper class for building the interval lists.
	 */
	private class IntervalListBuilder {

		class TwoIntervalList {
			private final ImmutableList<Interval<T>> intervals;
			private final ImmutableList<Interval<T>> intervalsEnd;

			public TwoIntervalList(ImmutableList<Interval<T>> intervals, ImmutableList<Interval<T>> intervalsEnd) {
				this.intervals = intervals;
				this.intervalsEnd = intervalsEnd;
			}
		}

		private final Collection<T> elements;
		private final IntervalEndExtractor<T> extractor;

		private ArrayList<MutableInterval<T>> tmpList = null;
		private ImmutableList<Interval<T>> intervals = null;
		private ImmutableList<Interval<T>> intervalsEnd = null;

		public IntervalListBuilder(Collection<T> elements, IntervalEndExtractor<T> extractor) {
			this.elements = elements;
			this.extractor = extractor;
		}

		public TwoIntervalList build() {
			buildIntervals();
			buildIntervalsEnd();

			return new TwoIntervalList(intervals, intervalsEnd);
		}

		/**
		 * Fill {@link #tmpList} and {@link #intervals}.
		 */
		private void buildIntervals() {
			// obtain list of elements sorted by begin positions
			tmpList = new ArrayList<MutableInterval<T>>();
			for (T element : elements)
				tmpList.add(new MutableInterval<T>(extractor.getBegin(element), extractor.getEnd(element), element,
						extractor.getEnd(element)));
			Collections.sort(tmpList);

			// compute the maxEnd members of the lst entries
			computeMaxEndProperties(tmpList, 0, tmpList.size());

			// convert the mutable intervals into immutable ones
			ImmutableList.Builder<Interval<T>> builder = new ImmutableList.Builder<Interval<T>>();
			for (MutableInterval<T> i : tmpList)
				builder.add(new Interval<T>(i));
			intervals = builder.build();
		}

		private int computeMaxEndProperties(ArrayList<MutableInterval<T>> lst, int beginIdx, int endIdx) {
			if (beginIdx == endIdx)
				return -1;

			int centerIdx = (endIdx + beginIdx) / 2;
			MutableInterval<T> mi = lst.get(centerIdx);

			if (beginIdx + 1 == endIdx)
				return mi.getMaxEnd();

			mi.setMaxEnd(Math.max(
					mi.getMaxEnd(),
					Math.max(computeMaxEndProperties(lst, beginIdx, centerIdx),
							computeMaxEndProperties(lst, centerIdx + 1, endIdx))));
			return mi.getMaxEnd();
		}

		/**
		 * Fill {@link #intervalsEnd}.
		 */
		void buildIntervalsEnd() {
			// sort by (end, begin)
			Collections.sort(tmpList, new Comparator<MutableInterval<T>>() {
				public int compare(MutableInterval<T> o1, MutableInterval<T> o2) {
					final int result = (o1.getEnd() - o2.getEnd());
					if (result == 0)
						return (o1.getBegin() - o2.getBegin());
					else
						return result;
				}
			});

			// build list of intervals
			ImmutableList.Builder<Interval<T>> builder = new ImmutableList.Builder<Interval<T>>();
			for (MutableInterval<T> i : tmpList)
				builder.add(new Interval<T>(i));
			intervalsEnd = builder.build();
		}

	}

}
