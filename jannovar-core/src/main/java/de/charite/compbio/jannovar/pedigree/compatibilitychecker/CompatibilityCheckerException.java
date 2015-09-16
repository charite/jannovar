package de.charite.compbio.jannovar.pedigree.compatibilitychecker;

import de.charite.compbio.jannovar.JannovarException;
import de.charite.compbio.jannovar.pedigree.GenotypeList;
import de.charite.compbio.jannovar.pedigree.Pedigree;

/**
 * Exception that occurs when using invalid {@link GenotypeList} or {@link Pedigree} objects in the compatibility
 * checking.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 * @deprecated Use {@link InheritanceCompatibilityCheckerException}
 */
@Deprecated
public class CompatibilityCheckerException extends JannovarException {

	public static final long serialVersionUID = 1L;

	public CompatibilityCheckerException() {
		super();
	}

	public CompatibilityCheckerException(String msg) {
		super(msg);
	}

	public CompatibilityCheckerException(String msg, Throwable cause) {
		super(msg, cause);
	}

}