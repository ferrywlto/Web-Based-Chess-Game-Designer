import flash.geom.*;
class Chess
{
	var chessName:String;
	var playerNum:Number;
	
	var map:Object;
	var regionTypes:Array;
	var regions:Array;
	var objects:Array;
	var objectTypes:Array;
	
	var selectedTile:String;
	var selectedObj:String;
	
	var players:Array;
	var tiles:Array;
	var chess_mc:MovieClip;
	var mk_mc:MovieClip;
	var drawer:otDrawer;
	
	var tHeight:Number;
	var tWidth:Number;
	//drawRect(tile_mc,tWidth,tHeight,0xFFFF00,50,1);
	
	var tileCache:Array;
	// Newly added for graphic support
	var graphics:Array;
	var tileMCLoader:MovieClipLoader;
	var objMCLoader:MovieClipLoader;
	
	public function Chess(){}
	
	public function addTileCache(tileID:String)
	{
		if(this.tileCache == null || this.tileCache == undefined)
			this.tileCache = new Array();
			
		this.tileCache.push(tileID);
		var tile_mc:MovieClip = this.tiles[tileID];
		//drawRect(tile_mc,this.tWidth,this.tHeight,0x88FF88,50,1);
		tile_mc.bound_mc._visible = true;
		
		var self = this;
		tile_mc.bound_mc.onRelease = function ()
		{
			//tile_mc.bound_mc.onRelease = null;
			//delete tile_mc.bound_mc.onRelease;
			self.clearTileCache();
			self.mk_mc._visible = true;
			//trace("this:"+this);
			var xmlCmd:String = "<wbcgdp_command><gameCommand>";
			xmlCmd += "<type>postMove</type>";
			xmlCmd += "<param>"+tile_mc._name+"</param>";
			xmlCmd += "</gameCommand></wbcgdp_command>\n";
			//for debug
			//_root.result_txt.text += (xmlCmd +"\n");
			_global.xmlSock.send(new XML(xmlCmd));	
		};	
	}
	
	public function clearTileCache()
	{
		for(var i=0; i<this.tileCache.length; i++)
		{
			var tileID:String = this.tileCache[i].toString();
			//trace("::tile_mc::"+this.tiles[tileID]);
			this.tiles[tileID].bound_mc.onRelease = null;
			delete this.tiles[tileID].bound_mc.onRelease;
			//drawRect(this.tiles[tileID].bound_mc,tWidth,tHeight,0xFFFF00,50,1);
			this.tiles[tileID].bound_mc._visible = false;
		}
		this.tileCache = null;
		delete this.tileCache;
	}
	
	function resetAssets()
	{
		// Objects
		this.map = new Object();
		this.regionTypes = new Array();
		this.regions = new Array();
		this.objects = new Array();
		this.objectTypes = new Array();
		this.players = new Array();
		this.tiles = new Array();
		this.graphics = new Array();
		this.tileMCLoader = new MovieClipLoader();
		this.objMCLoader = new MovieClipLoader();
		// Values
		this.selectedTile = null;
		this.selectedObj = null;
		this.chessName = null;
		this.playerNum = null;
		// MovieClips
		this.chess_mc = null;
		this.tWidth =0;
		this.tHeight =0;		
	}

	function drawRect(rMC:MovieClip, rWidth:Number, rHeight:Number, rColor:Number, rAlpha:Number, rBorder:Number)
	{
		rMC.clear();
		rMC.lineStyle(rBorder);
		rMC.beginFill(rColor,rAlpha);
		rMC.moveTo(0,0);
		rMC.lineTo(rWidth,0);
		rMC.lineTo(rWidth,rHeight);
		rMC.lineTo(0,rHeight);
		rMC.lineTo(0,0);
		rMC.endFill();
	}
	
	public function clearChessGraphic()
	{
		trace("clearChessGraphic");
		this.chess_mc._visible = false;
		this.chess_mc.removeMovieClip();
		delete this.chess_mc;
		this.mk_mc.visible = false;
		this.mk_mc.removeMovieClip();
		delete this.mk_mc;
	}
	
