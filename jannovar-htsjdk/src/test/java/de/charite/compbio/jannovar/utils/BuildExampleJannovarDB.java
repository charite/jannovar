package de.charite.compbio.jannovar.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.ini4j.Ini;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;

import de.charite.compbio.jannovar.data.JannovarData;
import de.charite.compbio.jannovar.data.JannovarDataSerializer;
import de.charite.compbio.jannovar.data.ReferenceDictionary;
import de.charite.compbio.jannovar.data.ReferenceDictionaryBuilder;
import de.charite.compbio.jannovar.data.SerializationException;
import de.charite.compbio.jannovar.impl.parse.TranscriptParseException;
import de.charite.compbio.jannovar.impl.parse.refseq.RefSeqParser;
import de.charite.compbio.jannovar.reference.TranscriptModel;

public class BuildExampleJannovarDB {

	public static void main(String args[])
			throws FileNotFoundException, IOException, SerializationException, TranscriptParseException {
		build("fbn1");
		build("ctns");
	}

	private static void build(String name)
			throws FileNotFoundException, IOException, SerializationException, TranscriptParseException {
		// create temporary directory
		File tmpDir = Files.createTempDir();
		// copy files
		ResourceUtils.copyResourceToFile("/ex_" + name + "/transcript.gff3", new File(tmpDir + "/transcript.gff3"));
		ResourceUtils.copyResourceToFile("/ex_" + name + "/hgnc_complete_set.txt", new File(tmpDir + "/hgnc_complete_set.txt"));
		ResourceUtils.copyResourceToFile("/ex_" + name + "/ref.fa", new File(tmpDir + "/ref.fa"));
		ResourceUtils.copyResourceToFile("/ex_" + name + "/rna.fa", new File(tmpDir + "/rna.fa"));
		ResourceUtils.copyResourceToFile("/ex_" + name + "/data.ini", new File(tmpDir + "/data.ini"));
		// build ReferenceDictionary
		ReferenceDictionaryBuilder refDictBuilder = new ReferenceDictionaryBuilder();
		refDictBuilder.putContigID("ref", 1);
		refDictBuilder.putContigLength(1, 500001);
		refDictBuilder.putContigName(1, "ref");
		ReferenceDictionary refDict = refDictBuilder.build();
		// parse TranscriptModel
		Ini ini = new Ini();
		ini.load(new File(tmpDir + "/data.ini"));
		RefSeqParser parser = new RefSeqParser(refDict, tmpDir.toString(), ini.get("dummy"));
		ImmutableList<TranscriptModel> tms = parser.run();
		JannovarData data = new JannovarData(refDict, tms);
		// write out file
		new JannovarDataSerializer("mini_" + name + ".ser").save(data);
	}

}
