package de.charite.compbio.jannovar.filter;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import de.charite.compbio.jannovar.pedigree.ModeOfInheritance;
import de.charite.compbio.jannovar.pedigree.Pedigree;
import de.charite.compbio.jannovar.pedigree.Person;
import de.charite.compbio.jannovar.pedigree.compatibilitychecker.CompatibilityCheckerException;
import de.charite.compbio.jannovar.pedigree.compatibilitychecker.InheritanceCompatibilityChecker;
import htsjdk.variant.variantcontext.VariantContext;

/**
 * A {@link VariantContext} filter that checks each variant individually for compatibility.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class VariantWiseInheritanceFilter implements VariantContextFilter {

	/** the logger object to use */
	private static final Logger LOGGER = LoggerFactory.getLogger(VariantWiseInheritanceFilter.class);

	/** Next filter. */
	private final VariantContextFilter next;
	/** Compatibility checker for genotype call lists and {@link #pedigree}. */
	private final InheritanceCompatibilityChecker checker;

	/** Initialize */
	public VariantWiseInheritanceFilter(Pedigree pedigree, ImmutableSet<ModeOfInheritance> modeOfInheritances,
			VariantContextFilter next) {
		this.next = next;
		this.checker = new InheritanceCompatibilityChecker.Builder().pedigree(pedigree).addModes(modeOfInheritances)
				.build();

		ImmutableList.Builder<String> namesBuilder = new ImmutableList.Builder<String>();
		for (Person p : pedigree.getMembers())
			namesBuilder.add(p.getName());
	}

	public void put(FlaggedVariant fv) throws FilterException {
		// check gene for compatibility and mark variants as compatible if so
		List<VariantContext> list = new ArrayList<VariantContext>();
		list.add(fv.getVC());
		try {

			fv.setIncluded(!checker.getCompatibleWith(list).isEmpty());
			if (fv.isIncluded())
				next.put(fv);
			LOGGER.trace("Variant {} compatible with {} (var={})", new Object[] { fv.isIncluded() ? "" : "in",
					Joiner.on(", ").join(checker.getInheritanceModes()), fv.getVC() });
		} catch (CompatibilityCheckerException e) {
			throw new FilterException("Problem in mode of inheritance filter.", e);
		}
	}

	public void finish() throws FilterException {
		next.finish();
	}

}
