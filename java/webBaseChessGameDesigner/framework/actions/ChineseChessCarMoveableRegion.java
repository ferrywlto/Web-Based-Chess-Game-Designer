package webBaseChessGameDesigner.framework.actions;

import webBaseChessGameDesigner.framework.core.Action;
import webBaseChessGameDesigner.framework.core.ChessObject;
import webBaseChessGameDesigner.framework.core.Region;
import webBaseChessGameDesigner.framework.core.Tile;
import webBaseChessGameDesigner.framework.managers.GeneralResourceManager;
import webBaseChessGameDesigner.framework.managers.ResultManager;
import webBaseChessGameDesigner.framework.types.NeighbourDirection;

public class ChineseChessCarMoveableRegion extends Action
{
	String[] regions;
	
	public ChineseChessCarMoveableRegion (GeneralResourceManager manager, String[] args)
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
			while(northTile != null)
			{
				if(northTile.getObjectCount()==0 && !inRegion(northTile))
				{	
					rMan.addPreMoveRegion(northTile.getID());
					northTile = northTile.getNeighbour(NeighbourDirection.NORTH);
				}
				else
					break;
			}
			while(southTile != null)
			{
				if(southTile.getObjectCount()==0 && !inRegion(southTile))
				{	
					rMan.addPreMoveRegion(southTile.getID());
					southTile = southTile.getNeighbour(NeighbourDirection.SOUTH);
				}
				else
					break;
			}
			while(eastTile != null)
			{
				if(eastTile.getObjectCount()==0 && !inRegion(eastTile))
				{
					rMan.addPreMoveRegion(eastTile.getID());
					eastTile = eastTile.getNeighbour(NeighbourDirection.EAST);
				}
				else
					break;
			}
			while(westTile != null)
			{
				if(westTile.getObjectCount()==0 && !inRegion(westTile))
				{
					rMan.addPreMoveRegion(westTile.getID());
					westTile = westTile.getNeighbour(NeighbourDirection.WEST);
				}
				else
					break;
			}
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
