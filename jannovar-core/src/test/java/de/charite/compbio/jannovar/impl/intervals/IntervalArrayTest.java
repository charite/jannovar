package de.charite.compbio.jannovar.impl.intervals;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.charite.compbio.jannovar.impl.intervals.IntervalArray;
import de.charite.compbio.jannovar.impl.intervals.IntervalEndExtractor;

public class IntervalArrayTest {

	class Triple implements Comparable<Triple> {

		final int beginPos;
		final int endPos;
		final String text;

		Triple(int beginPos, int endPos, String text) {
			this.beginPos = beginPos;
			this.endPos = endPos;
			this.text = text;
		}

		@Override
		public String toString() {
			return "Triple [beginPos=" + beginPos + ", endPos=" + endPos + ", text=" + text + "]";
		}

		public int compareTo(Triple o) {
			final int result = (beginPos - o.beginPos);
			if (result != 0)
				return result;
			return (endPos - o.endPos);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + beginPos;
			result = prime * result + endPos;
			result = prime * result + ((text == null) ? 0 : text.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Triple other = (Triple) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (beginPos != other.beginPos)
				return false;
			if (endPos != other.endPos)
				return false;
			if (text == null) {
				if (other.text != null)
					return false;
			} else if (!text.equals(other.text))
				return false;
			return true;
		}

		private IntervalArrayTest getOuterType() {
			return IntervalArrayTest.this;
		}

	}

	class TripleEndExtractor implements IntervalEndExtractor<Triple> {

		public int getBegin(Triple triple) {
			return triple.beginPos;
		}

		public int getEnd(Triple triple) {
			return triple.endPos;
		}

	}

	ArrayList<Triple> getList1() {
		ArrayList<Triple> lst = new ArrayList<Triple>();

		lst.add(new Triple(1, 4, "a"));
		lst.add(new Triple(5, 9, "b"));
		lst.add(new Triple(4, 8, "c"));
		lst.add(new Triple(5, 7, "d"));
		lst.add(new Triple(16, 20, "e"));
		lst.add(new Triple(11, 16, "f"));
		lst.add(new Triple(30, 67, "g"));

		return lst;
	}

	ArrayList<Triple> getList2() {
		ArrayList<Triple> lst = new ArrayList<Triple>();

		lst.add(new Triple(1, 9, "a"));
		lst.add(new Triple(2, 4, "b"));
		lst.add(new Triple(5, 8, "d"));
		lst.add(new Triple(4, 12, "c"));
		lst.add(new Triple(7, 13, "e"));
		lst.add(new Triple(9, 20, "f"));
		lst.add(new Triple(16, 20, "g"));
		lst.add(new Triple(17, 21, "h"));

		lst.add(new Triple(26, 31, "i"));
		lst.add(new Triple(27, 30, "j"));

		return lst;

	}

	ArrayList<Triple> getList3() {
		ArrayList<Triple> lst = new ArrayList<Triple>();

		lst.add(new Triple(1, 9, "a"));
		lst.add(new Triple(2, 4, "b"));
		lst.add(new Triple(4, 15, "c"));
		lst.add(new Triple(5, 8, "d"));
		lst.add(new Triple(7, 13, "e"));
		lst.add(new Triple(9, 23, "f"));
		lst.add(new Triple(16, 20, "g"));
		lst.add(new Triple(17, 21, "h"));
		lst.add(new Triple(29, 34, "i"));
		lst.add(new Triple(30, 33, "j"));

		return lst;

	}

