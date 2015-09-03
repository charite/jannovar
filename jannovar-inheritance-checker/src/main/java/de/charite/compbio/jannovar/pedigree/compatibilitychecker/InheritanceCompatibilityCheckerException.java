package de.charite.compbio.jannovar.pedigree.compatibilitychecker;

import de.charite.compbio.jannovar.JannovarException;

/**
 * Exception that occurs when using invalid {@link de.charite.compbio.jannovar.pedigree.InheritanceVariantContextList} or {@link de.charite.compbio.jannovar.pedigree.Pedigree} objects in the
 * compatibility checking.
 *
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 * @version 0.15-SNAPSHOT
 */
public class InheritanceCompatibilityCheckerException extends JannovarException {


	/** Constant <code>serialVersionUID=4032184488029441661L</code> */
	private static final long serialVersionUID = -4032184488029441661L;

	/**
	 * <p>Constructor for InheritanceCompatibilityCheckerException.</p>
	 */
	public InheritanceCompatibilityCheckerException() {
		super();
	}

	/**
	 * <p>Constructor for InheritanceCompatibilityCheckerException.</p>
	 *
	 * @param msg a {@link java.lang.String} object.
	 */
	public InheritanceCompatibilityCheckerException(String msg) {
		super(msg);
	}

	/**
	 * <p>Constructor for InheritanceCompatibilityCheckerException.</p>
	 *
	 * @param msg a {@link java.lang.String} object.
	 * @param cause a {@link java.lang.Throwable} object.
	 */
	public InheritanceCompatibilityCheckerException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
