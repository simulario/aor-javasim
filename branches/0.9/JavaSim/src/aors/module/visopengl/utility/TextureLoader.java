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

  /**
   * Loads an image from the file system and stores it as a OpenGL texture
   * object.
   * 
   * @param filename
   *          Path to the file.
   * @return OpenGL texture object.
   */
  public static Texture load(String filename) {
    Texture texture = null;

    try {
      // Read the texture from the file that was specified
      texture = TextureIO.newTexture(new File(filename), false);

      // Set texture scaling filters
      texture.setTexParameteri(GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
      texture.setTexParameteri(GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
    } catch (Exception e) {
      // System.out.println("Visualization Error: Cannot load texture!");
      e.printStackTrace();
    }

    return texture;
  }

}
