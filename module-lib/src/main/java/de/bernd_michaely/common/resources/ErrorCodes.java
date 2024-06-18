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
package de.bernd_michaely.common.resources;

import java.util.Map;
import org.checkerframework.checker.nullness.qual.Nullable;

import static java.util.Arrays.stream;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

/**
 * Enumeration of annotation processing error codes and messages.
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 */
public enum ErrorCodes
{
	ERR_NO_ERROR(0, ""),
	ERR_UNKNOWN(1001, "an unknown (non resource annotation processor related) error occured"),
	ERR_ELEMENT_NOT_PUBLIC(1002,
		"Element »%s« must be public"),
	ERR_FIELD_NOT_PUBLIC(1003,
		"Field »%s« must be public"),
	ERR_CLASS_NOT_PUBLIC(1004,
		"Class »%s« must be public"),
	ERR_FIELD_FINAL(1005,
		"Field »%s« must not be final"),
	ERR_RESOURCE_HOLDER_NOT_ANNOTATED(1011,
		"ResourceHolder type »%s« has neither @StringResources nor @BinaryResources"),
	ERR_RESOURCE_HOLDER_AND_GENERIC(1012,
		"ResourceHolder type »%s« must not have @GenericResources"),
	ERR_GENERIC_RESOURCES_ANN(1013,
		"Type »%s« with @GenericResources must not have @StringResources or @BinaryResources"),
	ERR_TYPE_NOT_RESOURCE_HOLDER(1014,
		"Type »%s« annotated with @StringResources or @BinaryResources must extend class ResourceHolder"),
	ERR_INVALID_FIELD_TYPE(1015,
		"Invalid type for field »%s« in ResourceHolder »%s«"),
	ERR_INVALID_ENUM_MAP_FOR_ANNOTATION(1016,
		"Invalid value type for EnumMap with @Inject"),
	ERR_STRING_RESOURCE_NOT_FOUND(1101,
		"Resource with identifier »%s« not found"),
	ERR_BINARY_RESOURCE_NOT_FOUND(1102,
		"Resource »%s« not found"),
	@Deprecated
	ERR_DUPLICATE_FIELD_NAME(1103,
		"Multiple declarations of fieldname %s in @EnumTypes"),
	ERR_ENUM_TYPES_DUPLICATE_FIELD_NAMES(1104,
		"»@EnumTypes(…) […] %s« contains duplicate field name »%s«"),
	ERR_INTERNAL_STATE(1201,
		"Internal state error in »%s« : expected »%s«");

	private static final Map<Integer, ErrorCodes> mapErrorByNumber;

	private final int errorNumber;
	private final String errorMessage;

	static
	{
		mapErrorByNumber = stream(values()).collect(toMap(ErrorCodes::getErrorNumber, identity()));
	}

	private ErrorCodes(int errorNumber, String errorMessage)
	{
		this.errorNumber = errorNumber;
		this.errorMessage = errorMessage;
	}

	public int getErrorNumber()
	{
		return errorNumber;
	}

	public String getErrorMessage()
	{
		return errorMessage;
	}

	public static @Nullable
	ErrorCodes getValueByNumber(int errorNumber)
	{
		return mapErrorByNumber.get(errorNumber);
	}
}
