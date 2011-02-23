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
package com.plugtree.solrmeter.model.exception;

/**
 * Exception thrown by the StatisticConnection component when for some reason, SolrMeter coudln't connect
 * with Solr or obtain the required data.
 * 
 * @author tflobbe
 *
 */
public class StatisticConnectionException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8840127557339088330L;

	public StatisticConnectionException() {
		super();
	}

	public StatisticConnectionException(String message, Throwable cause) {
		super(message, cause);
	}

	public StatisticConnectionException(String message) {
		super(message);
	}

	public StatisticConnectionException(Throwable cause) {
		super(cause);
	}

}
