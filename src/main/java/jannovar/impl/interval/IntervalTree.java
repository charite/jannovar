package jannovar.impl.interval;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implements an Interval Tree.
 * <P>
 * The construction of the interval tree proceeds in several phases.
 * <ol>
 * <li>The 2n endpoints are sorted for the n intervals and the median point of the endpoints is calculated.
 * <li>The intervals are divided into three categories.
 * <ol>
 * <li>Intervals that cross the median are stored in the intervals list.
 * <li>Intervals completely to the left of the median are stored in the leftNode list.
 * <li>Intervals completely to the right are stored in the rightNode list.
 * </ol>
 * <li>Each interval in the intervals list is sorted in two lists: leftorder (sorted by increasing left endpoints) and
 * rightorder (sorted by decreasing right endpoints).
 * </ol>
 * Note that a Node object may have non-null right and left children while the Node itself has not associated intervals,
 * depending on the relationship of the median value to the values of the intervals.
 * <P>
 * To search in the interval tree, the following procedure is used.
 * <ol>
 * <li>If the highpoint of the interval is smaller than the median, the right subtree is eliminated. Otherwise, the
 * search method is called recursively.
 * <li>If the lowpoint of the interval is larger than the median, the left subtree is eliminated. Otherwise, the search
 * method is called recursively.
 * <li>Always searches the intervals stored at current node using the two sorted lists leftorder and rightorder.
 * <ol>
 * <li>If the highpoint is smaller than the median, search the leftorder list and output all intervals until there is
 * one with a left endpoint larger than the lowpoint.
 * <li>If the lowpoint is larger than the median, search the rightorder list and output all intervals until there is one
 * with a right endpoint smaller than the highpoint.
 * </ol>
 * </ol>
 * <P>
 * A slight modification of the search algorithm is made to search for the left and right neighbors of search queries
 * that do not overlap with any intervals. We make essentially a binary-tree type search using the medians of the nodes.
 * Each time we pass a node, we check whether we can update the neihgbors. One issue is that we have to make special
 * compensation when we update using a node with no intervals on its own.
 * <P>
 * The construction of an Interval Tree enables a fast search of overlapping intervals.
 *
 * Although public, this class is not meant to be part of the public Jannovar intervace. It can be changed or removed at
 * any point.
 *
 * @author Christopher Dommaschenz
 * @author Radostina Misirkova
 * @author Nadine Taube
 * @author Gizem Top
 * @author Peter Robinson
 * @author Manuel Holtgrewe
 * @version 0.12 (13 June, 2013)
 *
 * @param <T>
 *            The contained type to attach to the intervals.
 */
public final class IntervalTree<T> implements java.io.Serializable {
	/**
	 * Encapsulates the algorithm state such that the current query position is not
	 *
	 * In cases where we do not find an intersection, i.e., when a call to {@link #search} reveals an empty list because
	 * none of the items overlaps with the search coordinates, then the search function automatically calls
	 * {@link #searchInbetween}, which sets the variables {@link #rightNeighbor} and {@link #leftNeighbor}.
	 */
	private class AlgorithmState {
		/** A buffer for collecting intervals during the query. */
		public ArrayList<Interval<T>> result = new ArrayList<Interval<T>>();

		/**
		 * The left neighbor of the current query position (non-overlapping interval to the left of the query that is
		 * the closest of all intervals).
		 */
		private Interval<T> leftNeighbor = null;

		/**
		 * The right neighbor of the current query position (non-overlapping interval to the right of the query that is
		 * the closest of all intervals).
		 */
		private Interval<T> rightNeighbor = null;

		/**
		 * This function checks if {@code item}, which is passed to the function as an argument, is a better left
		 * neighbor for position x than the current value of {@link #leftNeighbor}. It first checks if the currentValue
		 * of leftNeighbor is null, in which case we simply assign a value. Because of the way we traverse the interval
		 * tree, this value is guaranteed to be valid. We then check whether the item is actually located to the left of
		 * x (if not, we just return). Finally, we check if the "high" (rightmost) value of the interval represented by
		 * item is closer to x than the value of {@link #leftNeighbor}. If this is the case, then item is a better left
		 * neighbor, and we assign it to the class variable {@link #leftNeighbor}.
		 *
		 * Note that this function is intended to be used by the function {@link #searchInbetween} if there is no
		 * interval that overlaps the original search query.
		 *
		 * @param item
		 *            The new candidate left neighbor
		 * @param x
		 *            the search position
		 */
		private void switchLeftNeighborIfBetter(Interval<T> item, int x) {
			if (item == null)
				return;
			// System.out.println("TOP: switchLefttNeighborIfBetter item=" + item +
			// " current leftN=" + leftNeighbor);
			if (this.leftNeighbor == null) {
				this.leftNeighbor = item;
				return;
			}
			/* 1) Return if item is not to left of x */
			if (item.getHigh() >= x)
				return;
			/* 2) if item is closer to x than current leftNeighbor, replace it */
			if (item.getHigh() > this.leftNeighbor.getHigh()) {
				this.leftNeighbor = item;
			}
		}

