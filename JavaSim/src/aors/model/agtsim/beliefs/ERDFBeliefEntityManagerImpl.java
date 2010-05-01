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
import aors.model.agtsim.beliefs.graph.ERDFGraph;
import aors.model.agtsim.beliefs.model.InvalidNodeTypeException;
import aors.model.agtsim.beliefs.model.NegativeTriple;
import aors.model.agtsim.beliefs.model.Node;
import aors.model.agtsim.beliefs.model.Triple;
import aors.model.agtsim.beliefs.model.URIReference;
import aors.query.sparql.QueryEngine;

/**
 * RDFBeliefEntityManagerImpl - an RDFBeliefManager implementation
 * 
 * @see RDFBeliefEntityManage interface
 * 
 * @author Mircea Diaconescu
 * @since July 06, 2009
 * @version $Revision$
 */
public class ERDFBeliefEntityManagerImpl implements ERDFBeliefEntityManager {
  /** the Base URI of the RDF model **/
  private URIReference baseURI;

  /**
   * Comments: beliefs graph - beliefs of this agent in ERDF graph
   */
  private ERDFGraph erdfBeliefsGraph;

  /**
   * Construct an empty RDFBeliefEntityManager
   */
  public ERDFBeliefEntityManagerImpl() {
    this.erdfBeliefsGraph = new ERDFGraph();
    this.baseURI = new URIReference();
  }

  /**
   * Gets the baseURI of the Simulation Model
   * 
   * @return the baseURI
   */
  private String getBaseURI() {
    // the base URI defined for the simulation model
    String baseURI = this.baseURI.getValue();

    // beautify the baseURI resource
    if (!baseURI.endsWith("/") && !baseURI.endsWith("#")) {
      baseURI += "#";
    }

    return baseURI;
  }

  /**
   * Set new value for the baseURI
   * 
   * @param baseURI
   *          the new base URI value.
   */
  public void setBaseURI(String baseURI) {
    this.baseURI.setValue(baseURI);
  }

  /**
   * Gets the local name from a given URI formed using baseURI
   * 
   * @param uriReference
   * @return
   */
  private String getLocalName(String uriReference) {
    if (this.getBaseURI().length() < uriReference.length()) {
      return uriReference.substring(this.getBaseURI().length());
    } else {
      return null;
    }
  }

  /**
   * This add the XS datatype for some values.
   * 
   * @param value
   *          the value that is considered
   * @return a piece of string in form of ^^xs:theType
   */
  @SuppressWarnings("unused")
  private String getXSDatatype(Object value) {
    String result = "";

    if (value instanceof Boolean) {
      result += "^^xs:boolean";
    } else if (value instanceof Long || value instanceof Integer) {
      result += "^^xs:long";
    } else if (value instanceof Double || value instanceof Float) {
      result += "^^xs:double";
    } else if (value instanceof String) {
      result += "^^xs:string";
    }

    return result;
  }

  /**
   * Decides the Java datatype based on the XS datatype for a given value.
   * 
   * @param value
   *          the value (the object of the RDF triple usually)
   * @return the name of the Java datatype
   */
  @SuppressWarnings("unused")
  private String getJavaDatatype(String value) {
    String result = "";

    int pos = value.indexOf("^^");

    if (pos == -1) {
      return "Object";
    }

    String type = value.substring(pos);
    if (type.equals("xs:boolean")) {
      result = "boolean";
    } else if (type.equals("xs:long")) {
      result = "long";
    } else if (type.equals("xs:double")) {
      result = "double";
    } else if (type.equals("xs:string")) {
      result = "String";
    }
    return result;
  }

