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
package com.linebee.solrmeter.view.statistic;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.linebee.solrmeter.model.statistic.FullQueryStatistic;
import com.linebee.solrmeter.model.statistic.QueryLogStatistic;
import com.linebee.solrmeter.model.statistic.QueryLogStatistic.QueryLogValue;
import com.linebee.solrmeter.view.I18n;
import com.linebee.solrmeter.view.StatisticPanel;
import com.linebee.solrmeter.view.component.InfoPanel;
import com.linebee.stressTestScope.StressTestScope;

@StressTestScope
public class FullQueryStatisticPanel extends StatisticPanel {
	
	private static final long serialVersionUID = 7432143826253437314L;

	private static final int paddingSize = 2;
	private static final int doubleScale = 2;
	
	private FullQueryStatistic fullQueryStatictic;
	private QueryLogStatistic queryLogStatictic;
	
	private InfoPanel medianInfoPanel;
	private InfoPanel modeInfoPanel;
	private InfoPanel varianceInfoPanel;
	private InfoPanel standardDeviationInfoPanel;
	private InfoPanel totalAverageInfoPanel;
	private InfoPanel lastMinuteAverageInfoPanel;
	private InfoPanel lastTenMinutesAverageInfoPanel;
	private InfoPanel lastErrorInfoPanel;
	private JTable logTable;
//	private JButton clearButton;
	private JToggleButton scrollLockButton;
	
	@Inject
	public FullQueryStatisticPanel(FullQueryStatistic statictic, QueryLogStatistic queryLogStatictic) {
		super();
		this.fullQueryStatictic = statictic;
		this.queryLogStatictic = queryLogStatictic;
		this.initGUI();
	}

	private void initGUI() {
		this.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.insets = new Insets(3,3,3,3);
		this.add(this.createStatsPanel(), constraints);
		
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.weightx = 1;
		constraints.weighty = 1;
		this.add(this.createQueryLogPanel(), constraints);
		
	}
	
	private Component createStatsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setMinimumSize(new Dimension(200, 200));
		
		medianInfoPanel = new InfoPanel(I18n.get("statistic.fullQueryStatistic.median"));
		addAndPadd(panel,medianInfoPanel);

		modeInfoPanel = new InfoPanel(I18n.get("statistic.fullQueryStatistic.mode"));
		addAndPadd(panel,modeInfoPanel);

		varianceInfoPanel = new InfoPanel(I18n.get("statistic.fullQueryStatistic.variance"));
		addAndPadd(panel,varianceInfoPanel);

		standardDeviationInfoPanel = new InfoPanel(I18n.get("statistic.fullQueryStatistic.standardDeviation"));
		addAndPadd(panel,standardDeviationInfoPanel);

		totalAverageInfoPanel = new InfoPanel(I18n.get("statistic.fullQueryStatistic.totalAverage"));
		addAndPadd(panel,totalAverageInfoPanel);

		lastMinuteAverageInfoPanel = new InfoPanel(I18n.get("statistic.fullQueryStatistic.lastMinuteAverage"));
		addAndPadd(panel,lastMinuteAverageInfoPanel);

		lastTenMinutesAverageInfoPanel = new InfoPanel(I18n.get("statistic.fullQueryStatistic.lastTenMinutesAverage"));
		addAndPadd(panel,lastTenMinutesAverageInfoPanel);

