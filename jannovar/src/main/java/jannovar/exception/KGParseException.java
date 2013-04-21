package jannovar.exception;



/**
 * Exception that occurs during parsing of UCSC KnownGene.txt files.
 * @author Peter Robinson 
 * @version 0.02 (April 22,2013)
 */
public class KGParseException extends JannovarException {

    public static final long serialVersionUID = 1L;

    public KGParseException() {
	super("Unknown exception during parsing of UCSC Known Gene File");
    }

    public KGParseException(String msg) {
	super(msg);
    }

}