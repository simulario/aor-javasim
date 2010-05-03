package aors.module.statistics.gui;

import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import aors.util.jar.JarUtil;

/**
 * ComponentTranslator
 * 
 * Singleton Class for translating the Gui
 * 
 * @author Daniel Draeger
 * @since 01.11.2009
 */
public class ComponentTranslator {

  private static ComponentTranslator instance = null;
  private static JComponent c;
  public static final String languageLocation = JarUtil.TMP_DIR
      + File.separator + "statisticsModule";
  private final String regex = "(GuiComponent)((_[a-z]{2})?)((_[A-Z]{2})?)(.properties)";
  protected static List<String> languages = new ArrayList<String>();
  public static Map<String, JComponent> compMap;
  protected static List<Locale> supportedLocales = new ArrayList<Locale>();
  private static ResourceBundle resourceBundle;

  private static final String componentError = "ComponentError";
  private final String noMatch = "noMatchError";

  /**
   * Create a new {@code ComponentTranslator}.
   * 
   */
  public ComponentTranslator() {
    getSupportedLocales();
    setResourceBundle(Locale.getDefault());
  }

  /**
   * Usage: instantiates the class ComponentTranslator (done in TabStatistics)
   * 
   * @return ComponentTranslator
   */
  public synchronized static ComponentTranslator getInstance() {
    if (instance == null) {
      instance = new ComponentTranslator();
    }
    return instance;
  }

  /**
   * Usage: sets the actual ResourceBundle
   * 
   * @param locale
   */
  public static void setResourceBundle(Locale locale) {
    File file = new File(languageLocation);
    try {
      URL[] urls = new URL[1];
      urls[0] = file.toURI().toURL();
      ClassLoader loader = new URLClassLoader(urls);
      ResourceBundle bundle = ResourceBundle.getBundle("GuiComponent", locale,
          loader);
      resourceBundle = bundle;
    } catch (MalformedURLException ex) {
      ex.printStackTrace();
    }
  }

  /**
   * Usage: return the ResourceBundle
   * 
   * @return ResourceBundle
   */
  public static ResourceBundle getResourceBundle() {
    return resourceBundle;
  }

