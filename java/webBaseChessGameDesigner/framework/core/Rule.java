package webBaseChessGameDesigner.framework.core;

import webBaseChessGameDesigner.framework.managers.GeneralResourceManager;

/**
 * @author  Ferry To
 */
public final class Rule 
{
	final GeneralResourceManager manager;
	/**
	 * @uml.property  name="components"
	 * @uml.associationEnd  multiplicity="(0 -1)"
	 */
	final RuleComponent[] components;
	final String ID;
	
	public Rule(String id, GeneralResourceManager manager, RuleComponent[] components)
	{
		this.ID = id;
		this.manager = manager;
		this.components = components;
	}
	
	public void execute()
	{
		for(int i=0; i<components.length; i++)
		{
			components[i].execute();
		}
		//manager.getResultManager().flushResponse();
	}
}