		/**
		 * This function checks if {@code item}, which is passed to the function as an argument, is a better right
		 * neighbor for position x than the current value of {@link #rightNeighbor}. If the current value of
		 * {@link #rightNeighbor} is null, we assign it the value of {@code item} (this is guaranteed to be a valid
		 * value). Otherwise, we check whether item is to the right of {@link #rightNeighbor} (if not, just return).
		 * Finally, we check whether item is closer to {@code x} than the current rightneighbor, and if so, we update
		 * the value of {@link #rightNeighbor} to {@code item}.#
		 *
		 * @param item
		 *            The new candidate rightt neighbor
		 * @param x
		 *            the search position
		 */
		private void switchRightNeighborIfBetter(Interval<T> item, int x) {
			if (item == null)
				return;
			// System.out.println("TOP: switchRightNeighborIfBetter item=" + item +
			// " current rightN=" + rightNeighbor);
			if (this.rightNeighbor == null) {
				this.rightNeighbor = item;
				return;
			}
			/* 1) Return if item is not to right of x */
			if (item.getLow() <= x)
				return;
			/* 2) if item is closer to x than current leftNeighbor, replace it */
			if (item.getLow() < this.rightNeighbor.getLow()) {
				this.rightNeighbor = item;
			}
		}
	}

	/**
	 * Immutable result of an interval tree query.
	 */
	public class QueryResult {
		/** The resulting element list. */
		public final ArrayList<T> result;
		/** Left neighboring interval. */
		public final Interval<T> leftNeighbor;
		/** Right neighboring interval. */
		public final Interval<T> rightNeighbor;

		public QueryResult(ArrayList<T> result, Interval<T> leftNeighbor, Interval<T> rightNeighbor) {
			this.result = result;
			this.leftNeighbor = leftNeighbor;
			this.rightNeighbor = rightNeighbor;
		}

		/** @return contained value of left neighbor */
		public T getLeftNeighbor() {
			if (leftNeighbor == null)
				return null;
			else
				return leftNeighbor.getValue();
		}

		/** @return contained value of left neighbor */
		public T getRightNeighbor() {
			if (rightNeighbor == null)
				return null;
			else
				return rightNeighbor.getValue();
		}
	}

	/**
	 * A version number used during deserialization to verify that the sender and receiver of a serialized object have
	 * loaded classes for that object that are compatible with respect to serialization.
	 */
	private static final long serialVersionUID = 1L;

	/** The root node of the entire interval tree. */
	private final Node<T> root;

	/**
	 * All intervals of the entire tree (pointers to these intervals are also used in the nodes).
	 */
	private final List<Interval<T>> intervals;

	/**
	 * A Comparator that is used to sort intervals by their left endpoint in ascending order.
	 */
	private final LeftComparator leftcomp = new LeftComparator();

	/**
	 * A Comparator that is used to sort intervals by their right endpoint in descending order.
	 */
	private final RightComparator rightcomp = new RightComparator();

	/**
	 * @return number of intervals in the tree
	 */
	public int size() {
		return this.intervals.size();
	}

	/**
	 * Tree constructor.
	 *
	 * @param intervals
	 *            A list that contains the intervals
	 */
	public IntervalTree(List<Interval<T>> intervals) {
		/* sets the root and calls the node constructor with list */
		this.root = new Node<T>(intervals, this.leftcomp, this.rightcomp);
		this.intervals = intervals;
		Collections.sort(this.intervals, leftcomp);
	}

	// TODO(holtgrem): What's the difference to search()?
	/**
	 * Search function which looks up Intervals overlapping the search region [low,high].
	 *
	 * This function should only be used for rare big (1000+) regions, since this is a really simple approach.
	 *
	 * TODO implement using a faster more fancy algorithm.
	 *
	 * @param low
	 *            The lower element of the interval
	 * @param high
	 *            The higher element of the interval
	 * @return an {@link ArrayList} containing all overlapping intervals
	 */
	public ArrayList<T> searchBigInterval(int low, int high) {
		ArrayList<T> obtlst = new ArrayList<T>();
		for (Interval<T> interval : this.intervals) {
			if (high < interval.getLow()) // break if the end of the searchregion is smaller than the intervalstart
				break;
			if (low > interval.getHigh())
				continue;
			obtlst.add(interval.getValue());
		}
		return obtlst;
	}

	/**
	 * Search function which calls the method searchInterval to find intervals.
	 *
	 * As a side-effect of the search, the variables {@link #rightNeighbor} and {@link #leftNeighbor} are set. If this
	 * method returns an empty list, then these variables contain the intervals that are the closest neighbors to the
	 * left and the right to the query position.
	 *
	 * @param low
	 *            The lower element of the interval
	 * @param high
	 *            The higher element of the interval
	 * @return result, which is an ArrayList containing the found intervals
	 */
	public QueryResult search(int low, int high) {
		AlgorithmState state = new AlgorithmState();
		// System.out.println("Search for (" + low + "," + high + ")");
		// debugPrint(this.root);
		searchInterval(root, state, low, high);
		ArrayList<T> obtlst = new ArrayList<T>();
		for (Interval<T> it : state.result) {
			obtlst.add(it.getValue());
		}
		/* Search for neighbors if there are no hits */
		if (obtlst.isEmpty())
			searchInBetween(state, low);
		return new QueryResult(obtlst, state.leftNeighbor, state.rightNeighbor);
	}

