package de.charite.compbio.jannovar.mendel.impl;

import java.util.Collection;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.mendel.GenotypeCalls;
import de.charite.compbio.jannovar.mendel.IncompatiblePedigreeException;
import de.charite.compbio.jannovar.mendel.MendelianInheritanceChecker;
import de.charite.compbio.jannovar.pedigree.Pedigree;
import de.charite.compbio.jannovar.pedigree.PedigreeQueryDecorator;

// TODO: check compatibility of pedigree with GenotypeCalls

/**
 * Abstract base class for mendelian checkers
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public abstract class AbstractMendelianChecker {

	/** The owning {@link MendelianInheritanceChecker} with the pedigree etc. */
	final protected MendelianInheritanceChecker parent;
	/** The pedigree from the parent */
	final protected Pedigree pedigree;
	/** The pedigree query helper */
	final protected PedigreeQueryDecorator queryDecorator;

	public AbstractMendelianChecker(MendelianInheritanceChecker parent) {
		this.parent = parent;
		this.pedigree = parent.getPedigree();
		this.queryDecorator = new PedigreeQueryDecorator(this.pedigree);
	}

	/**
	 * Filter list of {@link GenotypeCalls} for fitting to mode
	 * 
	 * @param calls
	 *            The list of calls to check for compatibility
	 * @return Filtered {@link ImmutableList} of {@link GenotypeCalls} objects, subset of <code>calls</code>
	 * @throws IncompatiblePedigreeException
	 *             if <code>calls</code> is incompatible with the pedigree
	 */
	public abstract ImmutableList<GenotypeCalls> filterCompatibleRecords(Collection<GenotypeCalls> calls)
			throws IncompatiblePedigreeException;

}
