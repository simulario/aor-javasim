package aors.physim.baseclasses;

import java.util.ArrayList;
import java.util.List;

import aors.model.AtomicEvent;
import aors.model.envsim.Physical;

/**
 * ExternalSimulator is the baseclass for every external simulator of PhySim. It
 * allows access to different attributes that are used by all external
 * simulators.
 * 
 * @author Stefan Boecker
 * 
 */
public abstract class ExternalSimulator extends AbstractSimulator {

  protected PhysicsData pdata;

  public ExternalSimulator(PhysicsData pdata) {
    this.pdata = pdata;
  }

  /*
   * (non-Javadoc)
   * 
   * @see aors.physim.baseclasses.AbstractSimulator#simulate(java.util.List,
   * long)
   */
  public abstract void simulate(List<AtomicEvent> currentEvents,
      long currentStep);

  public void addPhysicalCreated(Physical phy) {
    this.pdata.getPhysicalCreated().add(phy);
  }

  public void addPhysicalDestroyed(long id) {
    this.pdata.getPhysicalDestroyed().add(id);
  }

  public void clearPhysicalDestroyed() {
    this.pdata.getPhysicalDestroyed().clear();
  }

  /**
   * @return an ArrayList with all Physicals (PhysObj and PhysAgents)
   */
  protected List<Physical> getPhysicalObjectList() {
    List<Physical> po = new ArrayList<Physical>();
    po.addAll(this.pdata.getPhysAgents());
    po.addAll(this.pdata.getPhysObjects());
    return po;
  }

}
