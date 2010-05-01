package aors.util;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * XMLLoader
 * 
 * @author Andreas Freier (business.af@web.de), Mircea Diaconescu
 * @date February 7, 2009
 * @version $Revision$
 */
public class XMLLoader {

  private static Document document = null;
  private static DocumentBuilder builder = null;
  private static DocumentBuilderFactory factory = null;

  /**
   * Transform, if possible, a string representation of an XML to a Document
   * (DOM) object.
   * 
   * @param stringXML
   *          the string representation of the XML data
   * @return the DOM (Document) representation of the XML data
   */
  public static Document transformStringToDom(String stringXML) {
    Document result = null;

    StringReader stringReader = new StringReader(stringXML);
    InputSource inputSource = new InputSource(stringReader);
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);

    try {
      result = factory.newDocumentBuilder().parse(inputSource);
    } catch (SAXException e) {
      System.out
          .println("The simulation scenario does not pass XML validation!");
      // e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
    }

    return result;
  }

  /**
   * Load a the DOM. Yes, this is a setter with another name for keeping the old
   * naming of these methods.
   * 
   * @param domDocument
   */
  public static void loadXML(Document domDocument) {
    document = domDocument;
  }

  /**
   * Load a DOM from an InputSource
   * 
   * @param xmlSource
   *          the input source
   */
  public static void loadXML(InputSource xmlSource) {

    factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);

    try {
      builder = factory.newDocumentBuilder();
      document = builder.parse(xmlSource);
    } catch (ParserConfigurationException e) {
      System.out
          .println("DOM ParserConfigurationException trying to load XML !");
    } catch (SAXException e) {
      System.out.println("DOM SAXException trying to load XML !");
    } catch (IOException e) {
      System.out.println("DOM IOException trying to load XML !");
    }

  }

  public static NodeList getNodeList(String nodeName) {
    if (document != null) {
      return document.getElementsByTagNameNS("http://aor-simulation.org",
          nodeName);
    } else {
    }
    return null;
  }

  public static Document getDescriptionDocument() {
    if (document != null)
      return document;
    return null;
  }

  public static DocumentBuilder getDocBuilder() {
    return builder;
  }

}
