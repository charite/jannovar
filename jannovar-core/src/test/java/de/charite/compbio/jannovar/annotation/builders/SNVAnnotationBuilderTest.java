package de.charite.compbio.jannovar.annotation.builders;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableSortedSet;

import de.charite.compbio.jannovar.annotation.Annotation;
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

// TODO(holtgrew): Test results of getGenomicNTChange()?

public class SNVAnnotationBuilderTest {

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
	public void testForwardUpstream() throws InvalidGenomeVariant {
		GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640061,
				PositionType.ZERO_BASED), "T", "A");
		Annotation anno = new SNVAnnotationBuilder(infoForward, change, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), anno.getTranscript().getAccession());
		Assert.assertEquals(null, anno.getAnnoLoc());
		Assert.assertEquals(null, anno.getCDSNTChange());
		Assert.assertEquals(null, anno.getProteinChange());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.UPSTREAM_GENE_VARIANT), anno.getEffects());
	}

	@Test
	public void testForwardDownstream() throws InvalidGenomeVariant {
		GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6649340,
				PositionType.ZERO_BASED), "T", "A");
		Annotation anno = new SNVAnnotationBuilder(infoForward, change, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), anno.getTranscript().getAccession());
		Assert.assertEquals(null, anno.getAnnoLoc());
		Assert.assertEquals(null, anno.getCDSNTChange());
		Assert.assertEquals(null, anno.getProteinChange());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.DOWNSTREAM_GENE_VARIANT), anno.getEffects());
	}

	@Test
	public void testForwardIntergenic() throws InvalidGenomeVariant {
		// upstream intergenic
		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6639061,
				PositionType.ZERO_BASED), "T", "A");
		Annotation anno1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), anno1.getTranscript().getAccession());
		Assert.assertEquals(null, anno1.getAnnoLoc());
		Assert.assertEquals(null, anno1.getCDSNTChange());
		Assert.assertEquals(null, anno1.getProteinChange());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.INTERGENIC_VARIANT), anno1.getEffects());

		// downstream intergenic
		GenomeVariant change2 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6650340,
				PositionType.ZERO_BASED), "T", "A");
		Annotation anno2 = new SNVAnnotationBuilder(infoForward, change2, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), anno2.getTranscript().getAccession());
		Assert.assertEquals(null, anno2.getAnnoLoc());
		Assert.assertEquals(null, anno2.getCDSNTChange());
		Assert.assertEquals(null, anno2.getProteinChange());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.INTERGENIC_VARIANT), anno2.getEffects());
	}

	@Test
	public void testForwardIntronic() throws InvalidGenomeVariant {
		// position towards right side of intron
		GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6642106,
				PositionType.ZERO_BASED), "T", "A");
		Annotation anno = new SNVAnnotationBuilder(infoForward, change, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), anno.getTranscript().getAccession());
		Assert.assertEquals(1, anno.getAnnoLoc().getRank());
		Assert.assertEquals("691-11T>A", anno.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", anno.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.CODING_TRANSCRIPT_INTRON_VARIANT), anno.getEffects());

		// position towards left side of intron
		GenomeVariant change2 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6646100,
				PositionType.ZERO_BASED), "T", "A");
		Annotation anno2 = new SNVAnnotationBuilder(infoForward, change2, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), anno2.getTranscript().getAccession());
		Assert.assertEquals(3, anno2.getAnnoLoc().getRank());
		Assert.assertEquals("1044+11T>A", anno2.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", anno2.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.CODING_TRANSCRIPT_INTRON_VARIANT), anno2.getEffects());
	}

	@Test
	public void testForwardThreePrimeUTR() throws InvalidGenomeVariant {
		GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6649272,
				PositionType.ZERO_BASED), "T", "A");
		Annotation anno = new SNVAnnotationBuilder(infoForward, change, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), anno.getTranscript().getAccession());
		Assert.assertEquals(10, anno.getAnnoLoc().getRank());
		Assert.assertEquals("*1T>A", anno.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", anno.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.THREE_PRIME_UTR_VARIANT), anno.getEffects());
	}

	@Test
	public void testForwardFivePrimeUTR() throws InvalidGenomeVariant {
		GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640668,
				PositionType.ZERO_BASED), "T", "A");
		Annotation anno = new SNVAnnotationBuilder(infoForward, change, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), anno.getTranscript().getAccession());
		Assert.assertEquals(1, anno.getAnnoLoc().getRank());
		Assert.assertEquals("-1T>A", anno.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", anno.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FIVE_PRIME_UTR_VARIANT), anno.getEffects());
	}

	@Test
	public void testForwardStartLoss() throws InvalidGenomeVariant {
		GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640669,
				PositionType.ZERO_BASED), "A", "T");
		Annotation anno = new SNVAnnotationBuilder(infoForward, change, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), anno.getTranscript().getAccession());
		Assert.assertEquals(1, anno.getAnnoLoc().getRank());
		Assert.assertEquals("1A>T", anno.getCDSNTChange().toHGVSString());
		Assert.assertEquals("0?", anno.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.MISSENSE_VARIANT, VariantEffect.START_LOST),
				anno.getEffects());
	}

	@Test
	public void testForwardStopLoss() throws InvalidGenomeVariant {
		GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6649271,
				PositionType.ZERO_BASED), "G", "C");
		Annotation anno = new SNVAnnotationBuilder(infoForward, change, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), anno.getTranscript().getAccession());
		Assert.assertEquals(10, anno.getAnnoLoc().getRank());
		Assert.assertEquals("2067G>C", anno.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(*689Tyrext*23)", anno.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.STOP_LOST), anno.getEffects());
	}

	@Test
	public void testForwardStopGained() throws InvalidGenomeVariant {
		GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6649262,
				PositionType.ZERO_BASED), "T", "A");
		Annotation anno = new SNVAnnotationBuilder(infoForward, change, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), anno.getTranscript().getAccession());
		Assert.assertEquals(10, anno.getAnnoLoc().getRank());
		Assert.assertEquals("2058T>A", anno.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Cys686*)", anno.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.STOP_GAINED), anno.getEffects());
	}

	@Test
	public void testForwardStopRetained() throws InvalidGenomeVariant {
		GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6649271,
				PositionType.ZERO_BASED), "G", "A");
		Annotation anno = new SNVAnnotationBuilder(infoForward, change, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), anno.getTranscript().getAccession());
		Assert.assertEquals(10, anno.getAnnoLoc().getRank());
		Assert.assertEquals("2067G>A", anno.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", anno.getProteinChange().toHGVSString());
		Assert.assertEquals(
				ImmutableSortedSet.of(VariantEffect.STOP_RETAINED_VARIANT, VariantEffect.SYNONYMOUS_VARIANT),
				anno.getEffects());
	}

	@Test
	public void testForwardSplicingDonor() throws InvalidGenomeVariant {
		GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640196,
				PositionType.ZERO_BASED), "G", "A");
		Annotation anno = new SNVAnnotationBuilder(infoForward, change, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), anno.getTranscript().getAccession());
		Assert.assertEquals(0, anno.getAnnoLoc().getRank());
		Assert.assertEquals("-70+1G>A", anno.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", anno.getProteinChange().toHGVSString());
		Assert.assertEquals(
				ImmutableSortedSet.of(VariantEffect.FIVE_PRIME_UTR_VARIANT, VariantEffect.SPLICE_DONOR_VARIANT),
				anno.getEffects());
	}

	@Test
	public void testForwardSplicingAcceptor() throws InvalidGenomeVariant {
		GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640599,
				PositionType.ZERO_BASED), "G", "A");
		Annotation anno = new SNVAnnotationBuilder(infoForward, change, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), anno.getTranscript().getAccession());
		Assert.assertEquals(0, anno.getAnnoLoc().getRank());
		Assert.assertEquals("-69-1G>A", anno.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", anno.getProteinChange().toHGVSString());
		Assert.assertEquals(
				ImmutableSortedSet.of(VariantEffect.FIVE_PRIME_UTR_VARIANT, VariantEffect.SPLICE_ACCEPTOR_VARIANT),
				anno.getEffects());
	}

	@Test
	public void testForwardSplicingRegion() throws InvalidGenomeVariant {
		// in UTR
		GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640602,
				PositionType.ZERO_BASED), "G", "A");
		Annotation anno = new SNVAnnotationBuilder(infoForward, change, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), anno.getTranscript().getAccession());
		Assert.assertEquals(1, anno.getAnnoLoc().getRank());
		Assert.assertEquals("-67G>A", anno.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", anno.getProteinChange().toHGVSString());
		Assert.assertEquals(
				ImmutableSortedSet.of(VariantEffect.FIVE_PRIME_UTR_VARIANT, VariantEffect.SPLICE_REGION_VARIANT),
				anno.getEffects());
		// in CDS
		GenomeVariant change2 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6647537,
				PositionType.ZERO_BASED), "T", "G");
		Annotation anno2 = new SNVAnnotationBuilder(infoForward, change2, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), anno2.getTranscript().getAccession());
		Assert.assertEquals(6, anno2.getAnnoLoc().getRank());
		Assert.assertEquals("1225T>G", anno2.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Cys409Gly)", anno2.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.MISSENSE_VARIANT, VariantEffect.SPLICE_REGION_VARIANT),
				anno2.getEffects());
	}

	@Test
	public void testForwardFirstCDSBases() throws InvalidGenomeVariant {
		// We check the first 10 CDS bases and compared them by hand to Mutalyzer results.

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640669,
				PositionType.ZERO_BASED), "A", "T");
		Annotation anno1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), anno1.getTranscript().getAccession());
		Assert.assertEquals(1, anno1.getAnnoLoc().getRank());
		Assert.assertEquals("1A>T", anno1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("0?", anno1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.MISSENSE_VARIANT, VariantEffect.START_LOST),
				anno1.getEffects());

		GenomeVariant change2 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640670,
				PositionType.ZERO_BASED), "T", "C");
		Annotation anno2 = new SNVAnnotationBuilder(infoForward, change2, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), anno2.getTranscript().getAccession());
		Assert.assertEquals(1, anno2.getAnnoLoc().getRank());
		Assert.assertEquals("2T>C", anno2.getCDSNTChange().toHGVSString());
		Assert.assertEquals("0?", anno2.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.MISSENSE_VARIANT, VariantEffect.START_LOST),
				anno2.getEffects());

		GenomeVariant change3 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640671,
				PositionType.ZERO_BASED), "G", "A");
		Annotation anno3 = new SNVAnnotationBuilder(infoForward, change3, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), anno3.getTranscript().getAccession());
		Assert.assertEquals(1, anno3.getAnnoLoc().getRank());
		Assert.assertEquals("3G>A", anno3.getCDSNTChange().toHGVSString());
		Assert.assertEquals("0?", anno3.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.MISSENSE_VARIANT, VariantEffect.START_LOST),
				anno3.getEffects());

		GenomeVariant change4 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640672,
				PositionType.ZERO_BASED), "G", "T");
		Annotation anno4 = new SNVAnnotationBuilder(infoForward, change4, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), anno4.getTranscript().getAccession());
		Assert.assertEquals(1, anno4.getAnnoLoc().getRank());
		Assert.assertEquals("4G>T", anno4.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Asp2Tyr)", anno4.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.MISSENSE_VARIANT), anno4.getEffects());

		GenomeVariant change5 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640673,
				PositionType.ZERO_BASED), "A", "T");
		Annotation anno5 = new SNVAnnotationBuilder(infoForward, change5, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), anno5.getTranscript().getAccession());
		Assert.assertEquals(1, anno5.getAnnoLoc().getRank());
		Assert.assertEquals("5A>T", anno5.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Asp2Val)", anno5.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.MISSENSE_VARIANT), anno5.getEffects());

		GenomeVariant change6 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640674,
				PositionType.ZERO_BASED), "C", "T");
		Annotation anno6 = new SNVAnnotationBuilder(infoForward, change6, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), anno6.getTranscript().getAccession());
		Assert.assertEquals(1, anno6.getAnnoLoc().getRank());
		Assert.assertEquals("6C>T", anno6.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", anno6.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.SYNONYMOUS_VARIANT), anno6.getEffects());

		GenomeVariant change7 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640675,
				PositionType.ZERO_BASED), "G", "T");
		Annotation anno7 = new SNVAnnotationBuilder(infoForward, change7, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), anno7.getTranscript().getAccession());
		Assert.assertEquals(1, anno7.getAnnoLoc().getRank());
		Assert.assertEquals("7G>T", anno7.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Gly3Cys)", anno7.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.MISSENSE_VARIANT), anno7.getEffects());

		GenomeVariant change8 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640676,
				PositionType.ZERO_BASED), "G", "T");
		Annotation anno8 = new SNVAnnotationBuilder(infoForward, change8, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), anno8.getTranscript().getAccession());
		Assert.assertEquals(1, anno8.getAnnoLoc().getRank());
		Assert.assertEquals("8G>T", anno8.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Gly3Val)", anno8.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.MISSENSE_VARIANT), anno8.getEffects());

		GenomeVariant change9 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640677,
				PositionType.ZERO_BASED), "C", "G");
		Annotation anno9 = new SNVAnnotationBuilder(infoForward, change9, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), anno9.getTranscript().getAccession());
		Assert.assertEquals(1, anno9.getAnnoLoc().getRank());
		Assert.assertEquals("9C>G", anno9.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", anno9.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.SYNONYMOUS_VARIANT), anno9.getEffects());

		GenomeVariant change10 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640678,
				PositionType.ZERO_BASED), "T", "A");
		Annotation anno10 = new SNVAnnotationBuilder(infoForward, change10, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), anno10.getTranscript().getAccession());
		Assert.assertEquals(1, anno10.getAnnoLoc().getRank());
		Assert.assertEquals("10T>A", anno10.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Ser4Thr)", anno10.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.MISSENSE_VARIANT), anno10.getEffects());
	}

	@Test
	public void testForwardLastCDSBases() throws InvalidGenomeVariant {
		// Here, we start off 3 positions before the end (2 positions before the inclusive end).

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6649270,
				PositionType.ZERO_BASED), "A", "G");
		Annotation anno1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), anno1.getTranscript().getAccession());
		Assert.assertEquals(10, anno1.getAnnoLoc().getRank());
		Assert.assertEquals("2066A>G", anno1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(*689Trpext*23)", anno1.getProteinChange().toHGVSString());
		Assert.assertEquals(anno1.getEffects(), ImmutableSortedSet.of(VariantEffect.STOP_LOST));

		GenomeVariant change2 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6649269,
				PositionType.ZERO_BASED), "T", "C");
		Annotation anno2 = new SNVAnnotationBuilder(infoForward, change2, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), anno2.getTranscript().getAccession());
		Assert.assertEquals(10, anno2.getAnnoLoc().getRank());
		Assert.assertEquals("2065T>C", anno2.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(*689Glnext*23)", anno2.getProteinChange().toHGVSString());
		Assert.assertEquals(anno2.getEffects(), ImmutableSortedSet.of(VariantEffect.STOP_LOST));

		GenomeVariant change3 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6649268,
				PositionType.ZERO_BASED), "A", "T");
		Annotation anno3 = new SNVAnnotationBuilder(infoForward, change3, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), anno3.getTranscript().getAccession());
		Assert.assertEquals(10, anno3.getAnnoLoc().getRank());
		Assert.assertEquals("2064A>T", anno3.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", anno3.getProteinChange().toHGVSString());
		Assert.assertEquals(anno3.getEffects(), ImmutableSortedSet.of(VariantEffect.SYNONYMOUS_VARIANT));

		GenomeVariant change4 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6649267,
				PositionType.ZERO_BASED), "C", "G");
		Annotation anno4 = new SNVAnnotationBuilder(infoForward, change4, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), anno4.getTranscript().getAccession());
		Assert.assertEquals(10, anno4.getAnnoLoc().getRank());
		Assert.assertEquals("2063C>G", anno4.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Thr688Arg)", anno4.getProteinChange().toHGVSString());
		Assert.assertEquals(anno4.getEffects(), ImmutableSortedSet.of(VariantEffect.MISSENSE_VARIANT));

		GenomeVariant change5 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6649266,
				PositionType.ZERO_BASED), "A", "G");
		Annotation anno5 = new SNVAnnotationBuilder(infoForward, change5, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), anno5.getTranscript().getAccession());
		Assert.assertEquals(10, anno5.getAnnoLoc().getRank());
		Assert.assertEquals("2062A>G", anno5.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Thr688Ala)", anno5.getProteinChange().toHGVSString());
		Assert.assertEquals(anno5.getEffects(), ImmutableSortedSet.of(VariantEffect.MISSENSE_VARIANT));

		GenomeVariant change6 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6649265,
				PositionType.ZERO_BASED), "C", "T");
		Annotation anno6 = new SNVAnnotationBuilder(infoForward, change6, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), anno6.getTranscript().getAccession());
		Assert.assertEquals(10, anno6.getAnnoLoc().getRank());
		Assert.assertEquals("2061C>T", anno6.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", anno6.getProteinChange().toHGVSString());
		Assert.assertEquals(anno6.getEffects(), ImmutableSortedSet.of(VariantEffect.SYNONYMOUS_VARIANT));

		GenomeVariant change7 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6649264,
				PositionType.ZERO_BASED), "A", "G");
		Annotation anno7 = new SNVAnnotationBuilder(infoForward, change7, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), anno7.getTranscript().getAccession());
		Assert.assertEquals(10, anno7.getAnnoLoc().getRank());
		Assert.assertEquals("2060A>G", anno7.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Asp687Gly)", anno7.getProteinChange().toHGVSString());
		Assert.assertEquals(anno7.getEffects(), ImmutableSortedSet.of(VariantEffect.MISSENSE_VARIANT));

		GenomeVariant change8 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6649263,
				PositionType.ZERO_BASED), "G", "A");
		Annotation anno8 = new SNVAnnotationBuilder(infoForward, change8, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), anno8.getTranscript().getAccession());
		Assert.assertEquals(10, anno8.getAnnoLoc().getRank());
		Assert.assertEquals("2059G>A", anno8.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Asp687Asn)", anno8.getProteinChange().toHGVSString());
		Assert.assertEquals(anno8.getEffects(), ImmutableSortedSet.of(VariantEffect.MISSENSE_VARIANT));

		GenomeVariant change9 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6649262,
				PositionType.ZERO_BASED), "T", "G");
		Annotation anno9 = new SNVAnnotationBuilder(infoForward, change9, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), anno9.getTranscript().getAccession());
		Assert.assertEquals(10, anno9.getAnnoLoc().getRank());
		Assert.assertEquals("2058T>G", anno9.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Cys686Trp)", anno9.getProteinChange().toHGVSString());
		Assert.assertEquals(anno9.getEffects(), ImmutableSortedSet.of(VariantEffect.MISSENSE_VARIANT));

		GenomeVariant change10 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6649261,
				PositionType.ZERO_BASED), "G", "C");
		Annotation anno10 = new SNVAnnotationBuilder(infoForward, change10, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), anno10.getTranscript().getAccession());
		Assert.assertEquals(10, anno10.getAnnoLoc().getRank());
		Assert.assertEquals("2057G>C", anno10.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Cys686Ser)", anno10.getProteinChange().toHGVSString());
		Assert.assertEquals(anno10.getEffects(), ImmutableSortedSet.of(VariantEffect.MISSENSE_VARIANT));
	}

	@Test
	public void testReverseFirstCDSBases() throws InvalidGenomeVariant {
		// We check the first 10 CDS bases and compared them by hand to Mutalyzer results.

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 23694497,
				PositionType.ZERO_BASED), "T", "A");
		Annotation anno1 = new SNVAnnotationBuilder(infoReverse, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoReverse.getAccession(), anno1.getTranscript().getAccession());
		Assert.assertEquals(1, anno1.getAnnoLoc().getRank());
		Assert.assertEquals("1A>T", anno1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("0?", anno1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.MISSENSE_VARIANT, VariantEffect.START_LOST),
				anno1.getEffects());

		GenomeVariant change2 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 23694496,
				PositionType.ZERO_BASED), "A", "G");
		Annotation anno2 = new SNVAnnotationBuilder(infoReverse, change2, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoReverse.getAccession(), anno2.getTranscript().getAccession());
		Assert.assertEquals(1, anno2.getAnnoLoc().getRank());
		Assert.assertEquals("2T>C", anno2.getCDSNTChange().toHGVSString());
		Assert.assertEquals("0?", anno2.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.MISSENSE_VARIANT, VariantEffect.START_LOST),
				anno2.getEffects());

		GenomeVariant change3 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 23694495,
				PositionType.ZERO_BASED), "C", "T");
		Annotation anno3 = new SNVAnnotationBuilder(infoReverse, change3, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoReverse.getAccession(), anno3.getTranscript().getAccession());
		Assert.assertEquals(1, anno3.getAnnoLoc().getRank());
		Assert.assertEquals("3G>A", anno3.getCDSNTChange().toHGVSString());
		Assert.assertEquals("0?", anno3.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.MISSENSE_VARIANT, VariantEffect.START_LOST),
				anno3.getEffects());

		GenomeVariant change4 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 23694494,
				PositionType.ZERO_BASED), "C", "A");
		Annotation anno4 = new SNVAnnotationBuilder(infoReverse, change4, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoReverse.getAccession(), anno4.getTranscript().getAccession());
		Assert.assertEquals(1, anno4.getAnnoLoc().getRank());
		Assert.assertEquals("4G>T", anno4.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Ala2Ser)", anno4.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.MISSENSE_VARIANT), anno4.getEffects());

		GenomeVariant change5 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 23694493,
				PositionType.ZERO_BASED), "G", "A");
		Annotation anno5 = new SNVAnnotationBuilder(infoReverse, change5, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoReverse.getAccession(), anno5.getTranscript().getAccession());
		Assert.assertEquals(1, anno5.getAnnoLoc().getRank());
		Assert.assertEquals("5C>T", anno5.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Ala2Val)", anno5.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.MISSENSE_VARIANT), anno5.getEffects());

		GenomeVariant change6 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 23694492,
				PositionType.ZERO_BASED), "T", "G");
		Annotation anno6 = new SNVAnnotationBuilder(infoReverse, change6, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoReverse.getAccession(), anno6.getTranscript().getAccession());
		Assert.assertEquals(1, anno6.getAnnoLoc().getRank());
		Assert.assertEquals("6A>C", anno6.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", anno6.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.SYNONYMOUS_VARIANT), anno6.getEffects());

		GenomeVariant change7 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 23694491,
				PositionType.ZERO_BASED), "C", "T");
		Annotation anno7 = new SNVAnnotationBuilder(infoReverse, change7, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoReverse.getAccession(), anno7.getTranscript().getAccession());
		Assert.assertEquals(1, anno7.getAnnoLoc().getRank());
		Assert.assertEquals("7G>A", anno7.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Ala3Thr)", anno7.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.MISSENSE_VARIANT), anno7.getEffects());

		GenomeVariant change8 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 23694490,
				PositionType.ZERO_BASED), "G", "A");
		Annotation anno8 = new SNVAnnotationBuilder(infoReverse, change8, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoReverse.getAccession(), anno8.getTranscript().getAccession());
		Assert.assertEquals(1, anno8.getAnnoLoc().getRank());
		Assert.assertEquals("8C>T", anno8.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Ala3Val)", anno8.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.MISSENSE_VARIANT), anno8.getEffects());

		GenomeVariant change9 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 23694489,
				PositionType.ZERO_BASED), "G", "C");
		Annotation anno9 = new SNVAnnotationBuilder(infoReverse, change9, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoReverse.getAccession(), anno9.getTranscript().getAccession());
		Assert.assertEquals(1, anno9.getAnnoLoc().getRank());
		Assert.assertEquals("9C>G", anno9.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", anno9.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.SYNONYMOUS_VARIANT), anno9.getEffects());

		GenomeVariant change10 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 23694488,
				PositionType.ZERO_BASED), "T", "C");
		Annotation anno10 = new SNVAnnotationBuilder(infoReverse, change10, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoReverse.getAccession(), anno10.getTranscript().getAccession());
		Assert.assertEquals(1, anno10.getAnnoLoc().getRank());
		Assert.assertEquals("10A>G", anno10.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Thr4Ala)", anno10.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.MISSENSE_VARIANT), anno10.getEffects());
	}

	@Test
	public void testReverseLastCDSBases() throws InvalidGenomeVariant {
		// Here, we start off 3 positions before the end (2 positions before the inclusive end).

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 23688461,
				PositionType.ZERO_BASED), "T", "C");
		Annotation anno1 = new SNVAnnotationBuilder(infoReverse, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoReverse.getAccession(), anno1.getTranscript().getAccession());
		Assert.assertEquals(3, anno1.getAnnoLoc().getRank());
		Assert.assertEquals("1413A>G", anno1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", anno1.getProteinChange().toHGVSString());
		Assert.assertEquals(
				ImmutableSortedSet.of(VariantEffect.STOP_RETAINED_VARIANT, VariantEffect.SYNONYMOUS_VARIANT),
				anno1.getEffects());

		GenomeVariant change2 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 23688462,
				PositionType.ZERO_BASED), "T", "G");
		Annotation anno2 = new SNVAnnotationBuilder(infoReverse, change2, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoReverse.getAccession(), anno2.getTranscript().getAccession());
		Assert.assertEquals(3, anno2.getAnnoLoc().getRank());
		Assert.assertEquals("1412A>C", anno2.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(*471Serext*9)", anno2.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.STOP_LOST), anno2.getEffects());

		GenomeVariant change3 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 23688463,
				PositionType.ZERO_BASED), "A", "T");
		Annotation anno3 = new SNVAnnotationBuilder(infoReverse, change3, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoReverse.getAccession(), anno3.getTranscript().getAccession());
		Assert.assertEquals(3, anno3.getAnnoLoc().getRank());
		Assert.assertEquals("1411T>A", anno3.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(*471Lysext*9)", anno3.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.STOP_LOST), anno3.getEffects());

		GenomeVariant change4 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 23688464,
				PositionType.ZERO_BASED), "G", "C");
		Annotation anno4 = new SNVAnnotationBuilder(infoReverse, change4, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoReverse.getAccession(), anno4.getTranscript().getAccession());
		Assert.assertEquals(3, anno4.getAnnoLoc().getRank());
		Assert.assertEquals("1410C>G", anno4.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Asp470Glu)", anno4.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.MISSENSE_VARIANT), anno4.getEffects());

		GenomeVariant change5 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 23688465,
				PositionType.ZERO_BASED), "T", "C");
		Annotation anno5 = new SNVAnnotationBuilder(infoReverse, change5, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoReverse.getAccession(), anno5.getTranscript().getAccession());
		Assert.assertEquals(3, anno5.getAnnoLoc().getRank());
		Assert.assertEquals("1409A>G", anno5.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Asp470Gly)", anno5.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.MISSENSE_VARIANT), anno5.getEffects());

		GenomeVariant change6 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 23688466,
				PositionType.ZERO_BASED), "C", "A");
		Annotation anno6 = new SNVAnnotationBuilder(infoReverse, change6, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoReverse.getAccession(), anno6.getTranscript().getAccession());
		Assert.assertEquals(3, anno6.getAnnoLoc().getRank());
		Assert.assertEquals("1408G>T", anno6.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Asp470Tyr)", anno6.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.MISSENSE_VARIANT), anno6.getEffects());

		GenomeVariant change7 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 23688467,
				PositionType.ZERO_BASED), "C", "G");
		Annotation anno7 = new SNVAnnotationBuilder(infoReverse, change7, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoReverse.getAccession(), anno7.getTranscript().getAccession());
		Assert.assertEquals(3, anno7.getAnnoLoc().getRank());
		Assert.assertEquals("1407G>C", anno7.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", anno7.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.SYNONYMOUS_VARIANT), anno7.getEffects());

		GenomeVariant change8 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 23688468,
				PositionType.ZERO_BASED), "G", "T");
		Annotation anno8 = new SNVAnnotationBuilder(infoReverse, change8, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoReverse.getAccession(), anno8.getTranscript().getAccession());
		Assert.assertEquals(3, anno8.getAnnoLoc().getRank());
		Assert.assertEquals("1406C>A", anno8.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Thr469Lys)", anno8.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.MISSENSE_VARIANT), anno8.getEffects());

		GenomeVariant change9 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 23688469,
				PositionType.ZERO_BASED), "T", "C");
		Annotation anno9 = new SNVAnnotationBuilder(infoReverse, change9, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoReverse.getAccession(), anno9.getTranscript().getAccession());
		Assert.assertEquals(3, anno9.getAnnoLoc().getRank());
		Assert.assertEquals("1405A>G", anno9.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Thr469Ala)", anno9.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.MISSENSE_VARIANT), anno9.getEffects());

		GenomeVariant change10 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 23688470,
				PositionType.ZERO_BASED), "A", "G");
		Annotation anno10 = new SNVAnnotationBuilder(infoReverse, change10, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoReverse.getAccession(), anno10.getTranscript().getAccession());
		Assert.assertEquals(3, anno10.getAnnoLoc().getRank());
		Assert.assertEquals("1404T>C", anno10.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", anno10.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.SYNONYMOUS_VARIANT), anno10.getEffects());
	}

	//
	// Various Stop-Loss Variants
	//

	@Test
	public void testRealWorldCase_uc001hjk_3() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory.parseKnownGenesLine(refDict,
				"uc001hjk.3	chr1	+	212797788	212800120	212798219	212800004	1	212797788,	212800120,	Q8IYT1	uc001hjk.3");
		this.builderForward
				.setSequence("aaatatggctgcacagggggtttaggcagagagggtcgtcacttcctgtcactaggactacagtttgtagtctctctcagggagagagactacaaactgtaattaaaaaaatcagaggtggaggtcagggggttaggtccagaagaaaacctctctggagcccaccctgcatctctacgtcttggaagagtctggagtccaggcaccctacagcaccccccacagcaatcatccgtgctcccacccccaccccggcctctgggaggctctgaggcaacagtgggggcctgtggggtggggtgggattgagagaccagctgcagagacatctttgtgacccaggagcaccactggtccctcatctgccaccgaggccaaggaagacccagagacctgcctcaccagtgctgccgttgtgctgaaagacaaccatgaatgctgattttctgctgccgtattatacggcccagagtggctccagcatgagcatgttcaacaccaccatggggaaactgcagcgacaactgtacaagggggagtacgatatattcaagtatgcaccgatatttgagagcgactttatccagatcaccaaaaggggagaagtgattgatgtgcacaaccgtgtccgtatggtgaccatgggcattgcacgtaccagccccatcctcccactcccagatgtcatgctactggcacgaccggccaccggctgcgaagagtatgctggacatggccaggccaccaagagaaaaaaacgcaaggcagcaaagaacttagagctcaccaggcttctgcccctgaggtttgtacggatctctgttcaagaccatgagaaacaacagctgcgcctgaagttcgccactggcagatcttgctatctgcaattgtgtcccgctcttgacacacgggatgacctctttgcctattgggaaaaactaatttacctcttgcggccacccatggagagtaacagcagtacctgtggcattccagctgaagacatgatgtggatgcctgtgtttcaggaagacaggaggagcctgggagccgtgaaccttcaaggaaagggggatcaggaccaggtcagcatccaaagcctccacatggtctctgaggtgtgtggggccacctctgctgcttatgctggaggggagggactccaaaatgactttaacaaacccactaatgtgctcaatgcatccatccccaaaacatctacagaacttgctgaggagccagcaacaggggggattaaagaggcagcagcagcaggggcagctgcaggggcagcaacaggcaccgtagcaggtgccttgagtgtggcagcagccaattctgcccctggacaggtgagcgcagccatagctggggcggccaccatcggtgcaggaggaaacaaaggcaacatggcccttgcaggcactgccagcatggctccaaacagcacgaaggtggctgtggcaggggctgcaggcaagtcctcagagcatgtttccagcgcatccatgagcctttcccgagagggcagtgtgagcctggccattgcaggagtagtactgaccagcaggacagctgcagaagcagacatggatgcagcagcgggacctcccgtctccacccggcagagcaagagcagcctgagtggacagcatggaagggagcgaacccaggccagcgctgaaggctgcaaggaggggagggaaagaagggaaaaggacagggctctcggaaggagttcccatcgccgcaggacaggtgaaagccgccacaaaacaaggggagacaagattgcccaaaagtcctccagcaggtcctcattcagccacagagccaatagagatgacaaaaaggagaaaggctgtggcaacccggggagcagcaggcacagggactcgcataaaggtgtcagccacacgcccatctcaaaggagtccaggacctctcacaaatctgggaggagcttatggaccaccagttccggttccagcaagggacttggcagggtcagctctttcctgaggaacgtcagagccaaccttactacaaaagtagtgggcacaccacatggcagagatgtgaacgtcatggctaagatggcggagaggagcaccaacgtggccatcgccgagacagcagagggtggccaggggctggagacggttggttctatgacaccggacatcatggagacagtgacctttgaagcccattaaataagacccagagctggaagctgcaaaggagcccagagctcatgggagtgtccctggaaagcctattccagcgttctttactgccgtttaaataaagaatcatacatctgaaagtg"
						.toUpperCase());
		this.builderForward.setGeneSymbol("FAM71A");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 212799881,
				PositionType.ZERO_BASED), "A", "T");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(0, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("1663A>T", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Lys555*)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.STOP_GAINED), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc003npv_2() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc003npv.2	chr6	+	30227338	30234728	30228329	30231204	7	30227338,30227703,30228173,30228980,30229363,30230071,30231052,	30227474,30227861,30228384,30229256,30229480,30230119,30234728,	Q6ZSK9	uc003npv.2");
		this.builderForward
				.setSequence("acgatcccggcactacagtcccggcgcaaccacccgcactcagattctccccaaacgccaaggatgggggtcatggctccccgaaccctcctcctgctgctcttgggggccctggccctgaccgagacctgggccgcgactccgtgagtccgaggatggagcggcgggcgccgtgggtggagcaggaggggctggagtattgggaccaggagacacggaacgccaagggccacgcgcagatttaccgagtgaacctgcggaccctgctccgctattacaaccagagcgaggccggtatgaacagttcgcctacgatggcaaggattacatcgccctgaacgaggacctgcactcctggaccgccgcgaacacagcggctcagatctcccagcacaagtgggaagcggacaaatactcagagcaggtcagggcctacctgagggcaagtgcatggagtggctccgcagacacctggagaacgggaaggagacgctgcagcacgcggatcccccaaaggcacatgtgacccagcaccccatctctgaccatgaggccaccctgaggtgctgggccctgggcctctaccctgcggagatcacactgacctggcagcaggatggggaggaccagacccaggacacggagcttgtggagaccaggcctgcaggggacggaaccttccagaagtgggtggctgtagtggtgccttccggagaggagcagagatacatgtgccatgtgcagcatgaggggctgccagagcccctcaccctgagatgggagccgtcttctcagcccaccatccccatcgtgggcatcgttgctggcctgtttctccttggagctgtggtcactggagctgtggttgctgctgcgatgtggaggaagaaaagctcaggcagcaattgtgctcagtactctgatgcatctcatgatacttgtaaagaggactatgcctgttcctgttctggtgtctgcgttctgatctctttctcccctgggtgtccctcatctctgacagcagcaggagtcatttttcctgtcattaaccccacaaggtggaaggcagcccctgcacacagaagtctgtggtattaagagatgaattttcaagcccgtgcagcttttaccctatttccagggctctttcttggattgtattttctatcttttccccaacctttttaaaggaactagattctgaaattagcagagaagagggatgccacaagttctcatcttaggtaactttctagtggaactcctcttctgctcagctctcctacccactctcccttccctgagttgtagtaatcctagcactggctctaatgcaaactcatggatctataaagcaaagtctaacttagatttatatttgtttggaaattgggattcatagtcaaagattgttctttcctaagagggaaatataattgcatgctgcagtgtgcagagggttggtgtgaaggagggatgcagggaggaagggagggaggacacacaagcagcactgctgggaaaagcacaggcggcctggatgtcagtgtgaggggaccttgtgctgtcgttgctgcaaaaccgcatttggcctgaggctatgttaataaagatactgcctttagaataggaggtgctctacagtgatgattcattcagccgacatttgctgtctgccagacatatgacagaatgtttttgcatctggggaaagtcattgaagtaaaatcagaaaaatctctagccttgtggagcatgtgttccagtgggaagaggcagacggtacatacactctaatatatgcagagtaaatgaggaaagtgttagaaggtgataagtgctgtggaacaggtgatcagagtatgggttgtgggacagagaaggtagctattgtgccggggttgtcagcgtgggccttgttgggaaggtgacctttgatgaaatatttgaaggacataaaggaatttgtcatgagggtatctggaagaagttttttctagggagtaggaaccttcagtgtcagtgtaccagggcaggatcatgtctgtgtgttctgggaagaacacgggatcgggtatggctagagcagagagtcactgagataaggtcaggggtttggtcagatcatgtgggcatagggctcaagtatgtgggaaggattttgattttgaatgagatagttttaagcagaataaagacatgccacaacttctcttttaaaaggatcactgtagctgctctgctgagaacagaatccaaaggccggcgatgagcaaggcaggtgggaaaactgtaggaaatgagtgcagtatttcaggctggagatgtcggttacttcaactggggtgtgagcagtggaaatagtgggacgtgattggattcctactatttccaatcactttataccgcattttctaatggactaaatctggggtatgagaaagaagagtaaaggataccaaaaatgtcagactgtgactaaaaagagttgccatcagctgagaatgagaagactagcaggagcatatgagaggaggggacgtcgcaggcagtcactatgggagacgtgggatctgagatgccgctgagaaataccagtgaggtagtcgggttggcagttggacagatgaatctggagacatttaggagaaatagacttgggaggtgatgtcatataacagttatttaaagccttgagtctgaatgacgtctccaagggagtgattggctgtagaagagaacaggaacaaggactgaacactaggcctctgttgctaaaggatctgatcagacaacacacctagatcagactgcacagtcctgaccccacatctagaaggtacatagaccagggagttctagactttcctgtggacaggaatcacctggacatcaccttaagtctaagctgatctggaatcgagaatgagatttcctacttatataatgttgctgttggcgctgatgctgctggtcttcagatcccacttttggtagcaagaacacagaccaggattcctaggctatgcatcagcctcgcctgtgaggcttgttaataagcaattcctgcactccatgcgcaacattctgacacaggggcatctgtggagaggcctgagtattctacaacaagcccacagcaaacctggtgctcagccagatttgatatcactgagatcagtagttggagaatgcccaggatggggaggggtctcagacccacatttaagtgttgctttattctgggttttttatttatttatttatttatttttaaggaggatgtgtttctttaattataagacaggatgctgagagataaatgtcattttctctatcatggggtatagccagatggaagattgagaagtggctcacagctcagcagaatgaaaaaatatctgaatgctgctttctgaaactactctccagaatgatttcacactcactccttggagcaaacaatgacttgcaaatttttctaatttaaacataaaggagtgtacatattggtattagtattcattttattttggggaagggcactgtattagtccatagtccgttttcacactgccgataaagacatacccaacattgggaagaaaaagaggtttaattggacttacagttccatttggctggggaggcctcagaatcatggtgggaggcgaaaggcacttcttacatggtggtggcaagagaaaatgaggaagaagcaaatgccaaaacccctgataaacacattggatctcaggagacttattcattatcatgagaatagcatgggaaagactggcccccatgattcaattacctccccctgggtccctcccacaacatgtgggaattctgggagatacaattcaagttgagatttgggtggggacacagccaaaccacattggacacagaaccaggtttgaagctacacagccaggaacataatccacagccaccctaattcagatctctcataggaaccactgtccctgctcctgagcacagatgctactgcatatacctctgataccctgatggccgacactgggccctgtggcaaagactgctatcactgctgctcctgagaactgctccactactgctcctcagccatctttaccaaaatgcagtatttactgtcccagcctctctgtgtcatctcatcctgattagaagcccacatgtggttatctaaattgtgcagccaaagcctcttgcagtgtttaactgcaataatgttggggaaagtgaatttttctcctttgtagaaggaggtagtccctgccttctaataagactcttcaacataggaagagaattcagttgctggaggtagaggggtgagggatggaaaaagaatgacaaatttcaattcctagaatcatgttctgagactagaactttatctagtacattgcaggcacctgggtttggttgagtgtataataaatgacatagttcaacttattcccttgacagtttgttttggggtccagcttttgtctaccccagttttcacacacagatacgtggagaagcattgtgtgatggtaaaatgtttacttgaaagcctttttccctatctttgtctcttgctaggattaaaaacccgtatctgt"
						.toUpperCase());
		this.builderForward.setGeneSymbol("HLA-L");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 6, 30229462,
				PositionType.ZERO_BASED), "G", "A");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(4, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("431G>A", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Trp144*)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.STOP_GAINED), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc010rht_2() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory.parseKnownGenesLine(refDict,
				"uc010rht.2	chr11	+	48285412	48286330	48285412	48286330	1	48285412,	48286330,	Q8NH49	uc010rht.2");
		this.builderForward
				.setSequence("atggttgctacaaacaatgtgactgaaataattttcgtgggattttcccagaattggagtgagcagagggtcatttctgtgatgtttctcctcatgtacacagctgttgtgctgggcaatggcctcattgtggtgaccatcctggccagcaaagtgctcacctcccccatgtatttctttctcagctacttatcctttgtggagatctgctactgttctgtcatggcccccaagcttatctttgactcctttatcaagaggaaagtcatttctctcaagggctgcctcacacagatgttttccctccatttctttggtggcactgaggcctttctcctgatggtgatggcctatgaccgctatgtggccatctgcaagcccttgcactacatggccatcatgaaccagcgaatgtgtggtctcctcgtgaggatagcatggggcgggggcctgctgcattctgttgggcaaaccttcctgattttccagctcccgttctgtggccccaacatcatggaccactacttctgtgatgtccacccagtgctggagctggcctgcgcagacaccttcttcattagcctgctgatcatcaccaatggcggctccatctccgtagtcagtttcttcgtgctgatggcttcctacctgatcatcctgcacttcctgagaagccacaacttggaggggcagcacaaggccctctccacctgtgcctctcatgtcacagttgtcgacctgttcttcataccttgctccttggtctatattaggccctgtgtcaccctccctgcagacaagatagttgctgtattttatacagtggtcacacctctcttaaaccctgtgatttactccttcaggaatgctgaagtgaaaaatgccatgaggagatttattgggggaaaagtaatttga"
						.toUpperCase());
		this.builderForward.setGeneSymbol("OR4X1");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 11, 48286230,
				PositionType.ZERO_BASED), "T", "A");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(0, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("819T>A", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Tyr273*)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.STOP_GAINED), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc010zdp_2() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc010zdp.2	chr2	-	172173912	172291312	172180768	172291191	9	172173912,172182350,172182551,172187068,172188313,172193962,172195693,172216931,172291183,	172180872,172182416,172182658,172187208,172188377,172194012,172196064,172217023,172291312,	B4DLT0	uc010zdp.2");
		this.builderForward
				.setSequence("taaagggaggccccagcgcgttaaaaccgcatccccgcaggaggctcggccgccgcgcgcccgcggccctccgtcccgccccggccggctggcggtcacctggcacaccagcgcccgcaggatgcccagggatcacatgcagtggtctaaggaagaagaagcagcagccagaaaaaaagtaaaagaaaactcagctgtgcgagtccttctggaagagcaagttaagtatgagagagaagctagtaaatactgggacacattttacaagattcataagaataagtttttcaaggatcgtaattggctgttgagggaatttcctgaaattcttccagttgatcaaaaacctgaagagaaggcgagagaatcatcatgggatcatgtaaaaactagtgctacaaatcgtttctcaagaatgcactgtcctactgtgcctgatgaaaaaaatcattatgagaaaagttctggttcttcagaaggtcaaagcaaaacagaatctgatttttccaacctagactctgaaaaacacaaaaaaggacctatggagactggattgtttcctggtagcaatgccactttcaggatactagaggttggttgtggagctggaaatagtgtgtttccaattttgaacactttggagaactctccggagtcctttctgtattgttgtgattttgcttctggagctgtggagctcgtaaagtcacactcgtcctacagagcaacccagtgttttgcctttgttcatgatgtatgtgatgatggcttaccttacccttttccagatgggatcctggatgtcattctccttgtctttgtgctctcttctattcatcctgacaggatgcaaggtgttgtaaaccgactgtccaagttactgaaacctgggggaatgctgttatttcgagactatggaagatatgataagactcagcttcgttttaaaaagggacattgtttatctgaaaatttttatgttcgaggagatggtaccagagcatatttctttacaaaaggggaagtccacagtatgttctgcaaagccagtttagatgaaaagcaaaatctggttgatcgccgcttacaagttaataggaaaaaacaagtgaaaatgcactgagtgtggattcaaggcaaattccagaaaccattgcaccagactcagaatagctccaatatggtatctacactcctttcacaagactgaactttgtaacatgttaaggtacaaagccagaggactgtgctattcaaggactactgtaagtctattgtttctcaaaagacaatgagaaaaaaagaagagaatttgtatttcctgccgttttgtcataggtgagctcctttgtgcattttaagcacatgtaagtggttcagcacagtatgcctttttctgtgctttgaaaacttgatatgctcaagcttgtttgaatttattacatctaaccattttgcttgttccttgatttttataagcattcaattaagttagtattatgtcaagtaattttgagaaaatgtaacttgacattttttgcaagtaaaaaaaattgtttatttgtttaggcttagtaaaccagttcccaaacacagtcagactcttcccattgtcatctgattgcagagagaaagcacaccttatttccagggaaagctacaacaagcccaaggtcaaagtgtattattttttgtcttgttgttgtctattttctcccaatttttttttgaaattcagaggctcatatctgaaatagaattttagttcctctttcctttcctaaaattggggaagtacagcccatgctgacattattttcaggctattcttagatatacaagttgttaggccaggtgcaatggctcgcacctgtaatcccagcactttggggggctgaggcaggcagatcggttgagctcaggagttcaagaccagcctggacaacatggcaaaaccctgtctctcccaagaatacaaaaattagccaggcatggtggcacacacctgtggtcccagctactcaggagactgaggtgggaggatcgcttgagcctcggaggcggaggttgcagtgagctgagattgtaccactgcgctccaaactgggtgacatggtgagaccttgtctccaaaaaaaaaaaaaaaaaaaaaaaaggtatgtaagctgacaagctcttgtatcaagggcttttgaaccagagcaacttcatcttgaatagggactgtgtaaaatgaagctgagacctactaggctgcaatcccaggaggttaagacattcttagtcacaggatgagataggaagtcagtacaagatacaggtcatacagaccttgctgataaaacaggttgcagtgaagaagccggccaaatgccaccaaaaccaagatggcaatgagagtgtcctctggctgtcctcactgctacactcccaccagtgccatgatagtttacaaatgccatggcaacatcaggaagttacccatatggtctacaaaggggaggcctgaataatccaccccttgtgtagcatataatcaagaaataaccttaaaaatgggcaaccagtagcccttggggctgctctgcctatggagtagccattctttatgttttactttttttttttttttttttttgagatggagtcttgctctggcacccaggctggagtgcagtggcgtgatctcagctcactgccagctccacctcccgggttcacgccattctcttgcttcagcctcctgagtagctgggactacaagcacccaccaccatgcccagctaattttttttttttttgtatttttagtagagacggggtttcaccatgttagccaggatggtctcaatctcctgaccttgtgatccgcccgcctcggcctcccaaagtgctgggattacaggtgtgagccaccgcgcccagcctattctttacttaaacttgttttcactttgctgtatggactcaattctttcttgcacaagatccaagaatcctctcttggcgtctggatccagaccctttctggtatcagtggtaagctctgtttaagacaactgtcatacccaagagaattcttaaattgtggcagttaaaaatgtatttatcccctcgagggccgggcgcagtcgctcaagcccgtaatcccagcactttgggaggccgaggaggcggatcatgaggtcaggagttcaagaccagcctgaccaacactgtgaaaccctgtctctactaaaaatacaaaaaaaattagccaggcgtggtggtgggcacctgtaatcccagctactcgggaggctgaggcaggagaatcacttgaacccgggaggcagaggttgcagtgagccgagatgacaccactgccctccagcctgggtgacagttgttagactgcgtatcaaaaaaaaaaaaaaaaatttatttactcccttgcttcaaaaaggacataagttggcttgcaaatcacattttgtagaaatgaagtttttaagattagtctcatactcattacataatatttattgactacttacatattgagtgctaaattcagtggaataatatgaacaataaagacccagccttgtttatgctcttcagggagcataaatagagtacagctggcaaaacaaaatgtgcatatcccaaacctgtaaatcatgatctaagacaatatacaaacactcaaatagggaacttaccagctagcagactttgattatgaaccacagaacctagaataaataaatactctcttgaaagaaccatcattggaataattaagttccaactgttttttctttctgaattcacttaaaatccaatttctagaagaatatcattggtccagctttagtcattggttcatctcttaacaggctggggcagggcagcttaattgatgatgccaccagaatgggcaacatgggaaaaaccctatctctaccaaaaatacaaaaattagccagtgatggtggcacgcacctgttgtcccagctactcaggaggctgagctgggaggatcttttgagcctgggaggcagaggttgcagtgagctgagatcgcaccactgcactccaacctggagtggaaggaaatttcacttcctggaatggaagaaagttcttcaaaggaaaatcaagacactgctgtcaaaaaacaccaggggagggatgctgtccagtcacagccaacacatcatatagcagtggtagcagtagtaatagtatttccagagagagagagagagattagctctgaaaaagaatggagaagaatcaggtttcttgggggaggtaggtctcaggctagttcttgaatgtacattctcagcagatggtgagaagacaagtgtggtgtgggacattaatgatgagggataggtagaaaagatgggaagggggtcagcttgtagataacctagactagcaggtgtatgacttctagtctctgaactggagccatctgagatcagtttgagatatggtgtcttataaatgtattgagagaggttgaggagttgtctatagaatggaatggagtgaggtcagagatcagtggcaaggaagaccacataaaggtttttgcacaaacttgccttgatggcaagtcctcaaaagtacaattatcaagctttctgtttaaacatgcataagtcagtaaatataaacattaaaatttgcccaagactcttaactcagttgtatttcataattaaagtgtacattaaaaagccaaactagtcatgaaagtattacatacagcaagggcagtttagataagttaatgttgctatgggaaccccagcagatctccttgtgtctccgtttaaaaatcatggcccaaggtggtttcaaaaacaacagaaggtaaaattattctcacattctcacacatgtaaattgtactgtctctctcagttctaaatagcggattcaagccagcctcttgactggcacagtaaaattcggcttcgtgtctttctataatgtattacagcatgtgagttaacctttagaagcttcaaaaatacatcaaccaaatttggactgtgagacacaaactggtctcttttgggctctgtccagcatagccttcctattccacatggctgcgcagcagggccatttccaccagtgtcttctgcttgcctggtcagtctttctgtctcttaacctacctacaacaacttccttctcggggtttgctgctagatagaaatgtagattttggcacagaaagaaaagggaaagacctatttggaattttgtctatagatgttaagaaatcatgaagcgttgcaaatgtaactgcttgggtattagtttaaaatatttgataactgcatgaaattctaatttacccctcacatccataaagctaggaccagttggttaggcagagtttgatatttaaaaattcatcttctgagagagctgaatttcaaaaggaccattttctttccaggagtaaaaggtatattctttcatcttcttacagtacagaaacaatggaatatattgtttaaatgcacccttcatttttttctccatttctaaaaattcacttttgagcatcgtccaaatgtaaaaattctcacacatgaggaactgtttccgaaggttgtgagaaagtcaagataacagtttctattattaaaagttttgcattcggtatgacacatttgcctgtattgaaacacccatttaacattgacttctgagaacttatttgctttgcctctctcgattccatatgctttgtctacaaattgtgaacacgtcccggtttctacccgtgtgtgattctaagtcgttgctgtactctagcttgtacgtgaaaattcacccatatgcagatagctgccctcttggaggcaaaacttaaagctatcagaaccatctggattgttaaaccccaaaacttccaactcatcaaagtcctgaagcctgaatagatcacacattgttacttctaattggttttgatgtttgttatgctcctcacaagtgcagataacttcacatttgcatcactaatcgacctctttcaaagcctctgctcctcctgcttcctgcctctgttagtcatcaggagacttagtgttggcgtgggtattttccccgcttgtgaaagtgcattgtcaattctagagcattaaatatatgtgaggtattgttattaatctacaaggcagtacacacagtattccagcaacagtgactttttcaaaggttttatgagacattgtaagaaagacagtatctttccatggaaaaaagtgttgcctcagagtcaaacaggaagcatgggtcctagttcctattgaactgcttagctactcttaatttagctctgaatgctagattttgtgtatactatacacattatttaatcctcaaactcctaggaattcattttgatgataaccatttacagaagagatgttgggtaacttgctcaggataccacatctagtaagtagcagagtcacaactcggacccagaactctctgattccaaaacctctacttgctgctcttgtaatggatgggcccaaatgctgctgggaaaggcaccagggaagaaagagctttctgagtcaagcagaaaacgatagcagtggagaggagtttgtttcagacgaccttcaaggttatggctcattctagatccaatcattctagcaaataagtaggttacacttgtccacaattacaaattggaaaagaagagaaggaaaggaactgagaggaatgacaaagcaaaaagctttcaatcagttcttttcagcattctgcttctgcctagttctgcactcgccccaccccctcaggcttcagccttccacttggcatccctcctcgcagagcactctcacttgcatactcctcgcttaaagaaaaaaagcattctgtactgcagtggggaaaaaaaacccttaaaatgtcttcaaaaggattcagctctagggtaatgtaaaatatgtgaaatcaggaggtgtccataataaaatatgagtgaaaacatcgttatttaaaacattgtatgtacattaatgccacattaataccaattcaggcatgaaatgttgagcatttcttggaatacggtttcttggaatatggttggggatttgagaggcgcaaaggtacccgcaagatggctttggtgtagttataaatataaatagaagtaaaataacagttgcatcttcatttataatgaatctatattgtggcctcaaagctacagagaaaagtggatgccaaaatctgtctttcattaacatatgtcttaaatgacaaggaagaggtgttgaagataaaattactttttcctatgagtatgaatcagcaacatgcagtaacaagcaagtaataatagttctgcgtgtgagtgcctacagagggtaagaaaaatagcatcagattgaaaaagcaagtgcacctgcagaagggacccaggaggctgcagagcacagctcccttgagcatcatctcaaagtgtctcagagcacagtgaccctaattctggcaagtcaggttggcatgagatggatggagcacactgaaatctccagatggtcagacagcccaccctcagctgcccctggagctcggcaaaggcaagctacaaggcactgctacttggccaagagtttaaaacaacagcttctgattctacagtgtctgtggtttcacaaccacttaaccttaagtaaggaacagctagagttagctgaatgaataaatacacgttaacgttacataaagagggaaaaaaagcaatacttgagcacatagacttcaaatagtgtccagctagctgtgtccttggcatcccagtgatctgggtctttatctttgattttctccagcaggtaggcactgagtagatccagggcccagggcgggaaatgcttctgtatcagatgttacctatcaaataaaatgagcctttgattgcctcctaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("METTL8");
		this.infoForward = builderForward.build();
		// RefSeq NM_024770.3

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 2, 172180770,
				PositionType.ZERO_BASED), "A", "G");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(8, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("1000T>C", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(*334Argext*29)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.STOP_LOST), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc010zdo_2() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc010zdo.2	chr2	-	172173912	172291312	172180681	172248695	11	172173912,172180771,172182350,172182551,172187068,172188313,172193962,172195693,172216931,172248552,172291183,	172180768,172180872,172182416,172182658,172187208,172188377,172194012,172196064,172217023,172248707,172291312,	B3KW44	uc010zdo.2");
		this.builderForward
				.setSequence("taaagggaggccccagcgcgttaaaaccgcatccccgcaggaggctcggccgccgcgcgcccgcggccctccgtcccgccccggccggctggcggtcacctggcacaccagcgcccgcaggatgcccaggtactgcttaggatgaatatgatttggagaaattccatttcttgtctaaggctaggaaaggtgccacacagataccaaagtggttaccacccagtggcccctctgggatcaaggattttaactgacccagccaaagtttttgaacacaacatgtgggatcacatgcagtggtctaaggaagaagaagcagcagccagaaaaaaagtaaaagaaaactcagctgtgcgagtccttctggaagagcaagttaagtatgagagagaagctagtaaatactgggacacattttacaagattcataagaataagtttttcaaggatcgtaattggctgttgagggaatttcctgaaattcttccagttgatcaaaaacctgaagagaaggcgagagaatcatcatgggatcatgtaaaaactagtgctacaaatcgtttctcaagaatgcactgtcctactgtgcctgatgaaaaaaatcattatgagaaaagttctggttcttcagaaggtcaaagcaaaacagaatctgatttttccaacctagactctgaaaaacacaaaaaaggacctatggagactggattgtttcctggtagcaatgccactttcaggatactagaggttggttgtggagctggaaatagtgtgtttccaattttgaacactttggagaactctccggagtcctttctgtattgttgtgattttgcttctggagctgtggagctcgtaaagtcacactcgtcctacagagcaacccagtgttttgcctttgttcatgatgtatgtgatgatggcttaccttacccttttccagatgggatcctggatgtcattctccttgtctttgtgctctcttctattcatcctgacaggatgcaaggtgttgtaaaccgactgtccaagttactgaaacctgggggaatgctgttatttcgagactatggaagatatgataagactcagcttcgttttaaaaagggacattgtttatctgaaaatttttatgttcgaggagatggtaccagagcatatttctttacaaaaggggaagtccacagtatgttctgcaaagccagtttagatgaaaagcaaaatctggttgatcgccgcttacaagttaataggaaaaaacaagtgaaaatgcaccgagtgtggattcaaggcaaattccagaaaccattgcaccagactcagaatagctccaatatggtatctacactcctttcacaagactgaactttgtaacatgttaaggtacaaagccagaggactgtgctattcaaggactactgtaagtctattgtttctcaaaagacaatgagaaaaaaagaagagaatttgtatttcctgccgttttgtcataggtgagctcctttgtgcattttaagcacatgtaagtggttcagcacagtatgcctttttctgtgctttgaaaacttgatatgctcaagcttgtttgaatttattacatctaaccattttgcttgttccttgatttttataagcattcaattaagttagtattatgtcaagtaattttgagaaaatgtaacttgacattttttgcaagtaaaaaaaattgtttatttgtttaggcttagtaaaccagttcccaaacacagtcagactcttcccattgtcatctgattgcagagagaaagcacaccttatttccagggaaagctacaacaagcccaaggtcaaagtgtattattttttgtcttgttgttgtctattttctcccaatttttttttgaaattcagaggctcatatctgaaatagaattttagttcctctttcctttcctaaaattggggaagtacagcccatgctgacattattttcaggctattcttagatatacaagttgttaggccaggtgcaatggctcgcacctgtaatcccagcactttggggggctgaggcaggcagatcggttgagctcaggagttcaagaccagcctggacaacatggcaaaaccctgtctctcccaagaatacaaaaattagccaggcatggtggcacacacctgtggtcccagctactcaggagactgaggtgggaggatcgcttgagcctcggaggcggaggttgcagtgagctgagattgtaccactgcgctccaaactgggtgacatggtgagaccttgtctccaaaaaaaaaaaaaaaaaaaaaaaaggtatgtaagctgacaagctcttgtatcaagggcttttgaaccagagcaacttcatcttgaatagggactgtgtaaaatgaagctgagacctactaggctgcaatcccaggaggttaagacattcttagtcacaggatgagataggaagtcagtacaagatacaggtcatacagaccttgctgataaaacaggttgcagtgaagaagccggccaaatgccaccaaaaccaagatggcaatgagagtgtcctctggctgtcctcactgctacactcccaccagtgccatgatagtttacaaatgccatggcaacatcaggaagttacccatatggtctacaaaggggaggcctgaataatccaccccttgtgtagcatataatcaagaaataaccttaaaaatgggcaaccagtagcccttggggctgctctgcctatggagtagccattctttatgttttactttttttttttttttttttttgagatggagtcttgctctggcacccaggctggagtgcagtggcgtgatctcagctcactgccagctccacctcccgggttcacgccattctcttgcttcagcctcctgagtagctgggactacaagcacccaccaccatgcccagctaattttttttttttttgtatttttagtagagacggggtttcaccatgttagccaggatggtctcaatctcctgaccttgtgatccgcccgcctcggcctcccaaagtgctgggattacaggtgtgagccaccgcgcccagcctattctttacttaaacttgttttcactttgctgtatggactcaattctttcttgcacaagatccaagaatcctctcttggcgtctggatccagaccctttctggtatcagtggtaagctctgtttaagacaactgtcatacccaagagaattcttaaattgtggcagttaaaaatgtatttatcccctcgagggccgggcgcagtcgctcaagcccgtaatcccagcactttgggaggccgaggaggcggatcatgaggtcaggagttcaagaccagcctgaccaacactgtgaaaccctgtctctactaaaaatacaaaaaaaattagccaggcgtggtggtgggcacctgtaatcccagctactcgggaggctgaggcaggagaatcacttgaacccgggaggcagaggttgcagtgagccgagatgacaccactgccctccagcctgggtgacagttgttagactgcgtatcaaaaaaaaaaaaaaaaatttatttactcccttgcttcaaaaaggacataagttggcttgcaaatcacattttgtagaaatgaagtttttaagattagtctcatactcattacataatatttattgactacttacatattgagtgctaaattcagtggaataatatgaacaataaagacccagccttgtttatgctcttcagggagcataaatagagtacagctggcaaaacaaaatgtgcatatcccaaacctgtaaatcatgatctaagacaatatacaaacactcaaatagggaacttaccagctagcagactttgattatgaaccacagaacctagaataaataaatactctcttgaaagaaccatcattggaataattaagttccaactgttttttctttctgaattcacttaaaatccaatttctagaagaatatcattggtccagctttagtcattggttcatctcttaacaggctggggcagggcagcttaattgatgatgccaccagaatgggcaacatgggaaaaaccctatctctaccaaaaatacaaaaattagccagtgatggtggcacgcacctgttgtcccagctactcaggaggctgagctgggaggatcttttgagcctgggaggcagaggttgcagtgagctgagatcgcaccactgcactccaacctggagtggaaggaaatttcacttcctggaatggaagaaagttcttcaaaggaaaatcaagacactgctgtcaaaaaacaccaggggagggatgctgtccagtcacagccaacacatcatatagcagtggtagcagtagtaatagtatttccagagagagagagagagattagctctgaaaaagaatggagaagaatcaggtttcttgggggaggtaggtctcaggctagttcttgaatgtacattctcagcagatggtgagaagacaagtgtggtgtgggacattaatgatgagggataggtagaaaagatgggaagggggtcagcttgtagataacctagactagcaggtgtatgacttctagtctctgaactggagccatctgagatcagtttgagatatggtgtcttataaatgtattgagagaggttgaggagttgtctatagaatggaatggagtgaggtcagagatcagtggcaaggaagaccacataaaggtttttgcacaaacttgccttgatggcaagtcctcaaaagtacaattatcaagctttctgtttaaacatgcataagtcagtaaatataaacattaaaatttgcccaagactcttaactcagttgtatttcataattaaagtgtacattaaaaagccaaactagtcatgaaagtattacatacagcaagggcagtttagataagttaatgttgctatgggaaccccagcagatctccttgtgtctccgtttaaaaatcatggcccaaggtggtttcaaaaacaacagaaggtaaaattattctcacattctcacacatgtaaattgtactgtctctctcagttctaaatagcggattcaagccagcctcttgactggcacagtaaaattcggcttcgtgtctttctataatgtattacagcatgtgagttaacctttagaagcttcaaaaatacatcaaccaaatttggactgtgagacacaaactggtctcttttgggctctgtccagcatagccttcctattccacatggctgcgcagcagggccatttccaccagtgtcttctgcttgcctggtcagtctttctgtctcttaacctacctacaacaacttccttctcggggtttgctgctagatagaaatgtagattttggcacagaaagaaaagggaaagacctatttggaattttgtctatagatgttaagaaatcatgaagcgttgcaaatgtaactgcttgggtattagtttaaaatatttgataactgcatgaaattctaatttacccctcacatccataaagctaggaccagttggttaggcagagtttgatatttaaaaattcatcttctgagagagctgaatttcaaaaggaccattttctttccaggagtaaaaggtatattctttcatcttcttacagtacagaaacaatggaatatattgtttaaatgcacccttcatttttttctccatttctaaaaattcacttttgagcatcgtccaaatgtaaaaattctcacacatgaggaactgtttccgaaggttgtgagaaagtcaagataacagtttctattattaaaagttttgcattcggtatgacacatttgcctgtattgaaacacccatttaacattgacttctgagaacttatttgctttgcctctctcgattccatatgctttgtctacaaattgtgaacacgtcccggtttctacccgtgtgtgattctaagtcgttgctgtactctagcttgtacgtgaaaattcacccatatgcagatagctgccctcttggaggcaaaacttaaagctatcagaaccatctggattgttaaaccccaaaacttccaactcatcaaagtcctgaagcctgaatagatcacacattgttacttctaattggttttgatgtttgttatgctcctcacaagtgcagataacttcacatttgcatcactaatcgacctctttcaaagcctctgctcctcctgcttcctgcctctgttagtcatcaggagacttagtgttggcgtgggtattttccccgcttgtgaaagtgcattgtcaattctagagcattaaatatatgtgaggtattgttattaatctacaaggcagtacacacagtattccagcaacagtgactttttcaaaggttttatgagacattgtaagaaagacagtatctttccatggaaaaaagtgttgcctcagagtcaaacaggaagcatgggtcctagttcctattgaactgcttagctactcttaatttagctctgaatgctagattttgtgtatactatacacattatttaatcctcaaactcctaggaattcattttgatgataaccatttacagaagagatgttgggtaacttgctcaggataccacatctagtaagtagcagagtcacaactcggacccagaactctctgattccaaaacctctacttgctgctcttgtaatggatgggcccaaatgctgctgggaaaggcaccagggaagaaagagctttctgagtcaagcagaaaacgatagcagtggagaggagtttgtttcagacgaccttcaaggttatggctcattctagatccaatcattctagcaaataagtaggttacacttgtccacaattacaaattggaaaagaagagaaggaaaggaactgagaggaatgacaaagcaaaaagctttcaatcagttcttttcagcattctgcttctgcctagttctgcactcgccccaccccctcaggcttcagccttccacttggcatccctcctcgcagagcactctcacttgcatactcctcgcttaaagaaaaaaagcattctgtactgcagtggggaaaaaaaacccttaaaatgtcttcaaaaggattcagctctagggtaatgtaaaatatgtgaaatcaggaggtgtccataataaaatatgagtgaaaacatcgttatttaaaacattgtatgtacattaatgccacattaataccaattcaggcatgaaatgttgagcatttcttggaatacggtttcttggaatatggttggggatttgagaggcgcaaaggtacccgcaagatggctttggtgtagttataaatataaatagaagtaaaataacagttgcatcttcatttataatgaatctatattgtggcctcaaagctacagagaaaagtggatgccaaaatctgtctttcattaacatatgtcttaaatgacaaggaagaggtgttgaagataaaattactttttcctatgagtatgaatcagcaacatgcagtaacaagcaagtaataatagttctgcgtgtgagtgcctacagagggtaagaaaaatagcatcagattgaaaaagcaagtgcacctgcagaagggacccaggaggctgcagagcacagctcccttgagcatcatctcaaagtgtctcagagcacagtgaccctaattctggcaagtcaggttggcatgagatggatggagcacactgaaatctccagatggtcagacagcccaccctcagctgcccctggagctcggcaaaggcaagctacaaggcactgctacttggccaagagtttaaaacaacagcttctgattctacagtgtctgtggtttcacaaccacttaaccttaagtaaggaacagctagagttagctgaatgaataaatacacgttaacgttacataaagagggaaaaaaagcaatacttgagcacatagacttcaaatagtgtccagctagctgtgtccttggcatcccagtgatctgggtctttatctttgattttctccagcaggtaggcactgagtagatccagggcccagggcgggaaatgcttctgtatcagatgttacctatcaaataaaatgagcctttgattgcctcctaaaaaaaaaaaaaaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("METTL8");
		this.infoForward = builderForward.build();
		// RefSeq NM_024770.3

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 2, 172180770,
				PositionType.ZERO_BASED), "A", "G");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(9, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("1134+1T>C", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("?", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.CODING_TRANSCRIPT_INTRON_VARIANT,
				VariantEffect.SPLICE_DONOR_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc002ugu_4() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc002ugu.4	chr2	-	172173912	172290619	172180768	172248695	11	172173912,172182350,172182551,172187068,172188313,172193962,172195693,172216931,172248552,172289535,172290392,	172180872,172182416,172182658,172187208,172188377,172194012,172196064,172217023,172248707,172289729,172290619,	E7ETE0	uc002ugu.4");
		this.builderForward
				.setSequence("cttctctgtgctgcgagtggctagctgggcagagccctgggggcgcggtgctgccgcctccaggtctccgccccgtgtgtgcgccctgcacttagggatccctccctcactgccccggtactcacaagcttctcggccccgaccttcgccctgggaggttctggccaggtgccgggaggggcgctgtgtcgagggcgatccccccaaagcagcgtcccgtgctaaaggtaccccagggtactgcctcccacatctcagtgcaggctggatgaattggccttgtctgtgtttcctgtctgagtgtgagtgtgagtgctccctgcgatggaatgtcgtcgtgtccaagggttggttcccaccttgcaccctgagctgaggggataggcttcagccaccctcgaccctgaactggaataattaggtactgcttaggatgaatatgatttggagaaattccatttcttgtctaaggctaggaaaggtgccacacagataccaaagtggttaccacccagtggcccctctgggatcaaggattttaactgacccagccaaagtttttgaacacaacatgtgggatcacatgcagtggtctaaggaagaagaagcagcagccagaaaaaaagtaaaagaaaactcagctgtgcgagtccttctggaagagcaagttaagtatgagagagaagctagtaaatactgggacacattttacaagattcataagaataagtttttcaaggatcgtaattggctgttgagggaatttcctgaaattcttccagttgatcaaaaacctgaagagaaggcgagagaatcatcatgggatcatgtaaaaactagtgctacaaatcgtttctcaagaatgcactgtcctactgtgcctgatgaaaaaaatcattatgagaaaagttctggttcttcagaaggtcaaagcaaaacagaatctgatttttccaacctagactctgaaaaacacaaaaaaggacctatggagactggattgtttcctggtagcaatgccactttcaggatactagaggttggttgtggagctggaaatagtgtgtttccaattttgaacactttggagaactctccggagtcctttctgtattgttgtgattttgcttctggagctgtggagctcgtaaagtcacactcgtcctacagagcaacccagtgttttgcctttgttcatgatgtatgtgatgatggcttaccttacccttttccagatgggatcctggatgtcattctccttgtctttgtgctctcttctattcatcctgacaggatgcaaggtgttgtaaaccgactgtccaagttactgaaacctgggggaatgctgttatttcgagactatggaagatatgataagactcagcttcgttttaaaaagggacattgtttatctgaaaatttttatgttcgaggagatggtaccagagcatatttctttacaaaaggggaagtccacagtatgttctgcaaagccagtttagatgaaaagcaaaatctggttgatcgccgcttacaagttaataggaaaaaacaagtgaaaatgcactgagtgtggattcaaggcaaattccagaaaccattgcaccagactcagaatagctccaatatggtatctacactcctttcacaagactgaactttgtaacatgttaaggtacaaagccagaggactgtgctattcaaggactactgtaagtctattgtttctcaaaagacaatgagaaaaaaagaagagaatttgtatttcctgccgttttgtcataggtgagctcctttgtgcattttaagcacatgtaagtggttcagcacagtatgcctttttctgtgctttgaaaacttgatatgctcaagcttgtttgaatttattacatctaaccattttgcttgttccttgatttttataagcattcaattaagttagtattatgtcaagtaattttgagaaaatgtaacttgacattttttgcaagtaaaaaaaattgtttatttgtttaggcttagtaaaccagttcccaaacacagtcagactcttcccattgtcatctgattgcagagagaaagcacaccttatttccagggaaagctacaacaagcccaaggtcaaagtgtattattttttgtcttgttgttgtctattttctcccaatttttttttgaaattcagaggctcatatctgaaatagaattttagttcctctttcctttcctaaaattggggaagtacagcccatgctgacattattttcaggctattcttagatatacaagttgttaggccaggtgcaatggctcgcacctgtaatcccagcactttggggggctgaggcaggcagatcggttgagctcaggagttcaagaccagcctggacaacatggcaaaaccctgtctctcccaagaatacaaaaattagccaggcatggtggcacacacctgtggtcccagctactcaggagactgaggtgggaggatcgcttgagcctcggaggcggaggttgcagtgagctgagattgtaccactgcgctccaaactgggtgacatggtgagaccttgtctccaaaaaaaaaaaaaaaaaaaaaaaaggtatgtaagctgacaagctcttgtatcaagggcttttgaaccagagcaacttcatcttgaatagggactgtgtaaaatgaagctgagacctactaggctgcaatcccaggaggttaagacattcttagtcacaggatgagataggaagtcagtacaagatacaggtcatacagaccttgctgataaaacaggttgcagtgaagaagccggccaaatgccaccaaaaccaagatggcaatgagagtgtcctctggctgtcctcactgctacactcccaccagtgccatgatagtttacaaatgccatggcaacatcaggaagttacccatatggtctacaaaggggaggcctgaataatccaccccttgtgtagcatataatcaagaaataaccttaaaaatgggcaaccagtagcccttggggctgctctgcctatggagtagccattctttatgttttactttttttttttttttttttttgagatggagtcttgctctggcacccaggctggagtgcagtggcgtgatctcagctcactgccagctccacctcccgggttcacgccattctcttgcttcagcctcctgagtagctgggactacaagcacccaccaccatgcccagctaattttttttttttttgtatttttagtagagacggggtttcaccatgttagccaggatggtctcaatctcctgaccttgtgatccgcccgcctcggcctcccaaagtgctgggattacaggtgtgagccaccgcgcccagcctattctttacttaaacttgttttcactttgctgtatggactcaattctttcttgcacaagatccaagaatcctctcttggcgtctggatccagaccctttctggtatcagtggtaagctctgtttaagacaactgtcatacccaagagaattcttaaattgtggcagttaaaaatgtatttatcccctcgagggccgggcgcagtcgctcaagcccgtaatcccagcactttgggaggccgaggaggcggatcatgaggtcaggagttcaagaccagcctgaccaacactgtgaaaccctgtctctactaaaaatacaaaaaaaattagccaggcgtggtggtgggcacctgtaatcccagctactcgggaggctgaggcaggagaatcacttgaacccgggaggcagaggttgcagtgagccgagatgacaccactgccctccagcctgggtgacagttgttagactgcgtatcaaaaaaaaaaaaaaaaatttatttactcccttgcttcaaaaaggacataagttggcttgcaaatcacattttgtagaaatgaagtttttaagattagtctcatactcattacataatatttattgactacttacatattgagtgctaaattcagtggaataatatgaacaataaagacccagccttgtttatgctcttcagggagcataaatagagtacagctggcaaaacaaaatgtgcatatcccaaacctgtaaatcatgatctaagacaatatacaaacactcaaatagggaacttaccagctagcagactttgattatgaaccacagaacctagaataaataaatactctcttgaaagaaccatcattggaataattaagttccaactgttttttctttctgaattcacttaaaatccaatttctagaagaatatcattggtccagctttagtcattggttcatctcttaacaggctggggcagggcagcttaattgatgatgccaccagaatgggcaacatgggaaaaaccctatctctaccaaaaatacaaaaattagccagtgatggtggcacgcacctgttgtcccagctactcaggaggctgagctgggaggatcttttgagcctgggaggcagaggttgcagtgagctgagatcgcaccactgcactccaacctggagtggaaggaaatttcacttcctggaatggaagaaagttcttcaaaggaaaatcaagacactgctgtcaaaaaacaccaggggagggatgctgtccagtcacagccaacacatcatatagcagtggtagcagtagtaatagtatttccagagagagagagagagattagctctgaaaaagaatggagaagaatcaggtttcttgggggaggtaggtctcaggctagttcttgaatgtacattctcagcagatggtgagaagacaagtgtggtgtgggacattaatgatgagggataggtagaaaagatgggaagggggtcagcttgtagataacctagactagcaggtgtatgacttctagtctctgaactggagccatctgagatcagtttgagatatggtgtcttataaatgtattgagagaggttgaggagttgtctatagaatggaatggagtgaggtcagagatcagtggcaaggaagaccacataaaggtttttgcacaaacttgccttgatggcaagtcctcaaaagtacaattatcaagctttctgtttaaacatgcataagtcagtaaatataaacattaaaatttgcccaagactcttaactcagttgtatttcataattaaagtgtacattaaaaagccaaactagtcatgaaagtattacatacagcaagggcagtttagataagttaatgttgctatgggaaccccagcagatctccttgtgtctccgtttaaaaatcatggcccaaggtggtttcaaaaacaacagaaggtaaaattattctcacattctcacacatgtaaattgtactgtctctctcagttctaaatagcggattcaagccagcctcttgactggcacagtaaaattcggcttcgtgtctttctataatgtattacagcatgtgagttaacctttagaagcttcaaaaatacatcaaccaaatttggactgtgagacacaaactggtctcttttgggctctgtccagcatagccttcctattccacatggctgcgcagcagggccatttccaccagtgtcttctgcttgcctggtcagtctttctgtctcttaacctacctacaacaacttccttctcggggtttgctgctagatagaaatgtagattttggcacagaaagaaaagggaaagacctatttggaattttgtctatagatgttaagaaatcatgaagcgttgcaaatgtaactgcttgggtattagtttaaaatatttgataactgcatgaaattctaatttacccctcacatccataaagctaggaccagttggttaggcagagtttgatatttaaaaattcatcttctgagagagctgaatttcaaaaggaccattttctttccaggagtaaaaggtatattctttcatcttcttacagtacagaaacaatggaatatattgtttaaatgcacccttcatttttttctccatttctaaaaattcacttttgagcatcgtccaaatgtaaaaattctcacacatgaggaactgtttccgaaggttgtgagaaagtcaagataacagtttctattattaaaagttttgcattcggtatgacacatttgcctgtattgaaacacccatttaacattgacttctgagaacttatttgctttgcctctctcgattccatatgctttgtctacaaattgtgaacacgtcccggtttctacccgtgtgtgattctaagtcgttgctgtactctagcttgtacgtgaaaattcacccatatgcagatagctgccctcttggaggcaaaacttaaagctatcagaaccatctggattgttaaaccccaaaacttccaactcatcaaagtcctgaagcctgaatagatcacacattgttacttctaattggttttgatgtttgttatgctcctcacaagtgcagataacttcacatttgcatcactaatcgacctctttcaaagcctctgctcctcctgcttcctgcctctgttagtcatcaggagacttagtgttggcgtgggtattttccccgcttgtgaaagtgcattgtcaattctagagcattaaatatatgtgaggtattgttattaatctacaaggcagtacacacagtattccagcaacagtgactttttcaaaggttttatgagacattgtaagaaagacagtatctttccatggaaaaaagtgttgcctcagagtcaaacaggaagcatgggtcctagttcctattgaactgcttagctactcttaatttagctctgaatgctagattttgtgtatactatacacattatttaatcctcaaactcctaggaattcattttgatgataaccatttacagaagagatgttgggtaacttgctcaggataccacatctagtaagtagcagagtcacaactcggacccagaactctctgattccaaaacctctacttgctgctcttgtaatggatgggcccaaatgctgctgggaaaggcaccagggaagaaagagctttctgagtcaagcagaaaacgatagcagtggagaggagtttgtttcagacgaccttcaaggttatggctcattctagatccaatcattctagcaaataagtaggttacacttgtccacaattacaaattggaaaagaagagaaggaaaggaactgagaggaatgacaaagcaaaaagctttcaatcagttcttttcagcattctgcttctgcctagttctgcactcgccccaccccctcaggcttcagccttccacttggcatccctcctcgcagagcactctcacttgcatactcctcgcttaaagaaaaaaagcattctgtactgcagtggggaaaaaaaacccttaaaatgtcttcaaaaggattcagctctagggtaatgtaaaatatgtgaaatcaggaggtgtccataataaaatatgagtgaaaacatcgttatttaaaacattgtatgtacattaatgccacattaataccaattcaggcatgaaatgttgagcatttcttggaatacggtttcttggaatatggttggggatttgagaggcgcaaaggtacccgcaagatggctttggtgtagttataaatataaatagaagtaaaataacagttgcatcttcatttataatgaatctatattgtggcctcaaagctacagagaaaagtggatgccaaaatctgtctttcattaacatatgtcttaaatgacaaggaagaggtgttgaagataaaattactttttcctatgagtatgaatcagcaacatgcagtaacaagcaagtaataatagttctgcgtgtgagtgcctacagagggtaagaaaaatagcatcagattgaaaaagcaagtgcacctgcagaagggacccaggaggctgcagagcacagctcccttgagcatcatctcaaagtgtctcagagcacagtgaccctaattctggcaagtcaggttggcatgagatggatggagcacactgaaatctccagatggtcagacagcccaccctcagctgcccctggagctcggcaaaggcaagctacaaggcactgctacttggccaagagtttaaaacaacagcttctgattctacagtgtctgtggtttcacaaccacttaaccttaagtaaggaacagctagagttagctgaatgaataaatacacgttaacgttacataaagagggaaaaaaagcaatacttgagcacatagacttcaaatagtgtccagctagctgtgtccttggcatcccagtgatctgggtctttatctttgattttctccagcaggtaggcactgagtagatccagggcccagggcgggaaatgcttctgtatcagatgttacctatcaaataaaatgagcctttgattgcctcctaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("METTL8");
		this.infoForward = builderForward.build();
		// RefSeq NM_024770.3

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 2, 172180770,
				PositionType.ZERO_BASED), "A", "G");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(10, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("1135T>C", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(*379Argext*29)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.STOP_LOST), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc003teh_1() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc003teh.1	chr7	+	34697896	34889590	34698024	34889224	10	34697896,34724163,34818073,34851381,34867012,34873995,34884507,34888094,34888934,34889176,	34698171,34724296,34818177,34851475,34867214,34874072,34884594,34888275,34889034,34889590,	Q6W5P4-3	uc003teh.1");
		this.builderForward
				.setSequence("gggctcagggagggctctgtgcctccgttcagcagagctgcagctgctgcccagctctcaggaggcaagctggactccctcactcagctgcaggagcaaggacagtgaggctcaaccccgcctgagccatgccagccaacttcacagagggcagcttcgattccagtgggaccgggcagacgctggattcttccccagtggcttgcactgaaacagtgacttttactgaagtggtggaaggaaaggaatggggttccttctactactcctttaagactgagcaattgataactctgtgggtcctctttgtttttaccattgttggaaactccgttgtgcttttttccacatggaggagaaagaagaagtcaagaatgaccttctttgtgactcagctggccatcacagattctttcacaggactggtcaacatcttgacagatattaattggcgattcactggagacttcacggcacctgacctggtttgccgagtggtccgctatttgcaggttgtgctgctctacgcctctacctacgtcctggtgtccctcagcatagacagataccatgccatcgtctaccccatgaagttccttcaaggagaaaagcaagccagggtcctcattgtgatcgcctggagcctgtcttttctgttctccattcccaccctgatcatatttgggaagaggacactgtccaacggtgaagtgcagtgctgggccctgtggcctgacgactcctactggaccccatacatgaccatcgtggccttcctggtgtacttcatccctctgacaatcatcagcatcatgtatggcattgtgatccgaactatttggattaaaagcaaaacctacgaaacagtgatttccaactgctcagatgggaaactgtgcagcagctataaccgaggactcatctcaaaggcaaaaatcaaggctatcaagtatagcatcatcatcattcttgccttcatctgctgttggagtccatacttcctgtttgacattttggacaatttcaacctccttccagacacccaggagcgtttctatgcctctgtgatcattcagaacctgccagcattgaatagtgccatcaaccccctcatctactgtgtcttcagcagctccatctctttcccctgcagggctaatagcagtgtctacctgttggcctgtgatgtctctgtcctctgggctctggtgctgaccagtgagaaggagagctgtgagtcatggagaaggaagggagcaaagatcacaggattccagaatgacgttccgggagagaactgagaggcatgagatgcagattctgtccaagccagaattcatctagaccctagggcagtgccagtgctaggctgagcaccatcagctctcccaggtccttgtcacctgcttgggcacgtgcatggaacccgagccaacttcaccccaccctcgtcattacctgggagatgcacaagacaaatgttctaatgactgcatgcactgcttaagtattggccaacacgaactccccagttattcatgccagccaggaaggaaacgccttccttccccaccattcccagccctccttcccactggccagcacctgaacccagtgaacacaggcattagtggtccagggtcctggcttggagccagtgagtagac"
						.toUpperCase());
		this.builderForward.setGeneSymbol("GENE_NPSR1");
		this.infoForward = builderForward.build();
		// RefSeq NM_207173.1

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 7, 34889221,
				PositionType.ZERO_BASED), "T", "C");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(9, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("1171T>C", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(*391Argext*3)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.STOP_LOST), annotation1.getEffects());
	}

	//
	// Various Missense Variants
	//

	@Test
	public void testRealWorldCase_uc011mzv_2_missense() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc011mzv.2	chrX	-	154006958	154033802	154007451	154028031	13	154006958,154009513,154009874,154011701,154012302,154013325,154014478,154018228,154019257,154020043,154020416,154028019,154033546,	154007628,154009588,154010077,154011782,154012383,154013432,154014675,154018297,154019343,154020122,154020560,154028168,154033802,	Q00013-2	uc011mzv.2");
		this.builderForward
				.setSequence("agctccgtccgcgccctcccggcgcaccgcctgcggggcggtgactggcccagccgcaccgcgtctcccgccttctccgcagccccgcaggccccgggccctgtcattcccagcgctgccctgtcttgcgttccagtgttccagcttctgcgagatgaccctcaaggcgagcgagggcgagagtgggggcagcatgcacacggcgctctccgacctctacctggagcatttgctgcagaagcgtagtcggccagagcttcctttgagcaactgcctctgccctttgaaccctgctgcagatgtttcctctgaaggggctttcagagcggcaggcattgccactccagcactttgtctgattaaaggccagaggctgcagaacaaaggccagacatggagtcctgggctgtatcgcatccattgaatactgtgaccgaggacatgtacaccaacgggtctcctgccccaggtagccctgcccaggtcaagggacaggaggtgcggaaagtgcgactcatacagtttgagaaggtcacagaagagcccatgggaatcacgctgaagctgaatgaaaaacagtcctgtacggtggccagaattcttcatggtggcatgatccatagacaaggctcccttcacgtgggggatgagatcctagaaatcaatggcacaaatgtgacaaatcattcagtggatcagctgcagaaggcgatgaaagaaaccaaaggaatgatctcattaaaagtaattcccaaccagcaaagccgtcttcctgcactacagatgttcatgagagcgcagtttgactatgatcccaaaaaggacaatctgatcccttgcaaggaggcgggactgaagtttgctactggggacattatccagattatcaacaaggatgacagcaattggtggcagggacgggtggaaggctcctccaaggagtcagcaggattgatcccttcccctgagctgcaggaatggcgagtggcaagtatggctcagtcagctcctagcgaagccccgagctgcagtccctttgggaagaagaagaagtacaaagacaaatatctggccaagcacagctcgatttttgatcagttggatgttgtttcctacgaggaagtcgttcggctccctgcattcaagaggaagaccctggtgctgatcggagccagtggggtgggtcgcagccacattaagaatgccctgctcagccagaatccggagaagtttgtgtaccctgtcccatatacaacacggccgccaaggaagagtgaggaagatgggaaggagtaccactttatctcaacggaggagatgacgaggaacatctctgccaatgagttcttggagtttggcagctaccaaggcaacatgtttggcaccaaatttgaaacagtgcaccagatccataagcagaacaagattgccatccttgacattgagccccagaccctgaaaattgttcggacagcagaactttcgcctttcattgtgttcattgcacctactgaccagggcactcagacagaagccctgcagcagctgcagaaggactctgaggccatccgcagccagtacgctcactactttgacctctcactggtcaataatggtgttgatgaaacccttaagaaattacaagaagccttcgaccaagcgtgcagttctccacagtgggtgcctgtctcctgggtttactaagcttgtagaatgggggaacccactgtatgcccctctccagcatttggaattccacccgccttgctttaagacaaacagggctgctccaactagttttgtgtcagcttccagctctctgcagctatcctaattcagccagtaaggttcagtcttcttgctcaggctcctgaagggttgattctcctgatagatggggccccactgatctggatttgaaaaggatttctagaaattgggggtaagaagtactaccaaaatgtaactgctaatcaagggtgatgcacagcaaaagcaatggaccccatccctctaaagcctgccctcctttgccttcaactgtatatgctgggtatttcatttgtctttttattttggagaaagcgtttttaactgcaactttctataatgccaaaatgacacatctgtgcaatagaatgatgtctgctctagggaaaccttcaaaagcaataaaaatgctgtgttgaaatgccaaaaaaaaaaaaaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("MPP1");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, refDict.getContigNameToID()
				.get("X"), 154009587, PositionType.ZERO_BASED), "T", "A");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(11, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("1060A>T", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Thr354Ser)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.MISSENSE_VARIANT, VariantEffect.SPLICE_REGION_VARIANT),
				annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc010nvg_2() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc010nvg.2	chrX	-	154006958	154033802	154007451	154033648	12	154006958,154009513,154009874,154011701,154012302,154013325,154014478,154018228,154019257,154020043,154020416,154033546,	154007628,154009588,154010077,154011782,154012383,154013432,154014615,154018297,154019343,154020122,154020560,154033802,	G3XAI1	uc010nvg.2");
		this.builderForward
				.setSequence("agctccgtccgcgccctcccggcgcaccgcctgcggggcggtgactggcccagccgcaccgcgtctcccgccttctccgcagccccgcaggccccgggccctgtcattcccagcgctgccctgtcttgcgttccagtgttccagcttctgcgagatgaccctcaaggcgagcgagggcgagagtgggggcagcatgcacacggcgctctccgacctctacctggagcatttgctgcagaagcgtagtcggccagaggctgtatcgcatccattgaatactgtgaccgaggacatgtacaccaacgggtctcctgccccaggtagccctgcccaggtcaagggacaggaggtgcggaaagtgcgactcatacagtttgagaaggtcacagaagagcccatgggaatcacgctgaagctgaatgaaaaacagtcctgtacggtggccagaattcttcatggtggcatgatccatagacaaggctcccttcacgtgggggatgagatcctagaaatcaatggcacaaatgtgacaaatcattcagtggatcagctgcagaaggcgatgaaagaaaccaaaggaatgatctcattaaaagtaattcccaaccagcaaagccgtcttcctgcactacaggaggcgggactgaagtttgctactggggacattatccagattatcaacaaggatgacagcaattggtggcagggacgggtggaaggctcctccaaggagtcagcaggattgatcccttcccctgagctgcaggaatggcgagtggcaagtatggctcagtcagctcctagcgaagccccgagctgcagtccctttgggaagaagaagaagtacaaagacaaatatctggccaagcacagctcgatttttgatcagttggatgttgtttcctacgaggaagtcgttcggctccctgcattcaagaggaagaccctggtgctgatcggagccagtggggtgggtcgcagccacattaagaatgccctgctcagccagaatccggagaagtttgtgtaccctgtcccatatacaacacggccgccaaggaagagtgaggaagatgggaaggagtaccactttatctcaacggaggagatgacgaggaacatctctgccaatgagttcttggagtttggcagctaccaaggcaacatgtttggcaccaaatttgaaacagtgcaccagatccataagcagaacaagattgccatccttgacattgagccccagaccctgaaaattgttcggacagcagaactttcgcctttcattgtgttcattgcacctactgaccagggcactcagacagaagccctgcagcagctgcagaaggactctgaggccatccgcagccagtacgctcactactttgacctctcactggtcaataatggtgttgatgaaacccttaagaaattacaagaagccttcgaccaagcgtgcagttctccacagtgggtgcctgtctcctgggtttactaagcttgtagaatgggggaacccactgtatgcccctctccagcatttggaattccacccgccttgctttaagacaaacagggctgctccaactagttttgtgtcagcttccagctctctgcagctatcctaattcagccagtaaggttcagtcttcttgctcaggctcctgaagggttgattctcctgatagatggggccccactgatctggatttgaaaaggatttctagaaattgggggtaagaagtactaccaaaatgtaactgctaatcaagggtgatgcacagcaaaagcaatggaccccatccctctaaagcctgccctcctttgccttcaactgtatatgctgggtatttcatttgtctttttattttggagaaagcgtttttaactgcaactttctataatgccaaaatgacacatctgtgcaatagaatgatgtctgctctagggaaaccttcaaaagcaataaaaatgctgtgttgaaatgccaaaaaaaaaaaaaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("MPP1");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, refDict.getContigNameToID()
				.get("X"), 154009587, PositionType.ZERO_BASED), "T", "A");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(10, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("1090A>T", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Thr364Ser)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.MISSENSE_VARIANT, VariantEffect.SPLICE_REGION_VARIANT),
				annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc011mzw_2() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc011mzw.2	chrX	-	154006958	154033802	154007451	154033648	12	154006958,154009513,154009874,154011701,154012302,154013325,154014478,154018228,154019257,154020043,154020467,154033546,	154007628,154009588,154010077,154011782,154012383,154013432,154014675,154018297,154019343,154020122,154020560,154033802,	NP_001159932	uc011mzw.2");
		this.builderForward
				.setSequence("agctccgtccgcgccctcccggcgcaccgcctgcggggcggtgactggcccagccgcaccgcgtctcccgccttctccgcagccccgcaggccccgggccctgtcattcccagcgctgccctgtcttgcgttccagtgttccagcttctgcgagatgaccctcaaggcgagcgagggcgagagtgggggcagcatgcacacggcgctctccgacctctacctggagcatttgctgcagaagcgtagtcggccagaggctgtatcgcatccattgaatactgtgaccgaggacatgtacaccaacgggtctcctgccccaggtagccctgcccaggtcaagggacaggagggaatcacgctgaagctgaatgaaaaacagtcctgtacggtggccagaattcttcatggtggcatgatccatagacaaggctcccttcacgtgggggatgagatcctagaaatcaatggcacaaatgtgacaaatcattcagtggatcagctgcagaaggcgatgaaagaaaccaaaggaatgatctcattaaaagtaattcccaaccagcaaagccgtcttcctgcactacagatgttcatgagagcgcagtttgactatgatcccaaaaaggacaatctgatcccttgcaaggaggcgggactgaagtttgctactggggacattatccagattatcaacaaggatgacagcaattggtggcagggacgggtggaaggctcctccaaggagtcagcaggattgatcccttcccctgagctgcaggaatggcgagtggcaagtatggctcagtcagctcctagcgaagccccgagctgcagtccctttgggaagaagaagaagtacaaagacaaatatctggccaagcacagctcgatttttgatcagttggatgttgtttcctacgaggaagtcgttcggctccctgcattcaagaggaagaccctggtgctgatcggagccagtggggtgggtcgcagccacattaagaatgccctgctcagccagaatccggagaagtttgtgtaccctgtcccatatacaacacggccgccaaggaagagtgaggaagatgggaaggagtaccactttatctcaacggaggagatgacgaggaacatctctgccaatgagttcttggagtttggcagctaccaaggcaacatgtttggcaccaaatttgaaacagtgcaccagatccataagcagaacaagattgccatccttgacattgagccccagaccctgaaaattgttcggacagcagaactttcgcctttcattgtgttcattgcacctactgaccagggcactcagacagaagccctgcagcagctgcagaaggactctgaggccatccgcagccagtacgctcactactttgacctctcactggtcaataatggtgttgatgaaacccttaagaaattacaagaagccttcgaccaagcgtgcagttctccacagtgggtgcctgtctcctgggtttactaagcttgtagaatgggggaacccactgtatgcccctctccagcatttggaattccacccgccttgctttaagacaaacagggctgctccaactagttttgtgtcagcttccagctctctgcagctatcctaattcagccagtaaggttcagtcttcttgctcaggctcctgaagggttgattctcctgatagatggggccccactgatctggatttgaaaaggatttctagaaattgggggtaagaagtactaccaaaatgtaactgctaatcaagggtgatgcacagcaaaagcaatggaccccatccctctaaagcctgccctcctttgccttcaactgtatatgctgggtatttcatttgtctttttattttggagaaagcgtttttaactgcaactttctataatgccaaaatgacacatctgtgcaatagaatgatgtctgctctagggaaaccttcaaaagcaataaaaatgctgtgttgaaatgccaaaaaaaaaaaaaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("MPP1");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, refDict.getContigNameToID()
				.get("X"), 154009587, PositionType.ZERO_BASED), "T", "A");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(10, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("1099A>T", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Thr367Ser)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.MISSENSE_VARIANT, VariantEffect.SPLICE_REGION_VARIANT),
				annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc004fmp_2() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc004fmp.2	chrX	-	154006958	154033802	154007451	154033648	12	154006958,154009513,154009874,154011701,154012302,154013325,154014478,154018228,154019257,154020043,154020416,154033546,	154007628,154009588,154010077,154011782,154012383,154013432,154014675,154018297,154019343,154020122,154020560,154033802,	Q00013	uc004fmp.2");
		this.builderForward
				.setSequence("agctccgtccgcgccctcccggcgcaccgcctgcggggcggtgactggcccagccgcaccgcgtctcccgccttctccgcagccccgcaggccccgggccctgtcattcccagcgctgccctgtcttgcgttccagtgttccagcttctgcgagatgaccctcaaggcgagcgagggcgagagtgggggcagcatgcacacggcgctctccgacctctacctggagcatttgctgcagaagcgtagtcggccagaggctgtatcgcatccattgaatactgtgaccgaggacatgtacaccaacgggtctcctgccccaggtagccctgcccaggtcaagggacaggaggtgcggaaagtgcgactcatacagtttgagaaggtcacagaagagcccatgggaatcacgctgaagctgaatgaaaaacagtcctgtacggtggccagaattcttcatggtggcatgatccatagacaaggctcccttcacgtgggggatgagatcctagaaatcaatggcacaaatgtgacaaatcattcagtggatcagctgcagaaggcgatgaaagaaaccaaaggaatgatctcattaaaagtaattcccaaccagcaaagccgtcttcctgcactacagatgttcatgagagcgcagtttgactatgatcccaaaaaggacaatctgatcccttgcaaggaggcgggactgaagtttgctactggggacattatccagattatcaacaaggatgacagcaattggtggcagggacgggtggaaggctcctccaaggagtcagcaggattgatcccttcccctgagctgcaggaatggcgagtggcaagtatggctcagtcagctcctagcgaagccccgagctgcagtccctttgggaagaagaagaagtacaaagacaaatatctggccaagcacagctcgatttttgatcagttggatgttgtttcctacgaggaagtcgttcggctccctgcattcaagaggaagaccctggtgctgatcggagccagtggggtgggtcgcagccacattaagaatgccctgctcagccagaatccggagaagtttgtgtaccctgtcccatatacaacacggccgccaaggaagagtgaggaagatgggaaggagtaccactttatctcaacggaggagatgacgaggaacatctctgccaatgagttcttggagtttggcagctaccaaggcaacatgtttggcaccaaatttgaaacagtgcaccagatccataagcagaacaagattgccatccttgacattgagccccagaccctgaaaattgttcggacagcagaactttcgcctttcattgtgttcattgcacctactgaccagggcactcagacagaagccctgcagcagctgcagaaggactctgaggccatccgcagccagtacgctcactactttgacctctcactggtcaataatggtgttgatgaaacccttaagaaattacaagaagccttcgaccaagcgtgcagttctccacagtgggtgcctgtctcctgggtttactaagcttgtagaatgggggaacccactgtatgcccctctccagcatttggaattccacccgccttgctttaagacaaacagggctgctccaactagttttgtgtcagcttccagctctctgcagctatcctaattcagccagtaaggttcagtcttcttgctcaggctcctgaagggttgattctcctgatagatggggccccactgatctggatttgaaaaggatttctagaaattgggggtaagaagtactaccaaaatgtaactgctaatcaagggtgatgcacagcaaaagcaatggaccccatccctctaaagcctgccctcctttgccttcaactgtatatgctgggtatttcatttgtctttttattttggagaaagcgtttttaactgcaactttctataatgccaaaatgacacatctgtgcaatagaatgatgtctgctctagggaaaccttcaaaagcaataaaaatgctgtgttgaaatgccaaaaaaaaaaaaaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("MPP1");
		this.infoForward = builderForward.build();
		// RefSeq NM_002436.3

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, refDict.getContigNameToID()
				.get("X"), 154009587, PositionType.ZERO_BASED), "T", "A");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(10, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("1150A>T", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Thr384Ser)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.MISSENSE_VARIANT, VariantEffect.SPLICE_REGION_VARIANT),
				annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc010fks_4() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc010fks.4	chr2	+	113993103	114024600	114016863	114021000	5	113993103,114012966,114016840,114020854,114023052,	113993147,114013126,114017161,114021031,114024600,	Q7Z3M5	uc010fks.4");
		this.builderForward
				.setSequence("gctagaactggacacctcgggggtttcctgctttatggcgaaggacacatatactcacagaaatgagttatttaaagaagacggtccagattccacagtccaaaaaaaaaagaggtttgccctgcttctgaaggagtcagggtatgaatgcagaggtcttcctgttggctcacatatcagatcttcaacgtcaacattaaatgcgagtgatgcttccccctctgtgaatgtggctttgggtatggctaatccacaccctgcactctggccttcagaagcccagggagagaagcctcccagaagcaaccttccagaaccttctccatcctcccaccgacctccccagcccctgtcctctctttgaatctcgttgtcaagttcttcccgcagacacctggctccttgaggggaggtgcagctttcatctgaccatgcaggcgtgctttgcagtgggtagagcagtgctttcctcctcccagctgcacactggaatcacctggagagtccagaaacttcccgcctcagtgaaagaacatcagtgtatcagcacagcaaatattccaaatgccaggctggattccctccaattaccaggacctccaggcttctcctcctttcaagaactttctgaccctggatccagtctaaatgttggttataaactcacctgacatccacagggcccttccaaaatactgtcaaggctgactgctggtgcctggaacctgcacaccctcctgggactctgcatctctgctggggctggtttctcctctgagccaccttaaggaatcccaccctgacccggtcagagctcaccatgcctccctctcctacttgctcagcattttgtgtctatctagactgaagcctttgctttctctctgtgattatggtgaaagttcagtttgctttcctatctctagaatgtcagcacgaaggcagaggcatggtctcctgagttccataaccccaaagcctaactccctgcctgccacactgaggggcttagtagacattgacttactaaattgaatagttgagtaataatgctctttgaaataatttggagttagatcttgagccttgtgtggccagaggatgggtgaggacacatcataaaaactaaaggtaactgagcatgccagtggttggggtgagaaatttgcatggagcactccagacacagaggcagcaaaacccaagtgctgggaagtcagagtgagagagtgaaggggcagtgagtgcttctttaggagctggatcaaaacctaagatgatctggccacatggtgcagactgagcacacacttgggagagaaatgggccaggaggagaggagagaggttcaccacaggacagtggatgactgggaatagaagccctccccatggtggaccaagaagtctgtgaagctgagagaaagacaaaagaaatcacgacttctgtaggtagctgggatggaaacctcaggggtgacagggtctttaaaggaaatctcaaggaaggcatcatgttctgggtgtgctccagggatgctgagtaagaaaagaccagcctttctgtgtgtgtctttactctgccacaaggcacctgggctcctaaatgaccaatacacatgtgctatactggtgccaagaaagaagggtgttttgtaggaaagtcaaggtgagaggaggtgcccaggacttggctcctttctttgaagggtgacggtgtaaattctccatgctggggatcctactgatggatggaggcatgtcgatctcatcatttcacagagtggggagctatggccacaacgtgcctatcacacaagtccagcctcacgtcagctggacaccagctgttctgacacttgtcagcacatggggcacccggaagttcatggtgttctgcaaagaacgccacaaaacagtgagctcaacttcttgaaagtcataatgtgttgctaagtccgatcccagatcctaaaaataatatgaggtagtggccattgaacagaaaaaatttacatcttaaacttgaagataagtacaagtgtacctttttctaccactgttgttcagttttattatattgacaagcacctctgcccaccctgtggggtaaaactgtttgaaagtgtttatgaaagttttatataaatattggttgatcaaatgtacaaaatatgcatgatggcaccatattgtgtataatagtgacaaatacagtctatgtgggtggagaaacaagctcggtgttgtg"
						.toUpperCase());
		this.builderForward.setGeneSymbol("LOC654433");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 2, 114017028,
				PositionType.ZERO_BASED), "A", "G");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(2, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("166A>G", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Thr56Ala)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.MISSENSE_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc002tjq_5() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc002tjq.5	chr2	+	113993103	114024600	114016863	114021000	6	113993103,113994089,114012966,114016840,114020854,114023052,	113993147,113994190,114013126,114017161,114021031,114024600,	Q7Z3M5	uc002tjq.5");
		this.builderForward
				.setSequence("gctagaactggacacctcgggggtttcctgctttatggcgaagggccagggccccacaccttccgcctgacagccagccaagctcttcagtcccccgccctccacctgccagggaggctccgggcgttgtacctgccaccacgggacacatatactcacagaaatgagttatttaaagaagacggtccagattccacagtccaaaaaaaaaagaggtttgccctgcttctgaaggagtcagggtatgaatgcagaggtcttcctgttggctcacatatcagatcttcaacgtcaacattaaatgcgagtgatgcttccccctctgtgaatgtggctttgggtatggctaatccacaccctgcactctggccttcagaagcccagggagagaagcctcccagaagcaaccttccagaaccttctccatcctcccaccgacctccccagcccctgtcctctctttgaatctcgttgtcaagttcttcccgcagacacctggctccttgaggggaggtgcagctttcatctgaccatgcaggcgtgctttgcagtgggtagagcagtgctttcctcctcccagctgcacactggaatcacctggagagtccagaaacttcccgcctcagtgaaagaacatcagtgtatcagcacagcaaatattccaaatgccaggctggattccctccaattaccaggacctccaggcttctcctcctttcaagaactttctgaccctggatccagtctaaatgttggttataaactcacctgacatccacagggcccttccaaaatactgtcaaggctgactgctggtgcctggaacctgcacaccctcctgggactctgcatctctgctggggctggtttctcctctgagccaccttaaggaatcccaccctgacccggtcagagctcaccatgcctccctctcctacttgctcagcattttgtgtctatctagactgaagcctttgctttctctctgtgattatggtgaaagttcagtttgctttcctatctctagaatgtcagcacgaaggcagaggcatggtctcctgagttccataaccccaaagcctaactccctgcctgccacactgaggggcttagtagacattgacttactaaattgaatagttgagtaataatgctctttgaaataatttggagttagatcttgagccttgtgtggccagaggatgggtgaggacacatcataaaaactaaaggtaactgagcatgccagtggttggggtgagaaatttgcatggagcactccagacacagaggcagcaaaacccaagtgctgggaagtcagagtgagagagtgaaggggcagtgagtgcttctttaggagctggatcaaaacctaagatgatctggccacatggtgcagactgagcacacacttgggagagaaatgggccaggaggagaggagagaggttcaccacaggacagtggatgactgggaatagaagccctccccatggtggaccaagaagtctgtgaagctgagagaaagacaaaagaaatcacgacttctgtaggtagctgggatggaaacctcaggggtgacagggtctttaaaggaaatctcaaggaaggcatcatgttctgggtgtgctccagggatgctgagtaagaaaagaccagcctttctgtgtgtgtctttactctgccacaaggcacctgggctcctaaatgaccaatacacatgtgctatactggtgccaagaaagaagggtgttttgtaggaaagtcaaggtgagaggaggtgcccaggacttggctcctttctttgaagggtgacggtgtaaattctccatgctggggatcctactgatggatggaggcatgtcgatctcatcatttcacagagtggggagctatggccacaacgtgcctatcacacaagtccagcctcacgtcagctggacaccagctgttctgacacttgtcagcacatggggcacccggaagttcatggtgttctgcaaagaacgccacaaaacagtgagctcaacttcttgaaagtcataatgtgttgctaagtccgatcccagatcctaaaaataatatgaggtagtggccattgaacagaaaaaatttacatcttaaacttgaagataagtacaagtgtacctttttctaccactgttgttcagttttattatattgacaagcacctctgcccaccctgtggggtaaaactgtttgaaagtgtttatgaaagttttatataaatattggttgatcaaatgtacaaaatatgcatgatggcaccatattgtgtataatagtgacaaatacagtctatgtgggtggagaaacaagctcggtgttgtg"
						.toUpperCase());
		this.builderForward.setGeneSymbol("LOC654433");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 2, 114017028,
				PositionType.ZERO_BASED), "A", "G");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(3, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("166A>G", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Thr56Ala)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.MISSENSE_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc003vmj_2() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory.parseKnownGenesLine(refDict,
				"uc003vmj.2	chr7	+	127637561	127640130	127637746	127638079	1	127637561,	127640130,	Q9HBX3	uc003vmj.2");
		this.builderForward
				.setSequence("gtttgatttttgtgggccttacttttcccatttgctcatgtatgtctatattcctatatctaatgtttcttaaaagacaaatctgactgtgtaggtgcatttgtttaaaaaatataactccccattgcctctgggataaagtcttatctcagtttacatatattcttgataacctctccaactttatgtctcaccacccccactcacttcgtaattcgtgccttatcagaatggacctgttatactggcagttcaccatatataccattacattttgtttctcccatctctcaggtagacttacactttcggcccagcacatcagtcatcgcccttgcttgctttcctattcactcctgttctggaaggtgcaccaccttttcttggaaggcttcccttgctctcccaggctagatgagatgtccttccatcagttcccacagcaccctgtgcatgtatctgttgtgcacttaccaatagtatacaagggatctatgacccaagtctctccccactagcttgtaagctcctcacagacaggaaccatgttttgtctttgtactccagtgcctagtatataagagatactcaataaataaatatttgtcaaatcaactaattgattccttgtgacctaattctagagaatgggaagaaggcctgttattttgttgtcctttatggttctttaggaaagctctccaagcttggcatttgtcagggtgggaggaaaactgttgaagtattaagactggacacatggctgctaattcatcagcttatcattgaaaaagtccatagccaaaacctgactgtgcacttactataggaccctgacctgcctgggtctccctgtctctccagccagtatatttaaaggtaatgagataatgatgagtgttttgaaaaatgtttagtgttcaaatagaaaccattcgtgtcccttcactctctgaacatagtctgcaaatctctccctctctggtatgtttctgttttgtggattagactgctaaataagcgaagagttaattttgaagttcaggtcaacaaattcctgtttggagacgggctgttcctgaataccaggttgttatctgtctattacagccccttcacttctgggctcctggccctttgcgaagtacttcagaaagcctagagagaagagagggccctcaaatcccattccagcccctaaatgaaagtggcatcacaaggagaaaagtggaggtagaaatgtaccatgggggacgttgccccattgcctttcttcccctgactactgggcttcctctatcagcttttaacgcctggaagtagctgagtctccagtcactggtggccttccccaagtgagctcagcagcatgtgctaataggtgccaatactgtgctggctccagtgggattcctctctgcaaagctgctggcttagcttgactgtttccctattcctctccttagaactttcatagccagtcaagcatagaattgcatccattcttctgaaataggtactgtgcctaatttttcattggcttggccaagattctgttacctgtaattatgtcatatgggctgactggtgtaggtttggtaggcctcactgctctccagaggctagtgttcctatcccaggatggctgttgggattaggagtggaatgaggaatagtttaggaccacaagagatttggcctccttggaacatgtaggaaggcctcaaggatgtaattctagcccagggaagaaaatccttttcagcagacgcttcccttcccttcccctagggaatgtgtccctactatagtaaactcttccttaagagagttttatgttttctctgtcagatcaatttgtttttcttaatgttgttcctatcaaaaaaaaaaaaaaggaagaaatacagtcctcagacctgacagtttcatttttccccccaagtagaagcaatagaaaccttagcagttccaagactcctggcactcacttttcctctccatctttaggtctaacccagctcagcccagagtaaagacataaaagcctcacatgtgatttcctcttgcattttttctgctagaagggtccacagtacttactttatgcctcatctcaattgaagctggatgatgaggatagtgggcagtgattgcaattggggaatgcatgtactggatcacataaggtcatgaaagtccttgaaacagagccttgccaagcagaacccttgccactcatggtattcgatacagtctacccatctgacatgaaatgatattcttaatgagccattcctatggaagatagctattgccaggcacattcagtctcctggtctccctctcatagctatgtagggcctatgacctagaataaatggaaaaatgttatcaggaggaagagactgctggactcccctgagaggtagggtttgtgctaggagagctgggtgccatttatttcattttgatatatcaggtgctcctagtaacaattcctagtttctcttacctctgtaatcatcctgaatagtcactgcttcttgagctttggtgagtgttctct"
						.toUpperCase());
		this.builderForward.setGeneSymbol("SND1-IT1");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 7, 127637815,
				PositionType.ZERO_BASED), "A", "G");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(0, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("70A>G", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Thr24Ala)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.MISSENSE_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc001amg_3() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001amg.3	chr1	+	6266188	6281359	6266595	6279467	18	6266188,6266595,6267438,6268940,6269174,6269327,6269473,6269983,6270282,6270429,6270929,6271080,6271926,6272290,6272741,6273125,6278348,6279295,	6266362,6266786,6267571,6269085,6269256,6269403,6269599,6270030,6270355,6270498,6270998,6271178,6272113,6272476,6272793,6273243,6278429,6281359,	Q6ZRF8	uc001amg.3");
		this.builderForward
				.setSequence("cccgcgcagagtgggaaccatcgcccggtgcgggcctgaacttccagggccggctactcctcggcagagcgaccgcgcggtgtctcagagcgcggcccggagccgcactaagagcgctggacggcgggagagaggctcggaggaccggtagctcccagcaaagcggcccagcggatgtcgggagctatcttcgggcccctggagggcccgagctccctggatgccccgagcatccacccgctggtgtgcccgctgtgccacgtgcagtacgagcgcccgtgtcttctggactgtttccacgacttctgtgccggctgcctgcgtggccgcgcgaccgacggccgcctcacctgcccgctgtgccaacaccagacggtgctgaagggtcccagcgggctcccgccggtggaccggctgctgcagttcctggtggacagctcaggggatggcgtggaggcggtgcgctgtgccaactgtgacctggagtgcagcgagcaggacgtggagaccacgtacttctgcaacacgtgcggacagcccctatgcgcgcgctgccgcgacgagacgcaccgagcacgcatgttcgcgcgccacgacatcgtggccctgggtcagcgaagccgcgacgtgccccagaagtgcacgctgcacgcagagccctacctcttgttctccaccgacaagaagttgctgttgtgcatccgctgcttccgcgacatgcagaaggagagccgggcacactgcgtggacctggaatcggcttacgtgcagggctgcgagcggctggagcaggcggtgctggccgtgaaggccctgcagacggccacgcgggaggccatcgcgctgctgcaggccatggtggaggaggtgcggcacagcgccgccgaggaggaggacgctatccacgccctcttcggcagcatgcaggacaggctggcagagaggaaagcgctgctgctgcaggctgtgcagagccaatacgaagagaaggacaaggccttcaaggagcagctctctcacttggccaccttgctgcccaccctgcaggtccacctggtcatctgctcctccttcctcagcttggccaacaaggctgagttcctggacctgggctatgagctgatggagaggctgcagggcatcgtcacgcggccgcaccacctaaggcctattcagagcagcaagattgccagtgaccaccgagctgaattcgcgcgctgtctggagccactgctgctgctggggccacgtcgggtggcagctgctgcaagtggtgctaacacgctggcagggggcttaggccccaaggcgctgacggggccccactgcccctccccagtaggaaagatgtcggggtcacccgtccaaaagcccacgctgcaccggtccatcagcaccaaggtgctgctggcggagggcgagaacacgcccttcgcagagcactgccgccactatgaggactcctaccggcacctgcaggcagagatgcagagcctaaaggaccaggtacaggagctgcaccgagacctcaccaagcaccactcgctcatcaaggcggagatcatgggagacgtcctgcacaagtccctgcaactggacgtgcagatcgcctcggagcacgcctccttagagggcatgagggtcgtcttccaggagatttgggaggaagcctatcagcgagtggctaatgagcaggagatttatgaagcccagctccatgaccttctccagctgaggcaggagaatgcctacctgaccaccatcaccaagcagatcacgccctacgtccgctccattgccaaggtgaaggagcggctggagcccaggtttcaggcacccgtggatgagcagtcagagagtctacagaacacgcacgacgacagcaggaacaacgcggcctcagccaggaataatccaggaagtgtcccggaaaagagagagaagacatcagagcctaaaggaaacagctgggctccgaacggcctctcagaagagcctctactgaaaaatatggatcatcacagatccaaacagaaaaatgggggcgatgtccccacatggagggaacacccgacttagcaaatgggaccggtccccagggtcaggctcttagagcaggcacaagactgggacactggacagaaggttgttcccatgatggttttttttattttttatttttgagatggagtttcgctctgttgcccaggctggagtgtaatggtgcaatctcggctcactgcaacctctgcctcctgggttcaagcgattctcctgcctcagcctcccgagtagctgggattacaggcgcctgacaccacgccccgctaattttttgtatttttagtagagatggggtttcaccatgttggccaggctggtctcaaacgccagacctcaggtgatccacctgcctcagcctcccaaagtgctgagattacaggggtgagtcaccgcgcctggccaatgttgttgttgtttttaagacagaatttcactctttgttgcccaggctggagtgcaatggcgcaatctctggctcaccgcaacctccgcctcccaggttcaagcgattctcctacctcagcccccagagtagctgggattacaggcatgtaccatcacacccggctaattttttgtattttaagtagagagggggtttctccatgttggtcaggctggcctcgaactcccaacctcaggtgatccgcccacctcggcctcccaaaatgctgggattacaggggtaagccactgtgcccggccggttatttctttaaaaggtaatcatttgtcaagagtaaaacccagaagctctgacaggccataatttcagatcctttggcttgggcagttttgattttccccgtgtttgcatggcatgaagtcttcgtccttgtcacagtagcttgggatgactcccagtccacatggaaaacatcagggagtgacaatccagcaagaaatccctcgctagttccacacctacgcaccgagcgtcggtgtgccaggccctgtgctgggcagagtgtggtatgtcagggtgtgccggttttaggtaacaagactccaccactgagtggcacctgccctattgcaaaggaatccagttcctccggaataacagtcccactgttaacctggtgctactgggaagttccacacagtaatctgagcagtgactcatggaaggatgaggaacgtttgctccagcttctctccctttccagcaagggcagagctcctaaagccaggggttagcacctggccagcttatgtggcagatggtctcagttacaacttcgctgctttcccaaactcctgcagccctcctgagtccgacttccgttgatagcaaggcactgggtggcagcaaccttttttctagtagttttttcccagcagttttccatttctccacagtatccttttcatttagaggagcttaataaatgctttttaaaaagtaacccacgtgacgtaaaattttacaagtttttgtggcaaaatgatgcccagatagtcacatttaagcaaatattcagcttgattcagtgattaacagcaaatgggtctacgtgctaacatggcagcacattcaacacataacacatcactcacattgacgtccactgtccctgcacctgctacttcaggggcactgaggctcctgttccaaggccttacaaacctatgtggtggcctgcagggcaaaaggaattatcattacaactggttagaggtaggaattcagaaagaaattgaggaggccaaacacacgtcgtttgaggctaaaggcttaagacgcttcttacccaagagtgacctcagagtttcacatcccagacaatcacactgtggttgagtgaaatcaagtgcagttttatttaagaactggaaagaataatcagtatctgtgaaagaaaatccaatttagaatatttaaataaacatttatgtaaaaagaaaaaaaaaaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("RNF207");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6278413,
				PositionType.ZERO_BASED), "A", "G");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(16, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("1718A>G", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Asn573Ser)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.MISSENSE_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc001auk_2() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001auk.2	chr1	-	12884467	12891264	12884799	12888523	4	12884467,12887107,12888356,12891209,	12885361,12887689,12888664,12891264,	O60813	uc001auk.2");
		this.builderForward
				.setSequence("ttcctgcttggttcttcctgaggtctgagcaccttctagactacatccagatctgttttccctgcagattcatgaagatgagcatccggattccacccagactcctggagcttgcgggcggagcctgctgagggaccaagccttggccgtctccaccctggaggagctgcccacggaacttttccccccactgttcatggaggccttcagcaggagacgctgtgaggccctgaagctgatggtgcaggcctggcccttccgccgcctccctctgaggcctctgataaagatgccttgtctggaggccttccaagctgtgctcgatgggcttgatgcactgcttacccaaggggttcgtcccaggagatggaaacttcaagtgctggatttacaggatgtctgtgagaacttctggatggtttggtctgaagctatggcccatgggtgcttcctcaatgccaagaggaacaaaacaccagtgcaggactgtccaaggatgagagaacggcagcccttgactgtgtttgtagaactttggctcaagaacaggactctggatgaatacctcacctgcctccttctatgggtcaagcagaggagagatttactacacctgtgctgtaagaagctgaaaattttgggaatgcccttccgcaatatcagaagcatcctgaaaatggtgaacctagactgtatccaggaggtggaagtgaattgcaagtggatactgcccatcctgacacagtttaccccatacctgggccacttgaggaatcttcagaagctcgttctctcccacatggatgtctctcgctacgtttccccagagcagaagaaggagattgttacccagttcaccactcagttcctcaagctgcgctgcctccaaaagctttatatgaactctgtttctttcctcgaaggccacctggaccagctgctcagctgtctgaagacctcgttaaaggtcctcacaataactaactgtgtgcttttggaatcagacttgaagcatctctcccagtgcccgagtatcagtcaactaaagaccctggacctgagtggcatcagactgaccaattacagtcttgtgcctctccaaattctcctagaaaaagttgcagccacccttgagtacctggatttagatgactgtggcatcatagactcccaagtcaacgccatcctgcctgccctgagccgctgctttgagctcaacaccttcagcttctgtggaaatcccatctgcatggccaccctggagaacctgctgagccacacaatcatactcaaaaacttatgcctggagctgtatcctgccccgcaggaaagttatggtgctgatggtactctctgctggagcagatttgctcaaattagggctgagctgatgaagaaagtgaggcacttaaggcaccccaagaggatcttgttctgtactgacaactgccctgaccatggcgacaggtcattttatgacctggaggcagatcaatactgctgttgaatgcctgcctatttggatgggtatgtcaaacgctttcttctggacacttggaaactaaaacctaggtcttaggtacatcctaaagggagcacagaacccatcgtttcacacatgggctctgaaagtgggaaaggaaagctgatcaagcaggggcaggacttgggggaaatgttgccatggattcgatgggactttgggaacctgtatcctgtagagtcgaaaatgggaatctgaatgtctagagtggaattcaggcttgagaatacatgagggagttactcttgcatggatggttgtaaagaaacaatcagaaataaaggaaaactgagcag"
						.toUpperCase());
		this.builderForward.setGeneSymbol("(AMEF11)");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 12887548,
				PositionType.ZERO_BASED), "T", "C");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(2, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("308A>G", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Glu103Gly)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.MISSENSE_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc010obg_2() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(refDict,
						"uc010obg.2	chr1	-	13182959	13184326	13182990	13183872	2	13182959,13184264,	13184053,13184326,	NP_001130033	uc010obg.2");
		this.builderForward
				.setSequence("cctggagttcctgcttggctcttcctgaggtctgagcaccttctaaactacaaccagatctgaattcttgttggcagccattttgtgaagagacgaagactgagctgttttggctgcatttctggcctcgagccgcagtcagcttctccccgtagaacccggcagtaggagacttagaatcgaatctcttctccctcccgcctcctgtttttggctttttgagaaaccttatcatccaacacaatggccagcaacgttaccaacaagatggatcctcactccgtgaactcccgtgtgttcattgggaatctcaacactcttgttgtcaagaaatctgatgtggaggcgatcttttccaagtatggcaaaattgcgggctgctctgttcataagggctttgccttcgttcaatatgataaggagaaaaatgcccgggctgctgtagcaggagaggatggcagaatgattgctagccaggttgcagttattaacctggctgcagagccaaaagtgaaccgaggaaacgcaggtgtgaaacgatccgcagcggagatgtacggctcctcttttgacttggactatggctttcaacgggattattatgatgggatgtacagtttcccagcacgtgtacctcctcctcctcccattgctctggctgtagtgccctcgaaacaccagcgcatatcaggaaacacctcacgaaggggcaaaagtggcttcaattctaagagtggaaagcggggatcttccaagtctggaaagctgaaaggagatgaccttcaggccattaagcaggagttgacccagataaaacagaaagtggattctctcctggaaaacctggaaaaaattgaaaaggaacagagcaaacaagaggtagaggtgaaaaatgctaagtcagaagaggagcagagcagtagctccatgaagaaagatgagactcatgtaaagatggagtctgaggggggtgcagaagactctgctgaggagggggacccactggatgatgatgataatgaagatcagggggacaaccagcttcatttgatcaagaataatgaaaaagatgctgaggaaggagaggataacagagacagcaccaatggccaggatgactcttaagcacatagtggggttgagaaatcttatccca"
						.toUpperCase());
		this.builderForward.setGeneSymbol("LOC440563");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 13183438,
				PositionType.ZERO_BASED), "T", "C");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(1, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("434A>G", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(His145Arg)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.MISSENSE_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc001awf_3() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001awf.3	chr1	+	15684286	15690812	15684668	15690297	4	15684286,15686973,15689137,15690236,	15684779,15687199,15689222,15690812,		uc001awf.3");
		this.builderForward
				.setSequence("gagacagacatgacagccctcctcaaatatttaaagggctgtctcataaaagagaaataaaacatactcaatgagctccaggtgacagaactgagctcagagaccagaaatcacaaggagacagatctccgctgaatataaagacgagttctataataattggaaccttccagagaaggcatggactttgtgctttaaaagaatcgacagaataattagagaatagactttaccctgtccttgaaggtgggggataagggggctgggtaaatatgtattcctgaatgtctgttattttgctacttgtttttttgtctaggaattagaattaaaagagcaaaaagaggacgttttaaataataaattaagtgacgcactggccatggttgaagagactcagaaaacaaaggcaactgaaagtctaaaagcagagagcctcgccttgaaattaaatgaaacattagccgaactggaaactaccaagacaaaaatgatcatggtggaagagcggctaatcctgcagcagaagatggtaaaggccctccaggatgagcaggaatcacagagacacgggtttgaagaagagatcatggaatataaggagcaaatcaaacagcacgcccagacaattgtgagcctcgaagagaaactccagaaagtcactcagcaccataaaaaaatagaaggcgagattgcaacattgaaggacaatgacccagccccaaaggaggaaaggccgcaagaccctctggtggctcccatgacagagagcagtgccaaagacatggcgtacgaacatctgattgagtctacaaagaagtctatcctcaaagaactgacagttttcagagaaacaaacgtatgaatgggaaagatccagaaagcagcacgtaaagaaggaggcgccgatgggatgcagtggagggattcagaagctcccccaagaggagaaccacctgcctgacccttgcaccaaatcctagtgaccgaagtctaatgtcacagggtttttaccccaaaaagacaaggatactcagcctccgcattgtgaaaagttcctgactcctcctcctgtttaaaatctagaaacggtggtgcccatgacttggagaagactcagcaccacccagctctgctctggtccacttccttggcgtgtgaacatgcttgaattctaaggttttatcctcaagttggaagatttgtggttgcagccaagaacacactcacgtggacacacacgcatgcatgcacacacagaatatactatatctatgtagttattttcattctgatgatcaccagtctttttgtttgtttgtttgacatgcaacctgccagtgttttactttaattcataataaacacagtaccttctaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("FHAD1");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 15687058,
				PositionType.ZERO_BASED), "A", "G");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(1, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("197A>G", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Glu66Gly)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.MISSENSE_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc001awe_1() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001awe.1	chr1	+	15671911	15724622	15679420	15723909	14	15671911,15679372,15684605,15686973,15689137,15692322,15693958,15695865,15700997,15702098,15707196,15707726,15708523,15723792,	15672019,15679480,15684779,15687199,15689222,15692429,15694118,15695998,15701127,15702226,15707292,15707947,15708689,15724622,	Q5JYW6	uc001awe.1");
		this.builderForward
				.setSequence("gctttggcgaaaagcattacccaggagaagaacagagtgaaggaagcattagaggaagagcagacaagagtccaagagctggaggaacgcttggcccgccagaaggagatctcagagagcaacattgcgtacgagaaacgcaaagcaaaggaggccatggagaaggaaaagaaaaaggtgcaagacctggagaatcgcttaaccaagcagaaagaggaattagaattaaaagagcaaaaagaggacgttttaaataataaattaagtgacgcactggccatggttgaagagactcagaaaacaaaggcaactgaaagtctaaaagcagagagcctcgccttgaaattaaatgaaacattagccgaactggaaactaccaagacaaaaatgatcatggtggaagagcggctaatcctgcagcagaagatggtaaaggccctccaggatgagcaggaatcacagagacacgggtttgaagaagagatcatggaatataaggagcaaatcaaacagcacgcccagacaattgtgagcctcgaagagaaactccagaaagtcactcagcaccataaaaaaatagaaggcgagattgcaacattgaaggacaatgacccagccccaaaggaggaaaggccgcaagaccctctggtggctcccatgacagagagcagtgccaaagacatggcgtacgaacatctgatagatgacttattggctgctcagaaggaaattctgtctcagcaggaagtcatcatgaagttaaggaaagaccttaccgaagcccacagcagaatgtcggatttgagaggggagctaaacgagaagcagaagatggaactggagcagaacgtggtgctggtccagcagcagagcaaggagctgagtgtgctcaaggagaagatggcccagatgagcagcctggtagaaaagaaagatcgggagctgaaggcccttgaggaggcactcagggcttcccaagagaaacacagactccagctgaacacagagaaggaacagaagccccggaagaagacccagacgtgtgacacctctgtgcagatagaacccgtccacactgaggccttctccagcagccaagagcagcaatccttcagcgatctaggggtcaggtgcaaagggtcccggcacgaggaggtcattcagcgtcagaaaaaggccttatctgaacttcgagcgcgaattaaagaactcgagaaggcgcgctcaccagatcataaagaccaccagaatgaatcatttctagatttaaagaacctcagaatggaaaacaatgtccagaaaatactactggatgcaaaaccggatttgccaactctctcaagaatagagatcctagcgcctcagaatggcctttgcaacgcaaggttcggctcagccatggagaagtcagggaagatggatgtggctgaggctttagagctcagtgaaaagctgtacctggatatgagcaaaaccctcggaagtctcatgaacatcaagaatatgtcaggccacgtgtccatgaaatacctctcccgccaggagagggagaaggtcaaccagcttcgacaaagggacctcgacctggtgtttgataagatcacccaactcaagaaccagctggggaggaaagaggagctgttgagaggatatgaaaaggacgttgaacagctcaggcggagcaaagtgtccattgagatgtaccagtcgcaggtggcaaagctggaggatgatatctacaaagaggccgaagagaaggccctgctgaaggaggccctggagcgcatggagcaccagctgtgccaggagaagaggatcaacagggccatccggcagcagaaggttggaaccagaaaagcctccctaaagatggaccaagaaagagagatgctgaggaaagagacctccagcaagtccagccagagccttttgcattctaagcccagtggaaagtactagagaaacctcgtcccaccaggcctcatgtgatcctctgtgagttcatgtgactcttctgtgtcatctgtgtcaaaatactgagttgcttttgtaagtctttaaagattgttaccctagtgtttcatttcctagaccagtattttgaacaatattatattttggagactgtggggagaagggttcttctttaaaaatacctatgaatgtacacgaactcaggtatatgaagaatagaagtgtgtaacccagatgtccaggcctggtagatataattatgtggtccacgttgggtcatgatgttcccaaatatcaaacctcctaagacttccaacagactcacacttgagaaaacctaagacttgtactggagcctcagggcagaattgtaaccttggagctctcagggccttgggatagtgaattcaagtctccgttatggcctggcaggatggctcatgcctgtaatctcagcactttgggaggccgaggcaggcggattataagatcaggagtttgagaccagcctggcaaacatagtgaatacctgtctctactaaaaatacaaaaattagccaggtgtggtggcacatgcctgtagtcccagctacacaggaggctgaggtgggagaatcacttgaacctaggaggcagaggttgcagtgagccaagaccacgccattgcacgccagcctaacagagtgagactctgtctt"
						.toUpperCase());
		this.builderForward.setGeneSymbol("FHAD1");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 15687058,
				PositionType.ZERO_BASED), "A", "G");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(3, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("320A>G", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Glu107Gly)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.MISSENSE_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc010obl_1() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc010obl.1	chr1	+	15671578	15724622	15671647	15723909	16	15671578,15671911,15679372,15684605,15686973,15689137,15692322,15693958,15695865,15700997,15702098,15707196,15707726,15708523,15717705,15723792,	15671686,15672019,15679480,15684779,15687199,15689222,15692429,15694118,15695998,15701127,15702226,15707292,15707947,15708689,15717759,15724622,	B7WPP2	uc010obl.1");
		this.builderForward
				.setSequence("gctttggaggagtacattactcaagagagaaacagagcgaaagagactttagaggaagaacggaagagaatgcaagaactggagagcctcctggcccagcagaagaaggctttggcgaaaagcattacccaggagaagaacagagtgaaggaagcattagaggaagagcagacaagagtccaagagctggaggaacgcttggcccgccagaaggagatctcagagagcaacattgcgtacgagaaacgcaaagcaaaggaggccatggagaaggaaaagaaaaaggtgcaagacctggagaatcgcttaaccaagcagaaagaggaattagaattaaaagagcaaaaagaggacgttttaaataataaattaagtgacgcactggccatggttgaagagactcagaaaacaaaggcaactgaaagtctaaaagcagagagcctcgccttgaaattaaatgaaacattagccgaactggaaactaccaagacaaaaatgatcatggtggaagagcggctaatcctgcagcagaagatggtaaaggccctccaggatgagcaggaatcacagagacacgggtttgaagaagagatcatggaatataaggagcaaatcaaacagcacgcccagacaattgtgagcctcgaagagaaactccagaaagtcactcagcaccataaaaaaatagaaggcgagattgcaacattgaaggacaatgacccagccccaaaggaggaaaggccgcaagaccctctggtggctcccatgacagagagcagtgccaaagacatggcgtacgaacatctgatagatgacttattggctgctcagaaggaaattctgtctcagcaggaagtcatcatgaagttaaggaaagaccttaccgaagcccacagcagaatgtcggatttgagaggggagctaaacgagaagcagaagatggaactggagcagaacgtggtgctggtccagcagcagagcaaggagctgagtgtgctcaaggagaagatggcccagatgagcagcctggtagaaaagaaagatcgggagctgaaggcccttgaggaggcactcagggcttcccaagagaaacacagactccagctgaacacagagaaggaacagaagccccggaagaagacccagacgtgtgacacctctgtgcagatagaacccgtccacactgaggccttctccagcagccaagagcagcaatccttcagcgatctaggggtcaggtgcaaagggtcccggcacgaggaggtcattcagcgtcagaaaaaggccttatctgaacttcgagcgcgaattaaagaactcgagaaggcgcgctcaccagatcataaagaccaccagaatgaatcatttctagatttaaagaacctcagaatggaaaacaatgtccagaaaatactactggatgcaaaaccggatttgccaactctctcaagaatagagatcctagcgcctcagaatggcctttgcaacgcaaggttcggctcagccatggagaagtcagggaagatggatgtggctgaggctttagagctcagtgaaaagctgtacctggatatgagcaaaaccctcggaagtctcatgaacatcaagaatatgtcaggccacgtgtccatgaaatacctctcccgccaggagagggagaaggtcaaccagcttcgacaaagggacctcgacctggtgtttgataagatcacccaactcaagaaccagctggggaggaaagaggagctgttgagaggatatgaaaaggacgttgaacagctcaggcggagcaaagtgtccattgagatgtaccagtcgcaggtggcaaagctggaggatgatatctacaaagaggccgaagagaaggccctgctgaaggaggccctggagcgcatggagcaccagctgtgccaggagaagaggatcaacagggccatccggcagcagaagagacgagtatttgtagagatggtgaagaacaggatgcagaactcaaattcccaggttggaaccagaaaagcctccctaaagatggaccaagaaagagagatgctgaggaaagagacctccagcaagtccagccagagccttttgcattctaagcccagtggaaagtactagagaaacctcgtcccaccaggcctcatgtgatcctctgtgagttcatgtgactcttctgtgtcatctgtgtcaaaatactgagttgcttttgtaagtctttaaagattgttaccctagtgtttcatttcctagaccagtattttgaacaatattatattttggagactgtggggagaagggttcttctttaaaaatacctatgaatgtacacgaactcaggtatatgaagaatagaagtgtgtaacccagatgtccaggcctggtagatataattatgtggtccacgttgggtcatgatgttcccaaatatcaaacctcctaagacttccaacagactcacacttgagaaaacctaagacttgtactggagcctcagggcagaattgtaaccttggagctctcagggccttgggatagtgaattcaagtctccgttatggcctggcaggatggctcatgcctgtaatctcagcactttgggaggccgaggcaggcggattataagatcaggagtttgagaccagcctggcaaacatagtgaatacctgtctctactaaaaatacaaaaattagccaggtgtggtggcacatgcctgtagtcccagctacacaggaggctgaggtgggagaatcacttgaacctaggaggcagaggttgcagtgagccaagaccacgccattgcacgccagcctaacagagtgagactctgtctt"
						.toUpperCase());
		this.builderForward.setGeneSymbol("FHAD1");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 15687058,
				PositionType.ZERO_BASED), "A", "G");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(4, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("515A>G", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Glu172Gly)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.MISSENSE_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc001awd_1() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001awd.1	chr1	+	15668231	15711054	15671647	15709654	16	15668231,15671578,15671911,15679372,15684605,15686973,15689137,15692322,15693958,15695865,15700997,15702098,15707196,15707726,15708523,15709651,	15668384,15671686,15672019,15679480,15684779,15687199,15689222,15692429,15694118,15695998,15701127,15702226,15707292,15707947,15708689,15711054,	B7WPP2	uc001awd.1");
		this.builderForward
				.setSequence("atcaagttcaacagttctcaggaaactcagcagtctttactgcaggaaaagctgcgggagcatctggcagagaaggagaagctgaacgaggagaggctagagcaagaggagaagctcaaagccaaaatcaggcaactgacggaagagaaggcggctttggaggagtacattactcaagagagaaacagagcgaaagagactttagaggaagaacggaagagaatgcaagaactggagagcctcctggcccagcagaagaaggctttggcgaaaagcattacccaggagaagaacagagtgaaggaagcattagaggaagagcagacaagagtccaagagctggaggaacgcttggcccgccagaaggagatctcagagagcaacattgcgtacgagaaacgcaaagcaaaggaggccatggagaaggaaaagaaaaaggtgcaagacctggagaatcgcttaaccaagcagaaagaggaattagaattaaaagagcaaaaagaggacgttttaaataataaattaagtgacgcactggccatggttgaagagactcagaaaacaaaggcaactgaaagtctaaaagcagagagcctcgccttgaaattaaatgaaacattagccgaactggaaactaccaagacaaaaatgatcatggtggaagagcggctaatcctgcagcagaagatggtaaaggccctccaggatgagcaggaatcacagagacacgggtttgaagaagagatcatggaatataaggagcaaatcaaacagcacgcccagacaattgtgagcctcgaagagaaactccagaaagtcactcagcaccataaaaaaatagaaggcgagattgcaacattgaaggacaatgacccagccccaaaggaggaaaggccgcaagaccctctggtggctcccatgacagagagcagtgccaaagacatggcgtacgaacatctgatagatgacttattggctgctcagaaggaaattctgtctcagcaggaagtcatcatgaagttaaggaaagaccttaccgaagcccacagcagaatgtcggatttgagaggggagctaaacgagaagcagaagatggaactggagcagaacgtggtgctggtccagcagcagagcaaggagctgagtgtgctcaaggagaagatggcccagatgagcagcctggtagaaaagaaagatcgggagctgaaggcccttgaggaggcactcagggcttcccaagagaaacacagactccagctgaacacagagaaggaacagaagccccggaagaagacccagacgtgtgacacctctgtgcagatagaacccgtccacactgaggccttctccagcagccaagagcagcaatccttcagcgatctaggggtcaggtgcaaagggtcccggcacgaggaggtcattcagcgtcagaaaaaggccttatctgaacttcgagcgcgaattaaagaactcgagaaggcgcgctcaccagatcataaagaccaccagaatgaatcatttctagatttaaagaacctcagaatggaaaacaatgtccagaaaatactactggatgcaaaaccggatttgccaactctctcaagaatagagatcctagcgcctcagaatggcctttgcaacgcaaggttcggctcagccatggagaagtcagggaagatggatgtggctgaggctttagagctcagtgaaaagctgtacctggatatgagcaaaaccctcggaagtctcatgaacatcaagaatatgtcaggccacgtgtccatgaaatacctctcccgccaggagagggagaaggtcaaccagcttcgacaaagggacctcgacctggtgtttgataagatcacccaactcaagaaccagctggggaggaaagaggagctgttgagaggatatgaaaaggacgttgaacagctcaggcggagcaaagtgtccattgagatgtaccagtcgcaggtggcaaagctggaggatgatatctacaaagaggccgaagagaaggccctgctgaaggaggccctggagcgcatggagcaccagctgtgccaggagaagaggatcaacagggccatccggcagcagaagtagatgggagcttccagcccctgcctcgcagacagatggtgaggatgaaagaatgcaggcatccctcgctatctgctgtccactctcgctgtcgtcatacctcaggagccaagtgcttttggatggtgagggtgcttgtcccgtctctgaagcacctgcccagggcctggcaccagccggcggacactgccttccccatgctgcccatgacccatgctgcccgtgacccatgcaggtggtctcctgggtgagtcctgcctcagacccggtagggtctacacactggaaagtttccccaaagcaagtcacacatgagcagcacccgggcggcttccaaagctccctgaaagctggcagaggggctttcctgtctgctttgcagttgacatattcatcatccactcctgcagtccacaaggcactggaacagtgacctctaaatagagccagactctccctttccttcactcttgcctaaggtggagcagatcccaaggctggggttaggcaagtcagggaaagaaccacagtcactgtttcttcatgaccgtctccaggccagggactgggctcacctgagctttgcagccgtgatagggcttattaatgcctctaatagccctgcctggtacgatgatccctattttacagatgaggaaactgcgcctcagagagttaagaggcgcccaagtgcacacagcggctggtaaggacagctgggacctgaccccagatccctctgaccctggttctcctgctgtctgcagggttggccgtgagtcccctccttgtaactgtcagcttttatgtgtgtgtgcattctcgtgtgtgtgtacattctcatgtgtgtgtgtgtgtgcacatgtgtaccatgtgcatgagggtttggctgtgtgtgacactatgtgtgtgtgtttgtgtgtgttgcctgcctgagctcagagagagccaaacccccagagaagggtgccccctccaccaaccaggtgagctccttgcagaggcctggccttcatcccacaaaccttgcagaccacaggctccctggcttgcagcccccaaaaatgaaggcagcgctctgctctggacgtggcctttccagcactcaccactctgaattaaacatcacaggcccccatctgcacatgtcgtggggctgcctcgggcagaggaccgtttccttttacgctgtgccatgccagggagatctgggcagcagcagagtcctgagatgtcctttgatgtacccacaggagagttcgctccttcccgaggcagtgtcccccagggcttggcaggccaggcccacagcagagacaccaaaccacagaatgggacggcagagggccctaaaaagcccattgtcatgcactggctgaaaatgaaagaaaacctgcagattt"
						.toUpperCase());
		this.builderForward.setGeneSymbol("FHAD1");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 15687058,
				PositionType.ZERO_BASED), "A", "G");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(5, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("515A>G", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Glu172Gly)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.MISSENSE_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc001awb_2() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001awb.2	chr1	+	15573767	15724622	15578280	15723909	31	15573767,15578266,15598818,15615894,15623179,15627700,15635108,15639552,15642873,15644319,15650947,15653554,15654772,15655841,15668231,15671578,15671911,15675537,15679372,15684605,15686973,15689137,15692322,15693958,15695865,15700997,15702098,15707196,15707726,15708523,15723792,	15573891,15578373,15599025,15616162,15623289,15627937,15635232,15639683,15642963,15644424,15651055,15653638,15654925,15656037,15668389,15671686,15672019,15675645,15679480,15684779,15687199,15689222,15692429,15694118,15695998,15701127,15702226,15707292,15707947,15708689,15724622,	B1AJZ9	uc001awb.2");
		this.builderForward
				.setSequence("gggtgcgagctggagactccgcgggagcgcggccgggaggcttcgccccggagctggcccgacgcctcccgagctggcagggctctcggcggaggtcggagcgtgggcttcctcctcccgccagggaaaacagagaggatgaaggcctatctaaagagcgcagaaggcttttttgtcctaaataaaagtaccacaattggaaggcatgaaaattcagaccttgttttacagtctcctgacatcgacaaccaccatgcactcattgaatataacgaggcggagtgcagctttgttctccaggacttcaattcccgcaacggcacgtttgtcaacgagtgccacattcaaaacgtggctgtgaagctcatccctggagacatcctgagatttgggtctgcagggctgacctatgaactggtcattgaaaatccacctccggtctctttcccatggatgaggggcccagcaccatggccagggccacagccacctcgtgccacacagcagccaaaccaggcccccccaccatcacatatccccttccaccaaggtgtccagccagcaccgatgcaaaggagctggtcccaggcctttcccagacccaccgtggtcctgccggcctcccacaggcggcctgtgagcgccaacaaggagatgttctcgttcgtggtggacgacgcccgcaagccacccgtcatcaagcaagtgtggaccaatgccatgaaactgtcagaaaaatcagtggccgaggggattcctggggcagttccccctgcggagatttatgtggaggaggacttggcccagcaggacaaggatgaaataattctgctgctgggaaaagaggtcagccgtctctcagattatgaaattgaatccaaatacaaagacgtcataatagcaaacctgcagaatgaagtggctgagctgagtcagaaggtgtcagagaccaccacctccaggcagaatgagaaggagatctcgcagaagtgtcaggttctggatgaagacatcgatgccaaacagaaagagatccagagcttgaaaagccagatcagtgccctacagaaaggctacagcaaggtgctgtgccagaccctgtcagagcggaactcagaaatcacatccctgaagaatgagggcgagaacttaaagagagacaacgctatcacatcagggatggtgtcatctttgcaaaaagacatattagcaaaggatgagcaagttcaacaactaaaggaagaggtcagtcacctaaaaagtcagaacaaggacaaggaccaccagctggaagcccttggctctagatgctcggtgctaaaggaagagttaaaacaggaagatgctcacagggagctcagggaagcccaggagaaagagttaaaactctgcaaaacccaaatccaagacatggagaaagaaatgaagaagcttagggcagagctgaggaagagttgtactgaacaaagcgtgatctctaggactctgagagaaaaaagcaaggttgaagagaagcttcaggaggattccagaaggaaattgcttcagctgcaagaaatggggaacagagagagcgtcattaaaatcaatttggagagggcagtaggtcagctggagcacttcagaagtcaagtcatcaaggccacctatggacgggcgaagccgttccgggacaagcccgtcaccgaccaacagttaatagagaaaattacccaggtcactgaggacaacatcaattttcagcagaaaaagtggaccctccagaaagagacccagctgagcaactccaagcaggaggagaccaccgagaacatcgagaagctgaggacgtcgctggacagctgccaggcttgcatgaaaatatcctgttgcagccatgacctgaagaaggaggtcgaccttcttcagcacctccaggtgagcccacctgtctcggggctccagaaggtggtgctggacgtcctgaggcacgcgctgtcctggctggaggaggtggagcagctcctccgggacctcgggatcctgccctccagccccaacaaagatcaagttcaacagttctcaggaaactcagcagtctttactgcaggaaaagctgcgggagcatctggcagagaaggagaagctgaacgaggagaggctagagcaagaggagaagctcaaagccaaaatcaggcaactgacggaagagaaggcggtaaggctttggaggagtacattactcaagagagaaacagagcgaaagagactttagaggaagaacggaagagaatgcaagaactggagagcctcctggcccagcagaagaaggctttggcgaaaagcattacccaggagaagaacagagtgaaggaagcattagaggaagagcagacaagagtccaagagctggaggaacgcttggcccgccagaaggaggttttggagagcagcatagcccatgaaaaaagaaaagcaaaggaagccttggagtcggaaaagagaaaagttcaggatctggagaaccatttaacccaacagaaggagatctcagagagcaacattgcgtacgagaaacgcaaagcaaaggaggccatggagaaggaaaagaaaaaggtgcaagacctggagaatcgcttaaccaagcagaaagaggaattagaattaaaagagcaaaaagaggacgttttaaataataaattaagtgacgcactggccatggttgaagagactcagaaaacaaaggcaactgaaagtctaaaagcagagagcctcgccttgaaattaaatgaaacattagccgaactggaaactaccaagacaaaaatgatcatggtggaagagcggctaatcctgcagcagaagatggtaaaggccctccaggatgagcaggaatcacagagacacgggtttgaagaagagatcatggaatataaggagcaaatcaaacagcacgcccagacaattgtgagcctcgaagagaaactccagaaagtcactcagcaccataaaaaaatagaaggcgagattgcaacattgaaggacaatgacccagccccaaaggaggaaaggccgcaagaccctctggtggctcccatgacagagagcagtgccaaagacatggcgtacgaacatctgatagatgacttattggctgctcagaaggaaattctgtctcagcaggaagtcatcatgaagttaaggaaagaccttaccgaagcccacagcagaatgtcggatttgagaggggagctaaacgagaagcagaagatggaactggagcagaacgtggtgctggtccagcagcagagcaaggagctgagtgtgctcaaggagaagatggcccagatgagcagcctggtagaaaagaaagatcgggagctgaaggcccttgaggaggcactcagggcttcccaagagaaacacagactccagctgaacacagagaaggaacagaagccccggaagaagacccagacgtgtgacacctctgtgcagatagaacccgtccacactgaggccttctccagcagccaagagcagcaatccttcagcgatctaggggtcaggtgcaaagggtcccggcacgaggaggtcattcagcgtcagaaaaaggccttatctgaacttcgagcgcgaattaaagaactcgagaaggcgcgctcaccagatcataaagaccaccagaatgaatcatttctagatttaaagaacctcagaatggaaaacaatgtccagaaaatactactggatgcaaaaccggatttgccaactctctcaagaatagagatcctagcgcctcagaatggcctttgcaacgcaaggttcggctcagccatggagaagtcagggaagatggatgtggctgaggctttagagctcagtgaaaagctgtacctggatatgagcaaaaccctcggaagtctcatgaacatcaagaatatgtcaggccacgtgtccatgaaatacctctcccgccaggagagggagaaggtcaaccagcttcgacaaagggacctcgacctggtgtttgataagatcacccaactcaagaaccagctggggaggaaagaggagctgttgagaggatatgaaaaggacgttgaacagctcaggcggagcaaagtgtccattgagatgtaccagtcgcaggtggcaaagctggaggatgatatctacaaagaggccgaagagaaggccctgctgaaggaggccctggagcgcatggagcaccagctgtgccaggagaagaggatcaacagggccatccggcagcagaaggttggaaccagaaaagcctccctaaagatggaccaagaaagagagatgctgaggaaagagacctccagcaagtccagccagagccttttgcattctaagcccagtggaaagtactagagaaacctcgtcccaccaggcctcatgtgatcctctgtgagttcatgtgactcttctgtgtcatctgtgtcaaaatactgagttgcttttgtaagtctttaaagattgttaccctagtgtttcatttcctagaccagtattttgaacaatattatattttggagactgtggggagaagggttcttctttaaaaatacctatgaatgtacacgaactcaggtatatgaagaatagaagtgtgtaacccagatgtccaggcctggtagatataattatgtggtccacgttgggtcatgatgttcccaaatatcaaacctcctaagacttccaacagactcacacttgagaaaacctaagacttgtactggagcctcagggcagaattgtaaccttggagctctcagggccttgggatagtgaattcaagtctccgttatggcctggcaggatggctcatgcctgtaatctcagcactttgggaggccgaggcaggcggattataagatcaggagtttgagaccagcctggcaaacatagtgaatacctgtctctactaaaaatacaaaaattagccaggtgtggtggcacatgcctgtagtcccagctacacaggaggctgaggtgggagaatcacttgaacctaggaggcagaggttgcagtgagccaagaccacgccattgcacgccagcctaacagagtgagactctgtctt"
						.toUpperCase());
		this.builderForward.setGeneSymbol("FHAD1");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 15687058,
				PositionType.ZERO_BASED), "A", "G");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(20, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("2756A>G", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Glu919Gly)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.MISSENSE_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc001awp_4() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001awp.4	chr1	-	15817895	15850940	15819437	15833555	9	15817895,15820386,15821767,15831105,15832484,15833393,15834367,15844604,15850563,	15819530,15820496,15821947,15831253,15832574,15833628,15834402,15844890,15850940,	P55211	uc001awp.4");
		this.builderForward
				.setSequence("ttggccctgggggcggggcgaggcgcagaggtgcgtcctgagggcggggcggtgacgcaagagcgactcctgggggcggggcgaggccttggagatgcgtccggaggcggtggggagcgaagactgacccggggccgtgacgcgggggcaggccctggggcgggggcgggtcctggggactggggcgggcggccgaggcccggaagcggactgaggcggcctggagtcttagttggctactcgccatggacgaagcggatcggcggctcctgcggcggtgccggctgcggctggtggaagagctgcaggtggaccagctctgggacgccctgctgagccgcgagctgttcaggccccatatgatcgaggacatccagcgggcaggctctggatctcggcgggatcaggccaggcagctgatcatagatctggagactcgagggagtcaggctcttcctttgttcatctcctgcttagaggacacaggccaggacatgctggcttcgtttctgcgaactaacaggcaagcagcaaagttgtcgaagccaaccctagaaaaccttaccccagtggtgctcagaccagagattcgcaaaccagaggttctcagaccggaaacacccagaccagtggacattggttctggaggatttggtgatgtcggtgctcttgagagtttgaggggaaatgcagatttgactgtcctctagggagcgtggggagagcccgggtttacgtgggtccctgtgctggcaggcttacatcctgagcatggagccctgtggccactgcctcattatcaacaatgtgaacttctgccgtgagtccgggctccgcacccgcactggctccaacatcgactgtgagaagttgcggcgtcgcttctcctcgctgcatttcatggtggaggtgaagggcgacctgactgccaagaaaatggtgctggctttgctggagctggcgcagcaggaccacggtgctctggactgctgcgtggtggtcattctctctcacggctgtcaggccagccacctgcagttcccaggggctgtctacggcacagatggatgccctgtgtcggtcgagaagattgtgaacatcttcaatgggaccagctgccccagcctgggagggaagcccaagctctttttcatccaggcctgtggtggggagcagaaagaccatgggtttgaggtggcctccacttcccctgaagacgagtcccctggcagtaaccccgagccagatgccaccccgttccaggaaggtttgaggaccttcgaccagctggacgccatatctagtttgcccacacccagtgacatctttgtgtcctactctactttcccaggttttgtttcctggagggaccccaagagtggctcctggtacgttgagaccctggacgacatctttgagcagtgggctcactctgaagacctgcagtccctcctgcttagggtcgctaatgctgtttcggtgaaagggatttataaacagatgcctggttgctttaatttcctccggaaaaaacttttctttaaaacatcataaggccagggcccctcaccctgccttatcttgcaccccaaagctttcctgccccaggcctgaaagaggctgaggcctggactttcctgcaactcaaggactttgcagccggcacagggtctgctctttctctgccagtgacagacaggctcttagcagcttccagattgacgacaagtgctgaacagtggaggaagagggacagatgaatgccgtggattgcacgtggcctcttgagcagtggctggtccagggctagtgacttgtgtcccatgatccctgtgttgtctctagagcagggattaacctctgcactactgacatgtggggccaggtcaccctttgctgtgaggctgtcctgtacattgtgggatgttcagcactgtcccttgcctcaatgccagtaacgcgtcttcctgagtggtgccaaacaaaaaggttctcaggtgttgccaaatatgtcctggggtataaaactttcctcgcctgacaaccactggtctgtagggatttttggctacacacaaaccagtatcgctcatagatcagcaaaccggggcctactagagtctgaacagctgtaatctatgaattctaagtgaaattttaaaaattgttaatttttcctatattgcattaattttaaaaaataaatctgaggcaaatatggactctcttttgcctatttcttccctcattttgctccaactctttcttcttccttacaaaagagacttttgcttttttcgaaacatttccccatgtttttctggggtctcgctatgttgcccaggctggtctcaaactcctgggctcaagtgaccctcccaagtagctcttactacaggcgtgcaccattgcacccagccccatttattcatgtcttatttcacttgatccttatcccatcccaggaaggcaacaagggtgagaaccctgtgctcagggaggttaggtctcttgtccaagggaaaacgattatccagagaagagacctggccagaacctgggtcccctgagtcctagccatgcttcccatgtgccttacttgctgaagcacccccggactgcagtgtgaacgtgctgtgcaatagtgacacgctgggcttccccacaaggctccaccctgaggtcttttaagctgtccttatgccagcctatttcttgttttttgggcctttttttttggagatagggtctcactctgtcgcccaggctggagtgcaatgacgcaatcttggcttattgcagtctcgacctcctgggctcaagagatccttccacctcagccacctgagtagcttggactacaggtgtgcaccacctctcccagttaatttttgtatttttagtagagacagagttatgccatgttactcaggctggtcttgaactcctggactcaagcgatcagcctgccttagcctcccaaagtgcaggggttacaggcttgagccattgcgcctgacctatttctggttcttagggccctggatgttaggatggatttctgaattaataataataataaaaccctcatcaaga"
						.toUpperCase());
		this.builderForward.setGeneSymbol("CASP9");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 15832542,
				PositionType.ZERO_BASED), "T", "C");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(4, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("194A>G", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Gln65Arg)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.MISSENSE_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc021oho_1() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc021oho.1	chr1	-	19592475	19600568	19593793	19597306	5	19592475,19595065,19596944,19597241,19600354,	19593955,19595195,19597049,19597429,19600568,	Q8NHP1-2	uc021oho.1");
		this.builderForward
				.setSequence("atgtcccggcagctgtcgcgggcccggccagccacggtgctgggcgccatggagatggggcgccgcatggacgcgcccaccagcgccgcagtcacgcgcgccttcctggagcgcggccacaccgagatagacacggccttcctgtacagcgacggccagtccgagaccatccttggcggcctggggctccgaatgggcagcagcgactgcagagtgaaaattgctaccaaggccaatccatggattgggaactccctgaagcctgacagtgtccgatcccagctggagacgtcactgaagcggctgcagtgtccctgagtggacctcttctatctacatgcacctgaccacagcgccccggtggaagagacactgcgtgcctgccaccagctgcaccaggagggcaagttcgtggagcttggcctctccaactatgccgcctgggaagtggccgagatctgtaccctctgcaagagcaacggctggatcctgcccactgtgtaccagcttctggaaggagcaccacttcgagggcattgccctggtggagaaggccctgcaggccgcgtatggcgccagcgctcccagcatgacctcggccgccctccggtggatgtaccaccactcacagctgcagggtgcccacggggacgcggtcatcctgggcatgtccagcctggagcagctggagcagaacttggcagcggcagaggaagggcccctggagccggctgtcgtggacgcctttaatcaagcctggcatttgtttgcccacgaatgtcccaactacttcatctaagctcattgtggctcaggctgcccaaggcttttctgtcaactcttttgctctctcccgctttgtctaatttagaactgcctcactaaattcttagggatggaagtatttggaaaaaaacctaacagtagagtcaccacctaaggaagaataaaatctcccagggtgctgtgtgttagtctgtttgcgttgctataaaagaatacctgagactgggtcatgtataaagaaaagaggtttctttggctcacagttctgcagtctgtacaagagggtgtggtgccggcatctgctcttggtgagggcctcaggaagcttagaatcatggcagaaggggaaacggagccagcgtgtcacatggtgagagagggagcaagagacagacaggggaggaaattcacacactgatggtggggatgtaaaatggtacaaccagtttggaaaacagtttggcagtttctcaaaggattaaacataaaattaccataggattcagcagttccacttgtgggtatgtagccaagagaaatgaaaacatatctccccacaaaaaaacttgggtatgagatttcacattatcattgctcataatagccaataagtaaaaacaacccaaatgtccaagaatgaataaatggataaacaaaatatggtatatttatacaacagactattattcggccattaaaaaaaaaaaagagtggctgacacctgtaatctcagcactttgagaggccaaggcagtaggactgattgaagacaggagttccagaccagtctgggaaacaaagcgagaccctgtctccactaaacataaaaacaaaattactggggccccatggcacacacctgtagtcccagctgctcgggaagctgagatgggcggattgcttgagcccaggtattcaagtctggagtgagctatgactgtgccactgcactccagcctgggcgacagagcaaaccctgtttccaaacaaacaaacaaacaaacaaatacacagacagaagtagttaaacatctacaaggtaagtgcatcttgaagacgtgttaagttaaagaagccaattacaaaaggtttcacgttgtatgatttcgtttatatgaaatgtccagaataggcaaatctgtttgagagagagaaagtagatgagtacttgcctaggactgggaggaggattgcgaggaaatggagattcactgctaatgagtacagggtttcttttgggggcgcttatgaagatgctctgaaattgattgtgatggttgtacaactctgaatacatgaaacagcattaaacatcactttaagtaagtcaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("AKR7L");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 19595136,
				PositionType.ZERO_BASED), "C", "T");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(3, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("229G>A", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Ala77Thr)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.MISSENSE_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc021ohn_1() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc021ohn.1	chr1	-	19592475	19600568	19593793	19596170	7	19592475,19595065,19595765,19596076,19596944,19597241,19600354,	19593955,19595195,19595865,19596173,19597049,19597429,19600568,	Q8NHP1-2	uc021ohn.1");
		this.builderForward
				.setSequence("atgtcccggcagctgtcgcgggcccggccagccacggtgctgggcgccatggagatggggcgccgcatggacgcgcccaccagcgccgcagtcacgcgcgccttcctggagcgcggccacaccgagatagacacggccttcctgtacagcgacggccagtccgagaccatccttggcggcctggggctccgaatgggcagcagcgactgcagagtgaaaattgctaccaaggccaatccatggattgggaactccctgaagcctgacagtgtccgatcccagctggagacgtcactgaagcggctgcagtgtccctgagtggacctcttctatctacatgcacctgaccacagcgccccggtggaagagacactgcgtgcctgccaccagctgcaccaggagggcaagttcgtggagcttggcctctccaactatgccgcctgggaagtggccgagatctgtaccctctgcaagagcaacggctggatcctgcccactgtgtaccagggcatgtacagcgccaccacccggcaggtggaaacggagctcttcccctgcctcaggcactttggactgaggttctatgcctacaaccctctggctgggggcctgctgaccggcaagtacaagtatgaggacaaggacgggaaacagcccgtgggccgcttctttgggactcagtgggcagagatctacaggaatcacttctggaaggagcaccacttcgagggcattgccctggtggagaaggccctgcaggccgcgtatggcgccagcgctcccagcatgacctcggccgccctccggtggatgtaccaccactcacagctgcagggtgcccacggggacgcggtcatcctgggcatgtccagcctggagcagctggagcagaacttggcagcggcagaggaagggcccctggagccggctgtcgtggacgcctttaatcaagcctggcatttgtttgcccacgaatgtcccaactacttcatctaagctcattgtggctcaggctgcccaaggcttttctgtcaactcttttgctctctcccgctttgtctaatttagaactgcctcactaaattcttagggatggaagtatttggaaaaaaacctaacagtagagtcaccacctaaggaagaataaaatctcccagggtgctgtgtgttagtctgtttgcgttgctataaaagaatacctgagactgggtcatgtataaagaaaagaggtttctttggctcacagttctgcagtctgtacaagagggtgtggtgccggcatctgctcttggtgagggcctcaggaagcttagaatcatggcagaaggggaaacggagccagcgtgtcacatggtgagagagggagcaagagacagacaggggaggaaattcacacactgatggtggggatgtaaaatggtacaaccagtttggaaaacagtttggcagtttctcaaaggattaaacataaaattaccataggattcagcagttccacttgtgggtatgtagccaagagaaatgaaaacatatctccccacaaaaaaacttgggtatgagatttcacattatcattgctcataatagccaataagtaaaaacaacccaaatgtccaagaatgaataaatggataaacaaaatatggtatatttatacaacagactattattcggccattaaaaaaaaaaaagagtggctgacacctgtaatctcagcactttgagaggccaaggcagtaggactgattgaagacaggagttccagaccagtctgggaaacaaagcgagaccctgtctccactaaacataaaaacaaaattactggggccccatggcacacacctgtagtcccagctgctcgggaagctgagatgggcggattgcttgagcccaggtattcaagtctggagtgagctatgactgtgccactgcactccagcctgggcgacagagcaaaccctgtttccaaacaaacaaacaaacaaacaaatacacagacagaagtagttaaacatctacaaggtaagtgcatcttgaagacgtgttaagttaaagaagccaattacaaaaggtttcacgttgtatgatttcgtttatatgaaatgtccagaataggcaaatctgtttgagagagagaaagtagatgagtacttgcctaggactgggaggaggattgcgaggaaatggagattcactgctaatgagtacagggtttcttttgggggcgcttatgaagatgctctgaaattgattgtgatggttgtacaactctgaatacatgaaacagcattaaacatcactttaagtaagtcaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("AKR7L");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 19596123,
				PositionType.ZERO_BASED), "C", "T");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(3, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("47G>A", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Cys16Tyr)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.MISSENSE_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_c001bem_2() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001bem.2	chr1	-	21543739	21672034	21546447	21671871	19	21543739,21548239,21551742,21553651,21554423,21560050,21562342,21563238,21564626,21571481,21573713,21582439,21584017,21585185,21586763,21599191,21605683,21616562,21671868,	21546624,21548335,21551933,21553719,21554534,21560154,21562420,21563337,21564737,21571596,21573856,21582631,21584083,21585332,21586885,21599404,21605825,21616649,21672034,	P42892-3	uc001bem.2");
		this.builderForward
				.setSequence("actcggcgccgaagccgcgagctcgcccgctggagctgagcgcgccgcctgggccaggcagccgagccgtccgagcagctgggctgggagcagggaacccggagctgggaatcgggagccgggcgcggggagctgcgcgaagccggggcggagcacgcgagctatgatgtcgacgtacaagcgggccacgctggacgaggaggacctggtggactcgctctccgagggcgacgcataccccaacggcctgcaggtgaacttccacagcccccggagtggccagaggtgctgggctgcacggacccaggtggagaagcggctggtggtgttggtggtacttctggcggcaggactggtggcctgcttggcagcactgggcatccagtaccagacaagatccccctctgtgtgcctgagcgaagcttgtgtctcagtgaccagctccatcttgagctccatggaccccacagtggacccctgccatgacttcttcagctacgcctgtgggggctggatcaaggccaacccagtccctgatggccactcacgctgggggaccttcagcaacctctgggaacacaaccaagcaatcatcaagcacctcctcgaaaactccacggccagcgtgagcgaggcagagagaaaggcgcaagtatactaccgtgcgtgcatgaacgagaccaggatcgaggagctcagggccaaacctctaatggagttgattgagaggctcgggggctggaacatcacaggtccctgggccaaggacaacttccaggacaccctgcaggtggtcaccgcccactaccgcacctcacccttcttctctgtctatgtcagtgccgattccaagaactccaacagcaacgtgatccaggtggaccagtctggcctgggcttgccctcgagagactattacctgaacaaaactgaaaacgagaaggtgctgaccggatatctgaactacatggtccagctggggaagctgctgggcggcggggacgaggaggccatccggccccagatgcagcagatcttggactttgagacggcactggccaacatcaccatcccacaggagaagcgccgtgatgaggagctcatctaccacaaagtgacggcagccgagctgcagaccttggcacccgccatcaactggttgccttttctcaacaccatcttctaccccgtggagatcaatgaatccgagcctattgtggtctatgacaaggaataccttgagcagatctccactctcatcaacaccaccgacagatgcctgctcaacaactacatgatctggaacctggtgcggaaaacaagctccttccttgaccagcgctttcaggacgccgatgagaagttcatggaagtcatgtacgggaccaagaagacctgtcttcctcgctggaagttttgcgtgagtgacacagaaaacaacctgggctttgcgttgggccccatgtttgtcaaagcaaccttcgccgaggacagcaagagcatagccaccgagatcatcctggagattaagaaggcatttgaggaaagcctgagcaccctgaagtggatggatgaggaaacccgaaaatcagccaaggaaaaggccgatgccatctacaacatgataggataccccaacttcatcatggatcccaaggagctggacaaagtgtttaatgactacactgcagttccagacctctactttgaaaatgccatgcggtttttcaacttctcatggagggtcactgccgatcagctcaggaaagcccccaacagagatcagtggagcatgaccccgcccatggtgaacgcctactactcgcccaccaagaatgagattgtgtttccggccgggatcctgcaggcaccattctacacacgctcctcacccaaggccttaaactttggtggcataggtgtcgtcgtgggccatgagctgactcatgcttttgatgatcaaggacgggagtatgacaaggacgggaacctccggccatggtggaagaactcatccgtggaggccttcaagcgtcagaccgagtgcatggtagagcagtacagcaactacagcgtgaacggggagccggtgaacgggcggcacaccctgggggagaacatcgccgacaacgggggtctcaaggcggcctatcgggcttaccagaactgggtgaagaagaacggggctgagcactcgctccccaccctgggcctcaccaataaccagctcttcttcctgggctttgcacaggtctggtgctccgtccgcacacctgagagctcccacgaaggcctcatcaccgatccccacagcccctctcgcttccgggtcatcggctccctctccaattccaaggagttctcagaacacttccgctgcccacctggctcacccatgaacccgcctcacaagtgcgaagtctggtaaggacgaagcggagagagccaagacggaggaggggaaggggctgaggacgagacccccatccagcctccagggcattgctcagcccgcttggccacccggggccctgcttcctcacactggcgggttttcagccggaaccgagcccatggtgttggctctcaacgtgacccgcagtctgatcccctgtgaagagccggacatcccaggcacacgtgtgcgccaccttcagcaggcattcgggtgctgggctggtggctcatcaggcctgggccccacactgacaagcgccagatacgccacaaataccactgtgtcaaatgctttcaagatatatttttggggaaactattttttaaacactgtggaatacactggaaatcttcagggaaaaacacatttaaacacttttttttttaaggaaagaattggtatatttattatgttctgtttttctaaataacctgtggacaagggaagccccactgatttactccctctcttccccactccctgtgaggctgggctgaggcacggatccctgggccacagagcaagtctccaaatcagacagctgcctcagcccctgggatgtgtgatttcagctcctgtcacctcatgcaagggcgtggagaccagtagaggtgtggaggccaggcagagagaggagcctgctctgcggggggcccagctcatgggcactgccccttcagctagcctgcctccgtcccctgagtccaacagtgggagccctagctgggaagttctgatccccaaagccacagcaggggactgatggctatagcagaatgaggtcgggtcaggaccctcaaacaccatctgggaacaccaagcaccctgaatcgagactgcaggagccctgcggggtgagactgtgtcagagatacactgctggccacaagtgtcccctctcagtcccaccttttcgggctgtcccatgtctatctcaggggcccgttacctctctgcagcagtcccccatcccagccacaccagggtctgtccggccaaccctcttccccagggaaaggagaaaagagaaaacaggctgggcccggtggctcactcctgtaatcccagcactttgggaggttgaggtgggcggatcacctgaggtcaggagtttgagaccagcctggccaacgtggtgaaaccccatctctactaaaaaaaattacaaaaattagccgggagtggtggtgggcacctgtaatcccagttactcgggaggctgaggcaagagaatctcttgagctcaggaggcagaggttgcagtgagctgagattgcgccactgcactccagcctgggtgacagagggagactccgtcccaaaaaaaagaaaagagaaacagctgtcacctcccgcaggacccaaatcctctctctgagcaccgtcatccaccacatggctgggcctggctcccaggaccagtccagtcctctagtgccttatctgaggctgcagccgccagtctccaccccaaggagacagcccctgctcctagatgcccttggcctccgcagtgcagcccccaggtgtcctgactgaagcacaggccatagccccatttccccggtgcctgcagggctaacctccacgggagcccaggagctctggccggcaggtccatggcacagggcatcggaagactgcaaaactgctggacttaccctgggctgcagtccattgtcggcccctgggttgaatcaagatagtacttgcagctagatggatgcttttagccaggggacattgtgaggggaagattcctccacccagtctggcctgtggtgtctgtctcctccctgagaccacagcttctccagtagcagactcatgggcgccaccaagtggaagcacctggagcggcctctgccatccagtggggaagccaggccccgagacggaggtgggggcagcacgtgccctccacagccaccgctttcccgcctcagcagcccaggcctcctggcccagccctgcctggacagtgctctcccctcacccgggaagctggaatcctcctgcccgagaggaagcagacggcacagggacacccctgccaccttgggatctgcctccaagctggtgcagggtatcgagagtggattccagatggaggtcctggtcccagcacgcagcagtcctggtagctctgcagaggagacaggaacccgagaagtagctgaagcagaagccagccgcagtccccttgccacatagaggcgggcttctcccagccatggtgtcccctctgcctcccctcccccgaccctcctgccttccgcgtggagggtggtggtcctgtagtgtcagcaccagcaccatgggcttggaccccctccctggacacaggcaggtgtcctagggctgggggtgcagcccgagggaatggagaccacactcatggctcaggtctgccggggccggcagggggttggggaagaagagggctcaggcccagcaggggtggaagcccctgccactgccactacccgctccagagctttaaggaaaatgaagtgagacccctccccttaggcctggggagccatagggctggcttctctgtgggtgcgtggacgtggggttgggagctgggaatctattttttgtattatgttttgagctactgtagttttggcgtggcactattgtaatggaaataaaatacttgtacggagggcaaaaaaaaaaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("ECE1");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 21573854,
				PositionType.ZERO_BASED), "G", "A");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(8, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("974C>T", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Thr325Ile)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.MISSENSE_VARIANT, VariantEffect.SPLICE_REGION_VARIANT),
				annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc001bfa_3() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001bfa.3	chr1	-	22004791	22051612	22005909	22050655	15	22004791,22007300,22013683,22016466,22027990,22030004,22030754,22032219,22032618,22032959,22033236,22041881,22047528,22048142,22050390,	22005932,22007327,22013732,22016592,22028095,22030111,22030885,22032330,22032680,22033082,22033361,22041950,22047656,22048257,22051612,	Q86UV5-5	uc001bfa.3");
		this.builderForward
				.setSequence("aattgagatggtttactgtaaaattaaaatgggagttagttttctagacatgtaactctctggggagtataaaactggtcacaatgggctttgcgttgaagtatctgtccctcagatctcatcagaaggagtatataatacaaaaatataaattcaacctctgtatgttttcttctatgttttatgctgggactcactggagatctttgtaagtaatcaaagctggccctttatctttgattttggcttttagggaattctagggacttcctttccccaaattaaaactcattttgaggtgtttggttttgtcttttaatttttgcaacttcaaaattgatgactcagttctagtgtactttatcctcgaacttgtttaaaataggttactttcttgaaataaataataatataatgaagagatcatgacttggatttgggtgggggaattaaataccttttcaaaattgccatcagtatcatcttggatgaagaaagttcagtggttaaaagcgtcttctgaagtgtggtcatagtcggtgaggtttatttcagtttggcttctcattttaattttcagatgtatatcacatatatgtgaaaagtactgacaaaaataatttatgaaataaacacagtagggcatgaggttgtgttatttgtgaggcagcaatggagcatgacttgtagtcaacaaacatgggtgtcctgatcaaagctctgctgtgctattcggcaagactctatgtggatgcctcttgtagaatgtggttaataataccagccgatctcatagggcactgtgcagactcagcattatggtgcacagctccttgaactcataggagtggcacttaactggagctttgtgtcgtactcatcaaacatcttcccccagacaatgattcttctgattctgctaactctggacagagtttgcattgtcttttgagaaaaatgaatgtatcacttagtcaaaaaactgtaaaaatacataggctctttccgatgcttgctttctcagagccctatgagtttgtctctctggaatggctgcaaaagtggttggatgaatcaacacctaccaaacctattgataatcacgcttgcctgtgttcccatgacaagcttcacccggataaaatatcaattatgaagaggatatctgaatatgcagctgacattttctatagtagatatggaggaggtccaagactaactgtgaaagccctgtgtaaggaatgtgtagtagaacgttgtcgcatattgcgtctgaagaaccaactaaatgaagattataaaactgttaataatctgctgaaagcagcagtaaagggcgatggattttgggtggggaagtcctccttgcggagttggcgccagctagctcttgaacagctggatgagcaagatggtgatgcagaacaaagcaacggaaagatgaacggtagcaccttaaataaagatgaatcaaaggaagaaagaaaagaagaggaggaattaaattttaatgaagatattctgtgtccacatggtgagttatgcatatctgaaaatgaaagaaggcttgtttctaaagaggcttggagcaaactgcagcagtactttccaaaggctcctgagtttccaagttacaaagagtgctgttcacagtgcaagattttagaaagagaaggggaagaaaatgaagccttacataagatgattgcaaacgagcaaaagacttctctcccaaatttgttccaggataaaaacagaccgtgtctcagtaactggccagaggatacggatgtcctctacatcgtgtctcagttctttgtagaagagtggcggaaatttgttagaaagcctacaagatgcagccctgtgtcatcagttgggaacagtgctcttttgtgtccccacgggggcctcatgtttacatttgcttccatgaccaaagaagattctaaacttatagctctcatatggcccagtgagtggcaaatgatacaaaagctctttgttgtggatcatgtaattaaaatcacgagaattgaagtgggagatgtaaacccttcagaaacacagtatatttctgagcccaaactctgtccagaatgcagagaaggcttattgtgtcagcagcagagggacctgcgtgaatacactcaagccaccatctatgtccataaagttgtggataataaaaaggtgatgaaggattcggctccggaactgaatgtgagtagttctgaaacagaggaggacaaggaagaagctaaaccagatggagaaaaagatccagattttaatcaaatcatgcatgcattttcagttgctccttttgaccagaatttgtcaattgatggaaagattttaagtgatgactgtgccaccctaggcacccttggcgtcattcctgaatctgtcattttattgaaggctgatgaaccaattgcagattatgctgcaatggatgatgtcatgcaagtttgtatgccagaagaagggtttaaaggtactggtcttcttggacattaatctttgaatacttgctgactgctaagaaatgaccagaggggaagaggagtttgacatgttagggcattaaagcaaaggtggatttaagaattaaaccattacatgccccttccaaaaggcagaaatccattcaaacgtgactgtcccaaatgccttatgtcaaataaagcagattgcactgatggacatcagacttgaaggaaatgtttccaattttatatttaaggggggtggtgggtgggagggggcaagtaaagacggaacaagtttagtagcagtaatagtaaatcatgtttacatatgagatttatagtcgtgggaggggaataaagttctgttatatttccttgctcgagtttcataccagatgcgttggtccataaaggattgtatcaagtagatgggacaacattctgctctgaacgaaaagtaattttagagacataacctgcttaccaatgcctgtctttgattcatattctactttcaataaagcatgaaagtgaagaacttgtcctaagtgtggaaaagtgtcttcagatttagactcttctccatgtcagctgcagcgccacccgccttacacctgcccggccgtctgtctcttggtattgggtaaaggagggggcacctgcatgtctcctgcaatgagcaaggaattatgtctcatgttttgacttcagaggctttttgctttggtgcatttcagaaaggatggagaacatttattatgtgtgaaagcatcctcttccggttttgctgttattcaaaagtgggaaatgtacctggcacgtttgaaaataaaaaatctgactacctatcagaagagtaaatcagactgaagtacatttggataacacaaggtttctataaaatttgttcttcctgtcctccatgtcactgtttcttggacctcagttctctttttgaaagcattattccaaaatgccctgagagggtctcttagatcattgtttaaaaaaggaaaaaagtatatggatgtgctgtccatccaactcaggattatcattcttagcaacacgtaaccgaagcaatattcttaagaatattgaaggggtttttttaattgaacttaagactggagtttttcctttgaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("USP48");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 22050648,
				PositionType.ZERO_BASED), "C", "A");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(0, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("7G>T", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Val3Leu)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.MISSENSE_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc009vqi_1() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc009vqi.1	chr1	+	22778343	22857650	22816441	22852889	16	22778343,22816372,22817892,22832541,22834493,22835022,22835592,22837671,22838168,22839416,22843792,22846553,22847988,22848869,22850710,22852694,	22778497,22817138,22818026,22832734,22834630,22835224,22835726,22837840,22838627,22839623,22843957,22846768,22848151,22848956,22850937,22857650,	F8WAI8	uc009vqi.1");
		this.builderForward
				.setSequence("gatacccattgcgcgccggcctcaagatggccgccttctggcgtctccggcgctgttgaatggcgaaagctttattgttcccttcgggcaggagtgttcgtgtcctctatggcgctgtcaataaagaacggcagtttgaatcggtgctgaacagggcctgtcctcccaaagccaactctaaggagaggagaggaagagcagttcttggggcagagttgacgcaatggagctccccaactacagccggcagctgctgcagcagctgtacactctgtgcaaggagcagcagttctgtgattgcaccatctccattggtaccatttacttcagggctcacaagcttgtcctggctgctgccagcctcctgttcaaaaccctgctggataacacagataccatctccatcgatgcatctgtggtgagccccgaggagtttgcgctcttgttggaaatgatgtacacgggcaaactacctgtgggcaagcacaacttctccaaaatcatctccttagcagacagtctacagatgtttgatgtagctgttagctgcaaaaatcttctgaccagccttgtaaactgctcggttcagggtcaggtggtaagggatgtctctgcgccatcctcagagacattcagaaaggaaccagagaagcctcaagtagaaatcctttcatctgaaggtgctggagagcctcattcttccccagagcttgctgccactccagggggccctgtgaaagctgagactgaggaagcagcccattcagtttcacaagagatgagtgtgaattctcccacagcccaggagagccagaggaatgcagaaaccccagcggagactcctactacagctgaagcttgttccccctcccctgctgtgcaaacctttagtgaggcaaagaagacaagcacagaaccaggatgtgaaaggaaacactaccagctgaattttcttctagaaaatgaaggtgtcttctcagatgcactcatggttacccaggatgttttaaaaaaactagaaatgtgttcagaaattaaaggtccacagaaggaggtgattctgaattgctgtgagggcagaacacccaaggagacaatagaaaatttgttgcacagaatgactgaagagaagacgctgactgctgagggtttggtaaaactcctccaggctgtgaagacgactttcccaaacctgggccttctgctagagaagttgcagaaatcagccactttgccaagcaccacagtccaaccaagccctgatgattatgggactgagctattgagacgctatcatgaaaacctctctgagattttcacagacaaccagattttattaaagatgatctcacacatgacaagtttagcccctggagaaagagaggtcatggagaagcttgtgaaacgtgactctggttcaggtggtttcaattctctgatatcagcagttctagaaaagcagactctctctgccacagccatttggcaactgctgctggtggttcaggagacaaagacctgtccattggacctgctcatggaggaaatacgaagggagcctggtgccgatgctttcttccgggcagtgaccaccccagaacatgccactttagaaacaatcctgaggcataaccagttgatcttggaggccatccaacagaagattgagtacaagctctttacctcggaggaggagcacctggcagagactgtgaaagagattctgagcattccctctgagacagccagccctgaagcttccctgagagcagtgctgagcagagccatggaaaaatcagtcccggccattgaaatatgccacctcctgtgcagtgtccacaaatcttttccaggcctgcagcctgtcatgcaggagttggcatacattggtgtccttactaaggaagatggagagaaggaaacgtggaaggtgagtaataaatttcaccttgaagccaacaacaaagaagatgaaaaggcagccaaagaagacagccagcctggggaacagaatgatcaaggagagactggttccttgccaggacagcaagagaaagaggcttcagcctccccagaccctgccaagaagagcttcatctgtaaggcctgcgacaaaagcttccatttctactgccgcctaaaggtgcacatgaagcgctgccgggtggctaagagcaaacaggtgcagtgtaaggagtgcagtgagaccaaggattcaaagaaagagctggacaaacatcagctggaggcccatggtgcaggtggagagcccgatgcccccaagaagaagaagaagaggcttccagtgacatgtgacctctgtggcagagaatttgcccatgcctcaggcatgcagtaccataagctgacagagcacttcgatgagaagcctttctcctgtgaagagtgtggggcgaagtttgcagccaattccaccctgaagaaccaccttcgccttcacaccggggaccgcccgttcatgtgcaagcactgcctcatgaccttcacccaggcctccgccctggcctatcacaccaagaagaagcactcagaagggaaaatgtatgcatgccagtactgtgatgctgtgtttgcccagtctattgagctgtcccgccacgtgaggacccacaccggggacaagccctatgtctgcagagactgtggcaagggcttccggcaagccaatggcctctccatccatctgcacacctttcacaacatagaagatccttatgactgcaagaagtgcaggatgagtttccccactcttcaggatcaccggaagcacatccatgaggtgcactccaaagagtaccacccctgccccacgtgtgggaagatcttcagtgccccgtccatgctggagcggcacgtggtgacccacgttggagggaagcccttcagctgcgggatctgcaacaaggcctaccagcaattgtctggtttgtggtaccacaatcgaacccaccaccctgacgtatttgctgctcagaaccaccgatcttccaagttctcatcactccagtgcagctcctgtgacaaaaccttccccaacaccattgagcacaagaagcacatcaaagcagaacatgcagatatgaagttccatgaatgtgaccagtgtaaggagctcttccccacgccagccttgctgcaggttcatgtcaagtgccagcattcagggtcccagccattccggtgcttgtactgtgctgctactttccgttttcctggagcattgcagcaccatgtcaccacggagcacttcaagcagtcagagaccaccttcccctgtgagctctgtggggaactcttcacctcccaggcccagcttgacagtcacctggaatctgagcacccaaaggtgatgagcacggaaacccaggccgcagcctcacagatggcgcaggtgatccaaaccccagagccggtggccccgacagagcaggtgatcactttggaggagacccagcttgccgggtcgcaggtgtttgtgacgttgccagattctcaggcatctcaggccagctctgagctcgtggcggtgactgtggaggacttgctggatggcacagtgacgctgatctgtggtgaggccaaatgagcagcctttcatccggcagagccttcctgcgtttgcagcagagaggaggccccacagcttgccctttgccctccatccctggctgtcctgagtggtgagcatcttagcttagcaccaaccaacacagtctcacctagaaaacagatggaagcttcgttgttctcatagaaccaacagcatctgagccctcaacaccaacagcaccatcctctgtagcagacaggcctccctccccacaggcccgctgctgcggcctctatgacactgtccatccccaagtgacatgtggcttcagaagtagagtctgaaagagcagtgggatgggagctggtgaccagggtaccccaggaggcatgatgtgccgacagcgctcagtgggcagaatggcagttagatatgggacttggcccacagtgggggctgcaaatgctgccactgcctctggccatttaaagtgagaggggcaccaacagacaattcggggaccttaggccccttcctgaggcacacttggccccttcctcggtcctatcatcctaccctctgctggggttccccctccacccagtggccacgcccagcaccctattgatggctataggacaggtagccctcatttccatgcctgataaccccttgtcagttgtcagctcttcccaaaacaaagctctggagtttctgctggaagtgtttaatgtgaggcatcagctgctagacagtgtctgcctttccaggacattcttcttggtattccttacctgaagcctggttcccacagctcttccgtgtcatcttcatccccttcctttaatcaaaggctgaaatctctgcctgacctggcagcctggctccctctgggtcatatggtgcagactgtgaccagcaccaggcaggcagtcctgtctgcgtgtggaggagcagccgtgactgcccgtggctctgctgccgcccactccctgccttgtgagtggcctgctgcctcacctcccgcaggccgccacacttattgcaggtcagtgatcctttggagtttgaatttcacaaagctttttttatcttcagttcctaaaatataatctgataattaatggtttgtggaatccattatgaagtgcaattagatagatgtaggttcctgccgtttggagagaatgactagcatttatcttcttttccttccatgccaaaggttggagtctgctggtgcggtgtttacacaccaggcagggctgtctgccaccttgtgtggcttgaccccctgtggaggggagtgcctggttggatcccacactggcagagatggggccaccccttccttccaggcacatctcacattgccatgtacagaggcaggagtcacgtttggtcaccatggaagcttttattgctcccacatggtcaggtctcaccctgcttgaagcgcagaaggcacagccttactgaccagcacgcccactgactggcatctgccggctttgtgcaggcagccccgacggtttccacagcgagcgccagctccggccagcctgggacagctcatcccagccgactacacctgcttctggtcctgtccacattttgatacgcagatcacttgagcttgtcaattagggttctgccattctgaaataaatgaagtttctaaagcagagtcggcctcagaagccaaaaactgaccagaagatggtgctcaaacctttaagacttcatctccatgtgaagggctcactgtttctaccaaggctgtgcctgtattaaggcttttccgtcctgggaactgtcagtctgggagagctcttgatctgcaggtggcaaaatggcactgaatatccccttggcagcagagaaaacccactgaaagatcgtagagtggcacatgcttacagggcattggtgccaagtccagtggatgagaatccagcccctcacaagctgtgggatggggcttgggagttgcagtgaatgtcattaaaatttcttccaaaacaaaactagaaataattgctgagggcttatagggaagtgatttaaaaagaaaaaacaaacaacaacaaaaaaaactcttcggaataaagagggctgtaaattttgaattccagtgtcagatcctttcaagcactgagaaattctttctcaggtttctttttttgggggagacagggtcttgctctgtcacccagactggaacacagtggcacgatcttggctcactgcaacctctgcgggctcacgcaatcctcctgccacagcttcccaagtagctgggaccacaggcgtgagccaccactgctggccagtttttgtatttttttttttttttgtagagacagggtctcgccatgttgcccaggctggtttaaaactcctgagctcaagcagttctcccacctgggcctcccaaagtgctaggattacagacatgagccactacgcctggccaggtttctgttggagacccagattgtgacaagacatttgttttcttcggaatcaccagatttgcagcttactgtgccgagagtggactggctgccggggcccatcagatgccctctggcccatggcacactcagcagaaggcacaaacccagtgctggttctcatcaagaaagaggggaaggcccttcccgagtttactgtctgctctgagtgggctccgcactggctgactgattttatagtcttgctctctagagaagcccaggcatggatcttataggaaaactttctgactctgcttggccattttatccttttctcctacttctgcccaagagacctgaattgctgccatagaggacagtgtttgtgtggtctcctgagtccacatcgctcgcttccatggggtcccggtgttgtttttgcctcgttccccataggctgctgcccttatggcctctggactgaactctggggcctttggggtggtgtgaaggagtctgtgggcttcttggaacacatggatctgttcggtgggtccccagacctctgctcccagagctcatggcccaggtggtgaggagggaaaggcagtcagattccaggctggagtgtgattctgtgggaatactggggtcagttatggaacaggacttgcccatcataggtaagtgagacagcaaatagatgattcaagagcaaggattactgcgggaaggtgagactcctactgtccacgcgcatgagcagaacctggaaccagaggggcagggaccaggggtctttactcatttattttatgggtaaagagacatgaagagacagcctctctcttctgtctcagaagctctgtgtttgggaaactttgagcccagtgagtagcagggtctgcagtgtgagtaccaggtttccctggcaatccaggtctcctctgaggaagcattctgacttcccactgaccacggaaggcatgtcagcttcatgcctcgggctagagttctgataatcggggctgaggggtgaaaagaaatccagtcagacagacagtggggagacaggtccctgccctttatttgcgggatcaatcagggactcccagaaaggaaggagaatggtgagaagggccctaagagttcgtctctcacctgggggctggtgacgtggtcaccacaagctgaagacaggctaatggggtggcgggtgtgtgtttaaacctcacgtgcctggaagctgcacattgaccaaaggagggagggaagtgctaaccatgtatagagtgggcaggcggttccagggagacaagcagcatgttattaaattgggcctaggcagttggacgataatggagaaaaagcagggatgctataatgagtcctccccaagggtgagttcagcaccccagccctgttctgcttgtatcccagtgatacttgggaggtaggaagaaaatgggagtaagagaacaatttggggctgaagggagtgtcagaggcacgttgatccttgttttgttgtcatggaaacttcggggctggtgggacttaggccaaaagctcagaggcacagccaaaatttagaagcttgctactcctacgactcggcctataaggaagagagaagctgtctgtactttggggactacattgctgaaggaaaaaaatcactccctggctaattaagattgcttccaaattgggggaatgtgtgtcatttcctttaccaaggccagtcatccctgcttccacccatggtcaggacagtcagccactacgtgatgctgtataaattggattacaaaccatattcttgttcagcttgcactaatctatataaataaaatatgtactttgaaaaaaattaggctacatgagtttcaaatggactgtgatgttatagacctgctttctctttggttctgggccagtgtcagacggggacaggggtgataggcctggtgtcctaggggccatttgtgtaccttgaggccgtgttaacatggcctgggggaaagaaagctctcctgtcacttggagtctcattcctaaaccctccttcccagggagcaagtgtggggcagggtttcagagcacaggctttggtgtccagcctgggtacatccagctgtcccgctgtctaactgacattgtgtgagatgcttactctctctgagctccctcctcctggctcccaactttattataaaatggggaaaatgattgtgcttgccctacagaattgtagtatgaattaaaagtctggatttaatgtatttaatatagaaaattttagcttttattaataaaagtttttggcatacaagtc"
						.toUpperCase());
		this.builderForward.setGeneSymbol("ZBTB40");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 22846708,
				PositionType.ZERO_BASED), "G", "A");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(11, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("2653G>A", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Val885Met)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.MISSENSE_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc001bie_3() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001bie.3	chr1	-	24171571	24194859	24172204	24194776	8	24171571,24172563,24175138,24180849,24186287,24189623,24191980,24194387,	24172345,24172663,24175329,24181050,24186393,24189761,24192115,24194859,	P04066	uc001bie.3");
		this.builderForward
				.setSequence("cgggccaatcgttagtcagagtgggcggagccgcccgcgggcacctgcgcgttaagagtgggccgcgtcgctgaggggtagcgatgcgggctccggggatgaggtcgcggccggcgggtcccgcgctgttgctgctgctgctcttcctcggagcggccgagtcggtgcgtcgggcccagcctccgcgccgctacaccccagactggccgagcctggattctcggccgctgccggcctggttcgacgaagccaagttcggggtgttcatccactggggcgtgttctcggtgcccgcctggggcagcgagtggttctggtggcactggcagggcgaggggcggccgcagtaccagcgcttcatgcgcgacaactacccgcccggcttcagctacgccgacttcggaccgcagttcactgcgcgcttcttccacccggaggagtgggccgacctcttccaggccgcgggcgccaagtatgtagttttgacgacaaagcatcacgaaggcttcacaaactggccgagtcctgtgtcttggaactggaactccaaagacgtggggcctcatcgggatttggttggtgaattgggaacagctctccggaagaggaacatccgctatggactataccactcactcttagagtggttccatccactctatctacttgataagaaaaatggcttcaaaacacagcattttgtcagtgcaaaaacaatgccagagctgtacgaccttgttaacagctataaacctgatctgatctggtctgatggggagtgggaatgtcctgatacttactggaactccacaaattttctttcatggctctacaatgacagccctgtcaaggatgaggtggtagtaaatgaccgatggggtcagaactgttcctgtcaccatggaggatactataactgtgaagataaattcaagccacagagcttgccagatcacaagtgggagatgtgcaccagcattgacaagttttcctggggctatcgtcgtgacatggcattgtctgatgttacagaagaatctgaaatcatttcggaactggttcagacagtaagtttgggaggcaactatcttctgaacattggaccaactaaagatggactgattgttcccatcttccaagaaaggcttcttgctgttgggaaatggctgagcatcaatggggaggctatctatgcctccaaaccatggcgggtgcaatgggaaaagaacacaacatctgtatggtatacctcaaagggatcggctgtttatgccatttttctgcactggccagaaaatggagtcttaaaccttgaatcccccataactacctcaactacaaagataacaatgctgggaattcaaggagatctgaagtggtccacagatccagataaaggtctcttcatctctctaccccagttgccaccctctgctgtccccgcagagtttgcttggactataaagctgacaggagtgaagtaatcatttgagtgcaagaagaaagaggcgctgctcactgttttcctgcttcagtttttctcttatagtaccatcactataatcaacgaacttctcttctccacccagagatggcttttccaacacattttaattaaaggaactgagtacattaccctgatgtctaaatggaccaaagatctgagatccattgtgattatatctgtatcaggtcagcagaagaaggaactgagcagttgaactctgagttcatcaattgtaatatttggaaattatctacaatggaatcttccctctgttctctgataacctacttgcttactcaatgcctttaagccaagtcaccctgttgcctatgggaggaggtggaaggatttggcaagctcaaccacatgctatttagttagcatcagttgtcaccaacagtctttctgcaaagggcaggagagctttgggggaaaggaaaaggcttaccaggctgctatggtcaactcttcagaaattttcagagcaatctaaaagcgccaaaattcgctatgtttacagtgatactattaagaaaatgaatgtgattctgctctgtctttttaagtatgatcaaataaaaaatttgtacatcacaatcatttctaccaaaaaaaaaaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("FUCA1");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 24180961,
				PositionType.ZERO_BASED), "T", "C");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(4, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("857A>G", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Gln286Arg)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.MISSENSE_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc010oez_2() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc010oez.2	chr1	+	26517118	26529033	26517118	26529033	10	26517118,26517777,26520277,26524176,26524447,26524776,26526374,26527320,26527844,26528979,	26517331,26517921,26520379,26524274,26524568,26524910,26526549,26527532,26528010,26529033,	Q7RTX7	uc010oez.2");
		this.builderForward
				.setSequence("atgagggataatgaaaaggcctggtggcagcaatggacctcccatacaggcctcgaggggtggggcgggactcaggaggaccgtatggggtttggaggggcagtagctgcactgaggggccgcccctctcccctgcagagtaccattcacgagtcctacggtcggccagaggagcaagtgctcatcaaccgccaggaaatcacgaacaaagcggacgcctgggacatgcaggagttcatcactcacatgtacatcaagcagctgctccgacaccccgccttccaactgctgctggccctgctgctggtgatcaatgccatcaccatcgctctccgtaccaactcctacctggaccagaaacactatgagttgttctctaccatagatgacattgtgctgaccatccttctttgtgaggttctccttggctggctcaatggcttctggattttctggaaggacggctggaacatcctcaacttcattatcgtctttatcttgctcttgcggttcttcattaatgaaatcaatattccctccatcaactacactctcagggcgcttcgtctggtgcatgtgtgcatggcggtggagcccctcgcccggatcatccgcgtcatcctgcagtcggtgcctgacatggccaatatcatggtcctcatcctcttcttcatgctggttttttccgtgtttggagtaacactctttggtgcattcgtgcccaagcatttccagaacatacaggttgcgctgtacaccctcttcatctgcatcacccaggacggctgggtggacatctacagtgacttccagacagagaagagggaatatgcaatggagattgggggtgccatctactttaccatcttcatcaccatcggtgccttcattggcatcaacctgttcgtcatcgtggtgaccaccaacctggagcaaatgatgaaggcaggagagcagggacaacagcaacgaataacctttagtgagacaggcgcagaggaagaggaggagaatgaccagctgccactggtgcattgtgtggtcgcccgctcggagaaatctggtctcctccaggaaccccttgcgggaggccccctgtcgaacctctcagaaaacacgtgtgacaacttttgcttggtgcttgaggcaatacaggagaacctgaggcagtacaaggagatccgagatgaactcaacatgattgtggaggaggtgcgcgcaatccgcttcaaccaggagcaggagtcagaggtgttgaacaggcgctcgtcgacgagcgggtcgttggagactacgtcatccaaggacatccgccagatgtctcaacagcaagacttgctcagtgcgctcgttagcatggaaaaggttcatgactctagctcacaaatactccttaaaaaacacaagagcagccactga"
						.toUpperCase());
		this.builderForward.setGeneSymbol("CATSPER4");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 26517793,
				PositionType.ZERO_BASED), "A", "G");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(1, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("230A>G", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Gln77Arg)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.MISSENSE_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc001bwx_1() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001bwx.1	chr1	+	33547259	33586131	33549518	33583721	7	33547259,33557650,33558882,33560148,33562307,33563667,33583502,	33549728,33557823,33559017,33560314,33562470,33563780,33586131,	Q96A70	uc001bwx.1");
		this.builderForward
				.setSequence("ctcaggatcagtccagcccccatttacaattggggaaactgcggctccgaaagggtcagagggtacccgaggtcaagcagtagagagcggactcgaacttaagctcttgctcttaggctgagacgccttgatgtggccaccggctaccctctaggtgggcgtggtcaagactgggggcgccctggaaacttccccacccaagtttctcagattttctgttccatctctctctcactctttcctgaaatccagaatttcagaactgggaaggctctccaaaacaattcatccattttacagagggagcaactgactcggagaggcagtgacatttgggcatacggtgccttgtctccgtctgaaatccgggattcttgactttctccacccttgcccagtgaaccccgggcttgtgctgggggcgggttgtgacgggattgggatccctggcctcgggaggcgagtggggcagcagagccggccccccagcggttcccttcatctcccctcgccccgcagtgtgttgcatactttctaaggcggcggctgcagcagcggctccatccagcccgtcagctcctcctgcaaggcatggctggctacctgagtgaatcggactttgtgatggtggaggagggcttcagtacccgagacctgctgaaggaactcactctgggggcctcacaggccaccacggtgaggggctgggaatgggggtgggtccccggtcccctgtacagatcagctgcctcctcaggaagggtccctagggccctcatcttatcctttcgcttatttggtacgcaatccttgctatttgcaagagaggggctcaatttatccccaattctttgtccttgcccccaaacctgcccctcctatgttgcctttttacccagccctccaaggtcagaaacgggggattcctttcctgctgccccctctccctcaccgcccacatctaggcattaccaagctctgtgaattctgcctccttaaataagcctccagtgtgcttatgtggaacctcgctttctagttcagtcttcctctctccaaaggccccctgatctcccggtctcatgccaccccctccaaaggtatcatagtcctgttcaaaatctgaatttggcaattctccactgcctacaggatcaagtcagagcttccctgcctccatagccccattcccagctttccccacagtatatattatgctccttcggagctgaatgactcaatttgcatttcccaatgatgctgtgtgcccaatgatgctattaccatgcctttgcccaggctgttccttctgcctgcagcatccttctctcagcctctcccctctttacccctttctagtcattccttgcttctcatcagtgttgcctttcctgatcgccccaggcagattttctattttctttctccatgcccctgcattgcaccttgtagaaactgccatctaacatctgctgaatggacttgtaagtacttaaggggttccttttcttccagacccaagctccttcagaacaacagtactgtttggtcatctctgtcccttagcataacgtttggcaccaagtgggcatgagtaaaaaagtgttgcatgaaagggaaactgagtcccagggaagcaactctggaagggtcccttccacatttaagtaatgtggtctcacccacagcctcatacatgtttggcctttagcacatctacaatgaacagttgtccatagggataggttgcagtgtcagtgggactagcttaaggcttcattgagcacctgtgtttggaaggcagtgagctgtaggtcatagcttctgcctttagggagtttacccttttctagaatggacagatgcatatgcagttctgataccttggaagcatatgcaaatgcagtgggagcatttaaaagcagagttgccttattggtttggggaaggtggcatttgagtgaaactttaaggattttgccagacacaggagtggggagggtattctgcaggacatggtgggaacaaaggtttgaaggtgggaaaatgatccagcctacagctggagttttagccgcattgaggggagggattggggctagatcagagccttgaatgccaggctgaggagtttgccttctgccctgtaggcttggggaggcccagaaaacttagcagcagggtagctctgtggccagtactgaacctgggtcaggtacttggaaggatcacggatggcatgcacagggtgcgagctggatggggtctcacattcatccacttcttgtcattcaggacgaggtagctgccttcttcgtggctgacctgggtgccatagtgaggaagcacttttgctttctgaagtgcctgccacgagtccggcccttttatgctgtcaagtgcaacagcagcccaggtgtgctgaaggttctggcccagctggggctgggctttagctgtgccaacaaggcagagatggagttggtccagcatattggaatccctgccagtaagatcatctgcgccaacccctgtaagcaaattgcacagatcaaatatgctgccaagcatgggatccagctgctgagctttgacaatgagatggagctggcaaaggtggtaaagagccaccccagtgccaagatggttctgtgcattgctaccgatgactcccactccctgagctgcctgagcctaaagtttggagtgtcactgaaatcctgcagacacctgcttgaaaatgcgaagaagcaccatgtggaggtggtgggtgtgagttttcacattggcagtggctgtcctgaccctcaggcctatgctcagtccatcgcagacgcccggctcgtgtttgaaatgggcaccgagctgggtcacaagatgcacgttctggaccttggtggtggcttccctggcacagaaggggccaaagtgagatttgaagagattgcttccgtgatcaactcagccttggacctgtacttcccagagggctgtggcgtggacatctttgctgagctggggcgctactacgtgacctcggccttcactgtggcagtcagcatcattgccaagaaggaggttctgctagaccagcctggcagggaggaggaaaatggttccacctccaagaccatcgtgtaccaccttgatgagggcgtgtatgggatcttcaactcagtcctgtttgacaacatctgccctacccccatcctgcagaagaaaccatccacggagcagcccctgtacagcagcagcctgtggggcccggcggttgatggctgtgattgcgtggctgagggcctgtggctgccgcaactacacgtaggggactggctggtctttgacaacatgggcgcctacactgtgggcatgggttcccccttttgggggacccaggcctgccacatcacctatgccatgtcccgggtggcctggtaagagggccctgctggaaaatgggggtatggggaggaactgggcagaaacgagggaaacttgagatttgagattctgcttctgtgtaatccaacttgacaagtagctgttggctgctggtcccacgtgagggatgtgcttggcacctggctgtggccataagtgaggctgggctttcctagaggagctcacagccctgagggaggcacagagaagcagacaggcccttgtgataatactgtgcctggtggaagtgcagagccctccctgaggcccaggcctccctgaggaagtgggagtcaactacaggagtgggtggtaggacttcccagaagaaaggaacggagagcaagtgtggtgcattcaaggctcgcatggagagtggctgtgactgcagagggaggcagggctagatctgggaagggcttgaaggccgagtgacacgctttgacagcagtgggaggcatggaccagtttgaagcaggggcataacacagtctgatttgcactttggaaagaccaccctagttcctgtatagactgaggacaagggcagatgcagaaagagatgaccagttaggtggctgctgtgtgacacaggagagacttggtctgcctgcttgctgagccttggtctttccatctagaaaataagcgtaacagtagccctgcctgccccaccaggctagtgctgggatccagtgaggcaagaggcatgcaagatgggggagtcacatccagtgaaccctgacatcaggccttacgttaggtgtcgtatgactatttcacccacacaacactctgtgggggtcagcgttatcacagagaggtcattcttctgaggtcacgcagctagtcagtggtgaagggtccctcttttggggtagggagagggccacagaaagcaggagcagtttgcccaggcacattcaattcctctgctgtgtccagtctcaggactgtcaccaaagctgagacctgaaggctaaggaggaattagatgagagttgatgatacaggcagagagacaaggatggatccaggctggggagaggacatgtgtaagagcccaaaaggtggccatagtggatggctcaggtgtgtgggccagtgtgtccaggagtgggtggaagaggcgggtagagccagatcttgcagggcctgagagcagagggaaatggctggcagctcaagggcagggatttgttttttggaaagatgaatctgggtcctggctgagaactgtctggagagggcacgagcagatcagggagactgaaatggtgttacccatggggatggagaccatggggcacatgaggatgttcagaagctgggctcagcagggctcaactgggtgtgggaacaagattgccaactgtttacaactctgatagtggagccattgatcaacatgggtatgtaggaggagaagcagtttgtgggattgtttgagagaatgggatatgattttatttgaagtcccaagcctggggtatgatgaatgtgggctttcgagtgagagttgggttcagtcctagttctactacctactagggatgtgatcttaggtaggtcacttctctctctggggaactgattgattacatcatcaatgcaacggggatggtaatgcctccctcatggggtttttgggaagatacaaatagataatataagtgcctggcatgatgtctggcatgtagtaaatgctcgatacatgtggatttctttcccttgttgaggggtggggaccacacgggagtaggaagcagttggggaggggtcagcagcctgtccctgcccctgccctcagctgagctcgggctcacgtgagagccctctgcccaatggggctgcgtctccaggtacagggccctgccagtcccatgctggctacttgcagcacccctctctcacccctagggaagcgctgcgaaggcagctgatggctgcagaacaggaggatgacgtggagggtgtgtgcaagcctctgtcctgcggctgggagatcacagacaccctgtgcgtgggccctgtcttcaccccagcgagcatcatgtgagtgggcctcgttccccccggagaatcccagcggggcctcagagatgcatctgggagaggtggggaagatggcaggcaagggtacccttggccaggactctggtgcccaccctgccacccccgcgctccacctgcagtgtttctgccctgtaaataggaccagtcttacactcgctgtagttcaagtatgcaacataaatcctgttccttccagctgtgtctgcctcctctgcagtgcaaggggcctggtcagccaggtgtgggggtgttcttggggtctcctttggtctccttcccacctttgtaaatataatgcaaataaataaatatttaggtttttaaaaactgc"
						.toUpperCase());
		this.builderForward.setGeneSymbol("ADC");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 33549534,
				PositionType.ZERO_BASED), "A", "G");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(0, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("17A>G", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(His6Arg)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.MISSENSE_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc002rcc_2() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc002rcc.2	chr2	-	15307031	15701472	15307171	15701428	52	15307031,15319111,15326865,15330387,15358896,15372545,15374668,15378591,15415607,15416974,15427196,15432660,15448339,15449294,15467873,15468322,15470721,15492115,15493676,15496426,15506703,15514731,15519725,15523338,15534350,15536536,15542291,15555669,15557650,15564438,15567834,15601324,15601810,15607452,15607787,15608505,15613345,15614190,15615810,15618349,15629017,15644268,15651335,15674666,15676541,15679346,15691616,15693549,15694184,15696906,15698703,15701311,	15307447,15319240,15327004,15330527,15359092,15372635,15374871,15378810,15415942,15417225,15427307,15432890,15448477,15449371,15467994,15468436,15470889,15492205,15493834,15496540,15506817,15514844,15519955,15523441,15534473,15536599,15542425,15555843,15557836,15564592,15567918,15601461,15601915,15607531,15607928,15608657,15613471,15614448,15616004,15618413,15629146,15644337,15651474,15674765,15676675,15679480,15691660,15693597,15694262,15696943,15698758,15701472,	A2RRP1	uc002rcc.2");
		this.builderForward
				.setSequence("gattccgtagagacactcctccgctgcctgagtcctcggcgaacatggcggcccccgagtcagggccggctttgagtccaggcactgcagagggtgaggaggagacgattctctatgacttgttggtcaacaccgagtggccaccggagactgaagtacagcctagaggcaaccaaaaacatggtgcatcctttatcatcacgaaagcaattcgagatcgtttattatttttacgccaatacatctggtacagcccggcaccttttttgctccctgatggactggttcgcttggttaataaacagataaactggcatttggtacttgcaagcaatggaaagcttttggctgctgttcaagatcagtgtgtggaaatcaggtctgcaaaagatgattttacatccattattgggaaatgtcaagttccgaaagacccgaaaccccagtggagacgggtagcatggagttacgattgtaccctactggcctatgccgaaagcacaggaactgtgagggtgtttgatctcatgggaagtgaactctttgtcatttccccggcatctagttttataggtgacttaagctatgccattgctgggttgatatttttagaatataaagcaagtgcacagtggtctgcagaactcctggtcatcaattaccgaggagaacttagaagttaccttgtaagtgttggaacaaatcagagctaccaagaaagtcactgtttcagcttcagtagtcattatcctcatggaatcaacacagctatttaccaccctggtcacagacttttacttgttggtggatgtgaaactgctgaagtaggcatgtcaaaagcttctagctgtggcctttctgcctggagagttctttcaggatcaccgtattataagcaggttactaatggtggagacggggttactgcagtaccgaagacactgggattattaaggatgttaagtgtcaagttttacagtcgccagggacaagaacaggatggaatttttaagatgagcctttctccagatgggatgctcctggcagccattcacttctcagggaaactgagcatctgggcgattccatctctgaagcaacaaggggaatggggtcaaaatgagcagccaggctatgatgaccttaatcctgattggaggctctctactgagaagagaaaaaaaatcaaagataaagagtccttttacccactgatagatgtcaattggtgggcagacagtgcagtgactttagctcgatgctctggtgctttaactgtttcatctgtgaaaactttgaagaatttactgggaaaatcctgtgaatggtttgaaccatcacctcaagtcactgctacccatgatgggggatttttaagtttggagtgtgagattaaacttgcccccaaacgatctcgtttggagactagagctggagaagaagatgaaggagaagaggattctgattctgattatgaaatatctgccaaggctcgctactttggttatataaaacagggcctttacttggtgactgaaatggagcgatttgcaccaccacggaaacgcccacgaaccattactaaaaactaccgccttgtgagtttgcgctccacgacaccagaggaactttatcagaggaagattgaaagtgaagagtatgaggaagccttgtccttggctcatacctacggcctggatactgaccttgtatatcagaggcagtggaggaagtcagcggtcaacgttgcttcaattcagaattatttgagtaaaataaagaagcgatcctgggttctccatgagtgtttggaaagagttcctgaaaatgtggatgctgcaaaagaactgcttcagtatggattaaaaggcacagacctggaggctcttttagcaatagggaaaggagcagatgatggcagatttacattacctggtgaaatagacattgacagtatctcctatgaagagctttcaccacctgatgaagagcctgccaagaataaaaaggaaaaggagctcaagaagagacaagaactactgaaattagtgaacttttccaagttgacactggaacaaaaggaactttgccgttgtagacggaagttattaacctacttagatcgacttgcaacatatgaggaaatcctaggagtgcctcatgcatctgaacagagatatgatgctgaattctttaagaaattcagaaatcagaatattgttctctcagcaagaacttatgctcaggaaagtaatgtacaagccctggaaattctgtttacttaccatggttccgacctgcttcctcatcgccttgcaattctgtccaactttccagagaccacttctccacatgaatattctgttttgctgcccgaagcttgttttaacggtgactccctgatgatcattccttggcatgaacataaacaccgagctaaagattggtgcgaggagttggcttgcagaatggttgttgagccgaatctccaagatgaaagtgaattcttgtatgctgcacagcctgagttactaaggttcaggatgacccagcttacggtggagaaggttatggactggtatcagaccagagcagaggaaatagagcattatgctcggcaggtggactgtgcattgtcacttattcgacttgggatggagcggaatattcctggtttgctggttctctgtgacaatttggttactctggaaacattggtttatgaagccaggtgtgatgtaactctaaccctgaaagaactccagcagatgaaagacattgaaaaactaagattactgatgaatagttgttctgaggataaatatgtgacaagtgcctaccagtggatggttccctttcttcatcgttgtgagaaacagtcgcctggtgtggctaatgagctattaaaagaatatttagtaactttagctaaaggggacttaaaatttcccctgaagatatttcagcattccaaaccagatctgcagcaaaaaattattcctgatcaggaccaactgatggcaatagcactagagtgcatctatacctgtgaacgaaatgatcaactctgtctttgctatgacctactagaatgtctgccagaaagaggatatggtgataagacagaggcaaccacaaagcttcatgacatggtagaccaactggaacaaattctcagtgtgtcagagcttttggaaaaacatggactcgagaaaccaatttcatttgttaaaaacactcaatctagctcagaagaggcacgcaagctgatggttagattgacgaggcacactggccggaagcagcctcctgtcagtgagtctcattggagaacgttgctgcaagacatgttaactatgcagcagaatgtatacacatgtctagattctgatgcctgctatgagatatttacagaaagccttctgtgctctagtcgccttgaaaacatccacctggctggacagatgatgcactgcagtgcttgttcagaaaatcctccagctggtatagcccataaagggaaaccccactacagggtcagctacgaaaagagtattgacttggttttggctgccagcagagagtacttcaattcttctaccaacctcactgatagctgcatggatctagccaggtgctgcttacaactgataacagacagaccccctgccattcaagaggagctagatcttatccaagccgttggatgtcttgaagaatttggggtaaagatcctgcctttgcaagtgcgattgtgccctgatcggatcagtctcatcaaggagtgtatttcccagtcccccacatgctataaacaatccaccaagcttctgggccttgctgagctgctgagggttgcaggtgagaacccagaagaaaggcggggacaggttctaatccttttagtggagcaggcacttcgcttccatgactacaaagcagccagtatgcattgtcaggagctgatggccacaggttatcctaaaagttgggatgtttgtagccagttaggacaatcagaaggttaccaggacttggccactcgtcaagagctcatggcttttgctttgacacattgccctcctagcagcattgaacttcttttggcagctagcagctctctgcagacagaaattctttatcaaagagtgaatttccagatccatcatgaaggaggggaaaatatcagtgcttcaccattaactagtaaagcagtacaagaggatgaagtaggtgttccaggtagcaattcagctgacctattgcgctggaccactgctaccaccatgaaagtcctttccaacaccacaaccaccaccaaagcggtgctgcaggccgtcagtgatgggcagtggtggaagaagtctttaacttaccttcgaccccttcaggggcaaaaatgtggtggtgcatatcaaatcggaactacagccaatgaagatctagagaaacaagggtgtcatcctttttatgaatctgtcatctcaaatccttttgtcgctgagtctgaagggacctatgacacctatcagcatgttccagtggaaagctttgcagaagtattgctgagaactggaaaattggcagaggctaaaaataaaggagaagtatttccaacaactgaagttctcttgcaactagcaagtgaagccttgccaaatgacatgaccttggctcttgcttaccttcttgccttaccacaagtgttagatgctaaccggtgctttgaaaagcagtccccctctgcattatctctccagctggcagcgtattactatagcctccagatctatgcccgattggccccatgtttcagggacaagtgccatcctctttacagggctgatcccaaagaactaatcaagatggtcaccaggcatgtgactcgacatgagcacgaagcctggcctgaagaccttatttcactgaccaagcagttacactgctacaatgaacgtctcctggatttcactcaggcgcagatccttcagggccttcggaagggtgtggacgtgcagcggtttactgcagatgaccagtataaaagggaaactatccttggtctggcagaaactctagaggaaagcgtctacagcattgctatttctctggcacaacgttacagtgtctcccgctgggaagtttttatgacccatttggagttcctcttcacggacagtggtttgtccacactagaaattgaaaatagagcccaagaccttcatctctttgagactttgaagactgatccagaagcctttcaccagcacatggtcaagtatatttaccctactattggtggctttgatcacgaaaggctgcagtattatttcactcttctggaaaactgtggctgtgcagatttggggaactgtgccattaaaccagaaacccacattcgactgctgaagaagtttaaggttgttgcatcaggtcttaattacaaaaagctgacagatgaaaacatgagtcctcttgaagcattggagccagttctttcaagtcaaaatatcttgtctatttccaaacttgttcccaaaatccctgaaaaggatggacagatgctttccccaagctctctgtacaccatctggttacagaagttgttctggactggagaccctcatctcattaaacaagtcccaggctcttcaccggagtggcttcatgcctatgatgtctgcatgaagtactttgatcgtctccacccaggtgacctcatcactgtggtagatgcagttacattttctccaaaagctgtgaccaagctgtctgtggaagcccgtaaagagatgactagaaaggctattaagacagtcaaacattttattgagaagccaaggaaaagaaactcagaagacgaagctcaagaagctaaggattctaaagttacctatgcagatactttgaatcatctggagaaatcacttgcccacctggaaaccctgagccacagcttcatcctttctctgaagaatagtgagcaggaaacactgcaaaaatacagtcacctctatgatctgtcccgatcagaaaaagagaaacttcatgatgaagctgtggctatttgtttagatggtcagcctctagcaatgattcagcagctgctagaggtggcagttggccctcttgacatctcacccaaggatatagtgcagagtgcaatcatgaaaataatttctgcattgagtggtggcagtgctgaccttggtgggccaagggacccactgaaggtcctggaaggtgttgttgcagcagtccacgccagtgtggacaagggtgaggagctggtttcacctgaggacctgctggagtggctgcggcctttctgtgctgatgacgcctggccggtgcggccccgcattcacgtgctgcagattttggggcaatcatttcacctgactgaggaggacagcaagctcctcgtgttctttagaactgaagccattctcaaagcctcctggccccagagacaggtagacatagctgacattgagaatgaagagaaccgctactgtctattcatggaactcctggaatctagtcaccacgaggctgaatttcagcacttggttttacttttgcaagcttggccacctatgaaaagtgaatatgtcataaccaataatccatgggtgagactagctacagtgatgctaaccagatgtacgatggagaacaaggaaggattggggaatgaagttttgaaaatgtgtcgctctttgtataacaccaagcagatgctgcctgcagagggtgtgaaggagctgtgtctgctgctgcttaaccagtccctcctgcttccatctctgaaacttctcctcgagagccgagatgagcatctgcacgagatggcactggagcaaatcacggcagtcactacggtgaatgattccaattgtgaccaagaacttctttccctgctcctggatgccaagctgctggtgaagtgtgtctccactcccttctatccacgtattgttgaccacctcttggctagcctccagcaagggcgctgggatgcagaggagctgggcagacacctgcgggaggccggccatgaagccgaagccgggtctctccttctggccgtgagggggactcaccaggccttcagaaccttcagtacagccctccgcgcagcacagcactgggtgtgagggccacctgtggccctgctccttagcagaaaaagcatctggagttgaatgctgttcccagaagcaacatgtgtatctgccgattgttctccatggttccaacaaattgcaaataaaactgtatggaaacgatgagcaag"
						.toUpperCase());
		this.builderForward.setGeneSymbol("NBAS");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 2, 15674685,
				PositionType.ZERO_BASED), "T", "C");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(8, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("727A>G", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Ile243Val)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.MISSENSE_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc002rew_3() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc002rew.3	chr2	+	24299727	24392507	24362290	24390524	11	24299727,24303745,24318010,24357988,24362239,24369617,24369799,24384375,24387067,24390495,24392224,	24299839,24303825,24318099,24358057,24362320,24369705,24369956,24384483,24387204,24390552,24392507,	B5MD07	uc002rew.3");
		this.builderForward
				.setSequence("gtgtagcgggaggtggtggggcctgaggagggtggcgagaggtggggtggggcccaggtgagtagcgggcgaaggcggggcctgaggaggactgagctggggcgggacccagcagctgctccaagcttttctgccatttgaagcttcttctgggagccagctgtgaccagaggaatagctccagccatccggttaaaagtttaacaaccggaacagctccagtctgcagctcccagcgtgatcgacgcagaagacaggtgatttctgcatttccgactgaggttttagctaaagaagatactgaggcagctattcaatcaatattatacaaagaaaattctgtaattaagggaaatgcatttatagaacattatgatccaaaagagtatgatcccttttatatgagcaagaaggaccccaattttctgaaggttaccatcccaccatttcatgaccctttgaaaaaagcacaatatgacaaggataacgaaaaaagaactcttcttcagtgtgagactggcaaaatatattcaataaaggaattcaaagaagttgagaaggttcagctgcattccagattcccacaaatttctaattcaaggcactttataactccaaacgagtggctgaaactgcctacaagatacatagaaagtgaattttgtagaaggagaaggttaaaggtgaaagtgaattttaatgactgtagttttgatttgaaacctttggcaagagctccttatcttttggaatcccaggaagaagaaaaaacagttatttacaaaaacaaagggtcatcctttctagaaagagaaccgctgtgctatcaggagggaaataatccaagtgccaaagaggccatctctgaagggtatttttcttccttgagcctcagcaggaacgggaggaagaccaggatgggtctccctccccgcgtttggggctgctgaagctggagctataagaaagaagagggaggtttcagcaaccaaggagcacacaccctgatccttgattggtgaaaggatacacaaaatcaggctgcttcagaggaagtcatctgctcacaggaatcagggtgaaggccaaggacggcactcaagaattgtcctgtctctaaaaaaaaagcatctgcaacgaacttcctttaatttttgagttcagtgtctgtgataattaagaaatggcaataccatgtattttccccagaagttaagaaatattgctgaacgaaccaataaaaataattacagctccctca"
						.toUpperCase());
		this.builderForward.setGeneSymbol("FAM228B");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 2, 24390516,
				PositionType.ZERO_BASED), "G", "A");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(9, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("542G>A", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Gly181Asp)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.MISSENSE_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc010eyq_2() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc010eyq.2	chr2	+	27301434	27306017	27301933	27306017	5	27301434,27303018,27303599,27304950,27305906,	27302103,27303138,27303820,27305849,27306017,	NP_008977	uc010eyq.2");
		this.builderForward
				.setSequence("gggaggggagccagcagggaggaggaggccagggcccgccccacagccactctcgcgcctccgaacagccacaggggcaaagccctgtcacccccaggatccggtcatcagggaaagaggacagggagaccagaagagggccagctgggacgagggggcggacgcccaggaggcaacttctgagacgcagctcctgagaggggcagggaccaggcgcgggaggccagagggggcacagagaacaaaccccctcagaagtgaagaggagagcggaaggaaccgagaggggacggacaggagctgaggaggaaagaggaggggagaggggtcaggccaggcagccaaggagaagacgtgtggccgggggctatcagaaggaaactgggacggacgggccgggctcgggctgtcctgtggagcagcagcatccccggggccggcagaggcgccagtggctgggcgggatgagtctctgagggccactgtggagcgccccgccatggccccccgcaccctctggagctgctacctctgctgcctgctgacggcagctgcaggggccgccagctaccctcctcgaggtttcagcctctacacaggttccagtggggccctcagccccggggggccccaggcccagattgccccccggccagccagccgccacaggaactggtgtgcctacgtggtgacccggacagtgagctgtgtccttgaggatggagtggagacatatgtcaagtaccagccttgtgcctggggccagccccagtgtccccaaagcatcatgtaccgccgcttcctccgccctcgctaccgtgtggcctacaagacagtgaccgacatggagtggaggtgctgtcagggttatgggggcgatgactgtgctgagagtcccgctccagcgctggggcctgcgtcttccacaccacggcccctggcccagcctgcccgccccaacctctctggctccagtgcaggcagccccctcagtggactggggggagaaggtcctggggagtcagagaaggtgcagcagctggaggaacaggtgcagagcctgaccaaggagctgcaaggcctgcggggcgtcctgcaaggactgagcgggcgcctggcagaggatgtgcagagggctgtggagacggccttcaacgggaggcagcagccagctgacgcggctgcccgccctggggtgcatgaaaccctcaatgagatccagcaccagctgcagctcctggacacccgcgtctccacccacgaccaggagctgggtcacctcaacaaccatcatggcggcagcagcagcagtgggggcagcagggccccagccccagcctcagcccctccgggccccagtgaggagctgctgcggcagctggagcagcggttgcaggagtcctgctccgtgtgcctggccgggctagatggcttccgccggcagcagcaggaggacagggagcggctgcgagcgatggagaagctgctggcctcggtggaggagcggcaacggcacctcgcagggctggcggtgggccgcaggccccctcaggaatgctgctctccagagctgggccggcgactggcagagctggagcgcaggctggatgtcgtggccggctcagtgacagtgctgagtgggcggcgaggcacagagctgggaggagccgcggggcagggaggccaccccccaggctacaccagcttggcctcccgcctgtctcgcctggaggaccgcttcaactccaccctgggcccttcggaggagcaggaggagagctggcctggggctcctggggggctgagccactggctgcctgctgcccggggccgactagagcagttgggggggctgctggccaatgtgagcggggagctgggggggcggttggatctgttggaggagcaggactctcaagtcagcgagatcctcagtgccttggagcgcagggtgctggacagtgaggggcagctgcggctggtgggctccggcctgcacacggtggaagcagcgggggag"
						.toUpperCase());
		this.builderForward.setGeneSymbol("EMILIN1");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 2, 27303754,
				PositionType.ZERO_BASED), "A", "G");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(2, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("446A>G", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Gln149Arg)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.MISSENSE_VARIANT), annotation1.getEffects());
	}

	//
	// Various Synonymous Variants
	//

	@Test
	public void testRealWorldCase_uc011mzv_2_synonymous() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc011mzv.2	chrX	-	154006958	154033802	154007451	154028031	13	154006958,154009513,154009874,154011701,154012302,154013325,154014478,154018228,154019257,154020043,154020416,154028019,154033546,	154007628,154009588,154010077,154011782,154012383,154013432,154014675,154018297,154019343,154020122,154020560,154028168,154033802,	Q00013-2	uc011mzv.2");
		this.builderForward
				.setSequence("agctccgtccgcgccctcccggcgcaccgcctgcggggcggtgactggcccagccgcaccgcgtctcccgccttctccgcagccccgcaggccccgggccctgtcattcccagcgctgccctgtcttgcgttccagtgttccagcttctgcgagatgaccctcaaggcgagcgagggcgagagtgggggcagcatgcacacggcgctctccgacctctacctggagcatttgctgcagaagcgtagtcggccagagcttcctttgagcaactgcctctgccctttgaaccctgctgcagatgtttcctctgaaggggctttcagagcggcaggcattgccactccagcactttgtctgattaaaggccagaggctgcagaacaaaggccagacatggagtcctgggctgtatcgcatccattgaatactgtgaccgaggacatgtacaccaacgggtctcctgccccaggtagccctgcccaggtcaagggacaggaggtgcggaaagtgcgactcatacagtttgagaaggtcacagaagagcccatgggaatcacgctgaagctgaatgaaaaacagtcctgtacggtggccagaattcttcatggtggcatgatccatagacaaggctcccttcacgtgggggatgagatcctagaaatcaatggcacaaatgtgacaaatcattcagtggatcagctgcagaaggcgatgaaagaaaccaaaggaatgatctcattaaaagtaattcccaaccagcaaagccgtcttcctgcactacagatgttcatgagagcgcagtttgactatgatcccaaaaaggacaatctgatcccttgcaaggaggcgggactgaagtttgctactggggacattatccagattatcaacaaggatgacagcaattggtggcagggacgggtggaaggctcctccaaggagtcagcaggattgatcccttcccctgagctgcaggaatggcgagtggcaagtatggctcagtcagctcctagcgaagccccgagctgcagtccctttgggaagaagaagaagtacaaagacaaatatctggccaagcacagctcgatttttgatcagttggatgttgtttcctacgaggaagtcgttcggctccctgcattcaagaggaagaccctggtgctgatcggagccagtggggtgggtcgcagccacattaagaatgccctgctcagccagaatccggagaagtttgtgtaccctgtcccatatacaacacggccgccaaggaagagtgaggaagatgggaaggagtaccactttatctcaacggaggagatgacgaggaacatctctgccaatgagttcttggagtttggcagctaccaaggcaacatgtttggcaccaaatttgaaacagtgcaccagatccataagcagaacaagattgccatccttgacattgagccccagaccctgaaaattgttcggacagcagaactttcgcctttcattgtgttcattgcacctactgaccagggcactcagacagaagccctgcagcagctgcagaaggactctgaggccatccgcagccagtacgctcactactttgacctctcactggtcaataatggtgttgatgaaacccttaagaaattacaagaagccttcgaccaagcgtgcagttctccacagtgggtgcctgtctcctgggtttactaagcttgtagaatgggggaacccactgtatgcccctctccagcatttggaattccacccgccttgctttaagacaaacagggctgctccaactagttttgtgtcagcttccagctctctgcagctatcctaattcagccagtaaggttcagtcttcttgctcaggctcctgaagggttgattctcctgatagatggggccccactgatctggatttgaaaaggatttctagaaattgggggtaagaagtactaccaaaatgtaactgctaatcaagggtgatgcacagcaaaagcaatggaccccatccctctaaagcctgccctcctttgccttcaactgtatatgctgggtatttcatttgtctttttattttggagaaagcgtttttaactgcaactttctataatgccaaaatgacacatctgtgcaatagaatgatgtctgctctagggaaaccttcaaaagcaataaaaatgctgtgttgaaatgccaaaaaaaaaaaaaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("MPP1");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, refDict.getContigNameToID()
				.get("X"), 154009587, PositionType.ZERO_BASED), "T", "A");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(11, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("1060A>T", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Thr354Ser)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.MISSENSE_VARIANT, VariantEffect.SPLICE_REGION_VARIANT),
				annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc001aya_2() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001aya.2	chr1	-	16450831	16482582	16451709	16482427	17	16450831,16455928,16456720,16458215,16458558,16458872,16459674,16459975,16460354,16460962,16461530,16462149,16464347,16464769,16474872,16477390,16482342,	16451815,16456084,16456914,16458365,16458768,16458934,16459863,16460101,16460410,16461062,16461684,16462265,16464680,16464925,16475542,16477458,16482582,	P29317	uc001aya.2");
		this.builderForward
				.setSequence("ggttctcacccaacttccattaaggactcggggcaggaggggcagaagttgcgcgcaggccggcgggcgggagcggacaccgaggccggcgtgcaggcgtgcgggtgtgcgggagccgggctcggggggatcggaccgagagcgagaagcgcggcatggagctccaggcagcccgcgcctgcttcgccctgctgtggggctgtgcgctggccgcggccgcggcggcgcagggcaaggaagtggtactgctggactttgctgcagctggaggggagctcggctggctcacacacccgtatggcaaagggtgggacctgatgcagaacatcatgaatgacatgccgatctacatgtactccgtgtgcaacgtgatgtctggcgaccaggacaactggctccgcaccaactgggtgtaccgaggagaggctgagcgtatcttcattgagctcaagtttactgtacgtgactgcaacagcttccctggtggcgccagctcctgcaaggagactttcaacctctactatgccgagtcggacctggactacggcaccaacttccagaagcgcctgttcaccaagattgacaccattgcgcccgatgagatcaccgtcagcagcgacttcgaggcacgccacgtgaagctgaacgtggaggagcgctccgtggggccgctcacccgcaaaggcttctacctggccttccaggatatcggtgcctgtgtggcgctgctctccgtccgtgtctactacaagaagtgccccgagctgctgcagggcctggcccacttccctgagaccatcgccggctctgatgcaccttccctggccactgtggccggcacctgtgtggaccatgccgtggtgccaccggggggtgaagagccccgtatgcactgtgcagtggatggcgagtggctggtgcccattgggcagtgcctgtgccaggcaggctacgagaaggtggaggatgcctgccaggcctgctcgcctggattttttaagtttgaggcatctgagagcccctgcttggagtgccctgagcacacgctgccatcccctgagggtgccacctcctgcgagtgtgaggaaggcttcttccgggcacctcaggacccagcgtcgatgccttgcacacgacccccctccgccccacactacctcacagccgtgggcatgggtgccaaggtggagctgcgctggacgccccctcaggacagcgggggccgcgaggacattgtctacagcgtcacctgcgaacagtgctggcccgagtctggggaatgcgggccgtgtgaggccagtgtgcgctactcggagcctcctcacggactgacccgcaccagtgtgacagtgagcgacctggagccccacatgaactacaccttcaccgtggaggcccgcaatggcgtctcaggcctggtaaccagccgcagcttccgtactgccagtgtcagcatcaaccagacagagccccccaaggtgaggctggagggccgcagcaccacctcgcttagcgtctcctggagcatccccccgccgcagcagagccgagtgtggaagtacgaggtcacttaccgcaagaagggagactccaacagctacaatgtgcgccgcaccgagggtttctccgtgaccctggacgacctggccccagacaccacctacctggtccaggtgcaggcactgacgcaggagggccagggggccggcagcaaggtgcacgaattccagacgctgtccccggagggatctggcaacttggcggtgattggcggcgtggctgtcggtgtggtcctgcttctggtgctggcaggagttggcttctttatccaccgcaggaggaagaaccagcgtgcccgccagtccccggaggacgtttacttctccaagtcagaacaactgaagcccctgaagacatacgtggacccccacacatatgaggaccccaaccaggctgtgttgaagttcactaccgagatccatccatcctgtgtcactcggcagaaggtgatcggagcaggagagtttggggaggtgtacaagggcatgctgaagacatcctcggggaagaaggaggtgccggtggccatcaagacgctgaaagccggctacacagagaagcagcgagtggacttcctcggcgaggccggcatcatgggccagttcagccaccacaacatcatccgcctagagggcgtcatctccaaatacaagcccatgatgatcatcactgagtacatggagaatggggccctggacaagttccttcgggagaaggatggcgagttcagcgtgctgcagctggtgggcatgctgcggggcatcgcagctggcatgaagtacctggccaacatgaactatgtgcaccgtgacctggctgcccgcaacatcctcgtcaacagcaacctggtctgcaaggtgtctgactttggcctgtcccgcgtgctggaggacgaccccgaggccacctacaccaccagtggcggcaagatccccatccgctggaccgccccggaggccatttcctaccggaagttcacctctgccagcgacgtgtggagctttggcattgtcatgtgggaggtgatgacctatggcgagcggccctactgggagttgtccaaccacgaggtgatgaaagccatcaatgatggcttccggctccccacacccatggactgcccctccgccatctaccagctcatgatgcagtgctggcagcaggagcgtgcccgccgccccaagttcgctgacatcgtcagcatcctggacaagctcattcgtgcccctgactccctcaagaccctggctgactttgacccccgcgtgtctatccggctccccagcacgagcggctcggagggggtgcccttccgcacggtgtccgagtggctggagtccatcaagatgcagcagtatacggagcacttcatggcggccggctacactgccatcgagaaggtggtgcagatgaccaacgacgacatcaagaggattggggtgcggctgcccggccaccagaagcgcatcgcctacagcctgctgggactcaaggaccaggtgaacactgtggggatccccatctgagcctcgacagggcctggagccccatcggccaagaatacttgaagaaacagagtggcctccctgctgtgccatgctgggccactggggactttatttatttctagttctttcctccccctgcaacttccgctgaggggtctcggatgacaccctggcctgaactgaggagatgaccagggatgctgggctgggccctctttccctgcgagacgcacacagctgagcacttagcaggcaccgccacgtcccagcatccctggagcaggagccccgccacagccttcggacagacatatgggatattcccaagccgaccttccctccgccttctcccacatgaggccatctcaggagatggagggcttggcccagcgccaagtaaacagggtacctcaagccccatttcctcacactaagagggcagactgtgaacttgactgggtgagacccaaagcggtccctgtccctctagtgccttctttagaccctcgggccccatcctcatccctgactggccaaacccttgctttcctgggcctttgcaagatgcttggttgtgttgaggtttttaaatatatattttgtactttgtggagagaatgtgtgtgtgtggcagggggccccgccagggctggggacagagggtgtcaaacattcgtgagctggggactcagggaccggtgctgcaggagtgtcctgcccatgccccagtcggccccatctctcatccttttggataagtttctattctgtcagtgttaaagattttgttttgttggacatttttttcgaatcttaatttattattttttttatatttattgttagaaaatgacttatttctgctctggaataaagttgcagatgattcaaaccgaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("GENE_SYMBOL");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 16475122,
				PositionType.ZERO_BASED), "C", "T");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(2, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("573G>A", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.SYNONYMOUS_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc001bbk_1() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001bbk.1	chr1	-	19447682	19478353	19447683	19478311	21	19447682,19448427,19449326,19451034,19452939,19454116,19454704,19455471,19464500,19465576,19467840,19468140,19470473,19471279,19472286,19472516,19473319,19474486,19475056,19477070,19478136,	19447928,19448506,19449554,19451184,19453149,19454233,19454812,19455568,19464675,19465714,19468018,19468271,19470585,19471401,19472427,19472600,19473494,19474621,19475120,19477287,19478353,	uc001bbk.1");
		this.builderForward
				.setSequence("cccggaggcttcaccattgagattagtaacaacaatagcactatggtgatgacaggcatgcggatccagattgggactcaagcaatagaacgggccccgtcatatatcgagatcttcggcagaactatgcagctcaacctgagtcgctcacgctggtttgacttccccttcaccagagaagaagccctgcaggctgataagaagctgaacctcttcattggggcctcggtggatccagcaggtgtcaccatgatagatgctgtaaaaatttatggcaagactaaggagcagtttggctggcctgatgagcccccagaagaattcccttctgcctctgtcagcaacatctgcccttcaaatctgaaccagagcaacggcactggagatagcgactcagctgcccccactacgaccagtggaactgtcctggagaggctggttgtgagttctttagaagccctggaaagctgctttgccgttggcccaatcatcgagaaggagagaaacaagaatgctgctcaggagctggccactttgctgttgtccctgccagcacctgccagtgtccagcagcagtccaagagccttctggccagcctgcacaccagccgctcggcctaccacagccacaaggatcaggccttgctgagcaaagctgtgcagtgtctcaacacatctagcaaagagggcaaggatttggaccctgaggtgttccagaggctagtgatcacagctcgctccattgccatcatgcgccccaacaaccttgtccactttacggagtcaaagctgccccagatggaaacagactgtttttttcctagatgtgcctgctggagtctagggatagttggcatattgattggggccccacttgaaactccctccccagaaggaatggatgaagggaaggaaccgcagaagcagttggaaggagattgctgtagtttcatcacccagcttgtgaaccacttctggaaactccatgcatccaaacccaagaatgccttcttggcacctgcctgccttccaggactaactcatattgaagctactgtcaatgctctggtggacatcatccatggctactgtacctgtgagctggattgtattaacacagcatccaagatctacatgcagatgctcttgtgtcctgatcctgctgtgagcttctcttgtaaacaagctctaattcgagtcctaaggcccaggaacaaacggagacatgtgactttaccctcttcccctcgaagcaacactccaatgggagacaaggatgatgatgacgatgatgatgcagatgagaaaatgcagtcatcagggatcccgaatggtggtcacatccgtcaggaaagccaggaacagagtgaggtggaccatggagattttgagatggtgtctgagtcgatggtcctggagacagctgaaaatgtcaacaatggcaacccctctcccctggaggccctgctggcaggcgcagagggcttcccccccatgctggacatcccacctgatgcagatgacgagaccatggttgaactagccattgccctgagcctgcagcaggaccaacaagctccagcctcagacgacgagggcagtacagcagcgacagatggttctacccttcggacctctcctgctgaccacggtggtagtgtgggctcggagagcgggggcagtgcagtggactcagtggctggcgagcacagtgtatctggccggagcagtgcttatggcgatgctacagctgaggggcatccggctggaccaggaagtgtcagctcaagcactggagccatcagcaccaccactgggcaccaggagggagatggctccgagggagaaggagaaggagaaactgaaggagatgtccacactagcaacaggctgcacatggtccgtctaatgctgttggagagattactgcagaccctgcctcaattacgaaacgttggcggtgtccgggccatcccatacatgcaggtcattctaatgctcactacagatctggatggagaagatgagaaagacaagggggccctagacaacctgctctcccagcttattgctgagttgggtatggataaaaaggatgtctccaagaagaatgagcgcagcgccctgaatgaagtccatctggtagtaatgagactcctgagtgtcttcatgtcccgcaccaaatctggatccaagtcttccatatgtgagtcatcttccctcatctccagtgccacagcagcagctctactgagctctggggctgtggactactgcctgcacgtgctcaaatcactgctggaatattggaagagccaacagaatgacgaggagcctgtggctaccagccagttgctgaaaccacatactacctcctccccacctgacatgagcccattctttctccgccagtatgtgaagggtcatgctgctgatgtgtttgaggcctatactcagcttctaacagaaatggtactgaggcttccttaccaaatcaaaaagattactgacaccaattctcgaatcccacctcctgtctttgaccactcgtggttttactttctctccgagtacctcatgatccagcagactccatttgtgcgccgtcaagtccgcaaacttctgctcttcatctgtggatccaaagagaagtaccgccagctccgggatttgcacaccctggactctcacgtgcgtgggatcaagaagctgctagaagagcaggggatattcctccgggcaagtgtggttacagccagctcaggctccgccttgcaatatgacacactcatcagcctgatggagcacctgaaagcctgtgcagagattgccgcccagcgaaccatcaactggcagaaattctgcatcaaagatgactccgtcctgtacttcctcctccaagtcagtttccttgtggatgagggcgtgtccccagtgctgctgcaactgctctcctgtgctctgtgcggcagcaaggtgctcgctgcactggcagcctcttcgggatcctccagtgcttcttcctcctcagcccctgtggctgccagttctggacaagccacaacacagtccaagtcttccactaaaaagagcaagaaagaagaaaaagaaaaggagaaagatg"
						.toUpperCase());
		this.builderForward.setGeneSymbol("UBR4");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 19447842,
				PositionType.ZERO_BASED), "C", "G");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(20, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("2922G>C", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.SYNONYMOUS_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc001bxq_3() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(refDict,
						"uc001bxq.3	chr1	+	34329679	34330392	34330014	34330353	2	34329679,34329863,	34329778,34330392,	Q8WW32	uc001bxq.3");
		this.builderForward
				.setSequence("agacagaaaaattcgctgcaagtacagcactttctagattgctcctggagtgtgggaacaacagtctctcctgtccacgttactgaatccagaaaaaagaaacaaattcaaggagcagcagccaaatacctatgttggctttaaagagttctctagaaagtgttcggaaaaatggagatccatctcaaagcatgaaaaggccaaatatgaagccctggccaaactcgacaaagcccgataccaggaagaaatgatgaattatgttggcaagaggaagaaacggagaaagcgggatccccaggaacccagacggcctccatcatccttcctactcttctgccaagaccactatgctcagctgaagagggagaacccgaactggtcggtggtgcaggtggccaaggccacagggaagatgtggtcaacagcgacagacctggagaagcacccttatgagcaaagagtggctctcctgagagctaagtacttcgaggaacttgaactctaccgtaaacaatgtaatgccaggaagaagtaccgaatgtcagctagaaaccggtgcagagggaaaagagtcaggcagagctgatggatccagtttgaaaaaacaaaatgccattcaaccgta"
						.toUpperCase());
		this.builderForward.setGeneSymbol("HMGB4");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 34329896,
				PositionType.ZERO_BASED), "T", "C");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		// XXX
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(1, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("-118T>C", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FIVE_PRIME_UTR_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc001cas_2() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001cas.2	chr1	-	36921361	36930040	36921388	36929876	8	36921361,36921787,36923523,36926292,36926864,36927688,36929406,36929746,	36921526,36921979,36923582,36926377,36926913,36927764,36929451,36930040,	P82914	uc001cas.2");
		this.builderForward
				.setSequence("gagtcacgccacctaatccattctctcggtcttcgtctgctccggtattgcaactgcctcgattggtcgatcctgggccagcatggcggcgcccatgtaacccggtccgtgccgcaaagcgaacggcggccgcggcgcgggccccgcgggggttagaggtcaccatgctgagggtcgcgtggaggacgctgagtttgattcggacccgggcagttacccaggtcctagtacccgggctgccgggcggtgggagcgccaagtttcctttcaaccagtggggcctgcagcctcgaagtctcctcctccaggccgcgcgcggatatgtcgtccggaaaccagcccagtctaggctggatgatgacccacctccctctacgctgctcaaagactaccagaatgtccctggaattgagaaggttgatgatgtcgtgaaaagactcttgtctttggaaatggccaacaagaaggagatgctaaaaatcaagcaagaacagtttatgaagaagattgttgcaaacccagaggacaccagatccctggaggctcgaattattgccttgtctgtcaagatccgcagttatgaagaacacttggagaaacatcgaaaggacaaagcccacaaacgctatctgctaatgagcattgaccagaggaaaaagatgctcaaaaacctccgtaacaccaactatgatgtctttgagaagatatgctgggggctgggaattgagtacaccttcccccctctgtattaccgaagagcccaccgccgattcgtgaccaagaaggctctgtgcattcgggttttccaggagactcaaaagctgaagaagcgaagaagagccttaaaggctgcagcagcagcccaaaaacaagcaaagcggaggaacccagacagccctgccaaagccataccaaagacactcaaagacagccaataaattctgttcaatcatttctttctgtct"
						.toUpperCase());
		this.builderForward.setGeneSymbol("MRPS15");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 36927732,
				PositionType.ZERO_BASED), "G", "A");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(2, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("207C>T", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.SYNONYMOUS_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc001dsh_1() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001dsh.1	chr1	-	100174258	100231349	100174453	100214324	17	100174258,100174753,100176389,100177660,100177973,100181141,100182965,100185089,100194048,100195205,100203641,100206348,100207734,100212849,100214128,100214452,100230852,	100174676,100174815,100176505,100177719,100178071,100181228,100183081,100185203,100194196,100195304,100203824,100206496,100207829,100212986,100214324,100214557,100231349,	Q6ZNA5-2	uc001dsh.1");
		this.builderForward
				.setSequence("atactgctgggattacaggcgcccgccaccacgcccggctaattttttgtatttttagtagagacggggtttcactgtgttggcaaggatggtctctatctcctgacctcgtgatatgcccgcctccgcctcccaaagtgctgggattacaggcttgagccaccgcgtctggcctatttatttattattttcgagacggagtgttgctcttgtggcccaggctggagtgcaacggcgggatttcggctcactgcaacctctgcctcccgggttcaagcaattctcctgcctcagcctcctgagtagctgggattacaggcaggcaccaccacacccggctaattttgtatttttagtagaaacggggtttctccatgttggtcagtctggtttcgaactcccagcgtcaggtcatctgcctgcctcggcctcccaaagtgctgggattacaggcgtgagccaccgcgcccagccacttctgtatttttaaaaaagtggtaaggaagtggaggattaaatgatttgcccaaagtctcacagtaatttgtagagctgagattgaaattcgggtgaaacttcacatatcacattctttttatcagatggcagtttctggatttactcttggtacctgcatacttctgttgcacattagttatgtggctaattatcccaatggaaaagtaacacagtcatgccatggaatgattcctgaacatggtcatagtccacagtctgttcctgttcatgacatttacgtgagtcagatgacattcaggccaggagatcagattgaagttactttgtcagggcatccatttaaaggctttctcctagaagcgcgtaatgctgaggatctgaatggccctcctattggctccttcacattgattgacagtgaagtgtcacaacttttgacctgtgaagatatacagggatcagcagtgagtcacagaagtgcatctaaaaaaacagaaattaaagtctactggaatgctccaagcagtgctccaaatcacacacagtttctagtcacagttgttgagaagtataaaatctactgggtgaagattcctggtcctataatttcacaaccaaatgcatttccttttacaacacctaaagctacagtagtacctttgccaacgttacctcccgtttcccacttaaccaaaccattcagtgcctcagattgtgggaacaagaagttctgtattaggagtcctttgaactgtgacccagagaaggaggcttcctgtgtcttcttgtccttcacaagagatgaccaatcggtgatggttgaaatgagcggccccagtaaaggctatttatcctttgcattgtctcatgatcagtggatgggtgatgatgatgcttatctgtgtattcatgaagatcagactgtgtacatccagccttcccatttaacggggcgaagtcaccctgtaatggactccagggatacccttgaggatatggcttggaggttggcggacggtgttatgcagtgttctttcagaagaaacattacccttcctggagttaagaatagatttgatctaaacacaagctattacatatttctagcagatggtgcagctaatgatggtcgaatttacaagcactctcagcaacctttgattacctatgaaaaatatgatgtgacagactctccaaagaacataggaggatcccattctgtactccttctgaaggttcatggtgccttaatgtttgtggcatggatgactactgttagcataggtgtactggttgcccggttcttcaagccagtttggtcaaaagctttcttgcttggtgaagcagcttggtttcaggtgcatcggatgctcatgttcaccacaactgtcctcacctgcattgcttttgttatgccgtttatatacaggggaggctggagtaggcatgcaggttaccacccatacctcggctgtatagtgatgactttggcagttcttcagcctcttctggcagtcttcaggccacctttacatgacccaagaaggcaaatgtttaactggactcattggagtatgggaacagctgctagaataatagcagtggcagcgatgttcctgggaatggatttaccaggactgaatcttcctgattcatggaaaacctatgcaatgaccggattcgtagcctggcatgttgggactgaggttgttctggagttgaaatattggatgatgacagaattcagatccttcagtcatttactgcagtggaaacagagggtcatgcttttaaaaaggcagtgttggcaatttatgtctgtgggaatgttacttttctcatcatatttttatctgcaatcaaccatctatgagcaagcaaagaccttggcttttgcaggccaagtgataattatcatcaaaccaaagaaacttgaagcctgtcctgactgcctggagcatatttgtgaattctcacttggaagactggggtcatgtctgtagaggaattctgaagtccagcctttagagaacaacattcaagagggtcatatagactataaattaatgtcatgccctatatgtaattctgggtcttaaaggaaagattgtacttcaggagaagtaactctcaaatatttcatgccaagattttaagaatgttggtatttaagaaaataaatagtgatttggaaaatc"
						.toUpperCase());
		this.builderForward.setGeneSymbol("FRRS1");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 100203692,
				PositionType.ZERO_BASED), "G", "A");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(6, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("708C>T", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.SYNONYMOUS_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc001dxa_4() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001dxa.4	chr1	+	109792640	109818378	109792701	109816671	34	109792640,109801053,109803663,109804134,109804429,109804908,109805427,109805763,109806232,109806699,109807075,109807489,109807812,109808356,109808726,109810169,109810472,109811202,109811501,109811731,109812045,109812315,109812554,109813047,109813548,109813838,109814007,109814221,109814899,109815261,109815454,109815787,109816057,109816643,	109796011,109801701,109803886,109804250,109804518,109805066,109805591,109805889,109806399,109806987,109807250,109807631,109807933,109808540,109808828,109810264,109810682,109811386,109811630,109811912,109812213,109812442,109812755,109813222,109813661,109813918,109814134,109814344,109815027,109815350,109815649,109815958,109816292,109818378,	Q9HCU4	uc001dxa.4");
		this.builderForward
				.setSequence("aggagccggaggaggagccgccgccgccgttgacccggccgccggccgggagctgggagagatgcggagcccggccaccggcgtccccctcccaacgccgccgccgccgctgctgctgctgttgctgctgctgctgccgccgccactattgggagaccaagtggggccctgtcgttccttggggtccaggggacgaggctcttcgggggcctgcgcccccatgggctggctctgtccatcctcagcgtcgaacctctggctctacaccagccgctgcagggatgcgggcactgagctgactggccacctggtaccccaccacgatggcctgagggtttggtgtccagaatccgaggcccatattcccctaccaccagctcctgaaggctgcccctggagctgtcgcctcctgggcattggaggccacctttccccacagggcaagctcacactgcccgaggagcacccgtgcttaaaggctccacggctcagatgccagtcctgcaagctggcacaggcccccgggctcagggcaggggaaaggtcaccagaagagtccctgggtgggcgtcggaaaaggaatgtaaatacagccccccagttccagccccccagctaccaggccacagtgccggagaaccagccagcaggcacccctgttgcatccctgagggccatcgacccggacgagggtgaggcaggtcgactggagtacaccatggatgccctctttgatagccgctccaaccagttcttctccctggacccagtcactggtgcagtaaccacagccgaggagctggatcgtgagaccaagagcacccacgtcttcagggtcacggcgcaggaccacggcatgccccgacgaagtgccctggctacactcaccatcttggttactgacaccaatgaccatgaccctgtgttcgagcagcaggagtacaaggagagcctcagggagaacctggaggttggctatgaggtgctcactgtcagggccacggatggtgatgcccctcccaatgccaatattctgtaccgcctgctggaggggtctgggggcagcccctctgaagtctttgagatcgaccctcgctctggggtgatccgaacccgtggccctgtggatcgggaagaggtggaatcctaccagctgacggtagaggcaagtgaccagggtcgggacccgggtcctcggagtaccacagccgctgttttcctttctgtggaggatgacaatgataatgccccccagtttagtgagaagcgctatgtggtccaggtgagggaggatgtgactccaggggccccagtactccgagtcacagcctcggatcgagacaaggggagcaatgccgtggtgcactatagcatcatgagtggcaatgctcggggacagttttatctggatgcccagactggagctctggatgtggtgagccctcttgactatgagacgaccaaggagtacaccctacgggtgcgagcacaggatggtggccgtcccccactctctaatgtctctggcttggtgacagtacaggtcctggatatcaacgacaatgcccccatcttcgtcagcacccctttccaggctactgtcctggagagtgtccccttaggctacctggttctccatgtccaggctatcgacgctgatgctggtgacaatgcccgcctggaataccgccttgctggggtgggacatgacttccccttcaccatcaacaatggcacaggctggatctctgtggctgctgaactggaccgggaggaagttgatttctacagctttggggtagaagctcgagaccatggcactccagcactcactgcctcggccagtgtcagcgtgactgtcctggatgtcaacgacaacaatccaacctttacccaaccagagtacacagtgcggctcaatgaggatgcagctgtgggcaccagcgtggtgacggtgtcagctgtggaccgtgatgctcatagtgtcatcacctaccagatcaccagtggcaatactcgaaaccgcttctccatcaccagccaaagtggtggtgggctggtatcccttgccctgccactggactacaaacttgagcggcagtatgtgttggctgttaccgcctccgatggcactcggcaggacacggcacagattgtggtgaatgtcaccgacgccaacacccatcgtcctgtctttcagagctcccactatacagtgaatgttaatgaggaccggccggcaggcaccacggtggtgctgatcagcgccacggatgaggacacaggtgagaatgcccgcatcacctacttcatggaggacagcatcccccagttccgcatcgatgcagacacgggggctgtcaccacccaggctgagctggactatgaagaccaagtgtcttacaccctggccattactgctcgggacaatggcattccccagaagtccgacaccacctacctggagatcctggtgaacgacgtgaatgacaatgcccctcagttcctgcgagactcctaccagggcagtgtctatgaggatgtgccacccttcactagcgtcctgcagatctcagccactgatcgtgattctggacttaatggcagggtcttctacaccttccaaggaggcgacgatggagacggtgactttattgttgagtccacgtcaggcatcgtgcgaacgctacggaggctggatcgagagaacgtggcccagtatgtcttgcgggcatatgcagtggacaaggggatgcccccagcccgcacacctatggaagtgacagtcactgtgttggatgtgaatgacaatccccctgtctttgagcaggatgagtttgatgtgtttgtggaagagaacagccccattgggctagccgtggcccgggtcacagccactgaccccgatgaaggcaccaatgcccagattatgtaccagattgtggagggcaacatccctgaggtctttcagctggacatcttctccggggagctgacagccctggtagacttagactacgaggaccggcctgagtacgtcctggtcatccaggccacgtcagctcctctggtgagccgggctacagtccacgtccgcctccttgaccgcaatgacaacccaccagtgctgggcaactttgagatccttttcaacaactatgtcaccaatcgctcaagcagcttccctgggggtgccattggccgagtacctgcccatgaccctgatatctcagatagtctgacttacagctttgagcggggaaatgaactcagcctggtcctgctcaatgcctccacgggtgagctgaagctaagccgcgcactggacaacaaccggcctctggaggccatcatgagcgtgctggtgtcagacggcgtacacagcgtgaccgcccagtgcgcgctgcgtgtgaccatcatcaccgatgagatgctcacccacagcatcacgctgcgcctggaggacatgtcacccgagcgcttcctgtcaccactgctaggcctcttcatccaggcggtggccgccacgctggccacgccaccggaccacgtggtggtcttcaacgtacagcgggacaccgacgcccccgggggccacatcctcaacgtgagcctgtcggtgggccagccgccagggcccgggggcgggccgcccttcctgccctctgaggacctgcaggagcgcctatacctcaaccgcagcctgctgacggccatctcggcacagcgcgtgctgcccttcgacgacaacatctgcctgcgggagccctgcgagaactacatgcgctgcgtgtcggtgctgcgcttcgactcctccgcgcccttcatcgcctcctcctccgtgctcttccggcccatccaccccgtcggagggctgcgctgccgctgcccgcccggcttcacgggtgactactgcgagaccgaggtggacctctgctactcgcggccctgtggcccccacgggcgctgccgcagccgcgagggcggctacacctgcctctgtcgtgatggctacacgggtgagcactgtgaggtgagtgctcgctcaggccgttgcaccccgggtgtctgcaagaatgggggcacctgtgtcaacctgctggtgggcggtttcaagtgcgattgcccatctggagacttcgagaagccctactgccaggtgaccacgcgcagcttccccgcccactccttcatcacctttcgcggcctgcgccagcgtttccacttcaccctggccctctcgtttgccacaaaggagcgcgacgggttgctgttgtacaatgggcgtttcaatgagaagcatgactttgtggccctcgaggtgatccaggagcaggtccagctcaccttctctgcaggggagtcaaccaccacggtgtccccattcgtgcccggaggagtcagtgatggccagtggcatacggtgcagctgaaatactacaataagccactgttgggtcagacagggctcccacagggcccatcagagcagaaggtggctgtggtgaccgtggatggctgtgacacaggagtggccttgcgcttcggatctgtcctgggcaactactcctgtgctgcccagggcacccagggtggcagcaagaagtctctggatctgacggggcccctgctactaggcggggtgcctgacctgcccgagagcttcccagtccgaatgcggcagttcgtgggctgcatgcggaacctgcaggtggacagccggcacatagacatggctgacttcattgccaacaatggcaccgtgcctggctgccctgccaagaagaacgtgtgtgacagcaacacttgccacaatgggggcacttgcgtgaaccagtgggacgcgttcagctgcgagtgccccctgggctttgggggcaagagctgcgcccaggaaatggccaatccacagcacttcctgggcagcagcctggtggcctggcatggcctctcgctgcccatctcccaaccctggtacctcagcctcatgttccgcacgcgccaggccgacggtgtcctgctgcaggccatcaccagggggcgcagcaccatcaccctacagctacgagagggccacgtgatgctgagcgtggagggcacagggcttcaggcctcctctctccgtctggagccaggccgggccaatgacggtgactggcaccatgcacagctggcactgggagccagcggggggcccggccatgccattctgtccttcgattatgggcagcagagagcagagggcaacctgggcccccggctgcatggtctgcacctgagcaacataacagtgggcggaatacctgggccagccggcggtgtggcccgtggctttcggggctgtttgcagggtgtgcgggtgagcgatacgccggagggggttaacagcctggatcccagccatggggagagcatcaacgtggagcaaggctgtagcctgcctgacccttgtgactcaaacccgtgtcctgctaacagctattgcagcaacgactgggacagctattcctgcagctgtgatccaggttactatggtgacaactgtactaatgtgtgtgacctgaacccgtgtgagcaccagtctgtgtgtacccgcaagcccagtgccccccatggctatacctgcgagtgtcccccaaattaccttgggccatactgtgagaccaggattgaccagccttgtccccgtggctggtggggacatcccacatgtggcccatgcaactgtgatgtcagcaaaggctttgacccagactgcaacaagacaagcggcgagtgccactgcaaggagaaccactaccggcccccaggcagccccacctgcctcttgtgtgactgctaccccacaggctccttgtccagagtctgtgaccctgaggatggccagtgtccatgcaagccaggtgtcatcgggcgtcagtgtgaccgctgtgacaacccttttgctgaggtcaccaccaatggctgtgaagtgaattatgacagctgcccacgagcgattgaggctgggatctggtggccccgtacccgcttcgggctgcctgctgctgctccctgtcccaaaggctcctttgggactgctgtgcgccactgtgatgagcacagggggtggctccccccaaacctcttcaactgcacgtccatcaccttctcagaactgaagggcttcgctgagcggctacagcggaatgagtcaggcctagactcagggcgctcccagcagctagccctgctcctgcgcaacgccacgcagcacacagctggctacttcggcagcgacgtcaaggtggcctaccagctggccacgcggctgctggcccacgagagcacccagcggggctttgggctgtctgccacacaggacgtgcacttcactgagaatctgctgcgggtgggcagcgccctcctggacacagccaacaagcggcactgggagctgatccagcagacagagggtggcaccgcctggctgctccagcactatgaggcctacgccagtgccctggcccagaacatgcggcacacctacctaagccccttcaccatcgtcacgcccaacattgtcatctccgtagtgcgcttggacaaagggaactttgctggggccaagctgccccgctacgaggccctgcgtggggagcagcccccggaccttgagacaacagtcattctgcctgagtctgtcttcagagagacgccccccgtggtcaggcccgcaggccccggagaggcccaggagccagaggagctggcacggcgacagcgacggcacccggagctgagccagggtgaggctgtggccagcgtcatcatctaccgcaccctggccgggctactgcctcataactatgaccctgacaagcgcagcttgagagtccccaaacgcccgatcatcaacacacccgtggtgagcatcagcgtccatgatgatgaggagcttctgccccgggccctggacaaacccgtcacggtgcagttccgcctgctggagacagaggagcggaccaagcccatctgtgtcttctggaaccattcaatcctggtcagtggcacaggtggctggtcggccagaggctgtgaagtcgtcttccgcaatgagagccacgtcagctgccagtgcaaccacatgacgagcttcgctgtgctcatggacgtttctcggcgggagaatggggagatcctgccactgaagacactgacatacgtggctctaggtgtcaccttggctgcccttctgctcaccttcttcttcctcactctcttgcgtatcctgcgctccaaccaacacggcatccgacgtaacctgacagctgccctgggcctggctcagctggtcttcctcctgggaatcaaccaggctgacctcccttttgcctgcacagtcattgccatcctgctgcacttcctgtacctctgcaccttttcctgggctctgctggaggccttgcacctgtaccgggcactcactgaggtgcgcgatgtcaacaccggccccatgcgcttctactacatgctgggctggggcgtgcctgccttcatcacagggctagccgtgggcctggaccccgagggctacgggaaccctgacttctgctggctctccatctatgacacgctcatctggagttttgctggcccggtggcctttgccgtctcgatgagtgtcttcctgtacatcctggcggcccgggcctcctgtgctgcccagcggcagggctttgagaagaaaggtcctgtctcgggcctgcagccctccttcgccgtcctcctgctgctgagcgccacgtggctgctggcactgctctctgtcaacagcgacaccctcctcttccactacctctttgctacctgcaattgcatccagggccccttcatcttcctctcctatgtggtgcttagcaaggaggtccggaaagcactcaagcttgcctgcagccgcaagcccagccctgaccctgctctgaccaccaagtccaccctgacctcgtcctacaactgccccagcccctacgcagatgggcggctgtaccagccctacggagactcggccggctctctgcacagcaccagtcgctcgggcaagagtcagcccagctacatccccttcttgctgagggaggagtccgcactgaaccctggccaagggccccctggcctgggggatccaggcagcctgttcctggaaggtcaagaccagcagcatgatcctgacacggactccgacagtgacctgtccttagaagacgaccagagtggctcctatgcctctacccactcatcagacagtgaggaggaagaagaggaggaggaagaggaggccgccttccctggagagcagggctgggatagcctgctggggcctggagcagagagactgcccctgcacagtactcccaaggatgggggcccagggcctggcaaggccccctggccaggagactttgggaccacagcaaaagagagtagtggcaacggggcccctgaggagcggctgcgggagaatggagatgccctgtctcgagaggggtccctaggcccccttccaggctcttctgcccagcctcacaaaggcatccttaagaagaagtgtctgcccaccatcagcgagaagagcagcctcctgcggctccccctggagcaatgcacagggtcttcccggggctcctccgctagtgagggcagccggggaggcccccctccccgcccaccgccccggcagagcctccaggagcagctgaacggggtcatgcccatcgccatgagcatcaaggcaggcacggtggatgaggactcgtcaggctccgaatttctcttctttaacttcctgcattaaccctgggccgtggttcctacgcccgaggctcccttcccttccccagccgcactcatgccctgctcctgtcttgtgctttatcctgccccgctccccatcgcctgcccgcagcagcgacgaaacgtccatctgaggagcctgggccttgccgggaggggtactcaccccacctaaggccatctagtgccaactccccccccaccattcccctcactgcactttggacccctggggccaacatctccaagacaaagtttttcagaaaagaggaaaaaaagaatttaaaaaaggatctccactcttcatgacttcagggattcattttttttatacgctggaaattgactcccctttcccttcccaaagaggataggacctcccaggatgcttcccagcctctcctcagtttcccatctgctgtgcctctgggaggagagggactcctggggggcctgcccctcatacgccatcaccaaaaggaaaggacaaagccacacgcagccagggcttcacacccttcaggctgcacccgggcaggcctcagaacggtgaggggccagggcaaagggtgtgcctcgtcctgcccgcactgcctctcccaggaactggaaaagccctgtccggtgagggggcagaaggactcagcgcccctggacccccaaatgctgcatgaacacattttcaggggagcctgtgcccccaggcgggggtcgggcagccccagcccctctccttttcctggactctggccgtgcgcggcagcccaggtgtttgctcagttgctgacccaaaagtgcttcatttttcgtgcccgccccgcgccccgggcaggccagtcatgtgttaagttgcgcttctttgctgtgatgtgggtgggggaggaagagtaaacacagtgctggctcggctgccctgagggtgctcaatcaagcacaggtttcaagtctgggttctggtgtccactcacccaccccaccccccaaaatcagacaaatgctactttgtctaacctgctgtggcctctgagacatgttctatttttaaccccttcttggaattggctctcttcttcaaaggaccaggtcctgttcctctttctccccgactccaccccagctccctgtgaagagagagttaatatatttgttttatttatttgctttttgtgttgggatgggttcgtgtccagtcccgggggtctgatatggccatcacaggctgggtgttcccagcagccctggcttgggggcttgacgcccttccccttgccccaggccatcatctccccacctctcctcccctctcctcagttttgccgactgcttttcatctgagtcaccatttactccaagcatgtattccagacttgtcactgactttccttctggagcaggtggctagaaaaagaggctgtgggcaggaaagaaaggctcctgtttctcatttgtgaggccagcctctggcttttctgccgtggattctcccccgtcttctcccctcagcaattcctgcaaagggttaaaaatttaactggtttttactactgatgacttgatttaaaaaaaatacaaagatgctggatgctaacttggtactaaccatcagattgtacagtttggttgttgctgtaaatagggtagcgttttgttgttgttgttttttcatgccccatactactgaataaactagttctgtgcgggtacagca"
						.toUpperCase());
		this.builderForward.setGeneSymbol("CELSR2");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 109794251,
				PositionType.ZERO_BASED), "T", "C");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(0, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("1551T>C", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.SYNONYMOUS_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc001ebt_3() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001ebt.3	chr1	+	112303018	112310199	112305493	112309521	3	112303018,112303327,112308358,	112303210,112305629,112310199,	E9PJ60	uc001ebt.3");
		this.builderForward
				.setSequence("agtagagtaacaatattggctgagatttacacaaatttggtacatcataaatatttcaattttttttttttaattaggcagaattaagcaactcatagaacttgactacttgaacccaggcagtatacgcctctttattcttgatgaagcagataagcttttagaagaaggcagcttccaggagcaaataaattggatttattcttccttgcctgccagtaaacagatgctggcagtatcagctacttatcccgaatttttggctaatgctttgacaaagtacatgagagatcccacttttgtaagactgaattccagtgatccaagtctcataggtgtgtacagcttttaggtacttatattattggtttatttgtgatatgctggagctttcccttgcctagttttgtcttgctgtttcatggttaccattagcatatatatttgttttaactgatagctcttctttgtaggtttgaagcagtattacaaagttgtcaattcataccctttggcacataaggtttttgaggaaaagactcagcatttacaggaactgttcagcagaattccatttaatcaagctttagtcttttctaatttgcacagcaggtaatgtaacttaaaaggtcatctggggaacttgtgaaataaaaggatagtcatggggcaatatttgtaaatttgtaaccatttatgttatatttcatagagcacaacatttggctgatatcctttcttctaaaggctttcctgctgagtgcatttcaggtaagttcatctcttactttaatctttatttagtatactgacttgaagcctccaaagtcaggaggaaaaaataccactttatgagttgtcttgagagtgtgaaattatcttgtatcttttagagtaaaggaattttcaggcttatttttcattggaatttgatatgtcaatattctatttttaaggagtaacttggtatatagtaaaaagttagaaaacgagtgttcagaaagacaattttattagaatctctcttatagccacatataacttaattctataaaaccaagtttgtctgtattgtgttaagataaaaatattttttctttagaatttttttttttaataattgttaaaatgctggagattgctaactgaattgactgttttatggttttgtggaaggcaaaagtgattgagttcttgctgcaaagttaaatgattactgctggcataataagaatgaaagagtcgaaggattttttgatcagtgagatcctttttagagttttgtttggtgtcagcttaatttttaacctgttggcatgagcttgtttagttatggtatatcacgagatactggggagactattttctaatgaaccaatacttaaactatagatcacactttaacaccaaattataaacagctgattaaagcatttttattttcctaagaattgctagttatacttcatatgtaactttgcaaatagaatgttatatattcaggcatggcctgatcactatttaggattgatttttgaatttcaagttgattgcctaaaccatagtttaaaagattttaactttggatcttaacctagatttgcacagaaggacagaattaactgtggttggtgtattaaaagtgttaagactgggtgttatgctttttactttttgtggattggataaatatgtattagcttagttgtttttatgtgactttcttttggggtccttttaggcaatatgaatcagaatcagcgtcttgatgctatggctaaactgaagcactttcattgcagagtcctcatttccacagatttggtaaatttcctattcagtttgggtgactaatccatcttgacatatgttctattatcagatgtacagatttcatatattattttatgtaggcgtatctaacaagaataccagaagataattatctttccagtgctgaggaaaatactttcttaaaaatgtctttaatatgtttatctttgtccttgaaattagtcattaggaatgaatttccttttcctctgctcctcagtattcagttcttaatttgtctcttgactgtactggaagaatataatatgcattggtatccatgctgtatagttaatgcaaacaaataattgctatcttcttcaattcaagacttctcgtgggattgatgctgagaaggtgaatctggttgtaaatctggatgtaccattggattgggagacatacatgcatcggattgggagagctggccgttttggtaaaaaaaaaaaaaaaagtttgagtgctttctttaaggggagggatctataatgtgacaggtgggctttgaatattatttttgtgaaaatgatttactacctaacattctctctgcttttaggtacattggggctgacagtgacctactgttgccggggagaggaagaaaatatgatgatgagaattgcccagaaatgtaatatcaaccttctccctttaccagatcccattccttctggtctgatggaagaatgtgtggattgggatgtggaagttaaagctgctgtgcatacatatggtatagcaagtgtacctaaccaacccttaaaaaagcaaattcagaaaatagagagaacccttcaaattcagaaagctcatggtgaccacatggcttcctctagaaataattctgtatctggactatcagtcaaatcaaaaaataataccaaacaaaagcttcctgtgaaaagccactcagaatgtggaatcatagaaaaagcaacgtcaccaaaagaactgggctgtgacaggcaatccgaagagcaaatgaagaattctgttcagactcccgttgaaaactccaccaacagtcagcaccaggtcaaagaagctttacctgtgtcactcccccagattccttgtctgtcttcctttaaaatccatcagccatacacgttgacttttgctgaattggtagaggattatgaacattatattaaagaggggttagagaaacctgtggaaatcatcaggcactacacaggccctggggatcagactgtgaatcctcaaaatggttttgtgagaaataaagttattgaacagagagtccctgtgttggcaagtagtagccaatctggagactctgagagtgacagtgattcttacagctcaagaacctcttcccagagcaaaggaaataagtcatacttggaaggctcttctgataatcagctgaaagactctgaatctacgcctgtggatgatcgtatttctttggaacaaccaccaaatggaagtgacacccccaatccagagaaatatcaagaatcacctggaatccagatgaagacaagacttaaagagggggctagccagagagctaagcagagccggagaaacctacccaggcggtcttccttcagattgcagactgaagcccaggaagatgattggtatgactgtcatagggaaatacgtctgagtttttctgatacctatcaggattatgaggagtactggagagcttactacagggcatggcaagaatattatgctgccgcttctcattcatattattggaatgctcagagacatccaagttggatggcagcttatcacatgaataccatttatctacaagaaatgatgcatagtaaccagtgattataggatatacctgagaccatcaggaactgtcaacaaatgatacctttggatatccatcctcctcgacttatagtacagtggtgtatagtggcatttctgataaacttgaaaagacttgagtctttccactgggacacatccatttttcagattgttttgatttaggccaggtatattatcttcatttttaagagtttctttaagaaacttcatcagattgttgaaagataatttttgggacatagagctgaaagtttcagggtgccattttctataagatcttcccaaaagaaacatttaaaaagatgacatataccaccacttacttaaaaacaataaaagcaatagatttgattagtaatattattgctgtggagttctttacaaagggcttaggctgcatttccttctatagaggtgcattcttgtagaaatttgtagcatggtagatcagaaaatcctcttaggttttaggcaggaaggaatatttaaacattgctttagattttcctaaaggtatttgatttgatctgttagcttccagtaaaaatatttgtttttagtaaaaacaaatttaaatatatttacatgtttgttgtttatcttttgaatgtcttttgaggctgtgaaagctgtccacatttttgctgtgtattaaatagctgtgggtcatca"
						.toUpperCase());
		this.builderForward.setGeneSymbol("DDX20");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 112308971,
				PositionType.ZERO_BASED), "G", "A");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(2, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("750G>A", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.SYNONYMOUS_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc001ezt_2() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001ezt.2	chr1	-	152184551	152196672	152185551	152195729	3	152184551,152195591,152196618,	152193966,152195754,152196672,	Q86YZ3	uc001ezt.2");
		this.builderForward
				.setSequence("agcaccctgaaagctgtttctgtctctaccctacttgttcctctggtgagctaggttactcaaacttgcaaaaaaaaaaatgcctaaactcctacaaggcgtcatcactgtcatcgatgttttctaccaatatgccacccagcatggggagtatgatacgttgaacaaggcagagctgaaagaacttctggaaaatgagtttcatcaaattctgaagaatccaaacgatccagatactgtggatatcatcttgcaaagtctggatcgagaccataacaagaaagtggattttactgagtatcttctgatgatattcaagctggttcaggctcgtaataaaatcattggcaaagattactgccaagtttcagggtcaaagctgagagatgacactcaccagcaccaagaggaacaagaagaaactgaaaaagaggagaacaaacggcaagaatcctcttttagtcattcaagttggagtgcaggagagaatgattcctattccagaaacgtcagaggaagtcttaaacctgggactgaatccatatccagaagactgagttttcaaagagacttttctggccaacataactcctactcaggtcagtcttccagctatggtgagcaaaactccgactcccatcagtcttcaggccgcggccaatgtgggtctgggtcagggcagtctcccaactatggccaacacggctctggctccggacagtcttccagcaatgacacacatgggtctggctcaggccagtcttctggctttagtcaacacaagtctagctcagggcagtcctctggttacagtcagcatggatctggctcaggtcactcctctggctacggacaacacggctctaggtcaggacagtcatctaggggtgaacgacacagatctagctcaggttcgtcttccagctatggtcagcatgggtctggttcccgtcagtctttgggccacggccgacaagggtctggatctcgccagtctcctagccacgtccgacatgggtccggttcggggcactcctccagccacggccaacacgggtctggctcaagttactcttacagccgtggccattatgagtctggctcaggccagacttctggctttgggcaacatgagtctggctcaggacagtcctctggctatagtaagcatggttctggctcaggtcactcctctagccagggacaacatggatctacgtcagggcaggcatcaagctctggccaacatggctccagctcacgtcagtcttccagctatggtcagcatgagtctgcctcccgtcactcttcaggccgcggccaacacagctctggatctggccagtctccaggccacggccagcgtgggtctgggtcagggcagtctcccagctccggccaacatgggactggctttggtcgatcttccagcagtggcccatatgtgtctggttcaggctactcttctggctttggtcaccacgagtctagctcagagcattcctctggttacactcagcatggatctggctcaggtcactcctccggccacggacaacacggctctaggtcaggacagtcatctaggggtgaacgacaaggatctagtgcaggttcatcttccagctatggtcagcatgggtctggctcccgtcaatctttgggacacagccgacatgggtctggatctggccagtctcctagccctagccgtggccgacatgagtctggttccaggcagtcttccagctatggcccacatgggtatggctcagggaggtcttcaagccgtggcccatatgagtctggctccggtcactcttctggcttaggtcaccaagagtctcgctcaggacagtcctctggctacggtcaacacggatctagctcgggtcattcctctacccatgggcaacatggttctacatcaggacagtcatcgagctgtggccaacatggagctacctcaggtcagtcttccagccacggtcagcatggctctggctcaagtcagtcttctcgctatggccaacagggctctggatctggccagtctcctagtcgcggccgacatgggtccgattttgggcactcttccagctacggccaacatgggtctggctccggttggtcttcaagcaatggcccacatgggtctgtctcaggccagtcttccggctttggtcacaagtctggctcagggcagtcctctggttacagtcagcatggatctggctcaagtcactcctccggctacagaaaacacggctctaggtcaggacagtcatctaggagtgaacaacacggatctagctcaggtttgtcttccagctatggtcagcatgggtcgggctcccatcaatcttcgggccacggccgacaagggtctggatctggccactctcctagccgtgtccgacatgggtccagttcagggcactcctccagccacggccaacacgggtctggcacaagttgttcttccagctgtggccattatgagtctggctcaggccaggcttctggttttgggcaacacgagtctggctcaggacagggctatagtcagcatggttctgcctcaggtcacttctctagccagggacgacatggatctacgtcagggcagtcatcaagctccggccaacatgactctagctcaggtcaatcttccagctatggtcagcatgagtctgcctcccatcacgcttcgggccgcggccgacatggctctggatctggccagtctccaggccacggccagcgtgggtctgggtcagggcagtctcccagctatggccgacatgggtctggctccggtcggtcttccagcagtggccgacatgggtctggctcaggccagtcttctggctttggtcacaagtctagctcagggcagtcctctggttacactcagcatggatctggctcaggtcactcctccagctacgaacaacacggctctaggtcaggacagtcatctaggagcgaacaacatggatctagctcaggttcgtcttccagctatggtcagcatgggtctggctcccgtcagtctttgggccacggccaacatgggtctggatctggccagtctcctagccctagccgtggccgacatgggtctggttccgggcagtcttccagctatggcccatataggtctggctcagggtggtcttcaagccgtggcccatatgagtctggctccggtcactcttctggcttaggtcaccgagagtctcgctcaggacagtcctctggctacggtcaacatggatctagctcaggtcattcctctacccatgggcaacacggttctacatcaggacagtcatcgagctgtggccaacatggagctagctcaggtcagtcttccagccacggtcagcatggctctggctcaagtcagtcttctggctatggccgacagggctctggatctggccagtctccaggccacggccagcgtgggtctgggtcaaggcagtctcccagctacggccgacatgggtctggctccggtcggtcttccagcagtggccaacatgggtctggcttaggcgagtcttctggctttggtcaccacgagtctagctcagggcagtcctctagttacagtcagcatgggtctggctcaggtcactcctctggctacggacaacacggctctagatcaggacagtcatctaggggtgaacgacacggatctagctcaggttcgtcttcccactatggtcagcatgggtctggctcccgtcagtcttcgggccacggccgacaagggtctggatctggccattcccctagccgcggccgacatgggtccggtttggggcactcctccagccacggccaacatgggtctggctcaggtcgttcttccagccgtggcccatatgagtctcgctcgggtcactcttctgtctttggtcaacatgagtctggctcaggacattcctctgcttacagtcagcatggtagtggctcagggcacttctgtagccaaggacagcatggttctacatcaggacagtcatcaacctttgaccaggagggatctagcacaggccagtcttccagctatggccaccgtggctctggctccagtcagtcttctggctatggccgacatggggctggatctggccagtctcctagtcgcggccgacatgggtccggttctgggcactcttccagctacggccaacatgggtctggctccggttggtcttccagcagtggccgacatgggtctggctcaggtcagtcttctggatttggtcaccacgagtctagctcatggcagtcctctggttgcactcagcatggatctggctcaggtcactcctccagctacgaacaacacggctctaggtcaggacagtcatctaggggtgaacgacacggatctagctcaggttcatcttccagctatggtcagcatgggtctggctcccgtcagtctttgggccacggccaacatgggtctggatctggccagtctcctagccctagccgtggccgacatgggtctggttctgggcagtcttccagctacagcccatatgggtctggctcagggtggtcttccagccgtggcccatatgagtctggctccagtcactcttctggcttaggtcaccgagagtctcgctcaggacagtcctctggctacggtcaacatggatctagctcaggtcattcctctacccatgggcaacatggttctacatcaggacagtcatcgagctgtggccaacatggagctagctcaggtcagtcttccagccacggtcagcatggctctggctcaagtcagtcttctggctatggccgacagggctctggatctggccagtctccaggccacggccagcgtgggtctgggtcaaggcagtctcccagctacggccgacatgggtctggctccggtcggtcttccagcagtggccaacatgggtctggcttaggcgagtcttctggctttggtcaccacgagtctagctcagggcagtcctctagttacagtcagcatgggtctggctcaggtcactcctctggctacggacaacacggctctagatcaggacagtcatctaggggtgaacgacacggatctagctcacgttcgtcttcccgctatggtcagcatgggtctggctcccgtcagtcttcgggccacggccgacaagggtctggatctggccagtcccctagccgcggccgacatgggtccggtttggggcactcctccagccacggccaacatgggtctggctcaggtcgttcttccagccgtggcccatatgagtctcgctcgggtcactcttctgtctttggtcaacatgagtctggctcaggacattcctctgcttacagtcagcatggtagtggctcagggcacttctgtagccaaggacagcatggttctacatcaggacagtcatcaacctttgaccaggagggatctagcacaggtcagtcttccagccacggtcagcatggctctggctcaagtcagtcttctagctatggccaacagggctctggatctggccagtctcctagtcgcggccgacatgggtccggttccgggcactcttccagctacggccaacatgggtctggctccggttggtcttccagcagtggccgacatgggtctggctcaggtcagtcttctggatttggtcaccatgagtctagctcatggcagtcctctggttacactcagcatggatctggctcaggtcactcctccagctacgaacaacacggctctaggtcaggacagtcatctaggggtgaacaacacggatctagctcaggttcatcttccagctatggtcagcatgggtctggctcccgtcagtctttgggccacggccaacatgggtctggatctggccagtctcctagccctagccgtggccgacatgggtctggttctgggcagtcttccagctacggcccatatgggtctggctcagggtggtcttccagccgtggcccatatgagtctggctccggtcactcttctggcttaggtcaccgagagtctcgctcaggacagtcctctggctacggtcaacatggatctagctcaggtcattcctctacccatgggcaacatggttctgcatcaggacagtcatcgagctgtggccaacatggagctagctcaggtcagtcttccagccacggtcagcatggctctggctcaagtcagtcttctggctatggccgacagggctctggatctggccagtctccaggccacggccagcgtgggtctgggtcaaggcagtctcccagctatggccgacatgggtctggctccggtcggtcttccagcagtggccaacatgggcctggcttaggcgagtcttctggctttggtcaccacgagtctagctcagggcagtcctctagttacagtcagcatgggtctggctcaggtcactcctctggctacggacaacacggctctagatcaggacagtcatctaggggtgaacgacacggatctagctcaggttcgtcttcccgctatggtcagcatgggtctggctcccgtcagtcttcgggccacggccgacaagggtctggatctggccattcccctagccgcggccgacatgggtccggttcggggcactcctccagccacggccaacatgggtctggctcaggtcgttcttccagccgtggcccatatgagtctcgctcgggtcactcttctgtctttggtcaacatgagtctggctcaggacattcctctgcttacagtcagcatggtagtggctcagggcacttctgtagccaaggacagcatggttctacatcaggacagtcatcaacctttgaccaggagggatctagcacaggtcagtcttccagccacggtcagcatggctctggctcaagtcagtcttctagctatggccaacagggctctggatctggccagtctcctagtcgcggccgacatgggtccggttccgggcactcttccagctacggccaacatgggtctggctccggttggtcttccagcagtggccgacatgggtctggctcaggtcagtcttctggatttggtcaccacgagtctagctcatggcagtcctctggttacactcagcatggatctggctcaggtcactcctccagctacgaacaacacggctctaggtcaggacagtcatctaggggtgaacgacacggatctagctcaggttcatcttccagctatggtcagcatgggtctggctcccgtcagtctttgggccacggccaacatgggtctggatctggccagtctcctagccctagccgtggccgacatgggtctggttctgggcagtcttccagctacagcccatatgggtctggctcagggtggtcttccagccgtggcccatatgagtctggctccggtcactcttctggcttaggtcaccgagagtctcgctcaggacagtcctctggctacggtcaacatggatctagctcaggtcattcctctacccatgggcaacatggttctacatcaggacagtcatcgagctgtggccaacatggagctagctcaggtcagtcttccagccacggtcagcatggctctggctcaagtcagtcttctggctatggccgacagggctctggatctggccagtctccaggccacggccagcgtgggtctgggtcaaggcagtctcccagctacggccgacatgggtctggctccggtcggtcttccagcagtggccaacatgggtctggcttaggcgagtcttctggctttggtcaccacgagtctagctcagggcagtcctctagttacagtcagcatgggtctggctcaggtcactcctctggctacggacaacacggctctagatcaggacagtcatctaggggtgaacgacacggatctagctcaggttcgtcttcccactatggtcagcatgggtctggctcccgtcagtcttcgggccacggccgacaagggtctggatctggccagtcccctagccgcggccgacatgggtccggtttggggcactcctccagccacggccaacatgggtctggctcaggtcgttcttccagccgtggcccatatgagtctcgcttgggtcactcttctgtctttggtcaacatgagtctggctcaggacattcctctgcttacagtcagcatggtagtggctcagggcacttctgtagccaaggacagcatggttctacatcaggacagtcatcaacctttgaccaggagggatctagcacaggccagtcttccagctatggccaccgtggctctggctccagtcagtcttctggctatggccgacatggggctggatctggccagtctcttagccacggccgacacgggtctggttcagggcagtcttccagctacggccaacatgggtctggctcaggacagtcctctggttatagtcagcatggaagtggctcagggcaagatgggtattcttattgcaaaggaggaagtaaccatgatgggggaagttctggctcatattttctcagttttcctagtagcacttcaccctatgaatatgtccaagagcagaggtgctacttttatcagtgaataataaacataaatgcaatttactcaagtagcaatttaagaaataggaaagtcatctatgaattcatcatgaaagacaagcaatccatcatgaaattcgttctaaaagtgaatcaatgcatttctgtctctttctttagagcctaaaactgtagcatatatcttgttatggggttccttccaaagactgttaggcatttgtgctactttgttagaaaatactgagtggaataacttgttagaatgagggttaaactttgaggaataatgaaaagcttttaaagagctttgggtttagttggagttgtctttttgagagctcatcattcatttatagatggtgccaaagctaaccttacatttcttagaagcaaaatattacaaatgcattaccagtcctagatacaaagctttgttttacagcaattagtgtaccctaatttttagtgtgccccaagtttggtgtgtcccaatttttggtattgtggcagaaggtgaaggctctgaaagcaaagatgcagcagcggtagtgtctttacttatcaaaaccatcaagtcctttttcttgggtatatatttaatcagtaagttaattagtggcataaaaaagtagcatcagggtcttttcccaagccagtgagcaagagcattatttcataaagaatagggatttatcatttcaggaaaaaaaaaaacattcaaatgtgggctttagcttgttttcagcagaaagatcttgctccctatttctaagaggctgctcaatattgggaaatatattgaggagttattccatggaaatacaatgctttccacctactactgtagttcaataacgtttccacctgaaaaaatatcatccatgcccagatgaaaaggaagagtatctgtcactgctacatagttccttaatttgactgtaacacatttgtttcaagtctttggattcaaacaaccggattgtattaaaattgacaataaataaatgttgattaaatac"
						.toUpperCase());
		this.builderForward.setGeneSymbol("HRNR");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 152193290,
				PositionType.ZERO_BASED), "G", "T");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(2, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("814C>A", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.SYNONYMOUS_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc001gde_2() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001gde.2	chr1	+	165513477	165533185	165513533	165533061	2	165513477,165532741,	165514155,165533185,	Q8N7C0	uc001gde.2");
		this.builderForward
				.setSequence("gatcagaggacagagcccgcaggaaggtgaaaggagggtggttgtggcttcttactatgtcccttgcttcaggccctggccctgggtggttactcttttcctttggaatggggctggtatcagggtcaaagtgtccaaataattgtctgtgtcaagcccaagaagtaatctgcacagggaagcagttaaccgaatacccccttgacatacccctgaacacccggaggctgttcctgaacgagaacagaatcactagtttgccagcaatgcatctaggactcctcagtgaccttgtttatttggactgtcagaacaaccggattcgagaggtgatggattataccttcatcggggtcttcaaactcatctaccttgacctcagctccaacaacctaacctcgatctccccattcactttctcggtgctcagcaacctggtgcagctgaacattgccaacaaccctcacctgttatcgcttcacaagttcacctttgccaacaccacctctttgaggtacctggacctcagaaataccggcttgcagaccctggacagtgctgccttataccacctcactactctggagaccctgtttctgagtggaaacccctggaagtgcaactgctctttcctggacttcgccatcttcttaatagtgttccatatggacccctcagatgatctaaatgccacatgtgtggagcccacagagctgacagggtggcccatcacccgggtggggaacccactccgatacatgtgcatcacgcacctggaccacaaagactacatcttcctgctgctcatcggcttctgcatcttcgccgcgggaactgtggctgcctggctcacaggtgtgtgtgctgtgctctaccagaacacccgccacaagtcgagtgaagaagatgaggacgaggccgggactagggtggaagtcagccggcggatttttcaaacccagacgagctcggtccaggagttccctcagcttatttagttgccagagaccactatcttatgtgcctcccccaggctccctgctttctctcttgccctccccatcccaccaccttggagctgtcatagagattgaaaccttctagtaaaataaataaaatctcaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("LRC52");
		this.infoForward = builderForward.build();
		// RefSeq NM_001005214.3

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 165533004,
				PositionType.ZERO_BASED), "C", "A");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(1, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("886C>A", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.SYNONYMOUS_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc001ggz_4() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001ggz.4	chr1	+	170501262	170514929	170501289	170513990	4	170501262,170508350,170511631,170513845,	170501425,170508708,170511733,170514929,	NP_001139511	uc001ggz.4");
		this.builderForward
				.setSequence("attggccgcggccggggaaaaacccggatgagctgggcagcagtgttggcagtcgcggctgcgagatttgggcacttttgggggtgccggtggcccgggccgatggcgcaaggttgggcaggcttctctgaggaggaactgaggagactaaagcagactaaagatccatttgaaccacagcgacgtctccccgcgaagaaaagtcgacaacaacttcagcgagaaaaagcccttgtagagcaaagccaaaaacttgggcttcaagatggatcaacctcattacttccagagcagctgctttcagcaccaaaacagagagttaacgttcaaaaaccacctttttcttcccctactcttccgagtcatttcactctcacctcccccgttggtgatggacaaccacagggcattgaaagtcagccaaaggaactgggacttgagaattcccatgatggtcacaacaatgttgagattctacctccaaagccagattgcaaattggagaaaaagaaagtggaattgcaagaaaaatctcgttgggaagtcctccaacaagaacaacggctaatggaagagaaaaataaacgtaaaaaagctcttttggctaaagctattgcagaaagatccaaaagaactcaggcagagaccatgaaactaaagcggatccagaaggagttgcaggctttagatgacatggtgtcagctgacattggaattctcaggaaccggattgatcaggccagcttagactattcatacgctcggtgagttggggaaattgaatctgaacaaggagtatgatttaattaatgtttattgccagctgttccaggggcttttatgtatatcatcattgaagcctcaaaacaaccccattcagtgctgtatttattcttcttttgtgctaattcaagtgtctcctcttcagtgaagtcttccttaactctgccctactagttaaccttgttcgtgtgccattagagcagttcttgcattatttatttacgttttagtctttgctgcgaaaccctcagctatttaatgtttggaagtgtttttgctcatctttcagcatctagcaaagtaaatatctgagtggtttcgccaaaactgttactctgacaccaagtctgtgttctttccattaaaatttttgatatctgatatagtcctataggattgaagtctagtcctcactttagctttcatcagagtaagaggatctagtttagtttttaaaagcttatgaatattttaagcatatcctctgtcttggaaatttgaaatattttctctttttttttcctttcaattatactctgtatgaaatggtttaatatatgtttccttggtttcaaccattggcattttttaaaagagaaaaaataccatggagtatttttaaaatgaactaacacatgtaatgaattacatggactttgcagacagataaaagggcaaaaaaatatgtccaatttcagggttttcagccctttcaacattgctttaatcatcctggtttgtcttttttcacaagggaatgctgtgttaatgactcagttttctatcttactctgttacactagaaatgaatcagaatgcctcattaggccagggtaaaccacaacatcagaatgtttatataacaatttattaaagcggaaatgcttgattgtggtttttaataaactgttgagtgtttaagaaaaaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("GORAB");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 170501384,
				PositionType.ZERO_BASED), "C", "T");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(0, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("96C>T", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.SYNONYMOUS_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc001gih_1() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001gih.1	chr1	+	172348157	172387567	172348196	172387457	4	172348157,172356272,172357712,172387420,	172348322,172356499,172357949,172387567,	H0YBC6	uc001gih.1");
		this.builderForward
				.setSequence("gctgaaaatgatgagaatggacaagcagaaaacttttccatggacccacaattggagaggcaagtggagaccattcgcaacctcgtagactcctacatgtccattatcaacaaatgtatccgagatctaattccaaaaacaataatgcaccttatgatcaataacgttaaagatttcataaattccgagctcctagcacagttgtattcttcagaggaccaaaataccctgatggaggaatctgctgagcaggctcagcgccgggatgagatgcttcgaatgtatcaagcactgaaagaagcccttgggataattggggacatcagcacagccaccgtgtccactccggcaccccctccagtggatgactcctggatacagcactctcgcaggtcacctcctccaagccccacaacccaaaggaggccaacactaagtgctcccctcgcaaggcccacatccggccgaggaccagctcctgccattccctctcctggcccccactctggggctcctccagtcccattccgtccaggcccattacctcctttccccagcagcagtgactccttcggagcccctccacaagttccatctaggcctacgagggccccgcccagtgtcccaaggcgaccccctcctgcagttccaggacgaccatcctaacccccatcattcatctccttttgtttccaacaacatgtctatccatggtatttaaaagcttttcatttgcactatatgtcgtatgtacataaaaacttttatttttcatc"
						.toUpperCase());
		this.builderForward.setGeneSymbol("DNM3");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 172356436,
				PositionType.ZERO_BASED), "A", "G");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(1, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("291A>G", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.SYNONYMOUS_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc001gpy_4() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001gpy.4	chr1	+	182992594	183114727	182992851	183111925	28	182992594,183072462,183077410,183079622,183083665,183084654,183085711,183085901,183086454,183086668,183087168,183090857,183091197,183093765,183094531,183095254,183096360,183097728,183099478,183100433,183101534,183102540,183103794,183104176,183105520,183106803,183109538,183111668,	182993269,183072767,183077541,183079789,183083854,183084772,183085810,183086038,183086577,183086858,183087281,183091079,183091386,183094011,183094685,183095397,183096539,183097885,183099684,183100513,183101672,183102685,183103944,183104291,183105720,183106962,183109638,183114727,	P11047	uc001gpy.4");
		this.builderForward
				.setSequence("gtgcaggctgctcccggggtaggtgagggaagcgcggaggcggcgcgcgggggcagtggtcggcgagcagcgcggtcctcgctaggggcgcccacccgtcagtctctccggcgcgagccgccgccaccgcccgcgccggagtcaggcccctgggcccccaggctcaagcagcgaagcggcctccgggggacgccgctaggcgagaggaacgcgccggtgcccttgccttcgccgtgacccagcgtgcgggcggcgggatgagagggagccatcgggccgcgccggccctgcggccccgggggcggctctggcccgtgctggccgtgctggcggcggccgccgcggcgggctgtgcccaggcagccatggacgagtgcacggacgagggcgggcggccgcagcgctgcatgcccgagttcgtcaacgccgccttcaacgtgactgtggtggccaccaacacgtgtgggactccgcccgaggaatactgtgtgcagaccggggtgaccggggtcaccaagtcctgtcacctgtgcgacgccgggcagccccacctgcagcacggggcagccttcctgaccgactacaacaaccaggccgacaccacctggtggcaaagccagaccatgctggccggggtgcagtaccccagctccatcaacctcacgctgcacctgggaaaagcttttgacatcacctatgtgcgtctcaagttccacaccagccgcccggagagctttgccatttacaagcgcacacgggaagacgggccctggattccttaccagtactacagtggttcctgtgagaacacctactccaaggcaaaccgcggcttcatcaggacaggaggggacgagcagcaggccttgtgtactgatgaattcagtgacatttctcccctcactgggggcaacgtggccttttctaccctggaaggaaggcccagcgcctataactttgacaatagccctgtgctgcaggaatgggtaactgccactgacatcagagtaactcttaatcgcctgaacacttttggagatgaagtgtttaacgatcccaaagttctcaagtcctattattatgccatctctgattttgctgtaggtggcagatgtaaatgtaatggacacgcaagcgagtgtatgaagaacgaatttgataagctggtgtgtaattgcaaacataacacatatggagtagactgtgaaaagtgtcttcctttcttcaatgaccggccgtggaggagggcaactgcggaaagtgccagtgaatgcctgccctgtgattgcaatggtcgatcccaggaatgctacttcgaccctgaactctatcgttccactggccatgggggccactgtaccaactgccaggataacacagatggcgcccactgtgagaggtgccgagagaacttcttccgccttggcaacaatgaagcctgctcttcatgccactgtagtcctgtgggctctctaagcacacagtgtgatagttacggcagatgcagctgtaagccaggagtgatgggggacaaatgtgaccgttgccagcctggattccattctctcactgaagcaggatgcaggccatgctcttgtgatccctctggcagcatagatgaatgtaatattgaaacaggaagatgtgtttgcaaagacaatgtcgaaggcttcaattgtgaaagatgcaaacctggattttttaatctggaatcatctaatcctcggggttgcacaccctgcttctgctttgggcattcttctgtctgtacaaacgctgttggctacagtgtttattctatctcctctacctttcagattgatgaggatgggtggcgtgcggaacagagagatggctctgaagcatctctcgagtggtcctctgagaggcaagatatcgccgtgatctcagacagctactttcctcggtacttcattgctcctgcaaagttcttgggcaagcaggtgttgagttatggtcagaacctctccttctcctttcgagtggacaggcgagatactcgcctctctgcagaagaccttgtgcttgagggagctggcttaagagtatctgtacccttgatcgctcagggcaattcctatccaagtgagaccactgtgaagtatgtcttcaggctccatgaagcaacagattacccttggaggcctgctcttaccccttttgaatttcagaagctcctaaacaacttgacctctatcaagatacgtgggacatacagtgagagaagtgctggatatttggatgatgtcaccctggcaagtgctcgtcctgggcctggagtccctgcaacttgggtggagtcctgcacctgtcctgtgggatatggagggcagttttgtgagatgtgcctctcaggttacagaagagaaactcctaatcttggaccatacagtccatgtgtgctttgcgcctgcaatggacacagcgagacctgtgatcctgagacaggtgtttgtaactgcagagacaatacggctggcccgcactgtgagaagtgcagtgatgggtactatggagattcaactgcaggcacctcctccgattgccaaccctgtccgtgtcctggaggttcaagttgtgctgttgttcccaagacaaaggaggtggtgtgcaccaactgtcctactggcaccactggtaagagatgtgagctctgtgatgatggctactttggagaccccctgggtagaaacggccctgtgagactttgccgcctgtgccagtgcagtgacaacatcgatcccaatgcagttggaaattgcaatcgcttgacgggagaatgcctgaagtgcatctataacactgctggcttctattgtgaccggtgcaaagacggattttttggaaatcccctggctcccaatccagcagacaaatgcaaagcctgcaattgcaatctgtatgggaccatgaagcagcagagcagctgtaaccccgtgacggggcagtgtgaatgtttgcctcacgtgactggccaggactgtggtgcttgtgaccctggattctacaatctgcagagtgggcaaggctgtgagaggtgtgactgccatgccttgggctccaccaatgggcagtgtgacatccgcaccggccagtgtgagtgccagcccggcatcactggtcagcactgtgagcgctgtgaggtcaaccactttgggtttggacctgaaggctgcaaaccctgtgactgtcatcctgagggatctctttcacttcagtgcaaagatgatggtcgctgtgaatgcagagaaggctttgtgggaaatcgctgtgaccagtgtgaagaaaactatttctacaatcggtcttggcctggctgccaggaatgtccagcttgttaccggctggtaaaggataaggttgctgatcatagagtgaagctccaggaattagagagtctcatagcaaaccttggaactggggatgagatggtgacagatcaagccttcgaggatagactaaaggaagcagagagggaagttatggacctccttcgtgaggcccaggatgtcaaagatgttgaccagaatttgatggatcgcctacagagagtgaataacactctgtccagccaaattagccgtttacagaatatccggaataccattgaagagactggaaacttggctgaacaagcgcgtgcccatgtagagaacacagagcggttgattgaaatcgcatccagagaacttgagaaagcaaaagtcgctgctgccaatgtgtcagtcactcagccagaatctacaggggacccaaacaacatgactcttttggcagaagaggctcgaaagcttgctgaacgtcataaacaggaagctgatgacattgttcgagtggcaaagacagccaatgatacgtcaactgaggcatacaacctgcttctgaggacactggcaggagaaaatcaaacagcatttgagattgaagagcttaataggaagtatgaacaagcgaagaacatctcacaggatctggaaaaacaagctgcccgagtacatgaggaggccaaaagggccggtgacaaagctgtggagatctatgccagcgtggctcagctgagccctttggactctgagacactggagaatgaagcaaataacataaagatggaagctgagaatctggaacaactgattgaccagaaattaaaagattatgaggacctcagagaagatatgagagggaaggaacttgaagtcaagaaccttctggagaaaggcaagactgaacagcagaccgcagaccaactcctagcccgagctgatgctgccaaggccctcgctgaagaagctgcaaagaagggacgggataccttacaagaagctaatgacattctcaacaacctgaaagattttgataggcgtgtgaacgataacaagacggccgcagaggaggcactaaggaagattcctgccatcaaccagaccatcactgaagccaatgaaaagaccagagaagcccagcaggccctgggcagtgctgcggcggatgccacagaggccaagaacaaggcccatgaggcggagaggatcgcgagcgctgtccaaaagaatgccaccagcaccaaggcagaagctgaaagaacttttgcagaagttacagatctggataatgaggtgaacaatatgttgaagcaactgcaggaagcagaaaaagagctaaagagaaaacaagatgacgctgaccaggacatgatgatggcagggatggcttcacaggctgctcaagaagccgagatcaatgccagaaaagccaaaaactctgttactagcctcctcagcattattaatgacctcttggagcagctggggcagctggatacagtggacctgaataagctaaacgagattgaaggcaccctaaacaaagccaaagatgaaatgaaggtcagcgatcttgataggaaagtgtctgacctggagaatgaagccaagaagcaggaggctgccatcatggactataaccgagatatcgaggagatcatgaaggacattcgcaatctggaggacatcaggaagaccttaccatctggctgcttcaacaccccgtccattgaaaagccctagtgtctttagggctggaaggcagcatccctctgacaggggggcagttgtgaggccacagagtgccttgacacaaagattacatttttcagacccccactcctctgctgctgtccatgactgtccttttgaaccaggaaaagtcacagagtttaaagagaagcaaattaaacatcctgaatcgggaacaaagggttttatctaataaagtgtctcttccattcacgttgctaccttacccacactttcccttctgatttgcgtgaggacgtggcatcctacgttactgtacagtggcataagcacatcgtgtgagcccatgtatgctggggtagagcaagtagccctcccctgtctcatcgataccagcagaacctcctcagtctcagtactcttgtttctatgaaggaaaagtttggctactaacagtagcattgtgatggccagtatatccagtccatggataaagaaaatgcatctgcatctcctacccctcttccttctaagcaaaaggaaataaacatcctgtgccaaaggtattggtcatttagaatgtcggtagccatccatcagtgcttttagttattatgagtgtaggacactgagccatccgtgggtcaggatgcaattatttataaaagtctccaggtgaacatggctgaagatttttctagtatattaataattgactaggaagatgaactttttttcagatctttgggcagctgataatttaaatctggatgggcagcttgcactcaccaatagaccaaaagacatcttttgatattcttataaatggaacttacacagaagaaatagggatatgataaccactaaaattttgttttcaaaatcaaactaattcttacagcttttttattagttagtcttggaactagtgttaagtatctggcagagaacagttaatccctaaggtcttgacaaaacagaagaaaaacaagcctcctcgtcctagtcttttctagcaaagggataaaacttagatggcagcttgtactgtcagaatcccgtgtatccatttgttcttctgttggagagatgagacatttgacccttagctccagttttcttctgatgtttccatcttccagaatccctcaaaaaacattgtttgccaaatcctggtggcaaatacttgcactcagtatttcacacagctgccaacgctatcgagttcctgcactttgtgatttaaatccactctaaaccttccctctaagtgtagagggaagacccttacgtggagtttcctagtgggcttctcaacttttgatcctcagctctgtggttttaagaccacagtgtgacagttccctgccacacacccccttcctcctaccaacccacctttgagattcatatatagcctttaacactatgcaactttgtactttgcgtagcaggggcggggtggggggaaagaaactattatctgacacactggtgctattaattatttcaaatttatatttttgtgtgaatgttttgtgttttgtttatcatgattatagaataaggaatttatgtaaatatacttagtcctatttctagaatgacactctgttcactttgctcaatttttcctcttcactggcacaatgtatctgaatacctccttccctcccttctagaattctttggattgtactccaaagaattgtgccttgtgtttgcagcatctccattctctaaaattaatataattgctttcctccacacccagccactgtaaagaggtaacttgggtcctcttccattgcagtcctgatgatcctaacctgcagcacggtggttttacaatgttccagagcaggaacgccaggttgacaagctatggtaggattaggaaagtttgctgaagaggatctttgacgccacagtgggactagccaggaatgagggagaaatgccctttctggcaattgttggagctggataggtaagttttataagggagtacattttgactgagcacttagggcatcaggaacagtgctacttactgatgggtagactgggagaggtggtgtaacttagttcttgatgatcccacttcctgtttccatctgcttgggatataccagagtttaccacaagtgttttgacgatatactcctgagctttcactctgctgcttctcccaggcctcttctactatggcaggagatgtggcgtgctgttgcaaagttttcacgtcattgtttcctggctagttcatttcattaagtggctacatcctaacatatgcatttggtcaaggttgcagaagaggactgaagattgactgccaagctagtttgggtgaagttcactccagcaagtctcaggccacaatggggtggtttggtttggtttccttttaactttctttttgttatttgcttttctcctccacctgtgtggtatattttttaagcagaattttattttttaaaataaaaggttctttacaagatgataccttaattacactcccgcaacacagccattattttattgtctagctccagttatctgtattttatgtaatgtaattgacaggatggctgctgcagaatgctggttgacacagggattattatactgctatttttccctgaatttttttcctttgaattccaactgtggaccttttatatgtgccttcactttagctgtttgccttaatctctacagccttgctctccggggtggttaataaaatgcaacacttggcatttttatgttttaagaaaaacagtattttatttataataaaatctgaatatttgtaacccttta"
						.toUpperCase());
		this.builderForward.setGeneSymbol("LAMC1");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 183105533,
				PositionType.ZERO_BASED), "T", "C");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(24, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("4128T>C", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.SYNONYMOUS_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc001gxe_3() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001gxe.3	chr1	+	201951765	201975275	201966489	201974844	10	201951765,201958031,201965274,201966446,201969029,201970503,201970786,201972364,201973481,201974685,	201952241,201958172,201965537,201966682,201969143,201970616,201970895,201972589,201973624,201975275,	Q9H4A4	uc001gxe.3");
		this.builderForward
				.setSequence("cccggccggtgagcaacggctctgcggccatggcgagcggcgagcattcccccggcagcggcgcggcccggcggccgctgcactccgcgcaggctgtggacgtggcctcggcctccaacttccgggcctttgagctgctgcacttgcacctggacctgcgggctgagttcgggcctccagggcccggcgcagggagccgggggctgagcggcaccgcggtcctggacctgcgctgcctggagcccgagggcgccgccgagctgcggctggactcgcacccgtgcctggaggtgacggcggcggcgctgcggcgggagcggcccggctcggaggagccgcctgcggagcccgtgagcttctacacgcagcccttctcgcactatggccaggccctgtgcgtgtccttcccgcagccctgccgcgccgccgagcgcctccaggtgctgctcacctaccgcgtcggggagggacccggggtttgctggttggctcccgagcagacagcaggaaagaagaagcccttcgtgtacacccagggccaggctgtcctaaaccgggccttcttcccttgcttcgacacgcctgctgttaaatacaagtattcagctcttattgaggagccgggtgtgggctgagccctgcctgattgatgctgccaaggaggagtacaacggggtgatagaagaatttttggcaacaggagagaagctttttggaccttatgtttggggaaggtgtggtatcacattgactctagtgtctcctgttaaactgagtcactggcttccattcttcctcttcgtgtggcttttccacctcctgcctcccagagcaaagtttccctgatcccagaagtgaaggagaggaggaagagaggagggtatgacttgctcttcatgccaccgtcctttccatttggaggaatggagaacccttgtctgacctttgtcaccccctgcctgctagctggggaccgctccttggcagatgtcatcatccatgagatctcccacagttggtttgggaacctggtcaccaacgccaactggggtgaattctggctcaatgaaggtttcaccatgtacgcccagaggaggatctccaccatcctctttggcgctgcgtacacctgcttggaggctgcaacggggcgggctctgctgcgtcagcacatggacatcactggagaggaaaacccactcaacaagctccgcgtgaagattgaaccaggcgttgacccggacgacacctataatgagaccccctacgagaaaggtttctgctttgtttcatacctggcccacttggtgggtgatcaggatcagtttgacagttttctcaaggcctatgtgcatgaattcaaattccgaagcatcttagccgatgactttctggacttctacttggaatatttccctgagcttaagaaaaagagagtggatatcattccaggttttgagtttgatcgatggctgaatacccccggctggcccccgtacctccctgatctctcccctggggactcactcatgaagcctgctgaagagctagcccaactgtgggcagccgaggagctggacatgaaggccattgaagccgtggccatctctccctggaagacctaccagctggtctacttcctggataagatcctccagaaatcccctctccctcctgggaatgtgaaaaaacttggagacacatacccaagtatctcaaatgcccggaatgcagagctccggctgcgatggggccaaatcgtccttaagaacgaccaccaggaagatttctggaaagtgaaggagttcctgcataaccaggggaagcagaagtatacacttccgctgtaccacgcaatgatgggtggcagtgaggtggcccagaccctcgccaaggagacttttgcatccaccgcctcccagctccacagcaatgttgtcaactatgtccagcagatcgtggcacccaagggcagttagaggctcgtgtgcatggcccctgcctcttcaggctctccaggctttcagaataattgtttgttcccaaattcctgttccctgatcaacttcctggagtttatatcccctcaggataatctattctctagcttaggtatctgtgactcttgggcctctgctctggtgggaacttacttctctatagcccactgagccccgagacagagaacctgcccacagctctccccgctacaggctgcaggcactgcagggcagcgggtattctcctccccacctaagtctctgggaagaagtggagaggactgatgctcttcttttttctctttctgtcctttttcttgctgattttatgcaaagggctggcattctgattgttcttttttcaggtttaatccttattttaataaagttttcaagcaaaaattaagtta"
						.toUpperCase());
		this.builderForward.setGeneSymbol("RNPEP");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 201969081,
				PositionType.ZERO_BASED), "G", "A");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(4, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("246G>A", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.SYNONYMOUS_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc001hnh_1() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001hnh.1	chr1	-	222695601	222721444	222695942	222721386	9	222695601,222696953,222700310,222705307,222711989,222713351,222715353,222716878,222721065,	222696229,222697036,222700392,222705453,222712116,222713683,222715497,222717531,222721444,	Q6UWX4	uc001hnh.1");
		this.builderForward
				.setSequence("gcagagcagggaagccaacctgagcaaacacagcagcccgagtgttcccaaggccaaaatgctgagaacgtccactcctaatctgtgtggtggtctgcattgccgggccccctggctctcttctggcattctctgcctctgcctcatattcttgttgggccaggtgggcttgctgcagggacacccccagtgcctggattacgggccccctttccagccccctctgcaccttgagttttgctctgactatgagtccttcggctgctgtgatcagcacaaggaccgccgcatcgctgcccggtactgggacatcatggaatattttgatctgaagagacatgagctgtgtggagattacattaaagacatcctttgccaggagtgctcgccctacgcagcccacctctacgacgccgaaaacacccagacgcctctccggaatctcccgggcctctgctctgattactgctctgccttccattctaactgtcactcagccatttccctgctgaccaatgaccgcggcctccaggagtctcatggaagggacggtacccgcttctgccacctcctggaccttcctgacaaggactattgcttccctaatgtcctgaggaacgactatctcaaccgccacctgggcatggtggcccaagatcctcagggctgcctgcagctctgcctgagcgaggtggccaacgggctgaggaaccccgtctccatggtccatgctggggacggcacccatcgcttctttgttgccgagcaggtaggagtggtgtgggtctacctccctgatgggagtcgcctggagcaacccttcctggacctcaagaacatcgtgttgaccaccccatggatcggggatgagagaggcttcttggggttggcttttcaccccaaattccgccacaatcgcaagttctatatttattattcgtgcctggacaagaagaaggtagaaaagatccgaattagtgagatgaaggtttctcgggctgatcctaacaaagctgacctgaaatcagagagggtcatcttggagattgaagaaccagcctcaaaccataatggcggacaacttctttttggcctggatggctatatgtacatattcactggggacgggggacaggctggagatccctttggcctgtttggaaatgctcagaacaaaagttccctgctgggaaaagttttaaggatcgatgtgaacagggcaggctcacatggcaagcggtaccgagtcccctcggacaatccatttgtttctgagccaggggcccaccccgccatctatgcctatgggatcaggaacatgtggcgttgtgctgtggaccgaggggaccccatcacgcgccagggccgaggccggatattctgtggggacgtgggccagaacaggtttgaagaggttgacctcattttgaaaggtggaaactatggctggagagcaaaggaagggtttgcatgttatgacaaaaaactttgtcacaatgcctctttggatgatgttctgccaatctatgcttatggccatgcagtggggaagtcagtcactggaggttatgtctatcgtggttgtgaatccccaaatctcaatggcctgtatatctttggagacttcatgagtggtcgacttatggctttgcaggaagatagaaaaaacaagaaatggaagaagcaggatctttgcctgggcagcaccacgtcctgtgccttcccagggctgatcagcacccatagcaagttcatcatctcctttgctgaagatgaagcaggggagctgtatttcctggcgacctcttacccaagtgcctatgcaccacgtggatctatttacaagtttgttgacccctcaaggcgagcacccccaggcaagtgcaaatacaagccagtgcccgtgagaaccaagagtaagcggatcccgttcagaccactcgccaagacagtcttggacttgctaaaggaacaatcagagaaagctgctagaaaatcttccagtgcaaccttagcttctggcccagcccagggtttgtctgagaaaggctcctccaagaagctggcttctcctacaagcagcaagaatacattgcgagggcctggtacaaagaagaaagccagagtggggccccacgtccgccagggcaagaggaggaagagcctgaaaagccacagtggcaggatgaggccatcagcagagcagaagcgagctggcagaagtctcccttgacctattggtcaaggtggccgacagggtgacgtgagagaggagagccacctcatcaaatgaaagtcactgctgaataaagaccttagaagtctgggaagccagggtagaggtggggcagggcggttttcctctccctgggaaatcttgctgtctactgaataaataaatgcaccttctctgtatgcagtgcttctgtgggagaccatatcccagattgctggtgcacctgggttatggtaagcactagtccatgagcctgcttggaatcacactggatgtctccgttttgtcttgtaaatgcctacaacctgaggtaataaatcaacatttgctcaaactggaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("HHIPL2");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 222721287,
				PositionType.ZERO_BASED), "C", "T");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(0, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("99G>A", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.SYNONYMOUS_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc003str_3() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc003str.3	chr7	-	16832263	16844738	16832531	16841420	8	16832263,16834559,16837254,16839367,16840774,16840943,16841281,16844558,	16832581,16834643,16837318,16839441,16840827,16841007,16841427,16844738,	O95994	uc003str.3");
		this.builderForward
				.setSequence("aatcacttggggaaaggaaggttcgtttctgagttagcaacaagtaaatgcagcactagtgggtgggattgaggtatgccctggtgcataaatagagactcagctgtgctggcacactcagaagcttggaccgcatcctagccgccgactcacacaaggcaggtgggtgaggaaatccagagttgccatggagaaaattccagtgtcagcattcttgctccttgtggccctctcctacactctggccagagataccacagtcaaacctggagccaaaaaggacacaaaggactctcgacccaaactgccccagaccctctccagaggttggggtgaccaactcatctggactcagacatatgaagaagctctatataaatccaagacaagcaacaaacccttgatgattattcatcacttggatgagtgcccacacagtcaagctttaaagaaagtgtttgctgaaaataaagaaatccagaaattggcagagcagtttgtcctcctcaatctggtttatgaaacaactgacaaacacctttctcctgatggccagtatgtccccaggattatgtttgttgacccatctctgacagttagagccgatatcactggaagatattcaaatcgtctctatgcttacgaacctgcagatacagctctgttgcttgacaacatgaagaaagctctcaagttgctgaagactgaattgtaaagaaaaaaaatctccaagcccttctgtctgtcaggccttgagacttgaaaccagaagaagtgtgagaagactggctagtgtggaagcatagtgaacacactgattaggttatggtttaatgttacaacaactattttttaagaaaaacaagttttagaaatttggtttcaagtgtacatgtgtgaaaacaatattgtatactaccatagtgagccatgattttctaaaaaaaaaaataaatgttttgggggtgttctgttttctccaaaaaaaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("AGR2");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 7, 16834596,
				PositionType.ZERO_BASED), "A", "G");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(6, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("441T>C", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.SYNONYMOUS_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc003suh_3() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc003suh.3	chr7	+	18535884	18993939	18535925	18993876	23	18535884,18624903,18629967,18631138,18633530,18668972,18674249,18684293,18687407,18688088,18705835,18767202,18788627,18801779,18806728,18832967,18868783,18869083,18875089,18875522,18914100,18975431,18993768,	18535947,18625145,18630109,18631265,18633652,18669104,18674365,18684416,18687621,18688306,18706099,18767380,18788761,18801900,18806778,18833075,18868839,18869171,18875209,18875620,18914219,18975565,18993939,	Q9UKV0	uc003suh.3");
		this.builderForward
				.setSequence("atggggtggctggacgagagcagctcttggctcagcaaagaatgcacagtatgatcagctcagtggatgtgaagtcagaagttcctgtgggcctggagcccatctcacctttagacctaaggacagacctcaggatgatgatgcccgtggtggaccctgttgtccgtgagaagcaattgcagcaggaattacttcttatccagcagcagcaacaaatccagaagcagcttctgatagcagagtttcagaaacagcatgagaacttgacacggcagcaccaggctcagcttcaggagcatatcaaggaacttctagccataaaacagcaacaagaactcctagaaaaggagcagaaactggagcagcagaggcaagaacaggaagtagagaggcatcgcagagaacagcagcttcctcctctcagaggcaaagatagaggacgagaaagggcagtggcaagtacagaagtaaagcagaagcttcaagagttcctactgagtaaatcagcaacgaaagacactccaactaatggaaaaaatcattccgtgagccgccatcccaagctctggtacacggctgcccaccacacatcattggatcaaagctctccaccccttagtggaacatctccatcctacaagtacacattaccaggagcacaagatgcaaaggatgatttcccccttcgaaaaactgcctctgagcccaacttgaaggtgcggtccaggttaaaacagaaagtggcagagaggagaagcagccccttactcaggcggaaggatggaaatgttgtcacttcattcaagaagcgaatgtttgaggtgacagaatcctcagtcagtagcagttctccaggctctggtcccagttcaccaaacaatgggccaactggaagtgttactgaaaatgagacttcggttttgccccctacccctcatgccgagcaaatggtttcacagcaacgcattctaattcatgaagattccatgaacctgctaagtctttatacctctccttctttgcccaacattaccttggggcttcccgcagtgccatcccagctcaatgcttcgaattcactcaaagaaaagcagaagtgtgagacgcagacgcttaggcaaggtgttcctctgcctgggcagtatggaggcagcatcccggcatcttccagccaccctcatgttactttagagggaaagccacccaacagcagccaccaggctctcctgcagcatttattattgaaagaacaaatgcgacagcaaaagcttcttgtagctggtggagttcccttacatcctcagtctcccttggcaacaaaagagagaatttcacctggcattagaggtacccacaaattgccccgtcacagacccctgaaccgaacccagtctgcacctttgcctcagagcacgttggctcagctggtcattcaacagcaacaccagcaattcttggagaagcagaagcaataccagcagcagatccacatgaacaaactgctttcgaaatctattgaacaactgaagcaaccaggcagtcaccttgaggaagcagaggaagagcttcagggggaccaggcgatgcaggaagacagagcgccctctagtggcaacagcactaggagcgacagcagtgcttgtgtggatgacacactgggacaagttggggctgtgaaggtcaaggaggaaccagtggacagtgatgaagatgctcagatccaggaaatggaatctggggagcaggctgcttttatgcaacagcctttcctggaacccacgcacacacgtgcgctctctgtgcgccaagctccgctggctgcggttggcatggatggattagagaaacaccgtctcgtctccaggactcactcttcccctgctgcctctgttttacctcacccagcaatggaccgccccctccagcctggctctgcaactggaattgcctatgaccccttgatgctgaaacaccagtgcgtttgtggcaattccaccacccaccctgagcatgctggacgaatacagagtatctggtcacgactgcaagaaactgggctgctaaataaatgtgagcgaattcaaggtcgaaaagccagcctggaggaaatacagcttgttcattctgaacatcactcactgttgtatggcaccaaccccctggacggacagaagctggaccccaggatactcctaggtgatgactctcaaaagtttttttcctcattaccttgtggtggacttggggtggacagtgacaccatttggaatgagctacactcgtccggtgctgcacgcatggctgttggctgtgtcatcgagctggcttccaaagtggcctcaggagagctgaagaatgggtttgctgttgtgaggccccctggccatcacgctgaagaatccacagccatggggttctgcttttttaattcagttgcaattaccgccaaatacttgagagaccaactaaatataagcaagatattgattgtagatctggatgttcaccatggaaacggtacccagcaggccttttatgctgaccccagcatcctgtacatttcactccatcgctatgatgaagggaactttttccctggcagtggagccccaaatgaggttggaacaggccttggagaagggtacaatataaatattgcctggacaggtggccttgatcctcccatgggagatgttgagtaccttgaagcattcaggaccatcgtgaagcctgtggccaaagagtttgatccagacatggtcttagtatctgctggatttgatgcattggaaggccacacccctcctctaggagggtacaaagtgacggcaaaatgttttggtcatttgacgaagcaattgatgacattggctgatggacgtgtggtgttggctctagaaggaggacatgatctcacagccatctgtgatgcatcagaagcctgtgtaaatgcccttctaggaaatgagctggagccacttgcagaagatattctccaccaaagcccgaatatgaatgctgttatttctttacagaagatcattgaaattcaaagtatgtctttaaagttctcttaaaaattctaagcaggtaaaactaactaaaattatattgaaaattatagtacaaagaaacatttaaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("HDAC9");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 7, 18993869,
				PositionType.ZERO_BASED), "C", "T");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(22, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("3030C>T", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.SYNONYMOUS_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc003tta_2() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc003tta.2	chr7	-	63980254	64023505	63981538	64023332	4	63980254,64004084,64004683,64023302,	63982878,64004180,64004810,64023505,	Q8NEM1	uc003tta.2");
		this.builderForward
				.setSequence("aatcaggcacgcagctggagcggacaggacggcttccgggatttggcggggcctttgtctctagctgctgcgggagctccaggtctagtctttactgctctgtgtattctgctcctagaggcccagcctctgtgactccgttatctgcaggtattgggagatgcacagctaagatgccaggaccacctggaagcctagaaatgggaccactgacatttagggatgtggccatagaattctctctggaggagtggcaatgcctggacactgcacaacggaatttatataggaaagtgatgtttgagaactacagaaacctggtcttcctgggtattgctgtctctaagcctcacctgataacctgtttggagcaaggaaaagagccctggaataggaagagacaggagatggtagccaaacccccagttatatattctcatttcactgaagacctttggccagagcatagcataaaagattcttttcaaaaagtgatactgagaggatatggaaaatgtggacatgagaatttacaattaagaataagttgtaaaagtgtggatgagtctaaggtgttcaaagaaggttataatgaacttaaccaatgtttgagaactacccagagcaaaatatttcaatgtgataaatacgtgaaagtctttcataaattttcaaattcaaacagtcataagaaaagaaatactggaaagaaggttttcaaatgtaaagaatgtggcaaatcattttgcatgctttcacatctaacacaacatataagaattcacactagagagaattcttacaaatgtgaggaatgtggcaaagttcttaactggttctcagagcttattaaacataagggaattcatatgggagagaaaccctacaaatgtgaggaatgtggcaaagcctttaaccaatcctcaacccttattaaacataagaaaattcatattgaagagaaacccttcaaatgtgaagaatgtggcaaagcctttagtttattctcaatccttagtaaacataagataattcatactggagacaaaccttacaaatgtgatgaatgtcacaaagcctttaactggtttgcaacccttactaaccataagagaattcatactggagagaaacccttcaaatgtgaagaatgtggcaaagactttaaccagttttcaaaccttactaaacataagaaaattcatactggagagaaaccctacaaatgtgaagaatgtggcaaagcttttaaccagtttgcaaaccttactagacataagaaaattcatactggagagaaatcctacaaatgtgaagaatgcggcaaagcttttatacagtcctcaaaccttactgaacatatgagaattcatactggagagaaaccctacaaatgtgaagaatgtggcaaagcttttaatgggtgctccagccttactcgacataagagaattcacactagagagaatacctacaaatgtgaagaatgtggcaaaggctttactttattttcaacccttactaaccataaagtaattcatactggagagaaatcctacaaatgtgatgaatgtggcaatgtttttaactggcctgcaactcttgctaatcataagagaattcatgctagagagaaaccctacaaatgtgaagaatgtggcaaagcttttaaccggtcctcacaccttactagacataagaaaattcatactggtgagaaactctacaaacctgaaaaatgtgacaataattttgataacacctaaaacttttctaaacataaaagacaccatcctgttgagaaaccctaaaaatgtgaagaatttgacaaagccttccaatgattgttacacttgcttgtaggtaagataatttatattggagaaaactttataagtgtaaagaatgtgacaaaacttaaccaatactcacatcttattgcacaggaaagcatttttacttgagaaagattgtacaagtataaagaatgtgaaaaagtcattgatatcttttcacatcttactcaacaccagagtttatacttattaaaagcattataaatgcaattactgtcaaaagaaatttcagaaaacataacactttaaagtgaagaagcgtgtttattttgaagacaaacataacaaatataaggagcataggagggttgtagtacctttacttgtattacaaatcttattgtacgcattttgtactagagaaaaacactgaagcagttgctcaaactttgttcaacatcagggaatttatattggagaaaaatcctgcaaatgtaatgaatttggaaaaacattttttttcaaaaactacggcgtagaaaacatgaatttatattgaaatgtgtttttgcagatgcagtaagtatgaaaaatatttaatccaaaattgagtctatgtaaatattaaataatttacagtagaaataactaaggcactgacactttagacattacactaaaacagagtgttgagtataaaaaaatctataagttgttagattatttgtaaataactttaaaaggagtagaagattctttggagagttataattacattgaaagtatactttttttcttgaaaaaattatggcttatttgaaaagtaaataatgatgaaattcaactctcaaattactttatttcattcctattgtattcacatgtgaaagcatgtgataaattgttgctgtgtcagatataggagagattcttttttattagatgggcattatttatggttttttctgtggaagagtatgggcattaaaatgtaaggtttatgatgaaaatctaagtggagaggccctgttagtttacttttcatattgagtgatgcatgaggtaggtgtttggagtaatattctgcatcatagagaaaagcatttttaattttactttaaattaaatgtaattaaattactagtgaatcattttactaattgtacttttatgctataaatgcagtacatttgaaaatttttagattatgtataagcttaattttataattaaacatttctttaacatgttaag"
						.toUpperCase());
		this.builderForward.setGeneSymbol("ZNF680");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 7, 63981562,
				PositionType.ZERO_BASED), "A", "G");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(3, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("1569T>C", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.SYNONYMOUS_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc010lft_2() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc010lft.2	chr7	-	98771196	98805089	98771330	98805089	10	98771196,98775542,98779505,98782551,98785922,98790641,98792692,98793706,98800728,98805023,	98771417,98775689,98779688,98782785,98786186,98790724,98792961,98793789,98800863,98805089,	A9QM74	uc010lft.2");
		this.builderForward
				.setSequence("atgccgaccttagatgctccagaagagaggcggagaaaatttaagtaccgaggcaaagatgtgtctctgaggcgacagcagaggatggcggtcagtctggagctccgaaaggccaagaaagatgaacagaccttaaagagaaggaatatcacgagcttctgccctgacacaccttctgaaaaaacagccaaaggggtggcggtcagcctcactctgggtgaaataatcaaaggtgtgaatagctcagatccagtcctatgtttccaggccacccagacagccaggaaaatgctatcccaggaaaagaacccccctctgaaactggtcattgaagcgggcctcattcccaggatggtggagttcctgaagtcatcactttacccctgcttgcagtttgaggctgcctgggccctgaccaacatcgcttcagggacttcggagcagactcgtgccgtggtagaagggggagccatccagcccttgattgagctcctgtcttcctccaacgtggctgtgtgtgaacaggcagtgtgggctcttggtaatatagccggtgatggcccagagttcagagataacgtcatcacaagcaatgccatcccacatctcctagccttgatttcacccaccctgccgatcacatttctgcggaacatcacgtggaccttgtcgaatctgtgccgaaacaagaacccatacccttgcgacactgcggtgaagcagatactgccggccctccttcacctcctgcagcaccaggacagtgaggttctctcggatgcctgctgggcactgtcctacctcaccgacggctccaacaagcgcatcggccaagtggttaacacgggggtcctgcccaggctggtagtgctcatgaccagctcagaactcaatgtcttgactccttctctccgcaccgtggggaacattgtcacgggcacagatgagcagacgcagatggccattgatgcgggtatgctgaacgtgctcccccagctcctgcaacacaacaagccctccatccagaaggaggcagcctgggccctgagcaacgtagcagcggggccttgtcaccacatccagcagctgcttgcctacgacgtcttgcctcccttggtggctctgctaaaaaacggagaatttaaagtccagaaagaggctgtctggatggtggcgaactttgcaacaggggccaccatggatcagctgatccagctcgtccactctggggtcctggagccactggtgaatctgctcactgccccagatgttaaaattgttctcatcatccttgatgtcatctcttgcatcctccaggcggcagagaaacggtctgagaaggaaaacctgtgtcttctgatagaagaacttggtgggatcgatagaattgaggctttacagctgcatgagaaccgtcaaattggccagtcggctttgaacatcatcgagaagcactttggtgaggaagaagatgagagccaaactttactgagccaagtcatagaccaagattatgaatttatagattatgaatgcttagcaaaaaaatagccaagctccctacctcctaaaccaacaacccagtgctaaaggataacttctttaagaagcagcagtcctctatcttagtgtaacccaaatgtgaagcttttaaaacttgacattaataaaatgttcaacactttaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("KPNA7");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 7, 98782749,
				PositionType.ZERO_BASED), "C", "T");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(6, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("936G>A", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.SYNONYMOUS_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc003vtu_3() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc003vtu.3	chr7	-	137074384	137531609	137075965	137304662	33	137074384,137080343,137082122,137092620,137096915,137128822,137150653,137151647,137154295,137170119,137172355,137178528,137206611,137237114,137255920,137257510,137261967,137263015,137266595,137269954,137271842,137282592,137284568,137293711,137294280,137304569,137308228,137330217,137339477,137341211,137363302,137528133,137531207,	137076082,137080443,137082159,137092741,137096953,137128848,137150781,137151688,137154365,137170164,137172435,137178582,137206712,137237314,137256032,137257584,137262030,137263071,137266674,137270092,137271956,137282653,137284651,137293810,137294355,137304686,137308300,137330283,137339534,137341286,137363398,137528208,137531609,	E9PFX6	uc003vtu.3");
		this.builderForward
				.setSequence("gatggatgctgcgggaaggggctgccatttgctgcccctgccagcggcgcgcggacctgcccgcgctcctgcagccgccgccgccgccgccgccagcccgcccggcccctgcagcggcgccgcctgcgctccctccgcggccgccggagcgggcgccatgaaccccagctcctcggcgggagaggagaaaggggcgacgggcggcagcagcagcagcggaagcggcgccgggagctgctgcctgggcgccgagggcggcgcggacccgcggggcgcagggtcagccgcggcggcgggggccgctgccctggacgagcccgcggccgccggccagaaggagaaggacgaagcgctggaggagaagctgaggaacttaactttccggaagcaggtctcgtacagaatctgcacccctagagcaaatagtctctgtcgactgtaaagctgaaggtttgaaaaggcagtgcgagtgagctggagaatgccgtgaatggagaacacctgtggctggagaccaacgtctcgggagacctctgctaccttggagaggagaactgccaagtcagatttgcaaaatcagctctcaggaggaagtgtgcagtctgtaaaatcgtcgtccacaccgcctgcattgagcagctagaaaagattaatttcagatgtaaaccaacatttcgagaaggaggctcaaggtcaccaagagaaaattttgtacgtcatcactgggtgcacaggcgtcggcaggaggggaaatgtaagcagtgtggtaagggcttccagcaaaagttctccttccacagtaaagagattgtggctatcagctgttcctggtgcaagcaggcgtttcacaataaggtgacctgcttcatgctgcatcacattgaagaaccctgctccctgggggctcatgctgctgttattgtcccgcccacttggatcattaaggtgaagaaacctcagaactccctgaaggcttcaaatcggaagaagaagagaacaagctttaaaagaaaagccagtaaaagagggatggaacaggaaaacaaaggtcgtccttttgtgataaaacccatctcttctcctctcatgaaacccttgcttgtatttgtgaatcccaagagtggaggcaaccagggaaccaaagtcctgcagatgttcatgtggtacctgaatccacggcaagtctttgatctttctcaggaagggccaaaagatgcgcttgaattgtataggaaagtaccaaatctgcgaattctggcctgtggtggggatggaacggtgggctggatcctttccatcctggatgaactgcagctgagccctcagcctcctgtgggggtccttcctctggggactgggaatgacctggctcgaactctcaactggggagggggctacactgatgaacctgtttctaagatcctgtgtcaagtggaagatgggacagttgtacagctagatcgctggaacctccatgtggaaagaaaccccgacttgcctccagaagaacttgaagatggcgtatgtaagctccctctgaatgttttcaataactacttcagccttggatttgatgcccatgtcacactggagttccatgaatccagagaagcaaatccagagaaattcaacagtcgttttcgaaataaaatgttctatgcaggggcagctttttctgacttcctacagagaagttctagagatctatccaaacatgttaaagttgtttgtgatggaacagatctcaccccaaagattcaggaactgaagttccagtgtatagtatttttaaatatacccagatattgtgctggcacaatgccctggggaaacccaggtgatcaccatgatttcgaacctcagcgtcatgatgatggttatattgaagtcattggatttaccatggcctctttggcagccctgcaagttgggggccatggagagaggctacaccagtgtcgagaagtcatgcttctaacttacaaatccatccccatgcaagtggatggggagccctgtaggttggccccagctatgattcggatctccctgaggaatcaggccaacatggtacagaagagcaagaggagaacatccatgcctttactcaatgatccccagtctgtcccagatcgtctgaggatccgggtgaacaaaatcagtttacaagactatgaaggattccactatgacaaggagaaactccgagaagcttctatttcagactggttaagaaccattgctggggaactagtgcagtcatttggagcgatacctctgggtattctagttgtgcgtggagactgtgatttggagacttgccgtatgtacatagaccgcctacaggaggacctacagtcagtttcttctggctcccagagagttcattaccaggaccatgaaacctccttccccagggctctctcagcacagaggctctctcctcggtggtgcttcctagatgcaacttctgctgatcgcttttatcgaatagacagatctcaggaacatttgcactttgtgatggagatttcccaagatgagatttttattctggacccagatatggtggtgtcacagccggcggggacacctccgggcatgcctgacctggtggtggaacaagcctcggggtcaccagtctcttcagaagatcatgcaattttgcaggcagtaatagctggtgatcttatgaagctaatagaaagctataaaaatggaggcagtctgctaattcagggaccagaccactgttcactccttcactacgcagctaaaaccggcaacggggagattgtgaaatatatccttgaccacggaccttccgagttattggatatggcagacagtgaaacgggtgagactgcactgcacaaggctgcctgccagcggaaccgggctgtgtgccagcttctggtggatgcaggagcatctctgagaaagacggactccaagggtaagacacctcaagaaagagcacagcaggctggggacccagacttggctgcttacctagaaagccgtcagaactataaggtcattggccatgaggacctggaaactgctgtttgaccctggtattcgggcaaagaggacatgagcaagcgtatcacatctgccctccctgcaattgggcagctcccctggaagaagctgatggaattcatatatctgtctctctcctgcaagaatctacctgagaccatgccactagcttttaagggctaccaagatgtacaacagaacatgatagcccattgagaaggaggcaggatacctggagatttgtggaatacagtacgagttccacaaaatttgatccttattgcttccagcaagtagcatgaacttctgtgttcacctgtataatttattttaaagattcaaaggatgttcgtataaatggcactgctccatcctccccctatgcattggtttttttccctgtaccatacaattctactgtaactacccatcaacttaaagaaaaatattatctcttctctttacattcagtcttggaagaccacaagattgtctgaaggccttctaaaaccttctgaatgtcctgcagaaatataactgtaaaaccacttccatttctaagactaaatatatcaagactatttagtgactctctctgcatgtccccctcacccgccaaccctccgtttcattatataggagctgggaagtgccacatggataatgtcaacttgtgtgctatatctctgaggaatggtgaggtggcatgggagatgtctgtgcttggaggtacctcagagaggtaacccaggggtcagcccaggctgctgggctgtagccaatagccatgcaggactggttcagcttgggctgtctgtacagctccgtactgcctatgtgtagccatctttgccttttgctgcaatagaagatgagcaaaggattaaacagaggcccacagctagtttgcagaaccactcaattttaagtgctgtttaaattgcagagcaaataatcctgtgtgggaactgtggttacaggaaatggagcactctaacaatgtttacttctaaactttgttgaatgataatagaaagcaccctaattgacttggaaaaaaaaaacagcaaaagcaaaagtagcaacatatgtcaacatatgtcactgaaataggaaacagtcattggaatgttgcacagaggctaataactatagactgttggatacagtggtgagaggagccccattttaggtctttcttttaggtttttggttttcattactccaagtaatccttgacccaagaacaaaggcttgttgtatgagttccactgccagatttatgggatgcctggatcattcagaaggatgcttcaactattatttgtcaggtccaaaggtcgtacttaataaccccattttctatgtatggggtagtctaatatattattttatctactttatttttcccttttcagaaagtccttagtgcaaaccaccattggaatctagccagaaatgtctgtcagatagttagaattgtaacatctaaacctgccacggatcgaatggtacttacaggtacctctcttagggactctgtgatccctaaaatatcagaagaaaatgtctgtctttctgtccaaatatctacttgacttgggggta"
						.toUpperCase());
		this.builderForward.setGeneSymbol("DGKI");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 7, 137128829,
				PositionType.ZERO_BASED), "T", "C");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(27, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("1785A>G", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.SYNONYMOUS_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc002lzv_4() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc002lzv.4	chr19	+	4247110	4269085	4247143	4268693	8	4247110,4249224,4251023,4254351,4258238,4261990,4267620,4268580,	4247167,4249325,4251168,4254486,4258420,4262111,4267771,4269085,	Q9BW85	uc002lzv.4");
		this.builderForward
				.setSequence("ctcaggtgcttgcgaggtgatcagaaggcaaagatgtcggagcgaaaagtattaaacaaatactacccgccggactttgacccatcaaagatccccaaactcaagctccccaaagaccggcagtacgtggtgcggctgatggcccccttcaacatgaggtgtaagacgtgcggagaatacatctacaaggggaagaaattcaatgctcggaaggagacggtgcagaacgaggtctacctgggcctgcccatcttccgcttttacatcaagtgcacgcgctgcctggcagagatcaccttcaagacagaccctgaaaacacagactacaccatggagcatggagccacgcggaatttccaggctgagaagctcctggaggaggaggagaagagggtgcagaaggagcgggaggacgaggagctgaacaaccccatgaaggtgctggagaaccggaccaaggactccaagctggagatggaggtgctggagaacctccaggagctgaaagacctgaaccagcggcaggcgcacgtggacttcgaggctatgctgaggcagcaccgcctgtcggaggaggagcggcggaggcagcagcaggaggaggacgagcaggagaccgcggccctgttggaggaagccagaaagcgaagactgctggaggactccgactcagaggatgaggctgctccctcgcccctgcagccagcccttcggcccaaccccaccgccatcctggatgaggccccaaagcccaagaggaaggtggaggtctgggagcagagcgttggcagcctgggcagccggcccccgctgtcgaggctggtcgtggtgaagaaggcaaaggccgacccggactgcagcaacgggcagcctcaggcggcccccaccccaggagccccgcagaacaggaaggaggccaaccctacacccctgacgcctggcgcgtcctccctgagccaactgggtgcatacctggacagtgacgacagcaacggcagcaactgagccctcccaggaccccctcacggggtcaaagtcacacgtccagcttcagccacattgaggccagcattgctggtggtcagggcaggaggccttggcgtgactggaggccggacagacaagcgccagcgtgctccaacacatagggccaccaggggcctcagccccaggaggtcccttctctgtgccctcaccagcctctcaacacctcggggacccctgctgctcctgcccccacctgtcactgtgcttagggctgcaacatccctggagcagcttccaacactacttcagggtggcagtgtttggggcactgggcgagcctgccggcctctagatggcctcatctcttccttccacaaactgtctagaaccaataaaaggaaacctgccaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("CCDC94");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 19, 4251068,
				PositionType.ZERO_BASED), "T", "C");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(2, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("171T>C", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.SYNONYMOUS_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc003apg_3() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc003apg.3	chr22	-	36677322	36784063	36678713	36745281	41	36677322,36680138,36680448,36681166,36681703,36681910,36682763,36684297,36684772,36685130,36688031,36689374,36689804,36690137,36690977,36691550,36692888,36694964,36696172,36696896,36697579,36698613,36700040,36701078,36701975,36702459,36705326,36708093,36710189,36712561,36714251,36715584,36716264,36716842,36717802,36718473,36722612,36723505,36737414,36744948,36783851,	36678831,36680311,36680557,36681375,36681827,36681999,36682892,36684459,36684985,36685343,36688280,36689527,36689909,36690344,36691122,36691763,36693060,36695088,36696310,36697103,36697711,36698722,36700201,36701148,36702097,36702653,36705441,36708267,36710363,36712714,36714370,36715680,36716408,36716941,36717866,36718566,36722706,36723533,36737571,36745300,36784063,	P35579	uc003apg.3");
		this.builderForward
				.setSequence("gagggcggggcgggaaggcggcgaggagccgagctgggtgcggtgaggcgcgcagatcaccgcggttcctgggcagggcacggaaggctaagcaaggctgacctgctgcagctcccgcctcgtgcgctcgccccacccggccgccgcccgagcgctcgagaaagtcctctcgggagaagcagcgcctgttcccggggcagatccaggttcaggtcctggctataagtcaccatggcacagcaagctgccgataagtatctctatgtggataaaaacttcatcaacaatccgctggcccaggccgactgggctgccaagaagctggtatgggtgccttccgacaagagtggctttgagccagccagcctcaaggaggaggtgggcgaagaggccatcgtggagctggtggagaatgggaagaaggtgaaggtgaacaaggatgacatccagaagatgaacccgcccaagttctccaaggtggaggacatggcagagctcacgtgcctcaacgaagcctcggtgctgcacaacctcaaggagcgttactactcagggctcatctacacctattcaggcctgttctgtgtggtcatcaatccttacaagaacctgcccatctactctgaagagattgtggaaatgtacaagggcaagaagaggcacgagatgccccctcacatctatgccatcacagacaccgcctacaggagtatgatgcaagaccgagaagatcaatccatcttgtgcactggtgaatctggagctggcaagacggagaacaccaagaaggtcatccagtatctggcgtacgtggcgtcctcgcacaagagcaagaaggaccagggcgagctggagcggcagctgctgcaggccaaccccatcctggaggccttcgggaacgccaagaccgtgaagaatgacaactcctcccgcttcggcaaattcattcgcatcaactttgatgtcaatggctacattgttggagccaacattgagacttatcttttggagaaatctcgtgctatccgccaagccaaggaagaacggaccttccacatcttctattatctcctgtctggggctggagagcacctgaagaccgatctcctgttggagccgtacaacaaataccgcttcctgtccaatggacacgtcaccatccccgggcagcaggacaaggacatgttccaggagaccatggaggccatgaggattatgggcatcccagaagaggagcaaatgggcctgctgcgggtcatctcaggggttcttcagctcggcaacatcgtcttcaagaaggagcggaacactgaccaggcgtccatgcccgacaacacagctgcccaaaaggtgtcccatctcttgggtatcaatgtgaccgatttcaccagaggaatcctcaccccgcgcatcaaggtgggacgggattacgtccagaaggcgcagactaaagagcaggctgactttgccatcgaggccttggccaaggcgacctatgagcggatgttccgctggctggtgctgcgcatcaacaaggctctggacaagaccaagaggcagggcgcctccttcatcgggatcctggacattgccggcttcgagatctttgatctgaactcgtttgagcagctgtgcatcaattacaccaatgagaagctgcagcagctcttcaaccacaccatgttcatcctggagcaggaggagtaccagcgcgagggcatcgagtggaacttcatcgactttggcctcgacctgcagccctgcatcgacctcattgagaagccagcaggccccccgggcattctggccctgctggacgaggagtgctggttccccaaagccaccgacaagagcttcgtggagaaggtgatgcaggagcagggcacccaccccaagttccagaagcccaagcagctgaaggacaaagctgatttctgcattatccactatgccggcaaggtggattacaaagctgacgagtggctgatgaagaacatggatcccctgaatgacaacatcgccacactgctccaccagtcctctgacaagtttgtctcggagctgtggaaggatgtggaccgcatcatcggcctggaccaggtggccggcatgtcggagaccgcactgcccggggccttcaagacgcggaagggcatgttccgcactgtggggcagctttacaaggagcagctggccaagctgatggctacgctgaggaacacgaaccccaactttgtccgctgcatcatccccaaccacgagaagaaggccggcaagctggacccgcatctcgtgctggaccagctgcgctgcaacggtgttctcgagggcatccgtatctgccgccagggcttccccaacagggtggtcttccaggagtttcggcagagatatgagatcctgactccaaactccattcccaagggtttcatggacgggaagcaggcgtgcgtgctcatgataaaagccctggagctcgacagcaatctgtaccgcattggccagagcaaagtcttcttccgtgccggtgtgctggcccacctggaggaggagcgagacctgaagatcaccgacgtcatcatagggttccaggcctgctgcaggggctacctggccaggaaagcatttgccaagcggcagcagcagcttaccgccatgaaggtcctccagcggaactgcgctgcctacctgaagctgcggaactggcagtggtggcggctcttcaccaaggtcaagccgctgctgcaggtgagccggcaggaggaggagatgatggccaaggaggaggagctggtgaaggtcagagagaagcagctggctgcggagaacaggctcacggagatggagacgctgcagtctcagctcatggcagagaaattgcagctgcaggagcagctccaggcagaaaccgagctgtgtgccgaggctgaggagctccgggcccgcctgaccgccaagaagcaggaattagaagagatctgccatgacctagaggccagggtggaggaggaggaggagcgctgccagcacctgcaggcggagaagaagaagatgcagcagaacatccaggagcttgaggagcagctggaggaggaggagagcgcccggcagaagctgcagctggagaaggtgaccaccgaggcgaagctgaaaaagctggaggaggagcagatcatcctggaggaccagaactgcaagctggccaaggaaaagaaactgctggaagacagaatagctgagttcaccaccaacctcacagaagaggaggagaaatctaagagcctcgccaagctcaagaacaagcatgaggcaatgatcactgacttggaagagcgcctccgcagggaggagaagcagcgacaggagctggagaagacccgccggaagctggagggagactccacagacctcagcgaccagatcgccgagctccaggcccagatcgcggagctcaagatgcagctggccaagaaagaggaggagctccaggccgccctggccagagtggaagaggaagctgcccagaagaacatggccctcaagaagatccgggagctggaatctcagatctctgaactccaggaagacctggagtctgagcgtgcttccaggaataaagctgagaagcagaaacgggaccttggggaagagctagaggctctgaaaacagagttggaggacacgctggattccacagctgcccagcaggagctcaggtcaaaacgtgagcaggaggtgaacatcctgaagaagaccctggaggaggaggccaagacccacgaggcccagatccaggagatgaggcagaagcactcacaggccgtggaggagctggcggagcagctggagcagacgaagcgggtgaaagcaaacctcgagaaggcaaagcagactctggagaacgagcggggggagctggccaacgaggtgaaggtgctgctgcagggcaaaggggactcggagcacaagcgcaagaaagtggaggcgcagctgcaggagctgcaggtcaagttcaacgagggagagcgcgtgcgcacagagctggccgacaaggtcaccaagctgcaggtggagctggacaacgtgaccgggcttctcagccagtccgacagcaagtccagcaagctcaccaaggacttctccgcgctggagtcccagctgcaggacactcaggagctgctgcaggaggagaaccggcagaagctgagcctgagcaccaagctcaagcaggtggaggacgagaagaattccttccgggagcagctggaggaggaggaggaggccaagcacaacctggagaagcagatcgccaccctccatgcccaggtggccgacatgaaaaagaagatggaggacagtgtggggtgcctggaaactgctgaggaggtgaagaggaagctccagaaggacctggagggcctgagccagcggcacgaggagaaggtggccgcctacgacaagctggagaagaccaagacgcggctgcagcaggagctggacgacctgctggtggacctggaccaccagcgccagagcgcgtgcaacctggagaagaagcagaagaagtttgaccagctcctggcggaggagaagaccatctctgccaagtatgcagaggagcgcgaccgggctgaggcggaggcccgagagaaggagaccaaggctctgtcgctggcccgggccctggaggaagccatggagcagaaggcggagctggagcggctcaacaagcagttccgcacggagatggaggaccttatgagctccaaggatgatgtgggcaagagtgtccacgagctggagaagtccaagcgggccctagagcagcaggtggaggagatgaagacgcagctggaagagctggaggacgagctgcaggccaccgaagatgccaagctgcggttggaggtcaacctgcaggccatgaaggcccagttcgagcgggacctgcagggccgggacgagcagagcgaggagaagaagaagcagctggtcagacaggtgcgggagatggaggcagagctggaggacgagaggaagcagcgctcgatggcagtggccgcccggaagaagctggagatggacctgaaggacctggaggcgcacatcgactcggccaacaagaaccgggacgaagccatcaaacagctgcggaagctgcaggcccagatgaaggactgcatgcgcgagctggatgacacccgcgcctctcgtgaggagatcctggcccaggccaaagagaacgagaagaagctgaagagcatggaggccgagatgatccagttgcaggaggaactggcagccgcggagcgtgccaagcgccaggcccagcaggagcgggatgagctggctgacgagatcgccaacagcagcggcaaaggagccctggcgttagaggagaagcggcgtctggaggcccgcatcgcccagctggaggaggagctggaggaggagcagggcaacacggagctgatcaacgaccggctgaagaaggccaacctgcagatcgaccagatcaacaccgacctgaacctggagcgcagccacgcccagaagaacgagaatgctcggcagcagctggaacgccagaacaaggagcttaaggtcaagctgcaggagatggagggcactgtcaagtccaagtacaaggcctccatcaccgccctcgaggccaagattgcacagctggaggagcagctggacaacgagaccaaggagcgccaggcagcctgcaaacaggtgcgtcggaccgagaagaagctgaaggatgtgctgctgcaggtggatgacgagcggaggaacgccgagcagtacaaggaccaggccgacaaggcatctacccgcctgaagcagctcaagcggcagctggaggaggccgaagaggaggcccagcgggccaacgcctcccgccggaaactgcagcgcgagctggaggacgccactgagacggccgatgccatgaaccgcgaagtcagctccctaaagaacaagctcaggcgcggggacctgccgtttgtcgtgccccgccgaatggcccggaaaggcgccggggatggctccgacgaagaggtagatggcaaagcggatggggctgaggccaaacctgccgaataagcctcttctcctgcagcctgagatggatggacagacagacaccacagcctccccttcccagaccccgcagcacgcctctccccaccttcttgggactgctgtgaacatgcctcctcctgccctccgccccgtccccccatcccgtttccctccaggtgttgttgagggcatttggcttcctctgctgcatccccttccagctccctcccctgctcagaatctgataccaaagagacagggcccgggcccaggcagagagcgaccagcaggctcctcagccctctcttgccaaaaagcacaagatgttgaggcgagcagggcaggcccccggggaggggccagagttttctatgaatctatttttcttcagactgaggccttttggtagtcggagcccccgcagtcgtcagcctccctgacgtctgccaccagcgcccccactcctcctcctttctttgctgtttgcaatcacacgtggtgacctcacacacctctgccccttgggcctcccactcccatggctctgggcggtccagaaggagcaggccctgggcctccacctctgtgcagggcacagaaggctggggtggggggaggagtggattcctccccaccctgtcccaggcagcgccactgtccgctgtctccctcctgattctaaaatgtctcaagtgcaatgccccctcccctcctttaccgaggacagcctgcctctgccacagcaaggctgtcggggtcaagctggaaaggccagcagccttccagtggcttctcccaacactcttggggaccaaatatatttaatggttaagggacttgtcccaagtctgacagccagagcgttagaggggccagcggccctcccaggcgatcttgtgtctactctaggactgggcccgagggtggtttacctgcaccgttgactcagtatagtttaaaaatctgccacctgcacaggtatttttgaaagcaaaataaggttttcttttttcccctttcttgtaataaatgataaaattccgagtctttctcactgcctttgtttagaagagagtagctcgtcctcactggtctacactggttgccgaatttacttgtattcctaactgttttgtatatgctgcattgagacttacggcaagaaggcattttttttttttaaaggaaacaaactctcaaatcatgaagtgatataaaagctgcatatgcctacaaagctctgaattcaggtcccagttgctgtcacaaaggagtgagtgaaactcccaccctacccccttttttatataataaaagtgccttagcatgtgttgcagctgtcaccactacagtaagctggtttacagatgttttccactgagcatcacaataaagagaaccatgtgctacga"
						.toUpperCase());
		this.builderForward.setGeneSymbol("MYH9");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 22, 36691606,
				PositionType.ZERO_BASED), "A", "C");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(25, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("3429T>G", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.SYNONYMOUS_VARIANT), annotation1.getEffects());
	}

	//
	// Various UTR Variants
	//

	@Test
	public void testRealWorldCase_uc003gpr_1_first() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc003gpr.1	chr4	+	20255234	20620788	20255438	20620632	37	20255234,20258294,20259490,20270432,20469374,20482338,20487822,20490441,20493383,20512117,20512688,20521004,20525382,20525636,20526771,20530571,20533606,20535194,20541063,20543075,20544116,20547653,20550110,20550679,20552449,20555427,20568884,20569140,20570487,20591266,20597319,20598037,20599887,20611635,20618532,20619061,20620390,	20255617,20258366,20259562,20270504,20469446,20482410,20487894,20490605,20493522,20512189,20512760,20521076,20525526,20525800,20526795,20530722,20533681,20535338,20541207,20543242,20544249,20547722,20550182,20550751,20552521,20555591,20569009,20569238,20570627,20591360,20597457,20598278,20600018,20611790,20618821,20619273,20620788,	O94813	uc003gpr.1");
		this.builderForward
				.setSequence("cagagcagggtggagagggcggtgggaggcgtgtgcctgagtgggctctactgccttgttccatattattttgtgcacattttccctggcactctgggttgctagccccgccgggcactgggcctcagacactgcgcggttccctcggagcagcaagctaaagaaagcccccagtgccggcgaggaaggaggcggcggggaaagatgcgcggcgttggctggcagatgctgtccctgtcgctggggttagtgctggcgatcctgaacaaggtggcaccgcaggcgtgcccggcgcagtgctcttgctcgggcagcacagtggactgtcacgggctggcgctgcgcagcgtgcccaggaatatcccccgcaacaccgagagactggatttaaatggaaataacatcacaagaattacgaagacagattttgctggtcttagacatctaagagttcttcagcttatggagaataagattagcaccattgaaagaggagcattccaggatcttaaagaactagagagactgcgtttaaacagaaatcaccttcagctgtttcctgagttgctgtttcttgggactgcgaagctatacaggcttgatctcagtgaaaaccaaattcaggcaatcccaaggaaagctttccgtggggcagttgacataaaaaatttgcaactggattacaaccagatcagctgtattgaagatggggcattcagggctctccgggacctggaagtgctcactctcaacaataacaacattactagactttctgtggcaagtttcaaccatatgcctaaacttaggacttttcgactgcattcaaacaacctgtattgtgactgccacctggcctggctctccgactggcttcgccaaaggcctcgggttggtctgtacactcagtgtatgggcccctcccacctgagaggccataatgtagccgaggttcaaaaacgagaatttgtctgcagtggtcaccagtcatttatggctccttcttgtagtgttttgcactgccctgccgcctgtacctgtagcaacaatatcgtagactgtcgtgggaaaggtctcactgagatccccacaaatcttccagagaccatcacagaaatacgtttggaacagaacacaatcaaagtcatccctcctggagctttctcaccatataaaaagcttagacgaattgacctgagcaataatcagatctctgaacttgcaccagatgctttccaaggactacgctctctgaattcacttgtcctctatggaaataaaatcacagaactccccaaaagtttatttgaaggactgttttccttacagctcctattattgaatgccaacaagataaactgccttcgggtagatgcttttcaggatctccacaacttgaaccttctctccctatatgacaacaagcttcagaccatcgccaaggggaccttttcacctcttcgggccattcaaactatgcatttggcccagaacccctttatttgtgactgccatctcaagtggctagcggattatctccataccaacccgattgagaccagtggtgcccgttgcaccagcccccgccgcctggcaaacaaaagaattggacagatcaaaagcaagaaattccgttgttcagctaaagaacagtatttcattccaggtacagaagattatcgatcaaaattaagtggagactgctttgcggatctggcttgccctgaaaagtgtcgctgtgaaggaaccacagtagattgctctaatcaaaagctcaacaaaatcccggagcacattccccagtacactgcagagttgcgtctcaataataatgaatttaccgtgttggaagccacaggaatctttaagaaacttcctcaattacgtaaaataaactttagcaacaataagatcacagatattgaggagggagcatttgaaggagcatctggtgtaaatgaaatacttcttacgagtaatcgtttggaaaatgtgcagcataagatgttcaagggattggaaagcctcaaaactttgatgttgagaagcaatcgaataacctgtgtggggaatgacagtttcataggactcagttctgtgcgtttgctttctttgtatgataatcaaattactacagttgcaccaggggcatttgatactctccattctttatctactctaaacctcttggccaatccttttaactgtaactgctacctggcttggttgggagagtggctgagaaagaagagaattgtcacgggaaatcctagatgtcaaaaaccatacttcctgaaagaaatacccatccaggatgtggccattcaggacttcacttgtgatgacggaaatgatgacaatagttgctccccactttctcgctgtcctactgaatgtacttgcttggatacagtcgtccgatgtagcaacaagggtttgaaggtcttgccgaaaggtattccaagagatgtcacagagttgtatctggatggaaaccaatttacactggttcccaaggaactctccaactacaaacatttaacacttatagacttaagtaacaacagaataagcacgctttctaatcagagcttcagcaacatgacccagctcctcaccttaattcttagttacaaccgtctgagatgtattcctcctcgcacctttgatggattaaagtctcttcgattactttctctacatggaaatgacatttctgttgtgcctgaaggtgctttcaatgatctttctgcattatcacatctagcaattggagccaaccctctttactgtgattgtaacatgcagtggttatccgactgggtgaagtcggaatataaggagcctggaattgctcgttgtgctggtcctggagaaatggcagataaacttttactcacaactccctccaaaaaatttacctgtcaaggtcctgtggatgtcaatattctagctaagtgtaacccctgcctatcaaatccgtgtaaaaatgatggcacatgtaatagtgatccagttgacttttaccgatgcacctgtccatatggtttcaaggggcaggactgtgatgtcccaattcatgcctgcatcagtaacccatgtaaacatggaggaacttgccacttaaaggaaggagaagaagatggattctggtgtatttgtgctgatggatttgaaggagaaaattgtgaagtcaacgttgatgattgtgaagataatgactgtgaaaataattctacatgtgtcgatggcattaataactacacatgcctttgcccacctgagtatacaggtgagttgtgtgaggagaagctggacttctgtgcccaggacctgaacccctgccagcacgattcaaagtgcatcctaactccaaagggattcaaatgtgactgcacaccagggtacgtaggtgaacactgcgacatcgattttgacgactgccaagacaacaagtgtaaaaacggagcccactgcacagatgcagtgaacggctatacgtgcatatgccccgaaggttacagtggcttgttctgtgagttttctccacccatggtcctccctcgtaccagcccctgtgataattttgattgtcagaatggagctcagtgtatcgtcagaataaatgagccaatatgtcagtgtttgcctggctatcagggagaaaagtgtgaaaaattggttagtgtgaattttataaacaaagagtcttatcttcagattccttcagccaaggttcggcctcagacgaacataacacttcagattgccacagatgaagacagcggaatcctcctgtataagggtgacaaagaccatatcgcggtagaactctatcgggggcgtgttcgtgccagctatgacaccggctctcatccagcttctgccatttacagtgtggagacaatcaatgatggaaacttccacattgtggaactacttgccttggatcagagtctctctttgtccgtggatggtgggaaccccaaaatcatcactaacttgtcaaagcagtccactctgaattttgactctccactctatgtaggaggcatgccagggaagagtaacgtggcatctctgcgccaggcccctgggcagaacggaaccagcttccacggctgcatccggaacctttacatcaacagtgagctgcaggacttccagaaggtgccgatgcaaacaggcattttgcctggctgtgagccatgccacaagaaggtgtgtgcccatggcacatgccagcccagcagccaggcaggcttcacctgcgagtgccaggaaggatggatggggcccctctgtgaccaacggaccaatgacccttgccttggaaataaatgcgtacatggcacctgcttgcccatcaatgcgttctcctacagctgtaagtgcttggagggccatggaggtgtcctctgtgatgaagaggaggatctgtttaacccatgccaggcgatcaagtgcaagcacgggaagtgcaggctttcaggtctggggcagccctactgtgaatgcagcagtggatacacgggggacagctgtgatcgagaaatctcttgtcgaggggaaaggataagagattattaccaaaagcagcagggctatgctgcttgccaaacaaccaagaaggtgtcccgattagagtgcagaggtgggtgtgcaggagggcagtgctgtggaccgctgaggagcaagcggcggaaatactctttcgaatgcactgacggctcctcctttgtggacgaggttgagaaagtggtgaagtgcggctgtacgaggtgtgtgtcctaaacacactcccggcagctctgtctttggaaaaggttgtatacttcttgaccatgtgggactaatgaatgcttcatagtggaaatatttgaaatatattgtaaaatacagaacagacttatttttattatgagaataaagactttttttctgcatttg"
						.toUpperCase());
		this.builderForward.setGeneSymbol("SLIT2");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 4, 20620682,
				PositionType.ZERO_BASED), "G", "A");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(36, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("*51G>A", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.THREE_PRIME_UTR_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc001vjy_2() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001vjy.2	chr13	+	76445173	76457948	76445262	76457238	4	76445173,76447792,76447987,76457185,	76445379,76447870,76448108,76457948,	F2Z398	uc001vjy.2");
		this.builderForward
				.setSequence("ttttattctttttagatgattatcccggggctcctctgagaaaagtaggagtagattgctggaattaccatgggttaaaatgaaagaggatgacctggctggacaaaggcgtatggactcaggaagacgagaactcctgctccttctctgaatcggattttcctggctgtagagaccagatcaacccttccattccatcgatttggaccgcagtttcgggaatgatgatttcgctggaagtgcggtggtggataaaggggaagcagggttacgtaatttcactgggtcacgctctgtcacctaggctggagtgcagtggaacattctcagctcactgcatcctcggcctcccagggggctcaagctatcctcccgcctcagtctcccaagtagttgggaccacagctttgtatctggtggaggaggcttgggccgaagctggtaaaatgagaagttaggggaggatgtcctcagagagctggaagatcacagtcctcattgtcacaaccgatacagtgttgatagggacaacatatgccttggaatgtttcaaatactttgccacaattaagggacactgaacaggaactcagttttggaaaacaaagtggaatttctggaaactgctagcataaaagctttgacagggtcacatgcagattagcataattttattacattttctattttagtcctgtgagatactaatttggatttcattgtgcccattgagcggcttttgttctttggctgtgagtttaattatttagttatttatgagtgcatctgggcataagtgccacagttttatttcggtgacaaatttatggtcagtgaattttagtaattaatgaataaattatttctcctccataaatgcccctgcaatttctgattttcataacaattcataatacttaagaaaaattctttattagttgaaaaagccaatgctaatggaatcaggaaaaggagatttcaaattgctttaattaactgtattaattaatatccaatatgtgtaagatattgaattggctttggttgtctctcttagcatttagttgcctaaaatctgtttaacatatgaatatactgtgttaaaataaattctaattcttttgtgaagtaaatggcaagaataaaaggatctcctttataatcttta"
						.toUpperCase());
		this.builderForward.setGeneSymbol("C13orf45");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 13, 76445188,
				PositionType.ZERO_BASED), "A", "G");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(0, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("-74A>G", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FIVE_PRIME_UTR_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc010pcf_2() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc010pcf.2	chr1	+	150480486	150484220	150482208	150483692	6	150480486,150482006,150482136,150482438,150482577,150483351,	150480755,150482057,150482238,150482478,150482658,150484220,	Q16610-3	uc010pcf.2");
		this.builderForward
				.setSequence("agaggaggagcagctgggactgagtcatggcaggaagctgaggagggcgggagatcacaccagacaattataaaagaagagctggtcctgaagctcacaaccgtaacagccaccagacaagcttcagtggccggcccttcacatccagacttgcctgagaggacccacctctgagtgtccagtggtcagttgccccaggatggggaccacagccagagcagccttggtcttgacctatttggctgttgcttctgctgcctctgagggaggcttcacggctacaggacagaggcagctgaggccagagcactttcaagaagttggctacgcagctcccccctccccacccctatcccgaagcctccccatggatcaccctgactcctctcagcatggccctccctttgagggacagagtcaaggaaaagctgctacctgcccaactccctgctgaaaaggaagtgggtccccctctccctcaggaagctgtccccctccaaaaagagctgccctctctccagcaccccaatgaacagaaggaaggaacgccagctccatttggggaccagagccatccagaacctgagtcctggaatgcagcccagcactgccaacaggaccggtcccaagggggctggggccaccggctggatggcttcccccctgggcggccttctccagacaatctgaaccaaatctgccttcctaaccgtcagcatgtggtatatggtccctggaacctaccacagtccagctactcccacctcactcgccagggtgagaccctcaatttcctggagattggatattcccgctgctgccactgccgcagccacacaaaccgcctagagtgtgccaaacttgtggtaaggttgggttcttgatgccggggggtgtcctttaaccccagacagtatgtgtgttttaagggttagagcactaggcctgggtctggaggaacctcaggtcccgttcactggccctaccctgggcctccccaatgtgccaggggagcagaggacaaccactgtctcttctttatctgcctgcccagtgtcctttcctggagcctgggaggaaggcaggaatgtggaaagtgggctgatcctcccctcttgctctagtgggaggaagcaatgagccgattctgtgaggccgagttctcggtcaagacccgaccccactggtgctgcacgcggcagggggaggctcggttctcctgcttccaggaggaagctccccagccacactaccagctccgggcctgccccagccatcagcctgatatttcctcgggtcttgagctgcctttccctcctggggtgcccacattggacaatatcaagaacatctgccacctgaggcgcttccgctctgtgccacgcaacctgccagctactgaccccctacaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("ECM1");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 150483839,
				PositionType.ZERO_BASED), "C", "T");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(5, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("*148C>T", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.THREE_PRIME_UTR_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc001ibf_1() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001ibf.1	chr1	+	245318286	245866428	245318726	245865908	15	245318286,245319583,245530135,245582880,245704068,245765878,245770952,245772567,245775094,245809422,245847534,245848706,245861407,245862188,245865759,	245318789,245319985,245530669,245583047,245704252,245766085,245771046,245772830,245775278,245809582,245847697,245852109,245861610,245862339,245866428,	Q2KJY2	uc001ibf.1");
		this.builderForward
				.setSequence("agttaccaagctcggtgaaggagacaagttcccacagctgactcggctcggctctcccaccttcccggcagcggccgcgagccctgattgtatccctcgctttcctcgtgggggagcacggactgacttggctgaagaaaatgccagttctgtggatgtggccgtgacaagaggacgtgcggctggaagaggcagaaggggacgaggaaaagcatgctttgaagagaagaataaaccagcgaccccaaccctttctgcaaattggtgctattacttgtgggatccatttgctttcattccctcccaccccaccgctgaagaaaccttgccctgagggctgagagccagccccctgcagccgggggacgcttctgggttggaggaccttctggatgtagcgtcggtggaacctttagatactctcctctggaaaagccaccatgaattcggtagctgggaataaagagaggcttgcggtctccaccaggggcaagaaatacggggtgaatgaagtctgctcgcccaccaagcccgcagcgcccttctccccggaaagctggtaccggaaagcatacgaggagtcgcgcgccggcagccggcccactcctgagggcgcgggctcagcgctcggctcctcggggaccccgtctcccggctcgggcacctcgtccccgagctcgttcaccggctccccgggacccgcctcccccggcatcggcactagttcgccgggctccttgggcggctctccgggcttcggcacaggctccccgggctccggcagcggcggcggctcctcccccggctcggaccgcggcgtctggtgcgagaactgcaacgcccgcctggtggagctcaagaggcaggccctgaggttgctcctcccggggcccttcccgggcaaggaccctgctttctcggctgtgattcacgacaaactccaggtccccaacaccatccggaaggcatggaacgaccgggacaaccgctgtgacatttgcgccactcacctgaaccagttgaagcaggaggccatccagatggtgctgacgttggagcaggcagccggcagtgagcactacgacgcctcgccctgctccccgccaccgctctccaacatccccaccctggtggggtcccggcacgtgggtgggctccagcagcccagagactgggcctttgtgcccgccccctgtgccacctccaactacacaggcttcgccaacaagcacggcagcaaacccagcagccttggggtcagcaatggggcggaaaagaagagcgggtccccaacccaccaggccaaggtcagcctccagatggccaccagtccaagcaatgggaacatcctcaattcggtggccatccaggctcaccagtacctggatggcacctggtccctgtcgagaaccaacggggtcaccctgtacccataccagatctcccagctgatgacagagagtagccgggagggactaacagaagcagtgctgaaccgctacaatgcagacaagccttccgcctgcagtgtcccagcctcgcagggctcctgcgtggccagcgagacttccacaggcacatcggtggccgcctccttctttgcacgagctgcccagaagttaaatctgtcttctaaaaagaagaaacatcggccttccacttcttccgctgccgaaccaccgctctttgcaaccagcttcagtgggattctgcagacctcccctcccccagccccaccctgcctgctgagggctgtcaacaaggtgaaggacaccccggggctgggcaaggtgaaagtcatgcttcgcatctgttccaccttggctcgagatacttcagaatccagctctttcttaaaggtggacccacggaagaagcagatcaccttgtacgatcccctgacttgtggaggtcaaaatgccttccaaaagagaggcaaccaggttcctccaaagatgtttgccttcgatgcagtttttccacaagacgcttctcaggctgaagtgtgtgcaggcaccgtggcagaggtgatccagtctgtggtcaacggggcagatggctgcgtgttctgtttcggccacgccaaactgggaaaatcctacaccatgatcggaaaggatgattccatgcagaacctgggcatcattccctgtgccatctcttggctcttcaagctcataaacgaacgcaaggaaaagaccggcgcccgtttctcagtccgggtttccgccgtggaagtgtgggggaaggaggagaacctgcgggacctgctgtcggaggtggccacgggcagcctgcaggacggccagtccccgggcgtgtacctctgtgaggaccccatctgcggcacgcagctgcagaaccagagcgagctgcgggcccccaccgcagagaaggctgcctttttcctggatgccgccattgcctcccgcaggagccaccaacaggactgtgatgaggacgaccaccgcaactcacacgtgttcttcacactgcacatctaccagtaccggatggagaagagcgggaaagggggaatgtctggaggtcgcagccgcctgcatctcattgatctcggcagctgtgtgaaagctcttagcaaaaatcgagaaggaggctcagggctgtgtctctcgctgtctgctctgggcaatgtcatcctggctctcgtcaatggcagcaaacacattccatacaaagagagcaagctcgccatgttgctgcgggagtctctggggaacatgaactgccgtaccaccatgatcgcgcacatctcggccgcggtcgggagctacgcggagaccctgtccaccatccagattgcatcgagagtcttgaggatgaagaaaaagaagacgaagtacacatccagctcgtccggcggggagagctcctgcgaagaaggccgcatgcgcaggcccacccagctgagacccttccacaccagggccacggtggaccctgacttccccatcgctcacctgtccagcgaccccgactactcctccagcagcgagcagtcctgcgacaccgtcatctacatcgggcccaacggcacggccctctctgacaaggagctcaccgacaacgagggccccccagactttgtccctatcgtgccagccctgcagaagacccggggcgacagccggcccgcagaggcaggagaggctgcagccggcaagtcagaaagggactgcctgaagtgcaacacgtttgccgagctgcaggagaggctggactgcatcgacggcagcgaggagcccagcagctttcctttcgaagaactgcctgctcagtttgggccagagcaggcaagcagaggcccccggttaagccaagcagcgggggcaagcccactctctgagtctgataaggaagataatgggtccgaaggtcagctgaccaacagagaaggccctgaactcccagcctccaagatgcagaggagtcactcacctgtgcccgccgcggcacccgcccacagccccagcccggcctcacccaggagcgtcccgggcagcagtagccagcacagcgcctccccactcgtgcagagccccagcctccagagcagccgggagagcctcaactcctgcggcttcgtggaaggcaagcccaggcccatgggctccccccggctgggcatcgccagcctgtccaagacctcggagtacaagccacccagctctccttcccagagatgcaaagtctacacccagaagggggtcctgccgtctcccgccccactgcctccctcgagcaaggattccggcgtggcgtctagggagtccttgctgcagcccgaggtgcgtacgcccccggttggaatgagcccccaggttttgaaaaaatccatgtctgctgggagcgaagggttcccggaaactcctgtcgatgatgagcagcaggcagctactccttcagagtccaagaaggagatcctgagcaccacgatggtgacggtgcagcagccactggagctgaacggtgaggacgagctggtgttcacgctggtggaggagctgaccatcagcggggtcctggacagcggccgccccaccagcatcatcagcttcaacagcgactgctctgcacgggccctggcctcgggctcgcggcccgtcagcatcatcagcagcatcagcgaggacctggagtgctactccagcacggcccccgtctccgaggtcagcatcacacagttcttgcccctcccgaagatgagcctggatgagaaggcccaggacgcagggagcagacgctcttccatcagctcctggctgagcgagatgagcgcgggcagtgagggtgagcagtcgtgccacagtttcatagcccagacgtgttttgggcacggggaggcaatggcagaacctgtggcctcggagtttgtcagcagcctccagaacaccgctgtggtgtgcagagagaagcccaaggccagccccgacaacttgctcatcctgtctgagatgggagatgactctttcaacaaagcagcccccatcaaaggctgcaaaatatccacagtgagcaaggccatggtcaccatctccaacacggccaatctgagcagctgcgaggggtacatccccatgaagaccaatatcacagtttacccctgcattgccatgagcccccggaacatccaagagccggaggcccccaccgccacccccaaagcaggccccacattagcccagtcccgggagagtaaggaaaacagtgcaaagaaagagatgaaatttgaggacccgtggctgaaacgagaagaggaagtgaaaaaagagacggctcatcccaatgaagaagggatgatgaggtgtgagactgccacgggcccctcgaatgctgagaccagagcagagcaggagcaggacggaaagcccagtccgggagacaggctcagcagcagcagcggagaggtgtcggcctccccggtcactgacaacttcaggagggtcgtggatgggtgtgagatggccctgcccggtttggccacccagagccccgtgcatcccaacaaaagcgtcaagtccagcagccttcccagggcctttcagaaggccagccggcaggaggagccggacagcctctcctattactgcgctgctgagaccaacggggtgggtgcagcctcgggcaccccgccctccaaggctaccctggaggggaaggtggcttcccccaagcactgtgttctggctcggcccaaagggactccccctctgccccctgtccgaaagtccagcctggaccagaagaaccgggccagccctcagcacagtgccagcggcagcggcaccagcagccccctgaaccaaccagccgccttcccggcgggcctcccagacgagcctagcggcaagacgaaggacgccagcagcagcagcaagctcttcagtgccaagctggagcagctggccagcagaagcaactcgctgggcagggcgacagtcagccactacgaatgcctctccctggagcgggccgagagcctgtcctccgtgagctcccggctgcacgcgggcaaggacggcaccatgccccgcgcggggaggagcctgggccgcagcgccgggacctcgccccccagctccggggcctcgcccaaggccggccagtccaagatctccgccgtgagcagactcctcctggccagccccagagcgcgcggcccgtccgcctccaccaccaaaaccctcagcttctccaccaagtccctgccgcaggcggtgggccagggctccagctcgccccccggtgggaagcacacgccctggtccacgcagtccctcagcaggaacaggagctcgggcctggcctccaagcttcccctgcgggccgtcagcgggcgcatctcggagctgctgcagggtggcgcgggcgcccggggcttgcagctgcgggccgggcccgaggcggaggcgcgcgggggggccctggccgaggacgagcccgcggccgcgcacctgctcccgtcgccctacagcaagatcacgcccccgcggaggccccaccgctgcagcagcggccacggcagcgacaacagcagcgtgctgagcggggagctcccgccggccatggggaagacggccctgttctaccacagcggcggcagcagcggctacgagagcgtgatgcgggacagcgaggccaccggcagcgcgtcctcggcgcaggactccacgagcgagaacagcagctccgtgggcggcaggtgccggagcctcaagaccccgaagaaacgctccaatccaggttctcagagacggaggcttatcccagcactatccctggacacctcttcccctgtgagaaaaccccccaacagcacaggcgtccgctgggtggatggccccttgcggagcagcccgaggggccttggggaaccctttgagattaaagtctatgaaatcgatgacgtggagcgcctgcagcggcgacgagggggtgccagcaaggaggccatgtgcttcaatgcaaagctgaagattctggaacaccgccagcagaggatcgccgaggtccgcgcgaagtacgagtggctgatgaaggagctggaggcgaccaaacagtatctgatgctggatcccaacaagtggctcagtgaatttgacttggagcaggtttgggagctggattccctggagtacctggaggcactggagtgtgtgacggagcgcctggagagccgtgtcaacttctgcaaggcccatctcatgatgatcacctgcttcgacatcacctccaggcgccggtagatgagccagacccttgtcctagtggtcccccgctccccaggacttcagagatgttgcacgcccctaggccctctgtgctggggcatcaaagacaatgaatgaggatgaaggttggtggcaagtctggagcgggcgttgagcggaaggcgagttttcttttgttttctgtaggaaaggtgcaaacgtcaaacaccgtggaaggagaaaaggatgggaagcccgaggggtgtccaagccctgtgagactgaaaaagcactttgaggaaccttaaagaccttgtttgtacataagaactgctagcaaaagagacctcactcttctcttgctttcgtgagaaaggaggggcgtggatgtaggattgctgtggaaagcgaacacaaaacaacccagaatgactgattaagtgccttgcaaatctttattattatccaaacatttatgttcatactttcttgtgtacagatggtgctagtcaagatgaaaacaacaaaacaaacaagaaaaacattttggaaatgt"
						.toUpperCase());
		this.builderForward.setGeneSymbol("KIF26B");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 245318687,
				PositionType.ZERO_BASED), "C", "T");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(0, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("-39C>T", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FIVE_PRIME_UTR_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc001idp_1() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001idp.1	chr1	+	248031264	248060142	248058888	248059833	3	248031264,248039201,248058858,	248031365,248039339,248060142,	Q7Z3T1	uc001idp.1");
		this.builderForward
				.setSequence("aagtaaggctgtcacaaggctggaagcagagaacatccccatggaactgaagacagcatgctgcatccctgggaggagggagctcttaaggaagttccaagtggatgtaaagctggatcccgccacggcgcacccgagtctgctcttgaccgccgacctgcgcagtgtgcaggatggagaaccatggagggatgtccccaacaaccctgagcgatttgacacatggccctgcatcctgggattgatatttctgttcagcagcagtagagatggatggaaccaatggcagcacccaaacccatttcatcctactgggattctctgaccgaccccatctggagaggatcctctttgtggtcatcctgatcgcgtacctcctgaccctcgtaggcaacaccaccatcatcctggtgtcccggctggacccccacctccacacccccatgtacttcttcctcgcccacctttccttcctggacctcagtttcaccaccagctccatcccccagctgctctacaaccttaatggatgtgacaagaccatcagctacatgggctgtgccatccagctcttcctgttcctgggtctgggtggtgtggagtgcctgcttctggctgtcatggcctatgaccggtgtgtggctatctgcaagcccctgcactacatggtgatcatgaaccccaggctctgccggggcttggtgtcagtgacctggggctgtggggtggccaactccttggccatgtctcctgtgaccctgcgcttaccccgctgtgggcaccacgaggtggaccacttcctgcgtgagatgcccgccctgatccggatggcctgcgtcagcactgtggccatcgaaggcaccgtctttgtcctggcggtgggtgttgtgctgtcccccttggtgtttatcctgctctcttacagctacattgtgagggctgtgttacaaattcggtcagcatcaggaaggcagaaggccttcggcacctgcggctcccatctcactgtggtctcccttttctatggaaacatcatctacatgtacatgcagccaggagccagttcttcccaggaccagggcatgttcctcatgctcttctacaacattgtcacccccctcctcaatcctctcatctacaccctcagaaacagagaggtgaagggggcactgggaaggttgcttctggggaagagagagctaggaaaggagtaaaggcatctccacctgacttcacttccatccagggccactggcagcatctggaacggctgaattccagctgatattagcccacgactcccaacttgcctttttctggacttttgtgaggctgtttcagttctgacattatgtgtttttgttgttgctcttaaaattgagacggggtctcactctgtcacctagggtagagtgcagtggtgccaccatagctccttcgactattgggcttaagcgatcctcccccacctcagccttccaagtaactgggactacaggtgtgcatcactggcagtgggaatt"
						.toUpperCase());
		this.builderForward.setGeneSymbol("TRIM58");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 248058878,
				PositionType.ZERO_BASED), "A", "T");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(2, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("-10A>T", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FIVE_PRIME_UTR_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc010yrl_2() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc010yrl.2	chr2	+	73872045	73912694	73899961	73912217	7	73872045,73898206,73899435,73899934,73900939,73901968,73912087,	73872281,73898277,73899622,73900177,73901123,73902032,73912694,	Q96L16	uc010yrl.2");
		this.builderForward
				.setSequence("gaaagattcatttattcattcaacaaacagtggccgaaggcttgttatgtgctgtgcctggacttggcatgaaggcaccagatgtcagaagcgaggctgctgcccaggaggacacacctgatggggctctgggaccaggacgatggtggagacatcacatccggaataactttgttaggaaggtgaacttgacagttaggacttagttctccttgaagacgtccttcacatccctttttcctgagctcgaactctactttttgcaacaagcagaatgtacacatgttaaacaagggcacacaagcaggtaacttggagattgtgaacggtgccgaaaaacacactcgagccaactccaagttccagcgaggctaaattggaagaggacagtgatgtgacttcttggtcagaagaaaaacatgaagagaaaatgctctttaccagttatcctgaggacagaaagttaaaaaagaacaagaagaattcccatgaaggagtttcctggtttgttcctgtggaaaatgtggagtctagataaaaagaaggaaaacatgctcaagactcatgaccctggcatctcccggttggaaccagtaaccaagaccaagccgtggagggagccactgtgggagcggaactggcaggggcagcacctggacagtcggggctacctggcaggcccaggcagagaggatggcagaaacccactgaagctgtttgtgagagcaaccctgcaggaatcgcttcagtttcacagacctgacttcatctcccacattggggagcggataaagcgtctgaagttaatagtccaggagagaaagctgcagagcatgttaaagagcgagcgggatgcgctattcgacattgacagggaacggcagggccaccagaatcgcatgcgcccactacccaagagagtcttcctggctgtccagaagaacaagcctatcagcaagaaggaaatgattcagaggtccaaacggacacacgcaggaggaacagtagcacaaaggcttcccggtgccttgtgggtgaaagaaaggcttccagggaaaggagagaaacacccagggatcccggtgacgtcgggaagcagcacccagaccatgtaaggacaccgcctgcagggggagactcaaggcctgggaaggttttctaatctgtgccttgagtcgggtggggggattttctgggaaaggaaaaccagacacagtaggacaaatattgtatagtttcacttatatgaaattgtcagaataggcaagttcgtagagacagaatgtagaacagggctttccgggggctggaggagaagacaaggagttcttgcctagtgggtacagagtttctgtttgaaatgatgaaaaacttctggaaatagattgtggtgacagttacacaccattgtgaacataattagtaccactgaactgtacacgttaaaatggttagttgtatgttttatgtattttgccataataaaaactagggggaaaaaaaggaaggggagattgtctcaggctggctgactttccctgcatgtacgcacatcttcacatgtgcacacatgtaaaacaaaaatgaagcaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("ALMS1P");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 2, 73899612,
				PositionType.ZERO_BASED), "T", "G");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(2, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("-37T>G", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FIVE_PRIME_UTR_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc010fyp_1() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(refDict,
						"uc010fyp.1	chr2	-	237146331	237150258	237146333	237150163	2	237146331,237149922,	237146599,237150258,	uc010fyp.1");
		this.builderForward
				.setSequence("gatgacaattgagtaatgacaatagaaatagctcacactccataagaccatctttcccgttcctgaagaactcttctgaaatcgacggcatctcaatggagagacagccagggccagtgagaggaaaacttcaaatatttcaaaagacagagaaggatcctcaagctagagcagggtccccggtgcaggagtaccacactgccctggtcgcaggggacctcgaccatctgaagcccctcatggaccagttcttccaggatgccaacgtggtgtttgagatcaataaggatgagatggaatggcaggtgaaatctccagccacgtttggactatcaggcctctggaccctggagtacaagcgtgagctcaccacgcccctgtgcatcgccgcggcccacggccacaccgcctgcgtgcgacacctgctcggccgcggcgcagacccagacgccagccccggcggccgcggcgccctgcacgaggcctgcctcgggggccacaccgcctgcgtccgcctgctgctgcagcaccgcgccgaccccgacctgctcagcgccgagggcctggcgcctctgcacctctgccgcacggccgcctcgctcgg"
						.toUpperCase());
		this.builderForward.setGeneSymbol("ASB18");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 2, 237150165,
				PositionType.ZERO_BASED), "A", "C");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(0, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("-3T>G", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FIVE_PRIME_UTR_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc010kdf_3() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc010kdf.3	chr6	-	108023363	108145521	108026403	108093531	8	108023363,108029069,108041906,108066152,108067892,108070887,108093375,108145329,	108026529,108029215,108042197,108066347,108068093,108071017,108093590,108145521,	Q8N228	uc010kdf.3");
		this.builderForward
				.setSequence("gaatgcattagtctaatgagatgtttgcagctggagcgcagggctgctggagactaactgtgagctactaacacgggtggaagatagcttttgcaatactcggtttgcatgtgctgaaagtcatctgtcttctgagtcaacactcccgacctggtaaacaacctgctcagggctctggtgaacaagctgtagcacctcttctgcctgtgagcgatttgtcacctcattctgtaagactggcaccagcagaaatgcagtctcaaaggatcccggggagaaagcgaggccgaccctcacttcactccacgcctatgaagatggcagttcataacctttattctgcttcagctggctctttaccagcagtgaagatcccaaagaaaagagggcggaaacccgggtacaagatcaagtctcgggttctcatgactcccttagccctctcacctccgcggagtaccccagagcccgacctcagctccatccctcaggacgcagccacggtccccagcttggcggccccacaggctctcacagtctgcctctacatcaacaagcaggccaatgcggggccctatctggagaggaagaaggtgcagcagctcccggagcattttgggcccgagcggccatcggcggtgctgcagcaggccgtccaagcctgcatcgactgcgcccaccagcagaagctggtcttctccctggtcaagcagggctatggtggtgagatggtgtcagtctcggcttcctttgatggcaaacagcacctgcggagcctgcctgtggtgaacagcatcggctatgtcctccgcttcctcgccaagctgtgccgaagcctcctgtgcgatgacctcttcagccaccagcccttccccaggggctgcagtgcctctgagaaagtccaggagaaagaggaagggaggatggaatcagtcaagacagtcaccaccgaagagtacctggtgaaccctgtgggcatgaaccgctacagcgtggacacctccgcctccacctttaaccacaggggctccttgcacccctcctcctcgctgtactgcaagaggcagaactctggagacagccaccttgggggtggtcctgctgccaccgctggtggtccccgcactagccccatgtcttctggtggcccctcggcacctgggctgaggcctccagcctccagccccaagagaaacacgacctctcttgaaggaaacagatgtgcctcaagcccttctcaggatgcgcaggatgccaggcggccacggagcaggaacccctccgcctggactgtggaggacgtggtgtggtttgtgaaggacgccgacccacaggctctggggcctcacgtggagctcttcagaaagcacgagattgatggcaacgctctgctgttgctgaagagtgacatggtcatgaagtacctgggcctgaagctgggacctgcactgaaactctgctaccacattgacaaactgaagcaagccaagttctgacttttttaaaaagacagaagcgaaacccaaaacaacagatcccaagattatcttctgccttaccaatatcccgccaacatcacaaactagactctcctcttaaaattaacagccacagagacgtggtctttttataaaacttgtgaatctttgccttttgaagaatttaacatggaccttttcgagaggctcctctgtgttcataatttgccaaaaaattacaaaagcctgtgatttttaacatccctgttatgctggtttctcttaaagtgggtcctatttgcataacgagagagtggggaactgaatgcttatgcccaaggagagttctggagggttcaaaggatgaaagaaggacctttgtccctgcggtctctgcagggacaaccccctcagcaccatctgcctctaactctgacctggggacctatccatgtgagccttgtttgcctcagctctggaagctgacttctgaagatgactgcctcaccttgcactgtctggaaaacttgaattattttacgccgtgaaagaaaaaggaaaaaaaaaaaaatcttttctgttcctagaaaatctgaagtactgtgtttctccgctagagggcagactgctaatgaaatttcaggaccctcactgactgcagtagcaggattgttcagtactgagtggatgggttagggtgtctgtgaacaagatctagcccacacagaaacaggggatttattccacagttagcaccacagattgcgacttgggaagaaaccatcagctagaggggtctagttcgtatccaatcattattgactgactgactgatactcaactagcaaaggcaagaatttggaagcacgctctacccagatgggactttggagttccttcccttccagagtccttattgaggtgttgaagtgttggcatgccgaagaatcttagtgacatttagccatgggtgtttctttaaaaaaggaaaagaaaatgtctcaacgaagctttgaaatgggagagtgttgatttctagttacattgctgggttatgtagttgtatctgtggctaatttttctagtcctcaacaaatacacagacatgctattatggggtttaattcaatttacaaataggttttccttcctcacagtggagtaatagaaaaaattgattttctgctcctttgcagctgtgtccagagacggacaatgaatccacaatttattaggcagaggcacaacttctccaccattcgttttatccttctcttttctctgtttccctccacctctcttcttcttctttcacactgagcagaaaacatatcttcaaaatgaattcgcctttgccatgtgcatattctcttccttaaaaggacatcaacagtgttggagatgaggagatgaacattagattttggaatttctgggagggagagggttagggatgactcagggcctcccttcgcccaaaggtgaatcagaaaaggcacccctttctctgggatacaacctgagcaggggtctcagctggcaagtggagcagggtgggggctcaacctcctcaccgcttgatcagctgccgttgtacagagcatgacctgtataaccttgagggcagccctgggtagatctgcagagtgaccctctatatagttagaatccagatgaaggccataagaaatagtccatgccatgccgttaggcccactttcatgtgcagtacttgagaaagcacatcgtagcctccttttcagacacacaaatggcggctggactgagagcatggcagagaaggggaggaggagaatgtactaaacttgctcactgaattgagcttgagttattagattgtagaagagcttgatgtctggtgattttgttacaggaagggggtcccgatccagaccccaagagagggctcttggatctcatgcaagaaagaattcagggtgagtccataaagtaaagtgaaagcaagtttattaagaaagtaaaggaaggaaagaatggctaccccatagtcaaagcagctctgagggctgctggttgcccatttttatgtttatttcttgatatgctaaacaaggggtggattattcatgcctcccccttttagatcatatagggtaacttcctgatgttgccatggcatttgtaaaccgtcatggcactggtggtagtatagcagcgaggacaaccagaggtcactctcgttgccatcttggttttggtgggttttagccaacttctttactgcaacctgttttatcagaaggtctttatgacctatatcttgtgctgacctcctgtctcatcctgtgactcagaatgctttagccatctgggaatgcagcccagtaggtctcagcctcattttacccagctcccattcaagatggagttgctctggttcaaatgcctctgacaggttcagcacctgagaaaagtaccttgaatcaggccttggggcagagtcttcaagattttaaggtgatagggcctgtctcttaagaatttattaatcgtaagtgctcactgctggtttagagttagatacatttccctcaaaacagccatgggcaggttatgtactcagaactgccagggtgaaaacaatgagatccagaaaaatcccacaacaatccaggtttttcactcctcttttttttctcaatcttccgatacttgtggtacctcttccatcaacagatacctcatgtagtgacttccagtatcaagttttcaaaacttcctacccaccctcatcagtgcctccttcactacagagtcacaaacattggtgctctcaaataaaaatcagccccaaacaatgtcaaagagatcccttgagtagttttcaggcgagagatatgttctcatttcaagtcttggagaaaactggatccatgttcacgtggaacttctctcgcggaggtgctatctcattttgcattaaactttaagcaggacagattgctgaagccatgatatttaaggtttgactttttaaaaatctcattactcttaaaataaatgctgccacatcagaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("SCML4");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 6, 108093579,
				PositionType.ZERO_BASED), "C", "T");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(1, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("-49G>A", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FIVE_PRIME_UTR_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc003ztp_2() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc003ztp.2	chr9	-	33921690	33935189	33922501	33927882	12	33921690,33922684,33922963,33923183,33923376,33923792,33924203,33926614,33926986,33927794,33932559,33933487,	33922597,33922876,33923031,33923291,33923476,33923998,33924282,33926662,33927078,33927990,33932626,33935189,	Q5JV03	uc003ztp.2");
		this.builderForward
				.setSequence("ttgggagaaaatgagaccccccccccccgccacttccaacagctcagtgattaccaaagatagtgcaggtaaatccgttaattgcttcttttctgtagttggccagcttgactgatggtttatagtagttttctttctttattcaggaaggcaaatgggtggggaagccagaaagactctttaaataggatttcctgggagtgcggatattgaacttaagcagaccgaggtgagcctcagttgaaactgtaatgacagatttggagccccaggattttggtttgacatttgctggtattgttgggctgccaaggctgtttggagattcgttggtcgttctgagcttgttggatgacagaggaacttttgactctagcagtggtagtggcaggtttcttttgtctaggcatgggtcgagaactaaagtatctgggaagcagtgtacattaacctacttttcatttcccatactctcttctccctcacttcctcctccccttccctccccattttgtttgtttgtttgagagaggcttcctgtgaagctgttagcatcataatacaaggcccagggcggtatgttttggggtcttagttttaggtgaggtgtatctgtggtgtctttatacagtttatctatgaacaaggtttatatatggaagagactgccaataaaaagaaggcctctgtaagaactgacctaggtgtaagttgaccctttcattgcttatgtttgtttttgacctgcccttcctttagagactcaagtgctttccctgggttttagaagggtcaaggttgctcctctttcctaactggaaaagacaatgatgttttatttccaagcacatatctgagttgtatgtgtggacagcactgagactgagtctttccacagctaggactgagtgtctccacatcctttctgaagcctacgaagctcatttatgtgctctgagatacatctattcaagcacataccaaggaaatgctcactgtgcatttggaagtaggtgttggacagttggactgcagatgttaggtgtctgtgtctctccacaacataggcatcaaccccaatctgccttacgttggccgtgagcattggtagagtcagcctgacttggcggaagcacgtgtaggtggtcttgcctgttagcttcttactcccccttgctgtgccctttgaggagtttgcctcacgttataattctacatattaggctcagcacctgtttttctttctggatgcatctacagcttaggttcctttaggtgtgaaaagtatgtccccatataggataggttgggttataaaggtgtcatttcctattctttgctgcatcttgaacagctgaccatggtgtggtgtcctttgatagtgttgtttgacagacaaagggctatctggcgatatgcttggcattttcctggggaatgtaactttatggacgtctcaactgaggctgacatgtcacaagatattgagcatattttcaggcacttaggattggttttagaaataaacagatctgcttcttacagctacttcatctgttccagctccaaagacaacaggccctccctctgccctcccgtctgtgagctccctgcccagcaccacctcctgcactgcacttctgccgtccacatcccagcacactggcgacctgactagcagccctctctctcagcttagcagttcgctctccagccaccagagcagcctctctgcacatgcagccctctcctcgagcacgtcacacacacatgccagtgtggagagcgcctcttcccaccagtcctcagccaccttctccacggcagcgacctccgtctcaagttccgcatcctcaggcgccagcctgtccagtagcatgaacaccgcgaacagcctctgtctgggtgggacccccgcgagtgcatccagcagcagtagcagggccgcgcccttggtgacctcaggcaaagcacccccaaacttacctcagggggtgcctcccctgctgcacaaccagtacctcgtaggtcccggaggactgcttcctgcctacccgatctatggctatgacgagctccagatgctgcagtcacggctgccagtggactactatggaattccctttgctgcacccacagcgcttgccagccgagatgggagcctagctaataatccatatccaggtgatgtcacaaagtttggccgtggggactctgcatcccctgcacccgctaccacaccagctcagccacagcagagccaatcacagacccaccacacagcccagcagcccttcgtgaatcctgcactgccacctggctatagctacactggtcttccctactacacaggcatgcccagtgccttccagtatggccccaccatgtttgtccctccagcctcagccaagcaacatggggtgaacctcagcactcccacacctcccttccagcaggccagtggttatggccagcacggctacagtacaggttatgacgacctgacccaggggacagcagcaggagactactccaaaggtggctatgctggatcatcgcaggcaccaaacaagtctgcaggttctgggcctggcaaaggagtatcagtgtcttcaagcaccactggtctacctgatatgactggttctgtctacaataagacacagacttttgacaagcagggatttcatgcagggacgcctccacctttcagcctgccctcggtcttgggctccactgggcccctggcctcgggagcggcccctggctatgcacccccaccattcctacacatcttgccagcccaccagcagccccactcacagctgctgcaccaccaccttccgcaggatgcacagagtggctcgggtcagcgcagccagcccagctccctgcagcccaagtctcaagcctccaaacctgcctacggcaactctccatactggacaaactaaacccagaagagaggggtgggctggggcaaggcttatcctgggcaggagagaacacacgagcacgtatttgggagcccagtgccctttcctagaattcccgacatgtgtcagccatgcctctgtggggagtctgcctcccagactggctactgtatgtaatgtatttatgtatgtatttgtaaatgtgatagaagtctgggggggagttgggggatggcggcagatgttagccaggtctgccctccccattcaagccccttctccactgtagcaaaataagcacccccaccccatctgccttcaggtcttcttcacagcctgcactgcccagtgggccactaggggcagtctctggaagggctggttcaaggctgtttgggtataggggtcaggtaccaatgaagaatcacgacttgtctcactcctttggaaattgttttctttcctgtgtaattacttcatacctctgtttttgagaaactgttccgtttgtcatctgtcatggtctccttccaccaaatcttcatctgggaatagcagcggtatccctccacccaagtatggccacctgtttgtcttcatatagaacaggggcttctggtctggtcatgtccctagagacttactagagactggctgaccatgaaaaaaaaaaaaataatctgagttattcagccagtcagctcccctttaaaaatctaaggtatctccaagcaacacatctccattctagttgtcttgaaccacgtgggcaactgtttcaggggttttctcttgcccaatccccacctaataaagttctgagagcatg"
						.toUpperCase());
		this.builderForward.setGeneSymbol("UBAP2");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 9, 33933704,
				PositionType.ZERO_BASED), "A", "G");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(0, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("-393T>C", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FIVE_PRIME_UTR_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc010mug_4() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc010mug.4	chr9	-	114448900	114521813	114449049	114521568	23	114448900,114453830,114456078,114456510,114462233,114464384,114466160,114467526,114468847,114470134,114475379,114476724,114484688,114486071,114489912,114500559,114503755,114508513,114510371,114518604,114520363,114520974,114521435,	114449150,114454791,114456228,114456642,114462327,114464506,114466272,114467644,114469026,114470204,114475452,114476903,114484865,114486191,114490329,114500802,114503850,114508622,114510479,114518758,114520475,114521128,114521813,	A6PVK7	uc010mug.4");
		this.builderForward
				.setSequence("agacagctagccaagattctaaagaaacccagacaaggcagggtggagaccgagaggagaaatttattccagaaattaactgttagcagtagtgtttcttaatacataagctatatcatactcctcaagtagattctttgcttaaaactttcactgtaaataattttatagcaaccatgtgaataacttaagaataatagaatcagtctcatttgtaggcactgtagaccatctccattccctacatgtcagagactctgggggatgaattggagatattaagaggtaaaatgatgcagagaagaccaaggtcagcagaagtcaaatacttctatttctttaaaattttgcttaggctacgcctggctattttgaagtatttatttattgatgataaaggaatactttttgtaagtagtagaaaacacctaccaactttgcctactctcttgagtagactaaaactgtttttggtaaaggatcctcttttagatttcaaaggacagatcttcacagaagctaatttttccagggaatgtttctctcttcaagaaactttggaagcttttgtgaaagaagatttttgtatggataaagtgaacttttgtcaagagaaactagaagatacaatatgtttaaatgagccgtcaagttttcttattgagtatgaattcttaatacctccaagcctcaaaccagaaattgatattccatcactctcagaactgaaggagttattaaacccagtgccagaaataataaactatgtagatgaaaaggaaaagctttttgaaagagatcttactaacaagcatggaattgaggatatcggggatataaaattcagctccacagagattttgaccattcaaagccagagtgaaccagaagagtgcagtaaaccaggagagttagaaatgccactaactcctctattcctaacatgccaacattcttcagtgaattcattacgtacagaacttcagacatttccattatctccggtttgtaaaattaatttgcttactgctgaagaatcagctaatgaatactacatgatgtggcaattagaaagatgtagaagccctttgaacccatttttgcttacagtgccaagaattcaagagccccacagccaatattcagttacagatttgaaaaagatattttctgttaaagaagaaagccttgtgattaatctggaaaaggcagagtggtggaaacaagcaggactaaatctgaaaatgatggaaacattggaacatctgaatacatatttatgtcatgataatttgtcttctaatgacactaaaattgagatatttttgcctacgaaagtgcttcaattagaatcatgtctagaacataaaagtcattcttcacctattgcacttattgatgaaaaatctacaaatgctcatttatcacttccacaaaagagtccatctctggcaaaagaagtaccagatctatgtttttctgatgactatttctctgataaaggagcagcaaaagaagaaaaaccaaagaatgaccaagaaccagtaaacagaataatccaaaagaaagaaaataacgatcactttgaacttgactgcacaggaccatctattaaatcaccttcctcttcaataattaaaaaagcatcttttgaacatggcaaaaaacaagagaatgatttggaccttttgagcgactttattatgctgcgaaataaatataagacttgcacctcaaagactgaagtcacaaacagtgatgaaaaacatgataaagaagcatgttctttgacacttcaagaagaaagtcctattgttcatattaataaaaccctggaggaaataaatcaggaaaggggaacagatagtgtcattgaaattcaagcgtcagatagccagtgccaagcattttgcctcctcgaagcagcagcttctcctatcttaaaaaaccttgtatccttgtgtaccctccctactgctaattggaaatttgccactgttatttttgaccaaacaaggtttctcttaaaggaacaagaaaaagtagtaagtgatgctgttcgccaaggatatttgtcgaaggcaaaagatatctacaacagcattttaggcccctatttgggtgacatttggagacagctggagattgtacagtttattagggggaaaaagcctgaaaccaactacaagatacaagaattgcaatgtcagatactaagttggatgcaaagtcaacagcaaattaaggtactgattataataagaatggactcagacggtgaaaaacattttctcattaaaattcttaacaaaatagaaggtttaacactgactgtccttcattcaaatgaaagaaaagattttctggaatctgaaggtgttttaaggggtacaagttcctgtgtagttgtacataatcaatatattggagcagatttcccctggagtaatttctcatttgtggtggaatacaattatgtggaagactcttgttggactaaacactgcaaagagttgaatattccttacatggcctttaaagtgattcttccagacacagttttagaaagaagcaccttgctggatagatttggaggttttcttttggaaattcagattccatatgtgttttttgcatctgaaggacttcttaatactccagacatacttcagctgctagaatccaactataacatctcactagtagagagaggctgcagtgagtcattgaaactctttggaagttcagagtgttatgtagtggtgacaattgatgaacacactgccataattttgcaggatctagaagaattgaattatgagaaggcatcagacaatatcattatgaggctgatggcattatcattacagtacagatattgttggataattttatataccaaagaaacattaaattcagagtatctgcttacagaaaagacacttcatcacctagcactgatttatgcagctttggtttcatttgggctaaactctgaagaactggatgtaaagcttataattgccccaggagtagaagcaactgccttgataattcgacaaattgctgaccacagtttaatgacctcaaagagagatcctcatgaatggttggataaatcctggcttaaagtttcaccatctgaggaagaaatgtacttacttgattttccatgtattaacccattggtggctcagctcatgctaaataaaggaccttcactgcattggatattattagcaactctgtgtcaacttcaggaactcctacctgaagtcccagaaaaagtgttaaagcatttttgtagcatcacttccctattcaagattggttcttcttccataacaaaatcaccgcaaatttcgtcacctcaggaaaataggaatcagattagtaccttgtcttctcaaagttcagcttctgatttagactctgtcattcaagaacataatgaatattatcagtatttaggattaggagagacagtgcaggaagacaaaaccaccattttgaatgacaactcttccattatggaactaaaagaaatctcaagttttttaccacctgtgacttcatacaatcagaccagctactggaaagactccagctgtaaatctaatatagggcagaatactccttttctaattaatatagaatcaaggagaccggcttataactcctttctaaaccacagtgattcagagtcagatgtcttttctttgggtctaacacaaatgaactgtgaaactataaaatcaccaactgacactcagaagagagtgtcagttgtcccccgttttataaattctcagaaaaggagaacacatgaagcaaaaggtttcataaataaagatgtatcggaccctatcttttcactagagggcactcaatctcctcttcattggaactttaagaaaaatatatgggaacaagagaatcacccgttcaacttacaatatggtgcacagcagactgcatgtaacaaattgtactctcagaaaggtaatttattcactgatcagcaaaaatgtctatcagatgagtctgaaggcctcacatgtgaaagttcaaaagatgagactttctggagagaattaccatctgtccccagtttggatttatttcgtgcttctgattctaatgcaaatcaaaaagaattcaacagcctttatttctaccaaagagctggaaaaagtttaggacagaaaaggcaccatgaatcttcatttaactcaggagacaaggaatcattaacaggttttatgtgctcacaactaccacaattcaaaaaacgacgtctagcatatgaaaaagtccctggtagagttgatgggcagactcggctgaggtttttttgaaggaggagaagagcaatgttacatgccatattccactgtttttgatgctaatccactagcgcaattatttagatttgctcatacactaaagaaaacacaattgttcatatatgtctcagtatttctgtattaaatattcataatatgta"
						.toUpperCase());
		this.builderForward.setGeneSymbol("C9orf84");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 9, 114521629,
				PositionType.ZERO_BASED), "A", "G");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(0, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("-62T>C", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FIVE_PRIME_UTR_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc010ddv_2() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc010ddv.2	chr17	+	61562177	61575741	61566022	61574727	14	61562177,61562596,61563917,61564346,61565930,61566301,61568314,61568569,61570796,61571282,61571732,61573754,61574158,61574497,	61562427,61562733,61564076,61564434,61566152,61566493,61568412,61568742,61571020,61571427,61571831,61573877,61574346,61575741,	D3DU13	uc010ddv.2");
		this.builderForward
				.setSequence("gctcttattggccaggggacggtagctgcaggactctgctctcctgcggccatgggccagggttgggctactgcaggacttcccagcctcctcttcctgctgctctgctacgggcaccctctgctggtccccagccaggaggcatcccaacaggtgacagtcacccatgggacaagcagccaggcaacaaccagcagccagacaaccacccaccaggcgacggcccaccagacatcagcccagagcccaaacctggtgactgatgaggctgaggccagcaagtttgtggaggaatatgaccggacatcccaggtggtgtggaacgagtatgccgaggccaactggaactacaacaccaacatcaccacagagaccagcaagattctgctgcagaagaacatgcaaatagccaaccacaccctgaagtacggcacccaggccaggaagtttgatgtgaaccagttgcagaacaccactatcaagcggatcataaagaaggttcaggacctagaacgggcagcactgcctgcccaggagctggaggagtacaacaagatcctgttggatatggaaaccacctacagcgtggccactgtgtgccacccgaatggcagctgcctgcagctcgagccagctctgaaattctctgagctccccttacaagcagaggtgagctaagggctggagctcaaggcattcaaacccctaccagatctgacgaatgtgatggccacgtcccggaaatatgaagacctgttatgggcatgggagggctggcgagacaaggcggggagagccatcctccagttttacccgaaatacgtggaactcatcaaccaggctgcccggctcaatggctatgtagatgcaggggactcgtggaggtctatgtacgagacaccatccctggagcaagacctggagcggctcttccaggagctgcagccactctacctcaacctgcatgcctacgtgcgccgggccctgcaccgtcactacggggcccagcacatcaacctggaggggcccattcctgctcacctgctggggaacatgtgggcgcagacctggtccaacatctatgacttggtggtgcccttcccttcagccccctcgatggacaccacagaggctatgctaaagcagggctggacgcccaggaggatgtttaaggaggctgatgatttcttcacctccctggggctgctgcccgtgcctcctgagttctggaacaagtcgatgctggagaagccaaccgacgggcgggaggtggtctgccacgcctcggcctgggacttctacaacggcaaggacttccggatcaagcagtgcaccaccgtgaacttggaggacctggtggtggcccaccacgaaatgggccacatccagtatttcatgcagtacaaagacttacctgtggccttgagggagggtgccaaccccggcttccatgaggccattggggacgtgctagccctctcagtgtctacgcccaagcacctgcacagtctcaacctgctgagcagtgagggtggcagcgacgagcatgacatcaactttctgatgaagatggcccttgacaagatcgcctttatccccttcagctacctcgtcgatcagtggcgctggagggtatttgatggaagcatcaccaaggagaactataaccaggagtggtggagcctcaggctgaagtaccagggcctctgccccccagtgcccaggactcaaggtgactttgacccaggggccaagttccacattccttctagcgtgccttacatcaggtactttgtcagcttcatcatccagttccagttccacgaggcactgtgccaggcagctggccacacgggccccctgcacaagtgtgacatctaccagtccaaggaggccgggcagcgcctggcgaccgccatgaagctgggcttcagtaggccgtggccggaagccatgcagctgatcacgggccagcccaacatgagcgcctcggccatgttgagctacttcaagccgctgctggactggctccgcacggagaacgagctgcatggggagaagctgggctggccgcagtacaactggacgccgaactccgctcgctcagaagggcccctcccagacagcggccgcgtcagcttcctgggcctggacctggatgcgcagcaggcccgcgtgggccagtggctgctgctcttcctgggcatcgccctgctggtagccaccctgggcctcagccagcggctcttcagcatccgccaccgcagcctccaccggcactcccacgggccccagttcggctccgaggtggagctgagacactcctgaggtgacccggctgggtcggccctgcccaagggcctcccaccagagactgggatgggaacactggtgggcagctgaggacacaccccacaccccagcccaccctgctcctcctgccctgtccctgtccccctcccctcccagtcctccagaccaccagccgccccagccccttctcccagcacacggctgcctgacactgagccccacctctccaagtctctctgtgaatacaattaaaggtcctgccctccccatctgagtctgtgtccctcacagggaagccagggacagggacaggctgctttcctgcctcctggcagtcaagtgggtcccgttactaggtttgttcctccatcctccttcaggagccggggaggatccccagagctctgccccagcacctcctggcgctggcgcctgtcttccctccagcccaggcagcccgccactgtcctgccaccgcaggcagcccctgtctggcccaagcactgacccacgcggactctgggaagcagacatcctgggctgctggcctcacatttccactggcagtggagcctttccctgctccacaaatggccaggtccccccaggggaaggcttccggctgttatcggctgcctcagggggcgagtaccttggagggcctgcttcaaggagggtgccccctggagggcacacaccagcctagtgcttaccttggctcctgcctgtaccagctccatgactctgctcgggtgaacagccttggctctcagacagccattctaacactgccagtgcagaggggcctcagacgctggagtgtagcagtggctgcacctgcacagggattagctgccagcagccaccctgctggcgtcccagcacacacctcctcactccctgcattggagggagtgtcattttaagggacatttttatgacttttatgtgtatgtttatgtagaaatttggaaaatacagaaaactgtaaagaaaataaaagccctttatatcaacgtcaagagataa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("ACE");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 17, 61565989,
				PositionType.ZERO_BASED), "G", "C");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(4, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("-33G>C", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FIVE_PRIME_UTR_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc003ach_4() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc003ach.4	chr22	-	26846848	26868384	26849198	26861428	9	26846848,26853824,26854410,26859882,26861420,26864516,26866684,26867178,26868267,	26849370,26853933,26854543,26860792,26862228,26864589,26866779,26867354,26868384,	Q9NQG7	uc003ach.4");
		this.builderForward
				.setSequence("aactgttctcaggaagaactgagcacggagtgggacaccttcatcgagcaaattctgaaaaacaccagtgatctgcataagattttcaattccctctggaacttggaccaaactaaaggacttctaggcccctgactccatacaaggaatgtggaactgaggcatatttccccgagtgaaagagcatgatgttttcattttatgtgatggaatagagtcatggaacctggtcagacatcaagccacctgtatcaccatggggattgtgcttcagaaggaaagtggaggatagggtggagcccctgttgttgctgaaggcagcccgcattctgcagacctgccagcgctcgcctcacattctcgctggctgcatcctctataaaggactgattgtcagcacccaactcccgccctccctcaccgccaaggtcctgcttcaccgaacagcacctcaggagcagagactccctacgggagaggatgccccgcaggaacatggtaagtagtgaggcgcatggttgcctcttgagaacggcggtcttttatgctacctacccctgtacaagttatgcgaaggaaaccaagccatctgcatgtttatttcctctgctcataataggaaaatggatgttatggagcttcaagaatcgagttacccaccagaaccctaatggtaaagctatgattttaggcatgctgtcaaatccctgctttcagaccctccttaacttcatattgtgagactgctcggtcaagtgccaggagattgtgattgggagagggtaaggcaaacagcgactgccagaaagaccttttgcaggatgtggaggcgtcctcaggagagttatttcgatttattctgaagcctgagttggagcagatccctttcttagcttatatcttcttcttgtaaaagtgcttttcaacctttctctcccgaacatcttccttctccaaagctgggagtcatgttgcctgcagggactctttccttactgggcccaggagttcgaggtgcagtttggcaacctgtgtcctggctgaggaatgcactcacagcacccctttccgagcccacgtgtgggttggatgcatcacgccagtgcagctattgccccgctgccctctctggctcaggggaaagtcctgttcatattgctttgcaattccaggagcggcattgcccccgaatgtccagattatccctgtttttgtgaccaaagaggaagccattagtctccacgagttcccggtggaacagatgacaaggtctctagcatctccagcaggactccaggatggttcagcccagcaccatccaaagggtgggagcacatctgccctgaaagaaaacgccactggccatgtggaatccatggcctggaccaccccagatcccacatcccctgacgaagcttgtccagatggcaggaaggagaacggatgcttgtctggccatgatctggagagcatcaggcccgcaggactgcacaactctgccaggggtgaggttcttggcctcagctcctccctggggaaggaactagtctttctccaagaagaactcgacttgtctgaaatccacattccagaggctcaggaagtggaaatggcctcaggtcattttgccttcctacatgtgcctgttccagatggcagggctccttactgcaaggcatctctcagcgcctccagcagcctggaacccacgcctcctgaggacacagccatcagcagcttgcgccctccctctgctcctgagatgctgacccagcatggagcccaagagcagctcgaagaccatcctggccatagcagccaagcccccattcccagagcagaccctctccccagaaggacccgcaggcccttgttattgcctcgcttagatccaggacagagaggaaacaagcttcccacgggggaacaaggcctggatgaggatgttgatggggtctgtgaaagccacgcagcccctggtctggaatgcagttcaggctcagcaaactgtcagggtgctggcccctctgcagatggaatcagctccaggctgacaccagcagagtcctgcatggggctcgtgaggatgaatctctacactcactgcgtcaaagggctggtgctgtccctgctggctgaggagccgctgctgggagacagcgcagccatagaggaagtgtaccacagcagcctggcttcactgaatgggctggaagtccacctgaaagagacgctgcccagggatgaggcagcctccacgagcagcacctacaacttcacacattacgaccgcattcagagcttgctgatggcaaacctgccgcaggtggccaccccgcaggatcgccgcttcctccaggccgtcagcctgatgcatagcgaatttgcccagctgcccgcgctttatgaaatgactgtcagaaatgcctccacggctgtgtacgcctgttgcaaccccatccaggagacatatttccagcagctggcacctgcagcacggagctccggcttcccaaaccctcaggatggcgccttcagcctctccggcaaagcaaagcagaagctgctgaagcacggggtgaacttgctctgaactgcacccaggaggtgactgggaaggagaaaaccagcaaaggaagctctgccttttataattgaaaaggcccctctattttatttttcttgaaaacattcccttttttaggaaccaaatgatatttgagtttttgttattccttttgcagattgggatgtgttttgggggcaggggttagttcttcaggtcggcagacccagagcacttgataaagaactgtatttaatcggtagtgttggggccgggacgggcttggctccctctctgccatactgagcctgaggtatttcatatctcctgctgttccatcccagcttgaattggtgccacaagcttccaagttggcattttttctagaacctgatcgtccactagcccagagtgtgtgtgttcaacccccacaccaggtggtggtaggcggtgtgactgcacagcgaggtgccggatctgtgagcaggccgactccactcccacgccgcaggtaggtttctccagtgcgctcttgctgggaggtccggatcgttcctgcagggaagcggcagcacacggagaccacttggttgaattctgttggaactctactcaaatctaggggcgtcttctttggacccacaatgggggcaagccttaataatatggaagggagtttgggctttagagatccctttataaaagctctgggggctgagccctgagaattcagtgacaacaggaccaacctgcgctgcctttgactacaagtgggccgtgcagctggttcctctcgagcgagtgtccctaaataggagtttacaagatgtctgggggtaaaagcactgtgcttttcagtggtggctgcgtgaaagggagcgacactcagctgtgtgttcctgggcttgtgtggtacttagaacctcagttctattacgttatagtcagacatttttttgacagtatgagacagactgcaggatgaaaatatttgtcaaaatcttaactgaatgtttactggaagtacttgagattccatttgagagttgtattgttaataatttcatgtcagtgaactgatatctgatgtttatgatatggtgtctttttcttgaaacaagcttccaagggctagaaataaaatagccaaaaaatgctggagttctgagtaattaaatggcagcatttttttgtgacaaaggtaagggttaagacagtgtgtgtgtgtgtgtgtgtgtgtgtgtgtgtgtgtgtgcgcgcgcgcgcgcgcgcgtaacgtgggcgcatccctcttttccttccctgggttcagtgtggctgggtaaggaccgtctgtttgtagggctgccaggctgcagcctgctgctccagggagcaccagctgcagctgatccaatacaccaagcctgtgtctccctcttctatcacctctctccaggatctctgctgtctcttggatcccatctgcctagatggaagaggatcctatccgaagctaggataaatgttgtcttactcctctgctatgttctcccagaaaaaattgattacaagttctctgctgtggttgctgcagctccaggaagtccccgcaatgtctcacgttgtctttgaccagtggtccccagtaccaggccagcgaagacaactttataatgtcatttgtgttgtgaaaattcttccactgacccaaaatggaaccgtacaaagcctatccgtgtatatggaaaagtctcatgcacctggattaactcagaaaaagtaaagtcctgaaacttcactttgcaggtgtgcccctttctttcctcctttgatcctgcttagtttctgcagtggaggttctgctcagagccccagggcactggcagctgcagccgtcatctctaaagcttggtaactgtggcccagaggctgcagagaagaaacacttaacccgctgttttcccgttttttaatatttcagaggtttcagggggtcactcaggacacggttaatcccacagccccaggcccatcccctcaaaattagcgcccccagagtacgggccaagcgacggacagccaagcagcatgttcttgctggaatgggcaccacaacctcttatggccatgtttcccaagggaaggcagtgtgcaggacagcctgcccaccctggactcctagttgacaccgagtgccactgtcagtgatccaggaccacagattctagtgactcttcagggcagaaaagcctctgttttggcttcctctgtgccagccatctgccacctgtgaactgtaaggataaaggtcgcatgagtgattaatagtggtgtgttagtccgttctcacggttctaataaagacatacccgagactgggtactttataaaggaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("HPS4");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 22, 26862152,
				PositionType.ZERO_BASED), "C", "A");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(4, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("-725G>T", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FIVE_PRIME_UTR_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc001dcv_3() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001dcv.3	chr1	+	67218139	67244730	67220341	67243137	5	67218139,67220338,67236069,67241961,67242933,	67218267,67220460,67236161,67242086,67244730,	Q8N7M0	uc001dcv.3");
		this.builderForward
				.setSequence("tcagagtccagggagagtgcgcgggcggccgccggctgaatgaagcctgggacgcgggagccgcgccgcgcgcagtgtctgcagtgccggaggtctgggaggctccgggcgaagcctccctgctgcaggttatgatgatgtcagacaatgctaaaggcagagcagctcattcatggaagaaaagagggagtatttcttctctaagtaatcatgaattttggcgaaaggaaattcatgggcgcatcaaagattctatgagtactgtgtcttatatggaagaacccagtcagcgtgatgatatctctcgccttacagttcagatggaaaacacctatcagttgggtcctcccaaacattttcctgtggtcaccgtcaatcatattttgaaagatgtagtaaccagctatctacaagtagaagaatatgaaccagagctctgtagacagatgactaaaaccatttctgaggttattaaagcccaggtcaaggacttgatgattccacggtataaactaattgtgattgttcacattggacaactgaacaggcagagcatacttattggaagcagatgcctctgggatcctaaaagtgataccttttcatcttatgttttcagaaattcttctctcttcgctcttgcaaatgtctatgcagtttaccttgagtgattgaaaataagaaatctagctcttacttttgaaaattctgaggcaggctgtatgtctgtacacaaaagttttactgccaaaaactttgagaaagaaacaacactgatatttcaagcaactggaagcttttgaattttttcatattctagtaaagatttggggaggggagggtagccacagaaagaatcttgtttatctaaagcaggaacttgatcttggttttgctggattgctttccatagacatagattcttattaaatatttgttttggtactaattattttaattattctttaattagagtgttacaatttcctgcattgttttttaataaaaagaagctttagaaaaactatggctatataccctgaccaataaatgatagtagaatattcaacatggtatacacaaatgataaataatgtgcctcagtgctgcatataaaagaatactggttttcattttgtacttaataaattgagcagcattcattaagtgcaattattgttacatgtacctgacagttgaaactgctgaggtagaattaatggcttcataacaattatcgtggtcccacgggctgcaagctaaaactagaagctcttgtggcttgcctgcactttacacaattgtgggagaaccgaaagttggctcttcagtaatggaaactataaagtgaataaaggctttttgggacactgaagtagacattgtgatattaaatatacatgatgcttaatactaaatgtgtctgtaacacataaaatcatttattggaaagagacattgcttttaaatatcctaggctaacaaactttttcacaaaatatatggctggcaccttaacaaataaagcatgtgtagttaccattttactcctatgaatttaatactaaagtgacccagtgaggtttttgaatagacgggtgctaaaatttacagggctgaaatagcttattattctgctggttaatgtatcttacgccacggatcatttatttttatgaagctttcaagtctttgtgtgcaagtaaaatgatgttgtggcgttgttcttggatggtaagtggactgcctagaggacatttgttaaaggtcaaagacaaattgtacacatatggagaaaattagcaatgctgtagtcttaaggaaaaaatgatttatcattcatatcaaaaagagtttagcaaatggaaaaccagttaatgctttaattttaaaatgttttggtgtcttacttttccctaaaaatgacatgtagtaaaaaatggatacttttaaaatcccttcccttctgctattttctggcttttaaagtgattattcaaaatgaatttgtcaattttcacttttttttttctgttagtgcattagtgggaatatgtctatgtaataatttatcacaaccaatatttgtcaatctccatagaacagttttcaatctggattagattacacgtataacaagtaaatgagcttcatattttcattacacattggtgaacatctcccttcgtggaacaacagcgggggtaatcatatcactaattttgtatgaataaatgaaaatgaagaaacttctttaacatataaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("TCTEX1D1");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 67242086,
				PositionType.ZERO_BASED), "G", "A");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(3, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("336+1G>A", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("?", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.SPLICE_DONOR_VARIANT,
				VariantEffect.CODING_TRANSCRIPT_INTRON_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc001alq_2() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001alq.2	chr1	-	5922869	6052533	5923324	6046349	30	5922869,5923949,5924397,5925161,5926432,5927089,5927799,5933311,5934530,5934933,5937152,5940173,5947345,5950927,5964676,5965351,5965691,5967174,5969211,5987708,5993206,6007163,6008129,6012759,6021853,6027358,6029146,6038329,6046214,6052303,	5923465,5924093,5924577,5925333,5926518,5927175,5927956,5933395,5934717,5935160,5937358,5940299,5947526,5951088,5964864,5965543,5965843,5967282,5969273,5987847,5993389,6007290,6008311,6012896,6022009,6027423,6029319,6038473,6046387,6052533,	O75161	uc001alq.2");
		this.builderForward
				.setSequence("gtgacgcgaggcgggttcttggactgagtgtgcggcgcggtgcgccgccttccgaggctcctcccgcgggtggcagcggacggggcgcgcccctcggccagtcctcggtcctcaggcttgtggctccgttgagcaccggccgccgggcctctgggtccgtcgagtggagactctctgaaaagcgtgggctccgtggcctccggcgcggccgcggcgggtcggtctcctagatcatccgggaagcccacgggaccctcaggcgggcaggatgaacgactggcacaggatcttcacccaaaacgtgcttgtccctccccacccacagagagcgcgccagccttggaaggaatccacggcattccagtgtgtcctcaagtggctggacggaccggtaattaggcagggcgtgctggaggtactgtcagaggttgaatgccatctgcgagtgtctttctttgatgtcacctaccggcacttctttgggaggacgtggaaaaccacagtgaagccgacgaagagaccgccgtccaggatcgtctttaatgagcccttgtattttcacacatccctaaaccaccctcatatcgtggctgtggtggaagtggtcgctgagggcaagaaacgggatgggagcctccagacattgtcctgtgggtttggaattcttcggatcttcagcaaccagccggactctcctatctctgcttcccaggacaaaaggttgcggctgtaccatggcacccccagagccctcctgcacccgcttctccaggaccccgcagagcaaaacagacacatgaccctcattgagaactgcagcctgcagtacacgctgaagccacacccggccctggagcctgcgttccaccttcttcctgagaaccttctggtgtctggtctgcagcagatacctggcctgcttccagctcatggagaatccggcgacgctctccgaaagcctcgcctccagaagcccatcacggggcacttggatgacttattcttcaccctgtacccctccctggagaagtttgaggaagagctgctggagctccacgtccaggaccacttccaggagggatgtggcccactggacggtggtgccctggagatcctggagcggcgcctgcgtgtgggcgtgcacaatggtctgggcttcgtgcagaggccgcaggtcgttgtactggtgcctgagatggatgtggccttgacgcgctcagctagcttcagcaggaaagtggtctcctcttccaagaccagctccgggagccaagctctggttttgagaagccgcctccgcctcccagagatggtcggccaccctgcatttgcggtcatcttccagctggagtacgtgttcagcagccctgcaggagtggacggcaatgcagcttcggtcacctctctgtccaacctggcatgcatgcacatggtccgctgggctgtttggaaccccttgctggaagctgattctggaagggtgaccctgcctctgcagggtgggatccagcccaacccctcgcactgtctggtctacaaggtaccctcagccagcatgagctctgaagaggtgaagcaggtggagtcgggtacactccggttccagttctcgctgggctcagaagaacacctggatgcacccacggagcctgtcagtggccccaaagtggagcggcggccttccaggaaaccacccacgtccccttcgagcccgccagcgccagtacctcgagttctcgctgccccgcagaactcacctgtgggaccagggttgtcaatttcccagctggcggcctccccgcggtccccgactcagcactgcttggccaggcctacttcacagctaccccatggctctcaggcctccccggcccaggcacaggagttcccgttggaggccggtatctcccacctggaagccgacctgagccagacctccctggtcctggaaacatccattgccgaacagttacaggagctgccgttcacgcctttgcatgcccctattgttgtgggaacccagaccaggagctctgcagggcagccctcgagagcctccatggtgctcctgcagtcctccggctttcccgagattctggatgccaataaacagccagccgaggctgtcagcgctacagaacctgtgacgtttaaccctcagaaggaagaatcagattgtctacaaagcaacgagatggtgctacagtttcttgcctttagcagagtggcccaggactgccgaggaacatcatggccaaagactgtgtatttcaccttccagttctaccgcttcccacccgcaacgacgccacgactgcagctggtccagctggatgaggccggccagcccagctctggcgccctgacccacatcctcgtgcctgtgagcagagatggcacctttgatgctgggtctcctggcttccagctgaggtacatggtgggccctgggttcctgaagccaggtgagcggcgctgctttgcccgctacctggccgtgcagaccctgcagattgacgtctgggacggagactccctgctgctcatcggatctgctgccgtccagatgaagcatctcctccgccaaggccggccggctgtgcaggcctcccacgagcttgaggtcgtggcaactgaatacgagcaggacaacatggtggtgagtggagacatgctggggtttggccgcgtcaagcccatcggcgtccactcggtggtgaagggccggctgcacctgactttggccaacgtgggtcacccgtgtgaacagaaagtgagaggttgtagcacattgccaccgtccagatctcgggtcatctcaaacgatggagccagccgcttctctggaggcagcctcctcacgactggaagctcaaggcgaaaacacgtggtgcaagcacagaagctggcggacgtggacagtgagctggctgccatgctactgacccatgcccggcagggcaaggggccccaggacgtcagccgcgagtcggatgccacccgcaggcgtaagctggagcggatgaggtctgtgcgcctgcaggaggccgggggagacttgggccggcgcgggacgagcgtgttggcgcagcagagcgtccgcacacagcacttgcgggacctacaggtcatcgccgcctaccgggaacgcacgaaggccgagagcatcgccagcctgctgagcctggccatcaccacggagcacacgctccacgccacgctgggggtcgccgagttctttgagtttgtgcttaagaacccccacaacacacagcacacggtgactgtggagatcgacaaccccgagctcagcgtcatcgtggacagtcaggagtggagggacttcaagggtgctgctggcctgcacacaccggtggaggaggacatgttccacctgcgtggcagcctggccccccagctctacctgcgcccccacgagaccgcccacgtccccttcaagttccagagcttctctgcagggcagctggccatggtgcaggcctctcctgggttgagcaacgagaagggcatggacgccgtgtcaccttggaagtccagcgcagtgcccactaaacacgccaaggtcttgttccgagcgagtggtggcaagcccatcgccgtgctctgcctgactgtggagctgcagccccacgtggtggaccaggtcttccgcttctatcacccggagctctccttcctgaagaaggccatccgcctgccgccctggcacacatttccaggtgctccggtgggaatgcttggtgaggaccccccagtccatgttcgctgcagcgacccgaacgtcatctgtgagacccagaatgtgggccccggggaaccacgggacatatttctgaaggtggccagtggtccaagcccggagatcaaagacttctttgtcatcatttactcggatcgctggctggcgacacccacacagacgtggcaggtctacctccactccctgcagcgcgtggatgtctcctgcgtcgcaggccagctgacccgcctgtcccttgtccttcgggggacacagacagtgaggaaagtgagagctttcacctctcatccccaggagctgaagacagaccccaaaggtgtcttcgtgctgccgcctcgtggggtgcaggacctgcatgttggcgtgaggccccttagggccggcagccgctttgtccatctcaacctggtggacgtggattgccaccagctggtggcctcctggctcgtgtgcctctgctgccgccagccgctcatctccaaggcctttgagatcatgttggctgcgggcgaagggaagggtgtcaacaagaggatcacctacaccaacccctacccctcccggaggacattccacctgcacagcgaccacccggagctgctgcggttcagagaggactccttccaggtcgggggtggagagacctacaccatcggcttgcagtttgcgcctagtcagagagtgggtgaggaggagatcctgatctacatcaatgaccatgaggacaaaaacgaagaggcattttgcgtgaaggtcatctaccagtgagggcttgagggtgacgtccttcctgcggcacccagctggggcctgtctgtgcccctcctgccctgcaggctgtcctccccgcctctctgcagcctttcacttcagtgcccacctggctgacctgtgcacttggctgaggaagcagagaccgagcgctggtcattttgtagtacctgcatccagcttagctgctgctgacacccagcaggcctgggttccgtgagcgcgaactccgtggtggtgggtctggctctggtgctgccatctacgcatgtgggaccctcgttatcgctgttgctcaaaatgtattttatgaatcatcctaaatgagaaaattatgtttttcttactggattttgtacaaacataatctattatttgctatgcaatattttatgctggtattatatctgttttttaaattgttgaacaaaatactaaacttttacacgtctaaaaaaaaaaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("NPHP4");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 5935161,
				PositionType.ZERO_BASED), "A", "T");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(19, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("2818-2T>A", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("?", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.SPLICE_ACCEPTOR_VARIANT,
				VariantEffect.CODING_TRANSCRIPT_INTRON_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc001byw_3() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001byw.3	chr1	-	35899090	35920237	35900494	35920053	12	35899090,35906647,35907846,35908506,35909761,35913773,35915467,35915958,35917228,35919157,35919962,35920214,	35900682,35906739,35907937,35908629,35909904,35913933,35915606,35916110,35917392,35919290,35920086,35920237,	Q8IZA0-3	uc001byw.3");
		this.builderForward.setSequence("SEQ".toUpperCase());
		this.builderForward
				.setGeneSymbol("atattcactcacatgctgaactgggtgttagaacaccaaccttacagctctctgcgatgcaagaaggagactacacttaccagctcacagtgactgacacaataggacagcaggccactgctcaagtgactgttattgtgcaacctgaaaacaataagcctcctcaggcagatgcaggcccagataaagagctgacccttcctgtggatagcacaaccctggatggcagcaagagctcagatgatcagaaaattatctcatatctctgggaaaaaacacatttctttttttgcaggggacctgatggggtgcagctcgagaatgctaacagcagtgttgctactgtgactgggctgcaagtggggacctatgtgttcaccttgactgtcaaagatgagaggaacctgcaaagccagagctctgtgaatgtcattgtcaaagaagaaataaacaaaccacctatagccaagataactgggaatgtggtgattaccctacccacgagcacagcagagctggatggctctaagtcctcagatgacaagggaatagtcagctacctctggactcgagatgaggggagcccagcagcaggggaggtgttaaatcactctgaccatcaccctatcctttttctttcaaacctggttgagggaacctacacttttcacctgaaagtgaccgatgcaaagggtgagagtgacacagaccggaccactgtggaggtgaaacctgatcccaggaaaaacaacctggtggagatcatcttggatatcaacgtcagtcagctaactgagaggctgaaggggatgttcatccgccagattggggtcctcctgggggtgctggattccgacatcattgtgcaaaagattcagccgtacacggagcagagcaccaaaatggtattttttgttcaaaacgagcctccccaccagatcttcaaaggccatgaggtggcagcgatgctcaagagtgagctgcggaagcaaaaggcagactttttgatattcagagccttggaagtcaacactgtcacatgtcagctgaactgttccgaccatggccactgtgactcgttcaccaaacgctgtatctgtgaccctttttggatggagaatttcatcaaggtgcagctgagggatggagacagcaactgtgagtggagcgtgttatatgttatcattgctacctttgtcattgttgttgccttgggaatcctgtcttggactgtgatctgttgttgtaagaggcaaaaaggaaaacccaagaggaaaagcaagtacaagatcctggatgccacggatcaggaaagcctggagctgaagccaacctcccgagcaggcatcaaacagaaaggccttttgctaagtagcagcctgatgcactccgagtcagagctggacagcgatgatgccatctttacatggccagaccgagagaagggcaaactcctgcatggtcagaatggctctgtacccaacgggcagacccctctgaaggccaggagcccgcgggaggagatcctgtagccacctggtctgtctcctcagggcagggcccagcacactgcccggccagtcctcctacctcccgagtctgcgggcagctgctgtcccagcatctgctggtcatttcgccctgacagtcccaaccagaacccctgggacttgaatccagagacgtcctccaggaacccctcaacgaagctgtgaatgaagaggtttcctctttaaacctgtctggtgggcccccagatatcctcacctcagggcctcctttttttgcaaactcctcccctcccccgagggcagacccagccagctgctaagctctgcagctccccagtggacagtgtcattgtgcccagagtgctgcaaggtgaggcctgctgtgctgcccgcacacctgagtgcaaaaccaagcactgtgggcatggtgtttccctctctggggtagagtacgccctctcgctgggcaaagaggaagtggcacccctcccctcaccacagatgctgagatggtagcatagaaatgatggccgggcgcggtggctcacgcctgtaatcccagcactttgggaggccgaggcgggcggatcatgaggtcaggagatcaagaccaccctggctaacacggtgaaaccccatctctactaaaaataaaaaaaaaaattagccgggtttggtggcgtatgcctgtaatcccagctactcgggaggctgaggcaggagaattgcttaaacctgggaggtggaggctgcagtgagccaagatcgtgccactgcactccagcctgagtgacagagcaagactccgtcaaaaaaaaaaaaaaaaaaaaagaaatgatatctggcccccccttaacactggagccccactcccttctcccatccggcccgagattagggaggattgactgtgtcagggatggcgggtggcctctctcgctgccagggcccttgtcagagcagccaggctggacagacggcctccctcctctccatctgaccggcacctgctgcttcggggcttaggccaccgctccctgtccccagaggagatagccccagatggactggaatgttgtggcatgagagcgcatgtgtgcgatggccccgctgtggtcccctctctgtccctccatctgtatgtgttctgtgtcccttgcatgtgtgcgtgttagagtgagcgcgtatgcatcaactcattgggctcttggctgctcacaaggcaaatttgacttggaaagactttcatctccttggaaccaagacttcctgagtccccctcaccctggccctgttccaccatggttatctgggtattggggaatggaaactttgggggagtgactttttaaagagacacttataatttctactactgcactactgtccattgtgggatgattaaacatggtatttaactgtgca");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 35917392,
				PositionType.ZERO_BASED), "C", "T");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(2, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("225-1G>A", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("?", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.SPLICE_ACCEPTOR_VARIANT,
				VariantEffect.CODING_TRANSCRIPT_INTRON_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc001fkt_3_first() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001fkt.3	chr1	-	155305051	155532324	155307450	155491310	28	155305051,155307879,155309118,155311726,155313104,155313394,155313973,155316173,155317446,155319117,155319332,155322496,155324263,155327106,155327375,155330091,155340294,155340567,155348071,155348293,155349833,155365249,155385534,155408117,155429587,155447676,155490890,155531943,	155307542,155308181,155309159,155311893,155313277,155313533,155314064,155316260,155317695,155319250,155319387,155322649,155324421,155327201,155327540,155330200,155340441,155340774,155348180,155348339,155349907,155365344,155385714,155408859,155429689,155452240,155491409,155532324,	Q9NR48-2	uc001fkt.3");
		this.builderForward
				.setSequence("aggagtggaaggttgaggggggcgctaggcgcccttcgctccctccctctggaggagctgccgccgccaccgccgccactctgctgctgccgccgccgccgccgccgctcccgccgccattttgggttcgctttgcggaggggagacgatcccagtctcggttgcgggacccgcctcccctcagtttgccccctttagccttccacctttcccttctcctctctcgcatttccgccagtcagcttacccgctggccgcctcctgacaagcgggagggatccgccgtggacccagggaagcggaggagcctggcggccaccccctcttccccacttccctgcactctcatcgctctcggcctcggcctcggcctccgacacgagaaagatgctggtttcgagttttggagatccttgttttttatggaacacagttctgtaaaattttcataagattccttggcaataacatacgcttgtgatggaccctagaaatactgctatgttaggattgggttctgattccgaaggtttttcaagaaagagtccttctgccatcagtactggcacattggtcagtaagagagaagtagagctagaaaaaaacacaaaggaggaagaggaccttcgcaaacggaatcgagaaagaaacatcgaagctgggaaagatgatggtttgactgatgcacagcaacagttttcagtgaaagaaacaaacttttcagagggaaatttaaaattgaaaattggcctccaggctaagagaactaaaaaacctccaaagaacttggagaactatgtatgtcgacctgccataaaaacaactattaagcacccaaggaaagcacttaaaagtggaaagatgacggatgaaaagaatgaacactgtccttcaaaacgagacccttcaaagttgtacaagaaagcagatgatgttgcagccattgaatgccagtctgaagaagtcatccgtcttcattcacagggagaaaacaatcctttgtctaagaagctgtctccagtacactcagaaatggcagattatattaatgcaacgccatctactcttcttggtagccgggatcctgatttaaaggacagagcattacttaatggaggaactagtgtaacagaaaagttggcacagctgattgctacctgtcctccttccaagtcttccaagacaaaaccgaagaagttaggaactggcactacagcaggattggttagcaaggatttgatcaggaaagcaggtgttggctctgtagctggaataatacataaggacttaataaaaaagccaaccatcagcacagcagttggattggtaactaaagatcctgggaaaaagccagtgtttaatgcagcagtaggattggtcaataaggactctgtgaaaaaactgggaactggcactacagcggtattcattaataaaaacttaggcaaaaagccaggaactatcactacagtaggactgctaagcaaagattcaggaaagaagctaggaattggtattgttccaggtttagtgcataaagagtctggcaagaagttaggacttggcactgtggttggactggttaataaagatttgggaaagaaattgggttctactgttggcctagtggccaaggactgtgcaaagaagattgtagcaagttcagcaatgggattggttaataaggacattggaaagaaactaatgagttgtcctttggcaggtctgatcagtaaagatgccataaaccttaaagccgaagcactgctccccactcaggaaccgcttaaggcttcttgtagtacaaacatcaataatcaggaaagtcaggaactttctgaatccctgaaagatagtgccaccagcaaaacttttgaaaagaatgttgtacggcagaataaagaaagcatattggaaaagttctcagtacgaaaagaaatcattaatttggagaaagaaatgtttaatgaaggaacatgcattcagcaagacagtttctcatccagtgaaaagggatcttatgaaacctcaaagcatgaaaagcagcctcctgtatattgcacttctccggactttaaaatgggaggtgcttctgatgtatctaccgctaaatccccattcagtgcagtaggagaaagcaatctcccttccccatcacctactgtatctgttaatcctttaaccagaagtccccctgaaacttcttcacagttggctcctaatccattacttttaagttctactacagaactaatcgaagaaatttctgaatctgttggaaagaaccagtttacttctgaaagtacccacttgaacgttggtcataggtcagttggtcatagtataagtattgaatgtaaagggattgataaagaggtaaatgattcaaaaactacccatatagatattccaagaataagctcttcccttggaaaaaagccaagtttgacttctgaatccagcattcatactattactccttcagttgttaacttcactagtttatttagtaataagccttttttaaaactgggtgcagtatctgcatcagacaaacactgccaagttgctgaaagcctaagtactagtttgcagtccaaaccattaaaaaaaagaaaaggaagaaaacctcggtggactaaagtggtggcaagaagcacatgccggtctccaaaagggctagaattagaaagatcagagctttttaaaaacgtttcatgtagctcactatcaaatagtaattctgagccagccaagtttatgaaaaacattggacccccttcatttgtagatcatgacttccttaaacgccgattgccaaagttgagcaaatccacagctccatctcttgctctcttagctgatagtgaaaaaccatctcataagtcttttgctactcacaaactatcctccagtatgtgtgtctctagtgaccttttgtctgatatttataagcccaaaagaggaaggcctaaatctaaggagatgcctcaactggaagggccacctaaaaggactttaaaaatccctgcttctaaagtgttttctttacagtctaaggaagaacaagaacccccaattttacagccagaaattgaaatcccttccttcaaacaaggtctgtctgtgtctccttttccaaaaaagagaggcaggcctaagaggcaaatgaggtcaccagtcaagatgaagccacctgtactgtcagtggctccatttgttgccactgaaagtccaagcaagctagaatctgaaagtgacaaccatagaagtagcagtgatttctttgagagcgaggatcaacttcaggatccagatgacctagatgacagtcataggccaagtgtctgtagtatgagtgaccttgagatggaaccagataaaaaaattaccaagagaaacaatggacaattaatgaaaacaattatccgcaaaataaataaaatgaagactttaaagagaaagaaactgttgaatcagattctttcaagttctgtagaatcaagtaataaagggaaagtgcaatccaaactccataatacggtatcaagtcttgctgccacatttggctctaaattgggccaacagataaatgtcagcaagaaaggaaccatttatataggaaagagaagaggtcgcaaaccaaaaactgtcttaaatggtattctttctggtagtcctactagccttgctgttcttgagcaaacagctcaacaggcagctgggtcagcattaggacagattcttcccccattactgccttcatctgctagtagttctgagattcttccatcacctatttgctctcagtcttctgggactagtggaggtcagagccctgtaagtagtgatgcaggttttgttgaacccagttcagtgccatatttgcatttacactccagacagggcagtatgattcagactcttgcaatgaagaaggcctcaaaggggaggaggcggttatctcctcctactttgttgccaaattctccttcgcacttgagtgaactcacatctctaaaagaagctactccttccccaatcagtgagtctcatagtgatgagaccattcccagtgatagtggaattggaacagataataacagcacatcagacagggcagagaaattttgtgggcaaaaaaagaggaggcattcttttgagcatgtttctctgattccccctgaaacctctacagtgctaagcagtcttaaagaaaaacataaacacaaatgtaagcgcaggaatcatgattacctcagctatgacaagatgaaaaggcagaaacgaaaacggaaaaagaaatatccccagcttcgaaatagacaggatccagactttattgcagagctggaggaactaataagtcgcctaagtgaaattcggatcactcatcgaagtcatcattttatcccccgagatcttctgccaactatctttcgaatcaactttaatagtttctatacacatccttctttccccttagaccctttgcactacattcgaaaacctgacttaaaaaagaaaagagggagaccccctaagatgagggaggcaatggctgaaatgccttttatgcacagccttagttttcctctttctagtactggattctatccatcttatggtatgccttactctccttcaccccttacagctgctcccataggattaggttactatggaaggtatcctcccactctttatccacctcctccatctccttctttcaccacgccacttccacctccttcctatatgcatgctggtcatttacttctcaatcctgccaaataccataagaaaaagcataagctacttcgacaggaggcctttcttacaaccagcaggactcccctcctttccatgagtacctaccccagtgttcctcctgagatggcctatggttggatggttgagcacaaacacaggcaccgtcacaaacacagagaacaccgttcttctgaacaaccccaggtttctatggacactggctcttcccgatctgtcctggaatctttgaagcgctatagatttggaaaggatgctgttggagagcgatataagcataaggaaaagcaccgttgtcacatgtcctgccctcatctctctccttcaaaaagcttaataaacagagaggaacagtgggtccaccgagagccttcagaatctagtccattggccttgggattgcagacacctttacagattgactgttcagaaagttctccaagcttatcccttggaggattcactcccaactctgagccagccagcagtgatgaacatacaaaccttttcacaagtgcaataggcagctgcagagtttcaaaccctaactccagtggccggaagaaattaactgacagccctggactcttttctgcacaggacacttcactaaatcggcttcacagaaaggagtcactgccttctaacgaaagggcagtacagactttggcaggctcccagccaacctctgataaaccctcccagcggccatcagagagcacaaattgtagccctacccggaaaaggtcttcatctgagagtacttcttcaacagtaaacggagttccctctcgaagtccaagattagttgcttctggggatgactctgtggatagtctgctgcagcggatggtacaaaatgaggaccaagagcccatggagaaaagtattgatgctgtgattgcaactgcctctgcaccaccttcttccagtccaggccgtagccacagcaaggaccgaaccctgggaaaaccagacagccttttagtgcctgcagtcacaagtgactcttgcaataatagcatctcactcctatctgaaaagttgacaagcagctgttccccccatcatatcaagagaagtgtagtggaagctatgcaacgccaagctcggaaaatgtgcaattacgacaaaatcttggccacaaagaaaaacctagaccatgtcaataaaatcttaaaagccaaaaaacttcaaaggcaggccaggacagggaataactttgtgaaacgtaggccaggtcgacctcggaaatgtccccttcaggctgtcgtatcaatgcaagcattccaggctgctcagtttgtcaacccagaattgaacagagacgaggaaggagcagcactgcacctcagtcctgacacagttacagatgtaattgaggctgttgttcagagtgtaaatctgaacccagaacataaaaaggggttgaagagaaaaggttggctattggaagaacagaccagaaaaaagcagaagccattaccagaggaagaagagcaagagaataataaaagctttaatgaagcaccagttgagattcccagtccttctgaaaccccagctaaaccttctgaacctgaaagtaccttgcagcctgtgctttctctcatcccaagggaaaagaagcccccacgtcccccaaagaagaagtatcagaaagcagggctgtattctgacgtttacaaaactacagacccaaagagtcgattgatccaattaaagaaagagaagctggagtatactccaggagagcatgaatatggattatttccagcgcccattcatgttggaaagtatctaagacaaaagagaattgacttccagcttccttatgatatcctttggcagtggaaacacaatcagctatacaaaaagccagatgtcccactatataagaaaattcgttcaaatgtctacgttgatgtcaaacccctttctggttacgaagctaccacctgtaactgtaagaagccagatgatgacaccaggaagggctgtgttgatgactgcctcaatagaatgatctttgctgagtgttcccccaacacttgcccatgtggcgagcaatgctgtaaccagaggatacagaggcatgaatgggtgcaatgtctagaacgatttcgagctgaggaaaaaggttggggaatcagaaccaaagagcccctaaaagctgggcagttcatcattgaatacctaggggaggtcgtcagtgaacaggagttcaggaacaggatgattgagcagtatcataatcacagtgaccactactgcctgaacctggatagtgggatggtgattgacagttaccgcatgggaaatgaggcccgattcatcaaccatagctgtgacccaaattgtgaaatgcagaaatggtctgttaatggagtataccggattggactctatgctcttaaagacatgccagctgggactgaactcacttatgattataactttcattccttcaatgtggaaaaacagcaactttgtaagtgtggctttgagaaatgtcgaggaatcatcggaggcaagagtcagcgtgtgaatggactcaccagcagcaaaaacagccagcccatggccacacacaaaaaatctggacggtcaaaagagaagagaaagtctaagcacaagctgaagaaaaggagaggccatctctctgaggaacccagtgaaaatatcaacaccccaactagattgaccccccaattacagatgaagccaatgtccaatcgtgaaaggaactttgtgttaaagcatcatgtattcttggtccgaaactgggagaagattcgtcaaaaacaggaggaagtaaagcacaccagtgataatattcactcagcatcattatatacccgttggaatgggatctgccgagatgatgggaatatcaagtctgatgtcttcatgacccagttctctgccctgcagacagctcgatctgttcgaacaagacggttggcagctgcagaggaaaatattgaagtggctcgggcagcccgcctagcccagatcttcaaagaaatttgtgatggtatcatctcttataaagattcttcccggcaagcactggcagctccacttttgaaccttcccccaaagaaaaagaatgctgattattatgagaagatctctgatcccctagatcttatcaccatagagaagcagatcctcactggttactataagacagtggaagcttttgatgctgacatgctcaaagtctttcggaatgctgagaagtactatgggcgtaaatccccagttgggagagatgtttgtcgtctacgaaaggcctattacaatgcccggcatgaggcatcagcccagattgatgagattgtgggagagacagcaagtgaggcagacagcagtgagacctcagtctctgaaaaggagaatgggcatgagaaggacgacgatgttattcgctgtatctgtggcctctacaaggatgaaggtctcatgatccagtgtgacaagtgcatggtatggcagcactgtgattgtatgggagtgaactcagatgtggagcactacctttgtgagcagtgtgacccaaggcctgtggacagggaggttcccatgatccctcggccccactatgcccaacctggctgtgtctacttcatctgtttgctccgagatgacttgctgcttcgtcagggtgactgtgtgtatctgatgagggatagtcggcgcacccctgatggccacccggtccgtcagtcctatcgactgttatctcacattaaccgagataaacttgacatctttcgcattgagaagctttggaagaatgaaaaagaggaacggtttgcctttggtcaccattatttccgtccccacgaaacacaccactctccatcccgtcggttctatcataatgaactatttcgggtgccactctatgagatcattcccttggaggctgtagtggggacctgctgtgtgttggacctttatacgtattgtaaagggagacccaaaggagtaaaggagcaagatgtgtacatctgtgattatcggcttgacaagtcagcacacctgttttacaagatccaccggaaccgctatcctgtctgcaccaaaccctatgcttttgatcacttccccaagaagctcactcccaaaaaagatttctcgcctcattacgtcccagacaactacaagaggaatggaggacgatcatcctggaagtctgagcgctcaaagccacccctaaaagacttgggccaggaggatgatgctctacccttgattgaagaggttctagccagtcaagagcaagcagccaatgagatacccagcctggaggagccagaacgggaaggggccactgctaacgtcagtgagggtgaaaaaaaaacagaggaaagtagtcaagaaccccagtcaacctgtacccctgaggaacgacggcataaccaacgggaacgactcaaccagatcttgctcaatctccttgaaaaaatccctggaaaaaatgccattgatgtgacctacttgctggaggaaggatcaggcaggaaactgcgaaggcgtactttgtttatcccagaaaacagctttcgaaagtgaccctcaaagaatgagaacctcaagcatctgggatccagtggagctaatcagtcctgcctcctgctctctgggtatagacaggggtgggaagggtccatctgggcaaggggaatggggccatgttgttgacattaggtacttaataagccttggagctagtggagagggagaggaaagggttctgtccaagacagttcaggttaattaattttcttctccattgcttcaccttaagggttaataatgtagagaggagggaggaccacattgatgaccagaacctactggtactttatagcatttgccccaccccacagcttaggtttttctgtcatcctcagatcccacaggcattgcgaagaagctgcttcctatacccaggtataactcaaaatccaaagggatagggccaggatccctattcctaccccatctattctctgttggctccaagagctaccccagagaccttaaacagaaacagtagctgaggcttcttcctagatacctgactagggaagtttgtctctcctttcttgcccaaccaggtcaaagtaaaatgtgagttgacagctcaaagcacttgtaactgctgccccctccctacctctactccccaaaatggaatcatgggatagggaaggcccccatggggtcagaagggcacggtagttcttgcaattatttttgttttacccttcataacctgtcaaacatatttttttctaatgagaaagccaggcccccgccagcacacatgctgtttttaatgcgctgtagttcttgtgtgtctgctgtgctgtgcaaatggagattcagttcaaaataaaatcatttaaaaacctacataaaaagaactctaaacccacccctgcaacaaaagtcactacataaactgttcagcagtattcacctatcagagtatttgttgtgagtatagattatcaattgaaaacactactcttgttttcttaattgtacagttttcaatgtccctttcttaaagagacagtatatttctcttcacccctagcccatcttccctcaccctcctgaatgacatcaggaggtatatccagggtgtctccttccttcctactctcttgaccagaagttaacagactatactgtctctttaaaaataaaatttaaaaagctttgttgtcttttcagacatacatatgcatatatgttttagatgttcttataagagaaaagatggtttttaaatgtgccaagttgtgtgtgtgtgtgtatatatatgtgtgtatgtgtgtgtatatatatatgtgtgtgtgtatatatatacacacacacacacacacctgctgtgtgattggtaagcaatacaatagtaaacatgtccccattacttttttctaatattggaccaatgctgtcctaattgtacatttccccttatggtgacgatgctctgactcgtttaggtagacacattgaccaccttccattccattaaatattttttcctttttcccctttctgtgtcattcttgaggaaaaaacaaaagagagaggggatgccaatgatccccttgagcagagaaaaagcaaaataaatattttattaaagaaaaaagagaattaagaaaatagtttggagtattttcttactgtagagaagcactgtacattactaagagacctgggtataagatactcacatgtggagctggaaaaatcgcatgtccaagcccgtttgagtggtttcttttgtttttcattgcagggagtgggtgggagggaggtgggactaggggcactttgggggtctccttttagtcaaaagcgagaaaatgacaagaaagagattaaaattcaatgtttcctttatagtgttaaacactaaaattttaaaaaagatgaaaaagaaaaaaaaactttgtaaaatgcgagaacagaagcaaaagacactacgctctgtcattttatctttcttttgttgaaagactaaaaaaaaactgaaatgttttttagacaatcaaatgttaggtaagtgcaaaaacttgttttttcttactggtgtagaaattaatgcctttttttatttttcagttattttataataacgaaataaaaagaaccccccagctgccaggcgggttttggtgtttgaaatgcggggcaaagcactacatcactgcaaatagatacagagttagtctgcatgtctgtaggctgtgtgattgcggaaaatataaatgctgctaatatatttcctttttacaaaagcatatctaaatagatgattgttttgatgttaatctttgtaaattatgtattaccaattttaacattggatgtaattgcatacaaagcttgcatctcaatccttgaaagtctagtattaaatggaaaaaacttttcctaactgtggaaaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("ASH1L");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 155348180,
				PositionType.ZERO_BASED), "C", "G");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(8, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("6224-1G>C", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("?", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.CODING_TRANSCRIPT_INTRON_VARIANT,
				VariantEffect.SPLICE_ACCEPTOR_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc001fkt_3_second() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001fkt.3	chr1	-	155305051	155532324	155307450	155491310	28	155305051,155307879,155309118,155311726,155313104,155313394,155313973,155316173,155317446,155319117,155319332,155322496,155324263,155327106,155327375,155330091,155340294,155340567,155348071,155348293,155349833,155365249,155385534,155408117,155429587,155447676,155490890,155531943,	155307542,155308181,155309159,155311893,155313277,155313533,155314064,155316260,155317695,155319250,155319387,155322649,155324421,155327201,155327540,155330200,155340441,155340774,155348180,155348339,155349907,155365344,155385714,155408859,155429689,155452240,155491409,155532324,	Q9NR48-2	uc001fkt.3");
		this.builderForward
				.setSequence("aggagtggaaggttgaggggggcgctaggcgcccttcgctccctccctctggaggagctgccgccgccaccgccgccactctgctgctgccgccgccgccgccgccgctcccgccgccattttgggttcgctttgcggaggggagacgatcccagtctcggttgcgggacccgcctcccctcagtttgccccctttagccttccacctttcccttctcctctctcgcatttccgccagtcagcttacccgctggccgcctcctgacaagcgggagggatccgccgtggacccagggaagcggaggagcctggcggccaccccctcttccccacttccctgcactctcatcgctctcggcctcggcctcggcctccgacacgagaaagatgctggtttcgagttttggagatccttgttttttatggaacacagttctgtaaaattttcataagattccttggcaataacatacgcttgtgatggaccctagaaatactgctatgttaggattgggttctgattccgaaggtttttcaagaaagagtccttctgccatcagtactggcacattggtcagtaagagagaagtagagctagaaaaaaacacaaaggaggaagaggaccttcgcaaacggaatcgagaaagaaacatcgaagctgggaaagatgatggtttgactgatgcacagcaacagttttcagtgaaagaaacaaacttttcagagggaaatttaaaattgaaaattggcctccaggctaagagaactaaaaaacctccaaagaacttggagaactatgtatgtcgacctgccataaaaacaactattaagcacccaaggaaagcacttaaaagtggaaagatgacggatgaaaagaatgaacactgtccttcaaaacgagacccttcaaagttgtacaagaaagcagatgatgttgcagccattgaatgccagtctgaagaagtcatccgtcttcattcacagggagaaaacaatcctttgtctaagaagctgtctccagtacactcagaaatggcagattatattaatgcaacgccatctactcttcttggtagccgggatcctgatttaaaggacagagcattacttaatggaggaactagtgtaacagaaaagttggcacagctgattgctacctgtcctccttccaagtcttccaagacaaaaccgaagaagttaggaactggcactacagcaggattggttagcaaggatttgatcaggaaagcaggtgttggctctgtagctggaataatacataaggacttaataaaaaagccaaccatcagcacagcagttggattggtaactaaagatcctgggaaaaagccagtgtttaatgcagcagtaggattggtcaataaggactctgtgaaaaaactgggaactggcactacagcggtattcattaataaaaacttaggcaaaaagccaggaactatcactacagtaggactgctaagcaaagattcaggaaagaagctaggaattggtattgttccaggtttagtgcataaagagtctggcaagaagttaggacttggcactgtggttggactggttaataaagatttgggaaagaaattgggttctactgttggcctagtggccaaggactgtgcaaagaagattgtagcaagttcagcaatgggattggttaataaggacattggaaagaaactaatgagttgtcctttggcaggtctgatcagtaaagatgccataaaccttaaagccgaagcactgctccccactcaggaaccgcttaaggcttcttgtagtacaaacatcaataatcaggaaagtcaggaactttctgaatccctgaaagatagtgccaccagcaaaacttttgaaaagaatgttgtacggcagaataaagaaagcatattggaaaagttctcagtacgaaaagaaatcattaatttggagaaagaaatgtttaatgaaggaacatgcattcagcaagacagtttctcatccagtgaaaagggatcttatgaaacctcaaagcatgaaaagcagcctcctgtatattgcacttctccggactttaaaatgggaggtgcttctgatgtatctaccgctaaatccccattcagtgcagtaggagaaagcaatctcccttccccatcacctactgtatctgttaatcctttaaccagaagtccccctgaaacttcttcacagttggctcctaatccattacttttaagttctactacagaactaatcgaagaaatttctgaatctgttggaaagaaccagtttacttctgaaagtacccacttgaacgttggtcataggtcagttggtcatagtataagtattgaatgtaaagggattgataaagaggtaaatgattcaaaaactacccatatagatattccaagaataagctcttcccttggaaaaaagccaagtttgacttctgaatccagcattcatactattactccttcagttgttaacttcactagtttatttagtaataagccttttttaaaactgggtgcagtatctgcatcagacaaacactgccaagttgctgaaagcctaagtactagtttgcagtccaaaccattaaaaaaaagaaaaggaagaaaacctcggtggactaaagtggtggcaagaagcacatgccggtctccaaaagggctagaattagaaagatcagagctttttaaaaacgtttcatgtagctcactatcaaatagtaattctgagccagccaagtttatgaaaaacattggacccccttcatttgtagatcatgacttccttaaacgccgattgccaaagttgagcaaatccacagctccatctcttgctctcttagctgatagtgaaaaaccatctcataagtcttttgctactcacaaactatcctccagtatgtgtgtctctagtgaccttttgtctgatatttataagcccaaaagaggaaggcctaaatctaaggagatgcctcaactggaagggccacctaaaaggactttaaaaatccctgcttctaaagtgttttctttacagtctaaggaagaacaagaacccccaattttacagccagaaattgaaatcccttccttcaaacaaggtctgtctgtgtctccttttccaaaaaagagaggcaggcctaagaggcaaatgaggtcaccagtcaagatgaagccacctgtactgtcagtggctccatttgttgccactgaaagtccaagcaagctagaatctgaaagtgacaaccatagaagtagcagtgatttctttgagagcgaggatcaacttcaggatccagatgacctagatgacagtcataggccaagtgtctgtagtatgagtgaccttgagatggaaccagataaaaaaattaccaagagaaacaatggacaattaatgaaaacaattatccgcaaaataaataaaatgaagactttaaagagaaagaaactgttgaatcagattctttcaagttctgtagaatcaagtaataaagggaaagtgcaatccaaactccataatacggtatcaagtcttgctgccacatttggctctaaattgggccaacagataaatgtcagcaagaaaggaaccatttatataggaaagagaagaggtcgcaaaccaaaaactgtcttaaatggtattctttctggtagtcctactagccttgctgttcttgagcaaacagctcaacaggcagctgggtcagcattaggacagattcttcccccattactgccttcatctgctagtagttctgagattcttccatcacctatttgctctcagtcttctgggactagtggaggtcagagccctgtaagtagtgatgcaggttttgttgaacccagttcagtgccatatttgcatttacactccagacagggcagtatgattcagactcttgcaatgaagaaggcctcaaaggggaggaggcggttatctcctcctactttgttgccaaattctccttcgcacttgagtgaactcacatctctaaaagaagctactccttccccaatcagtgagtctcatagtgatgagaccattcccagtgatagtggaattggaacagataataacagcacatcagacagggcagagaaattttgtgggcaaaaaaagaggaggcattcttttgagcatgtttctctgattccccctgaaacctctacagtgctaagcagtcttaaagaaaaacataaacacaaatgtaagcgcaggaatcatgattacctcagctatgacaagatgaaaaggcagaaacgaaaacggaaaaagaaatatccccagcttcgaaatagacaggatccagactttattgcagagctggaggaactaataagtcgcctaagtgaaattcggatcactcatcgaagtcatcattttatcccccgagatcttctgccaactatctttcgaatcaactttaatagtttctatacacatccttctttccccttagaccctttgcactacattcgaaaacctgacttaaaaaagaaaagagggagaccccctaagatgagggaggcaatggctgaaatgccttttatgcacagccttagttttcctctttctagtactggattctatccatcttatggtatgccttactctccttcaccccttacagctgctcccataggattaggttactatggaaggtatcctcccactctttatccacctcctccatctccttctttcaccacgccacttccacctccttcctatatgcatgctggtcatttacttctcaatcctgccaaataccataagaaaaagcataagctacttcgacaggaggcctttcttacaaccagcaggactcccctcctttccatgagtacctaccccagtgttcctcctgagatggcctatggttggatggttgagcacaaacacaggcaccgtcacaaacacagagaacaccgttcttctgaacaaccccaggtttctatggacactggctcttcccgatctgtcctggaatctttgaagcgctatagatttggaaaggatgctgttggagagcgatataagcataaggaaaagcaccgttgtcacatgtcctgccctcatctctctccttcaaaaagcttaataaacagagaggaacagtgggtccaccgagagccttcagaatctagtccattggccttgggattgcagacacctttacagattgactgttcagaaagttctccaagcttatcccttggaggattcactcccaactctgagccagccagcagtgatgaacatacaaaccttttcacaagtgcaataggcagctgcagagtttcaaaccctaactccagtggccggaagaaattaactgacagccctggactcttttctgcacaggacacttcactaaatcggcttcacagaaaggagtcactgccttctaacgaaagggcagtacagactttggcaggctcccagccaacctctgataaaccctcccagcggccatcagagagcacaaattgtagccctacccggaaaaggtcttcatctgagagtacttcttcaacagtaaacggagttccctctcgaagtccaagattagttgcttctggggatgactctgtggatagtctgctgcagcggatggtacaaaatgaggaccaagagcccatggagaaaagtattgatgctgtgattgcaactgcctctgcaccaccttcttccagtccaggccgtagccacagcaaggaccgaaccctgggaaaaccagacagccttttagtgcctgcagtcacaagtgactcttgcaataatagcatctcactcctatctgaaaagttgacaagcagctgttccccccatcatatcaagagaagtgtagtggaagctatgcaacgccaagctcggaaaatgtgcaattacgacaaaatcttggccacaaagaaaaacctagaccatgtcaataaaatcttaaaagccaaaaaacttcaaaggcaggccaggacagggaataactttgtgaaacgtaggccaggtcgacctcggaaatgtccccttcaggctgtcgtatcaatgcaagcattccaggctgctcagtttgtcaacccagaattgaacagagacgaggaaggagcagcactgcacctcagtcctgacacagttacagatgtaattgaggctgttgttcagagtgtaaatctgaacccagaacataaaaaggggttgaagagaaaaggttggctattggaagaacagaccagaaaaaagcagaagccattaccagaggaagaagagcaagagaataataaaagctttaatgaagcaccagttgagattcccagtccttctgaaaccccagctaaaccttctgaacctgaaagtaccttgcagcctgtgctttctctcatcccaagggaaaagaagcccccacgtcccccaaagaagaagtatcagaaagcagggctgtattctgacgtttacaaaactacagacccaaagagtcgattgatccaattaaagaaagagaagctggagtatactccaggagagcatgaatatggattatttccagcgcccattcatgttggaaagtatctaagacaaaagagaattgacttccagcttccttatgatatcctttggcagtggaaacacaatcagctatacaaaaagccagatgtcccactatataagaaaattcgttcaaatgtctacgttgatgtcaaacccctttctggttacgaagctaccacctgtaactgtaagaagccagatgatgacaccaggaagggctgtgttgatgactgcctcaatagaatgatctttgctgagtgttcccccaacacttgcccatgtggcgagcaatgctgtaaccagaggatacagaggcatgaatgggtgcaatgtctagaacgatttcgagctgaggaaaaaggttggggaatcagaaccaaagagcccctaaaagctgggcagttcatcattgaatacctaggggaggtcgtcagtgaacaggagttcaggaacaggatgattgagcagtatcataatcacagtgaccactactgcctgaacctggatagtgggatggtgattgacagttaccgcatgggaaatgaggcccgattcatcaaccatagctgtgacccaaattgtgaaatgcagaaatggtctgttaatggagtataccggattggactctatgctcttaaagacatgccagctgggactgaactcacttatgattataactttcattccttcaatgtggaaaaacagcaactttgtaagtgtggctttgagaaatgtcgaggaatcatcggaggcaagagtcagcgtgtgaatggactcaccagcagcaaaaacagccagcccatggccacacacaaaaaatctggacggtcaaaagagaagagaaagtctaagcacaagctgaagaaaaggagaggccatctctctgaggaacccagtgaaaatatcaacaccccaactagattgaccccccaattacagatgaagccaatgtccaatcgtgaaaggaactttgtgttaaagcatcatgtattcttggtccgaaactgggagaagattcgtcaaaaacaggaggaagtaaagcacaccagtgataatattcactcagcatcattatatacccgttggaatgggatctgccgagatgatgggaatatcaagtctgatgtcttcatgacccagttctctgccctgcagacagctcgatctgttcgaacaagacggttggcagctgcagaggaaaatattgaagtggctcgggcagcccgcctagcccagatcttcaaagaaatttgtgatggtatcatctcttataaagattcttcccggcaagcactggcagctccacttttgaaccttcccccaaagaaaaagaatgctgattattatgagaagatctctgatcccctagatcttatcaccatagagaagcagatcctcactggttactataagacagtggaagcttttgatgctgacatgctcaaagtctttcggaatgctgagaagtactatgggcgtaaatccccagttgggagagatgtttgtcgtctacgaaaggcctattacaatgcccggcatgaggcatcagcccagattgatgagattgtgggagagacagcaagtgaggcagacagcagtgagacctcagtctctgaaaaggagaatgggcatgagaaggacgacgatgttattcgctgtatctgtggcctctacaaggatgaaggtctcatgatccagtgtgacaagtgcatggtatggcagcactgtgattgtatgggagtgaactcagatgtggagcactacctttgtgagcagtgtgacccaaggcctgtggacagggaggttcccatgatccctcggccccactatgcccaacctggctgtgtctacttcatctgtttgctccgagatgacttgctgcttcgtcagggtgactgtgtgtatctgatgagggatagtcggcgcacccctgatggccacccggtccgtcagtcctatcgactgttatctcacattaaccgagataaacttgacatctttcgcattgagaagctttggaagaatgaaaaagaggaacggtttgcctttggtcaccattatttccgtccccacgaaacacaccactctccatcccgtcggttctatcataatgaactatttcgggtgccactctatgagatcattcccttggaggctgtagtggggacctgctgtgtgttggacctttatacgtattgtaaagggagacccaaaggagtaaaggagcaagatgtgtacatctgtgattatcggcttgacaagtcagcacacctgttttacaagatccaccggaaccgctatcctgtctgcaccaaaccctatgcttttgatcacttccccaagaagctcactcccaaaaaagatttctcgcctcattacgtcccagacaactacaagaggaatggaggacgatcatcctggaagtctgagcgctcaaagccacccctaaaagacttgggccaggaggatgatgctctacccttgattgaagaggttctagccagtcaagagcaagcagccaatgagatacccagcctggaggagccagaacgggaaggggccactgctaacgtcagtgagggtgaaaaaaaaacagaggaaagtagtcaagaaccccagtcaacctgtacccctgaggaacgacggcataaccaacgggaacgactcaaccagatcttgctcaatctccttgaaaaaatccctggaaaaaatgccattgatgtgacctacttgctggaggaaggatcaggcaggaaactgcgaaggcgtactttgtttatcccagaaaacagctttcgaaagtgaccctcaaagaatgagaacctcaagcatctgggatccagtggagctaatcagtcctgcctcctgctctctgggtatagacaggggtgggaagggtccatctgggcaaggggaatggggccatgttgttgacattaggtacttaataagccttggagctagtggagagggagaggaaagggttctgtccaagacagttcaggttaattaattttcttctccattgcttcaccttaagggttaataatgtagagaggagggaggaccacattgatgaccagaacctactggtactttatagcatttgccccaccccacagcttaggtttttctgtcatcctcagatcccacaggcattgcgaagaagctgcttcctatacccaggtataactcaaaatccaaagggatagggccaggatccctattcctaccccatctattctctgttggctccaagagctaccccagagaccttaaacagaaacagtagctgaggcttcttcctagatacctgactagggaagtttgtctctcctttcttgcccaaccaggtcaaagtaaaatgtgagttgacagctcaaagcacttgtaactgctgccccctccctacctctactccccaaaatggaatcatgggatagggaaggcccccatggggtcagaagggcacggtagttcttgcaattatttttgttttacccttcataacctgtcaaacatatttttttctaatgagaaagccaggcccccgccagcacacatgctgtttttaatgcgctgtagttcttgtgtgtctgctgtgctgtgcaaatggagattcagttcaaaataaaatcatttaaaaacctacataaaaagaactctaaacccacccctgcaacaaaagtcactacataaactgttcagcagtattcacctatcagagtatttgttgtgagtatagattatcaattgaaaacactactcttgttttcttaattgtacagttttcaatgtccctttcttaaagagacagtatatttctcttcacccctagcccatcttccctcaccctcctgaatgacatcaggaggtatatccagggtgtctccttccttcctactctcttgaccagaagttaacagactatactgtctctttaaaaataaaatttaaaaagctttgttgtcttttcagacatacatatgcatatatgttttagatgttcttataagagaaaagatggtttttaaatgtgccaagttgtgtgtgtgtgtgtatatatatgtgtgtatgtgtgtgtatatatatatgtgtgtgtgtatatatatacacacacacacacacacctgctgtgtgattggtaagcaatacaatagtaaacatgtccccattacttttttctaatattggaccaatgctgtcctaattgtacatttccccttatggtgacgatgctctgactcgtttaggtagacacattgaccaccttccattccattaaatattttttcctttttcccctttctgtgtcattcttgaggaaaaaacaaaagagagaggggatgccaatgatccccttgagcagagaaaaagcaaaataaatattttattaaagaaaaaagagaattaagaaaatagtttggagtattttcttactgtagagaagcactgtacattactaagagacctgggtataagatactcacatgtggagctggaaaaatcgcatgtccaagcccgtttgagtggtttcttttgtttttcattgcagggagtgggtgggagggaggtgggactaggggcactttgggggtctccttttagtcaaaagcgagaaaatgacaagaaagagattaaaattcaatgtttcctttatagtgttaaacactaaaattttaaaaaagatgaaaaagaaaaaaaaactttgtaaaatgcgagaacagaagcaaaagacactacgctctgtcattttatctttcttttgttgaaagactaaaaaaaaactgaaatgttttttagacaatcaaatgttaggtaagtgcaaaaacttgttttttcttactggtgtagaaattaatgcctttttttatttttcagttattttataataacgaaataaaaagaaccccccagctgccaggcgggttttggtgtttgaaatgcggggcaaagcactacatcactgcaaatagatacagagttagtctgcatgtctgtaggctgtgtgattgcggaaaatataaatgctgctaatatatttcctttttacaaaagcatatctaaatagatgattgttttgatgttaatctttgtaaattatgtattaccaattttaacattggatgtaattgcatacaaagcttgcatctcaatccttgaaagtctagtattaaatggaaaaaacttttcctaactgtggaaaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("ASH1L");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 155348069,
				PositionType.ZERO_BASED), "A", "G");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(9, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("6332+2T>C", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("?", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.CODING_TRANSCRIPT_INTRON_VARIANT,
				VariantEffect.SPLICE_DONOR_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc001fkt_3_third() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001fkt.3	chr1	-	155305051	155532324	155307450	155491310	28	155305051,155307879,155309118,155311726,155313104,155313394,155313973,155316173,155317446,155319117,155319332,155322496,155324263,155327106,155327375,155330091,155340294,155340567,155348071,155348293,155349833,155365249,155385534,155408117,155429587,155447676,155490890,155531943,	155307542,155308181,155309159,155311893,155313277,155313533,155314064,155316260,155317695,155319250,155319387,155322649,155324421,155327201,155327540,155330200,155340441,155340774,155348180,155348339,155349907,155365344,155385714,155408859,155429689,155452240,155491409,155532324,	Q9NR48-2	uc001fkt.3");
		this.builderForward
				.setSequence("aggagtggaaggttgaggggggcgctaggcgcccttcgctccctccctctggaggagctgccgccgccaccgccgccactctgctgctgccgccgccgccgccgccgctcccgccgccattttgggttcgctttgcggaggggagacgatcccagtctcggttgcgggacccgcctcccctcagtttgccccctttagccttccacctttcccttctcctctctcgcatttccgccagtcagcttacccgctggccgcctcctgacaagcgggagggatccgccgtggacccagggaagcggaggagcctggcggccaccccctcttccccacttccctgcactctcatcgctctcggcctcggcctcggcctccgacacgagaaagatgctggtttcgagttttggagatccttgttttttatggaacacagttctgtaaaattttcataagattccttggcaataacatacgcttgtgatggaccctagaaatactgctatgttaggattgggttctgattccgaaggtttttcaagaaagagtccttctgccatcagtactggcacattggtcagtaagagagaagtagagctagaaaaaaacacaaaggaggaagaggaccttcgcaaacggaatcgagaaagaaacatcgaagctgggaaagatgatggtttgactgatgcacagcaacagttttcagtgaaagaaacaaacttttcagagggaaatttaaaattgaaaattggcctccaggctaagagaactaaaaaacctccaaagaacttggagaactatgtatgtcgacctgccataaaaacaactattaagcacccaaggaaagcacttaaaagtggaaagatgacggatgaaaagaatgaacactgtccttcaaaacgagacccttcaaagttgtacaagaaagcagatgatgttgcagccattgaatgccagtctgaagaagtcatccgtcttcattcacagggagaaaacaatcctttgtctaagaagctgtctccagtacactcagaaatggcagattatattaatgcaacgccatctactcttcttggtagccgggatcctgatttaaaggacagagcattacttaatggaggaactagtgtaacagaaaagttggcacagctgattgctacctgtcctccttccaagtcttccaagacaaaaccgaagaagttaggaactggcactacagcaggattggttagcaaggatttgatcaggaaagcaggtgttggctctgtagctggaataatacataaggacttaataaaaaagccaaccatcagcacagcagttggattggtaactaaagatcctgggaaaaagccagtgtttaatgcagcagtaggattggtcaataaggactctgtgaaaaaactgggaactggcactacagcggtattcattaataaaaacttaggcaaaaagccaggaactatcactacagtaggactgctaagcaaagattcaggaaagaagctaggaattggtattgttccaggtttagtgcataaagagtctggcaagaagttaggacttggcactgtggttggactggttaataaagatttgggaaagaaattgggttctactgttggcctagtggccaaggactgtgcaaagaagattgtagcaagttcagcaatgggattggttaataaggacattggaaagaaactaatgagttgtcctttggcaggtctgatcagtaaagatgccataaaccttaaagccgaagcactgctccccactcaggaaccgcttaaggcttcttgtagtacaaacatcaataatcaggaaagtcaggaactttctgaatccctgaaagatagtgccaccagcaaaacttttgaaaagaatgttgtacggcagaataaagaaagcatattggaaaagttctcagtacgaaaagaaatcattaatttggagaaagaaatgtttaatgaaggaacatgcattcagcaagacagtttctcatccagtgaaaagggatcttatgaaacctcaaagcatgaaaagcagcctcctgtatattgcacttctccggactttaaaatgggaggtgcttctgatgtatctaccgctaaatccccattcagtgcagtaggagaaagcaatctcccttccccatcacctactgtatctgttaatcctttaaccagaagtccccctgaaacttcttcacagttggctcctaatccattacttttaagttctactacagaactaatcgaagaaatttctgaatctgttggaaagaaccagtttacttctgaaagtacccacttgaacgttggtcataggtcagttggtcatagtataagtattgaatgtaaagggattgataaagaggtaaatgattcaaaaactacccatatagatattccaagaataagctcttcccttggaaaaaagccaagtttgacttctgaatccagcattcatactattactccttcagttgttaacttcactagtttatttagtaataagccttttttaaaactgggtgcagtatctgcatcagacaaacactgccaagttgctgaaagcctaagtactagtttgcagtccaaaccattaaaaaaaagaaaaggaagaaaacctcggtggactaaagtggtggcaagaagcacatgccggtctccaaaagggctagaattagaaagatcagagctttttaaaaacgtttcatgtagctcactatcaaatagtaattctgagccagccaagtttatgaaaaacattggacccccttcatttgtagatcatgacttccttaaacgccgattgccaaagttgagcaaatccacagctccatctcttgctctcttagctgatagtgaaaaaccatctcataagtcttttgctactcacaaactatcctccagtatgtgtgtctctagtgaccttttgtctgatatttataagcccaaaagaggaaggcctaaatctaaggagatgcctcaactggaagggccacctaaaaggactttaaaaatccctgcttctaaagtgttttctttacagtctaaggaagaacaagaacccccaattttacagccagaaattgaaatcccttccttcaaacaaggtctgtctgtgtctccttttccaaaaaagagaggcaggcctaagaggcaaatgaggtcaccagtcaagatgaagccacctgtactgtcagtggctccatttgttgccactgaaagtccaagcaagctagaatctgaaagtgacaaccatagaagtagcagtgatttctttgagagcgaggatcaacttcaggatccagatgacctagatgacagtcataggccaagtgtctgtagtatgagtgaccttgagatggaaccagataaaaaaattaccaagagaaacaatggacaattaatgaaaacaattatccgcaaaataaataaaatgaagactttaaagagaaagaaactgttgaatcagattctttcaagttctgtagaatcaagtaataaagggaaagtgcaatccaaactccataatacggtatcaagtcttgctgccacatttggctctaaattgggccaacagataaatgtcagcaagaaaggaaccatttatataggaaagagaagaggtcgcaaaccaaaaactgtcttaaatggtattctttctggtagtcctactagccttgctgttcttgagcaaacagctcaacaggcagctgggtcagcattaggacagattcttcccccattactgccttcatctgctagtagttctgagattcttccatcacctatttgctctcagtcttctgggactagtggaggtcagagccctgtaagtagtgatgcaggttttgttgaacccagttcagtgccatatttgcatttacactccagacagggcagtatgattcagactcttgcaatgaagaaggcctcaaaggggaggaggcggttatctcctcctactttgttgccaaattctccttcgcacttgagtgaactcacatctctaaaagaagctactccttccccaatcagtgagtctcatagtgatgagaccattcccagtgatagtggaattggaacagataataacagcacatcagacagggcagagaaattttgtgggcaaaaaaagaggaggcattcttttgagcatgtttctctgattccccctgaaacctctacagtgctaagcagtcttaaagaaaaacataaacacaaatgtaagcgcaggaatcatgattacctcagctatgacaagatgaaaaggcagaaacgaaaacggaaaaagaaatatccccagcttcgaaatagacaggatccagactttattgcagagctggaggaactaataagtcgcctaagtgaaattcggatcactcatcgaagtcatcattttatcccccgagatcttctgccaactatctttcgaatcaactttaatagtttctatacacatccttctttccccttagaccctttgcactacattcgaaaacctgacttaaaaaagaaaagagggagaccccctaagatgagggaggcaatggctgaaatgccttttatgcacagccttagttttcctctttctagtactggattctatccatcttatggtatgccttactctccttcaccccttacagctgctcccataggattaggttactatggaaggtatcctcccactctttatccacctcctccatctccttctttcaccacgccacttccacctccttcctatatgcatgctggtcatttacttctcaatcctgccaaataccataagaaaaagcataagctacttcgacaggaggcctttcttacaaccagcaggactcccctcctttccatgagtacctaccccagtgttcctcctgagatggcctatggttggatggttgagcacaaacacaggcaccgtcacaaacacagagaacaccgttcttctgaacaaccccaggtttctatggacactggctcttcccgatctgtcctggaatctttgaagcgctatagatttggaaaggatgctgttggagagcgatataagcataaggaaaagcaccgttgtcacatgtcctgccctcatctctctccttcaaaaagcttaataaacagagaggaacagtgggtccaccgagagccttcagaatctagtccattggccttgggattgcagacacctttacagattgactgttcagaaagttctccaagcttatcccttggaggattcactcccaactctgagccagccagcagtgatgaacatacaaaccttttcacaagtgcaataggcagctgcagagtttcaaaccctaactccagtggccggaagaaattaactgacagccctggactcttttctgcacaggacacttcactaaatcggcttcacagaaaggagtcactgccttctaacgaaagggcagtacagactttggcaggctcccagccaacctctgataaaccctcccagcggccatcagagagcacaaattgtagccctacccggaaaaggtcttcatctgagagtacttcttcaacagtaaacggagttccctctcgaagtccaagattagttgcttctggggatgactctgtggatagtctgctgcagcggatggtacaaaatgaggaccaagagcccatggagaaaagtattgatgctgtgattgcaactgcctctgcaccaccttcttccagtccaggccgtagccacagcaaggaccgaaccctgggaaaaccagacagccttttagtgcctgcagtcacaagtgactcttgcaataatagcatctcactcctatctgaaaagttgacaagcagctgttccccccatcatatcaagagaagtgtagtggaagctatgcaacgccaagctcggaaaatgtgcaattacgacaaaatcttggccacaaagaaaaacctagaccatgtcaataaaatcttaaaagccaaaaaacttcaaaggcaggccaggacagggaataactttgtgaaacgtaggccaggtcgacctcggaaatgtccccttcaggctgtcgtatcaatgcaagcattccaggctgctcagtttgtcaacccagaattgaacagagacgaggaaggagcagcactgcacctcagtcctgacacagttacagatgtaattgaggctgttgttcagagtgtaaatctgaacccagaacataaaaaggggttgaagagaaaaggttggctattggaagaacagaccagaaaaaagcagaagccattaccagaggaagaagagcaagagaataataaaagctttaatgaagcaccagttgagattcccagtccttctgaaaccccagctaaaccttctgaacctgaaagtaccttgcagcctgtgctttctctcatcccaagggaaaagaagcccccacgtcccccaaagaagaagtatcagaaagcagggctgtattctgacgtttacaaaactacagacccaaagagtcgattgatccaattaaagaaagagaagctggagtatactccaggagagcatgaatatggattatttccagcgcccattcatgttggaaagtatctaagacaaaagagaattgacttccagcttccttatgatatcctttggcagtggaaacacaatcagctatacaaaaagccagatgtcccactatataagaaaattcgttcaaatgtctacgttgatgtcaaacccctttctggttacgaagctaccacctgtaactgtaagaagccagatgatgacaccaggaagggctgtgttgatgactgcctcaatagaatgatctttgctgagtgttcccccaacacttgcccatgtggcgagcaatgctgtaaccagaggatacagaggcatgaatgggtgcaatgtctagaacgatttcgagctgaggaaaaaggttggggaatcagaaccaaagagcccctaaaagctgggcagttcatcattgaatacctaggggaggtcgtcagtgaacaggagttcaggaacaggatgattgagcagtatcataatcacagtgaccactactgcctgaacctggatagtgggatggtgattgacagttaccgcatgggaaatgaggcccgattcatcaaccatagctgtgacccaaattgtgaaatgcagaaatggtctgttaatggagtataccggattggactctatgctcttaaagacatgccagctgggactgaactcacttatgattataactttcattccttcaatgtggaaaaacagcaactttgtaagtgtggctttgagaaatgtcgaggaatcatcggaggcaagagtcagcgtgtgaatggactcaccagcagcaaaaacagccagcccatggccacacacaaaaaatctggacggtcaaaagagaagagaaagtctaagcacaagctgaagaaaaggagaggccatctctctgaggaacccagtgaaaatatcaacaccccaactagattgaccccccaattacagatgaagccaatgtccaatcgtgaaaggaactttgtgttaaagcatcatgtattcttggtccgaaactgggagaagattcgtcaaaaacaggaggaagtaaagcacaccagtgataatattcactcagcatcattatatacccgttggaatgggatctgccgagatgatgggaatatcaagtctgatgtcttcatgacccagttctctgccctgcagacagctcgatctgttcgaacaagacggttggcagctgcagaggaaaatattgaagtggctcgggcagcccgcctagcccagatcttcaaagaaatttgtgatggtatcatctcttataaagattcttcccggcaagcactggcagctccacttttgaaccttcccccaaagaaaaagaatgctgattattatgagaagatctctgatcccctagatcttatcaccatagagaagcagatcctcactggttactataagacagtggaagcttttgatgctgacatgctcaaagtctttcggaatgctgagaagtactatgggcgtaaatccccagttgggagagatgtttgtcgtctacgaaaggcctattacaatgcccggcatgaggcatcagcccagattgatgagattgtgggagagacagcaagtgaggcagacagcagtgagacctcagtctctgaaaaggagaatgggcatgagaaggacgacgatgttattcgctgtatctgtggcctctacaaggatgaaggtctcatgatccagtgtgacaagtgcatggtatggcagcactgtgattgtatgggagtgaactcagatgtggagcactacctttgtgagcagtgtgacccaaggcctgtggacagggaggttcccatgatccctcggccccactatgcccaacctggctgtgtctacttcatctgtttgctccgagatgacttgctgcttcgtcagggtgactgtgtgtatctgatgagggatagtcggcgcacccctgatggccacccggtccgtcagtcctatcgactgttatctcacattaaccgagataaacttgacatctttcgcattgagaagctttggaagaatgaaaaagaggaacggtttgcctttggtcaccattatttccgtccccacgaaacacaccactctccatcccgtcggttctatcataatgaactatttcgggtgccactctatgagatcattcccttggaggctgtagtggggacctgctgtgtgttggacctttatacgtattgtaaagggagacccaaaggagtaaaggagcaagatgtgtacatctgtgattatcggcttgacaagtcagcacacctgttttacaagatccaccggaaccgctatcctgtctgcaccaaaccctatgcttttgatcacttccccaagaagctcactcccaaaaaagatttctcgcctcattacgtcccagacaactacaagaggaatggaggacgatcatcctggaagtctgagcgctcaaagccacccctaaaagacttgggccaggaggatgatgctctacccttgattgaagaggttctagccagtcaagagcaagcagccaatgagatacccagcctggaggagccagaacgggaaggggccactgctaacgtcagtgagggtgaaaaaaaaacagaggaaagtagtcaagaaccccagtcaacctgtacccctgaggaacgacggcataaccaacgggaacgactcaaccagatcttgctcaatctccttgaaaaaatccctggaaaaaatgccattgatgtgacctacttgctggaggaaggatcaggcaggaaactgcgaaggcgtactttgtttatcccagaaaacagctttcgaaagtgaccctcaaagaatgagaacctcaagcatctgggatccagtggagctaatcagtcctgcctcctgctctctgggtatagacaggggtgggaagggtccatctgggcaaggggaatggggccatgttgttgacattaggtacttaataagccttggagctagtggagagggagaggaaagggttctgtccaagacagttcaggttaattaattttcttctccattgcttcaccttaagggttaataatgtagagaggagggaggaccacattgatgaccagaacctactggtactttatagcatttgccccaccccacagcttaggtttttctgtcatcctcagatcccacaggcattgcgaagaagctgcttcctatacccaggtataactcaaaatccaaagggatagggccaggatccctattcctaccccatctattctctgttggctccaagagctaccccagagaccttaaacagaaacagtagctgaggcttcttcctagatacctgactagggaagtttgtctctcctttcttgcccaaccaggtcaaagtaaaatgtgagttgacagctcaaagcacttgtaactgctgccccctccctacctctactccccaaaatggaatcatgggatagggaaggcccccatggggtcagaagggcacggtagttcttgcaattatttttgttttacccttcataacctgtcaaacatatttttttctaatgagaaagccaggcccccgccagcacacatgctgtttttaatgcgctgtagttcttgtgtgtctgctgtgctgtgcaaatggagattcagttcaaaataaaatcatttaaaaacctacataaaaagaactctaaacccacccctgcaacaaaagtcactacataaactgttcagcagtattcacctatcagagtatttgttgtgagtatagattatcaattgaaaacactactcttgttttcttaattgtacagttttcaatgtccctttcttaaagagacagtatatttctcttcacccctagcccatcttccctcaccctcctgaatgacatcaggaggtatatccagggtgtctccttccttcctactctcttgaccagaagttaacagactatactgtctctttaaaaataaaatttaaaaagctttgttgtcttttcagacatacatatgcatatatgttttagatgttcttataagagaaaagatggtttttaaatgtgccaagttgtgtgtgtgtgtgtatatatatgtgtgtatgtgtgtgtatatatatatgtgtgtgtgtatatatatacacacacacacacacacctgctgtgtgattggtaagcaatacaatagtaaacatgtccccattacttttttctaatattggaccaatgctgtcctaattgtacatttccccttatggtgacgatgctctgactcgtttaggtagacacattgaccaccttccattccattaaatattttttcctttttcccctttctgtgtcattcttgaggaaaaaacaaaagagagaggggatgccaatgatccccttgagcagagaaaaagcaaaataaatattttattaaagaaaaaagagaattaagaaaatagtttggagtattttcttactgtagagaagcactgtacattactaagagacctgggtataagatactcacatgtggagctggaaaaatcgcatgtccaagcccgtttgagtggtttcttttgtttttcattgcagggagtgggtgggagggaggtgggactaggggcactttgggggtctccttttagtcaaaagcgagaaaatgacaagaaagagattaaaattcaatgtttcctttatagtgttaaacactaaaattttaaaaaagatgaaaaagaaaaaaaaactttgtaaaatgcgagaacagaagcaaaagacactacgctctgtcattttatctttcttttgttgaaagactaaaaaaaaactgaaatgttttttagacaatcaaatgttaggtaagtgcaaaaacttgttttttcttactggtgtagaaattaatgcctttttttatttttcagttattttataataacgaaataaaaagaaccccccagctgccaggcgggttttggtgtttgaaatgcggggcaaagcactacatcactgcaaatagatacagagttagtctgcatgtctgtaggctgtgtgattgcggaaaatataaatgctgctaatatatttcctttttacaaaagcatatctaaatagatgattgttttgatgttaatctttgtaaattatgtattaccaattttaacattggatgtaattgcatacaaagcttgcatctcaatccttgaaagtctagtattaaatggaaaaaacttttcctaactgtggaaaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("ASH1L");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 155348068,
				PositionType.ZERO_BASED), "T", "G");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(9, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("6332+3A>C", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("?", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.CODING_TRANSCRIPT_INTRON_VARIANT,
				VariantEffect.SPLICE_REGION_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc001fkt_3_fourth() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001fkt.3	chr1	-	155305051	155532324	155307450	155491310	28	155305051,155307879,155309118,155311726,155313104,155313394,155313973,155316173,155317446,155319117,155319332,155322496,155324263,155327106,155327375,155330091,155340294,155340567,155348071,155348293,155349833,155365249,155385534,155408117,155429587,155447676,155490890,155531943,	155307542,155308181,155309159,155311893,155313277,155313533,155314064,155316260,155317695,155319250,155319387,155322649,155324421,155327201,155327540,155330200,155340441,155340774,155348180,155348339,155349907,155365344,155385714,155408859,155429689,155452240,155491409,155532324,	Q9NR48-2	uc001fkt.3");
		this.builderForward
				.setSequence("aggagtggaaggttgaggggggcgctaggcgcccttcgctccctccctctggaggagctgccgccgccaccgccgccactctgctgctgccgccgccgccgccgccgctcccgccgccattttgggttcgctttgcggaggggagacgatcccagtctcggttgcgggacccgcctcccctcagtttgccccctttagccttccacctttcccttctcctctctcgcatttccgccagtcagcttacccgctggccgcctcctgacaagcgggagggatccgccgtggacccagggaagcggaggagcctggcggccaccccctcttccccacttccctgcactctcatcgctctcggcctcggcctcggcctccgacacgagaaagatgctggtttcgagttttggagatccttgttttttatggaacacagttctgtaaaattttcataagattccttggcaataacatacgcttgtgatggaccctagaaatactgctatgttaggattgggttctgattccgaaggtttttcaagaaagagtccttctgccatcagtactggcacattggtcagtaagagagaagtagagctagaaaaaaacacaaaggaggaagaggaccttcgcaaacggaatcgagaaagaaacatcgaagctgggaaagatgatggtttgactgatgcacagcaacagttttcagtgaaagaaacaaacttttcagagggaaatttaaaattgaaaattggcctccaggctaagagaactaaaaaacctccaaagaacttggagaactatgtatgtcgacctgccataaaaacaactattaagcacccaaggaaagcacttaaaagtggaaagatgacggatgaaaagaatgaacactgtccttcaaaacgagacccttcaaagttgtacaagaaagcagatgatgttgcagccattgaatgccagtctgaagaagtcatccgtcttcattcacagggagaaaacaatcctttgtctaagaagctgtctccagtacactcagaaatggcagattatattaatgcaacgccatctactcttcttggtagccgggatcctgatttaaaggacagagcattacttaatggaggaactagtgtaacagaaaagttggcacagctgattgctacctgtcctccttccaagtcttccaagacaaaaccgaagaagttaggaactggcactacagcaggattggttagcaaggatttgatcaggaaagcaggtgttggctctgtagctggaataatacataaggacttaataaaaaagccaaccatcagcacagcagttggattggtaactaaagatcctgggaaaaagccagtgtttaatgcagcagtaggattggtcaataaggactctgtgaaaaaactgggaactggcactacagcggtattcattaataaaaacttaggcaaaaagccaggaactatcactacagtaggactgctaagcaaagattcaggaaagaagctaggaattggtattgttccaggtttagtgcataaagagtctggcaagaagttaggacttggcactgtggttggactggttaataaagatttgggaaagaaattgggttctactgttggcctagtggccaaggactgtgcaaagaagattgtagcaagttcagcaatgggattggttaataaggacattggaaagaaactaatgagttgtcctttggcaggtctgatcagtaaagatgccataaaccttaaagccgaagcactgctccccactcaggaaccgcttaaggcttcttgtagtacaaacatcaataatcaggaaagtcaggaactttctgaatccctgaaagatagtgccaccagcaaaacttttgaaaagaatgttgtacggcagaataaagaaagcatattggaaaagttctcagtacgaaaagaaatcattaatttggagaaagaaatgtttaatgaaggaacatgcattcagcaagacagtttctcatccagtgaaaagggatcttatgaaacctcaaagcatgaaaagcagcctcctgtatattgcacttctccggactttaaaatgggaggtgcttctgatgtatctaccgctaaatccccattcagtgcagtaggagaaagcaatctcccttccccatcacctactgtatctgttaatcctttaaccagaagtccccctgaaacttcttcacagttggctcctaatccattacttttaagttctactacagaactaatcgaagaaatttctgaatctgttggaaagaaccagtttacttctgaaagtacccacttgaacgttggtcataggtcagttggtcatagtataagtattgaatgtaaagggattgataaagaggtaaatgattcaaaaactacccatatagatattccaagaataagctcttcccttggaaaaaagccaagtttgacttctgaatccagcattcatactattactccttcagttgttaacttcactagtttatttagtaataagccttttttaaaactgggtgcagtatctgcatcagacaaacactgccaagttgctgaaagcctaagtactagtttgcagtccaaaccattaaaaaaaagaaaaggaagaaaacctcggtggactaaagtggtggcaagaagcacatgccggtctccaaaagggctagaattagaaagatcagagctttttaaaaacgtttcatgtagctcactatcaaatagtaattctgagccagccaagtttatgaaaaacattggacccccttcatttgtagatcatgacttccttaaacgccgattgccaaagttgagcaaatccacagctccatctcttgctctcttagctgatagtgaaaaaccatctcataagtcttttgctactcacaaactatcctccagtatgtgtgtctctagtgaccttttgtctgatatttataagcccaaaagaggaaggcctaaatctaaggagatgcctcaactggaagggccacctaaaaggactttaaaaatccctgcttctaaagtgttttctttacagtctaaggaagaacaagaacccccaattttacagccagaaattgaaatcccttccttcaaacaaggtctgtctgtgtctccttttccaaaaaagagaggcaggcctaagaggcaaatgaggtcaccagtcaagatgaagccacctgtactgtcagtggctccatttgttgccactgaaagtccaagcaagctagaatctgaaagtgacaaccatagaagtagcagtgatttctttgagagcgaggatcaacttcaggatccagatgacctagatgacagtcataggccaagtgtctgtagtatgagtgaccttgagatggaaccagataaaaaaattaccaagagaaacaatggacaattaatgaaaacaattatccgcaaaataaataaaatgaagactttaaagagaaagaaactgttgaatcagattctttcaagttctgtagaatcaagtaataaagggaaagtgcaatccaaactccataatacggtatcaagtcttgctgccacatttggctctaaattgggccaacagataaatgtcagcaagaaaggaaccatttatataggaaagagaagaggtcgcaaaccaaaaactgtcttaaatggtattctttctggtagtcctactagccttgctgttcttgagcaaacagctcaacaggcagctgggtcagcattaggacagattcttcccccattactgccttcatctgctagtagttctgagattcttccatcacctatttgctctcagtcttctgggactagtggaggtcagagccctgtaagtagtgatgcaggttttgttgaacccagttcagtgccatatttgcatttacactccagacagggcagtatgattcagactcttgcaatgaagaaggcctcaaaggggaggaggcggttatctcctcctactttgttgccaaattctccttcgcacttgagtgaactcacatctctaaaagaagctactccttccccaatcagtgagtctcatagtgatgagaccattcccagtgatagtggaattggaacagataataacagcacatcagacagggcagagaaattttgtgggcaaaaaaagaggaggcattcttttgagcatgtttctctgattccccctgaaacctctacagtgctaagcagtcttaaagaaaaacataaacacaaatgtaagcgcaggaatcatgattacctcagctatgacaagatgaaaaggcagaaacgaaaacggaaaaagaaatatccccagcttcgaaatagacaggatccagactttattgcagagctggaggaactaataagtcgcctaagtgaaattcggatcactcatcgaagtcatcattttatcccccgagatcttctgccaactatctttcgaatcaactttaatagtttctatacacatccttctttccccttagaccctttgcactacattcgaaaacctgacttaaaaaagaaaagagggagaccccctaagatgagggaggcaatggctgaaatgccttttatgcacagccttagttttcctctttctagtactggattctatccatcttatggtatgccttactctccttcaccccttacagctgctcccataggattaggttactatggaaggtatcctcccactctttatccacctcctccatctccttctttcaccacgccacttccacctccttcctatatgcatgctggtcatttacttctcaatcctgccaaataccataagaaaaagcataagctacttcgacaggaggcctttcttacaaccagcaggactcccctcctttccatgagtacctaccccagtgttcctcctgagatggcctatggttggatggttgagcacaaacacaggcaccgtcacaaacacagagaacaccgttcttctgaacaaccccaggtttctatggacactggctcttcccgatctgtcctggaatctttgaagcgctatagatttggaaaggatgctgttggagagcgatataagcataaggaaaagcaccgttgtcacatgtcctgccctcatctctctccttcaaaaagcttaataaacagagaggaacagtgggtccaccgagagccttcagaatctagtccattggccttgggattgcagacacctttacagattgactgttcagaaagttctccaagcttatcccttggaggattcactcccaactctgagccagccagcagtgatgaacatacaaaccttttcacaagtgcaataggcagctgcagagtttcaaaccctaactccagtggccggaagaaattaactgacagccctggactcttttctgcacaggacacttcactaaatcggcttcacagaaaggagtcactgccttctaacgaaagggcagtacagactttggcaggctcccagccaacctctgataaaccctcccagcggccatcagagagcacaaattgtagccctacccggaaaaggtcttcatctgagagtacttcttcaacagtaaacggagttccctctcgaagtccaagattagttgcttctggggatgactctgtggatagtctgctgcagcggatggtacaaaatgaggaccaagagcccatggagaaaagtattgatgctgtgattgcaactgcctctgcaccaccttcttccagtccaggccgtagccacagcaaggaccgaaccctgggaaaaccagacagccttttagtgcctgcagtcacaagtgactcttgcaataatagcatctcactcctatctgaaaagttgacaagcagctgttccccccatcatatcaagagaagtgtagtggaagctatgcaacgccaagctcggaaaatgtgcaattacgacaaaatcttggccacaaagaaaaacctagaccatgtcaataaaatcttaaaagccaaaaaacttcaaaggcaggccaggacagggaataactttgtgaaacgtaggccaggtcgacctcggaaatgtccccttcaggctgtcgtatcaatgcaagcattccaggctgctcagtttgtcaacccagaattgaacagagacgaggaaggagcagcactgcacctcagtcctgacacagttacagatgtaattgaggctgttgttcagagtgtaaatctgaacccagaacataaaaaggggttgaagagaaaaggttggctattggaagaacagaccagaaaaaagcagaagccattaccagaggaagaagagcaagagaataataaaagctttaatgaagcaccagttgagattcccagtccttctgaaaccccagctaaaccttctgaacctgaaagtaccttgcagcctgtgctttctctcatcccaagggaaaagaagcccccacgtcccccaaagaagaagtatcagaaagcagggctgtattctgacgtttacaaaactacagacccaaagagtcgattgatccaattaaagaaagagaagctggagtatactccaggagagcatgaatatggattatttccagcgcccattcatgttggaaagtatctaagacaaaagagaattgacttccagcttccttatgatatcctttggcagtggaaacacaatcagctatacaaaaagccagatgtcccactatataagaaaattcgttcaaatgtctacgttgatgtcaaacccctttctggttacgaagctaccacctgtaactgtaagaagccagatgatgacaccaggaagggctgtgttgatgactgcctcaatagaatgatctttgctgagtgttcccccaacacttgcccatgtggcgagcaatgctgtaaccagaggatacagaggcatgaatgggtgcaatgtctagaacgatttcgagctgaggaaaaaggttggggaatcagaaccaaagagcccctaaaagctgggcagttcatcattgaatacctaggggaggtcgtcagtgaacaggagttcaggaacaggatgattgagcagtatcataatcacagtgaccactactgcctgaacctggatagtgggatggtgattgacagttaccgcatgggaaatgaggcccgattcatcaaccatagctgtgacccaaattgtgaaatgcagaaatggtctgttaatggagtataccggattggactctatgctcttaaagacatgccagctgggactgaactcacttatgattataactttcattccttcaatgtggaaaaacagcaactttgtaagtgtggctttgagaaatgtcgaggaatcatcggaggcaagagtcagcgtgtgaatggactcaccagcagcaaaaacagccagcccatggccacacacaaaaaatctggacggtcaaaagagaagagaaagtctaagcacaagctgaagaaaaggagaggccatctctctgaggaacccagtgaaaatatcaacaccccaactagattgaccccccaattacagatgaagccaatgtccaatcgtgaaaggaactttgtgttaaagcatcatgtattcttggtccgaaactgggagaagattcgtcaaaaacaggaggaagtaaagcacaccagtgataatattcactcagcatcattatatacccgttggaatgggatctgccgagatgatgggaatatcaagtctgatgtcttcatgacccagttctctgccctgcagacagctcgatctgttcgaacaagacggttggcagctgcagaggaaaatattgaagtggctcgggcagcccgcctagcccagatcttcaaagaaatttgtgatggtatcatctcttataaagattcttcccggcaagcactggcagctccacttttgaaccttcccccaaagaaaaagaatgctgattattatgagaagatctctgatcccctagatcttatcaccatagagaagcagatcctcactggttactataagacagtggaagcttttgatgctgacatgctcaaagtctttcggaatgctgagaagtactatgggcgtaaatccccagttgggagagatgtttgtcgtctacgaaaggcctattacaatgcccggcatgaggcatcagcccagattgatgagattgtgggagagacagcaagtgaggcagacagcagtgagacctcagtctctgaaaaggagaatgggcatgagaaggacgacgatgttattcgctgtatctgtggcctctacaaggatgaaggtctcatgatccagtgtgacaagtgcatggtatggcagcactgtgattgtatgggagtgaactcagatgtggagcactacctttgtgagcagtgtgacccaaggcctgtggacagggaggttcccatgatccctcggccccactatgcccaacctggctgtgtctacttcatctgtttgctccgagatgacttgctgcttcgtcagggtgactgtgtgtatctgatgagggatagtcggcgcacccctgatggccacccggtccgtcagtcctatcgactgttatctcacattaaccgagataaacttgacatctttcgcattgagaagctttggaagaatgaaaaagaggaacggtttgcctttggtcaccattatttccgtccccacgaaacacaccactctccatcccgtcggttctatcataatgaactatttcgggtgccactctatgagatcattcccttggaggctgtagtggggacctgctgtgtgttggacctttatacgtattgtaaagggagacccaaaggagtaaaggagcaagatgtgtacatctgtgattatcggcttgacaagtcagcacacctgttttacaagatccaccggaaccgctatcctgtctgcaccaaaccctatgcttttgatcacttccccaagaagctcactcccaaaaaagatttctcgcctcattacgtcccagacaactacaagaggaatggaggacgatcatcctggaagtctgagcgctcaaagccacccctaaaagacttgggccaggaggatgatgctctacccttgattgaagaggttctagccagtcaagagcaagcagccaatgagatacccagcctggaggagccagaacgggaaggggccactgctaacgtcagtgagggtgaaaaaaaaacagaggaaagtagtcaagaaccccagtcaacctgtacccctgaggaacgacggcataaccaacgggaacgactcaaccagatcttgctcaatctccttgaaaaaatccctggaaaaaatgccattgatgtgacctacttgctggaggaaggatcaggcaggaaactgcgaaggcgtactttgtttatcccagaaaacagctttcgaaagtgaccctcaaagaatgagaacctcaagcatctgggatccagtggagctaatcagtcctgcctcctgctctctgggtatagacaggggtgggaagggtccatctgggcaaggggaatggggccatgttgttgacattaggtacttaataagccttggagctagtggagagggagaggaaagggttctgtccaagacagttcaggttaattaattttcttctccattgcttcaccttaagggttaataatgtagagaggagggaggaccacattgatgaccagaacctactggtactttatagcatttgccccaccccacagcttaggtttttctgtcatcctcagatcccacaggcattgcgaagaagctgcttcctatacccaggtataactcaaaatccaaagggatagggccaggatccctattcctaccccatctattctctgttggctccaagagctaccccagagaccttaaacagaaacagtagctgaggcttcttcctagatacctgactagggaagtttgtctctcctttcttgcccaaccaggtcaaagtaaaatgtgagttgacagctcaaagcacttgtaactgctgccccctccctacctctactccccaaaatggaatcatgggatagggaaggcccccatggggtcagaagggcacggtagttcttgcaattatttttgttttacccttcataacctgtcaaacatatttttttctaatgagaaagccaggcccccgccagcacacatgctgtttttaatgcgctgtagttcttgtgtgtctgctgtgctgtgcaaatggagattcagttcaaaataaaatcatttaaaaacctacataaaaagaactctaaacccacccctgcaacaaaagtcactacataaactgttcagcagtattcacctatcagagtatttgttgtgagtatagattatcaattgaaaacactactcttgttttcttaattgtacagttttcaatgtccctttcttaaagagacagtatatttctcttcacccctagcccatcttccctcaccctcctgaatgacatcaggaggtatatccagggtgtctccttccttcctactctcttgaccagaagttaacagactatactgtctctttaaaaataaaatttaaaaagctttgttgtcttttcagacatacatatgcatatatgttttagatgttcttataagagaaaagatggtttttaaatgtgccaagttgtgtgtgtgtgtgtatatatatgtgtgtatgtgtgtgtatatatatatgtgtgtgtgtatatatatacacacacacacacacacctgctgtgtgattggtaagcaatacaatagtaaacatgtccccattacttttttctaatattggaccaatgctgtcctaattgtacatttccccttatggtgacgatgctctgactcgtttaggtagacacattgaccaccttccattccattaaatattttttcctttttcccctttctgtgtcattcttgaggaaaaaacaaaagagagaggggatgccaatgatccccttgagcagagaaaaagcaaaataaatattttattaaagaaaaaagagaattaagaaaatagtttggagtattttcttactgtagagaagcactgtacattactaagagacctgggtataagatactcacatgtggagctggaaaaatcgcatgtccaagcccgtttgagtggtttcttttgtttttcattgcagggagtgggtgggagggaggtgggactaggggcactttgggggtctccttttagtcaaaagcgagaaaatgacaagaaagagattaaaattcaatgtttcctttatagtgttaaacactaaaattttaaaaaagatgaaaaagaaaaaaaaactttgtaaaatgcgagaacagaagcaaaagacactacgctctgtcattttatctttcttttgttgaaagactaaaaaaaaactgaaatgttttttagacaatcaaatgttaggtaagtgcaaaaacttgttttttcttactggtgtagaaattaatgcctttttttatttttcagttattttataataacgaaataaaaagaaccccccagctgccaggcgggttttggtgtttgaaatgcggggcaaagcactacatcactgcaaatagatacagagttagtctgcatgtctgtaggctgtgtgattgcggaaaatataaatgctgctaatatatttcctttttacaaaagcatatctaaatagatgattgttttgatgttaatctttgtaaattatgtattaccaattttaacattggatgtaattgcatacaaagcttgcatctcaatccttgaaagtctagtattaaatggaaaaaacttttcctaactgtggaaaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("ASH1L");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 155348071,
				PositionType.ZERO_BASED), "C", "G");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(9, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("6332G>C", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Arg2111Thr)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.MISSENSE_VARIANT, VariantEffect.SPLICE_REGION_VARIANT),
				annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc001fpu_3() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001fpu.3	chr1	+	156698262	156706752	156698896	156706545	8	156698262,156701782,156702072,156702776,156703168,156703800,156705516,156706423,	156699007,156701907,156702265,156702839,156703312,156704285,156705701,156706752,	Q96FB5	uc001fpu.3");
		this.builderForward
				.setSequence("tgcgcgccgacgaagcccgggaaggcaggcgcgcgggttagaacgcgccagaggtcggcgcgcgcacacccgcaccgccccgaccccaggtagtgaggccagtgattccgagtgtgtgaggagcggcagtggcggcggaggagggggcggcgtgggtgggggcgggggcgggatgcgctccccggcccctctagccccgtggtggtacaacgcgaaggtgtgggaaggccgcgataaaccggaactgcagcccgccggacacctccggcttcacttccgtaagaggagaggagtgtacggcaaggggcgggaactggaacttggcccgcctcgttgtgagctgagctcagcggcacgcttttgtggcgtcactgcactgttaccccgccctacgtgtctctgacgctgacaccttctcactgtgaaacgtcgcgacctgtgacgtctggggggcgcctcaaatcttccactccagcatcggatcccggaaaggcagcgtcggagactggacccaaaactcttcctgttctgcctgcagagttgagccccgtccgggtcctggacccgcgtagtactgaccctggatccctgttcactgcgttctcgctccccgcgctccctgctggaccccgggatgccgggcatctccgcccgaggcctctctcatgaggggaggaagcagctagctgttaacctcacccgtgtcctggcactctaccgttccatcttggatgcctacatcatcgaatttttcacagacaacctatgggacacactcccttgctcatggcaggaagcattggatggactgaaaccaccacagctggccacaatgctgctggggatgcctggggaaggggaggtcgtcaggtacaggtcagtgtggccactcaccctgctggccctgaagtccacggcgtgtgccctggcctttacccggatgcctggctttcagaccccctcagaattcctggagaaccccagccagagctcccgactaacagctccattccggaaacatgtcaggcccaagaagcagcatgagatccggaggctgggagagttggtgaagaagctgagtgatttcacaggctgcacccaggttgtagacgtgggctcaggccagggccatctctcccgcttcatggctcttggcctggggttgatggtgaagagcatcgaaggggatcagagactggtggagagagcccagcgcctggaccaggagcttctgcaggctctggagaaagaggagaagaggaacccgcaggtggtccaaaccagccctcgtcactccccacaccacgtggttaggtgggtagaccccacagccctgtgtgaggagcttctgcttccactggagaacccgtgtcagggcagggcccgcttgctgctcacaggcctccacgcctgtggggatctgagtgttgccttgctgagacacttctcctgctgtcctgaggtggtggccctggcctcagtgggctgctgctacatgaagctgagtgaccctggcggctacccactgagtcagtgggtggctgggctgcctggctatgaactgccctaccggcttcgggagggggcctgccatgccctggaggaatatgctgagcggctacagaaagctggccctggccttcgaactcactgctaccgtgcagcactggagacagtcatccgacgggcccggcccgagctccgtcggccaggcgtgcagggtatccccagggtccacgagctcaagattgaagaatatgtgcagcgggggctacagcgagtggggctagatccccagctgccactgaatctggctgcccttcaggcccacgtggcccaggagaaccgtgtggtggccttcttcagcctggctctactgcttgccccactggtggagacgcttattctactggaccggctgctgtaccttcaggaacagggtttccatgctgagctcctgcccatcttcagtcctgaactctctcccagaaacctggttctggtggccaccaagatgcccctgggtcaggctctttctgttctggagactgaagacagctgatgcagcctgaggaaacatctcagaccccatcatctgaaagtgcccagagagcacagtggcagagtacatctcatccagagaaacagcatcctgcatcctccagagtcctggttccttcagtttcatcccctttctctccttccatggattatgtaatacattgtaaagttttaattaattaaaaattggatatctgtttccttcccaaaaaaaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("RRNAD1");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 156704286,
				PositionType.ZERO_BASED), "T", "C");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(5, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("1121+2T>C", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("?", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.CODING_TRANSCRIPT_INTRON_VARIANT,
				VariantEffect.SPLICE_DONOR_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc001fro_4() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001fro.4	chr1	+	158056385	158065844	158056481	158064910	11	158056385,158057544,158057795,158058116,158059243,158059507,158061147,158063128,158063412,158064102,158064433,	158056536,158057650,158057944,158058244,158059418,158059608,158061346,158063236,158063552,158064180,158065844,	Q5W0G0	uc001fro.4");
		this.builderForward
				.setSequence("gaattgctgaaggatgggaagagggagaccaccgtgagccaactgcttattaaccccacggacctggacatagggcgtgtcttcacttgccgaagcatgaacgaagccatccctagtggcaaggagacttccatcgagctggatgtgcaccaccctcctacagtgaccctgtccattgagccacagacggtgcaggagggtgagcgtgttgtctttacctgccaggccacagccaaccccgagatcttgggctacaggtgggccaaagggggtttcttgattgaagacgcccacgagagtcgctatgagacaaatgtggattattcctttttcacggagcctgtgtcttgtgaggttcacaacaaagtgggaagcaccaatgtcagcactttagtaaatgtccactttgctccccggattgtagttgaccccaaacccacaaccacagacattggctctgatgtgacccttacctgtgtctgggttgggaatccccccctcactctcacctggaccaaaaaggactcaaatatggggcccaggcctcctggctccccacccgaggctgctctctctgcccaggtcctgagtaacagcaaccagctgctgctgaagtcggtgactcaggcagacgctggcacctacacctgccgggccatcgtgcctcgaatcggagtggctgagcgggaggtgccgctctatgtgaacgggccccccatcatctccagtgaggcagtgcagtatgctgtgaggggtgacggtggcaaggtggagtgtttcattgggagcacaccacccccagaccgcatagcatgggcctggaaggagaacttcttggaggtggggaccctggaacgctatacagtggagaggaccaactcaggcagtggggtgctatccacgctcaccatcaacaatgtcatggaggccgactttcagactcactacaactgcaccgcctggaacagcttcgggccaggcacagccatcatccagctggaagagcgagaggtgttacctgtgggcatcatagctggggccaccatcggcgcgagcatcctgctcatcttcttcttcatcgccttggtattcttcctctaccggcgccgcaaaggcagtcgcaaagacgtgaccctgaggaagctggatatcaaggtggagacagtgaaccgagagccacttacgatgcattctgaccgggaggatgacaccgccagcgtctccacagcaacccgggtcatgaaggccatctactcgtcgtttaaggatgatgtggatctgaagcaggacctgcgctgcgacaccatcgacacccgggaggagtatgagatgaaggaccccaccaatggctactacaacgtgcgtgcccatgaagaccgcccgtcttccagggcagtgctctatgctgactaccgtgcccctggccctgcccgcttcgacggccgcccctcatcccgtctctcccactccagcggctatgcccagctcaacacctatagccggggccctgcctctgactatggccctgagcccacaccccctggccctgctgccccagctggcactgacacaaccagccagctgtcctacgagaactatgagaagttcaactcccatcccttccctggggcagctgggtaccccacctaccgactgggctacccccaggccccaccctctggcctggagcggaccccatatgaggcgtatgaccccattggcaagtacgccacagccactcgattctcctacacctcccagcactcggactacggccagcgattccagcagcgcatgcagactcacgtgtaggggccagagcctggctggggcatctctgcggggcagaggagaaggctttcacagctgttccctgatattcaggggcattgctcattgctcccttctcggaccagccttcttcctcccaccatggcaggtggggagcaggtctcccagaaacaccccgtcccgaggatggtgctctgtgcatgccccagcctcctgggcctgcccttccctcttcttcgggaggatgtgtctcttctgacctgcactcttgcctgaccctagaatggggacagggaaagtgaaggttagggaaagcagaggggggcactttttagcattccctttctatcccacccctctgatctcccataagtggaaatgggggtacccagggatgggcaggctttggcctagggacatgaagtatgggagtgggtggctgtggcacagacaggtggaaaacgggatagcctggccagtccctctgttgtctgcattcgtgccctgggtgcctctctccttcctcagggtactgcagaagggagcgaacagggtactgttcgctcttgtctacagaacagccctggcactgcattcaaatccagtcttcattcagctgggatcaaaatgccagtcaccttggctacccactgtggacagctgtctgtcagcatgcagagggatccaggaatccccccggcagcacggcccgctttccttctcctccatgctgggccagccagataagtcagggtcctggtggagaaagaaaggctaggaccatgtcctcattgacccagatactgctgtgtgctgcacagcagtgaaccaacactagagggagccacacaagcctcctctccccagtctgccccacttcctggctttaactcttgagctggtttggggagtggtgaggtaggggtgggggtgctgtaggctctttttcaaagaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("KIRREL");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 158064181,
				PositionType.ZERO_BASED), "T", "C");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(9, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("1239+2T>C", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("?", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.CODING_TRANSCRIPT_INTRON_VARIANT,
				VariantEffect.SPLICE_DONOR_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc001hjn_3() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001hjn.3	chr1	-	212899494	212965139	212911749	212965105	6	212899494,212912875,212955713,212957704,212960900,212964871,	212912028,212912943,212955768,212957835,212960979,212965139,	Q96IY1	uc001hjn.3");
		this.builderForward
				.setSequence("tcccagagtgccccgcccacagttccgacgaaaaatggcggggtctcctgagttggtggtccttgaccctccatgggacaaggagctcgcggctggcacagagagccaggccttggtctccgccactccccgagaagactttcgggtgcgctgcacctcgaagcgggctgtgaccgaaatgctacaactgtgcggccgcttcgtgcaaaagctcggggacgctctgccggaggagattcgggagcccgctctgcgagatgcgcagtggacttttgaatcagctgtgcaagagaatatcagcattaatgggcaagcatggcaggaagcttcagataattgttttatggattctgacatcaaagtacttgaagatcagtttgatgaaatcatagtagatatagccacaaaacgtaagcagtatcccagaaagatcctggaatgtgtcatcaaaaccataaaagcaaaacaagaaattctgaagcagtaccaccctgttgtacatccactggacctaaaatatgaccctgatccagcccctcatatggaaaatttgaaatgcagaggggaaacagtagcaaaggagatcagtgaagccatgaagtccttgcctgcattaattgaacaaggagagggattttcccaagttctcaggatgcagcctgttatccacctccagaggattcaccaagaagtcttttccagttgtcataggaaaccagatgctaaacctgagaactttataacacagatagaaaccacaccaacagagactgcttccaggaaaacctctgacatggtactgaaaagaaagcaaactaaagactgcccccagagaaaatggtatccattgcggccaaagaaaattaatcttgatacatgagctctttctgtttattttgggagttgaaaataggcaccatcaacatttagattacagcctaattaatacctagataagacttcatttgaaataagaaataactcttttactagtgattcatttatacagatatagtatctctgtgcggggatatgatataatattgtatttccttactgttttatctattgtaaataaaaagcattttaaaaagtattgacacaaagcccatcagtgggcattaaaaatattaaaagtgcagacttttactgtccttaagtgccatcaactctcagctcccttgtagcttttgtgggatttaacaagtaacaaattctgttgtgtttccctggtatacatctttctaggaaaaaaaaaaaaaagagagagagctgtataatgatttttcgtttacatgctgaaaagtaattatcagttctgcacagcagcagatgcagggtttttttttaaagatgtagtttgatttatcaaattaatgtgctgatgataatactggctttgactttgttactccatgttcagctaatttaggtttgtgagattaactttaggattttttgttgtgtaagacaatgataactattatttgtgcaacattactctttgaaataaaaattggcatgtagccaatgtttcctgcccacactcacttttttctatagaccattaacataatttgacttggaactaatggtttctttttagggtttcttatttatttctttacaaatcattccagttcaaaatatatatcagattaatacactggcaaagtgtcattgtgttattgaactatttcacatttgaagatgtttatatatcaggtatataatcactgggagaagtctgagaaaacgatattggctttacttctaacatttaactatagtttcttatagctcttattgtgtgtcttacattatacagtgtaggcagggccatcaaacttaccaactgtttccccttttgatgactctgataccttcattcatttttcaaaggaatttactgatcatgaggttggaaaatctgtatttctcttgcttattatgtattaataatcataaatgtctagattcaccagaagtcaccagaaggtctgtctcagtgaagaaaacttataaagccactttgttgcattttgtgtttcagtgttacagtttgagatctgtatatttgtacacagctatgtgtttttcattgaaataatgtacaaagactgatcttgatgctgtgtatttttatgagttgtcttaggcattcctgagctcagcttcagttggatggtgggtcagcaccctgcgtttctgaacatactagacttcagtttaaaactgttttgaggctgtataatatgttgctgtttttacagtgctaatgtttttatagtattaaaattctgtatgatttgtttcacttcagaggtccccaaacaaaagagttattgttaattctattagattatataagctacttgcattttgattatataagaacatggtccctcccttttaagagaggtatattgtgctacatagagtttttaaatgacatgatagatttgctctgaagtattcagcagagaaacaaggaatcgattaaggaaatgtgacaaaaatcctgttaatgttgaatctgtgtaatgactatattctactttgtgttggaaatttttataagtttttaaaaaatggttatattttaaagtccccaggcagacagctgagatttgaaatcctcttgagagcccaaagttttactccatagttatagcagagtttaaaatagaaataccactgggaaagacagtacgtattagcattattaactaagaaaagcatgaccaatagcagtaataatattgaataagtatactgtgtaagaggagaagttgcatttctgcataggccaagaactagtaagctaaggattaaaagcaagaaagtctgtagttttaaggaattggaaaggttgaaggaaaaaattgaaaatcaaatcttgctctatgataaaaagagtctggattgaatagggtcttaaaattatctttcagaaaagaaatagccaggcgcggtgggctcatgcctgtaatcccagcactttgggaggctgaagtggacagattacttgagcccagcctgggcaacatagtgaaactccatttctacaaaaaatacaaaaattagcagggtgtgatggcacacacctgtagtcccagccacccaggaggctgaaatgagaggatcacttgagccctggaggcagaagttgcagtgagccaggatggcaccactgtactccagcctgggtgacagagtgagaccctgtttcaaaataataataagaagaacacaagaaggttaagtctagtcccaaagaagagagaacactgtcaaagctggtttctgaaaactggtaccaaaatctacatttgtttatttaggctactataacagaataccacaggctgggtggcttaaacaacagatacttacttctcacagttctggaggctagaagtccatgagaaggtgctggcaaatgcagtttctggtgagggctgtcttcccgacttgtagatgaccacctccttactatgtcctcacatggcctttcctcagtgcatgcatgtggagagagggacagagtgcaagctctctggtgtctcttcttataaggacaccaattgtatcagatcagagccccacccttatggcctcatttaaccttaattatttccttagaggccccatctccaaatacagctataggcacatgggagttaaggcttcatcatacagattttagggggacacaaacattgagtccataacacaagcccacagaaaatcttatcctagacctctttccctggccctttatggcttgggctccgtagaggacaaagtgaataaaaccaaattttttattgcttcaaacagtatgatttcatcttgtaagatgtactttttcttttctaaggctgaatttgtttgatttctggtgaacacctatcttctctatccaccttcattcatttattcatctgttcaacacatacagttggacacctactcggatcaaaagtcagagatcttaagagcacagcctctaagccagatcatctgagttcagatttcagttctaccattattagttcaatgatattggacaagttacttaacctgtctatgatcaaaagtcagagatcttaaagagcatagtctctgagccagatcatctgggttcagattgcagttctaccattattagttcagtgacattgggcaagttacataacctgtctatgctgcagttttctcatgtgcaaaatggggataataattatgcctacctcatagcgttattctgagaagtaaatgagttcataatgtgaaggccttagactaataactggtgcatatgagtactctatgatcactgttattacttcttttgataaaggaaatgaaaacactcaggacaagtgttgccttcaggaaggtcatattccagtgaagaggatgagtgtgcaaacaatgataatgccatgcagcaaatggtatgatagatgtatgtgtggaacttaggggagtagtcatggaagtctttgtagaagaggtggcatttgagcagtcttgaatgttttaggcaaataatcagggaaggaatacaggaataaagaacaaattactgttataacaatgtggatcaatatcaaagatactatattgaataaaagaagcaaagccaggcacaagagaatacatcctgtgtatggaatcacagaatgggcaaaactaagccatggtggttccctctggggagaggggagatattaactgggaatggcacgggaaacatttttgggtgagcaaaatgttctgtatcttgatctgagtggtgctcacatggctgtatatgtatgtaaaattttgttgaactatacacttaaaatttctgcactttatttaagttttatattttaaaaatgcacaaaactgattcaccaaatatttgaggagcgtctccagcatcaaagacagacccaaaaactgtaagtcccttaatggcaggaatctttgttccatccacctggaataattgctggcacatgatgggccctcgatgcatagtggttaactgagtgaattaaaacaaaaaccaaagagaataaaggacctcaaagaaaacagatacatacaaagcaaaatcccagaatgatgactgtactcccagaaagaagagaaaatcatacattttttggaaaatcggaatgctattaaataatgttcagtaaataagaaaaagtttttgaaaattaaaaatatgaaagttgaaataaaaattcagtgggagggctgggagataaaatgaaattttcccaaaaataaaacagaaagattaagaaatggcaaattagaaaagtaagaataaagagaataagttcataaagttcaaagtctaatcgatacccattccagagatagcatgcagaaaaagcagcagagtcaaaattatcaaagagataatgcagttaccatatggcccagcaattttactccacgttctaaacctaagagaattgaaaacatgctcacacaataacttacacacaaatgtttgtagcagcaggttgaactttcatgaactgatgaatggataaataaaatgtggactatccactcaatagaatattatttggcaataagaagaaatgaggtactggtaaatgccacagtgtagatgaaccttaaaacatgccaagtgaaagaagcctctcacaaaagactgctgaattttgtagactccatgtaaatgaactgtccagaacaggcaaatctacagaaacaaaaaatagatctgtcactgcctaaggctggaaagatggagagggtggaaggaataggaagtgactgctaatgggtatgggatttttttttttttttttttgtgaaacaaggtcttgctgtgttgcccaagctggagtacagtggtgctatcttggcttactgcaacctctgcccccgcaactcaagccatcctcccaccttagcctcccaagtaggtgggactataggcatgcaccatcatgcccagctaatttttgtattttttgtagagacggagtttcgctatgtggctggtctcaaactcgtgagctcaagcgatccgcccacctctgcctcccaaagtgctggattacaggcgtgagccactgcgcctggcctgggatgtcttttgagggtgatgaaaaccctctaaatttgattatggtgattcacgactgcttgttgaatatactgaaaaccattgaacaatacagtttaaaagggtgagtgagatggtaaagtatatctcaatttaaaaagagaataaagaaatagaaataacacaagaaacattcccagacctgaggaataccaagaggaattggaatggcatcagattactaaacaacaatcctaaaatctagaagcatctggagaaatattctcagcataaatgatatacaacctaggcttttatacacagtcaaactaataatcaagtgcatgattagctaaaagatatctcagatatgcaaagcgtcaaaaaaaattacctccaaggcatcccgtttgggggttctaccaggggctgtgctctattccaaagggtaaaccaaaaaagcagcagccatgggatctgggaaactaaggctcccaaaacacagcacactcgagtctacaggctactaactaatcagcagatttggagactaggcagaccaggttggaataagactatgtaggccgggtgcggtggctcacacctgtaatcccagcaccttgggaggccgagacaggcagatcacctgaggctgggaattggagaccagcctgaccaacaactaaaaatacaaaattagctgggcgtggtggcgcatgcctgtaatcccagctactcaggaggctgaggctggagaatcgcttgaacccaggaggcagaggttgcagtgagccaagatcacgccatcgcactccagcctgggcaacaagaacgaaacttcatctcaaaaaaaaaaaaaggactatgtaggactccaaaagagatattttcagaaaaaaataaaagtgacaacacgagtgataatgttaactatgcagaaaattgtataaaggagatgttttgtaatatgattaagatgttcaaagaaacaaaaaataaggcagttattcactacagtagaggcagaaagctgtacaaaaaaagaaatgtaatcatggttacaaattagttctccagtgaacaatatttatatagccataataataaaaaccctgaatctgaattcagtaaaaaaaaaaaaaaaaaattgggacagttacactgggaggctaaaggatgggaattagcaattagtacagcacaggcactgaacagttagatggacattggttaaagcagtaaggtggggtgtggaggcccttatagctgctgggtcctggggcagggtagggtgtcccaggacatggagctggagcagccagagtggcagggatagtgaggaggcttctgtgccaaccagtacaatccatatgtttgtaacagtgtgactgtctgaacaccagctttgacatggcagtggggagtgaaatcttcacccaccacaactggaagtcagtggatgtctaaaaagctgattcaagatcatacaatatggatataatttagtgcagcagaataaagagctgacgaagtgggaagtattaactctgtggatgaacttaggtgggggaactgggccagggactgataatttttgatagaagcctcatagatttgatttttaaaattaaatgcatatagtaattaagggaaaataaatttggtttaagagacaaatcatcccctagaacctttctcggggtttcagtttttcagggtttcagaagctttctcagggtttcaaaacaggcgtgtgccaccatgcccggctagtttatttgttttagagatggggtctcgccatattgccccggctggtcttgaattcctgggctcaagccatcctcctgcctcagcttcccaaagtgctgggattacaggaatgagccactgcacctggcctatcacagtatatttcagataagacttactattctctgtttttggctgataattctagttttggtttcttcaggtttttttttttttctcccctctgaccatttgttttgcaaatagattatattatgaaagtctctgaagaaaaaaatatctctctagagaaccactgaagggggttcttggggattttatatgaaaggatttgagaaaattaatgttatattcatttgaagcttcaggggtaaagaatcccttgtaattcgttgctgtgtctctacattgctcatactttgcccatgggctgtaaactaatattcgttgaatgaatgagttcatattgcaaatttctaacttgtcggtttccctggatcacaatctcacacattaatggcttgtggcatttatattcattattaattgaacttagaaggtgagtgtggggaggggagaggtaaaaaactgaggacagatcactgggaaactgacctgaattaggactccagagtctcctgtctactagaactacgtctactagaactaagtctaattcccatatccctgcaggacataagagcagaggatggcattgacatccctcagtgacatcactgtgctagcaggtatagttgccttctgatcaccttttcctttggtcacatcccctgacttctgggtggcctgggatagctagagaagaaaattcaacttctccctgcaggtcttctgctccagctgactcctgtgcctgtgtaaaagattgggcatttctatcatcaccttgactagccttactcagagaatggctccagtgcctttgggctcatacctagttgcagattttaagacctcccttggtgcccattcatgtagctctgtggcccacatcttctacaatgatgtcattggagttcttttgtccatttctgcccctgatgccaccttgatgggaatgccaccctccacacaactcttcaccagtccataaatacctgagacttggcgcatttcttttttttttttttttttttttttttttgagactgtcacccaggctggagtgtagtggagcgatctcaactcactgcaacctccacctcccaggttcaagaggttctcatgccttagcctcccgagtagctgggattacaggcgcgtgctaccatgctgggctaatatttgcatttttatagagatgaggttttgccatgttggtcaggctggtctcgaactcctgacctcaggtgatccactcgccctggcctcccaaagtgctgtgattacaggagtgagccgccacgcccggcccatttcattcttaaaaaaaaaaatccttccggaggtttagttcctccacaaggcagcttctggttacctgccccctcagggtccttgctgcctgggtcaccccagcaaccccttcgtccacacatcactgccctttgcagcaccagctgattacattgctttttccatgttctagctgttcttttgagccgttttatagccgagcatctccaccagaagagcggataggaaaaaggaaaagagaggaggatttgaccttggtatcttgcttcatttccttctctgacaaaaatttggtgaaaaggttaaaataagtgttcgaattctctctgacttgtcatattctgtctatgccctgtgtgcctgcctctgatcctgaggaagtgtagaaatgaggggatgacactggcagaataataaaaatgggcaaagttgtagggcacccctgctccaggtcccatacaccccagctccagagcctcaggatctttcccagccatgtgccagacaatattgtaaaatgatttttcgtagccacaccttttattacacacatactcgatgaatctaaatactgtgctaagagctgtgtggatgtacacctgtaaaccaaggcacagaccctgcccacaagcaatttgccgtcagttaggggagccatcagtagcatctagggatccattagagggaaaagagggagctgagggagaggctaggagcccaagcaaacctgcacttctgctcctagagggaacctgagagtgaaactcatgggtttggaaacttctgtggaattacctgcttgcaagaaatgattttgtctggttttagaaactacgatggaacactaatatattcctgtaattgactggcaagttttaacacttactgcaatttatttttcttgtaagcccaacccaagtattcttcctttaaaaaaaatttcagattgacaaatgactgcttctttagaaatcctccttactaaaattactcctcatttctttttctttcatattcaaagggaacaaacgttcaaacacacaaataaacactgctctttgccagagtggtggactctggcctccccacccccagcactcccagtcttgccctcctgcaaacactagacttaattttctctgcgctgagggccctcctcgagtcagagtgagaggaaaaacagaagggagaagctgcgagatgtggcaacaaaggggacggtgatcaggtttatccctgatgttgtctgtgaaggtgttccattctccagagatctcaccaaatggtataagtgatgtgcagataagcctgtggttcccttcactcatcagatcagagcctcagaattctcagaaaccatttttttcctaacataaaactccttttctcatttctatccctctgtaaatctgatttgaaaaaattgtttgaacaaaccaacaatgtcagatttgttccagctatatgagctcacaattcatctactcttggtagttcaagataaaagattacccaaagaaaaggaagagattgagtaaagccacaactgtaatagtgtcccagattgggtaaaaggccattaataaatagcgtgaattgtagaactgtttaaagcagcatatttttaaaacaggcatataaagaatccactttatgcctggaattgtacaaaaatactttaaataaatgtttacatatataatgttatcaacttatttgctgaaggtaatatcaatataatgctagtagttaccatgtattgagtgcttactacatgccatgatcatgctaaaagcttcacatgcaccatctcttttaatcctcaagacaatccatgaattaagggctattaccacgcccatttcatgagaaagaggtggagatgagaagggaagtgcctggccaccttcccagcggtctatagaccatgtagagcagagcgaggccttctgaccacaaactctgcttccaccttctccccgtggttcacagcaaaggccttgtccagagtctcagacgctctcaccgctacaccacagcgacctcgttggttcttctcattttttttcaatgacacatagcaacagagagtatcaacaaacatttttaaatttcccagcagttaacgaatagtgaatattactatgtaatactagtgtctaagaaagtgctcaaagttgtttaaagcaatttattatcttaatgaggttaaatatgaacttttgctgttaatttcttgggaaatataaatcatcatagctagtcattctttcatcgtgggaagctaacagtcctgattgttcaggattataattatagtaaattctgaatttgtgacgtgtgaattttagaaggttacaaaacaacttccagacaactgagtcataaagtttttgagagaagtatgtggcacacgtctaattcattgtcctttctctccagtggtatagttatgatattaacctaaattgcaaaaggaatcgttttagttgactgctgcttgaaattgtgtcacttttccaaatttaggtaatgtctgttacaaggagtatattttggaaacattaacttgactgatacttatttccctggttaagtgaccttcctgccatgtgctgagtgccaggacttactgcccctcagtaatttggaaggggatcctgggccctgcctggaacaaagggttgtgaaacagctctgagtctcaaggcaagtcttgaaagccatatatagttttcagaaaaagagatcttctaaattccattcacccccatttaacacattaggagccaagcctggtggactagctctaggggcagtttaggagagaaggggatcggggaaccttcatcccttctgcctctggtgaactggcaagatgaagaggatgaagctgggtgacccagagcccgccctccctctctcggcagtcatctccaaggccggagtagactggactggagggcctctgagaggacacagccaggaagcacactctccctgtgctcttcacccaccatgaagagacaggacgggaaagtccgtgctgtgctggtgtccatcagagggagggacaccatccagcccctgggaccgggctcggctctgttgttgcacagaagtcatttggagatccccacgtgccacatggaggattgtgggaggaccccaccgaggtggctggcagcctctcatggacaccatggagagtgacccaatcactctgcatttgaggactgcagagtggctcagcggtctggacattgaagaggagccaacctcaggggtaacacagtgccgctgggagctgagatgagccaggtggtcggtgggggacacaagcctagaacaaattcaataaaccagccacagacgttatcatcagatgtgcccagagacaaggtggacaggggcatttgctgtggagaccagagggccagagagagtacagggacctggatagatggctccagcacctcttcttctccctacacccatatgccatcttgataaacagcagggaaatctgaaaaataccaagtcttcccatagggacagtgttacctcccaaaaactgtttgaataattggcttgaactggattttctgaactgaactaatgctattgttttgtgggtttttttcttcctactgagtagagtagttgtttatgggggacatctgatcctttcattttcttccagagagctaaccacctgttctgacaccatttattgaatagtacctcttttcccccactgatgtgagaggctgcatttttcatctaataaaatcgaatatatatttagatctaaaaaaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("NSL1");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 212964869,
				PositionType.ZERO_BASED), "A", "G");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(0, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("234+2T>C", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("?", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.CODING_TRANSCRIPT_INTRON_VARIANT,
				VariantEffect.SPLICE_DONOR_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc010pyu_2() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc010pyu.2	chr1	+	247419373	247420447	247419373	247420447	2	247419373,247419511,	247419508,247420447,	Q7Z5H4	uc010pyu.2");
		this.builderForward
				.setSequence("atgttgaaattggttattattgagaacatggcagaaattatgctattctcattagatctcttgcttttctccacagatatcctttgctttaattttccttctaagatgatcaaacttcctggttttattaccatacaaatcttcttttatccacaagccagctttggaatttcagcaaacaccatccttcttcttttccacatcttcacctttgttttcagtcacaggtctaagtccattgacatgataattagtcacctgtctctcatccacatactgctgctcttcactcaggcaatattggtgtccttagacttctttggttcacagaatactcaggatgatcttaggtataaggtcattgtctttttaaacaaggtgatgaggggcctctccatctgcaccccctgcctcctgagtgtgctccaggccatcatcagccccagcatcttctccttggcaaagctcaaacatccttctgcaagtcacatcttaggattcttccttttctcatgggtcctcaacatgttcattggtgtaatcttctgctgtacactgcggctacccccagtgaaacggggccagtcttctgtttgtcatacagcactgttcctttttgcccatgagctacacccacaggagactgtttttcacactaatgactttgagggatgtcacctttatagggttcatggtcctctcaagaggctacatggtgattattttatacagacaataagaggctatctcagtgccttcacacagccagcctgtccccgagtctcaccagtgaaaagagcctcccaggctatcttactgctggtgagttttgtcttcacatactgggtggactttacgttctcattttcaggaggtgtgacatggataaatgattctctgctagtgtggctccaggttattgtggccaatagctatgccgcaattagtcctttgatgctaatttatgctgataaccaaatattcaagactctgcaaatgttatggtttaaatatttgtctcctccaaagctcatgttgaaatttaatcgccaatgtggcagtactaagaagtga"
						.toUpperCase());
		this.builderForward.setGeneSymbol("VN1R5");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 247419508,
				PositionType.ZERO_BASED), "T", "C");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(0, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("135+1T>C", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("?", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.CODING_TRANSCRIPT_INTRON_VARIANT,
				VariantEffect.SPLICE_DONOR_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc002rso_1() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc002rso.1	chr2	+	42721708	42984086	42806318	42980539	18	42721708,42722302,42797576,42806246,42836597,42867312,42871266,42883339,42886902,42909540,42922904,42924915,42931336,42935042,42936013,42946127,42950028,42980513,	42722146,42722394,42797644,42806340,42836724,42867376,42871384,42883442,42887002,42909729,42922979,42924974,42931458,42935194,42936236,42946214,42950175,42984086,	D6W5A2	uc002rso.1");
		this.builderForward
				.setSequence("agcctcggtgccgcctgccagcccgcggagggaagcggtccccgacccggccccggcccccacgttctggggcgcgagtcctgagaaggcgcctaacgtgtagcgcgcctggaccctctcgagatgctgcccttagagtgggatgaaagtctggggaggaacgccttgtcaccggcaaaaacagcctttctcttggtcttcgtctgcatcctccttttcaaaatatcttgatttcccccgcgcacccctgacccggctgcagcaccggccctccgagcgggaagagccccgtgaaggctccgcagagcgatctaccccgcgctcttgtccgtgcccagaaagcgggttccacttggcagggattattttaaactgctaccagcaattcattcacctgacacccgacaccggcagcccagcccgccggaggcgagacaggattgtccggtgcccgctagaggtcgccaaacggagtccttttgcgctggttcttaagtggattcctagtgaaaaggcagacaactgaaaatattatgtctactttgagaattcctccagcaacccatacctaataagaaggatagaagaactcaacaagactgcaagtggcaacgtggaagcaaaagtagtatgcttttatagacgacgtgatatttccaacacacttataatgctcgcagataagcatgctaaagaaattgaggaagaatctgaaacaacagttgaggctgacttgaccgataagcagaaacatcagttgaaacatagggaactctttttgtcacgccagtatgaatctctgcccgcaacacatatcaggggaaagtgcagtgttgcccttctgaatgagacagaatcagtattgtcatatcttgataaggaggataccttcttctactcattggtctatgacccctcattgaaaacactattagctgacaaaggtgaaatcagagtgggacctagatatcaagcagacattccagaaatgctgttagaaggagaatcagatgagagggaacaatcaaaattggaagttaaagtttgggatccaaatagcccacttacggatcgacagattgaccagtttttagttgtagcacgtgctgttgggacattcgccagagccctggattgcagcagttctgtgaggcagcctagtttgcatatgagtgctgctgcagcttcccgagacatcaccttgtttcacgctatggatacattgtatagacacagctatgatttgagcagtgccattagtgtcttagtaccactcggaggacctgttttatgcagagatgaaatggaggaatggtcagcctctgaagctagcttatttgaagaggcactggaaaaatatggcaaagacttcaatgacatacggcaagattttcttccttggaaatcattgactagcatcattgaatattattacatgtggaaaactactgacagatatgtgcaacagaaacgtctaaaagcagcagaagctgagagtaaactgaaacaagtatatatcccaacctacaaaccaaatcccaaccaaatatccactagtaatgggaagcctggtgctgtgaatggagctgtggggaccacgttccagcctcagaatcctctcttagggagagcctgtgagagctgctatgctacacagtctcaccagtggtattcttggggcccacctaatatgcagtgtagattatgtgcaatttgttggctttattggaaaaaatatggaggcttgaaaatgcccacccagtcagaagaagagaagttatctcctagcccaactacagaggaccctcgtgttagaagtcacgtgtcccgccaggccatgcagggaatgccagtccgaaacactgggagtccaaagtctgcagtgaagacccgccaagctttcttccttcatactacatatttcacaaaatttgctcgtcaggtctgcaaaaataccctccggctgcggcaggcagcaagacggccgtttgttgctattaattatgctgccattagggcagaatatgcagacagacatgctgaactatctggaagtccactgaaaagcaaaagcactaggaagcctttggcatgtatcattgggtatttagagatccatcctgcaaagaaacctaatgtaattcgatctacaccaagcctgcaaaccccaactaccaagcggatgctaacaactccaaatcacacatctctgagcattctggggaaaagaaactacagtcatcacaatggtctggatgaactcacgtgctgtgtgtcagactgagctttccctgattcattctacaatccaagacttgctgcactgtcctgctgatgttcacagccgtgcctgggaagaaggcagccccactcccagtacatttcagtgggagacctctgcgtgcatccatggagacgcaatggggcggggaaggaactgtgggagtgcacgttccaaatcctgtgtctccacgtgtggatcagcagcacctcgctttcttgtcagagacctcgctgttacggagcgagacctgctgagaattgaggggctgagggaacccctccacctcctcccttctgcagcgccctgcgccccacccagcaacagcggccacttggcagtggggctgctgcaagctcagagccgctgccaccctgcatgtgtccgctcagctcggtcttatgctgtatagttactaaatatgtacaggagggccatggcatctttctgaatggatttttcttaagaaatgcgccagtgtttatgaggttcaaggtatttccctgtccttgctgttaccgtcactcagctttttctcgataggcttcatccttgtttttttgaaatgggggaatttgctgtttaccctctgcattcctatatgtgaccctccctcctactcctccaaggaacagaattaccgaggttctgacaaaagataagcctgtaaactcatcatctgtgttttgtggttggagagaaactggtgttctgcccggctctgcttggtcacagacagctccagcaagagcagttgttaaaagtgccaagcgtgtgtatcactgtgacaagccgtttgcttactgccctgttcccttgcagccaaaccagctgatgaagaactgctgccaggtgggtcctacagcaggtcacaaatgacctagtttcattttaagcagacagactctgtttggcctagaggtgtggagtgagagaactgtgtttgtgggtatgagtctgtgtggccaaccccatgacccccacccctccagcccaacatcttgtgagcacatgtgacctaggccccgggggacctgcctgctcctttggcttgggctcttcgtgtttcccacctgccctcggcacgagcccttggtggcatcacagttggccactcagctgtgctgagtagctgtgctacttgtgctggcagctgcaaggataggaatagctcagcgcccgatgagctccctgagcagatgtgaggctggcaactcccctgccctctgtttgcaggcacagggtcacagtcccaagaaagacaactggagtctgatctcccagccatctctggggttactaggaggcagctggatggcagatacgagaggcccaaatagccaagctgttgcaagacagagtggctacaattgaattgacaccctgggaagcacgaggtaacttggtaaggataatgatgctgtagatgtctgtgtcctcggaggctgagctccgcttggcagagagagcgtgctgtgtgaggtggagggcggttttgcagacatctcagcttcttttctgaggaggagttggttctcatcttaggcttctgcaagggcgagcatgggatgtctccaccaccacccactcttggagctgtgctgggtcttggcttggggcgctgagggtggggcctgtgtcagaagcatttggtgagaggggtggaggtggcaggcaggggttctcctcagggttcccactgaggggtcccttcagcaaagacctgggaggaggtgccgcatcacgtggatgtttcttccctaaagaaaaagacacaggaaagctgtctgtctgtaccctgctctggatttattgtcgtacttggacccagaaggggaaatgattccctcaccctttcactttctctctgaacccctactaagtggtgactgcagattctggaaacaattagctgcccgtgactcagctgccagcttcattttctctgccttttgggagaggccctctcacccaggcccaagagatttggagacaggagtcaggccaggtctgaagcaggagaagggaggcccctcctatctacccagttgacatttggctttgggaaaagcgcagcttgttcgagccacgtgtgccaagcaggcttttccttcctcttgtaagtaaagctcgtggttctgtagtccagtcatcctaggagggtgatgttgactgagacttcacgctctccctttgtctctggaaactgccccctcgttctgacagaatcccccaggcaatggaggaagggtgccgaggcgcctctagtctgtgcctttgccgttggaagcatttggtgctgagagggtttcccagccacccgctccctttctggggccatggtgtccctgctgtgtgtcagtggcatgtcactgtggttcagtgagcacatgggtggacgtgcagagactgtctgcgcagcccccagcagacatgcccctggggtgaggacacaggctctgcaggctatctccccctctggctcagtcatcgcctgcccacccttcacttcttaaaggtgcgcaagagaggagggccgactggagggtgtcgccggaaggtttcagcctgcccttcacaattccccttgtgcacagcccagtttccatctctcagggcccacccaggaaaatggatttcaagtgggggttttcatccagagatttgtttaacacaaaacaagaaaagctgagaggcaaaacaggggagtgaggggcaacccagaggtggggaacaacaacagcaagccgcccccatcctgagactggctgggcaccaggggaggacgcgtcaccagagcctggggccaaggccactgggggacctgccacactgtggacctgtctggtgggggctggagcctcgagaagccatgattcttgtcagaaacatttccccaggcagagagagggggccccagcctctcccctcctcttggcctccagagtcctgcaggtgcctcacagtagtgaaacccagttggaagcagctgccctgggagcctgggacaggcgacccaccgggtcagtcccctgccactcagagcagagcagggggctgagggcaagcaggtggggctgtgcgtggcctcagtgcactcggtgtcatgtctgagcctggtgtttatgccccactgctgtcctaagtccctggcgaggggaggtggaggagctgccccgtgggtgtttggagattctgttttactctgcctagagaggaaacggctttggggagggagggggaagcctttattctttactgttgtccctgttttcctttgggggaatttactcagttagcagcccctcctcaccattccccccaggaaggccatgtcccagttttctgtccacccctcctgttcctctgcactatgtctctgattttccctgccagggaagctaacccagagcacgcacctgtgctcatgagtgtttccgcaggataattcgttctgagcatgataccacagtgtggattgtctgtctgtaaggagatgccatctactaaccaatttgtattgtgtttccaataaattcctggaaatttt"
						.toUpperCase());
		this.builderForward.setGeneSymbol("MTA3");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 2, 42871264,
				PositionType.ZERO_BASED), "A", "G");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(5, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("214-2A>G", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("?", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.CODING_TRANSCRIPT_INTRON_VARIANT,
				VariantEffect.SPLICE_ACCEPTOR_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc010ysm_2() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc010ysm.2	chr2	-	85569077	85581821	85570364	85581630	10	85569077,85570761,85571121,85571361,85571716,85573097,85576506,85577162,85577902,85581458,	85570504,85570921,85571288,85571471,85571855,85573217,85576704,85577364,85578144,85581821,	G5E9N3	uc010ysm.2");
		this.builderForward
				.setSequence("aaaagaagtacggagagccgggagggtcaatcatcacctgagagttggctcttcgctggctggctggttaatcagtgctcaaagctgcctccgcctcctgactggagagcgaactagcccgggcggagccacagtcctagaggctgagcgcagtcggagctgtcccatttacccgacccgacgccggcgtgatgtggcttccgctggtgctgctcctggctgtgctgctgctggccgtcctctgcaaagtttacttgggactattctctggcagctccccgaatcctttctccgaagatgtcaaacggcccccagcgcccctggtaactgacaaggaggccaggaagaaggttctcaaacaaggaatccattacattgggcgtatggaagagggcagcattggccgttttatcttggaccagatcactgaagggcagctggactgggctcccctgtcctctccttttgacatcatggtactggaagggcccaatggccgaaaggagtaccccatgtacagtggagagaaagcctacattcagggcctcaaggagaagtttccacaggaggaagctatcattgacaagtatataaagctggttaaggtggtatccagtggagcccctcatgccatcctgttgaaattcctcccattgcccgtggttcagctcctcgacaggtgtgggctgctgactcgtttctctccattccttcaagcatccacccagagcctggctgaggtcctgcagcagctgggggcctcctctgagctccaggcagtactcagctacatcttccccacttacggtgtcacccccaaccacagtgccttttccatgcacgccctgctggtcaaccactacatgaaaggaggcttttatccccgagggggttccagtgaaattgccttccacaccatccctgtgattcagcgggctgggggcgctgtcctcacaaaggccactgtgcagagtgtgttgctggactcagctgggaaagcctgtggtgtcagtgtgaagaaggggcatgagctggtgaacatctattgccccatcgtggtctccaacgcaggactgttcaacacctatgaacacctactgccggggaacgcccgctgcctgccaggtgtgaagcagcaactggggacggtgcggcccggcttaggcatgacctctgttttcatctgcctgcgaggcaccaaggaagacctgcatctgccgtccaccaactactatgtttactatgacacggacatggaccaggcgatggagcgctacgtctccatgcccagggaagaggctgcggaacacatccctcttctcttcttcgctttcccatcagccaaagatccgacctgggaggaccgattcccaggccggtccaccatgatcatgctcatacccactgcctacgagtggtttgaggagtggcaggcggagctgaagggaaagcggggcagtgactatgagaccttcaaaaactcctttgtggaagcctctatgtcagtggtcctgaaactgttcccacagctggaggggaaggtggagagtgtgactgcaggatccccactcaccaaccagttctatctggctgctccccgaggtgcctgctacggggctgaccatgacctgggccgcctgcacccttgtgtgatggcctccttgagggcccagagccccatccccaacctctatctgacaggccaggatatcttcacctgtggactggtcggggccctgcaaggtgccctgctgtgcagcagcgccatcctgaagcggaacttgtactcagaccttaagaatcttgattctaggatccgggcacagaagaaaaagaattagttccatcagggaggagtcagaggaatttgcccaatggctggggcatctcccttgacttacccataatgtctttctgcattagttccttgcacgtataaagcactctaatttggttctgatgcctgaagagaggcctagtttaaatcacaattccgaatctggggcaatggaatcactgcttccagctggggcaggtgagatctttacgccttttataacatgccatccctactaataggatattgacttggatagcttgatgtctcatgacgagcggcgctctgcatccctcacccatgcctcctaactcagtgatcaaagcgaatattccatctgtggatagaacccctggcagtgttgtcagctcaacctggtgggttcagttctgtcctgaggcttctgctctcattcatttagtgctacgctgcacagttctacactgtcaagggaaaagggagactaatgaggcttaactcaaaacctgggcgtggttttggttgccattccataggtttggagagctctagatctcttttgtgctgggttcagtggctcttcaggggacaggaaatgcctgtgtctggccagtgtggttctggagctttggggtaacagcaggatccatcagttagtagggtgcatgtcagatgatcatatccaattcatatggaagtcccgggtctgtcttccttatcatcggggtggcagctggttctcaatgtgccagcagggactcagtacctgagcctcaatcaagccttatccaccaaatacacagggaagggtgatgcagggaagggtgacatcaggagtcagggcatggactggtaagatgaatactttgctgggctgaagcaggctgcagggcattccagccaagggcacagcaggggacagtgcagggaggtgtggggtaagggagggaagtcacatcagaaaagggaaagccacggaatgtgtgtgaagcccagaaatggcatttgcagttaattagcacatgtgagggttagacaggtaggtgaatgcaagctcaaggtttggaaaaatgacttttcagttatgtctttggtatcagacatacgaaaggtctctttgtagttcgtgttaatgtaacattaataaatttattgattccattgctttaacatttgaaatttattttggttttttgttcaagaaaacaaaactattattgtgatggcatttgcagaagctcagtaaaacactatatactgaataacaccaaaataagctttaaaaaaataaaattaagtaattataaagtta"
						.toUpperCase());
		this.builderForward.setGeneSymbol("RETSAT");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 2, 85571471,
				PositionType.ZERO_BASED), "C", "G");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(5, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("1074-1G>C", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("?", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.CODING_TRANSCRIPT_INTRON_VARIANT,
				VariantEffect.SPLICE_ACCEPTOR_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc002spq_3() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc002spq.3	chr2	+	85661917	85664152	85662078	85663705	5	85661917,85662535,85662788,85663588,85663959,	85662246,85662619,85662947,85663717,85664152,	Q7Z4S9	uc002spq.3");
		this.builderForward
				.setSequence("ggcattttccggtgcctgcaccaaatcaggaagctcctagtctgtgccccaagatcacgcctcatttggaggtcacacgaggcctggctggtgggtcctgccagggcactggatctggagagtggaggaagcactgggtgccggggtttgggctcggggccatgtacgcctcctcctaccccccacccccgcagctgtcccctagaagccacctttgcccacccccaccccaccccaccccaccccagctcaataatctgcttcttctagaaggaaggaaatcgtctcttccctctgtagcccccactgggagtgcctcagctgctgaggacagtgatctgctgactcagccttggtactcggggaactgtgaccgctatgctgttgagagtgccctgctccacttacaaaaggatggggcctataccgtgcgccccagctcagggcctcatggctcccagcccttcaccctggcagtgcttctccgaggccgggtcttcaacattcccatccggcggctggatggcggacgccactatgccctgggccgggagggcaggaaccgtgaggagctcttctcctccgtggcggccatggtccagcacttcatgtggcaccctctgccccttgtggacagacacagcggcagccgggaactcacctgcctgctcttccccaccaagccttgaggccacagcgaagaatgcaggtgtctgcccagttcactaggtcctggatgaaggaaccgtggtggcctagaccagtcaggggacagcacaggcactgctggaacagcaaaggatcctctcacatctacttgtgggcctagggcaccctgagagggactggcctaccttgcacaagttcacattcaataaacatttgttgattgaaaaaaaaaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("SH2D6");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 2, 85662247,
				PositionType.ZERO_BASED), "T", "A");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(0, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("168+2T>A", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("?", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.CODING_TRANSCRIPT_INTRON_VARIANT,
				VariantEffect.SPLICE_DONOR_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc002tfo_4() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc002tfo.4	chr2	-	110880913	110962639	110881367	110962545	18	110880913,110883213,110886762,110889255,110901118,110902069,110904329,110905492,110907758,110917703,110919179,110920624,110922264,110922628,110926028,110927382,110958997,110962476,	110881640,110883258,110886836,110889368,110901218,110902146,110904412,110905603,110907833,110917832,110919274,110920709,110922307,110922732,110926130,110927575,110959071,110962639,	O15259-3	uc002tfo.4");
		this.builderForward
				.setSequence("ctgggaggcgggcgcacatcgatggcgtcaccttctggcgccgccggttggtttccctggcaactggagcaatcagagcaccgcagccagggagatgctggcgagacgacagcgagatcctctccaggccctgcggcgccgcaatcaggagctgaagcaacaggttgatagtttgctttctgagagccaactgaaagaagctctagaacccaataaaagacaacatatttatcaaagagttggggcacctactgaagaagaggaagaaagtgaaagtgaagatagtgaagacagtggtggggaggaagaagatgcagaggaggaagaggaagagaaagaggaaaatgaatctcacaaatggtcaaccggtgaagaatacatcgctgttggagattttactgctcagcaagttggagatcttacatttaagaaaggggaaattctccttgtaattgaaaaaaaacctgatggttggtggatagctaaggatgccaaaggaaatgaaggtcttgttcccagaacctacctagagccttatagtgaagaagaagaaggccaagagtcaagtgaagagggcagtgaagaagatgtagaggcggtggatgaaacagcagatggagcagaagttaagcaaagaactgatccccactggagtgctgttcagaaagcgatttcagagataaacactgttgatgtgttaactacgatgggagctattcctgcagggttcaggccttccacgctctcacagcttctggaggaagggaatcaatttcgagcaaattacttcttacaaccagagctcatgccttcacaactggccttcagagatctgatgtgggatgctacagaaggcactattaggtcgagaccaagtcgtatttcattgattctgacattatggagctgtaaaatgattcctcttccaggaatgagcatacaggttctcagcagacatgtacgcctctgtctatttgatggtaataaggttctgagcaacattcatacagtcagagccacatggcaacctaaaaagcccaaaacatggaccttttctccccaggttactcgcatcttaccatgtttgcttgatggtgattgctttatcaggtctaattctgcatctccagatcttggaatattatttgaacttggaatttcttatattcgcaattcaactggtgaaagaggagagttaagctgtggctgggtgtttcttaaactttttgatgccagtggagttcctattccagcaaaaacttatgagcttttcttgaatggtggtactccttatgaaaaaggtattgaagtggacccttcaatatccagaagagcacacggcagtgttttctaccagattatgacaatgagaaggcagcctcaacttctagtgaaactgagatccttgaacagaagatcaagaaatgtactaagtctactgccagaaacattaattggaaatatgtgttctattcacttgttgatattttatcgacaaattcttggagatgtgctcctgaaagacaggatgagcttgcaaagtactgatttaattagccatcccatgctggccaccttccccatgctcttggagcagcctgatgtgatggatgctctcaggagttcgtgggctggaaaagaaagcacattaaaaagatcagagaagagagacaaagagttcctgaagtccacgtttctcctggtttaccatgactgcgtgctcccacttctccactccacacgcctacccccattcaggtgggcagaagaagagactgagactgcacggtggaaagttatcactgacttccttaagcaaaaccaagaaaaccagggcgccctccaagctctgctgtcaccagacggagttcatgaaccttttgacctttcagagcagacctatgacttcttgggtgaaatgagaaagaatgcagtgtgacagtggcagcctctagccctcagcttcccacggaatcagatggatcctccacgattacgtgaataaaatgatggaaccaaaaatcactgtcactttacaacttaggttttactcttttctttctacagaccatatttttaaagaaatgtttatacaataatttaaatattttttaaaaccataaaataaatttttataaggaatactgttatatctaaatttaaacagtatttattttttcaaaaacagctacttaagttaatggtatagatttctataaaagcaagattttgtcaaaaactaaatttatgattattcaagaaagtgaaaaaaacaacctacagaatgggaaaacatatttgcaaatcatctaactgataaaggtctagtatccaaaatatttaaatttatgagtgttaataaaatttatcttgttcaatgaagaggaagttaaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("NPHP1");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 2, 110926130,
				PositionType.ZERO_BASED), "C", "T");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(2, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("337-1G>A", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("?", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.CODING_TRANSCRIPT_INTRON_VARIANT,
				VariantEffect.SPLICE_ACCEPTOR_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc003gpr_1_second() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc003gpr.1	chr4	+	20255234	20620788	20255438	20620632	37	20255234,20258294,20259490,20270432,20469374,20482338,20487822,20490441,20493383,20512117,20512688,20521004,20525382,20525636,20526771,20530571,20533606,20535194,20541063,20543075,20544116,20547653,20550110,20550679,20552449,20555427,20568884,20569140,20570487,20591266,20597319,20598037,20599887,20611635,20618532,20619061,20620390,	20255617,20258366,20259562,20270504,20469446,20482410,20487894,20490605,20493522,20512189,20512760,20521076,20525526,20525800,20526795,20530722,20533681,20535338,20541207,20543242,20544249,20547722,20550182,20550751,20552521,20555591,20569009,20569238,20570627,20591360,20597457,20598278,20600018,20611790,20618821,20619273,20620788,	O94813	uc003gpr.1");
		this.builderForward
				.setSequence("cagagcagggtggagagggcggtgggaggcgtgtgcctgagtgggctctactgccttgttccatattattttgtgcacattttccctggcactctgggttgctagccccgccgggcactgggcctcagacactgcgcggttccctcggagcagcaagctaaagaaagcccccagtgccggcgaggaaggaggcggcggggaaagatgcgcggcgttggctggcagatgctgtccctgtcgctggggttagtgctggcgatcctgaacaaggtggcaccgcaggcgtgcccggcgcagtgctcttgctcgggcagcacagtggactgtcacgggctggcgctgcgcagcgtgcccaggaatatcccccgcaacaccgagagactggatttaaatggaaataacatcacaagaattacgaagacagattttgctggtcttagacatctaagagttcttcagcttatggagaataagattagcaccattgaaagaggagcattccaggatcttaaagaactagagagactgcgtttaaacagaaatcaccttcagctgtttcctgagttgctgtttcttgggactgcgaagctatacaggcttgatctcagtgaaaaccaaattcaggcaatcccaaggaaagctttccgtggggcagttgacataaaaaatttgcaactggattacaaccagatcagctgtattgaagatggggcattcagggctctccgggacctggaagtgctcactctcaacaataacaacattactagactttctgtggcaagtttcaaccatatgcctaaacttaggacttttcgactgcattcaaacaacctgtattgtgactgccacctggcctggctctccgactggcttcgccaaaggcctcgggttggtctgtacactcagtgtatgggcccctcccacctgagaggccataatgtagccgaggttcaaaaacgagaatttgtctgcagtggtcaccagtcatttatggctccttcttgtagtgttttgcactgccctgccgcctgtacctgtagcaacaatatcgtagactgtcgtgggaaaggtctcactgagatccccacaaatcttccagagaccatcacagaaatacgtttggaacagaacacaatcaaagtcatccctcctggagctttctcaccatataaaaagcttagacgaattgacctgagcaataatcagatctctgaacttgcaccagatgctttccaaggactacgctctctgaattcacttgtcctctatggaaataaaatcacagaactccccaaaagtttatttgaaggactgttttccttacagctcctattattgaatgccaacaagataaactgccttcgggtagatgcttttcaggatctccacaacttgaaccttctctccctatatgacaacaagcttcagaccatcgccaaggggaccttttcacctcttcgggccattcaaactatgcatttggcccagaacccctttatttgtgactgccatctcaagtggctagcggattatctccataccaacccgattgagaccagtggtgcccgttgcaccagcccccgccgcctggcaaacaaaagaattggacagatcaaaagcaagaaattccgttgttcagctaaagaacagtatttcattccaggtacagaagattatcgatcaaaattaagtggagactgctttgcggatctggcttgccctgaaaagtgtcgctgtgaaggaaccacagtagattgctctaatcaaaagctcaacaaaatcccggagcacattccccagtacactgcagagttgcgtctcaataataatgaatttaccgtgttggaagccacaggaatctttaagaaacttcctcaattacgtaaaataaactttagcaacaataagatcacagatattgaggagggagcatttgaaggagcatctggtgtaaatgaaatacttcttacgagtaatcgtttggaaaatgtgcagcataagatgttcaagggattggaaagcctcaaaactttgatgttgagaagcaatcgaataacctgtgtggggaatgacagtttcataggactcagttctgtgcgtttgctttctttgtatgataatcaaattactacagttgcaccaggggcatttgatactctccattctttatctactctaaacctcttggccaatccttttaactgtaactgctacctggcttggttgggagagtggctgagaaagaagagaattgtcacgggaaatcctagatgtcaaaaaccatacttcctgaaagaaatacccatccaggatgtggccattcaggacttcacttgtgatgacggaaatgatgacaatagttgctccccactttctcgctgtcctactgaatgtacttgcttggatacagtcgtccgatgtagcaacaagggtttgaaggtcttgccgaaaggtattccaagagatgtcacagagttgtatctggatggaaaccaatttacactggttcccaaggaactctccaactacaaacatttaacacttatagacttaagtaacaacagaataagcacgctttctaatcagagcttcagcaacatgacccagctcctcaccttaattcttagttacaaccgtctgagatgtattcctcctcgcacctttgatggattaaagtctcttcgattactttctctacatggaaatgacatttctgttgtgcctgaaggtgctttcaatgatctttctgcattatcacatctagcaattggagccaaccctctttactgtgattgtaacatgcagtggttatccgactgggtgaagtcggaatataaggagcctggaattgctcgttgtgctggtcctggagaaatggcagataaacttttactcacaactccctccaaaaaatttacctgtcaaggtcctgtggatgtcaatattctagctaagtgtaacccctgcctatcaaatccgtgtaaaaatgatggcacatgtaatagtgatccagttgacttttaccgatgcacctgtccatatggtttcaaggggcaggactgtgatgtcccaattcatgcctgcatcagtaacccatgtaaacatggaggaacttgccacttaaaggaaggagaagaagatggattctggtgtatttgtgctgatggatttgaaggagaaaattgtgaagtcaacgttgatgattgtgaagataatgactgtgaaaataattctacatgtgtcgatggcattaataactacacatgcctttgcccacctgagtatacaggtgagttgtgtgaggagaagctggacttctgtgcccaggacctgaacccctgccagcacgattcaaagtgcatcctaactccaaagggattcaaatgtgactgcacaccagggtacgtaggtgaacactgcgacatcgattttgacgactgccaagacaacaagtgtaaaaacggagcccactgcacagatgcagtgaacggctatacgtgcatatgccccgaaggttacagtggcttgttctgtgagttttctccacccatggtcctccctcgtaccagcccctgtgataattttgattgtcagaatggagctcagtgtatcgtcagaataaatgagccaatatgtcagtgtttgcctggctatcagggagaaaagtgtgaaaaattggttagtgtgaattttataaacaaagagtcttatcttcagattccttcagccaaggttcggcctcagacgaacataacacttcagattgccacagatgaagacagcggaatcctcctgtataagggtgacaaagaccatatcgcggtagaactctatcgggggcgtgttcgtgccagctatgacaccggctctcatccagcttctgccatttacagtgtggagacaatcaatgatggaaacttccacattgtggaactacttgccttggatcagagtctctctttgtccgtggatggtgggaaccccaaaatcatcactaacttgtcaaagcagtccactctgaattttgactctccactctatgtaggaggcatgccagggaagagtaacgtggcatctctgcgccaggcccctgggcagaacggaaccagcttccacggctgcatccggaacctttacatcaacagtgagctgcaggacttccagaaggtgccgatgcaaacaggcattttgcctggctgtgagccatgccacaagaaggtgtgtgcccatggcacatgccagcccagcagccaggcaggcttcacctgcgagtgccaggaaggatggatggggcccctctgtgaccaacggaccaatgacccttgccttggaaataaatgcgtacatggcacctgcttgcccatcaatgcgttctcctacagctgtaagtgcttggagggccatggaggtgtcctctgtgatgaagaggaggatctgtttaacccatgccaggcgatcaagtgcaagcacgggaagtgcaggctttcaggtctggggcagccctactgtgaatgcagcagtggatacacgggggacagctgtgatcgagaaatctcttgtcgaggggaaaggataagagattattaccaaaagcagcagggctatgctgcttgccaaacaaccaagaaggtgtcccgattagagtgcagaggtgggtgtgcaggagggcagtgctgtggaccgctgaggagcaagcggcggaaatactctttcgaatgcactgacggctcctcctttgtggacgaggttgagaaagtggtgaagtgcggctgtacgaggtgtgtgtcctaaacacactcccggcagctctgtctttggaaaaggttgtatacttcttgaccatgtgggactaatgaatgcttcatagtggaaatatttgaaatatattgtaaaatacagaacagacttatttttattatgagaataaagactttttttctgcatttg"
						.toUpperCase());
		this.builderForward.setGeneSymbol("SLIT2");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 4, 20620682,
				PositionType.ZERO_BASED), "G", "A");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(36, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("20620683G>A", annotation1.getGenomicNTChange().toHGVSString());
		Assert.assertEquals("*51G>A", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.THREE_PRIME_UTR_VARIANT), annotation1.getEffects());
	}

	//
	// Various NC-RNA Exonic Variants
	//

	@Test
	public void testRealWorldCase_uc009zky_1() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc009zky.1	chr12	+	48877022	48888762	48877022	48877022	8	48877022,48879960,48880470,48882706,48882878,48884594,48886729,48888580,	48877144,48879991,48880509,48882739,48883077,48884619,48886778,48888762,		uc009zky.1");
		this.builderForward
				.setSequence("ggcctggagctatctccatcttcagctccagagtccttggtttctgtctgagaacaaatggcacagcatccctgccaggatcaggaacaaaaggtagaaatgacctccaagcagcagagaagcacatccatagaagagacaatgagaccacaggaaaaacaggtaaccatcactgaaaccctgtgggaccaggtgctgacagtttttaaggatatacaaaaggagagtaccactgacttgtcagtccaggcggctaaggcaagaagatgcctaacagttcccttgaggaaatgtgataatacaaacagaaaattgtatctgctagaacttgtctcagcaagaatactctatgtggttgtatgaaatgctgataatgactaagctggggagaatcccctagtgatgtagtaaactgccttattagctgcaggaagatgctcggattcgagggatgagcaactgctccatgacacccatgacatcagcacccaggactggaagcataaggcctccagattccttgatgaccccaaagttgagaagattgcagttcagctctggagagcagccatcaggaggccgtatccacaacctgaagacacagctcttcagtcaatcagcttactaccctggaccctaactctacaatcaaggaagaaggacatctctgcttccgccag"
						.toUpperCase());
		this.builderForward.setGeneSymbol("C12orf54");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 12, 48883011,
				PositionType.ZERO_BASED), "T", "C");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(4, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("359T>C", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals(null, annotation1.getProteinChange());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.NON_CODING_TRANSCRIPT_EXON_VARIANT),
				annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc010pmp_1() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(refDict,
						"uc010pmp.1	chr1	-	173387087	173430501	173387087	173387087	2	173387087,173429516,	173387761,173430501,	uc010pmp.1");
		this.builderForward
				.setSequence("agatgctgcagttagctgggggtccactctacaccctggttgcctcagtttttcctgtacttggaggtatcagcagtgaatgctatgaaacagcaaagatggcagcctgccccttcccctggaagctccatcccagggcggtactgtcctgttgccagcatgagcacacagtatgtagggggtggctggagaccccagttgggacatctcacctagtcaggaggaacgggatcagggatccacttaaagaagcagttgggctgtttttgatagagcagttgtgctgtgttggggatcccatcagccccttgttggtttgggctctccgaggcccacaggctgaactggctgaggtgcccaacagccaagttggtggcccgccttcccctctgggcactctgtcccagggaaaaattagaagtctgttggccacagaacatgggcgggggtggctggataccccagctggcatgatctacccgtcaaggaaaagtggatcagggtcccacttaaagaagccatctggccatgcctcaatagaacagccttgttgtgctgggaaactgcttctgcccttgttggcttggactctctaaagcccactggttggaatggctgagtcattcaaacagccagcttggtggccctcctttcctcccctcctcccctcccctcctccttccctcctcgcctcccctcgggcatgctattccagaaagagatcaggatccgtttgtggaatacaggcagagatggctggaggccccggttgggaggtccctcccagtgaagaggaatggattggggccccacttaaagaagcaacctggccacgttcaggcaatgccgctgtagtgttctgggaagacccttccttgtttgaaccatatggattctccgaagcccatagactgaaatggctgagtcaaccaaacagtagagatggcagcccacccctcccactggggactctgtcccatcacagtatgaaagttttcaagacgcctacaagtggaaaatgcactcctcccagccaaatcaagcttcgccagattccttacagctgtgtgcagtgaaatgactaatgaaagagccctctgctgctggtggaactttcacatatggcactgttatgccactcagcaggacccgaccctggtatttacatattggatgtcaccccaaaagcccctgaccagggggtttccgggatgtaataagaatgccactctgggctttggaaggtgcaccgtatgcacacccatactatagggcaggggttagcatttcagacccctgttcctcccttgacaagcaagtgcttttctccagggaagacgttcaggttcattcatagatccattggtgccgtgggcagctcagtaacaatagctctgctgcctaccagctgctggtggaggtgttgagaatacctaatgaggaagtcagggcccctgcgtgcttcagcagagctggatgcctggcatggcaatcaatggacacaaacctttggataatttccgtccttggcggtgtgttctatagcataggtatgaagtgggtgctctgggagctcagaagaggggctcttctcttaacttatgggtggccagggaaggcttcctggagctcaaatgactgagtggagtcttcaacatt"
						.toUpperCase());
		this.builderForward.setGeneSymbol("BC136808");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 173429994,
				PositionType.ZERO_BASED), "G", "A");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(0, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("507C>T", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals(null, annotation1.getProteinChange());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.NON_CODING_TRANSCRIPT_EXON_VARIANT),
				annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc021yhe_1() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory.parseKnownGenesLine(refDict,
				"uc021yhe.1	chr5	+	159912358	159912457	159912358	159912358	1	159912358,	159912457,		uc021yhe.1");
		this.builderForward
				.setSequence("ccgatgtgtatcctcagctttgagaactgaattccatgggttgtgtcagtgtcagacctctgaaattcagttcttcagctgggatatctctgtcatcgt"
						.toUpperCase());
		this.builderForward.setGeneSymbol("MIR146A");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 5, 159912417,
				PositionType.ZERO_BASED), "C", "G");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(0, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("60C>G", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals(null, annotation1.getProteinChange());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.NON_CODING_TRANSCRIPT_EXON_VARIANT),
				annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc010wdn_1_first() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc010wdn.1	chr17	-	36344875	36375177	36344875	36344875	15	36344875,36345438,36346761,36347009,36351935,36352421,36353600,36357157,36358124,36358298,36358841,36361696,36365054,36367406,36375099,	36344956,36345478,36346847,36347082,36351996,36352526,36353765,36357272,36358158,36358395,36359042,36361804,36365176,36367522,36375177,		uc010wdn.1");
		this.builderForward
				.setSequence("aaatacatgctacaggatttaactatcagaatgaagatgaaaaagtcaccttgtctttccctagtactctgcaaacaggtaataactctttccaatactttgtattttgtttagtatgccactttttggaagtggtcactgtcaatatcttgttatggtttgatggccttggagatttaaaaccgagtaaaagtgtacgggaaccttaaagatagattttgttggagagctgaatgacaaaatgaaaggtttctatagaagtaagtatactaccccttctggagaggtgcgctatgctgctgtaacacagtttgaggctactgatgcccgaagggcttttccttgctgggatgagcctgctatcaaagcaacttttgatatctcattggttgttcctaaagacagagtagctttatcaaacatgaatgtaattgaccggaaaccataccctgatgatgaaaatttagtggaagtgaagtttgcccgcacacctgttacatctacatatctggtggcatttgttgtgggtgaatatgactttgtagaaacaaggtcaaaagatggtgtgtgtgtctgtgtttacactcctgttggcaaagcagaacaaggaaaatttgcaatagaggttgctgctaaaaccttgcctttttataaggactacttcaatgttccttatcctctacctaaaattgatctcattgctattgcagactttgcagctggtgccatggagaactgggaccttgttacttatagggagactgcattgcttattgatccaaaaaattcctgttcttcatcccgccagtgggttgctctggttgtgggacatgaacttgcccatcaatggtttggaaatcttgttactatggaatggtggactcatctttggttaaatgaaggttttgcatcctggattgaatatctgtgtgtagaccactgcttcccagagtatgatatttggactcagtttgtttctgctgattacacccgtgcccaggagcttgacgccttagataacagccatcctattgaagtcagtgtgggccatccatctgaggttgatgagatatttgatgctatatcatatagcaaaggtgcatctgtcatccgaatgctgcatgactacattggggataaggactttaagaaaggaatgaacatgtatttaaccaagttccaacaaaagaatgctgccgcaggatggacgtggtagaggtcgcgggcagttggtgggcacaagagcgagaggacatcattatgaaatacgaaaagggacaccgagctgggctgccagaggacaaggggcctaagccttttcgaagctacaacaacaacgtcgatcatttggggattgtacatgagacggagctgcctcctctgactgcgcgggaggcgaagcaaattcggcgggagatcagccgaaagagcaagtgggtggatatgctgggagactgggagaaatacaaaagcagcagaaag"
						.toUpperCase());
		this.builderForward.setGeneSymbol("LOC440434");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 17, 36353760,
				PositionType.ZERO_BASED), "C", "T");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(8, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("876G>A", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals(null, annotation1.getProteinChange());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.NON_CODING_TRANSCRIPT_EXON_VARIANT),
				annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc002kts_3() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc002kts.3	chr18	-	19405447	19411367	19405447	19405447	3	19405447,19410580,19411282,	19409413,19410755,19411367,		uc002kts.3");
		this.builderForward
				.setSequence("actgaggaagaatataagttagcctcgtagtgaccatgtggaagtcctgacctcgcttgcagacttgacctctggatgtgacaaggtgaggttctgtgttcactgatacatccccttccttcaccgggctcatgaaaagaaatgctgaatcagcaaatagagtgaacgaacaatcaagatgagatggctgctgttgatgtgattctctaggttactacctttcttcctgctgcaaatgctgcatgacactaagcttacaggatctagaagcagactacaaaaaaagaagaaattcactctgaagactgaagaaacactaggctgatcttaagtttccgaaatacactctgtaacactacactgattctttgaatctcttgcaaaaagaatcaaaccaggacaaagttcaacagataactggtcaaatgcttaggaatgtctatgtccaccaagaatatggaaccattaatgccatgcttcaggtaaacttgttacaaaatttagtcttcgataaattagtctctgcaaatgatttaccattgctctacattagtaagctgaatgtttcactaacaaataagaaaataaaatatttcatgtttttacagctaacaacttagtaatacctactcagagtacatacttctttatgtacccatatgaacatacaatgctatggaatgtaaagaagtatgtatttttggtaggcaataaaccaccaagggagaattaaactgagctaaaagaagctcttgcttctttctacgtgaatgaccgtcatatggtaaattaacctagtcatgaacagatgacctttggcaatactaacaccaaaaaaaaaaggttatctactactgatagatagaaactatccctaaggtttttatttcatgaaggaaaagaagatccagtgaagtagatttcaattatcatgaaactccttttaaatcatctaagtttaaaacagacatcttacatttttaaattaggcactctcattattaaaatttgactgcaaaagggaaaatagtattactagcacaaaagaatgaggaatacacatgacctattttctgtctccacaatttaatcttaagtatatctcattagtagggctcactgatttgtacagaaaaataagatactaagagctggcttcattcacataataagctatttcttctcattttgataaatgtataattaatattagaggaatttctgaccaaaagttttgttgttttaaaaaggctttcttttttgacagctacaaacatagtggcccttgtccttccttctcaagtttatcagggtcttaacccaaatctctactttaaatgtgaagttaaaacttaaaattaattaaaatgtaagatttaagcatgaaaaatgacaaaattctggtccacagctgtaagtcagcagggtatattggtaacagactaccaaatatttaaaagaaattaatttatctgtattatcttcgaatttgctttctccaaagagggcatgtaaaatgaaagcactatagtcaagaaccaatttatacagatcgaagaactgtttttattgtactatgcaattacaattcaatttctctaaacttgatcaatccattgtttagaaatatcaaaatacttttatttcacacagtttgtatattatttataaaaaattccaaataatgctcttagcaaaatttataagcagtacctgttttaagctactaatgacaaagttagaaaataagagtaatattcacatattctaaccatttcttactaaaagtattatttcaactgaaatatttacacatgtataaaagcataatattttaaattttttgtatcagaaattgaatggaaatgactaagtttaactatagttcattttctgtttaataagactagttaatacaaagatgattttcattttatcagattataaagaatgagaatgacttacacatggcatcatatgtgttactgcatgtttactggaaattataaaaggcacataattttaaaattctacactttcaaagaaattagttcaaagcttaactcacataagcatagcccttctgatcatccacaaaacagaatggcaatgattcatgttctatttggtaagtttacctgtataggtcctaaataccaagaactcaaaatatatacctcaaaagtagaggttcttctataaaaatactcaaagcttattactctacttgtgttttataataaacagttatattggtcattctataggtatacaagttgtttaataatccttctcaaacatcctagcaactaagactctctttgctataatattaagactatataaataatccttagaaaataaaatgacaggctattcatttttaaaagtaaaattatatgctgattctggtgcttgcgagtttaacttatctaagagtattttcaatcactctaaaatgtcacatctatcacttagatcagctgttcatttttaactgagtacatattaaactctgggcatctgctgacactggtataactcaaacatttcaactgtctccctgacagggataacagatgctgacattatcaaatgctgtactatttttttcccatggcctctccttgccccgatatggccagtatatgaaactgaactgccatccaaaaacacacttgagcaactggatctgctaaacagctcccatataactagtaaaaatagaagccagtattattgctatcttcagcacttaagtttaggcagtttaacacctgtttcacttctactagaaaaaatgatgaaaaagaattgtagttaatttcaccagcagtatgatacttaccaaccaacagacgcagaatatacttagtgtaagagtcatttgcatactgcctgaattagcgttgacctctcagtttaaaatagtaactgtaaagaatatttctctactctatatttctctgagagcacttaactttataggcaaattataatttatcaaacttttattttatgcaagaagacacccctctcaagacattgaaatcaacagtaaaaccacattataacaaaaatttactctgatttttcaagtattatccacacaatagattttaatacttgtatccatggattgtaactaaccaccatgtattactaaggcgataatgagaagtaacttggcaatctgtacccaaaatgacaaagaacttagtcaaggaaatagacctttaaaagggttaaattactaagctatgattagtaacaccacgtaagcaaaaacaaagaaaaactggcatgcatggtagttcctttcgacacaagtccaagcaatacactgaatacacatgtgatctatttataaatcaaagggcagaggtactttcttaaggccactgatattcctacacccatcaacaatttgctattagccctcaactataatgctaaaagaagcagtggcagtagaagggggtgactaactgaactcttccccaaccttttagcttaactatttatctggtttgctgcctagagataaatggaaaacattactctaaaatacatcaaggtctatttgttcctttaaatactattggttttgaaatagcttattgtctacatgtaagaattaagcaaagacttcggctgtggacaagattagaaaaagaaaaagaagcaaagatgttgaaaacaaagacgtacattgatcattccttaatgcagtaatgtgttaaaaggaaaagatttttaaaccattaagcgcaggaaaacagtaggaaagtgacttcaaaatggaaaatttgacattcagtgactgaagcattggtatgataatttctaacacttgtagaaggtccatgactgtaattttaccaatgaaaagcatttaactgttttggattccaaactagcagcactacaatgctttgctagagctggtaaaatggaaccaaatcgcctcttcaatggatttggtccccttcaaccagctgtagctatgcattgattactacgggacaaccaacgttttcatttgtgaatatcaattacttgccaactaatttcaacttatttacattatgcagactgtgattataatcaaataacttgttactgttaattctacctggaaaagctagaatggttaaagatgtttataaaaagctttatgaaaaatgtatggatgacttaaggatttcaaataaaaattttactta"
						.toUpperCase());
		this.builderForward.setGeneSymbol("mir-133b");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 18, 19408949,
				PositionType.ZERO_BASED), "C", "T");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(2, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("724G>A", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals(null, annotation1.getProteinChange());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.NON_CODING_TRANSCRIPT_EXON_VARIANT),
				annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc002wve_3() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc002wve.3	chr20	-	25754653	25843837	25754653	25754653	3	25754653,25829340,25843695,	25755948,25829584,25843837,		uc002wve.3");
		this.builderForward
				.setSequence("gacctctcctcggggtcgacgggaaggtctccggatgccaggactcgcaaagggccgaccagaatgaggaaatcccaggcggagtccgggggaggcagcacggcatcccagcctcagggctgcccggacggtgttggttggggtaagattggggaagatgccaggtgatagaatccaccctgcctctgacagcatccaatctggagcaaaggcttctttcaaccttcccccaagctcccaatacaagcccaaattctggaacaagtcctttccaacacctccttcagagaagctccctggctccaggcagtgcttgctcccctcactgcagcaggaagcctaacttgcgaactgcaggtgtgctccaggtggtcactggatgcaagaaatggtgcgagaactttggatgtggaacgtggaggaggaggaacacgaagtggggatctgcacttggggtggtcagcactgcggatgcccagcaaagtcactgcctggtccacatcccggaggggtctctgcgcctcagtcggcatcgcagctgatggtgaaacttttggtgtggcagaagagtgtccacaaacttcggaaggtatccgctacatcctccatagccgtgtatccctgcccagggcaatcttctgggggagcggaaagtcctgctcccgggccagggctggctggctggagccacttgtgcggcgcagccctggcggaggttcaggcggccccggtgtcgcaagcggctgtgagtgatgcctccctggggccggagtggtcccaggaaggctgcagaccagggctgaccagcgggcagcatggtggccgcgatggaaggtgacggggttcgcagcgccaggggacccagcagagcccgagcccgggcatcccgcatctccagcagcatcgcaggtaggcctggtgctggtgaagtggcggccagtgcacaaggcctacgacccagtcccagaggccagcccattgtcagctaacttcaggaaccccgggccagctgaggccccgggccccatgggcaagacaaagggcagagggtccgcaggcggggccgagtgcaggcagtgcaggcctggctccaccgccgcggagctcgcagggcgcagcaggcatcgggcagtgaccagggcttccagaggaggctgtgcacccctgcaaaggctcctgccctgcgtccagcctatccgcggggactccacgtgcaccccctcctcattgtccttgtctagggccgcggcggcaaggtccttgctcccatggcgtgactccagggtgcaggagcctgggctgagcaggtggagtagggtgagctccgccaagaacccagcgagagtggcgccccagggcggcacaggaggccgcatttaacatgtcaatcatctgaaagattttatggcatcttttttttgacatcttataatatctataatgtttattatatcttgtgatataattattaacaccacttcagtgtgattattatgattatttttataccaacacatcttcaattattaatattcccagttgctagagaaaactgaaaactactagttttgaaagcctcacttctgccaatggaagcacattccagcatgtcgccaacgcaatccactttccaccactttcacaaaaaacgttactgcacaattatactatcctaccctta"
						.toUpperCase());
		this.builderForward.setGeneSymbol("FAM182B");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 20, 25829351,
				PositionType.ZERO_BASED), "T", "C");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(1, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("375A>G", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals(null, annotation1.getProteinChange());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.NON_CODING_TRANSCRIPT_EXON_VARIANT),
				annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc004dzz_3() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory.parseKnownGenesLine(refDict,
				"uc004dzz.3	chrX	-	70711376	70712604	70711376	70711376	1	70711376,	70712604,		uc004dzz.3");
		this.builderForward
				.setSequence("ggagaccctgaaggacctggacaagtgctatgagaaggtccgccgcgacacggactgcgcgcagaagccgcgggtgccaccctgcgcccagcgggccctgatccgcagctaggagccgggcgatcagaagatccagatcaagagccagagggtggagctggtggagagcctcacgcggcaggtagacagccacgtggagccgttccacccgcagcaggagcagggcgacgcagcgcgacggcggcaaggtcggcccggacaggcccaggggcgaggcggacgcgcaggcggagaagtccaacagcaaagcgttctcggcggcagcgcaacaactagaaccgtgagaacgcgtccagcaaccgcgaccacgacgacgtcacctcgggcacgcccaaggagaagaaagcccagacctctaagaagaagcagagctccatggccaaggcgtagcggcaggcgtcccccgcagacctccccatcgaccccagcgagccctcctactgggagatgatccgctgcgacaacgaatgccccatcgagtggttccgcttctcgtgtgtgagtctcaaccataaaccaaagcgcaagtggtactgttccagatgccggggaaagaacgatgggcaaagcccttgagaagtccagaaaaaaaacagggcttataacaggtagtttggggacatgcgtctaatagtgaggagaacaaaataagccagtgtgttgattacattgccacctttgctgaggtgcaggaagtgtaaaatgtatatttttaaagaatgttgttagaggccgggcgcggtggctcacgcctgtaatcccagcactttgggaggccgaggcggtcggatcacgaggtcaggagatcgagaccatcctggctaacacggtgaaaccccgtctctactaaaaattcaaaaaaaaaattagctgggcgtggtggcgggcgcctgtagtcccagctattcgggaggctgaggcaggagaatggcatgaacctgggaggtggagcttgcagtgagccaaggtcgcgccactgcactccagcctgggcgacagagcgagactccatctcaaaaaaaaaaaaaaaaaaagaatgttagaaaaggagccattccttttatagggatgggagtgattctctttgccttttgttttcgttggcagacatgtgtaacaacaaaatggtctgttatcaacattttagaaactacaaatataggtttga"
						.toUpperCase());
		this.builderForward.setGeneSymbol("INGX");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, refDict.getContigNameToID()
				.get("X"), 70711957, PositionType.ZERO_BASED), "T", "C");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(0, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("647A>G", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals(null, annotation1.getProteinChange());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.NON_CODING_TRANSCRIPT_EXON_VARIANT),
				annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc004frk_2() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc004frk.2	chrY	-	8651358	8685423	8651358	8651358	5	8651358,8651928,8657040,8662901,8685386,	8651447,8652244,8657343,8662985,8685423,		uc004frk.2");
		this.builderForward
				.setSequence("gatggagcaagaatcatcttgtaacaggaatagtcagcatttcacataaattaaccttgaagagagaaatgcaaaagaagaaggaaattttccatcttacaaaactacattactttaaaagggacctctcatggtgcaccacctgagcgagggccttggatgtcttatggtggaagcagctgccatgattataacaatacatgagatagatacggcagaagtcaggaaagttgctcaaggagctatggcgatttttattcttgtggtcatgagaacattggcagaaaagaccaaaggaatccaccttctctggatagggtgcactctgctcctcttgaagcatatggtagttcaagttatgtgccatctacaggagatggtggggaaagtcaatctgaaaaaggcgactaaagcggattttaaaccagagaggtgctgtttcagagaaactaggcaacaacatcattctgcaacaaccagccgtggttgatgggtctgccaggctcaaatcattggccaactgccaaatcccatcctggtttttttctgcaaaacagccaatgctgtgacaaagtggcagcttggtgattatccacagaaggggcatgtgaccccaccccattgcagcagccatacttttgaccatcctagccctgtgtgctccatccatccccaggctgaaaatcggaggcaatttagtggttaaggatgaagtttctggccctacctggacccagcagaatcaaagtggatccagtgtcagctccagcctattcaatttcaatttttgaaatggagtcattactaataaataacctaccagccaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("TTTY11");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, refDict.getContigNameToID()
				.get("Y"), 8657214, PositionType.ZERO_BASED), "C", "A");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(2, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("250G>T", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals(null, annotation1.getProteinChange());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.NON_CODING_TRANSCRIPT_EXON_VARIANT),
				annotation1.getEffects());
	}

	/**
	 * <P>
	 * annovar: GPBAR1 chr2:219128506C>T
	 * </P>
	 */
	@Test
	public void testRealWorldCase_uc010zjy_1() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc010zjy.1	chr2	+	219125737	219128582	219127447	219128440	2	219125737,219127289,	219125939,219128582,	Q8TDU6	uc010zjy.1");
		this.builderForward
				.setSequence("gagaacccagacgggcagagcctgggtaggagagcctggccccgctgtccccactgggtggagacaccatgcacttggtccacttgtgctcttcagccaggacaccagacatggtccaaaccgctgcagggctggctgcagcaactccctgacactcaggaaggcccaggctgggcaggcaatacctgctcccaacagccatcgaacatgatacagcccacagcctgcgggtctgcgcccctggattaacatgctgccctgccaggaggacacgacctgcagccccatcctaactctggccaccccatcctgcaggcatgccggctgccgctccaggactcccctgtccccaggaccaagatgacgcccaacagcactggcgaggtgcccagccccattcccaagggggctttggggctctccctggccctggcaagcctcatcatcaccgcgaacctgctcctagccctgggcatcgcctgggaccgccgcctgcgcagcccacctgctggctgcttcttcctgagcctactgctggctgggctgctcacgggtctggcattgcccacattgccagggctgtggaaccagagtcgccggggttactggtcctgcctcctcgtctacttggctcccaacttctccttcctctccctgcttgccaacctcttgctggtgcacggggagcgctacatggcagtcctgaggccactccagccccctgggagcattcggctggccctgctcctcacctgggctggtcccctgctctttgccagtctgcccgctctggggtggaaccactggacccctggtgccaactgcagctcccaggctatcttcccagccccctacctgtacctcgaagtctatgggctcctgctgcccgccgtgggtgctgctgccttcctctctgtccgcgtgctggccactgcccaccgccagctgcaggacatctgccggctggagcgggcagtgtgccgcgatgagccctccgccctggcccgggcccttacctggaggcaggcaagggcacaggctggagccatgctgctcttcgggctgtgctgggggccctacgtggccacactgctcctctcagtcctggcctatgagcagcgcccgccactggggcctgggacactgttgtccctcctctccctaggaagtgccagtgcagcggcagtgcccgtagccatggggctgggcgatcagcgctacacagccccctggagggcagccgcccaaaggtgcctgcaggggctgtggggaagagcctcccgggacagtcccggccccagcattgcctaccacccaagcagccaaagcagtgtcgacctggacttgaactaaaggaagggcctctgctgactcctaccagagcatccgtccagctcagccatccagcctgtctctaccgggccccacttctctggatcagagaccctgcctctgtttgaccccgcactgactgaataaagctcctctggccgttaaaaaaaaaaaaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("GPBAR1");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 2, 219128505,
				PositionType.ZERO_BASED), "C", "T");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(1, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("*66C>T", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.THREE_PRIME_UTR_VARIANT), annotation1.getEffects());
	}

	//
	// Various Downstream Variants
	//

	@Test
	public void testRealWorldCase_uc001abo_3() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001abo.3	chr1	-	700244	714068	700244	700244	7	700244,701708,703927,704876,708355,709550,713663,	700627,701767,703993,705092,708487,709660,714068,		uc001abo.3");
		this.builderForward
				.setSequence("acaagggcgggtcttcgccgacaccatagaggtgggccgttggcgacgttagaggcgcgggtgttcggctacatcactggggcgccatggtgcctggagctgggcagttttctcatcagagtggggactggtaagagtgacctccccgccaggttctgtgtgttgccggctgaagaagggtagctgaaaaattcagacccagcacagtgtttatgttggtcaaaaatagaaaactatgtctggcgcggccgaggcgggaggacccttcaggccaagagcagcctagcaacatggcgcaaccccatctctgtagtcctacctcagccccccagctacttgaacccaaaggttcaaggctccagtgagctatgatcccaccacagcattccagcctgcgagattgagatgatgattattccccaccttctaagagacaaagaccaacgagccaccacagccaccagtcccagaacccgccaatgctggggaacggaaaatgagggagttcaactctggccctcacaatccagtggaggagacgcaactcatctgcctctgtccctctgggcacgcctcatgccaggtgcatctgtggacaggggccatgctcctgggcttccaaagttggagaaagctgccaggctcaggtctgaaggccagaattctacagtaagtcctactgagtcaaggtgggagcagggtcggtagcttccgaggctctgcgggagaatctgtttcctggccgtagaggtggcctgcactcctcagcctgtgctgcccgtctcgaatgactggagtttcctgcttctgtcactacacctcccaccctctccatcacctgctctgctcttacaaggatccgaagaaatggaatcatcgtatcgctgatctacgtaaacaaactgaagaattgtctgaaagaaaatatgacatgaacttatgaattcaacaggtgaagatttacaacttgataaatcaactttgtcagctcgagctgtaaaagccaaaggtccggtgatgatcccatacccttttttccagtctcatgttgaagatttttatgtagaaggccttcccaaaggaattttttttttttttttttgagatggagttttcactcttatcgcccaggctggggtgcaatggcgcaaccttgctggtcactgcaacctctgcctcctgggttcaagaaattctcctgccttagcctcccaagtcactgggattacaggtgcccaccaccacaccaggctaatttttgtatttttagtggagatgcggtttcaccatgttggccgggccagtctcgaactcctgacgtcaagtgatcttcccgcctcgactcctgatatcaagtgatcttcccgcctc"
						.toUpperCase());
		this.builderForward.setGeneSymbol("LOC100288069");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 753405,
				PositionType.ZERO_BASED), "C", "T");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(null, annotation1.getAnnoLoc());
		Assert.assertEquals(null, annotation1.getCDSNTChange());
		Assert.assertEquals(null, annotation1.getProteinChange());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.INTERGENIC_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc021vpr_2() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc021vpr.2	chr2	+	132285406	132291239	132285543	132290971	8	132285406,132287219,132288151,132289236,132290296,132290436,132290585,132290841,	132285919,132287264,132288400,132289375,132290354,132290510,132290642,132291239,	NP_001245233	uc021vpr.2");
		this.builderForward
				.setSequence("agcgacaggccccgcccccgccaaccgcctcgcgccttccgtcgcccggtttccatggtgacggggcgccaggctagggcggcctggccactgagccggggtgcagtggcagcgggagagtacctggcgatggcgatatgagcggtgcgggggtggcggctgggacgcggccccccagctcgccgaccccgggctctcggcgccggcgccagcgcccctctgtgggcgtccagtccttgaggccgcagagcccgcagctcaggcagagcgacccgcagaaacggaacctggacctggagaaaagcctgcagttcctgcagcagcagcactcggagatgctggccaagctccatgaggagatcgagcatctgaagcgggaaaacaagggtgagccggcgcggggccctaggccggccctgcctccccaggcacactcaacactgccgctcccgcagcacagaaacacagccatcaactccagcacacgcctgggctcagggggaacacaggacgatctccattacaagctcataatgaatcagacatcacagaagaaagatggcccctcaggaaaccacctttccagggcctctgctcccttgggcgctcgctgggtctgcatcaacggagtgtgggtagagccgggaggacccagccctgccaggctgaaggagggctcctcacggacacacaggccaggaggcaagcgtgggcgtcttgcgggcggtagcgccgacactgtgcgctctcctgcagacagcctctccatgtcaagcttccagtctgtcaagtccatctctaattcaggcaaggccaggccccagcccggctccttcaacaagcaagattcaaaagctgacgtctcccagaaggcggacctggaagaggagcccctacttcacaacagcaagctggacaaagttcctggggtacaagggcaggccaggcagtgcgaagtgctcatccgcgagctgtggaataccaacctcctgcagacccaagagctgcggcacctcaagtccctcctggaagggagccagaggccccaggcagccccggaggaagctagctttcccagggaccaagaagccacgcatttccccaaggtctccaccaagagcctctccaagaaatgcctgagcccacctgtggcggagcgtgccatcctgcccgcactgaagcagaccccgaagaacaactttgccgagaggcagaagaggctgcaggcaatgcagaaacggcgcctgcatcgctcagtgctttgagccaccccaatctggtcagtgccaggcccaccaacctgcagctggagactggctctctatagcatttcctgatacttccgctacttttaggcctggctaaattccaagacagataacactcaagatagataaagtacttgatctccaaactgacaaactgtttattttctagctgttattttgctatttggcatttacataaaagcacacgatgaagcaggtatcgccttacctgttgaaactgaaaataaagcttgtttatttccaaaaaaaaaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("CCDC74A");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 2, 132349413,
				PositionType.ZERO_BASED), "G", "A");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(null, annotation1.getAnnoLoc());
		Assert.assertEquals(null, annotation1.getCDSNTChange());
		Assert.assertEquals(null, annotation1.getProteinChange());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.INTERGENIC_VARIANT), annotation1.getEffects());
	}

	/**
	 * <P>
	 * annovar: AK025975(dist=42221),LOC729059(dist=47759) chr1:23289568T>C
	 * </P>
	 */
	@Test
	public void testRealWorldCase_uc001bgg_1() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory.parseKnownGenesLine(refDict,
				"uc001bgg.1	chr1	+	23243782	23247347	23243782	23243782	1	23243782,	23247347,		uc001bgg.1");
		this.builderForward
				.setSequence("ctgcctcactgaagcacaggaaggacccaggccccagaccatcacccaccccagggctgggccctgggccactcctggctcactccagggcccctgttgtttgaagatggtaccaaaggctggaagactcttgctagggaagataacgtaaatgcattcaaaagacagggtaccacatgatgctagggaaagtgcgtcatgaccgcagggtagcagcctgctcctgtcactagggatgtgaatctggggagacacttcccctcactgagccatctgtaaaatgagagcattggactgggtgaggcaggctgtgcaaaacacttatacaccttagtcccatgaggaaggagtattccccctgcatattttggggggaaactgaggctcagggggatgaagtactttgctcagggtaacacagccaggaaggagcagagcaaggccatgagctttggatttttctgtttccaatgtctcctctaccgtgctcaattagatgatgagaaacagttggtgtctgatgctggccaataagagaaaagaaagctcaagtgggctggggctgtcagggagggcttcatggagaggctgtcttgaaaatgacagaggtcaaaagaaaggtcactccaagacgcagctgccagaatggtccaacacagtgaggagttgtgtctggatgggccagtggaacgggggaagtgaaggttgatataagtggaggttgaggcagtttaggcgtgatgtacagtccagccacagcaggttcttgagcaggagggtagcatagtgagcatcaggttctaggaagaagcaccagttcagccatcagatggggcaggatgcctcccagctactcctctccccgagaaggaattgcccccggagcggccctcatttattccaggagaagccaggccctgctgcttcagtttccatcctcactgacgcagataatccaaagagtcacttcagctgcttggaacagcagggttgggtcttgtgctacctgattcttaatttggctgatgtctaccccatttcctccaggcttctctagaggtggcaggggtggggtggggagaaagctgccagacaccaaaggagctgagcctggcgggggcgggcgtgcgggtgggcatgagaaattgctcaggaaacccatccctgccctggtcaccctagtcctggagagatcacccagacatgttggaatgagtctggggtcctgtcttttccaataatgtgttatcctggggaaacccagagggaagttgagggagccccactgtgctgtgtgatgctacatacgcatgcgaatacaccaccgggtggccttgacccagccttctgcaaaatggcttctgccatcaaacttaaggtggagaatggtcttgcagctgccagtttttccttcagagaaactaaggtcagagtgttggctgcagaaaaggacttggcctttgtcacataacaggaaagagagtcaactccgggcacaccccctcggaggctaatctcaggaggttggaaccacagggattgcaataatcgctgaatacttaccatatgccaggtgccaaagcattatcctctccactttatgagtgaggaaactgattcccagaggtgattcccagaagattccaaccaggccatctgtctgtagtccttccttcccacctcctgggatttctcaaaatgtttgccatcgcccacctgtatcagaatctcctgggctatttattgtttaacttgcagactcctgggcgccaccccaaccccctaaatcagaatccctggggttgggcccaagagtctccattttttgacaagctcctccaggagattcctccagacatggaaatcctcacttgactcttcccccataacaaactgccctgatgtctgcagagagagggtctgctgtagacagggattaagaaaaagcaggacattttcagataggatgcccaaacccagacgcagattaggacccatgaagtagggggtagagctgaccctcaggaatgtgcccagacagagctaagctttcaggaaaattagaaagaccctcagacccagagtctgtctgagtttagcttcgcagcatgaaggctgccttgagaaagctgatataacaagaagcagcagaaatttaatactttggaggggcccagcaacccactcagggccaccctgggagaccccacagaccccaaggctatggtccagacctgttcactcaacacctggccaggcggggcctccagctggacctagagctacagatgccccaccaccactaccaagcccggctacccagaccaagacacggcttttcctggaggagagagagagcaaaggccctgtctctacccaagacctaacggcccctgttgaaggagagggagcaggagagggaggagggaggaggtcacagccaggaccacatacactcttgggggccctgctgagactgcaagagtcatgaattctaacgttccacaggtgaaagattccaagattctaggattgcaaaaccccatcattctaagattccaatggaatgattccatgtttctaagattccatcacactatgattctatgctgctaattcttgaaatttcgagtctcattgttcgttgctgttccccagacctgtgggccctagcatttttttaaaagcacaaaaaaaaaagagcaagaaagagagaaagagatggggagggagacagctgaaaacagatactaagaaaagctccatcccttggatctgagttacagatgcaccttggcaagggccaaagaccctccttttgagtggggtgcagaacgtctttccctggtagagtggcagaattgcatgcatcaggccttcctgggggtaaaaggggctggctgttccaggctacagctgagtaaagccccacacaggccacagtgcccactggctggtggacctaggaaccaagcagggccccactggctcagtctggggagggactcatctggggtggaatttccctccctgcagcaaggaagctgcagggccaggaatttgccttgggagacccccactgaggaatatttccgaagcagaaccccttcctattcagagccagagtcttaacactggacaaccacagggtgttgctgcaaactccagagccaggtgccttccctctgacatttgggacatagcttccatggccacacagccttcgcccccttccaagaccccccttgacctttctaatcttagtcactgccttccagagctgggaggccacacggcagaggtgcctgtgaatcactccgtcatcagcctggccgctttcccctctgtggagagggactctgatgggcagggggcaccaagttgagcctctgaggctggcccagcataggccaggcagggacattcactcacaaccagcctttttggactctagagaagagtaattaccacttggtcatgcccagcccccaaatcataatcatagctgctatttcttgagtgccagcaatgtgccaggcaccgtgcccaactttttatacacaat"
						.toUpperCase());
		this.builderForward.setGeneSymbol("AK025975");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 23289568,
				PositionType.ZERO_BASED), "T", "C");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(null, annotation1.getAnnoLoc());
		Assert.assertEquals(null, annotation1.getCDSNTChange());
		Assert.assertEquals(null, annotation1.getProteinChange());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.INTERGENIC_VARIANT), annotation1.getEffects());
	}

	// Various Intronic Variants

	/**
	 * An intron of PLEKHN1 gtgagtaagg atcctgcctc ctg [a] ggtgagtgcc tgttgcctcc cacaggctga cacatctctg ccttccctac cag
	 * Result hand-checked OK.
	 */
	@Test
	public void testRealWorldCase_uc001acf_3() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001acf.3	chr1	+	901876	910484	901911	909955	15	901876,902083,905656,905900,906065,906258,906456,906703,907454,907667,908240,908879,909212,909695,909821,	901994,902183,905803,905981,906138,906386,906588,906784,907530,907804,908390,909020,909431,909744,910484,	Q494U1-3	uc001acf.3");
		this.builderForward
				.setSequence("gggacccagacttgccgacctgtacgactctggccatggggaacagccactgtgtccctcaggcccccaggaggctccgggcctccttctccagaaagccctcgctgaagggaaacagagaggacagcgcgcggatgtcggccggcctgccgggccccgaggctgctcgaagcggggacgccgccgccaacaagctcttccactacatcccgggcacggacatcctggacctggagaaccagcgagaaaacctggagcagccattcctgagtgtgttcaagaaggggcggcggagggtgcctgtgaggaacctgggaaaagttgtgcattacgccaaggtccagctgcggttccagcacagccaggatgtcagcgactgctacctggagctattccccgcccacctgtacttccaggcccacggctcggaaggactcacatttcaggggctgttaccgctgacggagctgagtgtctgcccgctcgaggggtcccgagagcacgccttccagatcacaggcccactgcccgcacccctcctggtgctctgccccagccgggccgagctggaccgctggctttaccacctggagaagcagacggccctcctcggggggccgcggcgctgccactcggcacccccacaggggtcctgcggagacgaactcccctggactttgcagcgccgtctaacccggctgcggacggcgtcagggcacgaacccggcggcagtgctgtctgtgcctcgagggtcaagctgcagcacctgcccgcacaggagcagtgggaccggctcttggtcctgtacccaacgtccttggccattttctccgaggagctggacgggctttgcttcaagggggagctcccactccgtgccgtccacatcaacctggaggagaaggagaagcagatccgctccttcctgattgaaggccccctcatcaacaccatccgcgtggtgtgcgccagctacgaggactacggtcactggctgctgtgccttcgcgctgtcacccacagggagggggccccgccgctgcctggtgccgagagcttcccagggtcgcaggttatgggcagtggccgaggctcactctcctcaggcggacagaccagctgggactcggggtgcttggcgcccccctccacccgcaccagccactccctgcctgagtcctcagtgccatccaccgtgggctgctcctcccagcacacaccgctgcacaggctgagcctggagagcagcccagatgcccctgaccacacttcggaaacatcacactcgcccctctatgccgacccctacacaccacccgccacctcccaccgcagggtcacagatgtccggggcctggaggagttcctcagtgccatgcagagtgcacgtggacccacgccctcgagcccactcccctcggtgcctgtgtctgtgcctgcctctgaccctcgctcctgctcctccggccccgctggcccctacttgctctccaagaagggagccctgcagtccagagccgctcagagacaccggggctcagccaaggatggggggccgcagcccccagacgcccctcagcttgtctcctctgccagggaaggttcgcccgaaccctggctgcctctgacagatggtcggtcccccaggaggagccgggaccccggctacgaccacctctgggacgagactttgtcttcctcccaccagaagtgcccccagcttggagggcctgaggccagtggggggcttgtgcagtggatctgatggccgcggtgaggtgggttctcaggaccaccctcgccaagctccagggtacctgcccctctaacccacttcaaattacaagtcagggtctgaacccagtgtgatggggggagtctctggggccctgagttcagagcccgtccctcagctcctgttccttggtgccagcagctggggcagggaagggtgggaggggccccatccaaaggatgccctggccagcgaggctgggtcacaggtcagggaggtcctggccgtccacagggtcggccctcagctcagcccgccaggagtcagggaggagactcgctgggagtgggagggcagcacgggcgtgaaggtcggaggacagagaaaggtcagcagggtcagagtatgtgaggtcagagggcatgagggtcacaggtcagcaaggtgtgaggagcacaagccagggtgccccgaggaggagggtgggtgggtccttgtgtggcctggcgcgcaccacagggcagcacgggagacgttgacaccaccggacgagaaagaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("(EKHN1)");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 909767,
				PositionType.ZERO_BASED), "A", "G");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(13, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("1597+24A>G", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.CODING_TRANSCRIPT_INTRON_VARIANT),
				annotation1.getEffects());
	}

	/**
	 * An intron of C12orf54<br>
	 * 5'UTR <br>
	 * closer to left exon<br>
	 * '+'-strand
	 */
	@Test
	public void testRealWorldCase_uc001rrr_3_first() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001rrr.3	chr12	+	48876285	48890297	48877079	48888722	9	48876285,48877022,48879960,48880470,48882706,48884594,48886729,48888580,48889963,	48876359,48877144,48879991,48880509,48882739,48884619,48886778,48888762,48890297,	Q6X4T0	uc001rrr.3");
		this.builderForward
				.setSequence("gagatggggctcaaataggaaaaagtctaggggtgaaggagtggggcaaagtgtaatttggggaaccaagcaggggcctggagctatctccatcttcagctccagagtccttggtttctgtctgagaacaaatggcacagcatccctgccaggatcaggaacaaaaggtagaaatgacctccaagcagcagagaagcacatccatagaagagacaatgagaccacaggaaaaacaggtaaccatcactgaaaccctgtgggaccaggtgctgacagtttttaaggatatacaaaaggagctgcaggaagatgctcggattcgagggatgagcaactgctccatgacacccatgacatcagcacccaggactggaagcataaggcctccagattccttgatgaccccaaagttgagaagattgcagttcagctctggagagcagccatcaggaggccgtatccacaacctgaagacacagctcttcagtcaatcagcttactaccctggaccctaactctacaatcaaggaagaaggacatctctgcttccgccagcacagcttcagttggggaagatattagcagacatcatcactgaacccagaagagagggtggtgcccatgagtggagatgccagggtctatggctgaaactgggaacttggaaatcaagtgagacccaagcaaggaaaccaactgccaaagcaaggaaaccagcttggtttggcaaacagctgatgaaataaatgtgacgtagaagacttgccttcctggttcttcctgggctgtggaatgggtagtgatagaatttccaagtatgatagtgcatttggttcaaagaacagcactgtagcatgggagaacctgcactataatgtcataaactcaaaaaaaaaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("C12orf54");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 12, 48876499,
				PositionType.ZERO_BASED), "C", "T");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(0, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("-58+141C>T", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FIVE_PRIME_UTR_VARIANT), annotation1.getEffects());
	}

	/**
	 * An intron of C12orf54<br>
	 * 5'UTR <br>
	 * closer to right exon<br>
	 * '+'-strand
	 */
	@Test
	public void testRealWorldCase_uc001rrr_3_second() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001rrr.3	chr12	+	48876285	48890297	48877079	48888722	9	48876285,48877022,48879960,48880470,48882706,48884594,48886729,48888580,48889963,	48876359,48877144,48879991,48880509,48882739,48884619,48886778,48888762,48890297,	Q6X4T0	uc001rrr.3");
		this.builderForward
				.setSequence("gagatggggctcaaataggaaaaagtctaggggtgaaggagtggggcaaagtgtaatttggggaaccaagcaggggcctggagctatctccatcttcagctccagagtccttggtttctgtctgagaacaaatggcacagcatccctgccaggatcaggaacaaaaggtagaaatgacctccaagcagcagagaagcacatccatagaagagacaatgagaccacaggaaaaacaggtaaccatcactgaaaccctgtgggaccaggtgctgacagtttttaaggatatacaaaaggagctgcaggaagatgctcggattcgagggatgagcaactgctccatgacacccatgacatcagcacccaggactggaagcataaggcctccagattccttgatgaccccaaagttgagaagattgcagttcagctctggagagcagccatcaggaggccgtatccacaacctgaagacacagctcttcagtcaatcagcttactaccctggaccctaactctacaatcaaggaagaaggacatctctgcttccgccagcacagcttcagttggggaagatattagcagacatcatcactgaacccagaagagagggtggtgcccatgagtggagatgccagggtctatggctgaaactgggaacttggaaatcaagtgagacccaagcaaggaaaccaactgccaaagcaaggaaaccagcttggtttggcaaacagctgatgaaataaatgtgacgtagaagacttgccttcctggttcttcctgggctgtggaatgggtagtgatagaatttccaagtatgatagtgcatttggttcaaagaacagcactgtagcatgggagaacctgcactataatgtcataaactcaaaaaaaaaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("C12orf54");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 12, 48876999,
				PositionType.ZERO_BASED), "C", "T");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(0, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("-57-23C>T", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FIVE_PRIME_UTR_VARIANT), annotation1.getEffects());
	}

	/**
	 * An intron of C12orf54<br>
	 * 3'UTR <br>
	 * closer to left exon<br>
	 * '+'-strand
	 */

	@Test
	public void testRealWorldCase_uc001rrr_3_third() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001rrr.3	chr12	+	48876285	48890297	48877079	48888722	9	48876285,48877022,48879960,48880470,48882706,48884594,48886729,48888580,48889963,	48876359,48877144,48879991,48880509,48882739,48884619,48886778,48888762,48890297,	Q6X4T0	uc001rrr.3");
		this.builderForward
				.setSequence("gagatggggctcaaataggaaaaagtctaggggtgaaggagtggggcaaagtgtaatttggggaaccaagcaggggcctggagctatctccatcttcagctccagagtccttggtttctgtctgagaacaaatggcacagcatccctgccaggatcaggaacaaaaggtagaaatgacctccaagcagcagagaagcacatccatagaagagacaatgagaccacaggaaaaacaggtaaccatcactgaaaccctgtgggaccaggtgctgacagtttttaaggatatacaaaaggagctgcaggaagatgctcggattcgagggatgagcaactgctccatgacacccatgacatcagcacccaggactggaagcataaggcctccagattccttgatgaccccaaagttgagaagattgcagttcagctctggagagcagccatcaggaggccgtatccacaacctgaagacacagctcttcagtcaatcagcttactaccctggaccctaactctacaatcaaggaagaaggacatctctgcttccgccagcacagcttcagttggggaagatattagcagacatcatcactgaacccagaagagagggtggtgcccatgagtggagatgccagggtctatggctgaaactgggaacttggaaatcaagtgagacccaagcaaggaaaccaactgccaaagcaaggaaaccagcttggtttggcaaacagctgatgaaataaatgtgacgtagaagacttgccttcctggttcttcctgggctgtggaatgggtagtgatagaatttccaagtatgatagtgcatttggttcaaagaacagcactgtagcatgggagaacctgcactataatgtcataaactcaaaaaaaaaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("(EKHN1)");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 12, 48888799,
				PositionType.ZERO_BASED), "C", "T");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(7, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("*40+38C>T", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.THREE_PRIME_UTR_VARIANT), annotation1.getEffects());
	}

	/**
	 * An intron of C12orf54<br>
	 * 3'UTR <br>
	 * closer to right exon<br>
	 * '+'-strand
	 */
	@Test
	public void testRealWorldCase_uc001rrr_3_fourth() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001rrr.3	chr12	+	48876285	48890297	48877079	48888722	9	48876285,48877022,48879960,48880470,48882706,48884594,48886729,48888580,48889963,	48876359,48877144,48879991,48880509,48882739,48884619,48886778,48888762,48890297,	Q6X4T0	uc001rrr.3");
		this.builderForward
				.setSequence("gagatggggctcaaataggaaaaagtctaggggtgaaggagtggggcaaagtgtaatttggggaaccaagcaggggcctggagctatctccatcttcagctccagagtccttggtttctgtctgagaacaaatggcacagcatccctgccaggatcaggaacaaaaggtagaaatgacctccaagcagcagagaagcacatccatagaagagacaatgagaccacaggaaaaacaggtaaccatcactgaaaccctgtgggaccaggtgctgacagtttttaaggatatacaaaaggagctgcaggaagatgctcggattcgagggatgagcaactgctccatgacacccatgacatcagcacccaggactggaagcataaggcctccagattccttgatgaccccaaagttgagaagattgcagttcagctctggagagcagccatcaggaggccgtatccacaacctgaagacacagctcttcagtcaatcagcttactaccctggaccctaactctacaatcaaggaagaaggacatctctgcttccgccagcacagcttcagttggggaagatattagcagacatcatcactgaacccagaagagagggtggtgcccatgagtggagatgccagggtctatggctgaaactgggaacttggaaatcaagtgagacccaagcaaggaaaccaactgccaaagcaaggaaaccagcttggtttggcaaacagctgatgaaataaatgtgacgtagaagacttgccttcctggttcttcctgggctgtggaatgggtagtgatagaatttccaagtatgatagtgcatttggttcaaagaacagcactgtagcatgggagaacctgcactataatgtcataaactcaaaaaaaaaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("C12orf54");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 12, 48889799,
				PositionType.ZERO_BASED), "T", "C");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(7, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("*41-164T>C", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.THREE_PRIME_UTR_VARIANT), annotation1.getEffects());
	}

	/**
	 * An intron of C12orf54<br>
	 * CDS <br>
	 * closer to left exon<br>
	 * '+'-strand
	 */
	@Test
	public void testRealWorldCase_uc001rrr_3_fifth() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001rrr.3	chr12	+	48876285	48890297	48877079	48888722	9	48876285,48877022,48879960,48880470,48882706,48884594,48886729,48888580,48889963,	48876359,48877144,48879991,48880509,48882739,48884619,48886778,48888762,48890297,	Q6X4T0	uc001rrr.3");
		this.builderForward
				.setSequence("gagatggggctcaaataggaaaaagtctaggggtgaaggagtggggcaaagtgtaatttggggaaccaagcaggggcctggagctatctccatcttcagctccagagtccttggtttctgtctgagaacaaatggcacagcatccctgccaggatcaggaacaaaaggtagaaatgacctccaagcagcagagaagcacatccatagaagagacaatgagaccacaggaaaaacaggtaaccatcactgaaaccctgtgggaccaggtgctgacagtttttaaggatatacaaaaggagctgcaggaagatgctcggattcgagggatgagcaactgctccatgacacccatgacatcagcacccaggactggaagcataaggcctccagattccttgatgaccccaaagttgagaagattgcagttcagctctggagagcagccatcaggaggccgtatccacaacctgaagacacagctcttcagtcaatcagcttactaccctggaccctaactctacaatcaaggaagaaggacatctctgcttccgccagcacagcttcagttggggaagatattagcagacatcatcactgaacccagaagagagggtggtgcccatgagtggagatgccagggtctatggctgaaactgggaacttggaaatcaagtgagacccaagcaaggaaaccaactgccaaagcaaggaaaccagcttggtttggcaaacagctgatgaaataaatgtgacgtagaagacttgccttcctggttcttcctgggctgtggaatgggtagtgatagaatttccaagtatgatagtgcatttggttcaaagaacagcactgtagcatgggagaacctgcactataatgtcataaactcaaaaaaaaaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("C12orf54");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 12, 48880599,
				PositionType.ZERO_BASED), "T", "C");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(3, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("135+91T>C", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.CODING_TRANSCRIPT_INTRON_VARIANT),
				annotation1.getEffects());
	}

	/**
	 * An intron of C12orf54<br>
	 * CDS <br>
	 * closer to right exon<br>
	 * '+'-strand
	 */
	@Test
	public void testRealWorldCase_uc001rrr_3_sixth() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001rrr.3	chr12	+	48876285	48890297	48877079	48888722	9	48876285,48877022,48879960,48880470,48882706,48884594,48886729,48888580,48889963,	48876359,48877144,48879991,48880509,48882739,48884619,48886778,48888762,48890297,	Q6X4T0	uc001rrr.3");
		this.builderForward
				.setSequence("gagatggggctcaaataggaaaaagtctaggggtgaaggagtggggcaaagtgtaatttggggaaccaagcaggggcctggagctatctccatcttcagctccagagtccttggtttctgtctgagaacaaatggcacagcatccctgccaggatcaggaacaaaaggtagaaatgacctccaagcagcagagaagcacatccatagaagagacaatgagaccacaggaaaaacaggtaaccatcactgaaaccctgtgggaccaggtgctgacagtttttaaggatatacaaaaggagctgcaggaagatgctcggattcgagggatgagcaactgctccatgacacccatgacatcagcacccaggactggaagcataaggcctccagattccttgatgaccccaaagttgagaagattgcagttcagctctggagagcagccatcaggaggccgtatccacaacctgaagacacagctcttcagtcaatcagcttactaccctggaccctaactctacaatcaaggaagaaggacatctctgcttccgccagcacagcttcagttggggaagatattagcagacatcatcactgaacccagaagagagggtggtgcccatgagtggagatgccagggtctatggctgaaactgggaacttggaaatcaagtgagacccaagcaaggaaaccaactgccaaagcaaggaaaccagcttggtttggcaaacagctgatgaaataaatgtgacgtagaagacttgccttcctggttcttcctgggctgtggaatgggtagtgatagaatttccaagtatgatagtgcatttggttcaaagaacagcactgtagcatgggagaacctgcactataatgtcataaactcaaaaaaaaaaaaaaaaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("C12orf54");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 12, 48882699,
				PositionType.ZERO_BASED), "C", "T");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(3, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("136-7C>T", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("?", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.CODING_TRANSCRIPT_INTRON_VARIANT,
				VariantEffect.SPLICE_REGION_VARIANT), annotation1.getEffects());
	}

	/**
	 * An intron of TAF1A<br>
	 * 5'UTR <br>
	 * closer to trailing exon<br>
	 * '-'-strand
	 */
	@Test
	public void testRealWorldCase_uc001hni_2_first() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001hni.2	chr1	-	222731243	222763275	222732001	222753163	11	222731243,222731964,222734705,222736514,222737400,222742851,222743876,222750786,222753100,222761784,222763068,	222731610,222732114,222734860,222736638,222737467,222743010,222744007,222750985,222753214,222761907,222763275,	NP_647603	uc001hni.2");
		this.builderForward.setSequence("SEQ".toUpperCase());
		this.builderForward.setGeneSymbol("TAF1A");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 222761999,
				PositionType.ZERO_BASED), "A", "G");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(0, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("-174-93T>C", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FIVE_PRIME_UTR_VARIANT), annotation1.getEffects());
	}

	/**
	 * An intron of TAF1A<br>
	 * 5'UTR <br>
	 * closer to leading exon<br>
	 * '-'-strand
	 */
	@Test
	public void testRealWorldCase_uc001hni_2_second() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001hni.2	chr1	-	222731243	222763275	222732001	222753163	11	222731243,222731964,222734705,222736514,222737400,222742851,222743876,222750786,222753100,222761784,222763068,	222731610,222732114,222734860,222736638,222737467,222743010,222744007,222750985,222753214,222761907,222763275,	NP_647603	uc001hni.2");
		this.builderForward.setSequence("SEQ".toUpperCase());
		this.builderForward.setGeneSymbol("TAF1A");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 222762999,
				PositionType.ZERO_BASED), "T", "C");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(0, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("-175+69A>G", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.FIVE_PRIME_UTR_VARIANT), annotation1.getEffects());
	}

	/**
	 * An intron of TAF1A<br>
	 * 3'UTR <br>
	 * closer to trailing exon<br>
	 * '-'-strand
	 */
	@Test
	public void testRealWorldCase_uc009xdy_1_first() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc009xdy.1	chr1	-	222731243	222737640	222732001	222737434	5	222731243,222731964,222734705,222736514,222737400,	222731610,222732114,222734860,222736638,222737640,	NP_647603	uc009xdy.1");
		this.builderForward
				.setSequence("atgcttttctatacttagattgaaactagataaagagccagggtatttttaaaatacttaataaatttaaagggagcatattagacagtaagaactttattttgaaagtaagctgaaatttgagaaatgcttttacaagtatgtaaagattacaagactgttctttctttcagattttgtatcagattgtaccatctcataaattgatgttggaattccatacattacttagaaaatcagaaaaagaagaacaccgtaaactggggttggaggtattatttggagtcttagattttgccggatgcactaagaatataactgcttggaaatacttggcaaaatatctgaaaaatatcttaatgggaaaccaccttgcgtgggttcaagaagagtggaactccaggaaaaactggtggccaggctttcatttcagctacttttgggcaaaaagtgattggaaggaagatacagctttggcctgtgagaaagcttttgtggctggtttactgttaggaaaaggttgtagatatttccggtatattttaaagcaagatcaccaaatcttagggaagaaaattaagcggatgaagagatctgtgaaaaaatacagtattgtaaatccaagactctgatactgaattttagttatttcacagttgtagctacacagtataccaccatgaagaaatatattggtgatgagttctattgaggaattttgaaaagagagaaggatttagaaaaaagactctttctcggccgggcgcagtggctcacacttctaatcccagcacttgggaggccgaggtgggtggatcatgaggtcaggagttcaagaccagcctggccaacacagtgaaaccctgtctctactaaaaatacaaaaagtagctgggcgcagtggcgggcatttgtaatcccagatactcgggaggctgaagcaggagaattgcttgaacccgggaggtggaggttgcagtgagccgagattgcaccaccgtactcctgcctgggcgacagaactagactctgtctc"
						.toUpperCase());
		this.builderForward.setGeneSymbol("TAF1A");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 222731699,
				PositionType.ZERO_BASED), "A", "G");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(3, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("*38-90T>C", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.THREE_PRIME_UTR_VARIANT), annotation1.getEffects());
	}

	/**
	 * An intron of TAF1A<br>
	 * 3'UTR <br>
	 * closer to leading exon<br>
	 * '-'-strand
	 */
	@Test
	public void testRealWorldCase_uc009xdy_1_second() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc009xdy.1	chr1	-	222731243	222737640	222732001	222737434	5	222731243,222731964,222734705,222736514,222737400,	222731610,222732114,222734860,222736638,222737640,	NP_647603	uc009xdy.1");
		this.builderForward
				.setSequence("atgcttttctatacttagattgaaactagataaagagccagggtatttttaaaatacttaataaatttaaagggagcatattagacagtaagaactttattttgaaagtaagctgaaatttgagaaatgcttttacaagtatgtaaagattacaagactgttctttctttcagattttgtatcagattgtaccatctcataaattgatgttggaattccatacattacttagaaaatcagaaaaagaagaacaccgtaaactggggttggaggtattatttggagtcttagattttgccggatgcactaagaatataactgcttggaaatacttggcaaaatatctgaaaaatatcttaatgggaaaccaccttgcgtgggttcaagaagagtggaactccaggaaaaactggtggccaggctttcatttcagctacttttgggcaaaaagtgattggaaggaagatacagctttggcctgtgagaaagcttttgtggctggtttactgttaggaaaaggttgtagatatttccggtatattttaaagcaagatcaccaaatcttagggaagaaaattaagcggatgaagagatctgtgaaaaaatacagtattgtaaatccaagactctgatactgaattttagttatttcacagttgtagctacacagtataccaccatgaagaaatatattggtgatgagttctattgaggaattttgaaaagagagaaggatttagaaaaaagactctttctcggccgggcgcagtggctcacacttctaatcccagcacttgggaggccgaggtgggtggatcatgaggtcaggagttcaagaccagcctggccaacacagtgaaaccctgtctctactaaaaatacaaaaagtagctgggcgcagtggcgggcatttgtaatcccagatactcgggaggctgaagcaggagaattgcttgaacccgggaggtggaggttgcagtgagccgagattgcaccaccgtactcctgcctgggcgacagaactagactctgtctc"
						.toUpperCase());
		this.builderForward.setGeneSymbol("TAF1A");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 222731899,
				PositionType.ZERO_BASED), "T", "C");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(3, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("*37+65A>G", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.THREE_PRIME_UTR_VARIANT), annotation1.getEffects());
	}

	/**
	 * An intron of TAF1A<br>
	 * CDS <br>
	 * closer to trailing exon<br>
	 * '-'-strand
	 */
	@Test
	public void testRealWorldCase_uc001hni_2_third() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001hni.2	chr1	-	222731243	222763275	222732001	222753163	11	222731243,222731964,222734705,222736514,222737400,222742851,222743876,222750786,222753100,222761784,222763068,	222731610,222732114,222734860,222736638,222737467,222743010,222744007,222750985,222753214,222761907,222763275,	NP_647603	uc001hni.2");
		this.builderForward.setSequence("SEQ".toUpperCase());
		this.builderForward.setGeneSymbol("TAF1A");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 222736699,
				PositionType.ZERO_BASED), "A", "G");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(6, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("620-62T>C", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.CODING_TRANSCRIPT_INTRON_VARIANT),
				annotation1.getEffects());
	}

	/**
	 * An intron of TAF1A<br>
	 * CDS <br>
	 * closer to leading exon<br>
	 * '-'-strand
	 */
	@Test
	public void testRealWorldCase_uc001hni_2_fourth() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001hni.2	chr1	-	222731243	222763275	222732001	222753163	11	222731243,222731964,222734705,222736514,222737400,222742851,222743876,222750786,222753100,222761784,222763068,	222731610,222732114,222734860,222736638,222737467,222743010,222744007,222750985,222753214,222761907,222763275,	NP_647603	uc001hni.2");
		this.builderForward.setSequence("SEQ".toUpperCase());
		this.builderForward.setGeneSymbol("TAF1A");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 222737199,
				PositionType.ZERO_BASED), "A", "G");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(6, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("619+201T>C", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(=)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.CODING_TRANSCRIPT_INTRON_VARIANT),
				annotation1.getEffects());
	}

	/**
	 * An intron of BC052952<br>
	 * non-coding <br>
	 * closer to leading exon<br>
	 * '+'-strand
	 */
	@Test
	public void testRealWorldCase_uc002wvf_3_first() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc002wvf.3	chr20	+	25936452	25949924	25936452	25936452	4	25936452,25936721,25943697,25945638,	25936505,25936846,25943832,25949924,		uc002wvf.3");
		this.builderForward
				.setSequence("ccatacttcaaccactcttacactactgagtgttcaaactgtgctcaactaagatggtgtcagaagtaggatctgaagaagagacttaacaatccccaggagtgctgagtgaacaagcaaggtacctgcaaggacctacttttgtcccttgatctctcaaagtggctggggatcatggatggatacatgtgaccaagcattgcaagttaaagatatttctgaatcttggacctcttatttacagtgaagccccctcatgcatagtaaagcagagaggaatgtaaaaggttgctgcatggataatcaatttctgatggagtctcactctgttgcccaagctggagtgcaacagcatgatcttggcttactgcaacctctgcctcccgggttcaagcgattctgctgcctcagcctcctgaatagctggaattacaggcacccaccaccatacccagctaattttttgtatttttagtagagacggggtttcaccgtgttagccaggatggtcttaatctgctgacctcttgatccacccaccttgccctcccaaagtgcttagattactggcatgagccattgcaccaggtcttatttatctttttgagacaggttttagctctgtcactcaggctggagtgcagtggtgcaatgatggctcaatgcagcaccaacctccagggctcaagtaaccctcccacctcagcttcctgagtagctgggaccacaagcgtgtgtcaccatacctgacacatttcttagacctcagaaaagatgctcaacaccataagtcactaggaatattcagtattcaaatagaggcttcaatgggctccgaactatcccctcctgaactctataaaaagcgtgtttccaaccggctgaatgaaaacaaaggtttacatctgtgagatgaattcacacatcagaaagcattttcaccaagaacttttctagttttatagcaagatgttcattttttcaatattggtctcaatgggctccaaaatgtccctttgtacagtctacaaaaatttttgccaaagtactgaatcaaaacaaatgtttaacactgtgagatgaattttcaagtcaccaaacatttttacagatagtttgtatctactttttaatcacaaaacttagaattgttcactataagcctcaaagggctctaaattgtcccccttagcttctactaaaagagagttttgaacctagtgtgtagaaacaaaagtttaagtctgtgaaataaatccccacattgcaaagctatttcacagatagcttgcttctagtttttatcatgaattattcaacttttcactacaggcctcaatgggctctgaaatgtcccttcatacattctacaaaaagagtgtttccaacttagctgaatcaaaaggaagtttaaactgtatgagatgaaaccgcaaattgcaaagcattttcacatatagcttctatagtttctatcatataatattcagtttttcactgtaggcctccatgggctatgaaatgtcccctcataggttctacagaaaggtgtttccaacatgctgaatgaaaacaaagatttaactttgtgagatgaacccacacatcacaaaacatttttatggatggattttctctagtttttattgcagaatatttgatttcatgctgtaggtctcaattttctccaaaatttccccttgtagattctacaaaaagaatatttccaatctgctgaatcaaaaccgatgttcaattctgtgagatgatatgcaaagctttttcacagatagcctgtttctagtttttattgtgggatatttggtttttcactataagcatcagtgggctctgaaattgtccctttgtagattctacaaaaagagggtttccaacctgttgaatcaaaataaagttttaactttgtaagatgaacccacacatcgcaaagcattttcacccatagcttgtttctaatttttaccatgggttattggtttttcactaaatgcctcagtggactaggaattgttgccttgtagattgtacaaccaaagagtttccaacctgctgaagcaaaacacaggtttaactttgtaagatgaatctacacattgcaaagcatttttacagataggttgctttgagtttttatcaagagatatttgatttttcacagtatgcttcaatgggttatgaaatctccccttgtagattctacaaaaaaagtttccaaactgatgaatcaaaagaaaagttcaacttgtgagatgaaatcacacattacaaagtattttcagaaatagcttctttatagtatttctcgcaggatattagattttccaatattagcctcaacgagctctgaaatgtccctttgtagattgtacaaaatgagtgtttccaacctatggaataaaaaggaaggtttaactctgtgagataaatccacacatcacaaagcatcttcacagatggcttgtttctaatttttattgtgggatattcggtttttcactaaaggcctcaagcactccgaaatgtccttttgtagattctataacaagagtgttttaaaactgctgaatcaaaacaaacgtgtaactctttgagaggaacccacacatcacaaagcatattttcagatcacttgtttctagtttttaaagcatgatattcggtttttcactatatgcctcatcagactcagaaatgtctcatagtagattttacaaaaacagtgtttcctacctgctgaatcaaaacaacagtttaaatctgtgagatgactccaaacctcgcaaagcattttcacagatagttttttcctagtttttttcgcaggatatttggtttttcagtataggcctcaatgaactctgaaatgtccattcataggttctataaaagtgtgttcccaactccctgtataatagcaaaggtttaactctgtgaaatgaatctatacattggaaaacattttcacaaatagcttgttcctagcttttatgatgagacatttgattttttactattggcctcaatgggttcagaaatgtctctttgcagattctacaaaaaaaaaagtttttcatttttctgaacaaaaataaaggtttaactctttgatatgaaatcagacatcacaaagaattttcacacatagcttgtttctagggttcatcactggatattgtgtttttcattataggcttcaaaggagtcttaattgtaacttcctaattctacaaaaagagtgtcccccaacctgctgtatgtaaataaaggtttacctctgtgagataaatcttcacattgcaaagcattttccccgatagcttgtttctagtttttatatcgggatattgatttttcactataggcttcaatgggctttgaaagattcctctgtagattgtacaaaaagagtgttttaacctgctgaatcaaaacaacgtttaactttttgagatacatccacacatcacaaagtattttcacggataggtttttattttattttattttattttttttagatggagtctcactcagtcacccaggctggagtgcagtggcacgatcttggcttcccgggttcatgccattctcctgcctcagcctcccaagtagctgagactacaggtgtctgccaccaggccaggctaattttttgtgtttttagtagagatggggtttcaccatgttagccaggatggtctcgatctcctgacctcgtgatccacccgcctcagcctttcaaagtgctgggactacaggcgtgagccaccatgcctggccaatagcttttttttacttttattgtggaatattggtttttcactacaggctgatattggcttcaaattgtaccctcacagattctacaaacagtgaattttcaagcttctgaagcaaaataaaggtttaaccttgtaagatgaatctgcacattattgctggatattcggtttttcaccgtaggcatcgattttctctgaaatgtcctattgaagattttgccaaaagagtgtttctaccctgctgaatcaaaagaatggtttaattctgtgagatgaaatcacccatcgtaaattattttcagagacatcttcatatagtttttacctcagaatattcggtttttcactataagcctcaatgggctttgaaatgtctcccataaattgtacaaaaaaaaaaaggaatttccaacctgttgaatcaaaacaaatatttaatgttgtgagataaattcaaatatcgcaaagcattttcacagattttttttctactttttatcacggaatatttggtttttcactatagtcctcagtgggctcagaaatgccccttgtagattctacaaaaagaatgtttctaacctgctgaaccaaaacaatactttaactctttgaggtgaatccacacattgcaaacattttcacagatagcttgtttttagttttgatcacgtaatattggatgttttacaataggcctcaacgggctcagaattgtcccctcttagattctactaaaagagtgtttccaaaatgctgtgtaaaaataaaggcttaacactgtgagatgaatcga"
						.toUpperCase());
		this.builderForward.setGeneSymbol("BC052952");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 20, 25943999,
				PositionType.ZERO_BASED), "C", "A");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(2, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("313+168C>A", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals(null, annotation1.getProteinChange());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.NON_CODING_TRANSCRIPT_INTRON_VARIANT),
				annotation1.getEffects());
	}

	/**
	 * An intron of BC052952<br>
	 * non-coding <br>
	 * closer to trailing exon<br>
	 * '+'-strand
	 */
	@Test
	public void testRealWorldCase_uc002wvf_3_second() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc002wvf.3	chr20	+	25936452	25949924	25936452	25936452	4	25936452,25936721,25943697,25945638,	25936505,25936846,25943832,25949924,		uc002wvf.3");
		this.builderForward
				.setSequence("ccatacttcaaccactcttacactactgagtgttcaaactgtgctcaactaagatggtgtcagaagtaggatctgaagaagagacttaacaatccccaggagtgctgagtgaacaagcaaggtacctgcaaggacctacttttgtcccttgatctctcaaagtggctggggatcatggatggatacatgtgaccaagcattgcaagttaaagatatttctgaatcttggacctcttatttacagtgaagccccctcatgcatagtaaagcagagaggaatgtaaaaggttgctgcatggataatcaatttctgatggagtctcactctgttgcccaagctggagtgcaacagcatgatcttggcttactgcaacctctgcctcccgggttcaagcgattctgctgcctcagcctcctgaatagctggaattacaggcacccaccaccatacccagctaattttttgtatttttagtagagacggggtttcaccgtgttagccaggatggtcttaatctgctgacctcttgatccacccaccttgccctcccaaagtgcttagattactggcatgagccattgcaccaggtcttatttatctttttgagacaggttttagctctgtcactcaggctggagtgcagtggtgcaatgatggctcaatgcagcaccaacctccagggctcaagtaaccctcccacctcagcttcctgagtagctgggaccacaagcgtgtgtcaccatacctgacacatttcttagacctcagaaaagatgctcaacaccataagtcactaggaatattcagtattcaaatagaggcttcaatgggctccgaactatcccctcctgaactctataaaaagcgtgtttccaaccggctgaatgaaaacaaaggtttacatctgtgagatgaattcacacatcagaaagcattttcaccaagaacttttctagttttatagcaagatgttcattttttcaatattggtctcaatgggctccaaaatgtccctttgtacagtctacaaaaatttttgccaaagtactgaatcaaaacaaatgtttaacactgtgagatgaattttcaagtcaccaaacatttttacagatagtttgtatctactttttaatcacaaaacttagaattgttcactataagcctcaaagggctctaaattgtcccccttagcttctactaaaagagagttttgaacctagtgtgtagaaacaaaagtttaagtctgtgaaataaatccccacattgcaaagctatttcacagatagcttgcttctagtttttatcatgaattattcaacttttcactacaggcctcaatgggctctgaaatgtcccttcatacattctacaaaaagagtgtttccaacttagctgaatcaaaaggaagtttaaactgtatgagatgaaaccgcaaattgcaaagcattttcacatatagcttctatagtttctatcatataatattcagtttttcactgtaggcctccatgggctatgaaatgtcccctcataggttctacagaaaggtgtttccaacatgctgaatgaaaacaaagatttaactttgtgagatgaacccacacatcacaaaacatttttatggatggattttctctagtttttattgcagaatatttgatttcatgctgtaggtctcaattttctccaaaatttccccttgtagattctacaaaaagaatatttccaatctgctgaatcaaaaccgatgttcaattctgtgagatgatatgcaaagctttttcacagatagcctgtttctagtttttattgtgggatatttggtttttcactataagcatcagtgggctctgaaattgtccctttgtagattctacaaaaagagggtttccaacctgttgaatcaaaataaagttttaactttgtaagatgaacccacacatcgcaaagcattttcacccatagcttgtttctaatttttaccatgggttattggtttttcactaaatgcctcagtggactaggaattgttgccttgtagattgtacaaccaaagagtttccaacctgctgaagcaaaacacaggtttaactttgtaagatgaatctacacattgcaaagcatttttacagataggttgctttgagtttttatcaagagatatttgatttttcacagtatgcttcaatgggttatgaaatctccccttgtagattctacaaaaaaagtttccaaactgatgaatcaaaagaaaagttcaacttgtgagatgaaatcacacattacaaagtattttcagaaatagcttctttatagtatttctcgcaggatattagattttccaatattagcctcaacgagctctgaaatgtccctttgtagattgtacaaaatgagtgtttccaacctatggaataaaaaggaaggtttaactctgtgagataaatccacacatcacaaagcatcttcacagatggcttgtttctaatttttattgtgggatattcggtttttcactaaaggcctcaagcactccgaaatgtccttttgtagattctataacaagagtgttttaaaactgctgaatcaaaacaaacgtgtaactctttgagaggaacccacacatcacaaagcatattttcagatcacttgtttctagtttttaaagcatgatattcggtttttcactatatgcctcatcagactcagaaatgtctcatagtagattttacaaaaacagtgtttcctacctgctgaatcaaaacaacagtttaaatctgtgagatgactccaaacctcgcaaagcattttcacagatagttttttcctagtttttttcgcaggatatttggtttttcagtataggcctcaatgaactctgaaatgtccattcataggttctataaaagtgtgttcccaactccctgtataatagcaaaggtttaactctgtgaaatgaatctatacattggaaaacattttcacaaatagcttgttcctagcttttatgatgagacatttgattttttactattggcctcaatgggttcagaaatgtctctttgcagattctacaaaaaaaaaagtttttcatttttctgaacaaaaataaaggtttaactctttgatatgaaatcagacatcacaaagaattttcacacatagcttgtttctagggttcatcactggatattgtgtttttcattataggcttcaaaggagtcttaattgtaacttcctaattctacaaaaagagtgtcccccaacctgctgtatgtaaataaaggtttacctctgtgagataaatcttcacattgcaaagcattttccccgatagcttgtttctagtttttatatcgggatattgatttttcactataggcttcaatgggctttgaaagattcctctgtagattgtacaaaaagagtgttttaacctgctgaatcaaaacaacgtttaactttttgagatacatccacacatcacaaagtattttcacggataggtttttattttattttattttattttttttagatggagtctcactcagtcacccaggctggagtgcagtggcacgatcttggcttcccgggttcatgccattctcctgcctcagcctcccaagtagctgagactacaggtgtctgccaccaggccaggctaattttttgtgtttttagtagagatggggtttcaccatgttagccaggatggtctcgatctcctgacctcgtgatccacccgcctcagcctttcaaagtgctgggactacaggcgtgagccaccatgcctggccaatagcttttttttacttttattgtggaatattggtttttcactacaggctgatattggcttcaaattgtaccctcacagattctacaaacagtgaattttcaagcttctgaagcaaaataaaggtttaaccttgtaagatgaatctgcacattattgctggatattcggtttttcaccgtaggcatcgattttctctgaaatgtcctattgaagattttgccaaaagagtgtttctaccctgctgaatcaaaagaatggtttaattctgtgagatgaaatcacccatcgtaaattattttcagagacatcttcatatagtttttacctcagaatattcggtttttcactataagcctcaatgggctttgaaatgtctcccataaattgtacaaaaaaaaaaaggaatttccaacctgttgaatcaaaacaaatatttaatgttgtgagataaattcaaatatcgcaaagcattttcacagattttttttctactttttatcacggaatatttggtttttcactatagtcctcagtgggctcagaaatgccccttgtagattctacaaaaagaatgtttctaacctgctgaaccaaaacaatactttaactctttgaggtgaatccacacattgcaaacattttcacagatagcttgtttttagttttgatcacgtaatattggatgttttacaataggcctcaacgggctcagaattgtcccctcttagattctactaaaagagtgtttccaaaatgctgtgtaaaaataaaggcttaacactgtgagatgaatcga"
						.toUpperCase());
		this.builderForward.setGeneSymbol("BC052952");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 20, 25944999,
				PositionType.ZERO_BASED), "C", "A");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(2, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("314-639C>A", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals(null, annotation1.getProteinChange());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.NON_CODING_TRANSCRIPT_INTRON_VARIANT),
				annotation1.getEffects());
	}

	/**
	 * An intron of BC052952<br>
	 * non-coding <br>
	 * closer to leading exon<br>
	 * '-'-strand
	 */
	@Test
	public void testRealWorldCase_uc010wdn_1_second() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc010wdn.1	chr17	-	36344875	36375177	36344875	36344875	15	36344875,36345438,36346761,36347009,36351935,36352421,36353600,36357157,36358124,36358298,36358841,36361696,36365054,36367406,36375099,	36344956,36345478,36346847,36347082,36351996,36352526,36353765,36357272,36358158,36358395,36359042,36361804,36365176,36367522,36375177,		uc010wdn.1");
		this.builderForward
				.setSequence("aaatacatgctacaggatttaactatcagaatgaagatgaaaaagtcaccttgtctttccctagtactctgcaaacaggtaataactctttccaatactttgtattttgtttagtatgccactttttggaagtggtcactgtcaatatcttgttatggtttgatggccttggagatttaaaaccgagtaaaagtgtacgggaaccttaaagatagattttgttggagagctgaatgacaaaatgaaaggtttctatagaagtaagtatactaccccttctggagaggtgcgctatgctgctgtaacacagtttgaggctactgatgcccgaagggcttttccttgctgggatgagcctgctatcaaagcaacttttgatatctcattggttgttcctaaagacagagtagctttatcaaacatgaatgtaattgaccggaaaccataccctgatgatgaaaatttagtggaagtgaagtttgcccgcacacctgttacatctacatatctggtggcatttgttgtgggtgaatatgactttgtagaaacaaggtcaaaagatggtgtgtgtgtctgtgtttacactcctgttggcaaagcagaacaaggaaaatttgcaatagaggttgctgctaaaaccttgcctttttataaggactacttcaatgttccttatcctctacctaaaattgatctcattgctattgcagactttgcagctggtgccatggagaactgggaccttgttacttatagggagactgcattgcttattgatccaaaaaattcctgttcttcatcccgccagtgggttgctctggttgtgggacatgaacttgcccatcaatggtttggaaatcttgttactatggaatggtggactcatctttggttaaatgaaggttttgcatcctggattgaatatctgtgtgtagaccactgcttcccagagtatgatatttggactcagtttgtttctgctgattacacccgtgcccaggagcttgacgccttagataacagccatcctattgaagtcagtgtgggccatccatctgaggttgatgagatatttgatgctatatcatatagcaaaggtgcatctgtcatccgaatgctgcatgactacattggggataaggactttaagaaaggaatgaacatgtatttaaccaagttccaacaaaagaatgctgccgcaggatggacgtggtagaggtcgcgggcagttggtgggcacaagagcgagaggacatcattatgaaatacgaaaagggacaccgagctgggctgccagaggacaaggggcctaagccttttcgaagctacaacaacaacgtcgatcatttggggattgtacatgagacggagctgcctcctctgactgcgcgggaggcgaagcaaattcggcgggagatcagccgaaagagcaagtgggtggatatgctgggagactgggagaaatacaaaagcagcagaaag"
						.toUpperCase());
		this.builderForward.setGeneSymbol("LOC440434");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 17, 36360999,
				PositionType.ZERO_BASED), "A", "T");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(3, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("424+697T>A", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals(null, annotation1.getProteinChange());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.NON_CODING_TRANSCRIPT_INTRON_VARIANT),
				annotation1.getEffects());
	}

	/**
	 * An intron of BC052952<br>
	 * non-coding <br>
	 * closer to trailing exon<br>
	 * '-'-strand
	 */
	@Test
	public void testRealWorldCase_uc010wdn_1_third() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc010wdn.1	chr17	-	36344875	36375177	36344875	36344875	15	36344875,36345438,36346761,36347009,36351935,36352421,36353600,36357157,36358124,36358298,36358841,36361696,36365054,36367406,36375099,	36344956,36345478,36346847,36347082,36351996,36352526,36353765,36357272,36358158,36358395,36359042,36361804,36365176,36367522,36375177,		uc010wdn.1");
		this.builderForward
				.setSequence("aaatacatgctacaggatttaactatcagaatgaagatgaaaaagtcaccttgtctttccctagtactctgcaaacaggtaataactctttccaatactttgtattttgtttagtatgccactttttggaagtggtcactgtcaatatcttgttatggtttgatggccttggagatttaaaaccgagtaaaagtgtacgggaaccttaaagatagattttgttggagagctgaatgacaaaatgaaaggtttctatagaagtaagtatactaccccttctggagaggtgcgctatgctgctgtaacacagtttgaggctactgatgcccgaagggcttttccttgctgggatgagcctgctatcaaagcaacttttgatatctcattggttgttcctaaagacagagtagctttatcaaacatgaatgtaattgaccggaaaccataccctgatgatgaaaatttagtggaagtgaagtttgcccgcacacctgttacatctacatatctggtggcatttgttgtgggtgaatatgactttgtagaaacaaggtcaaaagatggtgtgtgtgtctgtgtttacactcctgttggcaaagcagaacaaggaaaatttgcaatagaggttgctgctaaaaccttgcctttttataaggactacttcaatgttccttatcctctacctaaaattgatctcattgctattgcagactttgcagctggtgccatggagaactgggaccttgttacttatagggagactgcattgcttattgatccaaaaaattcctgttcttcatcccgccagtgggttgctctggttgtgggacatgaacttgcccatcaatggtttggaaatcttgttactatggaatggtggactcatctttggttaaatgaaggttttgcatcctggattgaatatctgtgtgtagaccactgcttcccagagtatgatatttggactcagtttgtttctgctgattacacccgtgcccaggagcttgacgccttagataacagccatcctattgaagtcagtgtgggccatccatctgaggttgatgagatatttgatgctatatcatatagcaaaggtgcatctgtcatccgaatgctgcatgactacattggggataaggactttaagaaaggaatgaacatgtatttaaccaagttccaacaaaagaatgctgccgcaggatggacgtggtagaggtcgcgggcagttggtgggcacaagagcgagaggacatcattatgaaatacgaaaagggacaccgagctgggctgccagaggacaaggggcctaagccttttcgaagctacaacaacaacgtcgatcatttggggattgtacatgagacggagctgcctcctctgactgcgcgggaggcgaagcaaattcggcgggagatcagccgaaagagcaagtgggtggatatgctgggagactgggagaaatacaaaagcagcagaaag"
						.toUpperCase());
		this.builderForward.setGeneSymbol("LOC440434");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 17, 36359599,
				PositionType.ZERO_BASED), "G", "T");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(3, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("425-558C>A", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals(null, annotation1.getProteinChange());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.NON_CODING_TRANSCRIPT_INTRON_VARIANT),
				annotation1.getEffects());
	}

	/**
	 * <P>
	 * annovar: CHD5 chr1:6204222C>G distance of 7 hand-checked
	 * </P>
	 */
	@Test
	public void testRealWorldCase_uc001amb_2() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc001amb.2	chr1	-	6161846	6240194	6166339	6240083	42	6161846,6166293,6166454,6166675,6169854,6170453,6171834,6172199,6172968,6181164,6181553,6184007,6184576,6185159,6185583,6185825,6186631,6188105,6188558,6188897,6190263,6191690,6194187,6194777,6195289,6196576,6196787,6202187,6202473,6203882,6204083,6206271,6206724,6208913,6209305,6211091,6212471,6214719,6215658,6219395,6228209,6240004,	6165487,6166347,6166569,6166839,6170050,6170586,6171943,6172337,6173058,6181297,6181633,6184167,6184721,6185293,6185672,6185918,6186806,6188278,6188669,6189129,6190388,6191808,6194319,6194919,6195463,6196698,6196925,6202388,6202665,6203991,6204215,6206483,6206931,6209135,6209472,6211215,6212596,6214958,6215777,6219575,6228337,6240194,	Q8TDI0	uc001amb.2");
		this.builderForward
				.setSequence("ccgaggccgcctcgcccgccgcgcgcgccggtccggcagcgcacgggttaaggctggcaccgcgcccggcgggagggggggcgcccacctcccctcctccccgcgccgggcatgcggggcccagtgggcaccgaggaggagctgccgcggctgttcgccgaggagatggagaatgaggacgagatgtcagaagaagaagatggtggtcttgaagccttcgatgactttttccctgtggagcccgtgagccttcctaagaagaagaaacccaagaagctcaaggaaaacaagtgtaaagggaagcggaagaagaaagaggggagcaatgatgagctatcagagaatgaagaggatctggaagagaagtcggagagtgaaggcagtgactactccccgaataaaaagaagaagaagaaactcaaggacaagaaggagaaaaaagccaagcgaaaaaagaaggatgaggatgaggatgataatgatgatggatgcttaaaggagcccaagtcctcggggcagctcatggccgagtggggcctggacgacgtggactacctgttctcggaggaggattaccacacgctgaccaactacaaggccttcagccagttcctcaggccactcattgccaagaagaacccgaagatccccatgtccaaaatgatgaccgtcctgggtgccaagtggcgggagttcagcgccaacaaccccttcaagggcagctccgcggcagcagcggcggcggcggtggctgcggctgtagagacggtcaccatctcccctccgctagccgtcagccccccgcaggtgccccagcctgtgcctatccgcaaggccaagaccaaggagggcaaagggcctggagtgaggaagaagatcaaaggctccaaagatgggaagaaaaagggcaaagggaaaaagacggccgggctcaagttccgcttcggggggatcagcaacaagaggaagaaaggctcctcgagtgaagaagatgagagggaggagtcggacttcgacagcgccagcatccacagtgcctccgtgcgctccgaatgctctgcagccctgggcaagaagagcaagaggaggcgcaagaagaagaggattgatgatggtgacggctatgagacagaccaccaggattactgtgaggtgtgccagcagggtggggagatcatcctgtgcgacacctgcccgagggcctaccatctcgtatgcctggacccagagctggagaaggctcccgagggcaagtggagctgcccccactgtgagaaggaggggatccagtgggagccgaaggacgacgacgatgaagaggaggagggcggctgcgaggaggaggaggacgaccacatggagttctgccgcgtgtgcaaggacgggggcgagctgctctgctgcgacgcctgcccctcctcctaccacctgcattgcctcaacccgccgctgcccgagatcccaaacggtgaatggctctgcccgcgctgtacttgccccccactgaagggcaaagtccagcggattctacactggaggtggacggagccccctgcccccttcatggtggggctgccggggcctgacgtggagcccagcctccctccacctaagcccctggagggcatccctgagagagagttctttgtcaagtgggcagggctgtcctactggcattgctcctgggtgaaggagctacagctggagctgtaccacacggtgatgtatcgcaactaccaaagaaagaacgacatggatgagccgcccccctttgactacggctctggggatgaagacggcaagagcgagaagaggaagaacaaggaccccctctatgccaagatggaggagcgcttctaccgctatggcatcaagccagagtggatgatgattcaccgaatcctgaaccatagctttgacaagaagggggatgtgcactacctgatcaagtggaaagacctgccctacgaccagtgcacctgggagatcgatgacatcgacatcccctactacgacaacctcaagcaggcctactggggccacagggagctgatgctgggagaagacaccaggctgcccaagaggctgctcaagaagggcaagaagctgagggacgacaagcaggagaagccgccggacacgcccattgtggaccccacggtcaagttcgacaagcagccatggtacatcgactccacaggcggcacactgcacccgtaccagctggagggcctcaactggctgcgcttctcttgggcccagggcactgacaccatcctggccgatgagatgggtctgggcaagacggtgcagaccatcgtgttcctttactccctctacaaggagggccactccaaagggccctacctggttagcgcgcccctctccaccatcatcaactgggaacgcgagtttgagatgtgggcgcccgacttctacgtggtcacctacacgggggacaaggagagccgctcggtgattcgggagaacgagttttcctttgaggacaacgccattcggagtgggaagaaggtattccgtatgaagaaagaagtgcagatcaaattccacgtgctgctcacctcctatgagctcatcaccattgaccaggccatcctgggctccatcgagtgggcctgcctggtggtagatgaggcccaccgcctcaagaacaaccagtccaagttttttagggtcttaaacagctacaagattgattacaagctgctgctgacagggaccccccttcagaacaacctggaggagctgttccatctcctcaacttcctgactccagagaggttcaacaacctggagggcttcctggaggagtttgctgacatctccaaggaagaccagatcaagaagctgcatgacctgctggggccgcacatgctcaggcggctcaaggctgacgtgttcaagaacatgccggccaagaccgagctcattgtccgggtggagctgagccagatgcagaagaagtactacaagttcatcctcacacggaactttgaggcactgaactccaaggggggcgggaaccaagtatcgctgctcaacatcatgatggacctgaaaaagtgctgcaaccacccctacctcttccctgtggctgccgtggaggcccctgtcttgcccaatggctcctacgatggaagctccctggtcaagtcttcagggaagctcatgctgctacagaagatgctgaagaaactgcgggatgaggggcaccgtgtgctcatcttctcccagatgaccaagatgctggacctcctggaggacttcctggagtacgaaggctacaagtatgagcggattgatggtggcatcaccgggggcctccggcaggaggcaatcgacagattcaatgcccccggggcccagcagttctgcttcctcctctcaacccgggcaggtggtctgggcatcaacctggccacggcggacactgtcatcatctacgactcggactggaacccgcacaatgacatccaggccttcagccgcgcccaccgcatcggccagaacaagaaggtgatgatctaccgcttcgtgactcgggcctcggtggaggagcgcatcacgcaggtggccaagcgcaagatgatgctcacccacctggtggtgcggcccggcctcggctccaagtcggggtccatgaccaagcaggagctggacgacatcctcaagttcggcacggaggaactcttcaaggacgacgtggagggcatgatgtctcagggccagaggccggtcacacccatccctgatgtccagtcctccaaaggggggaacttggccgccagtgcaaagaagaagcacggtagcaccccgccaggtgacaacaaggacgtggaggacagcagtgtgatccactatgacgatgcggccatctccaagctgctggaccggaaccaggacgctacagatgacacggagctacagaacatgaacgagtacctgagctccttcaaggtggcgcagtacgtggtgcgcgaggaggacggcgtggaggaggtggagcgggaaatcatcaagcaggaggagaacgtggaccccgactactgggagaagctgctgcggcaccactatgagcagcagcaggaggacctggcccgcaacctgggcaagggcaagcgcatccgcaagcaggtcaactacaacgatgcctcccaggaggaccaggagtggcaggatgagctctctgataaccagtcagaatattccattggctctgaggatgaggatgaggactttgaagagaggccggaagggcagagtggacgacgacaatcccggaggcagctgaagagtgacagggacaagcccctgcccccgcttctcgcccgagttggtggcaacatcgaggtgctgggcttcaatgcccgacagcggaaggcctttctgaacgccatcatgcgctggggcatgcccccgcaggacgccttcaactcccactggctggtgcgggaccttcgagggaagagcgagaaggagtttagagcctatgtgtccctcttcatgcggcacctgtgtgagccgggggcggatggtgcagagaccttcgcagacggcgtgccccgggagggcctctccaggcagcacgtgctgacccgcatcggggtcatgtcactagttaggaagaaggttcaggagtttgagcatgtcaacgggaagtacagcaccccagacttgatccctgaggggcccgaggggaagaagtcgggcgaggtgatctcctcggaccccaacacaccagtgcccgccagccctgcccacctcctgccagccccgctgggcctgccagacaaaatggaagcccagctgggctacatggatgagaaagaccccggggcacagaagccaaggcagcccctggaagtccaggcccttccagccgccttggatagagtggagagtgaggacaagcacgagagcccagccagcaaggagagagcccgagaggagcggccagaggagacggagaaggccccgccctccccggagcagctgccgagagaggaggtgcttcctgagaaggagaagatcctggacaagctggagctgagcttgatccacagcagaggggacagttccgaactcaggccagatgacaccaaggctgaggagaaggagcccattgaaacacagcaaaatggtgacaaagaggaagatgacgaggggaagaaggaggacaagaaggggaaattcaagttcatgttcaacatcgcggacgggggcttcacggagttgcacacgctgtggcagaacgaggagcgggctgctgtatcctctgggaaaatctacgacatctggcaccggcgccatgactactggctgctggcgggcatcgtgacgcacggctacgcccgctggcaggacatccagaatgacccacggtacatgatcctcaacgagcccttcaagtctgaggtccacaagggcaactacctggagatgaagaacaagttcctggcccgcaggtttaagctgctggagcaggcgttggtcattgaggagcagctccggagggccgcgtacctgaacatgacgcaggaccccaaccaccccgccatggccctcaacgcccgcctggctgaagtggagtgcctcgccgagagccaccagcacctgtccaaggagtcccttgctgggaacaagcctgccaatgccgtcctgcacaaggtcctgaaccagctggaggagctgctgagcgacatgaaggccgacgtgacccggctgccatccatgctgtcccgcatccccccggtggccgcccggctgcagatgtcggagcgcagcatcctgagccgcctgaccaaccgcgccggggaccccaccatccagcagggcgctttcggctcctcccagatgtacagcaacaactttgggcccaacttccggggccctggaccgggagggattgtcaactacaaccagatgcccctggggccctatgtgaccgatatctagccgtcctcgagacttccctgtgttgcagcgctcatttccagctgagccacgcctgccgggccacctgcccgacccacatgggagagaaaagctgccacctttttaggagccagcgccaccttgggacaaaaagggaaacctagtaatgccatcacatggaggacgaggcccagctcagctgggccagagcccagaagtgccacctcatcataattcaagtgttcttccacacagcgttgcccccacaaccacgccggacgtgccccctcgccaccttttccagacgacttcttagaagagatttcatttatttgtacatcttttgcactttcctattgaagacttgaacacgtttgtcttgataaaagttggatgacgtatggaagattcgaacctgcagcactgatgtctctttaccgatgggttccagacccaaggtagtcctggcactgccctgtggactcagcccagctggggaggacatggcgcccggtgccctaggagccctcagtgtcccctacctgacctgtctgcacctgtgtgacagccccttctgatttggccccctgcccgcttggagcctcccagcaccagacagggcaggtttggggagccgctgtccagccctatggtgagaccctggctgacatttcccctccttcctgccaaggctggaggccgccaggtgtttgctctctccttgtggggaggtggatcctctgccagcaggtgtctgtccccggcccagcgcatcttgaaggccctggactctgttaccagtggggggcctgtggccccggctcttcacagcaggcagaagcgtatgatcccagggagggtgggctggggggggggcagcacttgcctcggagccactgctgcgttgggtttgccttctgccagaggtgtggttaagggcagtgatagctgcgccccagcaaggagggtcatgggccctgagctggcccaggagaccctggccctgccctgtgccctgggatgcctaccctgcccacctccggaggtggaaccgggctgcccctgggggcctggacttgtcccattcccctgtccctggaaaggccttcccgggggctttttgcctgaggctgcactcttggaaggcgtggggagagttctgcctggagggggactggacccagtgccctctgcagacccctcccagccgcagcaccaagggcttgctgccttgtttcctgccaggagcgccctgacatcccaaaaccatcttcccacatgtgggttgttggaatcccacccactcccccggaccctgcccctccaagttggggacggagatggggagcaggtcaggccctccctaggcctgtttgtgtgtgatccttctcctggccccaggtcgacctggaaggcgtttctgagtcttgtactgggaccttcccagggaaggtgccttggggtgcagaatcctgggaatgttaggaaagggctgcgtgaaaaacccagctgggcctggggtggaggtgcacacgggctggggctcagctccttggagagggcctgcctgggactgagggggccagggctgggtcaggattcaccagcttgtgttgcagacactggagcgattgcctggccctccctaagccctatatgcctcactcataatgaggctgtctctgaaccccggagggcgggacctacaagtccttcctcttggcgcattcccacactccagtctggatccaggtctgcaaggccagcccgaccctgacactgcacgtgggcgtagtggggagcaccctggagtggagatgattgtcagcgggctcacatgttatggttcatccacatgcgtgttgtgtgctctgcggtgcctcctggcaaagggtcctggctcgggtcagaagctcactcccgtgccctcgtccccaaacaagcagtggcaagcactggggttggccctcgttgggagcagtgcccaccttccttggcccacagcagataggtcccgagcagcaggactggaggcctgtggcggtcagggcaggggtctgtgtctccaccccaagggctgatggtccctcccctgtgcgccttcctctcaggcaggcttgctctgtcccttcctctctgcttccctcccaggcctcgccagggctcccttctcctctgcctggttagactcgggcacccaggaaagcctggccagggctcctttgggcctgggccccctgcactgcctggtccagaaggggtggtgctgtccgtggccagcaccccggggcccgggaggggtgggctactagagtcagagcgggtttggggctgaccagtttgggagaggagaaaagatctgagaatgtccttcttggtttgtcagtcatctctgccaaaagtggtgatggtggtgtccgtatgtttggcgtctttgggctgggtttggtttttgctgctggtagaatcagggtcctcgggcatgaacgcgagcccaaagtgccagtctgcgattggaaatttccagccactttaagccagtgctgagtagggcttctgcagagccatgtttgagccaaggtcttggaaggcattgccccatgggctcaggtgactcggggtggagtgagcacgtctgcagggccctctcatacacgcctgaggcagaagcagcgtcccccgtgaaagccaccttccgaagctcctgcgttttttgcaaacttggcttcccccaggggcaggctggactttccctgccccctatgattgaagtcctcctgcttttgggggctgccttcccagagtcccccgggtgctcccctgccgaggtcaggagctgaccaagccttggcccggtgacacctgcagccctcactcctgtcatcccaggacacttgaggcccaaggaggtggagtggagagtgggctcgggtacatgggagccagaagccagatggacttggtcaagtgtcggtcacttggagcctccagtgtgcgtcagggtctgtgggcaggggacagggcgtgggtgggggccgaggctggcacgcccctctgccctcaccgtcttggtgacctggcctcggcccctcccccaagtctcttctgtgcaaggcccgcctcggcctcggcagctggttcctgtcctgttttctgtgtctgaaagtttacaggttgtggtgcatcagcccaaactcactggcgttgtgttttttttttctttaattttcagattttttttttaaacaaagtatttttttaggtgcgataacccagaaagggcccgttgggtgtgtgtgtgtgtcctgaactcctcaagcagcgattggagcccaagcacccctggagaggaagggagggtccccactggcccgtggggtctgagttcaggggtgtggagggagcagactccaccggcccaggcccagctaagagggggccgacccctttccccaggcacagccccaggctggcaaagggagggccctgggctgggtgcaaggcgcgccaggagtcccagccagggtggccccggcgggggcgggtccagctttggaagccaggctcccctgtgagccgtggcttgtctggtcttcgcccacgggaggctggacagaggctgtagccaacacaatcacctttactttgtactctgtgtgtatgttttggttttctgtgttttaataaatcctttgggaaaggatttaa"
						.toUpperCase());
		this.builderForward.setGeneSymbol("CHD5");
		this.infoForward = builderForward.build();
		// RefSeq REFSEQ_ID

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6204221,
				PositionType.ZERO_BASED), "C", "G");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(10, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("1803-7G>C", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("?", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.CODING_TRANSCRIPT_INTRON_VARIANT,
				VariantEffect.SPLICE_REGION_VARIANT), annotation1.getEffects());
	}

	@Test
	public void testRealWorldCase_uc010pha_1() throws InvalidGenomeVariant {
		this.builderForward = TranscriptModelFactory
				.parseKnownGenesLine(
						refDict,
						"uc010pha.1	chr1	+	156105523	156108548	156105787	156107555	5	156105523,156106004,156106711,156106903,156107444,	156105912,156106227,156106819,156107023,156108548,	B4DFR3	uc010pha.1");
		this.builderForward
				.setSequence("atgtttagagcttgtgatgttcagagctggctctgatgagggctctggggaagctctgattgcagatcctggagagagtagccaggtgtctcctacaccgacccacgtccctccttccccatacttagggcccttgggagctcaccaaaccctcccaccccccttcagctggcagccaaggaggcgaagcttcgagacctggaggactcactggcccgtgagcgggacaccagccggcggctgctggcggaaaaggagcgggagatggccgagatgcgggcaaggatgcagcagcagctggacgagtaccaggagcttctggacatcaagctggccctggacatggagatccacgcctaccgcaagctcttggagggcgaggaggagaggctacgcctgtcccccagccctacctcgcagcgcagccgtggccgtgcttcctctcactcatcccagacacagggtgggggcagcgtcaccaaaaagcgcaaactggagtccactgagagccgcagcagcttctcacagcacgcacgcactagcgggcgcgtggccgtggaggaggtggatgaggagggcaagtttgtccggctgcgcaacaagtccaatgaggaccagtccatgggcaattggcagatcaagcgccagaatggagatgatcccttgctgacttaccggttcccaccaaagttcaccctgaaggctgggcaggtggtgacgatctgggctgcaggagctggggccacccacagcccccctaccgacctggtgtggaaggcacagaacacctggggctgcgggaacagcctgcgtacggctctcatcaactccactggggaagaagtggccatgcgcaagctggtgcgctcagtgactgtggttgaggacgacgaggatgaggatggagatgacctgctccatcaccaccacgtgagtggtagccgccgctgaggccgagcctgcactggggccacccagccaggcctgggggcagcctctccccagcctccccgtgccaaaaatcttttcattaaagaatgttttggaactttactcgctggcctggcctttcttctctctcctccctataccttgaacagggaacccaggtgtctgggtgccctactctggtaaggaagggagtgggaactttctgatgccatggaatattcctgtgggagcagtggacaagggtctggatttgtcttctgggaaagggaggggaggacagacgtggggcatgcccgccctgcctctctcccccattcttgttgcatgcatatcctctcatttccctcatttttcctgcaagaatgttctctctcattcctgaccgcccctccactccaattaatagtgcatgcctgctgccctacaagcttgctcccgttctctcttcttttcctcttaagctcagagtagctagaacagagtcagagtcactgctctggttctctgtccccaagtcttcctgagccttctccccttttatgtcttccctctcctcctccgggcccctagcctcccaaacccccattgcccgctggctccttgggcacagaaccacaccttcctgcctggcggctgggagcctgcaggagcctggagcctggttgggcctgagtggtcagtcccagactcgccgtcccgcctgagccttgtctcccttcccagggctcccactgcagcagctcgggggaccccgctgagtacaacctgcgctcgcgcaccgtgctgtgcgggacctgcgggcagcctgccgacaaggcatctgccagcggctcaggagcccaggtgggcggacccatctcctctggctcttctgcctccagtgtcacggtcactcgcagctaccgcagtgtggggggcagtgggggtggcagcttcggggacaatctggtcacccgctcctacctcctgggcaactccagcccccgaacccag"
						.toUpperCase());
		this.builderForward.setGeneSymbol("LMNA");
		this.infoForward = builderForward.build();
		// RefSeq NM_005572.3

		GenomeVariant change1 = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 156107469,
				PositionType.ZERO_BASED), "G", "A");
		Annotation annotation1 = new SNVAnnotationBuilder(infoForward, change1, new AnnotationBuilderOptions()).build();
		Assert.assertEquals(infoForward.getAccession(), annotation1.getTranscript().getAccession());
		Assert.assertEquals(4, annotation1.getAnnoLoc().getRank());
		Assert.assertEquals("602G>A", annotation1.getCDSNTChange().toHGVSString());
		Assert.assertEquals("(Arg201His)", annotation1.getProteinChange().toHGVSString());
		Assert.assertEquals(ImmutableSortedSet.of(VariantEffect.MISSENSE_VARIANT), annotation1.getEffects());
	}

}
