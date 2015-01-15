package jannovar.pedigree;

import jannovar.JannovarException;

/**
 * Exception that occurs when using invalid {@link GenotypeList} or {@link Pedigree} objects in the compatibility
 * checking.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class CompatibilityCheckerException extends JannovarException {

	public static final long serialVersionUID = 1L;

	public CompatibilityCheckerException(String msg) {
		super(msg);
	}

}