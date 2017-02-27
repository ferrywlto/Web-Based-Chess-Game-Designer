import Chess;
import mx.controls.TextArea;
import mx.controls.TextInput;
import mx.controls.Button;
import mx.controls.Alert;
import mx.containers.Window;

_global.chessGame = new Chess();
_global.joined = false;
_global.username = "";
_global.assignedPlayer = "";

_global.xmlSock = new XMLSocket();
xmlSock.onConnect = function(success:Boolean) 
{
	if(success)
	{
		_root.connect_btn.enabled = false;
		showMsg("Successfully connected to server.");
	}
	else
	{
		showMsg("Failed to connect.");
	}
};
_global.xmlSock.onClose = function()
{
	_global.chessGame.clearChessGraphic();
	_root.connect_btn.enabled = true;
	showMsg("Connection closed.");
};

_global.xmlSock.onXML = function(dataRec:XML)
{
	// WTF you need to make a new XML Document with ignoreWhite = ture everytime an XML doc is received!!!!!
	var newXML:XML = new XML();
	newXML.ignoreWhite = true;
	newXML.parseXML(dataRec.toString());
	
	//showMsg(newXML.toString());
	//trace(newXML);
	//trace(newXML.firstChild.toString());
	
	var type_str:String = newXML.firstChild.attributes.type;
	if(newXML.firstChild.nodeName == "wbcgdp_response")
	{
		var resultNode:XMLNode = newXML.firstChild.firstChild;
		if(type_str.toString() == "login_result")
		{
			if(resultNode.firstChild.toString()=="true")
			{
				_global.username = _root.u_txt.text;
				showMsg("Login success.");
			}
			else
				showMsg("Failed to login.");
		}
		else if(type_str.toString() == "listChess_result")
		{
			if(resultNode.childNodes.length == 0)
				showMsg("You have not designed any chess game.");
			else
			{
				showMsg("Chess games designed:");
				for(var i=0;  i<resultNode.childNodes.length; i++)
				{
					var str:String = "Chess Name: "+resultNode.childNodes[i].childNodes[0].firstChild;
					str += "\tDescription: "+resultNode.childNodes[i].childNodes[1].firstChild
					showMsg(str);
				}
			}
		}
		else if(type_str == "listGame_result")
		{
			if(resultNode.childNodes.length == 0)
				showMsg("No games available.");
			else
			{
				showMsg("Available games:");
				for(var i=0;  i<resultNode.childNodes.length; i++)
				{
					var gID:String = resultNode.childNodes[i].childNodes[0].firstChild.toString();
					var gName:String = resultNode.childNodes[i].childNodes[1].firstChild.toString();
					var gDesc:String = resultNode.childNodes[i].childNodes[2].firstChild.toString();
					var gCreator:String = resultNode.childNodes[i].childNodes[3].firstChild.toString();
					showMsg("Game Name: "+gName+" ("+gID+")\tCreator: "+gCreator+"\tDescription: "+gDesc);
				}
			}			
		}
		else if(type_str == "create_result")
		{
			var statusNode:XMLNode = resultNode.childNodes[0];
			//trace("statusNode.firstChild:"+statusNode.firstChild.toString());
			if(statusNode.firstChild.toString() == "true")
			{		
				var specNode:XMLNode = resultNode.childNodes[1];
				_global.chessGame.createModel(specNode);
				_global.chessGame.generateChess(350,350,125,90);
				for(var i=2; i<resultNode.childNodes.length; i++)
				{
					var tempNode:XMLNode = resultNode.childNodes[i];	
					//trace(tempNode.nodeName);					
					if(tempNode.nodeName == "player")
					{
						var slotName:String = tempNode.childNodes[0].firstChild.toString();
						var nickName:String = tempNode.childNodes[1].firstChild.toString();	
						_global.chessGame.players[slotName] = nickName;
						if(nickName == _global.username)
						{
							_global.assignedPlayer = slotName;
							showMsg(nickName+" has joined the game.");
						}
					}
				}
				_global.joined = true;
			}
			else
			{
				showMsg("Failed to create game.");
			}
		}
		else if(type_str == "join_result")
		{
			var statusNode:XMLNode = resultNode.childNodes[0];
			//trace("statusNode.firstChild:"+statusNode.firstChild.toString());
			if(statusNode.firstChild.toString() == "true")
			{
				var specNode:XMLNode = resultNode.childNodes[1];			
				_global.chessGame.createModel(specNode);
				_global.chessGame.generateChess(350,350,125,90);		
				for(var i=2; i<resultNode.childNodes.length; i++)
				{
					var tempNode:XMLNode = resultNode.childNodes[i];		
					if(tempNode.nodeName == "player")
					{
						var slotName:String = tempNode.childNodes[0].firstChild.toString();
						var nickName:String = tempNode.childNodes[1].firstChild.toString();	
						_global.chessGame.players[slotName] = nickName;
						if(nickName == _global.username)
						{
							_global.assignedPlayer = slotName;
							showMsg(nickName+" has joined the game.");
						}
					}
				}
				_global.joined = true;			
			}
			else
			{
				showMsg("Failed to join game.");
			}		
		}
		else if(type_str == "game_event")
		{
			var event_typeNode:XMLNode = resultNode.childNodes[0];
			var event_type:String = event_typeNode.nodeName;
			var event_msg:String  = resultNode.childNodes[1].firstChild;
			if(_global.joined == true)
			{
				if(event_type == "join")
				{
					var slot:String = event_typeNode.childNodes[0].firstChild.toString();
					var who:String = event_typeNode.childNodes[1].firstChild.toString();
					_global.chessGame.players[slot] = who;
					showMsg(event_msg);
				}
				else if(event_type == "start")
				{
					showMsg(event_msg);
				}
				else if(event_type == "turn")
				{
					//_global.chessGame.redrawAll();
					var slot:String = event_typeNode.childNodes[0].firstChild.toString();
					var who:String = event_typeNode.childNodes[1].firstChild.toString();
					if(slot == _global.assignedPlayer && who == _global.username)
					{
						_global.chessGame.enableObjects(_global.assignedPlayer);
					}
					else
					{
						_global.chessGame.disableObjects();
					}
					showMsg(event_msg);
				}
				else if(event_type == "action")
				{
					for(var i=0; i<event_typeNode.childNodes.length; i++)
					{
						var actionNode:XMLNode = event_typeNode.childNodes[i];
						if(actionNode.nodeName == "move")
						{
							var objID:String = actionNode.childNodes[0].firstChild.toString();
							var tileID:String = actionNode.childNodes[1].firstChild.toString();
							var obj_mc:MovieClip = _global.chessGame.objects[objID].o_mc;
							var tile_mc:MovieClip = _global.chessGame.chess_mc[tileID];
							obj_mc._x = tile_mc._x;
							obj_mc._y = tile_mc._y;
							showMsg("Object "+objID+" has moved to location "+tileID+".");
						}
						else if(actionNode.nodeName == "remove")
						{
							var objID:String = actionNode.childNodes[0].firstChild.toString();
							var obj_mc:MovieClip = _global.chessGame.objects[objID].o_mc;
							obj_mc._visible = false;
							obj_mc.removeMovieClip();
							showMsg("Object "+objID+" has removed.");
						}
					}
				}
				else if(event_type == "win")
				{
					var winner:String = event_typeNode.firstChild.toString();
					//_root.result_txt.text += "winner:"+winner+" @"+_global.username;
					if(winner == _global.assignedPlayer)
						Alert.show("You win the game!","Information",Alert.OK,null,null,null,Alert.OK);
					else
						Alert.show("Sorry, you lose!","Information",Alert.OK,null,null,null,Alert.OK);
					showMsg(event_msg);
				}
				else if(event_type == "terminate")
				{
					showMsg(event_msg);
					_global.chessGame.clearChessGraphic();
				}
			}
			else{
			//	trace("this game event should not notify to client as he has not joined any game.");			
			}
			
			//showMsg(event_msg.toString());
		}
		else if(type_str == "gameCommand_result")
		{
			var cmdType:String = resultNode.firstChild.nodeName;
			//trace("cmdType:"+cmdType);
			if(cmdType == "preMove")
			{
				_global.chessGame.clearTileCache();
				//_global.chessGame.redrawAll();
				var nodes:Array = resultNode.firstChild.childNodes;
				for(var i=0; i<nodes.length; i++)
				{
					var node:XMLNode = nodes[i];
					_global.chessGame.addTileCache(node.firstChild.toString());
				}
			}
			else if(cmdType == "postMove")
			{
				
			}
			//showMsg("gameCommand_result:"+resultNode.firstChild);
		}
		else if(type_str == "error")
		{
			var msg:XMLNode = resultNode.firstChild;
			showMsg("Error encountered: "+msg.toString());
			_global.xmlSock.disconnect();
		}
	}
	else
	{
		//trace("unknown XML document. first child:"+newXML.firstChild.nodeName);
		//showMsg("unknown:"+newXML.toString());
	}
};


