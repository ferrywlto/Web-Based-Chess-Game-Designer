package webBaseChessGameDesigner.system;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.validation.Validator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import webBaseChessGameDesigner.system.state.ConnectionState;

// need to think about how to avoid modify state of connection outside.
// state transition should be done internally.
/**
 * @author  Ferry To
 */
public class CommandProcessor
{
	protected static final XMLLoader loader = XMLLoader.getLoader();
	protected static final Validator validator = loader.getCommandValidator();
	protected static final ConnectionManager connManager = ConnectionManager.getManager();
	protected static final ChessGameManager  gameManager = ChessGameManager.getManager();
	protected static final DatabaseManager   dbManager = DatabaseManager.getManager();
	
	private Connection connection;
	private Document cmdDoc;
	
	public CommandProcessor(Connection conn)
	{
		this.connection = conn;
	}
	
	public boolean validateCommand(String input)
	{
		this.cmdDoc = loader.getXMLDocumentFromString(input);
		if(this.cmdDoc == null) 
			return false;
		else if(!loader.validateXMLDocument(validator, this.cmdDoc)) 
			return false;
		else
			return true;
	}
	
	public void processCommand(String input)
	{	
		if(validateCommand(input))
		{
			try
			{
				Element element = this.cmdDoc.getDocumentElement();
				
				String cmdType = element.getFirstChild().getNodeName();;
				
				if(cmdType.equals("loginRequest") && (connection.getState() == ConnectionState.INIT))
				{
					NodeList ulist = element.getElementsByTagName("username");
					NodeList plist = element.getElementsByTagName("password");
					// Change connection.state from init to logged on
					String username = ((Element)ulist.item(0)).getFirstChild().getNodeValue();
					String password = ((Element)plist.item(0)).getFirstChild().getNodeValue();
					Messager.printMsg(connection,"Request to login with username:"+username+" password:"+password+".");
					processLogin(username,password);
				}
				else if(cmdType.equals("listChessRequest") && (connection.getState() == ConnectionState.LOGGED))
				{
					Messager.printMsg(connection,"Request to list created chesses.");
					processListChess();
				}
				else if(cmdType.equals("createGameRequest") && (connection.getState() == ConnectionState.LOGGED))
				{
					NodeList clist = element.getElementsByTagName("chessName");
					NodeList nlist = element.getElementsByTagName("gameName");
					NodeList dlist = element.getElementsByTagName("gameDesc");
					
					String chessName = clist.item(0).getFirstChild().getTextContent();
					String gameName = nlist.item(0).getFirstChild().getTextContent();
					String gameDesc = dlist.item(0).getFirstChild().getTextContent();
					Messager.printMsg(connection,"Request to create chess game:"+chessName+".");					
					processCreateGame(chessName,gameName,gameDesc);
				}
				else if(cmdType.equals("joinGameRequest") && (connection.getState() == ConnectionState.LOGGED))
				{
					NodeList glist = element.getElementsByTagName("gameID");
					int gID = Integer.parseInt(glist.item(0).getFirstChild().getTextContent());
					Messager.printMsg(connection,"Request to join game "+gID+".");
					processJoinGame(gID);
				}
				else if(cmdType.equals("quitGameRequest") && 
						(connection.getState() == ConnectionState.PLAY_CMD || 
							connection.getState() == ConnectionState.PLAY_WAIT ||
								connection.getState() == ConnectionState.WAITING))
				{
					Messager.printMsg(connection,"Request to quit current game.");
					processQuitGame();
				}			
				else if(cmdType.equals("listGameRequest") && (connection.getState() == ConnectionState.LOGGED))
				{
					Messager.printMsg(connection,"Request to list available games.");
					processListGame();
				}
				else if(cmdType.equals("gameCommand") && (connection.getState() == ConnectionState.PLAY_CMD))
				{
					NodeList nList = element.getFirstChild().getChildNodes();
					String type = nList.item(0).getTextContent();
					//param can be a tile ID or obj ID, depends on type
					String param = nList.item(1).getTextContent();
					Messager.printMsg(connection,"Request to process game command. type:"+type+" param:"+param);					
					processGameCommand(type,param);
					//<wbcgdp_command><gameCommand><type>preMove</type><param>p2cat</param></gameCommand></wbcgdp_command>
				}
				else if(cmdType.equals("disconnectRequest"))
				{
					Messager.printMsg(connection,"Request to disconnect.");
					processDisconnect();
				}
				else
				{
					// Do nothing instead from now on.
					//connection.sendMessage(Messager.errorMsg("Invalid Command. (E001)"));
				}
			}
			catch (NullPointerException e)
			{
				// Just ignore invalid command
				//e.printStackTrace();
				Messager.printMsg(connection,"Suspicious action detected: Null string paramater.");
				connection.sendMessage(Messager.errorMsg("Internal Server Error. Connection Terminated. (E002)"));
				connManager.notifyErrorEvent(connection);
			}
		}
		else
		{
			// Just ignore invalid command
			//Do nothing if command is not valid.
			Messager.printMsg(connection,"Invalid command detected.");
			//connection.sendMessage(Messager.errorMsg("Invalid command detected. Connection Terminated. (E003)"));
			//connManager.notifyErrorEvent(connection);
		}
	}
	
