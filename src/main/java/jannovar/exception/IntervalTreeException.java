package jannovar.exception;


/**
 * IntervalTree exceptions are thrown if there is an error during the
 * construction of the IntervalTree.
 * @see jannovar.interval.IntervalTree
 * @see jannovar.interval.Node
 * @see jannovar.interval.Interval
 * @author Peter Robinson
 * @version 0.02 (April 28, 2013)
 */
public class IntervalTreeException extends JannovarException {

    private String mistake=null;
    
    public static final long serialVersionUID = 2L;

    public IntervalTreeException() {
	super();
    }

    public IntervalTreeException(String msg) {
	super(msg);
	this.mistake = msg;
    }

    @Override
    public String getError()
    {
	return mistake;
    }
}