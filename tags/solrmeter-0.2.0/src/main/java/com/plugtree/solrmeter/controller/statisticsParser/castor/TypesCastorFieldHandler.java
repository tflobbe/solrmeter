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
package com.plugtree.solrmeter.controller.statisticsParser.castor;

import java.util.List;

import com.plugtree.solrmeter.controller.StatisticDescriptor;
import com.plugtree.solrmeter.controller.StatisticType;

/**
 * FieldHandler to marshall/unmarshall a list of StatisticType on Castor
 * @author tflobbe
 *
 */
public class TypesCastorFieldHandler extends AbstractCastorFieldHandler {

	@Override
	public String getValue(StatisticDescriptor object) throws IllegalStateException {
		StringBuffer csvTypes = new StringBuffer();
		List<StatisticType> types = object.getTypes();
		for(StatisticType type:types) {
			csvTypes.append(type.name().toLowerCase() + ", ");
		}
		if(csvTypes.toString().isEmpty()) {
			return null;
		}
		return csvTypes.toString().substring(0, csvTypes.length() - 2);
	}

	@Override
	public void setValue(StatisticDescriptor descriptor, String value)
			throws IllegalStateException, IllegalArgumentException {
		String[] types = value.split(",");
		if(types.length == 0) {
			return;
		}
		for(String type:types) {
			descriptor.getTypes().add(StatisticType.valueOf(type.toUpperCase().trim()));
		}
		
	}

}
