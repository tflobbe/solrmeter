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
package com.plugtree.solrmeter.view.statistic;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.plugtree.solrmeter.controller.FullQueryStatisticController;
import com.plugtree.solrmeter.model.statistic.FullQueryStatistic;
import com.plugtree.solrmeter.model.statistic.QueryLogStatistic;
import com.plugtree.solrmeter.model.statistic.QueryLogStatistic.QueryLogValue;
import com.plugtree.solrmeter.view.I18n;
import com.plugtree.solrmeter.view.StatisticPanel;
import com.plugtree.solrmeter.view.component.InfoPanel;
import com.plugtree.solrmeter.view.component.TooltipJTable;
import com.plugtree.stressTestScope.StressTestScope;

@StressTestScope
public class FullQueryStatisticPanel extends StatisticPanel {
	
	private static final int MARGIN = 10;
	
	private static final long serialVersionUID = 7432143826253437314L;

	private static final int doubleScale = 2;
	
	private FullQueryStatistic fullQueryStatistic;
	private QueryLogStatistic queryLogStatistic;
	
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
	private JButton exportButton;
	private FullQueryStatisticController controller;
		
	@Inject
	public FullQueryStatisticPanel(FullQueryStatistic statictic, QueryLogStatistic queryLogStatictic, FullQueryStatisticController controller) {
		super();
		this.fullQueryStatistic = statictic;
		this.queryLogStatistic = queryLogStatictic;
		this.controller = controller;
		this.initGUI();
	}

	private void initGUI() {
		this.setLayout(new BorderLayout());
		
		this.add(this.createStatsPanel(), BorderLayout.WEST);
		this.add(this.createQueryLogPanel(), BorderLayout.CENTER);
	}
	
	private Component createStatsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBorder(BorderFactory.createEmptyBorder(MARGIN, MARGIN, MARGIN, MARGIN));
		
		medianInfoPanel = new InfoPanel(I18n.get("statistic.fullQueryStatistic.median"));
		panel.add(medianInfoPanel);

		modeInfoPanel = new InfoPanel(I18n.get("statistic.fullQueryStatistic.mode"));
		panel.add(modeInfoPanel);

		varianceInfoPanel = new InfoPanel(I18n.get("statistic.fullQueryStatistic.variance"));
		panel.add(varianceInfoPanel);

		standardDeviationInfoPanel = new InfoPanel(I18n.get("statistic.fullQueryStatistic.standardDeviation"));
		panel.add(standardDeviationInfoPanel);

		totalAverageInfoPanel = new InfoPanel(I18n.get("statistic.fullQueryStatistic.totalAverage"));
		panel.add(totalAverageInfoPanel);

		lastMinuteAverageInfoPanel = new InfoPanel(I18n.get("statistic.fullQueryStatistic.lastMinuteAverage"));
		panel.add(lastMinuteAverageInfoPanel);

		lastTenMinutesAverageInfoPanel = new InfoPanel(I18n.get("statistic.fullQueryStatistic.lastTenMinutesAverage"));
		panel.add(lastTenMinutesAverageInfoPanel);

		lastErrorInfoPanel = new InfoPanel(I18n.get("statistic.fullQueryStatistic.lastError"));
		panel.add(lastErrorInfoPanel);
		
		return panel;
	}

	private Component createQueryLogPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBorder(BorderFactory.createEmptyBorder(MARGIN, MARGIN, MARGIN, MARGIN));
		logTable = new TooltipJTable();
		logTable.setModel(this.createTableModel());
		panel.add(new JScrollPane(logTable));
		logTable.getColumnModel().getColumn(1).setPreferredWidth(250);
		panel.add(this.createButtonPanel());
		return panel;
	}

	private Component createButtonPanel() {
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(Box.createHorizontalGlue());
		scrollLockButton = new JToggleButton(I18n.get("statistic.fullQueryStatistic.freezeButton"));
		exportButton = new JButton(I18n.get("statistic.fullQueryStatistic.exportButton"));
		
		exportButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.onExportButtonClick();
			}
		});
		
		buttonPanel.add(scrollLockButton);
		buttonPanel.add(Box.createHorizontalStrut(MARGIN));
		buttonPanel.add(exportButton);
		return buttonPanel;
	}

	private TableModel createTableModel() {
		return new QueryLogTableModel(queryLogStatistic.getLastQueries());
	}

	@Override
	public String getStatisticName() {
		return I18n.get("statistic.fullQueryStatistic.title");
	}

	@Override
	public void refreshView() {
		Logger.getLogger(this.getClass()).debug("refreshing Full Query Statistics");
		medianInfoPanel.setValue(getString(fullQueryStatistic.getMedian()));
		modeInfoPanel.setValue(fullQueryStatistic.getMode().toString());
		varianceInfoPanel.setValue(getString(fullQueryStatistic.getVariance()));
		standardDeviationInfoPanel.setValue(getString(fullQueryStatistic.getStandardDeviation()));
		totalAverageInfoPanel.setValue(fullQueryStatistic.getTotaAverage().toString());
		lastMinuteAverageInfoPanel.setValue(fullQueryStatistic.getLastMinuteAverage().toString());
		lastTenMinutesAverageInfoPanel.setValue(fullQueryStatistic.getLastTenMinutesAverage().toString());
		if(fullQueryStatistic.getLastErrorTime() != null) {
			lastErrorInfoPanel.setValue(SimpleDateFormat.getInstance().format(fullQueryStatistic.getLastErrorTime()));
		} else {
			lastErrorInfoPanel.setValue("-");
		}
		if(!scrollLockButton.isSelected()) {
			((QueryLogTableModel)logTable.getModel()).refreshData(queryLogStatistic.getLastQueries());
		}
	}
	
	private String getString(Double number) {
		return new BigDecimal(number).setScale(doubleScale, BigDecimal.ROUND_HALF_DOWN).toString();
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
