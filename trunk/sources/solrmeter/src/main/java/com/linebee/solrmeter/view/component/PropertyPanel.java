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
package com.linebee.solrmeter.view.component;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.linebee.solrmeter.model.SolrMeterConfiguration;
import com.linebee.solrmeter.view.listener.PropertyChangeListener;
/**
 * 
 * TODO add validators
 * @author tflobbe
 *
 */
public class PropertyPanel extends JPanel implements FocusListener {

	private static final long serialVersionUID = -6531190706130757263L;
	
	private static final int paddingLeft = 1;
	private static final int paddingCenter = 1;
	private static final int paddingRight = 1;

	private JLabel label;
	
	private JTextField textField;
	
	private List<PropertyChangeListener> listeners;
	
	private String property;
	
	private boolean editable;
	
	public PropertyPanel(String text, String property, PropertyChangeListener listener) {
		this(text, property, true);
		listeners.add(listener);
	}
	
	public PropertyPanel(String text, String property, boolean editable, PropertyChangeListener listener) {
		this(text, property, editable);
		listeners.add(listener);
	}
	
	public PropertyPanel(String text, String property, boolean editable) {
		super();
		this.editable = editable;
		listeners = new LinkedList<PropertyChangeListener>();
		this.property = property;
		this.initGUI(text);
	}

	protected void initGUI(String text) {
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
		this.add(Box.createRigidArea(new Dimension(paddingLeft, paddingLeft)));
		label = new JLabel(text + ":");
		label.setFont(new Font(label.getFont().getName(), Font.BOLD, label.getFont().getSize()));
		this.add(label);
		this.add(Box.createRigidArea(new Dimension(paddingCenter, paddingCenter)));
		if(editable) {
			this.createInput();
		}else {
			this.add(new JLabel(SolrMeterConfiguration.getProperty(property)));
		}
		this.add(Box.createRigidArea(new Dimension(paddingRight, paddingRight)));
	}

	private void createInput() {
		textField = new JTextField();
		this.add(textField);
		textField.setText(SolrMeterConfiguration.getProperty(property));
		textField.addFocusListener(this);
		
	}

	@Override
	public void focusGained(FocusEvent e) {}

	@Override
	public void focusLost(FocusEvent e) {
		this.notifyObservers();
		
	}

	private void notifyObservers() {
		for(PropertyChangeListener listener:listeners) {
			listener.onPropertyChanged(property, textField.getText());
		}
		
	}
}
