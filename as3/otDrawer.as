class otDrawer
{
	var drawOT:Function;

	public function otDrawer(ot_type_index:Number)
	{
		switch(ot_type_index)
		{
			case 0: drawOT = drawOT1; break;
			case 1: drawOT = drawOT2; break;
			case 2: drawOT = drawOT3; break;
			case 3: drawOT = drawOT4; break;
			case 4: drawOT = drawOT5; break;
			case 5: drawOT = drawOT6; break;
			case 6: drawOT = drawOT7; break;
			case 7: drawOT = drawOT8; break;
			default: drawOT = drawOT8; break;
		}
	}
	
	public function draw()
	{
		
	}
	
	public function getPlayerColor(player:String):Number
	{
		if(player == "#PLAYER_1#")
			return 0xFF0000;
		else if(player == "#PLAYER_2#")
			return 0x00FF00;
		else if(player == "#PLAYER_3#")
			return 0x0000FF;
		else if(player == "#PLAYER_4#")
			return 0x00FFFF;
		else if(player == "#PLAYER_5#")
			return 0xFF00FF;
		else if(player == "#PLAYER_6#")
			return 0xFFFF00;
	}
	
	function drawOT1(o_mc:MovieClip,tWidth:Number,tHeight:Number,player:String)
	{
		o_mc.moveTo(0,tHeight/2);
		o_mc.lineStyle(1);
		o_mc.beginFill(getPlayerColor(player),75);
		o_mc.lineTo(tWidth/2,tHeight);
		o_mc.lineTo(tWidth,tHeight/2);
		o_mc.lineTo(tWidth/2,0);
		o_mc.lineTo(0,tHeight/2);
		o_mc.endFill();		
	}
	
	function drawOT2(o_mc:MovieClip,tWidth:Number,tHeight:Number,player:String)
	{
		o_mc.moveTo(tWidth/4,tHeight/2);
		o_mc.lineStyle(1);
		o_mc.beginFill(getPlayerColor(player),75);
		o_mc.lineTo(0,0);
		o_mc.lineTo(tWidth/2,tHeight/4);
		o_mc.lineTo(tWidth,0);
		o_mc.lineTo(tWidth/4*3,tHeight/2);
		o_mc.lineTo(tWidth,tHeight);
		o_mc.lineTo(tWidth/2,tHeight/4*3);
		o_mc.lineTo(0,tHeight);
		o_mc.lineTo(tWidth/4,tHeight/2);
		o_mc.endFill();		
	}
	
	function drawOT3(o_mc:MovieClip,tWidth:Number,tHeight:Number,player:String)
	{
		o_mc.moveTo(0,tHeight/4);
		o_mc.lineStyle(1);
		o_mc.beginFill(getPlayerColor(player),75);
		//o_mc.lineTo(0,tHeight/4*3);
		o_mc.lineTo(tWidth/4,tHeight/4);
		o_mc.lineTo(tWidth/4,0);
		o_mc.lineTo(tWidth/4*3,0);
		o_mc.lineTo(tWidth/4*3,tHeight/4);
		o_mc.lineTo(tWidth,tHeight/4);
		o_mc.lineTo(tWidth,tHeight/4*3);
		o_mc.lineTo(tWidth/4*3,tHeight/4*3);
		o_mc.lineTo(tWidth/4*3,tHeight);
		o_mc.lineTo(tWidth/4,tHeight);
		o_mc.lineTo(tWidth/4,tHeight/4*3);
		o_mc.lineTo(0,tHeight/4*3);
		o_mc.lineTo(0,tHeight/4);
		o_mc.endFill();	
	}
	function drawOT4(o_mc:MovieClip,tWidth:Number,tHeight:Number,player:String)
	{
		o_mc.moveTo(0,tHeight/3);
		o_mc.lineStyle(1);
		o_mc.beginFill(getPlayerColor(player),75);
		o_mc.lineTo(tWidth/3,0);
		o_mc.lineTo(tWidth/3*2,0);
		o_mc.lineTo(tWidth,tHeight/3);
		o_mc.lineTo(tWidth,tHeight/3*2);
		o_mc.lineTo(tWidth/3*2,tHeight);
		o_mc.lineTo(tWidth/3,tHeight);
		o_mc.lineTo(0,tHeight/3*2);
		o_mc.lineTo(0,tHeight/3);
		o_mc.endFill();			
	}
	function drawOT5(o_mc:MovieClip,tWidth:Number,tHeight:Number,player:String)
	{
		o_mc.moveTo(0,tHeight/2);
		o_mc.lineStyle(1);
		o_mc.beginFill(getPlayerColor(player),75);
		o_mc.lineTo(tWidth/3,tHeight/3);
		o_mc.lineTo(tWidth/2,0);
		o_mc.lineTo(tWidth/3*2,tHeight/3);
		o_mc.lineTo(tWidth,tHeight/2);
		o_mc.lineTo(tWidth/3*2,tHeight/3*2);
		o_mc.lineTo(tWidth/2,tHeight);
		o_mc.lineTo(tWidth/3,tHeight/3*2);
		o_mc.lineTo(0,tHeight/2);
		o_mc.endFill();			
	}
	function drawOT6(o_mc:MovieClip,tWidth:Number,tHeight:Number,player:String)
	{
		o_mc.moveTo(0,tHeight/3);
		o_mc.lineStyle(1);
		o_mc.beginFill(getPlayerColor(player),75);
		o_mc.lineTo(tWidth/2,tHeight/2);
		o_mc.lineTo(tWidth/3,0);
		o_mc.lineTo(tWidth/3*2,0);
		o_mc.lineTo(tWidth/2,tHeight/2);
		o_mc.lineTo(tWidth,tHeight/3);
		o_mc.lineTo(tWidth,tHeight/3*2);
		o_mc.lineTo(tWidth/2,tHeight/2);	
		o_mc.lineTo(tWidth/3*2,tHeight);
		o_mc.lineTo(tWidth/3,tHeight);
		o_mc.lineTo(tWidth/2,tHeight/2);
		o_mc.lineTo(0,tHeight/3*2);
		o_mc.lineTo(0,tHeight/3);
		o_mc.endFill();			
	}
	function drawOT7(o_mc:MovieClip,tWidth:Number,tHeight:Number,player:String)
	{
		o_mc.moveTo(0,tHeight/4);
		o_mc.lineStyle(1);
		o_mc.beginFill(getPlayerColor(player),75);
		o_mc.lineTo(tWidth/4,tHeight/4);
		o_mc.lineTo(tWidth/4,0);
		o_mc.lineTo(tWidth/2,tHeight/2);
		
		o_mc.lineTo(tWidth/4*3,0);
		o_mc.lineTo(tWidth/4*3,tHeight/4);
		o_mc.lineTo(tWidth,tHeight/4);
		o_mc.lineTo(tWidth/2,tHeight/2);
		
		o_mc.lineTo(tWidth,tHeight/4*3);
		o_mc.lineTo(tWidth/4*3,tHeight/4*3);
		o_mc.lineTo(tWidth/4*3,tHeight);
		o_mc.lineTo(tWidth/2,tHeight/2);
		
		o_mc.lineTo(tWidth/4,tHeight);
		o_mc.lineTo(tWidth/4,tHeight/4*3);
		o_mc.lineTo(0,tHeight/4*3);
		o_mc.lineTo(tWidth/2,tHeight/2);
		
		o_mc.lineTo(0,tHeight/4);
		o_mc.endFill();				
	}
	function drawOT8(o_mc:MovieClip,tWidth:Number,tHeight:Number,player:String)
	{
		o_mc.moveTo(0,tHeight/4);
		o_mc.lineStyle(1);
		o_mc.beginFill(getPlayerColor(player),75);
		o_mc.lineTo(tWidth/4,0);
		o_mc.lineTo(tWidth/2,tHeight/4);
		
		o_mc.lineTo(tWidth/4*3,0);
		o_mc.lineTo(tWidth,tHeight/4);
		o_mc.lineTo(tWidth/4*3,tHeight/2);
		
		o_mc.lineTo(tWidth,tHeight/4*3);
		o_mc.lineTo(tWidth/4*3,tHeight);
		o_mc.lineTo(tWidth/2,tHeight/4*3);
		
		o_mc.lineTo(tWidth/4,tHeight);
		o_mc.lineTo(0,tHeight/4*3);
		o_mc.lineTo(tWidth/4,tHeight/2);
		
		o_mc.lineTo(0,tHeight/4);
		o_mc.endFill();				
	}	
}