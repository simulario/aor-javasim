package aors.model.dataTypes;

public final class AORSInteger {

  public static Long clone(Long original) {
    return Long.valueOf(original);
  }

	public static Long valueOf(String string) {
		return Long.valueOf(string);
	}

	public static String toString(Long value) {
		return value.toString();
	}
}