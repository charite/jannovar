package de.charite.compbio.jannovar.impl.intervals;

/**
 * Half-open interval for serialization of an {@link IntervalArray}.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class Interval<T> implements java.io.Serializable, Comparable<Interval<T>> {

	/** start point of the interval (inclusive) */
	private final int begin;

	/** end point of the interval (exclusive) */
	private final int end;

	/** the value stored for the Interval */
	private final T value;

	/** the maximum of this nodes {@link #end} and both of it children's {@link #end} */
	private final int maxEnd;

	/** version number for serializing an Interval */
	private static final long serialVersionUID = 1L;

	public Interval(MutableInterval<T> other) {
		this.begin = other.getBegin();
		this.end = other.getEnd();
		this.value = other.getValue();
		this.maxEnd = other.getMaxEnd();
	}

	public Interval(int begin, int end, T value, int maxEnd) {
		this.begin = begin;
		this.end = end;
		this.value = value;
		this.maxEnd = maxEnd;
	}

	/** @return start point of the interval (inclusive) */
	public int getBegin() {
		return begin;
	}

	/** @return end point of the interval (exclusive) */
	public int getEnd() {
		return end;
	}

	/** @return the value stored for the Interval */
	public T getValue() {
		return value;
	}

	/** @return the maximum of this nodes {@link #end} and both of it children's {@link #end} */
	public int getMaxEnd() {
		return maxEnd;
	}

	/**
	 * @return <code>true</code> if <code>point</code> is right of {@link #maxEnd}.
	 */
	public boolean allLeftOf(int point) {
		return (maxEnd <= point);
	}

	/**
	 * @return <code>true</code> if <code>point</code> is right of this interval.
	 */
	public boolean isLeftOf(int point) {
		return (end <= point);
	}

	/**
	 * @return <code>true</code> if <code>point</code> is left of this interval.
	 */
	public boolean isRightOf(int point) {
		return (point < begin);
	}

	/**
	 * @return <code>true</code> if this intervals contains the given point.
	 */
	public boolean contains(int point) {
		return ((begin <= point) && (point < end));
	}

	/**
	 * @return <code>true</code> if this interval overlaps with <code>[begin, end)</code>.
	 */
	public boolean overlapsWith(int begin, int end) {
		return ((begin < this.end) && (this.begin < end));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + begin;
		result = prime * result + end;
		result = prime * result + maxEnd;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("rawtypes")
		Interval other = (Interval) obj;
		if (begin != other.begin)
			return false;
		if (end != other.end)
			return false;
		if (maxEnd != other.maxEnd)
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	public int compareTo(Interval<T> o) {
		final int result = (begin - o.begin);
		if (result == 0)
			return (end - o.end);
		return result;
	}

	@Override
	public String toString() {
		return "Interval [begin=" + begin + ", end=" + end + ", value=" + value + ", maxEnd=" + maxEnd + "]";
	}

}
