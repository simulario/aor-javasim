package aors.module.visopengl.gui;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;

import javax.swing.JScrollPane;

import aors.module.GUIModule;
import aors.module.Module;
import aors.module.visopengl.Visualization;
import aors.util.jar.JarUtil;

/**
 * GUI component of the visualization module.
 * 
 * @author Sebastian Mucha
 * @since March 16th, 2010
 * 
 */
public class GUIComponent extends JScrollPane implements GUIModule {

  private static final long serialVersionUID = 2808602945355107239L;

  // Base component of the visualization module
  private Module baseComponent;

  // Panel containing all GUI elements
  private ContentPanel content;

  /**
   * Creates the visualization modules GUI component.
   * 
   * @param baseComponent
   */
  public GUIComponent(Module baseComponent) {
    this.baseComponent = baseComponent;

    // Load JOGL library files
    initJOGL();

    // Create the content panel (always do this after JOGL was initialized)
    content = new ContentPanel();
    
    this.setMinimumSize(new Dimension(500,400));
  }

  /**
   * Dynamically loads required JOGL library files.
   * 
   * @author Mircea Diaconescu
   */
  private void initJOGL() {
    String localTmpPath = Visualization.localTmpPath;
    String jarPath = System.getProperty("user.dir") + File.separator
        + "modules" + File.separator + "visOpenGLModule_";

    // Win32 libraries
    String[] libsWin = { "gluegen-rt.dll", "jogl_gl2.dll",
        "nativewindow_awt.dll", "jogl_cg.dll", "nativewindow_jvm.dll",
        "newt.dll" };

    // UNIX libraries
    String[] libsUnix = { "libgluegen-rt.so", "libjogl_gl2.so",
        "libnativewindow_awt.so", "libjogl_cg.so", "libnativewindow_jvm.so",
        "libnativewindow_x11.so" };

    // MacOS libraries (currently not supported)
    String[] libsMac = null;

    // Library to use
    String[] libs = null;

    // Get the working operating system and use the correct libraries
    if (JarUtil.isWindows()) {
      libs = libsWin;
      jarPath += "win";
    } else if (JarUtil.isUnix()) {
      libs = libsUnix;
      jarPath += "linux";
    } else if (JarUtil.isMac()) {
      libs = libsMac;
      jarPath += "mac";
    }

    // add last part of the file name, depending on the OS Bits version
    if (System.getProperty("os.arch").indexOf("64") != -1) {
      jarPath += "64.jar";
    } else {
      jarPath += "32.jar";
    }

    if (libs != null) {
      // Extract native libraries
      for (int i = 0; i < libs.length; i++) {
        try {
          JarUtil.extractFileFromJar(jarPath, localTmpPath, "lib/native",
              libs[i]);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    // Extract the jar files for OpenGL
    try {
      JarUtil
          .extractFileFromJar(jarPath, localTmpPath, "lib", "gluegen-rt.jar");
      JarUtil.extractFileFromJar(jarPath, localTmpPath, "lib", "jogl.jar");
      JarUtil.extractFileFromJar(jarPath, localTmpPath, "lib",
          "nativewindow.jar");
      JarUtil.extractFileFromJar(jarPath, localTmpPath, "lib", "newt.jar");

      // extract the lang files
      JarUtil.extractFilesListFromJar(jarPath, localTmpPath, "resources");
    } catch (IOException e) {
      e.printStackTrace();
    }

    JarUtil.setLibraryPath(localTmpPath);

    JarUtil.loadJar(localTmpPath, "gluegen-rt.jar");
    JarUtil.loadJar(localTmpPath, "jogl.jar");
    JarUtil.loadJar(localTmpPath, "nativewindow.jar");
    JarUtil.loadJar(localTmpPath, "newt.jar");
  }



  @Override
  public Module getBaseComponent() {
    return baseComponent;
  }

  public ContentPanel getContent() {
    return content;
  }
}
