package com.plugtree.solrmeter;

import com.google.inject.AbstractModule;
import com.plugtree.solrmeter.controller.StatisticsRepository;
import com.plugtree.solrmeter.mock.StatisticsRepositorySpy;

public class ModelTestModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(StatisticsRepository.class).to(StatisticsRepositorySpy.class);
	}
}
