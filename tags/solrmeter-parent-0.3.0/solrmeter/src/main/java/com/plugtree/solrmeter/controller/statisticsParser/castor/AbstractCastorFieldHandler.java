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
import java.util.Properties;

import org.apache.log4j.Logger;
import org.exolab.castor.mapping.ConfigurableFieldHandler;
import org.exolab.castor.mapping.ValidityException;

import com.plugtree.solrmeter.controller.StatisticDescriptor;

/**
 * Abstract Field Handler to be used by Castor for unmarshalling statistic descriptors
 * @author tflobbe
 *
 */
public abstract class AbstractCastorFieldHandler implements ConfigurableFieldHandler {

	@Override
	public void checkValidity(Object object) throws ValidityException,
			IllegalStateException {
		throw new UnsupportedOperationException("void checkValidity(Object object)");
	}

	@Override
	public Object getValue(Object object) throws IllegalStateException {
		return this.getValue((StatisticDescriptor) object);
	}

	@Override
	public Object newInstance(Object parent) throws IllegalStateException {
		throw new UnsupportedOperationException("Object newInstance(Object parent)");
	}

	@Override
	public void resetValue(Object object) throws IllegalStateException,
			IllegalArgumentException {
		throw new UnsupportedOperationException("void resetValue(Object object)");

	}

	@Override
	public void setValue(Object object, Object value)
			throws IllegalStateException, IllegalArgumentException {
		setValue((StatisticDescriptor)object, (String) value);

	}
	
	@Override
	public void setConfiguration(Properties config) throws ValidityException {
		for(Object objectKey:config.keySet()) {
			String key = (String)objectKey;
			assert key.length() > 1;
			try {
				String setterMethodName = "set" + key.substring(0, 1).toUpperCase() + key.substring(1);
				Method setterMethod = this.getClass().getMethod(setterMethodName, String.class);
				setterMethod.invoke(this, config.get(objectKey));
			} catch (Exception e) {
				RuntimeException exception = new RuntimeException("Configuration error. Unnable to set value " + config.get(objectKey) + " in field " + key);
				Logger.getLogger(this.getClass()).error(exception);
				throw exception;
			}
		}
		
	}
	
	public abstract void setValue(StatisticDescriptor descriptor, String value);
	
	public abstract String getValue(StatisticDescriptor descriptor);

}
