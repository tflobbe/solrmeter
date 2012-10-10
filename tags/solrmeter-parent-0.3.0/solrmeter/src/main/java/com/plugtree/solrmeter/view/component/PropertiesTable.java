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

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.swing.DefaultCellEditor;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

import com.plugtree.solrmeter.model.SolrMeterConfiguration;
import com.plugtree.solrmeter.view.I18n;
import com.plugtree.solrmeter.view.SolrPropertyObserver;
import com.plugtree.solrmeter.view.listener.PropertyChangeListener;

public class PropertiesTable extends JTable implements SolrPropertyObserver, Iterable<String>{

	private static final long serialVersionUID = -7064199936721464923L;
	
	private List<PropertyChangeListener> propListeners;
	
	private PropertiesTableModel model;
	
	private boolean canAdd;
	
	public boolean isSorted() {
		return this.model.sorted;
	}

	public void setSorted(boolean sorted) {
		this.model.sorted = sorted;
	}

	public PropertiesTable(PropertyChangeListener l, boolean canAdd, Map<String, String> values){
		super();
		this.canAdd = canAdd;
		
		if(values != null){
			model = new PropertiesTableModel(this.canAdd, values);
		} else {
			model = new PropertiesTableModel(this.canAdd);
		}
		
		this.addKeyListener(new ProperitesTableKeyListener(this));
		
		model.addPropertyChangeListener(l);
		this.setModel(model);
	
		this.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
			private static final long serialVersionUID = 3979746239295239039L;
			
			@Override
		    public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column){
				JLabel c = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				if(column == 0){
					Font f = c.getFont();
					if(row < model.keys.size()){
						c.setFont(f.deriveFont(Font.BOLD));
					} else {
						c.setFont(f.deriveFont(Font.ITALIC));
					}					
				}

				return c;
			}			
		});
		
		DefaultCellEditor cellEditorProperty = new DefaultCellEditor(new JTextField()){
			
			private static final long serialVersionUID = 1L;

			@Override
		    public Component getTableCellEditorComponent(JTable table, Object value,
                    boolean isSelected,
                    int row, int column){
				return super.getTableCellEditorComponent(table, "", isSelected, row, column);
			}
		};
		cellEditorProperty.setClickCountToStart(1);
		
		((JComponent)cellEditorProperty.getComponent()).setBorder(new LineBorder(Color.black));
		this.getColumnModel().getColumn(0).setCellEditor(cellEditorProperty);
		
		DefaultCellEditor cellEditorValue = new DefaultCellEditor(new JTextField());
		((JComponent)cellEditorValue.getComponent()).setBorder(new LineBorder(Color.black));
		cellEditorValue.setClickCountToStart(1);
		this.getColumnModel().getColumn(1).setCellEditor(cellEditorValue);

	}
	
	public PropertiesTable(PropertyChangeListener l){
		this(l, true, null);
	}
	
	public PropertiesTable(PropertyChangeListener l, Map<String, String> values){
		this(l, true, values);
	}
	
	protected class PropertiesTableModel extends AbstractTableModel implements TableModelListener{
		
		private static final long serialVersionUID = -2045687583721284694L;
		
		private List<String> keys;
		private Map<String, String> values;
		
		private boolean canAdd;
		
		private boolean sorted = true;
		
		private String newText;
		
		public PropertiesTableModel(boolean canAdd, Map<String, String> v){
			this.canAdd = canAdd;
			
			newText = I18n.get("propertiesTable.newText");
			
			keys = new ArrayList<String>(v.size());
			
			for(String key:v.keySet()){
				keys.add(key);
			}
			
			sortKeys();
			
			values= new HashMap<String, String>(keys.size());
			
			for(String key: keys){
				values.put(key, v.get(key));
			}
			
			this.addTableModelListener(this);
		}

		
		public PropertiesTableModel(boolean canAdd){
			this.canAdd = canAdd;
			
			newText = I18n.get("propertiesTable.newText");
			
			keys = SolrMeterConfiguration.getKeys(Pattern.compile(".*"));
			sortKeys();
			
			values= new HashMap<String, String>(keys.size());
			
			for(String key: keys){
				values.put(key, SolrMeterConfiguration.getProperty(key));
			}
			
			this.addTableModelListener(this);
		}
		
		private void sortKeys(){
			if(this.sorted){
				Collections.sort(keys);
			}
		}

		@Override
		public int getRowCount() {
			int extraRow = this.canAdd?1:0;
			return keys.size() + extraRow;
		}

		@Override
		public int getColumnCount() {
			return 2;
		}
		
		@Override
	    public String getColumnName(int column) {
	        switch(column){
	        	case 0:
	        		return I18n.get("settings.advanced.table.property");
	        	case 1:
	        		return I18n.get("settings.advanced.table.value");
	        	default:
	        		return "";        		
	        }
	    }
		
		@Override
	    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			if(this.canAdd && columnIndex==0 && rowIndex == keys.size()){
				if(((String)aValue).isEmpty()){
					return;
				}
				this.keys.add((String)aValue);
	    		fireTableCellUpdated(rowIndex, columnIndex);
	    		
			} else if(columnIndex == 1){
	    		values.put(keys.get(rowIndex), (String)aValue);
	    		fireTableCellUpdated(rowIndex, columnIndex);
	    	}
	    }
		
		@Override
	    public boolean isCellEditable(int rowIndex, int columnIndex) {
			if(this.canAdd && rowIndex == keys.size()){
				return columnIndex == 0;
			} else {
		        return columnIndex == 1;
			}
	    }

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if(this.canAdd && rowIndex == keys.size()){
				if(columnIndex == 0){
					return newText;
				} else {
					return "";
				}				
			}
			
			switch (columnIndex) {
			case 0:
				return keys.get(rowIndex);
			case 1:
				return values.get(keys.get(rowIndex));
			default:
				return null;

			}
		}

		@Override
		public void tableChanged(TableModelEvent e) {
	        int column = e.getColumn();
	        int row = e.getFirstRow();

	        if(column == 1 || (canAdd && column == 0 && row == keys.size() - 1)){
	        	TableModel model = (TableModel)e.getSource();
	        	String propName = (String) model.getValueAt(row, 0);
	        	String propValue = (String) model.getValueAt(row, 1);
	        	
	        	if(propValue == null){
	        		propValue = "";
	        	}
	        	
	        	notifyListeners(propName, propValue);
	        	return;
	        }
	        
	      //Deleted column
	        if(e.getType() == TableModelEvent.DELETE && e.getColumn() == TableModelEvent.ALL_COLUMNS){
	        	TableModel model = (TableModel)e.getSource();
	        	String propName = (String) model.getValueAt(row, 0);
	        	
	    		if(row >= this.keys.size()){
					return;
				}
				
				String key = this.keys.get(row);
				this.values.remove(key);
				this.keys.remove(row);
				
				notifyListeners(propName, null);
	        }	        
		}
		
		public void addPropertyChangeListener(PropertyChangeListener l){
			if(propListeners == null){
				propListeners = new ArrayList<PropertyChangeListener>();
			}
			
			propListeners.add(l);
		}
		
		protected void notifyListeners(String propertyChanged, String value){
			for(PropertyChangeListener l: propListeners){
				l.onPropertyChanged(propertyChanged, value);
			}
		}

	}

	private class ProperitesTableKeyListener implements KeyListener {

		private PropertiesTable parent;
		
		public ProperitesTableKeyListener(PropertiesTable parent){
			this.parent = parent;
		}
		
		@Override
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_DELETE){
				parent.deleteSelected();
			}			
		}

		@Override
		public void keyReleased(KeyEvent e) {}

		@Override
		public void keyTyped(KeyEvent e) {}
		
	}
	
	
	@Override
	public void solrPropertyChanged(String prop, String value) {
		this.setProperty(prop, value);
	}
	
	protected void deleteSelected() {
		int row = this.getSelectedRow();
		if (row >= 0) {
			this.model.fireTableRowsDeleted(row, row);
		}
	}

	public void removeAll(){
		model.values.clear();
		model.keys.clear();
		this.tableChanged(new TableModelEvent(model));		
	}
	
	public void setProperty(String prop, String value) {
		if (model.values.get(prop) == null) {
			model.keys.add(prop);
		}
		model.values.put(prop, value);

		int row = 0;
		for (String property : model.keys) {
			if (property.equals(prop))
				break;
			row++;
		}
		TableModelEvent e = new TableModelEvent(model, row, row, 1);
		this.tableChanged(e);
	}
	
	public Map<String, String> getProperties(){
		Map<String, String> propMap = new HashMap<String, String>();
		for(String key: model.keys){
			propMap.put(key, model.values.get(key));
		}
		return propMap;
	}
	
	public List<String> getPropertiesNames(){
		List<String> l = new ArrayList<String>(model.keys.size());
		for(String s: model.keys){
			l.add(s);
		}
		return l;
	}
	
	@Override
	public Iterator<String> iterator() {
		return model.keys.iterator();
	}
}
