package webBaseChessGameDesigner.framework.actions;

import webBaseChessGameDesigner.framework.core.Action;
import webBaseChessGameDesigner.framework.core.ChessObject;
import webBaseChessGameDesigner.framework.core.Region;
import webBaseChessGameDesigner.framework.core.Tile;
import webBaseChessGameDesigner.framework.managers.GeneralResourceManager;
import webBaseChessGameDesigner.framework.managers.ResultManager;
import webBaseChessGameDesigner.framework.types.NeighbourDirection;

public class GetSimpleMoveableRegionExcept extends Action
{
	String[] regions;
	
	public GetSimpleMoveableRegionExcept (GeneralResourceManager manager, String[] args)
	{
		super(manager,args);
		this.regions = args;
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
		
		if(regions!=null)
		{
			if(northTile != null)
				if(!inRegion(northTile))
					rMan.addPreMoveRegion(northTile.getID());
			if(southTile != null)
				if(!inRegion(southTile))
					rMan.addPreMoveRegion(southTile.getID());
			if(eastTile != null)
				if(!inRegion(eastTile))
					rMan.addPreMoveRegion(eastTile.getID());
			if(westTile != null)
				if(!inRegion(westTile))
					rMan.addPreMoveRegion(westTile.getID());
		}
	}
	public boolean inRegion(Tile tile)
	{
		Region region = tile.getRegion();
		if(region == null) return false;
		else
		{
			String regionTypeStr = region.getType().getID();
			for(int i=0; i<regions.length; i++)
			{
				if(regions[i].equals(regionTypeStr))
					return true;
			}
			return false;
		}
	}
}
