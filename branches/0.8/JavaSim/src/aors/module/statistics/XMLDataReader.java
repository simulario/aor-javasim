package aors.module.statistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import aors.controller.SimulationDescription;
import aors.statistics.AbstractStatisticsVariable;
import aors.statistics.AbstractStatisticsVariable.AggregFunEnumLit;
import aors.statistics.AbstractStatisticsVariable.StatVarDataSourceEnumLit;
import aors.statistics.AbstractStatisticsVariable.StatVarDataTypeEnumLit;
import aors.util.XMLLoader;

/**
 * XmlDataReader
 * 
 * This class extracts all the necessary data from the XML simulation
 * description. This data is needed to draw something before the actual
 * simulation starts.
 * 
 * @author Daniel Draeger
 * @since 27.10.2009
 * 
 * */

public class XMLDataReader {

  private final String PREFIX = SimulationDescription.ER_AOR_PREFIX + ":";
  private final String ENGLISH = "en";
  private StatisticVar statsVar;
  private SimulationDescription simDescription;
  private int currencyISOLength = 3;
  private String disName = "";
  private List<String> comparisons = new ArrayList<String>();
  private List<FrequencyDistributionChart> frequencyDistributionChartVars;
  private Map<String, StatisticVar> statsVarMap = new HashMap<String, StatisticVar>();
  // Nodes
  // StatisticsVariableUI
  private final String statisticsVariableUI = "StatisticsVariableUI";
  private final String displayName = "DisplayName";
  private final String toolTip = "ToolTip";
  private final String hint = "Hint";
  private final String label = "Label";
  // Statistics
  private final String variableNode = "Variable";
  private final String sourceNode = "Source";
  private final String formatNode = "Format";
  private static final String objProperty = "property";
  private static final String objType = "objectType";
  private static final String objIdRef = "objectIdRef";
  // Attributes
  private final String variable = "variable";
  private final String computeOnlyAtEnd = "computeOnlyAtEnd";
  private final String aggregationFunction = "aggregationFunction";
  private final String showChart = "showChart";
  private final String initialValue = "initialValue";
  private final String name = "name";
  private final String dataType = "dataType";
  private final String comparisonGroup = "comparisonGroup";
  private final String decimalPlace = "decimalPlaces";
  // ObjectPropertyChart
  private final String frequencyDistributionChart = "FrequencyDistributionChart";
  private final String objectPropertyStatisticsVariable = "objectPropertyStatisticsVariable";
  private final String minValue = "minValue";
  private final String maxValue = "maxValue";
  private final String intervalSize = "intervalSize";
  private final String chartType = "chartType";

  public List<String> getComparisons() {
    return comparisons;
  }

  /* The constructor of an XmlDataReader object loads a DOM. */
  public XMLDataReader(Document domDocument) {
    XMLLoader.loadXML(domDocument);
  }

  public XMLDataReader(SimulationDescription simDescription) {
    this.simDescription = simDescription;
  }

  public List<FrequencyDistributionChart> getFrequencyDistributionChartVars() {
    return frequencyDistributionChartVars;
  }

