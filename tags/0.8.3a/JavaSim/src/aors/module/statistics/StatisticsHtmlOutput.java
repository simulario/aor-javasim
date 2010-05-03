package aors.module.statistics;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import aors.module.statistics.gui.ComponentTranslator;

/**
 * StatisticsHtmlOutput
 * 
 * This class creates the html output
 * 
 * @author Daniel Draeger
 * @since 01.12.2009
 */
public class StatisticsHtmlOutput {

  private Dimension screenDimension = Toolkit.getDefaultToolkit()
      .getScreenSize().getSize();

  private String jqueryMinJS;
  private String jqueryHoverJS;
  private String hoverCSS;
  private String jquerySVGJS;
  private String hoverJS;

  private final File htmlFile;
  private File imageFolder;
  private FileWriter fw;
  private BufferedWriter bw;

  private final Map<Integer, String[]> htmlMap;
  private final Map<Integer, Integer[]> htmlCompMap;

  private int width = new Double(screenDimension.getWidth() * 0.8).intValue();
  private int height = new Double(screenDimension.getHeight() * 0.6).intValue();

  // language Strings
  private final String statsOutput = ComponentTranslator.getResourceBundle()
      .getString("statsOutputL");
  private final String singleSim = ComponentTranslator.getResourceBundle()
      .getString("singleChartHL");
  private final String multiSim = ComponentTranslator.getResourceBundle()
      .getString("multiChartHL");
  private final String objectPr = ComponentTranslator.getResourceBundle()
      .getString("distChartHL");
  private final String userCharts = ComponentTranslator.getResourceBundle()
      .getString("userChartL");
  private final String comparison = ComponentTranslator.getResourceBundle()
      .getString("comparisonHL");
  private final String clear = ComponentTranslator.getResourceBundle()
      .getString("clearB");
  private final String zoomIn = ComponentTranslator.getResourceBundle()
      .getString("zoomInB");
  private final String zoomOut = ComponentTranslator.getResourceBundle()
      .getString("zoomOutB");
  private final String varName = ComponentTranslator.getResourceBundle()
      .getString("VariableNameL");
  private final String minimum = ComponentTranslator.getResourceBundle()
      .getString("minimumL");
  private final String maximum = ComponentTranslator.getResourceBundle()
      .getString("maximumL");
  private final String average = ComponentTranslator.getResourceBundle()
      .getString("averageL");
  private final String stdDev = ComponentTranslator.getResourceBundle()
      .getString("stdDeviationL");
  private final String ci_low = ComponentTranslator.getResourceBundle()
      .getString("ciLowL");
  private final String ci_up = ComponentTranslator.getResourceBundle()
      .getString("ciUpL");

  /**
   * 
   * Create a new {@code StatisticsHtmlOutput}.
   * 
   * @param path
   *          - path of the current project
   */
  public StatisticsHtmlOutput(String path, Map<Integer, String[]> map,
      Map<Integer, Integer[]> mapComp) {
    try {
      jqueryMinJS = new URL("file://" + StatisticsCore.pathToHtmlFiles
          + "jquery-1.3.min.js").toString();
      jqueryHoverJS = new URL("file://" + StatisticsCore.pathToHtmlFiles
          + "jquery.hoverIntent.minified.js").toString();
      hoverCSS = new URL("file://" + StatisticsCore.pathToHtmlFiles
          + "hover.css").toString();
      jquerySVGJS = new URL("file://" + StatisticsCore.pathToHtmlFiles
          + "jquery.svg.js").toString();
      hoverJS = new URL("file://" + StatisticsCore.pathToHtmlFiles + "hover.js")
          .toString();

    } catch (MalformedURLException e1) {
      e1.printStackTrace();
    }

    imageFolder = new File(path);
    htmlFile = new File(imageFolder + File.separator + "Statistics.html");
    htmlMap = map;
    htmlCompMap = mapComp;

    try {
      fw = new FileWriter(htmlFile);
      bw = new BufferedWriter(fw);
      createHead();
      createContent();
      bw.write("</html>");
      bw.flush();
      bw.close();
      System.out.println("HTML-File created");
    } catch (Exception e) {
      System.out.println("Fehler");
    }
  }

  /**
   * Usage: creates the header
   * 
   */

