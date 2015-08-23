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
package com.plugtree.solrmeter;

import com.plugtree.solrmeter.ExpectedParameter;

public class ExpectedParameterTestCase extends BaseTestCase {
	
	public void testEmpty() {
		String[] args = new String[]{};
		ExpectedParameter param = new ExpectedParameter(args, "testParam", "default");
		assertEquals("default", param.getValue());
	}
	
	public void testInvalid() {
		String[] args1 = new String[]{"testParam=1"};
		String[] args2 = new String[]{"-DtestParam 1"};
		ExpectedParameter param = new ExpectedParameter(args1, "testParam", "default");
		assertEquals("default", param.getValue());
		
		try {
			new ExpectedParameter(args2, "testParam", "default");
			fail("An exception is expected");
		}catch(RuntimeException exception) {
			//expected
		}
	}
	
	public void testValid() {
		String[] args1 = new String[]{"-DtestParam=1"};
		String[] args2 = new String[]{"-DtestParam=1", "some", "other", "args"};
		String[] args3 = new String[]{"-DtestParam=1", "some", "other", "args", "-DtestParam=2"};
		assertEquals("1", new ExpectedParameter(args1, "testParam", "default").getValue());
		assertEquals("1", new ExpectedParameter(args2, "testParam", "default").getValue());
		assertEquals("1", new ExpectedParameter(args3, "testParam", "default").getValue());
	}

}
