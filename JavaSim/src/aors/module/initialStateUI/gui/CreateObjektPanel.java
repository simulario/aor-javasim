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

	}

	private void initializeBottomPanel() {
		bottomPanel = new JPanel();
		bottomPanel.setBorder(new EtchedBorder());
		topPanel.setSize(this.createObjektFrame.getWidth(),
				(int) ((this.createObjektFrame.getHeight()) * (0.8)));
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
		topPanel.setSize(this.createObjektFrame.getWidth(),
				(int) ((this.createObjektFrame.getHeight()) * (0.8)));
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
		topPanel.setLayout(new GridLayout(labelListLength, 2));
		JLabel jlabel;
		InitialStateUIProperty initialStateUIProperty;
		JComponent jComponent;
		for (int i = 0; i < labelListLength; i++) {

			int propertyIndex = i + 2;
			// For
			// leaving
			// out
			// typeName,InstanceHashMapKey
			// Property

			propertyHint = initialStatePropertiesHintsList.get(propertyIndex);
			jlabel = new JLabel(propertiesNames.get(propertyIndex));
			jlabel.setToolTipText(propertyHint);

			labelList.add(jlabel);
			topPanel.add(jlabel);

			initialStateUIProperty = selectedTypePropertiesList
					.get(propertyIndex);

			jComponent = initalizeInputField(initialStateUIProperty,
					propertyHint);

			inputFieldsList.add(jComponent);
			topPanel.add(jComponent);
		}
	}

	private JComponent initalizeInputField(
			InitialStateUIProperty initialStateUIProperty, String propertyHint) {
		JComponent jComponent;
		if (initialStateUIProperty.getPropertyClass().equals(boolean.class)) {

			jComponent = new JCheckBox();

		} else {

			Long inputFieldLength = initialStateUIProperty
					.getInputFieldLength();
			if (inputFieldLength != null
					&& inputFieldLength != InitialStateUIProperty.Unbounded_Field_Length) {
				jComponent = new JTextField(inputFieldLength.intValue());
				// ((JTextField) jComponent).addKeyListener(this);

			} else

				jComponent = new JTextField(10); // Default Length taken
			// is 10
		}

		jComponent.setToolTipText(propertyHint);
		return jComponent;

	}

	private void initializeCreateObjektFrame() {
		createObjektFrame = new JFrame("Create Instance");
		JFrame.setDefaultLookAndFeelDecorated(true);
		Dimension parentSize = initialStateUI.getSize();
		Point p = initialStateUI.getLocation();

		createObjektFrame.setLocation(p.x + parentSize.width / 4, p.y
				+ parentSize.height / 4);

		createObjektFrame.setVisible(true);
		// newInstanceFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		createObjektFrame.setSize(parentSize.width / 3, parentSize.height / 3);

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

			// TypeName & InstanceHashMap key are the properties at zeroth
			// and first index ,thats why they are left out
			Class<?> propertyValueClass = initialStateUIProperty
					.getPropertyClass();
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
					propertyValueClass, propertyLabel);

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
			Class<? extends Object> propertyValueClass, String propertyLabel) {

		String errMsg;

		Object propertyValue = null;

		try {
			if (propertyValueClass.equals(long.class)) {
				propertyValue = (Long.parseLong(((JTextComponent) jComponent)
						.getText()));

			} else if (propertyValueClass.equals(double.class)) {
				propertyValue = Double
						.parseDouble(((JTextComponent) jComponent).getText());
			} else if (propertyValueClass.equals(boolean.class)) {
				propertyValue = ((JCheckBox) jComponent).isSelected();

			} else {
				propertyValue = ((JTextComponent) jComponent).getText();
			}
		} catch (NumberFormatException e) {
			errMsg = "Property : " + propertyLabel + " is of "
					+ propertyValueClass.getSimpleName()
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
