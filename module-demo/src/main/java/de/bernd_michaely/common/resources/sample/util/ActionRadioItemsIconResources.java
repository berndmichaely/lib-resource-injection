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
package de.bernd_michaely.common.resources.sample.util;

import de.bernd_michaely.common.resources.EnumByteArrayMap;
import de.bernd_michaely.common.resources.GenericResources;

/**
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 * @param <E>
 */
@GenericResources
public class ActionRadioItemsIconResources<E extends Enum<E>>
	extends ActionRadioItemsResources<E>
{
	public EnumByteArrayMap<E> buttonIcons;
}
