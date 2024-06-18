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

import de.bernd_michaely.common.resources.sample.util.InfoAboutDialog;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * Base class to test and start the LibResourceInjection sample and demo
 * application,
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 */
public class ApplicationBase extends Application
{
	private MainWindow mainWindow;
	private static boolean testMode;

	static void runTest()
	{
		testMode = true;
		launch();
	}

	@Override
	public void init() throws Exception
	{
		super.init();
		mainWindow = new MainWindow();
		mainWindow.init();
	}

	@Override
	public void start(Stage stage) throws Exception
	{
		mainWindow.start(stage);
		if (testMode)
		{
			System.out.format("Stage title = »%s«%n", stage.getTitle());
			new InfoAboutDialog(stage, mainWindow.getResourceLoader()).init();
			Platform.runLater(Platform::exit);
		}
		else
		{
			stage.show();
		}
	}
}
