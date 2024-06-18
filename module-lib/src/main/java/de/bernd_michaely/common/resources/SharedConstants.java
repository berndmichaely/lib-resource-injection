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

/**
 * Constants common to both annotation processing and runtime injection.
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 */
public interface SharedConstants
{
	// separator strings:
	// (Note: the three separators are effectively the same and
	// distinction between them in source code is not really tested)
	String SEPARATOR_ENUM_FILE = ".";
	String SEPARATOR_ENUM_KEY = ".";
	String SEPARATOR_KEY_NESTED = ".";
	// type names:
	String TYPE_NAME_STRING = "java.lang.String";
//	String TYPE_NAME_BYTE_ARRAY = "byte[]";
//	String CLASS_NAME_BYTE_ARRAY = "[B";
	String TYPE_NAME_BYTE_ARRAY = "de.bernd_michaely.common.resources.OptionalBinaryObject";
	String TYPE_NAME_ENUM_STRING_MAP =
		"de.bernd_michaely.common.resources.EnumStringMap";
	String TYPE_NAME_ENUM_BYTE_ARRAY_MAP =
		"de.bernd_michaely.common.resources.EnumByteArrayMap";
//	@Deprecated
	String TYPE_NAME_ENUM_RESOURCE_HOLDER_MAP =
		"de.bernd_michaely.common.resources.EnumResourceHolderMap";
}
