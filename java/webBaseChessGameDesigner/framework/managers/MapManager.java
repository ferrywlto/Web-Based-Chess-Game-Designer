package webBaseChessGameDesigner.framework.managers;

import java.util.Hashtable;

import webBaseChessGameDesigner.framework.core.ChessMap;
import webBaseChessGameDesigner.framework.core.Player;
import webBaseChessGameDesigner.framework.core.Region;
import webBaseChessGameDesigner.framework.core.RegionType;
import webBaseChessGameDesigner.framework.core.Tile;
import webBaseChessGameDesigner.framework.types.TileType;

/*
 *	This class is responsible for managing tiles, map, regions, and regionTypes 
 */
/**
 * @author  Ferry To
 */
public class MapManager 
{
	GeneralResourceManager master;
	ChessMap map;
	Hashtable<String, Region>		regions;
	Hashtable<String, RegionType> 	regTypes;
	
	public MapManager(GeneralResourceManager manager)
	{
		master = manager;
		map = new ChessMap();
		// Since a map may not have any region, 
		// therefore the initial side of the hashtable is set to 0; 
		regions = new Hashtable<String, Region>(0);
		regTypes = new Hashtable<String, RegionType>(0);
	}
	
	public Region getRegion(String ID) 
	{
		return regions.get(ID); 
	}
	public RegionType getRegionType(String ID) 
	{
		return regTypes.get(ID); 
	}
	public Tile getTile(String tileID) 
	{ 
		return map.getTile(tileID); 
	}
	
	public boolean addRegionType(String id)
	{
		if(regTypes.containsKey(id))
		{
			return false;
		}
		else
		{
			regTypes.put(id, new RegionType(id));
			return true;
		}
	}
	
	public boolean addRegion(String regID, String typeID, String playerID)
	{
		if(regions.containsKey(regID))
			return false;
		else
		{
			Player player = master.getPlayerManager().getPlayer(playerID);
			RegionType regType = getRegionType(typeID);
			regions.put(regID, new Region(regID,regType,player,map));
			
			// Player will be NULL for #PLAYER_NETURAL#
			if(player!=null) player.addOwnedRegionID(regID);
			
			return true;
		}
	}
	
	public boolean delRegionType(String id)
	{	// throw null pointer exception if ID is null
		if(!regTypes.containsKey(id))
			return false;
		else
		{
			regTypes.remove(id);
			return true;
		}
	}
   	public boolean delRegion(String id)
	{	// throw null pointer exception if ID is null
		if(!regions.containsKey(id))
			return false;
		else
		{
			regions.remove(id);
			return true;
		}
	}
	// For extenisibility, subclass of ChessMap can
	// override this method and supply with new map construction
	// algorithm to support more map types.
	/*
	 * Here is an example to implement the idea in subclass:
	 * public void construct(TileType type)
	 * {
	 * 		if(type != TileType.GRID || type != TileType.HEX || type != TileType.LINEAR) 
	 * 		{	super.construct(type); }
	 * 		else
	 * 		{ 
	 * 			// your own code //
	 * 			constructSomeMap();
	 * 		}
	 * }
	 */
	public boolean construct(TileType type,String[] param)
	{
		try
		{
			if(type == TileType.GRID)
			{
				int row = Integer.parseInt(param[0]);
				int col = Integer.parseInt(param[1]);
				map.constructGridMap(row, col);
				return true;
			}
			else if(type == TileType.HEX)
			{
				map.constructHexMap();
				return true;
			}
			else if(type == TileType.LINEAR)
			{
				int length = Integer.parseInt(param[0]);
				boolean isCircular = Boolean.parseBoolean(param[1]);
				map.constructLinearMap(length,isCircular);
				return true;
			}
			else
			{
				throw new Exception("Invaild Parameter!");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	// Overloaded method for easier parsing
	public boolean construct(String type, String[] param)
	{
		try
		{
			if(type.equalsIgnoreCase("grid"))
			{
				int row = Integer.parseInt(param[0]);
				int col = Integer.parseInt(param[1]);
				map.constructGridMap(row, col);
				return true;
			}
			else if(type.equalsIgnoreCase("linear"))
			{
				int length = Integer.parseInt(param[0]);
				boolean isCircular = Boolean.parseBoolean(param[1]);
				map.constructLinearMap(length,isCircular);
				return true;
			}
			else if(type.equalsIgnoreCase("hex"))
			{
				map.constructHexMap();
				return true;
			}
			else
			{
				throw new Exception("Invaild Parameter!");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
}
