package webBaseChessGameDesigner.framework.core;

import java.util.Hashtable;

/**
 * @author  Ferry To
 */
public class Region 
{
	final String ID;
	Player owner;
	ChessMap map;
	RegionType type;
	Hashtable<String, Tile> tiles;
	
	public Region(String id, RegionType type, Player owner, ChessMap map)
	{
		this.ID = id;
		this.type = type;
		this.owner = owner;
		this.map = map;
		this.tiles = new Hashtable<String, Tile>();
	}
	
	/**
	 * @return  the owner
	 * @uml.property  name="owner"
	 */
	public Player getOwner() {return this.owner;}
	/**
	 * @return  the type
	 * @uml.property  name="type"
	 */
	public RegionType getType() {return this.type;}
	
	public boolean addTile(Tile tile)
	{
		if(!tiles.containsKey(tile.getID()))
		{
			tiles.put(tile.getID(), tile);
			tile.setRegion(this);
			return true;
		}
		else
			return false;
	}
	
	public Tile getTile(String id)
	{
		return tiles.get(id);
	}
	
	public boolean delTile(String ID)
	{
		if(!tiles.containsKey(ID))
			return false;
		else
		{
			tiles.remove(ID);
			return true;
		} 
	}
}
