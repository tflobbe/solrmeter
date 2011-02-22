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
package com.plugtree.solrmeter.view.settings;

import javax.swing.Box;
import javax.swing.BoxLayout;

import com.plugtree.solrmeter.controller.SettingsController;
import com.plugtree.solrmeter.view.I18n;
import com.plugtree.solrmeter.view.SettingsPanel;
import com.plugtree.solrmeter.view.component.ComboPropertyPanel;
import com.plugtree.solrmeter.view.exception.InvalidPropertyException;
import com.plugtree.solrmeter.view.listener.PropertyChangeListener;
/**
 * Settings panel for optimize operation settings
 * @author tflobbe
 *
 */
public class OptimizeSettingsPanel extends SettingsPanel implements PropertyChangeListener {

	private static final long serialVersionUID = 7691155409986605585L;
	private boolean editable;

	@Override
	public String getSettingsName() {
		return I18n.get("settings.optimize.title");
	}
	
	public OptimizeSettingsPanel(SettingsController controller, boolean editable) {
		super(controller);
		this.editable = editable;
		this.initGUI();
	}

	private void initGUI() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(new ComboPropertyPanel(I18n.get("settings.optimize.optimizeExecutor"), "executor.optimizeExecutor", editable, this, new String[]{"ondemand"}, true));
		this.add(Box.createVerticalGlue());
	}

	@Override
	public void onPropertyChanged(String property, String text)
			throws InvalidPropertyException {
		controller.setProperty(property, text);
	}

}
