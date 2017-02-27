package webBaseChessGameDesigner.system;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import webBaseChessGameDesigner.framework.core.Chess;
import webBaseChessGameDesigner.framework.core.ChessObject;
import webBaseChessGameDesigner.framework.core.Player;
import webBaseChessGameDesigner.framework.core.Tile;
import webBaseChessGameDesigner.framework.managers.ResultManager;
import webBaseChessGameDesigner.framework.managers.RuleManager;
import webBaseChessGameDesigner.framework.types.RuleType;
import webBaseChessGameDesigner.system.state.ConnectionState;
import webBaseChessGameDesigner.system.state.GameState;

/**
 * @author  Ferry To
 */
public class ChessGame 
{
	/**
	 * @author  Ferry To
	 */
	protected final int gameID;
	protected final String gameName;
	protected final String gameDesc;
	protected final String gameCreator;
	
	protected final Chess chess;
	protected final static ChessGameManager gameManager = ChessGameManager.getManager();
	protected final static ConnectionManager connManager = ConnectionManager.getManager();
	
	// HashMap used instead of Hashtable because of the values need to be null sometimes.
	protected final HashMap<String, Integer> connSlots;
	
	protected GameState state;
	protected String currentPlayerStr;
	protected Connection currentPlayerConnection;
	protected String selectedObjID;
	protected String[] selectableTiles;
	protected boolean commandLock;
	
	public ChessGame(int gID, String gName, String gDesc, String creator, Chess chess)
	{
		this.gameID = gID;
		this.gameName = gName;
		this.gameDesc = gDesc;
		this.gameCreator = creator;
		this.chess = chess;

		this.commandLock = false;
		this.currentPlayerStr = null;
		this.currentPlayerConnection = null;
		this.selectedObjID = null;
		this.selectableTiles = null;
		
		// Initialize connection slots with keys equal "#PLAYER_1#", "#PLAYER_2#"...etc
		String[] playerStr = this.chess.getGRM().getPlayerManager().getPlayerStrings();
		this.connSlots = new HashMap<String, Integer>(playerStr.length);
		for(int i=0; i<playerStr.length; i++)
			this.connSlots.put(playerStr[i], null);
		this.state = GameState.INIT;
	}
	/**
	 * @return  the currentPlayerConnID
	 * @uml.property  name="currentPlayerConnID"
	 */
	//protected int getCurrentPlayerConnID() { return this.currentPlayerConnID; }
	/**
	 * @param currentPlayerConnID  the currentPlayerConnID to set
	 * @uml.property  name="currentPlayerConnID"
	 */
	//protected void setCurrentPlayerConnID(int connID) { this.currentPlayerConnID = connID; }
	/**
	 * @return  the connSlots
	 * @uml.property  name="connSlots"
	 */
	protected HashMap<String, Integer> getConnSlots() {return this.connSlots; }
	/**
	 * @param state  the state to set
	 * @uml.property  name="state"
	 */
	protected void setState(GameState state) {this.state = state;}
	/**
	 * @return  the state
	 * @uml.property  name="state"
	 */
	public GameState getState() { return this.state; }
	public int getID() 			{ return this.gameID; }
	public String getName() 	{ return this.gameName; }
	public String getDesc() 	{ return this.gameDesc; }
	public String getCreator() 	{ return this.gameCreator; }	
	/**
	 * @return  the chess
	 * @uml.property  name="chess"
	 */
	public Chess getChess()	{ return this.chess; }
	
	public void winGame(String playerID)
	{
		//if(commandType == RuleType.TURN)
		//{
		//	responses.add("<win><player>"+currentGame.playerID+"</player></win>");
		//}
	}
	
