package aors.model.dataTypes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class AORSDate {

	private static final String dateFormatPattern = "YYYY-MM-dd(Z|z)?";

  public static Date clone(Date original) {
    return (Date)original.clone();
  }

	public static Date valueOf(String string) {
		try {
			return new SimpleDateFormat(dateFormatPattern).parse(string);
		} catch(ParseException e) {
			return null;
		}
	}

	public static String toString(Date value) {
		return new SimpleDateFormat(dateFormatPattern).format(value);
	}
}