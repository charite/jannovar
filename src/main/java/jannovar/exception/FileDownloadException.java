/**
 * 
 */
package jannovar.exception;

/**
 * Exception that can be called if something went wrong while downloading the transcript files. 
 * @author mjaeger
 * @version 0.1 (2013-07-11)
 */
public class FileDownloadException extends JannovarException {
    private static final long serialVersionUID = 1L;
    /**
     * 
     */
    public FileDownloadException() {
	super("Exception while downloading transcript annotation files");
    }
    
    /**
     * @param msg
     */
    public FileDownloadException(String msg) {
	super(msg);
    }

}
