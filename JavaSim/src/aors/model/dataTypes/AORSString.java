package aors.model.dataTypes;

public final class AORSString extends AORSDatatype<String> {

  public AORSString(String value) {
    super(value);
  }

  @Override
  public AORSString clone() {
    return new AORSString(this.getValue());
  }
}