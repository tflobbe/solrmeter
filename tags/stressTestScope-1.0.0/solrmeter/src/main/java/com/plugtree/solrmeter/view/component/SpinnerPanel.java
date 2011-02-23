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

import java.awt.Dimension;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

public class SpinnerPanel extends JPanel implements TwoColumns, Row {
	
	private static final Logger log = Logger.getLogger(SpinnerPanel.class);
	
	private static final long serialVersionUID = 8355604724298546390L;
	
	private static final int VERTICAL_MARGIN = 3;
	private static final int HORIZONTAL_MARGIN = 2;

	private JSpinner spinner;
	
	private JLabel jLabelTitle;
	
	private int initNumber = 1;
	
	private int minNumber = 1;
	
	private int maxNumber = Integer.MAX_VALUE;
	
	private int stepSize = 1;
	
	private String title;
	
	private List<ChangeListener> listeners;
	
	public SpinnerPanel(int initNumber, int minNumber, int maxNumber,
			int stepSize, String title) {
		super();
		listeners = new LinkedList<ChangeListener>();
		this.initNumber = initNumber;
		this.minNumber = minNumber;
		this.maxNumber = maxNumber;
		this.stepSize = stepSize;
		this.title = title;
		this.initGUI();
	}

	public SpinnerPanel(int initNumber, String title) {
		super();
		listeners = new LinkedList<ChangeListener>();
		this.initNumber = initNumber;
		this.title = title;
		this.initGUI();
	}



	private void initGUI() {
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.setBorder(BorderFactory.createEmptyBorder(VERTICAL_MARGIN, HORIZONTAL_MARGIN, VERTICAL_MARGIN, HORIZONTAL_MARGIN));
		
		jLabelTitle = new JLabel(title + ":");
		
		spinner = new JSpinner(new SpinnerNumberModel(initNumber, minNumber, maxNumber, stepSize));
		spinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				notifyChangeListeners(e);
			}
		});
		
		try {
			JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor)spinner.getEditor();
			editor.getTextField().setEditable(false);
			editor.getTextField().setColumns(5);
		} catch(ClassCastException ex) {
			log.warn("Unexpected JSpinner editor class: " + spinner.getEditor().getClass().getName());
		}
		
		// limit the height of the spinner to the minimum
		spinner.setMaximumSize(new Dimension(spinner.getMaximumSize().width, spinner.getMinimumSize().height));
		// let the spinner shrink
		spinner.setMinimumSize(new Dimension(10, spinner.getMinimumSize().height));
		
		this.add(jLabelTitle);
		this.add(Box.createHorizontalStrut(TwoColumns.GAP));
		this.add(Box.createHorizontalGlue());
		this.add(spinner);
	}
	
	public void addChangeListener(ChangeListener listener) {
		listeners.add(listener);
	}
	
	private void notifyChangeListeners(ChangeEvent event) {
		for(ChangeListener listener:listeners) {
			listener.stateChanged(event);
		}
		
	}

	public Integer getValue() {
		return (Integer)spinner.getValue();
	}
	
	public void setValue(Integer value) {
		spinner.setValue(value);
	}
	
	@Override
	public int getFirstColumnWidth() {
		return jLabelTitle.getMinimumSize().width;
	}
	
	@Override
	public void setFirstColumnWidth(int width) {
		Dimension d = (Dimension)jLabelTitle.getMinimumSize().clone();
		d.width = width;
		jLabelTitle.setMinimumSize(d);
		jLabelTitle.setPreferredSize(d);
		jLabelTitle.setMaximumSize(d);
	}
	
	@Override
	public Dimension getMaximumSize() {
		Dimension d = new Dimension();
		d.width = super.getMaximumSize().width;
		d.height = Math.max(jLabelTitle.getMinimumSize().height, spinner.getMinimumSize().height) + 2*VERTICAL_MARGIN;
		return d;
	}
}
