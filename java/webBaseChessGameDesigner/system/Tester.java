/**
 * Class: testJDBC
 * Author: Ferry To
 * Organization: 3 Dynamics (Asia) Limited
 * Date: 2007-01-05 
 * Description:
 * This class used to demostrate how to use MySQL DB Connector/J
 * to connect a Java application to MySQL Database Server 
 */
package webBaseChessGameDesigner.system;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import webBaseChessGameDesigner.framework.actions.NullAction;
import webBaseChessGameDesigner.framework.core.Action;
import webBaseChessGameDesigner.framework.core.Chess;
import webBaseChessGameDesigner.framework.managers.GeneralResourceManager;
import webBaseChessGameDesigner.framework.managers.RuleManager;


public class Tester {

	public static void main(String args[])
	{
		
		Tester tester = new Tester();
		tester.testServerConfig();
		RuleClassMapper.startRuleMapping();
		
		//tester.testChessBuilder();
		Server server = new Server();
		server.startServer();

		//System.out.println(NullAction.class.getName());
		//Chess chess = new Chess("abc","def");
		//GeneralResourceManager gm = chess.getGRM();
		//RuleManager rm = gm.getRuleManager();
		//rm.test(new String[] {"haha","hehe","eiesucks"});
		/*
		System.out.println(a[0]);
		GeneralResourceManager grm = new GeneralResourceManager(new Chess("this"));
		ifObjectHasAttribute x = new ifObjectHasAttribute(grm,a);
		RuleManager ruleM = grm.getRuleManager();
		//Action act1 = ruleM.getActionPrototype(1, a);
		//Action act2 = ruleM.getActionPrototype(1, a);
		//System.out.println(ruleM.addAction(1, a));
		//System.out.println(ruleM.addAction(1, b));
//		Hashtable<Action,Action> h = new Hashtable<Action,Action>(0);
//		h.put(act1, act1);
//		h.put(act2, act2);
//		System.out.println(act1 ==(act2));
//		Enumeration e = h.keys();
//		while(e.hasMoreElements())
//		{
//			e.nextElement();
//			System.out.println(e.toString());
//		}
//		System.out.println(h.containsKey(act2));
*/
	}
	
	public void testServerConfig()
	{
		ServerConfiguration.loadConfiguration("configuration/server_configuration.xml");
	} 
	
	public void testChessBuilder()
	{
		try
		{
			//ChessBuilder builder = ChessBuilder.getBuilder();
			//String filePath = ServerConfiguration.getStringParameter("SpecificationStorageLocation");
			//String fileName = "real.xml";
			//Chess chess = builder.parseChessSpecification(filePath+fileName);
			//Document doc = builder.buildChess("E:\\PortableWorks\\FYP\\Code\\test2.xml"));
			//builder.buildChess(doc);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	

	
	public void testJDBC1()
	{
		//try
		//{
			DatabaseManager dbm = new DatabaseManager();
			boolean isValid = dbm.validateLogin("frog", "meimei");
			//dbm.exeCommand("SELECT * FROM USER;");
			//dbm.printResultSet(dbm.exeCommand("SELECT * FROM USER;"));
			System.out.println("Connect Success, user found = "+isValid);
			if(isValid)
			{
				dbm.addChessDefinition("frog", "3rd Chess", "3 desc", "frog_chess3.xml");
				System.out.println("Chess count > 3 :" +dbm.checkChessCount("frog", 3));
				System.out.println("Chess found:" + dbm.listChessCount("frog"));
				//String[] names = dbm.listChessNames("frog");
				//for(int i=0; i<names.length; i++)
				//{
				//	System.out.println(names[i]);
				//}
			}
			//System.out.println("Affected rows:"+affectedCount);
		//}
		/*
		catch (SQLException ex) 
		{
	        // handle any errors
	        System.out.println("SQLException: " + ex.getMessage());
	        System.out.println("SQLState: " + ex.getSQLState());
	        System.out.println("VendorError: " + ex.getErrorCode());
		}
		finally
		{
			/**
			if(rs != null)
			{
				try{rs.close();}
				catch(SQLException sqe){sqe.printStackTrace();}
				rs = null;
			}
			
			if(sql != null)
			{
				try{sql.close();}
				catch(SQLException sqe){sqe.printStackTrace();}
				sql = null;
			}
			
			if(conn != null)
			{
				try{conn.close();}
				catch(SQLException sqe){sqe.printStackTrace();}
				conn = null;
			}
			*/
		//}
	}
}
