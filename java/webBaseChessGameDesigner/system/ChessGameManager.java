package webBaseChessGameDesigner.system;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import webBaseChessGameDesigner.framework.core.Chess;

// WARNING: remember to add mechanism to block assignPlayerSlot after game start.

/**
 * @author  Ferry To
 */
public class ChessGameManager 
{
	protected static final ConnectionManager connManager = ConnectionManager.getManager();
	protected static int gameID = ServerConfiguration.getNumericParameter("InitGameID");
	protected final Hashtable<Integer, ChessGame> games;
	protected static ChessGameManager gameManager;	
	
	public static ChessGameManager getManager()
	{
		if (gameManager == null) 
			gameManager = new ChessGameManager();
		return gameManager;
	}
	
	protected ChessGameManager()
	{
		games = new Hashtable<Integer, ChessGame>(10);
	}
	
	protected int getNextGameID()
	{ 
		// No game is allowed to have ID equal to or smaller than ZERO.
		if(gameID < 0) gameID = 0;
		// ++0 = 1 <- the first game ID must be 1.
		return ++gameID;
	}
	
	public int buildGame(String gameName, String gameDesc, String creator, String fileName)
	{
		ChessBuilder builder = ChessBuilder.getBuilder();
		Chess chess = builder.parseChessSpecification(fileName);
			
		if(chess != null)
		{
			// Add serialized specification to hashtable.
			int gID = getNextGameID();
			ChessGame game = new ChessGame(gID, gameName, gameDesc, creator, chess);
			games.put(gID, game);
			return gID;
		}
		else
			return 0; // Chess ID will always start from 1 (from ++0), so getting 0 means error occur.
	}

	public boolean removeGame(ChessGame game)
	{
		synchronized (games)
		{
			if(games.containsKey(game.getID()))
			{
				games.remove(game.getID());
				return true;
			}
			else
				return false;
		}
	}
	
	public boolean removeGame(int gameID)
	{
		synchronized (games)
		{
			if(games.containsKey(gameID))
			{
				games.remove(gameID);
				return true;
			}
			else
				return false;
		}
	}
	
	public ChessGame getGame(int gameID)
	{
		return games.get(gameID);
	}
	
	public Enumeration<Integer> getGameIDs()
	{
		return games.keys();
	}
	
	public void notifyJoinEvent(ChessGame game, int connID)
	{
		Connection conn = connManager.getConnection(connID);
		String userName = conn.getUsername();
		String gameName = game.getName();
		String slotName = game.getPlayerIDFromConnID(connID);
		String msg = userName + " has joined game "+gameName;
		Messager.printMsg("GameManager", msg);
		String responseMsg = "<join><slot>"+slotName+"</slot><nickname>";
		responseMsg += (userName+"</nickname></join><msg>"+msg+"</msg>");
		responseMsg = Messager.responseMsg("game_event", responseMsg);
		connManager.multicastMessage(game.getConnectionIDList(), responseMsg);
	}	
	
	public void notifyQuitEvent(ChessGame game, int connID)
	{
		Connection conn = connManager.getConnection(connID);
		String msg = conn.getUsername() + " has left game "+game.getName();
		Messager.printMsg("GameManager", msg);
		String responseMsg = "<quit><slot></slot><nickname></nickname></quit>";
		responseMsg += "<msg>"+msg+"</msg>";
		responseMsg = Messager.responseMsg("game_event", responseMsg);
		connManager.multicastMessage(game.getConnectionIDList(), responseMsg);		
	}
	
	public void notifyStartEvent(ChessGame game)
	{
		String msg = "Game "+game.getName() + " started.";
		Messager.printMsg("GameManager", msg);
		String responseMsg = Messager.responseMsg("game_event", "<start/><msg>"+msg+"</msg>");
		connManager.multicastMessage(game.getConnectionIDList(), responseMsg);	
	}
	
