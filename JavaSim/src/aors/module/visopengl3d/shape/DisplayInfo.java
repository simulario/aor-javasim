package aors.module.visopengl3d.shape;

import java.lang.reflect.Field;
import java.text.NumberFormat;

import javax.media.opengl.GL2;

import aors.logger.model.AgtType;
import aors.logger.model.ObjType;
import aors.logger.model.ObjectType;
import aors.logger.model.SlotType;
import aors.model.envsim.Objekt;
import aors.model.envsim.Physical;

import com.sun.opengl.util.gl2.GLUT;

/**
 * Class that handles the display of information about objects.
 * 
 * @author Sebastian Mucha; sUSANNE sCHÖLZEL
 * @since March 28th, 2010
 * 
 */
public class DisplayInfo {

  // DisplayInfo node name and attribute names
  public static final String DISPLAY_INFO = "DisplayInfo";
  public static final String DISPLAY_NAME = "displayName";
  public static final String DISPLAY_ID = "displayID";
  public static final String CONTENT = "content";
  public static final String PROPERTY = "property";

  // Flags indicating if ID and name should be displayed and if the text is
  // visible
  private boolean displayID;
  private boolean displayName;
  private boolean enabled;

  // Text coordinates
  private double x, y, z;

  // Values
  private String id;
  private String name;
  private String content;
  private String property;
  private String propertyValue;

  // Number format
  private NumberFormat format = NumberFormat.getInstance();

  /**
   * Displays the text.
   * 
   * @param gl
   * @param glut
   */
  public void display(GL2 gl, GLUT glut) {
    gl.glColor3d(0, 0, 0);

    gl.glPushMatrix();

    if ((displayName && name != null) &&
    		(displayID && id != null) &&
    		(content != null || propertyValue != null)) {
    	y += 24;
    }
    
    else if ((displayName && name != null) &&
    		(displayID && id != null)) {
    	y += 12;
    }
    
    else if ((displayName && name != null) &&
    		(content != null || propertyValue != null)) {
    	y += 12;
    }
    
    else if ((displayID && id != null) &&
    		(content != null || propertyValue != null)) {
    	y += 12;
    }

    if (displayName && name != null) {
      gl.glRasterPos3d(x, y, z);
      glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, name);
      y -= 12;
    }
    
    if (displayID && id != null) {
      gl.glRasterPos3d(x, y, z);
      glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, "ID: " + id);
      y -= 12;
    }

    if (content != null && propertyValue != null) {
      gl.glRasterPos3d(x, y, z);
      glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, propertyValue + " "
          + content);
      y -= 12;
    }

    else if (content != null && propertyValue == null) {
      gl.glRasterPos3d(x, y, z);
      glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, content);
      y -= 12;
    }

    else if (content == null && propertyValue != null) {
      gl.glRasterPos3d(x, y, z);
      glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, propertyValue);
      y -= 12;
    }

    gl.glPopMatrix();
  }

	/**
   * Applies the property value for the initial state.
   * 
   * @param obj
   */
  public void applyProperty(Objekt obj) {
    if (property != null) {
      try {
        // Get the property value through reflection
        Field propertyField = obj.getClass().getDeclaredField(property);
        propertyField.setAccessible(true);

        // Apply the property value
        propertyValue = propertyField.get(obj).toString();

      } catch (SecurityException e) {
        e.printStackTrace();
      } catch (NoSuchFieldException e) {
        return;
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Applies property values for physical attributes.
   * 
   * @param phy
   */
  public void applyProperty(Physical phy) {
    if (property != null) {
      // Display only one digit behind the comma
      format.setMaximumFractionDigits(1);
      format.setGroupingUsed(false);

      if (property.equals("x"))
        propertyValue = format.format(phy.getX());

      else if (property.equals("y"))
        propertyValue = format.format(phy.getY());

      else if (property.equals("z"))
        propertyValue = format.format(phy.getZ());

      else if (property.equals("rotationAngleX"))
        propertyValue = format.format(phy.getRotX());

      else if (property.equals("rotationAngleY"))
        propertyValue = format.format(phy.getRotY());

      else if (property.equals("rotationAngleZ"))
        propertyValue = format.format(phy.getRotZ());

      else if (property.equals("vx"))
        propertyValue = format.format(phy.getVx());

      else if (property.equals("vy"))
        propertyValue = format.format(phy.getVy());

      else if (property.equals("vz"))
        propertyValue = format.format(phy.getVz());

      else if (property.equals("ax"))
        propertyValue = format.format(phy.getAx());

      else if (property.equals("ay"))
        propertyValue = format.format(phy.getAy());

      else if (property.equals("az"))
        propertyValue = format.format(phy.getAz());

      else if (property.equals("omegaX"))
        propertyValue = format.format(phy.getOmegaX());

      else if (property.equals("omegaY"))
        propertyValue = format.format(phy.getOmegaY());

      else if (property.equals("omegaZ"))
        propertyValue = format.format(phy.getOmegaZ());

      else if (property.equals("alphaX"))
        propertyValue = format.format(phy.getAlphaX());

      else if (property.equals("alphaY"))
        propertyValue = format.format(phy.getAlphaY());

      else if (property.equals("alphaZ"))
        propertyValue = format.format(phy.getAlphaZ());

      else if (property.equals("m"))
        propertyValue = format.format(phy.getM());

      else if (property.equals("width"))
        propertyValue = format.format(phy.getWidth());

      else if (property.equals("height"))
        propertyValue = format.format(phy.getHeight());

      else if (property.equals("depth"))
        propertyValue = format.format(phy.getDepth());

      else if (property.equals("materialType"))
        propertyValue = phy.getMaterialType().name();
    }
  }

  /**
   * Applies the property value during runtime.
   * 
   * @param objectType
   */
  public void applyProperty(ObjectType objectType) {
    if (property != null) {
      for (SlotType slot : objectType.getSlot()) {
        if (slot.getProperty().equals(property)) {
          propertyValue = slot.getValue();
        }
      }
    }
  }

  /**
   * Applies the property value during runtime.
   * 
   * @param objectType
   */
  public void applyProperty(ObjType objType) {
    if (property != null) {
      for (SlotType slot : objType.getSlot()) {
        if (slot.getProperty().equals(property)) {
          propertyValue = slot.getValue();
        }
      }
    }
  }

  /**
   * Applies the property value during runtime.
   * 
   * @param objectType
   */
  public void applyProperty(AgtType agt) {
    if (property != null) {
      for (SlotType slot : agt.getSlot()) {
        if (slot.getProperty().equals(property)) {
          propertyValue = slot.getValue();
        }
      }
    }
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
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

  public boolean isDisplayID() {
    return displayID;
  }

  public void setDisplayID(boolean displayID) {
    this.displayID = displayID;
  }

  public boolean isDisplayName() {
    return displayName;
  }

  public void setDisplayName(boolean displayName) {
    this.displayName = displayName;
  }

  public String getProperty() {
    return property;
  }

  public void setProperty(String property) {
    this.property = property;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public String getPropertyValue() {
    return propertyValue;
  }

  public void setPropertyValue(String propertyValue) {
    this.propertyValue = propertyValue;
  }
}
