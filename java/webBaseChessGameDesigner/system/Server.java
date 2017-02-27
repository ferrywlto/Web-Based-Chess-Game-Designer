package webBaseChessGameDesigner.system;


import java.io.IOException;

import java.net.ServerSocket;

/**
 * @author  Ferry To
 */
public class Server 
{
	static final ConnectionManager connManager = ConnectionManager.getManager();
	static Server server;
	
	ServerSocket ss;
	int port;
	
	protected Server()
	{
		port = ServerConfiguration.getNumericParameter("ServerPort");
	}
	
	public static Server getInstance()
	{
		if(server==null) 
			server = new Server();
		return server;
	}
	
	public void startServer() 
	{
		boolean keepRunning = true;
		
		Messager.printMsg(this,"Server starting...");
		try 
		{
			ss = new ServerSocket(port);
		} 
		catch (IOException e) 
		{
			Messager.printMsg(this,"Could not listen on port: "+port);
			System.exit(1);
		}

		Messager.printMsg(this,"Web-based Chess Game Designer - Chess Game Server started.");
		while(keepRunning) 
		{
			try 
			{
				connManager.bindConnection(ss.accept());
			}
			catch (IOException e) 
			{
				//Just simply break the loop in release version. 
				//e.printStackTrace();
				//Messager.printMsg(this,"Accept failed:3333");
				keepRunning = false;
			}
		}
	}
	
	public void closeServer()
	{
		Messager.printMsg(this,"Server closing...");
		try
		{
			if(ss != null) ss.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		Messager.printMsg(this,"Web based chess game design platform play server closed.");
	}
}