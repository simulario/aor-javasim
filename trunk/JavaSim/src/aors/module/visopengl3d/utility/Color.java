package aors.module.visopengl3d.utility;

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

  // Color name constants
  private static final String NAME_AQUA = "aqua";
  private static final String NAME_BLACK = "black";
  private static final String NAME_BLUE = "blue";
  private static final String NAME_BROWN = "brown";
  private static final String NAME_DARKBLUE = "darkblue";
  private static final String NAME_DARKGREEN = "darkgreen";
  private static final String NAME_DARKGREY = "darkgrey";
  private static final String NAME_DARKRED = "darkred";
  private static final String NAME_FUCHSIA = "fuchsia";
  private static final String NAME_GREEN = "green";
  private static final String NAME_GREY = "grey";
  private static final String NAME_LIGHTBLUE = "lightblue";
  private static final String NAME_LIGHTGREEN = "lightgreen";
  private static final String NAME_LIGHTGREY = "lightgrey";
  private static final String NAME_LIGHTYELLOW = "lightyellow";
  private static final String NAME_LIME = "lime";
  private static final String NAME_MAGENTA = "magenta";
  private static final String NAME_MAROON = "maroon";
  private static final String NAME_NAVY = "navy";
  private static final String NAME_OLIVE = "olive";
  private static final String NAME_ORANGE = "orange";
  private static final String NAME_PINK = "pink";
  private static final String NAME_PURPLE = "purple";
  private static final String NAME_RED = "red";
  private static final String NAME_SILVER = "silver";
  private static final String NAME_TEAL = "teal";
  private static final String NAME_VIOLET = "violet";
  private static final String NAME_WHITE = "white";
  private static final String NAME_YELLOW = "yellow";

  // Color constants
  public static final Color AQUA = new Color(0, 1, 1, 1);
  public static final Color BLACK = new Color(0, 0, 0, 1);
  public static final Color BLUE = new Color(0, 0, 1, 1);
  public static final Color BROWN = new Color(165.0 / 255, 42.0 / 255,
      42.0 / 255, 1);
  public static final Color DARKBLUE = new Color(0, 0, 139.0 / 255, 1);
  public static final Color DARKGREEN = new Color(0, 100.0 / 255, 0, 1);
  public static final Color DARKGREY = new Color(169.0 / 255, 169.0 / 255,
      169.0 / 255, 1);
  public static final Color DARKRED = new Color(139.0 / 255, 0, 0, 1);
  public static final Color FUCHSIA = new Color(1, 0, 1, 1);
  public static final Color GREEN = new Color(0, 0.5, 0, 1);
  public static final Color GREY = new Color(0.5, 0.5, 0.5, 1);
  public static final Color LIGHTBLUE = new Color(173.0 / 255, 216.0 / 255,
      230.0 / 255, 1);
  public static final Color LIGHTGREEN = new Color(144.0 / 255, 238.0 / 255,
      144.0 / 255, 1);
  public static final Color LIGHTGREY = new Color(211.0 / 255, 211.0 / 255,
      211.0 / 255, 1);
  public static final Color LIGHTYELLOW = new Color(1, 1, 224.0 / 255, 1);
  public static final Color LIME = new Color(0, 1, 0, 1);
  public static final Color MAGENTA = new Color(1, 0, 1, 1);
  public static final Color MAROON = new Color(0.5, 0, 0, 1);
  public static final Color NAVY = new Color(0, 0, 0.5, 1);
  public static final Color OLIVE = new Color(0.5, 0.5, 0, 1);
  public static final Color ORANGE = new Color(1, 165.0 / 255, 0, 1);
  public static final Color PINK = new Color(1, 192.0 / 255, 203.0 / 255, 1);
  public static final Color PURPLE = new Color(0.5, 0, 0.5, 1);
  public static final Color RED = new Color(1, 0, 0, 1);
  public static final Color SILVER = new Color(192.0 / 255, 192.0 / 255,
      192.0 / 255, 1);
  public static final Color TEAL = new Color(0, 0.5, 0.5, 1);
  public static final Color VIOLET = new Color(238.0 / 255, 130.0 / 255,
      238.0 / 255, 1);
  public static final Color WHITE = new Color(1, 1, 1, 1);
  public static final Color YELLOW = new Color(1, 1, 0, 1);

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
   *          String coded color value
   */
  public void setColorFromString(String color) {
    if (color.matches("\\d{1,3}[ ]\\d{1,3}[ ]\\d{1,3}")) {
      setColorByRGB(color);
    } else {
      setColorByName(color);
    }
  }

  /**
   * Initializes the color from a string coded as "rgb(255, 255, 255)". If an
   * invalid color value was provided the color will be set to white.
   * 
   * @param rgb
   *          RGB value coded as a string
   */
  private void setColorByRGB(String rgb) {
    String redStr = null;
    String greenStr = null;
    String blueStr = null;

    int firstIndex = 0;
    int lastIndex = rgb.indexOf(" ");

    redStr = rgb.substring(firstIndex, lastIndex);

    firstIndex = lastIndex;
    lastIndex = rgb.indexOf(" ", firstIndex + 1);

    greenStr = rgb.substring(firstIndex + 1, lastIndex);

    firstIndex = lastIndex;

    blueStr = rgb.substring(firstIndex + 1);

    red = Double.valueOf(redStr) / 255;
    green = Double.valueOf(greenStr) / 255;
    blue = Double.valueOf(blueStr) / 255;

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
    if (name.equals(NAME_AQUA)) {
      red = 0;
      green = blue = 1;
    } else if (name.equals(NAME_BLACK)) {
      red = green = blue = 0;
    } else if (name.equals(NAME_BLUE)) {
      red = green = 0;
      blue = 1;
    } else if (name.equals(NAME_BROWN)) {
      red = 165.0 / 255.0;
      green = blue = 42.0 / 255.0;
    } else if (name.equals(NAME_DARKBLUE)) {
      red = green = 0;
      blue = 139.0 / 255.0;
    } else if (name.equals(NAME_DARKGREEN)) {
      red = blue = 0;
      green = 100.0 / 255.0;
    } else if (name.equals(NAME_DARKGREY)) {
      red = green = blue = 169.0 / 255.0;
    } else if (name.equals(NAME_DARKRED)) {
      red = 139.0 / 255.0;
      green = blue = 0;
    } else if (name.equals(NAME_FUCHSIA)) {
      red = blue = 1;
      green = 0;
    } else if (name.equals(NAME_GREEN)) {
      red = blue = 0;
      green = 0.5;
    } else if (name.equals(NAME_GREY)) {
      red = green = blue = 0.5;
    } else if (name.equals(NAME_LIGHTBLUE)) {
      red = 173.0 / 255.0;
      green = 216.0 / 255.0;
      blue = 230.0 / 255.0;
    } else if (name.equals(NAME_LIGHTGREEN)) {
      red = blue = 144.0 / 255.0;
      green = 238.0 / 255.0;
    } else if (name.equals(NAME_LIGHTGREY)) {
      red = green = blue = 211.0 / 255.0;
    } else if (name.equals(NAME_LIGHTYELLOW)) {
      red = green = 1;
      blue = 224.0 / 255.0;
    } else if (name.equals(NAME_LIME)) {
      red = blue = 0;
      green = 1;
    } else if (name.equals(NAME_MAGENTA)) {
      red = blue = 1;
      green = 0;
    } else if (name.equals(NAME_MAROON)) {
      red = 0.5;
      green = blue = 0;
    } else if (name.equals(NAME_NAVY)) {
      red = green = 0;
      blue = 0.5;
    } else if (name.equals(NAME_OLIVE)) {
      red = green = 0.5;
      blue = 0;
    } else if (name.equals(NAME_ORANGE)) {
      red = 1;
      green = 165.0 / 255.0;
      blue = 0;
    } else if (name.equals(NAME_PINK)) {
      red = 1;
      green = 192.0 / 255.0;
      blue = 203.0 / 255.0;
    } else if (name.equals(NAME_PURPLE)) {
      red = blue = 0.5;
      green = 0;
    } else if (name.equals(NAME_RED)) {
      red = 1;
      green = blue = 0;
    } else if (name.equals(NAME_SILVER)) {
      red = green = blue = 192.0 / 255.0;
    } else if (name.equals(NAME_TEAL)) {
      red = 0;
      green = blue = 0.5;
    } else if (name.equals(NAME_VIOLET)) {
      red = blue = 238.0 / 255.0;
      green = 130.0 / 255.0;
    } else if (name.equals(NAME_WHITE)) {
      red = green = blue = 1;
    } else if (name.equals(NAME_YELLOW)) {
      red = green = 1;
      blue = 0;
    } else {
      red = green = blue = 1;
    }

    // Alpha is always 1 by default
    alpha = 1;
  }

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
