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
package com.plugtree.solrmeter.model.statistic;

/**
 * Stores the information of an instance of the cache
 * @author tflobbe
 *
 */
public class CacheData {
	
	private long lookups;
	private long hits ;
	private float hitratio;
	private long inserts;
	private long evictions;
	private long size;
	private long warmupTime;
	
	public CacheData(long lookups, long hits, float hitratio, long inserts,
			long evictions, long size, long warmupTime) {
		super();
		this.lookups = lookups;
		this.hits = hits;
		this.hitratio = hitratio;
		this.inserts = inserts;
		this.evictions = evictions;
		this.size = size;
		this.warmupTime = warmupTime;
	}
	
	public CacheData(long lookups, long hits, float hitratio, long inserts,
			long evictions) {
		this(lookups, hits, hitratio, inserts, evictions, -1, -1);
	}

	public long getLookups() {
		return lookups;
	}

	public void setLookups(long lookups) {
		this.lookups = lookups;
	}

	public long getHits() {
		return hits;
	}

	public void setHits(long hits) {
		this.hits = hits;
	}

	public float getHitratio() {
		return hitratio;
	}

	public void setHitratio(float hitratio) {
		this.hitratio = hitratio;
	}

	public long getInserts() {
		return inserts;
	}

	public void setInserts(long inserts) {
		this.inserts = inserts;
	}

	public long getEvictions() {
		return evictions;
	}

	public void setEvictions(long evictions) {
		this.evictions = evictions;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public long getWarmupTime() {
		return warmupTime;
	}

	public void setWarmupTime(long warmupTime) {
		this.warmupTime = warmupTime;
	}
	
}