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
package de.bernd_michaely.common.resources.sample.data;

import de.bernd_michaely.common.resources.ResourceHolder;
import de.bernd_michaely.common.resources.ResourceKey;
import de.bernd_michaely.common.resources.StringResources;

/**
 * Resource definitions for the info about dialog.
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 */
@StringResources(packagename = ".strings", basename = "string")
public class DialogResources extends ResourceHolder
{
	// reuse menu item resource here:
	@ResourceKey("menuitemInfoAbout")
	public String titleDialog;

	public String textDialog;
	public String labelClose;
}
