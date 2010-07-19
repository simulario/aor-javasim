package aors.module.initialStateUI.gui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import aors.module.initialStateUI.controller.CategoryType;
import aors.module.initialStateUI.controller.InitialStateUIController;
import aors.module.initialStateUI.controller.InitialStateUIProperty;
import aors.module.initialStateUI.controller.InitialStateUIType;

public class InitialStateUIBottomPanel implements ActionListener {

	private InitialStateUI initialStateUI;
	private JPanel bottomPanel;
	private JButton saveToXMLButton;
	private JButton saveAstoXMLButton;
	private JButton copyInstanceButton;
	private JButton newInstanceButton;
	private JButton deleteInstanceButton;
	private JComboBox languageComboBox;
	private String selectedLanguageType;

	private HashMap<String, HashMap<String, String>> buttonsTextHashMap;

	public InitialStateUIBottomPanel() {

		bottomPanel = new JPanel();
		bottomPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		bottomPanel.setBorder(new EtchedBorder());

		// Default Language Chosen when no language is selected
		this.selectedLanguageType = InitialStateUIProperty.ENGLISH_LANG;
	}

	public InitialStateUIBottomPanel(InitialStateUI initialStateUI,
			String selectedType) {
		this.initialStateUI = initialStateUI;
		bottomPanel = new JPanel();
		bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		bottomPanel.setSize(initialStateUI.getSize().width, 100);
		bottomPanel.setBorder(new EtchedBorder());

		InitialStateUIController initialStateUIController = initialStateUI
				.getinitialStateUIController();

		InitialStateUIType initialStateUIType = initialStateUIController
				.getInitialStateHashMap().getTypeStructure(selectedType);
		Object[] languageSet = initialStateUIType.getLanguageSet();

		languageComboBox = new JComboBox(languageSet);

		for (int i = 0; i < languageSet.length; i++) {
			if (languageSet[i].equals(InitialStateUIProperty.ENGLISH_LANG)) {
				languageComboBox.setSelectedIndex(i);
				break;
			} else
				languageComboBox.setSelectedIndex(0);

		}

		languageComboBox.addActionListener(this);

		saveToXMLButton = new JButton();
		saveAstoXMLButton = new JButton();
		copyInstanceButton = new JButton();
		newInstanceButton = new JButton();
		deleteInstanceButton = new JButton();

		copyInstanceButton.addActionListener(this);
		copyInstanceButton.setEnabled(false);

		deleteInstanceButton.addActionListener(this);
		deleteInstanceButton.setEnabled(false);

		if (selectedType.equals(CategoryType.Global.name())) {
			newInstanceButton.setEnabled(false);
		}

		newInstanceButton.addActionListener(this);

		populateButtonsTextHashMap(languageSet);

		selectedLanguageType = languageComboBox.getSelectedItem().toString();

		setButtonsText(selectedLanguageType);

		bottomPanel.add(languageComboBox);
		bottomPanel.add(copyInstanceButton);
		bottomPanel.add(newInstanceButton);
		bottomPanel.add(deleteInstanceButton);
		bottomPanel.add(saveToXMLButton);
		bottomPanel.add(saveAstoXMLButton);

	}

	private void setButtonsText(String selectedLanguage) {
		saveToXMLButton.setText(buttonsTextHashMap.get(
				ButtonConstants.SAVE_TO_XML_BUTTON).get(selectedLanguage));
		saveAstoXMLButton.setText(buttonsTextHashMap.get(
				ButtonConstants.SAVE_AS_TO_XML_BUTTON).get(selectedLanguage));
		copyInstanceButton.setText(buttonsTextHashMap.get(
				ButtonConstants.COPY_INSTANCE_BUTTON).get(selectedLanguage));
		newInstanceButton.setText(buttonsTextHashMap.get(
				ButtonConstants.NEW_INSTANCE_BUTTON).get(selectedLanguage));
		deleteInstanceButton.setText(buttonsTextHashMap.get(
				ButtonConstants.DELETE_INSTANCE_BUTTON).get(selectedLanguage));

	}

