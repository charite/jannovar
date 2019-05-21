package de.charite.compbio.jannovar.impl.parse.refseq;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import de.charite.compbio.jannovar.data.JannovarData;
import de.charite.compbio.jannovar.data.JannovarDataSerializer;
import de.charite.compbio.jannovar.data.ReferenceDictionary;
import de.charite.compbio.jannovar.impl.parse.ReferenceDictParser;
import de.charite.compbio.jannovar.impl.parse.TranscriptParseException;
import de.charite.compbio.jannovar.reference.GenomeInterval;
import de.charite.compbio.jannovar.reference.GenomePosition;
import de.charite.compbio.jannovar.reference.HG19RefDictBuilder;
import de.charite.compbio.jannovar.reference.ProjectionException;
import de.charite.compbio.jannovar.reference.Strand;
import de.charite.compbio.jannovar.reference.TranscriptModel;
import de.charite.compbio.jannovar.reference.TranscriptPosition;
import de.charite.compbio.jannovar.reference.TranscriptProjectionDecorator;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringJoiner;
import java.util.TreeSet;
import java.util.function.Function;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.ini4j.Ini;
import org.ini4j.Profile;
import org.ini4j.Profile.Section;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RefSeqParserTest {

	/**
	 * this test uses this static hg19 reference dictionary
	 */
	private static final ReferenceDictionary refDict = HG19RefDictBuilder.build();

	InputStream stream;
	File dataDirectory;
	// INI section for all transcripts
	Section allIniSection;
	// INI section for curated transcripts
	Section curatedIniSection;

	@Before
	public void setUp() throws Exception {
		dataDirectory = new File("src/test/data/mini_refseq");

		String allLines = "[hg19/refseq]\n" + "type=refseq\n" + "alias=MT,M,chrM\n"
			+ "chromInfo=http://hgdownload.soe.ucsc.edu/goldenPath/hg19/database/chromInfo.txt.gz\n"
			+ "chrToAccessions=ftp://ftp.ncbi.nlm.nih.gov/genomes/H_sapiens/ARCHIVE/ANNOTATION_RELEASE.105/Assembled_chromosomes/chr_accessions_GRCh37.p13\n"
			+ "chrToAccessions.format=chr_accessions\n"
			+ "gff=ftp://ftp.ncbi.nlm.nih.gov/genomes/H_sapiens/ARCHIVE/ANNOTATION_RELEASE.105/GFF/ref_GRCh37.p13_top_level.gff3\n"
			+ "rna=ftp://ftp.ncbi.nlm.nih.gov/genomes/H_sapiens/ARCHIVE/ANNOTATION_RELEASE.105/RNA/rna.fa\n";
		stream = new ByteArrayInputStream(allLines.getBytes());
		Ini allIni = new Ini(stream);
		allIniSection = allIni.get("hg19/refseq");

		String curatedLines = "[hg19/refseq]\n" + "type=refseq\n" + "alias=MT,M,chrM\n" + "onlyCurated=true\n"
			+ "chromInfo=http://hgdownload.soe.ucsc.edu/goldenPath/hg19/database/chromInfo.txt.gz\n"
			+ "chrToAccessions=ftp://ftp.ncbi.nlm.nih.gov/genomes/H_sapiens/ARCHIVE/ANNOTATION_RELEASE.105/Assembled_chromosomes/chr_accessions_GRCh37.p13\n"
			+ "chrToAccessions.format=chr_accessions\n"
			+ "gff=ftp://ftp.ncbi.nlm.nih.gov/genomes/H_sapiens/ARCHIVE/ANNOTATION_RELEASE.105/GFF/ref_GRCh37.p13_top_level.gff3\n"
			+ "rna=ftp://ftp.ncbi.nlm.nih.gov/genomes/H_sapiens/ARCHIVE/ANNOTATION_RELEASE.105/RNA/rna.fa\n";
		stream = new ByteArrayInputStream(curatedLines.getBytes());
		Ini ini = new Ini(stream);
		curatedIniSection = ini.get("hg19/refseq");
	}

	@After
	public void tearDown() throws Exception {
		LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
		Configuration conf = ctx.getConfiguration();
		conf.getLoggerConfig(LogManager.ROOT_LOGGER_NAME).setLevel(Level.INFO);
	}

	@Test
	public void testAll() throws TranscriptParseException {
		RefSeqParser parser = new RefSeqParser(refDict, dataDirectory.getAbsolutePath(), new ArrayList<String>(),
			allIniSection);
		ImmutableList<TranscriptModel> result = parser.run();

		Assert.assertEquals(12, result.size());

		SortedSet<String> values = new TreeSet<String>();
		for (TranscriptModel tx : result)
			values.add(tx.toString());

		SortedSet<String> expected = new TreeSet<String>();
		expected.addAll(
			Lists.newArrayList("NM_000539.3(3:g.129247482_129254187)", "NM_001025596.2(11:g.47487489_47510576)",
				"NM_001172639.1(11:g.47487489_47545540)", "NM_001172640.1(11:g.47487489_47510576)",
				"NM_006560.3(11:g.47487489_47574792)", "NM_198700.2(11:g.47487489_47516073)",
				"XM_005252753.1(11:g.47487489_47517587)", "XM_005252754.1(11:g.47487489_47517587)",
				"XM_005252755.1(11:g.47487489_47517587)", "XM_005252756.1(11:g.47487489_47522484)",
				"XM_005252757.1(11:g.47487489_47522484)", "XM_005252758.1(11:g.47487489_47545089)"));

		Assert.assertEquals(expected, values);

		Assert.assertEquals("10658", result.get(0).getGeneID());
		Assert.assertEquals("CELF1", result.get(0).getGeneSymbol());
		Assert.assertEquals(
			"{CCDS_ID=CCDS7938|CCDS7939|CCDS31482|CCDS53622|CCDS53623, COSMIC_ID=CELF1, "
				+ "ENSEMBL_GENE_ID=ENSG00000149187, ENTREZ_ID=10658, "
				+ "HGNC_ALIAS=CUG-BP|hNab50|BRUNOL2|NAB50|CUGBP|NAPOR|EDEN-BP, "
				+ "HGNC_ID=HGNC:2549, HGNC_PREVIOUS=CUGBP1, HGNC_SYMBOL=CELF1, MGD_ID=MGI:1342295, "
				+ "OMIM_ID=601074, PUBMED_ID=8948631|9371827, REFSEQ_ACCESSION=NM_006560, "
				+ "RGD_ID=RGD:1307721, UCSC_ID=uc001nfl.4, UNIPROT_ID=Q92879, VEGA_ID=OTTHUMG00000166526}",
			result.get(0).getAltGeneIDs().toString());
	}

	@Test
	public void testOnlyCurated() throws TranscriptParseException {
		RefSeqParser parser = new RefSeqParser(refDict, dataDirectory.getAbsolutePath(), new ArrayList<>(),
			curatedIniSection);
		ImmutableList<TranscriptModel> result = parser.run();

		Assert.assertEquals(6, result.size());

		SortedSet<String> values = new TreeSet<String>();
		for (TranscriptModel tx : result)
			values.add(tx.toString());

		SortedSet<String> expected = new TreeSet<String>();
		expected.addAll(
			Lists.newArrayList("NM_000539.3(3:g.129247482_129254187)", "NM_001025596.2(11:g.47487489_47510576)",
				"NM_001172639.1(11:g.47487489_47545540)", "NM_001172640.1(11:g.47487489_47510576)",
				"NM_006560.3(11:g.47487489_47574792)", "NM_198700.2(11:g.47487489_47516073)"));

		Assert.assertEquals(expected, values);
	}

	@Test
	public void testRun() throws Exception {
		Path dataDirectory = Paths.get("src/test/resources/build/hg19/refseq").toAbsolutePath();

		Ini iniFile = new Ini();
		iniFile.load(Paths.get("src/test/resources/build/default_sources.ini").toFile());
		Profile.Section iniSection = iniFile.get("hg19/refseq");
		// CAUTION! the real RefDict is built from the download files. Having a mismatched chromosome name will
		// result in missing transcriptModels
		ReferenceDictionary refDict = new ReferenceDictParser(dataDirectory.resolve("chromInfo.txt.gz").toString(), dataDirectory.resolve("chr_accessions_GRCh37.p13").toString(), iniSection).parse();
		RefSeqParser instance = new RefSeqParser(refDict, dataDirectory.toString(), Collections.emptyList(), iniSection);
		ImmutableList<TranscriptModel> transcripts = instance.run();

//		Using UCSC Entrez ID 646999 for transcript NR_024390.1 as HGNC did not provide alternative gene ID
//		Using UCSC Entrez ID 6801 for transcript XM_005264519.1 as HGNC did not provide alternative gene ID
//		Using UCSC Entrez ID 6801 for transcript NM_003162.3 as HGNC did not provide alternative gene ID

//		SH3RF3 NM_001099289.1(2:g.109745997_110107395)
//		STRN XM_005264519.1(2:g.37070934_37193673)
//		STRN NM_003162.3(2:g.37064841_37193615)
//		LOC646999 NR_024390.1(7:g.39649086_39651687)
//		SLC25A6 NM_001636.3(Y:g.1455045_1461039)

		transcripts.forEach(transcript -> System.out.println(printTranscriptModel(transcript)));
		checkNm1636Transcript(transcripts);

		assertEquals(5, transcripts.size());
		List<Integer> chromosomes = transcripts.stream().map(TranscriptModel::getChr).distinct().sorted().collect(toList());
		System.out.println("Chromosomes: " + chromosomes);
		assertEquals(ImmutableList.of(2, 7, 24), chromosomes);
	}

	private static void checkNm1636Transcript(ImmutableList<TranscriptModel> transcripts) {
		TranscriptModel nm_1636 = transcripts.stream().filter(x -> x.getAccession().equals("NM_001636.3")).findFirst().get();

		GenomeInterval txRegion = nm_1636.getTXRegion().withStrand(Strand.FWD);
		assertEquals(1455044, txRegion.getBeginPos());
		assertEquals(1461039, txRegion.getEndPos());

		GenomeInterval cdsRegion = nm_1636.getCDSRegion().withStrand(Strand.FWD);
		assertEquals(1455494, cdsRegion.getBeginPos());
		assertEquals(1460902, cdsRegion.getEndPos());

		assertEquals(4, nm_1636.getExonRegions().size());
		GenomeInterval exon1 = nm_1636.getExonRegions().get(0).withStrand(Strand.FWD);
		assertEquals(1460791, exon1.getBeginPos());
		assertEquals(1461039, exon1.getEndPos());
	}

	private void checkMatchesOldData(ImmutableList<TranscriptModel> transcripts) throws Exception {

		JannovarData oldJannovarData = new JannovarDataSerializer("src/test/resources/build/hg19/hg19_refseq.ser").load();

		Map<String, TranscriptModel> oldTranscripts = oldJannovarData.getTmByAccession();
		Map<String, TranscriptModel> newTranscripts = transcripts.stream().collect(toMap(TranscriptModel::getAccession, Function
			.identity()));
		System.out.printf("Num old chr: %d, transcripts: %d%n", oldTranscripts.values().stream().map(TranscriptModel::getChr).distinct().count(), oldTranscripts.size());
		System.out.printf("Num new chr: %d, transcripts: %d%n", newTranscripts.values().stream().map(TranscriptModel::getChr).distinct().count(), newTranscripts.size());

		assertEquals(oldTranscripts.keySet(), newTranscripts.keySet());

		Set<TranscriptModel> missingTranscriptModels = Sets.difference(new HashSet<>(oldTranscripts.values()), new HashSet<>(newTranscripts.values()));
		if (!missingTranscriptModels.isEmpty()) {
			System.out.println(missingTranscriptModels.size() + " missing txAccessions:");
//			missingTranscriptModels.forEach(tx -> System.out.println(printTranscriptModel(tx)));
		}

		assertEquals(oldTranscripts.size(), transcripts.size());
		oldTranscripts.forEach((s, transcriptModel) -> {
			TranscriptModel newTranscriptModel = newTranscripts.get(s);
			if (!transcriptModel.equals(newTranscriptModel)) {
				System.out.printf("Expected: %s%n", printTranscriptModel(transcriptModel));
				System.out.printf("But got:  %s%n", printTranscriptModel(newTranscriptModel));
			}
//			assertEquals(newTranscriptModel, transcriptModel);
		});
	}

	private String printTranscriptModel(TranscriptModel transcriptModel) {
		StringJoiner stringJoiner = new StringJoiner(",");
		stringJoiner.add(transcriptModel.getAccession());
		stringJoiner.add(transcriptModel.getGeneID());
		stringJoiner.add(transcriptModel.getGeneSymbol());
		stringJoiner.add(transcriptModel.getStrand().toString());
		stringJoiner.add(transcriptModel.getCDSRegion().toString());
		stringJoiner.add(transcriptModel.getExonRegions().toString());
		stringJoiner.add(transcriptModel.getTXRegion().toString());
		stringJoiner.add(Integer.toString(transcriptModel.getTranscriptSupportLevel()));
		stringJoiner.add(transcriptModel.getSequence());
		return stringJoiner.toString();
	}

	/**
	 * Build transcripts using a reduced data set for LTBP4 of RefSeq.  The challenge is that the
	 * transcript sequence contains an indel with respect to the reference.
	 */
	@Test public void testBuildLtbp4()
		throws IOException, TranscriptParseException, ProjectionException {
		// TODO: test with reverse transcript
		// TODO: test with NM_001017975.3 which has a leading gap
		Path dataDirectory = Paths.get("src/test/resources/build/hg19/refseq_ltbp4")
			.toAbsolutePath();

		Ini iniFile = new Ini();
		iniFile.load(
			Paths.get("src/test/resources/build/hg19/refseq_ltbp4/default_sources.ini").toFile());
		Profile.Section iniSection = iniFile.get("hg19/refseq_ltbp4");
		// CAUTION! the real RefDict is built from the download files. Having a mismatched chromosome name will
		// result in missing transcriptModels
		ReferenceDictionary refDict = new ReferenceDictParser(
			dataDirectory.resolve("chromInfo.txt.gz").toString(),
			dataDirectory.resolve("chr_accessions_GRCh37.p13").toString(), iniSection).parse();
		RefSeqParser instance = new RefSeqParser(refDict, dataDirectory.toString(),
			Collections.emptyList(), iniSection);

		ImmutableList<TranscriptModel> transcripts = instance.run();
		assertEquals(3, transcripts.size());

		// Check transcript properties.

		// NM_001042544.1
		// TODO

		// NM_001042545.1
		// TODO

		// NM_003573.2
		TranscriptModel tx2 = transcripts.get(2);
		assertEquals("NM_003573.2", tx2.getAccession());
		// Check alignment
		assertEquals(
			"Alignment{refAnchors=[Anchor{gapPos=0, seqPos=0}, Anchor{gapPos=5033, seqPos=5033}], "
				+ "qryAnchors=[Anchor{gapPos=0, seqPos=0}, Anchor{gapPos=5033, seqPos=5033}]}",
			tx2.getSeqAlignment().toString());  // TODO: will break
		// Check projection from genomic position.
		TranscriptProjectionDecorator decorator = new TranscriptProjectionDecorator(tx2);
		TranscriptPosition tx2Pos0 = decorator
			.genomeToTranscriptPos(new GenomePosition(refDict, Strand.FWD, 19, 41123090));
		assertEquals(3119, tx2Pos0.getPos());
		TranscriptPosition tx2Pos1 = decorator
			.genomeToTranscriptPos(new GenomePosition(refDict, Strand.FWD, 19, 41123091));
		assertEquals(3120, tx2Pos1.getPos());  // TODO: will break?
		TranscriptPosition tx2Pos2 = decorator
			.genomeToTranscriptPos(new GenomePosition(refDict, Strand.FWD, 19, 41123092));
		assertEquals(3121, tx2Pos2.getPos());  // TODO: will break?
		TranscriptPosition tx2Pos3 = decorator
			.genomeToTranscriptPos(new GenomePosition(refDict, Strand.FWD, 19, 41123093));
		assertEquals(3122, tx2Pos3.getPos());  // TODO: will break
	}
}
