package aors.module.initialStateUI.controller;

public class ObjektUpdate {
	private String typeName;
	private UpdateType updateType;
	private Long instanceID;
	private String updatedPropertyName; //Only in case of Edit

	public ObjektUpdate(UpdateType updateType ,String typeName , Long instanceID)
	{
		this.updateType = updateType;
		this.typeName = typeName;
		this.instanceID = instanceID;
		this.updatedPropertyName = null;
		
	}
	
	public ObjektUpdate(UpdateType updateType ,String typeName , Long instanceID , String updatedPropertyName)
	{
		this.updateType = updateType;
		this.typeName = typeName;
		this.instanceID = instanceID;
		this.updatedPropertyName = updatedPropertyName;
		
	}
	
	
	/**
	 * @param typeName the typeName to set
	 */
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	/**
	 * @return the typeName
	 */
	public String getTypeName() {
		return typeName;
	}

	/**
	 * @param updateType the updateType to set
	 */
	public void setUpdateType(UpdateType updateType) {
		this.updateType = updateType;
	}

	/**
	 * @return the updateType
	 */
	public UpdateType getUpdateType() {
		return updateType;
	}

	/**
	 * @param instanceID the instanceID to set
	 */
	public void setInstanceID(Long instanceID) {
		this.instanceID = instanceID;
	}

	/**
	 * @return the instanceID
	 */
	public Long getInstanceID() {
		return instanceID;
	}

	/**
	 * @param updatedPropertyName the updatedPropertyName to set
	 */
	public void setUpdatedPropertyName(String updatedPropertyName) {
		this.updatedPropertyName = updatedPropertyName;
	}

	/**
	 * @return the updatedPropertyName
	 */
	public String getUpdatedPropertyName() {
		return updatedPropertyName;
	}
	

}
