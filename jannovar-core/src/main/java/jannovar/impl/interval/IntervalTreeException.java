package jannovar.impl.interval;

import jannovar.JannovarException;

/**
 * IntervalTree exceptions are thrown if there is an error during the construction of the IntervalTree.
 *
 * @author Peter N Robinson <peter.robinson@charite.de>
 */
public class IntervalTreeException extends JannovarException {

	public static final long serialVersionUID = 2L;

	public IntervalTreeException() {
		super();
	}

	public IntervalTreeException(String msg) {
		super(msg);
	}

}