  // Extract the statistic variables out of the XML simulation description
  public Map<String, StatisticVar> getStatisticVars() {
    // List for the statistic variables
    List<StatisticVar> list = new ArrayList<StatisticVar>();
    frequencyDistributionChartVars = new ArrayList<FrequencyDistributionChart>();
    String node = "//" + PREFIX + variableNode;
    NodeList nodeList = simDescription.getNodeList(node);
    if (nodeList != null) {
      for (int i = 0; i < nodeList.getLength(); i++) {
        // create StatisticVar
        statsVar = new StatisticVar(false);
        // AnalyseItem
        statsVar.getStatsVarUIMap().put(StatisticVar.ID, "" + i);
        statsVar.getStatsVarUIMap().put(StatisticVar.MIN, "0");
        statsVar.getStatsVarUIMap().put(StatisticVar.MAX, "0");
        statsVar.getStatsVarUIMap().put(StatisticVar.AVG, "0");
        statsVar.getStatsVarUIMap().put(StatisticVar.CONFIDENCELOWBOUND, "0");
        statsVar.getStatsVarUIMap().put(StatisticVar.CONFIDENCEUPBOUND, "0");
        statsVar.getStatsVarUIMap().put(StatisticVar.STDDEVIATION, "0");
        // get attributes of each StatisticVar and add them
        getAttributes(nodeList.item(i), statsVar);

        if (nodeList.item(i).hasChildNodes()) {
          Node source = simDescription.getNode("//" + PREFIX + variableNode
              + "[@name='" + statsVar.getName() + "']/" + PREFIX + sourceNode);
          getAttributes(source, statsVar);

          Node sourceTypeNode = simDescription.getNode("//" + PREFIX
              + variableNode + "[@name='" + statsVar.getName() + "']/" + PREFIX
              + sourceNode + "/*");
          if (sourceTypeNode != null) {
            if (sourceTypeNode.getNodeName().equalsIgnoreCase(
                StatVarDataSourceEnumLit.ObjectProperty.name())) {
              getAttributes(sourceTypeNode, statsVar);
              FrequencyDistributionChart frDiCh;
              if (statsVar.getSourceObjIdRef() == StatisticVar.DEFAULTIDREF) {
                String name = statsVar.getName();
                StatVarDataTypeEnumLit type = statsVar.getDataType();
                AggregFunEnumLit aggr = statsVar.getAggrFn();
                boolean isCompEnd = statsVar.isComputeOnlyAtEnd();
                String id = statsVar.getStatsVarUIMap().get(StatisticVar.ID);
                statsVar = new StatisticVar(true);
                statsVar.setName(name);
                statsVar.setDataType(type);
                statsVar.setAggrFn(aggr);
                statsVar.setComputeOnlyAtEnd(isCompEnd);
                statsVar.getStatsVarUIMap().put(StatisticVar.ID, id);
                getAttributes(sourceTypeNode, statsVar);
                frDiCh = getFrequencyDistributionChart(statsVar, false);
              } else {
                frDiCh = getFrequencyDistributionChart(statsVar, true);
              }
              statsVar
                  .setSourceDataSource(StatVarDataSourceEnumLit.ObjectProperty);
              if (frDiCh != null) {
                statsVar.setFrequencyDistributionChart(true);
                frequencyDistributionChartVars.add(frDiCh);
              } else {
                statsVar.setFrequencyDistributionChart(false);
              }
            } else if (sourceTypeNode.getNodeName().equals(
                StatVarDataSourceEnumLit.ValueExpr.name())) {
              statsVar.setSourceDataSource(StatVarDataSourceEnumLit.ValueExpr);
            } else if (sourceTypeNode.getNodeName().equals(
                StatVarDataSourceEnumLit.ResourceUtilization.name())) {
              statsVar
                  .setSourceDataSource(StatVarDataSourceEnumLit.ResourceUtilization);
            } else if (sourceTypeNode.getNodeName().equals(
                StatVarDataSourceEnumLit.ObjectTypeExtensionSize.name())) {
              statsVar
                  .setSourceDataSource(StatVarDataSourceEnumLit.ObjectTypeExtensionSize);
              getAttributes(sourceTypeNode, statsVar);
            } else if (sourceTypeNode.getNodeName().equals(
                StatVarDataSourceEnumLit.StatisticsVariable.name())) {
              statsVar
                  .setSourceDataSource(StatVarDataSourceEnumLit.StatisticsVariable);
            } else if (sourceTypeNode.getNodeName().equals(
                StatVarDataSourceEnumLit.GlobalVariable.name())) {
              statsVar
                  .setSourceDataSource(StatVarDataSourceEnumLit.GlobalVariable);
            }
          }
        } else {
          statsVar.setSourceDataSource(StatVarDataSourceEnumLit.Default);
        }

        getStatisticsVarUI(statsVar, ENGLISH);
        if ((statsVar.getStatsVarUIMap().containsKey(displayName))) {
          list.add(statsVar);
        } else {
          /*
           * delete comment if vars without StatisticsVariableUI are needed to
           * be shown statsVar.getStatsVarUIMap().put(displayName, disName);
           * disName = ""; list.add(statsVar);
           */
        }
        if (statsVar.getInitialValue() != null) {
          statsVar.getStatsVarUIMap().put(StatisticVar.MIN,
              statsVar.getInitialValue().toString());
          statsVar.getStatsVarUIMap().put(StatisticVar.MAX,
              statsVar.getInitialValue().toString());
        }
        if (statsVar.isComputeOnlyAtEnd()) {
          statsVar.getStatsVarUIMap().put(StatisticVar.MIN, "-");
          statsVar.getStatsVarUIMap().put(StatisticVar.MAX, "-");
          statsVar.getStatsVarUIMap().put(StatisticVar.AVG, "-");
        }
        statsVarMap.put(statsVar.getName(), statsVar);
      }
    } else {
      // return null if no statistic variables are defined
      return null;
    }
    // return list
    return statsVarMap;
  }

