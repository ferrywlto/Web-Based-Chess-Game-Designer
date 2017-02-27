package webBaseChessGameDesigner.framework.actions;

import webBaseChessGameDesigner.framework.core.Action;
import webBaseChessGameDesigner.framework.core.ChessObject;
import webBaseChessGameDesigner.framework.core.Tile;
import webBaseChessGameDesigner.framework.managers.GeneralResourceManager;
import webBaseChessGameDesigner.framework.managers.ResultManager;
import webBaseChessGameDesigner.framework.types.NeighbourDirection;

public class GetSimpleEmptyMoveableRegion extends Action {

	public GetSimpleEmptyMoveableRegion (GeneralResourceManager manager, String[] args)
	{
		super(manager,args);
	}
	
	public void execute()
	{
		ResultManager rMan = manager.getResultManager();
		ChessObject obj = manager.getObjectManager().getObject(rMan.getSelectedObj());
			
		Tile tile = obj.getLocation();	
		Tile northTile = tile.getNeighbour(NeighbourDirection.NORTH);		
		Tile southTile = tile.getNeighbour(NeighbourDirection.SOUTH);		
		Tile westTile = tile.getNeighbour(NeighbourDirection.WEST);		
		Tile eastTile = tile.getNeighbour(NeighbourDirection.EAST);
		
		if(northTile != null)
			if(northTile.getObjectCount()==0)
				rMan.addPreMoveRegion(northTile.getID());
		if(southTile != null)
			if(southTile.getObjectCount()==0)
				rMan.addPreMoveRegion(southTile.getID());
		if(eastTile != null)
			if(eastTile.getObjectCount()==0)
				rMan.addPreMoveRegion(eastTile.getID());
		if(westTile != null)
			if(westTile.getObjectCount()==0)
				rMan.addPreMoveRegion(westTile.getID());
	}
}

