package webBaseChessGameDesigner.framework.actions;
import webBaseChessGameDesigner.framework.core.Action;
import webBaseChessGameDesigner.framework.managers.GeneralResourceManager;

public class NullAction extends Action
{
	public NullAction(GeneralResourceManager manager, String[] args)
	{
		super(manager,args);
		for(int i=0; i<args.length; i++)
		{
			System.out.println(args[i]);
		}
	}
	
	public void execute(){System.out.println("NULL ACTION");}
}
