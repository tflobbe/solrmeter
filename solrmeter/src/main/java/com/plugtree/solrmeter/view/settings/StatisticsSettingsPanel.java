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
 package com.plugtree.solrmeter.view.settings;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.plugtree.solrmeter.controller.SettingsController;
import com.plugtree.solrmeter.controller.StatisticDescriptor;
import com.plugtree.solrmeter.controller.StatisticsRepository;
import com.plugtree.solrmeter.model.SolrMeterConfiguration;
import com.plugtree.solrmeter.view.I18n;
import com.plugtree.solrmeter.view.SettingsPanel;
import com.plugtree.solrmeter.view.component.IntegerPropertyPanel;
import com.plugtree.solrmeter.view.exception.InvalidPropertyException;
import com.plugtree.solrmeter.view.listener.PropertyChangeListener;


public class StatisticsSettingsPanel extends SettingsPanel implements PropertyChangeListener {

	private static final long serialVersionUID = -6507961112657893796L;
	private boolean editable;
	private StatisticsRepository statisticsRepository;
	private List<StatisticSelectionPanel> statisticSelectionPanels;
	
	@Override
	public String getSettingsName() {
		return I18n.get("settings.statistics.title");
	}
	
	public StatisticsSettingsPanel(SettingsController controller, boolean editable, StatisticsRepository statisticsRepository) {
		super(controller);
		this.editable = editable;
		this.statisticsRepository = statisticsRepository;
		this.initGUI();
	}

	private void initGUI() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(new IntegerPropertyPanel(I18n.get("settings.statistics.timeToRefresh"), "statistic.refreshTime", editable, this));
		this.add(new JLabel(I18n.get("settings.statistics.showing")));
		this.addStatistics();
		this.add(Box.createVerticalGlue());
	}

	private void addStatistics() {
		statisticSelectionPanels = new LinkedList<StatisticSelectionPanel>();
		String selectedString = SolrMeterConfiguration.getProperty("statistic.showingStatistics");
		for(StatisticDescriptor description:statisticsRepository.getAvailableStatistics()) {
			if(description.isHasView()) {
				StatisticSelectionPanel panel = new StatisticSelectionPanel(description, isSelected(description, selectedString), this);
				statisticSelectionPanels.add(panel);
				this.add(panel);
			}
		}
		
	}

	private boolean isSelected(StatisticDescriptor description, String actualProperty) {
		if(actualProperty == null || actualProperty.isEmpty() || actualProperty.equalsIgnoreCase("all")) {
			return true;
		}
		return actualProperty.contains(description.getName());
	}

	@Override
	public void onPropertyChanged(String property, String text)
			throws InvalidPropertyException {
		controller.setProperty(property, text);
	}
	
	private String getShowingStatisticsText() {
		StringBuffer selectedString = new StringBuffer();
		for(StatisticSelectionPanel panel:statisticSelectionPanels) {
			if(panel.isStatisticSelected()) {
				selectedString.append(panel.getStatisticName() + ", ");
			}
		}
		if(selectedString.toString().isEmpty()) {
			return "";
		}
		return selectedString.substring(0, selectedString.length() - 2);
	}
	
	private class StatisticSelectionPanel extends JPanel implements ActionListener {
		
		private static final long serialVersionUID = 5821715561699271584L;
		private static final int paddingLeft = 1;
		private static final int paddingRight = 1;
		private JCheckBox checkBox;
		private PropertyChangeListener listener;
		private String statisticName;
		
		public StatisticSelectionPanel(StatisticDescriptor description, boolean selected, PropertyChangeListener listener) {
			super();
			this.listener = listener;
			this.statisticName = description.getName();
			initGUI(description, selected);
		}

		private void initGUI(StatisticDescriptor description, boolean selected) {
			this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			this.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
			this.add(Box.createRigidArea(new Dimension(paddingLeft, paddingLeft)));
			checkBox = new JCheckBox(description.getName());
			checkBox.setToolTipText(description.getDescription());
			checkBox.addActionListener(this);
			checkBox.setSelected(selected);
			checkBox.setEnabled(StatisticsSettingsPanel.this.editable);
			this.add(checkBox);
			this.add(Box.createRigidArea(new Dimension(paddingRight, paddingRight)));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			listener.onPropertyChanged("statistic.showingStatistics", getShowingStatisticsText());
			
		}

		public String getStatisticName() {
			return statisticName;
		}
		
		public boolean isStatisticSelected() {
			return checkBox.isSelected();
		}
	}

}
