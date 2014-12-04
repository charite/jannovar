package jannovar.cmd;

import jannovar.exception.CommandLineParsingException;
import jannovar.exception.HelpRequestedException;
import jannovar.exception.JannovarException;
import jannovar.io.SerializationManager;
import jannovar.reference.Chromosome;
import jannovar.reference.TranscriptModel;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Base class for commands needing annotation data.
 *
 * Although public, this class is not meant to be part of the public Jannovar intervace. It can be changed or removed at
 * any point.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public abstract class JannovarAnnotationCommand extends JannovarCommand {
	/** Map of Chromosomes, used in the annotation. */
	protected HashMap<Byte, Chromosome> chromosomeMap = null;

	public JannovarAnnotationCommand(String[] argv) throws CommandLineParsingException, HelpRequestedException {
		super(argv);
	}

	/**
	 * Deserialize the transcript definition file, as configured in {@link #options}.
	 *
	 * To run Jannovar, the user must pass a transcript definition file with the -D flag. This can be one of the files
	 * ucsc.ser, ensembl.ser, or refseq.ser (or a comparable file) containing a serialized version of the
	 * TranscriptModel objects created to contain info about the transcript definitions (exon positions etc.) extracted
	 * from UCSC, Ensembl, or Refseq and necessary for annotation.
	 *
	 * @throws JannovarException
	 *             when there is a problem with the deserialization
	 * @throws HelpRequestedException
	 *             when the user requested the help page
	 */
	protected void deserializeTranscriptDefinitionFile() throws JannovarException, HelpRequestedException {
		ArrayList<TranscriptModel> kgList;
		SerializationManager manager = new SerializationManager();
		kgList = manager.deserializeKnownGeneList(this.options.dataFile);
		this.chromosomeMap = Chromosome.constructChromosomeMapWithIntervalTree(kgList);
	}
}