	private void populateButtonsTextHashMap(Object[] languageSet) {

		buttonsTextHashMap = new HashMap<String, HashMap<String, String>>();

		HashMap<String, String> saveToXMLButtonHashMap = new HashMap<String, String>();
		saveToXMLButtonHashMap.put(InitialStateUIProperty.ENGLISH_LANG,
				ButtonConstants.SAVE_ENGLISH);
		saveToXMLButtonHashMap.put(InitialStateUIProperty.DEUTSCH_LANG,
				ButtonConstants.SAVE_DEUTSCH);

		buttonsTextHashMap.put(ButtonConstants.SAVE_TO_XML_BUTTON,
				saveToXMLButtonHashMap);

		HashMap<String, String> saveAsToXMLButtonHashMap = new HashMap<String, String>();
		saveAsToXMLButtonHashMap.put(InitialStateUIProperty.ENGLISH_LANG,
				ButtonConstants.SAVE_AS_ENGLISH);
		saveAsToXMLButtonHashMap.put(InitialStateUIProperty.DEUTSCH_LANG,
				ButtonConstants.SAVE_AS_DEUTSCH);

		buttonsTextHashMap.put(ButtonConstants.SAVE_AS_TO_XML_BUTTON,
				saveAsToXMLButtonHashMap);

		HashMap<String, String> copyInstanceButtonHashMap = new HashMap<String, String>();
		copyInstanceButtonHashMap.put(InitialStateUIProperty.ENGLISH_LANG,
				ButtonConstants.COPY_ENGLISH);
		copyInstanceButtonHashMap.put(InitialStateUIProperty.DEUTSCH_LANG,
				ButtonConstants.COPY_DEUTSCH);

		buttonsTextHashMap.put(ButtonConstants.COPY_INSTANCE_BUTTON,
				copyInstanceButtonHashMap);

		HashMap<String, String> newInstanceXMLButtonHashMap = new HashMap<String, String>();
		newInstanceXMLButtonHashMap.put(InitialStateUIProperty.ENGLISH_LANG,
				ButtonConstants.NEW_ENGLISH);
		newInstanceXMLButtonHashMap.put(InitialStateUIProperty.DEUTSCH_LANG,
				ButtonConstants.NEW_DEUTSCH);

		buttonsTextHashMap.put(ButtonConstants.NEW_INSTANCE_BUTTON,
				newInstanceXMLButtonHashMap);

		HashMap<String, String> deleteInstanceButtonHashMap = new HashMap<String, String>();
		deleteInstanceButtonHashMap.put(InitialStateUIProperty.ENGLISH_LANG,
				ButtonConstants.DELETE_ENGLISH);
		deleteInstanceButtonHashMap.put(InitialStateUIProperty.DEUTSCH_LANG,
				ButtonConstants.DELETE_DEUTSCH);
		buttonsTextHashMap.put(ButtonConstants.DELETE_INSTANCE_BUTTON,
				deleteInstanceButtonHashMap);

	}

	/**
	 * @param bottomPanel
	 *            the bottomPanel to set
	 */
	public void setBottomPanel(JPanel bottomPanel) {
		this.bottomPanel = bottomPanel;
	}

	/**
	 * @return the bottomPanel
	 */
	public JPanel getBottomPanel() {
		return bottomPanel;
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

	/**
	 * @param buttonsTextHashMap
	 *            the buttonsTextHashMap to set
	 */
	public void setButtonsTextHashMap(
			HashMap<String, HashMap<String, String>> buttonsTextHashMap) {
		this.buttonsTextHashMap = buttonsTextHashMap;
	}

	/**
	 * @return the buttonsTextHashMap
	 */
	public HashMap<String, HashMap<String, String>> getButtonsTextHashMap() {
		return buttonsTextHashMap;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource().equals(languageComboBox)) {
			JComboBox languageComboBox = (JComboBox) e.getSource();
			String selectedLanguage = languageComboBox.getSelectedItem()
					.toString();
			System.out.println(selectedLanguage);
			this.selectedLanguageType = selectedLanguage;

			this.initialStateUI.updateUIForLanguageChosen();

			// this.initialStateUI.populateInitialStatePropertiesTable();

			setButtonsText(selectedLanguage);

		} else if (e.getSource().equals(copyInstanceButton)) {

			this.initialStateUI.copyInstance();
		} else if (e.getSource().equals(deleteInstanceButton)) {
			this.initialStateUI.deleteInstance();
		} else if (e.getSource().equals(newInstanceButton)) {
			this.initialStateUI.initializeCreateObjektPanel();
		}

	}

	/**
	 * @return the selectedLanguageType
	 */
	public String getSelectedLanguageType() {
		return selectedLanguageType;
	}

	/**
	 * @param selectedLanguageType
	 *            the selectedLanguageType to set
	 */
	public void setSelectedLanguageType(String selectedLanguageType) {
		this.selectedLanguageType = selectedLanguageType;
	}

	public void enableCopyDelete() {
		if (this.initialStateUI.getSelectedListType() != ListType.GLOBAL_VARIABLE_LIST) {
			this.copyInstanceButton.setEnabled(true);
			this.deleteInstanceButton.setEnabled(true);
		}
	}

	/**
	 * @return the languageComboBox
	 */
	public JComboBox getLanguageComboBox() {
		return languageComboBox;
	}

	/**
	 * @param languageComboBox
	 *            the languageComboBox to set
	 */
	public void setLanguageComboBox(JComboBox languageComboBox) {
		this.languageComboBox = languageComboBox;
	}

}
