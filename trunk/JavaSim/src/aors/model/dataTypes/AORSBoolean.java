package aors.model.dataTypes;

public final class AORSBoolean extends AORSDatatype<Boolean> {

  public AORSBoolean(boolean value) {
    super(value);
  }

  @Override
  public AORSBoolean clone() {
    return new AORSBoolean(this.getValue());
  }

	public static AORSBoolean valueOf(String string) {
		return new AORSBoolean(Boolean.valueOf(string));
	}
}