package webBaseChessGameDesigner.system;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import java.net.SocketTimeoutException;

import org.w3c.dom.Document;

import webBaseChessGameDesigner.system.state.ConnectionState;


/**
 * @author  Ferry To
 */
public class Connection implements Runnable 
{
	/**
	 * @author  Ferry To
	 */
	protected static final ConnectionManager connManager = ConnectionManager.getManager();
	
	protected final int connID;
	protected final Socket cs;
	
	protected CommandProcessor cmdProcessor; 

	protected boolean keepRunning;
	protected PrintWriter out;
	protected BufferedReader in;
	protected Document cmdDoc;
	protected ConnectionState state;
	protected boolean isFlashMsg;
	
	// String used to store the username of the connected client
	protected String username;
	protected int gameID;
	
	public Connection(Socket s, int id)
	{
		this.cs = s;
		this.connID = id;
		this.keepRunning = true;
		// [CONN_STATE_TRANSITION]
		this.state = ConnectionState.INIT;
		this.cmdProcessor = new CommandProcessor(this);
		
		try
		{
			this.in = new BufferedReader(new InputStreamReader(this.cs.getInputStream()));
			this.out = new PrintWriter(this.cs.getOutputStream(), true);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			close();
		}
	}
	
	/**
	 * @return  the connID
	 * @uml.property  name="connID"
	 */
	public int getConnID() {return this.connID; }
	/**
	 * @return  the gameID
	 * @uml.property  name="gameID"
	 */
	public int getGameID() {return this.gameID; }
	/**
	 * @return  the username
	 * @uml.property  name="username"
	 */
	public String getUsername() {return this.username; }
	/**
	 * @return  the state
	 * @uml.property  name="state"
	 */
	public ConnectionState getState() {return this.state;}
	
	/**
	 * @param gameID  the gameID to set
	 * @uml.property  name="gameID"
	 */
	public void setGameID(int gID) {this.gameID = gID;}
	/**
	 * @param username  the username to set
	 * @uml.property  name="username"
	 */
	public void setUsername(String name) { this.username = name; }
	/**
	 * @param state  the state to set
	 * @uml.property  name="state"
	 */
	public void setState(ConnectionState state) { this.state = state;}
		
	public void sendMessage(String msg) 
	{
		// Message must append a \0 in order to let flash client XML socket to receive message
		if (this.out != null)
			this.out.println(msg+"\0");
		/*
			if(this.isFlashMsg)
			{
				// reset flag
				this.isFlashMsg = false;
				this.out.println(msg+"\0");
			}
			else
				this.out.println(msg);
				*/
	}
	
	public static String eraseZeroByte(String msg)
	{
		return msg.replace("\0","");
	}

	public void close()
	{
		// [CONN_STATE_TRANSITION]
		if(this.state != ConnectionState.CLOSED)
		{			
			// Reset conncetion values before dispose.
			this.keepRunning = false;
			this.gameID = 0;
			this.username = null;
			this.state = ConnectionState.CLOSED;
			
			if(this.out!= null) this.out.close();
			
			try	{ if(this.in != null) this.in.close(); }
			catch(IOException ioe){ioe.printStackTrace();}
	
			try { if(this.cs != null) this.cs.close(); }
			catch(IOException ioe){ioe.printStackTrace();}			
		}
	}
	
	public void run() 
	{
		String clientInput;
		Messager.printMsg(this, "Connected.");
		
		try
		{
			while (this.keepRunning) 
			{
				clientInput=this.in.readLine();
				
				// pure \0 means the socket disconnected from client flash player.
				if (clientInput == null || 
					clientInput.equals("") || 
					clientInput.equals("\0")) break;
				
				// Handle \0 Zero Byte generated from flash player.
				if(clientInput.contains("\0"))
				{
					//this.isFlashMsg = true;
					clientInput = eraseZeroByte(clientInput);
				}
				
				cmdProcessor.processCommand(clientInput);
			}
		}
		catch (SocketTimeoutException ste) 
		{ 
			connManager.notifyErrorEvent(this);
			Messager.printMsg(this, "Exception caused by timeout.");
		}
		catch (IOException e) 
		{ 
			connManager.notifyErrorEvent(this);
			Messager.printMsg(this, "I/O Exception occur."); 
		}
		catch (Exception e)
		{
			e.printStackTrace();
			connManager.notifyErrorEvent(this);
			Messager.printMsg(this, "Unknown Error.");
		}
		
		connManager.notifyDisconnectEvent(this);
		Messager.printMsg(this, "Terminated.");
	}	
}