package exomizer.exception;


/**
 * Exception that occurs during parsing of VCF files.
 * @author Peter Robinson
 * @version 0.01 (August 22,2012)
 */
public class ExomizerInitializationException extends ExomizerException {

    public ExomizerInitializationException() {
	super("Unknown exception during initialization of Exomizer");
    }

    public ExomizerInitializationException(String msg) {
	super(msg);
    }
}
