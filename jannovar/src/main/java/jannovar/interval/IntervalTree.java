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
 * @version 0.11 (7 June, 2013)
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
	//System.out.println("Search for (" + low + "," + high + ")");
	//debugPrint(this.root);
	searchInterval(root, result, low, high);
	ArrayList<T> obtlst = new ArrayList<T>();
	for (Interval<T> it : result) {
	    obtlst.add(it.getValue());
	}
	/* Search for neighbors if there are no hits */
	if (obtlst.isEmpty())
	    searchInbetween(root,low);
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

    /**
     * In cases where we do not find an intersection, i.e., when  a call to 
     * {@link #search} reveals an emptylist because none of the
     * items overlaps with the search coordinates, then the search function
     * automatically calls 
     * {@link #searchInbetween}, which sets the variables 
     * {@link #rightNeighbor} and 
     * {@link #lefttNeighbor}, which can then be called by this function 
     * and {@link #getLeftNeighbor}.
     * @return the right neighbor of the current search query, if no overlapping interval was found.
     */
    public T getRightNeighbor() {
	return rightNeighbor.getValue();
    }

     /**
     * In cases where we do not find an intersection, i.e., when  a call to 
     * {@link #search} reveals an emptylist because none of the
     * items overlaps with the search coordinates, then the search function
     * automatically calls 
     * {@link #searchInbetween}, which sets the variables 
     * {@link #rightNeighbor} and 
     * {@link #lefttNeighbor}, which can then be called by this function 
     * and {@link #getRighttNeighbor}.
     * @return the left neighbor of the current search query, if no overlapping interval was found.
     */
    public T getLeftNeighbor() {
	return leftNeighbor.getValue();
    }

    /**
     * This method get either the leftmost item contained
     * in a Node or any of its descendents. Remembering that if a
     * Node contains zero intervals, then it must have descendents with intervals,
     * we first check if the node has an interval. If not, we take the leftmost interval
     * of any of the descendets of the node, i.e., we keep going down the to the left
     * child node until we get to a leaf, and return the leftmost item
     * of that node. 
     * <P>
     * As a special case, we note that the node may be null if we are at the very end of a 
     * Chromosome. In this case, we return null.
     * @param n The node for which we want to get the leftmost interval of the node or
     * any of its descendents.
     * @return The leftmost interval.
     */
    private Interval<T> getLeftmost(Node<T> n) {
	if (n==null) return null;
	if (n.hasInterval()) {
	    return  n.getLeftmostItem();
	    
	} else {
	    Node<T> current = n;
	    while (n != null) {
		current = n;
		n = n.getLeft();
	    }
	    return  current.getLeftmostItem();
	}
    }

    /**
     * This method get either the rightmost item contained
     * in a Node or any of its descendents. Remembering that if a
     * Node contains zero intervals, then it must have descendents with intervals,
     * we first check if the node has an interval. If not, we take the righttmost interval
     * of any of the descendets of the node, i.e., we keep going down the to the right
     * child node until we get to a leaf, and return the rightmost item
     * of that node. 
     * <P>
     * As a special case, we note that the node may be null if we are at the very end of a 
     * Chromosome. In this case, we return null.
     * @param n The node for which we want to get the rightmost interval of the node or
     * any of its descendents.
     * @return The rightmost interval.
     */
    private Interval<T> getRightmost(Node<T> n) {
	if (n == null) return null;
	if (n.hasInterval()) {
	    return  n.getRightmostItem();
	} else {
	    Node<T> current = n;
	    while (n != null) {
		current = n;
		n = n.getRight();
	    }
	    return  current.getRightmostItem();
	}
    }

    private void searchInbetween(Node<T> n, int x) {
	Node<T> current = null;
	Node<T> left = null;
	Node<T> right = null;
	int leftmostToDate = Integer.MAX_VALUE;
	int rightmostToDate = Integer.MIN_VALUE;
	Interval<T> lN = null;
	Interval<T> rN = null;
	while (n != null) {
	    current = n;
	    if (x < n.getMedian() ) {
		/* First up date the right neighbor (most left to date that is right of query) .*/
		if (n.hasInterval()) {
		    Interval<T> item = n.getLeftmostItem();
		    if (rN == null) {
			rN = item;
		    } else if (rN.getLow() > item.getLow()) {
			rN = item;
		    }
		}
		/* Now continue to navigate the interval tree */
		right = n;
		n = n.getLeft();
	    } else {
		if (n.hasInterval()) {
		    /* First up date the left neighbor (most right to date that is left of query) .*/
		    if (n.hasInterval()) {
			Interval<T> item = n.getRightmostItem();  
			if (lN == null) {
			    lN = item;
			} else if (lN.getHigh() < item.getHigh()){
			    lN = item;
			}
		    }
		}
		/* Now continue to navigate the interval tree */
		left = n;
		n = n.getRight();
	    }
	}
	System.out.println("Done with first loop. Left  = " + left + " and right = " + right);
	System.out.println(String.format("x=%d and x-left = %d",x, x - lN.getHigh()));
	System.out.println("Done with first loop. lN  = " + lN + " and rN = " + rN);
	/* if there is no Node at all in the tree, then current will be null
	   and the neighbors will also be null. Actually should never happen. */
	if (current==null) return;
	/* When we get here, current will be a non-null leaf Node. If
	   x < current.getMedian(), then current has an interval containing
	   the right neighbor of X, and if x > current.getMedian() and we can obtain
	   the left neighbor of x via this.leftNeighbor, then current
	   has an interval containing the left neighbor of x.
	*/
	if (x < current.getMedian()) {
	     System.out.println("x < current.getMedian()");
	    this.rightNeighbor = current.getLeftmostItem();
	    System.out.println("rightNeighbior = " + this.rightNeighbor);
	    Interval<T> leftCandidate = getRightmost(left);
	    System.out.println("leftCan = " + leftCandidate);
	    if (leftCandidate.getHigh() < x && leftCandidate.getHigh() > lN.getHigh()) {
		this.leftNeighbor = leftCandidate; System.out.println("leftCandidate wins");
	    } else {
		this.leftNeighbor = lN;  System.out.println("lN=" + lN + " wins");
	    }
	} else {
	    System.out.println("ELSE current median=" + current.getMedian());
	  
	    this.leftNeighbor = current.getRightmostItem();
	    System.out.println("leftNeighbior = " + this.leftNeighbor);
	    Interval<T> rightCandidate = getLeftmost(right);
	     System.out.println("rightCan = " + rightCandidate);
	    if (rightCandidate.getLow() > x && rightCandidate.getLow() < rN.getLow())
		this.rightNeighbor = rightCandidate;
	    else
		this.rightNeighbor = rN;
	}
    }


    Interval<T> getBestRightNeighbor( Interval<T> a, Interval<T> b, int x) {
	if (a.getLow() <= x && b.getLow() <= x) return null;
	else if (a.getLow() <= x) return b;
	else if (b.getLow() <= x) return a;
	else if (a.getLow() < b.getLow()) return a;
	else return b;
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

	/*
	 * if the query is to the left of the median and the
	 * leftNode is not empty the searchInterval method is called
	 * recursively
	 */
	if ( ilow < n.getMedian() && n.getLeft() != null ) {
	    searchInterval(n.getLeft(), result, ilow, ihigh);
	    
	}
	/*
	 * if the query is to the right of the median and the
	 * rightNode is not empty the searchInterval method is called
	 * recursively
	 */
	if (ihigh > n.getMedian() && n.getRight() != null) {
	    searchInterval(n.getRight(), result, ilow, ihigh);
	}
	return;
    }
    
    /**
     * This is intended to be used to print out the interval tree
     * for debugging purposes.
     */
    public void debugPrint(Node<T> n) {
	System.out.println("IntervalTree<T> starting at  " + n.toString());
	n.debugPrint(null,0);
	System.out.println("LeftNeighbor:" + leftNeighbor);
	System.out.println("RightNeighbor:" + rightNeighbor);
    }
    
}
