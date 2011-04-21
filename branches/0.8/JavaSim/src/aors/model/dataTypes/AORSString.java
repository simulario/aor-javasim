package aors.model.dataTypes;

public final class AORSString {

  public static String clone(String original) {
    return new String(original);
  }

	public static String valueOf(String string) {
		return AORSString.clone(string);
	}

	public static String toString(String value) {
		return AORSString.clone(value);
	}
}