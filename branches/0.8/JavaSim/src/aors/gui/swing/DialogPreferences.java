package aors.gui.swing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.UIManager;

import aors.data.DataBus;

public class DialogPreferences extends JDialog {

  private static final long serialVersionUID = 5151092781231223366L;

  private final String TITLE = "Preferences";
  public static final String OK_BUTTON_ACTION_COMMAND = "Dialog Preferences Ok Button";

  public static final String XML_FULL_LOGGER = "XML Full Logger";
  public static final String MEMORY_LOGGER = "Memory Logger";
  public static final String NO_LOGGER = "No Logger";

  private JPanel contentPanel;
  private ActionListener actionListenerCallingFrame;
  private JComboBox lookAndFeelComboBox;
  private ButtonGroup loggerButtonGroup;
  private JCheckBox externalXMLEditor;
  private JCheckBox externalLogViewer;
  private JTextField externalXMLEditorTextField;
  private JCheckBox multithreading;

  private JButton chooseXMLEditorButton;

  private String explanationMT = "<html>"
      + "On multi-core processors simulations can be executed faster. But only when"
      + "<br/>"
      + "the time of the parallel execution is much greater than the time used for"
      + "<br/>" + "the creation of the threads!" + "</html>";

  public DialogPreferences(Frame owner) {
    super(owner, true);
    this.setTitle(this.TITLE);
    // the calling frame is the action lister
    this.actionListenerCallingFrame = (ActionListener) owner;
    int windowWidth = 500;
    int windowHeight = 400;
    this.setSize(windowWidth, windowHeight);

    // centre the window
    this.setLocation(
        (this.getToolkit().getScreenSize().width - windowWidth) / 2, (this
            .getToolkit().getScreenSize().height - windowHeight) / 2);

    JPanel panel = new JPanel(new BorderLayout());

    // construct dialog components

    this.contentPanel = new JPanel();
    this.contentPanel.setLayout(new BoxLayout(this.contentPanel,
        BoxLayout.Y_AXIS));

    // arrange dialog components
    this.contentPanel.add(this.getLookAndFeelPanel());
    this.contentPanel.add(this.getXMLEditorPanel());
    this.contentPanel.add(this.getSimulationPanel());

    panel.add(new JScrollPane(this.contentPanel), BorderLayout.CENTER);

    panel.add(this.getButtonPanel(), BorderLayout.SOUTH);

    this.add(panel);
    // this.setVisible(true);
  }

