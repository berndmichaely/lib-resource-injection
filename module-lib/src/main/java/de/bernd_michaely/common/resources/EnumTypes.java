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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to communicate enum classes of {@link EnumStringMap}s and
 * {@link EnumByteArrayMap}s in nested {@link ResourceHolder}s at runtime.
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 * @see #enumType()
 * @see #fieldNames()
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Repeatable(EnumTypesContainer.class)
public @interface EnumTypes
{
	/**
	 * Indicates an enum type to use for {@link EnumStringMap}s and
	 * {@link EnumByteArrayMap}s in nested {@link ResourceHolder}s.
	 *
	 * @return an enum type
	 */
	Class<? extends Enum> enumType();

	/**
	 * Indicates the {@link EnumStringMap} and {@link EnumByteArrayMap} fields
	 * which use the {@link #enumType()}.
	 *
	 * @return an array of field names
	 */
	String[] fieldNames();
}
