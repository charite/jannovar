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
 * A slight modification of the search algorithm is made to search for the left and
 * right neighbors of search queries that do not overlap with any intervals. We make essentially
 * a binary-tree type search using the medians of the nodes. Each time we pass a node, we check
 * whether we can update the neihgbors. One issue is that we have to make special compensation when
 * we update using a node with no intervals on its own.
 * <P>
 * The construction of an Interval Tree enables a fast search of overlapping
 * intervals.
 * 
 * @author Christopher Dommaschenz, Radostina Misirkova, Nadine Taube, Gizem Top, Peter Robinson
 * @version 0.12 (13 June, 2013)
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
	    searchInbetween(low);
	return obtlst;
    }


    private void searchOnEmpty(int low, int high) {
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
	if (rightNeighbor == null)
	    return null;
	else
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
	if (leftNeighbor == null)
	    return null;
	else
	    return leftNeighbor.getValue();
    }

    /**
     * This function checks if {@code item}, which is passed to the function as an argument,
     * is a better left neighbor for position x than the current value of 
     * {@link #leftNeighbor}. It first checks if the currentValue of leftNeighbor is null,
     * in which case we simply assign a value. Because of the way we traverse the interval tree, this
     * value is guaranteed to be valid. We then check whether the item is actually located to the
     * left of x (if not, we just return). Finally, we check if the "high" (rightmost) value of the 
     * interval represented by item is closer to x than the value of {@link #leftNeighbor}. If this
     * is the case, then item is a better left neighbor, and we assign it to the class variable
     * {@link #leftNeighbor}.
     * <P>
     * Note that this function is intended to be used by the function 
     * {@link #searchInbetween} if there is no interval that overlaps the original search query.
     * @param item The new candidate left neighbor
     * @param x the search position 
     */
    private void switchLeftNeighborIfBetter(Interval<T> item, int x) {
	if (item == null) return;
	//System.out.println("TOP: switchLefttNeighborIfBetter item=" + item + " current leftN=" + leftNeighbor);
	if (this.leftNeighbor == null) {
		this.leftNeighbor = item;
		return;
	}
	/* 1) Return if item is not to left of x */
	if (item.getHigh() >= x)
		return;
	/* 2) if item is closer to x than current leftNeighbor, replace it */
	if (item.getHigh() > this.leftNeighbor.getHigh()){
		this.leftNeighbor = item;
	}
	//System.out.println("BOTTOM=> switchLefttNeighborIfBetter item=" + item + " current leftN=" + leftNeighbor);
    }
    
    /**
     * TODO: see switchLeftNeighborIfBetter for explanation (Improve me!).
     */
    private void switchRightNeighborIfBetter(Interval<T> item, int x){
	if (item == null) return;
	//System.out.println("TOP: switchRightNeighborIfBetter item=" + item + " current rightN=" + rightNeighbor);
	if (this.rightNeighbor == null) {
		this.rightNeighbor = item;
		return;
	}
	/* 1) Return if item is not to right of x */
	if (item.getLow() <= x)
		return;
	/* 2) if item is closer to x than current leftNeighbor, replace it */
	if (item.getLow() < this.rightNeighbor.getLow()){
		this.rightNeighbor = item;
	}
	//System.out.println("BOTTOM=> switchRightNeighborIfBetter item=" + item + " current rightN=" + rightNeighbor);
    }

    /**
     * This function is called if no inverval is found to overlap with the search query.
     * @param x The lower range of the oringal search query (it doesnt matter whether we take the lower or
     * the upper range, since both do not overlap).
     */
    private void searchInbetween(int x) {
	Node<T> n = this.root;
	while (n != null) {
	    //System.out.println("****** SIBN: " + current);
	    if (x < n.getMedian() ) {
		/* First up date the right neighbor (most left to date that is right of query) .*/
		if (n.hasInterval()) {
		    Interval<T> item = n.getLeftmostItem();
		    switchRightNeighborIfBetter(item,x);
		} else {
		    Node rd = n.getLeftmostDescendentOfRightChild();
		    Interval<T> item = rd.getLeftmostItem();  
		    switchRightNeighborIfBetter(item,x);
		}
		/* Now continue to navigate the interval tree */
		n = n.getLeft();
	    } else {
		/* First up date the left neighbor (most right to date that is left of query) .*/
		if (n.hasInterval()) {
		    Interval<T> item = n.getRightmostItem();  
		    switchLeftNeighborIfBetter(item,x);
		} else {
		    /* If the current node has no intervals on its own, then it
		       is possible that its right child has a better right neighbor than the current
		       right neighbor. */
		    Node rd = n.getRightmostDescendentOfLeftChild();
		    Interval<T> item = rd.getRightmostItem();  
		    switchLeftNeighborIfBetter(item,x);
		}
		/* Now continue to navigate the interval tree */
		n = n.getRight();
	    }
	    //System.out.println("rightN=" + rightNeighbor);
	    //System.out.println("leftN=" + leftNeighbor);
	}
	
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
    
    public void debugPrint() {
	debugPrint(this.root);
    }
    
}
