package de.bernd_michaely.common.resources.itests;

import de.bernd_michaely.common.resources.ResourceInjector;
import de.bernd_michaely.common.resources.itest.resourceholders.*;
import java.util.Locale;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for runtime resource Dependency Injection.
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 */
public class ResourceInjectorITest
{
	@BeforeAll
	public static void setUpClass() throws Exception
	{
	}

	@AfterAll
	public static void tearDownClass() throws Exception
	{
	}

	@BeforeEach
	public void setUp() throws Exception
	{
	}

	@AfterEach
	public void tearDown() throws Exception
	{
	}

	@Test
	public void testConstructorDefault()
	{
		assertEquals(Locale.ROOT, new ResourceInjector().getLocale());
	}

	@Test
	public void testConstructorLocale()
	{
		assertEquals(Locale.ROOT, new ResourceInjector(null).getLocale());
	}

	@Test
	public void testResourcesAvailable1()
	{
		final var resourceHolder = new ResourceInjector()
			.injectResourcesInto(TestProcessingSuccessful.class);
		assertEquals("String resource 1", resourceHolder.labelValid1);
	}

	@Test
	public void testResourcesAvailable2()
	{
		final var resourceHolder = new TestProcessingSuccessful();
		new ResourceInjector().injectResourcesInto(resourceHolder);
		assertEquals("String resource 1", resourceHolder.labelValid1);
	}
//
//	@Test
//	public void runTestFallBackNoAnnotation()
//	{
//		final var resourceHolder = new TestFallBackNoAnnotation();
//		new ResourceInjector().injectResourcesInto(resourceHolder);
//		assertEquals("Fall Back String Resource",
//			resourceHolder.testFallBackStringResource);
//	}
//
//	@Test
//	public void runTestFallBackNoModule()
//	{
//		final var resourceHolder = new TestFallBackNoModule();
//		new ResourceInjector().injectResourcesInto(resourceHolder);
//		assertEquals("Fall Back String Resource",
//			resourceHolder.testFallBackStringResource);
//	}
//
//	@Test
//	public void runTestFallBackNoPackage()
//	{
//		final var resourceHolder = new TestFallBackNoPackage();
//		new ResourceInjector().injectResourcesInto(resourceHolder);
//		assertEquals("Fall Back String Resource",
//			resourceHolder.testFallBackStringResource);
//	}
//
//	@Test
//	public void runTestFallBackNoFile()
//	{
//		final var resourceHolder = new TestFallBackNoFile();
//		new ResourceInjector().injectResourcesInto(resourceHolder);
//		assertEquals("Fall Back String Resource",
//			resourceHolder.testFallBackStringResource);
//	}
//
//	@Test
//	public void runTestFallBackNoKey()
//	{
//		final var resourceHolder = new TestFallBackNoKey();
//		new ResourceInjector().injectResourcesInto(resourceHolder);
//		assertEquals("Fall Back String Resource",
//			resourceHolder.testFallBackStringResource);
//	}
//
//	@Test
//	public void testResourceHolderIsNotResourcesAnnotated()
//	{
//		final var resourceHolder = new TestResourcesAnnotation_0001();
//		new ResourceInjector().injectResourcesInto(resourceHolder);
//		assertEquals("Test Text", resourceHolder.strTestText);
//	}
//
//	@Test
//	public void testResourcesAnnotationErrStr()
//	{
//		final var resourceHolder = new TestResourcesAnnotation_1011();
//		new ResourceInjector().injectResourcesInto(resourceHolder);
////		assertEquals("Test Text", resourceHolder.strTestText);
//	}
//
//	@Test
//	public void testResourcesAnnotationErrBin()
//	{
//		final var resourceHolder = new TestResourcesAnnotation_1101();
//		new ResourceInjector().injectResourcesInto(resourceHolder);
////		assertNull(resourceHolder.testData);
//	}
}
