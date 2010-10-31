package aors.model.dataTypes;

public final class AORSBoolean {

  public static Boolean clone(Boolean original) {
		return Boolean.valueOf(original);
  }

	public static Boolean valueOf(String string) {
		if(string == null || "".equals(string.trim())) {
			return false;
		}
		return Boolean.valueOf(string);
	}

	public static String toString(Boolean value) {
		return value.toString();
	}
}