	/**
	 * This function is called if no interval is found to overlap with the search query. If this method is called, we
	 * know that we are "in between" two intervals (or at the extreme right or left end of the search space).
	 *
	 * @param state
	 *            The AlgorithmState to use for keeping the algorithm's state.
	 * @param x
	 *            The lower range of the original search query (it doesn't matter whether we take the lower or the upper
	 *            range, since both do not overlap).
	 */
	private void searchInBetween(AlgorithmState state, int x) {
		Node<T> n = this.root;
		while (n != null) {
			if (x < n.getMedian()) {
				/*
				 * First up date the right neighbor (most left to date that is right of query) .
				 */
				if (n.hasInterval()) {
					Interval<T> item = n.getLeftmostItem();
					state.switchRightNeighborIfBetter(item, x);
				} else {
					Node<T> rd = n.getLeftmostDescendentOfRightChild();
					Interval<T> item = rd.getLeftmostItem();
					state.switchRightNeighborIfBetter(item, x);
				}
				/* Now continue to navigate the interval tree */
				n = n.getLeft();
			} else {
				/*
				 * First up date the left neighbor (most right to date that is left of query) .
				 */
				if (n.hasInterval()) {
					Interval<T> item = n.getRightmostItem();
					state.switchLeftNeighborIfBetter(item, x);
				} else {
					/*
					 * If the current node has no intervals on its own, then it is possible that its right child has a
					 * better right neighbor than the current right neighbor.
					 */
					Node<T> rd = n.getRightmostDescendentOfLeftChild();
					Interval<T> item = rd.getRightmostItem();
					state.switchLeftNeighborIfBetter(item, x);
				}
				/* Now continue to navigate the interval tree */
				n = n.getRight();
			}
		}
	}

	/**
	 * Searches for intervals in the interval tree.
	 *
	 * @param n
	 *            A node of the interval tree
	 * @param state
	 *            An AlgorithmState with the mutable algorithm state for a query.
	 * @param ilow
	 *            The lower element of the search interval
	 * @param ihigh
	 *            The higher element of the search interval
	 */
	private void searchInterval(Node<T> n, AlgorithmState state, int ilow, int ihigh) {
		/* ends if the node n is empty */
		if (n == null) {
			return;
		}
		/*
		 * if ilow is smaller than the median of n the left side of the tree is searched
		 */
		if (ilow <= n.getMedian()) {
			/* as long as the iterator i is smaller than the size of leftorder */
			int size = n.leftorder.size();
			for (int i = 0; i < size; i++) {
				/*
				 * breaks if the lowpoint at position i is bigger than the wanted high point
				 */
				if (n.leftorder.get(i).getLow() > ihigh) {
					break;
				}
				/* adds the interval at position i of leftorder to result */
				state.result.add(n.leftorder.get(i));
			}
			/*
			 * if ihigh is bigger than the median of n the right side of the tree is searched
			 */
		} else if (ihigh > n.getMedian()) {
			/* as long as the iterator i is smaller than the size of rightorder */
			int size = n.rightorder.size();
			for (int i = 0; i < size; i++) {
				/*
				 * breaks if the highpoint at position i is smaller than the wanted low point
				 */
				if (n.rightorder.get(i).getHigh() < ilow) {
					break;
				}
				/* adds the interval at position i of rightorder to result */
				state.result.add(n.rightorder.get(i));
			}
		}

		/*
		 * if the query is to the left of the median and the leftNode is not empty the searchInterval method is called
		 * recursively
		 */
		if (ilow < n.getMedian() && n.getLeft() != null) {
			searchInterval(n.getLeft(), state, ilow, ihigh);

		}
		/*
		 * if the query is to the right of the median and the rightNode is not empty the searchInterval method is called
		 * recursively
		 */
		if (ihigh > n.getMedian() && n.getRight() != null) {
			searchInterval(n.getRight(), state, ilow, ihigh);
		}
	}

	/**
	 * This is intended to be used to print out the interval tree for debugging purposes.
	 *
	 * @param n
	 *            current {@link Node}
	 */
	public void debugPrint(Node<T> n, AlgorithmState state) {
		System.out.println("IntervalTree<T> starting at  " + n.toString());
		n.debugPrint(null, 0);
		System.out.println("LeftNeighbor:" + state.leftNeighbor.getValue());
		System.out.println("RightNeighbor:" + state.rightNeighbor.getValue());
	}

	public void debugPrint(AlgorithmState state) {
		debugPrint(this.root, state);
	}
}