  /**
   * sets all attributes within the node to the specific statsVar
   * 
   * @param node
   * @param statsVar
   */
  private void getAttributes(Node node, StatisticVar statsVar) {
    if (node.hasAttributes()) {
      NamedNodeMap nodeAttr = node.getAttributes();
      for (int i = 0; i < nodeAttr.getLength(); i++) {
        String attName = nodeAttr.item(i).getNodeName();
        String attValue = nodeAttr.item(i).getNodeValue();
        if (attName.equalsIgnoreCase(computeOnlyAtEnd)) {
          statsVar.setComputeOnlyAtEnd(Boolean.valueOf(attValue.toLowerCase()));
        } else if (attName.equalsIgnoreCase(aggregationFunction)) {
          statsVar.setAggrFn(AbstractStatisticsVariable.AggregFunEnumLit
              .valueOf(attValue));
        } else if (attName.equalsIgnoreCase(objIdRef)) {
          statsVar.setSourceObjIdRef(Integer.parseInt(attValue));
        } else if (attName.equalsIgnoreCase(objProperty)) {
          statsVar.setSourceObjProperty(attValue);
        } else if (attName.equalsIgnoreCase(objType)) {
          statsVar.setSourceObjType(attValue);
        } else if (attName.equalsIgnoreCase(initialValue)) {
          if (statsVar.getDataType().equals(StatVarDataTypeEnumLit.Integer)) {
            statsVar.setInitialValue(Long.parseLong(attValue));
            statsVar.setLastValue(Long.parseLong(attValue));
          } else if (statsVar.getDataType()
              .equals(StatVarDataTypeEnumLit.Float)) {
            statsVar.setInitialValue(Float.parseFloat(attValue));
            statsVar.setLastValue(Float.parseFloat(attValue));
          }
        } else if (attName.equalsIgnoreCase(name)) {
          statsVar.setName(attValue);
          if (this.disName.equalsIgnoreCase("")) {
            this.disName = attValue;
          }
        } else if (attName.equalsIgnoreCase(dataType)) {
          statsVar.setDataType(StatVarDataTypeEnumLit.valueOf(attValue));
        } else if (attName.equalsIgnoreCase(displayName)) {
          this.disName = attValue;
        } else if (attName.equalsIgnoreCase(decimalPlace)) {
          statsVar.getStatsVarUIMap().put(StatisticVar.DECIMALPLACES, attValue);
        }
      }
    }
  }

