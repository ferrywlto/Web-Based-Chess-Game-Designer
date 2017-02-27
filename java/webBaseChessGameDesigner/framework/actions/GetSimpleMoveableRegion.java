package webBaseChessGameDesigner.framework.actions;

import webBaseChessGameDesigner.framework.core.Action;
import webBaseChessGameDesigner.framework.core.ChessObject;
import webBaseChessGameDesigner.framework.core.Tile;
import webBaseChessGameDesigner.framework.managers.GeneralResourceManager;
import webBaseChessGameDesigner.framework.managers.ResultManager;
import webBaseChessGameDesigner.framework.types.NeighbourDirection;

public class GetSimpleMoveableRegion extends Action 
{

	public GetSimpleMoveableRegion (GeneralResourceManager manager, String[] args)
	{
		super(manager,args);
		for(int i=0; i<args.length; i++)
		{
			System.out.println(args[i]);
		}
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
				rMan.addPreMoveRegion(northTile.getID());
		if(southTile != null)
				rMan.addPreMoveRegion(southTile.getID());
		if(eastTile != null)
				rMan.addPreMoveRegion(eastTile.getID());
		if(westTile != null)
				rMan.addPreMoveRegion(westTile.getID());
	}
}