	public void start()
	{
		if(this.state == GameState.WAITING)
		{
			// [GAME_STATE_TRANSITION]
			setState(GameState.STARTED);
			// Refer to KickAll() for this implementation
			Iterator<Integer> It = getConnectionIDList();
			while(It.hasNext())
			{
				int value = It.next();
				Connection conn = connManager.getConnection(value);
				// [CONN_STATE_TRANSITION]
				conn.setState(ConnectionState.PLAY_WAIT);
			}
			gameManager.notifyStartEvent(this);
			
			// Should set player1 to start
			setFirstPlayerTurn();
		}
	}
	
	public synchronized void terminate()
	{
		// [GAME_STATE_TRANSITION]
		setState(GameState.ENDED);
		kickAll();
		gameManager.removeGame(this);
		gameManager.notifyTerminateEvent(this);
		// Need to hold the connID entries before all required messages sent.
		getConnSlots().clear();	
	}

	public void executeTurn()
	{
		//setCurrentPlayerConnID(connID);
	}
	
	protected synchronized void kickAll()
	{
		Iterator<Integer> It = getConnectionIDList();
		while(It.hasNext())
		{
			// [Notice] since HashMap allow null... e.g. slot that no player joined.
			// connSlots.get(key) can be null or an integer
			// so it must ensure the object accquired is not null
			// and cast it back as an integer... <- fixed after getConnectionIDList() updated.
			int value = It.next();
			Connection conn = connManager.getConnection(value);
			// [CONN_STATE_TRANSITION]
			conn.setGameID(0);
			conn.setState(ConnectionState.LOGGED);
		}
	}
	
	public synchronized boolean quit(int connID)
	{
		Connection conn = connManager.getConnection(connID);
		GameState state = getState();
		
		// Any player left after the game started will cause the game to terminate.
		if(state == GameState.STARTED || conn.getUsername().equals(getCreator()))
		{
			terminate();
			return true;
		}
		else if(state == GameState.WAITING && connSlots.containsValue(connID))
		{
			HashMap<String,Integer> connSlots = getConnSlots();
			// Reset player slot.
			Iterator<String> it = connSlots.keySet().iterator();
			while(it.hasNext())
			{
				String key = it.next();
				if(connSlots.get(key) == connID)
				{
					connSlots.put(key, null);
					// [CONN_STATE_TRANSITION]
					// Reset connection value and state.
					conn.setGameID(0);
					conn.setState(ConnectionState.LOGGED);
					gameManager.notifyQuitEvent(this, connID);
					return true;
				}
			}
			return false;
		}
		else
			return false;
	}
	
	public synchronized boolean join(int connID)
	{
		Connection conn = connManager.getConnection(connID);
		GameState state = getState();
		
		if((state == GameState.INIT || state == GameState.WAITING) && moreSlotLeft())
		{
			if(assignPlayerSlot(connID))
			{
				// [CONN_STATE_TRANSITION]
				// Setup connection values.
				conn.setGameID(this.gameID);
				conn.setState(ConnectionState.WAITING);

				// Notify Join before start...
				gameManager.notifyJoinEvent(this, connID);
				
				// Start the game if all player joined successfully.
				if(!moreSlotLeft())
					start();
				else if(state == GameState.INIT)
					// [GAME_STATE_TRANSITION]
					setState(GameState.WAITING);
				
				return true;
			}
			else
				return false;
		}
		else
			return false;
	}
	
	// This method used to get all connection ID which
	// registered to the created chess object.
	// those keys are required for boardcasting chess game
	// information during the game play.
	// [WARNING] this method will returns an Iterator that will
	// have null if a slot don't have a player joined yet.
	// This problem has fixed by filter out all null entries and return a new Iterator
	public Iterator<Integer> getConnectionIDList()
	{
		ArrayList<Integer> list = new ArrayList<Integer>(0);
		
		Iterator<String> It = connSlots.keySet().iterator();
		while(It.hasNext())
		{
			String key = It.next();
			if(connSlots.get(key)!=null)
				list.add(connSlots.get(key));
		}
		
		return list.iterator();
	}
	
	
	// Handy method to just check whether have empty player slot.
	// Should return faster than slotsLeft();
	public boolean moreSlotLeft()
	{
		return connSlots.containsValue(null);
	}
	
