package de.charite.compbio.jannovar.annotation.builders;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableSortedSet;

import de.charite.compbio.jannovar.annotation.Annotation;
import de.charite.compbio.jannovar.annotation.AnnotationLocation;
import de.charite.compbio.jannovar.annotation.InvalidGenomeChange;
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

// TODO(holtgrem): What exactly should be counted as stop gain?
// TODO(holtgrem): check distance computation in annotation, for upstream/downstream etc. also in on other tests

public class InsertionAnnotationBuilderTest {

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
	public void testForwardUpstream() throws InvalidGenomeChange {
		GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640061,
				PositionType.ZERO_BASED), "", "A");
		Annotation anno = new InsertionAnnotationBuilder(infoForward, change, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), anno.getTranscript().getAccession());
		Assert.assertEquals(null, anno.getAnnoLoc());
		Assert.assertEquals(null, anno.getCDSNTChange());
		Assert.assertEquals(null, anno.getProteinChange());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.UPSTREAM_GENE_VARIANT), anno.getEffects());
	}

	@Test
	public void testForwardDownstream() throws InvalidGenomeChange {
		GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6649340,
				PositionType.ZERO_BASED), "", "A");
		Annotation anno = new InsertionAnnotationBuilder(infoForward, change, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), anno.getTranscript().getAccession());
		Assert.assertEquals(null, anno.getAnnoLoc());
		Assert.assertEquals(null, anno.getCDSNTChange());
		Assert.assertEquals(null, anno.getProteinChange());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.DOWNSTREAM_GENE_VARIANT), anno.getEffects());
	}

	@Test
	public void testForwardIntergenic() throws InvalidGenomeChange {
		// upstream intergenic
		GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6639062,
				PositionType.ZERO_BASED), "", "A");
		Annotation anno = new InsertionAnnotationBuilder(infoForward, change, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), anno.getTranscript().getAccession());
		Assert.assertEquals(null, anno.getAnnoLoc());
		Assert.assertEquals(null, anno.getCDSNTChange());
		Assert.assertEquals(null, anno.getProteinChange());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.INTERGENIC_VARIANT), anno.getEffects());

		// downstream intergenic
		GenomeVariant change2 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6650340,
				PositionType.ZERO_BASED), "", "A");
		Annotation anno2 = new InsertionAnnotationBuilder(infoForward, change2, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), anno2.getTranscript().getAccession());
		Assert.assertEquals(null, anno2.getAnnoLoc());
		Assert.assertEquals(null, anno2.getCDSNTChange());
		Assert.assertEquals(null, anno2.getProteinChange());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.INTERGENIC_VARIANT), anno2.getEffects());
	}

	@Test
	public void testForwardIntronic() throws InvalidGenomeChange {
		GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6646098,
				PositionType.ZERO_BASED), "", "A");
		Annotation anno = new InsertionAnnotationBuilder(infoForward, change, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), anno.getTranscript().getAccession());
		Assert.assertEquals(3, anno.getAnnoLoc().getRank());
		Assert.assertEquals("1044+8_1044+9insA", anno.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", anno.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.CODING_TRANSCRIPT_INTRON_VARIANT), anno.getEffects());
	}

	@Test
	public void testForwardFivePrimeUTR() throws InvalidGenomeChange {
		GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640669,
				PositionType.ZERO_BASED), "", "C");
		Annotation anno = new InsertionAnnotationBuilder(infoForward, change, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), anno.getTranscript().getAccession());
		Assert.assertEquals(1, anno.getAnnoLoc().getRank());
		Assert.assertEquals("-1dup", anno.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", anno.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FIVE_PRIME_UTR_VARIANT), anno.getEffects());
	}

	@Test
	public void testForwardThreePrimeUTR() throws InvalidGenomeChange {
		GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6649272,
				PositionType.ZERO_BASED), "", "A");
		Annotation anno = new InsertionAnnotationBuilder(infoForward, change, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), anno.getTranscript().getAccession());
		Assert.assertEquals(10, anno.getAnnoLoc().getRank());
		Assert.assertEquals("2067_*1insA", anno.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", anno.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.THREE_PRIME_UTR_VARIANT), anno.getEffects());
	}

	@Test
	public void testForwardSplicing() throws InvalidGenomeChange {
		// TODO(holtgrem): test more cases
		GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6642117,
				PositionType.ZERO_BASED), "", "ACT");
		Annotation anno = new InsertionAnnotationBuilder(infoForward, change, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), anno.getTranscript().getAccession());
		Assert.assertEquals(2, anno.getAnnoLoc().getRank());
		Assert.assertEquals("691-1_691insACT", anno.getCDSNTChange().toHGVSString());
		Assert.assertEquals("?", anno.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.SPLICE_ACCEPTOR_VARIANT,
				VariantEffect.CODING_TRANSCRIPT_INTRON_VARIANT), anno.getEffects());
	}

	@Test
	public void testForwardThreeBasesNoFrameShiftInsertion() throws InvalidGenomeChange {
		// Tests insertion of three bases (smallest no-frameshift insertion).

		// TODO(holtgrem): The WT stop codon is replaced by another one -- duplication.
		// TODO(holtgrem): The WT start codon is replaced by another one -- duplication.

		// The WT stop codon is replaced by another one.
		GenomeVariant change1agc = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6649271,
				PositionType.ZERO_BASED), "", "AGC");
		Annotation annotation1agc = new InsertionAnnotationBuilder(infoForward, change1agc,
				new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1agc.getTranscript().getAccession());
		Assert.assertEquals(10, annotation1agc.getAnnoLoc().getRank());
		Assert.assertEquals("2066_2067insAGC", annotation1agc.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", annotation1agc.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.SYNONYMOUS_VARIANT), annotation1agc.getEffects());

		// The WT stop codon is destroyed but there is a new one downstream
		GenomeVariant change1tgc = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6649271,
				PositionType.ZERO_BASED), "", "TGC");
		Annotation annotation1tgc = new InsertionAnnotationBuilder(infoForward, change1tgc,
				new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1tgc.getTranscript().getAccession());
		Assert.assertEquals(10, annotation1tgc.getAnnoLoc().getRank());
		Assert.assertEquals("2066_2067insTGC", annotation1tgc.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(*689Tyrext*24)", annotation1tgc.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.STOP_LOST), annotation1tgc.getEffects());

		// Test case where the start codon is destroyed.
		GenomeVariant change2agc = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640670,
				PositionType.ZERO_BASED), "", "AGC");
		Annotation annotation2agc = new InsertionAnnotationBuilder(infoForward, change2agc,
				new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation2agc.getTranscript().getAccession());
		Assert.assertEquals(1, annotation2agc.getAnnoLoc().getRank());
		Assert.assertEquals("1_2insAGC", annotation2agc.getCDSNTChange().toHGVSString());
		Assert.assertEquals("0?", annotation2agc.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.INFRAME_INSERTION, VariantEffect.START_LOST),
				annotation2agc.getEffects());

		// Test cases where the start codon is not subjected to an insertion.

		// Directly insert stop codon.
		GenomeVariant change3taa = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640672,
				PositionType.ZERO_BASED), "", "TAA");
		Annotation annotation3taa = new InsertionAnnotationBuilder(infoForward, change3taa,
				new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation3taa.getTranscript().getAccession());
		Assert.assertEquals(1, annotation3taa.getAnnoLoc().getRank());
		Assert.assertEquals("3_4insTAA", annotation3taa.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Asp2*)", annotation3taa.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.INFRAME_INSERTION, VariantEffect.STOP_GAINED),
				annotation3taa.getEffects());

		// Directly insert some base and then a stop codon.
		GenomeVariant change3tcctaa = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640672,
				PositionType.ZERO_BASED), "", "TCCTAA");
		Annotation annotation3tcctaa = new InsertionAnnotationBuilder(infoForward, change3tcctaa,
				new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation3tcctaa.getTranscript().getAccession());
		Assert.assertEquals(1, annotation3tcctaa.getAnnoLoc().getRank());
		Assert.assertEquals("3_4insTCCTAA", annotation3tcctaa.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Asp2_Gly3delinsSer)", annotation3tcctaa.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.INFRAME_INSERTION, VariantEffect.STOP_GAINED),
				annotation3tcctaa.getEffects());

		// Insertion without a new stop codon that is no duplication.
		GenomeVariant change4tcctcctcc = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640672,
				PositionType.ZERO_BASED), "", "TCCTCCTCC");
		Annotation annotation4tcctcctcc = new InsertionAnnotationBuilder(infoForward, change4tcctcctcc,
				new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation4tcctcctcc.getTranscript().getAccession());
		Assert.assertEquals(1, annotation4tcctcctcc.getAnnoLoc().getRank());
		Assert.assertEquals("3_4insTCCTCCTCC", annotation4tcctcctcc.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Met1_Asp2insSerSerSer)", annotation4tcctcctcc.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.INFRAME_INSERTION), annotation4tcctcctcc.getEffects());

		// Insertion without a new stop codon that is a duplication.
		GenomeVariant change5gatggc = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640672,
				PositionType.ZERO_BASED), "", "GATGGC");
		Annotation annotation5gatggc = new InsertionAnnotationBuilder(infoForward, change5gatggc,
				new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation5gatggc.getTranscript().getAccession());
		Assert.assertEquals(1, annotation5gatggc.getAnnoLoc().getRank());
		Assert.assertEquals("5_6insTGGCGA", annotation5gatggc.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Asp2_Gly3dup)", annotation5gatggc.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.DISRUPTIVE_INFRAME_INSERTION,
				VariantEffect.DIRECT_TANDEM_DUPLICATION), annotation5gatggc.getEffects());
	}

	@Test
	public void testForwardOneBaseFrameShiftInsertion() throws InvalidGenomeChange {
		// We check some one-nucleotide insertions in the first ten bases and compared them by hand to Mutalyzer
		// results.

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640670,
				PositionType.ZERO_BASED), "", "G");
		Annotation annotation1 = new InsertionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(1, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("1_2insG", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("0?", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.START_LOST), annotation1.getEffects());

		GenomeVariant change2 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640671,
				PositionType.ZERO_BASED), "", "A");
		Annotation annotation2 = new InsertionAnnotationBuilder(infoForward, change2, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation2.getTranscript().getAccession());
		Assert.assertEquals(1, annotation2.getAnnoLoc().getRank());
		Assert.assertEquals("2_3insA", annotation2.getCDSNTChange().toHGVSString());
		Assert.assertEquals("0?", annotation2.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.START_LOST), annotation2.getEffects());

		// Try to insert all non-duplicate NTs between 3 and 4.

		GenomeVariant change3a = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640672,
				PositionType.ZERO_BASED), "", "A");
		Annotation annotation3a = new InsertionAnnotationBuilder(infoForward, change3a, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation3a.getTranscript().getAccession());
		Assert.assertEquals(1, annotation3a.getAnnoLoc().getRank());
		Assert.assertEquals("3_4insA", annotation3a.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Asp2Argfs*37)", annotation3a.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FRAMESHIFT_VARIANT), annotation3a.getEffects());

		GenomeVariant change3c = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640672,
				PositionType.ZERO_BASED), "", "C");
		Annotation annotation3c = new InsertionAnnotationBuilder(infoForward, change3c, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation3c.getTranscript().getAccession());
		Assert.assertEquals(1, annotation3c.getAnnoLoc().getRank());
		Assert.assertEquals("3_4insC", annotation3c.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Asp2Argfs*37)", annotation3c.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FRAMESHIFT_VARIANT), annotation3c.getEffects());

		GenomeVariant change3t = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640672,
				PositionType.ZERO_BASED), "", "T");
		Annotation annotation3t = new InsertionAnnotationBuilder(infoForward, change3t, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation3t.getTranscript().getAccession());
		Assert.assertEquals(1, annotation3t.getAnnoLoc().getRank());
		Assert.assertEquals("3_4insT", annotation3t.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Asp2*)", annotation3t.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.STOP_GAINED), annotation3t.getEffects());

		// Try to insert all non-duplicate NTs between 4 and 5.

		GenomeVariant change4c = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640673,
				PositionType.ZERO_BASED), "", "C");
		Annotation annotation4c = new InsertionAnnotationBuilder(infoForward, change4c, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation4c.getTranscript().getAccession());
		Assert.assertEquals(1, annotation4c.getAnnoLoc().getRank());
		Assert.assertEquals("4_5insC", annotation4c.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Asp2Alafs*37)", annotation4c.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FRAMESHIFT_VARIANT), annotation4c.getEffects());

		GenomeVariant change4t = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640673,
				PositionType.ZERO_BASED), "", "T");
		Annotation annotation4t = new InsertionAnnotationBuilder(infoForward, change4t, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation4t.getTranscript().getAccession());
		Assert.assertEquals(1, annotation4t.getAnnoLoc().getRank());
		Assert.assertEquals("4_5insT", annotation4t.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Asp2Valfs*37)", annotation4t.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FRAMESHIFT_VARIANT), annotation4t.getEffects());

		// Try to insert all non-duplicate NTs between 5 and 6.

		GenomeVariant change5g = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640674,
				PositionType.ZERO_BASED), "", "G");
		Annotation annotation5g = new InsertionAnnotationBuilder(infoForward, change5g, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation5g.getTranscript().getAccession());
		Assert.assertEquals(1, annotation5g.getAnnoLoc().getRank());
		Assert.assertEquals("5_6insG", annotation5g.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Asp2Glufs*37)", annotation5g.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FRAMESHIFT_VARIANT), annotation5g.getEffects());

		GenomeVariant change5t = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640674,
				PositionType.ZERO_BASED), "", "T");
		Annotation annotation5t = new InsertionAnnotationBuilder(infoForward, change5t, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation5t.getTranscript().getAccession());
		Assert.assertEquals(1, annotation5t.getAnnoLoc().getRank());
		Assert.assertEquals("5_6insT", annotation5t.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Gly3Argfs*36)", annotation5t.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FRAMESHIFT_VARIANT), annotation5t.getEffects());

		// It appears to be impossible to force a stop loss for this transcript.

		// Tests for stop shift.
		GenomeVariant change6t = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6649271,
				PositionType.ZERO_BASED), "", "T");
		Annotation annotation6t = new InsertionAnnotationBuilder(infoForward, change6t, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation6t.getTranscript().getAccession());
		Assert.assertEquals(10, annotation6t.getAnnoLoc().getRank());
		Assert.assertEquals("2066_2067insT", annotation6t.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(*689Tyrext*15)", annotation6t.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FRAMESHIFT_ELONGATION), annotation6t.getEffects());

		GenomeVariant change6c = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6649270,
				PositionType.ZERO_BASED), "", "C");
		Annotation annotation6c = new InsertionAnnotationBuilder(infoForward, change6c, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation6c.getTranscript().getAccession());
		Assert.assertEquals(10, annotation6c.getAnnoLoc().getRank());
		Assert.assertEquals("2065_2066insC", annotation6c.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(*689Serext*15)", annotation6c.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FRAMESHIFT_ELONGATION), annotation6c.getEffects());

		// Test for no change when inserting into stop codon.
		GenomeVariant change7g = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6649270,
				PositionType.ZERO_BASED), "", "G");
		Annotation annotation7g = new InsertionAnnotationBuilder(infoForward, change7g, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation7g.getTranscript().getAccession());
		Assert.assertEquals(10, annotation7g.getAnnoLoc().getRank());
		Assert.assertEquals("2065_2066insG", annotation7g.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", annotation7g.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.SYNONYMOUS_VARIANT), annotation7g.getEffects());
	}

	@Test
	public void testForwardTwoBaseFrameShiftInsertion() throws InvalidGenomeChange {
		// We check some two-nucleotide insertions in the first ten bases and compared them by hand to Mutalyzer
		// results.

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640670,
				PositionType.ZERO_BASED), "", "GA");
		Annotation annotation1 = new InsertionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(1, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("1_2insGA", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("0?", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.START_LOST), annotation1.getEffects());

		GenomeVariant change2 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640671,
				PositionType.ZERO_BASED), "", "AG");
		Annotation annotation2 = new InsertionAnnotationBuilder(infoForward, change2, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation2.getTranscript().getAccession());
		Assert.assertEquals(1, annotation2.getAnnoLoc().getRank());
		Assert.assertEquals("2_3insAG", annotation2.getCDSNTChange().toHGVSString());
		Assert.assertEquals("0?", annotation2.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.START_LOST), annotation2.getEffects());

		// Try to insert some non-duplicate NT pairs between 3 and 4.

		GenomeVariant change3ac = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640672,
				PositionType.ZERO_BASED), "", "AC");
		Annotation annotation3ac = new InsertionAnnotationBuilder(infoForward, change3ac,
				new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation3ac.getTranscript().getAccession());
		Assert.assertEquals(1, annotation3ac.getAnnoLoc().getRank());
		Assert.assertEquals("3_4insAC", annotation3ac.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Asp2Thrfs*10)", annotation3ac.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FRAMESHIFT_ELONGATION), annotation3ac.getEffects());

		GenomeVariant change3cg = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640672,
				PositionType.ZERO_BASED), "", "CG");
		Annotation annotation3cg = new InsertionAnnotationBuilder(infoForward, change3cg,
				new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation3cg.getTranscript().getAccession());
		Assert.assertEquals(1, annotation3cg.getAnnoLoc().getRank());
		Assert.assertEquals("3_4insCG", annotation3cg.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Asp2Argfs*10)", annotation3cg.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FRAMESHIFT_ELONGATION), annotation3cg.getEffects());

		GenomeVariant change3ta = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640672,
				PositionType.ZERO_BASED), "", "TA");
		Annotation annotation3ta = new InsertionAnnotationBuilder(infoForward, change3ta,
				new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation3ta.getTranscript().getAccession());
		Assert.assertEquals(1, annotation3ta.getAnnoLoc().getRank());
		Assert.assertEquals("3_4insTA", annotation3ta.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Asp2*)", annotation3ta.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.STOP_GAINED), annotation3ta.getEffects());

		// Try to insert some non-duplicate NT pairs between 4 and 5.

		GenomeVariant change4ct = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640673,
				PositionType.ZERO_BASED), "", "CT");
		Annotation annotation4ct = new InsertionAnnotationBuilder(infoForward, change4ct,
				new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation4ct.getTranscript().getAccession());
		Assert.assertEquals(1, annotation4ct.getAnnoLoc().getRank());
		Assert.assertEquals("4_5insCT", annotation4ct.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Asp2Alafs*10)", annotation4ct.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FRAMESHIFT_ELONGATION), annotation3cg.getEffects());

		GenomeVariant change4tg = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640673,
				PositionType.ZERO_BASED), "", "TG");
		Annotation annotation4tg = new InsertionAnnotationBuilder(infoForward, change4tg,
				new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation4tg.getTranscript().getAccession());
		Assert.assertEquals(1, annotation4tg.getAnnoLoc().getRank());
		Assert.assertEquals("4_5insTG", annotation4tg.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Asp2Valfs*10)", annotation4tg.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.STOP_GAINED), annotation3ta.getEffects());

		// Try to insert some non-duplicate NT pairs between 5 and 6.

		GenomeVariant change5gc = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640674,
				PositionType.ZERO_BASED), "", "GC");
		Annotation annotation5gc = new InsertionAnnotationBuilder(infoForward, change5gc,
				new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation5gc.getTranscript().getAccession());
		Assert.assertEquals(1, annotation5gc.getAnnoLoc().getRank());
		Assert.assertEquals("5_6insGC", annotation5gc.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Asp2Glufs*10)", annotation5gc.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.STOP_GAINED), annotation3ta.getEffects());

		GenomeVariant change5ta = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640674,
				PositionType.ZERO_BASED), "", "TA");
		Annotation annotation5ta = new InsertionAnnotationBuilder(infoForward, change5ta,
				new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation5ta.getTranscript().getAccession());
		Assert.assertEquals(1, annotation5ta.getAnnoLoc().getRank());
		Assert.assertEquals("5_6insTA", annotation5ta.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Gly3Thrfs*9)", annotation5ta.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FRAMESHIFT_ELONGATION), annotation3cg.getEffects());
	}

	@Test
	public void testForwardOnFourBaseFrameShiftInsertion() throws InvalidGenomeChange {
		// We check some four-nucleotide insertions in the first ten bases and compared them by hand to Mutalyzer
		// results.

		// Try to insert some non-duplicate NT pairs between 4 and 5.

		GenomeVariant change4actagact = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640673,
				PositionType.ZERO_BASED), "", "ACTAGACT");
		Annotation annotation4actagact = new InsertionAnnotationBuilder(infoForward, change4actagact,
				new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation4actagact.getTranscript().getAccession());
		Assert.assertEquals(1, annotation4actagact.getAnnoLoc().getRank());
		Assert.assertEquals("6_7insTAGACTAC", annotation4actagact.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Gly3*)", annotation4actagact.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.STOP_GAINED), annotation4actagact.getEffects());

		GenomeVariant change4cgtg = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640673,
				PositionType.ZERO_BASED), "", "CGTG");
		Annotation annotation4cgtg = new InsertionAnnotationBuilder(infoForward, change4cgtg,
				new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation4cgtg.getTranscript().getAccession());
		Assert.assertEquals(1, annotation4cgtg.getAnnoLoc().getRank());
		Assert.assertEquals("4_5insCGTG", annotation4cgtg.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Asp2Alafs*2)", annotation4cgtg.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FRAMESHIFT_ELONGATION), annotation4cgtg.getEffects());
	}

	@Test
	public void testReverseOneBaseFrameShiftInsertion() throws InvalidGenomeChange {
		// We check some one-nucleotide insertions in the first ten bases and compared them by hand to Mutalyzer
		// results. We perform less tests than for the forward case since the only change is the coordinate system
		// transformation to the reverse strand and the reverse-complementing of the inserted bases.

		// Insert C and G between nucleotides 1 and 2.

		GenomeVariant change1c = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 23694497,
				PositionType.ZERO_BASED), "", "C");
		Annotation annotation1c = new InsertionAnnotationBuilder(infoReverse, change1c, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoReverse.getAccession(), annotation1c.getTranscript().getAccession());
		Assert.assertEquals(1, annotation1c.getAnnoLoc().getRank());
		Assert.assertEquals("1_2insG", annotation1c.getCDSNTChange().toHGVSString());
		Assert.assertEquals("0?", annotation1c.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.START_LOST), annotation1c.getEffects());

		GenomeVariant change1g = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 23694497,
				PositionType.ZERO_BASED), "", "G");
		Annotation annotation1g = new InsertionAnnotationBuilder(infoReverse, change1g, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoReverse.getAccession(), annotation1g.getTranscript().getAccession());
		Assert.assertEquals(1, annotation1g.getAnnoLoc().getRank());
		Assert.assertEquals("1_2insC", annotation1g.getCDSNTChange().toHGVSString());
		Assert.assertEquals("0?", annotation1g.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.START_LOST), annotation1g.getEffects());

		// Insert A and C between nucleotides 2 and 3.

		GenomeVariant change2a = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 23694496,
				PositionType.ZERO_BASED), "", "T");
		Annotation annotation2a = new InsertionAnnotationBuilder(infoReverse, change2a, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoReverse.getAccession(), annotation2a.getTranscript().getAccession());
		Assert.assertEquals(1, annotation2a.getAnnoLoc().getRank());
		Assert.assertEquals("2_3insA", annotation2a.getCDSNTChange().toHGVSString());
		Assert.assertEquals("0?", annotation2a.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.START_LOST), annotation2a.getEffects());

		GenomeVariant change2c = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 23694496,
				PositionType.ZERO_BASED), "", "G");
		Annotation annotation2c = new InsertionAnnotationBuilder(infoReverse, change2c, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoReverse.getAccession(), annotation2c.getTranscript().getAccession());
		Assert.assertEquals(1, annotation2c.getAnnoLoc().getRank());
		Assert.assertEquals("2_3insC", annotation2c.getCDSNTChange().toHGVSString());
		Assert.assertEquals("0?", annotation2c.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.START_LOST), annotation2c.getEffects());

		// Insertions between nucleotides 3 and 4.

		GenomeVariant change3a = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 23694495,
				PositionType.ZERO_BASED), "", "T");
		Annotation annotation3a = new InsertionAnnotationBuilder(infoReverse, change3a, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoReverse.getAccession(), annotation3a.getTranscript().getAccession());
		Assert.assertEquals(1, annotation3a.getAnnoLoc().getRank());
		Assert.assertEquals("3_4insA", annotation3a.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Ala2Serfs*16)", annotation3a.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FRAMESHIFT_VARIANT), annotation3a.getEffects());

		GenomeVariant change3c = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 23694495,
				PositionType.ZERO_BASED), "", "G");
		Annotation annotation3c = new InsertionAnnotationBuilder(infoReverse, change3c, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoReverse.getAccession(), annotation3c.getTranscript().getAccession());
		Assert.assertEquals(1, annotation3c.getAnnoLoc().getRank());
		Assert.assertEquals("3_4insC", annotation3c.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Ala2Argfs*16)", annotation3c.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FRAMESHIFT_VARIANT), annotation3c.getEffects());

		// Some insertions into stop codon

		GenomeVariant change4g = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 23688463,
				PositionType.ZERO_BASED), "", "G");
		Annotation annotation4g = new InsertionAnnotationBuilder(infoReverse, change4g, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoReverse.getAccession(), annotation4g.getTranscript().getAccession());
		Assert.assertEquals(3, annotation4g.getAnnoLoc().getRank());
		Assert.assertEquals("1411_1412insC", annotation4g.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(*471Serext*7)", annotation4g.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FRAMESHIFT_ELONGATION), annotation4g.getEffects());

		GenomeVariant change4c = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 23688463,
				PositionType.ZERO_BASED), "", "C");
		Annotation annotation4c = new InsertionAnnotationBuilder(infoReverse, change4c, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoReverse.getAccession(), annotation4c.getTranscript().getAccession());
		Assert.assertEquals(3, annotation4c.getAnnoLoc().getRank());
		Assert.assertEquals("1411_1412insG", annotation4c.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", annotation4c.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.SYNONYMOUS_VARIANT), annotation4c.getEffects());
	}

	@Test
	public void testReverseOnFourBaseFrameShiftInsertion() throws InvalidGenomeChange {
		// We check some four-nucleotide insertions in the first ten bases and in the stop codon and compared them by
		// hand to Mutalyzer results.

		// Try to insert some non-duplicate NT pairs between 4 and 5.

		GenomeVariant change4actagact = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 23694494,
				PositionType.ZERO_BASED), "", "ACTAGACT");
		Annotation annotation4actagact = new InsertionAnnotationBuilder(infoReverse, change4actagact,
				new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoReverse.getAccession(), annotation4actagact.getTranscript().getAccession());
		Assert.assertEquals(1, annotation4actagact.getAnnoLoc().getRank());
		Assert.assertEquals("4_5insAGTCTAGT", annotation4actagact.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Ala2Glufs*16)", annotation4actagact.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FRAMESHIFT_ELONGATION),
				annotation4actagact.getEffects());

		// This insertion will be shifted.
		GenomeVariant change4cgtg = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 23694494,
				PositionType.ZERO_BASED), "", "CGTG");
		Annotation annotation4cgtg = new InsertionAnnotationBuilder(infoReverse, change4cgtg,
				new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoReverse.getAccession(), annotation4cgtg.getTranscript().getAccession());
		Assert.assertEquals(1, annotation4cgtg.getAnnoLoc().getRank());
		Assert.assertEquals("6_7insCGCA", annotation4cgtg.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Ala3Argfs*16)", annotation4cgtg.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FRAMESHIFT_ELONGATION), annotation4cgtg.getEffects());

		// Insert whole stop codon.
		GenomeVariant change5cgtg = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 23694492,
				PositionType.ZERO_BASED), "", "ATTA");
		Annotation annotation5cgtg = new InsertionAnnotationBuilder(infoReverse, change5cgtg,
				new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoReverse.getAccession(), annotation5cgtg.getTranscript().getAccession());
		Assert.assertEquals(1, annotation5cgtg.getAnnoLoc().getRank());
		Assert.assertEquals("6_7insTAAT", annotation5cgtg.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Ala3*)", annotation5cgtg.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.STOP_GAINED), annotation5cgtg.getEffects());
	}

	@Test
	public void testInsertionAtIntronExonBorder() throws InvalidGenomeChange {
		String x = "uc001anx.3	chr1	+	6640062	6649340	6640669	6649272	11	6640062,6640600,6642117,6645978,6646754,6647264,6647537,6648119,6648337,6648815,6648975,	6640196,6641359,6642359,6646090,6646847,6647351,6647692,6648256,6648502,6648904,6649340,	P10074	uc001anx.3";

		// directly after the exons

		GenomeVariant varInsertionAfterExon1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640196,
				PositionType.ZERO_BASED), "", "A");
		Annotation annoInsertionAfterExon1 = new InsertionAnnotationBuilder(infoForward, varInsertionAfterExon1,
				new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annoInsertionAfterExon1.getTranscript().getAccession());
		Assert.assertEquals(0, annoInsertionAfterExon1.getAnnoLoc().getRank());
		Assert.assertEquals("-70_-70+1insA", annoInsertionAfterExon1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", annoInsertionAfterExon1.getProteinChange().toHGVSString()); // XXX
		Assert.assertEquals(
				ImmutableSortedSet.of(VariantEffect.SPLICE_REGION_VARIANT, VariantEffect.FIVE_PRIME_UTR_VARIANT),
				annoInsertionAfterExon1.getEffects());

		GenomeVariant varInsertionAfterExon2 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6641359,
				PositionType.ZERO_BASED), "", "A");
		Annotation annoInsertionAfterExon2 = new InsertionAnnotationBuilder(infoForward, varInsertionAfterExon2,
				new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annoInsertionAfterExon2.getTranscript().getAccession());
		Assert.assertEquals(1, annoInsertionAfterExon2.getAnnoLoc().getRank());
		Assert.assertEquals("690_690+1insA", annoInsertionAfterExon2.getCDSNTChange().toHGVSString());
		Assert.assertEquals("?", annoInsertionAfterExon2.getProteinChange().toHGVSString()); // XXX
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.SPLICE_DONOR_VARIANT,
				VariantEffect.CODING_TRANSCRIPT_INTRON_VARIANT), annoInsertionAfterExon2.getEffects());

		GenomeVariant varInsertionAfterExon3 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6642359,
				PositionType.ZERO_BASED), "", "A");
		Annotation annoInsertionAfterExon3 = new InsertionAnnotationBuilder(infoForward, varInsertionAfterExon3,
				new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annoInsertionAfterExon3.getTranscript().getAccession());
		Assert.assertEquals(2, annoInsertionAfterExon3.getAnnoLoc().getRank());
		Assert.assertEquals("932_932+1insA", annoInsertionAfterExon3.getCDSNTChange().toHGVSString());
		Assert.assertEquals("?", annoInsertionAfterExon3.getProteinChange().toHGVSString()); // XXX
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.SPLICE_DONOR_VARIANT,
				VariantEffect.CODING_TRANSCRIPT_INTRON_VARIANT), annoInsertionAfterExon3.getEffects());

		// directly before the exons

		GenomeVariant varInsertionBeforeExon1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640600,
				PositionType.ZERO_BASED), "", "A");
		Annotation annoInsertionBeforeExon1 = new InsertionAnnotationBuilder(infoForward, varInsertionBeforeExon1,
				new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annoInsertionBeforeExon1.getTranscript().getAccession());
		Assert.assertEquals(1, annoInsertionBeforeExon1.getAnnoLoc().getRank());
		Assert.assertEquals("-69-1_-69insA", annoInsertionBeforeExon1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", annoInsertionBeforeExon1.getProteinChange().toHGVSString());
		Assert.assertEquals(
				ImmutableSortedSet.of(VariantEffect.SPLICE_REGION_VARIANT, VariantEffect.FIVE_PRIME_UTR_VARIANT),
				annoInsertionBeforeExon1.getEffects());

		GenomeVariant varInsertionBeforeExon2 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6642117,
				PositionType.ZERO_BASED), "", "A");
		Annotation annoInsertionBeforeExon2 = new InsertionAnnotationBuilder(infoForward, varInsertionBeforeExon2,
				new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annoInsertionBeforeExon2.getTranscript().getAccession());
		Assert.assertEquals(2, annoInsertionBeforeExon2.getAnnoLoc().getRank());
		Assert.assertEquals("691-1_691insA", annoInsertionBeforeExon2.getCDSNTChange().toHGVSString());
		Assert.assertEquals("?", annoInsertionBeforeExon2.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.SPLICE_ACCEPTOR_VARIANT,
				VariantEffect.CODING_TRANSCRIPT_INTRON_VARIANT), annoInsertionBeforeExon2.getEffects());

		GenomeVariant varInsertionBeforeExon3 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6645978,
				PositionType.ZERO_BASED), "", "A");
		Annotation annoInsertionBeforeExon3 = new InsertionAnnotationBuilder(infoForward, varInsertionBeforeExon3,
				new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annoInsertionBeforeExon3.getTranscript().getAccession());
		Assert.assertEquals(3, annoInsertionBeforeExon3.getAnnoLoc().getRank());
		Assert.assertEquals("933-1_933insA", annoInsertionBeforeExon3.getCDSNTChange().toHGVSString());
		Assert.assertEquals("?", annoInsertionBeforeExon3.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.SPLICE_ACCEPTOR_VARIANT,
				VariantEffect.CODING_TRANSCRIPT_INTRON_VARIANT), annoInsertionBeforeExon3.getEffects());
	}

	@Test
	public void testRealWorldCase_uc010slx_2() throws InvalidGenomeChange {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc010slx.2	chr12	+	49208214	49219026	49208330	49218557	5	49208214,49217126,49217463,49218040,49218451,	49208336,49217249,49217586,49218156,49219026,	F8VV14	uc010slx.2");
		this.builderForward
				.setSequence("agaaaggggcgggaggggtactgacccgaggagcggtcccgcgactctgtactccctgctgcccagtcccggccaggacgctgcccggcttagctggggcgcccctcgagaccaggatggagggttcagccgactcctacaccagccgcccatctctggactcagacgtctccctggaggaggaccgggagagtgcccggcgtgaagtagagagccaggctcagcagcagctcgaaagggccaagcacaaacctgtggcatttgcggtgaggaccaatgtcagctactgtggcgtactggatgaggagtgcccagtccagggctctggagtcaactttgaggccaaagattttctgcacattaaagagaagtacagcaatgactggtggatcgggcggctagtgaaagagggcggggacatcgccttcatccccagcccccagcgcctggagagcatccggctcaaacaggagcagaaggccaggagatctgggaacccttccagcctgagtgacattggcaaccgacgctcccctccgccatctctaggtagcctccccccaacccacccattttgggtaggtgggtagctaagacacagggaaagaggggatcctgcccaagtgaactgtgtgggaagggtttgtggagggattggaaggggtggggagaagccagtatctcccctggggacggggataccatgcccccagggacctttcccccttcaccaactttaatcttgtatttccttttccaaaaagccaagcagaagcaaaagcaggtgagtcaaggaagggacctgggctggggggatcatggcttatggctctggggacagtgttctaggcagtcattgttggagggcagaacagagaggggagggagcccagaacctctggatctgccctgacgccaaccaggcatgagacaggcaccagggccatggtttctactgacctcatgtccattctgcaggcggaacatgttcccccatatgacgtggtgccctccatgcggcctgtggtgctggtgggaccctctctgaaaggttatgag"
						.toUpperCase());
		this.builderForward.setGeneSymbol("CACNB3");
		this.infoForward = builderForward.build();
		// no RefSeq

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 12, 49218811,
				PositionType.ZERO_BASED), "", "T");
		Annotation annotation1 = new InsertionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(4, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("*255dup", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.THREE_PRIME_UTR_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc010vsd_2() throws InvalidGenomeChange {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc010vsd.2	chr17	-	4534213	4545230	4534894	4544946	14	4534213,4535183,4535475,4536155,4536440,4536708,4539053,4540409,4541511,4541875,4542144,4542345,4542724,4544811,	4535074,4535351,4535576,4536277,4536610,4536795,4539263,4540553,4541672,4541979,4542267,4542427,4542809,4545230,	B7ZA11	uc010vsd.2");
		this.builderForward
				.setSequence("ctgcgtgttttcggtccaaatccttttctttttctccctcccgtcaagatagtggtttccactccctgctctcgccaggacaccgccttttggactggggctgaattctgccccttgaagctctgctccttggagctgggggccccagcggtaggcggagttgattggagacctgccacccacattccgaccccaagcgacctccgagagggcggggtctcaggctgggttatttagctcgtccacccttctccaccagaaggagcgaaacatctttgagcaagatgggtctctaccgcatccgcgtgtccactggggcctcgctctatgccggttccaacaaccaggtgcagctgtggctggtcggccagcacggggaggcggcgctcgggaagcgactgtggcccgcacggggcaagggccccggagccggggacgaggtcaggttcccttgttaccgctgggtggagggcaacggcgtcctgagcctgcctgaaggcaccggccgcactgtgggcgaggaccctcagggcctgttccagaaacaccgggaagaagagctggaagagagaaggaagttgtaccggtggggaaactggaaggacgggttaattctgaatatggctggggccaaactatatgacctccctgtggatgagcgatttctggaagacaagagagttgactttgaggtttcgctggccaaggggctggccgacctcgctatcaaagactctctaaatgttctgacttgctggaaggatctagatgacttcaaccggattttctggtgtggtcagagcaagctggctgagcgcgtgcgggactcctggaaggaagatgccttatttgggtaccagtttcttaatggcgccaaccccgtggtgctgaggcgctctgctcaccttcctgctcgcctagtgttccctccaggcatggaggaactgcaggcccagctggagaaggagctggagggaggcacactgttcgaagctgacttctccctgctggatgggatcaaggccaacgtcattctctgtagccagcagcacctggctgcccctctagtcatgctgaaattgcagcctgatgggaaactcttgcccatggtcatccagctccagctgccccgcacaggatccccaccacctccccttttcttgcctacggatcccccaatggcctggcttctggccaaatgctgggtgcgcagctctgacttccagctccatgagctgcagtctcatcttctgaggggacacttgatggctgaggtcattgttgtggccaccatgaggtgcctgccgtcgatacatcctatcttcaagcttataattccccacctgcgatacaccctggaaattaacgtccgggccaggactgggctggtctctgacatgggaattttcgaccagataatgagcactggtgggggaggccacgtgcagctgctcaagcaagctggagccttcctaacctacagctccttctgtccccctgatgacttggccgaccgggggctcctgggagtgaagtcttccttctatgcccaagatgcgctgcggctctgggaaatcatctatcggtatgtggaaggaatcgtgagtctccactataagacagacgtggctgtgaaagacgacccagagctgcagacctggtgtcgagagatcactgaaatcgggctgcaaggggcccaggaccgagggtttcctgtctctttacaggctcgggaccaggtttgccactttgtcaccatgtgtatcttcacctgcaccggccaacacgcctctgtgcacctgggccagctggactggtactcttgggtgcctaatgcaccctgcacgatgcggctgcccccgccaaccaccaaggatgcaacgctggagacagtgatggcgacactgcccaacttccaccaggcttctctccagatgtccatcacttggcagctgggcagacgccagcccgttatggtggctgtgggccagcatgaggaggagtatttttcgggccctgagcctaaggctgtgctgaagaagttcagggaggagctggctgccctggataaggaaattgagatccggaatgcaaagctggacatgccctacgagtacctgcggcccagcgtggtggaaaacagtgtggccatctaagcgtcgccaccctttggttatttcagcccccatcacccaagccacaagctgaccccttcgtggttatagccctgccctcccaagtcccaccctcttcccatgtcccaccctccctagaggggcaccttttcatggtctctgcacccagtgaacacattttactctagaggcatcacctgggaccttactcctctttccttccttcctcctttcctatcttccttcctctctctcttcctctttcttcattcagatctatatggcaaatagccacaattatataaatcatttcaagactagaatagggggatataatacatattactccacaccttttatgaatcaaatatgatttttttgttgttgttaagacagagtctcactttgacacccaggctggagtgcagtggtgccatcaccacggctcactgcagcctcagcgtcctgggctcaaatgatcctcccacctcagcctcctgagtagctgggactacaggctcatgccatcatgcccagctaatatttttttattttcgtggagacggggcctcactatgttgcctaggctggaaataggattttgaacccaaattgagtttaacaataataaaaagttgttttacgctaaagatggaaaagaactaggactgaactattttaaataaaatattggcaaaagaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("ALOX15");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 17, 4544982,
				PositionType.ZERO_BASED), "", "AAG");
		Annotation annotation1 = new InsertionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(0, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("-37_-36insCTT", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FIVE_PRIME_UTR_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc004aus_1() throws InvalidGenomeChange {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc004aus.1	chr9	+	97317350	97330409	97317350	97317350	3	97317350,97321250,97329671,	97317592,97321434,97330409,		uc004aus.1");
		this.builderForward
				.setSequence("atgcgcataacctcaggatataaataatgctgaagcagagttacgttttttttgttgttgttttttttgtttttgtttttttaggtttccgtgtgtttctattgagctgctcagtgcccggcttagaagaccaggaaaaggagtcacaggtcgtatgctggaggcttgagccgcggcaccgtggcgcggctcgcctcgctgcggttggtggtggcggtggacattgcagcgcggctggaggggtgagatattcctgcacatcctctggtgaccccagaatgagggggactcgctggtgaattgcctcgggcttcacgtccagtacaggctgggtccccgtggtcgccaagcctcctgcctgctcaatgatgtaggccacgggattgcattcatacaggagccggagctgtggaggaacagaggcaggacaaattcaccaagagcctagcaacatgaagagagatgccaggaagaagagagaagccaggaaacaagccaaccgcacaatccccacatcagagcaggagaagatgggggcctgctggcagagctggggcttggctgtggtcactctgaacctgctctttggtgttttcatgagtggtgggaagaatagggaccatatggagcccacacaggaagctctagcagtaacacagcaagcaggaagacaattctaaggaagcagcccatagtcttctttcttttcctgtgcatcttccactgtcagtgaggctcctcatttatggtgaacccaactgtgtgtatctcccaagttctcacccgcagattaatgttttcaggaagataggccatcaacagtgagaggaagaagttacattgtcgtatgagggatgcattttaaccattaatttgtggtacaggctgggcgcagtggcttatgcatgtaatcccagcactttgggaggccgaggtgggtggatcacgaggtcaggagatcgagaccatcctggctaacatggtgaaaccccgtctttactaaatatacaaaaaattggccgggcgtggtggtgggcacctgtagtcccagctactcggggggctgaggcaggagaatggtgtgaacccgggaggcagagcttgcagtgagccgagatcgcgccactgcactccagcctggatgacagagcaagactccatctc"
						.toUpperCase());
		this.builderForward.setGeneSymbol("BC042913");
		this.infoForward = builderForward.build();
		// RefSeq XR_242648.1

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 9, 97329737,
				PositionType.ZERO_BASED), "", "GA");
		Annotation annotation1 = new InsertionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(2, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("492_493insGA", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals(null, annotation1.getProteinChange());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.NON_CODING_TRANSCRIPT_EXON_VARIANT),
				annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc010arh_1() throws InvalidGenomeChange {
		this.builderForward = TranscriptModelFactory.parseKnownGenesLine(refDict,
				"uc010arh.1	chr14	-	73075741	73079803	73075741	73075741	1	73075741,	73079803,		uc010arh.1");
		this.builderForward
				.setSequence("tatgaatattgatgatgagaacaggaatgcccatgtgcatgcgcgtgcgtgcacgcacgcacatacacacacacacacacacacacacacacacacacacacgccctacttaatgaaccaaagggaaactttgatgttgctccaggacaagagggagagccaggggctgccaggcaaggtgcatctttcctgaagtcttctccagtcctgctcctccagctctcagcctctgcacagccaccagggttgcaaagggagccttgtgcttggaaagctactggctccgcgacctctctccttctcttcccttcgttcacctatgcttcttttcatgaaagatatttggcaatagttgggtacttcaaaagtctcaattctgctatcaaagggatgcaaagcttttatggctagaatggggtttgtggtttatacggacttggtttgcccttccatgtctactccccactgagaccctgattctcctcctgccctctcccttccctgcactgtttcctcccttccctctgccgtgatagatgctaaggagtggggtggaggtgggaagtgggaagcaacagtggcgtataggaaaaagaaaatataccccgtgcacattttcaacatgtagttgaagaagcctaaattaggtactagaaaaaaaaaaaggacagaaacactgcctgatatgtgagcaagagcatgaaaatatagaatgattatctcagagcagaggaggtggggagcaggtggctggagagaggcggggtaggtaggcagggcgtccctggctcaccaatgcgtctggtccacatggcatttgggaaagcagatgggtcatgcttttgcaatgaatggtcagatgcttagggggcatttgtgctcctctggccaggaaagggaacagagtccatccaagctgccccccactcatctgccatggttgtccaccctgggggtttttcctctgaaggaaaatgaacccatcttttgcctgccatgaatcattgcagggcaggctgttactggctaaattaaaacccttggcagggcatcgactgtgtgcgaggcaatgctctaggtcctgtaggggctggaaaataaaccgcatgctggcctagccttcaagttgcttttcagccatgaaattaagctctgccaagcaatcgtgattgtaggtcaagtatcgcgccatcacggaaggtgtaagtatatgtaggtttctctgttgaggatgcgtgttcctcagtagaagacaagcaggtggaggcatccagtgatttctaccctgtggaggctgaggggtcgggggaagaaaacagatgacctcagcctagttgctttaatctgcttttccaagcactggatgcccttggatgacagcatcctcagcattaaaactggtgaactgatgaagtcacctggcctggagtgtgttgggcagccagtgtccccagagctgcttgtgggtttctggggtggaaggcagggaggtgcaactggcagggcctgatcagaggcagaaaatgacccccacagtggtcttttccctgctagagaaagcagagagcgggactgggggggtgggggcttcaagtacagattgggcacactccaccagaccccagcaaggtcagctgccccacgcctgtatctggcactgctggtgtgtgcagggatgaaacccagcatcagagaggttttcagcaaaccttccatgctcacctttggccaggtgcttgtcagatcctagcttcgtgctggcttagactctcagttgtttgtgttgacagcattaggaataaaccgtttgtttcatctttccttttctccacacgtgtcacagccagatttcagctttgagttattccctgaagaagccacaccaaccttgctttcaagcaaaatgcctgggcttgggggaaggtgtgtatctgtccatgtgtggatgttggctcagagctatagcttctctgtggggggtggcccaagggaaggctcctctggggccctggatggcacatgactcccagtgaggagaattctggtgatctctgtggaggtagtcagggacacaaggcttggctgtgagtctggttttaaagtgcgtgacagcctgaaaagcatgcaggggtttggtccactcacctacttgaaagcctgtgggcaacgttcttttgagccaagacttctctgaatggccctgctggtggaaggggtgaggcaaaggcctctgacttggaccctttccacaccagactggcagcacttcccccaggcagccagtggtgggccctgagccctcaggtccccagctccttgagggatgaacctgggagcccaagagccagtggctgagctctgagaaggctccatctcccacctgcccttgagcgcgctctcaggctgagaacacggtctcatcaggcgccttcctggcctgatgctgtgctgtctacgtcacacggtcgattcacaaaagccagaactagacctcaaccaggtcatctccccgttgccaagtgggttcagggtgagggcaatttgtaagctcaatttctctgacagccaagacatggagcatctctgctaagaagccaaaagaaattggttttcttcttctcattgctgaagcccctctgtgtctcttctcagggacaggctggtccagtggctttggtgagggcgcctccatttgtgaacgctgggattcctctgggtgggctttggcaggtggctcctatggtaggaaggatgaccaggtccctgggaatgagctgcctacctgctgctccacgaggaagctcaggcctgcacaggctccaccaggccttggatgccctctagttgagtcagagaccctggaaacacactgagatctccaattgctgcctccattgatgtctctagacctgcagatacgaagcaaacctgggattgcttcttccaggtatgggcaccagagagggaagccactgcaacattttatccccatcattccaaaatgcttgcttgtctcttttacctcacactcacaactccttctgagactctcagtcataaaggaatgaccaagagagtgggtctccagtgagagaaatgcctatgaaagagggtttccctttttgctcttttgaacaccctccccactgatccttgggacccaacgccgcattgcctcttgcagatgaggttttgccttgggctgctttggtacttcagaccaggactgagtctgacacagctttcatgaggttacagaaaagggctacagatttgggaagctgtgtgtaatggtcttgagacaatatctccatttggcccaccctggcttctctaaaaagcaacgacagcaacagacaaacaaaaagctcccacctcccaccccgttagctgtcctcctccttcactgtgatgtggttgcggtctctgtaggtgtgtgtgccacccttgtcctctgtcctctggggatgtgcccttcccacgtgtgtcaggttcccactctttcgtggttcctaacgtgaagtgctgtgatgtttctgccctgcctaaggaacgtatcaagctctctcagtgtttcagtgttggagattgaggctgtgccacatcttctgccatcctaaggggacatgatggttctgtgattcccagagagctggcagattgtgacaatctccaggagaacctacagattggaagcagcccacacctgatgtggactcctgacccgggactcactcttcattcagaagactggtggcccacgtgccaggaccaccccacctctcttgctgccttttctcctgtcctgatggggttctgggagggagacctgtcgctgatgagatgaagaatgtggggatcgagcagccttcttctttgggacccctcgatatcccatggaatgctcgcacgttctcaaagactgagtcacaagcccctaccccttccttgctgtggttagtatcttgttctgtgattggttagcaatgttgactacccacgtagtgaatcttttgtctgcaatttagagaatgtgtaaacaaataaaaggctttaaaactc"
						.toUpperCase());
		this.builderForward.setGeneSymbol("AK024141");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 14, 73079293,
				PositionType.ZERO_BASED), "", "AA");
		Annotation annotation1 = new InsertionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(0, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("511_512dup", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals(null, annotation1.getProteinChange());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.NON_CODING_TRANSCRIPT_EXON_VARIANT),
				annotation1.getEffects());
	}

	//
	// Various Duplication Variants
	//

	/**
	 * This is the test for the in-frame duplication of a single triplicate / one amino acids '+' strand
	 *
	 * Mutalyzer: NM_001005495(OR2T3_v001):c.769_771dup NM_001005495(OR2T3_i001):p.(Phe257dup)
	 */
	@Test
	public void testRealWorldCase_uc001iel_1_first() throws InvalidGenomeChange {
		this.builderForward = TranscriptModelFactory.parseKnownGenesLine(refDict,
				"uc001iel.1	chr1	+	248636651	248637608	248636651	248637608	1	248636651,	248637608,	Q8NH03	uc001iel.1");
		this.builderForward
				.setSequence("atgtgctcagggaatcagacttctcagaatcaaacagcaagcactgatttcaccctcacgggactctttgctgagagcaagcatgctgccctcctctacaccgtgaccttccttcttttcttgatggccctcactgggaatgccctcctcatcctcctcatccactcagagccccgcctccacacccccatgtacttcttcatcagccagctcgcgctcatggatctcatgtacctatgcgtgactgtgcccaagatgcttgtgggccaggtcactggagatgataccatttccccgtcaggctgtgggatccagatgttcttctacctgaccctggctggagctgaggttttcctcctggctgccatggcctatgaccgatatgctgctgtttgcagacctctccattacccactgctgatgaaccagagggtgtgccagctcctggtgtcagcctgctgggttttgggaatggttgatggtttgttgctcacccccattaccatgagcttccccttttgccagtctaggaaaatcctgagttttttctgtgagactcctgccctgctgaagctctcctgctctgacgtctccctctataagacgctcatgtacctgtgctgcatcctcatgcttctcgcccccatcatggtcatctccagctcatacaccctcatcctgcatctcatccacaggatgaattctgccgccggccacaggaaggccttggccacctgctcctcccacatgatcatagtgctgctgctcttcggtgcttccttctacacctacatgctcccgagttcctaccacacagctgagcaggacatgatggtgtctgccttttacaccatcttcactcctgtgctgaaccccctcatttacagtctccgcaacaaagatgtcaccagggctctgaggagcatgatgcagtcaagaatgaaccaagaaaagtag"
						.toUpperCase());
		this.builderForward.setGeneSymbol("OR2T3");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 248637422,
				PositionType.ZERO_BASED), "", "TTC");
		Annotation annotation1 = new InsertionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(0, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("769_771dup", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Phe257dup)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(
				ImmutableSortedSet.of(VariantEffect.INFRAME_INSERTION, VariantEffect.DIRECT_TANDEM_DUPLICATION),
				annotation1.getEffects());
	}

	/**
	 * annovar: FRG1:uc003izs.3:exon6:c.439_440insA:p.M147fs, chr4:190878559->A FRG1 is on the "+" strand
	 *
	 * Jannovar says: FRG1(uc003izs.3:exon6:c.438dupA:p.M147fs) expected <...c003izs.3:exon6:c.43[9]dupA:p.M147fs)> but
	 * was: <...c003izs.3:exon6:c.43[8]dupA:p.M147fs)> is uc003izs.3 NM_004477.2
	 *
	 * Mutalyzer says NM_004477.2(FRG1_v001):c.439dup NM_004477.2(FRG1_i001):p.(Met147Asnfs*8)
	 *
	 * Raw variant 1: duplication from 630 to 630 GAACCAGTCTTTCAAAATGGGAAAA - TGGCTTTGTTGGCCTCAAATAGCTG
	 * GAACCAGTCTTTCAAAATGGGAAAA A TGGCTTTGTTGGCCTCAAATAGCTG
	 *
	 * Thus, 439 and not 438 is the correct number for the duplicated nucleotide. Jannovar lists refvarstart as 630.
	 * This is the last "A" of a polyA tract in the gene (see genbank L76159.1). Jannovar lists refcdsstart as 192. This
	 * is the position of the start of the start codon in FRG1 (L76159.1). <...c003izs.3:exon6:c.43[9]dupA:p.M147fs)>
	 * but was: <...c003izs.3:exon6:c.43[8]dupA:p.M147fs)>
	 */
	@Test
	public void testRealWorldCase_uc003izs_3_first() throws InvalidGenomeChange {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc003izs.3	chr4	+	190861973	190884359	190862164	190884284	9	190861973,190864356,190873316,190874222,190876191,190878552,190881902,190882976,190884247,	190862226,190864427,190873442,190874280,190876306,190878657,190881994,190883087,190884359,	Q14331	uc003izs.3");
		this.builderForward
				.setSequence("gaaacccggaagtggaactctgagccattcagcgtttgggtgaagacggaggcgggttctacagagacgtaggctgtcagggagtgtttatttcgcgtccgcttctgtttctccgcgcccctgtgctgccccgactcacatactcgtccagaaccggcctcagcctctccgcgcagaagtttcccggagccatggccgagtactcctacgtgaagtctaccaagctcgtgctcaagggaaccaagacgaagagtaagaagaaaaagagcaaagataagaaaagaaaaagagaagaagatgaagaaacccagcttgatattgttggaatctggtggacagtaacaaactttggtgaaatttcaggaaccatagccattgaaatggataagggaacctatatacatgcactcgacaatggtctttttaccctgggagctccacacaaagaagttgatgagggccctagtcctccagagcagtttacggctgtcaaattatctgattccagaatcgccctgaagtctggctatggaaaatatcttggtataaattcagatggacttgttgttgggcgttcagatgcaattggaccaagagaacaatgggaaccagtctttcaaaatgggaaaatggctttgttggcctcaaatagctgctttattagatgcaatgaagcaggggacatagaagcaaaaagtaaaacagcaggagaagaagaaatgatcaagattagatcctgtgctgaaagagaaaccaagaaaaaagatgacattccagaagaagacaaaggaaatgtaaaacaatgtgaaatcaattatgtaaagaaatttcagagcttccaagaccacaaacttaaaataagtaaagaagacagtaaaattcttaaaaaggctcggaaagatggatttttgcatgagacgcttctggacaggagagccaaattgaaagccgacagatactgcaagtgactgggatttttgtttctgccttatctttctgtgtttttttctgaataaaatattcagaggaaatgcttttacagaaaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("FRG1");
		this.infoForward = builderForward.build();
		// RefSeq NM_004477.2

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 4, 190878559,
				PositionType.ZERO_BASED), "", "A");
		Annotation annotation1 = new InsertionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(5, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("439dup", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Met147Asnfs*8)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FRAMESHIFT_VARIANT), annotation1.getEffects());
	}

	/**
	 * This is the test for the in-frame duplication of a single triplicate / one amino acids '+' strand
	 */
	@Test
	public void testRealWorldCase_uc010naq_2_first() throws InvalidGenomeChange {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc010naq.2	chr9	+	137967088	137969237	137967582	137969065	2	137967088,137968657,	137967648,137969237,	Q6IMJ6	uc010naq.2");
		this.builderForward
				.setSequence("atcctgtcccagcctgcttccccgccggccgccgccctcctccccgggagagagcgaggcgcgcgggtccctctgcgccacccccgcccccgccccttccgagcaaacttttggcacccaccgcagcccagcgcgcgttcgtgctccgcagggcgcgcctctctccgccaatgccaggcgcgcgggggagccattaggaggcgaggagagaggagggcgcagctcccgcccagcccagccctgcccagccctgcccggaggcagacgcgccggaaccgggacgcgataaatatgcagagcggaggcttcgcgcagcagagcccgcgcgccgcccgctccgggtgctgaatccaggcgtggggacacgagccaggcgccgccgccggagccagcggagccggggccagagccggagcgcgtccgcgtccacgcagccgccggccggccagcacccagggccctgcatgccaggtcgttggaggtggcagcgagacatgcacccggcccggaagctcctcagcctcctcttcctcatcctgatgggcactgaactcactcaaaataaaagagaaaacaaagcagagaagatgggagggccagagagcgagaggaagaccacaggagagaagacactgaacgagcttcccttgttttgcctggaagcccacgctggctccctggctctgcccaggatgtgcagtccaaatcccaatccagcagtggggttatgtcgtcccgcttaccctcagagcccttctcctggtgctgcccagacgatcagccagtccctcctggagaggttctgcatggcctctaggagaggttttcttggccccaggaaggcctggtggagggtggtggttgtgcactgttgctggacagatgcattcattcatgtgcacacacacacacacacatgcacacacaggggagcagatacctgcagagaagagccaaccaggtcctgattagtggcaagctgccccacaaagggctatgcctgtgtcttattgagacaccttggcaaagagatggctgattctgggtggtcctggacatggccgcacccaagggccctccaagccttaatggcaccctgaagcctccatgcccaggccaaaagatgcttttcctccctaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("OLFM1");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 9, 137968918,
				PositionType.ZERO_BASED), "", "AGA");
		Annotation annotation1 = new InsertionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(1, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("325_327dup", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Arg109dup)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(
				ImmutableSortedSet.of(VariantEffect.INFRAME_INSERTION, VariantEffect.DIRECT_TANDEM_DUPLICATION),
				annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc001iel_1_second() throws InvalidGenomeChange {
		this.builderForward = TranscriptModelFactory.parseKnownGenesLine(refDict,
				"uc001iel.1	chr1	+	248636651	248637608	248636651	248637608	1	248636651,	248637608,	Q8NH03	uc001iel.1");
		this.builderForward
				.setSequence("atgtgctcagggaatcagacttctcagaatcaaacagcaagcactgatttcaccctcacgggactctttgctgagagcaagcatgctgccctcctctacaccgtgaccttccttcttttcttgatggccctcactgggaatgccctcctcatcctcctcatccactcagagccccgcctccacacccccatgtacttcttcatcagccagctcgcgctcatggatctcatgtacctatgcgtgactgtgcccaagatgcttgtgggccaggtcactggagatgataccatttccccgtcaggctgtgggatccagatgttcttctacctgaccctggctggagctgaggttttcctcctggctgccatggcctatgaccgatatgctgctgtttgcagacctctccattacccactgctgatgaaccagagggtgtgccagctcctggtgtcagcctgctgggttttgggaatggttgatggtttgttgctcacccccattaccatgagcttccccttttgccagtctaggaaaatcctgagttttttctgtgagactcctgccctgctgaagctctcctgctctgacgtctccctctataagacgctcatgtacctgtgctgcatcctcatgcttctcgcccccatcatggtcatctccagctcatacaccctcatcctgcatctcatccacaggatgaattctgccgccggccacaggaaggccttggccacctgctcctcccacatgatcatagtgctgctgctcttcggtgcttccttctacacctacatgctcccgagttcctaccacacagctgagcaggacatgatggtgtctgccttttacaccatcttcactcctgtgctgaaccccctcatttacagtctccgcaacaaagatgtcaccagggctctgaggagcatgatgcagtcaagaatgaaccaagaaaagtag"
						.toUpperCase());
		this.builderForward.setGeneSymbol("OR2T3");
		this.infoForward = builderForward.build();
		// RefSeq NM_001005495.1

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 248637607,
				PositionType.ZERO_BASED), "", "A");
		Annotation annotation1 = new InsertionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(0, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("956dup", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.SYNONYMOUS_VARIANT), annotation1.getEffects());
	}

	/**
	 * This is the test for the in-frame duplication of six nuc.acids / two amino acids '+' strand
	 *
	 * Mutalyzer: NM_001005495.1(OR2T3_v001):c.766_771dup NM_001005495.1(OR2T3_i001):p.(Leu256_Phe257dup)
	 *
	 *
	 * <...6_771dupCTCTTC:p.L25[6_F257]dup)> but was: <...6_771dupCTCTTC:p.L25[4_F256]dup)>
	 */
	@Test
	public void testRealWorldCase_uc001iel_1_third() throws InvalidGenomeChange {
		this.builderForward = TranscriptModelFactory.parseKnownGenesLine(refDict,
				"uc001iel.1	chr1	+	248636651	248637608	248636651	248637608	1	248636651,	248637608,	Q8NH03	uc001iel.1");
		this.builderForward
				.setSequence("atgtgctcagggaatcagacttctcagaatcaaacagcaagcactgatttcaccctcacgggactctttgctgagagcaagcatgctgccctcctctacaccgtgaccttccttcttttcttgatggccctcactgggaatgccctcctcatcctcctcatccactcagagccccgcctccacacccccatgtacttcttcatcagccagctcgcgctcatggatctcatgtacctatgcgtgactgtgcccaagatgcttgtgggccaggtcactggagatgataccatttccccgtcaggctgtgggatccagatgttcttctacctgaccctggctggagctgaggttttcctcctggctgccatggcctatgaccgatatgctgctgtttgcagacctctccattacccactgctgatgaaccagagggtgtgccagctcctggtgtcagcctgctgggttttgggaatggttgatggtttgttgctcacccccattaccatgagcttccccttttgccagtctaggaaaatcctgagttttttctgtgagactcctgccctgctgaagctctcctgctctgacgtctccctctataagacgctcatgtacctgtgctgcatcctcatgcttctcgcccccatcatggtcatctccagctcatacaccctcatcctgcatctcatccacaggatgaattctgccgccggccacaggaaggccttggccacctgctcctcccacatgatcatagtgctgctgctcttcggtgcttccttctacacctacatgctcccgagttcctaccacacagctgagcaggacatgatggtgtctgccttttacaccatcttcactcctgtgctgaaccccctcatttacagtctccgcaacaaagatgtcaccagggctctgaggagcatgatgcagtcaagaatgaaccaagaaaagtag"
						.toUpperCase());
		this.builderForward.setGeneSymbol("OR2T3");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 248637422,
				PositionType.ZERO_BASED), "", "CTCTTC");
		Annotation annotation1 = new InsertionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(0, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("766_771dup", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Leu256_Phe257dup)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(
				ImmutableSortedSet.of(VariantEffect.INFRAME_INSERTION, VariantEffect.DIRECT_TANDEM_DUPLICATION),
				annotation1.getEffects());
	}

	/**
	 * This is the test for the in-frame duplication of 12 nuc.acids / tree amino acids '+' strand
	 */
	@Test
	public void testRealWorldCase_uc001iel_1_fourth() throws InvalidGenomeChange {
		this.builderForward = TranscriptModelFactory.parseKnownGenesLine(refDict,
				"uc001iel.1	chr1	+	248636651	248637608	248636651	248637608	1	248636651,	248637608,	Q8NH03	uc001iel.1");
		this.builderForward
				.setSequence("atgtgctcagggaatcagacttctcagaatcaaacagcaagcactgatttcaccctcacgggactctttgctgagagcaagcatgctgccctcctctacaccgtgaccttccttcttttcttgatggccctcactgggaatgccctcctcatcctcctcatccactcagagccccgcctccacacccccatgtacttcttcatcagccagctcgcgctcatggatctcatgtacctatgcgtgactgtgcccaagatgcttgtgggccaggtcactggagatgataccatttccccgtcaggctgtgggatccagatgttcttctacctgaccctggctggagctgaggttttcctcctggctgccatggcctatgaccgatatgctgctgtttgcagacctctccattacccactgctgatgaaccagagggtgtgccagctcctggtgtcagcctgctgggttttgggaatggttgatggtttgttgctcacccccattaccatgagcttccccttttgccagtctaggaaaatcctgagttttttctgtgagactcctgccctgctgaagctctcctgctctgacgtctccctctataagacgctcatgtacctgtgctgcatcctcatgcttctcgcccccatcatggtcatctccagctcatacaccctcatcctgcatctcatccacaggatgaattctgccgccggccacaggaaggccttggccacctgctcctcccacatgatcatagtgctgctgctcttcggtgcttccttctacacctacatgctcccgagttcctaccacacagctgagcaggacatgatggtgtctgccttttacaccatcttcactcctgtgctgaaccccctcatttacagtctccgcaacaaagatgtcaccagggctctgaggagcatgatgcagtcaagaatgaaccaagaaaagtag"
						.toUpperCase());
		this.builderForward.setGeneSymbol("OR2T3");
		this.infoForward = builderForward.build();
		// RefSeq NM_001005495.1

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 248637422,
				PositionType.ZERO_BASED), "", "CTGCTGCTCTTC");
		Annotation annotation1 = new InsertionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(0, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("760_771dup", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Leu254_Phe257dup)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(
				ImmutableSortedSet.of(VariantEffect.INFRAME_INSERTION, VariantEffect.DIRECT_TANDEM_DUPLICATION),
				annotation1.getEffects());
	}

	/**
	 * This is the test for the in-frame duplication of a single triplicate / one amino acids '-' strand
	 *
	 * Mutalyzer: NM_022149.4(MAGEF1_v001):c.424_426dup NM_022149.4(MAGEF1_i001):p.(Thr142dup)
	 */
	@Test
	public void testRealWorldCase_uc003fpa_3_first() throws InvalidGenomeChange {
		this.builderForward = TranscriptModelFactory.parseKnownGenesLine(refDict,
				"uc003fpa.3	chr3	-	184428154	184429836	184428685	184429609	1	184428154,	184429836,	Q9HAY2	uc003fpa.3");
		this.builderForward
				.setSequence("gcgggcgcggactgaggctgcgcgccgcaggttccggctgctggcggcgttgcggccgcaggtttgactcccgtgcggtgcggcccagcagccacaaagctcccgctgccattgctccttgtactcccgccgtcactgccgctgtccaacccctcccccggggcttgcgcggcggctcccacacccctcggcccgtgtacgcgctctgcacctgcctgcccgaaaacatgttgcagacaccagagagcagggggctcccggtcccgcaggccgagggggagaaggatggcggccatgatggtgagacccgggccccgaccgcctcgcaggagcgccccaaggaggagcttggcgccgggagggaggagggggctgcggagcccgccctcacccggaaaggcgcgagggccttggcggccaaagccttggcaaggcgcagggcctaccgccggctgaatcggacggtggcggagttggtgcagttcctcctggtgaaagacaagaagaagagtcccatcacacgctcggagatggtgaaatacgttattggagacttgaagattctgttcccggacatcatcgcaagggccgcagagcatctgcggtatgtctttggttttgagctgaaacagtttgaccgcaagcaccacacttacatcctgatcaacaaactaaaacctctggaggaggaggaggaggaggatctgggaggagatggccccagattgggtctgttaatgatgatcctgggccttatctatatgagaggtaatagcgccagggaggcccaggtctgggagatgctgcgtcggttgggggtgcaaccctcaaagtatcatttcctctttgggtatccgaagaggcttattatggaagattttgtgcagcagcgatatctcagttacaggcgggtgcctcacaccaatccaccagaatatgaattctcttggggtccccgaagcaacctggaaatcagcaagatggaagtcctggggttcgtggccaaactgcataagaaggaaccgcagcactggccagtgcagtaccgtgaggccctagcagacgaggccgacagggccagagccaaggccagagctgaagccagtatgagggccagggccagtgctagggccggcatccacctctggtgagggttggtgaaaagttggccagtgggtccccgtgaggacgaactactgtcctgagtcataagtaatatgggtggggcgagggtcttatttctgtagaaatcgtgtgactttaaggatttagattttgtatcttatgttttgtaacatttaataattactgttaaaatgctgtttgtaaatgagattggtctactttttcctgtaggattttattgtagagttttgctggttttgtaaaatggatggaagaactttgtatttatactgtgattttgaacagattatgcaacattggaaggaaggctgtactttgatggtttgaaggaactcagcagtatgatgatctggttccaggggaaaaaaatagctggttggtgtctagccccccaacacttttgtctgttgtgtataaaagaagaaagactggcatgtaccttcatttgcttagctatttgagtatctagagaaaaattaaaatgcaatgagttagcagtataccctggcacacttaataaattaaacatttgtggaaaaaaaaaaaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("MAGEF1");
		this.infoForward = builderForward.build();
		// RefSeq NM_022149.4

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 3, 184429186,
				PositionType.ZERO_BASED), "", "AGT");
		Annotation annotation1 = new InsertionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(0, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("424_426dup", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Thr142dup)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(
				ImmutableSortedSet.of(VariantEffect.INFRAME_INSERTION, VariantEffect.DIRECT_TANDEM_DUPLICATION),
				annotation1.getEffects());
	}

	/**
	 *
	 * This is the test for the in-frame duplication of 6 nuc.acids / two amino acids '-' strand
	 *
	 * mutalzyer: NM_022149.4(MAGEF1_v001):c.439_444dup NM_022149.4(MAGEF1_i001):p.(Asn147_Lys148dup)
	 */
	@Test
	public void testRealWorldCase_uc003fpa_3_second() throws InvalidGenomeChange {
		this.builderForward = TranscriptModelFactory.parseKnownGenesLine(refDict,
				"uc003fpa.3	chr3	-	184428154	184429836	184428685	184429609	1	184428154,	184429836,	Q9HAY2	uc003fpa.3");
		this.builderForward
				.setSequence("gcgggcgcggactgaggctgcgcgccgcaggttccggctgctggcggcgttgcggccgcaggtttgactcccgtgcggtgcggcccagcagccacaaagctcccgctgccattgctccttgtactcccgccgtcactgccgctgtccaacccctcccccggggcttgcgcggcggctcccacacccctcggcccgtgtacgcgctctgcacctgcctgcccgaaaacatgttgcagacaccagagagcagggggctcccggtcccgcaggccgagggggagaaggatggcggccatgatggtgagacccgggccccgaccgcctcgcaggagcgccccaaggaggagcttggcgccgggagggaggagggggctgcggagcccgccctcacccggaaaggcgcgagggccttggcggccaaagccttggcaaggcgcagggcctaccgccggctgaatcggacggtggcggagttggtgcagttcctcctggtgaaagacaagaagaagagtcccatcacacgctcggagatggtgaaatacgttattggagacttgaagattctgttcccggacatcatcgcaagggccgcagagcatctgcggtatgtctttggttttgagctgaaacagtttgaccgcaagcaccacacttacatcctgatcaacaaactaaaacctctggaggaggaggaggaggaggatctgggaggagatggccccagattgggtctgttaatgatgatcctgggccttatctatatgagaggtaatagcgccagggaggcccaggtctgggagatgctgcgtcggttgggggtgcaaccctcaaagtatcatttcctctttgggtatccgaagaggcttattatggaagattttgtgcagcagcgatatctcagttacaggcgggtgcctcacaccaatccaccagaatatgaattctcttggggtccccgaagcaacctggaaatcagcaagatggaagtcctggggttcgtggccaaactgcataagaaggaaccgcagcactggccagtgcagtaccgtgaggccctagcagacgaggccgacagggccagagccaaggccagagctgaagccagtatgagggccagggccagtgctagggccggcatccacctctggtgagggttggtgaaaagttggccagtgggtccccgtgaggacgaactactgtcctgagtcataagtaatatgggtggggcgagggtcttatttctgtagaaatcgtgtgactttaaggatttagattttgtatcttatgttttgtaacatttaataattactgttaaaatgctgtttgtaaatgagattggtctactttttcctgtaggattttattgtagagttttgctggttttgtaaaatggatggaagaactttgtatttatactgtgattttgaacagattatgcaacattggaaggaaggctgtactttgatggtttgaaggaactcagcagtatgatgatctggttccaggggaaaaaaatagctggttggtgtctagccccccaacacttttgtctgttgtgtataaaagaagaaagactggcatgtaccttcatttgcttagctatttgagtatctagagaaaaattaaaatgcaatgagttagcagtataccctggcacacttaataaattaaacatttgtggaaaaaaaaaaaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("MAGEF1");
		this.infoForward = builderForward.build();
		// RefSeq NM_022149.4

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 3, 184429171,
				PositionType.ZERO_BASED), "", "TTTGTT");
		Annotation annotation1 = new InsertionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(0, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("439_444dup", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Asn147_Lys148dup)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(
				ImmutableSortedSet.of(VariantEffect.INFRAME_INSERTION, VariantEffect.DIRECT_TANDEM_DUPLICATION),
				annotation1.getEffects());
	}

	/**
	 * This is the test for the in-frame duplication of 12 nuc.acids / three amino acids '-' strand
	 */
	@Test
	public void testRealWorldCase_uc003fpa_3_third() throws InvalidGenomeChange {
		this.builderForward = TranscriptModelFactory.parseKnownGenesLine(refDict,
				"uc003fpa.3	chr3	-	184428154	184429836	184428685	184429609	1	184428154,	184429836,	Q9HAY2	uc003fpa.3");
		this.builderForward
				.setSequence("gcgggcgcggactgaggctgcgcgccgcaggttccggctgctggcggcgttgcggccgcaggtttgactcccgtgcggtgcggcccagcagccacaaagctcccgctgccattgctccttgtactcccgccgtcactgccgctgtccaacccctcccccggggcttgcgcggcggctcccacacccctcggcccgtgtacgcgctctgcacctgcctgcccgaaaacatgttgcagacaccagagagcagggggctcccggtcccgcaggccgagggggagaaggatggcggccatgatggtgagacccgggccccgaccgcctcgcaggagcgccccaaggaggagcttggcgccgggagggaggagggggctgcggagcccgccctcacccggaaaggcgcgagggccttggcggccaaagccttggcaaggcgcagggcctaccgccggctgaatcggacggtggcggagttggtgcagttcctcctggtgaaagacaagaagaagagtcccatcacacgctcggagatggtgaaatacgttattggagacttgaagattctgttcccggacatcatcgcaagggccgcagagcatctgcggtatgtctttggttttgagctgaaacagtttgaccgcaagcaccacacttacatcctgatcaacaaactaaaacctctggaggaggaggaggaggaggatctgggaggagatggccccagattgggtctgttaatgatgatcctgggccttatctatatgagaggtaatagcgccagggaggcccaggtctgggagatgctgcgtcggttgggggtgcaaccctcaaagtatcatttcctctttgggtatccgaagaggcttattatggaagattttgtgcagcagcgatatctcagttacaggcgggtgcctcacaccaatccaccagaatatgaattctcttggggtccccgaagcaacctggaaatcagcaagatggaagtcctggggttcgtggccaaactgcataagaaggaaccgcagcactggccagtgcagtaccgtgaggccctagcagacgaggccgacagggccagagccaaggccagagctgaagccagtatgagggccagggccagtgctagggccggcatccacctctggtgagggttggtgaaaagttggccagtgggtccccgtgaggacgaactactgtcctgagtcataagtaatatgggtggggcgagggtcttatttctgtagaaatcgtgtgactttaaggatttagattttgtatcttatgttttgtaacatttaataattactgttaaaatgctgtttgtaaatgagattggtctactttttcctgtaggattttattgtagagttttgctggttttgtaaaatggatggaagaactttgtatttatactgtgattttgaacagattatgcaacattggaaggaaggctgtactttgatggtttgaaggaactcagcagtatgatgatctggttccaggggaaaaaaatagctggttggtgtctagccccccaacacttttgtctgttgtgtataaaagaagaaagactggcatgtaccttcatttgcttagctatttgagtatctagagaaaaattaaaatgcaatgagttagcagtataccctggcacacttaataaattaaacatttgtggaaaaaaaaaaaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("MAGEF1");
		this.infoForward = builderForward.build();
		// RefSeq NM_022149.4

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 3, 184429171,
				PositionType.ZERO_BASED), "", "TTTTAGTTTGTT");
		Annotation annotation1 = new InsertionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(0, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("439_450dup", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Asn147_Lys150dup)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(
				ImmutableSortedSet.of(VariantEffect.INFRAME_INSERTION, VariantEffect.DIRECT_TANDEM_DUPLICATION),
				annotation1.getEffects());
	}

	/**
	 *
	 * This is the test for the offset (+1) duplication of a single triplicate / one amino acids shifting the Stop-codon
	 * '+' strand
	 *
	 * mutalyzer: NM_001005495.1(OR2T3_v001):c.949_954dup NM_001005495.1(OR2T3_i001):p.(*319Gluext*2) I think mutalyzer
	 * is wrong here, the stop is right after the duplication.
	 */
	@Test
	public void testRealWorldCase_uc001iel_1_fifth() throws InvalidGenomeChange {
		this.builderForward = TranscriptModelFactory.parseKnownGenesLine(refDict,
				"uc001iel.1	chr1	+	248636651	248637608	248636651	248637608	1	248636651,	248637608,	Q8NH03	uc001iel.1");
		this.builderForward
				.setSequence("atgtgctcagggaatcagacttctcagaatcaaacagcaagcactgatttcaccctcacgggactctttgctgagagcaagcatgctgccctcctctacaccgtgaccttccttcttttcttgatggccctcactgggaatgccctcctcatcctcctcatccactcagagccccgcctccacacccccatgtacttcttcatcagccagctcgcgctcatggatctcatgtacctatgcgtgactgtgcccaagatgcttgtgggccaggtcactggagatgataccatttccccgtcaggctgtgggatccagatgttcttctacctgaccctggctggagctgaggttttcctcctggctgccatggcctatgaccgatatgctgctgtttgcagacctctccattacccactgctgatgaaccagagggtgtgccagctcctggtgtcagcctgctgggttttgggaatggttgatggtttgttgctcacccccattaccatgagcttccccttttgccagtctaggaaaatcctgagttttttctgtgagactcctgccctgctgaagctctcctgctctgacgtctccctctataagacgctcatgtacctgtgctgcatcctcatgcttctcgcccccatcatggtcatctccagctcatacaccctcatcctgcatctcatccacaggatgaattctgccgccggccacaggaaggccttggccacctgctcctcccacatgatcatagtgctgctgctcttcggtgcttccttctacacctacatgctcccgagttcctaccacacagctgagcaggacatgatggtgtctgccttttacaccatcttcactcctgtgctgaaccccctcatttacagtctccgcaacaaagatgtcaccagggctctgaggagcatgatgcagtcaagaatgaaccaagaaaagtag"
						.toUpperCase());
		this.builderForward.setGeneSymbol("OR2T3");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 248637605,
				PositionType.ZERO_BASED), "", "GAAAAG");
		Annotation annotation1 = new InsertionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(0, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("949_954dup", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(*319Gluext*2)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.STOP_LOST), annotation1.getEffects());
	}

	/**
	 * annovar: MAGEF1:uc003fpa.3:exon1:c.456_457insGGA:p.L152delinsLE, chr3:184429154->TCC
	 *
	 * uc003fpa.3:exon1:c.456_458dupGGA:p.L152delinsLG
	 *
	 * Refseq: NM_022149
	 *
	 * Mutalyzer: Note that the position gets shifted downstream to bcome the most 3' position.
	 *
	 * NM_022149.4(MAGEF1_v001):c.474_476dup NM_022149.4(MAGEF1_i001):p.(Glu158dup)
	 */
	@Test
	public void testRealWorldCase_uc003fpa_3_fourth() throws InvalidGenomeChange {
		this.builderForward = TranscriptModelFactory.parseKnownGenesLine(refDict,
				"uc003fpa.3	chr3	-	184428154	184429836	184428685	184429609	1	184428154,	184429836,	Q9HAY2	uc003fpa.3");
		this.builderForward
				.setSequence("gcgggcgcggactgaggctgcgcgccgcaggttccggctgctggcggcgttgcggccgcaggtttgactcccgtgcggtgcggcccagcagccacaaagctcccgctgccattgctccttgtactcccgccgtcactgccgctgtccaacccctcccccggggcttgcgcggcggctcccacacccctcggcccgtgtacgcgctctgcacctgcctgcccgaaaacatgttgcagacaccagagagcagggggctcccggtcccgcaggccgagggggagaaggatggcggccatgatggtgagacccgggccccgaccgcctcgcaggagcgccccaaggaggagcttggcgccgggagggaggagggggctgcggagcccgccctcacccggaaaggcgcgagggccttggcggccaaagccttggcaaggcgcagggcctaccgccggctgaatcggacggtggcggagttggtgcagttcctcctggtgaaagacaagaagaagagtcccatcacacgctcggagatggtgaaatacgttattggagacttgaagattctgttcccggacatcatcgcaagggccgcagagcatctgcggtatgtctttggttttgagctgaaacagtttgaccgcaagcaccacacttacatcctgatcaacaaactaaaacctctggaggaggaggaggaggaggatctgggaggagatggccccagattgggtctgttaatgatgatcctgggccttatctatatgagaggtaatagcgccagggaggcccaggtctgggagatgctgcgtcggttgggggtgcaaccctcaaagtatcatttcctctttgggtatccgaagaggcttattatggaagattttgtgcagcagcgatatctcagttacaggcgggtgcctcacaccaatccaccagaatatgaattctcttggggtccccgaagcaacctggaaatcagcaagatggaagtcctggggttcgtggccaaactgcataagaaggaaccgcagcactggccagtgcagtaccgtgaggccctagcagacgaggccgacagggccagagccaaggccagagctgaagccagtatgagggccagggccagtgctagggccggcatccacctctggtgagggttggtgaaaagttggccagtgggtccccgtgaggacgaactactgtcctgagtcataagtaatatgggtggggcgagggtcttatttctgtagaaatcgtgtgactttaaggatttagattttgtatcttatgttttgtaacatttaataattactgttaaaatgctgtttgtaaatgagattggtctactttttcctgtaggattttattgtagagttttgctggttttgtaaaatggatggaagaactttgtatttatactgtgattttgaacagattatgcaacattggaaggaaggctgtactttgatggtttgaaggaactcagcagtatgatgatctggttccaggggaaaaaaatagctggttggtgtctagccccccaacacttttgtctgttgtgtataaaagaagaaagactggcatgtaccttcatttgcttagctatttgagtatctagagaaaaattaaaatgcaatgagttagcagtataccctggcacacttaataaattaaacatttgtggaaaaaaaaaaaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("MAGEF1");
		this.infoForward = builderForward.build();
		// RefSeq NM_022149.4

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 3, 184429154,
				PositionType.ZERO_BASED), "", "TCC");
		Annotation annotation1 = new InsertionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(0, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("474_476dup", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Glu158dup)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.DISRUPTIVE_INFRAME_INSERTION,
				VariantEffect.DIRECT_TANDEM_DUPLICATION), annotation1.getEffects());
	}

	/**
	 * annovar:
	 * FAM178B:uc002sxl.4:exon13:c.1579_1580insCGAT:p.L527fs,FAM178B:uc002sxk.4:exon7:c.628_629insCGAT:p.L210fs,
	 * chr2:97568428->ATCG
	 */
	@Test
	public void testRealWorldCase_uc002sxk_4() throws InvalidGenomeChange {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc002sxk.4	chr2	-	97541618	97617238	97542012	97617157	11	97541618,97543626,97544152,97559662,97568356,97586901,97587214,97589226,97594942,97613554,97617115,	97542045,97543779,97544230,97559788,97568444,97586999,97587391,97589320,97595057,97613639,97617238,	B3KV66	uc002sxk.4");
		this.builderForward
				.setSequence("agccctctgcctcccagctccccgccagcccaacagctctccttcctgcgcagtggcctcctgaacatcctctacctgcacatgcctgactgcccggtatccctgctccagtggctgttccagctgctgacatggcctccagaaacatctttgggagcctttggtcttctgtgggatctcattgtggatggaatcttccttcagcctgatgaagacaagcacctgtggtgcccctcactgcaagaagtcagggaggcattccacagcctgggtgcccacagtcctgccctgtaccctctggggcccttttggcacggtggcagggtgcttccaggcgaggctggcctgaatgagaatgaggagcaggacgctccccaagagattgccttggacatcagcctgggccacatctacaagtttctggcgctgtgtgcccaggcccagccgggggcctacactgatgagaacctcatgggactgattgagctgctgtgccgcaccagcctggacgtggggctccgcctgctgcccaaagttgacctccagcagcttctcctcttgctcctggagaacatccgggagtggccagggaagctccaggaactgtgctgcaccctgagctgggtgtctgaccaccaccacaacctgctggccctcgtgcagttcttcccagacatgacctcccggagcaggcggcttcgaagccagctcagccttgtggtcattgctcgaatgctgggccagcaggagatgctccctctctggcaagagaagacccagctgtcctcgctcagccggctcctgggcctcatgaggccatcatctctcaggcaatacctggactctgtgcccttgccaccctgccaggagcaacagccaaaggctagtgccgagctagaccacaaggcctgctacctgtgccacagcttgctgatgctggccggggtagttgttagctgccaggacatcactccagaccagtggggcgagctgcagctgctgtgcatgcagttggaccgccacatcagcacgcagatccgggagagcccccaggccatgcaccgcaccatgctcaaggacctggctacccagacctacatccgttggcaggagctgctgacccactgccagccccaggcccagtatttcagcccctggaaagacatctaaagggacagggtcagggcagcccagggctcctggcttcagcaggaagtgaacaggctcagggaactggaggaagcgaagcatcaaggccagaggaggccacatgctgaccagcctgatgaggcaagagcctgcccctgccaccgccccgacccctctcctctctgcaagagcctgcctctgccaccgccccgaccccctctcctctcagcaagggatgggcctctctgcctcgcccacccctcagccctcctcccagccatctcctcttccctaaggcctctgtctccatagctctggtttccctgggcctcagtcctccccaccctccttcctctgtctccctgtcactaatgtgaggtttctttgtgcacattaaagtcttctttcagcatca"
						.toUpperCase());
		this.builderForward.setGeneSymbol("FAM178B");
		this.infoForward = builderForward.build();
		// RefSeq NM_001122646.2

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 2, 97568427,
				PositionType.ZERO_BASED), "", "ATCG");
		Annotation annotation1 = new InsertionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(6, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("628_629insCGAT", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Leu210Profs*61)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FRAMESHIFT_ELONGATION), annotation1.getEffects());
	}

	/**
	 * annovar: RANBP2:uc002tem.4:exon16:c.2265_2266insCC:p.D755fs, chr2:109371423->CC
	 */
	@Test
	public void testRealWorldCase_uc002tem_4_first() throws InvalidGenomeChange {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc002tem.4	chr2	+	109335936	109402267	109336062	109400357	29	109335936,109345587,109347229,109347777,109351987,109352559,109356944,109363166,109365375,109367719,109367983,109368326,109369453,109369881,109370280,109371360,109371631,109374868,109378556,109379692,109388156,109388944,109389323,109392187,109393585,109397724,109398583,109398983,109400051,	109336134,109345655,109347341,109347930,109352218,109352705,109357137,109363254,109365585,109367901,109368159,109368450,109369615,109370019,109370427,109371540,109371715,109375004,109378651,109384844,109388327,109389037,109389502,109392392,109393687,109397885,109398857,109399318,109402267,	P49792	uc002tem.4");
		this.builderForward
				.setSequence("cacagtggtcctccgccggctacggcgctgcgtcactggtttgcaggcgctttcctcttggaagtggcgactgctgcgggcctgagcgctggtctcacgcgcctcgggagccaggttggcggcgcgatgaggcgcagcaaggctgacgtggagcggtacatcgcctcggtgcagggctccaccccgtcgcctcgacagaagtcaatgaaaggattctattttgcaaagctgtattatgaagctaaagaatatgatcttgctaaaaaatacatatgtacttacattaatgtgcaagagagggatcccaaagctcacagatttctgggtcttctttatgaattggaagaaaacacagacaaagccgttgaatgttacaggcgttcagtggaattaaacccaacacaaaaagatcttgtgttgaagattgcagaattgctttgtaaaaatgatgttactgatggaagagcaaaatactggcttgaaagagcagccaaacttttcccaggaagtcctgcaatttataaactaaaggaacagcttctagattgtgaaggtgaagatggatggaataaactttttgacttgattcagtcagaactttatgtaagacctgatgacgtccatgtgaacatccggctagtggaggtgtatcgctcaactaaaagattgaaggatgctgtggcccactgccatgaggcagagaggaacatagctttgcgttcaagtttagaatggaattcgtgtgttgtacagacccttaaggaatatctggagtctttacagtgtttggagtctgataaaagtgactggcgagcaaccaatacagacttactgctggcctatgctaatcttatgcttcttacgctttccactagagatgtgcaggaaagtagagaattactgcaaagttttgatagtgctcttcagtctgtgaaatctttgggtggaaatgatgaactgtcagctactttcttagaaatgaaaggacatttctacatgcatgctggttctctgcttttgaagatgggtcagcatagtagtaatgttcaatggcgagctctttctgagctggctgcattgtgctatctcatagcatttcaggttccaagaccaaagattaaattaataaaaggtgaagctggacaaaatctgctggaaatgatggcctgtgaccgactgagccaatcagggcacatgttgctaaacttaagtcgtggcaagcaagattttttaaaagagattgttgaaacttttgccaacaaaagcgggcagtctgcattatatgatgctctgttttctagtcagtcacctaaggatacatcttttcttggtagcgatgatattggaaacattgatgtacgagaaccagagcttgaagatttgactagatacgatgttggtgctattcgagcacataatggtagtcttcagcaccttacttggcttggcttacagtggaattcattgcctgctttacctggaatccgaaaatggctaaaacagcttttccatcatttgccccatgaaacctcaaggcttgaaacaaatgcacctgaatcaatatgtattttagatcttgaagtatttctccttggagtagtatataccagccacttacaattaaaggagaaatgtaattctcaccacagctcctatcagccgttatgcctgccccttcctgtgtgtaaacagctttgtacagaaagacaaaaatcttggtgggatgcggtttgtactctgattcacagaaaagcagtacctggaaacgtagcaaaattgagacttctagttcagcatgaaataaacactctaagagcccaggaaaaacatggccttcaacctgctctgcttgtacattgggcagaatgccttcagaaaacgggcagcggtcttaattctttttatgatcaacgagaatacatagggagaagtgttcattattggaagaaagttttgccattgttgaagataataaaaaagaagaacagtattcctgaacctattgatcctctgtttaaacattttcatagtgtagacattcaggcatcagaaattgttgaatatgaagaagacgcacacataacttttgctatattggatgcagtaaatggaaatatagaagatgctgtgactgcttttgaatctataaaaagtgttgtttcttattggaatcttgcactgatttttcacaggaaggcagaagacattgaaaatgatgccctttctcctgaagaacaagaagaatgcaaaaattatctgagaaagaccagggactacctaataaagattatagatgacagtgattcaaatctttcagtggtcaagaaattgcctgtgcccctggagtctgtaaaagagatgcttaattcagtcatgcaggaactcgaagactatagtgaaggaggtcctctctataaaaatggttctttgcgaaatgcagattcagaaataaaacattctacaccgtctcctaccagatattcactatcaccaagtaaaagttacaagtattctcccaaaacaccacctcgatgggcagaagatcagaattctttactgaaaatgatttgccaacaagtagaggccattaagaaagaaatgcaggagttgaaactaaatagcagtaactcagcatcccctcatcgttggcccacagagaattatggaccagactcagtgcctgatggatatcaggggtcacagacatttcatggggctccactaacagttgcaactactggcccttcagtatattatagtcagtcaccagcatataattcccagtatcttctcagaccagcagctaatgttactcccacaaagggcccagtctatggcatgaataggcttccaccccaacagcatatttatgcctatccgcaacagatgcacacaccgccagtgcaaagctcatctgcttgtatgttctctcaggagatgtatggtcctcctgcattgcgttttgagtctcctgcaacgggaattctatcgcccaggggtgatgattactttaattacaatgttcaacagacaagcacaaatccacctttgccagaaccaggatatttcacaaaacctccgattgcagctcatgcttcaagatctgcagaatctaagactatagaatttgggaaaactaattttgttcagcccatgccgggtgaaggattaaggccatctttgccaacacaagcacacacaacacagccaactccttttaaatttaactcaaatttcaaatcaaatgatggtgacttcacgttttcctcaccacaggttgtgacacagccccctcctgcagcttacagtaacagtgaaagccttttaggtctcctgacttcagataaacccttgcaaggagatggctatagtggagccaaaccaattcctggtggtcaaaccattgggcctcgaaatacattcaattttggaagcaaaaatgtgtctggaatttcatttacagaaaacatggggtcgagtcagcaaaagaattctggttttcggcgaagtgatgatatgtttactttccatggtccagggaaatcagtatttggaacacccactttagagacagcaaacaagaatcatgagacagatggaggaagtgcccatggggatgatgatgatgacggtcctcactttgagcctgtagtacctcttcctgataagattgaagtaaaaactggtgaggaagatgaagaagaattcttttgcaaccgcgcgaaattgtttcgtttcgatgtagaatccaaagaatggaaagaacgtgggattggcaatgtaaaaatactgaggcataaaacatctggtaaaattcgccttctaatgagacgagagcaagtattgaaaatctgtgcaaatcattacatcagtccagatatgaaattgacaccaaatgctggatcagacagatcttttgtatggcatgcccttgattatgcagatgagttgccaaaaccagaacaacttgctattaggttcaaaactcctgaggaagcagcactttttaaatgcaagtttgaagaagcccagagcattttaaaagccccaggaacaaatgtagccatggcgtcaaatcaggctgtcagaattgtaaaagaacccacaagtcatgataacaaggatatttgcaaatctgatgctggaaacctgaattttgaatttcaggttgcaaagaaagaagggtcttggtggcattgtaacagctgctcattaaagaatgcttcaactgctaagaaatgtgtatcatgccaaaatctaaacccaagcaataaagagctcgttggcccaccattagctgaaactgtttttactcctaaaaccagcccagagaatgttcaagatcgatttgcattggtgactccaaagaaagaaggtcactgggattgtagtatttgtttagtaagaaatgaacctactgtatctaggtgcattgcgtgtcagaatacaaaatctgctaacaaaagtggatcttcatttgttcatcaagcttcatttaaatttggccagggagatcttcctaaacctattaacagtgatttcagatctgttttttctacaaaggaaggacagtgggattgcagtgcatgtttggtacaaaatgaggggagctctacaaaatgtgctgcttgtcagaatccgagaaaacagagtctacctgctacttctattccaacacctgcctcttttaagtttggtacttcagagacaagtaaaactctaaaaagtggatttgaagacatgtttgctaagaaggaaggacagtgggattgcagttcatgcttagtgcgaaatgaagcaaatgctacaagatgtgttgcttgtcagaatccggataaaccaagtccatctacttctgttccagctcctgcctcttttaagtttggtacttcagagacaagcaaggctccaaagagcggatttgagggaatgttcactaagaaggagggacagtgggattgcagtgtgtgcttagtaagaaatgaagccagtgctaccaaatgtattgcttgtcagaatccaggtaaacaaaatcaaactacttctgcagtttcaacacctgcctcttcagagacaagcaaggctccaaagagcggatttgagggaatgttcactaagaaggagggacagtgggattgcagtgtgtgcttagtaagaaatgaagccagtgctaccaaatgtattgcttgtcagaatccaggtaaacaaaatcaaactacttctgcagtttcaacacctgcctcttcagagacaagcaaggctccaaagagcggatttgagggaatgttcactaagaaggaaggacagtgggattgcagtgtgtgcttagtaagaaatgaagccagtgctaccaaatgtattgcttgtcagtgtccaagtaaacaaaatcaaacaactgcaatttcaacacctgcctcttcggagataagcaaggctccaaagagtggatttgaaggaatgttcatcaggaaaggacagtgggattgtagtgtttgctgtgtacaaaatgagagttcttccttaaaatgtgtggcttgtgatgcctctaaaccaactcataaacctattgcagaagctccttcagctttcacactgggctcagaaatgaagttgcatgactcttctggaagtcaggtgggaacaggatttaaaagtaatttctcagaaaaagcttctaagtttggcaatacagagcaaggattcaaatttgggcatgtggatcaagaaaattcaccttcatttatgtttcagggttcttctaatacagaatttaagtcaaccaaagaaggattttccatccctgtgtctgctgatggatttaaatttggcatttcggaaccaggaaatcaagaaaagaaaagtgaaaagcctcttgaaaatggtactggcttccaggctcaggatattagtggccagaagaatggccgtggtgtgatttttggccaaacaagtagcacttttacatttgcagatcttgcaaaatcaacttcaggagaaggatttcagtttggcaaaaaagaccccaatttcaagggattttcaggtgctggagaaaaattattctcatcacaatacggtaaaatggccaataaagcaaacacttccggtgactttgagaaagatgatgatgcctataagactgaggacagcgatgacatccattttgaaccagtagttcaaatgcccgaaaaagtagaacttgtaacaggagaagaagatgaaaaagttctgtattcacagcgggtaaaactatttagatttgatgctgaggtaagtcagtggaaagaaaggggcttggggaacttaaaaattctcaaaaacgaggtcaatggcaaactaagaatgctgatgcgaagagaacaagtactaaaagtgtgtgctaatcattggataacgactacgatgaacctgaagcctctctctggatcagatagagcatggatgtggttagccagtgatttctctgatggtgatgccaaactagagcagttggcagcaaaatttaaaacaccagagctggctgaagaattcaagcagaaatttgaggaatgccagcggcttctgttagacataccacttcaaactccccataaacttgtagatactggcagagctgccaagttaatacagagagctgaagaaatgaagagtggactgaaagatttcaaaacatttttgacaaatgatcaaacaaaagtcactgaggaagaaaataagggttcaggtacaggtgcggccggtgcctcagacacaacaataaaacccaatcctgaaaacactgggcccacattagaatgggataactatgatttaagggaagatgctttggatgatagtgtcagtagtagctcagtacatgcttctccattggcaagtagccctgtgagaaaaaatcttttccgttttggtgagtcaacaacaggatttaacttcagttttaaatctgctttgagtccatctaagtctcctgccaagttgaatcagagtgggacttcagttggcactgatgaagaatctgatgttactcaagaagaagagagagatggacagtactttgaacctgttgttcctttacctgatctagttgaagtatccagtggtgaggaaaatgaacaagttgtttttagtcacagggcaaaactctacagatatgataaagatgttggtcaatggaaagaaaggggcattggtgatataaagattttacagaattatgataataagcaagttcgtatagtgatgagaagggaccaagtattaaaactttgtgccaatcacagaataactccagacatgactttgcaaaatatgaaagggacagaaagagtatggttgtggactgcatgtgattttgcagatggagaaagaaaagtagagcatttagctgttcgttttaaactacaggatgttgcagactcgtttaagaaaatttttgatgaagcaaaaacagcccaggaaaaagattctttgataacacctcatgtttctcggtcaagcactcccagagagtcaccatgtggcaaaattgctgtagctgtattagaagaaaccacaagagagaggacagatgttattcagggtgatgatgtagcagatgcaacttcagaagttgaagtgtctagcacatctgaaacaacaccaaaagcagtggtttctcctccaaagtttgtatttggttcagagtctgttaaaagcatttttagtagtgaaaaatcaaaaccatttgcattcggcaacagttcagccactgggtctttgtttggatttagttttaatgcacctttgaaaagtaacaatagtgaaactagttcagtagcccagagtggatctgaaagcaaagtggaacctaaaaaatgtgaactgtcaaagaactctgatatcgaacagtcttcagatagcaaagtcaaaaatctctttgcttcctttccaacggaagaatcttcaatcaactacacatttaaaacaccagaaaaggcaaaagagaagaaaaaacctgaagattctccctcagatgatgatgttctcattgtatatgaactaactccaaccgctgagcagaaagcccttgcaaccaaacttaaacttcctccaactttcttctgctacaagaatagaccagattatgttagtgaagaagaggaggatgatgaagatttcgaaacagctgtcaagaaacttaatggaaaactatatttggatggctcagaaaaatgtagacccttggaagaaaatacagcagataatgagaaagaatgtattattgtttgggaaaagaaaccaacagttgaagagaaggcaaaagcagatacgttaaaacttccacctacatttttttgtggagtctgtagtgatactgatgaagacaatggaaatggggaagactttcaatcagagcttcaaaaagttcaggaagctcaaaaatctcagacagaagaaataactagcacaactgacagtgtatatacaggtgggactgaagtgatggtaccttctttctgtaaatctgaagaacctgattctattaccaaatccattagttcaccatctgtttcctctgaaactatggacaaacctgtagatttgtcaactagaaaggaaattgatacagattctacaagccaaggggaaagcaagatagtttcatttggatttggaagtagcacagggctctcatttgcagacttggcttccagtaattctggagattttgcttttggttctaaagataaaaatttccaatgggcaaatactggagcagctgtgtttggaacacagtcagtcggaacccagtcagccggtaaagttggtgaagatgaagatggtagtgatgaagaagtagttcataatgaagatatccattttgaaccaatagtgtcactaccagaggtagaagtaaaatctggagaagaagatgaagaaattttgtttaaagagagagccaaactttatagatgggatcgggatgtcagtcagtggaaggagcgcggtgttggagatataaagattctttggcatacaatgaagaattattaccggatcctaatgagaagagaccaggtttttaaagtgtgtgcaaaccacgttattactaaaacaatggaattaaagcccttaaatgtttcaaataatgctttagtttggactgcctcagattatgctgatggagaagcaaaagtagaacagcttgcagtgagatttaaaactaaagaagtagctgattgtttcaagaaaacatttgaagaatgtcagcagaatttaatgaaactccagaaaggacatgtatcactggcagcagaattatcaaaggagaccaatcctgtggtgttttttgatgtttgtgcggacggtgaacctctagggcggataactatggaattattttcaaacattgttcctcggactgctgagaacttcagagcactatgcactggagagaaaggctttggtttcaagaattccatttttcacagagtaattccagattttgtttgccaaggaggagatatcaccaaacatgatggaacaggcggacagtccatttatggagacaaatttgaagatgaaaattttgatgtgaaacatactggtcctggtttactatccatggccaatcaaggccagaataccaataattctcaatttgttataacactgaagaaagcagaacatttggactttaagcatgtagtatttgggtttgttaaggatggcatggatactgtgaaaaagattgaatcatttggttctcccaaagggtctgtttgtcgaagaataactatcacagaatgtggacagatataaaatcattgttgttcatagaaaatttcatctgtataagcagttggattgaagcttagctattacaatttgatagttatgttcagcttttgaaaatggacgtttccgatttacaaatgtaaaattgcagcttatagctgttgtcactttttaatgtgttataattgaccttgcatggtgtgaaataaaagtttaaacactggtgtatttcaggtgtacttgtgtttatgtactcctgacgtattaaaatggaataatactaatcttgttaaaagcaatagacctcaaactattgaaggaatatgatatatgcaatttaattttaattccttttaagatatttggacttcctgcatggatatacttaccatttgaataaagggaccacaacttggataatttaattttaggtttgaaatatatttggtaatcttaactattggtgtactcatttatgcatagagactcgtttatgaatgggtagagccacagaacgtatagagttaaccaaagtgctcttctctagaatctttacacctcctgtgtggttacaagttaactttgtaagtagcgtaccttccttccttaaaatatctagcttcctgtgccctttcatagatattcgattaatttttacattttaaacaagttgactatttcctttaggggttttgtttcaaacttttctgtcatctgtctctactacctcagaaactgcagcttggttctgatgatagaaattgaatttttccttgtagttattgtgataaagtatgaatatttttagaaagtctataccatgttctttcgttaaagatttgctttatacaagattgttgcagtacctttttctggtaaattttgtagcagaaataaaatgacaattcctaagagccactgacatccaaaaaattcattacttacgcttcgggttcattctaaagtaaggaagacaatttaaaggcagtaaattcaaactgctgcataatttccagagctcctagtttctcaagtttgatacacaccaaaaacgtatttggaaatggcttgtatcaaatgttaggcaaattgctaaagaaaagaggatgttcttattggcctactcaatatggaactacaaagaatccaggtagaacacaaaattttgtatattgcaattatgaatattgactgtcttccacccatctgtgttctttcgggtgaaattacctcattttatttagtgaggaaagacaggtttattccctgttacatggggatttggaaattgggtatcctaaagcaagtaactgttcaaccaccagtcaaaagaggggagggatgttgtgcgagtaatgagtgatggtataccatcaccattccactcggccacaaagccagatacttgaaataaacctactccaaatgtatcagttcagtcttgaaccatggattacatatgtttacactaaatatttcaaattggcttatttggaaatctatgtaatataaactgatgtaaagtgtgttgtaacttttcagctgagacagttgatgccttcgtcatgattttagaataaattcttaagttaatgcaagtgctttttaagagactttttacagatttgtatgctttcctaaagcactaaagttacaaattaaaaagctttaaaaactttgaccaaaaatttgacaaaatgacatgtaaactgacttttcccgtattagtattccaaagatgcttaaaagtggcttgtggcatttatgagaaagtctttgtgtcacatttcaggaaaggactttgatttctctttgttatttaatcactgatgtggtctaaacccacgataatatgtatctttccttttaaactggattttatgttgtctcattaaaatctgcttaaagatagaataaaattcattatttgtaca"
						.toUpperCase());
		this.builderForward.setGeneSymbol("RANBP2");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 2, 109371423,
				PositionType.ZERO_BASED), "", "CC");
		Annotation annotation1 = new InsertionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(15, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("2265_2266insCC", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Tyr756Profs*21)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FRAMESHIFT_ELONGATION), annotation1.getEffects());
	}

	/**
	 * annovar: FBXL21 chr5:135272375->A
	 *
	 * mutalzyer: NM_012159(FBXL21_v001):c.93_94insA NM_012159(FBXL21_i001):p.(Gln32Thrfs*39)
	 */
	@Test
	public void testRealWorldCase_uc031sld_1() throws InvalidGenomeChange {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc031sld.1	chr5	+	135266005	135277367	135272283	135277360	9	135266005,135270043,135271008,135271311,135272282,135272451,135273113,135276177,135276715,	135266169,135270138,135271128,135271413,135272448,135272649,135273236,135276348,135277367,	Q9UKT6	uc031sld.1");
		this.builderForward
				.setSequence("ttgggcggctgcggtctgcgcggacccccgtgcctaggcgctcctgtgcctccagtgcgcggagccggcctcgctcgggcgtgttcctgcgccgaccggacggccggactccagcaccttggcccggcccgcgaacgctgagcacgcgcggaaaccctttaaaggaagtgaaatcaaatggaggttggtgagtaccccacggcaaccattgcttcgtttcatggttcagagtgaactctaatctggaagccttccttaggtcattcctgcttctgggatccaactctatgttgtgcttaagaaatgtgtcacatcagacagagtgaatcacaagacagagatatttacctatcacataaatattttattagatttcaagatgaccatatatacttggacgtatctggacttcaaatttgctggaacttaattgcgtcccaagctggctaatacctagagattcatcatttctgtttggaggaatgaagaggaacagtttatctgttgagaataaaattgtccagttgtcaggagcagcgaaacagccaaaagttgggttctactcttctctcaaccagactcatacacacacggttcttctagactgggggagtttgcctcaccatgtagtattacaaatttttcagtatcttcctttactagatcgggcctgtgcatcttctgtatgtaggaggtggaatgaagtttttcatatttctgacctttggagaaagtttgaatttgaactgaaccagtcagctacttcatcttttaagtccactcatcctgatctcattcagcagatcattaaaaagcattttgctcatcttcagtatgtcagctttaaggttgacagtagcgctgagtcagcagaagctgcctgtgatatactctctcagctggtaaattgttccatccagaccttgggcttgatttcaacagccaagccaagtttcatgaatgtgtcggagtctcattttgtgtcagcacttacagttgtttttatcaactcaaaatcattatcatcaatcaaaattgaagatacaccagtggatgatccttcattgaagattcttgtggccaataatagtgacactctaagactcccaaagatgagtagctgtcctcatgtttcatctgatggaattctttgtgtagctgaccgttgtcaaggccttagagaactggcgttgaattattacatcctaactgatgaacttttccttgcactctcaagcgagactcatgttaaccttgaacatcttcgaattgatgttgtgagtgaaaatcctggacagattaaatttcatgctgttaaaaaacacagttgggatgcacttattaaacattcccctagagttaatgttgttatgcacttctttctatatgaagaggaattcgagacgttcttcaaagaagaaacccctgttactcacctttattttggtcgttcagtcagcaaagtggttttaggacgggtaggtctcaactgtcctcgactgattgagttagtggtgtgtgctaatgatcttcagcctcttgataatgaacttatttgtattgctgaacactgtacaaacctaacagccttgggcctcagcaaatgtgaagttagctgcagtgccttcatcaggtttgtaagactgtgtgagagaaggttaacacagctctctgtaatggaggaagttttgatccctgatgaggattatagcctagatgaaattcacactgaagtctccaaatacctgggaagagtatggttccctgatgtgatgcctctctggtaatgggctg"
						.toUpperCase());
		this.builderForward.setGeneSymbol("FBXL21");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 5, 135272376,
				PositionType.ZERO_BASED), "", "A");
		Annotation annotation1 = new InsertionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(4, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("93_94insA", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Gln32Thrfs*39)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FRAMESHIFT_VARIANT), annotation1.getEffects());
	}

	/**
	 * annovar: RANBP2:uc002tem.4:exon20:c.6318_6319insAGCG:p.M2106fs, chr2:109383313->AGCG
	 */
	@Test
	public void testRealWorldCase_uc002tem_4_second() throws InvalidGenomeChange {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc002tem.4	chr2	+	109335936	109402267	109336062	109400357	29	109335936,109345587,109347229,109347777,109351987,109352559,109356944,109363166,109365375,109367719,109367983,109368326,109369453,109369881,109370280,109371360,109371631,109374868,109378556,109379692,109388156,109388944,109389323,109392187,109393585,109397724,109398583,109398983,109400051,	109336134,109345655,109347341,109347930,109352218,109352705,109357137,109363254,109365585,109367901,109368159,109368450,109369615,109370019,109370427,109371540,109371715,109375004,109378651,109384844,109388327,109389037,109389502,109392392,109393687,109397885,109398857,109399318,109402267,	P49792	uc002tem.4");
		this.builderForward
				.setSequence("cacagtggtcctccgccggctacggcgctgcgtcactggtttgcaggcgctttcctcttggaagtggcgactgctgcgggcctgagcgctggtctcacgcgcctcgggagccaggttggcggcgcgatgaggcgcagcaaggctgacgtggagcggtacatcgcctcggtgcagggctccaccccgtcgcctcgacagaagtcaatgaaaggattctattttgcaaagctgtattatgaagctaaagaatatgatcttgctaaaaaatacatatgtacttacattaatgtgcaagagagggatcccaaagctcacagatttctgggtcttctttatgaattggaagaaaacacagacaaagccgttgaatgttacaggcgttcagtggaattaaacccaacacaaaaagatcttgtgttgaagattgcagaattgctttgtaaaaatgatgttactgatggaagagcaaaatactggcttgaaagagcagccaaacttttcccaggaagtcctgcaatttataaactaaaggaacagcttctagattgtgaaggtgaagatggatggaataaactttttgacttgattcagtcagaactttatgtaagacctgatgacgtccatgtgaacatccggctagtggaggtgtatcgctcaactaaaagattgaaggatgctgtggcccactgccatgaggcagagaggaacatagctttgcgttcaagtttagaatggaattcgtgtgttgtacagacccttaaggaatatctggagtctttacagtgtttggagtctgataaaagtgactggcgagcaaccaatacagacttactgctggcctatgctaatcttatgcttcttacgctttccactagagatgtgcaggaaagtagagaattactgcaaagttttgatagtgctcttcagtctgtgaaatctttgggtggaaatgatgaactgtcagctactttcttagaaatgaaaggacatttctacatgcatgctggttctctgcttttgaagatgggtcagcatagtagtaatgttcaatggcgagctctttctgagctggctgcattgtgctatctcatagcatttcaggttccaagaccaaagattaaattaataaaaggtgaagctggacaaaatctgctggaaatgatggcctgtgaccgactgagccaatcagggcacatgttgctaaacttaagtcgtggcaagcaagattttttaaaagagattgttgaaacttttgccaacaaaagcgggcagtctgcattatatgatgctctgttttctagtcagtcacctaaggatacatcttttcttggtagcgatgatattggaaacattgatgtacgagaaccagagcttgaagatttgactagatacgatgttggtgctattcgagcacataatggtagtcttcagcaccttacttggcttggcttacagtggaattcattgcctgctttacctggaatccgaaaatggctaaaacagcttttccatcatttgccccatgaaacctcaaggcttgaaacaaatgcacctgaatcaatatgtattttagatcttgaagtatttctccttggagtagtatataccagccacttacaattaaaggagaaatgtaattctcaccacagctcctatcagccgttatgcctgccccttcctgtgtgtaaacagctttgtacagaaagacaaaaatcttggtgggatgcggtttgtactctgattcacagaaaagcagtacctggaaacgtagcaaaattgagacttctagttcagcatgaaataaacactctaagagcccaggaaaaacatggccttcaacctgctctgcttgtacattgggcagaatgccttcagaaaacgggcagcggtcttaattctttttatgatcaacgagaatacatagggagaagtgttcattattggaagaaagttttgccattgttgaagataataaaaaagaagaacagtattcctgaacctattgatcctctgtttaaacattttcatagtgtagacattcaggcatcagaaattgttgaatatgaagaagacgcacacataacttttgctatattggatgcagtaaatggaaatatagaagatgctgtgactgcttttgaatctataaaaagtgttgtttcttattggaatcttgcactgatttttcacaggaaggcagaagacattgaaaatgatgccctttctcctgaagaacaagaagaatgcaaaaattatctgagaaagaccagggactacctaataaagattatagatgacagtgattcaaatctttcagtggtcaagaaattgcctgtgcccctggagtctgtaaaagagatgcttaattcagtcatgcaggaactcgaagactatagtgaaggaggtcctctctataaaaatggttctttgcgaaatgcagattcagaaataaaacattctacaccgtctcctaccagatattcactatcaccaagtaaaagttacaagtattctcccaaaacaccacctcgatgggcagaagatcagaattctttactgaaaatgatttgccaacaagtagaggccattaagaaagaaatgcaggagttgaaactaaatagcagtaactcagcatcccctcatcgttggcccacagagaattatggaccagactcagtgcctgatggatatcaggggtcacagacatttcatggggctccactaacagttgcaactactggcccttcagtatattatagtcagtcaccagcatataattcccagtatcttctcagaccagcagctaatgttactcccacaaagggcccagtctatggcatgaataggcttccaccccaacagcatatttatgcctatccgcaacagatgcacacaccgccagtgcaaagctcatctgcttgtatgttctctcaggagatgtatggtcctcctgcattgcgttttgagtctcctgcaacgggaattctatcgcccaggggtgatgattactttaattacaatgttcaacagacaagcacaaatccacctttgccagaaccaggatatttcacaaaacctccgattgcagctcatgcttcaagatctgcagaatctaagactatagaatttgggaaaactaattttgttcagcccatgccgggtgaaggattaaggccatctttgccaacacaagcacacacaacacagccaactccttttaaatttaactcaaatttcaaatcaaatgatggtgacttcacgttttcctcaccacaggttgtgacacagccccctcctgcagcttacagtaacagtgaaagccttttaggtctcctgacttcagataaacccttgcaaggagatggctatagtggagccaaaccaattcctggtggtcaaaccattgggcctcgaaatacattcaattttggaagcaaaaatgtgtctggaatttcatttacagaaaacatggggtcgagtcagcaaaagaattctggttttcggcgaagtgatgatatgtttactttccatggtccagggaaatcagtatttggaacacccactttagagacagcaaacaagaatcatgagacagatggaggaagtgcccatggggatgatgatgatgacggtcctcactttgagcctgtagtacctcttcctgataagattgaagtaaaaactggtgaggaagatgaagaagaattcttttgcaaccgcgcgaaattgtttcgtttcgatgtagaatccaaagaatggaaagaacgtgggattggcaatgtaaaaatactgaggcataaaacatctggtaaaattcgccttctaatgagacgagagcaagtattgaaaatctgtgcaaatcattacatcagtccagatatgaaattgacaccaaatgctggatcagacagatcttttgtatggcatgcccttgattatgcagatgagttgccaaaaccagaacaacttgctattaggttcaaaactcctgaggaagcagcactttttaaatgcaagtttgaagaagcccagagcattttaaaagccccaggaacaaatgtagccatggcgtcaaatcaggctgtcagaattgtaaaagaacccacaagtcatgataacaaggatatttgcaaatctgatgctggaaacctgaattttgaatttcaggttgcaaagaaagaagggtcttggtggcattgtaacagctgctcattaaagaatgcttcaactgctaagaaatgtgtatcatgccaaaatctaaacccaagcaataaagagctcgttggcccaccattagctgaaactgtttttactcctaaaaccagcccagagaatgttcaagatcgatttgcattggtgactccaaagaaagaaggtcactgggattgtagtatttgtttagtaagaaatgaacctactgtatctaggtgcattgcgtgtcagaatacaaaatctgctaacaaaagtggatcttcatttgttcatcaagcttcatttaaatttggccagggagatcttcctaaacctattaacagtgatttcagatctgttttttctacaaaggaaggacagtgggattgcagtgcatgtttggtacaaaatgaggggagctctacaaaatgtgctgcttgtcagaatccgagaaaacagagtctacctgctacttctattccaacacctgcctcttttaagtttggtacttcagagacaagtaaaactctaaaaagtggatttgaagacatgtttgctaagaaggaaggacagtgggattgcagttcatgcttagtgcgaaatgaagcaaatgctacaagatgtgttgcttgtcagaatccggataaaccaagtccatctacttctgttccagctcctgcctcttttaagtttggtacttcagagacaagcaaggctccaaagagcggatttgagggaatgttcactaagaaggagggacagtgggattgcagtgtgtgcttagtaagaaatgaagccagtgctaccaaatgtattgcttgtcagaatccaggtaaacaaaatcaaactacttctgcagtttcaacacctgcctcttcagagacaagcaaggctccaaagagcggatttgagggaatgttcactaagaaggagggacagtgggattgcagtgtgtgcttagtaagaaatgaagccagtgctaccaaatgtattgcttgtcagaatccaggtaaacaaaatcaaactacttctgcagtttcaacacctgcctcttcagagacaagcaaggctccaaagagcggatttgagggaatgttcactaagaaggaaggacagtgggattgcagtgtgtgcttagtaagaaatgaagccagtgctaccaaatgtattgcttgtcagtgtccaagtaaacaaaatcaaacaactgcaatttcaacacctgcctcttcggagataagcaaggctccaaagagtggatttgaaggaatgttcatcaggaaaggacagtgggattgtagtgtttgctgtgtacaaaatgagagttcttccttaaaatgtgtggcttgtgatgcctctaaaccaactcataaacctattgcagaagctccttcagctttcacactgggctcagaaatgaagttgcatgactcttctggaagtcaggtgggaacaggatttaaaagtaatttctcagaaaaagcttctaagtttggcaatacagagcaaggattcaaatttgggcatgtggatcaagaaaattcaccttcatttatgtttcagggttcttctaatacagaatttaagtcaaccaaagaaggattttccatccctgtgtctgctgatggatttaaatttggcatttcggaaccaggaaatcaagaaaagaaaagtgaaaagcctcttgaaaatggtactggcttccaggctcaggatattagtggccagaagaatggccgtggtgtgatttttggccaaacaagtagcacttttacatttgcagatcttgcaaaatcaacttcaggagaaggatttcagtttggcaaaaaagaccccaatttcaagggattttcaggtgctggagaaaaattattctcatcacaatacggtaaaatggccaataaagcaaacacttccggtgactttgagaaagatgatgatgcctataagactgaggacagcgatgacatccattttgaaccagtagttcaaatgcccgaaaaagtagaacttgtaacaggagaagaagatgaaaaagttctgtattcacagcgggtaaaactatttagatttgatgctgaggtaagtcagtggaaagaaaggggcttggggaacttaaaaattctcaaaaacgaggtcaatggcaaactaagaatgctgatgcgaagagaacaagtactaaaagtgtgtgctaatcattggataacgactacgatgaacctgaagcctctctctggatcagatagagcatggatgtggttagccagtgatttctctgatggtgatgccaaactagagcagttggcagcaaaatttaaaacaccagagctggctgaagaattcaagcagaaatttgaggaatgccagcggcttctgttagacataccacttcaaactccccataaacttgtagatactggcagagctgccaagttaatacagagagctgaagaaatgaagagtggactgaaagatttcaaaacatttttgacaaatgatcaaacaaaagtcactgaggaagaaaataagggttcaggtacaggtgcggccggtgcctcagacacaacaataaaacccaatcctgaaaacactgggcccacattagaatgggataactatgatttaagggaagatgctttggatgatagtgtcagtagtagctcagtacatgcttctccattggcaagtagccctgtgagaaaaaatcttttccgttttggtgagtcaacaacaggatttaacttcagttttaaatctgctttgagtccatctaagtctcctgccaagttgaatcagagtgggacttcagttggcactgatgaagaatctgatgttactcaagaagaagagagagatggacagtactttgaacctgttgttcctttacctgatctagttgaagtatccagtggtgaggaaaatgaacaagttgtttttagtcacagggcaaaactctacagatatgataaagatgttggtcaatggaaagaaaggggcattggtgatataaagattttacagaattatgataataagcaagttcgtatagtgatgagaagggaccaagtattaaaactttgtgccaatcacagaataactccagacatgactttgcaaaatatgaaagggacagaaagagtatggttgtggactgcatgtgattttgcagatggagaaagaaaagtagagcatttagctgttcgttttaaactacaggatgttgcagactcgtttaagaaaatttttgatgaagcaaaaacagcccaggaaaaagattctttgataacacctcatgtttctcggtcaagcactcccagagagtcaccatgtggcaaaattgctgtagctgtattagaagaaaccacaagagagaggacagatgttattcagggtgatgatgtagcagatgcaacttcagaagttgaagtgtctagcacatctgaaacaacaccaaaagcagtggtttctcctccaaagtttgtatttggttcagagtctgttaaaagcatttttagtagtgaaaaatcaaaaccatttgcattcggcaacagttcagccactgggtctttgtttggatttagttttaatgcacctttgaaaagtaacaatagtgaaactagttcagtagcccagagtggatctgaaagcaaagtggaacctaaaaaatgtgaactgtcaaagaactctgatatcgaacagtcttcagatagcaaagtcaaaaatctctttgcttcctttccaacggaagaatcttcaatcaactacacatttaaaacaccagaaaaggcaaaagagaagaaaaaacctgaagattctccctcagatgatgatgttctcattgtatatgaactaactccaaccgctgagcagaaagcccttgcaaccaaacttaaacttcctccaactttcttctgctacaagaatagaccagattatgttagtgaagaagaggaggatgatgaagatttcgaaacagctgtcaagaaacttaatggaaaactatatttggatggctcagaaaaatgtagacccttggaagaaaatacagcagataatgagaaagaatgtattattgtttgggaaaagaaaccaacagttgaagagaaggcaaaagcagatacgttaaaacttccacctacatttttttgtggagtctgtagtgatactgatgaagacaatggaaatggggaagactttcaatcagagcttcaaaaagttcaggaagctcaaaaatctcagacagaagaaataactagcacaactgacagtgtatatacaggtgggactgaagtgatggtaccttctttctgtaaatctgaagaacctgattctattaccaaatccattagttcaccatctgtttcctctgaaactatggacaaacctgtagatttgtcaactagaaaggaaattgatacagattctacaagccaaggggaaagcaagatagtttcatttggatttggaagtagcacagggctctcatttgcagacttggcttccagtaattctggagattttgcttttggttctaaagataaaaatttccaatgggcaaatactggagcagctgtgtttggaacacagtcagtcggaacccagtcagccggtaaagttggtgaagatgaagatggtagtgatgaagaagtagttcataatgaagatatccattttgaaccaatagtgtcactaccagaggtagaagtaaaatctggagaagaagatgaagaaattttgtttaaagagagagccaaactttatagatgggatcgggatgtcagtcagtggaaggagcgcggtgttggagatataaagattctttggcatacaatgaagaattattaccggatcctaatgagaagagaccaggtttttaaagtgtgtgcaaaccacgttattactaaaacaatggaattaaagcccttaaatgtttcaaataatgctttagtttggactgcctcagattatgctgatggagaagcaaaagtagaacagcttgcagtgagatttaaaactaaagaagtagctgattgtttcaagaaaacatttgaagaatgtcagcagaatttaatgaaactccagaaaggacatgtatcactggcagcagaattatcaaaggagaccaatcctgtggtgttttttgatgtttgtgcggacggtgaacctctagggcggataactatggaattattttcaaacattgttcctcggactgctgagaacttcagagcactatgcactggagagaaaggctttggtttcaagaattccatttttcacagagtaattccagattttgtttgccaaggaggagatatcaccaaacatgatggaacaggcggacagtccatttatggagacaaatttgaagatgaaaattttgatgtgaaacatactggtcctggtttactatccatggccaatcaaggccagaataccaataattctcaatttgttataacactgaagaaagcagaacatttggactttaagcatgtagtatttgggtttgttaaggatggcatggatactgtgaaaaagattgaatcatttggttctcccaaagggtctgtttgtcgaagaataactatcacagaatgtggacagatataaaatcattgttgttcatagaaaatttcatctgtataagcagttggattgaagcttagctattacaatttgatagttatgttcagcttttgaaaatggacgtttccgatttacaaatgtaaaattgcagcttatagctgttgtcactttttaatgtgttataattgaccttgcatggtgtgaaataaaagtttaaacactggtgtatttcaggtgtacttgtgtttatgtactcctgacgtattaaaatggaataatactaatcttgttaaaagcaatagacctcaaactattgaaggaatatgatatatgcaatttaattttaattccttttaagatatttggacttcctgcatggatatacttaccatttgaataaagggaccacaacttggataatttaattttaggtttgaaatatatttggtaatcttaactattggtgtactcatttatgcatagagactcgtttatgaatgggtagagccacagaacgtatagagttaaccaaagtgctcttctctagaatctttacacctcctgtgtggttacaagttaactttgtaagtagcgtaccttccttccttaaaatatctagcttcctgtgccctttcatagatattcgattaatttttacattttaaacaagttgactatttcctttaggggttttgtttcaaacttttctgtcatctgtctctactacctcagaaactgcagcttggttctgatgatagaaattgaatttttccttgtagttattgtgataaagtatgaatatttttagaaagtctataccatgttctttcgttaaagatttgctttatacaagattgttgcagtacctttttctggtaaattttgtagcagaaataaaatgacaattcctaagagccactgacatccaaaaaattcattacttacgcttcgggttcattctaaagtaaggaagacaatttaaaggcagtaaattcaaactgctgcataatttccagagctcctagtttctcaagtttgatacacaccaaaaacgtatttggaaatggcttgtatcaaatgttaggcaaattgctaaagaaaagaggatgttcttattggcctactcaatatggaactacaaagaatccaggtagaacacaaaattttgtatattgcaattatgaatattgactgtcttccacccatctgtgttctttcgggtgaaattacctcattttatttagtgaggaaagacaggtttattccctgttacatggggatttggaaattgggtatcctaaagcaagtaactgttcaaccaccagtcaaaagaggggagggatgttgtgcgagtaatgagtgatggtataccatcaccattccactcggccacaaagccagatacttgaaataaacctactccaaatgtatcagttcagtcttgaaccatggattacatatgtttacactaaatatttcaaattggcttatttggaaatctatgtaatataaactgatgtaaagtgtgttgtaacttttcagctgagacagttgatgccttcgtcatgattttagaataaattcttaagttaatgcaagtgctttttaagagactttttacagatttgtatgctttcctaaagcactaaagttacaaattaaaaagctttaaaaactttgaccaaaaatttgacaaaatgacatgtaaactgacttttcccgtattagtattccaaagatgcttaaaagtggcttgtggcatttatgagaaagtctttgtgtcacatttcaggaaaggactttgatttctctttgttatttaatcactgatgtggtctaaacccacgataatatgtatctttccttttaaactggattttatgttgtctcattaaaatctgcttaaagatagaataaaattcattatttgtaca"
						.toUpperCase());
		this.builderForward.setGeneSymbol("RANBP2");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 2, 109383313,
				PositionType.ZERO_BASED), "", "AGCG");
		Annotation annotation1 = new InsertionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(19, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("6318_6319insAGCG", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Trp2107Serfs*6)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FRAMESHIFT_ELONGATION), annotation1.getEffects());
	}

	/**
	 * annovar: RANBP2:uc002tem.4:exon20:c.6882_6883insCAT:p.D2294delinsDH, chr2:109383877->CAT
	 *
	 * Mutalyzer: NM_006267.4(RANBP2_v001):c.6882_6883insCAT NM_006267.4(RANBP2_i001):p.(Asp2294_Glu2295insHis)
	 */
	@Test
	public void testRealWorldCase_uc002tem_4_third() throws InvalidGenomeChange {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc002tem.4	chr2	+	109335936	109402267	109336062	109400357	29	109335936,109345587,109347229,109347777,109351987,109352559,109356944,109363166,109365375,109367719,109367983,109368326,109369453,109369881,109370280,109371360,109371631,109374868,109378556,109379692,109388156,109388944,109389323,109392187,109393585,109397724,109398583,109398983,109400051,	109336134,109345655,109347341,109347930,109352218,109352705,109357137,109363254,109365585,109367901,109368159,109368450,109369615,109370019,109370427,109371540,109371715,109375004,109378651,109384844,109388327,109389037,109389502,109392392,109393687,109397885,109398857,109399318,109402267,	P49792	uc002tem.4");
		this.builderForward
				.setSequence("cacagtggtcctccgccggctacggcgctgcgtcactggtttgcaggcgctttcctcttggaagtggcgactgctgcgggcctgagcgctggtctcacgcgcctcgggagccaggttggcggcgcgatgaggcgcagcaaggctgacgtggagcggtacatcgcctcggtgcagggctccaccccgtcgcctcgacagaagtcaatgaaaggattctattttgcaaagctgtattatgaagctaaagaatatgatcttgctaaaaaatacatatgtacttacattaatgtgcaagagagggatcccaaagctcacagatttctgggtcttctttatgaattggaagaaaacacagacaaagccgttgaatgttacaggcgttcagtggaattaaacccaacacaaaaagatcttgtgttgaagattgcagaattgctttgtaaaaatgatgttactgatggaagagcaaaatactggcttgaaagagcagccaaacttttcccaggaagtcctgcaatttataaactaaaggaacagcttctagattgtgaaggtgaagatggatggaataaactttttgacttgattcagtcagaactttatgtaagacctgatgacgtccatgtgaacatccggctagtggaggtgtatcgctcaactaaaagattgaaggatgctgtggcccactgccatgaggcagagaggaacatagctttgcgttcaagtttagaatggaattcgtgtgttgtacagacccttaaggaatatctggagtctttacagtgtttggagtctgataaaagtgactggcgagcaaccaatacagacttactgctggcctatgctaatcttatgcttcttacgctttccactagagatgtgcaggaaagtagagaattactgcaaagttttgatagtgctcttcagtctgtgaaatctttgggtggaaatgatgaactgtcagctactttcttagaaatgaaaggacatttctacatgcatgctggttctctgcttttgaagatgggtcagcatagtagtaatgttcaatggcgagctctttctgagctggctgcattgtgctatctcatagcatttcaggttccaagaccaaagattaaattaataaaaggtgaagctggacaaaatctgctggaaatgatggcctgtgaccgactgagccaatcagggcacatgttgctaaacttaagtcgtggcaagcaagattttttaaaagagattgttgaaacttttgccaacaaaagcgggcagtctgcattatatgatgctctgttttctagtcagtcacctaaggatacatcttttcttggtagcgatgatattggaaacattgatgtacgagaaccagagcttgaagatttgactagatacgatgttggtgctattcgagcacataatggtagtcttcagcaccttacttggcttggcttacagtggaattcattgcctgctttacctggaatccgaaaatggctaaaacagcttttccatcatttgccccatgaaacctcaaggcttgaaacaaatgcacctgaatcaatatgtattttagatcttgaagtatttctccttggagtagtatataccagccacttacaattaaaggagaaatgtaattctcaccacagctcctatcagccgttatgcctgccccttcctgtgtgtaaacagctttgtacagaaagacaaaaatcttggtgggatgcggtttgtactctgattcacagaaaagcagtacctggaaacgtagcaaaattgagacttctagttcagcatgaaataaacactctaagagcccaggaaaaacatggccttcaacctgctctgcttgtacattgggcagaatgccttcagaaaacgggcagcggtcttaattctttttatgatcaacgagaatacatagggagaagtgttcattattggaagaaagttttgccattgttgaagataataaaaaagaagaacagtattcctgaacctattgatcctctgtttaaacattttcatagtgtagacattcaggcatcagaaattgttgaatatgaagaagacgcacacataacttttgctatattggatgcagtaaatggaaatatagaagatgctgtgactgcttttgaatctataaaaagtgttgtttcttattggaatcttgcactgatttttcacaggaaggcagaagacattgaaaatgatgccctttctcctgaagaacaagaagaatgcaaaaattatctgagaaagaccagggactacctaataaagattatagatgacagtgattcaaatctttcagtggtcaagaaattgcctgtgcccctggagtctgtaaaagagatgcttaattcagtcatgcaggaactcgaagactatagtgaaggaggtcctctctataaaaatggttctttgcgaaatgcagattcagaaataaaacattctacaccgtctcctaccagatattcactatcaccaagtaaaagttacaagtattctcccaaaacaccacctcgatgggcagaagatcagaattctttactgaaaatgatttgccaacaagtagaggccattaagaaagaaatgcaggagttgaaactaaatagcagtaactcagcatcccctcatcgttggcccacagagaattatggaccagactcagtgcctgatggatatcaggggtcacagacatttcatggggctccactaacagttgcaactactggcccttcagtatattatagtcagtcaccagcatataattcccagtatcttctcagaccagcagctaatgttactcccacaaagggcccagtctatggcatgaataggcttccaccccaacagcatatttatgcctatccgcaacagatgcacacaccgccagtgcaaagctcatctgcttgtatgttctctcaggagatgtatggtcctcctgcattgcgttttgagtctcctgcaacgggaattctatcgcccaggggtgatgattactttaattacaatgttcaacagacaagcacaaatccacctttgccagaaccaggatatttcacaaaacctccgattgcagctcatgcttcaagatctgcagaatctaagactatagaatttgggaaaactaattttgttcagcccatgccgggtgaaggattaaggccatctttgccaacacaagcacacacaacacagccaactccttttaaatttaactcaaatttcaaatcaaatgatggtgacttcacgttttcctcaccacaggttgtgacacagccccctcctgcagcttacagtaacagtgaaagccttttaggtctcctgacttcagataaacccttgcaaggagatggctatagtggagccaaaccaattcctggtggtcaaaccattgggcctcgaaatacattcaattttggaagcaaaaatgtgtctggaatttcatttacagaaaacatggggtcgagtcagcaaaagaattctggttttcggcgaagtgatgatatgtttactttccatggtccagggaaatcagtatttggaacacccactttagagacagcaaacaagaatcatgagacagatggaggaagtgcccatggggatgatgatgatgacggtcctcactttgagcctgtagtacctcttcctgataagattgaagtaaaaactggtgaggaagatgaagaagaattcttttgcaaccgcgcgaaattgtttcgtttcgatgtagaatccaaagaatggaaagaacgtgggattggcaatgtaaaaatactgaggcataaaacatctggtaaaattcgccttctaatgagacgagagcaagtattgaaaatctgtgcaaatcattacatcagtccagatatgaaattgacaccaaatgctggatcagacagatcttttgtatggcatgcccttgattatgcagatgagttgccaaaaccagaacaacttgctattaggttcaaaactcctgaggaagcagcactttttaaatgcaagtttgaagaagcccagagcattttaaaagccccaggaacaaatgtagccatggcgtcaaatcaggctgtcagaattgtaaaagaacccacaagtcatgataacaaggatatttgcaaatctgatgctggaaacctgaattttgaatttcaggttgcaaagaaagaagggtcttggtggcattgtaacagctgctcattaaagaatgcttcaactgctaagaaatgtgtatcatgccaaaatctaaacccaagcaataaagagctcgttggcccaccattagctgaaactgtttttactcctaaaaccagcccagagaatgttcaagatcgatttgcattggtgactccaaagaaagaaggtcactgggattgtagtatttgtttagtaagaaatgaacctactgtatctaggtgcattgcgtgtcagaatacaaaatctgctaacaaaagtggatcttcatttgttcatcaagcttcatttaaatttggccagggagatcttcctaaacctattaacagtgatttcagatctgttttttctacaaaggaaggacagtgggattgcagtgcatgtttggtacaaaatgaggggagctctacaaaatgtgctgcttgtcagaatccgagaaaacagagtctacctgctacttctattccaacacctgcctcttttaagtttggtacttcagagacaagtaaaactctaaaaagtggatttgaagacatgtttgctaagaaggaaggacagtgggattgcagttcatgcttagtgcgaaatgaagcaaatgctacaagatgtgttgcttgtcagaatccggataaaccaagtccatctacttctgttccagctcctgcctcttttaagtttggtacttcagagacaagcaaggctccaaagagcggatttgagggaatgttcactaagaaggagggacagtgggattgcagtgtgtgcttagtaagaaatgaagccagtgctaccaaatgtattgcttgtcagaatccaggtaaacaaaatcaaactacttctgcagtttcaacacctgcctcttcagagacaagcaaggctccaaagagcggatttgagggaatgttcactaagaaggagggacagtgggattgcagtgtgtgcttagtaagaaatgaagccagtgctaccaaatgtattgcttgtcagaatccaggtaaacaaaatcaaactacttctgcagtttcaacacctgcctcttcagagacaagcaaggctccaaagagcggatttgagggaatgttcactaagaaggaaggacagtgggattgcagtgtgtgcttagtaagaaatgaagccagtgctaccaaatgtattgcttgtcagtgtccaagtaaacaaaatcaaacaactgcaatttcaacacctgcctcttcggagataagcaaggctccaaagagtggatttgaaggaatgttcatcaggaaaggacagtgggattgtagtgtttgctgtgtacaaaatgagagttcttccttaaaatgtgtggcttgtgatgcctctaaaccaactcataaacctattgcagaagctccttcagctttcacactgggctcagaaatgaagttgcatgactcttctggaagtcaggtgggaacaggatttaaaagtaatttctcagaaaaagcttctaagtttggcaatacagagcaaggattcaaatttgggcatgtggatcaagaaaattcaccttcatttatgtttcagggttcttctaatacagaatttaagtcaaccaaagaaggattttccatccctgtgtctgctgatggatttaaatttggcatttcggaaccaggaaatcaagaaaagaaaagtgaaaagcctcttgaaaatggtactggcttccaggctcaggatattagtggccagaagaatggccgtggtgtgatttttggccaaacaagtagcacttttacatttgcagatcttgcaaaatcaacttcaggagaaggatttcagtttggcaaaaaagaccccaatttcaagggattttcaggtgctggagaaaaattattctcatcacaatacggtaaaatggccaataaagcaaacacttccggtgactttgagaaagatgatgatgcctataagactgaggacagcgatgacatccattttgaaccagtagttcaaatgcccgaaaaagtagaacttgtaacaggagaagaagatgaaaaagttctgtattcacagcgggtaaaactatttagatttgatgctgaggtaagtcagtggaaagaaaggggcttggggaacttaaaaattctcaaaaacgaggtcaatggcaaactaagaatgctgatgcgaagagaacaagtactaaaagtgtgtgctaatcattggataacgactacgatgaacctgaagcctctctctggatcagatagagcatggatgtggttagccagtgatttctctgatggtgatgccaaactagagcagttggcagcaaaatttaaaacaccagagctggctgaagaattcaagcagaaatttgaggaatgccagcggcttctgttagacataccacttcaaactccccataaacttgtagatactggcagagctgccaagttaatacagagagctgaagaaatgaagagtggactgaaagatttcaaaacatttttgacaaatgatcaaacaaaagtcactgaggaagaaaataagggttcaggtacaggtgcggccggtgcctcagacacaacaataaaacccaatcctgaaaacactgggcccacattagaatgggataactatgatttaagggaagatgctttggatgatagtgtcagtagtagctcagtacatgcttctccattggcaagtagccctgtgagaaaaaatcttttccgttttggtgagtcaacaacaggatttaacttcagttttaaatctgctttgagtccatctaagtctcctgccaagttgaatcagagtgggacttcagttggcactgatgaagaatctgatgttactcaagaagaagagagagatggacagtactttgaacctgttgttcctttacctgatctagttgaagtatccagtggtgaggaaaatgaacaagttgtttttagtcacagggcaaaactctacagatatgataaagatgttggtcaatggaaagaaaggggcattggtgatataaagattttacagaattatgataataagcaagttcgtatagtgatgagaagggaccaagtattaaaactttgtgccaatcacagaataactccagacatgactttgcaaaatatgaaagggacagaaagagtatggttgtggactgcatgtgattttgcagatggagaaagaaaagtagagcatttagctgttcgttttaaactacaggatgttgcagactcgtttaagaaaatttttgatgaagcaaaaacagcccaggaaaaagattctttgataacacctcatgtttctcggtcaagcactcccagagagtcaccatgtggcaaaattgctgtagctgtattagaagaaaccacaagagagaggacagatgttattcagggtgatgatgtagcagatgcaacttcagaagttgaagtgtctagcacatctgaaacaacaccaaaagcagtggtttctcctccaaagtttgtatttggttcagagtctgttaaaagcatttttagtagtgaaaaatcaaaaccatttgcattcggcaacagttcagccactgggtctttgtttggatttagttttaatgcacctttgaaaagtaacaatagtgaaactagttcagtagcccagagtggatctgaaagcaaagtggaacctaaaaaatgtgaactgtcaaagaactctgatatcgaacagtcttcagatagcaaagtcaaaaatctctttgcttcctttccaacggaagaatcttcaatcaactacacatttaaaacaccagaaaaggcaaaagagaagaaaaaacctgaagattctccctcagatgatgatgttctcattgtatatgaactaactccaaccgctgagcagaaagcccttgcaaccaaacttaaacttcctccaactttcttctgctacaagaatagaccagattatgttagtgaagaagaggaggatgatgaagatttcgaaacagctgtcaagaaacttaatggaaaactatatttggatggctcagaaaaatgtagacccttggaagaaaatacagcagataatgagaaagaatgtattattgtttgggaaaagaaaccaacagttgaagagaaggcaaaagcagatacgttaaaacttccacctacatttttttgtggagtctgtagtgatactgatgaagacaatggaaatggggaagactttcaatcagagcttcaaaaagttcaggaagctcaaaaatctcagacagaagaaataactagcacaactgacagtgtatatacaggtgggactgaagtgatggtaccttctttctgtaaatctgaagaacctgattctattaccaaatccattagttcaccatctgtttcctctgaaactatggacaaacctgtagatttgtcaactagaaaggaaattgatacagattctacaagccaaggggaaagcaagatagtttcatttggatttggaagtagcacagggctctcatttgcagacttggcttccagtaattctggagattttgcttttggttctaaagataaaaatttccaatgggcaaatactggagcagctgtgtttggaacacagtcagtcggaacccagtcagccggtaaagttggtgaagatgaagatggtagtgatgaagaagtagttcataatgaagatatccattttgaaccaatagtgtcactaccagaggtagaagtaaaatctggagaagaagatgaagaaattttgtttaaagagagagccaaactttatagatgggatcgggatgtcagtcagtggaaggagcgcggtgttggagatataaagattctttggcatacaatgaagaattattaccggatcctaatgagaagagaccaggtttttaaagtgtgtgcaaaccacgttattactaaaacaatggaattaaagcccttaaatgtttcaaataatgctttagtttggactgcctcagattatgctgatggagaagcaaaagtagaacagcttgcagtgagatttaaaactaaagaagtagctgattgtttcaagaaaacatttgaagaatgtcagcagaatttaatgaaactccagaaaggacatgtatcactggcagcagaattatcaaaggagaccaatcctgtggtgttttttgatgtttgtgcggacggtgaacctctagggcggataactatggaattattttcaaacattgttcctcggactgctgagaacttcagagcactatgcactggagagaaaggctttggtttcaagaattccatttttcacagagtaattccagattttgtttgccaaggaggagatatcaccaaacatgatggaacaggcggacagtccatttatggagacaaatttgaagatgaaaattttgatgtgaaacatactggtcctggtttactatccatggccaatcaaggccagaataccaataattctcaatttgttataacactgaagaaagcagaacatttggactttaagcatgtagtatttgggtttgttaaggatggcatggatactgtgaaaaagattgaatcatttggttctcccaaagggtctgtttgtcgaagaataactatcacagaatgtggacagatataaaatcattgttgttcatagaaaatttcatctgtataagcagttggattgaagcttagctattacaatttgatagttatgttcagcttttgaaaatggacgtttccgatttacaaatgtaaaattgcagcttatagctgttgtcactttttaatgtgttataattgaccttgcatggtgtgaaataaaagtttaaacactggtgtatttcaggtgtacttgtgtttatgtactcctgacgtattaaaatggaataatactaatcttgttaaaagcaatagacctcaaactattgaaggaatatgatatatgcaatttaattttaattccttttaagatatttggacttcctgcatggatatacttaccatttgaataaagggaccacaacttggataatttaattttaggtttgaaatatatttggtaatcttaactattggtgtactcatttatgcatagagactcgtttatgaatgggtagagccacagaacgtatagagttaaccaaagtgctcttctctagaatctttacacctcctgtgtggttacaagttaactttgtaagtagcgtaccttccttccttaaaatatctagcttcctgtgccctttcatagatattcgattaatttttacattttaaacaagttgactatttcctttaggggttttgtttcaaacttttctgtcatctgtctctactacctcagaaactgcagcttggttctgatgatagaaattgaatttttccttgtagttattgtgataaagtatgaatatttttagaaagtctataccatgttctttcgttaaagatttgctttatacaagattgttgcagtacctttttctggtaaattttgtagcagaaataaaatgacaattcctaagagccactgacatccaaaaaattcattacttacgcttcgggttcattctaaagtaaggaagacaatttaaaggcagtaaattcaaactgctgcataatttccagagctcctagtttctcaagtttgatacacaccaaaaacgtatttggaaatggcttgtatcaaatgttaggcaaattgctaaagaaaagaggatgttcttattggcctactcaatatggaactacaaagaatccaggtagaacacaaaattttgtatattgcaattatgaatattgactgtcttccacccatctgtgttctttcgggtgaaattacctcattttatttagtgaggaaagacaggtttattccctgttacatggggatttggaaattgggtatcctaaagcaagtaactgttcaaccaccagtcaaaagaggggagggatgttgtgcgagtaatgagtgatggtataccatcaccattccactcggccacaaagccagatacttgaaataaacctactccaaatgtatcagttcagtcttgaaccatggattacatatgtttacactaaatatttcaaattggcttatttggaaatctatgtaatataaactgatgtaaagtgtgttgtaacttttcagctgagacagttgatgccttcgtcatgattttagaataaattcttaagttaatgcaagtgctttttaagagactttttacagatttgtatgctttcctaaagcactaaagttacaaattaaaaagctttaaaaactttgaccaaaaatttgacaaaatgacatgtaaactgacttttcccgtattagtattccaaagatgcttaaaagtggcttgtggcatttatgagaaagtctttgtgtcacatttcaggaaaggactttgatttctctttgttatttaatcactgatgtggtctaaacccacgataatatgtatctttccttttaaactggattttatgttgtctcattaaaatctgcttaaagatagaataaaattcattatttgtaca"
						.toUpperCase());
		this.builderForward.setGeneSymbol("RANBP2");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 2, 109383877,
				PositionType.ZERO_BASED), "", "CAT");
		Annotation annotation1 = new InsertionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(19, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("6882_6883insCAT", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Asp2294_Glu2295insHis)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.INFRAME_INSERTION), annotation1.getEffects());
	}

	/**
	 * annovar: TTN:uc002umz.1:exon112:c.21594_21595insACTT:p.K7198fs, chr2:179519685->AAGT
	 */
	@Test
	public void testRealWorldCase_uc002umz_1() throws InvalidGenomeChange {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc002umz.1	chr2	-	179519471	179629029	179519472	179629000	113	179519471,179519638,179527692,179534944,179535816,179536697,179537133,179537361,179538359,179539040,179539765,179540647,179542347,179542851,179543140,179543473,179544065,179544325,179544620,179544980,179545805,179546102,179546387,179547423,179547937,179548725,179549056,179549392,179549632,179549979,179550244,179552837,179553403,179553779,179554016,179554241,179554539,179556742,179557223,179558335,179558648,179559325,179559555,179560074,179560591,179561847,179563569,179565846,179566254,179566766,179566894,179567180,179568873,179569236,179569603,179569900,179571180,179571588,179572252,179574292,179575361,179575788,179576670,179577041,179577423,179577811,179578623,179579018,179579712,179580219,179581821,179582249,179582669,179583048,179583421,179583890,179584280,179584709,179585111,179585647,179586573,179586985,179587385,179587772,179588144,179588582,179588986,179590094,179590494,179591816,179592311,179592836,179593226,179593617,179594014,179594390,179594819,179595231,179595651,179596031,179596419,179596792,179597166,179597560,179597965,179598340,179599054,179599433,179600237,179602808,179617850,179623710,179628903,	179519555,179519722,179527782,179535022,179535897,179536994,179537208,179537430,179538437,179539136,179539840,179540722,179542644,179542935,179543224,179543557,179544143,179544409,179544782,179545058,179545898,179546177,179546465,179547630,179548018,179548809,179549140,179549476,179549716,179550057,179550325,179552951,179553505,179553863,179554100,179554322,179554623,179556826,179557307,179558416,179558735,179559403,179559633,179560137,179560996,179561895,179563641,179565930,179566314,179566793,179566972,179567390,179569134,179569504,179569693,179570084,179571466,179571681,179572540,179574583,179575649,179576076,179576949,179577320,179577702,179578099,179578902,179579300,179579991,179580501,179582109,179582537,179582948,179583327,179583700,179584178,179584559,179584991,179585390,179585929,179586861,179587273,179587664,179588051,179588423,179588870,179589265,179590376,179590773,179592098,179592590,179593124,179593505,179593896,179594293,179594672,179595098,179595519,179595930,179596310,179596698,179597074,179597445,179597848,179598244,179598619,179599333,179599715,179600801,179603087,179617907,179623899,179629029,		uc002umz.1");
		this.builderForward
				.setSequence("ttccagaagttgtgtctcctgatcaggaaatgcctgtttatccacctgccatcatcaccccgcttcaggacactgtcacttctgaagggcagccagcccgttttcaatgccgggtttctggaacagatctaaaagtgtcgtggtacagcaaagacaagaaaatcaagccatctcggttctttagaatgactcaatttgaagacacttatcaactggaaattgccgaagcttatccagaagatgaaggaacttacacgtttgttgctagtaatgctgtaggccaagtatcaagcacagccaacctgagtctggaagctcctgaatcaattttgcatgagaggattgaacaagagattgagatggaaatgaaagctgccccagtgatcaagaggaaaatcgaacccctggaagtagcactgggccacctagccaaattcacctgtgagatccaaagtgctcccaatgtccggttccagtggtttaaagctggccgagaaatttatgagagtgacaagtgttctattcgatcttcaaagtatatctccagccttgaaatcctgagaacccaggtggttgactgcggcgagtatacatgcaaagcttccaatgagtatggcagtgtcagctgtacagccacactaactgtgacagaggcatatccaccaacctttctctccagacctaagtcccttacaacttttgtgggtaaggcagccaagttcatctgcacagtgacagggactcctgtgatagaaaccatttggcagaaagatggtgctgcactctcaccttcacctaactggaggatttccgacgcagaaaacaaacacattttagaactctcaaaccttaccattcaagatagaggagtttattcttgtaaggcttccaacaagtttggagcagacatctgccaagcagagttgatcatcattgataagccacatttcattaaagaattagagcctgtgcagtccgctatcaataagaaggtccaccttgagtgccaagtagatgaagacagaaaagtcacagttacgtggagcaaagatgggcaaaaactccccccagggaaagattataagatctgttttgaagataaaatagcaacacttgagattcctttggccaaactgaaagattcaggaacctatgtctgtacagcttcaaatgaggctggaagcagctcctgctcagccacagtcactgtcagagagccaccatccttcgtgaagaaggtggatccctcttatttaatgctcccaggtgaatcagcacgcctccattgcaagctgaaaggctcacctgtgatccaggttacttggtttaaaaataacaaagaactcagtgaaagtaacacagtccgaatgtattttgtcaattctgaggctatacttgatattacggatgtaaaagttgaagacagtgggagttactcatgtgaagcagtgaatgacgtcggcagtgatagctgcagtactgaaatagttatcaaagaacctccttctttcatcaaaactcttgagcctgcagacatagtgagaggaacaaatgctctacttcagtgtgaagtctcaggcactggaccatttgaaattagctggttcaaagacaagaaacaaattcgaagtagtaaaaaatacagattgttttctcagaagtctcttgtgtgtctggagatcttttcgtttaatagtgcagatgttggtgaatatgaatgtgttgttgctaatgaagtcggcaagtgtggctgcatggcaacccacttactcaaagaacctccaacctttgtaaagaaagtagatgatttgattgcactaggaggacaaaccgttaccctgcaagctgctgtgagagggtcagagcccatttctgtcacatggatgaaaggtcaagaggtcatcagagaagacggaaaaatcaaaatgagcttttccaatggtgttgcagtcttgataatccctgatgttcagattagttttggaggcaaatacacgtgcctggctgaaaatgaagctggaagccaaacatctgttggggaattaatagttaaagaacctgccaaaatcattgagcgagcagaactgatccaggtgacagcaggagatcccgccacactggagtacacagtggcaggcacgccagaactgaagcccaaatggtacaaagatgggagacccttggtcgccagtaaaaaataccgaataagttttaaaaacaatgttgcccagctcaaattttattcagctgagctgcacgacagtggccaatacacatttgagatttccaatgaagtgggcagcagctcctgtgagactacattcactgtattagatcgagacattgctccattttttaccaaacccttgcgcaacgtggatagtgttgttaatggtacctgcagactggactgcaaaattgcaggctccctccccatgagggtgtcctggtttaaggatggcaaagaaatagctgcttctgacagatacaggatagcatttgtggaaggcacagcctctttggagatcatcagagtagacatgaatgatgcagggaatttcacttgtcgagccacaaattccgtgggaagcaaagacagcagtggagccctgattgtgcaagaaccacccagttttgtaactaaacccggctcaaaggatgttctgcctggctcagcagtctgcctgaagagcactttccaaggatctactcctctcacaatcagatggtttaagggcaacaaagagctggtttctggtggaagctgctatattaccaaagaagctttagagagttccctggaactctatttagtaaaaacctctgattcgggcacgtacacatgtaaagtcagcaatgtcgctggaggggtggaatgcagtgcaaacttgtttgtaaaagaacctgccacatttgttgaaaagttagagccatcacagctgttaaagaagggagatgccacccagctagcctgcaaagtaactggtacccctccaattaaaataacatggtttgcaaatgatagagaaattaaggagagcagcaaacacagaatgtcttttgtggagtctactgcagttctaagactgacagatgttggcatcgaagacagtggtgaatatatgtgtgaggcccaaaatgaggctggcagtgaccactgcagtagcattgtaatagtcaaagagtcaccttattttaccaaggaatttaagccaattgaagtcttaaaggagtacgatgtcatgttgctggctgaggtggcaggcactcctccctttgagatcacttggttcaaagataacacaatcctgcgaagtggtagaaagtataagactttcattcaggatcatctggttagcctgcagatcctcaagtttgtagctgcagatgctggcgaataccagtgtcgggtgaccaatgaggtgggcagcagcatctgcagtgccagggtgactttaagagaacccccatccttcataaagaagatcgagagtaccagctccctccggggaggcacagctgccttccaggccactctgaagggctccttgccaattacggtgacttggctaaaagacagcgatgaaatcactgaagacgataatataagaatgacctttgagaacaatgtggccagtttgtacctcagtggcattgaagtcaagcatgatgggaaatatgtctgtcaggccaaaaatgatgcaggaatacaaagatgttctgcattactctcagtaaaagaacctgcaacaatcaccgaggaagctgtgtctatagatgtcacccaaggagacccagccactttgcaggttaaattttcagggactaaggagattacagccaaatggtttaaagatggccaagaactgaccctgggctcaaaatataaaatcagtgtcactgatacagtctcaatactgaagattatttctacagaaaagaaagatagtggagaatatactttcgaggtccaaaatgatgttgggaggagcagctgcaaggctagaattaatgtattagatcttatcatacctccttcattcaccaaaaagctgaagaaaatggacagcattaaaggttctttcattgatttagaatgtatagtggctgggtcacatcccataagcatccagtggttcaaagatgaccaagaaatatcagctagtgaaaagtacaaattctcttttcatgacaatactgccttcttggaaatcagccagctggaaggtacagacagtgggacatacacttgttctgccacaaataaggcagggcacaaccaatgcagtgggcatctgacagtcaaagaacccccttactttgtggaaaagccacagtcacaagatgtcaatcccaacacaagggtacagttaaaggctcttgtgggtggcactgcacccatgacaataaagtggtttaaagataacaaagagttacactcaggagcagcccgctcagtttggaaggatgacacctctaccagtctagagctctttgcagccaaagctaccgattctggaacctacatttgccaactaagcaatgatgttggcacagcaaccagcaaagccactctctttgtaaaagaacctcctcaattcattaagaagcccagtccagtcttagtgctgaggaatggacagtcaacaacatttgaatgccaaataacaggcactcctaaaatccgagtgtcttggtatctagacgggaatgaaataacagccattcagaaacatggcatttccttcattgatggtttagccactttccagatttctggtgccagggtagaaaatagtgggacttatgtgtgtgaagctcgaaatgacgcaggcacggcgagctgcagcattgaactcaaagtgaaagaaccccccacctttatcagagagctgaagcctgtggaggtagtaaaatatagtgacgtggagctggagtgtgaagttacgggaacacctccgtttgaagtcacttggctgaagaataacagggaaattcgaagcagcaaaaaatacacattgaccgacagagtgtctgtgtttaacctccatataaccaagtgtgacccttcagacactggggaataccagtgcattgtatccaatgaaggcggcagctgctcatgcagtactagagttgccctgaaagaaccaccatcattcattaagaagatagaaaataccactactgttttgaaaagttctgccacctttcagagtaccgtggcaggttctcctcctatttctataacctggctaaaggatgatcagattcttgatgaagatgataatgtctatatttcatttgtggacagtgtggccacacttcaaatcagaagtgtggataatggacacagtgggagatatacctgtcaagccaagaatgagtcaggagttgagaggtgttatgctttccttttagtacaagaaccagctcaaatcgtagagaaagctaaatcagttgatgttacggagaaagatcccatgaccttggaatgtgttgtggctggaacaccagaactcaaagtgaaatggcttaaagacgggaaacaaatagttcccagtagatacttttcaatgagttttgaaaataacgtggccagttttagaattcagtcagtaatgaagcaggacagcggtcagtacactttcaaggtggaaaatgacttcggaagcagtagctgtgatgcctacttaagagtgctagatcaaaatattcctccatcattcaccaaaaaattaaccaaaatggataaagttctgggctcttctattcatatggagtgcaaggtgtctggttcacttcccattagtgctcagtggtttaaggatggaaaagaaatatctacaagtgcaaaatacagacttgtgtgccatgagagatctgtgtctctggaagttaataatctggaattagaagatactgcaaattacacatgcaaagtgtcaaatgtagcaggagatgatgcatgcagtggcatcttaactgtgaaagaaccaccaagcttcctagtgaaacctgggcgacagcaagccatacctgattctacagtggaatttaaggcaatactaaaaggaacaccaccatttaaaataaaatggtttaaggatgatgtggaacttgtctcaggtcctaaatgtttcattggcttggaagggtcgactagcttcttaaatctctactcagtggatgcttctaagactggacagtatacttgccatgttaccaatgatgttggtagcgactcttgtactacgatgttgcttgtgacagaaccaccaaagtttgtaaagaaattagaagcctccaaaattgtgaaagcaggtgactcttcacgacttgaatgcaagatagctggatccccagaaatcagagttgtgtggttccgaaatgaacatgaacttccagccagtgataagtacagaatgactttcattgactcagtggccgtcatacagatgaacaatctcagtactgaggacagtggagatttcatttgtgaggctcagaatcccgctggcagcacaagctgcagtaccaaggttatagtaaaagagccacctgtttttagcagcttccctcctatagtagaaacccttaaaaatgctgaagtcagtcttgaatgtgaactttcgggaacaccaccgtttgaggtggtatggtacaaagacaagcggcaactcagaagcagcaagaaatacaagattgcatccaaaaacttccacacaagtattcacattctcaatgtggacacttcagacatcggtgaataccactgcaaagcacagaatgaagtgggaagtgatacttgtgtttgtactgtaaaattgaaagaaccaccaagatttgtctccaaactgaacagcctcactgttgtagccggagagcctgctgaattacaagcatccatagaaggcgcccagcctatttttgtccagtggcttaaagagaaggaagaagtgattagagaaagtgaaaacatcaggattacatttgtggaaaatgttgcaactctacagtttgcaaaagcagagccagctaatgctggaaagtatatctgccaaatcaagaatgatggtggaatgagggagaacatggccacactgatggtcttagagcccgcagtcattgttgagaaggcaggaccgatgacggtgactgtaggagaaacgtgcactctggagtgtaaggtggctggcactccggaactctctgttgaatggtacaaggatggaaagctgttgaccagcagccaaaaacacaaatttagcttctacaacaaaatctcttccttaaggattctctcagttgaaaggcaagatgcaggcacatacactttccaggttcaaaataatgttgggaaaagcagctgcacagctgtggttgatgtttcagaccgagcagttcctccctctttcacacgaagactgaaaaatactggtggggtgttaggtgcttcttgcatcttggaatgcaaagtagctggatcatcacctatttcagttgcctggtttcatgagaaaaccaagattgtcagtggagcaaagtaccaaaccacattttcagataatgtctgcacattgcagttgaattctctggattcctcagatatgggcaattacacatgcgtggctgctaatgtcgctgggtctgatgaatgtcgtgcagtgctaactgtacaagaaccaccctcttttgtgaaagaacctgaacctttggaagtactaccaggcaaaaatgtaaccttcacaagcgttattagaggaacccctccattcaaagtcaactggttcagaggtgccagagaactagtgaaaggagaccggtgcaacatctattttgaagacactgtggcagaactggaattatttaatattgacatatctcagagtggggaatacacctgtgtggtttctaacaacgctggccaagcatcttgcactacccgtctctttgtgaaagaaccagctgcatttttgaagagattaagtgatcattctgtagaaccagggaagtccataattctggagagcacctacactggaacacttccaatttctgtcacttggaaaaaggatggttttaacataactacctctgaaaaatgtaacatagtcacaacagagaaaacttgtatcctggaaattctgaatagcacaaaaagagatgcagggcagtattcctgcgagattgaaaatgaggcaggaagggatgtttgtggagctctggtatctacattagaaccgccttattttgttacggaactggaacctctggaggcagcagttggagattcggtttctttacaatgccaagttgctgggacaccagaaattacagtgtcttggtacaaaggagataccaaattacggccaactcctgaatacaggacctactttacaaacaatgtggccacacttgtttttaataaagtgaacatcaatgacagtggagagtacacatgcaaagcagaaaatagtataggaactgcttcttctaagactgtgttcagaattcaagagcgccaactcccaccttcttttgcaagacaattaaaggacattgaacaaactgtggggttacctgttacactcacttgtcgattaaatggctctgcacccatccaagtgtgctggtatagagatggagtacttttaagagacgatgaaaatctacagacttcatttgtagataatgtggcaactttaaaaattttgcaaactgacttgagtcactctggccagtactcttgctcagcttccaacccacttggaacagcatcttctagtgctagactcacagcaagagaacccaagaaatctcccttctttgacatcaagcctgtatctatagatgttattgctggagaaagtgctgattttgagtgtcatgttactggtgctcaaccgatgcgaatcacttggtcaaaagataacaaggagatccgtcctggaggaaactatacaatcacatgtgtgggaaacactcctcatttgagaattcttaaagtaggcaaaggagactctggtcaatatacttgccaagcaaccaatgatgttggcaaagacatgtgctcagctcagctcagtgtaaaagaacctccaaagtttgttaagaaattagaagcttcaaaagttgcaaagcagggagaatccattcaactggaatgtaaaataagtggctccccagaaatcaaagtttcatggttccgaaatgacagcgaacttcatgaaagttggaaatacaacatgtcattcattaattctgtggcattgcttacgatcaatgaagctagtgctgaagacagcggggactacatttgtgaggctcataatggtgttggtgacgccagctgcagcacagcgttgacagtgaaagcacctcctgttttcacccagaagccttctccagtaggagctcttaaaggttctgatgtgattctccaatgtgaaatttcgggaactcccccatttgaagtagtatgggttaaagatcgaaagcaggttagaaacagcaagaaatttaaaatcacttcaaaacattttgatacaagtcttcatatccttaatcttgaagcctccgatgtcggggaatatcactgcaaagctactaatgaggtgggaagtgacacgtgttcttgctctgtcaagttcaaagaacctccacgattcgtgaaaaagctaagtgacacctcaacccttattggggatgctgttgagttacgggccatagtggagggcttccagccaatttctgttgtctggctgaaagatagaggtgaagtcatcagagagagtgaaaataccaggatttcattcattgataacattgcaaccctccagctggggagtccagaagcatctaattctggaaaatatatatgccaaatcaaaaacgatgctggaatgagagaatgctctgcagtcttgactgtactagaaccagcaagaatcatagagaagcccgaacccatgactgtcactactggaaatccttttgcattagagtgtgtagtgactggaacaccagaactctcagccaagtggttcaaagatggaagagaattgtctgcagacagcaaacatcacattacattcatcaataaagtggcttcccttaaaatcccctgtgccgaaatgagtgacaaaggattatatagctttgaagtgaaaaacagtgttggcaaaagtaactgcactgtatccgtccatgtttctgatcggattgtgcctccttccttcatccgcaagctgaaagacgtgaatgccatcctgggggcctcagttgttttggagtgccgagtctctggctcagccccgatttcagttggctggtttcaggatggaaatgagattgttagtgggcccaaatgtcagtccagcttttcggaaaacgtctgtactttgaatctgagcttgttggagccctccgacacaggcatatacacgtgtgtggctgccaatgtagctggttccgatgagtgctcagcagtcctgactgtgcaagaaccaccatcttttgaacaaacccctgattctgtggaagttttgcctggaatgagcctcacattcaccagtgtcatcagaggcacccctcctttcaaggtcaagtggtttaaaggcagcagggaactggtgcctggggagtcatgcaacatctctctggaagattttgtcacagaactggagttgtttgaggtgcagccattagaaagtggagactattcttgcctcgttacaaatgatgctggcagtgcttcctgtaccacacatctttttgtgaaagaaccagccacctttgtgaaaagattggctgatttcagtgttgagacaggaagccccatagttctcgaggccacatacactggcacacctccaatctcagtgagctggataaaggacgagtatcttatttcacaatctgagagatgcagtattactatgacagaaaaatctaccatactggaaattcttgagagcacaatagaggattatgcacagtacagctgcctgatagaaaatgaggctggtcaagatatctgtgaagctctggtgtctgtcttagaaccaccgtactttattgaacctctggaacatgtggaagcagtcattggagaacctgcaactttacagtgtaaagtggatggaactccagaaattagaatttcttggtataaagaacacacaaagctacgatcagctcctgcatataaaatgcaattcaaaaataacgttgcttccttagtaatcaacaaagtggatcacagtgatgtgggagagtattcatgcaaggcagacaacagtgtgggggcagtcgcttcttcagctgtgcttgttatcaaagcgcgcaaacttccacctttctttgcaagaaaactgaaagacgttcatgagactctaggcttcccagttgcatttgaatgccgcatcaatggctcagaacctcttcaagtgtcttggtacaaggatggggttcttttgaaagatgatgctaatttgcagacatcttttgttcataatgtagcaactcttcagattttacaaactgaccagagccacatagggcagtataattgctctgcttctaatcctcttgggactgcttcatccagtgccaagctcattctctcagagcatgaagtgcctcctttctttgatctaaagcctgtatcagtagatcttgctcttggagaaagtggtacttttaaatgtcatgtaactgggactgcaccaatcaaaatcacttgggccaaagataaccgagagattcgccctggaggcaactacaagatgactttggtagaaaatactgccactctgacagttctcaaagtaggcaaaggcgatgccgggcagtacacctgctatgcaagcaacatcgctggaaaagactcttgttctgctcagctgggtgtacaagaaccacccaggttcattaagaagctagaaccttcaagaattgtgaaacaggatgaattcacaaggtatgaatgcaaaatcggtgggtctccagaaatcaaagttttatggtataaggacgagactgaaattcaagagagcagtaaattcagaatgtcattcgttgactcggtggctgtgctggaaatgcacaatctcagtgttgaagacagtggagactacacctgtgaggcccacaatgcagcaggcagtgccagcagcagcacatccttaaaagttaaagaaccacccattttccgcaaaaagcctcatcctatagagacactgaaaggagctgatgttcaccttgaatgtgagcttcagggcactcccccatttcacgtttcttggtataaagacaagagagaacttaggagcggcaagaagtacaagataatgtctgagaacttcctaaccagtatccacatcctgaatgtcgatgctgcagacattggggaatatcagtgtaaagccacaaatgatgtgggaagcgacacttgcgttggttccatcgctctcaaagcaccaccaagatttgtgaagaagctcagtgacatatctactgtcgttggtaaagaagttcagctgcagactaccattgaaggcgctgaacccatttcagtggtgtggttcaaagataagggagaaatcgttagagaaagtgacaacatatggatttcttattcagaaaacattgcaaccttacagttttcaagagtggaaccagccaatgctggaaaatacacttgtcagatcaaaaacgatgctgggatgcaagagtgcttcgccacgctatccgttctcgagcctgcaacaattgtagaaaagccagaatccataaaagttaccacgggagacacctgtaccttggagtgtacagtagctggcacccctgaactcagtactaagtggtttaaggatggaaaagagctaacaagtgacaacaaatacaaaataagcttcttcaacaaagtatccggccttaagatcatcaatgtagcaccgagtgacagtggggtatacagttttgaggtgcagaaccctgttggcaaagacagctgcacagcttcattgcaggtttcagaccgaaccgttcctccttcattcacaagaaaattgaaagagacaaatggtctatccggctcctcagttgtaatggagtgtaaagtctatgggtcacctccaatctctgtctcctggttccatgaaggaaatgagatcagtagtggaaggaaataccagaccaccctgacagataatacttgtgctttaactgtgaacatgctggaagaatcagacagtggtgactacacatgtatagctactaatatggctggttctgatgaatgtagtgctcctttgactgtgagagaaccaccatcttttgttcagaaacctgatcccatggatgttttaactgggaccaatgtaactttcacaagtatcgtaaaaggaacacctcctttcagtgttagctggttcaaaggtagcagtgaactagtaccaggtgacagatgcaacgtgtctttggaggattcagttgctgaactggagttgttcgatgtagatacatcacaaagtggagaatatacttgcatagttagcaatgaagctggcaaggcttcttgtacaacacatctctacataaaagccccagccaaatttgtgaagaggctgaatgattacagcatagagaaaggaaaacccctgatcctagagggtacattcactggaactcctcccatttcggttacctggaagaaaaatggcataaatgtaactccttctcagaggtgtaatataactacgactgaaaaatcggcaatcctggaaattccaagtagcacagtagaggatgcaggacaatacaactgctacattgaaaatgcctctggaaaagattcctgttcagcacaaatactcatactagaaccaccgtattttgtcaagcagttggagccggttaaggtgtctgttggagattctgcctctctacaatgccagcttgctggaacccctgaaattggggtatcctggtataaaggagatacaaaattgagacccactacgacttacaaaatgcattttaggaataatgttgctacactggttttcaaccaggttgatattaatgatagtggagaatatatctgcaaagctgaaaacagcgtgggagaagtttctgcatcaactttccttaccgttcaagagcaaaaacttccaccatcattttctcgacaattgagagatgttcaagaaacagttggactgccagttgtttttgattgtgccataagtggatcagaacctatctccgtgtcttggtataaagacggcaagccattgaaagacagcccaaatgtacaaacatcatttttagacaatacagccacactcaatatttttaaaactgaccggagccttgcaggccagtattcctgcacagctacaaaccctataggctctgcttcttccagtgccaggctcattctcacagaggggaagaacccacccttctttgacatccgtcttgcccctgtggatgctgtggtgggagaaagtgctgactttgagtgccacgtcacgggcacacaaccgataaaggtcagctgggccaaagacagcagagaaatacgaagtggcggaaagtaccagattagttatctggaaaacagcgcccacctgacagtcctcaaagtagacaaaggagattctggacaatatacctgctatgctgtgaatgaagtgggaaaagactcttgcacagctcagctgaatataaaagagcggctcatcccaccaagtttcactaaaagactctcagagacagtagaagaaacagaagggaattctttcaaacttgagggacgtgtggctggttcccaacctataactgttgcctggtacaaaaataatatagagatacaaccaacttctaactgtgaaataacattcaagaacaacacgttagtgctgcaagtcaggaaagcaggcatgaacgacgctggtttgtacacatgcaaagtgtccaatgatgcaggctctgctctgtgcacgtcttcaatcgtcatcaaagaacctaagaagccacctgtatttgatcagcaccttactccagtaacagtgagtgaaggagaatacgtgcagctcagctgccatgtccagggatctgagccaattaggatccagtggttgaaggctggcagggaaataaagccttcagacagatgcagcttcagctttgctagtgggacagctgtactggaactcagagatgtggctaaagcagattcgggagattatgtgtgtaaagcttcaaatgtggctggaagtgacactaccaaatcaaaagtgaccattaaagacaaaccagctgtggccccagcaaccaagaaagctgcggtagatggaagactcttttttgtgtcagaacctcagagtatcagagtcgtagaaaaaaccactgcgaccttcattgcaaaagttggaggtgacccaatcccaaatgttaaatggacaaaagggaagtggagacagctgaaccaaggaggtcgtgttttcatccaccaaaaaggcgatgaagcaaaactggagattagggacaccacaaaaactgattctgggttataccgatgcgtggcatttaacgaacatggtgaaattgaaagtaatgttaacttacaggtggatgaaaggaagaaacaagagaaaattgaaggcgatcttagagcaatgctgaaaaagactccaatcttaaagaaaggagctggagaagaagaggaaattgatatcatggaacttctcaaaaatgttgatcctaaagaatatgaaaaatatgcccgcatgtatggaatcactgacttccgaggtcttcttcaagcatttgaactgctcaagcaaagccaagaagaagagacacatagactggaaattgaggaaatagagaggtcagagagggacgaaaaggaatttgaggaacttgtatcatttattcagcaaagactgtcacagacagagcctgtcactctgatcaaggacattgaaaatcagacagttttgaaagataatgatgctgtctttgaaattgacattaaaattaattatccagaaatcaagctttcgtggtacaaaggaactgaaaaactggaacccagtgataaatttgaaataagcattgatggtgaccgacatacactcagagtcaaaaactgtcaacttaaagaccagggcaattatcgattggtttgtggtccacacatcgctagcgctaaactaactgtaattgagcctgcatgggaacggcatcttcaggatgtgactctgaaagaaggccagacttgcaccatgacatgccaattttctgtaccaaatgtaaaatctgaatggttcagaaatggcagaatccttaaaccccaaggtagacataaaacagaagtggaacacaaagtccataaattgaccattgcagatgttcgagcagaagaccagggtcagtacacctgcaaatatgaagaccttgaaacttcagcagaactcagaatcgaagctgaaccaattcagtttacaaagcgcatacagaacatcgtggtgagtgagcatcagtctgccacctttgagtgtgaagtgtcctttgatgatgccattgtaacatggtacaaaggaccaacagaactgacagagagccagaaatacaacttcaggaatgatggccgctgccattatatgaccatccacaatgtgaccccagacgatgaaggtgtctactcggtcatcgctcggctggagccaagaggtgaagcaagaagcacggcagagctgtacctaacgacgaaagaaatcaaacttgagctgaagcctcctgatattcctgactccagagttccaatcccaaccatgcctatcagagcagtgccaccagaagaaatccctcctgtggttgctcctcctatcccccttttgctaccaacacccgaagaaaagaaaccaccaccaaaacgtattgaagttaccaaaaaggctgtgaagaaagatgccaaaaaagttgttgcaaagcccaaagagatgacaccacgtgaagagattgtcaagaagcctccacctcctactaccttaattccagcaaaagctcctgaaatcattgatgtatcctctaaagctgaagaagtaaaaataatgactataaccagaaagaaagaggttcagaaagaaaaagaagctgtgtatgagaaaaagcaagcagtccacaaggagaagagagtcttcattgaatctttcgaagaaccttatgacgaactggaggtagaaccatacacagagccatttgaacaaccttattatgaagaaccagatgaagactatgaagagattaaggtagaagctaaaaaagaagttcacgaggaatgggaagaagattttgaagaagggcaagaatactatgaaagggaagaaggctatgacgaaggggaggaagagtgggaagaggcttaccaagaaagggaagtaattcaagttcaaaaggaggtctatgaagaatcacatgagagaaaagttccagccaaagtacctgaaaagaaagcaccaccacctcctaaagttataaagaagccagtaattgaaaaaattgaaaagacttctcgaagaatggaggaagaaaaagttcaagtcaccaaagtacctgaagtttcaaagaagattgttccacaaaaaccttcccggactccagtacaggaagaagttattgaagtgaaagtaccagctgtgcatacaaagaagatggttatttcagaagaaaagatgttctttgcttctcacacagaggaggaggtgtcagtcacagtccccgaggtacaaaaggaaattgttactgaagagaaaattcacgttgccatttccaaaagggttgaaccaccacctaaagtccctgagctacctgagaaaccagctccagaagaagtggcccctgttcctatccctaaaaaagtggagcccccagcaccaaaagttcctgaggttcccaagaaacccgtgccagaggagaaaaagccagttcctgtgcctaagaaggaacctgctgctcccccaaaagtcccagaggtgccaaagaaacctgtccctgaagaaaagattcccgttcctgttgcaaagaaaaaggaagctcccccagctaaagttcctgaagtacagaagggagttgtgacagaagaaaaaataaccattgtaactcaaagagaggaatctccaccaccagcagtgccagaaataccaaagaagaaagttcctgaagaaaggaaacctgttcctcggaaggaggaagaagttccaccaccaccaaaagtgccagctctgcctaagaaacccgtcccagaggagaaagttgcagtgccagttcctgtcgctaagaaagctcctcctccccgagctgaagtctctaagaaaactgttgtagaagaaaagagatttgttgctgaagaaaaactatccttcgcagttcctcaaagagtggaagtcacgcggcacgaagtatctgcagaggaggaatggagttactcagaagaggaggaaggtgtgtccatttcagtttatagagaagaagaaagagaggaggaggaagaagcagaggttacagaatatgaagtgatggaagagcctgaggaatatgttgtggaagaaaagctgcacattatttctaagagagtggaagctgagccagctgaagtgacagagaggcaggagaagaaaattgtactgaaaccaaaaattcctgctaaaatagaggagcctccaccggctaaagttcctgaagcacctaagaaaattgtgccagaaaagaaagttcctgctccagttcctaaaaaggaaaaggtgcccccacctaaagtgccagaagagccaaagaaaccagttccagaaaaaaaggttcctccaaaagtcattaagatggaagaacctctcccagccaaagtgactgagaggcacatgcaaattacccaggaagaaaaagttcttgttgctgtaactaaaaaagaggcgcctccaaaagcaagagtgccagaggaaccgaagagagctgtcccagaagaaaaagttctgaaactcaaacctaaaagagaggaggaaccaccagctaaagtgactgaattcagaaaaagagtggttaaagaagaaaaagtatcaattgaagctccaaaaagagaacctcaacccatcaaagaagtaactataatggaagagaaagaaagggcttataccctagaagaagaagctgtttcagtacaacgggaagaagaatatgaggaatatgaagaatatgattataaagaatttgaggagtatgaaccaacagaagaatatgaccaatatgaagaatacgaggagcgggagtatgaacgatatgaagagcatgaagaatacatcacagaaccagagaagcctatccctgtaaagcctgtcccagaagaaccagttcccacaaaaccaaaggccccaccggctaaagtgctgaagaaagctgtccctgaagaaaaagtaccagtgcccattcctaagaaactcaaacctccaccacccaaagtgcctgaagaaccaaagaaagtttttgaggaaaaaatacgtatttcaattaccaaacgtgaaaaagagcaggtgactgaacccgctgctaaagtgcccatgaagcccaagagggttgtcgcagaagaaaaagtacctgtccctagaaaagaagtagcaccacctgttagagtgccagaagtgcctaaagaacttgaaccagaagaggttgcctttgaagaggaagttgtaacccatgtagaagaatatcttgtagaagaagaagaagagtacattcatgaagaagaggagttcataactgaggaagaagtggtgccagtgataccagtcaaagtgcctgaggtacccaggaaacctgttccagaagagaagaaacctgttcctgttcccaagaagaaggaagctccaccggcaaaagtgcctgaggttcctaagaagccagaggagaaagttcctgtgcttattcctaaaaaggagaagcctccgccagcaaaagttcctgaagtgcccaagaaacctgtgccagaggagaaagtaccagtaccagttcctaaaaaggtggaagctccacctgccaaagtgccagaggtacccaagaagcctgtgcctgagaagaaggtgccagttcctgctcctaagaaagtggaggctccacctgcaaaagtgccagaggtgcccaagaagctcatcccagaagaaaagaaaccaacacctgttccgaaaaaagtggaagcaccaccacccaaagtgccaaagaaacgtgaaccagttccagttcctgtagctctacctcaggaagaggaagttctatttgaagaagaaattgttcctgaagaggaagttctacctgaggaagaggaagttctacctgaggaagaggaagttctacctgaagaagaggaagttctacctgaagaagaggaaattccacctgaggaagaggaagttcctcccgaagaagaatatgtacctgaggaagaagaatttgtacctgaagaagaagtccttccagaagttaaacctaaggtgccagtacctgcaccagtgcctgaaattaagaagaaagtgacagagaagaaagtggtcattcccaagaaagaggaggctccccctgccaaagttcctgaggtgcctaagaaggtggaagaaaaacgaatcattctccctaaagaagaggaagttctaccagttgaagtgactgaggagcctgaagaagagcctatttcagaagaagaaatcccagaagaaccacctagcatagaggaagttgaagaggtggcaccacctagagtgcctgaagtgattaagaaagcagtacctgaagcacctactcctgttcctaaaaaagtggaggcaccaccagctaaagtgtcaaagaaaattcctgaggaaaaagtacctgttcctgttcagaaaaaagaggcacccccagccaaagtgcctgaagtaccaaagaaagtcccagaaaagaaagtccttgtgcctaaaaaggaagctgttcccccagctaaagggagaactgtccttgaagaaaaagtatcagttgccttccgccaagaagtagtagtaaaagaaagactagaattagaagtagtagaagcagaagtggaagaaattccggaagaagaagagttccatgaagttgaagaatattttgaagaaggcgagtttcatgaagtagaagaattcatcaaattagaacaacatagagttgaagaagaacacagagttgaaaaagttcatagggtaatagaagtttttgaggctgaagaagtggaagtatttgaaaaaccaaaagctccacctaaagggcctgagatatctgagaaaatcatccctccaaaaaaaccgcccactaaagttgttcctcgaaaagagccaccagctaaagtaccggaggtgcctaagaaaattgtggtagaagaaaaagtacgtgttcctgaagagcccagagttccaccaactaaagtgcctgaagtgctgccaccgaaggaagtggtcccagaaaagaaagtaccggtgcctcctgccaaaaagccagaagctccacctcctaaagtgcctgaggctcccaaagaagttgttcctgaaaagaaagtgccagtgcctcctcctaaaaagcctgaagtgccacccacaaaagtcccagaggtgccaaaggcagctgtcccagaaaagaaggtgcctgaagctattcctcccaaaccggaaagtcctccccctgaag"
						.toUpperCase());
		this.builderForward.setGeneSymbol("TTN");
		this.infoForward = builderForward.build();
		// no RefSeq ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 2, 179519684,
				PositionType.ZERO_BASED), "", "AAGT");
		Annotation annotation1 = new InsertionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(111, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("21594_21595insACTT", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Val7199Thrfs*8)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FRAMESHIFT_ELONGATION), annotation1.getEffects());
	}

	/**
	 * annovar: CPS1:uc010fur.3:exon2:c.15_16insTTC:p.I5delinsIF, chr2:211421454->TTC
	 *
	 * Mutalyzer: NM_001122633.2:n.97_98insTTC NM_001122633.2(CPS1_v001):c.15_16insTTC
	 * NM_001122633.2(CPS1_i001):p.(Ile5_Lys6insPhe)
	 */
	@Test
	public void testRealWorldCase_uc010fur_3() throws InvalidGenomeChange {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc010fur.3	chr2	+	211342405	211543831	211342487	211542709	39	211342405,211421442,211438021,211441069,211442144,211444437,211447340,211452781,211454829,211455523,211456554,211457602,211459231,211460210,211464095,211465278,211466925,211469825,211471454,211473084,211476840,211481146,211502425,211503873,211504719,211507207,211512586,211513196,211515086,211518748,211521248,211523322,211525208,211527846,211532909,211539625,211540451,211541730,211542610,	211342490,211421583,211438131,211441214,211442234,211444494,211447433,211452871,211454958,211455630,211456693,211457680,211459330,211460306,211464285,211465436,211467054,211469970,211471665,211473283,211477017,211481265,211502567,211503939,211504783,211507389,211512781,211513264,211515162,211518826,211521356,211523412,211525379,211527921,211533008,211539685,211540564,211541860,211543831,	J3KQL0	uc010fur.3");
		this.builderForward
				.setSequence("gctcttttaaaatagttgctttcttaggaaatgtagttgctttcttaacctcatcaaattcatgaagatttagccgaggcccatgccacaaatcatcaaaatgacgaggattttgacagctttcaaagtggtgaggacactgaagactggttttggctttaccaatgtgactgcacaccaaaaatggaaattttcaagacctggcatcaggctcctttctgtcaaggcacagacagcacacattgtcctggaagatggaactaagatgaaaggttactcctttggccatccatcctctgttgctggtgaagtggtttttaatactggcctgggagggtacccagaagctattactgaccctgcctacaaaggacagattctcacaatggccaaccctattattgggaatggtggagctcctgatactactgctctggatgaactgggacttagcaaatatttggagtctaatggaatcaaggtttcaggtttgctggtgctggattatagtaaagactacaaccactggctggctaccaagagtttagggcaatggctacaggaagaaaaggttcctgcaatttatggagtggacacaagaatgctgactaaaataattcgggataagggtaccatgcttgggaagattgaatttgaaggtcagcctgtggattttgtggatccaaataaacagaatttgattgctgaggtttcaaccaaggatgtcaaagtgtacggcaaaggaaaccccacaaaagtggtagctgtagactgtgggattaaaaacaatgtaatccgcctgctagtaaagcgaggagctgaagtgcacttagttccctggaaccatgatttcaccaagatggagtatgatgggattttgatcgcgggaggaccggggaacccagctcttgcagaaccactaattcagaatgtcagaaagattttggagagtgatcgcaaggagccattgtttggaatcagtacaggaaacttaataacaggattggctgctggtgccaaaacctacaagatgtccatggccaacagagggcagaatcagcctgttttgaatatcacaaacaaacaggctttcattactgctcagaatcatggctatgccttggacaacaccctccctgctggctggaaaccactttttgtgaatgtcaacgatcaaacaaatgaggggattatgcatgagagcaaacccttcttcgctgtgcagttccacccagaggtcaccccggggccaatagacactgagtacctgtttgattcctttttctcactgataaagaaaggaaaagctaccaccattacatcagtcttaccgaagccagcactagttgcatctcgggttgaggtttccaaagtccttattctaggatcaggaggtctgtccattggtcaggctggagaatttgattactcaggatctcaagctgtaaaagccatgaaggaagaaaatgtcaaaactgttctgatgaacccaaacattgcatcagtccagaccaatgaggtgggcttaaagcaagcggatactgtctactttcttcccatcacccctcagtttgtcacagaggtcatcaaggcagaacagccagatgggttaattctgggcatgggtggccagacagctctgaactgtggagtggaactattcaagagaggtgtgctcaaggaatatggtgtgaaagtcctgggaacttcagttgagtccattatggctacggaagacaggcagctgttttcagataaactaaatgagatcaatgaaaagattgctccaagttttgcagtggaatcgattgaggatgcactgaaggcagcagacaccattggctacccagtgatgatccgttccgcctatgcactgggtgggttaggctcaggcatctgtcccaacagagagactttgatggacctcagcacaaaggcctttgctatgaccaaccaaattctggtggagaagtcagtgacaggttggaaagaaatagaatatgaagtggttcgagatgctgatgacaattgtgtcactgtctgtaacatggaaaatgttgatgccatgggtgttcacacaggtgactcagttgttgtggctcctgcccagacactctccaatgccgagtttcagatgttgagacgtacttcaatcaatgttgttcgccacttgggcattgtgggtgaatgcaacattcagtttgcccttcatcctacctcaatggaatactgcatcattgaagtgaatgccagactgtcccgaagctctgctctggcctcaaaagccactggctacccattggcattcattgctgcaaagattgccctaggaatcccacttccagaaattaagaacgtcgtatccgggaagacatcagcctgttttgaacctagcctggattacatggtcaccaagattccccgctgggatcttgaccgttttcatggaacatctagccgaattggtagctctatgaaaagtgtaggagaggtcatggctattggtcgtacctttgaggagagtttccagaaagctttacggatgtgccacccatctatagaaggtttcactccccgtctcccaatgaacaaagaatggccatctaatttagatcttagaaaagagttgtctgaaccaagcagcacgcgtatctatgccattgccaaggccattgatgacaacatgtcccttgatgagattgagaagctcacatacattgacaagtggtttttgtataagatgcgtgatattttaaacatggaaaagacactgaaaggcctcaacagtgagtccatgacagaagaaaccctgaaaagggcaaaggagattgggttctcagataagcagatttcaaaatgccttgggctcactgaggcccagacaagggagctgaggttaaagaaaaacatccacccttgggttaaacagattgatacactggctgcagaatacccatcagtaacaaactatctctatgttacctacaatggtcaggagcatgatgtcaattttgatgaccatggaatgatggtgctaggctgtggtccatatcacattggcagcagtgtggaatttgattggtgtgctgtctctagtatccgcacactgcgtcaacttggcaagaagacggtggtggtgaattgcaatcctgagactgtgagcacagactttgatgagtgtgacaaactgtactttgaagagttgtccttggagagaatcctagacatctaccatcaggaggcatgtggtggctgcatcatatcagttggaggccagattccaaacaacctggcagttcctctatacaagaatggtgtcaagatcatgggcacaagccccctgcagatcgacagggctgaggatcgctccatcttctcagctgtcttggatgagctgaaggtggctcaggcaccttggaaagctgttaatactttgaatgaagcactggaatttgcaaagtctgtggactacccctgcttgttgaggccttcctatgttttgagtgggtctgctatgaatgtggtattctctgaggatgagatgaaaaaattcctagaagaggcgactagagtttctcaggagcacccagtggtgctgacaaaatttgttgaaggggcccgagaagtagaaatggacgctgttggcaaagatggaagggttatctctcatgccatctctgaacatgttgaagatgcaggtgtccactcgggagatgccactctgatgctgcccacacaaaccatcagccaaggggccattgaaaaggtgaaggatgctacccggaagattgcaaaggcttttgccatctctggtccattcaacgtccaatttcttgtcaaaggaaatgatgtcttggtgattgagtgtaacttgagagcttctcgatccttcccctttgtttccaagactcttggggttgacttcattgatgtggccaccaaggtgatgattggagagaatgttgatgagaaacatcttccaacattggaccatcccataattcctgctgactatgttgcaattaaggctcccatgttttcctggccccggttgagggatgctgaccccattctgagatgtgagatggcttccactggagaggtggcttgctttggtgaaggtattcatacagccttcctaaaggcaatgctttccacaggatttaagataccccagaaaggcatcctgataggcatccagcaatcattccggccaagattccttggtgtggctgaacaattacacaatgaaggtttcaagctgtttgccacggaagccacatcagactggctcaacgccaacaatgtccctgccaccccagtggcatggccgtctcaagaaggacagaatcccagcctctcttccatcagaaaattgattagagatggcagcattgacctagtgattaaccttcccaacaacaacactaaatttgtccatgataattatgtgattcggaggacagctgttgatagtggaatccctctcctcactaattttcaggtgaccaaactttttgctgaagctgtgcagaaatctcgcaaggtggactccaagagtcttttccactacaggcagtacagtgctggaaaagcagcatagagatgcagacaccccagccccattattaaatcaacctgagccacatgttatctaaaggaactgattcacaactttctcagagatgaatattgataactaaacttcatttcagtttactttgttatgccttaatattctgtgtcttttgcaattaaattgtcagtcacttcttcaaaaccttacagtccttcctaagttactcttcatgagatttcatccatttactaatactgtatttttggtggactaggcttgcctatgtgcttatgtgtagctttttactttttatggtgctgattaatggtgatcaaggtaggaaaagttgctgttctattttctgaactctttctatactttaagatactctatttttaaaacactatctgcaaactcaggacactttaacagggcagaatactctaaaaacttgataaaattaaatatagatttaatttatgaaccttccatcatgatgtttgtgtattgcttctttttggatcctcattctcacccatttggctaatccaggaatattgttatcccttcccattatattgaagttgagaaatgtgacagaggcatttagagtatggacttttcttttctttttctttttctttttttctttttgagatggagtcacactctccaggctggagtgcagtggcacaatctcggctcactgcaatttccgtctcccaagttcaagcgattctcctgctttagactatggatttctttaaggaatactggtttgcagttttgttttctggactatatcagcagatggtagacagtgtttatgtagatgtgttgttgtttttatcattggattttaacttggcccgagtgaaataatcagatttttgtcattcacactctcccccagttttggaataacttggaagtaaggttcattcccttaagacgatggattctgttgaactatggggtcccacactgcactattaattccacccactgtaagggcaaggacaccattccttctacatataagaaaaaagtctctccccaagggcagcctttgttacttttaaatattttctgttattacaagtgctctaattgtgaacttttaaataaaatactattaagaggtaaaaaaaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("CPS1");
		this.infoForward = builderForward.build();
		// RefSeq NM_001122633.2

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 2, 211421454,
				PositionType.ZERO_BASED), "", "TTC");
		Annotation annotation1 = new InsertionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(1, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("15_16insTTC", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Ile5_Lys6insPhe)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.INFRAME_INSERTION), annotation1.getEffects());
	}

	/**
	 * annovar: MUC4:uc021xjp.1:exon2:c.8108_8109insTG:p.T2703fs, chr3:195510343->CA
	 *
	 * Mutalyzer: NM_018406.6:n.8263_8264insTG NM_018406.6:c.8108_8109insTG NM_018406.6:p.(Ser2704Alafs*301)
	 */
	@Test
	public void testRealWorldCase_uc021xjp_1_first() throws InvalidGenomeChange {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc021xjp.1	chr3	-	195473637	195538844	195474046	195538688	25	195473637,195475772,195477759,195478077,195479243,195479921,195481083,195484017,195485994,195487754,195488354,195488957,195489725,195490303,195490915,195491867,195492140,195493533,195495892,195497086,195498522,195501042,195505173,195505660,195538606,	195474251,195475935,195477983,195478142,195479317,195480101,195481243,195484199,195486132,195487988,195488456,195489125,195489816,195490512,195491035,195491993,195492320,195493622,195496023,195497242,195498687,195501176,195505326,195518368,195538844,	E9PDY6	uc021xjp.1");
		this.builderForward
				.setSequence("ctcttttgtcctcttcccaggttccctggccccttcggagaaacgcacttggttcgggccagccgcctgaggggacgggctcacgtctgctcctcacactgcagctgctgggccgtggagcttccccagggagccagggggacttttgccgcagccatgaagggggcacgctggaggagggtcccctgggtgtccctgagctgcctgtgtctctgcctccttccgcatgtggtcccaggaaccacagaggacacattaataactggaagtaaaactgctgccccagtcacctcaacaggctcaacaacagcgacactagagggacaatcaactgcagcttcttcaaggacctctaatcaggacatatcagcttcatctcagaaccaccagactaagagcacggagaccaccagcaaagctcaaaccgacaccctcacgcagatgatgacatcaactcttttttcttccccaagtgtacacaatgtgatggagacagctcctccagatgaaatgaccacatcatttccctccagtgtcaccaacacactcatgatgacatcaaagactataacaatgacaacctccacagactccactcttggaaacacagaagagacatcaacagcaggaactgaaagttctaccccagtgacctcagcagtctcaataacagctggacaggaaggacaatcacgaacaacttcctggaggacctctatccaagacacatcagcttcttctcagaaccactggactcggagcacgcagaccaccagggaatctcaaaccagcaccctaacacacagaaccacttcaactccttctttctctccaagtgtacacaatgtgacagggactgtttctcagaagacatctccttcaggtgaaacagctacctcatccctctgtagtgtcacaaacacatccatgatgacatcagagaagataacagtgacaacctccacaggctccactcttggaaacccaggggagacatcatcagtacctgttactggaagtcttatgccagtcacctcagcagccttagtaacatttgatccagaaggacaatcaccagcaactttctcaaggacttctactcaggacacaacagctttttctaagaaccaccagactcagagcgtggagaccaccagagtatctcaaatcaacaccctcaacaccctcacaccggttacaacatcaactgttttatcctcaccaagtggattcaacccaagtggaacagtttctcaggagacattcccttctggtgaaacaaccacctcatccccttccagtgtcagcaatacattcctggtaacatcaaaggtgttcagaatgccaacctccagagactctactcttggaaacacagaggagacatcactatctgtaagtggaaccatttctgcaatcacttccaaagtttcaaccatatggtggtcagacactctgtcaacagcactctcccccagttctctacctccaaaaatatccacagctttccacacccagcagagtgaaggtgcagagaccacaggacggcctcatgagaggagctcattctctccaggtgtgtctcaagaaatatttactctacatgaaacaacaacatggccttcctcattctccagcaaaggccacacaacttggtcacaaacagaactgccctcaacatcaacaggtgctgccactaggcttgtcacaggaaatccatctacagggacagctggcactattccaagggtcccctctaaggtctcagcaataggggaaccaggagagcccaccacatactcctcccacagcacaactctcccaaaaacaacaggggcaggcgcccagacacaatggacacaagaaacggggaccactggagaggctcttctcagcagcccaagctacagtgtgactcagatgataaaaacggccacatccccatcttcttcacctatgctggatagacacacatcccaacaaattacaacggcaccatcaacaaatcattcaacaatacattccacaagcacctctcctcaggaatcaccagctgtttcccaaaggggtcacactcaagccccgcagaccacacaagaatcacaaaccacgaggtccgtctcccccatgactgacaccaagacagtcaccaccccaggttcttccttcacagccagtgggcactcgccctcagaaattgttcctcaggacgcacccaccataagtgcagcaacaacctttgccccagctcccaccggggatggtcacacaacccaggccccgaccacagcactgcaggcagcacccagcagccatgatgccaccctggggccctcaggaggcacgtcactttccaaaacaggtgcccttactctggccaactctgtagtgtcaacaccagggggcccagaaggacaatggacatcagcctctgccagcacctcacctgacacagcagcagccatgacccatacccaccaggctgagagcacagaggcctctggacaaacacagaccagcgaaccggcctcctcagggtcacgaaccacctcagcgggcacagctaccccttcctcatccggggcgagtggcacaacaccttcaggaagcgaaggaatatccacctcaggagagacgacaaggttttcatcaaacccctccagggacagtcacacaacccagtcaacaaccgaattgctgtccgcctcagccagtcatggtgccatcccagtaagcacaggaatggcgtcttcgatcgtccccggcacctttcatcccaccctctctgaggcctccactgcagggagaccgacaggacagtcaagcccaacttctcccagtgcctctcctcaggagacagccgccatttcccggatggcccagactcagaggacaagaaccagcagagggtctgacactatcagcctggcgtcccaggcaaccgacaccttctcaacagtcccacccacacctccatcgatcacatccactgggcttacatctccacaaaccgagacccacactctgtcaccttcagggtctggtaaaaccttcaccacggccctcatcagcaacgccacccctcttcctgtcacctacgcttcctcggcatccacaggtcacaccacccctcttcatgtcaccgatgcttcctcagtatccacaggtcacgccacccctcttcctgtcaccagcccttcctcagtatccacaggtcacaccacccctcttcctgtcaccgacacttcctcagaatccacaggtcacgtcacccctcttcctgtcaccagcttttcctcagcatccacaggtgacagcacccctcttcctgtcactgacacttcctcagcatccacaggtcacgtcacccctcttcctgtcaccagcctttcctcagcatccacaggtgacaccacccctcttcctgtcactgacacttcctcagcatccacaggtcacgccacctctcttcctgtcaccgacacttcctcagtatccacaggtcacaccacccctcttcctgtcaccgacacttcctcagcatccacaggtcacgccacctctcttcctgtcaccgacacttcctcagtatccacaggtcacaccacccctcttcatgtcactgatgcttcctcagcatccacaggtcaggccacccctcttcctgtcaccagcctttcctcagtatccacaggtgacaccacgcctcttcctgtcactagcccttcctcagcatccacaggtcacgccacccctcttcttgtcaccgacacttcctcagcatccacaggacacgccacccctcttcctgtcaccgacgcttcctcagtgtccacagatcacgccacctctcttcctgtaaccatcccttccgcagcatccacaggtcacaccacccctcttcctgtcaccgacacttcctcagcatccacaggtcaggccacctctcttcttgtcaccgacacttcctcagtatccacaggtgacaccacgcctcttcctgtcactagcacttcctcagcatccacaggtcacgtcactcctcttcatgtcaccagcccttcctcagcatccacaggtcacgccacccctcttcctgtcaccagcctttcctcagcatccacaggtgacaccatgcctcttcctgtcactagcccttcctcagcatccacaggtgacaccacccctcttcctgtcaccgacgcttcctcagtatccacaggtcacaccacccctcttcatgtcactgatgcttcctcagcatccacaggtcaggccacccctcttcctgtcaccagcctttcctcagtatccacaggtgacaccacgcctcttcctgtcactagcccttcctcagcatccacaggtcacgccacccctcttcttgtcaccgacacttcctcagcatccacaggacacgccacccctcttcctgtcaccgacgcttcctcagtgtccacagatcacgccacctctcttcctgtaaccatcccttccgcagcatccacaggtcacaccacccctcttcctgtcaccgacacttcctcagcatccacaggtcaggccacctctcttcttgtcaccgacacttcctcagtatccacaggtgacaccacgcctcttcctgtcactagcacttcctcagcatccacaggtcacgtcactcctcttcatgtcaccagcccttcctcagcatccacaggtcacgccacccctcttcctgtcaccagcctttcctcagcatccacaggtgacaccatgcctcttcctgtcactagcccttcctcagcatccacaggtgacaccacccctcttcctgtcaccgacgcttcctcagtatccacaggtcacaccacccctcttcctgtcaccagcccttcctcagcatctacaggtcacaccacccctcttcctgtcaccgacacttcctcagcatccaaaggtgacaccacccctcttcctgtcaccagcccttcctcagcatctacaggtcacaccacccctcttcctgtcaccgacacttcctcagcatccacaggtgacaccacccctcttcctgtcaccaatgcttcctcattatccacaggtcacgccacccctcttcatgtcaccagcccttcctcagcatccacaggtcacgccacccctcttcctgtcaccagcacttcctcagcatccaccggtcacgccacccctcttcctgtcaccggcctttcctcagctaccacagatgacaccacccgtcttcctgtcaccgacgtttcctcggcatccacaggtcaggccacccctcttcctgtcaccagcctttcctcagtatccacaggtgacaccacgcctcttcctgtcactagcccttcctcagcatccacaggtcacgccagccctcttcttgtcactgacgcttcctcagcatccacaggtcaggccacccctcttcctgtcaccgacacttcctcagtatccacagctcacgccaccccacttcctgtcaccggcctttcttcagcttccacagatgacaccacccgtcttcctgtcaccgacgtttcctcggcatccacaggtcaggccatccctcttcctgtcaccagcccttcctcagcatccacaggtgacaccacccctcttcctgtcaccgacgcttcctcagcatccacaggtgacaccacctctcttcctgtcaccatcccttcctcagcatcttcaggtcacaccacctctcttcctgtcaccgacgcttcctcagtgtccacaggtcacgccacctctcttcttgtcaccgacgcttcctcagtatccacaggtgacaccacccctcttcctgtcaccgacactaactcagcatccacaggtgacaccacccctcttcatgtcaccgacgcttcctcagtatccacaggtcacgccacctctcttcctgtcaccagcctttcctcagcatccacaggtgacaccacgcctcttcctgtcactagcccttcctcagcatcctcaggtcacaccacccctcttcctgtcaccgacgcttcctcagtacccacaggtcacgccacctctcttcctgtcaccgacgcttcctcagtgtccacaggtcacgccacccctcttcctgtcaccgacgcttcctcagtgtccacaggtcatgccacccctcttccggtcaccgacacttcctcagtatctacaggacaggccacccctcttcctgtcaccagcctttcctcagcatccactggtgacaccacgccgcttcctgtcaccgatacttcctcagcatccacaggtcaggacacccctcttcctgtcaccagcctttcctcagtatccacaggtgacaccacgcctcttcctgtcactaacccttcctcagcatccacaggtcacgccacccctcttcttgtcaccgacgcttcctcaatatccacaggtcacgccacctctcttcttgtcaccgacgcttcctcagtatccacaggtcacgccaccgctcttcatgacaccgatgcttcctcattatccacaggggacaccacccctcttcctgtcaccagcccttcctcaacatccacaggtgacaccacccctcttcctgtcaccgaaacttcctcagtatccacaggtcacgccacctctcttcctgtcaccgacacttcctcagcatccacaggtcacgccacctctcttcctgtcaccgacacttcctcagcatccacaggtcacgccacccctcttcctgtcaccgacacttcctcagcatccacaggtcaggccacccctcttcctgtcaccagcccttcctcagcatccacaggtcacgccatccctcttcttgtcaccgacacttcctcagcatccacaggacaggccacccctcttcctgtcaccagcctttcctcagcatccacaggtgacaccacccctcttcctgtcaccgacgcttcctcagtgtccacaggtcacgccacctctcttcctgtcaccagcctttcctcagtatccacaggtgacaccactcctcttcctgtcactagcccttcctcagcatccacaggtcacgccacccctcttcatgtcaccgacgcttcctcagcatccacaggtcacgccacccctcttcctgtcaccagcctttcctcagcatccacaggtgacaccacgcctcttcctgtcactagcccttcctcagcatccacaggtcacgccacccctcttcatgtcaccgacgcttcctcagtatccacaggtgacaccacccctcttcctgtcaccagctcttcctcagcatcctcaggtcacaccacccctcttcctgtcaccgacgcttcctcagcatccacaggtgacaccacccctcttcctgtcaccgacacttcctcagcatccacaggtcacgccacccatcttcctgtcaccggcctttcctcagcttccacaggtgacaccacccgtcttcctgtcaccaacgtttcctcggcatccacaggtcatgccacccctcttcctgtcaccagcacttcctcagcatccacaggtgacaccacccctcttcctggcaccgacacttcctcagtatccacaggtcacaccacccctcttcttgtcaccgacgcttcgtcagtatccacaggtgacaccacccgtcttcctgtcaccagcccttcctcagcatctacaggtcacaccacccctctacctgtcaccgacactccctcagcatccacaggtgacaccacccctcttcctgtcaccaatgcttcctcattatccacacgtcacgccacctctcttcatgtcaccagcccttcctcagcatccacaggtcacgccacctctcttcctgtcaccgacacttccgcagcatccacaggtcacgccacccctcttcctgtcaccagcacttcctcagcatccacaggtgacaccacccctcttcctgtcaccgacacttactcagcatccacaggtcaggccacccctcttcctgtcaccagcctttcctcagtatccacaggtgacaccacgcctcttcctgtcactagcccttcctcagcatccacaggtcacgccactcctcttcttgtcaccgacgcttcctcagcatccacaggtcaggccacccctcttcctgtcaccagcctttcctcagtatccacaggtgacaccacgcctcttcctgtcactagcccttcctcagcatccaccggtcatgccacctctcttcctgtcaccgacacttcctcagcatccacaggtgacaccacctctcttcctgtcaccgacacttcctcagcatacacaggtgacaccacctctcttcctgtcaccgacacttcctcatcatccacaggtgacaccacccctcttcttgtcaccgagacttcctcagtatccacaggtgacaccacccctcttcctgtcaccgacacttcctcagcatccacaggtcacgccacccctcttcctgtcaccaacacttcctcagtatccacaggtcacgccacccctcttcatgtcaccagcccttcctcagcatccacaggtcacaccacccctcttcctgtcaccgacgcttcgtcagtgtccacaggtcacgccacctctcttcctgtcaccgacgcttcctcagtgttcacaggtcatgccacctctcttcctgtcaccatcccttcctcagcatcctcaggtcacaccacccctcttcctgtcaccgacgcttcctcagtgtccacaggtcacgccacctctcttcctgtcaccgacgcttcctcagtgtccacaggtcatgccacccctcttcctgtcaccgacgcttcctcagtgtccacaggtcacgctacccctcttcctctcaccagcctttcctcagtatccacaggtgacaccacgcctcttcctgtcaccgacacttcctcagcatccacaggtcaggccacccctcttcctgtcaccagcctttcctcagtatccacaggtgacaccacccctcttcctgtcaccgacacttcctcagcatccacaggtcacgccacctctcttcctgtcaccgacacttcctcagcatccacaggtcacgccacccctcttcctgacaccgacacttcctcagcatccacaggtcacgccacccttcttcctgtcaccgacacttcctcagcatccataggtcacgccacctctcttcctgtcaccgacacttcctcaatatccacaggtcacgccacccctcttcatgtcaccagcccttcctcagcatccaccggtcacgccaccccgcttcctgtcaccgacacttcctcagcatccacaggtcacgccaaccctcttcatgtcaccagcccttcctcagcatccaccggtcacgccaccccgcttcctgtcaccgacacttcctcagcatccacaggtcacgccacccctcttcctgtcaccagcctttcctcagtatccacaggtgacaccacgcctcttcctgtcactagcccttcctcagcatccacaggtcacaccacccctcttcctgtcaccgacacttcctcagcatccacaggtcaggccaccgctcttcctgtcaccagcacttcctcagcatccacaggtgacaccacccctcttcctgtcaccgacacttcctcagcatccacaggtcaggccacccctcttcctgtcaccagcctttcctcagtatccacaggtgacaccacgcctcttcctgtcactagcccttcctcagcatccacaggtcacgccactcctcttcttgtcaccgacgcttcctcagcatccacaggtcaggccacccctcttcctgtcaccagcctttcctcagtatccacaggtgacaccacgcctcttcctgtcactagcccttcctcagcatccaccggtcatgccacctctcttcctgtcaccgacacttcctcagcatccacaggtgacaccacctctcttcctgtcaccgacacttcctcagcatacacaggtgacaccacctctcttcctgtcaccgacacttcctcatcatccacaggtgacaccacccctcttcttgtcaccgagacttcctcagtatccacaggtcacgccactcctcttcttgtcaccgacgcttcctcagcatccacaggtcacgccacccctcttcatgtcaccagcccttcctcagcatccacaggtgacaccacccctgtgcctgtcaccgacacttcctcagtatccacaggtcacgccacccctcttcctgtcaccggcctttcctcagcttccacaggtgacaccacccgtcttcctgtcaccgacatttcctcggcatccacaggtcaggccacccctcttcctgtcaccaacacttcctcagtatccacaggtgacaccatgcctcttcctgtcactagcccttcctcagcatccacaggtcacgccacccctcttcctgtcaccagcacttcctcagcatccaccggtcacgccacccctgttcctgtcaccagcacttcctcagcatctacaggtcacaccacccctcttcctgtcaccgacacttcctcagcatccacaggtgacaccacccctcttcctgtcaccagcccttcctcagcatctacaggtcacaccacccctcttcatgtcaccatcccttcctcagcatccacaggtgacaccagcactcttcctgtcaccggcgcttcctcagcatccaccggtcacgccacccctcttcctgtcaccgacacttcctcagtatccaccggtcacgccacgcctcttcctgtcaccagcctttcctcagtatccacaggtgacaccacccctcttcctgtcaccgacgcttcctcggcatccacaggtcaggccacccctcttcctgtcaccagcctttcctcagtatccacaggtgacaccacccctcttcttgtcaccgacgcttcctcagtatccacaggtcacgccacccctcttcctgtcaccgacacttcctcagcatccacaggtgacaccacccgtcttcctgtcacggacacttcctcagcatccacaggtcaggccacccctcttcctgtcaccagcctttcctcagtatccacaggtgacaccacccctcttcttgtcaccgacgcttcctcagtatccacaggtcacgccacccctcttcctgtcaccgacacttcctcagcatccacaggtgacaccacccgtcttcctgtcacggacacttcctcagcatccacaggtcaggccacccctcttcctgtcaccatcccttcctcatcatcctcaggtcacaccacccctcttcctgtcaccagcacttcctcagtatctacaggtcacgtcacccctcttcatgtcaccagcccttcctcagcatccacaggtcacgtcacccctcttcctgtcaccagcacttcctcagcatccacaggtcacgccacccctcttcttgtcaccgacgcttcctcagtgtccacaggtcacgccacgcctcttcctgtcaccgacgcttcctcagcatccacaggtgacaccacccctcttcctgtcaccgacacttcctcagcatccacaggtcaggccacccctcttcctgtcaccagcctttcctcagtatccacaggtgacaccacccctcttcctgtcaccgacgcttcctcagcatccacaggtcacgccacccctcttcctgtcaccatcccttcctcagtatccacaggtgacaccatgcctcttcctgtcactagcccttcctcagcatccacaggtcacgccacccctcttcctgttaccggcctttcctcagcttccacaggtgacaccacccctcttcctgtcaccgacacttcctcagcatccacacgtcacgccacccctcttcctgtcaccgacacttcctcagcttccacagatgacaccacccgtcttcctgtcaccgacgtttcctcggcatccacaggacatgccacccctcttcctgtcaccagcacttcctcagcatccacaggtgacaccacccctcttcctgtcaccgacacttcctcagtatccacaggtcacgccacctctcttcctgtcaccagccgttcctcagcatccacaggtcacgccaccccccttcctgtcaccgacacttcctcagtatccacaggtcacgccacccctcttcctgtcaccagcacttcctcagtatctacaggtcacgccacccctcttcctgtcaccagcccttcctcagcatccacaggtcacgccacccctgttcctgtcaccagcacttcctcagcatccacaggtgacaccacccctcttcctgtcaccaatgcttcctcattatccacaggtcacgccacccctcttcatgtcaccagcccttcctcagcatccagaggtgacaccagcactcttcctgtcaccgatgcttcctcagcatccaccggtcacgccacccctcttcctctcaccagcctttcctcagtatccacaggtgacaccacgcctcttcctgtcaccgacacttcctctgcatccacaggtcaggccacccctcttcctgtcaccagcctttcctcagtatccacaggtgacaccacgcctcttcctgtcaccatcccttcctcagcatcctcaggtcacaccacctctcttcctgtcaccgacgcttcctcagtgtccacaggtcacggcacccctcttcctgtcaccagcacttcctcagcatccacaggtgacaccacccctcttcctgtcaccgacacttcctcagcatccacaggtcacgccacccctcttcctgtcaccgacacttcctcagcatccacaggtcacgccacccctcttcctgtcaccagcctttcctcagtatccacaggtcacgccacccctcttgctgtcagcagtgctacctcagcttccacagtatcctcggactcccctctgaagatggaaacaccaggaatgacaacaccgtcactgaagacagacggtgggagacgcacagccacatcaccaccccccacaacctcccagaccatcatttccaccattcccagcactgccatgcacacccgctccacagctgcccccatccccatcctgcctgagagaggagtttccctcttcccctatggggcaggcgccggggacctggagttcgtcaggaggaccgtggacttcacctccccactcttcaagccggcgactggcttcccccttggctcctctctccgtgattccctctacttcacagacaatggccagatcatcttcccagagtcagactaccagattttctcctaccccaacccactcccaacaggcttcacaggccgggaccctgtggccctggtggctccgttctgggacgatgctgacttctccactggtcgggggaccacattttatcaggaatacgagacgttctatggtgaacacagcctgctagtccagcaggccgagtcttggattagaaagatgacaaacaacgggggctacaaggccaggtgggccctaaaggtcacgtgggtcaatgcccacgcctatcctgcccagtggaccctcgggagcaacacctaccaagccatcctctccacggacgggagcaggtcctatgccctgtttctctaccagagcggtgggatgcagtgggacgtggcccagcgctcaggcaacccggtgctcatgggcttctctagtggagatggctatttcgaaaacagcccactgatgtcccagccagtgtgggagaggtatcgccctgatagattcctgaattccaactcaggcctccaagggctgcagttctacaggctacaccgggaagaaaggcccaactaccgtctcgagtgcctgcagtggctgaagagccagcctcggtggcccagctggggctggaaccaggtctcctgcccttgttcctggcagcagggacgacgggacttacgattccaacccgtcagcataggtcgctggggcctcggcagtaggcagctgtgcagcttcacctcttggcgaggaggcgtgtgctgcagctacgggccctggggagagtttcgtgaaggctggcacgtgcagcgtccttggcagttggcccaggaactggagccacagagctggtgctgccgctggaatgacaagccctacctctgtgccctgtaccagcagaggcggccccacgtgggctgtgctacatacaggcccccacagcccgcctggatgttcggggacccccacatcaccaccttggatggtgtcagttacaccttcaatgggctgggggacttcctgctggtcggggcccaagacgggaactcctccttcctgcttcagggccgcaccgcccagactggctcagcccaggccaccaacttcatcgcctttgcggctcagtaccgctccagcagcctgggccccgtcacggtccaatggctccttgagcctcacgacgcaatccgtgtcctgctggataaccagactgtgacatttcagcctgaccatgaagacggcggaggccaggagacgttcaacgccaccggagtcctcctgagccgcaacggctctgaggtctcggccagcttcgacggctgggccaccgtctcggtgatcgcgctctccaacatcctccacgcctccgccagcctcccgcccgagtaccagaaccgcacggaggggctcctgggggtctggaataacaatccagaggacgacttcaggatgcccaatggctccaccattcccccagggagccctgaggagatgcttttccactttggaatgacctggcagatcaacgggacaggcctccttggcaagaggaatgaccagctgccttccaacttcacccctgttttctactcacaactgcaaaaaaacagctcctgggctgaacatttgatctccaactgtgacggagatagctcatgcatctatgacaccctggccctgcgcaacgcaagcatcggacttcacacgagggaagtcagtaaaaactacgagcaggcgaacgccaccctcaatcagtacccgccctccatcaatggtggtcgtgtgattgaagcctacaaggggcagaccacgctgattcagtacaccagcaatgctgaggatgccaacttcacgctcagagacagctgcaccgacttggagctctttgagaatgggacgttgctgtggacacccaagtcgctggagccattcactctggagattctagcaagaagtgccaagattggcttggcatctgcactccagcccaggactgtggtctgccattgcaatgcagagagccagtgtttgtacaatcagaccagcagggtgggcaactcctccctggaggtggctggctgcaagtgtgacgggggcaccttcggccgctactgcgagggctccgaggatgcctgtgaggagccgtgcttcccgagtgtccactgcgttcctgggaagggctgcgaggcctgccctccaaacctgactggggatgggcggcactgtgcggctctggggagctctttcctgtgtcagaaccagtcctgccctgtgaattactgctacaatcaaggccactgctacatctcccagactctgggctgtcagcccatgtgcacctgccccccagccttcactgacagccgctgcttcctggctgggaacaacttcagtccaactgtcaacctagaacttcccttaagagtcatccagctcttgctcagtgaagaggaaaatgcctccatggcagaagtcaacgcctcggtggcatacagactggggaccctggacatgcgggcctttctccgcaacagccaagtggaacgaatcgattctgcagcaccggcctcgggaagccccatccaacactggatggtcatctcggagttccagtaccgccctcggggcccggtcattgacttcctgaacaaccagctgctggccgcggtggtggaggcgttcttataccacgttccacggaggagtgaggagcccaggaacgacgtggtcttccagcccatctccggggaagacgtgcgcgatgtgacagccctgaacgtgagcacgctgaaggcttacttcagatgcgatggctacaagggctacgacctggtctacagcccccagagcggcttcacctgcgtgtccccgtgcagtaggggctactgtgaccatggaggccagtgccagcacctgcccagtgggccccgctgcagctgtgtgtccttctccatctacacggcctggggcgagcactgtgagcacctgagcatgaaactcgacgcgttcttcggcatcttctttggggccctgggcggcctcttgctgctgggggtcgggacgttcgtggtcctgcgcttctggggttgctccggggccaggttctcctatttcctgaactcagctgaggccttgccttgaaggggcagctgtggcctaggctacctcaagactcacctcatccttaccgcacatttaaggcgccattgcttttgggagactggaaaagggaaggtgactgaaggctgtcaggattcttcaaggagaatgaatactgggaatcaagacaagactataccttatccataggcgcaggtgcacagggggaggccataaagatcaaacatgcatggatgggtcctcacgcagacacacccacagaaggacactagcctgtgcacgcgcgcgtgcacacacacacacacacacacgagttcataatgtggtgatggccctaagttaagcaaaatgcttctgcacacaaaactctctggtttacttcaaattaactctatttaaataaagtctctctgactttttgtgtctccaaaaaaaaaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("MUC4");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 3, 195510342,
				PositionType.ZERO_BASED), "", "CA");
		Annotation annotation1 = new InsertionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(1, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("8108_8109insTG", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Ser2704Alafs*301)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FRAMESHIFT_ELONGATION), annotation1.getEffects());
	}

	/**
	 * annovar: MUC4:uc021xjp.1:exon2:c.6858_6859insCAG:p.T2286delinsTS, chr3:195511593->CTG
	 */
	@Test
	public void testRealWorldCase_uc021xjp_1_second() throws InvalidGenomeChange {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc021xjp.1	chr3	-	195473637	195538844	195474046	195538688	25	195473637,195475772,195477759,195478077,195479243,195479921,195481083,195484017,195485994,195487754,195488354,195488957,195489725,195490303,195490915,195491867,195492140,195493533,195495892,195497086,195498522,195501042,195505173,195505660,195538606,	195474251,195475935,195477983,195478142,195479317,195480101,195481243,195484199,195486132,195487988,195488456,195489125,195489816,195490512,195491035,195491993,195492320,195493622,195496023,195497242,195498687,195501176,195505326,195518368,195538844,	E9PDY6	uc021xjp.1");
		this.builderForward
				.setSequence("ctcttttgtcctcttcccaggttccctggccccttcggagaaacgcacttggttcgggccagccgcctgaggggacgggctcacgtctgctcctcacactgcagctgctgggccgtggagcttccccagggagccagggggacttttgccgcagccatgaagggggcacgctggaggagggtcccctgggtgtccctgagctgcctgtgtctctgcctccttccgcatgtggtcccaggaaccacagaggacacattaataactggaagtaaaactgctgccccagtcacctcaacaggctcaacaacagcgacactagagggacaatcaactgcagcttcttcaaggacctctaatcaggacatatcagcttcatctcagaaccaccagactaagagcacggagaccaccagcaaagctcaaaccgacaccctcacgcagatgatgacatcaactcttttttcttccccaagtgtacacaatgtgatggagacagctcctccagatgaaatgaccacatcatttccctccagtgtcaccaacacactcatgatgacatcaaagactataacaatgacaacctccacagactccactcttggaaacacagaagagacatcaacagcaggaactgaaagttctaccccagtgacctcagcagtctcaataacagctggacaggaaggacaatcacgaacaacttcctggaggacctctatccaagacacatcagcttcttctcagaaccactggactcggagcacgcagaccaccagggaatctcaaaccagcaccctaacacacagaaccacttcaactccttctttctctccaagtgtacacaatgtgacagggactgtttctcagaagacatctccttcaggtgaaacagctacctcatccctctgtagtgtcacaaacacatccatgatgacatcagagaagataacagtgacaacctccacaggctccactcttggaaacccaggggagacatcatcagtacctgttactggaagtcttatgccagtcacctcagcagccttagtaacatttgatccagaaggacaatcaccagcaactttctcaaggacttctactcaggacacaacagctttttctaagaaccaccagactcagagcgtggagaccaccagagtatctcaaatcaacaccctcaacaccctcacaccggttacaacatcaactgttttatcctcaccaagtggattcaacccaagtggaacagtttctcaggagacattcccttctggtgaaacaaccacctcatccccttccagtgtcagcaatacattcctggtaacatcaaaggtgttcagaatgccaacctccagagactctactcttggaaacacagaggagacatcactatctgtaagtggaaccatttctgcaatcacttccaaagtttcaaccatatggtggtcagacactctgtcaacagcactctcccccagttctctacctccaaaaatatccacagctttccacacccagcagagtgaaggtgcagagaccacaggacggcctcatgagaggagctcattctctccaggtgtgtctcaagaaatatttactctacatgaaacaacaacatggccttcctcattctccagcaaaggccacacaacttggtcacaaacagaactgccctcaacatcaacaggtgctgccactaggcttgtcacaggaaatccatctacagggacagctggcactattccaagggtcccctctaaggtctcagcaataggggaaccaggagagcccaccacatactcctcccacagcacaactctcccaaaaacaacaggggcaggcgcccagacacaatggacacaagaaacggggaccactggagaggctcttctcagcagcccaagctacagtgtgactcagatgataaaaacggccacatccccatcttcttcacctatgctggatagacacacatcccaacaaattacaacggcaccatcaacaaatcattcaacaatacattccacaagcacctctcctcaggaatcaccagctgtttcccaaaggggtcacactcaagccccgcagaccacacaagaatcacaaaccacgaggtccgtctcccccatgactgacaccaagacagtcaccaccccaggttcttccttcacagccagtgggcactcgccctcagaaattgttcctcaggacgcacccaccataagtgcagcaacaacctttgccccagctcccaccggggatggtcacacaacccaggccccgaccacagcactgcaggcagcacccagcagccatgatgccaccctggggccctcaggaggcacgtcactttccaaaacaggtgcccttactctggccaactctgtagtgtcaacaccagggggcccagaaggacaatggacatcagcctctgccagcacctcacctgacacagcagcagccatgacccatacccaccaggctgagagcacagaggcctctggacaaacacagaccagcgaaccggcctcctcagggtcacgaaccacctcagcgggcacagctaccccttcctcatccggggcgagtggcacaacaccttcaggaagcgaaggaatatccacctcaggagagacgacaaggttttcatcaaacccctccagggacagtcacacaacccagtcaacaaccgaattgctgtccgcctcagccagtcatggtgccatcccagtaagcacaggaatggcgtcttcgatcgtccccggcacctttcatcccaccctctctgaggcctccactgcagggagaccgacaggacagtcaagcccaacttctcccagtgcctctcctcaggagacagccgccatttcccggatggcccagactcagaggacaagaaccagcagagggtctgacactatcagcctggcgtcccaggcaaccgacaccttctcaacagtcccacccacacctccatcgatcacatccactgggcttacatctccacaaaccgagacccacactctgtcaccttcagggtctggtaaaaccttcaccacggccctcatcagcaacgccacccctcttcctgtcacctacgcttcctcggcatccacaggtcacaccacccctcttcatgtcaccgatgcttcctcagtatccacaggtcacgccacccctcttcctgtcaccagcccttcctcagtatccacaggtcacaccacccctcttcctgtcaccgacacttcctcagaatccacaggtcacgtcacccctcttcctgtcaccagcttttcctcagcatccacaggtgacagcacccctcttcctgtcactgacacttcctcagcatccacaggtcacgtcacccctcttcctgtcaccagcctttcctcagcatccacaggtgacaccacccctcttcctgtcactgacacttcctcagcatccacaggtcacgccacctctcttcctgtcaccgacacttcctcagtatccacaggtcacaccacccctcttcctgtcaccgacacttcctcagcatccacaggtcacgccacctctcttcctgtcaccgacacttcctcagtatccacaggtcacaccacccctcttcatgtcactgatgcttcctcagcatccacaggtcaggccacccctcttcctgtcaccagcctttcctcagtatccacaggtgacaccacgcctcttcctgtcactagcccttcctcagcatccacaggtcacgccacccctcttcttgtcaccgacacttcctcagcatccacaggacacgccacccctcttcctgtcaccgacgcttcctcagtgtccacagatcacgccacctctcttcctgtaaccatcccttccgcagcatccacaggtcacaccacccctcttcctgtcaccgacacttcctcagcatccacaggtcaggccacctctcttcttgtcaccgacacttcctcagtatccacaggtgacaccacgcctcttcctgtcactagcacttcctcagcatccacaggtcacgtcactcctcttcatgtcaccagcccttcctcagcatccacaggtcacgccacccctcttcctgtcaccagcctttcctcagcatccacaggtgacaccatgcctcttcctgtcactagcccttcctcagcatccacaggtgacaccacccctcttcctgtcaccgacgcttcctcagtatccacaggtcacaccacccctcttcatgtcactgatgcttcctcagcatccacaggtcaggccacccctcttcctgtcaccagcctttcctcagtatccacaggtgacaccacgcctcttcctgtcactagcccttcctcagcatccacaggtcacgccacccctcttcttgtcaccgacacttcctcagcatccacaggacacgccacccctcttcctgtcaccgacgcttcctcagtgtccacagatcacgccacctctcttcctgtaaccatcccttccgcagcatccacaggtcacaccacccctcttcctgtcaccgacacttcctcagcatccacaggtcaggccacctctcttcttgtcaccgacacttcctcagtatccacaggtgacaccacgcctcttcctgtcactagcacttcctcagcatccacaggtcacgtcactcctcttcatgtcaccagcccttcctcagcatccacaggtcacgccacccctcttcctgtcaccagcctttcctcagcatccacaggtgacaccatgcctcttcctgtcactagcccttcctcagcatccacaggtgacaccacccctcttcctgtcaccgacgcttcctcagtatccacaggtcacaccacccctcttcctgtcaccagcccttcctcagcatctacaggtcacaccacccctcttcctgtcaccgacacttcctcagcatccaaaggtgacaccacccctcttcctgtcaccagcccttcctcagcatctacaggtcacaccacccctcttcctgtcaccgacacttcctcagcatccacaggtgacaccacccctcttcctgtcaccaatgcttcctcattatccacaggtcacgccacccctcttcatgtcaccagcccttcctcagcatccacaggtcacgccacccctcttcctgtcaccagcacttcctcagcatccaccggtcacgccacccctcttcctgtcaccggcctttcctcagctaccacagatgacaccacccgtcttcctgtcaccgacgtttcctcggcatccacaggtcaggccacccctcttcctgtcaccagcctttcctcagtatccacaggtgacaccacgcctcttcctgtcactagcccttcctcagcatccacaggtcacgccagccctcttcttgtcactgacgcttcctcagcatccacaggtcaggccacccctcttcctgtcaccgacacttcctcagtatccacagctcacgccaccccacttcctgtcaccggcctttcttcagcttccacagatgacaccacccgtcttcctgtcaccgacgtttcctcggcatccacaggtcaggccatccctcttcctgtcaccagcccttcctcagcatccacaggtgacaccacccctcttcctgtcaccgacgcttcctcagcatccacaggtgacaccacctctcttcctgtcaccatcccttcctcagcatcttcaggtcacaccacctctcttcctgtcaccgacgcttcctcagtgtccacaggtcacgccacctctcttcttgtcaccgacgcttcctcagtatccacaggtgacaccacccctcttcctgtcaccgacactaactcagcatccacaggtgacaccacccctcttcatgtcaccgacgcttcctcagtatccacaggtcacgccacctctcttcctgtcaccagcctttcctcagcatccacaggtgacaccacgcctcttcctgtcactagcccttcctcagcatcctcaggtcacaccacccctcttcctgtcaccgacgcttcctcagtacccacaggtcacgccacctctcttcctgtcaccgacgcttcctcagtgtccacaggtcacgccacccctcttcctgtcaccgacgcttcctcagtgtccacaggtcatgccacccctcttccggtcaccgacacttcctcagtatctacaggacaggccacccctcttcctgtcaccagcctttcctcagcatccactggtgacaccacgccgcttcctgtcaccgatacttcctcagcatccacaggtcaggacacccctcttcctgtcaccagcctttcctcagtatccacaggtgacaccacgcctcttcctgtcactaacccttcctcagcatccacaggtcacgccacccctcttcttgtcaccgacgcttcctcaatatccacaggtcacgccacctctcttcttgtcaccgacgcttcctcagtatccacaggtcacgccaccgctcttcatgacaccgatgcttcctcattatccacaggggacaccacccctcttcctgtcaccagcccttcctcaacatccacaggtgacaccacccctcttcctgtcaccgaaacttcctcagtatccacaggtcacgccacctctcttcctgtcaccgacacttcctcagcatccacaggtcacgccacctctcttcctgtcaccgacacttcctcagcatccacaggtcacgccacccctcttcctgtcaccgacacttcctcagcatccacaggtcaggccacccctcttcctgtcaccagcccttcctcagcatccacaggtcacgccatccctcttcttgtcaccgacacttcctcagcatccacaggacaggccacccctcttcctgtcaccagcctttcctcagcatccacaggtgacaccacccctcttcctgtcaccgacgcttcctcagtgtccacaggtcacgccacctctcttcctgtcaccagcctttcctcagtatccacaggtgacaccactcctcttcctgtcactagcccttcctcagcatccacaggtcacgccacccctcttcatgtcaccgacgcttcctcagcatccacaggtcacgccacccctcttcctgtcaccagcctttcctcagcatccacaggtgacaccacgcctcttcctgtcactagcccttcctcagcatccacaggtcacgccacccctcttcatgtcaccgacgcttcctcagtatccacaggtgacaccacccctcttcctgtcaccagctcttcctcagcatcctcaggtcacaccacccctcttcctgtcaccgacgcttcctcagcatccacaggtgacaccacccctcttcctgtcaccgacacttcctcagcatccacaggtcacgccacccatcttcctgtcaccggcctttcctcagcttccacaggtgacaccacccgtcttcctgtcaccaacgtttcctcggcatccacaggtcatgccacccctcttcctgtcaccagcacttcctcagcatccacaggtgacaccacccctcttcctggcaccgacacttcctcagtatccacaggtcacaccacccctcttcttgtcaccgacgcttcgtcagtatccacaggtgacaccacccgtcttcctgtcaccagcccttcctcagcatctacaggtcacaccacccctctacctgtcaccgacactccctcagcatccacaggtgacaccacccctcttcctgtcaccaatgcttcctcattatccacacgtcacgccacctctcttcatgtcaccagcccttcctcagcatccacaggtcacgccacctctcttcctgtcaccgacacttccgcagcatccacaggtcacgccacccctcttcctgtcaccagcacttcctcagcatccacaggtgacaccacccctcttcctgtcaccgacacttactcagcatccacaggtcaggccacccctcttcctgtcaccagcctttcctcagtatccacaggtgacaccacgcctcttcctgtcactagcccttcctcagcatccacaggtcacgccactcctcttcttgtcaccgacgcttcctcagcatccacaggtcaggccacccctcttcctgtcaccagcctttcctcagtatccacaggtgacaccacgcctcttcctgtcactagcccttcctcagcatccaccggtcatgccacctctcttcctgtcaccgacacttcctcagcatccacaggtgacaccacctctcttcctgtcaccgacacttcctcagcatacacaggtgacaccacctctcttcctgtcaccgacacttcctcatcatccacaggtgacaccacccctcttcttgtcaccgagacttcctcagtatccacaggtgacaccacccctcttcctgtcaccgacacttcctcagcatccacaggtcacgccacccctcttcctgtcaccaacacttcctcagtatccacaggtcacgccacccctcttcatgtcaccagcccttcctcagcatccacaggtcacaccacccctcttcctgtcaccgacgcttcgtcagtgtccacaggtcacgccacctctcttcctgtcaccgacgcttcctcagtgttcacaggtcatgccacctctcttcctgtcaccatcccttcctcagcatcctcaggtcacaccacccctcttcctgtcaccgacgcttcctcagtgtccacaggtcacgccacctctcttcctgtcaccgacgcttcctcagtgtccacaggtcatgccacccctcttcctgtcaccgacgcttcctcagtgtccacaggtcacgctacccctcttcctctcaccagcctttcctcagtatccacaggtgacaccacgcctcttcctgtcaccgacacttcctcagcatccacaggtcaggccacccctcttcctgtcaccagcctttcctcagtatccacaggtgacaccacccctcttcctgtcaccgacacttcctcagcatccacaggtcacgccacctctcttcctgtcaccgacacttcctcagcatccacaggtcacgccacccctcttcctgacaccgacacttcctcagcatccacaggtcacgccacccttcttcctgtcaccgacacttcctcagcatccataggtcacgccacctctcttcctgtcaccgacacttcctcaatatccacaggtcacgccacccctcttcatgtcaccagcccttcctcagcatccaccggtcacgccaccccgcttcctgtcaccgacacttcctcagcatccacaggtcacgccaaccctcttcatgtcaccagcccttcctcagcatccaccggtcacgccaccccgcttcctgtcaccgacacttcctcagcatccacaggtcacgccacccctcttcctgtcaccagcctttcctcagtatccacaggtgacaccacgcctcttcctgtcactagcccttcctcagcatccacaggtcacaccacccctcttcctgtcaccgacacttcctcagcatccacaggtcaggccaccgctcttcctgtcaccagcacttcctcagcatccacaggtgacaccacccctcttcctgtcaccgacacttcctcagcatccacaggtcaggccacccctcttcctgtcaccagcctttcctcagtatccacaggtgacaccacgcctcttcctgtcactagcccttcctcagcatccacaggtcacgccactcctcttcttgtcaccgacgcttcctcagcatccacaggtcaggccacccctcttcctgtcaccagcctttcctcagtatccacaggtgacaccacgcctcttcctgtcactagcccttcctcagcatccaccggtcatgccacctctcttcctgtcaccgacacttcctcagcatccacaggtgacaccacctctcttcctgtcaccgacacttcctcagcatacacaggtgacaccacctctcttcctgtcaccgacacttcctcatcatccacaggtgacaccacccctcttcttgtcaccgagacttcctcagtatccacaggtcacgccactcctcttcttgtcaccgacgcttcctcagcatccacaggtcacgccacccctcttcatgtcaccagcccttcctcagcatccacaggtgacaccacccctgtgcctgtcaccgacacttcctcagtatccacaggtcacgccacccctcttcctgtcaccggcctttcctcagcttccacaggtgacaccacccgtcttcctgtcaccgacatttcctcggcatccacaggtcaggccacccctcttcctgtcaccaacacttcctcagtatccacaggtgacaccatgcctcttcctgtcactagcccttcctcagcatccacaggtcacgccacccctcttcctgtcaccagcacttcctcagcatccaccggtcacgccacccctgttcctgtcaccagcacttcctcagcatctacaggtcacaccacccctcttcctgtcaccgacacttcctcagcatccacaggtgacaccacccctcttcctgtcaccagcccttcctcagcatctacaggtcacaccacccctcttcatgtcaccatcccttcctcagcatccacaggtgacaccagcactcttcctgtcaccggcgcttcctcagcatccaccggtcacgccacccctcttcctgtcaccgacacttcctcagtatccaccggtcacgccacgcctcttcctgtcaccagcctttcctcagtatccacaggtgacaccacccctcttcctgtcaccgacgcttcctcggcatccacaggtcaggccacccctcttcctgtcaccagcctttcctcagtatccacaggtgacaccacccctcttcttgtcaccgacgcttcctcagtatccacaggtcacgccacccctcttcctgtcaccgacacttcctcagcatccacaggtgacaccacccgtcttcctgtcacggacacttcctcagcatccacaggtcaggccacccctcttcctgtcaccagcctttcctcagtatccacaggtgacaccacccctcttcttgtcaccgacgcttcctcagtatccacaggtcacgccacccctcttcctgtcaccgacacttcctcagcatccacaggtgacaccacccgtcttcctgtcacggacacttcctcagcatccacaggtcaggccacccctcttcctgtcaccatcccttcctcatcatcctcaggtcacaccacccctcttcctgtcaccagcacttcctcagtatctacaggtcacgtcacccctcttcatgtcaccagcccttcctcagcatccacaggtcacgtcacccctcttcctgtcaccagcacttcctcagcatccacaggtcacgccacccctcttcttgtcaccgacgcttcctcagtgtccacaggtcacgccacgcctcttcctgtcaccgacgcttcctcagcatccacaggtgacaccacccctcttcctgtcaccgacacttcctcagcatccacaggtcaggccacccctcttcctgtcaccagcctttcctcagtatccacaggtgacaccacccctcttcctgtcaccgacgcttcctcagcatccacaggtcacgccacccctcttcctgtcaccatcccttcctcagtatccacaggtgacaccatgcctcttcctgtcactagcccttcctcagcatccacaggtcacgccacccctcttcctgttaccggcctttcctcagcttccacaggtgacaccacccctcttcctgtcaccgacacttcctcagcatccacacgtcacgccacccctcttcctgtcaccgacacttcctcagcttccacagatgacaccacccgtcttcctgtcaccgacgtttcctcggcatccacaggacatgccacccctcttcctgtcaccagcacttcctcagcatccacaggtgacaccacccctcttcctgtcaccgacacttcctcagtatccacaggtcacgccacctctcttcctgtcaccagccgttcctcagcatccacaggtcacgccaccccccttcctgtcaccgacacttcctcagtatccacaggtcacgccacccctcttcctgtcaccagcacttcctcagtatctacaggtcacgccacccctcttcctgtcaccagcccttcctcagcatccacaggtcacgccacccctgttcctgtcaccagcacttcctcagcatccacaggtgacaccacccctcttcctgtcaccaatgcttcctcattatccacaggtcacgccacccctcttcatgtcaccagcccttcctcagcatccagaggtgacaccagcactcttcctgtcaccgatgcttcctcagcatccaccggtcacgccacccctcttcctctcaccagcctttcctcagtatccacaggtgacaccacgcctcttcctgtcaccgacacttcctctgcatccacaggtcaggccacccctcttcctgtcaccagcctttcctcagtatccacaggtgacaccacgcctcttcctgtcaccatcccttcctcagcatcctcaggtcacaccacctctcttcctgtcaccgacgcttcctcagtgtccacaggtcacggcacccctcttcctgtcaccagcacttcctcagcatccacaggtgacaccacccctcttcctgtcaccgacacttcctcagcatccacaggtcacgccacccctcttcctgtcaccgacacttcctcagcatccacaggtcacgccacccctcttcctgtcaccagcctttcctcagtatccacaggtcacgccacccctcttgctgtcagcagtgctacctcagcttccacagtatcctcggactcccctctgaagatggaaacaccaggaatgacaacaccgtcactgaagacagacggtgggagacgcacagccacatcaccaccccccacaacctcccagaccatcatttccaccattcccagcactgccatgcacacccgctccacagctgcccccatccccatcctgcctgagagaggagtttccctcttcccctatggggcaggcgccggggacctggagttcgtcaggaggaccgtggacttcacctccccactcttcaagccggcgactggcttcccccttggctcctctctccgtgattccctctacttcacagacaatggccagatcatcttcccagagtcagactaccagattttctcctaccccaacccactcccaacaggcttcacaggccgggaccctgtggccctggtggctccgttctgggacgatgctgacttctccactggtcgggggaccacattttatcaggaatacgagacgttctatggtgaacacagcctgctagtccagcaggccgagtcttggattagaaagatgacaaacaacgggggctacaaggccaggtgggccctaaaggtcacgtgggtcaatgcccacgcctatcctgcccagtggaccctcgggagcaacacctaccaagccatcctctccacggacgggagcaggtcctatgccctgtttctctaccagagcggtgggatgcagtgggacgtggcccagcgctcaggcaacccggtgctcatgggcttctctagtggagatggctatttcgaaaacagcccactgatgtcccagccagtgtgggagaggtatcgccctgatagattcctgaattccaactcaggcctccaagggctgcagttctacaggctacaccgggaagaaaggcccaactaccgtctcgagtgcctgcagtggctgaagagccagcctcggtggcccagctggggctggaaccaggtctcctgcccttgttcctggcagcagggacgacgggacttacgattccaacccgtcagcataggtcgctggggcctcggcagtaggcagctgtgcagcttcacctcttggcgaggaggcgtgtgctgcagctacgggccctggggagagtttcgtgaaggctggcacgtgcagcgtccttggcagttggcccaggaactggagccacagagctggtgctgccgctggaatgacaagccctacctctgtgccctgtaccagcagaggcggccccacgtgggctgtgctacatacaggcccccacagcccgcctggatgttcggggacccccacatcaccaccttggatggtgtcagttacaccttcaatgggctgggggacttcctgctggtcggggcccaagacgggaactcctccttcctgcttcagggccgcaccgcccagactggctcagcccaggccaccaacttcatcgcctttgcggctcagtaccgctccagcagcctgggccccgtcacggtccaatggctccttgagcctcacgacgcaatccgtgtcctgctggataaccagactgtgacatttcagcctgaccatgaagacggcggaggccaggagacgttcaacgccaccggagtcctcctgagccgcaacggctctgaggtctcggccagcttcgacggctgggccaccgtctcggtgatcgcgctctccaacatcctccacgcctccgccagcctcccgcccgagtaccagaaccgcacggaggggctcctgggggtctggaataacaatccagaggacgacttcaggatgcccaatggctccaccattcccccagggagccctgaggagatgcttttccactttggaatgacctggcagatcaacgggacaggcctccttggcaagaggaatgaccagctgccttccaacttcacccctgttttctactcacaactgcaaaaaaacagctcctgggctgaacatttgatctccaactgtgacggagatagctcatgcatctatgacaccctggccctgcgcaacgcaagcatcggacttcacacgagggaagtcagtaaaaactacgagcaggcgaacgccaccctcaatcagtacccgccctccatcaatggtggtcgtgtgattgaagcctacaaggggcagaccacgctgattcagtacaccagcaatgctgaggatgccaacttcacgctcagagacagctgcaccgacttggagctctttgagaatgggacgttgctgtggacacccaagtcgctggagccattcactctggagattctagcaagaagtgccaagattggcttggcatctgcactccagcccaggactgtggtctgccattgcaatgcagagagccagtgtttgtacaatcagaccagcagggtgggcaactcctccctggaggtggctggctgcaagtgtgacgggggcaccttcggccgctactgcgagggctccgaggatgcctgtgaggagccgtgcttcccgagtgtccactgcgttcctgggaagggctgcgaggcctgccctccaaacctgactggggatgggcggcactgtgcggctctggggagctctttcctgtgtcagaaccagtcctgccctgtgaattactgctacaatcaaggccactgctacatctcccagactctgggctgtcagcccatgtgcacctgccccccagccttcactgacagccgctgcttcctggctgggaacaacttcagtccaactgtcaacctagaacttcccttaagagtcatccagctcttgctcagtgaagaggaaaatgcctccatggcagaagtcaacgcctcggtggcatacagactggggaccctggacatgcgggcctttctccgcaacagccaagtggaacgaatcgattctgcagcaccggcctcgggaagccccatccaacactggatggtcatctcggagttccagtaccgccctcggggcccggtcattgacttcctgaacaaccagctgctggccgcggtggtggaggcgttcttataccacgttccacggaggagtgaggagcccaggaacgacgtggtcttccagcccatctccggggaagacgtgcgcgatgtgacagccctgaacgtgagcacgctgaaggcttacttcagatgcgatggctacaagggctacgacctggtctacagcccccagagcggcttcacctgcgtgtccccgtgcagtaggggctactgtgaccatggaggccagtgccagcacctgcccagtgggccccgctgcagctgtgtgtccttctccatctacacggcctggggcgagcactgtgagcacctgagcatgaaactcgacgcgttcttcggcatcttctttggggccctgggcggcctcttgctgctgggggtcgggacgttcgtggtcctgcgcttctggggttgctccggggccaggttctcctatttcctgaactcagctgaggccttgccttgaaggggcagctgtggcctaggctacctcaagactcacctcatccttaccgcacatttaaggcgccattgcttttgggagactggaaaagggaaggtgactgaaggctgtcaggattcttcaaggagaatgaatactgggaatcaagacaagactataccttatccataggcgcaggtgcacagggggaggccataaagatcaaacatgcatggatgggtcctcacgcagacacacccacagaaggacactagcctgtgcacgcgcgcgtgcacacacacacacacacacacgagttcataatgtggtgatggccctaagttaagcaaaatgcttctgcacacaaaactctctggtttacttcaaattaactctatttaaataaagtctctctgactttttgtgtctccaaaaaaaaaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("MUC4");
		this.infoForward = builderForward.build();
		// RefSeq NM_018406.6

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 3, 195511592,
				PositionType.ZERO_BASED), "", "CTG");
		Annotation annotation1 = new InsertionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(1, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("6858_6859insCAG", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Thr2286_Thr2287insGln)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.INFRAME_INSERTION), annotation1.getEffects());
	}

	/**
	 * annovar: FRG1:uc003izs.3:exon7:c.608_609insGACT:p.K203fs, chr4:190881973->GACT
	 */
	@Test
	public void testRealWorldCase_uc003izs_3_second() throws InvalidGenomeChange {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc003izs.3	chr4	+	190861973	190884359	190862164	190884284	9	190861973,190864356,190873316,190874222,190876191,190878552,190881902,190882976,190884247,	190862226,190864427,190873442,190874280,190876306,190878657,190881994,190883087,190884359,	Q14331	uc003izs.3");
		this.builderForward
				.setSequence("gaaacccggaagtggaactctgagccattcagcgtttgggtgaagacggaggcgggttctacagagacgtaggctgtcagggagtgtttatttcgcgtccgcttctgtttctccgcgcccctgtgctgccccgactcacatactcgtccagaaccggcctcagcctctccgcgcagaagtttcccggagccatggccgagtactcctacgtgaagtctaccaagctcgtgctcaagggaaccaagacgaagagtaagaagaaaaagagcaaagataagaaaagaaaaagagaagaagatgaagaaacccagcttgatattgttggaatctggtggacagtaacaaactttggtgaaatttcaggaaccatagccattgaaatggataagggaacctatatacatgcactcgacaatggtctttttaccctgggagctccacacaaagaagttgatgagggccctagtcctccagagcagtttacggctgtcaaattatctgattccagaatcgccctgaagtctggctatggaaaatatcttggtataaattcagatggacttgttgttgggcgttcagatgcaattggaccaagagaacaatgggaaccagtctttcaaaatgggaaaatggctttgttggcctcaaatagctgctttattagatgcaatgaagcaggggacatagaagcaaaaagtaaaacagcaggagaagaagaaatgatcaagattagatcctgtgctgaaagagaaaccaagaaaaaagatgacattccagaagaagacaaaggaaatgtaaaacaatgtgaaatcaattatgtaaagaaatttcagagcttccaagaccacaaacttaaaataagtaaagaagacagtaaaattcttaaaaaggctcggaaagatggatttttgcatgagacgcttctggacaggagagccaaattgaaagccgacagatactgcaagtgactgggatttttgtttctgccttatctttctgtgtttttttctgaataaaatattcagaggaaatgcttttacagaaaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("FRG1");
		this.infoForward = builderForward.build();
		// RefSeq NM_004477.2

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 4, 190881973,
				PositionType.ZERO_BASED), "", "GACT");
		Annotation annotation1 = new InsertionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(6, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("608_609insGACT", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Gln204Thrfs*4)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FRAMESHIFT_ELONGATION), annotation1.getEffects());
	}

	/**
	 * annovar: PRDM9:uc003jgo.3:exon11:c.1147_1148insTGA:p.P383delinsLT, chr5:23526344->TGA
	 */
	@Test
	public void testRealWorldCase_uc003jgo_3() throws InvalidGenomeChange {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc003jgo.3	chr5	+	23507723	23528706	23509142	23527882	11	23507723,23509058,23509578,23510028,23517989,23521131,23522412,23522722,23523399,23524442,23526341,	23507821,23509211,23509702,23510136,23518039,23521288,23522514,23522994,23523467,23524636,23528706,	Q9NQV7	uc003jgo.3");
		this.builderForward
				.setSequence("ctctgagagaacgcccggccagggtgaacgccgcggcaggagagcacgggagactgtgaagagcatggggagcctttgtcgtgcagcgtgaaacccttgagcctttggcctaggagctgggagactcagggcccttctcacactcagaattggagcagggccttctagacagtcccagcaccatgagccctgaaaagtcccaagaggagagcccagaagaagacacagagagaacagagcggaagcccatggtcaaagatgccttcaaagacatttccatatacttcaccaaggaagaatgggcagagatgggagactgggagaaaactcgctataggaatgtgaaaaggaactataatgcactgattactataggtctcagagccactcgaccagctttcatgtgtcaccgaaggcaggccatcaaactccaggtggatgacacagaagattctgatgaagaatggacccctaggcagcaagtcaaacctccttggatggccttaagagtggaacagcgtaaacaccagaagggaatgcccaaggcgtcattcagtaatgaatctagtttgaaagaattgtcaagaacagcaaatttactgaatgcaagtggctcagagcaggctcagaaaccagtgtccccttctggagaagcaagtacctctggacagcactcaagactaaaactggaactcaggaagaaggagactgaaagaaagatgtatagcctgcgagaaagaaagggtcatgcatacaaagaggtcagcgagccgcaggatgatgattacctctattgtgagatgtgtcagaacttcttcattgacagctgtgctgcccatgggccccctacatttgtaaaggacagtgcagtggacaaggggcaccccaaccgttcagccctcagtctgcccccagggctgagaattgggccatcaggcatccctcaggctgggcttggagtatggaatgaggcatctgatctgccgctgggtctgcactttggcccttatgagggccgaattacagaagacgaagaggcagccaacaatggatactcctggctgatcaccaaggggagaaactgctatgagtatgtggatggaaaagataaatcctgggccaactggatgaggtatgtgaactgtgcccgggatgatgaagagcagaacctggtggccttccagtaccacaggcagatcttctatagaacctgccgagtcattaggccaggctgtgaactgctggtctggtatggggatgaatacggccaggaactgggcatcaagtggggcagcaagtggaagaaagagctcatggcagggagagaaccaaagccagagatccatccatgtccctcatgctgtctggccttttcaagtcagaaatttctcagtcaacatgtagaacgcaatcactcctctcagaacttcccaggaccatctgcaagaaaactcctccaaccagagaatccctgcccaggggatcagaatcaggagcagcaatatccagatccacacagccgtaatgacaaaaccaaaggtcaagagatcaaagaaaggtccaaactcttgaataaaaggacatggcagagggagatttcaagggccttttctagcccacccaaaggacaaatggggagctgtagagtgggaaaaagaataatggaagaagagtccagaacaggccagaaagtgaatccagggaacacaggcaaattatttgtgggggtaggaatctcaagaattgcaaaagtcaagtatggagagtgtggacaaggtttcagtgttaaatcagatgttattacacaccaaaggacacatacaggggagaagctctacgtctgcagggagtgtgggcggggctttagctggaagtcacacctcctcattcaccagaggatacacacaggggagaagccctatgtctgcagggagtgtgggcggggctttagctggcagtcagtcctcctcactcaccagaggacacacacaggggagaagccctatgtctgcagggagtgtgggcggggctttagccggcagtcagtcctcctcactcaccagaggagacacacaggggagaagccctatgtctgcagggagtgtgggcggggctttagccggcagtcagtcctcctcactcaccagaggagacacacaggggagaagccctatgtctgcagggagtgtgggcggggctttagctggcagtcagtcctcctcactcaccagaggacacacacaggggagaagccctatgtctgcagggagtgtgggcggggctttagctggcagtcagtcctcctcactcaccagaggacacacacaggggagaagccctatgtctgcagggagtgtgggcggggctttagcaataagtcacacctcctcagacaccagaggacacacacaggggagaagccctatgtctgcagggagtgtgggcggggctttcgcgataagtcacacctcctcagacaccagaggacacacacaggggagaagccctatgtctgcagggagtgtgggcggggctttagagataagtcaaacctcctcagtcaccagaggacacacacaggggagaagccctatgtctgcagggagtgtgggcggggctttagcaataagtcacacctcctcagacaccagaggacacacacaggggagaagccctatgtctgcagggagtgtgggcggggctttcgcaataagtcacacctcctcagacaccagaggacacacacaggggagaagccctacgtctgcagggagtgtgggcggggctttagcgataggtcaagcctctgctatcaccagaggacacacacaggggagaagccctacgtctgcagggaggatgagtaagtcattagtaataaaacctcatctcaatagccacaaaaagacaaatgtggtcaccacacacttgcacaccccagctgtgaggtggcttcagcggaagtctgctgaccccttatattccccgagagtataaagagatcggaaataactgattaaacaaatccgccactttcatgactagagatgaggaagaacaagggatagttctgtaagtgttcgggggacatcagcatgtgtggttctttcccgcactgatcccctccattttttgtttgtttttttgcctcctgttctaataaattttgtctccatacaaatctgaaccccaagtgtgtacctcattcttcccttatcactgaaggcaagaagagtccagaagggccacagagaactcatgtgttcagctcaagactccacaggaattcaacccccagaaagacataaacttggagtccgtctggtttaattattggagaatcgattcccaagtccaggaagagaaatgtaagattctagaaagtcgcagcaggaaagggagttccctggtctcctgggaagtgtggcttcttctcctaatggacacctctcctctgctgccatactctcccttggctccccagtctcctctcctgatctcctccaatctctgtagcccaagatgtgaaagccagacaagaacacgcgtgtgtgtatatatgtgttcgggtgtgggggtatgtgccctccgtgtaggtaactgtgtgagtgtggggggtttcaagggtgtgttaggaacaacgctcaaaatcctaaggaaactgaacactcgaacgaaggattcttagcaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("(DM9)");
		this.infoForward = builderForward.build();
		// RefSeq NM_020227.2

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 5, 23526344,
				PositionType.ZERO_BASED), "", "TGA");
		Annotation annotation1 = new InsertionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(10, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("1147_1148insTGA", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Pro383delinsLeuThr)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.DISRUPTIVE_INFRAME_INSERTION), annotation1.getEffects());
	}

	/**
	 * annovar: SCAMP1:uc003kfl.3:exon8:c.730_731insT:p.C244fs, chr5:77745856->T
	 *
	 * -- According to mutalyzer, p.(Asn244Ilefs*52), thus should be p.N244fs (this is what de.charite.compbio.jannovar
	 * says, annovar finds a "C")
	 */
	@Test
	public void testRealWorldCase_uc003kfl_3() throws InvalidGenomeChange {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc003kfl.3	chr5	+	77656338	77776562	77656495	77771491	10	77656338,77684660,77711343,77712364,77714598,77717624,77745756,77745849,77755067,77771326,	77656552,77684738,77711442,77712473,77714727,77717784,77745847,77745857,77755185,77776562,	O15126	uc003kfl.3");
		this.builderForward
				.setSequence("gcaccgggcatgctcagtggcgcggccggctgggtggcccgcgagctgcacccggatctgctcggcggcggcgacgtgagcgcgcaggggggcggcggcctcgcctcgtctctctctctgcgcctgggtcgggtgggtgacgccgagagccagagagatgtcggatttcgacagtaacccgtttgccgacccggatctcaacaatcccttcaaggatccatcagttacacaagtgacaagaaatgttccaccaggacttgatgaatataatccattctcggattctagaacacctccaccaggcggtgtgaagatgcctaatgtacccaatacacaaccagcaataatgaaaccaacagaggaacatccagcttatacacagattgcaaaggaacatgcattggcccaagctgaacttcttaagcgccaggaagaactagaaagaaaagccgcagaattagatcgtcgggaacgagaaatgcaaaacctcagtcaacatggtagaaaaaataattggccacctcttcctagcaattttcctgtcggaccttgtttctatcaggatttttctgtagacattcctgtagaattccaaaagacagtaaagcttatgtactacttgtggatgttccatgcagtaacactgtttctaaatatcttcggatgcttggcttggttttgtgttgattctgcaagagcggttgattttggattgagtatcctgtggttcttgctttttactccttgttcatttgtctgttggtacagaccactttatggagctttcaggagtgacagttcatttagattctttgtattcttcttcgtctatatttgtcagtttgctgtacatgtactccaagctgcaggatttcataactggggcaattgtggttggatttcatcccttactggtctcaaccaaaatattcctgttggaatcatgatgataatcatagcagcacttttcacagcatcagcagtcatctcactagttatgttcaaaaaagtacatggactatatcgcacaacaggtgctagttttgagaaggcccaacaggagtttgcaacaggtgtgatgtccaacaaaactgtccagaccgcagctgcaaatgcagcttcaactgcagcatctagtgcagctcagaatgctttcaagggtaaccagatttaagaatcttcaaacaatacactgttaccttttgactgtacctttttctccagttactgtattctacaaatatttttatgttcaaaacacacagtacagacagcatggatatttcctgttcacttgtgcatgggctaaaaccaggaaaacttccttgtcttattactttacctaatagtttcttaatatttcagtgccccttgcagaaaaaatattacatgctaaataaatattctccatatttttgggggatgacattcagtgaattatttcagtggtgacccactgaaaattaataatggtacttatgattaaaaacgcatttaatactaactgcagtagttctttcaagaatctttagagataaggattgcacattggaaaagtaaaccatgtttcattcctttttccctatttatattgaaagaaataggccagcagagacttagggattttaaattggcttgctttttagctgtttcagtcaccagtgaagagcctatgtgcattttgtagtagataatgtaaaatttgtcatctttttcttttcttttttttagaatagctgatattttgataacaatctctaatttgcatgggcaccacatttcttatattaaaagaattagtgttttggcttctgtactgcttatggttgtaggattcaggggttaatggaatcacagaaatgatattctgcaagaatttcttttaaataaaaagtttgggggtgcaatataagaagtttatataatatgcagtacattatccaaaagagaaggtagttaatgcagtagaaagtagtggtaataattcctttttaaaaaaatttcggtagtcatatagtaacattttgctatatgaaaactttggtatattctgtggttacaactaagattgtgtctggcagctcttttttggggatgtgtgtgtgtgatttttaacagaggtattaaaggctagcctaactgttgtctaaaaagattgtacagtatttaagggattttccttttagcttttcatctccagtggcattaaacataaaaagaccctggcattttttcacatacttgaatccctaaatgcacctgtctttcactttttgagacagactgaatatatctaaaatttccagcaataaaaaaaaaagcatttaacttgcaccaagcaagaaaatataaatacagttaactgcattaagataatcacgttaaaattgttactatgcagcacagaacttcattcttatagtattcttgggttcaacctttgaatcaattttaccactgattaaataaatgactcaaagacatctgtaagtcatgctgctgtgttttgaaagtctttaactaaattaagattgcagaatgatagtgattattcaattagattttaagtaaggattgtgatattagaggctggaaatccttattttttaaaaaatcagataggcataaatagttaaatcactttcattctccccaaacctgtagttacagaaaaagttttatgctagaggtgggatgccaagttttcactatccatgaagcagcgctgcatgtcactaggtaacacagatccatccagatggtgtttacatttgatttatttgggatcttattgacatcaggtatacttggaagacatttcttttattcttcagcgtatgaatttaaagctattttttgtaaatatttctaatcagcgataatttctacctatgttctcaaccaacttagccagtttgtttttcagagcctgtagtcttattggaaatctattttatcagtgtgctttattgagtgtggattttgcatacattcaaaacattaaccacaaaatacagcaagtgcacctatattcaccattaacttatatcccaagtccattttttcctgtacactacaaacaaaagatatattagagacttttgaaaaatgctgaaatactttgcttcagaattggaatgtttatattatgtagaaatcttcaaaggtagcattattaaatagcaaagaataattagaacccacatatctttttttgtgtggatggggaaaatgttttaaaatccagttatttaatatgagtttgagagagaaaattgttttttaaaaatatatgtgcattgaaatgatggcaatgcttatagtatgatcaagtatgaaaggaactttaaattcttatatttacttttctctcagtaaattgttaaattttcactcagcaaaagattggcatttgttaagtgttctatatttagtactaaaatcacagtcatgaaatcatagtcataaaatggtcttcacacagcagtcatccgtgtcatttatcattttgtaatattaaattatggcaattttatttcaaactaaagtttgaacaccggaaagtcattactcagtgatttgtaatttgggacttggattatttatctagagatgtttgtatattttgtcagtaactaatactgcgctgccatcatggtgactgtcatggttctacagaaatgccctccatgtgtccctctaatgttgcatgtttcagtgggttggaagttttgtatatttattgtattaacacagagtgtcataaaataaaatgctgtttactggatgtttgtttgtataattttgaacactataatagcaattcagagacagacattgttaaaggtttgatgtatatagaaattccatgtttgattttttaaaatatgtgtataagtctgtcatgtgctaaacaaaataatatgaaagacctagttaaaaattctaaccaatgtaaaatgaccatttttctgttgcattagacctttacaggtaatggaacatgagcttcacccatattaaatattttggcccctttaaggtcaaaatacagatcatctagaagttagattcaaaatggaaaacctattcatggctcagatttttcattgtgggttaaaaatgggtgtctctgtactagtattgtatttattcaattgaacttgtattctgatttctatccttgctacctattgctgttttatgtactgatgaaagtacctatttgtgtatattggatttttcacttggttagctaaagaagatgtaaaaatatctaaaataatgttcatggtgaatcttattttgagaaatacatgttaaaaaaggaacagtattctttattttctgggtgttatatttaaaaaagcaagtttggatttttacacctaatttactaggaaaatattttattctgtaattcatgttaagattatgtatggtttgcattttaaggggatttatgttaggttaattagttgtttctgtaaatcatttgtaatagcatagtgctttttactcattgctgtatctttttctgaaaacactgttgttaacatctaattcagtatccttattggtacaaatctgtgtttggcatgactgtttatatacagaatttgttacattttgagcattttttcccctgcttatgtataccttagagttaccatggctgtcatataccatttcactatatctcctttcagtttttccttaaggaaaatgtttagaggaatttgttcatttcatgtgattaagccctttagagatgaaataagattggttaattttaaaaaaattgaggatggttaaaaaatagaaaacaccttactttgatacattttaaagtacaatagtatacatttatttagagtagactaatgtgtttaaaacatgagttgttttaaatacttttttattgagctaaaaagttttatctcacatattaagtattacagaaagtgaagtattttggctagaattttagggcatattttataaagcagcatgcctgtaatattggtgggtatttttaaactttaggactttatcacagtatgtagagagctagaaataaatctagaaactttctaagccaggtattgccactaacctgtcttatataagcagatacctcttatttgaagattgtaggaaaatagagaaagactgttctccagttttctcacccccgctgtgggttttatatttacaatttaactttggggtttgggtaagacaaacatttaatgtataggattttggccaggtgtggtggctcacgcctgtaatcccagcactttgggaggccaaggtgggcggatcacgaggtcaggagatcgaaaccatcctggctaacatggtgaaaccccatctctactaaaaatacaaaaaaaaaattagctgagcatggtggcgggcgcctgtagtcccagctacttgggaggctgaggcaggagaatgacgtgaacctgggaggcggagcttgcagtgagccgagatctctccactgcactccagcctgggcgacagagcgagactccatctcaagaaaaaaaaaaaagaattttcattagtgctggccgtgtttcaaatggcaagggaacatgggaactatcatgtggcaatgtagtgagtgttaaactttgtgtttgtccaaatcctgatttatttttcagttcatatctttctgggcttgacatggctgatggtgtagctgaaaccctcctaacactaaaagccatttaatcttttctgtaataggagcagaaaatagttaatcatccacctagtaatataagattactgtgaatattatcttctatacattaaaacagttctagtttgtagaataataccatacaagttttatttttaaattctagttattttcagtgcttacttaaatgtaattctagaattcctccacaacttttaatattttgtatgccagtgattctcaagataaatcatgattgtagtagttgttactgttggcagtttgtagtagtattcaggtattttggggatgggggaaaacaccaaaaatcagtgtcttttatctggtgatcactgtggtatctacagtattctagtctcctgcacaaaaactgaacccactgggcctatgcatccctcacactttttttctagtataaaagcaatacataatgtgttgtagaacaattaaaaattcagaaagtgatacatgagaaaataaaaataaatccttaattctgtca"
						.toUpperCase());
		this.builderForward.setGeneSymbol("SCAMP1");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 5, 77745856,
				PositionType.ZERO_BASED), "", "T");
		Annotation annotation1 = new InsertionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(7, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("730_731insT", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Asn244Ilefs*52)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FRAMESHIFT_VARIANT), annotation1.getEffects());
	}

	/**
	 * annovar: PCDHB10:uc003lix.3:exon1:c.1806_1807insATGC:p.L602fs, chr5:140573931->ATGC
	 */
	@Test
	public void testRealWorldCase_uc003lix_3() throws InvalidGenomeChange {
		this.builderForward = TranscriptModelFactory.parseKnownGenesLine(refDict,
				"uc003lix.3	chr5	+	140571951	140575213	140572125	140574528	1	140571951,	140575213,	Q9UN67	uc003lix.3");
		this.builderForward
				.setSequence("gaagacacggacagatgaacttaaaagagaagctttagctgccaaagattgggaaagggaaaggacaaaaaagacccctgggctacacggcgtaggtgcagggtttcctactgctgttcttttatgctgggagctgtggctgtaaccaactaggaaataacgtatgcagcagctatggctgtcagagagttgtgcttcccaagacaaaggcaagtcctgtttctttttcttttttggggagtgtccttggcaggttctgggtttggacgttattcggtgactgaggaaacagagaaaggatcctttgtggtcaatctggcaaaggatctgggactagcagagggggagctggctgcaaggggaaccagggtggtttccgatgataacaaacaatacctgctcctggattcacataccgggaatttgctcacaaatgagaaactggaccgagagaagctgtgtggccctaaagagccctgtatgctgtatttccaaattttaatggatgatccctttcagatttaccgggctgagctgagagtcagggatataaatgatcacgcgccagtatttcaggacaaagaaacagtcttaaaaatatcagaaaatacagctgaagggacagcatttagactagaaagagcacaggatccagatggaggacttaacggtatccaaaactacacgatcagccccaactcttttttccatattaacattagtggcggtgatgaaggcatgatatatccagagctagtgttggacaaagcactggatcgggaggagcagggagagctcagcttaaccctcacagcgctggatggtgggtctccatccaggtctgggacctctactgtacgcatcgttgtcttggacgtcaatgacaatgccccacagtttgcccaggctctgtatgagacccaggctccagaaaacagccccattgggttccttattgttaaggtatgggcagaagatgtagactctggagtcaacgcggaagtatcctattcattttttgatgcctcagaaaatattcgaacaacctttcaaatcaatcctttttctggggaaatctttctcagagaattgcttgattatgagttagtaaattcttacaaaataaatatacaggcaatggacggtggaggcctttctgcaagatgtagggttttagtggaagtattggacaccaatgacaatccccctgaactgatcgtatcatcattttccaactctgttgctgagaattctcctgagacgccgctggctgtttttaagattaatgacagagactctggagaaaatggaaagatggtttgctacattcaagagaatctgccattcctactaaaaccttctgtggagaatttttacatcctaattacagaaggcgcgctggacagagagatcagagccgagtacaacatcactatcaccgtcactgacttggggacacccaggctgaaaaccgagcacaacataacggtcctggtctccgacgtcaatgacaacgcccccgccttcacccaaacctcctacaccctgttcgtccgcgagaacaacagccccgccctgcacatcggcagcgtcagcgccacagacagagactcgggcaccaacgcccaggtcacctactcgctgctgccgccccaagacccgcacctgcccctcgcctccctggtctccatcaacgcggacaacggccacctgttcgccctcaggtcgctggactacgaggccctgcaggctttcgagttccgcgtgggcgccacagaccgcggctcccccgcgctgagcagagaggcgctggtgcgcgtgctggtgctggacgccaacgacaactcgcccttcgtgctgtacccgctgcagaacggctccgcgccctgcaccgagctggtgccccgggcggccgagccgggctacctggtgaccaaggtggtggcggtggacggcgactcgggccagaacgcctggctgtcgtaccagctgctcaaggccacggagcccgggctgttcggtgtgtgggcgcacaatggggaggtgcgcaccgccaggctgctgagcgagcgcgacgcagccaagcacaggctcgtggtgcttgtcaaggacaatggcgagcctcctcgctcggccaccgccacgctgcacttgctcctggtggacggcttctcccagccctacctgcctctcccggaggcggccccggcccaggcccaggccgaggccgacttgctcaccgtctacctggtggtggcgttggcctcggtgtcttcgctcttcctcctctcggtgctcctgttcgtggcggtgcggctgtgcaggaggagcagggcggcctcggtgggtcgctgctcggtgcccgagggtccttttccagggcatctggtggacgtgaggggcgctgagaccctgtcccagagctaccagtatgaggtgtgtctgacgggaggccccgggaccagtgagttcaagttcttgaaaccagttatttcggatattcaggcacagggccctgggaggaagggtgaagaaaattccaccttccgaaatagctttggatttaatattcagtaaagtctgtttttagtttcatatacttttggtgtgttacatagccatgtttctattagtttacttttaaatctcaaatttaagttattatgcaacttcaagcattattttcaagtagtatacccctgtggttttacaatgtttcatcatttttttgcattaataacaactgggtttaatttaatgagtatttttttctaaatgatagtgttaaggttttaattctttccaactgcccaaggaattaattactattatatctcattacagaaatctgaggttttgattcatttcagagcttgcatctcatgattctaatcacttctgtctatagtgtacttgctctatttaagaaggcatatctacatttccaaactcattctaacattctatatattcgtgtttgaaaaccatgtcatttatttctacatcatgtatttaaaaagaaatatttctctactactatgctcatgacaaaatgaaacaaagcatattgtgagcaatactgaacatcaataatacccttagtttatatacttattattttatctttaagcatgctacttttacttggccaatattttcttatgttaacttttgctgatgtataaaacagactatgccttataattgaaataaaattataatctgcctgaaaatgaataaaaataaaacattttgaaatgtgaaaaaaaaaaaaaaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("(DHB10)");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 5, 140573931,
				PositionType.ZERO_BASED), "", "ATGC");
		Annotation annotation1 = new InsertionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(0, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("1806_1807insATGC", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Ser603Metfs*144)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FRAMESHIFT_ELONGATION), annotation1.getEffects());
	}

	/**
	 * annovar: AK098012:uc003nrp.1:exon2:c.254_255insCAAA:p.P85fs, chr6:30782220->TTTG
	 */
	@Test
	public void testRealWorldCase_uc003nrp_1() throws InvalidGenomeChange {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(refDict,
						"uc003nrp.1	chr6	-	30780642	30798436	30781987	30782473	2	30780642,30798291,	30782549,30798436,	uc003nrp.1");
		this.builderForward
				.setSequence("attccaggaagcgtttgcacatcctggccccagaagattttgcgtagcctcaccaagagaagaagttggaaagaaaaaaaaagaagttggcaaggcatccagaggagaccccgccagcacctgggttctctgccaggggagagagttatgaagaagacttgagcaggcagtccagagctgatacagctgctccaatcaccagagatgcaatctcctgctagatgtcttcacatctacctttctatgtcacatgtgcaagatggttgctctgccttcagacatcacatttacattccaggcagaaaggagaaggaaagagcaaaagatgtgttccgaacaaatccatcttaaaaacaaacaaggggaggggtagcaggagaaaatttaaaaagaagaagaagaaaaacaaaaacaggaaagccgttgtttccctaaaagccccatcaagccgacttctaccggtgactcattacccagtcatgtggccacccctggattcaaggcagtctgaggaggtgagtgtttcaaagaggcacatggatgccacagacaaaatcaagtttccatggttgaaggagaagaggacgatagagtttggggaggcagcaggctacgtctgccacagaagatataaagatatggagccacacgagaagatgggggaggctgccaaaaaagtccactgggtacaggtgaggagcaggtgacattggagttgctccatctgaggagcccgcacctgcaatggcttgtgctcctgctcctgtgggtataggcacaaggctgatgccgagagagtgagttgtttctgttggggttcagaacacaataccccaaagtatggcgatgagtacttaactaaaggacattggaaggcctcagaagcagcctcagatccaaggtccctctgacctgctcctgccctcctgtctctctcgtccctcgttctcccctgaagtgagtcatagaaaccagaatcctcttccccacggtaggtcctagaaactagaaccctttttccccgaagcaaaagccataaaaactggaattattactctaaactctccttgactttttgtccagaagatggccataaagaaattctcagctaggtgtggtggctcgtatctgtaatcccagcactttgagaggccaaggtgggaggatcacttgaggccaggagtttgagatcggcctgggcaacatagtaagacccccatctctacaaaacttttttttttaaacttacctaagcgtggtggtacacctgtagtctcagctacttaggaggctgaggtgggagggaccctgctctggctgtgactgctttatcacagggagtgcctatctgtgggctgctttctacacattaaagcattttcagaaagggccaacatcagccttggaaaaaaggaggaagagccacggagagtctccctttatcttccccttatgtgatgtgagagtgcacatgactgtgtgcacatacacacacacacacacaccacacacacacaccccacaggcacacagggtcaagaacatgctgggcagagcttaagccactgaatgtttcagtaggaatcatgatatctaaaaataggacagctgataagggggttcttactactcacagttcctggaattatatgcaattgcatcttggaaactcaacaaataaacagttagtatggaattaaggaattaagtttggtgaagttcaaagaagctgatgttactcacaagagtaaatagagaggaaaccgggcgtggtggcttcacgcctgtaattctagcactttgggaggccaagacgagcagatcacttgaggtagagagtttgagaccagcctggccaacatgatgaaaccctgttctctactaaaaatacaaaaattagctgggtgtggtggcaggcacctgcaatcccagctacatgggaggctgaggcaggagaatcgcttgaacccaggagacaggggttgcagtgagccaagatcacaccactgcactccagcctgggcgacagagcaagactccctctcaggaaag"
						.toUpperCase());
		this.builderForward.setGeneSymbol("AK098012");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 6, 30782220,
				PositionType.ZERO_BASED), "", "TTTG");
		Annotation annotation1 = new InsertionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(1, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("255_256insAACA", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Val86Asnfs*13)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FRAMESHIFT_ELONGATION), annotation1.getEffects());
	}

	/**
	 * annovar: PRICKLE4:uc011duf.1:exon8:c.863_864insTCT:p.L288delinsLL, chr6:41754575->TCT
	 */
	@Test
	public void testRealWorldCase_uc011duf_1() throws InvalidGenomeChange {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc011duf.1	chr6	+	41748499	41755563	41751211	41754867	8	41748499,41749087,41751199,41751868,41752672,41753074,41753865,41754499,	41748564,41749258,41751343,41751976,41752810,41753278,41754070,41755563,	Q2TBC4-3	uc011duf.1");
		this.builderForward
				.setSequence("gaccgggtgaggggcctgacggcagagtcggtccagaactgcccggacagcgacgcacagcgaggagccaaaatggaagagggtgcaagtcccctcctggggccccagacgccccctcctttcttgctgcctggagcaggggcagtgctgttagtggctactcctggtgtgaacagcccatcctggccaccttccacaggaaacctgacctgaaggacagaagaggaaggaagcaggctttgccacaaatgtcagtgcagaactctggctggccccaccaagaagacagccccaagccccaggatccaggtccaccagccaactcagacagtgactcaggccacctgccgggggaggaccctgaggatacccatgctcagggtcctgcagttctgagcttgggttccctttgcctggacaccaaccaagcccccaactggactggacttcagaccctcctgcagcaactccctccgcaggacattgatgagcgctactgcctggcccttggggaggaggagcgggccgagctgcagctcttctgtgccaggcggaagcaggaagccctgggacagggggtagcccgcctggtacttcccaagcttgaaggacacacctgtgagaagtgtagggagctgctgaagccaggggagtacggagtgtttgcagcccgggcaggggaacagcgctgctggcaccagccttgctttgcctgccaggcctgtggccaggccctgataaacctcatctacttctaccatgatggacaactctactgcggccgtcatcatgcagagttgctgcgcccgcgctgcccggcttgtgaccagctgatcttctcctggcgctgcaccgaggcggagggacagcgctggcatgagaaccacttctgttgccaggactgcgccgggcctctgggcgggggacgttatgccctgcctgggggaagcccctgctgccccagctgcttcgagaaccgctactcggatgcaggctcgagctgggccggggcactggaagggcaggcattccttggggagactggactcgaccgaactgaaggaagggaccaaacctcggtgaactctgcaaccctctcccgaacactcctcgctgctgccggcggttccagcctgcaaactcagagggggctgcctggatccagtccccagcaggagaaccgacctggggacaaagcggaggcacccaaagggcaggagcaatgccgcctggagactattcgtgatcccaaggacacccctttctccacctgctcctcctcctctgactcggaacctgaaggatttttcttaggcgagcgccttccccagtcctggaagacccccggaagcctccaagcagaggacagcaacgcctctaagacgcactgcaccatgtgctagtggcgcagctcagagaggggatgtgagtgggaggaaaggggtctgtaaagcgggagaacaaggctagcctccccctaacaatcctagactgagacgcagtcaggcgcacgcccgcaagaggcggcgaggtgacaagtttggagtgcgcccccttcagtactgcgcgttctaagacttttggcggagactttcttggcaaaacccattccccaaagctacgcttcccctgctgagatagcccct"
						.toUpperCase());
		this.builderForward.setGeneSymbol("(ICKLE4)");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 6, 41754575,
				PositionType.ZERO_BASED), "", "TCT");
		Annotation annotation1 = new InsertionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(7, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("863_864insTCT", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Leu288dup)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.DISRUPTIVE_INFRAME_INSERTION,
				VariantEffect.DIRECT_TANDEM_DUPLICATION), annotation1.getEffects());
	}

	/**
	 * annovar: AEBP1:uc003tkb.3:exon1:c.118_119insAAAA:p.G40fs, chr7:44144382->AAAA
	 */
	@Test
	public void testRealWorldCase_uc003tkb_4() throws InvalidGenomeChange {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc003tkb.4	chr7	+	44143959	44154164	44144264	44153860	21	44143959,44146144,44147037,44147227,44147407,44147605,44148497,44148705,44148886,44149613,44149805,44150323,44150511,44150752,44151105,44151452,44151740,44152156,44152589,44152850,44153192,	44144517,44146486,44147109,44147299,44147530,44147683,44148575,44148783,44148940,44149723,44149945,44150408,44150656,44150838,44151229,44151649,44151920,44152508,44152729,44152950,44154164,	Q8IUX7	uc003tkb.4");
		this.builderForward
				.setSequence("cggctatccgcgcgggagtgcgccacgcggggccggagcgcctattagccgccaggacctcggagcgccccgaccacccctgagcccctctggcttcggagccccccagcaccccttcccgggtcccctcgcccaccctaatccactctccctccctttcccggattccctcgctcaccccatcctctctcccgccccttcctggattccctcacccgtctcgatcccctctccgccctttcccagagacccagagcccctgaccccccgcgccctccccggagccccccgcgcgtgccgcggccatggcggccgtgcgcggggcgcccctgctcagctgcctcctggcgttgctggccctgtgccctggagggcgcccgcagacggtgctgaccgacgacgagatcgaggagttcctcgagggcttcctgtcagagctagaacctgagccccgggaggacgacgtggaggccccgccgcctcccgagcccaccccgcgggtccgaaaagcccaggcggggggcaagccagggaagcggccagggacggccgcagaagtgcctccggaaaagaccaaagacaaagggaagaaaggcaagaaagacaaaggccccaaggtgcccaaggagtccttggaggggtcccccaggccgcccaagaaggggaaggagaagccacccaaggccaccaagaagcccaaggagaagccacctaaggccaccaagaagcccaaggagaagccacccaaggccaccaagaagcccaaagagaagccacccaaggccaccaagaagcccccgtcagggaagaggccccccattctggctccctcagaaaccctggagtggccactgcccccaccccccagccctggccccgaggagctaccccaggagggaggggcgcccctctcaaataactggcagaatccaggagaggagacccatgtggaggcacgggagcaccagcctgagccggaggaggagaccgagcaacccacactggactacaatgaccagatcgagagggaggactatgaggactttgagtacattcggcgccagaagcaacccaggccacccccaagcagaaggaggaggcccgagcgggtctggccagagccccctgaggagaaggccccggccccagccccggaggagaggattgagcctcctgtgaagcctctgctgcccccgctgccccctgactatggtgatggttacgtgatccccaactacgatgacatggactattactttgggcctcctccgccccagaagcccgatgctgagcgccagacagacgaagagaaggaggagctgaagaaacccaaaaaggaggacagcagccccaaggaggagaccgacaagtgggcagtggagaagggcaaggaccacaaagagccccgaaagggcgaggagttggaggaggagtggacgcctacggagaaagtcaagtgtccccccattgggatggagtcacaccgtattgaggacaaccagatccgagcctcctccatgctgcgccacggcctgggggcacagcgcggccggctcaacatgcagaccggtgccactgaggacgactactatgatggtgcgtggtgtgccgaggacgatgccaggacccagtggatagaggtggacaccaggaggactacccggttcacaggcgtcatcacccagggcagagactccagcatccatgacgattttgtgaccaccttcttcgtgggcttcagcaatgacagccagacatgggtgatgtacaccaacggctatgaggaaatgacctttcatgggaacgtggacaaggacacacccgtgctgagtgagctcccagagccggtggtggctcgtttcatccgcatctacccactcacctggaatggcagcctgtgcatgcgcctggaggtgctggggtgctctgtggcccctgtctacagctactacgcacagaatgaggtggtggccaccgatgacctggatttccggcaccacagctacaaggacatgcgccagctcatgaaggtggtgaacgaggagtgccccaccatcacccgcacttacagcctgggcaagagctcacgaggcctcaagatctatgccatggagatctcagacaaccctggggagcatgaactgggggagcccgagttccgctacactgctgggatccatggcaacgaggtgctgggccgagagctgttgctgctgctcatgcagtacctgtgccgagagtaccgcgatgggaacccacgtgtgcgcagcctggtgcaggacacacgcatccacctggtgccctcactgaaccctgatggctacgaggtggcagcgcagatgggctcagagtttgggaactgggcgctgggactgtggactgaggagggctttgacatctttgaagatttcccggatctcaactctgtgctctggggagctgaggagaggaaatgggtcccctaccgggtccccaacaataacttgcccatccctgaacgctacctttcgccagatgccacggtatccacggaggtccgggccatcattgcctggatggagaagaaccccttcgtgctgggagcaaatctgaacggcggcgagcggctagtatcctacccctacgatatggcccgcacgcctacccaggagcagctgctggccgcagccatggcagcagcccggggggaggatgaggacgaggtctccgaggcccaggagactccagaccacgccatcttccggtggcttgccatctccttcgcctccgcacacctcaccttgaccgagccctaccgcggaggctgccaagcccaggactacaccggcggcatgggcatcgtcaacggggccaagtggaacccccggaccgggactatcaatgacttcagttacctgcataccaactgcctggagctctccttctacctgggctgtgacaagttccctcatgagagtgagctgccccgcgagtgggagaacaacaaggaggcgctgctcaccttcatggagcaggtgcaccgcggcattaagggggtggtgacggacgagcaaggcatccccattgccaacgccaccatctctgtgagtggcattaatcacggcgtgaagacagccagtggtggtgattactggcgaatcttgaacccgggtgagtaccgcgtgacagcccacgcggagggctacaccccgagcgccaagacctgcaatgttgactatgacatcggggccactcagtgcaacttcatcctggctcgctccaactggaagcgcatccgggagatcatggccatgaacgggaaccggcctatcccacacatagacccatcgcgccctatgaccccccaacagcgacgcctgcagcagcgacgcctacaacaccgcctgcggcttcgggcacagatgcggctgcggcgcctcaacgccaccaccaccctaggcccccacactgtgcctcccacgctgccccctgcccctgccaccaccctgagcactaccatagagccctggggcctcataccgccaaccaccgctggctgggaggagtcggagactgagacctacacagaggtggtgacagagtttgggaccgaggtggagcccgagtttgggaccaaggtggagcccgagtttgagacccagttggagcctgagtttgagacccagctggaacccgagtttgaggaagaggaggaggaggagaaagaggaggagatagccactggccaggcattccccttcacaacagtagagacctacacagtgaactttggggacttctgagatcagcgtcctaccaagaccccagcccaactcaagctacagcagcagcacttcccaagcctgctgaccacagtcacatcacccatcagcacatggaaggcccctggtatggacactgaaaggaagggctggtcctgcccctttgagggggtgcaaacatgactgggacctaagagccagaggctgtgtagaggctcctgctccacctgccagtctcgtaagagatggggttgctgcagtgttggagtaggggcagagggagggagccaaggtcactccaataaaacaagctcatggcacggacaaaaaaaaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("AEBP1");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 7, 44144382,
				PositionType.ZERO_BASED), "", "AAAA");
		Annotation annotation1 = new InsertionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(0, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("118_119insAAAA", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Gly40Glufs*10)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FRAMESHIFT_ELONGATION), annotation1.getEffects());
	}

	/**
	 * annovar: MUC12:uc003uxo.3:exon2:c.3442_3443insGTA:p.T1148delinsST, chr7:100637286->GTA
	 */
	@Test
	public void testRealWorldCase_uc003uxo_3() throws InvalidGenomeChange {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc003uxo.3	chr7	+	100612903	100662230	100612903	100661917	12	100612903,100633911,100649751,100651914,100652375,100655576,100656099,100656260,100657193,100658895,100660855,100661875,	100612970,100648800,100649853,100652042,100652440,100655728,100656171,100656423,100657355,100658972,100660944,100662230,	F5GWV9	uc003uxo.3");
		this.builderForward
				.setSequence("atgctggtgatctggattctgacgctggctctccggctctgcgcgtccgttactacagtgacaccaggctcaacagtaaacaccagtattggaggtaatacaacttctgcatccacacccagttcaagcgacccttttaccacctttagtgactatggggtgtcagtcacatttatcacgggctcaactgcaacaaaacacttccttgacagctccacaaactcaggccacagtgaggaatcaacagtatcccacagcggcccaggtgcaactggaacaacactcttcccttcccactctgcaacctcagtttttgttggagaacctaaaacctcacccatcacttcagcctcaatggaaacaacagcgttacctggcagtaccacaacagcaggcctgagtgagaaatctaccaccttctacagtagccccagatcaccagacagaacactctcacctgcccgcacgacaagctcaggcgtcagtgaaaaatcaaccacctcccacagccgaccaggcccaacgcacacaatagcgttccctgacagtaccaccatgccaggcgtcagtcaggaatctacagcttcccacagcatccccggctccacagacacaacactgtcccctggcactaccacaccatcatcccttggtccagaatctactaccttccacagcagcccaggctacactaaaacaacacgcttacctgacaacaccacaacctcaggcctccttgaagcatctacgcccgtccacagcagcactggatcgccacacacaacactgtccccttccagctctacaacccatgagggagaacctaccaccttccagagctggccaagctcaaaggacacttcgcctgcaccttctggtaccacatcagcctttgttaaactatctacaacttatcacagcagcccgagctcaactccaacaacccacttttctgccagctccacaaccttgggccatagtgaggaatcgacaccagtccacagcagcccagttgcaactgcaacaacacccccacctgcccgctccgcgacctcaggccatgttgaagaatctacagcctaccacaggagcccgggctcaactcaaacaatgcacttccctgaaagctccacaacttcaggccatagtgaagaatcagcaactttccacggcagcacaacacacacaaaatcttcaactcctagcaccacagctgccctagcacatacaagctaccacagcagcctgggctcaactgaaacaacacacttccgtgatagctccacaatctcaggccgtagtgaggaatcaaaagcatcccacagcagcccagatgcaatggcaacaacagtcttacctgccggctctacaccctcagttcttgttggagactcgacgccctcacccatcagttcaggctcaatggaaaccacagcgttacccggcagtaccacaaaaccaggcctcagtgagaaatctaccactttctacagtagccccagatcaccagacacaacacacttacctgccagcatgacaagctcaggcgtcagtgaagaatccaccacctcccacagccgaccaggctcaacacacacaacagcattccctggcagtaccaccatgccaggcctcagtcaggaatctacagcttcccacagcagcccaggccccacagacacaacattgtcccctggcagtaccacagcatcatcccttggtccagaatatactaccttccacagccgcccaggctccactgaaacaacactcttacctgacaacaccacagcctcaggactccttgaagcatctatgcccgtccacagcagcaccagatcgccacacacaacactgtcccctgccggctctacaacccgtcagggagaatctaccacattccatagctggccaagctcaaaggacactaggcctgcacctcctactaccacatcagcctttgttgagccatctacaacctcccacggcagcccgagctcaattccaacaacccacatttctgcccgctccacaacctcaggcctcgttgaagaatctacgacctaccacagcagcccgggctcaactcaaacaatgcacttccctgaaagcgacacaacttcaggccgtggtgaagaatcaacaacttcccacagcagcacaacacacacaatatcttcagctcctagcaccacatctgcccttgttgaagaacctaccagctaccacagcagcccgggctcaactgcaacaacacacttccctgacagctccacaacctcaggccgtagtgaggaatcaacagcatcgcacagcagccaagacgcaacgggaacaatagtcctacctgcccgctccacaacctcagttcttcttggagaatctacgacctcacccatcagttcaggctcaatggaaacgacagcgttacccggcagtaccacaacgccaggcctcagtgagagatctaccactttccatagtagccccagatcaccagccacaacactctcacctgccagcacgacaagctcaggcgtcagtgaagaatccaccacctcccgcagccgaccaggctcaacgcacacaacagcattccctgacagcaccaccacgccaggcctcagtcggcattctacaacttcccacagcagcccaggctcaacggatacaacactgttacctgccagcaccaccacctcaggccccagtcaggaatcaacaacttcccacagcagctcaggttcaactgacacagcactgtcccctggcagtaccacagccttatcctttggtcaagaatctacaaccttccacagcaacccaggctccactcacacaacactcttccctgacagcaccacaagctcaggcatcgttgaagcatctacacgcgtccacagcagcactggctcaccacgcacaacactgtcccctgccagctccacaagccctggacttcagggagaatctactgccttccagacccacccagcctcaactcacacaacgccttcacctcctagcaccgcaacagcccctgttgaagaatctacaacctaccaccgcagcccaggctcgactccaacaacacacttccctgccagctccacaacttcgggccacagtgagaaatcaacaatattccacagcagcccagatgcaagtggaacaacaccctcatctgcccactccacaacctcaggtcgtggagaatctacaacctcacgcatcagtccaggctcaactgaaataacaacgttacctggcagtaccacaacaccaggcctcagtgaggcatctaccaccttctacagcagccccagatcaccaaccacaacactctcacctgccagcatgacaagcctgggcgtcggtgaagaatccaccacctcccgtagccaaccaggttctactcactcaacagtgtcacctgccagcaccaccacgccaggcctcagtgaggaatctaccaccgtctacagcagcagccgaggctcaactgaaaccacagtgttccctcacagcaccacaacctcagttcatggtgaagagcctacaaccttccacagccggccagcctcaactcacacaacactgttcactgaggacagcaccacctcgggcctcactgaagaatctacagccttccccggcagcccagcctccacccaaacagggttacctgccacactcacaaccgcagacctcggtgaggaatcaactacctttcccagcagctcaggctcaactggaacaaaactctcacctgcccgctccaccacctctggcctcgttggagaatccacaccctcacgcctcagtccaagctcaaccgaaacaacaactttacctggcagtcccacaacaccaagcctcagtgagaaatcaaccaccttctacactagccccagatcaccagatgcaacactctcacctgcaaccacaacaagctcaggcgtcagcgaagaatccagcacatcccacagtcaaccaggctcaacgcacacaacagcattccctgacagcaccaccacctcagacctcagtcaggaacctacaacttcccacagcagccaaggctcaacagaggcaacactgtcccctggcagtaccacagcctcatcccttggtcaacaatctacaaccttccacagcagcccaggcgacactgaaaccacactcttacctgacgacaccataacctcaggcctcgtggaggcatctacacccacccacagcagcactggctcgctacacacaacactgacccctgccagctccacaagcgctggccttcaggaagaatctaccactttccagagctggccaagctcaagtgacacaacaccttcacctcccggcaccacagcagcccctgttgaagtatccacaacctaccacagccgcccgagctcaactccaacaacacacttttctgccagttccacaaccttgggccgtagtgaggaatcaacaacagtccacagcagcccaggtgcaactggaacagcactcttccctacccgctctgcaacctcagttcttgttggagaacctacaacgtcacccatcagttcaggctcaacggaaacaacagcgttacctggcagtaccacaacagcaggcctgagtgagaaatctaccaccttctacagtagccccagatcaccggacacaacactctcacctgccagcacgacaagctcaggcgtcagtgaagaatccaccacctcccacagccgaccaggctcaacgcacacaacagcattccctggcagtaccaccatgccaggcgtcagtcaggaatctacagcttcccacagcagcccaggctccacagacacaacattgtcccctggcagtaccacagcatcatcccttggtccagaatctactactttccacagcagcccaggctccactgaaacaacactcttacctgacaacaccacagcctcaggcctccttgaagcatctacgcccgtccacagcagcactggatcgccacacacaacactgtcccctgccggctctacaacacgtcagggagaatctaccaccttccagagctggccaagctcaaaggacactatgcctgcacctcctactaccacatcagcctttgttgagctatctacaacctcccacggcagcccgagctcaactccaacaacccacttttctgccagctccacaaccttgggccgtagtgaggaatcgacaacagtccacagcagcccagttgcaactgcaacaacaccctcgcctgcccgctccacaacctcaggcctcgttgaagaatctacggcgtaccacagcagcccgggctcaactcaaacaatgcacttccctgaaagctccacagcttcaggtcgtagtgaagaatcaagaacttcccacagcagcacaacacacacaatatcttcacctcctagcaccacatctgcccttgttgaagaacctaccagctaccacagcagcccgggctcaactgcaacaacacacttccctgacagctccacaacctcaggccgtagtgaggaatcaacagcatcccacagcagccaagacgcaacgggaacaatagtcctacctgcccgctccacaacctcagttcttcttggagaatctacgacctcacccatcagttcaggctcaatggaaacgacagcgttacccggcagtaccacaacgccaggcctcagtgagaaatctaccactttccacagtagccccagatcaccagccacaacactctcacctgccagcacgacaagctcaggcgtcagtgaagaatccaccacctcccacagccgaccaggctcaacgcacacaacagcattccctgacagcaccaccacgccaggcctcagtcggcattctacaacttcccacagcagcccaggctcaacggatacaacactgttacctgccagcaccaccacctcaggccccagtcaggaatcaacaacttcccacagcagcccaggttcaactgacacagcactgtcccctggcagtaccacagccttatcctttggtcaagaatctacaaccttccacagcagcccaggctccactcacacaacgctcttccctgacagcaccacaagctcaggcatcgttgaagcatctacacgcgtccacagcagcactggctcaccacgcacaacactgtcccctgccagctccacaagccctggacttcagggagaatctaccgccttccagacccacccagcctcaactcacacgacgccttcacctcctagcaccgcaacagcccctgttgaagaatctacaacctaccaccgcagcccaggctcgactccaacaacacacttccctgccagctccacaacttcgggccacagtgagaaatcaacaatattccacagcagcccagatgcaagtggaacaacaccctcatctgcccactccacaacctcaggtcgtggagaatctacaacctcacgcatcagtccaggctcaactgaaataacaacgttacctggcagtaccacaacaccaggcctcagtgaggcatctaccaccttctacagcagccccagatcaccaaccacaacactctcacctgccagtatgacaagcctaggcgtcggtgaagaatccaccacctcccgtagccaaccaggttctactcactcaacagtgtcacctgccagcaccaccacgccaggcctcagtgaggaatctaccaccgtctacagcagcagcccaggctcaactgaaaccacagtgttccctcgcacccccacaacctcagttcgtggtgaagagcctacaaccttccacagccggccagcctcaactcacacaacactgttcactgaggacagcaccacctcgggcctcactgaagaatctacagccttccccggcagcccagcctccacccaaacagggttacctgccacactcacaaccgcagacctcggtgaggaatcaactacctttcccagcagctcaggctcaactggaacaacactctcacctgcccgctccaccacctctggcctcgttggagaatccacaccctcacgcctcagtccaagctcaaccgaaacaacaactttacccggcagtcccacaacaccaagcctcagtgagaaatcaaccaccttctacactagccccagatcaccagatgcaacactctcacctgcaaccacaacaagctcaggcgtcagtgaagaatccagcacatcccacagtcaaccaggctcaacgcacacaacagcattccctgacagcaccaccacgccaggcctcagtcggcattctacaacttcccacagcagcccaggctcaacggatacaacactgttacctgccagcaccaccacctcaggccccagtcaggaatcaacaacttcccacagcagcccaggttcaactgacacagcactgtcccctggcagtaccacagccttatcctttggtcaagaatctacaaccttccacagcagcccaggctccactcacacaacactcttccctgacagcaccacaagctcaggcatcgttgaagcatctacacgcgtccacagcagcactggctcaccacgcacaacactgtcccctgccagctccacaagccctggacttcagggagaatctaccaccttccagacccacccagcctcaactcacacgacgccttcacctcctagcaccgcaacagcccctgttgaagaatctacaacctaccaccgcagcccaggctcgactccaacaacacacttccctgccagctccacaacttcgggccacagtgagaaatcaacaatattccacagcagcccagatgcaagtggaacaacaccctcatctgcccactccacaacctcaggtcgtggagaatctacaacctcacgcatcagtccaggctcaactgaaataacaacgttacctggcagtaccacaacaccaggcctcagtgaggcatctaccaccttctacagcagccccagatcaccaaccacaacactctcacctgccagtatgacaagcctaggcgtcggtgaagaatccaccacctcccgtagccaaccaggttctactcactcaacagtgtcacctgccagcaccaccacgccaggcctcagtgaggaatctaccaccgtctacagcagcagcccaggctcaactgaaaccacagtgttccctcgcagcaccacaacctcagttcgtggtgaagagcctacaaccttccacagccggccagcctcaactcacacaacactgttcactgaggacagcaccacctcgggcctcactgaagaatctacagccttccccggcagcccagcctccacccaaacagggttacctgccacactcacaaccgcagacctcggtgaggaatcaactacctttcccagcagctcaggctcaactggaacaacactctcacctgcccgctccaccacctctggcctcgttggagaatccacaccctcacgcctcagtccaagctcaaccgaaacaacaactttacccggcagtcccacaacaccaagcctcagtgagaaatcaaccaccttctacactagccccagatcaccagatgcaacactctcacctgcaaccacaacaagctcaggcgtcagtgaagaatccagcacatcccacagtcaaccaggctcaacgcacacaacagcgttccctgacagcaccaccacctcaggcctcagtcaggaacctacagcttcccacagcagccaaggctcaacagaggcaacactgtcccctggcagtaccacagcctcatcccttggtcaacaatctacaaccttccacagcagcccaggcgacactgaaaccacactcttacctgacgacaccataacctcaggcctcgtggaggcatctacacccacccacagcagcactggctcgctacacacaacactgacccctgccagctccacaagcgctggccttcaggaagaatctaccactttccagagctggccaagctcaagtgacacaacaccttcacctcccggcaccacagcagcccctgttgaagtatccacaacctaccacagccgcccgagctcaactccaacaacacacttttctgccagttccacaaccttgggccgtagtgaggaatcaacaacagtccacagcagcccaggtgcaactggaacagcactcttccctacccgctctgcaacctcagttcttgttggagaacctacaacgtcacccatcagttcaggctcaacggaaacaacagcgttacctggcagtaccacaacagcaggcctgagtgagaaatctaccaccttctacagtagccccagatcaccggacacaacactctcacctgccagcacgacaagctcaggcgtcagtgaagaatccaccacctcccacagccgaccaggctcaacgcacacaacagcattccctggcagtaccaccatgccaggcgtcagtcaggaatctacagcttcccacagcagcccaggctccacagacacaacactgtcccctggcagtaccacagcatcatcccttggtccagaatctactaccttccacagcggcccaggctccactgaaacaacactcttacctgacaacaccacagcctcaggcctccttgaagcatctacgcccgtccacagcagcactggatcgccacacacaacactgtcccctgccggctctacaacccgtcagggagaatctaccaccttccagagctggcctaactcgaaggacactacccctgcacctcctactaccacatcagcctttgttgagctatctacaacctcccacggcagcccgagctcaactccaacaacccacttttctgccagctccacaaccttgggccgtagtgaggaatcgacaacagtccacagcagcccagttgcaactgcaacaacaccctcgcctgcccgctccacaacctcaggcctcgttgaagaatctacgacctaccacagcagcccgggctcaactcaaacaatgcacttccctgaaagcgacacaacttcaggccgtggtgaagaatcaacaacttcccacagcagcacaacacacacaatatcttcagctcctagcaccacatctgcccttgttgaagaacctaccagctaccacagcagcccgggctcaactgcaacaacacacttccctgacagctccacaacctcaggccgtagtgaggaatcaacagcatcccacagcagccaagacgcaacgggaacaatagtcctacctgcccgctccacaacctcagttcttcttggagaatctacgacctcacccatcagttcaggctcaatggaaacgacagcgttacccggcagtaccacaacgccaggcctcagtgagaaatctaccactttccacagtagccccagatcaccagccacaacactctcacctgccagcacgacaagctcaggcgtcagtgaagaatccaccacctcccacagccgaccaggctcaacgcacacaacagcattccctgacagcaccaccacgccaggcctcagtcggcattctacaacttcccacagcagcccaggctcaacggatacaacactgttacctgccagcaccaccacctcaggctccagtcaggaatcaacaacttcccacagcagctcaggttcaactgacacagcactgtcccctggcagtaccacagccttatcctttggtcaagaatctacaaccttccacagcagcccaggctccactcacacaacactcttccctgacagcaccacaagctcaggcatcgttgaagcatctacacgcgtccacagcagcactggctcaccacgcacaacactgtcccctgccagctccacaagccctggacttcagggagaatctaccgccttccagacccacccagcctcaactcacacgacgccttcacctcctagcaccgcaacagcccctgttgaagaatctacaacctaccaccgcagcccaggctcgactccaacaacacacttccctgccagctccacaacttcgggccacagtgagaaatcaacaatattccacagcagcccagatgcaagtggaacaacaccctcatctgcccactccacaacctcaggtcgtggagaatctacaacctcacgcatcagtccaggctcaactgaaataacaacgttacctggcagtaccacaacaccaggcctcagtgaggcatctaccaccttctacagcagccccagatcaccaaccacaacactctcacctgccagtatgacaagcctaggcgtcggtgaagaatccaccacctcccgtagccaaccaggttctactcactcaacagtgtcacctgccagcaccaccacgccaggcctcagtgaggaatctaccaccgtctacagcagcagcccaggctcaactgaaaccacagtgttccctcgcagcaccacaacctcagttcgtcgtgaagagcctacaaccttccacagccggccagcctcaactcacacaacactgttcactgaggacagcaccacctcgggcctcactgaagaatctacagccttccccggcagcccagcctccacccaaacagggttacctgccacactcacaaccgcagacctcggtgaggaatcaactacctttcccagcagctcaggctcaactggaacaaaactctcacctgcccgctccaccacctctggcctcgttggagaatccacaccctcacgcctcagtccaagctcaaccgaaacaacaactttacccggcagtcccacaacaccaagcctcagtgagaaatcaaccaccttctacactagccccagatcaccagatgcaacactctcacctgcaaccacaacaagctcaggcgtcagcgaagaatccagcacatcccacagtcaaccaggctcaacgcacacaacagcgttccctgacagcaccaccacctcaggcctcagtcaggaacctacaacttcccacagcagccaaggctcaacagaggcaacactgtcccctggcagtaccacagcctcatcccttggtcaacaatctacaaccttccacagcagcccaggcgacactgaaaccacactcttacctgacgacaccataacctcaggcctcgtggaggcatctacacccacccacagcagcactggctcgctacacacaacactgacccctgccagctccacaagcactggccttcaggaagaatctaccactttccagagctggccaagctcaagtgacacaacaccttcacctcccagcaccacagcagtccctgttgaagtatccacaacctaccacagccgcccgagctcaactccaacaacacacttttctgccagttccacaaccttgggccgtagtgaggaatcaacaacagtccacagcagcccaggtgcaactggaacagcactcttccctacccgctctgcaacctcagttcttgttggagaacctacaacgtcacccatcagttcaggctcaacggaaacaacagcgttacctggcagtaccacaacagcaggcctgagtgagaaatctaccaccttctacagtagccccagatcaccggacacaacactttcacctgccagcacgacaagctcaggcgtcagtgaagaatccaccacctcccacagccgaccaggctcaatgcacacaacagcattccctagcagtaccaccatgccaggcgtcagtcaggaatctacagcttcccacagcagcccaggctccacagacacaacactgtcccctggcagtaccacagcatcatcccttggtccagaatctactaccttccacagcagcccaggctccactgaaacaacactcttacctgacaacaccacagcctcaggcctccttgaagcatctacacccgtccacagcagcactggatcgccacacacaacactgtcccctgccggctctacaacccgtcagggagaatctaccaccttccagagctggccaaactcgaaggacactacccctgcacctcctactaccacatcagcctttgttgagctatctacaacctcccacggcagcccgagctcaactccaacaacccacttttctgccagctccacaacattgggccgtagtgaggaatcgacaacagtccacagcagcccagttgcaactgcaacaacaccctcgcctgcccgctccacaacctcaggcctcgttgaagaatctacgacctaccacagcagcccgggctcaactcaaacaatgcacttccctgaaagcaacacaacttcaggccgtggtgaagaatcaacaacttcccacagcagcacaacacacacaatatcttcagctcctagcaccacatctgcccttgttgaagaacctaccagctaccacagcagcccgggctcaactgcaacaacacacttccctgacagctccacaacctcaggccgtagtgaggaatcaacagcatcccacagcagccaagacgcaacgggaacaatagtcctacctgcccgctccacaacctcagttcttcttggagaatctacgacctcacccatcagttcaggctcaatggaaacgacagcgttacccggcagtaccacaacgccaggcctcagtgagaaatctaccactttccacagtagcccgagctcaactccaacaacccacttttctgccagctccacaaccttgggccgtagtgaggaatcgacaacagtccacagcagcccagttgcaactgcaacaacaccctcgcctgcccgctccacaacctcaggcctcgttgaagaatctacggcgtaccacagcagcccgggctcaactcaaacaatgcacttccctgaaagctccacagcttcaggtcgtagtgaagaatcaagaacttcccacagcagcacaacacacacaatatcttcacctcctagcaccacatctgcccttgttgaagaacctaccagctaccacagcagcccgggctcaattgcaacaacacactttcctgagagctccacaacctccggccgtagtgaggaatcaacagcatcccacagcagcccagatacaaatggaatcacacccttacctgcccattttactacctcaggccgcattgcagaatctaccaccttctatatctctccaggctcaatggaaacaacattagccagcactgccacaacaccaggcctcagtgcaaaatctaccatcctttacagtagctccagatcaccagaccaaacactctcacctgccagcatgacaagctccagcatcagtggagaacccaccagcttgtatagccaagcagagtcaacacacacaacagcgttccctgccagcaccaccacctcaggcctcagtcaggaatcaacaactttccacagtaagccaggctcaactgagacaacactgtcccctggcagcatcacaacttcatcttttgctcaagaatttaccacccctcatagccaaccaggctcagctctgtcaacagtgtcacctgccagcaccacagtgccaggccttagtgaggaatctaccaccttctacagcagcccaggctcaactgaaaccacagcgttttctcacagcaacacaatgtccattcatagtcaacaatctacacccttccctgacagcccaggcttcactcacacagtgttacctgccaccctcacaaccacagacattggtcaggaatcaacagccttccacagcagctcagacgcaactggaacaacacccttacctgcccgctccacagcctcagaccttgttggagaacctacaactttctacatcagcccatcccctacttacacaacactctttcctgcgagttccagcacatcaggcctcactgaggaatctaccaccttccacaccagtccaagcttcacttctacaattgtgtctactgaaagcctggaaaccttagcaccagggttgtgccaggaaggacaaatttggaatggaaaacaatgcgtctgtccccaaggctacgttggttaccagtgcttgtcccctctggaatccttccctgtagaaaccccggaaaaactcaacgccactttaggtatgacagtgaaagtgacttacagaaatttcacagaaaagatgaatgacgcatcctcccaggaataccagaacttcagtaccctcttcaagaatcggatggatgtcgttttgaagggcgacaatcttcctcagtatagaggggtgaacattcggagattgctcaacggtagcatcgtggtcaagaacgatgtcatcctggaggcagactacactttagagtatgaggaactgtttgaaaacctggcagagattgtaaaggccaagattatgaatgaaactagaacaactcttcttgatcctgattcctgcagaaaggccatactgtgctatagtgaagaggacactttcgtggattcatcggtgactccgggctttgacttccaggagcaatgcacccagaaggctgccgaaggatatacccagttctactatgtggatgtcttggatgggaagctggcctgtgtgaacaagtgcaccaaaggaacgaagtcgcaaatgaactgtaacctgggcacatgtcagctgcaacgcagtggcccccgctgcctgtgcccaaatacgaacacacactggtactggggagagacctgtgaattcaacatcgccaagagcctcgtgtatgggatcgtgggggctgtgatggcggtgctgctgctcgcattgatcatcctaatcatcttattcagcctatcccagagaaaacggcacagggaacagtatgatgtgcctcaagagtggcgaaaggaaggcacccctggcatcttccagaagacggccatctgggaagaccagaatctgagggagagcagattcggccttgagaacgcctacaacaacttccggcccaccctggagactgttgactctggcacagagctccacatccagaggccggagatggtagcatccactgtgtgagccaacgggggcctcccaccctcatctagctctgttcaggagagctgcaaacacagagcccaccacaagcctccggggcgggtcaagaggagaccgaagtcaggccctgaagccggtcctgctctgagctgacagacttggccagtcccctgcctgtgctcctgctggggaaggctgggggctgtaagcctctccatccgggagcttccagactcccagaagcctcggcacccctgtctcctcctgggtggctccccactctggaatttccctaccaataaaagcaaatctgaaagctcagaatgagaagtga"
						.toUpperCase());
		this.builderForward.setGeneSymbol("MUC12()");
		this.infoForward = builderForward.build();
		// RefSeq NM_001164462.1

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 7, 100637286,
				PositionType.ZERO_BASED), "", "GTA");
		Annotation annotation1 = new InsertionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(1, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("3442_3443insGTA", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Ser1147dup)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.DISRUPTIVE_INFRAME_INSERTION,
				VariantEffect.DIRECT_TANDEM_DUPLICATION), annotation1.getEffects());
	}

	/**
	 * annovar: OLFM1:uc010naq.2:exon2:c.328_329insAA:p.G110fs, chr9:137968919->AA
	 */
	@Test
	public void testRealWorldCase_uc010naq_2second() throws InvalidGenomeChange {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc010naq.2	chr9	+	137967088	137969237	137967582	137969065	2	137967088,137968657,	137967648,137969237,	Q6IMJ6	uc010naq.2");
		this.builderForward
				.setSequence("atcctgtcccagcctgcttccccgccggccgccgccctcctccccgggagagagcgaggcgcgcgggtccctctgcgccacccccgcccccgccccttccgagcaaacttttggcacccaccgcagcccagcgcgcgttcgtgctccgcagggcgcgcctctctccgccaatgccaggcgcgcgggggagccattaggaggcgaggagagaggagggcgcagctcccgcccagcccagccctgcccagccctgcccggaggcagacgcgccggaaccgggacgcgataaatatgcagagcggaggcttcgcgcagcagagcccgcgcgccgcccgctccgggtgctgaatccaggcgtggggacacgagccaggcgccgccgccggagccagcggagccggggccagagccggagcgcgtccgcgtccacgcagccgccggccggccagcacccagggccctgcatgccaggtcgttggaggtggcagcgagacatgcacccggcccggaagctcctcagcctcctcttcctcatcctgatgggcactgaactcactcaaaataaaagagaaaacaaagcagagaagatgggagggccagagagcgagaggaagaccacaggagagaagacactgaacgagcttcccttgttttgcctggaagcccacgctggctccctggctctgcccaggatgtgcagtccaaatcccaatccagcagtggggttatgtcgtcccgcttaccctcagagcccttctcctggtgctgcccagacgatcagccagtccctcctggagaggttctgcatggcctctaggagaggttttcttggccccaggaaggcctggtggagggtggtggttgtgcactgttgctggacagatgcattcattcatgtgcacacacacacacacacatgcacacacaggggagcagatacctgcagagaagagccaaccaggtcctgattagtggcaagctgccccacaaagggctatgcctgtgtcttattgagacaccttggcaaagagatggctgattctgggtggtcctggacatggccgcacccaagggccctccaagccttaatggcaccctgaagcctccatgcccaggccaaaagatgcttttcctccctaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("OLFM1");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 9, 137968919,
				PositionType.ZERO_BASED), "", "AA");
		Annotation annotation1 = new InsertionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(1, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("328_329insAA", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Gly110Glufs*51)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FRAMESHIFT_ELONGATION), annotation1.getEffects());
	}

	/**
	 * Mutalyzer: NM_033419.3:n.328dup NM_033419.3(PGAP3_v001):c.285dup NM_033419.3(PGAP3_i001):p.(Phe96Leufs*67)
	 */
	@Test
	public void testRealWorldCase_uc002hsk_3() throws InvalidGenomeChange {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc002hsk.3	chr17	-	37827374	37844310	37829055	37844267	7	37827374,37829303,37829766,37830245,37830869,37842174,37844086,	37829119,37829508,37829903,37830307,37830932,37842272,37844310,	Q96FM1-2	uc002hsk.3");
		this.builderForward
				.setSequence("atactcctaagctcctcccccggcggcgagccagggagaaaggatggccggcctggcggcgcggttggtcctgctagctggggcagcggcgctggcgagcggctcccagggcgaccgtgagccggtgtaccgcgactgcgtactgcagtgcgaagagcagaactgctctgggggcgctctgaatcacttccgctcccgccagccaatctacatgagtctagcaggctggacctgtcgggacgactgtaagtatgagtgtatgtgggtcaccgttgggctctacctccaggaaggtcacaaagtgcctcagttccatggcaaggtgtccctcaatgcatggttctggtccacagttttccacaccagggacactgacctcacagagaaaatggactacttctgtgcctccactgtcatcctacactcaatctacctgtgctgcgtcaggaccgtggggctgcagcacccagctgtggtcagtgccttccgggctctcctgctgctcatgctgaccgtgcacgtctcctacctgagcctcatccgcttcgactatggctacaacctggtggccaacgtggctattggcctggtcaacgtggtgtggtggctggcctggtgcctgtggaaccagcggcggctgcctcacgtgcgcaagtgcgtggtggtggtcttgctgctgcaggggctgtccctgctcgagctgcttgacttcccaccgctcttctgggtcctggatgcccatgccatctggcacatcagcaccatccctgtccacgtcctctttttcagctttctggaagatgacagcctgtacctgctgaaggaatcagaggacaagttcaagctggactgaagaccttggagcgagtctgccccagtggggatcctgcccccgccctgctggcctcccttctcccctcaacccttgagatgattttctcttttcaacttcttgaacttggacatgaaggatgtgggcccagaatcatgtggccagcccaccccctgttggccctcaccagccttggagtctgttctagggaaggcctcccagcatctgggactcgagagtgggcagcccctctacctcctggagctgaactggggtggaactgagtgtgctcttagctctaccgggaggacagctgcctgtttcctccccatcagcctcctccccacatccccagctgcctggctgggtcctgaagccctctgtctacctgggagaccagggaccacaggccttagggatacagggggtccccttctgttaccaccccccaccctcctccaggacaccactaggtggtgctggatgcttgttctttggccagccaaggttcacggcgattctccccatgggatcttgagggaccaagctgctgggattgggaaggagtttcaccctgaccattgccctagccaggttcccaggaggcctcaccatactccctttcagggccagggctccagcaagcccagggcaaggatcctgtgctgctgtctggttgagagcctgccaccgtgtgtcgggagtgtgggccaggctgagtgcataggtgacagggccgtgagcatgggcctgggtgtgtgtgagctcaggcctaggtgcgcagtgtggagacgggtgttgtcggggaagaggtgtggcttcaaagtgtgtgtgtgcagggggtgggtgtgttagcgtgggttaggggaacgtgtgtgcgcgtgctggtgggcatgtgagatgagtgactgccggtgaatgtgtccacagttgagaggttggagcaggatgagggaatcctgtcaccatcaataatcacttgtggagcgccagctctgcccaaggcgccacctgggcggacagccaggagctctccatggccaggctgcctgtgtgcatgttccctgtctggtgcccctttgcccgcctcctgcaaacctcacagggtccccacacaacagtgccctccagaagcagcccctcggaggcagaggaaggaaaatggggatggctggggctctctccatcctccttttctccttgccttcgcatggctggccttcccctccaaaacctccattcccctgctgccagcccctttgccatagcctgattttggggaggaggaaggggcgatttgagggagaaggggagaaagcttatggctgggtctggtttcttcccttcccagagggtcttactgttccagggtggccccagggcaggcaggggccacactatgcctgcgccctggtaaaggtgacccctgccatttaccagcagccctggcatgttcctgccccacaggaatagaatggagggagctccagaaactttccatcccaaaggcagtctccgtggttgaagcagactggatttttgctctgcccctgaccccttgtccctctttgagggaggggagctatgctaggactccaacctcagggactcgggtggcctgcgctagcttcttttgatactgaaaacttttaaggtgggagggtggcaagggatgtgcttaataaatcaattccaagcctca"
						.toUpperCase());
		this.builderForward.setGeneSymbol("(AP3)");
		this.infoForward = builderForward.build();
		// RefSeq NM_033419.4

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 17, 37830926,
				PositionType.ZERO_BASED), "", "G");
		Annotation annotation1 = new InsertionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(2, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("286dup", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Leu96Profs*16)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FRAMESHIFT_VARIANT), annotation1.getEffects());
	}

	/**
	 * This duplication variation should lead to the loss of the translation initiation site
	 *
	 * uc003izs.3 is NM_004477 mutalyzer: NM_004477(FRG1_v001):c.1_2insC NM_004477(FRG1_i001):p.? Note that mutalyzer
	 * says it does not know what happens to the protein but this can be classified as startloss since the start codon
	 * is destroyed.
	 */
	@Test
	public void testRealWorldCase_uc003izs_3_third() throws InvalidGenomeChange {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc003izs.3	chr4	+	190861973	190884359	190862164	190884284	9	190861973,190864356,190873316,190874222,190876191,190878552,190881902,190882976,190884247,	190862226,190864427,190873442,190874280,190876306,190878657,190881994,190883087,190884359,	Q14331	uc003izs.3");
		this.builderForward
				.setSequence("gaaacccggaagtggaactctgagccattcagcgtttgggtgaagacggaggcgggttctacagagacgtaggctgtcagggagtgtttatttcgcgtccgcttctgtttctccgcgcccctgtgctgccccgactcacatactcgtccagaaccggcctcagcctctccgcgcagaagtttcccggagccatggccgagtactcctacgtgaagtctaccaagctcgtgctcaagggaaccaagacgaagagtaagaagaaaaagagcaaagataagaaaagaaaaagagaagaagatgaagaaacccagcttgatattgttggaatctggtggacagtaacaaactttggtgaaatttcaggaaccatagccattgaaatggataagggaacctatatacatgcactcgacaatggtctttttaccctgggagctccacacaaagaagttgatgagggccctagtcctccagagcagtttacggctgtcaaattatctgattccagaatcgccctgaagtctggctatggaaaatatcttggtataaattcagatggacttgttgttgggcgttcagatgcaattggaccaagagaacaatgggaaccagtctttcaaaatgggaaaatggctttgttggcctcaaatagctgctttattagatgcaatgaagcaggggacatagaagcaaaaagtaaaacagcaggagaagaagaaatgatcaagattagatcctgtgctgaaagagaaaccaagaaaaaagatgacattccagaagaagacaaaggaaatgtaaaacaatgtgaaatcaattatgtaaagaaatttcagagcttccaagaccacaaacttaaaataagtaaagaagacagtaaaattcttaaaaaggctcggaaagatggatttttgcatgagacgcttctggacaggagagccaaattgaaagccgacagatactgcaagtgactgggatttttgtttctgccttatctttctgtgtttttttctgaataaaatattcagaggaaatgcttttacagaaaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("FRG1");
		this.infoForward = builderForward.build();
		// RefSeq NM_004477.2

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 4, 190862165,
				PositionType.ZERO_BASED), "", "C");
		Annotation annotation1 = new InsertionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(0, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("1_2insC", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("0?", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.START_LOST), annotation1.getEffects());
	}

	/**
	 * This duplication variation should lead to the loss of the translation initiation site
	 */
	@Test
	public void testRealWorldCase_uc003izs_3_fourth() throws InvalidGenomeChange {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc003izs.3	chr4	+	190861973	190884359	190862164	190884284	9	190861973,190864356,190873316,190874222,190876191,190878552,190881902,190882976,190884247,	190862226,190864427,190873442,190874280,190876306,190878657,190881994,190883087,190884359,	Q14331	uc003izs.3");
		this.builderForward
				.setSequence("gaaacccggaagtggaactctgagccattcagcgtttgggtgaagacggaggcgggttctacagagacgtaggctgtcagggagtgtttatttcgcgtccgcttctgtttctccgcgcccctgtgctgccccgactcacatactcgtccagaaccggcctcagcctctccgcgcagaagtttcccggagccatggccgagtactcctacgtgaagtctaccaagctcgtgctcaagggaaccaagacgaagagtaagaagaaaaagagcaaagataagaaaagaaaaagagaagaagatgaagaaacccagcttgatattgttggaatctggtggacagtaacaaactttggtgaaatttcaggaaccatagccattgaaatggataagggaacctatatacatgcactcgacaatggtctttttaccctgggagctccacacaaagaagttgatgagggccctagtcctccagagcagtttacggctgtcaaattatctgattccagaatcgccctgaagtctggctatggaaaatatcttggtataaattcagatggacttgttgttgggcgttcagatgcaattggaccaagagaacaatgggaaccagtctttcaaaatgggaaaatggctttgttggcctcaaatagctgctttattagatgcaatgaagcaggggacatagaagcaaaaagtaaaacagcaggagaagaagaaatgatcaagattagatcctgtgctgaaagagaaaccaagaaaaaagatgacattccagaagaagacaaaggaaatgtaaaacaatgtgaaatcaattatgtaaagaaatttcagagcttccaagaccacaaacttaaaataagtaaagaagacagtaaaattcttaaaaaggctcggaaagatggatttttgcatgagacgcttctggacaggagagccaaattgaaagccgacagatactgcaagtgactgggatttttgtttctgccttatctttctgtgtttttttctgaataaaatattcagaggaaatgcttttacagaaaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("FRG1");
		this.infoForward = builderForward.build();
		// RefSeq NM_004477.2

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 4, 190862166,
				PositionType.ZERO_BASED), "", "A");
		Annotation annotation1 = new InsertionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(0, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("2_3insA", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("0?", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.START_LOST), annotation1.getEffects());
	}

	//
	// Various UTR 3' Variants Below
	//

	/**
	 * annovar: THAP3 chr1:6693165->TA
	 */
	@Test
	public void testRealWorldCase_uc001aod_3() throws InvalidGenomeChange {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001aod.3	chr1	+	6685209	6693642	6685278	6693137	5	6685209,6688558,6690350,6692450,6692855,	6685352,6688751,6690413,6692555,6693642,	Q8WTV1-3	uc001aod.3");
		this.builderForward
				.setSequence("gtccctcccctctccgcaggccccgccgccgccgccatctttgttgggggcagccaggcctggctcgagatgccgaagtcgtgcgcggcccggcagtgctgcaaccgctacagcagccgcaggaagcagctcaccttccaccggtttccgttcagccgcccggagctgctgaaggaatgggtgctgaacatcggccggggcaacttcaagcccaagcagcacacggtcatctgctccgagcacttccggccagagtgcttcagcgcctttggaaaccgcaagaacctaaagcacaatgccgtgcccacggtgttcgcctttcaggaccccacacaggtgagggagaacacagaccctgccagtgagagaggaaatgccagctcttctcagaaagaaaaggtcctccctgaggcgggggccggagaggacagtcctgggagaaacatggacactgcacttgaagagcttcagttgcccccaaatgccgaaggccacgtaaaacaggtctcgccacggaggccgcaagcaacagaggctgttggccggccgactggccctgcaggcctgagaaggacccccaacaagcagccatctgatcacagctatgcccttttggacttagattccctgaagaaaaaactcttcctcactctgaaggaaaatgaaaagctccggaagcgcttgcaggcccagaggctggtgatgcgaaggatgtccagccgcctccgtgcttgcaaagggcaccagggactccaggccagacttgggccagagcagcagagctgagccccacaggctccggacgcagaggtggcagtggcaccagggccggcagagctttggagctctggctgtggacatttttgtctgctgtggacactgagaaagttggccatgaggcctgcttggccggggatcgagacagtagccaagctccccggcgagagccccaatgccgtctgggggacgtttagaggcgtggcactaggagtgcacatctgtgagcatgacaagcttatcctcccatggtaacagaagtccaggctgaggctgattctggacgctgtcctttcagcacacgcagagcaaagatcgttggaagccccagtgtgggagatgctcctcagggaggaagccatgtgagggggctggctctgtggcgggtgagtggtcccctcctccatcagcctggacagccgctcggggttctaaggagtgactcctgtcccggcctggtgtgagtgggcagtgtaataaagtgtctttctatacggtgtcgctcccatcatcaaaaaaaaaaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("THAP3");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6693165,
				PositionType.ZERO_BASED), "", "TA");
		Annotation annotation1 = new InsertionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(4, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("*28_*29insTA", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.THREE_PRIME_UTR_VARIANT), annotation1.getEffects());
	}

	/**
	 * annovar: RGS21 chr1:192335275->TAAT
	 */
	@Test
	public void testRealWorldCase_uc001gsh_3() throws InvalidGenomeChange {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001gsh.3	chr1	+	192286121	192336414	192312166	192335254	5	192286121,192312106,192316442,192321176,192335050,	192286235,192312177,192316519,192321343,192336414,	Q2M5E4	uc001gsh.3");
		this.builderForward
				.setSequence("gttaccacttggaaaacaattcatctgaaagaagcacagattttctcatctatcctgtcaacaaagaaggaatcaagagagcaaggacagtgatttcccctgcattgcatttgtcttgaagatcagtcagaaagagaaactcggcatcatctgtgacagacagtggaacgaaaaatgccagtgaaatgctgtttctacaggtcaccaactgcggaaacaatgacatggtctgaaaatatggacacgcttttagccaaccaagctggtctagatgcttttcgaatatttctaaaatcagagtttagtgaagaaaatgttgagttctggcttgcctgtgaagactttaagaaaacgaaaaatgcagacaaaattgcttccaaagccaagatgatttattctgaattcattgaagctgatgcacctaaagagattaacattgacttcggtaccagagacctcatctcaaagaatattgctgaaccaacactcaaatgctttgatgaggctcagaaattaatctattgtctcatggccaaggattctttccctcgatttctgaagtcagagatttataaaaaactggtaaatagccaacaggttccaaatcataaaaaatggctcccttttttgtgaggaaggtaaaagttaactaatcactatacttcagggctacaatattttaaatatacaagcatgatgcattgtcttttgttttgtttttaggatttagaaaacattttttacccaaacagatgaataacgttttatacaacaagcctgaatttctaactcagttgtttagaatgtatttgctttaccagctatttaatctcctactgggggagtacaaagaaagtttatagagatacaatatagtcttaaaccaaaactgaatattcttattatattataatgtaaggaattatacatatcttcacgtggcagaatgaaagacttttgagcatcatatacacaattttaaataccattgctttattcaaaaaaatctcacttttgtaaaaagagaatttctgaaccaaaatacaagctttcatttaatatatttaactgttttttttctgccatttctttccaactatttctaataatgtggttatgaaaactgctacgcctctcaaattatattttttaaatcacaggaatgtatacacatttatatgtatgtcttgaatgcaccatggaccaaagtttttcaaaatatatcacttggctcaattcaatggcatcacatataaaatgtgatgagttatgtatgaaaaggcctcaagggtggggaatactgattttcttatgttaacagaaatataaaagaaagtggaagactaaggagcatagataaatccttataagatgaagtatatagcaagtcataaaatttaagaatttgcaacattatctactcaattgtggggaagtatctattcactccttcagcactgatacttgtttataaaacccaaacaatttttaaatgcatttattttgagatgttcctaaaattgtttcattctatatgtaaatatcctgtgataaatacgaataatttcatttcaatatgagaagctgtaaagattcaacagatctcccacgtttccattttctttgcacagatttatttatctgcattgatatttctgcttttagattgtttgaacattaaaaaatggaggaaaaatagcatggcttattttatgttttcacaaactactcatttgatagacaaaattttgtcttcccttcatcatgagaaataaacatttaaacatattcaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("RGS21");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 192335275,
				PositionType.ZERO_BASED), "", "TAAT");
		Annotation annotation1 = new InsertionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(4, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("*18_*21dup", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.THREE_PRIME_UTR_VARIANT), annotation1.getEffects());
	}

	/**
	 * <P>
	 * annovar: FRG1 chr4:190884289->GACA
	 * </P>
	 */
	@Test
	public void testRealWorldCase_uc003izs_3() throws InvalidGenomeChange {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc003izs.3	chr4	+	190861973	190884359	190862164	190884284	9	190861973,190864356,190873316,190874222,190876191,190878552,190881902,190882976,190884247,	190862226,190864427,190873442,190874280,190876306,190878657,190881994,190883087,190884359,	Q14331	uc003izs.3");
		this.builderForward
				.setSequence("gaaacccggaagtggaactctgagccattcagcgtttgggtgaagacggaggcgggttctacagagacgtaggctgtcagggagtgtttatttcgcgtccgcttctgtttctccgcgcccctgtgctgccccgactcacatactcgtccagaaccggcctcagcctctccgcgcagaagtttcccggagccatggccgagtactcctacgtgaagtctaccaagctcgtgctcaagggaaccaagacgaagagtaagaagaaaaagagcaaagataagaaaagaaaaagagaagaagatgaagaaacccagcttgatattgttggaatctggtggacagtaacaaactttggtgaaatttcaggaaccatagccattgaaatggataagggaacctatatacatgcactcgacaatggtctttttaccctgggagctccacacaaagaagttgatgagggccctagtcctccagagcagtttacggctgtcaaattatctgattccagaatcgccctgaagtctggctatggaaaatatcttggtataaattcagatggacttgttgttgggcgttcagatgcaattggaccaagagaacaatgggaaccagtctttcaaaatgggaaaatggctttgttggcctcaaatagctgctttattagatgcaatgaagcaggggacatagaagcaaaaagtaaaacagcaggagaagaagaaatgatcaagattagatcctgtgctgaaagagaaaccaagaaaaaagatgacattccagaagaagacaaaggaaatgtaaaacaatgtgaaatcaattatgtaaagaaatttcagagcttccaagaccacaaacttaaaataagtaaagaagacagtaaaattcttaaaaaggctcggaaagatggatttttgcatgagacgcttctggacaggagagccaaattgaaagccgacagatactgcaagtgactgggatttttgtttctgccttatctttctgtgtttttttctgaataaaatattcagaggaaatgcttttacagaaaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("FRG1");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 4, 190884289,
				PositionType.ZERO_BASED), "", "GACA");
		Annotation annotation1 = new InsertionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(8, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("*5_*6insGACA", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.THREE_PRIME_UTR_VARIANT), annotation1.getEffects());
	}

	// The following variant from the Platinum Genome project caused problems against hg19/ucsc:
	// chr22:20640691:>ATGCCGTGCACGGCATCCTCGTTAGCA
	@Test
	public void testRealWorldCase_uc011aho_1() throws InvalidGenomeChange {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc011aho.1	chr22	-	20640677	20656858	20640677	20656715	8	20640677,20643224,20643723,20645785,20650757,20653788,20654281,20656687,	20641015,20643258,20643785,20645851,20650844,20653813,20654340,20656858,	E7EP86	uc011aho.1");
		this.builderForward
				.setSequence("aatgtttatgccctatcgccatggtgatgggattagggatctcctgcccttggtcgtaagtgccactacctgtgctgagtttttcaaaggtcagagcagattgaaccattgtggtttcattttccctgattttgatttttcttatggggaacctgtgtggctgcattcaaggtgactcgaagaagccttccaaaaagcgtgtgaaaaggaagccctactctactaccaaggtgacttcagggagcacattcaatgagaatacaagaagatatgctgtgcacaccaaccagtgtaggagacctcatggctcccgggtaaagaagaagaggtacccacaagaagatgacttccatcatacagtcttcagcaaccttgaaagattggacaagcttcagcccactcttgaagcctctgaggagtctctagttcacaaggacagaggagatggagagaggccagtcaacgtgaaggtggtgcaggtggcccctctgaggcgtgaatctactccccatgaggacaccgtacacaacatcactaacgaggatgcctcacacgatatcactaacgaggacgctgtccacggcatcgctaacgaggccgccgacaagggcatcgccaacgaggacgccgcccagggcatcgccaacgaggacgccgcccacggaatcgccagcgaggacgccgcccagggcatcgccaacgaggtcgccgcccagggcatcgccaacgaggacgccgcccagggcatcgccaaggaggacgccgcccacggcatcgccaacgaggatgctgcccacggcattgctaacgaggatgccgtgcacggcatcgctaatgaggac"
						.toUpperCase());
		this.builderForward.setGeneSymbol("E7EP86");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, refDict.getContigNameToID()
				.get("22"), 20640690, PositionType.ZERO_BASED), "", "ATGCCGTGCACGGCATCCTCGTTAGCA");
		Annotation annotation1 = new InsertionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		// The following result is equal to the one of Mutalyzer.
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(7, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("660_686dup", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Ala225_Asp233dup)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.DISRUPTIVE_INFRAME_INSERTION,
				VariantEffect.DIRECT_TANDEM_DUPLICATION), annotation1.getEffects());
	}

	// The following variant from the clinvar project caused problems against hg19/ucsc: chr3:37081782:>TAAG
	@Test
	public void testRealWorldCase_uc010hgj_1() throws InvalidGenomeChange {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc010hgj.1	chr3	+	37053501	37081785	37067163	37081783	4	37053501,37067127,37070274,37081676,	37053590,37067498,37070423,37081785,	Q0ZAJ5	uc010hgj.1");
		this.builderForward
				.setSequence("caaggagagacagtagctgatgttaggacactacccaatgcctcaaccgtggacaatattcgctccatctttggaaatgctgttagtcgactttgctaccaggacttgctggcccctctggggagatggttaaatccacaacaagtctgacctcgtcttctacttctggaagtagtgataaggtctatgcccaccagatggttcgtacagattcccgggaacagaagcttgatgcatttctgcagcctctgagcaaacccctgtccagtcagccccaggccattgtcacagaggataagacagatatttctagtggcagggctaggcagcaagatgaggagatgcttgaactcccagcccctgctgaagtggctgccaaaaatcagagcttggagggggatacaacaaaggggacttcagaaatgtcagagaagagaggacctacttccagcaaccccagaaagagacatcgggaagattctgatgtggaaatggtggaagatgattcccgaaaggaaatgactgcagcttgtaccccccggagaaggatcattaacctcactagtgttttgagtctccaggaagaaattaatgagcagggacatgaggttctccgggagatgttgcataaccactccttcgtgggctgtgtgaatcctcagtgggccttggcacagcatcaaaccaagttataccttctcaacaccaccaagcttag"
						.toUpperCase());
		this.builderForward.setGeneSymbol("Q0ZAJ5");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 3, 37081781,
				PositionType.ZERO_BASED), "", "TAAG");
		Annotation annotation1 = new InsertionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();
		// Mutalyzer: NM_001258274.1(MLH1_v001):c.940_941insTAAG NM_001258274.1(MLH1_i001):p.(Glu316*)
		//
		// The UCSC transcript DNA sequence is bogus here.
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(3, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("589_590insTAAG", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Leu198insLeuSer*)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.INFRAME_INSERTION, VariantEffect.STOP_GAINED),
				annotation1.getEffects());
	}

	@Test
	public void testRealWorld_uc002udr_1() throws InvalidGenomeChange {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc002udr.1	chr2	-	167138188	167163109	167138189	167163099	10	167138188,167140962,167142845,167144946,167149740,167151108,167159599,167160747,167162301,167163019,	167138318,167141334,167143133,167145153,167149882,167151172,167159812,167160839,167162430,167163109,	H7C064	uc002udr.1");
		this.builderForward
				.setSequence("cttattcagcatgctcatcatgtgcactattctgacaaactgcatatttatgaccatgaataacccaccggactggaccaaaaatgtcgagtacacttttactggaatatatacttttgaatcacttgtaaaaatccttgcaagaggcttctgtgtaggagaattcacttttcttcgtgacccgtggaactggctggattttgtcgtcattgtttttgcgtatttaacagaatttgtaaacctaggcaatgtttcagctcttcgaactttcagagtattgagagctttgaaaactatttctgtaatcccaggcctgaagacaattgtaggggctttgatccagtcagtgaagaagctttctgatgtcatgatcctgactgtgttctgtctgagtgtgtttgcactaattggactacagctgttcatgggaaacctgaagcataaatgttttcgaaattcacttgaaaataatgaaacattagaaagcataatgaataccctagagagtgaagaagactttagaaaatatttttattacttggaaggatccaaagatgctctcctttgtggtttcagcacagattcaggtcagtgtccagaggggtacacctgtgtgaaaattggcagaaaccctgattatggctacacgagctttgacactttcagctgggccttcttagccttgtttaggctaatgacccaagattactgggaaaacctttaccaacagacgctgcgtgctgctggcaaaacctacatgatcttctttgtcgtagtgattttcctgggctccttttatctaataaacttgatcctggctgtggttgccatggcatatgaagaacagaaccaggcaaacattgaagaagctaaacagaaagaattagaatttcaacagatgttagaccgtcttaaaaaagagcaagaagaagctgaggcaattgcagcggcagcggctgaatatacaagtattaggagaagcagaattatgggcctctcagagagttcttctgaaacatccaaactgagctctaaaagtgctaaagaaagaagaaacagaagaaagaaaaagaatcaaaagaagctctccagtggagaggaaaagggagatgctgagaaattgtcgaaatcagaatcagaggacagcatcagaagaaaaagtttccaccttggtgtcgaagggcataggcgagcacatgaaaagaggttgtctacccccaatcagtcaccactcagcattcgtggctccttgttttctgcaaggcgaagcagcagaacaagtctttttagtttcaaaggcagaggaagagatataggatctgagactgaatttgccgatgatgagcacagcatttttggagacaatgagagcagaaggggctcactgtttgtgccccacagaccccaggagcgacgcagcagtaacatcagccaagccagtaggtccccaccaatgctgccggtgaacgggaaaatgcacagtgctgtggactgcaacggtgtggtctccctggttgatggacgctcagccctcatgctccccaatggacagcttctgccagaggtgataatagataaggcaacttctgatgacagcggcacgaccaatcaaatacacaagaaaaggcgttgtagttcctatctcctttcagaggatatgctgaatgatcccaacctcagacagagagcaatgagtagagcaagcatattaacaaacactgtggaag"
						.toUpperCase());
		this.builderForward.setGeneSymbol("Q0ZAJ5");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 2, 167138319,
				PositionType.ZERO_BASED), "", "A");
		Annotation annotation1 = new InsertionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();

		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(8, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("1588-2_1588-1insT", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("?", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.SPLICE_ACCEPTOR_VARIANT,
				VariantEffect.CODING_TRANSCRIPT_INTRON_VARIANT), annotation1.getEffects());

		GenomeVariant change2 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 2, 167140961,
				PositionType.ZERO_BASED), "", "A");
		Annotation annotation2 = new InsertionAnnotationBuilder(infoForward, change2, new AnnotationBuilderOptions())
				.build();

		Assert.assertEquals(infoForward.getAccession(), annotation2.getTranscript().getAccession());
		Assert.assertEquals(8, annotation2.getAnnoLoc().getRank());
		Assert.assertEquals("1587+1_1587+2insT", annotation2.getCDSNTChange().toHGVSString());
		Assert.assertEquals("?", annotation2.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.SPLICE_DONOR_VARIANT,
				VariantEffect.CODING_TRANSCRIPT_INTRON_VARIANT), annotation2.getEffects());
	}

	@Test
	public void testRealWorld_uc001qsh_4() throws InvalidGenomeChange {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001qsh.4	chr12	+	7079943	7085165	7080086	7084971	7	7079943,7080214,7083500,7083713,7084251,7084390,7084857,	7080212,7080253,7083602,7083855,7084310,7084540,7085165,	Q92979	uc001qsh.4");
		this.builderForward
				.setSequence("ctcccagggagggcagcgcaagacagcaagtcatctccatttcctggcccactttcaaaatggcagccggaaggaaatttgtgattagaagccgcgctgttcttatttaagagcgttagcgcaacttccggtattgttgcaagatggccgcgcccagtgatggattcaagcctcgtgaacgaagcggtggggagcaggcacaggactgggatgctctgccacccaagcggccccgactaggggcaggaaacaagatcggaggccgtaggcttattgtggtgctggaaggggccagtctggagacagtcaaggtagggaagacatatgagctactcaactgtgacaagcacaagtctatattgttgaagaatggacgggaccctggggaagcgcggccagatatcacccaccagagtttgctgatgctgatggatagtcccctgaaccgagctggcttgctacaggtttatatccatacacagaagaatgttctgattgaagtgaatccccagacccgaattcccagaacctttgaccgcttttgtggcctcatggttcaacttttacacaagctcagtgttcgagcagctgatggcccccagaagcttttgaaggtaattaagaatccagtatcagatcactttccagttggatgtatgaaagttggcacttctttttccatcccggttgtcagtgatgtgcgtgagctggtgcccagcagtgatcctattgtttttgtggtaggggcctttgcccatggcaaggtcagtgtggagtatacagagaagatggtgtccatcagtaactaccccctttctgctgccctcacctgtgcaaaacttaccacagcctttgaggaagtatggggggtcatttgacagtagtagaacctgttctgaaaccagaaactgttgatgtcacatcctttgaccctggtctgagctgactgctggaagatgatctttctgcactgagactgtggagtttggggaagccaaggctgtacatttgctatttgtttatcctatgaatactgttcttgcaaacctggttgttttggggttcctaaagt"
						.toUpperCase());
		this.builderForward.setGeneSymbol("Q0ZAJ5");
		this.infoForward = builderForward.build();
		// RefSeq NM_006331.7

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 12, 7080210,
				PositionType.ZERO_BASED), "", "G");
		Annotation annotation1 = new InsertionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();

		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(AnnotationLocation.RankType.EXON, annotation1.getAnnoLoc().getRankType());
		Assert.assertEquals(1, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("126dup", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("?", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.SPLICE_DONOR_VARIANT,
				VariantEffect.CODING_TRANSCRIPT_INTRON_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorld_uc002shg_3() throws InvalidGenomeChange {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc002shg.3	chr2	-	71057342	71062953	71058180	71062906	6	71057342,71058831,71060030,71060776,71062621,71062833,	71058331,71058950,71060182,71061151,71062738,71062953,	NP_056532	uc002shg.3");
		this.builderForward
				.setSequence("ggcagccagaagcacctgtgctcccaggataagggtgagcactcaggatgactgtggagaaggaggcccctgatgcgcacttcactgtggacaaacagaacatctccctctggccccgagagcctcctcccaagtccggtccatctctggtcccggggaaaacacccacagtccgtgctgcattaatctgcctgacgctggtcctggtcgcctccgtcctgctgcaggccgtcctttatccccggtttatgggcaccatatcagatgtaaagaccaatgtccagttgctgaaaggtcgtgtggacaacatcagcaccctggattctgaaattaaaaagaatagtgacggcatggaggcagctggcgttcagatccagatggtgaatgagagcctgggttatgtgcgttctcagttcctgaagttaaaaaccagtgtggagaaggccaacgcacagatccagatcttaacaagaagttgggaagaagtcagtaccttaaatgcccaaatcccagagttaaaaagtgatttggagaaagccagtgctttaaatacaaagatccgggcactccagggcagcttggagaatatgagcaagttgctcaaacgacaaaatgatattctacaggtggtttctcaaggctggaagtacttcaaggggaacttctattacttttctctcattccaaagacctggtatagtgccgagcagttctgtgtgtccaggaattcacacctgacctcggtgacctcagagagtgagcaggagtttctgtataaaacagcggggggactcatctactggattggcctgactaaagcagggatggaaggggactggtcctgggtggatgacacgccattcaacaaggtccaaagtgcgaggttctggattccaggtgagcccaacaatgctgggaacaatgaacactgtggcaatataaaggctccctcacttcaggcctggaatgatgccccatgtgacaaaacgtttcttttcatttgtaagcgaccctatgtcccatcagaaccgtgacaggacaggctcccaagctcactctttgagctccaacgcttgttaaacatgaggaaatgcctctttcttccccagactccaggatgactttgcacgttaatttttcttgcttcaaaattgtcccacagtggcattctggagtccgtctgtcttggctggaaattctctgacgtcttggaggcagctggaatggaaaggagaattcaggttaaagtgggaggggtgggtagagaggatttagaagttccaattgccctgctaaggaggatcaagacccgtaatccggcataacaccctggggttttccactctttcagagaaacctcagcttcatcacatcaaagttactccagagcaaccaagcaattctcctgatattgtcatccagggcttttcttggccaaaccccctagaatttccatgtctctgcttagctgtgctggcagctagcagctggctgtgtttgcagtgcaaatagctctgttcttggaaatcctgctcatggtatgtccccagtggtttcttcatccacatcatctaaagcctgaacccgttcttctctggttcaagtcagtggctgacacggacttgtatctccttcagagctcggctggcacccagcctcccttctccttccactcccttagtacactggagtgccgagccctgccttccacccagcgtccatccagcccctgtcctcacctctccggcacctcctcctccttctgcatttcctatcttcctgtgtcttgtgcatgggaagcagccttcagtgccttcatgaattcaccttccagcttcctcagaataaaatgctgcctgggtcaaggactca"
						.toUpperCase());
		this.builderForward.setGeneSymbol("Q0ZAJ5");
		this.infoForward = builderForward.build();
		// RefSeq NM_015717

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 2, 71062833,
				PositionType.ZERO_BASED), "", "C");
		Annotation annotation1 = new InsertionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();

		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(AnnotationLocation.RankType.EXON, annotation1.getAnnoLoc().getRankType());
		Assert.assertEquals(0, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("73_73+1insG", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("?", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.SPLICE_DONOR_VARIANT,
				VariantEffect.CODING_TRANSCRIPT_INTRON_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorld_uc002ulp_3() throws InvalidGenomeChange {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc002ulp.3	chr2	-	178487976	178753466	178494134	178740620	17	178487976,178528593,178534220,178540182,178545553,178562059,178565848,178576496,178592385,178592753,178634050,178681555,178682584,178684946,178704977,178740585,178753345,	178494290,178528677,178534295,178540246,178545631,178562160,178565939,178576606,178592493,178592900,178634101,178681648,178682652,178685022,178705110,178740650,178753466,	NP_001070664	uc002ulp.3");
		this.builderForward
				.setSequence("gtggaaagatgttacttcatctcccaggtttgctcactgcaaatacaatcctgagaactgaactagggccttaaagtcctgacatgcatggcttggttttgtggattgcctctctcaacaggtggtgaaatttaccaaatcctttgaattgatgtccccaaagtgcagtgctgatgctgagaacagtttcaaagaaagcatggagaaatcatcatactccgactggctaataaataacagcattgctgagctggttgcttcaacaggccttccagtgaacatcagtgatgcctaccaggatccgcgctttgatgcagaggcagaccagatatctggttttcacataagatctgttctttgtgtccctatttggaatagcaaccaccaaataattggagtggctcaagtgttaaacagacttgatgggaaaccttttgatgatgcagatcaacgactttttgaggcttttgtcatcttttgtggacttggcatcaacaacacaattatgtatgatcaagtgaagaagtcctgggccaagcagtctgtggctcttgatgtgctatcataccatgcaacatgttcaaaagctgaagttgacaagtttaaggcagccaacatccctctggtgtcagaacttgccatcgatgacattcattttgatgacttttctctcgacgttgatgccatgatcacagctgctctccggatgttcatggagctggggatggtacagaaatttaaaattgactatgagacactgtgtaggtggcttttgacagtgaggaaaaactatcggatggttctataccacaactggagacatgccttcaacgtgtgtcagctgatgttcgcgatgttaaccactgctgggtttcaagacattctgaccgaggtggaaattttagcggtgattgtgggatgcctgtgtcatgacctcgaccacaggggaaccaacaatgccttccaagctaagagtggctctgccctggcccaactctatggaacctctgctaccttggagcatcaccatttcaaccacgccgtgatgatccttcaaagtgagggtcacaatatctttgctaacctgtcctccaaggaatatagtgaccttatgcagcttttgaagcagtcaatattggcaacagacctcacgctgtactttgagaggagaactgaattctttgaacttgtcagtaaaggagaatacgattggaacatcaaaaaccatcgtgatatatttcgatcaatgttaatgacagcctgtgaccttggagccgtgaccaaaccgtgggagatctccagacaggtggcagaacttgtaaccagtgagttcttcgaacaaggagatcgggagagattagagctcaaactcactccttcagcaatttttgatcggaaccggaaggatgaactgcctcggttgcaactggagtggattgatagcatctgcatgcctttgtatcaggcactggtgaaggtcaacgtgaaactgaagccgatgctagattcagtagctacaaacagaagtaagtgggaagagctacaccaaaaacgactgctggcctcaactgcctcatcctcccctgccagtgttatggtagccaaggaagacaggaactaaacctccaggtcagctgcagctgcaaaatgactacagcctgaagggccattttcagtccagcaatgtcatccttttgttcttttagctcagaaagacctaacatctcaaggatgcactgggaagcatgcctgggctttcaccttgaagcatggtcagcagcagagagagcaacgggaaggacaaagaaagaggtggggcagggagcacaccccaggaccctcacttttccctaatgaacacgcatgggctgaaatgaaggctctgggtaggggactgttttggatccaaggacctgtggacagtcggcctacttactctgagctgagggaacactgaacagtaaaagcgtcattagcgctgcttcgttttgtatagggcttttctgtttgttacaagccaaacactgcctgtctttgcttcctgtccctgaatgcctttttgtgccagactgtcccaagaatcctaatttgtattccatagaggtattttatttttaatcctagagcttcttattgatggatcctttagaattgcctacctaaaaggtaaactatactatccttataaatactgatcaatcccagttctccccctaaaaatgaatacatagtaggactatagcaaatgtgtttgatgggtaattctagactgggactatggtacccttttccagagttttaaaattcaaccttcattacagacaaagttttctcccagaaggaatggattgatagattttgattaaagtaagggtggaaggaaatctgtagctggatttaccacaagtgacatctagaaactatagttcacaggacagagcagagccatggagactaagcattgactaccttgagttctcctagtgaggagttctggtataaaatttaagattactaccagtaaccaacttaaagcaaactataggggtccctaattttggatttttccttaagtgtaagaaacaatgcttcaaatgttaagaaataacagtctgggcaaagaacgcatattctataggaagccaggtttacaataggtaagaataaactgtattaagtagatgtaatgactagaaagctgctttgctccctatattgagaaattgtggacatggtatgtgttatccaaagaacattgggctagaagatagatttctatccttagctttggcattattgactggattgacttgaacaagtcacttaacttctacaagcttgtttccttatttgtcaaattagattacactaggaaacgattctcgaacatgttttaaccttacaactctttgttcaaataaatctttcaatgaatccccaacatataaaacgtcaaaaagctgaattcctatgtgaattataagcagggtttggacatccagagcacctacaaaccttcatctctttgattacgtacatgggggtccacagggactctgaagagtttgaaaatccctggcctagattaaacctgagagccattctagctcttaaaaaaatctgaaatttacaaaccatttttcttgttattgtttttgtttctttgtttgtttgttttttgagactgagttttcactcttgttgcccaggctggagtgcaatggcgtgatctcggcttactgcaacctctgcttcctgggttcaagcaattctccagcctcaacctcctgagaagctgggattacaggtgcctgccaccacgcctggctaatttttgtacttttagtagacacagggtttcaccatgtttgccaggctgatctcaaactcctgacctcaggtgagccacccacctctgcctccgaaagtgctgggattacaggcgtgagccaccgtacccggcccttgttattgttattaatcattatcaatcattaactttacagatattaaagaaataggggccatgttctacatgcgacattgttttcctggcacacagtagacaattcaggaaatgtctgaagaaggaatgggtaaaattaagataccaaaatggaattgaggggagagcaggtaattgtagagaaaaagactctttatggcaagaaacaaagtaaaagaaatgtatagaccgggcgcggtggctcacgcctgtaatcccagcaatttggaaggccgaggcgggcggatcacgaggtcaggagatcgagaccgtcctggctaacacggtgaaaccctgtctctactaaaaaacacaaaacaaattagccaggcgcggtggcaggcgtctgtagtcccagctactggggaggctgaggcaagagaatggcgggaacccgcgaggcggagcttgcagtgagtcgagatcacgtcactgccctccagcctgggcgaccagcgagactccgtctcaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaagtataaaccagactaagaggggaataaaagcaagaagaaaaagaaacgtaagaacaccagtaaaggaaagacagggaccgagacaggtggttaagggaaatgaaaaaaaaaaaaaaaaggtgaaagtcaatccaaacaaagaaaaatggcacttccaaccaaaaaaaatgaaggtgtagaaccctgtcatcaattgctcagaaaagagagacacttgagcagggaggggtttgagaaaggggagaaggaaggagtgagagccctaaaaaataatgttcctagctaccctttacagctgtctctgcagggaagactggagaaagtagagggggaaatgaatgcaggaagaaactgcctctgcagagcaggtaagaggaggttgttcagatttatggattttattaagccagttttctgtatcactctgctataaaggaaaatagcttctgcagggctgtctctcctccttcaggtggggaacaaagggtgggtggagtcagggacagagtctggtctgcgagatcgtgacgtgtctgcagctccgaagcactgcaggtgtttctgggtaatgtgccacatcatgtttatatcacgaacgggaattgaaaatcagtctgtgtgaggcagagatgagcccacatctgttatcctccccaacaataaataaataaatagcccagacaaatggatgccagtccatctacagcaacattttaaagcatgtgctttatgacaaaactggacccagggtctgacagagactgcaggctgccgtgagcactgtagctatcctgaacattgatgctacatattgctaaggggaacataatttatagcataaaccctacaagcttccactaaggttgagttttcctctcattttcttgccgttttattggcttcactgcatggttgccaatagcaacatccactggtagcaaggggagttcaacaccattgcaatctgccatacagtggagccctgacaatgtcaaccatcaatctagtagctgtttgtaattgcctccatctatttctcctgtcttgaggtatctttcctattgtgggtcaagatttagaaactgctgactaatgccatgggtctgtgtctccatgactatgaagcagtggacaggaaggtagcagcttcctggtgccaacaaccccagaggttagaggttgaactcttgcgggtttcaggcgtttttaatcccttttctaaacaatttataattttgcaagaatgtgaggcatatgcagttatccatataatctgcttttccaaaaaagagaaaggaagtttcccagtccttattattaatgtttatttcctattggagaaagacatttgaaatatgagccttcttgtaaaaactcaaactgaccagattatgtttgcttcctcaggtctcttggcattaattgttgaaataacttcatcctactttttcgatgagatgtatttttttcagacatatttattcttactagtcactatgtagctattatgtgcacaatctacaatgtagtggcaatcctggtgggtagggacagcttcacatactgcaaagggtgccattcagtgcatagcagactggaactccctcctagcttggccaccagctgaacctgtaatcttaggactctccaagtcttagaatctccatctattacataaaagcactgacttttattcagtccccacaccaaaggaccaagacgtgtttagctttaaagaaattttccagataattttcttttttattcttctcaaataaaatagcaacaaacctaaagcagtcatcttcttaataaagacataataaaacttacatatatggggtagatgtaagcacaatcatactacagatgtatcttgagcctgtctctaattatgatgaaagatgtagtcatcaatacaatttaaaaaaagatatgtcctttttttgagacagggtcttgctctgtcacccaggctggagggcagtggcacaatcacagccactgcagccttgacctctccaacttaagtgatctttcctgcctcagcctcatgagtagctgggaccacaggtgtgcaccaccacacccagctaaattttgtattgtttgtaaagacgaggcctcactatgctgaccacactggtctcgaactcctgggctcaagcaatcctcctgccttggcctcccaaagtgttagggttagagatatgaaccaccacacccaccttatatgtccttttatttcataaaaaacaattttagattatctgatatcacacatatcccataaatggtagtcattaatttagaagtgaatttataaagttaagagaagttaaaattgcacatgcattttgtttctaaaatctgagatgtctatgcatttccaggtattgtatatttgcaaatacattgggttagaaatcatctttaacattttgtgttagtgtaataacaatattatctaatattaatgctgccagaatccagttgctttttctgtgtatcaaaatttatcataactttttccacttaacactaggtaattaaacagaatctgcccaatttattctgcagtaaaattattttaaaatctatttttcctgctgactcttagaaattgcagaaagacaaaaaccagtttcatctccagtaatagtgtgaaacaatttccttccagtgggacagaaacctagacatactagggaaagatttaaatataaagaaaaatgccttggctgaaaaaaaaaaaaaccaccaaaacacaaaaaggattcattaaagcaacagaacaaaacctaatttgggaccctaaaaactctggcaatgccactgtaatgaagtttcattatctgcttcagagtaacagtctctgaaattgttattctgctacatgctgtaaagaactcccaaaactcaaatgtatcaggaaatgtaaaggttaagtctgactacaagaaggccaaaattgcaccagcttcctaagtgaagaataatagaataaaacatatagagggcagaaataaaatgaggtgtatctggagaatttcatgatgagcatttagatttagcaatgcccaatgtcatgctgacactgtttgtcatgaccttgtcttcagctagtaatttggggttgtacttttttaaatttaattttgaatgttcttgcatgtttggtacctctctcctcactgctaaagataaattgtttatctgtataacataactacaccaatgtcatttttgtatacgctagtacacaaatgtgtttttttattaagtaatgaagtatttgctgtgaaaaatgtattatttgtgccaccgtttatatctgtgttcattttctgtgtgtatatgcgtgtgtattcgaatctcaatttttcttttactctagtttagattaagacatatttagatgaaattttaaaaataacattggaaataggaggctaagttttgttgagtctcattcccttggggggaaattgcttttgccattttattttcatgtacaataacctaaaaaggatctcctactgacttccttcctaattattattgttttacacgaaagaaaggaaatacgttttcaattgagttgtttgaaatcattcactttgtgtagatttcccagactgatgtttcattgtaagaatattacattatagacaggttggccatttcacaagcaactaatccatagttttggaagcccgctttaagagacctgaatatctttgtttttaataaaatacttagagtttaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("Q9HCR9");
		this.infoForward = builderForward.build();
		// RefSeq NM_001077196

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 2, 178494173,
				PositionType.ZERO_BASED), "", "GGA");
		Annotation annotation1 = new InsertionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();

		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(AnnotationLocation.RankType.EXON, annotation1.getAnnoLoc().getRankType());
		Assert.assertEquals(16, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("1429_1431dup", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Ser477dup)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(
				ImmutableSortedSet.of(VariantEffect.INFRAME_INSERTION, VariantEffect.DIRECT_TANDEM_DUPLICATION),
				annotation1.getEffects());
	}

	@Test
	public void testRealWorld_uc010crz_1() throws InvalidGenomeChange {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc010crz.1	chr17	-	27290964	27333081	27290965	27332887	5	27290964,27296774,27306697,27308388,27332832,	27291165,27296970,27306831,27309057,27333081,	C9JFF1	uc010crz.1");
		this.builderForward
				.setSequence("gatccccggcgccgtcgccaggcgctggccgtggtgctgattctgtcaggcgctggcggcggcagcggcggtgacggctgcggccccgctccctctacccggccggacccggctctgcccccgcgcccaagccccaccaagccccccgccctcccgccgcggtcccagcccagggcgcggccgcaaccagcaccatgcgcccggtagccctgctgctcctgccctcgctgctggcgctcctggctcacggactctctttagaggccccaaccgtggggaaaggacaagccccaggcatcgaggagacagatggcgagctgacagcagcccccacacctgagcagccagaacgaggcgtccactttgtcacaacagcccccaccttgaagctgctcaaccaccacccgctgcttgaggaattcctacaagaggggctggaaaagggagatgaggagctgaggccagcactgcccttccagcctgacccacctgcacccttcaccccaagtccccttccccgcctggccaaccaggacagccgccctgtctttaccagccccactccagccatggctgcggtacccactcagccccagtccaaggagggaccctggagtccggagtcagagtcccctatgcttcgaatcacagctcccctacctccagggcccagcatggcagtgcccaccctaggcccaggggagatagccagcactacaccccccagcagagcctggacaccaacccaagagggtcctggagacatgggaaggccgtgggttgcagaggttgtgtcccagggcgcagggatcgggatccaggggaccatcacctcctccacagcttcaggagatgatgaggagaccaccactaccaccaccatcatcaccaccaccatcaccacagtccagacaccaggcccttgtagctggaatttctcaggcccagagggctctctggactcccctacagacctcagctcccccactgatgttggcctggactgcttcttctacatctctgtctaccctggctatggcgtggaaatcaaggtccagaatatcagcctccgggaaggggagacagtgactgtggaaggcctgggggggcctgacccactgcccctggccaaccagtctttcctgctgcggggccaagtcatccgcagccccacccaccaagcggccctgaggttccagagcctcccgccaccggctggccctggcaccttccatttccattaccaagcctatctcctgagctgccactttccccgtcgtccagcttatggagatgtgactgtcaccagcctccacccagggggtagtgcccgcttccattgtgccactggctaccagctgaagggcgccaggcatctcacctgtctcaatgccacccagcccttctgggattcaaaggagcccgtctgcatcggtgagtgcccagggg"
						.toUpperCase());
		this.builderForward.setGeneSymbol("C9JFF1");
		this.infoForward = builderForward.build();
		// RefSeq NM_001077196

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 17, 27290969,
				PositionType.ZERO_BASED), "", "G");
		Annotation annotation1 = new InsertionAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions())
				.build();

		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(AnnotationLocation.RankType.EXON, annotation1.getAnnoLoc().getRankType());
		Assert.assertEquals(4, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("1250dup", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Gly418Argfs*?)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FRAMESHIFT_ELONGATION), annotation1.getEffects());
	}
}
