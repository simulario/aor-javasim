/**
 * 
 */
package aors.module.physics2d.util;

import aors.model.envsim.MaterialType;

/**
 * This class provides static methods to retrieve certain material constants
 * like friction or restitution.
 * 
 * @author Holger Wuerke
 * @since 17.01.2010
 */
public class MaterialConstants {

  /**
   * Returns the restitution of the given material type.
   * 
   * @param materialType
   * @return the restitution of the given material type
   */
  public static double restitution(MaterialType materialType) {
    if (materialType == null) {
      return 0.5; // default
    }

    switch (materialType) {
    case Aluminium:
      return 0.65;

    case Iron:
      return 0.60;

    case Rubber:
      return 0.83;

    case Stone:
      return 0.3;

    case Wood:
      return 0.60;
    }

    return 0.5;
  }

  /**
   * Returns the friction of the given material type.
   * 
   * @param materialType
   * @return the friction of the given material type
   */
  public static double friction(MaterialType materialType) {
    if (materialType == null) {
      return 1; // default
    }

    switch (materialType) {
    case Aluminium:
      return 1.0;

    case Iron:
      return 0.8;

    case Rubber:
      return 5;

    case Stone:
      return 0.2;

    case Wood:
      return 0.6;
    }

    return 1;
  }
}
