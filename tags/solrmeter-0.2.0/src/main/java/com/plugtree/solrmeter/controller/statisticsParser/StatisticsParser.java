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
package com.plugtree.solrmeter.controller.statisticsParser;

import java.util.List;

import com.plugtree.solrmeter.controller.StatisticDescriptor;

/**
 * Parser to obtain all the available statistics of the system.
 * @author tflobbe
 *
 */
public interface StatisticsParser {

	/**
	 * 
	 * @param filePath file where the statistics file is located
	 * @return The list of statistics available for Solrmeter
	 * @throws ParserException
	 */
	List<StatisticDescriptor> getStatisticDescriptors(String filePath) throws ParserException;
}
