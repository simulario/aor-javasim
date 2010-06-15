package aors.model.dataTypes;

public final class AORSFloat {

  public static Double clone(Double original) {
    return Double.valueOf(original);
  }

	public static Double valueOf(String string) {
		return Double.valueOf(string);
	}

	public static String toString(Double value) {
		return value.toString();
	}
}