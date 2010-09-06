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
 * File: DefaultAgentSimulator.java
 * 
 * Package: info.aors.model.agtsim
 *
 **************************************************************************************************************/
package aors.model.agtsim.sim;

import aors.controller.AbstractSimulator;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import aors.logger.model.AgentSimulatorStep;
import aors.model.agtsim.ActualPerceptionRule;
import aors.model.agtsim.AgentMemory;
import aors.model.agtsim.AgentSubject;
import aors.model.agtsim.proxy.agentControl.AgentSimulatorFacade;
import aors.model.agtsim.proxy.agentControl.AgentSubjectProxy;
import aors.model.envevt.ActionEvent;
import aors.model.envevt.InMessageEvent;
import aors.model.envevt.OutMessageEvent;
import aors.model.envevt.PerceptionEvent;
import aors.model.envsim.AgentObject;
import aors.physim.PhysicsSimulator;
import aors.util.JsonData;

/**
 * DefaultAgentSimulator, default implementation of the
 * AgentSimulator-Interface. Used in the standalone-version of the AOR-JSim.
 * 
 * @author Emilian Pascalau, Adrian Giurca, Jens Werner, Volkmar Kantor,
 *         Christian Noack
 * @since May 25, 2008
 * @version $Revision$
 */
public class DefaultAgentSimulator implements AgentSimulator,
    AgentSimulatorFacade {

  /**
   * 
   */
  private static final long serialVersionUID = 523789025L;

  /**
   * if true, in every step received/send events are displayed in the console
   */
  private boolean debug = false;

  /**
   * Comments: this is used to inform the AbstractSimulator about the new
   * ActionEvents which were generated by this AgentSubject. this property must
   * be created ONLY by the constructor
   */
  private AgentSimulatorListener actionEventsListener;

  /**
   * Used to transfer control to an external AgentSubject, e.g. JavaScript in
   * WebSim.
   */
  private AgentSubjectProxy agentSubjectProxy;

  /**
   * The agentSubject that is being simulated by the simulator instance
   */
  private AgentSubject agentSubject;

  private ClassLoader simClassLoader;

  /**
   * current perception events that will be processed by the agentSubject
   */
  private List<PerceptionEvent> perceptionEvents = new ArrayList<PerceptionEvent>();

  /**
   * the current simulation step
   */
  private long currentSimulationStep;

  private long agentTimeout;

  /**
   * Used in external, multithreading mode when this AgentSimulator is connected
   * to an AgentSimulatorProxy to transfer events from/to an external
   * AgentSubject, e.g. a JavaScript agent.
   * 
   * true if ActionEvents have been processed -> step completed, false otherwise
   */
  private volatile boolean stepCompleted = false;

  /**
   * flag is set to false if the AgentSubject has been destroyed by an
   * AgentSubjectDestructionEvent.
   */
  private boolean running;

  /**
   * A listener which receives property change events coming from the
   * AgentSubject
   */
  private PropertyChangeListener propertyChangeListener;

  private AgentObject agentObject;

  /**
   * Reference to the abstract simulator running this simulator.
   */
  private AbstractSimulator abstractSimulator;

  /**
   * Time when the step current step ends.
   */
  private long stepEndTime;

  /**
   * Creates a new DefaultAgentSimulator instance.
   * 
   * @param agentSubject
   * @param listener
   *          a listener to send ActionEvents to
   * @param pcl
   * @param abstractSimulator
   */
  public DefaultAgentSimulator(AgentSubject agentSubject,
      AgentSimulatorListener listener, PropertyChangeListener pcl,
      AbstractSimulator abstractSimulator) {
    this.abstractSimulator = abstractSimulator;
    this.agentSubject = agentSubject;
    this.agentSubject.addPropertyChangeListener(this);
    this.agentSubject.setAgentSimulator(this);
    this.actionEventsListener = listener;
    this.propertyChangeListener = pcl;
    this.running = true;
    this.initializeAgentMemory();
    this.stepEndTime = 0;
  } // DefaultAgentSimulator

  @Override
  public void setSimClassLoader(ClassLoader simClassLoader) {
    this.simClassLoader = simClassLoader;
  }

  @Override
  public AgentSubject getAgentSubject() {
    return this.agentSubject;
  }

  /**
   * Notifies the AgentSimulator of changes of belief properties of the
   * AgentSubject. Is called by the AgentSubject
   */
  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    if (evt != null && "isControlled".equals(evt.getPropertyName())) {
      return;
    }
    if (this.propertyChangeListener != null) {
      this.propertyChangeListener.propertyChange(evt);
    }
  }

  /**
   * Initialize the AgentMemory.
   * <p>
   * Search for Perceptions to ignore. Set the size of the Memory (by an
   * declaredField in an Subclass) Called on creation of the Object.
   * 
   * @see #DefaultAgentSimulator
   */
  private void initializeAgentMemory() {
    /**
     * If an agent was defined with at least one actualPerceptionRule, only the
     * resulting actualPerception of that rule are saved into the agent's
     * memory. The triggering perception of that rule gets ignored.
     */
    List<String> perceptionsToIgnore = new ArrayList<String>();

    // determine memorySize and actualperceptionrules
    List<ActualPerceptionRule> rules = agentSubject.getActualPerceptionRules();
    Iterator<ActualPerceptionRule> i = rules.iterator();
    while (i.hasNext()) {
      // determine triggering event of that actualperceptionrule
      ActualPerceptionRule aPRule = i.next();
      perceptionsToIgnore.add(aPRule.getTriggeringEventType());
    }

    try {
      // finally determine memory-size
      Field field = agentSubject.getClass().getDeclaredField(
          PhysicsSimulator.MEMORY_SIZE);
      int capacity = Integer.valueOf(field.get(null).toString());
      if (capacity > 0 || capacity == -1) {
        /** Add the agentMemory **/
        agentSubject.setAgentMemory(new AgentMemory(capacity,
            perceptionsToIgnore));
      }
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (NoSuchFieldException e) {
      /**
       * no MemorySize defined in agentSubject do nothing TODO check if that
       * will work in all cases, or if its useful to define ALWAYS a AgentMemory
       * i.e. with MemorySize 1 or 0
       */
    }
  }

  @Override
  public void setCorrespondingAgentObject(AgentObject agentObject) {
    this.agentObject = agentObject;
    this.agentSubject.setAgentObject(this.agentObject);
  }

  @Override
  public void setAgentSubjectProxy(AgentSubjectProxy proxy) {
    agentSubjectProxy = proxy;
    if (agentSubjectProxy != null) {
      agentSubjectProxy.setSimClassLoader(this.simClassLoader);
      agentSubjectProxy.setAgentObject(this.agentObject);
    }
  }

  @Override
  public boolean isAgentSubjectProxySet() {
    return (this.agentSubjectProxy != null);
  }

  /*
   * @Override public AgentSimulatorProxy getAgentSimulatorProxy() { return
   * this.agentSimulatorProxy; }
   */

  @Override
  public long getAgentId() {
    return this.agentSubject.getId();
  }

  @Override
  public String getAgentName() {
    return this.agentSubject.getName();
  }

  @Override
  public String getAgentType() {
    return this.agentSubject.getType();
  }

  @Override
  public String getUserName() {
    if (isAgentSubjectProxySet()) {
      return this.agentSubjectProxy.getUserName();
    } else if(this.agentSubject.isControlled()) {
			return "local user";
		}
    return "";
  }

  @Override
  public void setBaseURI(String baseURI) {
    this.agentSubject.setBaseURI(baseURI);
  }

  /**
   * Set the State for one defined SimulationStep. The AgentSimulator / Agent
   * get a list of PerceptionEvents to react. Called by
   * AbstractSimulator.runStep()
   * 
   * @param newStep
   *          the new current simulation step
   * @param events
   *          list of perception events
   */
  public void setNewEvents(long newStep, List<PerceptionEvent> events) {
    this.currentSimulationStep = newStep;
    this.perceptionEvents = events;

    if (debug && !events.isEmpty()) {
      System.out.println("setAgentSubjectPerceptionEvents of "
          + this.agentSubject.getName() + ": " + events.size()
          + " perceptionEvents");
      for (PerceptionEvent perception : events) {
        if (perception instanceof InMessageEvent) {
          InMessageEvent ime = (InMessageEvent) perception;
          System.out.println(this.currentSimulationStep + ":: "
              + this.agentSubject.getName() + "(" + this.agentSubject.getId()
              + "): IM from " + ime.getSenderIdRef() + " of type "
              + ime.getMessage().getType() + "(time: "
              + ime.getOccurrenceTime() + ")");
          System.out.println(this.currentSimulationStep + ":: "
              + this.agentSubject.getName() + "(" + this.agentSubject.getId()
              + "): " + ime.toString());
        } else {
          System.out.println(this.currentSimulationStep + ":: "
              + this.agentSubject.getName() + "(" + this.agentSubject.getId()
              + "): " + perception.toString());
        }
      }
    } // if
  } // setNewEvents

  @Override
  public void setAgentTimeout(long agentTimeout) {
    this.agentTimeout = agentTimeout;
  }

  @Override
  public long getAgentTimeout() {
    return this.agentTimeout;
  }

  @Override
  public Map<String, Map<String, String>> getSubjectProperties() {
    Map<String, Map<String, String>> res = new HashMap<String, Map<String, String>>();
    Class<? extends AgentSubject> c = this.agentSubject.getClass();
    // c is a real subclass of AgentSubject
    if (!AgentSubject.class.getName().equals(c.getName())) {
      res.putAll(getFields(c));
    }
    return res;
  } // getSubjectProperties

  @SuppressWarnings("unchecked")
  private Map<String, Map<String, String>> getFields(
      Class<? extends AgentSubject> c) {
    Map<String, Map<String, String>> res = new HashMap<String, Map<String, String>>();

    if (debug) {
      System.out.println("Class name: " + c.getSimpleName());
    }

    Field[] fields = c.getDeclaredFields();
    if (debug) {
      System.out.println("Fields: ");
    }
    for (Field f : fields) {
      String fName = f.getName();
      if (!fName.equals("MEMORY_SIZE")) {
        if (debug) {
          System.out.println(fName);
        }
        try {
          Map<String, String> r = new HashMap<String, String>();

          String mName = "set" + fName.substring(0, 1).toUpperCase()
              + fName.substring(1);
          String gName = "get" + fName.substring(0, 1).toUpperCase()
              + fName.substring(1);
          Method method = c.getMethod(gName, new Class[0]);
          Object fValue = method.invoke(this.agentSubject, new Object[0]);
          r.put("method", mName);
          r.put("value", fValue.toString());
          res.put(fName, r);

        } catch (IllegalArgumentException e) {
          System.out.println(e);
        } catch (IllegalAccessException e) {
          System.out.println(e);
        } catch (SecurityException e) {
          System.out.println(e);
        } catch (NoSuchMethodException e) {
          System.out.println(e);
        } catch (InvocationTargetException e) {
          System.out.println(e);
        }
      }
    }

    Class s = c.getSuperclass();

    // because c is a real subclass of AgentSubject it is ensured that s is
    // a real subclass too or AgentSubject; if it is AgentSubject, we are
    // finished
    if (!AgentSubject.class.getName().equals(s.getName())) {
      res.putAll(getFields(s));
    }

    return res;
  } // getFields

  /**
   * Send a list of actions and the agent log to the actionEventsListener.
   * 
   * @param agentSubjectEvent
   */
  private void fireEvent(List<ActionEvent> actions, JsonData agentStepLog) {
    if (debug && !actions.isEmpty()) { // System.out.println("-"); //
      System.out.println("fireEvent of " + this.agentSubject.getName() + ": "
          + actions.size() + " action");
      for (ActionEvent action : actions) {
        if (action instanceof OutMessageEvent) {
          OutMessageEvent ome = (OutMessageEvent) action;
          System.out.println(this.currentSimulationStep + ":: "
              + this.agentSubject.getName() + "(" + this.agentSubject.getId()
              + "): OM to " + ome.getReceiverIdRef() + " of type "
              + ome.getMessage().getType() + "(time: "
              + ome.getOccurrenceTime() + ")");
          System.out.println(this.currentSimulationStep + ":: "
              + this.agentSubject.getName() + "(" + this.agentSubject.getId()
              + "): " + ome.toString());
        } else {
          System.out.println(this.currentSimulationStep + ":: "
              + this.agentSubject.getName() + "(" + this.agentSubject.getId()
              + "): " + action.toString());
        }
      }
    }
    actionEventsListener.receiveActionEvents(actions, agentStepLog);
  } // fireEvent

  private void fireEventWithJaxb(List<ActionEvent> actions,
      AgentSimulatorStep agentSimulatorStep) {
    this.actionEventsListener.receiveActionEvents(actions, agentSimulatorStep);
  }

  /**
   * Runs the AgentSimulator.
   */
  @Override
  public void run() {
    if (this.running) {
      stepCompleted = false;

      if (isAgentSubjectProxySet()) {
        // proxy set, do not run the local AgentSubject

        synchronized (this.perceptionEvents) {
          this.agentSubjectProxy.notifyPerceptions(this.perceptionEvents,
              this.currentSimulationStep, this.agentTimeout);
        }

        // wait for ActionEvents response from AgentSimulatorProxy
        synchronized (this) {
          long startTime = System.currentTimeMillis();
          long timeOut = startTime + agentTimeout * 1000;
          try {
            while (System.currentTimeMillis() < timeOut && !stepCompleted) {
              wait(agentTimeout * 1000);
            }
          } catch (InterruptedException e) {
          }
        }

      } else {
        // no proxy set, run local AgentSubject
        this.agentSubject.setNewEvents(this.perceptionEvents);
        this.agentSubject.setCurrentSimulationStep(this.currentSimulationStep);
        this.agentSubject.run();
				if(this.agentSubject.isControlled()) {
					this.agentSubject.updateView();

					// wait for ActionEvents response from AgentController
					synchronized (this) {
						try {
							if((this.stepEndTime - System.currentTimeMillis()) > 0
								&& !stepCompleted) {
								wait(this.stepEndTime - System.currentTimeMillis());
							}
						} catch (InterruptedException e) {
						} catch (IllegalArgumentException e) {
						}
					}
					this.agentSubject.performUserActions();
				}

        // TODO: review this:
        if (this.agentSubject.isLogGenerationEnabled()
            || this.agentSubject.isJaxbLogGenerationEnabled()) {
          if (this.agentSubject.isLogGenerationEnabled()) {
            this.fireEvent(this.agentSubject.getActionEvents(),
                this.agentSubject.getStepLog());
          }

          if (this.agentSubject.isJaxbLogGenerationEnabled()) {
            this.fireEventWithJaxb(this.agentSubject.getActionEvents(),
                this.agentSubject.getJaxbStepLog());
          }
        } else {
          this.fireEvent(this.agentSubject.getActionEvents(), null);
        }
      }
    }
  } // run

  @Override
  public void notifyActionEvents(long responseSimulationStep,
      List<ActionEvent> actionEvents) {

    this.fireEvent(actionEvents, null);
    stepCompleted = true;
    if (responseSimulationStep == this.currentSimulationStep) {
      synchronized (this) {
        // wake up the current thread so that it can complete
        notify();
      }
    }
  }

  @Override
  public void notifySimulationEnd() {
    if (isAgentSubjectProxySet()) {
      synchronized (this) {
        stepCompleted = true;
        notify();
      }
      this.agentSubjectProxy.notifySimulationEnd();
    }
  }

  @Override
  public void notifyRemoval() {
    // TODO create a last log for the removed AgentSubject?
    this.running = false;
    if (isAgentSubjectProxySet()) {
      this.agentSubjectProxy.notifyRemoval();
    } else {
      this.agentSubject.notifyRemoval();
    }
  }

  @Override
  public boolean agentIsControlled() {
    return this.agentSubject.isControlled();
  }

  @Override
  public boolean agentIsControllable() {
    return this.agentSubject.isControllable();
  }

	@Override
	public void setAgentIsControlled(boolean isControlled) {
		this.abstractSimulator.setAgentIsControlled(this, isControlled);
	}

  public void setStepEndTime(long stepEndTime) {
    this.stepEndTime = stepEndTime;
  }
}