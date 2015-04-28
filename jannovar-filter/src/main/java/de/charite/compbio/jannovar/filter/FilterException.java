package de.charite.compbio.jannovar.filter;

import de.charite.compbio.jannovar.JannovarException;

/**
 * Thrown on problems during the filtration.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class FilterException extends JannovarException {

	public FilterException() {
		super();
	}

	public FilterException(String string) {
		super(string);
	}

	public FilterException(String string, Throwable cause) {
		super(string, cause);
	}

	private static final long serialVersionUID = 1L;

}
