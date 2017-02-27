package webBaseChessGameDesigner.framework.conditions;

import webBaseChessGameDesigner.framework.core.Condition;
import webBaseChessGameDesigner.framework.core.Tile;
import webBaseChessGameDesigner.framework.managers.GeneralResourceManager;

public class IfTileEmpty extends Condition
{
	public IfTileEmpty(GeneralResourceManager manager, String[] args)
	{
		super(manager,args);
	}
	public boolean vaildate()
	{
		String tileID = params[0];
		Tile tile = manager.getMapManager().getTile(tileID);
		int count = tile.getObjectCount();
		if(count == 0)
			return true;
		else
			return false;
	}
}
