/*************************************************************************************************************
 * AgentObject-Object-Relationship (AOR) Simulation
 * Ontologically well-founded and closer-to-reality simulation of complex discrete event scenarios
 *
 * AOR-JSim v.2
 *
 * Copyright (C) 2008 AOR Team: Daniel Draeger, Adrian Giurca, Emilian Pascalau, Andreas Post, Marco Pehla,
 * Gerd Wagner, Jens Werner, Mircea Diaconescu
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 **************************************************************************************************************/

package aors.module.sound;

/**
 * SonificationMap
 * 
 * @author Andreas Freier (business.af@web.de), Mircea Diaconescu
 * @date April 02, 2009
 * @version $Revision$
 */
public class SonificationMap {
  private final int INSTRUMENT_MAX = 127;
  private final int NOTE_MAX = 127;
  private final int VOLUME_MAX = 127;

  private String propertyName = "";
  private String soundPropertyName = "";
  private String mapType = SoundController.MAP_TYPE_POLYNOMIAL;
  private Object a0;
  private Object a1;
  private Object a2;
  private Object a3;
  private int v0;
  private int v1;
  private int v2;
  private int v3;
  private int v4;
  private int[][] defaultNotes;
  private int defaultInstrument;

  // the total duration of the sound
  private long totalDuration = 0;

  // special parameter that defines the acting way of sonification map changes.
  // If true then the changes starts always from the value defined by the user
  // in the XML.
  private boolean useCloneMapValues = true;

  /**
   * Create a new sonification map object
   * 
   * @param defaultNotes
   *          a backup of the notes before any modifications that may occurs
   * @param defaultInstrument
   *          the instrument used before any modifications that may occurs
   * @param propertyName
   *          the property that activate this sonification map
   * @param soundPropertyName
   *          the sound property that is affected by the somification map
   * @param mapType
   *          the type of transformation that may occurs
   * @param a0
   *          the value of a0
   * @param a1
   *          the value of a1
   * @param a2
   *          the value of a2
   * @param a3
   *          the value of a3
   * @param v0
   *          the value of v0
   * @param v1
   *          the value of v1
   * @param v2
   *          the value of v2
   * @param v3
   *          the value of v3
   * @param v4
   *          the value of v4
   */
  public SonificationMap(int[][] defaultNotes, int defaultInstrument,
      long totalDuration, boolean useCloneMapValues, String propertyName,
      String soundPropertyName, String mapType, Object a0, Object a1,
      Object a2, Object a3, int v0, int v1, int v2, int v3, int v4) {

    this.defaultNotes = this.cloneArray(defaultNotes);
    this.defaultInstrument = defaultInstrument;
    this.propertyName = propertyName;
    this.soundPropertyName = soundPropertyName;
    this.mapType = mapType;
    this.totalDuration = totalDuration;
    this.useCloneMapValues = useCloneMapValues;
    this.a0 = a0;
    this.a1 = a1;
    this.a2 = a2;
    this.a3 = a3;
    this.v0 = v0;
    this.v1 = v1;
    this.v2 = v2;
    this.v3 = v3;
    this.v4 = v4;

  }

  /**
   * Gets the activator property name
   * 
   * @return the name of the property that activate the sonification map
   */
  public String getPropertyName() {
    return propertyName;
  }

  /**
   * Gets the sound property name that will be modified by the sonification map
   * 
   * @return the sound property name that is affected by the sonification map
   */
  public String getSoundPropertyName() {
    return soundPropertyName;
  }

  /**
   * Change notes
   * 
   * @param property
   *          the property value that affect the notes values
   * @return the new notes array
   */
  public int[][] mapNotes(Object property) {
    int[][] result;

    if (useCloneMapValues) {
      result = this.cloneArray(defaultNotes.clone());
    } else {
      result = defaultNotes;
    }

    if (property == null) {
      return result;
    }

    int res = 0;
    try {
      res = computeMapValue(property, a0, a1, a2, a3, v0, v1, v2, v3, v4);
    } catch (Exception e) {
      return result;
    }

    for (int i = 0; i < result[0].length; i++) {
      result[0][i] = defaultNotes[0][i] + res;

      // out of range note - reset to the max possible note
      if (result[0][i] > NOTE_MAX) {
        result[0][i] = NOTE_MAX;
      }
    }

    return result;
  }

