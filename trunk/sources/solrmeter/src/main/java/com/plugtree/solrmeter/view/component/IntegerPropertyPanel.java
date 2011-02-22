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


import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import com.plugtree.solrmeter.model.SolrMeterConfiguration;
import com.plugtree.solrmeter.view.listener.PropertyChangeListener;

/**
 * Property panel that shows a JSpinner to select integer values
 * @author Emmanuel Espina
 *
 */

public class IntegerPropertyPanel extends PropertyPanel {

	private static final long serialVersionUID = -1462790928641899755L;
	private JSpinner spinner;
	
	public IntegerPropertyPanel(String text, String property, boolean editable) {
		super(text, property, editable);
		this.initGUI(text);
	}
	
	public IntegerPropertyPanel(String text, String property, boolean editable,
			PropertyChangeListener listener) {
		super(text, property, editable, listener);
		this.initGUI(text);
	}

	@Override
	protected String getSelectedValue() {
		SpinnerNumberModel model = (SpinnerNumberModel) this.spinner.getModel();
		return model.getNumber().toString();
	}
	
	@Override
	protected Component getVisualComponent() {
		this.spinner = new JSpinner();
		SpinnerNumberModel model = new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)); 
		this.spinner.setModel(model);
		
		String initial = SolrMeterConfiguration.getProperty(property);
		if(initial != null){
			this.setSelectedValue(initial);			
		}
		
		spinner.addChangeListener(new ChangeListener() {			
			@Override
			public void stateChanged(ChangeEvent e) {
				notifyObservers();				
			}
		});
		
		return this.spinner;
	}

	@Override
	protected void setSelectedValue(String value) {
		SpinnerModel model = spinner.getModel();
		try {
			model.setValue(new Integer(value));
		} catch(NumberFormatException ex){
			Logger.getLogger(this.getClass()).error("Can't parse string " + value + " as integer. Asuming 0");
			model.setValue(new Integer(0));
		}
		
	}
	
	

}
