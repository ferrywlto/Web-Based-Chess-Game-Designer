package webBaseChessGameDesigner.framework.core;

import java.util.Hashtable;

/**
 * @author  Ferry To
 */
public class ChessObject 
{
	final String 	objID;
	final ObjectType type;
	Tile 	location;
	Player 	owner;
	Hashtable<String, ObjectAttribute> attributes;
	
	public ChessObject(String objID, Tile loc, Player owner, ObjectType objType)
	{
		this.objID = objID;
		this.type = objType;
		this.location = loc;
		this.owner = owner;
		this.attributes = new Hashtable<String, ObjectAttribute>(0);
		this.location.addObject(this);
	}
	
	public void addAttribute(CommonAttributeType type, String value)
	{
		attributes.put(type.getID(), new ObjectAttribute(type,value));
	}
	
	public ObjectAttribute getAttribute(String ID)
	{
		return attributes.get(ID);
	}
	
	/**
	 * @param location  the location to set
	 * @uml.property  name="location"
	 */
	public void setLocation(Tile loc){this.location = loc;}
	/**
	 * @param owner  the owner to set
	 * @uml.property  name="owner"
	 */
	public void setOwner(Player owner){this.owner = owner;}
	/**
	 * @return  the owner
	 * @uml.property  name="owner"
	 */
	public Player getOwner(){return this.owner;}
	/**
	 * @return  the location
	 * @uml.property  name="location"
	 */
	public Tile getLocation(){return this.location;}
	public String getID(){ return this.objID;}
	public ObjectType getType() { return this.type;}
}
