package aors.module.initialStateUI.controller;

public class InitialStateUIPropertyValue implements Cloneable {
	
	private Object propertyValue;
	private boolean propertyValid = true;

	/**
	 * @param propertyValue the propertyValue to set
	 */
	public void setPropertyValue(Object propertyValue) {
		this.propertyValue = propertyValue;
	}

	/**
	 * @return the propertyValue
	 */
	public Object getPropertyValue() {
		return propertyValue;
	}

	/**
	 * @param propertyValid the propertyValid to set
	 */
	public void setPropertyValid(boolean propertyValid) {
		this.propertyValid = propertyValid;
	}

	/**
	 * @return the propertyValid
	 */
	public boolean isPropertyValid() {
		return propertyValid;
	}

	public Object clone()
	{
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
}
