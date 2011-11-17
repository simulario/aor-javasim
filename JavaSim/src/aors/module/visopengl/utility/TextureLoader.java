package aors.module.visopengl.utility;

import java.io.File;

import javax.media.opengl.GL2;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureIO;

/**
 * The TextureLoader class provides the possibility to load images from the file
 * system and use them as OpenGL texture objects. All common formats (*.bmp,
 * *.jpg, *.gif, *.png) are supported.
 * 
 * @author Sebastian Mucha
 * @since February 15th, 2010
 * 
 */
public class TextureLoader {
  public static File rootFolderPath = null;

  /**
   * Loads an image from the file system and stores it as a OpenGL texture
   * object.
   * 
   * @param filename
   *          Path to the file.
   * @return OpenGL texture object.
   */
  public static Texture loadTexture(String filename) {
    Texture texture = null;
    String texturePath = TextureLoader.getTexturePath(filename);

    if (texturePath == null) {
      return null;
    }

    try {
      // Read the texture from the file that was specified
      texture = TextureIO.newTexture(
          new File(texturePath), false);

      // Set texture scaling filters
      texture.setTexParameteri(GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
      texture.setTexParameteri(GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
    } catch (Exception e) {
      // System.out.println("Visualization Error: Cannot load texture!");
      //e.printStackTrace();
    }

    return texture;
  }

  /**
   * Loads and returns textures from the file system.
   * 
   * @param filename
   */
  public static String getTexturePath(String filename) {
    if (TextureLoader.rootFolderPath == null) {
      return null;
    }

    // Search in the project's media directory
    String path = TextureLoader.rootFolderPath.getPath() + File.separator
        + "media" + File.separator + "images" + File.separator + filename;

    // Check if the file is existing in the project directory
    if (new File(path).isFile()) {
      return path;
    } else {
      // Search in the global media directory
      path = System.getProperty("user.dir") + File.separator + "media"
          + File.separator + "images" + File.separator + filename;

      // Check if the file is existing in the global directory
      if (new File(path).isFile()) {
        return path;
      } else {
        System.out.println("Warning: Could not find image " + filename);
      }
    }

    return null;
  }

}