  /**
   * Usage: imports all languages from the package statistics.gui.lang and sets
   * "English" as default language
   */
  private void getSupportedLocales() {
    File langFolder = new File(languageLocation);
    int nmrOfLang = langFolder.list().length;
    for (int i = 0; i < nmrOfLang; i++) {
      try {
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
                // for setting the default-language to the system-language
                // if
                // (locale.getLanguage().equals(System.getProperty("user.language"
                // ))) {
                Locale.setDefault(locale);
                supportedLocales.remove(0);
                languages.remove(0);
                for (int j = 1; j < supportedLocales.size(); j++) {
                  supportedLocales.set(j - 1, supportedLocales.get(j));
                  languages.set(j - 1, languages.get(j));
                }
              }
            } else {
              locale = new Locale(parts[1], parts[2].split("\\.")[0]);
            }
            supportedLocales.add(locale);
            languages.add(locale.getDisplayLanguage());
          } else {
            System.out.println(resourceBundle.getString(noMatch));
          }
        }
      } catch (Exception e) {
      }
    }
  }

  /**
   * Usage: create translatable JButton
   * 
   * @param buttonName
   * @param destination
   * @param al
   * @param enabled
   * @return JButton
   */
  public static JButton setButton(String buttonName, JPanel destination,
      ActionListener al, boolean enabled) {
    JButton but = new JButton();
    setGuiComponent(but, buttonName);
    but.addActionListener(al);
    but.setEnabled(enabled);
    destination.add(but);
    return but;
  }

  /**
   * Usage: create translatable JLabel
   * 
   * @param labelName
   * @param destination
   * @param toolTip
   * @return JLabel
   */
  public static JLabel setLabel(String labelName, JPanel destination,
      String toolTip) {
    JLabel lab = new JLabel();
    setGuiComponent(lab, labelName);
    lab.setToolTipText(toolTip);
    destination.add(lab);
    return lab;
  }

  /**
   * Usage: allocates language key to gui component via HashMap
   * 
   * @param comp
   *          - component to set
   * @param name
   *          - language key for name of component (see *.properties files)
   */
  public static void setGuiComponent(JComponent comp, String name) {
    c = comp;
    translate(name);
    compMap.put(name, c);
  }

  /**
   * Usage: translates all gui components
   */
  public static void translateGuiComponents() {
    List<String> tabPanes = new ArrayList<String>();
    for (Iterator<String> it = compMap.keySet().iterator(); it.hasNext();) {
      String str = it.next();
      c = compMap.get(str);
      if (isJTabbedPane()) {
        tabPanes.add(str);
      } else {
        translate(str);
        compMap.put(str, c);
      }
    }
    // TabbedPanes have to be handled after their Tab-Components
    // because the keys in a HashMap are not sorted
    if (tabPanes.size() > 0) {
      for (int i = 0; i < tabPanes.size(); i++) {
        c = compMap.get(tabPanes.get(i));
        translate(tabPanes.get(i));
      }
      tabPanes.clear();
    }
  }

  /**
   * Usage: checks if the component is a JLabel
   * 
   * @return boolean
   */
  private static boolean isJLabel() {
    if (c.getClass().getSimpleName().equalsIgnoreCase("JLabel")) {
      return true;
    }
    return false;
  }

  /**
   * Usage: checks if the component is a JButton
   * 
   * @return boolean
   */
  private static boolean isJButton() {
    if (c.getClass().getSimpleName().equalsIgnoreCase("JButton")) {
      return true;
    }
    return false;
  }

  /**
   * Usage: checks if the component is a JTabbedPane
   * 
   * @return boolean
   */
  private static boolean isJTabbedPane() {
    if (c.getClass().getSimpleName().equalsIgnoreCase("JTabbedPane")) {
      return true;
    }
    return false;
  }

  /**
   * Usage: checks if the component is a JPanel JPanel often are extended by new
   * classes, so three level are checked 1. c is type JPanel 2. c extends JPanel
   * 3. c has superclass which extends JPanel
   * 
   * @return boolean
   */
  private static boolean isJPanel() {
    if (((c.getClass().getSimpleName().equalsIgnoreCase("JPanel")) || (c
        .getClass().getSuperclass().getSimpleName().equalsIgnoreCase("JPanel")))
        || (c.getClass().getSuperclass().getSuperclass().getSimpleName()
            .equalsIgnoreCase("JPanel"))) {
      return true;
    }
    return false;
  }

  /**
   * Usage: checks if the component is a JCheckBox
   * 
   * @return boolean
   */
  private static boolean isJCheckBox() {
    if (c.getClass().getSimpleName().equalsIgnoreCase("JCheckBox")) {
      return true;
    }
    return false;
  }

  /**
   * Usage: checks if the component is a JCheckBox
   * 
   * @return boolean
   */
  private static boolean isJComboBox() {
    if (c.getClass().getSimpleName().equalsIgnoreCase("JComboBox")) {
      return true;
    }
    return false;
  }

  /**
   * Usage: updates the component's text
   * 
   * @param str
   *          language specific text value
   */
  private static void translate(String str) {
    if (isJLabel()) {
      ((JLabel) c).setText(resourceBundle.getString(str));
      try {
        ((JLabel) c).setToolTipText(resourceBundle
            .getString(c.getToolTipText()));
      } catch (Exception e) {
      }
    } else if (isJButton()) {
      ((JButton) c).setText(resourceBundle.getString(str));
    } else if (isJTabbedPane()) {
      JTabbedPane tab = (JTabbedPane) c;
      for (int i = 0; i < tab.getTabCount(); i++) {
        tab.setTitleAt(i, tab.getComponentAt(i).getName());
      }
    } else if (isJCheckBox()) {
      ((JCheckBox) c).setText(resourceBundle.getString(str));
    } else if (isJPanel()) {
      ((JPanel) c).setName(resourceBundle.getString(str));
    } else if (isJComboBox()) {
      // nothing to do -- Items have to be of type KeyValuePair
    } else {
      // other JComponent types have to be added, if needed
      System.out.println(resourceBundle.getString(componentError));
    }
  }

}
