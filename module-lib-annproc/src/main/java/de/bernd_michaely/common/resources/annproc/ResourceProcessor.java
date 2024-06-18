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
import de.bernd_michaely.common.resources.EnumTypes;
import de.bernd_michaely.common.resources.ErrorCodes;
import de.bernd_michaely.common.resources.FileExt;
import de.bernd_michaely.common.resources.GenericResources;
import de.bernd_michaely.common.resources.IsResourceHolder;
import de.bernd_michaely.common.resources.ResourceKey;
import de.bernd_michaely.common.resources.ResourceParameters;
import de.bernd_michaely.common.resources.StringResources;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import static de.bernd_michaely.common.resources.ErrorCodes.*;
import static de.bernd_michaely.common.resources.SharedConstants.*;
import static javax.lang.model.element.ElementKind.FIELD;

/**
 * Annotation processor for compile time checking of resources injected at
 * runtime. See table for supported options
 * <table border="1">
 * <caption>Supported annotation processor options</caption>
 * <tr>
 * <th>name</th><th>type</th><th>default</th><th>description</th>
 * </tr>
 * <tr>
 * <td>showCheckedResourceKeys</td><td>boolean</td><td>false</td><td>be verbose
 * (show processed annotations, checked resource keys)</td>
 * </tr>
 * <tr>
 * <td>warnOnlyMissingResources</td><td>boolean</td><td>false</td>
 * <td>if true, report missing resources as warnings only (by default report
 * missing resources as errors)</td>
 * </tr>
 * </table>
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 */
@SupportedAnnotationTypes("de.bernd_michaely.common.resources.*")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@SupportedOptions(
	{
		"showCheckedResourceKeys", "warnOnlyMissingResources"
	})
public class ResourceProcessor extends AbstractProcessor
{
	private final boolean beVerbose = true;
	// annotation processor option names:
	private static final String A_SHOW_CHECKED_RESOURCE_KEYS = "showCheckedResourceKeys";
	private static final String A_WARN_ONLY_MISSING_RESOURCES = "warnOnlyMissingResources";
	private static final String FORMAT_ERROR_NUMBER = "%d";
	/**
	 * Error code format string. The format string contains a »%d« argument which
	 * will be replaced by the error code number.
	 *
	 * @see String#format(String, Object...)
	 * @see #REGEX_ERROR_CODE
	 */
	static final String FORMAT_ERROR_MSG = "[ResProcErrID#" + FORMAT_ERROR_NUMBER + "]";
	/**
	 * Regular expression to extract an error code number from an annotation
	 * processing diagnostic message. Mainly provided for integration tests.
	 */
	public static final String REGEX_ERROR_CODE;
	// fields:
	private boolean isNewInstance = true;
	private @MonotonicNonNull DiagnosticReporter diagnostics;
	private @MonotonicNonNull ElementUtil elementUtil;
	private @MonotonicNonNull ResourceParameters resourceParameters;

