package de.charite.compbio.jannovar.cmd;

import com.google.common.collect.ImmutableMap;

import de.charite.compbio.jannovar.JannovarException;
import de.charite.compbio.jannovar.data.Chromosome;
import de.charite.compbio.jannovar.data.JannovarData;
import de.charite.compbio.jannovar.data.JannovarDataSerializer;
import de.charite.compbio.jannovar.data.ReferenceDictionary;

/**
 * Base class for commands needing annotation data.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public abstract class JannovarAnnotationCommand extends JannovarCommand {

	/** {@link JannovarData} with the information */
	protected JannovarData jannovarData = null;

	/** {@link ReferenceDictionary} with genome information. */
	protected ReferenceDictionary refDict = null;

	/** Map of Chromosomes, used in the annotation. */
	protected ImmutableMap<Integer, Chromosome> chromosomeMap = null;

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
		this.jannovarData = new JannovarDataSerializer(this.options.dataFile).load();
		this.refDict = this.jannovarData.getRefDict();
		this.chromosomeMap = this.jannovarData.getChromosomes();
	}

}