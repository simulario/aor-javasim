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
 **************************************************************************************************************/

package aors.model.agtsim.beliefs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import aors.model.Entity;

/**
 * BeliefEntity - a belief entity representation
 * 
 * @author Mircea Diaconescu
 * @since July 09, 2009
 * @version $Revision$
 */
public class BeliefEntity extends Entity {

  /** the map with property value pairs **/
  private HashMap<String, Object> value = new HashMap<String, Object>();

  /**
   * Create the belief entity knowing only the ID
   * 
   * @param id
   *          the ID of the belief entity
   */
  public BeliefEntity(long id) {
    super(id);
  }

  /**
   * Create a belief entity knowing the ID and name
   * 
   * @param id
   * @param name
   */
  public BeliefEntity(long id, String name) {
    super(id, name);
  }

  /**
   * Gets the value of a given property
   * 
   * @param propName
   *          the property name for which we need the value
   * @return the value of the property
   */
  public Object getPropertyValue(String propName) {
    return this.value.get(propName);
  }

  /**
   * Update a value of a property when the old value is known.
   * 
   * @param propName
   *          the property name
   * @param oldPropValue
   *          the old value
   * @param newPropValue
   *          the new value
   */
  @SuppressWarnings("unchecked")
  public void setPropertyValue(String propName, Object oldPropValue,
      Object newPropValue) {

    // proprty name and the value to set can't be null !
    if (propName == null || newPropValue == null) {
      return;
    }

    // we don't know the old value this means we perform an add
    if (oldPropValue == null) {
      // no property exists yet with this name, so add it
      if (!this.value.containsKey(propName)) {
        this.value.put(propName, newPropValue);
      }
      // the property exists and has already a value assigned
      else {
        // multi-valued property and already existing values
        if (this.value.get(propName) instanceof List) {
          ((ArrayList<Object>) this.value.get(propName)).add(newPropValue);
        }
        // multi-valued property but just one value yet, make it collection
        else {
          Object oldVal = this.value.get(propName);
          this.value.remove(propName);
          ArrayList<Object> newVal = new ArrayList<Object>();
          newVal.add(oldVal);
          this.value.put(propName, newVal);
        }
      }
    }
    // we update a value of a property when we know the old value
    else {
      // no property exists yet with this name, so it is nothing to do
      if (!this.value.containsKey(propName)) {
        return;
      }
      // the property exists, so we need to update the value
      else {
        // multi-valued property
        if (this.value.get(propName) instanceof List) {
          int pos = ((ArrayList<Object>) this.value.get(propName))
              .indexOf(oldPropValue);
          ((ArrayList<Object>) this.value.get(propName)).remove(pos);
          ((ArrayList<Object>) this.value.get(propName)).add(newPropValue);

        }
        // multi-valued property but just one value yet, make it collection
        else {
          this.value.remove(propName);
          this.value.put(propName, newPropValue);
        }
      }

    }

  }

  /**
   * Set a value for a specific property of the belief entity
   * 
   * @param propName
   *          the name of the property
   * @param propValue
   *          the value for the property
   */
  public void setPropertyValue(String propName, Object propValue) {
    this.setPropertyValue(propName, null, propValue);
  }

  @Override
  public String toString() {
    String result = "Entity = {id=" + this.getId() + ", name=" + this.getName()
        + ", rdf:type=" + this.value.get(ERDFPrefixes.RDF_TYPE_QNAME);

    Iterator<String> iter = this.value.keySet().iterator();

    while (iter.hasNext()) {
      String key = iter.next();
      if (key.equals(ERDFPrefixes.RDF_TYPE_QNAME)) {
        continue;
      }
      result += (", " + key + "=" + this.value.get(key));
    }

    result += "}";

    return result;
  }

}
