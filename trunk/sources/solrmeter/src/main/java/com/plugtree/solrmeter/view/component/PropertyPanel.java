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
import com.plugtree.solrmeter.view.I18n;
import com.plugtree.solrmeter.view.SolrPropertyObserver;
import com.plugtree.solrmeter.view.listener.PropertyChangeListener;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.LinkedList;
import java.util.List;
/**
 * 
 * TODO add validators
 * @author tflobbe
 *
 */
public abstract class PropertyPanel extends JPanel implements FocusListener, SolrPropertyObserver {

	private static final long serialVersionUID = -6531190706130757263L;
	
	protected static final int MAX_COMPONENT_WIDTH = 250;
	protected static final int LABEL_WIDTH = 200;
	
	private static final int paddingLeft = 2;
	private static final int paddingCenter = 2;
	private static final int paddingRight = 2;

	private JLabel label;
	
	private List<PropertyChangeListener> listeners;
	
	protected String property;
	
	protected boolean editable;
	
	private JLabel propertyValue;
	
	private boolean guiInitialized = false;
	
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
		this.editable = (editable && System.getProperty(property) == null);// system properties can't be modified on runtime
		this.listeners = new LinkedList<PropertyChangeListener>();
		this.property = property;
	}

	protected void initGUI(String text) {
		if(guiInitialized) {
			throw new RuntimeException("initGUI is being called twice!");
		}
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
		this.add(Box.createRigidArea(new Dimension(paddingLeft, paddingLeft)));
		this.label = new JLabel(text + ":");
		label.setPreferredSize(new Dimension(LABEL_WIDTH, 0));
		this.add(label);
		this.add(Box.createRigidArea(new Dimension(paddingCenter, paddingCenter)));
		if(editable) {
			this.createInput();
		}else {
			propertyValue = new JLabel(SolrMeterConfiguration.getProperty(property)); 
			this.add(propertyValue);
			if(System.getProperty(property) != null) {
				propertyValue.setToolTipText(I18n.get("propertyPanel.toolTipText.blockedBySystemProperty"));
			}
		}
		this.add(Box.createRigidArea(new Dimension(paddingRight, paddingRight)));
		setBorder(this.createBorder());
		guiInitialized = true;
	}

	protected void createInput() {
		this.add(getVisualComponent());
	}

	@Override
	public void focusGained(FocusEvent e) {}

	@Override
	public void focusLost(FocusEvent e) {
		this.notifyObservers();
		
	}

	protected void notifyObservers() {
		for(PropertyChangeListener listener:listeners) {
			listener.onPropertyChanged(property, getSelectedValue());
		}
	}
	
	@Override
	public void paint(Graphics g) {
		if(!guiInitialized) {
			throw new RuntimeException("GUI was not initialized");
		}
		super.paint(g);
	}
	
	protected Border createBorder() {
		return BorderFactory.createEmptyBorder(2, 2, 2, 2);
	}
	
	public void solrPropertyChanged(String prop, String value){
		if(prop.equals(this.property)){
			this.setSelectedValue(value);
		}
	}
	
	public String getPropertyName(){
		return this.property;
	}

    
	
	protected abstract String getSelectedValue();
	
	protected abstract void setSelectedValue(String value);
	
	protected abstract Component getVisualComponent();

}
