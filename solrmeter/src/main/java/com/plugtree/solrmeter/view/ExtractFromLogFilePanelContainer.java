package com.plugtree.solrmeter.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import com.plugtree.solrmeter.controller.ExtractFromLogFileController;

public class ExtractFromLogFilePanelContainer extends JPanel {
  
  private static final long serialVersionUID = -8081960658969356885L;
  private JPanel buttonPanel;
  private JButton buttonExtract;
  private JButton buttonCancel;
  private JTextPane textPaneExtractionDetails;
  
  private ExtractFromLogFileController controller;
  private JProgressBar progressBar;
  
  public ExtractFromLogFilePanelContainer(Window parent) {
    super();
    controller = new ExtractFromLogFileController(this, parent);
    this.initGUI();
  }
  
  private void initGUI() {
    buttonPanel = this.createButtonPanel();
    GridBagLayout layout = new GridBagLayout();
    this.setLayout(layout);

    GridBagConstraints constraints = new GridBagConstraints();
    constraints.fill = GridBagConstraints.BOTH;
    constraints.gridwidth =4;
    constraints.gridheight = 1;
    constraints.insets = new Insets(5,10,0,10);

    int row = 0;

    constraints.gridx = 0;
    constraints.gridy = row;
//    constraints.weighty = 1.0;
    constraints.weightx = 1.0;
    this.add(new ExtractFromLogFilePanel(controller), constraints);
    row++;
    
    constraints.gridx = 0;
    constraints.gridy = row;
//      constraints.insets = new Insets(5,10,0,10);
//    constraints.weighty = 1.0;
//    constraints.weightx = 0.8;
    this.progressBar = new JProgressBar(0,100);
    this.progressBar.setVisible(false);
     
    this.add(this.progressBar, constraints);
    row++;
    
    constraints.gridx = 0;
    constraints.gridy = row;
    constraints.weighty = 1.0;
//    constraints.weightx = 0.0;

//    constraints.ipadx= 10;
//    constraints.insets = new Insets(5,10,0,10);
    this.textPaneExtractionDetails = new JTextPane();
    this.textPaneExtractionDetails.setVisible(false);
    this.textPaneExtractionDetails.setEditable(false);
    this.textPaneExtractionDetails.setEnabled(true);

    this.add(new JScrollPane(this.textPaneExtractionDetails), constraints);
    row++;
//    
    constraints.gridx = 0;
    constraints.gridy = row;
    constraints.weighty = 0.0;
      constraints.insets = new Insets(0,0,0,0);
//    constraints.weightx = 1.0;
    this.add(buttonPanel, constraints);
      
  }
  
  
  private JPanel createButtonPanel() {
    JPanel panel = new JPanel();
    panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
    panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
    panel.add(Box.createHorizontalGlue());
    
    buttonExtract = new JButton(I18n.get("tools.extract.do"));
    
    buttonExtract.addActionListener(new ActionListener() {
      
      @Override
      public void actionPerformed(ActionEvent e) {
        controller.extract();
      }
    });
    
    panel.add(buttonExtract);
    
    panel.add(Box.createRigidArea(new Dimension(5, 5)));
    buttonCancel = new JButton(I18n.get("tools.extract.cancel"));
    buttonCancel.addActionListener( new ActionListener() {
      
      @Override
      public void actionPerformed(ActionEvent e) {
        controller.cancel();
      }
    });
    
    panel.add(buttonCancel);
    
    return panel;
  }
  
  
  public void beginExtraction() {
    progressBar.setVisible(true);
    progressBar.setIndeterminate(true);
    buttonExtract.setEnabled(false);
    textPaneExtractionDetails.setVisible(false);
  }
  
  public void succed(File outputFile) {
    this.endExtraction("tools.extract.result.success");
    textPaneExtractionDetails.setText(I18n.get("tools.extract.result.success.details") +" "+ outputFile.getAbsolutePath());
  }
  
  public void error(Exception e) {
    this.endExtraction("tools.extract.result.error");
    textPaneExtractionDetails.setText(I18n.get("tools.extract.result.error.details") +" "+ e.getMessage());
  }
  
  
  private void endExtraction(String message) {
    progressBar.setIndeterminate(false);
    progressBar.setValue(100);
    progressBar.setString(I18n.get(message));
    textPaneExtractionDetails.setVisible(true);
    buttonExtract.setEnabled(true);
    
  }
}