	protected void processLogin(String username, String password)
	{
		DatabaseManager dbm = DatabaseManager.getManager();
		// Check if username already logged.
		// Access login function only if username no logged.
		// This block multiple login of single username.
		if(!connManager.checkLogged(username) && 
			dbm.validateLogin(username, password) &&
			connManager.addLoginEntry(username) )
		{					
			// [CONN_STATE_TRANSITION]
			connection.setState(ConnectionState.LOGGED);
			connection.setUsername(username);						
			Messager.printMsg(connection,"Login success.");
			connection.sendMessage(Messager.responseMsg("login_result","true"));
		}
		else
		{
			Messager.printMsg(connection,"Login Failed.");
			connection.sendMessage(Messager.responseMsg("login_result","false"));
		}
	}
	
	protected void processListChess()
	{
		// no connection.getState() change
		DatabaseManager dbm = DatabaseManager.getManager();
		String[][] chessNames = dbm.listChessNames(connection.getUsername());
		String result = "";
		for(int i=0; i<chessNames.length; i++)
		{
			result += ("<chess><name>"+chessNames[i][0]+"</name><desc>"+chessNames[i][1]+"</desc></chess>");
		}
		connection.sendMessage(Messager.responseMsg("listChess_result",result));
	}
	
	protected void processCreateGame(String chessName, String gameName, String gameDesc)
	{
		// Test whether the combination have a chess definition file associated.
		// ChessName used because user never know the actual chess definition name.
		String fileName = dbManager.getChessFileName(connection.getUsername(), chessName);
		if(fileName == null)
		{
			Messager.printMsg(connection,"No such chess definition created by '"+connection.getUsername()+"' called '"+chessName+"'.");
			connection.sendMessage(Messager.responseMsg("create_result","<status>false</status><spec/>"));					
			return;
		}
		
		int gID = gameManager.buildGame(gameName, gameDesc, connection.getUsername(), fileName);
		if(gID > 0 && gameManager.getGame(gID).join(connection.getConnID()))
		{
			// Get joined user list.
			HashMap<String, Integer> map = gameManager.getGame(gID).getConnSlots();
			Iterator<String> It = map.keySet().iterator();
			String joinedList = "";
			while(It.hasNext())
			{
				String key = It.next();
				if(map.get(key)!= null)
				{
					String nickname = connManager.getConnection(map.get(key)).getUsername();
					joinedList += "<player><slot>"+key+"</slot><nickname>"+nickname+"</nickname></player>";
				}
			}
			String result = "<status>true</status><spec>";
			result += gameManager.getGame(gID).getChess().getSpecification();
			result += "</spec>"+joinedList;
			connection.sendMessage(Messager.responseMsg("create_result",result));
		}
		else if(gID == 0)
		{
			Messager.printMsg(connection,"Problem occur in parsing chess. Game createion failed.");
			connection.sendMessage(Messager.responseMsg("create_result","<status>false</status><spec/>"));
		}
		else if(gID == -1)
		{
			Messager.printMsg(connection,"Game name already exists. Game creation failed.");
			connection.sendMessage(Messager.responseMsg("create_result","<status>false</status><spec/>"));					
		}
		else if(gID == -2)
		{
			Messager.printMsg(connection,"File not found? Game creation failed.");
			connection.sendMessage(Messager.responseMsg("create_result","<status>false</status><spec/>"));					
		}
		else if(gID == -3)
		{
			Messager.printMsg(connection,"File cannot read? Game creation failed.");
			connection.sendMessage(Messager.responseMsg("create_result","<status>false</status><spec/>"));					
		}	
	}

