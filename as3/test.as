import  mx.controls.Alert;

var xmlStr:String = "<designInterfaceRequest user='' password=''>";
xmlStr += "<chess name='Animal Chess Emulation' playerNum='2' turnCheck='catGameTurn'>";
xmlStr += "<map type='grid'><param>7</param><param>7</param></map></chess>";
xmlStr += "<description>testing haha 1</description></designInterfaceRequest>";

var xml:XML = new XML();
xml.ignoreWhite = true;
xml.onLoad = function(success:Boolean)
{
	if(success)
	{
		trace("talking to server...");
		xml = new XML("<designInterfaceRequest user='ferry' password='subai'>"+xml.toString()+"<description>testing haha 1</description></designInterfaceRequest>");
		//trace(xml);
		xml.sendAndLoad("http://127.0.0.1/WebBasedChessGameDesigner/servlet/SpecificationReceiver",xmlR);
	}
	else
	{
		trace("fuck");
	}
}

var xmlR:XML = new XML();
xmlR.ignoreWhite = true;
xmlR.onLoad = function(success:Boolean)
{
	if(success)
	{
		trace("this:"+this);
		//trace("xmLR:"+xmlR);
		var statusStr:String = this.firstChild.childNodes[0].firstChild.toString();
		var msg:String =  this.firstChild.childNodes[1].firstChild.toString();
		if(statusStr == "true");
			Alert.show("Specification uploaded successfully.", "Information", Alert.OK);
		else
			Alert.show("Error encountered: "+msg, "Information", Alert.OK);
	}
	else
	{
		trace("diu");
	}
}

xml.load("../repository/real2.xml",xml);
/**  Sample XML content from Flash client's HTTP request 
<designInterfaceRequest user="ferry" password="subai">
<chess name="Animal Chess Emulation" playerNum="2" turnCheck="catGameTurn">
<map type="grid"><param>7</param><param>7</param></map>
<regionType name="water" />
<regionType name="cave" />
<region name="p1cave" type="cave" owner="#PLAYER_1#">
	<tile name="R0C3" />
</region>
<region name="p2cave" type="cave" owner="#PLAYER_2#">
	<tile name="R6C3" />
</region>
<region name="river" type="water" owner="#PLAYER_NETURAL#">
	<tile name="R3C2" /><tile name="R3C3" /><tile name="R3C4" />
</region>
<objectAttributeType name="power" type="number" />
<objectType name="mice" preMove="catPreMove" postMove="catPostMove">
	<objectAttribute type="power" value="1" />
</objectType>
<objectType name="cat" preMove="catPreMove" postMove="catPostMove">
	<objectAttribute type="power" value="2" />
</objectType>
<objectType name="dog" preMove="catPreMove" postMove="catPostMove">
	<objectAttribute type="power" value="3" />
</objectType>
<objectType name="fox" preMove="catPreMove" postMove="catPostMove">
	<objectAttribute type="power" value="4" />
</objectType>
<objectType name="wolf" preMove="catPreMove" postMove="catPostMove">
	<objectAttribute type="power" value="5" />
</objectType>
<objectType name="tiger" preMove="catPreMove" postMove="catPostMove">
	<objectAttribute type="power" value="6" />
</objectType>
<objectType name="lion" preMove="catPreMove" postMove="catPostMove">
	<objectAttribute type="power" value="7" />
</objectType>
<objectType name="elephant" preMove="catPreMove" postMove="catPostMove">
	<objectAttribute type="power" value="8" />
</objectType>
<object type="cat" name="p1cat" location="R1C0" owner="#PLAYER_1#" />
<object type="dog" name="p1dog" location="R1C1" owner="#PLAYER_1#" />
<object type="mice" name="p1mice" location="R1C2" owner="#PLAYER_1#" /
><object type="tiger" name="p1tiger" location="R1C3" owner="#PLAYER_1#" />
<object type="wolf" name="p1wolf" location="R1C4" owner="#PLAYER_1#" />
<object type="fox" name="p1fox" location="R1C5" owner="#PLAYER_1#" />
<object type="elephant" name="p1elephant" location="R1C6" owner="#PLAYER_1#" />
<object type="lion" name="p1lion" location="R2C3" owner="#PLAYER_1#" />
<object type="cat" name="p2cat" location="R5C0" owner="#PLAYER_2#" />
<object type="dog" name="p2dog" location="R5C1" owner="#PLAYER_2#" />
<object type="mice" name="p2mice" location="R5C2" owner="#PLAYER_2#" />
<object type="tiger" name="p2tiger" location="R5C3" owner="#PLAYER_2#" />
<object type="wolf" name="p2wolf" location="R5C4" owner="#PLAYER_2#" />
<object type="fox" name="p2fox" location="R5C5" owner="#PLAYER_2#" />
<object type="elephant" name="p2elephant" location="R5C6" owner="#PLAYER_2#" />
<object type="lion" name="p2lion" location="R4C3" owner="#PLAYER_2#" />
<rule name="catGameTurn">
	<ruleComponent>
		<condition name="1"><param>cave</param></condition>
		<action name="2" />
	</ruleComponent>
</rule>
<rule name="catPreMove">
	<ruleComponent>
		<action name="5" />
	</ruleComponent>
</rule>
<rule name="catPostMove">
	<ruleComponent>
		<action name="1" />
	</ruleComponent>
</rule>
</chess>
<description>testing haha 1</description>
</designInterfaceRequest>
*/
var str:String = "<node label='Chess'>";
str += "<node label='Region Type' data='regionType' isBranch='true' >";
str += "<node label='water' data='water' isBranch='false'/>";
str += "<node label='cave' data='water' isBranch='false'/>";
str += "</node>";

