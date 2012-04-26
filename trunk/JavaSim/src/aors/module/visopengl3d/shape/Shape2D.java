package aors.module.visopengl3d.shape;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import aors.logger.model.AgtType;
import aors.logger.model.ObjType;
import aors.logger.model.ObjectType;
import aors.logger.model.SlotType;
import aors.model.envsim.Objekt;
import aors.model.envsim.Physical;
import aors.module.visopengl3d.utility.TextureLoader;
import aors.module.visopengl3d.space.view.PropertyMap;
import aors.module.visopengl3d.space.view.SpaceView;
import aors.module.visopengl3d.utility.Color;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;

/**
 * The Shape2D class is the base class for all two dimensional shapes.
 * 
 * @author Sebastian Mucha
 * @since February 3rd, 2010
 * 
 */
public abstract class Shape2D implements Cloneable {

	// String constant for the "PhysicalShape2D" node
	public static final String PHYSICAL_SHAPE_2D = "PhysicalShape2D";

	// String constant for the "Shape2D" node
	public static final String SHAPE_2D = "Shape2D";

	// String constants for the actual shape nodes
	public static final String CIRCLE = "Circle";
	public static final String SQUARE = "Square";
	public static final String RECTANGLE = "Rectangle";
	public static final String TRIANGLE = "Triangle";
	public static final String ELLIPSE = "Ellipse";
	public static final String REGULAR_POLYGON = "RegularPolygon";
	public static final String POLYGON = "Polygon";
	public static final String POLYLINE = "Polyline";
	public static final String ARC = "Arc";

	// String constants for shape attributes
	public static final String FILL = "fill";
	public static final String FILL_RGB = "fillRGB";
	public static final String FILL_OPACITY = "fillOpacity";
	public static final String STROKE = "stroke";
	public static final String STROKE_RGB = "strokeRGB";
	public static final String STROKE_WIDTH = "strokeWidth";
	public static final String STROKE_OPACITY = "strokeOpacity";
	public static final String TEXTURE = "texture";
	public static final String POSITIONING = "positioning";
	public static final String NUMBER_OF_POINTS = "numberOfPoints";

	// String constants for attributes of non-physical object shapes
	public static final String X = "x";
	public static final String Y = "y";
	public static final String OFFSET_X = "offsetX";
	public static final String OFFSET_Y = "offsetY";
	public static final String WIDTH = "width";
	public static final String HEIGHT = "height";
	public static final String R = "r";
	public static final String RX = "rx";
	public static final String RY = "ry";
	public static final String POINTS = "points";
	public static final String DIAMETER = "diameter";
	public static final String SIDE_LENGTH = "sideLength";
	public static final String ANGLE = "angle";
	public static final String START_ANGLE = "startAngle";
	public static final String END_ANGLE = "endAngle";
	public static final String HORIZONTAL_FLIP = "horizontalFlip";
	public static final String ROT = "rot";

	// Shape type
	protected ShapeType type;

	// Position
	protected double x, y;
	protected boolean xRelative, yRelative;
	protected double relativeX, relativeY;

	// Offset
	protected double offsetX, offsetY;
	protected boolean offsetXRelative, offsetYRelative;

	// Dimensions
	protected double width, height;
	protected boolean widthRelative, heightRelative;
	protected double relativeWidth, relativeHeight;

	// Positioning
	protected Positioning positioning = Positioning.CenterCenter;

	// List of points
	protected ArrayList<double[]> pointList;

	// Flag indicating that the point string needs to be parsed
	protected boolean parsePointString = true;

	// Number of points for regular polygons
	protected double numberOfPoints = 3;
	
	protected double angle = 360;
	protected double startAngle = 0;
	protected double endAngle = 360;
	protected boolean useEndAngle = false;

	// Colors
	protected Color fill = Color.BLACK;
	protected Color stroke = Color.WHITE;

	// Opacity
	protected double fillOpacity = 1;
	protected double strokeOpacity = 1;

	// Stroke width
	protected double strokeWidth;

	// Texture
	protected Texture texture;
	protected String textureFilename;

