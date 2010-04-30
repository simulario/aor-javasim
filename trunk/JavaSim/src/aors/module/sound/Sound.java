/*************************************************************************************************************
 * AgentObject-Object-Relationship (AOR) Simulation
 * Ontologically well-founded and closer-to-reality simulation of complex discrete event scenarios
 *
 * AOR-JSim v.2
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
package aors.module.sound;

/**
 * Sound - is the interface that has to be implemented by any sound type. It
 * offers basic functionalities, such as PLAY, STOP and so on.
 * 
 * @author Mircea Diaconescu
 * @date November 21, 2009
 * @version $Revision: 1.0 $
 */
public interface Sound extends Runnable {
  /**
   * Plays the sound if this is not already playing. If the sound is already
   * playing then calling this method must have no effect.
   */
  public void play();

  /**
   * Stop the sound if it is currently playing.
   */
  public void stop();

  /**
   * Gets the playing state of the sound
   * 
   * @return true if the sound is currently playing, false otherwise
   */
  public boolean isPlaying();

  /**
   * Stops the sound if is playing and clear the resources that are got by this
   * sound such as closing streams, etc.
   */
  public void close();

  /**
   * Define auto loop function for a MIDI file
   * 
   * @param autoLoop
   *          true means that the MIDI file will be played infinitely and false
   *          means that will be played just once (default is played just once)
   */
  public void setSoundFileAutoLoop(boolean autoLoop);

}
