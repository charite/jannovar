package jannovar.exception;



/**
 * Annotation exceptions are thrown when the information provided is not 
 * well formed or not sufficient to create a correct annotation.
 * @author Peter Robinson
 * @version 0.02 (April 28, 2013)
 */
public class AnnotationException extends JannovarException {

    private String mistake=null;
    
    public static final long serialVersionUID = 2L;

    public AnnotationException() {
	super();
    }

    public AnnotationException(String msg) {
	super(msg);
	this.mistake = msg;
    }

    @Override
    public String getError()
    {
	return mistake;
    }
}