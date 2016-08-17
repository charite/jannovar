package de.charite.compbio.jannovar.mendel.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.mendel.GenotypeCalls;
import de.charite.compbio.jannovar.mendel.IncompatiblePedigreeException;
import de.charite.compbio.jannovar.mendel.MendelianInheritanceChecker;

/**
 * Implementation of Mendelian compatibility check for autosomal recessive case
 * 
 * <h2>Compatibility Check</h2>
 *
 * This class merely delegates to the {@link MendelianCheckerXRHom} and {@link MendelianCheckerXRCompoundHet}. The
 * {@link GenotypeCalls} objects passing either filter will be returned.
 * 
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class MendelianCheckerXR extends AbstractMendelianChecker {

	final private MendelianCheckerXRCompoundHet checkerCompound;
	final private MendelianCheckerXRHom checkerHom;

	public MendelianCheckerXR(MendelianInheritanceChecker parent) {
		super(parent);

		this.checkerCompound = new MendelianCheckerXRCompoundHet(parent);
		this.checkerHom = new MendelianCheckerXRHom(parent);
	}

	@Override
	public ImmutableList<GenotypeCalls> filterCompatibleRecords(Collection<GenotypeCalls> calls)
			throws IncompatiblePedigreeException {
		// Apply homozygous and compound heterozygous checker, then select distinct records
		Stream<GenotypeCalls> joint = Stream.concat(checkerCompound.filterCompatibleRecords(calls).stream(),
				checkerHom.filterCompatibleRecords(calls).stream());
		HashSet<GenotypeCalls> set = new HashSet<>();
		set.addAll(joint.collect(Collectors.toList()));
		return ImmutableList.copyOf(set);
	}

}
