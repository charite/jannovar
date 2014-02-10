package jannovar.exception;



/**
 * Exception that occurs during parsing of VCF files.
 * @author Peter Robinson 
 * @version 0.03 (April 28,2013)
 */
public class VCFParseException extends JannovarException {

    public static final long serialVersionUID = 2L;

    public String badChromosome=null;

    /** Record when there is an unparsable chromosome, usually
     * a scaffold such as GL000225.1
     * @param c 'bad' chromosome
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