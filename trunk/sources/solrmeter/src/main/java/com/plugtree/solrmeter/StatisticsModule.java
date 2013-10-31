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

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.binder.ScopedBindingBuilder;
import com.google.inject.name.Names;
import com.plugtree.solrmeter.model.SolrMeterConfiguration;
import com.plugtree.solrmeter.view.HeadlessStatisticPanel;
import com.plugtree.stressTestScope.StressTestScope;
import com.plugtree.solrmeter.controller.StatisticDescriptor;
import com.plugtree.solrmeter.controller.StatisticScope;
import com.plugtree.solrmeter.controller.StatisticType;
import com.plugtree.solrmeter.controller.StatisticsRepository;
import com.plugtree.solrmeter.controller.statisticsParser.StatisticsParser;
import com.plugtree.solrmeter.controller.statisticsParser.castor.StatisticsParserCastorImpl;
import com.plugtree.solrmeter.model.OptimizeStatistic;
import com.plugtree.solrmeter.model.QueryStatistic;
import com.plugtree.solrmeter.model.UpdateStatistic;
import com.plugtree.solrmeter.view.Refreshable;
import com.plugtree.solrmeter.view.StatisticPanel;
import com.plugtree.solrmeter.view.statistic.ErrorLogPanel;

/**
 * 
 * @author tflobbe
 *
 */
public class StatisticsModule extends AbstractModule {
	
	private StatisticsRepository statisticsRepository;
	
	private Map<StatisticScope, Class<? extends Annotation>> scopes;
	
	public StatisticsModule() {
		super();
		statisticsRepository = new StatisticsRepository(new StatisticsParserCastorImpl());
		scopes = new HashMap<StatisticScope, Class<? extends Annotation>>();
		scopes.put(StatisticScope.PROTOTYPE, null);
		scopes.put(StatisticScope.SINGLETON, Singleton.class);
		scopes.put(StatisticScope.STRESS_TEST, StressTestScope.class);
	}
	
	
	@Override
	protected void configure() {
		bind(StatisticsRepository.class).toInstance(statisticsRepository);
		bindStatistics(statisticsRepository);
		bind(Refreshable.class).annotatedWith(Names.named("errorLogPanel")).to(ErrorLogPanel.class);
		bind(StatisticsParser.class).to(StatisticsParserCastorImpl.class);
		
	}

	private void bindStatistics(StatisticsRepository statisticsRepository) {
		for(StatisticDescriptor description:statisticsRepository.getAvailableStatistics()) {
			bindStatistic(description);
		}
	}

	/**
	 * If the descriptor indicates that the statistic must be observing an operation, the statistic class will be binded to the 
	 * observer interface (QueryStatistic, UpdateStatistic or OptimizeStatistic).
	 * @param description
	 */
	@SuppressWarnings("unchecked")
	private void bindStatistic(StatisticDescriptor description) {
		if(description.isHasView()) {
            if (SolrMeterConfiguration.isHeadless()) {
                bindHeadlessView(description);
            }
            else {
                bindView(description);
            }
		}
		ScopedBindingBuilder interfaceBinderBuilder = null;
		if(description.getTypes().contains(StatisticType.QUERY)) {
			Class<? extends QueryStatistic> statisticModelClass = (Class<? extends QueryStatistic>) description.getModelClass();
			interfaceBinderBuilder = bind(QueryStatistic.class).annotatedWith(Names.named(description.getModelName())).to(statisticModelClass);
		}
		if(description.getTypes().contains(StatisticType.UPDATE)) {
			Class<? extends UpdateStatistic> statisticModelClass = (Class<? extends UpdateStatistic>) description.getModelClass();
			interfaceBinderBuilder = bind(UpdateStatistic.class).annotatedWith(Names.named(description.getModelName())).to(statisticModelClass);
		}
		if(description.getTypes().contains(StatisticType.OPTIMIZE)) {
			Class<? extends OptimizeStatistic> statisticModelClass = (Class<? extends OptimizeStatistic>) description.getModelClass();
			interfaceBinderBuilder = bind(OptimizeStatistic.class).annotatedWith(Names.named(description.getModelName())).to(statisticModelClass);
		}
		applyScope(description, interfaceBinderBuilder);
		bindModelClass(description);
	}


	/**
	 * Bind also the concrete model class, this binding is necesary to add the scope also to the concrete class.
	 * @param description
	 */
	private void bindModelClass(StatisticDescriptor description) {
		Class<?> statisticModelClass = description.getModelClass();
		ScopedBindingBuilder binderBuilder = bind(statisticModelClass);
		applyScope(description, binderBuilder);
		
	}
	
	/**
	 * Bind also the concrete view class, this binding is necesary to add the scope also to the concrete class.
	 * @param description
	 */
	private void bindViewClass(StatisticDescriptor description) {
		Class<?> statisticViewClass = description.getViewClass();
		ScopedBindingBuilder binderBuilder = bind(statisticViewClass);
		applyScope(description, binderBuilder);
	}

    private void bindHeadlessViewClass(StatisticDescriptor description) {
        Class<?> statisticHeadlessViewClass = description.getHeadlessViewClass();
        ScopedBindingBuilder binderBuilder =  bind(statisticHeadlessViewClass);
        applyScope(description, binderBuilder);
    }

	private void bindView(StatisticDescriptor description) {
		Class<? extends StatisticPanel> statisticViewClass = description.getViewClass();
		ScopedBindingBuilder binderBuilder = bind(StatisticPanel.class).annotatedWith(Names.named(description.getViewName())).to(statisticViewClass);
		applyScope(description, binderBuilder);
		bindViewClass(description);
	}

    private void bindHeadlessView(StatisticDescriptor description) {
        Class<? extends HeadlessStatisticPanel> statisticHeadlessViewClass = description.getHeadlessViewClass();
        ScopedBindingBuilder binderBuilder = bind(HeadlessStatisticPanel.class).annotatedWith(Names.named(description.getHeadlessViewName())).to(statisticHeadlessViewClass);
        applyScope(description, binderBuilder);
        bindHeadlessViewClass(description);
    }
	
	private void applyScope(StatisticDescriptor description,
			ScopedBindingBuilder binderBuilder) {
		if(description.getScope() != null && !description.getScope().equals(StatisticScope.PROTOTYPE)) {
			binderBuilder.in(scopes.get(description.getScope()));
		}
	}
}
