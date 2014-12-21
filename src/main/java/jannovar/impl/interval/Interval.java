package jannovar.impl.interval;

/**
 * This class implements an interval on a numberline with a lowpoint and a highpoint (where lowpoint <= highpoint). The
 * Interval is intended to contain some other kind of data (clients of this templated class will need to initialize the
 * template variable T to the corresponding datatype). The intervals are intended to be used with the
 * {@link jannovar.impl.interval.IntervalTree IntervallTree}.
 *
 * Although public, this class is not meant to be part of the public Jannovar intervace. It can be changed or removed at
 * any point.
 *
 * @param <T>
 *            The value attached to each node/interval.
 *
 * @see "jannovar.interval.IntervalTree"
 * @author Christopher Dommaschenz, Radostina Misirkova, Nadine Taube, Gizem Top
 * @version 0.03 (22 May, 2013)
 */
public final class Interval<T> implements java.io.Serializable {
	/**
	 * The object that we are putting into the interval tree (such as a {@link jannovar.reference.TranscriptModel
	 * TranscriptModel} object).
	 */
	private T value;
	/** The smaller end point of the interval */
	private int lowpoint;
	/** The larger end point of the interval */
	private int highpoint;
	/** Class version (for serialization). */
	public static final long serialVersionUID = 1L;

	/**
	 * @param lpoint
	 *            The lower end of the Interval
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
	 * @param hpoint
	 *            The upper end of the Interval
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
	 * @param val
	 *            The object that is to be represented by the interval
	 */
	public void setValue(T val) {
		this.value = val;
	}

	/**
	 * @return The object that is represented by this interval
	 */
	public T getValue() {
		return value;
	}

	/**
	 * Interval constructor.
	 *
	 * @param low
	 *            lower endpoint of the interval (cannot be higher than the upper endpoint or exception is thrown)
	 * @param high
	 *            upper endpoint of the interval
	 * @param value
	 *            The object represented by the interval.
	 */
	public Interval(int low, int high, T value) {
		if (low <= high) {
			this.lowpoint = low;
			this.highpoint = high;
			this.value = value;
		} else {
			System.err.println("Error, low endpoint higher than upper endpoint for interval");
			System.err.println("Recheck the format of the input data, low end of interval must be less than high end");
			System.err.println(String.format("lo:%d-high:%d ()", this.lowpoint, this.highpoint));
			System.exit(1);
		}
	}

	/* returns a string that represents the interval */
	@Override
	public String toString() {
		return "[" + lowpoint + "," + highpoint + "," + value + "]";
	}

}
