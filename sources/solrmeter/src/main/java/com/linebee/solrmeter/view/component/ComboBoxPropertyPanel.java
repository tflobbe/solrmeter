package com.linebee.solrmeter.view.component;

import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.linebee.solrmeter.model.SolrMeterConfiguration;
import com.linebee.solrmeter.view.listener.PropertyChangeListener;

public class ComboBoxPropertyPanel extends JPanel implements FocusListener {

	
	private static final long serialVersionUID = 1810404353320890282L;
	private static final int paddingLeft = 1;
	private static final int paddingCenter = 1;
	private static final int paddingRight = 1;
	
	private JLabel label;
	protected JComboBox comboBox;
	public JComboBox getComboBox() {
		return comboBox;
	}

	private boolean editable;
	private String property;
	private List<PropertyChangeListener> listeners;
	private String[] values;
	
	
	public ComboBoxPropertyPanel(String text, String property, String[] values, PropertyChangeListener listener) {
		this(text, property, true, values);
		listeners.add(listener);
	}
	
	public ComboBoxPropertyPanel(String text, String property, boolean editable, String[] values, PropertyChangeListener listener) {
		this(text, property, editable, values);
		listeners.add(listener);
	}
	
	
	public ComboBoxPropertyPanel(String text, String property, boolean editable, String[] values) {
		super();
		this.editable = editable;
		this.values = values;
		listeners = new LinkedList<PropertyChangeListener>();
		this.property = property;
		this.initGUI(text);
	}
	
	
	protected void initGUI(String text){
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
//		this.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
		this.add(Box.createRigidArea(new Dimension(paddingLeft, paddingLeft)));
		label = new JLabel(text + ":");
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
//		values = {"true", "false"};
		comboBox = new JComboBox(values);
		this.add(comboBox);
		String currentValue = SolrMeterConfiguration.getProperty(property);
		comboBox.setSelectedIndex(this.getKeyFrom(values, currentValue));
		comboBox.addFocusListener(this);
		
	}
	
	private int getKeyFrom(String[] values, String key){
		for(int i = 0; i< values.length; i++){
			if(values[i].equalsIgnoreCase(key)){
				return i;
			}
		}
		return 0;
	}


	@Override
	public void focusGained(FocusEvent e) {	}

	@Override
	public void focusLost(FocusEvent e) {
		this.notifyObservers();
	}

	private void notifyObservers() {
		for(PropertyChangeListener listener:listeners) {
			listener.onPropertyChanged(property, (String)comboBox.getSelectedItem());
		}
		
	}

}
