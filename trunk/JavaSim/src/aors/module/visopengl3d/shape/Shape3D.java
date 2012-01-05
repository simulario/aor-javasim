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
import aors.module.visopengl3d.space.view.PropertyMap;
import aors.module.visopengl3d.utility.Color;

import com.sun.opengl.util.texture.Texture;

/**
 * The Shape3D class is the base class for all three dimensional shapes.
 * 
 * @author Susanne Schölzel
 * @since November 25th, 2010
 * 
 */
public abstract class Shape3D {
	
	// String constant for the "PhysicalShape3D" node
	public static final String PHYSICAL_SHAPE_3D = "PhysicalShape3D";

	// String constant for the "Shape3D" node
	public static final String SHAPE_3D = "Shape3D";

	// String constants for the actual shape nodes
	public static final String CUBE = "Cube";
	public static final String CUBOID = "Cuboid";
	public static final String CONE = "Cone";
	public static final String CYLINDER = "Cylinder";
	public static final String MESH = "Mesh";
	public static final String PYRAMID = "Pyramid";
	public static final String REGULAR_TRIANGULAR_PRISM = "RegularTriangularPrism";
	public static final String SPHERE = "Sphere";
	public static final String TETRAHEDRA = "Tetrahedra";

	// String constants for shape attributes
	public static final String FILL = "fill";
	public static final String FILL_RGB = "fillRGB";
	public static final String FILL_OPACITY = "fillOpacity";
	public static final String TEXTURE = "texture";
	public static final String FILE = "file";

	// String constants for attributes of non-physical object shapes
	public static final String X = "x";
	public static final String Y = "y";
	public static final String Z = "z";
	public static final String OFFSET_X = "offsetX";
	public static final String OFFSET_Y = "offsetY";
	public static final String OFFSET_Z = "offsetZ";
	public static final String ROT_X = "rotX";
	public static final String ROT_Y = "rotY";
	public static final String ROT_Z = "rotZ";
	public static final String WIDTH = "width";
	public static final String HEIGHT = "height";
	public static final String DEPTH = "depth";
	public static final String R = "r";

	// Shape type
	protected ShapeType type;

	// Position
	protected double x, y, z;
	protected boolean xRelative, yRelative, zRelative;
	protected double relativeX, relativeY, relativeZ;

	// Offset
	protected double offsetX, offsetY, offsetZ;
	protected boolean offsetXRelative, offsetYRelative, offsetZRelative;

	// Rotation
	protected double rotX, rotY, rotZ;
	
	// Dimensions
	protected double width, height, depth;
	protected boolean widthRelative, heightRelative, depthRelative;
	protected double relativeWidth, relativeHeight, relativeDepth;

	// Color
	protected Color fill = Color.BLACK;

	// Opacity
	protected double fillOpacity = 1;

	// Texture
	protected Texture texture;
	protected String textureFilename;
	
	//Mesh
	protected String meshFilename;

	// Embedded shape
	protected Shape3D attachedShape;

	// List of associated shape property maps
	protected ArrayList<PropertyMap> propertyMaps;

	// Flag indicating that the display list needs to be recompiled
	protected boolean recompile;
	
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
	 * Normalizes a vector by dividing its components through its length
	 * 
	 * @param v
	 * 			vector, which should be normalized, as an array
	 */
	public void normalize(double[] v) {
		double length = Math.sqrt(v[0]*v[0] + v[1]*v[1] + v[2]*v[2]);
		v[0] /= length;
		v[1] /= length;
		v[2] /= length;
	}
	
	/**
	 * Calculates the vertices lying on the shapes contour. Either the contour of
	 * the whole shape (outShape) or the contour of a smaller version of the shape
	 * (inShape), that is used to apply a border around the shape. If inContour is
	 * null only the outer contour will be calculated.
	 * 
	 * @param outContour
	 *          List storing vertices of the shapes contour.
	 * @param inContour
	 *          List storing vertices of the shapes border.
	 */
//	protected abstract void calculateContour(ArrayList<double[]> outContour,
//			ArrayList<double[]> inContour);
	
	/**
	 * Applies a color to each vertex in a list of vertices.
	 * 
	 * @param contour
	 *          List of vertices.
	 * @param color
	 *          Color that will be applied to each vertex.
	 */
	/*
	protected void applyColor(ArrayList<double[]> contour, Color color) {
		for (double[] vertex : contour) {
			// Set the RGBA components
			vertex[3] = color.getRed();
			vertex[4] = color.getGreen();
			vertex[5] = color.getBlue();
			vertex[6] = color.getAlpha();
		}
	}
  */
	
