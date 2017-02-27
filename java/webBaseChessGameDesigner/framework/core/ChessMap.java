package webBaseChessGameDesigner.framework.core;

import java.util.Hashtable;
import webBaseChessGameDesigner.framework.types.NeighbourDirection;
import webBaseChessGameDesigner.framework.types.TileType;

public class ChessMap 
{
	Hashtable<String, Tile> tiles;
	
	public ChessMap()
	{
		tiles = new Hashtable<String, Tile>();
	}

	// This method will construct a square map and store in in variable tiles
	public void constructGridMap(int row, int col)
	{
		// Cautions!!
		// All previous stored tiles will be destroyed!
		tiles.clear();
		
		// Construct a new set of tiles
		for(int i=0; i<row; i++)
		{
			for(int j=0; j<col; j++)
			{
				String tID = "R"+i+"C"+j;
				tiles.put(tID, new Tile(tID,TileType.GRID,this));
			}
		}
		
		// Link up rows
		for(int i=0; i<row; i++)
		{
			for(int j=0; j<col-1; j++)
			{
				String tIDa = "R"+i+"C"+j;
				String tIDb = "R"+i+"C"+(j+1);
				Tile tmpTileA = tiles.get(tIDa);
				Tile tmpTileB = tiles.get(tIDb);
				tmpTileA.setNeighbour(NeighbourDirection.EAST, tmpTileB);
				tmpTileB.setNeighbour(NeighbourDirection.WEST, tmpTileA);
			}
		}
		
		// Link up columns
		for(int i=0; i<row-1; i++)
		{
			for(int j=0; j<col; j++)
			{
				String tIDa = "R"+i+"C"+j;
				String tIDb = "R"+(i+1)+"C"+j;
				Tile tmpTileA = tiles.get(tIDa);
				Tile tmpTileB = tiles.get(tIDb);
				tmpTileA.setNeighbour(NeighbourDirection.SOUTH, tmpTileB);
				tmpTileB.setNeighbour(NeighbourDirection.NORTH, tmpTileA);
			}
		}
	}
	// This method will construct a linear map and store it in variable tiles
	public void constructLinearMap(int length, boolean isCircular)
	{
		// Cautions!!
		// All previous stored tiles will be destroyed!
		tiles.clear();
		
		// Construct a new set of tiles
		for(int i=0; i<length; i++)
		{
			String tID = "L"+i;
			tiles.put(tID, new Tile(tID,TileType.LINEAR,this));
		}
		
		// Link up all the tiles
		for(int i=0; i<length-1; i++)
		{
			String tIDa = "L"+i;
			String tIDb = "L"+(i+1);
			Tile tmpTileA = tiles.get(tIDa);
			Tile tmpTileB = tiles.get(tIDb);
			tmpTileA.setNeighbour(NeighbourDirection.NEXT, tmpTileB);
			tmpTileB.setNeighbour(NeighbourDirection.PREV, tmpTileA);
		}
		
		// Link the head with the tail if the map is circular
		if(isCircular)
		{
			String tIDa = "L"+(length-1);
			String tIDb = "L"+0;
			Tile tmpTileA = tiles.get(tIDa);
			Tile tmpTileB = tiles.get(tIDb);
			tmpTileA.setNeighbour(NeighbourDirection.NEXT, tmpTileB);
			tmpTileB.setNeighbour(NeighbourDirection.PREV, tmpTileA);
		}
	}	

	// Reserve for later use
	public void constructHexMap(){}
	
	public Tile getTile(String ID)
	{
		return tiles.get(ID);
	}
  
	public boolean addTile(Tile tile)
	{
		if(tiles.containsKey(tile.ID))
			return false;
		else
		{
			tiles.put(tile.ID, tile);
			return true;
		}
	}
	
	public boolean delTile(String id)
	{  // Throw null pointer exception if id is null
		if(!tiles.containsKey(id))
			return false;
		else
		{
			tiles.remove(id);
			return true;
		}
	}

}
