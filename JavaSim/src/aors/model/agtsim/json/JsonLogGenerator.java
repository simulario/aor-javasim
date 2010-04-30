package aors.model.agtsim.json;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONValue;

import aors.model.Message;
import aors.model.agtsim.AgentSubject;
import aors.model.envevt.ActionEvent;
import aors.model.envevt.InMessageEvent;
import aors.model.envevt.OutMessageEvent;
import aors.model.envevt.PerceptionEvent;
import aors.model.envevt.PhysicalObjectPerceptionEvent;
import aors.util.JsonData;

/**
 * 
 * JsonGenerator
 * 
 * @author Christian Noack
 * @since 07.04.2009
 * @version $Revision: 1.0 $
 */
public class JsonLogGenerator {

  private Map<String, Object> agentSubject;
  private List<Object> perceptions;

  public JsonLogGenerator() {
    reset();
  }

  public void reset() {
    this.agentSubject = new LinkedHashMap<String, Object>();
    this.perceptions = new ArrayList<Object>();
  }

  public void notifyAgentSubject(AgentSubject agentSubject) {
    this.agentSubject.put("id", agentSubject.getId());
    this.agentSubject.put("name", agentSubject.getName());
    String t = agentSubject.getType();
    if (t.endsWith("AgentSubject")) {
      // prefix MUST correlate with ext/javagen/custom.xsl prefix.agentSubject
      // declaration
      t = t.substring(0, t.length() - 12);
    }
    this.agentSubject.put("type", t);
  }

  public JsonData getJson() {
    this.agentSubject.put("perceptions", perceptions);
    try {
      return new JsonData(JSONValue.toJSONString(agentSubject));
    } catch (Exception e) {
      return null;
    }

  }

  public void notifyPerception(PerceptionEvent perceptionEvent,
      List<ActionEvent> resultingEventList) {
    Map<String, Object> perception = new LinkedHashMap<String, Object>();
    perception.put("id", perceptionEvent.getId());
    perception.put("name", perceptionEvent.getName());
    perception.put("type", perceptionEvent.getType());
    perception.put("perceiver", perceptionEvent.getPerceiverIdRef());

    if (perceptionEvent instanceof InMessageEvent) {
      InMessageEvent inMessageEvent = (InMessageEvent) perceptionEvent;
      Message message = inMessageEvent.getMessage();
      perception.put("sender", inMessageEvent.getSenderIdRef());
      perception.put("message.type", message.getType());
      perception.put("message.id", message.getId());
      perception.put("message.name", message.getName());

      // set the message-slots
      Class<?> c = message.getClass();
      Map<String, Object> slots = new LinkedHashMap<String, Object>();
      do {
        Field[] fields = c.getDeclaredFields();
        for (Field field : fields) {
          field.setAccessible(true);
          try {
            slots.put(field.getName(), String.valueOf(field.get(message)));
          } catch (IllegalArgumentException e) {
            e.printStackTrace();
          } catch (IllegalAccessException e) {
            e.printStackTrace();
          }
        }
        c = c.getSuperclass();
      } while (c != Message.class);
      perception.put("slots", slots);
    }

    if (perceptionEvent instanceof PhysicalObjectPerceptionEvent) {
      PhysicalObjectPerceptionEvent pope = (PhysicalObjectPerceptionEvent) perceptionEvent;
      perception.put("perceived.type", pope.getPerceivedPhysicalObjectType());
      perception.put("perceived.id", pope.getPerceivedPhysicalObjectIdRef());
      perception.put("distance", pope.getDistance());
      perception.put("perceptionangle", pope.getPerceptionAngle());
    }

    ArrayList<Object> resultingEvents = new ArrayList<Object>();

    for (ActionEvent actionEvent : resultingEventList) {
      Map<String, Object> event = new LinkedHashMap<String, Object>();
      event.put("id", actionEvent.getId());
      event.put("type", actionEvent.getType());
      event.put("name", actionEvent.getName());
      event.put("occurrenceTime", actionEvent.getOccurrenceTime());

      if (actionEvent instanceof OutMessageEvent) {
        Message message = ((OutMessageEvent) actionEvent).getMessage();
        perception.put("message.type", message.getType());
        perception.put("message.id", message.getId());
        perception.put("message.name", message.getName());
      }

      resultingEvents.add(event);
    }
    perception.put("resultingEvents", resultingEvents);

    this.perceptions.add(perception);
  }

}
