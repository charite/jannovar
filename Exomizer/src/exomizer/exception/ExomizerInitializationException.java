package exomizer.exception;


/**
 * Exception that occurs during parsing of VCF files.
 * @author Peter Robinson <peter.robinson@charite.de>
 * @date August 22,2012
 */
public class ExomizerInitializationException extends ExomizerException {

    public ExomizerInitializationException() {
	super("Unknown exception during initialization of Exomizer");
    }

    public ExomizerInitializationException(String msg) {
	super(msg);
    }
}
