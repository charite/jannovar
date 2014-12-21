package jannovar.impl.parse.gff;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

// TODO(mjaeger): Add support for RefSeq IDs?
// TODO(holtgrem): Already existent here?

/**
 * The {@link RNA2GeneIDMapper} generates integer gene ids from given Gennames etc.
 *
 * Ensembl gene ids look like <code>"ENSG00000000419"</code>. The following demonstrates the usage and result:
 *
 * <pre>
 * int geneID = RNA2GeneIDMapper.getGeneID(&quot;ENSG00000000419&quot;);
 * System.out.println(&quot;geneID=&quot; + geneID); // prints &quot;geneID=419&quot;
 * </pre>
 *
 * @author Marten Jaeger <marten.jaeger@charite.de>
 */
public final class RNA2GeneIDMapper {

	private static final String ENSEMBL_REGEX = "ENS[MUS]*G0+([0-9]+)";
	private static final Pattern ENSEMBL_PATTERN = Pattern.compile(ENSEMBL_REGEX);

	private static final String REFSEQ_REGEX = "gene([0-9]+)";
	private static final Pattern REFSEQ_PATTERN = Pattern.compile(REFSEQ_REGEX);

	/**
	 * Returns a integer representation of the gene identifier or '-1' if no valid integer identifier could be
	 * generated.
	 *
	 * @param geneName
	 *            A gene identifier (e.g. Ensembl <code>"ENSG00000000419"</code>)
	 * @return A integer representation (e.g. <code>419</code>)
	 */
	public static int getGeneID(String geneName) {
		// check for Ensembl genename
		Matcher matcher;
		matcher = ENSEMBL_PATTERN.matcher(geneName);
		if (matcher.matches())
			return Integer.parseInt(matcher.group(1));

		matcher = REFSEQ_PATTERN.matcher(geneName);
		if (matcher.matches())
			return Integer.parseInt(matcher.group(1));
		return -1;
	}

}
