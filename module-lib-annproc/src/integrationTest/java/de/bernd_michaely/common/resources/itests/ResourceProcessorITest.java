/* Created on 04.07.2019 */
package de.bernd_michaely.common.resources.itests;

import de.bernd_michaely.common.resources.BinaryResources;
import de.bernd_michaely.common.resources.GenericResources;
import de.bernd_michaely.common.resources.ResourceHolder;
import de.bernd_michaely.common.resources.StringResources;
import de.bernd_michaely.common.resources.annproc.ResourceProcessor;
import de.bernd_michaely.common.resources.itest.resourceholders.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.tools.Diagnostic;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static de.bernd_michaely.common.resources.ErrorCodes.*;
import static javax.tools.Diagnostic.Kind.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for annotation processor.
 *
 * <h3>Method naming conventions:</h3>
 * The test methods with the name scheme
 * <code>testResourcesAnnotation_b4b3b2b1, b1..b4 in {0,1}</code> test the
 * conditions for the presence of the (non repeatable) annotations on
 * ResourceHolder classes:
 * <ul>
 * <li>b1 : @IsResourceHolder (package local, inherited with the ResourceHolder
 * and indicating, that the class extends {@link ResourceHolder})</li>
 * <li>b2 : {@link StringResources}</li>
 * <li>b3 : {@link BinaryResources}</li>
 * <li>b4 : {@link GenericResources}</li>
 * </ul>
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 */
public class ResourceProcessorITest
{
	// class path locations rely on gradle default directory structure layout:
	private static final Path PATH_LIB_ANN_PROC = Paths.get(
		// assuming library is already compiled, e.g. by gradle dependency →
		"build", "classes", "java", "main");
	private static final Path PATH_LIB_RESOURCES = Paths.get(
		// assuming library is already compiled, e.g. by gradle dependency →
		"..", "module-lib", "build", "classes", "java", "main");
	private static final Path PATH_SRC_TEST_RESOURCEHOLDERS = Paths.get(
		"..", "module-itest-resourceholders", "src", "main", "java");
	private static final Path PATH_SRC_TEST_RESOURCES = Paths.get(
		"..", "module-itest-resources", "src", "main", "resources");
	private static final Path PATH_SRC_PACKAGE_LOCAL_CLASSES = Paths.get(
		"src", "integrationTest", "java");
	private AnnotationProcessingTestRunner<Object> testRunner0;
	private AnnotationProcessingTestRunner<ResourceHolder> testRunner1;

	@BeforeAll
	public static void setUpAll()
	{
		assertTrue(Files.isDirectory(PATH_LIB_ANN_PROC),
			"CWD is »" + Paths.get("").toAbsolutePath() +
			"«, PATH_LIB_ANN_PROC is not a valid directory: »" + PATH_LIB_ANN_PROC + "«");
		assertTrue(Files.isDirectory(PATH_LIB_RESOURCES),
			"CWD is »" + Paths.get("").toAbsolutePath() +
			"«, PATH_LIB_RESOURCES is not a valid directory: »" + PATH_LIB_RESOURCES + "«");
		assertTrue(Files.isDirectory(PATH_SRC_TEST_RESOURCEHOLDERS),
			"PATH_SRC_TEST_RESOURCEHOLDERS is not a valid directory: »" + PATH_SRC_TEST_RESOURCEHOLDERS + "«");
		assertTrue(Files.isDirectory(PATH_SRC_TEST_RESOURCES),
			"PATH_SRC_TEST_RESOURCES is not a valid directory: »" + PATH_SRC_TEST_RESOURCES + "«");
		assertTrue(Files.isDirectory(PATH_SRC_PACKAGE_LOCAL_CLASSES),
			"PATH_SRC_PACKAGE_LOCAL_CLASSES is not a valid directory: »" + PATH_SRC_PACKAGE_LOCAL_CLASSES + "«");
	}

	@BeforeEach
	public void setUp() throws Exception
	{
		testRunner0 = new AnnotationProcessingTestRunner<>(PATH_LIB_ANN_PROC, ResourceProcessor.class,
			PATH_LIB_RESOURCES, PATH_SRC_TEST_RESOURCEHOLDERS, PATH_SRC_TEST_RESOURCES);
		testRunner1 = new AnnotationProcessingTestRunner<>(PATH_LIB_ANN_PROC, ResourceProcessor.class,
			PATH_LIB_RESOURCES, PATH_SRC_TEST_RESOURCEHOLDERS, PATH_SRC_TEST_RESOURCES);
	}

	@Test
	public void testResourcesAnnotation_0000() throws IOException
	{
		testRunner0.compile(TestResourcesAnnotation_0000.class)
			.checkExpectedDiagnostics();
		class WithoutCorrespondingJavaFile extends ResourceHolder
		{
			// will cause a FileNotFoundException in AnnotationProcessingTestRunner
			// which is to be distinguished from annotation processing diagnostics
		}
		testRunner1.compile(WithoutCorrespondingJavaFile.class).checkExpectedDiagnostics(
			new AnnotationProcessingDiagnostic(ERR_UNKNOWN, ERROR, Diagnostic.NOPOS));
	}

	@Test
	public void testResourcesAnnotation_0001() throws IOException
	{
		testRunner1.compile(TestResourcesAnnotation_0001.class).checkExpectedDiagnostics();
	}

	@Test
	public void testResourcesAnnotation_0010() throws IOException
	{
		testRunner0.compile(TestResourcesAnnotation_0010.class).checkExpectedDiagnostics(
			new AnnotationProcessingDiagnostic(ERR_TYPE_NOT_RESOURCE_HOLDER, ERROR, 6));
	}

