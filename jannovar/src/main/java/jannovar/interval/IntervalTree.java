package jannovar.interval;

import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;


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
 * Note that a Node object may have non-null right and left children while the
 * Node itself has not associated intervals, depending on the relationship of the
 * median value to the values of the intervals.
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
 * @author Christopher Dommaschenz, Radostina Misirkova, Nadine Taube, Gizem Top, Peter Robinson
 * @version 0.08 (25 May, 2013)
 */
public class IntervalTree<T> implements java.io.Serializable {
    /** The root node of the entire interval tree. */
    private Node<T> root;
    /** All intervals of the entire tree (pointers to these intervals are
     * also used in the nodes).
     */
    private List<Interval<T>> intervals;
    
    /** A Comparator that is used to sort intervals by their left endpoint
     * in ascending order. */
    private Comparator<Interval<T>> leftcomp = null;
    /** A Comparator that is used to sort intervals by their right endpoint
     * in descending order. */
    private Comparator<Interval<T>> rightcomp = null;
    /** The left neighbor of the current query position (non-overlapping interval to
	the left of the query that is the closest of all intervals). */
    private Interval<T> leftNeighbor = null;
    /** The right neighbor of the current query position (non-overlapping interval to
	the right of the query that is the closest of all intervals). */
    private Interval<T> rightNeighbor = null;
    
    
    
    /** The default constructor should not be used and is declared private. */
    private IntervalTree() {
    }

    /**
     * Tree constructor.
     * 
     * @param intervals A list that contains the intervals
     */
    public IntervalTree(List<Interval<T>> intervals) {
	/* sets the root and calls the node constructor with list */
	initializeComparators();
	this.root = new Node<T>(intervals);
	this.intervals = intervals;
    }
    
    /**
     * A helper method intended to be used by the Constructors of this
     * class to initialize the static Comparator objects that will be
     * used to sort intervals.
     */
    private void initializeComparators() {
	if (this.leftcomp == null){
	    this.leftcomp = new LeftComparator();
	}
	if (this.rightcomp == null) {
	    this.rightcomp = new RightComparator();
	}
	Node.setLeftComparator(leftcomp);
	Node.setRightComparator(rightcomp);
    }
    
    /**
     * Search function which calls the method searchInterval to find intervals.
     * <P>
     * As a side-effect of the search, the variables {@link #rightNeighbor} and
     * {@link #leftNeighbor} are set. If this method returns an empty list, then
     * these variables contain the intervals that are the closest neighbors to 
     * the left and the right to the query position.  
     * @param low The lower element of the interval
     * @param high The higher element of the interval
     * @return result, which is an ArrayList containg the found intervals
     */
    public ArrayList<T> search(int low, int high) {
	ArrayList<Interval<T>> result = new ArrayList<Interval<T>>();
	/* reset */
	this.leftNeighbor = null;
	this.rightNeighbor = null;
//	System.out.println("Search for (" + low + "," + high + ")");
	//debugPrint();
	searchInterval(root, result, low, high);
	ArrayList<T> obtlst = new ArrayList<T>();
	for (Interval<T> it : result) {
	    obtlst.add(it.getValue());
	}
	return obtlst;
    }
    
    
    
    /**
     * This function is intended to be called after a call to 
     * {@link #search} reveals an emptylist, i.e., if none of the
     * items overlaps with the search coordinates. In the example of
     * gene annotations, this would be the case for intergenic variants or
     * for variants that are upstream or downstream to a gene (within 1000 nt).
     * <P>
     * The search strategy basically implies that we will go to a
     * {@link jannovar.interval.Node Node} that is a leaf in the tree. When we
     * get to this Node, again there is no interval that overlaps with the 
     * search coordinates. This is a bit tricky and will require a little
     * thought...
     * @param low Leftwards end of search interval
     * @param high rightwards end of search interval
     */
    public ArrayList<T> getNeighboringItems(int low, int high) {
	return null;
    }


    public T getRightNeighbor() {
	return rightNeighbor == null ? null : this.rightNeighbor.getValue();
    }


    public T getLeftNeighbor() {
	return leftNeighbor==null ? null : this.leftNeighbor.getValue();
    }

