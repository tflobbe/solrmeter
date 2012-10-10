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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

import com.plugtree.solrmeter.model.SolrMeterConfiguration;
import com.plugtree.solrmeter.view.listener.PropertyChangeListener;

public class ComboPropertyPanel extends PropertyPanel {
	
	private static final long serialVersionUID = 1L;

	protected JComboBox comboBox;
	
	private String[] values;
	
	private boolean allowUserOption;

	public ComboPropertyPanel(String text, String property, boolean editable,
			PropertyChangeListener listener, String[] values, boolean allowUserOption) {
		super(text, property, editable, listener);
		this.values = values;
		this.allowUserOption = allowUserOption;
		this.initGUI(text);
	}

	public ComboPropertyPanel(String text, String property, boolean editable, String[] values, boolean allowUserOption) {
		super(text, property, editable);
		this.values = values;
		this.allowUserOption = allowUserOption;
		this.initGUI(text);
	}

	public ComboPropertyPanel(String text, String property,
			PropertyChangeListener listener, String[] values, boolean allowUserOption) {
		super(text, property, listener);
		this.values = values;
		this.allowUserOption = allowUserOption;
		this.initGUI(text);
	}

	@Override
	protected String getSelectedValue() {
		return comboBox.getSelectedItem().toString();
	}

	@Override
	protected Component getVisualComponent() {
		comboBox = new JComboBox();
		for(String value:values) {
			comboBox.addItem(value);
		}
		if(SolrMeterConfiguration.getProperty(property) != null && !"".equals(SolrMeterConfiguration.getProperty(property))) {
			this.setSelectedValue(SolrMeterConfiguration.getProperty(property));
		}
		comboBox.addFocusListener(this);
		comboBox.setEditable(this.isComboEditable());
		comboBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				notifyObservers();
			}
		});
		return comboBox;
	}
	
	protected boolean isComboEditable() {
		return allowUserOption;
	}

	@Override
	protected void setSelectedValue(String value) {
		comboBox.setSelectedItem(value);		
	}

}
