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
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import aors.controller.SimulationDescription;
import aors.data.java.ObjektDestroyEvent;
import aors.data.java.ObjektInitEvent;
import aors.data.java.SimulationEvent;
import aors.data.java.SimulationStepEvent;
import aors.model.envevt.EnvironmentEvent;
import aors.model.envevt.InMessageEvent;
import aors.model.envevt.OutMessageEvent;
import aors.module.InitialState;
import aors.module.Module;
import aors.module.sound.gui.TabSound;
import aors.util.jar.JarUtil;

/**
 * Sound
 * 
 * @author Andreas Freier (business.af@web.de), Mircea Diaconescu
 * @date March 9, 2009
 * @version $Revision: 1.1 $
 */
public class SoundController extends Thread implements Module {

  /**
   * The GUI component associated with the sound module
   */
  private TabSound tabSound;

  private final String ANIMATION_UI_ELEMENT_NAME = "AnimationUI";
  private final String EVENT_TYPE = new String("eventType");
  private final String MESSAGE_TYPE = new String("messageType");
  private final String DURATION = new String("duration");
  private final String SOUNDFILE = new String("soundFile");
  private final String INTRO_SOUND_FILE = "introSoundFile";
  private final String MIDI_SOUND = new String("MidiSound");
  private final String NOTE_SEQUENCE = new String("noteSequence");
  private final String INSTRUMENT_NO = new String("instrumentNo");
  private final String INSTRUMENT_NAME = new String("instrumentName");
  private final String EVENT_NODES = new String("EventAppearance");
  private final String SONIFICATIONMAP = new String("SonificationMap");
  private final String PROPERTY = new String("property");
  private final String SOUND_PROPERTY = new String("soundProperty");
  private static final String MAP_TYPE = "mapType";
  public static final String MAP_TYPE_CASEWISE = "caseWise";
  public static final String MAP_TYPE_POLYNOMIAL = "polynomial";
  public static final String MAP_TYPE_ENUM_MAP = "enumerationMap";
  public static final String MAP_TYPE_EQUALITY_CASE_WISE = "equalityCaseWise";
  private final String NOTE = new String("note");
  private final String VOLUME = new String("volume");
  private final String A0 = new String("a0");
  private final String A1 = new String("a1");
  private final String A2 = new String("a2");
  private final String A3 = new String("a3");
  private final String V0 = new String("v0");
  private final String V1 = new String("v1");
  private final String V2 = new String("v2");
  private final String V3 = new String("v3");
  private final String V4 = new String("v4");
  private final String EVENT_TYPE_DEFAULT = new String("CollisionEvent");
  private final long DURATION_DEFAULT = -1;
  private final String[] NOTE_SEQUENCE_DEFAULT = { "50", "500", "90" };
  private final String INSTRUMENT_DEFAULT = new String("0");
  private final String typesNameDelimiter = ":";

  // the flag for mute/unmute the sound
  private boolean soundOn;

  // the list with events coming from environment
  private ConcurrentLinkedQueue<EnvironmentEvent> eventQ;

  // controls the thread of the sound module
  private boolean running = false;

  // the thread of the sound module
  private Thread soundThread = null;

  // The project directory
  private File projectDirectory = null;

  // the Map with sonification maps
  private HashMap<String, SonificationMap> sonificationMap;

  /**
   * The sounds map. The key is the event name, the value is the sound thread.
   */
  private HashMap<String, Sound> soundsMap;

  /**
   * The map with the active state of the sound for an event. This determine
   * which of the events that have sounds assigned will play. This is controlled
   * by the GUI component to disable only some of the sounds not all.
   */
  private HashMap<String, Boolean> soundEventActiveStateMap;

  /**
   * This is used to fulfill the table from the GUI component
   */
  private HashMap<String, String> soundEventsTypeNameMap;

  /**
   * This thread will hold a player that plays a sound in the background
   */
  private Sound backgroundSound = null;

  /**
   * This flag keeps the simulation running state
   */
  private boolean runSimulationState = false;

  // special parameter that defines the acting way of sonification map changes.
  // If true then the changes starts always from the value defined by the user
  // in the XML.
  private boolean useCloneMapValues = true;

