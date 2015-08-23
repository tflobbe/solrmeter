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

import com.plugtree.solrmeter.controller.StatisticDescriptor;
import com.plugtree.solrmeter.controller.StatisticScope;

/**
 * FieldHandler to marshall/unmarshall StatisticScope type on Castor
 * @author tflobbe
 *
 */
public class ScopeCastorFieldHandler extends AbstractCastorFieldHandler {

	@Override
	public String getValue(StatisticDescriptor object) throws IllegalStateException {
		StatisticScope scope = object.getScope();
		if(scope == null) {
			return null;
		}
		return scope.name().toLowerCase();
	}
	
	@Override
	public void setValue(StatisticDescriptor object, String value)
			throws IllegalStateException, IllegalArgumentException {
		if(value == null || value.isEmpty()) {
			return;
		}
		((StatisticDescriptor)object).setScope(StatisticScope.valueOf(value.replaceAll("-", "_").toUpperCase()));
	}

}
