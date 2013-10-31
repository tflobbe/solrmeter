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
package com.plugtree.solrmeter.controller;

import java.util.LinkedList;
import java.util.List;

import com.plugtree.solrmeter.view.HeadlessStatisticPanel;
import com.plugtree.solrmeter.view.StatisticPanel;

/**
 * This class holds all the information of some statistic that can added to SolrMeter UI
 * @author tflobbe
 *
 */
public class StatisticDescriptor {

	/**
	 * The name of this statistic. This will be used by the dependency Injection Framework.
	 * It shouln't have white spaces or special characters
	 */
	private String name;
	
	/**
	 * A brief description of this statistic. This will be shown on the UI to select this
	 * statistic.
	 */
	private String description;
	
	/**
	 * Model class of this statistic. This should implement one or more of 
	 * <code>com.plugtree.solrmeter.model.QueryStatistic</code>
	 * <code>com.plugtree.solrmeter.model.UpdateStatistic</code>
	 * <code>com.plugtree.solrmeter.model.OptimizeStatistic</code>
	 * This class must keep relation with the <code>types</code> attribute of the 
	 * description. When <code>types</code> contains StatisticType.QUERY, the class
	 * must implement QueryStatistic and so. Otherwise, a ClassCastException will be
	 * thrown.
	 */
	private Class<?> modelClass;
	
	/**
	 * Class that implements <code>com.plugtree.solrmeter.view.StatisticPanel</code> and will
	 * show graphically this statistic. This should never be null unless the attribute 
	 * <code>hasView</code> is false.
	 */
	private Class<? extends StatisticPanel> viewClass;

    private Class<? extends HeadlessStatisticPanel> headlessViewClass;
	
	/**
	 * This list contains the list of <code>com.plugtree.solrmeter.controller.StatisticType</code>
	 * that this statistic description will represent. When this list contains the value
	 * StatisticType.QUERY, it will be added as a QueryExecutor listener. The same with Update
	 * and Optimize.
	 * When a type is added, you have to be sure than the model class implements the required
	 * internface to be added as an executor listener.
	 */
	private List<StatisticType> types;
	
	/**
	 * the scope of this statistic. By default, StressTestScope will be used.
	 */
	private StatisticScope scope;
	
	/**
	 * Use only when a statistic has no view side
	 */
	private boolean hasView = true;
	
	public StatisticDescriptor() {
		super();
		types = new LinkedList<StatisticType>();
		scope = StatisticScope.STRESS_TEST;
	}
	
	public StatisticDescriptor(String name, String description,
			Class<?> modelClass, Class<? extends StatisticPanel> viewClass,
            Class<? extends HeadlessStatisticPanel> headlessViewClass,
			List<StatisticType> types, StatisticScope scope) {
		super();
		this.name = name;
		this.description = description;
		this.modelClass = modelClass;
		this.viewClass = viewClass;
        this.headlessViewClass = headlessViewClass;
		this.types = types;
		this.scope = scope;
	}
	
	public StatisticDescriptor(String name, String description,
			Class<?> modelClass, Class<? extends StatisticPanel> viewClass,
            Class<? extends HeadlessStatisticPanel> headlessViewClass,
			StatisticType[] typesArray, StatisticScope scope) {
		super();
		this.name = name;
		this.description = description;
		this.modelClass = modelClass;
		this.viewClass = viewClass;
        this.headlessViewClass = headlessViewClass;
		this.scope = scope;
		this.types = new LinkedList<StatisticType>();
		for(StatisticType type:typesArray) {
			this.types.add(type);
		}
	}
	
	public StatisticDescriptor(String name,
			Class<?> modelClass,
			StatisticType[] typesArray, StatisticScope scope) {
		this(name, null, modelClass, null, null, typesArray, scope);
		this.hasView = false;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Class<?> getModelClass() {
		return modelClass;
	}

	public void setModelClass(Class<?> modelClass) {
		this.modelClass = modelClass;
	}

	public Class<? extends StatisticPanel> getViewClass() {
		return viewClass;
	}

	public void setViewClass(Class<? extends StatisticPanel> viewClass) {
		this.viewClass = viewClass;
	}

    public void setHeadlessViewClass(Class<? extends HeadlessStatisticPanel> headlessViewClass) {
        this.headlessViewClass = headlessViewClass;
    }

	public List<StatisticType> getTypes() {
		return types;
	}

	public void setTypes(List<StatisticType> types) {
		this.types = types;
	}

	public StatisticScope getScope() {
		return scope;
	}

	public void setScope(StatisticScope scope) {
		this.scope = scope;
	}

	public String getModelName() {
		return "Model_" + getName();
	}

	public String getViewName() {
		return "View_" + getName();
	}

    public String getHeadlessViewName() {
        return "HeadlessView_" + getName();
    }

	public boolean isHasView() {
		return hasView;
	}

	public void setHasView(boolean hasView) {
		this.hasView = hasView;
	}

    public Class<? extends HeadlessStatisticPanel> getHeadlessViewClass() {
        return headlessViewClass;
    }
}
