package aors.model.agtsim;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import aors.model.AtomicEvent;
import aors.util.collection.BoundedBuffer;

/**
 * Implementation of an AgentMemory. It is used to save previous actual
 * perception events of an agent. Implementation via a ringbuffer.
 * 
 * @author Volkmar Kantor
 */
public class AgentMemory {
  
  public static final String MEMORY_SIZE = "MEMORY_SIZE";

  /**
   * Internal Class, used to have an datatype for the buffer.
   */
  private class MemoryItem {

    public AtomicEvent event;

    public String toString() {
      return event.toString();
    }
  }

  /**
   * The agent's memory. (BoundedBuffer)
   */
  private BoundedBuffer<MemoryItem> memory;

  /**
   * Results of last search in memory.
   */
  private ArrayList<AtomicEvent> lastResults = new ArrayList<AtomicEvent>();

  /**
   * List of perceptions from current simulation-step. These perceptions are not
   * in agent's memory yet.
   */
  private ArrayList<AtomicEvent> gate = new ArrayList<AtomicEvent>();

  /**
   * A List of PerceptionEvent-Names (via Class.getSimplename()) which don't
   * will be stored into memory.
   */
  private List<String> percToIgnore;

  /**
   * Constructor
   * 
   * @param capacity
   *          of the Memory
   * @param perceptionsToIgnore
   *          names of PerceptionEvents to ignore (via Class.getSimpleName())
   */
  public AgentMemory(int capacity, List<String> perceptionsToIgnore) {
    memory = new BoundedBuffer<MemoryItem>(capacity);
    percToIgnore = perceptionsToIgnore;
  }

  /**
   * This method checks, if there is a perception-event of type eventType in the
   * agent's memory. Store possible result in the lastResults Array. Parameter
   * atStep (first parameter) must have the following format: <br />
   * [N,M] where N is a positive integer number and M is a positive integer
   * number or * as wildcard.<br />
   * N - Startindex (1 is smallest index!)<br />
   * M - Endindex
   * 
   * @param atStep
   *          Start- and Endindex to look for perception-event in memory.
   * @param eventType
   *          Type of perception-event to look for.
   * @return true, if perception-event if in memory, false otherwise.
   */
  public boolean existsEventMemory(String atStep, String eventType) {
    // existsEventMemory may not change the state of this Object
    int startStep = -1;
    int endStep = -1;
    int pos = atStep.indexOf(',');

    try {
      startStep = Integer.parseInt(atStep.substring(1, pos));

      if (atStep.charAt(pos + 1) != '*') {
        endStep = Integer.parseInt(atStep.substring(pos + 1,
            atStep.length() - 1));
      }
    } catch (NumberFormatException e) {
      System.out
          .println("wrong number format in first argument of existsEventMemory");
      return false;
    }

    return this.existsEventMemory(startStep, endStep, eventType);
  }