	// Return the number of available slots left
	public int slotsLeft()
	{
		Set<String> keys = connSlots.keySet();
		int keySize = keys.size();
		Iterator<String> it = keys.iterator();
		while(it.hasNext())
		{
			if(connSlots.get(it.next()) != null)
				keySize--;
		}
		return keySize;		
	}
	
	// Assign a player to first available slot 
	public boolean assignPlayerSlot(int connID)
	{
		synchronized (connSlots)
		{
			Iterator<String> it = connSlots.keySet().iterator();
			while(it.hasNext())
			{			
				String key = it.next();
				if(connSlots.get(key) == null)
				{
					connSlots.put(key,connID);
					return true;
				}
			}			
		}
		return false;
	}

	// Overloadded method that assign player into specific player slot.
	public boolean assignPlayerSlot(int connID, String targetSlot)
	{
		synchronized (connSlots) 
		{
			// Only created player name that currently not assigned can be added.
			if(connSlots.containsKey(targetSlot) && connSlots.get(targetSlot)==null)
			{
				connSlots.put(targetSlot, connID);
				gameManager.notifyJoinEvent(this, connID);
				return true;
			}
			else
				return false;			
		}
	}

	public void processCommand(int connID, String type, String param)
	{
		/** 0. check whether seleced obj belongs to current player 
		// 1. remember selected obj
		// 2. get moveable tiles for obj
		// 3. remember those tiles
		// 4. send result back to client
		// 5a. if receive pre move again, repeat 1. - 4. 
		// 5b. if receive post move, check whether received tile
		// recorded already.
		// 6a. do nothing if tile is not in recorded set.
		// 6b. perform post move action if tile is in recorded set.
		// 7. clear recorded obj and tile set.
		// 8. return post move action result. 
		
		// Do nothing if:
		// 1. Command lock is on.
		// 2. Command sender is not the current player.
		// 3. Game is not started.
		// 4. Current player not in PLAY_CMD state.
		 */
		Connection pConn = connManager.getConnection(connID);
		if(pConn == null) return;
		
		if(getState() == GameState.STARTED && this.commandLock == false &&
			connID == this.currentPlayerConnection.getConnID() && 
			this.currentPlayerConnection.getState() == ConnectionState.PLAY_CMD)
		{
			//pre or post?
			// current player - player ID / username? connID?
			// selected object
			// selected tile
			
			String pID = getPlayerIDFromConnID(connID);
			
			if(pID == null) {
				pConn.sendMessage(Messager.errorMsg("No such player?"));
				return;
			}
			
			Chess chess = this.getChess();
			RuleType rType = RuleManager.getRuleType(type);
			
			Player player = chess.getGRM().getPlayerManager().getPlayer(pID);
			
			if(rType == RuleType.PREMOVE)
			{
				ChessObject obj = chess.getGRM().getObjectManager().getObject(param);
				if(obj == null || player == null) {
					pConn.sendMessage(Messager.errorMsg("Invalid Object/Player."));
					return;
				}
				
				// Impossible that obj not belongs to player
				if(player != obj.getOwner()){ 
					pConn.sendMessage(Messager.errorMsg("You are not object owner."));
					return;
				}
				
				this.selectedObjID = param;
				ResultManager rMan = chess.getGRM().getResultManager();
				
				rMan.setSelectedObj(this.selectedObjID);
				
				obj.getType().executePreMove();
				this.selectableTiles = rMan.flushPreMoveResult();
				
				// Construct back response message
				String resultStr = "<preMove>";
				// Ensure no null.
				for(int i=0; i<this.selectableTiles.length; i++)
				{
					String temp = this.selectableTiles[i];
					if(temp != null)
						resultStr += ("<tile>"+temp+"</tile>");
				}
				resultStr += "</preMove>";
				// Send result back to player
				pConn.sendMessage(Messager.responseMsg("gameCommand_result", resultStr));
			}
			else if(rType == RuleType.POSTMOVE)
			{
				ChessObject obj = chess.getGRM().getObjectManager().getObject(this.selectedObjID);
				if(obj == null || player == null) {
					pConn.sendMessage(Messager.errorMsg("Invalid Object/Player."));
					return;
				}
				
				boolean found = false;
				//System.out.println("param:"+param);
				
				for(int i=0; i<this.selectableTiles.length; i++)
				{
					//System.out.println("selectableTiles[i]:"+selectableTiles[i]);
					if(this.selectableTiles[i].equals(param))
					{
						found = true;
						break;
					}
				}
				if(found)
				{	
					ResultManager rMan = chess.getGRM().getResultManager();
					rMan.setSelectedTile(param);
					obj.getType().executePostMove();
					String[][] result = rMan.flushPostMoveResult();
					String resultStr = "";
					for(int i=0; i<result.length; i++)
					{
						resultStr += "<action>";
						if(result[i][0] == "move")
						{
							resultStr += ("<move><obj>"+result[i][1]+"</obj>");
							resultStr +=("<tile>"+result[i][2]+"</tile></move>");
						}
						else if(result[i][0] == "remove")
						{
							resultStr += ("<remove><obj>"+result[i][1]+"</obj></remove>");
						}
						resultStr += "</action>";
					}
					String output = Messager.responseMsg("game_event", resultStr);
					connManager.multicastMessage(this.getConnectionIDList(), output);
					endTurn();
				}
				else
				{
					pConn.sendMessage(Messager.errorMsg("Tile not moveable."));
					return;
				}
			}
			else
			{
				// do nothing if not these 2 types
				pConn.sendMessage(Messager.errorMsg("Invalid Command Type."));
				return;
			}
		}
		else
		{
			pConn.sendMessage(Messager.errorMsg("Please wait while command processing."));
			return;
		}
	}
	
