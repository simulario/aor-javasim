package aors.module.initialStateUI.gui;

import java.awt.Component;
import java.awt.Font;
import java.util.Arrays;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/*
 * SliderDemo.java requires all the files in the images/doggy
 * directory.
 */
public class InputFieldSlider extends JPanel implements ChangeListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final long factor_limit = 1000;

	private JLabel propertyValueLabel;
	private JSlider propertyValueSlider;
	private InputFieldSliderType inputFieldSliderType;
	private InitialStatePropertiesPanel initialStatePropertiesPanel; // Null for
	// object
	// Creation
	// Panel
	private Object value;
	private Long multipyingFactor; // Only for double case

	public InputFieldSlider(Long minValue, Long maxValue, Long sliderStepSize,
			Long initialValue,
			InitialStatePropertiesPanel initialStatePropertiesPanel) {

		this.initialStatePropertiesPanel = initialStatePropertiesPanel;
		// Create the slider.
		propertyValueSlider = new JSlider(JSlider.HORIZONTAL, minValue
				.intValue(), maxValue.intValue(), initialValue.intValue());
		setInputFieldSliderType(InputFieldSliderType.Long);

		Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
		labelTable.put(new Integer(minValue.intValue()), new JLabel(minValue
				.toString()));

		labelTable.put(new Integer(maxValue.intValue()), new JLabel(maxValue
				.toString()));
		propertyValueSlider.setLabelTable(labelTable);

		propertyValueSlider.setPaintLabels(true);

		propertyValueSlider.addChangeListener(this);

		// Turn on labels at major tick marks.

		propertyValueSlider.setMinorTickSpacing(sliderStepSize.intValue());

		// Create the label that displays the animation.
		propertyValueLabel = new JLabel(
				new Long(propertyValueSlider.getValue()).toString());

		populateInputFieldSlider();

	}

	private void populateInputFieldSlider() {
		propertyValueSlider.setSnapToTicks(true);
		propertyValueSlider.setPaintTicks(true);
		propertyValueSlider.setPaintLabels(true);
		propertyValueSlider.setBorder(BorderFactory.createEmptyBorder(0, 0, 10,
				0));
		Font font = new Font("Serif", Font.ITALIC, 15);
		propertyValueSlider.setFont(font);

		propertyValueLabel.setHorizontalAlignment(JLabel.CENTER);
		propertyValueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		propertyValueLabel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLoweredBevelBorder(), BorderFactory
						.createEmptyBorder(10, 10, 10, 10)));

		// Put everything together.

		add(propertyValueSlider);
		add(propertyValueLabel);
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

	}

	public InputFieldSlider(Double minValue, Double maxValue,
			Double sliderStepSize, Double initialValue,
			InitialStatePropertiesPanel initialStatePropertiesPanel) {

		this.multipyingFactor = initializeMultiplyingFactor(minValue, maxValue,
				sliderStepSize, initialValue);
		int minSliderValue = (int) ((minValue) * multipyingFactor);
		int maxSliderValue = (int) ((maxValue) * multipyingFactor);
		int stepSize = (int) ((sliderStepSize) * multipyingFactor);
		int sliderInitialValue = (int) ((initialValue) * multipyingFactor);

		this.initialStatePropertiesPanel = initialStatePropertiesPanel;
		// Create the slider.
		propertyValueSlider = new JSlider(JSlider.HORIZONTAL, minSliderValue,
				maxSliderValue, sliderInitialValue);
		// propertyValueSlider.set

		setInputFieldSliderType(InputFieldSliderType.Double);

		propertyValueSlider.addChangeListener(this);

		Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
		labelTable.put(minSliderValue, new JLabel(minValue.toString()));

		labelTable.put(maxSliderValue, new JLabel(maxValue.toString()));
		propertyValueSlider.setLabelTable(labelTable);

		propertyValueSlider.setPaintLabels(true);

		// Turn on labels at major tick marks.

		propertyValueSlider.setMinorTickSpacing(stepSize);
		// Create the label that displays the animation.

		this.value = new Double((new Double(sliderInitialValue))
				/ this.multipyingFactor);
		propertyValueLabel = new JLabel(this.value.toString());

		populateInputFieldSlider();

	}

	private Long initializeMultiplyingFactor(Double minValue, Double maxValue,
			Double sliderStepSize, Double initialValue) {
		Long[] factors = new Long[4];
		factors[0] = findFactor(minValue);
		factors[1] = findFactor(maxValue);
		factors[2] = findFactor(sliderStepSize);
		factors[3] = findFactor(initialValue);

		Arrays.sort(factors);

		return factors[factors.length - 1];
	}

	private Long findFactor(Double value) {

		Long factor = new Long(1);
		Double comparedValue = new Double(value.intValue());
		while (!value.equals(comparedValue)) {

			if (factor.equals(InputFieldSlider.factor_limit)) {
				return factor;
			} else {
				factor = factor * 10;
				value = value * 10;
				comparedValue = new Double(value.intValue());
			}
		}
		return factor;
	}

	/** Listen to the slider. */
	public void stateChanged(ChangeEvent e) {
		JSlider source = (JSlider) e.getSource();
		if (!source.getValueIsAdjusting()) {
			int fps = source.getValue();

			switch (inputFieldSliderType) {
			case Long: {
				this.value = new Long(fps);

				break;
			}
			case Double: {
				this.value = new Double((new Double(fps))
						/ this.multipyingFactor);

				break;

			}
			}
			propertyValueLabel.setText(this.value.toString());

			if (initialStatePropertiesPanel != null)
				initialStatePropertiesPanel.updateValue(this);

		}
	}

	/**
	 * 
	 * @return the propertyValueLabel
	 */
	public JLabel getPropertyValueLabel() {
		return propertyValueLabel;
	}

	/**
	 * @param propertyValueLabel
	 *            the propertyValueLabel to set
	 */
	public void setPropertyValueLabel(JLabel propertyValueLabel) {
		this.propertyValueLabel = propertyValueLabel;
	}

	/**
	 * @return the propertyValueSlider
	 */
	public JSlider getPropertyValueSlider() {
		return propertyValueSlider;
	}

	/**
	 * @param propertyValueSlider
	 *            the propertyValueSlider to set
	 */
	public void setPropertyValueSlider(JSlider propertyValueSlider) {
		this.propertyValueSlider = propertyValueSlider;
	}

	/**
	 * @param inputFieldSliderType
	 *            the inputFieldSliderType to set
	 */
	public void setInputFieldSliderType(
			InputFieldSliderType inputFieldSliderType) {
		this.inputFieldSliderType = inputFieldSliderType;
	}

	/**
	 * @return the inputFieldSliderType
	 */
	public InputFieldSliderType getInputFieldSliderType() {
		return inputFieldSliderType;
	}

	/**
	 * @param initialStatePropertiesPanel
	 *            the initialStatePropertiesPanel to set
	 */
	public void setInitialStatePropertiesPanel(
			InitialStatePropertiesPanel initialStatePropertiesPanel) {
		this.initialStatePropertiesPanel = initialStatePropertiesPanel;
	}

	/**
	 * @return the initialStatePropertiesPanel
	 */
	public InitialStatePropertiesPanel getInitialStatePropertiesPanel() {
		return initialStatePropertiesPanel;
	}

	public Object getValue() {

		return value;
	}

	/**
	 * @param multipyingFactor
	 *            the multipyingFactor to set
	 */
	public void setMultipyingFactor(Long multipyingFactor) {
		this.multipyingFactor = multipyingFactor;
	}

	/**
	 * @return the multipyingFactor
	 */
	public Long getMultipyingFactor() {
		return multipyingFactor;
	}

}
