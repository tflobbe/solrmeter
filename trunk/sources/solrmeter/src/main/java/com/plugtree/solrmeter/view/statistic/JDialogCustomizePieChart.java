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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;

import com.plugtree.solrmeter.model.FileUtils;
import com.plugtree.solrmeter.model.statistic.TimeRange;
import com.plugtree.solrmeter.model.statistic.TimeRangeStatistic;
import com.plugtree.solrmeter.view.I18n;
import com.plugtree.solrmeter.view.SwingUtils;
import com.plugtree.solrmeter.view.component.IntegerField;
/**
 * Dialog for customizing the pie chart intervals
 * @author tflobbe
 *
 */
public class JDialogCustomizePieChart extends JDialog {

	private static final long serialVersionUID = 8726170637994747017L;
	private IntegerField jTextFieldMin;
	private IntegerField jTextFieldMax;
	private JButton jButtonAdd;

	private TimeRangeStatistic statistic;
	private List<TimeRange> ranges;

	private JPanel jPanelRanges;
	
	private JLabel jLabelValidation;
	
	public JDialogCustomizePieChart(Window parent, TimeRangeStatistic statistic) {
		super(parent);
		this.setTitle(I18n.get("statistic.pieChartPanel.customize.ranges"));
		this.statistic = statistic;
		this.ranges = new LinkedList<TimeRange>(statistic.getActualRanges());
		this.sortRanges();
		this.initGUI();
		this.setSize(new Dimension(300, 300));
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		SwingUtils.centerWindow(this);
		this.setModal(true);
	}

	private void sortRanges() {
		Collections.sort(ranges, new Comparator<TimeRange>() {

			@Override
			public int compare(TimeRange arg0, TimeRange arg1) {
				if(arg0.getMinTime() > arg1.getMinTime()) {
					return 1;
				}
				if(arg0.getMinTime() < arg1.getMinTime()) {
					return -1;
				}
				return 0;
			}
			
		});
		
	}

	private void initGUI() {
		int row = 0;
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(1, 1, 1, 1);
		constraints.weightx = 2.0;
		this.setLayout(new GridBagLayout());
		if(!statistic.getActualPercentage().isEmpty()) {
			constraints.gridx = 0;
			constraints.gridy = row;
			constraints.weighty = 0.0;
			JLabel messageLabel = new JLabel("<html><p>" + I18n.get("statistic.pieChartPanel.customize.isRunning") + "</p></html>");
			try {
				messageLabel.setIcon(new ImageIcon(FileUtils.findFileAsResource("./images/warning.png")));
			} catch (FileNotFoundException e) {
				Logger.getLogger(JDialogCustomizePieChart.class).error("Image not found", e);
				//no image added, but stil working.
			}
			this.add(messageLabel, constraints);
			row++;
		}
		{
			constraints.gridx = 0;
			constraints.gridy = row;
			constraints.weighty = 0.0;
			jLabelValidation = new JLabel("");
			jLabelValidation.setForeground(Color.red);
			this.add(jLabelValidation, constraints);
			row++;
		}
		{
			constraints.gridx = 0;
			constraints.gridy = row;
			constraints.weighty = 0.0;
			this.add(this.createAddRagePanel(), constraints);
			row++;
		}
		{
			constraints.gridx = 0;
			constraints.gridy = row;
			constraints.weighty = 2.0;
			this.add(this.createRangesPanel(), constraints);
			row++;
		}
		{
			constraints.gridx = 0;
			constraints.gridy = row;
			constraints.weighty = 0.0;
			this.add(this.createButtonsPanel(), constraints);
			row++;
		}
	}

