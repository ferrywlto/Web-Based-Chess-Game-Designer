package webBaseChessGameDesigner.framework.conditions;

import webBaseChessGameDesigner.framework.core.Condition;
import webBaseChessGameDesigner.framework.core.Player;
import webBaseChessGameDesigner.framework.managers.GeneralResourceManager;
import webBaseChessGameDesigner.framework.managers.PlayerManager;
import webBaseChessGameDesigner.framework.managers.ResultManager;

public class IfAllEnemyObjectsKilled extends Condition {

	public IfAllEnemyObjectsKilled(GeneralResourceManager manager, String[] args)
	{
		super(manager,args);
	}
	
	public boolean vaildate()
	{
		PlayerManager playerM = manager.getPlayerManager();
		ResultManager resultM = manager.getResultManager();
		Player curPlayer = playerM.getPlayer(resultM.getCurrentPlayer());
		String[] players = playerM.getPlayerStrings();
		boolean result = true;
		for(int i=0; i<players.length; i++)
		{
			if(curPlayer.getID() != players[i])
			{
				Player enemy = playerM.getPlayer(players[i]);
				if(enemy.getOwnedObjectIDs().length != 0)
				{
					result = false;
					break;
				}
			}
		}
		return result;
	}
}
