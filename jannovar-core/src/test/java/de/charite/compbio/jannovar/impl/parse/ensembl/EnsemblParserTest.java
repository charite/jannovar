package de.charite.compbio.jannovar.impl.parse.ensembl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import de.charite.compbio.jannovar.data.JannovarData;
import de.charite.compbio.jannovar.data.JannovarDataSerializer;
import de.charite.compbio.jannovar.data.ReferenceDictionary;
import de.charite.compbio.jannovar.reference.HG19RefDictBuilder;
import de.charite.compbio.jannovar.reference.TranscriptModel;
import org.ini4j.Ini;
import org.ini4j.Profile;
import org.junit.Ignore;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Jules Jacobsen <j.jacobsen@qmul.ac.uk>
 */
public class EnsemblParserTest {

	@Ignore
	@Test
	public void testRun() throws Exception {

		Path dataDirectory = Paths.get("src/test/resources/build/hg19/ensembl");

		Ini iniFile = new Ini();
		iniFile.load(Paths.get("src/test/resources/build/default_sources.ini").toFile());
		Profile.Section iniSection = iniFile.get("hg19/ensembl");
		// CAUTION! the real RefDict is built from the download files. Having a mismatched chromosome name will
		// result in missing transcriptModels
		ReferenceDictionary refDict = HG19RefDictBuilder.build();
		EnsemblParser instance = new EnsemblParser(refDict, dataDirectory.toAbsolutePath().toString(), Collections.emptyList(), iniSection);
		ImmutableList<TranscriptModel> transcripts = instance.run();

		// uncomment this for manual debug
		checkMatchesOldData(transcripts);

		assertEquals(163468, transcripts.size());
		assertEquals(25L, transcripts.stream().map(TranscriptModel::getChr).distinct().count());
	}

	private void checkMatchesOldData(ImmutableList<TranscriptModel> transcripts) throws Exception {

		JannovarData oldJannovarData = new JannovarDataSerializer("src/test/resources/build/hg19/hg19_ensembl.ser").load();

		Map<String, TranscriptModel> oldTranscripts = oldJannovarData.getTmByAccession();
		Map<String, TranscriptModel> newTranscripts = transcripts.stream().collect(toMap(TranscriptModel::getAccession, Function.identity()));
		System.out.println("Num old transcripts: " + oldTranscripts.size());
		System.out.println("Num new transcripts: " + newTranscripts.size());

		assertEquals(oldTranscripts.keySet(), newTranscripts.keySet());

		Set<TranscriptModel> missingTranscriptModels = Sets.difference(new HashSet<>(oldTranscripts.values()), new HashSet<>(newTranscripts.values()));
		if (!missingTranscriptModels.isEmpty()) {
			System.out.println(missingTranscriptModels.size() + " missing txAccessions:");
			missingTranscriptModels.forEach(tx -> {
				System.out.println(tx + " " + tx.getSequence());
			});
		}
		assertTrue(missingTranscriptModels.isEmpty());

		assertEquals(oldTranscripts.size(), transcripts.size());
		oldTranscripts.forEach((s, transcriptModel) -> {
			TranscriptModel newTranscriptModel = newTranscripts.get(s);
			assertEquals(newTranscriptModel, transcriptModel);
		});
	}
}
