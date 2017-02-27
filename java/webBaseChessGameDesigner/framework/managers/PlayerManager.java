package webBaseChessGameDesigner.framework.managers;


import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Set;

import webBaseChessGameDesigner.framework.core.Player;

/**
 * @author  Ferry To
 */
public class PlayerManager 
{
	GeneralResourceManager master;
	Hashtable<String, Player> players;
	Hashtable<String, Integer> connMap;
	
	public PlayerManager(GeneralResourceManager manager)
	{
		master = manager;
		players = new Hashtable<String, Player>(0);
		connMap = new Hashtable<String, Integer>(0);
	}
	
	// This method only used for internal use such as the chess builder
	public boolean addPlayer(String id)
	{
		if(players.containsKey(id))
		{
			return false;
		}
		else
		{
			//try{
				players.put(id,new Player(id));
			//}
			//catch(UnknownHostException uhe){uhe.printStackTrace();}
			return true;
		}
	}

	// This is a method to return player ID like #PLAYER_1#, #PLAYER_2#, etc.
	public String[] getPlayerStrings()
	{
		Set<String> keys = players.keySet();
		return keys.toArray(new String[keys.size()]);
	}
	
	// [IMPORTANT]
	// #PLAYER_NETURAL# is expected a NULL
	// therefore getPlayer("#PLAYER_NETURAL#") will be null
	// Rule designer should be aware of this.
	public Player getPlayer(String id)
	{
		return players.get(id);
	}
	
	// This method used to get all connection ID which
	// registered to the created chess object.
	// those keys are required for boardcasting chess game
	// information during the game play.
	public Enumeration<Integer> getAllConnectionID()
	{
		return connMap.elements();
	}
}
