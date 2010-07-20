package com.linebee.solrmeter.mock;

import com.linebee.solrmeter.controller.StatisticsRepository;

public class StatisticsRepositorySpy extends StatisticsRepository{

	public StatisticsRepositorySpy() {
		super(null);
	}

	protected void parseAvailable() {
		
	}
}
