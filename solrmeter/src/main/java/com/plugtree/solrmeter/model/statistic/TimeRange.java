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
package com.plugtree.solrmeter.model.statistic;

public class TimeRange {
	
	private int minTime;
	
	private int maxTime;
	
	public TimeRange(int minTime, int maxTime) {
		this.minTime = minTime;
		this.maxTime = maxTime;
	}
	
	public TimeRange(int minTime) {
		this.minTime = minTime;
		this.maxTime = Integer.MAX_VALUE;
	}
	
	public boolean isIncluded(int time) {
		return biggerThanMin(time) && lowerThanMax(time);
	}

	private boolean lowerThanMax(int time) {
		return time <= maxTime;
	}

	private boolean biggerThanMin(int time) {
		return time >= minTime;
	}
	
	@Override
	public int hashCode() {
		return minTime ^ maxTime;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof TimeRange)) {
			return false;
		}
		TimeRange range = (TimeRange)obj;
		return range.getMaxTime() == this.getMaxTime() && range.getMinTime() == this.getMinTime();
	}

	public int getMinTime() {
		return minTime;
	}

	public int getMaxTime() {
		return maxTime;
	}
	
	@Override
	public String toString() {
		if(maxTime == Integer.MAX_VALUE) {
			return "More than " + getMinTime() + "ms";
		}
		return "From " + getMinTime() + "ms to " + getMaxTime() + "ms";
	}

}
