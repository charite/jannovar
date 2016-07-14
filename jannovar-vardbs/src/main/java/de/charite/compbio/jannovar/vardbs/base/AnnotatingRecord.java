package de.charite.compbio.jannovar.vardbs.base;

/**
 * Helper for packing allele number together with <code>RecordType</code>.
 */
public class AnnotatingRecord<RecordType> {

	/** The annotating record */
	private final RecordType record;
	/** Allele number in the record */
	private final int allelNo;

	public AnnotatingRecord(RecordType record, int allelNo) {
		this.record = record;
		this.allelNo = allelNo;
	}

	public RecordType getRecord() {
		return record;
	}

	public int getAlleleNo() {
		return allelNo;
	}

	@Override
	public String toString() {
		return "AnnotatingRecord [record=" + record + ", allelNo=" + allelNo + "]";
	}

}
