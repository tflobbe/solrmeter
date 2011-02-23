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

import java.awt.Component;
import java.awt.Dimension;
import java.util.Collection;
import java.util.LinkedList;

import javax.swing.JPanel;

import org.apache.log4j.Logger;

public class TwoColumnsPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;

	private Collection<TwoColumns> components;
	
	private int firstColumnWidth;
	
	public TwoColumnsPanel() {
		components = new LinkedList<TwoColumns>();
		firstColumnWidth = 0;
	}
	
	@Override
	public Component add(Component comp) {
		newComponent(comp);
		return super.add(comp);
	}
	
	@Override
	public Component add(Component comp, int index) {
		newComponent(comp);
		return super.add(comp, index);
	}
	
	@Override
	public void add(Component comp, Object constraints) {
		newComponent(comp);
		super.add(comp, constraints);
	}
	
	@Override
	public void add(Component comp, Object constraints, int index) {
		newComponent(comp);
		super.add(comp, constraints, index);
	}
	
	@Override
	public Component add(String name, Component comp) {
		newComponent(comp);
		return super.add(name, comp);
	}
	
	private void newComponent(Component comp) {
		if(TwoColumns.class.isAssignableFrom(comp.getClass())) {
			TwoColumns twoColumns = (TwoColumns) comp;
			components.add(twoColumns);
			
			if(twoColumns.getFirstColumnWidth()>firstColumnWidth) {
				firstColumnWidth = twoColumns.getFirstColumnWidth();
				adjust();
			} else {
				twoColumns.setFirstColumnWidth(firstColumnWidth);
			}
			validate();
		}
	}
	
	private void adjust() {
		for(TwoColumns c: components) {
			c.setFirstColumnWidth(firstColumnWidth);
		}
	}
	
	@Override
	public Dimension getMinimumSize() {
		int minimumWidth = 0;
		for(Component c: getComponents()) {
			minimumWidth = Math.max(minimumWidth, c.getMinimumSize().width);
		}
		Logger.getLogger(getClass()).debug(minimumWidth);
		return new Dimension(minimumWidth, super.getMinimumSize().height);
	}
	
	@Override
	public Dimension getMaximumSize() {
		Dimension d = new Dimension();
		d.width = Integer.MAX_VALUE;
		d.height = Integer.MAX_VALUE;
		return d;
	}

}
