package model.envevt;

import aors.util.Random;
import controller.SimModel;
import controller.SimParameter;
import controller.Simulator;

public class SlowDown extends aors.model.envevt.ActionEvent {
  private long velocity;

  public SlowDown(long occurrenceTime, long actorIdRef, aors.model.envsim.PhysicalAgentObject physicalAgentObject) {
    super(occurrenceTime, actorIdRef, physicalAgentObject);
  }

  public void setVelocity(long velocity) {
    this.velocity = velocity;
  }

  public long getVelocity() {
    return this.velocity;
  }
}
