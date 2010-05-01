package aors.gui.swing;

import java.io.File;
import java.util.Arrays;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileView;

import aors.controller.Project;

/**
 * ProjectFileView - This class returns specific icons for already saved
 * simulation projects and descriptions.
 * 
 * @author Marco Pehla
 * @since 21.08.2008
 * @version $Revision$
 */
public class ProjectFileView extends FileView {

  private Icon projectIcon = new ImageIcon(ImageLoader
      .loadImage("package-x-generic.png"));

  /**
   * 
   * Usage: This method return just the name of the file.
   * 
   * 
   * Comments: Overrides method {@code getName} from super class
   * 
   * 
   * 
   * @param file
   * @return
   */
  public String getName(File file) {
    /*
     * Remark: If in the JFileChooser dialog instead of the file name another or
     * additional description should be visible, you need to return something
     * else here.
     */
    return file.getName();
  }

  /**
   * 
   * Usage: This method returns the right icon for the given file.
   * 
   * 
   * Comments: Overrides method {@code getIcon} from super class
   * 
   * 
   * 
   * @param file
   * @return Icon object for the file
   */
  public Icon getIcon(File file) {
    // return the icon for simulation projects?
    // if the file is a directory
    if (file.isDirectory()) {
      // which has at least some files
      if (file.list() != null) {
        // in all the files of this direcory
        for (String fileName : Arrays.asList(file.list())) {
          // when there is an project description file
          if (fileName.equals(Project.PROJECT_FILE_NAME)) {
            // return the project icon
            return this.projectIcon;
          }
        }
      }
      // otherwise
      return null;
      // if its a file not a directory
    } else {
      return null;
    }

  }

}
