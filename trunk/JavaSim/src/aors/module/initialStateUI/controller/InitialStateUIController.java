package aors.module.initialStateUI.controller;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import aors.controller.InitialState;
import aors.controller.SimulationDescription;
import aors.data.evt.ControllerEvent;
import aors.data.evt.sim.ObjektDestroyEvent;
import aors.data.evt.sim.ObjektInitEvent;
import aors.data.evt.sim.SimulationEvent;
import aors.data.evt.sim.SimulationStepEvent;
import aors.model.Entity;
import aors.model.agtsim.AgentSubject;
import aors.model.envevt.EnvironmentEvent;
import aors.model.envsim.AgentObject;
import aors.model.envsim.Objekt;
import aors.module.Module;
import aors.module.initialStateUI.gui.InitialStateUI;
import aors.module.initialStateUI.gui.MessageBoxConstants;
import aors.module.initialStateUI.gui.TypeList;

public class InitialStateUIController implements Module {

	private boolean initializedAtStartup = false;
	private InitialStateUI GUIComponent = null;
	private ArrayList<String> objectTypes;
	private ArrayList<String> eventTypes;
	private ArrayList<String> agentTypes;
	private ArrayList<String> globalVariables;
	private InitialState initialState;
	private InitialStateUIEditedInformation initialStateUIEditedInformation;

	private InitialStateUIHashMap initialStateUIHashMap;

	private SimulationDescription simulationDescription;

	private final String PX = SimulationDescription.ER_AOR_PREFIX + ":";

	private final String INITIALSTATEUI = "/" + PX + "SimulationScenario/" + PX
			+ "UserInterface/" + PX + "InitialStateUI";

	public InitialStateUIController() {

		this.setGUIComponent(new InitialStateUI(this));
	}

