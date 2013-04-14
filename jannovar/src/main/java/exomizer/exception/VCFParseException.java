package exomizer.exception;



/**
 * Exception that occurs during parsing of VCF files.
 * @author Peter Robinson 
 * @version 0.02 (February 18,2013)
 */
public class VCFParseException extends ExomizerException {

    public static final long serialVersionUID = 1L;

    public String badChromosome=null;

    /** Record when there is an unparsable chromosome, usually
     * a scaffold such as GL000225.1
     */
    public void setBadChromosome(String c) { this.badChromosome = c; }

    public String getBadChromosome() { return this.badChromosome; }

    public VCFParseException() {
	super("Unknown exception during parsing of VCF File");
    }

    public VCFParseException(String msg) {
	super(msg);
    }

}