package aors.module.visopengl3d.space.component;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import aors.module.visopengl3d.space.model.SpaceModel;
import aors.module.visopengl3d.space.model.TwoDimSpaceModel;
import aors.module.visopengl3d.utility.Color;
import aors.module.visopengl3d.utility.Offset;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;

/**
 * Margin of a two dimensional continuous space model.
 * 
 * @author Sebastian Mucha
 * @since March 25th, 2010
 * 
 */
public class Margin implements SpaceComponent {

  // Space model
  private SpaceModel spaceModel;

  // Offsets
  private Offset offset;

  // Background image
  private Texture backgroundImg;

  // Background color
  private Color backgroundColor;

  // Border color
  private Color borderColor;

  /**
   * Creates a margin instance and initializes its offset.
   * 
   * @param x1
   * @param y1
   * @param x2
   * @param y2
   */
  public Margin(double x1, double y1, double x2, double y2) {
    offset = new Offset(x1, y1, x2, y2);
  }

 
  
  @Override
  public void display(GL2 gl, GLU glu) {
    if (backgroundImg != null) {
      // Get texture coordinates
      TextureCoords tc = backgroundImg.getImageTexCoords();

      // Set the drawing color to white (because of texture)
      gl.glColor4dv(Color.WHITE.getColor(), 0);

      // Enable texture
      backgroundImg.bind();
      backgroundImg.enable();

      // Draw the background with texture
      gl.glBegin(GL2.GL_QUADS);
      double[] normal = {0, 1, 0};
      gl.glNormal3dv(normal, 0);
      gl.glTexCoord2d(tc.left(), tc.bottom()); gl.glVertex3d(offset.x1, 0, -offset.y1);
      gl.glTexCoord2d(tc.right(), tc.bottom()); gl.glVertex3d(offset.x2, 0, -offset.y1);
      gl.glTexCoord2d(tc.right(), tc.top()); gl.glVertex3d(offset.x2, 0, -offset.y2);
      gl.glTexCoord2d(tc.left(), tc.top()); gl.glVertex3d(offset.x1, 0, -offset.y2);
      gl.glEnd();

      // Disable texture
      backgroundImg.disable();
      
    } else {
      // Set the drawing color
      gl.glColor4dv(backgroundColor.getColor(), 0);
      
      // Draw the background
      gl.glBegin(GL2.GL_QUADS);
      double[] normal = {0, 1, 0};
      gl.glNormal3dv(normal, 0);
      gl.glVertex3d(offset.x1, 0, -offset.y1);
      gl.glVertex3d(offset.x2, 0, -offset.y1);
      gl.glVertex3d(offset.x2, 0, -offset.y2);
      gl.glVertex3d(offset.x1, 0, -offset.y2);
      gl.glEnd();
    }

    // Draw the border
    gl.glColor4dv(borderColor.getColor(), 0);
    
    gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);

    gl.glBegin(GL2.GL_QUADS);
    double[] normal = {0, 1, 0};
    gl.glNormal3dv(normal, 0);
    gl.glVertex3d(offset.x1, 0, -offset.y1);
    gl.glVertex3d(offset.x2, 0, -offset.y1);
    gl.glVertex3d(offset.x2, 0, -offset.y2);
    gl.glVertex3d(offset.x1, 0, -offset.y2);
    gl.glEnd();
  }

  @Override
  public double getObjectHeight(double height) {
    return (offset.getHeight() * height) / spaceModel.getyMax();
  }

  @Override
  public double getObjectWidth(double width) {
    return (offset.getWidth() * width) / spaceModel.getxMax();
  }

  @Override
  public double getRotation(double x, double y) {
    return 0;
  }

  @Override
  public double[] getWorldCoordinates(double x, double y) {
    double pos[] = new double[3];

    pos[0] = offset.x1 + ((offset.getWidth() * x) / spaceModel.getxMax());
    pos[1] = offset.y1 + ((offset.getHeight() * y) / spaceModel.getyMax());
    pos[2] = 0;
    
    if(spaceModel instanceof TwoDimSpaceModel) {
    	pos[2] = -pos[1];
    	pos[1] = 0;
    }

    return pos;
  }

  public SpaceModel getSpaceModel() {
    return spaceModel;
  }

  public void setSpaceModel(SpaceModel spaceModel) {
    this.spaceModel = spaceModel;
  }

  public Offset getOffset() {
    return offset;
  }

  public void setOffset(Offset offset) {
    this.offset = offset;
  }

  public Texture getBackgroundImg() {
    return backgroundImg;
  }

  public void setBackgroundImg(Texture backgroundImg) {
    this.backgroundImg = backgroundImg;
  }

  public Color getBackgroundColor() {
    return backgroundColor;
  }

  public void setBackgroundColor(Color backgroundColor) {
    this.backgroundColor = backgroundColor;
  }

  public Color getBorderColor() {
    return borderColor;
  }

  public void setBorderColor(Color borderColor) {
    this.borderColor = borderColor;
  }

}
