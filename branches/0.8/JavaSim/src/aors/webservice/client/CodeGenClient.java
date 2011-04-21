package aors.webservice.client;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.xml.ws.WebServiceRef;

import aors.controller.Project;

import com.simulario.Output;
import com.simulario.Simweb;
import com.simulario.TranslationResult;
import com.simulario.ValidationResult;
import com.simulario.XSLT2ProcessorImplService;

public class CodeGenClient {
  @WebServiceRef(wsdlLocation = "http://simulario.informatik.tu-cottbus.de:8080/codegen/codegen?wsdl")
  static XSLT2ProcessorImplService webService = new XSLT2ProcessorImplService();

  /**
   * Generate the source code from XML scenario. This will result in WebService
   * invocation and then the result is split in the java source files and then
   * written to disk in the given parent directory.
   * 
   * @param xmlSourceString
   *          the XML scenario as string
   * @param generateSourcePath
   *          the root path where the java files will be written
   * @return true if the generation is successful, false otherwise
   */
  public boolean generateSource(String xmlSourceString, File generateSourcePath) {
    boolean result = true;

    if (xmlSourceString == null || xmlSourceString.length() < 1) {
      System.out
          .println("Error: try to generate source with an empty scenario file! "
              + "Nothing will be generated!");
      return false;
    }

    // create the folders structure
    createFolders(generateSourcePath);

    // get the web service proxy
    Simweb wsPort = webService.getSimwebPort();

    // invoke the translation service and get the result
    TranslationResult translationResult = wsPort.translate(xmlSourceString);

    // the outputs - each output is a source file
    List<Output> resultingOutput = translationResult.getContents();

    // problem in validation/translation
    if (translationResult.getCode() != 0) {
      System.err.println("\n    received error code: "
          + translationResult.getCode());

      for (Output currentResult : resultingOutput) {
        String errName = currentResult.getName();
        String errMessage = currentResult.getContent();
        System.err.println("      [" + errName + " :  " + errMessage + "]");
      }

      return false;
    }

    // parse the output source and write the Java source code on disk.
    for (Output currentFile : resultingOutput) {
      String outputName = currentFile.getName();
      String outputContent = currentFile.getContent();

      if (!outputName.startsWith("./")) {
        continue;
      }

      String resultFileNameAndPath = outputName.substring(2);

      resultFileNameAndPath = generateSourcePath + File.separator
          + resultFileNameAndPath;

      // System.out.println("Write file: " + resultFileNameAndPath);

      try {
        BufferedWriter writter = new BufferedWriter(new FileWriter(
            resultFileNameAndPath));
        writter.write(outputContent);
        writter.flush();
        writter.close();
      } catch (IOException e) {
        result = false;
        System.out.println("Error in writing source file: "
            + resultFileNameAndPath);
        e.printStackTrace();
      }
    }

    return result;
  }

  /**
   * Invoke the "validate" method of the web service.
   * 
   * @param xmlSourceString
   *          the XML scenario represented as a string
   * @return true if the XML is valid according with schema, false otherwise
   */
  public boolean validate(String xmlSourceString) {
    // get the web service proxy
    Simweb wsPort = webService.getSimwebPort();

    // invoke the validation service and get the result
    ValidationResult validationResult = wsPort.validate(xmlSourceString);

    // the outputs - each output is a source file
    String errMessage = validationResult.getMessages();

    if (validationResult.getCode() != 0) {
      System.err.println("\n    received error code: "
          + validationResult.getCode());
      System.err.println("      [" + errMessage + "]");
      return false;
    } else {
      return true;
    }

  }

  /**
   * Create the structure directories required by the Java sources
   * 
   * @param sourceRootPath
   *          the root path for the source directories/files
   */
  private void createFolders(File sourceRootPath) {
    File directory;
    String separator = File.separator;

    // create the ./sim directors
    if (!Project.MAIN_PACKAGE_NAME.equals("")) {
      directory = new File(sourceRootPath + separator
          + Project.MAIN_PACKAGE_NAME);
      if (!directory.isDirectory()) {
        directory.mkdir();
      }
      sourceRootPath = new File(sourceRootPath.getAbsolutePath() + separator
          + Project.MAIN_PACKAGE_NAME);
    }

    // create the ./controller directory
    directory = new File(sourceRootPath + separator + "controller");
    if (!directory.isDirectory()) {
      directory.mkdir();
    }

    // create the ./model directory
    directory = new File(sourceRootPath + separator + "model");
    if (!directory.isDirectory()) {
      directory.mkdir();
    }

    // create the ./model/agtsim directory
    directory = new File(sourceRootPath + separator + "model" + separator
        + "agtsim");
    if (!directory.isDirectory()) {
      directory.mkdir();
    }

    // create the ./model/dataTypes directory
    directory = new File(sourceRootPath + separator + "model" + separator
        + "dataTypes");
    if (!directory.isDirectory()) {
      directory.mkdir();
    }

    // create the ./model/envevt directory
    directory = new File(sourceRootPath + separator + "model" + separator
        + "envevt");
    if (!directory.isDirectory()) {
      directory.mkdir();
    }

    // create the ./model/envsim directory
    directory = new File(sourceRootPath + separator + "model" + separator
        + "envsim");
    if (!directory.isDirectory()) {
      directory.mkdir();
    }

    // create the ./interaction directory
    directory = new File(sourceRootPath + separator + "interaction");
    if (!directory.isDirectory()) {
      directory.mkdir();
    }

    // create the ./interaction/agentControl directory
    directory = new File(sourceRootPath + separator + "interaction" + separator
        + "agentControl");
    if (!directory.isDirectory()) {
      directory.mkdir();
    }
  }
}
