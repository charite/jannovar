package de.charite.compbio.jannovar.data;

import de.charite.compbio.jannovar.JannovarException;

/**
 * Exception thrown on problems with serialization or deserialization.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public class SerializationException extends JannovarException {
	
	public SerializationException(String msg) {
		super(msg);
	}

	private static final long serialVersionUID = 1L;

}
