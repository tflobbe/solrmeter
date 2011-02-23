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
package com.plugtree.solrmeter.utils;

import java.util.Date;

import com.plugtree.solrmeter.model.statistic.QueryTimeHistoryStatistic;


/**
 * 
 * Class to test QueryTimeHistoryStatistic. It simply override the method
 * "getNewDate" so it doesn't create a new Date, it uses the previously setted
 * date.
 * @author tflobbe
 *
 */
public class QueryTimeHistoryStatisticTest extends QueryTimeHistoryStatistic {
	
	private Date dateEvent;
	
	@Override
	protected Date getNewDate() {
		return dateEvent;
	}

	public Date getDateEvent() {
		return dateEvent;
	}

	public void setDateEvent(Date dateEvent) {
		this.dateEvent = dateEvent;
	}
	
}
