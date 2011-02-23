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
package com.plugtree.solrmeter;

/**
 * Used to extract parameters of the program main.
 * @author tflobbe
 *
 */
public class ExpectedParameter {
	
	private String value;
	
	public ExpectedParameter(String[] args, String paramName, String defaultValue) {
		super();
		this.value = getValue(args, paramName, defaultValue);
	}
	
	private String getValue(String[] args, String paramName, String defaultValue) {
		for(String arg:args) {
			if(arg.startsWith("-D" + paramName)) {
				if(!arg.startsWith("-D" + paramName + "=")) {
					throw new RuntimeException("Invalid parameter: '-D" + paramName + "' ");
				}
				return arg.replace("-D" + paramName + "=", "");
			}
		}
		return defaultValue;
	}

	public String getValue() {
		return this.value;
	}

}
