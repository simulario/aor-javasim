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
 * The RDFLiteral representation.
 * 
 * @author Mircea Diaconescu
 * @since September 29, 2009
 * @version $Revision$
 */

public class RDFLiteral implements Value {

  /**
   * The value of this literal.
   */
  private String value;

  /**
   * Create an empty RDFLiteral
   */
  public RDFLiteral() {
    this.value = new String("");
  }

  /**
   * Create a RDFLiteral.
   * 
   * @param value
   *          the value of the RDF Literal.
   */
  public RDFLiteral(String value) {
    this.value = value;
  }

  @Override
  public String getValue() {
    return this.value;
  }

  @Override
  public void setValue(String value) {
    this.value = value;
  }
}
