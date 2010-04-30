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
package aors.model.agtsim.beliefs.graph;

import java.util.ArrayList;
import java.util.Iterator;

import aors.model.agtsim.beliefs.ERDFPrefixes;
import aors.model.agtsim.beliefs.model.NegativeTriple;
import aors.model.agtsim.beliefs.model.Node;
import aors.model.agtsim.beliefs.model.Triple;

/**
 * The ERDF Graph representation.
 * 
 * @author Mircea Diaconescu
 * @since September 29, 2009
 * @version $Revision: 1.0 $
 */
public class ERDFGraph {

  /**
   * The positive ERDF triple store
   */
  private ArrayList<Triple> erdfPositiveTripleStore;

  /**
   * The negative ERDF triple store. There are maintained in different triple
   * stores for a better operation performance
   */
  private ArrayList<NegativeTriple> erdfNegativeTripleStore;

  /**
   * The list with beliefs type definitions. Keeping it in a different list
   * improves the search performance.
   */
  private ArrayList<Triple> erdfTripleTypeDefinitions;

  /**
   * Create the default empty ERDF Graph
   */
  public ERDFGraph() {
    this.erdfPositiveTripleStore = new ArrayList<Triple>(15);
    this.erdfNegativeTripleStore = new ArrayList<NegativeTriple>(15);
    this.erdfTripleTypeDefinitions = new ArrayList<Triple>(15);
  }

  /**
   * Add a triple to the graph. The triple is added only if it is not already in
   * the graph.
   * 
   * @param triple
   *          the triple to be added.
   * @return true if the edge was added, false otherwise
   */
  public boolean addTriple(Triple triple) {
    boolean result = false;

    // first see if is a type definition
    if (triple.getProperty().getValue().equals(ERDFPrefixes.RDF_TYPE)) {
      if (!this.erdfTripleTypeDefinitions.contains(triple)) {
        result = this.erdfTripleTypeDefinitions.add(triple);
      }
    } else if (triple instanceof NegativeTriple) {
      if (!this.erdfNegativeTripleStore.contains(triple)) {
        result = this.erdfNegativeTripleStore.add((NegativeTriple) triple);
      }
    } else {
      if (!this.erdfPositiveTripleStore.contains(triple)) {
        result = this.erdfPositiveTripleStore.add(triple);
      }
    }

    return result;
  }

  /**
   * Remove a triple form the graph.
   * 
   * @param triple
   *          the triple to be removed
   * @return boolean when successful
   */
  public boolean removeTriple(Triple triple) {
    boolean result = false;

    if (triple instanceof NegativeTriple) {
      result = this.erdfNegativeTripleStore.remove(triple);
    } else {
      result = this.erdfPositiveTripleStore.remove(triple);
    }

    return result;
  }

  /**
   * Remove triples based on the given subject, property and object nodes.
   * Please note that a call with all parameters equal with null will result in
   * deleting the hole ERDF graph.
   * 
   * @param subject
   *          the subject to look for. Use null for any.
   * @param property
   *          the property to look for. Use null for any.
   * @param object
   *          the object to look for. Use null for any.
   */
  public void removeTriples(Node subject, Node property, Node object) {
    Iterator<Triple> iter = null;

    // remove type definitions if necessarily
    iter = this.erdfTripleTypeDefinitions.iterator();
    while (iter.hasNext()) {
      Triple t = iter.next();
      if ((t.getSubject().equals(subject) || subject == null)
          && (t.getProperty().equals(property) || property == null)
          && (t.getObject().equals(object) || object == null)) {
        iter.remove();
      }
    }

    // remove positive triples
    iter = this.erdfPositiveTripleStore.iterator();
    while (iter.hasNext()) {
      Triple t = iter.next();
      if ((t.getSubject().equals(subject) || subject == null)
          && (t.getProperty().equals(property) || property == null)
          && (t.getObject().equals(object) || object == null)) {
        iter.remove();
      }
    }

    // remove negative triples
    Iterator<NegativeTriple> iterNeg = this.erdfNegativeTripleStore.iterator();
    while (iterNeg.hasNext()) {
      Triple t = iterNeg.next();
      if ((t.getSubject().equals(subject) || subject == null)
          && (t.getProperty().equals(property) || property == null)
          && (t.getObject().equals(object) || object == null)) {
        iterNeg.remove();
      }
    }
  }

  /**
   * Query the ERDF graph to extract the triples that contains the given
   * subject, property and object nodes.
   * 
   * @param subject
   *          the subject to look for. Use null for any.
   * @param property
   *          the property to look for. Use null for any.
   * @param object
   *          the object to look for. Use null for any.
   * @return the iterator over the found triples list.
   */
  public Iterator<Triple> getTriples(Node subject, Node property, Node object) {
    ArrayList<Triple> triples = new ArrayList<Triple>(15);

    // define the iterator
    Iterator<Triple> iterType = this.erdfTripleTypeDefinitions.iterator();

    // check whatever the type definitions are checked
    if (property != null && property.getValue().equals(ERDFPrefixes.RDF_TYPE)) {

      // query for rdf:type triples
      while (iterType.hasNext()) {
        Triple t = iterType.next();

        if ((t.getSubject().equals(subject) || subject == null)
            && (t.getProperty().equals(property) || property == null)
            && (t.getObject().equals(object) || object == null)) {
          triples.add(t);
        }
      }

      // return rdf:type based triples since is no need to check more
      return triples.iterator();
    }

    Iterator<Triple> iterPos = this.erdfPositiveTripleStore.iterator();

    // query for positive triples
    while (iterPos.hasNext()) {
      Triple t = iterPos.next();

      if ((t.getSubject().equals(subject) || subject == null)
          && (t.getProperty().equals(property) || property == null)
          && (t.getObject().equals(object) || object == null)) {

        triples.add(t);
      }
    }

    Iterator<NegativeTriple> iterNeg = this.erdfNegativeTripleStore.iterator();

    // query for negative triples
    while (iterNeg.hasNext()) {
      NegativeTriple t = iterNeg.next();

      if ((t.getSubject().equals(subject) || subject == null)
          && (t.getProperty().equals(property) || property == null)
          && (t.getObject().equals(object) || object == null)) {

        triples.add(t);
      }
    }

    return triples.iterator();
  }

  @Override
  public String toString() {
    return "Begin GRAPH \n   Positive: "
        + this.erdfPositiveTripleStore.toString() + "\n   Negative: "
        + this.erdfNegativeTripleStore.toString() + "\n   Type: "
        + this.erdfTripleTypeDefinitions.toString() + "\nEnd GRAPH";
  }

}
