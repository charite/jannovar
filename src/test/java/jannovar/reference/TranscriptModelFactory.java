package jannovar.reference;

import jannovar.common.Constants;

/**
 * Allows the easy creation of transcript models from knownGenes.txt.gz lines.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class TranscriptModelFactory {

	/**
	 * Helper function to parse a knownGenes.txt.gz line into a TranscriptModel.
	 *
	 * @param s
	 *            The knownGeneList line to parse.
	 */
	public static TranscriptModel parseKnownGenesLine(String s) {
		String[] fields = s.split("\t");
		TranscriptModel result = TranscriptModel.createTranscriptModel();
		result.setAccessionNumber(fields[0]);
		String chrNum = fields[1].substring(3);
		if (chrNum.equals("X"))
			result.setChromosome(Constants.X_CHROMOSOME);
		else if (chrNum.equals("Y"))
			result.setChromosome(Constants.Y_CHROMOSOME);
		else if (chrNum.equals("M"))
			result.setChromosome(Constants.M_CHROMOSOME);
		else
			result.setChromosome((byte) Integer.parseInt(chrNum));
		result.setStrand(fields[2].charAt(0));
		result.setTranscriptionStart(Integer.parseInt(fields[3]) + 1); // knownGenes is 0-based
		result.setTranscriptionEnd(Integer.parseInt(fields[4]));
		result.setCdsStart(Integer.parseInt(fields[5]) + 1); // knownGenes is 0-based
		result.setCdsEnd(Integer.parseInt(fields[6]));
		result.setExonCount(Short.parseShort(fields[7]));
		String[] startFields = fields[8].split(",");
		int[] starts = new int[result.getExonCount()];
		for (int i = 0; i < result.getExonCount(); ++i)
			starts[i] = Integer.parseInt(startFields[i]) + 1; // knownGenes is 0-based
		String[] endFields = fields[9].split(",");
		int[] ends = new int[result.getExonCount()];
		for (int i = 0; i < result.getExonCount(); ++i)
			ends[i] = Integer.parseInt(endFields[i]);
		result.setExonStartsAndEnds(starts, ends);
		return result;
	}

}