  /**
   * Create a new Sound object
   */
  public SoundController() {
    // initialize the module libraries
    this.initModuleLibraries();

    // in this moment nos sonification map is defined
    this.sonificationMap = new HashMap<String, SonificationMap>();

    // in this moment we have an empty soundMap
    this.soundsMap = new HashMap<String, Sound>();

    // in this moment no type names are defined
    this.soundEventsTypeNameMap = new HashMap<String, String>();

    // create the GUI component
    this.tabSound = new TabSound(this);

    // create the sounds-events active state empty map
    this.soundEventActiveStateMap = new HashMap<String, Boolean>();

    // enable sound
    this.setEnabled(true);

    // the sound is active by default
    soundOn = true;

    // empty events queue
    eventQ = new ConcurrentLinkedQueue<EnvironmentEvent>();

    // the module thread is not running yet
    running = false;

    this.soundThread = new Thread(this);
  }

  /**
   * Initialize the module libraries by unpacking the Jars, loading and setting
   * class paths.
   */
  private void initModuleLibraries() {
    // local path in the temporarily directory for this module
    String localTmpPath = "soundModule";

    // path to jar
    String jarPath = System.getProperty("user.dir") + File.separator
        + "modules" + File.separator + "soundModule.jar";

    // extract the jar files for sound module
    try {
      JarUtil.extractFileFromJar(jarPath, localTmpPath, "lib", "jl1.0.jar");
      JarUtil.extractFileFromJar(jarPath, localTmpPath, "lib",
          "mp3spi1.9.4.jar");
      JarUtil.extractFileFromJar(jarPath, localTmpPath, "lib",
          "tritonus_share-0.3.6.jar");
    } catch (IOException ex) {
      ex.printStackTrace();
    }

    // add this path in the library path...
    JarUtil.setLibraryPath(localTmpPath);

    // load jars from that temporarily directory (sound required jars)
    JarUtil.loadJar(localTmpPath, "tritonus_share-0.3.6.jar");
    JarUtil.loadJar(localTmpPath, "mp3spi1.9.4.jar");
    JarUtil.loadJar(localTmpPath, "jl1.0.jar");
  }

  /**
   * Play a sound for an event, if the event has an assigned sound.
   * 
   * @param event
   *          the event for which want to play the sound.
   */
  public void playSound(EnvironmentEvent event) {
    // a precaution check... usually the event can't be null
    if (event == null) {
      return;
    }

    String eventTypeName = event.getType();

    // manage the OutMessageEvents
    if (event instanceof OutMessageEvent) {
      String messageTypeName = ((OutMessageEvent) event).getMessage().getType();

      // check if we deal with out messages generic or with out message that
      // have a sound assigned for a specific type and not for any out message
      if (this.soundsMap.get(eventTypeName + this.typesNameDelimiter
          + messageTypeName) != null) {
        eventTypeName += (this.typesNameDelimiter + messageTypeName);
      }
    }

    // play this sound if need to play and is allowed to play.
    if (this.soundOn
        && this.soundEventActiveStateMap.get(eventTypeName) != null
        && this.soundEventActiveStateMap.get(eventTypeName)
        && this.soundsMap.get(eventTypeName) != null) {

      SonificationMap sMap = this.sonificationMap.get(eventTypeName);
      if (sMap != null
          && this.soundsMap.get(eventTypeName) instanceof SoundMIDI) {
        boolean fromMsg = eventTypeName.contains(this.typesNameDelimiter);

        Object evtPropValue = null;

        try {
          evtPropValue = getValue(sMap.getPropertyName(), event, fromMsg);

        } catch (Exception ex) {
        }

        int instrument = -1;
        if (sMap.getSoundPropertyName().equals(NOTE)) {
          ((SoundMIDI) this.soundsMap.get(eventTypeName)).setNotes(sMap
              .mapNotes(evtPropValue));
        }
        if (sMap.getSoundPropertyName().equals(DURATION)) {
          ((SoundMIDI) this.soundsMap.get(eventTypeName)).setNotes(sMap
              .mapDuration(evtPropValue));
        }
        if (sMap.getSoundPropertyName().equals(VOLUME)) {
          ((SoundMIDI) this.soundsMap.get(eventTypeName)).setNotes(sMap
              .mapVolume(evtPropValue));
        }
        if (sMap.getSoundPropertyName().equals(INSTRUMENT_NO)) {
          instrument = sMap.mapInstrument(evtPropValue);
          ((SoundMIDI) this.soundsMap.get(eventTypeName))
              .setInstrument(instrument);
        }

      }

      this.soundsMap.get(eventTypeName).play();
    }
  }

