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
package com.linebee.solrmeter;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.binder.ScopedBindingBuilder;
import com.google.inject.name.Names;
import com.linebee.solrmeter.controller.StatisticDescriptor;
import com.linebee.solrmeter.controller.StatisticScope;
import com.linebee.solrmeter.controller.StatisticType;
import com.linebee.solrmeter.controller.StatisticsRepository;
import com.linebee.solrmeter.model.OptimizeStatistic;
import com.linebee.solrmeter.model.QueryStatistic;
import com.linebee.solrmeter.model.UpdateStatistic;
import com.linebee.solrmeter.view.Refreshable;
import com.linebee.solrmeter.view.StatisticPanel;
import com.linebee.solrmeter.view.statistic.ErrorLogPanel;
import com.linebee.stressTestScope.StressTestScope;

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
		statisticsRepository = new StatisticsRepository();
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
		
	}

	private void bindStatistics(StatisticsRepository statisticsRepository) {
		for(StatisticDescriptor description:statisticsRepository.getAvailableStatistics()) {
			bindStatistic(description);
		}
	}

	@SuppressWarnings("unchecked")
	private void bindStatistic(StatisticDescriptor description) {
		if(description.isHasView()) {
			bindView(description);
		}
		ScopedBindingBuilder binderBuilder = null;
		if(description.getTypes().contains(StatisticType.QUERY)) {
			Class<? extends QueryStatistic> statisticModelClass = (Class<? extends QueryStatistic>) description.getModelClass();
			binderBuilder = bind(QueryStatistic.class).annotatedWith(Names.named(description.getModelName())).to(statisticModelClass);
		}
		if(description.getTypes().contains(StatisticType.UPDATE)) {
			Class<? extends UpdateStatistic> statisticModelClass = (Class<? extends UpdateStatistic>) description.getModelClass();
			binderBuilder = bind(UpdateStatistic.class).annotatedWith(Names.named(description.getModelName())).to(statisticModelClass);
		}
		if(description.getTypes().contains(StatisticType.OPTIMIZE)) {
			Class<? extends OptimizeStatistic> statisticModelClass = (Class<? extends OptimizeStatistic>) description.getModelClass();
			binderBuilder = bind(OptimizeStatistic.class).annotatedWith(Names.named(description.getModelName())).to(statisticModelClass);
		}
		applyScope(description, binderBuilder);
	}


	private void bindView(StatisticDescriptor description) {
		Class<? extends StatisticPanel> statisticViewClass = description.getViewClass();
		ScopedBindingBuilder binderBuilder = bind(StatisticPanel.class).annotatedWith(Names.named(description.getViewName())).to(statisticViewClass);
		applyScope(description, binderBuilder);
	}
	
	private void applyScope(StatisticDescriptor description,
			ScopedBindingBuilder binderBuilder) {
		if(description.getScope() != null && !description.getScope().equals(StatisticScope.PROTOTYPE)) {
			binderBuilder.in(scopes.get(description.getScope()));
		}
	}
}
