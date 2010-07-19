package aors.module.initialStateUI.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;

public class instancePanelsLayout extends FlowLayout {

	private int preferredWidth = 0, preferredHeight = 0;
	private int minWidth = 0, minHeight = 0;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public instancePanelsLayout() {
		super();
	}

	public Dimension preferredLayoutSize(Container parent) {
		Dimension dim = new Dimension(0, 0);
		setSizes(parent);

		// Always add the container's insets!
		Insets insets = parent.getInsets();
		dim.width = preferredWidth + insets.left + insets.right;
		dim.height = preferredHeight + insets.top + insets.bottom;

		return dim;
	}

	public Dimension minimumLayoutSize(Container parent) {
		Dimension dim = new Dimension(0, 0);
		Insets insets = parent.getInsets();
		dim.width = minWidth + insets.left + insets.right;
		dim.height = minHeight + insets.top + insets.bottom;

		return null;

	}

	private void setSizes(Container parent) {
		int nComps = parent.getComponentCount();
		Dimension d = null;

		// Reset preferred/minimum width and height.
		preferredWidth = 0;
		preferredHeight = 0;
		minHeight = 0;
		minWidth = 0;
		for (int i = 0; i < nComps; i++) {
			Component c = parent.getComponent(i);
			if (c.isVisible()) {
				d = c.getPreferredSize();
				preferredWidth += d.width;
				preferredHeight += d.height;
				d = c.getMinimumSize();

				minWidth += d.width;
				minHeight += d.height;
			}

		}

	}

	/**
	 * @param preferredWidth
	 *            the preferredWidth to set
	 */
	public void setPreferredWidth(int preferredWidth) {
		this.preferredWidth = preferredWidth;
	}

	/**
	 * @return the preferredWidth
	 */
	public int getPreferredWidth() {
		return preferredWidth;
	}

	/**
	 * @param preferredHeight
	 *            the preferredHeight to set
	 */
	public void setPreferredHeight(int preferredHeight) {
		this.preferredHeight = preferredHeight;
	}

	/**
	 * @return the preferredHeight
	 */
	public int getPreferredHeight() {
		return preferredHeight;
	}

	/**
	 * @param minWidth
	 *            the minWidth to set
	 */
	public void setMinWidth(int minWidth) {
		this.minWidth = minWidth;
	}

	/**
	 * @return the minWidth
	 */
	public int getMinWidth() {
		return minWidth;
	}

	/**
	 * @param minHeight
	 *            the minHeight to set
	 */
	public void setMinHeight(int minHeight) {
		this.minHeight = minHeight;
	}

	/**
	 * @return the minHeight
	 */
	public int getMinHeight() {
		return minHeight;
	}

}
