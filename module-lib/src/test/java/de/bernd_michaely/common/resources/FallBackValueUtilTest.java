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
import org.junit.jupiter.api.Test;

import static de.bernd_michaely.common.resources.SharedConstants.SEPARATOR_ENUM_KEY;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for class FallBackValueUtil.
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 */
public class FallBackValueUtilTest
{
	/**
	 * Test of getFallbackValue method, of class FallBackValueUtil.
	 */
	@Test
	public void testGetFallbackValue_null()
	{
		System.out.println("getFallbackValue –> null");
		final String key = null;
		final String expResult = FallBackValueUtil.STR_NULL_REPLACEMENT;
		final String result = new FallBackValueUtil(Locale.ROOT).getFallBackValue(key, false);
		assertEquals(expResult, result);
	}

	/**
	 * Test of getFallbackValue method, of class FallBackValueUtil.
	 */
	@Test
	public void testGetFallbackValue_empty()
	{
		System.out.println("getFallbackValue –> empty key");
		final String key = "";
		final String expResult = FallBackValueUtil.STR_NULL_REPLACEMENT;
		final String result = new FallBackValueUtil(Locale.ROOT).getFallBackValue(key, false);
		assertEquals(expResult, result);
	}

	/**
	 * Test of getFallbackValue method, of class FallBackValueUtil.
	 */
	@Test
	public void testGetFallbackValue_key1()
	{
		System.out.println("getFallbackValue –> sample key");
		final String key = "titleMainWindow";
		final String expResult = "Main Window";
		final String result = new FallBackValueUtil(Locale.ROOT).getFallBackValue(key, false);
		assertEquals(expResult, result);
	}

	/**
	 * Test of getFallbackValue method, of class FallBackValueUtil.
	 */
	@Test
	public void testGetFallbackValue_key2()
	{
		System.out.println("getFallbackValue –> sample key");
		final String key = "titleMain1Window234___with_HTML5___content";
		final String expResult = "Main 1 Window 234 With HTML 5 Content";
		final String result = new FallBackValueUtil(Locale.ROOT).getFallBackValue(key, false);
		assertEquals(expResult, result);
	}

	/**
	 * Test of getFallbackValue method, of class FallBackValueUtil.
	 */
	@Test
	public void testGetFallbackValue_key3()
	{
		System.out.println("getFallbackValue –> sample key");
		final String key = "___main_____window";
		final String expResult = "Main Window";
		final String result = new FallBackValueUtil(Locale.ROOT).getFallBackValue(key, false);
		assertEquals(expResult, result);
	}

	/**
	 * Test of getFallbackValue method, of class FallBackValueUtil.
	 */
	@Test
	public void testGetFallbackValue_nested_1_level()
	{
		System.out.println("getFallbackValue –> sample nested 1 level");
		final String key = "menu.titleButton";
		final String expResult = "Button";
		final String result = new FallBackValueUtil(Locale.ROOT).getFallBackValue(key, false);
		assertEquals(expResult, result);
	}

	/**
	 * Test of getFallbackValue method, of class FallBackValueUtil.
	 */
	@Test
	public void testGetFallbackValue_nested_2_levels()
	{
		System.out.println("getFallbackValue –> sample nested 2 levels");
		final String key = "mainWindow.menu.titleButton";
		final String expResult = "Button";
		final String result = new FallBackValueUtil(Locale.ROOT).getFallBackValue(key, false);
		assertEquals(expResult, result);
	}

	/**
	 * Test of getFallbackValue method, of class FallBackValueUtil.
	 */
	@Test
	public void testGetFallbackValue_postfix()
	{
		System.out.println("getFallbackValue –> postfix");
		final String key = "mapColors" + SEPARATOR_ENUM_KEY + "RED";
		final String expResult = "Red";
		final String result = new FallBackValueUtil(Locale.ROOT).getFallBackValue(key, true);
		assertEquals(expResult, result);
	}

	/**
	 * Test of getFallbackValue method, of class FallBackValueUtil.
	 */
	@Test
	public void testGetFallbackValue_postfix_empty()
	{
		System.out.println("getFallbackValue –> postfix empty");
		final String key = "mapColors" + SEPARATOR_ENUM_KEY + "";
		final String expResult = "Colors";
		final String result = new FallBackValueUtil(Locale.ROOT).getFallBackValue(key, true);
		assertEquals(expResult, result);
	}

	/**
	 * Test of getFallbackValue method, of class FallBackValueUtil.
	 */
	@Test
	public void testGetFallbackValue_postfix_len1()
	{
		System.out.println("getFallbackValue –> postfix of length 1");
		final String key = "mapColors" + SEPARATOR_ENUM_KEY + "R";
		final String expResult = "R";
		final String result = new FallBackValueUtil(Locale.ROOT).getFallBackValue(key, true);
		assertEquals(expResult, result);
	}

	/**
	 * Test of getFallbackValue method, of class FallBackValueUtil.
	 */
	@Test
	public void testGetFallbackValue_postfix_nested_1_level()
	{
		System.out.println("getFallbackValue –> postfix; nested 1 level");
		final String key = "menu.mapColors" + SEPARATOR_ENUM_KEY + "RED";
		final String expResult = "Red";
		final String result = new FallBackValueUtil(Locale.ROOT).getFallBackValue(key, true);
		assertEquals(expResult, result);
	}

	/**
	 * Test of getFallbackValue method, of class FallBackValueUtil.
	 */
	@Test
	public void testGetFallbackValue_postfix_nested_2_levels()
	{
		System.out.println("getFallbackValue –> postfix; nested 2 levels");
		final String key = "mainWindow.menu.mapColors" + SEPARATOR_ENUM_KEY + "RED";
		final String expResult = "Red";
		final String result = new FallBackValueUtil(Locale.ROOT).getFallBackValue(key, true);
		assertEquals(expResult, result);
	}

	/**
	 * Test of getFallbackValue method, of class FallBackValueUtil.
	 */
	@Test
	public void testGetFallbackValue_nonJavaIdentifierKey()
	{
		System.out.println("getFallbackValue –> nonJavaIdentifierKey");
		final String key = "test-key";
		assertDoesNotThrow(() -> new FallBackValueUtil(Locale.ROOT).getFallBackValue(key, true));
		final String expResult = "test-key";
		final String result = new FallBackValueUtil(Locale.ROOT).getFallBackValue(key, false);
		assertEquals(expResult, result);
	}
}
