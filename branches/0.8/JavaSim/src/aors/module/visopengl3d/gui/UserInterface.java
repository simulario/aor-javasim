package aors.module.visopengl3d.gui;

import javax.swing.JScrollPane;

import aors.module.GUIModule;
import aors.module.Module;

/**
 * This is the GUI plug-in of the OpenGL visualization module.
 * 
 * @author Sebastian Mucha
 * @since March 1st, 2010
 * 
 */
public class UserInterface extends JScrollPane implements GUIModule {

  private static final long serialVersionUID = 8738505767104317113L;

  // Reference to the OpenGL visualization module
  private Module module;

  /**
   * Creates a new UserInterface instance and initializes its members.
   * 
   * @param module
   *          Reference to the OpenGL visualization module.
   */
  public UserInterface(Module module) {
    this.module = module;
  }

  @Override
  public Module getBaseComponent() {
    return module;
  }

}