	public void notifyTerminateEvent(ChessGame game)
	{
		String msg = "Game "+game.getName() + " terminated.";
		Messager.printMsg("GameManager", msg);
		String responseMsg = Messager.responseMsg("game_event", "<terminate/><msg>"+msg+"</msg>");
		connManager.multicastMessage(game.getConnectionIDList(), responseMsg);	
	}
	
	public void notifyTurnEvent(ChessGame game)
	{
		int connID = game.getCurrentPlayerConnection().getConnID();
		String slotName = game.getPlayerIDFromConnID(connID);
		String currentPlayer = game.getCurrentPlayerConnection().getUsername();
		String msg = "It is player "+currentPlayer+" turn of game "+ game.getName() + ".";
		Messager.printMsg("GameManager", msg);
		// should use messager to format message?
		String responseMsg = "<turn><slot>"+slotName+"</slot>";
		responseMsg += "<nickname>"+currentPlayer+"</nickname></turn><msg>"+msg+"</msg>";
		responseMsg = Messager.responseMsg("game_event", responseMsg);
		connManager.multicastMessage(game.getConnectionIDList(), responseMsg);	
	}
		
	public void notifyWinEvent(ChessGame game, String who)
	{
		String winner = game.getCurrentPlayerConnection().getUsername();
		String msg = winner +" has won the game "+game.getName() + ".";
		Messager.printMsg("GameManager", msg);
		String responseMsg = "<win>"+who+"</win><msg>"+msg+"</msg>";
		responseMsg = Messager.responseMsg("game_event", responseMsg);
		connManager.multicastMessage(game.getConnectionIDList(), responseMsg);
		
	}
	
	public void notifyLoseEvent(ChessGame game, String who)
	{
		String winner = game.getCurrentPlayerConnection().getUsername();
		String msg = winner +" has lost the game "+game.getName() + ".";
		Messager.printMsg("GameManager", msg);
		String responseMsg = "<lose>"+who+"</lose><msg>"+msg+"</msg>";
		responseMsg = Messager.responseMsg("game_event", responseMsg);
		connManager.multicastMessage(game.getConnectionIDList(), responseMsg);
		
	}
}
// <wbcgdp_response type="game_event"><win>who</win><msg></msg></wbcgdp>
// <wbcgdp_response type="game_event"><turn>who</turn><msg></msg></wbcgdp>
// <wbcgdp_response type="game_event"><terminate></terminate><msg></msg></wbcgdp>
// <wbcgdp_response type="game_event"><start></start><msg></msg></wbcgdp>
// <wbcgdp_response type="game_event"><quit>who</quit><msg></msg></wbcgdp>
// <wbcgdp_response type="game_event"><join><slot>#PLAYER_?#</slot><nickname>who</nickname></join><msg></msg></wbcgdp>
// <wbcgdp_response type="game_event"><action><msg></msg></action></wbcgdp>
// init, waiting , start ,end
// turn wait, turn play
// 
// in join_result, attach current joined player information in response message
// for game event turn, if value in <turn> tag == player in flash player, that
// flash player should enable player to select OBJ and TILE.
// if not, keep freezing the chess game interface until that player turn.
// one final question: how to distinguish pre and post?
// P.S. seems server need to whole the moveable region for checking whether the 
// post move destination vaild.
// YEAH! if pre: 
// <wbcgdp_command type="game_command">
//	<type>PRE</type>
//  <obj>objID</obj>
// </wbcgdp_command>
// if post: 
// <wbcgdp_command type="game_command">
//	<type>POST</type>
//	<obj>objID</obj>
//	<tile>tileID</tile>
// </wbcgdp_command>
// SO! flash client should not let player to click a tile. After the flash client
// got the moveable regions from server, then enable those tiles to become clickable.
/*
 * <preMove><tile>R1C1</tile><tile>R1C1</tile><tile>R1C1</tile></preMove>
 * 
 * <move><obj></obj><tile></tile></move>
 * <remove><obj></obj></remove>
 * <place><obj></obj><tile></tile></place>
 * 
 */