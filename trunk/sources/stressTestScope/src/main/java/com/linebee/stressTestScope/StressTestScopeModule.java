/**
 * Copyright Linebee LLC
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
package com.linebee.stressTestScope;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
/**
 * 
 * @author tflobbe
 *
 */
public class StressTestScopeModule extends AbstractModule {

	 public void configure() {
		 StressTestScopeImpl batchScope = new StressTestScopeImpl();

		    // tell Guice about the scope
		    bindScope(StressTestScope.class, batchScope);

		    // make our scope instance injectable
		    bind(StressTestScopeImpl.class)
		        .annotatedWith(Names.named("StressTestScope"))
		        .toInstance(batchScope);
		  }

}
