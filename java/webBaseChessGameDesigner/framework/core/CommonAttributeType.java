package webBaseChessGameDesigner.framework.core;

import webBaseChessGameDesigner.framework.types.AttributeDataType;

/**
 * @author  Ferry To
 */
public class CommonAttributeType 
{
	final String ID;
	final AttributeDataType dataType;
	
	public CommonAttributeType(String id, AttributeDataType datatype)
	{
		this.ID = id;
		this.dataType = datatype;
	}
	
	/**
	 * @return  the iD
	 * @uml.property  name="iD"
	 */
	public String getID(){return ID;}
	public AttributeDataType getType() {return dataType;}
}
