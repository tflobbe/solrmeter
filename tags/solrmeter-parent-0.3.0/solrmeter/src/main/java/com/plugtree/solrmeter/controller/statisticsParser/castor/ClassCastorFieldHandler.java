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

import java.lang.reflect.Method;

import org.apache.log4j.Logger;

import com.plugtree.solrmeter.controller.StatisticDescriptor;

/**
 * Field Handler to marshall/unmarshall classes with Castor
 * @author tflobbe
 *
 */
public class ClassCastorFieldHandler extends AbstractCastorFieldHandler {
	
	private String attribute;

	@Override
	public String getValue(StatisticDescriptor descriptor) {
		Method method;
		String getterString = null;
		try {
			getterString = "get" + attribute.substring(0, 1).toUpperCase() + attribute.substring(1);
			method = StatisticDescriptor.class.getMethod(getterString);
			Class<?> clazz = (Class<?>)method.invoke(descriptor);
			if(clazz == null) {
				return null;
			}
			return clazz.getName();
		} catch (Exception e) {
			RuntimeException exception = new RuntimeException("Unnable to obtain attribute " + attribute + " using method " + getterString, e);
			Logger.getLogger(this.getClass()).error(exception.getMessage(), exception);
			throw exception;
		} 
	}

	@Override
	public void setValue(StatisticDescriptor descriptor, String value) {
		String setterMethod = null;
		Method method;
		try {
			setterMethod = "set" + attribute.substring(0, 1).toUpperCase() + attribute.substring(1);
			method = StatisticDescriptor.class.getMethod(setterMethod, Class.class);
			Class<?> clazz = null;
			if(value != null && !value.isEmpty()) {
				clazz = Class.forName(value);
			}
			method.invoke(descriptor, clazz);
		} catch (ClassNotFoundException e) {
			RuntimeException exception = new RuntimeException("Class for name " + value + " not found!", e);
			Logger.getLogger(this.getClass()).error(exception.getMessage(), exception);
			throw exception;
		} catch (Exception e) {
			RuntimeException exception = new RuntimeException("Error setting attribute " + attribute + " with method " + setterMethod, e);
			Logger.getLogger(this.getClass()).error(exception.getMessage(), exception);
			throw exception;
		} 
		
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		assert attribute.length() > 1;
		this.attribute = attribute;
	}

}
