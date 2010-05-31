package model.envsim;

import aors.util.Random;
import controller.SimModel;
import controller.SimParameter;
import controller.Simulator;

public class Truck extends model.envsim.Vehicle {
  public final static boolean ID_PERCEIVABLE = false;
  public final static boolean AUTO_PERCEPTION = true;

  public Truck(long id, String name) {
    super(id, name);
  }
}
