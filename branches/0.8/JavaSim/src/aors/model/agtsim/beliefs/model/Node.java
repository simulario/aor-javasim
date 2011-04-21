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
 * The node representation.
 * 
 * @author Mircea Diaconescu
 * @since September 29, 2009
 * @version $Revision$
 */
public class Node {
  /**
   * The value of the node.
   */
  private Value value;

  /**
   * Create an empty Node.
   */
  public Node() {
    this("");
  }

  /**
   * The constructor of a node.
   * 
   * @param value
   *          the value of the node.
   */
  public Node(Value value) {
    this.value = value;
  }

  /**
   * The constructor of a node.
   * 
   * @param value
   *          the value of the node as string representation.
   */
  public Node(String value) {
    this.value.setValue(value);
  }

  /**
   * Gets the String representation of the node value.
   * 
   * @return the string representation of node value.
   */
  public String getValue() {
    return value.getValue();
  }

  /**
   * Gets the NodeValue for this node.
   * 
   * @return the NodeValue representation of the value of this node.
   */
  public Value getNodeValue() {
    return this.value;
  }

  /**
   * Set the node value.
   * 
   * @param value
   *          the value to set.
   */
  public void setValue(String value) {
    this.value.setValue(value);
  }

  @Override
  public boolean equals(Object node) {
    if (!(node instanceof Node)
        || !this.getValue().equals(((Node) node).getValue())) {
      return false;
    } else {
      return true;
    }
  }

  @Override
  public int hashCode() {
    return this.value.getValue().length();
  }

  @Override
  public String toString() {
    return this.getValue();
  }

}
