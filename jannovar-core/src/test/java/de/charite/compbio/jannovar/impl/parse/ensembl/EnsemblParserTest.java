package de.charite.compbio.jannovar.impl.parse.ensembl;

import com.google.common.collect.ImmutableList;
import de.charite.compbio.jannovar.Immutable;
import de.charite.compbio.jannovar.data.ReferenceDictionary;
import de.charite.compbio.jannovar.datasource.DataSource;
import de.charite.compbio.jannovar.datasource.JannovarDataFactory;
import de.charite.compbio.jannovar.reference.HG19RefDictBuilder;
import de.charite.compbio.jannovar.reference.TranscriptModel;
import org.ini4j.Ini;
import org.ini4j.Profile;
import org.junit.Test;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

import static org.junit.Assert.*;

/**
 * @author Jules Jacobsen <j.jacobsen@qmul.ac.uk>
 */
public class EnsemblParserTest {

	@Test
	public void testRun() throws Exception {
		ReferenceDictionary refDict = HG19RefDictBuilder.build();

		InputStream stream;
		Path dataDirectory = Paths.get("src/test/resources/build/hg19/ensembl");
		Ini iniFile = new Ini();
		iniFile.load(Paths.get("src/test/resources/build/default_sources.ini").toFile());
		Profile.Section iniSection = iniFile.get("hg19/ensembl");

		EnsemblParser instance = new EnsemblParser(refDict, dataDirectory.toAbsolutePath().toString(), Collections.emptyList(), iniSection);
		ImmutableList<TranscriptModel> transcripts = instance.run();
	}
}
