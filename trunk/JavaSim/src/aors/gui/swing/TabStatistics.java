package aors.gui.swing;

/*
 * TODO: delete this class, because is unused
 */

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

public class TabStatistics extends JPanel {

  private static final long serialVersionUID = 2252909432133629804L;

  private JTextPane outputTextPane;
  private JScrollPane scrollPane;

  @Deprecated
  public TabStatistics() {
    setLayout(new BorderLayout());

    outputTextPane = new JTextPane();
    scrollPane = new JScrollPane(outputTextPane);
    add(scrollPane, BorderLayout.CENTER);
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Get the {@code outPutTextPane}.
   * 
   * 
   * 
   * @return the {@code outPutTextPane}.
   */
  public JTextPane getOutputTextPane() {
    return outputTextPane;
  }

}
