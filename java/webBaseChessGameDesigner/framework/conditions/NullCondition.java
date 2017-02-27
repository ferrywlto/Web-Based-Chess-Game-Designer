package webBaseChessGameDesigner.framework.conditions;

import webBaseChessGameDesigner.framework.core.Condition;
import webBaseChessGameDesigner.framework.managers.GeneralResourceManager;

public class NullCondition extends Condition
{
	public NullCondition(GeneralResourceManager manager, String[] args)
	{
		super(manager,args);
	}
	public boolean vaildate(){return true;}
}
