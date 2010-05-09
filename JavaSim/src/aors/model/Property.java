package aors.model;

/**
 * Components that implement this interface are rollbackable.
 * Rollbackable means, that after activated alle state changes are monitored
 * until these chages are accepted or rejected. If they are rejected the
 * compnente returns to the state before the activation.
 *
 * @param <T> the property type that implements cloneable
 *
 * @author Thomas Grundmann
 * @since May 05, 2010
 * @version $Revision: 125 $
 */
public final class Property<T extends aors.util.Cloneable<T>> implements Rollbackable {
  private T value;
  private T backup;
  private boolean monitoringActivated;

  public Property(T value) {
    this.value = value;
    this.backup = null;
    this.monitoringActivated = false;
  }

  public void setValue(T value) {
    this.value = value;
  }

  public T getValue() {
    return this.value;
  }

  @Override
  public void activateMonitoring() {
    this.monitoringActivated = true;
    this.backup = this.value.clone();
  }

  @Override
  public void acceptChanges() {
    if(this.monitoringActivated) {
      this.monitoringActivated = false;
      this.backup = null;
    }
  }

  @Override
  public void rejectChanges() {
    if(this.monitoringActivated) {
      this.monitoringActivated = false;
      this.value = this.backup.clone();
    }
  }
}
