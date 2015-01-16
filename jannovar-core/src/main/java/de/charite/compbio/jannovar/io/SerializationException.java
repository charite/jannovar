package de.charite.compbio.jannovar.io;

import de.charite.compbio.jannovar.JannovarException;

/**
 * Exception thrown on problems with serialization or deserialization.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class SerializationException extends JannovarException {
	
	public SerializationException(String msg) {
		super(msg);
	}

	private static final long serialVersionUID = 1L;

}
