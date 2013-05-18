package jannovar.interval;

import java.util.Comparator;

/**
 * This class implements an interval on a numberline with a lowpoint
 * and a highpoint (where lowpoint <= highpoint). The Interval is intended
 * to contain some other kind of data (clients of this templated class will
 * need to initialize the template variable T to the corresponding datatype). The
 * intervals are intended to be used with the
 * {@link jannovar.interval.IntervalTree IntervallTree}.
 * @see jannovar.interval.IntervalTree
 * @author names..
 * @version 0.02 (15 May, 2013)
 */
public class Interval<T>  { // implements Comparable<Interval<T>> 
    /** The object that we are putting into the interval tree (such as a 
     * {@jannovar.reference.TranscriptModel TranscriptModel} object).*/
    private T value;
    /** The smaller end point of the interval */
    private int lowpoint;
    /** The larger end point of the interval */
    private int highpoint;

    /**
     * @param lpoint The lower end of the Interval
     */
    public void setLow(int lpoint) {
    	this.lowpoint = lpoint;
    }

    /**
     * @return the lower end of the interval.
     */
    public int getLow() {
        return lowpoint;
    }

    /**
     * @param hpoint The upper end of the Interval
     */
    public void setHigh(int hpoint) {
        this.highpoint = hpoint;
    }

    /**
     * @return the upper end of the interval.
     */
    public int getHigh() {
    	return highpoint;
    }

    /**
     * @param val The object that is to be represented by the interval
     */
    public void setValue(T val) {
        this.value = val;
    }

    /**
     *	@return The object that is represented by this interval
     */
    public T getValue() {
        return value;
    }

    /**
     * Interval constructor.
     */
    public Interval(int low, int high, T value) {
        if (low < high) {
		this.lowpoint = low;
		this.highpoint = high;
		this.value = value;
	    }
    }

    /**
     * The new comparator for the leftorder interval list which contains the
     * left end points sorted in increasing order.
     */
    public class LeftComparator implements Comparator<Interval<T>> {
    	public int compare(Interval<T> interval_1, Interval<T> interval_2) {
	    /* returns -1 if the lowpoint of i is smaller than the lowpoint of j */
	    if (interval_1.getLow() < interval_2.getLow())
        	return -1;
	    /* returns 1 if the lowpoint of i is bigger than the lowpoint of j */
	    else if (interval_1.getLow() > interval_2.getLow())
		return 1;
	    /*
	    * returns -1 if the highpoint of i is smaller than the highpoint of
	     * j
	     */
	    else if (interval_1.getHigh() < interval_2.getHigh())
		return -1;
	    /* returns 1 if the highpoint of i is bigger than the highpoint of j */
	    else if (interval_1.getHigh() > interval_2.getHigh())
		return 1;
	    /* returns 0 if they are equal */
	    else
		return 0;
	    }
	}

	/**
	 * The new comparator for the rightorder interval list which contains the
	 * right end points sorted in decreasing order.
	 * 
	 */
	public class RightComparator implements Comparator<Interval<T>> {
	    public int compare(Interval<T> interval_1, Interval<T> interval_2) {
		/*
		 * returns -1 if the highpoint of i is bigger than the highpoint of
		 * j
		 */
		if (interval_1.getHigh() > interval_2.getHigh())
		    return -1;
		/*
		 * returns 1 if the highpoint of i is smaller than the highpoint of
		 * j
		 */
		else if (interval_1.getHigh() < interval_2.getHigh() )
		    return 1;
		/* returns -1 if the lowpoint of i is bigger than the lowpoint of j */
		else if (interval_1.getLow() > interval_2.getLow())
		    return -1;
		    /* returns 1 if the lowpoint of i is smaller than the lowpoint of j */
		else if (interval_1.getLow() < interval_2.getLow() )
		    return 1;
		 /* returns 0 if they are equal */
		else
		    return 0;
	    }
	}

	/* returns a string that represents the interval */
	public String toString() {
	    return "[" + lowpoint + "," + highpoint + "," + value + "]";
	}

}
