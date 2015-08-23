package com.plugtree.solrmeter.view.component;

import com.plugtree.solrmeter.model.SolrMeterConfiguration;
import com.plugtree.solrmeter.view.listener.PropertyChangeListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import static com.plugtree.solrmeter.QueryModeParam.*;

public class QueryModeComboBoxPropertyPanel extends ComboPropertyPanel {

	private static final long serialVersionUID = 450290036767193606L;
	private static final String[] VALUES = {STANDARD, EXTERNAL };
	
	public QueryModeComboBoxPropertyPanel(String text, String property,
			boolean editable, PropertyChangeListener listener) {
		super(text, property, editable, listener, VALUES, false);
	}

	public void setDependantProperties(final List<PropertyPanel> propertyPanels) {
		if(editable) {
			comboBox.addActionListener(new IsVisibleActionListener(propertyPanels));
			comboBox.setSelectedIndex(comboBox.getSelectedIndex());
		} else {
			evaluateChildPropertiesVisibility(SolrMeterConfiguration.getProperty(property), propertyPanels);
		}
	}
	
	private void evaluateChildPropertiesVisibility(String selection, final List<PropertyPanel> propertyPanels) {
		if(selection.equals(STANDARD)){
			setVisible(propertyPanels, true);
		}
		if(selection.equals(EXTERNAL)){
			setVisible(propertyPanels, false);
		}
	}
	
	private void setVisible(final List<PropertyPanel> propertyPanels, boolean status) {
		for (PropertyPanel propertyPanel : propertyPanels) {
			propertyPanel.setVisible(status);
		}
	}

	private final class IsVisibleActionListener implements ActionListener {
		private final List<PropertyPanel> propertyPanels;

		private IsVisibleActionListener(List<PropertyPanel> propertyPanels) {
			this.propertyPanels = propertyPanels;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			evaluateChildPropertiesVisibility(comboBox.getSelectedItem().toString(), propertyPanels);
		}

	}
	
}
