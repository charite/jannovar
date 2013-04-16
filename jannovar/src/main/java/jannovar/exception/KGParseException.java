package jannovar.exception;



/**
 * Exception that occurs during parsing of UCSC KnownGene.txt files.
 * @author Peter Robinson 
 * @version 0.01 (September 15,2012)
 */
public class KGParseException extends ExomizerException {

    public static final long serialVersionUID = 1L;

    public KGParseException() {
	super("Unknown exception during parsing of UCSC Known Gene File");
    }

    public KGParseException(String msg) {
	super(msg);
    }

}