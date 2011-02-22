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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import com.plugtree.solrmeter.SolrMeterMain;
import com.plugtree.solrmeter.controller.SettingsController;
import com.plugtree.solrmeter.view.settings.AdvancedSettingsPanel;

public class AdvancedSettingsDialog extends JDialog implements ActionListener {
	
	private static final long serialVersionUID = 7371972543919153746L;
	private SettingsController sc;
	private JButton closeButton;
	
	public AdvancedSettingsDialog(SettingsController settingsController){
		super(SolrMeterMain.mainFrame);
		sc = settingsController;
		initGui();
	}
	
	private void initGui(){
		JPanel innerPanel = new JPanel();
		innerPanel.setLayout(new BorderLayout());
		
		JPanel buttonPanel = new JPanel();		
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));	
		closeButton = new JButton(I18n.get("settings.advanced.close"));
		closeButton.addActionListener(this);
		buttonPanel.add(closeButton);
		innerPanel.add(buttonPanel, BorderLayout.PAGE_END);

		AdvancedSettingsPanel advancedPanel = new AdvancedSettingsPanel(sc, true);
		innerPanel.add(advancedPanel, BorderLayout.CENTER);		
		
		this.setName(advancedPanel.getSettingsName());
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.setContentPane(innerPanel);
		this.setSize(new Dimension(500, 350));
		SwingUtils.centerWindow(this);
		this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == closeButton){
			this.setVisible(false);
		}		
	}

}
