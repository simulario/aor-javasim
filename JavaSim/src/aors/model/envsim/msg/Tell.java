/*************************************************************************************************************
 * AgentObject-Object-Relationship (AOR) Simulation
 * Ontologically well-founded and closer-to-reality simulation of complex discrete event scenarios
 *
 * AOR-JSim v.2
 *
 * Copyright (C) 2008 AOR Team: Daniel Draeger, Adrian Giurca, Emilian Pascalau, Andreas Post, Marco Pehla,
 * Gerd Wagner, Jens Werner
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
 * 
 * File: Tell.java
 * 
 * Package: aors.model.envsim.msg
 *
 **************************************************************************************************************/
package aors.model.envsim.msg;

import java.util.HashMap;

import aors.model.Message;

/**
 * Tell
 * 
 * A standard Tell message type used for agents communication.
 * 
 * @author Ion-Mircea Diaconescu
 * @since March 23, 2009
 * @version $Revision: 1.0 $
 */
public class Tell extends Message {
  /**
   * The infoSet send via this message This is a Map where the key is the belief
   * ID and the value is a set of (property, value) pairs
   **/
  private HashMap<Long, HashMap<String, Object>> infoSet;

  /** the type of belief entity type we tell about **/
  private String beliefEntityType;

  /**
   * Default constructor
   */
  public Tell() {
    super();
    this.infoSet = new HashMap<Long, HashMap<String, Object>>();
  }

  /**
   * Constructor with ID parameter
   * 
   * @param id
   *          the ID of this message
   */
  public Tell(long id) {
    super(id);
    this.infoSet = new HashMap<Long, HashMap<String, Object>>();
  }

  /**
   * Constructor with ID parameter
   * 
   * @param id
   *          the ID of this message
   * @param name
   *          the name of this message
   */
  public Tell(long id, String name) {
    super(id, name);
    this.infoSet = new HashMap<Long, HashMap<String, Object>>();
  }

  /**
   * Return the info set
   * 
   * @return the infoSet
   */
  public HashMap<Long, HashMap<String, Object>> getInfoSet() {
    return infoSet;
  }

  /**
   * Return an info for a given belief ID
   * 
   * @param beliefId
   *          the beliefId
   * @return (property,value) pairs for the belief with the given ID
   */
  public HashMap<String, Object> getInfoByBeliefEntityId(long beliefId) {
    return this.infoSet.get(beliefId);
  }

  /**
   * 
   * @param beliefId
   *          the id of the belief entity
   * @param propName
   *          the property name
   * @return the value of that property
   */
  public Object getPropValueByBeliefEntityId(long beliefId, String propName) {
    return this.infoSet.get(beliefId).get(propName);
  }

  /**
   * Set the (property, value) pairs for an ID
   * 
   * @param info
   *          the data to set for this ID
   */
  public void setInfoForBeliefEntityId(long beliefId,
      HashMap<String, Object> info) {
    this.infoSet.put(beliefId, info);
  }

  /**
   * Set the infoSet.
   * 
   * @param infoSet
   *          the new infoSet
   */
  public void setInfoSet(HashMap<Long, HashMap<String, Object>> infoSet) {
    this.infoSet = infoSet;
  }

  /**
   * Add a given (property,value) pair for a given belief ID If the Id not
   * exist, then it is created a new entry int the info set
   * 
   * @param beliefId
   *          the Id of the wanted belief
   * @param propName
   *          the name of the property
   * @param value
   *          the value for that property
   */
  public void addPropValueForBeliefEntityId(long beliefId, String propName,
      Object value) {
    if (this.infoSet.get(beliefId) != null) {
      this.infoSet.get(beliefId).put(propName, value);
    } else {
      HashMap<String, Object> info = new HashMap<String, Object>();
      info.put(propName, value);

      this.infoSet.put(beliefId, info);
    }
  }

  /**
   * @return the beliefEntityType
   */
  public String getBeliefEntityType() {
    return beliefEntityType;
  }

  /**
   * @param beliefEntityType
   *          the beliefEntityType to set
   */
  public void setBeliefEntityType(String beliefEntityType) {
    this.beliefEntityType = beliefEntityType;
  }
}
