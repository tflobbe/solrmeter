package com.linebee.solrmeter.view.component;

import java.awt.Component;

import javax.swing.JTextField;

import com.linebee.solrmeter.model.SolrMeterConfiguration;
import com.linebee.solrmeter.view.listener.PropertyChangeListener;

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
		textField.setText(SolrMeterConfiguration.getProperty(property));
		textField.addFocusListener(this);
//		textField.setMaximumSize(new Dimension(MAX_COMPONENT_WIDTH - 50, Integer.MAX_VALUE));
		return textField;
	}

}
