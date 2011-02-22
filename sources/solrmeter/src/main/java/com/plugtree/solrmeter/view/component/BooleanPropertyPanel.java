package com.plugtree.solrmeter.view.component;

import com.plugtree.solrmeter.view.listener.PropertyChangeListener;

public class BooleanPropertyPanel extends ComboPropertyPanel {

	private static final long serialVersionUID = 8711126626453372582L;

	public BooleanPropertyPanel(String text, String property, boolean editable,
			PropertyChangeListener listener) {
		super(text, property, editable, listener, new String[]{"true", "false"}, false);
	}

	public BooleanPropertyPanel(String text, String property, boolean editable) {
		super(text, property, editable, new String[]{"true", "false"}, false);
	}

	public BooleanPropertyPanel(String text, String property,
			PropertyChangeListener listener) {
		super(text, property, listener, new String[]{"true", "false"}, false);
	}

}
