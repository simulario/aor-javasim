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
package aors.model.agtsim.beliefs;

/**
 * The RDF and ERDF namespaces and constants.
 * 
 * @author Mircea Diaconescu
 * @since September 04, 2009
 * @version $Revision: 1.0 $
 */
public interface ERDFPrefixes {

  /** RDF namespace **/
  public static final String RDF_NS = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

  /** RDFS namespace **/
  public static final String RDFS_NS = "http://www.w3.org/2000/01/rdf-schema#";

  /** ERDF namespace **/
  public static final String ERDF_NS = "http://www.informatik.tu-cottbus.de/IT/erdf#";

  /** OWL namespace **/
  public static final String OWL_NS = "http://www.w3.org/2002/07/owl#";

  /** the rdf:type property **/
  public static String RDF_TYPE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";

  /** the rdf:type property as QName **/
  public static String RDF_TYPE_QNAME = "rdf:type";

}
