package de.charite.compbio.jannovar.reference;

import de.charite.compbio.jannovar.JannovarException;

/**
 * Thrown when a coordinate conversion failed.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public class ProjectionException extends JannovarException {

	public static final long serialVersionUID = 1L;

	public ProjectionException(String msg) {
		super(msg);
	}

}
