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

	/**
	 * Deserialize the transcript definition file from {@link pathToDataFile}.
	 *
	 * @param pathToDataFile
	 *            String with the path to the data file to deserialize
	 * @throws JannovarException
	 *             when there is a problem with the deserialization
	 * @throws HelpRequestedException
	 *             when the user requested the help page
	 */
	protected void deserializeTranscriptDefinitionFile(String pathToDataFile)
			throws JannovarException, HelpRequestedException {
		this.jannovarData = new JannovarDataSerializer(pathToDataFile).load();
		this.refDict = this.jannovarData.getRefDict();
		this.chromosomeMap = this.jannovarData.getChromosomes();
	}

}