package aors.model.dataTypes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class AORSDate extends AORSDatatype<Date> {

  public AORSDate(Date value) {
    super(value);
  }

  @Override
  public AORSDate clone() {
    return new AORSDate((Date)(this.getValue().clone()));
  }

	public static AORSDate valueOf(String string) {
		try {
			return new AORSDate(new SimpleDateFormat("YYYY-MM-dd(Z|z)?").parse(string));
		} catch(ParseException e) {
			return null;
		}
	}
}