	public function generateChess(mWidth:Number,mHeight:Number,mX:Number,mY:Number)
	{
		var root_mc:MovieClip = _root;
		root_mc.chess_mc.removeMovieClip();
			
		root_mc.createEmptyMovieClip("chess_mc",root_mc.getNextHighestDepth());
		// WTF need to convert the to number like this.... =_=
		var row:Number = Number(this.map.m_row.toString());
		var col:Number = Number(this.map.m_col.toString());
	
		this.chess_mc = root_mc.chess_mc;
		this.chess_mc._visible = true;
		this.tHeight = mHeight/row;
		this.tWidth = mWidth/col;
		
		
		var tileMCLoadLis:Object = new Object();
		tileMCLoadLis.onLoadInit = function (target_mc:MovieClip, httpStatus:Number)
		{
			target_mc._width = _global.chessGame.tWidth;
			target_mc._height = _global.chessGame.tHeight;	
		};	
		
		var objMCLoadLis:Object = new Object();
		objMCLoadLis.onLoadInit = function (target_mc:MovieClip, httpStatus:Number)
		{
			target_mc._width = _global.chessGame.tWidth;
			target_mc._height = _global.chessGame.tHeight;	
		};	
				
		this.tileMCLoader.addListener(tileMCLoadLis);
		
		this.objMCLoader.addListener(objMCLoadLis);
		
		for(var i=0; i<row; i++)
		{
			for(var j=0; j<col; j++)
			{
				this.chess_mc.createEmptyMovieClip("R"+i+"C"+j,	this.chess_mc.getNextHighestDepth());
				var tile_mc:MovieClip = this.chess_mc["R"+i+"C"+j];
				// Newly added to support graphics
				tile_mc.createEmptyMovieClip("base_mc",tile_mc.getNextHighestDepth());
				tile_mc.createEmptyMovieClip("bound_mc",tile_mc.getNextHighestDepth());
				// Use load graphic instead
				drawRect(tile_mc.bound_mc,tWidth,tHeight,0x88FF88,50,1);
				tile_mc.bound_mc._visible = false;
				
				tile_mc._x = j*tWidth;
				tile_mc._y = i*tHeight;
				this.tiles["R"+i+"C"+j] = tile_mc;
			}
		}
			
		for(var key in this.objects)
		{
			this.chess_mc.createEmptyMovieClip(key,	this.chess_mc.getNextHighestDepth());
			this.objects[key].o_mc = this.chess_mc[key];
			var obj_mc:MovieClip = this.objects[key].o_mc;
		
			//var drawer:otDrawer = this.objectTypes[this.objects[key].o_type];
			//drawer.drawOT(obj_mc,tWidth,tHeight,this.objects[key].o_owner);
			
			var t_mc:MovieClip = this.tiles[this.objects[key].o_loc];
			
			obj_mc._x = t_mc._x;
			obj_mc._y = t_mc._y;
		}
		for(var key in this.graphics)
		{
			var graphicArr:Array = this.graphics[key];
			for(var i = 0; i<graphicArr.length; i++)
			{
				var graphic:Object = graphicArr[i];
				if(graphic.type == "tile")
				{					
					this.tileMCLoader.loadClip(key,this.tiles[graphic.target].base_mc);
				}
				else if(graphic.type == "object")
				{
					this.objMCLoader.loadClip(key,this.objects[graphic.target].o_mc);			}
				}
		}
							
		this.chess_mc.createEmptyMovieClip("mk_mc",chess_mc.getNextHighestDepth());
		this.mk_mc = this.chess_mc.mk_mc;
		drawRect(this.mk_mc,mWidth,mHeight,0x999999,75,0);
		this.mk_mc._x = 0;
		this.mk_mc._y = 0;
		//make objects and tiles cannot be clicked.
		this.mk_mc.onRelease = function(){};
		this.mk_mc.useHandCursor = false;
		this.mk_mc._visible = true;
		this.chess_mc._x = mX;
		this.chess_mc._y = mY;
		
	}
	
	public function enableObjects(player:String)
	{
		this.chess_mc.mk_mc._visible = false;
		//trace("enable objects player:"+player);
		for(var str in this.objects)
		{
			//trace("this.objects["+str+"].o_owner:"+this.objects[str].o_owner);
			if(this.objects[str].o_owner == player)
			{
				//trace("shit? "+str+":"+this.objects[str].o_mc);
				var self = this;
				var realName = str;
				this.objects[str].o_mc.onRelease = function ()
				{
					var xmlCmd:String = "<wbcgdp_command><gameCommand>";
					xmlCmd += "<type>preMove</type>";
					xmlCmd += "<param>"+this._name+"</param>";
					xmlCmd += "</gameCommand></wbcgdp_command>\n";
					//for debug
					//trace("str:"+str);
					//_root.result_txt.text += (xmlCmd +"\n");
					_global.xmlSock.send(new XML(xmlCmd));
				};
			}
			else
			{
				this.objects[str].o_mc.onRelease = null;
				delete this.objects[str].o_mc.onRelease;
			}
		}
	}

