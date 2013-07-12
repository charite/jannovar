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

	/**
	 * 
	 */
	public FileDownloadException() {
		super("Unknown Exception while downloading trranscript annotation files");
	}

	/**
	 * @param msg
	 */
	public FileDownloadException(String msg) {
		super(msg);
	}

}
