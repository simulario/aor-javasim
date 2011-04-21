package aors.module.initialStateUI.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.text.JTextComponent;

import aors.module.initialStateUI.controller.InitialStateUIProperty;
import aors.module.initialStateUI.controller.PropertyNameConstants;

public class CreateObjektPanel implements ActionListener, KeyListener {
	private JFrame createObjektFrame;
	private InitialStateUI initialStateUI;
	private ArrayList<JLabel> labelList;
	private ArrayList<JComponent> inputFieldsList;
	private JPanel topPanel;
	private JPanel bottomPanel;
	private JButton okButton;
	private JButton canButton;
	private HashMap<String, Object> createdInstancePropertiesValues;

	public CreateObjektPanel(InitialStateUI initialStateUI) {
		this.initialStateUI = initialStateUI;

		initializeCreateObjektFrame();
		initializeTopPanel();
		initializeBottomPanel();
		createObjektFrame.setLayout(new BorderLayout());
		createObjektFrame.add(topPanel, BorderLayout.CENTER);
		createObjektFrame.add(bottomPanel, BorderLayout.SOUTH);

		createObjektFrame.pack();

	}

	private void initializeBottomPanel() {
		bottomPanel = new JPanel();
		bottomPanel.setBorder(new EtchedBorder());

		initializeBottomPanelControls();

	}

	private void initializeBottomPanelControls() {

		bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		okButton = new JButton("OK");
		canButton = new JButton("Cancel");

		okButton.addActionListener(this);
		canButton.addActionListener(this);
		bottomPanel.add(okButton);
		bottomPanel.add(canButton);

	}

	private void initializeTopPanel() {
		topPanel = new JPanel();
		topPanel.setBorder(new EtchedBorder());

		initializeTopPanelControls();

	}

	private void initializeTopPanelControls() {

		labelList = new ArrayList<JLabel>();
		inputFieldsList = new ArrayList<JComponent>();

		ArrayList<String> propertiesNames = this.initialStateUI
				.getInitialStatePropertiesNamesList();

		ArrayList<InitialStateUIProperty> selectedTypePropertiesList = this.initialStateUI
				.getSelectedTypePropertiesList();

		ArrayList<String> initialStatePropertiesHintsList = this.initialStateUI
				.getInitialStatePropertiesHintsList();

		int labelListLength = propertiesNames.size() - 2;
		// typeName Property
		// InstanceHashMapKey
		// is not required
		// in create
		// Instance form

		String propertyHint;
		GridLayout gridLayout = new GridLayout(labelListLength, 3);

		topPanel.setLayout(gridLayout);
		JLabel jlabel;
		JLabel unitJLabel = null;
		String unitLabel;
		String propertyLabel;
		InitialStateUIProperty initialStateUIProperty;
		JComponent jComponent = null;
		for (int i = 0; i < labelListLength; i++) {

			int propertyIndex = i + 2;
			// For
			// leaving
			// out
			// typeName,InstanceHashMapKey
			// Property

			initialStateUIProperty = selectedTypePropertiesList
					.get(propertyIndex);

			unitLabel = this.initialStateUI.getinitialStateUIController()
					.getPropertyUnitLabel(initialStateUIProperty);

			propertyLabel = propertiesNames.get(propertyIndex);

			propertyLabel = "    " + propertyLabel + ": ";

			if (unitLabel != null) {

				unitLabel = "  " + unitLabel;
				unitJLabel = new JLabel(unitLabel);
			} else {
				unitJLabel = new JLabel("");
			}

			jlabel = new JLabel(propertyLabel);
			propertyHint = initialStatePropertiesHintsList.get(propertyIndex);

			jlabel.setToolTipText(propertyHint);

			labelList.add(jlabel);
			topPanel.add(jlabel);

			jComponent = initalizeInputField(initialStateUIProperty,
					propertyHint, jComponent);

			inputFieldsList.add(jComponent);
			topPanel.add(jComponent);

			topPanel.add(unitJLabel);
			jComponent = null;
		}
	}

