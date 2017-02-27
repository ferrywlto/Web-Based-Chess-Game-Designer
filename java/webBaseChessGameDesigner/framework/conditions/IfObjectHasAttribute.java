package webBaseChessGameDesigner.framework.conditions;

import webBaseChessGameDesigner.framework.core.Condition;
import webBaseChessGameDesigner.framework.managers.GeneralResourceManager;

public class IfObjectHasAttribute extends Condition
{
	// Specific values needed in this condition
	String attribute;
	String objID;
	
	public IfObjectHasAttribute(GeneralResourceManager manager, String[] args)
	{
		super(manager,args);
		this.objID = args[0];
		this.attribute = args[1];
	}
	
	public boolean vaildate()
	{
		if(manager.getObject(objID).getAttribute(attribute) != null)
			return true;
		else
			return false;
	}
}
