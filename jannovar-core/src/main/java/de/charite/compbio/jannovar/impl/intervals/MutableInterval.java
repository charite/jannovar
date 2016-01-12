package de.charite.compbio.jannovar.impl.intervals;

/**
 * Mutable half-open interval, for incremental building of {@link Interval} objects.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 */
public class MutableInterval<T> implements java.io.Serializable, Comparable<MutableInterval<T>> {

	/** start point of the interval (inclusive) */
	private int begin = -1;

	/** end point of the interval (exclusive) */
	private int end = -1;

	/** the value stored for the Interval */
	private T value = null;

	/** the maximum of this nodes {@link #end} and both of it children's {@link #end} */
	private int maxEnd = -1;

	/** verioin number for serializing an Interval */
	private static final long serialVersionUID = 1L;

	public MutableInterval() {
	}

	public MutableInterval(int begin, int end, T value, int maxEnd) {
		this.begin = begin;
		this.end = end;
		this.value = value;
		this.maxEnd = maxEnd;
	}

	public MutableInterval(int begin, int end, T value) {
		this(begin, end, value, -1);
	}

	public int getBegin() {
		return begin;
	}

	public void setBegin(int begin) {
		this.begin = begin;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public int getMaxEnd() {
		return maxEnd;
	}

	public void setMaxEnd(int maxEnd) {
		this.maxEnd = maxEnd;
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
		if (begin != other.getBegin())
			return false;
		if (end != other.getEnd())
			return false;
		if (maxEnd != other.getMaxEnd())
			return false;
		if (value == null) {
			if (other.getValue() != null)
				return false;
		} else if (!value.equals(other.getValue()))
			return false;
		return true;
	}

	public int compareTo(MutableInterval<T> o) {
		final int result = (begin - o.begin);
		if (result == 0)
			return (end - o.end);
		return result;
	}

}
