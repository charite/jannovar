package de.charite.compbio.jannovar.reference;

import static org.junit.Assert.assertEquals;

import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;

public class AlignmentTest {

	// NNNNNNNNNN
	// NNNNNNNNNN
	Alignment ungapped;

	// NNNNN-NN--
	// --NN-NNNNN
	Alignment overlap;

	// NNNNNNNNNN
	// --NNNNNN--
	Alignment refIsLonger;

	// --NNNNNN--
	// NNNNNNNNNN
	Alignment qryIsLonger;

	@Before public void setUp() {
		ungapped = new Alignment(ImmutableList.of(new Anchor(0, 0), new Anchor(10, 10)),
			ImmutableList.of(new Anchor(0, 0), new Anchor(10, 10)));

		overlap = new Alignment(ImmutableList
			.of(new Anchor(0, 0), new Anchor(5, 5), new Anchor(6, 5), new Anchor(8, 7),
				new Anchor(10, 7)), ImmutableList
			.of(new Anchor(0, 0), new Anchor(2, 0), new Anchor(4, 2), new Anchor(5, 2),
				new Anchor(10, 7)));

		refIsLonger = new Alignment(ImmutableList.of(new Anchor(0, 0), new Anchor(10, 10)),
			ImmutableList
				.of(new Anchor(0, 0), new Anchor(2, 0), new Anchor(8, 6), new Anchor(10, 6)));

		qryIsLonger = new Alignment(ImmutableList
			.of(new Anchor(0, 0), new Anchor(2, 0), new Anchor(8, 6), new Anchor(10, 6)),
			ImmutableList.of(new Anchor(0, 0), new Anchor(10, 10)));
	}

	@Test public void refLeadingGapLength() {
		assertEquals(0, ungapped.refLeadingGapLength());
		assertEquals(0, overlap.refLeadingGapLength());
		assertEquals(0, refIsLonger.refLeadingGapLength());
		assertEquals(2, qryIsLonger.refLeadingGapLength());
	}

	@Test public void refTrailingGapLength() {
		assertEquals(0, ungapped.refTrailingGapLength());
		assertEquals(2, overlap.refTrailingGapLength());
		assertEquals(0, refIsLonger.refTrailingGapLength());
		assertEquals(2, qryIsLonger.refTrailingGapLength());
	}

	@Test public void qryLeadingGapLength() {
		assertEquals(0, ungapped.qryLeadingGapLength());
		assertEquals(2, overlap.qryLeadingGapLength());
		assertEquals(2, refIsLonger.qryLeadingGapLength());
		assertEquals(0, qryIsLonger.qryLeadingGapLength());
	}

	@Test public void qryTrailingGapLength() {
		assertEquals(0, ungapped.qryTrailingGapLength());
		assertEquals(0, overlap.qryTrailingGapLength());
		assertEquals(2, refIsLonger.qryTrailingGapLength());
		assertEquals(0, qryIsLonger.qryTrailingGapLength());
	}

	@Test public void projectRefToQuery() {
		for (int i = 0; i < 10; ++i) {
			assertEquals(0, ungapped.projectRefToQry(0));
		}

		assertEquals(0, overlap.projectRefToQry(0));
		assertEquals(0, overlap.projectRefToQry(1));
		assertEquals(0, overlap.projectRefToQry(2));
		assertEquals(1, overlap.projectRefToQry(3));
		assertEquals(2, overlap.projectRefToQry(4));
		assertEquals(3, overlap.projectRefToQry(5));
		assertEquals(4, overlap.projectRefToQry(6));
		assertEquals(7, overlap.projectRefToQry(7));

		assertEquals(0, refIsLonger.projectRefToQry(0));
		assertEquals(0, refIsLonger.projectRefToQry(1));
		assertEquals(0, refIsLonger.projectRefToQry(2));
		assertEquals(1, refIsLonger.projectRefToQry(3));
		assertEquals(2, refIsLonger.projectRefToQry(4));
		assertEquals(3, refIsLonger.projectRefToQry(5));
		assertEquals(4, refIsLonger.projectRefToQry(6));
		assertEquals(5, refIsLonger.projectRefToQry(7));
		assertEquals(6, refIsLonger.projectRefToQry(8));
		assertEquals(6, refIsLonger.projectRefToQry(9));
		assertEquals(6, refIsLonger.projectRefToQry(10));

		assertEquals(2, qryIsLonger.projectRefToQry(0));
		assertEquals(3, qryIsLonger.projectRefToQry(1));
		assertEquals(4, qryIsLonger.projectRefToQry(2));
		assertEquals(5, qryIsLonger.projectRefToQry(3));
		assertEquals(6, qryIsLonger.projectRefToQry(4));
		assertEquals(7, qryIsLonger.projectRefToQry(5));
		assertEquals(10, qryIsLonger.projectRefToQry(6));
	}

	@Test public void projectQueryToRef() {
		for (int i = 0; i < 10; ++i) {
			assertEquals(0, ungapped.projectQryToRef(0));
		}

		assertEquals(2, overlap.projectQryToRef(0));
		assertEquals(3, overlap.projectQryToRef(1));
		assertEquals(5, overlap.projectQryToRef(2));
		assertEquals(5, overlap.projectQryToRef(3));
		assertEquals(6, overlap.projectQryToRef(4));
		assertEquals(7, overlap.projectQryToRef(5));
		assertEquals(7, overlap.projectQryToRef(6));
		assertEquals(7, overlap.projectQryToRef(7));

		assertEquals(2, refIsLonger.projectQryToRef(0));
		assertEquals(3, refIsLonger.projectQryToRef(1));
		assertEquals(4, refIsLonger.projectQryToRef(2));
		assertEquals(5, refIsLonger.projectQryToRef(3));
		assertEquals(6, refIsLonger.projectQryToRef(4));
		assertEquals(7, refIsLonger.projectQryToRef(5));
		assertEquals(10, refIsLonger.projectQryToRef(6));

		assertEquals(0, qryIsLonger.projectQryToRef(0));
		assertEquals(0, qryIsLonger.projectQryToRef(1));
		assertEquals(0, qryIsLonger.projectQryToRef(2));
		assertEquals(1, qryIsLonger.projectQryToRef(3));
		assertEquals(2, qryIsLonger.projectQryToRef(4));
		assertEquals(3, qryIsLonger.projectQryToRef(5));
		assertEquals(4, qryIsLonger.projectQryToRef(6));
		assertEquals(5, qryIsLonger.projectQryToRef(7));
		assertEquals(6, qryIsLonger.projectQryToRef(8));
		assertEquals(6, qryIsLonger.projectQryToRef(9));
		assertEquals(6, qryIsLonger.projectQryToRef(10));
	}

}
