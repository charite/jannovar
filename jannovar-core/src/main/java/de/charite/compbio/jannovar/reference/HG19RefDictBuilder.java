package de.charite.compbio.jannovar.reference;

import de.charite.compbio.jannovar.data.ReferenceDictionary;
import de.charite.compbio.jannovar.data.ReferenceDictionaryBuilder;

/**
 * Builds a {@link ReferenceDictionary} for the hg19 release.
 *
 * This is a utility class that can be used in tests of Java code that uses the Jannovar library for the easy
 * construction of a {@link ReferenceDictionary}.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 * @author Marten Jaeger <marten.jaeger@charite.de>
 */
public class HG19RefDictBuilder {

	public static ReferenceDictionary build() {
		ReferenceDictionaryBuilder builder = new ReferenceDictionaryBuilder();

		builder.putContigLength(1, 249250621);
		builder.putContigLength(2, 243199373);
		builder.putContigLength(3, 198022430);
		builder.putContigLength(4, 191154276);
		builder.putContigLength(5, 180915260);
		builder.putContigLength(6, 171115067);
		builder.putContigLength(7, 159138663);
		builder.putContigLength(8, 146364022);
		builder.putContigLength(9, 141213431);
		builder.putContigLength(10, 135534747);
		builder.putContigLength(11, 135006516);
		builder.putContigLength(12, 133851895);
		builder.putContigLength(13, 115169878);
		builder.putContigLength(14, 107349540);
		builder.putContigLength(15, 102531392);
		builder.putContigLength(16, 90354753);
		builder.putContigLength(17, 81195210);
		builder.putContigLength(18, 78077248);
		builder.putContigLength(19, 59128983);
		builder.putContigLength(20, 63025520);
		builder.putContigLength(21, 48129895);
		builder.putContigLength(22, 51304566);
		builder.putContigLength(23, 155270560);
		builder.putContigLength(24, 59373566);
		builder.putContigLength(25, 16571);

		for (int i = 1; i < 23; ++i) {
			builder.putContigName(i, "" + i);
			builder.putContigID("" + i, i);
			builder.putContigID("chr" + i, i);
		}
		builder.putContigName(23, "X");
		builder.putContigID("X", 23);
		builder.putContigName(24, "Y");
		builder.putContigID("Y", 24);
		builder.putContigName(25, "M");
		builder.putContigID("M", 25);
		builder.putContigID("chrX", 23);
		builder.putContigID("chrY", 24);
		builder.putContigID("chrM", 25);

		return builder.build();
	}

}