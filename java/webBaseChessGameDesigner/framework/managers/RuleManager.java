package webBaseChessGameDesigner.framework.managers;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Hashtable;
import webBaseChessGameDesigner.framework.actions.*;
import webBaseChessGameDesigner.framework.conditions.*;
import webBaseChessGameDesigner.framework.core.Action;
import webBaseChessGameDesigner.framework.core.Condition;
import webBaseChessGameDesigner.framework.core.Rule;
import webBaseChessGameDesigner.framework.core.RuleComponent;
import webBaseChessGameDesigner.framework.types.RuleType;
import webBaseChessGameDesigner.system.RuleClassMapper;
import webBaseChessGameDesigner.system.ServerConfiguration;

/**
 * @author  Ferry To
 */
public class RuleManager 
{
	// For Java Reflection
	final static Class[] cs = new Class[] {GeneralResourceManager.class, String[].class};
	final static String actionPackageName = ServerConfiguration.getStringParameter("RuleMapperActionPackage");
	final static String conditionnPackageName = ServerConfiguration.getStringParameter("RuleMapperConditionPackage");
	
	final GeneralResourceManager master;
	Hashtable<String, Condition> conditions;
	Hashtable<String, Action> actions;
	Hashtable<String, Rule> rules;
	
	public RuleManager(GeneralResourceManager manager)
	{
		master = manager;
		conditions = new Hashtable<String, Condition>(0);
		actions = new Hashtable<String, Action>(0);
		rules = new Hashtable<String, Rule>(0);
	}
	
	public void addRule(String ID, RuleComponent[] components)
	{
		try
		{
			if(rules.containsKey(ID)) 
				throw new Exception("Rule name cannot duplicate.");
			else
			{
				Rule rule = new Rule(ID,master,components);
				rules.put(ID,rule);
			}
		} catch (Exception e) {e.printStackTrace(); }
	}

	//Assume the required actions and conditions prototype has already added.
	public RuleComponent makeComponent(String[] cons, String[] acts)
	{
		ArrayList<Condition> conList = new ArrayList<Condition>(0);
		ArrayList<Action> actList = new ArrayList<Action>(0);
		
		for(int i=0; i<cons.length; i++)
		{
			conList.add(getCondition(cons[i]));
		}
		for(int i=0; i<acts.length; i++)
		{
			actList.add(getAction(acts[i]));
		}
		RuleComponent component = new RuleComponent(conList,actList);
		return component;
	}
	
	public Action getAction(String fullQualifiedName)
	{
		return actions.get(fullQualifiedName); 
	}
	
	public Condition getCondition(String fullQualifiedName)
	{
		return conditions.get(fullQualifiedName);
	}
	
	public String addAction(int ID, String[] args)
	{
		Action action = getActionPrototype(ID,args);
		// IMPORTANT! since purely using the class name will
		// result in a case that same rule with different paramater
		// cannot add to the respository, but even using the object itself
		// as the respository key, it cause another problem that every object
		// get a new key even the initial paramater of each object is different.
		// To solve this provblem, class_name+parameters will become the key
		// to distinguish objects that have the same class but different parameters.
		String keyStr = getKeyString(action, args);
		if(!actions.containsKey(keyStr))
			actions.put(keyStr, action);
		return keyStr;
	}
	
	public String addCondition(int ID, String[] args)
	{
		Condition condition = getConditionPrototype(ID,args);
		// Same situation and solution as Action.
		String keyStr = getKeyString(condition, args);
		
		if(!conditions.containsKey(keyStr))
			conditions.put(keyStr, condition);
		return keyStr;
	}

	// [NOTICE]
	// Load class dynamically by Java Reflection
	public Action getActionPrototype(int ID, String[] args)
	{
		try
		{	
			Constructor c = Class.forName(actionPackageName+"."+RuleClassMapper.getActionClassName(ID)).getConstructor(cs);
			return (Action)c.newInstance(new Object[]{master,args});
		}
		catch(InstantiationException e){e.printStackTrace();}
		catch(IllegalAccessException e){e.printStackTrace();}
		catch(InvocationTargetException e){e.printStackTrace();}
		catch(ClassNotFoundException e){e.printStackTrace();}
		catch(NoSuchMethodException e){e.printStackTrace();}
		return new NullAction(master,args); // Return NullAction if any exception thrown.
		
/*		switch(ID)
		{
			case 0: return new NullAction(master,args);
			case 1: return new MoveObjectToSelectedTile(master,args);
			case 2: return new CurrentPlayerWin(master,args);
			case 3: return new GetSimpleMoveableRegion(master,args);
			case 4: return new GetSimpleMoveableRegionExcept(master,args);
			case 5: return new GetSimpleEmptyMoveableRegion(master,args);
			case 6: return new GetSimpleEmptyMoveableRegionExcept(master,args);
			
			default: return new NullAction(master,args);
		}*/
	}
	
	public Condition getConditionPrototype(int ID, String[] args)
	{
		try
		{	
			Constructor c = Class.forName(conditionnPackageName+"."+RuleClassMapper.getConditionClassName(ID)).getConstructor(cs);
			return (Condition)c.newInstance(new Object[]{master,args});
		}
		catch(InstantiationException e){e.printStackTrace();}
		catch(IllegalAccessException e){e.printStackTrace();}
		catch(InvocationTargetException e){e.printStackTrace();}
		catch(ClassNotFoundException e){e.printStackTrace();}
		catch(NoSuchMethodException e){e.printStackTrace();}
		return new NullCondition(master,args); // Return NullCondition if any exception thrown.
		
		/*switch(ID)
		{
			case 0: return new NullCondition(master,args);
			case 1: return new IfCurrentObjectInOpponentRegionType(master,args);
			case 2: return new IfTileEmpty(master,args);
			default: return new NullCondition(master,args);
		}*/
	}
	
	public Rule getRule(String ruleID)
	{
		return rules.get(ruleID);
	}
	
	public static String getKeyString(Object obj,String[] params)
	{
		// For each parameter, a special character have to be insert 
		// to prevent the following key duplication case;
		// e.g. a = {0,asd,1f); b = {0a,sd1,f};
		// without marker insertion: a:oasd1f b:oasd1f
		// with marker: a: 0#asd#1f# b:0a#sd1#f#
		String resultKey = obj.getClass().getName();
		if(params != null && params.length > 0)
			for(int i=0; i<params.length; i++)
				resultKey += ("#"+params[i]);
		return resultKey;
	}
	
	public static RuleType getRuleType(String type)
	{
		if(type.equals("preMove"))
		{
			return RuleType.PREMOVE;
		}
		else if(type.equals("postMove"))
		{
			return RuleType.POSTMOVE;
		}
		else if(type.equals("turn"))
		{
			return RuleType.TURN;
		}
		else
		{
			return null;
		}
	}
}