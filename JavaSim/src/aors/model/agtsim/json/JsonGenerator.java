package aors.model.agtsim.json;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONValue;

import aors.model.Message;
import aors.model.envevt.InMessageEvent;
import aors.model.envevt.PerceptionEvent;
import aors.model.envevt.PhysicalObjectPerceptionEvent;
import aors.model.envsim.AgentObject;
import aors.model.envsim.Physical;
import aors.model.envsim.PhysicalAgentObject;

/**
 * This class processes PerceptionEvents and translates them, together with a
 * representation of the AgentObject, into a JSON string which can be sent to a
 * remote agent.
 * 
 * @author Christian Noack
 * 
 */
public class JsonGenerator {

  private List<Object> perceptions;
  private long currentSimulationStep;
  private Map<String, Object> agentObject;

  public JsonGenerator(long currentSimulationStep) {
    this.currentSimulationStep = currentSimulationStep;
    this.perceptions = new ArrayList<Object>();
    this.agentObject = null;
  }

  public String getJsonString(long agentTimeout) {
    Map<String, Object> message = new LinkedHashMap<String, Object>();
    message.put("step", currentSimulationStep);
    message.put("agentTimeout", agentTimeout);
    if (this.agentObject != null)
      message.put("agentObject", agentObject);
    if (perceptions.size() > 0)
      message.put("perceptions", perceptions);
    return JSONValue.toJSONString(message);
  }

  /**
   * Encode AgentObject as Map<String, Object> so that external EJBs do not have
   * access to real AgentObject running within the simulation
   * 
   * @param agentObject
   */
  public void notifyAgentObject(AgentObject agentObject) {
    this.agentObject = null;
    if (agentObject instanceof AgentObject) {
      Map<String, Object> ao = new LinkedHashMap<String, Object>();
      ao.put("id", agentObject.getId());
      ao.put("name", agentObject.getName());
      ao.put("type", agentObject.getType());
      if (agentObject instanceof PhysicalAgentObject) {
        PhysicalAgentObject paObject = (PhysicalAgentObject) agentObject;
        // ao.put("physical", true);
        ao.put(Physical.PROP_WIDTH, paObject.getWidth());
        ao.put(Physical.PROP_HEIGHT, paObject.getHeight());
        ao.put(Physical.PROP_DEPTH, paObject.getDepth());
        ao.put(Physical.PROP_X, paObject.getX());
        ao.put(Physical.PROP_Y, paObject.getY());
        ao.put(Physical.PROP_Z, paObject.getZ());
        ao.put(Physical.PROP_ROTATION_ANGLE_X, paObject.getRotX());
        ao.put(Physical.PROP_ROTATION_ANGLE_Y, paObject.getRotY());
        ao.put(Physical.PROP_ROTATION_ANGLE_Z, paObject.getRotZ());
        // ao.put("v", paObject.getV()); // computed from v-components
        ao.put(Physical.PROP_VX, paObject.getVx());
        ao.put(Physical.PROP_VY, paObject.getVy());
        ao.put(Physical.PROP_VZ, paObject.getVz());
        ao.put(Physical.PROP_OMEGA_X, paObject.getOmegaX());
        ao.put(Physical.PROP_OMEGA_Y, paObject.getOmegaY());
        ao.put(Physical.PROP_OMEGA_Z, paObject.getOmegaZ());
        // ao.put("a", paObject.getA()); // computed from a-components
        ao.put(Physical.PROP_AX, paObject.getAx());
        ao.put(Physical.PROP_AY, paObject.getAy());
        ao.put(Physical.PROP_AZ, paObject.getAz());
        ao.put(Physical.PROP_PERCEPTION_RADIUS, paObject.getPerceptionRadius());
      } else {
        // ao.put("physical", false);
      }
      this.agentObject = ao;
    }
  }

