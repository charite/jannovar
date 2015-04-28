package de.charite.compbio.jped;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.vcf.VCFFileReader;
import de.charite.compbio.jannovar.JannovarException;
import de.charite.compbio.jannovar.data.JannovarData;
import de.charite.compbio.jannovar.filter.CoordinateSortChecker;
import de.charite.compbio.jannovar.filter.FilterException;
import de.charite.compbio.jannovar.filter.FlaggedVariant;
import de.charite.compbio.jannovar.filter.GeneWiseInheritanceFilter;
import de.charite.compbio.jannovar.filter.VariantContextFilter;
import de.charite.compbio.jannovar.filter.VariantWiseInheritanceFilter;
import de.charite.compbio.jannovar.filter.WriterFilter;
import de.charite.compbio.jannovar.pedigree.ModeOfInheritance;
import de.charite.compbio.jannovar.pedigree.Pedigree;

public class FilteredWriter {

	/** Pedigree */
	private final Pedigree pedigree;
	/** Jannovar DB */
	private final JannovarData jannovarDB;
	/** mode of inheritance */
	private final ModeOfInheritance modeOfInheritance;
	/** source of {@link VariantContext} objects */
	private final VCFFileReader source;
	/** sink for {@link VariantContext} objects */
	private final VariantContextWriter sink;

	public FilteredWriter(Pedigree pedigree, ModeOfInheritance modeOfInheritance, JannovarData jannovarDB,
			VCFFileReader source, VariantContextWriter sink) {
		this.pedigree = pedigree;
		this.modeOfInheritance = modeOfInheritance;
		this.jannovarDB = jannovarDB;
		this.source = source;
		this.sink = sink;
	}

	void run(JPedOptions options) throws JannovarException {
		// public GeneWiseInheritanceFilter(Pedigree pedigree, JannovarData jannovarDB, ModeOfInheritance
		// modeOfInheritance,
		// ImmutableList<String> names, VariantContextFilter next) {

		VariantContextFilter topFilter = new WriterFilter(sink);
		if (options.geneWise)
			topFilter = new GeneWiseInheritanceFilter(pedigree, jannovarDB, modeOfInheritance, topFilter);
		else
			topFilter = new VariantWiseInheritanceFilter(pedigree, jannovarDB, modeOfInheritance, topFilter);
		topFilter = new CoordinateSortChecker(topFilter);

		try {
			for (VariantContext vc : source)
				topFilter.put(new FlaggedVariant(vc));
			topFilter.finish();
		} catch (FilterException e) {
			throw new JannovarException("Problem in the filtration.", e);
		}
	}

}
