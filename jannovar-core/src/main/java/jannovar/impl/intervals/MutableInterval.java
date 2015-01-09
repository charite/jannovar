package jannovar.impl.intervals;

/**
 * Mutable half-open interval, for incremental building of {@link Interval} objects.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class MutableInterval<T> implements java.io.Serializable, Comparable<MutableInterval<T>> {

	/** start point of the interval (inclusive) */
	public int begin = -1;

	/** end point of the interval (exclusive) */
	public int end = -1;

	/** the value stored for the Interval */
	public T value = null;

	/** the maximum of this nodes {@link #end} and both of it children's {@link #end} */
	public int maxEnd = -1;

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

	@Override
	public int compareTo(MutableInterval<T> o) {
		final int result = (begin - o.begin);
		if (result == 0)
			return (end - o.end);
		return result;
	}

}
