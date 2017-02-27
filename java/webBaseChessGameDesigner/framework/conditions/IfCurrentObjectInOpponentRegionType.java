package webBaseChessGameDesigner.framework.conditions;

import webBaseChessGameDesigner.framework.core.ChessObject;
import webBaseChessGameDesigner.framework.core.Condition;
import webBaseChessGameDesigner.framework.core.Region;
import webBaseChessGameDesigner.framework.core.RegionType;
import webBaseChessGameDesigner.framework.core.Tile;
import webBaseChessGameDesigner.framework.managers.GeneralResourceManager;
import webBaseChessGameDesigner.framework.managers.ObjectManager;
import webBaseChessGameDesigner.framework.managers.ResultManager;

public class IfCurrentObjectInOpponentRegionType extends Condition{
	
	String[] regions;
	
	public IfCurrentObjectInOpponentRegionType(GeneralResourceManager manager, String[] args)
	{
		super(manager,args);
		this.regions = args;
	}
	
	public boolean vaildate()
	{
		if(this.regions == null)
			return false;
		else
		{			
			ResultManager resultM = manager.getResultManager();
			ObjectManager objM = manager.getObjectManager();
			ChessObject obj = objM.getObject(resultM.getSelectedObj());
			
			Tile tile = obj.getLocation();
			Region region = tile.getRegion();
			if(region != null)
				if(region.getOwner() != null)
				{
					RegionType rtype = region.getType();
					String rtypeStr = rtype.getID();
					for(int i=0; i<regions.length; i++)
						if(regions[i].equals(rtypeStr) && (region.getOwner()!=obj.getOwner()))
							return true;
					return false;
				}
				else
					return false;
			else
				return false;
		}
	}
}
