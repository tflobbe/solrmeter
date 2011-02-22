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

/**
 * Utility class for functions related to reflection
 * @author tomas
 *
 */
public class ReflectionUtils {

	/**
	 * This method will invoke the getter method of the attribute with the attributeName
	 * passed as parameter.
	 * @param object
	 * @param attributeName
	 * @return
	 */
	public static Object getAttribute(Object object, String attributeName) {
		try {
			return object.getClass().getMethod("get" + capitalize(attributeName)).invoke(object);
		} catch (Exception e) {
			throw new RuntimeException("Cant invoke the getter method of the attribute " + attributeName + 
					" on the object " + object.toString() + ". Generated method name was " + "get" + capitalize(attributeName), e);
		}
	}

	/**
	 * The string will be capitalized
	 * @param string
	 * @return
	 */
	private static String capitalize(String string) {
		if(string.length() <= 1) {
			return string.toUpperCase();
		}
		return string.substring(0, 1).toUpperCase() + string.substring(1);
	}
}
