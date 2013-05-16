package jannovar.interval;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements an Interval Tree.
 * <P>
 * The construction of the interval tree proceeds in several phases.
 * <ol>
 * <li>Phase 1
 * <li>Phase 2
 * <li>...
 * </ol>
 * To search in the interval tree, the following procedure is used (...)
 * @author names
 * @version 0.02 (15 May, 2013)
 */
public class IntervalTree<T> {
	public Node<T> root;
	public List<Interval<T>> intervals;

	/**
	 * Default constructor.
	 */
	public IntervalTree() {
		/* sets the root of the tree */
		this.root = new Node<T>();
		/* sets the intervals list which is of the type ArrayList */
		this.intervals = new ArrayList<Interval<T>>();

	}

	/**
	 * Tree constructor.
	 * 
	 * @param intervals A list that contains the intervals
	 */
	public IntervalTree(List<Interval<T>> intervals) {
		/* sets the root and calls the node constructor with list */
		this.root = new Node<T>(intervals);
		this.intervals = intervals;
	}

	/**
	 * Search function which calls the method searchInterval to find intervals.
	 * 
	 * @param low The lower element of the interval
	 * @param high The higher element of the interval
	 * @return result, which is an ArrayList containg the found intervals
	 */
	public ArrayList<Interval<T>> search(int low, int high) {
		ArrayList<Interval<T>> result = new ArrayList<Interval<T>>();
		searchInterval(root, result, low, high);
		return result;
	}

	/**
	 * Searches for intervals in the interval tree.
	 * 
	 * @param n A node of the interval tree
	 * @param result An ArrayList containg the found intervals
	 * @param ilow The lower element of the interval
	 * @param ihigh The higher element of the interval
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
