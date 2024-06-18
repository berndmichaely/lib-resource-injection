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
package de.bernd_michaely.common.resources.sample;

import de.bernd_michaely.common.resources.ResourceLoader;
import de.bernd_michaely.common.resources.sample.data.MainWindowResources;
import de.bernd_michaely.common.resources.sample.util.ActionRadioItems;
import de.bernd_michaely.common.resources.sample.util.Colors;
import de.bernd_michaely.common.resources.sample.util.InfoAboutDialog;
import de.bernd_michaely.common.resources.sample.util.SupportedLocales;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ToolBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import static de.bernd_michaely.common.resources.sample.util.GraphicsUtil.*;

/**
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 */
class MainWindow
{
	private final ResourceLoader resourceLoader;
	private InfoAboutDialog infoAboutDialog;
	private final MenuBar menuBar;
	private final Menu menuFile;
	private final Menu menuHelp;
	private ActionRadioItems<Colors> actionRadioItemsColors;
	private ActionRadioItems<SupportedLocales> actionRadioItemsSupportedLocales;
	private final MenuItem menuItemExit;
	private final MenuItem menuItemInfoAbout;
	private final ToolBar toolBar;
	private final Text text;
	private final BorderPane root;

	MainWindow()
	{
		resourceLoader = new ResourceLoader();
		toolBar = new ToolBar();
		menuFile = new Menu();
		menuHelp = new Menu();
		menuItemExit = new MenuItem();
		menuItemInfoAbout = new MenuItem();
		menuBar = new MenuBar();
		text = new Text();
		root = new BorderPane();
	}

	ResourceLoader getResourceLoader()
	{
		return resourceLoader;
	}

	private void setColors(Colors colors)
	{
		root.setBackground(new Background(new BackgroundFill(
			colors.getColor(), CornerRadii.EMPTY, Insets.EMPTY)));
		final Color colorShadow;
		switch (colors)
		{
			case ORANGE, YELLOW ->
			{
				text.setFill(Color.BLACK);
				colorShadow = Color.GRAY;
			}
			default ->
			{
				text.setFill(Color.WHITE);
				colorShadow = Color.LIGHTGRAY;
			}
		}
		final double shadowSize = DEFAULT_SIZE;
		text.setEffect(new DropShadow(shadowSize, shadowSize, shadowSize, colorShadow));
	}

	void init() throws Exception
	{
		resourceLoader.setLocale(SupportedLocales.ENGLISH.getLocale());
		actionRadioItemsSupportedLocales = new ActionRadioItems<>(
			SupportedLocales.class, menuFile, toolBar,
			supportedLocale -> resourceLoader.setLocale(supportedLocale.getLocale()));
		menuFile.getItems().addAll(new SeparatorMenuItem());
		toolBar.getItems().add(new Separator());
		actionRadioItemsColors = new ActionRadioItems<>(
			Colors.class, menuFile, toolBar, this::setColors);
		menuItemExit.setOnAction(e -> Platform.exit());
		menuItemInfoAbout.setOnAction(e -> infoAboutDialog.show());
		menuFile.getItems().addAll(new SeparatorMenuItem(), menuItemExit);
		menuHelp.getItems().add(menuItemInfoAbout);
		menuBar.getMenus().addAll(menuFile, menuHelp);
	}

	void start(Stage stage) throws Exception
	{
		infoAboutDialog = new InfoAboutDialog(stage, resourceLoader);
		final double fontSize = 6 * DEFAULT_SIZE;
		text.setFont(new Font(fontSize));
		final VBox vBox = new VBox(menuBar, toolBar);
		root.setCenter(new Group(text));
		root.setTop(vBox);
		final Scene scene = new Scene(root);
		// register callback to set i18n resources:
		resourceLoader.register(MainWindowResources.class, resources ->
		{
			menuFile.setText(resources.menuFile);
			menuHelp.setText(resources.menuHelp);
			actionRadioItemsSupportedLocales.setResources(resources.supportedLocales);
			actionRadioItemsColors.setResources(resources.menuColors);
			menuItemExit.setText(resources.menuitemExit);
			menuItemInfoAbout.setText(resources.menuitemInfoAbout);
			text.setText(resources.titleGreeting);
			stage.setTitle(resources.titleMainWindow);
		});
		setColors(Colors.RED);
		stage.setScene(scene);
		stage.setWidth(900);
		stage.setHeight(500);
	}
}
