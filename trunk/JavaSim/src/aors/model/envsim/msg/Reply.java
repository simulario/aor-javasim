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
 * File: Relpy.java
 * 
 * Package: aors.model.envsim.msg
 *
 **************************************************************************************************************/
package aors.model.envsim.msg;

import java.util.HashMap;

import aors.model.Message;

/**
 * Reply
 * 
 * A standard Reply message type used for agents communication.
 * 
 * @author Ion-Mircea Diaconescu
 * @since March 23, 2009
 * @version $Revision: 1.0 $
 */
public class Reply extends Message {
  /**
   * The answerSet send via this message This is a Map where the key is the
   * belief ID and the value is a set of (property, value) pairs
   **/
  private HashMap<Long, HashMap<String, Object>> answerSet;

  /** the type of belief entity type we reply about **/
  private String beliefEntityType;

  /**
   * Default constructor
   */
  public Reply() {
    super();
    this.answerSet = new HashMap<Long, HashMap<String, Object>>();
  }

  /**
   * Constructor with ID parameter
   * 
   * @param id
   *          the ID of this message
   */
  public Reply(long id) {
    super(id);
    this.answerSet = new HashMap<Long, HashMap<String, Object>>();
  }

  /**
   * Constructor with ID parameter
   * 
   * @param id
   *          the ID of this message
   * @param name
   *          the name of this message
   */
  public Reply(long id, String name) {
    super(id, name);
    this.answerSet = new HashMap<Long, HashMap<String, Object>>();
  }

  /**
   * Return the answer set
   * 
   * @return the answerSet
   */
  public HashMap<Long, HashMap<String, Object>> getAnswerSet() {
    return answerSet;
  }

  /**
   * Return an answer for a given belief ID
   * 
   * @param beliefId
   *          the beliefId
   * @return (property,value) pairs for the belief with the given ID
   */
  public HashMap<String, Object> getAnswerByBeliefEntityId(long beliefId) {
    return this.answerSet.get(beliefId);
  }

  public Object getPropValueByBeliefEntityId(long beliefId, String propName) {
    return this.answerSet.get(beliefId).get(propName);
  }

  /**
   * Set the (property, value) pairs for an ID
   * 
   * @param answer
   *          the data to set for this ID
   */
  public void setAnswerForBeliefEntityId(long beliefId,
      HashMap<String, Object> answer) {
    this.answerSet.put(beliefId, answer);
  }

  /**
   * Add a given (property,value) pair for a given belief ID If the Id not
   * exist, then it is created a new entry int the answer set
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
    if (this.answerSet.get(beliefId) != null) {
      this.answerSet.get(beliefId).put(propName, value);
    } else {
      HashMap<String, Object> answer = new HashMap<String, Object>();
      answer.put(propName, value);

      this.answerSet.put(beliefId, answer);
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
