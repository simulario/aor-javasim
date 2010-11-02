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
 **************************************************************************************************************/

package aors.test.testTool.manager;

import java.io.File;

import aors.controller.SimulationManager;
import aors.data.DataBus;
import aors.exceptions.SimulatorException;

/**
 * TestManager - used to run and execute a set of test cases
 * 
 * @author Mircea Diaconescu
 * @since July 10, 2009
 * @version $Revision$
 */

public class TestManager {
  /** the path to the XML simulation file **/
  private File file;

  /** the content of the XML simulation file **/
  private String xmlContent;

  /** the simulation manager - used to load, build and run the simulation **/
  private SimulationManager simulationManager;

  /**
   * Create a test manager instance
   * 
   * @param absolutePath
   *          the absolute path to the XML simulation file
   */
  public TestManager(String absolutePath) {
    this.file = new File(absolutePath);
    this.xmlContent = "";
    this.simulationManager = new SimulationManager();
  }

  /**
   * Utility function that delete a directory (empty or non empty)
   * 
   * @param dir
   *          the directory to be deleted
   * @return true when successful
   */
  private boolean deleteDir(File dir) {
    if (dir.isDirectory()) {
      String[] children = dir.list();
      for (int i = 0; i < children.length; i++) {
        boolean success = deleteDir(new File(dir, children[i]));
        if (!success) {
          return false;
        }
      }
    }

    // The directory is now empty so delete it
    return dir.delete();
  }

  /**
   * Generate the Java source code and compile it.
   */
  public void runTest() {
    /** activate the logger as full logger **/
    try {
      ((DataBus) simulationManager.getDataBus())
          .initLogger(DataBus.LoggerType.FULL_XML_LOGGER);
    } catch (Exception e) {
      e.printStackTrace();
    }

    /** delete the directory of this test if exists **/
    File prjDir = new File(file.getParentFile().getParentFile()
        + System.getProperty("file.separator") + "testsJava"
        + System.getProperty("file.separator")
        + file.getName().substring(0, file.getName().lastIndexOf(".")));

    System.out.print("Performing cleaning...please wait...");
    if (prjDir.isDirectory()) {
      this.deleteDir(prjDir);
    }
    System.out.println("OK");

    /*** Load the XML file ***/
    System.out.print("Loading XML simulation description...please wait...");
    this.xmlContent = simulationManager.readXMLFile(file);

    if (this.xmlContent.length() < 1) {
      System.out.println("Loading failed.\n");
      return;
    }
    System.out.println("OK");

    /*** Validate the Simulation according with the schema ***/
    System.out.print("Validating simulation description...please wait...");
    this.simulationManager.newProject();
    this.simulationManager.getProject().setDirectory(
        file.getParentFile().getParentFile()
            + System.getProperty("file.separator") + "testsJava");
    this.simulationManager.getProject().setName(
        file.getName().substring(0, file.getName().lastIndexOf(".")));
    this.simulationManager.getProject().setSimulationDescription(
        this.xmlContent);

    if (!simulationManager.validateSimulation()) {
      System.out.println("Simulation validation failed!");
      return;
    }
    System.out.println("OK");

    /*** Generate the Java code ***/
    System.out.print("Generating Java code...please wait...");
    if (!simulationManager.generate()) {
      System.out.println("Code generation failed!");
      return;
    }
    System.out.println("OK");

    /*** Compile the Java code ***/
    System.out.print("Compiling the generated Java code...please wait...");
    if (!this.simulationManager.getProject().compile()) {
      System.out.println("Compilation of the code failed!\n");
      return;
    }
    System.out.println("OK");

    /*** Run the simulation ***/
    System.out.print("Running the simulation...please wait...");

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    try {
      this.simulationManager.getProject().instantiateSimulation();
    } catch (SimulatorException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    this.simulationManager.getProject().prepareSimulation(file.getName(), null,
        true);
    if (simulationManager.getProject().getSimulation() != null) {
      // run the simulation
      simulationManager.getProject().getSimulation().runSimulation();
      // System.gc();
    }
    System.out.println("OK");

    System.out.println("Task finished for file: " + this.file.toString());
  }
}
