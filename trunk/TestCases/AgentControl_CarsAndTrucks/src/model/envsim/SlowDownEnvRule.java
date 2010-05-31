package model.envsim;

import aors.util.Random;
import controller.SimModel;
import controller.SimParameter;
import controller.Simulator;

public class SlowDownEnvRule extends aors.model.envsim.EnvironmentRule {
  private model.envevt.SlowDown e;
  private model.envsim.Vehicle vehicle;

  public SlowDownEnvRule(String __name, aors.model.envsim.EnvironmentAccessFacet __envSim) {
    super(__name, __envSim);
  }

  public java.util.ArrayList<aors.model.envevt.EnvironmentEvent> execute() {
    java.util.ArrayList<aors.model.envevt.EnvironmentEvent> __result = new java.util.ArrayList<aors.model.envevt.EnvironmentEvent>();
    this.vehicle = ((model.envsim.Vehicle)e.getActor());
    if((this.vehicle != null)) {
      this.doStateEffects();
    }
    return __result;
  }

  protected void doStateEffects() {
    this.vehicle.setVx(vehicle.getVx() - e.getVelocity());
  }

  protected boolean condition() {
    return true;
  }

  public void setTriggeringEvent(aors.model.AtomicEvent __atomicEvent) {
    this.e = ((model.envevt.SlowDown)__atomicEvent);
    this.setTriggeredTime(this.e.getOccurrenceTime());
  }

  public String getTriggeringEventType() {
    return "SlowDown";
  }

  public String getMessageType() {
    return "";
  }
}
