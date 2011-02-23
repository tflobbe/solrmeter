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
package com.plugtree.solrmeter.statistic.parser;

import java.util.List;

import com.plugtree.solrmeter.BaseTestCase;
import com.plugtree.solrmeter.controller.StatisticDescriptor;
import com.plugtree.solrmeter.controller.StatisticScope;
import com.plugtree.solrmeter.controller.StatisticType;
import com.plugtree.solrmeter.controller.statisticsParser.ParserException;
import com.plugtree.solrmeter.controller.statisticsParser.StatisticsParser;
import com.plugtree.solrmeter.controller.statisticsParser.castor.StatisticsParserCastorImpl;
/**
 * 
 * @author tflobbe
 *
 */
public class StatisticsParserCastorImplTestCase extends BaseTestCase {
	
	public StatisticsParser parser = new StatisticsParserCastorImpl();
	
	public void testSimple() {
		try {
			List<StatisticDescriptor> list = parser.getStatisticDescriptors("./parserFiles/test1.xml");
			assertNotNull(list);
			assertEquals(2, list.size());
			StatisticDescriptor descriptor1 = list.get(0);
			StatisticDescriptor descriptor2 = list.get(1);
			assertEquals("descriptor1", descriptor1.getName());
			assertEquals("descriptor2", descriptor2.getName());
			assertEquals("Descriptor for test 1", descriptor1.getDescription());
			assertEquals("Descriptor for test 2", descriptor2.getDescription());
			assertEquals(2, descriptor1.getTypes().size());
			assertEquals(StatisticType.QUERY, descriptor1.getTypes().get(0));
			assertEquals(StatisticType.UPDATE, descriptor1.getTypes().get(1));
			assertEquals(2, descriptor2.getTypes().size());
			assertEquals(StatisticType.QUERY, descriptor2.getTypes().get(0));
			assertEquals(StatisticType.OPTIMIZE, descriptor2.getTypes().get(1));
			assertEquals(StatisticScope.STRESS_TEST, descriptor1.getScope());
			assertEquals(StatisticScope.PROTOTYPE, descriptor2.getScope());
			assertEquals(com.plugtree.solrmeter.view.statistic.HistogramChartPanel.class, descriptor1.getViewClass());
			assertEquals(com.plugtree.solrmeter.mock.MockStatistic.class, descriptor1.getModelClass());
			assertTrue(descriptor1.isHasView());
		} catch (ParserException e) {
			fail(e);
		}
	}
	
	public void testNoRequiredAttributes() {
		try {
			List<StatisticDescriptor> list = parser.getStatisticDescriptors("./parserFiles/test2.xml");
			assertNotNull(list);
			StatisticDescriptor descriptor = list.get(0);
			assertTrue(descriptor.isHasView());//true is the default value
			
			descriptor = list.get(1);
			assertNull(descriptor.getViewClass());
			
			descriptor = list.get(2);
			assertEquals(StatisticScope.STRESS_TEST, descriptor.getScope());
			
		} catch (Exception e) {
			fail(e);
		}
	}
	
	public void testValidationNoRepeatedNames() {
		validationTest("./parserFiles/testRepeatedNames_OK.xml", "./parserFiles/testRepeatedNames_Fail.xml");
	}
	
	public void testValidateNoView() {
		validationTest("./parserFiles/testValidateNoView_OK.xml", "./parserFiles/testValidateNoView_Fail.xml");
	}
	
	public void testValidateTypes() {
		validationTest("./parserFiles/testValidateTypes_OK.xml", new String[]{"./parserFiles/testValidateTypes_Fail1.xml",
				"./parserFiles/testValidateTypes_Fail2.xml", "./parserFiles/testValidateTypes_Fail3.xml"});
	}
	
	public void testValidateModelClass() {
		validationTest("./parserFiles/testValidateModelClass_OK.xml", new String[]{"./parserFiles/testValidateModelClass_Fail.xml",
				"./parserFiles/testValidateModelClass_Fail2.xml", "./parserFiles/testValidateModelClass_Fail3.xml"});
	}
	
	public void testValidateViewClass() {
		validationTest("./parserFiles/testValidateViewClass_OK.xml", "./parserFiles/testValidateViewClass_Fail.xml");
	}
	
	private void validationTest(String okFilePath, String[] failFilePaths) {
		for(String filePath:failFilePaths) {
			try {
				parser.getStatisticDescriptors(filePath);
				fail("An exception was expected. File: " + filePath);
			} catch (ParserException e) {
				//expected
			}
		}
		try {
			parser.getStatisticDescriptors(okFilePath);
		} catch (ParserException e) {
			fail(e);
		}
		
	}

	
	private void validationTest(String okFilePath, String failFilePath) {
		this.validationTest(okFilePath, new String[]{failFilePath});
	}

}