  /**
   * Change the notes duration
   * 
   * @param property
   *          the property value that affects the notes duration
   * @return the new notes array
   */
  public int[][] mapDuration(Object property) {
    int[][] result;

    if (useCloneMapValues) {
      result = this.cloneArray(defaultNotes.clone());
    } else {
      result = defaultNotes;
    }

    double fact = this.computeDurationFactor(this.totalDuration, result);

    if (property == null) {
      for (int i = 0; i < result[1].length; i++) {
        result[1][i] = (int) (((double) defaultNotes[1][i]) * fact);
      }
      return result;
    }

    int res = 1;
    try {
      res = computeMapValue(property, a0, a1, a2, a3, v0, v1, v2, v3, v4);
    } catch (Exception e) {
      for (int i = 0; i < result[1].length; i++) {
        result[1][i] = (int) (((double) defaultNotes[1][i]) * fact);
      }
      return result;
    }

    fact = this.computeDurationFactor(res, result);
    for (int i = 0; i < result[1].length; i++) {
      result[1][i] = (int) (((double) defaultNotes[1][i]) * fact);
    }

    return result;
  }

  /**
   * Compute the multiplication factor for each note duration
   * 
   * @param totalSoundDuration
   *          the amount of required total duration of all sounds
   * @param notes
   *          the notes array
   * @return the duration multiplication factor for each note
   */
  private double computeDurationFactor(long totalSoundDuration, int[][] notes) {
    double count = 0;
    for (int i = 0; i < notes[1].length; i++) {
      count += notes[1][i];
    }

    if (count == 0) {
      count = 1;
    }

    return totalSoundDuration / count;
  }

  /**
   * Change the sound volume for the sound that has assigned this sonification
   * map to it
   * 
   * @param property
   *          the property value that will affect the notes volume
   * 
   * @return the new notes array
   */
  public int[][] mapVolume(Object property) {
    int[][] result;

    if (useCloneMapValues) {
      result = this.cloneArray(defaultNotes.clone());
    } else {
      result = defaultNotes;
    }

    if (property == null) {
      return result;
    }
    int res = 0;
    try {
      res = computeMapValue(property, a0, a1, a2, a3, v0, v1, v2, v3, v4);
    } catch (Exception e) {
      return result;
    }
    for (int i = 0; i < result[0].length; i++) {
      result[2][i] = this.defaultNotes[2][i] * res;
      if (result[2][i] > VOLUME_MAX) {
        result[2][i] = VOLUME_MAX;
      }
    }

    return result;
  }

  /**
   * Compute the instrument for the given property value
   * 
   * @param property
   *          the property value that affects the computation of the instrument
   * @return the computed instrument
   */
  public int mapInstrument(Object property) {
    int instrument = 0;
    try {
      instrument = computeMapValue(property, a0, a1, a2, a3, v0, v1, v2, v3, v4);
    } catch (Exception e) {
      if (useCloneMapValues) {
        this.defaultInstrument = instrument;
      }
      return this.defaultInstrument;
    }

    if (instrument <= INSTRUMENT_MAX) {
      if (useCloneMapValues) {
        this.defaultInstrument = instrument;
      }
      return instrument;
    } else {
      if (useCloneMapValues) {
        this.defaultInstrument = INSTRUMENT_MAX;
      }
      return INSTRUMENT_MAX;
    }
  }

