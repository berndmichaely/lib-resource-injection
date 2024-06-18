/* Created on Sep 10, 2019 */
package de.bernd_michaely.common.resources.itests;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import javax.annotation.processing.AbstractProcessor;
import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

import static java.io.File.pathSeparator;
import static java.lang.System.Logger.Level.INFO;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static javax.tools.Diagnostic.Kind.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Utility class to run annotation processing integrations tests.
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 * @param <T> base type for classes to be tested by annotation processing
 */
public class AnnotationProcessingTestRunner<T>
{
	private static final System.Logger LOGGER = System.getLogger(
		AnnotationProcessingTestRunner.class.getName());
	private static final String MSG_TOOL_PROVIDER_NO_COMPILER =
		"This JDK does not feature a tool provider access to a compiler" +
		" → testing the annotation processor is not possible!";
	private static final String PREFIX_TEMP_DIR = "-JUnit-AnnotationProcessingTestRunner-";
	private final Class<? extends AbstractProcessor> classAnnotationProcessor;
	private final Path pathSrcTestResourceHolders;
	private final String classPath;
	private final List<AnnotationProcessingDiagnostic> actualDiagnostics = new ArrayList<>();

	/**
	 * Creates a new TestRunner instance.
	 *
	 * @param pathLibAnnotationProcessor path to annotation processor library
	 * @param classAnnotationProcessor   the annotation processor class
	 * @param pathLibResources           path to resources library
	 * @param pathSrcTestResourceHolders the path to the ResourceHolder sources
	 *                                   (containing the class to test), assuming
	 *                                   it also contains the resources needed
	 */
	public AnnotationProcessingTestRunner(Path pathLibAnnotationProcessor,
		Class<? extends AbstractProcessor> classAnnotationProcessor,
		Path pathLibResources,
		Path pathSrcTestResourceHolders)
	{
		this(pathLibAnnotationProcessor, classAnnotationProcessor,
			pathLibResources, pathSrcTestResourceHolders, null);
	}

	/**
	 * Creates a new TestRunner instance.
	 *
	 * @param pathLibAnnotationProcessor path to annotation processor library
	 * @param classAnnotationProcessor   the annotation processor class
	 * @param pathLibResources           path to resources library
	 * @param pathSrcTestResourceHolders the path to the ResourceHolder sources
	 *                                   (containing the class to test)
	 * @param pathSrcTestResources       a separate path to the resources (may be
	 *                                   equal to parameter
	 *                                   pathSrcTestResourceHolders or null)
	 */
	public AnnotationProcessingTestRunner(Path pathLibAnnotationProcessor,
		Class<? extends AbstractProcessor> classAnnotationProcessor,
		Path pathLibResources,
		Path pathSrcTestResourceHolders, Path pathSrcTestResources)
	{
		this.classAnnotationProcessor = requireNonNull(classAnnotationProcessor,
			"Annotation processor class must not be null");
		this.pathSrcTestResourceHolders = requireNonNull(pathSrcTestResourceHolders,
			"pathSrcTestResourceHolders must not be null");
		this.classPath = Stream.of(pathLibAnnotationProcessor, pathLibResources,
			pathSrcTestResourceHolders, pathSrcTestResources)
			.filter(Objects::nonNull)
			.distinct()
			.map(Path::toAbsolutePath).map(Path::normalize).map(Path::toString)
			.collect(joining(pathSeparator));
	}

	private static Path createTempDir() throws IOException
	{
		final String userName = System.getProperty(
			"user.name", ResourceProcessorITest.class.getSimpleName());
		final Path tempDirectory = Files.createTempDirectory(userName + PREFIX_TEMP_DIR);
		LOGGER.log(INFO, "Created temp dir : »" + tempDirectory + "«");
		return tempDirectory;
	}

	private static void cleanUpTempDir(Path pathTempDir) throws IOException
	{
		LOGGER.log(INFO, "Cleaning up temp dir : »" + pathTempDir + "«");
		Files.walkFileTree(pathTempDir, new SimpleFileVisitor<>()
		{
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
				throws IOException
			{
				LOGGER.log(INFO, "Cleaning up file »" + file.toAbsolutePath() + "«");
				Files.delete(file);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException ex)
				throws IOException
			{
				if (ex == null)
				{
					Files.delete(dir);
					return FileVisitResult.CONTINUE;
				}
				else
				{
					throw ex;
				}
			}
		});
	}

