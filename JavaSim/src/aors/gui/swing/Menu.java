package aors.gui.swing;

import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

/**
 * This class implements the menu for the AOR simulator Swing GUI.
 * 
 * @author Marco Pehla
 * @since 19.07.2008
 * @version $Revision$
 */
public class Menu extends JMenuBar {

  private static final long serialVersionUID = 2513712522253844013L;

  private JMenu projectMenu;
  private JMenu buildMenu;

  private JMenu viewMenu;
  private ActionListener actionListener;

  @SuppressWarnings("unused")
  private List<File> recentlyUsed;

  // menu name declaration
  public final static String PROJECT = "Project";
  public final static String BUILD = "Build";
  public final static String SIMULATION = "Simulation";
  public final static String SIMULATION_DESCRIPTION = "Description";
  public final static String VIEW = "View";
  public final static String HELP = "Help";

  /**
   * 
   * Create a new {@code Menu}.
   * 
   * @param actionListener
   */
  public Menu(ActionListener actionListener) {
    this.actionListener = actionListener;

    this.recentlyUsed = new LinkedList<File>();

    this.projectMenu = new JMenu(Menu.PROJECT);
    // used in order to create a dynamic growing menu
    this.createProjectMenuItems();
    this.createBuildMenuItems();
    this.createViewMenuItems();
    this.createHelpMenuItems();

  }

  /**
   * 
   * Usage: Comments:
   * 
   * @param menu
   * @param menuItemName
   * @param mnemonic
   */
  private void addMenuItem(JMenu menu, String menuItemName, char mnemonic,
      KeyStroke keyStroke, String imageName) {
    JMenuItem menuItem = new JMenuItem(menuItemName, mnemonic);
    menuItem.setName(menuItemName);
    menuItem.setActionCommand(menuItemName);
    menuItem.addActionListener(this.actionListener);
    if (keyStroke != null) {
      menuItem.setAccelerator(keyStroke);
    }

    if (imageName != null) {
      Image image = ImageLoader.loadImage(imageName);
      if (image != null) {
        menuItem.setIcon(new ImageIcon(image));
      }
    }
    menu.add(menuItem);
  }

  public void switchMenuItems(List<String> menuItemNames, boolean status) {
    int menuBarCounter = this.getMenuCount();
    int menuItemCounter;
    JMenuItem menuItem;
    // for all menu bars
    for (int menuBarNo = 0; menuBarNo < menuBarCounter; menuBarNo++) {
      menuItemCounter = this.getMenu(menuBarNo).getItemCount();
      // for all menu items
      for (int menuItemNo = 0; menuItemNo < menuItemCounter; menuItemNo++) {
        menuItem = this.getMenu(menuBarNo).getItem(menuItemNo);

        // check if not null, like separators for instance
        if (menuItem != null) {
          for (String menuItemName : menuItemNames) {
            // if they match, switch and break
            if (menuItem.getName().equals(menuItemName)) {
              menuItem.setEnabled(status);
              break;
            }
          }
        }
      }
    }
  }

  /**
   * 
   * Usage: returns the menu item with the given name, if no item is found a new
   * instance is returned
   * 
   * 
   * Comments:
   * 
   * 
   * 
   * @param actionCommand
   * @return
   */
  public JMenuItem getMenuItemByName(String actionCommand) {
    int menuBarCounter = this.getMenuCount();
    int menuItemCounter;
    JMenuItem menuItem;
    // for all menu bars
    for (int menuBarNo = 0; menuBarNo < menuBarCounter; menuBarNo++) {

      menuItemCounter = this.getMenu(menuBarNo).getItemCount();
      // for all menu items
      for (int menuItemNo = 0; menuItemNo < menuItemCounter; menuItemNo++) {
        menuItem = this.getMenu(menuBarNo).getItem(menuItemNo);
        // check if not null, like separators for instance
        if (menuItem != null) {
          // if they match, return the instance
          if (menuItem.getActionCommand().equals(actionCommand)) {
            return menuItem;
          }
        }
      }
    }
    return new JMenuItem();
  }

