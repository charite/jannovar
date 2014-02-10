package jannovar.exception;



/**
 * A small Exception hierarchy for the Exomizer. Subclasses of this
 * class are used to catch various problems from the various parts of the
 * program, some of which will cause the program to terminate and some will
 * note. In any case, we want to terminate gracefully so that it is possible
 * to print an HTML error message for users if say there are major problems with
 * the input format.
 * @author Peter Robinson
 * @version 0.03 (April 28, 2013)
 */
public class JannovarException extends Exception {

    private String mistake=null;
    
    public static final long serialVersionUID = 2L;

    public JannovarException() {
	super();
    }

    public JannovarException(String msg) {
	super(msg);
	this.mistake = msg;
    }

    public String getError()
    {
	return mistake;
    }

    /**
     * Print a summary of the error
     */
    @Override
    public String toString() {
	return getError();
    }
}