	private static JavaCompiler getJavaCompiler()
	{
		final JavaCompiler systemJavaCompiler = ToolProvider.getSystemJavaCompiler();
		assertTrue(nonNull(systemJavaCompiler), MSG_TOOL_PROVIDER_NO_COMPILER);
		return systemJavaCompiler;
	}

	private Path getPathTestClass(Class<? extends T> classCompilerInput)
	{
		final String[] packageComponents = classCompilerInput.getPackageName().split("\\.");
		final String fileNameClass = classCompilerInput.getSimpleName() + ".java";
		final String[] path = Stream.concat(
			Arrays.stream(packageComponents), Stream.of(fileNameClass))
			.toArray(String[]::new);
		return Paths.get(this.pathSrcTestResourceHolders.toString(), path).toAbsolutePath().normalize();
	}

	/**
	 * Compiles the given test class using the annotation processor.
	 *
	 * @param classCompilerInput the class to be tested
	 * @return this instance
	 * @throws IOException
	 */
	public AnnotationProcessingTestRunner<T> compile(Class<? extends T> classCompilerInput)
		throws IOException
	{
		LOGGER.log(INFO, "Running annotation processor test with compiler input class »" +
			classCompilerInput.getName() + "« and classpath »" + this.classPath + "«");
		final var compiler = getJavaCompiler();
		actualDiagnostics.clear();
		final DiagnosticListener<? super JavaFileObject> diagnosticCollector = diagnostic ->
		{
			if ((diagnostic != null) && EnumSet.of(ERROR, WARNING).contains(diagnostic.getKind()))
			{
				actualDiagnostics.add(new AnnotationProcessingDiagnostic(diagnostic));
			}
		};
		try (var fileManager = compiler.getStandardFileManager(diagnosticCollector, null, null))
		{
			final Path testClassPath = getPathTestClass(classCompilerInput);
			final var classToCompile = fileManager.getJavaFileObjects(testClassPath);
			LOGGER.log(INFO, "Class to compile : »" + testClassPath + "«");
			final Path pathTempDir = createTempDir();
			try
			{
				final var compilerOptions = List.of(
					"-AshowCheckedResourceKeys=false",
					"-AwarnOnlyMissingResources=false",
					"-classpath", this.classPath,
					"-d", pathTempDir.toString(),
					"-implicit:none",
					"-processor", this.classAnnotationProcessor.getName()
				);
				compiler.getTask(null, fileManager, diagnosticCollector,
					compilerOptions, null, classToCompile).call();
			}
			finally
			{
				cleanUpTempDir(pathTempDir);
			}
		}
		return this;
	}

	/**
	 * Checks, that the list of generated (error and warning) diagnostics equals
	 * the given list of expected diagnostics.
	 *
	 * @param expectedDiagnostics an array of expected diagnostics (the order of
	 *                            diagnostics doesn't matter)
	 */
	public void checkExpectedDiagnostics(AnnotationProcessingDiagnostic... expectedDiagnostics)
	{
		assertNotNull(expectedDiagnostics);
		final var listExpectedDiagnostics = Arrays.asList(expectedDiagnostics);
		final var listActualDiagnostics = new ArrayList<>(actualDiagnostics);
		// check unexpected diagnostics:
		final var unexpectedDiagnostics = new ArrayList<>(listActualDiagnostics);
		unexpectedDiagnostics.removeAll(listExpectedDiagnostics);
		unexpectedDiagnostics.forEach(diagnostic ->
			LOGGER.log(INFO, "Unexpected diagnostic messages : »{0}«", diagnostic.getMessage()));
		// check missing diagnostics:
		final var missingDiagnostics = new ArrayList<>(listExpectedDiagnostics);
		missingDiagnostics.removeAll(actualDiagnostics);
		missingDiagnostics.forEach(diagnostic ->
			LOGGER.log(INFO, "Missing diagnostic messages : »{0}«", diagnostic.getMessage()));
		// compare list elements independent of order:
		listActualDiagnostics.sort(null);
		listExpectedDiagnostics.sort(null);
		assertEquals(listExpectedDiagnostics, listActualDiagnostics);
	}
}
