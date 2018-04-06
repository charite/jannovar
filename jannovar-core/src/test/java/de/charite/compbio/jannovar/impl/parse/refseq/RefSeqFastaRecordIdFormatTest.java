package de.charite.compbio.jannovar.impl.parse.refseq;

import org.junit.Test;

import static de.charite.compbio.jannovar.impl.parse.refseq.RefSeqFastaRecordIdFormat.DEFAULT_FORMAT;
import static de.charite.compbio.jannovar.impl.parse.refseq.RefSeqFastaRecordIdFormat.INTERIM_RELEASE_201701_FORMAT;
import static de.charite.compbio.jannovar.impl.parse.refseq.RefSeqFastaRecordIdFormat.UNKNOWN_FORMAT;
import static de.charite.compbio.jannovar.impl.parse.refseq.RefSeqFastaRecordIdFormat.detect;
import static de.charite.compbio.jannovar.impl.parse.refseq.RefSeqFastaRecordIdFormat.extractAccession;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.Assert.assertEquals;

/** Tests for {@link RefSeqFastaRecordIdFormat}. */
public class RefSeqFastaRecordIdFormatTest {

	@Test
	public void detectOk() throws Exception {
		assertEquals(UNKNOWN_FORMAT, detect(null));
		assertEquals(UNKNOWN_FORMAT, detect(""));
		assertEquals(DEFAULT_FORMAT, detect("gi|66932946|ref|NM_000014.4| Homo sapiens alpha-2-macroglobulin (A2M), mRNA"));
		assertEquals(INTERIM_RELEASE_201701_FORMAT, detect("NM_000014.5"));
		assertEquals(UNKNOWN_FORMAT, detect("unknown"));
	}

	@Test
	public void extractAccessionOk() {
		assertEquals(empty(), extractAccession(null));
		assertEquals(empty(), extractAccession(""));
		assertEquals(of("NM_000014.4"), extractAccession("gi|66932946|ref|NM_000014.4|"));
		assertEquals(of("NM_000014.4"), extractAccession("NM_000014.4"));
	}

}