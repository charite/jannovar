/**
 *
 */
package jannovar.datasource;

import jannovar.JannovarException;

/**
 * Exception that can be called if something went wrong while downloading the transcript files.
 *
 * @author Marten Jaeger <marten.jaeger@charite.de>
 */
public class FileDownloadException extends JannovarException {
	private static final long serialVersionUID = 1L;

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
