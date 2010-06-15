/**
 * Copyright Linebee. www.linebee.com
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
package com.linebee.solrmeter.task;

import org.apache.solr.client.solrj.SolrServerException;

import com.linebee.solrmeter.BaseTestCase;
import com.linebee.solrmeter.mock.SolrServerMock;
import com.linebee.solrmeter.mock.UpdateExecutorMock;
import com.linebee.solrmeter.model.task.UpdateThread;


public class UpdateThreadTestCase extends BaseTestCase {

	public void test() throws InterruptedException, SolrServerException {
		UpdateExecutorMock executor = new UpdateExecutorMock();
		executor.prepare();
		UpdateThread thread = new UpdateThread(executor, 1);
		thread.start();
		Thread.sleep(1000);
		thread.destroy();
		assertEquals(1, ((SolrServerMock)executor.getSolrServer()).getAddedDocuments().size());
	}
}
