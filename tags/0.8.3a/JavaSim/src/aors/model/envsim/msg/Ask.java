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
 * File: Ask.java
 * 
 * Package: aors.model.envsim.msg
 *
 **************************************************************************************************************/
package aors.model.envsim.msg;

import aors.model.Message;

/**
 * Ask
 * 
 * A standard Ask message type used for agents communication.
 * 
 * @author Ion-Mircea Diaconescu
 * @since March 23, 2009
 * @version $Revision$
 */
public class Ask extends Message {

  /** the type of belief entity type we ask about **/
  private String beliefEntityType;

  /**
   * the language of the send query (constants of the corresponding query
   * packages are used)
   **/
  private String queryLanguage;

  /**
   * the query string - written in the language defined by
   * <code>queryLanguage</code>
   */
  private String queryString;

  /** default constructor **/
  public Ask() {
    super();
  }

  /**
   * The constructor with id parameter
   * 
   * @param id
   *          the ID of this message
   */
  public Ask(long id) {
    super(id);
  }

  /**
   * The constructor with id and name parameters
   * 
   * @param id
   *          the ID of this message
   * @param name
   *          the name of the message
   */
  public Ask(long id, String name) {
    super(id, name);
  }

  /**
   * @return the queryLanguage
   */
  public String getQueryLanguage() {
    return queryLanguage;
  }

  /**
   * @param queryLanguage
   *          the queryLanguage to set
   */
  public void setQueryLanguage(String queryLanguage) {
    this.queryLanguage = queryLanguage;
  }

  /**
   * @return the queryString
   */
  public String getQueryString() {
    return queryString;
  }

  /**
   * @param queryString
   *          the queryString to set
   */
  public void setQueryString(String queryString) {
    this.queryString = queryString;
  }

  /**
   * @return the beliefEntityType
   */
  public String getBeliefEntityType() {
    return beliefEntityType;
  }

  /**
   * @param beliefEntityType
   *          the beliefEntityType to set
   */
  public void setBeliefEntityType(String beliefEntityType) {
    this.beliefEntityType = beliefEntityType;
  }
}
