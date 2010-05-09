package aors.model;

/**
 * Components that implement this interface are rollbackable.
 * Rollbackable means, that after activated alle state changes are monitored
 * until these chages are accepted or rejected. If they are rejected the
 * compnente returns to the state before the activation.
 *
 * @author Thomas Grundmann
 * @since May 04, 2010
 * @version $Revision: 125 $
 */
public interface Rollbackable {

	/**
	 * Activates the rollback mechanism.
	 * After the activation each property change is monitored until the changes
	 * are acceptes or rejected.
	 */
	public void activateMonitoring();

	/**
	 * Accepts all property changes that were made during the monitored phase.
   * With accepting the changes the monitoring phase ends.
   * If the monitoring is not activated this method has no effect.
	 */
	public void acceptChanges();

	/**
	 * Rejects all property changes that were made during the monitored phase.
   * With rejecting the changes the monitoring phase ends and the properties'
   * states are set to their state before the monitoring started.
   * If the monitoring is not activated this method has no effect.
	 */
	public void rejectChanges();
}
