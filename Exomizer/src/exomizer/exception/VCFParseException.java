package exomizer.exception;



/**
 * A small Exception hierarchy for the Exomizer. Subclasses of this
 * class are used to catch various problems from the various parts of the
 * program, some of which will cause the program to terminate and some will
 * note. In any case, we want to terminate gracefully so that it is possible
 * to print an HTML error message for users if say there are major problems with
 * the input format.
 * @author Peter Robinson <peter.robinson@charite.de>
 * @date August 22,2012
 */
public class VCFParseException extends ExomizerException {

    public VCFParseException() {
	super("Unknown exception during parsing of VCF File");
    }

    public VCFParseException(String msg) {
	super(msg);
    }

}