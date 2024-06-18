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

/**
 * Interface which just specializes the general Map interfaces generic types to
 * an enum type for keys and ResourceHolder for values.
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 * @param <E> the key enum type
 * @param <R> the ResourceHolder type
 */
@Deprecated
public interface EnumResourceHolderMap<E extends Enum<E>, R extends ResourceHolder>
	extends Map<E, R>
{
}