  public void getStatisticsVarUI(StatisticVar var, String lang) {
    String node = "//" + PREFIX + statisticsVariableUI;
    NodeList nodeList = simDescription.getNodeList(node);
    if (nodeList.getLength() > 0) {
      for (int i = 0; i < nodeList.getLength(); i++) {
        String varName = nodeList.item(i).getAttributes()
            .getNamedItem(variable).getNodeValue();
        // StatisticsVariableUI belongs to StatisticVar
        if (varName.equalsIgnoreCase(var.getName())) {
          // Attributes
          NamedNodeMap attributes = nodeList.item(i).getAttributes();
          for (int j = 0; j < attributes.getLength(); j++) {
            String attName = attributes.item(j).getNodeName();
            String attValue = attributes.item(j).getNodeValue();
            // DisplayValues
            if (attName.equalsIgnoreCase("minDisplayValue")) {
              var.getStatsVarUIMap().put(attName, attValue);
            } else if (attName.equalsIgnoreCase("maxDisplayValue")) {
              var.getStatsVarUIMap().put(attName, attValue);
            } else if (attName.equalsIgnoreCase(comparisonGroup)) {
              var.getStatsVarUIMap().put(attName, attValue);
              if (!(comparisons.contains(attValue))) {
                comparisons.add(attValue);
              }
            } else if (attName.equalsIgnoreCase(showChart)) {
              var.getStatsVarUIMap().put(StatisticVar.SHOWCHART, attValue);
            }
          }
          // language elements
          NodeList childs = nodeList.item(i).getChildNodes();
          for (int k = 0; k < childs.getLength(); k++) {
            // Hint
            if (childs.item(k).getNodeName().equalsIgnoreCase(hint)) {
              NodeList texts = childs.item(k).getChildNodes();
              for (int t = 0; t < texts.getLength(); t++) {
                if ((texts.item(t).hasAttributes())
                    && (texts.item(t).getAttributes().item(0).getNodeValue()
                        .equalsIgnoreCase(lang))) {
                  var.getStatsVarUIMap().put(toolTip,
                      texts.item(t).getTextContent());
                }
              }
            }
            // Label
            else if (childs.item(k).getNodeName().equalsIgnoreCase(label)) {
              NodeList texts = childs.item(k).getChildNodes();
              for (int l = 0; l < texts.getLength(); l++) {
                // Text
                if ((texts.item(l).hasAttributes())
                    && (texts.item(l).getAttributes().item(0).getNodeValue()
                        .equalsIgnoreCase(lang))) {
                  var.getStatsVarUIMap().put(displayName,
                      texts.item(l).getTextContent());
                }
              }
            } else if (childs.item(k).getNodeName()
                .equalsIgnoreCase(formatNode)) {
              Node format = simDescription.getNode("//" + PREFIX
                  + statisticsVariableUI + "[@variable='" + var.getName()
                  + "']/" + PREFIX + formatNode);
              if (format != null) {
                this.getAttributes(format, var);
                
                Node formatCategory = simDescription.getNode("//" + PREFIX
                    + statisticsVariableUI + "[@variable='" + var.getName()
                    + "']/" + PREFIX + formatNode + "/*");
                if (formatCategory != null) {
                  if (formatCategory.getNodeName().equalsIgnoreCase("Currency")) {
                    String curr = formatCategory.getTextContent();
                    if (curr.length() > currencyISOLength) {
                      curr = " "
                          + curr.substring(currencyISOLength + 1,
                              curr.length() - 1);
                    }
                    var.getStatsVarUIMap().put(formatNode, curr);
                  } else {
                    var.getStatsVarUIMap().put(formatNode,
                        formatCategory.getTextContent());
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  private FrequencyDistributionChart getFrequencyDistributionChart(
      StatisticVar var, boolean isSingle) {
    FrequencyDistributionChart opChart = null;
    String node = "//" + PREFIX + frequencyDistributionChart
        + "[@objectPropertyStatisticsVariable='" + var.getName() + "']";
    Node chart = simDescription.getNode(node);
    if (chart != null) {
      opChart = new FrequencyDistributionChart();
      opChart.setObjectType(var.getSourceObjType());
      opChart.setProperty(var.getSourceObjProperty());
      opChart.setValues(new HashMap<Integer, List<Number>>());
      opChart.setSingleObject(isSingle);
      NamedNodeMap attributes = chart.getAttributes();
      for (int j = 0; j < attributes.getLength(); j++) {
        String attName = attributes.item(j).getNodeName();
        String attValue = attributes.item(j).getNodeValue();
        if (attName.equalsIgnoreCase(objectPropertyStatisticsVariable)) {
          opChart.setName(attValue);
        } else if (attName.equalsIgnoreCase(minValue)) {
          opChart.setMinValue(Integer.parseInt(attValue));
        } else if (attName.equalsIgnoreCase(maxValue)) {
          opChart.setMaxValue(Integer.parseInt(attValue));
        } else if (attName.equalsIgnoreCase(intervalSize)) {
          opChart.setIntervalSize(Integer.parseInt(attValue));
        } else if (attName.equalsIgnoreCase(chartType)) {
          opChart.setChartType(attValue);
        }
      }
    }
    return opChart;
  }

}
