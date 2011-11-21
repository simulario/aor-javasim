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
 * File: AbstractResourceUtilizationStatisticVariable.java
 * 
 * Package: aors.statistics
 *
 **************************************************************************************************************/
package aors.statistics;

import java.util.List;

import aors.data.evt.sim.ObjektDestroyEvent;
import aors.data.evt.sim.ObjektDestroyEventListener;
import aors.model.envsim.Objekt;

/**
 * AbstractResourceUtilizationStatisticVariable
 * 
 * @author Jens Werner
 * @since 10.12.2009
 * @version $Revision$
 */
public abstract class AbstractResourceUtilizationStatisticVariable extends
    AbstractStatisticsVariable implements ObjektDestroyEventListener {

  protected String activityType;

  protected double destroyedObjectResourceUtilization = 0;

  /*
   * ResourceUtilization with one object
   */
  public AbstractResourceUtilizationStatisticVariable(String name,
      StatVarDataTypeEnumLit type, Objekt objekt) {
    super(name, type, objekt);
  }

  /*
   * ResourceUtilization with a list of objects
   */
  public AbstractResourceUtilizationStatisticVariable(String name,
      StatVarDataTypeEnumLit type, List<Objekt> objektList) {
    super(name, type, objektList);
  }

  /**
   * 
   * Usage:
   * 
   * 
   * Comments:
   * 
   * 
   * 
   * @param currentStep
   * @return
   */
  public abstract Number getValue(long currentStep);

  /**
   * Usage:
   * 
   * 
   * Comments: Get the {@code activityType}.
   * 
   * 
   * 
   * @return the {@code activityType}.
   */
  public String getActivityType() {
    return activityType;
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Set the {@code activityType}.
   * 
   * 
   * 
   * @param activityType
   *          The {@code activityType} to set.
   */
  public void setActivityType(String activityType) {
    this.activityType = activityType;
  }

  @Override
  public void objektDestroyEvent(ObjektDestroyEvent objektDestroyEvent) {
    Objekt objekt = (Objekt) objektDestroyEvent.getSource();
    if (objekt.getType().equals(this.getSourceObjectType())) {
      this.destroyedObjectResourceUtilization = this.destroyedObjectResourceUtilization
          + objekt.getResourceUtilizationTimeByActivity(this.activityType,
              objektDestroyEvent.getDestroyOccurenceTime());
    }
  }

}
