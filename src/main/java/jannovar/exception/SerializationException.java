package jannovar.exception;

/**
 * Exception thrown on problems with serialization or deserialization.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class SerializationException extends JannovarException {
	
	public SerializationException(String msg) {
		super(msg);
	}

	private static final long serialVersionUID = 1L;

}
