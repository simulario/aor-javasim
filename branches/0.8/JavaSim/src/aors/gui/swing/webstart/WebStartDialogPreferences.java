package aors.gui.swing.webstart;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.UIManager;

public class WebStartDialogPreferences extends JDialog {

  private static final long serialVersionUID = 6082909351568141532L;
  private final String TITLE = "Preferences";
  public static final String OK_BUTTON_ACTION_COMMAND = "Dialog Preferences Ok Button";

  private JPanel contentPanel;
  private ActionListener actionListenerCallingFrame;
  private JComboBox lookAndFeelComboBox;
  private JCheckBox multithreading;

  private String explanationMT = "<html>"
      + "On multi-core processors simulations can be executed faster. But only when"
      + "<br/>"
      + "the time of the parallel execution is much greater than the time used for"
      + "<br/>" + "the creation of the threads!" + "</html>";

  public WebStartDialogPreferences(Frame owner) {
    super(owner, true);
    this.setTitle(this.TITLE);
    // the calling frame is the action lister
    this.actionListenerCallingFrame = (ActionListener) owner;
    int windowWidth = 500;
    int windowHeight = 300;
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
    this.contentPanel.add(this.getSimulationPanel());

    panel.add(new JScrollPane(this.contentPanel), BorderLayout.CENTER);

    panel.add(this.getButtonPanel(), BorderLayout.SOUTH);

    this.add(panel);
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

  private JPanel getSimulationPanel() {

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBorder(BorderFactory.createTitledBorder(" Simulation "));

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

}
