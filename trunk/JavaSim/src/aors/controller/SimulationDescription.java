/*************************************************************************************************************
 * Agent-Object-Relationship (AOR) Simulation
 * Ontologically well-founded and closer-to-reality simulation of complex discrete event scenarios
 *
 * AOR-JSim v.2
 *
 * Copyright (C) 2009 AOR Team: Daniel Draeger, Adrian Giurca, Emilian Pascalau, Andreas Post, Marco Pehla,
 * Gerd Wagner, Jens Werner
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 * File: SimulationDescription.java
 * 
 * Package: aors.controller
 *
 **************************************************************************************************************/
package aors.controller;

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * SimulationDescription
 * 
 * @author Jens Werner
 * @since 22.12.2009
 * @version $Revision$
 */
public class SimulationDescription {

  private Document dom;
  private XPath xpath;
  private DocumentBuilder documentBuilder;

  private String scenarioName = "SimulationScenario";
  /*
   * use this prefix in xPath expressions to search for ER/AOR-Nodes
   */
  public static final String ER_AOR_PREFIX = "er";

  public SimulationDescription() {
    this.initXPath();
    this.initDocumentBuilder();
  }

  public SimulationDescription(Document dom) {
    this.dom = dom;
    this.initXPath();
    this.initDocumentBuilder();
  }

  public SimulationDescription(String simulationScenarioXML) {
    this.initXPath();
    this.initDocumentBuilder();
    this.setDom(simulationScenarioXML);
  }

  private void initXPath() {
    System.setProperty("javax.xml.xpath.XPathFactory:"
        + XPathConstants.DOM_OBJECT_MODEL,
        "net.sf.saxon.xpath.XPathFactoryImpl");

    XPathFactory xpf;
    try {
      xpf = XPathFactory.newInstance(XPathConstants.DOM_OBJECT_MODEL);
      this.xpath = xpf.newXPath();
    } catch (XPathFactoryConfigurationException e) {
      e.printStackTrace();
    }

    this.xpath.setNamespaceContext(new AORLNamespaceContext());
  }

  private void initDocumentBuilder() {
    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
        .newInstance();
    documentBuilderFactory.setNamespaceAware(true);
    try {
      this.documentBuilder = documentBuilderFactory.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
    }
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Get the {@code dom}.
   * 
   * 
   * 
   * @return the {@code dom}.
   */
  public Document getDom() {
    return dom;
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Set the {@code dom}.
   * 
   * 
   * 
   * @param dom
   *          The {@code dom} to set.
   */
  public void setDom(Document dom) {
    this.dom = dom;
  }

  /**
   * Set the dom by using its String representation.
   * 
   * @param domString
   *          the dom string representation
   */
  public void setDom(String domString) {

    if (this.documentBuilder != null) {
      StringReader stringReader = new StringReader(domString);
      InputSource inputSource = new InputSource(stringReader);

      try {
        this.setDom(this.documentBuilder.parse(inputSource));
        this.readAndSetSimulationScenarioName();
      } catch (SAXException e) {
        // System.out.println("WRONG DOM-String: " + domString);
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private void readAndSetSimulationScenarioName() {
    NodeList simulationScenarioNodes = this.dom
        .getElementsByTagName("SimulationScenario");

    if (simulationScenarioNodes.getLength() > 0) {

      NamedNodeMap attMap = simulationScenarioNodes.item(0).getAttributes();
      Node scenarioNameAttr = attMap.getNamedItem("scenarioName");

      if (scenarioNameAttr != null) {
        this.scenarioName = scenarioNameAttr.getNodeValue();
      }
    }
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
   *          - XPath expression to select a node list in a er/aor simulation
   *          description use InitialState.ER_AOR_PREFIX as the namespace-prefix
   * @return
   */
  public NodeList getNodeList(String expression) {

    XPathExpression xPathExpression;
    NodeList resultNodeList = null;
    try {
      xPathExpression = this.xpath.compile(expression);
      resultNodeList = (NodeList) xPathExpression.evaluate(this.dom,
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
   * @param expression
   *          - XPath expression to select a node list in a er/aor simulation
   *          description use InitialState.ER_AOR_PREFIX as the namespace-prefix
   * @param node
   *          - ContextNode
   * @return
   */
  public NodeList getNodeList(String expression, Node node) {

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
   *          - XPath expression to select a node in a er/aor simulation
   *          description use InitialState.ER_AOR_PREFIX as the namespace-prefix
   * @return
   */
  public Node getNode(String expression) {

    XPathExpression xPathExpression;
    Node resultNode = null;
    try {
      xPathExpression = this.xpath.compile(expression);
      resultNode = (Node) xPathExpression.evaluate(this.dom,
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
   *          - XPath expression to select a node in a er/aor simulation
   *          description use InitialState.ER_AOR_PREFIX as the namespace-prefix
   * 
   * @param node
   *          - ContextNode
   * @return
   */
  public Node getNode(String expression, Node node) {

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

  /**
   * 
   * Usage: use the expression to search for a text content in the node
   * 
   * 
   * Comments:
   * 
   * 
   * 
   * @param expression
   *          - XPath expression to select a node in a er/aor simulation
   *          description use InitialState.ER_AOR_PREFIX as the namespace-prefix
   * @param node
   *          - ContextNode
   * @return - text content of a node or an empty string
   */
  public String getNodeContent(String expression, Node node) {

    String result = "";
    try {
      result = xpath.evaluate(expression, node);
    } catch (XPathExpressionException e) {
      e.printStackTrace();
    }
    return result;
  }

  public class AORLNamespaceContext implements NamespaceContext {

    @Override
    public String getNamespaceURI(String prefix) {

      if (prefix.equals(SimulationDescription.ER_AOR_PREFIX)) {
        return "http://aor-simulation.org";
      } else if (prefix.equals("xsi")) {
        return "http://www.w3.org/2001/XMLSchema-instance";
      } else if (prefix.equals(XMLConstants.XML_NS_PREFIX)) {
        return XMLConstants.XML_NS_URI;
      } else if (prefix.equals(XMLConstants.XMLNS_ATTRIBUTE)) {
        return XMLConstants.XMLNS_ATTRIBUTE_NS_URI;
      } else if (prefix.equals("fn")) {
        return "http://www.w3.org/2005/xpath-functions";
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

  /**
   * Usage:
   * 
   * 
   * Comments: Get the {@code scenarioName}.
   * 
   * 
   * 
   * @return the {@code scenarioName}.
   */
  public String getScenarioName() {
    return scenarioName;
  }

}
