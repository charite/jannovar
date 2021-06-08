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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.*;


/**
 * @author Jules Jacobsen <j.jacobsen@qmul.ac.uk>
 */
public class EnsemblParserTest {

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

		transcripts.stream().sorted().forEach(transcriptModel -> System.out.printf("%s %s %n", transcriptModel.getGeneSymbol(), transcriptModel));

//		STRN ENST00000263918.4(2:g.37070783_37193615)
//		STRN ENST00000379213.2(2:g.37075472_37193606)
//		STRN ENST00000495595.1(2:g.37096846_37130071)
//		SLC25A6 ENST00000381401.5(X:g.1505045_1511617)
//		SLC25A6 ENST00000475167.1(X:g.1506204_1510996)
//		SLC25A6 ENST00000484026.1(X:g.1506238_1510981)
//		SH3RF3 ENST00000309415.6(2:g.109745997_110259248)
//		SH3RF3 ENST00000418513.1(2:g.109745804_110065945)
//		SH3RF3 ENST00000444352.1(2:g.110259080_110262207)

		Assertions.assertEquals(9, transcripts.size());
		List<Integer> chromosomes = transcripts.stream().map(TranscriptModel::getChr).distinct().sorted().collect(toList());
		Assertions.assertEquals(ImmutableList.of(2, 23), chromosomes);
	}

	private void checkMatchesOldData(ImmutableList<TranscriptModel> transcripts) throws Exception {

		JannovarData oldJannovarData = new JannovarDataSerializer("src/test/resources/build/hg19/hg19_ensembl.ser").load();

		Map<String, TranscriptModel> oldTranscripts = oldJannovarData.getTmByAccession();
		Map<String, TranscriptModel> newTranscripts = transcripts.stream().collect(toMap(TranscriptModel::getAccession, Function.identity()));
		System.out.println("Num old transcripts: " + oldTranscripts.size());
		System.out.println("Num new transcripts: " + newTranscripts.size());

		Assertions.assertEquals(oldTranscripts.keySet(), newTranscripts.keySet());

		Set<TranscriptModel> missingTranscriptModels = Sets.difference(new HashSet<>(oldTranscripts.values()), new HashSet<>(newTranscripts.values()));
		if (!missingTranscriptModels.isEmpty()) {
			System.out.println(missingTranscriptModels.size() + " missing txAccessions:");
			missingTranscriptModels.forEach(tx -> {
				System.out.println(tx + " " + tx.getSequence());
			});
		}
		Assertions.assertTrue(missingTranscriptModels.isEmpty());

		Assertions.assertEquals(oldTranscripts.size(), transcripts.size());
		oldTranscripts.forEach((s, transcriptModel) -> {
			TranscriptModel newTranscriptModel = newTranscripts.get(s);
			Assertions.assertEquals(newTranscriptModel, transcriptModel);
		});
	}
}