	public function createModel(_xml:XML)
	{
		var _xmlNode:XMLNode = _xml.firstChild;
		if(_xmlNode.nodeName == "chess")
		{
			resetAssets();
			this.chessName = _xmlNode.attributes["name"];
			this.playerNum = _xmlNode.attributes["playerNum"];
			for(var i=1; i<=this.playerNum; i++)
			{
				players["#PLAYER_"+i+"#"] = null;
			}
			
			//trace(this.chessName+":"+this.playerNum);

			var subNodes:Array = _xmlNode.childNodes;
			for(var i=0; i<subNodes.length; i++)
			{
				if(subNodes[i].nodeName == "map")
					makeMapNode(subNodes[i]);
				else if(subNodes[i].nodeName == "regionType")
					makeRegionTypeNode(subNodes[i]);
				else if(subNodes[i].nodeName == "region")
					makeRegionNode(subNodes[i]);
				else if(subNodes[i].nodeName == "object")
					makeObjectNode(subNodes[i]);
				else if(subNodes[i].nodeName == "objectType")
					makeObjectTypeNode(subNodes[i]);
				else if(subNodes[i].nodeName == "graphic")
					makeGraphicNode(subNodes[i]);
			}
		}
		else
		{
			//trace("shit");
		}		
	}
	
	function getObjectTypesNum():Number
	{
		var num:Number = 0;
		for(var key in this.objectTypes)
			num++;
		return num;
	}
	
	function makeObjectTypeNode(_node:XMLNode)
	{
		this.objectTypes[_node.attributes["name"]] = new otDrawer(getObjectTypesNum());
		// <objectType name="cat" preMove="catPreMove" postMove="catPostMove"><objectAttribute type="power" value="1"/></objectType>
	}
	
	function makeMapNode(_node:XMLNode)
	{
		this.map.m_type = _node.attributes["type"];
		//trace("this.map.m_type:"+this.map.m_type);
		this.map.m_row = _node.childNodes[0].firstChild;
		//trace("this.map.m_row:"+this.map.m_row);
		this.map.m_col = _node.childNodes[1].firstChild;
		//trace("this.map.m_col:"+this.map.m_col);
		this.map.m_mc = null;
	}
	
	function makeRegionTypeNode(_node:XMLNode)
	{
		//this.regionTypes.push(new Object({rt_name:_node.attributes["name"]}));
		//trace("regionType:"+_node.attributes["name"]);
		var str:String = _node.attributes["name"];
		this.regionTypes[str] = str;
	}
	
	function makeObjectNode(_node:XMLNode)
	{
		var temp:Object = new Object();
		temp.o_name = _node.attributes["name"];
		//trace(temp.o_name);
		temp.o_type = _node.attributes["type"];
		//trace(temp.o_type);		
		temp.o_loc = _node.attributes["location"];		
		//trace(temp.o_loc);		
		temp.o_owner = _node.attributes["owner"];
		//trace(temp.o_owner);
		temp.o_mc = null;		
		//this.objects.push(temp);
		this.objects[_node.attributes["name"]] = temp;
		//<object type="cat" name="p1cat" location="R2C3" owner="#PLAYER_1#" />
	}
	
	function makeRegionNode(_node:XMLNode)
	{
		var temp:Object = new Object();
		temp.r_name = _node.attributes["name"];
		//trace(temp.r_name);
		temp.r_type = _node.attributes["type"];
		//trace(temp.r_type);
		temp.r_owner = _node.attributes["owner"];
		//trace(temp.r_owner);
		
		temp.r_tiles = new Array();
		for(var i=0; i<_node.childNodes.length; i++)
		{
			var str:String = _node.childNodes[i].attributes["name"];
			temp.r_tiles[str] = str;
			//temp.r_tiles.push(new Object({t_name:_node.childNodes[i].attributes["name"]}));
			//trace(str);
		}
		this.regions[_node.attributes["name"]] = temp;	
		//this.regions.push(temp);
	}
	
	function makeGraphicNode(_node:XMLNode)
	{
		/*
		 <graphic fileURI="">
		 	<graphicEntry type="tile" target="R0C0"/>
		 	<graphicEntry type="object" target="p1cat"/>
		 </graphic>		 		 */
		var fileStr:String = _node.attributes["fileURI"];
		var fileEntries:Array = new Array();
		for(var i=0; i<_node.childNodes.length; i++)
		{
			var obj:Object = new Object();
			obj.type = _node.childNodes[i].attributes["type"];
			obj.target = _node.childNodes[i].attributes["target"];
			fileEntries.push(obj);
		}
		this.graphics[fileStr] = fileEntries;
	}		
}