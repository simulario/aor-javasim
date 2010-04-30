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
 * File: Entity.java
 * 
 * Package: info.aors.model
 *
 **************************************************************************************************************/
package aors.model;

/**
 * Entity
 * 
 * @author Emilian Pascalau, Adrian Giurca
 * @since May 23, 2008
 * @version $Revision: 1.0 $
 */
public class Entity implements Cloneable {

  /**
   * The AOR entity type as defined in the Simulation Description.
   */
  private String aorEntityType = "";

  /**
   * Each Entity has an id. It acts as unique identifier for the Entity.
   */
  private long id;

  /**
   * The custom name given to the Entity.
   */
  private String name = "";

  /**
   * 
   * Create a new {@code Entity}.
   */
  protected Entity() {
    this.aorEntityType = this.getClass().getSimpleName();
  } //

  /**
   * 
   * Create a new {@code Entity}.
   * 
   * @param id
   */
  protected Entity(long id) {
    this.id = id;
    this.aorEntityType = this.getClass().getSimpleName();
  }

  /**
   * 
   * Create a new {@code Entity}.
   * 
   * @param id
   */
  protected Entity(long id, String name) {
    this.id = id;
    this.name = name;
    this.aorEntityType = this.getClass().getSimpleName();
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Get the {@code id}.
   * 
   * 
   * 
   * @return the {@code id}.
   */
  public long getId() {
    return this.id;
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Set the {@code id}.
   * 
   * 
   * 
   * @param id
   *          The {@code id} to set.
   */
  public void setId(long id) {
    this.id = id;
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Get the {@code name}.
   * 
   * 
   * 
   * @return the {@code name}.
   */
  public String getName() {
    return this.name;
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Set the {@code name}.
   * 
   * 
   * 
   * @param name
   *          The {@code name} to set.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return the entity type as defined in Simulation Description
   */
  public String getType() {
    return this.aorEntityType;
  }

  /**
   * Set the entity type value. This is protected while it can't be set by
   * outside this object and its inherited classes.
   * 
   * @param aorEntityType
   *          the new value of the aorEntityType
   */
  protected void setType(String aorEntityType) {
    this.aorEntityType = aorEntityType;
  }

  public Object clone() {
    Object o = null;
    try {
      o = super.clone();
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
    }
    return o;
  }
}
