/*************************************************************************************************************
 * Agent-Object-Relationship (AOR) Simulation
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
 * File: AgentSubjectCreationEventTest.java
 * 
 * Package: info.aors.model.envevt
 *
 **************************************************************************************************************/
package test.aors.model.envevt;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import aors.controller.AbstractSimulator;
import aors.model.agtsim.AgentSubject;
import aors.model.envevt.AgentSubjectCreationEvent;
import examples.movingcars.controller.Simulator;
import examples.movingcars.model.agtsim.CarAgentSubject;

/**
 * AgentSubjectCreationEventTest
 * 
 * @author Marco Pehla
 * @since 05.06.2008
 * @version $Revision: 1.0 $
 */
public class AgentSubjectCreationEventTest {

  AbstractSimulator simulationEngine;
  AgentSubject agentSubject;
  AgentSubjectCreationEvent event;
  long occuranceTime;

  /**
   * Usage:
   * 
   * 
   * Comments:
   * 
   * 
   * 
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {

    this.simulationEngine = new Simulator();
    this.agentSubject = new CarAgentSubject(1);
    this.occuranceTime = 1L;
    this.event = new AgentSubjectCreationEvent(occuranceTime, this.agentSubject);

  }

  /**
   * Test method for
   * {@link aors.model.envevt.AgentSubjectCreationEvent#AgentSubjectCreationEvent(long, aors.model.agtsim.PhysicalAgentSubject)}
   * .
   */
  @Test
  public void testAgentSubjectCreationEvent() {

    assertTrue(this.event != null);
  }

  /**
   * Test method for
   * {@link aors.model.envevt.AgentSubjectCreationEvent#getAgentSubject()}.
   */
  @Test
  public void testGetAgentSubject() {

    assertTrue(this.event.getAgentSubject().equals(this.agentSubject));
    // fail("Not yet implemented");
  }

  /**
   * Test method for {@link aors.model.AtomicEvent#getOccurrenceTime()}.
   */
  @Test
  public void testGetOccurrenceTime() {

    assertTrue(this.event.getOccurrenceTime() == this.occuranceTime);
    // fail("Not yet implemented");
  }

  /**
   * Test method for {@link aors.model.AtomicEvent#setOccurrenceTime(long)}.
   */
  @Test
  public void testSetOccurrenceTime() {
    fail("Not yet implemented");
  }

  /**
   * Test method for {@link aors.model.Entity#getId()}.
   */
  @Test
  public void testGetId() {
    fail("Not yet implemented");
  }

  /**
   * Test method for {@link aors.model.Entity#setId(java.lang.String)}.
   */
  @Test
  public void testSetId() {
    fail("Not yet implemented");
  }

  /**
   * Test method for {@link aors.model.Entity#getName()}.
   */
  @Test
  public void testGetName() {
    fail("Not yet implemented");
  }

  /**
   * Test method for {@link aors.model.Entity#setName(java.lang.String)}.
   */
  @Test
  public void testSetName() {
    fail("Not yet implemented");
  }

}