	protected void endTurn()
	{
		ResultManager rMan = chess.getGRM().getResultManager();
		chess.executeTurnRule();
		String[][] result = rMan.flushTurnResult();
		//System.out.println(result.length);
		
		if(result.length != 0)
		{
			ArrayList<String[]> winLose = new ArrayList<String[]>(0);
			ArrayList<String[]> actions = new ArrayList<String[]>(0);
			for(int i=0; i<result.length; i++)
			{
				if(result[i][0] == "move" || result[i][0] == "remove")
					actions.add(result[i]);
				else if(result[i][0] == "win" || result[i][0] == "lose")
					winLose.add(result[i]);
			}
			winLose.trimToSize();
			actions.trimToSize();
			
			if(actions.size() != 0)
			{
				String resultStr = "";
				for(int i=0; i<actions.size(); i++)
				{
					String[] temp = actions.get(i);
					resultStr += "<action>";
					if(temp[0] == "move")
					{
						resultStr += ("<move><obj>"+result[i][1]+"</obj>");
						resultStr += ("<tile>"+result[i][2]+"</tile></move>");
					}
					else if(temp[0] == "remove")
					{
						resultStr += ("<remove><obj>"+result[i][1]+"</obj></remove>");
					}
					resultStr += "</action>";
				}
				String output = Messager.responseMsg("game_event", resultStr);
				connManager.multicastMessage(this.getConnectionIDList(), output);				
			}
			
			if(winLose.size()!=0)
			{
				boolean end = false;	
				for(int i=0; i<winLose.size(); i++)
				{
					// can more than 1 player lose, but only 1 player can win.
					String[] temp = winLose.get(i);
					if(temp[0].equals("win"))
					{
						end = true;
						gameManager.notifyWinEvent(this, temp[1]);
						break;
					}
					/*
					 * to handle more than one player lose,
					 * 1. a new state of connection needed: LOST_WAIT
					 * 2. nextPlayerTurn() have to check if the connection state == LOST_WAIT, skip that player turn.
					 * 3. set LOST_WAIT of connection in this method: endTurn();
					else
					*/
				}
				if(end)
					terminate();
				else
					nextPlayerTurn();
			}
		}
		else
			nextPlayerTurn();
	}
	
