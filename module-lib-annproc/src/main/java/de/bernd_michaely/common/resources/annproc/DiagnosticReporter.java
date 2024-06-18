/*
 * Copyright 2024 Bernd Michaely (info@bernd-michaely.de).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.bernd_michaely.common.resources.annproc;

import de.bernd_michaely.common.resources.ErrorCodes;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic.Kind;
import org.checkerframework.checker.nullness.qual.Nullable;

import static de.bernd_michaely.common.resources.ErrorCodes.ERR_BINARY_RESOURCE_NOT_FOUND;
import static de.bernd_michaely.common.resources.ErrorCodes.ERR_STRING_RESOURCE_NOT_FOUND;

/**
 * Utility class for diagnostic reporting.
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 */
class DiagnosticReporter
{
	private final ProcessingEnvironment processingEnvironment;
	private final boolean showCheckedResourceKeys;
	private final boolean warnOnlyMissingResources;

	DiagnosticReporter(ProcessingEnvironment processingEnvironment,
		boolean showCheckedResourceKeys, boolean warnOnlyMissingResources)
	{
		this.processingEnvironment = processingEnvironment;
		this.showCheckedResourceKeys = showCheckedResourceKeys;
		this.warnOnlyMissingResources = warnOnlyMissingResources;
	}

	private Kind getMissingResourceDiagnosticKind()
	{
		return warnOnlyMissingResources ? Kind.WARNING : Kind.ERROR;
	}

	void trace(String msg, Object value)
	{
		if (showCheckedResourceKeys)
		{
			final StringBuilder str = new StringBuilder(msg);
			if (value != null)
			{
				str.append(" »").append(value).append("«");
			}
			final String result = str.toString();
			if (!result.isBlank())
			{
				processingEnvironment.getMessager().printMessage(Kind.NOTE, str);
			}
		}
	}

	static String getFormattedErrorCode(int errorCode)
	{
		return String.format(ResourceProcessor.FORMAT_ERROR_MSG, errorCode);
	}

	void reportError(ErrorCodes errorCode, Element element)
	{
		reportDiagnostic(Kind.ERROR, errorCode, errorCode.getErrorMessage(), element);
	}

	void reportErrorWithFormattedElement(ErrorCodes errorCode, Element element)
	{
		reportDiagnostic(Kind.ERROR, errorCode,
			String.format(errorCode.getErrorMessage(), element),
			element);
	}

	void reportError(ErrorCodes errorCode, String errorMsg, @Nullable Element element)
	{
		reportDiagnostic(Kind.ERROR, errorCode, errorMsg, element);
	}

	void reportMissingStringResource(String errorMsg, Element element)
	{
		reportDiagnostic(getMissingResourceDiagnosticKind(),
			ERR_STRING_RESOURCE_NOT_FOUND, errorMsg, element);
	}

	void reportMissingBinaryResource(String errorMsg, Element element)
	{
		reportDiagnostic(getMissingResourceDiagnosticKind(),
			ERR_BINARY_RESOURCE_NOT_FOUND, errorMsg, element);
	}

	void reportDiagnostic(Kind kind, ErrorCodes errorCode, String errorMsg, @Nullable Element element)
	{
		if (element != null)
		{
			processingEnvironment.getMessager().printMessage(kind,
				getFormattedErrorCode(errorCode.getErrorNumber()) + " : " + errorMsg, element);
		}
		else
		{
			processingEnvironment.getMessager().printMessage(kind,
				getFormattedErrorCode(errorCode.getErrorNumber()) + " : " + errorMsg);
		}
	}
}
