package aors.module.initialStateUI.controller;

import java.util.HashMap;

public class InitialStateUIProperty {
	public static final Long Unbounded_Field_Length = new Long(-1);
	public static final Double No_Slider_Step_Size_Provided = new Double(1.0);
	public static final String No_Widget_Provided = "";
	public static final boolean No_Read_Only_Provided = false;
	public static final String No_Lang_Attr_Given = "default";
	public static final String ENGLISH_LANG = "en";
	public static final String DEUTSCH_LANG = "de";

	private Long inputFieldLength;

	// variable attribute in case of global variable
	private String propertyName;

	private Double sliderStepSize;

	private String widget;
	private Boolean readonly;
	private HashMap<String, String> languagePropertyLabelTextHashMap;
	private HashMap<String, String> languagePropertyHintTextHashMap;
	private Class<?> propertyClass;
	private Unit unit;

	/*
	 * Only for Global Variables ,as there are no instances,So single property
	 * instance for the complete Global Type
	 */
	private Object propertyValue;

	/*
	 * Only for Agents
	 */
	private AgentType agentType;

	private boolean propertyValid = true;

	/**
	 * @param inputFieldLength
	 *            the inputFieldLength to set
	 */
	public void setInputFieldLength(Long inputFieldLength) {
		this.inputFieldLength = inputFieldLength;

	}

	/**
	 * @return the inputFieldLength
	 */
	public Long getInputFieldLength() {
		return inputFieldLength;
	}

	/**
	 * @param propertyName
	 *            the propertyName to set
	 */
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	/**
	 * @return the propertyName
	 */
	public String getPropertyName() {
		return propertyName;
	}

	/**
	 * @param widget
	 *            the widget to set
	 */
	public void setWidget(String widget) {
		this.widget = widget;
	}

	/**
	 * @return the widget
	 */
	public String getWidget() {
		return widget;
	}

	/**
	 * @param readonly
	 *            the readonly to set
	 */
	public void setReadonly(Boolean readonly) {
		this.readonly = readonly;
	}

	/**
	 * @return the readonly
	 */
	public Boolean getReadonly() {
		return readonly;
	}

	/**
	 * @param languagePropertyLabelTextHashMap
	 *            the languagePropertyLabelTextHashMap to set
	 */
	public void setLanguagePropertyLabelTextHashMap(
			HashMap<String, String> languagePropertyLabelTextHashMap) {
		this.languagePropertyLabelTextHashMap = languagePropertyLabelTextHashMap;
	}

	/**
	 * @return the languagePropertyLabelTextHashMap
	 */
	public HashMap<String, String> getLanguagePropertyLabelTextHashMap() {
		return languagePropertyLabelTextHashMap;
	}

	/**
	 * @param languagePropertyHintTextHashMap
	 *            the languagePropertyHintTextHashMap to set
	 */
	public void setLanguagePropertyHintTextHashMap(
			HashMap<String, String> languagePropertyHintTextHashMap) {
		this.languagePropertyHintTextHashMap = languagePropertyHintTextHashMap;
	}

	/**
	 * @return the languagePropertyHintTextHashMap
	 */
	public HashMap<String, String> getLanguagePropertyHintTextHashMap() {
		return languagePropertyHintTextHashMap;
	}

	/**
	 * @param unit
	 *            the unit to set
	 */
	public void setUnit(Unit unit) {
		this.unit = unit;
	}

	/**
	 * @return the unit
	 */
	public Unit getUnit() {
		return unit;
	}

	/**
	 * @param unitType
	 *            the unitType to set
	 */

	public void initializePropertyWithDefaultValues() {

		this.setPropertyName("");
		this.setLanguagePropertyHintTextHashMap(null);
		this.setLanguagePropertyLabelTextHashMap(null);
		this
				.setReadonly((Boolean) InitialStateUIProperty.No_Read_Only_Provided);
		this.setWidget(InitialStateUIProperty.No_Widget_Provided);
		this.setInputFieldLength(InitialStateUIProperty.Unbounded_Field_Length);
		this.setPropertyClass(String.class);

	}

	/**
	 * @param propertyClass
	 *            the propertyClass to set
	 */
	public void setPropertyClass(Class<?> propertyClass) {
		this.propertyClass = propertyClass;
	}

	/**
	 * @return the propertyClass
	 */
	public Class<?> getPropertyClass() {
		return propertyClass;
	}

	/**
	 * @param propertyValid
	 *            the propertyValid to set
	 */
	public void setPropertyValid(boolean propertyValid) {
		this.propertyValid = propertyValid;
	}

	/**
	 * @return the propertyValid
	 */
	public boolean isPropertyValid() {
		return propertyValid;
	}

	/**
	 * @param propertyValue
	 *            the propertyValue to set
	 */
	public void setPropertyValue(Object propertyValue) {
		this.propertyValue = propertyValue;
	}

	/**
	 * @return the propertyValue
	 */
	public Object getPropertyValue() {
		return propertyValue;
	}

	/**
	 * @param sliderStepSize
	 *            the sliderStepSize to set
	 */
	public void setSliderStepSize(Double sliderStepSize) {
		this.sliderStepSize = sliderStepSize;
	}

	/**
	 * @return the sliderStepSize
	 */
	public Double getSliderStepSize() {
		return sliderStepSize;
	}

	/**
	 * @param propertyType
	 *            the propertyType to set
	 */
	public void setAgentType(AgentType agentType) {
		this.agentType = agentType;
	}

	/**
	 * @return the propertyType
	 */
	public AgentType getAgentType() {
		return agentType;
	}

}
