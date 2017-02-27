package webBaseChessGameDesigner.framework.core;

import java.util.Hashtable;
import java.util.Iterator;

import webBaseChessGameDesigner.framework.types.NeighbourDirection;
import webBaseChessGameDesigner.framework.types.TileType;

/**
 * @author  Ferry To
 */
public class Tile 
{
	// These three attributes cannot modify after it is initialized
	final String ID;
	final ChessMap map;
	final TileType type;
	
	Region region;
	Hashtable<String, ChessObject> objects;
	Hashtable<NeighbourDirection, Tile>	neighbours;
	
	
	public Tile(String id, TileType type, ChessMap map)
	{
		this.ID = id;
		this.map = map;
		this.type = type;

		this.region = null;
		this.objects = new Hashtable<String, ChessObject>(0);
		this.neighbours = new Hashtable<NeighbourDirection, Tile>(0);
	}
	
	/**
	 * @param region  the region to set
	 * @uml.property  name="region"
	 */
	public void setRegion(Region region)
	{
		this.region = region;
	}
	
	public Tile getNeighbour(NeighbourDirection direction)
	{
		return neighbours.get(direction);
	}
	
	public void setNeighbour(NeighbourDirection direction, Tile tile)
	{
		// Replace the old value of that key with new value
		// Add a new entry if that key doesn't exists
		neighbours.put(direction,tile);
	}
	
	public boolean delObject(String objID)
	{
		if(objects.containsKey(objID))
		{
			objects.remove(objID);
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public boolean addObject(ChessObject obj)
	{
		if(objects.containsKey(obj.getID()))
		{
			return false;
		}
		else
		{
			objects.put(obj.getID(), obj);
			return true;
		}
	}
	
	public ChessObject getObjectByID(String ID)
	{
		if(objects.containsKey(ID))
		{
			return objects.get(ID);
		}
		else
		{
			return null;
		}
	}
	
	public int getObjectCount()
	{
		return objects.values().size();
	}
	
	public Iterator<ChessObject> getAllObjects()
	{
		return objects.values().iterator();
	}
	
	public void delAllObjects()
	{
		objects.clear();
	}
	/**
	 * @return  the iD
	 * @uml.property  name="iD"
	 */
	public String getID()
	{
		return this.ID;
	}

	/**
	 * @return  the map
	 * @uml.property  name="map"
	 */
	public ChessMap getMap()
	{
		return this.map;
	}
	
	/**
	 * @return  the type
	 * @uml.property  name="type"
	 */
	public TileType getType()
	{
		return this.type;
	}

	public Region getRegion() {
		return region;
	}
}
