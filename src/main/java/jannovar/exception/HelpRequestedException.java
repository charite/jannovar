package jannovar.exception;

/**
 * Thrown when the user requests help.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class HelpRequestedException extends JannovarException {

	private static final long serialVersionUID = 1L;

	public HelpRequestedException() {
	}

	public HelpRequestedException(String msg) {
		super(msg);
	}

}