	// This method should be called only once in start()
	protected void setFirstPlayerTurn()
	{
		if(getState() == GameState.STARTED)
		{
			HashMap<String, Integer> conns = getConnSlots();
			//get first key, should be #PLAYER_1# in most cases.
			Iterator<String> keys = conns.keySet().iterator();
			if(keys.hasNext())
			{
				String key = keys.next();
				this.currentPlayerStr = key;
				this.currentPlayerConnection = connManager.getConnection(conns.get(key));
				// [CONN_STATE_TRANSITION]
				this.currentPlayerConnection.setState(ConnectionState.PLAY_CMD);
				// No need to set other player connection to PLAY_WAIT since 
				// join() did this.
				gameManager.notifyTurnEvent(this);
				// Set result manager for current player
				this.chess.getGRM().getResultManager().setCurrentPlayer(this.currentPlayerStr);
			} // Do nothing if no player at all.
		} // Do nothing if game is not started.
	}

	// This method should be called only after setFirstPlayerTurn() has called.
	protected void nextPlayerTurn()
	{
		if(getState() == GameState.STARTED)
		{
			HashMap<String, Integer> conns = getConnSlots();
			Iterator<String> keys = conns.keySet().iterator();

			// do the following if more than one player.
			while(keys.hasNext())
			{
				String key = keys.next();
				if(key == this.currentPlayerStr)
				{
					// [CONN_STATE_TRANSITION]
					// reset the previous player back to PLAY_WAIT state first.
					this.currentPlayerConnection.setState(ConnectionState.PLAY_WAIT);
					
					// if keys still have next, that means it is not the last player on list.
					// then just assign next player key current player.
					if(keys.hasNext())
					{
						this.currentPlayerStr = keys.next();
						break;
					}
					// otherwise that means the current player is the last one on list,
					// so we have to set current player back to the first player on list,
					// thats why we need to get the first key.
					else
					{
						//start from beginning to get the first key
						Iterator<String> keys2 = conns.keySet().iterator();
						this.currentPlayerStr = keys2.next();
						break;
					}
				}
			}
			// Update current player connection to next one.
			this.currentPlayerConnection = connManager.getConnection(conns.get(this.currentPlayerStr));
			// [CONN_STATE_TRANSITION]
			this.currentPlayerConnection.setState(ConnectionState.PLAY_CMD);
			
			// Release command lock finally.
			this.commandLock = false;
			
			// Notify Event
			gameManager.notifyTurnEvent(this);
			// End the process
			return;			
		} // Do nothing if no players at all.
	} // Do nothing if the game is not started.
	
	public void notifyCommandProcessCompleted(String result)
	{
		
	}
	
	protected int getConnIDFromPlayerID(String playerID)
	{
		return connSlots.get(playerID);
	}
	protected String getPlayerIDFromConnID(int connID)
	{
		Iterator<String> It = connSlots.keySet().iterator();
		while(It.hasNext())
		{
			String key = It.next();
			if(connSlots.get(key) == connID)
				return key;
		}
		return null;
	}
	public Connection getCurrentPlayerConnection() {
		return currentPlayerConnection;
	}	
	protected void setPlayerTurn()
	{		
		// 1. get the player ID that should be his turn.
		// 2. get conncection of that player ID
		// 3. set connection state of that connection to PLAY_CMD
		// 4. process cmd.
		// 5. if cmd type = postMove && process cmd = true then
		//		return result
		// 		set connection state of that connection to PLAY_WAIT
		//		set to next player turn
		//	  else if type = preMove && process cmd = true
		//		just return result
		//	  else
		//		do nothing.
	}

	

}