  /**
   * Set the sound active or inactive for a specified event
   * 
   * @param eventName
   *          the event name as string
   * @param activeState
   *          the active state of the sound for this event
   */
  public void enableSoundForEvent(String eventName, boolean activeState) {
    if (this.soundEventActiveStateMap.get(eventName) != activeState) {
      this.soundEventActiveStateMap.remove(eventName);
      this.soundEventActiveStateMap.put(eventName, activeState);
    }
  }

  /**
   * Enable/Disable the sound
   * 
   * @param enable
   *          the enable state: true = enabled, false = disabled
   */
  public synchronized void setEnabled(boolean enable) {
    this.soundOn = enable;

    // play the background sound if it has to be played
    this.playBackgroundSound(enable);
  }

  /**
   * Gets the sound enabled status
   * 
   * @return true when sound is on, false otherwise
   */
  public synchronized boolean getEnabled() {
    return this.soundOn;
  }

  private int[][] extractNotes(String noteSequence) {
    if (!noteSequence.equals("")) {
      String[] temp1 = noteSequence.split(" ");
      String[] temp2;
      int field[][] = new int[3][temp1.length];
      for (int i = 0; i < temp1.length; i++) {
        temp2 = temp1[i].split("/");
        for (int j = 0; j < temp2.length; j++) {
          if (temp2[j].equals("")) {
            field[j][i] = Integer.parseInt(NOTE_SEQUENCE_DEFAULT[j]);
          } else {
            field[j][i] = Integer.parseInt(temp2[j]);
          }
        }
        for (int j = temp2.length; j < field.length; j++) {
          field[j][i] = Integer.parseInt(NOTE_SEQUENCE_DEFAULT[j]);
        }
      }
      return field;
    }
    return null;
  }

