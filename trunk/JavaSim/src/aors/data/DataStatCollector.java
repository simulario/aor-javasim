package aors.data;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONValue;

import aors.data.evt.sim.ObjektDestroyEvent;
import aors.data.evt.sim.ObjektDestroyEventListener;
import aors.data.evt.sim.ObjektInitEvent;
import aors.data.evt.sim.ObjektInitEventListener;
import aors.model.agtsim.AgentSubject;
import aors.model.envsim.AgentObject;
import aors.model.envsim.Objekt;
import aors.model.envsim.Physical;
import aors.model.envsim.PhysicalAgentObject;
import aors.model.envsim.PhysicalObject;
import aors.space.AbstractCell;

/**
 * This class has a similar task as the DataCollector. It receives changeEvents
 * and compiles a data structure of it. It generates JSON data from the stored
 * data presenting the current (latest) state of all objects in the simulation.
 * 
 * @author Christian Nnoack
 * 
 */
public class DataStatCollector implements PropertyChangeListener,
    ObjektInitEventListener, ObjektDestroyEventListener {

  /**
   * incoming stores 4-tupels of (step, aid, prop, value) with step being the
   * step in which a change occurs, aid being the ID of the agent, prop a
   * property of the agent and value the new value of the property.
   */
  private Map<Long, Map<Long, Map<String, Object>>> incoming;

  /**
   * currentFullData stores the latest state of all agents in a 3-tupel of (aid,
   * prop, value) with aid being the ID of the agent, prop a property of the
   * agent and value the new value of the property.
   */
  private Map<Long, Map<String, Object>> currentFullData;

  /**
   * Once generated, this string contains the JSON representation of the last
   * global state (full information about all objects). It is destroyed as soon
   * as new events come in that will change the state.
   */
  private String currentFullString;

  /**
   * The last simulation step received in an event.
   */
  private long currentSimulationStep;

  // true, if currently a step is running. Then, the retrieved information might
  // be incomplete or inconsistent
  private boolean stepRunning;

  /**
   * Max step in simulation
   */
  private long stepNumber;

  public DataStatCollector() {
    this.incoming = new HashMap<Long, Map<Long, Map<String, Object>>>();
    Map<Long, Map<String, Object>> initMap = new HashMap<Long, Map<String, Object>>();
    this.incoming.put(0L, initMap);
    this.currentFullData = new HashMap<Long, Map<String, Object>>();
    this.currentSimulationStep = 0;
    this.stepNumber = 0;
    this.currentFullString = null;
  }

  public Object getProperty(Long id, String property) {
    try {
      return this.currentFullData.get(id).get(property);
    } catch (Exception e) {
      return null;
    }
  }

  public void setCurrentSimulatorStep(long simulationStep) {
    this.currentSimulationStep = simulationStep;
    Map<Long, Map<String, Object>> initMap = new HashMap<Long, Map<String, Object>>();
    this.incoming.put(this.currentSimulationStep, initMap);
  }

  public void setStepNumber(long stepNumber) {
    this.stepNumber = stepNumber;
  }

  public String getStatistics(long simulationStep) {
    System.out.println("getStat step=" + simulationStep);
    if (simulationStep == -1) {
      // return a full stat of all objects and agents.
      if (this.currentFullString == null) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("step", this.currentSimulationStep);
        map.put("max", this.stepNumber);
        map.put("type", true); // means full data
        map.put("running", stepRunning);
        map.put("data", currentFullData);
        this.currentFullString = JSONValue.toJSONString(map);
      }
      return this.currentFullString;

    } else if (simulationStep == this.currentSimulationStep) {
      Map<String, Object> map = new HashMap<String, Object>();
      map.put("step", this.currentSimulationStep);
      map.put("type", false); // means full data
      map.put("running", stepRunning);
      map.put("data", new HashMap<Long, Map<String, Object>>());
      return JSONValue.toJSONString(map);

    } else if (simulationStep == this.currentSimulationStep - 1) {
      // return only the changed data of last step

      Map<Long, Map<String, Object>> cur = incoming.get(simulationStep);
      if (cur != null) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("step", this.currentSimulationStep);
        map.put("type", false); // means incremental data
        map.put("running", stepRunning);
        map.put("data", cur);
        return JSONValue.toJSONString(map);
      }
      return "";

    } else {
      // return a compiled change list of the last n steps
      // n = currentSimulationStep - simulationStep

      Map<Long, Map<String, Object>> resMap = new HashMap<Long, Map<String, Object>>();

      for (long i = simulationStep; i <= currentSimulationStep; i++) {
        Map<Long, Map<String, Object>> cur = incoming.get(i);
        if (cur != null) {
          for (Long key : cur.keySet()) {
            Map<String, Object> objMap = cur.get(key);

            Map<String, Object> resObjMap = resMap.get(key);
            if (resObjMap == null) {
              resObjMap = new HashMap<String, Object>();
              resMap.put(key, resObjMap);
            }
            resObjMap.putAll(objMap);
          }
        }
      }

      Map<String, Object> map = new HashMap<String, Object>();
      map.put("step", this.currentSimulationStep);
      map.put("type", false); // means incremental data
      map.put("running", stepRunning);
      map.put("data", resMap);
      return JSONValue.toJSONString(map);
    }
  }

  @Override
  public void objektInitEvent(ObjektInitEvent objInitEvent) {
    this.currentFullString = null;

    if (objInitEvent.getSource() instanceof PhysicalAgentObject) {
      PhysicalAgentObject pao = (PhysicalAgentObject) objInitEvent.getSource();

      Map<String, Object> map = new HashMap<String, Object>();
      map.put("id", pao.getId());
      map.put("name", pao.getName());
      map.put("otype", "pa");
      map.put("type", pao.getType());
      map.put(Physical.PROP_X, pao.getX());
      map.put(Physical.PROP_Y, pao.getX());
      map.put(Physical.PROP_Z, pao.getX());
      map.put(Physical.PROP_ROTATION_ANGLE_X, pao.getRotX());
      map.put(Physical.PROP_ROTATION_ANGLE_Y, pao.getRotY());
      map.put(Physical.PROP_ROTATION_ANGLE_Z, pao.getRotZ());
      map.put(Physical.PROP_VX, pao.getVx());
      map.put(Physical.PROP_VY, pao.getVy());
      map.put(Physical.PROP_VZ, pao.getVz());
      map.put(Physical.PROP_OMEGA_X, pao.getOmegaX());
      map.put(Physical.PROP_OMEGA_Y, pao.getOmegaY());
      map.put(Physical.PROP_OMEGA_Z, pao.getOmegaZ());
      map.put(Physical.PROP_AX, pao.getAx());
      map.put(Physical.PROP_AY, pao.getAy());
      map.put(Physical.PROP_AZ, pao.getAz());
      map.put(Physical.PROP_WIDTH, pao.getWidth());
      map.put(Physical.PROP_HEIGHT, pao.getHeight());
      map.put(Physical.PROP_DEPTH, pao.getDepth());
      map.put(Physical.PROP_M, pao.getM());
      map.put(Physical.PROP_PERCEPTION_RADIUS, pao.getPerceptionRadius());

      incoming.get(currentSimulationStep).put(pao.getId(), map);

      Map<String, Object> map2 = new HashMap<String, Object>();
      map2.putAll(map);

      currentFullData.put(pao.getId(), map2);
    } else if (objInitEvent.getSource() instanceof PhysicalObject) {
      PhysicalObject po = (PhysicalObject) objInitEvent.getSource();

      Map<String, Object> map = new HashMap<String, Object>();
      map.put("id", po.getId());
      map.put("name", po.getName());
      map.put("otype", "po");
      map.put("type", po.getType());
      map.put(Physical.PROP_X, po.getX());
      map.put(Physical.PROP_Y, po.getX());
      map.put(Physical.PROP_Z, po.getX());
      map.put(Physical.PROP_ROTATION_ANGLE_X, po.getRotX());
      map.put(Physical.PROP_ROTATION_ANGLE_Y, po.getRotY());
      map.put(Physical.PROP_ROTATION_ANGLE_Z, po.getRotZ());
      map.put(Physical.PROP_VX, po.getVx());
      map.put(Physical.PROP_VY, po.getVy());
      map.put(Physical.PROP_VZ, po.getVz());
      map.put(Physical.PROP_OMEGA_X, po.getOmegaX());
      map.put(Physical.PROP_OMEGA_Y, po.getOmegaY());
      map.put(Physical.PROP_OMEGA_Z, po.getOmegaZ());
      map.put(Physical.PROP_AX, po.getAx());
      map.put(Physical.PROP_AY, po.getAy());
      map.put(Physical.PROP_AZ, po.getAz());
      map.put(Physical.PROP_WIDTH, po.getWidth());
      map.put(Physical.PROP_HEIGHT, po.getHeight());
      map.put(Physical.PROP_DEPTH, po.getDepth());
      map.put(Physical.PROP_M, po.getM());

      incoming.get(currentSimulationStep).put(po.getId(), map);

      Map<String, Object> map2 = new HashMap<String, Object>();
      map2.putAll(map);

      currentFullData.put(po.getId(), map2);

    } else if (objInitEvent.getSource() instanceof AgentObject) {
      AgentObject ao = (AgentObject) objInitEvent.getSource();

      Map<String, Object> map = new HashMap<String, Object>();
      map.put("id", ao.getId());
      map.put("name", ao.getName());
      map.put("otype", "a");
      map.put("type", ao.getType());

      incoming.get(0L).put(ao.getId(), map);

      Map<String, Object> map2 = new HashMap<String, Object>();
      map2.putAll(map);

      currentFullData.put(ao.getId(), map2);

    } else if (objInitEvent.getSource() instanceof Objekt) {
      Objekt o = (Objekt) objInitEvent.getSource();

      Map<String, Object> map = new HashMap<String, Object>();
      map.put("id", o.getId());
      map.put("name", o.getName());
      map.put("otype", "o");
      map.put("type", o.getType());

      incoming.get(currentSimulationStep).put(o.getId(), map);

      Map<String, Object> map2 = new HashMap<String, Object>();
      map2.putAll(map);

      currentFullData.put(o.getId(), map2);

    } else if (objInitEvent.getSource() instanceof AgentSubject) {
      // Reflect over AgentSubject to retrieve its private selfbelief-properties
      AgentSubject as = (AgentSubject) objInitEvent.getSource();

      Map<Long, Map<String, Object>> simStepMap = this.incoming
          .get(this.currentSimulationStep);

      Map<String, Object> fMap = this.currentFullData.get(as.getId());
      if (fMap == null) {
        fMap = new HashMap<String, Object>();
        this.currentFullData.put(as.getId(), fMap);
      }

      Map<String, Object> map = simStepMap.get(as.getId());
      if (map == null) {
        map = new HashMap<String, Object>();
        simStepMap.put(as.getId(), map);
      }

      Class<? extends AgentSubject> c = as.getClass();
      Field[] fields = c.getDeclaredFields();
      for (Field f : fields) {
        String fName = f.getName();
        if (!fName.equals("MEMORY_SIZE")) {
          try {

            String gName = "get" + fName.substring(0, 1).toUpperCase()
                + fName.substring(1);
            Method method = c.getMethod(gName, new Class[0]);
            Object fValue = method.invoke(as, new Object[0]);
            map.put(fName, fValue);
            fMap.put(fName, fValue);

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

    } else if (objInitEvent.getSource() instanceof AbstractCell[][]) {
      // AbstractCell[][] ac = (AbstractCell[][])objInitEvent.getSource();
      // TODO store this, too

    } else {
      System.err.println("Unknown Class in PhysObjInitEvent");
    }

  }

  @Override
  public void objektDestroyEvent(ObjektDestroyEvent objektDestroyEvent) {
    this.currentFullString = null;

    Map<Long, Map<String, Object>> simStepMap = this.incoming
        .get(this.currentSimulationStep);

    if (objektDestroyEvent.getSource() instanceof Objekt) {
      Objekt o = (Objekt) objektDestroyEvent.getSource();

      this.currentFullData.remove(o.getId());

      Map<String, Object> map = new HashMap<String, Object>();
      map.put("action", "deleted");

      simStepMap.put(o.getId(), map);
    }
  }

  @Override
  public synchronized void propertyChange(PropertyChangeEvent evt) {
    this.currentFullString = null;

    // map of current step
    Map<Long, Map<String, Object>> simStepMap = this.incoming
        .get(this.currentSimulationStep);

    if (evt.getSource() instanceof Objekt) {
      Objekt o = (Objekt) evt.getSource();

      Map<String, Object> fMap = this.currentFullData.get(o.getId());

      Map<String, Object> map = simStepMap.get(o.getId());
      if (map == null) {
        map = new HashMap<String, Object>();
        simStepMap.put(o.getId(), map);
      }

      if (evt.getNewValue() != null) {
        if (evt.getNewValue() instanceof Objekt) {
          long refId = ((Objekt) evt.getNewValue()).getId();
          map.put(evt.getPropertyName(), refId);
          fMap.put(evt.getPropertyName(), evt.getNewValue());
        } else {
          map.put(evt.getPropertyName(), evt.getNewValue());
          fMap.put(evt.getPropertyName(), evt.getNewValue());
        }
      }
    } else if (evt.getSource() instanceof AgentSubject) {
      AgentSubject as = (AgentSubject) evt.getSource();

      Map<String, Object> fMap = this.currentFullData.get(as.getId());

      Map<String, Object> map = simStepMap.get(as.getId());
      if (map == null) {
        map = new HashMap<String, Object>();
        simStepMap.put(as.getId(), map);
      }

      if (evt.getNewValue() != null) {
        if (evt.getNewValue() instanceof Objekt) {
          long refId = ((Objekt) evt.getNewValue()).getId();
          map.put(evt.getPropertyName(), refId);
          fMap.put(evt.getPropertyName(), evt.getNewValue());
        } else {
          map.put(evt.getPropertyName(), evt.getNewValue());
          fMap.put(evt.getPropertyName(), evt.getNewValue());
        }
      }
    }
  }

  public void notifyStepRunning(boolean stepRunning) {
    this.stepRunning = stepRunning;
  }

}
