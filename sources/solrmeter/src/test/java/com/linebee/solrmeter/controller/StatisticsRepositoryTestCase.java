package com.linebee.solrmeter.controller;

import com.linebee.solrmeter.BaseTestCase;
import com.linebee.solrmeter.model.SolrMeterConfiguration;

public class StatisticsRepositoryTestCase extends BaseTestCase {
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		SolrMeterConfiguration.loadDefatultConfiguration();
	}
	
	public void testGetAllShowingStatistiscs() {
		SolrMeterConfiguration.setProperty("statistic.showingStatistics", "");
		StatisticsRepository repository = injector.getInstance(StatisticsRepository.class);
		assertEquals(0, repository.getAvailableStatistics().size());
		assertEquals(0, repository.getActiveStatistics().size());
		repository.addStatistic(new StatisticDescriptor("test","Test", null, null,new StatisticType[]{StatisticType.QUERY}, StatisticScope.STRESS_TEST));
		assertEquals(1, repository.getAvailableStatistics().size());
		assertEquals(1, repository.getActiveStatistics().size());
		for(int i = 0; i < 9; i++) {
			repository.addStatistic(new StatisticDescriptor("test" + i,"Test " + i, null, null,new StatisticType[]{StatisticType.QUERY}, StatisticScope.STRESS_TEST));
		}
		assertEquals(10, repository.getAvailableStatistics().size());
		assertEquals(10, repository.getActiveStatistics().size());
		SolrMeterConfiguration.setProperty("statistic.showingStatistics", "all");
		assertEquals(10, repository.getActiveStatistics().size());
		SolrMeterConfiguration.setProperty("statistic.showingStatistics", "ALL");
		assertEquals(10, repository.getActiveStatistics().size());
	}
	
	public void testGetSomeShowingStatistiscs() {
		SolrMeterConfiguration.setProperty("statistic.showingStatistics", "test");
		StatisticsRepository repository = injector.getInstance(StatisticsRepository.class);
		assertEquals(0, repository.getAvailableStatistics().size());
		assertEquals(0, repository.getActiveStatistics().size());
		repository.addStatistic(new StatisticDescriptor("test","Test", null, null,new StatisticType[]{StatisticType.QUERY}, StatisticScope.STRESS_TEST));
		assertEquals(1, repository.getAvailableStatistics().size());
		assertEquals(1, repository.getActiveStatistics().size());
		for(int i = 0; i < 9; i++) {
			repository.addStatistic(new StatisticDescriptor("test" + i,"Test " + i, null, null,new StatisticType[]{StatisticType.QUERY}, StatisticScope.STRESS_TEST));
		}
		assertEquals(10, repository.getAvailableStatistics().size());
		assertEquals(1, repository.getActiveStatistics().size());
		SolrMeterConfiguration.setProperty("statistic.showingStatistics", "test, test1");
		assertEquals(2, repository.getActiveStatistics().size());
		SolrMeterConfiguration.setProperty("statistic.showingStatistics", "test, test1, test2, test3");
		assertEquals(4, repository.getActiveStatistics().size());
		SolrMeterConfiguration.setProperty("statistic.showingStatistics", " test,    test1,       test2        , test");
		assertEquals(3, repository.getActiveStatistics().size());
	}

}
