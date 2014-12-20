package jannovar.reference;

import jannovar.io.ReferenceDictionary;

/**
 * Allows the easy creation of transcript models from knownGenes.txt.gz lines.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class TranscriptModelFactory {

	/**
	 * Helper function to parse a knownGenes.txt.gz line into a TranscriptModel.
	 *
	 * @param refDict
	 *            reference dictionary
	 * @param s
	 *            The knownGeneList line to parse.
	 */
	public static TranscriptInfoBuilder parseKnownGenesLine(ReferenceDictionary refDict, String s) {
		String[] fields = s.split("\t");
		TranscriptInfoBuilder result = new TranscriptInfoBuilder();
		result.setAccession(fields[0]);

		int chr = refDict.contigID.get(fields[1].substring(3));

		result.setStrand(fields[2].charAt(0));
		GenomeInterval txRegion = new GenomeInterval(refDict, '+', chr, Integer.parseInt(fields[3]) + 1,
				Integer.parseInt(fields[4]), PositionType.ONE_BASED);
		result.setTxRegion(txRegion);
		GenomeInterval cdsRegion = new GenomeInterval(refDict, '+', chr, Integer.parseInt(fields[5]) + 1,
				Integer.parseInt(fields[6]), PositionType.ONE_BASED);
		result.setCdsRegion(cdsRegion);

		int exonCount = Integer.parseInt(fields[7]);
		String[] startFields = fields[8].split(",");
		String[] endFields = fields[9].split(",");
		for (int i = 0; i < exonCount; ++i) {
			GenomeInterval exonRegion = new GenomeInterval(refDict, '+', chr, Integer.parseInt(startFields[i]) + 1,
					Integer.parseInt(endFields[i]));
			result.addExonRegion(exonRegion);
		}

		return result;
	}

}
