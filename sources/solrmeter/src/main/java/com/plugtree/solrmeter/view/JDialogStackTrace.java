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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.plugtree.solrmeter.model.SolrMeterConfiguration;
import com.plugtree.solrmeter.model.exception.OperationException;
import com.plugtree.solrmeter.view.component.InfoPanel;

public class JDialogStackTrace extends JDialog {
	
	private static final long serialVersionUID = 1L;
	private final static SimpleDateFormat dateFormat = new SimpleDateFormat(SolrMeterConfiguration.getProperty("solr.view.logPanel.dateFormat", "MM/dd/yyyy HH:mm:ss:SS"));
	
	public JDialogStackTrace(Frame parent, OperationException exception) {
		super(parent, I18n.get("dialogStackTrace.title"));
		this.initGUI(exception);
		this.setSize(new Dimension(600, 400));
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}

	private void initGUI(OperationException exception) {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(1, 1, 1, 1);
		this.setLayout(new GridBagLayout());
		{
			constraints.gridx = 0;
			constraints.gridy = 0;
			constraints.gridwidth = 1;
			constraints.gridheight = 1;
			constraints.weightx = 0.0;
			constraints.weighty = 0.0;
			this.add(this.createLabelOperationType(exception), constraints);
		}
		{
			constraints.gridx = 1;
			constraints.gridy = 0;
			constraints.gridwidth = 1;
			constraints.gridheight = 1;
			constraints.weightx = 2.0;
			constraints.weighty = 0.0;
			this.add(new JLabel(), constraints);
		}
		{
			constraints.gridx = 2;
			constraints.gridy = 0;
			constraints.gridwidth = 1;
			constraints.gridheight = 1;
			constraints.weightx = 0.0;
			constraints.weighty = 0.0;
			this.add(this.createLabelDate(exception), constraints);
		}
		{
			constraints.gridx = 0;
			constraints.gridy = 1;
			constraints.gridwidth = 3;
			constraints.gridheight = 1;
			constraints.weightx = 2.0;
			constraints.weighty = 2.0;
			this.add(this.createStackTrace(exception), constraints);
		}
		
		{
			constraints.gridx = 0;
			constraints.gridy = 2;
			constraints.gridwidth = 3;
			constraints.gridheight = 1;
			constraints.weightx = 0.0;
			constraints.weighty = 0.0;
			this.add(this.createButtonBar(), constraints);
		}
		
	}

	private Component createButtonBar() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.add(Box.createHorizontalGlue());
		JButton closeButton = new JButton(I18n.get("dialogStackTrace.close"));
		panel.add(closeButton);
		closeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				closeDialog();
			}
			
		});
		return panel;
	}
	
	private void closeDialog() {
		this.setVisible(false);
		this.dispose();
	}

	private Component createStackTrace(OperationException exception) {
		JTextArea textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setOpaque(false);
		textArea.setBorder(null);
		textArea.setText(SwingUtils.getStackTraceString(exception));
		textArea.setSize(200, 200);
		exception.printStackTrace();
		return new JScrollPane(textArea);
	}

	private Component createLabelDate(OperationException exception) {
		return new InfoPanel(I18n.get("dialogStackTrace.ocurredAt"), dateFormat.format(exception.getDate()));
	}

	private Component createLabelOperationType(OperationException exception) {
		return new InfoPanel(I18n.get("dialogStackTrace.operationType"), exception.getOperationName());
	}

}
