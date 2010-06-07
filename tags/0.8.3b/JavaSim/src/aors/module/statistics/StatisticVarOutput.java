package aors.module.statistics;

public class StatisticVarOutput {

  private double value, min, max, avg;
  private String name, confidenceInterval;

  public StatisticVarOutput() {

  }

  public double getValue() {
    return value;
  }

  public void setValue(double value) {
    this.value = value;
  }

  public double getMin() {
    return min;
  }

  public void setMin(double min) {
    this.min = min;
  }

  public double getMax() {
    return max;
  }

  public void setMax(double max) {
    this.max = max;
  }

  public double getAvg() {
    return avg;
  }

  public void setAvg(double avg) {
    this.avg = avg;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setConfidenceInterval(String confidenceInterval) {
    this.confidenceInterval = confidenceInterval;
  }

  public String getConfidenceInterval() {
    return confidenceInterval;
  }
}
