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
 * File: FileMonitor.java
 * 
 * Package: aors.gui.helper
 *
 **************************************************************************************************************/
package aors.gui.helper;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * FileMonitor
 * 
 * @author Jens Werner
 * @since 18.01.2010
 * @version $Revision: 1.0 $
 */
public class FileMonitor {

  private Timer timer;
  private long pollingInterval;
  private Map<File, Long> fileMap;
  private List<WeakReference<FileListener>> listeners;
  private FileMonitorNotifier fileMonitorNotifier;

  private final boolean debug = false;

  public FileMonitor(long pollingInterval) {
    this.fileMap = new HashMap<File, Long>();
    this.listeners = new ArrayList<WeakReference<FileListener>>();
    this.pollingInterval = pollingInterval;

    this.timer = new Timer(true);
  }

  public void startFileMonitorNotifier() {
    this.fileMonitorNotifier = new FileMonitorNotifier();
    this.timer.schedule(this.fileMonitorNotifier, 0, pollingInterval);
    if (debug)
      System.out.println("Start file monitor");
  }

  public void stopFileMonitorNotifier() {
    this.fileMonitorNotifier.cancel();
    this.timer.purge();
    if (debug)
      System.out.println("Stop file monitor");
  }

  public void addFile(File file) {
    if (!this.fileMap.containsKey(file)) {
      long modifiedTime = file.exists() ? file.lastModified() : -1;
      this.fileMap.put(file, modifiedTime);
      if (debug)
        System.out.println("NEW File in Map (" + this.fileMap.size() + "). "
            + file);
    }
  }

  public void updateFile(File file) {
    if (this.fileMap.containsKey(file)) {
      long modifiedTime = file.exists() ? file.lastModified() : -1;
      this.fileMap.put(file, modifiedTime);
      if (debug)
        System.out.println("UPDATE File in Map (" + this.fileMap.size() + "). "
            + file);
    }
  }

  public void removeFile(File file) {
    this.fileMap.remove(file);
    if (debug)
      System.out.println("REMOVE File from Map (" + this.fileMap.size() + "). "
          + file);
  }

  public void removeAllFiles() {
    this.fileMap.clear();
    if (debug)
      System.out.println("REMOVE all Files from Map.");
  }

  /**
   * Add listener to this file monitor.
   * 
   * @param fileListener
   *          Listener to add.
   */
  public void addListener(FileListener fileListener) {

    // Don't add if its already there
    for (WeakReference<FileListener> listener : this.listeners) {

      FileListener l = listener.get();
      if (listener == l)
        return;
    }

    // Use WeakReference to avoid memory leak if this becomes the
    // sole reference to the object.
    this.listeners.add(new WeakReference<FileListener>(fileListener));
  }

  /**
   * Remove listener from this file monitor.
   * 
   * @param fileListener
   *          Listener to remove.
   */
  public void removeListener(FileListener fileListener) {

    Iterator<WeakReference<FileListener>> i = this.listeners.iterator();
    while (i.hasNext()) {
      FileListener listener = i.next().get();
      if (listener == fileListener) {
        i.remove();
        break;
      }
    }
  }

  private class FileMonitorNotifier extends TimerTask {

    @Override
    public void run() {

      for (File file : FileMonitor.this.fileMap.keySet()) {
        long lastModifiedTime = FileMonitor.this.fileMap.get(file);
        long newModifiedTime = file.exists() ? file.lastModified() : -1;

        if (newModifiedTime != lastModifiedTime) {

          // Register new modified time
          FileMonitor.this.fileMap.put(file, new Long(newModifiedTime));

          // Notify listeners
          Iterator<WeakReference<FileListener>> i = FileMonitor.this.listeners
              .iterator();
          while (i.hasNext()) {
            FileListener listener = i.next().get();

            // Remove from list if the back-end object has been GC'd
            if (listener == null) {
              i.remove();
            } else {
              listener.fileChange(file);
            }
          }
        }
      }
    }

  }
}
