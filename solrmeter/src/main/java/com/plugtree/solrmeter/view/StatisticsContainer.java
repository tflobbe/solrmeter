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
package com.plugtree.solrmeter.view;

import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.plugtree.solrmeter.controller.StatisticsContainerController;

@Singleton
public class StatisticsContainer extends JTabbedPane implements ChangeListener, Refreshable {
	
	private static final long serialVersionUID = -1238490278156682110L;
	private Map<String, StatisticPanel> statistics;
	private StatisticsContainerController controller;
	
	@Inject
	public StatisticsContainer(StatisticsContainerController controller) {
		super();
		statistics = new HashMap<String, StatisticPanel>();
		this.controller = controller;
		this.addChangeListener(this);
	}
	
	public void addStatistic(StatisticPanel panel) {
		statistics.put(panel.getStatisticName(), panel);
		this.addTab(panel.getStatisticName(), panel);
	}
	
	public synchronized void refreshView() {
		StatisticPanel selectedPanel =(StatisticPanel)this.getSelectedComponent();
		if(selectedPanel != null) {
			selectedPanel.refreshView();
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		controller.onTabChanged();
	}
	
	public void clearStatistics() {
		statistics.clear();
		this.removeAll();
		this.revalidate();
	}
	
	@Override
	public Dimension getPreferredSize() {
		return super.getMinimumSize();
	}
	
	@Override
	public Dimension getMaximumSize() {
		return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

}
