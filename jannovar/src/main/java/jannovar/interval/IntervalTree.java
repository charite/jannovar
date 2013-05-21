package jannovar.interval;

import java.util.ArrayList;
import java.util.List;

import jannovar.interval.LeftComparator;

/**
 * Implements an Interval Tree.
* <P>
 * The construction of the interval tree proceeds in several phases.
 * <ol>
 * <li>The 2n endpoints are sorted for the n intervals and the median point of
 * the endpoints is calculated.
 * <li>The intervals are divided into three categories.
 * <ol>
 * <li>Intervals that cross the median are stored in the intervals list.
 * <li>Intervals completely to the left of the median are stored in the leftNode
 * list.
 * <li>Intervals completely to the right are stored in the rightNode list.
 * </ol>
 * <li>Each interval in the intervals list is sorted in two lists: leftorder
 * (sorted by increasing left endpoints) and rightorder (sorted by decreasing
 * right endpoints).
 * </ol>
 * <P>
 * To search in the interval tree, the following procedure is used.
 * <ol>
 * <li>If the highpoint of the interval is smaller than the median, the right
 * subtree is eliminated. Otherwise, the search method is called recursively.
 * <li>If the lowpoint of the interval is larger than the median, the left
 * subtree is eliminated. Otherwise, the search method is called recursively.
 * <li>Always searches the intervals stored at current node using the two sorted
 * lists leftorder and rightorder.
 * <ol>
 * <li>If the highpoint is smaller than the median, search the leftorder list
 * and output all intervals until there is one with a left endpoint larger than
 * the lowpoint.
 * <li>If the lowpoint is larger than the median, search the rightorder list and
 * output all intervals until there is one with a right endpoint smaller than
 * the highpoint.
 * </ol>
 * </ol>
 * <P>
 * The construction of an Interval Tree enables a fast search of overlapping
 * intervals.
 * 
 * @author Christopher Dommaschenz, Radostina Misirkova, Nadine Taube, Gizem Top
 * @version 0.02 (15 May, 2013)
 */
public class IntervalTree<T> implements java.io.Serializable {
	/** The root node of the entire interval tree. */
	public Node<T> root;
	/** All intervals of the entire tree (pointers to these intervals are
	 * also used in the nodes).
	 */
	public List<Interval<T>> intervals;
	
	/** A Comparator that is used to sort intervals by their left endpoint
	 * in ascending order. */
	public static LeftComparator leftcomp = null;
	/** A Comparator that is used to sort intervals by their right endpoint
	 * in descending order. */
	public static RightComparator rightcomp = null;

	/**
	 * Default constructor.

	public IntervalTree() {
	    /* sets the root of the tree 
	    this.root = new Node<T>();
		/* sets the intervals list which is of the type ArrayList 
		this.intervals = new ArrayList<Interval<T>>();
		initializeComparators();
	}	 */

	/**
	 * Tree constructor.
	 * 
	 * @param intervals A list that contains the intervals
	 */
	public IntervalTree(List<Interval<T>> intervals) {
		/* sets the root and calls the node constructor with list */
		this.root = new Node<T>(intervals);
		this.intervals = intervals;
		initializeComparators();
	}
	
	/**
	 * A helper method intended to be used by the Constructors of this
	 * class to initialize the static Comparator objects that will be
	 * used to sort intervals.
	 */
	private void initializeComparators() {
		if (IntervalTree.leftcomp == null){
			IntervalTree.leftcomp = new LeftComparator();
		}
		if (IntervalTree.rightcomp == null) {
			IntervalTree.rightcomp = new RightComparator();
		}
	}

	/**
	 * Search function which calls the method searchInterval to find intervals.
	 * 
	 * @param low The lower element of the interval
	 * @param high The higher element of the interval
	 * @return result, which is an ArrayList containg the found intervals
	 */
	public ArrayList<T> search(int low, int high) {
	    ArrayList<Interval<T>> result = new ArrayList<Interval<T>>();
	    searchInterval(root, result, low, high);
	    ArrayList<T> obtlst = new ArrayList<T>();
	    for (Interval<T> it : result) {
		obtlst.add(it.getValue());
	    }
	    return obtlst;
	}

	/**
	 * Searches for intervals in the interval tree.
	 * 
	 * @param n A node of the interval tree
	 * @param result An ArrayList containg the found intervals
	 * @param ilow The lower element of the search interval
	 * @param ihigh The higher element of the search interval
	 */
	public void searchInterval(Node<T> n, ArrayList<Interval<T>> result, int ilow, int ihigh) {
		/* ends if the node n is empty */
		if (n == null) {
			return;
		}
		/*
		 * if ilow is smaller than the median of n the left side of the tree is
		 * searched
		 */
		if (ilow < n.median) {
			/* as long as the iterator i is smaller than the size of leftorder */
			int size = n.leftorder.size();

			for (int i = 0; i < size; i++) {
				/*
				 * breaks if the lowpoint at position i is bigger than the
				 * wanted high point
				 */
				if (n.leftorder.get(i).getLow() > ihigh) {
					break;
				}
				/* adds the interval at position i of leftorder to result */
				result.add(n.leftorder.get(i));
			}
			/*
			 * if ihigh is bigger than the median of n the right side of the
			 * tree is searched
			 */
		} else if (ihigh > n.median) {
			/* as long as the iterator i is smaller than the size of rightorder */
			int size = n.rightorder.size();
			for (int i = 0; i < size; i++) {
				/*
				 * breaks if the highpoint at position i is smaller than the
				 * wanted low point
				 */
				if (n.rightorder.get(i).getHigh() < ilow) {
					break;
				}
				/* adds the interval at position i of rightorder to result */
				result.add(n.rightorder.get(i));
			}
		}
		/*
		 * if leftNode is not empty the searchInterval method is called
		 * recursively
		 */
		if (n.leftNode != null) {
			searchInterval(n.leftNode, result, ilow, ihigh);
		}
		/*
		 * if rightNode is not empty the searchInterval method is called
		 * recursively
		 */
		if (n.rightNode != null) {
			searchInterval(n.rightNode, result, ilow, ihigh);
		}
		return;
	}

	/**
	 * Adds a new interval to the intervals list, which contains all intervals.
	 * 
	 * @param newinterval A new interval that is inserted into intervals
	 */
	public void addInterval(Interval<T> newinterval) {
		intervals.add(newinterval);
		update();
	}

	/**
	 * Updates the list containing all intervals, for example after adding a new
	 * interval.
	 */
	public void update() {
		this.root = new Node<T>(intervals);
	}

}