	protected void processJoinGame(int gID)
	{
		ChessGame game = gameManager.getGame(gID);
		
		if(game != null)
		{
			if(game.join(connection.getConnID()))
			{
				// Get joined user list.
				HashMap<String, Integer> map = game.getConnSlots();
				Iterator<String> It = map.keySet().iterator();
				String joinedList = "";
				while(It.hasNext())
				{
					String key = It.next();
					if(map.get(key)!= null)
					{
						String nickname = connManager.getConnection(map.get(key)).getUsername();
						joinedList += "<player><slot>"+key+"</slot><nickname>"+nickname+"</nickname></player>";
					}
				}
				connection.sendMessage(Messager.responseMsg("join_result","<status>true</status><spec>"+game.getChess().getSpecification()+"</spec>"+joinedList));
			}
			else
			{
				Messager.printMsg(connection, "Failed to join game "+game.getName());
				connection.sendMessage(Messager.responseMsg("join_result","<status>false</status><spec/>"));
			}
		}
		else
		{
			Messager.printMsg(connection, "No such game.");
			connection.sendMessage(Messager.responseMsg("join_result","<status>false</status><spec/>"));
		}	
	}
	
	protected void processQuitGame()
	{
		boolean quitSucc = gameManager.getGame(connection.getGameID()).quit(connection.getConnID());
		
		if(quitSucc)
		{
			connection.sendMessage(Messager.responseMsg("quit_result","<status>true</status>"));
		}
		else
		{
			Messager.printMsg(connection,"Failed to quit game?");
			connection.sendMessage(Messager.responseMsg("quit_result","<status>false</status>"));
		}
	}
	
	protected void processListGame()
	{
		Enumeration<Integer> e = gameManager.getGameIDs();
		String resultStr = "";
		while(e.hasMoreElements())
		{
			int i = e.nextElement();
			ChessGame game = gameManager.getGame(i);
			String tmpStr = "";
			tmpStr += "<id>" + game.getID() + "</id>";
			tmpStr += "<name>" + game.getName() + "</name>";
			tmpStr += "<desc>" + game.getDesc() + "</desc>";
			tmpStr += "<creator>" + game.getCreator() + "</creator>";
			resultStr  += "<game>"+tmpStr+"</game>";
		}
		connection.sendMessage(Messager.responseMsg("listGame_result",resultStr));		
	}
	
	protected void processDisconnect()
	{
		ConnectionState state = connection.getState();
		if(state != ConnectionState.INIT && 
			state != ConnectionState.LOGGED &&
			state != ConnectionState.CLOSED)
		{
			ChessGame game = gameManager.getGame(connection.getGameID());
			//Just to ensure to quit current game if it joined one
			game.quit(connection.getConnID());
			//No need to multicast manually as chess.quit()
			//will invoke notify gameManager to multicast autoamatically.
		}
		
		connection.sendMessage(Messager.responseMsg("disconnect_result","true"));
		
		connManager.notifyDisconnectEvent(connection);	
	}
	
	protected void processGameCommand(String type, String param)
	{
		int connID = connection.getConnID();
		ChessGame game = gameManager.getGame(connection.getGameID());
		game.processCommand(connID,type,param);
	}
	//
}
