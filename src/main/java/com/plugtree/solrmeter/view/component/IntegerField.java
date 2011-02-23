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

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextField;

/**
 * Component similar to a TextField, but that only accepts numbers and null
 * @author tflobbe
 *
 */
public class IntegerField extends JTextField implements KeyListener {

	private static final long serialVersionUID = -3473941335501520527L;
	
	private String prevValue;
	
	private Integer actualValue;
	
	public IntegerField() {
		super();
		this.addKeyListener(this);
		prevValue = null;
		actualValue = null;
	}
	
	@Override
	public synchronized void keyPressed(KeyEvent arg0) {
		if(this.getText().length() <= 10) {
			prevValue = this.getText();
		}
	}

	@Override
	public synchronized void keyReleased(KeyEvent arg0) {
		if(this.getText() == null || "".equals(this.getText())) {
			actualValue = null;
		}else {
			try {
				Integer auxInteger = Integer.parseInt(this.getText());
				actualValue = auxInteger;
				prevValue = actualValue.toString();
			}catch (NumberFormatException exception) {
				try {
					Long.parseLong(this.getText());//if this works, then the problem is that the number is too big.
					actualValue = Integer.MAX_VALUE;
					prevValue = actualValue.toString();
				} catch (NumberFormatException exceptionLong) {
					//if this happens, there must be a non-number caracter.
				}
				this.setText(prevValue);
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}
	
	public Integer getInteger() {
		return actualValue;
	}
	
	@Override
	public void setText(String t) {
		super.setText(t);
		this.keyPressed(null);
		this.keyReleased(null);
	}

}