  @Override
  public void run() {
    // thread is ready to run
    this.running = true;

    // start the execution cycle
    while (running) {
      if (!eventQ.isEmpty()) {
        while (!eventQ.isEmpty() && running) {
          EnvironmentEvent envEvent = eventQ.poll();
          if (envEvent != null) {
            playSound(envEvent);
          }
        }
      } else {
        try {
          Thread.sleep(10);
          Thread.yield();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * Stop the module sound thread by ending the run() method execution.
   */
  public synchronized void stopSound() {
    running = false;
  }

  /**
   * 
   * @param propertyName
   *          the field name
   * @param env
   *          the environment event
   * @param fromMsg
   *          - specifies if the message
   * @return
   */
  public Object getValue(String propertyName, EnvironmentEvent env,
      boolean fromMsg) {

    Object result = null;

    try {
      java.lang.reflect.Field field;
      if (!fromMsg) {
        field = env.getClass().getDeclaredField(propertyName);
      } else {
        if (env instanceof OutMessageEvent) {
          field = ((OutMessageEvent) env).getMessage().getClass()
              .getDeclaredField(propertyName);
          field.setAccessible(true);
          result = field.get(((OutMessageEvent) env).getMessage());
        }
        if (env instanceof InMessageEvent) {
          field = ((InMessageEvent) env).getMessage().getClass()
              .getDeclaredField(propertyName);
          field.setAccessible(true);
          result = field.get(((InMessageEvent) env).getMessage());
        }

      }
    } catch (SecurityException e) {
      e.printStackTrace();
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    return result;
  }

  /**
   * Create the sound map from the DOM node list.
   * 
   * @param soundEventsList
   *          the DOM excerpt that contains the sound-events definitions
   * @return the map that with the events and sound threads.
   */
  private void createSoundsMap(NodeList soundEventsList) {

    // reset the event sounds queue
    eventQ = new ConcurrentLinkedQueue<EnvironmentEvent>();

    // empty node list -> empty map with playable sounds
    if (soundEventsList == null) {
      return;
    }

    boolean midiSoundFound = false;
    boolean instrumentFound = false;

    for (int i = 0; i < soundEventsList.getLength(); i++) {
      // nothing set yet for this sound
      String durationValue = "";
      String eventTypeValue = "";
      String messageTypeValue = "";
      String soundFileValue = "";
      String instrumentValue = "";
      String noteSequenceValue = "";
      String propertyValue = "";
      String soundPropertyValue = "";
      Object property = 0;
      SonificationMap sonificationMap = null;
      MIDIMap midiMap = new MIDIMap();
      String functionType = "";
      String a0Value = "";
      String a1Value = "";
      String a2Value = "";
      String a3Value = "";
      int v0Value = 0;
      int v1Value = 0;
      int v2Value = 0;
      int v3Value = 0;
      int v4Value = 0;
      NodeList midiList = null;
      Sound playMIDI = null;
      Sound playSoundFile = null;
      long duration = DURATION_DEFAULT;

      if (soundEventsList.item(i).hasAttributes()) {

        // extract Attributes of EventAppearance
        NamedNodeMap attributes = soundEventsList.item(i).getAttributes();

        // parse attributes: "eventType", "duration", "soundFile"
        for (int j = 0; j < attributes.getLength(); j++) {
          if (attributes.item(j).getNodeName().equals(EVENT_TYPE)) {
            eventTypeValue = attributes.item(j).getNodeValue();
          }
          if (attributes.item(j).getNodeName().equals(MESSAGE_TYPE)) {
            messageTypeValue = attributes.item(j).getNodeValue();
          }
          if (attributes.item(j).getNodeName().equals(DURATION)) {

            durationValue = attributes.item(j).getNodeValue();
            try {
              duration = Long.parseLong(durationValue);
            } catch (Exception ex) {
              duration = DURATION_DEFAULT;
            }
          }

          if (attributes.item(j).getNodeName().equals(SOUNDFILE)) {
            soundFileValue = attributes.item(j).getNodeValue();
          }
        }

        // default event type if none is set in XML: see constant
        // EVENT_TYPE_DEFAULT
        if (eventTypeValue.equals("")) {
          eventTypeValue = EVENT_TYPE_DEFAULT;
        }
        // dealing with out messages
        else if (messageTypeValue != null && messageTypeValue.length() > 0) {
          eventTypeValue += (this.typesNameDelimiter + messageTypeValue);
        }

        // if source file is defined then we deal with MP3 or MIDI files we have
        // notes definitions as sequences
        if (soundFileValue.equals("")) {

          // search for MidiSound Elements
          if (soundEventsList.item(i).hasChildNodes()) {
            midiList = soundEventsList.item(i).getChildNodes();
            for (int k = 0; k < midiList.getLength(); k++) { // Nodes n*
              // "MidiSound"
              if (midiList.item(k).getNodeName().equals(MIDI_SOUND)) {
                midiSoundFound = true;
                if (midiList.item(k).hasAttributes()) {
                  // extract Attributes of MidiSound
                  NamedNodeMap childAttributes = midiList.item(k)
                      .getAttributes();
                  for (int l = 0; l < childAttributes.getLength(); l++) {
                    // Attributes: "instrumentName", "instrumentNo",
                    // "noteSequence"
                    if (childAttributes.item(l).getNodeName().equals(
                        INSTRUMENT_NAME)) {
                      int temp = midiMap.getInstrumentNo(childAttributes
                          .item(l).getNodeValue());
                      if (temp != -1) {
                        instrumentValue = Integer.toString(temp);
                        instrumentFound = true;
                      }
                    }
                    // the "instrumentName" property has priority over
                    // "instrumentNo" property
                    if (childAttributes.item(l).getNodeName().equals(
                        INSTRUMENT_NO)
                        && !instrumentFound) {
                      instrumentValue = childAttributes.item(l).getNodeValue();
                    }
                    if (childAttributes.item(l).getNodeName().equals(
                        NOTE_SEQUENCE)) {
                      noteSequenceValue = childAttributes.item(l)
                          .getNodeValue();
                    }

                  }// for l

                  // set Default Value if "instrumentNo", "velocity" or
                  // "noteSequence" are not set
                  if (instrumentValue.equals("")) {
                    instrumentValue = INSTRUMENT_DEFAULT;
                  }
                  if (noteSequenceValue.equals("")) {
                    noteSequenceValue = "//";
                  }

                } else {
                  instrumentValue = INSTRUMENT_DEFAULT;
                  noteSequenceValue = "//";
                }

                int[][] notes = extractNotes(noteSequenceValue);

                // no duration was defined then is computed as the sum
                // of the duration for all defined note sequences
                if (duration == -1) {
                  duration = 0;
                  for (int d = 0; d < notes[1].length; d++) {
                    duration += notes[1][d];
                  }
                }

                int instrument;
                try {
                  instrument = Integer.parseInt(instrumentValue);
                } catch (Exception e) {
                  instrument = Integer.parseInt(INSTRUMENT_DEFAULT);
                }

                // search for SonifikationMap Elements
                if (midiList.item(k).hasChildNodes()) {
                  NodeList nl = midiList.item(k).getChildNodes();
                  for (int m = 0; m < nl.getLength(); m++) { // Nodes n*
                    // "SonificationMap"
                    if (nl.item(m).getNodeName().equals(SONIFICATIONMAP)) {

                      // reset for next SonificationMap
                      propertyValue = "";
                      property = null;
                      soundPropertyValue = "";
                      functionType = "";
                      a0Value = "";
                      a1Value = "";
                      a2Value = "";
                      a3Value = "";
                      v0Value = 0;
                      v1Value = 0;
                      v2Value = 0;
                      v3Value = 0;
                      v4Value = 0;

                      if (nl.item(m).hasAttributes()) {
                        // extract Attributes of SonificationMap
                        NamedNodeMap childAttributes = nl.item(m)
                            .getAttributes();
                        for (int n = 0; n < childAttributes.getLength(); n++) {
                          // Attributes : "property", "soundProperty",
                          // "casewise", "a0", "a1", "a2, "a3", "v0", "v1",
                          // "v2, "v3"
                          if (childAttributes.item(n).getNodeName().equals(
                              PROPERTY)) {
                            propertyValue = childAttributes.item(n)
                                .getNodeValue();
                            try {
                              property = propertyValue;
                            } catch (Exception e) {
                              property = null;
                            }
                          }
                          if (childAttributes.item(n).getNodeName().equals(
                              SOUND_PROPERTY)) {
                            soundPropertyValue = childAttributes.item(n)
                                .getNodeValue();
                          }
                          if (childAttributes.item(n).getNodeName().equals(
                              MAP_TYPE)) {
                            functionType = childAttributes.item(n)
                                .getNodeValue();
                          }

                          if (childAttributes.item(n).getNodeName().equals(A0)) {
                            try {
                              a0Value = childAttributes.item(n).getNodeValue();
                            } catch (Exception e) {
                            }
                          }
                          if (childAttributes.item(n).getNodeName().equals(A1)) {
                            try {
                              a1Value = childAttributes.item(n).getNodeValue();
                            } catch (Exception e) {
                            }
                          }
                          if (childAttributes.item(n).getNodeName().equals(A2)) {
                            try {
                              a2Value = childAttributes.item(n).getNodeValue();
                            } catch (Exception e) {
                            }
                          }
                          if (childAttributes.item(n).getNodeName().equals(A3)) {
                            try {
                              a3Value = childAttributes.item(n).getNodeValue();
                            } catch (Exception e) {
                            }
                          }
                          if (childAttributes.item(n).getNodeName().equals(V0)) {
                            try {
                              v0Value = Integer.parseInt(childAttributes
                                  .item(n).getNodeValue());
                            } catch (Exception e) {
                            }
                          }
                          if (childAttributes.item(n).getNodeName().equals(V1)) {
                            try {
                              v1Value = Integer.parseInt(childAttributes
                                  .item(n).getNodeValue());
                            } catch (Exception e) {
                            }
                          }
                          if (childAttributes.item(n).getNodeName().equals(V2)) {
                            try {
                              v2Value = Integer.parseInt(childAttributes
                                  .item(n).getNodeValue());
                            } catch (Exception e) {
                            }
                          }
                          if (childAttributes.item(n).getNodeName().equals(V3)) {
                            try {
                              v3Value = Integer.parseInt(childAttributes
                                  .item(n).getNodeValue());
                            } catch (Exception e) {
                            }
                          }
                          if (childAttributes.item(n).getNodeName().equals(V4)) {
                            try {
                              v4Value = Integer.parseInt(childAttributes
                                  .item(n).getNodeValue());
                            } catch (Exception e) {
                            }
                          }

                          // create the sonification map
                          sonificationMap = new SonificationMap(notes,
                              instrument, duration, this.useCloneMapValues,
                              propertyValue, soundPropertyValue, functionType,
                              a0Value, a1Value, a2Value, a3Value, v0Value,
                              v1Value, v2Value, v3Value, v4Value);

                          if (soundPropertyValue.equals(NOTE)) {
                            notes = sonificationMap.mapNotes(property);
                          } else if (soundPropertyValue.equals(DURATION)) {
                            notes = sonificationMap.mapDuration(property);
                          } else if (soundPropertyValue.equals(VOLUME)) {
                            notes = sonificationMap.mapVolume(property);
                          } else if (soundPropertyValue.equals(INSTRUMENT_NO)) {
                            instrument = sonificationMap
                                .mapInstrument(property);
                          }

                          this.sonificationMap.put(eventTypeValue,
                              sonificationMap);
                        }
                      }
                    }
                  }
                }

                // Channelmanagement + set Instrument

                // create the MIDI sound object
                playMIDI = new SoundMIDI(notes);
                ((SoundMIDI) playMIDI).setInstrument(instrument);

                this.soundsMap.put(eventTypeValue, playMIDI);
                this.soundEventActiveStateMap.put(eventTypeValue, true);
                this.soundEventsTypeNameMap.put(eventTypeValue, "MIDI");

                // reset for next MidiSound
                instrumentValue = "";
                noteSequenceValue = "";
                instrumentFound = false;
              }
            }// for k

          }// if hasChildNodes
          if (!midiSoundFound) {
            System.out
                .println("Warrning: MidiSound attributes in EventAppearance needed !");
          }
        }
        // the sound is from a file
        else {

          if (soundFileValue.length() < 4) {
            System.out
                .println("The name of the sound file must have at least 5 chars and ends with '.mid' or '.mp3' ! Found: "
                    + soundFileValue);
            break;
          }

          String fileExtension = soundFileValue.substring(soundFileValue
              .length() - 3);

          // if MP3 file
          if (fileExtension.equalsIgnoreCase("mp3")) {

            // create new background sound
            playSoundFile = new SoundMP3(duration, soundFileValue,
                this.projectDirectory);
          }

          // if MIDI file
          if (fileExtension.equalsIgnoreCase("mid")) {

            // create new background sound
            playSoundFile = new SoundMIDI(this.projectDirectory, soundFileValue);
          }

          this.soundsMap.put(eventTypeValue, playSoundFile);
          this.soundEventActiveStateMap.put(eventTypeValue, true);
          this.soundEventsTypeNameMap.put(eventTypeValue, soundFileValue);

        }
      }
    }
  }

  /**
   * Calling this method will determine to play the background sound if the
   * simulation is not running (is not started, is paused or is ended) and there
   * is a background sound defined.
   * 
   * @param state
   *          - the playing state: true = play, false = stop;
   */
  private synchronized void playBackgroundSound(boolean state) {
    // enable/disable the background sound
    if (this.backgroundSound != null) {

      // stop the sound in case that is already playing
      if (this.backgroundSound.isPlaying()) {
        this.backgroundSound.stop();
        this.backgroundSound.close();
      }

      // wait for the above sound to complete the stop request
      while (this.backgroundSound.isPlaying()) {
        try {
          Thread.sleep(50);
          Thread.yield();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }

      // play the sound if the sound was enabled
      if (this.soundOn && state && !this.runSimulationState) {
        this.backgroundSound.setSoundFileAutoLoop(true);
        this.backgroundSound.play();
      }
    }
  }

  @Override
  public Object getGUIComponent() {
    return this.tabSound;
  }

  @Override
  public void simulationStepEnd(SimulationStepEvent simulationStepEvent) {
    // nothing to do

  }

  @Override
  public void simulationStepStart(long stepNumber) {
    // nothing to do

  }

  @Override
  public void simulationEnvironmentEventOccured(
      EnvironmentEvent environmentEvent) {

    // put the event in queue waiting to be played - only events that have
    // sounds are considered, the rest are just ignored
    eventQ.offer(environmentEvent);

  }

  @Override
  public void simulationInfosEvent(SimulationEvent simulationEvent) {
    // nothing to do
  }

  @Override
  public void simulationDomOnlyInitialization(
      SimulationDescription simulationDescription) {
    Document dom = simulationDescription.getDom();

    if (dom == null) {
      return;
    }

    // create a new module sound thread

    this.soundThread = new Thread(this);

    Iterator<String> iter = this.soundsMap.keySet().iterator();

    // close all sound and free resources
    while (iter.hasNext()) {
      String key = iter.next();
      this.soundsMap.get(key).close();
    }

    // initialize the sounds map
    this.sonificationMap.clear();
    this.soundsMap.clear();
    this.soundEventActiveStateMap.clear();
    this.soundEventsTypeNameMap.clear();

    // creates the sounds map
    createSoundsMap(dom.getElementsByTagName(EVENT_NODES));

    // look for background sound
    Node viewsNode = dom.getElementsByTagName(ANIMATION_UI_ELEMENT_NAME)
        .item(0);

    if (viewsNode != null) {
      NamedNodeMap attributes = viewsNode.getAttributes();

      for (int i = 0; i < attributes.getLength(); i++) {
        if (attributes.item(i).getNodeName().equals(INTRO_SOUND_FILE)) {
          // stop the old background sound if it is playing
          this.playBackgroundSound(false);

          // get the fileName
          String inputSoundFileName = attributes.item(i).getNodeValue();

          if (inputSoundFileName.length() < 4) {
            System.out
                .println("The name of the sound file must have at least 5 chars and ends with '.mid' or '.mp3' ! Found: "
                    + inputSoundFileName);
            break;
          }

          String fileExtension = inputSoundFileName
              .substring(inputSoundFileName.length() - 3);

          // if MP3 file
          if (fileExtension.equalsIgnoreCase("mp3")) {

            // create new background sound
            this.backgroundSound = new SoundMP3(86400000, inputSoundFileName,
                this.projectDirectory);
          }
          // if MIDI file
          else if (fileExtension.equalsIgnoreCase("mid")) {

            // create new background sound
            this.backgroundSound = new SoundMIDI(this.projectDirectory,
                inputSoundFileName);
          } else {
            System.out
                .println("The extension of the sound file must be '.mid' or '.mp3' ! Found: "
                    + inputSoundFileName);
            break;
          }

          // if the sound system is on then play the background sound
          if (this.soundOn) {
            this.playBackgroundSound(true);
          }
        }
      }
    }

    // inform the GUI component about these events in order to display them in
    // the sound tab as a table
    ((TabSound) this.getGUIComponent()).initSoundEventTable(
        this.soundEventsTypeNameMap, this.soundEventActiveStateMap);
  }

  @Override
  public void simulationInitialize(InitialState initialState) {
    // nothing to do
  }

  @Override
  public void simulationPaused(boolean pauseState) {
    // the simulation is paused or continued
    this.runSimulationState = !pauseState;

    // play the background sound if the simulation is paused
    this.playBackgroundSound(pauseState);
  }

  @Override
  public void simulationStarted() {
    // stop and destroy the old module sound thread if there is one
    if (this.running == true) {
      this.stopSound();
    }

    // start the sound module thread
    this.soundThread.start();

    // stop the background sound if it is playing
    this.playBackgroundSound(false);

    // the simulation is now running
    this.runSimulationState = true;
  }

  @Override
  public void simulationEnded() {
    // the module sound thread will be stopped and destroyed now...
    this.soundThread = null;

    // end the module thread
    this.running = false;

    // the simulation is not running
    this.runSimulationState = false;

    // play the background sound
    this.playBackgroundSound(true);
  }

  @Override
  public void simulationProjectDirectoryChanged(File projectDirectory) {
    this.projectDirectory = projectDirectory;

    this.playBackgroundSound(false);
    this.backgroundSound = null;

    // the module sound thread will be stopped and destroyed now...
    this.soundThread = null;
  }

  @Override
  public void objektDestroyEvent(ObjektDestroyEvent objektDestroyEvent) {
    // nothing to do
  }

  @Override
  public void objektInitEvent(ObjektInitEvent objInitEvent) {
    // nothing to do
  }
}
