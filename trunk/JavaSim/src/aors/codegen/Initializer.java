/**
 * 
 */
package aors.codegen;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;
import javax.xml.xpath.XPathVariableResolver;

import net.sf.saxon.om.NamespaceConstant;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import aors.controller.AbstractSimulator;
import aors.model.envsim.AgentObject;
import aors.model.envsim.Objekt;
import aors.model.envsim.PhysicalAgentObject;
import aors.model.envsim.PhysicalObject;

/**
 * @author Jens Werner
 * @since April 17, 2009
 * @version $Revision$
 * 
 */
public class Initializer implements InitializerKonstants {

  /*
   * 
   */
  private AbstractSimulator simulator;

  private URLClassLoader urClassLoader;

  private XPath xpath;
  private Document document;
  private final String aor_ns = "aorsml:";
  private final String output_language = "Java";

  /**
   * 
   * Create a new {@code Initializer}.
   * 
   * @param simulator
   */
  public Initializer(AbstractSimulator simulator) {

    System.setProperty("javax.xml.xpath.XPathFactory:"
        + XPathConstants.DOM_OBJECT_MODEL,
        "net.sf.saxon.xpath.XPathFactoryImpl");

    XPathFactory xpf;
    try {
      xpf = XPathFactory.newInstance(XPathConstants.DOM_OBJECT_MODEL);
      this.xpath = xpf.newXPath();
      System.out.println("Loaded XPath Provider "
          + this.xpath.getClass().getName());
    } catch (XPathFactoryConfigurationException e) {
      e.printStackTrace();
    }

    this.simulator = simulator;
    this.xpath.setNamespaceContext(new AORLNamespaceContext());
    this.xpath.setXPathVariableResolver(new AORVariableResolver());
  }

  /**
   * 
   * Usage:
   * 
   * 
   * Comments:
   * 
   * 
   * 
   * @param document
   *          - the dom-document that contains the SimulationModel and the
   *          InitialState (complete SimulationScenario)
   * @param urlClassLoader
   *          - the used URLClassLoader for the created classes
   */
  public void createEnvironment(Document document, URLClassLoader urlClassLoader) {

    if (document != null && urlClassLoader != null) {

      this.document = document;
      this.urClassLoader = urlClassLoader;

      // get all simple objekt, agent, physicalObjekt and physicalAgent
      // TODO: implement Objects, Agents, ...
      String expression = "//" + aor_ns
          + InitializerNodeNames.InitialState.name() + "/" + aor_ns
          + InitializerNodeNames.Object.name() + " | ";
      expression += "//" + aor_ns + InitializerNodeNames.InitialState.name()
          + "/" + aor_ns + InitializerNodeNames.Agent.name() + " | ";
      expression += "//" + aor_ns + InitializerNodeNames.InitialState.name()
          + "/" + aor_ns + InitializerNodeNames.PhysicalObject.name() + " | ";
      expression += "//" + aor_ns + InitializerNodeNames.InitialState.name()
          + "/" + aor_ns + InitializerNodeNames.PhysicalAgent.name();

      NodeList nodeSet = this.getNodeList(expression);
      this.initSimulation(nodeSet);

    } else {
      System.err.println("[ERROR] - No Description loaded!");
    }
  }

  /**
   * 
   * Usage: Initialize the simulation with a given list of objekts
   * 
   * 
   * Comments:
   * 
   * 
   * 
   * @param nodeList
   *          - a list with all Objects (Object, Agent, PhysicalObject and
   *          PhysicalAgent) from InitialState
   */
  private void initSimulation(NodeList nodeList) {

    for (int i = 0; i < nodeList.getLength(); i++) {
      Element nodeE = (Element) nodeList.item(i);

      Object o = this.initObjekt(nodeE);

      // Object
      if (nodeE.getNodeName().equals(InitializerNodeNames.Object.name())) {
        this.simulator.getEnvironmentSimulator().addObjekt((Objekt) o);

        // Agent
      } else if (nodeE.getNodeName().equals(InitializerNodeNames.Agent.name())) {
        this.simulator.getEnvironmentSimulator().addAgent((AgentObject) o);

        // PhysicalObject
      } else if (nodeE.getNodeName().equals(
          InitializerNodeNames.PhysicalObject.name())) {
        this.simulator.getEnvironmentSimulator().addPhysicalObjekt(
            (PhysicalObject) o);

        // PhysicalAgent
      } else if (nodeE.getNodeName().equals(
          InitializerNodeNames.PhysicalAgent.name())) {
        this.simulator.getEnvironmentSimulator().addPhysicalAgent(
            (PhysicalAgentObject) o);
      }
    }
  }

