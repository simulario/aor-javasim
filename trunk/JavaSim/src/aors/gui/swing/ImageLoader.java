package aors.gui.swing;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * This class loads an image and this also from an jar file.
 * 
 * @author Marco Pehla
 * @since 19.07.2008
 * @version $Revision: 1.0 $
 */
public class ImageLoader {

  public static Image loadImage(final String imageName) {
    final java.lang.ClassLoader loader = ImageLoader.class.getClassLoader();
    Image image = null;
    InputStream is = (InputStream) AccessController
        .doPrivileged(new PrivilegedAction<Object>() {
          public Object run() {
            if (loader != null) {
              return loader.getResourceAsStream(imageName);
            } else {
              return ClassLoader.getSystemResourceAsStream(imageName);
            }
          }
        });
    if (is != null) {
      try {
        final int BlockLen = 256;
        int offset = 0;
        int len;
        byte imageData[] = new byte[BlockLen];
        while ((len = is.read(imageData, offset, imageData.length - offset)) > 0) {
          if (len == (imageData.length - offset)) {
            byte newData[] = new byte[imageData.length * 2];
            System.arraycopy(imageData, 0, newData, 0, imageData.length);
            imageData = newData;
            newData = null;
          }
          offset += len;
        }
        image = Toolkit.getDefaultToolkit().createImage(imageData);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return image;
  }
}
