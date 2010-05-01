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
 * File: UnsupportedQueryLanguageException.java
 * 
 * Package: aors.query
 *
 **************************************************************************************************************/
package aors.query;

/**
 * UnsupportedQueryLanguageException Exceptions trown for unknown query
 * languages.
 * 
 * @author Mircea Diaconescu
 * @since December 5, 2008
 * @version $Revision$
 */
public class UnsupportedQueryLanguageException extends Exception {
  public static final long serialVersionUID = 875469;

  public UnsupportedQueryLanguageException(String message) {
    super(message);
  }
}
