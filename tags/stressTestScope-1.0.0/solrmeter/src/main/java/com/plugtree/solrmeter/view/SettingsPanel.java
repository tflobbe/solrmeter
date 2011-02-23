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
package com.plugtree.solrmeter.view;

import java.awt.Component;

import javax.swing.JPanel;

import com.plugtree.solrmeter.controller.SettingsController;
import com.plugtree.solrmeter.view.component.PropertyPanel;

public abstract class SettingsPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	protected SettingsController controller;
	
	public SettingsPanel(SettingsController controller){
		this.controller = controller;
	}
	
	public Component add(PropertyPanel comp){
		this.controller.addPropertyObserver(comp.getPropertyName(), comp);
		return super.add(comp);
	}
	
	public abstract String getSettingsName();

}
