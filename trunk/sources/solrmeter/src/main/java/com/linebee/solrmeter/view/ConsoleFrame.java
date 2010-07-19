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
package com.linebee.solrmeter.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.MenuBar;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ConsoleFrame extends JFrame {
	
	private static final long serialVersionUID = 976934495299084244L;
	private QueryConsolePanel queryPanel;
	private UpdateConsolePanel updatePanel;
	private OptimizeConsolePanel optimizePanel;
	private StatisticsContainer statisticsContainer;
	
	
	@Inject
	public ConsoleFrame(QueryConsolePanel queryPanel,
			UpdateConsolePanel updatePanel, OptimizeConsolePanel optimizePanel,
			StatisticsContainer statisticsContainer) throws HeadlessException {
		super();
		this.queryPanel = queryPanel;
		this.updatePanel = updatePanel;
		this.optimizePanel = optimizePanel;
		this.statisticsContainer = statisticsContainer;
//		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setSize(new Dimension(800, 700));
		this.initGUI();
		this.addWindowListener(new WindowAdapter() {
	        public void windowClosing(WindowEvent evt) {
	        	exitApplication();
	        }
	    });
		SwingUtils.centerWindow(this);
	}

	private void exitApplication() {
		System.exit(0);
	}

	private void initGUI() {
		this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		JPanel auxiliarPanel = new JPanel();
		auxiliarPanel.setLayout(new GridBagLayout());
		auxiliarPanel.add(this.getQueryPanel(), new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		auxiliarPanel.add(this.getUpdatePanel(), new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		auxiliarPanel.add(this.getOptimizePanel(), new GridBagConstraints(2, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		
		this.getContentPane().add(auxiliarPanel);
		this.addStatisticsPanel();
		
	}

	private OptimizeConsolePanel getOptimizePanel() {
		return optimizePanel;
	}

	private UpdateConsolePanel getUpdatePanel() {
		return updatePanel;
	}

	private void addStatisticsPanel() {
		this.getContentPane().add(statisticsContainer);
	}

	private QueryConsolePanel getQueryPanel() {
		return queryPanel;
	}

	public void onConfigurationChanged() {
		statisticsContainer.clearStatistics();
		this.getContentPane().removeAll();
		this.initGUI();
		this.getContentPane().repaint();
		((JComponent)this.getContentPane()).revalidate();
	}
	
	@Inject
	@Override
	public void setMenuBar(MenuBar mb) {
		super.setMenuBar(mb);
	}

	public StatisticsContainer getStatisticsContainer() {
		return statisticsContainer;
	}

	public void setStatisticsContainer(StatisticsContainer statisticsContainer) {
		this.statisticsContainer = statisticsContainer;
	}

	public void setQueryPanel(QueryConsolePanel queryPanel) {
		this.queryPanel = queryPanel;
	}

	public void setUpdatePanel(UpdateConsolePanel updatePanel) {
		this.updatePanel = updatePanel;
	}

	public void setOptimizePanel(OptimizeConsolePanel optimizePanel) {
		this.optimizePanel = optimizePanel;
	}

}
