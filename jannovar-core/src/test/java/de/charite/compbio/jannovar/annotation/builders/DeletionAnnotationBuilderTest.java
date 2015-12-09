package de.charite.compbio.jannovar.annotation.builders;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.ImmutableSortedSet;

import de.charite.compbio.jannovar.annotation.Annotation;
import de.charite.compbio.jannovar.annotation.AnnotationLocation;
import de.charite.compbio.jannovar.annotation.InvalidGenomeVariant;
import de.charite.compbio.jannovar.annotation.VariantEffect;
import de.charite.compbio.jannovar.data.ReferenceDictionary;
import de.charite.compbio.jannovar.reference.GenomePosition;
import de.charite.compbio.jannovar.reference.GenomeVariant;
import de.charite.compbio.jannovar.reference.HG19RefDictBuilder;
import de.charite.compbio.jannovar.reference.PositionType;
import de.charite.compbio.jannovar.reference.Strand;
import de.charite.compbio.jannovar.reference.TranscriptModel;
import de.charite.compbio.jannovar.reference.TranscriptModelBuilder;
import de.charite.compbio.jannovar.reference.TranscriptModelFactory;

public class DeletionAnnotationBuilderTest {

	/** this test uses this static hg19 reference dictionary */
	static final ReferenceDictionary refDict = HG19RefDictBuilder.build();

	/** transcript on forward strand */
	TranscriptModelBuilder builderForward;
	/** transcript on reverse strand */
	TranscriptModelBuilder builderReverse;
	/** transcript info on forward strand */
	TranscriptModel infoForward;
	/** transcript info on reverse strand */
	TranscriptModel infoReverse;

