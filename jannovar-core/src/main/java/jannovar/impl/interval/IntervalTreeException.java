package jannovar.impl.interval;

import jannovar.JannovarException;

/**
 * IntervalTree exceptions are thrown if there is an error during the construction of the IntervalTree.
 *
 * @see jannovar.impl.interval.IntervalTree
 * @see jannovar.impl.interval.Node
 * @see jannovar.impl.interval.Interval
 * @author Peter Robinson
 * @version 0.02 (April 28, 2013)
 */
public class IntervalTreeException extends JannovarException {

	private String mistake = null;

	public static final long serialVersionUID = 2L;

	public IntervalTreeException() {
		super();
	}

	public IntervalTreeException(String msg) {
		super(msg);
		this.mistake = msg;
	}

	@Override
	public String getError() {
		return mistake;
	}
}