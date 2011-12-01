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

import com.plugtree.solrmeter.model.generator.ComplexQueryGeneratorTestCase;
import com.plugtree.solrmeter.model.generator.ExternalFileQueryGeneratorTestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

import com.plugtree.solrmeter.controller.FullQueryStatisticControllerTestCase;
import com.plugtree.solrmeter.controller.StatisticsRepositoryTestCase;
import com.plugtree.solrmeter.extractor.FileInputDocumentExtractorTestCase;
import com.plugtree.solrmeter.extractor.FileStringExtractorTestCase;
import com.plugtree.solrmeter.statistic.ErrorLogStatisticTestCase;
import com.plugtree.solrmeter.statistic.FullQueryStatisticTestCase;
import com.plugtree.solrmeter.statistic.HistogramQueryStatisticTestCase;
import com.plugtree.solrmeter.statistic.OperationRateStatisticTestCase;
import com.plugtree.solrmeter.statistic.OperationTimeHistoryTestCase;
import com.plugtree.solrmeter.statistic.QueryLogStatisticTestCase;
import com.plugtree.solrmeter.statistic.QueryTimeHistoryTestCase;
import com.plugtree.solrmeter.statistic.RequestHandlerConnectionTestCase;
import com.plugtree.solrmeter.statistic.SimpleQueryStatisticTestCase;
import com.plugtree.solrmeter.statistic.TimeRangeStatisticTestCase;
import com.plugtree.solrmeter.statistic.TimeRangeTestCase;
import com.plugtree.solrmeter.statistic.parser.StatisticsParserCastorImplTestCase;
import com.plugtree.solrmeter.task.AbstractOperationThreadTestCase;
import com.plugtree.solrmeter.task.ConstantOperationExecutorThreadTestCase;

public class SolrMeterTestSuite {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for com.plugtree.solrmeter");
		
		// com.plugtree.solrmeter.*
		suite.addTestSuite(AbstractExecutorTestCase.class);
		suite.addTestSuite(ExpectedParameterTestCase.class);
		suite.addTestSuite(FileUtilsTest.class);
		suite.addTestSuite(OptimizeExecutorTestCase.class);
		suite.addTestSuite(QueryServiceSolrJImplTestCase.class);
		suite.addTestSuite(SolrMeterConfigurationTestCase.class);
		suite.addTestSuite(UpdateExecutorTestCase.class);
		
		// com.plugtree.solrmeter.controller.*
		suite.addTestSuite(FullQueryStatisticControllerTestCase.class);
		suite.addTestSuite(StatisticsRepositoryTestCase.class);
		
		// com.plugtree.solrmeter.statistics.*
		suite.addTestSuite(ErrorLogStatisticTestCase.class);
		suite.addTestSuite(FullQueryStatisticTestCase.class);
		suite.addTestSuite(HistogramQueryStatisticTestCase.class);
		suite.addTestSuite(OperationRateStatisticTestCase.class);
		suite.addTestSuite(OperationTimeHistoryTestCase.class);
		suite.addTestSuite(QueryLogStatisticTestCase.class);
		suite.addTestSuite(QueryTimeHistoryTestCase.class);
		suite.addTestSuite(RequestHandlerConnectionTestCase.class);
		suite.addTestSuite(SimpleQueryStatisticTestCase.class);
		suite.addTestSuite(TimeRangeStatisticTestCase.class);
		suite.addTestSuite(TimeRangeTestCase.class);
		suite.addTestSuite(StatisticsParserCastorImplTestCase.class);
		
		// com.plugtree.solrmeter.extractor.*
		suite.addTestSuite(FileInputDocumentExtractorTestCase.class);
		suite.addTestSuite(FileStringExtractorTestCase.class);
		
		// com.plugtree.solrmeter.model.generator.*
		suite.addTestSuite(ExternalFileQueryGeneratorTestCase.class);
		suite.addTestSuite(ComplexQueryGeneratorTestCase.class);

		// com.plugtree.solrmeter.task.*
		suite.addTestSuite(AbstractOperationThreadTestCase.class);
		suite.addTestSuite(ConstantOperationExecutorThreadTestCase.class);
		
		return suite;
	}

}
