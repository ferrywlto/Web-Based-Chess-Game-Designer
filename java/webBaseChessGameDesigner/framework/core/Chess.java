package webBaseChessGameDesigner.framework.core;

import webBaseChessGameDesigner.framework.managers.GeneralResourceManager;

/**
 * @author  Ferry To
 */
public class Chess 
{
	final GeneralResourceManager manager;
	final String chessID;
	
	final String spec;
	
	// It is a very special case that a rule is not final due to the 
	// XML parsing sequence problem. (Try to fix it later)
	Rule turnRule;
	
	public Chess(String id, String spec)
	{
		this.chessID = id;
		this.spec = spec;
		this.manager = new GeneralResourceManager(this);
	}
	
	public String getSpecification()
	{
		return this.spec;
	}
	
	public void executeTurnRule()
	{
		this.turnRule.execute();
	}
	
	public void setTurnRule(String ruleID)
	{
		try
		{
			Rule rule = manager.getRuleManager().getRule(ruleID);
			if(rule != null)
				turnRule = rule;
			else
				throw new Exception("Turn Rule Cannot be null.");
		} 
		catch (Exception e) {e.printStackTrace();}
	}
	
	public GeneralResourceManager getGRM(){return manager;}
}
