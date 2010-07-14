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

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;

public abstract class RoundedBorderJPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;

	private static final Color DEFAULT_COLOR = new Color(0,150,255);
	
	private static final boolean DO_ROUND_DEFAULT = true;
	
	private static final int DEFAULT_RADIUS = 10;
	
	private int radius;
	
	
	public RoundedBorderJPanel(){
		super();
		radius = DEFAULT_RADIUS;
		this.createBorder("", DEFAULT_COLOR, DO_ROUND_DEFAULT);
	}
	
	public RoundedBorderJPanel(String title){
		super();
		radius = DEFAULT_RADIUS;
		this.createBorder(title, DEFAULT_COLOR, DO_ROUND_DEFAULT);
	}
	
	public RoundedBorderJPanel(String title, Color color, boolean rounded){
		super();
		radius = DEFAULT_RADIUS;
		this.createBorder(title, color, rounded);
	}
	
	public RoundedBorderJPanel(String title, Color color, int radius){
		super();
		this.radius = radius;
		this.createBorder(title, color, true);
	}
	
	public RoundedBorderJPanel(String title, boolean rounded){
		super();
		radius = DEFAULT_RADIUS;
		this.createBorder(title, DEFAULT_COLOR, rounded);
	}
	
	private void createBorder(String title, Color color, boolean rounded) {
		Border border;
		if(rounded)
			border = new RoundedBorder(radius, color);
		else
			border = BorderFactory.createLineBorder(color);
		this.setBorder(BorderFactory.createTitledBorder(border, title));
	}

}
