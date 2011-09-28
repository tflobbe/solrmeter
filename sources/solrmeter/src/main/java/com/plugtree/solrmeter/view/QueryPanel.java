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
import com.plugtree.solrmeter.controller.QueryPanelController;
import com.plugtree.solrmeter.model.exception.QueryException;
import com.plugtree.solrmeter.view.component.IntegerField;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

/**
 * Panel to query Solr
 * @author tflobbe
 *
 */
public class QueryPanel extends StatisticPanel {
  
  private static final long serialVersionUID = 2354841710137826515L;
  
  private final static int INPUT_PANEL_WIDTH = 250;
  
  private final static int OUTPUT_SPLIT_LOCATION = 90;
  
  private JTextField jTextFieldQ;
  
  private JTextField jTextFieldFQ;
  
  private JTextField jTextFieldFacetFields;
  
  private JTextField jTextFieldQT;
  
  private JTextField jTextFieldSort;
  
  private JTextField jTextFieldOtherParams;
  
  private JComboBox jComboSortOrder;
  
  private IntegerField integerFieldRows;
  
  private IntegerField integerFieldStart;
  
  private JCheckBox jCheckBoxHighlight;
  
  private QueryPanelController controller;
  
  private JLabel jLabelQueryTime;
  
  private JLabel jLabelResultsFound;
  
  private JList jListFacets;
  
  private JTable jTableResults;
  
  private QueryResultsTableModel resultsTableModel;
  
  private JSplitPane outputPanel;
  
  private JButton jButtonSearch;
  
  private JTextArea jTextAreaError;
  
  private JScrollPane jScrollPaneError;
  
  @Inject
  public QueryPanel(QueryPanelController controller) {
    super();
    this.initGUI();
    this.controller = controller;
    controller.setView(this);
  }
  
  private void initGUI() {
    this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
    this.add(this.createPanel());
  }
  