    /**
     * Searches for intervals in the interval tree.
     * 
     * @param n A node of the interval tree
     * @param result An ArrayList containg the found intervals
     * @param ilow The lower element of the search interval
     * @param ihigh The higher element of the search interval
     */
    private void searchInterval(Node<T> n, ArrayList<Interval<T>> result, int ilow, int ihigh) {
	/* ends if the node n is empty */
	if (n == null) {
	    return;
	}
	/*
	 * if ilow is smaller than the median of n the left side of the tree is
	 * searched
	 */
	if (ilow < n.getMedian()) {
	    /* as long as the iterator i is smaller than the size of leftorder */
	    int size = n.leftorder.size();
	    //System.out.println(String.format("ilow=%d < median=%d, leftorder size=%d",ilow,n.getMedian(),n.leftorder.size()));
	    for (int i = 0; i < size; i++) {
		/*
		 * breaks if the lowpoint at position i is bigger than the
		 * wanted high point
		 */
		if (n.leftorder.get(i).getLow() > ihigh) {
		    //System.out.println(String.format("break because low-interval [%d] > ihigh [%d] with median=%d", 
		    //				     n.leftorder.get(i).getLow(),ihigh,n.getMedian()));
		    break;
		}
		/* adds the interval at position i of leftorder to result */
		//System.out.println(String.format("ilow < median: adding interval: " + n.leftorder.get(i)));
		result.add(n.leftorder.get(i));
	    }
	    /*
	     * if ihigh is bigger than the median of n the right side of the
	     * tree is searched
	     */
	} else if (ihigh > n.getMedian()) {
	    /* as long as the iterator i is smaller than the size of rightorder */
	    int size = n.rightorder.size();
	    //System.out.println(String.format("ihigh=%d > median=%d, rightorder size=%d",ihigh,n.getMedian(),n.rightorder.size()));
	    for (int i = 0; i < size; i++) {
		/*
		 * breaks if the highpoint at position i is smaller than the
		 * wanted low point
		 */
		if (n.rightorder.get(i).getHigh() < ilow) {
		    //System.out.println(String.format("break because high-interval [%d] < ilow [%d] ", n.leftorder.get(i).getHigh(),ilow));
		    break;
		}
		/* adds the interval at position i of rightorder to result */
		//System.out.println(String.format("ihigh [%d] > median [%d]: adding interval: %s",
		//				 ihigh,n.getMedian(),n.rightorder.get(i)));
		result.add(n.rightorder.get(i));
	    }
	}
	/** The following two lines set the left and right neighbor. This
	    will only be useful if we do not interact with an interval. */
	if (ihigh < n.getMedian() && n.hasInterval() ) {
	    Interval<T> ivl = n.getLeftmostInterval();
	    if (this.rightNeighbor == null)
		this.rightNeighbor = ivl;
	    else if (ivl.getLow() < this.rightNeighbor.getLow())
		this.rightNeighbor = ivl;
//	    System.out.println("rightniehgbor = " + this.rightNeighbor);
	}

	if (ilow > n.getMedian() && n.hasInterval()) {
	    Interval<T> ivl  =  n.getRightmostInterval();
	    if (this.leftNeighbor == null)
		this.leftNeighbor = ivl;
	    else if (ivl.getHigh() > this.leftNeighbor.getHigh())
		this.leftNeighbor = ivl;
//	     System.out.println("leftniehgbor = " + this.leftNeighbor);
	}
	    /*
	if (ilow > n.getMedian() ) {
	     Interval<T> neighbor = n.getRightmostInterval();
	     if (leftNeighbor == null) {
		 leftNeighbor = neighbor;
	     } else if (neighbor != null && neighbor.getHigh()>this.leftNeighbor.getHigh()) {
		 this.leftNeighbor =  neighbor;
	     }
	     }*/
	
	/*
	 * if the query is to the left of the median and the
	 * leftNode is not empty the searchInterval method is called
	 * recursively
	 */
	if ( ilow < n.getMedian() && n.getLeft() != null ) {
	    searchInterval(n.getLeft(), result, ilow, ihigh);
	    
	}
	/*
	 * if thequery is to the right of the median and the
	 * rightNode is not empty the searchInterval method is called
	 * recursively
	 */
	if (ihigh > n.getMedian() && n.getRight() != null) {
	    searchInterval(n.getRight(), result, ilow, ihigh);
	}
	return;
    }
    
    /**
     * Adds a new interval to the intervals list, which contains all intervals.
     * 
     * @param newinterval A new interval that is inserted into intervals
     
     public void addInterval(Interval<T> newinterval) {
     intervals.add(newinterval);
     update();
     } */
    
    /**
     * Updates the list containing all intervals, for example after adding a new
     * interval.
     
     public void update() {
     this.root = new Node<T>(intervals);
     } */


    /**
     * This is intended to be used to print out the interval tree
     * for debugging purposes.
     */
    public void debugPrint() {
	System.out.println("IntervalTree<T>");
	root.debugPrint(null,0);
	System.out.println("LeftNeighbor:" + leftNeighbor);
	System.out.println("RightNeighbor:" + rightNeighbor);
    }
    
}