	// Attached shape
	protected Shape2D attachedShape;

	// Flag indicating that the display list needs to be recompiled
	protected boolean recompile;

	// List of associated shape property maps
	protected ArrayList<PropertyMap> propertyMaps;

	// Display list
	protected int displayList = -1;

	/**
	 * Displays the shape.
	 * 
	 * @param gl
	 *          OpenGL pipeline object.
	 * @param glu
	 *          OpenGL utility library object.
	 */
	public void display(GL2 gl, GLU glu) {
		if (displayList != -1) {
			gl.glCallList(displayList);
		}
	}

	/**
	 * Generates the shape's display list.
	 * 
	 * @param gl
	 *          OpenGL pipeline object.
	 * @param glu
	 *          OpenGL utility library object.
	 */
	public abstract void generateDisplayList(GL2 gl, GLU glu);

  /**
   * Applies a color to each vertex in a list of vertices.
   * 
   * @param contour
   *          List of vertices.
   * @param color
   *          Color that will be applied to each vertex.
   */
  protected void applyColor(ArrayList<double[]> contour, Color color) {
    for (double[] vertex : contour) {
      // Set the RGBA components
      vertex[3] = color.getRed();
      vertex[4] = color.getGreen();
      vertex[5] = color.getBlue();
      vertex[6] = color.getAlpha();
    }
  }
  
  /**
   * Performs a mapping from object coordinates into texture coordinates. If the
   * second parameter equals null texture coordinates will be set to 0.
   * 
   * @param contour
   *          List storing vertices.
   * @param tc
   *          tImage coordinates of the texture image.
   * @param verticalFlipped
   *          Specifies, if the texture should be flipped vertical (for the bottom face).
   */
  protected void applyTexture(ArrayList<double[]> contour, TextureCoords tc, boolean verticalFlipped) {
    /*
     * Initialize the texture coordinates to 0 if no image coordinates are
     * available.
     */
    if (tc == null) {
      for (double[] vertex : contour) {
        vertex[7] = 0;
        vertex[8] = 0;
      }
    } else {
      // Maximal and minimal extensions of the polygon
      double xMin = contour.get(0)[0];
      double xMax = contour.get(0)[0];
      double zMin = contour.get(0)[2];
      double zMax = contour.get(0)[2];

      // Loop over all vertices to find the maxima and minima
      for (double[] vertex : contour) {
        if (vertex[0] < xMin)
          xMin = vertex[0];

        if (vertex[0] > xMax)
          xMax = vertex[0];

        if (vertex[2] < zMin)
          zMin = vertex[2];

        if (vertex[2] > zMax)
          zMax = vertex[2];
      }

      // Dimensions of the polygon
      double width = xMax - xMin;
      double height = zMax - zMin;

      // Texture coordinates
      double s = 0, t = 0;

      // Loop over all vertices to find the appropriate texture
      // coordinates
      for (double[] vertex : contour) {
        s = tc.left() + ((vertex[0] - xMin) / width);
        if(!verticalFlipped) {
          t = tc.bottom() + ((vertex[2] - zMin) / height);
        } else {
          t = tc.bottom() + ((zMax - vertex[2]) / height);
        }

        vertex[7] = s;
        vertex[8] = t;
      }
    }
  }
  
  /**
   * Applies a normal to each vertex in a list of vertices.
   * 
   * @param contour
   *          List of vertices.
   * @param normal
   *          Normal that will be applied to each vertex.
   */
  protected void applyNormal(ArrayList<double[]> contour, double[] normal) {
    for (double[] vertex : contour) {
      // Set the x, y and z coordinates of the normal
      vertex[9] = normal[0];
      vertex[10] = normal[1];
      vertex[11] = normal[2];
    }
  }
  
