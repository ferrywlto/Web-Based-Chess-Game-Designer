package webBaseChessGameDesigner.framework.core;

import java.util.Hashtable;
import java.util.Set;

/**
 * @author  Ferry To
 */
public class Player 
{
	String 	ID;
	Hashtable<String,String> 	ownedObjectIDs;
	Hashtable<String,String>	ownedRegionIDs;
	
	public Player(String id)
	{
		ID = id;
		ownedObjectIDs = new Hashtable<String, String>(0);
		ownedRegionIDs = new Hashtable<String, String>(0);
	}
	
	/**
	 * @return  the iD
	 * @uml.property  name="iD"
	 */
	public String getID(){return ID;}
	public boolean addOwnedObjectID(String objID)
	{
		if(!ownedObjectIDs.containsKey(objID))
		{
			ownedObjectIDs.put(objID, objID);
			return true;
		}
		else
			return false;
	}
	public boolean addOwnedRegionID(String regID)
	{
		if(!ownedObjectIDs.containsKey(regID))
		{
			ownedObjectIDs.put(regID, regID);
			return true;
		}
		else
			return false;		
	}
	public boolean delOwnedObjectID(String objID)
	{
		if(ownedObjectIDs.containsKey(objID))
		{
			ownedObjectIDs.remove(objID);
			return true;
		}
		else
			return false;		
	}
	public boolean delOwnedRegionID(String regID)
	{
		if(ownedObjectIDs.containsKey(regID))
		{
			ownedObjectIDs.remove(regID);
			return true;
		}
		else
			return false;			
	}
	/**
	 * @return  the ownedObjectIDs
	 * @uml.property  name="ownedObjectIDs"
	 */
	public String[] getOwnedObjectIDs() 
	{
		Set<String> keys = ownedObjectIDs.keySet();
		return keys.toArray(new String[keys.size()]);
	}	
	/**
	 * @return  the ownedRegionIDs
	 * @uml.property  name="ownedRegionIDs"
	 */
	public String[] getOwnedRegionIDs() 
	{
		Set<String> keys = ownedRegionIDs.keySet();
		return keys.toArray(new String[keys.size()]);
	}
}
