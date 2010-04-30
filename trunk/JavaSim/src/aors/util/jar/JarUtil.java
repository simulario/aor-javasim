/*************************************************************************************************************
 * AgentObject-Object-Relationship (AOR) Simulation
 * Ontologically well-founded and closer-to-reality simulation of complex discrete event scenarios
 *
 * AOR-JSim v.2
 *
 * Copyright (C) 2008 AOR Team: Daniel Draeger, Adrian Giurca, Emilian Pascalau, Andreas Post, Marco Pehla,
 * Gerd Wagner, Jens Werner, Mircea Diaconescu
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 **************************************************************************************************************/
package aors.util.jar;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * JarUtil - defines some methods used for dealing with jar files.
 * 
 * @author Mircea Diaconescu
 * @since July 23, 2009
 * @version $Revision: 1.0 $
 */
public class JarUtil {

  /** the generic temporarily director **/
  public static String TMP_DIR = System.getProperty("java.io.tmpdir")
      + File.separator + "tmp_aors";

  /** create the class loader used by this Jar Utility */
  public static URLClassLoader classLoader = null;

  /**
   * Check if the OS is Windows
   * 
   * @return true if windows OS false otherwise
   */
  public static boolean isWindows() {
    String os = System.getProperty("os.name").toLowerCase();
    return (os.indexOf("win") >= 0);
  }

  /**
   * Check if the OS is MAC
   * 
   * @return true if MAC OS false otherwise
   */
  public static boolean isMac() {
    String os = System.getProperty("os.name").toLowerCase();
    return (os.indexOf("mac") >= 0);
  }

  /**
   * Check if the OS is Unix based
   * 
   * @return true if Unix based OS false otherwise
   */
  public static boolean isUnix() {
    String os = System.getProperty("os.name").toLowerCase();
    return (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0);
  }

  /**
   * Extract the file from the jar and put it into a temporarily directory, in
   * the system temporarily directory, and a specified folder.
   * 
   * @param jarPathAndFilename
   *          the path to the jar file from where we extract.
   * @param destLocalPath
   *          the local directory inside the temporarily system directory
   * @param fileName
   *          the file name to look into the jar for. It looks for file inside
   *          all directories of the jar, but not inside other jar files!
   * @throws IOException
   *           if the file is not found in jar.
   */
  public static void extractFileFromJar(String jarPathAndFilename,
      String destLocalPath, String filePath, String fileName)
      throws IOException {

    // something is not good...null object ?
    if (jarPathAndFilename == null || destLocalPath == null || fileName == null) {
      return;
    }

    // path to the temporarily directory
    File directory = new File(TMP_DIR + File.separator + destLocalPath);

    // create the dir if not exists
    if (!directory.exists()) {
      directory.mkdirs();
    }

    // the file path
    File libFile = new File(directory, fileName);

    // delete file if exists
    if (libFile.exists()) {
      libFile.delete();
    }

    InputStream in = null;

    try {
      JarFile jarFile = new JarFile(jarPathAndFilename);
      JarEntry jarEntry = null;

      // look for file inside the jar, but not inside other inner jar files!
      Enumeration<JarEntry> files = jarFile.entries();
      while (files.hasMoreElements()) {
        jarEntry = files.nextElement();

        // we found the right file, so break the search
        if (jarEntry.toString().endsWith(fileName)) {
          break;
        }

        jarEntry = null;
      }

      if (jarEntry != null) {
        in = jarFile.getInputStream(jarEntry);
      }

    } catch (Exception ex) {

      in = JarUtil.class.getClassLoader().getResourceAsStream(
          filePath + "/" + fileName);

      if (in == null) {
        throw new IOException("Specified file not found: '" + fileName
            + "' in jar file " + jarPathAndFilename + "!");
      }
    }

    // write the file...
    BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(
        libFile));
    byte buffer[] = new byte[1024];
    int len;
    for (int sum = 0; (len = in.read(buffer)) > 0; sum += len) {
      out.write(buffer, 0, len);
    }
    in.close();
    out.close();

