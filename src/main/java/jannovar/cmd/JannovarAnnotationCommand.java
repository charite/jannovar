package jannovar.cmd;

import jannovar.JannovarException;
import jannovar.io.JannovarData;
import jannovar.io.JannovarDataSerializer;
import jannovar.io.ReferenceDictionary;
import jannovar.reference.Chromosome;

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

	/** {@link ReferenceDictionary} with genome information. */
	protected ReferenceDictionary refDict = null;

	/** Map of Chromosomes, used in the annotation. */
	protected HashMap<Integer, Chromosome> chromosomeMap = null;

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
		JannovarData data = new JannovarDataSerializer(this.options.dataFile).load();
		this.refDict = data.refDict;
		this.chromosomeMap = Chromosome.constructChromosomeMapWithIntervalTree(data.transcriptInfos);
	}
}
