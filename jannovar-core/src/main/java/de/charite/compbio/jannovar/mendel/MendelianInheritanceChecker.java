package de.charite.compbio.jannovar.mendel;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import de.charite.compbio.jannovar.Immutable;
import de.charite.compbio.jannovar.mendel.impl.AbstractMendelianChecker;
import de.charite.compbio.jannovar.mendel.impl.MendelianCheckerAD;
import de.charite.compbio.jannovar.mendel.impl.MendelianCheckerARCompoundHet;
import de.charite.compbio.jannovar.mendel.impl.MendelianCheckerARHom;
import de.charite.compbio.jannovar.mendel.impl.MendelianCheckerXD;
import de.charite.compbio.jannovar.mendel.impl.MendelianCheckerXRCompoundHet;
import de.charite.compbio.jannovar.mendel.impl.MendelianCheckerXRHom;
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
	/** Mendelian compatibility checker for each sub mode of inheritance */
	final private ImmutableMap<SubModeOfInheritance, AbstractMendelianChecker> checkers;

	/**
	 * Construct checker with the pedigree to use
	 * 
	 * @param pedigree
	 *            The pedigree to use for the mendelian inheritance checking
	 */
	public MendelianInheritanceChecker(Pedigree pedigree) {
		this.pedigree = pedigree;
		this.queryPed = new PedigreeQueryDecorator(pedigree);

		ImmutableMap.Builder<SubModeOfInheritance, AbstractMendelianChecker> builder = new ImmutableMap.Builder<>();
		builder.put(SubModeOfInheritance.AUTOSOMAL_DOMINANT, new MendelianCheckerAD(this));
		builder.put(SubModeOfInheritance.AUTOSOMAL_RECESSIVE_COMP_HET, new MendelianCheckerARCompoundHet(this));
		builder.put(SubModeOfInheritance.AUTOSOMAL_RECESSIVE_HOM_ALT, new MendelianCheckerARHom(this));
		builder.put(SubModeOfInheritance.X_DOMINANT, new MendelianCheckerXD(this));
		builder.put(SubModeOfInheritance.X_RECESSIVE_COMP_HET, new MendelianCheckerXRCompoundHet(this));
		builder.put(SubModeOfInheritance.X_RECESSIVE_HOM_ALT, new MendelianCheckerXRHom(this));
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
	 * Perform checking for compatible sub mode of inheritance
	 * 
	 * @param calls
	 *            {@link Collection} of {@link GenotypeCalls} objects to perform the mode of inheritance check for
	 * @return {@link Map} that, for each {@link SubModeOfInheritance}, contains the {@link Collection} of compatible
	 *         {@link GenotypeCalls} from <code>list</code>
	 * @throws IncompatiblePedigreeException
	 *             if the individuals in <code>calls</code> do not fit to the pedigree
	 */
	public ImmutableMap<SubModeOfInheritance, ImmutableList<GenotypeCalls>> checkMendelianInheritanceSub(
			Collection<GenotypeCalls> calls) throws IncompatiblePedigreeException {
		ImmutableMap.Builder<SubModeOfInheritance, ImmutableList<GenotypeCalls>> builder = new ImmutableMap.Builder<>();
		for (SubModeOfInheritance mode : SubModeOfInheritance.values())
			if (mode != SubModeOfInheritance.ANY)
				builder.put(mode, filterCompatibleRecordsSub(calls, mode));
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
	public ImmutableList<GenotypeCalls> filterCompatibleRecords(Collection<GenotypeCalls> calls, ModeOfInheritance mode)
			throws IncompatiblePedigreeException {
		// Check for compatibility of calls with pedigree
		if (!calls.stream().allMatch(c -> isCompatibleWithPedigree(c)))
			throw new IncompatiblePedigreeException("GenotypeCalls not compatible with pedigree");
		// Filter down to the compatible records
		ImmutableSet<GenotypeCalls> calls1, calls2;
		ImmutableList.Builder<GenotypeCalls> builder;
		switch (mode) {
		case AUTOSOMAL_DOMINANT:
			return checkers.get(SubModeOfInheritance.AUTOSOMAL_DOMINANT).filterCompatibleRecords(calls);
		case AUTOSOMAL_RECESSIVE:
			calls1 = ImmutableSet.copyOf(
					checkers.get(SubModeOfInheritance.AUTOSOMAL_RECESSIVE_HOM_ALT).filterCompatibleRecords(calls));
			calls2 = ImmutableSet.copyOf(
					checkers.get(SubModeOfInheritance.AUTOSOMAL_RECESSIVE_COMP_HET).filterCompatibleRecords(calls));
			builder = new ImmutableList.Builder<>();
			for (GenotypeCalls c : calls)
				if (calls1.contains(c) || calls2.contains(c))
					builder.add(c);
			return builder.build();
		case X_DOMINANT:
			return checkers.get(SubModeOfInheritance.X_DOMINANT).filterCompatibleRecords(calls);
		case X_RECESSIVE:
			calls1 = ImmutableSet
					.copyOf(checkers.get(SubModeOfInheritance.X_RECESSIVE_HOM_ALT).filterCompatibleRecords(calls));
			calls2 = ImmutableSet
					.copyOf(checkers.get(SubModeOfInheritance.X_RECESSIVE_COMP_HET).filterCompatibleRecords(calls));
			builder = new ImmutableList.Builder<>();
			for (GenotypeCalls c : calls)
				if (calls1.contains(c) || calls2.contains(c))
					builder.add(c);
			return builder.build();
		default:
		case ANY:
			return ImmutableList.copyOf(calls);
		}
	}

	/**
	 * Filters records in <code>calls</code> for compatibility with <code>subMode</code>
	 * 
	 * @param calls
	 *            List of {@link GenotypeCalls} to filter
	 * @param subMode
	 *            {@link SubModeOfInheritance} to check for
	 * @return List of {@link GenotypeCalls} from <code>calls</code> that are compatible with <code>mode</code>
	 * @throws IncompatiblePedigreeException
	 *             if the individuals in <code>calls</code> do not fit to the pedigree
	 */
	public ImmutableList<GenotypeCalls> filterCompatibleRecordsSub(Collection<GenotypeCalls> calls,
			SubModeOfInheritance subMode) throws IncompatiblePedigreeException {
		// Check for compatibility of calls with pedigree
		if (!calls.stream().allMatch(c -> isCompatibleWithPedigree(c)))
			throw new IncompatiblePedigreeException("GenotypeCalls not compatible with pedigree");
		// Filter down to the compatible records
		if (subMode == SubModeOfInheritance.ANY)
			return ImmutableList.copyOf(calls);
		else
			return checkers.get(subMode).filterCompatibleRecords(calls);
	}

	/** @return {@link Pedigree} to use */
	public Pedigree getPedigree() {
		return pedigree;
	}

	/**
	 * @return <code>true</code> if <code>call</code> is compatible with this pedigree
	 */
	private boolean isCompatibleWithPedigree(GenotypeCalls calls) {
		return pedigree.getNames().containsAll(calls.getSampleNames());
	}

}
