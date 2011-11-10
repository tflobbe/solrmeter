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

import com.plugtree.solrmeter.model.SolrMeterConfiguration;
import com.plugtree.solrmeter.view.listener.PropertyChangeListener;

import javax.swing.*;
import java.awt.*;

public class TextPropertyPanel extends PropertyPanel {
	
	private static final long serialVersionUID = -8432741922913438587L;
	private JTextField textField;

	public TextPropertyPanel(String text, String property, boolean editable,
			PropertyChangeListener listener) {
		super(text, property, editable, listener);
		this.initGUI(text);
	}

	public TextPropertyPanel(String text, String property, boolean editable) {
		super(text, property, editable);
		this.initGUI(text);
	}

	public TextPropertyPanel(String text, String property,
			PropertyChangeListener listener) {
		super(text, property, listener);
		this.initGUI(text);
	}
	
	protected String getSelectedValue() {
        
		return textField.getText();
	}
	
	protected Component getVisualComponent() {
		textField = new JTextField();
		this.setSelectedValue(SolrMeterConfiguration.getProperty(property));
		textField.addFocusListener(this);
//		textField.setMaximumSize(new Dimension(MAX_COMPONENT_WIDTH - 50, Integer.MAX_VALUE));
		return textField;
	}

	@Override
	protected void setSelectedValue(String value) {
		textField.setText(value);		
	}

}
