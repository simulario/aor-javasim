package aors.model.dataTypes;

import java.util.Date;

public final class AORSDate extends AORSDatatype<Date> {

  public AORSDate(Date value) {
    super(value);
  }

  @Override
  public AORSDate clone() {
    return new AORSDate((Date)(this.getValue().clone()));
  }
}