package aors.model.dataTypes;

public final class AORSInteger {

  public static Long clone(Long original) {
    return Long.valueOf(original);
  }

	public static Long valueOf(String string) {
		if(string == null || "".equals(string.trim())) {
			return 0L;
		}
		return Long.valueOf(string);
	}

	public static String toString(Long value) {
		return value.toString();
	}
}