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

import com.google.inject.Inject;
import com.plugtree.solrmeter.controller.SolrMeterMenuController;
import com.plugtree.stressTestScope.StressTestScope;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@StressTestScope
public class SolrMeterMenuBar extends JMenuBar {
	
	private static final long serialVersionUID = 1L;
	private SolrMeterMenuController controller;

	@Inject
	public SolrMeterMenuBar(SolrMeterMenuController controller) {
		super();
		this.initGUI();
		this.controller = controller;
	}

	private void initGUI() {
		this.add(this.createFileMenu());
        this.add(this.createEditMenu());
        this.add(this.createToolsMenu());

    }

    private JMenu createToolsMenu() {
		JMenu menu = new JMenu(I18n.get("menu.tools"));
		menu.setName("toolsMenu");
		menu.add(this.createExtractFromLogMenuItem());
		return menu;
	}

	private JMenuItem createExtractFromLogMenuItem() {
		JMenuItem item = new JMenuItem(I18n.get("menu.tools.extract"));
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.onExtractMenu();

			}
		});
		return item;
	}

	private JMenu createEditMenu() {
		JMenu menu = new JMenu(I18n.get("menu.edit"));
		menu.setName("editMenu");
		menu.add(this.createRestartTestMenuItem());
		menu.add(this.createSettingsMenuItem());
		return menu;
	}

	private JMenuItem createSettingsMenuItem() {
		JMenuItem item = new JMenuItem(I18n.get("menu.edit.settings"));
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.onSettingsMenu();
			}
			
		});
		return item;
	}

	private JMenu createFileMenu() {
		JMenu menu = new JMenu(I18n.get("menu.file"));
		menu.setName("fileMenu");
		menu.add(this.createImportConfigurationMenuItem());
		menu.add(this.createExportConfigurationMenuItem());
		menu.addSeparator();
		menu.add(this.getMenuItemExit());
		return menu;
	}

	private JMenuItem createRestartTestMenuItem() {
		JMenuItem item = new JMenuItem(I18n.get("menu.edit.restart"));
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.onRestartMenu();
			}
			
		});
		return item;
	}

	private JMenuItem createExportConfigurationMenuItem() {
		JMenuItem item = new JMenuItem(I18n.get("menu.file.export"));
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.onExportMenu();
			}
			
		});
		return item;
	}

	private JMenuItem createImportConfigurationMenuItem() {
		JMenuItem item = new JMenuItem(I18n.get("menu.file.import"));
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.onImportMenu();
			}
			
		});
		return item;
	}

	private JMenuItem getMenuItemExit() {
		JMenuItem item = new JMenuItem(I18n.get("menu.file.exit"));
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.onExitMenu();
			}
			
		});
		return item;
	}
	
}