  private Component createPanel(){
    return new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, this.createInputPanel(), this.createRightOutputPanel());
  }
  
  private Component createRightOutputPanel(){
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
    panel.add(this.createOutputPanel());
    panel.add(this.createErrorLabel());
    return panel;
    
  }
  
  private Component createErrorLabel() {
    jTextAreaError = new JTextArea();
    jTextAreaError.setEditable(false);
    jTextAreaError.setOpaque(false);
    jTextAreaError.setBorder(null);
    jTextAreaError.setFont(new Font(jTextAreaError.getFont().getName(), jTextAreaError.getFont().getStyle(), 12));
    jScrollPaneError = new JScrollPane(jTextAreaError);
    jScrollPaneError.setVisible(false);
    return jScrollPaneError;
  }
  
  private Component createInputPanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new GridBagLayout());
    panel.setMaximumSize(new Dimension(INPUT_PANEL_WIDTH, Integer.MAX_VALUE));
    panel.setMinimumSize(new Dimension(INPUT_PANEL_WIDTH, 1));
    panel.setPreferredSize(new Dimension(INPUT_PANEL_WIDTH, 100));
    GridBagConstraints constraintsLabels = createConstraints();
    GridBagConstraints constraintsInputs = createConstraints();
    constraintsInputs.gridx = 1;
    constraintsInputs.weightx = 1.5;
    
    panel.add(createLabel("query"),constraintsLabels);
    jTextFieldQ = createTextField();
    panel.add(jTextFieldQ, constraintsInputs);
    
    incrementConstraints(constraintsLabels,constraintsInputs);
    
    panel.add(createLabel("filterQuery"), constraintsLabels);
    jTextFieldFQ = createTextField();
    panel.add(jTextFieldFQ, constraintsInputs);
    
    incrementConstraints(constraintsLabels,constraintsInputs);
    
    panel.add(createLabel("facetFields"), constraintsLabels);
    jTextFieldFacetFields = createTextField();
    panel.add(jTextFieldFacetFields, constraintsInputs);
    
    incrementConstraints(constraintsLabels,constraintsInputs);
    
    panel.add(createLabel("queryType"), constraintsLabels);
    jTextFieldQT = createTextField();
    panel.add(jTextFieldQT, constraintsInputs);
    
    incrementConstraints(constraintsLabels,constraintsInputs);
    
    panel.add(createLabel("sort"), constraintsLabels);
    panel.add(this.createSortInputPanel(), constraintsInputs);
    
    incrementConstraints(constraintsLabels,constraintsInputs);
    
    panel.add(createLabel("otherParams"), constraintsLabels);
    jTextFieldOtherParams = createTextField();
    panel.add(jTextFieldOtherParams, constraintsInputs);
    
    incrementConstraints(constraintsLabels,constraintsInputs);
    
    panel.add(createLabel("rows"), constraintsLabels);
    integerFieldRows = createIntegerField();
    panel.add(integerFieldRows, constraintsInputs);
    
    incrementConstraints(constraintsLabels,constraintsInputs);
    
    panel.add(createLabel("start"), constraintsLabels);
    integerFieldStart = createIntegerField();
    panel.add(integerFieldStart, constraintsInputs);
    
    incrementConstraints(constraintsLabels,constraintsInputs);
    constraintsLabels.gridwidth = 2;
    jCheckBoxHighlight = new JCheckBox(I18n.get("queryPanel.highlight"));
    panel.add(jCheckBoxHighlight, constraintsLabels);
    
    incrementConstraints(constraintsLabels,constraintsInputs);
    jButtonSearch = new JButton(I18n.get("queryPanel.search"));
    jButtonSearch.addActionListener(new ExecuteQueryActionListener());
    constraintsLabels.fill = GridBagConstraints.NONE;
    panel.add(jButtonSearch, constraintsLabels);
    
    incrementConstraints(constraintsLabels,constraintsInputs);
    constraintsLabels.weighty = 2.0;
    panel.add(Box.createGlue(), constraintsLabels);
    return panel;
  }
  
  private IntegerField createIntegerField() {
    IntegerField integerField = new IntegerField();
    integerField.addActionListener(new ExecuteQueryActionListener());
    return integerField;
  }
  
  private JTextField createTextField() {
    JTextField textField = new JTextField();
    textField.addActionListener(new ExecuteQueryActionListener());
    return textField;
  }
  
  private void incrementConstraints(GridBagConstraints constraints1,
      GridBagConstraints constraints2) {
    constraints1.gridy = constraints2.gridy = constraints2.gridy+1;
  }
  
  private GridBagConstraints createConstraints() {
    GridBagConstraints constraints = new GridBagConstraints();
    constraints.fill = GridBagConstraints.HORIZONTAL;
    constraints.insets = new Insets(2, 2, 2, 2);
    constraints.gridx = 0;
    constraints.gridy = 0;
    return constraints;
  }
  
  private Component createSortInputPanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
    jTextFieldSort = createTextField();
    panel.add(jTextFieldSort);
    jComboSortOrder = new JComboBox(this.createSortOrderListModel());
    jComboSortOrder.setEditable(false);
    
    panel.add(jComboSortOrder);
    return panel;
  }
  
  private ComboBoxModel createSortOrderListModel() {
    DefaultComboBoxModel model = new DefaultComboBoxModel();
    model.addElement(ORDER.asc);
    model.addElement(ORDER.desc);
    return model;
  }
  
  private Component createOutputPanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new GridBagLayout());
    GridBagConstraints constraints = new GridBagConstraints();
    constraints.fill = GridBagConstraints.BOTH;
    constraints.gridx = 0;
    constraints.gridy = 0;
    panel.add(this.createQueryInfoPanel(), constraints);
    
    constraints.gridx = 1;
    constraints.gridy = 0;
    constraints.weightx = 2.0;
    constraints.weighty = 2.0;
    panel.add(this.createFacetPanel(), constraints);
    
    outputPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, panel, this.createResultsPanel());
    outputPanel.setDividerLocation(OUTPUT_SPLIT_LOCATION);
    return outputPanel;
  }
  
  private Component createResultsPanel() {
    jTableResults = new JTable();
    resultsTableModel = new QueryResultsTableModel();
    jTableResults.setModel(resultsTableModel);
    return new JScrollPane(jTableResults);
  }
  
  private Component createFacetPanel() {
    jListFacets = new JList();
    jListFacets.addMouseListener(new MouseListener() {
      
      @Override
      public void mouseClicked(MouseEvent e) {
        if(e.getClickCount() == 2) {
          String text = jTextFieldFQ.getText();
          if(!text.equals("")) {
            jTextFieldFQ.setText(text + ", " + getFq());
          }else {
            jTextFieldFQ.setText(getFq());
          }
        }
      }
      
      private String getFq() {
        return ((String)jListFacets.getSelectedValue()).substring(0, ((String)jListFacets.getSelectedValue()).lastIndexOf("("));
      }
      
      @Override
      public void mouseEntered(MouseEvent e) {}
      
      @Override
      public void mouseExited(MouseEvent e) {}
      
      @Override
      public void mousePressed(MouseEvent e) {}
      
      @Override
      public void mouseReleased(MouseEvent e) {}
      
    });
    return new JScrollPane(jListFacets);
  }
  
  private Component createQueryInfoPanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    jLabelQueryTime = createLabel("queryTime");
    panel.add(jLabelQueryTime);
    
    jLabelResultsFound = createLabel("resultsFound");
    panel.add(jLabelResultsFound);
    return panel;
  }
  
  @Override
  public String getStatisticName() {
    return I18n.get("queryPanel.title");
  }
  
  @Override
  public void refreshView() {
    //nothing
  }
  
  private JLabel createLabel(String key) {
    return new JLabel(I18n.get("queryPanel." + key));
  }
  
  public void showResults(QueryResponse response) {
    jScrollPaneError.setVisible(false);
    outputPanel.setVisible(true);
    jLabelQueryTime.setText(I18n.get("queryPanel.queryTime") + " " + response.getQTime() + " ms");
    jLabelResultsFound.setText(I18n.get("queryPanel.resultsFound") + " " + response.getResults().getNumFound());
    this.setFacets(response.getFacetFields());
    resultsTableModel.setResponse(response);
    jTableResults.setModel(resultsTableModel);
    jTableResults.doLayout();
    jTableResults.requestFocusInWindow();
    resultsTableModel.fireTableStructureChanged();
    resultsTableModel.fireTableDataChanged();
    this.revalidate();
  }
  
  private void setFacets(List<FacetField> facetFields) {
    if(facetFields == null) {
      return;
    }
    DefaultListModel model = new DefaultListModel();
    int i = 0;
    for(FacetField facet:facetFields) {
      for(Count count:facet.getValues()) {
        model.add(i++, facet.getName() + ":" + count.getName() + "(" + count.getCount() + ")");
      }
    }
    jListFacets.setModel(model);
  }
  
  public String getQ() {
    return jTextFieldQ.getText();
  }
  
  public String getFQ() {
    return jTextFieldFQ.getText();
  }
  
  public String getFacetFields() {
    return jTextFieldFacetFields.getText();
  }
  
  public String getQT() {
    return jTextFieldQT.getText();
  }
  
  public String getSort() {
    return jTextFieldSort.getText();
  }
  
  public ORDER getSortOrder() {
    return (ORDER) jComboSortOrder.getSelectedItem();
  }
  
  public String getOtherParams() {
    return jTextFieldOtherParams.getText();
  }
  
  public boolean getHighlight() {
    return jCheckBoxHighlight.isSelected();
  }
  
  public Integer getRows() {
    return integerFieldRows.getInteger();
  }
  
  public void showError(QueryException exception) {
    jTextAreaError.setText(SwingUtils.getStackTraceString(exception));
    jScrollPaneError.setVisible(true);
    outputPanel.setVisible(false);
    this.revalidate();
  }
  
  public Integer getStart() {
    return integerFieldStart.getInteger();
  }
  
  private class ExecuteQueryActionListener implements ActionListener {
    
    @Override
    public void actionPerformed(final ActionEvent e) {
      controller.executeQuery();
    }
  }
  
}
