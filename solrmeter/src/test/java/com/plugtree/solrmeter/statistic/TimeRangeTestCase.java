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
package com.plugtree.solrmeter.statistic;

import com.plugtree.solrmeter.BaseTestCase;
import com.plugtree.solrmeter.model.statistic.TimeRange;

public class TimeRangeTestCase extends BaseTestCase {

	public void testIsIncluded() {
		TimeRange timeRange = new TimeRange(1000, 2000);
		assertTrue(timeRange.isIncluded(1500));
		assertFalse(timeRange.isIncluded(2500));
		assertFalse(timeRange.isIncluded(500));
		assertFalse(timeRange.isIncluded(-500));
	}
	
	public void testEquals() {
		TimeRange timeRange1 = new TimeRange(1000, 2000);
		TimeRange timeRange2 = new TimeRange(1000, 3000);
		TimeRange timeRange3 = new TimeRange(500, 2000);
		TimeRange timeRange4 = new TimeRange(2000);
		TimeRange timeRange5 = new TimeRange(1000, 2000);
		
		assertFalse(timeRange1.equals(timeRange2));
		assertFalse(timeRange2.equals(timeRange1));
		
		assertFalse(timeRange1.equals(timeRange3));
		assertFalse(timeRange3.equals(timeRange1));
		
		assertFalse(timeRange1.equals(timeRange4));
		assertFalse(timeRange4.equals(timeRange1));
		
		assertTrue(timeRange1.equals(timeRange5));
		assertTrue(timeRange5.equals(timeRange1));
	}
	
}