  @Override
  public void addBeliefEntityTriple(long id, String propName, String value,
      boolean negated) {

    // the base URI defined for the simulation model
    String baseURI = getBaseURI();

    // create the subject node
    Node subject = new Node(new URIReference(baseURI + id));

    // create the property (predicate) node
    Node property;
    if (!propName.equals(ERDFPrefixes.RDF_TYPE)) {
      property = new Node(new URIReference(baseURI + propName));
    } else {
      property = new Node(new URIReference(propName));
    }

    // create the object (value) node
    Node object = new Node(new URIReference(value));

    // add the triple to the ERDF graph
    try {
      if (negated) {
        this.erdfBeliefsGraph.addTriple(new NegativeTriple(subject, property,
            object));
      } else {
        this.erdfBeliefsGraph.addTriple(new Triple(subject, property, object));
      }
    } catch (InvalidNodeTypeException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void addBeliefEntityTriple(long id, String propName, String value) {
    this.addBeliefEntityTriple(id, propName, value, false);
  }

  @Override
  public Iterator<String> getBeliefEntityPropertyValues(long id, String propName) {
    // the base URI defined for the simulation model
    String baseURI = getBaseURI();

    // create the subject node
    Node subject = new Node(new URIReference(baseURI + id));

    // create the property node
    Node property = new Node(new URIReference(baseURI + propName));

    Iterator<Triple> iter = this.erdfBeliefsGraph.getTriples(subject, property,
        null);

    ArrayList<String> values = new ArrayList<String>(15);
    while (iter.hasNext()) {
      values.add(iter.next().getObject().getValue());
    }

    // the result iterator
    return values.iterator();
  }

  @Override
  public void updateBeliefEntityPropertyValue(long id, String propName,
      String value) {

  }

  @Override
  public List<BeliefEntity> getBeliefEntitiesByType(String typeName) {
    List<BeliefEntity> beliefEntities = new ArrayList<BeliefEntity>();

    // create the propery node
    Node property = new Node(new URIReference(ERDFPrefixes.RDF_TYPE));

    // create the object node
    Node object = new Node(new URIReference(typeName));

    // get all entities with this type
    Iterator<Triple> iter = this.erdfBeliefsGraph.getTriples(null, property,
        object);
    // parse the list with found entities for this type
    while (iter.hasNext()) {
      // current entity
      Triple t = iter.next();

      BeliefEntity belief = new BeliefEntity(Long.parseLong(this.getLocalName(t
          .getSubject().getValue())));

      // now create the belief entity for each above found entity of that type
      Iterator<Triple> iterBelief = this.erdfBeliefsGraph.getTriples(t
          .getSubject(), null, null);

      // set the type property
      belief.setPropertyValue(ERDFPrefixes.RDF_TYPE_QNAME, t.getObject()
          .getValue());

      // set the other properties
      while (iterBelief.hasNext()) {
        Triple tb = iterBelief.next();
        String val = tb.getProperty().getValue();

        if (val.equals(this.getBaseURI() + "name")) {
          belief.setName(tb.getObject().getValue());
        } else {
          belief.setPropertyValue(this.getLocalName(val), tb.getObject()
              .getValue());
        }
      }

      // add this belief entity to the result list
      beliefEntities.add(belief);
    }
    // System.out.println(this.erdfBeliefsGraph.toString());
    return beliefEntities;
  }

  @Override
  public BeliefEntity getBeliefEntityById(long id) {
    BeliefEntity beliefEntity = null;

    // the base URI defined for the simulation model
    String baseURI = getBaseURI();

    // create the subject node (start node)
    Node subject = new Node(new URIReference(baseURI + id));

    Iterator<Triple> iterBelief = null;

    // search to see if the entity has any type
    iterBelief = this.erdfBeliefsGraph.getTriples(subject, new Node(
        new URIReference(ERDFPrefixes.RDF_TYPE)), null);

    if (!iterBelief.hasNext()) {
      return null;
    }

    // create the belief entity
    beliefEntity = new BeliefEntity(id);

    // set the type
    beliefEntity.setPropertyValue(ERDFPrefixes.RDF_TYPE_QNAME, iterBelief
        .next().getObject().getValue());

    // search the wanted belief entity triples and obtain the iterator
    iterBelief = this.erdfBeliefsGraph.getTriples(subject, null, null);

    // now add any property-value pairs
    if (iterBelief.hasNext()) {

      while (iterBelief.hasNext()) {

        Triple tb = iterBelief.next();
        String val = tb.getProperty().getValue();

        if (val.equals(this.getBaseURI() + "name")) {
          beliefEntity.setName(tb.getObject().getValue());
        } else {
          beliefEntity.setPropertyValue(this.getLocalName(val), tb.getObject()
              .getValue());
        }
      }
    }

    return beliefEntity;
  }

  @Override
  public void removeBeliefEntity(long id) {
    // the base URI defined for the simulation model
    String baseURI = getBaseURI();

    // create the wanted subject node
    Node subject = new Node(new URIReference(baseURI + id));

    // remove all triples that have this subject.
    this.erdfBeliefsGraph.removeTriples(subject, null, null);
  }

  @Override
  public void removeBeliefEntityTriple(long id, String propName, String value) {
    // the base URI defined for the simulation model
    String baseURI = getBaseURI();

    // create the wanted subject node
    Node subject = (propName == null ? null : new Node(new URIReference(baseURI
        + id)));

    // create the wanted property node
    Node property = (value == null ? null : new Node(new URIReference(baseURI
        + propName)));

    // create the wanted object node
    Node object = new Node(new URIReference(value));

    // remove the required triple
    this.erdfBeliefsGraph.removeTriples(subject, property, object);

  }

  @Override
  public void removeBeliefEntityTriple(long id, String propName) {
    this.removeBeliefEntityTriple(id, propName, null);
  }

  @Override
  public List<HashMap<String, String>> executeQuery(String queryLanguage,
      String queryString) {
    List<HashMap<String, String>> result = null;

    /**
     * Here check which engine to use. Please add here all known solution. What
     * is not here is just not recognized, and therefore not executed.
     */
    if (queryLanguage
        .equalsIgnoreCase(aors.query.sparql.QueryEngine.ENGINE_NAME)) {

      /*
       * result = QueryEngine.executeQuery(queryString, this.baseURI.getValue(),
       * null);
       */
    }

    return result;
  }

  @Override
  public List<HashMap<String, String>> executeQuery(String queryLanguage,
      String queryString, List<Entity> beliefList) {
    List<HashMap<String, String>> result = null;

    /**
     * Here check which engine to use. Please add here all known solution. What
     * is not here is just not recognized, and therefore not executed.
     */
    if (queryLanguage
        .equalsIgnoreCase(aors.query.sparql.QueryEngine.ENGINE_NAME)) {
      result = QueryEngine.executeQuery(queryString, this.baseURI.getValue(),
          beliefList);
    }

    return result;
  }

  /**
   * Query beliefs using SPARQL query.
   * 
   * @param queryString
   *          The string containing the query
   * @return A hash map with all solutions.
   */
  public List<HashMap<String, String>> executeQuery(String queryString) {
    return this.executeQuery(aors.query.sparql.QueryEngine.ENGINE_NAME,
        queryString);
  }

}
