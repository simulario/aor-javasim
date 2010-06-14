package aors.model.dataTypes;

public final class AORSInteger extends AORSDatatype<Long> {

  public AORSInteger(long value) {
    super(value);
  }

  @Override
  public AORSInteger clone() {
    return new AORSInteger(this.getValue());
  }

	public static AORSInteger valueOf(String string) {
		return new AORSInteger(Long.valueOf(string));
	}
}