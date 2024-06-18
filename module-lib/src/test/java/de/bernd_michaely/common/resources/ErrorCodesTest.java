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

import java.util.TreeSet;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for enum ErrorCodes.
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 */
public class ErrorCodesTest
{
	@Test
	public void testErrorNumberUniqueness()
	{
		System.out.println("testErrorCodeUniqueness");
		final var setCodes = new TreeSet<Integer>();
		for (ErrorCodes errorCode : ErrorCodes.values())
		{
			final int value = errorCode.getErrorNumber();
			assertFalse(setCodes.contains(value),
				String.format("Duplicate error code %d for enum constant »%s.%s«",
					value, ErrorCodes.class.getName(), errorCode.name()));
			setCodes.add(value);
		}
	}

	@Test
	public void testMapErrorByCode()
	{
		for (ErrorCodes errorCode : ErrorCodes.values())
		{
			assertEquals(errorCode, ErrorCodes.getValueByNumber(errorCode.getErrorNumber()));
		}
	}
}