  private void createHead() {
    try {
      bw
          .write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"\n"
              + " \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"> \n");
      bw.write("<html xmlns=\"http://www.w3.org/1999/xhtml\"> \n");
      bw.write("<head> \n");
      bw
          .write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" /> \n");
      bw.write("<title>AOR-Statistics</title> \n");
      bw.write("<link rel='stylesheet' href='" + hoverCSS + "'> \n");
      bw.write("<script type='text/javascript' src='" + jqueryMinJS
          + "'></script> \n");
      bw.write("<script type='text/javascript' src='" + jqueryHoverJS
          + "'></script> \n");
      bw.write("<script type='text/javascript' src='" + jquerySVGJS
          + "'></script> \n");
      bw.write("<script type='text/javascript' src='" + hoverJS
          + "'></script> \n");
      bw
          .write("<script type='text/javascript'>$(function() { \n"
              + "  var width = "
              + width
              + ";\n"
              + "  var height = "
              + height
              + ";\n"
              + "$('#svgContainer').svg( {"
              + "   settings: {width: '"
              + width
              + "px', height: '"
              + height
              + "px'}}); \n"
              + "$('div.sub a').click(function() { \n"
              + "  reBuildTable();\n"
              + "  var svg = $('#svgContainer').svg('get');  \n"
              + "  svg.load($(this).text(), {addTo: false, changeSize: false, onLoad: setData});  \n"
              + "  resetSize(svg, '100%', '100%');  \n"
              + "});  \n"
              + "$('#clear').click(function() { \n"
              + "  $('#svgContainer').svg('get').clear(); \n"
              + "  reBuildTable();\n"
              + "}); \n"
              + "$('#zoomIn').click(function() { \n"
              + "  width = width*1.2 \n"
              + "  height = height*1.2 \n"
              + "  $('#svgContainer').svg('get').configure({width: width, height: height}, false); \n"
              + "}); \n"
              + "$('#zoomOut').click(function() { \n"
              + "  width = width/1.2 \n"
              + "  height = height/1.2 \n"
              + "  $('#svgContainer').svg('get').configure({width: width, height: height}, false); \n"
              + "})}); \n"
              + "function resetSize(svg, width, height) { \n"
              + "svg.configure({width: width, height: height }); \n" + "}; \n");
      setAssosiativeArray(htmlMap);
      setAssosiativeCompArray(htmlCompMap);
      bw
          .write("function setData(){ \n"
              + "var svg = $('#svgContainer').svg('get').root();  \n"
              + "var id = $($(svg),svg).attr('id'); \n"
              + "if(data[id]!=null){ \n"
              + "$('#name').html(data[id]['name']);  \n"
              + "$('#min').html(data[id]['min']);  \n"
              + "$('#max').html(data[id]['max']);  \n"
              + "$('#avg').html(data[id]['avg']);  \n"
              + "$('#std').html(data[id]['std']);  \n"
              + "$('#cil').html(data[id]['CIlow']);  \n"
              + "$('#ciu').html(data[id]['CIup']); \n"
              + "}else if(dataComp[id]!=null){  \n"
              + "$('#name').html(dataComp[id][0]['name']); \n"
              + "$('#min').html(dataComp[id][0]['min']);  \n"
              + "$('#max').html(dataComp[id][0]['max']);  \n"
              + "$('#avg').html(dataComp[id][0]['avg']);  \n"
              + "$('#std').html(dataComp[id][0]['std']);  \n"
              + "$('#cil').html(dataComp[id][0]['CIlow']); \n"
              + "$('#ciu').html(dataComp[id][0]['CIup']); \n"
              + "for(var r=1;r<dataComp[id].length;r++){ \n"
              + "  addTableRow($('#table'),dataComp,id,r); \n"
              + "}};} \n"
              + "function addTableRow(jQtable, dataComp, id, r){ \n"
              + "jQtable.each(function(){ \n"
              + "var valueNames = new Array('name', 'min', 'max', 'avg', 'std', 'CIlow', 'CIup'); \n"
              + "var $table = $(this); \n"
              + "var n = $('tr:last td', this).length; \n"
              + "var tds = '<tr class=\\'rem\\'>'; \n"
              + "for(var i = 0; i < n; i++){ \n"
              + "  tds += '<td>'+ dataComp[id][r][valueNames[i]] +'</td>'; \n"
              + "} \n" + "tds += '</tr>'; \n"
              + "if($('tbody', this).length > 0){ \n"
              + "  $('tbody', this).append(tds); \n" + "}else { \n"
              + "  $(this).append(tds); \n" + "} \n" + "}); \n" + "} \n"
              + "function reBuildTable(){ \n" + "  $('#name').html('');   \n"
              + "  $('#min').html('');   \n" + "  $('#max').html('');   \n"
              + "  $('#avg').html('');   \n" + "  $('#std').html('');   \n"
              + "  $('#cil').html('');  \n" + "  $('#ciu').html('');  \n"
              + "  $('.rem').remove(); \n" + "}");
      bw.write("</script> \n");
      bw.write("</head> \n");
    } catch (IOException e) {
      System.out.println("Error in HEAD");
    }
  }

  /**
   * Usage: insert associative array for single variable
   * 
   * @param map
   */
  private void setAssosiativeArray(Map<Integer, String[]> map) {
    try {
      bw.write("var data = new Array(); \n");
      Set<Map.Entry<Integer, String[]>> entrySet = map.entrySet();
      Iterator<Map.Entry<Integer, String[]>> it = entrySet.iterator();
      while (it.hasNext()) {
        Map.Entry<Integer, String[]> entry = it.next();
        bw.write("data[" + entry.getKey() + "]= new Array(); \n");
        bw.write("data[" + entry.getKey() + "]['name'] = '"
            + (entry.getValue())[0] + "'; \n");
        bw.write("data[" + entry.getKey() + "]['min'] = '"
            + (entry.getValue())[1] + "'; \n");
        bw.write("data[" + entry.getKey() + "]['max'] = '"
            + (entry.getValue())[2] + "'; \n");
        bw.write("data[" + entry.getKey() + "]['avg'] = '"
            + (entry.getValue())[3] + "'; \n");
        bw.write("data[" + entry.getKey() + "]['std'] = '"
            + (entry.getValue())[4] + "'; \n");
        bw.write("data[" + entry.getKey() + "]['CIlow'] = '"
            + (entry.getValue())[5] + "'; \n");
        bw.write("data[" + entry.getKey() + "]['CIup'] = '"
            + (entry.getValue())[6] + "'; \n");
      }
    } catch (Exception e) {
    }
  }

  /**
   * Usage: insert assosiative array for comparison
   * 
   * @param map
   */
  private void setAssosiativeCompArray(Map<Integer, Integer[]> map) {
    try {
      bw.write("var dataComp = new Array(); \n");
      Set<Map.Entry<Integer, Integer[]>> entrySet = map.entrySet();
      Iterator<Map.Entry<Integer, Integer[]>> it = entrySet.iterator();
      while (it.hasNext()) {
        Map.Entry<Integer, Integer[]> entry = it.next();
        bw.write("dataComp[" + entry.getKey() + "]= new Array(); \n");
        for (int i = 0; i < entry.getValue().length; i++) {
          bw.write("dataComp[" + entry.getKey() + "][" + i + "] = data["
              + (entry.getValue())[i] + "]; \n");
        }
      }
    } catch (Exception e) {
    }
  }

  /**
   * Usage: create the body
   */
  private void createContent() {
    try {
      bw.write("<body id='home'> \n ");
      bw.write("<div class='container'>" + statsOutput + "</div> \n");
      bw.write("<ul id='topnav'> \n");
      // createCategories
      createCategories();
      bw.write("</ul> \n");
      bw.write("</div> \n");
      bw.write("<div id='dataDiv'> \n" + "<table id='table' border='1'> \n"
          + "<tr> \n" + "<th>"
          + varName
          + "</th> \n"
          + "<th>"
          + minimum
          + "</th> \n"
          + "<th>"
          + maximum
          + "</th> \n"
          + "<th>"
          + average
          + "</th> \n"
          + "<th>"
          + stdDev
          + "</th> \n"
          + "<th>"
          + ci_low
          + "</th> \n"
          + "<th>"
          + ci_up
          + "</th> \n"
          + "</tr><tr> \n "
          + "<td id='name'></td> \n"
          + "<td id='min'></td> \n"
          + "<td id='max'></td> \n"
          + "<td id='avg'></td> \n"
          + "<td id='std'></td> \n"
          + "<td id='cil'></td> \n"
          + "<td id='ciu'></td> </tr></table>\n"
          + "</div> \n");
      bw.write("<div id='svgContainer' class='svgDiv' style='width:" + width
          + "px; height:" + height + "px;'></div> \n");
      bw.write("<p><button type='button' id='clear'>" + clear
          + "</button></p> \n");
      bw.write("<p><button type='button' id='zoomIn'>" + zoomIn
          + "</button></p> \n");
      bw.write("<p><button type='button' id='zoomOut'>" + zoomOut
          + "</button></p> \n");
      bw.write("</body> \n ");
    } catch (IOException e) {
      System.out.println("Error in Content");
    }
  }

  /**
   * Usage: creates the categories
   * 
   */
  private void createCategories() {
    addCategoryWithFiles(singleSim, "SSim.svg");
    addCategoryWithFiles(objectPr, "Objp.svg");
    addCategoryWithFiles(multiSim, "MSim.svg");
    addCategoryWithFiles(comparison, "MCom.svg");
    addCategoryWithFiles(userCharts, "own.svg");
  }

  private void addCategoryWithFiles(String categoryName, String fileEnding) {
    File[] imageArray = imageFolder.listFiles();
    String fileString = fileEnding.substring(0, fileEnding.length() - 4);

    try {
      bw.write("<li> \n");
      bw.write("<a href='#' class='category'>" + categoryName + "</a> \n");
      bw.write("<div class='sub'> \n");
      bw.write("<ul> \n");

      for (int i = 0; i < imageArray.length; i++) {
        if (imageArray[i].getName().endsWith(fileEnding)) {
          bw.write("<li><a id='" + fileString + "' href='#'>"
              + imageArray[i].getName() + "</a></li> \n");
        }
      }

      bw.write("</ul> \n");
      bw.write("</div> \n");
      bw.write("</li> \n");
    } catch (IOException e) {
      System.out.println("Error in Content");
    }
  }

  /**
   * Usage: return the html path
   * 
   * @return String
   */
  public String getHtmlPath() {
    System.out.println(htmlFile.getPath());
    return htmlFile.getPath();
  }
}
