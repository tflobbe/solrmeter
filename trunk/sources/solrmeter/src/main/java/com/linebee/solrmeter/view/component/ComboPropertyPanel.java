package com.linebee.solrmeter.view.component;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

import com.linebee.solrmeter.model.SolrMeterConfiguration;
import com.linebee.solrmeter.view.listener.PropertyChangeListener;

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
			comboBox.setSelectedItem(SolrMeterConfiguration.getProperty(property));
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

}
