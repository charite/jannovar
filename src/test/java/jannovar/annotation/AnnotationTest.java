package jannovar.annotation;

import jannovar.exception.JannovarException;

/**
 * Base class for annotation tests.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class AnnotationTest {
	/** Simple creation of variant contexts. */
	protected VariantContextGenerator variantContextGenerator = null;

	public void setUp() throws JannovarException {
		variantContextGenerator = new VariantContextGenerator();
	}

	public void tearDown() throws JannovarException {
		if (variantContextGenerator != null)
			variantContextGenerator.close();
	}
}
