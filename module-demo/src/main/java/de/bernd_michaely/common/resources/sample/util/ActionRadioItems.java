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

import java.util.EnumMap;
import java.util.function.Consumer;
import javafx.scene.control.Menu;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;

import static de.bernd_michaely.common.resources.sample.util.GraphicsUtil.getIcon;

/**
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 * @param <E>
 */
public class ActionRadioItems<E extends Enum<E>>
{
	private final EnumMap<E, RadioMenuItem> mapMenuItems;
	private final EnumMap<E, ToggleButton> mapToggleButtons;

	public ActionRadioItems(Class<E> enumClass, Menu parentMenu, ToolBar toolBar, Consumer<E> action)
	{
		this.mapMenuItems = new EnumMap<>(enumClass);
		this.mapToggleButtons = new EnumMap<>(enumClass);
		final ToggleGroup toggleGroupMenu = new ToggleGroup();
		final ToggleGroup toggleGroupButtons = new ToggleGroup();
		boolean isFirst = true;
		for (E enumConstant : enumClass.getEnumConstants())
		{
			final RadioMenuItem radioMenuItem = new RadioMenuItem();
			radioMenuItem.setOnAction(e ->
			{
				action.accept(enumConstant);
				mapToggleButtons.get(enumConstant).setSelected(true);
			});
			radioMenuItem.setToggleGroup(toggleGroupMenu);
			final ToggleButton toggleButton = new ToggleButton();
			toggleButton.setOnAction(e ->
			{
				action.accept(enumConstant);
				mapMenuItems.get(enumConstant).setSelected(true);
			});
			toggleButton.setToggleGroup(toggleGroupButtons);
			if (isFirst)
			{
				radioMenuItem.setSelected(true);
				toggleButton.setSelected(true);
				isFirst = false;
			}
			mapMenuItems.put(enumConstant, radioMenuItem);
			parentMenu.getItems().add(radioMenuItem);
			mapToggleButtons.put(enumConstant, toggleButton);
			toolBar.getItems().add(toggleButton);
		}
	}

	public void setResources(ActionRadioItemsResources<E> resources)
	{
		mapMenuItems.keySet().forEach(enumConstant ->
			mapMenuItems.get(enumConstant).setText(resources.menuTitles.get(enumConstant)));
		mapToggleButtons.keySet().forEach(enumConstant ->
		{
			final ToggleButton button = mapToggleButtons.get(enumConstant);
			button.setText(resources.buttonTitles.get(enumConstant));
			button.setTooltip(new Tooltip(resources.buttonTooltips.get(enumConstant)));
			if (resources instanceof ActionRadioItemsIconResources)
			{
				final ActionRadioItemsIconResources<E> iconResources =
					(ActionRadioItemsIconResources<E>) resources;
				button.setGraphic(getIcon(iconResources.buttonIcons.get(enumConstant), false));
			}
		});
	}
}
