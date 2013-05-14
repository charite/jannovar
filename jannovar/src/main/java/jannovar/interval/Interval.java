package jannovar.interval;

import java.util.Comparator;

/**
 * Implements an interval with two end points and each interval contains
 * additional information.
 * 
 * @param value element that contains additional information
 * @param lowpoint The smaller end point of the interval
 * @param highpoint The bigger end point of the interval
 */
public class Interval<K extends Comparable<? super K>, V> {
	public V value;
	public K lowpoint;
	public K highpoint;

	/**
	 * Getters and setters for the parameters of the Interval class.
	 */
	public void setLow(K lowpoint) {
		this.lowpoint = lowpoint;
	}

	public K getLow() {
		return lowpoint;
	}

	public void setHigh(K highpoint) {
		this.highpoint = highpoint;
	}

	public K getHigh() {
		return highpoint;
	}

	public void setValue(V value) {
		this.value = value;
	}

	public V getValue() {
		return value;
	}

	/**
	 * Interval constructor.
	 */
	public Interval(K low, K high, V value) {
		if (low.compareTo(high) <= 0) {
			this.lowpoint = low;
			this.highpoint = high;
			this.value = value;
		}
	}

	/**
	 * The new comparator for the leftorder interval list which contains the
	 * left end points sorted in increasing order.
	 */
	public static class LeftComparator implements Comparator<Interval<Integer, String>> {
		public int compare(Interval<Integer, String> interval_1, Interval<Integer, String> interval_2) {
			/* returns -1 if the lowpoint of i is smaller than the lowpoint of j */
			if (interval_1.getLow().compareTo(interval_2.getLow()) < 0)
				return -1;
			/* returns 1 if the lowpoint of i is bigger than the lowpoint of j */
			else if (interval_1.getLow().compareTo(interval_2.getLow()) > 0)
				return 1;
			/*
			 * returns -1 if the highpoint of i is smaller than the highpoint of
			 * j
			 */
			else if (interval_1.getHigh().compareTo(interval_2.getHigh()) < 0)
				return -1;
			/* returns 1 if the highpoint of i is bigger than the highpoint of j */
			else if (interval_1.getHigh().compareTo(interval_2.getHigh()) > 0)
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
	public static class RightComparator implements Comparator<Interval<Integer, String>> {
		public int compare(Interval<Integer, String> interval_1, Interval<Integer, String> interval_2) {
			/*
			 * returns -1 if the highpoint of i is bigger than the highpoint of
			 * j
			 */
			if (interval_1.getHigh().compareTo(interval_2.getHigh()) > 0)
				return -1;
			/*
			 * returns 1 if the highpoint of i is smaller than the highpoint of
			 * j
			 */
			else if (interval_1.getHigh().compareTo(interval_2.getHigh()) < 0)
				return 1;
			/* returns -1 if the lowpoint of i is bigger than the lowpoint of j */
			else if (interval_1.getLow().compareTo(interval_2.getLow()) > 0)
				return -1;
			/* returns 1 if the lowpoint of i is smaller than the lowpoint of j */
			else if (interval_1.getLow().compareTo(interval_2.getLow()) < 0)
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
