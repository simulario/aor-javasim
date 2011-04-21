package aors.module.visopengl3d.test.gui;

import java.io.File;
import java.io.IOException;

import aors.util.jar.JarUtil;

/**
 * Main class containing the applications entry point.
 * 
 * @author Sebastian Mucha
 * @since February 16th, 2010
 */
public class Main {

  public static void main(String[] args) {

    // Local path in the temporary directory for this module
    String localTmpPath = "visOpenGL";

    // Path to jar
    String jarPath = System.getProperty("user.dir") + File.separator
        + "modules" + File.separator + "visOpenGLModule_win32.jar";

    // Libraries for Win32
    String[] libsWin = { "gluegen-rt.dll", "jogl_gl2.dll",
        "nativewindow_awt.dll", "jogl_cg.dll", "nativewindow_jvm.dll",
        "newt.dll" };

    // Extract native libraries to the temporary directory
    for (int i = 0; i < libsWin.length; i++) {
      try {
        JarUtil.extractFileFromJar(jarPath, localTmpPath, "lib/native",
            libsWin[i]);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    // Add this path in the library path...
    JarUtil.setLibraryPath(localTmpPath);

    // Create a new application window
    ApplicationWindow window = new ApplicationWindow("JOGL Demo");

    // Start the rendering thread
    Thread rendering = new Thread(window.getDrawingPanel());
    rendering.start();
  }
}
