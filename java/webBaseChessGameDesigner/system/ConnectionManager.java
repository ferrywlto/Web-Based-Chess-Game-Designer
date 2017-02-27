package webBaseChessGameDesigner.system;

import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * @author  Ferry To
 */
public class ConnectionManager 
{
	protected final static int timeoutSetting = ServerConfiguration.getNumericParameter("Timeout");
	protected static int connectionID = ServerConfiguration.getNumericParameter("InitConnectionID");
	
	protected static final ChessGameManager gameManager = ChessGameManager.getManager();
	
	protected static ConnectionManager connManager;
	
	protected final Hashtable<Integer, Connection> connections;
	// Created for blocking multiple login.
	protected final Hashtable<String, String> loginEntries; 	
	
	public static ConnectionManager getManager()
	{
		if(connManager == null)
			connManager = new ConnectionManager();
		return connManager;
	}
	
	private ConnectionManager()
	{
		connections = new Hashtable<Integer, Connection>(100);
		loginEntries = new Hashtable<String, String>(100);
	}
	
	protected int getNextConnectionID()
	{
		// Make connectionID always start from positive integer.
		if(connectionID < 0 ) connectionID = 0;
		return ++connectionID;
	}
	
	public boolean checkLogged(String username)
	{
		return loginEntries.containsKey(username);
	}
	
	public boolean removeLoginEntry(String username)
	{
		if(loginEntries.containsKey(username))
		{
			loginEntries.remove(username);
			return true;
		}
		else
			return false;
	}
	public boolean removeLoginEntry(Connection conn)
	{
		if(loginEntries.containsKey(conn.getUsername()))
		{
			loginEntries.remove(conn.getUsername());
			return true;
		}
		else
			return false;
	}
	public boolean addLoginEntry(String username)
	{
		if(!loginEntries.containsKey(username))
		{
			loginEntries.put(username, username);
			return true;
		}
		else
			return false;
	}
	
	public void bindConnection(Socket cs)
	{
		synchronized(connections) 
		{
			boolean accept = true;
			try
			{
				// Set socket timeout (in minutes, 1 min = 60 * 1000 millisecond)
				cs.setSoTimeout(timeoutSetting * 60 * 1000); // in millisecond
			}
			catch(SocketException e)
			{ 
				accept = false; 
				Messager.printMsg(this,"System encountered a socket exception and connection fails to establish.");
			}
			
			if(accept)
			{
				int connectionID = getNextConnectionID();
				Connection conn = new Connection(cs, connectionID);
				connections.put(connectionID,conn);
				Thread t = new Thread(conn);
				t.start();
			}
			// Do nothing if failed.
		}
	}
	
	public Connection getConnection(int ID)
	{
		synchronized(connections)
		{
			return connections.get(ID);
		}
	}
	
	public void removeConnection(int ID) 
	{
		synchronized(connections) 
		{
			connections.get(ID).close();
			connections.remove(ID);
		}
	}

	public void removeConnection(Connection conn) 
	{
		synchronized(connections) 
		{
			connections.remove(conn.getConnID());
			conn.close();
		}
	}
	
	// Calling this method will clear everything.
	public void resetAll()
	{
		synchronized(connections) 
		{
			Enumeration<Connection> e = connections.elements();
			while(e.hasMoreElements())
			{
				Connection conn = e.nextElement();
				conn.close();
			}
		}
		connections.clear();
		loginEntries.clear();
	}
	
	//	 This method is the handler of whisper messages
	public void unicastMessage(int receiverID, String msg)
	{
		synchronized(connections) 
		{
			Connection conn = connections.get(receiverID);
			if(conn != null)
				conn.sendMessage(msg);
		}
	}
	public void multicastMessage(Iterator<Integer> iterator, String msg)
	{
		while(iterator.hasNext())
		{
			
			int ID = iterator.next();
			Connection conn = connManager.getConnection(ID);
			if(conn != null)
			{
				conn.sendMessage(msg);
			}
		}
	}
	
	public void broadcastMessage(String msg)
	{
		synchronized(connections) 
		{
			Enumeration<Connection> e = connections.elements();
			while(e.hasMoreElements())
			{
				Connection r = e.nextElement();
				if(r != null)
				{
					r.sendMessage(msg);
				}
				else
				{
					Messager.printMsg(this,"Null connection ID detected. That connection should be already disconnected.");
				}
			}
		}
	}
	
	/*
//	 Chesscast means boardcast to all players in specific chess game object.
	public void gamecastMessage(int gameID, String msg)
	{
		// Get all player connection ID of specific chess game.
		ChessGameManager gameManager = ChessGameManager.getManager();
		Iterator<Integer> e = gameManager.getGame(gameID).getConnectionIDList().iterator();
		
		while(e.hasNext())
		{
			synchronized (connections) 
			{
				int connID = e.next();
				Connection c = connections.get(connID);
				if(c != null)
				{
					c.sendMessage(msg);
				}
				else
				{
					Messager.printMsg(this,"Null connection ID detected. That connection should be already disconnected.");
				}
			}
		}
	}
	*/
	public void notifyErrorEvent(Connection conn)
	{
		// Quit current game before dispose.
		if(conn.getGameID() > 0)
			gameManager.getGame(conn.getGameID()).quit(conn.getConnID());
		
		if(conn.getUsername() != null)
			removeLoginEntry(conn);
		removeConnection(conn);
	}
	public void notifyDisconnectEvent(Connection conn)
	{
		// Quit current game before dispose.
		if(conn.getGameID() > 0)
			gameManager.getGame(conn.getGameID()).quit(conn.getConnID());
		
		if(conn.getUsername() != null)
			removeLoginEntry(conn);
		removeConnection(conn);
	}
}
