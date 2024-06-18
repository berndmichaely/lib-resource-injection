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

/**
 * Library for simple runtime injection of string and binary resources.
 * <h2>Notes</h2>
 * Examination of the usage of
 * {@link java.util.spi.ResourceBundleProvider ResourceBundleProviders}:
 * <ul>
 * <li>This would require a call of
 * {@link Module#addUses(Class) libraryModule.addUses(spiClass)}
 * at runtime and loading the string resource on behalf of the library module.
 * </li>
 * <li>→ This distinction of cases would suggest a
 * <code>@ResourcesProvider (modulename<sub>opt</sub>, basename)</code>
 * as opposed to
 * <code>@StringResources (modulename<sub>opt</sub>, packagename, basename)</code>.
 * </li>
 * <li>Build process requirement: the annotation processing would need to use the
 * already precompiled service provider module.
 * </li>
 * </ul>
 */
module de.bernd_michaely.common.resources
{
	requires java.compiler;
	requires org.checkerframework.checker.qual;
	exports de.bernd_michaely.common.resources;
}
