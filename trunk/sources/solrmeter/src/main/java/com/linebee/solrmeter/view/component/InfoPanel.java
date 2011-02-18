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

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
/**
 * JPanel that shows a label and a value.
 * @author tflobbe
 *
 */
public class InfoPanel extends JPanel {
	
	private static final long serialVersionUID = 1153426315441180772L;

	private String label;
	
	private String value;
	
	private JLabel jLabelLabel;
	
	private JLabel jLabelValue;
	
	public InfoPanel(String label) {
		this(label, "");
	}
	
	public InfoPanel(String label, String value) {
		super();
		this.label = label;
		this.value = value;
		this.initGUI();
	}
	
	private void initGUI() {
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.setSize(new Dimension(50, 20));
		jLabelLabel = new JLabel(label + ":");
		this.add(jLabelLabel);
		this.add(Box.createHorizontalGlue());
		jLabelValue = new JLabel(value);
		jLabelValue.setToolTipText(value);
		this.add(jLabelValue);
		
	}

	public void setValue(String value) {
		this.value = value;
		jLabelValue.setText(value);
		jLabelValue.setToolTipText(value);
	}

}