  private void createProjectMenuItems() {
    // reset the actual instance with a new one
    this.projectMenu = new JMenu(Menu.PROJECT);
    this.add(this.projectMenu);
    // add all the first fixed menu items
    this.addMenuItem(this.projectMenu, Menu.Item.NEW, Menu.Item.NEW_MNEMONIC,
        KeyStroke.getKeyStroke('N', InputEvent.CTRL_MASK), Menu.Item.NEW_IMAGE);
    this
        .addMenuItem(this.projectMenu, Menu.Item.OPEN, Menu.Item.OPEN_MNEMONIC,
            KeyStroke.getKeyStroke('O', InputEvent.CTRL_MASK),
            Menu.Item.OPEN_IMAGE);
    this
        .addMenuItem(this.projectMenu, Menu.Item.SAVE, Menu.Item.SAVE_MNEMONIC,
            KeyStroke.getKeyStroke('S', InputEvent.CTRL_MASK),
            Menu.Item.SAVE_IMAGE);
    this.addMenuItem(this.projectMenu, Menu.Item.SAVE_AS,
        Menu.Item.SAVE_AS_MNEMONIC, null, Menu.Item.SAVE_IMAGE);
    this.addMenuItem(this.projectMenu, Menu.Item.CLOSE,
        Menu.Item.CLOSE_MNEMONIC, null, Menu.Item.CLOSE_IMAGE);

    this.projectMenu.addSeparator();
    /*
     * this.addMenuItem(this.projectMenu, Menu.Item.IMPORT_XML,
     * Menu.Item.IMPORT_XML_MNEMONIC, Menu.Item.IMPORT_XML_IMAGE);
     * 
     * this.projectMenu.addSeparator();
     */
    this.addMenuItem(this.projectMenu, Menu.Item.EXPORT_XML,
        Menu.Item.EXPORT_XML_MNEMONIC, null, Menu.Item.EXPORT_XML_IMAGE);
    this.addMenuItem(this.projectMenu, Menu.Item.EXPORT_JAR,
        Menu.Item.EXPORT_JAR_MNEMONIC, null, Menu.Item.EXPORT_JAR_IMAGE);

    this.projectMenu.addSeparator();
    this
        .addMenuItem(this.projectMenu, Menu.Item.EXIT, Menu.Item.EXIT_MNEMONIC,
            KeyStroke.getKeyStroke('X', InputEvent.CTRL_MASK),
            Menu.Item.EXIT_IMAGE);

  }

  private void createBuildMenuItems() {

    // +++++ BUILD-Menu +++++
    this.buildMenu = new JMenu(Menu.BUILD);
    this.add(this.buildMenu);
    // BUILD_ALL
    this.addMenuItem(this.buildMenu, Menu.Item.BUILD_ALL,
        Menu.Item.BUILD_ALL_MNEMONIC, KeyStroke.getKeyStroke('B',
            InputEvent.CTRL_MASK), Menu.Item.BUILD_ALL_IMAGE);
    this.buildMenu.addSeparator();

    // VALIDATE
    this.addMenuItem(this.buildMenu, Menu.Item.VALIDATE_ONLY,
        Menu.Item.VALIDATE_ONLY_MNEMONIC, KeyStroke.getKeyStroke('V',
            InputEvent.CTRL_MASK), Menu.Item.VALIDATE_ONLY_IMAGE);

    // GENERATE
    this.addMenuItem(this.buildMenu, Menu.Item.GENERATE_ONLY,
        Menu.Item.GENERATE_ONLY_MNEMONIC, KeyStroke.getKeyStroke('G',
            InputEvent.CTRL_MASK), Menu.Item.GENERATE_ONLY_IMAGE);

    // COMPILE
    this.addMenuItem(this.buildMenu, Menu.Item.COMPILE_ONLY,
        Menu.Item.COMPILE_ONLY_MNEMONIC, KeyStroke.getKeyStroke('C',
            InputEvent.CTRL_MASK), Menu.Item.COMPILE_ONLY_IMAGE);
    this.buildMenu.addSeparator();

    // RUN
    this.addMenuItem(this.buildMenu, Menu.Item.RUN, Menu.Item.RUN_MNEMONIC,
        KeyStroke.getKeyStroke('R', InputEvent.CTRL_MASK), Menu.Item.RUN_IMAGE);

  }

  private void createViewMenuItems() {

    // +++++ VIEW-Menu +++++
    this.viewMenu = new JMenu(Menu.VIEW);
    this.add(this.viewMenu);
    /*
     * this.addMenuItem(this.viewMenu, Menu.Item.VISUALISATION,
     * Menu.Item.VISUALISATION_MNEMONIC, Menu.Item.VISUALISATION_IMAGE);
     */
    // PREFERENCES
    this.addMenuItem(this.viewMenu, Menu.Item.PREFERENCES,
        Menu.Item.PREFERENCES_MNEMONIC, null, Menu.Item.PREFERENCES_IMAGE);

    // EXTERNAL_XML_EDITOR
    this.addMenuItem(this.viewMenu, Menu.Item.EXTERNAL_XML_EDITOR,
        Menu.Item.EXTERNAL_XML_EDITOR_MNEMONIC, null,
        Menu.Item.EXTERNAL_XML_EDITOR_IMAGE);

    // EXTERNAL_LOG_VIEWER
    this.addMenuItem(this.viewMenu, Menu.Item.EXTERNAL_LOG_VIEWER,
        Menu.Item.EXTERNAL_LOG_VIEWER_MNEMONIC, null,
        Menu.Item.EXTERNAL_LOG_VIEWER_IMAGE);

  }

