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
 *
 **************************************************************************************************************/
package aors.model.agtsim.beliefs.model;

/**
 * The ERDF Triple representation.
 * 
 * @author Mircea Diaconescu
 * @since September 29, 2009
 * @version $Revision$
 */

public class Triple {
  /**
   * The subject node of the triple.
   */
  private Node subject;

  /**
   * The property of the triple.
   */
  private Node property;

  /**
   * The object node of the triple
   */
  private Node object;

  /**
   * The certainty factor.
   */
  private float certaintyFactor;

  /**
   * Create an empty triple.
   * 
   * @throws InvalidNodeTypeException
   */
  public Triple() throws InvalidNodeTypeException {
    this(new Node(), new Node(), new Node(), 0.0f);
  }

  /**
   * Create a triple with maximum certainty factor.
   * 
   * @param subject
   *          the triple subject
   * @param property
   *          the triple property
   * @param object
   *          the triple object
   * @throws InvalidNodeTypeException
   *           throws exception while one of the node type does not fit with the
   *           required types.
   */
  public Triple(Node subject, Node property, Node object)
      throws InvalidNodeTypeException {

    this(subject, property, object, 1.0f);
  }

  /**
   * Create a triple with provided nodes and certainty factor.
   * 
   * @param subject
   *          the triple subject
   * @param property
   *          the triple property
   * @param object
   *          the triple object
   * @param certaintyFactor
   *          the certainty factor for this triple
   * @throws InvalidNodeTypeException
   *           throws exception while one of the node type does not fit with the
   *           required types.
   */
  public Triple(Node subject, Node property, Node object, float certaintyFactor)
      throws InvalidNodeTypeException {

    // the subject node of the triple can be only URIReference
    if (!(subject.getNodeValue() instanceof URIReference)) {
      throw new InvalidNodeTypeException(
          "The value of the subject node must be an URIReference! Found: "
              + subject.getNodeValue().getClass());
    }

    // the property node of the triple can be only URIReference
    if (!(property.getNodeValue() instanceof URIReference)) {
      throw new InvalidNodeTypeException(
          "The value of the property node must be an URIReference! Found: "
              + property.getNodeValue().getClass());
    }

    this.subject = subject;
    this.property = property;
    this.object = object;
    this.certaintyFactor = certaintyFactor;

  }

  /**
   * Set a new value for the subject of the triple.
   * 
   * @param subject
   *          the new value of the subject node as a Node.
   */
  public void setSubject(Node subject) {
    this.subject = subject;
  }

  /**
   * Gets the triple subject.
   * 
   * @return the subject of this triple.
   */
  public Node getSubject() {
    return this.subject;
  }

  /**
   * Set a new value for the property of the triple.
   * 
   * @param property
   *          the new value of the property.
   */
  public void setProperty(Node property) {
    this.property = property;
  }

  /**
   * Gets the triple property.
   * 
   * @return the property of this triple.
   */
  public Node getProperty() {
    return this.property;
  }

  /**
   * Set a new value for the object of the triple.
   * 
   * @param object
   *          the new value of the object node as a Node.
   */
  public void setObject(Node object) {
    this.object = object;
  }

  /**
   * Gets the triple object.
   * 
   * @return the object of this triple.
   */
  public Node getObject() {
    return this.object;
  }

  /**
   * Set the value of the certainty factor.
   * 
   * @param certaintyFactor
   *          the new value of the certainty factor.
   */
  public void setCertaintyFactor(float certaintyFactor) {
    if (certaintyFactor > 1.0) {
      this.certaintyFactor = 1.0f;
    } else {
      this.certaintyFactor = certaintyFactor;
    }
  }

  /**
   * Gets the value of the certainty factor.
   * 
   * @return the value of the certainty factor.
   */
  public float getCertaintyFactor() {
    return this.certaintyFactor;
  }

  @Override
  public boolean equals(Object triple) {
    if (!(triple instanceof Triple)) {
      return false;
    }

    Triple t = (Triple) triple;

    if (!this.subject.equals(t.getSubject())
        || !this.property.equals(t.getProperty())
        || !this.object.equals(t.getObject())) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return this.subject.hashCode();
  }

  @Override
  public String toString() {
    return "(" + this.subject.toString() + ", " + this.property.toString()
        + ", " + this.object.toString() + ") ";
  }
}
