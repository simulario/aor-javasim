package aors.codegen;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.Controller;
import net.sf.saxon.event.MessageEmitter;
import net.sf.saxon.trans.XPathException;

/**
 * This class is responsible for the transformations of XML via XSLT. This
 * implementation has been taken from my implementation of the web simulator.
 * 
 * XSLTProcessor
 * 
 * @author Marco Pehla
 * @since 30.07.2008
 * @version $Revision$
 */
public class XSLT2Processor {

  // to store <xsl:param/> 's
  private HashMap<String, String> parameter;
  private StringWriter messageWriter;
  private MessageEmitter messageEmitter;
  private List<String> messages;

  public final static String OUTPUT_URI_RESOLVER = "http://saxon.sf.net/feature/outputURIResolver";
  public final static String ALLOW_EXTERNAL_FUNCTIONS = "http://saxon.sf.net/feature/allow-external-functions";
  public final static String INDENT_AMOUNT = "{http://xml.apache.org/xslt}indent-amount";

  public XSLT2Processor() {
    this.parameter = new HashMap<String, String>();
    this.messageWriter = new StringWriter();
    this.messageEmitter = new MessageEmitter();
    this.messages = new ArrayList<String>();
  }

  public String validateXML(URI xmlSchema) {
    // TODO: the validation of XML against an XML Schema
    return "";
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
   * @param xml
   * @param xslt
   * @param parameter
   * @return
   */
  public HashMap<String, String> transformFromURL(String xml, URI xslt,
      HashMap<String, String> parameter) {
    // keep the parameter settings
    this.parameter = parameter;

    // clean up before transforming
    this.messageWriter = new StringWriter();
    this.messageEmitter = new MessageEmitter();

    // used for the method return, fulfils the NullObject pattern
    HashMap<String, String> result = new HashMap<String, String>();

    try {
      // Create a transform factory instance.
      TransformerFactory transformerFactory = TransformerFactory.newInstance();

      // set the output resolver, that writes the content of the XSLT's
      // <xsl:result-document href="test.out"> attribute into the result
      // HashMap, where the key is the file name and the content is the
      // content build with the help of the XSLT
      //
      // The extension can be set using an HashMap
      // e.g.: xsltParameter.put("fileExtension","out");
      transformerFactory.setAttribute(OUTPUT_URI_RESOLVER,
          new MemoryOutputURIResolver());

      // allow external XSLT functions
      transformerFactory.setAttribute(ALLOW_EXTERNAL_FUNCTIONS, Boolean.TRUE);

      // Create a transformer for the style sheet.
      Transformer transformer = transformerFactory
          .newTransformer(new StreamSource(new File(xslt)));

      // set <xsl:param/> if necessary
      if (!this.parameter.isEmpty()) { // if HashMap is not empty
        String key = "";
        String value = "";
        // for all parameters
        for (Iterator<String> iterator = this.parameter.keySet().iterator(); iterator
            .hasNext();) {
          key = iterator.next();// get parameter name
          value = this.parameter.get(key);// get its value
          transformer.setParameter(key, value);// set it to the XSLT processor
        }// for
      }// if

      // Transform the source XML to String
      StringReader xmlStringReader = new StringReader(xml);
      StringWriter outputStringWriter = new StringWriter();

      // MessageEmitter
      this.messageEmitter.setWriter(this.messageWriter);
      Controller controller = (Controller) transformer;
      controller.setMessageEmitter(this.messageEmitter);

      transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
//      transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
      transformer.setOutputProperty(OutputKeys.METHOD, "xml");
      transformer.setOutputProperty(INDENT_AMOUNT, "4");
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");

      // transform XML with XSLT
      transformer.transform(new StreamSource(xmlStringReader),
          new StreamResult(outputStringWriter));

      MemoryOutputURIResolver memoryOutputURIResolver = (MemoryOutputURIResolver) controller
          .getOutputURIResolver();
      result = memoryOutputURIResolver.getResultHashMap();

      // if xsl:result-document is not used in the XSLT 2.0 style sheet,
      // which means just one single file is being created in memory
      // the file name (key in the HashMap) is then "output"
      if (result.isEmpty()) {
        result.put("output", outputStringWriter.toString());
      }

      xmlStringReader.close();

      try {
        outputStringWriter.close(); // close StringWriter stream
      } catch (IOException e) {
        e.printStackTrace();
      }// try-catch

    } catch (javax.xml.transform.TransformerException te) {
      this.messages.add(te.getMessageAndLocation());
    }// try-catch

    // if we get a xsl:message
    if (!messageWriter.toString().equals("")) {
      this.messages.add(messageWriter.toString());
    }// if

    try {
      messageWriter.close(); // close the StringWriter for messages
    } catch (IOException e) {
      e.printStackTrace();
    }// try-catch

    try {
      messageEmitter.close();
    } catch (XPathException e) {
      e.printStackTrace();
    }// try-catch

    return result;
  }

  public List<String> getMessages() {
    return this.messages;
  }

}
