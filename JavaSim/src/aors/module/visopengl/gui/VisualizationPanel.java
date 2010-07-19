package aors.module.visopengl.gui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import aors.module.visopengl.Visualization;
import aors.module.visopengl.lang.LanguageManager;
import aors.util.jar.JarUtil;

/**
 * Panel containing information and settings affecting the visualization.
 * 
 * @author Sebastian Mucha
 * @since March 1st, 2010
 * 
 */
public class VisualizationPanel extends JPanel {

  private static final long serialVersionUID = -8747483004958627070L;

  // Frame rate label
  private JLabel frameRateLabel;

  // Simulation step label
  private JLabel simStepLabel;

  // Object ID label
  private JLabel objectIDLabel;

  // check-box for enable/disable visualization
  private JCheckBox enableVisThread;

  // combo box for language selection
  private final JComboBox languageSelectionBox;

  // associated label for language selection combo-box.
  private JLabel languageSelectionBoxLabel;

  // FPS rate
  private int fps = 0;

  // simulation step
  private long simStep = 0;

  // selected object ID
  private long objectID = 0;

  // language map (keys = lang code, value = lang name)
  private Map<String, String> languages = new HashMap<String, String>();

  /**
   * Creates a panel containing information and settings affecting the
   * visualization.
   */
  public VisualizationPanel() {
    setLayout(new FlowLayout(FlowLayout.LEADING, 20, 0));
    this.setBorder(new EtchedBorder());

    // create GUI components
    this.frameRateLabel = new JLabel();
    this.updateFrameRateLabel(0);

    this.simStepLabel = new JLabel();
    this.updateSimStepLabel(0);

    this.objectIDLabel = new JLabel();
    updateObjectIDLabel(0);

    this.enableVisThread = new JCheckBox(LanguageManager
        .getMessage("enableVisualization_LABEL"), true);

    this.languageSelectionBoxLabel = new JLabel(LanguageManager
        .getMessage("languageSelectionBox_LABEL")
        + ":");

    // Don't draw a focus border around the check box if it is selected
    enableVisThread.setFocusPainted(false);

    // create languages stuff
    this.languages = createSupportedLanguages();
    languageSelectionBox = new JComboBox(getLanguagesNames());
    languageSelectionBox.setLightWeightPopupEnabled(false);
    for (int i = 0; i < languages.size(); i++) {
      if (languages.get(((String) languageSelectionBox.getItemAt(i))).equals(
          LanguageManager.getCurrentLanguageCode())) {
        
        languageSelectionBox.setSelectedIndex(i);
        break;
      }
    }
    languageSelectionBox.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String selectedLang = (String) ((JComboBox) e.getSource())
            .getSelectedItem();
        String langCode = languages.get(selectedLang);
        Visualization visController = ((Visualization) ((GUIComponent) (getParent()
            .getParent().getParent())).getBaseComponent());
        visController.notifyLanguageChange(langCode, "");
      }
    });

    // Add components to the panel
    add(frameRateLabel);
    add(simStepLabel);
    add(objectIDLabel);
    add(enableVisThread);
    add(languageSelectionBoxLabel);
    add(languageSelectionBox);
  }

  /**
   * take care of creating all languages based on existing translation files
   */
  private Map<String, String> createSupportedLanguages() {
    File langFolder = new File(JarUtil.TMP_DIR + File.separator
        + Visualization.localTmpPath);
    String regex = "(translation)((_[a-z]{2})?)((_[A-Z]{2})?)(.properties)";
    Map<String, String> result = new HashMap<String, String>();

    int nmrOfLang = langFolder.list().length;
    for (int i = 0; i < nmrOfLang; i++) {
      String fileName = langFolder.listFiles()[i].getName();
      if (fileName.endsWith("properties")) {
        if (fileName.matches(regex)) {
          String parts[] = fileName.split("_");
          Locale locale;
          if (parts.length == 1) {
            locale = new Locale("en", "Default");
            Locale.setDefault(locale);
          } else if (parts.length == 2) {
            locale = new Locale(parts[1].split("\\.")[0]);
            // set English as default Locale
            if (locale.getLanguage().equalsIgnoreCase("en")) {
              Locale.setDefault(locale);
            }
          } else {
            locale = new Locale(parts[1], parts[2].split("\\.")[0]);
          }
          result.put(locale.getDisplayLanguage(), locale.getLanguage());
        }
      }
    }

    return result;
  }

  /**
   * Create the languages name list that will be displayed in the combo box for
   * language selection
   * 
   * @return the array with language names
   */
  private String[] getLanguagesNames() {
    Object[] keySet = this.languages.keySet().toArray();
    int n = this.languages.keySet().size();
    String[] result = new String[n];

    for (int i = 0; i < n; i++) {
      result[i] = (String) keySet[i];
    }

    // order alphabetically
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < i; j++) {
        if (result[i].compareTo(result[j]) <= 0) {
          String temp = result[i];
          result[i] = result[j];
          result[j] = temp;
        }
      }
    }

    return result;
  }

  /**
   * Updates the text of the frame rate label.
   * 
   * @param fps
   *          Current frame rate.
   */
  public void updateFrameRateLabel(int fps) {
    this.fps = fps;
    frameRateLabel.setText(LanguageManager.getMessage("frameRate_LABEL") + ": "
        + this.fps);
  }

  /**
   * Updates the text of the simulation step label.
   * 
   * @param step
   *          Step that is currently displayed.
   */
  public void updateSimStepLabel(long step) {
    this.simStep = step;
    simStepLabel.setText(LanguageManager.getMessage("simStep_LABEL") + ": "
        + this.simStep);
  }

  /**
   * Updates the text of the object ID label.
   * 
   * @param id
   *          ID of a selected object.
   */
  public void updateObjectIDLabel(long id) {
    this.objectID = id;
    objectIDLabel.setText(LanguageManager.getMessage("objectID_LABEL") + ": "
        + this.objectID);
  }

  public JCheckBox getEnableVisThread() {
    return enableVisThread;
  }

  /**
   * Enable/Disable the check-box which allows activating or disabling the
   * visualization module.
   * 
   * @param enable
   *          true = enable, false = disable
   */
  public void setEnabledOnOffFeature(boolean enable) {
    this.enableVisThread.setEnabled(enable);
  }

  /**
   * Enable/Disable the language selection box which allows to choose a language
   * that will be used for visualization module.
   * 
   * @param enable
   *          true = enable, false = disable
   */
  public void setEnabledLanguageSelection(boolean enable) {
    this.languageSelectionBox.setEnabled(enable);
    this.languageSelectionBoxLabel.setEnabled(enable);
  }

  /**
   * This method refresh this GUI component. That implies updating all language
   * dependent messages/labels used.
   */
  public void refreshGUI() {
    
    this.languages = createSupportedLanguages();
    languageSelectionBox.removeAllItems();
    String[] langNames = this.getLanguagesNames();
    for(int i=0;i<langNames.length;i++) {
      languageSelectionBox.addItem(langNames[i]);
    }
    languageSelectionBox.setLightWeightPopupEnabled(false);
    for (int i = 0; i < languages.size(); i++) {
      if (languages.get(((String) languageSelectionBox.getItemAt(i))).equals(
          LanguageManager.getCurrentLanguageCode())) {

        languageSelectionBox.setSelectedIndex(i);
        break;
      }
    }
    
    updateFrameRateLabel(this.fps);
    updateSimStepLabel(this.simStep);
    updateObjectIDLabel(this.objectID);
    this.languageSelectionBoxLabel.setText(LanguageManager
        .getMessage("languageSelectionBox_LABEL")
        + ":");
    this.enableVisThread.setText(LanguageManager
        .getMessage("enableVisualization_LABEL"));
  }
}
