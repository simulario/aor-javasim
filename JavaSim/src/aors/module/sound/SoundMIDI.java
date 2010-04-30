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

import java.io.File;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Synthesizer;

import aors.controller.Project;

/**
 * SoundMIDI
 * 
 * @author Andreas Freier (business.af@web.de),Mircea Diaconescu
 * @date February 9, 2009
 * @version $Revision: 1.1 $
 */
public class SoundMIDI implements Sound {

  private Receiver rcvr;
  private int channel;
  private int[][] notes;

  // the array of MIDI channels
  private MidiChannel[] mc;

  // the MIDI Synthesizer
  private Synthesizer synthesizer;

  // the maximum allowed number of MIDI files to play simultaneous
  private final static int MAX_MIDI_SIMULTANEOUS_PLAYS = 32;

  // the number of currnet active MIDI files
  private static int currentActiveMidiFiles = 0;

  // control the MIDI play Thread
  private boolean running = false;

  // this flag is used to be sure that the sound was stoped
  private boolean active = false;

  // the MIDI File Sequencer
  private Sequencer sequencer;

  // this flag determine if we play a midi file or we play a note sequence
  private boolean playMidiFile = false;

  // the path to the MIDI file to play if there is any
  private File pathToFile;

  /**
   * Create a new MIDI player object. This plays a sequence of notes and not a
   * file.
   * 
   * @param r
   *          the receiver
   * @param channel
   *          the channel where this can be played
   * @param notes
   *          the notes to by played
   * @param channels
   *          channels of the notes to by played
   * @param usedChannels
   *          the used channels
   */
  public SoundMIDI(int[][] notes) {
    try {
      this.synthesizer = MidiSystem.getSynthesizer();
      this.synthesizer.open();
      this.rcvr = this.synthesizer.getReceiver();
      mc = this.synthesizer.getChannels();
    } catch (MidiUnavailableException e) {
      System.out.println("MidiUnavailableException in Sound.java");
    }
    this.channel = 0;
    this.notes = notes;
    this.playMidiFile = false;
  }

  /**
   * Create a new MIDI player. This create a MIDI player that gets data from a
   * MIDI file. Notice that the allowed extensions are: ".mid" and ".midi"
   * 
   * @param projectPath
   *          the project path
   * @param localPath
   *          the local path for the file related to project path media
   *          directory
   */
  public SoundMIDI(File projectPath, String localPath) {

    // the source is a MIDI file
    this.playMidiFile = true;

    File path = null;

    if (projectPath != null) {
      // try to get the file from the project media directory
      path = new File(projectPath.getPath() + File.separator
          + Project.MEDIA_FOLDER_NAME + File.separator
          + Project.MEDIA_SOUNDS_FOLDER_NAME + File.separator + localPath);
    }

    // the file is not in the project media directory ?
    if (path == null || !path.isFile()) {
      // try to get the file from the project media directory
      path = new File(System.getProperty("user.dir") + File.separator
          + Project.MEDIA_FOLDER_NAME + File.separator
          + Project.MEDIA_SOUNDS_FOLDER_NAME + File.separator + localPath);
    }

    // the MIDI file is not in the project media directory neither in the
    // global media directory...so it can't be found and can't be played
    if (!path.isFile()) {
      System.out.println("Warning: the MIDI file: " + localPath
          + " cannot be found!");
      return;
    }

    // save the final path to the file
    this.pathToFile = path;

    // get the MIDI sequencer
    try {
      this.sequencer = MidiSystem.getSequencer();
    } catch (MidiUnavailableException e) {
      e.printStackTrace();
    }
  }

  /**
   * Set the instrument to use
   * 
   * @param channel
   *          the channel for which we set the instrument
   * @param instrument
   *          the instrument to use
   */
  public void setInstrument(int instrument) {
    mc[this.channel].programChange(instrument);
  }

  /**
   * Get a Midi message
   * 
   * @param channel
   *          the channel
   * @param note
   *          the note
   * @param velocity
   *          the playing velocity
   * @return the Midi message
   */
  private MidiMessage getNoteOnMessage(int channel, int note, int velocity) {
    try {
      ShortMessage msg = new ShortMessage();
      msg.setMessage(ShortMessage.NOTE_ON, channel, note, velocity);
      return (MidiMessage) msg;
    } catch (InvalidMidiDataException e) {
      e.printStackTrace();
    }
    return null;
  }

