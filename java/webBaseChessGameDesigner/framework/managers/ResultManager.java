package webBaseChessGameDesigner.framework.managers;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import webBaseChessGameDesigner.framework.core.ChessObject;
import webBaseChessGameDesigner.framework.core.Player;
import webBaseChessGameDesigner.framework.core.Tile;
import webBaseChessGameDesigner.framework.types.RuleType;
import webBaseChessGameDesigner.system.ChessGame;

/**
 * @author  Ferry To
 */
public class ResultManager 
{
	GeneralResourceManager master;
	
	String currentPlayer;
	String selectedTile;
	String selectedObj;
	
	Hashtable<String,String> preMoveCache;
	ArrayList<String[]> postMoveCache;
	ArrayList<String[]> turnCache;
	
	public void setCurrentPlayer(String currentPlayer) {
		this.currentPlayer = currentPlayer;
	}

	public void setSelectedObj(String selectedObj) {
		this.selectedObj = selectedObj;
	}

	public void setSelectedTile(String selectedTile) {
		this.selectedTile = selectedTile;
	}

	public String getCurrentPlayer() {
		return currentPlayer;
	}

	public String getSelectedObj() {
		return selectedObj;
	}

	public String getSelectedTile() {
		return selectedTile;
	}
	
	public ResultManager(GeneralResourceManager manager)
	{
		master = manager;
		preMoveCache = new Hashtable<String, String>();
		postMoveCache = new ArrayList<String[]>(0);
		turnCache = new ArrayList<String[]>(0);
	}
	
	public void addPreMoveRegion(String tileID)
	{
		preMoveCache.put(tileID, tileID);
	}

	public String[] flushPreMoveResult()
	{
		// Using Hashtable ensure no duplicated tiles ID appear.
		String[] result = preMoveCache.keySet().toArray(new String[preMoveCache.keySet().size()]);
		preMoveCache.clear();
		return result;
	}
	
	public void addPostMoveActionRemove(String objID)
	{
		String[] temp = new String[2];
		temp[0] = "remove";
		temp[1] = objID;
		postMoveCache.add(temp);
	}
	
	public void addPostMoveActionMove(String objID, String tileID)
	{
		String[] temp = new String[3];
		temp[0] = "move";
		temp[1] = objID;
		temp[2] = tileID;
 		postMoveCache.add(temp);		
	}
	

	
	public String[][] flushPostMoveResult()
	{
		postMoveCache.trimToSize();
		String[][] result = postMoveCache.toArray(new String[postMoveCache.size()][]);
		postMoveCache.clear();
		return result;
	}
	
	public void addTurnActionWin(String playerID)
	{
		String[] temp = new String[2];
		temp[0] = "win";
		temp[1] = playerID;
		turnCache.add(temp);		
	}

	public void addTurnActionLose(String playerID)
	{
		String[] temp = new String[2];
		temp[0] = "lose";
		temp[1] = playerID;
		turnCache.add(temp);		
	}
	
	public void addTurnActionRemove(String objID)
	{
		String[] temp = new String[2];
		temp[0] = "remove";
		temp[1] = objID;
		turnCache.add(temp);
	}
	
	public void addTurnActionMove(String objID, String tileID)
	{
		String[] temp = new String[3];
		temp[0] = "move";
		temp[1] = objID;
		temp[2] = tileID;
		turnCache.add(temp);		
	}
	
	public String[][] flushTurnResult()
	{
		turnCache.trimToSize();
		String[][] result = turnCache.toArray(new String[turnCache.size()][]);
		turnCache.clear();
		return result;		
	}
}
/* Code no longer use */
/*

public void addGameWinResponse(String playerID)
{
	if(commandType == RuleType.TURN)
	{
		responses.add("<win><player>"+playerID+"<player></win>");
	}
}

public void addMoveableResponse(String tileID)
{
	if(commandType == RuleType.PREMOVE)
	{
		responses.add("<tile>"+tileID+"</tile>");
	}
}

public void addMoveActionResponse(String objID, String tileID)
{
	if(commandType == RuleType.POSTMOVE)
	{
		responses.add("<move><obj>"+objID+"</obj><tile>"+tileID+"</tile></move>");
	}
}

public void addRemoveActionResponse(String objID)
{
	if(commandType == RuleType.POSTMOVE)
	{
		responses.add("<remove><obj>"+objID+"</obj></remove>");
	}
}

public void flushResponse()
{
	String[] results = responses.toArray(new String[responses.size()]);
	String result = "";
	for(int i=0; i<results.length; i++)
	{
		result+=results[i];
	}
	currentGame.notifyCommandProcessCompleted(result);
}

public void clearAll()
{
	commandType = null;
	selectedObj = null;
	selectedTile = null;
	currentGame = null;
	responses.clear();
}
// an object which contains required informaton:
// such as rule type, player id, objID, tile ID
// should pass to here to set the #CURRENT_PLAYER#
// #SELECTED_OBJECT# #SELECTED_TILE#
// [NOTICE] 
// if rule type = preMove,  #SELECTED_TILE# = the position of #SELECTED_OBJECT# is from 
// if rule type = postMove, #SELECTED_TILE# = the position of #SELECTED_OBJECT# move to
public void processCommand(String pID, String type, String objID, String tileID, ChessGame game)
{
	/*
	clearAll();
	commandType = RuleManager.getRuleType(type);
	selectedObj = master.getObjectManager().getObject(objID);
	selectedTile = master.getMapManager().getTile(tileID);
	currentGame = game;
	if(commandType == RuleType.PREMOVE)
	{
		selectedObj.getType().executePreMove();
	}
	else if(commandType == RuleType.POSTMOVE)
	{
		selectedObj.getType().executePostMove();
		
	}*/
