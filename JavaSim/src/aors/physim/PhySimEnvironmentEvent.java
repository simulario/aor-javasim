package aors.physim;

import aors.model.envevt.ExogenousEvent;

/**
 * This EnvironmentEvent is used to notify the Logger about changes of
 * attributes of physical objects. See class PhySim.
 * 
 * @author Stefan Boecker
 * 
 */
public final class PhySimEnvironmentEvent extends ExogenousEvent {

  public PhySimEnvironmentEvent(String name, long occurrenceTime) {
    super(name, occurrenceTime);
  }

  public boolean stopCondition() {
    return false;
  }

  public long periodicity() {
    return 1;
  }
}
