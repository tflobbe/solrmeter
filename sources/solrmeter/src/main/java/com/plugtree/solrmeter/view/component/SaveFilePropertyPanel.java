package com.plugtree.solrmeter.view.component;

import com.plugtree.solrmeter.view.listener.PropertyChangeListener;

import javax.swing.*;
import java.io.File;

public class SaveFilePropertyPanel extends FilePropertyPanel {

  private static final long serialVersionUID = 4928933387598038322L;

  public SaveFilePropertyPanel(String text, String property, boolean editable,
      PropertyChangeListener listener) {
    super(text, property, editable, listener);
  }

  public SaveFilePropertyPanel(String text, String property, boolean editable) {
    super(text, property, editable);
  }

  public SaveFilePropertyPanel(String text, String property,
      PropertyChangeListener listener) {
    super(text, property, listener);
  }
  
  @Override
  protected int getDialogType() {
    return JFileChooser.SAVE_DIALOG;
  }
  
  @Override
  protected void handleReturnValue(int returnValue, JFileChooser fileChooser) {
    if(returnValue == JFileChooser.APPROVE_OPTION) {
      File selectedFile = fileChooser.getSelectedFile();
      textField.setText(selectedFile.getAbsolutePath());
      notifyObservers();
    }
  }

}
