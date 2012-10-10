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
package com.plugtree.solrmeter.view.component;

import com.plugtree.solrmeter.SolrMeterMain;
import com.plugtree.solrmeter.model.FileUtils;
import com.plugtree.solrmeter.model.SolrMeterConfiguration;
import com.plugtree.solrmeter.view.I18n;
import com.plugtree.solrmeter.view.listener.PropertyChangeListener;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class FilePropertyPanel extends PropertyPanel {

	private static final long serialVersionUID = 6121674014586012807L;
	
	protected JTextField textField;
	
	private JButton button;

	public FilePropertyPanel(String text, String property, boolean editable,
			PropertyChangeListener listener) {
		super(text, property, editable, listener);
		this.initGUI(text);
	}

	public FilePropertyPanel(String text, String property, boolean editable) {
		super(text, property, editable);
		this.initGUI(text);
	}

	public FilePropertyPanel(String text, String property,
			PropertyChangeListener listener) {
		super(text, property, listener);
		this.initGUI(text);
	}

	@Override
	protected String getSelectedValue() {
		return textField.getText();
	}

	@Override
	protected Component getVisualComponent() {
		JPanel panelAux = new JPanel();
		panelAux.setLayout(new BoxLayout(panelAux, BoxLayout.X_AXIS));
		panelAux.add(this.createTextField());
		panelAux.add(this.createButton());
		return panelAux;
	}

	private Component createButton() {
		button = new JButton(I18n.get("settings.fileProperty.browse"));
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fileChooser  = null;
				if(SolrMeterConfiguration.getProperty(property) == null) {
					fileChooser = new JFileChooser();
				} else {
					try {
						File file = new File(FileUtils.findFileAsResource(SolrMeterConfiguration.getProperty(property)).toURI());
						if(file.exists()) {
						  if(file.isDirectory()) {
						    fileChooser = new JFileChooser(file);
						  } else {
						    fileChooser = new JFileChooser(file.getParentFile());
						  }
						}
					} catch (Exception e) {
						
					} finally {
						if(fileChooser == null) {
							fileChooser = new JFileChooser();
						}
					}
				}
				fileChooser.setDialogType(FilePropertyPanel.this.getDialogType());
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				handleReturnValue(fileChooser.showOpenDialog(SolrMeterMain.mainFrame), fileChooser);
			}

		});
		return button;
	}

	private Component createTextField() {
		textField = new JTextField();
		this.setSelectedValue(SolrMeterConfiguration.getProperty(property));
		textField.addFocusListener(this);
		textField.setPreferredSize(new Dimension(MAX_COMPONENT_WIDTH, 0));
		return textField;
	}

	@Override
	protected void setSelectedValue(String value) {
		textField.setText(value);		
	}
	
  protected int getDialogType() {
    return JFileChooser.OPEN_DIALOG;
  }
  
  protected void handleReturnValue(int returnValue, JFileChooser fileChooser) {
    if(returnValue == JFileChooser.APPROVE_OPTION) {
      File selectedFile = fileChooser.getSelectedFile();
      if(!selectedFile.exists()) {
        Logger.getLogger(this.getClass()).error("Can't find file with name " + selectedFile.getName());
        //TODO show error
      }else {
        textField.setText(selectedFile.getAbsolutePath());
        notifyObservers();
      }
    }
  }

}