  /**
   * Used by the public existsEventMemory-Method. Checks if there is a
   * perception-event of type eventType in the agent's memory. Store possible
   * result in the lastResults Array.
   * 
   * @param startStep
   * @param endStep
   * @param eventType
   * @return
   */
  private boolean existsEventMemory(int startStep, int endStep, String eventType) {
    int start, end;
    lastResults.clear();

    // if there are less events in memory than user wants to search
    start = ((memory.size() - 1 - (startStep - 1)) < 0) ? 0
        : (memory.size() - 1 - (startStep - 1));

    if (endStep == -1) {
      end = 0;
    } else {
      // minimum for end is zero
      end = (start - endStep + 1) < 0 ? 0 : (start - endStep + 1);
    }

    for (int i = start; i >= end; i--) {
      try {
        if (memory.get(i).event.getClass().getSimpleName().equalsIgnoreCase(
            eventType)) {
          lastResults.add(memory.get(i).event);
        }
      } catch (NullPointerException e) {
        lastResults.clear();
        return false;
      }
    }

    if (lastResults.size() > 0) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * TODO write an expressive comment
   * 
   * @param atStep
   * @param eventType
   * @return the values of the last search
   */
  public Object[] getEventMemory(String atStep, String eventType) {
    existsEventMemory(atStep, eventType);
    return this.lastSearchResults();
  }

  /**
   * Get the first index in search-results that matches the given condition in
   * the string pattern. The string must have the following format: "
   * <code>&lt;NameOfAttribute&gt; &lt;operator&gt; &lt;placeholder&gt; [&lt;logic&gt;]...</code>
   * "
   * <ul>
   * <li>&lt;NameOfAttribute&gt; is the name of an attribute of the perception
   * event.
   * <li>&lt;operator&gt; can be &lt;, &gt; or == &lt;placeholder&gt; can be %d
   * (for double) or %l (for long). This depends on the type of
   * &lt;NameOfAttribute&gt; and type of value in values.
   * <li>&lt;logic&gt; can be && or || and it is optional. It allows you to
   * connect different conditions.
   * </ul>
   * The following arguments are the values for this check.<br />
   * Examples:
   * 
   * <pre>
   * getFirstIndexWith("distance > %d && physicalObject == %l", 7.5,23) 
   * getFirstIndexWith("velocity < %d", 23.3)
   * </pre>
   * 
   * @param pattern
   *          Conditions
   * @param values
   *          Values
   * @return The first index in lastResults, that matches the given conditions.
   *         -1 if there was an error or no match.
   */
  public int getFirstIndexWith(String pattern, Object... values) {
    // TODO partition into parts or optimize code-length
    Scanner sc = new Scanner(pattern);

    ArrayList<String> attribute = new ArrayList<String>();
    ArrayList<String> operator = new ArrayList<String>();
    ArrayList<String> placeholder = new ArrayList<String>();
    ArrayList<String> logic = new ArrayList<String>();

    boolean hasNext = true;

    try {
      while (hasNext) {
        attribute.add(sc.next());
        operator.add(sc.next());
        placeholder.add(sc.next());
        if (sc.hasNext()) {
          logic.add(sc.next());
          hasNext = true;
        } else {
          hasNext = false;
        }
      }
    } catch (NoSuchElementException e) {
      System.out.println("NoSuchElementException");
      return -2;
    }

    int index;
    boolean bools[] = new boolean[logic.size() + 1];

    for (int i = 0; i < lastResults.size(); i++) {

      try {
        AtomicEvent result = lastResults.get(i);
        index = 0;

        do {
          String attr = attribute.get(index);
          attr = attr.substring(0, 1).toUpperCase() + attr.substring(1);
          String methodName = "get" + attr;

          String op = operator.get(index);

          String ph = placeholder.get(index);

          Method method = result.getClass().getDeclaredMethod(methodName,
              new Class[0]);

          /*
           * if(ph.equals("%d")) { System.out.println(methodName + ": " +
           * (double)(Double)method.invoke(result));
           * System.out.println("value: " + (double)(Double)values[index]); }
           * else if(ph.equals("%l")) { System.out.println(methodName + ": " +
           * (long)(Long)method.invoke(result)); System.out.println("value: " +
           * (long)(Long)values[index]); } else if(ph.equals("%b")) {
           * 
           * }
           */

          if (op.equals(">")) {
            if (ph.equals("%d")) {
              bools[index] = (double) (Double) method.invoke(result) > (double) (Double) values[index];
            } else if (ph.equals("%l")) {
              bools[index] = (long) (Long) method.invoke(result) > (long) (Long) values[index];
            }
          } else if (op.equals("<")) {
            if (ph.equals("%d")) {
              bools[index] = (double) (Double) method.invoke(result) < (double) (Double) values[index];
            } else if (ph.equals("%l")) {
              bools[index] = (long) (Long) method.invoke(result) < (long) (Long) values[index];
            }
          } else if (op.equals("==")) {
            if (ph.equals("%d")) {
              bools[index] = (double) (Double) method.invoke(result) == (double) (Double) values[index];
            } else if (ph.equals("%l")) {
              bools[index] = (long) (Long) method.invoke(result) == (long) (Long) values[index];
            } else if (ph.equals("%b")) {
              bools[index] = (boolean) (Boolean) method.invoke(result) == (boolean) (Boolean) values[index];
            }
          } else if (op.equals(">=")) {
            if (ph.equals("%d")) {
              bools[index] = (double) (Double) method.invoke(result) >= (double) (Double) values[index];
            } else if (ph.equals("%l")) {
              bools[index] = (long) (Long) method.invoke(result) >= (long) (Long) values[index];
            }
          } else if (op.equals("<=")) {
            if (ph.equals("%d")) {
              bools[index] = (double) (Double) method.invoke(result) <= (double) (Double) values[index];
            } else if (ph.equals("%l")) {
              bools[index] = (long) (Long) method.invoke(result) <= (long) (Long) values[index];
            }
          } else if (op.equals("!=")) {
            if (ph.equals("%d")) {
              bools[index] = (double) (Double) method.invoke(result) != (double) (Double) values[index];
            } else if (ph.equals("%l")) {
              bools[index] = (long) (Long) method.invoke(result) != (long) (Long) values[index];
            } else if (ph.equals("%b")) {
              bools[index] = (boolean) (Boolean) method.invoke(result) != (boolean) (Boolean) values[index];
            }
          }

          index++;

        } while (index - 1 != logic.size());

        boolean fresult = bools[0];
        for (int j = 0; j < logic.size(); j++) {
          if (logic.get(j).equals("&&")) {
            // System.out.println("logic: &&");
            fresult &= bools[j + 1];
          } else if (logic.get(j).equals("||")) {
            // System.out.println("logic: ||");
            fresult |= bools[j + 1];
          }
        }

        if (fresult) {
          // System.out.println("index found: " + i);
          return i;
        }

      } catch (ArrayIndexOutOfBoundsException e) {
        System.out.println("ArrayIndex");
        return -2;
      } catch (NoSuchMethodException e) {
        System.out.println("NoSuchMethod");
        continue;
      } catch (InvocationTargetException e) {
        System.out.println("InvocationTarget");
        return -2;
      } catch (IllegalAccessException e) {
        System.out.println("IllegalAccess");
        return -2;
      } catch (ClassCastException e) {
        System.out
            .println("warning: wrong datatype in pattern for getFirstIndexWith()");
        return -2;
      }

    }
    // System.out.println("index found: -1");
    return -1;
  }

  /**
   * Returns the values of the last search result.
   * 
   * @return an Object-Array of events with the last results.
   * @see #getFirstIndexWith(String, Object...)
   */
  public Object[] lastSearchResults() {
    return lastResults.toArray();
  }

  /**
   * Store a list of Events into the AgentMemory.
   * <p>
   * New events (of the current Simulationstep) will wait on an gate for access.
   * Events of the previous Simulationstep will copy into the memory.
   * 
   * @param events
   *          the events of the current Simulationstep
   * @param currentStep
   *          the current Simulationstep
   */
  public void storeMemories(List<AtomicEvent> events, long currentStep) {
    // add perceptions from previous step from gate to memory
    if (gate.size() > 0) {
      for (int i = 0; i < gate.size(); i++) {
        MemoryItem item = new MemoryItem();
        item.event = gate.get(i);

        memory.add(item);
      }
      gate.clear();
    }

    // add current perceptions to gate
    if (events.size() > 0) {
      addToGate(events);
    }
  }

  /**
   * Add new (actual) perception events to agent's memory. All perceptions will
   * be stored in the "gate" first and added to the memory in the next
   * simulation-step (next call of method simulate()).
   * 
   * @param events
   *          List of perceptions, that should be stored in the agent's memory
   */
  private void addToGate(List<AtomicEvent> events) {
    if (!gate.isEmpty()) {
      System.out.println("AgentMemory: Warning! Gate is not empty!");
    }

    for (int i = 0; i < events.size(); i++) {
      AtomicEvent pe = events.get(i);
      if (!percToIgnore.contains(pe.getClass().getSimpleName())) {
        gate.add(pe);
      }
    }
  }
}