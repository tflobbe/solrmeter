package com.linebee.solrmeter;

import com.google.inject.AbstractModule;
import com.linebee.solrmeter.controller.StatisticsRepository;
import com.linebee.solrmeter.mock.StatisticsRepositorySpy;

public class ModelTestModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(StatisticsRepository.class).to(StatisticsRepositorySpy.class);
	}
}
