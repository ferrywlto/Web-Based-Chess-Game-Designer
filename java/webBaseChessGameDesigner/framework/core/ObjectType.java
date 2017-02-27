package webBaseChessGameDesigner.framework.core;

/**
 * @author  Ferry To
 */
public class ObjectType 
{
	final Rule preMove;
	final Rule postMove;
	final String ID;
	/**
	 * @uml.property  name="attributes"
	 * @uml.associationEnd  multiplicity="(0 -1)"
	 */
	final ObjectAttribute[] attributes;
	
	public ObjectType(String ID, Rule preMove, Rule postMove, ObjectAttribute[] attributes)
	{
		this.ID = ID;
		this.preMove = preMove;
		this.postMove = postMove;
		this.attributes = attributes;
	}
	
	/**
	 * @return  the attributes
	 * @uml.property  name="attributes"
	 */
	public ObjectAttribute[] getAttributes() {return attributes;}
	/**
	 * @return  the preMove
	 * @uml.property  name="preMove"
	 */
	public Rule getPreMove(){return preMove;}
	/**
	 * @return  the postMove
	 * @uml.property  name="postMove"
	 */
	public Rule getPostMove(){return postMove;}
	public void executePreMove(){ preMove.execute(); }
	public void executePostMove(){ postMove.execute(); }
	/**
	 * @return  the iD
	 * @uml.property  name="iD"
	 */
	public String getID() {return ID;}
}
