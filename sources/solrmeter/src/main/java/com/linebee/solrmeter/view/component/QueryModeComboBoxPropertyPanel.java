package com.linebee.solrmeter.view.component;

import static com.linebee.solrmeter.QueryModeParam.EXTERNAL;
import static com.linebee.solrmeter.QueryModeParam.STANDARD;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import com.linebee.solrmeter.view.component.ComboBoxPropertyPanel;
import com.linebee.solrmeter.view.component.PropertyPanel;
import com.linebee.solrmeter.view.listener.PropertyChangeListener;

public class QueryModeComboBoxPropertyPanel extends ComboBoxPropertyPanel {

	private static final long serialVersionUID = 450290036767193606L;
	private static String[] values = {STANDARD, EXTERNAL };

	public QueryModeComboBoxPropertyPanel(String text, String property,
			boolean editable, String[] values, PropertyChangeListener listener) {
		super(text, property, editable, values, listener);
	}

	public QueryModeComboBoxPropertyPanel(String text, String property,	boolean editable, PropertyChangeListener listener) {
		this(text, property, editable, values , listener);
	}

	public void initGUI(final List<PropertyPanel> propertyPanels) {
		comboBox.addActionListener(new IsVisibleActionListener(propertyPanels));
		comboBox.setSelectedIndex(comboBox.getSelectedIndex());
	}

	

	private final class IsVisibleActionListener implements ActionListener {
		private final List<PropertyPanel> propertyPanels;

		private IsVisibleActionListener(List<PropertyPanel> propertyPanels) {
			this.propertyPanels = propertyPanels;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			String selection = comboBox.getSelectedItem().toString();
			this.displayElementBy(selection);
		}

		private void displayElementBy(String selection) {
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
	}
	
}
