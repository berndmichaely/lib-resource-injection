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

import de.bernd_michaely.common.resources.EnumTypes;
import de.bernd_michaely.common.resources.EnumTypesContainer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import org.checkerframework.checker.nullness.qual.Nullable;

import static de.bernd_michaely.common.resources.ErrorCodes.*;

/**
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 */
class EnumTypesUtil
{
	private final ProcessingEnvironment processingEnvironment;
	private final DiagnosticReporter diagnosticReporter;
	// annotation method names:
	private static final String METHOD_NAME_FIELD_NAMES = "fieldNames";
	private static final String METHOD_NAME_ENUM_TYPE = "enumType";
	private static final String METHOD_NAME_ENUM_TYPES_CONTAINER = "value";

	EnumTypesUtil(ProcessingEnvironment processingEnvironment, DiagnosticReporter diagnosticReporter)
	{
		this.processingEnvironment = processingEnvironment;
		this.diagnosticReporter = diagnosticReporter;
	}

	private boolean isEnumTypesAnnotation(AnnotationMirror annotationMirror)
	{
		final Name annotationName = annotationMirror.getAnnotationType().asElement().getSimpleName();
		final String enumTypesName = EnumTypes.class.getSimpleName();
		final boolean result = annotationName.contentEquals(enumTypesName);
		return result;
	}

	private boolean isEnumTypesContainerAnnotation(AnnotationMirror annotationMirror)
	{
		final Name annotationName = annotationMirror.getAnnotationType().asElement().getSimpleName();
		final String enumTypesContainerName = EnumTypesContainer.class.getSimpleName();
		final boolean result = annotationName.contentEquals(enumTypesContainerName);
		return result;
	}

	private void reportInternalStateError(String expected, @Nullable VariableElement element)
	{
		final String msg = String.format(
			ERR_INTERNAL_STATE.getErrorMessage(), getClass().getName(), expected);
		diagnosticReporter.reportError(ERR_INTERNAL_STATE, msg, element);
	}

	private boolean checkTypeAnnotationMirror(AnnotationValue annotationValue, VariableElement element)
	{
		if (annotationValue instanceof AnnotationMirror)
		{
			return true;
		}
		else
		{
			reportInternalStateError(AnnotationValue.class.getCanonicalName(), element);
			return false;
		}
	}

	Map<@Nullable String, TypeElement> getMapEnumTypes(
		@Nullable VariableElement fieldRef, BiConsumer<EnumTypes, String> onDuplicateFieldName)
	{
		final var mapNames = new HashMap<String, @Nullable String>();
		// (… map implementation must support null values)
		final Consumer<AnnotationMirror> annotationMirrorHandler = annotationMirror ->
		{
			String enumTypeName = "";
			final var fieldNames = new ArrayList<String>();
			for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry
				: annotationMirror.getElementValues().entrySet())
			{
				final String methodName = entry.getKey().getSimpleName().toString();
				final AnnotationValue annotationValue = entry.getValue();
				final Object annotationValueObject = annotationValue.getValue();
				switch (methodName)
				{
					case METHOD_NAME_ENUM_TYPE ->
					{
						if (annotationValueObject instanceof TypeMirror)
						{
							enumTypeName = annotationValueObject.toString();
						}
						else
						{
							reportInternalStateError("String", fieldRef);
						}
					}
					case METHOD_NAME_FIELD_NAMES ->
					{
						if (annotationValueObject instanceof List)
						{
							@SuppressWarnings("unchecked")
							final List<? extends AnnotationValue> listFieldNames =
								(List<? extends AnnotationValue>) annotationValueObject;
							listFieldNames.forEach(fieldName -> fieldNames.add(fieldName.toString()));
						}
						else
						{
							reportInternalStateError("List<? extends AnnotationValue>", fieldRef);
						}
					}
				}
			}
			for (String fieldName : fieldNames)
			{
				if (mapNames.containsKey(fieldName))
				{
					mapNames.put(fieldName, null);
					final String msg = String.format(ERR_DUPLICATE_FIELD_NAME.getErrorMessage(), fieldName);
					diagnosticReporter.reportError(ERR_DUPLICATE_FIELD_NAME, msg, fieldRef);
				}
				else
				{
					mapNames.put(fieldName, enumTypeName);
				}
			}
		};
		if (fieldRef != null)
		{
			fieldRef.getAnnotationMirrors().forEach(annotationMirror ->
			{
				if (isEnumTypesContainerAnnotation(annotationMirror))
				{
					final Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues =
						annotationMirror.getElementValues();
					final Optional<? extends ExecutableElement> method =
						elementValues.keySet().stream()
							.filter(executableElement ->
								executableElement.getSimpleName().contentEquals(METHOD_NAME_ENUM_TYPES_CONTAINER))
							.findAny();
					if (method.isPresent())
					{
						final AnnotationValue annotationValue = elementValues.get(method.get());
						if (annotationValue != null)
						{
							final Object annotationValueObject = annotationValue.getValue();
							if (annotationValueObject instanceof List)
							{
								@SuppressWarnings("unchecked")
								final List<? extends AnnotationValue> listEnumTypesMirrors =
									(List<? extends AnnotationValue>) annotationValueObject;
								listEnumTypesMirrors.stream()
									.filter(av -> checkTypeAnnotationMirror(av, fieldRef))
									.map(av -> (AnnotationMirror) av)
									.forEach(annotationMirrorHandler);
							}
						}
					}
					else
					{
						reportInternalStateError(EnumTypesContainer.class.getCanonicalName() +
							"." + METHOD_NAME_ENUM_TYPES_CONTAINER + "()",
							fieldRef);
					}
				}
				else if (isEnumTypesAnnotation(annotationMirror))
				{
					annotationMirrorHandler.accept(annotationMirror);
				}
			});
		}
		final var mapTypes = new HashMap<String, TypeElement>();
		mapNames.keySet().forEach(fieldName ->
		{
			final String enumName = mapNames.get(fieldName);
			if (enumName != null)
			{
				final TypeElement typeElement = processingEnvironment.getElementUtils().getTypeElement(enumName);
				final List<Name> enumConstantNames = typeElement.getEnclosedElements().stream()
					.filter(e -> e.getKind().equals(ElementKind.ENUM_CONSTANT))
					.map(Element::getSimpleName)
					.collect(Collectors.toList());
				diagnosticReporter.trace(fieldName, enumConstantNames);
				mapTypes.put(fieldName, typeElement);
			}
		});
		diagnosticReporter.trace("annotationMirror.getElementValues() →", mapTypes);
		return Collections.unmodifiableMap(mapTypes);
	}
}
