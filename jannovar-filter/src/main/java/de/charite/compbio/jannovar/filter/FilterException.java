package de.charite.compbio.jannovar.filter;

/**
 * Thrown on problems during the filtration.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class FilterException extends Exception {

	public FilterException(String string) {
		super(string);
	}

	private static final long serialVersionUID = 1L;

}
