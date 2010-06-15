/**
 * Copyright Linebee. www.linebee.com
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
package com.linebee.solrmeter.view;

import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.linebee.solrmeter.controller.SolrMeterMenuController;

public class SolrMeterMenuBar extends MenuBar {
	
	private static final long serialVersionUID = 1L;
	private SolrMeterMenuController controller;

	public SolrMeterMenuBar() {
		super();
		this.initGUI();
		controller = new SolrMeterMenuController();
	}

	private void initGUI() {
		this.add(this.createFileMenu());
		this.add(this.createEditMenu());
		
	}

	private Menu createEditMenu() {
		Menu menu = new Menu();
		menu.setLabel(I18n.get("menu.edit"));
		menu.setName("editMenu");
		menu.add(this.createSettingsMenuItem());
		return menu;
	}

	private MenuItem createSettingsMenuItem() {
		MenuItem item = new MenuItem(I18n.get("menu.edit.settings"));
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.onSettingsMenu();
			}
			
		});
		return item;
	}

	private Menu createFileMenu() {
		Menu menu = new Menu();
		menu.setLabel(I18n.get("menu.file"));
		menu.setName("fileMenu");
		menu.add(this.createImportConfigurationMenuItem());
		menu.add(this.createExportConfigurationMenuItem());
		menu.addSeparator();
		menu.add(this.getMenuItemExit());
		return menu;
	}

	private MenuItem createExportConfigurationMenuItem() {
		MenuItem item = new MenuItem(I18n.get("menu.file.export"));
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.onExportMenu();
			}
			
		});
		return item;
	}

	private MenuItem createImportConfigurationMenuItem() {
		MenuItem item = new MenuItem(I18n.get("menu.file.import"));
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.onImportMenu();
			}
			
		});
		return item;
	}

	private MenuItem getMenuItemExit() {
		MenuItem item = new MenuItem(I18n.get("menu.file.exit"));
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.onExitMenu();
			}
			
		});
		return item;
	}
	
}
