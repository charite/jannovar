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
		Assertions.assertEquals("", translator.translateDNA("A", true));
		Assertions.assertEquals("", translator.translateDNA("AC", true));
		Assertions.assertEquals("", translator.translateDNA("A", false));
		Assertions.assertEquals("", translator.translateDNA("AC", false));
	}

	/**
	 * Test for translateDNA() with short input
	 */
	@Test
	public void testTranslateDna_short() throws AnnotationException {
		Assertions.assertEquals("T", translator.translateDNA("ACT", true));
		Assertions.assertEquals("T", translator.translateDNA("ACT", false));
	}

	/**
	 * Test for translateDNA() with longer input (ignore remainder)
	 */
	@Test
	public void testTranslateDna_longer() throws AnnotationException {
		Assertions.assertEquals("M*S", translator.translateDNA("ATGTAGAGT", true));
		Assertions.assertEquals("**S", translator.translateDNA("TGATAGAGT", true));
		Assertions.assertEquals("W*S", translator.translateDNA("TGATAGAGT", false));
	}

	/**
	 * Test for translateDNA() with too long input (ignore remainder)
	 */
	@Test
	public void testTranslateDna_tooLonger() throws AnnotationException {
		Assertions.assertEquals("T", translator.translateDNA("ACTG", true));
		Assertions.assertEquals("*", translator.translateDNA("TGAT", true));
		Assertions.assertEquals("W", translator.translateDNA("TGAT", false));
	}

	/**
	 * Test for mitochondrial code exceptions (1 character)
	 */
	@Test
	public void testTranslateChrMtExceptions_short() throws AnnotationException {
		Assertions.assertEquals("R", translator.translateDNA("AGA", true));
		Assertions.assertEquals("R", translator.translateDNA("AGG", true));
		Assertions.assertEquals("I", translator.translateDNA("ATA", true));
		Assertions.assertEquals("*", translator.translateDNA("TGA", true));

		Assertions.assertEquals("*", translator.translateDNA("AGA", false));
		Assertions.assertEquals("*", translator.translateDNA("AGG", false));
		Assertions.assertEquals("M", translator.translateDNA("ATA", false));
		Assertions.assertEquals("W", translator.translateDNA("TGA", false));
	}

	/**
	 * Test for mitochondrial code exceptions (3 characters)
	 */
	@Test
	public void testTranslateChrMtExceptions_long() throws AnnotationException {
		Assertions.assertEquals("Arg", translator.translateDNA3("AGA", true));
		Assertions.assertEquals("Arg", translator.translateDNA3("AGG", true));
		Assertions.assertEquals("Ile", translator.translateDNA3("ATA", true));
		Assertions.assertEquals("*", translator.translateDNA3("TGA", true));

		Assertions.assertEquals("*", translator.translateDNA3("AGA", false));
		Assertions.assertEquals("*", translator.translateDNA3("AGG", false));
		Assertions.assertEquals("Met", translator.translateDNA3("ATA", false));
		Assertions.assertEquals("Trp", translator.translateDNA3("TGA", false));
	}
}
