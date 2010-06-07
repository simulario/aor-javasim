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
 * File: QueryEngine.java
 * 
 * Package: aors.query.sparql
 *
 **************************************************************************************************************/
package aors.query.sparql;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import aors.model.Entity;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * QueryEngine
 * 
 * @author Mircea Diaconescu
 * @since November 28, 2008
 * @version $Revision$
 */
public class QueryEngine {
  /** the name of the engine **/
  public static String ENGINE_NAME = "sparql";

  /** default BaseURI value if not defined **/
  private static String defaultBaseURI = "http://aor-simulation.org/default/";

  /**
   * Create default prefixes for queries (such as rdf, rdfs, erdf, owl and so
   * on)
   * 
   * @return default prefixes string
   */
  private static String createDefaultPrefixes() {
    String result = "";
    result += "PREFIX rdf: <" + Prefixes.RDF_NS + "> \n";
    result += "PREFIX rdfs: <" + Prefixes.RDFS_NS + "> \n";
    result += "PREFIX erdf: <" + Prefixes.ERDF_NS + "> \n";
    ;
    result += "PREFIX owl: <" + Prefixes.OWL_NS + "> \n";
    ;

    return result;
  }

  /**
   * Create a RDF model from a belief. This method is called for each individual
   * belief from the beliefs list.
   * 
   * @param belief
   *          the belief to be parsed
   * @return the RDF model
   */
  private static Model createModel(Entity belief, String baseURI) {
    Model model = ModelFactory.createDefaultModel();
    Property prop = null;

    if (!baseURI.endsWith("/") && !baseURI.endsWith("#")) {
      baseURI += "#";
    }

    // create the resource which express this belief object based in its ID
    Resource res = model.createResource(baseURI + belief.getId());

    // add resource type (using rdf:type property)
    res.addProperty(RDF.type, ResourceFactory.createResource(baseURI
        + belief.getClass().getSimpleName()));

    // add resource name (the value of property name if not null or empty)
    if (belief.getName() != null && belief.getName().length() > 0) {
      prop = ResourceFactory.createProperty(baseURI, "name");
      res.addProperty(prop, belief.getName());
    }

    // add all other properties of this belief to the rdf:Description of this
    // belief
    Field fields[] = belief.getClass().getDeclaredFields();
    for (Field f : fields) {
      if (!f.getName().startsWith("this$")) {
        prop = ResourceFactory.createProperty(baseURI, f.getName());
        Method method = null;
        try {
          // create getter name (for boolean start with "is" and for other types
          // with "get"
          String methodName = (f.getType().getName().equals("boolean") ? "is"
              : "get");
          methodName += f.getName().substring(0, 1).toUpperCase();
          methodName += f.getName().substring(1);

          // get the right getter method for this field
          method = belief.getClass().getMethod(methodName);

          // allow access to this method
          method.setAccessible(true);
        } catch (NoSuchMethodException exnm) {
          System.err.println("QueryEngine.createModel: " + exnm);
          return null;
        }

        try {
          res.addProperty(prop, method.invoke(belief).toString());
        } catch (InvocationTargetException exit) {
          System.err.println("QueryEngine.createModel: " + exit);
          return null;
        } catch (IllegalAccessException exia) {
          System.err.println("QueryEngine.createModel: " + exia);
          return null;
        }
      }
    }

    // return the model of this belief
    return model;
  }

  /**
   * Applies a SPARQL query over the list of beliefs
   * 
   * @param sparqlQuery
   *          the SPARQL query to be executed
   * @param baseURI
   *          the baseURI of this simulation
   * @param beliefs
   *          the beliefs list
   * @return a list of HashMap objects. Each HashMap is a solution of the query.
   *         The Hash map contains structures of form (key, value). Key is the
   *         variable name used in query and value is the bounded value for that
   *         variable for this solution.
   */

  public static List<HashMap<String, String>> executeQuery(String sparqlQuery,
      String baseURI, List<Entity> beliefs) {
    List<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
    Model model = ModelFactory.createDefaultModel();

    // no base URI or null
    if (baseURI == null) {
      baseURI = defaultBaseURI;
    }

    // create model from all beliefs
    for (Entity belief : beliefs) {
      model.add(QueryEngine.createModel(belief, baseURI));
    }

    // create base prefix
    sparqlQuery = "PREFIX : <" + baseURI + "> \n" + sparqlQuery;

    // add default prefixes to query
    sparqlQuery = QueryEngine.createDefaultPrefixes() + sparqlQuery;

    // create and execute the SPARQL query
    Query query = QueryFactory.create(sparqlQuery);
    QueryExecution qe = QueryExecutionFactory.create(query, model);
    ResultSet queryResults = qe.execSelect();
    List<?> vars = queryResults.getResultVars();

    // extract solution and create a list of HashMaps from it
    while (queryResults.hasNext()) {
      QuerySolution solution = queryResults.nextSolution();
      HashMap<String, String> aSolution = new HashMap<String, String>();

      // get the current solution
      for (Object v : vars) {
        String var = (String) v;

        // this variable has an assigned value
        if (solution.get(var) != null) {
          if (solution.get(var).toString().startsWith(baseURI)) {
            aSolution.put(var, solution.get(var).toString().substring(
                baseURI.length()));
          } else {
            aSolution.put(var, solution.get(var).toString());
          }
        }
        // variable has not an assigned value, put "" as default value
        else {
          aSolution.put(var, "");
        }
      }

      // add the solution to list
      result.add(aSolution);
    }

    // return solutions list
    return result;
  }

  /**
   * Applies a SPARQL query over the RDF beliefs representation.
   * 
   * @param sparqlQuery
   *          the SPARQL query to be executed
   * @param baseURI
   *          the baseURI of this simulation
   * @param beliefs
   *          the RDF beliefs list beliefs list
   * @return a list of HashMap objects. Each HashMap is a solution of the query.
   *         The Hash map contains structures of form (key, value). Key is the
   *         variable name used in query and value is the bounded value for that
   *         variable for this solution.
   */

  public static List<HashMap<String, String>> executeQuery(String sparqlQuery,
      String baseURI, Model rdfBeliefsModel) {

    List<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();

    // no base URI or null
    if (baseURI == null) {
      baseURI = defaultBaseURI;
    }

    // create base prefix
    sparqlQuery = "PREFIX : <" + baseURI + "> \n" + sparqlQuery;

    // add default prefixes to query
    sparqlQuery = QueryEngine.createDefaultPrefixes() + sparqlQuery;

    // create and execute the SPARQL query
    Query query = QueryFactory.create(sparqlQuery);
    QueryExecution qe = QueryExecutionFactory.create(query, rdfBeliefsModel);
    ResultSet queryResults = qe.execSelect();
    List<?> vars = queryResults.getResultVars();

    // extract solution and create a list of HashMaps from it
    while (queryResults.hasNext()) {
      QuerySolution solution = queryResults.nextSolution();
      HashMap<String, String> aSolution = new HashMap<String, String>();

      // get the current solution
      for (Object v : vars) {
        String var = (String) v;

        // this variable has an assigned value
        if (solution.get(var) != null) {
          if (solution.get(var).toString().startsWith(baseURI)) {
            aSolution.put(var, solution.get(var).toString().substring(
                baseURI.length()));
          } else {
            aSolution.put(var, solution.get(var).toString());
          }
        }
        // variable has not an assigned value, put "" as default value
        else {
          aSolution.put(var, "");
        }
      }

      // add the solution to list
      result.add(aSolution);
    }

    // return solutions list
    return result;

  }

}
