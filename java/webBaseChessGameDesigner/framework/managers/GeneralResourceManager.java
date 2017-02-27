package webBaseChessGameDesigner.framework.managers;

import webBaseChessGameDesigner.framework.core.Chess;
import webBaseChessGameDesigner.framework.core.ChessObject;
import webBaseChessGameDesigner.framework.core.Tile;

/**
 * @author    Ferry To
 * @uml.dependency   supplier="webBaseChessGameDesigner.framework.managers.MapManager.getRegion"
 */
public class GeneralResourceManager 
{
	Chess owner;
	PlayerManager playerM;
	
	ReferenceManager referenceM;
	ResultManager resultM;
	RuleManager ruleM;
	
	ObjectManager objM;

	MapManager mapM;
	
	public GeneralResourceManager(Chess owner)
	{
		this.owner = owner; 
		objM = new ObjectManager(this);
	
		playerM = new PlayerManager(this);
		referenceM = new ReferenceManager(this);
		ruleM = new RuleManager(this);

		mapM = new MapManager(this);
		resultM = new ResultManager(this);
	}
	
	public Chess			getOwnerChess()			{return owner;}
	public MapManager	 	getMapManager()   		{return mapM;}
	public RuleManager	 	getRuleManager()  		{return ruleM;}
	public PlayerManager 	getPlayerManager()		{return playerM;}
	public ResultManager 	getResultManager()		{return resultM;}
	public ObjectManager 	getObjectManager()		{return objM;}
	public ReferenceManager getReferenceManager() 	{return referenceM;}
	
	public ChessObject getObject(String ID)
	{
		return objM.getObject(ID);
	}
	
	public Tile getTile(String ID)
	{
		return mapM.getTile(ID);
	}
}