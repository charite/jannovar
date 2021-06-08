package de.charite.compbio.jannovar.impl.util;

import de.charite.compbio.jannovar.annotation.AnnotationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test for the TranscriptModel class.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public class TranslatorTest {

	Translator translator;

	@BeforeEach
	public void setUp() {
		translator = Translator.getTranslator();
	}

	/**
	 * Test for translateDNA() with too short input
	 */
	@Test
	public void testTranslateDna_tooShort() throws AnnotationException {
		Assertions.assertEquals("", translator.translateDNA("A"));
		Assertions.assertEquals("", translator.translateDNA("AC"));
	}

	/**
	 * Test for translateDNA() with short input
	 */
	@Test
	public void testTranslateDna_short() throws AnnotationException {
		Assertions.assertEquals("T", translator.translateDNA("ACT"));
	}

	/**
	 * Test for translateDNA() with longer input (ignore remainder)
	 */
	@Test
	public void testTranslateDna_longer() throws AnnotationException {
		Assertions.assertEquals("M*S", translator.translateDNA("ATGTAGAGT"));
	}

	/**
	 * Test for translateDNA() with too long input (ignore remainder)
	 */
	@Test
	public void testTranslateDna_tooLonger() throws AnnotationException {
		Assertions.assertEquals("T", translator.translateDNA("ACTG"));
	}
}
