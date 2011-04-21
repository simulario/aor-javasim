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
 * The TypedLiteral representation.
 * 
 * @author Mircea Diaconescu
 * @since September 29, 2009
 * @version $Revision$
 */

public class TypedLiteral extends Literal implements Value {

  /**
   * The literal type.
   */
  private URIReference literalType;

  /**
   * Create an empty TypedLiteral.
   */
  public TypedLiteral() {
    super(new String(""));
    this.literalType = new URIReference();
  }

  /**
   * Create a typed literal.
   * 
   * @param value
   *          the literal value
   * @param literalType
   *          the type or the value for this literal
   */
  public TypedLiteral(String value, URIReference literalType) {
    super(value);
    this.literalType = literalType;
  }

  @Override
  public String getValue() {
    return super.getValue() + "^^" + this.literalType.toString();
  }

  @Override
  public void setValue(String value) {
    super.setValue(value);
  }

  /**
   * Set the type of the literal.
   * 
   * @param literalType
   *          the literal type.
   */
  public void setLiteralType(URIReference literalType) {
    this.literalType = literalType;
  }

}
