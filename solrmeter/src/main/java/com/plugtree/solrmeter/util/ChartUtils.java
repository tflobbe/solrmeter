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
package com.plugtree.solrmeter.util;

import org.jfree.chart.axis.TickUnit;
import org.jfree.chart.axis.TickUnitSource;

public class ChartUtils {
	
	/**
	 * This class decorates a TickUnitSource to force a lower bound. For example,
	 * if you want your minimum tick unit to be X, you should construct a new
	 * instance of this tick unit source, passing it the previous tick unit 
	 * source, and the lower bound.
	 *
	 */
	public static class LowerBoundedTickUnitSource implements TickUnitSource {
		
		private TickUnit lowerUnit;
		
		private TickUnitSource decorated;
		
		/**
		 * Constructs a new LowerBoundedTickUnitSource. The lowest TickUnit
		 * returned by this TickUnitSource will be
		 * {@code decorated.getCeilingTickUnit(lowerBound)}.
		 * 
		 * @param decorated the default TickUnitSource
		 * @param lowerBound the lower bound
		 */
		public LowerBoundedTickUnitSource(TickUnitSource decorated, double lowerBound) {
			this.decorated = decorated;
			this.lowerUnit = decorated.getCeilingTickUnit(lowerBound);
		}
		
		@Override
		public TickUnit getCeilingTickUnit(double size) {
			TickUnit newUnit = decorated.getCeilingTickUnit(size);
			if(newUnit.compareTo(lowerUnit)<0) {
				return lowerUnit;
			} else {
				return newUnit;
			}
		}
		
		@Override
		public TickUnit getCeilingTickUnit(TickUnit unit) {
			TickUnit newUnit = decorated.getCeilingTickUnit(unit);
			if(newUnit.compareTo(lowerUnit)<0) {
				return lowerUnit;
			} else {
				return newUnit;
			}
		}
		
		@Override
		public TickUnit getLargerTickUnit(TickUnit unit) {
			TickUnit newUnit = decorated.getLargerTickUnit(unit);
			if(newUnit.compareTo(lowerUnit)<0) {
				return lowerUnit;
			} else {
				return newUnit;
			}
		}
	};

}
