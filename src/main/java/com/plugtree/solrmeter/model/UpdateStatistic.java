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
package com.plugtree.solrmeter.model;

import org.apache.solr.client.solrj.response.UpdateResponse;

import com.plugtree.solrmeter.model.exception.CommitException;
import com.plugtree.solrmeter.model.exception.UpdateException;
/**
 * nterface to be implemented by all the update statistics.
 * @author tflobbe
 *
 */
public interface UpdateStatistic {

	/**
	 * To be executed when an update is performed
	 * @param response
	 */
	void onAddedDocument(UpdateResponse response);

	/**
	 * To be executed when a commit is performed
	 * @param response
	 */
	void onCommit(UpdateResponse response);

	/**
	 * This method will be executed when the test finishes.
	 */
	void onFinishedTest();

	/**
	 * This method will be executed when an error ocurrs on a commit.
	 */
	void onCommitError(CommitException exception);
	
	/**
	 * This method will be executed when an error ocurrs on an update.
	 */	
	void onAddError(UpdateException exception);

}