  /**
   * 
   * Usage: initialize an Objekt
   * 
   * 
   * Comments:
   * 
   * 
   * 
   * @param objE
   *          - an childnode from Initialize
   */
  private Object initObjekt(Element objE) {

    Object o = null;

    // get the corresponded Type of on Objekt from the SimModel
    String aorType = objE.getAttribute(InitializerNodeNames.type.name());
    String expression = "/" + aor_ns
        + InitializerNodeNames.SimulationScenario.name() + "/" + aor_ns
        + InitializerNodeNames.SimulationModel.name() + "/" + aor_ns
        + InitializerNodeNames.EntityTypes + "/" + aor_ns + "*[@name = '"
        + aorType + "']";
    Element typeE = (Element) this.getNode(expression);

    if (typeE != null) {

      ArrayList<Node> allProperties = this.getAllProperties(typeE,
          InitializerNodeNames.EntityTypes);
      Constructor<?> constructor = this.createObjektConstructor(aorType,
          allProperties, SIM_ENVIRONMENT_PACKAGE);

      // create the arguments for the constructor
      Object[] initObjects;
      if (constructor != null) {
        initObjects = this.getInitObjects(objE, typeE, allProperties);

        try {
          o = constructor.newInstance(initObjects);
        } catch (IllegalArgumentException e) {
          e.printStackTrace();
        } catch (InstantiationException e) {
          e.printStackTrace();
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        } catch (InvocationTargetException e) {
          e.printStackTrace();
        }

      } else {
        System.err.println("[ERROR] Can't create constructor for "
            + objE.getNodeName() + " "
            + objE.getAttribute(InitializerNodeNames.type.name()));
      }

    } else {
      System.err.println("[ERROR] No type " + aorType + " found for "
          + objE.getNodeName());
    }
    return o;
  }

  /**
   * 
   * Usage: create an Object-Array
   * 
   * 
   * Comments: we use her the properties again from an earlier xpath-request
   * 
   * 
   * 
   * @param objE
   *          - AORObjekt to init
   * @param typeE
   *          - AORObjektType of the objE
   * @param allProperties
   *          - all properties from AORObjektType
   * @return
   */
  private Object[] getInitObjects(Element objE, Element typeE,
      ArrayList<Node> allProperties) {
    Object[] initObjects = new Object[allProperties.size() + 1];
    // set the id
    initObjects[0] = Long.valueOf(objE.getAttribute("id"));

    int position = 1;
    for (Node properyN : allProperties) {

      Element propertyE = (Element) properyN;
      Object o = null;

      // attributes
      if (propertyE.getNodeName().equals(InitializerNodeNames.Attribute.name())) {
        o = this.getInitialAttributeValue(propertyE, objE, typeE);

        // complexdataproperty
      } else if (propertyE.getNodeName().equals(
          InitializerNodeNames.ComplexDataProperty.name())) {
        o = this.getInitialComplexDataValue(propertyE, objE, typeE);

      } else if (propertyE.getNodeName().equals(
          InitializerNodeNames.EnumerationProperty.name())) {
        o = this.getInitialEnumerationPropertyValue(propertyE, objE, typeE);

      } else if (propertyE.getNodeName().equals(
          InitializerNodeNames.ReferenceProperty.name())) {
        // TODO: create getInitialReferencePropertyValue()
      }

      initObjects[position] = o;
      position++;

    }

    return initObjects;
  }

