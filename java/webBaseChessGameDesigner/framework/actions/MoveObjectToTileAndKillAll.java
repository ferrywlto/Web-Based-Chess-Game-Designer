package webBaseChessGameDesigner.framework.actions;


import java.util.Iterator;

import webBaseChessGameDesigner.framework.core.Action;
import webBaseChessGameDesigner.framework.core.ChessObject;
import webBaseChessGameDesigner.framework.core.Player;
import webBaseChessGameDesigner.framework.core.Tile;
import webBaseChessGameDesigner.framework.managers.GeneralResourceManager;
import webBaseChessGameDesigner.framework.managers.MapManager;
import webBaseChessGameDesigner.framework.managers.ObjectManager;
import webBaseChessGameDesigner.framework.managers.PlayerManager;
import webBaseChessGameDesigner.framework.managers.ResultManager;

public class MoveObjectToTileAndKillAll extends Action 
{

	public MoveObjectToTileAndKillAll (GeneralResourceManager manager, String[] args)
	{
		super(manager,args);
	}
	
	public void execute()
	{
		ResultManager resultM = manager.getResultManager();
		ObjectManager objM = manager.getObjectManager();
		MapManager mapM = manager.getMapManager();
		PlayerManager playerM = manager.getPlayerManager();
		ChessObject curObj = objM.getObject(resultM.getSelectedObj());
		Tile curTile = mapM.getTile(resultM.getSelectedTile());
		Player curPlayer = playerM.getPlayer(resultM.getCurrentPlayer());
		
		//obj.getOwner().delOwnedObjectID(obj.getID());
		//obj.getLocation().delObject(obj.getID());
		// Kill all chess objects which are not belongs to current player
		Iterator<ChessObject> it = curTile.getAllObjects();
		while(it.hasNext())
		{
			ChessObject objD = it.next();
			Player owner = objD.getOwner();
			if(owner.getID() != curPlayer.getID()) 
			{
				owner.delOwnedObjectID(objD.getID());
				objD.getLocation().delObject(objD.getID());
				// [RESULT]
				//resultM.addRemoveActionResponse(objD.getID());
			}
		}
		curTile.addObject(curObj);
		curObj.setLocation(curTile);
		// [RESULT]
		//resultM.addMoveActionResponse(curObj.getID(),curTile.getID());
	}
}

