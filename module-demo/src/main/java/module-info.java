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
 * Demo application for the {@code de.bernd_michaely.common.resources} lib.
 */
module de.bernd_michaely.common.resources.demo
{
	requires de.bernd_michaely.common.resources;
	requires javafx.controls;
	requires org.checkerframework.checker.qual;

	opens de.bernd_michaely.common.resources.sample to javafx.graphics;

	exports de.bernd_michaely.common.resources.sample.data to de.bernd_michaely.common.resources;
	exports de.bernd_michaely.common.resources.sample.util to de.bernd_michaely.common.resources;

	opens de.bernd_michaely.common.resources.sample.data.strings to de.bernd_michaely.common.resources;
	opens de.bernd_michaely.common.resources.sample.data.binary to de.bernd_michaely.common.resources;
}
