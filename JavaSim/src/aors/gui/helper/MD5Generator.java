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
 * File: MD5Generator.java
 * 
 * Package: aors.gui.helper
 *
 **************************************************************************************************************/
package aors.gui.helper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

/**
 * MD5Generator
 * 
 * @author Jens Werner
 * @since 19.01.2010
 * @version $Revision$
 */
public class MD5Generator {

  public static String getMD5(String input) {

    StringBuffer stringBuffer = new StringBuffer();
    try {
      MessageDigest md5 = MessageDigest.getInstance("MD5");
      md5.update(input.getBytes());
      Formatter f = new Formatter(stringBuffer);
      for (byte b : md5.digest()) {
        f.format("%02x", b);
      }
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return stringBuffer.toString();
  }
}
