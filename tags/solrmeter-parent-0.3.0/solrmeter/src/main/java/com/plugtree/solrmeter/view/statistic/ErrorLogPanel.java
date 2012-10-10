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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.plugtree.stressTestScope.StressTestScope;
import com.plugtree.solrmeter.controller.ErrorLogController;
import com.plugtree.solrmeter.model.SolrMeterConfiguration;
import com.plugtree.solrmeter.model.exception.OperationException;
import com.plugtree.solrmeter.model.statistic.ErrorLogStatistic;
import com.plugtree.solrmeter.view.I18n;
import com.plugtree.solrmeter.view.StatisticPanel;

@StressTestScope
public class ErrorLogPanel extends StatisticPanel implements ActionListener, MouseListener {
	
	private static final long serialVersionUID = 6190280732555917695L;
	
	private final static SimpleDateFormat dateFormat = new SimpleDateFormat(SolrMeterConfiguration.getProperty("solr.view.logPanel.dateFormat", "MM/dd/yyyy HH:mm:ss:SS"));

	private JTable logTable;
	
	private ErrorLogStatistic statistic;
	
	private JCheckBox showQueries;
	
	private JCheckBox showAdds;
	
	private JCheckBox showCommits;
	
	private JCheckBox showOptimizes;
	
	private ErrorLogController controller;

	@Inject
	public ErrorLogPanel(ErrorLogStatistic statistic, ErrorLogController controller) {
		super();
		this.statistic = statistic;
		this.controller = controller;
		initGUI();
	}

	private void initGUI() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JPanel checkBoxPanel = this.createJPanelCheckBox();
		
		logTable = new JTable();
		logTable.setModel(this.createTableModel());
		logTable.getColumnModel().getColumn(0).setMaxWidth(150);
		logTable.getColumnModel().getColumn(1).setMaxWidth(150);
		logTable.getColumnModel().getColumn(1).setPreferredWidth(150);
		logTable.addMouseListener(this);
		
		
		this.add(new JScrollPane(logTable));
		this.add(checkBoxPanel);
	}
	
	private JPanel createJPanelCheckBox() {
		JPanel panel = new JPanel();
		panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel.getMinimumSize().height));
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		showAdds = new JCheckBox(I18n.get("statistic.errorLogPanel.showAddErros"));
		showCommits = new JCheckBox(I18n.get("statistic.errorLogPanel.showCommitErrors"));
		showOptimizes = new JCheckBox(I18n.get("statistic.errorLogPanel.showOptimizeErrors"));
		showQueries = new JCheckBox(I18n.get("statistic.errorLogPanel.showQueryErrors"));
		this.checkAll();
		this.addCheckBoxListeners();
		panel.add(Box.createHorizontalGlue());
		panel.add(showAdds);
		panel.add(Box.createHorizontalGlue());
		panel.add(showCommits);
		panel.add(Box.createHorizontalGlue());
		panel.add(showOptimizes);
		panel.add(Box.createHorizontalGlue());
		panel.add(showQueries);
		panel.add(Box.createHorizontalGlue());
		return panel;
	}

	private void addCheckBoxListeners() {
		showAdds.addActionListener(this);
		showCommits.addActionListener(this);
		showOptimizes.addActionListener(this);
		showQueries.addActionListener(this);
		
	}

	private void checkAll() {
		showAdds.setSelected(true);
		showCommits.setSelected(true);
		showOptimizes.setSelected(true);
		showQueries.setSelected(true);
		
	}

	@Override
	public String getStatisticName() {
		return I18n.get("statistic.errorLogPanel.title");
	}

	@Override
	public synchronized void refreshView() {
		Logger.getLogger(this.getClass()).debug("refreshing Error Log");
		((OperationExceptionTableModel)logTable.getModel()).refreshData(getErrorsToShow());
	}

	private TableModel createTableModel() {
		return new OperationExceptionTableModel(getErrorsToShow());
	}

	private List<OperationException> getErrorsToShow() {
		return statistic.getLastErrors(showCommits.isSelected(), showOptimizes.isSelected(), showQueries.isSelected(), showAdds.isSelected());
	}
	
	public class OperationExceptionTableModel extends AbstractTableModel {
		
		private static final long serialVersionUID = 2802718550666767601L;

		private List<OperationException> errors;
		
		private String[] columnNames = new String[]{I18n.get("statistic.errorLogPanel.column.operation"), 
				I18n.get("statistic.errorLogPanel.column.time"), 
				I18n.get("statistic.errorLogPanel.column.message")};
		
		public OperationExceptionTableModel(List<OperationException> errors) {
			super();
			this.errors = errors;
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public int getRowCount() {
			return errors.size();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if(rowIndex > errors.size()) {
				throw new RuntimeException("The requested row does not exist");
			}
			OperationException operationException = errors.get(rowIndex);
			switch(columnIndex) {
				case 0: return operationException.getOperationName();
				case 1: return dateFormat.format(operationException.getDate());
				case 2: return operationException.getMessage();
			}
			throw new RuntimeException("The requested column does not exist");
		}
		
		
		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}
		
		public void refreshData(List<OperationException> errors) {
			this.errors = errors;
			this.fireTableDataChanged();
		}
		
		public OperationException getOperation(int index) {
			return this.errors.get(index);
		}
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		controller.onErrorsToShowChaned();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getClickCount() == 2) {
			OperationException exception = ((OperationExceptionTableModel)logTable.getModel()).getOperation(logTable.getSelectedRow());
			this.controller.onErrorDoubleClick(exception);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

}