	private JComponent initalizeInputField(

	InitialStateUIProperty initialStateUIProperty, String propertyHint,
			JComponent jComponent) {

		Class<?> propertyClass = initialStateUIProperty.getPropertyClass();

		if (initialStateUIProperty.getWidget().equals(
				InitialStateUIProperty.SLIDER_WIDGET)) {
			if (propertyClass.equals(long.class)) {

				jComponent = initializeSlider(jComponent,
						initialStateUIProperty, null, InputFieldSliderType.Long);

			} else if (propertyClass.equals(double.class)) {
				jComponent = initializeSlider(jComponent,
						initialStateUIProperty, null,
						InputFieldSliderType.Double);

			}

		}

		if (propertyClass.equals(boolean.class)) {

			jComponent = new JCheckBox();

		} else {

			if (jComponent != null)
				;

			else {

				jComponent = new JTextField();

				((JTextField) jComponent).setColumns(10);
			}

		}

		jComponent.setToolTipText(propertyHint);
		return jComponent;
	}

	private JComponent initializeSlider(JComponent jComponent,
			InitialStateUIProperty initialStateUIProperty,
			Object propertyValue, InputFieldSliderType inputFieldSliderType) {

		if (propertyValue != null)
			;
		else {
			propertyValue = initialStateUIProperty.getPropertyMin();
		}
		switch (inputFieldSliderType) {
		case Long: {
			jComponent = new InputFieldSlider((Long) initialStateUIProperty
					.getPropertyMin(), (Long) initialStateUIProperty
					.getPropertyMax(), new Long(initialStateUIProperty
					.getSliderStepSize().longValue()), (Long) propertyValue,
					null);
			break;

		}
		case Double: {
			jComponent = new InputFieldSlider((Double) initialStateUIProperty
					.getPropertyMin(), (Double) initialStateUIProperty
					.getPropertyMax(), new Double(initialStateUIProperty
					.getSliderStepSize()), (Double) propertyValue, null);

		}
		}

		return jComponent;
	}

	private void initializeCreateObjektFrame() {
		createObjektFrame = new JFrame("Create Instance");
		JFrame.setDefaultLookAndFeelDecorated(true);
		Dimension parentSize = initialStateUI.getSize();
		Point p = initialStateUI.getLocation();

		createObjektFrame.setLocation(p.x + parentSize.width / 2, p.y
				+ parentSize.height / 2);

		createObjektFrame.setVisible(true);

	}

	/**
	 * @param initialStateUI
	 *            the initialStateUI to set
	 */
	public void setInitialStateUI(InitialStateUI initialStateUI) {
		this.initialStateUI = initialStateUI;
	}

