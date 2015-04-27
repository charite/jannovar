package de.charite.compbio.jannovar.annotation.builders;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableSortedSet;

import de.charite.compbio.jannovar.annotation.Annotation;
import de.charite.compbio.jannovar.annotation.AnnotationLocation;
import de.charite.compbio.jannovar.annotation.InvalidGenomeChange;
import de.charite.compbio.jannovar.annotation.VariantEffect;
import de.charite.compbio.jannovar.io.ReferenceDictionary;
import de.charite.compbio.jannovar.reference.GenomeChange;
import de.charite.compbio.jannovar.reference.GenomePosition;
import de.charite.compbio.jannovar.reference.HG19RefDictBuilder;
import de.charite.compbio.jannovar.reference.PositionType;
import de.charite.compbio.jannovar.reference.Strand;
import de.charite.compbio.jannovar.reference.TranscriptModel;
import de.charite.compbio.jannovar.reference.TranscriptModelBuilder;
import de.charite.compbio.jannovar.reference.TranscriptModelFactory;

// TODO(holtgrem): Extend tests to also use reverse transcript?

public class BlockSubstitutionAnnotationBuilderTest {

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
	public void testForwardUstream() throws InvalidGenomeChange {
		GenomeChange change1 = new GenomeChange(new GenomePosition(refDict, Strand.FWD, 1, 6640059,
				PositionType.ZERO_BASED), "ACG", "CGTT");
		Annotation annotation1 = new BlockSubstitutionAnnotationBuilder(infoForward, change1,
				new AnnotationBuilderOptions()).build();
		// TODO(holtgrew): Check for distance==0
		Assert.assertEquals(infoForward.accession, annotation1.getTranscript().accession);
		Assert.assertEquals(null, annotation1.getNucleotideHGVSDescription());
		Assert.assertEquals(null, annotation1.getAminoAcidHGVSDescription());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.UPSTREAM_GENE_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testForwardDownstream() throws InvalidGenomeChange {
		GenomeChange change1 = new GenomeChange(new GenomePosition(refDict, Strand.FWD, 1, 6649340,
				PositionType.ZERO_BASED), "ACG", "CGTT");
		Annotation annotation1 = new BlockSubstitutionAnnotationBuilder(infoForward, change1,
				new AnnotationBuilderOptions()).build();
		// TODO(holtgrew): Check for distance==0
		Assert.assertEquals(infoForward.accession, annotation1.getTranscript().accession);
		Assert.assertEquals(null, annotation1.getNucleotideHGVSDescription());
		Assert.assertEquals(null, annotation1.getAminoAcidHGVSDescription());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.DOWNSTREAM_GENE_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testForwardIntergenic() throws InvalidGenomeChange {
		// intergenic upstream
		GenomeChange change1 = new GenomeChange(new GenomePosition(refDict, Strand.FWD, 1, 6639059,
				PositionType.ZERO_BASED), "ACG", "CGTT");
		Annotation annotation1 = new BlockSubstitutionAnnotationBuilder(infoForward, change1,
				new AnnotationBuilderOptions()).build();
		// TODO(holtgrew): Check for distance==1000
		Assert.assertEquals(infoForward.accession, annotation1.getTranscript().accession);
		Assert.assertEquals(null, annotation1.getNucleotideHGVSDescription());
		Assert.assertEquals(null, annotation1.getAminoAcidHGVSDescription());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.INTERGENIC_VARIANT), annotation1.getEffects());
		// intergenic downstream
		GenomeChange change2 = new GenomeChange(new GenomePosition(refDict, Strand.FWD, 1, 6650340,
				PositionType.ZERO_BASED), "ACG", "CGTT");
		Annotation annotation2 = new BlockSubstitutionAnnotationBuilder(infoForward, change2,
				new AnnotationBuilderOptions()).build();
		// TODO(holtgrew): Check for distance==1000
		Assert.assertEquals(infoForward.accession, annotation2.getTranscript().accession);
		Assert.assertEquals(null, annotation2.getNucleotideHGVSDescription());
		Assert.assertEquals(null, annotation2.getAminoAcidHGVSDescription());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.INTERGENIC_VARIANT), annotation2.getEffects());
	}

	@Test
	public void testForwardTranscriptAblation() throws InvalidGenomeChange {
		StringBuilder chars200 = new StringBuilder();
		for (int i = 0; i < 200; ++i)
			chars200.append("A");
		GenomeChange change1 = new GenomeChange(new GenomePosition(refDict, Strand.FWD, 1, 6640061,
				PositionType.ZERO_BASED), chars200.toString(), "CGTT");
		Annotation annotation1 = new BlockSubstitutionAnnotationBuilder(infoForward, change1,
				new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.accession, annotation1.getTranscript().accession);
		Assert.assertEquals("c.-204_-70+65delinsCGTT", annotation1.getNucleotideHGVSDescription());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.TRANSCRIPT_ABLATION), annotation1.getEffects());
	}

	@Test
	public void testForwardIntronic() throws InvalidGenomeChange {
		GenomeChange change1 = new GenomeChange(new GenomePosition(refDict, Strand.FWD, 1, 6642106,
				PositionType.ZERO_BASED), "ACG", "CGTT");
		Annotation annotation1 = new BlockSubstitutionAnnotationBuilder(infoForward, change1,
				new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.accession, annotation1.getTranscript().accession);
		Assert.assertEquals("c.691-11_691-9delinsCGTT", annotation1.getNucleotideHGVSDescription());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.CODING_TRANSCRIPT_INTRON_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testForwardFivePrimeUTR() throws InvalidGenomeChange {
		GenomeChange change1 = new GenomeChange(new GenomePosition(refDict, Strand.FWD, 1, 6640070,
				PositionType.ZERO_BASED), "ACG", "CGTT");
		Annotation annotation1 = new BlockSubstitutionAnnotationBuilder(infoForward, change1,
				new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.accession, annotation1.getTranscript().accession);
		Assert.assertEquals(0, annotation1.getAnnoLoc().rank);
		Assert.assertEquals("c.-195_-193delinsCGTT", annotation1.getNucleotideHGVSDescription());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FIVE_PRIME_UTR_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testForwardThreePrimeUTR() throws InvalidGenomeChange {
		GenomeChange change1 = new GenomeChange(new GenomePosition(refDict, Strand.FWD, 1, 6649329,
				PositionType.ZERO_BASED), "ACG", "CGGTT");
		Annotation annotation1 = new BlockSubstitutionAnnotationBuilder(infoForward, change1,
				new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.accession, annotation1.getTranscript().accession);
		Assert.assertEquals(10, annotation1.getAnnoLoc().rank);
		Assert.assertEquals("c.*58_*60delinsCGGTT", annotation1.getNucleotideHGVSDescription());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.THREE_PRIME_UTR_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testForwardStartLoss() throws InvalidGenomeChange {
		// Testing with some START_LOST scenarios.

		// Delete one base of start codon.
		GenomeChange change1 = new GenomeChange(new GenomePosition(refDict, Strand.FWD, 1, 6640669,
				PositionType.ZERO_BASED), "ACG", "CGTT");
		Annotation annotation1 = new BlockSubstitutionAnnotationBuilder(infoForward, change1,
				new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.accession, annotation1.getTranscript().accession);
		Assert.assertEquals(1, annotation1.getAnnoLoc().rank);
		Assert.assertEquals("c.1_3delinsCGTT", annotation1.getNucleotideHGVSDescription());
		Assert.assertEquals("p.0?", annotation1.getAminoAcidHGVSDescription());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.START_LOST), annotation1.getEffects());

		// Delete chunk out of first exon, spanning start codon from the left.
		GenomeChange change2 = new GenomeChange(new GenomePosition(refDict, Strand.FWD, 1, 6640660,
				PositionType.ZERO_BASED), "CCCTCCAGACC", "GTTG");
		Annotation annotation2 = new BlockSubstitutionAnnotationBuilder(infoForward, change2,
				new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.accession, annotation2.getTranscript().accession);
		Assert.assertEquals(1, annotation2.getAnnoLoc().rank);
		Assert.assertEquals("c.-9_2delinsGTTG", annotation2.getNucleotideHGVSDescription());
		Assert.assertEquals("p.0?", annotation2.getAminoAcidHGVSDescription());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.START_LOST), annotation2.getEffects());

		// Delete chunk out of first exon, spanning start codon from the right.
		GenomeChange change3 = new GenomeChange(new GenomePosition(refDict, Strand.FWD, 1, 6640671,
				PositionType.ZERO_BASED), "GGACGGCTCCT", "CTTG");
		Annotation annotation3 = new BlockSubstitutionAnnotationBuilder(infoForward, change3,
				new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.accession, annotation3.getTranscript().accession);
		Assert.assertEquals(1, annotation3.getAnnoLoc().rank);
		Assert.assertEquals("c.3_13delinsCTTG", annotation3.getNucleotideHGVSDescription());
		Assert.assertEquals("p.0?", annotation3.getAminoAcidHGVSDescription());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.START_LOST), annotation3.getEffects());

		// Deletion from before transcript, reaching into the start codon.
		GenomeChange change4 = new GenomeChange(
				new GenomePosition(refDict, Strand.FWD, 1, 6640399, PositionType.ZERO_BASED),
				"TCTCACCAGGCCCTTCTTCACGACCCTGGCCCCCCATCCAGCATCCCCCCTGGCCAATCCAATATGGCCCCCGGCCCCCGGGAGGCTGTCAGTGTGTTCCAGCCCTCCGCGTGCACCCCTCACCCTGACCCAAGCCCTCGTGCTGATAAATATGATTATTTGAGTAGAGGCCAACTTCCCGTTTCTCTCTCTTGACTCCAGGAGCTTTCTCTTGCATACCCTCGCTTAGGCTGGCCGGGGTGTCACTTCTGCCTCCCTGCCCTCCAGACCA",
				"ACCT");
		Annotation annotation4 = new BlockSubstitutionAnnotationBuilder(infoForward, change4,
				new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.accession, annotation4.getTranscript().accession);
		Assert.assertEquals(AnnotationLocation.INVALID_RANK, annotation4.getAnnoLoc().rank);
		Assert.assertEquals("c.-69-201_1delinsACCT", annotation4.getNucleotideHGVSDescription());
		Assert.assertEquals("p.0?", annotation4.getAminoAcidHGVSDescription());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.START_LOST), annotation4.getEffects());
	}

	@Test
	public void testForwardStopLoss() throws InvalidGenomeChange {
		// Replace bases of stop codon by 4 nucleotides, frameshift case.
		GenomeChange change1 = new GenomeChange(new GenomePosition(refDict, Strand.FWD, 1, 6649271,
				PositionType.ZERO_BASED), "ACG", "CGTT");
		// Note that the transcript here differs to the one Mutalyzer uses after the CDS.
		Annotation annotation1 = new BlockSubstitutionAnnotationBuilder(infoForward, change1,
				new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.accession, annotation1.getTranscript().accession);
		Assert.assertEquals(10, annotation1.getAnnoLoc().rank);
		Assert.assertEquals("c.2067_*2delinsCGTT", annotation1.getNucleotideHGVSDescription());
		Assert.assertEquals("p.*689Tyrext*25", annotation1.getAminoAcidHGVSDescription());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.COMPLEX_SUBSTITUTION, VariantEffect.STOP_LOST),
				annotation1.getEffects());

		// Replace stop codon by 6 nucleotides, non-frameshift case.
		GenomeChange change2 = new GenomeChange(new GenomePosition(refDict, Strand.FWD, 1, 6649270,
				PositionType.ZERO_BASED), "ACT", "CGGTCG");
		Annotation annotation2 = new BlockSubstitutionAnnotationBuilder(infoForward, change2,
				new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.accession, annotation2.getTranscript().accession);
		Assert.assertEquals(10, annotation2.getAnnoLoc().rank);
		Assert.assertEquals("c.2066_*1delinsCGGTCG", annotation2.getNucleotideHGVSDescription());
		Assert.assertEquals("p.*689Serext*17", annotation2.getAminoAcidHGVSDescription());
		// Note that the transcript here differs to the one Mutalyzer uses after the CDS.
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.COMPLEX_SUBSTITUTION, VariantEffect.STOP_LOST),
				annotation2.getEffects());

		// Delete first base of stop codon, leads to complete loss.
		GenomeChange change3 = new GenomeChange(new GenomePosition(refDict, Strand.FWD, 1, 6649269,
				PositionType.ZERO_BASED), "ACG", "CGGT");
		Annotation annotation3 = new BlockSubstitutionAnnotationBuilder(infoForward, change3,
				new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.accession, annotation3.getTranscript().accession);
		Assert.assertEquals(10, annotation3.getAnnoLoc().rank);
		Assert.assertEquals("c.2065_2067delinsCGGT", annotation3.getNucleotideHGVSDescription());
		Assert.assertEquals("p.*689Argext*16", annotation3.getAminoAcidHGVSDescription());
		// Note that the transcript here differs to the one Mutalyzer uses after the CDS.
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.COMPLEX_SUBSTITUTION, VariantEffect.STOP_LOST),
				annotation3.getEffects());
	}

	@Test
	public void testForwardSplicing() throws InvalidGenomeChange {
		// intronic splicing
		GenomeChange change1 = new GenomeChange(new GenomePosition(refDict, Strand.FWD, 1, 6642116,
				PositionType.ZERO_BASED), "G", "TT");
		Annotation annotation1 = new BlockSubstitutionAnnotationBuilder(infoForward, change1,
				new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.accession, annotation1.getTranscript().accession);
		Assert.assertEquals(1, annotation1.getAnnoLoc().rank);
		Assert.assertEquals("c.691-1delinsTT", annotation1.getNucleotideHGVSDescription());
		Assert.assertEquals("p.?", annotation1.getAminoAcidHGVSDescription());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.CODING_TRANSCRIPT_INTRON_VARIANT,
				VariantEffect.SPLICE_ACCEPTOR_VARIANT), annotation1.getEffects());

		// exonic splicing
		GenomeChange change2 = new GenomeChange(new GenomePosition(refDict, Strand.FWD, 1, 6642117,
				PositionType.ZERO_BASED), "TGG", "AA");
		Annotation annotation2 = new BlockSubstitutionAnnotationBuilder(infoForward, change2,
				new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.accession, annotation2.getTranscript().accession);
		Assert.assertEquals(2, annotation2.getAnnoLoc().rank);
		Assert.assertEquals("c.691_693delinsAA", annotation2.getNucleotideHGVSDescription());
		Assert.assertEquals("p.Trp231Lysfs*23", annotation2.getAminoAcidHGVSDescription());
		Assert.assertEquals(
				ImmutableSortedSet.of(VariantEffect.COMPLEX_SUBSTITUTION, VariantEffect.SPLICE_REGION_VARIANT),
				annotation2.getEffects());
	}

	@Test
	public void testForwardFrameShiftBlockSubstitution() throws InvalidGenomeChange {
		// The following case contains a shift in the nucleotide sequence.
		GenomeChange change1 = new GenomeChange(new GenomePosition(refDict, Strand.FWD, 1, 6647537,
				PositionType.ZERO_BASED), "TGCCCCACCT", "CCC");
		Annotation annotation1 = new BlockSubstitutionAnnotationBuilder(infoForward, change1,
				new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.accession, annotation1.getTranscript().accession);
		Assert.assertEquals(6, annotation1.getAnnoLoc().rank);
		Assert.assertEquals("c.1225_1234delinsCCC", annotation1.getNucleotideHGVSDescription());
		Assert.assertEquals("p.Cys409Profs*127", annotation1.getAminoAcidHGVSDescription());
		Assert.assertEquals(
				ImmutableSortedSet.of(VariantEffect.COMPLEX_SUBSTITUTION, VariantEffect.SPLICE_REGION_VARIANT),
				annotation1.getEffects());
	}

	@Test
	public void testForwardNonFrameBlockSubstitution() throws InvalidGenomeChange {
		// deletion of two codons, insertion of one
		GenomeChange change1 = new GenomeChange(new GenomePosition(refDict, Strand.FWD, 1, 6642114,
				PositionType.ZERO_BASED), "TAAACA", "GTT");
		Annotation annotation1 = new BlockSubstitutionAnnotationBuilder(infoForward, change1,
				new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.accession, annotation1.getTranscript().accession);
		Assert.assertEquals(AnnotationLocation.INVALID_RANK, annotation1.getAnnoLoc().rank);
		Assert.assertEquals("c.691-3_693delinsGTT", annotation1.getNucleotideHGVSDescription());
		Assert.assertEquals("p.Trp231Val", annotation1.getAminoAcidHGVSDescription());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.COMPLEX_SUBSTITUTION,
				VariantEffect.SPLICE_ACCEPTOR_VARIANT, VariantEffect.FEATURE_TRUNCATION), annotation1.getEffects());

		// deletion of three codons, insertion of one
		GenomeChange change2 = new GenomeChange(new GenomePosition(refDict, Strand.FWD, 1, 6642126,
				PositionType.ZERO_BASED), "GTGGTTCAA", "ACC");
		Annotation annotation2 = new BlockSubstitutionAnnotationBuilder(infoForward, change2,
				new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.accession, annotation2.getTranscript().accession);
		Assert.assertEquals(2, annotation2.getAnnoLoc().rank);
		Assert.assertEquals("c.700_708delinsACC", annotation2.getNucleotideHGVSDescription());
		Assert.assertEquals("p.Val234_Gln236delinsThr", annotation2.getAminoAcidHGVSDescription());
		Assert.assertEquals(
				ImmutableSortedSet.of(VariantEffect.FEATURE_TRUNCATION, VariantEffect.COMPLEX_SUBSTITUTION),
				annotation2.getEffects());

		// deletion of three codons, insertion of one, includes truncation of replacement ref from the right
		GenomeChange change3 = new GenomeChange(new GenomePosition(refDict, Strand.FWD, 1, 6642134,
				PositionType.ZERO_BASED), "AGTGGAGGAT", "CTT");
		Annotation annotation3 = new BlockSubstitutionAnnotationBuilder(infoForward, change3,
				new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.accession, annotation3.getTranscript().accession);
		Assert.assertEquals(2, annotation3.getAnnoLoc().rank);
		Assert.assertEquals("c.708_716delinsCT", annotation3.getNucleotideHGVSDescription());
		Assert.assertEquals("p.Gln236Hisfs*16", annotation3.getAminoAcidHGVSDescription());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.COMPLEX_SUBSTITUTION), annotation3.getEffects());
	}

	@Test
	public void testRealWorldCase_uc002djq_3() throws InvalidGenomeChange {
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

		GenomeChange change1 = new GenomeChange(new GenomePosition(refDict, Strand.FWD, 15, 74536399,
				PositionType.ZERO_BASED), "TAAGAAGGAGACCATCA", "ACTACCAGAGGAAT");
		Annotation annotation1 = new BlockSubstitutionAnnotationBuilder(infoForward, change1,
				new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.accession, annotation1.getTranscript().accession);
		Assert.assertEquals(1, annotation1.getAnnoLoc().rank);
		Assert.assertEquals("c.96_112delinsACTACCAGAGGAAT", annotation1.getNucleotideHGVSDescription());
		Assert.assertEquals("p.Lys33_Met38delinsLeuProGluGluLeu", annotation1.getAminoAcidHGVSDescription());
		Assert.assertEquals(
				ImmutableSortedSet.of(VariantEffect.COMPLEX_SUBSTITUTION, VariantEffect.FEATURE_TRUNCATION),
				annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc010qzf_2() throws InvalidGenomeChange {
		this.builderForward = TranscriptModelFactory.parseKnownGenesLine(refDict,
				"uc010qzf.2	chr11	+	5474637	5475707	5474718	5475657	1	5474637,	5475707,	Q9H344	uc010qzf.2");
		this.builderForward
				.setSequence("ttgtcctccagcaagtgcaactgttagaattctccaagtcagaagatctgactctgaaaagtaccctaagtttgttttgctatggggttgttcaatgtcactcaccctgcattcttcctcctgactggtatccctggtctggagagctctcactcctggctgtcagggcccctctgcgtgatgtatgctgtggcccttgggggaaatacagtgatcctgcaggctgtgcgagtggagcccagcctccatgagcccatgtactacttcctgtccatgttgtccttcagtgatgtggccatatccatggccacactgcccactgtactccgaaccttctgcctcaatgcccgcaacatcacttttgatgcctgtctaattcagatgtttcttattcacttcttctccatgatggaatcaggtattctgctggccatgagttttgaccgctatgtggccatttgtgaccccttgcgctatgcaactgtgctcaccactgaagtcattgctgcaatgggtttaggtgcagctgctcgaagcttcatcacccttttccctcttccctttcttattaagaggctgcctatctgcagatccaatgttctttctcactcctactgcctgcacccagacatgatgaggcttgcctgtgctgatatcagtatcaacagcatctatggactctttgttcttgtatccacctttggcatggacctgttttttatcttcctctcctatgtgctcattctgcgttctgtcatggccactgcttcccgtgaggaacgcctcaaagctctcaacacatgtgtgtcacatatcctggctgtacttgcattttatgtgccaatgattggggtctccacagtgcaccgctttgggaagcatgtcccatgctacatacatgtcctcatgtcaaatgtgtacctatttgtgcctcctgtgctcaaccctctcatttatagcgccaagacaaaggaaatccgccgagccattttccgcatgtttcaccacatcaaaatatgactttcacacttggctttagaatctgttattttggccataggctctcatca"
						.toUpperCase());
		this.builderForward.setGeneSymbol("LOC100132247");
		this.infoForward = builderForward.build();
		// RefSeq NM_001004754.2

		GenomeChange change1 = new GenomeChange(new GenomePosition(refDict, Strand.FWD, 11, 5475430,
				PositionType.ZERO_BASED), "TCAACA", "ACAACACT");
		Annotation annotation1 = new BlockSubstitutionAnnotationBuilder(infoForward, change1,
				new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.accession, annotation1.getTranscript().accession);
		Assert.assertEquals(0, annotation1.getAnnoLoc().rank);
		Assert.assertEquals("c.713_718delinsACAACACT", annotation1.getNucleotideHGVSDescription());
		Assert.assertEquals("p.Leu238Hisfs*19", annotation1.getAminoAcidHGVSDescription());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.COMPLEX_SUBSTITUTION), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc011ddm_2_first() throws InvalidGenomeChange {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc011ddm.2	chr5	-	156479371	156485970	156479372	156484954	4	156479371,156482211,156484908,156485931,	156479665,156482544,156485087,156485970,	E9PFX0	uc011ddm.2");
		this.builderForward
				.setSequence("gttacccagcattgtgagtgacagagcctggatctgaacagcaggctcatatgaatcaaccaactgggtgaaaagataagttgcaatctgagatttaagacttgatcagataccatctggtggagggtaccaaccagcctgtctgctcattttccttcaggctgatcccataatgcatcctcaagtggtcatcttaagcctcatcctacatctggcagattctgtagctggttctgtaaaggttggtggagaggcaggtccatctgtcacactaccctgccactacagtggagctgtcacatccatgtgctggaatagaggctcatgttctctattcacatgccaaaatggcattgtctggaccaatggaacccacgtcacctatcggaaggacacacgctataagctattgggggacctttcaagaagggatgtctctttgaccatagaaaatacagctgtgtctgacagtggcgtatattgttgccgtgttgagcaccgtgggtggttcaatgacatgaaaatcaccgtatcattggagattgtgccacccaaggtcacgactactccaattgtcacaactgttccaaccgtcacgactgttcgaacgagcaccactgttccaacgacaacgactgttccaatgacgactgttccaacgacaactgttccaacaacaatgagcattccaacgacaacgactgttctgacgacaatgactgtttcaacgacaacgagcgttccaacgacaacgagcattccaacaacaacaagtgttccagtgacaacaactgtctctacctttgttcctccaatgcctttgcccaggcagaaccatgaaccag"
						.toUpperCase());
		this.builderForward.setGeneSymbol("HAVCR1");
		this.infoForward = builderForward.build();
		// RefSeq NM_012206.2

		GenomeChange change1 = new GenomeChange(new GenomePosition(refDict, Strand.FWD, 5, 156479564,
				PositionType.ZERO_BASED), "AGTCGT", "AGTGAG");
		Annotation annotation1 = new BlockSubstitutionAnnotationBuilder(infoForward, change1,
				new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.accession, annotation1.getTranscript().accession);
		Assert.assertEquals(3, annotation1.getAnnoLoc().rank);
		Assert.assertEquals("c.475_477delinsCTC", annotation1.getNucleotideHGVSDescription());
		Assert.assertEquals("p.Thr159Leu", annotation1.getAminoAcidHGVSDescription());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.MNV), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc002axo_4() throws InvalidGenomeChange {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc002axo.4	chr15	+	74528629	74628482	74529060	74628394	19	74528629,74536325,74554780,74559018,74560682,74564043,74565111,74572303,74573008,74574118,74588094,74622529,74623003,74623321,74623543,74625019,74626221,74627315,74628265,	74529081,74536489,74554914,74559128,74560799,74564135,74565232,74572433,74573142,74574190,74588289,74622695,74623092,74623453,74623637,74625186,74626308,74627429,74628482,	Q8N5R6-6	uc002axo.4");
		this.builderForward
				.setSequence("agtgttgcccaggtgacccagctaggtggaagagcttattgttttccaggcctgggcagagacagggcccccctgccccatctctccaccgtcctaggtgtgccaagagtcaattgcctcattgctgaccctgtccagctggccatggccctcaacccccaaggcccttccacccacagaccatctcgctgctgagagatccaggacctgctcccacctggccaccctccccctccccccacatccaggccccagggctggtgtgtggcacccctgagaccacattgacctccatactgtctactacccataaggactccaagacgcccaggccagctgtctgggcaggactgattcctgatcacccactgataccaagtactcatccccaagattgttaaacaaggccagacactcctggcctcaagaggatgggactgaaaaacaaaaagaacactgaagacccagaggagcccctgatcgcctcccagagcacggaacctgagatcggtcacctgtctccctctaagaaggagaccatcatggtcaccctccatggggctaccaacctgcctgcctgcaaggatggctccgagccgtggccctatgtggtggtgaaaagcacatctgaggaaaagaacaatcagagctccaaggcagtcacatctgtgacctcagagcccaccagagcccctatctggggggacacggtgaatgtggagatccaagctgaggatgcagggcaagaagatgtgatcctcaaggtggtggacaacagaaagaaacaggagttgttgtcctacaaaatccccatcaagtacctgcgtgtcttccacccctaccactttgagctggtgaagcccactgagtctgggaaagccgatgaagccactgccaagacccagttgtacgcaacagtcgttcggaagagcagcttcataccccgctacatcggctgcaaccacatggctctggagatctttctccggggagtcaacgagcccctggccaacaaccccaaccccatagtggtgattgcccgggtcgttcccaactacaaggaatttaaggtcagccaggctaacagggacctggcctctgtggggctgcccatcaccccactgtccttccctatcccgtccatgatgaactttgacgtgcctcgcgtcagccagaacggatgccctcagctgtccaagcctgggggacccccagagcagcccctgtggaatcagtccttcctcttccaaggccgagatggagctaccagcttctcagaagacacagccctggtgctggagtactactcctcaacttcaatgaaaggcagccagccgtggaccctcaaccagcccctgggcatctctgtgttgccgctaaagagccgtttgtaccagaagatgctgacagggaaaggcttggacgggcttcacgtggagcggctccccatcatggacaccagcctgaaaactatcaatgatgaggcccccacagtggctctctccttccagctgctttcctctgagagaccagaaaacttcttgacaccaaacaacagcaaggctcttcctaccttggaccccaagatcctggataagaagctgagaaccatccaagagtcctggtccaaggacacagtgagctccacaatggacttgagcacgtccactccacgagaagcagaggaggaacctctggtgcctgagatgtcccatgacacagagatgaacaactaccggcgggccatgcagaagatggcagaggacatcctgtctctgcggagacaggccagcatcctggaaggagagaaccgcatactgaggagccgcctggcccagcaggaggaggaagaggggcagggcaaagccagtgaggcccagaacacggtgtccatgaagcagaaactgctgctgagtgagctggatatgaagaaactgagggacagggtgcagcatttgcagaatgagctgattcgaaagaatgatcgagagaaggagctgctccttctgtatcaggcccagcagccacaggccgctctgctgaagcagtaccagggcaagctgcagaagatgaaggcgctggaggagactgtgcggcaccaagagaaggtgatcgagaagatggagcgggtgctggaggacaggctgcaggacaggagcaagccccctcctctgaacaggcagcagggaaagccctacacgggcttccctatgctctcagcctctggccttcccttgggttctatgggagagaacctgccggttgaactttactcggtgctgctggcagaaaacgcgaagctgcggacggagctggataagaaccgccaccagcaggcccccatcattctgcagcaacaggccctgccggatctcctctctggtacttcagacaagttcaacctcctggccaagctggaacacgctcagagccggatcctgtccctggaaagccagttagaggactcagctcgacgctggggacgagagaagcaggatctggccacacggctgcaggagcaagaaaaaggtttcaggcacccctcgaactccatcatcatagaacagcctagtgccctcacccactccatggacctcaagcagccctcagagctggagcccctgctgcccagctcagactctaagctcaacaagcccttgagcccccagaaggagaccgctaactctcagcagacctgagccccagagcaggcctccttccctgtgtgctggggagtctcatcaccgccccctaaaaatgacgttattaaatgttgtagctctgtgaaaaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("CCDC33");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeChange change1 = new GenomeChange(new GenomePosition(refDict, Strand.FWD, 15, 74536399,
				PositionType.ZERO_BASED), "TAAGAAGGAGACCATCA", "ACTACCAGAGGAAT");
		Annotation annotation1 = new BlockSubstitutionAnnotationBuilder(infoForward, change1,
				new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.accession, annotation1.getTranscript().accession);
		Assert.assertEquals(1, annotation1.getAnnoLoc().rank);
		Assert.assertEquals("c.96_112delinsACTACCAGAGGAAT", annotation1.getNucleotideHGVSDescription());
		Assert.assertEquals("p.Lys33_Met38delinsLeuProGluGluLeu", annotation1.getAminoAcidHGVSDescription());
		Assert.assertEquals(
				ImmutableSortedSet.of(VariantEffect.FEATURE_TRUNCATION, VariantEffect.COMPLEX_SUBSTITUTION),
				annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc011ddm_2_second() throws InvalidGenomeChange {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc011ddm.2	chr5	-	156479371	156485970	156479372	156484954	4	156479371,156482211,156484908,156485931,	156479665,156482544,156485087,156485970,	E9PFX0	uc011ddm.2");
		this.builderForward
				.setSequence("gttacccagcattgtgagtgacagagcctggatctgaacagcaggctcatatgaatcaaccaactgggtgaaaagataagttgcaatctgagatttaagacttgatcagataccatctggtggagggtaccaaccagcctgtctgctcattttccttcaggctgatcccataatgcatcctcaagtggtcatcttaagcctcatcctacatctggcagattctgtagctggttctgtaaaggttggtggagaggcaggtccatctgtcacactaccctgccactacagtggagctgtcacatccatgtgctggaatagaggctcatgttctctattcacatgccaaaatggcattgtctggaccaatggaacccacgtcacctatcggaaggacacacgctataagctattgggggacctttcaagaagggatgtctctttgaccatagaaaatacagctgtgtctgacagtggcgtatattgttgccgtgttgagcaccgtgggtggttcaatgacatgaaaatcaccgtatcattggagattgtgccacccaaggtcacgactactccaattgtcacaactgttccaaccgtcacgactgttcgaacgagcaccactgttccaacgacaacgactgttccaatgacgactgttccaacgacaactgttccaacaacaatgagcattccaacgacaacgactgttctgacgacaatgactgtttcaacgacaacgagcgttccaacgacaacgagcattccaacaacaacaagtgttccagtgacaacaactgtctctacctttgttcctccaatgcctttgcccaggcagaaccatgaaccag"
						.toUpperCase());
		this.builderForward.setGeneSymbol("HAVCR1");
		this.infoForward = builderForward.build();
		// RefSeq NM_012206.2

		GenomeChange change1 = new GenomeChange(new GenomePosition(refDict, Strand.FWD, 5, 156479564,
				PositionType.ZERO_BASED), "AGTCGT", "GAGCTA");
		Annotation annotation1 = new BlockSubstitutionAnnotationBuilder(infoForward, change1,
				new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.accession, annotation1.getTranscript().accession);
		Assert.assertEquals(3, annotation1.getAnnoLoc().rank);
		Assert.assertEquals("c.475_480delinsTAGCTC", annotation1.getNucleotideHGVSDescription());
		Assert.assertEquals("p.Thr159*", annotation1.getAminoAcidHGVSDescription());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.MNV, VariantEffect.STOP_GAINED), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc001evp_2_second() throws InvalidGenomeChange {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001evp.2	chr1	-	150768683	150780917	150769274	150779281	8	150768683,150771643,150772019,150776496,150778336,150778577,150779161,150780689,	150769374,150771749,150772185,150776715,150778492,150778700,150779282,150780917,	P43235	uc001evp.2");
		this.builderForward
				.setSequence("acacatgctgcatacacacagaaacactgcaaatccactgcctccttccctcctccctacccttccttctctcagcatttctatccccgcctcctcctcttacccaaattttccagccgatcactggagctgacttccgcaatcccgatggaataaatctagcacccctgatggtgtgcccacactttgctgccgaaacgaagccagacaacagatttccatcagcaggatgtgggggctcaaggttctgctgctacctgtggtgagctttgctctgtaccctgaggagatactggacacccactgggagctatggaagaagacccacaggaagcaatataacaacaaggtggatgaaatctctcggcgtttaatttgggaaaaaaacctgaagtatatttccatccataaccttgaggcttctcttggtgtccatacatatgaactggctatgaaccacctgggggacatgaccagtgaagaggtggttcagaagatgactggactcaaagtacccctgtctcattcccgcagtaatgacaccctttatatcccagaatgggaaggtagagccccagactctgtcgactatcgaaagaaaggatatgttactcctgtcaaaaatcagggtcagtgtggttcctgttgggcttttagctctgtgggtgccctggagggccaactcaagaagaaaactggcaaactcttaaatctgagtccccagaacctagtggattgtgtgtctgagaatgatggctgtggagggggctacatgaccaatgccttccaatatgtgcagaagaaccggggtattgactctgaagatgcctacccatatgtgggacaggaagagagttgtatgtacaacccaacaggcaaggcagctaaatgcagagggtacagagagatccccgaggggaatgagaaagccctgaagagggcagtggcccgagtgggacctgtctctgtggccattgatgcaagcctgacctccttccagttttacagcaaaggtgtgtattatgatgaaagctgcaatagcgataatctgaaccatgcggttttggcagtgggatatggaatccagaagggaaacaagcactggataattaaaaacagctggggagaaaactggggaaacaaaggatatatcctcatggctcgaaataagaacaacgcctgtggcattgccaacctggccagcttccccaagatgtgactccagccagccaaatccatcctgctcttccatttcttccacgatggtgcagtgtaacgatgcactttggaagggagttggtgtgctatttttgaagcagatgtggtgatactgagattgtctgttcagtttccccatttgtttgtgcttcaaatgatccttcctactttgcttctctccacccatgacctttttcactgtggccatcaggactttccctgacagctgtgtactcttaggctaagagatgtgactacagcctgcccctgactgtgttgtcccagggctgatgctgtacaggtacaggctggagattttcacataggttagattctcattcacgggactagttagctttaagcaccctagaggactagggtaatctgacttctcacttcctaagttcccttctatatcctcaaggtagaaatgtctatgttttctactccaattcataaatctattcataagtctttggtacaagtttacatgataaaaagaaatgtgatttgtcttcccttctttgcacttttgaaataaagtatttatctcctgtctacagtttaataaatagcatctagtacacattcaaaaaaaaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("HAVCR1");
		this.infoForward = builderForward.build();
		// RefSeq NM_000396.3

		GenomeChange change1 = new GenomeChange(new GenomePosition(refDict, Strand.FWD, 1, 150771702,
				PositionType.ZERO_BASED), "TG", "CA");
		Annotation annotation1 = new BlockSubstitutionAnnotationBuilder(infoForward, change1,
				new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.accession, annotation1.getTranscript().accession);
		Assert.assertEquals(6, annotation1.getAnnoLoc().rank);
		Assert.assertEquals("c.830_831delinsTG", annotation1.getNucleotideHGVSDescription());
		Assert.assertEquals("p.Ala277Val", annotation1.getAminoAcidHGVSDescription());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.MNV), annotation1.getEffects());
	}

	// This change was in clinvar37 and made problems.
	@Test
	public void testRealWorldCase_uc011ayb_2() throws InvalidGenomeChange {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc011ayb.2	chr3	+	37034840	37092337	37055968	37092144	18	37034840,37042445,37045891,37048481,37050304,37053310,37053501,37055922,37058996,37061800,37067127,37070274,37081676,37083758,37089009,37090007,37090394,37091976,	37035154,37042544,37045965,37048554,37050396,37053353,37053590,37056035,37059090,37061954,37067498,37070423,37081785,37083822,37089174,37090100,37090508,37092337,	NP_001245203	uc011ayb.2");
		this.builderForward
				.setSequence("gaagagacccagcaacccacagagttgagaaatttgactggcattcaagctgtccaatcaatagctgccgctgaagggtggggctggatggcgtaagctacagctgaaggaagaacgtgagcacgaggcactgaggtgattggctgaaggcacttccgttgagcatctagacgtttccttggctcttctggcgccaaaatgtcgttcgtggcaggggttattcggcggctggacgagacagtggtgaaccgcatcgcggcgggggaagttatccagcggccagctaatgctatcaaagagatgattgagaactgaaagaagatctggatattgtatgtgaaaggttcactactagtaaactgcagtcctttgaggatttagccagtatttctacctatggctttcgaggtgaggctttggccagcataagccatgtggctcatgttactattacaacgaaaacagctgatggaaagtgtgcatacagagcaagttactcagatggaaaactgaaagcccctcctaaaccatgtgctggcaatcaagggacccagatcacggtggaggaccttttttacaacatagccacgaggagaaaagctttaaaaaatccaagtgaagaatatgggaaaattttggaagttgttggcaggtattcagtacacaatgcaggcattagtttctcagttaaaaaacaaggagagacagtagctgatgttaggacactacccaatgcctcaaccgtggacaatattcgctccatctttggaaatgctgttagtcgagaactgatagaaattggatgtgaggataaaaccctagccttcaaaatgaatggttacatatccaatgcaaactactcagtgaagaagtgcatcttcttactcttcatcaaccatcgtctggtagaatcaacttccttgagaaaagccatagaaacagtgtatgcagcctatttgcccaaaaacacacacccattcctgtacctcagtttagaaatcagtccccagaatgtggatgttaatgtgcaccccacaaagcatgaagttcacttcctgcacgaggagagcatcctggagcgggtgcagcagcacatcgagagcaagctcctgggctccaattcctccaggatgtacttcacccagactttgctaccaggacttgctggcccctctggggagatggttaaatccacaacaagtctgacctcgtcttctacttctggaagtagtgataaggtctatgcccaccagatggttcgtacagattcccgggaacagaagcttgatgcatttctgcagcctctgagcaaacccctgtccagtcagccccaggccattgtcacagaggataagacagatatttctagtggcagggctaggcagcaagatgaggagatgcttgaactcccagcccctgctgaagtggctgccaaaaatcagagcttggagggggatacaacaaaggggacttcagaaatgtcagagaagagaggacctacttccagcaaccccagaaagagacatcgggaagattctgatgtggaaatggtggaagatgattcccgaaaggaaatgactgcagcttgtaccccccggagaaggatcattaacctcactagtgttttgagtctccaggaagaaattaatgagcagggacatgaggttctccgggagatgttgcataaccactccttcgtgggctgtgtgaatcctcagtgggccttggcacagcatcaaaccaagttataccttctcaacaccaccaagcttagtgaagaactgttctaccagatactcatttatgattttgccaattttggtgttctcaggttatcggagccagcaccgctctttgaccttgccatgcttgccttagatagtccagagagtggctggacagaggaagatggtcccaaagaaggacttgctgaatacattgttgagtttctgaagaagaaggctgagatgcttgcagactatttctctttggaaattgatgaggaagggaacctgattggattaccccttctgattgacaactatgtgccccctttggagggactgcctatcttcattcttcgactagccactgaggtgaattgggacgaagaaaaggaatgttttgaaagcctcagtaaagaatgcgctatgttctattccatccggaagcagtacatatctgaggagtcgaccctctcaggccagcagagtgaagtgcctggctccattccaaactcctggaagtggactgtggaacacattgtctataaagccttgcgctcacacattctgcctcctaaacatttcacagaagatggaaatatcctgcagcttgctaacctgcctgatctatacaaagtctttgagaggtgttaaatatggttatttatgcactgtgggatgtgttcttctttctctgtattccgatacaaagtgttgtatcaaagtgtgatatacaaagtgtaccaacataagtgttggtagcacttaagacttatacttgccttctgatagtattcctttatacacagtggattgattataaataaatagatgtgtcttaacataaaaaaaaaaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("NP_001245203");
		this.infoForward = builderForward.build();
		// RefSeq NM_001258273

		GenomeChange change1 = new GenomeChange(new GenomePosition(refDict, Strand.FWD, 3, 37090097,
				PositionType.ONE_BASED), "TGAGG", "C");
		Annotation annotation1 = new BlockSubstitutionAnnotationBuilder(infoForward, change1,
				new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.accession, annotation1.getTranscript().accession);
		Assert.assertEquals(AnnotationLocation.INVALID_RANK, annotation1.getAnnoLoc().rank);
		Assert.assertEquals("c.1263_1266+1delinsC", annotation1.getNucleotideHGVSDescription());
		Assert.assertEquals("p.Glu422del", annotation1.getAminoAcidHGVSDescription());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.COMPLEX_SUBSTITUTION,
				VariantEffect.SPLICE_DONOR_VARIANT, VariantEffect.FEATURE_TRUNCATION), annotation1.getEffects());
	}

	// Bug found #87 on GitHub
	@Test
	public void testRealWorldCase_uc010nov_3() throws InvalidGenomeChange {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc010nov.3	chrX	+	103031438	103047547	103031923	103045526	8	103031438,103031780,103040510,103041393,103042726,103043365,103044261,103045454,	103031575,103031927,103040697,103041655,103042895,103043439,103044327,103047547,	P60201	uc010nov.3");
		this.builderForward
				.setSequence("atggcttctcacgcttgtgctgcatatcccacaccaattagacccaaggatcagttggaagtttccaggacatcttcattttatttccaccctcaatccacatttccagatgtctctgcagcaaagcgaaattccaggagaagaggacaaagatactcagagagaaaaagtaaaagaccgaagaaggaggctggagagaccaggatccttccagctgaacaaagtcagccacaaagcagactagccagccggctacaattggagtcagagtcccaaagacatgggcttgttagagtgctgtgcaagatgtctggtaggggccccctttgcttccctggtggccactggattgtgtttctttggggtggcactgttctgtggctgtggacatgaagccctcactggcacagaaaagctaattgagacctatttctccaaaaactaccaagactatgagtatctcatcaatgtgatccatgccttccagtatgtcatctatggaactgcctctttcttcttcctttatggggccctcctgctggctgagggcttctacaccaccggcgcagtcaggcagatctttggcgactacaagaccaccatctgcggcaagggcctgagcgcaacggtaacagggggccagaaggggaggggttccagaggccaacatcaagctcattctttggagcgggtgtgtcattgtttgggaaaatggctaggacatcccgacaagtttgtgggcatcacctatgccctgaccgttgtgtggctcctggtgtttgcctgctctgctgtgcctgtgtacatttacttcaacacctggaccacctgccagtctattgccttccccagcaagacctctgccagtataggcagtctctgtgctgatgccagaatgtatggtgttctcccatggaatgctttccctggcaaggtttgtggctccaaccttctgtccatctgcaaaacagctgagttccaaatgaccttccacctgtttattgctgcatttgtgggggctgcagctacactggtttccctgctcaccttcatgattgctgccacttacaactttgccgtccttaaactcatgggccgaggcaccaagttctgatcccccgtagaaatccccctttctctaatagcgaggctctaaccacacagcctacaatgctgcgtctcccatcttaactctttgcctttgccaccaactggccctcttcttacttgatgagtgtaacaagaaaggagagtcttgcagtgattaaggtctctctttggactctcccctcttatgtacctcttttagtcattttgcttcatagctggttcctgctagaaatgggaaatgcctaagaagatgacttcccaactgcaagtcacaaaggaatggaggctctaattgaattttcaagcatctcctgaggatcagaaagtaatttcttctcaaagggtacttccactgatggaaacaaagtggaaggaaagatgctcaggtacagagaaggaatgtctttggtcctcttgccatctataggggccaaatatattctctttggtgtacaaaatggaattcattctggtctctctattaccactgaagatagaagaaaaaagaatgtcagaaaaacaataagagcgtttgcccaaatctgcctattgcagctgggagaagggggtcaaagcaaggatctttcacccacagaaagagagcactgaccccgatggcgatggactactgaagccctaactcagccaaccttacttacagcataagggagcgtagaatctgtgtagacgaagggggcatctggccttacacctcgttagggaagagaaacagggtgttgtcagcatcttctcactcccttctccttgataacagctaccatgacaaccctgtggtttccaaggagctgagaatagaaggaaactagcttacatgagaacagactggcctgaggagcagcagttgctggtggctaatggtgtaacctgagatggccctctggtagacacaggatagataactctttggatagcatgtctttttttctgttaattagttgtgtactctggcctctgtcatatcttcacaatggtgctcatttcatgggggtattatccattcagtcatcgtaggtgatttgaaggtcttgatttgttttagaatgatgcacatttcatgtattccagtttgtttattacttatttggggttgcatcagaaatgtctggagaataattctttgattatgactgttttttaaactaggaaaattggacattaagcatcacaaatgatattaaaaattggctagttgaatctattgggattttctacaagtattctgcctttgcagaaacagatttggtgaatttgaatctcaatttgagtaatctgatcgttctttctagctaatggaaaatgattttacttagcaatgttatcttggtgtgttaagagttaggtttaacataaaggttattttctcctgatatagatcacataacagaatgcaccagtcatcagctattcagttggtaagcttccaggaaaaaggacaggcagaaagagtttgagacctgaatagctcccagatttcagtcttttcctgtttttgttaactttgggttaaaaaaaaaaaaagtctgattggttttaattgaaggaaagatttgtactacagttcttttgttgtaaagagttgtgttgttcttttcccccaaagtggtttcagcaatatttaaggagatgtaagagctttacaaaaagacacttgatacttgttttcaaaccagtatacaagataagcttccaggctgcatagaaggaggagagggaaaatgttttgtaagaaaccaatcaagataaaggacagtgaagtaatccgtaccttgtgttttgttttgatttaataacataacaaataaccaacccttccctgaaaacctcacatgcatacatacacatatatacacacacaaagagagttaatcaactgaaagtgtttccttcatttctgatatagaattgcaattttaacacacataaaggataaacttttagaaacttatcttacaaagtgtattttataaaattaaagaaaataaaattaagaatgttctcaatcaaaaaaaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("PLP1");
		this.infoForward = builderForward.build();
		// RefSeq NM_001128834.1

		GenomeChange change1 = new GenomeChange(new GenomePosition(refDict, Strand.FWD, refDict.contigID.get("X"),
				103041655, PositionType.ONE_BASED), "GGTGATC", "A");
		Annotation annotation1 = new BlockSubstitutionAnnotationBuilder(infoForward, change1,
				new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.accession, annotation1.getTranscript().accession);
		Assert.assertEquals(AnnotationLocation.INVALID_RANK, annotation1.getAnnoLoc().rank);
		Assert.assertEquals("c.453_453+6delinsA", annotation1.getNucleotideHGVSDescription());
		Assert.assertEquals("p.=", annotation1.getAminoAcidHGVSDescription());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.COMPLEX_SUBSTITUTION,
				VariantEffect.SPLICE_DONOR_VARIANT, VariantEffect.FEATURE_TRUNCATION), annotation1.getEffects());
	}

}
