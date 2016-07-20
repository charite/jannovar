package de.charite.compbio.jannovar.mendel;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import de.charite.compbio.jannovar.Immutable;
import de.charite.compbio.jannovar.mendel.impl.AbstractMendelianChecker;
import de.charite.compbio.jannovar.mendel.impl.MendelianCheckerAD;
import de.charite.compbio.jannovar.mendel.impl.MendelianCheckerAR;
import de.charite.compbio.jannovar.mendel.impl.MendelianCheckerXD;
import de.charite.compbio.jannovar.mendel.impl.MendelianCheckerXR;
import de.charite.compbio.jannovar.pedigree.Pedigree;
import de.charite.compbio.jannovar.pedigree.PedigreeQueryDecorator;

/**
 * Facade class for checking lists of {@link GenotypeCalls} for compatibility with mendelian inheritance
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
@Immutable
public final class MendelianInheritanceChecker {

	/** Pedigree to use for mendelian inheritance checking */
	final private Pedigree pedigree;
	/** Helper for querying a pedigree */
	final private PedigreeQueryDecorator queryPed;
	/** Mendelian compatibility checker for each mode of inheritance */
	final private ImmutableMap<ModeOfInheritance, AbstractMendelianChecker> checkers;

	/**
	 * Construct checker with the pedigree to use
	 * 
	 * @param pedigree
	 *            The pedigree to use for the mendelian inheritance checking
	 */
	public MendelianInheritanceChecker(Pedigree pedigree) {
		this.pedigree = pedigree;
		this.queryPed = new PedigreeQueryDecorator(pedigree);

		ImmutableMap.Builder<ModeOfInheritance, AbstractMendelianChecker> builder = new ImmutableMap.Builder<>();
		builder.put(ModeOfInheritance.AUTOSOMAL_DOMINANT, new MendelianCheckerAD(this));
		builder.put(ModeOfInheritance.AUTOSOMAL_RECESSIVE, new MendelianCheckerAR(this));
		builder.put(ModeOfInheritance.X_DOMINANT, new MendelianCheckerXD(this));
		builder.put(ModeOfInheritance.X_RECESSIVE, new MendelianCheckerXR(this));
		this.checkers = builder.build();
	}

	/**
	 * Perform checking for compatible mode of inheritance
	 * 
	 * @param calls
	 *            {@link Collection} of {@link GenotypeCalls} objects to perform the mode of inheritance check for
	 * @return {@link Map} that, for each {@link ModeOfInheritance}, contains the {@link Collection} of compatible
	 *         {@link GenotypeCalls} from <code>list</code>
	 * @throws IncompatiblePedigreeException
	 *             if the individuals in <code>calls</code> do not fit to the pedigree
	 */
	public ImmutableMap<ModeOfInheritance, ImmutableList<GenotypeCalls>> checkMendelianInheritance(
			Collection<GenotypeCalls> calls) throws IncompatiblePedigreeException {
		ImmutableMap.Builder<ModeOfInheritance, ImmutableList<GenotypeCalls>> builder = new ImmutableMap.Builder<>();
		for (ModeOfInheritance mode : ModeOfInheritance.values())
			if (mode != ModeOfInheritance.ANY)
				builder.put(mode, filterCompatibleRecords(calls, mode));
			else
				builder.put(mode, ImmutableList.copyOf(calls));
		return builder.build();
	}

	/**
	 * Filters records in <code>calls</code> for compatibility with <code>mode</code>
	 * 
	 * @param calls
	 *            List of {@link GenotypeCalls} to filter
	 * @param mode
	 *            {@link ModeOfInheritance} to check for
	 * @return List of {@link GenotypeCalls} from <code>calls</code> that are compatible with <code>mode</code>
	 * @throws IncompatiblePedigreeException
	 *             if the individuals in <code>calls</code> do not fit to the pedigree
	 */
	private ImmutableList<GenotypeCalls> filterCompatibleRecords(Collection<GenotypeCalls> calls,
			ModeOfInheritance mode) throws IncompatiblePedigreeException {
		if (mode == ModeOfInheritance.ANY)
			return ImmutableList.copyOf(calls);
		else
			return checkers.get(mode).filterCompatibleRecords(calls);
	}

	/** @return {@link Pedigree} to use */
	public Pedigree getPedigree() {
		return pedigree;
	}

}
