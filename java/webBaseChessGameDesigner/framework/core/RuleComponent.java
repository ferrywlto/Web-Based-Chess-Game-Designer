package webBaseChessGameDesigner.framework.core;

import java.util.ArrayList;

public final class RuleComponent 
{
	// The reason of using arraylist instead of hashtable here 
	// is somehow the same actions/conditions may exist in the
	// same rule component more than once. As hashtable's entries
	// are unique, using arraylist is more suitable.
	ArrayList<Condition> conditions;
	ArrayList<Action> actions;
	
	// Default constructor
	public RuleComponent(){}
	
	public RuleComponent(ArrayList<Condition> cons, ArrayList<Action> acts)
	{
		this.conditions = cons;
		this.actions = acts;
	}
	
	public void addCondition(Condition c)
	{
		conditions.add(c);
	}
	
	public void addAction(Action a)
	{
		actions.add(a);
	}
	
	public boolean execute()
	{
/*		=unused code =
 * 		// As most actions will not return any tiles,
		// result hashtable is initialized with size 0.
		// hashtable will resize if no more space left for an add.
		Hashtable<String, Tile> resultTiles = new Hashtable<String, Tile>(0);
*/
		// If any one of the condition doesn't satisify,
		// no action will perform. 
		for(int i=0; i<conditions.size(); i++)
		{
			if(!conditions.get(i).vaildate())
			{
				return false;
			}
		}
		// Actions start to execute only all condition check passed.
		for(int j=0; j<actions.size(); j++)
		{
			// Keep collect the result tiles from the action performed.
			actions.get(j).execute();
		}
		
		// Return the complete result.
		return true;
	}
}
