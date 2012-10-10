/**
 * Copyright Plugtree LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.plugtree.solrmeter.model.generator;

import com.plugtree.solrmeter.BaseTestCase;
import com.plugtree.solrmeter.mock.ComplexQueryGeneratorSpy;

public class ComplexQueryGeneratorTestCase extends BaseTestCase {

	public void testLoadExtraParameters() {
		ComplexQueryGeneratorSpy generator = new ComplexQueryGeneratorSpy();
		generator.loadExtraParameters("");
		assertTrue(generator.getExtraParameters().isEmpty());

		generator.loadExtraParameters("param=value");
		assertFalse(generator.getExtraParameters().isEmpty());
		assertNotNull(generator.getExtraParameters().get("param"));
		assertEquals("value", generator.getExtraParameters().get("param"));

		generator.loadExtraParameters("param=value, param2=value2");
		assertFalse(generator.getExtraParameters().isEmpty());
		assertNotNull(generator.getExtraParameters().get("param"));
		assertEquals("value", generator.getExtraParameters().get("param"));
		assertNotNull(generator.getExtraParameters().get("param2"));
		assertEquals("value2", generator.getExtraParameters().get("param2"));

		generator.loadExtraParameters("param=value, param2=value2, ");
		assertFalse(generator.getExtraParameters().isEmpty());
		assertNotNull(generator.getExtraParameters().get("param"));
		assertEquals("value", generator.getExtraParameters().get("param"));
		assertNotNull(generator.getExtraParameters().get("param2"));
		assertEquals("value2", generator.getExtraParameters().get("param2"));

		generator.loadExtraParameters("param=value, param2=value2, \"param3=one, two, three\"");
		assertFalse(generator.getExtraParameters().isEmpty());
		assertNotNull(generator.getExtraParameters().get("param"));
		assertEquals("value", generator.getExtraParameters().get("param"));
		assertNotNull(generator.getExtraParameters().get("param2"));
		assertEquals("value2", generator.getExtraParameters().get("param2"));
		assertNotNull(generator.getExtraParameters().get("param3"));
		assertEquals("one, two, three", generator.getExtraParameters().get("param3"));
	}
}