		lastErrorInfoPanel = new InfoPanel(I18n.get("statistic.fullQueryStatistic.lastError"));
		addAndPadd(panel,lastErrorInfoPanel);
		return panel;
	}

	private Component createQueryLogPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(this.createButtonPanel());
		logTable = new JTable();
		logTable.setModel(this.createTableModel());
		panel.add(new JScrollPane(logTable));
		logTable.getColumnModel().getColumn(1).setMaxWidth(250);
		logTable.getColumnModel().getColumn(1).setPreferredWidth(250);
		return panel;
	}

	private Component createButtonPanel() {
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
		buttonPanel.add(Box.createHorizontalGlue());
//		buttonPanel.add(new JButton(I18n.get("statistic.fullQueryStatistic.clearButton")));
		scrollLockButton = new JToggleButton(I18n.get("statistic.fullQueryStatistic.freezeButton"));
		buttonPanel.add(scrollLockButton);
		return buttonPanel;
	}

	private TableModel createTableModel() {
		return new QueryLogTableModel(queryLogStatictic.getLastQueries());
	}

	@Override
	public String getStatisticName() {
		return I18n.get("statistic.fullQueryStatistic.title");
	}

	@Override
	public void refreshView() {
		Logger.getLogger(this.getClass()).debug("refreshing Full Query Statistics");
		medianInfoPanel.setValue(getString(fullQueryStatictic.getMedian()));
		modeInfoPanel.setValue(fullQueryStatictic.getMode().toString());
		varianceInfoPanel.setValue(getString(fullQueryStatictic.getVariance()));
		standardDeviationInfoPanel.setValue(getString(fullQueryStatictic.getStandardDeviation()));
		totalAverageInfoPanel.setValue(fullQueryStatictic.getTotaAverage().toString());
		lastMinuteAverageInfoPanel.setValue(fullQueryStatictic.getLastMinuteAverage().toString());
		lastTenMinutesAverageInfoPanel.setValue(fullQueryStatictic.getLastTenMinutesAverage().toString());
		if(fullQueryStatictic.getLastErrorTime() != null) {
			lastErrorInfoPanel.setValue(SimpleDateFormat.getInstance().format(fullQueryStatictic.getLastErrorTime()));
		} else {
			lastErrorInfoPanel.setValue("-");
		}
		if(!scrollLockButton.isSelected()) {
			((QueryLogTableModel)logTable.getModel()).refreshData(queryLogStatictic.getLastQueries());
		}
	}
	
	private String getString(Double number) {
		return new BigDecimal(number).setScale(doubleScale, BigDecimal.ROUND_HALF_DOWN).toString();
	}
	
	private void addAndPadd(JPanel panel, Component component) {
		panel.add(component);
		panel.add(Box.createRigidArea(new Dimension(paddingSize, paddingSize)));
	}
	
	public class QueryLogTableModel extends AbstractTableModel {
		
		private static final long serialVersionUID = 2802718550666767601L;

		private QueryLogValue[] queries;
		
		private String[] columnNames = new String[]{
				I18n.get("statistic.queryLog.status"), 
				I18n.get("statistic.queryLog.query"), 
				I18n.get("statistic.queryLog.filterQuery"),
				I18n.get("statistic.queryLog.facetQuery"),
				I18n.get("statistic.queryLog.qTime"),
				I18n.get("statistic.queryLog.resultCount")};
		
		public QueryLogTableModel(List<QueryLogValue> queries) {
			super();
			this.queries = new QueryLogValue[queries.size()];
			queries.toArray(this.queries);
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public int getRowCount() {
			return queries.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if(rowIndex > queries.length) {
				throw new RuntimeException("The requested row does not exist");
			}
			QueryLogValue query = queries[rowIndex];
			switch(columnIndex) {
				case 0: 
					if(query.isError()) {
						return I18n.get("statistic.queryLog.query.error");
					}else {
						return I18n.get("statistic.queryLog.query.ok");
					}
				case 1: return query.getQueryString();
				case 2: return query.getFilterQueryString();
				case 3: return query.getFacetQueryString();
				case 4: return query.getQTime();
				case 5: return query.getResults();
			}
			throw new RuntimeException("The requested column does not exist");
		}
		
		
		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}
		
		public void refreshData(List<QueryLogValue> newData) {
			this.queries = new QueryLogValue[newData.size()];
			newData.toArray(this.queries);
			this.fireTableDataChanged();
		}
		
		public QueryLogValue getOperation(int index) {
			return this.queries[index];
		}
		
	}
	
}
