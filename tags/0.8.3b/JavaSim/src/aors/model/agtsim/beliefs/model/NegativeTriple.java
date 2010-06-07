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
 * The ERDF Negative Triple representation.
 * 
 * @author Mircea Diaconescu
 * @since September 29, 2009
 * @version $Revision$
 */

public class NegativeTriple extends Triple {

  /**
   * Create a negative empty triple.
   */
  public NegativeTriple() throws InvalidNodeTypeException {
    this(new Node(), new Node(), new Node(), 0.0f);
  }

  /**
   * Create a negative triple with maximum certainty factor.
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
  public NegativeTriple(Node subject, Node property, Node object)
      throws InvalidNodeTypeException {

    this(subject, property, object, 1.0f);
  }

  /**
   * Create a negative triple.
   * 
   * @param subject
   *          the triple subject
   * @param property
   *          the triple property
   * @param object
   *          the triple object
   * @param certaintyFactor
   *          the certainty factor of this triple
   * @throws InvalidNodeTypeException
   *           throws exception while one of the node type does not fit with the
   *           required types.
   */
  public NegativeTriple(Node subject, Node property, Node object,
      float certaintyFactor) throws InvalidNodeTypeException {

    super(subject, property, object, certaintyFactor);
  }

  @Override
  public boolean equals(Object negativeTriple) {
    if (!(negativeTriple instanceof NegativeTriple)) {
      return false;
    }

    if (!super.equals(negativeTriple)) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  @Override
  public String toString() {
    return "(" + this.getSubject().toString() + ", -"
        + this.getProperty().toString() + ", " + this.getObject().toString()
        + ") ";
  }
}
