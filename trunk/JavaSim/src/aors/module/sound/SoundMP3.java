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

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

import aors.controller.Project;

/**
 * SoundMP3 - takes care of playing MP3 sounds
 * 
 * @author Andreas Freier (business.af@web.de), Mircea Diaconescu
 * @date February 9, 2009
 * @version $Revision$
 */
public class SoundMP3 implements Sound {
  private AudioInputStream din;
  private File soundFile;
  private AudioInputStream in;
  private AudioFormat baseFormat;
  private AudioFormat decodedFormat;
  private DataLine.Info info;
  private SourceDataLine line;
  private byte[] data;
  private long duration;
  private String path;
  private String localPath;
  private File projectPath;
  private boolean running = false;

  // determine if the sound was stopped (all streams closed and so on)
  private boolean alive = false;

  // the maximum number of the simultaneous MP3 active sounds
  private static final int MAX_MP3_SIMULTANEOUS_PLAYS = 32;

  // the number of current playing MP3 sounds
  private static int activeMp3Sounds = 0;

  // flag for auto looping of this sound
  private boolean autoLoop = false;

  /**
   * Create a new PlayMp3Sound object
   * 
   * @param duration
   *          the sound duration
   * @param path
   *          the file path to be played
   * 
   * @param projectPath
   *          - the project path where to look for media files
   * 
   */
  public SoundMP3(long duration, String path, File projectPath) {
    din = null;

    // 4k is a reasonable transfer size.
    data = new byte[4096];

    this.duration = duration;
    this.projectPath = projectPath;
    this.localPath = path;

    if (this.projectPath == null) {
      this.projectPath = new File("");
    }

    // look for the file in the project media sounds directory
    this.path = this.projectPath.getPath() + File.separator
        + Project.MEDIA_FOLDER_NAME + File.separator
        + Project.MEDIA_SOUNDS_FOLDER_NAME + File.separator + this.localPath;

    if ((new File(this.path)).isFile()) {
      // we have found the file in the project directory, so we use this
      // file
      return;
    }

    // look for the file in the global media sounds directory
    this.path = System.getProperty("user.dir") + File.separator
        + Project.MEDIA_FOLDER_NAME + File.separator
        + Project.MEDIA_SOUNDS_FOLDER_NAME + File.separator + this.localPath;

    if (!(new File(this.path)).isFile()) {
      System.out.println("Warning: the mp3 file '" + path + "' was not found.");
    }
  }

  @Override
  public void run() {

    this.alive = true;

    // max number of simultaneous plays was reached,
    // so the sound will not be played
    if (activeMp3Sounds > MAX_MP3_SIMULTANEOUS_PLAYS) {
      return;
    }

    this.running = true;

    try {
      soundFile = new File(path);
      in = AudioSystem.getAudioInputStream(soundFile);
      baseFormat = in.getFormat();
      decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
          baseFormat.getSampleRate(), 16, baseFormat.getChannels(), baseFormat
              .getChannels() * 2, baseFormat.getSampleRate(), false);
      din = AudioSystem.getAudioInputStream(decodedFormat, in);
      info = new DataLine.Info(SourceDataLine.class, decodedFormat);
      line = (SourceDataLine) AudioSystem.getLine(info);
      line.open(decodedFormat);

      if (line != null) {

        // Start the line.
        line.start();

        // new sound is playing
        synchronized (this) {
          activeMp3Sounds++;
        }

        int nBytesRead;
        double start = System.currentTimeMillis();
        double time = System.currentTimeMillis();

        if (this.autoLoop) {
          while (this.running) {
            while (((nBytesRead = din.read(data, 0, data.length)) != -1)
                && this.running) {
              line.write(data, 0, nBytesRead);
            }

            // Stop
            line.flush();
            line.stop();
            line.close();
            din.close();

            in = AudioSystem.getAudioInputStream(soundFile);
            baseFormat = in.getFormat();
            decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                baseFormat.getSampleRate(), 16, baseFormat.getChannels(),
                baseFormat.getChannels() * 2, baseFormat.getSampleRate(), false);
            din = AudioSystem.getAudioInputStream(decodedFormat, in);
            info = new DataLine.Info(SourceDataLine.class, decodedFormat);
            line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(decodedFormat);

            // Start the line.
            line.start();
          }
        } else {
          while (((nBytesRead = din.read(data, 0, data.length)) != -1)
              && (this.duration > (time - start) || this.duration <= 0)
              && this.running) {
            line.write(data, 0, nBytesRead);
            time = System.currentTimeMillis();
          }
        }

        // Stop
        line.flush();
        line.stop();
        line.close();
        din.close();
      }
    } catch (Exception e) {
      System.out.println("Error playing mp3 File ! PATH: " + this.path + "  "
          + e.toString());
    } finally {
      if (din != null) {
        try {
          din.close();
        } catch (IOException e) {
        }
      }
    }

    // sound was finished
    synchronized (this) {
      activeMp3Sounds--;
    }

    this.alive = false;
  }

  @Override
  public synchronized void play() {
    if (this.running) {
      this.running = false;
    }

    (new Thread(this)).start();
  }

  @Override
  public synchronized void stop() {
    this.running = false;
  }

  @Override
  public synchronized boolean isPlaying() {
    return this.alive;
  }

  @Override
  public synchronized void close() {
    this.running = false;
    this.alive = false;
  }

  @Override
  public void setSoundFileAutoLoop(boolean autoLoop) {
    this.autoLoop = autoLoop;
  }

}