  /**
   * Translates a list of PerceptionEvents into a Map<String,Object> that can be
   * easily translated into JSON
   * 
   * @param perceptionEvents
   */
  public void notifyPerceptions(List<PerceptionEvent> perceptionEvents) {
    this.perceptions.clear();
    for (PerceptionEvent perceptionEvent : perceptionEvents) {

      Map<String, Object> perception = new LinkedHashMap<String, Object>();
      perception.put("id", perceptionEvent.getId());
      perception.put("name", perceptionEvent.getName());
      perception.put("type", perceptionEvent.getType());
      perception.put("receiverId", perceptionEvent.getPerceiverIdRef());

      if (perceptionEvent instanceof InMessageEvent) {
        InMessageEvent inMessageEvent = (InMessageEvent) perceptionEvent;
        Message message = inMessageEvent.getMessage();
        perception.put("senderId", inMessageEvent.getSenderIdRef());
        perception.put("messageType", message.getType());

        Map<String, Object> content = new LinkedHashMap<String, Object>();

        content.put("messageId", message.getId());
        content.put("messageName", message.getName());

        // set the message-slots
        Class<?> c = message.getClass();
        ArrayList<Object> slots = new ArrayList<Object>();

        Map<String, Object> slot = new LinkedHashMap<String, Object>();
        do {
          Field[] fields = c.getDeclaredFields();
          for (Field field : fields) {
            field.setAccessible(true);
            try {
              slot.put("name", field.getName());
              slot.put("value", String.valueOf(field.get(message)));
            } catch (IllegalArgumentException e) {
              e.printStackTrace();
            } catch (IllegalAccessException e) {
              e.printStackTrace();
            }
            slots.add(slot);
          }
          c = c.getSuperclass();
        } while (c != Message.class);
        content.put("slots", slots);
        perception.put("content", content);
      }

      if (perceptionEvent instanceof PhysicalObjectPerceptionEvent) {
        PhysicalObjectPerceptionEvent pope = (PhysicalObjectPerceptionEvent) perceptionEvent;
        perception.put("occurrenceLocX", pope.getOccurrenceLocX());
        perception.put("occurrenceLocY", pope.getOccurrenceLocY());
        perception.put("occurrenceLocZ", pope.getOccurrenceLocZ());
        perception.put("occurrenceTime", pope.getOccurrenceTime());
        perception.put("duration", pope.getDuration());
        perception.put("distance", pope.getDistance());
        perception.put("perceivedPhysicalObjectType", pope
            .getPerceivedPhysicalObjectType());
        perception.put("perceivedPhysicalObjectIdRef", pope
            .getPerceivedPhysicalObjectIdRef());
        perception.put("perceptionAngle", pope.getPerceptionAngle());
        perception.put("startTime", pope.getStartTime());
        Physical p = pope.getPerceivedPhysicalObject();

        Map<String, Object> physO = new LinkedHashMap<String, Object>();
        // physO.put("a", p.getA()); // computed from a-components
        physO.put(Physical.PROP_AX, p.getAx());
        physO.put(Physical.PROP_AY, p.getAy());
        physO.put(Physical.PROP_AZ, p.getAz());
        physO.put(Physical.PROP_DEPTH, p.getDepth());
        physO.put(Physical.PROP_HEIGHT, p.getHeight());
        physO.put(Physical.PROP_WIDTH, p.getWidth());
        physO.put("id", p.getId());
        physO.put("name", p.getName());
        physO.put("type", p.getType());
        // physO.put("v", p.getV()); // computed from v-components
        physO.put(Physical.PROP_VX, p.getVx());
        physO.put(Physical.PROP_VY, p.getVy());
        physO.put(Physical.PROP_VZ, p.getVz());
        physO.put(Physical.PROP_OMEGA_X, p.getOmegaX());
        physO.put(Physical.PROP_OMEGA_Y, p.getOmegaY());
        physO.put(Physical.PROP_OMEGA_Z, p.getOmegaZ());
        physO.put(Physical.PROP_X, p.getX());
        physO.put(Physical.PROP_Y, p.getY());
        physO.put(Physical.PROP_Z, p.getZ());
        physO.put(Physical.PROP_ROTATION_ANGLE_X, p.getRotX());
        physO.put(Physical.PROP_ROTATION_ANGLE_Y, p.getRotY());
        physO.put(Physical.PROP_ROTATION_ANGLE_Z, p.getRotZ());
        physO.put(Physical.PROP_PERCEPTION_RADIUS, 0);

        perception.put("physicalObj", physO);
      }

      this.perceptions.add(perception);
    }
  }

}
