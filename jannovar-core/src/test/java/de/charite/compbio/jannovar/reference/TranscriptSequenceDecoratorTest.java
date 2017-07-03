package de.charite.compbio.jannovar.reference;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for {@link TranscriptSequenceDecorator}.
 */
public class TranscriptSequenceDecoratorTest {

	private static final String[] CODONS = { "ATG", "ATC", "AGA", "GCT", "TGA" };

	private static final int START_LAST_CODON = 12;

	private static final TranscriptModel MODEL = getTestTranscriptModel();

	@Test
	public void testGetCodonAt() throws Exception {
		TranscriptSequenceDecorator decorator = new TranscriptSequenceDecorator(MODEL);
		Assert.assertEquals(CODONS[CODONS.length - 1], decorator.getCodonAt(tx(START_LAST_CODON), cds(START_LAST_CODON)));
		Assert.assertEquals(CODONS[0], decorator.getCodonAt(tx(0), cds(0)));
	}

	@Test
	public void testGetCodonsStartingFrom() throws Exception {
		TranscriptSequenceDecorator decorator = new TranscriptSequenceDecorator(MODEL);
		Assert.assertEquals(CODONS[0] + CODONS[1], decorator.getCodonsStartingFrom(tx(0), cds(0), 2));
		Assert.assertEquals(CODONS[CODONS.length - 2] + CODONS[CODONS.length - 1],
				decorator.getCodonsStartingFrom(tx(9), cds(9), 2));
	}

	private static TranscriptModel getTestTranscriptModel() {
		String sequence = Joiner.on("").join(CODONS);
		return new TranscriptModel("TEST", "TEST", new GenomeInterval(null, Strand.FWD, 1, 0, sequence.length()),
				new GenomeInterval(null, Strand.FWD, 1, 0, sequence.length()), ImmutableList.<GenomeInterval>builder().build(),
				sequence, "TEST", 1);
	}

	private static TranscriptPosition tx(int pos) {
		return new TranscriptPosition(MODEL, pos);
	}

	private static CDSPosition cds(int pos) {
		return new CDSPosition(MODEL, pos);
	}

}