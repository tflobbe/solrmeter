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

import java.util.Date;

public abstract class OperationException extends Exception {

	private static final long serialVersionUID = -1815541508342882169L;
	
	private Date date;

	public OperationException() {
		super();
		date = new Date();
	}

	public OperationException(String message, Throwable cause) {
		super(message, cause);
		date = new Date();
	}

	public OperationException(String message) {
		super(message);
		date = new Date();
	}

	public OperationException(Throwable cause) {
		super(cause);
		date = new Date();
	}
	
	public abstract String getOperationName();

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}
