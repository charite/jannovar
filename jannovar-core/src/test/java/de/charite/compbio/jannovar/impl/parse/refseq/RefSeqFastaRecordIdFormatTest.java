package de.charite.compbio.jannovar.impl.parse.refseq;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static de.charite.compbio.jannovar.impl.parse.refseq.RefSeqFastaRecordIdFormat.*;
import static java.util.Optional.empty;
import static java.util.Optional.of;

/**
 * Tests for {@link RefSeqFastaRecordIdFormat}.
 */
public class RefSeqFastaRecordIdFormatTest {

	@Test
	public void detectOk() throws Exception {
		Assertions.assertEquals(UNKNOWN_FORMAT, detect(null));
		Assertions.assertEquals(UNKNOWN_FORMAT, detect(""));
		Assertions.assertEquals(GI_REF_FORMAT, detect("gi|66932946|ref|NM_000014.4| Homo sapiens alpha-2-macroglobulin (A2M), mRNA"));
		Assertions.assertEquals(ACCESSION_FORMAT, detect("NM_000014.5"));
		Assertions.assertEquals(UNKNOWN_FORMAT, detect("unknown"));
	}

	@Test
	public void extractAccessionOk() {
		Assertions.assertEquals(empty(), extractAccession(null));
		Assertions.assertEquals(empty(), extractAccession(""));
		Assertions.assertEquals(of("NM_000014.4"), extractAccession("gi|66932946|ref|NM_000014.4|"));
		Assertions.assertEquals(of("NM_000014.4"), extractAccession("ref|NM_000014.4|"));
		Assertions.assertEquals(of("NM_000014.4"), extractAccession("NM_000014.4"));
	}

}
