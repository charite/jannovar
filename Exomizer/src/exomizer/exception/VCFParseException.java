package exomizer.exception;



/**
 * Exception that occurs during parsing of VCF files.
 * @author Peter Robinson 
 * @version 0.01 (August 22,2012)
 */
public class VCFParseException extends ExomizerException {

    public VCFParseException() {
	super("Unknown exception during parsing of VCF File");
    }

    public VCFParseException(String msg) {
	super(msg);
    }

}