  /**
   * 
   * Usage:
   * 
   * 
   * Comments:
   * 
   * 
   * 
   * @param propertyE
   *          - PropertyChild of aorsml:Object
   * @param objE
   *          - Child of aorsml:InitialState
   * @param typeE
   *          - the corresponded aorsml:ObjectType to objE
   * @return a value for this property if it exist or null
   */
  private Object getInitialAttributeValue(Element propertyE, Element objE,
      Element typeE) {

    Object result = null;
    String propertyName = propertyE.getAttribute("name");
    String type = propertyE.getAttribute("type");

    // try a Slot
    Element slotE = this.getSlot(propertyName, objE);
    if (slotE != null) {
      return this.castObject2AttributeType(type, this.getSlotValue(slotE));
    }

    // try InitialAttributeValue
    String expression = "/" + aor_ns
        + InitializerNodeNames.InitialAttributeValue.name() + "[@attribute = '"
        + propertyName + "']";
    Element initialAttributeValueE = (Element) this.getNode(expression, typeE);
    if (initialAttributeValueE != null) {
      return this.castObject2AttributeType(type, initialAttributeValueE
          .getAttribute("value"));
    }

    // try @initialValue
    if (!typeE.getAttribute("initialValue").equals(""))
      return this.castObject2AttributeType(type, typeE
          .getAttribute("initialValue"));

    return result;
  }

  /**
   * 
   * Usage:
   * 
   * 
   * Comments: be sure, that the slots contains the right format (json in curly
   * brackets) Example: <Slot xsi:type="aors:SimpleSlot" property="inputs"
   * value="{itemType:1002,quantityPerRound:100} {itemType:1003,quantityPerRound:200} {itemType:1004,quantityPerRound:2000}"
   * />
   * 
   * 
   * 
   * @param propertyE
   *          - Property Element from objE
   * @param objE
   *          - AORObjekt to init
   * @param typeE
   *          - AORObjektType from objE
   * @return
   */
  private Object getInitialComplexDataValue(Element propertyE, Element objE,
      Element typeE) {

    Object result = null;
    String propertyName = propertyE.getAttribute("name");
    String aorComplexDataType = propertyE.getAttribute("type");

    // get the ComplexDataType
    String expression = "/" + aor_ns
        + InitializerNodeNames.SimulationScenario.name() + "/" + aor_ns
        + InitializerNodeNames.SimulationModel.name() + "/" + aor_ns
        + InitializerNodeNames.DataTypes.name() + "/" + aor_ns
        + InitializerNodeNames.ComplexDataType.name() + "[@name eq '"
        + aorComplexDataType + "']";
    Element complexDataTypeE = (Element) this.getNode(expression);
    if (complexDataTypeE == null) {
      System.err.println("[ERROR ]No ComplexDataType " + propertyName
          + " exist!");
      return null;
    }

    Element slotE = this.getSlot(propertyName, objE);
    if (slotE != null) {

      // all properties of a complexDataType
      ArrayList<Node> allComplexDataProperties = this.getAllProperties(
          complexDataTypeE, InitializerNodeNames.DataTypes);
      Constructor<?> constructor = this.createObjektConstructor(
          aorComplexDataType, allComplexDataProperties, SIM_DATATYPES_PACKAGE);

      // create the arguments for the constructor
      Object[] initObjects = new Object[allComplexDataProperties.size()];
      int counter = 0;
      if (constructor != null) {

        String slotValue = this.getSlotValue(slotE).toString();
        StringTokenizer tokenizer = new StringTokenizer(slotValue);
        JSONParser parser = new JSONParser();
        JSONObject jo = null;

        while (tokenizer.hasMoreTokens()) {
          String jsonToken = tokenizer.nextToken();
          // TODO: found a regex to check for the right json-format
          // -> {"item1":value1,"item2":value2} or
          // {'item1':value1,'item2':value2}
          jsonToken = jsonToken.replace("'", "\"");
          try {
            jo = (JSONObject) parser.parse(jsonToken);
            System.out.println(jo.toJSONString());
            if (jo != null) {
              // jo contains now the JSON-Object

              for (Node complexDataPropertyN : allComplexDataProperties) {
                Element complexDataPropertyE = (Element) complexDataPropertyN;
                String cdPropertyName = complexDataPropertyE
                    .getAttribute("name");
                initObjects[counter++] = jo.get(cdPropertyName);

              }
            }
          } catch (ParseException e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
            System.err.println("[ERROR] can't parse String [ " + jsonToken
                + " ] to an JSON-Object");
          }

        }

        try {
          result = constructor.newInstance(initObjects);
        } catch (IllegalArgumentException e) {
          e.printStackTrace();
        } catch (InstantiationException e) {
          e.printStackTrace();
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        } catch (InvocationTargetException e) {
          e.printStackTrace();
        }

      } else {
        System.err.println("[ERROR] Can't create constructor for "
            + objE.getNodeName() + " "
            + objE.getAttribute(InitializerNodeNames.type.name()));
      }
    }

    return result;
  }

