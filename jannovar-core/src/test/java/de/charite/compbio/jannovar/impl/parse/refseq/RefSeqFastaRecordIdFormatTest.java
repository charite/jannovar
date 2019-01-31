package de.charite.compbio.jannovar.impl.parse.refseq;

import static de.charite.compbio.jannovar.impl.parse.refseq.RefSeqFastaRecordIdFormat.*;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

/** Tests for {@link RefSeqFastaRecordIdFormat}. */
public class RefSeqFastaRecordIdFormatTest {

  @Test
  public void detectOk() throws Exception {
    assertEquals(UNKNOWN_FORMAT, detect(null));
    assertEquals(UNKNOWN_FORMAT, detect(""));
    assertEquals(
        GI_REF_FORMAT,
        detect("gi|66932946|ref|NM_000014.4| Homo sapiens alpha-2-macroglobulin (A2M), mRNA"));
    assertEquals(ACCESSION_FORMAT, detect("NM_000014.5"));
    assertEquals(UNKNOWN_FORMAT, detect("unknown"));
  }

  @Test
  public void extractAccessionOk() {
    assertEquals(empty(), extractAccession(null));
    assertEquals(empty(), extractAccession(""));
    assertEquals(of("NM_000014.4"), extractAccession("gi|66932946|ref|NM_000014.4|"));
    assertEquals(of("NM_000014.4"), extractAccession("ref|NM_000014.4|"));
    assertEquals(of("NM_000014.4"), extractAccession("NM_000014.4"));
  }
}
