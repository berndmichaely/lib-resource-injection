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
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to communicate enum classes of {@link EnumStringMap}s and
 * {@link EnumByteArrayMap}s in {@link ResourceHolder}s at runtime.
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 * @see #value()
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface EnumType
{
	Class<? extends Enum> value();
}
