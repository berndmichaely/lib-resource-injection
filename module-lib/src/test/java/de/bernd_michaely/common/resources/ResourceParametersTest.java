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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 */
public class ResourceParametersTest
{
	@Test
	public void testNotAnnotated()
	{
		class RhImpl extends ResourceHolder
		{
		}
		final ResourceParameters params = new ResourceParameters(RhImpl.class);
		assertFalse(params.hasStringResources());
		assertFalse(params.hasBinaryResources());
		assertFalse(params.hasAnyResources());
	}

	@Test
	public void testWithStringResources()
	{
		@StringResources(packagename = ".strings", basename = "string")
		class RhImpl extends ResourceHolder
		{
		}
		final ResourceParameters params = new ResourceParameters(RhImpl.class);
		assertTrue(params.hasStringResources());
		assertFalse(params.hasBinaryResources());
		assertTrue(params.hasAnyResources());
		assertEquals(ResourceParametersTest.class.getPackageName() + ".strings",
			params.getStringResourcePackageName());
		assertEquals("string", params.getBasename());
	}

	@Test
	public void testWithBinaryResources()
	{
		@BinaryResources(packagename = ".binary")
		class RhImpl extends ResourceHolder
		{
		}
		final ResourceParameters params = new ResourceParameters(RhImpl.class);
		assertFalse(params.hasStringResources());
		assertTrue(params.hasBinaryResources());
		assertTrue(params.hasAnyResources());
		assertEquals(ResourceParametersTest.class.getPackageName() + ".binary",
			params.getBinaryResourcePackageName());
		assertEquals("", params.getDefaultExtension());
	}

	@Test
	public void testWithStringAndBinaryResources()
	{
		@StringResources(
			modulename = "java.base", packagename = "a.c", basename = "str")
		@BinaryResources(
			modulename = "java.base", packagename = "b.d", defaultExtension = ".bin")
		class RhImpl extends ResourceHolder
		{
		}
		final ResourceParameters params = new ResourceParameters(RhImpl.class);
		assertTrue(params.hasStringResources());
		assertTrue(params.hasBinaryResources());
		assertTrue(params.hasAnyResources());
		assertEquals("a.c", params.getStringResourcePackageName());
		assertEquals("str", params.getBasename());
		assertEquals("b.d", params.getBinaryResourcePackageName());
		assertEquals(".bin", params.getDefaultExtension());
		final Module module = Void.class.getModule();
		assertEquals(module, params.getStringResourcesModule(null));
		assertEquals(module, params.getBinaryResourcesModule(null));
	}

	@Test
	public void testAbsolutePackageEmpty()
	{
		final ResourceParameters params = new ResourceParameters(null, null, null);
		assertEquals("", params.getAbsolutePackage(null));
		assertEquals("", params.getAbsolutePackage(""));
	}

	@Test
	public void testAbsolutePackageRelative()
	{
		@StringResources(packagename = ".subpackage", basename = "")
		class RhImpl extends ResourceHolder
		{
		}
		final ResourceParameters params = new ResourceParameters(RhImpl.class);
		assertEquals(ResourceParametersTest.class.getPackageName() + ".subpackage",
			params.getStringResourcePackageName());
	}
}
