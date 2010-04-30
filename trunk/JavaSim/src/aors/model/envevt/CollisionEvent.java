package aors.model.envevt;

import aors.model.envsim.Physical;

public class CollisionEvent extends CausedEvent {

  private Physical physicalObject1;
  private Physical physicalObject2;

  public CollisionEvent() {
    super();
  }

  public CollisionEvent(long occurrenceTime) {
    super(occurrenceTime);
  }

  public Physical getPhysicalObject1() {
    return physicalObject1;
  }

  public void setPhysicalObject1(Physical physicalObject1) {
    this.physicalObject1 = physicalObject1;
  }

  public Physical getPhysicalObject2() {
    return physicalObject2;
  }

  public void setPhysicalObject2(Physical physicalObject2) {
    this.physicalObject2 = physicalObject2;
  }

}
