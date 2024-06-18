package de.bernd_michaely.common.resources.itests;

import de.bernd_michaely.common.resources.ErrorCodes;
import de.bernd_michaely.common.resources.annproc.ResourceProcessor;
import java.util.Comparator;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import static de.bernd_michaely.common.resources.ErrorCodes.*;

/**
 * Utility class for comparison of expected and actual compiler diagnostics
 * during annotation processing integrations tests.
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 */
public class AnnotationProcessingDiagnostic implements Comparable<AnnotationProcessingDiagnostic>
{
	private static final Pattern PATTERN_ERROR_CODE;
	private final ErrorCodes errorCode;
	private final Diagnostic.Kind diagnosticKind;
	private final long lineNumber;
	private final String message;

	static
	{
		PATTERN_ERROR_CODE = Pattern.compile(
			".*" + ResourceProcessor.REGEX_ERROR_CODE + ".*");
	}

	/**
	 * Constructor for expected diagnostics.
	 *
	 * @param errorCode      the expected error code
	 * @param diagnosticKind the expected diagnostic kind
	 * @param lineNumber     the expected lineNumber for the error to appear
	 */
	public AnnotationProcessingDiagnostic(ErrorCodes errorCode,
		Diagnostic.Kind diagnosticKind, long lineNumber)
	{
		this.errorCode = errorCode;
		this.diagnosticKind = diagnosticKind;
		this.lineNumber = lineNumber;
		this.message = null;
	}

	/**
	 * Constructor for actual diagnostics.
	 *
	 * @param diagnostic the actual diagnostic
	 */
	public AnnotationProcessingDiagnostic(Diagnostic<? extends JavaFileObject> diagnostic)
	{
		this.message = diagnostic.getMessage(null);
		final Matcher matcher = PATTERN_ERROR_CODE.matcher(message);
		final int errorNumber = matcher.matches() ?
			Integer.parseInt(matcher.group(1)) : ERR_UNKNOWN.getErrorNumber();
		this.errorCode = ErrorCodes.getValueByNumber(errorNumber);
		this.diagnosticKind = diagnostic.getKind();
		this.lineNumber = diagnostic.getLineNumber();
	}

	public ErrorCodes getErrorCode()
	{
		return errorCode;
	}

	public Diagnostic.Kind getDiagnosticKind()
	{
		return diagnosticKind;
	}

	public long getLineNumber()
	{
		return lineNumber;
	}

	@Override
	public int compareTo(AnnotationProcessingDiagnostic other)
	{
		return Comparator
			.comparing(AnnotationProcessingDiagnostic::getErrorCode)
			.thenComparing(AnnotationProcessingDiagnostic::getDiagnosticKind)
			.thenComparing(AnnotationProcessingDiagnostic::getLineNumber)
			.compare(this, other);
	}

	@Override
	public boolean equals(Object obj)
	{
		return (obj instanceof AnnotationProcessingDiagnostic) ?
			compareTo((AnnotationProcessingDiagnostic) obj) == 0 : false;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(errorCode, diagnosticKind, lineNumber);
	}

	public String getMessage()
	{
		return (message != null) ? message : toString();
	}

	@Override
	public String toString()
	{
		return String.format("{ResProc–%s → %s_[#%d] @ line %d}",
			diagnosticKind, errorCode.name(), errorCode.getErrorNumber(), lineNumber);
	}
}
