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
package aors.test.testTool;

import aors.test.testTool.manager.TestManager;

/**
 * TestTool - used to execute a set of test cases
 * 
 * @author Mircea Diaconescu
 * @since July 10, 2009
 * @version $Revision: 1.0 $
 */
public class TestTool {

  /**
   * Run the test cases with the given args.
   * 
   * @param args
   *          command line arguments
   */
  public static void main(String[] args) {
    // get the path of app
    String appPath = System.getProperty("user.dir");
    appPath += System.getProperty("file.separator");
    appPath += "projects" + System.getProperty("file.separator") + "testCases";
    appPath += System.getProperty("file.separator");
    appPath += "testsXML";
    appPath += System.getProperty("file.separator");

    // create a test manager
    TestManager testManager = null;

    // run load file, compile and run the test
    if (args.length > 0 && args[0] != null) {
      System.out
          .println("____________________________________________________________________________________");
      System.out.println("\nDirectory in use: '" + appPath + "'");
      System.out.println("Start performing test for file: '" + args[0] + "'");
      System.out.println("This may take some time, please be patient.\n");

      testManager = new TestManager(appPath + args[0]);
      testManager.runTest();

    } else {
      System.out
          .println("|##############################################################|");
      System.out
          .println("|##                                                          ##|");
      System.out
          .println("|##  The application requires the filename as argument!      ##|");
      System.out
          .println("|##                                                          ##|");
      System.out
          .println("|##  USAGE: java aors.test.testTool.TestTool myTestFile.xml  ##|");
      System.out
          .println("|##                                                          ##|");
      System.out
          .println("|##  Files are loaded from 'testCase/testsXML' directory.    ##|");
      System.out
          .println("|##                                                          ##|");
      System.out
          .println("|##  Log files are generated in 'testsJava/myTestFile/log'.  ##|");
      System.out
          .println("|##                                                          ##|");
      System.out
          .println("|##############################################################|");
    }

  }

}
