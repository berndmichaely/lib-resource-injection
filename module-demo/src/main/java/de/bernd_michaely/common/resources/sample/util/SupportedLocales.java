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

import java.util.Locale;

/**
 * Enumeration of locales supported by this application.
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 */
public enum SupportedLocales
{
	ENGLISH(Locale.ENGLISH), FRENCH(Locale.FRENCH), GERMAN(Locale.GERMAN);

	private final Locale locale;

	private SupportedLocales(Locale locale)
	{
		this.locale = locale;
	}

	/**
	 * Returns the appropriate locale.
	 *
	 * @return the matching locale
	 */
	public Locale getLocale()
	{
		return this.locale;
	}
}
