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

import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ConsoleFrame extends JFrame {
	
	private static final long serialVersionUID = 976934495299084244L;
	private QueryConsolePanel queryPanel;
	private UpdateConsolePanel updatePanel;
	private OptimizeConsolePanel optimizePanel;
	private CommitConsolePanel commitPanel;
	private StatisticsContainer statisticsContainer;
	
	
	@Inject
	public ConsoleFrame(QueryConsolePanel queryPanel,
			UpdateConsolePanel updatePanel, OptimizeConsolePanel optimizePanel,
			CommitConsolePanel commitPanel, StatisticsContainer statisticsContainer) throws HeadlessException {
		super();
		this.setLocale(I18n.getLocale());
		this.queryPanel = queryPanel;
		this.updatePanel = updatePanel;
		this.optimizePanel = optimizePanel;
		this.commitPanel = commitPanel;
		this.statisticsContainer = statisticsContainer;
		this.initGUI();
		this.addWindowListener(new WindowAdapter() {
	        public void windowClosing(WindowEvent evt) {
	        	exitApplication();
	        }
	    });
	}

	private void exitApplication() {
		System.exit(0);
	}

	private void initGUI() {
		this.getContentPane().setLayout(new BorderLayout());
		
		JPanel auxiliarPanel = new JPanel();		
		auxiliarPanel.setLayout(new BoxLayout(auxiliarPanel, BoxLayout.X_AXIS));
		auxiliarPanel.add(this.getQueryPanel());
		auxiliarPanel.add(this.getUpdatePanel());
		auxiliarPanel.add(this.getCommitPanel());
		auxiliarPanel.add(this.getOptimizePanel());
		
		this.getContentPane().add(auxiliarPanel, BorderLayout.NORTH);
		this.addStatisticsPanel();
	}

	private OptimizeConsolePanel getOptimizePanel() {
		return optimizePanel;
	}

	private UpdateConsolePanel getUpdatePanel() {
		return updatePanel;
	}
	
	private CommitConsolePanel getCommitPanel() {
		return commitPanel;
	}

	private void addStatisticsPanel() {
		this.getContentPane().add(statisticsContainer, BorderLayout.CENTER);
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
	public void setJMenuBar(JMenuBar mb) {
		super.setJMenuBar(mb);
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
	
	public void setCommitPanel(CommitConsolePanel commitPanel) {
		this.commitPanel = commitPanel;
	}

	public void setOptimizePanel(OptimizeConsolePanel optimizePanel) {
		this.optimizePanel = optimizePanel;
	}

}
