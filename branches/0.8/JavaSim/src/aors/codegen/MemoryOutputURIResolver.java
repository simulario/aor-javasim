package aors.codegen;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;

import javax.xml.transform.Result;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;

import net.sf.saxon.OutputURIResolver;

/**
 * This class was developed for the web simulator project and is part of the
 * implementation of an Java compiler that compiles and instantiates Java code
 * completely in memory without usage of the underlying file system. This
 * functionality was necessary in order to execute simulations on an J2EE 5
 * application server like JBoss.
 * 
 * Usage: by XSLT2Processor.java
 * 
 * MemoryOutputURIResolver
 * 
 * @author Marco Pehla
 * @since 19.03.2008
 * @version $Revision$
 */
public class MemoryOutputURIResolver implements OutputURIResolver {

  private StringWriter stringWriter = new StringWriter();
  private HashMap<String, String> resultHashMap = new HashMap<String, String>();
  private String fileName = "";

  private final String EXTENSION;

  public MemoryOutputURIResolver() {
    this.EXTENSION = ".out";
  }

  public MemoryOutputURIResolver(String extension) {
    this.EXTENSION = extension;
  }

  public Result resolve(String href, String base) throws TransformerException {
    if (href.endsWith(EXTENSION)) {
      this.fileName = href.substring(0, href.indexOf(EXTENSION));
      StreamResult result = new StreamResult(stringWriter);
      result.setSystemId(href);
      return result;
    } else {
      return null;
    }
  }

  public void close(Result result) throws TransformerException {
    try {
      // decode the file name
      // used to convert from %5C to \ in UTF-8 for instance (on Windows OS)
      this.resultHashMap.put(URLDecoder.decode(this.fileName, "UTF-8"),
          this.stringWriter.toString());

    } catch (UnsupportedEncodingException uee) {
      System.err
          .println("Not able to decode the UTF-8 URL in class MemoryOutputURIResolver. Using the un-decoded file name.");
      this.resultHashMap.put(this.fileName, this.stringWriter.toString());

    }

    this.stringWriter = new StringWriter(); // use a new instance, otherwise the
                                            // previous content is included!!!
  }

  public HashMap<String, String> getResultHashMap() {
    return this.resultHashMap;
  }

}
