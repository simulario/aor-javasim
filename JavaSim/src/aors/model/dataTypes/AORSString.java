package aors.model.dataTypes;

public final class AORSString extends AORSDatatype<String> {

  public AORSString(String value) {
    super(value);
  }

  @Override
  public AORSString clone() {
    return new AORSString(this.getValue());
  }

	public static AORSString valueOf(String string) {
		return new AORSString(string);
	}
}