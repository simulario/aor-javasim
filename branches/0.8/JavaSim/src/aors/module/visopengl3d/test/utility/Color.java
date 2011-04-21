package aors.module.visopengl3d.test.utility;

/**
 * The Color class is representing a color inside of the RGBA color space.
 * 
 * @author Sebastian Mucha
 * @since February 17th, 2010
 */
public class Color {

  // RGBA color components
  private double red;
  private double green;
  private double blue;
  private double alpha;

  // Color constants (16 VGA colors)
  public static final String BLACK = "black";
  public static final String GRAY = "gray";
  public static final String MAROON = "maroon";
  public static final String RED = "red";
  public static final String GREEN = "green";
  public static final String LIME = "lime";
  public static final String OLIVE = "olive";
  public static final String YELLOW = "yellow";
  public static final String NAVY = "navy";
  public static final String BLUE = "blue";
  public static final String PURPLE = "purple";
  public static final String FUCHSIA = "fuchsia";
  public static final String TEAL = "teal";
  public static final String AQUA = "aqua";
  public static final String SILVER = "silver";
  public static final String WHITE = "white";

  /**
   * Create a new Color instance and initialize the color components.
   * 
   * @param r
   *          Red
   * @param g
   *          Green
   * @param b
   *          Blue
   * @param a
   *          Alpha
   */
  public Color(double r, double g, double b, double a) {
    red = r;
    green = g;
    blue = b;
    alpha = a;
  }

  /**
   * Create a new Color instance and initialize it from a string.
   * 
   * @param color
   *          String coded color value.
   */
  public Color(String color) {
    setColorFromString(color);
  }

  /**
   * Initializes the color from a string. Either the color name, a RGB value
   * coded as "rgb(255,255,255)" or a hex value coded as "#FFFFFF" can be
   * provided.
   * 
   * @param color
   *          String coded color value.
   */
  public void setColorFromString(String color) {
    if (color.matches("[r][g][b][(]\\d{1,3}[,]\\d{1,3}[,]\\d{1,3}[)]")) {
      setColorByRGB(color);
    } else if (color.matches("[#][\\d[a-fA-F]]{6}")) {
      setColorByHex(color);
    } else {
      setColorByName(color);
    }
  }

  /**
   * Initializes the color from a string coded as "rgb(255, 255, 255)". If an
   * invalid color value was provided the color will be set to white.
   * 
   * @param rgb
   *          RGB value coded as a string.
   */
  private void setColorByRGB(String rgb) {
    String tmp = null;
    int offsetX = 0, offsetY = 0;

    // Extract red component
    offsetX = rgb.indexOf("(", offsetX) + 1;
    offsetY = rgb.indexOf(",", offsetX);
    tmp = rgb.substring(offsetX, offsetY);
    red = Double.valueOf(tmp) / 255.0;

    // Extract green component
    offsetX = offsetY + 1;
    offsetY = rgb.indexOf(",", offsetX);
    tmp = rgb.substring(offsetX, offsetY);
    green = Double.valueOf(tmp) / 255.0;

    // Extract blue component
    offsetX = offsetY + 1;
    offsetY = rgb.indexOf(")", offsetX);
    tmp = rgb.substring(offsetX, offsetY);
    blue = Double.valueOf(tmp) / 255.0;

    // Check if the values are valid
    if (red > 1.0 || green > 1.0 || blue > 1.0) {
      red = 1.0;
      green = 1.0;
      blue = 1.0;
    }

    // Alpha is always 1 by default
    alpha = 1;
  }

  /**
   * Initializes the color from a string coded as a hex value ("#FFFFFF"). If an
   * invalid color value was provided the color will be set to white.
   * 
   * @param hex
   *          HEX value coded as a string.
   */
  private void setColorByHex(String hex) {
    String tmp = null;

    // Extract red component
    tmp = hex.substring(1, 3);
    red = (double) (Integer.parseInt(tmp, 16)) / 255.0;

    // Extract green component
    tmp = hex.substring(3, 5);
    green = (double) (Integer.parseInt(tmp, 16)) / 255.0;

    // Extract blue component
    tmp = hex.substring(5, 7);
    blue = (double) (Integer.parseInt(tmp, 16)) / 255.0;

    // Check if the values are valid
    if (red > 1.0 || green > 1.0 || blue > 1.0) {
      red = 1.0;
      green = 1.0;
      blue = 1.0;
    }

    // Alpha is always 1 by default
    alpha = 1;
  }

  /**
   * Initializes the color components to the appropriate values of the color
   * stated by name. If an invalid color name is provided, the color will be set
   * to white.
   * 
   * @param name
   *          Name of the color
   */
  private void setColorByName(String name) {
    if (name.equals(BLACK)) {
      red = 0.0;
      green = 0.0;
      blue = 0.0;
    } else if (name.equals(GRAY)) {
      red = 0.5;
      green = 0.5;
      blue = 0.5;
    } else if (name.equals(MAROON)) {
      red = 0.5;
      green = 0.0;
      blue = 0.0;
    } else if (name.equals(RED)) {
      red = 1.0;
      green = 0.0;
      blue = 0.0;
    } else if (name.equals(GREEN)) {
      red = 0.0;
      green = 0.5;
      blue = 0.0;
    } else if (name.equals(LIME)) {
      red = 0.0;
      green = 1.0;
      blue = 0.0;
    } else if (name.equals(OLIVE)) {
      red = 0.5;
      green = 0.5;
      blue = 0.0;
    } else if (name.equals(YELLOW)) {
      red = 1.0;
      green = 1.0;
      blue = 0.0;
    } else if (name.equals(NAVY)) {
      red = 0.0;
      green = 0.0;
      blue = 0.5;
    } else if (name.equals(BLUE)) {
      red = 0.0;
      green = 0.0;
      blue = 1.0;
    } else if (name.equals(PURPLE)) {
      red = 0.5;
      green = 0.0;
      blue = 0.5;
    } else if (name.equals(FUCHSIA)) {
      red = 1.0;
      green = 0.0;
      blue = 1.0;
    } else if (name.equals(TEAL)) {
      red = 0.0;
      green = 0.5;
      blue = 0.5;
    } else if (name.equals(AQUA)) {
      red = 0.0;
      green = 1.0;
      blue = 1.0;
    } else if (name.equals(SILVER)) {
      red = 192.0 / 255.0;
      green = 192.0 / 255.0;
      blue = 192.0 / 255.0;
    } else if (name.equals(WHITE)) {
      red = 1.0;
      green = 1.0;
      blue = 1.0;
    } else {
      red = 1.0;
      green = 1.0;
      blue = 1.0;
    }

    // Alpha is always 1 by default
    alpha = 1;
  }

  // Setter & Getter -----------------------------------------------------------

  /**
   * Returns the color as an array with 4 elements.
   */
  public double[] getColor() {

    // Create the color array and initialize it to the color's components
    double[] color = new double[4];

    color[0] = red;
    color[1] = green;
    color[2] = blue;
    color[3] = alpha;

    return color;
  }

  public double getRed() {
    return red;
  }

  public void setRed(double red) {
    this.red = red;
  }

  public double getGreen() {
    return green;
  }

  public void setGreen(double green) {
    this.green = green;
  }

  public double getBlue() {
    return blue;
  }

  public void setBlue(double blue) {
    this.blue = blue;
  }

  public double getAlpha() {
    return alpha;
  }

  public void setAlpha(double alpha) {
    this.alpha = alpha;
  }

}
