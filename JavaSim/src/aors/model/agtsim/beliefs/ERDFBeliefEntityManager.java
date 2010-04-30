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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import aors.model.Entity;

/**
 * RDFBeliefEntityManager This interface has to be implemented by any manager
 * class of belief entities. It provides methods for the beliefs management.
 * 
 * @author Mircea Diaconescu
 * @since June 28, 2009
 * @version $Revision: 1.0 $
 */
public interface ERDFBeliefEntityManager {

  /** separator **/
  public static String NAMESPACE_SEPARATOR = "#";

  /** XML schema URI **/
  public static String XML_SCHEMA_URI = "http://www.w3.org/2001/XMLSchema";

  /** the long type used for expressing values **/
  public static String TYPE_LONG = XML_SCHEMA_URI + NAMESPACE_SEPARATOR
      + "long";

  /** the double type used for expressing values **/
  public static String TYPE_DOUBLE = XML_SCHEMA_URI + NAMESPACE_SEPARATOR
      + "double";

  /** the boolean type used for expressing values **/
  public static String TYPE_BOOLEAN = XML_SCHEMA_URI + NAMESPACE_SEPARATOR
      + "boolean";

  /** the string type used for expressing values **/
  public static String TYPE_STRING = XML_SCHEMA_URI + NAMESPACE_SEPARATOR
      + "string";

  /**
   * Set new value for the baseURI
   * 
   * @param baseURI
   *          the new base URI value.
   */
  public void setBaseURI(String baseURI);

  /**
   * Add an ERDF triple for a belief entity
   * 
   * @param id
   *          the belief entity ID
   * @param propName
   *          the property name
   * @param value
   *          the value of the property
   * @param negated
   *          specify if it is an negative or a positive triple
   */
  public void addBeliefEntityTriple(long id, String propName, String value,
      boolean negated);

  /**
   * Add an ERDF positive triple for a belief entity
   * 
   * @param id
   *          the belief entity ID
   * @param propName
   *          the property name
   * @param value
   *          the value of the property
   * @param negated
   *          specify if it is an negative or a positive triple
   */
  public void addBeliefEntityTriple(long id, String propName, String value);

  /**
   * Get the value of a property for a specified entity ID and Property Name
   * 
   * @param id
   *          the ID of the belief entity
   * @param propName
   *          the name of the property
   * @return an iterator over the list with values
   */
  public Iterator<String> getBeliefEntityPropertyValues(long id, String propName);

  /**
   * Updates the value of a belief property when know the ID and property name
   * 
   * @param id
   *          the belief entity ID
   * @param propName
   *          the property name
   * @param value
   *          the new value to set
   */
  public void updateBeliefEntityPropertyValue(long id, String propName,
      String value);

  /**
   * Returns a list with belief entities when the type is known.
   * 
   * @param typeName
   *          the belief entities type name.
   * @return the list with belief entities
   */
  public List<BeliefEntity> getBeliefEntitiesByType(String typeName);

  /**
   * Returns the belief entity with the given ID.
   * 
   * @param id
   *          the belief entity ID
   * @return the the belief entity with the given ID.
   */
  public BeliefEntity getBeliefEntityById(long id);

  /**
   * Remove a belief entity when the ID is known. This will remove all triples
   * of this belief entity.
   * 
   * @param id
   *          the ID of the belief entity
   */
  public void removeBeliefEntity(long id);

  /**
   * Remove a certain triple of a belief entity
   * 
   * @param id
   *          the ID of the belief entity
   * @param propName
   *          the property of the belief entity
   * @param value
   *          the value of the property
   */
  public void removeBeliefEntityTriple(long id, String propName, String value);

  /**
   * Remove a certain triple of a belief entity. The requested data is the
   * subject and property. All triples having this subject and property are
   * removed no matter which is the object
   * 
   * @param id
   *          the ID of the belief entity
   * @param propName
   *          the property of the belief entity
   * @param value
   *          the value of the property
   */
  public void removeBeliefEntityTriple(long id, String propName);

  /**
   * Query beliefs.
   * 
   * @param queryLanguage
   *          The language query. Not Case sensitive. It is recommended to use
   *          constants from each Query class
   * @param queryString
   *          The string containing the query
   * @return A hash map with all solutions.
   */
  public List<HashMap<String, String>> executeQuery(String queryLanguage,
      String queryString);

  /**
   * Query beliefs. Please note that this is just temporarily used for backward
   * compatibility.
   * 
   * @param queryLanguage
   *          The language query. Not Case sensitive. It is recommended to use
   *          constants from each Query class
   * @param queryString
   *          The string containing the query
   * @param beliefList
   *          - a list with belief entities.
   * @return A hash map with all solutions.
   */
  public List<HashMap<String, String>> executeQuery(String queryLanguage,
      String queryString, List<Entity> beliefList);

}
