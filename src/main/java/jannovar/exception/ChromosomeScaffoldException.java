package jannovar.exception;



/**
 * Exception that occurs during parsing of VCF files.
 * @author Peter Robinson 
 * @version 0.03 (April 28,2013)
 */
public class ChromosomeScaffoldException extends VCFParseException {

    public static final long serialVersionUID = 2L;

    public String badChromosome=null;

    /** Record when there is an unparsable chromosome, usually
     * a scaffold such as GL000225.1
     */
    @Override
    public void setBadChromosome(String c) { this.badChromosome = c; }

    @Override
    public String getBadChromosome() { return this.badChromosome; }

    public ChromosomeScaffoldException() {
	super("Unknown exception during parsing of VCF File");
    }

    public ChromosomeScaffoldException(String msg) {
	super(msg);
    }

}