package de.charite.compbio.jannovar.datasource;

import de.charite.compbio.jannovar.JannovarException;

/**
 * Thrown on problems with data source configuration files.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
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
