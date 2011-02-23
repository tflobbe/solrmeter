package com.plugtree.solrmeter.mock;

import com.plugtree.solrmeter.controller.StatisticsRepository;

public class StatisticsRepositorySpy extends StatisticsRepository{

	public StatisticsRepositorySpy() {
		super(null);
	}

	protected void parseAvailableStatyistics() {
		
	}
}
