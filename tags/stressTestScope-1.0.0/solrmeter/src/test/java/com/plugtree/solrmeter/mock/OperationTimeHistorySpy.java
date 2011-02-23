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
package com.plugtree.solrmeter.mock;

import com.plugtree.solrmeter.model.statistic.OperationTimeHistory;

/**
 * 
 * This OperationTimeHistory is just like original one but it always want to use
 * the same map key. The porpose of this class is to test the "addTime" method
 * @author tflobbe
 *
 */
public class OperationTimeHistorySpy extends OperationTimeHistory {

	@Override
	protected long getMapKey() {
		return 100L;
	}
	
}