str += "<node label='Region' data='region' isBranch='true' >";
str += "<node label='p1cave' data='p1cave' isBranch='false'/>";
str += "<node label='p2cave' data='p2cave' isBranch='false'/>";
str += "<node label='river' data='river' isBranch='false'/>";
str += "</node>";

str += "<node label='Object Attribute Type' data='objectAttributeType' isBranch='true' >";
str += "<node label='power' data='power' isBranch='false'/>";
str += "</node>";

str += "<node label='Object Type' data='objectType' isBranch='true' >";
str += "<node label='mice' data='mice' isBranch='false'/>";
str += "<node label='cat' data='cat' isBranch='false'/>";
str += "<node label='dog' data='dog' isBranch='false'/>";
str += "<node label='fox' data='fox' isBranch='false'/>";
str += "<node label='wolf' data='wolf' isBranch='false'/>";
str += "<node label='tiger' data='tiger' isBranch='false'/>";
str += "<node label='lion' data='lion' isBranch='false'/>";
str += "<node label='elephant' data='elephant' isBranch='false'/>";
str += "</node>";

str += "<node label='Object' data='object' isBranch='true' >";
str += "<node label='p1mice' data='p1mice' isBranch='false'/>";
str += "<node label='p1cat' data='p1cat' isBranch='false'/>";
str += "<node label='p1dog' data='p1dog' isBranch='false'/>";
str += "<node label='p1fox' data='p1fox' isBranch='false'/>";
str += "<node label='p1wolf' data='p1wolf' isBranch='false'/>";
str += "<node label='p1tiger' data='p1tiger' isBranch='false'/>";
str += "<node label='p1lion' data='p1lion' isBranch='false'/>";
str += "<node label='p1elephant' data='p1elephant' isBranch='false'/>";
str += "<node label='p2mice' data='p2mice' isBranch='false'/>";
str += "<node label='p2cat' data='p2cat' isBranch='false'/>";
str += "<node label='p2dog' data='p2dog' isBranch='false'/>";
str += "<node label='p2fox' data='p2fox' isBranch='false'/>";
str += "<node label='p2wolf' data='p2wolf' isBranch='false'/>";
str += "<node label='p2tiger' data='p2tiger' isBranch='false'/>";
str += "<node label='p2lion' data='p2lion' isBranch='false'/>";
str += "<node label='p2elephant' data='p2elephant' isBranch='false'/>";
str += "</node>";

str += "<node label='Rule' data='rule' isBranch='true' >";
str += "<node label='catGameTurn' data='catGameTurn' isBranch='false'/>";
str += "<node label='catPreMove' data='catPreMove' isBranch='false'/>";
str += "<node label='catPostMove' data='catPostMove' isBranch='false'/>";
str += "</node>";

str += "<node label='Mail'><node label='INBOX'/>";
str += "<node label='Personal Folder'><node label='Business' isBranch='true' />";
str += "<node label='Demo' isBranch='true' /><node label='Personal' isBranch='true' />";
str += "<node label='Saved Mail' isBranch='true' /><node label='bar' isBranch='true' />";
str += "</node><node label='Sent' isBranch='true' /><node label='Trash'/></node></node>";

var xml3:XML = new XML(str);
xml3.ignoreWhite = true;
_root.spec_tree.dataProvider = xml3;

_global.chess = new Object();

