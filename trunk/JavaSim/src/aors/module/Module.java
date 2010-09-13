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

import java.io.File;

import aors.data.evt.ControllerEventListener;
import aors.data.evt.sim.ObjektDestroyEventListener;
import aors.data.evt.sim.ObjektInitEventListener;
import aors.data.evt.sim.SimulationEventListener;
import aors.data.evt.sim.SimulationStepEventListener;

/**
 * Module - This interface has to be implemented by any plug-in/module that is
 * supposed to be part of the AORS
 * 
 * @author Mircea Diaconescu
 * @since July 20, 2009
 * @version $Revision$
 */

public interface Module extends SimulationStepEventListener,
    SimulationEventListener, ObjektDestroyEventListener,
    ObjektInitEventListener, ControllerEventListener {

  /** the path to the module temporarily directory */
  public static final String TEMP_DIR = System.getProperty("java.io.tmpdir")
      + File.separator + "tmp_aors";

  /**
   * The property name that defines the OS version of the module. If this is not
   * defined the module is considered to work on all OS versions.
   */
  public final static String PROP_MODULE_OS_VERSION = "os-version";

  /**
   * The property name that defines the OS bits version of the module. If this
   * is not defined the module is considered to work on all OS bits versions.
   */
  public final static String PROP_MODULE_OS_BITS_VERSION = "os-bits-version";

  /**
   * The property name that defines the Windows OS version name
   */
  public final static String PROP_MODULE_OS_VERSION_VALUE_WINDOWS = "windows";

  /**
   * The property name that defines the Linux OS version name
   */
  public final static String PROP_MODULE_OS_VERSION_VALUE_LINUX = "linux";

  /**
   * The property name that defines the MacOS version name
   */
  public final static String PROP_MODULE_OS_VERSION_VALUE_MAC = "mac";

  /**
   * The property name that defines the module name. This is optional.
   */
  public final static String PROP_NAME = "name";

  /**
   * The property name that defines the module base class. This is required.
   */
  public static final String PROP_BASE_MODULE_CLASS = "base-module-class";

  /**
   * The property name that defines the module's dependencies to other module
	 * classes. This is optional.
   */
  public static final String PROP_DEPENDS_ON_MODULE_CLASSES = "depends-on-module-classes";
  
  /**
   * The property name that defines the module's dependencies to other module
   * classes. This is optional.
   */
  public static final String PROP_MODULES_GROUP = "modules-group";

	/**
   * This method returns the GUI component if there is one, or null otherwise.
   * 
   * @return the GUI component if there is one, null otherwise.
   */
  public Object getGUIComponent();
}
