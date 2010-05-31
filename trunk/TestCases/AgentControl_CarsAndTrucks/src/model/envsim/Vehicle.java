package model.envsim;

import aors.util.Random;
import controller.SimModel;
import controller.SimParameter;
import controller.Simulator;

public class Vehicle extends aors.model.envsim.PhysicalAgentObject {
  public final static boolean ID_PERCEIVABLE = false;
  public final static boolean AUTO_PERCEPTION = true;

  public Vehicle(long id, String name) {
    super(id, name);
  }
}