  private MidiMessage getNoteOffMessage(int channel, int note, int velocity) {
    try {
      ShortMessage msg = new ShortMessage();
      msg.setMessage(ShortMessage.NOTE_OFF, channel, note, velocity);
      return (MidiMessage) msg;
    } catch (InvalidMidiDataException e) {
      e.printStackTrace();
    }
    return null;
  }

  private void noteOn(int channel, int note, int velocityON) {
    try {
      rcvr.send(getNoteOnMessage(channel, note, velocityON), 0);
    } catch (IllegalStateException e) {
      e.printStackTrace();
    }
  }

  private void noteOff(int channel, int note, int velocityOFF) {
    try {
      rcvr.send(getNoteOffMessage(channel, note, velocityOFF), 0);
    } catch (IllegalStateException e) {
    }
  }

  /**
   * Play the MIDI notes that are defined.
   */
  private void playNotes() {
    for (int i = 0; i < notes[0].length; i++) {
      noteOn(channel, notes[0][i], notes[2][i]);
      try {
        Thread.sleep(notes[1][i]);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      noteOff(channel, notes[0][i], 0);

      // sound was forced to stop...
      if (!this.running) {
        break;
      }
    }
  }

  /**
   * Set new notes to be played
   * 
   * @param notes
   *          - the new notes array
   */
  public void setNotes(int[][] notes) {
    this.notes = notes;
  }

  /**
   * Gets current MIDI notes
   * 
   * @return - the array with current MIDI notes.
   */
  public int[][] getNotes() {
    return notes;
  }

  @Override
  public void run() {
    // the sound is becoming active
    this.active = true;

    // play MIDI file
    if (this.playMidiFile && this.pathToFile != null) {

      // the maximum number of midi files playing simultaneous was reached
      if (currentActiveMidiFiles > MAX_MIDI_SIMULTANEOUS_PLAYS) {
        return;
      }

      try {

        // create the sequencer for the MIDI file
        this.sequencer.setSequence(MidiSystem.getSequence(this.pathToFile));

        if (!this.sequencer.isOpen()) {
          this.sequencer.open();

          // a file was started playing, so more MIDI file are playing now
          currentActiveMidiFiles++;
        }

      } catch (MidiUnavailableException e) {
        e.printStackTrace();
      } catch (InvalidMidiDataException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }

      // start playing
      if (this.sequencer.isOpen()) {
        try {
          this.sequencer.start();
          while (this.sequencer.isRunning() && this.running) {
            try {
              Thread.sleep(50);
              Thread.yield();
            } catch (InterruptedException ignore) {
              if (this.sequencer.isOpen()) {
                this.sequencer.stop();
                this.sequencer.close();
              }
              break;
            }
          }
          // Close the MidiDevice & free resources
          if (this.sequencer.isOpen()) {

            this.sequencer.stop();
            this.sequencer.close();
          }
        } catch (Exception e) {
          // nothing to do
        }
      }
    }
    // play MIDI notes
    else if (!this.playMidiFile) {
      // play MIDI notes
      playNotes();
    }

    // the sound is inactive and all resources are closed
    this.active = false;

    // a file was finished playing, so less MIDI file are playing now
    currentActiveMidiFiles--;
  }

  @Override
  public synchronized void play() {
    this.running = true;
    (new Thread(this)).start();
  }

  @Override
  public synchronized void stop() {
    this.running = false;
  }

  @Override
  public synchronized boolean isPlaying() {
    return this.active;
  }

  @Override
  public synchronized void close() {
    this.running = false;

    if (this.sequencer != null && this.sequencer.isOpen()) {
      if (this.sequencer.isRunning()) {
        this.sequencer.stop();
      }
      this.sequencer.close();
    }

    if (this.synthesizer != null && this.synthesizer.isOpen()) {
      this.rcvr.close();
      this.synthesizer.close();
    }
  }

  @Override
  public void setSoundFileAutoLoop(boolean autoLoop) {
    if (this.sequencer != null) {
      if (autoLoop) {
        this.sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
      } else {
        this.sequencer.setLoopCount(1);
      }
    }
  }

}
