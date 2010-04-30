/*************************************************************************************************************
 * Agent-Object-Relationship (AOR) Simulation
 * Ontologically well-founded and closer-to-reality simulation of complex discrete event scenarios
 *
 * AOR-JSim v.2
 *
 * Copyright (C) 2009 AOR Team: Daniel Draeger, Adrian Giurca, Emilian Pascalau, Andreas Post, Marco Pehla,
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
 * File: AbstractObjectPropertyStatisticVariable.java
 * 
 * Package: aors.statistics
 *
 **************************************************************************************************************/
package aors.statistics;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import aors.model.envsim.Objekt;

/**
 * AbstractObjectPropertyStatisticVariable
 * 
 * @author Jens Werner
 * @since 10.12.2009
 * @version $Revision: 1.0 $
 */
public abstract class AbstractObjectPropertyStatisticVariable extends
    AbstractStatisticsVariable {

  protected Iterator<Objekt> objektIterator;

  /*
   * AbstractObjectProperty with one object
   */
  public AbstractObjectPropertyStatisticVariable(String name,
      StatVarDataTypeEnumLit type, Objekt objekt) {
    super(name, type, objekt);
    if (this.objektList != null) {
      this.objektIterator = this.objektList.iterator();
    }
  }

  /*
   * AbstractObjectProperty with a list of objects
   */
  public AbstractObjectPropertyStatisticVariable(String name,
      StatVarDataTypeEnumLit type, List<Objekt> objektList) {
    super(name, type, objektList);
    if (this.objektList != null) {
      this.objektIterator = this.objektList.iterator();
    }
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Overrides method {@code computeVar} from super class
   * 
   * 
   * 
   */
  @Override
  public void computeVar() {
    // TODO Auto-generated method stub

  }

  /**
   * 
   * Usage:
   * 
   * 
   * Comments:
   * 
   * @return
   */
  public abstract AbstractPropertyIterator getPropertyIterator();

  public abstract AbstractObjektIDRefPropertyIterator getObjektIDRefPropertyIterator();

  /**
   * 
   * AbstractPropertyIterator
   * 
   * This Iterator could be used to iterate over a numeric property of a list of
   * objekts
   * 
   * @author Jens Werner
   * @since 12.11.2009
   * @version $Revision: 1.0 $
   */
  public abstract class AbstractPropertyIterator implements Iterator<Double> {

    protected Iterator<Objekt> iterator;

    protected AbstractPropertyIterator(Iterator<Objekt> iterator) {
      this.iterator = iterator;
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException("remove is not implemented");

    }
  }

  public abstract class AbstractObjektIDRefPropertyIterator implements
      Iterator<ObjektIdPropertyData> {

    protected Iterator<Objekt> iterator;

    protected AbstractObjektIDRefPropertyIterator(Iterator<Objekt> iterator) {
      this.iterator = iterator;
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException("remove is not implemented");

    }
  }

  /**
   * @return the {@code objektList}.
   */
  protected List<Objekt> getObjektList() {
    if (this.objektList == null)
      return new ArrayList<Objekt>();
    return this.objektList;
  }

  /**
   * 
   * ObjektIdPropertyData is used as a container of a pair of data; the id of an
   * aor-objekt and a value of a property
   * 
   * @author Jens Werner
   * @since 04.02.2010
   * @version $Revision: 1.0 $
   */
  public class ObjektIdPropertyData {

    private long idRef;
    private Double propertyValue;

    public ObjektIdPropertyData(long idRef, Double propertyValue) {
      this.idRef = idRef;
      this.propertyValue = propertyValue;
    }

    /**
     * @return the {@code idRef}.
     */
    public long getIdRef() {
      return idRef;
    }

    /**
     * @return the {@code propertyValue}.
     */
    public Double getPropertyValue() {
      return propertyValue;
    }

  }

}
