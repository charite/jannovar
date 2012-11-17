package exomizer.exception;



/**
 * Annotation exceptions are thrown when the information provided is not 
 * well formed or not sufficient to create a correct annotation.
 * @author Peter Robinson
 * @version 0.01 (Nov 22,2012)
 */
public class AnnotationException extends Exception {

    private String mistake=null;
    
    public static final long serialVersionUID = 1L;

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