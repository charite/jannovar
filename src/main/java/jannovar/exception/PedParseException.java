package jannovar.exception;



/**
 * Exception that occurs during parsing of PEDfiles.
 * @author Peter Robinson 
 * @version 0.03 (April 28,2013)
 */
public class PedParseException extends JannovarException {

    public static final long serialVersionUID = 2L;

    public PedParseException() {
	super("Unknown exception during parsing of Ped File");
    }

    public PedParseException(String msg) {
	super(msg);
    }

}