    // just a short wait/pause time for finish the job...
    try {
      Thread.sleep(50);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  /**
   * Extract the file from the jar and put it into a temporarily directory, in
   * the system temporarily directory, and a specified folder.
   * 
   * @param jarPathAndFilename
   *          the path to the jar file from where we extract.
   * @param destLocalPath
   *          the local directory inside the temporarily system directory
   * @param fileName
   *          the file name to look into the jar for. It looks for file inside
   *          all directories of the jar, but not inside other jar files!
   * @throws IOException
   *           if the file is not found in jar.
   */
  public static void extractFilesListFromJar(String jarPathAndFilename,
      String destLocalPath, String dirName) throws IOException {

    // something is not good...null object ?
    if (jarPathAndFilename == null || destLocalPath == null || dirName == null) {
      return;
    }

    // path to the temporarily directory
    File directory = new File(TMP_DIR + File.separator + destLocalPath);

    // create the dir if not exists
    if (!directory.exists()) {
      directory.mkdirs();
    }

    JarFile jarFile = new JarFile(jarPathAndFilename);
    JarEntry jarEntry = null;

    // look for file inside the jar, but not inside other inner jar files!
    Enumeration<JarEntry> files = jarFile.entries();
    boolean found = false;
    while (files.hasMoreElements()) {
      jarEntry = files.nextElement();

      // we found the directory, so now extract the content
      if (jarEntry.toString().lastIndexOf(dirName + "/") != -1
          && !jarEntry.isDirectory()) {

        // the file path
        File file = new File(directory, jarEntry.toString().substring(
            jarEntry.toString().lastIndexOf("/")));

        // delete file if exists
        if (file.exists()) {
          file.delete();
        }

        // write the file...
        InputStream in = jarFile.getInputStream(jarEntry);
        BufferedOutputStream out = new BufferedOutputStream(
            new FileOutputStream(file));
        byte buffer[] = new byte[1024];
        int len;
        for (int sum = 0; (len = in.read(buffer)) > 0; sum += len) {
          out.write(buffer, 0, len);
        }
        in.close();
        out.close();

        found = true;
      }
    }

    if (!found) {
      throw new IOException("Specified dir not found: '" + dirName
          + "' in jar file " + jarPathAndFilename + "!");
    }

    // just a short wait/pause time for finish the job...
    try {
      Thread.sleep(50);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  /**
   * Add the temporarily directory, with the specified sub-directory to the
   * library path java.library.path.
   * 
   * @param destLocalPath
   *          the sub-directory inside the temporarily directory. Please use
   *          File.separator if define sub-sub-directories.
   */
  public static void setLibraryPath(String destLocalPath) {
    File file = new File(TMP_DIR + File.separator + destLocalPath);

    // null path ?!
    if (destLocalPath == null) {
      return;
    }

    // not a correct directory!
    if (!file.isDirectory()) {
      return;
    }

    // get the path as string
    String libPath = file.getPath();

    // add path to java.path.library
    try {
      Field field = ClassLoader.class.getDeclaredField("usr_paths");
      field.setAccessible(true);
      String[] paths = (String[]) field.get(null);
      for (int i = 0; i < paths.length; i++) {
        if (libPath.equals(paths[i])) {
          return;
        }
      }
      String[] tmp = new String[paths.length + 1];
      System.arraycopy(paths, 0, tmp, 0, paths.length);
      tmp[paths.length] = libPath;
      field.set(null, tmp);

    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
    }
  }

  /**
   * Load a Jar file in the sense that it is added to the JVM classpath and this
   * way the classes inside this jar are available to be used for dynamic
   * loading.
   * 
   * @param localPath
   *          the file local path. The real path is formed by using a predefined
   *          directory and the local path is just a sub-path of it
   * @param jarFileName
   *          the jar filename
   */
  public static void loadJar(String localPath, String jarFileName) {
    try {
      loadJar(new URL(new File(JarUtil.TMP_DIR + File.separator + localPath
          + File.separator + jarFileName).toURI().toURL().toString()));
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
  }

  /**
   * This method load classes from a jar into JVM. This is the method that has
   * to be called by the modules for loading additional classes that they need,
   * like another libraries and so on.
   * 
   * @param fullFileURL
   *          the path where the class file or jar file can be found
   */
  public static void loadJar(URL fullFileURL) {
    loadJar(fullFileURL, true);
  }

  /**
   * Load a Jar file in the sense that it is added to the JVM classpath and this
   * way the classes inside this jar are available to be used for dynamic
   * loading. This method is not supposed to be called by the modules, but just
   * by the main application classes.
   * 
   * Suppress warnings is required since is not possible to create generic
   * arrays (primitive array [], not array lists)
   * 
   * @param fullFileURL
   *          the URL of the jar file (include the filename and extension)
   * @param useSystemLoader
   *          if this is true the the system class loader is used, otherwise an
   *          application defined system loader is used
   */
  @SuppressWarnings("unchecked")
  public static void loadJar(URL fullFileURL, boolean useSystemLoader) {

    URLClassLoader sysloader = null;

    if (useSystemLoader) {
      sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
    } else {
      if (classLoader == null) {
        URL[] urls = new URL[1];

        classLoader = new URLClassLoader(urls, ClassLoader
            .getSystemClassLoader());
      }

      sysloader = classLoader;
    }

    Class<URL>[] parameters = new Class[1];

    parameters[0] = URL.class;
    try {
      // Class was unchecked, so used URLClassLoader.class instead
      Method method = URLClassLoader.class.getDeclaredMethod("addURL",
          parameters);
      method.setAccessible(true);
      method.invoke(sysloader, new Object[] { fullFileURL });
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * This clean the class loader for all classes that are loaded using this
   * JarUtil class.
   * 
   * NOTE: this call has to be done only when and if you know exactly what you
   * are doing and what are the results of this call!
   */
  public static void cleanClassLoader() {
    classLoader = null;
    System.gc();
  }
}
