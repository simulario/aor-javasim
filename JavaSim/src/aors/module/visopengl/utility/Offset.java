package aors.module.visopengl.utility;

/**
 * Offset of a rectangular area of intrest.
 * 
 * @author Sebastian Mucha
 * @since January 23th, 2010
 * 
 */
public class Offset {

  // Offsets (for convenience these are accessible directly)
  public double x1;
  public double x2;
  public double y1;
  public double y2;

  // Width and height of the area
  private double width, height;

  /**
   * Creates an offset of a certain area of interest.
   * 
   * @param x1
   * @param y1
   * @param x2
   * @param y2
   */
  public Offset(double x1, double y1, double x2, double y2) {
    this.x1 = x1;
    this.x2 = x2;
    this.y1 = y1;
    this.y2 = y2;

    // Calculate width and height
    width = x2 - x1;
    height = y2 - y1;
  }

  public double getWidth() {
    return width;
  }

  public double getHeight() {
    return height;
  }

}
