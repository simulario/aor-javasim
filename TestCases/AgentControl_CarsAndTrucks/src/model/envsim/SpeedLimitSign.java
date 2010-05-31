package model.envsim;

 
public class SpeedLimitSign extends aors.model.envsim.PhysicalObject {
  private long admMaxVelocity;
  public final static boolean ID_PERCEIVABLE = false;

  public SpeedLimitSign(long id, String name, long admMaxVelocity) {
    super(id, name);
    this.setAdmMaxVelocity(admMaxVelocity);
  }

  public void setAdmMaxVelocity(long admMaxVelocity) {
    if((this.admMaxVelocity != admMaxVelocity)) {
      this.admMaxVelocity = admMaxVelocity;
      this.propertyChangeSupport.firePropertyChange(new java.beans.PropertyChangeEvent(this, "admMaxVelocity", null, this.admMaxVelocity));
    }
  }

  public long getAdmMaxVelocity() {
    return this.admMaxVelocity;
  }
}