	@Override
	public void simulationStepEnd(SimulationStepEvent simulationStepEvent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void simulationStepStart(long stepNumber) {
		// TODO Auto-generated method stub

	}

	@Override
	public void simulationDomOnlyInitialization(
			SimulationDescription simulationDescription) {
		// TODO Auto-generated method stub

	}

	@Override
	public void simulationEnded() {
		// TODO Auto-generated method stub

	}

	@Override
	public void simulationEnvironmentEventOccured(
			EnvironmentEvent environmentEvent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void simulationInfosEvent(SimulationEvent simulationEvent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void simulationInitialize(InitialState initialState) {
		// Document dom = initialState.getSimulationDescription().getDom();

		this.simulationDescription = initialState.getSimulationDescription();

		this.initialState = initialState;

		if (this.initialStateUIEditedInformation != null) {

			this.initialStateUIEditedInformation
					.processInitialStateUIEditedInformation();

		}

		this.getInformationFromUserInterface();
		this.GetTypeNamesFromInitialStateUIHashMap();

		if (initializedAtStartup == true) {

			this.GUIComponent.updateListsPanel();

		}

		this.initializeTypeLists();
		this.initializedAtStartup = true;

	}

	// create the nodeNameSet to contain all kinds of type in the InitialStateUI
	// section
	public void getInformationFromUserInterface() {

		Node initialStateUINode = simulationDescription.getNode(INITIALSTATEUI);
		NodeList childrenNodeList = initialStateUINode.getChildNodes();

		this.setInitialStateHashMap(new InitialStateUIHashMap());

		Element typeNameElement;
		String typeName = new String();
		String nodeName;
		Object[] languageSet = null;
		Integer nmrOfInstances;

		for (int i = 0; i < childrenNodeList.getLength(); i++) {

			InitialStateUIType initialStateUIType = null;
			Node child = childrenNodeList.item(i);

			if (child instanceof Element) {
				typeNameElement = (Element) child;
				nodeName = typeNameElement.getNodeName();
				CategoryType categoryType = null;
				if (nodeName.equalsIgnoreCase(XMLConstants.OBJECT_UI)) {
					initialStateUIType = new InitialStateUIType();
					categoryType = CategoryType.Object;
					initialStateUIType.setCategoryType(categoryType);

					typeName = typeNameElement.getAttributeNode(
							XMLConstants.OBJECT_TYPE).getValue();
					initialStateUIType.setTypeName(typeName);

				} else if (nodeName.equalsIgnoreCase(XMLConstants.AGENT_UI)) {
					initialStateUIType = new InitialStateUIType();
					categoryType = CategoryType.Agent;
					initialStateUIType.setCategoryType(categoryType);

					typeName = typeNameElement.getAttributeNode(
							XMLConstants.AGENT_TYPE).getValue();
					initialStateUIType.setTypeName(typeName);
				} else if (nodeName.equalsIgnoreCase(XMLConstants.EVENT_UI)) {
					initialStateUIType = new InitialStateUIType();
					categoryType = CategoryType.Event;
					initialStateUIType.setCategoryType(categoryType);

					typeName = typeNameElement.getAttributeNode(
							XMLConstants.EVENT_TYPE).getValue();
					initialStateUIType.setTypeName(typeName);
				}

				else if (nodeName
						.equalsIgnoreCase(XMLConstants.GLOBAL_VARIABLE_UI)) {
					initialStateUIType = this.initialStateUIHashMap
							.getTypeStructure(CategoryType.Global.name());
					typeNameElement = ((Element) childrenNodeList.item(i));
					if (initialStateUIType != null) {
						categoryType = initialStateUIType.getCategoryType();
						typeName = initialStateUIType.getTypeName();
					}

					else {
						initialStateUIType = new InitialStateUIType();

						categoryType = CategoryType.Global;
						initialStateUIType.setCategoryType(categoryType);

						typeName = CategoryType.Global.name();
						initialStateUIType.setTypeName(typeName);
					}

				}

				Attr nmrOfInstancesAttr = typeNameElement
						.getAttributeNode(XMLConstants.NMR_OF_INSTANCES);

				if (nmrOfInstancesAttr != null) {

					nmrOfInstances = Integer.valueOf(nmrOfInstancesAttr
							.getValue());
					initialStateUIType.setNmrOfInstances(nmrOfInstances
							.intValue());

				} else {
					initialStateUIType
							.setNmrOfInstances(InitialStateUIType.UNBOUNDED);

				}
				this.initializePropertiesInfoHashMap(initialStateUIType, child,
						typeName, categoryType);
				this.initializeInitialStateUIInstancesHashMap(
						initialStateUIType, child, typeName, nodeName);

				for (InitialStateUIProperty initialStateUIProperty : initialStateUIType
						.getPropertiesInfoHashMap().values()) {

					Set<String> langSet = initialStateUIProperty
							.getLanguagePropertyHintTextHashMap().keySet();

					if (langSet
							.contains(InitialStateUIProperty.No_Lang_Attr_Given)) {
						langSet
								.remove(InitialStateUIProperty.No_Lang_Attr_Given);
					}
					languageSet = langSet.toArray();

					break;
				}

				initialStateUIType.setLanguageSet(languageSet);

				this.initialStateUIHashMap.addTypeStructure(typeName,
						initialStateUIType);

			}

		}

	}

	private void initializePropertiesInfoHashMap(
			InitialStateUIType initialStateUIType, Node typeNode,
			String typeName, CategoryType categoryType) {

		switch (categoryType) {
		case Agent:
		case Object:
		case Event: {
			initializePropertiesInfoHashMapObjektsEvents(initialStateUIType,
					typeNode, typeName, categoryType);
			break;
		}
		case Global: {
			initializePropertiesInfoHashMapGlobals(initialStateUIType,
					typeNode, typeName, categoryType);
		}
		}

	}

	private void initializePropertiesInfoHashMapGlobals(
			InitialStateUIType initialStateUIType, Node typeNode,
			String typeName, CategoryType categoryType) {

		if (initialStateUIType.getPropertiesInfoHashMap() != null)
			;
		else {
			initialStateUIType
					.setPropertiesInfoHashMap(new HashMap<String, InitialStateUIProperty>());
		}

		HashMap<String, InitialStateUIProperty> propertiesInfoHashMap = initialStateUIType
				.getPropertiesInfoHashMap();
		InitialStateUIProperty initialStateUIProperty = new InitialStateUIProperty();
		Element propertyElement;

		if (typeNode instanceof Element) {
			propertyElement = (Element) typeNode;
			initialStateUIProperty = initializeProperty(propertyElement,
					initialStateUIProperty, typeName, categoryType);
			if (initialStateUIProperty.isPropertyValid())
				propertiesInfoHashMap.put(initialStateUIProperty
						.getPropertyName(), initialStateUIProperty);
		}

		initialStateUIType.setPropertiesInfoHashMap(propertiesInfoHashMap);

	}

	private void initializePropertiesInfoHashMapObjektsEvents(
			InitialStateUIType initialStateUIType, Node typeNode,
			String typeName, CategoryType categoryType) {

		HashMap<String, InitialStateUIProperty> propertiesInfoHashMap = new HashMap<String, InitialStateUIProperty>();
		NodeList propertyNodeList;
		propertyNodeList = this.simulationDescription.getNodeList(PX
				+ XMLConstants.PROPERTY_UI, typeNode);

		if (categoryType.equals(CategoryType.Agent)) {
			initializeProperties(propertyNodeList, typeName,
					propertiesInfoHashMap, categoryType, AgentType.Objective);
			propertyNodeList = this.simulationDescription.getNodeList(PX
					+ XMLConstants.SUBJECTIVE_PROPERTY_UI, typeNode);
			propertiesInfoHashMap = initializeProperties(propertyNodeList,
					typeName, propertiesInfoHashMap, CategoryType.Agent,
					AgentType.Subjective);
		} else {
			initializeProperties(propertyNodeList, typeName,
					propertiesInfoHashMap, categoryType, null);
		}

		initialStateUIType.setPropertiesInfoHashMap(propertiesInfoHashMap);

	}

	private HashMap<String, InitialStateUIProperty> initializeProperties(
			NodeList propertyNodeList, String typeName,
			HashMap<String, InitialStateUIProperty> propertiesInfoHashMap,
			CategoryType categoryType, AgentType agentType) {

		InitialStateUIProperty initialStateUIProperty;
		for (int i = 0; i < propertyNodeList.getLength(); i++) {

			initialStateUIProperty = new InitialStateUIProperty();

			if (agentType != null)
				initialStateUIProperty.setAgentType(agentType);

			Node child = propertyNodeList.item(i);

			propertiesInfoHashMap = processPropertyNode(child,
					initialStateUIProperty, typeName, categoryType,
					propertiesInfoHashMap);

		}
		return propertiesInfoHashMap;
	}

	private HashMap<String, InitialStateUIProperty> processPropertyNode(
			Node propertyNode, InitialStateUIProperty initialStateUIProperty,
			String typeName, CategoryType categoryType,
			HashMap<String, InitialStateUIProperty> propertiesInfoHashMap) {
		Element propertyElement;
		if (propertyNode instanceof Element) {

			propertyElement = (Element) propertyNode;

			initialStateUIProperty = initializeProperty(propertyElement,
					initialStateUIProperty, typeName, categoryType);

			if (initialStateUIProperty.isPropertyValid())
				propertiesInfoHashMap.put(initialStateUIProperty
						.getPropertyName(), initialStateUIProperty);

		}
		return propertiesInfoHashMap;
	}

	private InitialStateUIProperty initializeProperty(Element propertyElement,
			InitialStateUIProperty initialStateUIProperty, String typeName,
			CategoryType categoryType) {
		String propertyName = new String();
		Long inputFieldLength;
		Boolean readOnly;
		String widget;
		Double sliderStepSize;

		propertyName = initializePropertyName(propertyElement, categoryType);
		inputFieldLength = initializeInputFieldLength(propertyElement);
		readOnly = initializeReadOnly(propertyElement);
		widget = initializeWidget(propertyElement);
		sliderStepSize = initializeSliderStepSize(propertyElement);

		initialStateUIProperty.setInputFieldLength(inputFieldLength);
		initialStateUIProperty.setPropertyName(propertyName);
		initialStateUIProperty.setReadonly(readOnly);
		initialStateUIProperty.setWidget(widget);

		initialStateUIProperty.setSliderStepSize(sliderStepSize);

		initialStateUIProperty
				.setLanguagePropertyLabelTextHashMap(new HashMap<String, String>());
		initialStateUIProperty
				.setLanguagePropertyHintTextHashMap(new HashMap<String, String>());

		initializeLanguagePropertyLabelTextHashMap(initialStateUIProperty,
				propertyElement);
		initializeLanguagePropertyHintTextHashMap(initialStateUIProperty,
				propertyElement);

		initializePropertyUnit(initialStateUIProperty, propertyElement);

		// Initiliaze Class of property , it also initializes propertyValue in
		// case of Globals
		initializePropertyClass(propertyName, typeName, initialStateUIProperty,
				categoryType);

		return initialStateUIProperty;

	}

	private String initializePropertyName(Element propertyElement,
			CategoryType categoryType) {
		String propertyName;
		if (categoryType.equals(CategoryType.Global)) {
			propertyName = propertyElement.getAttributeNode(
					XMLConstants.VARIABLE).getValue();
		} else {
			propertyName = propertyElement.getAttributeNode(
					XMLConstants.PROPERTY).getValue();
		}
		return propertyName;
	}

	private Double initializeSliderStepSize(Element propertyElement) {

		Double sliderStepSize;

		Attr sliderStepSizeAttribute = propertyElement
				.getAttributeNode(XMLConstants.SLIDER_STEP_SIZE);

		if (sliderStepSizeAttribute != null) {
			String sliderStepSizeString = sliderStepSizeAttribute.getValue();

			sliderStepSize = new Double((Double
					.parseDouble(sliderStepSizeString)));
		} else {
			sliderStepSize = InitialStateUIProperty.No_Slider_Step_Size_Provided;
		}
		return sliderStepSize;

	}

	private String initializeWidget(Element propertyElement) {

		String widget;

		Attr widgetAttribute = propertyElement
				.getAttributeNode(XMLConstants.WIDGET);

		if (widgetAttribute != null) {
			widget = widgetAttribute.getValue();

		} else {
			widget = InitialStateUIProperty.No_Widget_Provided;
		}
		return widget;
	}

	private Boolean initializeReadOnly(Element propertyElement) {

		boolean readOnly;
		Attr readOnlyAttribute = propertyElement
				.getAttributeNode(XMLConstants.READ_ONLY);
		if (readOnlyAttribute != null) {
			readOnly = Boolean.parseBoolean(readOnlyAttribute.getValue());
		} else {
			readOnly = (Boolean) InitialStateUIProperty.No_Read_Only_Provided;
		}

		return readOnly;
	}

	private Long initializeInputFieldLength(Element propertyElement) {

		Attr inputFieldLengthAttribute;
		Long inputFieldLength;
		inputFieldLengthAttribute = propertyElement
				.getAttributeNode(XMLConstants.INPUT_FIELD_LENGTH);

		if (inputFieldLengthAttribute != null) {
			String nmrOfInstancesString = inputFieldLengthAttribute.getValue();

			inputFieldLength = new Double(Double
					.parseDouble(nmrOfInstancesString)).longValue();
		} else {
			inputFieldLength = InitialStateUIProperty.Unbounded_Field_Length;
		}
		return inputFieldLength;
	}

	/*
	 * initializes Given property class . and its value(Only in case of Global
	 * Variables)
	 */
	private void initializePropertyClass(String propertyName, String typeName,
			InitialStateUIProperty initialStateUIProperty,
			CategoryType categoryType) {

		String packageAndClassName = null;

		packageAndClassName = initializePackageAndClassName(typeName,
				categoryType, initialStateUIProperty.getAgentType());

		Class<?> typeNameClass = this.initialState
				.classForName(packageAndClassName);

		String getterName = "get" + propertyName.substring(0, 1).toUpperCase()
				+ propertyName.substring(1);
		Method getter = null;
		Class<?> propertyClass = null;
		Object propertyValue = null;

		try {
			getter = typeNameClass.getMethod(getterName);
			propertyClass = getter.getReturnType();

		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			try {
				getterName = "is" + propertyName.substring(0, 1).toUpperCase()
						+ propertyName.substring(1);
				getter = typeNameClass.getMethod(getterName);
				propertyClass = getter.getReturnType();

			} catch (SecurityException e1) {
				e1.printStackTrace();
			} catch (IllegalArgumentException e1) {
				e1.printStackTrace();
			} catch (NoSuchMethodException e1) {
				System.out.println("Property '" + propertyName
						+ "' doesn't exist for the objeKtName :'" + typeName
						+ "'");
				initialStateUIProperty.setPropertyValid(false);

			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}

		if (categoryType.equals(CategoryType.Global)) {

			try {
				propertyValue = getter.invoke(typeNameClass);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			initialStateUIProperty.setPropertyValue(propertyValue);
		}
		initialStateUIProperty.setPropertyClass(propertyClass);

	}

	private String initializePackageAndClassName(String typeName,
			CategoryType categoryType, AgentType agentType) {
		String packageName = null;
		switch (categoryType) {
		case Agent: {
			switch (agentType) {
			case Objective: {
				packageName = PackageNameConstants.AGENT_OBJECTIVE;
				break;
			}
			case Subjective: {
				packageName = PackageNameConstants.AGENT_SUBJECTIVE;
				typeName = typeName + "AgentSubject";

				break;
			}
			}
			break;
		}
		case Event: {

			packageName = PackageNameConstants.EVENT;
			break;

		}
		case Object: {
			packageName = PackageNameConstants.OBJECT;
			break;
		}
		case Global: {
			packageName = PackageNameConstants.GLOBAL;
			break;
		}

		}

		return packageName + "." + typeName;

	}

	public void initializeInitialStateUIInstancesHashMap(
			InitialStateUIType initialStateUIType, Node typeNode,
			String typeName, String categoryName) {

		switch (initialStateUIType.getCategoryType()) {
		case Agent: {
			initializeInitialStateUIObjektsInstancesHashMap(initialStateUIType,
					typeNode, typeName); // For Agents and Objects
			break;
		}
		case Object: {
			initializeInitialStateUIObjektsInstancesHashMap(initialStateUIType,
					typeNode, typeName); // For Agents and Objects
			break;
		}
		case Event: {
			initializeInitialStateUIEventInstancesHashMap(initialStateUIType,
					typeNode, typeName);
			break;

		}

		case Global: {
			initialStateUIType.setInstancesHashMap(null);
			break;
		}

		}

	}

	private void initializeInitialStateUIEventInstancesHashMap(
			InitialStateUIType initialStateUIType, Node typeNode,
			String typeName) {
		initialStateUIType
				.setInstancesHashMap(new HashMap<Long, InitialStateUIInstance>());
		List<EnvironmentEvent> eventList = this.initialState.getEvents();

		Long environmentEventIndex;
		Long eventInstanceId;
		String environmentEventInstanceName;
		InitialStateUIInstance initialStateUIInstance;
		String eventClassName;
		for (EnvironmentEvent environmentEvent : eventList) {
			eventClassName = environmentEvent.getClass().getSimpleName();
			if (eventClassName.equalsIgnoreCase(typeName)) {

				environmentEventIndex = new Long(eventList
						.indexOf(environmentEvent));
				eventInstanceId = environmentEvent.getId();
				environmentEventInstanceName = environmentEvent.getName();
				initialStateUIInstance = new InitialStateUIInstance(
						eventInstanceId);
				initialStateUIInstance.setName(environmentEventInstanceName);

				this.initializeInitialStateUIinstancePropertiesHashMap(
						initialStateUIType, initialStateUIInstance, typeNode,
						typeName, environmentEvent);
				initialStateUIType.getInstancesHashMap().put(
						environmentEventIndex, initialStateUIInstance);

			}

		}

	}

	private void initializeInitialStateUIObjektsInstancesHashMap(
			InitialStateUIType initialStateUIType, Node typeNode,
			String typeName) {
		initialStateUIType
				.setInstancesHashMap(new HashMap<Long, InitialStateUIInstance>());

		List<Objekt> typeNameObjektList = this.initialState
				.getObjectsByType(typeName);

		Iterator<Objekt> typeNameObjektListIterator = typeNameObjektList
				.iterator();

		Objekt typeNameObjeckt;
		Long typeNameObjektInstanceId;
		String typeNameObjektInstanceName;
		InitialStateUIInstance initialStateUIInstance;

		while (typeNameObjektListIterator.hasNext()) {
			typeNameObjeckt = typeNameObjektListIterator.next();
			typeNameObjektInstanceId = typeNameObjeckt.getId();
			typeNameObjektInstanceName = typeNameObjeckt.getName();
			initialStateUIInstance = new InitialStateUIInstance(
					typeNameObjektInstanceId);

			initialStateUIInstance.setName(typeNameObjektInstanceName);

			this.initializeInitialStateUIinstancePropertiesHashMap(
					initialStateUIType, initialStateUIInstance, typeNode,
					typeName, typeNameObjeckt);

			initialStateUIType.getInstancesHashMap().put(
					typeNameObjektInstanceId, initialStateUIInstance);

		}

	}

	private void initializeInitialStateUIinstancePropertiesHashMap(
			InitialStateUIType initialStateUIType,
			InitialStateUIInstance initialStateUIInstance, Node typeNode,
			String typeName, Entity typeNameEntity) {

		CategoryType categoryType = initialStateUIType.getCategoryType();
		HashMap<String, InitialStateUIPropertyValue> instancePropertiesValuesHashMap = new HashMap<String, InitialStateUIPropertyValue>();
		NodeList propertyNodeList;

		propertyNodeList = this.simulationDescription.getNodeList(PX
				+ XMLConstants.PROPERTY_UI, typeNode);

		instancePropertiesValuesHashMap = initializePropertiesValues(
				propertyNodeList, typeName, instancePropertiesValuesHashMap,
				categoryType, typeNameEntity);

		if (categoryType.equals(CategoryType.Agent)) {
			Entity subjectivePart = this.initialState
					.getAgentSubjectById(initialStateUIInstance.getId());
			propertyNodeList = this.simulationDescription.getNodeList(PX
					+ XMLConstants.SUBJECTIVE_PROPERTY_UI, typeNode);
			instancePropertiesValuesHashMap = initializePropertiesValues(
					propertyNodeList, typeName,
					instancePropertiesValuesHashMap, categoryType,
					subjectivePart);
		}

		initialStateUIInstance
				.setInstancePropertiesValuesHashMap(instancePropertiesValuesHashMap);

	}

	private HashMap<String, InitialStateUIPropertyValue> initializePropertiesValues(
			NodeList propertyNodeList,
			String typeName,
			HashMap<String, InitialStateUIPropertyValue> instancePropertiesValuesHashMap,
			CategoryType categoryType, Entity typeNameEntity) {
		Element propertyElement;
		String propertyName = new String();
		for (int i = 0; i < propertyNodeList.getLength(); i++) {

			InitialStateUIPropertyValue initialStateUIPropertyValue = new InitialStateUIPropertyValue();
			Node child = propertyNodeList.item(i);

			if (child instanceof Element) {

				propertyElement = (Element) child;

				propertyName = propertyElement.getAttributeNode(
						XMLConstants.PROPERTY).getValue();

				initializePropertyValueObjektsEvents(propertyName,
						initialStateUIPropertyValue, typeNameEntity);

				if (initialStateUIPropertyValue.isPropertyValid() == true) {

					instancePropertiesValuesHashMap.put(propertyName,
							initialStateUIPropertyValue);
				}

			}
		}
		return instancePropertiesValuesHashMap;
	}

	private void initializePropertyValueObjektsEvents(String propertyName,
			InitialStateUIPropertyValue initialStateUIPropertyValue,
			Entity typeNameEntity) {
		String getterName = "get" + propertyName.substring(0, 1).toUpperCase()
				+ propertyName.substring(1);
		Object value = null;
		try {
			Method getter = typeNameEntity.getClass().getMethod(getterName);

			value = getter.invoke(typeNameEntity);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			try {
				getterName = "is" + propertyName.substring(0, 1).toUpperCase()
						+ propertyName.substring(1);
				Method getter = typeNameEntity.getClass().getMethod(getterName);

				value = getter.invoke(typeNameEntity);
			} catch (SecurityException e1) {
				e1.printStackTrace();
			} catch (IllegalArgumentException e1) {
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				e1.printStackTrace();
			} catch (InvocationTargetException e1) {
				e1.printStackTrace();
			} catch (NoSuchMethodException e1) {
				System.out.println("Property '" + initialStateUIPropertyValue
						+ "' doesn't exist for the objectName :'"
						+ typeNameEntity.getName() + "'");
				initialStateUIPropertyValue.setPropertyValid(false);

			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		initialStateUIPropertyValue.setPropertyValue(value);

	}

	private void initializePropertyUnit(
			InitialStateUIProperty initialStateUIProperty, Node propertyNode) {
		Node unitNode = this.simulationDescription.getNode(PX
				+ XMLConstants.UNIT, propertyNode);
		if (unitNode != null) {
			NodeList unitQuantityNodes = unitNode.getChildNodes();
			initialStateUIProperty.setUnit(new Unit());
			Unit unit = initialStateUIProperty.getUnit();

			Element unitQuantityElement;
			String unitQuantityName = new String();
			for (int i = 0; i < unitQuantityNodes.getLength(); i++)

				if (unitNode instanceof Element) {
					unitQuantityElement = (Element) unitNode;
					unitQuantityName = unitQuantityElement.getNodeName();

					for (UnitQuantityType unitQuantityType : UnitQuantityType
							.values()) {
						if (unitQuantityType.name().equalsIgnoreCase(
								unitQuantityName)) {
							unit.setUnitQuantityType(unitQuantityType);
							findUnitType(unit, unitQuantityElement);

						}

					}

				}
		} else {
			Unit unit = new Unit();
			unit.setUnitPresent(false);
			initialStateUIProperty.setUnit(unit);
		}

	}

	private void findUnitType(Unit unit, Element unitQuantityElement) {
		String unitValue = unitQuantityElement.getNodeValue();
		switch (unit.getUnitQuantityType()) {
		case Area: {

			int superScriptIndex = unitValue.indexOf(253);
			if (superScriptIndex != -1) {
				unitValue = unitValue.substring(0, superScriptIndex);

			}

			if (unitValue.equalsIgnoreCase("ar")) {
				unit.area = Area.ar;
			} else if (unitValue.equalsIgnoreCase("cm")) {
				unit.area = Area.cmsq;
			} else if (unitValue.equalsIgnoreCase("ha"))
				unit.area = Area.ha;
			else if (unitValue.equalsIgnoreCase("km"))
				unit.area = Area.kmsq;
			else if (unitValue.equalsIgnoreCase("mm"))
				unit.area = Area.mmsq;
			else if (unitValue.equalsIgnoreCase("m"))
				unit.area = Area.msq;

			break;
		}

		case Currency: {
			int leftParenIndex = unitValue.indexOf('(');
			if (leftParenIndex != -1) {
				unitValue = unitValue.substring(0, leftParenIndex);
			}

			for (Currency currency : Currency.values()) {
				if (currency.name().equalsIgnoreCase(unitValue)) {
					unit.currency = currency;
				}
			}
			break;
		}
		case Length: {
			for (Length length : Length.values()) {
				if (length.name().equalsIgnoreCase(unitValue)) {
					unit.length = length;
				}
			}
			break;
		}
		case Math: {
			// 248 Ascii Value for Degree
			int degreeIndex = unitValue.indexOf(248);
			if (degreeIndex != -1) {
				unit.math = Math.DEGREE;
			} else if (unitValue.equalsIgnoreCase("%")) {
				unit.math = Math.PERCENT;
			} else if (unitValue.equalsIgnoreCase("RAD")) {
				unit.math = Math.RADIAN;
			} else
				unit.math = Math.PERMIL;
			break;
		}
		case Physics: {
			int startChar = unitValue.charAt(0);
			if ((startChar >= 65 && startChar <= 90)
					|| (startChar >= 97 && startChar <= 122)) {
				for (Physics physics : Physics.values()) {
					if (physics.name().equalsIgnoreCase(unitValue)) {
						unit.physics = physics;
					}
				}

			} else {
				int degreeIndex = unitValue.indexOf(248);
				if (degreeIndex != -1) {
					char lastChar = unitValue.charAt(degreeIndex + 1);
					switch (lastChar) {
					case 'C': {
						unit.physics = Physics.degC;
						break;
					}
					case 'F': {
						unit.physics = Physics.degF;
						break;
					}

					}

				} else {
					unit.physics = Physics.Ohm;
				}

			}
			break;
		}
		case Time: {
			for (Time time : Time.values()) {
				if (time.name().equalsIgnoreCase(unitValue)) {
					unit.time = time;
				}
			}
			break;
		}
		case Volume: {
			int superScriptIndex = unitValue.indexOf(252);
			if (superScriptIndex != -1) {
				unitValue = unitValue.substring(0, superScriptIndex);
				if (unitValue.equalsIgnoreCase("cm")) {
					unit.volume = Volume.cmcube;
				} else if (unitValue.equalsIgnoreCase("mm")) {
					unit.volume = Volume.mmcube;
				} else if (unitValue.equalsIgnoreCase("m")) {
					unit.volume = Volume.mcube;

				}

			} else {
				for (Volume volume : Volume.values()) {
					if (volume.name().equalsIgnoreCase(unitValue)) {
						unit.volume = volume;
					}
				}
			}
			break;
		}
		case Weight: {
			for (Weight weight : Weight.values()) {
				if (weight.name().equalsIgnoreCase(unitValue)) {
					unit.weight = weight;
				}
			}
			break;
		}
		}

	}

	private void initializeLanguagePropertyHintTextHashMap(
			InitialStateUIProperty initialStateUIProperty, Node propertyNode) {

		Node hintNode = this.simulationDescription.getNode(PX
				+ XMLConstants.HINT, propertyNode);
		NodeList textNodeList = this.simulationDescription.getNodeList(PX
				+ XMLConstants.TEXT, hintNode);
		HashMap<String, String> languagePropertyHintTextHashMap = initialStateUIProperty
				.getLanguagePropertyHintTextHashMap();
		Element textElement;
		Node child;
		String language = new String();
		String text = new String();
		Attr langAttribute;
		for (int i = 0; i < textNodeList.getLength(); i++) {
			child = textNodeList.item(i);
			if (child instanceof Element) {
				textElement = (Element) child;

				langAttribute = textElement
						.getAttributeNode(XMLConstants.XML_LANG);

				if (langAttribute != null) {
					language = langAttribute.getValue();
					if (language.equalsIgnoreCase("")) {
						language = InitialStateUIProperty.No_Lang_Attr_Given;
					}
				} else {
					language = InitialStateUIProperty.No_Lang_Attr_Given;
				}
				text = textElement.getTextContent();
				languagePropertyHintTextHashMap.put(language, text);

			}
		}

	}

	private void initializeLanguagePropertyLabelTextHashMap(
			InitialStateUIProperty initialStateUIProperty, Node propertyNode) {

		Node labelNode = this.simulationDescription.getNode(PX
				+ XMLConstants.LABEL, propertyNode);
		NodeList textNodeList = this.simulationDescription.getNodeList(PX
				+ XMLConstants.TEXT, labelNode);
		HashMap<String, String> languagePropertyLabelTextHashMap = initialStateUIProperty
				.getLanguagePropertyLabelTextHashMap();
		Element textElement;
		Node child;
		String language = new String();
		String text = new String();
		Attr langAttribute;
		for (int i = 0; i < textNodeList.getLength(); i++) {
			child = textNodeList.item(i);
			if (child instanceof Element) {
				textElement = (Element) child;

				langAttribute = textElement
						.getAttributeNode(XMLConstants.XML_LANG);

				if (langAttribute != null) {
					language = langAttribute.getValue();
					if (language.equalsIgnoreCase("")) {
						language = InitialStateUIProperty.No_Lang_Attr_Given;
					}
				} else {
					language = InitialStateUIProperty.No_Lang_Attr_Given;
				}

				text = textElement.getTextContent();
				languagePropertyLabelTextHashMap.put(language, text);

			}
		}

	}

	private void initializeTypeLists() {

		InitialStateUI GUIComponent = (InitialStateUI) this.getGUIComponent();
		GUIComponent.setTypeLists(new ArrayList<TypeList>());

		GUIComponent.populateObjectList();
		GUIComponent.populateAgentList();
		GUIComponent.populateEventList();

		GUIComponent.populateGlobalVariablesList();

	}

	private void GetTypeNamesFromInitialStateUIHashMap() {

		this.objectTypes = new ArrayList<String>();
		this.eventTypes = new ArrayList<String>();
		this.agentTypes = new ArrayList<String>();
		this.globalVariables = new ArrayList<String>();

		CategoryType superType;

		for (String typeName : this.initialStateUIHashMap
				.getInitialStateTypeshashMap().keySet()) {

			superType = this.initialStateUIHashMap.getTypeStructure(typeName)
					.getCategoryType();

			switch (superType) {
			case Object: {
				this.objectTypes.add(typeName);
				break;
			}
			case Agent: {
				this.agentTypes.add(typeName);
				break;
			}
			case Event: {
				this.eventTypes.add(typeName);
				break;
			}
			case Global: {

				// Since Global Variable names displayed in the above list is
				// dependent on the
				// language chosen ,we have to initialize global vaiable names
				// everytime when lanaguage is chosen
				String selectedLanguageType = this.GUIComponent
						.getInitialStateUIBottomPanel()
						.getSelectedLanguageType();
				this.getGlobalVariableNames(this.initialStateUIHashMap
						.getTypeStructure(typeName), selectedLanguageType);
				break;
			}

			}
		}

	}

	private void getGlobalVariableNames(InitialStateUIType initialStateUIType,
			String selectedLanguageType) {
		this.globalVariables = new ArrayList<String>();
		ArrayList<String> globalVariables = this.getGlobalVariables();
		HashMap<String, InitialStateUIProperty> propertiesInfoHashMap = initialStateUIType
				.getPropertiesInfoHashMap();
		String propertyLabel;
		for (InitialStateUIProperty initialStateUIProperty : propertiesInfoHashMap
				.values()) {
			propertyLabel = getPropertyLabel(initialStateUIProperty,
					selectedLanguageType);
			globalVariables.add(propertyLabel);

		}

	}

	@Override
	public void simulationPaused(boolean pauseState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void simulationProjectDirectoryChanged(File projectDirectory) {
		// TODO Auto-generated method stub

	}

	@Override
	public void simulationStarted() {
		// TODO Auto-generated method stub

	}

	@Override
	public void objektDestroyEvent(ObjektDestroyEvent objektDestroyEvent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void objektInitEvent(ObjektInitEvent objInitEvent) {
		// TODO Auto-generated method stub

	}

	public void setEnabled(boolean b) {
		// TODO Auto-generated method stub

	}

	public boolean getEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setObjectTypes(ArrayList<String> objectTypes) {
		this.objectTypes = objectTypes;
	}

	public ArrayList<String> getObjectTypes() {
		return objectTypes;
	}

	public void setInitialStateHashMap(InitialStateUIHashMap initialStateHashMap) {
		this.initialStateUIHashMap = initialStateHashMap;
	}

	public InitialStateUIHashMap getInitialStateHashMap() {
		return initialStateUIHashMap;
	}

	public void setEventTypes(ArrayList<String> eventTypes) {
		this.eventTypes = eventTypes;
	}

	public ArrayList<String> getEventTypes() {
		return eventTypes;
	}

	public void setAgentTypes(ArrayList<String> agentTypes) {
		this.agentTypes = agentTypes;
	}

	public ArrayList<String> getAgentTypes() {
		return agentTypes;
	}

	/**
	 * @param simulationDescription
	 *            the simulationDescription to set
	 */
	public void setSimulationDescription(
			SimulationDescription simulationDescription) {
		this.simulationDescription = simulationDescription;
	}

	/**
	 * @return the simulationDescription
	 */
	public SimulationDescription getSimulationDescription() {
		return simulationDescription;
	}

	/**
	 * @param globalVariables
	 *            the globalVariables to set
	 */
	public void setGlobalVariables(ArrayList<String> globalVariables) {
		this.globalVariables = globalVariables;
	}

	/**
	 * @return the globalVariables
	 */
	public ArrayList<String> getGlobalVariables() {
		return globalVariables;
	}

	public void getPropertiesListForSelectedType(String selectedType) {

		InitialStateUI initialStateUI = (InitialStateUI) this.getGUIComponent();
		ArrayList<InitialStateUIProperty> selectedTypePropertiesList = new ArrayList<InitialStateUIProperty>();

		InitialStateUIProperty instancesHashMapKeyProperty = new InitialStateUIProperty();
		instancesHashMapKeyProperty.initializePropertyWithDefaultValues();
		instancesHashMapKeyProperty
				.setPropertyName(PropertyNameConstants.INSTANCE_HASH_MAP_KEY);
		instancesHashMapKeyProperty.setPropertyClass(long.class);

		InitialStateUIProperty selectedTypeProperty = new InitialStateUIProperty();
		selectedTypeProperty.initializePropertyWithDefaultValues();
		selectedTypeProperty.setPropertyName(PropertyNameConstants.TYPE_NAME);
		selectedTypeProperty.setPropertyClass(String.class);

		InitialStateUIProperty instanceIDProperty = new InitialStateUIProperty();
		instanceIDProperty.initializePropertyWithDefaultValues();
		instanceIDProperty.setPropertyName(PropertyNameConstants.INSTANCE_ID);
		instanceIDProperty.setPropertyClass(long.class);

		InitialStateUIProperty nameProperty = new InitialStateUIProperty();
		nameProperty.initializePropertyWithDefaultValues();
		nameProperty.setPropertyName(PropertyNameConstants.INSTANCE_NAME);
		nameProperty.setPropertyClass(String.class);

		selectedTypePropertiesList.add(instancesHashMapKeyProperty);
		selectedTypePropertiesList.add(selectedTypeProperty);
		selectedTypePropertiesList.add(instanceIDProperty);
		selectedTypePropertiesList.add(nameProperty);

		InitialStateUIType initialStateUIType = this.initialStateUIHashMap
				.getTypeStructure(selectedType);

		initialStateUI
				.setSelectedTypePropertiesList(selectedTypePropertiesList);

		getPropertiesStructureForRemainingProperties(selectedType,
				initialStateUIType, initialStateUI);

	}

	private void getPropertiesStructureForRemainingProperties(
			String selectedType, InitialStateUIType initialStateUIType,
			InitialStateUI initialStateUI) {

		ArrayList<InitialStateUIProperty> selectedTypePropertiesList = initialStateUI
				.getSelectedTypePropertiesList();
		;
		HashMap<String, InitialStateUIProperty> propertiesInfoHashMap = initialStateUIType
				.getPropertiesInfoHashMap();

		InitialStateUIProperty initialStateUIProperty2;

		for (InitialStateUIProperty initialStateUIProperty : propertiesInfoHashMap
				.values()) {
			if (initialStateUIProperty.getPropertyName().equalsIgnoreCase(
					PropertyNameConstants.INSTANCE_NAME)) {

				for (int i = 0; i < selectedTypePropertiesList.size(); i++) {
					initialStateUIProperty2 = selectedTypePropertiesList.get(i);
					if (initialStateUIProperty2.getPropertyName()
							.equalsIgnoreCase(
									PropertyNameConstants.INSTANCE_NAME)) {
						int index = selectedTypePropertiesList
								.indexOf(initialStateUIProperty2);
						selectedTypePropertiesList.remove(index);
						selectedTypePropertiesList.add(index,
								initialStateUIProperty);

					}
				}

			} else {
				selectedTypePropertiesList.add(initialStateUIProperty);
			}
		}
	}

	public void getPropertiesValuesForSelectedType(String selectedType) {

		InitialStateUI initialStateUI = (InitialStateUI) this.getGUIComponent();

		ArrayList<String> initialStatePropertiesNamesList = new ArrayList<String>();
		ArrayList<Object> initialStatePropertiesData = new ArrayList<Object>();

		initialStatePropertiesNamesList
				.add(PropertyNameConstants.INSTANCE_HASH_MAP_KEY);

		initialStatePropertiesNamesList.add(PropertyNameConstants.TYPE_NAME);
		initialStatePropertiesNamesList.add(PropertyNameConstants.INSTANCE_ID);

		initialStatePropertiesNamesList
				.add(PropertyNameConstants.INSTANCE_NAME);

		InitialStateUIType initialStateUIType = this.initialStateUIHashMap
				.getTypeStructure(selectedType);

		String typeName = initialStateUIType.getTypeName();

		HashMap<Long, InitialStateUIInstance> initialStateInstancesHashMap = initialStateUIType
				.getInstancesHashMap();
		long previousKey = -1;

		for (Long key : initialStateInstancesHashMap.keySet()) {

			initialStatePropertiesData.add(key);// InstancesHAshMapKey

			// For events, Key in instancesHashMap and instanceID are different
			// ,but for
			// Objects & Agents these are same

			initialStatePropertiesData.add(typeName);
			InitialStateUIInstance initialStateUIInstance = initialStateInstancesHashMap
					.get(key);

			initialStatePropertiesData.add(initialStateUIInstance.getId()); // InstanceID

			String instanceName = initialStateUIInstance.getName();
			initialStatePropertiesData.add(instanceName);

			initialStateUI
					.setInitialStatePropertiesNamesList(initialStatePropertiesNamesList);
			initialStateUI
					.setInitialStatePropertiesData(initialStatePropertiesData);

			/*
			 * For Rest of the Properties for the selected Type
			 */
			getInformationForRemainingProperties(selectedType, key,
					initialStateUI, previousKey);
			previousKey = key;

		}

	}

	private void getInformationForRemainingProperties(String selectedType,
			Long key, InitialStateUI initialStateUI, long previousKey) {
		InitialStateUIType initialStateUIType = initialStateUI
				.getinitialStateUIController().getInitialStateHashMap()
				.getTypeStructure(selectedType);

		ArrayList<String> propertiesTypeTableColumnList = initialStateUI
				.getInitialStatePropertiesNamesList();
		ArrayList<Object> propertiesTypeTableDataList = initialStateUI
				.getInitialStatePropertiesData();
		InitialStateUIPropertyValue initialStateUIPropertyValue;
		HashMap<String, InitialStateUIPropertyValue> propertiesValuesHashMap;
		long currentKey;
		currentKey = key;

		InitialStateUIInstance initialStateUIInstance = initialStateUIType
				.getInitialStateUIInstanceStructure(key);

		System.out.println(initialStateUIInstance.getId());
		System.out.println(initialStateUIInstance.getName());
		propertiesValuesHashMap = initialStateUIInstance
				.getInstancePropertiesValuesHashMap();

		for (Object propertyName : propertiesValuesHashMap.keySet()) {

			System.out.println(propertyName);

			if (!propertiesTypeTableColumnList.contains(propertyName)) {

				propertiesTypeTableColumnList.add((String) propertyName);
				initialStateUIPropertyValue = propertiesValuesHashMap
						.get(propertyName);

				propertiesTypeTableDataList.add(initialStateUIPropertyValue
						.getPropertyValue());
			} else if (currentKey != previousKey) {
				if (!((String) propertyName)
						.equalsIgnoreCase(PropertyNameConstants.INSTANCE_NAME))

				{
					initialStateUIPropertyValue = propertiesValuesHashMap
							.get(propertyName);

					propertiesTypeTableDataList.add(initialStateUIPropertyValue
							.getPropertyValue());
				}
			}

		}

	}

	public void editPropertiesNamesForLanguageChosen(String selectedLanguage) {
		ArrayList<InitialStateUIProperty> selectedTypePropertiesList = this.GUIComponent
				.getSelectedTypePropertiesList();
		ArrayList<String> initialStatePropertiesNamesList = new ArrayList<String>();
		String propertyLabel;
		for (InitialStateUIProperty initialStateUIProperty : selectedTypePropertiesList) {

			propertyLabel = getPropertyLabel(initialStateUIProperty,
					selectedLanguage);

			initialStatePropertiesNamesList.add(propertyLabel);

		}

		this.GUIComponent
				.setInitialStatePropertiesNamesList(initialStatePropertiesNamesList);

	}

	private String getPropertyLabel(
			InitialStateUIProperty initialStateUIProperty,
			String selectedLanguage) {
		String propertyName;
		String propertyLabel;
		HashMap<String, String> languagePropertyLabelTextHashMap = initialStateUIProperty
				.getLanguagePropertyLabelTextHashMap();
		propertyName = initialStateUIProperty.getPropertyName();
		if (languagePropertyLabelTextHashMap != null) {

			if (languagePropertyLabelTextHashMap.containsKey(selectedLanguage)) {
				propertyLabel = languagePropertyLabelTextHashMap
						.get(selectedLanguage);
			} else {
				if (languagePropertyLabelTextHashMap
						.containsKey(InitialStateUIProperty.No_Lang_Attr_Given)) {
					propertyLabel = languagePropertyLabelTextHashMap
							.get(InitialStateUIProperty.No_Lang_Attr_Given);
				} else {
					propertyLabel = propertyName;
				}

			}

		} else {
			propertyLabel = propertyName;
		}

		return propertyLabel;
	}

	@Override
	public Object getGUIComponent() {
		// TODO Auto-generated method stub
		return this.GUIComponent;
	}

	/**
	 * @param gUIComponent
	 *            the gUIComponent to set
	 */
	public void setGUIComponent(InitialStateUI gUIComponent) {
		GUIComponent = gUIComponent;
	}

	public void updateInitialStateUIHashMap(String selectedObjectType,
			Long changedInstanceID, String changedPropertyName,
			Object changedPropertyValue) {
		// What to do with Event HashMap Index .............
		InitialStateUIType initialStateUIType = initialStateUIHashMap
				.getTypeStructure(selectedObjectType);
		if (selectedObjectType.equals(CategoryType.Global.name())) {

			InitialStateUIProperty initialStateUIProperty = initialStateUIType
					.getPropertiesInfoHashMap().get(changedPropertyName);
			initialStateUIProperty.setPropertyValue(changedPropertyValue);

		} else {
			InitialStateUIInstance initialStateUIInstance = initialStateUIType
					.getInstancesHashMap().get(changedInstanceID);
			InitialStateUIPropertyValue initialStateUIPropertyValue = initialStateUIInstance
					.getInstancePropertiesValuesHashMap().get(
							changedPropertyName);
			if (initialStateUIPropertyValue != null) {
				initialStateUIPropertyValue
						.setPropertyValue(changedPropertyValue);
			}
			if (changedPropertyName
					.equalsIgnoreCase(PropertyNameConstants.INSTANCE_NAME)) {
				initialStateUIInstance.setName(changedPropertyValue.toString());
			}
		}

	}

	/**
	 * @param initialStateUIEditedInformation
	 *            the initialStateUIEditedInformation to set
	 */
	public void setInitialStateUIEditedInformation(
			InitialStateUIEditedInformation initialStateUIEditedInformation) {
		this.initialStateUIEditedInformation = initialStateUIEditedInformation;
	}

	/**
	 * @return the initialStateUIEditedInformation
	 */
	public InitialStateUIEditedInformation getInitialStateUIEditedInformation() {
		return initialStateUIEditedInformation;
	}

	public void addInitialStateUIEditedInformation(UpdateType updateType,
			String typeName, Long instanceID) {

		if (this.initialStateUIEditedInformation != null) {

			this.initialStateUIEditedInformation.addObjektUpdate(updateType,
					typeName, instanceID);

		} else {
			initializeInitialStateUIEditedInformation();

			this.initialStateUIEditedInformation.addObjektUpdate(updateType,
					typeName, instanceID);

		}

	}

	public void addInitialStateUIEditedInformation(UpdateType updateType,
			String typeName, Long instanceID, String updatedPropertyName) {

		if (this.initialStateUIEditedInformation != null) {

			this.initialStateUIEditedInformation.addObjektUpdate(updateType,
					typeName, instanceID, updatedPropertyName);

		} else {
			initializeInitialStateUIEditedInformation();

			this.initialStateUIEditedInformation.addObjektUpdate(updateType,
					typeName, instanceID, updatedPropertyName);

		}

	}

	public void initializeInitialStateUIEditedInformation() {
		this
				.setInitialStateUIEditedInformation(new InitialStateUIEditedInformation(
						this));

	}

	/**
	 * @return the initialState
	 */
	public InitialState getInitialState() {
		return initialState;
	}

	/**
	 * @param initialState
	 *            the initialState to set
	 */
	public void setInitialState(InitialState initialState) {
		this.initialState = initialState;
	}

	public Object updateValueInActualObjects(Entity changedEntity,
			String changedProperty, Object changedPropertyValue,
			Class<?> propertyClass) {

		String propertyName = changedProperty;

		String setterName = "set" + propertyName.substring(0, 1).toUpperCase()
				+ propertyName.substring(1);

		Method setter = null;

		Class<?> setterClass;
		Object setterInvokingObject;

		if (changedEntity != null)// Case of events ,objects and Agents
		{
			setterClass = changedEntity.getClass();
			setterInvokingObject = changedEntity;

		} else // Case of Globals
		{

			String packageAndClassName = null;

			packageAndClassName = initializePackageAndClassName(
					CategoryType.Global.name(), CategoryType.Global, null);

			setterClass = this.initialState.classForName(packageAndClassName);
			setterInvokingObject = setterClass;
		}

		try {

			setter = setterClass.getMethod(setterName, propertyClass);
		} catch (SecurityException e) {

			e.printStackTrace();
		} catch (NoSuchMethodException e) {

			e.printStackTrace();
		}

		try {
			@SuppressWarnings("unused")
			Object o = setter
					.invoke(setterInvokingObject, changedPropertyValue);
		} catch (IllegalArgumentException e) {

			e.printStackTrace();
		} catch (IllegalAccessException e) {

			e.printStackTrace();
		} catch (InvocationTargetException e) {

			e.printStackTrace();
		}

		return setterInvokingObject;

	}

	public boolean isPresent(Long newInstanceID) {
		InitialStateUIHashMap initialStateUIHashMap = this
				.getInitialStateHashMap();
		for (InitialStateUIType initialStateUIType : initialStateUIHashMap
				.getInitialStateTypeshashMap().values()) {
			for (Long instanceHashMapKey : initialStateUIType
					.getInstancesHashMap().keySet()) {

				InitialStateUIInstance initialStateUIInstance = initialStateUIType
						.getInitialStateUIInstanceStructure(instanceHashMapKey);
				if (initialStateUIInstance.getId().longValue() == newInstanceID
						.longValue()) {
					return true;
				}
			}

		}
		return false;

	}

	public void copyInitialStateUIInstance(Long newInstanceID,
			Long selectedInstanceID, String selectedType) {
		InitialStateUIType initialStateUIType = this.getInitialStateHashMap()
				.getTypeStructure(selectedType);

		InitialStateUIInstance initialStateUIInstance = initialStateUIType
				.getInitialStateUIInstanceStructure(selectedInstanceID);
		InitialStateUIInstance copyInitialStateUIInstance = new InitialStateUIInstance(
				initialStateUIInstance, newInstanceID);

		switch (this.GUIComponent.getSelectedListType()) {
		case AGENT_LIST:
		case OBJECT_LIST: {
			initialStateUIType.addInitialStateUIInstance(newInstanceID,
					copyInitialStateUIInstance);
			this.addInitialStateUIEditedInformation(UpdateType.COPY,
					selectedType, newInstanceID);
			break;
		}
		case EVENT_LIST: {
			Long newInstanceKey = this.getKeyForCreatedEvent();
			initialStateUIType.addInitialStateUIInstance(newInstanceKey,
					copyInitialStateUIInstance);
			this.addInitialStateUIEditedInformation(UpdateType.COPY,
					selectedType, newInstanceKey);

		}
		case GLOBAL_VARIABLE_LIST:
		}

	}

	public void deleteInitialStateUIInstance(Long selectedInstanceID,
			String selectedType) {

		this.addInitialStateUIEditedInformation(UpdateType.DELETE,
				selectedType, selectedInstanceID);
		InitialStateUIType initialStateUIType = this.getInitialStateHashMap()
				.getTypeStructure(selectedType);
		initialStateUIType.getInstancesHashMap().remove(selectedInstanceID);

		if (initialStateUIType.getCategoryType().equals(CategoryType.Event)) {
			updateEventsKeyInHashMap(selectedInstanceID);
		}

	}

	private void updateEventsKeyInHashMap(Long deletedInstanceKey) {

		for (InitialStateUIType initialStateUIType : this.initialStateUIHashMap
				.getInitialStateTypeshashMap().values()) {
			if (initialStateUIType.getCategoryType().equals(CategoryType.Event)) {
				for (Long key : initialStateUIType.getInstancesHashMap()
						.keySet()) {
					if (key > deletedInstanceKey) {
						key--;
					}
				}

			}
		}

	}

	public boolean isObjektCreationValid(String selectedType) {
		InitialStateUIType initialStateUIType = this.getInitialStateHashMap()
				.getTypeStructure(selectedType);
		int nmrOfInstances = initialStateUIType.getNmrOfInstances();
		int curNmrOfInstances = initialStateUIType.getInstancesHashMap().size();

		if ((nmrOfInstances != InitialStateUIType.UNBOUNDED)
				&& (curNmrOfInstances >= nmrOfInstances)) {
			return false;

		}

		return true;
	}

	public boolean updateInitialStateUIHashMap(String selectedType) {
		InitialStateUIType initialStateUIType = this.initialStateUIHashMap
				.getTypeStructure(selectedType);
		HashMap<String, Object> createdInstancePropertiesValues = ((InitialStateUI) this.GUIComponent)
				.getCreateObjektPanel().getCreatedInstancePropertiesValues();
		InitialStateUIInstance initialStateUIInstance = null;
		Object propertyValue;

		Long createdInstanceID = null;
		boolean flagerror = false;

		for (String propertyName : createdInstancePropertiesValues.keySet()) {
			propertyValue = createdInstancePropertiesValues.get(propertyName);
			if (propertyName
					.equalsIgnoreCase(PropertyNameConstants.INSTANCE_ID)) {

				createdInstanceID = (Long) propertyValue;

				switch (this.GUIComponent.getSelectedListType()) {
				case OBJECT_LIST:
				case AGENT_LIST: {
					if (!isPresent(createdInstanceID)) {
						initialStateUIInstance = new InitialStateUIInstance(
								createdInstanceID);

						break;

					} else {
						JOptionPane.showMessageDialog(null,
								MessageBoxConstants.ERROR_ID_PRESENT,
								MessageBoxConstants.TITLE_NEW,
								JOptionPane.ERROR_MESSAGE);
						flagerror = true;

					}

					break;
				}
				case EVENT_LIST: {
					initialStateUIInstance = new InitialStateUIInstance(
							createdInstanceID);

					break;
				}
				case GLOBAL_VARIABLE_LIST:
				}

				if (flagerror == true)
					break;

			}
		}
		if (!flagerror) {
			for (String propertyName : createdInstancePropertiesValues.keySet()) {
				propertyValue = createdInstancePropertiesValues
						.get(propertyName);

				if (propertyName
						.equalsIgnoreCase(PropertyNameConstants.INSTANCE_NAME)) {
					initialStateUIInstance.setName((String) propertyValue);
				} else if (!propertyName
						.equalsIgnoreCase(PropertyNameConstants.INSTANCE_ID)) {

					InitialStateUIPropertyValue initialStateUIPropertyValue = new InitialStateUIPropertyValue();

					initialStateUIPropertyValue
							.setPropertyValue(createdInstancePropertiesValues
									.get(propertyName));

					initialStateUIInstance.getInstancePropertiesValuesHashMap()
							.put(propertyName, initialStateUIPropertyValue);
				}
			}

			switch (this.GUIComponent.getSelectedListType()) {
			case AGENT_LIST:
			case OBJECT_LIST: {
				initialStateUIType.addInitialStateUIInstance(createdInstanceID,
						initialStateUIInstance);
				this.addInitialStateUIEditedInformation(UpdateType.NEW,
						selectedType, createdInstanceID);
				break;
			}
			case EVENT_LIST: {

				Long createdEventHashMapKey = getKeyForCreatedEvent();
				initialStateUIType.addInitialStateUIInstance(
						createdEventHashMapKey, initialStateUIInstance);
				this.addInitialStateUIEditedInformation(UpdateType.NEW,
						selectedType, createdEventHashMapKey);
				break;

			}
			case GLOBAL_VARIABLE_LIST:

			}

		}
		return flagerror;

	}

	public Long getKeyForCreatedEvent() {

		// Get the count of the Already present Event.It means one less
		// than that keys are already occupied,So
		// the count is the key for the created Event

		int curNoOfEvents = getCurEventsSize();

		Long eventID = new Long(curNoOfEvents);

		return eventID;
	}

	private int getCurEventsSize() {
		int curEventsSize = 0;

		for (InitialStateUIType initialStateUIType : this.initialStateUIHashMap
				.getInitialStateTypeshashMap().values()) {
			if (initialStateUIType.getCategoryType().equals(CategoryType.Event)) {
				curEventsSize += initialStateUIType.getInstancesHashMap()
						.size();
			}

		}

		return curEventsSize;
	}

	public void updateObjekt(ObjektUpdate objektUpdate) {

		String typeName = objektUpdate.getTypeName();
		Long instanceID = objektUpdate.getInstanceID();
		UpdateType updateType = objektUpdate.getUpdateType();

		InitialStateUIType initialStateUIType = this.initialStateUIHashMap
				.getTypeStructure(typeName);
		InitialStateUIInstance initialStateUIInstance = null;
		if (initialStateUIType.getInstancesHashMap() != null) { // Null in case
			// of globals

			initialStateUIInstance = initialStateUIType
					.getInitialStateUIInstanceStructure(instanceID);
		}
		CategoryType categoryType = initialStateUIType.getCategoryType();

		switch (updateType) {
		case EDIT: {

			editActualObject(objektUpdate, initialStateUIType,
					initialStateUIInstance, typeName, categoryType, instanceID);

			break;

		}
		case COPY: {
			if (initialStateUIInstance != null) {
				createObjekt(typeName, instanceID, initialStateUIInstance);
			}
			break;
		}

		case NEW: {
			if (initialStateUIInstance != null) {
				createObjekt(typeName, instanceID, initialStateUIInstance);
			}
			break;
		}
		case DELETE: {

			deleteActualObject(instanceID, categoryType);

			break;

		}

		}
	}

	private void deleteActualObject(Long instanceID, CategoryType categoryType) {
		switch (categoryType) {
		case Agent: {
			this.initialState.deleteAORObjektById(instanceID);
			break;
		}
		case Object: {

			this.initialState.deleteAORObjektById(instanceID);
			break;
		}
		case Event: {

			if (instanceID < this.initialState.getEvents().size()) {

				EnvironmentEvent environmentEvent = this.initialState
						.getEvents().get(instanceID.intValue());
				if (environmentEvent != null) {
					this.initialState.deleteEnvironmentEvent(environmentEvent);
				}

			}
			break;
		}

		}

	}

	private void editActualObject(ObjektUpdate objektUpdate,
			InitialStateUIType initialStateUIType,
			InitialStateUIInstance initialStateUIInstance, String typeName,
			CategoryType categoryType, Long instanceID) {
		String updatedPropertyName;
		Entity typeNameEntity = null;

		Object updatedPropertyValue;
		InitialStateUIProperty initialStateUIProperty = null;
		Class<?> propertyClass;
		updatedPropertyName = objektUpdate.getUpdatedPropertyName();
		initialStateUIProperty = initialStateUIType.getPropertiesInfoHashMap()
				.get(updatedPropertyName);
		AgentType agentType = null;// Null in case of Objects and Events

		if (!typeName.equals(CategoryType.Global.name())) {

			if (initialStateUIInstance != null) {

				updatedPropertyValue = getUpdatedPropertyValue(
						initialStateUIInstance, updatedPropertyName);

				propertyClass = getUpdatedPropertyClass(initialStateUIProperty,
						updatedPropertyValue);

				if (initialStateUIProperty != null) {
					// Null in case of Objects and Events
					agentType = initialStateUIProperty.getAgentType();
				} else if (initialStateUIType.getCategoryType().equals(
						CategoryType.Agent)) {
					agentType = AgentType.Objective;
				}

				typeNameEntity = getTypeNameEntity(categoryType, instanceID,
						agentType);

				typeNameEntity = (Entity) updateValueInActualObjects(
						typeNameEntity, updatedPropertyName,
						updatedPropertyValue, propertyClass);
			}
		} else {

			updatedPropertyValue = initialStateUIProperty.getPropertyValue();
			propertyClass = getUpdatedPropertyClass(initialStateUIProperty,
					updatedPropertyValue);
			updateValueInActualObjects(null, updatedPropertyName,
					updatedPropertyValue, propertyClass);

		}

	}

	private Class<?> getUpdatedPropertyClass(
			InitialStateUIProperty initialStateUIProperty,
			Object updatedPropertyValue) {

		Class<?> propertyClass;
		if (initialStateUIProperty != null) {
			propertyClass = initialStateUIProperty.getPropertyClass();
		} else {
			propertyClass = updatedPropertyValue.getClass();
		}

		return propertyClass;
	}

	private Object getUpdatedPropertyValue(
			InitialStateUIInstance initialStateUIInstance,
			String updatedPropertyName) {
		InitialStateUIPropertyValue initialStatePropertyValue;
		Object updatedPropertyValue;

		if (!updatedPropertyName
				.equalsIgnoreCase(PropertyNameConstants.INSTANCE_NAME)) {
			initialStatePropertyValue = initialStateUIInstance
					.getInstancePropertiesValuesHashMap().get(
							updatedPropertyName);
			updatedPropertyValue = initialStatePropertyValue.getPropertyValue();
		} else {
			updatedPropertyValue = initialStateUIInstance.getName();
		}

		return updatedPropertyValue;

	}

	private Entity getTypeNameEntity(CategoryType categoryType,
			Long instanceID, AgentType agentType) {
		Entity typeNameEntity = null;
		switch (categoryType) {
		case Agent: {
			switch (agentType) {
			case Objective: {
				typeNameEntity = this.getInitialState().getObjektById(
						instanceID);
				break;
			}
			case Subjective: {
				typeNameEntity = this.getInitialState().getAgentSubjectById(
						instanceID);
				break;
			}
			}

			break;
		}
		case Object: {
			typeNameEntity = this.getInitialState().getObjektById(instanceID);
			break;
		}
		case Event: {
			typeNameEntity = this.getInitialState().getEvents().get(
					instanceID.intValue());
			break;
		}
		case Global:
		}

		return typeNameEntity;
	}

	private void createObjekt(String typeName, Long instanceID,
			InitialStateUIInstance initialStateUIInstance) {

		InitialStateUIType initialStateUIType = this.initialStateUIHashMap
				.getTypeStructure(typeName);
		CategoryType categoryType = initialStateUIType.getCategoryType();
		String packageAndClassName;
		Entity createdEntity;

		switch (categoryType) {
		case Agent: {
			packageAndClassName = initializePackageAndClassName(typeName,
					categoryType, AgentType.Objective);
			AgentObject agentObject = (AgentObject) createEntity(
					packageAndClassName, initialStateUIInstance,
					initialStateUIType, AgentType.Objective);

			packageAndClassName = initializePackageAndClassName(typeName,
					categoryType, AgentType.Subjective);
			AgentSubject agentSubject = (AgentSubject) createEntity(
					packageAndClassName, initialStateUIInstance,
					initialStateUIType, AgentType.Subjective);
			this.initialState.addAgent(agentObject, agentSubject);
			break;

		}
		case Object: {
			packageAndClassName = initializePackageAndClassName(typeName,
					categoryType, null);
			createdEntity = createEntity(packageAndClassName,
					initialStateUIInstance, initialStateUIType, null); // AgentType
			// Null
			// for
			// Objects
			this.initialState.addObjekt((Objekt) createdEntity);
			break;
		}
		case Event: {
			packageAndClassName = initializePackageAndClassName(typeName,
					categoryType, null);
			createdEntity = createEntity(packageAndClassName,
					initialStateUIInstance, initialStateUIType, null); // AgentType
			// Null
			// for
			// Events
			this.initialState
					.addEnvironmentEvent((EnvironmentEvent) createdEntity);
			break;
		}
		}

	}

	private Entity createEntity(String packageAndClassName,
			InitialStateUIInstance initialStateUIInstance,
			InitialStateUIType initialStateUIType, AgentType agentType) {

		Entity createdEntity;
		Class<?> objektClass = null;
		objektClass = this.initialState.classForName(packageAndClassName);

		Constructor<?>[] objektconstructors = objektClass.getConstructors();
		Constructor<?> createdObjektConstructor = objektconstructors[0];
		int parameterno = createdObjektConstructor.getParameterTypes().length;

		for (Constructor<?> constructor : objektconstructors) {
			if (constructor.getParameterTypes().length > parameterno) {
				createdObjektConstructor = constructor;
				parameterno = createdObjektConstructor.getParameterTypes().length;
			}
		}

		Class<?>[] parameterTypeList = createdObjektConstructor
				.getParameterTypes();
		Object[] params = new Object[parameterno];

		params = initializeParams(initialStateUIInstance, parameterTypeList,
				params);

		createdEntity = instantiateEntity(createdObjektConstructor, params,
				initialStateUIType.getCategoryType(), agentType);

		createdEntity = initializeCreatedEntity(createdEntity,
				initialStateUIInstance, initialStateUIType, agentType);

		return createdEntity;
	}

	private Object[] initializeParams(
			InitialStateUIInstance initialStateUIInstance,
			Class<?>[] parameterTypeList, Object[] params) {
		params[0] = new Long(initialStateUIInstance.getId());
		params[1] = new String(initialStateUIInstance.getName());
		for (int i = 2; i < parameterTypeList.length; i++) {
			Class<?> parameterClass = parameterTypeList[i];
			if (parameterClass.equals(long.class)) {
				params[i] = new Long(ParameterConstants.LONG_DEFAULT);
			} else if (parameterClass.equals(boolean.class)) {
				params[i] = new Boolean(ParameterConstants.BOOLEAN_DEFAULT);
			} else if (parameterClass.equals(String.class)) {
				params[i] = new String(ParameterConstants.STRING_DEFAULT);
			} else if (parameterClass.equals(double.class)) {
				params[i] = new Double(ParameterConstants.DOUBLE_DEFAULT);
			}
		}

		return params;
	}

	private Entity instantiateEntity(Constructor<?> createdObjektConstructor,
			Object[] params, CategoryType categoryType, AgentType agentType) {

		Entity createdEntity = null;
		try {
			switch (categoryType) {
			case Agent: {
				switch (agentType) {
				case Objective: {
					createdEntity = (AgentObject) createdObjektConstructor
							.newInstance(params);
					break;
				}
				case Subjective: {
					createdEntity = (AgentSubject) createdObjektConstructor
							.newInstance(params);
					break;
				}
				}

				break;
			}
			case Object: {
				createdEntity = (Objekt) createdObjektConstructor
						.newInstance(params);
				break;
			}
			case Event:
				createdEntity = (EnvironmentEvent) createdObjektConstructor
						.newInstance(params);
				break;
			case Global: {

			}
			}
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return createdEntity;
	}

	private Entity initializeCreatedEntity(Entity createdEntity,
			InitialStateUIInstance initialStateUIInstance,
			InitialStateUIType initialStateUIType, AgentType agentType) {
		HashMap<String, InitialStateUIPropertyValue> instancePropertiesValuesHashMap = initialStateUIInstance
				.getInstancePropertiesValuesHashMap();
		Object propertyValue;
		InitialStateUIPropertyValue initialStateUIPropertyValue;
		InitialStateUIProperty initialStateUIProperty;
		Class<?> propertyClass;
		for (String propertyName : instancePropertiesValuesHashMap.keySet()) {
			initialStateUIPropertyValue = instancePropertiesValuesHashMap
					.get(propertyName);
			propertyValue = initialStateUIPropertyValue.getPropertyValue();

			initialStateUIProperty = initialStateUIType
					.getPropertiesInfoHashMap().get(propertyName);

			if (initialStateUIType.getCategoryType().equals(CategoryType.Agent)) {
				if (initialStateUIProperty.getAgentType().equals(agentType)) {
					propertyClass = initialStateUIProperty.getPropertyClass();
					createdEntity = (Entity) updateValueInActualObjects(
							createdEntity, propertyName, propertyValue,
							propertyClass);
				}

			} else {
				propertyClass = initialStateUIProperty.getPropertyClass();
				createdEntity = (Entity) updateValueInActualObjects(
						createdEntity, propertyName, propertyValue,
						propertyClass);
			}
		}

		return createdEntity;

	}

	@Override
	public void notifyEvent(ControllerEvent event) {
		// TODO Auto-generated method stub

	}

	/**
	 * @param initializedAtStartup
	 *            the initializedAtStartup to set
	 */
	public void setInitializedAtStartup(boolean initializedAtStartup) {
		this.initializedAtStartup = initializedAtStartup;
	}

	/**
	 * @return the initializedAtStartup
	 */
	public boolean isInitializedAtStartup() {
		return initializedAtStartup;
	}

	public void updateGlobalVariableLabels(String selectedLanguage) {

		InitialStateUIType initialStateUIType = this.initialStateUIHashMap
				.getTypeStructure(CategoryType.Global.name());
		this.getGlobalVariableNames(initialStateUIType, selectedLanguage);

	}

}