	private JPanel createButtonsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.add(Box.createHorizontalGlue());
		JButton jButtonApply = new JButton("Apply");
		jButtonApply.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				applyChanges();
			}
			
		});
		JButton jButtonOK = new JButton("OK");
		jButtonOK.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				ok();
			}
			
		});
		JButton jButtonCancel = new JButton("Cancel");
		jButtonCancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				cancel();
			}
			
		});
		panel.add(jButtonApply);
		panel.add(Box.createRigidArea(new Dimension(5, 5)));
		panel.add(jButtonOK);
		panel.add(Box.createRigidArea(new Dimension(5, 5)));
		panel.add(jButtonCancel);
		panel.add(Box.createRigidArea(new Dimension(5, 5)));
		return panel;
	}
	
	private void applyChanges() {
		statistic.removeAllRanges();
		for(TimeRange range:ranges) {
			statistic.addNewRange(range.getMinTime(), range.getMaxTime());
		}
	}
	
	private void ok() {
		applyChanges();
		this.dispose();
	}
	
	private void cancel() {
		this.dispose();
	}
	

	private void refreshRangesPanel() {
		jPanelRanges.removeAll();
		for(TimeRange range:ranges) {
			jPanelRanges.add(createTimeRangePanel(range));
			jPanelRanges.add(Box.createRigidArea(new Dimension(1, 1)));
		}
	}

	private Component createRangesPanel() {
		jPanelRanges = new JPanel();
		jPanelRanges.setLayout(new BoxLayout(jPanelRanges, BoxLayout.Y_AXIS));
		refreshRangesPanel();
		return new JScrollPane(jPanelRanges);
	}

	private JPanel createTimeRangePanel(TimeRange range) {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.add(new JLabel(I18n.get("statistic.pieChartPanel.customize.min") + ": " + range.getMinTime()));
		panel.add(Box.createRigidArea(new Dimension(2, 2)));
		panel.add(new JLabel(I18n.get("statistic.pieChartPanel.customize.max") + ": "+ range.getMaxTime()));
		panel.add(Box.createHorizontalGlue());
		JButton jButtonRemove;
		try {
			jButtonRemove = new JButton(new ImageIcon(FileUtils.findFileAsResource("./images/remove.gif")));
		} catch (FileNotFoundException e) {
			jButtonRemove = new JButton(I18n.get("statistic.pieChartPanel.customize.remove"));
		}
		jButtonRemove.addActionListener(new RemoveRangeButtonListener(range));
		panel.add(jButtonRemove);
		return panel;
	}

	private Component createAddRagePanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.add(new JLabel(I18n.get("statistic.pieChartPanel.customize.min")));
		jTextFieldMin = new IntegerField();
		panel.add(jTextFieldMin);
		panel.add(Box.createRigidArea(new Dimension(1, 1)));
		panel.add(new JLabel(I18n.get("statistic.pieChartPanel.customize.ms")));
		panel.add(Box.createRigidArea(new Dimension(5, 5)));
		panel.add(new JLabel(I18n.get("statistic.pieChartPanel.customize.max")));
		panel.add(Box.createRigidArea(new Dimension(1, 1)));
		jTextFieldMax = new IntegerField();
		panel.add(jTextFieldMax);
		panel.add(Box.createRigidArea(new Dimension(1, 1)));
		panel.add(new JLabel(I18n.get("statistic.pieChartPanel.customize.ms")));
		panel.add(Box.createRigidArea(new Dimension(1, 1)));
		try {
			jButtonAdd = new JButton(new ImageIcon(FileUtils.findFileAsResource("./images/add.png")));
		} catch (FileNotFoundException e) {
			Logger.getLogger(this.getClass()).warn("Image add.png not found");
			jButtonAdd = new JButton(I18n.get("statistic.pieChartPanel.customize.add"));
		}
		jButtonAdd.addActionListener(new AddRangeButtonListener());
		panel.add(jButtonAdd);
		return panel;
	}
	
	private class AddRangeButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if(!this.validateData()) {
				return;
			}
			jTextFieldMin.setText("");
			jTextFieldMax.setText("");
			refreshRangesPanel();
			jPanelRanges.revalidate();
			jPanelRanges.repaint();
		}

		private boolean validateData() {
			Integer min = jTextFieldMin.getInteger();
			Integer max = jTextFieldMax.getInteger();
			if(min == null) {
				jLabelValidation.setText(I18n.get("statistic.pieChartPanel.customize.minRequired"));
				return false;
			}
			if(min < 0) {
				jLabelValidation.setText(I18n.get("statistic.pieChartPanel.customize.minLessThan0"));
				return false;
			}
			if(max != null && min > max) {
				jLabelValidation.setText(I18n.get("statistic.pieChartPanel.customize.minLessThanMax"));
				return false;
			}
			TimeRange range;
			if(max == null) {
				range = new TimeRange(min);
			}else {
				range = new TimeRange(min, max);
			}
			ranges.add(range);
			if (overlap()) {
				jLabelValidation.setText(I18n.get("statistic.pieChartPanel.customize.overlap"));
			}
			return true;
		}

	}
	
	public boolean overlap() {
		Collections.sort(ranges, new Comparator<TimeRange>() {

			@Override
			public int compare(TimeRange arg0, TimeRange arg1) {
				if(arg0.getMinTime() > arg1.getMinTime()) {
					return 1;
				}
				if(arg0.getMinTime() < arg1.getMinTime()) {
					return -1;
				}
				return 0;
			}
			
		});
		for(int i = 0; i < (ranges.size() - 1); i++) {
			if(ranges.get(i).getMaxTime() >= ranges.get(i + 1).getMinTime()) {
				return true;
			}
		}
		return false;
	}

	private class RemoveRangeButtonListener implements ActionListener {
		
		private TimeRange timeRange;
		
		public RemoveRangeButtonListener(TimeRange timeRange) {
			super();
			this.timeRange = timeRange;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			ranges.remove(timeRange);
			if (overlap()) {
				jLabelValidation.setText(I18n.get("statistic.pieChartPanel.customize.overlap"));
			}else {
				jLabelValidation.setText("");
			}
			refreshRangesPanel();
			jPanelRanges.revalidate();
			jPanelRanges.repaint();
		}

	}

}
