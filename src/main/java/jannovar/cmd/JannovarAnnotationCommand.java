package jannovar.cmd;

import java.util.ArrayList;
import java.util.HashMap;

import jannovar.JannovarOptions;
import jannovar.exception.JannovarException;
import jannovar.io.SerializationManager;
import jannovar.reference.Chromosome;
import jannovar.reference.TranscriptModel;

/**
 * Base class for commands needing annotation data.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public abstract class JannovarAnnotationCommand extends JannovarCommand {

	/** Map of Chromosomes, used in the annotation. */
	protected HashMap<Byte, Chromosome> chromosomeMap = null;

	public JannovarAnnotationCommand(JannovarOptions options) {
		super(options);
	}

	/**
	 * Perform deserialization of transcript data.
	 *
	 * @return true if the user gave a path to serialized data that was then serialized
	 * @throws JannovarException
	 *             on deserialization problems
	 */
	protected boolean deserialize() throws JannovarException {
		if (!options.deserialize())
			return false;
		deserializeTranscriptDefinitionFile();
		return true;
	}

	/**
	 * To run Jannovar, the user must pass a transcript definition file with the -D flag. This can be one of the files
	 * ucsc.ser, ensembl.ser, or refseq.ser (or a comparable file) containing a serialized version of the
	 * TranscriptModel objects created to contain info about the transcript definitions (exon positions etc.) extracted
	 * from UCSC, Ensembl, or Refseq and necessary for annotation.
	 *
	 * @throws JannovarException
	 */
	private void deserializeTranscriptDefinitionFile() throws JannovarException {
		ArrayList<TranscriptModel> kgList;
		SerializationManager manager = new SerializationManager();
		kgList = manager.deserializeKnownGeneList(this.options.serializedFile);
		this.chromosomeMap = Chromosome.constructChromosomeMapWithIntervalTree(kgList);
	}
}
