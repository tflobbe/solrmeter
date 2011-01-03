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

import junit.framework.Test;
import junit.framework.TestSuite;

import com.linebee.solrmeter.controller.StatisticsRepositoryTestCase;
import com.linebee.solrmeter.extractor.FileInputDocumentExtractorTestCase;
import com.linebee.solrmeter.extractor.FileStringExtractorTestCase;
import com.linebee.solrmeter.model.operations.SolrQueryGeneratorTest;
import com.linebee.solrmeter.statistic.ErrorLogStatisticTestCase;
import com.linebee.solrmeter.statistic.FullQueryStatisticTestCase;
import com.linebee.solrmeter.statistic.HistogramQueryStatisticTestCase;
import com.linebee.solrmeter.statistic.OperationTimeHistoryTestCase;
import com.linebee.solrmeter.statistic.QueryTimeHistoryTestCase;
import com.linebee.solrmeter.statistic.SimpleQueryStatisticTestCase;
import com.linebee.solrmeter.statistic.TimeRangeStatisticTestCase;
import com.linebee.solrmeter.statistic.TimeRangeTestCase;
import com.linebee.solrmeter.statistic.parser.StatisticsParserCastorImplTestCase;
import com.linebee.solrmeter.task.AbstractOperationThreadTestCase;
import com.linebee.solrmeter.task.ConstantOperationExecutorThreadTestCase;

public class SolrMeterTestSuite {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for com.linebee.solrmeter");
		//$JUnit-BEGIN$
		suite.addTestSuite(UpdateExecutorTestCase.class);
		suite.addTestSuite(AbstractExecutorTestCase.class);
		suite.addTestSuite(HistogramQueryStatisticTestCase.class);
		suite.addTestSuite(AbstractOperationThreadTestCase.class);
		suite.addTestSuite(QueryTimeHistoryTestCase.class);
		suite.addTestSuite(FileStringExtractorTestCase.class);
		suite.addTestSuite(ErrorLogStatisticTestCase.class);
		suite.addTestSuite(OperationTimeHistoryTestCase.class);
		suite.addTestSuite(SimpleQueryStatisticTestCase.class);
		suite.addTestSuite(TimeRangeStatisticTestCase.class);
		suite.addTestSuite(TimeRangeTestCase.class);
		suite.addTestSuite(OptimizeExecutorTestCase.class);
		suite.addTestSuite(SolrMeterConfigurationTestCase.class);
		suite.addTestSuite(FileInputDocumentExtractorTestCase.class);
		suite.addTestSuite(QueryExecutorTestCase.class);
		suite.addTestSuite(FullQueryStatisticTestCase.class);
		suite.addTestSuite(StatisticsRepositoryTestCase.class);
		suite.addTestSuite(StatisticsParserCastorImplTestCase.class);
		suite.addTestSuite(ExpectedParameterTestCase.class);
		suite.addTestSuite(ConstantOperationExecutorThreadTestCase.class);
		suite.addTestSuite(QueryExecutorConstantImplTestCase.class);
		suite.addTestSuite(QueryServiceSolrJImplTestCase.class);
		suite.addTestSuite(SolrQueryGeneratorTest.class);
		//$JUnit-END$
		return suite;
	}

}
