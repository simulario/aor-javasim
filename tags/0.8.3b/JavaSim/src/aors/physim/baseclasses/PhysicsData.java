package aors.physim.baseclasses;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import aors.GeneralSimulationParameters;
import aors.GeneralSpaceModel;
import aors.model.envsim.Physical;
import aors.model.envsim.PhysicalAgentObject;
import aors.model.envsim.PhysicalObject;
import aors.physim.extsim.CollisionSimulator;
import aors.physim.util.Vector;

public class PhysicsData {

  protected GeneralSimulationParameters params = null;

  protected GeneralSpaceModel spacemodel = null;

  protected List<PhysicalAgentObject> physAgents = null;

  protected List<PhysicalObject> physObjects = null;

  protected Map<Long, Vector> newVelocity = new Hashtable<Long, Vector>();

  protected Map<Long, Vector> newAcceleration = new Hashtable<Long, Vector>();

  protected Map<Long, Boolean> positionChanged = null;

  protected List<Long> physicalDestroyed = new ArrayList<Long>();

  protected Queue<Physical> physicalCreated = new LinkedList<Physical>();

  protected List<CollisionSimulator.ActualCollision> collisions;

  public boolean isInited() {
    return physAgents != null && physObjects != null && params != null
        && spacemodel != null;
  }

  public GeneralSimulationParameters getParams() {
    return params;
  }

  public void setParams(GeneralSimulationParameters params) {
    this.params = params;
  }

  public GeneralSpaceModel getSpaceModel() {
    return spacemodel;
  }

  public void setSpaceModel(GeneralSpaceModel spacemodel) {
    this.spacemodel = spacemodel;
  }

  public List<PhysicalAgentObject> getPhysAgents() {
    return physAgents;
  }

  public void setPhysAgents(List<PhysicalAgentObject> physAgents) {
    this.physAgents = physAgents;
  }

  public List<PhysicalObject> getPhysObjects() {
    return physObjects;
  }

  public void setPhysObjects(List<PhysicalObject> physObjects) {
    this.physObjects = physObjects;
  }

  public Map<Long, Vector> getNewVelocity() {
    return newVelocity;
  }

  public void setNewVelocity(Map<Long, Vector> newVelocity) {
    this.newVelocity = newVelocity;
  }

  public Map<Long, Vector> getNewAcceleration() {
    return newAcceleration;
  }

  public void setNewAcceleration(Map<Long, Vector> newAcceleration) {
    this.newAcceleration = newAcceleration;
  }

  public Map<Long, Boolean> getPositionChanged() {
    return positionChanged;
  }

  public void setPositionChanged(Map<Long, Boolean> positionChanged) {
    this.positionChanged = positionChanged;
  }

  public List<Long> getPhysicalDestroyed() {
    return physicalDestroyed;
  }

  public void setPhysicalDestroyed(List<Long> physicalDestroyed) {
    this.physicalDestroyed = physicalDestroyed;
  }

  public Queue<Physical> getPhysicalCreated() {
    return physicalCreated;
  }

  public void setPhysicalCreated(Queue<Physical> physicalCreated) {
    this.physicalCreated = physicalCreated;
  }

  public List<CollisionSimulator.ActualCollision> getCollisions() {
    return collisions;
  }

  public void setCollisions(List<CollisionSimulator.ActualCollision> collisions) {
    this.collisions = collisions;
  }

}
