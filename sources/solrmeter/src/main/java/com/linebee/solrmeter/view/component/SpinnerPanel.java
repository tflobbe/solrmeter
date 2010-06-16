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
import java.util.LinkedList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SpinnerPanel extends JPanel {
	
	private static final long serialVersionUID = 8355604724298546390L;

	private JSpinner spinner;
	
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
		spinner = new JSpinner(new SpinnerNumberModel(initNumber, minNumber, maxNumber, stepSize));
		((JSpinner.DefaultEditor)spinner.getEditor()).getTextField().setEditable(false);
		spinner.setSize(new Dimension(20, 20));
		spinner.setMaximumSize(new Dimension(40, 20));
		spinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				notifyChangeListeners(e);
			}
		});
		this.add(new JLabel(title));
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
}