	static
	{
		REGEX_ERROR_CODE = "\\Q" + FORMAT_ERROR_MSG.replace(FORMAT_ERROR_NUMBER, "\\E(\\d+)\\Q") + "\\E";
	}

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv)
	{
		super.init(processingEnv);
		if (beVerbose)
		{
			System.out.println(
				"Annotation processor »" + ResourceProcessor.class.getName() + "» initialized.");
		}
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
	{
		if (isInitialized())
		{
			if (isNewInstance)
			{
				final boolean showCheckedResourceKeys = Boolean.parseBoolean(
					processingEnv.getOptions().get(A_SHOW_CHECKED_RESOURCE_KEYS));
				final boolean warnOnlyMissingResources = Boolean.parseBoolean(
					processingEnv.getOptions().get(A_WARN_ONLY_MISSING_RESOURCES));
				if (beVerbose)
				{
					System.out.println("[-A] SHOW_CHECKED_RESOURCE_KEYS  : " + showCheckedResourceKeys);
					System.out.println("[-A] WARN_ONLY_MISSING_RESOURCES : " + warnOnlyMissingResources);
				}
				diagnostics = new DiagnosticReporter(processingEnv,
					showCheckedResourceKeys, warnOnlyMissingResources);
				elementUtil = new ElementUtil(processingEnv, getDiagnostics());
				if (showCheckedResourceKeys)
				{
					annotations.stream()
						.map(TypeElement::getQualifiedName)
						.forEach(a -> getDiagnostics().trace("Processing annotations of type", a));
				}
				isNewInstance = false;
			}
			roundEnv.getElementsAnnotatedWithAny(
				Set.of(StringResources.class, BinaryResources.class)).forEach(element ->
			{
				getDiagnostics().trace("Checking @[String|Binary]Resources for element", element);
				getElementUtil().checkResourceHolder(element);
			});
			roundEnv.getElementsAnnotatedWith(GenericResources.class).forEach(element ->
			{
				getDiagnostics().trace("Checking @GenericResources for element", element);
				getElementUtil().checkPredicate(ElementUtil::isNotHavingResources,
					element, ErrorCodes.ERR_GENERIC_RESOURCES_ANN);
				getElementUtil().checkPredicate(getElementUtil()::isNotResourceHolder,
					element, ErrorCodes.ERR_RESOURCE_HOLDER_AND_GENERIC);
			});
			roundEnv.getElementsAnnotatedWith(IsResourceHolder.class).stream()
				.filter(element -> element instanceof TypeElement)
				.map(element -> (TypeElement) element)
				.forEach(typeElement ->
				{
					getDiagnostics().trace("Check ResourceHolder class", typeElement);
					getElementUtil().checkElementPublic(typeElement);
					resourceParameters = new ResourceParameters(
						typeElement.getAnnotation(StringResources.class),
						typeElement.getAnnotation(BinaryResources.class),
						processingEnv.getElementUtils().getPackageOf(typeElement)
							.getQualifiedName().toString());
					handleResourceHolder(typeElement, null, null);
				});
		}
		return true;
	}

	private DiagnosticReporter getDiagnostics()
	{
		if (diagnostics == null)
		{
			throw new IllegalStateException("DiagnosticReporter not initialized!");
		}
		return diagnostics;
	}

	private ElementUtil getElementUtil()
	{
		if (elementUtil == null)
		{
			throw new IllegalStateException("ElementUtil not initialized!");
		}
		return elementUtil;
	}

	private ResourceParameters getResourceParameters()
	{
		if (resourceParameters == null)
		{
			throw new IllegalStateException("ResourceParameters not initialized!");
		}
		return resourceParameters;
	}

	private String getResourceKey(VariableElement field)
	{
		return getResourceKey(null, field);
	}

	private String getResourceKey(@Nullable String keyPrefix, VariableElement field)
	{
		final ResourceKey annotation = field.getAnnotation(ResourceKey.class);
		final String strResourceKey = (annotation != null && !annotation.value().isBlank()) ?
			annotation.value() : field.getSimpleName().toString();
		final String prefix = (keyPrefix != null) ?
			keyPrefix + strResourceKey : strResourceKey;
		return getElementUtil().isResourceHolder(field) ? (prefix + SEPARATOR_KEY_NESTED) : prefix;
	}

	private void handleResourceHolder(TypeElement resourceHolder,
		@Nullable String prefix, @Nullable VariableElement fieldRef)
	{
		final BiConsumer<EnumTypes, String> onDuplicateFieldName = (enumTypes, fieldName) ->
			getDiagnostics().reportError(ErrorCodes.ERR_ENUM_TYPES_DUPLICATE_FIELD_NAMES,
				String.format(ErrorCodes.ERR_ENUM_TYPES_DUPLICATE_FIELD_NAMES.getErrorMessage(), enumTypes, fieldName),
				resourceHolder);
		final Map<@Nullable String, TypeElement> mapEnumTypes =
			new EnumTypesUtil(processingEnv, getDiagnostics()).getMapEnumTypes(fieldRef, onDuplicateFieldName);
		resourceHolder.getEnclosedElements().stream()
			.filter(element -> element.getKind().equals(FIELD))
			.map(element -> (VariableElement) element)
			.filter(getElementUtil()::checkElementPublic)
			.filter(getElementUtil()::checkFieldNotFinal)
			.forEach(field ->
			{
				final String resourceKey = getResourceKey(prefix, field);
//				final String fieldName = field.getSimpleName().toString();
				final TypeMirror fieldRawType = processingEnv.getTypeUtils().erasure(field.asType());
				switch (fieldRawType.toString())
				{
					case TYPE_NAME_STRING -> checkStringResource(resourceKey, field);
					case TYPE_NAME_BYTE_ARRAY -> checkBinaryResource(resourceKey, field);
					case TYPE_NAME_ENUM_STRING_MAP ->
						handleFieldEnumStrings(resourceKey, field, mapEnumTypes);
					case TYPE_NAME_ENUM_BYTE_ARRAY_MAP ->
						handleFieldEnumByteArrays(resourceKey, field, mapEnumTypes);
					default ->
					{
						// check custom types recursively
						final boolean isGenericResource;
						final Element typeElement;
						final TypeMirror typeMirror = field.asType();
						if (typeMirror instanceof DeclaredType declaredType)
						{
							getDiagnostics().trace("Check custom type", declaredType);
							typeElement = processingEnv.getTypeUtils().asElement(declaredType);
							isGenericResource = typeElement != null &&
								typeElement.getAnnotation(GenericResources.class) != null;
						}
						else
						{
							isGenericResource = false;
							typeElement = null;
						}
						if (isGenericResource && typeElement != null)
						{
							handleResourceHolder((TypeElement) typeElement, resourceKey, field);
						}
						else
						{
							final String msg = String.format(
								ERR_INVALID_FIELD_TYPE.getErrorMessage(), field, resourceHolder);
							getDiagnostics().reportError(ERR_INVALID_FIELD_TYPE, msg, field);
						}
					}
				}
			});
	}

	@Deprecated
	private void checkEnumMap(VariableElement field, String idField)
	{
		final DeclaredType declaredType = (DeclaredType) field.asType();
		final List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
		final DeclaredType enumType = (DeclaredType) typeArguments.get(0);
		final TypeElement enumClass = (TypeElement) processingEnv.getTypeUtils().asElement(enumType);
		if (enumClass != null)
		{
			final List<String> enumIds = enumClass.getEnclosedElements().stream()
				.filter(element -> ElementKind.ENUM_CONSTANT.equals(element.getKind()))
				.map(element -> idField + SEPARATOR_ENUM_KEY + element)
				.collect(Collectors.toList());
			switch (typeArguments.get(1).toString())
			{
				case TYPE_NAME_STRING:
					enumIds.forEach(id -> checkStringResource(id, field));
					break;
				case TYPE_NAME_BYTE_ARRAY:
					enumIds.forEach(id -> checkBinaryResource(id, field));
					break;
				default:
					getDiagnostics().reportError(ERR_INVALID_ENUM_MAP_FOR_ANNOTATION, field);
			}
		}
	}

	private void checkStringResource(String resourceKey, VariableElement field)
	{
		if (getResourceParameters().hasStringResources())
		{
			final String filePropertiesMain = getResourceParameters().getBasename() + ".properties";
			getDiagnostics().trace("Check string resource »" + resourceKey + "« in",
				getResourceParameters().getStringResourcePackageName() + "/" + filePropertiesMain);
			try
			{
				final FileObject resourceFile = processingEnv.getFiler().getResource(
					StandardLocation.CLASS_PATH, getResourceParameters().getStringResourcePackageName(), filePropertiesMain);
				try (InputStream inputStream = resourceFile.openInputStream())
				{
					final Properties properties = new Properties();
					properties.load(inputStream);
					if (!properties.containsKey(resourceKey))
					{
						final String msg = String.format(
							ERR_STRING_RESOURCE_NOT_FOUND.getErrorMessage(), resourceKey);
						getDiagnostics().reportMissingStringResource(msg, field);
					}
				}
			}
			catch (IOException ex)
			{
				getDiagnostics().reportMissingStringResource(ex.toString(), field);
			}
		}
		else
		{
			getDiagnostics().reportMissingStringResource("Missing @StringResources", field);
		}
	}

	private void checkBinaryResource(String resourceKey, VariableElement field)
	{
		if (getResourceParameters().hasBinaryResources())
		{
			final FileExt fileExt = field.getAnnotation(FileExt.class);
			final String ext = (fileExt != null) ? fileExt.value() : getResourceParameters().getDefaultExtension();
			final String fileName = resourceKey + ext;
			final String fileNameFull = getResourceParameters().getBinaryResourcePackageName() + '/' + fileName;
			getDiagnostics().trace("Check binary resource", fileNameFull);
			try
			{
				processingEnv.getFiler().getResource(StandardLocation.CLASS_PATH,
					getResourceParameters().getBinaryResourcePackageName(), fileName);
				getDiagnostics().trace("Found binary resource", fileNameFull);
			}
			catch (IOException ex)
			{
				final String msg = String.format(
					ERR_BINARY_RESOURCE_NOT_FOUND.getErrorMessage(), fileNameFull);
				getDiagnostics().reportMissingBinaryResource(msg, field);
			}
		}
		else
		{
			// TODO
			getDiagnostics().reportMissingBinaryResource("Missing @BinaryResources", field);
		}
	}

	private void handleFieldEnumStrings(String resourceKey, VariableElement field,
		Map<@Nullable String, TypeElement> mapEnumTypes)
	{
		if (getResourceParameters().hasStringResources())
		{
			getDiagnostics().trace("Handle field of raw type " + TYPE_NAME_ENUM_STRING_MAP, field);
			// TODO
		}
	}

	private void handleFieldEnumByteArrays(String resourceKey, VariableElement field,
		Map<@Nullable String, TypeElement> mapEnumTypes)
	{
		if (getResourceParameters().hasBinaryResources())
		{
			getDiagnostics().trace("Handle field of raw type " + TYPE_NAME_ENUM_BYTE_ARRAY_MAP, field);
			// TODO
		}
	}
}