	/**
	 * Applies state changes of shape properties to the shape, for the initial
	 * state.
	 * 
	 * @param obj
	 */
	public void applyPropertyMaps(Objekt obj) {
		if (propertyMaps != null) {
			for (PropertyMap propertyMap : propertyMaps) {
				String propName = propertyMap.getPropertyName();
				if (propName == null || propName.trim().length() < 1) {
					continue;
				}

				if (obj.getClass().equals(Object.class)) {
					System.out
							.println("Visualization Warning: Object with ID " + obj.getId()
									+ " has no getter method for property: " + propName);
					return;
				}

				// get the property value calling the getter via reflection
				Method getter = this.locateMethodInClasses(obj.getClass(), propertyMap
						.getPropertyName());
				if (getter == null) {
					continue;
				}

				// Apply the property map
				try {
					updateVisualProperty(propertyMap.getVisualPropertyName(), propertyMap
							.performMapping(getter.invoke(obj)));
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}

			if (obj instanceof Physical) {
				applyPropertyMaps((Physical) obj);
			}
		}
	}

	/**
	 * Locates a getter method in the class of the object or in its super classes
	 * for a given property
	 * 
	 * @param clazz
	 *          the class where start to look for the getter method
	 * @param propName
	 *          the property name for which we look for the getter method
	 * @return the getter method object is found, null otherwise
	 */
	private Method locateMethodInClasses(Class<?> clazz, String propName) {
		try {
			// Look for the method in the super class and return it
			if (clazz.getSuperclass() != null) {
				String methodName = "get" + Character.toUpperCase(propName.charAt(0))
						+ (propName.length() > 1 ? propName.substring(1) : "");
				Method method = clazz.getDeclaredMethod(methodName);
				return method;
			} else {
				return null;
			}
		} catch (SecurityException e) {
			return null;
		} catch (NoSuchMethodException e) {
			// Look for methods of boolean types
			try {
				String methodName = "is" + Character.toUpperCase(propName.charAt(0))
						+ (propName.length() > 1 ? propName.substring(1) : "");
				Method method = clazz.getDeclaredMethod(methodName);
				return method;
			} catch (SecurityException e1) {
				return null;
			} catch (NoSuchMethodException e1) {
				// Recursively search for the field in other super classes
				return locateMethodInClasses(clazz.getSuperclass(), propName);
			}
		}
	}

	/**
	 * Applies state changes of shape properties to the shape, during runtime.
	 * 
	 * @param objectType
	 */
	public void applyPropertyMaps(ObjectType objectType) {
		if (propertyMaps != null) {
			for (PropertyMap propertyMap : propertyMaps) {
				if (objectType.getSlot() != null) {
					for (SlotType slot : objectType.getSlot()) {
						if (slot.getProperty().equals(propertyMap.getPropertyName())) {
							// Apply the property map
							updateVisualProperty(propertyMap.getVisualPropertyName(),
									propertyMap.performMapping(slot.getValue()));
						}
					}
				}
			}
		}
	}

	/**
	 * Applies state changes of shape properties to the shape, during runtime.
	 * 
	 * @param objType
	 */
	public void applyPropertyMaps(ObjType objType) {
		if (propertyMaps != null) {
			for (PropertyMap propertyMap : propertyMaps) {
				if (objType.getSlot() != null) {
					for (SlotType slot : objType.getSlot()) {
						if (slot.getProperty().equals(propertyMap.getPropertyName())) {
							// Apply the property map
							updateVisualProperty(propertyMap.getVisualPropertyName(),
									propertyMap.performMapping(slot.getValue()));
						}
					}
				}
			}
		}
	}

	/**
	 * Applies state changes of shape properties to the shape, during runtime.
	 * 
	 * @param agtType
	 */
	public void applyPropertyMaps(AgtType agtType) {
		if (propertyMaps != null) {
			for (PropertyMap propertyMap : propertyMaps) {
				if (agtType.getSlot() != null) {
					for (SlotType slot : agtType.getSlot()) {
						if (slot.getProperty().equals(propertyMap.getPropertyName())) {
							// Apply the property map
							updateVisualProperty(propertyMap.getVisualPropertyName(),
									propertyMap.performMapping(slot.getValue()));
						}
					}
				}
			}
		}
	}

	public void applyPropertyMaps(Physical phy) {
		if (propertyMaps != null) {
			for (PropertyMap propertyMap : propertyMaps) {
				if (propertyMap.getPropertyName().equals("x"))
					updateVisualProperty(propertyMap.getVisualPropertyName(), propertyMap
							.performMapping(phy.getX()));
				
				else if (propertyMap.getPropertyName().equals("y"))
					updateVisualProperty(propertyMap.getVisualPropertyName(), propertyMap
							.performMapping(phy.getY()));

				else if (propertyMap.getPropertyName().equals("z"))
					updateVisualProperty(propertyMap.getVisualPropertyName(), propertyMap
							.performMapping(phy.getZ()));

				else if (propertyMap.getPropertyName().equals("rotationAngleX"))
					updateVisualProperty(propertyMap.getVisualPropertyName(), propertyMap
							.performMapping(phy.getRotX()));

				else if (propertyMap.getPropertyName().equals("rotationAngleY"))
					updateVisualProperty(propertyMap.getVisualPropertyName(), propertyMap
							.performMapping(phy.getRotY()));

				else if (propertyMap.getPropertyName().equals("rotationAngleZ"))
					updateVisualProperty(propertyMap.getVisualPropertyName(), propertyMap
							.performMapping(phy.getRotZ()));

				else if (propertyMap.getPropertyName().equals("vx"))
					updateVisualProperty(propertyMap.getVisualPropertyName(), propertyMap
							.performMapping(phy.getVx()));

				else if (propertyMap.getPropertyName().equals("vy"))
					updateVisualProperty(propertyMap.getVisualPropertyName(), propertyMap
							.performMapping(phy.getVy()));

				else if (propertyMap.getPropertyName().equals("vz"))
					updateVisualProperty(propertyMap.getVisualPropertyName(), propertyMap
							.performMapping(phy.getVz()));

				else if (propertyMap.getPropertyName().equals("ax"))
					updateVisualProperty(propertyMap.getVisualPropertyName(), propertyMap
							.performMapping(phy.getAx()));

				else if (propertyMap.getPropertyName().equals("ay"))
					updateVisualProperty(propertyMap.getVisualPropertyName(), propertyMap
							.performMapping(phy.getAy()));

				else if (propertyMap.getPropertyName().equals("az"))
					updateVisualProperty(propertyMap.getVisualPropertyName(), propertyMap
							.performMapping(phy.getAz()));

				else if (propertyMap.getPropertyName().equals("omegaX"))
					updateVisualProperty(propertyMap.getVisualPropertyName(), propertyMap
							.performMapping(phy.getOmegaX()));

				else if (propertyMap.getPropertyName().equals("omegaY"))
					updateVisualProperty(propertyMap.getVisualPropertyName(), propertyMap
							.performMapping(phy.getOmegaY()));

				else if (propertyMap.getPropertyName().equals("omegaZ"))
					updateVisualProperty(propertyMap.getVisualPropertyName(), propertyMap
							.performMapping(phy.getOmegaZ()));

				else if (propertyMap.getPropertyName().equals("alphaX"))
					updateVisualProperty(propertyMap.getVisualPropertyName(), propertyMap
							.performMapping(phy.getAlphaX()));

				else if (propertyMap.getPropertyName().equals("alphaY"))
					updateVisualProperty(propertyMap.getVisualPropertyName(), propertyMap
							.performMapping(phy.getAlphaY()));

				else if (propertyMap.getPropertyName().equals("alphaZ"))
					updateVisualProperty(propertyMap.getVisualPropertyName(), propertyMap
							.performMapping(phy.getAlphaZ()));

				else if (propertyMap.getPropertyName().equals("m"))
					updateVisualProperty(propertyMap.getVisualPropertyName(), propertyMap
							.performMapping(phy.getM()));

				else if (propertyMap.getPropertyName().equals("width"))
					updateVisualProperty(propertyMap.getVisualPropertyName(), propertyMap
							.performMapping(phy.getWidth()));

				else if (propertyMap.getPropertyName().equals("height"))
					updateVisualProperty(propertyMap.getVisualPropertyName(), propertyMap
							.performMapping(phy.getHeight()));

				else if (propertyMap.getPropertyName().equals("depth"))
					updateVisualProperty(propertyMap.getVisualPropertyName(), propertyMap
							.performMapping(phy.getDepth()));

				else if (propertyMap.getPropertyName().equals("materialType"))
					updateVisualProperty(propertyMap.getVisualPropertyName(), propertyMap
							.performMapping(phy.getMaterialType().name()));
			}
		}
	}

	/**
	 * Updates the appropriate shape property with its new value.
	 * 
	 * @param shapeProperty
	 * @param value
	 */
	public void updateVisualProperty(String shapeProperty, String value) {
		if (value != null) {
			if (shapeProperty.equals(FILL) || shapeProperty.equals(FILL_RGB)) {
				Color tmp = new Color(value);

				// Only update if the color has really changed
				for (int i = 0; i < tmp.getColor().length; i++) {
					if (tmp.getColor()[i] != fill.getColor()[i]) {
						// Assign the new value
						fill = tmp;
						recompile = true;
						break;
					}
				}
			}

			else if (shapeProperty.equals(STROKE) || shapeProperty.equals(STROKE_RGB)) {
				Color tmp = new Color(value);

				// Only update if the color has really changed
				for (int i = 0; i < tmp.getColor().length; i++) {
					if (tmp.getColor()[i] != stroke.getColor()[i]) {
						// Assign the new value
						stroke = tmp;
						recompile = true;
						break;
					}
				}
			}

			else if (shapeProperty.equals(FILL_OPACITY)) {
				// Only update if the value has really changed
				if (fillOpacity != Double.valueOf(value)) {
					fillOpacity = Double.valueOf(value);
					recompile = true;
				}
			}

			else if (shapeProperty.equals(STROKE_OPACITY)) {
				// Only update if the value has really changed
				if (strokeOpacity != Double.valueOf(value)) {
					strokeOpacity = Double.valueOf(value);
					recompile = true;
				}
			}

			else if (shapeProperty.equals(STROKE_WIDTH)) {
				// Only update if the value has really changed
				if (strokeWidth != Double.valueOf(value)) {
					strokeWidth = Double.valueOf(value);
					recompile = true;
				}
			}

			else if (shapeProperty.equals(X)) {
				if (value.endsWith("%")) {
					relativeX = Double.valueOf(value.substring(0, value.length() - 1));
					xRelative = true;
				} else if (value.endsWith("px")) {
					x = Double.valueOf(value.substring(0, value.length() - 2));
				} else {
					x = Double.valueOf(value);
				}
				recompile = true;
			}

			else if (shapeProperty.equals(Y)) {
				if (value.endsWith("%")) {
					relativeY = Double.valueOf(value.substring(0, value.length() - 1));
					yRelative = true;
				} else if (value.endsWith("px")) {
					y = Double.valueOf(value.substring(0, value.length() - 2));
				} else {
					y = Double.valueOf(value);
				}
				recompile = true;
			}

			else if (shapeProperty.equals(WIDTH)) {
				if (value.endsWith("%")) {
					relativeWidth = Double
							.valueOf(value.substring(0, value.length() - 1));
					widthRelative = true;
				} else if (value.endsWith("px")) {
					width = Double.valueOf(value.substring(0, value.length() - 2));
				} else {
					width = Double.valueOf(value);
				}
				recompile = true;
			}

			else if (shapeProperty.equals(HEIGHT)) {
				if (value.endsWith("%")) {
					relativeHeight = Double.valueOf(value
							.substring(0, value.length() - 1));
					heightRelative = true;
				} else if (value.endsWith("px")) {
					height = Double.valueOf(value.substring(0, value.length() - 2));
				} else {
					height = Double.valueOf(value);
				}
				recompile = true;
			}

			else if (shapeProperty.equals(R)) {
				if (value.endsWith("%")) {
					relativeWidth = Double
							.valueOf(value.substring(0, value.length() - 1)) * 2;
					widthRelative = true;
				} else if (value.endsWith("px")) {
					width = Double.valueOf(value.substring(0, value.length() - 2)) * 2;
				} else {
					width = Double.valueOf(value) * 2;
				}
				recompile = true;
			}

			else if (shapeProperty.equals(RX)) {
				if (value.endsWith("%")) {
					relativeWidth = Double
							.valueOf(value.substring(0, value.length() - 1)) * 2;
					widthRelative = true;
				} else if (value.endsWith("px")) {
					width = Double.valueOf(value.substring(0, value.length() - 2)) * 2;
				} else {
					width = Double.valueOf(value) * 2;
				}
				recompile = true;
			}

			else if (shapeProperty.equals(RY)) {
				if (value.endsWith("%")) {
					relativeHeight = Double.valueOf(value
							.substring(0, value.length() - 1)) * 2;
					heightRelative = true;
				} else if (value.endsWith("px")) {
					height = Double.valueOf(value.substring(0, value.length() - 2)) * 2;
				} else {
					height = Double.valueOf(value) * 2;
				}
				recompile = true;
			}
			
			else if (shapeProperty.equals(TEXTURE)) {
			  if(value != textureFilename) {
          textureFilename = value;
          texture = TextureLoader.load(value);
          recompile = true;
			  }
      }
			
			else if (shapeProperty.equals(NUMBER_OF_POINTS)) {
			  if(Double.valueOf(value) != numberOfPoints) {
			    numberOfPoints = Double.valueOf(value);
			    recompile = true;
			  }
      }
			
			/*else if (shapeProperty.equals(ANGLE)) {
        if(Double.valueOf(value) != angle) {
          angle = Double.valueOf(value);
          recompile = true;
        }
      }
			
			else if (shapeProperty.equals(START_ANGLE)) {
        if(Double.valueOf(value) != startAngle) {
          startAngle = Double.valueOf(value);
          recompile = true;
        }
      }
			
			else if (shapeProperty.equals(END_ANGLE)) {
        if(Double.valueOf(value) != endAngle) {
          endAngle = Double.valueOf(value);
          recompile = true;
        }
      }
			
			else if (shapeProperty.equals(DIAMETER)) {
			  double sideLength = 2 * (Double.valueOf(value) / 2) * Math.sin(Math.PI / numberOfPoints);
        if(sideLength != width) {
          width = sideLength;
          recompile = true;
        }
      }
			
			else if (shapeProperty.equals(SIDE_LENGTH)) {
        if(Double.valueOf(value) != width) {
          width = Double.valueOf(value);
          recompile = true;
        }
      }
      
      else if (shapeProperty.equals(HORIZONTAL_FLIP)) {
        if(Boolean.valueOf(value) != horizontalFlip) {
          horizontalFlip = Boolean.valueOf(value);
          recompile = true;
        }
      }
      
      else if (shapeProperty.equals(ROT)) {
        if(Double.valueOf(value) != rot) {
          rot = Double.valueOf(value);
          recompile = true;
        }
      }*/
		}
	}

	public ShapeType getType() {
		return type;
	}

	public void setType(ShapeType type) {
		this.type = type;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public boolean isxRelative() {
		return xRelative;
	}

	public void setxRelative(boolean xRelative) {
		this.xRelative = xRelative;
	}

	public boolean isyRelative() {
		return yRelative;
	}

	public void setyRelative(boolean yRelative) {
		this.yRelative = yRelative;
	}

	public double getOffsetX() {
		return offsetX;
	}

	public void setOffsetX(double offsetX) {
		this.offsetX = offsetX;
	}

	public double getOffsetY() {
		return offsetY;
	}

	public void setOffsetY(double offsetY) {
		this.offsetY = offsetY;
	}

	public boolean isOffsetXRelative() {
		return offsetXRelative;
	}

	public void setOffsetXRelative(boolean offsetXRelative) {
		this.offsetXRelative = offsetXRelative;
	}

	public boolean isOffsetYRelative() {
		return offsetYRelative;
	}

	public void setOffsetYRelative(boolean offsetYRelative) {
		this.offsetYRelative = offsetYRelative;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public boolean isWidthRelative() {
		return widthRelative;
	}

	public void setWidthRelative(boolean widthRelative) {
		this.widthRelative = widthRelative;
	}

	public boolean isHeightRelative() {
		return heightRelative;
	}

	public void setHeightRelative(boolean heightRelative) {
		this.heightRelative = heightRelative;
	}

	public Color getFill() {
		return fill;
	}

	public void setFill(Color fill) {
		this.fill = fill;
	}

	public Color getStroke() {
		return stroke;
	}

	public void setStroke(Color stroke) {
		this.stroke = stroke;
	}

	public double getFillOpacity() {
		return fillOpacity;
	}

	public void setFillOpacity(double fillOpacity) {
		this.fillOpacity = fillOpacity;
	}

	public double getStrokeOpacity() {
		return strokeOpacity;
	}

	public void setStrokeOpacity(double strokeOpacity) {
		this.strokeOpacity = strokeOpacity;
	}

	public double getStrokeWidth() {
		return strokeWidth;
	}

	public void setStrokeWidth(double strokeWidth) {
		this.strokeWidth = strokeWidth;
	}

	public Texture getTexture() {
		return texture;
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
	}

	public boolean isRecompile() {
		return recompile;
	}

	public void setRecompile(boolean recompile) {
		this.recompile = recompile;
	}

	public ArrayList<PropertyMap> getPropertyMaps() {
		return propertyMaps;
	}

	public void setPropertyMaps(ArrayList<PropertyMap> propertyMaps) {
		this.propertyMaps = propertyMaps;
	}

	public Shape2D getAttachedShape() {
		return attachedShape;
	}

	public void setAttachedShape(Shape2D attachedShape) {
		this.attachedShape = attachedShape;
	}

	public Positioning getPositioning() {
		return positioning;
	}

	public void setPositioning(Positioning positioning) {
		this.positioning = positioning;
	}

	public ArrayList<double[]> getPointList() {
		return pointList;
	}

	public void setPointList(ArrayList<double[]> pointList) {
		this.pointList = pointList;
	}

	public String getTextureFilename() {
		return textureFilename;
	}

	public void setTextureFilename(String textureFilename) {
		this.textureFilename = textureFilename;
	}

	public int getDisplayList() {
		return displayList;
	}

	public void setDisplayList(int displayList) {
		this.displayList = displayList;
	}

	public double getNumberOfPoints() {
		return numberOfPoints;
	}

	public void setNumberOfPoints(double numberOfPoints) {
		this.numberOfPoints = numberOfPoints;
	}

	public boolean isParsePointString() {
		return parsePointString;
	}

	public void setParsePointString(boolean parsePointString) {
		this.parsePointString = parsePointString;
	}

	public double getRelativeWidth() {
		return relativeWidth;
	}

	public void setRelativeWidth(double relativeWidth) {
		this.relativeWidth = relativeWidth;
	}

	public double getRelativeHeight() {
		return relativeHeight;
	}

	public void setRelativeHeight(double relativeHeight) {
		this.relativeHeight = relativeHeight;
	}

	public double getRelativeX() {
		return relativeX;
	}

	public void setRelativeX(double relativeX) {
		this.relativeX = relativeX;
	}

	public double getRelativeY() {
		return relativeY;
	}

	public void setRelativeY(double relativeY) {
		this.relativeY = relativeY;
	}
	
	public double getObjectHeight() {
	  return SpaceView.getObjectHeight();
	}
	
	public double getAngle() {
    return angle;
  }

  public void setAngle(double angle) {
    this.angle = angle;
  }
	
	public double getStartAngle() {
    return startAngle;
  }

  public void setStartAngle(double startAngle) {
    this.startAngle = startAngle;
  }
  
  public double getEndAngle() {
    return endAngle;
  }

  public void setEndAngle(double endAngle) {
    this.endAngle = endAngle;
    this.useEndAngle = true;
  }
}
