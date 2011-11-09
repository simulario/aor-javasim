/*************************************************************************************************************
 * Agent-Object-Relationship (AOR) Simulation
 * Ontologically well-founded and closer-to-reality simulation of complex discrete event scenarios
 *
 * AOR-JSim v.2
 *
 * Copyright (C) 2010 AOR Team: Daniel Draeger, Adrian Giurca, Emilian Pascalau, Andreas Post, Marco Pehla,
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
 * File: SimulatorException.java
 * 
 * Package: aors.exceptions
 *
 **************************************************************************************************************/
package aors.exceptions;

/**
 * SimulatorException
 * 
 * @author Jens Werner
 * @since 02.11.2010
 * @version $Revision: 1.0 $
 */
public class SimulatorException extends Exception {

  private static final long serialVersionUID = 1430957645735496147L;

  public SimulatorException() {
    super();
  }

  public SimulatorException(String message) {
    super(message);
  }

  public SimulatorException(StackTraceElement[] stackTrace) {
    super();
    this.setStackTrace(stackTrace);
  }

  public SimulatorException(String message, StackTraceElement[] stackTrace) {
    super(message);
    this.setStackTrace(stackTrace);
  }

//  public void printStackTrace() {
//    System.err.println(" - > Here comes the stackTrace from the throwed exception:");
//    printStackTrace(System.err);
//  }

}
