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
package de.bernd_michaely.common.resources;

import java.util.Locale;
import java.util.function.IntPredicate;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Class to calculate a fallback value for a missing String resource from its
 * resource key.
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 */
class FallBackValueUtil implements SharedConstants
{
	static final String STR_NULL_REPLACEMENT = "»…«";
	private final Locale locale;

	FallBackValueUtil(Locale locale)
	{
		this.locale = locale;
	}

	/**
	 * Checks (roughly, e.g. ignoring keywords), whether the string is a Java
	 * identifier.
	 *
	 * @param str the string to check
	 * @return true, if the string is a Java identifier, false otherwise
	 */
	private boolean isIdentifier(String str)
	{
		if (str == null || str.isEmpty() || str.equals("_") ||
			!Character.isJavaIdentifierStart(str.codePointAt(0)))
		{
			return false;
		}
		return str.codePoints().skip(1)
			.filter(((IntPredicate) Character::isJavaIdentifierPart).negate())
			.findAny().isEmpty();
	}

	/**
	 * Returns the index of the nesting key separation character.
	 *
	 * @param key            the key string to check
	 * @param hasEnumPostfix true, if the key has an enum postfix
	 * @return the index of the nesting key separation character
	 */
	private static int getIndexNesting(String key, boolean hasEnumPostfix)
	{
		if (hasEnumPostfix && (SEPARATOR_KEY_NESTED.equals(SEPARATOR_ENUM_KEY)))
		{
			final int idxSepEnum = key.lastIndexOf(SEPARATOR_ENUM_KEY);
			return (idxSepEnum >= 0) ?
				key.substring(0, idxSepEnum).lastIndexOf(SEPARATOR_KEY_NESTED) : -1;
		}
		else
		{
			return key.lastIndexOf(SEPARATOR_KEY_NESTED);
		}
	}

	private static class FallBackValueBuilder
	{
		private final String identifier;
		private final StringBuilder stringBuilder = new StringBuilder();
		private int lastChar;

		private FallBackValueBuilder(String identifier)
		{
			this.identifier = identifier;
		}

		private String build()
		{
			lastChar = '0';
			identifier.codePoints().dropWhile(Character::isLowerCase).forEachOrdered(currentChar ->
			{
				if (currentChar == '_')
				{
					if (lastChar != '_')
					{
						addSpace();
					}
				}
				else if (Character.isDigit(currentChar))
				{
					if (!Character.isDigit(lastChar))
					{
						addSpace();
					}
					stringBuilder.appendCodePoint(currentChar);
				}
				else if (Character.isUpperCase(currentChar))
				{
					if (!(lastChar == '_' || Character.isUpperCase(lastChar)))
					{
						addSpace();
					}
					stringBuilder.appendCodePoint(currentChar);
				}
				else // Character::isLowerCase
				{
					if (lastChar == '_')
					{
						stringBuilder.appendCodePoint(Character.toUpperCase(currentChar));
					}
					else if (Character.isDigit(lastChar))
					{
						addSpace();
						stringBuilder.appendCodePoint(Character.toUpperCase(currentChar));
					}
					else
					{
						stringBuilder.appendCodePoint(currentChar);
					}
				}
				lastChar = currentChar;
			});
			return stringBuilder.toString();
		}

		private void addSpace()
		{
			if (stringBuilder.length() > 0)
			{
				stringBuilder.append(' ');
			}
		}
	}

	/**
	 * Calculates a resource string fallback value from a java identifier.
	 *
	 * @param identifier the java identifier
	 * @return a fallback value
	 */
	private String javaIdentifierToFallBackValue(String identifier)
	{
		return new FallBackValueBuilder(identifier).build();
	}

	/**
	 * Calculates a fallback value for a missing String resource from its key. A
	 * prefix consisting of lowercase chars will be dropped and spaces will be
	 * inserted according to camel case, e.g.: <code>"titleMainWindow"</code> will
	 * be turned into <code>"Main Window".</code> For enumerated keys, the enum
	 * constant name will be used, e.g. <code>"mapColors.RED"</code> will be
	 * turned into <code>"Red"</code>.
	 *
	 * @param key            the String resource key
	 * @param hasEnumPostfix true, if the key contains an enum postfix
	 * @return a fallback value for a missing String resource
	 */
	@NonNull
	String getFallBackValue(String key, boolean hasEnumPostfix)
	{
		if (key != null && !key.isBlank())
		{
			int indexNesting = getIndexNesting(key, hasEnumPostfix);
			final boolean hasNestedResourceHolderPrefix = indexNesting >= 0;
			final String baseKey = hasNestedResourceHolderPrefix ? key.substring(indexNesting + 1) : key;
			final int indexOfEnumSeparator = baseKey.lastIndexOf(SEPARATOR_ENUM_KEY);
			final boolean isPostfixEmpty = indexOfEnumSeparator == (baseKey.length() - 1);
			if (hasEnumPostfix && !isPostfixEmpty)
			{
				final String postfix = baseKey.substring(indexOfEnumSeparator + SEPARATOR_ENUM_KEY.length());
				return postfix.substring(0, 1).toUpperCase(locale) +
					postfix.substring(1).toLowerCase(locale);
			}
			else
			{
				final String prefix = hasEnumPostfix ?
					baseKey.substring(0, indexOfEnumSeparator) : baseKey;
				return isIdentifier(prefix) ? javaIdentifierToFallBackValue(prefix) : prefix;
			}
		}
		else
		{
			return STR_NULL_REPLACEMENT;
		}
	}
}
