package webBaseChessGameDesigner.framework.managers;

import java.util.Hashtable;

import webBaseChessGameDesigner.framework.core.ChessObject;
import webBaseChessGameDesigner.framework.core.CommonAttributeType;
import webBaseChessGameDesigner.framework.core.ObjectAttribute;
import webBaseChessGameDesigner.framework.core.ObjectType;
import webBaseChessGameDesigner.framework.core.Player;
import webBaseChessGameDesigner.framework.core.Rule;
import webBaseChessGameDesigner.framework.core.Tile;
import webBaseChessGameDesigner.framework.types.AttributeDataType;

/**
 * This manager responsible for managing CommonAttributeTypes, ObjectTypes  and ChessObjects objType depends on rule objectManager depends on RuleManager
 */
public class ObjectManager 
{
	GeneralResourceManager master;
	
	final Hashtable<String, ChessObject> objects;
	final Hashtable<String, ObjectType> objTypes;
	final Hashtable<String, CommonAttributeType> attrTypes;
	
	public ObjectManager(GeneralResourceManager manager)
	{
		master = manager;
		objects = new Hashtable<String, ChessObject>(0);
		objTypes = new Hashtable<String, ObjectType>(0);
		attrTypes = new Hashtable<String, CommonAttributeType>(0);
	}

	/*
	 * region for common attribute type
	 */
	public CommonAttributeType getAttributeType(String attrID)
	{
		return attrTypes.get(attrID);
	}
	
	public boolean addAttributeType(CommonAttributeType type)
	{
		if(attrTypes.containsKey(type.getID()))
		{
			attrTypes.put(type.getID(), type);
			return true;
		}
		else
		{
			return false;
		}
	}
	
	// Overloaded method for creating new common object attribute
	public boolean addAttributeType(String attrID, AttributeDataType type)
	{
		if(!attrTypes.containsKey(attrID))
		{
			attrTypes.put(attrID, new CommonAttributeType(attrID,type));
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public boolean delAttributeType(String attrID)
	{
		if(attrTypes.containsKey(attrID))
		{
			attrTypes.remove(attrID);
			return true;
		}
		else
		{
			return false;
		}
	}
	
	
	/* Region for object types */
	
	// Overloaded method for adding attributes to an object
	public boolean addAttributeToObject(String objID, ObjectAttribute attribute)
	{
		if(objects.containsKey(objID))
		{
			ChessObject obj = objects.get(objID);
			obj.addAttribute(attribute.getType(), attribute.getValue());
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public boolean addAttributeToObject(String objID, String attrName, String attrValue)
	{
		if(objects.containsKey(objID))
		{
			ChessObject obj = objects.get(objID);
			
			obj.addAttribute(getAttributeType(attrName), attrValue);
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public boolean addObjectType(String typeID, String preMoveID, String postMoveID, String[][] attrs)
	{
		if(objTypes.containsKey(typeID))
		{
			return false;
		}
		else
		{
			ObjectAttribute[] objAttrs = new ObjectAttribute[attrs.length];
			for(int i=0; i<attrs.length; i++)
			{
				CommonAttributeType type = getAttributeType(attrs[i][0]);
				objAttrs[i] = new ObjectAttribute(type,attrs[i][1]);
			}
			Rule preMove = master.ruleM.getRule(preMoveID);
			Rule postMove = master.ruleM.getRule(postMoveID);
			objTypes.put(typeID, new ObjectType(typeID,preMove,postMove,objAttrs));
			return true;
		}
	}
	
	public boolean delObjectType(String typeID)
	{
		if(objTypes.containsKey(typeID))
		{
			objTypes.remove(typeID);
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public ObjectType getObjectType(String typeID)
	{
		return objTypes.get(typeID);
	}
	
	/* Region for Chess Objects */
	public boolean addObject(String objID, String tileID, String playerID, String typeID)
	{
		if(objects.containsKey(objID))
		{
			return false;
		}
		else
		{
			ObjectType type = objTypes.get(typeID);
			Player player = master.playerM.getPlayer(playerID);
			Tile tile = master.mapM.getTile(tileID);
			objects.put(objID, new ChessObject(objID,tile,player,type));
					
			// Player will be null if it is #PLAYER_NETURAL# instead.
			if(player!=null)player.addOwnedObjectID(objID);
			
			ObjectAttribute[] attrs = type.getAttributes();
			
			for(int i=0; i<attrs.length; i++)
			{
				addAttributeToObject(objID, attrs[i]);
			}
			return true;
		}
	}
	
	public boolean delObject(String objID)
	{
		if(objects.containsKey(objID))
		{
			objects.remove(objID);
			return true;
		}
		else
			return false;
	}
	
	public ChessObject getObject(String objID)
	{
		return objects.get(objID);
	}
}