	@Before
	public void setUp() {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001anx.3	chr1	+	6640062	6649340	6640669	6649272	11	6640062,6640600,6642117,6645978,6646754,6647264,6647537,6648119,6648337,6648815,6648975,	6640196,6641359,6642359,6646090,6646847,6647351,6647692,6648256,6648502,6648904,6649340,	P10074	uc001anx.3");
		this.builderForward
				.setSequence("cgtcacgtccggcgcggagacggtggagtctccgcactgtcggcggggtacgcatagccgggcactaggttcgtgggctgtggaggcgacggagcagggggccagtggggccagctcagggaggacctgcctgggagctttctcttgcataccctcgcttaggctggccggggtgtcacttctgcctccctgccctccagaccatggacggctccttcgtccagcacagtgtgagggttctgcaggagctcaacaagcagcgggagaagggccagtactgcgacgccactctggacgtggggggcctggtgtttaaggcacactggagtgtccttgcctgctgcagtcactttttccagagcctctacggggatggctcagggggcagtgtcgtcctccctgctggcttcgctgagatctttggcctcttgttggactttttctacactggtcacctcgctctcacctcagggaaccgggatcaggtgctcctggcagccagggagttgcgagtgccagaggccgtagagctgtgccagagcttcaagcccaaaacttcagtgggacaggcagcaggtggccagagtgggctggggccccctgcctcccagaatgtgaacagccacgtcaaggagccggcaggcttggaagaagaggaagtttcgaggactctgggtctagtccccagggatcaggagcccagaggcagtcatagtcctcagaggccccagctccattccccagctcagagtgagggcccctcctccctctgtgggaaactgaagcaggccttgaagccttgtccccttgaggacaagaaacccgaggactgcaaagtgcccccaaggcccttagaggctgaaggtgcccagctgcagggcggcagtaatgagtgggaagtggtggttcaagtggaggatgatggggatggcgattacatgtctgagcctgaggctgtgctgaccaggaggaagtcaaatgtaatccgaaagccctgtgcagctgagccagccctgagcgcgggctccctagcagctgagcctgctgagaacagaaaaggtacagcggtgccggtcgaatgccccacatgtcataaaaagttcctcagcaaatattatctaaaagtccacaacaggaaacatactggggagaaaccctttgagtgtcccaaatgtgggaagtgttactttcggaaggagaacctcctggagcatgaagcccggaattgcatgaaccgctcggaacaggtcttcacgtgctctgtgtgccaggagacattccgccgaaggatggagctgcgggtgcacatggtgtctcacacaggggagatgccctacaagtgttcctcctgctcccagcagttcatgcagaagaaggacttgcagagccacatgatcaaacttcatggagcccccaagccccatgcatgccccacctgtgccaagtgcttcctgtctcggacagagctgcagctgcatgaagctttcaagcaccgtggtgagaagctgtttgtgtgtgaggagtgtgggcaccgggcctcgagccggaatggcctgcagatgcacatcaaggccaagcacaggaatgagaggccacacgtatgtgagttctgcagccacgccttcacccaaaaggccaatctcaacatgcacctgcgcacacacacgggtgagaagcccttccagtgccacctctgtggcaagaccttccgaacccaagccagcctggacaagcacaaccgcacccacaccggggaaaggcccttcagttgcgagttctgtgaacagcgcttcactgagaaggggcccctcctgaggcacgtggccagccgccatcaggagggccggccccacttctgccagatatgcggcaagaccttcaaagccgtggagcaactgcgtgtgcacgtcagacggcacaagggggtgaggaagtttgagtgcaccgagtgtggctacaagtttacccgacaggcccacctgcggaggcacatggagatccacgaccgggtagagaactacaacccgcggcagcgcaagctccgcaacctgatcatcgaggacgagaagatggtggtggtggcgctgcagccgcctgcagagctggaggtgggctcggcggaggtcattgtggagtccctggcccagggcggcctggcctcccagctccccggccagagactgtgtgcagaggagagcttcaccggcccaggtgtcctggagccctccctcatcatcacagctgctgtccccgaggactgtgacacatagcccattctggccaccagagcccacttggccccacccctcaataaaccgtgtggctttggactctcgtaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("ZBTB48");
		this.infoForward = builderForward.build();
		// RefSeq: NM_005341.3

		this.builderReverse = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001bgu.3	chr1	-	23685940	23696357	23688461	23694498	4	23685940,23693534,23694465,23695858,	23689714,23693661,23694558,23696357,	Q9C0F3	uc001bgu.3");
		this.builderReverse
				.setSequence("aataagctgctatattctttttccatcacttccctctccaaggctacagcgagctgggagctcttccccacgcagaatgcctgctttccccagtgctcgacttccattgtctaattccctcatcctggctggggaaagggagagctgcgagtcctcccgttccgaggaactccagctgaatgcagcttagttgctggtggtttctcggccagcctctgtggtctcagggatctgcctatgagcctgtggtttctgagctgcctgcgagtctgaggcctcgggaatctgagtctttaggatcagcctacgatatctgggcttcgcctgcaagtctacgaattcgagatctacctgcgggtctgagacctccgggacctgcccgtgctctctagaatcttcctgaacgccaggtctgagagaacgctgcggctctggaacccgttcgcggtctctcaggttttggagacgacgatctagtggatcttttgcgggacaggagcgctgtctgctagctgcttttcctgctctctctccctggaggcgaacccttgtgctcgagatggcagccaccctgctcatggctgggtcccaggcacctgtgacgtttgaagatatggccatgtatctcacccgggaagaatggagacctctggacgctgcacagagggacctttaccgggatgttatgcaggagaattatggaaatgttgtctcactagattttgagatcaggagtgagaacgaggtaaatcccaagcaagagattagtgaagatgtacaatttgggactacatctgaaagacctgctgagaatgctgaggaaaatcctgaaagtgaagagggctttgaaagcggagataggtcagaaagacaatggggagatttaacagcagaagagtgggtaagctatcctctccaaccagtcactgatctacttgtccacaaagaagtccacacaggcatccgctatcatatatgttctcattgtggaaaggccttcagtcagatctcagaccttaatcgacatcagaagacccacactggagacagaccctataaatgttatgaatgtggaaaaggcttcagtcgcagctcacaccttattcagcatcaaagaacacatactggggagaggccttatgactgtaacgagtgtgggaaaagttttggaagaagttctcacctgattcagcatcagacaatccacactggagagaagcctcacaaatgtaatgagtgtggaaaaagtttctgccgtctctctcacctaatccaacaccaaaggacccacagtggtgagaaaccctatgagtgtgaggagtgtgggaaaagcttcagccggagctctcacctagctcagcaccagaggacccacacgggtgagaaaccttatgaatgtaacgaatgtggccgaggcttcagtgagagatctgatctcatcaaacactatcgagtccacacaggggagaggccctacaagtgtgatgagtgtgggaagaatttcagtcagaactccgaccttgtgcgtcatcgcagagcccacacgggagagaagccataccactgtaacgaatgtggggaaaatttcagccgcatctcacacttggttcagcaccagagaactcacactggagagaagccatatgaatgcaatgcttgtgggaaaagcttcagccggagctctcatctcatcacacaccagaaaattcacactggagagaagccttatgagtgtaatgagtgttggcgaagctttggtgaaaggtcagatctaattaaacatcagagaacccacacaggggagaagccctacgagtgtgtgcagtgtgggaaaggtttcacccagagctccaacctcatcacacatcaaagagttcacacgggagagaaaccttatgaatgtaccgaatgtgagaagagtttcagcaggagctcagctcttattaaacataagagagttcatacggactaagctgtaattatgatggctgagaaatgattcatttgaagatacaattttatttgatatcaatgaacgccctcaagactgagctgcttttatcatactctcctagttgtgggccacgatttaaaccatcagagatgacaagccatttgaaattctgaccctcagctttgggaatgttatctcctccaaaatggtgatttttattcactcaatgggttacttcattaaaagcagccccacaagtaactggaaatctgaagaccaggggacaaatgctggtgaatgcttaggcctggaaatggagtaaatctttcaatgttattttctcccatccttggcccaaggaactatgctaagtgaaacgtgggactgtaatagggtggtaatggctgctttggaaaaaggcaactagagactctgcctaaattgccacacctattcacacaccatagtagttgggcacacacatcttcccttccaaagggctttttccttgagttgctcatgcatttgtatcttttccatcttcctgagggcaagattttgcacgatgaaggcaatgattgtaacttttctccttctcattgtttctaattagctcctttaaagcttgcatctttgtgaaggctaactgaagatacggttggaaaggaaaaatgagacacaggtttggggaccaaggacccatcaatgatggtgactttagcagaagatgcccacagttattactgccattaatcagatttatgaattttctttggggatcactatagggaatattgtatagaaaatatcttcaagaaaagataggaccatcagtgacagttaagtgtaaggagcaagtggaattgagtccttcagggaaggaaccacagagtcccttcccaaggaatgtaggtcgtttctgtgttctttcccttctaatctttaagatcaactcttcctatcctgctaactctaagatttgataagggccacatcccagtgtttatcttagcttgcatcagggcatgtgtatgtacagtaatgtgtattcctgtggtttttctaatagaaactgaatttacagagacttagcatgttcttgggtgatgtgagtcatgtgacagaagtacagacataactccaatgtgagaaatgtccttttttcattatggaaaataatttaaacactagtgctttagtgtgcactctcctgtaaggtctgtctttgtacagagctaagcacttgtttgtatgtgtttgtcaattgtggaagataatgaccagacaaataggtcgattgtcctattctcagaatgaattatcttctatggtaatgaagaactctttggcttagtcagaaggaattaacgaacctcggtaggaatgtatttccatcctcccaccctacagatataagaggttaaaataacagttcgcccaatttaagcccagtagtgtcagttttcctaatctcagtccaggtaggaattaagaaatatctcaagtgttgatgctatccaagcatgttggggtggaagggaattggtgcccagaaaatgggactggagtgaggaatatcttttcttttgagagtacccccagtttatttctactgtgctttattgctactgttctttattgtgaatgttgtaacattttaaaaatgttttgccatagctttttaggacttggtgttaaaggagccagtggtctctctgggtgggtactataatgagttattgtgacccacagctgtgtgggaccacatcacttgttaataacacaacctttaaagtaacccatcttccaggggggttccttcatgttgccactcctttttaaggacaaactcaggcaaggagcatgtttttttgttatttacaaaatctagcagactgtgggtatccatattttaattgtcgggtgacacatgttcttggtaactaaactcaaatatgtcttttctcatatatgttgctgatggttttaataaatgtcaaagttctcctgttgcttctgtgagccactatgggtatcagcttgggagtggccatagatgaccgcatttccatgacctaactgtatttcacccccttttccttccctactgttcttgccccaccccaaccagttcctgctgctgcttttggcttcttggaggtgaagggcttaaaacaaggcttctaagcacccagctatctccatacatgaacaatctagctgggaaacttaagggacaagggccacaccagctgtctcctctttctgccaattgttgcccgtttgctgtgttgaactttgtatagaactcatgcatcagactcccttcactaatgctttttgcatgccttctgctcccaagtccctggctgcctctgcacatcccgtgaacactttgtgcctgttttctatggttgtggagaattaatgaacaaatcaatatgtagaacagttttccttatggtattggtcacagttatcctagtgtttgtattattctaacaatattctataattaaaaatataatttttaaagtca"
						.toUpperCase());
		this.builderReverse.setGeneSymbol("ZNF436");
		this.infoReverse = builderReverse.build();
		// RefSeq: NM_001077195.1
	}

	@Test
	public void testForwardUstream() throws InvalidGenomeVariant {
		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640061,
				PositionType.ZERO_BASED), "A", "");
		Annotation annotation1 = new DeletionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(null, annotation1.getAnnoLoc());
		Assert.assertEquals(null, annotation1.getCDSNTChange());
		Assert.assertEquals(null, annotation1.getProteinChange());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.UPSTREAM_GENE_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testForwardDownstream() throws InvalidGenomeVariant {
		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6649340,
				PositionType.ZERO_BASED), "A", "");
		Annotation annotation1 = new DeletionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(null, annotation1.getAnnoLoc());
		Assert.assertEquals(null, annotation1.getCDSNTChange());
		Assert.assertEquals(null, annotation1.getProteinChange());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.DOWNSTREAM_GENE_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testForwardIntergenic() throws InvalidGenomeVariant {
		// intergenic upstream
		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6639061,
				PositionType.ZERO_BASED), "A", "");
		Annotation annotation1 = new DeletionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(null, annotation1.getAnnoLoc());
		Assert.assertEquals(null, annotation1.getCDSNTChange());
		Assert.assertEquals(null, annotation1.getProteinChange());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.INTERGENIC_VARIANT), annotation1.getEffects());
		// intergenic downstream
		GenomeVariant change2 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6650340,
				PositionType.ZERO_BASED), "A", "");
		Annotation annotation2 = new DeletionAnnotationBuilder(infoForward, change2, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(null, annotation1.getAnnoLoc());
		Assert.assertEquals(null, annotation1.getCDSNTChange());
		Assert.assertEquals(null, annotation1.getProteinChange());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.INTERGENIC_VARIANT), annotation2.getEffects());
	}

	@Test
	public void testForwardTranscriptAblation() throws InvalidGenomeVariant {
		StringBuilder chars200 = new StringBuilder();
		for (int i = 0; i < 200; ++i)
			chars200.append("A");
		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640061,
				PositionType.ZERO_BASED), chars200.toString(), "");
		Annotation annotation1 = new DeletionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(AnnotationLocation.INVALID_RANK, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("-204_-70+65del", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("0?", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.TRANSCRIPT_ABLATION), annotation1.getEffects());
	}

	@Test
	public void testForwardIntronic() throws InvalidGenomeVariant {
		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6642106,
				PositionType.ZERO_BASED), "A", "");
		Annotation annotation1 = new DeletionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(1, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("691-11del", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.CODING_TRANSCRIPT_INTRON_VARIANT),
				annotation1.getEffects());
	}

	@Test
	public void testForwardFivePrimeUTR() throws InvalidGenomeVariant {
		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640072,
				PositionType.ZERO_BASED), "A", "");
		Annotation annotation1 = new DeletionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(0, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("-192del", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FIVE_PRIME_UTR_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testForwardThreePrimeUTR() throws InvalidGenomeVariant {
		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6649329,
				PositionType.ZERO_BASED), "A", "");
		Annotation annotation1 = new DeletionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(10, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("*59del", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.THREE_PRIME_UTR_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testForwardStartLoss() throws InvalidGenomeVariant {
		// Testing with some START_LOST scenarios.

		// Delete one base of start codon.
		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640669,
				PositionType.ZERO_BASED), "A", "");
		Annotation annotation1 = new DeletionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(1, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("1del", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("0?", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.START_LOST), annotation1.getEffects());

		// Delete chunk out of first exon, spanning start codon from the left.
		GenomeVariant change2 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640660,
				PositionType.ZERO_BASED), "CCCTCCAGACC", "");
		Annotation annotation2 = new DeletionAnnotationBuilder(infoForward, change2, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation2.getTranscript().getAccession());
		Assert.assertEquals(1, annotation2.getAnnoLoc().getRank());
		Assert.assertEquals("-9_2del", annotation2.getCDSNTChange().toHGVSString());
		Assert.assertEquals("0?", annotation2.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.START_LOST), annotation2.getEffects());

		// Delete chunk out of first exon, spanning start codon from the right.
		GenomeVariant change3 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640671,
				PositionType.ZERO_BASED), "GGACGGCTCCT", "");
		Annotation annotation3 = new DeletionAnnotationBuilder(infoForward, change3, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation3.getTranscript().getAccession());
		Assert.assertEquals(1, annotation3.getAnnoLoc().getRank());
		Assert.assertEquals("3_13del", annotation3.getCDSNTChange().toHGVSString());
		Assert.assertEquals("0?", annotation3.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.START_LOST), annotation3.getEffects());

		// Deletion from before transcript, reaching into the start codon.
		GenomeVariant change4 = new GenomeVariant(
				new GenomePosition(refDict, Strand.FWD, 1, 6640399, PositionType.ZERO_BASED),
				"TCTCACCAGGCCCTTCTTCACGACCCTGGCCCCCCATCCAGCATCCCCCCTGGCCAATCCAATATGGCCCCCGGCCCCCGGGAGGCTGTCAGTGTGTTCCAGCCCTCCGCGTGCACCCCTCACCCTGACCCAAGCCCTCGTGCTGATAAATATGATTATTTGAGTAGAGGCCAACTTCCCGTTTCTCTCTCTTGACTCCAGGAGCTTTCTCTTGCATACCCTCGCTTAGGCTGGCCGGGGTGTCACTTCTGCCTCCCTGCCCTCCAGACCA",
				"");
		Annotation annotation4 = new DeletionAnnotationBuilder(infoForward, change4, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation4.getTranscript().getAccession());
		Assert.assertEquals(AnnotationLocation.INVALID_RANK, annotation4.getAnnoLoc().getRank());
		Assert.assertEquals("-69-201_1del", annotation4.getCDSNTChange().toHGVSString());
		Assert.assertEquals("0?", annotation4.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.START_LOST), annotation4.getEffects());
	}

	@Test
	public void testForwardStopLoss() throws InvalidGenomeVariant {
		// Note that Mutalyzer has a different transcript sequence such that it does not report full loss for the cases
		// below.

		// Delete last base of stop codon, leads to complete loss of stop codon (different from Mutalyzer).
		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6649271,
				PositionType.ZERO_BASED), "A", "");
		Annotation annotation1 = new DeletionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(10, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("2067del", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(*689Tyrext*?)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FRAMESHIFT_VARIANT, VariantEffect.STOP_LOST),
				annotation1.getEffects());

		// Delete middle base of stop codon, leads to complete loss.
		GenomeVariant change2 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6649270,
				PositionType.ZERO_BASED), "A", "");
		Annotation annotation2 = new DeletionAnnotationBuilder(infoForward, change2, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation2.getTranscript().getAccession());
		Assert.assertEquals(10, annotation2.getAnnoLoc().getRank());
		Assert.assertEquals("2066del", annotation2.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(*689Cysext*?)", annotation2.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FRAMESHIFT_VARIANT, VariantEffect.STOP_LOST),
				annotation2.getEffects());

		// Delete first base of stop codon, leads to extension
		GenomeVariant change3 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6649269,
				PositionType.ZERO_BASED), "A", "");
		Annotation annotation3 = new DeletionAnnotationBuilder(infoForward, change3, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation3.getTranscript().getAccession());
		Assert.assertEquals(10, annotation3.getAnnoLoc().getRank());
		Assert.assertEquals("2065del", annotation3.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(*689Serext*?)", annotation3.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FRAMESHIFT_VARIANT, VariantEffect.STOP_LOST),
				annotation3.getEffects());

		// Delete two bases of stop codon.
		GenomeVariant change4 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6649269,
				PositionType.ZERO_BASED), "AT", "");
		Annotation annotation4 = new DeletionAnnotationBuilder(infoForward, change4, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation4.getTranscript().getAccession());
		Assert.assertEquals(10, annotation4.getAnnoLoc().getRank());
		Assert.assertEquals("2065_2066del", annotation4.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(*689Alaext*15)", annotation4.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FRAMESHIFT_TRUNCATION, VariantEffect.STOP_LOST),
				annotation4.getEffects());

		// Delete from before into the stop codon.
		GenomeVariant change5 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6649267,
				PositionType.ZERO_BASED), "CATAGCCC", "");
		Annotation annotation5 = new DeletionAnnotationBuilder(infoForward, change5, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation5.getTranscript().getAccession());
		Assert.assertEquals(10, annotation5.getAnnoLoc().getRank());
		Assert.assertEquals("2063_*3del", annotation5.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(*689Hisext*14)", annotation5.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FRAMESHIFT_TRUNCATION, VariantEffect.STOP_LOST),
				annotation5.getEffects());
	}

	@Test
	public void testForwardSplicing() throws InvalidGenomeVariant {
		// intronic splicing
		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6642116,
				PositionType.ZERO_BASED), "G", "");
		Annotation annotation1 = new DeletionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(1, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("691-1del", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("?", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.CODING_TRANSCRIPT_INTRON_VARIANT,
				VariantEffect.SPLICE_ACCEPTOR_VARIANT), annotation1.getEffects());

		// exonic splicing
		GenomeVariant change2 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6642117,
				PositionType.ZERO_BASED), "TGG", "");
		Annotation annotation2 = new DeletionAnnotationBuilder(infoForward, change2, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation2.getTranscript().getAccession());
		Assert.assertEquals(2, annotation2.getAnnoLoc().getRank());
		Assert.assertEquals("691_693del", annotation2.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Trp231del)", annotation2.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.INFRAME_DELETION, VariantEffect.SPLICE_REGION_VARIANT),
				annotation2.getEffects());
	}

	@Test
	public void testForwardFrameShiftDeletion() throws InvalidGenomeVariant {
		// The following case contains a shift in the nucleotide sequence.
		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6645988,
				PositionType.ZERO_BASED), "TGGGGAGAAA", "");
		Annotation annotation1 = new DeletionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(3, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("943_952del", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Gly315Profs*26)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FRAMESHIFT_TRUNCATION), annotation1.getEffects());
	}

	@Test
	public void testForwardNonFrameShiftDeletion() throws InvalidGenomeVariant {
		// clean (FS of begin position is 0) deletion of one codon, starting in intron (thus no "exon3" annotation is
		// generated).
		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6642114,
				PositionType.ZERO_BASED), "GAAACA", "");
		Annotation annotation1 = new DeletionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(AnnotationLocation.INVALID_RANK, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("691-3_693del", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Trp231del)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(
				ImmutableSortedSet.of(VariantEffect.INFRAME_DELETION, VariantEffect.SPLICE_ACCEPTOR_VARIANT),
				annotation1.getEffects());

		// deletion of three codons
		GenomeVariant change2 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6642126,
				PositionType.ZERO_BASED), "GTGGTTCAA", "");
		Annotation annotation2 = new DeletionAnnotationBuilder(infoForward, change2, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation2.getTranscript().getAccession());
		Assert.assertEquals(2, annotation2.getAnnoLoc().getRank());
		Assert.assertEquals("704_712del", annotation2.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Val235_Val237del)", annotation2.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.DISRUPTIVE_INFRAME_DELETION), annotation2.getEffects());

		// deletion of three codons, resulting in delins case
		GenomeVariant change3 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6642134,
				PositionType.ZERO_BASED), "AGTGGAGGA", "");
		Annotation annotation3 = new DeletionAnnotationBuilder(infoForward, change3, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation3.getTranscript().getAccession());
		Assert.assertEquals(2, annotation3.getAnnoLoc().getRank());
		Assert.assertEquals("708_716del", annotation3.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Gln236_Asp239delinsHis)", annotation3.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.DISRUPTIVE_INFRAME_DELETION), annotation3.getEffects());
	}

	@Test
	public void testRealWorldCase_uc010ock_3() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc010ock.3	chr1	-	17081128	17090975	17083726	17090975	15	17081128,17084012,17084248,17084436,17084960,17085297,17085560,17085763,17085970,17086383,17086646,17086848,17087245,17087449,17090908,	17083888,17084152,17084355,17084583,17085081,17085530,17085663,17085894,17086181,17086502,17086767,17086985,17087368,17087597,17090975,	Q2TV78-2	uc010ock.3");
		this.builderForward
				.setSequence("atggcgcctgccccagtcaccctgctggcccctggggcagcatcctcaatgtcttgcagccagcccgggcagcgctcgccatcgaatgacttccaggtgctccggggcacagagctacagcacctgctacatgcggtggtgcccgggccttggcaggaggatgtggcagatgctgaagagtgtgctggtcgctgtgggcccttaatggactgctgggcgttccactacaatgtgagcagccatggttgccaactgctgccatggactcaacactcgccccactcaaggctgtggcattctgggcgctgtgacctcttccaggagaaaggcgagtgggggtacatgcccacgctccggaatggcctggaagagaacttctgccgtaaccctgatggcgaccccggaggtccttggtgccacacaacagaccctgccgtgcgcttccagagctgcagcatcaaatcctgccgggtggccgcgtgtgtctggtgcaatggcgaggaataccgcggcgcggtagaccgcaccgagtcagggcgcgagtgccagcgctgggatcttcagcacccgcaccagcaccccttcgagccgggcaagttcctcgaccaaggtctggacgacaactattgccggaatcctgacggctccgagcggccatggtgctacactacggatccgcagatcgagcgagagttctgtgacctcccccgctgcgggtccgaggcacagccccgccaagaggccacaagtgtcagctgcttccgcgggaagggtgagggctaccggggcacagccaataccaccaccgcggcgtaccttgccagcgttgggacgcgcaaatcccacatcagcaccgatttacgccagaaaaatacgcgtgcaagtgaggtgggcgggggggcgggcgttgggacgtgctgctgcggagaccttcgggagaacttctgctggaacctcgacggctcagaggcgccctggtgcttcaccctgcggcccggcacgcgcgtgggcttttgctaccagatccggcgttgtacagacgacgtgcggccccaggactgctaccacggcgcgggggagcagtaccgcggcacggtcagcaagacccgcaagggtgtccagtgccagcgctggtccgctgagacgccgcacaagctgcaggccctaaccctggggcggcatgctttgatgtctgggaccagagcctggaaatggttgagactaccctgccacgattttgctcccgctcccgcctcggttcacatttacctccgaaccgcatgcacaactggaggagaacttctgccagacccagatggggatagccatgggccctggtgctacacgatggacccaaggaccccattcgactactgtgccctgcgacgctgcgaccaggtgcagtttgagaagtgtggcaagagggtggatcggctggatcagcgtcgttccaagctgcgcgtggctgggggccatccgggcaactcaccctggacagtcagcttgcggaatcgccatatgcctctcacgggctatgaggtatggttgggcaccctgttccagaacccacaacatggagagccaggcctacagcgggtcccagtagccaagatgctgtgtgggccctcaggctcccagcttgtcctgctcaagctggagagatctgtgaccctgaaccagcgtgtggccctgatctgcctgccgcctgaatggtatgtggtgcctccagggaccaagtgtgagattgcaggctggggtgagaccaaaggtacgggtaatgacacagtcctaaatgtggccttgctgaacgtcatctccaaccaggagtgtaacatcaagcaccgaggacatgtgcgggagagcgagatgtgcactgagggactgttggcccctgtgggggcctgtgagggtgactacgggggcccacttgcctgctttacccacaactgctgggtcctgaaaggaattagaatccccaaccgagtatgcacaaggtcgcgctggccagccgtcttcacgcgtgtctctgtgtttgtggactggattcacaaggtcatgagactgggttaggcccagccttgacgccatatgctttggggaggacaaaacttgtaagtacagtcaaggacaagacttgtactcaaggttgagatttaataaaattaatatttttactacttcaccaaggactttcttaaacgaaaatggtttttccccctgcaagtaaacagtaatgaagaagagaattattcctagtgcagtttgttttcatggtcttaatttttgctaagactccactgtttttgccttatcaatacaagtgccaacacagtgaaaaggcaaatatcatcttagtattactctgaaaatagttctgagctaatggcctactgaaaggaaaagagtggctcctgctattctattagacttattacaattatcttaagtattctttctaccctcctttaattgaatggaaacagggatggattggaagagctgtttttctcctttctttcccccggcaatatttaccatttaatgccacttactaacactcaaagaaacaaaaccaaacttctcaattgacagtgcagtgacccaacaaagacacgggttcttgaattcaaagtggagcaggagagacggtaaatacacatttactttaatatatatatatttattatttatgtgtttaaagcacaaattagtttggtaaaaaacatctcatgtctgttttatttccacatccctgagactgacaatgggatgcctatcaattaattcatttagagagccatacaccacaagaaacaaattatttgtcctctggagcttgtcacagggggatttttaaaaaaccattaaacagaaagacaactgtgcatcttagaaagataaaaggccaattcttcctctccggctgataggttcttaataatagtgatatctactaataaggtgttttacatagtgtaaagcatgttcacatacaaattacttagcctctttgagcctcagttttcttatatgtaaaactggattaatagtacattttgtgtttaaaaagataatgtatatgaagtgtttaccatttttgcttggcatctagttcagttctcagtaactgatgtggtggtggtggtggtcatagtagcagtaagatccgtagtaatagtagcagcagttgttttagaaattagtaactgaggcctggcaaagttaaaggctctttcattaacacccagaggggaagaaatgaagctggtcttcagaggcaggctattttcactctgtgtcccaaattttcccccctagaccgtttttatacttctggggcctcagaaaatattctcagctattctgttagcttgatctcctaccatctgagagtgggcttccttcaaacaaccaaatttccaggtatttctaaactgcccttcccctacaccattctttggttcagtatttcaagacccctaagagaaatggtacatttacatgtaagcacaggatagtgaagtatttacaacaagtgctttggagccagcaaatatgaatcagaatccagctttcctttcctacatacatgacattgggcagctaatttctaagattttacttctttatctatgaaagtggagtactagtacttgctctgtgcaactgtgatggttgttacatgaggtagcatctagaagcagcttgcacattgccagacacccagtggaaggtcaatgaatgactatttgaggactaactattacagaaatgtttactcttctgagtcctgatttctagtctcctggactaaataggttcactgttttcctcccggttcagtttccagacacatcacagaattataagaatattaaaaactcaggcttatacctacacaggattttctataaccctctttctgctttgagctcctaaaggtatttcatagaaaaatgaccttatttttaaatagagggggcagttgaaaatcagtgaacgggcctaccccctaatgatttttttctcagacctaattataataattagcattataaagtgctaattatctttggacacagaggacctgcacaccagagacagaggtccgcattaagtaaagtggatttcactttcttcagttgtgagatttctcttttttcttctttgtaatgatgcaaagatatatcttccaccaagcctcatttaaaagctttttccagttaaggaaactatctcttggccatccacagccagactgcatattgagattatggatattcaaagaaattgtctttcctttgtatattgtcataactttttgtgaaatgttcgttttatagttccaggccagcacctagaacctggctagaataaaaaactgcagaaatcatgagtttcttgtttggatgaaagagcacacctattaacaaatgatagacggctatcctactgtgagtcctgaaaactggtggtgtgattgttgaatgggttaggggtatagcagagaaactcagtgtgggctacatacaatttcagcttgaatcacacttaacagatcctctgttccaaccatttaaatttacaaagaagaaactaaggcacagaactacttgagaagagaagcagaattgaaaactagagctcctgattgttctcaaaataatt"
						.toUpperCase());
		this.builderForward.setGeneSymbol("MST1L");
		this.infoForward = builderForward.build();
		// RefSeq NM_001271733.1

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 17087543,
				PositionType.ZERO_BASED), "GCTGT", "");
		Annotation annotation1 = new DeletionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(1, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("119_123del", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Gln40Profs*18)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FRAMESHIFT_TRUNCATION), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc001idm_1() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory.parseKnownGenesLine(refDict,
				"uc001idm.1	chr1	-	247978101	247979031	247978101	247979031	1	247978101,	247979031,	Q8NHC5	uc001idm.1");
		this.builderForward
				.setSequence("atggcaaatctcacaatcgtgactgaatttatccttatggggttttctaccaataaaaatatgtgcattttgcattcgattctcttcttgttgatttatttgtgtgccctgatggggaatgtcctcattatcatgatcacaactttggaccatcatctccacacccccgtgtatttcttcttgaagaatctatctttcttggatctctgccttatttcagtcacggctcccaaatctatcgccaattctttgatacacaacaactccatttcattccttggctgtgtttcccaggtctttttgttgctttcttcagcatctgcagagctgctcctcctcacggtgatgtcctttgaccgctatactgctatatgtcaccctctgcactatgatgtcatcatggacaggagcacctgtgtccaaagagccactgtgtcttggctgtatgggggtctgattgctgtgatgcacacagctggcaccttctccttatcctactgtgggtccaacatggtccatcagttcttctgtgacattccccagttattagctatttcttgctcagaaaatttaataagagaaattgcactcatccttattaatgtagttttggatttctgctgttttattgtcatcatcattacctatgtccacgtcttctctacagtcaagaagatcccttccacagaaggccagtcaaaagcctactctatttgccttccacacttgctggttgtgttatttctttccactggattcattgcttatctgaagccagcttcagagtctccttctattttggatgctgtaatttctgtgttctacactatgctgcccccaacctttaatcccattatatacagtttgagaaacaaggccataaaggtggctctggggatgttgataaagggaaagctcaccaaaaagtaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("OR14A16");
		this.infoForward = builderForward.build();
		// RefSeq NM_001001966.1

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 247978543,
				PositionType.ZERO_BASED), "GAG", "");
		Annotation annotation1 = new DeletionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(0, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("488_490del", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Ser163del)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.DISRUPTIVE_INFRAME_DELETION), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc011azx_2() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc011azx.2	chr3	-	44540461	44552128	44540636	44544471	4	44540461,44544033,44544456,44552044,	44542126,44544160,44544548,44552128,	Q6ZMS4-2	uc011azx.2");
		this.builderForward
				.setSequence("gcaactggggaagctgggaggtaggcgcggggcggtcggctgcggtggcggcgtttggatgattgtctctcggcggcggagtcgctcaatgtacttctccagttcctacccttcctcaagtggggaactcaggagaccaagcaggggcaactgtacttcggatggtcaggccccaggatactgtggcgtatgaggacctgtctgaggactatactcagaagaaatggaaaggtctcgcactcagtcagagagccctgcactggaacatgatgctggaaaatgaccgtagcatggcttctttgggtaggaacatgatggagagttcagagctgactccgaagcaggaaatttttaaaggatcagagtcatctaatagcacatcagggggactctttggggtggttcctgggggaacagagactggagatgtttgtgaagataccttcaaagagttagaaggacaaccctcaaatgaagaagggagcagactagaaagtgatttcttggaaataatagatgaggataagaaaaaatccacaaaagacagatatgaggaatataaggaagttgaggaacatccacctctgtcttccagtcctgttgaacatgaaggagttttaaagggacagaaatcctatcgatgtgatgaatgtggcaaagctttttattggagttcgcacctcattggtcatcggagaatccacactggagagaaaccctatgagtgtaatgagtgtgggaagaccttcaggcaaacctcccagctcattgttcatctcagaacccacacaggggaaaagccctatgaatgcagtgagtgtggaaaggcctataggcacagctcccatctcattcaacaccagagactccataatggggagaaaccctataaatgtaatgaatgtgcaaaagcttttaatcagagctccaaactcttcgaccaccagagaacccatactggggagaaaccttatgaatgtaaggagtgtggggcggcctttagtcggagtaaaaatcttgttcgacatcagtttctgcacactggtaagaaaccttataagtgtaatgaatgtgggagagcattctgttccaatagaaatctcattgaccatcagagaacccacactggggagaagccttataaatgtaatgaatgtggcaaagccttcagtcggagtaaatgtcttattcgacatcagagcctccacactggggaaaagccatacaaatgtagtgaatgtgggaaagccttcaatcagatctctcaacttgttgaacatgagcgaattcatactggagaaaaaccatttaagtgtagtgagtgtggtaaggcattcggtctgagtaaatgtcttattcggcaccagaggcttcacacaagtgaaaagccctataaatgcaatgagtgtggaaaatccttcaatcaaaactcatacctcattatacaccagagaattcacactggtgagaaaccctatgaatgtaatgagtgtgggaaggtcttcagttataattctagtcttatggtacatcagagaacccatactggggaaaaaccctataaatgcaatagttgtgggaaagcctttagtgacagctcacagcttactgtgcaccagagagtccacactggagagaaaaaccttatgaatgtattgagtgtgggaaagcctttagtcagcgttccacttttaatcaccaccagcgaactcatgctggagagaagccctcaggtctggctcggtcatcttcttaaggcatggttttctgagacagacagcaaagacctttgagttaagctgtctttataagaaggatgttcatcatggtctccttggagaccactaatcacagtggagaccatacctacttgcttttccttgggtcactaaggtgggagagtaggtgcaacttagtctgatccttacttagtaggaaattggggattacatctgtcattaacttgta"
						.toUpperCase());
		this.builderForward.setGeneSymbol("ZNF852");
		this.infoForward = builderForward.build();
		// no RefSeq

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 3, 44540795,
				PositionType.ZERO_BASED), "TC", "");
		Annotation annotation1 = new DeletionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(3, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("1476_1477del", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Asn494Profs*38)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FRAMESHIFT_TRUNCATION), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc003dsi_1() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory.parseKnownGenesLine(refDict,
				"uc003dsi.1	chr3	+	97983128	97984106	97983128	97984106	1	97983128,	97984106,	Q8NGV6	uc003dsi.1");
		this.builderForward
				.setSequence("atgttcctttacctttgcttcatttttcagaggacatgcagtgaggagatggaagaggaaaatgcaacattgctgacagagtttgttctcacaggatttttacatcaacctgactgtaaaataccgctcttcctggcattcttggtaatatatctcatcaccatcatggggaatcttggtctaattgttctcatctggaaagaccctcaccttcatatcccaatgtacttattccttgggagtttagcctttgtggatgcttcgttatcatccacagtgactccgaagatgctgatcaacttcttagctaagagtaagatgatatctctctctgaatgcatggtacaatttttttcccttgtaaccactgtaaccacagaatgttttctcttggcaacaatggcatatgatcgctatgtagccatttgcaaagctttactttatccagtcattatgaccaatgaactatgcattcagctattagtcttgtcatttataggtggccttcttcatgctttaatccatgaagctttttcattcagattaaccttctgtaattccaacataatacaacacttttactgtgacattatcccattgttaaagatttcctgtactgattcctctattaactttctaatggtttttattttcgcaggttctgttcaagtttttaccattggaactattcttatatcttatacaattatcctctttacaatcttagaaaagaagtctatcaaagggatacgaaaagctgtctccacctgtggggctcatctcttatctgtatctttatactatggccccctcaccttcaaatatctgggctctgcatctccgcaagcagatgaccaagatatgatggagtctctattttacactgtcatagttcctttattaaatcccatgatctacagcctgagaaacaagcaagtaatagcttcattcacaaaaatgttcaaaagcaatgtttag"
						.toUpperCase());
		this.builderForward.setGeneSymbol("OR5H6");
		this.infoForward = builderForward.build();
		// RefSeq NM_001005479.1

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 3, 97983496,
				PositionType.ZERO_BASED), "TGTAACCAC", "");
		Annotation annotation1 = new DeletionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(0, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("369_377del", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Val124_Thr126del)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.DISRUPTIVE_INFRAME_DELETION), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc011bgx_2() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory.parseKnownGenesLine(refDict,
				"uc011bgx.2	chr3	+	98216524	98217475	98216524	98217475	1	98216524,	98217475,	Q8NHB8	uc011bgx.2");
		this.builderForward
				.setSequence("atggttgaagaaaatcataccatgaaaaatgagtttatcctcacaggatttacagatcaccctgagctgaagactctgctgtttgtggtgttctttgccatctatctgatcaccgtggtggggaatattagtttggtggcactgatatttacacactgtcggcttcacacaccaatgtacatctttctgggaaatctggctcttgtggattcttgctgtgcctgtgctattacccccaaaatgttagagaacttcttttctgagggcaaaaggatttccctctatgaatgtgcagtacagttttattttctttgcactgtggaaactgcagactgctttcttctggcagcagtggcctatgaccgctatgtggccatctgcaacccactgcagtaccacatcatgatgtccaagaaactctgcattcagatgaccacaggcgccttcatagctggaaatctgcattccatgattcatgtagggcttgtatttaggttagttttctgtggattgaatcacatcaaccacttttactgtgatactcttcccttgtatagactctcctgtgttgaccctttcatcaatgaactggttctattcatcttctcaggttcagttcaagtctttaccataggtagtgtcttaatatcttatctctatattcttcttactattttcagaatgaaatccaaggagggaagggccaaagccttttctacttgtgcatcccacttttcatcagtttcattattctatggatctatttttttcctatacattagaccaaatttgcttgaagaaggaggtaatgatataccagctgctattttatttacaatagtagttcccttactaaatcctttcatttatagtctgagaaacaaggaagtaataagtgtcttaagaaaaattctgctgaaaataaaatctcaaggaagtgtgaacaaatga"
						.toUpperCase());
		this.builderForward.setGeneSymbol("OR5K2");
		this.infoForward = builderForward.build();
		// RefSeq NM_001004737.1

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 3, 98216798,
				PositionType.ZERO_BASED), "TTTCCCTCTAT", "");
		Annotation annotation1 = new DeletionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(0, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("275_285del", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Ile92Argfs*26)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FRAMESHIFT_TRUNCATION), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc003lhq_2() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc003lhq.2	chr5	+	140213968	140391929	140213968	140389522	4	140213968,140358533,140362059,140389211,	140216323,140358592,140362148,140391929,	Q9UN72	uc003lhq.2");
		this.builderForward
				.setSequence("atggtgtgcccgaatggatacgacccagggggccgacatctactgctgtttattataattctagcagcttgggaggcagggagaggccagctccactactcggtccccgaggaggctaaacatggcaacttcgtgggccgcatcgcgcaggacctggggctggagctggcggagctggtgccgcgcctgttccgggcggtgtgcaaattccgtggggatcttctggaggtaaatctgcagaatggcattttgtttgtgaattctcggatcgaccgcgaggagctgtgcgggcggagcgcggagtgcagcatccacctggaggtgatcgtggaaaggccgctgcaggttttccatgtggacgtggaggtgaaggacattaacgacaaccctccggtgttcccagcgacacaaaggaatctgttcatcgcggaatccaggccgcttgactctcggtttccactagagggcgcgtccgatgcagatatcggggagaacgccctgctcacttacagactgagccccaatgagtatttcttcctggacgtgccaaccagcaaccagcaggtaaaacctcttggacttgtattacggaaacttttagacagagaagaaactccggagcttcatttattgctcacggccaccgatggaggcaaacccgagctgactggcaccgttcaattactcatcacggtactggacaacaatgacaatgccccagtgttcgacagaaccctgtatacggtgaaattaccagaaaacgtttctatcggaacgctggtgattcaccccaatgcctcagatttagacgaaggcttgaatggggatattatttactccttctccagtgatgtttctccagatataaaatccaagttccacatggaccccttaagtggggcaatcacagtgataggacatatggattttgaagaaagtagagcacacaagatcccagtcgaggctgtcgataaaggcttcccacccctggctggtcattgtacagttcttgtggaagttgtggatgtaaatgacaatgctccacagttgactctcacttccctgtctctccctattccagaggacgcccaaccaggtaccgtcatcacattgattagcgtgtttgaccgagattttggagtcaacggacaggttacctgctccctgacgccccgcgttcccttcaagttggtgtccaccttcaagaattactattcattggtgctggacagcgctctggaccgcgagagtgtgtccgcctatgagctggtggttaccgcgcgggacgggggctcgccttctctgtgggccactgctagcgtgtccgtggaggtggccgacgtgaacgacaacgccccggcgttcgcgcagcccgagtatacggtgttcgtgaaggagaacaacccgccgggctgccacatcttcactgtgtcggcgggggacgcggacgcgcagaagaacgcgctggtgtcctactcgctggtggagctgcgggtgggcgagcgcgcgctgtcgagctacgtgtcagtgcacgcggagagcggcaaggtgtacgcgctgcagccgttggaccacgaggagctggagctgttgcagttccaggtgagcgcgcgcgatgcgggcgtgccgcctctgggcagcaacgtgacgctgcaggtgttcgtgctggacgagaacgacaacgcgccggcactgctggcgcctcgggtgggtggcactggtggcgcagtgagagagcttgtgccgcggtctgtgggcgcgggccatgtggtggcgaaggtacgtgcagttgacgctgactcaggctacaacgcgtggctttcgtatgagttgcaaccggtggcggccggtgcgagcatcccgttccgcgtggggctgtacactggtgagatcagcacgacacgagccctagatgagacggacgcaccgcgccaccgccttctggtgcttgtgaaggaccacggggagccctcgctgacagccacagccaccgtgctggtgtcgctggtggaaagcggccaggcaccaaaggcgtcgtcgcgggcatcgttgggcattgcaggcccagagaccgagctggtggatgtcaacgtgtacctgatcatcgccatctgcgcggtgtccagtctgttggtgcttaccctgctgctgtacacggcgttgcggtgctcagcgccgtcctctgagggcgcatgtagtttggtaaagcccactctggtgtgctccagcgcggtggggagctggtcattctcccagcagaggcggcagagggtgtgctctggggagggcccacccaagacagacctcatggccttcagtcccagccttcctcagggtccatcctctacagacaatccacgacagcccaaccctgactggcgttactctgcctccctgagagcaggcatgcacagctctgtgcacctagaggaggctggcattctacgggctggtccaggagggcctgatcagcagtggccaacagtatccagtgcaacaccagaaccagaggcaggagaagtgtcccctccagtcggtgcgggtgtcaacagcaacagctggacctttaaatacggaccaggcaaccccaaacaatccggtcccggtgagttgcccgacaaattcattatcccaggatctcctgcaatcatctccatccggcaggagcctactaacagccaaattgacaaaagtgacttcataaccttcggcaaaaaggaggagaccaagaaaaagaagaaaaagaagaagggtaacaagacccaggagaaaaaagagaaagggaacagcacgactgacaacagtgaccagtgaggtcctcaaatggaaacaagccacttagccagtttttgtaataatggcaaatctctcccatgtagcaattccctgctcctttttcctatctacatgagccctcttagagacctcagaaatctgcagaaagttccctgtgtctgtctagaacgcatttaacaggttttgtcgtaaaagctttactaagtctggtgttaactctttctctccactctggcttgttttcagaacctaaaaagcagacccaagtttcctttctcctccgccgcaaaggagaggcttcccagccccgccagtgagaggttggactctctgccctgtgctccggggatcctgtcttgatgacacttgcagggcaggctgaaaagttttgagattgagcagcttgggagtttgtggccactgggtatgtgtggctaccgcgggtatgcgagtgccagatattggctgagacgagccagcttagactaattggtacaaggaaggcaagaaaacaaagacaaataaacagcggaagttatcagtatggaggggaagtgtaaacttaaagggaccagactttctaaatcttacaactcaagaggtggcagccaccctctaggagacaaaactacccccactgacaaggctttaggagaccctaaagtctgttggctgtgacgtcattatacctaaaatctgcatcatacctgcaagccaacagttcagtgttttaacagagaaccaccctgggaaacagaagcagatctgatgtgtttcctatacatgtcctgtgctcactttattaaaaattcttttgcacacaatgtttatgaaaaggccagatccttttccaatacttatgcaaaagcaaaagaaaaccccgacacctcacctttcgctgtttgttgtttcatagatttatttaaaaaaagagaaagtctatagctataaatctttaaagagaaatatgaatacaattcccctaaactctcctcaaaagagaattcagtctacagccatttaaatgatcattgctgctacagaagtgctttaagagaattgcctgaaacatctgtattatatcggccacctgccaatcacagctttactctttcaggtcactctggggctgcctcttgcatgtattactaaataaaatgatctctctttctctctctctctctcttttctaagaaacaattatgtgcactttgatacacaaccttctctaaccaactatatatcaagacccaaaaattgaagaaaaatattgttttctcatacagtgagcagatttttcaatctactaattctgtgacttgtcttggtgtgctagcctacaccttctctttggtttagttttccttttctataacactctgaattgctaatcttactaacacctatgatgttacctgaaatcaatctcccatatgtatgctgtatgctatgctaagactcctgaaatatacttactctgtgcttgtgtatgtgaatgttaatgcaactattacctagagtgaactttaagctttattgttgaatgtaattccattatatttccttttgtacacctgtgaaaaagtggagtagtgtttttttaaccattgttaatcagcttttgtgtatgaaagacacagtaaaatttctttcttaaatcaagatactggtgattcaaggaattttatttatggtccagccaagagccatctcgtgccaagacttctgctggcaagggaatggataaagctgttttgttctagtaacaattttggaatgaatactgacaatattccatgagggtgtgcaagcacaaattttaccaatctgacctctttgaagttgcagaatgctttgaaattctaatggtatctgaaatatcagctcatagaaagtaacaaaatttgctgtcaccttaaataagacattttaattttgttataatgtacaatttagaagtttgattaattatattatctatttaggcattaatataaaagaggtaggagtctgttatttaaaaaaagcattaaatttaaaaaaaaactgtcttgtctacttttagcttcattctcccatattttgaagggtgtgtaacttcagctctgcaggattgcatggggtaaaacttgttaccaacacatgtgaaccattgctacattgtaggttgtgatcattttgccccactgaagcccatgtatctgaccttacgtgccttttgaactaggagaatcgggctaatttattaatgatgataattataatgtatctgtacagcactttttacatttgcgaagtgctttccaatccatgttagttactagttattacagctgtaaggataaaacacgtcatgtggattcattttgaattggtgctattggtatttcctctgttattgctaataaatgaaaatggtggtatg"
						.toUpperCase());
		this.builderForward.setGeneSymbol("(DHA7)");
		this.infoForward = builderForward.build();
		// RefSeq NM_018910.2

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 5, 140215470,
				PositionType.ZERO_BASED), "GCGCG", "");
		Annotation annotation1 = new DeletionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(0, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("1503_1507del", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Glu501Aspfs*96)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FRAMESHIFT_TRUNCATION), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc003ljc_1() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory.parseKnownGenesLine(refDict,
				"uc003ljc.1	chr5	+	140613937	140617101	140614285	140616490	1	140613937,	140617101,	Q96TA0	uc003ljc.1");
		this.builderForward
				.setSequence("aaagcgctggaataccaggagaaaatagtctttcaaaaggtagggcaccatcaacaccttcgcgacaggattaagagtttctagattcccaaaggtccaggctgataggtgagaggagcaactttaagagctgctagagactgagttgaaacgtttacgccagaaagacgtttggctaaggagctatggagccgggaaagggaagagctcagcggacaaggcaagtgctgcttttctttgttttcctgggagggtctttggtgtgttctgagaccgggagctattccatagcagaggaaatggaggtcggtacctttatagccaacgtggtgaaagacatgggtttggatgtggaagacctggctgcaagggggggccagagtcatctttgacgactataaaccttatttgcgattggatccacagaatggcgacttgctcttaaacgagcagctggaccgggaggcactttgtgatctcacagagccatgtatattgcatttccaggtgttatttgaaaatccgttgcaattttttcgtgctgagcttttggtcaaagacataaatgatcacactcccacgttcctaaacaatcatatgcttctaaaaatctccgaaggtgctactctaggaaccttattccaaatagatagtgcgcaggacttggatgtgggaaagaatggtgttcaaaactatacaataagtcccaatccccatttccaccttaaattacgggatagcgatgagggcagaaaatatccagagttggtactggaccaatccctggatcgagaaaaggtgtctgagtttagtttaacgctaacagccgtggatggcgggtctccgcccaggtctgggactacactgattaacgttgtggtcctggacatcagtgacaatgcccctgaatttgagaagccagtctatgaagttcatgtacctgagagcagccctctggactccttgatcatcaaagcgtctgctacagatttagatgcaggaataaatggagaactgtcttattcattttcccacgtctccagagatgtacggaaaacatttgaaatccatccaatttctggcgaagtctatttaaaagcccctctagatttcgagattattcaatcttatatcataaatattcaggccattgaaggtgggagcctttctggaaaatcaagcattttagttcgggttgtagatgtgaatgacaacccgccagaaatagccatgacatctcttaccagccccataccggaaaactcttcacctgagatggtggtcgctgttttcagcatacgagaccaagacgctggagacaatgggagaacagtttgctcaattcaggacaacctcccctttgtcttgaagcctaccttcaagaatttttacgctctggtaacagagcacccactggacagagaggtcagaaatgaatataacatcaccatcaccgtgaccgacttggggacacccaggctgaaaaccgagcacaacataaccgtgctggtctccgacgtcaatgacaacgcccccatcttcacccaaacctcctacaccctgttcgtccgcgagaacaacagccccgccctgcacatcggcagcgtcagcgctacagacagagactcaggcaccaacgcccaggtcacctactcgctgctgccgccccaggacccgcacctgcccctcacctccctggtctccatcaacgcggacaacggccacctattcgccctcaggtctttggactacgaggccctgcaggagttcgggtttcgcgtgggcgccgcagaccacggctccccggcgctgagcagcgaggtgctggtgcgcgtgctggtgctggacgccaacgacaactcgcccttcgtgctgtacccgctgcagaacggctcggcgccctgcaccgagctggtacctcgggcggccgagccgggctacctggtgaccaaggtggtggcggtggacggcgactcgggccagaacgcctggctgtcgtaccagctgctcaaggccacggagcccgggctgttcggcgtgtgggcgcacaatggcgaggggcgcaccgccaggctgctgagcgagcgcgacgcggccaagcacaggctggtggtgctggtcaaggacaatggcgagcctccgcgctcggccaccgccacgctgcacgtgctcctggtggagggcttctctcagccctacctgcctctcacggaggctgccccctcccaggcccaggccgactccctcaccgtctacctggtggtggcgttggcctcggtgtcgtcgctcttcctcttctcggtgttcctgttcgtggcggtgcggctgtgcaggaggagcagggcggcctcgatgggtcgctgctcggtgcccgagtgtccctttccagggcatctggtagacgtgagcggcaccgggaccctatcccagagctaccagtacgaggtgtgtctgacgggaggctcaggggcaaatgagttcaagttcctgaagccggtgattcccaatctcctgtcccgcgacagcgaaatggagaaagccccacctttctgaatggcgtggaatgcaattagggatctgattatgatgcagaacttttagaatgagtctatttctttgaaatcttattcattgttatgcagagtttttcattttgggtaactgcattttactcaagagttttcagaagttacaagaatttaagtctattttttgttgttttaaccgtgaaaaaattgagagccggaatttgcttagtcattgttttgaaatacaacctcaaataatatattcacaaacacattattttcccttcaagtttaatcgcacactgggctcattcatattttctgagtgttctgactgtggatcctctatccaaagcagtttttatataattgagaatattattatagaggtaaatgcatgatatgaataaaaacataattgcttgttatctggttaggttggtttctgagatgttatctaatttaggtttctttcttaaaaacctataatcttttcattctacttttctggcaaacattgcagagaatttttcctgtacttagggttttttttccataattatttgtgaaccatatatatgctagtagaagttgttttatttaaataaattcaaaaccttgtttggattaagatgt"
						.toUpperCase());
		this.builderForward.setGeneSymbol("(DHA7)");
		this.infoForward = builderForward.build();
		// RefSeq NM_018910.2

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 5, 140615503,
				PositionType.ZERO_BASED), "GTC", "");
		Annotation annotation1 = new DeletionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(0, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("1219_1221del", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Val407del)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.INFRAME_DELETION), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc011dkw_2() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory.parseKnownGenesLine(refDict,
				"uc011dkw.2	chr6	-	27878962	27880174	27879023	27880097	1	27878962,	27880174,	Q9GZK3	uc011dkw.2");
		this.builderForward
				.setSequence("gctgacaaaatcaggaagtgtgttgttaacttcctgacttcttatatttcagagaacgaagagttgaaccatttaacatgaattgggtaaataagagtgtcccacaggagttcattctgttagttttctcagatcaaccatggctagagattccaccctttgtgatgtttctgttttcctatatcttgacaatctttggcaatctgacaataattcttgtgtcacatgtggatttcaaactccacacccctatgtacttttttcttagcaatctctcactcctggacctttgctataccacaagtacagttccacaaatgctggtaaacatatgcaacaccaggaaagtaatcagttatggtggctgtgtggcccagcttttcattttcctggccttgggttccacagaatgtcttctcctggccgtcatgtgctttgataggtttgtagctatttgtcggcctctccattactcaattatcatgcaccagaggctctgcttccagttggcagctgcatcctggattagtggctttagcaattcagtattacagtccacctggacacttaagatgccactgtgtggtcacaaagaagtggatcacttcttctgtgaagtccctgctctgctcaagttgtcctgtgttgacacaacagcaaatgaggctgaactattcttcatcagtgtgctattccttctaatacccgtgacactcatccttatatcgtatgcttttattgtccaagcagtgttgagaatccagtctgctgaaggtcaacgaaaggcatttgggacatgtggctcccatctaattgtggtgtcacttttttatggtacagctatctccatgtacctgcaaccaccttcacccagctccaaagaccggggaaagatggtttctctcttctgtggaatcattgcacccatgctgaatccccttatatatacacttaggaacaaagaggtaaaggaagcctttaaaaggttggttgcaaagagtcttcttaatcaagaaataagaaatatgcaaatgataagctttgctaaagacacagtgcttacttaccttactaacttctccgcaagttgtcctatttttgtcattactatagaaaactattgtaatctccctcaaagaaaatttccttgacaaaaagctatatttgtttctgttgcctaaacattttcattgaacaagcccccagaattgg"
						.toUpperCase());
		this.builderForward.setGeneSymbol("(DHB18)");
		this.infoForward = builderForward.build();
		// RefSeq NR_001281.1

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 6, 27879112,
				PositionType.ZERO_BASED), "T", "");
		Annotation annotation1 = new DeletionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(0, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("985del", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Thr329Leufs*17)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FRAMESHIFT_VARIANT), annotation1.getEffects());
	}

	/**
	 * Fails because of discrepancy of UCSC and RefSeq, see the *_REFSEQ variant of this test below. The problem was
	 * that UCSC predicted the first and last exon one base too large which in turn led to insufficient shifting. In the
	 * test below, we adjusted the first and last exon by one base at the outside and use the RefSeq sequence as
	 * obtained from Mutalyzer.
	 */
	@Ignore("See method comment.")
	@Test
	public void testRealWorldCase_uc003ooo_3() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory.parseKnownGenesLine(refDict,
				"uc003ooo.3	chr6	-	39266776	39282237	39267202	39282096	"
						+ "5	39266776,39271732,39272270,39278668,39281859,	"
						+ "39267513,39271907,39272431,39278783,39282237,	Q96T54	uc003ooo.3");
		this.builderForward
				.setSequence("ttccccaacactcctcctccccggcgaaaccgggcaccagcaggcgtttgcgagaggagatacgagctggacgcctggcccttccctcccaccgggtcctagtccaccgctcccggcgccggctccccgcctctcccgctatgtaccgaccgcgagcccgggcggctcccgagggcagggtccggggctgcgcggtgcccagcaccgtgctcctgctgctcgcctacctggcttacctggcgctgggcaccggcgtgttctggacgctggagggccgcgcggcgcaggactccagccgcagcttccagcgcgacaagtgggagctgttgcagaacttcacgtgtctggaccgcccggcgctggactcgctgatccgggatgtcgtccaagcatacaaaaacggagccagcctcctcagcaacaccaccagcatggggcgctgggagctcgtgggctccttcttcttttctgtgtccaccatcaccaccattggctatggcaacctgagccccaacacgatggctgcccgcctcttctgcatcttctttgcccttgtggggatcccactcaacctcgtggtgctcaaccgactggggcatctcatgcagcagggagtaaaccactgggccagcaggctggggggcacctggcaggatcctgacaaggcgcggtggctggcgggctctggcgccctcctctcgggcctcctgctcttcctgctgctgccaccgctgctcttctcccacatggagggctggagctacacagagggcttctacttcgccttcatcaccctcagcaccgtgggcttcggcgactacgtgattggaatgaacccctcccagaggtacccactgtggtacaagaacatggtgtccctgtggatcctctttgggatggcatggctggccttgatcatcaaactcatcctctcccagctggagacgccagggagggtatgttcctgctgccaccacagctctaaggaagacttcaagtcccaaagctggagacagggacctgaccgggagccagagtcccactccccacagcaaggatgctatccagagggacccatgggaatcatacagcatctggaaccttctgctcacgctgcaggctgtggcaaggacagctagttatactccattctttggtcgtcgtcctcggtagcaagacccctgattttaagctttgcacatgtccacccaaactaaagactacattttccatccaccctagaggctgggtgcagctatatgattaattctgcccaatagggtatacagagacatgtcctgggtgacatgggatgtgactttcgggtgtcggggcagcatgcccttctcccccacttccttactttagcgggctgcaatgccgccgatatgatggctgggagctctggcagccatacggcaccatgaagtagcggcaatgtttgagcggcacaataagataggaagagtctggatctctgatgatcacagagccatcctaacaaacggaatatcacccgacctcctttatgtgagagagaaataaacatcttatgtaaaataccaaaaaaaaaaaaaaaaaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("KCNK17");
		this.infoForward = builderForward.build();
		// RefSeq NM_031460.3

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 6, 39278700,
				PositionType.ZERO_BASED), "AAG", "");
		Annotation annotation1 = new DeletionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(1, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("324_326del", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Phe109del)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.DISRUPTIVE_INFRAME_DELETION), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc003ooo_3_REFSEQ() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory.parseKnownGenesLine(refDict,
				"uc003ooo.3	chr6	-	39266777	39282236	39267202	39282096	"
						+ "5	39266777,39271732,39272270,39278668,39281859,	"
						+ "39267513,39271907,39272431,39278783,39282236,	Q96T54	uc003ooo.3");
		this.builderForward
				.setSequence("ttccccaacactcctcctccccggcgaaaccgggcaccagcaggcgtttgcgagaggagatacgagctggacgcctggcccttccctcccaccgggtcctagtccaccgctcccggcgccggctccccgcctctcccgctatgtaccgaccgcgagcccgggcggctcccgagggcagggtccggggctgcgcggtgcccagcaccgtgctcctgctgctcgcctacctggcttacctggcgctgggcaccggcgtgttctggacgctggagggccgcgcggcgcaggactccagccgcagcttccagcgcgacaagtgggagctgttgcagaacttcacgtgtctggaccgcccggcgctggactcgctgatccgggatgtcgtccaagcatacaaaaacggagccagcctcctcagcaacaccaccagcatggggcgctgggagctcgtgggctccttcttcttttctgtgtccaccatcaccaccattggctatggcaacctgagccccaacacgatggctgcccgcctcttctgcatcttctttgcccttgtggggatcccactcaacctcgtggtgctcaaccgactggggcatctcatgcagcagggagtaaaccactgggccagcaggctggggggcacctggcaggatcctgacaaggcgcggtggctggcgggctctggcgccctcctctcgggcctcctgctcttcctgctgctgccaccgctgctcttctcccacatggagggctggagctacacagagggcttctacttcgccttcatcaccctcagcaccgtgggcttcggcgactacgtgattggaatgaacccctcccagaggtacccactgtggtacaagaacatggtgtccctgtggatcctctttgggatggcatggctggccttgatcatcaaactcatcctctcccagctggagacgccagggagggtatgttcctgctgccaccacagctctaaggaagacttcaagtcccaaagctggagacagggacctgaccgggagccagagtcccactccccacagcaaggatgctatccagagggacccatgggaatcatacagcatctggaaccttctgctcacgctgcaggctgtggcaaggacagctagttatactccattctttggtcgtcgtcctcggtagcaagacccctgattttaagctttgcacatgtccacccaaactaaagactacattttccatccaccctagaggctgggtgcagctatatgattaattctgcccaatagggtatacagagacatgtcctgggtgacatgggatgtgactttcgggtgtcggggcagcatgcccttctcccccacttccttactttagcgggctgcaatgccgccgatatgatggctgggagctctggcagccatacggcaccatgaagtagcggcaatgtttgagcggcacaataagataggaagagtctggatctctgatgatcacagagccatcctaacaaacggaatatcacccgacctcctttatgtgagagagaaataaacatcttatgtaaaataccaaaaaaaaaaaaaaaaaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("KCNK17");
		this.infoForward = builderForward.build();
		// RefSeq NM_031460.3

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 6, 39278700,
				PositionType.ZERO_BASED), "AAG", "");
		Annotation annotation1 = new DeletionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(1, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("324_326del", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Phe109del)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.DISRUPTIVE_INFRAME_DELETION), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc010mht_3() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc010mht.3	chr9	-	5919007	5954095	5919683	5929066	4	5919007,5924657,5929043,5954015,	5923309,5924845,5929159,5954095,	Q5HYC2	uc010mht.3");
		this.builderForward
				.setSequence("gaaaaaaatccgtctctaaaaaagcaatcacaaagaagaggaaaactgtcataaagtcacctactgtaccagagtttcagggaaaatggtatcatcggaggcaagctgtaaaagaattacatagtacattgatacgtcttttaaatgaattgctgccatgggaaccaaagttaatgaaggctttccaaagaaacaggagccgcctgaagaaagactatgatgatttcagaagacaacctgatcatgatacatttaatagagaactatggaccactgatgaaggtgaaggagatcttggaaaagactctcctaagggagaaatcagtaagtctatagactcaacagaacctctggatatcctggagaaagatcactttgattcagatgatatgaagttgtcagaaatagattttcctatggctagaagcaagctgttgaaaaaggaattgccttcaaaagacttaccgaagacactgcttaaaacacttaaacgacagtccaaacaaactgattatgtggatgacagcacaaaagagctttctccaaggaagaaggcaaagttaagcacaaatgagacaacagttgagaatctagaaagtgatgtgcagattgactgtttcagtgaatcaaagcatacagagccatcatttccagagtcatttgcctcattggattcagtaccagtgtctactctccagaaagggaccaaacccattcaggctttgcttgcaaagaatattgggaataaagtgaccttaacaaatcaactgcctccttccacaggtagaaatgctcttgctgtggaaaaaccagttttatctcctccggaagcaagcccgataaagccagcattgacctgccacaccaatacaaaaggtcctttacaaatggtatacaaaatgccctgtggccagtggttgccaatagatcttcaaaatagctctgtaaagattcaggtgcagcctatggtggatcctaaaacaggagaaaaaatcatgcagcaagttcttattctgcctaagaattttgtgattcagcataaagaaggaaaagcagttgcaaaagaagtgccaccacttcaacagaaaggtacagaacaacattgttcatctttcccacagacaacaaatataaattcttctttagcatcagtttttgtcaactcaccaggaactgtttctacccaactaccaaatacagctttcaacaaaacaattacacctttatcaaatataagtagtgctagaccacagcctttgtcgcctgtaacctctgtaagtaacttattaacaccatcagttaagactagccagagtgaggcaggaaaagccaagaatgcagtttcagcagccacattctccttgcccagtgcttcaccgaccatttcctccacaggtcagcctctgtcatcaacaacaacactaaatgggtctacaaatcctggtagttccttcaactgttttgcacaacagactgctgattcttctgaagcaaagcaagagctgaaaactgtgtgcataagagattcacagtcaattcttgttaggacacgaggtgggaacactggagttgtaaaagtacagaccaacccagatcaaaattcacccaatactgtatcttcaagttcagttttcacttttgctcctcagctgcaggcatttctggtgccaaaatcaacaacttcttcctctgctttttcacctgtagctggaacaactactacatctagtctttcaccttttagccaaacaccaacttctgtttccattccagctagctttgccccatccatggggaaaaatctcaaacttacattaggccacaccactggcagtggtgatttgggccacgtgatagataaaacttcacacatgccctcttctcccttgaagtcctctatttgttctagtactctactaccatcaacaacaagtagttcggtaagtgtaattagcatatcagcagcaaattttggacaaaacaatgcaaatattattcatactccaactaaacagcaacaagtagattatatcacaaaaagttaccctgttacaaggtcagaagcaacagcagcaacaaatggagatgtaatcagtgggactccagttcagaaacttatgctggtatcagctccatctattctttcttctggcaatggaactgcaattaatatgacacctgcgctgacatctacaggtgtttctgcccagaaattagtttttattaatgccccagttcctagtggtacatcaaccccaactcttgttgcagaatcattaaaacaaactcttcctcctccattgcataaagcatatgttaagactccagagcaaccccaaatagtactgattccgtctacagtgggcacaccaataaaaataaattcatcaccagctgtgtctcagataaaggatgtgaaaattggactaaacataggtcaagcaattgtaaatacttcaggcactgtgccagctataccatcaattaacatattacaaaatgtaaccccaaaaggagaagacaaaagtagcaagggctatattttgccattgtcaacaagtggtaattcagttccagtaagctcaaattttgtgagtcagaatattacccctgttaatgaatcagtggtttcttcagcaagagcagtaaatgtgctttcagttaccggagcaaatttatctttgggttcctttccagtgacctcagcctctgcttcagctggggcacaacctcctgttttagtcagtggaaatgatacctcttcaagaattatgcctattttgtcaaatagactttgctcatcaagtctcggaaacactgtggctatatcaactgtgaaaacaggacaccttgcatcatctgttctgatttcaactacacaaccagtagtgtctcctaaatgtttaacatcagctttgcaaattcctgttacggttgccttacctacacctgcgactacatcccctaaaataataaacacagttccacattcggcagcagtaccaggagccacacgttctgtatctatttctaaaagacaatctcggacttcactccagttccattcaccagggatttcaactacagtgccaacaaatgtaaacacaaataaacctcaaactgaattgtcgtccctttcaacaagtccaggtaaaataactaatacgtccaattttgcttctctgccaaatcagcaagctttagtaaaaacccccagttacagttctgctccaggtggcactaccattcacacggcttcggcaccatccaatgtaactagtctagtcgggagtcagttcagtgaaccttgtattcagcaaaaaatagtcatcaataccagtacacctttggcacctggtactcagattatgattaacggaacccggtttattgttccaccacaaggtcttggagctggcagccatgttctccttatatctactaatccaaaatatggagcccccttagttcttaacagtggccaaggcattcaatctacaccaatagataactctgcccagaagatcacactagcatcaaataattccttaagtggccaacctctacaacatcctctaagaagccctacaaaatttataaactcttttgggaatgcaagttctatacccacagtacatacatcaccacaactcataaacacaactgctaaggttcctgttccacctcctgtaccaacagtatcactgacttcagtaattaagtctccagctactcttttggctaagacatctttggtttctgccatttgccccagtaatcctccactgccaagtagcacttcagtgtttcatttggacccacctgtcaaaaaattgttggttagcccagaaggagccattttgaataccataaatactccagcatctaaggtttcttcactctctccatctctctctcagattgttgtatctgccagtcgaagtcctgcgtctgtcttccctgcttttcagtcgtcgggcttagagaagcctgacagagctgcatcttgaatttagactaaaagagccagtttttaaataatggggcattgttttgactcccataaaaattttcaatgtttattatactgttgaaagcatactatacattgtgtgtgtgtgtgtgtgtgtgtgtgtgtgtgtgtgtgtgttttaatgtatcttttcattagcttgcaacaaggctttgttttcttggggagggaaagatctgttccatttcacttactgttctgaggagggtttcaaggttctgtgtttaaagtcaggatttacctgtctgtagaacataaagtgaacaaagtttgaatagtttagagtaagctagccttttgcacatttacattgacatgtttcttcttgaatttggactgttgcagcatatctatgatatggtattcagtgtcattagtgaaatcttatttttgtttctgtaaaaaaaaaaatacatatatatatttatgcattgtgaaaaatgcagtccatgatgtaaaattgtacatattccagaatccctaacaacctgtactaaatatatgtacaaagaactatgctgtcttatttatgttttgtttacataatgtatagttttttaagtattttagtaattttggaccagtgaactgttagcaacaatgcagaagaatctgcatgtaataaactgagttgctgtatttctttgtttgaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("KIAA2026");
		this.infoForward = builderForward.build();
		// RefSeq NM_001017969.2

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 9, 5921979,
				PositionType.ZERO_BASED), "GTT", "");
		Annotation annotation1 = new DeletionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(3, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("1542_1544del", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Thr517del)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.DISRUPTIVE_INFRAME_DELETION), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc001jix_4() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001jix.4	chr10	+	51748077	51770259	51748475	51769946	8	51748077,51749067,51751435,51754154,51761755,51764444,51767784,51768470,	51748698,51749136,51751504,51754189,51761856,51764480,51767836,51770259,	Q5VW22-2	uc001jix.4");
		this.builderForward
				.setSequence("gggcattgtgtgccacatgcctgggctcccacctggggccagtgggcttcagtctgtaggtgactacagaaggaggaggaactccgtctgttctctcttcaggcagttgttgtgtctctcagcgcttgttggtttcacaacctattaaataagccggctggtcttcaccctcccagacaagtcaactcaggggaggcagcagggtgcgggccttggcccgcagccctagccggggccggggccagggctggtgcccggggcctcgctgtgaggtgggcaggcgaggagcgggaagaccatctctgcaagtgcagcatagcctcggcctaggacagcgggagtgcgtggccaaagctgtgagcagaggcacaggtggtggcagacagtagaggcgccccatggggaacatactgacctgtcgtgtgcaccctagcgtcagcctcgagtttgaccagcagcaggggtcggtgtgtccctctgaatctgagacctatgaggcaggagctagggacaggatggcaggagcgcccatggctgctgctgtacagcctgctgaggtgactgttgaagttggtgaggacctccacatgcaccacgttcgtgaccgggagatgcctgaagctttggagtttaacctttctgccaatccagagtcaagcacaatattccagaggaactctcaaacagaagctttggagtttaacccttctgccaatccagaggcaagcacaatattccagaggaactctcaaacagatgttgtagaaataagaagaagcaactgtacaaaccatgtatctgctgtgcgtttcagtcaacaatacagcttgtgttcgacaatattccttgatgacagcacagccatccagcattatcttacaatgacaataatatctgtgaccttggagatacctcatcatatcacacaaagagatgcagatagaactttgagcatacctgatgaacagttacactcatttgcggtttccaccgtgcacattatgaagaaaagaaatggaggtgggagtttaaataactattcctcctccattccatcgactcccagcaccagccaggaggaccctcagttcagtgttcctcccactgccaacacacccacgcccgtttgcaagcggtccatgcgctggtccaacctgtttacatctgagaaagggagtgacccagacaaagagaggaaagccccggagaatcatgctgacaccatcgggagcggtagagccatccccattaaacagggcatgctcttaaagcgaagtgggaaatggctgaagacatggaaaaagaaatacgtcaccctgtgttccaatggcatgctcacctattattcaagcttaggtgattatatgaagaatattcataaaaaagagattgaccttcagacatctaccatcaaagtcccaggaaagtggccatccctagccacatcggcctgcacacccatctccagctctaaaagcaatggcctatccaaggacatggacaccgggctgggtgactccatatgcttcagccccagtatctccagcaccaccagccccaagctcaacccgcccccctctcctcatgctaataaaaagaaacacctaaagaagaaaagcaccaacaactttatgattgtgtctgccactggccaaacgtggcactttgaagccacgacgtatgaggagcgggatgcctgggtccaagccatccagagccagatcctggccagcctgcagtcatgcgagagcagtaaaagcaagtcccagctgaccagccagagcgaggccatggccctgcagtcgatccaaaacatgcgtgggaacgcccactgtgtggactgtgagacccagaatcctaagtgggccagtttgaacttgggagtcctcatgtgtattgaatgctcaggtatccaccgcagtcttggcccccacctttcccgtgtgcgatctctggagctggatgactggccagttgagctcaggaaggttatgtcatctattgtcaatgacctagccaacagcatctgggaagggagcagccaggggcagacaaaaccctcagaaaagtccacgagggaagagaaggaacggtggatccgttccaaatatgaggagaagctctttctggccccactaccctgcactgagctgtccctgggccagcagctgctgcgggccaccgctgatgaggacctgcagacagccatcctgctgctggcacatggctcctgtgaggaggtgaacgagacctgtggggagggagacggctgcacggcgctccatctggcctgccgcaaggggaatgtggtcctggcgcagctcctgatctggtacggggtggacgtcatggcccgagatgcccacgggaacacagcgctgacctacgcccggcaggcctccagccaggagtgcatcaacgtgcttctgcagtacggctgccccgacgagtgtgtgtagtatctgttttatttgactgcagtctccttggtgcaaaaacaaaatgggaaaaataaggataactcagaatttcaaaaggaaatcacaaattcagctaatattagcattttcagtacttttcgtaaactaagtaaatacacaaaatgttgatttttctgaccataagacgtattttatgtccttctgccaaggtggatttgttagtctcaggccctcctggccacattgcccaagtcacacaggcttctgtattatgtatttagataaaatgtgtgaaaacatatttgaaataaagttcataaatatgcattga"
						.toUpperCase());
		this.builderForward.setGeneSymbol("AGAP6");
		this.infoForward = builderForward.build();
		// RefSeq NM_001077665.2

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 10, 51768675,
				PositionType.ZERO_BASED), "AA", "");
		Annotation annotation1 = new DeletionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(7, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("791_792del", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Lys264Argfs*10)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FRAMESHIFT_TRUNCATION), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc001jix_4_no2() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001jix.4	chr10	+	51748077	51770259	51748475	51769946	8	51748077,51749067,51751435,51754154,51761755,51764444,51767784,51768470,	51748698,51749136,51751504,51754189,51761856,51764480,51767836,51770259,	Q5VW22-2	uc001jix.4");
		this.builderForward
				.setSequence("gggcattgtgtgccacatgcctgggctcccacctggggccagtgggcttcagtctgtaggtgactacagaaggaggaggaactccgtctgttctctcttcaggcagttgttgtgtctctcagcgcttgttggtttcacaacctattaaataagccggctggtcttcaccctcccagacaagtcaactcaggggaggcagcagggtgcgggccttggcccgcagccctagccggggccggggccagggctggtgcccggggcctcgctgtgaggtgggcaggcgaggagcgggaagaccatctctgcaagtgcagcatagcctcggcctaggacagcgggagtgcgtggccaaagctgtgagcagaggcacaggtggtggcagacagtagaggcgccccatggggaacatactgacctgtcgtgtgcaccctagcgtcagcctcgagtttgaccagcagcaggggtcggtgtgtccctctgaatctgagacctatgaggcaggagctagggacaggatggcaggagcgcccatggctgctgctgtacagcctgctgaggtgactgttgaagttggtgaggacctccacatgcaccacgttcgtgaccgggagatgcctgaagctttggagtttaacctttctgccaatccagagtcaagcacaatattccagaggaactctcaaacagaagctttggagtttaacccttctgccaatccagaggcaagcacaatattccagaggaactctcaaacagatgttgtagaaataagaagaagcaactgtacaaaccatgtatctgctgtgcgtttcagtcaacaatacagcttgtgttcgacaatattccttgatgacagcacagccatccagcattatcttacaatgacaataatatctgtgaccttggagatacctcatcatatcacacaaagagatgcagatagaactttgagcatacctgatgaacagttacactcatttgcggtttccaccgtgcacattatgaagaaaagaaatggaggtgggagtttaaataactattcctcctccattccatcgactcccagcaccagccaggaggaccctcagttcagtgttcctcccactgccaacacacccacgcccgtttgcaagcggtccatgcgctggtccaacctgtttacatctgagaaagggagtgacccagacaaagagaggaaagccccggagaatcatgctgacaccatcgggagcggtagagccatccccattaaacagggcatgctcttaaagcgaagtgggaaatggctgaagacatggaaaaagaaatacgtcaccctgtgttccaatggcatgctcacctattattcaagcttaggtgattatatgaagaatattcataaaaaagagattgaccttcagacatctaccatcaaagtcccaggaaagtggccatccctagccacatcggcctgcacacccatctccagctctaaaagcaatggcctatccaaggacatggacaccgggctgggtgactccatatgcttcagccccagtatctccagcaccaccagccccaagctcaacccgcccccctctcctcatgctaataaaaagaaacacctaaagaagaaaagcaccaacaactttatgattgtgtctgccactggccaaacgtggcactttgaagccacgacgtatgaggagcgggatgcctgggtccaagccatccagagccagatcctggccagcctgcagtcatgcgagagcagtaaaagcaagtcccagctgaccagccagagcgaggccatggccctgcagtcgatccaaaacatgcgtgggaacgcccactgtgtggactgtgagacccagaatcctaagtgggccagtttgaacttgggagtcctcatgtgtattgaatgctcaggtatccaccgcagtcttggcccccacctttcccgtgtgcgatctctggagctggatgactggccagttgagctcaggaaggttatgtcatctattgtcaatgacctagccaacagcatctgggaagggagcagccaggggcagacaaaaccctcagaaaagtccacgagggaagagaaggaacggtggatccgttccaaatatgaggagaagctctttctggccccactaccctgcactgagctgtccctgggccagcagctgctgcgggccaccgctgatgaggacctgcagacagccatcctgctgctggcacatggctcctgtgaggaggtgaacgagacctgtggggagggagacggctgcacggcgctccatctggcctgccgcaaggggaatgtggtcctggcgcagctcctgatctggtacggggtggacgtcatggcccgagatgcccacgggaacacagcgctgacctacgcccggcaggcctccagccaggagtgcatcaacgtgcttctgcagtacggctgccccgacgagtgtgtgtagtatctgttttatttgactgcagtctccttggtgcaaaaacaaaatgggaaaaataaggataactcagaatttcaaaaggaaatcacaaattcagctaatattagcattttcagtacttttcgtaaactaagtaaatacacaaaatgttgatttttctgaccataagacgtattttatgtccttctgccaaggtggatttgttagtctcaggccctcctggccacattgcccaagtcacacaggcttctgtattatgtatttagataaaatgtgtgaaaacatatttgaaataaagttcataaatatgcattga"
						.toUpperCase());
		this.builderForward.setGeneSymbol("AGAP6");
		this.infoForward = builderForward.build();
		// RefSeq NM_001077665.2

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 10, 51768774,
				PositionType.ZERO_BASED), "TGA", "");
		Annotation annotation1 = new DeletionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(7, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("890_892del", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Leu297_Lys298delinsGln)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.DISRUPTIVE_INFRAME_DELETION), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc001nja_1() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory.parseKnownGenesLine(refDict,
				"uc001nja.1	chr11	-	56380030	56380978	56380030	56380978	1	56380030,	56380978,	Q8NGP8	uc001nja.1");
		this.builderForward
				.setSequence("atgttctccccaaaccacaccatagtgacagaattcattctcttgggactgacagacgacccagtgctagagaagatcctgtttggggtattccttgcgatctacctaatcacactggcaggcaacctgtgcatgatcctgctgatcaggaccaattcccacctgcaaacacccatgtatttcttccttggccacctctcctttgtagacatttgctattcttccaatgttactccaaatatgctgcacaatttcctctcagaacagaagaccatctcctacgctggatgcttcacacagtgtcttctcttcatcgccctggtgatcactgagttttacatccttgcttcaatggcattggatcgctatgtagccatttgcagccctttgcattacagttccaggatgtccaagaacatctgtgtctgtctggtcactatcccttacatgtatgggtttcttagtgggttctctcagtcactgctaacctttcacttatccttctgtggctcccttgaaatcaatcatttctactgcgctgatcctcctcttatcatgctggcctgctctgacacccgtgtcaaaaagatggcaatgtttgtagttgcaggctttaatctctcaagctctctcttcatcattcttctgtcctatcttttcatttttgcagcgatcttcaggatccgttctgctgaaggcaggcacaaagccttttctacgtgtgcttcccacctgacaatagtcactttgttttatggaaccctcttctgcatgtacgtaaggcctccatcagagaagtctgtagaggagtccaaaataactgcagtcttttatacttttttgagcccaatgctgaacccattgatctatagcctacggaacacagatgtaatccttgccatgcaacaaatgattaggggaaaatcctttcataaaattgcagtttag"
						.toUpperCase());
		this.builderForward.setGeneSymbol("OR5M1");
		this.infoForward = builderForward.build();
		// RefSeq NM_001004740.1

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 11, 56380553,
				PositionType.ZERO_BASED), "GACA", "");
		Annotation annotation1 = new DeletionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(0, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("422_425del", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Cys141Serfs*21)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FRAMESHIFT_TRUNCATION), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc001qui_2() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001qui.2	chr12	-	8373855	8380214	8374417	8377428	7	8373855,8376044,8376611,8377305,8378431,8378867,8380075,	8375380,8376153,8376811,8377484,8378588,8379074,8380214,	Q86YD7	uc001qui.2");
		this.builderForward
				.setSequence("agctccagtcaggccccatccgggcccttccagaagcaacccaggagccccgagacctgcagggatgtgtgcaccctgacccctgacgcatagccctgcacctgcagccagctggcctcgggcttgaaaacatggcgggtgcgctccaattcacggtggtttccaagcgcattctggaggagaaaacacatgagtgtgtggtcagggttctctgctgacagacctaccgtggggaagaaagagaagctctgaagatggatcatggccgtgactgcatgtcaaggagaatctccatgatgacacggaggcctacgtcgagatagagtaaatatggtccaattaaaaggtgaccagaggcagtataaaaggcagcctggaaagcagaggtccctctctgccccttcctccgtcttcctggatgctgcatcgcttccagcggggctgctccagcacctgcccatctcagcgccagccggggaaagaaagtagacgtgtaatttcaggtgacccgacaatcaacccctgaaaaaggcggtcataaaacccccaggagacgaagatgatggcacgtcgtgaccccaaacctggggcaaagagactggtgagagcccagaccctccagaagcagcggagggccccagttgggccaagggctcccccgcccgatgaagaagatcccaggctcaagtgcaaaaactgtgaggcctttggccacacggccagaagtaccaggtgccccatgaagtgctggaaggcagccctggttccaccgaactttggggaaaaggaagggaaggaaaacctgaaaccatggaagccccaggttgaagcgaaccctgggcccttgaacaaggataagggagagaaggaagagagaccaaggccacaagacccgcagaggaaggctctcctccacatattttccaggaaacctccagagaagccgctgccaaatcaaaaaggatccacggaatcttctgattatctgagggttgcaagcgggccaatgccggtccacacaaccagtaagaggccgcgtgtggaccctgtcctctctgatcgctcagctaccgaaatgtctgacaggggctccgtcttagcttcactgtctcccctcagaaaagccagtctgagctcctcctcaagtcttggaccaaaggaaagacagacaggggctgcggccgacatccctcagactgcagtcaggcaccagggccccgagcctctcctcgtggtgaagccgacacacagcagccctgcgggtggctgtcgagaagttccccaggctgcctccaaaacccacggcctgctccaggccgtcagcccccaggcacaagacaaacgtcctgcggtgacctcacagccctgcccaccagccgccacacacagcttgggcctaggctccaatctcagcttcgggccaggagccaagagatctgccccggctccgattcaggcttgcctgaacttccccaagaaaccgagactgggtcccttccagatccccgaaagcgccatccagggaggtgagctgggggccccggagaatctccaacctccgccagccgcaaccgaacttggaccacgtacgtcaccccagacaggcacgaggacacccgcccaggtgcttagcggcgaccggcagcctccgcacagcagaccttgcctgcctactgcccaggcctgcaccatgtcccatcacccagcggccagccatgatggggcccagcctctcagagtgctctttcggagactggaaaacggacgctggagctccagcctgctgacggccccctcatttcactctcctgagaagccgggagccttcctcgctcagagccctcatgtctcagagaagtctgagggtccctgtgttcgtgtcccaccaagcgtcctctatgaggaccttcaggttccctcctcctcagaggacagcgattctgacctggagtgagactgcaggtggcaggggctccttggcctccagctcccgtgacttggaggggactgtgggactgaggagcacagagcagagagcagactctgtgcggtgactccgaagctccccggctgtggcccttctgtggatgtgggagcccaggccaggcagggagcagatgcagggactctgccacgttgaattctggtgagggacattgtagttcgcatggttctctggaaacgcgccaggaaaagcttccgtgccagtgattcgttgcctcagaaactgcatgacgcgcaggagtcagacttccgctgggacgtcaataggaaacgggggaattactgtgtatttgctctctagacgactgaataagggaaaagttagggaaccctgagaggtgcagcccttccgctgtgccccgccctgagagcagtgtttcggacgctgggaagcgcgctgtgcaaagcgctctcggggtctttcctcagcctcgaaaagtgggctctggaatccctttgtaaataggtgtgttgaatttgttttgaagtgaataaaattctcaaaaagatga"
						.toUpperCase());
		this.builderForward.setGeneSymbol("FAM90A1");
		this.infoForward = builderForward.build();
		// RefSeq NM_018088.3

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 12, 8376100,
				PositionType.ZERO_BASED), "G", "");
		Annotation annotation1 = new DeletionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(5, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("377del", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Pro126Glnfs*18)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FRAMESHIFT_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc001uew_3() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001uew.3	chr12	+	123868703	123893900	123868745	123892250	8	123868703,123873979,123875176,123879593,123880891,123888119,123889430,123892039,	123868755,123874101,123875333,123879813,123880979,123888179,123889621,123893900,	Q9NQR1-2	uc001uew.3");
		this.builderForward
				.setSequence("ctgggtttcccgggagatcccaggcggtgacagagtggagccatggctagaggcaggaagatgtccaagccccgcgcggtggaggcggcggcggcggcggcggcggtggcagcgacggccccgggcccggagatggtggagcggaggggcccggggaggccccgcaccgacggggagaacgtatttaccgggcagtcaaagatctattcctacatgagcccgaacaaatgctctggaatgcgtttcccccttcaggaagagaactcagttacacatcacgaagtcaaatgccaggggaaaccattagccggaatctacaggaaacgagaagagaaaagaaatgctgggaacgcagtacggagcgccatgaagtccgaggaacagaagatcaaagacgccaggaaaggtcccctggtaccttttccaaaccaaaaatctgaagcagcagaacctccaaaaactccaccctcatcttgtgattccaccaatgcagccatcgccaagcaagccctgaaaaagcccatcaagggcaaacaggccccccgaaaaaaagctcaaggaaaaacgcaacagaatcgcaaacttacggatttctaccctgtccgaaggagctccaggaagagcaaagccgagctgcagtctgaagaaaggaaaagaatagatgaattgattgaaagtgggaaggaagaaggaatgaagattgacctcatcgatggcaaaggcaggggtgtgattgccaccaagcagttctcccggggtgactttgtggtggaataccacggggacctcatcgagatcaccgacgccaagaaacgggaggctctgtacgcacaggacccttccacgggctgctacatgtactattttcagtatctgagcaaaacctactgcgtggatgcaactagagagacaaatcgcctaggaagactgatcaatcacagcaaatgtgggaactgccaaaccaaactgcacgacatcgacggcgtacctcacctcatcctcatcgcctcccgagacatcgcggctggggaggagctcctgtatgactatggggaccgcagcaaggcttccattgaagcccacccgtggctgaagcattaaccggtgggccccgtgccctccccgccccactttcccttcttcaaaggacaaagtgccctcaaagggaattgaattttttttttacacacttaatcttagcggattacttcagatgtttttaaaaagtatattaagatgccttttcactgtagtatttaaatatctgttacaggtttccaaggtggacttgaacagatggccttatattaccaaaacttttatattctagttgtttttgtactttttttgcatacaagccgaacgtttgtgcttcccgtgcatgcagtcaaagactcagcacaggttttagaggaaatagtcaaacatgaactaggaagccaggtgagtctcctttctccagtggaagagccgggaccttccccctgcacccccgacatccagggacggggtgtgaggaagacgctgcctcccaatggcctggacgggatgtttccaagctcttgttcccctaacgtctcaacaggcgctcactgaagtgtatgaatattttttaaaaaggtttttgcagtaagctagtcttcccctctgctttctcgaaagcttactgagccctgggccccaagcacgggccgggcatagatttcctcttccacaagctgccgcttttctgggcaccttgaagcatcagggcgtgaaatcaaactagatgtgggcagggagagggttgcttacctgccctgctggggcagggtttcctgaaactgggttaattctttatagaaatgtgaacactgaatttattttaaaaaataataataaaaatttaaaaaaattaaaaataaaaaaaaccacagaaaacaactttacatgtatataggtcttgaagtgagtgaagtggctgcttttttttttttttttttttgcttttttttgctttttgtagaagagattgagaatggtactctaatcaaaaataaagttttgtagtgggaccagaaattacttacctgacatccacccccattccccctcatcctgctggggttgaaagttccagacctgctgtcgaggccttgtgtttgtcagacacccagtgtcctcctgcaaggacgcaactgtgagctgaggtgtgagcctaggagcccaggacccctgaccccggccgctgctgccagcctcagaaaggcacccaggtgtgcaggggagcacacagggcccggcagcccccaggaatcaaggatagggctaaggttttcaccttaactgtgaaggcaggaggaataggtgactgcttcctcccgcccttcacagaactgattctcacacactgtcccttcagtccagggggccggggctcaggagccatgacctggtgtctcctgcccaccctggtcccaggtaaatgtgaatggagacaggtatgagaggctgtcctcgtctttgattcccccccaaccccacctcgggcctcacgacggtgctacctaagaaagtcttccctcccaccccccgctagcctggtcagtggtcagcaaattggaagaggatccgatgggagtgtaaatgtgagacacaatgtcttgattatacctgtttgtggtttagctttgtatttaaacaaggaaataaacttgaaaattatttgtcatcataaaaatgaaacaaattaaaatatttattgccaggcaaaaaaaaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("SETD8");
		this.infoForward = builderForward.build();
		// RefSeq NM_020382.3

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 12, 123880923,
				PositionType.ZERO_BASED), "TT", "");
		Annotation annotation1 = new DeletionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(4, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("542_543del", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Leu181Hisfs*20)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FRAMESHIFT_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc001val_2() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001val.2	chr13	-	46115431	46189874	46115596	46171140	15	46115431,46118894,46124027,46124365,46135503,46137861,46142449,46148976,46154067,46154737,46161197,46164783,46170503,46181698,46189819,	46115815,46119120,46124180,46124451,46135661,46137923,46142586,46149065,46154109,46154800,46161367,46164832,46171198,46181750,46189874,	Q5W0A0	uc001val.2");
		this.builderForward
				.setSequence("cacaaacgccggggcctgagatcgcggtggatgcacgcgcgggagcctggcttggctgttattgttgaaaactctcttggcttggactcagtttaccccctgacattgataataaagtgacgttggctgctgcagaaggagttcaccacctacgacttccccagcatgtctgctgaaaataatcagttatcaggagcatcacctcctcaccctcccacaactccccaatattccacacagaacttgccttcagagaaagaggatactgaagtagaactagatgaggaaagtctacaggatgaatctccattttctccagagggagagtctctggaggacaaagagtatctggaggaggaagaggatctggaagaggaagagtatctggggaaagaagaatacttgaaggaggaagagtatctggggaaggaagagcatctggaggaggaagagtatctggagaaggcagggtatctggaggaggaagagtatattgaagaggaagagtatctggggaaggaagggtatctggaggaggaagagtacctggggaaggaagagcatctggaggaggaagagtatctggggaaggaagggtatctggagaaggaagattatattgaggaggtagattatctggggaagaaagcgtatctggaggaggaggagtatctggggaagaaatcatatctagaagaggaaaaggctctggagaaggaagagaatctggaggaggaagaagctctggagaaggaagagaatctggatggaaaagaaaatctgtataaaaagtatctgaaagaacccaaggcaagttattcatcacaaaccatgcttcttcgtgatgcaaggagtccagacgctggcccctctcaggtgaccaccttcttgactgtcccgttgactttcgctaccccttctcctgtctctgaatctgccacagagtcttctgagttgcttctgacattgtacaggaggagccaggccagtcagacagactggtgctacgacagaactgccgtaaaatctttaaaatcgaaatcagaaacagagcaagaaaccaccacgaagctggctccggaagagcatgttaacactaaagtacagcaaaaaaaggaagaaaatgtcctggagtttgcttctaaagagaacttttgggatggtataacagatgagtccattgacaagctggaagtggaagatttagatgaaaactttttgaacagctcctatcagacagtatttaaaacaataatcaaagaaatggctgctcacaatgaactggaagaggattttgacattcccctaactaagctactggaaagtgaaaacagatggaaactggtaattatgctgaagaaaaattatgaaaagttcaaggaaacaatcttacggattaagaggagacgtgaagctcaaaagttaacagagatgaccagtttcacatttcatttaatgagcaaaccaacacctgagaagcctgagacagaagaaatccaaaagcctcaacgtgttgttcatcataggaagaaattagaacgagataaggaatggatacagaagaagacagtggtgcatcaaggtgatggaaaattaattctctaccccaacaagaatgtctatcaaattctctttcctgatgggacaggccagatacattatccatcaggaaacctggctatgctcatcttatatgcgaaaatgaaaaagttcacatacatcattctggaagacagtctagaagggaggatccgggcccttatcaacaactcaggcaatgctaccttctatgatgaaaatagtgatatctggttgaacctgagctccaacctgggctactacttccccaaagacaaacgccagaaggcctggaactggtggaatctgaacatccatgtccacgcaccccctgtccagcccatctccttgaaaatcaatgagtacatccaggtacaaataaggagccaggataagatcatcttctgcttcacctatgaacagaagcagatttgtttaaacctgggcaccaggtacaagtttgtgatcccagaggtgctgagcgaaatgaagaagaaaaccatcctggaggcagaacccggcccaacagcccagaagatccgggtccttctggggaaaatgaataggctcctgaattacgcgaccacccctgatctggaaaacttcatagaggccgtcagtatatcactgatggacaacaagtacctgaagaagatgctctctaaactctggttttaaggcggggcttatctggcatccaccaaatgccttaggaagttggagaccttgggagctccagggaaaaaaaacccaaagacctgttgaccttttatgagagttggtttgtaatttgggacctgtcgtggctccttgatttgaagtaaacatttttggcagaagggc"
						.toUpperCase());
		this.builderForward.setGeneSymbol("FAM194B");
		this.infoForward = builderForward.build();
		// RefSeq NM_182542.2

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 13, 46170725,
				PositionType.ZERO_BASED), "ACTCTTCCTCCTCCAGAT", "");
		Annotation annotation1 = new DeletionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(2, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("404_421del", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Glu135_Leu140del)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.DISRUPTIVE_INFRAME_DELETION), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc002axo_4() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc002axo.4	chr15	+	74528629	74628482	74529060	74628394	19	74528629,74536325,74554780,74559018,74560682,74564043,74565111,74572303,74573008,74574118,74588094,74622529,74623003,74623321,74623543,74625019,74626221,74627315,74628265,	74529081,74536489,74554914,74559128,74560799,74564135,74565232,74572433,74573142,74574190,74588289,74622695,74623092,74623453,74623637,74625186,74626308,74627429,74628482,	Q8N5R6-6	uc002axo.4");
		this.builderForward
				.setSequence("agtgttgcccaggtgacccagctaggtggaagagcttattgttttccaggcctgggcagagacagggcccccctgccccatctctccaccgtcctaggtgtgccaagagtcaattgcctcattgctgaccctgtccagctggccatggccctcaacccccaaggcccttccacccacagaccatctcgctgctgagagatccaggacctgctcccacctggccaccctccccctccccccacatccaggccccagggctggtgtgtggcacccctgagaccacattgacctccatactgtctactacccataaggactccaagacgcccaggccagctgtctgggcaggactgattcctgatcacccactgataccaagtactcatccccaagattgttaaacaaggccagacactcctggcctcaagaggatgggactgaaaaacaaaaagaacactgaagacccagaggagcccctgatcgcctcccagagcacggaacctgagatcggtcacctgtctccctctaagaaggagaccatcatggtcaccctccatggggctaccaacctgcctgcctgcaaggatggctccgagccgtggccctatgtggtggtgaaaagcacatctgaggaaaagaacaatcagagctccaaggcagtcacatctgtgacctcagagcccaccagagcccctatctggggggacacggtgaatgtggagatccaagctgaggatgcagggcaagaagatgtgatcctcaaggtggtggacaacagaaagaaacaggagttgttgtcctacaaaatccccatcaagtacctgcgtgtcttccacccctaccactttgagctggtgaagcccactgagtctgggaaagccgatgaagccactgccaagacccagttgtacgcaacagtcgttcggaagagcagcttcataccccgctacatcggctgcaaccacatggctctggagatctttctccggggagtcaacgagcccctggccaacaaccccaaccccatagtggtgattgcccgggtcgttcccaactacaaggaatttaaggtcagccaggctaacagggacctggcctctgtggggctgcccatcaccccactgtccttccctatcccgtccatgatgaactttgacgtgcctcgcgtcagccagaacggatgccctcagctgtccaagcctgggggacccccagagcagcccctgtggaatcagtccttcctcttccaaggccgagatggagctaccagcttctcagaagacacagccctggtgctggagtactactcctcaacttcaatgaaaggcagccagccgtggaccctcaaccagcccctgggcatctctgtgttgccgctaaagagccgtttgtaccagaagatgctgacagggaaaggcttggacgggcttcacgtggagcggctccccatcatggacaccagcctgaaaactatcaatgatgaggcccccacagtggctctctccttccagctgctttcctctgagagaccagaaaacttcttgacaccaaacaacagcaaggctcttcctaccttggaccccaagatcctggataagaagctgagaaccatccaagagtcctggtccaaggacacagtgagctccacaatggacttgagcacgtccactccacgagaagcagaggaggaacctctggtgcctgagatgtcccatgacacagagatgaacaactaccggcgggccatgcagaagatggcagaggacatcctgtctctgcggagacaggccagcatcctggaaggagagaaccgcatactgaggagccgcctggcccagcaggaggaggaagaggggcagggcaaagccagtgaggcccagaacacggtgtccatgaagcagaaactgctgctgagtgagctggatatgaagaaactgagggacagggtgcagcatttgcagaatgagctgattcgaaagaatgatcgagagaaggagctgctccttctgtatcaggcccagcagccacaggccgctctgctgaagcagtaccagggcaagctgcagaagatgaaggcgctggaggagactgtgcggcaccaagagaaggtgatcgagaagatggagcgggtgctggaggacaggctgcaggacaggagcaagccccctcctctgaacaggcagcagggaaagccctacacgggcttccctatgctctcagcctctggccttcccttgggttctatgggagagaacctgccggttgaactttactcggtgctgctggcagaaaacgcgaagctgcggacggagctggataagaaccgccaccagcaggcccccatcattctgcagcaacaggccctgccggatctcctctctggtacttcagacaagttcaacctcctggccaagctggaacacgctcagagccggatcctgtccctggaaagccagttagaggactcagctcgacgctggggacgagagaagcaggatctggccacacggctgcaggagcaagaaaaaggtttcaggcacccctcgaactccatcatcatagaacagcctagtgccctcacccactccatggacctcaagcagccctcagagctggagcccctgctgcccagctcagactctaagctcaacaagcccttgagcccccagaaggagaccgctaactctcagcagacctgagccccagagcaggcctccttccctgtgtgctggggagtctcatcaccgccccctaaaaatgacgttattaaatgttgtagctctgtgaaaaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("CCDC33");
		this.infoForward = builderForward.build();
		// RefSeq NM_025055.4

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 15, 74536403,
				PositionType.ZERO_BASED), "AAG", "");
		Annotation annotation1 = new DeletionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(1, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("100_102del", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Lys34del)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.INFRAME_DELETION), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc010bky_2() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc010bky.2	chr15	-	78206558	78219188	78207525	78212366	18	78206558,78207739,78207981,78208168,78208835,78209358,78210761,78211106,78212065,78212345,78212561,78213677,78213880,78214062,78214370,78215271,78217277,78219083,	78207653,78207893,78208085,78208271,78208927,78209434,78210830,78211657,78212153,78212453,78212648,78213784,78213962,78214113,78214414,78215352,78217397,78219188,	A6NDK9	uc010bky.2");
		this.builderForward
				.setSequence("ggtgactggaggtgttcgctgatgtggccccaaccctacctccctcccgaccccatgatgccagaagaaactcgatggaacaaattggcagcagtcaagaaaaagctaaaagaatatcagcaaaggaagagccctggtgttccagcaggagcaaagacaaaaaagaaaaaaactggcagtagccctgagacaaccacttctggtggttgccactcacctggggatagccagtaccaagaactagcagtagccctggagtcaagctccgtgacaatcaatcaactcaatgaaaacatagaatcattgaaacagcagaagaaacaagtggaacatcagctggaagaaataacgcaaagaaagcaaacaatgaaatacacaaagcacaaacagagcagttagagacaatcaacatcctcacattggaaaaggcagacttgaagaccaccctttaccatactaaacgtgctgcccgacacttcgaagaagagtccaaggatctggctggccgcctgcaatacgccttgcagcgtattcaagaattggagcgggctctctgtgctgtgtctacacagcagcaggaagaggacaggtcctcgagctgcagagaagcggtcctccagtggcggttacagcagaccataaaggagcaggcactgctgaacgcacacgtgacacaggtgacagagtcactaaaacaagtccagctagagcaggacgaatatgctaaacacataaaaggagagagggcccggtggcaggagaggatgtggaaaatgtcggtggaggctcgaacattaaaggaagagaagaagcgtgacatacatcggatacaggagctgaagaggagcttgtccgaactcaaaaaccagatggctgagcccctatccctggcgcccccagcagtgacctctgtggtggaacagctacaagatgaggccaaacacctgaggcaggaggtggaaggtctggagggaaagctccaatcccaggtggaaaacaatcaggccttgagtgtcctgagcaaggaacaaaagcagagactccaggagcaggaggagagactccaggagcaggaggagatgatccgagagcaggaggagatgctccgagagcaggaggcgcagagggtgcgggagctggagagactgtgtgaacaaaacgagaggcttcgggagcagcagaagatgctacaggagcagggtgagaggctgcaaaagcaggagcagaggctacgcaagcaggaggagaggctgcaaaaggaggaggagaggctgcaaaagcaggaaaagaggctgtgggaccaggaggagaggctgtggaagaaggaggacaggctacaaaagcaggaggagaggctcgtgctcttccagaaccacaagctcgacaagcagctggccgagccacagtgcagcttcgaggatctgaacaatgagaaaaagagcgcactgcagttggagcagcaagtaaaggagctgcaggagaagctagacgaggagcacctagaagctgccagccagcggaaccaacagctagagacccggttgagcctcgtggctctccctggagaaggagatggaggagaacatctggacagtgaggaggaggaggcgcctcggcccacgccaaacatcccagaggacctggagagccgggaggccacgagcagctttacggacctcccgaaggagaaggcagatgggacggagcaggtggagagacgagagcttggattcgtccagccttctggagtgacagatggcatgagagagtccttcaccgtatatgaaagccagggggcagtgccaaacacgtggcaccaggagatggaagatgtcatcaggctggcccagaaggaggaggagatgaaggtgaatctgctggagctgcaagagctggtgttgccccttgtgggcaaccatgaggggcatggcaaattcctcaccgctgcccagaaccctgctgatgagcccactccagggcccccagccccccaggaactgggggctgccggtgagcaggatgatttttatgaagtgagcctggacaacaatgtggagcctgcaccaggagcggccagggagggttctccccatgacaaccccactgcacagcagatcgtgcagctgtctcctgtaatgcaggacacctaggagcacccaggcttgcccagcaaaccctgcgtgccattcttttaccaggcagccgagaacagggagataaacatcatcatcttctaagagctggtcaagaaatttaaaacaacaacaacaacaaaaagttacagggttcatctcctacacaattcatttactccatttgaatgctagagccactcacatttatttgtgtttctaatttaccgtttaaatttatttgtaaaaagttaagggagagttggtctttccctgatgttctttctggcatcctttagcatttttattttttacttgataattgtaggtcattagcacgcatatcgagtttgcccttaggtggtgggaattcatacacacaaagacccactatttgcacaaaactattcttgctggtttggaacaggctgccatgctttttaaatgttattgcagcacatatattcattacagaattcagataaaatgtgcttatgttctgctattacgtttgattgaatcctaaccacagtgagctcttcattagctcaatatgtggtttgccctcaagtgcgcactgtttattactttgtaatatgccactgtgagtactgacatttagagttgtttaaaggccgagaactggaaacagcctttcccctattttctgtgtattggggatgggagtcataacattttggggagctttttaaatctcacagaagaggaaagtggcctgctctggcaggtgtgtgcaggatacagtgtgtttcatttgttctggtgccaagaatgagcactgtactatggtagttcccttaggatttgtgtgtgctctgggcttatgaagatattgcatcatgagctgcagcagttgtaccctttctcgatgacctaaaaagggattatttctgaggaatgaaaggctcccatcattgactgtggatgtggaaaaccttttctagcgtagagcatttatatctac"
						.toUpperCase());
		this.builderForward.setGeneSymbol("LOC645752");
		this.infoForward = builderForward.build();
		// RefSeq NR_027024.1

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 15, 78208898,
				PositionType.ZERO_BASED), "CTC", "");
		Annotation annotation1 = new DeletionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(13, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("842_844del", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Glu281del)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.DISRUPTIVE_INFRAME_DELETION), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc002ghm_3() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc002ghm.3	chr17	+	7465308	7475287	7466393	7474801	12	7465308,7466382,7467941,7468275,7468756,7469033,7470243,7470286,7473673,7473998,7474200,7474690,	7465580,7467108,7468181,7468387,7468904,7469081,7470285,7470322,7473811,7474082,7474251,7475287,	Q9H4L4	uc002ghm.3");
		this.builderForward
				.setSequence("gagtggcggcggcggcggtggcgctggtggcggcggtggcggaggtggaggtggaggtggaagctgaagctgaagcagagccagaggcggcgcggcggggtgctcggcggcgcggcacgcggcccagaagcgttggaatcctgaattgagacggctgcgcctaaagacagcaggagtggcggggccgccgccgtcgccggagagatgagccgggaagcttgaggccggagacgcccgccttcgggcccgtccgcccggcttccccgctcccgggtactggaagatgaaagagactatacaagggaccgggtcctgggggcctgagcctcctggacccggcatacccccagcttactcaagtcccaggcgggagcgtcttcgttggcccccacctcccaaaccccgactcaagtcaggtggagggtttgggccagatcctgggtcagggaccacagtgccagccagacgcctccctgtcccccgaccctcttttgatgcctcagcaagtgaagaggaggaagaagaggaggaggaggaggatgaagatgaagaggaggaagtggcagcttggaggctgcccccaagatggagtcagctgggaacctcccagcggccccgcccttcccgccccactcatcgaaaaacctgctcacagcgccgccgccgagccatgagagccttccggatgctgctctactcaaaaagcacctcgctgacattccactggaagctttgggggcgccaccggggccggcggcggggcctcgcacaccccaagaaccatctttcaccccagcaagggggtgcgacgccacaggtgccatccccctgttgtcgttttgactccccccgggggccacctccaccccggctgggtctgctaggtgctctcatggctgaggatggggtgagagggtctccaccagtgccctctgggccccccatggaggaagatggactcaggtggactccaaagtctcctctggaccctgactcgggcctcctttcatgtactctgcccaacggttttgggggacaatctgggccagaaggggagcgcagcttggcaccccctgatgccagcatcctcatcagcaatgtgtgcagcatcggggaccatgtggcccaggagctttttcagggctcagatttgggcatggcagaagaggcagagaggcctggggagaaagccggccagcacagccccctgcgagaggagcatgtgacctgcgtacagagcatcttggacgaattccttcaaacgtatggcagcctcatacccctcagcactgatgaggtagtagagaagctggaggacattttccagcaggagttttccaccccttccaggaagggcctggtgttgcagctgatccagtcttaccagcggatgccaggcaatgccatggtgaggggcttccgagtggcttataagcggcacgtgctgaccatggatgacttggggaccttgtatggacagaactggctcaatgaccaggtgatgaacatgtatggagacctggtcatggacacagtccctgaaaaggtgcatttcttcaatagtttcttctatgataaactccgtaccaagggttatgatggggtgaaaaggtggaccaaaaacgtggacatcttcaataaggagctactgctaatccccatccacctggaggtgcattggtccctcatctctgttgatgtgaggcgacgcaccatcacctattttgactcgcagcgtaccctaaaccgccgctgccctaagcatattgccaagtatctacaggcagaggcggtaaagaaagaccgactggatttccaccagggctggaaaggttacttcaaaatgaatgtggccaggcagaataatgacagtgactgtggtgcttttgtgttgcagtactgcaagcatctggccctgtctcagccattcagcttcacccagcaggacatgcccaaacttcgtcggcagatctacaaggagctgtgtcactgcaaactcactgtgtgagcctcgtaccccagaccccaagcccataaatgggaagggagacatgggagtcccttcccaagaaactccagttcctttcctctcttgcctcttcccactcacttccctttggtttttcatatttaaatgtttcaatttctgtatttttttttctttgagagaatacttgttgatttctgatgtgcagggggtggctacagaaaagcccctttcttcctctgtttgcaggggagtgtggccctgtggcctgggtggagcagtcatcctcccccttccccgtgcagggagcaggaaatcagtgctgggggtggtgggcggacaataggatcactgcctgccagatcttcaaacttttatatatatatatatatatatatatatatatatatatatatatatatatatatatatatatatatatatataaaaatatataaatgccacggtcctgctctggtcaataaaggatcctttgttgatacgtaaaaaaaaaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("SENP3");
		this.infoForward = builderForward.build();
		// RefSeq NM_015670.5

		// This deletion leads to position shifting downstream.
		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 17, 7470288,
				PositionType.ZERO_BASED), "G", "");
		Annotation annotation1 = new DeletionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(7, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("1310del", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Gly437Valfs*5)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(
				ImmutableSortedSet.of(VariantEffect.FRAMESHIFT_TRUNCATION, VariantEffect.SPLICE_REGION_VARIANT),
				annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc002hft_1() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc002hft.1	chr17	+	29161165	29196729	29161408	29196672	13	29161165,29164223,29167634,29170930,29171852,29182160,29183972,29185178,29187450,29192721,29195350,29196265,29196513,	29163066,29164332,29167799,29171057,29171934,29182345,29184130,29185341,29187630,29192818,29195430,29196408,29196729,	Q96QE3	uc002hft.1");
		this.builderForward
				.setSequence("ccatgcaaaaagcgaaagaaagatgatgacacatctacctgcaaaacaattacaaaatatttatcaccactagggaagactagagacagggtttttgctccaccaaaacctagtaatattctggattattttagaaagacttcacccacaaatgagaagacacaattagggaaagagtgcaagataaagtcacctgaatcagtacctgttgacagcaacaaagactgtacgacacctttggaaatgttctcaaatgtagagtttaagaagaaaagaaagagggttaatttatctcatcaactaaataatattaaaactgaaaatgaagctccaattgaaattagtagcgacgatagcaaagaagactatagtttaaataatgattttgtggaaagtagtacttctgttttacgttacaagaaacaagtagaggtacttgcagaaaacattcaagatacaaaaagtcaaccaaatactatgacctccctgcaaaattctaaaaaagtaaatcctaaacaagggaccacaaaaaatgacttcaaaaagttgagaaaaaggaaatgcagagatgtagtagatctatctgaaagcttacccttggcagaggaactaaatttgcttaaaaaagatggtaaagatactaaacagatggagaatactacaagccatgcaaactctagagataacgtaactgaagcagcccagttaaatgatagtataataactgtctcatatgaggaatttttaaaaagtcacaaggaaaataaagtggaagagataccagactctacaatgtcaatttgtgttccttctgaaactgtcgacgaaatagtcaaaagtggttatataagtgaatcagaaaactccgaaatttcccagcaggtacgctttaagacagttactgttcttgcacaggttcaccctattccgcccaaaaagacagggaaaataccccgaattttcttgaaacaaaagcaatttgaaatggaaaatagtttatctgatcctgagaatgaacagacagttcagaaaagaaaatctaatgttgttatacaggaggaagaattagaattggctgttttggaagctggaagttctgaagctgtgaaaccaaaatgcactctagaagaaagacagcaatttatgaaagcatttaggcagccagcatcagatgcacttaaaaatggagttaaaaagtcttctgataagcagaaagaccttaatgaaaaatgtctatatgaagtaggaagagatgataattctaaaaaaatcatggaaaattctggtatccaaatggtttcaaaaaatggcaatttacagttacacactgataaaggaagttttctgaaggagaaaaataaaaagctaaagaagaagaataagaaaacattagatactggggctattccaggcaaaaacagagagggaaacactcaaaagaaagaaacaacctttttcttaaaagagaaacaatatcaaaatagaatgagtttaagacaaaggaaaacagagtttttcaaaagcagcactttatttaacaatgaaagtcttgtttatgaagatatagcaaatgatgaccttctaaaggtttcctctctgtgtaacaataataaattgtcaagaaaaaccagcataccagttaaagatattaagcttacacagtctaaagctgaatctgaagccagcttgctaaatgtttccacgcccaagtcaactagaagatctggaagaattagcagcacacctactacagaaaccattagaggtattgattctgacgatgtacaagataatagtcaactaaaggcttccactcaaaaagcagccaacttatcggaaaagcacagcttatatacagcagaattaataacagtaccctttgattcagagagccctattagaatgaaattcaccagaattagtactcccaaaaaatctaagaaaaaatctaacaaaagatctgagaaatctgaagcaactgatggaggttttacttctcagattagaaaggcaagcaatacttcaaaaaacatatcaaaagcaaaacaattgattgaaaaagcaaaagctttacacatcagtaggtcaaaggtgactgaagaaatagcgatacccttaaggcgctcctctagacatcagacacttcctgaaaggaagaaattgtcagaaacagaagattctgttataataatagattcaagtcctactgctttaaagcatccagagaaaaatcagaagaaacttcagtgtttgaatgatgtgctaggaaaaaaacttaacacatccactaaaaatgtacctggaaaaatgaaagtcgctcctttatttcttgtcagaaaagcacaaaaagcagctgatcctgtccctagttttgatgaaagcagtcaagatacatctgaaaaatctcaggattgtgatgttcaatgtaaagcaaagcgtgacttcctaatgagtggtttgccagatttgttgaaacggcaaattgcaaagaaagctgctgcgctggatgtgtacaatgcagtgagtaccagtttccagagagtcgtacatgtgcaacaaaaggatgatgggtgttgtttgtggcatttgaaaccaccctcttgtcctctcttaactaaatttaaagaactgaacactaaagtaatagatctctcaaaatgtggtattgctcttggtgaattttcaacattgaattcaaagttgaaaagcggtaactctgctgctgtgttcatgaggacaaggaaggaatttactgaagaagtaagaaatcttttgcttgaggaaattaggtggtcaaatcctgaattttcattgaaaaaatattttcccttactcctaaaaaaacaaattgagcaccaagtactttcttccgagtgtcatagtaaacaagaactggaggctgatgtcagccataaagaaaccaaaaggaaactcgtagaagcagaaaattctaagtcaaaaagaaagaaaccaaatgagtattcaaaaaatctggagaagaccaataggaagtcagaagaacttagcaaaagaaacaactcttctgggataaagctagattcttccaaagattctggaactgaagacatgctttggacagaaaagtatcaacctcagactgccagtgaacttataggaaatgagttagctataaaaaagttacatagttggttgaaagactggaaaagaagagctgaattggaagaaaggcagaatctgaagggaaaaagagatgagaaacatgaagatttctcgggtggcatagactttaaaggcagttcagatgatgaagaagagagtcgtctttgcaatactgtccttataacagggccaacaggagtgggaaaaactgctgcagtgtatgcttgtgcccaggagcttggatttaagatatttgaagtgaatgcctcttcccagcgcagtggtagacaaattctatctcagttgaaggaagctactcagtcccatcaagtagacaaacaaggtgtaaactcacaaaaaccctgtttttttaatagctactacataggcaagtcaccaagtaagtaaactattttccttaagccataatgttttgaaaaaaataaaatctaagaaagtaatgtt"
						.toUpperCase());
		this.builderForward.setGeneSymbol("ATAD5");
		this.infoForward = builderForward.build();
		// RefSeq NM_024857.3

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 17, 29161650,
				PositionType.ZERO_BASED), "GTCAAT", "");
		Annotation annotation1 = new DeletionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(0, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("243_248del", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Leu82_Gln83del)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.DISRUPTIVE_INFRAME_DELETION), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc002hfs_1() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc002hfs.1	chr17	+	29159022	29222295	29159365	29221931	23	29159022,29161165,29164223,29167634,29170930,29171852,29182160,29183972,29185178,29187450,29192721,29195350,29196265,29196513,29203391,29204433,29205020,29206440,29214209,29219624,29220311,29221581,29221852,	29159431,29163066,29164332,29167799,29171057,29171934,29182345,29184130,29185341,29187630,29192818,29195430,29196408,29196664,29203568,29204567,29205114,29206505,29214390,29219806,29221168,29221740,29222295,	Q96QE3	uc002hfs.1");
		this.builderForward
				.setSequence("tttcctccgaagctctgtggtccgatctgcggtccgcttgctttccctgcccggtcccgagcgctcagcctgaagcgccgctttcgagggcaccctgcatacactggccgcgcctcagggatctcattgcccgcgctttctcattgcctctttccgtgttcgattcggctgatctgggcccagcctccgctcccgctctctgtcggtgggcgcgggggaatccgaaacggctcagcagaatcccagcagcttgctgctactggagcgggccgcctccatggcctccaggcaggccgggctggaccgcgtgaggtcctaggagacgggattccgggaagcggggagtatggtgggggtcctggccatggcggctgcagctgctccgcctcccgtgaaggactgcgagattgagccatgcaaaaagcgaaagaaagatgatgacacatctacctgcaaaacaattacaaaatatttatcaccactagggaagactagagacagggtttttgctccaccaaaacctagtaatattctggattattttagaaagacttcacccacaaatgagaagacacaattagggaaagagtgcaagataaagtcacctgaatcagtacctgttgacagcaacaaagactgtacgacacctttggaaatgttctcaaatgtagagtttaagaagaaaagaaagagggttaatttatctcatcaactaaataatattaaaactgaaaatgaagctccaattgaaattagtagcgacgatagcaaagaagactatagtttaaataatgattttgtggaaagtagtacttctgttttacgttacaagaaacaagtagaggtacttgcagaaaacattcaagatacaaaaagtcaaccaaatactatgacctccctgcaaaattctaaaaaagtaaatcctaaacaagggaccacaaaaaatgacttcaaaaagttgagaaaaaggaaatgcagagatgtagtagatctatctgaaagcttacccttggcagaggaactaaatttgcttaaaaaagatggtaaagatactaaacagatggagaatactacaagccatgcaaactctagagataacgtaactgaagcagcccagttaaatgatagtataataactgtctcatatgaggaatttttaaaaagtcacaaggaaaataaagtggaagagataccagactctacaatgtcaatttgtgttccttctgaaactgtcgacgaaatagtcaaaagtggttatataagtgaatcagaaaactccgaaatttcccagcaggtacgctttaagacagttactgttcttgcacaggttcaccctattccgcccaaaaagacagggaaaataccccgaattttcttgaaacaaaagcaatttgaaatggaaaatagtttatctgatcctgagaatgaacagacagttcagaaaagaaaatctaatgttgttatacaggaggaagaattagaattggctgttttggaagctggaagttctgaagctgtgaaaccaaaatgcactctagaagaaagacagcaatttatgaaagcatttaggcagccagcatcagatgcacttaaaaatggagttaaaaagtcttctgataagcagaaagaccttaatgaaaaatgtctatatgaagtaggaagagatgataattctaaaaaaatcatggaaaattctggtatccaaatggtttcaaaaaatggcaatttacagttacacactgataaaggaagttttctgaaggagaaaaataaaaagctaaagaagaagaataagaaaacattagatactggggctattccaggcaaaaacagagagggaaacactcaaaagaaagaaacaacctttttcttaaaagagaaacaatatcaaaatagaatgagtttaagacaaaggaaaacagagtttttcaaaagcagcactttatttaacaatgaaagtcttgtttatgaagatatagcaaatgatgaccttctaaaggtttcctctctgtgtaacaataataaattgtcaagaaaaaccagcataccagttaaagatattaagcttacacagtctaaagctgaatctgaagccagcttgctaaatgtttccacgcccaagtcaactagaagatctggaagaattagcagcacacctactacagaaaccattagaggtattgattctgacgatgtacaagataatagtcaactaaaggcttccactcaaaaagcagccaacttatcggaaaagcacagcttatatacagcagaattaataacagtaccctttgattcagagagccctattagaatgaaattcaccagaattagtactcccaaaaaatctaagaaaaaatctaacaaaagatctgagaaatctgaagcaactgatggaggttttacttctcagattagaaaggcaagcaatacttcaaaaaacatatcaaaagcaaaacaattgattgaaaaagcaaaagctttacacatcagtaggtcaaaggtgactgaagaaatagcgatacccttaaggcgctcctctagacatcagacacttcctgaaaggaagaaattgtcagaaacagaagattctgttataataatagattcaagtcctactgctttaaagcatccagagaaaaatcagaagaaacttcagtgtttgaatgatgtgctaggaaaaaaacttaacacatccactaaaaatgtacctggaaaaatgaaagtcgctcctttatttcttgtcagaaaagcacaaaaagcagctgatcctgtccctagttttgatgaaagcagtcaagatacatctgaaaaatctcaggattgtgatgttcaatgtaaagcaaagcgtgacttcctaatgagtggtttgccagatttgttgaaacggcaaattgcaaagaaagctgctgcgctggatgtgtacaatgcagtgagtaccagtttccagagagtcgtacatgtgcaacaaaaggatgatgggtgttgtttgtggcatttgaaaccaccctcttgtcctctcttaactaaatttaaagaactgaacactaaagtaatagatctctcaaaatgtggtattgctcttggtgaattttcaacattgaattcaaagttgaaaagcggtaactctgctgctgtgttcatgaggacaaggaaggaatttactgaagaagtaagaaatcttttgcttgaggaaattaggtggtcaaatcctgaattttcattgaaaaaatattttcccttactcctaaaaaaacaaattgagcaccaagtactttcttccgagtgtcatagtaaacaagaactggaggctgatgtcagccataaagaaaccaaaaggaaactcgtagaagcagaaaattctaagtcaaaaagaaagaaaccaaatgagtattcaaaaaatctggagaagaccaataggaagtcagaagaacttagcaaaagaaacaactcttctgggataaagctagattcttccaaagattctggaactgaagacatgctttggacagaaaagtatcaacctcagactgccagtgaacttataggaaatgagttagctataaaaaagttacatagttggttgaaagactggaaaagaagagctgaattggaagaaaggcagaatctgaagggaaaaagagatgagaaacatgaagatttctcgggtggcatagactttaaaggcagttcagatgatgaagaagagagtcgtctttgcaatactgtccttataacagggccaacaggagtgggaaaaactgctgcagtgtatgcttgtgcccaggagcttggatttaagatatttgaagtgaatgcctcttcccagcgcagtggtagacaaattctatctcagttgaaggaagctactcagtcccatcaagtagacaaacaaggtgtaaactcacaaaaaccctgtttttttaatagctactacataggcaagtcaccaaaaaaaataagctcccctaagaaagttgttacatcaccaagaaaagttcctccaccatcaccaaaaagtagtggaccaaagcgagcacttcctcccaaaaccttggcaaattattttaaagtatctcccaaacctaaaaataatgaagaaataggaatgcttctggaaaataataaaggaataaaaaattcttttgaacagaaacaaattactcagactaaatctacaaatgcaactaattcaaatgtcaaagacgttggagctgaagaacccagcagaaaaaatgcaacatctcttattctttttgaggaggttgatgtaatttttgatgaagatgctgggtttttgaatgcaatcaaaacattcatggcaacaactaaacgacctgtaatccttactacaagtgacccaacatttagtttaatgtttgatggctgctttgaagaaatcaagttcagtactccttccctgctaaatgttgccagctacctacaaatgatttgcttaactgagaattttagaactgatgtaaaagactttgtaaccttgttaactgcaaatacttgtgatatcagaaaaagtatcctttacttacaattctggattagaagtggaggtggagttttagaagaacgaccattaaccctttatcgtggaaatagcagaaatgtacaactagtttgctctgaacatggccttgataacaaaatttaccctaaaaatactaaaaagaaacgtgtagaccttccaaaatgtgacagtggctgtgctgagaccttgtttggccttaagaacattttttccccatctgaagacttattttcatttttaaagcacaaaatcacaatgaaggaagaatggcataaattcatccagcttcttacagaattccaaatgcggaatgtagattttttatatagtaatcttgagtttattctaccattaccagttgataccattccagaaactaaaaacttttgtggcccatcagtaactgtggatgccagtgcagcaacaaaaagtatgaattgtcttgctaggaaacactctgaaagagaacagccattgaaaaagtcccagaaaaagaaacaaaagaaaacattggtaatattagatgatagtgatctatttgacactgacttggactttcctgatcaatctattagcctgtcctctgtatcatcttcctcaaatgcagaagaaagcaaaaccggagacgaagaaagcaaagccagagacaaaggaaacaatccagagacaaagaaatctattccttgtcctcctaaaacaactgcaggaaaaaaatgttctgcccttgtttctcattgtttaaattctctctctgagttcatggataacatgtccttcttagatgcacttttaactgatgtaagggaacaaaacaaatacggtagaaatgactttagttggacaaatggaaaggttacaagtggactttgtgatgagtttagtcttgagagtaatgatggatggacttctcaaagctctggagaattaaaggcagctgcagaagctctcagctttactaaatgttcttctgctatttcaaaagcattggaaaccttgaattcttgcaagaaattaggaagagatccaaccaacgatcttactttttatgtttcacaaaagcgcaataatgtatactttagtcagtcagcagctaatttagacaatgcttggaagaggatatcagtcattaaaagtgtattttcgagtcgatctcttctctatgtgggtaatagacaagctagtataattgaatacctgccaacccttcgaaacatctgtaagactgagaagctaaaagaacaaggaaaaagtaaaagaagattcctgcactattttgaaggaattcatcttgacattccaaaagagactgtgaatactttggcagctgacttcccttaatgttccatactaacaatgctttgtatagattatcatgtggtccttaagatacatttttatattatgtggatcttcatggaaaagtatatttctcgatgtacattttaaacaaacaatttgtatatttttttattggcgggtaaatatttaaaatatttgagttacaaattttatatatgattgtaattttttttctgaattttttgtattatctgatttagctttgttggagtattttttgtatgtgagtgaactgtttctggaaggtagagttcattaagatgaactccctatttcaagtgtttatattatatattagcttaatattcagatacattatcttggctgctaacattagtgtcac"
						.toUpperCase());
		this.builderForward.setGeneSymbol("ATAD5");
		this.infoForward = builderForward.build();
		// RefSeq NM_024857.3

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 17, 29161650,
				PositionType.ZERO_BASED), "GTCAAT", "");
		Annotation annotation1 = new DeletionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(1, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("552_557del", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Ser185_Leu186del)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.DISRUPTIVE_INFRAME_DELETION), annotation1.getEffects());
	}

	/**
	 * In this test, there is a discrepancy between Mutalyzer and Jannovar. From manual inspection, I (Manuel) think
	 * that this is caused because of a discrepancy between UCSC and RefSeq.
	 */
	@Test
	public void testRealWorldCase_uc002jbc_4() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc002jbc.4	chr17	+	61627795	61671642	61628038	61666534	8	61627795,61655830,61656697,61657185,61660862,61660890,61662572,61666361,	61628176,61655989,61656809,61657304,61660889,61661073,61662690,61671642,	P61962	uc002jbc.4");
		this.builderForward
				.setSequence("gttccgagagggcggggcggcgtcccaaaacggaaggtggttgtcgtccgttcccaagctggtttgaaactaggggtcgggctcggccgtcgtcgttgtttgtcgccgcatccccgcttccgggttaggccgttcctgcccgccccctcctctcctcccttcggacccatagatctcaggctcggctccccgcccgccgcagcccactgttgacccggcccgtactgcggccccgtggccaccatgtccctgcacggcaaacggaaggagatctacaagtatgaagcgccctggacagtctacgcgatgaactggagtgtgcggcccgataagcgctttcgcttggcgctgggcagcttcgtggaggagtacaacaacaaggttcagcttgttggtttagatgaggagagttcagagtttatttgcagaaacacctttgaccacccataccccaccacaaagctcatgtggatccctgacacaaaaggcgtctatccagacctactggcaacaagcggtgactatctccgtgtgtggagggttggtgaaacagagaccaggctggagtgtttgctaaacaataataagaactctgatttctgtgctcccctgacctcctttgactggaatgaggtggatccttatcttttaggtacctcaagcattgatacgacatgcaccatctgggggctggagacagggcaggtgttagggcgagtgaatctcgtgtctggccacgtgaagacccagctgatcgcccatgacaaagaggtctatgatattgcatttagccgggccgggggtggcagggacatgtttgcctctgtgggtgctgatggctcggtgcggatgtttgacctccgccatctagaacacagcaccatcatttacgaagacccacagcatcacccactgcttcgcctctgctggaacaagcaggaccctaactacctggccaccatggccatggatggaatggaggtggtgattctagatgtccgggttccctgcacacctgtcgccaggttaaacaaccatcgagcatgtgtcaatggcattgcttgggccccacattcatcctgccacatctgcactgcagcggatgaccaccaggctctcatctgggacatccagcaaatgccccgagccattgaggaccctatcctggcctacacagctgaaggagagatcaacaatgtgcagtgggcatcaactcagcccgactggatcgccatctgctacaacaactgcctggagatactcagagtgtagtgttggtggcgctgtgcccacgaggcaggggcttttgtatttcctgcctctgccccacccccaaagtaagaagaaacatgtttccagtggccagtatgtctttcattgctttgcacccactgttaccagaagctgctctaggagttcctggccagtcaccccatcgccctctgtggcagactcagtgctgtgtggcgcctcctcagcccagggctgagttttaagattttctctcctttcctcttctcctttggttcctcaattaaaaaatgtgtgtatatttgtttgtcaggcgttgtgttgaggagcagttcacgcactggctgtgtctattcctctgcccaggtgtctctgtttgctgcccaaggcagcagttcatgtctcgtccatgtccatgttcgtgttagcacttacgtgggaacaaataccaatttgtcttttctcctagtatcagtgtgtttaacaaattttaactttgtatatttgttatctatcaggctaatttttttatgaaaagaattttactctcctgcttcatttctttgtcttatagtcctccctctttgcaccttcttctcttccctcagtgcctggagctggtactgggcccctggccccatgagcagtttgccttcttgagtcactgcctgtgtagtacatacctgaccgggagtccaaaccaccttggtgctctgaagtccactgactcatcacacctttcttagcctggctcctctcaagggcattctgggcttgtaaacagacataggaagcctctgtttaccctgaagcaccactgtccagcccattggttcccactggcagcatggtagagctgagagaaacaggctctcagggtacctgacttgaggggaatcgtttcatgaagctgaacttcaagcatatttccagtacattctttcagagtctgtttttccatccaaatataagccccaggccattccacttagtgtcttttcaatgataggcaagaatgatatctgagttgaacttcggtgcttctgttgtttgagtttactgtgcctggtggtatattgggcattctttggattgagtgttctgaggtgagagagtcttcccgaggcatcctgtctgtgcttccaaccctgaacaagaccttacatgagagatggactgatggactgcggcaatcctgggctgtcaagtggatagatagttaaaaagcattatactgtgggtaatgaaaagggaggaaaaaaaaagaaggaaaaggaattatagacccccagggtcagccagttaagagctctacccacacctgtcaacccctctctcccccagtttaggttctgagcagtattggacttgtagcctgcagttgtcttttgacttgcaggccgcaggtgtctttctgttatgtgaatgagttccatggaggggcatatgtgtgattccaccgttagatgagcccttggggcaggcagtttgggatgtgctcttgggggaaagttggctgtttccttgcgctctgctcctacccgaaggtttttaagtccctctgaattgctcatctgagattagtagagtagcaggcctgaaggatgatggttttgtcctctttggttctcacctgcttgagaagtaaaacagtaactttgttcttctgggcccttaagcttttttggttaagtcttccttttcagaagtagatgtcattatatgccaaaagtctagctctttgctttaccatacagggacctgtcccaaagaaaaaggctctttttttagccagcatatttccccttctacccttttactttgttgttctgattttaggactctggctggccatgtgcttgtggttgcctctcctgcatttgccactggatttgcactgcatcgtttggagatacaaagcgagcagttcttggtcagaaccctcctctgcttttcattgtgtttgataatggttactgggtccttctctcaagggtagcaaggccaagctgatggctgcttgtttaggaggccatcagttccttcctgtggagaagggtctgaaatggaagtcagtggtagaaggggctggtctgctgggcagggcttacatccactgagttctaagattcctttcctgatctgcacctacgcctggtctgtatggtggaatttgtcagctggaactcagaaacaacaacttgaaaaaaaaataataattagaacatatttgcataagatagctatttactctggaaaccaacaacttttgagatttcccttgccctgtggacgcccagctcctgtcatccttccttaggtcctgcagtacagtcttcccctgaatgccaccggggacccagggggactccacccccctaagcaagcacacacatactcacagttgatgagttgctggtctttgagtcccagctctcttaccctccctttactccaccagcccgacgacccatgactgaggaggggatttctacagtctcaggatttagaaagtctgtaagccatccatgctccagaaagcaccgatctgttgtagttgcaaaaacaactctgtaatttgttgaggttctcaaactgacagccagcgagactgggtgggaggccctggatctgttctccctgactgcgggaggagcagccactaggactttagcaggaagcccacatggaggctccgccaggctgtggcccagctggtgatggcccttttgctcctggcagcctgaggcacagctgcctgtattgtcctcatctgttctgactgaaggatggaggtgctgaataaattaggcctcaggcctctaccaccagagagctggagaatgggtccacgtcattcaaggacctgaattttttatgctcaggagcattggaatcctcttcttccagggaggaattagcctgcaaggttaggacttgaagagggaaggtatttaataactgggcgaggatgggtgtggtggctcacacctgtaatcccagcattttgggaggctgaggtggccagatcccaaggtcagaagatcgagaccatcctggctaacatggtgaaaccccatctctactaaaaatacaaaaaaaaattagccgggggtggtggcgggtacctgtagtcctagctacttgggaggctgaggcaggagaatggcgtgaacctgggaggtggagcttgcagtgagccaagatcgtgccactgcactccagcctgggcgacagagcaagactccgtctcaaaaaataaaaaaaaaaaaaaaataggtgaaaattccttataaatccaggattggctctgagagaactggctaagattcaggaagaaacaaaaaattcagaatcctacaaggttttgatgacaattagggccaaaattttaggaggagatgtaggatgcaggagaaaattaaagtgttttctttatatcagaggaggaaatagtagaggtcagtgaaggtctggggtagggaaacattcagactgtccattgcatggctgtggagtgagactgcccttagcctgggtcagccttcctgggccataaattgggcatccgtgatgctaggtaactgtgggaacaaaatgacagcttagagcagccatgggtgatgtttggtggtaaaaaacctacaggcgtttggggtcccatgattgttccagaccatgactcttcctggttgtgggtttgttacagagcaggagaagcagaggttatgacagttatgcagactttccccctcctttttctcttttctcttccccttgcttttccactgtttcttcctgctgccacctgggccttgaattcctgggctgtgaagacatgtagcagctgcagggtttaccacacgtgggagggcagcccagtactgtccctctgccttccccactttgagaatatggcagcccctttcattcctggcttggggtaggggagaccattgaagtagaagcctcaaagcagacttttccctttactgtgtgtactccaggacgaagaaggaagatcatgcttgatacttagattggttttcccagggaagagggcggagcagagcaaagtcactgtgaaccctgggccaggccctggctgggccagctcctgagagcgtctcgtgttgcagacccttgcccacttcacccacctgcaccttctccccctctcacagtgtcactgctgctaatggtcaaagtcaaatgtgtggccacatgggatgggccaggtcctctcaggctactttctggatgtcatttttaaaatatggaaacatgcaggtgccttcccaaagaggcttggactggtatatccaacgagaaacaaataagctaaagaaagtttaaactcaagaagaaagatgttgacagtctatgtaacagctggaaagtttataggcacccacctttgggacaacccagtgattatgaacatgtgatatctactatttaaaagaaatgttctcaccttgggttgattgtggtataccatgtgttatgaaaattgttgagctgaagctttgaatcgatttagttgagtctgactcacttgctttggttcctgtgtattttactacccctcttgtcagtgaccttccttccccaccccacccagagtgaatttgtagcatgattgtataaacctctatgtagaaaatggagatttcttgctctgaaatgttaagctctaactgatccatttctgtgtcctttagcctagtatgtctgaacttccattcttgttatatatttaaactttccctctatattataggttttgtggcatccacggtcaggtgtagaggaagctgccccttgcagaactgtactgtaatatttttcttttataaatattttcacaggactgattgtacacagggcttgtaataaaattttaacactgtgctgtgaaacaactatggggaatctccattgaaggctacttcatgggcacctgaaagtggagtgttatagctatgactttctatttcttgtttcctaagtaaattaaacctaattttcaccctttcattctgtttcagcctcctgtataagaagtaccgtattttctgcccatcatactttgtaataaaacttgaacatgtatagattgactgaaaaaaaaaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("DCAF7");
		this.infoForward = builderForward.build();
		// RefSeq NM_005828.4

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 17, 61660894,
				PositionType.ZERO_BASED), "G", "");
		Annotation annotation1 = new DeletionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(5, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("560del", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Gly187Valfs*23)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(
				ImmutableSortedSet.of(VariantEffect.FRAMESHIFT_VARIANT, VariantEffect.SPLICE_REGION_VARIANT),
				annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc002wcx_3() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory.parseKnownGenesLine(refDict,
				"uc002wcx.3	chr20	+	123251	126392	123269	126333	2	123251,126055,	123327,126392,	Q9BYW3	uc002wcx.3");
		this.builderForward
				.setSequence("agaacccactgcctcctgatgaagtccctactgttcacccttgcagtttttatgctcctggcccaattggtctcaggtaattggtatgtgaaaaagtgtctaaacgacgttggaatttgcaagaagaagtgcaaacctgaagagatgcatgtaaagaatggttgggcaatgtgcggcaaacaaagggactgctgtgttccagctgacagacgtgctaattatcctgttttctgtgtccagacaaagactacaagaatttcaacagtaacagcaacaacagcaacaacaactttgatgatgactactgcttcgatgtcttcgatggctcctacccccgtttctcccactggttgaacattccagcctctgtctcctgctctaggatccccgactcattaaagcaaagaggctta"
						.toUpperCase());
		this.builderForward.setGeneSymbol("DEFB126");
		this.infoForward = builderForward.build();
		// RefSeq NM_030931.3

		// Note that we here have a deviation from Mutalyzer, in that the UCSC sequence does not have a stop codon after
		// the deletion.
		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 20, 126313,
				PositionType.ZERO_BASED), "CC", "");
		Annotation annotation1 = new DeletionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(1, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("317_318del", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Pro106Argfs*?)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FRAMESHIFT_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc002yyz_3() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory.parseKnownGenesLine(refDict,
				"uc002yyz.3	chr21	-	42547157	42557166	42551102	42551555	1	42547157,	42557166,	Q8WY50	uc002yyz.3");
		this.builderForward
				.setSequence("cgtagctcataatccatttttataacaccttgctatctatatttacacctttaaagaacacgggaatttaagagggaagagtaactaggcttttgctaaacttgggctaataaaaccctctgtagagagatccttaatataggcatggggacaacaaggagtatcccaagggactcgccgctagggtgtcttttaagctattggagcaaattcaaatttggcttaaagaaaaagaaactcattttgtattgcaacaccatttgggttaaatacaagttagatgacgaatatatctggcctaaacatggttctatatactatagtgatattttacgattaggcttattttgtaaaagagaaggaaaatgggaagagatcccttatgtacaggcttttatggctctatactggatcacgttacttccaggcattagaatgccatgcataagggatccccacctagctgctccccatagaaagttcataagcctccccagagtctcttcagtcccccagtcctgagtgggggttctcgccaattccctaatgagattccaccccaatatcatcaggcacctttcccccttatccaactagccctagcctataccctctgctgcccaagaaaatgagcccaaccagtacaccaggagtggggctccatatcagcccctaaggtcaagcctgtgtccactgtggaaagtagttgatggaaatgagggaacactcaaagagtacatatgccactttccatgtctaattagaccttataaaaggaaagaattggccagttttcagataaaccagaaaagcttatacaagagtttgttacgttgactatgttcttcaaattgccacgatttacaaatattgtcatccgcttgctgtgctgtggggaaaaaaaagtagaggaaaaagtgtgtggttaagccagtcaattatgacaaggttaaagaagtaactcggggaaaagatgaaaatcccgctctgtttcagggtcttttagttgaagcactcaggaaatatactaatgcaggcccagacaccccagaagggcaagctctcctgggtatacattttctcattcaatcttctcctgacattaggaggaatctacaaaaagcagcaatgggaccttcaagtcctatgaaacgacgcttaaacatagcctttaaagtttacaacaacagggacagggcaaaagaggggagtaaaaagaaatagccaaaaagtacaattgttaacagtgactttaagcctccttgcccctcaggattactcatcttgagaaaatgttacaaaattagcatctgggatgcctagacaagacttgatgcctgacttgctgacccctgggccagaatcactgcgcctactatacgcaaaagggcccctggcaatgcaaatgtcctaactgctctggtgagagagaacaataacaacaaaaagcttccatcaatactagagctaaccttctcctactagccccagtgagctgcttagctcaagtaagtttactgtcccagaggacagctttccacagtggcagataagcagccgcctgaacatttttctttggtatttccaccactgagtgtgctctccagtggcgtggggactccagaatctccttttgagcaatgcagtttgcttcctcccctttttagttgatgctatgggattccctgtcctgccttttcctgttttccatacctatcggggcaaacaaaatttggccaggtagatgggtcccagttctgtaaataacttgaatccagttgtcttgtataggtcattttatttaatatgtttttgggtatatgtacatgtattgtgatgtgtgttacatctagcgtgctgtcaaactggcttatagataaaagaacactcatacattcaacaaataagactactgaaagcttattagtttgaagagaatcttgtatcttctaaaatttaactttaggatttttacctaggtaagtcactgatgttcataggctttaaaatggttaaaatggctttaaatggtgaccagctttgcatggtaccttggttctcggtgatctagataaagttaaaagtgaaataattaaatacacgtaaatgggatatgcttaatgtgtggtttaaaatcataaaatggtagaatggttctcagttatagaatgacaatgtctagtgtgaagttcatgacttcttccttcctaggtttccataaaatgtgctaaagaaatgtattctttattgagaaaaaattttttgtctaatccggaagttactaaatgggaggttcaaaacatgagtgaaccagtgagtagaaaagagagatgtaaagaatattatgaatagaaaatgtattttttgtttgttttgcaaggaaggatataaagaaagagtaattttatatgtggaggaatcctgtatagtaaattccctatcctagagtaaaataactttaagaaagaggtagtatagaacatgtcaggaaattcagctatgttgtagatggtctgtgtaagtcatctgcacagtgcatgagtgtggaggtgggcgggcactcattggcccttgaactccttttgagcagtatggaagccaagaactagaagccaggaaatggggttgtaaaactgatttgtctatggattttatgtgttgagctgctgtggtcttggcttgtagtaattacctatatgaaccttcccccctcccctttagaatttaggacaggttcaaaaggccctccaatataaaaataaaatactgtccttccccacaaaggaaaaaatagctccccggttcaaccaggagacttagtcttgctaaaaccttaaagacagggtaaagacagggataccccaagaatcaattacaatgaaatggaaggggccttatcaggtattgttaagtacccccactgctgttaaacttcagggaacacctacttgggcacacagatccaggactaaacctgtttcttatgagtcacaggcacaaaggaagggcactacaaccacaaccaatatcagtaaagctttggaagacctctgctacctatttaaaataatcaacactcagccagaagaggtaatgtaatgctgtagatgggaataggagcattgatcttgctcttcttcctgactgtagtacttcctttctatggctttaaccagccacctcctcctgggaaacatctcctgtgggcttgttgggtatagaagctactctaagacccaaccagataccatgatgccactgttaattctgtttgctcttctaattaacctaagctagtgtgtatgtggacagggagggtggacaaaattctacagtaaatatttcaaaaattatagcatcatagaatcatctttatggctgccagatttgtcatcaacacccccaggatagacagtttcatcttccgacctatctggaaaatctcaggaccatgtccccagacctcctaactaaccatagcaccccaaaatacccaaacccctattgtgaagtggaactcttccccacttagtggatcccccctggaccctgctgtccccctgccctgaccactattatcggaatctgggaagttgggcatctatatctccagtgcactcataactctaacatttgcatccactcttgcattaatgacacaaaagtggaagcttccctgcgatgctctggtccaactctagttgccaagtttccaagaccacggggaggtaaatgagattccatttgtgagtgaaaagaccatatatggtaccttctcccggatgggaacatacaaaggaaaaacaactgcctgatctgggaaggtgacagtactaccttcttctagaaaacaaagattgttcaaccaccaccatgagaacaggtggaaaatatctctatagacccaacctggcaatgaagtataaacatcgcaccccgcagggcttctcttggtgccctagttgggttcatttttgtttgtgactatgaatgggaagaagtcacaccctgtaaccactccaactccctaaggagtcacctcttctttaaggaatagctttcccttgtatctaaaaaacttggaactgacatgaatgaacgttggccactcttacccctccaggggtcacaatctataacgcctaggacccaagaatatcagaaataagtaagcaataaaactaattctggcaggaatcagggtggcaataggactagcagcaccctggggtggctttgcctaccatgagttaacgctaaagaacttggctcaaatcctagaatccttagccaccaacggagatcaggcattaaagagaattcaagagttccccagactctggaaaatgtagttgttgataacagactagcattggattatttactagctgaacaaggtggggtcttgtgcagttattaataaaacctgctgcacatatattaactctggacaggttgaggttaacattcaaaagatctatgagcaagctacctagttacatagatataaccagggcactgcccccaactatatctggtcaaccatcaaaagtgccttcccaagtctcacctgtttttcacctcttctaggacctttgacaactgtcttgttacaaatgtttggtccttgcttctttaacctcttagtaaagtttgtgtattctagattaccacagttccagagacaatgctggcacaaggcttccagcccatcctgtccactgacacggagaatgaaatcgtcctgcctctgggctccttagatcaggtatccagagatttttactcctccagtgccaggcagggcctacgtccataaactcagcaggaagtagttacggaaaacagatctccgcccttctgcagcccccttaagattaaggaggagtatctaatctctgaagggggaatgaggtaggaggtgggactcaactctggaagtggggctcaggcactcagaccaaactgagcactagctaaaataggtccagggcagatgctagtttccataggacacaccgacctgtgtcaagtcagttcaccatggctctggcagcacccagaagttaccaccctcaccctggaaatgtctgcataaactgccccttcatttgcatataattaaaagtggatacaaataccactgcagaactgcctctgagctgctactgtgggcgcacagcctgtagggcagccctgctttgcaaggagcagcgcctctgctgctgctgtgcacagccggccgcttcaataaaagttgctaacaccactggcttgcccttgagttccttcctgggcaaagctaagaaccctcccgggctatgcttcaatcttagggctcgcctgtcctgcatcactgggatcatctcccagtaaactagccacacttacatccatgtgtcagggacatttctggagaaagcagcccaggacactgttgaataaaacacacaatagtctctgtggtcttctccaccccaccccacaccaggcaccctcagcttgattctcctttttaattgcctgtaagcagggaagcacaatgttttcacattctttgtaaggcctttgttctactaaaatctaacctcagagcacaattttaaactagatgaaagagttgctgcgcctgaagcactgcaaacacctcctcaccacacatgtgcactcaccctggacaccctcactcaccctgacaccctcactcctcaccctggacaccctcactcaccccagacaccgtcactcctcaccctggacacctcactctgcaccctggacaccctcactcaccctggacacgttcactcaccctgacaccctcactcaccctggacaccctcactcaccctggataccctcactcctcaccctggacaccctcactcaccctggataccctcactcctcaccctggacactctcactcaccctgacaccctcaatcctcaccctggactccctcactcctcaccctggactccctcactcctcaccctggacaccctcactcctcatcctggacaccctcactcaacctggacaccctcactcctcaccctgacaccctcactcctcaccctggacaccctcactcctcaccctgacaccctcactcctcaccctggcaccctcagtcaccctgacaccctcactcctcaccctgacaccctcaagtcttcacctccctggctgcagcctgggacacgctttccctaacttctgaaggctcagtcctcctcaagccaatctcatctcaaattgcacctcctcagagaggtcttccataaccgcccttataaagcaggattctttcaccaataccccttcccacatggcactgtctcacagcactcctctaaaagtctgtttacttccttgacaatctgtcttccttataaggggaggttctgtaaaagccaagactctctctgtctagttgactgttgcataccagggcttagaccaaggccctgacatgcagtaggtgcttaatatgttttgaggcaaggtcttgctctgttgcacatgctggagtgcagtggcacaatcgtaattcattgcagccttgaactcctgagctcaagtgatcctcctgcctcagcctcctgagtagctgggactacaggcatgcaccaccaagcttggctaatttaaaaaaaaaattatatagatagggacttgctatgttgcctaggctgatcttgaactcctaacctcaagcaatcctcccacctcggccttccaaagtgctgggataataggcatggagccgccacacccagccaatgtgccgaagaaagaaagaaaaacatgctcatcctttgagtcaggttcaaattttttctcctctttaacccccagtcactccagttataagtgatttttaactcttctcacactttaatgcatctggcaagaagatccacgtggtgttaggaacaatacaggaccttaaggatgggggaatcagcaggtgtcagcgtgccctgtatgctcagggcagctgtttccactggacattctccctttgcctctctgggcagcaactcctaggccagccgacctgctgtgtcgagtaaccaggatttctcaatcttggcatggttgccattttggaccagatcgttctttgttgtgggggctgccctgtacggcaaagaatgccgagcagcacttccagtctccacccacaggacgccagtagcaccctctaagttgtgagaactcaaaatgtccccagaggatgccagatgtcccctggggtggggacacaatcaccccaggttgagatccatggagccaggtctgtttgccaccaaggggtaaagctccattcccaccttaggagggctaggaggcagcatcgtggggccacagaaggcctgggtttgcagtcagaggacaggatgcacattccttcaagatacagacccagattgttgggcatctagttcttgggttttctgttgttgctgttccgttttgtctgtcttccctcctttgtttactagcagcctggaatttgccactttttctaaacgaagatttatggaacacttaccacacggctgacgctgcgcgaggctaaggttctaatacaccgcagctcacttaactctcgcaataccataaacgcacactgtttcatcttgaccctttcttgggaaggtgacagagaggtaggagggcaaacatcttgtgtgccccgtcccaagggtattactggtggaataatatccgccccccaccccagtttctaatttgctgtaggctgtgacgctgtggggcaagactaggagtcctgttgaaattaggaataagtgtgctgtgagggaagggctgccttattttagagcacagattttctgaatatctattttgacaggttcgatcctctccccttcctgccttccttctgtcgattttcaatgtcttgatggtgtcccacctgagtggcctttagagatgtgagttgtgaggcactggggaggcaggcacacgtcctccagcccaagactgcctaatttaacagggatttctgcattctggaacaagcctccattttccccaagcaggattactccagagggcaaaacacagcccaatagtatcacatttcctttctgctttagcaaaaataaccactgtctcattcatgggaaaaggccgccaaacaaatttgttactggaaccatttgtaacaacttctagtttgcactgccttggagcaagcacactttgtagaggagggatttgcagttacttgggcaacaaggtaaccactgatcattacaggaagcttcagaaaccgtgggaccagtgtagaagaatggactatctgtccaaactaagaataaaaagaatgacacttgtattttgtatgtctttttcactttgcctttctagtaattcatttttcttgatatttacaccttgtggccctgtgatagactggaaatctcaaaaacacacgttcagcaccaagattttcagcagcaccgcctcagaatgagacccctagaaaaaactgcgtgttttccacttgcccaacacgaggagtttttggaacacgacctgcttgaggtggagattttctagatgggcaaagagaaggaaacacttaacctaggaagagtatttaggaagaagaaagaacacagcctttctgcacaggaaaccgccgagcagaggggcatctggcctctgcagtggcctccaaatagagtccaatggctggggccagcgtggctgcttaaaggggactcaagggatataataaaatgcagattctcaggtcctagtgcagacaggctcacccaataagtctggactgcatatgggaatctctatttctaggcccttctgcaaggtattcctgctctttccaggaaccatcggcagctggtttggggaaagaagcaacgactccaagtgtgacctgtgagctggcagcagccaccctcagctctgctctcggtcactgaatccgattctgcattttaacaggaccccaggtgttgcacccacacaaagctgaagcagattggtctgggggcaaaaaattagagctatggagattctctcaaatgaaatagatgatatcattgactgttagagcttctagaaggaatctgaggtcacttgttcaaattccctgatttacagatgaggaaacagaggctcagacagctcaaatgacttctctccaatacccaacattcgacaagtagcagctctgggactagtacccaaagcacctagctctccaatcactgcgcaagccacacaattctgtctgcttgtcagtggcttttctgattcaaaaaaagcttaggaatttccccaggaggcagcacgatgtagtgggaagggctctggatgtctctccaaggcttctggaattcatgcccacctccaccaagaagccactttcctgccagctacaggtgctcacctgaaaagcaagccagaccatattaaccctggcattgctggtacctggaagactttctgattcaatgctttccacctcctcctacccctcaccacccccgtggcatgaaatcctgggggctgctttagaaattgttttctttggctgctggtgggggtgctgctggtgggggtttgcacagctggcacactgcaccagtctggtgggggtttgcacagctggcacactgcaccagtctcctgcctgctgccaacaaggccatttcccaagcactggctttggagaagttggggctctgaagtgggaacacaaggctgccttttgcaggccaggtgtaaattctccccctgccactttcagcctagcgtgaaacagatggagtgtgcattcccacttccctttatggtaccctggaatgatggagctgcccagggcatcgccacgttactctctagacagtctctttgtcttcctgcaatggcagcgccgaggttgtatatttctaggtgcaggtatatgattgccatataataaaaatctgaaaacatccca"
						.toUpperCase());
		this.builderForward.setGeneSymbol("(AC4)");
		this.infoForward = builderForward.build();
		// RefSeq NM_182832.2

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 21, 42551467,
				PositionType.ZERO_BASED), "GTGTCAGGGTGAGTGAGGG", "");
		Annotation annotation1 = new DeletionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(0, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("72_90del", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Ser25Hisfs*78)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FRAMESHIFT_TRUNCATION), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc002qrd_2() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc002qrd.2	chr19	+	58570606	58581110	58570609	58579829	5	58570606,58572947,58574813,58578108,58579775,	58570678,58573074,58574909,58578935,58581110,	I3L0B3	uc002qrd.2");
		this.builderForward
				.setSequence("gaaatggagttaggctcgcgccggcgcagtgtcggctgccggtgccgcggcctttgtctcgcagtcaggagggagcaagtgacgtttgaggacgtggtagtgggcttcagccaggaggagtgggggcagctgaagcctgcccagaggaccctgtaccgtgatgtaatgctggacaccttcaggcttctggtctctgtgggacattggttaccgaagccgaatgtcatctccctgctggagcaagaggcagagctgtgggcggtggagtctagacttccccaaggcgtgtacccagacttggaaactagacccaaagtcaaactgtcagttctaaagcaaggcatctctgaagaaatatccaacagtgtcatcttggtagaaagattcctgtgggatggtctgtggtactgcaggggtgaggacactgagggccactgggaatggagttgtgagagtctagagagcctggcagtgccggtggccttcacgcctgtgaagacgcctgttctggagcagtggcagaggaatgggtttggggaaaacataagtctgaaccctgatctcccacatcaaccaatgactcctgaaagacaaagcccccacacatggggaacacgtggaaaaagggagaagccagacctaaatgttttacagaaaacctgtgtaaaagagaaaccctacaaatgtcaggaatgcggaaaggcctttagtcacagctcagcacttatcgaacaccaccggacgcacacaggagagagaccttacgaatgtcacgaatgcttaaaaggcttccggaacagctcggcacttaccaaacaccagagaatccatactggggagaaaccctataaatgcactcagtgtgggaggaccttcaaccaaattgccccactgatccagcaccagagaactcacacaggtgagaagccctatgaatgcagcgaatgtgggaaatccttcagttttaggtcctccttcagccagcacgagcgaactcacacaggcgagaagccctacgagtgcagtgagtgtgggaaagccttccggcaaagcatccacctcacccagcatctgcgaatccacactggggagaaaccctatcagtgtggtgagtgtggcaaggcctttacccacagctcctcccttaccaagcaccagagaactcacactggataaacccactccacatgtgctggggacataggaagaccttaagccatagctcatccctttctagatttgacccaatcatacacatgagaaacgtacattcatacacaagccttttcacacagcactcccctcagacaccctcagagagttcacactgatgggaaatgaccatgggaccaccaagctctaggtcatccatctctgcatccaaatagtagggaaacgtggagataatcaacactcaggaccttcagccttgaacgcccattagtgctatgttatagaacctacaaaaaagaaatggaacaaatgtagtggatccagggaacgcttttgtccaaggattcaccgtattccaaaccagagatgttcaaattggtgagaaacccaacaaatgcctttcatatatacgagaaccaaatgaagtcagaatttgccattattgcacatcacatttttgggggggaaagtgcttatgaatggtgcaggttgactctgatattcattcccaaatgacagtatggcagagtgttccagaaatgagagtggcatctttatggaatcactgtggatactgactgtctcagtaaacagctgtcttgtctgtgtgtatattgtttgatcagggtacatggcagccagtcacagattggaattacatatgacaaagtatcagtgtactataaacaggtttttagttatccctgcattatttttgcaattaatctttatatgcaatgagattgaaaagctttgtatgggaagactcaaaatgtaaagctgcttccgtagagtctcactgattctgagatggcttttgctgctctgttctccctctacatttctctgcagaactcgcgttagaaacacagatatttgttttacaaaaagggagatttttcctttgttaaaccatcgtcttataagcaatagcaaattcatgttagaaacttctgttttgcaagattcatcttttttgggatatgttcaagttaatcctctcaaactgctttttcattgttttcatacaatttaacattttgttaaaccctaagtccacaaatagcagtctttctgacaactataacctttaaatggtgacttgctgccctcattagaaattgcattggctggccaggcgcggtgacttatgcctgtaatcccagcactttgggaggccaaggtgggaggatcacctgaggtcgggagatggaggttgcagtgagctgagatcgtaccattgcactccagcctgggcaacaagagtgaaactctgtctcagaaaaaaaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("ZNF135");
		this.infoForward = builderForward.build();
		// RefSeq NM_001164530.1

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 19, 58579807,
				PositionType.ZERO_BASED), "CCAGAG", "");
		Annotation annotation1 = new DeletionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(4, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("1152_1157del", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(His384_Arg386delinsGln)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.DISRUPTIVE_INFRAME_DELETION), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc001ogt_3() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001ogt.3	chr11	-	65784222	65793988	65784360	65793850	12	65784222,65784530,65786297,65787610,65787787,65788034,65788281,65788564,65788966,65789236,65790319,65792634,	65784387,65784645,65786373,65787671,65787860,65788098,65788425,65788656,65789114,65789350,65790532,65793988,	Q8NEC5	uc001ogt.3");
		this.builderForward
				.setSequence("acgagaatagtcaggcctttctggaagcccagtgccacagtctgcattgagcttggctcaggaaagaagggaaatccaggcggggcctgttgggcccaggtcttgagctcttttggctccagagttcccagcacagtcatggatcaaaactcagtgcctgaaaaggctcagaatgaggcagacaccaataacgcagataggttctttcgctctcactcatcacccccacaccacaggccaggccacagcagagctctccaccattacgagttgcaccatcacggcgtgccccaccaacgtggtgaatctcaccaccctccggagttccaagacttccacgaccaagccttgtcctcccatgtccaccaatctcaccaccacagcgaggcacggaatcacggcagagcccatggccccacaggctttggtctggctccctctcaaggcgccgtcccctcccaccgttcctacggtgaggactaccatgatgagctccaacgtgatggcaggaggcatcatgatgggtcccaatacggtgggttccatcagcagagtgactcccattaccatagggggtctcaccatggcagaccccaatatctcggtgagaatttatcccactattcctctggcgtgccccaccacggtgaggcttcccaccatggtgggtcctacctcccccatggacccaatccctacagtgagtccttccaccacagcgaggcttcccaccttagcgggctccaacacgatgagtcccagcatcaccaagtcccccaccgtggctggccccaccatcaccaagtccaccaccatggcaggtcccgtcatcatgaagcccaccagcatggaaagtctcctcatcacggagagaccatttcccctcattcctctgtggggtcctaccagcgtgggatatctgactatcacagcgagtaccaccaaggtgatcaccaccccagtgagtaccaccatggcgaccatccccaccacacacagcaccactaccaccagacccaccggcaccgagactaccatcagcaccaagaccaccacggcgcgtatcattccagttacctccatggcgactacgtccagagcacttcccaactctctatcccacacacatcccggagcctgattcacgatgcccccggccctgctgcttctcgtacaggagtcttcccctatcacgtagcacacccacggggctcggctcacagcatgactcggtcctccagcacaatccgctcacgtgtcacccagatgtccaaaaaagtccatacccaggatatctccaccaaacattcagaagactggggcaaagaagaagggcaatttcagaaacgcaaaaccggccggctccagcggacccgcaagaagggacactctaccaatctcttccagtggctgtgggaaaagctaaccttcctcattcagggcttccgggaaatgatccggaacctgacccaatccttggcctttgaaactttcatcttcttcgttgtctgcctcaacaccgtcatgctggtggcccagaccttcgctgaagtcgagatccggggcgagtggtacttcatggccttggactccatattcttctgcatctacgtggtggaagccctgctcaagatcatcgccctgggcctctcgtacttctttgacttctggaacaatttggacttcttcattatggccatggccgtgctggacttcttgctgatgcagacccactccttcgccatctaccaccaaagcctcttccggatcctcaaggtcttcaagagcctgcgggccctgagggcaatccgggtcctgcggaggctcagcttcctgaccagcgtccaggaagtgacagggaccctgggccagtccttgccgtccatcgcagccatcctcatcctcatgtttacctgcctcttcctcttctccgcggtcctccgggcactgttccgcaaatctgaccccaagcgcttccagaacatcttcaccaccatcttcaccctcttcaccttgctcacgctggatgactggtccctcatctacatggacagccgtgcccagggcgcctggtacatcattcccatcctcgtaatttacatcatcatccagtacttcatcttcctcaacctggtgattactgtcctggtggatagcttccagacggcgctgttcaaaggccttgagaaagcgaagcaggagagggccgcccggatccaagagaagctgctggaagactcactgacggagctcagagctgcagagcccaaagaggtggcgagtgaaggcaccatgctgaagcggctcatcgagaaaaagtttgggaccatgactgagaagcagcaggagctcctgttccattacctgcagctggtggcaagcgtggagcaggagcagcagaagttccgctcccaggcagccgtcatcgatgagattgtggacaccacatttgaggctggagaagaggacttcaggaattgaccccaggaggacaccagatacagacttcagcccctggcagtctgcccacctgggtgcactgggacgggtccccagatctgctggaatgattgtccgggccctgcagagcaggggccccaacagagtttttaaaccccaaaaaaaaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("CATSPER1");
		this.infoForward = builderForward.build();
		// RefSeq NM_053054.3

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 11, 65793877,
				PositionType.ZERO_BASED), "A", "");
		Annotation annotation1 = new DeletionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(0, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("-25del", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FIVE_PRIME_UTR_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc001rtm_3() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001rtm.3	chr12	-	49521566	49525304	49521740	49525083	4	49521566,49523024,49523282,49525080,	49522721,49523173,49523505,49525304,	P68363	uc001rtm.3");
		this.builderForward
				.setSequence("ggcggccaggccgggcgcggagtgggcgcgcggggccggaggaggggccagcgaccgcggcaccgcctgtgcccgcccgcccctccgcagccgctacttaagaggctccagcgccggccccgccctagtgcgttacttacctcgactcttagcttgtcggggacggtaaccgggacccggtgtctgctcctgtcgccttcgcctcctaatccctagccactatgcgtgagtgcatctccatccacgttggccaggctggtgtccagattggcaatgcctgctgggagctctactgcctggaacacggcatccagcccgatggccagatgccaagtgacaagaccattgggggaggagatgactccttcaacaccttcttcagtgagacgggcgctggcaagcacgtgccccgggctgtgtttgtagacttggaacccacagtcattgatgaagttcgcactggcacctaccgccagctcttccaccctgagcagctcatcacaggcaaggaagatgctgccaataactatgcccgagggcactacaccattggcaaggagatcattgaccttgtgttggaccgaattcgcaagctggctgaccagtgcaccggtcttcagggcttcttggttttccacagctttggtgggggaactggttctgggttcacctccctgctcatggaacgtctctcagttgattatggcaagaagtccaagctggagttctccatttacccagcaccccaggtttccacagctgtagttgagccctacaactccatcctcaccacccacaccaccctggagcactctgattgtgccttcatggtagacaatgaggccatctatgacatctgtcgtagaaacctcgatatcgagcgcccaacctacactaaccttaaccgccttattagccagattgtgtcctccatcactgcttccctgagatttgatggagccctgaatgttgacctgacagaattccagaccaacctggtgccctacccccgcatccacttccctctggccacatatgcccctgtcatctctgctgagaaagcctaccatgaacagctttctgtagcagagatcaccaatgcttgctttgagccagccaaccagatggtgaaatgtgaccctcgccatggtaaatacatggcttgctgcctgttgtaccgtggtgacgtggttcccaaagatgtcaatgctgccattgccaccatcaaaaccaagcgcagcatccagtttgtggattggtgccccactggcttcaaggttggcatcaactaccagcctcccactgtggtgcctggtggagacctggccaaggtacagagagctgtgtgcatgctgagcaacaccacagccattgctgaggcctgggctcgcctggaccacaagtttgacctgatgtatgccaagcgtgcctttgttcactggtacgtgggtgaggggatggaggaaggcgagttttcagaggcccgtgaagatatggctgcccttgagaaggattatgaggaggttggtgtggattctgttgaaggagagggtgaggaagaaggagaggaatactaattatccattccttttggccctgcagcatgtcatgctcccagaatttcagcttcagcttaactgacagacgttaaagctttctggttagattgttttcacttggtgatcatgtcttttccatgtgtacctgtaatatttttccatcatatctcaaagtaaagtcattaacatcaaaaaaaaaaaaaaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("TUBA1B");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 12, 49525088,
				PositionType.ZERO_BASED), "CT", "");
		Annotation annotation1 = new DeletionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(0, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("-7_-6del", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FIVE_PRIME_UTR_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc004crz_3() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc004crz.3	chrX	+	7810302	7812184	7811244	7812057	3	7810302,7811098,7811538,	7810375,7811346,7812184,	Q9H320	uc004crz.3");
		this.builderForward
				.setSequence("atgtggacactgggagaaggaagtgagatggaaaagttctttgattgcatccaagaagtgaatggtcctcaagggcgaccattggctgggtagtggagtgttgaccaatcacagctcaggggcgtgattgtctcgtcctgggatcgcgagaggggtatatacagggaggccaggcagcctggagttagccgaccgttgcgagacgttgagctgcggaagatgagtccaaagccgagagcctcgggacctccggccaaggccacggaggcaggaaagaggaagtcctcctctcagccgagccccagtgacccgaagaagaagactaccaaggtggccaagaagggaaaagcagttcgtagagggagacgcgggaagaaaggggctgcgacaaagatggcggccgtgacggcacctgaggcggagagcgcgccagcggcacccggccccagcgaccagcccagccaggagctccctcagcacgagctgccgccggaggagccagtgagcgaggggacccagcacgaccccctgagtcaggaggccgagctggaggaaccactgagtcaggagagcgaggtggaagaaccactgagtcaggagagccaggtggaggaaccactgagtcaggagagcgaggtggaagaaccactgagtcaggagagccaggtggaggaaccactgagtcaggagagcgaggtggaggaaccactgagtcaggagagccaggtggaggaaccactgagtcaggagagcgagatggaagaaccactgagtcaggagagccaggtggaggaaccaccgagtcaggagagcgagatggaagaactaccgagtgtgtagacggccaagtactcccctatctccgagagcagcgactaagttcaggcccagccgccagacctcagagatctcaccagcggggtgcttgccattctgaagataataaaatgaatgtgttgcaaattgaaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("VCX");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, refDict.getContigNameToID()
				.get("X"), 7811233, PositionType.ZERO_BASED), "AGCTGCG", "");
		Annotation annotation1 = new DeletionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(1, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("-11_-5del", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FIVE_PRIME_UTR_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc001cjx_3() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001cjx.3	chr1	+	44115796	44171189	44118846	44170041	22	44115796,44118807,44121261,44125968,44128564,44131366,44132122,44132624,44133442,44134770,44137175,44149354,44154584,44156515,44157166,44157926,44159664,44160379,44163513,44169287,44169690,44169900,	44115923,44118984,44121437,44126083,44128758,44131416,44132226,44132762,44133690,44134970,44137546,44149475,44154766,44156720,44157243,44157982,44159773,44160565,44163684,44169407,44169783,44171189,	O75164	uc001cjx.3");
		this.builderForward
				.setSequence("acggctgcgcagatgccgactttagaggaggcggagtttcggccttcgcctgctggaaaagcagtaggatcggccagtggcgacagcaggagctgagcctaagccctggcggggctttgggctgtagattcctgtctgactaaagggacctcaaaaaggagggaaaatggcttctgagtctgaaactctgaatcccagtgctaggataatgaccttttatccaactatggaagagttccgaaacttcagtagatacattgcctacattgaatcccaaggagctcatcgggcagggctagccaaggttgttcctccaaaagagtggaagccacgagcatcctatgatgacattgatgatttggtcattcctgcccccattcaacagctggtgacggggcagtctggcctctttactcagtacaacatacagaagaaagccatgactgttcgagagttccgcaagatagccaatagcgataagtactgtaccccacgctatagtgagtttgaagagctcgagcggaaatactggaaaaatcttacattcaatcctccaatctatggtgcagatgtgaatggtaccctctatgaaaagcatgttgatgagtggaatattggccggctgagaacaatcctggacttggtggaaaaggagagtgggatcaccattgagggtgtgaacaccccatacctgtactttggcatgtggaagacatcctttgcttggcacactgaagacatggacctctacagcatcaactacctgcactttggagaaccaaagtcctggtactctgttccacctgagcatggaaagcggttggaacgcctcgccaaaggctttttcccaggaagtgctcaaagctgtgaggcatttctccgccacaagatgaccctgatttccccgttaatgctgaagaaatatggaattccctttgacaaggtgactcaagaggctggagagtttatgatcactttcccttatggttaccatgccggctttaaccatggttttaactgtgcggagtctaccaattttgctacccgtcggtggattgagtacggcaagcaagctgtgctgtgctcctgtagaaaggacatggtgaagatctccatggatgtgtttgtgagaaagttccagccagaaaggtacaaactttggaaagctgggaaggacaacacagttattgaccatactctgcccacgccagaagcagctgagtttcttaaggagagtgaactgcctccaagagctggcaacgaggaggagtgcccagaggaggacatggaaggggtggaggatggagaggaaggagacctgaagacaagcctggccaagcaccgaatagggacaaagaggcaccgagtttgtcttgaaataccacaggaggtgagtcagagtgagctcttccccaaggaggatctgagttctgagcagtatgagatgacggagtgcccggcagccctcgcccctgtgaggcccacccatagctctgtgcggcaagttgaggatggtcttaccttcccagattattctgactccactgaagtcaaatttgaagagcttaaaaatgtcaaactagaagaggaggatgaggaggaagaacaagcagcagctgccttggatctttctgtgaatcctgcgtctgtagggggacgccttgtcttctcaggctccaaaaagaaatcatcttctagcctgggctctggctcttcacgggattctatctcttctgattcagaaactagtgagcctctctcctgccgagcccaagggcaaacgggagttctcactgtgcacagttatgccaaaggggatggcagggtcactgtgggagagccatgcacgaggaagaaaggaagcgccgctagaagtttcagtgagcgggagctggcagaggttgcagatgaatacatgttttccctagaagagaataagaagtccaagggacgccgtcagcctttaagcaagctcccccgccatcacccacttgtgctgcaggagtgtgtcagtgatgatgagacatctgaacagctgacccctgaggaagaggctgaggagacagaggcctgggccaagcctctgagccaactgtggcagaaccgacctccaaactttgaggctgagaaggaattcaatgagaccatggcccaacaggcccctcactgcgctgtctgtatgatcttccagacttatcatcaggttgaatttggaggctttaatcagaactgtggaaatgcttcagatttagccccccagaagcagaggaccaagccattgattccagaaatgtgcttcacttcgactggctgcagcacggacatcaacctttctactccttatcttgaggaggatggcaccagcatactcgtttcctgcaagaagtgcagcgtccgggtccatgccagttgctatggggtcccccctgcaaaggcttctgaagactggatgtgttctcggtgttcagccaatgccctagaggaggactgctgtttatgctcattacgaggaggggccctgcagagagcaaatgatgacaggtgggtccacgtttcatgtgctgtggcaattctggaagcaaggtttgtcaacattgcagaaagaagtccggtggatgtgagcaaaatccccctgccccgcttcaaactgaaatgtatcttctgtaagaagcggaggaaaagaactgctggctgctgtgtgcagtgttctcacggccgctgcccaactgccttccatgtgagctgcgcccaggctgccggtgtgatgatgcagcctgacgactggccttttgtggtcttcattacctgctttcggcacaagattcctaatttggagcgtgccaagggggccttgcaaagcatcactgcaggccagaaagtcattagcaagcataagaacgggcgcttctaccagtgtgaagtggtcaggctcaccaccgagaccttctatgaagtcaactttgatgatggctccttcagcgacaatctttatcctgaggacatagtgagccaggactgtctccagtttggtcctcctgctgaaggggaagtggtccaagtgagatggacagacggccaagtctatggagccaagtttgtggcctcccaccctatccaaatgtaccaggtggagtttgaggatggctcacaacttgtggttaagagagatgatgtatacacactggatgaagagcttcccaagagagtcaaatctagactgtcagtagcctcagacatgcgcttcaatgagattttcacagagaaagaggttaagcaagaaaagaaacggcaacgagttatcaactcaagataccgggaagattatattgagcctgcactataccgggccatcatggagtaggtgcttccagggtccaagggattctcagccatccaggcaagagcactctgggttccacagcacagcagacatggaacgctgaagtctctgaaagtgaagttgtaaaaagaaaaggaatgaaataaccgacccatcatcttctcacccaccctcattgcattccgctgtagtgaaaggacgagccatttctgggcacgtggcagcagtcgctgatctcccagctgaggggctgagcactggaatgctgtggctgcactggccccagtccatagaggggtcaactatgctggctggactggctgccttgttcctggcctaggacttagcttcataactatcacctgcaccgactaggctgaggtgctggtacttgccccaacccctacttttgtatttatatgtgtgtgtgtgtgtgcgtgcgtgcgtgcgtgcgtgtatgtttggtctggaccagcttctgccagcccctggcctttactttcttccttgcctatgcagggcaaacaaaatgtgaaattctgccctcagctgagctgagtaagggctcctgggggttggctggagatgggtgtggcatctgtccaggcctggaaccgtctcaagacagtgctggcaaagctgcagtattgagatgctaaggagctgatgccacctctttgtcttcccctaaaggagaacatggggataacatgggtgtgtgcccacaacactctaggtgcagagcccctgtggcaaagtattacagggtgtgggtggggattaccctgaatcggggattttaatgatggaagcaggcagagcctggtgggtgattctgtcaacagaaaattgcaatcatgcaggggctgggagggttaggatgaaaaaactggggccattggaggcccactgtaggtgggagggagctgattttggggtggggggtgggactagagggcaatactgaaggggttaaacaggtttttgctcctcaagaatttgtttgcctgggcccaggattggagggcttcacaccaataccctgtgtatacaagaatcagatttataatacttccccttttttgttacgtatgaacactataaaccaaattattttgaaaactggtgcatcaccttgtccttagcaataaaatgtgttgagcagaggaaaaaaaaaaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("KDM4A");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 44125966,
				PositionType.ZERO_BASED), "A", "");
		Annotation annotation1 = new DeletionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(2, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("315-2del", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("?", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.CODING_TRANSCRIPT_INTRON_VARIANT,
				VariantEffect.SPLICE_ACCEPTOR_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc001bak_1() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001bak.1	chr1	+	17698740	17728195	17698740	17727934	17	17698740,17699550,17701921,17706414,17707541,17708461,17714875,17715271,17718608,17718673,17720470,17720795,17721446,17722035,17723566,17725181,17727700,	17698856,17699728,17701994,17706482,17707659,17708587,17715054,17715375,17718672,17718721,17720578,17720950,17721603,17722159,17723637,17725343,17728195,	Q6TGC4	uc001bak.1");
		this.builderForward
				.setSequence("atggtcagcgtggagggccgagccatgtccttccagagtatcatccacctgtccctggacagccctgtccatgccgtttgtgtgttgggcacagaaatctgcttggatctcagcgggtgtgccccccagaagtgccagtgcttcaccatccatggctctgggagggtcttgatcgatgtggccaacacggtgatttctgagaaggaggacgccaccatctggtggcccctgtctgatcccacgtacgccacagtgaagatgacatcgcccagcccttccgtggatgcggataaggtctcggtcacatactatgggcccaacgaggatgcccccgtgggcacagctgtgctgtacctcactggcattgaggtctctctagaggtagacatctaccgcaatgggcaagttgagatgtcaagtgacaaacaggctaagaaaaaatggatctggggtcccagcggttggggtgccatcctgcttgtgaattgcaaccctgctgatgtgggccagcaacttgaggacaagaaaaccaagaaagtgatcttttcagaggaaataacgaatctgtcccagatgactctgaatgtccaaggccccagctgtatcttaaagaaatatcggctagtcctccatacctccaaggaagagtcgaagaaggcgagagtctactggccccaaaaagacaactccagtacctttgagttggtgctggggcccgaccagcacgcctataccttggccctcctcgggaaccacttgaaggagactttctacgttgaagctatagcattcccatctgccgaattctcaggcctcatctcctactctgtgtccctggtggaggagtctcaagacccgtcaattccagagactgtgctgtacaaagacacggtggtgttccgggtggctccctgtgtcttcattccctgtacccaggtgcctctggaggtttacctgtgcagggagctgcagctgcagggttttgtggacacagtgacgaagctgagtgagaagagcaacagccaggtggcatctgtctatgaggaccccaaccgcctgggcaggtggctccaggatgagatggccttctgctacacccaggctccccacaagacaacgtccttgatcctcgacacacctcaggccgccgatctcgatgagttccccatgaagtactcactgagccctggtattggctacatgatccaggacactgaggaccataaagtggccagcatggattccattgggaacctgatggtgtccccacctgtcaaggtccaagggaaagagtacccgctgggcagagtcctcattggcagcagcttttaccccagcgcagagggccgggccatgagtaagaccctccgagacttcctctatgcccagcaggtccaagcgccggtggagctctactcagattggctaatgactggccacgtggatgagttcatgtgcttcatccccacagatgacaagaatgagggcaaaaagggcttcctgctgctcctggccagccccagtgcctgctataaactgttccgagagaaacagaaggaaggctatggcgacgctcttctgtttgatgagcttagagcagatcagctcctgtctaatggaagggaagccaaaaccatcgaccaacttctggctgatgaaagcctgaagaagcagaatgaatacgtggagaagtgcattcacctgaaccgtgacatcctgaagacggagctgggcctggtggaacaggacatcatcgagattccccagctgttctgcttggagaagctgactaacatcccctctgaccagcagcccaagaggtcctttgcgaggccatacttccctgacctgttgcggatgattgtgatgggcaagaacctggggatccccaagccttttgggccccaaatcaaggggacctgctgcctggaagaaaagatttgctgcttgctggagcccctgggcttcaagtgcaccttcatcaatgactttgactgttacctgacagaggtcggagacatctgtgcctgtgccaacatccgccgggtgccctttgccttcaaatggtggaagatggtaccttagacccaggccctggagctgccagctctgccccagcgtggatggcccactgtcaccatgcaacagcatgattctttgcccagtagaggaggctggagagtccaggcaacagaaccctttcttccctgtctgccccgaccgaccctcggacccagtaggatggcaaatgccgccagcttgaacccctatggggaaaagatgcaaaagtgttcagccaagtgacgtttactaaatagccaataaagggctggtgggtgtgaatgc"
						.toUpperCase());
		this.builderForward.setGeneSymbol("(DI6)");
		this.infoForward = builderForward.build();
		// RefSeq NM_207421.3

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 17718673,
				PositionType.ZERO_BASED), "G", "");
		Annotation annotation1 = new DeletionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(9, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("1027del", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Val343Trpfs*33)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(
				ImmutableSortedSet.of(VariantEffect.FRAMESHIFT_TRUNCATION, VariantEffect.SPLICE_DONOR_VARIANT),
				annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc031rom_1() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc031rom.1	chr2	+	89890561	90471176	89890561	89890561	44	89890561,89890987,89901314,89901493,89923537,89923728,89934326,89934358,89934414,89953070,89976189,89998818,89999247,90007868,90008036,90010969,90023979,90025410,90049859,90060710,90060779,90060878,90077776,90078012,90080241,90092093,90098216,90107446,90121849,90139091,90139284,90153738,90153962,90154260,90193139,90198904,90211733,90211957,90235239,90249118,90259963,90273947,90458614,90471165,	89890616,89891306,89901369,89901795,89923604,89924026,89934356,89934395,89934447,89953360,89976491,89998873,89999560,90008036,90008155,90010976,90023988,90025426,90049876,90060735,90060799,90060920,90077825,90078316,90080253,90092118,90098224,90107458,90122138,90139159,90139581,90153793,90154260,90154264,90193441,90199195,90211788,90212255,90235250,90249396,90260250,90274233,90458671,90471176,		uc031rom.1");
		this.builderForward
				.setSequence("ctcaccatgaggctccctgctcagctcctggggctgctaatgctctgggtccctggatccagtgaggatattgtgatgacccagactccactctccctgcccgtcacccctggagagccggcctccatctcctgcaggtctagtcagagcctcttggatagtgatgatggaaacacctatttggactggtacctgcagaagccagggcagtctccacagctcctgatctatacgctttcctatcgggcctctggagtcccagacaggttcagtggcagtgggtcaggcactgatttcacactgaaaatcagcagggtggaggctgaggatgttggagtttattactgcatgcaacgtatagagtttccttccacatggacatgagggtccccgctcagctcctggggctcctgctactctggctccgaggtgccagatgtgacatccagatgacccagtctccatcctccctgtctgcatctgtaggagacagagtcaccatcacttgccgggcaagtcagagcattagcagctatttaaattggtatcagcagaaaccagggaaagcccctaagctcctgatctatgctgcatccagtttgcaaagtggggtcccatcaaggttcagtggcagtggatctgggacagatttcactctcaccatcagcagtctgcaacctgaagattttgcaacttactactgtcaacagagttacagtacccctcccacatcaggacacagcatggacatgagggtccccgctcagctcctggggctcctactgctctgggtcccaggtgccagatgtgacatccagttgacccagtctccatcctccctgtctgcatctgtaggagacagagtcaccatcacttgccgggtgagtcagggcattagcagttatttaaattggtatcggcagaaaccagggaaagttcctaagctcctgatctatagtgcatccaatttgcaatctggagtcccatctcggttcagtggcagtggatctgggacagatttcactctcactatcagcagcctgcagcctgaagatgttgcaacttattacggtcaacggacttacaatgcccctccccagtctccatccacactgtctgcatctgtggagacagcgtcaccatcacttgccgggcgagtcagactggtatcagcagaaaccagggaaggcccctaagacatccagatgacccagtctccatcctccctgtctgcatctgtaggagacagagtcaccatcacttgccaggcgagtcaggacattagcaactatttaaattggtatcagcagaaaccagggaaagcccctaagctcctgatctacgatgcatccaatttggaaacaggggtcccatcaaggttcagtggaagtggatctgggacagattttactttcaccatcagcagcctgcagcctgaagatattgcaacatattactgtcaacagtatgataatctccctcccacgatgttgtgatgactcagtctccactctccctgcccgtcacccttggacagccggcctccatctcctgcaggtctagtcaaagcctcgtatacagtgatggaaacacctacttgaattggtttcagcagaggccaggccaatctccaaggcgcctaatttataaggtttctaactgggactctggggtcccagacagattcagcggcagtgggtcaggcactgatttcacactgaaaatcagcagggtggaggctgaggatgttggggtttattactgcatgcaaggtacacactggcctccctcacaatgaggctccctgctcagctcctggggctgctaatgctctgggtctctggatccagtggggatattgtgatgactcagtctccactctccctgcccgtcacccctggagagccggcctccatctcctgcaggtctagtcagagcctcctgcatagtaatggatacaactatttggattggtacctgcagaagccagggcagtctccacagctcctgatctatttgggttctaatcgggcctccggggtccctgacaggttcagtggcagtggatcaggcacagattttacactgaaaatcagcagagtggaggctgaggatgttggggtttattactgcatgcaagctctacaaactcctccgacatccagatgacccagtctccatcctccctgtctgcatctgtaggagacagagtcaccatcacttgccgggcgagtcagggcattagcaattatttagcctggtatcagcagaaaccagggaaagttcctaagctcctgatctatgctgcatccgctttgcaatcagggggtcccatctcggttcagtggcagtggatctgggacagatttcactctcaccatcagcagcctgcagcctgaagatgttgcaacttattactgtcaaaagtataacagtgcccctccttacagaacattgctttcagtggcagcgggtctaagctcctgatctatgaaattgtgctgactcagtctccagcgggccagtcagagcattggggggtcccctcgaggttcagtggcagtggatctgggacagatatggaaaccccagcgcagcttctcttcctcctgctactctggctcccagataccaccggagaaattgtgttgacgcagtctccagccaccctgtctttgtctccaggggaaagagccaccctctcctgcggggccagtcagagtgttagcagcagctacttagcctggtaccagcagaaacctggcctggcgcccaggctcctcatctatgatgcatccagcagggccactggcatcccagacaggttcagtggcagtgggtctgggacagacttcactctcaccatcagcagactggagcctgaagattttgcagtgtattactgtcagcagtatggtagctcacctcccactatattagataaccagacaggtttagtggcagtgggtatcagtgatactttttaaatacatccagatgacccagtctccatctgccatgtctgcatctgtaggagacagagtcaccatcacttgtcgggcgaggcagggcattagcaattatttagcctggtttcagcagaaaccagggaaagtccctaagcacctgatctatgctgcatccagtttgcaaagtggggtcccatcaaggttcagcggcagtggatctgggacagaattcactctcacaatcagcagcctgcagcctgaagattttgcaacttattactgtctacagcataatagttaccctcccacgtcaggacacagcatggacatgagggtcctcgctcagctcctggggctcctgctgctctgtttcccaggtgccagatgtgacatccagatgacccagtctccatcctcactgtctgcatctgtaggagacagagtcaccatcacttgtcgggcgagtcagggtattagcagctggttagcctggtatcagcagaaaccagagaaagcccctaagtccctgatctatgctgcatccagtttgcaaagtggggtcccatcaaggttcagcggcagtggatctgggacagatttcactctcaccatcagcagcctgcagcctgaagattttgcaacttattactgccaacagtataatagttaccctcggaaccatggaagccccagcgcagcttctcttcctcctgctactctggctcccagataccactggagaaatagtgatgacgcagtctccagccaccctgtctgtgtctccaggggaaagagccaccctctcctgcagggccagtcagagtgttagcagcaacttagcctggtaccagcagaaacctggccaggctcccaggctcctcatctatggtgcatccaccagggccactggcatcccagccaggttcagtggcagtgggtctgggacagagttcactctcaccatcagcagcctgcagtctgaagattttgcagtttattactgtcagcagtataataactggcctcccacagccatccagttgacccagtctccatcctccctgtctgcatctgtaggagacagagtcaccatcacttgccgggcaagtcagggcattagcagtgctttagcctggtatcagcagaaaccagggaaagctcctaagctcctgatctatgatgcctccagtttggaaagtggggtcccatcaaggttcagcggcagtggatctgggacagatttcactctcaccatcagcagcctgcagcctgaagattttgcaacttattactgtcaacagtttaataattaccctcacatagtgttacaaaccgacatccagatgacccagtctccatcttctgtgtctgcatctgtaggagacagagtcaccatcacttgtcgggcgagtcagggtattagcagctggttagcctggtatcagcagaaaccagggaaagcccctaagctcctgatctatgctgcatccagtttgcaaagtggggtcccatcaaggttcagcggcagtggatctgggacagatttcactctcactatcagcagcctgcagcctgaagattttgcaacttactattgtcaacaggctaacagtttccctcccacggaaccatggaagccccagcgcagcttctcttcctcctgctactctggctcccagataccaccggagaaattgtgttgacacagtctccagccaccctgtctttgtctccaggggaaagagccaccctctcctgcagggccagtcagggtgttagcagctacttagcctggtaccagcagaaacctggccaggctcccaggctcctcatctatgatgcatccaacagggccactggcatcccagccaggttcagtggcagtgggcctgggacagacttcactctcaccatcagcagcctagagcctgaagattttgcagtttattactgtcagcagcgtagcaactggcatcctttagaaagtagatgacccagtctccattctccctgtctgcatctgtaggagacagagtcaccatcacttgctgggccagtcagggcattagcagttatttagcctggtatcagcaaaaaccagcaaaagcccctaagctcttcatctattatgcatccagtttgcaaagtggggtcccatcaaggttcagcggcagtggatctgggacggattacactctcaccatcagcagcctgcagcctgaagattttgcaacttattactgtcaacagtattatagtacccctcgtcatctggatgacccagtctccatccttactctctgcatctacaggagacagagtcaccatcagttgtcggatgagtcagggcattagcagttatttagcctggtatcagcaaaaaccagggaaagcccctgagctcctgatctatgctgcatccactttgcaaagtggggtcccatcaaggttcagtggcagtggatctgggacagatttcactctcaccatcagttgcctgcagtctgaagattttgcaacttattactgtcaacagtattatagtttccctccgaaattgtaatgacacagtctccagccaccctgtctttgtctccaggggaaagagccaccctctcctgcagggccagtcagagtgttagcagcagctacttatcctggtaccagcagaaacctgggcaggctcccaggctcctcatctatggtgcatccaccagggccactggcatcccagccaggttcagtggcagtgggtctgggacagacttcactctcaccatcagcagcctgcagcctgaagattttgcagtttattactgtcagcaggattataacttaccctgcagcctgaagattttgcaacttattactgtcaacagagtgacagtacccctccacttttggcag"
						.toUpperCase());
		this.builderForward.setGeneSymbol("abParts");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 2, 90458647,
				PositionType.ZERO_BASED), "T", "");
		Annotation annotation1 = new DeletionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(42, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("5842del", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals(null, annotation1.getProteinChange());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.NON_CODING_TRANSCRIPT_EXON_VARIANT),
				annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc003nxo_1() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory.parseKnownGenesLine(refDict,
				"uc003nxo.1	chr6	+	31803039	31803103	31803039	31803039	1	31803039,	31803103,		uc003nxo.1");
		this.builderForward.setSequence("agtgatgatgaccccaggtaactcttgagtgtgtcgctgatgccatcaccgcagcgctctgacc"
				.toUpperCase());
		this.builderForward.setGeneSymbol("SNORD48");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 6, 31803064,
				PositionType.ZERO_BASED), "T", "");
		Annotation annotation1 = new DeletionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(0, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("26del", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals(null, annotation1.getProteinChange());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.NON_CODING_TRANSCRIPT_EXON_VARIANT),
				annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc004fus_3() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc004fus.3	chrY	-	23745485	23756552	23745485	23745485	6	23745485,23746668,23749386,23751157,23751851,23756471,	23745548,23746760,23749548,23751256,23752013,23756552,		uc004fus.3");
		this.builderForward
				.setSequence("ttcatgatccacagaaaaataaaaaaacatggagccccagagcccaagcagagccaaacagacaagcaaccaaaataaaagttgaagttgtgggatctcatccaattcccaccagattgtatcctcacccctgtctgaccttattgttgctcacactctatgtcccaaaatgaaaacccaagacgatggagtattgcccccttatgatgtgaaccaactgcttggctgggacctgaatttgagtttattcctagggctctgtttgatgttacttctggctggctcatgtctgccctctcctgggatcacgggactatcccatggatccaacagagaagacaggtgaagttgctggaacccattctccattcagcagattgtatcctcaccccatgtgaccttattgctgctcagactctatgttccaggataaaatcccaagaagatggagaagtgcatccctcatgatgtgaagcatgtgcgcagctgggaaccaaattcgagagctcacagtttgaatagaagaatccaaagctacaacagcagcagcagctatagctgtgaccgctatgaggcccgtgatgacagctattaagggagactcaccaccaaggggattaccaccatcgtagctaccattagattactggtggtcagca"
						.toUpperCase());
		this.builderForward.setGeneSymbol("TTTY13");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, refDict.getContigNameToID()
				.get("Y"), 23749506, PositionType.ZERO_BASED), "G", "");
		Annotation annotation1 = new DeletionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(3, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("385del", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals(null, annotation1.getProteinChange());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.NON_CODING_TRANSCRIPT_EXON_VARIANT),
				annotation1.getEffects());
	}

	// This variant was called on the Platinum genomes and caused a problem with string access.
	@Test
	public void testRealWorldCase_uc011mcs_() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc011mcs.1	chr9	-	135782117	135820020	135782118	135798879	13	135782117,135782687,135785957,135786388,135786839,135787668,135796749,135797205,135798734,135800973,135804153,135810419,135819929,	135782222,135782757,135786079,135786500,135786955,135787844,135796823,135797360,135798879,135801126,135804339,135810482,135820020,	Q59IT9	uc011mcs.1");
		this.builderForward
				.setSequence("acgacgggggaggtgctgtacgtccaagatggcggcgccctgtaggctggagggactgtgaggtaaacagctgagggggaggagacggtggtgaccatgaaagacaccaggttgacagcactggaaactgaagtaccagttgtcgctagaacagtttggtagtggccccaatgaagaaccttcagaacctgtagcacacgtcctggagccagcacagcgccttcgagcgagagaatggcccaacaagcaaatgtcggggagcttcttgccatgctggactcccccatgctgggtgtgcgggacgacgtgacagctgtctttaaagagaacctcaattctgcacctcttggacaggattaacgaatatgtgggcaaagccgccactcgtttatccatcctctcgttactgggtcatgtcataagactgcagccatcttggaagcataagctctctcaagcacctcttttgccttctttactaaaatgtctcaagatggacactgacgtcgttgtcctcacaacaggcgtcttggtgttgataaccatgctaccaatgattccacagtctgggaaacagcatcttcttgatttctttgacatttttggccgtctgtcatcatggtgcctgaagaaaccaggccacgtggcggaagtctatctcgtccatctccatgccagtgtgtacgcactctttcatcgcctttatggaatgtacccttgcaacttcgtctcctttttgcgttctcattacagtatgaaagaaaacctggagacttttgaagaagtggtcaagccaatgatggagcatgtgcgaattcatccggaattagtgactggatccaaggaccatgaactggaccctcgaaggtggaagagattagaaactcatgatgttgtgatcgagtgtgccaaaatctctctggatcccacagaagcctcatatgaagatggctattctgtgtctcaccaaatctcagcccgctttcctcatcgttcagccgatgtcaccaccagcccttatgctgacacacagaatagctatgggtgtgctacttctaccccttactccacgtctcggctgatgttgttaaatatgccagggcagctacctcagactctgagttccccatcgacacggctgataactgaaccaccacaagctactctttggagcccatctatggtttgtggtatgaccactcctccaacttctcctggaaatgtcccacctgatctgtcacacccttacagtaaagtctttggtacaactgcaggtggaaaaggaactcctctgggaaccccagcaacctctcctcctccagccccactctgtcattcggatgactacgtgcacatttcactcccccaggccacagtcacaccccccaggaaggaagagagaatggattctgcaagaccatgtctacacagacaacaccatcttctgaatgacagaggatcagaagagccacctggcagcaaaggttctgtcactctaagtgatcttccagggtttttaggtgatctggcctctgaagaagatagtattgaaaaagataaagaagaag"
						.toUpperCase());
		this.builderForward.setGeneSymbol("Q59IT9");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 9, 135782122,
				PositionType.ZERO_BASED), "TTCT", "");
		Annotation annotation1 = new DeletionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(12, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("1068_1071del", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Glu358del)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FRAMESHIFT_TRUNCATION), annotation1.getEffects());
	}

	// This variant was called on the Platinum genomes and caused a problem with string access.
	@Test
	public void testRealWorldCase_uc011dba_3() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(refDict,
						"uc011dba.2	chr5	+	140810157	140812789	140810326	140812789	1	140810157,	140812789,	O60330-2	uc011dba.2");
		this.builderForward
				.setSequence("acgcgcctgaagcacaaagcagatagctaggaatgaaccatccctgggagtatgtggaaacaacggaggagctctgacttcccaactgtcccattctatgggcgaaggaactgctcctgacttcagtggttaagggcagaattgaaaataattctggaggaagataagaatgattcctgcgcgactgcaccgggactacaaagggcttgtcctgctgggaatcctcctggggactctgtgggagaccggatgcacccagatacgctattcagttccggaagagctggagaaaggctctagggtgggcgacatctccagggacctggggctggagccccgggagctcgcggagcgcggagtccgcatcatccccagaggtaggacgcagcttttcgccctgaatccgcgcagcggcagcttggtcacggcgggcaggatagaccgggaggagctctgtatgggggccatcaagtgtcaattaaatctagacattctgatggaggataaagtgaaaatatatggagtagaagtagaagtaagggacattaacgacaatgcgccttactttcgtgaaagtgaattagaaataaaaattagtgaaaatgcagccactgagatgcggttccctctaccccacgcctgggatccggatatcgggaagaactctctgcagagctacgagctcagcccgaacactcacttctccctcatcgtgcaaaatggagccgacggtagtaagtaccccgaattggtgctgaaacgcgccctggaccgcgaagaaaaggctgctcaccacctggtccttacggcctccgacgggggcgacccggtgcgcacaggcaccgcgcgcatccgcgtgatggttctggatgcgaacgacaacgcaccagcgtttgctcagcccgagtaccgcgcgagcgttccggagaatctggccttgggcacgcagctgcttgtagtcaacgctaccgaccctgacgaaggagtcaatgcggaagtgaggtattccttccggtatgtggacgacaaggcggcccaagttttcaaactagattgtaattcagggacaatatcaacaataggggagttggaccacgaggagtcaggattctaccagatggaagtgcaagcaatggataatgcaggatattctgcgcgagccaaagtcctgatcactgttctggacgtgaacgacaatgccccagaagtggtcctcacctctctcgccagctcggttcccgaaaactctcccagagggacattaattgcccttttaaatgtaaatgaccaagattctgaggaaaacggacaggtgatctgtttcatccaaggaaatctgccctttaaattagaaaaatcttacggaaattactatagtttagtcacagacatagtcttggatagggaacaggttcctagctacaacatcacagtgaccgccactgaccggggaaccccgcccctatccacggaaactcatatctcgctgaacgtggcagacaccaacgacaacccgccggtcttccctcaggcctcctattccgcttatatcccagagaacaatcccagaggagtttccctcgtctctgtgaccgcccacgaccccgactgtgaagagaacgcccagatcacttattccctggctgagaacaccatccaaggggcaagcctatcgtcctacgtgtccatcaactccgacactggggtactgtatgcgctgagctccttcgactacgagcagttccgagacttgcaagtgaaagtgatggcgcgggacaacgggcacccgcccctcagcagcaacgtgtcgttgagcctgttcgtgctggaccagaacgacaatgcgcccgagatcctgtaccccgccctccccacggacggttccactggcgtggagctggctccccgctccgcagagcccggctacctggtgaccaaggtggtggcggtggacagagactccggccagaacgcctggctgtcctaccgtctgctcaaggccagcgagccgggactcttctcggtgggtctgcacacgggcgaggtgcgcacggcgcgagccctgctggacagagacgcgctcaagcagagcctcgtagtggccgtccaggaccacggccagccccctctctccgccactgtcacgctcaccgtggccgtggccgacagcatcccccaagtcctggcggacctcggcagcctcgagtctccagctaactctgaaacctcagacctcactctgtacctggtggtagcggtggccgcggtctcctgcgtcttcctggccttcgtcatcttgctgctggcgctcaggctgcggcgctggcacaagtcacgcctgctgcaggcttcaggaggcggcttgacaggagcgccggcgtcgcactttgtgggcgtggacggggtgcaggctttcctgcagacctattcccacgaggtttccctcaccacggactcgcggaagagtcacctgatcttcccccagcccaactatgcagacatgctcgtcagccaggagagctttgaaaaaagcgagccccttttgctgtcaggtgattcggtattttctaaagacagtcatgggttaattgaggtgagtttatatcaaatcttctttcttttttttttttaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("TTTY13");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 5, 140812775,
				PositionType.ZERO_BASED), "T", "");
		Annotation annotation1 = new DeletionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(0, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("2461del", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("?", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FRAMESHIFT_TRUNCATION, VariantEffect.STOP_LOST),
				annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc003zdj_3() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc003zdj.3	chr8	-	145736666	145743210	145736813	145743168	22	145736666,145737063,145737293,145737526,145737774,145738024,145738229,145738600,145738769,145738954,145739311,145739572,145739825,145740319,145740533,145740709,145741147,145741371,145742433,145742797,145742985,145743084,	145736938,145737172,145737450,145737707,145737944,145738154,145738521,145738768,145738864,145739096,145739491,145739746,145739909,145740456,145740626,145740841,145741274,145742148,145742574,145742892,145743019,145743210,	O94761	uc003zdj.3");
		this.builderForward
				.setSequence("ctggacgatcgcaagcgcggaggccgggcgggcgcgcgcgccatggagcggctgcgggacgtgcgggagcggctgcaggcgtgggagcgcgcgttccgacggcagcgcgggcggcgaccgagccaggacgacgtggaggcggcgccggaggagacccgcgcgctctaccgggaataccgcactctgaagcgtaccacgggccaggccggcggcgggctccgcagctccgagtcgctccccgcggcggccgaagaggcgccagagccccgctgctgggggccccatctgaatcgggctgcgaccaagagtccacagcctacgccagggcggagccgccagggctcggtgccggactacgggcagcggctcaaggccaatctgaaaggcaccctgcaggccggaccagccctgggccgcagaccgtggcctctaggaagagcctcatctaaggcatccaccccaaagcccccaggtacagggcctgtcccctcctttgcagaaaaagtcagtgatgagcctccacagctccctgagccccagccaaggccaggccggctccagcatctgcaggcatccctgagccagcggctgggctccctagatcctggctggttacagcgatgtcacagtgaggtcccagattttctgggggcccccaaagcctgcaggcctgatctaggctcagaggaatcacaacttctgatccctggtgagtcggctgtccttggtcctggtgctggctcccagggcccagaggcttcagccttccaagaagtcagcatccgtgtggggagcccccagcccagcagcagtggaggcgagaagcggagatggaacgaggagccctgggagagccccgcacaggtccagcaggagagcagccaagctggacccccatcggagggggctggggctgtagcagttgaggaagaccctccaggggaacctgtacaggcacagccacctcagccctgcagcagcccatcgaaccccaggtaccacggactcagcccctccagtcaagctagggctgggaaggctgagggcacagcccccctgcacatcttccctcggctggcccgccatgacaggggcaattacgtacggctcaacatgaagcagaaacactacgtgcggggccgggcactccgtagcaggctcctccgcaagcaggcatggaagcagaagtggcggaagaaaggggagtgttttgggggtggtggtgccacagtcacaaccaaggagtcttgtttcctgaacgagcagttcgatcactgggcagcccagtgtccccggccagcaagtgaggaagacacagatgctgttgggcctgagccactggttccttcaccacaacctgtacctgaggtgcccagcctggaccccaccgtgctgccactctactccctggggccctcagggcagttggcagagacgccggctgaggtgttccaggccctggagcagctggggcaccaagcctttcgccctgggcaggagcgtgcagtcatgcggatcctgtctggcatctccacgctgctggtgctgcctacaggtgccggcaagtccctgtgctaccagctcccagcgctgctctacagccggcgcagcccctgcctcacgttggtcgtctctcccctgctgtcactcatggatgaccaggtgtctggcctgccaccgtgtctcaaggcggcctgcatacactcgggcatgaccaggaagcaacgggaatctgtcctgcagaagattcgggcagcccaggtacacgtgctgatgctgacacctgaggcactggtgggggcgggaggcctccctccagccgcacagctgcctccagttgcttttgcctgcattgatgaggcccactgcctctcccagtggtcccacaacttccggccctgctacctgcgcgtctgcaaggtgcttcgggagcgcatgggcgtgcactgcttcctgggcctcacagccacagccacacgccgcactgccagtgacgtggcacagcacctggctgtggctgaagagcctgacctccacgggccagccccagttcccaccaacctgcacctttccgtgtccatggacagggacacagaccaggcactgttgacgctgctgcaaggcaaacgttttcaaaacctcgattccattatcatttactgcaaccggcgcgaggacacagagcggatcgctgcgctcctccgaacctgcctgcacgcagcctgggtcccagggtctggaggtcgtgcccccaaaaccacagccgaggcctaccacgcgggcatgtgcagccgggaacggcggcgggtacagcgagccttcatgcagggccagttgcgggtggtggtggccacggtggcctttgggatggggctggaccggccagatgtgcgggctgtgctgcatctggggctgcccccaagcttcgagagctacgtgcaggccgtgggccgggccgggcgtgacgggcagcctgcccactgccacctcttcctgcagccccagggcgaagacctgcgagagctgcgcagacatgtgcacgccgacagcacggacttcctggctgtgaagaggctggtacagcgcgtgttcccagcctgcacctgcacctgcaccaggccgccctcggagcaggaaggggccgtgggtggggagaggcctgtgcccaagtacccccctcaagaggctgagcagcttagccaccaagcagccccaggacccagaagggtctgcatgggccatgagcgggcactcccaatacagcttaccgtacaggctttggacatgccggaggaggccatcgagactttgctgtgctacctggagctgcacccacaccactggctggagctgctggcgaccacctatacccattgccgtctgaactgccctgggggccctgcccagctccaggccctggcccacaggtgtccccctttggctgtgtgcttggcccagcagctgcctgaggacccagggcaaggcagcagctccgtggagtttgacatggtcaagctggtggactccatgggctgggagctggcctctgtgcggcgggctctctgccagctgcagtgggaccacgagcccaggacaggtgtgcggcgtgggacaggggtgcttgtggagttcagtgagctggccttccaccttcgcagcccgggggacctgaccgctgaggagaaggaccagatatgtgacttcctctatggccgtgtgcaggcccgggagcgccaggccctggcccgtctgcgcagaaccttccaggcctttcacagcgtagccttccccagctgcgggccctgcctggagcagcaggatgaggagcgcagcaccaggctcaaggacctgctcggccgctactttgaggaagaggaagggcaggagccgggaggcatggaggacgcacagggccccgagccagggcaggccagactccaggattgggaggaccaggtccgctgcgacatccgccagttcctgtccctgaggccagaggagaagttctccagcagggctgtggcccgcatcttccacggcatcggaagcccctgctacccggcccaggtgtacgggcaggaccgacgcttctggagaaaatacctgcacctgagcttccatgccctggtgggcctggccacggaagagctcctgcaggtggcccgctgactgcactgcattgggggatgtcgggtagagctggggttgtcagaggctagggcagtgactgaggacctgggcaaaacctgccacagggtgtgggaacgaggaggctccaaaatgcagaataaaaaatgctcactttgtttttatgggaaaaaaaaaaaaaaaaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("RECQL4");
		this.infoForward = builderForward.build();
		// RefSeq NM_004260

		// The prediction differs from the one by Mutalyzer since the UCSC and RefSeq transcripts for RECQL4 differ.

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 8, 145738767,
				PositionType.ZERO_BASED), "G", "");
		Annotation annotation1 = new DeletionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(14, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("2296del", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Arg766Glyfs*77)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(
				ImmutableSortedSet.of(VariantEffect.FRAMESHIFT_TRUNCATION, VariantEffect.SPLICE_DONOR_VARIANT),
				annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc001sbo_3() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001sbo.1	chr12	+	53443834	53452266	53443969	53452264	15	53443834,53445638,53446238,53447194,53447557,53447748,53448053,53448969,53449351,53449564,53450796,53451350,53451556,53451810,53452099,	53444044,53445747,53446276,53447233,53447596,53447798,53448225,53449020,53449474,53449629,53450880,53451463,53451617,53451886,53452266,	F8W661	uc001sbo.1");
		this.builderForward
				.setSequence("tcctcacaccagccagacagcctaccctccgcccaggggaagcggctgcctccgccaggccgcttccaggaagccccgggccaggccccagcattgttcaggccctggggccagcaccccagccagccgaacaccatgaagtccagcggccctgtggagaggctgctcagagccctggggaggagggacagcagccgggccgcaagcaggcctaggaaagctgagcctcatagcttccgggagaaggttttccggaagaaacctccagtctgtgcagtatgtaaggtgaccatcgatgggacaggcgtttcgtgcagagtctgcaaggtggcgacgcacagaaaatgtgaagcaaaggtgacttcagcctgtcaggccttgcctcccgtggagttgcggcgaaacacggccccagtcaggcgcatagagcacctgggatccaccaaatctctgaaccactcaaagcagcgcagcactctgcccaggagcttcagcctggacccgctcatggagcggcgctgggacttagacctcacctacgtgacggagcgcatcttggccgccgccttccccgcgcggcccgatgaacagcggcaccggggccacctgcgcgagctggcccatgtgctgcaatccaagcaccgggacaagtacctgctcttcaacctttcagagaaaaggcatgacctgacccgcttaaaccccaaggttcaagacttcggctggcctgagctgcatgctccacccctggacaagctgtgctccatctgcaaagccatggagacatggctcagtgctgacccacagcacgtggtcgtactatactgcaagggaaacaagggcaagcttggggtcatcgtttctgcctacatgcactacagcaagatctctgcaggggcggaccaggcactggccactcttaccatgcggaaattctgcgaggacaaggtggccacagaactgcagccctcccagcgtcgatatatcagctacttcagtgggctgctatctggctccatcagaatgaacagcagccctctcttcctgcactatgtgctcatccccatgctgccagcctttgaacctggcacaggcttccagcccttccttaaaatctaccagtccatgcagcttgtctacacatctggagtctatcacattgcaggccctggtccccagcagctttgcatcagcctggagccagccctcctcctcaaaggcgatgtcatggtaacatgttatcacaagggtggccggggcacagaccggaccctcgtgttccgagtccagttccacacctgcaccatccacggaccacagctcactttccccaaggaccagcttgacgaggcctggactggtgagtctgagacaagtggcctggggctggagtccag"
						.toUpperCase());
		this.builderForward.setGeneSymbol("RECQL4");
		this.infoForward = builderForward.build();

		// TODO(holtgrew): The end position in the prediction could be more exact since it is in the intron.

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 12, 53452263,
				PositionType.ZERO_BASED), "CAGGTGGCAGG", "");
		Annotation annotation1 = new DeletionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(-1, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("1260_*10del", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(*420Serext*?)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FRAMESHIFT_VARIANT, VariantEffect.STOP_LOST),
				annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc010eqp_3() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc010eqp.3	chr19	+	53935226	53961515	53950521	53960002	7	53935226,53948253,53949479,53950448,53952764,53952888,53957903,	53935281,53948472,53949590,53950536,53952887,53952892,53961515,	NP_001008401	uc010eqp.3");
		this.builderForward
				.setSequence("agattcgtgcagaccaggaagcggatagcgtggagtgacggtgccaccgcggcgcatggagtttcgctcttgttgctcaggctggagtgcaatggtgtgatctcggctcactgcaacctccgcctcccgggttcaagcgattctcctgcctcagcctctcgagtagctgagattacaggcatgatccattgctcccagccacccatgtattcctgattgaaaaaatttgcttatgtcttagttctacagctgaccctctttcgctattttcaagagtattgcccgggctgtgagtgcagtctcatgatcttgggtcactgcaacctctgtttcctgggttcaagtgattcttgtgcctcagcatcccacgtagctgagattacaggattgacttctaaagactcttggtacgtgaggaagaaacccggaagaggaagaggagagcaaaggagtcagggatggctttttctcagggtctattgacattcagggatgtggccatagaattctctcaggaggagtggaaatgcctggaccctgctcagaggactctatacagggacgtgatgctggagaattataggaacctggtctccctggatatctcttccaaatgcacgatgaaggagttcttgtcaacagcgcaaggcaacagagaagtgttccatgcagggacattgcaaatacatgaaagtcatcacaatggagatttttgctaccaggatgttgataaagatattcatgactatgaatttcaatggcaagaagatgaaagaaatggccatgaagcacccatgacaaaaatcaaaaagttgacaggtattacagaacgatatgatcaaagtcatgctagaaacaagcctattaaagatcagcttggatcaagctttcattcgcatctgcctgaaatgcacatatttcagaccgaagagaaaattgataatcaagttgtgaagtctgtccacgatgcttccttggtttcaacagcccaaagaatttcttgtaggcccaaaacccatatatctaataaccatgggaataatttctggaattcttcattactcacacaaaaacaggaagtacacatgagagaaaaatctttccaatgtaatgagagtggcaaagcctttaattacagctcactcttaaggaaacatcagataatccatttagcagacaaatataaatgtgatgtatgtggcaagctctttaatcagaagcgaaacctagcatgccatcgtagatgtcacactggtgagaatccttacaagtgtaatgagtgtggcaagaccttcagtcagacgtcatcccttacatgccatcgtagacttcatactggagagaaaccttacaaatgtgaagaatgtgacaaagctttccatttcaaatcaatacttgaaagacataggataattcatactgaagagaaaccatataagtgtaatgagtgtggcaagacctttaggcagaagtcaatccttacacgccatcatcgacttcatactggagagaaaccttacaagtgtaatgagtgtggcaagacctttagtcacaagtcatcccttacatgccatcatagacttcatactggagagaaaccctacaaatgtaatgagtgtggcaagacctttagtcacaagtcatcccttacatgccatcgtagacttcatactggagagaaaccttacaaatgtgaagaatgtgacaaagcttacagtttcagatcaaattttgaaatacatcggaaaattcatactgaagacaatgcttacaagtgtaatgagtgtggaaagacctttagccggacatcatcccttacatgccatcgtagacgtcatactggagagcaaccttacaaatgtgaagaatgtgacaaagctttccgtttcaaatcaaaccttgaaagacataggagaattcatactggagagaaaccatacaagtgtaatgagtgtggcaagacctttagtcggaagtcatacctcacatgccatcatagacttcatactggagagaaagcttacaagtgtaatgagtgcggcaagacctttagttggaagtcatcccttacctgccatcgtagacttcattctggagagaaaccttacaagtgtaaggagtgtggcaagaccttcaatcagcagttaacccttaaacgccatcgtagacttcatagtggagagaacccttacaaatgtgaagatagtgacaaagcttacagtttcaaatcaaaccttgaaatacatcagaaaattcatactgaagagaatccttacaagtgtaatgagtgtggcaagaccttcagtcggacgtcatcccttacatgccatcgtagacttcataccggagagaaaccttacaaatgtgaagaatgtgacaaagctttccgtgtgaaatcaaaccttgaaggacataggagaattcatactggagagaaaccttacaagtgtaatgagtgtggcaagacctttagtcggaagtcatattttatatgccatcatagacttcatactggagagaaaccttataagtgtaatgagtgtggcaagaactttagtcagaagtcatcccttatatgccaccatagacttcatactggagagaaaccttacaagtgtaatgagtgtggcaagacctttagtcagaagtcaaaccttacatgccatcgtagacttcatactggagaaaaacaagtgtaatgaatgtggtgaggtttttaatcaacaagcacaccttgcaggtcatcatagaattcatactggggagaaaccttagaaatgtgaagcatgtgataaagtttacagtggcaaatcaagcctcagaagacaggagaattcatactggagagaaagcttataaatgtgaagaatgtcacaaagtttacagtcgcacatcaaaccgtgaaagacaggagaattcatactggagagaaaccataaaaatgtaagagtttgtgacaaggcttttgggcatgattcgcacctggcacaacatgctagaattcacactggagagaaaccttaccagtgtaatgagtgtggcaaagcctttagtaggcagtcaacacttgtttaccgtcaggcaatccatggtgtagggaaactttactaaggtaatgattgtcacaaagtcttcagtaatgctacaaccattgtgaatcactggagaatccataaggaagagagatcatactagggtaataaatgtggcagatttttcagacattgttcataccttgcagttcatcggtgaactcaacgctggagagaaaccttacaaatgtcatgactgtggcaaggtcttcagtcaagcttcatcctatgcaaaacataggagaattcatacaggagagaaacctcacatgtgtgatgatagtggcaaagccttcacttcacacctcatgagacatcagagaatgcatactggacagaaatcttacaaatgtcatcaatgtgccaaggtcttcagtctgagttcactccttgcagaatatgagaaaattcattttggaggtagttggtccatatgcaatgagtagagcaaaccatcaagcattaattgacattagggtcaattcagcattgacttgagtttgtattgacttaacattgagttcaagcattaattgacattagtgtttatgttaagaggattgggccaggcacatcagcttacacctgtaatctgagcgctttgggaggccaaggtgggtagatcacttgaggtcaggagtttgagatcagcctggccaacagacgtgagccattttcccagcctgttttttgtttctttaaaaaaactgatagggatttttatggatatcatgttgaatctaaatcacattgggttattatataatcatttcacaatattaatttttccaagctatcaatatgggttgtagctcaatgtttttaatcattttgatcaatgtttgtagatttcaaggtacaaacttctgacctttgtacgtttatttctaagtatttctttaagttctccagcaaatggaagtgttttaaaattttcttttaaaattgtttattgttaaagtatggaaattcaactaatttttggtgctgatactgtattgtgcaaatccactgactatgttacttagttccagtagtattttggttggctctttgtgattttctacacagaagattatgtcatctacaaacaaatataattttacttcttactttctgaaaaaaaaaaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("ZN761_HUMAN");
		this.infoForward = builderForward.build();
		// RefSeq NM_001008401

		// This transcript is different in UCSC hg19 and RefSeq hg19.

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 19, 53952885,
				PositionType.ZERO_BASED), "C", "");
		Annotation annotation1 = new DeletionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(4, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("138+1del", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("?", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.SPLICE_DONOR_VARIANT,
				VariantEffect.CODING_TRANSCRIPT_INTRON_VARIANT), annotation1.getEffects());
	}

}
