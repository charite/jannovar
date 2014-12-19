package jannovar.exception;

/**
 * Thrown on problems with data source configuration files.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class InvalidDataSourceException extends JannovarException {

	private static final long serialVersionUID = 1L;

	public InvalidDataSourceException(String msg) {
		super(msg);
	}
}