  /**
   * 
   * Usage:
   * 
   * 
   * Comments:
   * 
   * 
   * 
   * @param propertyE
   * @param objE
   * @param typeE
   * @return
   */
  private Object getInitialEnumerationPropertyValue(Element propertyE,
      Element objE, Element typeE) {
    Object result = null;
    String propertyName = propertyE.getAttribute("name");
    String type = propertyE.getAttribute("type");

    // get the Enumeration
    String expression = "/" + aor_ns
        + InitializerNodeNames.SimulationScenario.name() + "/" + aor_ns
        + InitializerNodeNames.SimulationModel.name() + "/" + aor_ns
        + InitializerNodeNames.DataTypes.name() + "/" + aor_ns
        + InitializerNodeNames.Enumeration.name() + "[@name eq '" + type + "']";
    Element enumerationE = (Element) this.getNode(expression);
    if (enumerationE == null) {
      System.err.println("[ERROR ]No Enumeration " + propertyName + " exist!");
      return null;
    }

    Element slotE = this.getSlot(propertyName, objE);
    expression = aor_ns + InitializerNodeNames.EnumerationLiteral.name();
    NodeList enumLiteralL = this.getNodeList(expression, enumerationE);
    if (slotE != null && enumLiteralL.getLength() > 0) {
      String slotValue = this.getSlotValue(slotE).toString();

      // regex check the format
      if (slotValue.matches("\\w*\\.\\w*")) {

        // check if such a EnumLiteral exists
        boolean enumCheck = false;
        String enumName = slotValue.split("\\.")[0];
        String enumLiteral = slotValue.split("\\.")[1];

        for (int i = 0; i < enumLiteralL.getLength(); i++) {
          if (enumLiteralL.item(i).getTextContent().equals(enumLiteral)) {
            enumCheck = true;
            break;
          }
        }

        Class<?> clazz = null;
        try {
          clazz = Class.forName(SIM_DATATYPES_PACKAGE + enumName, false,
              this.urClassLoader);
          if (!clazz.isEnum())
            enumCheck = false;
        } catch (ClassNotFoundException e) {
          enumCheck = false;
          e.printStackTrace();
        }

        // enumCheck=true means the Enum-ClassFile exists and is loaded and it
        // should exist the
        // Literals (if the codecreation works fine)
        if (enumCheck) {

          for (Object o : clazz.getEnumConstants()) {

            if (o.toString().equals(enumLiteral)) {
              result = (Enum<?>) o;
              break;
            }

          }

        } else {
          System.err
              .println("[ERROR] Unknown Enumeration or EnumLiteral in Slot in TypeInit");
        }
      } else {
        System.err.println("[ERROR] Wrong Enum-Value-Format in Slot! "
            + "Use [Enumeration/@name].[EnumerationLiteral/text()]");
      }

    }
    // System.out.println("Result: " + result + " - " +
    // result.getClass().getSimpleName() + " " + result.getClass().isEnum());
    return result;
  }

  private Element getSlot(String property, Element objE) {
    String expression = aor_ns + "Slot[@property = '" + property + "']";
    return (Element) this.getNode(expression, objE);
  }

