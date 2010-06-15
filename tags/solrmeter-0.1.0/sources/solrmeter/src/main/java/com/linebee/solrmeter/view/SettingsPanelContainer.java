/**
 * Copyright Linebee. www.linebee.com
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
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import com.linebee.solrmeter.controller.SettingsController;
import com.linebee.solrmeter.view.settings.QuerySettingsPanel;
import com.linebee.solrmeter.view.settings.UpdateSettingsPanel;

public class SettingsPanelContainer extends JPanel {
	
	private static final long serialVersionUID = 6009079148719374295L;

	private Map<String, SettingsPanel> statistics;
	
	private JTabbedPane tabPanel;
	
	private JPanel buttonPanel;
	
	private JButton buttonApply;
	
	private JButton buttonOK;
	
	private JButton buttonCancel;
	
	private SettingsController settingsController;
	
	private boolean editable;
	
	public SettingsPanelContainer(Window parent) {
		this(parent, true);
	}
	
	public SettingsPanelContainer(Window parent, boolean editable) {
		super();
		this.editable = editable;
		statistics = new HashMap<String, SettingsPanel>();
		tabPanel = new JTabbedPane();
		settingsController = new SettingsController(this, parent);
		this.initGUI();
		addTabs();
	}

	private void addTabs() {
		QuerySettingsPanel querySettingsPanel = new QuerySettingsPanel(settingsController, editable);
		this.addSetting(querySettingsPanel);
		this.addSetting(new UpdateSettingsPanel(settingsController, editable));
	}

	private void initGUI() {
		buttonPanel = this.createButtonPanel();
		this.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		int row = 0;
		if(!editable) {
			constraints.gridx = 0;
			constraints.gridy = row;
			constraints.weighty = 0.0;
			JLabel messageLabel = new JLabel("<html><p>" + I18n.get("settings.noEditableMessage") + "</p></html>");
			messageLabel.setIcon(new ImageIcon(ClassLoader.getSystemClassLoader().getResource("./images/warning.png")));
			this.add(messageLabel, constraints);
			row++;
		}
		constraints.gridx = 0;
		constraints.gridy = row;
		constraints.weighty = 1.0;
		this.add(tabPanel, constraints);
		row++;
		
		constraints.gridx = 0;
		constraints.gridy = row;
		constraints.weighty = 0.0;
		constraints.weightx = 1.0;
		this.add(buttonPanel,constraints);
		
	}

	private JPanel createButtonPanel() {
		JPanel panel = new JPanel();
		panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.add(Box.createHorizontalGlue());
		
		buttonApply = new JButton(I18n.get("settings.button.apply"));
		buttonApply.setEnabled(false);
		buttonApply.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				settingsController.applyChanges();
			}
			
		});
		panel.add(buttonApply);
		
		panel.add(Box.createRigidArea(new Dimension(5, 5)));
		buttonOK = new JButton(I18n.get("settings.button.ok"));
		buttonOK.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				settingsController.acceptChanges();
			}
			
		});
		panel.add(buttonOK);
		
		panel.add(Box.createRigidArea(new Dimension(5, 5)));
		buttonCancel = new JButton(I18n.get("settings.button.cancel"));
		buttonCancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				settingsController.cancel();
			}
			
		});
		panel.add(buttonCancel);
		
		return panel;
	}

	public void addSetting(SettingsPanel panel) {
		statistics.put(panel.getSettingsName(), panel);
		tabPanel.addTab(panel.getSettingsName(),  new JScrollPane(panel));
	}

	public void hasChangedValues(boolean hasChangedValues) {
		buttonApply.setEnabled(hasChangedValues);
		
	}
}
