package aors.model.agtsim.json;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import aors.model.Message;
import aors.model.envevt.ActionEvent;
import aors.model.envevt.OutMessageEvent;
import aors.model.envsim.AgentObject;
import aors.model.envsim.PhysicalAgentObject;
import aors.util.JsonData;

/**
 * This class processes the Json response received from an external AgentSubject
 * and produces ActionEvents from it.
 * 
 * @author Christian Noack
 * 
 */
public class JsonProcessor {

  private boolean debug = false;
  private ClassLoader simClassLoader;

  public void setClassLoader(ClassLoader simClassLoader) {
    this.simClassLoader = simClassLoader;
  }

  @SuppressWarnings("unchecked")
  public List<ActionEvent> process(AgentObject agentObject, JsonData json) {
    if (debug)
      System.out.println("JP:process 0");

    @SuppressWarnings("unused")
    long simulationStep = (Long) json.get("simulationStep");

    List<ActionEvent> actions = new ArrayList<ActionEvent>();

    List<Object> perceptions = (ArrayList<Object>) json.get("events");
    Iterator<Object> iterator = perceptions.iterator();

    // cycle over Perceptions
    while (iterator.hasNext()) {
      Map<String, Object> event = (HashMap<String, Object>) iterator.next();

      if (debug)
        System.out.println("JP:process 1");
      @SuppressWarnings("unused")
      Long eId = (Long) event.get("id");
      @SuppressWarnings("unused")
      String eName = (String) event.get("name");
      String eventType = (String) event.get("type");
      Long occurrenceTime = (Long) event.get("occurrenceTime");
      Long actorIdRef = (Long) event.get("actorIdRef");
      Long receiverIdRef = (Long) event.get("receiverIdRef");

      if ("OutMessageEvent".equals(eventType)) {
        if (debug)
          System.out.println("JP:process 2 OutMessageEvent");

        Map<String, Object> jMessage = (HashMap<String, Object>) event
            .get("message");
        String mType = (String) jMessage.get("type");
        Long mId = (Long) jMessage.get("id");
        String mName = (String) jMessage.get("name");

        try {
          if (debug)
            System.out.println("JP:process 3");
          Class messageClass = Class.forName("scenario.model.envsim." + mType,
              true, this.simClassLoader);
          if (debug)
            System.out.println("JP:process 4");
          Method[] methods = messageClass.getDeclaredMethods();

          Message om = (Message) messageClass.newInstance();
          if (mId != null)
            om.setId(mId);
          if (mName != null)
            om.setName(mName);
          for (String property : jMessage.keySet()) {
            if (!property.equals("type") && !property.equals("id")
                && !property.equals("name")) {
              if (debug)
                System.out.println("JP:process 5 " + property);
              // new message property found. process it
              Object propertyValue = jMessage.get(property);
              for (Method me : methods) {
                if (debug)
                  System.out.println("JP:process 6 " + me.getName());
                if (me.getName().equalsIgnoreCase("set" + property)) {
                  // method to set property found.
                  Class<?>[] paraTypes = me.getParameterTypes();
                  if (paraTypes.length == 1) {
                    Class<?> t = paraTypes[0];
                    if (debug)
                      System.out.println("JP: process 7 " + t.getSimpleName());
                    if (t.getSimpleName().equals("long")) {
                      if (propertyValue.getClass().equals(Long.class)) {
                        me.invoke(om, (Long) propertyValue);
                        if (debug)
                          System.out.println("xxx1 " + (Long) propertyValue);
                      } else {
                        // String
                        me.invoke(om, new Long((String) propertyValue));
                        if (debug)
                          System.out.println("xxx2 " + (String) propertyValue);
                      }
                    } else if (t.getSimpleName().equals("String")) {
                      me.invoke(om, (String) propertyValue);
                      if (debug)
                        System.out.println("xxx3 " + (String) propertyValue);
                    } else {
                      if (debug)
                        System.out.println("xxx4 " + propertyValue);
                    }
                  } // if (paraTypes.length==1)
                  if (debug)
                    System.out.println("JP:process 8 done");
                }
              }
            }
          }
          OutMessageEvent ome = new OutMessageEvent(occurrenceTime,
              receiverIdRef, actorIdRef, null, om);
          actions.add(ome);
        } catch (ClassNotFoundException e) {
          if (debug)
            System.out.println("JSonProcessor: " + e + e.getMessage());
        } catch (IllegalArgumentException e) {
          if (debug)
            System.out.println("JSonProcessor: " + e + e.getMessage());
        } catch (IllegalAccessException e) {
          if (debug)
            System.out.println("JSonProcessor: " + e + e.getMessage());
        } catch (InvocationTargetException e) {
          if (debug)
            System.out.println("JSonProcessor: " + e + e.getMessage());
        } catch (InstantiationException e) {
          if (debug)
            System.out.println("JSonProcessor: " + e + e.getMessage());
        }

      } else {
        // No OutMessageEvent, must be some kind of ActionEvent

        // SwitchToRightLane switchToRightLane_1 = new
        // SwitchToRightLane(occurrenceTime + 1, agentId, agentRef);

        try {
          if (debug)
            System.out.println("JP:process 2 ActionEvent");

          Class actionClass = Class.forName("scenario.model.envevt."
              + eventType, true, this.simClassLoader);
          if (agentObject instanceof PhysicalAgentObject) {
            Class[] typeList = { long.class, long.class,
                PhysicalAgentObject.class };
            Constructor constructor = actionClass
                .getDeclaredConstructor(typeList);
            PhysicalAgentObject pao = (PhysicalAgentObject) agentObject;
            Object[] argList = { occurrenceTime + 1, actorIdRef, pao };
            ActionEvent aEvent = (ActionEvent) constructor.newInstance(argList);
            actions.add(aEvent);
          } else {
            Class[] typeList = { long.class, long.class, AgentObject.class };
            Constructor constructor = actionClass
                .getDeclaredConstructor(typeList);
            Object[] argList = { occurrenceTime + 1, actorIdRef, agentObject };
            ActionEvent aEvent = (ActionEvent) constructor.newInstance(argList);
            actions.add(aEvent);
          }

        } catch (ClassNotFoundException e) {
          if (debug)
            System.out.println("JSonProcessor: " + e + e.getMessage());
        } catch (IllegalArgumentException e) {
          if (debug)
            System.out.println("JSonProcessor: " + e + e.getMessage());
        } catch (IllegalAccessException e) {
          if (debug)
            System.out.println("JSonProcessor: " + e + e.getMessage());
          e.printStackTrace();
        } catch (InvocationTargetException e) {
          if (debug)
            System.out.println("JSonProcessor: " + e + e.getMessage());
        } catch (InstantiationException e) {
          if (debug)
            System.out.println("JSonProcessor: " + e + e.getMessage());
        } catch (SecurityException e) {
          if (debug)
            System.out.println("JSonProcessor: " + e + e.getMessage());
        } catch (NoSuchMethodException e) {
          if (debug)
            System.out.println("JSonProcessor: " + e + e.getMessage());
        }

      } // if (eventType.equals("OutMessageEvent")) {

    } // eventsiterate

    if (debug)
      System.out.println("JP:process end");
    return actions;
  }

}