  private Object getSlotValue(Element slotE) {
    Object result = null;

    try {
      if ((Boolean) this.xpath
          .evaluate(
              "resolve-QName(@xsi:type, .) eq QName('http://aor-simulation.org', 'OpaqueExprSlot')",
              slotE, XPathConstants.BOOLEAN)) {
        result = this.getNode(
            aor_ns + "ValueExpr[@language = $output.language]", slotE)
            .getTextContent();
      } else {
        result = slotE.getAttribute("value");
      }
    } catch (XPathExpressionException e) {
      e.printStackTrace();
    }
    return result;
  }

  /**
   * 
   * Usage: get all properties inclusive properties from superType
   * 
   * 
   * Comments:
   * 
   * 
   * 
   * @param typeE
   * @param iName
   * @return
   */
  private ArrayList<Node> getAllProperties(Element typeE,
      InitializerNodeNames iName) {
    ArrayList<Node> result = new ArrayList<Node>();

    if (!typeE.getAttribute("superType").equals("")) {

      String expression = "/" + aor_ns
          + InitializerNodeNames.SimulationScenario.name() + "/" + aor_ns
          + InitializerNodeNames.SimulationModel.name() + "/" + aor_ns
          + iName.name() + "/" + aor_ns + typeE.getNodeName() + "[@name = '"
          + typeE.getAttribute("superType") + "']";
      Element superTypeE = (Element) this.getNode(expression);
      if (superTypeE != null) {
        result.addAll(this.getAllProperties(superTypeE, iName));
      } else {
        System.err.println("[ERROR] no superType "
            + typeE.getAttribute("superType") + " found!");
      }

    } else {

      String expression2 = aor_ns + InitializerNodeNames.Attribute.name()
          + " | " + aor_ns + InitializerNodeNames.ComplexDataProperty.name()
          + " | " + aor_ns + InitializerNodeNames.EnumerationProperty.name()
          + " | " + aor_ns + InitializerNodeNames.ReferenceProperty.name();
      NodeList propertyL = this.getNodeList(expression2, typeE);
      for (int i = 0; i < propertyL.getLength(); i++) {
        result.add(propertyL.item(i));
      }

    }
    return result;
  }

