/**
 * 
 */
package jannovar.gff;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The {@link RNA2GeneIDMapper} generates integer gene ids from given Gennames etc.
 * v0.1 only Ensemble gene ids are currently working
 * @author mjaeger
 * @version 0.1 (2013-07-12)
 */
public class RNA2GeneIDMapper {
	
	private static final String ensemblRegex = "ENS[MUS]*G0+([0-9]+)";
	private static final Pattern ensemblPattern = Pattern.compile(ensemblRegex);
	
	private static final String refseqRegex	= "gene([0-9]+)";
	private static final Pattern refseqPattern	= Pattern.compile(refseqRegex);
	
//	private static Matcher matcher;

	/**
	 * Returns a integer representation of the gene identifier or '-1' if no valid 
	 * integer identifier could be generated.<br>
	 * TODO add map for RefSeq...???
	 * @param geneName A gene identifier (e.g. Ensembl ENSG00000000419)
	 * @return A integer representation (e.g. 419)
	 */
	public static int getGeneID(String geneName){
		// check for Ensembl genename
		Matcher matcher;
		matcher = ensemblPattern.matcher(geneName);
		if(matcher.matches()){
			return Integer.parseInt(matcher.group(1));
		}
		matcher = refseqPattern.matcher(geneName);
		if(matcher.matches()){
			return Integer.parseInt(matcher.group(1));
		}
		return -1;
	}

}
