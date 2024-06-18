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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;

/**
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 */
public class GraphicsUtil
{
	public static final double DEFAULT_SIZE = Font.getDefault().getSize();
	private static final double ICON_SIZE = DEFAULT_SIZE;

	public static ImageView getIcon(byte[] data, boolean originalSize)
	{
		try (InputStream inputStream = new ByteArrayInputStream(data))
		{
			return originalSize ?
				new ImageView(new Image(inputStream)) :
				new ImageView(new Image(inputStream, ICON_SIZE, ICON_SIZE, true, true));
		}
		catch (NullPointerException | IOException ex)
		{
			return new ImageView();
		}
	}
}
