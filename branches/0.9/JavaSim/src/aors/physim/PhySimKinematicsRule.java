package aors.physim;

import java.util.ArrayList;

import aors.model.AtomicEvent;
import aors.model.envevt.EnvironmentEvent;
import aors.model.envsim.EnvironmentRule;
import aors.model.envsim.EnvironmentSimulator;

/**
 * This Rule is used to notify the Logger about changes of attributes of
 * physical objects. See class PhySim.
 * 
 * @author Stefan Boecker
 * 
 */
public final class PhySimKinematicsRule extends EnvironmentRule {

  public PhySimKinematicsRule(String name, EnvironmentSimulator envSim) {
    super(name, envSim);
  }

  @Override
  public boolean thenDestroyObjekt() {
    return true;
  }

  @Override
  public void setTriggeringEvent(AtomicEvent atomicEvent) {
  }

  public boolean createObjekt() {
    return true;
  }

  public boolean createAgent() {
    return true;
  }

  @Override
  public void thenStateEffects() {
  }

  @Override
  public boolean condition() {
    return true;
  }

  @Override
  public ArrayList<EnvironmentEvent> thenResultingEvents() {
    return null;
  }

  @Override
  public ArrayList<EnvironmentEvent> execute() {
    return null;
  }

  @Override
  public String getTriggeringEventType() {
    return null;
  }

  @Override
  public String getMessageType() {
    return "";
  }

  public void updateStatistic() {
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Overrides method {@code elseResultingEvents} from super class
   * 
   * 
   * 
   * @return
   */
  @Override
  public ArrayList<EnvironmentEvent> elseResultingEvents() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Overrides method {@code elseStateEffects} from super class
   * 
   * 
   * 
   */
  @Override
  public void elseStateEffects() {
    // TODO Auto-generated method stub

  }

  /**
   * Usage:
   * 
   * 
   * Comments: Overrides method {@code elseDestroyObjekt} from super class
   * 
   * 
   * 
   * @return
   */
  @Override
  public boolean elseDestroyObjekt() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean doDestroyObjekt() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public ArrayList<EnvironmentEvent> doResultingEvents() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void doStateEffects() {
    // TODO Auto-generated method stub

  }
}
