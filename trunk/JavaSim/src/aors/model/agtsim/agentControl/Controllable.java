package aors.model.agtsim.agentControl;

public interface Controllable {
	public boolean isControllable();
	public boolean isControlled();
	public void updateView();
	public void performUserActions();
}
