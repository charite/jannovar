package jannovar.interval;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Implements the Node structure of the interval tree. The class is intended to
 * store {@link jannovar.interval.Interval Interval} objects. Each node stores
 * all Intervals that intersect with the {@link #median}. The {@link #leftNode}
 * stores all Intervals that are completely to the left of {@link #median}, and
 * the {@link #rightNode} stores all intervals that are completely to the right.
 * Additionally there are two lists that store orders lists of the Intevals that
 * are stored within the present node. {@link #leftorder} stores the intervals
 * sorted by increased left end points (lowpoint), and {@link #rightorder}
 * stores the same intervals sorted by their right end points (highpoint).
 * 
 * @param <T>
 * @see jannovar.interval.IntervalTree
 * @author Christopher Dommaschenz, Radostina Misirkova, Nadine Taube, Gizem
 *         Top, Peter Robinson
 * @version 0.08 (9 January, 2014)
 */
public class Node<T> {
	/** Median of all intervals in this node */
	private int median;
	/** node containing the intervals completely to the left of the median. */
	private Node<T> leftNode;
	/** node containing the intervals completely to the right of the median. */
	private Node<T> rightNode;
	/** list of all intervals that cross median of this node. */
	// private List<Interval<T>> intervals;
	/** list of intervals sorted by increasing left end points */
	public List<Interval<T>> leftorder;
	/** list sorted by decreasing right end points */
	public List<Interval<T>> rightorder;
	/**
	 * A Comparator that is used to sort intervals by their left endpoint in
	 * ascending order.
	 */
	@SuppressWarnings("rawtypes")
	private Comparator<? extends Interval> leftcomp = null;
	/**
	 * A Comparator that is used to sort intervals by their right endpoint in
	 * descending order.
	 */
	@SuppressWarnings("rawtypes")
	private Comparator<? extends Interval> rightcomp = null;

	/**
	 * Default constructor is declared private to avoid it from being used..
	 */
	private Node() {
	}

	/**
	 * Node constructor.
	 * 
	 * @param intervals
	 *            A list containing all intervals
	 */
	// @SuppressWarnings("unchecked")
	public Node(List<Interval<T>> intervals, LeftComparator leftcomp2, RightComparator rightcomp2) {
		/* helper list to find the median of all interval endpoints */
		List<Integer> endpointslist = new ArrayList<Integer>();
		/* inserts intervals in endpointslist and sorts them */
		for (Interval<T> interval : intervals) {
			endpointslist.add(interval.getLow());
			endpointslist.add(interval.getHigh());
			// Collections.sort(endpointslist,Node.leftcomp); /* dont actually
			// need to sort! */
		}
		/* calculates the median of the current node */
		median = calculateMedian(endpointslist);

		/* lefts stores intervals to the left of the median */
		List<Interval<T>> lefts = new ArrayList<Interval<T>>();
		/* rights stores intervals to the right of the median */
		List<Interval<T>> rights = new ArrayList<Interval<T>>();
		/* medianIntervals stores all intervals containing the median point */
		List<Interval<T>> medianIntervals = new ArrayList<Interval<T>>();

		for (Interval<T> interval : intervals) {
			/*
			 * if the highpoint is smaller than the median, insert interval into
			 * lefts
			 */
			if (interval.getHigh() < median) {
				lefts.add(interval);
				/*
				 * if the lowpoint is bigger than the median, insert interval
				 * into rights
				 */
			}
			else if (interval.getLow() > median) {
				rights.add(interval);
			}
			else {
				medianIntervals.add(interval);
			}
		}

		// set the comparators
		this.leftcomp = leftcomp2;
		this.rightcomp = rightcomp2;

		leftorder = new ArrayList<Interval<T>>();
		rightorder = new ArrayList<Interval<T>>();

		Collections.sort(medianIntervals, leftcomp2);
		leftorder.addAll(medianIntervals);

		Collections.sort(medianIntervals, rightcomp2);
		rightorder.addAll(medianIntervals);

		if (lefts.size() > 0) {
			leftNode = new Node<T>(lefts, leftcomp2, rightcomp2);
		}
		if (rights.size() > 0) {
			rightNode = new Node<T>(rights, leftcomp2, rightcomp2);
		}
	}

	// /**
	// * A static method intended to be used by the IntervalTree
	// * constructor to set the left-comparator once for all
	// * node objects.
	// * @param lcmp left-comparator
	// */
	// @SuppressWarnings("rawtypes")
	// public static void setLeftComparator(Comparator lcmp) { //
	// Node.leftcomp = lcmp;
	// }

	// /**
	// * A static method intended to be used by the IntervalTree
	// * constructor to set the right-comparator once for all
	// * node objects.
	// * @param rcmp right-comparator
	// */
	// @SuppressWarnings("rawtypes")
	// public static void setRightComparator(Comparator rcmp) { //
	// Node.rightcomp = rcmp;
	// }

	/**
	 * Calculates the median value of the sorted list. For this purpose, the
	 * method finds the center of the list by dividing the size of the list by
	 * two and stores it in mid. If the size of the list is divisible by two,
	 * the previous element of mid is added to the median and divided by two.
	 * <P>
	 * Note that because list of Integers represent each of the endpoints of the
	 * intervals that cross the midline, they need to be sorted before we can
	 * calculate the median.
	 * 
	 * @param list
	 *            A list of integers
	 * @return The median of the list
	 */
	private Integer calculateMedian(List<Integer> list) {
		Collections.sort(list);
		int mid = list.size() / 2;
		int mymedian = list.get(mid);
		if (list.size() % 2 == 0) {
			mymedian = (mymedian + list.get(mid - 1)) / 2;
		}
		return mymedian;
	}

	/**
	 * @return the median value of all endpoints of all intervals that represent
	 *         this node and all of its children nodes.
	 */
	public int getMedian() {
		return this.median;
	}

	/**
	 * This function is intended to be used to find Intervals that surround
	 * queries that do not actually intersect with any of the intervals in the
	 * tree. For any search query at a given node, either the search query
	 * overlaps with one of the intervals, or it is the right of all of the
	 * intervals, or it is to the left of all of the intervals.
	 * <P>
	 * If the query is to the left of all of the intervals, then its right
	 * neighbor is the interval whose low point is the most to the left. This is
	 * the interval that is returned by this function (it is the first element
	 * of {@link #leftorder}).
	 * 
	 * @return the left most Interval
	 */
	public Interval<T> getLeftmostItem() {
		return leftorder.isEmpty() ? null : leftorder.get(0);
	}

	/**
	 * This function is intended to be used to find Intervals that surround
	 * queries that do not actually intersect with any of the intervals in the
	 * tree. For any search query at a given node, either the search query
	 * overlaps with one of the intervals, or it is the right of all of the
	 * intervals, or it is to the left of all of the intervals.
	 * <P>
	 * If the query is to the left of all of the intervals, then its left
	 * neighbor is the interval whose highpoint is the most to the right. This
	 * is the interval that is returned by this function (it is the first
	 * element of {@link #rightorder}).
	 * 
	 * @return the left most Interval
	 */
	public Interval<T> getRightmostItem() {
		return rightorder.isEmpty() ? null : rightorder.get(0);
	}

	/**
	 * Checks if this node is empty.
	 * 
	 * @return True, if the node is empty, otherwise False
	 */
	public boolean isEmpty() {
		return rightorder.isEmpty();
	}

	/**
	 * @return true if this Node has at least one interval stored within this
	 *         node.
	 */
	public boolean hasInterval() {
		return rightorder.size() > 0;
	}

	public Node<T> getRightmostDescendentOfLeftChild() {
		Node<T> n = this.getLeft();
		if (n == null)
			return null;
		while (n.getRight() != null)
			n = n.getRight();
		return n;
	}

	public Node<T> getLeftmostDescendentOfRightChild() {
		Node<T> n = this.getRight();
		if (n == null)
			return null;
		while (n.getLeft() != null)
			n = n.getLeft();
		return n;
	}

	/**
	 * @return A left node of the interval tree
	 */
	public Node<T> getLeft() {
		return leftNode;
	}

	/**
	 * @return A right node of the interval tree
	 */
	public Node<T> getRight() {
		return rightNode;
	}

	/**
	 * This is intended to be used to print out the interval tree for debugging
	 * purposes.
	 * 
	 * @param type
	 * @param level
	 */
	public void debugPrint(String type, int level) {

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < level; ++i)
			sb.append("-");
		sb.append("(").append(level).append(")");
		String indent = new String(sb);
		System.out.print(indent);
		if (type != null)
			System.out.println(String.format("%s,Node:  %d intervals crossing median [%d]:", type, this.leftorder.size(), median));
		else
			System.out.println(String.format("Node:  %d intervals crossing median [%d]:", this.leftorder.size(), median));
		for (Interval<T> i : leftorder) {
			System.out.println(indent + "##(leftorder):" + i);
		}
		for (Interval<T> i : rightorder) {
			System.out.println(indent + "##(rightorder):" + i);
		}
		System.out.println();
		Node<T> l = getLeft();
		if (l == null) {
			System.out.print(indent);
			System.out.println("-:Left=null");
		}
		else {
			l.debugPrint("L", level + 1);
		}
		Node<T> r = getRight();
		if (r == null) {
			System.out.print(indent);
			System.out.println("-:Right=null");
		}
		else {
			r.debugPrint("R", level + 1);
		}
	}

	@Override
	public String toString() {
		return String.format("[Node]: median: %d, number of items: %d", this.median, this.leftorder.size());
	}

}