  /**
   * 
   * Usage: instantiate a type from initialstate
   * 
   * 
   * Comments:
   * 
   * 
   * 
   * @param aorType
   * @param propertyList
   */
  private Constructor<?> createObjektConstructor(String aorType,
      ArrayList<Node> propertyList, String simPackage) {

    try {
      Class<?> clazz = Class.forName(simPackage + aorType, false,
          this.urClassLoader);
      if (simPackage.equals(SIM_ENVIRONMENT_PACKAGE)) {
        return this.createAORObjektConstructor(clazz, propertyList);
      } else if (simPackage.equals(SIM_DATATYPES_PACKAGE)) {
        return this.createAORComplexDataConstructor(clazz, propertyList);
      }

    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (SecurityException e) {
      e.printStackTrace();
    }
    return null;

  }

  /**
   * 
   * Usage:
   * 
   * 
   * Comments:
   * 
   * 
   * 
   * @param clazz
   *          - the aor-object class
   * @param propertyList
   *          - a list with all properties
   * @return
   */
  private Constructor<?> createAORObjektConstructor(Class<?> clazz,
      ArrayList<Node> propertyList) {

    Constructor<?> constructor = null;
    Class<?>[] parameterTypes = new Class[propertyList.size() + 1];

    // the id
    parameterTypes[0] = Long.TYPE;

    for (int i = 1; i <= propertyList.size(); i++) {
      Element propertyE = (Element) propertyList.get(i - 1);
      parameterTypes[i] = this.getPropertyType(propertyE);
    }

    try {
      constructor = clazz.getConstructor(parameterTypes);
    } catch (SecurityException e) {
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    }
    return constructor;
  }

  private Constructor<?> createAORComplexDataConstructor(Class<?> clazz,
      ArrayList<Node> propertyList) {

    Constructor<?> constructor = null;
    Class<?>[] parameterTypes = new Class[propertyList.size()];

    for (int i = 0; i < propertyList.size(); i++) {
      Element propertyE = (Element) propertyList.get(i);
      parameterTypes[i] = this.getPropertyType(propertyE);
    }

    try {
      constructor = clazz.getConstructor(parameterTypes);
    } catch (SecurityException e) {
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    }
    return constructor;
  }

  /**
   * 
   * Usage: Get the Class of the property
   * 
   * 
   * Comments:
   * 
   * 
   * 
   * @param propertyE
   * @return
   */
  private Class<?> getPropertyType(Element propertyE) {

    Class<?> result = null;
    String nodeName = propertyE.getNodeName();
    if (nodeName.equals(InitializerNodeNames.Attribute.name())) {
      return this.getAttributeType(propertyE);
    } else if (nodeName.equals(InitializerNodeNames.ReferenceProperty.name())) {
      return this.getReferencePropertyType(propertyE);
    } else if (nodeName.equals(InitializerNodeNames.ComplexDataProperty.name())
        || nodeName.equals(InitializerNodeNames.EnumerationProperty.name())) {
      return this.getDataPropertyType(propertyE);
    }

    return result;
  }

  /**
   * 
   * Usage:
   * 
   * 
   * Comments: mapped the attribute-types from Attribute/@type to a java-type
   * 
   * @see function mappeDataType in java.xsl
   * 
   * 
   * 
   * @param type
   * @param o
   * @return
   */
  private Object castObject2AttributeType(String type, Object o) {

    if (type.equals("Float")) {
      return Double.valueOf((String) o);
    } else if (type.equals("Boolean")) {
      return Boolean.valueOf((String) o);
    } else if (type.equals("Integer")) {
      return Long.valueOf((String) o);
    } else if (type.equals("String")) {
      return (String) o;
    } else {
      System.err.println("[ERROR] - unknown AttributeType!");
    }
    return null;
  }

  /**
   * 
   * Usage:
   * 
   * 
   * Comments: returns the corresponding Class of the @type of the Attribute
   * Boolean means Boolean.TYPE, Float means Double.TYPE, Integer means
   * Long.Type and String means String.class
   * 
   * 
   * 
   * @param propertyE
   * @return the Class for the type of the Attribute
   */
  private Class<?> getAttributeType(Element propertyE) {

    String attributeType = propertyE.getAttribute(InitializerNodeNames.type
        .name());
    if (attributeType.equals("Boolean")) {
      return Boolean.TYPE;
    } else if (attributeType.equals("Float")) {
      return Double.TYPE;
    } else if (attributeType.equals("Integer")) {
      return Long.TYPE;
    } else if (attributeType.equals("String")) {
      return String.class;
    }
    System.err.println("[ERROR] - Unknown AttributType " + attributeType);
    return null;
  }

  /**
   * 
   * Usage:
   * 
   * 
   * Comments:
   * 
   * 
   * 
   * @param propertyE
   * @return the Class for the type of the ReferenceProperty
   */
  private Class<?> getReferencePropertyType(Element propertyE) {

    String referencePropertyType = propertyE
        .getAttribute(InitializerNodeNames.type.name());

    Class<?> clazz;
    try {
      clazz = Class.forName(SIM_ENVIRONMENT_PACKAGE + referencePropertyType,
          false, this.urClassLoader);
    } catch (ClassNotFoundException e) {
      System.err.println("[ERROR] - Unknown ReferencePropertyType "
          + referencePropertyType);
      // e.printStackTrace();
      return null;
    }
    return clazz;
  }

  /**
   * 
   * Usage: returns the corresponding Class of the @type of an
   * EnumerationProperty or an ComplexDataProperty
   * 
   * 
   * Comments:
   * 
   * 
   * 
   * @param propertyE
   * @return
   */
  private Class<?> getDataPropertyType(Element propertyE) {

    String complexDataPropertyType = propertyE
        .getAttribute(InitializerNodeNames.type.name());
    Class<?> clazz;
    try {
      clazz = Class.forName(SIM_DATATYPES_PACKAGE + complexDataPropertyType,
          false, this.urClassLoader);
    } catch (ClassNotFoundException e) {
      System.err.println("[ERROR] - Unknown DataPropertyType "
          + complexDataPropertyType);
      // e.printStackTrace();
      return null;
    }
    return clazz;
  }

  /**
   * 
   * Usage: use the expression to search for a nodeList in the document
   * 
   * 
   * Comments:
   * 
   * 
   * 
   * @param expression
   * @return
   */
  private NodeList getNodeList(String expression) {

    XPathExpression xPathExpression;
    NodeList resultNodeList = null;
    try {
      xPathExpression = this.xpath.compile(expression);
      resultNodeList = (NodeList) xPathExpression.evaluate(this.document,
          XPathConstants.NODESET);
    } catch (XPathExpressionException e) {
      e.printStackTrace();
      return null;
    }
    return resultNodeList;
  }

  /**
   * 
   * Usage: use the expression to search for a nodeList in the node
   * 
   * 
   * Comments:
   * 
   * 
   * 
   * @param expression
   *          - XPath-expression
   * @param node
   *          - ContextNode
   * @return
   */
  private NodeList getNodeList(String expression, Node node) {

    XPathExpression xPathExpression;
    NodeList resultNodeList = null;
    try {
      xPathExpression = this.xpath.compile(expression);
      resultNodeList = (NodeList) xPathExpression.evaluate(node,
          XPathConstants.NODESET);
    } catch (XPathExpressionException e) {
      e.printStackTrace();
      return null;
    }
    return resultNodeList;
  }

  /**
   * 
   * Usage: use the expression to search for a node in the document
   * 
   * 
   * Comments: If the XPath expression does return more than one node returns
   * the first node in document order
   * 
   * 
   * @param expression
   * @return
   */
  private Node getNode(String expression) {

    XPathExpression xPathExpression;
    Node resultNode = null;
    try {
      xPathExpression = this.xpath.compile(expression);
      resultNode = (Node) xPathExpression.evaluate(this.document,
          XPathConstants.NODE);
    } catch (XPathExpressionException e) {
      e.printStackTrace();
      return null;
    }
    return resultNode;
  }

  /**
   * 
   * Usage: use the expression to search for a node in the node
   * 
   * 
   * Comments: If the XPath expression does return more than one node returns
   * the first node in document order
   * 
   * 
   * @param expression
   * @return
   */
  private Node getNode(String expression, Node node) {

    XPathExpression xPathExpression;
    Node resultNode = null;
    try {
      xPathExpression = this.xpath.compile(expression);
      resultNode = (Node) xPathExpression.evaluate(node, XPathConstants.NODE);
    } catch (XPathExpressionException e) {
      e.printStackTrace();
      return null;
    }
    return resultNode;
  }

  public class AORLNamespaceContext implements NamespaceContext {

    @Override
    public String getNamespaceURI(String prefix) {

      if (prefix.equals("aorsml")) {
        return "http://aor-simulation.org";
      } else if (prefix.equals("xsi")) {
        return "http://www.w3.org/2001/XMLSchema-instance";
      } else if (prefix.equals(XMLConstants.XML_NS_PREFIX)) {
        return XMLConstants.XML_NS_URI;
      } else if (prefix.equals(XMLConstants.XMLNS_ATTRIBUTE)) {
        return XMLConstants.XMLNS_ATTRIBUTE_NS_URI;
      } else if (prefix.equals("fn")) {
        return NamespaceConstant.FN;
      } else {
        return XMLConstants.NULL_NS_URI;
      }
    }

    @Override
    public String getPrefix(String namespace) {
      throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterator getPrefixes(String namespaceURI) {
      throw new UnsupportedOperationException();
    }
  }

  public class AORVariableResolver implements XPathVariableResolver {

    @Override
    public Object resolveVariable(QName variableName) {

      if (variableName == null)
        throw new NullPointerException("The variable name cannot be null");

      if (variableName.equals(new QName("output.language"))) {
        return new String(output_language);
      } else {
        return null;
      }
    }
  }

}
