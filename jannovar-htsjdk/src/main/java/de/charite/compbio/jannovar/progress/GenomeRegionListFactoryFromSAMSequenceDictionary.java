package de.charite.compbio.jannovar.progress;

import htsjdk.samtools.SAMSequenceDictionary;
import htsjdk.samtools.SAMSequenceRecord;

import java.util.ArrayList;

/**
 * Construct {@link GenomeRegionList} from a {@link SAMSequenceDictionary}
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class GenomeRegionListFactoryFromSAMSequenceDictionary {

	public GenomeRegionList construct(SAMSequenceDictionary seqDict) {
		ArrayList<GenomeRegion> regions = new ArrayList<>();
		for (SAMSequenceRecord record : seqDict.getSequences())
			regions.add(new GenomeRegion(record.getSequenceName(), 0, record.getSequenceLength()));
		return new GenomeRegionList(regions);
	}

}