	ArrayList<Triple> getList4() {
		ArrayList<Triple> lst = new ArrayList<Triple>();

		lst.add(new Triple(0, 11, "a"));
		lst.add(new Triple(15, 36, "b"));

		return lst;
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testSearchPub1() {
		IntervalArray<Triple> tree = new IntervalArray<Triple>(getList1(), new TripleEndExtractor());
		IntervalArray<Triple>.QueryResult res = tree.findOverlappingWithInterval(1, 2);

		Assert.assertEquals(1, res.getEntries().size());
		Assert.assertEquals(new Triple(1, 4, "a"), res.getEntries().get(0));
	}

	@Test
	public void testSearchPub2() {
		IntervalArray<Triple> tree = new IntervalArray<Triple>(getList2(), new TripleEndExtractor());
		IntervalArray<Triple>.QueryResult res = tree.findOverlappingWithInterval(13, 16);

		Assert.assertEquals(1, res.getEntries().size());
		Assert.assertEquals(new Triple(9, 20, "f"), res.getEntries().get(0));
	}

	@Test
	public void testSearchPub3() {
		IntervalArray<Triple> tree = new IntervalArray<Triple>(getList3(), new TripleEndExtractor());
		IntervalArray<Triple>.QueryResult res = tree.findOverlappingWithInterval(30, 31);

		Assert.assertEquals(2, res.getEntries().size());
		Assert.assertEquals(new Triple(29, 34, "i"), res.getEntries().get(0));
		Assert.assertEquals(new Triple(30, 33, "j"), res.getEntries().get(1));
	}

	@Test
	public void testSearch1() {
		IntervalArray<Triple> tree = new IntervalArray<Triple>(getList1(), new TripleEndExtractor());
		IntervalArray<Triple>.QueryResult res = tree.findOverlappingWithInterval(1, 3);

		Assert.assertEquals(1, res.getEntries().size());
		Assert.assertEquals(new Triple(1, 4, "a"), res.getEntries().get(0));
	}

	@Test
	public void testSearch2a() {
		IntervalArray<Triple> tree = new IntervalArray<Triple>(getList1(), new TripleEndExtractor());
		IntervalArray<Triple>.QueryResult res = tree.findOverlappingWithInterval(6, 8);

		Assert.assertEquals(3, res.getEntries().size());
		Assert.assertEquals(new Triple(4, 8, "c"), res.getEntries().get(0));
		Assert.assertEquals(new Triple(5, 7, "d"), res.getEntries().get(1));
		Assert.assertEquals(new Triple(5, 9, "b"), res.getEntries().get(2));
	}

	@Test
	public void testSearch2b() {
		IntervalArray<Triple> tree = new IntervalArray<Triple>(getList1(), new TripleEndExtractor());
		IntervalArray<Triple>.QueryResult res = tree.findOverlappingWithInterval(11, 13);

		Assert.assertEquals(1, res.getEntries().size());
		Assert.assertEquals(new Triple(11, 16, "f"), res.getEntries().get(0));
	}

	// Tests not finding any interval
	@Test
	public void testSearch3a() {
		IntervalArray<Triple> tree = new IntervalArray<Triple>(getList1(), new TripleEndExtractor());
		IntervalArray<Triple>.QueryResult res = tree.findOverlappingWithInterval(20, 21);

		Assert.assertEquals(0, res.getEntries().size());
		Assert.assertEquals(new Triple(16, 20, "e"), res.getLeft());
		Assert.assertEquals(new Triple(30, 67, "g"), res.getRight());
	}

	// Tests not finding an interval but getting the right neighbor
	@Test
	public void testSearch3d() {
		IntervalArray<Triple> tree = new IntervalArray<Triple>(getList1(), new TripleEndExtractor());
		IntervalArray<Triple>.QueryResult res = tree.findOverlappingWithInterval(512, 513);

		Assert.assertEquals(0, res.getEntries().size());
		Assert.assertEquals(new Triple(30, 67, "g"), res.getLeft());
		Assert.assertEquals(null, res.getRight());
	}

	// Tests not finding an interval but getting the right neighbor
	@Test
	public void testSearch3e() {
		IntervalArray<Triple> tree = new IntervalArray<Triple>(getList1(), new TripleEndExtractor());
		IntervalArray<Triple>.QueryResult res = tree.findOverlappingWithInterval(69, 70);

		Assert.assertEquals(0, res.getEntries().size());
		Assert.assertEquals(new Triple(30, 67, "g"), res.getLeft());
		Assert.assertEquals(null, res.getRight());
	}

	// Tests median
	@Test
	public void testSearch100() {
		IntervalArray<Triple> tree = new IntervalArray<Triple>(getList4(), new TripleEndExtractor());
		IntervalArray<Triple>.QueryResult res = tree.findOverlappingWithInterval(5, 6);

		Assert.assertEquals(1, res.getEntries().size());
		Assert.assertEquals(new Triple(0, 11, "a"), res.getEntries().get(0));
	}

	// Tests median
	@Test
	public void testSearch101() {
		IntervalArray<Triple> tree = new IntervalArray<Triple>(getList4(), new TripleEndExtractor());
		IntervalArray<Triple>.QueryResult res = tree.findOverlappingWithInterval(25, 26);

		Assert.assertEquals(1, res.getEntries().size());
		Assert.assertEquals(new Triple(15, 36, "b"), res.getEntries().get(0));
	}

}