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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Test class for the demo application.
 *
 * @author Bernd Michaely (info@bernd-michaely.de)
 */
public class SampleResourceConsumingAppTest
{
	/**
	 * Test of class SampleResourceConsumingApp. The application will be
	 * instantiated and run until the point immediately before MainWindow#show,
	 * and it will be verified, that no exceptions are thrown.
	 */
	@Test
	public void testInit()
	{
		System.out.println("SampleResourceConsumingAppTest → init");
		assertDoesNotThrow(ApplicationBase::runTest);
	}
}
