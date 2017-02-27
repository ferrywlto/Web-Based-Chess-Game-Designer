package webBaseChessGameDesigner.framework.core;

import webBaseChessGameDesigner.framework.managers.GeneralResourceManager;

//public interface Condition
/**
 * @author  Ferry To
 */
public abstract class Condition
{
	// All concrete subclass of condition need these
	// These should not be changed after object creation
	protected final GeneralResourceManager manager;
	protected final String[] params;
		
	protected Condition(GeneralResourceManager grm, String[] args)
	{
		manager = grm;
		params = args;
	}
	//Concrete class of condition have to hold reference
	// to reference_manager, but not result_manager.
	public abstract boolean vaildate();
}
