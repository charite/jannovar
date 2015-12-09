package de.charite.compbio.jannovar.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;

import de.charite.compbio.jannovar.data.JannovarData;
import de.charite.compbio.jannovar.data.JannovarDataSerializer;
import de.charite.compbio.jannovar.data.ReferenceDictionary;
import de.charite.compbio.jannovar.data.ReferenceDictionaryBuilder;
import de.charite.compbio.jannovar.data.SerializationException;
import de.charite.compbio.jannovar.impl.parse.RefSeqFastaParser;
import de.charite.compbio.jannovar.impl.parse.gff.FeatureProcessor;
import de.charite.compbio.jannovar.impl.parse.gff.GFFParser;
import de.charite.compbio.jannovar.impl.parse.gff.GFFVersion;
import de.charite.compbio.jannovar.impl.parse.gff.TranscriptModelBuilderFactory;
import de.charite.compbio.jannovar.reference.TranscriptModel;
import de.charite.compbio.jannovar.reference.TranscriptModelBuilder;

public class BuildExampleJannovarDB {

	public static void main(String args[]) throws FileNotFoundException, IOException, SerializationException {
		build("fbn1");
		build("ctns");
	}

	private static void build(String name) throws FileNotFoundException, IOException, SerializationException {
		// create temporary directory
		File tmpDir = Files.createTempDir();
		// copy files
		ResourceUtils.copyResourceToFile("/ex_" + name + "/transcript.gff3", new File(tmpDir + "/transcript.gff3"));
		ResourceUtils.copyResourceToFile("/ex_" + name + "/ref.fa", new File(tmpDir + "/ref.fa"));
		ResourceUtils.copyResourceToFile("/ex_" + name + "/rna.fa", new File(tmpDir + "/rna.fa"));
		// build ReferenceDictionary
		ReferenceDictionaryBuilder refDictBuilder = new ReferenceDictionaryBuilder();
		refDictBuilder.putContigID("ref", 1);
		refDictBuilder.putContigLength(1, 500001);
		refDictBuilder.putContigName(1, "ref");
		ReferenceDictionary refDict = refDictBuilder.build();
		// parse TranscriptModel
		GFFParser gffParser = new GFFParser(tmpDir + "/transcript.gff3", new GFFVersion(3), false);
		FeatureProcessor fp = new FeatureProcessor(gffParser.getGffVersion(), false, refDict);
		gffParser.parse(fp);
		// build ArrayList of TranscriptModelBuilder objects from feature list
		ArrayList<TranscriptModelBuilder> builders;
		TranscriptModelBuilderFactory tif = new TranscriptModelBuilderFactory(false, gffParser.getGffVersion(), refDict);
		builders = tif.buildTranscriptModelBuilders(fp.getGenes(), false);
		builders = new RefSeqFastaParser(tmpDir + "/rna.fa", builders, false).parse();
		// create final list of TranscriptInfos
		ImmutableList.Builder<TranscriptModel> tmListBuilder = new ImmutableList.Builder<TranscriptModel>();
		for (TranscriptModelBuilder builder : builders)
			tmListBuilder.add(builder.build());
		JannovarData data = new JannovarData(refDict, tmListBuilder.build());
		// write out file
		new JannovarDataSerializer("mini_" + name + ".ser").save(data);
	}

}