  /**
   * Compute the map value based on the given parameters
   * 
   * @param propertyValue
   *          the property value
   * @param a0
   *          a0 value
   * @param a1
   *          a1 value
   * @param a2
   *          a2 value
   * @param a3
   *          a3 value
   * @param v0
   *          v0 value
   * @param v1
   *          v1 value
   * @param v2
   *          v2 value
   * @param v3
   *          v3 value
   * @param v4
   *          v4 value
   * @return the corresponding value to be mapped
   */
  @SuppressWarnings("unchecked")
  private int computeMapValue(Object propertyValue, Object a0, Object a1,
      Object a2, Object a3, int v0, int v1, int v2, int v3, int v4) {

    int result = 0;

    if (this.mapType.equals(SoundController.MAP_TYPE_POLYNOMIAL)) {

      double prop = Double.parseDouble(propertyValue.toString());
      // polynomial
      result = Math.round((long) (prop
          * (int) (Math.pow(Double.parseDouble(a3.toString()), 3)) + prop
          * (int) (Math.pow(Double.parseDouble(a2.toString()), 2)) + prop
          * Double.parseDouble(a1.toString()) + Double.parseDouble(a0
          .toString())));
    } else if (this.mapType.equals(SoundController.MAP_TYPE_CASEWISE)) {
      // casewise
      int prop = (int) Double.parseDouble(propertyValue.toString());
      if (prop < Double.parseDouble(a0.toString())) {
        result = v0;
      } else if (Double.parseDouble(a0.toString()) <= prop
          && prop < Double.parseDouble(a1.toString())) {
        result = v1;
      } else if (Double.parseDouble(a1.toString()) <= prop
          && prop < Double.parseDouble(a2.toString())) {
        result = v2;
      } else if (Double.parseDouble(a2.toString()) <= prop
          && prop < Double.parseDouble(a3.toString())) {
        result = v3;
      } else {
        result = v4;
      }
    } else if (this.mapType.equals(SoundController.MAP_TYPE_EQUALITY_CASE_WISE)) {
      // equalityCaseWise
      if (propertyValue.equals(a0)) {
        result = v0;
      } else if (propertyValue.equals(a1)) {
        result = v1;
      } else if (propertyValue.equals(a2)) {
        result = v2;
      } else if (propertyValue.equals(a3)) {
        result = v3;
      } else {
        result = v4;
      }
    } else if (this.mapType.equals(SoundController.MAP_TYPE_ENUM_MAP)) {
      // enumMap
      Enum prop = (Enum) propertyValue;

      if (prop.ordinal() == 0) {
        result = v0;
      } else if (prop.ordinal() == 1) {
        result = v1;
      } else if (prop.ordinal() == 2) {
        result = v2;
      } else if (prop.ordinal() == 3) {
        result = v3;
      } else if (prop.ordinal() == 4) {
        result = v4;
      }
    }

    return result;
  }

  /**
   * Utility function user to clone a 2 dimension array. This is required while
   * the multidimensional arrays can't be cloned using clone().
   * 
   * @param sourceArray
   *          the array that needs to be cloned
   * @return a clone of the given source array
   */
  protected int[][] cloneArray(int[][] sourceArray) {
    int[][] result = new int[3][];

    result[0] = sourceArray[0].clone();
    result[1] = sourceArray[1].clone();
    result[2] = sourceArray[2].clone();

    return result;
  }

  @Override
  public String toString() {
    String result = "PropName:" + this.propertyName + "  SoundPropName:"
        + this.soundPropertyName + "  FuncType:" + this.mapType + "  a0:"
        + this.a0 + "  a1:" + this.a1 + "  a2:" + this.a2 + "  a3:" + this.a3
        + "  v0:" + this.v0 + "  v1:" + this.v1 + "  v2:" + this.v2 + "  v3:"
        + this.v3 + "  v4:" + this.v4;

    return result;
  }

  /**
   * Debug method...
   * 
   * @param array
   */
  @SuppressWarnings("unused")
  private void printArray(int[][] array) {
    for (int k1 = 0; k1 < 3; k1++) {
      for (int k2 = 0; k2 < array[k1].length; k2++) {
        System.out.print("  " + array[k1][k2]);
      }
      System.out.println();
    }
  }
}
