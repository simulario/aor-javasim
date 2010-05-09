package aors.model.dataTypes;

public abstract class AORSDatatype<T> implements aors.util.Cloneable<AORSDatatype<T>> {
  private T value;

  public AORSDatatype(T value) {
    this.setValue(value);
  }

  public T getValue() {
    return value;
  }

  public void setValue(T value) {
    this.value = value;
  }

  @Override
  public abstract AORSDatatype<T> clone();
}
