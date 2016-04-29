package de.charite.compbio.jannovar.impl.parse.refseq;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.ini4j.Ini;
import org.ini4j.Profile.Section;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import de.charite.compbio.jannovar.data.ReferenceDictionary;
import de.charite.compbio.jannovar.impl.parse.TranscriptParseException;
import de.charite.compbio.jannovar.reference.HG19RefDictBuilder;
import de.charite.compbio.jannovar.reference.TranscriptModel;

import org.junit.After;
import org.junit.Assert;

public class RefSeqParserTest {

	/** this test uses this static hg19 reference dictionary */
	static final ReferenceDictionary refDict = HG19RefDictBuilder.build();

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
		RefSeqParser parser = new RefSeqParser(refDict, dataDirectory.getAbsolutePath(), allIniSection);
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

		Assert.assertEquals("10658", result.get(0).getGeneID());
		Assert.assertEquals("CELF1", result.get(0).getGeneSymbol());
		Assert.assertEquals("{GeneID=10658, HGNC=2549, HPRD=03046, MIM=601074}",
				result.get(0).getAltGeneIDs().toString());
	}

	@Test
	public void testOnlyCurated() throws TranscriptParseException {
		RefSeqParser parser = new RefSeqParser(refDict, dataDirectory.getAbsolutePath(), curatedIniSection);
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
	}

}
