package de.charite.compbio.jannovar.datasource;

import de.charite.compbio.jannovar.JannovarException;

/**
 * Thrown on problems with data source configuration files.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class InvalidDataSourceException extends JannovarException {

	private static final long serialVersionUID = 1L;

	public InvalidDataSourceException() {
		super();
	}

	public InvalidDataSourceException(String msg) {
		super(msg);
	}

	public InvalidDataSourceException(String msg, Throwable cause) {
		super(msg);
	}

}
