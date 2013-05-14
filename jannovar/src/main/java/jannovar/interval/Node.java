package jannovar.interval;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Implements the Node structure of the interval tree.
 * 
 * @author n1, n2, ... (eure Namen!)
 * @version 0.1 (14 May, 2013)
 */
public class Node<Type> {
	/** Median of all intervals in this node */
	public Integer median;
	/** node containing the intervals completely to the left of the median. */ 
	public Node<Type> leftNode;
	/** node containing the intervals completely to the right of the median. */
	public Node<Type> rightNode;
	/** list of all intervals that cross median of this node. */
	public List<Interval<Integer, String>> intervals;
	/**  list of intervals sorted by increasing left end points */
	public List<Interval<Integer, String>> leftorder;
	/** list sorted by decreasing right end points */
	public List<Interval<Integer, String>> rightorder;

	/**
	 * Default constructor to create an empty node.
	 */
	public Node() {
		median = 0;
		leftNode = null;
		rightNode = null;
		intervals = new ArrayList<Interval<Integer, String>>();
		leftorder = new ArrayList<Interval<Integer, String>>();
		rightorder = new ArrayList<Interval<Integer, String>>();
	}

	/**
	 * Node constructor.
	 * 
	 * @param intervals A list containing all intervals
	 */
	public Node(List<Interval<Integer, String>> intervals) {
		/* helper list to find the median of all interval endpoints */
		List<Integer> endpointslist = new ArrayList<Integer>();
		/* inserts intervals in endpointslist and sorts them */
		for (Interval<Integer, String> interval : intervals) {
			endpointslist.add(interval.getLow());
			endpointslist.add(interval.getHigh());
			Collections.sort(endpointslist);
		}
		/* calculates the median of the current node */
		median = findMedian(endpointslist);

		/* lefts stores intervals to the left of the median */
		List<Interval<Integer, String>> lefts = new ArrayList<Interval<Integer, String>>();
		/* rights stores intervals to the right of the median */
		List<Interval<Integer, String>> rights = new ArrayList<Interval<Integer, String>>();

		for (Interval<Integer, String> interval : intervals) {
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
			} else if (interval.getLow() > median) {
				rights.add(interval);
			}
		}
		/* medianIntervals stores all intervals containing the median point */
		List<Interval<Integer, String>> medianIntervals = new ArrayList<Interval<Integer, String>>();
		/* inserts intervals into medianIntervals */
		medianIntervals.addAll(intervals);
		/* removes lefts from medianIntervals */
		medianIntervals.removeAll(lefts);
		/* removes rights from medianIntervals */
		medianIntervals.removeAll(rights);

		leftorder = new ArrayList<Interval<Integer, String>>();
		rightorder = new ArrayList<Interval<Integer, String>>();

		/*
		 * comp is a comparator used to sort the low points in increasing order
		 */
		Comparator<Interval<Integer, String>> comp = new Interval.LeftComparator();
		Collections.sort(medianIntervals, comp);
		/* leftorder stores sorted medianIntervals */
		leftorder.addAll(medianIntervals);
		/*
		 * compare is a comparator used to sort the high points in decreasing
		 * order
		 */
		Comparator<Interval<Integer, String>> compare = new Interval.RightComparator();
		Collections.sort(medianIntervals, compare);
		/* rightorder stores sorted medianIntervals */
		rightorder.addAll(medianIntervals);
		/* if lefts is not empty it is stored in leftNode */
		if (lefts.size() > 0) {
			leftNode = new Node<Type>(lefts);

		}
		/* if rights is not empty it is stored in rightNode */
		if (rights.size() > 0) {
			rightNode = new Node<Type>(rights);
		}
	}

	/**
	 * Calculates the median value of the sorted list. For this purpose, the
	 * method finds the center of the list by dividing the size of the list by
	 * two and stores it in mid. If the size of the list is devisible by two,
	 * the previous element of mid is added to the median and divided by two.
	 * 
	 * @param list A list of integers
	 * @return The median of the list
	 */
	public Integer findMedian(List<Integer> list) {
		/* mid is defined as the center of the list */
		int mid = list.size() / 2;
		/* median is set to mid */
		median = list.get(mid);
		if (list.size() % 2 == 0) {
			median = (median + list.get(mid - 1)) / 2;
		}
		return median;
	}

	/**
	 * Checks if a given node is empty.
	 * 
	 * @param node A node of the interval tree
	 * @return True, if the node is empty, otherwise False
	 */
	public boolean isEmpty(Node<Type> node) {
		if (node == null)
			return true;
		else {
			return false;
		}
	}

	/**
	 * @return A left node of the interval tree
	 */
	public Node<Type> getLeft() {
		return leftNode;
	}

	/**
	 * @return A right node of the interval tree
	 */
	public Node<Type> getRight() {
		return rightNode;
	}

}
