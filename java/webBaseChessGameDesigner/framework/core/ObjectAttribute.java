package webBaseChessGameDesigner.framework.core;

/**
 * @author  Ferry To
 */
public class ObjectAttribute 
{
	String value;
	final CommonAttributeType type;
	
	public ObjectAttribute(CommonAttributeType type, String value)
	{
		this.type = type;
		this.value = value;
	}
	
	/**
	 * @return  the type
	 * @uml.property  name="type"
	 */
	public CommonAttributeType getType() { return type; }
	/**
	 * @return  the value
	 * @uml.property  name="value"
	 */
	public String getValue() { return value; }
}
