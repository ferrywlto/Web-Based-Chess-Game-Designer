package webBaseChessGameDesigner.framework.actions;

import webBaseChessGameDesigner.framework.core.Action;
import webBaseChessGameDesigner.framework.core.ChessObject;
import webBaseChessGameDesigner.framework.core.Tile;
import webBaseChessGameDesigner.framework.managers.GeneralResourceManager;
import webBaseChessGameDesigner.framework.managers.MapManager;
import webBaseChessGameDesigner.framework.managers.ObjectManager;
import webBaseChessGameDesigner.framework.managers.ResultManager;

public class MoveObjectToSelectedTile extends Action 
{
	public MoveObjectToSelectedTile(GeneralResourceManager manager, String[] args)
	{
		super(manager,args);
	}
	
	public void execute()
	{
		ResultManager rMan = manager.getResultManager();
		ObjectManager oMan = manager.getObjectManager();
		MapManager mMan = manager.getMapManager(); 
		
		String objID = rMan.getSelectedObj();
		String tileID = rMan.getSelectedTile();
		
		ChessObject selectedObj = oMan.getObject(objID);
		Tile selectedTile = mMan.getTile(tileID);
		
		Tile oldTile = selectedObj.getLocation();
		oldTile.delObject(objID);
		
		selectedObj.setLocation(selectedTile);
		selectedTile.addObject(selectedObj);
		rMan.addPostMoveActionMove(objID, tileID);
	}
}
