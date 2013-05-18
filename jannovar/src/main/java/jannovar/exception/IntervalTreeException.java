package jannovar.exception;



/**
 * IntervalTree exceptions are thrown if there is an error during the
 * construction of hte IntervalTree.
 * @see jannovar.interval.IntervalTree
 * @see jannovar.interval.Node;
 * @see jannovar.interval.Interval;
 * @author Peter Robinson
 * @version 0.02 (April 28, 2013)
 */
public class IntervalTreeException extends JannovarException {

    private String mistake=null;
    
    public static final long serialVersionUID = 2L;

    public AnnotationException() {
	super();
    }

    public AnnotationException(String msg) {
	super(msg);
	this.mistake = msg;
    }

    public String getError()
    {
	return mistake;
    }
}