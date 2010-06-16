/**
 * Copyright Linebee LLC
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
package com.linebee.solrmeter;

import com.linebee.solrmeter.mock.QueryExecutorSpy;

public class QueryExecutorTestCase extends BaseTestCase {

	public void testLoadExtraParameters() {
		QueryExecutorSpy executor = new QueryExecutorSpy();
		executor.loadExtraParameters("");
		assertTrue(executor.getExtraParameters().isEmpty());
		
		executor.loadExtraParameters("param=value");
		assertFalse(executor.getExtraParameters().isEmpty());
		assertNotNull(executor.getExtraParameters().get("param"));
		assertEquals("value", executor.getExtraParameters().get("param"));
		
		executor.loadExtraParameters("param=value, param2=value2");
		assertFalse(executor.getExtraParameters().isEmpty());
		assertNotNull(executor.getExtraParameters().get("param"));
		assertEquals("value", executor.getExtraParameters().get("param"));
		assertNotNull(executor.getExtraParameters().get("param2"));
		assertEquals("value2", executor.getExtraParameters().get("param2"));
		
		executor.loadExtraParameters("param=value, param2=value2, ");
		assertFalse(executor.getExtraParameters().isEmpty());
		assertNotNull(executor.getExtraParameters().get("param"));
		assertEquals("value", executor.getExtraParameters().get("param"));
		assertNotNull(executor.getExtraParameters().get("param2"));
		assertEquals("value2", executor.getExtraParameters().get("param2"));
	}
}
