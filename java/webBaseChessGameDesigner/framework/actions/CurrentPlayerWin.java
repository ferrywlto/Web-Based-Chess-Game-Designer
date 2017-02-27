package webBaseChessGameDesigner.framework.actions;

import webBaseChessGameDesigner.framework.core.Action;
import webBaseChessGameDesigner.framework.managers.GeneralResourceManager;
import webBaseChessGameDesigner.framework.managers.ResultManager;

public class CurrentPlayerWin extends Action {

	public CurrentPlayerWin(GeneralResourceManager manager, String[] args)
	{
		super(manager,args);
	}
	
	public void execute() 
	{
		ResultManager resultM = manager.getResultManager();
		resultM.addTurnActionWin(resultM.getCurrentPlayer());
		
		// problem:
		// by calling setPlayerWin, seems the RuleComponents followed by this one will not 
		// run and this seems as a problem.
	}

}