  private void createHelpMenuItems() {

    // +++++ HELP-Menu
    this.projectMenu = new JMenu(Menu.HELP);
    this.add(this.projectMenu);
    // ABOUT
    this.addMenuItem(this.projectMenu, Menu.Item.ABOUT,
        Menu.Item.ABOUT_MNEMONIC, null, Menu.Item.ABOUT_IMAGE);

  }

  public void addRecentlyUsed(File file) {

  }

  /**
   * 
   * Usage: This method set a list of recently used files which should appear in
   * the project menu.
   * 
   * 
   * Comments:
   * 
   * 
   * 
   * @param list
   */
  public void setRecentlyUsed(List<File> list) {
    this.recentlyUsed = list;
  }

  /**
   * This inner class hold the constant name declarations for menu items.
   * 
   * @author Marco Pehla
   * @since 19.07.2008
   * @version $Revision$
   */
  public class Item {

    public final static String NEW = "New";
    public final static char NEW_MNEMONIC = 'n';
    public final static String NEW_IMAGE = "stock_generic-mimetype.png";

    public final static String OPEN = "Open";
    public final static char OPEN_MNEMONIC = 'o';
    public final static String OPEN_IMAGE = "document-open.png";

    public final static String SAVE = "Save";
    public final static char SAVE_MNEMONIC = 's';
    public final static String SAVE_IMAGE = "media-floppy.png";

    public final static String SAVE_AS = "Save as";
    public final static char SAVE_AS_MNEMONIC = 'v';
    public final static String SAVE_AS_IMAGE = "media-floppy.png";

    public final static String CLOSE = "Close";
    public final static char CLOSE_MNEMONIC = 'c';
    public final static String CLOSE_IMAGE = "broken-image_24x24.png";

    public final static String IMPORT_XML = "Import XML";
    public final static char IMPORT_XML_MNEMONIC = 'i';
    public final static String IMPORT_XML_IMAGE = "go-next.png";

    public final static String EXPORT_JAR = "Export JAR";
    public final static char EXPORT_JAR_MNEMONIC = 'j';
    public final static String EXPORT_JAR_IMAGE = "go-previous.png";

    public final static String EXPORT_XML = "Export XML";
    public final static char EXPORT_XML_MNEMONIC = 'e';
    public final static String EXPORT_XML_IMAGE = "go-previous.png";

    public final static String PREFERENCES = "Preferences";
    public final static char PREFERENCES_MNEMONIC = 'p';
    public final static String PREFERENCES_IMAGE = "preferences.png";

    public final static String EXTERNAL_XML_EDITOR = "View / edit source";
    public final static char EXTERNAL_XML_EDITOR_MNEMONIC = 'm';
    public final static String EXTERNAL_XML_EDITOR_IMAGE = "accessories-text-editor.png";

    public final static String EXTERNAL_LOG_VIEWER = "View Log";
    public final static char EXTERNAL_LOG_VIEWER_MNEMONIC = 'l';
    public final static String EXTERNAL_LOG_VIEWER_IMAGE = "accessories-text-viewer.png";

    public final static String EXIT = "Exit";
    public final static char EXIT_MNEMONIC = 'x';
    public final static String EXIT_IMAGE = "quit.png";

    public final static String BUILD_ALL = "Build all";
    public final static char BUILD_ALL_MNEMONIC = 'b';
    public final static String BUILD_ALL_IMAGE = "administration.png";

    public final static String VALIDATE_ONLY = "Validate";
    public final static char VALIDATE_ONLY_MNEMONIC = 'v';
    public final static String VALIDATE_ONLY_IMAGE = "system-log-file-viewer.png";

    public final static String GENERATE_ONLY = "Generate";
    public final static char GENERATE_ONLY_MNEMONIC = 'g';
    public final static String GENERATE_ONLY_IMAGE = "gnome-mime-application-java.png";

    public final static String COMPILE_ONLY = "Compile";
    public final static char COMPILE_ONLY_MNEMONIC = 'c';
    public final static String COMPILE_ONLY_IMAGE = "gnome-mime-application-x-class-file.png";

    public final static String RUN = "Run";
    public final static char RUN_MNEMONIC = 'r';
    public final static String RUN_IMAGE = "execute.png";

    public final static String PAUSE = "Pause";
    public final static char PAUSE_MNEMONIC = 'p';
    public final static String PAUSE_IMAGE = "pause.png";

    public final static String STOP = "Stop";
    public final static char STOP_MNEMONIC = 's';
    public final static String STOP_IMAGE = "stop.png";

    public final static String VISUALISATION_ON = "Visualisation on";
    public final static String VISUALISATION_ON_IMAGE = "open-eye.png";
    public final static String VISUALISATION_OFF = "Visualisation off";
    public final static String VISUALISATION_OFF_IMAGE = "no-open-eye.png";

    public final static String ABOUT = "About";
    public final static char ABOUT_MNEMONIC = 'a';
    public final static String ABOUT_IMAGE = "info.png";

    // TODO insert here the method to load different setting or
    // language translations from a XML properties file...
  }

}