  private JPanel getButtonPanel() {
    JPanel panel = new JPanel();

    panel.setLayout(new FlowLayout(FlowLayout.LEFT));
    panel.setSize(200, 100);

    JButton okButton = new JButton("Ok");
    okButton.setActionCommand(OK_BUTTON_ACTION_COMMAND);
    // let the calling frame handle this action event
    okButton.addActionListener(this.actionListenerCallingFrame);

    JButton cancelButton = new JButton("Cancel");

    // we handle this action event by ourself
    cancelButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent evt) {
        setVisible(false);
      }
    });

    panel.add(okButton);
    panel.add(cancelButton);

    return panel;
  }

  /**
   * 
   * Usage:
   * 
   * 
   * Comments:
   * 
   * 
   * 
   * @return
   */
  private JPanel getLookAndFeelPanel() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

    panel.setBorder(BorderFactory.createTitledBorder(" Look and Feel "));

    Vector<String> lookAndFeelNames = new Vector<String>();

    for (UIManager.LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels()) {
      lookAndFeelNames.add(laf.getName());
    }

    this.lookAndFeelComboBox = new JComboBox(lookAndFeelNames);

    this.lookAndFeelComboBox.setSelectedItem(UIManager
        .getSystemLookAndFeelClassName());
    this.lookAndFeelComboBox.setEditable(false);

    this.lookAndFeelComboBox.addActionListener(this.actionListenerCallingFrame);

    panel.add(this.lookAndFeelComboBox);

    return panel;
  }

  public void selectLookAndFeel(String name) {
    this.lookAndFeelComboBox.setSelectedItem(name);
  }

  /**
   * 
   * Usage:
   * 
   * 
   * Comments:
   * 
   * 
   * 
   * @return
   */
  public String getSelectedLookAndFeel() {
    return (String) this.lookAndFeelComboBox.getSelectedItem();
  }

  /**
   * 
   * Usage:
   * 
   * 
   * Comments:
   * 
   * 
   * 
   * @return
   */
  private JPanel getXMLEditorPanel() {
    JPanel panel = new JPanel(new GridLayout(2, 1));
    panel.setBorder(BorderFactory.createTitledBorder(" XML Editor "));

    this.externalXMLEditorTextField = new JTextField("", 32);
    this.externalXMLEditorTextField.setEnabled(false);

    this.chooseXMLEditorButton = new JButton("choose");
    this.chooseXMLEditorButton.setEnabled(false);
    this.chooseXMLEditorButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        JFileChooser fileChooser = new JFileChooser();

        boolean successful = false;
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {

          if (returnValue != JFileChooser.CANCEL_OPTION) {
            if (fileChooser.getSelectedFile().isFile()) {
              successful = true;
            }
          }
        }

        if (successful) {
          externalXMLEditorTextField.setText(fileChooser.getSelectedFile()
              .getAbsolutePath());
        } else {
          externalXMLEditor.setSelected(false);
          externalXMLEditorTextField.setEnabled(false);
          chooseXMLEditorButton.setEnabled(false);
        }
      }
    });

    this.externalXMLEditor = new JCheckBox("External XML editor ", false);

    this.externalXMLEditor.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        setExternalXMLEditor(((JCheckBox) evt.getSource()).isSelected());
      }
    });

    this.externalLogViewer = new JCheckBox("External log viewer ", false);

    this.externalLogViewer.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        setExternalLogViewer(((JCheckBox) evt.getSource()).isSelected());
      }
    });

    JPanel subPanel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    subPanel1.add(this.externalXMLEditor);
    subPanel1.add(this.externalLogViewer);

    JPanel subPanel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    subPanel2.add(this.externalXMLEditorTextField);
    subPanel2.add(chooseXMLEditorButton);

    panel.add(subPanel1);
    panel.add(subPanel2);

    return panel;
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Set the {@code isExternalXMLEditor}.
   * 
   * 
   * 
   * @param isExternalXMLEditor
   *          The {@code isExternalXMLEditor} to set.
   */
  public void setExternalXMLEditor(boolean isExternalXMLEditor) {

    if (isExternalXMLEditor) {
      externalXMLEditor.setSelected(true);
      externalXMLEditorTextField.setEnabled(true);
      chooseXMLEditorButton.setEnabled(true);
    } else {
      externalXMLEditor.setSelected(false);

      if (!isExternalLogViewer()) {
        externalXMLEditorTextField.setEnabled(false);
        chooseXMLEditorButton.setEnabled(false);
      }
    }

  }

  public boolean isExternalXMLEditor() {
    return this.externalXMLEditor.isSelected();
  }

  public void setExternalLogViewer(boolean isExternalLogViewer) {

    if (isExternalLogViewer) {
      externalLogViewer.setSelected(true);
      externalXMLEditorTextField.setEnabled(true);
      chooseXMLEditorButton.setEnabled(true);
    } else {
      externalLogViewer.setSelected(false);

      if (!isExternalXMLEditor()) {
        externalXMLEditorTextField.setEnabled(false);
        chooseXMLEditorButton.setEnabled(false);
      }
    }
  }

  public boolean isExternalLogViewer() {
    return this.externalLogViewer.isSelected();
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Get the {@code externalXMLEditorLocation}.
   * 
   * 
   * 
   * @return the {@code externalXMLEditorLocation}.
   */
  public String getExternalXMLEditorLocation() {
    return this.externalXMLEditorTextField.getText();
  }

  public void setExternalXMLEditorLocation(String location) {
    this.externalXMLEditorTextField.setText(location);
  }

  /**
   * 
   * Usage:
   * 
   * 
   * Comments:
   * 
   * 
   * 
   * @return
   */
  private JPanel getSimulationPanel() {

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBorder(BorderFactory.createTitledBorder(" Simulation "));

    JPanel loggerPanel = new JPanel(new GridLayout(0, 1));
    this.loggerButtonGroup = new ButtonGroup();

    JRadioButton xmlFullButton = new JRadioButton(XML_FULL_LOGGER);
    this.loggerButtonGroup.add(xmlFullButton);
    loggerPanel.add(xmlFullButton);

    JRadioButton noLoggerButton = new JRadioButton(NO_LOGGER);
    this.loggerButtonGroup.add(noLoggerButton);
    loggerPanel.add(noLoggerButton);

    panel.add(loggerPanel);

    if (Runtime.getRuntime().availableProcessors() > 1) {

      panel.add(new JSeparator());
      this.multithreading = new JCheckBox("Multithreading", false);
      JLabel label = new JLabel(this.explanationMT);
      label.setSize(400, 100);

      JPanel subPanel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));

      subPanel1.add(this.multithreading);

      JPanel subPanel2 = new JPanel();
      subPanel2.add(label);

      panel.add(subPanel1);
      panel.add(subPanel2);
    }

    return panel;
  }

  public boolean isMultithreading() {
    return this.multithreading.isSelected();
  }

  public void setMultithreading(boolean status) {
    // TODO: there was a null-pointer exception at this point, so i added this
    // simple test.
    if (this.multithreading == null)
      this.multithreading = new JCheckBox("Multithreading", false);
    this.multithreading.setSelected(status);
  }

  /**
   * 
   * Usage: this method select a radioButtom from loggerButtomGroup
   * 
   * 
   * Comments:
   * 
   * 
   * 
   * @param loggerValue
   */
  public void selectLoggerRadioButton(int loggerValue) {

    String text = "";
    if (loggerValue == DataBus.LoggerType.FULL_XML_LOGGER) {
      text = XML_FULL_LOGGER;
    } else if (loggerValue == DataBus.LoggerType.MEMORY_LOGGER) {
      // MemoryLogger set from outside
    } else if (loggerValue == DataBus.LoggerType.OBSERVER_LOGGER) {
      text = NO_LOGGER;
    }

    for (Enumeration<AbstractButton> e = this.loggerButtonGroup.getElements(); e
        .hasMoreElements();) {

      JRadioButton jrButton = (JRadioButton) e.nextElement();
      if (jrButton.getText().equals(text)) {
        jrButton.setSelected(true);
      }
    }
  }

  /**
   * 
   * Usage:
   * 
   * 
   * Comments:
   * 
   * 
   * 
   * @return
   */
  public String getLoggerSelection() {

    for (Enumeration<AbstractButton> e = this.loggerButtonGroup.getElements(); e
        .hasMoreElements();) {
      JRadioButton b = (JRadioButton) e.nextElement();
      if (b.getModel() == this.loggerButtonGroup.getSelection()) {
        return b.getText();
      }
    }
    return "";
  }

}
