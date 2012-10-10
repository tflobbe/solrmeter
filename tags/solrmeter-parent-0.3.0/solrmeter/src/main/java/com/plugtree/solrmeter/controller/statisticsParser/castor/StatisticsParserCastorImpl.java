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
package com.plugtree.solrmeter.controller.statisticsParser.castor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.XMLContext;
import org.xml.sax.InputSource;

import com.plugtree.solrmeter.controller.StatisticDescriptor;
import com.plugtree.solrmeter.controller.StatisticType;
import com.plugtree.solrmeter.controller.statisticsParser.ParserException;
import com.plugtree.solrmeter.controller.statisticsParser.StatisticsParser;
import com.plugtree.solrmeter.model.FileUtils;
import com.plugtree.solrmeter.model.OptimizeStatistic;
import com.plugtree.solrmeter.model.QueryStatistic;
import com.plugtree.solrmeter.model.UpdateStatistic;
import com.plugtree.solrmeter.view.StatisticPanel;

/**
 * Castor Implementation for StatisticsParser
 * @author tflobbe
 *
 */
public class StatisticsParserCastorImpl implements StatisticsParser {

	
	@Override
	public List<StatisticDescriptor> getStatisticDescriptors(String filePath)
			throws ParserException {
		try {
			Mapping mapping = new Mapping();
			mapping.loadMapping(FileUtils.findFileAsResource("StatisticDescriptorMapping.xml"));
			XMLContext context = new XMLContext();
			context.addMapping(mapping);
			InputSource source = new InputSource(FileUtils.findFileAsStream(filePath));
			Unmarshaller unmarshaller = context.createUnmarshaller();
			unmarshaller.setClass(StatisticList.class);
			StatisticList list = (StatisticList)unmarshaller.unmarshal(source);
			validate(list.getDescriptors());
			return list.getDescriptors();
		} catch (Exception e) {
			ParserException parserException = new ParserException("Exception parsing statistic descriptor files", e);
			Logger.getLogger(this.getClass()).error(parserException.getMessage(), parserException);
			throw parserException;
		} 
	}

	/**
	 * Validates the generated descriptors
	 * @param descriptors
	 * @throws ParserException 
	 */
	private void validate(List<StatisticDescriptor> descriptors) throws ParserException {
		this.validateDuplicatedNames(descriptors);
		this.validateNoView(descriptors);
		this.validateTypes(descriptors);
		this.validateModelClass(descriptors);
		this.validateVewClass(descriptors);
	}

	/**
	 * Validate that when a statistic has a view, it's view class extends StatisticPanel
	 * @param descriptors
	 * @throws ParserException
	 */
	private void validateVewClass(List<StatisticDescriptor> descriptors) throws ParserException {
		for(StatisticDescriptor descriptor:descriptors) {
			if(descriptor.isHasView()) {
				if(!StatisticPanel.class.isAssignableFrom(descriptor.getViewClass())) {
					throw new ParserException("Error on descriptor of statistic" + descriptor.getName() + ". " + 
							descriptor.getViewClass() + " can't be used as a View Class becouse is not a StatisticPanel");
				}
			}
		}
		
	}

	/**
	 * Validate that when a statistic has a type, it's model class implements the necesary interface
	 * @param descriptors
	 * @throws ParserException
	 */
	private void validateModelClass(List<StatisticDescriptor> descriptors) throws ParserException {
		for(StatisticDescriptor descriptor:descriptors) {
			if(descriptor.getTypes().contains(StatisticType.OPTIMIZE)) {
				validateInterface(descriptor.getModelClass(), OptimizeStatistic.class);
			}
			if(descriptor.getTypes().contains(StatisticType.QUERY)) {
				validateInterface(descriptor.getModelClass(), QueryStatistic.class);
			}
			if(descriptor.getTypes().contains(StatisticType.UPDATE)) {
				validateInterface(descriptor.getModelClass(), UpdateStatistic.class);
			}
		}
		
	}

	/**
	 * Validates that the model class implements the statisticInterface
	 * @param modelClass
	 * @param statisticInterface
	 * @throws ParserException
	 */
	private void validateInterface(Class<?> modelClass,
			Class<?> statisticInterface) throws ParserException {
		if(!statisticInterface.isAssignableFrom(modelClass)) {
			throw new ParserException("The class " + modelClass + " shoul implement the interface " + statisticInterface + " or the related type has to be removed.");
		}
		
	}

	/**
	 * Validate that the types are valid and are not repeated
	 * @param descriptors
	 * @throws ParserException
	 */
	private void validateTypes(List<StatisticDescriptor> descriptors) throws ParserException {
		for(StatisticDescriptor descriptor:descriptors) {
			if(descriptor.getTypes().size() == 0) {
				throw new ParserException("There has to be at least one type for statistic " + descriptor.getName());
			}
			int index = 0;
			for(StatisticType type:descriptor.getTypes()) {
				if(descriptor.getTypes().lastIndexOf(type) != index) {
					throw new ParserException("The statistic type " + type.name() + " of statistic " + descriptor.getName() + " is repeated.");
				}
				index++;
			}
		}
		
	}

	/**
	 * Validate that all statistics that has a view (have the hasView attribute set to true) 
	 * have a viewClass asociated
	 * @param descriptors
	 * @throws ParserException
	 */
	private void validateNoView(List<StatisticDescriptor> descriptors) throws ParserException {
		for(StatisticDescriptor descriptor:descriptors) {
			if(descriptor.isHasView() && descriptor.getViewClass() == null) {
				throw new ParserException("The descriptor for name " + descriptor.getName() + " should have a view class or have the 'hasView' attribute setted to false");
			}
			if(!descriptor.isHasView() && descriptor.getViewClass() != null) {
				Logger.getLogger(this.getClass()).warn("The descriptor " + descriptor.getName() + " has a view class but it's 'hasView' attribute is set to false");
			}
		}
	}

	/**
	 * Validate that there are no repeated names on the descriptors
	 * @param descriptors
	 * @throws ParserException 
	 */
	private void validateDuplicatedNames(List<StatisticDescriptor> descriptors) throws ParserException {
		Set<String> existingNames = new HashSet<String>();
		for(StatisticDescriptor descriptor:descriptors) {
			if(existingNames.contains(descriptor.getName())) {
				throw new ParserException("The name " + descriptor.getName() + " is duplicated. Names must be unique");
			}else {
				existingNames.add(descriptor.getName());
			}
		}
		
	}
}
