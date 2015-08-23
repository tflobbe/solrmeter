package com.plugtree.solrmeter.view.component;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class TooltipJTable extends JTable {
	
	private static final long serialVersionUID = 1L;
	
	private static final int MIN_CONTENT_LENGTH=20;

	@Override
	public Component prepareRenderer(TableCellRenderer renderer, int row,
			int column) {
		Component comp = super.prepareRenderer(renderer, row, column);
	    JComponent jcomp = (JComponent)comp;
	    if (comp == jcomp) {
	    	Object value = getValueAt(row, column);
	    	if(value == null || value.toString().length() < MIN_CONTENT_LENGTH) {
	    		jcomp.setToolTipText("");
	    	} else {
	    		jcomp.setToolTipText(value.toString());
	    	}
	        
	    }
	    return comp;
	}

}
