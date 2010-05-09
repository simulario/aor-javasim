package aors.model.dataTypes;

public final class AORSFloat extends AORSDatatype<Double> {

  public AORSFloat(double value) {
    super(value);
  }

  @Override
  public AORSFloat clone() {
    return new AORSFloat(this.getValue());
  }
}