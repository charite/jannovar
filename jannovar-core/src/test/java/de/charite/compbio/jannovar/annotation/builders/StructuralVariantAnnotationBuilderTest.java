package de.charite.compbio.jannovar.annotation.builders;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.charite.compbio.jannovar.annotation.Annotation;
import de.charite.compbio.jannovar.annotation.AnnotationLocation;
import de.charite.compbio.jannovar.annotation.InvalidGenomeVariant;
import de.charite.compbio.jannovar.data.ReferenceDictionary;
import de.charite.compbio.jannovar.reference.GenomePosition;
import de.charite.compbio.jannovar.reference.GenomeVariant;
import de.charite.compbio.jannovar.reference.HG19RefDictBuilder;
import de.charite.compbio.jannovar.reference.PositionType;
import de.charite.compbio.jannovar.reference.Strand;
import de.charite.compbio.jannovar.reference.TranscriptModel;
import de.charite.compbio.jannovar.reference.TranscriptModelBuilder;
import de.charite.compbio.jannovar.reference.TranscriptModelFactory;

/**
 * Tests for the StructuralVariantAnnotationBuilder.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public class StructuralVariantAnnotationBuilderTest {

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
	public void testSVInsertionOnTranscript() throws InvalidGenomeVariant {
		final GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640062,
				PositionType.ZERO_BASED), "", "CGAT");
		final Annotation anno = new StructuralVariantAnnotationBuilder(infoForward, change).build();
		Assert.assertEquals(infoForward.getAccession(), anno.getTranscript().getAccession());
		Assert.assertEquals(AnnotationLocation.INVALID_RANK, anno.getAnnoLoc().getRank());
		Assert.assertEquals(null, anno.getCDSNTChange());
		Assert.assertEquals("6640062_6640063insCGAT", anno.getGenomicNTChange().toHGVSString());
		Assert.assertEquals(null, anno.getProteinChange());
	}

	@Test
	public void testSVInsertionIntergenic() throws InvalidGenomeVariant {
		final GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640062,
				PositionType.ZERO_BASED), "", "CGAT");
		final Annotation anno = new StructuralVariantAnnotationBuilder(null, change).build();
		Assert.assertEquals(null, anno.getTranscript());
		Assert.assertEquals(null, anno.getAnnoLoc());
		Assert.assertEquals(null, anno.getCDSNTChange());
		Assert.assertEquals("6640062_6640063insCGAT", anno.getGenomicNTChange().toHGVSString());
		Assert.assertEquals(null, anno.getProteinChange());
	}

	@Test
	public void testSVDeletionOnTranscript() throws InvalidGenomeVariant {
		final GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640062,
				PositionType.ZERO_BASED), "", "CGAT");
		final Annotation anno = new StructuralVariantAnnotationBuilder(infoForward, change).build();
		Assert.assertEquals(infoForward.getAccession(), anno.getTranscript().getAccession());
		Assert.assertEquals(AnnotationLocation.INVALID_RANK, anno.getAnnoLoc().getRank());
		Assert.assertEquals(null, anno.getCDSNTChange());
		Assert.assertEquals("6640062_6640063insCGAT", anno.getGenomicNTChange().toHGVSString());
		Assert.assertEquals(null, anno.getProteinChange());
	}

	@Test
	public void testSVDeletionIntergenic() throws InvalidGenomeVariant {
		final GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640062,
				PositionType.ZERO_BASED), "CGAT", "");
		final Annotation anno = new StructuralVariantAnnotationBuilder(null, change).build();
		Assert.assertEquals(null, anno.getTranscript());
		Assert.assertEquals(null, anno.getAnnoLoc());
		Assert.assertEquals(null, anno.getCDSNTChange());
		Assert.assertEquals("6640063_6640066delCGAT", anno.getGenomicNTChange().toHGVSString());
		Assert.assertEquals(null, anno.getProteinChange());
	}

	@Test
	public void testSVInversionOnTranscript() throws InvalidGenomeVariant {
		final GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640062,
				PositionType.ZERO_BASED), "CGAT", "ATCG");
		final Annotation anno = new StructuralVariantAnnotationBuilder(infoForward, change).build();
		Assert.assertEquals(infoForward.getAccession(), anno.getTranscript().getAccession());
		Assert.assertEquals(AnnotationLocation.INVALID_RANK, anno.getAnnoLoc().getRank());
		Assert.assertEquals(null, anno.getCDSNTChange());
		Assert.assertEquals("6640063_6640066inv", anno.getGenomicNTChange().toHGVSString());
		Assert.assertEquals(null, anno.getProteinChange());
	}

	@Test
	public void testSVInversionIntergenic() throws InvalidGenomeVariant {
		final GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640062,
				PositionType.ZERO_BASED), "CGAT", "ATCG");
		final Annotation anno = new StructuralVariantAnnotationBuilder(null, change).build();
		Assert.assertEquals(null, anno.getTranscript());
		Assert.assertEquals(null, anno.getAnnoLoc());
		Assert.assertEquals(null, anno.getCDSNTChange());
		Assert.assertEquals("6640063_6640066inv", anno.getGenomicNTChange().toHGVSString());
		Assert.assertEquals(null, anno.getProteinChange());
	}

	@Test
	public void testSVSubstitutionOnTranscript() throws InvalidGenomeVariant {
		final GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640062,
				PositionType.ZERO_BASED), "CGAT", "TTTTA");
		final Annotation anno = new StructuralVariantAnnotationBuilder(infoForward, change).build();
		Assert.assertEquals(infoForward.getAccession(), anno.getTranscript().getAccession());
		Assert.assertEquals(AnnotationLocation.INVALID_RANK, anno.getAnnoLoc().getRank());
		Assert.assertEquals(null, anno.getCDSNTChange());
		Assert.assertEquals("6640063_6640066delCGATinsTTTTA", anno.getGenomicNTChange().toHGVSString());
		Assert.assertEquals(null, anno.getProteinChange());
	}

	@Test
	public void testSVSubstitutionIntergenic() throws InvalidGenomeVariant {
		final GenomeVariant change = new GenomeVariant(new GenomePosition(refDict, Strand.FWD, 1, 6640062,
				PositionType.ZERO_BASED), "CGAT", "TTTTA");
		final Annotation anno = new StructuralVariantAnnotationBuilder(null, change).build();
		Assert.assertEquals(null, anno.getTranscript());
		Assert.assertEquals(null, anno.getAnnoLoc());
		Assert.assertEquals(null, anno.getCDSNTChange());
		Assert.assertEquals("6640063_6640066delCGATinsTTTTA", anno.getGenomicNTChange().toHGVSString());
		Assert.assertEquals(null, anno.getProteinChange());
	}

}
