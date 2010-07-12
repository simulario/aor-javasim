package aors.model.dataTypes;

public class ComplexDataType implements Cloneable {

	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch(CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
}
