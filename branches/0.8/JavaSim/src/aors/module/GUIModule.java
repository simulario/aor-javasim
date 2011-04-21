/*************************************************************************************************************
 * Agent-Object-Relationship (AOR) Simulation
 * Ontologically well-founded and closer-to-reality simulation of complex discrete event scenarios
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
 **************************************************************************************************************/
package aors.module;

/**
 * GUIPlugin - This interface has to be implemented by any plug-in that offers
 * also a GUI functionality near other functionalities.
 * 
 * @author Mircea Diaconescu
 * @since July 20, 2009
 * @version $Revision$
 */
public interface GUIModule {
  /**
   * The property name that defines the GUI class. This is optional.
   */
  public final static String PROP_GUI_MODULE_CLASS = "gui-module-class";

  /**
   * The property name that defines the GUI component title. This is optional,
   * but if missing then the GUI component has no title!
   */
  public final static String PROP_GUI_TITLE = "gui-title";

  /**
   * This gets a reference to the base component of the module.
   * 
   * @return a reference to the base component of the module
   */
  public Module getBaseComponent();
}
