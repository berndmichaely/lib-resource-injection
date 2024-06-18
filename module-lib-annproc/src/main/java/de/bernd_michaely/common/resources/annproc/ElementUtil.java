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

import de.bernd_michaely.common.resources.BinaryResources;
import de.bernd_michaely.common.resources.ErrorCodes;
import de.bernd_michaely.common.resources.ResourceHolder;
import de.bernd_michaely.common.resources.StringResources;
import java.util.function.Predicate;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import static de.bernd_michaely.common.resources.ErrorCodes.*;
import static javax.lang.model.element.ElementKind.FIELD;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PUBLIC;

/**
 * Class containing utility methods related to elements.
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 */
class ElementUtil
{
	private final ProcessingEnvironment processingEnvironment;
	private final DiagnosticReporter diagnosticReporter;

	ElementUtil(ProcessingEnvironment processingEnvironment, DiagnosticReporter diagnosticReporter)
	{
		this.processingEnvironment = processingEnvironment;
		this.diagnosticReporter = diagnosticReporter;
	}

	/**
	 * Evaluates a predicate applied to an element and reports a diagnostic error
	 * as a side effect, if the predicate evaluates to false.
	 *
	 * @param <P>       the element type
	 * @param predicate the predicate to evaluate
	 * @param element   the element to check
	 * @param errorCode the error to report
	 * @return the value of the predicate applied to the element
	 */
	<P extends Element> boolean checkPredicate(Predicate<P> predicate,
		P element, ErrorCodes errorCode)
	{
		final boolean result = predicate.test(element);
		if (!result)
		{
			diagnosticReporter.reportErrorWithFormattedElement(errorCode, element);
		}
		return result;
	}

	static boolean isElementPublic(Element element)
	{
		return element.getModifiers().contains(PUBLIC);
	}

	boolean checkElementPublic(Element element)
	{
		final ErrorCodes errorCode;
		errorCode = switch (element.getKind())
		{
			case CLASS ->
				ERR_CLASS_NOT_PUBLIC;
			case FIELD ->
				ERR_FIELD_NOT_PUBLIC;
			default ->
				ERR_ELEMENT_NOT_PUBLIC;
		};
		return checkPredicate(ElementUtil::isElementPublic, element, errorCode);
	}

	static boolean isFieldNotFinal(VariableElement element)
	{
		return !element.getModifiers().contains(FINAL);
	}

	boolean checkFieldNotFinal(VariableElement element)
	{
		return checkPredicate(ElementUtil::isFieldNotFinal, element, ERR_FIELD_FINAL);
	}

	boolean isResourceHolder(Element element)
	{
		final TypeElement typeResourceHolder = processingEnvironment.getElementUtils()
			.getTypeElement(ResourceHolder.class.getCanonicalName());
		return processingEnvironment.getTypeUtils().isSubtype(
			element.asType(), typeResourceHolder.asType());
	}

	boolean isNotResourceHolder(Element element)
	{
		return !isResourceHolder(element);
	}

	boolean checkResourceHolder(Element element)
	{
		return checkPredicate(this::isResourceHolder, element, ERR_TYPE_NOT_RESOURCE_HOLDER);
	}

	static boolean isHavingResources(Element element)
	{
		final boolean hasStringResources = element.getAnnotation(StringResources.class) != null;
		final boolean hasBinaryResources = element.getAnnotation(BinaryResources.class) != null;
		return hasStringResources || hasBinaryResources;
	}

	static boolean isNotHavingResources(Element element)
	{
		return !isHavingResources(element);
	}
}
