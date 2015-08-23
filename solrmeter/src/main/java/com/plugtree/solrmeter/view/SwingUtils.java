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

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;

public class SwingUtils {

	public static void centerWindow(Window window) {
		Toolkit tk = Toolkit.getDefaultToolkit();
	    Dimension screenSize = tk.getScreenSize();
	    int screenHeight = screenSize.height;
	    int screenWidth = screenSize.width;
	    window.setLocation((int)(screenWidth - window.getSize().getWidth()) / 2, (int)(screenHeight -window.getSize().getHeight()) / 2);
	}
	
	public static String getStackTraceString(Throwable exception) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(exception.getMessage() + "\n");
		for(StackTraceElement element:exception.getStackTrace()) {
			buffer.append("\tat " + element.toString() + "\n");
		}
		if(exception.getCause() != null) {
			buffer.append("\n");
			buffer.append(getStackTraceString(exception.getCause()));
		}
		return buffer.toString();
	}
}
