package de.charite.compbio.jannovar.vardbs.base;

/**
 * Interface for annotation drivers by variant databases.
 * 
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public interface DBAnnotationDriver {

	/** @return The {@link VCFHeaderExtender} to use.
	 */
	VCFHeaderExtender constructVCFHeaderExtender();
	
}