	/**
	 * @return the initialStateUI
	 */
	public InitialStateUI getInitialStateUI() {
		return initialStateUI;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(okButton)) {

			if (readDataForObjektCreation()) {
				this.createObjektFrame.dispose();

			}
		} else if (e.getSource().equals(canButton)) {
			this.createObjektFrame.dispose();
		}

	}

	private boolean readDataForObjektCreation() {

		this.createdInstancePropertiesValues = new HashMap<String, Object>();
		ArrayList<InitialStateUIProperty> selectedTypePropertiesList = this.initialStateUI
				.getSelectedTypePropertiesList();

		ArrayList<String> propertyLabels = this.initialStateUI
				.getInitialStatePropertiesNamesList();
		JComponent jComponent;
		InitialStateUIProperty initialStateUIProperty;
		Object propertyValue;
		String propertyName;
		String propertyLabel;
		boolean flagerror = false; // false - Going fine , True - error
		for (int i = 0; i < this.inputFieldsList.size(); i++) {
			initialStateUIProperty = selectedTypePropertiesList.get(i + 2);

			propertyName = initialStateUIProperty.getPropertyName();
			// TypeName & InstanceHashMap key are the properties at zeroth
			// and first index ,thats why they are left out
			jComponent = inputFieldsList.get(i);

			if (jComponent.getClass().equals(JTextField.class)) {
				String input = ((JTextComponent) jComponent).getText();
				if (input.equalsIgnoreCase("")) {
					JOptionPane.showMessageDialog(null,
							MessageBoxConstants.ERROR_FIELDS_EMPTY,
							MessageBoxConstants.TITLE_NEW,
							JOptionPane.ERROR_MESSAGE);
					flagerror = true;
					return !flagerror;

				}
			}
			// TypeName & InstanceHashMap key are the properties at zeroth
			// and first index ,thats why they are left out

			propertyLabel = propertyLabels.get(i + 2);
			propertyValue = getChangedPropertyValue(jComponent,
					initialStateUIProperty, propertyLabel);

			if (propertyValue != null) {
				this.createdInstancePropertiesValues.put(propertyName,
						propertyValue);

			} else {
				flagerror = true;
			}
		}
		if (!flagerror) {
			flagerror = this.initialStateUI.initializeObjektCreation();
		}

		return !flagerror;

	}

	private Object getChangedPropertyValue(JComponent jComponent,
			InitialStateUIProperty initialStateUIProperty, String propertyLabel) {

		String errMsg;
		Class<?> propertyClass = initialStateUIProperty.getPropertyClass();
		Object propertyValue = null;
		String propertyName = initialStateUIProperty.getPropertyName();

		try {
			if (jComponent instanceof InputFieldSlider) {
				InputFieldSlider inputFieldSlider = (InputFieldSlider) jComponent;

				propertyValue = inputFieldSlider.getValue();

			}

			else {
				if (propertyClass.equals(long.class)) {
					propertyValue = (Long
							.parseLong(((JTextComponent) jComponent).getText()));
					if (!propertyName.equals(PropertyNameConstants.INSTANCE_ID))
						propertyValue = InitialStatePropertiesPanel
								.checkForInputValidity(propertyValue,
										initialStateUIProperty, propertyLabel);

				} else if (propertyClass.equals(double.class)) {
					propertyValue = Double
							.parseDouble(((JTextComponent) jComponent)
									.getText());

					propertyValue = InitialStatePropertiesPanel
							.checkForInputValidity(propertyValue,
									initialStateUIProperty, propertyLabel);
				} else if (propertyClass.equals(boolean.class)) {
					propertyValue = ((JCheckBox) jComponent).isSelected();

				} else {
					propertyValue = ((JTextComponent) jComponent).getText();
				}
			}
		} catch (NumberFormatException e) {
			errMsg = "Property : " + propertyLabel + " is of "
					+ propertyClass.getSimpleName()
					+ " type. Please enter the correct type";

			JOptionPane.showMessageDialog(null, errMsg,
					MessageBoxConstants.TITLE_NEW, JOptionPane.ERROR_MESSAGE);

		}
		return propertyValue;

	}

	/**
	 * @param createdInstancePropertiesValues
	 *            the createdInstancePropertiesValues to set
	 */
	public void setCreatedInstancePropertiesValues(
			HashMap<String, Object> createdInstancePropertiesValues) {
		this.createdInstancePropertiesValues = createdInstancePropertiesValues;
	}

	/**
	 * @return the createdInstancePropertiesValues
	 */
	public HashMap<String, Object> getCreatedInstancePropertiesValues() {
		return createdInstancePropertiesValues;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent e) {
		JTextField jTextField = (JTextField) e.getSource();
		int inputFieldLength = jTextField.getColumns();
		if (inputFieldLength < jTextField.getText().length()) {
			JOptionPane.showMessageDialog(null,
					MessageBoxConstants.INPUT_FIELD_LENGTH_EXCEED
							+ inputFieldLength, MessageBoxConstants.TITLE_NEW,
					JOptionPane.INFORMATION_MESSAGE);
		}

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

}