_global.chess.regionTypes = new Array();
_global.chess.regions = new Array();
_global.chess.objects = new Array();
_global.chess.objectTypes = new Array();
_global.chess.attributeTypes = new Array();	
_global.chess.rules = new Array();	

_global.chess.tiles = new Array();
_global.chess.players = new Array();

_global.chess.removeTreeNode = function()
{
	
}

_global.chess.updateGeneralForm = function(name:String,desc:String,rule:String,row:Number,col:Number)
{
	_global.chess.chess_name = name;
	_global.chess.chess_desc = desc;
	_global.chess.turn_rule = rule;
	_global.chess.map_row = row;
	_global.chess.map_col = col
}

_global.chess.uploadSpec = function(username:String,password:String,url:String)
{
	var chessObj:Object = _global.chess;
	var spec:String = "<designInterfaceRequest user='"+username+"' password='"+password+"'>";
	spec += "<chess name='"+chessObj.chess_name+"' playerNum='2' turnCheck='"+chessObj.turn_rule+"'>";
	spec += "<map type='grid'><param>"+chessObj.map_row+"</param><param>"+chessObj.map_col+"</param></map>";
	for(var key in chessObj.regionTypes)
	{
		var rtName:String = chessObj.regionTypes[key]._name;
		if(rtName != null && rtName != undefined)
			spec += "<regionType name='"+rtName+"' />";
	}
	for(var key in chessObj.regions)
	{
		var region:Object = chessObj.regions[key];
		if(region != null && region != undefined)
		{
			spec += "<region name='"+region._name+"' type='"+region._type+"' owner='"+region._owner+"'>";
			for(var innerKey in region.r_tiles)
			{
				if(region.r_tiles[innerKey] != null && region.r_tiles[innerKey] != undefined)
					spec += "<tile name='"+innerKey+"'>";
			}
			spec += "</region>";
		}
	}	
	for(var key in chessObj.attributeTypes)
	{
		var oaType:Object = chessObj.attributeTypes[key];
		if(oaType != null && oaType != undefined)
		{
			spec += "<objectAttributeType name='"+oaType._name+"' type='"oaType._type"' />";
		}
	}
	for(var key in chessObj.objectTypes)
	{
		var oType:Object = chessObj.objectTypes[key];
		if(oType != null && oType != undefined)
		{
			spec += "<objectType name='"+oType._name+"' preMove='"+oType._pre+"' postMove='"+oType._post+"'>";
			for(var innerKey in oType.attrs)
			{
				var attrs:Object = oType.attrs[innerKey];
				if(attrs != null && attrs != undefined)
				{
					spec += "<objectAttribute type='"+attrs._type+"' value='"+attrs._value+"' />";
				}
			}
			spec += "</objectType>";
		}
	}
	for(var key in chessObj.objects)
	{
		var obj:Object = chessObj.objects[key];
		if(obj != null && obj != undefined)
		{
			spec += "<object type='"+obj._type+"' name='"+obj._name+"' location='"+obj._loc+"' owner='"+obj._player+"' />";
		}
	}
	for(var key in chessObj.rules)
	{
		var rule:Object = chessObj.rules[key];
		if(rule != null && rule != undefined)
		{
			spec += "<rule name='"+rule._name+"'>";
			for(var innerKey in rule.components)
			{
				var comp:Object = rule.components[innerKey];
				if(comp != null && comp != undefined)
				{
					spec += "<ruleComponent>";
					for(var deeperKey in comp.conditions)
					{
						var condition:Object = comp.conditions[deeperKey];
						if(condition != null && condition != undefined)
						{
							spec += "<condition name='"+condition._name+"'>";
							for(var deepestKey in condition.params)
							{
								var param:Object = condition.params[deepestKey];
								if(param != null && param != undefined)
								{
									spec += "<param>"+param._value+"</param>";
								}
							}
							spec += "</condition>";
						}
					}
					for(var deeperKey in comp.actions)
					{
						var action:Object = comp.actions[deeperKey];
						if(action != null && action != undefined)
						{
							spec += "<action name='"+action._name+"'>";
							for(var deepestKey in action.params)
							{
								var param:Object = action.params[deepestKey];
								if(param != null && param != undefined)
								{
									spec += "<param>"+param._value+"</param>";
								}
							}							
							spec += "</action>";
						}
					}	
					spec += "</ruleComponent>";					
				}
			}
			spec += "</rule>";
		}
	}
	spec += "</chess>";
	spec += "<description>"+chessObj.chess_desc+"</description>";
	spec += "</designInterfaceRequest>";
	trace(spec);
}

_global.chess.serializeSpec = function()
{
trace("sigh");
}
