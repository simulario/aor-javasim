package aors.module.initialStateUI.gui;

import javax.swing.JPanel;

public class InstancePanel extends JPanel {

	public static final Long KEY_FOR_GLOBALS = new Long(-1);

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long instancePanelKey;

	/**
	 * @param instancePanelKey
	 *            the instancePanelKey to set
	 */
	public void setInstancePanelKey(Long instancePanelKey) {
		this.instancePanelKey = instancePanelKey;
	}

	/**
	 * @return the instancePanelKey
	 */
	public Long getInstancePanelKey() {
		return instancePanelKey;
	}

}
