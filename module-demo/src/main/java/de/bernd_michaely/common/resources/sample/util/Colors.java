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

import javafx.scene.paint.Color;

/**
 * Enumeration of sample colors.
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 */
public enum Colors
{
	RED(Color.FIREBRICK),
	ORANGE(Color.ORANGE),
	YELLOW(Color.YELLOW),
	GREEN(Color.GREEN),
	BLUE(Color.ROYALBLUE),
	PURPLE(Color.MEDIUMPURPLE);

	private final Color color;

	private Colors(Color color)
	{
		this.color = color;
	}

	/**
	 * Returns the color code.
	 *
	 * @return the color code
	 */
	public Color getColor()
	{
		return this.color;
	}
}