	/**
	 * Performs a mapping from object coordinates into texture coordinates. If the
	 * second parameter equals null texture coordinates will be set to 0.
	 * 
	 * @param contour
	 *          List storing vertices.
	 * @param tc
	 *          tImage coordinates of the texture image.
	 */
	//protected void applyTexture(ArrayList<double[]> contour, TextureCoords tc) {
		/*
		 * Initialize the texture coordinates to 0 if no image coordinates are
		 * available.
		 */
	/*	if (tc == null) {
			for (double[] vertex : contour) {
				vertex[7] = 0;
				vertex[8] = 0;
			}
		} else {
			// Maximal and minimal extensions of the polygon
			double xMin = contour.get(0)[0];
			double xMax = contour.get(0)[0];
			double yMin = contour.get(0)[1];
			double yMax = contour.get(0)[1];

			// Loop over all vertices to find the maxima and minima
			for (double[] vertex : contour) {
				if (vertex[0] < xMin)
					xMin = vertex[0];

				if (vertex[0] > xMax)
					xMax = vertex[0];

				if (vertex[1] < yMin)
					yMin = vertex[1];

				if (vertex[1] > yMax)
					yMax = vertex[1];
			}

			// Dimensions of the polygon
			double width = xMax - xMin;
			double height = yMax - yMin;

			// Texture coordinates
			double s = 0, t = 0;

			// Loop over all vertices to find the appropriate texture
			// coordinates
			for (double[] vertex : contour) {
				s = tc.left() + (width - (xMax - vertex[0])) / width;
				t = tc.bottom() - (height - (yMax - vertex[1])) / height;

				vertex[7] = s;
				vertex[8] = t;
			}
		}
	}*/

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

/*			else if (shapeProperty.equals(STROKE) || shapeProperty.equals(STROKE_RGB)) {
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
*/
			else if (shapeProperty.equals(FILL_OPACITY)) {
				// Only update if the value has really changed
				if (fillOpacity != Double.valueOf(value)) {
					fillOpacity = Double.valueOf(value);
					recompile = true;
				}
			}
/*
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
*/
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
/*
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
*/
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
	
	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
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
	
	public boolean iszRelative() {
		return zRelative;
	}

	public void setzRelative(boolean zRelative) {
		this.zRelative = zRelative;
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
	
	public double getRelativeZ() {
		return relativeZ;
	}

	public void setRelativeZ(double relativeZ) {
		this.relativeZ = relativeZ;
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
	
	public double getOffsetZ() {
		return offsetZ;
	}

	public void setOffsetZ(double offsetZ) {
		this.offsetZ = offsetZ;
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
	
	public boolean isOffsetZRelative() {
		return offsetZRelative;
	}

	public void setOffsetZRelative(boolean offsetZRelative) {
		this.offsetZRelative = offsetZRelative;
	}

	public double getRotX() {
		return rotX;
	}

	public void setRotX(double rotX) {
		this.rotX = rotX;
	}

	public double getRotY() {
		return rotY;
	}

	public void setRotY(double rotY) {
		this.rotY = rotY;
	}
	
	public double getRotZ() {
		return rotZ;
	}

	public void setRotZ(double rotZ) {
		this.rotZ = rotZ;
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
	
	public double getDepth() {
		return depth;
	}

	public void setDepth(double depth) {
		this.depth = depth;
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
	
	public boolean isDepthRelative() {
		return depthRelative;
	}

	public void setDepthRelative(boolean depthRelative) {
		this.depthRelative = depthRelative;
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
	
	public double getRelativeDepth() {
		return relativeDepth;
	}

	public void setRelativeDepth(double relativeDepth) {
		this.relativeDepth = relativeDepth;
	}

	public Color getFill() {
		return fill;
	}

	public void setFill(Color fill) {
		this.fill = fill;
	}

	public double getFillOpacity() {
		return fillOpacity;
	}

	public void setFillOpacity(double fillOpacity) {
		this.fillOpacity = fillOpacity;
	}

	public Texture getTexture() {
		return texture;
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
	}
	
	public String getTextureFilename() {
		return textureFilename;
	}

	public void setTextureFilename(String textureFilename) {
		this.textureFilename = textureFilename;
	}
	
	public String getMeshFilename() {
		return meshFilename;
	}

	public void setMeshFilename(String meshFilename) {
		this.meshFilename = meshFilename;
	}
	
	public Shape3D getAttachedShape() {
		return attachedShape;
	}

	public void setAttachedShape(Shape3D attachedShape) {
		this.attachedShape = attachedShape;
	}

	public ArrayList<PropertyMap> getPropertyMaps() {
		return propertyMaps;
	}

	public void setPropertyMaps(ArrayList<PropertyMap> propertyMaps) {
		this.propertyMaps = propertyMaps;
	}

	public boolean isRecompile() {
		return recompile;
	}

	public void setRecompile(boolean recompile) {
		this.recompile = recompile;
	}
	
	public int getDisplayList() {
		return displayList;
	}

	public void setDisplayList(int displayList) {
		this.displayList = displayList;
	}
}