	@Test
	public void testResourcesAnnotation_0011() throws IOException
	{
		testRunner1.compile(TestResourcesAnnotation_0011.class).checkExpectedDiagnostics();
	}

	@Test
	public void testResourcesAnnotation_0100() throws IOException
	{
		testRunner0.compile(TestResourcesAnnotation_0100.class).checkExpectedDiagnostics(
			new AnnotationProcessingDiagnostic(ERR_TYPE_NOT_RESOURCE_HOLDER, ERROR, 6));
	}

	@Test
	public void testResourcesAnnotation_0101() throws IOException
	{
		testRunner1.compile(TestResourcesAnnotation_0101.class).checkExpectedDiagnostics();
	}

	@Test
	public void testResourcesAnnotation_0110() throws IOException
	{
		testRunner0.compile(TestResourcesAnnotation_0110.class).checkExpectedDiagnostics(
			new AnnotationProcessingDiagnostic(ERR_TYPE_NOT_RESOURCE_HOLDER, ERROR, 8));
	}

	@Test
	public void testResourcesAnnotation_0111() throws IOException
	{
		testRunner1.compile(TestResourcesAnnotation_0111.class).checkExpectedDiagnostics();
	}

	@Test
	public void testResourcesAnnotation_1000() throws IOException
	{
		testRunner0.compile(TestResourcesAnnotation_1000.class).checkExpectedDiagnostics();
	}

	@Test
	public void testResourcesAnnotation_1001() throws IOException
	{
		testRunner1.compile(TestResourcesAnnotation_1001.class).checkExpectedDiagnostics(
			new AnnotationProcessingDiagnostic(ERR_RESOURCE_HOLDER_AND_GENERIC, ERROR, 7));
	}

	@Test
	public void testResourcesAnnotation_1010() throws IOException
	{
		testRunner0.compile(TestResourcesAnnotation_1010.class).checkExpectedDiagnostics(
			new AnnotationProcessingDiagnostic(ERR_GENERIC_RESOURCES_ANN, ERROR, 8),
			new AnnotationProcessingDiagnostic(ERR_TYPE_NOT_RESOURCE_HOLDER, ERROR, 8));
	}

	@Test
	public void testResourcesAnnotation_1011() throws IOException
	{
		testRunner1.compile(TestResourcesAnnotation_1011.class).checkExpectedDiagnostics(
			new AnnotationProcessingDiagnostic(ERR_RESOURCE_HOLDER_AND_GENERIC, ERROR, 9),
			new AnnotationProcessingDiagnostic(ERR_GENERIC_RESOURCES_ANN, ERROR, 9));
	}

	@Test
	public void testResourcesAnnotation_1100() throws IOException
	{
		testRunner0.compile(TestResourcesAnnotation_1100.class).checkExpectedDiagnostics(
			new AnnotationProcessingDiagnostic(ERR_GENERIC_RESOURCES_ANN, ERROR, 8),
			new AnnotationProcessingDiagnostic(ERR_TYPE_NOT_RESOURCE_HOLDER, ERROR, 8));
	}

	@Test
	public void testResourcesAnnotation_1101() throws IOException
	{
		testRunner1.compile(TestResourcesAnnotation_1101.class).checkExpectedDiagnostics(
			new AnnotationProcessingDiagnostic(ERR_RESOURCE_HOLDER_AND_GENERIC, ERROR, 9),
			new AnnotationProcessingDiagnostic(ERR_GENERIC_RESOURCES_ANN, ERROR, 9));
	}

	@Test
	public void testResourcesAnnotation_1110() throws IOException
	{
		testRunner0.compile(TestResourcesAnnotation_1110.class).checkExpectedDiagnostics(
			new AnnotationProcessingDiagnostic(ERR_GENERIC_RESOURCES_ANN, ERROR, 10),
			new AnnotationProcessingDiagnostic(ERR_TYPE_NOT_RESOURCE_HOLDER, ERROR, 10));
	}

	@Test
	public void testResourcesAnnotation_1111() throws IOException
	{
		testRunner1.compile(TestResourcesAnnotation_1111.class).checkExpectedDiagnostics(
			new AnnotationProcessingDiagnostic(ERR_RESOURCE_HOLDER_AND_GENERIC, ERROR, 11),
			new AnnotationProcessingDiagnostic(ERR_GENERIC_RESOURCES_ANN, ERROR, 11));
	}

	@Test
	public void testResourceHolderNonPublic() throws IOException
	{
		final AnnotationProcessingTestRunner<ResourceHolder> testRunner =
			new AnnotationProcessingTestRunner<>(PATH_LIB_ANN_PROC, ResourceProcessor.class,
				PATH_LIB_RESOURCES, PATH_SRC_PACKAGE_LOCAL_CLASSES);
		testRunner.compile(TestNonPublicResourceHolderStr.class).checkExpectedDiagnostics(
			new AnnotationProcessingDiagnostic(ERR_CLASS_NOT_PUBLIC, ERROR, 7));
		testRunner.compile(TestNonPublicResourceHolderBin.class).checkExpectedDiagnostics(
			new AnnotationProcessingDiagnostic(ERR_CLASS_NOT_PUBLIC, ERROR, 7));
	}

	@Test
	public void testProcessingSuccessful() throws IOException
	{
		testRunner1.compile(TestProcessingSuccessful.class).checkExpectedDiagnostics();
	}
}
