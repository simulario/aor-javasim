package aors.gui.swing.webstart;

import java.awt.Image;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import aors.gui.swing.ImageLoader;

/**
 * This class implements the menu for the AOR simulator Swing GUI.
 * 
 * @author Jens Werner
 * @since 14.02.2010
 * @version $Revision$
 */
public class WebStartMenu extends JMenuBar {

  private static final long serialVersionUID = 2513712522253844013L;

  private JMenu viewMenu;
  private JMenu helpMenu;
  private ActionListener actionListener;

  @SuppressWarnings("unused")
  private List<File> recentlyUsed;

  // menu name declaration
  public final static String VIEW = "View";
  public final static String HELP = "Help";
  public final static String SIMULATION = "Simulation";

  /**
   * 
   * Create a new {@code Menu}.
   * 
   * @param actionListener
   */
  public WebStartMenu(ActionListener actionListener) {
    this.actionListener = actionListener;

    this.recentlyUsed = new LinkedList<File>();
    // used in order to create a dynamic growing menu
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

  private void createViewMenuItems() {

    // +++++ VIEW-Menu +++++
    this.viewMenu = new JMenu(WebStartMenu.VIEW);
    this.add(this.viewMenu);
    /*
     * this.addMenuItem(this.viewMenu, Menu.Item.VISUALISATION,
     * Menu.Item.VISUALISATION_MNEMONIC, Menu.Item.VISUALISATION_IMAGE);
     */
    // PREFERENCES
    this.addMenuItem(this.viewMenu, WebStartMenu.Item.PREFERENCES,
        WebStartMenu.Item.PREFERENCES_MNEMONIC, null,
        WebStartMenu.Item.PREFERENCES_IMAGE);
  }

  private void createHelpMenuItems() {

    // +++++ HELP-Menu
    this.helpMenu = new JMenu(WebStartMenu.HELP);
    this.add(this.helpMenu);
    // ABOUT
    this.addMenuItem(this.helpMenu, WebStartMenu.Item.ABOUT,
        WebStartMenu.Item.ABOUT_MNEMONIC, null, WebStartMenu.Item.ABOUT_IMAGE);

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

    public final static String PREFERENCES = "Preferences";
    public final static char PREFERENCES_MNEMONIC = 'p';
    public final static String PREFERENCES_IMAGE = "preferences.png";

    public final static String EXTERNAL_LOG_VIEWER = "View Log";
    public final static char EXTERNAL_LOG_VIEWER_MNEMONIC = 'l';
    public final static String EXTERNAL_LOG_VIEWER_IMAGE = "accessories-text-viewer.png";

    public final static String EXIT = "Exit";
    public final static char EXIT_MNEMONIC = 'x';
    public final static String EXIT_IMAGE = "quit.png";

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
