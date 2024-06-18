/* Created on 19.07.2019 */
package de.bernd_michaely.common.resources.itests;

import de.bernd_michaely.common.resources.ResourceLoader;
import de.bernd_michaely.common.resources.itest.resourceholders.TestProcessingSuccessful;
import java.util.Locale;
import java.util.function.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for ResourceLoader.
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 */
public class ResourceLoaderITest
{
	private ResourceLoader resourceLoader;
	private String value;
	private Locale locale;

	private final Consumer<TestProcessingSuccessful> callback = resourceHolder ->
	{
		value = resourceHolder.labelValid1;
		locale = resourceHolder.getLocale();
	};

	private void clear()
	{
		this.value = null;
		this.locale = null;
	}

	@BeforeEach
	public void setUp() throws Exception
	{
		this.resourceLoader = new ResourceLoader();
		clear();
	}

	private void check(String value, Locale locale)
	{
		assertEquals(value, this.value);
		assertEquals(locale, this.locale);
	}

	@Test
	public void testConstructorAndLocale()
	{
		System.out.println("testConstructorAndLocale");
		assertEquals(Locale.ROOT, resourceLoader.getLocale());
		resourceLoader.setLocale(Locale.ENGLISH);
		assertEquals(Locale.ENGLISH, resourceLoader.getLocale());
		resourceLoader.setLocale(null);
		assertEquals(Locale.ROOT, resourceLoader.getLocale());
	}

	@Test
	public void testRegister()
	{
		System.out.println("testRegister");
		assertFalse(resourceLoader.register(TestProcessingSuccessful.class, callback));
		check("String resource 1", Locale.ROOT);
		clear();
		resourceLoader.setLocale(Locale.ENGLISH);
		check("String resource one", Locale.ENGLISH);
	}

	@Test
	public void testUnregister()
	{
		System.out.println("testUnregister");
		assertFalse(resourceLoader.register(TestProcessingSuccessful.class, callback));
		check("String resource 1", Locale.ROOT);
		clear();
		assertTrue(resourceLoader.unregister(callback));
		resourceLoader.setLocale(Locale.ENGLISH);
		check(null, null);
	}
}
