package de.charite.compbio.jannovar.impl.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.charite.compbio.jannovar.annotation.AnnotationException;
import de.charite.compbio.jannovar.impl.util.Translator;

/**
 * Test for the TranscriptModel class.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class TranslatorTest {

	Translator translator;

	@Before
	public void setUp() {
		translator = Translator.getTranslator();
	}

	/** Test for translateDNA() with too short input */
	@Test
	public void testTranslateDna_tooShort() throws AnnotationException {
		Assert.assertEquals("", translator.translateDNA("A"));
		Assert.assertEquals("", translator.translateDNA("AC"));
	}

	/** Test for translateDNA() with short input */
	@Test
	public void testTranslateDna_short() throws AnnotationException {
		Assert.assertEquals("T", translator.translateDNA("ACT"));
	}

	/** Test for translateDNA() with longer input (ignore remainder) */
	@Test
	public void testTranslateDna_longer() throws AnnotationException {
		Assert.assertEquals("M*S", translator.translateDNA("ATGTAGAGT"));
	}

	/** Test for translateDNA() with too long input (ignore remainder) */
	@Test
	public void testTranslateDna_tooLonger() throws AnnotationException {
		Assert.assertEquals("T", translator.translateDNA("ACTG"));
	}
}