function showMsg(msg:String)
{
	_root.result_txt.text += (msg+"\n");
	_root.result_txt.vPosition = _root.result_txt.maxVPosition;
}

stop();

//disconnect_result
//listGame_result
//quit_result
//join_result
//create_result
//listChess_result
//login_result
/*
<graphicEntry type='tile' target='R0C0'/>
<graphicEntry type='tile' target='R0C1'/>
<graphicEntry type='tile' target='R0C2'/>
<graphicEntry type='tile' target='R0C4'/>
<graphicEntry type='tile' target='R0C5'/>
<graphicEntry type='tile' target='R0C6'/>
<graphicEntry type='tile' target='R1C0'/>
<graphicEntry type='tile' target='R1C1'/>
<graphicEntry type='tile' target='R1C2'/>
<graphicEntry type='tile' target='R1C3'/>
<graphicEntry type='tile' target='R1C4'/>
<graphicEntry type='tile' target='R1C5'/>
<graphicEntry type='tile' target='R1C6'/>
<graphicEntry type='tile' target='R2C0'/>
<graphicEntry type='tile' target='R2C1'/>
<graphicEntry type='tile' target='R2C2'/>
<graphicEntry type='tile' target='R2C3'/>
<graphicEntry type='tile' target='R2C4'/>
<graphicEntry type='tile' target='R2C5'/>
<graphicEntry type='tile' target='R2C6'/>
<graphicEntry type='tile' target='R3C0'/>
<graphicEntry type='tile' target='R3C1'/>
<graphicEntry type='tile' target='R3C5'/>
<graphicEntry type='tile' target='R3C6'/>
<graphicEntry type='tile' target='R4C0'/>
<graphicEntry type='tile' target='R4C1'/>
<graphicEntry type='tile' target='R4C2'/>
<graphicEntry type='tile' target='R4C3'/>
<graphicEntry type='tile' target='R4C4'/>
<graphicEntry type='tile' target='R4C5'/>
<graphicEntry type='tile' target='R4C6'/>
<graphicEntry type='tile' target='R5C0'/>
<graphicEntry type='tile' target='R5C1'/>
<graphicEntry type='tile' target='R5C2'/>
<graphicEntry type='tile' target='R5C3'/>
<graphicEntry type='tile' target='R5C4'/>
<graphicEntry type='tile' target='R5C5'/>
<graphicEntry type='tile' target='R5C6'/>
<graphicEntry type='tile' target='R6C0'/>
<graphicEntry type='tile' target='R6C1'/>
<graphicEntry type='tile' target='R6C2'/>
<graphicEntry type='tile' target='R6C4'/>
<graphicEntry type='tile' target='R6C5'/>
<graphicEntry type='tile' target='R6C6'/>

<graphicEntry type='tile' target='R0C3'/>
<graphicEntry type='tile' target='R6C3'/>

<graphicEntry type='tile' target='R3C2'/>
<graphicEntry type='tile' target='R3C3'/>
<graphicEntry type='tile' target='R3C4'/>

<graphic fileURI="chessGraphics/catA.png">
<graphicEntry type='object' target='p1cat'/>
</graphic>
<graphic fileURI="chessGraphics/catB.png">
<graphicEntry type='object' target='p2cat'/>
</graphic>

<graphic fileURI="chessGraphics/dogA.png">
<graphicEntry type='object' target='p1dog'/>
</graphic>
<graphic fileURI="chessGraphics/dogB.png">
<graphicEntry type='object' target='p2dog'/>
</graphic>

<graphic fileURI="chessGraphics/tigerA.png">
<graphicEntry type='object' target='p1tiger'/>
</graphic>
<graphic fileURI="chessGraphics/tigerB.png">
<graphicEntry type='object' target='p2tiger'/>
</graphic>

<graphic fileURI="chessGraphics/lionA.png">
<graphicEntry type='object' target='p1lion'/>
</graphic>
<graphic fileURI="chessGraphics/lionB.png">
<graphicEntry type='object' target='p2lion'/>
</graphic>

<graphicEntry type='object' target='p1cat'/>
<graphicEntry type='object' target='p1dog'/>
<graphicEntry type='object' target='p1lion'/>
<graphicEntry type='object' target='p1tiger'/>
<graphicEntry type='object' target='p1cat'/>
<graphicEntry type='object' target='p1cat'/>
*/