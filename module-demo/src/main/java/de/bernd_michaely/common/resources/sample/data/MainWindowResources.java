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

import de.bernd_michaely.common.resources.BinaryResources;
import de.bernd_michaely.common.resources.EnumTypes;
import de.bernd_michaely.common.resources.ResourceHolder;
import de.bernd_michaely.common.resources.ResourceKey;
import de.bernd_michaely.common.resources.StringResources;
import de.bernd_michaely.common.resources.sample.util.ActionRadioItemsIconResources;
import de.bernd_michaely.common.resources.sample.util.ActionRadioItemsResources;
import de.bernd_michaely.common.resources.sample.util.Colors;
import de.bernd_michaely.common.resources.sample.util.SupportedLocales;

/**
 * Resource holder for the main window resources.
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 */
@StringResources(packagename = ".strings", basename = "string")
@BinaryResources(packagename = ".binary", defaultExtension = ".png")
public class MainWindowResources extends ResourceHolder
{
	public String titleMainWindow;
	public String titleGreeting;

	public String menuFile;
	@ResourceKey("title-exit")
	public String menuitemExit;

	public String menuHelp;
	public String menuitemInfoAbout;

	@EnumTypes(enumType = SupportedLocales.class, fieldNames =
					 {
						 "menuTitles", "buttonTitles", "buttonTooltips"
	})
	//	@EnumTypes(enumType = SupportedLocales.class, fieldNames = "menuTitles")
	public ActionRadioItemsResources<SupportedLocales> supportedLocales;

	@EnumTypes(enumType = Colors.class, fieldNames =
					 {
						 "menuTitles", "buttonTitles", "buttonTooltips", "buttonIcons"
	})
	public ActionRadioItemsIconResources<Colors> menuColors;

//	public int _invalid_;
}
