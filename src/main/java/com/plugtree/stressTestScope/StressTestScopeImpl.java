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
package com.plugtree.stressTestScope;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;

/**
 * 
 * @author tflobbe
 *
 */
public class StressTestScopeImpl implements Scope {

	private static Map<String, Object> providers = new HashMap<String, Object>();

	private static String actualTestId = "";

	public <T> Provider<T> scope(Key<T> key, final Provider<T> creator) {
		final String name = key.toString();
		return new Provider<T>() {
			public T get() {
				synchronized (providers) {
					verifyChangedStressTestId();
					@SuppressWarnings("unchecked")
					T provider = (T) providers.get(name);
					if (provider == null) {
						provider = creator.get();
						providers.put(name, provider);
					}
					return provider;
				}
			}



		};
	}

	private void verifyChangedStressTestId() {
		if(testIdHasChanged()) {
			providers.clear();
			actualTestId = StressTestRegistry.getStressTestId();
			if(actualTestId == null) {
				throw new RuntimeException("Stress Test Scope has not been started!");
			}
		}

	}

	private boolean testIdHasChanged() {
		return !actualTestId.equals(StressTestRegistry.getStressTestId());
	}

	@Override
	public String toString() {
		return "StressTestScope";
	}


}
